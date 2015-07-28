package cbit.vcell.mapping;

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */


import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.TokenMangler;
import org.vcell.util.VCellThreadChecker;

import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.ParameterContext.UnresolvedParameter;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.math.Action;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.JumpProcessRateDefinition;
import cbit.vcell.math.MacroscopicRateConstant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.ParticleComponentStateDefinition;
import cbit.vcell.math.ParticleComponentStatePattern;
import cbit.vcell.math.ParticleJumpProcess;
import cbit.vcell.math.ParticleMolecularComponent;
import cbit.vcell.math.ParticleMolecularComponentPattern;
import cbit.vcell.math.StochVolVariable;
import cbit.vcell.math.ParticleMolecularComponentPattern.ParticleBondType;
import cbit.vcell.math.ParticleMolecularType;
import cbit.vcell.math.ParticleMolecularTypePattern;
import cbit.vcell.math.ParticleObservable;
import cbit.vcell.math.ParticleProperties;
import cbit.vcell.math.ParticleProperties.ParticleInitialCondition;
import cbit.vcell.math.ParticleProperties.ParticleInitialConditionCount;
import cbit.vcell.math.ParticleVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableHash;
import cbit.vcell.math.VolumeParticleObservable;
import cbit.vcell.math.VolumeParticleSpeciesPattern;
import cbit.vcell.math.VolumeParticleVariable;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.RbmKineticLaw;
import cbit.vcell.model.RbmKineticLaw.RbmKineticLawParameterType;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * The MathMapping class performs the Biological to Mathematical transformation once upon calling getMathDescription().
 * This is not a "live" transformation, so that an updated SimulationContext must be given to a new MathMapping object
 * to get an updated MathDescription.
 */
public class RulebasedMathMapping extends AbstractStochMathMapping {

/**
 * This method was created in VisualAge.
 * @param model cbit.vcell.model.Model
 * @param geometry cbit.vcell.geometry.Geometry
 */
protected RulebasedMathMapping(SimulationContext simContext, MathMappingCallback callback, NetworkGenerationRequirements networkGenerationRequirements) {
	super(simContext, callback, networkGenerationRequirements);
}

	/**
	 * This method was created in VisualAge.
	 */
	private void refreshMathDescription() throws MappingException, MatrixException, MathException, ExpressionException, ModelException
	{
		//use local variable instead of using getter all the time.
		SimulationContext simContext = getSimulationContext();
		Model model = simContext.getModel();
		GeometryClass geometryClass = simContext.getGeometry().getGeometrySpec().getSubVolumes()[0];
		Domain domain = new Domain(geometryClass);

		//local structure mapping list
		StructureMapping structureMappings[] = simContext.getGeometryContext().getStructureMappings();
		//We have to check if all the reactions are able to tranform to stochastic jump processes before generating the math.
		String stochChkMsg =simContext.getModel().isValidForStochApp();
		if(!(stochChkMsg.equals("")))
		{
			throw new ModelException("Problem updating math description: "+ simContext.getName()+"\n"+stochChkMsg);
		}

		simContext.checkValidity();
		
		//
		// verify nonspatial
		//
		if (simContext.getGeometry().getDimension()>0){
			throw new MappingException("rule-based particle math mapping not implemented for spatial geometry - dimension >= 1");
		}
		
		//
		// check that we aren't solving for electric potential.
		//
		for (int i = 0; i < structureMappings.length; i++){
			if (structureMappings[i] instanceof MembraneMapping){
				if (((MembraneMapping)structureMappings[i]).getCalculateVoltage()){
					throw new MappingException("electric potential not yet supported for particle models");
				}
			}	
		}
		
		//
		// fail if any events
		//
		BioEvent[] bioEvents = simContext.getBioEvents();
		if (bioEvents!=null && bioEvents.length>0){
			throw new MappingException("events not yet supported for particle-based models");
		}
		
		//
		// verify that all structures are mapped to subvolumes and all subvolumes are mapped to a structure
		//
		Structure structures[] = simContext.getGeometryContext().getModel().getStructures();
		for (int i = 0; i < structures.length; i++){
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(structures[i]);
			if (sm==null || (sm instanceof FeatureMapping && ((FeatureMapping)sm).getGeometryClass() == null)){
				throw new MappingException("model structure '"+structures[i].getName()+"' not mapped to a geometry subVolume");
			}
			if (sm!=null && (sm instanceof MembraneMapping) && ((MembraneMapping)sm).getVolumeFractionParameter()!=null){
				Expression volFractExp = ((MembraneMapping)sm).getVolumeFractionParameter().getExpression();
				try {
					if(volFractExp != null)
					{
						double volFract = volFractExp.evaluateConstant();
						if (volFract>=1.0){
							throw new MappingException("model structure '"+(getSimulationContext().getModel().getStructureTopology().getInsideFeature(((MembraneMapping)sm).getMembrane()).getName()+"' has volume fraction >= 1.0"));
						}
					}
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
				}
			}
		}
		SubVolume subVolumes[] = simContext.getGeometryContext().getGeometry().getGeometrySpec().getSubVolumes();
		for (int i = 0; i < subVolumes.length; i++){
			Structure[] mappedStructures = simContext.getGeometryContext().getStructuresFromGeometryClass(subVolumes[i]);
			if (mappedStructures==null || mappedStructures.length==0){
				throw new MappingException("geometry subVolume '"+subVolumes[i].getName()+"' not mapped from a model structure");
			}
		}
		
		//
		// gather only those reactionSteps that are not "excluded"
		//
		ArrayList<ReactionRule> rrList = new ArrayList<ReactionRule>();
		for (ReactionRuleSpec reactionRuleSpec : simContext.getReactionContext().getReactionRuleSpecs()){
			if (!reactionRuleSpec.isExcluded()){
				rrList.add(reactionRuleSpec.getReactionRule());
			}
		}
		List<MolecularType> molecularTypeList = model.getRbmModelContainer().getMolecularTypeList();
		List<RbmObservable> observableList = model.getRbmModelContainer().getObservableList();
		
		//
		// fail if any unresolved parameters
		//
		for (ReactionRule reactionRule : rrList){
			UnresolvedParameter unresolvedParameters[] = reactionRule.getKineticLaw().getUnresolvedParameters();
			if (unresolvedParameters!=null && unresolvedParameters.length>0){
				StringBuffer buffer = new StringBuffer();
				for (int j = 0; j < unresolvedParameters.length; j++){
					if (j>0){
						buffer.append(", ");
					}
					buffer.append(unresolvedParameters[j].getName());
				}
				throw new MappingException("In Application '" + simContext.getName() + "', " + reactionRule.getDisplayType()+" '"+reactionRule.getName()+"' contains unresolved identifier(s): "+buffer);
			}
		}
	
		//
		// create new MathDescription (based on simContext's previous MathDescription if possible)
		//
		MathDescription oldMathDesc = simContext.getMathDescription();
		mathDesc = null;
		if (oldMathDesc != null){
			if (oldMathDesc.getVersion() != null){
				mathDesc = new MathDescription(oldMathDesc.getVersion());
			}else{
				mathDesc = new MathDescription(oldMathDesc.getName());
			}
		}else{
			mathDesc = new MathDescription(simContext.getName()+"_generated");
		}
	
		//
		// temporarily place all variables in a hashtable (before binding) and discarding duplicates (check for equality)
		//
		VariableHash varHash = new VariableHash();
		
		//
		// conversion factors
		//
		ModelUnitSystem modelUnitSystem = model.getUnitSystem();
		varHash.addVariable(new Constant(getMathSymbol(model.getKMOLE(), null), getIdentifierSubstitutions(model.getKMOLE().getExpression(),model.getKMOLE().getUnitDefinition(),null)));
		varHash.addVariable(new Constant(getMathSymbol(model.getN_PMOLE(), null), getIdentifierSubstitutions(model.getN_PMOLE().getExpression(),model.getN_PMOLE().getUnitDefinition(),null)));
		varHash.addVariable(new Constant(getMathSymbol(model.getPI_CONSTANT(),null), getIdentifierSubstitutions(model.getPI_CONSTANT().getExpression(),model.getPI_CONSTANT().getUnitDefinition(),null)));
		varHash.addVariable(new Constant(getMathSymbol(model.getFARADAY_CONSTANT(),null), getIdentifierSubstitutions(model.getFARADAY_CONSTANT().getExpression(),model.getFARADAY_CONSTANT().getUnitDefinition(),null)));
		varHash.addVariable(new Constant(getMathSymbol(model.getFARADAY_CONSTANT_NMOLE(),null), getIdentifierSubstitutions(model.getFARADAY_CONSTANT_NMOLE().getExpression(),model.getFARADAY_CONSTANT_NMOLE().getUnitDefinition(),null)));
		varHash.addVariable(new Constant(getMathSymbol(model.getGAS_CONSTANT(),null), getIdentifierSubstitutions(model.getGAS_CONSTANT().getExpression(),model.getGAS_CONSTANT().getUnitDefinition(),null)));
		varHash.addVariable(new Constant(getMathSymbol(model.getTEMPERATURE(),null), getIdentifierSubstitutions(new Expression(simContext.getTemperatureKelvin()), model.getTEMPERATURE().getUnitDefinition(),null)));
	
		// deals with model parameters
		ModelParameter[] modelParameters = simContext.getModel().getModelParameters();
		for (int j=0;j<modelParameters.length;j++){
			Expression expr = getSubstitutedExpr(modelParameters[j].getExpression(), true, false);
			expr = getIdentifierSubstitutions(expr,modelParameters[j].getUnitDefinition(), geometryClass);
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(modelParameters[j],geometryClass), expr,geometryClass));
		}
		
		//added July 2009, ElectricalStimulusParameter electric mapping tab
		ElectricalStimulus[] elecStimulus = simContext.getElectricalStimuli();
		if (elecStimulus.length > 0) {
			throw new MappingException("Modles with electrophysiology are not supported for stochastic applications.");			
		}
		
		//
		// add constant mem voltage
		//
		
		for (int j = 0; j < structureMappings.length; j++){
			if (structureMappings[j] instanceof MembraneMapping){
				MembraneMapping memMapping = (MembraneMapping)structureMappings[j];
				Parameter initialVoltageParm = memMapping.getInitialVoltageParameter();
				try{
					Expression exp = initialVoltageParm.getExpression();
					exp.evaluateConstant();
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(memMapping.getMembrane().getMembraneVoltage(),memMapping.getGeometryClass()),
							getIdentifierSubstitutions(memMapping.getInitialVoltageParameter().getExpression(),memMapping.getInitialVoltageParameter().getUnitDefinition(),memMapping.getGeometryClass()),memMapping.getGeometryClass()));
				}catch(ExpressionException e){
					e.printStackTrace(System.out);
					throw new MappingException("Membrane initial voltage: "+initialVoltageParm.getName()+" cannot be evaluated as constant.");
				}
			}
		}
			
		//
		// kinetic parameters (functions or constants)
		//
		for (ReactionRule reactionRule : rrList){
	//		if (getSimulationContext().getReactionContext().getReactionSpec(rs).isExcluded()){
	//			continue;
	//		}
			LocalParameter localParameters[] = reactionRule.getKineticLaw().getLocalParameters();
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(reactionRule.getStructure());
			for (LocalParameter localParameter : localParameters){
				//Reaction rate, and null parameters are not going to displayed in the particle math description.
				if ((localParameter.getRole() == RbmKineticLawParameterType.RuleRate) || (localParameter.getExpression()==null)){
					continue;
				}
				Expression expr = getSubstitutedExpr(localParameter.getExpression(), true, false);
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(localParameter,sm.getGeometryClass()), getIdentifierSubstitutions(expr,localParameter.getUnitDefinition(),sm.getGeometryClass()),sm.getGeometryClass()));
			}
		}
	
		//geometic mapping
		//the parameter "Size" is already put into mathsymbolmapping in refreshSpeciesContextMapping()
		for (int i=0;i<structureMappings.length;i++){
			StructureMapping sm = structureMappings[i];
			StructureMapping.StructureMappingParameter parm = sm.getParameterFromRole(StructureMapping.ROLE_Size);
			if(parm.getExpression() != null)
			{
				try {
					double value = parm.getExpression().evaluateConstant();
					varHash.addVariable(new Constant(getMathSymbol(parm,sm.getGeometryClass()),new Expression(value)));
				}catch (ExpressionException e){
					//varHash.addVariable(new Function(getMathSymbol0(parm,sm),getIdentifierSubstitutions(parm.getExpression(),parm.getUnitDefinition(),sm)));
					e.printStackTrace(System.out);
					throw new MappingException("Size of structure:"+sm.getNameScope().getName()+" cannot be evaluated as constant.");
				}
			}
		}

		SpeciesContextSpec speciesContextSpecs[] = simContext.getReactionContext().getSpeciesContextSpecs();
		
		addInitialConditions(domain, speciesContextSpecs, varHash);
	
		//
		// Particle Molecular Types
		//
		for (MolecularType molecularType : molecularTypeList){
			ParticleMolecularType particleMolecularType = new ParticleMolecularType(molecularType.getName());
			for (MolecularComponent molecularComponent : molecularType.getComponentList()){
				String pmcName = molecularComponent.getName();
				String pmcId = particleMolecularType.getName() + "_" + molecularComponent.getName();
				ParticleMolecularComponent particleMolecularComponent = new ParticleMolecularComponent(pmcId, pmcName);
				for (ComponentStateDefinition componentState : molecularComponent.getComponentStateDefinitions()){
					ParticleComponentStateDefinition pcsd = particleMolecularComponent.getComponentStateDefinition(componentState.getName());
					if(pcsd == null) {
						particleMolecularComponent.addComponentStateDefinition(new ParticleComponentStateDefinition(componentState.getName()));
					}
				}
				particleMolecularType.addMolecularComponent(particleMolecularComponent);
			}
			mathDesc.addParticleMolecularType(particleMolecularType);
		}
	
		//
		// Assemble list of all Species Patterns (from observables, reaction rules, and seed species).
		//
		
		LinkedHashSet<SpeciesPattern> speciesPatternSet = new LinkedHashSet<SpeciesPattern>();			// linked hash set maintains insertion order
		for (RbmObservable observable : observableList){
			speciesPatternSet.addAll(observable.getSpeciesPatternList());
		}
		for (ReactionRule reactionRule : rrList){
			for (ReactantPattern rp : reactionRule.getReactantPatterns()){
				speciesPatternSet.add(rp.getSpeciesPattern());
			}
			for (ProductPattern pp : reactionRule.getProductPatterns()){
				speciesPatternSet.add(pp.getSpeciesPattern());
			}
		}
		for (SpeciesContext sc : model.getSpeciesContexts()){
			if(!sc.hasSpeciesPattern()) { continue; }
			speciesPatternSet.add(sc.getSpeciesPattern());
		}
		
		//
		// add list of unique speciesPatterns
		//
		HashMap<String,VolumeParticleSpeciesPattern> speciesPatternVCMLMap = new HashMap<String,VolumeParticleSpeciesPattern>();
		HashMap<SpeciesPattern,VolumeParticleSpeciesPattern> speciesPatternMap = new HashMap<SpeciesPattern, VolumeParticleSpeciesPattern>();
		String speciesPatternName = "speciesPattern0";
		for (SpeciesPattern speciesPattern : speciesPatternSet) {
			VolumeParticleSpeciesPattern volumeParticleSpeciesPattern = new VolumeParticleSpeciesPattern(speciesPatternName,domain);
			
			for (MolecularTypePattern molecularTypePattern : speciesPattern.getMolecularTypePatterns()){
				ParticleMolecularType particleMolecularType = mathDesc.getParticleMolecularType(molecularTypePattern.getMolecularType().getName());
				ParticleMolecularTypePattern particleMolecularTypePattern = new ParticleMolecularTypePattern(particleMolecularType);
				
				for (MolecularComponentPattern molecularComponentPattern : molecularTypePattern.getComponentPatternList()){
					MolecularComponent molecularComponent = molecularComponentPattern.getMolecularComponent();
					ParticleMolecularComponent particleMolecularComponent = particleMolecularType.getMolecularComponent(molecularComponent.getName());
					ParticleMolecularComponentPattern particleMolecularComponentPattern = new ParticleMolecularComponentPattern(particleMolecularComponent);
					ComponentStatePattern componentState = molecularComponentPattern.getComponentStatePattern();
					if (componentState != null){
						if(componentState.isAny()) {
							ParticleComponentStatePattern pcsp = new ParticleComponentStatePattern();
							particleMolecularComponentPattern.setComponentStatePattern(pcsp);
						} else {
							String name = componentState.getComponentStateDefinition().getName();
							ParticleComponentStateDefinition pcsd = particleMolecularComponent.getComponentStateDefinition(name);
							
							//ParticleComponentStateDefinition pcsd = new ParticleComponentStateDefinition(componentState.getComponentStateDefinition().getName());
							//particleMolecularComponent.addComponentStateDefinition(pcsd);
							ParticleComponentStatePattern pcsp = new ParticleComponentStatePattern(pcsd);
							particleMolecularComponentPattern.setComponentStatePattern(pcsp);
						}
					}else{
						ParticleComponentStatePattern pcsp = new ParticleComponentStatePattern();
						particleMolecularComponentPattern.setComponentStatePattern(pcsp);
					}
					switch (molecularComponentPattern.getBondType()){
						case Specified:{
							particleMolecularComponentPattern.setBondType(ParticleBondType.Specified);
							particleMolecularComponentPattern.setBondId(molecularComponentPattern.getBondId());
							break;
						}
						case Exists:{
							particleMolecularComponentPattern.setBondType(ParticleBondType.Exists);
							particleMolecularComponentPattern.setBondId(-1);
							break;
						}
						case None:{
							particleMolecularComponentPattern.setBondType(ParticleBondType.None);
							particleMolecularComponentPattern.setBondId(-1);
							break;
						}
						case Possible:{
							particleMolecularComponentPattern.setBondType(ParticleBondType.Possible);
							particleMolecularComponentPattern.setBondId(-1);
							break;
						}
					}
					particleMolecularTypePattern.addMolecularComponentPattern(particleMolecularComponentPattern);
				}
				volumeParticleSpeciesPattern.addMolecularTypePattern(particleMolecularTypePattern);
			}
			String speciesPatternVCML = volumeParticleSpeciesPattern.getVCML("tempName");
			VolumeParticleSpeciesPattern uniqueVolumeParticleSpeciesPattern = speciesPatternVCMLMap.get(speciesPatternVCML);
			if (uniqueVolumeParticleSpeciesPattern==null){
				speciesPatternVCMLMap.put(speciesPatternVCML, volumeParticleSpeciesPattern);
				varHash.addVariable(volumeParticleSpeciesPattern);
				speciesPatternName = TokenMangler.getNextEnumeratedToken(speciesPatternName);
				speciesPatternMap.put(speciesPattern,volumeParticleSpeciesPattern);		
			}else{
				speciesPatternMap.put(speciesPattern,uniqueVolumeParticleSpeciesPattern);
			}
		}
		
		//
		// Particle Observables from Observables defined in Model
		//
		for(RbmObservable observable : observableList) {
			ParticleObservable particleObservable = new VolumeParticleObservable(getMathSymbol(observable, geometryClass),domain);
			try {
			if(observable.getType() == RbmObservable.ObservableType.Molecules) {
				particleObservable.setType(ParticleObservable.ObservableType.Molecules);
			} else {
				particleObservable.setType(ParticleObservable.ObservableType.Species);
			}
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
			for(SpeciesPattern speciesPattern : observable.getSpeciesPatternList()) {
				VolumeParticleSpeciesPattern vpsp = speciesPatternMap.get(speciesPattern);
				particleObservable.addParticleSpeciesPattern(vpsp);
			}
			varHash.addVariable(particleObservable);
		}
		//
		// Particle observables from Seed species
		//
		for (SpeciesContext sc : model.getSpeciesContexts()){
			if(!sc.hasSpeciesPattern()) { continue; }
			ParticleObservable particleObservable = new VolumeParticleObservable(getMathSymbol(sc, geometryClass),domain);
			try {
				particleObservable.setType(ParticleObservable.ObservableType.Species);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
			SpeciesPattern speciesPattern = sc.getSpeciesPattern();
			VolumeParticleSpeciesPattern vpsp = speciesPatternMap.get(speciesPattern);
			particleObservable.addParticleSpeciesPattern(vpsp);

			varHash.addVariable(particleObservable);
		}
		
		//
		// set Variables to MathDescription all at once with the order resolved by "VariableHash"
		//
		try {
			mathDesc.setAllVariables(varHash.getAlphabeticallyOrderedVariables());
		} catch (Exception e1){
			throw new MathException("Problem generating math in application "+simContext.getName()+": "+e1.getMessage(),e1);
		}
		
		
		//
		// geometry
		//
		if (simContext.getGeometryContext().getGeometry() != null){
			try {
				mathDesc.setGeometry(simContext.getGeometryContext().getGeometry());
			}catch (java.beans.PropertyVetoException e){
				e.printStackTrace(System.out);
				throw new MappingException("failure setting geometry "+e.getMessage());
			}
		}else{
			throw new MappingException("Geometry must be defined in Application "+simContext.getName());
		}
	
		//
		// create subDomain
		//
		SubVolume subVolume = simContext.getGeometry().getGeometrySpec().getSubVolumes()[0];
		SubDomain subDomain = new CompartmentSubDomain(subVolume.getName(),0);
		mathDesc.addSubDomain(subDomain);
	
	//	for (SeedSpecies seedSpecies : rbmModelContainer.getSeedSpeciesList()){
		for (SpeciesContext sc : model.getSpeciesContexts()){
			if(!sc.hasSpeciesPattern()) { 
				throw new MappingException("species "+sc.getName()+" has no molecular pattern");
			}
			VolumeParticleSpeciesPattern volumeParticleSpeciesPattern = speciesPatternMap.get(sc.getSpeciesPattern());
			ArrayList<ParticleInitialCondition> particleInitialConditions = new ArrayList<ParticleProperties.ParticleInitialCondition>();
			
			SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(sc);	// initial conditions from scs
			Parameter initialCountParameter = scs.getInitialCountParameter();
			Expression e = getIdentifierSubstitutions(new Expression(initialCountParameter,getNameScope()),initialCountParameter.getUnitDefinition(),geometryClass);
			particleInitialConditions.add(new ParticleInitialConditionCount(e,new Expression(0.0),new Expression(0.0),new Expression(0.0)));
			
			ParticleProperties particleProperies = new ParticleProperties(volumeParticleSpeciesPattern,new Expression(0.0),new Expression(0.0),new Expression(0.0),new Expression(0.0),particleInitialConditions);
			subDomain.addParticleProperties(particleProperies);
		}
		
		String reactionRuleNameDefault = "reactionRule0";	// We only use this as a safety feature, each reaction rule should have its own unique name
		String reactionRuleName = null;
		for (ReactionRule reactionRule : rrList){
			if (reactionRule.getKineticLaw().getRateLawType() != RbmKineticLaw.RateLawType.MassAction){
				throw new RuntimeException("rule-based math generation unsupported for kinetic law "+reactionRule.getKineticLaw().getRateLawType());
			}
			if((reactionRule.getName() != null) && (!reactionRule.getName().isEmpty())) {
				reactionRuleName = reactionRule.getName();
			} else {
				reactionRuleName = reactionRuleNameDefault;
			}
			LocalParameter forwardRateParameter = reactionRule.getKineticLaw().getLocalParameter(RbmKineticLaw.RbmKineticLawParameterType.MassActionForwardRate);
			Expression forwardRate = getIdentifierSubstitutions(forwardRateParameter.getExpression(), forwardRateParameter.getUnitDefinition(), geometryClass);
			ArrayList<ParticleVariable> reactantParticles = new ArrayList<ParticleVariable>();
			for (ReactantPattern reactantSpeciesPattern : reactionRule.getReactantPatterns()){
				reactantParticles.add(speciesPatternMap.get(reactantSpeciesPattern.getSpeciesPattern()));
			}
			ArrayList<ParticleVariable> productParticles = new ArrayList<ParticleVariable>();
			for (ProductPattern productSpeciesPattern : reactionRule.getProductPatterns()){
				productParticles.add(speciesPatternMap.get(productSpeciesPattern.getSpeciesPattern()));
			}
			ArrayList<Action> forwardActions = new ArrayList<Action>();
			ArrayList<Action> reverseActions = new ArrayList<Action>();
			for (ParticleVariable reactant : reactantParticles) {
				forwardActions.add(new Action(reactant,Action.ACTION_DESTROY,new Expression(1.0)));
				reverseActions.add(new Action(reactant,Action.ACTION_CREATE,new Expression(1.0)));
			}
			for (ParticleVariable product : productParticles) {
				forwardActions.add(new Action(product,Action.ACTION_CREATE,new Expression(1.0)));
				reverseActions.add(new Action(product,Action.ACTION_DESTROY,new Expression(1.0)));
			}
			
			// forward reaction
			String forwardName = reactionRuleName+"_forward";
			JumpProcessRateDefinition forwardRateDefinition = new MacroscopicRateConstant(forwardRate);
			ParticleJumpProcess forwardParticleJumpProcess = new ParticleJumpProcess(forwardName,reactantParticles,forwardRateDefinition,forwardActions);
			subDomain.addParticleJumpProcess(forwardParticleJumpProcess);
	
			// reverse reaction
			LocalParameter reverseRateParameter = reactionRule.getKineticLaw().getLocalParameter(RbmKineticLaw.RbmKineticLawParameterType.MassActionReverseRate);
			if (reactionRule.isReversible() && reverseRateParameter!=null){
				Expression reverseRate = getIdentifierSubstitutions(reverseRateParameter.getExpression(), reverseRateParameter.getUnitDefinition(), geometryClass);
				String reverseName = reactionRuleName+"_reverse";
				JumpProcessRateDefinition reverseRateDefinition = new MacroscopicRateConstant(reverseRate);
				ParticleJumpProcess reverseParticleJumpProcess = new ParticleJumpProcess(reverseName,productParticles,reverseRateDefinition,reverseActions);
				subDomain.addParticleJumpProcess(reverseParticleJumpProcess);
			}
			
			reactionRuleNameDefault = TokenMangler.getNextEnumeratedToken(reactionRuleName);
		}
		
		//
		// add any missing unit conversion factors (they don't depend on anyone else ... can do it at the end)
		//
		for (int i = 0; i < fieldMathMappingParameters.length; i++){
			if (fieldMathMappingParameters[i] instanceof UnitFactorParameter){
				Variable variable = newFunctionOrConstant(getMathSymbol(fieldMathMappingParameters[i],geometryClass),getIdentifierSubstitutions(fieldMathMappingParameters[i].getExpression(),fieldMathMappingParameters[i].getUnitDefinition(),geometryClass),fieldMathMappingParameters[i].getGeometryClass());
				if (mathDesc.getVariable(variable.getName())==null){
					mathDesc.addVariable(variable);
				}
			}
		}
	
	
		if (!mathDesc.isValid()){
			System.out.println(mathDesc.getVCML_database());
			throw new MappingException("generated an invalid mathDescription: "+mathDesc.getWarning());
		}
		
	
	System.out.println("]]]]]]]]]]]]]]]]]]]]]] VCML string begin ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
	System.out.println(mathDesc.getVCML());
	System.out.println("]]]]]]]]]]]]]]]]]]]]]] VCML string end ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
	}

/**
 * This method was created in VisualAge.
 */
private void refreshSpeciesContextMappings() throws ExpressionException, MappingException, MathException {
	
	//
	// create a SpeciesContextMapping for each speciesContextSpec.
	//
	// set initialExpression from SpeciesContextSpec.
	// set diffusing
	// set variable (only if "Constant" or "Function", else leave it as null)
	//
	
	//
	// have to put geometric paras into mathsymbolmapping, since species initial condition needs the volume size symbol.
	// and the parameters later on were added into contants or functions in refreshMathDescription()
	//
	StructureMapping structureMappings[] = getSimulationContext().getGeometryContext().getStructureMappings();
	for (int i=0;i<structureMappings.length;i++){
		StructureMapping sm = structureMappings[i];
		StructureMapping.StructureMappingParameter parm = sm.getParameterFromRole(StructureMapping.ROLE_Size);
		getMathSymbol(parm,sm.getGeometryClass());
	}
	
	speciesContextMappingList.removeAllElements();
	
	SpeciesContextSpec speciesContextSpecs[] = getSimulationContext().getReactionContext().getSpeciesContextSpecs();
	for (int i=0;i<speciesContextSpecs.length;i++){
		SpeciesContextSpec scs = speciesContextSpecs[i];

		SpeciesContextMapping scm = new SpeciesContextMapping(scs.getSpeciesContext());
		scm.setPDERequired(false);
		scm.setHasEventAssignment(false);	
		
		//
		// no support for hybrid reactions in RuleBased applications (for now).
		//
		scm.setHasHybridReaction(false);
//		for (ReactionRuleSpec reactionRuleSpec : getSimulationContext().getReactionContext().getReactionRuleSpecs()){
//			if (!reactionRuleSpec.isExcluded() && reactionRuleSpec.hasHybrid(getSimulationContext(), scs.getSpeciesContext())){
//				scm.setHasHybridReaction(true);
//			}
//		}
			
		scm.setDependencyExpression(null);
		scm.setFastParticipant(false);

		speciesContextMappingList.addElement(scm);
	}
}


/**
 * This method was created in VisualAge.
 * @Override
 */
private void refreshVariables() throws MappingException {

	Domain defaultDomain = new Domain(this.getSimulationContext().getGeometry().getGeometryClasses()[0]);

	//
	// non-constant independent variables require either a membrane or volume variable
	//
	Enumeration<SpeciesContextMapping> enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		SpeciesContextSpec scs = getSimulationContext().getReactionContext().getSpeciesContextSpec(scm.getSpeciesContext());
		//stochastic variable is always a function of size.
		SpeciesCountParameter spCountParm = null;
		try{
			String countName = scs.getSpeciesContext().getName() + BIO_PARAM_SUFFIX_SPECIES_COUNT;
			Expression countExp = new Expression(0.0);
			spCountParm = addSpeciesCountParameter(countName, countExp, PARAMETER_ROLE_COUNT, scs.getInitialCountParameter().getUnitDefinition(), scs);
		}catch(PropertyVetoException pve){
			pve.printStackTrace();
			throw new MappingException(pve.getMessage());
		}
		
		//add concentration of species as MathMappingParameter - this will map to species concentration function
		try{
			String concName = scs.getSpeciesContext().getName() + BIO_PARAM_SUFFIX_SPECIES_CONCENTRATION;
			Expression concExp = getExpressionAmtToConc(new Expression(spCountParm.getName()), scs.getSpeciesContext());
			concExp.bindExpression(this);
			addSpeciesConcentrationParameter(concName, concExp, PARAMETER_ROLE_CONCENRATION, scs.getSpeciesContext().getUnitDefinition(), scs);
		}catch(Exception e){
			e.printStackTrace();
			throw new MappingException(e.getMessage());
		}
		//we always add variables, all species are independent variables, no matter they are constant or not.
		String countMathSymbol = getMathSymbol(spCountParm, getSimulationContext().getGeometryContext().getStructureMapping(scs.getSpeciesContext().getStructure()).getGeometryClass());
		scm.setVariable(new VolumeParticleVariable(countMathSymbol,defaultDomain));
		mathSymbolMapping.put(scm.getSpeciesContext(),scm.getVariable().getName());
	}
}


@Override
protected void refresh(MathMappingCallback callback) throws MappingException, ExpressionException, MatrixException, MathException, ModelException {
	VCellThreadChecker.checkCpuIntensiveInvocation();
	
	localIssueList.clear();
	//refreshKFluxParameters();
	refreshSpeciesContextMappings();
	//refreshStructureAnalyzers();
	if(callback != null) {
		callback.setProgressFraction(52.0f/100.0f);
	}
	refreshVariables();
	refreshLocalNameCount();
	refreshMathDescription();
	reconcileWithOriginalModel();
}

}

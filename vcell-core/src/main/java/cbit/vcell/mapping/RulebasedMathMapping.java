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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Pair;
import org.vcell.util.TokenMangler;

import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.ParameterContext.UnresolvedParameter;
import cbit.vcell.mapping.RulebasedTransformer.Operation;
import cbit.vcell.mapping.RulebasedTransformer.ReactionRuleAnalysisReport;
import cbit.vcell.mapping.RulebasedTransformer.RulebasedTransformation;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.math.Action;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.JumpProcessRateDefinition;
import cbit.vcell.math.MacroscopicRateConstant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.ParticleComponentStateDefinition;
import cbit.vcell.math.ParticleComponentStatePattern;
import cbit.vcell.math.ParticleJumpProcess;
import cbit.vcell.math.ParticleJumpProcess.ProcessSymmetryFactor;
import cbit.vcell.math.ParticleMolecularComponent;
import cbit.vcell.math.ParticleMolecularComponentPattern;
import cbit.vcell.math.ParticleMolecularComponentPattern.ParticleBondType;
import cbit.vcell.math.ParticleMolecularType;
import cbit.vcell.math.ParticleMolecularTypePattern;
import cbit.vcell.math.ParticleObservable;
import cbit.vcell.math.ParticleObservable.Sequence;
import cbit.vcell.math.ParticleProperties;
import cbit.vcell.math.ParticleProperties.ParticleInitialCondition;
import cbit.vcell.math.ParticleProperties.ParticleInitialConditionCount;
import cbit.vcell.math.ParticleVariable;
import cbit.vcell.math.StochVolVariable;
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
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.RationalExpUtils;
import cbit.vcell.units.VCUnitDefinition;
import jscl.text.ParseException;
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
	@Override
	protected void refreshMathDescription() throws MappingException, MatrixException, MathException, ExpressionException, ModelException
	{
		//use local variable instead of using getter all the time.
		SimulationContext simContext = getSimulationContext();
		GeometryClass geometryClass = simContext.getGeometry().getGeometrySpec().getSubVolumes()[0];
		Domain domain = new Domain(geometryClass);

		//local structure mapping list
		StructureMapping structureMappings[] = simContext.getGeometryContext().getStructureMappings();
		//We have to check if all the reactions are able to transform to stochastic jump processes before generating the math.
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
		// gather only those reactionRules that are not "excluded"
		//
		ArrayList<ReactionRule> rrList = new ArrayList<ReactionRule>();
		for (ReactionRuleSpec reactionRuleSpec : simContext.getReactionContext().getReactionRuleSpecs()){
			if (!reactionRuleSpec.isExcluded()){
				rrList.add(reactionRuleSpec.getReactionRule());
			}
		}
		
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
		// temporarily place all variables in a hashtable (before binding) and discarding duplicates
		//
		VariableHash varHash = new VariableHash();
		
		//
		// conversion factors
		//
		Model model = simContext.getModel();
		varHash.addVariable(new Constant(getMathSymbol(model.getKMOLE(), null), getIdentifierSubstitutions(model.getKMOLE().getExpression(),model.getKMOLE().getUnitDefinition(),null)));
		varHash.addVariable(new Constant(getMathSymbol(model.getN_PMOLE(), null), getIdentifierSubstitutions(model.getN_PMOLE().getExpression(),model.getN_PMOLE().getUnitDefinition(),null)));
		varHash.addVariable(new Constant(getMathSymbol(model.getPI_CONSTANT(),null), getIdentifierSubstitutions(model.getPI_CONSTANT().getExpression(),model.getPI_CONSTANT().getUnitDefinition(),null)));
		varHash.addVariable(new Constant(getMathSymbol(model.getFARADAY_CONSTANT(),null), getIdentifierSubstitutions(model.getFARADAY_CONSTANT().getExpression(),model.getFARADAY_CONSTANT().getUnitDefinition(),null)));
		varHash.addVariable(new Constant(getMathSymbol(model.getFARADAY_CONSTANT_NMOLE(),null), getIdentifierSubstitutions(model.getFARADAY_CONSTANT_NMOLE().getExpression(),model.getFARADAY_CONSTANT_NMOLE().getUnitDefinition(),null)));
		varHash.addVariable(new Constant(getMathSymbol(model.getGAS_CONSTANT(),null), getIdentifierSubstitutions(model.getGAS_CONSTANT().getExpression(),model.getGAS_CONSTANT().getUnitDefinition(),null)));
		varHash.addVariable(new Constant(getMathSymbol(model.getTEMPERATURE(),null), getIdentifierSubstitutions(new Expression(simContext.getTemperatureKelvin()), model.getTEMPERATURE().getUnitDefinition(),null)));
		
		Enumeration<SpeciesContextMapping> enum1 = getSpeciesContextMappings();
		while (enum1.hasMoreElements()){
			SpeciesContextMapping scm = enum1.nextElement();
			if (scm.getVariable() instanceof StochVolVariable){
				varHash.addVariable(scm.getVariable());
			}
		}

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
//			if (reactionRule.getKineticLaw() instanceof LumpedKinetics){
//				throw new RuntimeException("Lumped Kinetics not yet supported for RuleBased Modeling");
//			}
			LocalParameter parameters[] = reactionRule.getKineticLaw().getLocalParameters();
			for (LocalParameter parameter : parameters){
				//
				// skip current density if not used.
				//
//				if ((parameter.getRole() == RbmKineticLawParameterType.ROLE_CurrentDensity) &&
//					(parameter.getExpression()==null || parameter.getExpression().isZero())){
//					continue;
//				}
				//
				// don't add rate, we'll do it later when creating the jump processes
				//
				if ((parameter.getRole() == RbmKineticLawParameterType.RuleRate)){
					continue;
				}
				
				//
				// don't add mass action reverse parameter if irreversible
				//
				if (!reactionRule.isReversible() && parameter.getRole() == RbmKineticLawParameterType.MassActionReverseRate){
					continue;
				}

				Expression expr = getSubstitutedExpr(parameter.getExpression(), true, false);
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(parameter,geometryClass), getIdentifierSubstitutions(expr,parameter.getUnitDefinition(),geometryClass),geometryClass));
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

		SpeciesContextSpec speciesContextSpecs[] = getSimulationContext().getReactionContext().getSpeciesContextSpecs();

		addInitialConditions(domain, speciesContextSpecs, varHash);
		
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
		// create subDomains
		//
		SubVolume subVolume = simContext.getGeometry().getGeometrySpec().getSubVolumes()[0];
		SubDomain subDomain = new CompartmentSubDomain(subVolume.getName(),0);
		mathDesc.addSubDomain(subDomain);

		//
		// define all molecules and unique species patterns (add molecules to mathDesc and speciesPatterns to varHash).
		//
		HashMap<SpeciesPattern, VolumeParticleSpeciesPattern> speciesPatternMap = addSpeciesPatterns(domain, rrList);
		HashSet<VolumeParticleSpeciesPattern> uniqueParticleSpeciesPatterns = new HashSet<>(speciesPatternMap.values());
		for (VolumeParticleSpeciesPattern volumeParticleSpeciesPattern : uniqueParticleSpeciesPatterns){
			varHash.addVariable(volumeParticleSpeciesPattern);
		}

		//
		// define observables (those explicitly declared and those corresponding to seed species.
		//
		List<ParticleObservable> observables = addObservables(geometryClass, domain, speciesPatternMap);
		for (ParticleObservable particleObservable : observables){
			varHash.addVariable(particleObservable);
		}

		try {
			addParticleJumpProcesses(varHash, geometryClass, subDomain, speciesPatternMap);
		} catch (PropertyVetoException e1) {
			e1.printStackTrace();
			throw new MappingException(e1.getMessage(),e1);
		}	

		//
		// include required UnitRateFactors
		//
		for (int i = 0; i < fieldMathMappingParameters.length; i++){
			if (fieldMathMappingParameters[i] instanceof UnitFactorParameter || fieldMathMappingParameters[i] instanceof ObservableConcentrationParameter){
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(fieldMathMappingParameters[i],geometryClass),getIdentifierSubstitutions(fieldMathMappingParameters[i].getExpression(),fieldMathMappingParameters[i].getUnitDefinition(),geometryClass),fieldMathMappingParameters[i].getGeometryClass()));
			}
		}

		//
		// set Variables to MathDescription all at once with the order resolved by "VariableHash"
		//
		mathDesc.setAllVariables(varHash.getAlphabeticallyOrderedVariables());
		
		//
		// set up particle initial conditions in subdomain
		//
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
			if (fieldMathMappingParameters[i] instanceof ObservableConcentrationParameter){
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
	}

	private void addParticleJumpProcesses(VariableHash varHash, GeometryClass geometryClass, SubDomain subDomain, HashMap<SpeciesPattern, VolumeParticleSpeciesPattern> speciesPatternMap) throws ExpressionException, MappingException, MathException, PropertyVetoException {

		ArrayList<ReactionRule> rrList = new ArrayList<>();
		for (ReactionRuleSpec rrSpec : getSimulationContext().getReactionContext().getReactionRuleSpecs()){
			if (!rrSpec.isExcluded()){
				rrList.add(rrSpec.getReactionRule());
			}
		}

		for (ReactionRule reactionRule : rrList){
			
			String jpName = TokenMangler.mangleToSName(reactionRule.getName());
			
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
			
			RbmKineticLaw kinetics = reactionRule.getKineticLaw();
			
			// check the reaction rate law to see if we need to decompose a reaction(reversible) into two jump processes.
			// rate constants are important in calculating the probability rate.
			// for Mass Action, we use KForward and KReverse, 
			// for General Kinetics we parse reaction rate J to see if it is in Mass Action form.

			if (kinetics.getRateLawType() == RbmKineticLaw.RateLawType.MassAction){
				boolean constantMassActionKineticCoefficients = true;
				
				StringBuffer errorMessage = new StringBuffer();
				Parameter forward_rateParameter = kinetics.getLocalParameter(RbmKineticLawParameterType.MassActionForwardRate);
				Expression substitutedForwardRate = MathUtilities.substituteModelParameters(forward_rateParameter.getExpression(), reactionRule.getNameScope().getScopedSymbolTable());
				if (!substitutedForwardRate.flatten().isNumeric()){
					errorMessage.append("flattened Kf for reactionRule("+reactionRule.getName()+") is not numeric, exp = '"+substitutedForwardRate.flatten().infix()+"'");
					constantMassActionKineticCoefficients = false;
				}
				if (reactionRule.isReversible()){
					Parameter reverse_rateParameter = kinetics.getLocalParameter(RbmKineticLawParameterType.MassActionReverseRate);
					if (reverse_rateParameter==null || reverse_rateParameter.getExpression()==null){
						throw new MappingException("reverse rate constant for reaction rule "+reactionRule.getName()+" is missing");
					}
					Expression substitutedReverseRate = MathUtilities.substituteModelParameters(reverse_rateParameter.getExpression(), reactionRule.getNameScope().getScopedSymbolTable());
					if (!substitutedReverseRate.flatten().isNumeric()){
						errorMessage.append("flattened Kr for reactionRule("+reactionRule.getName()+") is not numeric, exp = '"+substitutedReverseRate.flatten().infix()+"'");
						constantMassActionKineticCoefficients = false;
					}
				}
				
				if (constantMassActionKineticCoefficients){
					addStrictMassActionParticleJumpProcess(varHash, geometryClass, subDomain,
							reactionRule, jpName,
							reactantParticles, productParticles, 
							forwardActions, reverseActions);
				}else{
					throw new MappingException("not mass action: "+errorMessage.toString());
//					addGeneralParticleJumpProcess(varHash, geometryClass, subDomain,
//							reactionRule, jpName,
//							reactantParticles, productParticles, 
//							forwardActions, reverseActions);
				}
				
			}else{
				throw new MappingException("rule-based math generation unsupported for Kinetic Law: "+kinetics.getRateLawType());
			}			
			
		} // end reactionRules
	}

//	private void addGeneralParticleJumpProcess_NOT_USED(VariableHash varHash, GeometryClass geometryClass, SubDomain subDomain,
//															ReactionRule reactionRule, String jpName,
//															ArrayList<ParticleVariable> reactantParticles, ArrayList<ParticleVariable> productParticles,
//															ArrayList<Action> forwardActions, ArrayList<Action> reverseActions)
//					throws ExpressionException, ExpressionBindingException, PropertyVetoException, MathException, MappingException {
//		
//		//
//		// don't forget to add rule analysis operations here.
//		//
//		String reactionRuleName = reactionRule.getName();
//		RbmKineticLaw kinetics = reactionRule.getKineticLaw();
//
//		if (kinetics.getRateLawType() != RbmKineticLaw.RateLawType.MassAction){
//			throw new RuntimeException("expecting mass action kinetics for reaction rule "+reactionRuleName);
//		}
//		
//		//
//		// construct stochastic forward or reverse rate expression (separately).  Transform from 
//		//        original expression of "concentrationRate" in terms of rateParameter and reactants/products in concentrations 
//		//    to  
//		//        new stochastic expression of "molecularRate" in terms of forwardRateParameter, reactants/products in molecules, structure sizes, and unit conversions.
//		//
//		//  (1)  concentrationRate = K * [s0] * [s1]    [uM.s-1]  or   [molecules.um-3.s-1]   or   [molecules.um-2.s-1]  (or other)
//		//  (2)  molecularRate = P * <s0> * <s1>        [molecules.s-1]
//		//
//		//  in this math description, we are using <s_i> [molecules], but original kinetics were in [s_i] [uM or molecules.um-2].
//		//  so through a change in variable to get things in terms of <s_i>.  <<<< Here P is the desired stochastic rate coefficient. >>>
//		//
//		//  (3)  let [s_i] = <s_i>/structsize(s_i)*unitConversionFactor(substanceunit([s_i])/substanceunit(<s_i>))
//		//
//		//  in addition to the change in variables, we need to transform the entire expression from concentration/time to molecules/time
//		//
//		//  (4)  let molecularRate = concentrationRate * structSize(reaction) * unitConversionFactor(substanceunit(molecularRate)/substanceunit(concentrationRate))
//		//
//		//	(5)  in general, concentationRate = K * PRODUCT([s_i])
//		//
//		//  change of variables into stochastic variables used in MathDescription, substituting (3) into (5)
//		//
//		//  (6)  concentrationRate = K * PRODUCT(<s_i>/structsize(s_i)*unitConversionFactor(substanceunit([s_i])/substanceunit(<s_i>)))
//		//
//		//  reordering to separate the sizes, the unit conversions and the <s_i>
//		//
//		//  (7)  concentrationRate = K * PRODUCT(<s_i>) * PRODUCT(1/structsize(s_i)) * unitConversionFactor(PRODUCT(substanceunit([s_i])/substanceunit(<s_i>)))
//		//
//		//  combining (4) and (7)
//		//
//		//  (8) molecularRate = K * PRODUCT(<s_i>) * PRODUCT(1/structsize(s_i)) * unitConversionFactor(PRODUCT(substanceunit([s_i])/substanceunit(<s_i>))) * structSize(reaction) * unitConversionFactor(substanceunit(molecularRate)/substanceunit(concentrationRate))
//		//
//		//  collecting terms of sizes and unit conversions
//		//
//		//  (9)  molecularRate = K * PRODUCT(<s_i>) * structSize(reaction) / PRODUCT(structsize(s_i)) * unitConversionFactor(substanceunit(molecularRate)/substanceunit(concentrationRate) * PRODUCT(substanceunit([s_i])/substanceunit(<s_i>)))
//		//
//		//  (10) molecularRate = K * PRODUCT(<s_i>) * sizeFactor * unitConversionFactor(substanceConversionUnit)
//		//
//		//  where
//		//
//		//  (11) sizeFactor = structSize(reaction) / PRODUCT(structsize(s_i))
//		//  (12) substanceConversionUnit = substanceunit(molecularRate)/substanceunit(concentrationRate) * PRODUCT(substanceunit([s_i])/substanceunit(<s_i>))
//		//
//		//  The ParticleJumpCondition wants a single new rate stochastic, P from equation (2).  Note that PRODUCT(<s_i>) will be captured separately the the reactantPatterns.
//		//  comparing (2) and (10) we have found P.
//		//
//		//  (13) P = K * sizeFactor * unitConversionFactor(substanceConversionUnit)
//		//
//		//  the framework also needs the proper unit for P
//		//
//		//  (14) Unit(P) = Unit(K) * Unit(sizeFactor) * substanceConversionUnit
//		//
//		//
//		
//		ModelUnitSystem modelUnitSystem = getSimulationContext().getModel().getUnitSystem();
//		VCUnitDefinition stochasticSubstanceUnit = modelUnitSystem.getStochasticSubstanceUnit();
//		VCUnitDefinition reactionRuleSubstanceUnit = modelUnitSystem.getSubstanceUnit(reactionRule.getStructure());
//
//		{
//		//
//		// get forward rate parameter and make sure it is constant valued.
//		//
//		Parameter forward_rateParameter = kinetics.getLocalParameter(RbmKineticLawParameterType.MassActionForwardRate);
//		{
//		Expression substitutedForwardRate = MathUtilities.substituteModelParameters(forward_rateParameter.getExpression(), reactionRule.getNameScope().getScopedSymbolTable());
//		if (!substitutedForwardRate.flatten().isNumeric()){
////			throw new MappingException("forward rate constant for reaction rule "+reactionRule.getName()+" is not constant");
//		}
//		}
//		
//		// 
//		// create forward sizeExp and forward unitFactor
//		//
//		VCUnitDefinition forward_substanceConversionUnit = stochasticSubstanceUnit.divideBy(reactionRuleSubstanceUnit);
//		VCUnitDefinition forward_sizeFactorUnit = reactionRule.getStructure().getStructureSize().getUnitDefinition();
//		Expression forward_sizeFactor = new Expression(reactionRule.getStructure().getStructureSize(),getNameScope());
//		for (ReactantPattern reactantPattern : reactionRule.getReactantPatterns()){
//			Expression reactantSizeExp = new Expression(reactantPattern.getStructure().getStructureSize(),getNameScope());
//			VCUnitDefinition reactantSizeUnit = reactantPattern.getStructure().getStructureSize().getUnitDefinition();
//			VCUnitDefinition reactantSubstanceUnit = modelUnitSystem.getSubstanceUnit(reactantPattern.getStructure());
//			forward_sizeFactor = Expression.div(forward_sizeFactor,reactantSizeExp);
//			forward_sizeFactorUnit = forward_sizeFactorUnit.divideBy(reactantSizeUnit);
//			forward_substanceConversionUnit = forward_substanceConversionUnit.multiplyBy(reactantSubstanceUnit).divideBy(stochasticSubstanceUnit);
//		}
//		// simplify sizeFactor (often has size/size/size)
//		try {
//			forward_sizeFactor = RationalExpUtils.getRationalExp(forward_sizeFactor).simplifyAsExpression();
//			forward_sizeFactor.bindExpression(getSimulationContext().getModel());
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		
//		Expression forward_rateExp = Expression.mult(new Expression(forward_rateParameter, getNameScope()),forward_sizeFactor,getUnitFactor(forward_substanceConversionUnit)).flatten();
//		VCUnitDefinition forward_rateUnit = forward_rateParameter.getUnitDefinition().multiplyBy(forward_sizeFactorUnit).multiplyBy(forward_substanceConversionUnit);
//		
//		ProbabilityParameter forward_probParm = addProbabilityParameter(PARAMETER_PROBABILITYRATE_PREFIX+jpName, forward_rateExp, PARAMETER_ROLE_P, forward_rateUnit,reactionRule);
//		
//		//add probability to function or constant
//		varHash.addVariable(newFunctionOrConstant(getMathSymbol(forward_probParm,geometryClass),getIdentifierSubstitutions(forward_rateExp, forward_rateUnit, geometryClass),geometryClass));
//		
//		// add forward ParticleJumpProcess
//		String forward_name = reactionRuleName;
//		Expression forward_rate = getIdentifierSubstitutions(new Expression(forward_probParm,getNameScope()), forward_probParm.getUnitDefinition(), geometryClass);
//		JumpProcessRateDefinition forward_rateDefinition = new MacroscopicRateConstant(forward_rate);
//		ParticleJumpProcess forward_particleJumpProcess = new ParticleJumpProcess(forward_name,reactantParticles,forward_rateDefinition,forwardActions);
//		subDomain.addParticleJumpProcess(forward_particleJumpProcess);
//		}
//		
//		//
//		// get reverse rate parameter and make sure it is missing or constant valued.
//		//
//		if (reactionRule.isReversible()){
//			Parameter reverse_rateParameter = kinetics.getLocalParameter(RbmKineticLawParameterType.MassActionReverseRate);
//			if (reverse_rateParameter==null || reverse_rateParameter.getExpression()==null){
//				throw new MappingException("reverse rate constant for reaction rule "+reactionRule.getName()+" is missing");
//			}
//			{
//			Expression substitutedReverseRate = MathUtilities.substituteModelParameters(reverse_rateParameter.getExpression(), reactionRule.getNameScope().getScopedSymbolTable());
//			if (!substitutedReverseRate.flatten().isNumeric()){
//				throw new MappingException("reverse rate constant for reaction rule "+reactionRule.getName()+" is not constant");
//			}
//			}
//			
//			// 
//			// create reverse sizeExp and reverse unitFactor
//			//
//			VCUnitDefinition reverse_substanceConversionUnit = stochasticSubstanceUnit.divideBy(reactionRuleSubstanceUnit);
//			VCUnitDefinition reverse_sizeFactorUnit = reactionRule.getStructure().getStructureSize().getUnitDefinition();
//			Expression reverse_sizeFactor = new Expression(reactionRule.getStructure().getStructureSize(),getNameScope());
//			for (ProductPattern productPattern : reactionRule.getProductPatterns()){
//				Expression reactantSizeExp = new Expression(productPattern.getStructure().getStructureSize(),getNameScope());
//				VCUnitDefinition reactantSizeUnit = productPattern.getStructure().getStructureSize().getUnitDefinition();
//				VCUnitDefinition reactantSubstanceUnit = modelUnitSystem.getSubstanceUnit(productPattern.getStructure());
//				reverse_sizeFactor = Expression.div(reverse_sizeFactor,reactantSizeExp);
//				reverse_sizeFactorUnit = reverse_sizeFactorUnit.divideBy(reactantSizeUnit);
//				reverse_substanceConversionUnit = reverse_substanceConversionUnit.multiplyBy(reactantSubstanceUnit).divideBy(stochasticSubstanceUnit);
//			}
//			// simplify sizeFactor (often has size/size/size)
//			try {
//				reverse_sizeFactor = RationalExpUtils.getRationalExp(reverse_sizeFactor).simplifyAsExpression();
//				reverse_sizeFactor.bindExpression(getSimulationContext().getModel());
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			
//			Expression reverse_rateExp = Expression.mult(new Expression(reverse_rateParameter, getNameScope()),reverse_sizeFactor,getUnitFactor(reverse_substanceConversionUnit)).flatten();
//			VCUnitDefinition reverse_rateUnit = reverse_rateParameter.getUnitDefinition().multiplyBy(reverse_sizeFactorUnit).multiplyBy(reverse_substanceConversionUnit);
//			
//			// if the reaction has forward rate (Mass action,HMMs), or don't have either forward or reverse rate (some other rate laws--like general)
//			// we process it as forward reaction
//			// get jump process name
//			
//			ProbabilityParameter reverse_probParm = addProbabilityParameter(PARAMETER_PROBABILITYRATE_PREFIX+jpName+"_reverse", reverse_rateExp, PARAMETER_ROLE_P_reverse, reverse_rateUnit,reactionRule);
//			
//			//add probability to function or constant
//			varHash.addVariable(newFunctionOrConstant(getMathSymbol(reverse_probParm,geometryClass),getIdentifierSubstitutions(reverse_rateExp, reverse_rateUnit, geometryClass),geometryClass));
//											
//			// add reverse ParticleJumpProcess
//			Expression reverse_rate = getIdentifierSubstitutions(new Expression(reverse_probParm,getNameScope()), reverse_probParm.getUnitDefinition(), geometryClass);
//			String reverse_name = reactionRuleName+"_reverse";
//			JumpProcessRateDefinition reverse_rateDefinition = new MacroscopicRateConstant(reverse_rate);
//			ParticleJumpProcess reverse_particleJumpProcess = new ParticleJumpProcess(reverse_name,productParticles,reverse_rateDefinition,reverseActions);
//			subDomain.addParticleJumpProcess(reverse_particleJumpProcess);
//			
//		}
//	}
	
	private Map<ParticleJumpProcess, ReactionRuleAnalysisReport> jumpProcessMap = new  LinkedHashMap<ParticleJumpProcess, ReactionRuleAnalysisReport>();
	private void addStrictMassActionParticleJumpProcess(VariableHash varHash, GeometryClass geometryClass, SubDomain subDomain,
														ReactionRule reactionRule, String jpName,
														ArrayList<ParticleVariable> reactantParticles, ArrayList<ParticleVariable> productParticles,
														ArrayList<Action> forwardActions, ArrayList<Action> reverseActions)
					throws ExpressionException, ExpressionBindingException, PropertyVetoException, MathException, MappingException {
		
		String reactionRuleName = reactionRule.getName();
		RbmKineticLaw kinetics = reactionRule.getKineticLaw();
		RulebasedTransformation ruleBasedTransformation = ((RulebasedTransformation)getTransformation());

		if (kinetics.getRateLawType() != RbmKineticLaw.RateLawType.MassAction){
			throw new RuntimeException("expecting mass action kinetics for reaction rule "+reactionRuleName);
		}
		
		//
		// construct stochastic forward or reverse rate expression (separately).  Transform from 
		//        original expression of "concentrationRate" in terms of rateParameter and reactants/products in concentrations 
		//    to  
		//        new stochastic expression of "molecularRate" in terms of forwardRateParameter, reactants/products in molecules, structure sizes, and unit conversions.
		//
		//  (1)  concentrationRate = K * [s0] * [s1]    [uM.s-1]  or   [molecules.um-3.s-1]   or   [molecules.um-2.s-1]  (or other)
		//  (2)  molecularRate = P * <s0> * <s1>        [molecules.s-1]
		//
		//  in this math description, we are using <s_i> [molecules], but original kinetics were in [s_i] [uM or molecules.um-2].
		//  so through a change in variable to get things in terms of <s_i>.  <<<< Here P is the desired stochastic rate coefficient. >>>
		//
		//  (3)  let [s_i] = <s_i>/structsize(s_i)*unitConversionFactor(substanceunit([s_i])/substanceunit(<s_i>))
		//
		//  in addition to the change in variables, we need to transform the entire expression from concentration/time to molecules/time
		//
		//  (4)  let molecularRate = concentrationRate * structSize(reaction) * unitConversionFactor(substanceunit(molecularRate)/substanceunit(concentrationRate))
		//
		//	(5)  in general, concentationRate = K * PRODUCT([s_i])
		//
		//  change of variables into stochastic variables used in MathDescription, substituting (3) into (5)
		//
		//  (6)  concentrationRate = K * PRODUCT(<s_i>/structsize(s_i)*unitConversionFactor(substanceunit([s_i])/substanceunit(<s_i>)))
		//
		//  reordering to separate the sizes, the unit conversions and the <s_i>
		//
		//  (7)  concentrationRate = K * PRODUCT(<s_i>) * PRODUCT(1/structsize(s_i)) * unitConversionFactor(PRODUCT(substanceunit([s_i])/substanceunit(<s_i>)))
		//
		//  combining (4) and (7)
		//
		//  (8) molecularRate = K * PRODUCT(<s_i>) * PRODUCT(1/structsize(s_i)) * unitConversionFactor(PRODUCT(substanceunit([s_i])/substanceunit(<s_i>))) * structSize(reaction) * unitConversionFactor(substanceunit(molecularRate)/substanceunit(concentrationRate))
		//
		//  collecting terms of sizes and unit conversions
		//
		//  (9)  molecularRate = K * PRODUCT(<s_i>) * structSize(reaction) / PRODUCT(structsize(s_i)) * unitConversionFactor(substanceunit(molecularRate)/substanceunit(concentrationRate) * PRODUCT(substanceunit([s_i])/substanceunit(<s_i>)))
		//
		//  (10) molecularRate = K * PRODUCT(<s_i>) * sizeFactor * unitConversionFactor(substanceConversionUnit)
		//
		//  where
		//
		//  (11) sizeFactor = structSize(reaction) / PRODUCT(structsize(s_i))
		//  (12) substanceConversionUnit = substanceunit(molecularRate)/substanceunit(concentrationRate) * PRODUCT(substanceunit([s_i])/substanceunit(<s_i>))
		//
		//  The ParticleJumpCondition wants a single new rate stochastic, P from equation (2).  Note that PRODUCT(<s_i>) will be captured separately the the reactantPatterns.
		//  comparing (2) and (10) we have found P.
		//
		//  (13) P = K * sizeFactor * unitConversionFactor(substanceConversionUnit)
		//
		//  the framework also needs the proper unit for P
		//
		//  (14) Unit(P) = Unit(K) * Unit(sizeFactor) * substanceConversionUnit
		//
		//
		
		ModelUnitSystem modelUnitSystem = getSimulationContext().getModel().getUnitSystem();
		VCUnitDefinition stochasticSubstanceUnit = modelUnitSystem.getStochasticSubstanceUnit();
		VCUnitDefinition reactionRuleSubstanceUnit = modelUnitSystem.getSubstanceUnit(reactionRule.getStructure());
		int forwardRuleIndex = 0;
		//
		// get forward rate parameter and make sure it is constant valued.
		//
		Parameter forward_rateParameter = kinetics.getLocalParameter(RbmKineticLawParameterType.MassActionForwardRate);
		Expression substitutedForwardRate = MathUtilities.substituteModelParameters(forward_rateParameter.getExpression(), reactionRule.getNameScope().getScopedSymbolTable());
		if (!substitutedForwardRate.flatten().isNumeric()){
			throw new MappingException("forward rate constant for reaction rule "+reactionRule.getName()+" is not constant");
		}
		// 
		// create forward sizeExp and forward unitFactor
		//
		VCUnitDefinition forward_substanceConversionUnit = stochasticSubstanceUnit.divideBy(reactionRuleSubstanceUnit);
		VCUnitDefinition forward_sizeFactorUnit = reactionRule.getStructure().getStructureSize().getUnitDefinition();
		Expression forward_sizeFactor = new Expression(reactionRule.getStructure().getStructureSize(),getNameScope());
		for (ReactantPattern reactantPattern : reactionRule.getReactantPatterns()){
			Expression reactantSizeExp = new Expression(reactantPattern.getStructure().getStructureSize(),getNameScope());
			VCUnitDefinition reactantSizeUnit = reactantPattern.getStructure().getStructureSize().getUnitDefinition();
			VCUnitDefinition reactantSubstanceUnit = modelUnitSystem.getSubstanceUnit(reactantPattern.getStructure());
			forward_sizeFactor = Expression.div(forward_sizeFactor,reactantSizeExp);
			forward_sizeFactorUnit = forward_sizeFactorUnit.divideBy(reactantSizeUnit);
			forward_substanceConversionUnit = forward_substanceConversionUnit.multiplyBy(reactantSubstanceUnit).divideBy(stochasticSubstanceUnit);
		}
		// simplify sizeFactor (often has size/size/size)
		try {
			forward_sizeFactor = RationalExpUtils.getRationalExp(forward_sizeFactor).simplifyAsExpression();
			forward_sizeFactor.bindExpression(getSimulationContext().getModel());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Expression forward_rateExp = Expression.mult(getUnitFactor(forward_substanceConversionUnit), new Expression(forward_rateParameter, getNameScope()),forward_sizeFactor).flattenFactors("KMOLE");
		VCUnitDefinition forward_rateUnit = forward_rateParameter.getUnitDefinition().multiplyBy(forward_sizeFactorUnit).multiplyBy(forward_substanceConversionUnit);
		
		ProbabilityParameter forward_probParm = addProbabilityParameter(PARAMETER_PROBABILITYRATE_PREFIX+jpName, forward_rateExp, PARAMETER_ROLE_P, forward_rateUnit,reactionRule);
		//add probability to function or constant
		varHash.addVariable(newFunctionOrConstant(getMathSymbol(forward_probParm,geometryClass),getIdentifierSubstitutions(forward_rateExp, forward_rateUnit, geometryClass),geometryClass));
		
		// add forward ParticleJumpProcess
		String forward_name = reactionRuleName;
		Expression forward_rate = getIdentifierSubstitutions(new Expression(forward_probParm,getNameScope()), forward_probParm.getUnitDefinition(), geometryClass);
		JumpProcessRateDefinition forward_rateDefinition = new MacroscopicRateConstant(forward_rate);

		ReactionRuleAnalysisReport rrarBiomodelForward = ruleBasedTransformation.getRulesForwardMap().get(reactionRule);
		ProcessSymmetryFactor forwardSymmetryFactor = new ProcessSymmetryFactor(rrarBiomodelForward.getSymmetryFactor());
		ParticleJumpProcess forward_particleJumpProcess = new ParticleJumpProcess(forward_name,reactantParticles,forward_rateDefinition,forwardActions,forwardSymmetryFactor);
		subDomain.addParticleJumpProcess(forward_particleJumpProcess);
		
		//
		// verify that map and operations are consistent between BNG generated NFSimXML and RuleAnalysis algorithm.
		//
		for (ReactionRule rr : getSimulationContext().getModel().getRbmModelContainer().getReactionRuleList()){
			if (rr == reactionRule){
				break;
			}
			forwardRuleIndex++;
			if (rr.isReversible()){
				forwardRuleIndex++;
			}
		}
//        System.out.println("\n\n--------- new rule analysis report for ReactionRule \""+reactionRule.getName()+"\" (#"+(forwardRuleIndex+RuleAnalysis.INDEX_OFFSET)+") -----------\n");
//        ReactionRuleDirection forward = ReactionRuleDirection.forward;
//		System.out.println(RbmUtils.toBnglStringShort(reactionRule, CompartmentMode.show)+", direction="+forward);
//
//        MathRuleFactory mathRuleFactory = new MathRuleFactory();
//        MathRuleEntry mathRule = mathRuleFactory.createRuleEntry(forward_particleJumpProcess, forwardRuleIndex);
//        ((RulebasedTransformation)getTransformation()).compareOutputs(mathRule);	// compare the xml from BioNetGen with the one we build
//        
//        RuleAnalysisReport mathReport = RuleAnalysis.analyze(mathRule, true);
//        Set<String> ours = mathReport.getSummaryAsSet();
//        Set<String> theirs = getSummaryAsSet(rrarBiomodelForward);
//        if (compareSets(ours, theirs) == true){
//             System.out.println("Rule Analysis SAME\n");
//        }else{
//             System.out.println("Rule Analysis DIFFERENT\n");
//        }
		
		//
		// get reverse rate parameter and make sure it is missing or constant valued.
		//
		if (reactionRule.isReversible()){
			Parameter reverse_rateParameter = kinetics.getLocalParameter(RbmKineticLawParameterType.MassActionReverseRate);
			if (reverse_rateParameter==null || reverse_rateParameter.getExpression()==null){
				throw new MappingException("reverse rate constant for reaction rule "+reactionRule.getName()+" is missing");
			}
			{
			Expression substitutedReverseRate = MathUtilities.substituteModelParameters(reverse_rateParameter.getExpression(), reactionRule.getNameScope().getScopedSymbolTable());
			if (!substitutedReverseRate.flatten().isNumeric()){
				throw new MappingException("reverse rate constant for reaction rule "+reactionRule.getName()+" is not constant");
			}
			}
			
			// 
			// create reverse sizeExp and reverse unitFactor
			//
			VCUnitDefinition reverse_substanceConversionUnit = stochasticSubstanceUnit.divideBy(reactionRuleSubstanceUnit);
			VCUnitDefinition reverse_sizeFactorUnit = reactionRule.getStructure().getStructureSize().getUnitDefinition();
			Expression reverse_sizeFactor = new Expression(reactionRule.getStructure().getStructureSize(),getNameScope());
			for (ProductPattern productPattern : reactionRule.getProductPatterns()){
				Expression reactantSizeExp = new Expression(productPattern.getStructure().getStructureSize(),getNameScope());
				VCUnitDefinition reactantSizeUnit = productPattern.getStructure().getStructureSize().getUnitDefinition();
				VCUnitDefinition reactantSubstanceUnit = modelUnitSystem.getSubstanceUnit(productPattern.getStructure());
				reverse_sizeFactor = Expression.div(reverse_sizeFactor,reactantSizeExp);
				reverse_sizeFactorUnit = reverse_sizeFactorUnit.divideBy(reactantSizeUnit);
				reverse_substanceConversionUnit = reverse_substanceConversionUnit.multiplyBy(reactantSubstanceUnit).divideBy(stochasticSubstanceUnit);
			}
			// simplify sizeFactor (often has size/size/size)
			try {
				reverse_sizeFactor = RationalExpUtils.getRationalExp(reverse_sizeFactor).simplifyAsExpression();
				reverse_sizeFactor.bindExpression(getSimulationContext().getModel());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			Expression reverse_rateExp = Expression.mult(new Expression(reverse_rateParameter, getNameScope()),reverse_sizeFactor,getUnitFactor(reverse_substanceConversionUnit)).flattenFactors("KMOLE");
			VCUnitDefinition reverse_rateUnit = reverse_rateParameter.getUnitDefinition().multiplyBy(reverse_sizeFactorUnit).multiplyBy(reverse_substanceConversionUnit);
			
			// if the reaction has forward rate (Mass action,HMMs), or don't have either forward or reverse rate (some other rate laws--like general)
			// we process it as forward reaction
			// get jump process name
			
			ProbabilityParameter reverse_probParm = addProbabilityParameter(PARAMETER_PROBABILITYRATE_PREFIX+jpName+"_reverse", reverse_rateExp, PARAMETER_ROLE_P_reverse, reverse_rateUnit,reactionRule);
			
			//add probability to function or constant
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(reverse_probParm,geometryClass),getIdentifierSubstitutions(reverse_rateExp, reverse_rateUnit, geometryClass),geometryClass));
											
			// add reverse ParticleJumpProcess
			Expression reverse_rate = getIdentifierSubstitutions(new Expression(reverse_probParm,getNameScope()), reverse_probParm.getUnitDefinition(), geometryClass);
			String reverse_name = reactionRuleName+"_reverse";
			JumpProcessRateDefinition reverse_rateDefinition = new MacroscopicRateConstant(reverse_rate);
			ReactionRuleAnalysisReport rrarBiomodelReverse = ruleBasedTransformation.getRulesReverseMap().get(reactionRule);
			ProcessSymmetryFactor reverseSymmetryFactor = new ProcessSymmetryFactor(rrarBiomodelReverse.getSymmetryFactor());
			ParticleJumpProcess reverse_particleJumpProcess = new ParticleJumpProcess(reverse_name,productParticles,reverse_rateDefinition,reverseActions,reverseSymmetryFactor);
			subDomain.addParticleJumpProcess(reverse_particleJumpProcess);
			
			//
			// check reverse direction mapping and operations with RuleAnalysis.
			//
			int reverseRuleIndex = forwardRuleIndex + 1;
			ReactionRuleAnalysisReport rrar = ruleBasedTransformation.getRulesReverseMap().get(reactionRule);
			jumpProcessMap.put(reverse_particleJumpProcess, rrar);

			
		}
	}

	private static boolean compareSets(Set<String> ours, Set<String> theirs) {
		for(String our : ours) {
			if(!theirs.contains(our)) {
				return false;
			}
		}
		for(String their : theirs) {
			if(!ours.contains(their)) {
				return false;
			}
		}
		return true;
	}
	private static Set<String> getSummaryAsSet(ReactionRuleAnalysisReport rrar) {
		Set<String> summary = new HashSet<String>();
        for (Pair<String, String> mapping : rrar.getIdMappingList()){
        	String str1 = mapping.one;
        	String str2 = mapping.two;
        	if(str1 == null) {
        		summary.add("map "+str2+"\n");
        	} else {
        		summary.add("map "+str2+" to "+str1+"\n");
        	}
        }
        for (Operation op : rrar.getOperationsList()) {
        	summary.add("operation " + op.toString() + "\n");
        }
		return summary;
	}

	private List<ParticleObservable> addObservables(
			GeometryClass geometryClass,
			Domain domain,
			HashMap<SpeciesPattern, VolumeParticleSpeciesPattern> speciesPatternMap)
			throws MappingException, MathException {
		
		ArrayList<ParticleObservable> observables = new ArrayList<>();
		//
		// Particle Observables from Observables defined in Model
		//
		for(MathMappingParameter mathMappingParameter : getMathMappingParameters()) {
			if (mathMappingParameter instanceof ObservableCountParameter){
				ObservableCountParameter observableCountParameter = (ObservableCountParameter)mathMappingParameter;
				RbmObservable rbmObservable = observableCountParameter.getObservable();
				ParticleObservable.ObservableType particleObservableType = null;
				if (rbmObservable.getType() == RbmObservable.ObservableType.Molecules){
					particleObservableType = ParticleObservable.ObservableType.Molecules; 
				}else{
					particleObservableType = ParticleObservable.ObservableType.Species; 
				}
				ParticleObservable particleObservable = new VolumeParticleObservable(getMathSymbol(observableCountParameter, geometryClass),domain,particleObservableType);

				switch (rbmObservable.getSequence()){
					case Multimolecular:{
						particleObservable.setSequence(Sequence.Multimolecular);
						break;
					}
					case PolymerLengthEqual:{
						particleObservable.setSequence(Sequence.PolymerLengthEqual);
						particleObservable.setQuantity(rbmObservable.getSequenceLength());
						break;
					}
					case PolymerLengthGreater:{
						particleObservable.setSequence(Sequence.PolymerLengthGreater);
						particleObservable.setQuantity(rbmObservable.getSequenceLength());
						break;
					}
					default:{
						throw new RuntimeException("unexpected sequence "+rbmObservable.getSequence());
					}
				}

				for(SpeciesPattern speciesPattern : rbmObservable.getSpeciesPatternList()) {
					VolumeParticleSpeciesPattern vpsp = speciesPatternMap.get(speciesPattern);
					particleObservable.addParticleSpeciesPattern(vpsp);
				}
				observables.add(particleObservable);
			}
			if (mathMappingParameter instanceof SpeciesCountParameter){
				SpeciesCountParameter speciesCountParameter = (SpeciesCountParameter)mathMappingParameter;
				ParticleObservable.ObservableType particleObservableType = ParticleObservable.ObservableType.Species;
				ParticleObservable particleObservable = new VolumeParticleObservable(getMathSymbol(speciesCountParameter, geometryClass),domain,particleObservableType);
				particleObservable.setSequence(Sequence.Multimolecular);
				SpeciesPattern speciesPattern = speciesCountParameter.getSpeciesContext().getSpeciesPattern();
				VolumeParticleSpeciesPattern vpsp = speciesPatternMap.get(speciesPattern);
				particleObservable.addParticleSpeciesPattern(vpsp);
				observables.add(particleObservable);
			}
		}
		return observables;
	}

	private HashMap<SpeciesPattern, VolumeParticleSpeciesPattern> addSpeciesPatterns(Domain domain, List<ReactionRule> rrList) throws MathException {
		// Particle Molecular Types
		//
		Model model = getSimulationContext().getModel();
		List<RbmObservable> observableList = model.getRbmModelContainer().getObservableList();
		List<MolecularType> molecularTypeList = model.getRbmModelContainer().getMolecularTypeList();
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
			if(!molecularType.isAnchorAll()) {
				List<String> anchorList = new ArrayList<>();
				for(Structure struct : molecularType.getAnchors()) {
					anchorList.add(struct.getName());
				}
				particleMolecularType.setAnchorList(anchorList);
			}
			mathDesc.addParticleMolecularType(particleMolecularType);
		}
	
		//
		// Assemble list of all Species Patterns (from observables, reaction rules, and seed species).
		//
		
		LinkedHashMap<SpeciesPattern, Structure> speciesPatternStructureMap = new LinkedHashMap<SpeciesPattern, Structure>();			// linked hash set maintains insertion order
		for (RbmObservable observable : observableList){
			for (SpeciesPattern speciesPattern : observable.getSpeciesPatternList()){
				speciesPatternStructureMap.put(speciesPattern, observable.getStructure());
			}
		}
		for (ReactionRule reactionRule : rrList){
			for (ReactantPattern rp : reactionRule.getReactantPatterns()){
				speciesPatternStructureMap.put(rp.getSpeciesPattern(), rp.getStructure());
			}
			for (ProductPattern pp : reactionRule.getProductPatterns()){
				speciesPatternStructureMap.put(pp.getSpeciesPattern(), pp.getStructure());
			}
		}
		for (SpeciesContext sc : model.getSpeciesContexts()){
			if(!sc.hasSpeciesPattern()) { continue; }
			speciesPatternStructureMap.put(sc.getSpeciesPattern(), sc.getStructure());
		}
		
		//
		// add list of unique speciesPatterns
		//
		HashMap<String,VolumeParticleSpeciesPattern> speciesPatternVCMLMap = new HashMap<String,VolumeParticleSpeciesPattern>();
		HashMap<SpeciesPattern,VolumeParticleSpeciesPattern> speciesPatternMap = new HashMap<SpeciesPattern, VolumeParticleSpeciesPattern>();
		String speciesPatternName = "speciesPattern0";
		for (SpeciesPattern speciesPattern : speciesPatternStructureMap.keySet()) {
			VolumeParticleSpeciesPattern volumeParticleSpeciesPattern = new VolumeParticleSpeciesPattern(speciesPatternName,domain,speciesPatternStructureMap.get(speciesPattern).getName());
			
			for (MolecularTypePattern molecularTypePattern : speciesPattern.getMolecularTypePatterns()){
				ParticleMolecularType particleMolecularType = mathDesc.getParticleMolecularType(molecularTypePattern.getMolecularType().getName());
				ParticleMolecularTypePattern particleMolecularTypePattern = new ParticleMolecularTypePattern(particleMolecularType);
				String participantMatchLabel = molecularTypePattern.getParticipantMatchLabel();
				if (molecularTypePattern.getParticipantMatchLabel()!=null){
					particleMolecularTypePattern.setMatchLabel(participantMatchLabel);
				}
				
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
				speciesPatternName = TokenMangler.getNextEnumeratedToken(speciesPatternName);
				speciesPatternMap.put(speciesPattern,volumeParticleSpeciesPattern);		
			}else{
				speciesPatternMap.put(speciesPattern,uniqueVolumeParticleSpeciesPattern);
			}
		}
		return speciesPatternMap;
	}

@Override
protected void refreshSpeciesContextMappings() throws ExpressionException, MappingException, MathException 
{
	//
	// create a SpeciesContextMapping for each speciesContextSpec.
	//
	// set initialExpression from SpeciesContextSpec.
	// set diffusing
	// set variable (only if "Constant" or "Function", else leave it as null)-----why commented?
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

	getSpeciesContextMappingList().removeAllElements();
	
	SpeciesContextSpec speciesContextSpecs[] = getSimulationContext().getReactionContext().getSpeciesContextSpecs();
	for (int i=0;i<speciesContextSpecs.length;i++){
		SpeciesContextSpec scs = speciesContextSpecs[i];

		SpeciesContextMapping scm = new SpeciesContextMapping(scs.getSpeciesContext());
		scm.setPDERequired(false);
		scm.setHasEventAssignment(false);
		scm.setHasHybridReaction(false);
//		for (ReactionRuleSpec reactionRuleSpec : getSimulationContext().getReactionContext().getReactionRuleSpecs()){
//			if (!reactionRuleSpec.isExcluded() && reactionRuleSpec.hasHybrid(getSimulationContext(), scs.getSpeciesContext())){
//				scm.setHasHybridReaction(true);
//			}
//		}
		// we don't eliminate variables for stochastic
		scm.setDependencyExpression(null);
		// We don't participant in fast reaction step for stochastic
		scm.setFastParticipant(false);
		
		getSpeciesContextMappingList().addElement(scm);
	}
}


/**
 * This method was created in VisualAge.
 * @Override
 */
@Override
protected void refreshVariables() throws MappingException {

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
			spCountParm = addSpeciesCountParameter(countName, countExp, PARAMETER_ROLE_SPECIES_COUNT, scs.getInitialCountParameter().getUnitDefinition(), scs.getSpeciesContext());
		}catch(PropertyVetoException pve){
			pve.printStackTrace();
			throw new MappingException(pve.getMessage());
		}
		
		//add concentration of species as MathMappingParameter - this will map to species concentration function
		try{
			String concName = scs.getSpeciesContext().getName() + BIO_PARAM_SUFFIX_SPECIES_CONCENTRATION;
			Expression concExp = getExpressionAmtToConc(new Expression(spCountParm,getNameScope()), scs.getSpeciesContext().getStructure());
			concExp.bindExpression(this);
			addSpeciesConcentrationParameter(concName, concExp, PARAMETER_ROLE_SPECIES_CONCENRATION, scs.getSpeciesContext().getUnitDefinition(), scs.getSpeciesContext());
		}catch(Exception e){
			e.printStackTrace();
			throw new MappingException(e.getMessage());
		}
		//we always add variables, all species are independent variables, no matter they are constant or not.
		String countMathSymbol = getMathSymbol(spCountParm, getSimulationContext().getGeometryContext().getStructureMapping(scs.getSpeciesContext().getStructure()).getGeometryClass());
		scm.setVariable(new VolumeParticleVariable(countMathSymbol,defaultDomain));
		mathSymbolMapping.put(scm.getSpeciesContext(),scm.getVariable().getName());
	}
	
	for (RbmObservable observable : simContext.getModel().getRbmModelContainer().getObservableList()){
		//stochastic variable is always a function of size.
		ObservableCountParameter observableCountParm = null;
		try{
			String countName = observable.getName() + BIO_PARAM_SUFFIX_SPECIES_COUNT;
			Expression countExp = new Expression(0.0);
			observableCountParm = addObservableCountParameter(countName, countExp, PARAMETER_ROLE_OBSERVABLE_COUNT, simContext.getModel().getUnitSystem().getStochasticSubstanceUnit(), observable);
		}catch(PropertyVetoException pve){
			pve.printStackTrace();
			throw new MappingException(pve.getMessage());
		}
		
		//add concentration of species as MathMappingParameter - this will map to species concentration function
		try{
			String concName = observable.getName() + BIO_PARAM_SUFFIX_SPECIES_CONCENTRATION;
			Expression concExp = getExpressionAmtToConc(new Expression(observableCountParm,getNameScope()), observable.getStructure());
			concExp.bindExpression(this);
			addObservableConcentrationParameter(concName, concExp, PARAMETER_ROLE_OBSERVABLE_CONCENTRATION, observable.getUnitDefinition(), observable);
		}catch(Exception e){
			e.printStackTrace();
			throw new MappingException(e.getMessage());
		}
//		//we always add variables, all species are independent variables, no matter they are constant or not.
//		String countMathSymbol = getMathSymbol(observableCountParm, getSimulationContext().getGeometryContext().getStructureMapping(scs.getSpeciesContext().getStructure()).getGeometryClass());
//		scm.setVariable(new VolumeParticleVariable(countMathSymbol,defaultDomain));
//		mathSymbolMapping.put(observable,scm.getVariable().getName());
	}
}


}

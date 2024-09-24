/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;

import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.mapping.SimContextTransformer.ModelEntityMapping;
import cbit.vcell.mapping.SimContextTransformer.SimContextTransformation;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.stoch.GeneralKineticsStochasticFunction;
import cbit.vcell.mapping.stoch.MassActionStochasticFunction;
import cbit.vcell.mapping.stoch.StochasticFunction;
import cbit.vcell.mapping.stoch.StochasticTransformer;
import cbit.vcell.math.*;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.matrix.RationalExp;
import cbit.vcell.model.*;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.RationalExpUtils;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.TokenMangler;

import java.beans.PropertyVetoException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
/**
 * The StochMathMapping class performs the Biological to Mathematical transformation once upon calling getMathDescription()
 * for stochastic simulation. To get math description for deterministic simulation please reference @MathMapping.
 * ApplicationEditor.updatMath() decides to use either StochMathMapping or MathMapping.
 * Created Sept. 18, 2006
 * @version 1.0 Beta
 * @author Tracy LI
 */
public class StochMathMapping extends AbstractStochMathMapping {
	private static Logger lg = LogManager.getLogger(StochMathMapping.class);

	protected StochMathMapping(SimulationContext simContext, MathMappingCallback callback,	NetworkGenerationRequirements networkGenerationRequirements) {
		super(simContext, callback, networkGenerationRequirements);
	}

	private Expression getProbabilityRate(ReactionStep reactionStep, MassActionStochasticFunction massActionStochasticFunction, boolean isForwardDirection)
			throws MappingException, ExpressionException
	{
		Model model = getSimulationContext().getModel();

		// translate entire reaction rate to probability rate
		final Expression rateFactor = getProbabilityRateFactor(reactionStep, model);

		Expression productOfSpeciesCounts = null;
		Expression productOfSpeciesFactors = null;
		List<ReactionParticipant> reacPartList = (isForwardDirection) ? massActionStochasticFunction.reactants() : massActionStochasticFunction.products();
		for (ReactionParticipant reactPart : reacPartList) {
			// Instead of 's^stoich', we'll choose molecules from s without replacement 's*(s-1)(s-2)..(s-stoi+1)'
			// e.g. for stoichiometry 2, we have s*(s-1)
			SpeciesCountParameter spCountParam = getSpeciesCountParameter(reactPart.getSpeciesContext());
			Expression speciesCount = new Expression(spCountParam, getNameScope());
			Expression speciesCountTerm = new Expression(speciesCount);//species from uM to No. of Particles, form s*(s-1)*(s-2)
			int stoichiometry = reactPart.getStoichiometry();
			for(int j = 1; j < stoichiometry; j++) {
				speciesCountTerm = Expression.mult(speciesCountTerm, Expression.add(speciesCount, new Expression(-j)));
			}

			// accumulate products of species counts (keep these like terms together)
			if (productOfSpeciesCounts == null) {
				productOfSpeciesCounts = new Expression(speciesCountTerm);
			} else {//for more than one reactant
				productOfSpeciesCounts = Expression.mult(productOfSpeciesCounts, speciesCountTerm);
			}

			// accumulate products of species factors (translates product of counts to product of concentrations)
			// and keep these terms together for later simplification and cancellation with rate factor
			// so, a(a-1)b * speciesFactor is equivalent to [a]^2[b].
			Expression speciesStructureSize = new Expression(reactPart.getStructure().getStructureSize(), getNameScope());
			VCUnitDefinition speciesSubstanceUnit = model.getUnitSystem().getSubstanceUnit(reactPart.getStructure());
			VCUnitDefinition stochasticSubstanceUnit = model.getUnitSystem().getStochasticSubstanceUnit();
			Expression speciesUnitFactor = getUnitFactor(speciesSubstanceUnit.divideBy(stochasticSubstanceUnit));
			Expression speciesFactor = Expression.div(speciesUnitFactor, speciesStructureSize).simplifyJSCL();
			if(stoichiometry == 1) {
				if (productOfSpeciesFactors == null) {
					productOfSpeciesFactors = new Expression(speciesFactor);
				} else {
					productOfSpeciesFactors = Expression.mult(productOfSpeciesFactors, speciesFactor);
				}
			} else if (stoichiometry > 1) {
				if (productOfSpeciesFactors == null) {
					productOfSpeciesFactors = Expression.power(speciesFactor, new Expression(stoichiometry));
				} else {
					productOfSpeciesFactors = Expression.mult(productOfSpeciesFactors, Expression.power(speciesFactor, new Expression(stoichiometry)));
				}
			} else {
				throw new ExpressionException("Stoichiometry must be greater than 0");
			}
		}

		// Now construct the probability rate expression: rateCoefficient * speciesCounts * speciesFactors * rateFactor.
		final Expression probExp;
		if (productOfSpeciesCounts == null) {
			// zero order reaction (not really mass action) .. but still must multiply coefficient by rate factor to get probability rate units.
			Expression massActionRateCoefficient = isForwardDirection ? massActionStochasticFunction.forwardRate() : massActionStochasticFunction.reverseRate();
			probExp = Expression.mult(massActionRateCoefficient, rateFactor);
		} else {
			// 1st or higher order mass action reaction, with coefficient, species counts and conversion factors yield probability rate

			// merge the species factors and the rate factor and try to cancel terms
			RationalExp factorsRatExp = RationalExpUtils.getRationalExp(Expression.mult(productOfSpeciesFactors, rateFactor));
			Expression factorsExp = new Expression(factorsRatExp.infixString());
			factorsExp.bindExpression(this);
			factorsExp = factorsExp.flattenSafe();

			// proper mass action, multiply the rate coefficient with the probability expression
			Expression massActionRateCoefficient = isForwardDirection ? massActionStochasticFunction.forwardRate() : massActionStochasticFunction.reverseRate();
			probExp = Expression.mult(massActionRateCoefficient, productOfSpeciesCounts, factorsExp);
		}
		return probExp.flattenSafe();
	}

	private Expression getProbabilityRate(ReactionStep reactionStep, GeneralKineticsStochasticFunction generalKineticsStochasticFunction, boolean isForwardDirection)
			throws ExpressionException
	{
		Model model = getSimulationContext().getModel();

		// translate entire reaction rate to probability rate
		final Expression rateFactor = getProbabilityRateFactor(reactionStep, model);
		Expression netRateExpr = isForwardDirection ? generalKineticsStochasticFunction.forwardNetRate() : generalKineticsStochasticFunction.reverseNetRate();

		// collect symbolTableEntries for speciesContexts within netRateExpr and replace with concentration parameter
		netRateExpr = new Expression(netRateExpr);
		for (String symbol : netRateExpr.getSymbols()) {
			SymbolTableEntry symbolTableEntry = netRateExpr.getSymbolBinding(symbol);
			if (symbolTableEntry instanceof Kinetics.KineticsProxyParameter proxyParameter
					&& proxyParameter.getTarget() instanceof SpeciesContext sc) {
				SpeciesConcentrationParameter concParam = getSpeciesConcentrationParameter(sc);
				Expression concentrationExpr = new Expression(concParam, getNameScope());
				netRateExpr.substituteInPlace(new Expression(symbol), concentrationExpr);
			}
		}

		//simplify the factor
		RationalExp factorRatExp = RationalExpUtils.getRationalExp(rateFactor);
		Expression simplifiedFactorExp = new Expression(factorRatExp.infixString());
		simplifiedFactorExp.bindExpression(this);
		//get probability rate with converting factor
		Expression probExp = Expression.mult(netRateExpr, simplifiedFactorExp);
		probExp = probExp.flattenSafe();
		return probExp;
	}

	private Expression getProbabilityRateFactor(ReactionStep reactionStep, Model model) throws ExpressionException {
		final Expression rateFactor;
		{
			VCUnitDefinition stochasticSubstanceUnit = model.getUnitSystem().getStochasticSubstanceUnit();
			if (reactionStep.getKinetics() instanceof DistributedKinetics) {
				final VCUnitDefinition reactionSubstanceUnit;
				if (reactionStep instanceof SimpleReaction) {
					reactionSubstanceUnit = model.getUnitSystem().getSubstanceUnit(reactionStep.getStructure());
				} else if (reactionStep instanceof FluxReaction) {
					reactionSubstanceUnit = model.getUnitSystem().getVolumeSubstanceUnit();
				} else {
					throw new IllegalArgumentException("Unknown reaction step type: " + reactionStep.getClass().getName());
				}
				final Expression reactionSubstanceUnitFactor = getUnitFactor(stochasticSubstanceUnit.divideBy(reactionSubstanceUnit));
				StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(reactionStep.getStructure());
				Expression reactionStructureSize = new Expression(sm.getStructure().getStructureSize(), getNameScope());
				rateFactor = Expression.mult(reactionSubstanceUnitFactor, reactionStructureSize).simplifyJSCL();
			} else if (reactionStep.getKinetics() instanceof LumpedKinetics) {
				rateFactor = new Expression(1.0);
			} else {
				throw new ExpressionException("Unknown kinetics type: " + reactionStep.getKinetics().getClass().getName());
			}
		}
		return rateFactor;
	}

	/**
	 * set up a math description based on current simulationContext.
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

		simContext.checkValidity();
		
		//
		// verify nonspatial
		//
		if (simContext.getGeometry().getDimension()>0){
			throw new MappingException("nonspatial stochastic math mapping requires 0-dimensional geometry");
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
					lg.error(e);
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
		ReactionSpec reactionSpecs[] = simContext.getReactionContext().getReactionSpecs();
		Vector<ReactionStep> rsList = new Vector<ReactionStep>();
		for (int i = 0; i < reactionSpecs.length; i++){
			if (!reactionSpecs[i].isExcluded()){
				rsList.add(reactionSpecs[i].getReactionStep());
			}
		}
		
		//
		// fail if any unresolved parameters
		//
		for (ReactionStep reactionStep : rsList){
			Kinetics.UnresolvedParameter unresolvedParameters[] = reactionStep.getKinetics().getUnresolvedParameters();
			if (unresolvedParameters!=null && unresolvedParameters.length>0){
				StringBuffer buffer = new StringBuffer();
				for (int j = 0; j < unresolvedParameters.length; j++){
					if (j>0){
						buffer.append(", ");
					}
					buffer.append(unresolvedParameters[j].getName());
				}
				throw new MappingException("In Application '" + simContext.getName() + "', " + reactionStep.getDisplayType()+" '"+reactionStep.getName()+"' contains unresolved identifier(s): "+buffer);
			}
		}
			
		//
		// create new MathDescription (based on simContext's previous MathDescription if possible)
		//
		MathDescription oldMathDesc = simContext.getMathDescription();
		mathDesc = null;
		if (oldMathDesc != null){
			if (oldMathDesc.getVersion() != null){
				mathDesc = new MathDescription(oldMathDesc.getVersion(), mathSymbolMapping);
			}else{
				mathDesc = new MathDescription(oldMathDesc.getName(), mathSymbolMapping);
			}
		}else{
			mathDesc = new MathDescription(simContext.getName()+"_generated", mathSymbolMapping);
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
				StructureMapping sm = simContext.getGeometryContext().getStructureMapping(scm.getSpeciesContext().getStructure());
				scm.getVariable().setDomain(new Domain(sm.getGeometryClass()));
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
					lg.error(e);
					throw new MappingException("Membrane initial voltage: "+initialVoltageParm.getName()+" cannot be evaluated as constant.");
				}
			}
		}
		
		//
		// kinetic parameters (functions or constants)
		//
		for (ReactionStep rs : rsList){
			Kinetics.KineticsParameter[] parameters = rs.getKinetics().getKineticsParameters();
			for (KineticsParameter parameter : parameters){
				//
				// skip current density if not used.
				//
				if ((parameter.getRole() == Kinetics.ROLE_CurrentDensity) &&
					(parameter.getExpression()==null || parameter.getExpression().isZero())){
					continue;
				}
				//
				// don't add rate, we'll do it later when creating the jump processes
				//
//				if (parameter.getRole() == Kinetics.ROLE_ReactionRate) {
//					continue;
//				}
				
				//
				// don't add mass action reverse parameter if irreversible
				//
//				if (!rs.isReversible() && parameters[i].getRole() == Kinetics.ROLE_KReverse){
//					continue;
//				}

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
					lg.error(e);
					throw new MappingException("Size of structure:"+sm.getNameScope().getName()+" cannot be evaluated as constant.");
				}
			}
		}

		SpeciesContextSpec speciesContextSpecs[] = getSimulationContext().getReactionContext().getSpeciesContextSpecs();

		addInitialConditions(domain, speciesContextSpecs, varHash);
		
		//
		// constant species (either function or constant)
		//
		enum1 = getSpeciesContextMappings();
		while (enum1.hasMoreElements()){
			SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
			if (scm.getVariable() instanceof Constant){
				varHash.addVariable(scm.getVariable());
			}
		}

		//
		// geometry
		//
		if (simContext.getGeometryContext().getGeometry() != null){
			try {
				mathDesc.setGeometry(simContext.getGeometryContext().getGeometry());
			}catch (java.beans.PropertyVetoException e){
				lg.error(e);
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
		// functions: species which is not a variable, but has dependency expression
		//
		enum1 = getSpeciesContextMappings();
		while (enum1.hasMoreElements()){
			SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
			if (scm.getVariable()==null && scm.getDependencyExpression()!=null){
				StructureMapping sm = simContext.getGeometryContext().getStructureMapping(scm.getSpeciesContext().getStructure());
				Expression exp = scm.getDependencyExpression();
				exp.bindExpression(this);
				SpeciesCountParameter spCountParam = getSpeciesCountParameter(scm.getSpeciesContext());
				varHash.addVariable(new Function(getMathSymbol(spCountParam,sm.getGeometryClass()),getIdentifierSubstitutions(exp, spCountParam.getUnitDefinition(), sm.getGeometryClass()),domain));
			}
		}

		addJumpProcesses(varHash, geometryClass, subDomain);

		//
		// include required UnitRateFactors
		//
		for (int i = 0; i < fieldMathMappingParameters.length; i++){
			if (fieldMathMappingParameters[i] instanceof UnitFactorParameter){
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(fieldMathMappingParameters[i],geometryClass),getIdentifierSubstitutions(fieldMathMappingParameters[i].getExpression(),fieldMathMappingParameters[i].getUnitDefinition(),geometryClass),fieldMathMappingParameters[i].getGeometryClass()));
			}
		}

		//
		// set up variable initial conditions in subDomain
		//
		SpeciesContextSpec scSpecs[] = simContext.getReactionContext().getSpeciesContextSpecs();
		for (int i = 0; i < speciesContextSpecs.length; i++){
			//get stochastic variable by name
			SpeciesCountParameter spCountParam = getSpeciesCountParameter(speciesContextSpecs[i].getSpeciesContext());
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(speciesContextSpecs[i].getSpeciesContext().getStructure());
			String varName = getMathSymbol(spCountParam, sm.getGeometryClass()); 

			StochVolVariable var = (StochVolVariable)varHash.getVariable(varName);
			SpeciesContextSpec.SpeciesContextSpecParameter initParm = scSpecs[i].getInitialCountParameter();//stochastic use initial number of particles
			//stochastic variables initial expression.
			if (initParm!=null)
			{
				VarIniCondition varIni = null;
				if(!scSpecs[i].isConstant() && getSimulationContext().isRandomizeInitCondition())
				{
					varIni = new VarIniPoissonExpectedCount(var,new Expression(getMathSymbol(initParm, sm.getGeometryClass())));
				}
				else 
				{
					varIni = new VarIniCount(var,new Expression(getMathSymbol(initParm, sm.getGeometryClass())));
				}
				
				subDomain.addVarIniCondition(varIni);
			}
		}

		//
		// add any missing unit conversion factors (they don't depend on anyone else ... can do it at the end)
		//
		for (int i = 0; i < fieldMathMappingParameters.length; i++){
			if (fieldMathMappingParameters[i] instanceof UnitFactorParameter){
				Variable variable = newFunctionOrConstant(getMathSymbol(fieldMathMappingParameters[i],geometryClass),getIdentifierSubstitutions(fieldMathMappingParameters[i].getExpression(),fieldMathMappingParameters[i].getUnitDefinition(),geometryClass),fieldMathMappingParameters[i].getGeometryClass());
				if (varHash.getVariable(variable.getName())==null){
					varHash.addVariable(variable);
				}
			}
			if (fieldMathMappingParameters[i] instanceof ObservableCountParameter){
				Variable variable = newFunctionOrConstant(getMathSymbol(fieldMathMappingParameters[i],geometryClass),getIdentifierSubstitutions(fieldMathMappingParameters[i].getExpression(),fieldMathMappingParameters[i].getUnitDefinition(),geometryClass),fieldMathMappingParameters[i].getGeometryClass());
				if (varHash.getVariable(variable.getName())==null){
					varHash.addVariable(variable);
				}
			}
		}

		//
		// set Variables to MathDescription all at once with the order resolved by "VariableHash"
		//
		mathDesc.setAllVariables(varHash.getAlphabeticallyOrderedVariables());

		mathDesc.refreshDependencies();

		if (!mathDesc.isValid()){
			if (lg.isTraceEnabled()) {
				lg.trace(mathDesc.getVCML_database());
			}
			throw new MappingException("generated an invalid mathDescription: "+mathDesc.getWarning());
		}
	}

	private void addJumpProcesses(VariableHash varHash, GeometryClass geometryClass, SubDomain subDomain) throws ExpressionException, ModelException, MappingException, MathException {
		// set up jump processes
		// get all the reactions from simulation context
		// ReactionSpec[] reactionSpecs = simContext.getReactionContext().getReactionSpecs();---need to take a look here!
		ModelUnitSystem modelUnitSystem = getSimulationContext().getModel().getUnitSystem();
		ReactionSpec[] reactionSpecs = getSimulationContext().getReactionContext().getReactionSpecs();
		for (ReactionSpec reactionSpec : reactionSpecs)
		{
			if (reactionSpec.isExcluded()) {
				continue;
			}
						
			// get the reaction
			ReactionStep reactionStep = reactionSpec.getReactionStep();
			Kinetics kinetics = reactionStep.getKinetics();
			
	    	// probability parameter from modelUnitSystem
			VCUnitDefinition probabilityParamUnit = modelUnitSystem.getStochasticSubstanceUnit().divideBy(modelUnitSystem.getTimeUnit());

			// check the reaction rate law to see if we need to decompose a reaction(reversible) into two jump processes.
			// rate constants are important in calculating the probability rate.
			// for Mass Action, we use KForward and KReverse,
			// for General Kinetics we parse reaction rate J to see if it is in Mass Action form.
			StochasticFunction stochasticFunction = StochasticTransformer.transformToStochastic(reactionStep);

			// forward jump process
			if (stochasticFunction.hasForwardRate()) {
				// get jump process name
				String jpName = TokenMangler.mangleToSName(reactionStep.getName());
				final Expression probabilityExpression;
				if (stochasticFunction instanceof MassActionStochasticFunction massActionStochasticFunction) {
					probabilityExpression = getProbabilityRate(reactionStep, massActionStochasticFunction, true);
				} else if (stochasticFunction instanceof GeneralKineticsStochasticFunction generalKineticsStochasticFunction) {
					probabilityExpression = getProbabilityRate(reactionStep, generalKineticsStochasticFunction, true);
				} else {
					throw new MappingException("Unsupported stochastic function type: " + stochasticFunction.getClass().getName());
				}

				ProbabilityParameter probRateParm = null;
				try{
					probRateParm = addProbabilityParameter(PARAMETER_PROBABILITYRATE_PREFIX+jpName, probabilityExpression, PARAMETER_ROLE_P, probabilityParamUnit, reactionStep);
				}catch(PropertyVetoException pve){
					throw new MappingException(pve.getMessage(), pve);
				}
				//add probabilityRate function or constant
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(probRateParm,geometryClass),getIdentifierSubstitutions(probabilityExpression, probabilityParamUnit, geometryClass),geometryClass));

				JumpProcess forwardJumpProcess = new JumpProcess(jpName,new Expression(getMathSymbol(probRateParm,geometryClass)));
				// actions
				ReactionParticipant[] reacPart = reactionStep.getReactionParticipants();
				for(int j=0; j<reacPart.length; j++)
				{
					Action action = null;
					SpeciesCountParameter spCountParam = getSpeciesCountParameter(reacPart[j].getSpeciesContext());
					if(reacPart[j] instanceof Reactant)
					{
						// check if the reactant is a constant. If the species is a constant, there will be no action taken on this species
						if (!simContext.getReactionContext().getSpeciesContextSpec(reacPart[j].getSpeciesContext()).isConstant()) // not a constant
						{
							int stoi = ((Reactant)reacPart[j]).getStoichiometry();
							action = Action.createIncrementAction(varHash.getVariable(getMathSymbol(spCountParam, geometryClass)),new Expression(-stoi));
							forwardJumpProcess.addAction(action);
						}
					}
					else if(reacPart[j] instanceof Product)
					{
						// check if the product is a constant. If the product is a constant, there will be no action taken on this species
						if (!simContext.getReactionContext().getSpeciesContextSpec(reacPart[j].getSpeciesContext()).isConstant()) // not a constant
						{
							int stoi = ((Product)reacPart[j]).getStoichiometry();
							action = Action.createIncrementAction(varHash.getVariable(getMathSymbol(spCountParam, geometryClass)),new Expression(stoi));
							forwardJumpProcess.addAction(action);
						}
					}
				}
				// add jump process to compartment subDomain
				subDomain.addJumpProcess(forwardJumpProcess);
			}
			if (stochasticFunction.hasReverseRate()) // one more jump process for a reversible reaction
			{
				// get jump process name
				String jpName = TokenMangler.mangleToSName(reactionStep.getName())+PARAMETER_PROBABILITY_RATE_REVERSE_SUFFIX;


				final Expression probabilityExpression;
				if (stochasticFunction instanceof MassActionStochasticFunction massActionStochasticFunction) {
					probabilityExpression = getProbabilityRate(reactionStep, massActionStochasticFunction, false);
				} else if (stochasticFunction instanceof GeneralKineticsStochasticFunction generalKineticsStochasticFunction) {
					probabilityExpression = getProbabilityRate(reactionStep, generalKineticsStochasticFunction, false);
				} else {
					throw new MappingException("Unsupported stochastic function type: " + stochasticFunction.getClass().getName());
				}

				ProbabilityParameter probRevParm = null;
				try{
					probRevParm = addProbabilityParameter(PARAMETER_PROBABILITYRATE_PREFIX+jpName,probabilityExpression,PARAMETER_ROLE_P_reverse, probabilityParamUnit,reactionStep);
				}catch(PropertyVetoException pve){
					throw new MappingException(pve.getMessage(), pve);
				}
				//add probability to function or constant
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(probRevParm,geometryClass),getIdentifierSubstitutions(probabilityExpression, probabilityParamUnit, geometryClass),geometryClass));

				JumpProcess jp = new JumpProcess(jpName,new Expression(getMathSymbol(probRevParm,geometryClass)));
				// actions
				ReactionParticipant[] reacPart = reactionStep.getReactionParticipants();
				for(int j=0; j<reacPart.length; j++)
				{
					Action action = null;
					SpeciesCountParameter spCountParam = getSpeciesCountParameter(reacPart[j].getSpeciesContext());
					if(reacPart[j] instanceof Reactant)
					{
						// check if the reactant is a constant. If the species is a constant, there will be no action taken on this species
						if(!simContext.getReactionContext().getSpeciesContextSpec(reacPart[j].getSpeciesContext()).isConstant()) // not a constant
						{
							int stoi = ((Reactant)reacPart[j]).getStoichiometry();
							action = Action.createIncrementAction(varHash.getVariable(getMathSymbol(spCountParam, geometryClass)),new Expression(stoi));
							jp.addAction(action);
						}
					}
					else if(reacPart[j] instanceof Product)
					{
						// check if the product is a constant. If the product is a constant, there will be no action taken on this species
						if(!simContext.getReactionContext().getSpeciesContextSpec(reacPart[j].getSpeciesContext()).isConstant()) // not a constant
						{
							int stoi = ((Product)reacPart[j]).getStoichiometry();
							action = Action.createIncrementAction(varHash.getVariable(getMathSymbol(spCountParam, geometryClass)),new Expression(-stoi));
							jp.addAction(action);
						}
					}
				}
				// add jump process to compartment subDomain
				subDomain.addJumpProcess(jp);
			} // end of if(isForwardRateNonZero), if(isReverseRateNonRate)
		} // end of reaction step loop
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
//		for (ReactionSpec reactionSpec : getSimulationContext().getReactionContext().getReactionSpecs()){
//			if (!reactionSpec.isExcluded() && reactionSpec.hasHybrid(getSimulationContext(), scs.getSpeciesContext())){
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
 * Map speciesContext to variable, used for structural analysis (slow reactions and fast reactions)
 * Creation date: (10/25/2006 8:59:43 AM)
 * @exception cbit.vcell.mapping.MappingException The exception description.
 */
@Override
protected void refreshVariables() throws MappingException {

	//
	// stochastic species need  species variables require either a membrane or volume variable
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
			throw new MappingException(pve.getMessage(), pve);
		}
		
		//add concentration of species as MathMappingParameter - this will map to species concentration function
		try{
			String concName = scs.getSpeciesContext().getName() + BIO_PARAM_SUFFIX_SPECIES_CONCENTRATION;
			Expression concExp = getExpressionAmtToConc(new Expression(spCountParm,getNameScope()), scs.getSpeciesContext().getStructure());
			concExp.bindExpression(this);
			addSpeciesConcentrationParameter(concName, concExp, PARAMETER_ROLE_SPECIES_CONCENRATION, scs.getSpeciesContext().getUnitDefinition(), scs.getSpeciesContext());
		}catch(Exception e){
			throw new MappingException(e.getMessage(),e);
		}
		//we always add variables, all species are independent variables, no matter they are constant or not.
		String countMathSymbol = getMathSymbol(spCountParm, getSimulationContext().getGeometryContext().getStructureMapping(scs.getSpeciesContext().getStructure()).getGeometryClass());
		scm.setVariable(new StochVolVariable(countMathSymbol));
	}
	
	//
	// if the original (untransformed) model has any explicit observables (which are rule-based components), then the transformed model maps these observables to "Concentration" ModelParameters.
	// 
	// for symmetry with the RuleBasedMathMapping, we want to generate a "_Count" version of these observables if applicable.
	//
	// so if a rule-to-network "transformation" was performed, we want to find those ModelParameters which map to Observables (concentrations) so that we can generate an additional "Count" function (by scaling by compartment size and performing a unit conversion).
	//
	SimContextTransformation transformation = getTransformation();
	if (transformation!=null){
		ModelEntityMapping[] modelEntityMappings = transformation.modelEntityMappings;
		if (modelEntityMappings!=null){
			for (ModelEntityMapping mem : modelEntityMappings){
				if (mem.newModelObj instanceof ModelParameter && mem.origModelObj instanceof RbmObservable){
					ModelParameter concObservableParameter = (ModelParameter)mem.newModelObj;
					RbmObservable observable = (RbmObservable)mem.origModelObj;
					try {
						Expression countExp = getExpressionConcToExpectedCount(new Expression(concObservableParameter,getNameScope()), observable.getStructure());
						//countExp.bindExpression(this);
						addObservableCountParameter(concObservableParameter.getName() + BIO_PARAM_SUFFIX_SPECIES_COUNT, countExp, PARAMETER_ROLE_OBSERVABLE_COUNT, getSimulationContext().getModel().getUnitSystem().getStochasticSubstanceUnit(), observable);
					} catch (ExpressionException | PropertyVetoException e) {
						throw new MappingException(e.getMessage(),e);
					}
				}
			}
		}
	}	

}

}

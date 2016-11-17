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
import java.beans.PropertyVetoException;
import java.util.Enumeration;
import java.util.Vector;

import org.vcell.util.TokenMangler;

import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.mapping.SimContextTransformer.ModelEntityMapping;
import cbit.vcell.mapping.SimContextTransformer.SimContextTransformation;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.math.Action;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.JumpProcess;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.StochVolVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.VarIniCondition;
import cbit.vcell.math.VarIniCount;
import cbit.vcell.math.VarIniPoissonExpectedCount;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableHash;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.matrix.RationalExp;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.MassActionSolver;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.Product;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.RationalExpUtils;
import cbit.vcell.units.VCUnitDefinition;
/**
 * The StochMathMapping class performs the Biological to Mathematical transformation once upon calling getMathDescription()
 * for stochastic simulation. To get math description for deterministic simulation please reference @MathMapping.
 * ApplicationEditor.updatMath() decides to use either StochMathMapping or MathMapping.
 * Created Sept. 18, 2006
 * @version 1.0 Beta
 * @author Tracy LI
 */
public class StochMathMapping extends AbstractStochMathMapping {

	/**
	 * The constructor, which pass the simulationContext pointer.
	 * @param model cbit.vcell.model.Model
	 * @param geometry cbit.vcell.geometry.Geometry
	 */
	protected StochMathMapping(SimulationContext simContext, MathMappingCallback callback,	NetworkGenerationRequirements networkGenerationRequirements) {
		super(simContext, callback, networkGenerationRequirements);
	}

/**
 * Get probability expression for the specific elementary reaction.
 * Input: ReactionStep, the reaction. isForwardDirection, if the elementary reaction is forward from the reactionstep.
 * Output: Expression. the probability expression.
 * Creation date: (9/14/2006 3:22:58 PM)
 * @throws ExpressionException 
 */
private Expression getProbabilityRate(ReactionStep reactionStep, Expression rateConstantExpr, boolean isForwardDirection) throws MappingException, ExpressionException
{
	//the structure where reaction happens
	StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(reactionStep.getStructure());
	Model model = getSimulationContext().getModel();

	Expression reactionStructureSize = new Expression(sm.getStructure().getStructureSize(), getNameScope());
	
	VCUnitDefinition reactionSubstanceUnit = model.getUnitSystem().getSubstanceUnit(reactionStep.getStructure());
	VCUnitDefinition stochasticSubstanceUnit = model.getUnitSystem().getStochasticSubstanceUnit();
	Expression reactionSubstanceUnitFactor = getUnitFactor(stochasticSubstanceUnit.divideBy(reactionSubstanceUnit));

	Expression factorExpr = Expression.mult(reactionStructureSize,reactionSubstanceUnitFactor);

	//complete the probability expression by the reactants' stoichiometries 
	Expression rxnProbabilityExpr = null; 	//to compose the stochastic variable(species) expression, e.g. s*(s-1)*(s-2)* speciesFactor.
	ReactionParticipant[] reacPart = reactionStep.getReactionParticipants();
	for (int i=0; i<reacPart.length; i++)
	{
		VCUnitDefinition speciesSubstanceUnit = model.getUnitSystem().getSubstanceUnit(reacPart[i].getStructure());
		Expression speciesUnitFactor = getUnitFactor(speciesSubstanceUnit.divideBy(stochasticSubstanceUnit));

		int stoichiometry = 0;
		if((reacPart[i] instanceof Reactant && isForwardDirection) || (reacPart[i] instanceof Product && !isForwardDirection)) 
		{
			stoichiometry = reacPart[i].getStoichiometry();
			//******the following part is to form the s*(s-1)(s-2)..(s-stoi+1).portion of the probability rate.
			Expression speciesStructureSize = new Expression(reacPart[i].getStructure().getStructureSize(), getNameScope());
			Expression speciesFactor = Expression.div(speciesUnitFactor, speciesStructureSize);
			//s*(s-1)(s-2)..(s-stoi+1)
			SpeciesCountParameter spCountParam = getSpeciesCountParameter(reacPart[i].getSpeciesContext());
			Expression spCount_exp = new Expression(spCountParam, getNameScope());
			Expression speciesFactorial = new Expression(spCount_exp);//species from uM to No. of Particles, form s*(s-1)*(s-2)
			for(int j = 1; j < stoichiometry; j++) {
				speciesFactorial = Expression.mult(speciesFactorial, Expression.add(spCount_exp, new Expression(-j)));
			}
			//update total factor with speceies factor
			if(stoichiometry == 1) {
				factorExpr = Expression.mult(factorExpr, speciesFactor);
			} else if (stoichiometry > 1) {
				// rxnProbExpr * (structSize^stoichiometry)
				factorExpr = Expression.mult(factorExpr, Expression.power(speciesFactor, new Expression(stoichiometry)));
			}
			if (rxnProbabilityExpr == null) {
				rxnProbabilityExpr = new Expression(speciesFactorial);
			} else {//for more than one reactant
				rxnProbabilityExpr = Expression.mult(rxnProbabilityExpr, speciesFactorial);
			}
		}
	}

	// Now construct the probability expression.
	Expression probExp = null;
	if(rateConstantExpr == null) {
		throw new MappingException("Can not find reaction rate constant in reaction: "+ reactionStep.getName());
	} else if(rxnProbabilityExpr == null) {
	 	probExp = new Expression(rateConstantExpr);   
	} else if((rateConstantExpr != null) && (rxnProbabilityExpr != null)) {
		probExp = Expression.mult(rateConstantExpr, rxnProbabilityExpr);
    }
	//simplify the factor
	RationalExp factorRatExp = RationalExpUtils.getRationalExp(factorExpr);
	factorExpr = new Expression(factorRatExp.infixString());
	factorExpr.bindExpression(this);
	//get probability rate with converting factor
	probExp = Expression.mult(probExp, factorExpr);
	probExp = probExp.flatten();


	return probExp;
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
		for (ReactionStep rs : rsList){
			if (rs.getKinetics() instanceof LumpedKinetics){
				throw new RuntimeException("Lumped Kinetics not yet supported for Stochastic Math Generation");
			}
			Kinetics.KineticsParameter parameters[] = rs.getKinetics().getKineticsParameters();
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
					e.printStackTrace(System.out);
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
		// set Variables to MathDescription all at once with the order resolved by "VariableHash"
		//
		mathDesc.setAllVariables(varHash.getAlphabeticallyOrderedVariables());
		
		//
		// set up variable initial conditions in subDomain
		//
		SpeciesContextSpec scSpecs[] = simContext.getReactionContext().getSpeciesContextSpecs();
		for (int i = 0; i < speciesContextSpecs.length; i++){
			//get stochastic variable by name
			SpeciesCountParameter spCountParam = getSpeciesCountParameter(speciesContextSpecs[i].getSpeciesContext());
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(speciesContextSpecs[i].getSpeciesContext().getStructure());
			String varName = getMathSymbol(spCountParam, sm.getGeometryClass()); 

			StochVolVariable var = (StochVolVariable)mathDesc.getVariable(varName);
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
				if (mathDesc.getVariable(variable.getName())==null){
					mathDesc.addVariable(variable);
				}
			}
			if (fieldMathMappingParameters[i] instanceof ObservableCountParameter){
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

			// Different ways to deal with simple reactions and flux reactions
			if(reactionStep instanceof SimpleReaction) // simple reactions
			{
				// check the reaction rate law to see if we need to decompose a reaction(reversible) into two jump processes.
				// rate constants are important in calculating the probability rate.
				// for Mass Action, we use KForward and KReverse, 
				// for General Kinetics we parse reaction rate J to see if it is in Mass Action form.
				Expression forwardRate = null;
				Expression reverseRate = null;
				if (kinetics.getKineticsDescription().equals(KineticsDescription.MassAction) ||
					kinetics.getKineticsDescription().equals(KineticsDescription.General))
				{
					Expression rateExp = new Expression(kinetics.getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate),reactionStep.getNameScope());
					Parameter forwardRateParameter = null;
					Parameter reverseRateParameter = null;
					if (kinetics.getKineticsDescription().equals(KineticsDescription.MassAction)){
						forwardRateParameter = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KForward);
						reverseRateParameter = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KReverse);
					}
					MassActionSolver.MassActionFunction maFunc = MassActionSolver.solveMassAction(forwardRateParameter, reverseRateParameter, rateExp, reactionStep);
					if(maFunc.getForwardRate() == null && maFunc.getReverseRate() == null)
					{
						throw new MappingException("Cannot generate stochastic math mapping for the reaction:" + reactionStep.getName() + "\nLooking for the rate function according to the form of k1*Reactant1^Stoir1*Reactant2^Stoir2...-k2*Product1^Stoip1*Product2^Stoip2.");
					}
					else
					{
						if(maFunc.getForwardRate() != null)
						{
							forwardRate = maFunc.getForwardRate();
						}
						if(maFunc.getReverseRate() != null)
						{
							reverseRate = maFunc.getReverseRate();
						}
					}
				}
				//if it's macro/microscopic kinetics, we'll have them set up as reactions with only forward rate.
				else if(kinetics.getKineticsDescription().equals(KineticsDescription.Macroscopic_irreversible) ||
						kinetics.getKineticsDescription().equals(KineticsDescription.Microscopic_irreversible))
				{
					Expression Kon = getIdentifierSubstitutions(new Expression(reactionStep.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_KOn),getNameScope()), 
                            reactionStep.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_Binding_Radius).getUnitDefinition(), geometryClass);
					if(Kon != null)
					{
						Expression KonCopy = new Expression(Kon);
						try{
							MassActionSolver.substituteParameters(KonCopy, true).evaluateConstant();
							forwardRate = new Expression(Kon);
						}catch(ExpressionException e)
						{
							throw new MathException(VCellErrorMessages.getMassActionSolverMessage(reactionStep.getName(), "Problem with Kon parameter in " + reactionStep.getName() +":  '" + KonCopy.infix() + "', " + e.getMessage()));
						}
					}
					else
					{
						throw new MathException(VCellErrorMessages.getMassActionSolverMessage(reactionStep.getName(), "Kon parameter of " + reactionStep.getName() +" is null."));
					}
				}
			    boolean isForwardRatePresent = false;
			    boolean isReverseRatePresent = false;
		       	if(forwardRate != null)
		    	{
		       		isForwardRatePresent = true;
		    	}
		    	
		    	if(reverseRate != null)
		    	{
		    		isReverseRatePresent = true;
		    	}
			    
				// if the reaction has forward rate (Mass action,HMMs), or don't have either forward or reverse rate (some other rate laws--like general)
				// we process it as forward reaction
				if ((isForwardRatePresent) /*|| ((forwardRate == null) && (reverseRate == null))*/)
				{
					// get jump process name
					String jpName = TokenMangler.mangleToSName(reactionStep.getName());
					// get probability
					Expression exp = null;
									
					// reactions are of mass action form
					exp = getProbabilityRate(reactionStep, forwardRate, true);
					
					ProbabilityParameter probParm = null;
					try{
						probParm = addProbabilityParameter(PARAMETER_PROBABILITYRATE_PREFIX+jpName, exp, PARAMETER_ROLE_P, probabilityParamUnit,reactionStep);
					}catch(PropertyVetoException pve){
						pve.printStackTrace();
						throw new MappingException(pve.getMessage());
					}
					
					//add probability to function or constant
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(probParm,geometryClass),getIdentifierSubstitutions(exp, probabilityParamUnit, geometryClass),geometryClass));
										
					JumpProcess jp = new JumpProcess(jpName,new Expression(getMathSymbol(probParm,geometryClass)));
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
								action = Action.createIncrementAction(varHash.getVariable(getMathSymbol(spCountParam, geometryClass)),new Expression(-stoi));
								jp.addAction(action);
							}
						}
						else if(reacPart[j] instanceof Product)
						{
							// check if the product is a constant. If the product is a constant, there will be no action taken on this species
							if(!simContext.getReactionContext().getSpeciesContextSpec(reacPart[j].getSpeciesContext()).isConstant()) // not a constant
							{
								int stoi = ((Product)reacPart[j]).getStoichiometry();
								action = Action.createIncrementAction(varHash.getVariable(getMathSymbol(spCountParam, geometryClass)),new Expression(stoi));
								jp.addAction(action);
							}
						}
					}
					// add jump process to compartment subDomain
					subDomain.addJumpProcess(jp);
				}
				if (isReverseRatePresent) // one more jump process for a reversible reaction
				{
					// get jump process name
					String jpName = TokenMangler.mangleToSName(reactionStep.getName())+PARAMETER_PROBABILITY_RATE_REVERSE_SUFFIX;
					Expression exp = null;
					
					// reactions are mass actions
					exp = getProbabilityRate(reactionStep, reverseRate, false);
					
					ProbabilityParameter probRevParm = null;
					try{
						probRevParm = addProbabilityParameter(PARAMETER_PROBABILITYRATE_PREFIX+jpName,exp,PARAMETER_ROLE_P_reverse, probabilityParamUnit,reactionStep);
					}catch(PropertyVetoException pve){
						pve.printStackTrace();
						throw new MappingException(pve.getMessage());
					}
					//add probability to function or constant
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(probRevParm,geometryClass),getIdentifierSubstitutions(exp, probabilityParamUnit, geometryClass),geometryClass));
									
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
			}
			else if(reactionStep instanceof FluxReaction)// flux reactions
			{
				//we could set jump processes for general flux rate in forms of p1*Sout + p2*Sin
				if(kinetics.getKineticsDescription().equals(KineticsDescription.General) || kinetics.getKineticsDescription().equals(KineticsDescription.GeneralPermeability) )
				{
					Expression fluxRate = new Expression(kinetics.getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate),reactionStep.getNameScope());
					//we have to pass the math description para to flux solver, coz somehow math description in simulation context is not updated.
					// forward and reverse rate parameters may be null
					Parameter forwardRateParameter = null;
					Parameter reverseRateParameter = null;
					if (kinetics.getKineticsDescription().equals(KineticsDescription.GeneralPermeability)){
						forwardRateParameter = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_Permeability);
						reverseRateParameter = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_Permeability);
					}
					MassActionSolver.MassActionFunction fluxFunc = MassActionSolver.solveMassAction(forwardRateParameter, reverseRateParameter, fluxRate, (FluxReaction)reactionStep);
					//create jump process for forward flux if it exists.
					Expression rsStructureSize = new Expression(reactionStep.getStructure().getStructureSize(), getNameScope());
					VCUnitDefinition probRateUnit = modelUnitSystem.getStochasticSubstanceUnit().divideBy(modelUnitSystem.getAreaUnit()).divideBy(modelUnitSystem.getTimeUnit());
					Expression rsRateUnitFactor = getUnitFactor(probRateUnit.divideBy(modelUnitSystem.getFluxReactionUnit()));
					if(fluxFunc.getForwardRate() != null && !fluxFunc.getForwardRate().isZero()) 
					{
											
						Expression rate = fluxFunc.getForwardRate();
						//get species expression (depend on structure, if mem: Species/mem_Size, if vol: species*KMOLE/vol_size)
						if(fluxFunc.getReactants().size() != 1)
						{
							throw new MappingException("Flux " + reactionStep.getName() + " should have only one reactant." );
						}
						SpeciesContext scReactant = fluxFunc.getReactants().get(0).getSpeciesContext();
						
						Expression scConcExpr = new Expression(getSpeciesConcentrationParameter(scReactant),getNameScope());

						Expression probExp = Expression.mult(rate, rsRateUnitFactor, rsStructureSize, scConcExpr);

						//jump process name
						String jpName = TokenMangler.mangleToSName(reactionStep.getName());//+"_reverse";
						ProbabilityParameter probParm = null;
						try{
							probParm = addProbabilityParameter(PARAMETER_PROBABILITYRATE_PREFIX+jpName,probExp,PARAMETER_ROLE_P, probabilityParamUnit,reactionStep);
						}catch(PropertyVetoException pve){
							pve.printStackTrace();
							throw new MappingException(pve.getMessage());
						}
						//add probability to function or constant
						String ms = getMathSymbol(probParm,geometryClass);
						Expression is = getIdentifierSubstitutions(probExp, probabilityParamUnit, geometryClass);
						Variable nfoc = newFunctionOrConstant(ms,is,geometryClass);
						varHash.addVariable(nfoc);
										
						JumpProcess jp = new JumpProcess(jpName,new Expression(getMathSymbol(probParm,geometryClass)));
						// actions
						Action action = null;
						SpeciesContext sc = fluxFunc.getReactants().get(0).getSpeciesContext();
						
						if (!simContext.getReactionContext().getSpeciesContextSpec(sc).isConstant()) {
							SpeciesCountParameter spCountParam = getSpeciesCountParameter(sc);
							action = Action.createIncrementAction(varHash.getVariable(getMathSymbol(spCountParam, geometryClass)),new Expression(-1));
							jp.addAction(action);
						}	
						
						sc = fluxFunc.getProducts().get(0).getSpeciesContext();
						if (!simContext.getReactionContext().getSpeciesContextSpec(sc).isConstant()) {
							SpeciesCountParameter spCountParam = getSpeciesCountParameter(sc);
							action = Action.createIncrementAction(varHash.getVariable(getMathSymbol(spCountParam, geometryClass)),new Expression(1));
							jp.addAction(action);
						}
							
						subDomain.addJumpProcess(jp);
					}
					//create jump process for reverse flux if it exists.
					if(fluxFunc.getReverseRate() != null && !fluxFunc.getReverseRate().isZero()) 
					{
						//jump process name
						String jpName = TokenMangler.mangleToSName(reactionStep.getName())+PARAMETER_PROBABILITY_RATE_REVERSE_SUFFIX;
											
						Expression rate = fluxFunc.getReverseRate();
						//get species expression (depend on structure, if mem: Species/mem_Size, if vol: species*KMOLE/vol_size)
						if(fluxFunc.getProducts().size() != 1)
						{
							throw new MappingException("Flux " + reactionStep.getName() + " should have only one product." );
						}
						SpeciesContext scProduct = fluxFunc.getProducts().get(0).getSpeciesContext();
						
						Expression scConcExpr = new Expression(getSpeciesConcentrationParameter(scProduct),getNameScope());

						Expression probExp = Expression.mult(rate, rsRateUnitFactor, rsStructureSize, scConcExpr);
						
						ProbabilityParameter probRevParm = null;
						try{
							probRevParm = addProbabilityParameter(PARAMETER_PROBABILITYRATE_PREFIX+jpName,probExp,PARAMETER_ROLE_P_reverse, probabilityParamUnit,reactionStep);
						}catch(PropertyVetoException pve){
							pve.printStackTrace();
							throw new MappingException(pve.getMessage());
						}
						//add probability to function or constant
						varHash.addVariable(newFunctionOrConstant(getMathSymbol(probRevParm,geometryClass),getIdentifierSubstitutions(probExp, probabilityParamUnit, geometryClass),geometryClass));
										
						JumpProcess jp = new JumpProcess(jpName,new Expression(getMathSymbol(probRevParm,geometryClass)));
						// actions
						Action action = null;
						SpeciesContext sc = fluxFunc.getReactants().get(0).getSpeciesContext();
						if (!simContext.getReactionContext().getSpeciesContextSpec(sc).isConstant()) {
							SpeciesCountParameter spCountParam = getSpeciesCountParameter(sc);
							action = Action.createIncrementAction(varHash.getVariable(getMathSymbol(spCountParam, geometryClass)),new Expression(1));
							jp.addAction(action);
						}
							
						sc = fluxFunc.getProducts().get(0).getSpeciesContext();
						if (!simContext.getReactionContext().getSpeciesContextSpec(sc).isConstant()) {
							SpeciesCountParameter spCountParam = getSpeciesCountParameter(sc);
							action = Action.createIncrementAction(varHash.getVariable(getMathSymbol(spCountParam, geometryClass)),new Expression(-1));
							jp.addAction(action);
						}
						
						subDomain.addJumpProcess(jp);
					}
				}
			}//end of if (simplereaction)...else if(fluxreaction)
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
						e.printStackTrace();
						throw new MappingException(e.getMessage(),e);
					}
				}
			}
		}
	}	

}

}

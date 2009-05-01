package cbit.vcell.mapping;
import java.beans.PropertyVetoException;
import java.util.Enumeration;
import java.util.Vector;

import cbit.vcell.geometry.SubVolume;
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
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.Product;
import cbit.vcell.model.ProxyParameter;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.stoch.FluxSolver;
import cbit.vcell.solver.stoch.MassActionSolver;
import cbit.vcell.units.VCUnitDefinition;
/**
 * The StochMathMapping class performs the Biological to Mathematical transformation once upon calling getMathDescription()
 * for stochastic simulation. To get math description for deterministic simulation please reference @MathMapping.
 * ApplicationEditor.updatMath() decides to use either StochMathMapping or MathMapping.
 * Created Sept. 18, 2006
 * @version 1.0 Beta
 * @author Tracy LI
 */
public class StochMathMapping extends MathMapping {

	/**
	 * The constructor, which pass the simulationContext pointer.
	 * @param model cbit.vcell.model.Model
	 * @param geometry cbit.vcell.geometry.Geometry
	 */
	public StochMathMapping(SimulationContext simContext) {
		super(simContext);
	}

/**
 * getExpressionConcToAmt : converts the concentration expression ('concExpr') to an expression of the number of particles. 
 * 		If argument 'speciesContext' is on a membrane, particlesExpr = concExpr * size_of_Mem. If 'speciesContext' is in 
 * 		feature, particlesExpr = (concExpr * size_of_Feature)/KMOLE.
 * @param concExpr
 * @param speciesContext
 * @return
 * @throws MappingException
 * @throws ExpressionException
 */
public Expression getExpressionConcToAmt(Expression concExpr, SpeciesContext speciesContext) throws MappingException, ExpressionException
{
	Expression particlesExpr = null;	//to create an expression for number of particles 

	if (speciesContext.getStructure() instanceof Membrane)
	{
		// convert concentration(particles/area) to number of particles
		particlesExpr = Expression.mult(concExpr, new Expression(speciesContext.getStructure().getStructureSize().getName())); // particles = concentration(molecues/um2) * size(um2)
	}
	else
	{
		// convert number of particles to concentration(particles/volume)
		// particles = [iniConcentration(uM)*size(um3)]/KMOLE
		Expression numeratorExpr = Expression.mult(concExpr, new Expression(speciesContext.getStructure().getStructureSize().getName()));
		Expression denominatorExpr = new Expression(ReservedSymbol.KMOLE.getName());
		particlesExpr = Expression.div(numeratorExpr, denominatorExpr);
	}
	
	return particlesExpr;
}

/**
 * getExpressionAmtToConc : converts the particles expression ('particlesExpr') to an expression for concentration. 
 * 		If argument 'speciesContext' is on a membrane, concExpr = particlesExpr/size_of_Mem. If 'speciesContext' is in 
 * 		feature, concExpr = (particlesExpr/size_of_Feature)*KMOLE.
 * @param particlesExpr
 * @param speciesContext
 * @return
 * @throws MappingException
 * @throws ExpressionException
 */
public Expression getExpressionAmtToConc(Expression particlesExpr, SpeciesContext speciesContext) throws MappingException, ExpressionException
{
	Expression concentrationExpr = null;	//to create an expression for concentration 

	if (speciesContext.getStructure() instanceof Membrane)
	{
		// convert number of particles to concentration(particles/area) 
		concentrationExpr = Expression.div(particlesExpr, new Expression(speciesContext.getStructure().getStructureSize().getName())); // particles/size(um2) = concentration(molecues/um2)
	}
	else
	{
		// convert number of particles to concentration(particles/volume) 
		// concentration(uM) = [particles/size(um3)]*KMOLE)
		Expression numeratorExpr = Expression.mult(particlesExpr, new Expression(ReservedSymbol.KMOLE.getName()));
		Expression denominatorExpr = new Expression(speciesContext.getStructure().getStructureSize().getName());
		concentrationExpr = Expression.div(numeratorExpr, denominatorExpr);
	}
	
	return concentrationExpr;
}


	/**
	 * This method returns the mathDeac if it is existing, otherwise it creates a mathDescription and returns it.
	 * @return cbit.vcell.math.MathDescription
	 */
	public MathDescription getMathDescription() throws MappingException, MathException, cbit.vcell.matrix.MatrixException, ExpressionException, ModelException {
		if (mathDesc==null){
			refresh();
		}
		return mathDesc;
	}


/**
 * Get probability expression for the specific elementary reaction.
 * Input: ReactionStep, the reaction. isForwardDirection, if the elementary reaction is forward from the reactionstep.
 * Output: Expression. the probability expression.
 * Creation date: (9/14/2006 3:22:58 PM)
 */
public Expression getProbabilityRate(ReactionStep rs, boolean isForwardDirection) throws MappingException
{
	ReactionStep reactionStep = rs;
	Expression probExp = null;
	//get kinetics of the reaction step
	Kinetics kinetics = reactionStep.getKinetics();
	Expression rateConstantExpr = null; 	//to compose the probability expression
	Expression rxnProbabilityExpr = null; 	//to compose the probability expression
	//the structure where reaction happens
	StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(rs.getStructure());
	try {
		if(isForwardDirection) // forward reaction
		{
			//get the reaction rate constant and convert it to rate of Number of particles
			//for HMMs, it's a bit complicated. Vmax/(Km+s)-->Vmax*Size_s/(Km*Size_s+Ns)
			if (kinetics.getKineticsDescription().equals(KineticsDescription.MassAction))
			{
				KineticsParameter kfp = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KForward); 
				rateConstantExpr = new Expression(getNameScope().getSymbolName(kfp));
				rateConstantExpr.bindExpression(this);
			}
		    // count the structure size from the product side(location where the reaction happens actually). rateConstant*(feature_size*KMole OR membrane_Size)
		    if(sm.getStructure() instanceof Membrane) {
		    	rateConstantExpr = Expression.mult(rateConstantExpr, new Expression(sm.getStructure().getStructureSize().getName()));
		    } else {
		    	Expression numeratorExpr = Expression.mult(rateConstantExpr, new Expression(sm.getStructure().getStructureSize().getName()));
		    	Expression denominatorExpr = new Expression(ReservedSymbol.KMOLE.getName());
		    	rateConstantExpr = Expression.div(numeratorExpr, denominatorExpr);
			}
			
			//complete the probability expression by the reactants' stoichiometries if it is Mass Action rate law
			if(kinetics.getKineticsDescription().equals(KineticsDescription.MassAction))
			{
				ReactionParticipant[] reacPart = reactionStep.getReactionParticipants();
				for (int i=0; i<reacPart.length; i++)
				{
					int stoichiometry = 0;
					Expression tempExpr = null;
					if(reacPart[i] instanceof Reactant) 
					{ 
						stoichiometry = ((Reactant)reacPart[i]).getStoichiometry();
						//******the following part is to form the s*(s-1)(s-2)..(s-stoi+1).portion of the probability rate.
						StructureMapping reactSM = getSimulationContext().getGeometryContext().getStructureMapping(reacPart[i].getStructure());
						Expression reactStructSizeExpr = null;
						//convert speceis' unit from moles/liter to molecules.
						if(reactSM.getStructure() instanceof Membrane) {
							reactStructSizeExpr = Expression.invert(new Expression(reactSM.getStructure().getStructureSize().getName()));
						} else {
							Expression numExpr = new Expression(ReservedSymbol.KMOLE.getName());
							Expression denomExpr = new Expression(reactSM.getStructure().getStructureSize().getName());
							reactStructSizeExpr =  Expression.div(numExpr, denomExpr);
						}
						//s*(s-1)(s-2)..(s-stoi+1)
						SpeciesCountParameter spCountParam = getSpeciesCountParameter(reacPart[i].getSpeciesContext());
						for(int j = 0; j < stoichiometry; j++) {
							if(j == 0) {
								tempExpr = new Expression(spCountParam.getName());
							} else {
								tempExpr = Expression.mult(tempExpr, new Expression(spCountParam.getName()+"-"+j));
							}
						}
	
						if(stoichiometry == 1) {
							tempExpr = Expression.mult(tempExpr, reactStructSizeExpr);
						} else if (stoichiometry > 1) {
							// rxnProbExpr * (structSize^stoichiometry)
							Expression powerExpr = Expression.power(reactStructSizeExpr, new Expression(stoichiometry));
							tempExpr = Expression.mult(tempExpr, powerExpr);
						}
						if (rxnProbabilityExpr == null) {
							rxnProbabilityExpr = new Expression(tempExpr);
						} else {
							rxnProbabilityExpr = Expression.mult(rxnProbabilityExpr, tempExpr);
						}
					}
				}
			}
		} 
		else // reverse reaction
		{
			if (kinetics.getKineticsDescription().equals(KineticsDescription.MassAction))
			{
				KineticsParameter krp = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KReverse); 
				rateConstantExpr = new Expression(getNameScope().getSymbolName(krp));
				rateConstantExpr.bindExpression(this);
			}
		    // count the structure size from the product side(location where the reaction happens actually). rateConstant*(feature_size*KMole OR membrane_Size) 
		    if(sm.getStructure() instanceof Membrane) {
				rateConstantExpr = Expression.mult(rateConstantExpr, new Expression(sm.getStructure().getStructureSize().getName()));
		    } else {
		    	Expression numeratorExpr = Expression.mult(rateConstantExpr, new Expression(sm.getStructure().getStructureSize().getName()));
		    	Expression denominatorExpr = new Expression(ReservedSymbol.KMOLE.getName());
		    	rateConstantExpr = Expression.div(numeratorExpr, denominatorExpr);
			}
					
			//complete the remaining part of the probability expression by the products' stoichiometries.
			if(kinetics.getKineticsDescription().equals(KineticsDescription.MassAction))
			{
				ReactionParticipant[] reacPart = reactionStep.getReactionParticipants();
				
				for (int i=0; i<reacPart.length; i++)
				{
					int stoichiometry = 0;
					Expression tempExpr = null;
					if(reacPart[i] instanceof Product) 
					{ 
						stoichiometry = ((Product)reacPart[i]).getStoichiometry();
						//******the following part is to form the s*(s-1)*(s-2)...(s-stoi+1).portion of the probability rate.
						StructureMapping reactSM = getSimulationContext().getGeometryContext().getStructureMapping(reacPart[i].getStructure());
						Expression reactStructSizeExpr = null;
						//convert species' unit from moles/liter to molecules. 
						if(reactSM.getStructure() instanceof Membrane) {
							reactStructSizeExpr = Expression.invert(new Expression(reactSM.getStructure().getStructureSize().getName()));
						} else {
							Expression numExpr = new Expression(ReservedSymbol.KMOLE.getName());
							Expression denomExpr = new Expression(reactSM.getStructure().getStructureSize().getName());
//							Expression denomExpr = Expression.invert(new Expression(reactSM.getStructure().getStructureSize().getName()));
							reactStructSizeExpr =  Expression.div(numExpr, denomExpr);
						}
						//s*(s-1)*(s-2)...(s-stoi+1)
						SpeciesCountParameter spCountParam = getSpeciesCountParameter(reacPart[i].getSpeciesContext());
						for(int j = 0; j < stoichiometry; j++) {
							if (j == 0) {
								tempExpr = new Expression(spCountParam.getName());
							} else {
								tempExpr = Expression.mult(tempExpr, new Expression(spCountParam.getName()+"-"+j));
							}
						}
	
						if(stoichiometry == 1) {
							tempExpr = Expression.mult(tempExpr, reactStructSizeExpr);
						} else if (stoichiometry > 1) {
							// rxnProbExpr * (structSize^stoichiometry)
							Expression powerExpr = Expression.power(reactStructSizeExpr, new Expression(stoichiometry));
							tempExpr = Expression.mult(tempExpr, powerExpr);
						}
						if (rxnProbabilityExpr == null) {
							rxnProbabilityExpr = new Expression(tempExpr);
						} else {
							rxnProbabilityExpr = Expression.mult(rxnProbabilityExpr, tempExpr);
						}
					}
				}
			}
		}

		// Now construct the probability expression.
		if(rateConstantExpr == null) {
			throw new MappingException("Can not find reaction rate constant in reaction: "+ reactionStep.getName());
		} else if(rxnProbabilityExpr == null) {
		 	probExp = new Expression(rateConstantExpr);   
		} else if((rateConstantExpr != null) && (rxnProbabilityExpr != null)) {
			probExp = Expression.mult(rateConstantExpr, rxnProbabilityExpr);
	    }
		
		//flatten the expression
		probExp.flatten();
	}catch (ExpressionException e) {
		e.printStackTrace();
	}

	return probExp;
}


/**
 * Basically the function clears the error list and calls to get a new mathdescription.
 */
private void refresh() throws MappingException, ExpressionException, cbit.vcell.matrix.MatrixException, MathException, ModelException{
	localIssueList.clear();
	//refreshKFluxParameters();
	
	refreshSpeciesContextMappings();
	//refreshStructureAnalyzers();
	refreshVariables();
	
	refreshLocalNameCount();
	refreshMathDescription();
}


	/**
	 * set up a math description based on current simulationContext.
	 */
	private void refreshMathDescription() throws MappingException, cbit.vcell.matrix.MatrixException, MathException, ExpressionException, ModelException
	{
		//We have to check if all the reactions are able to tranform to stochastic jump processes before generating the math.
		String stochChkMsg =getSimulationContext().getBioModel().isValidForStochApp();
		if(!(stochChkMsg.equals("")))
		{
			throw new ModelException("Problem updating math description: "+ getSimulationContext().getName()+"\n"+stochChkMsg);
		}
		//All sizes must be set for new ODE models and ratios must be set for old ones.
		getSimulationContext().checkValidity();
		

		//
		// verify that all structures are mapped to subvolumes and all subvolumes are mapped to a structure
		//
		Structure structures[] = getSimulationContext().getGeometryContext().getModel().getStructures();
		for (int i = 0; i < structures.length; i++){
			StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(structures[i]);
			if (sm==null || (sm instanceof FeatureMapping && ((FeatureMapping)sm).getSubVolume() == null)){
				throw new MappingException("model structure '"+structures[i].getName()+"' not mapped to a geometry subVolume");
			}
			if (sm!=null && (sm instanceof MembraneMapping) && ((MembraneMapping)sm).getVolumeFractionParameter()!=null){
				Expression volFractExp = ((MembraneMapping)sm).getVolumeFractionParameter().getExpression();
				try {
					if(volFractExp != null)
					{
						double volFract = volFractExp.evaluateConstant();
						if (volFract>=1.0){
							throw new MappingException("model structure '"+((MembraneMapping)sm).getMembrane().getInsideFeature().getName()+"' has volume fraction >= 1.0");
						}
					}
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
				}
			}
		}
		SubVolume subVolumes[] = getSimulationContext().getGeometryContext().getGeometry().getGeometrySpec().getSubVolumes();
		for (int i = 0; i < subVolumes.length; i++){
			if (getSimulationContext().getGeometryContext().getStructures(subVolumes[i])==null || getSimulationContext().getGeometryContext().getStructures(subVolumes[i]).length==0){
				throw new MappingException("geometry subVolume '"+subVolumes[i].getName()+"' not mapped from a model structure");
			}
		}
		
		//
		// gather only those reactionSteps that are not "excluded"
		//
		ReactionSpec reactionSpecs[] = getSimulationContext().getReactionContext().getReactionSpecs();
		Vector<ReactionStep> rsList = new Vector<ReactionStep>();
		for (int i = 0; i < reactionSpecs.length; i++){
			if (reactionSpecs[i].isExcluded()==false){
				rsList.add(reactionSpecs[i].getReactionStep());
			}
		}
		ReactionStep reactionSteps[] = new ReactionStep[rsList.size()];
		rsList.copyInto(reactionSteps);	
		
		//
		// fail if any unresolved parameters
		//
		for (int i = 0; i < reactionSteps.length; i++){
			Kinetics.UnresolvedParameter unresolvedParameters[] = reactionSteps[i].getKinetics().getUnresolvedParameters();
			if (unresolvedParameters!=null && unresolvedParameters.length>0){
				StringBuffer buffer = new StringBuffer();
				for (int j = 0; j < unresolvedParameters.length; j++){
					if (j>0){
						buffer.append(", ");
					}
					buffer.append(unresolvedParameters[j].getName());
				}
				throw new MappingException(reactionSteps[i].getTerm()+" '"+reactionSteps[i].getName()+"' contains unresolved identifier(s): "+buffer);
			}
		}
			
		//
		// create new MathDescription (based on simContext's previous MathDescription if possible)
		//
		MathDescription oldMathDesc = getSimulationContext().getMathDescription();
		mathDesc = null;
		if (oldMathDesc != null){
			if (oldMathDesc.getVersion() != null){
				mathDesc = new MathDescription(oldMathDesc.getVersion());
			}else{
				mathDesc = new MathDescription(oldMathDesc.getName());
			}
		}else{
			mathDesc = new MathDescription(getSimulationContext().getName()+"_generated");
		}

		//
		// temporarily place all variables in a hashtable (before binding) and discarding duplicates
		//
		VariableHash varHash = new VariableHash();
		
		//
		// conversion factors
		//
		varHash.addVariable(new Constant(ReservedSymbol.KMOLE.getName(),getIdentifierSubstitutions(ReservedSymbol.KMOLE.getExpression(),ReservedSymbol.KMOLE.getUnitDefinition(),null)));
		varHash.addVariable(new Constant(ReservedSymbol.N_PMOLE.getName(),getIdentifierSubstitutions(ReservedSymbol.N_PMOLE.getExpression(),ReservedSymbol.N_PMOLE.getUnitDefinition(),null)));
			
		Enumeration<SpeciesContextMapping> enum1 = getSpeciesContextMappings();
		while (enum1.hasMoreElements()){
			SpeciesContextMapping scm = enum1.nextElement();
			if (scm.getVariable() instanceof StochVolVariable){
				varHash.addVariable(scm.getVariable());
			}
		}

		//
		// add rate term for all reactions
		// add current source terms for each reaction step in a membrane
		//
		/*for (int i = 0; i < reactionSteps.length; i++){
			boolean bAllReactionParticipantsFixed = true;
			ReactionParticipant rp_Array[] = reactionSteps[i].getReactionParticipants();
			for (int j = 0; j < rp_Array.length; j++) {
				SpeciesContextSpec scs = getSimulationContext().getReactionContext().getSpeciesContextSpec(rp_Array[j].getSpeciesContext());
				if (!(rp_Array[j] instanceof Catalyst) && !scs.isConstant()){
					bAllReactionParticipantsFixed = false;  // found at least one reactionParticipant that is not fixed and needs this rate
				}
			}
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(reactionSteps[i].getStructure());
		}---don't think it's useful, isn't it?*/

		// deals with model parameters
		ModelParameter[] modelParameters = getSimulationContext().getModel().getModelParameters();
		for (int j=0;j<modelParameters.length;j++){
			Expression expr = getSubstitutedExpr(modelParameters[j].getExpression(), true, false);
			expr = getIdentifierSubstitutions(expr,modelParameters[j].getUnitDefinition(), null);
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(modelParameters[j], null), expr));
		}

		//
		// kinetic parameters (functions or constants)
		//
		for (int j=0;j<reactionSteps.length;j++){
			ReactionStep rs = reactionSteps[j];
			if (getSimulationContext().getReactionContext().getReactionSpec(rs).isExcluded()){
				continue;
			}
			if (rs.getKinetics() instanceof LumpedKinetics){
				throw new RuntimeException("Lumped Kinetics not yet supported for Stochastic Math Generation");
			}
			Kinetics.KineticsParameter parameters[] = rs.getKinetics().getKineticsParameters();
			StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(rs.getStructure());
			if (parameters != null){
				for (int i=0;i<parameters.length;i++){
					if ((parameters[i].getRole() == Kinetics.ROLE_CurrentDensity) && (parameters[i].getExpression()==null || parameters[i].getExpression().isZero())){
						continue;
					}
					//don't add rate, we'll do it later when creating the jump processes
					if (parameters[i].getRole() != Kinetics.ROLE_ReactionRate) {
						Expression expr = getSubstitutedExpr(parameters[i].getExpression(), true, false);
						varHash.addVariable(newFunctionOrConstant(getMathSymbol(parameters[i],sm), getIdentifierSubstitutions(expr,parameters[i].getUnitDefinition(),sm)));
					}
				}
			}
		}
		

		//geometic mapping
		//the parameter "Size" is already put into mathsymbolmapping in refreshSpeciesContextMapping()
		StructureMapping structureMappings[] = getSimulationContext().getGeometryContext().getStructureMappings();
		for (int i=0;i<structureMappings.length;i++){
			StructureMapping sm = structureMappings[i];
			StructureMapping.StructureMappingParameter parm = sm.getParameterFromRole(StructureMapping.ROLE_Size);
			if(parm.getExpression() != null)
			{
				try {
					double value = parm.getExpression().evaluateConstant();
					varHash.addVariable(new Constant(getMathSymbol0(parm,sm),new Expression(value)));
				}catch (ExpressionException e){
					//varHash.addVariable(new Function(getMathSymbol0(parm,sm),getIdentifierSubstitutions(parm.getExpression(),parm.getUnitDefinition(),sm)));
					e.printStackTrace(System.out);
					throw new MappingException("Size of structure:"+sm.getNameScope().getName()+" cannot be evaluated as constant.");
				}
			}
		}

		//
		// species initial values (either function or constant)
		//
		SpeciesContextSpec speciesContextSpecs[] = getSimulationContext().getReactionContext().getSpeciesContextSpecs();
		for (int i = 0; i < speciesContextSpecs.length; i++){
			SpeciesContextSpec.SpeciesContextSpecParameter initParam = null;//can be concentration or amount
			Expression iniExp = null;
			StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(speciesContextSpecs[i].getSpeciesContext().getStructure());
			if(speciesContextSpecs[i].getInitialConcentrationParameter() != null && speciesContextSpecs[i].getInitialConcentrationParameter().getExpression() != null)
			{//use concentration, need to set up amount functions
				initParam = speciesContextSpecs[i].getInitialConcentrationParameter();
				iniExp = initParam.getExpression();
				iniExp = getSubstitutedExpr(iniExp, true, !speciesContextSpecs[i].isConstant());
				// now create the appropriate function or Constant for the speciesContextSpec.
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(initParam,sm),getIdentifierSubstitutions(iniExp,initParam.getUnitDefinition(),sm)));

				//add function for initial amount
				SpeciesContextSpec.SpeciesContextSpecParameter initAmountParam = speciesContextSpecs[i].getInitialCountParameter();
				Expression 	iniAmountExp = getExpressionConcToAmt(new Expression(getNameScope().getSymbolName(initParam)),speciesContextSpecs[i].getSpeciesContext());
				iniAmountExp.bindExpression(this);
				varHash.addVariable(new Function(getMathSymbol(initAmountParam, sm),getIdentifierSubstitutions(iniAmountExp,initAmountParam.getUnitDefinition(),sm)));
			}
			else if(speciesContextSpecs[i].getInitialCountParameter() != null && speciesContextSpecs[i].getInitialCountParameter().getExpression() != null)
			{// use amount
				initParam = speciesContextSpecs[i].getInitialCountParameter();
				iniExp = initParam.getExpression();
				iniExp = getSubstitutedExpr(iniExp, false, !speciesContextSpecs[i].isConstant());
				// now create the appropriate function or Constant for the speciesContextSpec.
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(initParam,sm),getIdentifierSubstitutions(iniExp,initParam.getUnitDefinition(),sm)));
			}

			//add spConcentration (concentration of species) to varHash as function or constant
			SpeciesConcentrationParameter spConcParam = getSpeciesConcentrationParameter(speciesContextSpecs[i].getSpeciesContext());
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(spConcParam,sm),getIdentifierSubstitutions(spConcParam.getExpression(), spConcParam.getUnitDefinition(), sm)));

		}
		
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
		if (getSimulationContext().getGeometryContext().getGeometry() != null){
			try {
				mathDesc.setGeometry(getSimulationContext().getGeometryContext().getGeometry());
			}catch (java.beans.PropertyVetoException e){
				e.printStackTrace(System.out);
				throw new MappingException("failure setting geometry "+e.getMessage());
			}
		}else{
			throw new MappingException("geometry must be defined");
		}


		//
		// functions: species which is not a variable, but has dependency expression
		//
		enum1 = getSpeciesContextMappings();
		while (enum1.hasMoreElements()){
			SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
			if (scm.getVariable()==null && scm.getDependencyExpression()!=null){
				StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(scm.getSpeciesContext().getStructure());
				Expression exp = scm.getDependencyExpression();
				exp.bindExpression(this);
				SpeciesCountParameter spCountParam = getSpeciesCountParameter(scm.getSpeciesContext());
				varHash.addVariable(new Function(getMathSymbol(spCountParam,sm),getIdentifierSubstitutions(exp, VCUnitDefinition.UNIT_molecules, sm)));
			}
		}

		//
		// create subDomains
		//
		SubDomain subDomain = null;
		subVolumes = getSimulationContext().getGeometryContext().getGeometry().getGeometrySpec().getSubVolumes();
		for (int j=0;j<subVolumes.length;j++){
			SubVolume subVolume = (SubVolume)subVolumes[j];
			//
			// get priority of subDomain
			//
			int priority;
			Feature spatialFeature = getSimulationContext().getGeometryContext().getResolvedFeature(subVolume);
			if (spatialFeature==null){
				if (getSimulationContext().getGeometryContext().getGeometry().getDimension()>0){
					throw new MappingException("no compartment (in Physiology) is mapped to subdomain '"+subVolume.getName()+"' (in Geometry)");
				}else{
					priority = CompartmentSubDomain.NON_SPATIAL_PRIORITY;
				}
			}else{
				priority = spatialFeature.getPriority() * 100 + j; // now does not have to match spatial feature, *BUT* needs to be unique
			}
			
			subDomain = new CompartmentSubDomain(subVolume.getName(),priority);
			mathDesc.addSubDomain(subDomain);
		}
	
		// set up jump processes
		// get all the reactions from simulation context
		// ReactionSpec[] reactionSpecs = getSimulationContext().getReactionContext().getReactionSpecs();---need to take a look here!
		for (int i = 0; i < reactionSpecs.length; i++)
		{
			if (reactionSpecs[i].isExcluded()) {
				continue;
			}
						
			// get the reaction
			ReactionStep reactionStep = reactionSpecs[i].getReactionStep();
			Kinetics kinetics = reactionStep.getKinetics();
			// the structure where reaction happens
			StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(reactionStep.getStructure());
			//create symbol table for jump process based on reactionStep and structure mapping
			//final ReactionStep finalRS = reactionStep;
			//final StructureMapping finalSM = sm;
//			SymbolTable symTable = new SymbolTable(){
//				public SymbolTableEntry getEntry(String identifierString) throws ExpressionBindingException {
//					SymbolTableEntry ste = finalRS.getEntry(identifierString);
//				    if(ste == null)
//				    {
//				    	ste = finalSM.getEntry(identifierString);
//				    }
//					return ste;
//				}
//			};
			// Different ways to deal with simple reactions and flux reactions
			if(reactionStep instanceof SimpleReaction) // simple reactions
			{
				// check the reaction rate law to see if we need to decompose a reaction(reversible) into two jump processes.
				// rate constants are important in calculating the probability rate.
				// for Mass Action, we use KForward and KReverse, 
				// for General Kinetics we parse reaction rate J to see if it is in Mass Action form.
				Expression forwardRate = null;
				Expression reverseRate = null;
				if (kinetics.getKineticsDescription().equals(KineticsDescription.MassAction))
				{
					forwardRate = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KForward).getExpression();
					reverseRate = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KReverse).getExpression();
					
				}
				else if (kinetics.getKineticsDescription().equals(KineticsDescription.General))
				{
					Expression rateExp = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
					rateExp = substitueKineticPara(rateExp, reactionStep, false);
					MassActionSolver.MassActionFunction maFunc = MassActionSolver.solveMassAction(rateExp, reactionStep);
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
				/*else if (kinetics.getKineticsDescription().getName().compareTo(KineticsDescription.HMM_irreversible.getName())==0)
			    {
				    forwardRate = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_Km).getExpression();
				}
			    else if (kinetics.getKineticsDescription().getName().compareTo(KineticsDescription.HMM_reversible.getName())==0)
			    {
					forwardRate = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KmFwd).getExpression();
					reverseRate = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KmRev).getExpression();
				}*/
			    boolean isForwardRateNonZero = false;
			    boolean isReverseRateNonZero = false;
		       	if(forwardRate != null)
		    	{
		       		isForwardRateNonZero = true;
		       		try {
		    			if (forwardRate.evaluateConstant()==0)
		    				isForwardRateNonZero = false;
		    		} catch (ExpressionException e) {
		    		}	    		
		    	}
		    	
		    	if(reverseRate != null)
		    	{
		    		isReverseRateNonZero = true;
		    		try {
		    			if(reverseRate.evaluateConstant()==0)
		    				isReverseRateNonZero = false;
				    }catch(ExpressionException e){			    
				    }
		    	}
			    
				// if the reaction has forward rate (Mass action,HMMs), or don't have either forward or reverse rate (some other rate laws--like general)
				// we process it as forward reaction
				if ((isForwardRateNonZero) /*|| ((forwardRate == null) && (reverseRate == null))*/)
				{
					// get jump process name
					String jpName = org.vcell.util.TokenMangler.mangleToSName(reactionStep.getName());
					// get probability
					Expression exp = null;
									
					// reactions are mass actions
					exp = getProbabilityRate(reactionStep, true);
					// bind symbol table before substitute identifiers in the reaction step
					exp.bindExpression(this);
					
					MathMapping.ProbabilityParameter probParm = null;
					try{
						probParm = addProbabilityParameter("P_"+jpName, exp,MathMapping.PARAMETER_ROLE_P,VCUnitDefinition.UNIT_molecules_per_s,reactionSpecs[i]);
					}catch(PropertyVetoException pve){
						pve.printStackTrace();
						throw new MappingException(pve.getMessage());
					}
					
					//add probability to function or constant
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(probParm,sm),getIdentifierSubstitutions(exp, VCUnitDefinition.UNIT_molecules_per_s, sm)));
										
					JumpProcess jp = new JumpProcess(jpName,new Expression(getMathSymbol0(probParm,sm)));
					// actions
					ReactionParticipant[] reacPart = reactionStep.getReactionParticipants();
					for(int j=0; j<reacPart.length; j++)
					{
						Action action = null;
						SpeciesCountParameter spCountParam = getSpeciesCountParameter(reacPart[j].getSpeciesContext());
						if(reacPart[j] instanceof Reactant)
						{ 
							// check if the reactant is a constant. If the species is a constant, there will be no action taken on this species
							if(!getSimulationContext().getReactionContext().getSpeciesContextSpec(reacPart[j].getSpeciesContext()).isConstant()) // not a constant
							{
								int stoi = ((Reactant)reacPart[j]).getStoichiometry();
								action = new Action(varHash.getVariable(getMathSymbol(spCountParam, sm)),"inc", new Expression("-"+String.valueOf(stoi)));
								jp.addAction(action);
							}
						}
						else if(reacPart[j] instanceof Product)
						{
							// check if the product is a constant. If the product is a constant, there will be no action taken on this species
							if(!getSimulationContext().getReactionContext().getSpeciesContextSpec(reacPart[j].getSpeciesContext()).isConstant()) // not a constant
							{
								int stoi = ((Product)reacPart[j]).getStoichiometry();
								action = new Action(varHash.getVariable(getMathSymbol(spCountParam, sm)),"inc", new Expression(stoi));
								jp.addAction(action);
							}
						}
					}
					// add jump process to compartment subDomain
					subDomain.addJumpProcess(jp);
				}
				if (isReverseRateNonZero) // one more jump process for a reversible reaction
				{
					// get jump process name
					String jpName = org.vcell.util.TokenMangler.mangleToSName(reactionStep.getName())+"_reverse";
					Expression exp = null;
					
					// reactions are mass actions
					exp = getProbabilityRate(reactionStep, false);
					// bind symbol table before substitute identifiers in the reaction step
					exp.bindExpression(this);
					
					MathMapping.ProbabilityParameter probRevParm = null;
					try{
						probRevParm = addProbabilityParameter("P_"+jpName,exp,MathMapping.PARAMETER_ROLE_P_reverse,VCUnitDefinition.UNIT_molecules_per_s,reactionSpecs[i]);
					}catch(PropertyVetoException pve){
						pve.printStackTrace();
						throw new MappingException(pve.getMessage());
					}
					//add probability to function or constant
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(probRevParm,sm),getIdentifierSubstitutions(exp, VCUnitDefinition.UNIT_molecules_per_s, sm)));
									
					JumpProcess jp = new JumpProcess(jpName,new Expression(getMathSymbol0(probRevParm,sm)));
					// actions
					ReactionParticipant[] reacPart = reactionStep.getReactionParticipants();
					for(int j=0; j<reacPart.length; j++)
					{
						Action action = null;
						SpeciesCountParameter spCountParam = getSpeciesCountParameter(reacPart[j].getSpeciesContext());
						if(reacPart[j] instanceof Reactant)
						{ 
							// check if the reactant is a constant. If the species is a constant, there will be no action taken on this species
							if(!getSimulationContext().getReactionContext().getSpeciesContextSpec(reacPart[j].getSpeciesContext()).isConstant()) // not a constant
							{
								int stoi = ((Reactant)reacPart[j]).getStoichiometry();
								action = new Action(varHash.getVariable(getMathSymbol(spCountParam, sm)),"inc", new Expression(stoi));
								jp.addAction(action);
							}
						}
						else if(reacPart[j] instanceof Product)
						{
							// check if the product is a constant. If the product is a constant, there will be no action taken on this species
							if(!getSimulationContext().getReactionContext().getSpeciesContextSpec(reacPart[j].getSpeciesContext()).isConstant()) // not a constant
							{
								int stoi = ((Product)reacPart[j]).getStoichiometry();
								action = new Action(varHash.getVariable(getMathSymbol(spCountParam, sm)),"inc", new Expression("-"+String.valueOf(stoi)));
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
				if(kinetics.getKineticsDescription().equals(KineticsDescription.General))
				{
					Expression fluxRate = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
					fluxRate = substitueKineticPara(fluxRate, reactionStep, false);
					//we have to pass the math description para to flux solver, coz somehow math description in simulation context is not updated.
					FluxSolver.FluxFunction fluxFunc = FluxSolver.solveFlux(fluxRate, (FluxReaction)reactionStep);
					//create jump process for forward flux if it exists.
					if(fluxFunc.getRateToInside() != null && !fluxFunc.getRateToInside().isZero()) 
					{
						//jump process name
						String jpName = org.vcell.util.TokenMangler.mangleToSName(reactionStep.getName());//+"_reverse";
											
						//get probability function, probExp = fluxRate*fluxCarrier*Size_membrane*602
						//we do it here instead of fluxsolver, coz we need to use getMathSymbol0(), structuremapping...etc.
						Expression rate = fluxFunc.getRateToInside();
						Expression expr1 = Expression.mult(rate, new Expression(fluxFunc.getSpeciesContextOutside().getName()));
						Expression numeratorExpr = Expression.mult(expr1, new Expression(sm.getStructure().getStructureSize().getName()));
						Expression denominatorExpr = new Expression(ReservedSymbol.KMOLE.getName());
						Expression probExp = Expression.div(numeratorExpr, denominatorExpr);
						probExp.bindExpression(reactionStep);//bind symbol table before substitute identifiers in the reaction step
						
						MathMapping.ProbabilityParameter probParm = null;
						try{
							probParm = addProbabilityParameter("P_"+jpName,probExp,MathMapping.PARAMETER_ROLE_P,VCUnitDefinition.UNIT_molecules_per_s,reactionSpecs[i]);
						}catch(PropertyVetoException pve){
							pve.printStackTrace();
							throw new MappingException(pve.getMessage());
						}
						//add probability to function or constant
						varHash.addVariable(newFunctionOrConstant(getMathSymbol(probParm,sm),getIdentifierSubstitutions(probExp, VCUnitDefinition.UNIT_molecules_per_s, sm)));
										
						JumpProcess jp = new JumpProcess(jpName,new Expression(getMathSymbol0(probParm,sm)));
						// actions
						Action action = null;
						SpeciesContext sc = fluxFunc.getSpeciesContextOutside();
						SpeciesCountParameter spCountParam = getSpeciesCountParameter(sc);
						action = new Action(varHash.getVariable(getMathSymbol0(spCountParam, sm)),"inc", new Expression(-1));
						jp.addAction(action);
							
						sc = fluxFunc.getSpeciesContextInside();
						spCountParam = getSpeciesCountParameter(sc);
						action = new Action(varHash.getVariable(getMathSymbol0(spCountParam, sm)),"inc", new Expression(1));
						jp.addAction(action);
						
						subDomain.addJumpProcess(jp);
					}
					if(fluxFunc.getRateToOutside() != null && !fluxFunc.getRateToOutside().isZero()) 
					{
						//jump process name
						String jpName = org.vcell.util.TokenMangler.mangleToSName(reactionStep.getName())+"_reverse";
											
						//get probability function, probExp = fluxRate*fluxCarrier*Size_membrane*602
						Expression rate = fluxFunc.getRateToOutside();
						Expression expr1 = Expression.mult(rate, new Expression(fluxFunc.getSpeciesContextInside().getName()));
						Expression numeratorExpr = Expression.mult(expr1, new Expression(sm.getStructure().getStructureSize().getName()));
						Expression denominatorExpr = new Expression(ReservedSymbol.KMOLE.getName());
						Expression probRevExp = Expression.div(numeratorExpr, denominatorExpr);
						probRevExp.bindExpression(reactionStep);//bind symbol table before substitute identifiers in the reaction step
						
						MathMapping.ProbabilityParameter probRevParm = null;
						try{
							probRevParm = addProbabilityParameter("P_"+jpName,probRevExp,MathMapping.PARAMETER_ROLE_P_reverse,VCUnitDefinition.UNIT_molecules_per_s,reactionSpecs[i]);
						}catch(PropertyVetoException pve){
							pve.printStackTrace();
							throw new MappingException(pve.getMessage());
						}
						//add probability to function or constant
						varHash.addVariable(newFunctionOrConstant(getMathSymbol(probRevParm,sm),getIdentifierSubstitutions(probRevExp, VCUnitDefinition.UNIT_molecules_per_s, sm)));
										
						JumpProcess jp = new JumpProcess(jpName,new Expression(getMathSymbol0(probRevParm,sm)));
						// actions
						Action action = null;
						SpeciesContext sc = fluxFunc.getSpeciesContextOutside();
						SpeciesCountParameter spCountParam = getSpeciesCountParameter(sc);
						action = new Action(varHash.getVariable(getMathSymbol0(spCountParam, sm)),"inc", new Expression(1));
						jp.addAction(action);
							
						sc = fluxFunc.getSpeciesContextInside();
						spCountParam = getSpeciesCountParameter(sc);
						action = new Action(varHash.getVariable(getMathSymbol0(spCountParam, sm)),"inc", new Expression(-1));
						jp.addAction(action);
						
						subDomain.addJumpProcess(jp);
					}
				}
			}//end of if (simplereaction)...else if(fluxreaction)
		} // end of reaction step loop
			
		//
		// set Variables to MathDescription all at once with the order resolved by "VariableHash"
		//
		mathDesc.setAllVariables(varHash.getAlphabeticallyOrderedVariables());
		
		// set up variable initial conditions in subDomain
		SpeciesContextSpec scSpecs[] = getSimulationContext().getReactionContext().getSpeciesContextSpecs();
		for (int i = 0; i < speciesContextSpecs.length; i++){
			//get stochastic variable by name
			SpeciesCountParameter spCountParam = getSpeciesCountParameter(speciesContextSpecs[i].getSpeciesContext());
			StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(speciesContextSpecs[i].getSpeciesContext().getStructure());
			String varName = getMathSymbol(spCountParam, sm); 
			if (scSpecs[i].isConstant()) {
				continue;
			}
			StochVolVariable var = (StochVolVariable)mathDesc.getVariable(varName);
			SpeciesContextSpec.SpeciesContextSpecParameter initParm = scSpecs[i].getInitialCountParameter();//stochastic use initial number of particles
			//stochastic variables initial expression.
			if (initParm!=null){
				VarIniCondition varIni = new VarIniCondition(var,new Expression(getMathSymbol0(initParm, sm)));
				subDomain.addVarIniCondition(varIni);
			}
		}

/*
		for (int i = 0; i < speciesContexts.length; i++){
			StochVolVariable var = new StochVolVariable(speciesContexts[i].getName()); 
			// get the initial condition in concentration
			cbit.vcell.mapping.SpeciesContextSpec speciesContextsSpec = getSimulationContext().getReactionContext().getSpeciesContextSpec(speciesContexts[i]);
			try {
				double molConst = ReservedSymbol.N_PMOLE.getExpression().evaluateConstant() * 1e12; // particles/pMol -> particles/Mol
				long numParticles = 0;
				if (speciesContexts[i].getStructure() instanceof Membrane)
				{
					// convert concentration(particles/area) to number of particles
					double c = speciesContextsSpec.getInitialConditionParameter().getConstantValue() * 1e-6; // uMol/m2 -> Mol/m2
					double v = StructureMapping.getDefaultAbsoluteSize(); // L
					double membraneSurfVolRate = getSimulationContext().getGeometryContext().getStructureMapping(speciesContexts[i].getStructure()).getParameterFromRole(StructureMapping.ROLE_SurfaceToVolumeRatio).getConstantValue(); //TODO: find out the unit!!! must convert to m2/L
					numParticles = (long)(c*membraneSurfVolRate*v*molConst);
				}
				else
				{
					// convert concentration(particles/volumn) to number of particles
					double c = speciesContextsSpec.getInitialConditionParameter().getConstantValue() * 1e-6; // uMol/L -> Mol/L
					double v = StructureMapping.getDefaultAbsoluteSize(); // L
					numParticles = (long)(c*v*molConst);
				}
				VarIniCondition varIni = new VarIniCondition(var,new Expression(numParticles));
				mathDesc.addVariable(var);
				subDomain.addVarIniCondition(varIni);
			} catch (cbit.vcell.parser.ExpressionException e) {
				e.printStackTrace();
			}
		}*/

		if (!mathDesc.isValid()){
			throw new MappingException("generated an invalid mathDescription: "+mathDesc.getWarning());
		}
	}

/**
 * 
 * @param expr
 * @param bConcentration
 * @return
 * @throws ExpressionException
 */	
private Expression getSubstitutedExpr(Expression expr, boolean bConcentration, boolean bIsInitialCondn) throws ExpressionException {
	expr = new Expression(expr);
	String[] symbols = expr.getSymbols();
	// Check if 'expr' has other speciesContexts in its expression, need to replace it with 'spContext_init'
	for (int j = 0; symbols != null && j < symbols.length; j++) {
		// if symbol is a speciesContext, replacing it with a reference to initial condition for that speciesContext.
		SpeciesContext spC = null;
		SymbolTableEntry ste = expr.getSymbolBinding(symbols[j]);
		if (ste instanceof ProxyParameter) {
			// if expression is for speciesContextSpec or Kinetics, ste will be a ProxyParameter instance.
			ProxyParameter spspp = (ProxyParameter)ste;
			if (spspp.getTarget() instanceof SpeciesContext) {
				spC = (SpeciesContext)spspp.getTarget();
			}
		} else if (ste instanceof SpeciesContext) {
			// if expression is for a global parameter, ste will be a SpeciesContext instance. 
			spC = (SpeciesContext)ste;
		}
		if (spC != null) {
			SpeciesContextSpec spcspec = getSimulationContext().getReactionContext().getSpeciesContextSpec(spC);
			Parameter spCParm = null;
			if (bConcentration && bIsInitialCondn) {
				// speciesContext has initConcentration set, so need to replace 'spContext' in 'expr' 'spContext_init'
				spCParm = spcspec.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
			} else if (!bConcentration && bIsInitialCondn) {
				// speciesContext has initCount set, so need to replace 'spContext' in 'expr' 'spContext_initCount'
				spCParm = spcspec.getParameterFromRole(SpeciesContextSpec.ROLE_InitialCount);
			} else if (bConcentration && !bIsInitialCondn) {
				// need to replace 'spContext' in 'expr' 'spContext_Conc'
				spCParm = getSpeciesConcentrationParameter(spC);
			} else if (!bConcentration && !bIsInitialCondn) {
				// need to replace 'spContext' in 'expr' 'spContext_Count'
				spCParm = getSpeciesCountParameter(spC);
			}
			// need to get init condn expression, but can't get it from getMathSymbol() (mapping between bio and math), hence get it as below.
			Expression scsInitExpr = new Expression(getNameScope().getSymbolName(spCParm));
			scsInitExpr.bindExpression(this);
			expr.substituteInPlace(new Expression(spC.getName()), scsInitExpr);
		}
	}
	return expr;
}
	
	
/**
 * Insert the method's description here.
 * Creation date: (10/26/2006 11:47:26 AM)
 * @exception cbit.vcell.parser.ExpressionException The exception description.
 * @exception cbit.vcell.mapping.MappingException The exception description.
 * @exception cbit.vcell.math.MathException The exception description.
 */
private void refreshSpeciesContextMappings() throws cbit.vcell.parser.ExpressionException, MappingException, cbit.vcell.math.MathException 
{
	//
	// create a SpeciesContextMapping for each speciesContextSpec.
	//
	// set initialExpression from SpeciesContextSpec.
	// set diffusing5
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
		getMathSymbol(parm,sm);
	}

	
	getSpeciesContextMappingList().removeAllElements();
	
	SpeciesContextSpec speciesContextSpecs[] = getSimulationContext().getReactionContext().getSpeciesContextSpecs();
	for (int i=0;i<speciesContextSpecs.length;i++){
		SpeciesContextSpec scs = speciesContextSpecs[i];

		SpeciesContextMapping scm = new SpeciesContextMapping(scs.getSpeciesContext());
		scm.setPDERequired(isPDERequired(scs.getSpeciesContext()));
//		scm.setDiffusing(isDiffusionRequired(scs.getSpeciesContext()));
//		scm.setAdvecting(isAdvectionRequired(scs.getSpeciesContext()));
		if (scs.isConstant()){
			SpeciesContextSpec.SpeciesContextSpecParameter initCountParm = scs.getInitialCountParameter();
			SpeciesContextSpec.SpeciesContextSpecParameter initConcParm =  scs.getInitialConcentrationParameter();
			Expression initCondInCount = null;
			//initial condition is concentration
			if(initConcParm != null && initConcParm.getExpression() != null)
			{
				initCondInCount = getExpressionConcToAmt(new Expression(getNameScope().getSymbolName(initConcParm)),speciesContextSpecs[i].getSpeciesContext());
			}
			else
			{
				initCondInCount = new Expression(getNameScope().getSymbolName(initCountParm));
			}
			initCondInCount.bindExpression(this);
			initCondInCount = getSubstitutedExpr(initCondInCount, true, true);
			scm.setDependencyExpression(initCondInCount);
		}
		//
		// test if participant in fast reaction step, request elimination if possible
		//
		scm.setFastParticipant(false);
		ReactionSpec reactionSpecs[] = getSimulationContext().getReactionContext().getReactionSpecs();
		for (int j=0;j<reactionSpecs.length;j++){
			ReactionSpec reactionSpec = reactionSpecs[j];
			if (reactionSpec.isExcluded()){
				continue;
			}
			ReactionStep rs = reactionSpec.getReactionStep();
			if (rs instanceof SimpleReaction && rs.getReactionParticipants(scs.getSpeciesContext()).length > 0){
				if (reactionSpec.isFast()){
					scm.setFastParticipant(true);
				}
			}
		}
		getSpeciesContextMappingList().addElement(scm);
	}
}


/**
 * Map speciesContext to variable, used for structural analysis (slow reactions and fast reactions)
 * Creation date: (10/25/2006 8:59:43 AM)
 * @exception cbit.vcell.mapping.MappingException The exception description.
 */
private void refreshVariables() throws MappingException {
	//
	// non-constant dependant variables(means rely on other contants/functions) require a function
	//
	Enumeration<SpeciesContextMapping> enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = enum1.nextElement();
		SpeciesContextSpec scs = getSimulationContext().getReactionContext().getSpeciesContextSpec(scm.getSpeciesContext());
		if (scm.getDependencyExpression() != null && !scs.isConstant()){
			//scm.setVariable(new Function(scm.getSpeciesContext().getName(),scm.getDependencyExpression()));
			scm.setVariable(null);
		}
	}

	//
	// non-constant independant variables require either a membrane or volume variable
	//
	enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		SpeciesContextSpec scs = getSimulationContext().getReactionContext().getSpeciesContextSpec(scm.getSpeciesContext());
		//stochastic variable is always a function of size.
		MathMapping.SpeciesCountParameter spCountParm = null;
		try{
			String countName = scs.getSpeciesContext().getName() + BIO_PARAM_SUFFIX_SPECIES_COUNT;
			Expression countExp = new Expression(0.0);
			spCountParm = addSpeciesCountParameter(countName, countExp, MathMapping.PARAMETER_ROLE_COUNT, VCUnitDefinition.UNIT_molecules, scs);
		}catch(PropertyVetoException pve){
			pve.printStackTrace();
			throw new MappingException(pve.getMessage());
		}
		
		//add concentration of species as MathMappingParameter - this will map to species concentration function
		try{
			String concName = scs.getSpeciesContext().getName() + BIO_PARAM_SUFFIX_SPECIES_CONCENTRATION;
			Expression concExp = getExpressionAmtToConc(new Expression(spCountParm.getName()), scs.getSpeciesContext());
			concExp.bindExpression(this);
			addSpeciesConcentrationParameter(concName, concExp, MathMapping.PARAMETER_ROLE_CONCENRATION, scs.getSpeciesContext().getUnitDefinition(), scs);
		}catch(Exception e){
			e.printStackTrace();
			throw new MappingException(e.getMessage());
		}

		if (scm.getDependencyExpression() == null && !scs.isConstant()){
			scm.setVariable(new StochVolVariable(getMathSymbol(spCountParm, getSimulationContext().getGeometryContext().getStructureMapping(scs.getSpeciesContext().getStructure()))));
			mathSymbolMapping.put(scm.getSpeciesContext(),scm.getVariable().getName());
		}
	}
}

//substitute parameters with mathsymbol(e.g. if there is "a" para in reaction1, subsitute it to "a_reaction1" in reaction1 if there are "a"s in different reactions )
//this is useful for general law kinetics
//private Expression substitueKineticPara(Expression exp, ReactionStep rs, StructureMapping sm) throws MappingException, ExpressionException
//{
//	Expression result = new Expression(exp);
//	String symbols[] = result.getSymbols();
//	for (int k = 0;symbols!=null && k < symbols.length; k++){
//		Kinetics.KineticsParameter kp = rs.getKinetics().getKineticsParameter(symbols[k]);
//		if (kp != null)
//		{
//			try{
//				if( getMathSymbol0(kp,sm).compareTo(symbols[k]) !=0)
//				{
//					result.substituteInPlace(new Expression(symbols[k]), new Expression(getMathSymbol0(kp,sm)));	
//				}
//			}catch(ExpressionException e1){
//				e1.printStackTrace();
//				throw new ExpressionException(e1.getMessage());
//			}catch(MappingException e2){
//				e2.printStackTrace();
//				throw new MappingException("Erroe occurs when try to get math symbol for kinetic para:"+symbols[k]+".\n"+e2.getMessage());
//			}
//		    
//		}
//	}
//	return result;
//}

private Expression substitueKineticPara(Expression exp, ReactionStep rs, boolean substituteConst) throws MappingException, ExpressionException
{
	Expression result = new Expression(exp);
	boolean bSubstituted = true;
	while(bSubstituted)
	{
		bSubstituted = false;
		String symbols[] = result.getSymbols();
		for (int k = 0;symbols!=null && k < symbols.length; k++){
			Kinetics.KineticsParameter kp = rs.getKinetics().getKineticsParameter(symbols[k]);
			if (kp != null)
			{
				try{
					Expression expKP = substitueKineticPara(kp.getExpression(), rs, true);
					if(!expKP.flatten().isNumeric()||substituteConst)
					{
						result.substituteInPlace(new Expression(symbols[k]), new Expression(kp.getExpression()));
						bSubstituted = true;
					}
				}catch(ExpressionException e1){
					e1.printStackTrace();
					throw new ExpressionException(e1.getMessage());
				}
			}
		}
		
	}
	return result;
}

}
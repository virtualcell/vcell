package cbit.vcell.mapping;
import java.beans.PropertyVetoException;
import jscl.math.Generic;
import jscl.math.operator.Factorial;
import cbit.vcell.mapping.potential.VoltageClampElectricalDevice;
import cbit.vcell.mapping.potential.CurrentClampElectricalDevice;
import cbit.vcell.mapping.potential.MembraneElectricalDevice;
import cbit.vcell.mapping.potential.PotentialMapping;
import cbit.vcell.mapping.potential.ElectricalDevice;
import cbit.vcell.solver.stoch.FluxSolver;
import cbit.vcell.solver.stoch.MassActionSolver;
import cbit.vcell.units.VCUnitException;
import cbit.gui.DialogUtils;
import cbit.util.ISize;
import cbit.vcell.math.*;
import cbit.vcell.model.*;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.*;
import cbit.vcell.parser.*;
import java.util.*;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.model.Kinetics;
import cbit.vcell.math.Action;
import cbit.util.Issue;
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
 * Convert rate constant (k, in terms of concentration) to rate constant (c, in terms of number of particles)
 * Creation date: (10/13/2006 5:23:41 PM)
 * @return double
 * @param arg_reacPart cbit.vcell.model.ReactionParticipant[]
 */
private double concentrationRateToParticleRate(double rate_k, ReactionParticipant[] arg_reacPart) 
{
	int factorialStoi = 1; //multiply all the factorial of reactant's stoi together.
	int pCount = 0; // count total number of particles needed in a reaction 
	for(int i=0;i<arg_reacPart.length;i++)
	{
		if(arg_reacPart[i] instanceof Reactant)
		{
			factorialStoi = factorialStoi * (flanagan.math.Fmath.factorial(arg_reacPart[i].getStoichiometry()));
			pCount = pCount +arg_reacPart[i].getStoichiometry();
		}
		// when the sum of reactant's stois greater than 4, the possibility for the reaction to happen is nearly 0 (since the volue is 1e-15 Liter)
		// if the pCount is 4, the rate c is already at the level of (le-15) power -3. will be overflow soon.
		if (pCount > 4) break;
	}
	if (pCount <= 4)
	{
		return rate_k*factorialStoi/Math.pow( StructureMapping.getDefaultAbsoluteSize(),(pCount-1));
	}
	System.out.println("\n Impossible reaction! The sum of reactant's stoichiometry is "+pCount+".");
	return 0;
}


/**
 * Get initial particles from initial concentration. 
 * Creation date: (10/26/2006 4:40:25 PM)
 * @return cbit.vcell.parser.Expression,  initial particles expression
 * @param iniConcentration cbit.vcell.parser.Expression, initial concentration expression
 * @param speciesContext cbit.vcell.model.SpeciesContext
 */
public Expression getIniExpression(Expression iniConcentration, SpeciesContext speciesContext) 
{
	String iniParticles = "";//to create an expression for initial number of particles at last 

	StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(speciesContext.getStructure());
	StructureMapping.StructureMappingParameter parm = sm.getParameterFromRole(StructureMapping.ROLE_Size);
	
	if (speciesContext.getStructure() instanceof Membrane)
	{
		try{
			// convert concentration(particles/area) to number of particles
			iniParticles = iniConcentration.infix() +"*"+ getMathSymbol0(parm, sm); // particles = iniConcentration(molecues/um2)*size(um2)
		}catch (MappingException ex) {ex.printStackTrace();}
		
	}
	else
	{

		try{
			// convert concentration(particles/volumn) to number of particles
			// particles = 1e-9*iniConcentration(uM)*size(um3)*N_pmole
			iniParticles = "1e-9*"+iniConcentration.infix()+"*"+ getMathSymbol0(parm, sm) + "*" + ReservedSymbol.N_PMOLE.getName();
		}catch (MappingException e) {e.printStackTrace();}
	}
	try
	{
		
		return new Expression(iniParticles);
	}catch (ExpressionException e) {e.printStackTrace();}
	
	return null;
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
	String rateConstant = ""; //to compose the probability expression
	String reacPosibility = ""; //to compose the probability expression
	//the structure where reaction happens
	StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(rs.getStructure());
	if(isForwardDirection) // forward reaction
	{
		//get the reaction rate constant and convert it to rate of Number of particles
		//for HMMs, it's a bit complicated. Vmax/(Km+s)-->Vmaz*Size_s/(Km*Size_s+Ns)
		
		if (kinetics.getKineticsDescription().equals(KineticsDescription.MassAction))
		{
			rateConstant = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KForward).getName();
			//rateConstant = getMathSymbol0(kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KForward), sm);????????????
			//System.out.println("kinetic constant name scope:"+kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KForward).getNameScope());
		}
		/*else if (kinetics.getKineticsDescription().getName().compareTo(KineticsDescription.HMM_irreversible.getName())==0)
	    {
			rateConstant = getMathSymbol0(kinetics.getKineticsParameterFromRole(Kinetics.ROLE_Vmax), sm);
			ReactionParticipant[] tempart = reactionStep.getReactionParticipants();
			for(int k =0; k<tempart.length; k++)
			{
				if(tempart[k] instanceof Reactant)
				{
					StructureMapping reactSM = getSimulationContext().getGeometryContext().getStructureMapping(tempart[k].getStructure());
					//rateconstant=Vmax/(Km+s)=Vmax*Size_s/(Km*Size_s+Ns)
					rateConstant = rateConstant+"*"+getMathSymbol0(reactSM.getParameterFromRole(StructureMapping.ROLE_Size),reactSM)+"/("+getMathSymbol0(kinetics.getKineticsParameterFromRole(Kinetics.ROLE_Km), sm)+"*"+getMathSymbol0(reactSM.getParameterFromRole(StructureMapping.ROLE_Size),reactSM)+"+"+tempart[k].getSpeciesContext().getName()+")";
					break; //there is only one reactant in HMM reactions
				}
			}
		}
	    else if (kinetics.getKineticsDescription().getName().compareTo(KineticsDescription.HMM_reversible.getName())==0)
	    {
			rateConstant = getMathSymbol0(kinetics.getKineticsParameterFromRole(Kinetics.ROLE_VmaxFwd), sm);
			ReactionParticipant[] tempart = reactionStep.getReactionParticipants();
			for(int k =0; k<tempart.length; k++)
			{
				if(tempart[k] instanceof Reactant)
				{
					StructureMapping reactSM = getSimulationContext().getGeometryContext().getStructureMapping(tempart[k].getStructure());
					//rateconstant=Vmax/(Km+s)=Vmax*Size_s/(Km*Size_s+Ns)
					rateConstant = rateConstant+"*"+getMathSymbol0(reactSM.getParameterFromRole(StructureMapping.ROLE_Size),reactSM)+"/("+getMathSymbol0(kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KmFwd), sm)+"*"+getMathSymbol0(reactSM.getParameterFromRole(StructureMapping.ROLE_Size),reactSM)+"+"+tempart[k].getSpeciesContext().getName()+")";
					break; //there is only one reactant in HMM reactions
				}
			}
		}
	    else if (kinetics.getKineticsDescription().getName().compareTo(KineticsDescription.General.getName())==0)
	    {	
		    rateConstant = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_Rate).getExpression().infix();
		}*/
	    // count the structure size from the product side(location where the reaction happens actually). rateConstant*(feature_size*KMole OR membrane_Size)
	    if(sm.getStructure() instanceof Membrane)
	    {
			rateConstant = rateConstant + "*" + sm.getParameterFromRole(StructureMapping.ROLE_Size).getName();
	    }
	    else
	    {
		    rateConstant = rateConstant + "*" + sm.getParameterFromRole(StructureMapping.ROLE_Size).getName() + "/" + ReservedSymbol.KMOLE.getName();
		}
		
		
		//complete the probability expression by the reactants' stoichiometries if it is Mass Action rate law
		if(kinetics.getKineticsDescription().equals(KineticsDescription.MassAction))
		{
			ReactionParticipant[] reacPart = reactionStep.getReactionParticipants();
			
			for (int i=0; i<reacPart.length; i++)
			{
				int stoi = 0;
				if(reacPart[i] instanceof Reactant) 
				{ 
					stoi = ((Reactant)reacPart[i]).getStoichiometry();
					//******the following part is to form the s*(s-1)(s-2)..(s-stoi+1).portion of the probability rate.
					StructureMapping reactSM = getSimulationContext().getGeometryContext().getStructureMapping(reacPart[i].getStructure());
					String reactStructureSize ="";
					//convert speceis' unit from moles/liter to molecues.
					if(reactSM.getStructure() instanceof Membrane)
					{
						reactStructureSize = "1/" + reactSM.getParameterFromRole(StructureMapping.ROLE_Size).getName();
					}
					else
					{
						reactStructureSize = ReservedSymbol.KMOLE.getName() + "/" + reactSM.getParameterFromRole(StructureMapping.ROLE_Size).getName();
					}
					//s*(s-1)(s-2)..(s-stoi+1)
					for(int j=0; j<stoi;j++)
					{
						if(j==0)
						{
							reacPosibility = reacPosibility + ((Reactant)reacPart[i]).getSpeciesContext().getName();
						}
						else
						{
							reacPosibility = reacPosibility + "(" +((Reactant)reacPart[i]).getSpeciesContext().getName()+"-"+j+")";
						}
						if(j<(stoi-1)) reacPosibility = reacPosibility + "*";	
					}

					if(stoi == 1)
						reacPosibility = reacPosibility + "*" + reactStructureSize;
					else if (stoi > 1)
						reacPosibility = reacPosibility + "*" + "pow(" + reactStructureSize + "," + stoi + ")";
					reacPosibility = reacPosibility + "*";
				}
			}
			int len = reacPosibility.length();
			if(len > 0) reacPosibility = reacPosibility.substring(0,(len-1));
		}
	}
	else // reverse reaction
	{
		
		if (kinetics.getKineticsDescription().equals(KineticsDescription.MassAction))
		{
			rateConstant = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KReverse).getName();
		}
		/*else if (kinetics.getKineticsDescription().getName().compareTo(KineticsDescription.HMM_reversible.getName())==0)
	    {
			rateConstant = getMathSymbol0(kinetics.getKineticsParameterFromRole(Kinetics.ROLE_VmaxRev), sm);
			ReactionParticipant[] tempart = reactionStep.getReactionParticipants();
			for(int k =0; k<tempart.length; k++)
			{
				if(tempart[k] instanceof Product)
				{
					StructureMapping reactSM = getSimulationContext().getGeometryContext().getStructureMapping(tempart[k].getStructure());
					//rateconstant=Vmax/(Km+s)=Vmax*Size_s/(Km*Size_s+Ns)
					rateConstant = rateConstant+"*"+getMathSymbol0(reactSM.getParameterFromRole(StructureMapping.ROLE_Size),reactSM)+"/("+getMathSymbol0(kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KmRev), sm)+"*"+getMathSymbol0(reactSM.getParameterFromRole(StructureMapping.ROLE_Size),reactSM)+"+"+tempart[k].getSpeciesContext().getName()+")";
					break; //there is only one reactant in HMM reactions
				}
			}
		}*/
	    // count the structure size from the product side(location where the reaction happens actually). rateConstant*(feature_size*KMole OR membrane_Size) 
	    if(sm.getStructure() instanceof Membrane)
	    {
			rateConstant = rateConstant + "*" + sm.getParameterFromRole(StructureMapping.ROLE_Size).getName();
	    }
	    else
	    {
		    rateConstant = rateConstant + "*" + sm.getParameterFromRole(StructureMapping.ROLE_Size).getName() + "/" + ReservedSymbol.KMOLE.getName();
		}
				
			
		//complete the rest part of the probability expression by the products' stoichiometries.
		if(kinetics.getKineticsDescription().equals(KineticsDescription.MassAction))
		{
			ReactionParticipant[] reacPart = reactionStep.getReactionParticipants();
			
			for (int i=0; i<reacPart.length; i++)
			{
				int stoi = 0;
				if(reacPart[i] instanceof Product) 
				{ 
					stoi = ((Product)reacPart[i]).getStoichiometry();
					//******the following part is to form the s*(s-1)*(s-2)...(s-stoi+1).portion of the probability rate.
					StructureMapping reactSM = getSimulationContext().getGeometryContext().getStructureMapping(reacPart[i].getStructure());
					String reactStructureSize ="";
					//convert speceis' unit from moles/liter to molecues. 
					if(reactSM.getStructure() instanceof Membrane)
					{
						reactStructureSize = "1/" + reactSM.getParameterFromRole(StructureMapping.ROLE_Size).getName();
					}
					else
					{
						reactStructureSize = ReservedSymbol.KMOLE.getName() + "/" + reactSM.getParameterFromRole(StructureMapping.ROLE_Size).getName();
					}
					//s*(s-1)*(s-2)...(s-stoi+1)
					for(int j=0; j<stoi;j++)
					{
						if (j == 0)
						{
							reacPosibility = reacPosibility + ((Product)reacPart[i]).getSpeciesContext().getName();
						}
						else
						{
							reacPosibility = reacPosibility + "(" +((Product)reacPart[i]).getSpeciesContext().getName()+"-"+j+")";
						}
						if(j<(stoi-1)) reacPosibility = reacPosibility + "*";	
					}
					if(stoi == 1)
						reacPosibility = reacPosibility + "*" + reactStructureSize;
					else if (stoi > 1)
						reacPosibility = reacPosibility + "*" + "pow(" + reactStructureSize + "," + stoi + ")";
					reacPosibility = reacPosibility + "*";
				}
			}
			int len = reacPosibility.length();
			if(len > 0) reacPosibility = reacPosibility.substring(0,(len-1));
		}
	}
	try
	{
		if(rateConstant.length() == 0)
		{
			throw new MappingException("Can not find reaction rate constant in reaction: "+ reactionStep.getName());
		}
		else if(reacPosibility.length() == 0)
	    {
		 	probExp = new Expression(rateConstant);   
		}
	    else if((rateConstant.length() > 0) && (reacPosibility.length() > 0))
	    {
			probExp = new Expression(rateConstant+"*"+reacPosibility);
	    }
		
		//if it is general, substitute the parameter with mathsymbol and reaction participants s with s/Size-s
		/*if(kinetics.getKineticsDescription().getName().compareTo(KineticsDescription.General.getName())==0)
		{
			ReactionParticipant[] tempart = reactionStep.getReactionParticipants();
			//substitute parameters with mathsymbol(e.g. if there is "a" para in reaction1, subsitute it to "a_reaction1" in reaction1 if there are "a"s in different reactions )
			String symbols[] = probExp.getSymbols();
			for (int k = 0;symbols!=null && k < symbols.length; k++){
				Kinetics.KineticsParameter kp = reactionStep.getKinetics().getKineticsParameter(symbols[k]);
				if (kp != null)
				{
					if( getMathSymbol0(kp,sm).compareTo(symbols[k]) !=0)
					{
						probExp.substituteInPlace(new Expression(symbols[k]), new Expression(getMathSymbol0(kp,sm)));	
					}
				}
			}
					
			//substitute species s with s/Size_s
			for(int k =0; k<tempart.length; k++)
			{
				Expression orgExp = new Expression(tempart[k].getSpeciesContext().getName());
				StructureMapping reactSM = getSimulationContext().getGeometryContext().getStructureMapping(tempart[k].getStructure());
				String reactStructureSize ="";
				try
				{
					reactStructureSize = getMathSymbol0(reactSM.getParameterFromRole(StructureMapping.ROLE_Size),reactSM);
				}catch(MappingException e) {e.printStackTrace();}
				Expression newExp = new Expression(tempart[k].getSpeciesContext().getName()+"/"+reactStructureSize);
				probExp.substituteInPlace(orgExp, newExp);
			}
		}*/
		//flatten the expression
		probExp.flatten();
	}catch (ExpressionException e){e.printStackTrace();}
	return probExp;
}


	/**
	 * Basicly the function clears the error list and calls to get a new mathdescription.
	 */
	private void refresh() throws MappingException, ExpressionException, cbit.vcell.matrix.MatrixException, MathException, ModelException{
		issueList.clear();
		//refreshKFluxParameters();
		
		refreshSpeciesContextMappings();
		//refreshStructureAnalyzers();
		refreshVariables();
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
		Vector rsList = new Vector();
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
		
		Enumeration enum1 = getSpeciesContextMappings();
		while (enum1.hasMoreElements()){
			SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
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

		//
		// kinetic constants that evaluate to constants
		//
		for (int j=0;j<reactionSteps.length;j++){
			ReactionStep rs = reactionSteps[j];
			Kinetics.KineticsParameter parameters[] = rs.getKinetics().getKineticsParameters();
			if (rs.getKinetics() instanceof LumpedKinetics){
				throw new RuntimeException("Lumped Kinetics not yet supported for Stochastic Math Generation");
			}
			if (parameters != null){
				for (int i=0;i<parameters.length;i++){
					if (parameters[i].getRole() == Kinetics.ROLE_CurrentDensity && (parameters[i].getExpression()==null || parameters[i].getExpression().isZero())){
						continue;
					}
					//don't add rate, we'll do it later when creating the jump processes
					if (parameters[i].getRole() != Kinetics.ROLE_ReactionRate)
					{
						try {
							double value = parameters[i].getExpression().evaluateConstant();
							Constant constant = new Constant(getMathSymbol(parameters[i],null),new Expression(value));
							varHash.addVariable(constant);
						}catch (ExpressionException e){}
					}
				}
			}
		}

		//
		// kinetic constants that are functions of other constants and maybe variables 
		//
		for (int j=0;j<reactionSteps.length;j++){
			ReactionStep rs = reactionSteps[j];
			
			Kinetics.KineticsParameter parameters[] = rs.getKinetics().getKineticsParameters();
			if (rs.getKinetics() instanceof LumpedKinetics){
				throw new RuntimeException("Lumped Kinetics not yet supported for Stochastic Math Generation");
			}
			if (parameters != null){
				for (int i=0;i<parameters.length;i++){
					if (parameters[i].getRole() == Kinetics.ROLE_CurrentDensity && (parameters[i].getExpression()==null || parameters[i].getExpression().isZero())){
						continue;
					}
					if (parameters[i].getRole() == Kinetics.ROLE_ReactionRate && reactionSteps[j].getPhysicsOptions()==ReactionStep.PHYSICS_ELECTRICAL_ONLY){
						continue;
					}
					//don't add rate, we'll do it later when creating the jump processes
					if(parameters[i].getRole() != Kinetics.ROLE_ReactionRate)
					{	
						try {
							parameters[i].getExpression().evaluateConstant();
						}catch (ExpressionException e){
							StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(rs.getStructure());
							Expression exp = getIdentifierSubstitutions(parameters[i].getExpression(),parameters[i].getUnitDefinition(),sm);
							Function function = new Function(getMathSymbol(parameters[i],null),exp);
							varHash.addVariable(function);
						}
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
			SpeciesContextSpec.SpeciesContextSpecParameter initParm = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
			Expression iniExp = getIniExpression(initParm.getExpression(),speciesContextSpecs[i].getSpeciesContext());
			//stochastic variables 
			if (initParm!=null)
			{
				try {
					double value = iniExp.evaluateConstant();
					varHash.addVariable(new Constant(getMathSymbol(initParm,null),new Expression(value)));
				}catch (ExpressionException e){
					varHash.addVariable(new Function(getMathSymbol(initParm,null),iniExp));
				}						
			}
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
		// conversion factors
		//
		varHash.addVariable(new Constant(ReservedSymbol.KMOLE.getName(),getIdentifierSubstitutions(ReservedSymbol.KMOLE.getExpression(),ReservedSymbol.KMOLE.getUnitDefinition(),null)));
		varHash.addVariable(new Constant(ReservedSymbol.N_PMOLE.getName(),getIdentifierSubstitutions(ReservedSymbol.N_PMOLE.getExpression(),ReservedSymbol.N_PMOLE.getUnitDefinition(),null)));
						
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
				varHash.addVariable(new Function(getMathSymbol(scm.getSpeciesContext(),sm),exp));
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
			final ReactionStep finalRS = reactionStep;
			final StructureMapping finalSM = sm;
			SymbolTable symTable = new SymbolTable(){
				public SymbolTableEntry getEntry(String identifierString) throws ExpressionBindingException {
					SymbolTableEntry ste = finalRS.getEntry(identifierString);
				    if(ste == null)
				    {
				    	ste = finalSM.getEntry(identifierString);
				    }
					return ste;
				}
			};
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
					String jpName = cbit.util.TokenMangler.mangleToSName(reactionStep.getName());
					// get probability
					Expression exp = null;
									
					// reactions are mass actions
					exp = getProbabilityRate(reactionStep, true);
					// bind symbol table before substitute identifiers in the reaction step
					exp.bindExpression(symTable);
					
					MathMapping.ProbabilityParameter probParm = null;
					try{
						probParm = addProbabilityParameter("P_"+jpName,exp,MathMapping.PARAMETER_ROLE_P,VCUnitDefinition.UNIT_per_s,reactionSpecs[i]);
					}catch(PropertyVetoException pve){
						pve.printStackTrace();
						throw new MappingException(pve.getMessage());
					}
					
					//add probability to function or constant
					try {
						double value = exp.evaluateConstant();
						varHash.addVariable(new Constant(getMathSymbol(probParm,sm),new Expression(value)));
					}catch (ExpressionException e){
						varHash.addVariable(new Function(getMathSymbol(probParm,sm),getIdentifierSubstitutions(exp, VCUnitDefinition.UNIT_per_s, sm)));
					}
										
					JumpProcess jp = new JumpProcess(jpName,new Expression(getMathSymbol0(probParm,sm)));
					// actions
					ReactionParticipant[] reacPart = reactionStep.getReactionParticipants();
					for(int j=0; j<reacPart.length; j++)
					{
						Action action = null;
						if(reacPart[j] instanceof Reactant)
						{ 
							// check if the reactant is a constant. If the species is a constant, there will be no action taken on this species
							if(!getSimulationContext().getReactionContext().getSpeciesContextSpec(reacPart[j].getSpeciesContext()).isConstant()) // not a constant
							{
								int stoi = ((Reactant)reacPart[j]).getStoichiometry();
								action = new Action(varHash.getVariable( reacPart[j].getSpeciesContext().getName()),"inc", new Expression("-"+String.valueOf(stoi)));
								jp.addAction(action);
							}
						}
						else if(reacPart[j] instanceof Product)
						{
							// check if the product is a constant. If the product is a constant, there will be no action taken on this species
							if(!getSimulationContext().getReactionContext().getSpeciesContextSpec(reacPart[j].getSpeciesContext()).isConstant()) // not a constant
							{
								int stoi = ((Product)reacPart[j]).getStoichiometry();
								action = new Action(varHash.getVariable( reacPart[j].getSpeciesContext().getName()),"inc", new Expression(stoi));
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
					String jpName = cbit.util.TokenMangler.mangleToSName(reactionStep.getName())+"_reverse";
					Expression exp = null;
					
					// reactions are mass actions
					exp = getProbabilityRate(reactionStep, false);
					// bind symbol table before substitute identifiers in the reaction step
					exp.bindExpression(symTable);
					
					MathMapping.ProbabilityParameter probRevParm = null;
					try{
						probRevParm = addProbabilityParameter("P_"+jpName,exp,MathMapping.PARAMETER_ROLE_P_reverse,VCUnitDefinition.UNIT_per_s,reactionSpecs[i]);
					}catch(PropertyVetoException pve){
						pve.printStackTrace();
						throw new MappingException(pve.getMessage());
					}
					//add probability to function or constant
					try {
						double value = exp.evaluateConstant();
						varHash.addVariable(new Constant(getMathSymbol(probRevParm,sm),new Expression(value)));
					}catch (ExpressionException e){
						varHash.addVariable(new Function(getMathSymbol(probRevParm,sm),getIdentifierSubstitutions(exp, VCUnitDefinition.UNIT_per_s, sm)));
					}
									
					JumpProcess jp = new JumpProcess(jpName,new Expression(getMathSymbol0(probRevParm,sm)));
					// actions
					ReactionParticipant[] reacPart = reactionStep.getReactionParticipants();
					for(int j=0; j<reacPart.length; j++)
					{
						Action action = null;
						if(reacPart[j] instanceof Reactant)
						{ 
							// check if the reactant is a constant. If the species is a constant, there will be no action taken on this species
							if(!getSimulationContext().getReactionContext().getSpeciesContextSpec(reacPart[j].getSpeciesContext()).isConstant()) // not a constant
							{
								int stoi = ((Reactant)reacPart[j]).getStoichiometry();
								action = new Action(varHash.getVariable( reacPart[j].getSpeciesContext().getName()),"inc", new Expression(stoi));
								jp.addAction(action);
							}
						}
						else if(reacPart[j] instanceof Product)
						{
							// check if the product is a constant. If the product is a constant, there will be no action taken on this species
							if(!getSimulationContext().getReactionContext().getSpeciesContextSpec(reacPart[j].getSpeciesContext()).isConstant()) // not a constant
							{
								int stoi = ((Product)reacPart[j]).getStoichiometry();
								action = new Action(varHash.getVariable( reacPart[j].getSpeciesContext().getName()),"inc", new Expression("-"+String.valueOf(stoi)));
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
					FluxSolver.FluxFunction fluxFunc = FluxSolver.solveFlux(fluxRate, reactionStep);
					//create jump process for forward flux if it exists.
					if(fluxFunc.getRateToInside() != null && !fluxFunc.getRateToInside().isZero()) 
					{
						//jump process name
						String jpName = cbit.util.TokenMangler.mangleToSName(reactionStep.getName());//+"_reverse";
											
						//get probability function, probExp = fluxRate*fluxCarrier*Size_membrane*602
						//we do it here instead of fluxsolver, coz we need to use getMathSymbol0(), structuremapping...etc.
						Expression rate = fluxFunc.getRateToInside();
						String probExpStr = rate.infix() + "*" + fluxFunc.getSpeciesOutside() + "*" + sm.getParameterFromRole(StructureMapping.ROLE_Size).getName();
						probExpStr = probExpStr + "/" + ReservedSymbol.KMOLE.getName();
						Expression probExp = new Expression(probExpStr);
						probExp.bindExpression(symTable);//bind symbol table before substitute identifiers in the reaction step
						
						MathMapping.ProbabilityParameter probParm = null;
						try{
							probParm = addProbabilityParameter("P_"+jpName,probExp,MathMapping.PARAMETER_ROLE_P,VCUnitDefinition.UNIT_molecules_per_s,reactionSpecs[i]);
						}catch(PropertyVetoException pve){
							pve.printStackTrace();
							throw new MappingException(pve.getMessage());
						}
						//add probability to function or constant
						try {
							double value = probExp.evaluateConstant();
							varHash.addVariable(new Constant(getMathSymbol(probParm,sm),new Expression(value)));
						}catch (ExpressionException e){
							varHash.addVariable(new Function(getMathSymbol(probParm,sm),getIdentifierSubstitutions(probExp, VCUnitDefinition.UNIT_per_s, sm)));
						}
										
						JumpProcess jp = new JumpProcess(jpName,new Expression(getMathSymbol0(probParm,sm)));
						// actions
						Action action = null;
						action = new Action(varHash.getVariable(fluxFunc.getSpeciesOutside()),"inc", new Expression(-1));
						jp.addAction(action);
							
						action = new Action(varHash.getVariable(fluxFunc.getSpeciesInside()),"inc", new Expression(1));
						jp.addAction(action);
						
						subDomain.addJumpProcess(jp);
					}
					if(fluxFunc.getRateToOutside() != null && !fluxFunc.getRateToOutside().isZero()) 
					{
						//jump process name
						String jpName = cbit.util.TokenMangler.mangleToSName(reactionStep.getName())+"_reverse";
											
						//get probability function, probExp = fluxRate*fluxCarrier*Size_membrane*602
						Expression rate = fluxFunc.getRateToOutside();
						String probRevExpStr = rate.infix() + "*" + fluxFunc.getSpeciesInside() + "*" + sm.getParameterFromRole(StructureMapping.ROLE_Size).getName();
						probRevExpStr = probRevExpStr + "/" + ReservedSymbol.KMOLE.getName();
						Expression probRevExp = new Expression(probRevExpStr);
						probRevExp.bindExpression(symTable);//bind symbol table before substitute identifiers in the reaction step
						
						MathMapping.ProbabilityParameter probRevParm = null;
						try{
							probRevParm = addProbabilityParameter("P_"+jpName,probRevExp,MathMapping.PARAMETER_ROLE_P_reverse,VCUnitDefinition.UNIT_molecules_per_s,reactionSpecs[i]);
						}catch(PropertyVetoException pve){
							pve.printStackTrace();
							throw new MappingException(pve.getMessage());
						}
						//add probability to function or constant
						try {
							double value = probRevExp.evaluateConstant();
							varHash.addVariable(new Constant(getMathSymbol(probRevParm,sm),new Expression(value)));
						}catch (ExpressionException e){
							varHash.addVariable(new Function(getMathSymbol(probRevParm,sm),getIdentifierSubstitutions(probRevExp, VCUnitDefinition.UNIT_per_s, sm)));
						}
										
						JumpProcess jp = new JumpProcess(jpName,new Expression(getMathSymbol0(probRevParm,sm)));
						// actions
						Action action = null;
						action = new Action(varHash.getVariable(fluxFunc.getSpeciesOutside()),"inc", new Expression(1));
						jp.addAction(action);
							
						action = new Action(varHash.getVariable(fluxFunc.getSpeciesInside()),"inc", new Expression(-1));
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
			String varName = scSpecs[i].getSpeciesContext().getName();
			if (scSpecs[i].isConstant()) {
				continue;
			}
			StochVolVariable var = (StochVolVariable)mathDesc.getVariable(varName);
			SpeciesContextSpec.SpeciesContextSpecParameter initParm = scSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
			//stochastic variables initial expression.
			if (initParm!=null){
				//check if initial condition is constant in shochastic.
				Expression iniExp = getIniExpression(initParm.getExpression(),scSpecs[i].getSpeciesContext());
				try{
					iniExp.bindExpression(getMathDescription());
					iniExp.flatten().evaluateConstant();
				}catch (ExpressionException e)
				{
					e.printStackTrace();
					throw new MathFormatException("variable "+ varName +"'s initial condition is required to be a constant.");
				}
				
				VarIniCondition varIni = new VarIniCondition(var,new Expression(getMathSymbol0(initParm, null)));
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

		scm.setDiffusing(isDiffusionRequired(scs.getSpeciesContext()));
		if (scs.isConstant()){
			
			SpeciesContextSpec.SpeciesContextSpecParameter initParm = scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
			//Expression initCond = getIniExpression(scs.getInitialConditionParameter().getExpression(), scs.getSpeciesContext());
			Expression initCond = new Expression(getMathSymbol0(initParm,null));
			scm.setDependencyExpression(initCond);
			////
			//// determine if a Function is necessary
			////
			//boolean bNeedFunction = false;
			//if (initCond.getSymbols()!=null){
				//bNeedFunction = true;
			//}
			//if (bNeedFunction){
				//scm.setVariable(new Function(scm.getSpeciesContext().getName(),initCond));
			//}else{
				//scm.setVariable(new Constant(scm.getSpeciesContext().getName(),initCond));
			//}
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
private void refreshVariables() throws MappingException 
{
	//
	// non-constant dependant variables(means rely on other contants/functions) require a function
	//
	Enumeration enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
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
		if (scm.getDependencyExpression() == null && !scs.isConstant()){
			scm.setVariable(new StochVolVariable(scm.getSpeciesContext().getName()));
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
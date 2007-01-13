package cbit.vcell.mapping;
import cbit.vcell.math.*;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.*;
import cbit.vcell.geometry.*;

import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;

import cbit.vcell.model.Kinetics;
import cbit.vcell.modelapp.FeatureMapping;
import cbit.vcell.modelapp.MembraneMapping;
import cbit.vcell.modelapp.ReactionSpec;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modelapp.SpeciesContextSpec;
import cbit.vcell.modelapp.StructureMapping;
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
 * This method returns the mathDeac if it is existing, otherwise it creates a mathDescription and returns it.
 * @return cbit.vcell.math.MathDescription
 */
public MathDescription getMathDescription() throws MathException {
	if (mathDesc==null){
		try {
			refresh();
		} catch (MatrixException e) {
			e.printStackTrace();
			throw new MathException(e.getMessage());
		} catch (MappingException e) {
			e.printStackTrace();
			throw new MathException(e.getMessage());
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new MathException(e.getMessage());
		} catch (ModelException e) {
			e.printStackTrace();
			throw new MathException(e.getMessage());
		}
	}
	return mathDesc;
}


/**
 * Get probability expression for the specific reaction.
 * Input: ReactionStep, the reaction. SimulationContext, the application which the reaction takes place in.
 * Output: Expression. the probability expression.
 * Creation date: (9/14/2006 3:22:58 PM)
 */
public IExpression getProbabilityRate(ReactionStep rs, boolean isForwardDirection)
{
	ReactionStep reactionStep = rs;
	IExpression probExp = null;
	//get the rate constant.
	Kinetics kinetics = reactionStep.getKinetics();
	double rateConstant = 0;
	if(isForwardDirection) // forward reaction
	{
		try
		{
			if (kinetics.getKineticsDescription().getName().compareTo(KineticsDescription.MassAction.getName())==0)
			{
				rateConstant = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KForward).getConstantValue();
			}
			else if (kinetics.getKineticsDescription().getName().compareTo(KineticsDescription.HMM_irreversible.getName())==0)
		    {
				rateConstant = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_Km).getConstantValue();
			}
		    else if (kinetics.getKineticsDescription().getName().compareTo(KineticsDescription.HMM_reversible.getName())==0)
		    {
				rateConstant = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KmFwd).getConstantValue();
			}
			
		} catch (ExpressionException e) {e.printStackTrace();}
//		if (rateConstant == 0) // if not Mass action, TODO: temperrarily set it to 1 at the time being.
//			rateConstant = 1.0;
			
		//write the rest part of the probability expression by the reactants' stoichiometries.
		ReactionParticipant[] reacPart = reactionStep.getReactionParticipants();
		String reacPosibility = "";
		for (int i=0; i<reacPart.length; i++)
		{
			int stoi = 0;
			if(reacPart[i] instanceof Reactant)
			{ 
				stoi = ((Reactant)reacPart[i]).getStoichiometry();
				// check if the reactant is a constant
				if(getSimulationContext().getReactionContext().getSpeciesContextSpec(reacPart[i].getSpeciesContext()).isConstant())
				{
					reacPosibility = reacPosibility + "(" +stoi+"*"+((Reactant)reacPart[i]).getSpeciesContext().getName()+")";
				}
				else // not a constant
				{
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
				}
				reacPosibility = reacPosibility + "*";
			}
		}
		int len = reacPosibility.length();
		reacPosibility = reacPosibility.substring(0,(len-1));
		try
		{
			probExp = ExpressionFactory.createExpression(/*concentrationRateToParticleRate(rateConstant,reacPart)*/ rateConstant+"*"+reacPosibility); //TODO: what kind of constant we should use???? Do we need to convert?
		}catch (ExpressionException e){e.printStackTrace();}
	}
	else // backward reaction
	{
		try
		{
			if (kinetics.getKineticsDescription().getName().compareTo(KineticsDescription.MassAction.getName())==0)
			{
				rateConstant = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KReverse).getConstantValue();
			}
			else if (kinetics.getKineticsDescription().getName().compareTo(KineticsDescription.HMM_reversible.getName())==0)
		    {
				rateConstant = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KmRev).getConstantValue();
			}
		
		} catch (ExpressionException e) {e.printStackTrace();}
		if (rateConstant == 0) // if not Mass action, TODO: temperrarily set it to 1 at the time being.
			rateConstant = 1.0;
			
		//write the rest part of the probability expression by the products' stoichiometries.
		ReactionParticipant[] reacPart = reactionStep.getReactionParticipants();
		String reacPosibility = "";
		for (int i=0; i<reacPart.length; i++)
		{
			int stoi = 0;
			if(reacPart[i] instanceof Product)
			{ 
				stoi = ((Product)reacPart[i]).getStoichiometry();
				// check if the reactant is a constant
				if(getSimulationContext().getReactionContext().getSpeciesContextSpec(reacPart[i].getSpeciesContext()).isConstant())
				{
					reacPosibility = reacPosibility + "(" +stoi+"*"+((Product)reacPart[i]).getSpeciesContext().getName()+")";
				}
				else // not a constant
				{
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
				}
				reacPosibility = reacPosibility + "*";
			}
		}
		int len = reacPosibility.length();
		reacPosibility = reacPosibility.substring(0,(len-1));
		try
		{
			probExp = ExpressionFactory.createExpression(/*concentrationRateToParticleRate(rateConstant,reacPart)*/ rateConstant+"*"+reacPosibility);
			probExp.flatten();
		}catch (ExpressionException e){e.printStackTrace();}
	}
	return probExp;
}


	/**
	 * Basicly the function clears the error list and calls to get a new mathdescription.
	 */
	private void refresh() throws MappingException, ExpressionException, cbit.vcell.matrix.MatrixException, MathException, ModelException {
		issueList.clear();
		refreshMathDescription();
	}


	/**
	 * set up a math description based on current simulationContext.
	 */
	private void refreshMathDescription() throws MappingException, cbit.vcell.matrix.MatrixException, MathException, ExpressionException, ModelException {

	//System.out.println("MathMapping.refreshMathDescription()");
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
				IExpression volFractExp = ((MembraneMapping)sm).getVolumeFractionParameter().getExpression();
				try {
					double volFract = volFractExp.evaluateConstant();
					if (volFract>=1.0){
						throw new MappingException("model structure '"+((MembraneMapping)sm).getMembrane().getInsideFeature().getName()+"' has volume fraction >= 1.0");
					}
				}catch (ExpressionException e){
				}
			}
		}
		SubVolume subVolumes[] = getSimulationContext().getGeometryContext().getGeometry().getGeometrySpec().getSubVolumes();
		for (int i = 0; i < subVolumes.length; i++){
			if (getSimulationContext().getGeometryContext().getStructures(subVolumes[i])==null || getSimulationContext().getGeometryContext().getStructures(subVolumes[i]).length==0){
				throw new MappingException("geometry subVolume '"+subVolumes[i].getName()+"' not mapped from a model structure");
			}
		}
		
				
		mathDesc = new MathDescription(getSimulationContext().getName()+"_generated");
		
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
	
		
	    //add constant
	    mathDesc.addVariable(new Constant(getMathSymbol(ReservedSymbol.N_PMOLE,null),getIdentifierSubstitutions(ReservedSymbol.N_PMOLE.getExpression(),ReservedSymbol.N_PMOLE.getUnitDefinition(),null)));
		
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

		

		// set up stochastic variables
		// add stochastic variables(in mathDesc) and variable initial conditions(in subDomain).
		SpeciesContext[] speciesContexts = getSimulationContext().getModel().getSpeciesContexts(); // get all the species from simulation context

		for (int i = 0; i < speciesContexts.length; i++){
			StochVolVariable var = new StochVolVariable(speciesContexts[i].getName());
			// get the initial condition in concentration
			SpeciesContextSpec speciesContextsSpec = getSimulationContext().getReactionContext().getSpeciesContextSpec(speciesContexts[i]);
			try {
				// convert concentration to number of particles
				double c = speciesContextsSpec.getInitialConditionParameter().getConstantValue() * 1e-6; // uMol/L -> Mol/L
				double v = StructureMapping.getDefaultAbsoluteSize(); // L
				double molConst = ReservedSymbol.N_PMOLE.getExpression().evaluateConstant() * 1e12; // particles/pMol -> particles/Mol 
				VarIniCondition varIni = new VarIniCondition(var,ExpressionFactory.createExpression(c*v*molConst));
				mathDesc.addVariable(var);
				subDomain.addVarIniCondition(varIni);
			} catch (ExpressionException e) {
				e.printStackTrace();
			}
		}

		// set up jump processes
		// get all the reactions from simulation context
		ReactionSpec[] reactionSpecs = getSimulationContext().getReactionContext().getReactionSpecs();
		for (int i = 0; i < reactionSpecs.length; i++)
		{
			if (reactionSpecs[i].isExcluded()) {
				continue;
			}
			// get the reaction
			ReactionStep reactionStep = reactionSpecs[i].getReactionStep();
			// check the reaction rate law to see if we need to decompose a reaction(reversible) into two jump processes.
			// rate constants are important in calculating the probability rate.
			// for Mass Action, we use KForward and KReverse, for HMM_irreversible we directly use Km
			// and for HMM_reversible we use KmFwd and KmRev, for General and General Total Kinetics we use reaction rate J directly as probability rate
			Kinetics kinetics = reactionStep.getKinetics();
			double forwardRate = 0;
			double reverseRate = 0;
			if (kinetics.getKineticsDescription().getName().compareTo(KineticsDescription.MassAction.getName())==0)
			{
				try
				{
					forwardRate = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KForward).getConstantValue();
					reverseRate = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KReverse).getConstantValue();
				} catch (ExpressionException e) {e.printStackTrace();}
			}
			else if (kinetics.getKineticsDescription().getName().compareTo(KineticsDescription.HMM_irreversible.getName())==0)
		    {
			    try
				{
					forwardRate = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_Km).getConstantValue();
				} catch (ExpressionException e) {e.printStackTrace();}
			}
		    else if (kinetics.getKineticsDescription().getName().compareTo(KineticsDescription.HMM_reversible.getName())==0)
		    {
			    try
				{
					forwardRate = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KmFwd).getConstantValue();
					reverseRate = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KmRev).getConstantValue();
				} catch (ExpressionException e) {e.printStackTrace();}
			}
		     
			// if the reaction has forward rate (Mass action,HMMs), or don't have either forward or reverse rate (some other rate laws--like general)
			// we process it as forward reaction
			if ((forwardRate != 0) || ((forwardRate == 0) && (reverseRate == 0)))
			{
				IExpression exp = null;
				//reactions which are not mass action and HMMs, we just use the user input expression directlt as probability rate with substitution of kinetic parameters
				if ((forwardRate == 0) && (reverseRate == 0))
				{
					if(kinetics.getKineticsParameterFromRole(Kinetics.ROLE_Rate).getExpression() != null)
					{
						exp = ExpressionFactory.createExpression(kinetics.getKineticsParameterFromRole(Kinetics.ROLE_Rate).getExpression());
						//subsitute the kenetic parameter (only species name or constants leave in expression)
						Kinetics.KineticsParameter[] kpara = kinetics.getKineticsParameters();
						for(int k=0; k<kpara.length; k++)
						{
							if(! kpara[k].getName().equals("J")) //omit parameter "J"
							{
								exp.substituteInPlace(ExpressionFactory.createExpression(kpara[k].getName()),ExpressionFactory.createExpression(kpara[k].getConstantValue()));	
							}
							
						}
						exp.flatten();
					}
					else exp = ExpressionFactory.createExpression("0");
				}
				else // reactions are either mass action or HMM
				{
					exp = getProbabilityRate(reactionStep, true);
				}
				JumpProcess jp = new JumpProcess(reactionStep.getName(),exp);
				// actions
				ReactionParticipant[] reacPart = reactionStep.getReactionParticipants();
				for(int j=0; j<reacPart.length; j++)
				{
					if(reacPart[j] instanceof Reactant)
					{ 
						// check if the reactant is a constant. If the species is a constant, there will be no action taken on this species
						if(!getSimulationContext().getReactionContext().getSpeciesContextSpec(reacPart[j].getSpeciesContext()).isConstant()) // not a constant
						{
							int stoi = ((Reactant)reacPart[j]).getStoichiometry();
							Action action = new Action(mathDesc.getVariable( reacPart[j].getSpeciesContext().getName()),"inc", ExpressionFactory.createExpression("-"+String.valueOf(stoi)));
							jp.addAction(action);
						}
					}
					else if(reacPart[j] instanceof Product)
					{
						// check if the product is a constant. If the product is a constant, there will be no action taken on this species
						if(!getSimulationContext().getReactionContext().getSpeciesContextSpec(reacPart[j].getSpeciesContext()).isConstant()) // not a constant
						{
							int stoi = ((Product)reacPart[j]).getStoichiometry();
							Action action = new Action(mathDesc.getVariable( reacPart[j].getSpeciesContext().getName()),"inc", ExpressionFactory.createExpression(stoi));
							jp.addAction(action);
						}
					}
				}
				// add jump process to compartment subDomain
				subDomain.addJumpProcess(jp);
			}
			if (reverseRate != 0) // one more jump process for a reversible reaction
			{
				IExpression exp = getProbabilityRate(reactionStep, false);
				JumpProcess jp = new JumpProcess(reactionStep.getName()+"_reverse",exp);
				// actions
				ReactionParticipant[] reacPart = reactionStep.getReactionParticipants();
				for(int j=0; j<reacPart.length; j++)
				{
					if(reacPart[j] instanceof Reactant)
					{ 
						// check if the reactant is a constant. If the species is a constant, there will be no action taken on this species
						if(!getSimulationContext().getReactionContext().getSpeciesContextSpec(reacPart[j].getSpeciesContext()).isConstant()) // not a constant
						{
							int stoi = ((Reactant)reacPart[j]).getStoichiometry();
							Action action = new Action(mathDesc.getVariable( reacPart[j].getSpeciesContext().getName()),"inc", ExpressionFactory.createExpression(stoi));
							jp.addAction(action);
						}
					}
					else if(reacPart[j] instanceof Product)
					{
						// check if the product is a constant. If the product is a constant, there will be no action taken on this species
						if(!getSimulationContext().getReactionContext().getSpeciesContextSpec(reacPart[j].getSpeciesContext()).isConstant()) // not a constant
						{
							int stoi = ((Product)reacPart[j]).getStoichiometry();
							Action action = new Action(mathDesc.getVariable( reacPart[j].getSpeciesContext().getName()),"inc", ExpressionFactory.createExpression("-"+String.valueOf(stoi)));
							jp.addAction(action);
						}
					}
				}
				// add jump process to compartment subDomain
				subDomain.addJumpProcess(jp);	
			}
		}
			
		
		if (!mathDesc.isValid()){
			throw new MappingException("generated an invalid mathDescription: "+mathDesc.getWarning());
		}

	
	}
}
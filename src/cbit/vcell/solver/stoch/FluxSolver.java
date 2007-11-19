package cbit.vcell.solver.stoch;
 
import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathFormatException;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionUtils;

/**
 * FluxSolver parses flux(described by generalKinetics law) density function  to see if it is eligable to
 * automatically convert to jump processes for stochastic application. Static method solveFlux does the 
 * reqired function and save the results in to structure FluxFunction. Basically, if a flux density function
 * in form of p1*speceisOutside-p2*dpeciesInside, we can separate them into two jump processes.
 * Jump1: propensity=p1*speceisOutside*mem_size/KMOLE , speciesOutside inc -1, speceisInside inc 1
 * Jump2: propensity=p1*speceisInside*mem_size/KMOLE , speciesOutside inc 1, speceisInside inc -1
 * author: Tracy Li
 * version: 1.0 Beta
 */
public class FluxSolver {
	public static final double Epsilon = 1e-6; // to be used for double calculation
	
	public FluxSolver()
	{
	
	}
	public static class FluxFunction {
		private String speciesInside;
		private String speciesOutside;
		private Expression rateToInside = null; // the only symbols allowed in the expression are species outside and constant parameters.
		private Expression rateToOutside = null; // the only symbols allowed in the expression are species inside and constant parameters.
		
		public Expression getRateToInside()
		{
			return rateToInside;
		}

		public void setRateToInside(Expression rateToInside) {
			this.rateToInside = rateToInside;
		}

		public Expression getRateToOutside() {
			return rateToOutside;
		}

		public void setRateToOutside(Expression rateToOutside) {
			this.rateToOutside = rateToOutside;
		}

		public String getSpeciesInside() {
			return speciesInside;
		}

		public void setSpeciesInside(String speciesInside) {
			this.speciesInside = speciesInside;
		}

		public String getSpeciesOutside() {
			return speciesOutside;
		}

		public void setSpeciesOutside(String speciesOutside) {
			this.speciesOutside = speciesOutside;
		}
			
		public void show()
		{
			System.out.println("flux rate to inside:"+ getRateToInside());
			System.out.println("flux rate to outside:" + getRateToOutside());
			System.out.println("species inside:" + getSpeciesInside());
			System.out.println("species outside:" + getSpeciesOutside());
		}
	}
	
	
//	public static void main(String[] args){
//		try {
//			FluxFunction fluxFunction = solveFlux();
//			if (fluxFunction!=null){
//				fluxFunction.show();
//			}
//		} catch (Exception e) {
//			e.printStackTrace(System.out);
//		}
//	}
	//we have to pass the math description because the math description is not updated in simulation context.	
	public static FluxFunction solveFlux(Expression orgExp, ReactionStep rs) throws ExpressionException, MathException{
		FluxFunction ff = new FluxFunction();
		//get Species outside and inside contexts
		SpeciesContext sc_outside = ((FluxReaction)rs).getFlux(((Feature)rs.getStructure().getParentStructure())).getSpeciesContext();
		SpeciesContext sc_inside = ((FluxReaction)rs).getFlux(((Feature)rs.getStructure().getSubFeatures().nextElement())).getSpeciesContext();
		ff.setSpeciesOutside(sc_outside.getName());
		ff.setSpeciesInside(sc_inside.getName());
		
		Expression p1 = null;//permeability for Sout
		Expression p2 = null;//permeability for Sin
		
		if(rs.getPhysicsOptions() != ReactionStep.PHYSICS_MOLECULAR_ONLY)
		{
			throw new MathException("Kinetics of flux " + rs.getName() + " has membrane current. Can not run stochastic simulation, looking for the flux density function according to the form of p1*SpeciesOutside-p2*SpeciesInside .");
		}
		else
		{
		    //substitute flux density function with constants and functions(in case species and t are hide in functions)
		    //Expression subOrgExp = MathUtilities.substituteFunctions(orgExp,mathDesc);
		    //duplicate the substituted general flux density function
			Expression fluxExp = new Expression(orgExp);
			//check if there is constant left beside p1Sout-p2Sin, (e.g.p1Sout-p2Sin+c)
		    fluxExp = fluxExp.getSubstitutedExpression(new Expression(sc_outside.getName()), new Expression(0)).flatten();
			fluxExp = fluxExp.getSubstitutedExpression(new Expression(sc_inside.getName()), new Expression(0)).flatten();
			if (!ExpressionUtils.functionallyEquivalent(fluxExp, new Expression(0.0), false, 1e-8, 1e-8)){
				throw new MathException("Cannot generate stochastic math mapping for the flux: "+rs.getName()+". Looking for the flux density function according to the form of p1*SpeciesOutside-p2*SpeciesInside.");
			}
			//get p1 after partially differentiating speciesOutside
			fluxExp = new Expression(orgExp);
			fluxExp = fluxExp.differentiate(sc_outside.getName()).flatten();
			String err = checkPermeabilityValidity(new Expression(fluxExp), sc_outside.getName(), sc_inside.getName());
			if(err.equals(""))
			{
				p1 = fluxExp;
			}
			else
			{
				throw new MathException("Cannot generate stochastic math mapping for the flux: "+rs.getName()+". Looking for the flux density function according to the form of p1*SpeciesOutside-p2*SpeciesInside.");
			}
			//get p2 after partially differentiating speciesInside
			fluxExp = new Expression(orgExp);
			fluxExp = fluxExp.differentiate(sc_inside.getName()).flatten();
			fluxExp = new Expression(fluxExp.infix( )+ "*(-1)").flatten(); // p2 should be negtive, we multiply (-1) with it to get a non negtive value as propensity.
			err = checkPermeabilityValidity(new Expression(fluxExp), sc_outside.getName(), sc_inside.getName()); 
			if(err.equals(""))
			{
				p2 = fluxExp;
			}
			else
			{
				if(err.equals("t"))
				{
					throw new MathFormatException("Cannot generate stochastic math mapping for the flux: "+rs.getName()+". Unexpected symbol \'t\' in flux density function.");
				}
				else
				{
					throw new MathException("Cannot generate stochastic math mapping for the flux: "+rs.getName()+". Looking for the flux density function according to the form of p1*SpeciesOutside-p2*SpeciesInside.");
				}
			}
			
			ff.setRateToInside(p1);
			ff.setRateToOutside(p2);
			//Below is for debuging purpose
	//		if(p1 != null) 
	//		{
	//			System.out.println("rate to inside:"+ff.getRateToInside());
	//			System.out.println("species outside:"+ff.getSpeciesOutside());
	//			System.out.println("rate to outside:"+ff.getRateToOutside());
	//			System.out.println("species inside:"+ff.getSpeciesInside());
	//			Expression recomposedExp = new Expression(ff.getRateToInside().infix()+"*"+ff.getSpeciesOutside()+"-"+ff.getRateToOutside().infix()+"*"+ff.getSpeciesInside()).flatten();
	//		    System.out.println("Original flux density function"+orgExp.infix());
	//		    System.out.println("Recomposed flux function"+recomposedExp.infix());
	//		    if(ExpressionUtils.functionallyEquivalent(recomposedExp, orgExp, false, 1e-8, 1e-8))
	//		    {
	//		    	System.out.println("They are functional equivalent.");
	//		    }
	//		    else	System.out.println("They are NOT functional equivalent.");
	//		}
			return ff;
		}
	}
	
	// permeability is the expression in front of the variable. e.g. p1, p2 in p1*speciesOutside-p2*speciesInside
	// after partially differentiating the variable, the permeability function shouldn't contain any variable(speciesOutside, speciesInside and t)
	public static String checkPermeabilityValidity(Expression exp, String spOutside, String spInside)
	{
		String errString = "";
		String[] syms = exp.getSymbols();
		if(syms != null)
		{
			for(int i=0; i<syms.length; i++)
			{
				if(syms[i].equals(spOutside) || syms[i].equals(spInside))
				{
					errString = syms[i];
					break;
				}
			}
		}
		return errString;
	}
}

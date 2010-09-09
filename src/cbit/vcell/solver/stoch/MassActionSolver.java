package cbit.vcell.solver.stoch;

import java.util.Vector;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.gui.TransformMassActions.TransformedReaction;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionUtils;
import cbit.vcell.solver.stoch.FluxSolver.FluxFunction;
import cbit.vcell.units.VCUnitDefinition;

public class MassActionSolver {

	public static final double Epsilon = 1e-6; // to be used for double calculation
	
	public MassActionSolver()
	{
	}
	public static class MassActionFunction 
	{
		private Expression fRate = null;
		private Expression rRate = null;
				
		public MassActionFunction()
		{}
		public MassActionFunction(Expression forwardRate, Expression reverseRate)
		{
			fRate = forwardRate;
			rRate = reverseRate;
		}
		
		public Expression getForwardRate() {
			return fRate;
		}

		public void setForwardRate(Expression rate) {
			fRate = rate;
		}

		public Expression getReverseRate() {
			return rRate;
		}

		public void setReverseRate(Expression rate) {
			rRate = rate;
		}
		public VCUnitDefinition getForwardRateUnit() {
			// TODO Auto-generated method stub
			return null;
		}
		public VCUnitDefinition getReverseRateUnit() {
			// TODO Auto-generated method stub
			return null;
		}
		
//		public void show()
//		{
//			System.out.println("Forward rate is " + getForwardRate().infix());
//			System.out.println("Reverse rate is " + getReverseRate().infix());
//		}
	}
	
	public static MassActionFunction solveMassAction(Expression orgExp, ReactionStep rs ) throws ExpressionException, MathException{
		MassActionFunction maFunc = new MassActionFunction();
		//get reactants, products, overlaps, non-overlap reactants and non-overlap products
		Vector<ReactionParticipant> reactants = new Vector<ReactionParticipant>();
		Vector<ReactionParticipant> products = new Vector<ReactionParticipant>();
		Vector<ReactionParticipant> overlaps = new Vector<ReactionParticipant>();
		Vector<ReactionParticipant> nonOverlapReacts = new Vector<ReactionParticipant>(); 
		Vector<ReactionParticipant> nonOverlapProds = new Vector<ReactionParticipant>();
		ReactionParticipant[] rp = rs.getReactionParticipants();
		Expression duplicatedExp = new Expression(orgExp);
		Expression forwardExp = null;
		Expression reverseExp = null;
		//separate the reactants and products
		for(int i=0; i<rp.length; i++)
		{
			if(rp[i] instanceof Reactant)
			{
				reactants.add(rp[i]);
			}
			else if(rp[i] instanceof Product)
			{
				products.add(rp[i]);
			}
		}
		//get the overlaps, nonOverlapReactants 
		for(int i=0; i<reactants.size(); i++)
		{
			if(isProduct(products, reactants.elementAt(i).getSpeciesContext()))
			{
				overlaps.add(reactants.elementAt(i));
			}
			else
			{
				nonOverlapReacts.add(reactants.elementAt(i));
			}
		}
		//get nonOverlapProducts
		for(int i=0; i<products.size(); i++)
		{
			if(!isReactant(reactants, products.elementAt(i).getSpeciesContext()))
			{
				nonOverlapProds.add(products.elementAt(i));
			}
		}
		//reaction with membrane current can not be transformed to mass action
		if(rs.getKinetics().getKineticsDescription().isElectrical())
		{
			throw new MathException("Kinetics of reaction " + rs.getName() + " has membrane current. It can not be automatically transformed to Mass Action kinetics.");
		}
		else
		{
			//reaction in which reactants and products are not overlaping
			if(overlaps.size() == 0)
			{
				//get forward rate by substituting reactants to 1 and products to 0.
				for(int i=0; i<reactants.size(); i++)
				{
					duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(reactants.elementAt(i).getName()), new Expression(1)).flatten();
				}
				for(int i=0; i<products.size(); i++)
				{
					try
					{
						duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(products.elementAt(i).getName()), new Expression(0)).flatten();
					}catch(DivideByZeroException ex)
					{
						throw new MathException("Transform failed in reaction: " + rs.getName() + "." + TransformedReaction.Label_expectedReacForm); 
					}
				}
				forwardExp = duplicatedExp;
				duplicatedExp = new Expression(orgExp);
				//get reverse rate by substituting reactants to 0 and products to 1.
				for(int i=0; i<reactants.size(); i++)
				{
					try{
						duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(reactants.elementAt(i).getName()), new Expression(0)).flatten();
					}catch(DivideByZeroException ex)
					{
						throw new MathException("Transform failed in reaction: " + rs.getName() + "." + TransformedReaction.Label_expectedReacForm);
					}
				}
				for(int i=0; i<products.size(); i++)
				{
					duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(products.elementAt(i).getName()), new Expression(1)).flatten();
				}
				//If the general kinetics is in form of mass action, the reverse rate constant should be a negtive value. We store the absolute value for reverse rate constant.
				reverseExp = Expression.mult(duplicatedExp, new Expression(-1)).flatten();
				//check if "t" is in forward or reverse rate. Probability can not be a function of time.
				if(forwardExp.hasSymbol(ReservedSymbol.TIME.getName())||reverseExp.hasSymbol(ReservedSymbol.TIME.getName()) )
				{
					throw new MathException("Reaction: "+rs.getName()+" has symbol \'t\' in reaction rate. Propensity of a stochastic jump process should not be a functon of time.");
				}
				//Reconstruct the rate based on the extracted forward rate and reverse rate. If the reconstructed rate is not equivalent to the original rate, 
				//it means the original rate is not in the form of Kf*r1^n1*r2^n2-Kr*p1^m1*p2^m2.
				Expression constructedExp = reconstructedRate(forwardExp, reverseExp, rs);
				if(ExpressionUtils.functionallyEquivalent(orgExp, constructedExp, false, 1e-8, 1e-8))
				{
					maFunc.setForwardRate(forwardExp);
					maFunc.setReverseRate(reverseExp);
				}
				else
				{
					throw new MathException("Transform failed in reaction: " + rs.getName() + "." + TransformedReaction.Label_expectedReacForm);
				}
			}
			else
			{
				//reaction in which reactans and products are partially  overlaping
				if(overlaps.size() < Math.max(reactants.size(), products.size()))
				{
					//substitute the overlapping reatants/products with "1" to simplify the rate expression
					for(int i=0; i< overlaps.size(); i++)
					{
						duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(overlaps.elementAt(i).getName()), new Expression(1));
					}
					Expression simplifiedExp = new Expression(duplicatedExp);
					
					//get forward rate by substituting non-overlap reactants to 1 and non-overlap products to 0.
					for(int i=0; i<nonOverlapReacts.size(); i++)
					{
						duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(nonOverlapReacts.elementAt(i).getName()), new Expression(1)).flatten();
					}
					for(int i=0; i<nonOverlapProds.size(); i++)
					{
						duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(nonOverlapProds.elementAt(i).getName()), new Expression(0)).flatten();
					}
					forwardExp = duplicatedExp;
									
					//get reverse rate by substituting reactants to 0 and products to 1.
					duplicatedExp = new Expression(simplifiedExp);
					for(int i=0; i<nonOverlapReacts.size(); i++)
					{
						duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(nonOverlapReacts.elementAt(i).getName()), new Expression(0)).flatten();
					}
					for(int i=0; i<nonOverlapProds.size(); i++)
					{
						duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(nonOverlapProds.elementAt(i).getName()), new Expression(1)).flatten();
					}
					//If the general kinetics is in form of mass action, the reverse rate constant should be a negtive value. We store the absolute value for reverse rate constant.
					reverseExp = Expression.mult(duplicatedExp, new Expression(-1)).flatten();
					//check if "t" is in forward or reverse rate. Probability can not be a function of time.
					if(forwardExp.hasSymbol(ReservedSymbol.TIME.getName())||reverseExp.hasSymbol(ReservedSymbol.TIME.getName()) )
					{
						throw new MathException("Reaction: "+rs.getName()+" has symbol \'t\' in reafction rate. Propensity of a stochastic jump process should not be a functon of time.");
					}
					//Reconstruct the rate based on the extracted forward rate and reverse rate. If the reconstructed rate is not equivalent to the original rate, 
					//it means the original rate is not in the form of Kf*r1^n1*r2^n2-Kr*p1^m1*p2^m2.
					Expression constructedExp = reconstructedRate(forwardExp, reverseExp, rs);
					if(ExpressionUtils.functionallyEquivalent(orgExp, constructedExp, false, 1e-8, 1e-8))
					{
						maFunc.setForwardRate(forwardExp);
						maFunc.setReverseRate(reverseExp);
					}
					else
					{
						throw new MathException("Transform failed in reaction: " + rs.getName() + "." + TransformedReaction.Label_expectedReacForm);
					}
				}
				//reaction in which reactants and products are totally overlaping, throw exception
				else
				{
					throw new MathException("The reactants and products in a reaction are completely the same, it is hard to figure out whether the kinetic law of reaction " + rs.getName() + " is equivalent to MassAction or not");
				}
			}
		}
		return maFunc;
	}
	
	private static Expression reconstructedRate(Expression forwardExp, Expression reverseExp, ReactionStep rs) throws ExpressionException
	{
		Expression result = null;
		
		String reacString = forwardExp.infix();
		String prodString = reverseExp.infix();
        //loop through reactants and products
		ReactionParticipant[] rp = rs.getReactionParticipants();
		for(int i=0; i<rp.length; i++)
		{
			int stoi = rp[i].getStoichiometry();
			if(rp[i] instanceof Reactant)
			{
				reacString = reacString + "*" + rp[i].getName() + "^" + String.valueOf(stoi);
			}
			if(rp[i] instanceof Product)
			{
				prodString = prodString + "*" + rp[i].getName() + "^" + String.valueOf(stoi);
			}
		}
		
		result = new Expression(reacString + "-" + prodString);
		if(result != null)
		{
			return result.flatten();
		}
		else
		{
			throw new ExpressionException("Cannot reconstruct rate based on extracted forward and reverse rate constants.");
		}
	}// end of method reconstructedRate()
	
	/**
	 * Check if a species described by speceisContext is a reactant in a specific reaction
	 * We need the function to separate the pure reactants, pure products, and speceis are both reactants and products.
	 * @param rp
	 * @param sc
	 * @return boolean
	 */
	private static boolean isReactant(Vector<ReactionParticipant> reacts, SpeciesContext sc)
	{
		for(int i=0; i<reacts.size(); i++)
		{
			if((reacts.elementAt(i) instanceof Reactant) && (reacts.elementAt(i).getSpeciesContext().compareEqual(sc)))
			{
				return true;
			}
		}
		return false;
	}// end of method isReactant()
	
	/**
	 * Check if a species described by speceisContext is a product in a specific reaction
	 * We need the function to separate the pure reactants, pure products, and speceis are both reactants and products.
	 * @param rp
	 * @param sc
	 * @return boolean
	 */
	private static boolean isProduct(Vector<ReactionParticipant> prods, SpeciesContext sc)
	{
		for(int i=0; i<prods.size(); i++)
		{
			if((prods.elementAt(i) instanceof Product) && (prods.elementAt(i).getSpeciesContext().compareEqual(sc)))
			{
				return true;
			}
		}
		return false;
	}// end of method isReactant()
}

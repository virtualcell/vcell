package cbit.vcell.solver.stoch;

import java.util.Vector;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionUtils;
import cbit.vcell.solver.stoch.FluxSolver.FluxFunction;

public class MassActionSolver {

	public static final double Epsilon = 1e-6; // to be used for double calculation
	
	public MassActionSolver()
	{
	}
	public static class MassActionFunction 
	{
		private Expression fRate = null;
		private Expression rRate = null;
		
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
		
		public void show()
		{
			System.out.println("Forward rate is " + getForwardRate().infix());
			System.out.println("Reverse rate is " + getReverseRate().infix());
		}
	}
	
	public static MassActionFunction solveMassAction(Expression orgExp, ReactionStep rs /*, SimulationContext simContext*/) throws ExpressionException, MathException{
		MassActionFunction maFunc = new MassActionFunction();
		//get reactants, products, overlaps, non-overlap reactants and non-overlap products
		Vector<String> reactants = new Vector<String>();
		Vector<String> products = new Vector<String>();
		Vector<String> overlaps = new Vector<String>();
		Vector<String> nonOverlapReacts = new Vector<String>(); 
		Vector<String> nonOverlapProds = new Vector<String>();
		ReactionParticipant[] rp = rs.getReactionParticipants();
		Expression duplicatedExp = new Expression(orgExp);
		Expression forwardExp = null;
		Expression reverseExp = null;
		for(int i=0; i<rp.length; i++)
		{
			if((rp[i] instanceof Reactant) && (rp[i] instanceof Product))
			{
				overlaps.add(rp[i].getName());
				reactants.add(rp[i].getName());
				products.add(rp[i].getName());
			}
			else
			{
				if(rp[i] instanceof Reactant)
				{
					nonOverlapReacts.add(rp[i].getName());
					reactants.add(rp[i].getName());
				}
				else if(rp[i] instanceof Product)
				{
					nonOverlapProds.add(rp[i].getName());
					products.add(rp[i].getName());
				}
			}
		}
		
		if(rs.getKinetics().getKineticsDescription().isElectrical())
		{
			throw new MathException("Kinetics of reaction " + rs.getName() + " has membrane current. It can not be automatically transformed to Mass Action kinetics.");
		}
		else
		{
			//reaction in which reactants and products are not overlaping
			if(overlaps.size() == 0)
			{
				//get forward rate by substitute reactants to 1 and products to 0.
				for(int i=0; i<reactants.size(); i++)
				{
					duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(reactants.elementAt(i)), new Expression(1)).flatten();
				}
				for(int i=0; i<products.size(); i++)
				{
					duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(products.elementAt(i)), new Expression(0)).flatten();
				}
				forwardExp = duplicatedExp;
				duplicatedExp = new Expression(orgExp);
				//get reverse rate by substitute reactants to 0 and products to 1.
				for(int i=0; i<reactants.size(); i++)
				{
					duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(reactants.elementAt(i)), new Expression(0)).flatten();
				}
				for(int i=0; i<products.size(); i++)
				{
					duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(products.elementAt(i)), new Expression(1)).flatten();
				}
				//If the general kinetics is in form of mass action, the reverse rate constant should be a negtive value. We store the absolute value for reverse rate constant.
				reverseExp = Expression.mult(duplicatedExp, new Expression(-1)).flatten();
				Expression constructedExp = reconstructedRate(forwardExp, reverseExp, rs);
				if(ExpressionUtils.functionallyEquivalent(orgExp, constructedExp, false, 1e-8, 1e-8))
				{
					maFunc.setForwardRate(forwardExp);
					maFunc.setReverseRate(reverseExp);
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
						duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(overlaps.elementAt(i)), new Expression(1));
					}
					Expression simplifiedExp = new Expression(duplicatedExp);
					
					//get forward rate by substitute non-overlap reactants to 1 and non-overlap products to 0.
					for(int i=0; i<nonOverlapReacts.size(); i++)
					{
						duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(nonOverlapReacts.elementAt(i)), new Expression(1)).flatten();
					}
					for(int i=0; i<nonOverlapProds.size(); i++)
					{
						duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(nonOverlapProds.elementAt(i)), new Expression(0)).flatten();
					}
					forwardExp = duplicatedExp;
									
					//get reverse rate by substitute reactants to 0 and products to 1.
					duplicatedExp = new Expression(simplifiedExp);
					for(int i=0; i<nonOverlapReacts.size(); i++)
					{
						duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(nonOverlapReacts.elementAt(i)), new Expression(0)).flatten();
					}
					for(int i=0; i<nonOverlapProds.size(); i++)
					{
						duplicatedExp = duplicatedExp.getSubstitutedExpression(new Expression(nonOverlapProds.elementAt(i)), new Expression(1)).flatten();
					}
					reverseExp = Expression.mult(duplicatedExp, new Expression(-1)).flatten();
					Expression constructedExp = reconstructedRate(forwardExp, reverseExp, rs);
					if(ExpressionUtils.functionallyEquivalent(orgExp, constructedExp, false, 1e-8, 1e-8))
					{
						maFunc.setForwardRate(forwardExp);
						maFunc.setReverseRate(reverseExp);
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
		return result.flatten();
	}
}

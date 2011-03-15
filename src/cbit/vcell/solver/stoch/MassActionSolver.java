package cbit.vcell.solver.stoch;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.util.xml.VCLogger;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.Flux;
import cbit.vcell.model.Flux.FluxDirection;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics.KineticsProxyParameter;
import cbit.vcell.model.Model;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.Product;
import cbit.vcell.model.ProxyParameter;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.gui.TransformMassActions;
import cbit.vcell.model.gui.TransformMassActions.TransformedReaction;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionUtils;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTableEntry;
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
		private List<ReactionParticipant> reactants = null;
		private List<ReactionParticipant> products = null;
		
		public MassActionFunction()
		{}
		public MassActionFunction(Expression forwardRate, Expression reverseRate)
		{
			this(forwardRate, reverseRate, null, null);
		}
		public MassActionFunction(Expression forwardRate, Expression reverseRate, List<ReactionParticipant> reactants, List<ReactionParticipant> products)
		{
			this.fRate = forwardRate;
			this.rRate = reverseRate;
			this.reactants = reactants;
			this.products = products;
		}
		
		public Expression getForwardRate() {
			return fRate;
		}

		private void setForwardRate(Expression rate) {
			fRate = rate;
		}

		public Expression getReverseRate() {
			return rRate;
		}

		private void setReverseRate(Expression rate) {
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
		public List<ReactionParticipant> getReactants() {
			return reactants;
		}
		public void setReactants(List<ReactionParticipant> reactants) {
			this.reactants = reactants;
		}
		public List<ReactionParticipant> getProducts() {
			return products;
		}
		public void setProducts(List<ReactionParticipant> products) {
			this.products = products;
		}
//		public void show()
//		{
//			System.out.println("Forward rate is " + getForwardRate().infix());
//			System.out.println("Reverse rate is " + getReverseRate().infix());
//		}
	}
	
	public static Expression substituteParameters(Expression exp, boolean substituteConst) throws ExpressionException 
	{
		Expression result = new Expression(exp);
		boolean bSubstituted = true;
		while (bSubstituted) {
			bSubstituted = false;
			String symbols[] = result.getSymbols();
			for (int k = 0; symbols != null && k < symbols.length; k++) {
				SymbolTableEntry ste = result.getSymbolBinding(symbols[k]);
				if (ste instanceof ProxyParameter) {
					ProxyParameter pp = (ProxyParameter)ste;
					result.substituteInPlace(new Expression(pp,pp.getNameScope()), new Expression(pp.getTarget(),pp.getTarget().getNameScope()));
					bSubstituted = true;
				}else if (ste instanceof Parameter){
					Parameter kp = (Parameter)ste;
					try {
						Expression expKP = substituteParameters(kp.getExpression(), true);
						if (!expKP.flatten().isNumeric() || substituteConst) {
							result.substituteInPlace(new Expression(symbols[k]), new Expression(kp.getExpression()));
							bSubstituted = true;
						}
					} catch (ExpressionException e1) {
						e1.printStackTrace();
						throw new ExpressionException(e1.getMessage());
					}
				}
			}

		}
		return result;
	}

	
	public static MassActionFunction solveMassAction(Expression orgExp, ReactionStep rs ) throws ExpressionException, MathException, DivideByZeroException{
		MassActionFunction maFunc = new MassActionFunction();
		//get reactants, products, overlaps, non-overlap reactants and non-overlap products
		ArrayList<ReactionParticipant> reactants = new ArrayList<ReactionParticipant>();
		ArrayList<ReactionParticipant> products = new ArrayList<ReactionParticipant>();
		Vector<ReactionParticipant> overlaps = new Vector<ReactionParticipant>();
		Vector<ReactionParticipant> nonOverlapReacts = new Vector<ReactionParticipant>(); 
		Vector<ReactionParticipant> nonOverlapProds = new Vector<ReactionParticipant>();
		ReactionParticipant[] rp = rs.getReactionParticipants();
		Expression duplicatedExp = substituteParameters(orgExp, false);
		Expression forwardExp = new Expression(duplicatedExp);
		Expression reverseExp = new Expression(duplicatedExp);
		//separate the reactants and products, fluxes, catalysts
		String rxnName = rs.getName();
		for(int i=0; i<rp.length; i++)
		{
			if(rp[i] instanceof Reactant) {
				reactants.add(rp[i]);
			} else if(rp[i] instanceof Product) {
				products.add(rp[i]);
			} else if(rp[i] instanceof Flux) {
				Flux flux = (Flux)rp[i];
				if (flux.getFluxDirection().equals(FluxDirection.Reactant)) {
					reactants.add(rp[i]);
				} else if (flux.getFluxDirection().equals(FluxDirection.Product)) {
					products.add(rp[i]);
				} if (flux.getFluxDirection().equals(FluxDirection.Unknown)) {
					throw new MathException("Unknown direction of flux participant '" + rp[i].getSpeciesContext().getName() + "' in flux '" + rxnName + "'");
				}
			} else if (rp[i] instanceof Catalyst) {
				String catalystName = rp[i].getSpeciesContext().getName();
				// check if the rateExp (duplicatedExp) contains catalystName. We can proceed to convert reaction kinetics to MassAction form
				// only if duplictedExp is not a non-linear function of catalystName.
				if (duplicatedExp.hasSymbol(catalystName)) {
					// differentiate duplicatedExp
					Expression diffExpr = duplicatedExp.differentiate(catalystName).flatten();
					if (diffExpr.hasSymbol(catalystName)) {
						throw new MathException("Unable to interpret Kinetic rate for reaction : " + rxnName + " Cannot interpret non-linear function of compartment size");
					}

					// substitute catalystName with value of 1.0 in duplicatedExpr and compare with differentiatedExpr
					Expression expr1 = duplicatedExp.getSubstitutedExpression(new Expression(catalystName), new Expression(1.0)).flatten();
					if (!expr1.compareEqual(diffExpr) && !(ExpressionUtils.functionallyEquivalent(expr1, diffExpr))) {
						throw new MathException("Unable to interpret Kinetic rate for reaction : " + rxnName + " Cannot interpret non-linear function of compartment size");
					}

					// substitute catalystName with value of 0.0 in duplicatedExpr -> should evaluate to 0.0
					Expression expr0 = duplicatedExp.getSubstitutedExpression(new Expression(catalystName), new Expression(0.0)).flatten();
					if (!expr0.isZero()) {
						throw new MathException("Unable to interpret Kinetic rate for reaction : " + rxnName + " Cannot interpret non-linear function of compartment size");
					}
					// if these conditions are satisfied, duplicatedExpr = expr1 (where catalystName was substituted with 1).
					// duplicatedExp = new Expression(expr1);
				}
				// catalyst added as both product and reactant. When catalyst considered as a reaction participant
				// the stoichiometry should be set to 1.
				
				ReactionParticipant catalystRP = new ReactionParticipant(null, rs, rp[i].getSpeciesContext(), 1) {
					public boolean compareEqual(Matchable obj) {
						ReactionParticipant rp = (ReactionParticipant)obj;
						if (rp == null){
							return false;
						}
						if (!Compare.isEqual(getSpecies(),rp.getSpecies())){
							return false;
						}
						if (!Compare.isEqual(getStructure(),rp.getStructure())){
							return false;
						}
						if (getStoichiometry() != rp.getStoichiometry()){
							return false;
						}
						return true;
					}
					@Override
					public void writeTokens(PrintWriter pw) {
					}
					@Override
					public void fromTokens(CommentStringTokenizer tokens, Model model) throws Exception {
					}
				};
				products.add(catalystRP);
				reactants.add(catalystRP);
			}
		}
		//get the overlaps (reactionParticipants that are both reactant & product, could be a catalyst), nonOverlapReactants 
		for(int i=0; i<reactants.size(); i++)
		{
			if(contains(products, reactants.get(i).getSpeciesContext()))
			{
				overlaps.add(reactants.get(i));
			}
			else
			{
				nonOverlapReacts.add(reactants.get(i));
			}
		}
		//get nonOverlapProducts
		for(int i=0; i<products.size(); i++)
		{
			if(!contains(reactants, products.get(i).getSpeciesContext()))
			{
				nonOverlapProds.add(products.get(i));
			}
		}
		//reaction with membrane current can not be transformed to mass action
		if(rs.getKinetics().getKineticsDescription().isElectrical())
		{
			throw new MathException("Kinetics of reaction " + rxnName + " has membrane current. It can not be automatically transformed to Mass Action kinetics.");
		}
		else
		{
			//reaction in which reactants and products are not overlaping
			if(overlaps.size() == 0)
			{
				//get forward rate by substituting reactants to 1 and products to 0.
				for(int i=0; i<reactants.size(); i++)
				{
					forwardExp = forwardExp.getSubstitutedExpression(new Expression(reactants.get(i).getName()), new Expression(1)).flatten();
				}
				for(int i=0; i<products.size(); i++)
				{
					forwardExp = forwardExp.getSubstitutedExpression(new Expression(products.get(i).getName()), new Expression(0)).flatten();
				}
				//get reverse rate by substituting reactants to 0 and products to 1.
				for(int i=0; i<reactants.size(); i++)
				{
					reverseExp = reverseExp.getSubstitutedExpression(new Expression(reactants.get(i).getName()), new Expression(0)).flatten();
				}
				for(int i=0; i<products.size(); i++)
				{
					reverseExp = reverseExp.getSubstitutedExpression(new Expression(products.get(i).getName()), new Expression(1)).flatten();
				}
				//If the general kinetics is in form of mass action, the reverse rate constant should be a negtive value. We store the absolute value for reverse rate constant.
				reverseExp = Expression.mult(reverseExp, new Expression(-1)).flatten();
				//check if "t" is in forward or reverse rate. Probability can not be a function of time.
				if(forwardExp.hasSymbol(ReservedSymbol.TIME.getName())||reverseExp.hasSymbol(ReservedSymbol.TIME.getName()) )
				{
					throw new MathException("Reaction: "+rxnName+" has symbol \'t\' in reaction rate. Propensity of a stochastic jump process should not be a functon of time.");
				}
				//Reconstruct the rate based on the extracted forward rate and reverse rate. If the reconstructed rate is not equivalent to the original rate, 
				//it means the original rate is not in the form of Kf*r1^n1*r2^n2-Kr*p1^m1*p2^m2.
				Expression constructedExp = reconstructedRate(forwardExp, reverseExp, reactants, products, rs.getNameScope());
				Expression orgExp_withoutCatalyst = removeCatalystFromExp(orgExp, rs);
				Expression constructedExp_withoutCatalyst = removeCatalystFromExp(constructedExp, rs);
				if(ExpressionUtils.functionallyEquivalent(orgExp_withoutCatalyst, constructedExp_withoutCatalyst, false, 1e-8, 1e-8))
				{
					maFunc.setForwardRate(forwardExp);
					maFunc.setReverseRate(reverseExp);
					maFunc.setReactants(reactants);
					maFunc.setProducts(products);
				}
				else
				{
					throw new MathException("Transform failed in reaction: " + rxnName + "." + TransformedReaction.Label_expectedReacForm);
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
						forwardExp = forwardExp.getSubstitutedExpression(new Expression(overlaps.elementAt(i).getName()), new Expression(1));
					}
					Expression simplifiedExp = new Expression(forwardExp);
					
					//get forward rate by substituting non-overlap reactants to 1 and non-overlap products to 0.
					for(int i=0; i<nonOverlapReacts.size(); i++)
					{
						forwardExp = forwardExp.getSubstitutedExpression(new Expression(nonOverlapReacts.elementAt(i).getName()), new Expression(1)).flatten();
					}
					for(int i=0; i<nonOverlapProds.size(); i++)
					{
						forwardExp = forwardExp.getSubstitutedExpression(new Expression(nonOverlapProds.elementAt(i).getName()), new Expression(0)).flatten();
					}
									
					//get reverse rate by substituting reactants to 0 and products to 1.
					reverseExp = new Expression(simplifiedExp);
					for(int i=0; i<nonOverlapReacts.size(); i++)
					{
						reverseExp = reverseExp.getSubstitutedExpression(new Expression(nonOverlapReacts.elementAt(i).getName()), new Expression(0)).flatten();
					}
					for(int i=0; i<nonOverlapProds.size(); i++)
					{
						reverseExp = reverseExp.getSubstitutedExpression(new Expression(nonOverlapProds.elementAt(i).getName()), new Expression(1)).flatten();
					}
					//If the general kinetics is in form of mass action, the reverse rate constant should be a negtive value. We store the absolute value for reverse rate constant.
					reverseExp = Expression.mult(reverseExp, new Expression(-1)).flatten();
					//check if "t" is in forward or reverse rate. Probability can not be a function of time.
					if(forwardExp.hasSymbol(ReservedSymbol.TIME.getName())||reverseExp.hasSymbol(ReservedSymbol.TIME.getName()) )
					{
						throw new MathException("Reaction: "+rxnName+" has symbol \'t\' in reafction rate. Propensity of a stochastic jump process should not be a functon of time.");
					}
					//Reconstruct the rate based on the extracted forward rate and reverse rate. If the reconstructed rate is not equivalent to the original rate, 
					//it means the original rate is not in the form of Kf*r1^n1*r2^n2-Kr*p1^m1*p2^m2.
					Expression constructedExp = reconstructedRate(forwardExp, reverseExp,  reactants, products, rs.getNameScope());
					Expression orgExp_withoutCatalyst = removeCatalystFromExp(orgExp, rs);
					Expression constructedExp_withoutCatalyst = removeCatalystFromExp(constructedExp, rs);
					if(ExpressionUtils.functionallyEquivalent(orgExp_withoutCatalyst, constructedExp_withoutCatalyst, false, 1e-8, 1e-8))
					{
						maFunc.setForwardRate(forwardExp);
						maFunc.setReverseRate(reverseExp);
						maFunc.setReactants(reactants);
						maFunc.setProducts(products);
					}
					else
					{
						throw new MathException("Transform failed in reaction: " + rxnName + "." + TransformedReaction.Label_expectedReacForm);
					}
				}
				//reaction in which reactants and products are totally overlaping, throw exception
				else
				{
					throw new MathException("The reactants and products in a reaction are completely the same, it is hard to figure out whether the kinetic law of reaction " + rxnName + " is equivalent to MassAction or not");
				}
			}
		}
		return maFunc;
	}
	
	private static Expression removeCatalystFromExp(Expression orgExp, ReactionStep rs) throws ExpressionException{
		Expression resultExp = new Expression(orgExp);
		ReactionParticipant[] reacParticipants = rs.getReactionParticipants();
		for(ReactionParticipant rp: reacParticipants)
		{
			if((rp instanceof Catalyst) &&  orgExp.hasSymbol(rp.getName()))
			{
				resultExp.substituteInPlace(new Expression(rp.getName()), new Expression(1));
			}
		}
		return resultExp.flatten();
	}

	private static Expression reconstructedRate(Expression forwardExp, Expression reverseExp, ArrayList<ReactionParticipant> reactants, ArrayList<ReactionParticipant> products, NameScope ns) throws ExpressionException
	{
		Expression result = null;
		
		Expression reactExpr = new Expression(forwardExp);
		Expression prodExpr = new Expression(reverseExp);
        //loop through reactants and products
		for(ReactionParticipant reactant : reactants) {
			reactExpr = Expression.mult(reactExpr,Expression.power(new Expression(reactant.getSpeciesContext(),ns),new Expression(reactant.getStoichiometry())));
		}
		for(ReactionParticipant product : products) {
			prodExpr = Expression.mult(prodExpr,Expression.power(new Expression(product.getSpeciesContext(),ns),new Expression(product.getStoichiometry())));
		}

		result = Expression.add(reactExpr,Expression.negate(prodExpr));
		
		return result.flatten();
	}// end of method reconstructedRate()
	

	private static boolean contains(ArrayList<ReactionParticipant> reactionParticipants, SpeciesContext sc)
	{
		for(int i=0; i<reactionParticipants.size(); i++)
		{
			if(reactionParticipants.get(i).getSpeciesContext() == sc) {
				return true;
			}
		}
		return false;
	}// end of method contains()
	
}

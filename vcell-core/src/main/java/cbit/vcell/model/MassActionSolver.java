/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.matrix.MatrixException;
import cbit.vcell.matrix.RationalExp;
import cbit.vcell.matrix.RationalExpMatrix;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionUtils;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.RationalExpUtils;
import cbit.vcell.parser.SymbolTableEntry;

public class MassActionSolver {

	public static final double Epsilon = 1e-6; // to be used for double calculation
	private static final int[] primeIntNumbers = new int[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97}; 
	
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
						Expression expKP = kp.getExpression();
						if (!expKP.flatten().isNumeric() || substituteConst) {
							result.substituteInPlace(new Expression(symbols[k]), new Expression(kp.getExpression()));
							bSubstituted = true;
						}
					} catch (ExpressionException e1) {
						e1.printStackTrace();
						throw new ExpressionException(e1.getMessage());
					}
				}else if (substituteConst && ste instanceof Model.ReservedSymbol){
					Model.ReservedSymbol rs = (Model.ReservedSymbol)ste;
					try {
						if (rs.getExpression() != null) 
						{
							result.substituteInPlace(new Expression(symbols[k]), new Expression(rs.getExpression()));
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

	
	public static MassActionFunction solveMassAction(Parameter optionalForwardRateParameter, Parameter optionalReverseRateParameter, Expression orgExp, ReactionStep rs ) throws ExpressionException, ModelException, DivideByZeroException{
		MassActionFunction maFunc = new MassActionFunction();
		//get reactants, products, overlaps, non-overlap reactants and non-overlap products
		ArrayList<ReactionParticipant> reactants = new ArrayList<ReactionParticipant>();
		ArrayList<ReactionParticipant> products = new ArrayList<ReactionParticipant>();
		ReactionParticipant[] rp = rs.getReactionParticipants();
		//should use this one to compare functional equavalent since this duplicatedExp has all params substituted.
		Expression duplicatedExp = substituteParameters(orgExp, false);
		//separate the reactants and products, fluxes, catalysts
		String rxnName = rs.getName();
		
		//reaction with membrane current can not be transformed to mass action
		if(rs.getPhysicsOptions() == ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL || rs.getPhysicsOptions() == ReactionStep.PHYSICS_ELECTRICAL_ONLY)
		{
			throw new ModelException("Kinetics of reaction " + rxnName + " has membrane current. It can not be automatically transformed to Mass Action kinetics.");
		}
		
		for(int i=0; i<rp.length; i++)
		{
			if(rp[i] instanceof Reactant) {
				reactants.add(rp[i]);
			} else if(rp[i] instanceof Product) {
				products.add(rp[i]);
			} else if (rp[i] instanceof Catalyst) {
				String catalystName = rp[i].getSpeciesContext().getName();
				// check if the rateExp (duplicatedExp) contains catalystName. We can proceed to convert reaction kinetics to MassAction form
				// only if duplictedExp is not a non-linear function of catalystName.
				if (duplicatedExp.hasSymbol(catalystName)) {
					// Only when catalyst appears in ReactionRate, we add catalyst to both reactant and product 
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
		}
		
		/**
		 * The code below is going to solve reaction with kinetics that are NOT Massaction. Or Massaction with catalysts involved.
		 */
		//
		// 2x2 rational matrix
		//
		// lets define 
		//    J() is the substituted total rate expression
		//    P() as the theoretical "product" term with Kf = 1 
		//    R() as the theoretical "reactant" term with Kr = 1
		//
		// Kf * R([1 1 1])  - Kr * P([1 1 1])  = J([1 1 1])
		// Kf * R([2 3 4])  - Kr * P([2 3 4])  = J([2 3 4])
		//
		// in matrix form, 
		//
		//    |                                        |
		//    | R([1 1 1])   -P([1 1 1])    J([1 1 1]) |
		//    | R([2 3 4])   -P([2 3 4])    J([2 3 4]) |
		//    |                                        |
		//
		//
		// example: Kf*A*B^2*C - Kr*C*A
		//    J() = forwardRate*43/Kabc*A*B^2*C - (myC*5-2)*C*A
		//    P() = C*A
		//    R() = A*B^2*C
		//
		//   |                                              |
		//   | R([1 1 1])  -P([1 1 1])    J([1 1 1])        |
		//   | R([2 3 4])  -P([2 3 4])    J([2 3 4])        |
		//   |                                              |
		//
		//   |                                                                              |
		//   | R([1 1 1])*R([2 3 4])    -P([1 1 1])*R([2 3 4])     J([1 1 1])*R([2 3 4])    |
		//   | R([2 3 4])*R([1 1 1])    -P([2 3 4])*R([1 1 1])     J([2 3 4])*R([1 1 1])    |
		//   |                                                                              |
		//
		//
		//   |                                                                                                                    |
		//   | R([1 1 1])*R([2 3 4])  -P([1 1 1])*R([2 3 4])                         J([1 1 1])*R([2 3 4])                        |
		//   | 0                      -P([2 3 4])*R([1 1 1])+P([1 1 1])*R([2 3 4])   J([2 3 4])*R([1 1 1])-J([1 1 1])*R([2 3 4])  |
		//   |                                                                                                                    |
		//
		//
		//   
		//
		Expression forwardExp = null;
		Expression reverseExp = null;
		
		Expression R_exp = new Expression(1);
		if (reactants.size()>0){
			R_exp = new Expression(1.0);
			for (ReactionParticipant reactant : reactants){
				R_exp = Expression.mult(R_exp,Expression.power(new Expression(reactant.getName()),new Expression(reactant.getStoichiometry())));
			}
		}
		Expression P_exp = new Expression(1);
		if (products.size()>0){
			P_exp = new Expression(1.0);
			for (ReactionParticipant product : products){
				P_exp = Expression.mult(P_exp,Expression.power(new Expression(product.getName()),new Expression(product.getStoichiometry())));
			}
		}
		HashSet<String> reactionParticipantNames = new HashSet<String>();
		for (ReactionParticipant reactionParticipant : rs.getReactionParticipants()){
			reactionParticipantNames.add(reactionParticipant.getName());
		}
		Expression R_1 = new Expression(R_exp);
		Expression R_2 = new Expression(R_exp);
		Expression P_1 = new Expression(P_exp);
		Expression P_2 = new Expression(P_exp);
		Expression J_1 = new Expression(duplicatedExp);
		Expression J_2 = new Expression(duplicatedExp);
		int index = 0;
		for (String rpName : reactionParticipantNames){
			R_1.substituteInPlace(new Expression(rpName),new Expression(1.0));
			R_2.substituteInPlace(new Expression(rpName),new Expression(primeIntNumbers[index]));
			P_1.substituteInPlace(new Expression(rpName),new Expression(1.0));
			P_2.substituteInPlace(new Expression(rpName),new Expression(primeIntNumbers[index]));
			J_1.substituteInPlace(new Expression(rpName),new Expression(1.0));
			J_2.substituteInPlace(new Expression(rpName),new Expression(primeIntNumbers[index]));
			index++;
		}
		R_1 = R_1.flatten();
		R_2 = R_2.flatten();
		P_1 = P_1.flatten();
		P_2 = P_2.flatten();
		J_1 = J_1.flatten();
		J_2 = J_2.flatten();
		//if reaction has the same reactants and products and both forward and reserve sides are the same.
		// e.g. A+B <-> A+B, A+2B <-> A+2B
		if(ExpressionUtils.functionallyEquivalent(R_exp, P_exp, false, 1e-8, 1e-8))
		{
			throw new ModelException(VCellErrorMessages.getMassActionSolverMessage(rs.getName(), "Identical reactants and products not supported for stochastic models."));
		}
		if (R_exp.compareEqual(new Expression(1)) &&  P_exp.compareEqual(new Expression(1))){
			// no reactants, no products ... nothing to do
			forwardExp = null;
			reverseExp = null;
		}else{
			// both reactants and products
			RationalExpMatrix matrix = new RationalExpMatrix(2,3);
			matrix.set_elem(0, 0, RationalExpUtils.getRationalExp(R_1, true));
			matrix.set_elem(0, 1, RationalExpUtils.getRationalExp(Expression.negate(P_1), true));
			matrix.set_elem(0, 2, RationalExpUtils.getRationalExp(J_1, true));
			matrix.set_elem(1, 0, RationalExpUtils.getRationalExp(R_2, true));
			matrix.set_elem(1, 1, RationalExpUtils.getRationalExp(Expression.negate(P_2), true));
			matrix.set_elem(1, 2, RationalExpUtils.getRationalExp(J_2, true));
			RationalExp[] solution = null;
			RationalExp[] originalSolution = null;
			try {
				//matrix.show();
				solution = matrix.solveLinearExpressions();
				originalSolution = matrix.solveLinearExpressions();
				solution[0] = solution[0].simplify(); //solution[0] is forward rate.
				solution[1] = solution[1].simplify(); //solution[1] is reverse rate. 
			} catch(ArithmeticException e) {
				if(e.getMessage() == null || !e.getMessage().startsWith(jscl.math.Expression.FailedToSimplify)) {
					throw(e);	// if failed to simplify, continue with what we have, otherwise rethrow
				}
			} catch (MatrixException e) {
				e.printStackTrace();
				throw new ModelException(VCellErrorMessages.getMassActionSolverMessage(rs.getName(), "MatrixException: " + e.getMessage()));
			}
			forwardExp = new Expression(solution[0].infixString());
			reverseExp = new Expression(solution[1].infixString());
			//for massAction, if there is no reactant in the forward rate, the forwardExp should be set to null to avoid writing jump process for the forward reaction.
			if(R_exp.compareEqual(new Expression(1)) && rs.getKinetics().getKineticsDescription().equals(KineticsDescription.MassAction))
			{
				forwardExp = null;
			}
			//for massAction, if there is no product in the reverse rate, the reverseExp should be set to null to avoid writing jump process for the reverse reaction.
			if(P_exp.compareEqual(new Expression(1)) && rs.getKinetics().getKineticsDescription().equals(KineticsDescription.MassAction))
			{
				reverseExp = null;
			}
		}
		if(forwardExp != null){
			forwardExp.bindExpression(rs);
		}
		if(reverseExp != null){
			reverseExp.bindExpression(rs);
		}
		//Reconstruct the rate based on the extracted forward rate and reverse rate. If the reconstructed rate is not equivalent to the original rate, 
		//it means the original rate is not in the form of Kf*r1^n1*r2^n2-Kr*p1^m1*p2^m2.
		Expression constructedExp = reconstructedRate(forwardExp, reverseExp,  reactants, products, rs.getNameScope());
		Expression orgExp_withoutCatalyst = removeCatalystFromExp(duplicatedExp, rs);
		Expression constructedExp_withoutCatalyst = removeCatalystFromExp(constructedExp, rs);
		if(! ExpressionUtils.functionallyEquivalent(orgExp_withoutCatalyst, constructedExp_withoutCatalyst, false, 1e-8, 1e-8))
		{
			//TODO: aici!!!
			
			
			
			throw new ModelException(VCellErrorMessages.getMassActionSolverMessage(rs.getName(), "Mathmatical form incompatible with mass action."));
		} else {
			//check if forward rate constant and reverse rate constant both can be evaluated to constants(numbers) after substituting all parameters.
			if(forwardExp != null)
			{
				Expression forwardExpCopy = new Expression(forwardExp);
				try{
					substituteParameters(forwardExpCopy, true).evaluateConstant();
				}catch(ExpressionException e)
				{
					throw new ModelException(VCellErrorMessages.getMassActionSolverMessage(rs.getName(), "Problem in forward rate '" + forwardExp.infix() + "', " + e.getMessage()));
				}
				//
				// if forwardExp is just flattened version of original Expression (orgExp), then keep orgExp so that Math Generation is readable.
				//
				if (optionalForwardRateParameter!=null){
					Expression forwardRateParameterCopy = new Expression(optionalForwardRateParameter,optionalForwardRateParameter.getNameScope());
					try{
						substituteParameters(forwardRateParameterCopy, true).evaluateConstant();
						if (forwardExpCopy.compareEqual(forwardRateParameterCopy)){
							forwardExp = new Expression(optionalForwardRateParameter,optionalForwardRateParameter.getNameScope());
						}
					}catch(ExpressionException e){
						// not expecting a problem because forwardExpCopy didn't have a problem, but in any case it is ok to swallow this exception
						e.printStackTrace(System.out);
					}
				}
				
			}
			if(reverseExp != null)
			{
				Expression reverseExpCopy = new Expression(reverseExp);
				try{
					substituteParameters(reverseExpCopy, true).evaluateConstant();
				}catch(ExpressionException e)
				{
					throw new ModelException(VCellErrorMessages.getMassActionSolverMessage(rs.getName(), "Problem in reverse rate '" + reverseExp.infix() + "', " + e.getMessage()));
				}
				
				//
				// if reverseExp is just flattened version of original Expression (orgExp), then keep orgExp so that Math Generation is readable.
				//
				if (optionalReverseRateParameter!=null){
					Expression reverseRateParameterCopy = new Expression(optionalReverseRateParameter,optionalReverseRateParameter.getNameScope());
					try{
						substituteParameters(reverseRateParameterCopy, true).evaluateConstant();
						if (reverseExpCopy.compareEqual(reverseRateParameterCopy)){
							reverseExp = new Expression(optionalReverseRateParameter,optionalReverseRateParameter.getNameScope());
						}
					}catch(ExpressionException e){
						// not expecting a problem because reverseExpCopy didn't have a problem, but in any case it is ok to swallow this exception
						e.printStackTrace(System.out);
					}
				}
			}
			maFunc.setForwardRate(forwardExp);
			maFunc.setReverseRate(reverseExp);
			maFunc.setReactants(reactants);
			maFunc.setProducts(products);
//			System.out.println("forward rate = "+maFunc.getForwardRate().infix());
//			System.out.println("reverse rate = "+maFunc.getReverseRate().infix());
					
			return maFunc;
		}
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
		Expression reactExpr = null;
		Expression prodExpr = null;
		if(forwardExp != null)
		{
			reactExpr = new Expression(forwardExp);
			//loop through reactants
			for(ReactionParticipant reactant : reactants) {
				reactExpr = Expression.mult(reactExpr,Expression.power(new Expression(reactant.getSpeciesContext(),ns),new Expression(reactant.getStoichiometry())));
			}
		}
		if(reverseExp != null){
			prodExpr = new Expression(reverseExp);
	        //loop through products
			for(ReactionParticipant product : products) {
				prodExpr = Expression.mult(prodExpr,Expression.power(new Expression(product.getSpeciesContext(),ns),new Expression(product.getStoichiometry())));
			}

		}
		if(reactExpr == null && prodExpr != null)
		{
			result = Expression.negate(prodExpr);
		}
		else if(reactExpr != null && prodExpr == null)
		{
			result = reactExpr;
		}
		else if(reactExpr != null && prodExpr != null)
		{
			result = Expression.add(reactExpr,Expression.negate(prodExpr));
		}
		else 
		{
			throw new ExpressionException("Both forward and reverse rate constants are null.");
		}
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

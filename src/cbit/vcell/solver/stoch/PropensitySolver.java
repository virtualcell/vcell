/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.stoch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

import cbit.vcell.math.MathException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionUtils;
import cbit.vcell.parser.SimpleSymbolTable;
/**
 * This PropensitySolver parses a probablity expression(either from biomodel or from mathmodel)
 * to check the validity and extract necessary information(e.g. rate constant, and order of each
 * stochastic variables). The prob expression should be in the form that binomials multiply together.
 * The form of binomial can be constant (e.g. K), var ^ order, var-i (if there is i in the binomial,
 * there should be (var-0)....(var-i+1) in the expression). This is based on the assumption that 
 * the reactions are all using Mass Action kinetic laws.
 * The class is used in NetCDFWriter.
 * authors: Jim Schaff, Tracy Li
 * version: 1.0 Beta
 */
public class PropensitySolver {
	
	public static class RootOrderIterator {
		private int numRoots;
		private int totalOrder;
		private int[] currentOrders;
		private int maxOrder;
		private boolean done = false;
		public RootOrderIterator(int argTotalOrder, int argNumRoots){
			this.numRoots = argNumRoots;
			this.totalOrder = argTotalOrder;
			this.currentOrders = new int[numRoots];
			this.maxOrder = totalOrder-numRoots+1;
			Arrays.fill(this.currentOrders, 0);
		}
		public int[] nextOrder(){
			incrementOrderSet();
			//System.out.print("...");  show();
			while (!done && !validateOrderSet()){
				incrementOrderSet();
				//System.out.print("..."); show();
			}
			if (!done){
				return currentOrders;
			}else{
				return null;
			}
		}
		private void incrementOrderSet(){
			currentOrders[0]++;
			for (int i = 0; i < currentOrders.length; i++) {
				if (currentOrders[i]>maxOrder){
					// need to carry
					if (i==currentOrders.length-1){
						done = true;
					}else{
						// carry
						currentOrders[i]=1;
						currentOrders[i+1]++;
					}
				}
			}
		}
		public boolean validateOrderSet(){
			int sumOrder = 0;
			for (int i = 0; i < currentOrders.length; i++) {
				sumOrder += currentOrders[i];
				if (currentOrders[i]<1 || currentOrders[i]>maxOrder){
					return false;
				}
			}
			if (sumOrder!=totalOrder){
				return false;
			}
			return true;
		}
		public int[] getCurrentOrders()
		{
			return currentOrders;
		}
		public void show(){
			for (int i = 0; i < currentOrders.length; i++) {
				System.out.print(currentOrders[i]+" ");
			}
			System.out.println();
		}
	}
	
	public static class Binomial {
		public String varName;
		public int root;
		public int power;
		public String powerVarName;
		public String infix(){
			if (power!=1){
				return "("+varName+"-"+root+")^"+power;
			}else{
				return "("+varName+"-"+root+")";
			}
		}
	}
	
	public static class PropensityFunction{
		public String[] speciesNames;	// size numSpecies
		public int[] speciesOrders;		// size numSpecies
		public Binomial[] binomials;	// size Sum(speciesOrders(i))
		public Expression k_expression;
		public Expression canonicalExpression;
		public void show(){
			System.out.println("PropensityFunction: K="+k_expression.infix()+" ... whole expression = "+canonicalExpression.infix());
			System.out.print(k_expression.infix());
			for (int i = 0; i < binomials.length; i++) {
				System.out.print("*"+binomials[i].infix());
			}
			System.out.println();
		}
		public int[] getSpeciesOrders()
		{
			return speciesOrders;
		}
		public String[] getSpeceisNames()
		{
			return speciesNames;
		}
		public Expression getRateExpression()
		{
			return k_expression;
		}
	}
	
	public static void main(String[] args){
		try {
			//PropensityFunction propensityFunction = solvePropensity(new Expression("K*(x-y)"), new String[] { "x", "y" }, 10);
			//PropensityFunction propensityFunction = solvePropensity(new Expression("K*cos(t)*(x)*(x-1)^2*y/(abc-exp(xyz))"), new String[] { "x", "y" }, 10);
			//PropensityFunction propensityFunction = solvePropensity(new Expression("x*(x-1)"), new String[] { "x" }, 10);
			//PropensityFunction propensityFunction = solvePropensity(new Expression("x^2*y - 2*x*y + 2*x - x*x"), new String[] { "x", "y"}, 10);
			//PropensityFunction propensityFunction = solvePropensity(new Expression("K*(x*y-y-2*x+2)"), new String[] { "x", "y" }, 10);
			//PropensityFunction propensityFunction = solvePropensity(new Expression("K*(x^3-5*x^2+8*x-4)"), new String[] { "x"}, 10);
			PropensityFunction propensityFunction = solvePropensity(new Expression("K*y*(y-2)*x^3"), new String[] { "y" , "x"}, 10);
			//PropensityFunction propensityFunction = solvePropensity(new Expression("(x-0.1)*sin(t)*(x^3*y^2-3*x^2*y^2+2*x*y^2-x^3*y+3*x^2*y-2*x*y)/(abc+a123)"), new String[] { "x", "y"}, 10);
			//PropensityFunction propensityFunction = solvePropensity(new Expression("KliYe*sin(t)*(x)*(x-9)-y*y/2"), new String[] { "x", "y" }, 10);
			if (propensityFunction!=null){
				propensityFunction.show();
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
		
	public static PropensityFunction solvePropensity(Expression original_expression, String[] speciesNames, int maxOrder) throws ExpressionException, MathException{
		boolean[][] rootExists = new boolean[speciesNames.length][maxOrder];
		ArrayList<Binomial> binomialList = new ArrayList<Binomial>();
		int[] order = new int[speciesNames.length];
		int totalOrder = 0;
		//To find if roots exited and save into rootExists, meanwhile find the order for each variable.
		//Actually, if order is 3, the roots can be either 0 (implies var^3) or 0,1,2 (implies var*(var-1)*(var-2))
		for (int i = 0; i < speciesNames.length; i++) {
			order[i] = -1;
			Expression diff_i = new Expression(original_expression);
			for (int j = 0; j < rootExists[0].length; j++){
				Expression exp_subX_j = original_expression.getSubstitutedExpression(new Expression(speciesNames[i]),new Expression(j));
				if (ExpressionUtils.functionallyEquivalent(exp_subX_j, new Expression(0.0), false, 1e-8, 1e-8)){
					rootExists[i][j] = true;
				}else{
					rootExists[i][j] = false;
				}
				diff_i = diff_i.differentiate(speciesNames[i]).flatten();
				if (!ExpressionUtils.functionallyEquivalent(diff_i, new Expression(0.0), false, 1e-8, 1e-8)){
					order[i] = j+1;
					if (order[i] > maxOrder){
						throw new MathException("order of "+speciesNames[i]+" in expression "+original_expression.infix()+" is larger than expected for a propensity function");
					}
				}
			}
		}
		
		//check if the roots are valid for mass actions (roots should start with 0 and increase consecutively)
		for(int i =0 ; i<speciesNames.length; i++)
		{
			int ord = order [i];
			totalOrder = totalOrder + order[i];
			int expectedRootCount = 0;
			int totalRootCount = 0;
			//expectedRootCount should be 1 or order
			for (int j = 0; j < ord; j++) {
				if (rootExists[i][j]){
					expectedRootCount++;
				}
			}
			//total root count
			for(int j=0; j< rootExists[i].length; j++)
			{
				if (rootExists[i][j]){
					totalRootCount++;
				}
			}
			//either root is 0, or number of roots equals to order.
			if(!((totalRootCount == 1 && expectedRootCount == totalRootCount && rootExists[i][0]) || (totalRootCount>1 && expectedRootCount == totalRootCount)))
			{
				throw new MathException("\n\nSpecies \'"+ speciesNames[i] + "\' has wrong form in propensity function. Hybrid solvers require strict propensity functions which should only contain the reactants in the function. \n\nIntegers in '" + speciesNames[i] + "' binomials should start with 0 and increase consecutively." + 
						"e.g. species1*(species1-1)*(species1-2)*species2*(speceis2-1)...");
			}
		}
		//get all the constraints(for expponets vars)
		ArrayList<String> constraintSymbols = new ArrayList<String>();
		ArrayList<Expression> constraints = new ArrayList<Expression>();
		ArrayList<ArrayList<String>> exponents = new ArrayList<ArrayList<String>>();
		Expression canonical_expression = new Expression(1.0);
		for (int i = 0; i < rootExists.length; i++) {
			exponents.add(new ArrayList<String>());
			for (int j = 0; j < rootExists[i].length; j++) {
				if (rootExists[i][j]){
					String exponentVar = "n_"+speciesNames[i]+"_"+j;
					constraintSymbols.add(exponentVar);
					exponents.get(i).add(exponentVar);
					canonical_expression = Expression.mult(canonical_expression,new Expression("("+speciesNames[i]+" - "+j+")^"+exponentVar));
					Binomial binomial = new Binomial();
					binomial.varName = speciesNames[i];
					binomial.root = j;
					binomial.powerVarName = exponentVar;
					binomialList.add(binomial);
				}
			}
			Expression expConstraint = new Expression(0.0);
			for (int j = 0; j < exponents.get(i).size(); j++) {
				expConstraint = Expression.add(expConstraint,new Expression(exponents.get(i).get(j)));
				constraints.add(new Expression(exponents.get(i).get(j)+" >= 1"));
				constraints.add(new Expression(exponents.get(i).get(j)+" <= "+order[i]));
			}
			constraints.add(new Expression(expConstraint.flatten().infix()+" == "+order[i]));
		}
		SimpleSymbolTable constraintSymbolTable = new SimpleSymbolTable(constraintSymbols.toArray(new String[constraintSymbols.size()]));
		for (int i = 0; i < constraints.size(); i++) {
			constraints.get(i).bindExpression(constraintSymbolTable);
		}
				
		//the array list to save different conbination values of orders of binomials. one element saves one conbination
		ArrayList<int[]> orderList = new ArrayList<int[]>();
		try {
			PropensitySolver.RootOrderIterator iter = new PropensitySolver.RootOrderIterator(totalOrder,constraintSymbols.size());
			while (iter.nextOrder()!=null){
				orderList.add(iter.getCurrentOrders().clone());
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new MathException("Did not recognize propensity function from \""+original_expression.infix()+ " product of species shoulde have order less than or equal to 3 and the form like 'rateConstant*species1*(species1-1)*species2'.");
		}
			
		Expression expression_for_K = Expression.mult(original_expression.flatten(),Expression.invert(canonical_expression.flatten()));
		Random rand = new Random(0);
		for (int[] values : orderList) {
			//
			// for each valid combination of orders (via constraints), choose the one that always gives the same answer for K
			//
			Expression order_substituted_canonical_expression = new Expression(canonical_expression);
			Expression order_substituted_expression_for_K = new Expression(expression_for_K);
			for (int j = 0; j < values.length; j++) {
				order_substituted_canonical_expression.substituteInPlace(new Expression(constraintSymbols.get(j)), new Expression(values[j]));
				order_substituted_expression_for_K.substituteInPlace(new Expression(constraintSymbols.get(j)), new Expression(values[j]));
			}
			order_substituted_canonical_expression = order_substituted_canonical_expression.flatten();
			order_substituted_expression_for_K = order_substituted_expression_for_K.flatten();
			Hashtable<String, Integer> k_hash = new Hashtable<String, Integer>();
			boolean bFailed = false;
			//for the specific conbination of orders, replace species names with random numbers, therefore to get rate constant value.
			for (int i = 0; !bFailed && i < 40; i++) {
				Expression substituted_expression_for_K = new Expression(order_substituted_expression_for_K);
				// substitute variables with random number and save the results in a hashtable.
				for (int j = 0; j < speciesNames.length; j++) {
					int randInt = rand.nextInt(20)-10;
					while (randInt < rootExists[j].length && randInt>=0 && rootExists[j][randInt]){
						// make sure it is not a root of the system
						randInt = rand.nextInt(20)-10;
					}
					substituted_expression_for_K.substituteInPlace(new Expression(speciesNames[j]), new Expression(randInt));
					substituted_expression_for_K = substituted_expression_for_K.flatten();
				}
				String k_key = substituted_expression_for_K.infix();
				if (k_hash.containsKey(k_key)){
					int numOccur = k_hash.get(k_key);
					k_hash.put(k_key, numOccur+1);
				}else{
					k_hash.put(k_key,1);
				}
			}
			//to see if the results in the hashtable are the same all the time. if so, the conbination is correct.
			Set<String> keySet = k_hash.keySet();
			String[] keys = keySet.toArray(new String[k_hash.size()]);
			Expression k_exp_0 = new Expression(keys[0]);
			Expression k_most_popular = k_exp_0;
			Integer mostOccurances = k_hash.get(keys[0]);
			//why not check if there is only one key, the conbination is correct??
			for (int i = 1; !bFailed && i < keys.length; i++) {
				Expression k_exp_i = new Expression(keys[i]);
				Integer occurances = k_hash.get(keys[i]);
				if (occurances>mostOccurances){
					mostOccurances = occurances;
					k_most_popular = k_exp_i;
				}
				if (!ExpressionUtils.functionallyEquivalent(k_exp_0, k_exp_i,false,1e-8,1e-8)){
					bFailed = true;
				}
			}
			if (!bFailed){
				PropensityFunction propensityFunction = new PropensityFunction();
				propensityFunction.speciesNames = speciesNames.clone();
				propensityFunction.speciesOrders = order.clone();
				propensityFunction.canonicalExpression = Expression.mult(k_most_popular,order_substituted_canonical_expression);
				propensityFunction.k_expression = k_most_popular;
				for (int i = 0; i < binomialList.size(); i++) {
					binomialList.get(i).power = (int)values[i];
				}
				propensityFunction.binomials = binomialList.toArray(new Binomial[binomialList.size()]);
				return propensityFunction;
			}
		}	
		throw new MathException("Did not recognize propensity function from \""+original_expression.infix()+"\", looking for product of species (e.g. \"rateConstant*species1*(species1-1)*species2\")");
	}
}

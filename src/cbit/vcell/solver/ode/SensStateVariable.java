/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode;

import java.util.Vector;

import cbit.vcell.math.Constant;
import cbit.vcell.math.MathException;
import cbit.vcell.math.VolVariable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.solver.SimulationSymbolTable;
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 9:00:20 PM)
 * @author: John Wagner
 */
public class SensStateVariable extends StateVariable {
	Expression optimizedRateSensExp = null;
	Expression optimizedJacobianExps[] = null;
	//SensVariable sensVars[] = null;
	Vector<SensVariable> sensVars = null;
/**
 * TimeSeriesData constructor comment.
 */
public SensStateVariable(SensVariable sensVariable, RateSensitivity rateSensitivity, 
		Jacobian jacobian, Vector<SensVariable> sensVarArray, SimulationSymbolTable simSymbolTable) throws MathException, ExpressionException {
	super(sensVariable);
	init(rateSensitivity,jacobian,simSymbolTable,sensVarArray);
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param values double[]
 */
public double evaluateIC(double values[]) throws ExpressionException {
	return 0.0;
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param values double[]
 */
public double evaluateRate(double values[]) throws ExpressionException {
	
	double value = 0.0;
	
	//
	//  add product of jacobian and sensVars for all k
	//
	//         d Fi     d Ck         d Fi
	//        ------ * ------   =   ------ * Skj
	//         d Ck     d Pj         d Ck
	//
	//               d Ci
	//  where Sij = ------  is the current value of the sensVariable
	//               d Pj
	//
	for (int i=0;i<sensVars.size();i++){
		SensVariable sensVariable = (SensVariable) sensVars.elementAt(i);
		double sensVarValue = values[sensVariable.getIndex()];
		value += optimizedJacobianExps[i].evaluateVector(values) * sensVarValue;
	}

	
	//                         d Fi
	// add rate sensitivity   ------
	//                         d Pj
	//
	value += optimizedRateSensExp.evaluateVector(values);

/* 
    double t = values[ReservedVariable.TIME.getIndex()];
    double k = 4.0;
    double K = 3.464101615;
    double k1 = 1.0;
    double k2 = 2.0;
    double k3 = 1.0;
    double A0 = 1.0;
    double lambda_1 = (k-K)/2;
    double lambda_2 = (k+K)/2;
    double K_total = 1.0; // K_total=A_init+B_init+C_init, where A_init=1, B_init=0, C_init=0
    
    double dSAk2_wrt_t = (A0*k1/Math.pow(K,2))*(((k-2*k3)/K)*(-lambda_1*Math.exp(-lambda_1*t) + lambda_2*Math.exp(-lambda_2*t)) + t/2*(-lambda_1*(2*k3-k+K)*Math.exp(-lambda_1*t) -lambda_2*(2*k3-k-K)*Math.exp(-lambda_2*t)) + 1/2*((2*k3-k+K)*Math.exp(-lambda_1*t) + (2*k3-k-K)*Math.exp(-lambda_2*t)));
    double dSBk2_wrt_t = (A0*k1/Math.pow(K,2))*(-k/K*(-lambda_1*Math.exp(-lambda_1*t) +lambda_2*Math.exp(-lambda_2*t)) - t/2*(-lambda_1*(K-k)*Math.exp(-lambda_1*t) + lambda_2*(K+k)*Math.exp(-lambda_2*t)) - 1/2*((K-k)*Math.exp(-lambda_1*t) - (K+k)*Math.exp(-lambda_2*t)));

    double dA_wrt_k2 = (A0*k1/Math.pow(K,2))*(((k-2*k3)/K)*(Math.exp(-lambda_1*t) - Math.exp(-lambda_2*t)) + t/2*((2*k3-k+K)*Math.exp(-lambda_1*t) + (2*k3-k-K)*Math.exp(-lambda_2*t)));
    double dB_wrt_k2 = (A0*k1/Math.pow(K,2))*(-k/K*(Math.exp(-lambda_1*t) - Math.exp(-lambda_2*t)) - t/2*((K-k)*Math.exp(-lambda_1*t) - (K+k)*Math.exp(-lambda_2*t)));

	double A = (A0/(2*K))*((k+K-2*k1)*Math.exp(-lambda_1*t) - (k-K-2*k1)*Math.exp(-lambda_2*t));
	double B = A0*k1/K*(Math.exp(-lambda_1*t) - Math.exp(-lambda_2*t));
	VolVariable var = ((SensVariable)variable).getVolVariable();

	double alt_comp_sens=0.0;
	SensVariable sensVar = (SensVariable) sensVars.elementAt(0);
	double SBk2 = values[sensVar.getIndex()];
	
	if (variable.getName().equals("sens_C_wrt_k2")) {
		alt_comp_sens = k3*SBk2;
	} else if (variable.getName().equals("sens_B_wrt_k2")) {
		alt_comp_sens = -values[var.getIndex()] - (k2+k3)*SBk2;
	}


	System.out.println("Time Step = " + t);
	System.out.println("Alternate Computed sensitivity for "+variable.getName()+" = "+alt_comp_sens);
    System.out.println("ExactRAte for B_wrt_k2 = " + dSBk2_wrt_t);
    System.out.println("ExactRate for C_wrt_k2 = " + -(dSAk2_wrt_t+dSBk2_wrt_t));
    System.out.println("Exact sens SBk2 = " + dB_wrt_k2);
    System.out.println("Exact sens SCk2 = " + -(dA_wrt_k2+dB_wrt_k2));

   	System.out.println("current solution for "+var.getName()+" = "+values[var.getIndex()]);
	System.out.println("Exact B = "+B);
	System.out.println("Exact C = "+ (K_total-A-B));
    System.out.println("\n"); 
*/
    
	return value;
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param values double[]
 */
public Expression getInitialRateExpression() throws ExpressionException {
	return new Expression(0.0);
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.Constant
 */
public Constant getParameter() {
	return ((SensVariable)variable).getParameter();
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param values double[]
 */
public Expression getRateExpression() throws ExpressionException {
	
	//                         d Fi
	// add rate sensitivity   ------
	//                         d Pj
	//
	Expression exp = new Expression(optimizedRateSensExp);
	
	//
	//  add product of jacobian and sensVars for all k
	//
	//         d Fi     d Ck         d Fi
	//        ------ * ------   =   ------ * Skj
	//         d Ck     d Pj         d Ck
	//
	//               d Ci
	//  where Sij = ------  is the current value of the sensVariable
	//               d Pj
	//
	for (int i=0;i<sensVars.size();i++){
		SensVariable sensVariable = sensVars.elementAt(i);
		Expression exp1 = Expression.mult(optimizedJacobianExps[i], 
				new Expression(SensVariable.getSensName(sensVariable.getVolVariable(), sensVariable.getParameter())));
		exp = Expression.add(exp, exp1);
	}
    
	return exp;
}
/**
 * This method was created in VisualAge.
 * @param rateSensitivity cbit.vcell.math.RateSensitivity
 * @param jacobian cbit.vcell.math.Jacobian
 */
private void init(RateSensitivity rateSensitivity, Jacobian jacobian, SymbolTable symbolTable, Vector<SensVariable> sensVarArray) throws MathException, ExpressionException {
	//
	// order sensVars according to the jacobian (rate index)
	//
	sensVars = (Vector<SensVariable>)sensVarArray.clone();
	for (int i=0;i<sensVarArray.size();i++){
		SensVariable sensVar = sensVarArray.elementAt(i);
		int index = jacobian.getRateIndex(sensVar.getVolVariable());
		sensVars.setElementAt(sensVar,index);
	}
	
	//
	// given the system
	//
	//
	//      d Ci
	//     ------- = Fi(C1..Cn,P1..Pm)
	//      d t
	//
	// where:  C's are variables and P's are parameters
	//
	//jacobian.show();
	VolVariable var = ((SensVariable)variable).getVolVariable();
	Constant parameter = ((SensVariable)variable).getParameter();
	
	//
	// get list of jacobian expressions for this var (Ci)
	//
	//     d Fi
	//    ------   for all j
	//     d Cj
	//
	optimizedJacobianExps = new Expression[jacobian.getNumRates()];
	int currIndex = jacobian.getRateIndex(var);
	
	for (int i=0;i<optimizedJacobianExps.length;i++){
		Expression exp = jacobian.getJexp(currIndex,i);
		exp.bindExpression(symbolTable);
		optimizedJacobianExps[i] = exp.flatten();
	}

	//
	// get rate sensitivity for this variable (Ci) and the parameter under investigation (Pj)
	//
	//    d Fi
	//   ------
	//    d Pj
	//
	//
	Expression exp = rateSensitivity.getCPexp(var,parameter.getName());
	exp.bindExpression(symbolTable);
	optimizedRateSensExp = exp.flatten();
}
}

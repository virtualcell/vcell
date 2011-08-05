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

import java.util.Enumeration;

import cbit.vcell.mapping.FastSystemAnalyzer;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.PseudoConstant;
import cbit.vcell.math.Variable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:57:23 PM)
 * @author: John Wagner
 */
public class FastAlgebraicSystem extends AlgebraicSystem {
	private FastSystemAnalyzer fs_analyzer = null;
	
	private Expression matrixExps[] = null;
/**
 * AlgebraicSystemExample constructor comment.
 * @param dimension int
 */
public FastAlgebraicSystem(FastSystemAnalyzer argFS_Analyzer) throws MathException, ExpressionException {
	super(argFS_Analyzer.getNumIndependentVariables());
	this.fs_analyzer = argFS_Analyzer;
	initMatrixExpressions();
}
/**
 * This method was created in VisualAge.
 * @param i int
 * @param dx double
 */
protected void addX(double oldValues[], double newValues[], int i, double dx) throws ExpressionException {
	int varIndex = fs_analyzer.getIndependentVariable(i).getIndex();
	newValues[varIndex] = oldValues[varIndex] + dx;
//	independentVars[i].setCurrValue(independentVars[i].getValue()+dx);
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param i int
 */
protected double getX(double values[], int i) throws ExpressionException {
	int varIndex = fs_analyzer.getIndependentVariable(i).getIndex();
	return values[varIndex];
//	return independentVars[i].getCurrValue();
}
/**
 * updateMatrix method comment.
 */
protected void initMatrixExpressions() throws MathException, ExpressionException {
	int frCount=0;
	int numIV = fs_analyzer.getNumIndependentVariables();
	int numDV = fs_analyzer.getNumDependentVariables();
	
	//
	// initialize independentVariables and matrix expressions
	//
	matrixExps = new Expression[numIV*(numIV+1)];
	Enumeration enum_fre = fs_analyzer.getFastRateExpressions();
	while (enum_fre.hasMoreElements()){
		Expression fastRateExpression = (Expression)enum_fre.nextElement();
		int varCount=0;
		Enumeration enum_var = fs_analyzer.getIndependentVariables();
		while (enum_var.hasMoreElements()){
			Variable var = (Variable)enum_var.nextElement();
			//System.out.println("FastAlgebraicSystem.initMatrixExpressions(), rate["+frCount+"] = "+fastRateExpression.toString());
			Expression exp = MathUtilities.substituteFunctions(fastRateExpression, fs_analyzer);
			//System.out.println("FastAlgebraicSystem.initMatrixExpressions(), flattened = "+exp.toString());
			Expression differential = exp.differentiate(var.getName());
			differential.bindExpression(fs_analyzer);
			differential = differential.flatten();
			//System.out.println("FastAlgebraicSystem.initMatrixExpressions(), d/d["+var.getName()+"] = "+differential.toString());
			matrixExps[frCount + numIV*varCount] = differential;
			varCount++;
		}

		Expression exp = Expression.negate(fastRateExpression);
		exp = MathUtilities.substituteFunctions(exp, fs_analyzer);
		exp.bindExpression(fs_analyzer);
		matrixExps[frCount + numIV*varCount] = exp.flatten();
		matrixExps[frCount + numIV*varCount].bindExpression(fs_analyzer);

		frCount++;
	}
///*
System.out.println("\n\nFastAlgebraicSolver.initMatrixExpressions(),  matrix = ");
for (int row=0;row<numIV;row++){
	for (int col=0;col<=numIV;col++){
		System.out.println(row+","+col+") "+matrixExps[row + numIV*col]);
	}
}
System.out.println("\n\n");

}

public Enumeration<PseudoConstant> getPseudoConstants() {
	return fs_analyzer.getPseudoConstants();
}


/**
 * initVars method comment.
 */
public void initVars(double oldValues[], double newValues[]) throws ExpressionException, MathException {
	//
	// calculate C's for invariants
	//
	Enumeration<PseudoConstant> enum_pc = fs_analyzer.getPseudoConstants();
	while (enum_pc.hasMoreElements()) {
		PseudoConstant pc = enum_pc.nextElement();
		int index = pc.getIndex();
		oldValues[index] = pc.getPseudoExpression().evaluateVector(oldValues);
		newValues[index] = oldValues[index];
	}	
}
/**
 * This method was created in VisualAge.
 */
protected void updateDependentVars(double newValues[]) throws ExpressionException {
	Enumeration<Variable> enum_depVars = fs_analyzer.getDependentVariables();
	Enumeration<Expression> dependencyExpEnum = fs_analyzer.getDependencyExps();
	while (enum_depVars.hasMoreElements()) {
		Variable dependentVar = enum_depVars.nextElement();
		Expression depExpr = dependencyExpEnum.nextElement();
		int index = dependentVar.getIndex();
		newValues[index] = depExpr.evaluateVector(newValues);
	}
}
/**
 * updateMatrix method comment.
 */
protected void updateMatrix(double oldValues[], double newValues[]) throws ExpressionException {

	int numIV = getDimension();
	for (int i=0;i<numIV;i++){
		int varIndex = fs_analyzer.getIndependentVariable(i).getIndex();
		oldValues[varIndex] = newValues[varIndex];
		//	independentVars[i].update();
	}
	for (int frCount=0;frCount<numIV;frCount++){
		int varCount;
		for (varCount=0;varCount<numIV;varCount++){
			Expression exp = matrixExps[frCount + numIV*varCount];
			double value = exp.evaluateVector(oldValues);
			setMatrixA(frCount,varCount,value);
		}
		Expression exp = matrixExps[frCount + numIV*numIV];
		setVectorB(frCount,exp.evaluateVector(oldValues));
	}

}
}

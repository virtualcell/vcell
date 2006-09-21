package cbit.vcell.solver.ode;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
import cbit.vcell.math.*;
import cbit.vcell.parser.*;
import cbit.vcell.simulation.Simulation;
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:57:23 PM)
 * @author: John Wagner
 */
public class FastAlgebraicSystem extends AlgebraicSystem {
	private Simulation simulation = null;
	private FastSystem fastSystem = null;
	
	private Expression matrixExps[] = null;
	private Variable independentVars[] = null;
	private Variable dependentVars[] = null;
	private PseudoConstant pseudoConstants[] = null;
	private Expression dependencyExps[] = null;
/**
 * AlgebraicSystemExample constructor comment.
 * @param dimension int
 */
public FastAlgebraicSystem(Simulation argSimulation, FastSystem fastSystem) throws MathException, ExpressionException {
	super(fastSystem.getNumIndependentVariables());
	this.fastSystem = fastSystem;
	this.simulation = argSimulation;
	initMatrixExpressions();
}
/**
 * This method was created in VisualAge.
 * @param i int
 * @param dx double
 */
protected void addX(double oldValues[], double newValues[], int i, double dx) throws ExpressionException {
	int varIndex = independentVars[i].getIndex();
	newValues[varIndex] = oldValues[varIndex] + dx;
//	independentVars[i].setCurrValue(independentVars[i].getValue()+dx);
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param i int
 */
protected double getX(double values[], int i) throws ExpressionException {
	int varIndex = independentVars[i].getIndex();
	return values[varIndex];
//	return independentVars[i].getCurrValue();
}
/**
 * updateMatrix method comment.
 */
protected void initMatrixExpressions() throws MathException, ExpressionException {
	int frCount=0;
	int numIV = fastSystem.getNumIndependentVariables();
	int numDV = fastSystem.getNumDependentVariables();
	
	//
	// initialize independentVariables and matrix expressions
	//
	matrixExps = new Expression[numIV*(numIV+1)];
	independentVars = new Variable[numIV];
	Enumeration enum_fre = fastSystem.getFastRateExpressions();
	while (enum_fre.hasMoreElements()){
		Expression fastRateExpression = (Expression)enum_fre.nextElement();
		int varCount=0;
		Enumeration enum_var = fastSystem.getIndependentVariables();
		while (enum_var.hasMoreElements()){
			Variable var = (Variable)enum_var.nextElement();
			independentVars[varCount] = var;
//System.out.println("FastAlgebraicSystem.initMatrixExpressions(), rate["+frCount+"] = "+fastRateExpression.toString());
			Expression exp = simulation.substituteFunctions(fastRateExpression);
//System.out.println("FastAlgebraicSystem.initMatrixExpressions(), flattened = "+exp.toString());
			Expression differential = exp.differentiate(var.getName());
			differential.bindExpression(simulation);
			differential = differential.flatten();
//System.out.println("FastAlgebraicSystem.initMatrixExpressions(), d/d["+var.getName()+"] = "+differential.toString());
			matrixExps[frCount + numIV*varCount] = differential;
			varCount++;
		}

		Expression exp = Expression.negate(fastRateExpression);
		exp = simulation.substituteFunctions(exp);
		exp.bindExpression(simulation);
		matrixExps[frCount + numIV*varCount] = exp.flatten();
		matrixExps[frCount + numIV*varCount].bindExpression(simulation);

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
//*/
	//
	// initialize pseudoConstants and dependency expressions
	//
	pseudoConstants = new PseudoConstant[numDV];
	dependencyExps = new Expression[numDV];
	dependentVars = new Variable[numDV];
	int pcCount = 0;
	Enumeration enum_pc = fastSystem.getPseudoConstants();
	Enumeration enum_de = fastSystem.getDependencyExps();
	Enumeration enum_dv = fastSystem.getDependentVariables();
	while (enum_pc.hasMoreElements()){
		PseudoConstant pc = (PseudoConstant)enum_pc.nextElement();
		pseudoConstants[pcCount] = pc;
		pc.getPseudoExpression().bindExpression(simulation);

		Expression de = (Expression)enum_de.nextElement();
		dependencyExps[pcCount] = de;
		de.bindExpression(simulation);

		dependentVars[pcCount] = (Variable)enum_dv.nextElement();
		
		pcCount++;
	}
}
/**
 * initVars method comment.
 */
public void initVars(double oldValues[], double newValues[]) throws ExpressionException, MathException {

//System.out.println("\n\ninitial ");
//for (int i=0;i<getDimension();i++){
//System.out.println("X["+i+"] = "+vars[i].getName()+" = "+getX(i));
//}
	//
	// calculate C's for invariants
	//
	for (int i=0;i<fastSystem.getNumPseudoConstants();i++){
		int index = pseudoConstants[i].getIndex();
		oldValues[index] = pseudoConstants[i].getPseudoExpression().evaluateVector(oldValues);
		newValues[index] = oldValues[index];
//		pseudoConstants[i].setCurrValue(pseudoConstants[i].getPseudoExpression().evaluateVector(values));
//		pseudoConstants[i].update();
	}	
}
/**
 * This method was created in VisualAge.
 */
protected void updateDependentVars(double newValues[]) throws ExpressionException {
	for (int i=0;i<dependentVars.length;i++){
		int index = dependentVars[i].getIndex();
		newValues[index] = dependencyExps[i].evaluateVector(newValues);
	}
}
/**
 * updateMatrix method comment.
 */
protected void updateMatrix(double oldValues[], double newValues[]) throws ExpressionException {

	int numIV = getDimension();
	for (int i=0;i<numIV;i++){
		int varIndex = independentVars[i].getIndex();
		oldValues[varIndex] = newValues[varIndex];
//		independentVars[i].update();
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

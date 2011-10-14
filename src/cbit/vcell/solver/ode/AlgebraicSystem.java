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

import Jama.*;
import cbit.vcell.parser.*;
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:56:17 PM)
 * @author: John Wagner
 */
public abstract class AlgebraicSystem {
	private Matrix matrixA = null;
	private Matrix matrixB = null;
	private double varIncrements[] = null;

	private double tolerance = 1E-7;
	private int dimension = 0;
/**
 * AlgebraicSystem constructor comment.
 */
protected AlgebraicSystem(int dimension) {
	this.dimension = dimension;
	matrixA = new Matrix(dimension,dimension);
	matrixB = new Matrix(dimension,1);
	varIncrements = new double[dimension];
}
/**
 * This method was created in VisualAge.
 * @param i int
 * @param value double
 */
protected abstract void addX(double oldValues[], double newValues[], int i, double value) throws ExpressionException;
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getDimension() {
	return dimension;
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param index int
 */
protected abstract double getX(double values[], int index) throws ExpressionException;
/**
 * This method was created in VisualAge.
 */
public abstract void initVars(double oldValues[], double newValues[]) throws Exception;
/**
 * This method was created in VisualAge.
 * @param i int
 * @param j int
 * @param value double
 */
protected void setMatrixA(int i, int j, double value) {
	matrixA.set(i,j,value);
}
/**
 * This method was created in VisualAge.
 * @param tolerance double
 */
public void setTolerance(double tolerance) {
	this.tolerance = tolerance;
}
/**
 * This method was created in VisualAge.
 * @param i int
 * @param j int
 * @param value double
 */
protected void setVectorB(int i, double value) {
	matrixB.set(i,0,value);
}
/**
 * This method was created in VisualAge.
 */
public void solveSystem(double oldValues[], double newValues[]) throws ExpressionException {

	int MAX_ITERATIONS = 100;
	double diff = 0;

	
	for (int count=0;count<MAX_ITERATIONS;count++) {
		diff = 0.0;

		//
		// update non-linear terms in matrix
		//
		updateMatrix(oldValues,newValues);
		//
		// solve for dx
		//
//matrix.show();
		double varIncrements[] = matrixA.solve(matrixB).getColumnPackedCopy();

		//
		// update x
		//
		for (int i=0;i<dimension;i++){
			addX(oldValues,newValues,i,varIncrements[i]);
			double valueX = getX(newValues,i);
			if (Math.abs(valueX)>1e-8){
				diff += Math.abs(varIncrements[i]/getX(newValues,i));
			}else{
				diff += Math.abs(varIncrements[i]);
			}
		}
//System.out.println("iteration "+count);
//for (int i=0;i<getDimension();i++){
//System.out.println("X["+i+"] = "+getX(newValues,i));
//}
		if (diff < tolerance) {
			updateDependentVars(newValues);
			return;
		}
	}
	
	//
	// didn't converge, throw exception
	//	
	throw new RuntimeException("Iterations didn't converge, diff="+diff+", tol="+tolerance);
}
/**
 * This method was created in VisualAge.
 */
protected abstract void updateDependentVars(double newValues[]) throws ExpressionException;
/**
 * This method was created in VisualAge.
 */
protected abstract void updateMatrix(double oldValues[], double newValues[]) throws cbit.vcell.parser.ExpressionException;
}

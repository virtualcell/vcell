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

import cbit.vcell.math.Variable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 9:00:31 PM)
 * @author: John Wagner
 */
public abstract class StateVariable {
	private final static int DEFAULT_DATA_SIZE = 100;
	protected Variable variable = null;
	private double fieldData[] = null;
	private int fieldDataCount = 0;
/**
 * TimeSeriesData constructor comment.
 */
protected StateVariable(Variable var) {
	this.variable = var;
}
/**
 * This method was created in VisualAge.
 * @param saveIndex int
 */
public void addData(double value) {
	if (fieldData == null || getDataCount() <= 0) {
		fieldData = new double[DEFAULT_DATA_SIZE];
		fieldDataCount = 0;
	} else if (getDataCount() >= fieldData.length) {
		double[] data = fieldData;
		fieldData = new double [fieldDataCount];
		for (int i = 0; i < data.length; i++) fieldData[i] = data[i];
	}
	fieldData[fieldDataCount++] = value;
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param values double[]
 */
public abstract double evaluateIC(double values[]) throws ExpressionException;
/**
 * This method was created in VisualAge.
 * @return double
 * @param values double[]
 */
public abstract double evaluateRate(double values[]) throws ExpressionException;
/**
 * This method was created in VisualAge.
 * @return double[]
 */
public double[] getData() {
	return fieldData;
}
/**
 * This method was created in VisualAge.
 * @return double[]
 */
public int getDataCount() {
	return fieldDataCount;
}
/**
 * This method was created in VisualAge.
 * @return double[]
 */
public abstract Expression getInitialRateExpression() throws ExpressionException;
/**
 * This method was created in VisualAge.
 * @return double[]
 */
public abstract Expression getRateExpression() throws ExpressionException ;
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.Variable
 */
public Variable getVariable() {
	return variable;
}
}

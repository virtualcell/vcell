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

import cbit.vcell.math.MathException;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.SimulationSymbolTable;
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:59:44 PM)
 * @author: John Wagner
 */
public class ODEStateVariable extends StateVariable {
	private Expression optimizedRateExp = null;
	private Expression initialExp = null;
/**
 * TimeSeriesData constructor comment.
 */
public ODEStateVariable(OdeEquation ode, SimulationSymbolTable simSymbolTable) throws ExpressionException, MathException {
	super(ode.getVariable());
	ode.bind(simSymbolTable);
	optimizedRateExp = ode.getFlattenedRateExpression(simSymbolTable);
	initialExp = ode.getInitialExpression();
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param values double[]
 */
public double evaluateIC(double values[]) throws ExpressionException {
	return initialExp.evaluateVector(values);
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param values double[]
 */
public double evaluateRate(double values[]) throws ExpressionException {
	return optimizedRateExp.evaluateVector(values);
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.Variable
 */
public Expression getInitialRateExpression() throws ExpressionException {
	return initialExp;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.Variable
 */
public Expression getRateExpression() throws ExpressionException {
	return optimizedRateExp;
}
}

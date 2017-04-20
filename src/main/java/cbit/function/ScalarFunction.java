/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.function;
/**
 * Insert the type's description here.
 * Creation date: (4/2/2002 1:26:52 PM)
 * @author: Michael Duff
 */
public interface ScalarFunction {
/**
 * Insert the method's description here.
 * Creation date: (4/3/2002 4:45:49 PM)
 * @return double[][]
 * @param x double[]
 * @exception cbit.vcell.parser.ExpressionException The exception description.
 */
double[] evaluateGradient(double[] x);
	double f(double[] x);
/**
 * Insert the method's description here.
 * Creation date: (4/3/2002 4:06:04 PM)
 * @return int
 */
int getNumArgs();
}

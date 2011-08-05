/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.test;

/**
 * Insert the type's description here.
 * Creation date: (1/16/2003 2:14:07 PM)
 * @author: Jim Schaff
 */
public interface ODESolution {
/**
 * Insert the method's description here.
 * Creation date: (1/16/2003 2:15:26 PM)
 * @return double
 * @param name java.lang.String
 * @param x double
 * @param y double
 * @param z double
 * @param t double
 */
TimeSeriesSample getSamples(String name) throws cbit.vcell.parser.ExpressionException;
}

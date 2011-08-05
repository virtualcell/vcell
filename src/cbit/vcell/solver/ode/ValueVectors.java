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
import cbit.vcell.math.*;
/**
 * Insert the class' description here.
 * This holds the values of:
 *     0-3: reserved variables 
 *     4-n: state variables, and constant expressions
 * Creation date: (8/19/2000 9:00:46 PM)
 * @author: John Wagner
 */
public class ValueVectors {
	private Vector fieldValues = new Vector();  // vector of type double[]
/**
 * StateVector constructor comment.
 */
public ValueVectors(int numVectors, int vectorSize) {
	for (int i = 0; i < numVectors; i++) {
		fieldValues.addElement(new double[vectorSize]);
	}
}
/**
 * This method was created in VisualAge.
 * @param source int
 * @param dest int
 */
public void copyValues(int from, int to) {
	double source[] = getValues(from);
	double dest[] = getValues(to);
	// cbit.util.Assertion.assert(source.length == dest.length);
	for (int i = 0; i < source.length; i++) {
		dest[i] = source[i];
	}
}
/**
 * This method was created in VisualAge.
 * @param source int
 * @param dest int
 */
public void copyValuesDown() {
	for (int i = 0; i < getValueCount() - 1; i++) {
		double[] d = getValues(i);
		double[] s = getValues(i+1);
		// cbit.util.Assertion.assert(s.length == d.length);
		for (int j = 0; j < d.length; j++) d[j] = s[j];
	}
}
/**
 * This method was created in VisualAge.
 * @param source int
 * @param dest int
 */
public void copyValuesUp() {
	for (int i = getValueCount() - 1; i > 0; i--) {
		double[] d = getValues(i);
		double[] s = getValues(i-1);
		// cbit.util.Assertion.assert(s.length == d.length);
		for (int j = 0; j < d.length; j++) d[j] = s[j];
	}
}
/**
 * This method was created in VisualAge.
 * @return double[]
 */
public int getValueCount() {
	return fieldValues.size();
}
/**
 * This method was created in VisualAge.
 * @return double[]
 */
public double[] getValues(int listIndex) {
	return (double[]) fieldValues.elementAt(listIndex);
}
/**
 * This method was created in VisualAge.
 * @param vars cbit.vcell.math.Variable[]
 */
public void show(Variable vars[], int vectorIndex) {
	double values[] = getValues(vectorIndex);
	if (vars.length != values.length) {
		throw new IllegalArgumentException("variable array length doesn't match vector length");
	}
	System.out.println("values for the " + vectorIndex + "th vector");
	for (int i = 0; i < values.length; i++) {
		String name = vars[i].getName();
		while (name.length() < 30) {
			name = " " + name;
		}
		System.out.println(name + " = " + values[i]);
	}
}
}

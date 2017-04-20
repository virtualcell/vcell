/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math;

import cbit.vcell.parser.Expression;
/**
 * Insert the type's description here.
 * Creation date: (1/11/2001 10:28:59 AM)
 * @author: John Wagner
 */
public class FunctionColumnDescription implements cbit.vcell.util.ColumnDescription, java.io.Serializable {
	private java.lang.String fieldDisplayName = new String();
	private java.lang.String fieldFunctionName = new String();
	private java.lang.String fieldParameterName = null;
	private boolean fieldIsUserDefined = false;
	private Expression fieldExpression = null;
/**
 * ODESolverResultSetColDescription constructor comment.
 */
public FunctionColumnDescription(Expression exp, String functionName, String parameterName, String displayName, boolean isUserDefined) {
	super();
	// cbit.util.Assertion.assertNotNull(functionName);
	// cbit.util.Assertion.assertNotNull(displayName);
	// cbit.util.Assertion.assertNotNull(exp);
	fieldFunctionName = functionName;
	fieldParameterName = parameterName;
	fieldDisplayName = displayName;
	fieldIsUserDefined = isUserDefined;
	fieldExpression = new Expression(exp);
}
/**
 * ODESolverResultSetColDescription constructor comment.
 */
public FunctionColumnDescription(FunctionColumnDescription funcColumnDescription) {
	super();
	fieldFunctionName = funcColumnDescription.fieldFunctionName;
	fieldParameterName = funcColumnDescription.fieldParameterName;
	fieldDisplayName = funcColumnDescription.fieldDisplayName;
	fieldExpression = new Expression(funcColumnDescription.fieldExpression);
}
/**
 * Gets the displayName property (java.lang.String) value.
 * @return The displayName property value.
 * @see #setDisplayName
 */
public java.lang.String getDisplayName() {
	return fieldDisplayName;
}
/**
 * Gets the variableName property (java.lang.String) value.
 * @return The variableName property value.
 * @see #setVariableName
 */
public Expression getExpression() {
	return fieldExpression;
}
/**
 * Gets the variableName property (java.lang.String) value.
 * @return The variableName property value.
 * @see #setVariableName
 */
public java.lang.String getFunctionName() {
	return fieldFunctionName;
}
/**
 * Gets the parameterName property (java.lang.String) value.
 * @return The name of the sensitivity parameter, may be null.
 * @see #setParameterName
 */
public boolean getIsUserDefined() {
	return fieldIsUserDefined;
}
/**
 * Gets the variableName property (java.lang.String) value.
 * @return The variableName property value.
 * @see #setVariableName
 */
public java.lang.String getName() {
	return (getFunctionName());
}
/**
 * Gets the parameterName property (java.lang.String) value.
 * @return The name of the sensitivity parameter, may be null.
 * @see #setParameterName
 */
public java.lang.String getParameterName() {
	return fieldParameterName;
}
/**
 * Generates a hash code for the receiver.
 * This method is supported primarily for
 * hash tables, such as those provided in java.util.
 * @return an integer hash code for the receiver
 * @see java.util.Hashtable
 */
public int hashCode() {
	// Insert code to generate a hash code for the receiver here.
	// This implementation forwards the message to super.  You may replace or supplement this.
	// NOTE: if two objects are equal (equals(Object) returns true) they must have the same hash code
	return getDisplayName().hashCode();
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		FunctionColumnDescription test = new FunctionColumnDescription (new Expression("abc/123"), "function", "parameter", "display", false);
		System.out.println("Test Not : " +
			">" + test.getFunctionName() + "<" +
			">" + test.getParameterName() + "<" +
			">" + test.getDisplayName() + "<" +
			">" + test.toString() + "<");
		System.out.println("Test One : " +
			">" + test.getFunctionName() + "<" +
			">" + test.getParameterName() + "<" +
			">" + test.getDisplayName() + "<" +
			">" + test.toString() + "<");
		System.out.println("Test Two : " +
			">" + test.getFunctionName() + "<" +
			">" + test.getParameterName() + "<" +
			">" + test.getDisplayName() + "<" +
			">" + test.toString() + "<");
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/16/2003 5:45:33 PM)
 * @param newFieldExpression cbit.vcell.parser.Expression
 */
public void setExpression(cbit.vcell.parser.Expression newExpression) {
	fieldExpression = newExpression;
}
/**
 * Returns a String that represents the value of this object.
 * @return a string representation of the receiver
 */
public String toString() {
	return (getDisplayName());
}
}

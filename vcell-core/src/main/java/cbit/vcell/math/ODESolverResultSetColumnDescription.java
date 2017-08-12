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

/**
 * Insert the type's description here.
 * Creation date: (1/11/2001 10:28:59 AM)
 * @author: John Wagner
 */
public class ODESolverResultSetColumnDescription implements cbit.vcell.util.ColumnDescription, java.io.Serializable {
	private java.lang.String fieldDisplayName = new String();
	private java.lang.String fieldVariableName = new String();
	private java.lang.String fieldParameterName = null;
/**
 * ODESolverResultSetColDescription constructor comment.
 */
public ODESolverResultSetColumnDescription(ODESolverResultSetColumnDescription odeColumnDescription) {
	super();
	fieldVariableName = odeColumnDescription.fieldVariableName;
	fieldParameterName = odeColumnDescription.fieldParameterName;
	fieldDisplayName = odeColumnDescription.fieldDisplayName;
}
/**
 * ODESolverResultSetColDescription constructor comment.
 */
public ODESolverResultSetColumnDescription(String variableName) {
	this(variableName, variableName);
}
/**
 * ODESolverResultSetColDescription constructor comment.
 */
public ODESolverResultSetColumnDescription(String variableName, String displayName) {
	this(variableName, null, displayName);
}
/**
 * ODESolverResultSetColDescription constructor comment.
 */
public ODESolverResultSetColumnDescription(String variableName, String parameterName, String displayName) {
	super();
	// cbit.util.Assertion.assertNotNull(variableName);
	// cbit.util.Assertion.assertNotNull(displayName);
	fieldVariableName = variableName;
	fieldParameterName = parameterName;
	fieldDisplayName = displayName;
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
public java.lang.String getName() {
	return (getVariableName());
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
 * Gets the variableName property (java.lang.String) value.
 * @return The variableName property value.
 * @see #setVariableName
 */
public java.lang.String getVariableName() {
	return fieldVariableName;
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
	// Insert code to start the application here.
	ODESolverResultSetColumnDescription testNot = new ODESolverResultSetColumnDescription ("variable");
	ODESolverResultSetColumnDescription testOne = new ODESolverResultSetColumnDescription ("variable", "display");
	ODESolverResultSetColumnDescription testTwo = new ODESolverResultSetColumnDescription ("variable", "parameter", "display");
	System.out.println("Test Not : " +
		">" + testNot.getVariableName() + "<" +
		">" + testNot.getParameterName() + "<" +
		">" + testNot.getDisplayName() + "<" +
		">" + testNot.toString() + "<");
	System.out.println("Test One : " +
		">" + testOne.getVariableName() + "<" +
		">" + testOne.getParameterName() + "<" +
		">" + testOne.getDisplayName() + "<" +
		">" + testOne.toString() + "<");
	System.out.println("Test Two : " +
		">" + testTwo.getVariableName() + "<" +
		">" + testTwo.getParameterName() + "<" +
		">" + testTwo.getDisplayName() + "<" +
		">" + testTwo.toString() + "<");

}
/**
 * Returns a String that represents the value of this object.
 * @return a string representation of the receiver
 */
public String toString() {
	return (getDisplayName());
}
}

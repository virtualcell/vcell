package cbit.vcell.solver.stoch;
/**
 * Insert the type's description here.
 * Creation date: (8/2/2006 3:59:45 PM)
 * @author: Tracy LI
 */
public class StochSolverResultSetColumnDescription implements cbit.vcell.util.ColumnDescription, java.io.Serializable {
	private java.lang.String fieldDisplayName = new String();
	private java.lang.String fieldVariableName = new String();
	private java.lang.String fieldParameterName = null;
/**
 * StochSolverResultSetColDescription constructor comment.
 */
public StochSolverResultSetColumnDescription(StochSolverResultSetColumnDescription stochColumnDescription) {
	super();
	fieldVariableName = stochColumnDescription.fieldVariableName;
	fieldParameterName = stochColumnDescription.fieldParameterName;
	fieldDisplayName = stochColumnDescription.fieldDisplayName;
}
/**
 * StochSolverResultSetColDescription constructor comment.
 */
public StochSolverResultSetColumnDescription(String variableName) {
	this(variableName, variableName);
}
/**
 * StochSolverResultSetColDescription constructor comment.
 */
public StochSolverResultSetColumnDescription(String variableName, String displayName) {
	this(variableName, null, displayName);
}
/**
 * StochSolverResultSetColDescription constructor comment.
 */
public StochSolverResultSetColumnDescription(String variableName, String parameterName, String displayName) {
	super();
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
/* uncomment it when testing is needed
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
*/ 
/**
 * Returns a String that represents the value of this object.
 * @return a string representation of the receiver
 */
public String toString() {
	return (getDisplayName());
}	

/**
 * StochSolverResultSetColumnDescription constructor comment.
 */
public StochSolverResultSetColumnDescription() {
	super();
}
}
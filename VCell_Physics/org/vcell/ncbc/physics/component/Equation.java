package org.vcell.ncbc.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (1/13/2004 4:20:21 PM)
 * @author: Jim Schaff
 */
public class Equation {
	private String expressionString = null;

/**
 * Equation constructor comment.
 */
public Equation(String argExpressionString) {
	super();
	this.expressionString = argExpressionString;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 4:21:16 PM)
 * @return java.lang.String
 */
public String getExpressionString() {
	return expressionString;
}
}
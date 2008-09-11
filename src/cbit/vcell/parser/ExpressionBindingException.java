package cbit.vcell.parser;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class ExpressionBindingException extends ExpressionException {

	private String identifier = null;
	
public ExpressionBindingException() {
	super();
}
/**
 * ExpressionBindingException constructor comment.
 * @param s java.lang.String
 */
public ExpressionBindingException(String s) {
	super(s);
}

public ExpressionBindingException(String s, String argIdentifier) {
	super(s);
	identifier = argIdentifier;
}
public String getIdentifier() {
	return identifier;
}

}

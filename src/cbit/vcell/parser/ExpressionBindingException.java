/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.parser;

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

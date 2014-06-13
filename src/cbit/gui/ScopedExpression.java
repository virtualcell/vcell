/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui;

import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.NameScope;

/**
 * Insert the type's description here.
 * Creation date: (9/2/2003 3:26:19 PM)
 * @author: Jim Schaff
 */
public class ScopedExpression {
	private Expression fieldRenamedExpression = null;
	private NameScope fieldNameScope = null;
	private boolean bValidateIdentifierBinding = true;
	private boolean bValidateFunctionBinding = true;
	private AutoCompleteSymbolFilter autoCompleteSymbolFilter = null;
	final private String fieldRenamedExpressionInfix;
	private ExpressionBindingException expressionBindingException = null;
	
/**
 * ContextualExpression constructor comment.
 * @throws ExpressionBindingException 
 */
public ScopedExpression(Expression argExpression, NameScope argNameScope) throws ExpressionBindingException {
	this(argExpression,argNameScope,true, true, null);
}

/**
 * ContextualExpression constructor comment.
 * @throws ExpressionBindingException 
 */
public ScopedExpression(Expression argExpression, NameScope argNameScope, boolean argValidateIdentifierBinding, boolean argValidateFunctionBinding, AutoCompleteSymbolFilter stef) {
	super();
	if (argExpression == null) {
		throw new RuntimeException("Expression cannot be null");
	}
	this.fieldNameScope = argNameScope;
	this.bValidateIdentifierBinding = argValidateIdentifierBinding;
	this.bValidateFunctionBinding = argValidateFunctionBinding;
	autoCompleteSymbolFilter = stef;
	this.fieldRenamedExpression = argExpression;
	if (fieldNameScope != null) {
		try {
			this.fieldRenamedExpression = argExpression.renameBoundSymbols(fieldNameScope);
		} catch (ExpressionBindingException e) {
			expressionBindingException = e;
			e.printStackTrace(System.out);
		}
	}
	fieldRenamedExpressionInfix = fieldRenamedExpression.infix();
}

/**
 * Insert the method's description here.
 * Creation date: (9/2/2003 3:27:41 PM)
 * @return cbit.vcell.parser.Expression
 */
public Expression getRenamedExpression() {
	return fieldRenamedExpression;
}
/**
 * Insert the method's description here.
 * Creation date: (9/2/2003 3:28:22 PM)
 * @return cbit.vcell.parser.NameScope
 */
public NameScope getNameScope() {
	return fieldNameScope;
}
/**
 * Insert the method's description here.
 * Creation date: (9/2/2003 5:39:29 PM)
 * @return java.lang.String
 */
public String infix() {
	return fieldRenamedExpressionInfix;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2004 3:02:27 PM)
 * @return boolean
 */
public boolean isValidateIdentifierBinding() {
	return bValidateIdentifierBinding;
}

public boolean isValidateFunctionBinding() {
	return bValidateFunctionBinding;
}
/**
 * Insert the method's description here.
 * Creation date: (9/2/2003 5:39:29 PM)
 * @return java.lang.String
 */
public String toString() {
	return infix();
}
public final AutoCompleteSymbolFilter getAutoCompleteSymbolFilter() {
	return autoCompleteSymbolFilter;
}
public final ExpressionBindingException getExpressionBindingException() {
	return expressionBindingException;
}
}

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
 * Insert the type's description here.
 * Creation date: (2/7/2005 4:13:57 PM)
 * @author: Anuradha Lakshminarayana
 */
public class LambdaFunction {
	private java.lang.String functionName;
	private cbit.vcell.parser.Expression functionExpression;
	private java.lang.String[] functionArgs;
/**
 * LambdaFunction constructor comment.
 */
public LambdaFunction(String argName, Expression argExpression, String[] argFunctionArgs) {
	super();
	functionName = argName;
	functionExpression = argExpression;
	functionArgs = argFunctionArgs;
}
/**
 * Insert the method's description here.
 * Creation date: (2/7/2005 4:26:25 PM)
 * @return cbit.vcell.parser.Expression
 */
public cbit.vcell.parser.Expression getExpression() {
	return functionExpression;
}
/**
 * Insert the method's description here.
 * Creation date: (2/7/2005 4:28:26 PM)
 * @return java.lang.String[]
 */
public java.lang.String[] getFunctionArgs() {
	return functionArgs;
}
/**
 * Insert the method's description here.
 * Creation date: (2/7/2005 4:24:44 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return functionName;
}
/**
 * Insert the method's description here.
 * Creation date: (2/9/2005 11:54:00 AM)
 * @param e1 cbit.vcell.parser.Expression
 * @param e2 cbit.vcell.parser.Expression
 */
public Expression substitute(Expression[] argExps) {
	Expression substitutedFnExpression = new Expression(functionExpression);

	if (functionArgs.length != argExps.length) {
		throw new RuntimeException("# of arguments to the lambda function and their expressions are not equal!");
	}

	try {
		for (int i = 0; i < argExps.length; i++){
			Expression fnArgExpr = new Expression(functionArgs[i]);
			substitutedFnExpression.substituteInPlace(fnArgExpr, argExps[i]);
		}
	} catch (ExpressionException e) {
		throw new RuntimeException(e.getMessage());
	}

	return substitutedFnExpression;
}
}

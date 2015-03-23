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
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;
/**
 * The class is tentatively used to store variable's initial value
 * for stochastic simulation. A better solution would be adding one
 * more attribute in class Variable to store it's initial value.
 * It takes Variable and initial value(expression) as input parameters.
 * Creation date: (6/27/2006 9:26:32 AM)
 * @author: Tracy LI
 */
public abstract class VarIniCondition implements org.vcell.util.Matchable,java.io.Serializable
{
	private Variable var = null;
	private Expression iniValue = null;

/**
 * VarIniCondition constructor comment.
 */
public VarIniCondition(Variable arg_var, Expression arg_iniVal)
{
	var = arg_var;
	iniValue = arg_iniVal;
}


/**
 * Bind symtoltable to the initial value expression.
 * Creation date: (6/27/2006 9:47:12 AM)
 * @param symbolTable SymbolTable
 */
public void bindExpression(SymbolTable symbolTable) throws ExpressionBindingException
{
	iniValue.bindExpression(symbolTable);
}

/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
protected boolean compareEqual0(VarIniCondition varIniCondition)
{
	if(!iniValue.compareEqual(varIniCondition.iniValue) ) return false;//initial value
	if(!var.compareEqual(varIniCondition.getVar())) return false; //variable
	
	return true;
}

/**
 * Get variable initial value represented by an expression.
 * Creation date: (6/27/2006 9:42:50 AM)
 * @return cbit.vcell.parser.Expression
 */
public Expression getIniVal() {
	return iniValue;
}


/**
 * Get the variable.
 * Creation date: (6/27/2006 9:42:50 AM)
 * @return cbit.vcell.math.Variable
 */
public Variable getVar() {
	return var;
}


/**
 * Insert the method's description here.
 * Creation date: (7/6/2006 5:42:05 PM)
 * @return java.lang.String
 */
public abstract String getVCML();

/**
 * Insert the method's description here.
 * Creation date: (9/28/2006 3:05:32 PM)
 * @return java.lang.String
 */
public String toString() {
	StringBuffer buffer = new StringBuffer();
	String initialValue = getIniVal().infix(); // display the constant/function name only
	
	buffer.append(getVar().getName()+" = "+initialValue);
	return buffer.toString();
}


public void flatten(MathSymbolTable mathSymbolTable, boolean bRoundCoefficients) throws ExpressionException, MathException {
	iniValue = Equation.getFlattenedExpression(mathSymbolTable, iniValue, bRoundCoefficients);
}
}

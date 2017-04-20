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

import java.util.Set;

/**
 * A <code>SymbolTableEntry</code> describes an interface for any object that
 * can represent the value corresponding to a symbol in an expression.  
 * A <code>SymbolTableEntry</code> is returned by a <code>SymbolTable</code>
 * to resolve the reference of a symbol defined in an <code>Expression</code> 
 * or a <code>ReactionParticipant</code>
 * <p>
 */
public interface SymbolTableFunctionEntry extends SymbolTableEntry {

	public interface Differentiable {
		Expression differentiate(Expression args[], String variable) throws ExpressionException;
	}

	public interface Evaluable {
		double evaluateConstant(Expression[] arguments) throws ExpressionException;
		double evaluateVector(Expression[] arguments, double[] vectorValues) throws ExpressionException;
	}

enum FunctionArgType {
	NUMERIC,
	LITERAL
};

public int getNumArguments();
public FunctionArgType[] getArgTypes();
public String[] getArgNames();
public String getFunctionName();
public Set<String> getAllowableLiteralValues(String argumentName); 
}

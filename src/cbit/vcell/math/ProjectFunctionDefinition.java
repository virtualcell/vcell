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

import java.util.HashSet;
import java.util.Set;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable.SimpleSymbolTableFunctionEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry.Differentiable;
import cbit.vcell.parser.SymbolTableFunctionEntry.Evaluable;

public class ProjectFunctionDefinition extends SimpleSymbolTableFunctionEntry implements Differentiable, Evaluable {
	public final static String PROJECT_ARG_NAME_FUNCTION  = "function";
	public final static String PROJECT_ARG_NAME_AXIS	  = "axis";
	public final static String PROJECT_ARG_NAME_OPERATION = "operation";
	
	public final static String FUNCTION_name				= "vcProject";
	private static final FunctionArgType[] ARGUMENT_TYPES = new FunctionArgType[] { FunctionArgType.NUMERIC, FunctionArgType.LITERAL, FunctionArgType.LITERAL};
	private static final String[] ARGUMENT_NAMES = new String[]{PROJECT_ARG_NAME_FUNCTION,PROJECT_ARG_NAME_AXIS,PROJECT_ARG_NAME_OPERATION};
	
	enum Projection_OP {
		max,
		min,
		avg,
		sum,
	}
	
	enum Projection_Axis {
		x,
		y,
		z,
	}
	
	public ProjectFunctionDefinition() {
		super(FUNCTION_name, 
				ARGUMENT_NAMES, 
				ARGUMENT_TYPES, 
				null,  // expression 
				null,  // units
				null); // namescope
	}

	public Expression differentiate(Expression[] args, String variable) throws ExpressionException {
		if (variable==null){
			throw new RuntimeException("cannot differentiate with respect to a null variable");
		}
		if (args.length!=3){
			throw new IllegalArgumentException("expecting 2 arguments for vcProject()");
		}
		throw new ExpressionException("differentiation not supported for function vcProject()");
	}

	public double evaluateConstant(Expression[] args) throws ExpressionException {
		throw new ExpressionException("cannot evaluate vcProject() to a constant value");
	}

	public double evaluateVector(Expression[] arguments, double[] vectorValues)
			throws ExpressionException {
		throw new ExpressionException("evaluateVector not yet supported for vcProject()");
	}

	@Override
	public Set<String> getAllowableLiteralValues(String argumentName) {
		Set<String> allowableValues = new HashSet<String>();
		if (argumentName.equals(PROJECT_ARG_NAME_OPERATION)) {
			for (Projection_OP op : Projection_OP.values()) {
				allowableValues.add(op.name());
			}
		} else if (argumentName.equals(PROJECT_ARG_NAME_AXIS)) {
			for (Projection_Axis axis : Projection_Axis.values()) {
				allowableValues.add(axis.name());
			}			
		}
		return allowableValues;
	}	

}

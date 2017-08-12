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
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable.SimpleSymbolTableFunctionEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry;

public class FieldFunctionDefinition extends SimpleSymbolTableFunctionEntry implements SymbolTableFunctionEntry.Differentiable {
	
	public final static String FUNCTION_name = "vcField";
	private static final FunctionArgType[] ARGUMENT_TYPES = new FunctionArgType[] { FunctionArgType.LITERAL, FunctionArgType.LITERAL, FunctionArgType.NUMERIC, FunctionArgType.LITERAL };
	private static final String[] ARGUMENT_NAMES = new String[]{"DatasetName","VariableName","Time","VariableType"};
	
	public FieldFunctionDefinition() {
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
		if (args.length!=4){
			throw new IllegalArgumentException("expecting 4 arguments for vcField()");
		}
		if (variable.equals(ReservedVariable.X.getName()) || variable.equals(ReservedVariable.Y.getName()) || variable.equals(ReservedVariable.Z.getName())){
			throw new ExpressionException("differentiation with respect to x,y,z not supported for fields");
		}
		Expression timeArgument = args[2];
		Expression exp = timeArgument.differentiate(variable);
		if (exp.isZero()){
			return new Expression(0.0);
		}else{
			throw new ExpressionException("differentiation with respect to '"+variable+"' not supported for function vcField() with time argument '"+timeArgument.infix()+"'");
		}
	}

}

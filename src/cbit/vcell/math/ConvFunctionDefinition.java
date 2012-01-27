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

import cbit.vcell.math.ReservedVariable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable.SimpleSymbolTableFunctionEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry.Differentiable;
import cbit.vcell.parser.SymbolTableFunctionEntry.Evaluable;

public class ConvFunctionDefinition extends SimpleSymbolTableFunctionEntry implements Differentiable, Evaluable {
	
	public final static String FUNCTION_name = "vcConv";
	private final static String[] ARGUMENT_NAMES = new String[]{"function","kernel"};
	private static final FunctionArgType[] ARGUMENT_TYPES = new FunctionArgType[] { FunctionArgType.NUMERIC, FunctionArgType.NUMERIC };
		  
	public ConvFunctionDefinition() {
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
		if (args.length!=2){
			throw new IllegalArgumentException("expecting 2 arguments for vcConv()");
		}
		if (variable.equals(ReservedVariable.X.getName()) || variable.equals(ReservedVariable.Y.getName()) || variable.equals(ReservedVariable.Z.getName())){
			throw new ExpressionException("differentiation with respect to x,y,z not supported for vcConv() function");
		}
		//
		// TODO: take advantage of commutative property of differential operators: d/dvar(conv(u,v)) = conv(du/dvar,dv/dvar) ????? (check this).
		// so, if du/dvar=constant then grad(du/dvar) = 0, since grad(constant)=0
		//
		Expression exp0 = args[0].differentiate(variable).flatten();
		Expression exp1 = args[1].differentiate(variable).flatten();
		if (exp0.isNumeric() || exp1.isNumeric()){  // conv(0,f) = 0
			return new Expression(0.0);
		}else{
			return new Expression(Expression.function(FUNCTION_name,exp0,exp1));
		}
	}

	public double evaluateConstant(Expression[] args) throws ExpressionException {
		if (args[0].isZero()){
			return 0.0;
		}
		if (args[1].isZero()){
			return 0.0;
		}
		throw new ExpressionException("cannot evaluate vcConv() to a constant value");
	}

	public double evaluateVector(Expression[] arguments, double[] values) throws ExpressionException {
		if (arguments[0].isZero()){
			return 0.0;
		}
		if (arguments[1].isZero()){
			return 0.0;
		}
		throw new ExpressionException("cannot evaluate vcConv()");
	}

}

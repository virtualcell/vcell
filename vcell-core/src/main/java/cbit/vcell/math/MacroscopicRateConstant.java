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

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

public class MacroscopicRateConstant extends JumpProcessRateDefinition {
	private Expression expression = null;
	
	public MacroscopicRateConstant(Expression exp){
		this.expression = exp;
	}

	public Expression getExpression(){
		return expression;
	}

	@Override
	public Expression[] getExpressions() {
		return new Expression[] { expression };
	}

	@Override
	public String getVCML() {
		return VCML.MacroscopicRateConstant+"\t"+getExpression().infix();
	}

	public boolean compareEqual(Matchable obj) {
		if (obj instanceof MacroscopicRateConstant){
			MacroscopicRateConstant mrc = (MacroscopicRateConstant)obj;
			if (!Compare.isEqual(getExpression(), mrc.getExpression())){
				return false;
			}
			return true;
		}
		return false;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	@Override
	public void flatten(MathSymbolTable mathSymbolTable, boolean bRoundCoefficients) throws ExpressionException, MathException {
		expression = Equation.getFlattenedExpression(mathSymbolTable, expression, bRoundCoefficients);
	}
	
}

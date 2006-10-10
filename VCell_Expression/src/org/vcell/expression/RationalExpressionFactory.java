package org.vcell.expression;

import cbit.vcell.parser.RationalExp;
import cbit.vcell.parser.RationalExpUtils;

public class RationalExpressionFactory {

	public static IRationalExpression getRationalExp(IExpression exp) throws ExpressionException {
		return RationalExpUtils.getRationalExp(exp);
	}

	public static IRationalExpression createRationalExpression(long num) {
		return new RationalExp(num);
	}

	public static IRationalExpression createRationalExpression(long num, long den) {
		return new RationalExp(num, den);
	}

	public static IRationalExpression createRationalExpression(String symbol) {
		return new RationalExp(symbol);
	}

}

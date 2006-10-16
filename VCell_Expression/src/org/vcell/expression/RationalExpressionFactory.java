package org.vcell.expression;

import edu.uchc.vcell.expression.internal.RationalExp;
import edu.uchc.vcell.expression.internal.RationalExpUtils;

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

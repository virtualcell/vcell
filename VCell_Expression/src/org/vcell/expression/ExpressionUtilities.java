package org.vcell.expression;

import cbit.vcell.parser.ExpressionUtils;

public class ExpressionUtilities {

	public static boolean functionallyEquivalent(IExpression exp1, IExpression exp2) {
		return ExpressionUtils.functionallyEquivalent(exp1, exp2);
	}

}

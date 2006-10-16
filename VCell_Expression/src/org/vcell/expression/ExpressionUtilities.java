package org.vcell.expression;

import edu.uchc.vcell.expression.internal.ExpressionUtils;

public class ExpressionUtilities {

	public static boolean functionallyEquivalent(IExpression exp1, IExpression exp2) {
		return ExpressionUtils.functionallyEquivalent(exp1, exp2);
	}

}

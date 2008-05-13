package org.vcell.optimization;

import cbit.vcell.parser.Expression;

public class ExplicitObjectiveFunction implements ObjectiveFunction {
	private Expression expression = null;
	
	public ExplicitObjectiveFunction(Expression arg_expression){
		expression = arg_expression;
	}

	public Expression getExpression() {
		return expression;
	}
}

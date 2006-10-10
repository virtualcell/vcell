package org.vcell.expression;

import java.util.StringTokenizer;

import cbit.vcell.parser.Expression;

public class ExpressionFactory {

	public static IExpression add(IExpression expression1, IExpression expression2) throws ExpressionException {
		return Expression.add((Expression)expression1, (Expression)expression2);
	}

	public static IExpression invert(IExpression expression) throws ExpressionException {
		return Expression.invert((Expression)expression);
	}

	public static IExpression mult(IExpression expression1, IExpression expression2) throws ExpressionException {
		return Expression.mult((Expression)expression1, (Expression)expression2);
	}

	public static IExpression negate(IExpression expression) throws ExpressionException {
		return Expression.negate((Expression)expression);
	}

	public static IExpression power(IExpression expression1, IExpression expression2) throws ExpressionException {
		return Expression.power((Expression)expression1, (Expression)expression2);
	}

	public static IExpression createExpression(double value) {
		return new Expression(value);
	}

	public static IExpression createExpression(IExpression exp) {
		return new Expression((Expression)exp);
	}

	public static IExpression createExpression(String expString) throws ExpressionException {
		return new Expression(expString);
	}

	public static IExpression createExpression(StringTokenizer tokens) throws ExpressionException {
		return new Expression(tokens);
	}

	public static IExpression createSubstitutedExpression(IExpression expression, IExpression origExp, IExpression newExp) throws ExpressionException {
		return ((Expression)expression).getSubstitutedExpression((Expression)origExp, (Expression)newExp);
	}

	public static IExpression assign(IExpression lvalueExp, IExpression rvalueExp) throws ExpressionException {
		return Expression.assign((Expression)lvalueExp, (Expression)rvalueExp);
	}

	public static IExpression derivative(String variable, IExpression expression) throws ExpressionException {
		return Expression.derivative(variable, (Expression)expression);
	}

	public static IExpression laplacian(IExpression expression) throws ExpressionException {
		return Expression.laplacian((Expression)expression);
	}

}

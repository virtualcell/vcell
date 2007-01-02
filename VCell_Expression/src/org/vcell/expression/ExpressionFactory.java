package org.vcell.expression;

import java.util.Random;
import java.util.StringTokenizer;

import org.jdom.Element;

public class ExpressionFactory {
	private static IExpressionProvider expressionProvider = null;
	
	public static ExpressionTerm extractTopLevelTerm(IExpression expression) throws ExpressionException {
		return expressionProvider.extractTopLevelTerm(expression);
	}
	
	public static RationalExpression getRationalExpression(IExpression expression) throws ExpressionException {
		return expressionProvider.getRationalExpression(expression);
	}

	public static IExpression add(IExpression expression1, IExpression expression2) throws ExpressionException {
		return expressionProvider.add(expression1, expression2);
	}

	public static IExpression invert(IExpression expression) throws ExpressionException {
		return expressionProvider.invert(expression);
	}

	public static IExpression mult(IExpression expression1, IExpression expression2) throws ExpressionException {
		return expressionProvider.mult(expression1, expression2);
	}

	public static IExpression negate(IExpression expression) throws ExpressionException {
		return expressionProvider.negate(expression);
	}

	public static IExpression power(IExpression expression1, IExpression expression2) throws ExpressionException {
		return expressionProvider.power(expression1, expression2);
	}

	public static IExpression createExpression(double value) {
		return expressionProvider.createExpression(value);
	}

	public static IExpression createExpression(IExpression exp) {
		return expressionProvider.createExpression(exp);
	}

	public static IExpression createExpression(String expString) throws ExpressionException {
		return expressionProvider.createExpression(expString);
	}

	public static IExpression createExpression(StringTokenizer tokens) throws ExpressionException {
		return expressionProvider.createExpression(tokens);
	}

	public static IExpression createRandomExpression(Random random, int maxDepth, boolean bIsConstraint) throws ExpressionException {
		return expressionProvider.createRandomExpression(random, maxDepth, bIsConstraint);
	}

	public static IExpression createSubstitutedExpression(IExpression expression, IExpression origExp, IExpression newExp) throws ExpressionException {
		return expressionProvider.createSubstitutedExpression(expression, origExp, newExp);
	}

	public static IExpression assign(IExpression lvalueExp, IExpression rvalueExp) throws ExpressionException {
		return expressionProvider.assign(lvalueExp, rvalueExp);
	}

	public static IExpression derivative(String variable, IExpression expression) throws ExpressionException {
		return expressionProvider.derivative(variable, expression);
	}

	public static IExpression laplacian(IExpression expression) throws ExpressionException {
		return expressionProvider.laplacian(expression);
	}

	public static IExpression fromMathML(Element mathElement, LambdaFunction[] lambdaFunctions) throws ExpressionException {
		return expressionProvider.fromMathML(mathElement, lambdaFunctions);
	}

	public static IExpression fromMathML(Element mathElement) throws ExpressionException {
		return expressionProvider.fromMathML(mathElement);
	}
	
	public static IExpression fromMathML(String mathMLString) throws ExpressionException {
		return expressionProvider.fromMathML(mathMLString);
	}

	public static IExpressionProvider getExpressionProvider() {
		return expressionProvider;
	}

	public static void setExpressionProvider(IExpressionProvider expressionProvider) {
		ExpressionFactory.expressionProvider = expressionProvider;
	}

}

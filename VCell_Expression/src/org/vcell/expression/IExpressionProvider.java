package org.vcell.expression;

import java.util.Random;
import java.util.StringTokenizer;

import org.jdom.Element;

public interface IExpressionProvider {

	public IExpression add(IExpression expression1, IExpression expression2) throws ExpressionException;

	public IExpression invert(IExpression expression) throws ExpressionException;

	public IExpression mult(IExpression expression1, IExpression expression2) throws ExpressionException;

	public IExpression negate(IExpression expression) throws ExpressionException;

	public IExpression power(IExpression expression1, IExpression expression2) throws ExpressionException;

	public IExpression createExpression(double value);

	public IExpression createExpression(IExpression exp);

	public IExpression createExpression(String expString) throws ExpressionException;

	public IExpression createExpression(StringTokenizer tokens) throws ExpressionException;

	public IExpression createRandomExpression(Random random, int maxDepth, boolean bIsConstraint) throws ExpressionException;

	public IExpression createSubstitutedExpression(IExpression expression, IExpression origExp, IExpression newExp) throws ExpressionException;

	public IExpression assign(IExpression lvalueExp, IExpression rvalueExp) throws ExpressionException;

	public IExpression derivative(String variable, IExpression expression) throws ExpressionException;

	public IExpression laplacian(IExpression expression) throws ExpressionException;

	public IExpression fromMathML(Element mathElement, LambdaFunction[] lambdaFunctions) throws ExpressionException;

	public IExpression fromMathML(Element mathElement) throws ExpressionException;
	
	public IExpression fromMathML(String mathMLString) throws ExpressionException;
	
	public RationalExpression getRationalExpression(IExpression expression) throws ExpressionException;
	
	public ExpressionTerm extractTopLevelTerm(IExpression expression) throws ExpressionException;

}

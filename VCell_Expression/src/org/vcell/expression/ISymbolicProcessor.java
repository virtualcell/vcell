package org.vcell.expression;

public interface ISymbolicProcessor {
	
	/**
	 * generalized simplification (may be replaced with more task-oriented routine)
	 * @param expression
	 * @return
	 */
	public abstract IExpression simplify(IExpression expression);
	
	/**
	 * generalized simplification (may be replaced with more task-oriented routine)
	 * @param expression
	 * @return
	 */
	public abstract RationalExpression simplify(RationalExpression expression);
	
	/**
	 * solve() attempts to solve the equation "expression == 0" with respect to "symbol".
	 * 
	 * @param expression
	 * @param symbol
	 * @return
	 */
	public abstract IExpression solve(IExpression expression, String symbol) throws ExpressionException;
	
}

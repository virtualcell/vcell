package cbit.vcell.math;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.SymbolTable;

public class ExplicitDataGenerator extends DataGenerator {

	private Expression expression = null;
	
	public ExplicitDataGenerator(String argName, Domain argDomain, Expression argExpression) {
		super(argName, argDomain);
		this.expression = argExpression;
	}

	@Override
	public void bind(SymbolTable symbolTable) throws ExpressionBindingException {
		if (expression!=null){
			expression.bindExpression(symbolTable);
		}
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	

}

package cbit.vcell.math;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.SymbolTable;

public class ExplicitDataGenerator extends DataGenerator {

	private Expression expression = null;
	
	public ExplicitDataGenerator(String argName, Domain argDomain, Expression argExpression) {
		super(argName, argDomain);
		if (argExpression == null) {
			throw new IllegalArgumentException("Explicit data generator '" + argName + "' does not have an expression.");
		}
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

	@Override
	public boolean compareEqual(Matchable object, boolean bIgnoreMissingDomain) {
		if (!(object instanceof ExplicitDataGenerator)){
			return false;
		}
		if (!compareEqual0(object,bIgnoreMissingDomain)){
			return false;
		}
		ExplicitDataGenerator v = (ExplicitDataGenerator)object;
		if (!Compare.isEqualOrNull(expression,v.expression)){
			return false;
		}
		return true;
	}

	@Override
	public String getVCML() throws MathException {
		return VCML.ExplicitDataGenerator + "  " + getName() + "\t" + expression.infix()+";\n";
	}
	
	

}

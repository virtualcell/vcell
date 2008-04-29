package cbit.vcell.model;

public interface ExpressionContainer {
	public void setExpression(cbit.vcell.parser.Expression expression) throws java.beans.PropertyVetoException, cbit.vcell.parser.ExpressionBindingException;
}

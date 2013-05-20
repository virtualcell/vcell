package org.vcell.model.rbm;

import org.vcell.util.document.PropertyConstants;

import cbit.vcell.parser.Expression;

public class RbmParameter extends RbmElement {

	public static final String PROPERTY_NAME_EXPRESSION = "expression";
	
	private String name;
	private Expression expression;
	public RbmParameter(String name, Expression expression) {
		super();
		this.name = name;
		this.expression = expression;
	}
	public final String getName() {
		return name;
	}
	public final void setName(String newValue) {
		String oldValue = name;
		this.name = newValue;
		firePropertyChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, newValue);
	}
	public final Expression getExpression() {
		return expression;
	}
	public final void setExpression(Expression newValue) {
		Expression oldValue = expression;
		this.expression = newValue;
		firePropertyChange(PROPERTY_NAME_EXPRESSION, oldValue, newValue);
	}
}

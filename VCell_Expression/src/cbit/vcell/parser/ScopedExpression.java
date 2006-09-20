package cbit.vcell.parser;

/**
 * Insert the type's description here.
 * Creation date: (9/2/2003 3:26:19 PM)
 * @author: Jim Schaff
 */
public class ScopedExpression {
	private Expression fieldExpression = null;
	private NameScope fieldNameScope = null;
	private boolean fieldIsUserEditable = true;
	
	static {
		System.out.println("ScopedExpression.toString() ignores the scope ... TEMPORARY ...");
	};
/**
 * ContextualExpression constructor comment.
 */
public ScopedExpression(Expression argExpression, NameScope argNameScope) {
	this(argExpression,argNameScope,true);
}
/**
 * ContextualExpression constructor comment.
 */
public ScopedExpression(Expression argExpression, NameScope argNameScope, boolean argIsUserEditable) {
	super();
	this.fieldExpression = argExpression;
	this.fieldNameScope = argNameScope;
	this.fieldIsUserEditable = argIsUserEditable;
}
/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 6:03:03 PM)
 * @param expressionString java.lang.String
 */
public ScopedExpression(String expressionString) throws ExpressionException {
	this.fieldExpression = new Expression(expressionString);
	this.fieldIsUserEditable = true;
	this.fieldNameScope = null;
}
/**
 * Insert the method's description here.
 * Creation date: (9/2/2003 3:27:41 PM)
 * @return cbit.vcell.parser.Expression
 */
public Expression getExpression() {
	return fieldExpression;
}
/**
 * Insert the method's description here.
 * Creation date: (9/2/2003 3:28:22 PM)
 * @return cbit.vcell.parser.NameScope
 */
public NameScope getNameScope() {
	return fieldNameScope;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2004 3:02:27 PM)
 * @return boolean
 */
public boolean isUserEditable() {
	return fieldIsUserEditable;
}
/**
 * Insert the method's description here.
 * Creation date: (9/2/2003 5:39:29 PM)
 * @return java.lang.String
 */
public String toString() {
	return getExpression().infix(getNameScope());
	//return getExpression().infix();
}
}

package cbit.vcell.constraints;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.parser.Expression;
/**
 * Insert the type's description here.
 * Creation date: (6/25/01 4:34:19 PM)
 * @author: Jim Schaff
 */
public class GeneralConstraint extends AbstractConstraint {
	private Expression fieldExpression = null;
/**
 * GeneralConstraint constructor comment.
 * @param argConstraintType int
 */
public GeneralConstraint(Expression argExpression, int argConstraintType, String description) {
	super(argConstraintType,description);
	this.fieldExpression = argExpression.getBinaryExpression();
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/01 4:35:35 PM)
 * @return cbit.vcell.parser.Expression
 */
public Expression getExpression() {
	return fieldExpression;
}
/**
 * Sets the bounds property (net.sourceforge.interval.ia_math.RealInterval) value.
 * @param bounds The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getBounds
 */
public void setExpression(Expression expression) throws java.beans.PropertyVetoException {
	Expression oldValue = fieldExpression;
	fireVetoableChange("expression", oldValue, expression);
	fieldExpression = expression;
	firePropertyChange("expression", oldValue, expression);
}
/**
 * Insert the method's description here.
 * Creation date: (9/18/2003 4:53:59 PM)
 * @return java.lang.String
 */
public String toString() {
	return super.toString() + " : "+getExpression().infix();
}
}

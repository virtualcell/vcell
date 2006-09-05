package org.vcell.physics.math;

/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 8:22:59 PM)
 * @author: Jim Schaff
 */
public class ExpressionVertex extends com.mhhe.clrs2e.Vertex {
	private cbit.vcell.parser.Expression exp = null;
/**
 * ExpressionVertex constructor comment.
 * @param index int
 * @param name java.lang.String
 */
public ExpressionVertex(int index, cbit.vcell.parser.Expression argExpression) {
	super(index, argExpression.infix());
	this.exp = argExpression;
}
/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 8:31:09 PM)
 * @return cbit.vcell.parser.Expression
 */
public cbit.vcell.parser.Expression getExp() {
	return exp;
}
/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 8:31:09 PM)
 * @param newExp cbit.vcell.parser.Expression
 */
public void setExp(cbit.vcell.parser.Expression newExp) {
	exp = newExp;
	setName(newExp.infix());
}
}

package cbit.vcell.parser;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/* JJT: 0.2.2 */
import net.sourceforge.interval.ia_math.*;

public class ASTExpression extends SimpleNode {
  ASTExpression() {
    super(ExpressionParserTreeConstants.JJTEXPRESSION);
  }
ASTExpression(int id) {
	super(id);
if (id != ExpressionParserTreeConstants.JJTEXPRESSION){ System.out.println("ASTExpressionNode(), i = "+id); }
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public Node copyTree() {
	ASTExpression node = new ASTExpression();
	for (int i=0;i<jjtGetNumChildren();i++){
		node.jjtAddChild(jjtGetChild(i).copyTree());
	}
	return node;	
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public Node copyTreeBinary() {
	ASTExpression node = new ASTExpression();
	for (int i=0;i<jjtGetNumChildren();i++){
		node.jjtAddChild(jjtGetChild(i).copyTreeBinary());
	}
	return node;	
}
/**
 * This method was created by a SmartGuide.
 * @return double
 * @exception java.lang.Exception The exception description.
 */
public Node differentiate(String variable) throws ExpressionException {
	return jjtGetChild(0).differentiate(variable);
}
/**
 * This method was created by a SmartGuide.
 * @return double
 * @exception java.lang.Exception The exception description.
 */
public double evaluateConstant() throws ExpressionException {
	return jjtGetChild(0).evaluateConstant();
}
/**
 * This method was created by a SmartGuide.
 * @return double
 * @exception java.lang.Exception The exception description.
 */
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException {
	setInterval(jjtGetChild(0).evaluateInterval(intervals),intervals);
	return getInterval(intervals);
}
/**
 * This method was created by a SmartGuide.
 * @return double
 * @exception java.lang.Exception The exception description.
 */
public double evaluateVector(double values[]) throws ExpressionException {
	return jjtGetChild(0).evaluateVector(values);
}
/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
public Node flatten() throws ExpressionException {
	try {
		double value = evaluateConstant();
		return new ASTFloatNode(value);
	}catch (Exception e){}		

	if (jjtGetNumChildren()!=1){ 
		throw new Error("ASTExpression should have 1 child"); 
	}
	return jjtGetChild(0).flatten();
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2003 2:11:37 PM)
 * @return java.lang.String
 * @param language int
 */
public String infixString(int language, NameScope nameScope) {
	StringBuffer buffer = new StringBuffer();
	
	for (int i=0;i<jjtGetNumChildren();i++){
		buffer.append(jjtGetChild(i).infixString(language,nameScope));
	}
	return buffer.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 4:40:51 PM)
 * @return boolean
 */
public boolean narrow(RealInterval intervals[]) {
	throw new RuntimeException("ASTExpression.narrow(), not yet supported");
}
}

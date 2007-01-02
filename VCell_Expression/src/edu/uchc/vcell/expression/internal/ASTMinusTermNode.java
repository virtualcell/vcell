package edu.uchc.vcell.expression.internal;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/* JJT: 0.2.2 */
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.IANarrow;
import net.sourceforge.interval.ia_math.RealInterval;

import org.vcell.expression.ExpressionBindingException;
import org.vcell.expression.ExpressionException;
import org.vcell.expression.NameScope;

/**
 */
public class ASTMinusTermNode extends SimpleNode {
  ASTMinusTermNode() {
    super(ExpressionParserTreeConstants.JJTMINUSTERMNODE);
  }
/**
 * Constructor for ASTMinusTermNode.
 * @param id int
 */
ASTMinusTermNode(int id) {
	super(id);
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 * @see edu.uchc.vcell.expression.internal.Node#copyTree()
 */
public Node copyTree() {
	ASTMinusTermNode node = new ASTMinusTermNode();
	for (int i=0;i<jjtGetNumChildren();i++){
		node.jjtAddChild(jjtGetChild(i).copyTree());
	}
	return node;	
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 * @see edu.uchc.vcell.expression.internal.Node#copyTreeBinary()
 */
public Node copyTreeBinary() {
	ASTMinusTermNode node = new ASTMinusTermNode();
	for (int i=0;i<jjtGetNumChildren();i++){
		node.jjtAddChild(jjtGetChild(i).copyTreeBinary());
	}
	return node;	
}
/**
 * This method was created by a SmartGuide.
 * @param variable String
 * @param derivativePolicy DerivativePolicy
 * @return double
 * @throws ExpressionException
 * @exception java.lang.Exception The exception description.
 * @see edu.uchc.vcell.expression.internal.Node#differentiate(String, DerivativePolicy)
 */
public Node differentiate(String variable, DerivativePolicy derivativePolicy) throws ExpressionException {
	ASTMinusTermNode node = new ASTMinusTermNode();
	node.jjtAddChild(jjtGetChild(0).differentiate(variable,derivativePolicy));
	return node;
}
/**
 * Method evaluateConstant.
 * @return double
 * @throws ExpressionException
 * @see edu.uchc.vcell.expression.internal.Node#evaluateConstant()
 */
public double evaluateConstant() throws ExpressionException {
	return (- jjtGetChild(0).evaluateConstant());
}    
/**
 * Method evaluateInterval.
 * @param intervals RealInterval[]
 * @return RealInterval
 * @throws ExpressionException
 * @see edu.uchc.vcell.expression.internal.Node#evaluateInterval(RealInterval[])
 */
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException {
	setInterval(IAMath.uminus(jjtGetChild(0).evaluateInterval(intervals)),intervals);
	return getInterval(intervals);
}    
/**
 * Method evaluateVector.
 * @param values double[]
 * @return double
 * @throws ExpressionException
 * @see edu.uchc.vcell.expression.internal.Node#evaluateVector(double[])
 */
public double evaluateVector(double values[]) throws ExpressionException {
	return (- jjtGetChild(0).evaluateVector(values));
}    
/**
 * This method was created by a SmartGuide.
 * @return Node
 * @throws ExpressionException
 * @exception java.lang.Exception The exception description.
 * @see edu.uchc.vcell.expression.internal.Node#flatten()
 */
public Node flatten() throws ExpressionException {
	
	try {
		double value = evaluateConstant();
		return new ASTFloatNode(value);
	}catch (Exception e){}		

	if (jjtGetNumChildren()!=1){ 
		throw new Error("ASTMinusTermNode should have 1 child"); 
	}
	Node flattenedChild = jjtGetChild(0).flatten();
	//
	// remove double minus
	//
	if (flattenedChild instanceof ASTMinusTermNode){
		return flattenedChild.jjtGetChild(0);
	}
	
	ASTMinusTermNode minusNode = new ASTMinusTermNode();
	minusNode.jjtAddChild(flattenedChild);	
	return minusNode;
}
/**
 * Method infixString.
 * @param lang int
 * @param nameScope NameScope
 * @return String
 * @see edu.uchc.vcell.expression.internal.Node#infixString(int, NameScope)
 */
public String infixString(int lang, NameScope nameScope){

	StringBuffer buffer = new StringBuffer();
	 
	buffer.append(" - ");

	buffer.append(jjtGetChild(0).infixString(lang,nameScope));

	return buffer.toString();

}    
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 11:04:41 AM)
 * @param intervals RealInterval[]
 * @return boolean
 * @throws ExpressionBindingException
 * @see edu.uchc.vcell.expression.internal.Node#narrow(RealInterval[])
 */
public boolean narrow(RealInterval intervals[]) throws ExpressionBindingException{
	return IANarrow.narrow_uminus(getInterval(intervals),jjtGetChild(0).getInterval(intervals)) 
			&& jjtGetChild(0).narrow(intervals)
			&& IANarrow.narrow_uminus(getInterval(intervals),jjtGetChild(0).getInterval(intervals));
}
}

package cbit.vcell.parser;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/* JJT: 0.2.2 */
import net.sourceforge.interval.ia_math.*;

public class ASTMinusTermNode extends SimpleNode {
  ASTMinusTermNode() {
    super(ExpressionParserTreeConstants.JJTMINUSTERMNODE);
  }
ASTMinusTermNode(int id) {
	super(id);
}
public String code() throws ExpressionException {

	StringBuffer buffer = new StringBuffer();
	 
	buffer.append(" - ");

	buffer.append(jjtGetChild(0).code());

	return buffer.toString();

}    
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
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
 * @return double
 * @exception java.lang.Exception The exception description.
 */
public Node differentiate(String variable) throws ExpressionException {
	ASTMinusTermNode node = new ASTMinusTermNode();
	node.jjtAddChild(jjtGetChild(0).differentiate(variable));
	return node;
}
public double evaluateConstant() throws ExpressionException {
	return (- jjtGetChild(0).evaluateConstant());
}    
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException {
	setInterval(IAMath.uminus(jjtGetChild(0).evaluateInterval(intervals)),intervals);
	return getInterval(intervals);
}    
public double evaluateVector(double values[]) throws ExpressionException {
	return (- jjtGetChild(0).evaluateVector(values));
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
public String infixString(int lang, NameScope nameScope){

	StringBuffer buffer = new StringBuffer();
	 
	buffer.append(" - ");

	buffer.append(jjtGetChild(0).infixString(lang,nameScope));

	return buffer.toString();

}    
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 11:04:41 AM)
 * @return boolean
 */
public boolean narrow(RealInterval intervals[]) throws ExpressionBindingException{
	return IANarrow.narrow_uminus(getInterval(intervals),jjtGetChild(0).getInterval(intervals)) 
			&& jjtGetChild(0).narrow(intervals)
			&& IANarrow.narrow_uminus(getInterval(intervals),jjtGetChild(0).getInterval(intervals));
}
}

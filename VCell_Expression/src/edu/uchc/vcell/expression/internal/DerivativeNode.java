package edu.uchc.vcell.expression.internal;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/* JJT: 0.2.2 */
import net.sourceforge.interval.ia_math.RealInterval;

import org.vcell.expression.ExpressionBindingException;
import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.NameScope;
import org.vcell.expression.SymbolTable;
import org.vcell.expression.SymbolTableEntry;

/**
 */
public class DerivativeNode extends SimpleNode {
	String independentVar = null;
   SymbolTableEntry symbolTableEntryVariable = null;
   Expression displayExp = null;
/**
 * Constructor for DerivativeNode.
 * @param independentVariable String
 */
DerivativeNode(String independentVariable) {
	super(-1);
	this.independentVar = independentVariable;
	try {
		displayExp = (Expression)ExpressionFactory.createExpression("d/d/"+independentVariable+";");
	}catch (Exception e){
	}		
}            
  /** Bind method, identifiers bind themselves to ValueObjects * @param symbolTable SymbolTable
   * @throws ExpressionBindingException
   * @see edu.uchc.vcell.expression.internal.Node#bind(SymbolTable)
   */
public void bind(SymbolTable symbolTable) throws ExpressionBindingException {
	
	if (symbolTable == null){
		symbolTableEntryVariable = null;
		return;
	}	
	
	symbolTableEntryVariable = symbolTable.getEntry(independentVar);

	if (symbolTableEntryVariable==null){
		throw new ExpressionBindingException("error binding independent variable " + independentVar);
	}
}    
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 * @see edu.uchc.vcell.expression.internal.Node#copyTree()
 */
public Node copyTree() {
	DerivativeNode node = new DerivativeNode(independentVar);
	node.symbolTableEntryVariable = symbolTableEntryVariable;
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
	DerivativeNode node = new DerivativeNode(independentVar);
	node.symbolTableEntryVariable = symbolTableEntryVariable;
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
	throw new ExpressionException("DerivativeNode.differentiate(), not implemented");
}
/**
 * Method evaluateConstant.
 * @return double
 * @throws ExpressionException
 * @see edu.uchc.vcell.expression.internal.Node#evaluateConstant()
 */
public double evaluateConstant() throws ExpressionException {
//	return (- jjtGetChild(0).evaluateConstant());
	ASTIdNode childNode = (ASTIdNode)jjtGetChild(0);
	if (childNode.symbolTableEntry != null){
		if (childNode.symbolTableEntry.isConstant()){
			return 0.0;
		}
	}		
	throw new ExpressionBindingException("cannot evaluate derivative, identifier "+childNode.infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+" not bound");
}    
/**
 * Method evaluateInterval.
 * @param intervals RealInterval[]
 * @return RealInterval
 * @throws ExpressionException
 * @see edu.uchc.vcell.expression.internal.Node#evaluateInterval(RealInterval[])
 */
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException {
	throw new ExpressionException("cannot call 'evaluateInterval()' on a derivative");
}    
/**
 * Method evaluateVector.
 * @param values double[]
 * @return double
 * @throws ExpressionException
 * @see edu.uchc.vcell.expression.internal.Node#evaluateVector(double[])
 */
public double evaluateVector(double values[]) throws ExpressionException {
	throw new ExpressionException("cannot call 'evaluateVector()' on a derivative");
}    
/**
 * This method was created by a SmartGuide.
 * @return Node
 * @exception java.lang.Exception The exception description.
 * @see edu.uchc.vcell.expression.internal.Node#flatten()
 */
public Node flatten() {
	return copyTree();
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
	 
	buffer.append(" Deriv_"+independentVar+"( ");

	buffer.append(jjtGetChild(0).infixString(lang,nameScope));
	
	buffer.append(" )");

	return buffer.toString();

}    
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 11:04:41 AM)
 * @param intervals RealInterval[]
 * @return boolean
 * @see edu.uchc.vcell.expression.internal.Node#narrow(RealInterval[])
 */
public boolean narrow(RealInterval intervals[]) {
	throw new RuntimeException("DerivativeNode.narrow(), not yet supported");
}
/**
 * Method getDisplayExp.
 * @return Expression
 */
public Expression getDisplayExp() {
	return displayExp;
}
}

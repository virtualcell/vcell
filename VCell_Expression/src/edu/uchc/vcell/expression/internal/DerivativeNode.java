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

public class DerivativeNode extends SimpleNode {
	String independentVar = null;
   SymbolTableEntry symbolTableEntryVariable = null;
   Expression displayExp = null;
DerivativeNode(String independentVariable) {
	super(-1);
	this.independentVar = independentVariable;
	try {
		displayExp = (Expression)ExpressionFactory.createExpression("d/d/"+independentVariable+";");
	}catch (Exception e){
	}		
}            
  /** Bind method, identifiers bind themselves to ValueObjects */
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
public String code() throws ExpressionException {

	StringBuffer buffer = new StringBuffer();
	 
	buffer.append(" Deriv<"+independentVar+">(");

	buffer.append(jjtGetChild(0).code());
	
	buffer.append(" )");

	return buffer.toString();

}    
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
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
 * @return double
 * @exception java.lang.Exception The exception description.
 */
public Node differentiate(String variable) throws ExpressionException {
	throw new ExpressionException("DerivativeNode.differentiate(), not implemented");
}
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
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException {
	throw new ExpressionException("cannot call 'evaluateInterval()' on a derivative");
}    
public double evaluateVector(double values[]) throws ExpressionException {
	throw new ExpressionException("cannot call 'evaluateVector()' on a derivative");
}    
/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
public Node flatten() throws ExpressionException {
	return copyTree();
}
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
 * @return boolean
 */
public boolean narrow(RealInterval intervals[]) {
	throw new RuntimeException("DerivativeNode.narrow(), not yet supported");
}
public Expression getDisplayExp() {
	return displayExp;
}
}

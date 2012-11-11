/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.parser;

/* JJT: 0.2.2 */
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.IANarrow;
import net.sourceforge.interval.ia_math.RealInterval;

public class ASTNotNode extends SimpleNode {
  ASTNotNode() {
    super(ExpressionParserTreeConstants.JJTNOTNODE);
  }
ASTNotNode(int id) {
	super(id);
}

	public boolean isBoolean() {
		  return true;
	}

  public void bind(SymbolTable symbolTable) throws ExpressionBindingException
  {
	  super.bind(symbolTable);
	  setInterval(new RealInterval(0.0,1.0),null);  // either true or false
  }    
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public Node copyTree() {
	ASTNotNode node = new ASTNotNode();
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
	ASTNotNode node = new ASTNotNode();
	for (int i=0;i<jjtGetNumChildren();i++){
		node.jjtAddChild(jjtGetChild(i).copyTreeBinary());
	}
	return node;	
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Expression
 * @param independentVariable java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public Node differentiate(String independentVariable) throws ExpressionException {
	ASTNotNode notNode = new ASTNotNode();
	
	notNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
	
	return notNode;
}
public double evaluateConstant() throws ExpressionException, DivideByZeroException {
	double childValue = jjtGetChild(0).evaluateConstant();
	if (childValue==0.0){
		return 1.0;
	}else{
		return 0.0;
	}
}    
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException, DivideByZeroException {
	setInterval(IAMath.vcell_not(jjtGetChild(0).evaluateInterval(intervals)),intervals);
	return getInterval(intervals);
}    
public double evaluateVector(double values[]) throws ExpressionException, DivideByZeroException {

	double childValue = jjtGetChild(0).evaluateVector(values);

	if (childValue == 0.0){
		return 1.0;
	}else{
		return 0.0;
	}
}    
/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
public Node flatten() throws ExpressionException {
	try {
		double value = evaluateConstant();
		if (value==0.0){
			return new ASTFloatNode(0.0);
		}else{
			return new ASTFloatNode(1.0);
		}
	}catch (DivideByZeroException e){
//		e.printStackTrace(System.out);
		throw e;
	}catch (ExpressionException e){
	}		

	if (jjtGetNumChildren()!=1){ 
		throw new Error("ASTNotNode should have 1 child"); 
	}
	
	//
	// remove double NOT
	//
	//if (jjtGetChild(0) instanceof ASTNotNode){
		//return jjtGetChild(0).jjtGetChild(0).flatten();
	//}
	
	ASTNotNode notNode = new ASTNotNode();
	notNode.jjtAddChild(jjtGetChild(0).flatten());	
	return notNode;
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2003 2:11:37 PM)
 * @return java.lang.String
 * @param language int
 */
public String infixString(int language) {
	StringBuffer buffer = new StringBuffer();

	if (language == LANGUAGE_VISIT){
		buffer.append("not(");
	}else if (language == LANGUAGE_ECLiPSe){
		buffer.append("neg(");
	}else{
		buffer.append("!(");
	}
	buffer.append(jjtGetChild(0).infixString(language));
	buffer.append(")");

	return buffer.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 11:04:41 AM)
 * @return boolean
 */
public boolean narrow(RealInterval intervals[]) throws ExpressionBindingException {
	return IANarrow.vcell_narrow_not(getInterval(intervals),jjtGetChild(0).getInterval(intervals)) 
			&& jjtGetChild(0).narrow(intervals)
			&& IANarrow.vcell_narrow_not(getInterval(intervals),jjtGetChild(0).getInterval(intervals));
}

	public Node convertToRvachevFunction()
	{
		ASTMinusTermNode node = new ASTMinusTermNode();
		node.jjtAddChild(jjtGetChild(0).convertToRvachevFunction());
		return node;
	}

}

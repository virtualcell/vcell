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

import cbit.vcell.parser.ASTFuncNode.FunctionType;
/* JJT: 0.2.2 */
import net.sourceforge.interval.ia_math.IAFunctionDomainException;
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.IANarrow;
import net.sourceforge.interval.ia_math.RealInterval;

public class ASTPowerNode extends SimpleNode {

ASTPowerNode() {
	super(ExpressionParserTreeConstants.JJTPOWERNODE);
}
ASTPowerNode(int id) {
	super(id);
if (id != ExpressionParserTreeConstants.JJTPOWERNODE){ System.out.println("ASTAddNode(), id = "+id); }
}
  /**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public Node copyTree() {
	ASTPowerNode node = new ASTPowerNode();
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
	ASTPowerNode node = new ASTPowerNode();
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
	// 
	// case of D(u^v) = v u^(v-1) D(u)  +  u^v log(u) D(v)
	//
	if (jjtGetNumChildren()!=2) throw new Error("'^' expects 2 arguments");

	// 
	// form  v pow(u,v-1) D(u)
	//
	ASTMultNode multNode1 = new ASTMultNode();
	ASTPowerNode powNode = new ASTPowerNode();
	ASTAddNode addNode = new ASTAddNode();
	addNode.jjtAddChild(jjtGetChild(1).copyTree());
	addNode.jjtAddChild(new ASTFloatNode(-1.0));
	powNode.jjtAddChild(jjtGetChild(0).copyTree());
	powNode.jjtAddChild(addNode);
	multNode1.jjtAddChild(jjtGetChild(1).copyTree());
	multNode1.jjtAddChild(powNode);
	multNode1.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
	
	// 
	// form  pow(u,v) log(u) D(v)
	//
	ASTMultNode multNode2 = new ASTMultNode();
	ASTFuncNode logNode = new ASTFuncNode();
	logNode.setFunctionType(FunctionType.LOG);
	logNode.jjtAddChild(jjtGetChild(0).copyTree());
	multNode2.jjtAddChild(copyTree());
	multNode2.jjtAddChild(logNode);
	multNode2.jjtAddChild(jjtGetChild(1).differentiate(independentVariable));
	
	ASTAddNode fullAddNode = new ASTAddNode();
	fullAddNode.jjtAddChild(multNode1);
	fullAddNode.jjtAddChild(multNode2);
	
	return fullAddNode;
}
public double evaluateConstant() throws ExpressionException {
	if (jjtGetNumChildren()!=2){
		throw new ExpressionException("expecting two arguments for Power");
	}
	//
	// see if there are any constant 0.0's, if there are simplify to 0.0
	//
	ExpressionException savedException = null;
	
	Double exponentValue = null;
	try {
		exponentValue = new Double(jjtGetChild(1).evaluateConstant());
	}catch (ExpressionException e){
		savedException = e;
	}
	Double  baseValue = null;
	try {
		baseValue = new Double(jjtGetChild(0).evaluateConstant());
	}catch (ExpressionException e){
		savedException = e;
	}

	if (exponentValue!=null && exponentValue.doubleValue()==0){
		return 1.0;
	}
	if (savedException!=null){
		throw savedException;
	}else if (baseValue!=null && exponentValue!=null){
		if (baseValue.doubleValue()==0.0 && exponentValue.doubleValue()<0.0){
			String childString = infixString(LANGUAGE_DEFAULT);
			throw new DivideByZeroException("u^v and u=0 and v<0 divides by zero, expression = '"+infixString(LANGUAGE_DEFAULT)+"'");
		}else if (baseValue.doubleValue()<0.0 && exponentValue.doubleValue()!=Math.round(exponentValue.doubleValue())){
			throw new FunctionDomainException("u^v and u<0 and v not an integer: undefined, u="+baseValue.doubleValue()+", v="+exponentValue.doubleValue()+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}else{
			double result = Math.pow(baseValue.doubleValue(),exponentValue.doubleValue());
			if (Double.isInfinite(result) || Double.isNaN(result)){
				throw new FunctionDomainException("u^v evaluated to "+result+", u="+baseValue.doubleValue()+", v="+exponentValue.doubleValue()+", expression = '"+infixString(LANGUAGE_DEFAULT)+"'");
			}
			return result;
		}
	}else{ // should never happen
		throw new RuntimeException("unexpected error, no exception and either baseValue or exponentValue is null");
	}
}    
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException {
	if (jjtGetNumChildren()!=2) throw new Error("pow() expects 2 arguments");
	try {
		setInterval(IAMath.vcell_power(jjtGetChild(0).evaluateInterval(intervals),jjtGetChild(1).evaluateInterval(intervals)),intervals);
	}catch (IAFunctionDomainException e){
		e.printStackTrace(System.out);
		throw new FunctionDomainException(e.getMessage());
	}
	return getInterval(intervals);
}    
public double evaluateVector(double values[]) throws ExpressionException {
	if (jjtGetNumChildren()!=2){
		throw new RuntimeException("ASTPowerNode@"+Integer.toHexString(hashCode())+" wrong number of arguments for '^' ("+jjtGetNumChildren()+"), expected 2");
	}
	double baseValue = jjtGetChild(0).evaluateVector(values);
	double exponentValue = jjtGetChild(1).evaluateVector(values);
	if (baseValue==0.0 && exponentValue<0.0){
		throw new DivideByZeroException("u^v and u=0 and v<0 divides by zero, expression = '"+infixString(LANGUAGE_DEFAULT)+"'");
	}else if (baseValue<0.0 && exponentValue!=Math.round(exponentValue)){
		throw new FunctionDomainException("u^v and u<0 and v not an integer: undefined, u="+baseValue+", v="+exponentValue+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
	}else{
		if (baseValue>=0.0 && exponentValue==1.0){
			return baseValue;
		}
		double result = Math.pow(baseValue,exponentValue);
		if (Double.isInfinite(result) || Double.isNaN(result)){
			throw new FunctionDomainException("u^v evaluated to "+result+", u="+baseValue+", v="+exponentValue+", expression = '"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		return result;
	}
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

	ASTPowerNode powerNode = new ASTPowerNode();
	java.util.Vector tempChildren = new java.util.Vector();
	for (int i=0;i<jjtGetNumChildren();i++){
		tempChildren.addElement(jjtGetChild(i).flatten());
	}
	
	if (tempChildren.size()!=2) throw new ExpressionException("'^' expects 2 arguments");
	//
	//  b
	// a   test for b = 1
	//
	Node exponentChild = (Node)tempChildren.elementAt(1);
	Node mantissaChild = (Node)tempChildren.elementAt(0);
	if (exponentChild instanceof ASTFloatNode){
		double exponent = ((ASTFloatNode)exponentChild).value.doubleValue();
		if (exponent == 1.0){
			return mantissaChild;
		}
	}
	//
	//  b
	// a   test for b = 0
	//
	if (exponentChild instanceof ASTFloatNode){
		double exponent = ((ASTFloatNode)exponentChild).value.doubleValue();
		if (exponent == 0.0){
			return new ASTFloatNode(1.0);
		}
	}
	//
	//   w    
	//  v          v*w
	// u    --->  u
	//
	if ((mantissaChild instanceof ASTFuncNode && ((ASTFuncNode)mantissaChild).getFunction() == FunctionType.POW) || mantissaChild instanceof ASTPowerNode){
		ASTMultNode newMultNode = new ASTMultNode();
		newMultNode.jjtAddChild(mantissaChild.jjtGetChild(1));
		newMultNode.jjtAddChild(exponentChild);
		ASTPowerNode newExponentNode = new ASTPowerNode();
		newExponentNode.jjtAddChild(mantissaChild.jjtGetChild(0));
		newExponentNode.jjtAddChild(newMultNode);
		return newExponentNode.flatten();
	}

	//
	//
	//
	ASTPowerNode powNode = new ASTPowerNode();
	powNode.jjtAddChild((SimpleNode)tempChildren.elementAt(0));
	powNode.jjtAddChild((SimpleNode)tempChildren.elementAt(1));

	return powNode;
		
}

public String infixString(int lang){

	if (jjtGetNumChildren()!=2){
		throw new RuntimeException("there are "+jjtGetNumChildren()+" arguments for the power operator, expecting 2");
	}
	
	StringBuffer buffer = new StringBuffer();

	if (lang == LANGUAGE_C){
		buffer.append("pow(");
		buffer.append("((double)(" + jjtGetChild(0).infixString(lang) + "))");
		buffer.append(",");
		buffer.append("((double)(" + jjtGetChild(1).infixString(lang) + "))");
		buffer.append(")");
	}else{
		buffer.append("(");
		buffer.append(jjtGetChild(0).infixString(lang));
		buffer.append(" ^ ");
		buffer.append(jjtGetChild(1).infixString(lang));
		buffer.append(")");
	} 

	return buffer.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 11:04:41 AM)
 * @return boolean
 */
public boolean narrow(RealInterval intervals[]) throws ExpressionBindingException{
	if (jjtGetNumChildren()!=2) throw new Error("power '^' expects 2 arguments");
	return IANarrow.vcell_narrow_power(getInterval(intervals),jjtGetChild(0).getInterval(intervals),jjtGetChild(1).getInterval(intervals))
			&& jjtGetChild(0).narrow(intervals)
			&& jjtGetChild(1).narrow(intervals)
			&& IANarrow.vcell_narrow_power(getInterval(intervals),jjtGetChild(0).getInterval(intervals),jjtGetChild(1).getInterval(intervals));
}

	public Node convertToRvachevFunction()
	{
		ASTPowerNode node = new ASTPowerNode();
		for (int i = 0; i < jjtGetNumChildren(); ++ i)
		{
			node.jjtAddChild(jjtGetChild(i).convertToRvachevFunction());
		}
		return node;
	}
}

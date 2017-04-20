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
import net.sourceforge.interval.ia_math.RealInterval;

public class ASTLaplacianNode extends SimpleNode {
ASTLaplacianNode() {
	super(-1);
}  
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public Node copyTree() {
	ASTLaplacianNode node = new ASTLaplacianNode();
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
	ASTLaplacianNode node = new ASTLaplacianNode();
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
	throw new ExpressionException("ASTLaplacianNode.differentiate(), not implemented");
}
public double evaluateConstant() throws ExpressionException {
	throw new ExpressionException("LaplaicianNode cannot be evaluated as a constant");
}    
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException {
	throw new ExpressionException("ASTLaplacianNode.evaluateInterval(), not implemented");
}    
public double evaluateVector(double values[]) throws ExpressionException {
	throw new ExpressionException("ASTLaplacianNode.evaluateVector(), not implemented");
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
		throw new Error("ASTLaplacianNode should have 1 child"); 
	}
		
	ASTLaplacianNode laplacianNode = new ASTLaplacianNode();
	laplacianNode.jjtAddChild(jjtGetChild(0).flatten());	
	return laplacianNode;
}

public String infixString(int lang){

	StringBuffer buffer = new StringBuffer();
	 
	buffer.append(" laplacian(");

	buffer.append(jjtGetChild(0).infixString(lang));
	
	buffer.append(") ");

	return buffer.toString();

}    
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 11:04:41 AM)
 * @return boolean
 */
public boolean narrow(RealInterval intervals[]) {
	throw new RuntimeException("ASTLaplacianNode.narrow(), not yet supported");
}

	public Node convertToRvachevFunction()
	{
		ASTLaplacianNode node = new ASTLaplacianNode();
		for (int i = 0; i < jjtGetNumChildren(); ++ i)
		{
			node.jjtAddChild(jjtGetChild(i).convertToRvachevFunction());
		}
		return node;
	}
}

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
public class ASTAddNode extends SimpleNode {

ASTAddNode() {
	super(ExpressionParserTreeConstants.JJTADDNODE);
}
ASTAddNode(int i) {
	super(i);
if (i != ExpressionParserTreeConstants.JJTADDNODE){ System.out.println("ASTAddNode(), i = "+i); }
}
  /**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public Node copyTree() {
	ASTAddNode node = new ASTAddNode();
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
	int i=0;
	ASTAddNode rootNode = new ASTAddNode();
	ASTAddNode node = rootNode;
	while (i<jjtGetNumChildren()){
		node.jjtAddChild(jjtGetChild(i++).copyTreeBinary());
		if (i<jjtGetNumChildren()-1){
			ASTAddNode tempNode = new ASTAddNode();
			node.jjtAddChild(tempNode);
			node = tempNode;
		}else{
			node.jjtAddChild(jjtGetChild(i++).copyTreeBinary());
			break;
		}
	}
	return rootNode;	
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Expression
 * @param independentVariable java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public Node differentiate(String independentVariable) throws ExpressionException {
	ASTAddNode addNode = new ASTAddNode(id);
	for (int i=0;i<jjtGetNumChildren();i++){
		addNode.jjtAddChild(jjtGetChild(i).differentiate(independentVariable));
	}
	return addNode;	 
}
public double evaluateConstant() throws ExpressionException {
	double sum = 0;
	for (int i=0;i<jjtGetNumChildren();i++){
		sum += jjtGetChild(i).evaluateConstant();
	}
	return sum;	 
}    
//
// sum of intervals
//
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException {
	RealInterval sum = jjtGetChild(0).evaluateInterval(intervals);
	for (int i=1;i<jjtGetNumChildren();i++){
		sum = IAMath.add(jjtGetChild(i).evaluateInterval(intervals),sum);
	}
	setInterval(sum,intervals);
	return getInterval(intervals);	 
}    
public double evaluateVector(double values[]) throws ExpressionException {
	double sum = 0;
	for (int i=0;i<jjtGetNumChildren();i++){
		sum += jjtGetChild(i).evaluateVector(values);
	}
	return sum;	 
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

	ASTAddNode addNode = new ASTAddNode(id);
	java.util.Vector<Node> tempChildren = new java.util.Vector<Node>();

	for (int i=0;i<jjtGetNumChildren();i++){
		tempChildren.addElement(jjtGetChild(i).flatten());
	}

//System.out.println("flattening.....");
//dump("");
//System.out.println(infixString());

	//
	// incorporate terms that are sums
	//
	boolean moreToDo = true;
	while (moreToDo){
		moreToDo = false;
		for (int i=0;i<tempChildren.size();i++){
			Node child = tempChildren.elementAt(i);
			if (child instanceof ASTAddNode){
				//
				// get all grandchildren and remove child
				//
				for (int j=0;j<child.jjtGetNumChildren();j++){
					tempChildren.insertElementAt(child.jjtGetChild(j),i+j);
				}
				tempChildren.removeElement(child);
				
				moreToDo = true;
				break;
			}
		}
	}			
	
//System.out.println(infixString());
	//
	// evaluate constants when possible and collect terms
	//
	double floatValue = 0.0;
	int floatCount = 0;
	boolean moreFloat = true;
	while (moreFloat){
		moreFloat = false;
		for (int i=0;i<tempChildren.size();i++){
			Node child = tempChildren.elementAt(i);
			try {
				double equivalentValue = child.evaluateConstant();
				floatCount++;
				floatValue += equivalentValue;
				tempChildren.removeElement(child);
				moreFloat = true;
				break;
			}catch(Exception e){
			}
		}
	}
	if (floatCount > 0 && floatValue != 0.0){
		tempChildren.insertElementAt(new ASTFloatNode(floatValue),0);
	}	
	if (tempChildren.size()==1){
		return tempChildren.elementAt(0);
	}		
	for (int i=0;i<tempChildren.size();i++){
		addNode.jjtAddChild(tempChildren.elementAt(i));
	}
	return addNode;					
//	for (int i=0;i<jjtGetNumChildren();i++){
//		jjtGetChild(i).flatten();
//	}
//System.out.println(infixString());
//dump("");
//System.out.println("........done flattening");


}
  
public String infixString(int lang)
  {
	  StringBuffer buffer = new StringBuffer();
	 
	  buffer.append("(");

	  for (int i=0;i<jjtGetNumChildren();i++){
		  if (jjtGetChild(i) instanceof ASTMinusTermNode){
//			 buffer.append(" - ");
//             buffer.append("(");
			 buffer.append(jjtGetChild(i).infixString(lang));
//             buffer.append(")");
		  }else{
			 if (i>0) buffer.append(" + ");
			 buffer.append(jjtGetChild(i).infixString(lang));
		  }
	  }

	  buffer.append(")");

	  return buffer.toString();
  }  
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 11:04:41 AM)
 * @return boolean
 */
public boolean narrow(RealInterval intervals[]) throws ExpressionBindingException{

	for (int i = 0; i < jjtGetNumChildren()-1; i++){
		if (!IANarrow.narrow_add(getInterval(intervals),jjtGetChild(i).getInterval(intervals),jjtGetChild(i+1).getInterval(intervals))){
			return false;
		}
	}
	for (int i = 0; i < jjtGetNumChildren(); i++){
		if (!jjtGetChild(i).narrow(intervals)){
			return false;
		}
	}
	for (int i = 0; i < jjtGetNumChildren()-1; i++){
		if (!IANarrow.narrow_add(getInterval(intervals),jjtGetChild(i).getInterval(intervals),jjtGetChild(i+1).getInterval(intervals))){
			return false;
		}
	}
	return true;
}

	public Node convertToRvachevFunction() 
	{
		ASTAddNode node = new ASTAddNode();
		for (int i = 0; i < jjtGetNumChildren(); ++ i)
		{
			node.jjtAddChild(jjtGetChild(i).convertToRvachevFunction());
		}
		return node;
	}
}

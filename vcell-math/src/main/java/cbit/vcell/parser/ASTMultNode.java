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

import java.util.Vector;

import net.sourceforge.interval.ia_math.IAException;
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.IANarrow;
import net.sourceforge.interval.ia_math.RealInterval;

public class ASTMultNode extends SimpleNode {

ASTMultNode() {
	super(ExpressionParserTreeConstants.JJTMULTNODE);
}
ASTMultNode(int id) {
	super(id);
if (id != ExpressionParserTreeConstants.JJTMULTNODE){ System.out.println("ASTMultNode(), id = "+id); }
}
  /**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public Node copyTree() {
	ASTMultNode node = new ASTMultNode();
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
    //
    // group any invert term nodes together
    //
    // eg. a / c / d     -->  a / (c * d)
    // eg. a * b / c / d --> (a * b)/(c * d)
    //
    Vector quotientList = new Vector();
    Vector numeratorList = new Vector();
    for (int i = 0; i < jjtGetNumChildren(); i++) {
        if (jjtGetChild(i) instanceof ASTInvertTermNode) {
            quotientList.add(jjtGetChild(i));
        } else {
            numeratorList.add(jjtGetChild(i));
        }
    }

    
    SimpleNode rootNode = null;
    if (numeratorList.size()==1){
	    rootNode = (SimpleNode)((SimpleNode)numeratorList.elementAt(0)).copyTreeBinary();
    }else{
	    ASTMultNode node = new ASTMultNode();
	    rootNode = node;
	    int i = 0;
	    while (i < numeratorList.size()) {
	        node.jjtAddChild(((SimpleNode) numeratorList.elementAt(i++)).copyTreeBinary());
	        if (i < numeratorList.size() - 1) {
	            ASTMultNode tempNode = new ASTMultNode();
	            node.jjtAddChild(tempNode);
	            node = tempNode;
			} else {
				node.jjtAddChild(((SimpleNode) numeratorList.elementAt(i++)).copyTreeBinary());
				break;
	        }
	    }
    }
    if (quotientList.size()>1){
	    ASTMultNode newRoot = new ASTMultNode(); // new root
	    newRoot.jjtAddChild(rootNode);
	    rootNode = newRoot;
	    ASTInvertTermNode quotientParent = new ASTInvertTermNode();
	    newRoot.jjtAddChild(quotientParent);
	    ASTMultNode node = new ASTMultNode();
	    quotientParent.jjtAddChild(node);
	    int i = 0;
	    while (i < quotientList.size()) {
	        node.jjtAddChild(((SimpleNode) quotientList.elementAt(i++)).jjtGetChild(0).copyTreeBinary());
	        if (i < quotientList.size() - 1) {
	            ASTMultNode tempNode = new ASTMultNode();
	            node.jjtAddChild(tempNode);
	            node = tempNode;
	        } else {
	            node.jjtAddChild(((SimpleNode) quotientList.elementAt(i++)).jjtGetChild(0).copyTreeBinary());
	            break;
	        }
	    }
    }else if (quotientList.size()==1){
	    ASTMultNode newRoot = new ASTMultNode(); // new root
	    newRoot.jjtAddChild(rootNode);
	    newRoot.jjtAddChild(((SimpleNode) quotientList.elementAt(0)).copyTreeBinary());
	    rootNode = newRoot;
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
	ASTAddNode addNode = new ASTAddNode();
	int count = 0;
	for (int i=0;i<jjtGetNumChildren();i++){
		ASTMultNode termNode = new ASTMultNode();
		for (int j=0;j<jjtGetNumChildren();j++){
			if (j==i){
				termNode.jjtAddChild(jjtGetChild(j).differentiate(independentVariable));
			}else{
				termNode.jjtAddChild(jjtGetChild(j).copyTree());
			}		
		}
		addNode.jjtAddChild(termNode);
	}	
	return addNode;	 
}
public double evaluateConstant() throws ExpressionException {
	// evaluate boolean children first for conditional expressions. 
	// if any one of them is false, just return 0;
	// this protects against evaluating the terms outside of
	// domain defined by the boolean conditions.
	ExpressionException childException = null;
	for (int i=0;i<jjtGetNumChildren();i++){
		if (jjtGetChild(i).isBoolean()) {
			try {
				if (jjtGetChild(i).evaluateConstant() == 0) {
					return 0.0;
				}
			}catch (ExpressionException e){
				childException = e;
			}		
		}
	}

	double product = 1.0;
	for (int i=0;i<jjtGetNumChildren();i++){
		//
		// see if there are any constant 0.0's, if there are simplify to 0.0
		//
		try {
			double constantValue = jjtGetChild(i).evaluateConstant();
			product *= constantValue;
		}catch (ExpressionException e){
			childException = e;
		}		
	}
	if (product == -0.0){
		return 0.0;
	}	
	if (childException != null){
		throw childException;
	}	
	return product;
}    
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException {
	RealInterval product = jjtGetChild(0).evaluateInterval(intervals);
	for (int i=1;i<jjtGetNumChildren();i++){
		product = IAMath.mul(product,jjtGetChild(i).evaluateInterval(intervals));
	}
	setInterval(product,intervals);
	return getInterval(intervals);
}    
public double evaluateVector(double values[]) throws ExpressionException {
	// evaluate boolean children first for conditional expressions. 
	// if any one of them is false, just return 0;
	// this protects against evaluating the terms outside of
	// domain defined by the boolean conditions.
	for (int i=0;i<jjtGetNumChildren();i++){
		if (jjtGetChild(i).isBoolean()) {
			if (jjtGetChild(i).evaluateVector(values) == 0) {
				return 0.0;
			}
		}
	}
	
	// otherwise, evaluate all terms in the old way.	
	double product = 1.0;
	for (int i=0;i<jjtGetNumChildren();i++){
		product *= jjtGetChild(i).evaluateVector(values);
	}

	if (product == -0.0){
		return 0.0;
	}else{
		return product;
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

	ASTMultNode multNode = new ASTMultNode();
	java.util.Vector tempChildren = new java.util.Vector();

	for (int i=0;i<jjtGetNumChildren();i++){
		tempChildren.addElement(jjtGetChild(i).flatten());
	}

//System.out.println("flattening.....");
//dump("");
//System.out.println(infixString());
	//
	// incorporate terms that are products
	//
	boolean moreToDo = true;
	while (moreToDo){
		moreToDo = false;
		for (int i=0;i<tempChildren.size();i++){
			Node child = (Node)tempChildren.elementAt(i);
			if (child instanceof ASTMultNode){
				for (int j=0;j<child.jjtGetNumChildren();j++){
					//tempChildren.add(i+j,child.jjtGetChild(j).flatten());
					tempChildren.insertElementAt(child.jjtGetChild(j).flatten(),i+j);
				}
				tempChildren.remove(child);
//				return flatten();
				moreToDo = true;
				break;
			}
			if (child instanceof ASTMinusTermNode && child.jjtGetChild(0) instanceof ASTMultNode){
				int j=0;
				for (;j<child.jjtGetChild(0).jjtGetNumChildren();j++){
					//tempChildren.add(i+j,child.jjtGetChild(0).jjtGetChild(j));
					tempChildren.add(child.jjtGetChild(0).jjtGetChild(j));
				}
				tempChildren.add(i+j,new ASTFloatNode(-1.0));
				tempChildren.remove(child);
//				return flatten();
				moreToDo = true;
				break;
			}
		}
	}	
	
	//
	// count number of constants, inversions, and negations
	//
	double floatValue = 1.0;
	int floatCount = 0;
	int invertCount = 0;
	int minusCount = 0;
	for (int i=0;i<tempChildren.size();i++){
		Node child = (Node)tempChildren.elementAt(i);
		if (child instanceof ASTFloatNode){
			floatCount++;
			floatValue *= ((ASTFloatNode)child).value.doubleValue();
		}
		if (child instanceof ASTInvertTermNode){
			invertCount++;
		}	
		if (child instanceof ASTMinusTermNode){
			minusCount++;
		}	
	}
	//
	// get rid of constants and negations
	//
	boolean moreFloat = true;
	boolean moreMinus = true;
	while (moreFloat || moreMinus){
		moreFloat = false;
		moreMinus = false;
		for (int i=0;i<tempChildren.size();i++){
			Node child = (Node)tempChildren.elementAt(i);
			if (child instanceof ASTFloatNode){
				tempChildren.removeElement(child);
				moreFloat = true;
				break;
			}
			if (child instanceof ASTMinusTermNode){
				tempChildren.removeElement(child);
				Node childsChild = child.jjtGetChild(0).flatten();
				tempChildren.addElement(childsChild);
				if (childsChild instanceof ASTMinusTermNode){
					minusCount++;
				}
				if (childsChild instanceof ASTFloatNode){
					floatCount++;
					floatValue *= ((ASTFloatNode)childsChild).value.doubleValue();
				}	
				moreMinus = true;
				break;
			}
		}
	}	
	
	if (floatValue<0){
		floatValue = -floatValue;
		minusCount++;
	}	
		
	java.util.Vector numerators = new java.util.Vector();
	java.util.Vector denominators = new java.util.Vector();
	for (int i=0;i<tempChildren.size();i++){
		SimpleNode child = (SimpleNode)tempChildren.elementAt(i);
		if (child instanceof ASTInvertTermNode){
			denominators.addElement(child);
		}else{
			numerators.addElement(child);
		}
	}
	//
	// if float value is not 1.0 or if no other numerators, then insert this number
	//
	
	// need to first remove drift from repeated divisions/multiplications
	if (Math.abs(1.0-floatValue)<1e-12) {
		floatValue=1.0;
	}
	
	if ((floatValue != 1.0) || (numerators.size() < 1)){
		numerators.insertElementAt(new ASTFloatNode(floatValue),0);
	}

	tempChildren.removeAllElements();
	for (int i=0;i<numerators.size();i++){
		tempChildren.addElement(numerators.elementAt(i));
	}
	for (int i=0;i<denominators.size();i++){
		tempChildren.addElement(denominators.elementAt(i));
	}

	//
	// if only one child, then just return it
	//
	if (tempChildren.size()==1){
//dump("");
//System.out.println(((Node)tempChildren.elementAt(0)).infixString());
//System.out.println("......flattening done");
		if (minusCount%2 == 1){
			ASTMinusTermNode minusNode = new ASTMinusTermNode();
			minusNode.jjtAddChild((SimpleNode)tempChildren.elementAt(0));
			return minusNode;
		}else{	
			return (Node)tempChildren.elementAt(0);
		}	
	}
	
	//
	// else return mult node
	//
	for (int i=0;i<tempChildren.size();i++){
		multNode.jjtAddChild((Node)tempChildren.elementAt(i));
	}
/*
	SimpleNode newNode = null;
	if (minusCount>0){
		newNode = (SimpleNode)multNode.flatten();
	}else{
		newNode = multNode;
	}	
*/

//dump("");
//System.out.println(multNode.infixString());
//System.out.println("......flattening done");
	if (minusCount%2 == 1){
		ASTMinusTermNode minusNode = new ASTMinusTermNode();
		minusNode.jjtAddChild(multNode);
		return minusNode;
	}else{	
		return multNode;
	}	
}

public boolean isBoolean() {
	for (int i=0;i<jjtGetNumChildren();i++){
		if (!jjtGetChild(i).isBoolean()) {
			return false;
		}
	}
	  return true;
}

public String infixString(int lang){

	boolean[] boolChildFlags = new boolean[jjtGetNumChildren()];
	boolean bAllBoolean = true;
	boolean bNoBoolean = true;
	for (int i=0;i<jjtGetNumChildren();i++){
		if (jjtGetChild(i).isBoolean()) {
			boolChildFlags[i] = true;
			bNoBoolean = false;
		} else {
			bAllBoolean = false;
		}
	}
	
	StringBuffer buffer = new StringBuffer();
	 
	buffer.append("(");
	
	if (bAllBoolean || bNoBoolean || (lang != SimpleNode.LANGUAGE_C && lang != SimpleNode.LANGUAGE_VISIT)) { // old way
		for (int i=0;i<jjtGetNumChildren();i++){
			if (jjtGetChild(i) instanceof ASTInvertTermNode){
				if (lang == SimpleNode.LANGUAGE_MATLAB){
					buffer.append(" ./ ");
				}else{
					buffer.append(" / ");
				}
				buffer.append(jjtGetChild(i).infixString(lang));
			}else{
				if (lang == SimpleNode.LANGUAGE_MATLAB){
					if (i>0) buffer.append(" .* ");
				}else{
					if (i>0) buffer.append(" * ");
				}
				buffer.append(jjtGetChild(i).infixString(lang));
			}
		}		
	} else {		
		StringBuffer conditionBuffer = new StringBuffer();
		StringBuffer valueBuffer = new StringBuffer();
		for (int i=0;i<jjtGetNumChildren();i++){
			if (boolChildFlags[i]) {
				if (conditionBuffer.length() > 0) {
					conditionBuffer.append(" && ");
				}
				conditionBuffer.append(jjtGetChild(i).infixString((lang == SimpleNode.LANGUAGE_VISIT?SimpleNode.LANGUAGE_DEFAULT:lang)));
			} else {
				if (valueBuffer.length() == 0) {					
					if (jjtGetChild(i) instanceof ASTInvertTermNode){
						valueBuffer.append(" 1.0 / ");	
					}
				} else {					
					if (jjtGetChild(i) instanceof ASTInvertTermNode){
						valueBuffer.append(" / ");	
					} else {
						valueBuffer.append(" * ");
					}
				}
				valueBuffer.append(jjtGetChild(i).infixString(lang));
			}
		}
		if(lang == SimpleNode.LANGUAGE_VISIT){
			try{
				Expression exp = new Expression(conditionBuffer.toString());
				buffer.append("if(" + exp.infix_VISIT(null) + " , " + valueBuffer + " , 0.0)");		
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			buffer.append("((" + conditionBuffer + ") ? (" + valueBuffer + ") : 0.0)");
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
	if (jjtGetNumChildren()!=2){
		throw new IAException("ASTMultNode.narrow() cannot work with "+jjtGetNumChildren()+" children");
	}
	return IANarrow.narrow_mul(getInterval(intervals),jjtGetChild(0).getInterval(intervals),jjtGetChild(1).getInterval(intervals))
			&& jjtGetChild(0).narrow(intervals)
			&& jjtGetChild(1).narrow(intervals)
			&& IANarrow.narrow_mul(getInterval(intervals),jjtGetChild(0).getInterval(intervals),jjtGetChild(1).getInterval(intervals));
}

	public Node convertToRvachevFunction()
	{
		ASTMultNode node = new ASTMultNode();
		for (int i = 0; i < jjtGetNumChildren(); ++ i)
		{
			node.jjtAddChild(jjtGetChild(i).convertToRvachevFunction());
		}
		return node;
	}

}

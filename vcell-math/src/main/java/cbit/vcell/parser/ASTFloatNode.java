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

public class ASTFloatNode extends SimpleNode {

  Double value;

/**
 * This method was created by a SmartGuide.
 * @param id java.lang.String
 * @param value double
 */
ASTFloatNode (double doubleValue) {
	super(ExpressionParserTreeConstants.JJTFLOATNODE);
	if (Double.isNaN(doubleValue)){
		throw new ArithmeticException("cannot set float node to NaN");
	}
	this.value = new Double(doubleValue);
}
/**
 * This method was created by a SmartGuide.
 */
ASTFloatNode (int id) {
	super(id);
}
  public void bind(SymbolTable symbolTable) throws ExpressionBindingException
  {
	  super.bind(symbolTable);
	  setInterval(new RealInterval(value.doubleValue(),value.doubleValue()),null);
  }    
  /**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public Node copyTree(){
	ASTFloatNode node = new ASTFloatNode(value.doubleValue());
	return node;	
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public Node copyTreeBinary(){
	ASTFloatNode node = new ASTFloatNode(value.doubleValue());
	return node;	
}
/**
 * This method was created by a SmartGuide.
 * @return double
 * @exception java.lang.Exception The exception description.
 */
public Node differentiate(String variable) {
	return new ASTFloatNode(0.0);
}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @param node cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public boolean equals(Node node) {
	//
	// check to see if the types and children are the same
	//
	if (!super.equals(node)){
		return false;
	}
	
	//
	// check this node for same state (value)
	//	
	ASTFloatNode floatNode = (ASTFloatNode)node;
	if (!floatNode.value.equals(value)){
		return false;
	}	

	return true;
}
public double evaluateConstant() {
	return value.doubleValue();
}      
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionBindingException{
	setInterval(new RealInterval(value.doubleValue()),intervals);
	return getInterval(intervals);
}      
public double evaluateVector(double values[]) {
	return value.doubleValue();
}      
/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
public Node flatten() throws ExpressionException {
	return copyTree();
}

  public String infixString(int lang)
  {
	  if (value==null){
		  return "null";
	  }else if (value.doubleValue()==0.0){
		  return "0.0";
	  }else{
		  if (lang == LANGUAGE_ECLiPSe){
			if (value.doubleValue() == Double.POSITIVE_INFINITY){
				return "1.0Inf";
			}else if (value.doubleValue() == Double.NEGATIVE_INFINITY){
				return "-1.0Inf";
			}else{
				return value.toString();
			}
		  } else if (lang == LANGUAGE_UNITS) {
			  if (value == value.intValue()) {
				  return Integer.toString(value.intValue());
			  } else {
				  return value.toString();
			  }
		  } else {
		      return value.toString();
		  }
	  }
  }
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 4:37:00 PM)
 * @return boolean
 */
public boolean narrow(RealInterval intervals[]) throws ExpressionBindingException{
	setInterval(new RealInterval(value.doubleValue()),intervals);
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (10/11/2002 8:53:36 AM)
 */
public void roundToFloat() {
	value = new Double(value.floatValue());
}

	public Node convertToRvachevFunction()
	{
		return copyTree();
	}
}

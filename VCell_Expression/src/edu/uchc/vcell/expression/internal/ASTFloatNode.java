package edu.uchc.vcell.expression.internal;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/* JJT: 0.2.2 */
import net.sourceforge.interval.ia_math.RealInterval;

import org.vcell.expression.ExpressionBindingException;
import org.vcell.expression.ExpressionException;
import org.vcell.expression.NameScope;
import org.vcell.expression.SymbolTable;

/**
 */
public class ASTFloatNode extends SimpleNode {

  Double value;

/**
 * This method was created by a SmartGuide.
 * @param doubleValue double
 */
ASTFloatNode (double doubleValue) {
	super(ExpressionParserTreeConstants.JJTFLOATNODE);
	if (Double.isNaN(doubleValue)){
		throw new RuntimeException("cannot set float node to NaN");
	}
	this.value = new Double(doubleValue);
}
/**
 * This method was created by a SmartGuide.
 * @param id int
 */
ASTFloatNode (int id) {
	super(id);
}
  /**
   * Method bind.
   * @param symbolTable SymbolTable
   * @throws ExpressionBindingException
   * @see edu.uchc.vcell.expression.internal.Node#bind(SymbolTable)
   */
  public void bind(SymbolTable symbolTable) throws ExpressionBindingException
  {
	  super.bind(symbolTable);
	  setInterval(new RealInterval(value.doubleValue(),value.doubleValue()),null);
  }    
  /**
   * Method getValue.
   * @return double
   */
  public double getValue() {
	return value.doubleValue();
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 * @see edu.uchc.vcell.expression.internal.Node#copyTree()
 */
public Node copyTree(){
	ASTFloatNode node = new ASTFloatNode(value.doubleValue());
	return node;	
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 * @see edu.uchc.vcell.expression.internal.Node#copyTreeBinary()
 */
public Node copyTreeBinary(){
	ASTFloatNode node = new ASTFloatNode(value.doubleValue());
	return node;	
}
/**
 * This method was created by a SmartGuide.
 * @param variable String
 * @param derivativePolicy DerivativePolicy
 * @return double
 * @exception java.lang.Exception The exception description.
 * @see edu.uchc.vcell.expression.internal.Node#differentiate(String, DerivativePolicy)
 */
public Node differentiate(String variable, DerivativePolicy derivativePolicy) {
	return new ASTFloatNode(0.0);
}
/**
 * This method was created by a SmartGuide.
 * @param node cbit.vcell.parser.Node
 * @return boolean
 * @throws ExpressionException
 * @exception java.lang.Exception The exception description.
 * @see edu.uchc.vcell.expression.internal.Node#equals(Node)
 */
public boolean equals(Node node) throws ExpressionException {
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
/**
 * Method evaluateConstant.
 * @return double
 * @see edu.uchc.vcell.expression.internal.Node#evaluateConstant()
 */
public double evaluateConstant() {
	return value.doubleValue();
}      
/**
 * Method evaluateInterval.
 * @param intervals RealInterval[]
 * @return RealInterval
 * @throws ExpressionBindingException
 * @see edu.uchc.vcell.expression.internal.Node#evaluateInterval(RealInterval[])
 */
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionBindingException{
	setInterval(new RealInterval(value.doubleValue()),intervals);
	return getInterval(intervals);
}      
/**
 * Method evaluateVector.
 * @param values double[]
 * @return double
 * @see edu.uchc.vcell.expression.internal.Node#evaluateVector(double[])
 */
public double evaluateVector(double values[]) {
	return value.doubleValue();
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
  public String infixString(int lang, NameScope nameScope)
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
		  }else{
		      return value.toString();
		  }
	  }
  }
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 4:37:00 PM)
 * @param intervals RealInterval[]
 * @return boolean
 * @throws ExpressionBindingException
 * @see edu.uchc.vcell.expression.internal.Node#narrow(RealInterval[])
 */
public boolean narrow(RealInterval intervals[]) throws ExpressionBindingException{
	setInterval(new RealInterval(value.doubleValue()),intervals);
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (10/11/2002 8:53:36 AM)
 * @see edu.uchc.vcell.expression.internal.Node#roundToFloat()
 */
public void roundToFloat() {
	value = new Double(value.floatValue());
}
}

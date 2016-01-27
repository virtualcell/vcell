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
import java.util.Set;
import java.util.Vector;

import net.sourceforge.interval.ia_math.RealInterval;
import cbit.vcell.parser.Expression.FunctionFilter;

public abstract class SimpleNode implements Node, java.io.Serializable {
  protected Node parent;
  protected Node[] children;
  protected int id;
  //protected Object info;
  transient private RealInterval ival = null;

  final static int LANGUAGE_DEFAULT = 0;
  final static int LANGUAGE_C = 1;
  final static int LANGUAGE_MATLAB = 2;
  final static int LANGUAGE_JSCL = 3;
  final static int LANGUAGE_ECLiPSe = 4;
  final static int LANGUAGE_VISIT = 5;
  final static int LANGUAGE_UNITS = 6;
  final static int LANGUAGE_BNGL = 7;

  public SimpleNode(int i) {
    id = i;
  }

  public boolean isBoolean() {
	  return false;
  }
  
  public void bind(SymbolTable symbolTable) throws ExpressionBindingException
  {
	  ival = null;
	  for (int i=0;i<jjtGetNumChildren();i++){
		  jjtGetChild(i).bind(symbolTable);
	  }
  }    


  public void dump(String prefix) {
    System.out.println(toString(prefix));
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
	SimpleNode n = (SimpleNode)children[i];
	if (n != null) {
	  n.dump(prefix + " ");
	}
      }
    }
  }


/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @param node cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public boolean equals(Node node) {
	//
	// check to see if this node is the same
	//
	if (!node.getClass().equals(getClass())){
		return false;
	}
	//
	// check for different number of children
	//	
	if (jjtGetNumChildren() != node.jjtGetNumChildren()){
		return false;
	}	
	//
	// now, check to see if all children are the same
	//
	//  (note: I'm assuming the children are in the same order)
	//
	for (int i=0;i<jjtGetNumChildren();i++){
		Node myChild = jjtGetChild(i);
		Node nodeChild = node.jjtGetChild(i);
		if (!myChild.equals(nodeChild)){
			return false;
		}	
	}		
	
	return true;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param symbol java.lang.String
 */
public SymbolTableEntry getBinding(String symbol) {
	for (int i=0;i<jjtGetNumChildren();i++){
		SymbolTableEntry ste = jjtGetChild(i).getBinding(symbol);
		if (ste != null){
			return ste;
		}
	}		
	return null;
}

void getFunctionInvocations(java.util.Vector<FunctionInvocation> v, FunctionFilter filter) {	
	for (int i = 0;  i < jjtGetNumChildren(); i ++) {
		SimpleNode child = (SimpleNode)jjtGetChild(i);
		child.getFunctionInvocations(v,filter);		 
	}	
}

//  public Object getInfo() { return info; }


/**
 * Insert the method's description here.
 * Creation date: (6/20/01 10:55:57 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 */
public RealInterval getInterval(RealInterval intervals[]) throws ExpressionBindingException{
	if (ival==null){
		ival = RealInterval.fullInterval();
	}
	return ival;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String[]
 * @exception java.lang.Exception The exception description.
 */
public void getSymbols(int language, Set<String> symbolSet) {
	for (int i=0;i<jjtGetNumChildren();i++){
		jjtGetChild(i).getSymbols(language, symbolSet);
	}	
}

public void jjtAddChild(Node n) {
	jjtAddChild(n,jjtGetNumChildren());
}    


  public void jjtAddChild(Node n, int i) {
    if (children == null) {
      children = new Node[i + 1];
    } else if (i >= children.length) {
      Node c[] = new Node[i + 1];
      System.arraycopy(children, 0, c, 0, children.length);
      children = c;
    }
    children[i] = n;
    ((SimpleNode)children[i]).parent = this;
  }


  public void jjtClose() {
  }


  public Node jjtGetChild(int i) {
    return children[i];
  }


  public int jjtGetNumChildren() {
    return (children == null) ? 0 : children.length;
  }


  public Node jjtGetParent() { return parent; }


  public void jjtOpen() {
  }


  public void jjtSetParent(Node n) { parent = n; }


public void roundToFloat() {
    for (int i = 0; i < jjtGetNumChildren(); i++) {
        jjtGetChild(i).roundToFloat();
    }
}


  /* These two methods provide a very simple mechanism for attaching
     arbitrary data to the node. */

//  public void setInfo(Object i) { info = i; }


/**
 * Insert the method's description here.
 * Creation date: (6/20/01 10:55:57 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 */
public void setInterval(RealInterval interval, RealInterval intervals[]) throws ExpressionBindingException{
	ival = interval;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String[]
 * @param array1 java.lang.String[]
 * @param array2 java.lang.String[]
 */
private String[] stringArrayMerge(String[] array1, String[] array2) {
	if (array1 == null && array2 == null){
		return null;
	}
	if (array1 == null){
		return array2;
	}
	if (array2 == null){
		return array1;
	}
	java.util.Vector<String> newVector = new java.util.Vector<String>();
	for (int i=0;i<array1.length;i++){
		newVector.addElement(array1[i]);
	}
	for (int i=0;i<array2.length;i++){
		boolean found = false;
		for (int j=0;j<array1.length;j++){
			if (array1[j].equals(array2[i])){
				found = true;
			}	
		}
		if (!found){
			newVector.addElement(array2[i]);
		}
	}			
	String newArray[] = new String[newVector.size()];
	for (int i=0;i<newVector.size();i++){
		newArray[i] = newVector.elementAt(i);
	}
	return newArray;		
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @param origExp cbit.vcell.parser.Node
 * @param newExp cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public void substitute(Node origNode, Node newNode) {

	for (int i=0;i<jjtGetNumChildren();i++){
		if (jjtGetChild(i).equals(origNode)){
			children[i] = newNode.copyTree();
			newNode.jjtSetParent(this);
		}else{
			jjtGetChild(i).substitute(origNode,newNode);
		}
	}
	
/*
	if (equals(origNode)){
		if (parent!=null){
			parent.jjtReplaceNode(this,newNode.copyTree());
		}else{
			throw new Exception("substitute failed");
		}					
	}
*/
}

/* You can override these two methods in subclasses of SimpleNode to
     customize the way the node appears when the tree is dumped.  If
     your output uses more than one line you should override
     toString(String), otherwise overriding toString() is probably all
     you need to do. */

  public String toString() { return getClass()+"@"+hashCode()+" \"" + infixString(LANGUAGE_DEFAULT) + "\""; }


  public String toString(String prefix) { return prefix + toString(); }
  
  public void getDiscontinuities(Vector<Discontinuity> v) throws ExpressionException {
	  if (children == null) {
		  return;
	  }
	  for (Node child : children) {
		  child.getDiscontinuities(v);
	  }
  }
  
  public void renameBoundSymbols(NameScope nameScope) throws ExpressionBindingException {
	  for (int i=0;i<jjtGetNumChildren();i++){
		  jjtGetChild(i).renameBoundSymbols(nameScope);
	  }
 }
}

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
import org.vcell.expression.SymbolTableEntry;


/**
 */
public abstract class SimpleNode implements Node, java.io.Serializable {
  protected Node parent;
  protected Node[] children;
  protected int id;
  protected Object info;
  transient private RealInterval ival = null;

  public static final int LANGUAGE_DEFAULT = 0;
  public static final int LANGUAGE_C = 1;
  public static final int LANGUAGE_MATLAB = 2;
  public static final int LANGUAGE_JSCL = 3;
  public static final int LANGUAGE_ECLiPSe = 4;

  public static final NameScope NAMESCOPE_DEFAULT = null;

  /**
   * Constructor for SimpleNode.
   * @param i int
   */
  public SimpleNode(int i) {
    id = i;
  }


  /**
   * Method bind.
   * @param symbolTable SymbolTable
   * @throws ExpressionBindingException
   * @see edu.uchc.vcell.expression.internal.Node#bind(SymbolTable)
   */
  public void bind(SymbolTable symbolTable) throws ExpressionBindingException
  {
	  ival = null;
	  for (int i=0;i<jjtGetNumChildren();i++){
		  jjtGetChild(i).bind(symbolTable);
	  }
  }    


  /**
   * Method dump.
   * @param prefix String
   */
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
 * @param node cbit.vcell.parser.Node
 * @return boolean
 * @throws ExpressionException
 * @exception java.lang.Exception The exception description.
 * @see edu.uchc.vcell.expression.internal.Node#equals(Node)
 */
public boolean equals(Node node) throws ExpressionException {
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
 * @param symbol java.lang.String
 * @return cbit.vcell.parser.SymbolTableEntry
 * @see edu.uchc.vcell.expression.internal.Node#getBinding(String)
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


  /**
   * Method getInfo.
   * @return Object
   */
  public Object getInfo() { return info; }


/**
 * Insert the method's description here.
 * Creation date: (6/20/01 10:55:57 AM)
 * @param intervals RealInterval[]
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @see edu.uchc.vcell.expression.internal.Node#getInterval(RealInterval[])
 */
public RealInterval getInterval(RealInterval intervals[]) {
	if (ival==null){
		ival = RealInterval.fullInterval();
	}
	return ival;
}


/**
 * This method was created by a SmartGuide.
 * @param language int
 * @param nameScope NameScope
 * @return java.lang.String[]
 * @exception java.lang.Exception The exception description.
 * @see edu.uchc.vcell.expression.internal.Node#getSymbols(int, NameScope)
 */
public String[] getSymbols(int language, NameScope nameScope) {
	String[] stringArray = null;
	for (int i=0;i<jjtGetNumChildren();i++){
		stringArray = stringArrayMerge(stringArray,jjtGetChild(i).getSymbols(language, nameScope));
	}	
	return stringArray;
}


/**
 * Method infixString.
 * @param lang int
 * @param nameScope NameScope
 * @return String
 * @see edu.uchc.vcell.expression.internal.Node#infixString(int, NameScope)
 */
public abstract String infixString(int lang, NameScope nameScope);


/**
 * Method jjtAddChild.
 * @param n Node
 * @see edu.uchc.vcell.expression.internal.Node#jjtAddChild(Node)
 */
public void jjtAddChild(Node n) {
	jjtAddChild(n,jjtGetNumChildren());
}    


  /**
   * Method jjtAddChild.
   * @param n Node
   * @param i int
   * @see edu.uchc.vcell.expression.internal.Node#jjtAddChild(Node, int)
   */
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


  /**
   * Method jjtClose.
   * @see edu.uchc.vcell.expression.internal.Node#jjtClose()
   */
  public void jjtClose() {
  }


  /**
   * Method jjtGetChild.
   * @param i int
   * @return Node
   * @see edu.uchc.vcell.expression.internal.Node#jjtGetChild(int)
   */
  public Node jjtGetChild(int i) {
    return children[i];
  }


  /**
   * Method jjtGetNumChildren.
   * @return int
   * @see edu.uchc.vcell.expression.internal.Node#jjtGetNumChildren()
   */
  public int jjtGetNumChildren() {
    return (children == null) ? 0 : children.length;
  }


  /**
   * Method jjtGetParent.
   * @return Node
   * @see edu.uchc.vcell.expression.internal.Node#jjtGetParent()
   */
  public Node jjtGetParent() { return parent; }


  /**
   * Method jjtOpen.
   * @see edu.uchc.vcell.expression.internal.Node#jjtOpen()
   */
  public void jjtOpen() {
  }


  /**
   * Method jjtSetParent.
   * @param n Node
   * @see edu.uchc.vcell.expression.internal.Node#jjtSetParent(Node)
   */
  public void jjtSetParent(Node n) { parent = n; }


/**
 * Method roundToFloat.
 * @see edu.uchc.vcell.expression.internal.Node#roundToFloat()
 */
public void roundToFloat() {
    for (int i = 0; i < jjtGetNumChildren(); i++) {
        jjtGetChild(i).roundToFloat();
    }
}


  /* These two methods provide a very simple mechanism for attaching
     arbitrary data to the node. */

  /**
   * Method setInfo.
   * @param i Object
   */
  public void setInfo(Object i) { info = i; }


/**
 * Insert the method's description here.
 * Creation date: (6/20/01 10:55:57 AM)
 * @param interval RealInterval
 * @param intervals RealInterval[]
 * @see edu.uchc.vcell.expression.internal.Node#setInterval(RealInterval, RealInterval[])
 */
public void setInterval(RealInterval interval, RealInterval intervals[]) {
	ival = interval;
}


/**
 * This method was created by a SmartGuide.
 * @param array1 java.lang.String[]
 * @param array2 java.lang.String[]
 * @return java.lang.String[]
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
	java.util.Vector newVector = new java.util.Vector();
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
		newArray[i] = (String)newVector.elementAt(i);
	}
	return newArray;		
}


/**
 * This method was created by a SmartGuide.
 * @param origNode Node
 * @param newNode Node
 * @throws ExpressionException
 * @exception java.lang.Exception The exception description.
 * @see edu.uchc.vcell.expression.internal.Node#substitute(Node, Node)
 */
public void substitute(Node origNode, Node newNode) throws ExpressionException {

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


  /**
   * Method substituteBoundSymbols.
   * @throws ExpressionException
   * @see edu.uchc.vcell.expression.internal.Node#substituteBoundSymbols()
   */
  public void substituteBoundSymbols() throws ExpressionException
  {
	  for (int i=0;i<jjtGetNumChildren();i++){
		  jjtGetChild(i).substituteBoundSymbols();
	  }
  }        


  /* You can override these two methods in subclasses of SimpleNode to
     customize the way the node appears when the tree is dumped.  If
     your output uses more than one line you should override
     toString(String), otherwise overriding toString() is probably all
     you need to do. */

  /**
   * Method toString.
   * @return String
   */
  public String toString() { return getClass()+"@"+hashCode()+" \"" + infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT) + "\""; }


  /**
   * Method toString.
   * @param prefix String
   * @return String
   */
  public String toString(String prefix) { return prefix + toString(); }
}
package edu.uchc.vcell.expression.internal;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/* JJT: 0.2.2 */

/* All AST nodes must implement this interface.  It provides basic
   machinery for constructing the parent and child relationships
   between nodes. */
import net.sourceforge.interval.ia_math.RealInterval;

import org.vcell.expression.ExpressionBindingException;
import org.vcell.expression.ExpressionException;
import org.vcell.expression.NameScope;
import org.vcell.expression.SymbolTable;
import org.vcell.expression.SymbolTableEntry;

public interface Node {

  /** Bind method, identifiers bind themselves to ValueObjects */
  void bind(SymbolTable symbolTable) 
						throws ExpressionBindingException;
  /** cppCode method, prints expressions in syntax of 'language' */
  String code() throws ExpressionException;          
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 */
public Node copyTree();
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 */
public Node copyTreeBinary();
/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
Node differentiate(String independentVariable) throws ExpressionException;
/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @param node cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public boolean equals(Node node) throws ExpressionException;
/**
 * This method was created by a SmartGuide.
 * @return double
 * @exception java.lang.Exception The exception description.
 */
public double evaluateConstant() throws org.vcell.expression.ExpressionException;
/**
 * This method was created in VisualAge.
 * @return double
 * @param values double[]
 * @exception org.vcell.expression.ExpressionException The exception description.
 */
RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException;
/**
 * This method was created in VisualAge.
 * @return double
 * @param values double[]
 * @exception org.vcell.expression.ExpressionException The exception description.
 */
double evaluateVector(double values[]) throws ExpressionException;
/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
Node flatten() throws ExpressionException;
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param symbol java.lang.String
 */
public SymbolTableEntry getBinding(String symbol);
/**
 * This method was created by a SmartGuide.
 * @return RealInterval
 */
public RealInterval getInterval(RealInterval intervals[]) throws ExpressionBindingException;
/**
 * This method was created by a SmartGuide.
 * @return java.lang.String[]
 * @exception java.lang.Exception The exception description.
 */
public String[] getSymbols(int language, NameScope nameScope);
  /** infixString method, prints expressions in infix */
  String infixString(int lang, NameScope nameScope);  
  /** This method tells the node to add its argument to the node's
	list of children.  */
   void jjtAddChild(Node n);  
  /** This method tells the node to add its argument to the node's
	list of children.  */
   void jjtAddChild(Node n, int i);  
  /** This method is called after all the child nodes have been
	added. */
   void jjtClose();  
  /** This method returns a child node.  The children are numbered
	 from zero, left to right. */
   Node jjtGetChild(int i);  
  /** Return the number of children the node has. */
  int jjtGetNumChildren();
   Node jjtGetParent();  
  /** This method is called after the node has been made the current
	node.  It indicates that child nodes can now be added to it. */
   void jjtOpen();  
  /** This pair of methods are used to inform the node of its
	parent. */
   void jjtSetParent(Node n);  
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 10:59:50 AM)
 * @return boolean
 */
boolean narrow(RealInterval intervals[]) throws ExpressionBindingException;
/**
 * Insert the method's description here.
 * Creation date: (10/11/2002 8:49:27 AM)
 */
void roundToFloat();
/**
 * This method was created by a SmartGuide.
 * @return void
 */
public void setInterval(RealInterval interval, RealInterval intervals[]) throws ExpressionBindingException;
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @param origNode cbit.vcell.parser.Node
 * @param newNode cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public void substitute(Node origNode, Node newNode) throws ExpressionException;
/**
 * This method was created in VisualAge.
 */
void substituteBoundSymbols() throws ExpressionException;
}

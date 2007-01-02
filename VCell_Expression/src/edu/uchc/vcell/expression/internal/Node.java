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

/**
 */
public interface Node {

  /** Bind method, identifiers bind themselves to ValueObjects * @param symbolTable SymbolTable
   * @throws ExpressionBindingException
   */
  void bind(SymbolTable symbolTable) 
						throws ExpressionBindingException;
  /**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 */
Node copyTree();
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 */
Node copyTreeBinary();
/**
 * This method was created by a SmartGuide.
 * @param independentVariable String
 * @param derivativePolicy DerivativePolicy
 * @return Node
 * @throws ExpressionException
 * @exception java.lang.Exception The exception description.
 */
Node differentiate(String independentVariable, DerivativePolicy derivativePolicy) throws ExpressionException;
/**
 * This method was created by a SmartGuide.
 * @param node cbit.vcell.parser.Node
 * @return boolean
 * @throws ExpressionException
 * @exception java.lang.Exception The exception description.
 */
boolean equals(Node node) throws ExpressionException;
/**
 * This method was created by a SmartGuide.
 * @return double
 * @throws org.vcell.expression.ExpressionException
 * @exception java.lang.Exception The exception description.
 */
double evaluateConstant() throws org.vcell.expression.ExpressionException;
/**
 * This method was created in VisualAge.
 * @param intervals RealInterval[]
 * @return double
 * @throws ExpressionException
 * @exception org.vcell.expression.ExpressionException The exception description.
 */
RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException;
/**
 * This method was created in VisualAge.
 * @param values double[]
 * @return double
 * @throws ExpressionException
 * @exception org.vcell.expression.ExpressionException The exception description.
 */
double evaluateVector(double values[]) throws ExpressionException;
/**
 * This method was created by a SmartGuide.
 * @return Node
 * @throws ExpressionException
 * @exception java.lang.Exception The exception description.
 */
Node flatten() throws ExpressionException;
/**
 * This method was created by a SmartGuide.
 * @param symbol java.lang.String
 * @return cbit.vcell.parser.SymbolTableEntry
 */
SymbolTableEntry getBinding(String symbol);
/**
 * This method was created by a SmartGuide.
 * @param intervals RealInterval[]
 * @return RealInterval
 * @throws ExpressionBindingException
 */
RealInterval getInterval(RealInterval intervals[]) throws ExpressionBindingException;
/**
 * This method was created by a SmartGuide.
 * @param language int
 * @param nameScope NameScope
 * @return java.lang.String[]
 * @exception java.lang.Exception The exception description.
 */
String[] getSymbols(int language, NameScope nameScope);
  /** infixString method, prints expressions in infix * @param lang int
   * @param nameScope NameScope
   * @return String
   */
  String infixString(int lang, NameScope nameScope);  
  /** This method tells the node to add its argument to the node's
	list of children.  * @param n Node
   */
   void jjtAddChild(Node n);  
  /** This method tells the node to add its argument to the node's
	list of children.  * @param n Node
   * @param i int
   */
   void jjtAddChild(Node n, int i);  
  /** This method is called after all the child nodes have been
	added. */
   void jjtClose();  
  /** This method returns a child node.  The children are numbered
	 from zero, left to right. * @param i int
   * @return Node
   */
   Node jjtGetChild(int i);  
  /** Return the number of children the node has. * @return int
   */
  int jjtGetNumChildren();
   /**
    * Method jjtGetParent.
    * @return Node
    */
   Node jjtGetParent();  
  /** This method is called after the node has been made the current
	node.  It indicates that child nodes can now be added to it. */
   void jjtOpen();  
  /** This pair of methods are used to inform the node of its
	parent. * @param n Node
   */
   void jjtSetParent(Node n);  
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 10:59:50 AM)
 * @param intervals RealInterval[]
 * @return boolean
 * @throws ExpressionBindingException
 */
boolean narrow(RealInterval intervals[]) throws ExpressionBindingException;
/**
 * Insert the method's description here.
 * Creation date: (10/11/2002 8:49:27 AM)
 */
void roundToFloat();
/**
 * This method was created by a SmartGuide.
 * @param interval RealInterval
 * @param intervals RealInterval[]
 * @throws ExpressionBindingException
 */
void setInterval(RealInterval interval, RealInterval intervals[]) throws ExpressionBindingException;
/**
 * This method was created by a SmartGuide.
 * @param origNode cbit.vcell.parser.Node
 * @param newNode cbit.vcell.parser.Node
 * @throws ExpressionException
 * @exception java.lang.Exception The exception description.
 */
void substitute(Node origNode, Node newNode) throws ExpressionException;
/**
 * This method was created in VisualAge.
 * @throws ExpressionException
 */
void substituteBoundSymbols() throws ExpressionException;
}

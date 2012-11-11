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

/* All AST nodes must implement this interface.  It provides basic
   machinery for constructing the parent and child relationships
   between nodes. */
import java.util.Set;
import java.util.Vector;

import net.sourceforge.interval.ia_math.RealInterval;

interface Node {
	
	public boolean isBoolean();

  /** Bind method, identifiers bind themselves to ValueObjects */
  void bind(SymbolTable symbolTable) 
						throws ExpressionBindingException;
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
public boolean equals(Node node);
/**
 * This method was created by a SmartGuide.
 * @return double
 * @exception java.lang.Exception The exception description.
 */
public double evaluateConstant() throws cbit.vcell.parser.ExpressionException;
/**
 * This method was created in VisualAge.
 * @return double
 * @param values double[]
 * @exception cbit.vcell.parser.ExpressionException The exception description.
 */
RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException;
/**
 * This method was created in VisualAge.
 * @return double
 * @param values double[]
 * @exception cbit.vcell.parser.ExpressionException The exception description.
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
public void getSymbols(int language, Set<String> symbolSet);
  /** infixString method, prints expressions in infix */
  String infixString(int lang);  
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
public void substitute(Node origNode, Node newNode);

void getDiscontinuities(Vector<Discontinuity> v) throws ExpressionException;

public void renameBoundSymbols(NameScope nameScope) throws ExpressionBindingException;

	public Node convertToRvachevFunction();
}

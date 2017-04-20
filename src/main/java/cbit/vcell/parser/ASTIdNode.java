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
import net.sourceforge.interval.ia_math.RealInterval;
import org.vcell.util.TokenMangler;

public class ASTIdNode extends SimpleNode {

  String name = null;
  transient SymbolTableEntry symbolTableEntry = null;

ASTIdNode() {
	super(ExpressionParserTreeConstants.JJTIDNODE);
  }    
ASTIdNode(int id) {
	super(id);
if (id != ExpressionParserTreeConstants.JJTIDNODE){ System.out.println("ASTIdNode(), id = "+id); }

  }    
/**
 * This method was created by a SmartGuide.
 */
ASTIdNode ( ASTIdNode node ) {
	super(node.id);
	this.name = node.name;
	this.symbolTableEntry = node.symbolTableEntry;
}
  /** Bind method, identifiers bind themselves to ValueObjects */
public void bind(SymbolTable symbolTable) throws ExpressionBindingException {
	
	if (symbolTable == null){
		symbolTableEntry = null;
		return;
	}	
	
	symbolTableEntry = symbolTable.getEntry(name);

	if (symbolTableEntry==null){
		String id = name;
		throw new ExpressionBindingException("'" + id + "' is either not found in your model or is not allowed to " +
				"be used in the current context. Check that you have provided the correct and full name (e.g. Ca_Cytosol).");
	}
}    
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public Node copyTree() {
	ASTIdNode node = new ASTIdNode(this);
	return node;	
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public Node copyTreeBinary() {
	ASTIdNode node = new ASTIdNode(this);
	return node;	
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @param variable java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public Node differentiate(String variable) throws ExpressionException {
	//===============================================================================================
	//
	//                      |
	//                      |  1                      if i == j
	//                      |
	//                      |
	//                      |  0                      if i != j and X is a terminal node
	//      d  Xi           |
	//     ------   =       |  
	//      d  Xj           |  d  expression(Xi)
	//                      |  -----------------      if i != j  and X is not a terminal node
	//                      |        d Xj
	//                      |
	//
	//===============================================================================================
	
	//
	// i == j (taking derivative wrt itself)
	//
	if (variable.equals(name)){
		return new ASTFloatNode(1.0);
	}
	
	//
	// i != j   and   terminal node  (no symbolTableEntry)
	//
	if (symbolTableEntry == null){
		return new ASTFloatNode(0.0);
	}	

	//
	// i != j   and   terminal node  (null expression)
	//
	Expression exp = symbolTableEntry.getExpression();
	if (exp==null){
		return new ASTFloatNode(0.0);
	}
	
	//
	// i != j   and non-terminal node, call differentiate on subexpression
	//
	SimpleNode rootNode = exp.getRootNode();
	return rootNode.differentiate(variable);

//
// generate DerivativeNode for term
//
//	DerivativeNode derivativeNode = new DerivativeNode("DerivativeNode",variable);
//	derivativeNode.jjtAddChild(new ASTIdNode(this));
//	return derivativeNode;
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
	// check this node for same state (identifier)
	//	
	ASTIdNode idNode = (ASTIdNode)node;
	if (!idNode.name.equals(name)){
		return false;
	}

	return true;
}
public double evaluateConstant() throws ExpressionException {

	if (symbolTableEntry == null){
		throw new ExpressionException("tryin to evaluate unbound identifier '"+infixString(LANGUAGE_DEFAULT)+"'");
	}	

	if (symbolTableEntry.isConstant()){
		return symbolTableEntry.getConstantValue();
	}	

	throw new ExpressionException("Symbol '" + name + "' cannot be evaluated as a constant.");

/*
	if (symbolTableEntry==null){
		String id = name;
		if (modifier!=null){
			id = id + "." + modifier;
		}
		throw new Exception("referencing unbound identifier " + id);
	}
	  
	return symbolTableEntry.getCurrValue();		
*/
}        
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException {

	if (symbolTableEntry==null){
		String id = name;
		throw new ExpressionBindingException("referencing unbound identifier " + id);
	}

	Expression exp = symbolTableEntry.getExpression();
	if (exp!=null){
		setInterval(exp.evaluateInterval(intervals),intervals);
		return getInterval(intervals);
	}else{
		if (symbolTableEntry.getIndex()<0){
			throw new ExpressionBindingException("referenced symbol table entry "+name+" not bound to an index");
		}
		setInterval(intervals[symbolTableEntry.getIndex()],intervals);
		return getInterval(intervals);
	}
}        
public double evaluateVector(double values[]) throws ExpressionException {

	if (symbolTableEntry==null){
		String id = name;
		throw new ExpressionBindingException("referencing unbound identifier " + id);
	}

	Expression exp = symbolTableEntry.getExpression();
	if (exp!=null){
		return exp.evaluateVector(values);
	}else{
		if (symbolTableEntry.getIndex()<0){
			throw new ExpressionBindingException("referenced symbol table entry "+name+" not bound to an index");
		}
		return values[symbolTableEntry.getIndex()];
	}
}        
/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
public Node flatten() throws ExpressionException {
	try {
		double retval = evaluateConstant();
		ASTFloatNode floatNode = new ASTFloatNode(retval);
		return floatNode;
	}catch (ExpressionException e){
	}			
	return copyTree();
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param symbol java.lang.String
 */
public SymbolTableEntry getBinding(String symbol) {
	if (name.equals(symbol)){
		return symbolTableEntry;
	}else{
		return null;
	}		
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 10:55:57 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 */
public RealInterval getInterval(RealInterval intervals[]) throws ExpressionBindingException {
	if (symbolTableEntry != null){
		if (symbolTableEntry instanceof ConstraintSymbolTableEntry && ((ConstraintSymbolTableEntry)symbolTableEntry).dontNarrow()){
			RealInterval ri = intervals[symbolTableEntry.getIndex()];
			return new RealInterval(ri.lo(),ri.hi());
		}else{
			return intervals[symbolTableEntry.getIndex()];
		}
	}else{
		throw new ExpressionBindingException("referencing unbound identifier " + name);
	}
}
/**
 * This method was created by a SmartGuide.
 * @return java.lang.String[]
 */
@Override
public void getSymbols(int language, Set<String> symbolSet) {
	symbolSet.add(infixString(language));
}

public static final String VISIT_RESERVED_X = "___X___";
public static final String VISIT_RESERVED_Y = "___Y___";
public static final String VISIT_RESERVED_Z = "___Z___";
public static final String VISIT_RESERVED_T = "___T___";
public String infixString(int lang) {
	String idName = name;
	if (lang == LANGUAGE_DEFAULT) {
		return idName;
	}else if (lang == LANGUAGE_MATLAB){	
		return TokenMangler.getEscapedTokenMatlab(idName);
	}else if (lang == LANGUAGE_JSCL) {
		return TokenMangler.getEscapedTokenJSCL(idName);
	}else if (lang == LANGUAGE_ECLiPSe) {
		return TokenMangler.getEscapedTokenECLiPSe(idName);
	}else if (lang == LANGUAGE_VISIT) {
		if (idName.equals("x")){
			return VISIT_RESERVED_X;
		}else if (idName.equals("y")){
			return VISIT_RESERVED_Y;
		}else if (idName.equals("z")){
			return VISIT_RESERVED_Z;
		}else if (idName.equals("t")){
			return VISIT_RESERVED_T;
		}
		return idName;
	} else {
		return idName;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 4:59:25 PM)
 * @return boolean
 */
public boolean narrow(RealInterval intervals[]) {
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 10:55:57 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 */
public void setInterval(RealInterval interval, RealInterval intervals[]) throws ExpressionBindingException {
	if (symbolTableEntry != null){
		if (!(symbolTableEntry instanceof ConstraintSymbolTableEntry) || !((ConstraintSymbolTableEntry)symbolTableEntry).dontNarrow()){
			intervals[symbolTableEntry.getIndex()] = interval;
		}
	}else{
		throw new ExpressionBindingException("referencing unbound identifier " + name);		
	}
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "IdNode (" + name + ")";
}
@Override
public void renameBoundSymbols(NameScope nameScope) throws ExpressionBindingException {
	if (symbolTableEntry == null) {
		throw new ExpressionBindingException("error renaming unbound identifier '" + name + "'");
	}
	
	name = nameScope.getSymbolName(symbolTableEntry);
}

	public Node convertToRvachevFunction() 
	{
		return copyTree();
	}
}

package cbit.vcell.parser;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/* JJT: 0.2.2 */

import net.sourceforge.interval.ia_math.*;

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
		throw new ExpressionBindingException("'" + id + "' is not found in your model. " 
				+ "Check that you have provided the correct and full name (e.g. Ca_Cytosol).");
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
public boolean equals(Node node) throws ExpressionException {
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
		throw new ExpressionException("tryin to evaluate unbound identifier '"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
	}	

	if (symbolTableEntry.isConstant()){
		return symbolTableEntry.getConstantValue();
	}	

	throw new ExpressionException("IdNode cannot be evaluated as a constant, (until 'Constants' are treated properly)");

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
public String[] getSymbols(int language, NameScope nameScope) {
	String array[] = new String[1];
	array[0] = infixString(language,nameScope);
	return array;
}
public String infixString(int lang, NameScope nameScope) {
	String idName;
	if (nameScope == null){
		idName = name;
	}else{
		if (symbolTableEntry!=null){
			idName = nameScope.getSymbolName(symbolTableEntry);
		}else{
			idName = nameScope.getUnboundSymbolName(name);
		}
	}
	if (lang == LANGUAGE_DEFAULT) {
		return idName;
	} else if (lang == LANGUAGE_C){
		return idName;
	} else if (lang == LANGUAGE_MATLAB){	
		return org.vcell.util.TokenMangler.getEscapedTokenMatlab(idName);
	}else if (lang == LANGUAGE_JSCL) {
		return org.vcell.util.TokenMangler.getEscapedTokenJSCL(idName);
	}else if (lang == LANGUAGE_ECLiPSe) {
		return org.vcell.util.TokenMangler.getEscapedTokenECLiPSe(idName);
	}else{
		throw new RuntimeException("Lanaguage '"+lang+" not supported");
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
public void substituteBoundSymbols() throws ExpressionException {
	if (symbolTableEntry == null) {
		throw new ExpressionBindingException("error substituting unbound identifier " + name);
	}else{
		String newName = symbolTableEntry.getName();
		if (newName!=null && !newName.equals("null")){
			name = newName;
		}else{
			throw new ExpressionBindingException("error substituting bound identifier with 'null' syntax");
		}
	}
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "IdNode (" + name + ")";
}
}

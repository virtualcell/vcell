package edu.uchc.vcell.expression.internal;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/* JJT: 0.2.2 */
import net.sourceforge.interval.ia_math.RealInterval;

import org.vcell.expression.ConstraintSymbolTableEntry;
import org.vcell.expression.ExpressionBindingException;
import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionUtilities;
import org.vcell.expression.IExpression;
import org.vcell.expression.NameScope;
import org.vcell.expression.SymbolTable;
import org.vcell.expression.SymbolTableEntry;

/**
 */
public class ASTIdNode extends SimpleNode {

  String name = null;
  transient SymbolTableEntry symbolTableEntry = null;

ASTIdNode() {
	super(ExpressionParserTreeConstants.JJTIDNODE);
  }    
/**
 * Constructor for ASTIdNode.
 * @param id int
 */
ASTIdNode(int id) {
	super(id);
if (id != ExpressionParserTreeConstants.JJTIDNODE){ System.out.println("ASTIdNode(), id = "+id); }

  }    
/**
 * This method was created by a SmartGuide.
 * @param node ASTIdNode
 */
ASTIdNode ( ASTIdNode node ) {
	super(node.id);
	this.name = node.name;
	this.symbolTableEntry = node.symbolTableEntry;
}
  /** Bind method, identifiers bind themselves to ValueObjects * @param symbolTable SymbolTable
   * @throws ExpressionBindingException
   * @see edu.uchc.vcell.expression.internal.Node#bind(SymbolTable)
   */
public void bind(SymbolTable symbolTable) throws ExpressionBindingException {
	
	if (symbolTable == null){
		symbolTableEntry = null;
		return;
	}	
	
	symbolTableEntry = symbolTable.getEntry(name);

	if (symbolTableEntry==null){
		String id = name;
		throw new ExpressionBindingException("error binding identifier " + id);
	}
}    
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 * @see edu.uchc.vcell.expression.internal.Node#copyTree()
 */
public Node copyTree() {
	ASTIdNode node = new ASTIdNode(this);
	return node;	
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 * @see edu.uchc.vcell.expression.internal.Node#copyTreeBinary()
 */
public Node copyTreeBinary() {
	ASTIdNode node = new ASTIdNode(this);
	return node;	
}
/**
 * This method was created by a SmartGuide.
 * @param variable java.lang.String
 * @param derivativePolicy DerivativePolicy
 * @return cbit.vcell.parser.Node
 * @throws ExpressionException
 * @exception java.lang.Exception The exception description.
 * @see edu.uchc.vcell.expression.internal.Node#differentiate(String, DerivativePolicy)
 */
public Node differentiate(String variable, DerivativePolicy derivativePolicy) throws ExpressionException {
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
	
	//            d A
	// if it is  ----- then we consult the derivative policy
	//            d B
	//
	// if the policy doesn't suggest a new identifier, then carry on with the default behavior
	//
	if (derivativePolicy!=null){
		String newSymbol = derivativePolicy.newSymbolForDerivative(name, symbolTableEntry, variable);
		if (newSymbol!=null){
			ASTIdNode newIdNode = new ASTIdNode();
			newIdNode.name = newSymbol;
			return newIdNode;
		}
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
	IExpression exp = symbolTableEntry.getExpression();
	if (exp==null){
		return new ASTFloatNode(0.0);
	}
	
	//
	// i != j   and non-terminal node, call differentiate on subexpression
	//
	if (exp instanceof Expression){
		SimpleNode rootNode = ((Expression)exp).getRootNode();
		return rootNode.differentiate(variable, derivativePolicy);
	}else{
		throw new RuntimeException("cannot apply chain rule over different classes of expressions");
	}

//
// generate DerivativeNode for term
//
//	DerivativeNode derivativeNode = new DerivativeNode("DerivativeNode",variable);
//	derivativeNode.jjtAddChild(new ASTIdNode(this));
//	return derivativeNode;
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
	// check this node for same state (identifier)
	//	
	ASTIdNode idNode = (ASTIdNode)node;
	if (!idNode.name.equals(name)){
		return false;
	}

	return true;
}
/**
 * Method evaluateConstant.
 * @return double
 * @throws ExpressionException
 * @see edu.uchc.vcell.expression.internal.Node#evaluateConstant()
 */
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
/**
 * Method evaluateInterval.
 * @param intervals RealInterval[]
 * @return RealInterval
 * @throws ExpressionException
 * @see edu.uchc.vcell.expression.internal.Node#evaluateInterval(RealInterval[])
 */
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException {

	if (symbolTableEntry==null){
		String id = name;
		throw new ExpressionBindingException("referencing unbound identifier " + id);
	}

	IExpression exp = symbolTableEntry.getExpression();
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
/**
 * Method evaluateVector.
 * @param values double[]
 * @return double
 * @throws ExpressionException
 * @see edu.uchc.vcell.expression.internal.Node#evaluateVector(double[])
 */
public double evaluateVector(double values[]) throws ExpressionException {

	if (symbolTableEntry==null){
		String id = name;
		throw new ExpressionBindingException("referencing unbound identifier " + id);
	}

	IExpression exp = symbolTableEntry.getExpression();
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
 * @return Node
 * @exception java.lang.Exception The exception description.
 * @see edu.uchc.vcell.expression.internal.Node#flatten()
 */
public Node flatten() {
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
 * @param symbol java.lang.String
 * @return cbit.vcell.parser.SymbolTableEntry
 * @see edu.uchc.vcell.expression.internal.Node#getBinding(String)
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
 * @param intervals RealInterval[]
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @see edu.uchc.vcell.expression.internal.Node#getInterval(RealInterval[])
 */
public RealInterval getInterval(RealInterval intervals[]) {
	if (symbolTableEntry != null){
		if (symbolTableEntry instanceof ConstraintSymbolTableEntry && ((ConstraintSymbolTableEntry)symbolTableEntry).dontNarrow()){
			RealInterval ri = intervals[symbolTableEntry.getIndex()];
			return new RealInterval(ri.lo(),ri.hi());
		}else{
			return intervals[symbolTableEntry.getIndex()];
		}
	}else{
		throw new RuntimeException("referencing unbound identifier " + name);
	}
}
/**
 * This method was created by a SmartGuide.
 * @param language int
 * @param nameScope NameScope
 * @return java.lang.String[]
 * @see edu.uchc.vcell.expression.internal.Node#getSymbols(int, NameScope)
 */
public String[] getSymbols(int language, NameScope nameScope) {
	String array[] = new String[1];
	array[0] = infixString(language,nameScope);
	return array;
}
/**
 * Method infixString.
 * @param lang int
 * @param nameScope NameScope
 * @return String
 * @see edu.uchc.vcell.expression.internal.Node#infixString(int, NameScope)
 */
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
	if (lang == LANGUAGE_DEFAULT || lang == LANGUAGE_C){
		return idName;
	}else if (lang == LANGUAGE_MATLAB){
		return org.vcell.util.TokenMangler.getEscapedTokenMatlab(idName);
	}else if (lang == LANGUAGE_JSCL) {
		return ExpressionUtilities.getEscapedTokenJSCL(idName);
	}else if (lang == LANGUAGE_ECLiPSe) {
		return org.vcell.util.TokenMangler.getEscapedTokenECLiPSe(idName);
	}else{
		throw new RuntimeException("Lanaguage '"+lang+" not supported");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 4:59:25 PM)
 * @param intervals RealInterval[]
 * @return boolean
 * @see edu.uchc.vcell.expression.internal.Node#narrow(RealInterval[])
 */
public boolean narrow(RealInterval intervals[]) {
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 10:55:57 AM)
 * @param interval RealInterval
 * @param intervals RealInterval[]
 * @see edu.uchc.vcell.expression.internal.Node#setInterval(RealInterval, RealInterval[])
 */
public void setInterval(RealInterval interval, RealInterval intervals[]) {
	if (symbolTableEntry != null){
		if (!(symbolTableEntry instanceof ConstraintSymbolTableEntry) || !((ConstraintSymbolTableEntry)symbolTableEntry).dontNarrow()){
			intervals[symbolTableEntry.getIndex()] = interval;
		}
	}else{
		throw new RuntimeException("referencing unbound identifier " + name);		
	}
}
/**
 * Method substituteBoundSymbols.
 * @throws ExpressionBindingException
 * @see edu.uchc.vcell.expression.internal.Node#substituteBoundSymbols()
 */
public void substituteBoundSymbols() throws ExpressionBindingException {
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
/**
 * Method getName.
 * @return String
 */
public String getName() {
	return name;
}
/**
 * Method getSymbolTableEntry.
 * @return SymbolTableEntry
 */
public SymbolTableEntry getSymbolTableEntry() {
	return symbolTableEntry;
}
}

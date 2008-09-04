package cbit.vcell.parser;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import net.sourceforge.interval.ia_math.RealInterval;
import cbit.util.Matchable;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.math.CommentStringTokenizer;
import cbit.vcell.math.MathException;
import cbit.vcell.simdata.ExternalDataIdentifier;

public class Expression implements java.io.Serializable, cbit.util.Matchable {

//   private String expString = null;
	private SimpleNode rootNode = null;
   private String normalizedInfixString = null;
   
   private static long flattenCount = 0;
   private static long diffCount = 0;
   private static long parseCount = 0;
   private static long derivativeCount = 0;
   private static long substituteCount = 0;
   private static long bindCount = 0;
   
   public static final int GRADIENT_NUM_SPATIAL_ELEMENTS = 13;
   public static final String GRAD_MAGNITUDE = "m";
   public static final String GRAD_X = "x";
   public static final String GRAD_Y = "y";
   public static final String GRAD_Z = "z";
  
/**
 * This method was created in VisualAge.
 * @param value double
 */
private Expression() {
}
/**
 * This method was created in VisualAge.
 * @param value double
 */
public Expression(double value) {
	this.rootNode = new ASTFloatNode(value);
	this.normalizedInfixString = Double.toString(value);
}
/**
 * This method was created by a SmartGuide.
 * @param exp cbit.vcell.parser.Expression
 */
public Expression (Expression exp) {
   this.rootNode = (SimpleNode)exp.rootNode.copyTree();
   this.normalizedInfixString = exp.normalizedInfixString;
}
/**
 * This method was created by a SmartGuide.
 * @param rootNode cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
Expression ( SimpleNode rootNode ) {
   this.rootNode = rootNode;
}
/**
 * This method was created by a SmartGuide.
 * @param expString java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public Expression ( String expString ) throws ExpressionException {
	//
	// first see if it is a number (should be must faster not to invoke the expression parser).
	//
	try {
		double value = Double.parseDouble(expString);
		this.rootNode = new ASTFloatNode(value);
		this.normalizedInfixString = Double.toString(value);
		return;
	}catch (Exception e){
	}
	//
	// not just a number, must parse string
	//
	if (!expString.trim().endsWith(";")){
		expString = expString.trim()+";";
	}
	parseExpression(expString);
}
public Expression(CommentStringTokenizer tokens) throws ExpressionException {
	read(tokens);
}  


public static void addFieldFuncArgsAndExpToCollection(Hashtable<FieldFunctionArguments, Vector<Expression>> fieldFuncArgsAndExpHash,Expression expression){
	if(expression == null){
		return;
	}
	FieldFunctionArguments[] fieldFuncArgs =
			expression.getFieldFunctionArguments();
	for(int i=0;i<fieldFuncArgs.length;i+= 1){
		Vector<Expression> expV = null;
		if(fieldFuncArgsAndExpHash.contains(fieldFuncArgs[i])){
			 expV= fieldFuncArgsAndExpHash.get(fieldFuncArgs[i]);
			
		}else{
			expV = new Vector<Expression>();
			fieldFuncArgsAndExpHash.put(fieldFuncArgs[i],expV);
			
		}
		expV.add(expression);
	}
}

public static void substituteFieldFuncNames(
		Hashtable<String, ExternalDataIdentifier> oldFieldFuncArgsNameNewID,
		Hashtable<FieldFunctionArguments, Vector<Expression>> fieldFuncArgsExpHash
		) throws MathException, ExpressionException{

	Set<Map.Entry<FieldFunctionArguments, Vector<Expression>>> set = fieldFuncArgsExpHash.entrySet();
	Iterator<Entry<FieldFunctionArguments, Vector<Expression>>> iter = set.iterator();
	while(iter.hasNext()){
		Entry<FieldFunctionArguments, Vector<Expression>> entry = iter.next();
		for(int i=0;i<entry.getValue().size();i+= 1){
			entry.getValue().elementAt(i).substituteFieldFunctionFieldName(oldFieldFuncArgsNameNewID);
		}
	}
}

/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Expression
 * @param expression1 cbit.vcell.model.Expression
 * @param expression2 cbit.vcell.model.Expression
 * @exception java.lang.Exception The exception description.
 */
public static Expression add(Expression expression1, Expression expression2) throws ExpressionException {
	Expression exp = new Expression();
	ASTAddNode addNode = new ASTAddNode();

	SimpleNode termNode = (SimpleNode)expression1.rootNode.copyTree();
	if (termNode instanceof ASTExpression){
		if (termNode.jjtGetNumChildren() == 1){
			termNode = (SimpleNode)termNode.jjtGetChild(0);
			termNode.jjtSetParent(null);
		}
	}		
	addNode.jjtAddChild(termNode);
	
   	
	termNode = (SimpleNode)expression2.rootNode.copyTree();
	if (termNode instanceof ASTExpression){
		if (termNode.jjtGetNumChildren() == 1){
			termNode = (SimpleNode)termNode.jjtGetChild(0);
			termNode.jjtSetParent(null);
		}
	}		
	addNode.jjtAddChild(termNode);
	
   	
	exp.rootNode = addNode;
	return exp;
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Expression
 * @param lvalueExp cbit.vcell.model.Expression
 * @param rvalueExp cbit.vcell.model.Expression
 * @exception java.lang.Exception The exception description.
 */
public static Expression assign(Expression lvalueExp, Expression rvalueExp) throws ExpressionException {
	Expression exp = new Expression();
	ASTAssignNode assignNode = new ASTAssignNode();

	SimpleNode termNode = (SimpleNode)lvalueExp.rootNode.copyTree();
	if (termNode instanceof ASTExpression){
		if (termNode.jjtGetNumChildren() == 1){
			termNode = (SimpleNode)termNode.jjtGetChild(0);
			termNode.jjtSetParent(null);
		}
	}		
	assignNode.jjtAddChild(termNode);
	
   	
	termNode = (SimpleNode)rvalueExp.rootNode.copyTree();
	if (termNode instanceof ASTExpression){
		if (termNode.jjtGetNumChildren() == 1){
			termNode = (SimpleNode)termNode.jjtGetChild(0);
			termNode.jjtSetParent(null);
		}
	}		
	assignNode.jjtAddChild(termNode);
	
   	
	exp.rootNode = assignNode;
	return exp;
}
   public void bindExpression(SymbolTable symbolTable) throws ExpressionBindingException
   {
bindCount++;/////////////////
	  rootNode.bind(symbolTable);
   }                     
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (!(obj instanceof Expression)){
		return false;
	}
	Expression exp = (Expression)obj;
	return getNormalizedInfixString().equals(exp.getNormalizedInfixString());
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Expression
 * @param variable String
 * @exception java.lang.Exception The exception description.
 */
public static Expression derivative(String variable, Expression expression) throws ExpressionException {
derivativeCount++;
//
	Expression exp = new Expression();
	DerivativeNode derivNode = new DerivativeNode(variable);

	SimpleNode termNode = expression.rootNode;
	if (termNode instanceof ASTExpression){
		if (termNode.jjtGetNumChildren() == 1){
			termNode = (SimpleNode)termNode.jjtGetChild(0);
			termNode.jjtSetParent(null);
		}
	}		
	derivNode.jjtAddChild(termNode);	
   	
	exp.rootNode = derivNode;
	return exp;
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Expression
 * @param variable String
 * @exception java.lang.Exception The exception description.
 */
public Expression differentiate(String variable) throws ExpressionException {
diffCount++;
	SimpleNode node = (SimpleNode)rootNode.differentiate(variable);
	if (node == null){
		throw new ExpressionException("derivative wrt "+variable+" returned null");
	}
	return new Expression(node);	
}
/**
 * Insert the method's description here.
 * Creation date: (5/24/2001 10:20:50 PM)
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (obj instanceof Expression){
		return compareEqual((Matchable)obj);
	}else{
		return false;
	}
}
/**
 * This method was created by a SmartGuide.
 * @return double
 * @exception java.lang.Exception The exception description.
 */
public double evaluateConstant() throws ExpressionException, DivideByZeroException {
	return rootNode.evaluateConstant();
}
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException, DivideByZeroException {
	return rootNode.evaluateInterval(intervals);
}         
public double evaluateVector(double values[]) throws ExpressionException, DivideByZeroException {
	return rootNode.evaluateVector(values);
}         
/**
 * Insert the method's description here.
 * Creation date: (1/23/2003 7:05:26 PM)
 * @return cbit.vcell.parser.Expression[]
 */
public ExpressionTerm extractTopLevelTerm() {
	String operator = null;
	if (rootNode instanceof ASTAddNode){
		operator = "+";
	}else if (rootNode instanceof ASTAndNode){
		operator = "&&";
	}else if (rootNode instanceof ASTAssignNode){
		operator = "=";
	}else if (rootNode instanceof ASTExpression){
		operator = "";
	}else if (rootNode instanceof ASTFloatNode){
		operator = "number";
	}else if (rootNode instanceof ASTFuncNode){
		operator = ((ASTFuncNode)rootNode).getName()+"()";
	}else if (rootNode instanceof ASTIdNode){
		operator = "identifier";
	}else if (rootNode instanceof ASTInvertTermNode){
		operator = "/";
	}else if (rootNode instanceof ASTLaplacianNode){
		operator = "laplacian";
	}else if (rootNode instanceof ASTMinusTermNode){
		operator = "-";
	}else if (rootNode instanceof ASTMultNode){
		operator = "*";
	}else if (rootNode instanceof ASTNotNode){
		operator = "not";
	}else if (rootNode instanceof ASTOrNode){
		operator = "or";
	}else if (rootNode instanceof ASTPowerNode){
		operator = "^";
	}else if (rootNode instanceof ASTRelationalNode){
		operator = ((ASTRelationalNode)rootNode).getOperation();
	}else{
		throw new RuntimeException("node "+rootNode.getClass().getName()+" not yet implemented");
	}
	
	Expression children[] = new Expression[rootNode.jjtGetNumChildren()];
	for (int i = 0; i < rootNode.jjtGetNumChildren(); i++){
		children[i] = new Expression((SimpleNode)rootNode.jjtGetChild(i).copyTree());
	}
	
	return new ExpressionTerm(operator,children);
}
/**
 * This method was created by a SmartGuide.
 */
public Expression flatten() throws ExpressionException {
flattenCount++;////////////////////////
	return new Expression((SimpleNode)rootNode.flatten());
}
/**
 * This method was created by a SmartGuide.
 */
public Expression getBinaryExpression() {
	return new Expression((SimpleNode)rootNode.copyTreeBinary());

}
   public static String getExpression(StreamTokenizer token) throws ExpressionException, IOException
   {

	  StringBuffer buffer = new StringBuffer(0);

	  while (token.ttype != StreamTokenizer.TT_EOF &&
			 token.ttype != ';'){

		 if (token.ttype == StreamTokenizer.TT_WORD){
			buffer.append(token.sval);
		 }else if (token.ttype == StreamTokenizer.TT_NUMBER){
			buffer.append(token.nval);
		 }else{
			buffer.append((char)(token.ttype));
		 }

		 token.nextToken();

	  } // end while(token)

	  if (token.ttype == ';'){
		 buffer.append(";");
		 return buffer.toString();
	  }else{
		 throw new ExpressionException("unexpected EOF while parsing Expression at line " + 
							 String.valueOf(token.lineno()));
	  }

   }         
public boolean hasGradient(){
	return rootNode.hasGradient();
}

public void substituteFieldFunctionFieldName(Hashtable<String, ExternalDataIdentifier> substituteNamesHash){
	normalizedInfixString = null;
	rootNode.substituteFieldFunctionFieldName(substituteNamesHash);
}

public FieldFunctionArguments[] getFieldFunctionArguments() {
	Vector v = new Vector();
	rootNode.getFieldFunctionArguments(v);
	FieldFunctionArguments[] funcs = new FieldFunctionArguments[v.size()];
	v.copyInto(funcs);
	return funcs;
}

/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 9:32:34 AM)
 * @return java.lang.String
 */
private String getNormalizedInfixString() {

	if (normalizedInfixString==null){
		try {
			Expression clonedExp = new Expression(this);
			clonedExp.bindExpression(null);
			normalizedInfixString = clonedExp.flatten().infix();
		}catch(ExpressionException e){
			e.printStackTrace(System.out);
			normalizedInfixString = infix();
		}
	}
	return normalizedInfixString;
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.SimpleNode
 */
SimpleNode getRootNode() {
	return rootNode;
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Expression
 * @param origExp cbit.vcell.parser.Expression
 * @param newExp cbit.vcell.parser.Expression
 * @exception java.lang.Exception The exception description.
 */
public Expression getSubstitutedExpression(Expression origExp, Expression newExp) throws ExpressionException {
substituteCount++;////////////////////////////////
	SimpleNode origNode = origExp.rootNode;
	if (origNode instanceof ASTExpression){
		origNode = (SimpleNode)origNode.jjtGetChild(0);
	}	
	SimpleNode newNode = (SimpleNode)newExp.rootNode.copyTree();
	if (newNode instanceof ASTExpression){
		newNode = (SimpleNode)newNode.jjtGetChild(0).copyTree();
	}else{
		newNode = (SimpleNode)newNode.copyTree();
	}
	//
	// first check if must replace entire tree, if not then leaves can deal with it
	//
	if (origNode.equals(this.rootNode)){
		return new Expression(newNode);
	}else{
		SimpleNode clonedRootNode = (SimpleNode)rootNode.copyTree();
		clonedRootNode.substitute(origNode,newNode);
		return new Expression(clonedRootNode);
	}
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param symbolName java.lang.String
 */
public SymbolTableEntry getSymbolBinding(String symbol) {
	return rootNode.getBinding(symbol);
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.String[]
 * @exception java.lang.Exception The exception description.
 */
public String[] getSymbols() {
	return rootNode.getSymbols(SimpleNode.LANGUAGE_DEFAULT,SimpleNode.NAMESCOPE_DEFAULT);
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.String[]
 * @exception java.lang.Exception The exception description.
 */
public String[] getSymbols(int language, NameScope nameScope) {
	return rootNode.getSymbols(language, nameScope);
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.String[]
 * @exception java.lang.Exception The exception description.
 */
public String[] getSymbols(NameScope nameScope) {
	return rootNode.getSymbols(SimpleNode.LANGUAGE_DEFAULT, nameScope);
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.String[]
 * @exception java.lang.Exception The exception description.
 */
public Iterator getSymbolsIterator() {
	return Arrays.asList(rootNode.getSymbols(SimpleNode.LANGUAGE_DEFAULT,SimpleNode.NAMESCOPE_DEFAULT)).iterator();
}
/**
 * Insert the method's description here.
 * Creation date: (5/24/2001 10:22:45 PM)
 * @return int
 */
public int hashCode() {
	return getNormalizedInfixString().hashCode();
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.String[]
 * @exception java.lang.Exception The exception description.
 */
public boolean hasSymbol(String symbolName) {
	String[] symbols = getSymbols();
	for (int i = 0 ; symbols != null && i < symbols.length; i++){
		if (symbolName.equals(symbols[i])) {
			return true;
		}
	}
	return false;
}
   public String infix()
   {
	  return infix(SimpleNode.NAMESCOPE_DEFAULT);
   }   
   public String infix(NameScope nameScope)
   {
	  if (rootNode==null){
		 return null;
	  }else{
		 return rootNode.infixString(SimpleNode.LANGUAGE_DEFAULT,nameScope);
	  }
   }   
   public String infix_C()
   {
	  return infix_C(SimpleNode.NAMESCOPE_DEFAULT);
   }   
   public String infix_C(NameScope nameScope)
   {
	  if (rootNode==null){
		 return null;
	  }else{
		 return rootNode.infixString(SimpleNode.LANGUAGE_C, nameScope);
	  }
   }   
   public String infix_ECLiPSe()
   {
	  return infix_ECLiPSe(SimpleNode.NAMESCOPE_DEFAULT);
   }   
   public String infix_ECLiPSe(NameScope nameScope)
   {
	  if (rootNode==null){
		 return null;
	  }else{
		 return rootNode.infixString(SimpleNode.LANGUAGE_ECLiPSe,nameScope);
	  }
   }   
   public String infix_JSCL()
   {
	  return infix_JSCL(SimpleNode.NAMESCOPE_DEFAULT);
   }   
   public String infix_JSCL(NameScope nameScope)
   {
	  if (rootNode==null){
		 return null;
	  }else{
		 return rootNode.infixString(SimpleNode.LANGUAGE_JSCL,nameScope);
	  }
   }   
   public String infix_Matlab()
   {
	  return infix_Matlab(SimpleNode.NAMESCOPE_DEFAULT);
   }   
   public String infix_Matlab(NameScope nameScope)
   {
	  if (rootNode==null){
		 return null;
	  }else{
		 return rootNode.infixString(SimpleNode.LANGUAGE_MATLAB,nameScope);
	  }
   }   
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Expression
 * @param expression Expression
 * @exception java.lang.Exception The exception description.
 */
public static Expression invert(Expression expression) throws ExpressionException {
	Expression exp = new Expression();
	ASTInvertTermNode invertNode = new ASTInvertTermNode();
	//
	// get rid of ExpressionNode (worthless for evaluation)
	//
	SimpleNode termNode = (SimpleNode)expression.rootNode.copyTree();
	if (termNode instanceof ASTExpression){
		if (termNode.jjtGetNumChildren() == 1){
			termNode = (SimpleNode)termNode.jjtGetChild(0);
			termNode.jjtSetParent(null);
		}
	}	
	//
	// get rid of double invert
	//
	if (termNode instanceof ASTInvertTermNode){
		exp.rootNode = (SimpleNode)termNode.jjtGetChild(0);
	}else{	
		invertNode.jjtAddChild(termNode);
		ASTMultNode multNode = new ASTMultNode();
		ASTFloatNode floatNode = new ASTFloatNode(1.0);
		multNode.jjtAddChild(floatNode);
		multNode.jjtAddChild(invertNode);
		exp.rootNode = multNode;
	}	
	return exp;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2003 7:19:57 PM)
 * @return boolean
 */
public boolean isAtomic() {
	return rootNode.jjtGetNumChildren()==0;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2003 7:19:57 PM)
 * @return boolean
 */
public boolean isLogical() {
	if (rootNode instanceof ASTAndNode ||
		rootNode instanceof ASTOrNode ||
		rootNode instanceof ASTNotNode){

		return true;
	}else{
		return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 10:43:33 AM)
 * @return boolean
 */
public boolean isNumeric() {
	return (rootNode instanceof ASTFloatNode);
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2003 7:19:57 PM)
 * @return boolean
 */
public boolean isRelational() {
	if (rootNode instanceof ASTRelationalNode){
		return true;
	}else{
		return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2002 2:37:46 PM)
 * @return boolean
 */
public boolean isZero() {
	return compareEqual(new Expression(0.0));
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Expression
 * @param expression Expression
 * @exception java.lang.Exception The exception description.
 */
public static Expression laplacian(Expression expression) throws ExpressionException {
	Expression exp = new Expression();
	ASTLaplacianNode laplacianNode = new ASTLaplacianNode();
	//
	// get rid of ExpressionNode (worthless for evaluation)
	//
	SimpleNode termNode = expression.rootNode;
	if (termNode instanceof ASTExpression){
		if (termNode.jjtGetNumChildren() == 1){
			termNode = (SimpleNode)termNode.jjtGetChild(0);
			termNode.jjtSetParent(null);
		}
	}	
	laplacianNode.jjtAddChild(termNode);
	exp.rootNode = laplacianNode;
	return exp;
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Expression
 * @param expression1 cbit.vcell.model.Expression
 * @param expression2 cbit.vcell.model.Expression
 * @exception java.lang.Exception The exception description.
 */
public static Expression mult(Expression expression1, Expression expression2) throws ExpressionException {
	Expression exp = new Expression();
	ASTMultNode multNode = new ASTMultNode();

	SimpleNode termNode = (SimpleNode)expression1.rootNode.copyTree();
	if (termNode instanceof ASTExpression){
		if (termNode.jjtGetNumChildren() == 1){
			termNode = (SimpleNode)termNode.jjtGetChild(0);
			termNode.jjtSetParent(null);
		}
	}		
	multNode.jjtAddChild(termNode);
	
   	
	termNode = (SimpleNode)expression2.rootNode.copyTree();
	if (termNode instanceof ASTExpression){
		if (termNode.jjtGetNumChildren() == 1){
			termNode = (SimpleNode)termNode.jjtGetChild(0);
			termNode.jjtSetParent(null);
		}
	}		
	multNode.jjtAddChild(termNode);
	
   	
	exp.rootNode = multNode;
	return exp;
}
public boolean narrow(RealInterval intervals[]) throws ExpressionException {
	return rootNode.narrow(intervals);
}         
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Expression
 * @param expression Expression
 * @exception java.lang.Exception The exception description.
 */
public static Expression negate(Expression expression) throws ExpressionException {
	Expression exp = new Expression();
	ASTMinusTermNode minusNode = new ASTMinusTermNode();
	SimpleNode termNode = (SimpleNode)expression.rootNode.copyTree();
	//
	// get rid of ExpressionNode (worthless for evaluation)
	//
	if (termNode instanceof ASTExpression){
		if (termNode.jjtGetNumChildren() == 1){
			termNode = (SimpleNode)termNode.jjtGetChild(0);
			termNode.jjtSetParent(null);
		}
	}	
	//
	// get rid of double negative
	//
	if (termNode instanceof ASTMinusTermNode){
		exp.rootNode = (SimpleNode)termNode.jjtGetChild(0);
	}else{	
		minusNode.jjtAddChild(termNode);
		exp.rootNode = minusNode;
	}	
	return exp;
}
private void parseExpression(String exp) throws ExpressionException {
parseCount++;
	try {
		//System.out.println("expression: " + exp);
		ExpressionParser parser;
		parser = new ExpressionParser(new java.io.ByteArrayInputStream(exp.getBytes()));
		rootNode = parser.Expression();

		if (rootNode instanceof ASTExpression){
			if (rootNode.jjtGetNumChildren() == 1){
				rootNode = (SimpleNode)rootNode.jjtGetChild(0);
				rootNode.jjtSetParent(null);
			}
		}		
	} catch (ParseException e) {
		throw new ParserException("Parse Error while parsing expression " + exp);
	}
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Expression
 * @param expression1 cbit.vcell.model.Expression
 * @param expression2 cbit.vcell.model.Expression
 * @exception java.lang.Exception The exception description.
 */
public static Expression power(Expression expression1, Expression expression2) throws ExpressionException {
	Expression exp = new Expression();
	ASTFuncNode funcNode = new ASTFuncNode();
	funcNode.setFunction(ASTFuncNode.POW);

	SimpleNode baseNode = (SimpleNode)expression1.rootNode.copyTree();
	if (baseNode instanceof ASTExpression){
		if (baseNode.jjtGetNumChildren() == 1){
			baseNode = (SimpleNode)baseNode.jjtGetChild(0);
			baseNode.jjtSetParent(null);
		}
	}		
	funcNode.jjtAddChild(baseNode);
	
   	
	SimpleNode expNode = (SimpleNode)expression2.rootNode.copyTree();
	if (expNode instanceof ASTExpression){
		if (expNode.jjtGetNumChildren() == 1){
			expNode = (SimpleNode)expNode.jjtGetChild(0);
			expNode.jjtSetParent(null);
		}
	}		
	funcNode.jjtAddChild(expNode);
	
   	
	exp.rootNode = funcNode;
	return exp;
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 1:34:06 PM)
 * @return cbit.vcell.parser.Expression
 * @param mathML java.lang.String
 */
public static void printNode(org.w3c.dom.Node nodeArg, String pad){
	if (nodeArg== null) {
		throw new IllegalArgumentException("Invalid null NodeList");
	}
	org.w3c.dom.NodeList nodeList = nodeArg.getChildNodes();
	//print node
	if (nodeArg.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE){
		if (nodeList.getLength()==0){
			System.out.println(pad + "<"+nodeArg.getNodeName()+"/>");
		}else{
			System.out.println(pad + "<"+nodeArg.getNodeName()+">");
		}
	}else if (nodeArg.getNodeType() == org.w3c.dom.Node.TEXT_NODE){
		System.out.println(pad + nodeArg.getNodeValue());
	}else{
		System.out.println(pad + nodeArg.getNodeName() + " "+ nodeArg.getNodeType() + " " + nodeArg.getNodeValue());
	}
	//get children
	if (nodeList.getLength()==1){
		printNode(nodeList.item(0), pad + "  ");
	}else{			
		for (int i = 0; i < nodeList.getLength(); i++){
			if (i%2==1){
				printNode(nodeList.item(i), pad +"  ");
			}
		}
	}
	if (nodeList.getLength()>0 && nodeArg.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE){
		System.out.println(pad + "</"+nodeArg.getNodeName()+">");
	}
}
   public void printTree()
   {
	  if (rootNode!=null){
		 rootNode.dump("");
	  }
   }   
/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
private void read(CommentStringTokenizer tokens) throws ExpressionException {
	String expressionString = new String();
	String token = null;
	while (tokens.hasMoreTokens()){
		token = tokens.nextToken();
		if (token.equals(";")){
			break;
		}	
		if (token.charAt(token.length()-1) == ';'){
			expressionString += token.substring(0,token.length()-1);
			break;
		}	
		expressionString += token;
	}	
	//
	// first see if it is a number (should be must faster not to invoke the expression parser).
	//
	try {
		double value = Double.parseDouble(expressionString);
		this.rootNode = new ASTFloatNode(value);
		this.normalizedInfixString = Double.toString(value);
		return;
	}catch (Exception e){
	}
	//
	// not just a number, must parse string
	//
	parseExpression(expressionString+";");
}
/**
 * Insert the method's description here.
 * Creation date: (1/29/01 5:18:34 PM)
 */
public static void resetCounters() {
   flattenCount = 0;
   diffCount = 0;
   parseCount = 0;
   derivativeCount = 0;
   substituteCount = 0;
   bindCount = 0;
}
/**
 * Insert the method's description here.
 * Creation date: (10/11/2002 8:48:29 AM)
 */
public void roundToFloat() {
  if (rootNode!=null){
	 rootNode.roundToFloat();
  }
}
/**
 * Insert the method's description here.
 * Creation date: (1/29/01 5:18:34 PM)
 */
public static void showCounters() {
System.out.println("flatten("+flattenCount+")");
System.out.println("differentiate("+diffCount+")");
System.out.println("parse("+parseCount+")");
System.out.println("derivative("+derivativeCount+")");
System.out.println("substitute("+substituteCount+")");
System.out.println("bind("+bindCount+")");
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Expression
 * @param origExp cbit.vcell.parser.Expression
 * @param newExp cbit.vcell.parser.Expression
 * @exception java.lang.Exception The exception description.
 */
public void substituteInPlace(Expression origExp, Expression newExp) throws ExpressionException {
substituteCount++;////////////////////////////////
	SimpleNode origNode = origExp.rootNode;
	if (origNode instanceof ASTExpression){
		origNode = (SimpleNode)origNode.jjtGetChild(0);
	}	
	SimpleNode newNode = (SimpleNode)newExp.rootNode.copyTree();
	if (newNode instanceof ASTExpression){
		newNode = (SimpleNode)newNode.jjtGetChild(0).copyTree();
	}else{
		newNode = (SimpleNode)newNode.copyTree();
	}
	//
	// first check if must replace entire tree, if not then leaves can deal with it
	//
	if (origNode.equals(rootNode)){
		rootNode = newNode;
	}else{
		rootNode.substitute(origNode,newNode);
	}
	this.normalizedInfixString = null;
}
   public String toString()
   {
	  return "Expression@"+Integer.toHexString(hashCode())+" '"+infix()+"'";
   }   
}

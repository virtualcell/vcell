package edu.uchc.vcell.expression.internal;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;

import net.sourceforge.interval.ia_math.RealInterval;

import org.vcell.expression.DerivativePolicy;
import org.vcell.expression.DivideByZeroException;
import org.vcell.expression.ExpressionBindingException;
import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.ExpressionTerm;
import org.vcell.expression.IExpression;
import org.vcell.expression.NameScope;
import org.vcell.expression.ParserException;
import org.vcell.expression.SymbolTable;
import org.vcell.expression.SymbolTableEntry;

import cbit.util.Matchable;

public class Expression implements java.io.Serializable, cbit.util.Matchable, IExpression {

//   private String expString = null;
	private SimpleNode rootNode = null;
   private String normalizedInfixString = null;
   
   private static long flattenCount = 0;
   private static long diffCount = 0;
   private static long parseCount = 0;
   private static long derivativeCount = 0;
   private static long substituteCount = 0;
   private static long bindCount = 0;
   
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

public Expression(StringTokenizer tokens) throws ExpressionException {
	read(tokens);
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
   /* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#bindExpression(cbit.vcell.parser.SymbolTable)
 */
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
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#differentiate(java.lang.String)
 */
public IExpression differentiate(String variable) throws ExpressionException {
	return differentiate(variable,null);
}
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#differentiate(java.lang.String)
 */
public IExpression differentiate(String variable, DerivativePolicy derivativePolicy) throws ExpressionException {
diffCount++;
	SimpleNode node = (SimpleNode)rootNode.differentiate(variable, derivativePolicy);
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
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#evaluateConstant()
 */
public double evaluateConstant() throws ExpressionException, DivideByZeroException {
	return rootNode.evaluateConstant();
}
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#evaluateInterval(net.sourceforge.interval.ia_math.RealInterval[])
 */
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException, DivideByZeroException {
	return rootNode.evaluateInterval(intervals);
}         
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#evaluateVector(double[])
 */
public double evaluateVector(double values[]) throws ExpressionException, DivideByZeroException {
	return rootNode.evaluateVector(values);
}         
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#extractTopLevelTerm()
 */
public ExpressionTerm extractTopLevelTerm() {
	String functionName = null;
	ExpressionTerm.Operator operator = null;
	if (rootNode instanceof ASTAddNode){
		operator = ExpressionTerm.Operator.ADD;
	}else if (rootNode instanceof ASTAndNode){
		operator = ExpressionTerm.Operator.AND;
	}else if (rootNode instanceof ASTAssignNode){
		operator = ExpressionTerm.Operator.ASSIGN;
	}else if (rootNode instanceof ASTExpression){
		operator = ExpressionTerm.Operator.PARENTHESIS;
	}else if (rootNode instanceof ASTFloatNode){
		operator = ExpressionTerm.Operator.FLOAT;
	}else if (rootNode instanceof ASTFuncNode){
		operator = ((ASTFuncNode)rootNode).getFunction();
	}else if (rootNode instanceof ASTIdNode){
		operator = ExpressionTerm.Operator.IDENTIFIER;
	}else if (rootNode instanceof ASTInvertTermNode){
		operator = ExpressionTerm.Operator.INVERT;
	}else if (rootNode instanceof ASTLaplacianNode){
		operator = ExpressionTerm.Operator.LAPLACIAN;
	}else if (rootNode instanceof ASTMinusTermNode){
		operator = ExpressionTerm.Operator.MINUS;
	}else if (rootNode instanceof ASTMultNode){
		operator = ExpressionTerm.Operator.MULT;
	}else if (rootNode instanceof ASTNotNode){
		operator = ExpressionTerm.Operator.NOT;
	}else if (rootNode instanceof ASTOrNode){
		operator = ExpressionTerm.Operator.OR;
	}else if (rootNode instanceof ASTPowerNode){
		operator = ExpressionTerm.Operator.POWEROP;
	}else if (rootNode instanceof ASTRelationalNode){
		operator = ((ASTRelationalNode)rootNode).getOperator();
	}else{
		throw new RuntimeException("node "+rootNode.getClass().getName()+" not yet implemented");
	}
	
	IExpression children[] = new IExpression[rootNode.jjtGetNumChildren()];
	for (int i = 0; i < rootNode.jjtGetNumChildren(); i++){
		children[i] = new Expression((SimpleNode)rootNode.jjtGetChild(i).copyTree());
	}
	
	return new ExpressionTerm(operator,functionName,children);
}
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#flatten()
 */
public Expression flatten() throws ExpressionException {
flattenCount++;////////////////////////
	return new Expression((SimpleNode)rootNode.flatten());
}
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#getBinaryExpression()
 */
public IExpression getBinaryExpression() {
	return new Expression((SimpleNode)rootNode.copyTreeBinary());

}
   /**
 * Insert the method's description here.
 * Creation date: (9/15/2003 9:32:34 AM)
 * @return java.lang.String
 */
private String getNormalizedInfixString() {

	if (normalizedInfixString==null){
		try {
			IExpression clonedExp = ExpressionFactory.createExpression(this);
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
public SimpleNode getRootNode() {
	return rootNode;
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Expression
 * @param origExp cbit.vcell.parser.Expression
 * @param newExp cbit.vcell.parser.Expression
 * @exception java.lang.Exception The exception description.
 */
public IExpression getSubstitutedExpression(Expression origExp, Expression newExp) throws ExpressionException {
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
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#getSymbolBinding(java.lang.String)
 */
public SymbolTableEntry getSymbolBinding(String symbol) {
	return rootNode.getBinding(symbol);
}
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#getSymbols()
 */
public String[] getSymbols() {
	return rootNode.getSymbols(SimpleNode.LANGUAGE_DEFAULT,SimpleNode.NAMESCOPE_DEFAULT);
}
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#getSymbols(int, cbit.vcell.parser.NameScope)
 */
public String[] getSymbols(int language, NameScope nameScope) {
	return rootNode.getSymbols(language, nameScope);
}
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#getSymbols(cbit.vcell.parser.NameScope)
 */
public String[] getSymbols(NameScope nameScope) {
	return rootNode.getSymbols(SimpleNode.LANGUAGE_DEFAULT, nameScope);
}
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#getSymbolsIterator()
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
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#hasSymbol(java.lang.String)
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
   /* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#infix()
 */
public String getMathML() throws ExpressionException, IOException{
	return ExpressionMathMLPrinter.getMathML(this);
}

public String infix()
   {
	  return infix(SimpleNode.NAMESCOPE_DEFAULT);
   }   
   /* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#infix(cbit.vcell.parser.NameScope)
 */
public String infix(NameScope nameScope)
   {
	  if (rootNode==null){
		 return null;
	  }else{
		 return rootNode.infixString(SimpleNode.LANGUAGE_DEFAULT,nameScope);
	  }
   }   
   /* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#infix_C()
 */
public String infix_C()
   {
	  return infix_C(SimpleNode.NAMESCOPE_DEFAULT);
   }   
   /* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#infix_C(cbit.vcell.parser.NameScope)
 */
public String infix_C(NameScope nameScope)
   {
	  if (rootNode==null){
		 return null;
	  }else{
		 return rootNode.infixString(SimpleNode.LANGUAGE_C, nameScope);
	  }
   }   
   /* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#infix_ECLiPSe()
 */
public String infix_ECLiPSe()
   {
	  return infix_ECLiPSe(SimpleNode.NAMESCOPE_DEFAULT);
   }   
   /* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#infix_ECLiPSe(cbit.vcell.parser.NameScope)
 */
public String infix_ECLiPSe(NameScope nameScope)
   {
	  if (rootNode==null){
		 return null;
	  }else{
		 return rootNode.infixString(SimpleNode.LANGUAGE_ECLiPSe,nameScope);
	  }
   }   
   /* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#infix_JSCL()
 */
public String infix_JSCL()
   {
	  return infix_JSCL(SimpleNode.NAMESCOPE_DEFAULT);
   }   
   /* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#infix_JSCL(cbit.vcell.parser.NameScope)
 */
public String infix_JSCL(NameScope nameScope)
   {
	  if (rootNode==null){
		 return null;
	  }else{
		 return rootNode.infixString(SimpleNode.LANGUAGE_JSCL,nameScope);
	  }
   }   
   /* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#infix_Matlab()
 */
public String infix_Matlab()
   {
	  return infix_Matlab(SimpleNode.NAMESCOPE_DEFAULT);
   }   
   /* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#infix_Matlab(cbit.vcell.parser.NameScope)
 */
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
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#isAtomic()
 */
public boolean isAtomic() {
	return rootNode.jjtGetNumChildren()==0;
}
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#isLogical()
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
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#isNumeric()
 */
public boolean isNumeric() {
	return (rootNode instanceof ASTFloatNode);
}
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#isRelational()
 */
public boolean isRelational() {
	if (rootNode instanceof ASTRelationalNode){
		return true;
	}else{
		return false;
	}
}
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#isZero()
 */
public boolean isZero() {
	return compareEqual(ExpressionFactory.createExpression(0.0));
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Expression
 * @param expression Expression
 * @exception java.lang.Exception The exception description.
 */
public static IExpression laplacian(Expression expression) throws ExpressionException {
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
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#narrow(net.sourceforge.interval.ia_math.RealInterval[])
 */
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
public static IExpression power(Expression expression1, Expression expression2) throws ExpressionException {
	Expression exp = new Expression();
	ASTFuncNode funcNode = new ASTFuncNode();
	funcNode.setFunction(ExpressionTerm.Operator.POW);

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
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#printTree()
 */
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
private void read(StringTokenizer tokens) throws ExpressionException {
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
/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#roundToFloat()
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

public void substituteInPlace(IExpression origExp, IExpression newExp) throws ExpressionException {
	substituteInPlace((Expression)origExp, (Expression)newExp);
}

/* (non-Javadoc)
 * @see cbit.vcell.parser.IExpression#substituteInPlace(cbit.vcell.parser.Expression, cbit.vcell.parser.Expression)
 */
private void substituteInPlace(Expression origExp, Expression newExp) throws ExpressionException {
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

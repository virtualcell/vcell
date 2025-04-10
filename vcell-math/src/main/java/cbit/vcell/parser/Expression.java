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

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.*;

import jscl.math.Generic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.Matchable;

import cbit.vcell.matrix.RationalNumber;
import cbit.vcell.parser.ASTFuncNode.FunctionType;
import net.sourceforge.interval.ia_math.RealInterval;

@SuppressWarnings("serial")
public class Expression implements java.io.Serializable, org.vcell.util.Matchable, IssueSource {

	private final static Logger logger = LogManager.getLogger(Expression.class);

//   private String expString = null;
	private SimpleNode rootNode = null;
   private String normalizedInfixString = null;
   
   private static long flattenCount = 0;
   private static long diffCount = 0;
   private static long parseCount = 0;
   private static long derivativeCount = 0;
   private static long substituteCount = 0;
   private static long bindCount = 0;
   private static final Expression ZERO = new Expression(0);
   private static final Expression ONE = new Expression(1);


private Expression() {
}

public Expression(SymbolTableEntry ste, NameScope nameScope) {
	ASTIdNode idNode = new ASTIdNode(); 
	idNode.name = (nameScope == null)? ste.getName():nameScope.getSymbolName(ste);
	idNode.setSymbolTableEntry(ste);
	this.rootNode = idNode;
	this.normalizedInfixString = idNode.name;
}
/**
 * This method was created in VisualAge.
 * @param value double
 */
public Expression(double value) {
	this.rootNode = new ASTFloatNode(value);
	this.normalizedInfixString = Double.toString(value);
}

public Expression(RationalNumber value) {
	this.rootNode = new ASTMultNode();
	ASTFloatNode numerator = new ASTFloatNode(value.getNumBigInteger().doubleValue());
	this.rootNode.jjtAddChild(numerator,0);
	ASTInvertTermNode invNode = new ASTInvertTermNode();
	ASTFloatNode denominator = new ASTFloatNode(value.getDenBigInteger().doubleValue());
	invNode.jjtAddChild(denominator);
	this.rootNode.jjtAddChild(invNode, 1);
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
Expression (SimpleNode rootNode) {
   this.rootNode = rootNode;
}
/**
 * This method was created by a SmartGuide.
 * @param expString java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public Expression(String expString) throws ExpressionException {
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
	parseExpression(expString);
}


public static Expression add(Expression... expressions) {
	Expression exp = new Expression();
	ASTAddNode addNode = new ASTAddNode();

	for (Expression expression1 : expressions) {
		Node termNode = expression1.rootNode.copyTree();
		addNode.jjtAddChild(termNode);
	}
   	
	exp.rootNode = addNode;
	return exp;
}

public static Expression assign(Expression lvalueExp, Expression rvalueExp) throws ExpressionException {
	Expression exp = new Expression();
	ASTAssignNode assignNode = new ASTAssignNode();

	Node termNode = lvalueExp.rootNode.copyTree();
	assignNode.jjtAddChild(termNode);
	
   	
	termNode = rvalueExp.rootNode.copyTree();
	assignNode.jjtAddChild(termNode);
	
   	
	exp.rootNode = assignNode;
	return exp;
}

    public static Expression clone(Expression exp) {
	   if (exp == null){
		   return null;
	   }else{
		   return new Expression(exp);
	   }
    }

    public void bindExpression(SymbolTable symbolTable) throws ExpressionBindingException
   {
bindCount++;/////////////////
	  rootNode.bind(symbolTable);
   }

   public Expression bindExpressionAndReturn(SymbolTable symbolTable) throws ExpressionBindingException
   {
	   bindExpression(symbolTable);
	   return this;
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
//	boolean b = ExpressionUtils.functionallyEquivalent(this, exp);
	boolean b = getNormalizedInfixString().equals(exp.getNormalizedInfixString());
	return b;
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

	Node termNode = expression.rootNode.copyTree();
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
	Expression exp = new Expression(this);
	SimpleNode node = (SimpleNode)exp.rootNode.differentiate(variable);
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
	if (obj == this){
		return true;
	}
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
	return rootNode.evaluateConstant(true);
}
public double evaluateConstantSafe() throws ExpressionException, DivideByZeroException {
	return rootNode.evaluateConstant(false);
}
public double evaluateConstantWithSubstitution() throws ExpressionException, DivideByZeroException {
	return rootNode.evaluateConstant(true);
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
 * flatten performs very basic simplification, and also replaces constant SymbolTableEntry.isConstant() with numerical values
 * if this numeric substitution is not wanted either clear binding before calling, or call flattenSafe() method.
 */
public Expression flatten() throws ExpressionException {
flattenCount++;////////////////////////
	return new Expression((SimpleNode)rootNode.flatten(true));
}

/**
 * flatten will substitute numbers for constants (e.g. SymbolTableEntry.isConstant() like math Constant class)
 * flattenSafe temporarily removes binding, flattens, and restores binding
 * @return flattened expression without substituting numbers for Constants
 * @throws ExpressionException
 */
public Expression flattenSafe() throws ExpressionException {
	flattenCount++;////////////////////////
	return new Expression((SimpleNode)rootNode.flatten(false));
}

	public Expression simplifyJSCL() throws ExpressionException {
		return simplifyJSCL(100, false);
	}

	public Expression simplifyJSCL(int maxExecutionTime_ms, boolean bFailOnTimeout) throws ExpressionException, jscl.math.Expression.ExpressionTimeoutException {
		String[] symbols = getSymbols();
		if (symbols==null || symbols.length==0){
			return flatten();
		}
		try {
			Expression simplified = new Expression(this);
			SymbolTable tempExpressionSymbolTable = this.createSymbolTableFromBinding();
			simplified = simplifyUsingJSCL(simplified, maxExecutionTime_ms);
			simplified.bindExpression(tempExpressionSymbolTable);
			return simplified;
		}catch (ExpressionException e){
			logger.debug("failed to simplify using JSCL, reverting to flatten(): "+e.getMessage());
			return flattenSafe();
		}catch (jscl.math.Expression.ExpressionTimeoutException e){
			if (bFailOnTimeout){
				throw new jscl.math.Expression.ExpressionTimeoutException("simplifyJSCL() timeout for: "+infix(), e);
			}else {
				logger.debug("timeout simplify using JSCL, reverting to flatten(): " + e.getMessage());
				return flattenSafe();
			}
		}
	}


	/**
	 * for internal use only - for restoring the expression binding for safe flatten().
	 * @return SymbolTable temporary symbol table associated with this expression
	 */
	public SymbolTable createSymbolTableFromBinding(){
		String[] symbols = this.getSymbols();
		if (symbols==null || symbols.length==0){
			return null;
		}
		HashMap<String, SymbolTableEntry> bindings = new HashMap<>();
		for (String symbol : symbols){
			SymbolTableEntry symbolBinding = getSymbolBinding(symbol);
			if (symbolBinding != null) {
				bindings.put(symbol, symbolBinding);
			}
		}
		SymbolTable symbolTable = new SymbolTable() {
			@Override
			public SymbolTableEntry getEntry(String identifierString) { return bindings.get(identifierString); }
			@Override
			public void getEntries(Map<String, SymbolTableEntry> entryMap) { throw new RuntimeException("not implemented");}
			@Override
			public boolean allowPartialBinding() {return true;}
		};
		return symbolTable;
	}

	private static Expression simplifyUsingJSCL(Expression exp, int maxExecutionTime_ms) throws ExpressionException, jscl.math.Expression.ExpressionTimeoutException {
		try {
			//
			// initialize computation timeout (threadlocal var)
			//
			long startTime = System.currentTimeMillis();
			jscl.math.Expression.timeoutMS.set(startTime + maxExecutionTime_ms);

			jscl.math.Expression jsclExpression = null;
			String jsclExpressionString = exp.infix_JSCL();
			if (jsclExpressionString.contains("<") || jsclExpressionString.contains(">")) {
				throw new ExpressionException("JSCL cannot parse inequalities, cannot parse \"" + jsclExpressionString + "\"");
			}
			try {
				jsclExpression = jscl.math.Expression.valueOf(jsclExpressionString);
			} catch (jscl.text.ParseException e) {
				throw new ExpressionException("JSCL couldn't parse \"" + jsclExpressionString + "\"");
			}
			Generic expand = jsclExpression.expand();
			Generic simplify = expand.simplify();
			jscl.math.Generic jsclSolution = simplify.factorize();

			Expression solution = new cbit.vcell.parser.Expression(jsclSolution.toString());

			String[] jsclSymbols = solution.getSymbols();
			for (int i = 0; jsclSymbols != null && i < jsclSymbols.length; i++) {
				String restoredSymbol = cbit.vcell.parser.SymbolUtils.getRestoredStringJSCL(jsclSymbols[i]);
				if (!restoredSymbol.equals(jsclSymbols[i])) {
					solution.substituteInPlace(new cbit.vcell.parser.Expression(jsclSymbols[i]), new Expression(restoredSymbol));
				}
			}
			if (logger.isTraceEnabled()) {
				long endTime = System.currentTimeMillis();
				if (endTime - startTime > 100) {
					logger.trace("JSCL expression simplification took " + (endTime - startTime) + " ms (limit was " + maxExecutionTime_ms + " ms) for '" + exp.infix() + "'");
				}
			}
			return solution;

		}finally{
			//
			// clear computation timeout (threadlocal var)
			//
			jscl.math.Expression.timeoutMS.remove();
		}
	}


	/**
 * This method was created by a SmartGuide.
 */
public Expression getBinaryExpression() {
	return new Expression((SimpleNode)rootNode.copyTreeBinary());

}
/**
 * 
 * @param token
 * @return
 * @throws ExpressionException
 * @throws IOException
 * @deprecated use new Expression(tokenizer.readToSemicolon()) instead.
 */
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

public interface FunctionFilter {
	boolean accept(String functionName, FunctionType functionType);
}

public FunctionInvocation[] getFunctionInvocations(FunctionFilter filter) {  // null selects all functions.
	Vector<FunctionInvocation> v = new Vector<FunctionInvocation>();
	rootNode.getFunctionInvocations(v,filter);
	FunctionInvocation[] funcs = new FunctionInvocation[v.size()];
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
			normalizedInfixString = clonedExp.flattenSafe().infix();
		}catch(ExpressionException e){
			logger.warn("failed to flatten '"+infix()+"' for normalizedInfixString: "+e.getMessage(), e);
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
public Expression getSubstitutedExpression(Expression origExp, Expression newExp) {
substituteCount++;////////////////////////////////
	SimpleNode origNode = origExp.rootNode;
	SimpleNode newNode = (SimpleNode)newExp.rootNode.copyTree();
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

public SymbolTableEntry getSymbolBinding(String symbol) {
	return rootNode.getBinding(symbol);
}

public String[] getSymbols() {
	return getSymbols(SimpleNode.LANGUAGE_DEFAULT);
}

public String[] getSymbols(int language) {
	Set<String> symbolSet = new HashSet<String>();
	rootNode.getSymbols(language, symbolSet);
	if (symbolSet.size() == 0) {
		return null;
	}
	return symbolSet.toArray(new String[symbolSet.size()]);	
}

public boolean hasSymbol(String symbolName) {
	String[] symbols = getSymbols();
	for (int i = 0 ; symbols != null && i < symbols.length; i++){
		if (symbolName.equals(symbols[i])) {
			return true;
		}
	}
	return false;
}

public String infix() {
	if (rootNode == null) {
		return null;
	} else {
		return rootNode.infixString(SimpleNode.LANGUAGE_DEFAULT);
	}
}
public String infixBng() {
	if (rootNode == null) {
		return null;
	} else {	// clean useless leading and ending parenthesis if they are paired
		String s = rootNode.infixString(SimpleNode.LANGUAGE_BNGL);
		if(Expression.isParenthesisMatchBothEnds(s)) {
			s = s.substring(1, s.length() - 1); 
		}
		return s;
	}
}

private static boolean isParenthesisMatchBothEnds(String str) {
	if (!str.startsWith("(") || !str.endsWith(")")) {
		return false;	// it's got to begin and end with matched parenthesis
	}
	Stack<Character> stack = new Stack<Character>();
	char c;
	for(int i=0; i < str.length(); i++) {
		c = str.charAt(i);
		if(c == '(') {
			stack.push(c);
		} else if(c == ')') {
			stack.pop();
			if(stack.empty()) {
				if( i == str.length()-1) {
					return true;	// found a match at the end of the string
				} else {
					return false;	// found a match within the string
				}
			}
		}
	}
	return false;
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
 * @deprecated
 */
   public String infix_C()
   {
	  if (rootNode==null){
		 return null;
	  }else{
		 return rootNode.infixString(SimpleNode.LANGUAGE_C);
	  }
   }   
   public String infix_ECLiPSe()
   {
	  if (rootNode==null){
		 return null;
	  }else{
		 return rootNode.infixString(SimpleNode.LANGUAGE_ECLiPSe);
	  }
   }   
   public String infix_VISIT(String visitMeshName)
   {
	   //
	   //This will return a string that potentially has VISIT_RESERVED_... strings
	   //ASTIdNodet.VISIT_RESEVED_X, ASTIdNodet.VISIT_RESEVED_Y, ASTIdNodet.VISIT_RESEVED_Z that have to be
	   //replaced with the syntax for X,Y,Z in visit expressions.  Referencing X,Y,Z coordinates
	   //in VisIt requires a mesh name and takes the form "coord(meshName)[n]" where n is 0->X,1->Y,2->Z.
	   //String should be searched for VISIT_RESERVED_... strings and be replaced with "coord(meshName)[n]" strings.
	   //
	  if (rootNode==null){
		 return null;
	  }else{
		 String afterInfix = rootNode.infixString(SimpleNode.LANGUAGE_VISIT);
		 if(visitMeshName != null){
			 return replaceVisitReservedSymbols(afterInfix,visitMeshName);
		 }
		 return afterInfix;
	  }
   }  
   private static String replaceVisitReservedSymbols(String after_Infix_VISIT,String visitMeshName){
	   StringBuffer visitStr = new StringBuffer(after_Infix_VISIT);
		do{
			boolean bFound = false;
			int index = visitStr.indexOf(ASTIdNode.VISIT_RESERVED_X);
			if(index != -1){
				bFound = true;
				visitStr.replace(index, index+ASTIdNode.VISIT_RESERVED_X.length(), "coord("+visitMeshName+")[0]");
			}
			index = visitStr.indexOf(ASTIdNode.VISIT_RESERVED_Y);
			if(index != -1){
				bFound = true;
				visitStr.replace(index, index+ASTIdNode.VISIT_RESERVED_Y.length(), "coord("+visitMeshName+")[1]");
			}
			index = visitStr.indexOf(ASTIdNode.VISIT_RESERVED_Z);
			if(index != -1){
				bFound = true;
				visitStr.replace(index, index+ASTIdNode.VISIT_RESERVED_Z.length(), "coord("+visitMeshName+")[2]");
			}
			index = visitStr.indexOf(ASTIdNode.VISIT_RESERVED_T);
			if(index != -1){
				bFound = true;
				visitStr.replace(index, index+ASTIdNode.VISIT_RESERVED_T.length(), "time("+visitMeshName+")");
			}
			if(!bFound){
				break;
			}
		}while(true);
		return visitStr.toString();
   }
   public String infix_JSCL()
   {
	  if (rootNode==null){
		 return null;
	  }else{
		 return rootNode.infixString(SimpleNode.LANGUAGE_JSCL);
	  }
   }   

   public String infix_UNITS()
   {
	  if (rootNode==null){
		 return null;
	  }else{
		 return rootNode.infixString(SimpleNode.LANGUAGE_UNITS);
	  }
   }   

   public String infix_Matlab()
   {
	  if (rootNode==null){
		 return null;
	  }else{
		 return rootNode.infixString(SimpleNode.LANGUAGE_MATLAB);
	  }
   }   
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Expression
 * @param expression Expression
 * @exception java.lang.Exception The exception description.
 */
public static Expression invert(Expression expression)  {
	Expression exp = new Expression();
	ASTInvertTermNode invertNode = new ASTInvertTermNode();
	
	Node termNode = expression.rootNode.copyTree();
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

public boolean isIdentifier() {
	return (rootNode instanceof ASTIdNode);
}

public boolean isLiteral() {
	return (rootNode instanceof ASTLiteralNode);
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
	return compareEqual(ZERO);
}
public boolean isOne() {
	return compareEqual(ONE);
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
	Node termNode = expression.rootNode.copyTree();
	laplacianNode.jjtAddChild(termNode);
	exp.rootNode = laplacianNode;
	return exp;
}

public static Expression mult(Expression... expressions) {
	Expression exp = new Expression();
	ASTMultNode multNode = new ASTMultNode();

	for (Expression expression1 : expressions) {
		Node termNode = expression1.rootNode.copyTree();
		multNode.jjtAddChild(termNode);
	}
   	
	exp.rootNode = multNode;
	return exp;
}

public static Expression div(Expression expression1, Expression expression2) {
	Expression exp = new Expression();
	ASTMultNode multNode = new ASTMultNode();

	Node termNode = expression1.rootNode.copyTree();
	multNode.jjtAddChild(termNode);
	
   	
	termNode = expression2.rootNode.copyTree();
	ASTInvertTermNode inverseNode = new ASTInvertTermNode();
	inverseNode.jjtAddChild(termNode);
	multNode.jjtAddChild(inverseNode);
	
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
public static Expression negate(Expression expression) {
	Expression exp = new Expression();
	ASTMinusTermNode minusNode = new ASTMinusTermNode();
	Node termNode = expression.rootNode.copyTree();
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
private void parseExpression(String expString) throws ExpressionException {
parseCount++;

	try {
		String exp = expString.trim();
		if (!exp.endsWith(";")){
			exp = exp + ";";
		}
		ExpressionParser parser;
		parser = new ExpressionParser(new java.io.ByteArrayInputStream(exp.getBytes()));
		rootNode = parser.Expression();

		//
		// get rid of ExpressionNode (worthless for evaluation), artifact of parsing
		//
		if (rootNode instanceof ASTExpression){
			if (rootNode.jjtGetNumChildren() == 1){
				rootNode = (SimpleNode)rootNode.jjtGetChild(0);
				rootNode.jjtSetParent(null);
			}
		}
	} catch (ParseException | TokenMgrError e) {
		String msg = "Parse Error while parsing expression '" + expString + "': " + e.getMessage();
		logger.error(msg, e);
		throw new ParserException("Parse Error while parsing expression '" + expString + "': " + e.getMessage());
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
	return function(FunctionType.POW, expression1, expression2);
}

public static Expression power(Expression expression1, double exponent) {
	return function(FunctionType.POW, expression1, new Expression(exponent));
}

public static Expression log(Expression expression1) throws ExpressionException {
	return function(FunctionType.LOG, expression1);
}

public static Expression max(Expression expression1, Expression expression2) throws ExpressionException {
	return function(FunctionType.MAX, expression1, expression2);
}

public static Expression min(Expression expression1, Expression expression2) throws ExpressionException {
	return function(FunctionType.MIN, expression1, expression2);
}

public static Expression exp(Expression expression1) throws ExpressionException {
	return function(FunctionType.EXP, expression1);
}

public static Expression sqrt(Expression expression1) throws ExpressionException {
	return function(FunctionType.SQRT, expression1);
}

public static Expression function(FunctionType funcType, Expression... expressions) {
	return function(funcType.getName(), expressions);
}

public static Expression or(Expression... expressions) {
	Expression exp = new Expression();
	ASTOrNode orNode = new ASTOrNode();

	for (Expression expression1 : expressions) {
		Node termNode = expression1.rootNode.copyTree();
		orNode.jjtAddChild(termNode);
	}
   	
	exp.rootNode = orNode;
	return exp;
}

public static Expression and(Expression... expressions) {
	Expression exp = new Expression();
	ASTAndNode andNode = new ASTAndNode();

	for (Expression expression1 : expressions) {
		Node termNode = expression1.rootNode.copyTree();
		andNode.jjtAddChild(termNode);
	}
   	
	exp.rootNode = andNode;
	return exp;
}

public static Expression not(Expression expression) {
	Expression exp = new Expression();
	ASTNotNode notNode = new ASTNotNode();
	Node termNode = expression.rootNode.copyTree();
	//
	// get rid of double negative
	//
	if (termNode instanceof ASTNotNode){
		exp.rootNode = (SimpleNode)termNode.jjtGetChild(0);
	}else{	
		notNode.jjtAddChild(termNode);
		exp.rootNode = notNode;
	}	
	return exp;
}

public static Expression relational(String logicalOperation, Expression exp1, Expression exp2) {
	Expression exp = new Expression();
	ASTRelationalNode relNode = new ASTRelationalNode();
	relNode.setOperationFromToken(logicalOperation);

	Node operand1 = exp1.rootNode.copyTree();
	relNode.jjtAddChild(operand1);
	Node operand2 = exp2.rootNode.copyTree();
	relNode.jjtAddChild(operand2);
   	
	exp.rootNode = relNode;
	return exp;
}

public static Expression function(String functionName, Expression... expressions) {
	Expression exp = new Expression();
	ASTFuncNode funcNode = new ASTFuncNode();
	funcNode.setFunctionFromName(functionName);

	for (Expression expression1 : expressions) {
		Node termNode = expression1.rootNode.copyTree();
		funcNode.jjtAddChild(termNode);
	}
   	
	exp.rootNode = funcNode;
	return exp;
}


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
public void substituteInPlace(Expression origExp, Expression newExp) {
substituteCount++;////////////////////////////////
	SimpleNode origNode = origExp.rootNode;
	SimpleNode newNode = (SimpleNode)newExp.rootNode.copyTree();
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
   
   public Vector<Discontinuity> getDiscontinuities() throws ExpressionException {	   
	   Vector<Discontinuity> v = new Vector<Discontinuity>();
	   rootNode.getDiscontinuities(v);	   
	   return v;
   }
   
   public Expression renameBoundSymbols(NameScope nameScope) throws ExpressionBindingException {
	   Expression exp = new Expression(this);
	   exp.normalizedInfixString = null;
	   exp.rootNode.renameBoundSymbols(nameScope);
	   return exp;
   }
   
   public Expression convertToRvachevFunction() 
   {
	   Expression exp = new Expression((SimpleNode)rootNode.convertToRvachevFunction());
	   return exp;
   }

/**
 * @param e
 * @return true if e is not null and not {@link Expression#isZero()}
 */
public static boolean notZero(Expression e) {
	return e != null && ( ! e.isZero() );
}

public void validateUnscopedSymbols() throws ExpressionBindingException {
	String[] symbols = getSymbols();
	if (symbols!=null){
		for (String symbol : symbols){
			if (symbol.contains(".")){
				throw new ExpressionBindingException("symbol "+symbol+" has a name scope separator '.', not allowed in this expression");
			}
		}
	}
}

}


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
import java.util.ArrayList;
import java.util.Set;

import net.sourceforge.interval.ia_math.IAFunctionDomainException;
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.IANarrow;
import net.sourceforge.interval.ia_math.RealInterval;
import cbit.vcell.parser.Expression.FunctionFilter;
import cbit.vcell.parser.SimpleSymbolTable.SimpleSymbolTableFunctionEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry.FunctionArgType;

public class ASTFuncNode extends SimpleNode {
	private static final long serialVersionUID = 6409714707358596459L;
	private String funcName = null;
 	private FunctionType funcType = FunctionType.USERDEFINED;
 	private transient SymbolTableFunctionEntry symbolTableFunctionEntry = null;

 	public static class PredefinedSymbolTableFunctionEntry extends SimpleSymbolTableFunctionEntry {
 		private FunctionType functionType;
		public PredefinedSymbolTableFunctionEntry(FunctionType ft){
			super(ft.getName(), ft.getArgNames(), ft.getArgTypes(), null, null, null);
			functionType = ft;
		}
		public String getDescription() {
			return functionType.getHtmlDescription();
		}
 	}
 	public static final PredefinedSymbolTableFunctionEntry[] predefinedSymbolTableFunctionEntries; 
 	static {
 		ArrayList<PredefinedSymbolTableFunctionEntry> predefinedSymbolTableFunctionEntryList = new ArrayList<PredefinedSymbolTableFunctionEntry>();
 		for (FunctionType ft : FunctionType.values()) {
 			if (ft != FunctionType.USERDEFINED && ft != FunctionType.LOGBASE) {
 				predefinedSymbolTableFunctionEntryList.add(new PredefinedSymbolTableFunctionEntry(ft));
 			}
 		}
 		predefinedSymbolTableFunctionEntries = predefinedSymbolTableFunctionEntryList.toArray(new PredefinedSymbolTableFunctionEntry[0]);
 	}
 	
 	public static enum FunctionType {
 		USERDEFINED("user defined",	 -1, 	null,	"" ),   
		EXP			("exp",				1, 	MathMLTags.EXP					,	"<html>exponential function, <i>e<sup>x</sup></i></html>" ),   
		SQRT		("sqrt",			1, 	null							,	"square root" ),	 
		ABS			("abs",				1, 	MathMLTags.ABS					,	"absolute value" ),	 
		POW			("pow",				2, 	MathMLTags.POWER				,	"<html>power function, <i>x<sup>y</sup></i></html>" ),	 
		LOG			("log",				1, 	MathMLTags.LN					,	"natural logarithmic function" ),
		SIN			("sin",				1, 	MathMLTags.SINE					,	"sine function" ),	 
		COS			("cos",				1, 	MathMLTags.COSINE				,	"cosine function" ),
		TAN			("tan",				1, 	MathMLTags.TANGENT				,	"tagent function" ),
		ASIN		("asin",			1, 	MathMLTags.INV_SINE				,	"inverse since function" ),	 
		ACOS		("acos",			1, 	MathMLTags.INV_COSINE			,	"inverse cosine function" ),
		ATAN		("atan",			1, 	MathMLTags.INV_TANGENT			,	"inverse tangent function" ),	 
		ATAN2		("atan2",			2, 	null							,	"<html>similar to <i>atan(y/x)</i>, except places the angle in the correct quadrant</html>" ),	 
		MAX			("max",				2, 	null							,	"<html>maximum of <i>x</i> and <i>y</i></html>" ),
		MIN			("min",				2, 	null							,	"<html>minimum of <i>x</i> and <i>y</i></html>" ),
		CEIL		("ceil",			1, 	MathMLTags.CEILING				,	"<html>smallest integral value not less than <i>x</i></html>" ),	 
		FLOOR		("floor",			1, 	MathMLTags.FLOOR				,	"<html>largest integral value not greater than <i>x</i></html>" ),	 
		CSC			("csc",				1, 	MathMLTags.COSECANT				,	"cosecant function" ),	 
		COT			("cot",				1, 	MathMLTags.COTANGENT			,	"cotangent function" ),	 
		SEC			("sec",				1, 	MathMLTags.SECANT				,	"secant function" ),	 
		ACSC		("acsc",			1, 	MathMLTags.INV_COSECANT			,	"inverse cosecant function" ),	 
		ACOT		("acot",			1, 	MathMLTags.INV_COTANGENT		,	"inverse cotangent function" ),	 
		ASEC		("asec",			1, 	MathMLTags.INV_SECANT			,	"inverse secant" ),	 
		SINH		("sinh",			1, 	MathMLTags.HYP_SINE				,	"hyperbolic sine function" ),	 
		COSH		("cosh",			1, 	MathMLTags.HYP_COSINE			,	"hyperbolic cosine function" ),	 
		TANH		("tanh",			1, 	MathMLTags.HYP_TANGENT			,	"hyperbolic tangent function" ),	 
		CSCH		("csch",			1, 	MathMLTags.HYP_COSECANT			,	"hyperbolic cosecant function" ),	 
		COTH		("coth",			1, 	MathMLTags.HYP_COTANGENT		,	"hyperbolic cotangent function" ),	 
		SECH		("sech",			1, 	MathMLTags.HYP_SECANT			,	"hyperbolic secant function" ),	 
		ASINH		("asinh",			1, 	MathMLTags.INV_HYP_SINE			,	"inverse hyperbolic sine function" ),	 
		ACOSH		("acosh",			1, 	MathMLTags.INV_HYP_COSINE		,	"inverse hyperbolic cosine function" ),	 
		ATANH		("atanh",			1, 	MathMLTags.INV_HYP_TANGENT		,	"inverse hyperbolic tangent function" ),	 
		ACSCH		("acsch",			1, 	MathMLTags.INV_HYP_COSECANT		,	"inverse hyperbolic cosecant function" ),	 
		ACOTH		("acoth",			1, 	MathMLTags.INV_HYP_COTANGENT	,	"inverse hyperbolic cotangent function" ),	 
		ASECH		("asech",			1, 	MathMLTags.INV_HYP_SECANT		,	"inverse hyperbolic secant function" ),	 
		FACTORIAL	("factorial",		1, 	MathMLTags.FACTORIAL			,	"<html>factorial function, <i>x!</i></html>" ),	 
		LOG_10		("log10",			1, 	MathMLTags.LOG_10				,	"base-10 logarithmic function" ),	 
		LOGBASE		("logbase", 		1, 	MathMLTags.LOGBASE		 		, 	"" );	 
 		
 		private String name, htmlDescription;
 		private String[] argNames;
 		private FunctionArgType[] argTypes;
 		private String mathMLTag;
		private FunctionType(String name, int numArgs, String mathMLTag, String htmlDescription) {
			if (numArgs != 1 && numArgs != 2 && numArgs != -1) {
				throw new RuntimeException("wrong number of numArgs");
			}
			this.name = name;
			if (numArgs == 1) {
				this.argNames = new String[]{"x"};
				this.argTypes = new FunctionArgType[] {FunctionArgType.NUMERIC};
			} else if (numArgs == 2) {
				this.argNames = new String[]{"x", "y"};
				this.argTypes = new FunctionArgType[] {FunctionArgType.NUMERIC, FunctionArgType.NUMERIC};
			}
			this.mathMLTag = mathMLTag;
			this.htmlDescription = htmlDescription;
		}
		public final String getName() {
			return name;
		}
		public final String getHtmlDescription() {
			return htmlDescription;
		}
		public final String[] getArgNames() {
			return argNames;
		}
		public final FunctionArgType[] getArgTypes() {
			return argTypes;
		}
		public final String getMathMLTag() {
			return mathMLTag;
		}
		public static FunctionType fromFunctionName(String functionName){
			for (FunctionType ft : values()){
				if (ft.name.equals(functionName)){
					return ft;
				}
			}
			return null;
		}
 	}
	
  ASTFuncNode() {
    super(ExpressionParserTreeConstants.JJTFUNCNODE);
  }


ASTFuncNode(int id) {
	super(id);
if (id != ExpressionParserTreeConstants.JJTFUNCNODE){ System.out.println("ASTFuncNode(), id = "+id); }
}

  /** Bind method, identifiers bind themselves to ValueObjects */
@Override
public void bind(SymbolTable symbolTable) throws ExpressionBindingException {
	if (getFunction() == FunctionType.USERDEFINED){
		if (symbolTable == null){
			symbolTableFunctionEntry = null;
			return;
		}
		String formalDefinition = getFormalDefinition();
		SymbolTableEntry symbolTableEntry = symbolTable.getEntry(formalDefinition);
		if (symbolTableEntry instanceof SymbolTableFunctionEntry) {
			FunctionArgType[] argTypes = new FunctionArgType[jjtGetNumChildren()];
			for (int i = 0; i < argTypes.length; i++) {
				if (jjtGetChild(i) instanceof ASTLiteralNode){
					String argName = ((SymbolTableFunctionEntry) symbolTableEntry).getArgNames()[i];
					Set<String> allowableArguments = ((SymbolTableFunctionEntry) symbolTableEntry).getAllowableLiteralValues(argName);
					if (allowableArguments == null || allowableArguments.isEmpty()) {
						continue;
					}
					String value = ((ASTLiteralNode)jjtGetChild(i)).infixString(LANGUAGE_DEFAULT);
					if (!allowableArguments.contains(value)) {
						throw new ExpressionBindingException(value + " is not a valid arugment for argument " + argName + " in function " 
								+ ((SymbolTableFunctionEntry) symbolTableEntry).getFunctionName());
					}
				}
			}
		}
		if (symbolTableEntry==null){
			throw new ExpressionBindingException("function definition '" + formalDefinition + "' is not found. " 
					+ "Check that you have provided the correct name and argument types (e.g. vcRegionVolume('cytosol')).");
		}else{
			if (symbolTableEntry instanceof SymbolTableFunctionEntry){
				symbolTableFunctionEntry = (SymbolTableFunctionEntry)symbolTableEntry;
			}else{
				throw new ExpressionBindingException("function definition '" + formalDefinition + "' not bound to a function");
			}
		}
	}
	super.bind(symbolTable);
}    


  /**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public Node copyTree() {
	ASTFuncNode node = new ASTFuncNode();
	node.funcType = funcType;
	node.funcName = funcName;
	node.symbolTableFunctionEntry = symbolTableFunctionEntry;
	for (int i=0;i<jjtGetNumChildren();i++){
		node.jjtAddChild(jjtGetChild(i).copyTree());
	}
	return node;	
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public Node copyTreeBinary() {
	ASTFuncNode node = new ASTFuncNode();
	node.funcType = funcType;
	node.funcName = funcName;
	node.symbolTableFunctionEntry = symbolTableFunctionEntry;
	for (int i=0;i<jjtGetNumChildren();i++){
		node.jjtAddChild(jjtGetChild(i).copyTreeBinary());
	}
	return node;	
}

public static String getFormalDefinition(String funcName, FunctionArgType[] argTypes){
	StringBuffer buffer = new StringBuffer(funcName);
	buffer.append("(");
	for (int i=0;i<argTypes.length;i++){
		if (i>0){
			buffer.append(",");
		}
		buffer.append(argTypes[i].name());
	}
	buffer.append(")");
	return buffer.toString();
}

public String getFormalDefinition(){
	FunctionArgType[] argTypes = new FunctionArgType[jjtGetNumChildren()];
	for (int i = 0; i < argTypes.length; i++) {
		if (jjtGetChild(i) instanceof ASTLiteralNode){
			argTypes[i] = FunctionArgType.LITERAL;
		}else{
			argTypes[i] = FunctionArgType.NUMERIC;
		}
	}
	return getFormalDefinition(funcName, argTypes);
}

private SimpleNode getSubstitutedFunction() throws ExpressionException {
	if (symbolTableFunctionEntry==null){
		throw new ExpressionException("function "+getFormalDefinition()+" is unbound");
	}
	if (symbolTableFunctionEntry.getExpression()==null){
		return null;
	}
	Expression substitutedExp = new Expression(symbolTableFunctionEntry.getExpression());
	String[] argumentNames = symbolTableFunctionEntry.getArgNames();
	for (int i=0; i<symbolTableFunctionEntry.getNumArguments(); i++){
		ASTIdNode formalArg = new ASTIdNode();
		formalArg.name = argumentNames[i];
		if (symbolTableFunctionEntry.getArgTypes()[i]==FunctionArgType.LITERAL){
			throw new ExpressionException("cannot substitute, function "+symbolTableFunctionEntry.getName()+" has formal arguments of type "+FunctionArgType.LITERAL.name());
		}
		if (jjtGetChild(i) instanceof ASTLiteralNode){
			throw new ExpressionException("cannot substitute, invocation of function "+symbolTableFunctionEntry.getName()+" has arguments of type "+FunctionArgType.LITERAL.name());
		}
		substitutedExp.getRootNode().substitute(formalArg, jjtGetChild(i).copyTree());
	}
	return substitutedExp.getRootNode();
}

/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Expression
 * @param independentVariable java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public Node differentiate(String independentVariable) throws ExpressionException {
	switch (funcType){
	case USERDEFINED: {
		SimpleNode substitutedFunction = getSubstitutedFunction();
		if (substitutedFunction!=null){
			return substitutedFunction.differentiate(independentVariable);
		}else if (symbolTableFunctionEntry instanceof SymbolTableFunctionEntry.Differentiable){
			return ((SymbolTableFunctionEntry.Differentiable)symbolTableFunctionEntry).differentiate(getArguments(),independentVariable).getRootNode();
		}else{
			throw new ExpressionException("user defined function "+getName()+" is not differentiable");
		}
	}
	case EXP: {
		// 
		// case of D(exp(u)) = exp(u) D(u)
		//
		if (jjtGetNumChildren()!=1) throw new Error("exp() expects 1 argument");

		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(copyTree());
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		
		return multNode;
	}
	case SQRT: {
		// 
		// case of D(sqrt(u)) = 0.5 * D(u) / sqrt(u)
		//
		if (jjtGetNumChildren()!=1) throw new Error("sqrt() expects 1 argument");
	
		// 
		// form  0.5 * D(u) / sqrt(u)
		//
		ASTMultNode multNode = new ASTMultNode();
		ASTInvertTermNode invertNode = new ASTInvertTermNode();
		invertNode.jjtAddChild(copyTree());
		multNode.jjtAddChild(new ASTFloatNode(0.5));
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		multNode.jjtAddChild(invertNode);
		
		return multNode;
	}
	case ABS: {
		// 
		// case of D(abs(u)) = (u>=0.0)*D(u) + (u<0.0)*D(-u))
		//                   = (u>=0.0)*D(u) - (u<0.0)*D(u))
		//                   = (1.0 - 2.0*(u<0.0))*D(u)
		//
		if (jjtGetNumChildren()!=1) throw new Error("abs() expects 1 argument");
		
		//
		// form -2.0*(u < 0.0)
		//
		ASTRelationalNode ltNode = new ASTRelationalNode();
		ltNode.setOperationFromToken("<");
		ltNode.jjtAddChild(jjtGetChild(0).copyTree());  // u
		ltNode.jjtAddChild(new ASTFloatNode(0.0));
		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(new ASTFloatNode(-2.0));
		multNode.jjtAddChild(ltNode);

		// 
		// form  1.0 - 2.0*(u<0.0)
		//
		ASTAddNode addNode = new ASTAddNode();
		addNode.jjtAddChild(new ASTFloatNode(1.0));
		addNode.jjtAddChild(multNode);
		
		//
		// form (1.0 - 2.0*(u<0.0))*D(u)
		//
		ASTMultNode multNode2 = new ASTMultNode();
		multNode2.jjtAddChild(addNode);
		multNode2.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));

		return multNode2;
	}
	case POW: {
		// 
		// case of D(pow(u,v)) = v pow(u,v-1) D(u)  +  pow(u,v) log(u) D(v)
		//
		if (jjtGetNumChildren()!=2) throw new Error("pow() expects 2 arguments");
	
		// 
		// form  v pow(u,v-1) D(u)
		//
		ASTMultNode multNode1 = new ASTMultNode();
		ASTFuncNode powNode = new ASTFuncNode();
		powNode.setFunctionType(FunctionType.POW);
		ASTAddNode addNode = new ASTAddNode();
		addNode.jjtAddChild(jjtGetChild(1).copyTree());
		addNode.jjtAddChild(new ASTFloatNode(-1.0));
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(addNode);
		multNode1.jjtAddChild(jjtGetChild(1).copyTree());
		multNode1.jjtAddChild(powNode);
		multNode1.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		
		// 
		// form  pow(u,v) log(u) D(v)
		//
		ASTMultNode multNode2 = new ASTMultNode();
		ASTFuncNode logNode = new ASTFuncNode();
		logNode.setFunctionType(FunctionType.LOG);
		logNode.jjtAddChild(jjtGetChild(0).copyTree());
		multNode2.jjtAddChild(copyTree());
		multNode2.jjtAddChild(logNode);
		multNode2.jjtAddChild(jjtGetChild(1).differentiate(independentVariable));
		
		ASTAddNode fullAddNode = new ASTAddNode();
		fullAddNode.jjtAddChild(multNode1);
		fullAddNode.jjtAddChild(multNode2);
		
		return fullAddNode;
	}
	case LOG: {
		//
		// case of D(log(a)) = D(a) / a
		//
		if (jjtGetNumChildren()!=1) throw new Error("log() expects 1 argument");
		ASTMultNode multNode = new ASTMultNode();
	
		//
		// form   1/a 
		//
		ASTInvertTermNode invertNode = new ASTInvertTermNode();
		invertNode.jjtAddChild(jjtGetChild(0).copyTree());	
		
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		multNode.jjtAddChild(invertNode);
		
		return multNode;
	}
	case SIN: {
		if (jjtGetNumChildren()!=1) throw new Error("sin() expects 1 argument");
		//
		// case of D(sin(a)) = D(a) * cos(a)
		//
		ASTMultNode multNode = new ASTMultNode();
	
		//
		// form   cos(a) 
		//
		ASTFuncNode cosNode = new ASTFuncNode();
		cosNode.setFunctionType(FunctionType.COS);
		cosNode.jjtAddChild(jjtGetChild(0).copyTree());	
		
		multNode.jjtAddChild(cosNode);
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		
		return multNode;
	}
	case COS: {
		if (jjtGetNumChildren()!=1) throw new Error("cos() expects 1 argument");
		//
		// case of D(cos(a)) = - (D(a) * sin(a))
		//
		ASTMultNode multNode = new ASTMultNode();
	
		//
		// form   sin(a) 
		//
		ASTFuncNode sinNode = new ASTFuncNode();
		sinNode.setFunctionType(FunctionType.SIN);
		sinNode.jjtAddChild(jjtGetChild(0).copyTree());	
		
		multNode.jjtAddChild(sinNode);
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		
		ASTMinusTermNode minusNode = new ASTMinusTermNode();
		minusNode.jjtAddChild(multNode);
		
		return minusNode;
	}
	case TAN: {
		//
		// case of D(tan(u)) = D(u)/pow(cos(u),2)
		//
		if (jjtGetNumChildren()!=1) throw new Error("tan() expects 1 argument");

		//
		// form   cos(a) 
		//
		ASTFuncNode cosNode = new ASTFuncNode();
		cosNode.setFunctionType(FunctionType.COS);
		cosNode.jjtAddChild(jjtGetChild(0).copyTree());	
		
		//
		// form   pow(cos(a),2) 
		//
		ASTFuncNode powNode = new ASTFuncNode();
		powNode.setFunctionType(FunctionType.POW);
		powNode.jjtAddChild(cosNode);
		powNode.jjtAddChild(new ASTFloatNode(2.0));
			
		//
		// form   D(u) / pow(cos(a),2) 
		//
		ASTMultNode multNode = new ASTMultNode();
		ASTInvertTermNode invertNode = new ASTInvertTermNode();
		
		invertNode.jjtAddChild(powNode);
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		multNode.jjtAddChild(invertNode);
		
		return multNode;
	}
	case ASIN: {
		// 
		// case of D(asin(u)) = D(u)/(sqrt(1.0 - pow(u,2)))
		//
		if (jjtGetNumChildren()!=1) throw new Error("asin() expects 1 argument");

		//
		// form sqrt(1.0 - pow(u,2))
		//
		ASTFuncNode powNode = new ASTFuncNode();
		powNode.setFunctionType(FunctionType.POW);
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));
		ASTMinusTermNode minusNode = new ASTMinusTermNode();
		minusNode.jjtAddChild(powNode);
		ASTAddNode addNode = new ASTAddNode();
		addNode.jjtAddChild(new ASTFloatNode(1.0));
		addNode.jjtAddChild(minusNode);
		ASTFuncNode sqrtNode = new ASTFuncNode();
		sqrtNode.setFunctionType(FunctionType.SQRT);
		sqrtNode.jjtAddChild(addNode);

		ASTInvertTermNode invertNode = new ASTInvertTermNode();
		invertNode.jjtAddChild(sqrtNode);
		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		multNode.jjtAddChild(invertNode);
		
		return multNode;
	}
	case ACOS: {
		// 
		// case of D(acos(u)) = -1.0*D(u)/(sqrt(1.0 - u^2))
		//
		if (jjtGetNumChildren()!=1) throw new Error("acos() expects 1 argument");

		//
		// form sqrt(1.0 - pow(u,2))
		//
		ASTFuncNode powNode = new ASTFuncNode();
		powNode.setFunctionType(FunctionType.POW);
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));
		ASTMinusTermNode minusNode = new ASTMinusTermNode();
		minusNode.jjtAddChild(powNode);
		ASTAddNode addNode = new ASTAddNode();
		addNode.jjtAddChild(new ASTFloatNode(1.0));
		addNode.jjtAddChild(minusNode);
		ASTFuncNode sqrtNode = new ASTFuncNode();
		sqrtNode.setFunctionType(FunctionType.SQRT);
		sqrtNode.jjtAddChild(addNode);

		ASTInvertTermNode invertNode = new ASTInvertTermNode();
		invertNode.jjtAddChild(sqrtNode);
		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(new ASTFloatNode(-1.0));
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		multNode.jjtAddChild(invertNode);
		
		return multNode;
	}
	case ATAN: {
		// 
		// case of D(atan(u)) = D(u)/(1.0 + pow(u,2))
		//
		if (jjtGetNumChildren()!=1) throw new Error("atan() expects 1 argument");

		//
		// form 1.0 + u^2
		//
		ASTFuncNode powNode = new ASTFuncNode();
		powNode.setFunctionType(FunctionType.POW);
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));
		ASTAddNode addNode = new ASTAddNode();
		addNode.jjtAddChild(new ASTFloatNode(1.0));
		addNode.jjtAddChild(powNode);

		ASTInvertTermNode invertNode = new ASTInvertTermNode();
		invertNode.jjtAddChild(addNode);
		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		multNode.jjtAddChild(invertNode);
		
		return multNode;
	}
	case ATAN2: {
		// 
		// case of D(atan2(u/v)) = D(u/v)/(1.0 + pow((u/v),2))
		//
		if (jjtGetNumChildren()!=2) throw new Error("atan2() expects 2 arguments");

		//
		// form (u/v)
		//
		ASTMultNode multUV = new ASTMultNode();
		ASTInvertTermNode invertV = new ASTInvertTermNode();
		invertV.jjtAddChild(jjtGetChild(1).copyTree());
		multUV.jjtAddChild(jjtGetChild(0).copyTree());
		multUV.jjtAddChild(invertV);
		
		//
		// form 1.0 + pow((u/v),2)
		//
		ASTFuncNode powNode = new ASTFuncNode();
		powNode.setFunctionType(FunctionType.POW);
		powNode.jjtAddChild(multUV.copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));
		ASTAddNode addNode = new ASTAddNode();
		addNode.jjtAddChild(new ASTFloatNode(1.0));
		addNode.jjtAddChild(powNode);

		ASTInvertTermNode invertNode = new ASTInvertTermNode();
		invertNode.jjtAddChild(addNode);
		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(multUV.differentiate(independentVariable));
		multNode.jjtAddChild(invertNode);
		
		return multNode;
	}
	case MAX: {
		// 
		// case of D(max(u,v)) = (u>v)*D(u) + (v>=u)*D(v)
		//
		if (jjtGetNumChildren()!=2) throw new Error("max() expects 2 arguments");
		
		//
		// form (u>v)*D(u)
		//
		ASTRelationalNode gtNode = new ASTRelationalNode();
		gtNode.setOperationFromToken(">");
		gtNode.jjtAddChild(jjtGetChild(0).copyTree());  // u
		gtNode.jjtAddChild(jjtGetChild(1).copyTree());  // v
		ASTMultNode multNode1 = new ASTMultNode();
		multNode1.jjtAddChild(gtNode);
		multNode1.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));  // D(u)

		//
		// form (v>=u)*D(v)
		//
		ASTRelationalNode geNode = new ASTRelationalNode();
		geNode.setOperationFromToken(">=");
		geNode.jjtAddChild(jjtGetChild(1).copyTree());  // v
		geNode.jjtAddChild(jjtGetChild(0).copyTree());  // u
		ASTMultNode multNode2 = new ASTMultNode();
		multNode2.jjtAddChild(geNode);
		multNode2.jjtAddChild(jjtGetChild(1).differentiate(independentVariable));  // D(v)
		
		ASTAddNode addNode = new ASTAddNode();
		addNode.jjtAddChild(multNode1);
		addNode.jjtAddChild(multNode2);

		return addNode;
	}
	case MIN: {
		// 
		// case of D(min(u,v)) = (u<v)*D(u) + (v<=u)*D(v)
		//
		if (jjtGetNumChildren()!=2) throw new Error("min() expects 2 arguments");
		
		//
		// form (u<v)*D(u)
		//
		ASTRelationalNode gtNode = new ASTRelationalNode();
		gtNode.setOperationFromToken("<");
		gtNode.jjtAddChild(jjtGetChild(0).copyTree());  // u
		gtNode.jjtAddChild(jjtGetChild(1).copyTree());  // v
		ASTMultNode multNode1 = new ASTMultNode();
		multNode1.jjtAddChild(gtNode);
		multNode1.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));  // D(u)

		//
		// form (v<=u)*D(v)
		//
		ASTRelationalNode geNode = new ASTRelationalNode();
		geNode.setOperationFromToken("<=");
		geNode.jjtAddChild(jjtGetChild(1).copyTree());  // v
		geNode.jjtAddChild(jjtGetChild(0).copyTree());  // u
		ASTMultNode multNode2 = new ASTMultNode();
		multNode2.jjtAddChild(geNode);
		multNode2.jjtAddChild(jjtGetChild(1).differentiate(independentVariable));  // D(v)
		
		ASTAddNode addNode = new ASTAddNode();
		addNode.jjtAddChild(multNode1);
		addNode.jjtAddChild(multNode2);

		return addNode;
	}
	case CEIL: {
		// 
		// case of D(ceil(u)) = ??
		//
		if (jjtGetNumChildren()!=1) throw new Error("ceil() expects 1 argument");
		ASTFloatNode floatNode = new ASTFloatNode(0.0);
		return floatNode;
	}
	case FLOOR: {
		// 
		// case of D(floor(u)) = ??
		//
		if (jjtGetNumChildren()!=1) throw new Error("floor() expects 1 argument");
		ASTFloatNode floatNode = new ASTFloatNode(0.0);
		return floatNode;
	}
	case CSC: {
		if (jjtGetNumChildren()!=1) throw new Error("csc() expects 1 argument");
		//
		// case of D(csc(a)) = -D(a) * csc(a)*cot(a)
		//
		ASTMultNode multNode = new ASTMultNode();
	
		// form   csc(a) 
		ASTFuncNode cscNode = new ASTFuncNode();
		cscNode.setFunctionType(FunctionType.CSC);
		cscNode.jjtAddChild(jjtGetChild(0).copyTree());
		
		// form   cot(a) 
		ASTFuncNode cotNode = new ASTFuncNode();
		cotNode.setFunctionType(FunctionType.COT);
		cotNode.jjtAddChild(jjtGetChild(0).copyTree());	
			
		multNode.jjtAddChild(cscNode);
		multNode.jjtAddChild(cotNode);
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));

		ASTMinusTermNode minusNode = new ASTMinusTermNode();
		minusNode.jjtAddChild(multNode);
		
		return minusNode;
	}
	case COT: {
		if (jjtGetNumChildren()!=1) throw new Error("cot() expects 1 argument");
		//
		// case of D(cot(a)) = - (D(a) * pow(csc(a), 2))
		//
		ASTMultNode multNode = new ASTMultNode();
	
		// form   csc(a) 
		ASTFuncNode cscNode = new ASTFuncNode();
		cscNode.setFunctionType(FunctionType.CSC);
		cscNode.jjtAddChild(jjtGetChild(0).copyTree());	
		
		// form   pow(csc(a),2) 
		ASTFuncNode powNode = new ASTFuncNode();
		powNode.setFunctionType(FunctionType.POW);
		powNode.jjtAddChild(cscNode);
		powNode.jjtAddChild(new ASTFloatNode(2.0));

		multNode.jjtAddChild(powNode);
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		
		ASTMinusTermNode minusNode = new ASTMinusTermNode();
		minusNode.jjtAddChild(multNode);
		
		return minusNode;
	}
	case SEC: {
		//
		// case of D(sec(u)) = D(u)*sec(a)*tan(a)
		//
		if (jjtGetNumChildren()!=1) throw new Error("sec() expects 1 argument");

		// form   sec(a) 
		ASTFuncNode secNode = new ASTFuncNode();
		secNode.setFunctionType(FunctionType.SEC);
		secNode.jjtAddChild(jjtGetChild(0).copyTree());	
		
		// form   tan(a) 
		ASTFuncNode tanNode = new ASTFuncNode();
		tanNode.setFunctionType(FunctionType.TAN);
		tanNode.jjtAddChild(jjtGetChild(0).copyTree());	
			
		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(secNode);
		multNode.jjtAddChild(tanNode);
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		
		return multNode;
	}
	case ACSC: {
		// 
		// case of D(acsc(u)) = -1.0*D(u)/(u*sqrt(pow(u,2) - 1))
		//
		if (jjtGetNumChildren()!=1) throw new Error("acsc() expects 1 argument");

		// form sqrt(pow(u,2) - 1.0)
		ASTFuncNode powNode = new ASTFuncNode();
		powNode.setFunctionType(FunctionType.POW);
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));

		ASTAddNode addNode = new ASTAddNode();
		addNode.jjtAddChild(powNode);
		addNode.jjtAddChild(new ASTFloatNode(-1.0));
		
		ASTFuncNode sqrtNode = new ASTFuncNode();
		sqrtNode.setFunctionType(FunctionType.SQRT);
		sqrtNode.jjtAddChild(addNode);

		//  form 1 / (u*sqrt(pow(u,2) - 1))
		ASTMultNode multNode_1 = new ASTMultNode();
		multNode_1.jjtAddChild(jjtGetChild(0).copyTree());
		multNode_1.jjtAddChild(sqrtNode);

		ASTInvertTermNode invertNode = new ASTInvertTermNode();
		invertNode.jjtAddChild(multNode_1);

		// final ans
		ASTMultNode multNode_2 = new ASTMultNode();
		multNode_2.jjtAddChild(new ASTFloatNode(-1.0));
		multNode_2.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		multNode_2.jjtAddChild(invertNode);
		
		return multNode_2;
	}
	case ACOT: {
		// 
		// case of D(acot(u)) = -1.0*D(u)/(1.0 + u^2)
		//
		if (jjtGetNumChildren()!=1) throw new Error("acot() expects 1 argument");

		// form (1.0 + pow(u,2))
		ASTFuncNode powNode = new ASTFuncNode();
		powNode.setFunctionType(FunctionType.POW);
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));
		
		ASTAddNode addNode = new ASTAddNode();
		addNode.jjtAddChild(new ASTFloatNode(1.0));
		addNode.jjtAddChild(powNode);
		
		ASTInvertTermNode invertNode = new ASTInvertTermNode();
		invertNode.jjtAddChild(addNode);
		
		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(new ASTFloatNode(-1.0));
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		multNode.jjtAddChild(invertNode);
		
		return multNode;
	}
	case ASEC: {
		// 
		// case of D(asec(u)) = D(u)/(u*(sqrt(pow(u,2) - 1.0))
		//
		if (jjtGetNumChildren()!=1) throw new Error("asec() expects 1 argument");

		// form sqrt(pow(u,2) - 1.0)
		ASTFuncNode powNode = new ASTFuncNode();
		powNode.setFunctionType(FunctionType.POW);
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));

		ASTAddNode addNode = new ASTAddNode();
		addNode.jjtAddChild(powNode);
		addNode.jjtAddChild(new ASTFloatNode(-1.0));
		
		ASTFuncNode sqrtNode = new ASTFuncNode();
		sqrtNode.setFunctionType(FunctionType.SQRT);
		sqrtNode.jjtAddChild(addNode);

		//  form 1 / (u*sqrt(pow(u,2) - 1))
		ASTMultNode multNode_1 = new ASTMultNode();
		multNode_1.jjtAddChild(jjtGetChild(0).copyTree());
		multNode_1.jjtAddChild(sqrtNode);

		ASTInvertTermNode invertNode = new ASTInvertTermNode();
		invertNode.jjtAddChild(multNode_1);

		// final ans
		ASTMultNode multNode_2 = new ASTMultNode();
		multNode_2.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		multNode_2.jjtAddChild(invertNode);
		
		return multNode_2;
	}
	case SINH: {
		if (jjtGetNumChildren()!=1) throw new Error("sinh() expects 1 argument");
		//
		// case of D(sinh(a)) = D(a) * cosh(a)
		//

		// form   cosh(a) 
		ASTFuncNode coshNode = new ASTFuncNode();
		coshNode.setFunctionType(FunctionType.COSH);
		coshNode.jjtAddChild(jjtGetChild(0).copyTree());	
		
		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(coshNode);
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		
		return multNode;
	}
	case COSH: {
		if (jjtGetNumChildren()!=1) throw new Error("cosh() expects 1 argument");
		//
		// case of D(cosh(a)) =  (D(a) * sinh(a))
		//
	
		// form   sinh(a) 
		ASTFuncNode sinhNode = new ASTFuncNode();
		sinhNode.setFunctionType(FunctionType.SINH);
		sinhNode.jjtAddChild(jjtGetChild(0).copyTree());	
		
		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(sinhNode);
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));

		return multNode;
	}
	case TANH: {
		//
		// case of D(tanh(u)) = D(u)*pow(sech(u),2)
		//
		if (jjtGetNumChildren()!=1) throw new Error("tanh() expects 1 argument");

		// form   sech(a) 
		ASTFuncNode sechNode = new ASTFuncNode();
		sechNode.setFunctionType(FunctionType.SECH);
		sechNode.jjtAddChild(jjtGetChild(0).copyTree());	
		
		// form   pow(sech(a),2) 
		ASTFuncNode powNode = new ASTFuncNode();
		powNode.setFunctionType(FunctionType.POW);
		powNode.jjtAddChild(sechNode);
		powNode.jjtAddChild(new ASTFloatNode(2.0));
			
		// form   D(u) * pow(sech(a),2) 
		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		multNode.jjtAddChild(powNode);
		
		return multNode;
	}
	case CSCH: {
		if (jjtGetNumChildren()!=1) throw new Error("csch() expects 1 argument");
		//
		// case of D(csch(a)) = -1.0 * D(a) * csch(a) * coth(a)
		//
	
		// form   csch(a) 
		ASTFuncNode cschNode = new ASTFuncNode();
		cschNode.setFunctionType(FunctionType.COSH);
		cschNode.jjtAddChild(jjtGetChild(0).copyTree());	

		// form   coth(a) 
		ASTFuncNode cothNode = new ASTFuncNode();
		cothNode.setFunctionType(FunctionType.COTH);
		cothNode.jjtAddChild(jjtGetChild(0).copyTree());	

		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(new ASTFloatNode(-1.0));
		multNode.jjtAddChild(cschNode);
		multNode.jjtAddChild(cothNode);
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		
		return multNode;
	}
	case COTH: {
		if (jjtGetNumChildren()!=1) throw new Error("coth() expects 1 argument");
		//
		// case of D(coth(a)) = - (D(a) * pow(csch(a), 2))
		//
	
		// form   csch(a) 
		ASTFuncNode cschNode = new ASTFuncNode();
		cschNode.setFunctionType(FunctionType.CSCH);
		cschNode.jjtAddChild(jjtGetChild(0).copyTree());	
		
		// form   pow(csch(a),2) 
		ASTFuncNode powNode = new ASTFuncNode();
		powNode.setFunctionType(FunctionType.POW);
		powNode.jjtAddChild(cschNode);
		powNode.jjtAddChild(new ASTFloatNode(2.0));
			
		// form   - D(u) * pow(csch(a),2) 
		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		multNode.jjtAddChild(powNode);
		ASTMinusTermNode minusNode = new ASTMinusTermNode();
		minusNode.jjtAddChild(multNode);
		
		return minusNode;
	}
	case SECH: {
		//
		// case of D(sech(u)) = - (D(u) * sech(u) * tanh(u))
		//
		if (jjtGetNumChildren()!=1) throw new Error("sech() expects 1 argument");

		// form   sech(u) 
		ASTFuncNode sechNode = new ASTFuncNode();
		sechNode.setFunctionType(FunctionType.SECH);
		sechNode.jjtAddChild(jjtGetChild(0).copyTree());	
		
		// form   tanh(u) 
		ASTFuncNode tanhNode = new ASTFuncNode();
		tanhNode.setFunctionType(FunctionType.TANH);
		tanhNode.jjtAddChild(jjtGetChild(0).copyTree());	
			
		// form   - D(u) * sech(u) * tanh(u)
		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(new ASTFloatNode(-1.0));
		multNode.jjtAddChild(sechNode);
		multNode.jjtAddChild(tanhNode);
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		
		return multNode;
	}
	case ASINH: {
		// 
		// case of D(asinh(u)) = D(u)/(sqrt(1.0 + pow(u,2)))
		//
		if (jjtGetNumChildren()!=1) throw new Error("asinh() expects 1 argument");

		// form sqrt(1.0 + pow(u,2))
		ASTFuncNode powNode = new ASTFuncNode();
		powNode.setFunctionType(FunctionType.POW);
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));
		
		ASTAddNode addNode = new ASTAddNode();
		addNode.jjtAddChild(new ASTFloatNode(1.0));
		addNode.jjtAddChild(powNode);
		
		ASTFuncNode sqrtNode = new ASTFuncNode();
		sqrtNode.setFunctionType(FunctionType.SQRT);
		sqrtNode.jjtAddChild(addNode);

		ASTInvertTermNode invertNode = new ASTInvertTermNode();
		invertNode.jjtAddChild(sqrtNode);
		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		multNode.jjtAddChild(invertNode);
		
		return multNode;
	}
	case ACOSH: {
		// 
		// case of D(acosh(u)) = D(u)/(sqrt(u^2 - 1.0))
		//
		if (jjtGetNumChildren()!=1) throw new Error("acosh() expects 1 argument");

		// form sqrt(pow(u,2) - 1.0)
		ASTFuncNode powNode = new ASTFuncNode();	// u^2
		powNode.setFunctionType(FunctionType.POW);
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));

		ASTAddNode addNode = new ASTAddNode(); 		// u^2 - 1
		addNode.jjtAddChild(powNode);
		addNode.jjtAddChild(new ASTFloatNode(-1.0));
		ASTFuncNode sqrtNode = new ASTFuncNode(); 	// sqrt(u^2 - 1)
		sqrtNode.setFunctionType(FunctionType.SQRT);
		sqrtNode.jjtAddChild(addNode);
		ASTInvertTermNode invertNode = new ASTInvertTermNode();	// 1/(sqrt(u^2 - 1))
		invertNode.jjtAddChild(sqrtNode);
		
		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		multNode.jjtAddChild(invertNode);
		
		return multNode;
	}
	case ATANH: {
		// 
		// case of D(atanh(u)) = D(u)/(1.0 - pow(u,2))
		//
		if (jjtGetNumChildren()!=1) throw new Error("atanh() expects 1 argument");

		// form 1.0 - u^2
		ASTFuncNode powNode = new ASTFuncNode();			// u^2
		powNode.setFunctionType(FunctionType.POW);
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));
		ASTMinusTermNode minusNode = new ASTMinusTermNode();// -u^2
		minusNode.jjtAddChild(powNode);
		ASTAddNode addNode = new ASTAddNode();				// (1-u^2)
		addNode.jjtAddChild(new ASTFloatNode(1.0));
		addNode.jjtAddChild(minusNode);

		ASTInvertTermNode invertNode = new ASTInvertTermNode();
		invertNode.jjtAddChild(addNode);
		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		multNode.jjtAddChild(invertNode);
		
		return multNode;
	}
	case ACSCH: {
		// 
		// case of D(acsch(u)) = - (D(u)/(|u| * sqrt(1.0 + pow(u,2))))
		//
		if (jjtGetNumChildren()!=1) throw new Error("acsch() expects 1 argument");

		// form sqrt(1.0 + pow(u,2))
		ASTFuncNode powNode = new ASTFuncNode();			// u^2
		powNode.setFunctionType(FunctionType.POW);
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));				
		ASTAddNode addNode = new ASTAddNode();				// 1+u^2
		addNode.jjtAddChild(new ASTFloatNode(1.0));
		addNode.jjtAddChild(powNode);
		ASTFuncNode sqrtNode = new ASTFuncNode();			// sqrt(1+u^2)
		sqrtNode.setFunctionType(FunctionType.SQRT);
		sqrtNode.jjtAddChild(addNode);

		// form 1 / (|u| * sqrt(1.0 + pow(u,2)))
		ASTFuncNode absNode = new ASTFuncNode();			//  |u|
		absNode.setFunctionType(FunctionType.ABS);
		absNode.jjtAddChild(jjtGetChild(0).copyTree());
		ASTMultNode multNode = new ASTMultNode();			// |u| * sqrt(1+u^2)
		multNode.jjtAddChild(absNode);
		multNode.jjtAddChild(sqrtNode);
		ASTInvertTermNode invertNode = new ASTInvertTermNode(); // 1 / (|u| * sqrt(1+u^2))
		invertNode.jjtAddChild(multNode);

		ASTMultNode multNode_1 = new ASTMultNode();
		multNode_1.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		multNode_1.jjtAddChild(invertNode);
		
		return multNode_1;
	}
	case ACOTH: {
		// 
		// case of D(acoth(u)) = D(u)/(1.0 - u^2)
		//
		if (jjtGetNumChildren()!=1) throw new Error("acoth() expects 1 argument");

		// form 1.0 - u^2
		ASTFuncNode powNode = new ASTFuncNode();			// u^2
		powNode.setFunctionType(FunctionType.POW);
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));
		ASTMinusTermNode minusNode = new ASTMinusTermNode();// -u^2
		minusNode.jjtAddChild(powNode);
		ASTAddNode addNode = new ASTAddNode();				// (1-u^2)
		addNode.jjtAddChild(new ASTFloatNode(1.0));
		addNode.jjtAddChild(minusNode);

		ASTInvertTermNode invertNode = new ASTInvertTermNode();
		invertNode.jjtAddChild(addNode);
		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		multNode.jjtAddChild(invertNode);
		
		return multNode;
	}
	case ASECH: {
		// 
		// case of D(asech(u)) = - (D(u)/(u * sqrt(1.0 - pow(u,2))))
		//
		if (jjtGetNumChildren()!=1) throw new Error("asech() expects 1 argument");

		// form sqrt(1.0 - u^2)
		ASTFuncNode powNode = new ASTFuncNode();			// u^2
		powNode.setFunctionType(FunctionType.POW);
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));
		ASTMinusTermNode minusNode = new ASTMinusTermNode();// -u^2
		minusNode.jjtAddChild(powNode);
		ASTAddNode addNode = new ASTAddNode();				// (1-u^2)
		addNode.jjtAddChild(new ASTFloatNode(1.0));
		addNode.jjtAddChild(minusNode);
		ASTFuncNode sqrtNode = new ASTFuncNode();			// sqrt(1-u^2)
		sqrtNode.setFunctionType(FunctionType.SQRT);
		sqrtNode.jjtAddChild(addNode);

		// form  1 / (u * sqrt(1-u^2))		
		ASTMultNode multNode = new ASTMultNode();			// u * sqrt(1-u^2)
		multNode.jjtAddChild(jjtGetChild(0).copyTree());
		multNode.jjtAddChild(sqrtNode);
		ASTInvertTermNode invertNode = new ASTInvertTermNode(); //  1 / (u * sqrt(1-u^2))
		invertNode.jjtAddChild(multNode);

		ASTMultNode multNode_1 = new ASTMultNode();
		multNode_1.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
		multNode_1.jjtAddChild(invertNode);
		
		return multNode_1;
	}
	case FACTORIAL: {
		// 
		// case of D(factorial(u)) = ??
		//
		if (jjtGetNumChildren()!=1) throw new Error("factorial() expects 1 argument");
		ASTFloatNode floatNode = new ASTFloatNode(0.0);
		return floatNode;
	}
	default: {
		throw new Error("undefined function");
	}
	}
//	throw new ExpressionException("Derivative for function '"+getName()+"' not yet implemented");	
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
	// check this node for same state (function name)
	//	
	ASTFuncNode funcNode = (ASTFuncNode)node;
	if (funcNode.funcType != funcType){
		return false;
	}
	if (!funcNode.funcName.equals(funcName)){
		return false;
	}

	return true;
}


public double evaluateConstant() throws ExpressionException {

	double result = 0.0;
	
	switch (funcType){
	case USERDEFINED: {
		SimpleNode substitutedFunction = getSubstitutedFunction();
		if (substitutedFunction!=null){
			result = substitutedFunction.evaluateConstant();
		}else if (symbolTableFunctionEntry instanceof SymbolTableFunctionEntry.Evaluable){
			SymbolTableFunctionEntry.Evaluable evaluatableFunction = (SymbolTableFunctionEntry.Evaluable)symbolTableFunctionEntry;
			result = evaluatableFunction.evaluateConstant(getArguments());
		}else{
			throw new ExpressionException("function '"+getName()+"()' cannot be evaluated as a constant");
		}
		break;
	}
	case EXP: {
		if (jjtGetNumChildren()!=1) throw new Error("exp() expects 1 argument");
		result = Math.exp(jjtGetChild(0).evaluateConstant());
		break;
	}
	case POW: {
		if (jjtGetNumChildren()!=2) throw new Error("pow() expects 2 arguments");
		Node exponentChild = jjtGetChild(1);
		Node mantissaChild = jjtGetChild(0);
		boolean bExponentConstant = false;
		boolean bMantissaConstant = false;
		double exponent = 0.0;
		double mantissa = 0.0;
		ExpressionException savedException = null;
		try {
			exponent = exponentChild.evaluateConstant();
			bExponentConstant = true;
		}catch (ExpressionException e){
			savedException = e;
		}	
		try {
			mantissa = mantissaChild.evaluateConstant();
			bMantissaConstant = true;
		}catch (ExpressionException e){
			savedException = e;
		}	
		
		if (bExponentConstant && bMantissaConstant){
			if (mantissa<0.0 && (Math.round(exponent)!=exponent)){
				throw new FunctionDomainException("pow(u,v) and u<0 and v not an integer: undefined, u="+mantissa+", v="+exponent+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
			}
			if (mantissa==0.0 && exponent<0){
				throw new FunctionDomainException("pow(u,v) and u=0 and v<0 divide by zero, u="+mantissa+", v="+exponent+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
			}
			result = Math.pow(mantissa,exponent);
		}
		//  b
		// a   test for b = 0
		//
		else if (bExponentConstant){
			if (exponent == 0.0){
				result = 1.0;
			}else{
				throw savedException;
			}
		}	
		//  b
		// a   test for a = 1
		//
		else if (bMantissaConstant){
			if (mantissa == 1.0){
				result = 1.0;
			}else{
				throw savedException;
			}
		}else{	
			throw new ExpressionException("non-constant expression");
		}
		break;
	}
	case LOG: {
		if (jjtGetNumChildren()!=1) throw new Error("log() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (argument == 0.0){
			throw new FunctionDomainException("log() of 0.0 is undefined, '"+infixString(LANGUAGE_DEFAULT)+"' == 0.0");
		}
		if (argument < 0.0){
			throw new FunctionDomainException("log() of a negative number is undefined, '"+infixString(LANGUAGE_DEFAULT)+"' < 0.0");
		}
		result = Math.log(argument);
		break;
	}
	case ABS: {
		if (jjtGetNumChildren()!=1) throw new Error("abs() expects 1 argument");
		result = Math.abs(jjtGetChild(0).evaluateConstant());
		break;
	}
	case SQRT: {
		if (jjtGetNumChildren()!=1) throw new Error("sqrt() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (argument<0){
			throw new FunctionDomainException("sqrt(u) where u<0 is undefined: u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"' < 0.0");
		}
		result = Math.sqrt(argument);
		break;
	}
	case SIN: {
		if (jjtGetNumChildren()!=1) throw new Error("sin() expects 1 argument");
		result = Math.sin(jjtGetChild(0).evaluateConstant());
		break;
	}
	case COS: {
		if (jjtGetNumChildren()!=1) throw new Error("cos() expects 1 argument");
		result = Math.cos(jjtGetChild(0).evaluateConstant());
		break;
	}
	case TAN: {
		if (jjtGetNumChildren()!=1) throw new Error("tan() expects 1 argument");
		result = Math.tan(jjtGetChild(0).evaluateConstant());
		break;
	}
	case ASIN: {
		if (jjtGetNumChildren()!=1) throw new Error("asin() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (Math.abs(argument)>1.0){
			throw new FunctionDomainException("asin(u) and |u|>1.0 undefined, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = Math.asin(argument);
		break;
	}
	case ACOS: {
		if (jjtGetNumChildren()!=1) throw new Error("acos() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (Math.abs(argument)>1.0){
			throw new FunctionDomainException("acos(u) and |u|>1.0 undefined, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = Math.acos(argument);
		break;
	}
	case ATAN: {
		if (jjtGetNumChildren()!=1) throw new Error("atan() expects 1 argument");
		result = Math.atan(jjtGetChild(0).evaluateConstant());
		break;
	}
	case ATAN2: {
		if (jjtGetNumChildren()!=2) throw new Error("atan2() expects 2 arguments");
		double arg1 = jjtGetChild(0).evaluateConstant();
		double arg2 = jjtGetChild(1).evaluateConstant();
		if (arg1 == 0 && arg2 == 0) {
			throw new FunctionDomainException("atan2(u, v) where u=0 and v=0 is undefined, expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = Math.atan2(arg1,arg2);
		break;
	}
	case MAX: {
		if (jjtGetNumChildren()!=2) throw new Error("max() expects 2 arguments");
		result = Math.max(jjtGetChild(0).evaluateConstant(),jjtGetChild(1).evaluateConstant());
		break;
	}
	case MIN: {
		if (jjtGetNumChildren()!=2) throw new Error("min() expects 2 arguments");
		result = Math.min(jjtGetChild(0).evaluateConstant(),jjtGetChild(1).evaluateConstant());
		break;
	}
	case CEIL: {
		if (jjtGetNumChildren()!=1) throw new Error("ceil() expects 1 argument");
		result = Math.ceil(jjtGetChild(0).evaluateConstant());
		break;
	}
	case FLOOR: {
		if (jjtGetNumChildren()!=1) throw new Error("floor() expects 1 argument");
		result = Math.floor(jjtGetChild(0).evaluateConstant());
		break;
	}
	case CSC: {
		if (jjtGetNumChildren()!=1) throw new Error("csc() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (Math.abs(argument) == 0.0){
			throw new FunctionDomainException("csc(u) & u = 0.0 undefined, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.csc(argument);
		break;
	}
	case COT: {
		if (jjtGetNumChildren()!=1) throw new Error("cot() expects 1 argument");
		result = MathUtil.cot(jjtGetChild(0).evaluateConstant());
		break;
	}
	case SEC: {
		if (jjtGetNumChildren()!=1) throw new Error("sec() expects 1 argument");
		result = MathUtil.sec(jjtGetChild(0).evaluateConstant());
		break;
	}
	case ACSC: {
		if (jjtGetNumChildren()!=1) throw new Error("acsc() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (Math.abs(argument) < 1.0){
			throw new FunctionDomainException("acsc(u) and -1<u<1 undefined, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.acsc(argument);
		break;
	}
	case ACOT: {
		if (jjtGetNumChildren()!=1) throw new Error("acot() expects 1 argument");
		result = MathUtil.acot(jjtGetChild(0).evaluateConstant());
		break;
	}
	case ASEC: {
		if (jjtGetNumChildren()!=1) throw new Error("asec() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (Math.abs(argument) < 1.0){
			throw new FunctionDomainException("asec(u) and -1<u<1 undefined, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.asec(argument);
		break;
	}
	case SINH: {
		if (jjtGetNumChildren()!=1) throw new Error("sinh() expects 1 argument");
		result = Math.sinh(jjtGetChild(0).evaluateConstant());
		break;
	}
	case COSH: {
		if (jjtGetNumChildren()!=1) throw new Error("cosh() expects 1 argument");
		result = Math.cosh(jjtGetChild(0).evaluateConstant());
		break;
	}
	case TANH: {
		if (jjtGetNumChildren()!=1) throw new Error("tanh() expects 1 argument");
		result = Math.tanh(jjtGetChild(0).evaluateConstant());
		break;
	}
	case CSCH: {
		if (jjtGetNumChildren()!=1) throw new Error("csch() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (argument == 0.0){
			throw new FunctionDomainException("csch(u) and |u| = 0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.csch(argument);
		break;
	}
	case COTH: {
		if (jjtGetNumChildren()!=1) throw new Error("coth() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (argument == 0.0){
			throw new FunctionDomainException("coth(u) and |u| = 0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.coth(argument);
		break;
	}
	case SECH: {
		if (jjtGetNumChildren()!=1) throw new Error("sech() expects 1 argument");
		result = MathUtil.sech(jjtGetChild(0).evaluateConstant());
		break;
	}
	case ASINH: {
		if (jjtGetNumChildren()!=1) throw new Error("asinh() expects 1 argument");
		result = MathUtil.asinh(jjtGetChild(0).evaluateConstant());
		break;
	}
	case ACOSH: {
		if (jjtGetNumChildren()!=1) throw new Error("acosh() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (argument < 1.0){
			throw new FunctionDomainException("acosh(u) and u < 1.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.acosh(argument);
		break;
	}
	case ATANH: {
		if (jjtGetNumChildren()!=1) throw new Error("atanh() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (Math.abs(argument) >= 1.0){
			throw new FunctionDomainException("atanh(u) and |u| >= 1.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.atanh(argument);
		break;
	}
	case ACSCH: {
		if (jjtGetNumChildren()!=1) throw new Error("acsch() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (argument == 0.0){
			throw new FunctionDomainException("acsch(u) and |u| = 0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.acsch(argument);
		break;
	}
	case ACOTH: {
		if (jjtGetNumChildren()!=1) throw new Error("acoth() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (Math.abs(argument) <= 1.0){
			throw new FunctionDomainException("acoth(u) and |u| <= 1.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.acoth(argument);
		break;
	}
	case ASECH: {
		if (jjtGetNumChildren()!=1) throw new Error("asech() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (argument <= 0.0 || argument > 1.0){
			throw new FunctionDomainException("asech(u) and 0.0 <= u  and u > 1.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.asech(argument);
		break;
	}
	case FACTORIAL: {
		if (jjtGetNumChildren()!=1) throw new Error("factorial() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (Math.abs(argument) < 0.0){
			throw new FunctionDomainException("factorial(u) and u < 0.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.factorial(argument);
		break;
	}
	case LOG_10: {
		if (jjtGetNumChildren()!=1) throw new Error("log10() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (argument == 0.0){
			throw new FunctionDomainException("log10() of 0.0 is undefined, '"+infixString(LANGUAGE_DEFAULT)+"' == 0.0");
		}
		if (argument < 0.0){
			throw new FunctionDomainException("log10() of a negative number is undefined, '"+infixString(LANGUAGE_DEFAULT)+"' < 0.0");
		}
		result = Math.log(argument)/Math.log(10.0);
		break;
	}
	case LOGBASE: {
		if (jjtGetNumChildren()!=1) throw new Error("logbase() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (argument == 0.0){
			throw new FunctionDomainException("logbase() of 0.0 is undefined, '"+infixString(LANGUAGE_DEFAULT)+"' == 0.0");
		}
		if (argument < 0.0){
			throw new FunctionDomainException("logbase() of a negative number is undefined, '"+infixString(LANGUAGE_DEFAULT)+"' < 0.0");
		}
		result = 1.0/Math.log(argument);
		break;
	}
	default: {
		throw new Error("undefined function");
	}
	}
	if (Double.isInfinite(result) || Double.isNaN(result)){
		System.out.println("ASTFuncNode.evaluateConstant("+getName()+") evaluated to "+result+", exp = "+infixString(LANGUAGE_DEFAULT));
	}
	return result;
}      


public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException {

	switch (funcType){
	case USERDEFINED: {
		throw new ExpressionException("function "+funcName+" doesn't support interval evaluation");
		//break;
	}
	case EXP: {
		if (jjtGetNumChildren()!=1) throw new Error("exp() expects 1 argument");
		setInterval(IAMath.exp(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case SQRT: {
		if (jjtGetNumChildren()!=1) throw new Error("sqrt() expects 1 arguments");
		RealInterval arg = jjtGetChild(0).evaluateInterval(intervals);
		if (arg.lo()<0.0){
			throw new FunctionDomainException("sqrt(X): X is "+arg+" is undefined");
		}
		setInterval(IAMath.evenRoot(jjtGetChild(0).evaluateInterval(intervals),2),intervals);
		break;
	}
	case ABS: {
		if (jjtGetNumChildren()!=1) throw new Error("abs() expects 1 arguments");
		setInterval(IAMath.vcell_abs(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case POW: {
		if (jjtGetNumChildren()!=2) throw new Error("pow() expects 2 arguments");
		try {
			setInterval(IAMath.vcell_power(jjtGetChild(0).evaluateInterval(intervals),jjtGetChild(1).evaluateInterval(intervals)),intervals);
		}catch (IAFunctionDomainException e){
			e.printStackTrace(System.out);
			throw new FunctionDomainException(e.getMessage());
		}
		break;
	}
	case LOG: {
		if (jjtGetNumChildren()!=1) throw new Error("log() expects 1 argument");
		RealInterval arg = jjtGetChild(0).evaluateInterval(intervals);
		if (arg.lo()<=0.0){
			throw new FunctionDomainException("log(X): X is "+arg+" is undefined");
		}
		setInterval(IAMath.log(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case SIN: {
		if (jjtGetNumChildren()!=1) throw new Error("sin() expects 1 argument");
		setInterval(IAMath.sin(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case COS: {
		if (jjtGetNumChildren()!=1) throw new Error("cos() expects 1 argument");
		setInterval(IAMath.cos(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case TAN: {
		if (jjtGetNumChildren()!=1) throw new Error("tan() expects 1 argument");
		setInterval(IAMath.tan(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case ASIN: {
		if (jjtGetNumChildren()!=1) throw new Error("asin() expects 1 argument");
		RealInterval arg = jjtGetChild(0).evaluateInterval(intervals);
		if (arg.lo()<-1.0 || arg.hi()>1.0){
			throw new FunctionDomainException("asin(X): X is "+arg+" is undefined");
		}
		setInterval(IAMath.asin(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case ACOS: {
		if (jjtGetNumChildren()!=1) throw new Error("acos() expects 1 argument");
		RealInterval arg = jjtGetChild(0).evaluateInterval(intervals);
		if (arg.lo()<-1.0 || arg.hi()>1.0){
			throw new FunctionDomainException("acos(X): X is "+arg+" is undefined");
		}
		setInterval(IAMath.acos(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case ATAN: {
		if (jjtGetNumChildren()!=1) throw new Error("atan() expects 1 argument");
		setInterval(IAMath.atan(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case ATAN2: {
		if (jjtGetNumChildren()!=2) throw new Error("atan2() expects 2 arguments");
		throw new RuntimeException("interval arithmetic not implemented for function atan2()");
//		setInterval(IAMath.atan(IAMath.div(jjtGetChild(0).evaluateInterval(intervals),jjtGetChild(1).evaluateInterval(intervals))),intervals);
//		break;
	}
	case MAX: {
		if (jjtGetNumChildren()!=2) throw new Error("max() expects 2 arguments");
		setInterval(IAMath.vcell_max(jjtGetChild(0).evaluateInterval(intervals),jjtGetChild(1).evaluateInterval(intervals)),intervals);
		break;
	}
	case MIN: {
		if (jjtGetNumChildren()!=2) throw new Error("min() expects 2 arguments");
		setInterval(IAMath.vcell_min(jjtGetChild(0).evaluateInterval(intervals),jjtGetChild(1).evaluateInterval(intervals)),intervals);
		break;
	}
	case CEIL: {
		if (jjtGetNumChildren()!=1) throw new Error("ceil() expects 1 arguments");
		setInterval(IAMath.vcell_ceil(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case FLOOR: {
		if (jjtGetNumChildren()!=1) throw new Error("floor() expects 1 arguments");
		setInterval(IAMath.vcell_floor(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case CSC: {
		if (jjtGetNumChildren()!=1) throw new Error("csc() expects 1 argument");
		setInterval(IAMath.vcell_csc(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case COT: {
		if (jjtGetNumChildren()!=1) throw new Error("cot() expects 1 argument");
		setInterval(IAMath.vcell_cot(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case SEC: {
		if (jjtGetNumChildren()!=1) throw new Error("sec() expects 1 argument");
		setInterval(IAMath.vcell_sec(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case ACSC: {
		if (jjtGetNumChildren()!=1) throw new Error("acsc() expects 1 argument");
		RealInterval arg = jjtGetChild(0).evaluateInterval(intervals);
		if (arg.lo()<-1.0 || arg.hi()>1.0){
			throw new FunctionDomainException("acscX): X is "+arg+" is undefined");
		}
		setInterval(IAMath.vcell_acsc(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case ACOT: {
		if (jjtGetNumChildren()!=1) throw new Error("acot() expects 1 argument");
		RealInterval arg = jjtGetChild(0).evaluateInterval(intervals);
		if (arg.lo()<-1.0 || arg.hi()>1.0){
			throw new FunctionDomainException("acot(X): X is "+arg+" is undefined");
		}
		setInterval(IAMath.vcell_acot(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case ASEC: {
		if (jjtGetNumChildren()!=1) throw new Error("asec() expects 1 argument");
		setInterval(IAMath.vcell_asec(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case SINH: {
		if (jjtGetNumChildren()!=1) throw new Error("sinh() expects 1 argument");
		setInterval(IAMath.vcell_sinh(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case COSH: {
		if (jjtGetNumChildren()!=1) throw new Error("cosh() expects 1 argument");
		try {
			setInterval(IAMath.vcell_cosh(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		}catch (IAFunctionDomainException e){
			e.printStackTrace(System.out);
			throw new FunctionDomainException(e.getMessage());
		}
		break;
	}
	case TANH: {
		if (jjtGetNumChildren()!=1) throw new Error("tanh() expects 1 argument");
		setInterval(IAMath.vcell_tanh(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case CSCH: {
		if (jjtGetNumChildren()!=1) throw new Error("csch() expects 1 argument");
		try {
			setInterval(IAMath.vcell_csch(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		}catch (IAFunctionDomainException e){
			e.printStackTrace(System.out);
			throw new FunctionDomainException(e.getMessage());
		}
		break;
	}
	case COTH: {
		if (jjtGetNumChildren()!=1) throw new Error("coth() expects 1 argument");
		try {
			setInterval(IAMath.vcell_coth(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		}catch (IAFunctionDomainException e){
			e.printStackTrace(System.out);
			throw new FunctionDomainException(e.getMessage());
		}
		break;
	}
	case SECH: {
		if (jjtGetNumChildren()!=1) throw new Error("sech() expects 1 argument");
		setInterval(IAMath.vcell_sech(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case ASINH: {
		if (jjtGetNumChildren()!=1) throw new Error("asinh() expects 1 argument");
		RealInterval arg = jjtGetChild(0).evaluateInterval(intervals);
		if (arg.lo()<-1.0 || arg.hi()>1.0){
			throw new FunctionDomainException("asinh(X): X is "+arg+" is undefined");
		}
		setInterval(IAMath.vcell_asinh(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		break;
	}
	case ACOSH: {
		if (jjtGetNumChildren()!=1) throw new Error("acosh() expects 1 argument");
		RealInterval arg = jjtGetChild(0).evaluateInterval(intervals);
		if (arg.lo()<-1.0 || arg.hi()>1.0){
			throw new FunctionDomainException("acosh(X): X is "+arg+" is undefined");
		}
		try {
			setInterval(IAMath.vcell_acosh(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		}catch (IAFunctionDomainException e){
			e.printStackTrace(System.out);
			throw new FunctionDomainException(e.getMessage());
		}
		break;
	}
	case ATANH: {
		if (jjtGetNumChildren()!=1) throw new Error("atanh() expects 1 argument");
		try {
			setInterval(IAMath.vcell_atanh(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		}catch (IAFunctionDomainException e){
			e.printStackTrace(System.out);
			throw new FunctionDomainException(e.getMessage());
		}
		break;
	}
	case ACSCH: {
		if (jjtGetNumChildren()!=1) throw new Error("acsch() expects 1 argument");
		RealInterval arg = jjtGetChild(0).evaluateInterval(intervals);
		if (arg.lo()<-1.0 || arg.hi()>1.0){
			throw new FunctionDomainException("acsch(X): X is "+arg+" is undefined");
		}
		try {
			setInterval(IAMath.vcell_acsch(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		}catch (IAFunctionDomainException e){
			e.printStackTrace(System.out);
			throw new FunctionDomainException(e.getMessage());
		}
		break;
	}
	case ACOTH: {
		if (jjtGetNumChildren()!=1) throw new Error("acoth() expects 1 argument");
		RealInterval arg = jjtGetChild(0).evaluateInterval(intervals);
		if (arg.lo()<-1.0 || arg.hi()>1.0){
			throw new FunctionDomainException("acoth(X): X is "+arg+" is undefined");
		}
		try {
			setInterval(IAMath.vcell_acoth(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		}catch (IAFunctionDomainException e){
			e.printStackTrace(System.out);
			throw new FunctionDomainException(e.getMessage());
		}
		break;
	}
	case ASECH: {
		if (jjtGetNumChildren()!=1) throw new Error("asech() expects 1 argument");
		try {
			setInterval(IAMath.vcell_asech(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		}catch (IAFunctionDomainException e){
			e.printStackTrace(System.out);
			throw new FunctionDomainException(e.getMessage());
		}
		break;
	}
	case FACTORIAL: {
		if (jjtGetNumChildren()!=1) throw new Error("factorial() expects 1 arguments");
		try {
			setInterval(IAMath.vcell_factorial(jjtGetChild(0).evaluateInterval(intervals)),intervals);
		}catch (IAFunctionDomainException e){
			e.printStackTrace(System.out);
			throw new FunctionDomainException(e.getMessage());
		}
		break;
	}
	default: {
		throw new RuntimeException("undefined function "+getName()+" in ASTFuncNode");
	}
	}
	return getInterval(intervals);
}      


public double evaluateVector(double values[]) throws ExpressionException {

	double result;
	
	switch (funcType){
	case USERDEFINED: {
		SimpleNode substitutedFunction = getSubstitutedFunction();
		if (substitutedFunction!=null){
			result = substitutedFunction.evaluateVector(values);
		}else if (symbolTableFunctionEntry instanceof SymbolTableFunctionEntry.Evaluable){
			SymbolTableFunctionEntry.Evaluable evaluatableFunction = (SymbolTableFunctionEntry.Evaluable)symbolTableFunctionEntry;
			result = evaluatableFunction.evaluateVector(getArguments(),values);
		}else{
			throw new ExpressionException("function '"+getName()+"()' cannot be evaluated");
		}
		break;
	}
	case EXP: {
		if (jjtGetNumChildren()!=1) throw new Error("exp() expects 1 argument");
		result = Math.exp(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case SQRT: {
		if (jjtGetNumChildren()!=1) throw new Error("sqrt() expects 1 arguments");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (argument<0){
			throw new FunctionDomainException("sqrt(u) where u<0 is undefined: u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"' < 0.0");
		}
		result = Math.sqrt(argument);
		break;
	}
	case ABS: {
		if (jjtGetNumChildren()!=1) throw new Error("abs() expects 1 arguments");
		result = Math.abs(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case POW: {
		if (jjtGetNumChildren()!=2) throw new Error("pow() expects 2 arguments");
		double u = jjtGetChild(0).evaluateVector(values);
		double v = jjtGetChild(1).evaluateVector(values);
		if (u<0 && Math.round(v)!=v){
			throw new FunctionDomainException("pow(u,v) and u<0 and v not an integer: undefined, u="+u+", v="+v+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		if (u==0.0 && v<0){
			throw new FunctionDomainException("pow(u,v) and u=0 and v<0 divide by zero, u="+u+", v="+v+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		if (u>=0.0 && v==1.0){
			return u;
		}
		result = Math.pow(u,v);
		break;
	}
	case LOG: {
		if (jjtGetNumChildren()!=1) throw new Error("log() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (argument == 0.0){
			throw new FunctionDomainException("log() of 0.0 is undefined, '"+infixString(LANGUAGE_DEFAULT)+"' == 0.0");
		}
		if (argument < 0.0){
			throw new FunctionDomainException("log() of a negative number is undefined, '"+infixString(LANGUAGE_DEFAULT)+"' < 0.0");
		}
		result = Math.log(argument);
		break;
	}
	case SIN: {
		if (jjtGetNumChildren()!=1) throw new Error("sin() expects 1 argument");
		result = Math.sin(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case COS: {
		if (jjtGetNumChildren()!=1) throw new Error("cos() expects 1 argument");
		result = Math.cos(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case TAN: {
		if (jjtGetNumChildren()!=1) throw new Error("tan() expects 1 argument");
		result = Math.tan(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case ASIN: {
		if (jjtGetNumChildren()!=1) throw new Error("asin() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (Math.abs(argument)>1.0){
			throw new FunctionDomainException("asin(u) and |u|>1.0 undefined, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = Math.asin(argument);
		break;
	}
	case ACOS: {
		if (jjtGetNumChildren()!=1) throw new Error("acos() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (Math.abs(argument)>1.0){
			throw new FunctionDomainException("acos(u) and |u|>1.0 undefined, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = Math.acos(argument);
		break;
	}
	case ATAN: {
		if (jjtGetNumChildren()!=1) throw new Error("atan() expects 1 argument");
		result = Math.atan(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case ATAN2: {
		if (jjtGetNumChildren()!=2) throw new Error("atan2() expects 2 arguments");
		double arg1 = jjtGetChild(0).evaluateVector(values);
		double arg2 = jjtGetChild(1).evaluateVector(values);
		if (arg1 == 0 && arg2 == 0) {
			throw new FunctionDomainException("atan2(u, v) where u=0 and v=0 is undefined, expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = Math.atan2(arg1, arg2);
		break;
	}
	case MAX: {
		if (jjtGetNumChildren()!=2) throw new Error("max() expects 2 arguments");
		result = Math.max(jjtGetChild(0).evaluateVector(values),jjtGetChild(1).evaluateVector(values));
		break;
	}
	case MIN: {
		if (jjtGetNumChildren()!=2) throw new Error("min() expects 2 arguments");
		result = Math.min(jjtGetChild(0).evaluateVector(values),jjtGetChild(1).evaluateVector(values));
		break;
	}
	case CEIL: {
		if (jjtGetNumChildren()!=1) throw new Error("ceil() expects 1 argument");
		result = Math.ceil(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case FLOOR: {
		if (jjtGetNumChildren()!=1) throw new Error("floor() expects 1 argument");
		result = Math.floor(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case CSC: {
		if (jjtGetNumChildren()!=1) throw new Error("csc() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (Math.abs(argument) == 0.0){
			throw new FunctionDomainException("csc(u) & u = 0.0 undefined, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.csc(argument);
		break;
	}
	case COT: {
		if (jjtGetNumChildren()!=1) throw new Error("cot() expects 1 argument");
		result = MathUtil.cot(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case SEC: {
		if (jjtGetNumChildren()!=1) throw new Error("sec() expects 1 argument");
		result = MathUtil.sec(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case ACSC: {
		if (jjtGetNumChildren()!=1) throw new Error("acsc() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (Math.abs(argument) < 1.0){
			throw new FunctionDomainException("acsc(u) is undefined in -1<u<1, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.acsc(argument);
		break;
	}
	case ACOT: {
		if (jjtGetNumChildren()!=1) throw new Error("acot() expects 1 argument");
		result = MathUtil.acot(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case ASEC: {
		if (jjtGetNumChildren()!=1) throw new Error("asec() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (Math.abs(argument) < 1.0){
			throw new FunctionDomainException("asec(u) is undefined in -1<u<1, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.asec(argument);
		break;
	}
	case SINH: {
		if (jjtGetNumChildren()!=1) throw new Error("sinh() expects 1 argument");
		result = Math.sinh(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case COSH: {
		if (jjtGetNumChildren()!=1) throw new Error("cosh() expects 1 argument");
		result = Math.cosh(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case TANH: {
		if (jjtGetNumChildren()!=1) throw new Error("tanh() expects 1 argument");
		result = Math.tanh(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case CSCH: {
		if (jjtGetNumChildren()!=1) throw new Error("csch() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (argument == 0.0){
			throw new FunctionDomainException("csch(u) is not defined for |u| = 0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.csch(argument);
		break;
	}
	case COTH: {
		if (jjtGetNumChildren()!=1) throw new Error("coth() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (argument == 0.0){
			throw new FunctionDomainException("coth(u) is not defined for |u| = 0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.coth(argument);
		break;
	}
	case SECH: {
		if (jjtGetNumChildren()!=1) throw new Error("sech() expects 1 argument");
		result = MathUtil.sech(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case ASINH: {
		if (jjtGetNumChildren()!=1) throw new Error("asinh() expects 1 argument");
		result = MathUtil.asinh(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case ACOSH: {
		if (jjtGetNumChildren()!=1) throw new Error("acosh() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (argument < 1.0){
			throw new FunctionDomainException("acosh(u) is not defined for u < 1.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.acosh(argument);
		break;
	}
	case ATANH: {
		if (jjtGetNumChildren()!=1) throw new Error("atanh() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (Math.abs(argument) >= 1.0){
			throw new FunctionDomainException("atanh(u) is not defined in |u| >= 1.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.atanh(argument);
		break;
	}
	case ACSCH: {
		if (jjtGetNumChildren()!=1) throw new Error("acsch() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (argument == 0.0){
			throw new FunctionDomainException("acsch(u) is not defined for |u| = 0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.acsch(argument);
		break;
	}
	case ACOTH: {
		if (jjtGetNumChildren()!=1) throw new Error("acoth() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (Math.abs(argument) <= 1.0){
			throw new FunctionDomainException("acoth(u) is not defined in |u| <= 1.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.acoth(argument);
		break;
	}
	case ASECH: {
		if (jjtGetNumChildren()!=1) throw new Error("asech() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (argument <= 0.0 || argument > 1.0){
			throw new FunctionDomainException("asech(u) is not defined in 0.0 <= u  and u > 1.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.asech(argument);
		break;
	}
	case FACTORIAL: {
		if (jjtGetNumChildren()!=1) throw new Error("factorial() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values); 
		if (Math.abs(argument) < 0.0){
			throw new FunctionDomainException("factorial(u) and u < 0.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT)+"'");
		}
		result = MathUtil.factorial(argument);
		break;
	}
	default: {
		throw new Error("undefined function");
	}
	}
	if (Double.isNaN(result) || Double.isInfinite(result)){
		System.out.println("ASTFuncNode.evaluateVector("+funcType.getName()+") evaluated to "+result);
	}
	return result;
}      


/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
public Node flatten() throws ExpressionException {
	
	try {
		double value = evaluateConstant();
		return new ASTFloatNode(value);
	}catch (Exception e){}		
	ASTFuncNode funcNode = new ASTFuncNode();
	funcNode.funcType = funcType;
	funcNode.funcName = funcName;
	funcNode.symbolTableFunctionEntry = symbolTableFunctionEntry;
	java.util.Vector<Node> tempChildren = new java.util.Vector<Node>();

	for (int i=0;i<jjtGetNumChildren();i++){
		tempChildren.addElement(jjtGetChild(i).flatten());
	}

	switch (funcType){
	case USERDEFINED: {
		break;
	}
	case EXP: {
		if (tempChildren.size()!=1) throw new ExpressionException("exp() expects 1 argument");
		break;
	}
	case POW: {
		if (tempChildren.size()!=2) throw new ExpressionException("pow() expects 2 arguments");
		//
		//  b
		// a   test for b = 1
		//
		Node exponentChild = (Node)tempChildren.elementAt(1);
		Node mantissaChild = (Node)tempChildren.elementAt(0);
		if (exponentChild instanceof ASTFloatNode){
			double exponent = ((ASTFloatNode)exponentChild).value.doubleValue();
			if (exponent == 1.0){
				return mantissaChild;
			}
		}
		//
		//   w    
		//  v          v*w
		// u    --->  u
		//
		if (mantissaChild instanceof ASTFuncNode){
			if (((ASTFuncNode)mantissaChild).funcType == FunctionType.POW){
				ASTMultNode newMultNode = new ASTMultNode();
				newMultNode.jjtAddChild(mantissaChild.jjtGetChild(1));
				newMultNode.jjtAddChild(exponentChild);
				ASTFuncNode newExponentNode = new ASTFuncNode();
				newExponentNode.funcType = funcType;
				newExponentNode.funcName = funcName;
				newExponentNode.jjtAddChild(mantissaChild.jjtGetChild(0));
				newExponentNode.jjtAddChild(newMultNode);
				return newExponentNode.flatten();
			}
		}
		break;
	}
	case SQRT: {
		if (tempChildren.size()!=1) throw new ExpressionException("sqrt() expects 1 argument");
		//
		//  
		// sqrt(a^2) -> abs(a)
		// sqrt(pow(a,2)) -> abs(a)
		//
		Node child = (Node)tempChildren.elementAt(0);
		if (child instanceof ASTFuncNode){
			if (((ASTFuncNode)child).funcType == FunctionType.POW){
				Node childPowMantissa = child.jjtGetChild(0);
				Node childPowExponent = child.jjtGetChild(1);
				if (childPowExponent instanceof ASTFloatNode && ((ASTFloatNode)childPowExponent).value.doubleValue() == 2.0){
					ASTFuncNode newAbsNode = new ASTFuncNode();
					newAbsNode.setFunctionType(FunctionType.ABS);
					newAbsNode.jjtAddChild(childPowMantissa);
					return newAbsNode.flatten();
				}
			}
		}else if (child instanceof ASTPowerNode){
			Node childPowMantissa = child.jjtGetChild(0);
			Node childPowExponent = child.jjtGetChild(1);
			if (childPowExponent instanceof ASTFloatNode && ((ASTFloatNode)childPowExponent).value.doubleValue() == 2.0){
				ASTFuncNode newAbsNode = new ASTFuncNode();
				newAbsNode.setFunctionType(FunctionType.ABS);
				newAbsNode.jjtAddChild(childPowMantissa);
				return newAbsNode.flatten();
			}
		}
		break;
	}
	case ABS: {
		if (tempChildren.size()!=1) throw new ExpressionException("abs() expects 1 argument");
		break;
	}
	case LOG: {
		if (tempChildren.size()!=1) throw new ExpressionException("log() expects 1 argument");
		break;
	}
	case LOG_10: {
		if (tempChildren.size()!=1) throw new ExpressionException("log10() expects 1 argument");
		break;
	}
	case SIN: {
		if (tempChildren.size()!=1) throw new ExpressionException("sin() expects 1 argument");
		break;
	}
	case COS: {
		if (tempChildren.size()!=1) throw new ExpressionException("cos() expects 1 argument");
		break;
	}
	case TAN: {
		if (tempChildren.size()!=1) throw new ExpressionException("tan() expects 1 argument");
		break;
	}
	case ASIN: {
		if (tempChildren.size()!=1) throw new ExpressionException("asin() expects 1 argument");
		break;
	}
	case ACOS: {
		if (tempChildren.size()!=1) throw new ExpressionException("acos() expects 1 argument");
		break;
	}
	case ATAN: {
		if (tempChildren.size()!=1) throw new ExpressionException("atan() expects 1 argument");
		break;
	}
	case ATAN2: {
		if (tempChildren.size()!=2) throw new ExpressionException("atan2() expects 2 arguments");
		break;
	}
	case MAX: {
		if (tempChildren.size()!=2) throw new ExpressionException("max() expects 2 arguments");
		break;
	}
	case MIN: {
		if (tempChildren.size()!=2) throw new ExpressionException("min() expects 2 arguments");
		break;
	}
	case CEIL: {
		if (tempChildren.size()!=1) throw new ExpressionException("ceil() expects 1 argument");
		break;
	}
	case FLOOR: {
		if (tempChildren.size()!=1) throw new ExpressionException("floor() expects 1 argument");
		break;
	}
	case CSC: {
		if (tempChildren.size()!=1) throw new ExpressionException("csc() expects 1 argument");
		break;
	}
	case COT: {
		if (tempChildren.size()!=1) throw new ExpressionException("cot() expects 1 argument");
		break;
	}
	case SEC: {
		if (tempChildren.size()!=1) throw new ExpressionException("sec() expects 1 argument");
		break;
	}
	case ACSC: {
		if (tempChildren.size()!=1) throw new ExpressionException("acsc() expects 1 argument");
		break;
	}
	case ACOT: {
		if (tempChildren.size()!=1) throw new ExpressionException("acot() expects 1 argument");
		break;
	}
	case ASEC: {
		if (tempChildren.size()!=1) throw new ExpressionException("asec() expects 1 argument");
		break;
	}
	case SINH: {
		if (tempChildren.size()!=1) throw new ExpressionException("sinh() expects 1 argument");
		break;
	}
	case COSH: {
		if (tempChildren.size()!=1) throw new ExpressionException("cosh() expects 1 argument");
		break;
	}
	case TANH: {
		if (tempChildren.size()!=1) throw new ExpressionException("tanh() expects 1 argument");
		break;
	}
	case CSCH: {
		if (tempChildren.size()!=1) throw new ExpressionException("csch() expects 1 argument");
		break;
	}
	case COTH: {
		if (tempChildren.size()!=1) throw new ExpressionException("coth() expects 1 argument");
		break;
	}
	case SECH: {
		if (tempChildren.size()!=1) throw new ExpressionException("sech() expects 1 argument");
		break;
	}
	case ASINH: {
		if (tempChildren.size()!=1) throw new ExpressionException("asinh() expects 1 argument");
		break;
	}
	case ACOSH: {
		if (tempChildren.size()!=1) throw new ExpressionException("acosh() expects 1 argument");
		break;
	}
	case ATANH: {
		if (tempChildren.size()!=1) throw new ExpressionException("atanh() expects 1 argument");
		break;
	}
	case ACSCH: {
		if (tempChildren.size()!=1) throw new ExpressionException("acsch() expects 1 argument");
		break;
	}
	case ACOTH: {
		if (tempChildren.size()!=1) throw new ExpressionException("acoth() expects 1 argument");
		break;
	}
	case ASECH: {
		if (tempChildren.size()!=1) throw new ExpressionException("asech() expects 1 argument");
		break;
	}
	case FACTORIAL: {
		if (tempChildren.size()!=1) throw new ExpressionException("factorial() expects 1 argument");
		break;
	}
	default: {
		throw new ExpressionException("undefined function");
	}
	}
	for (int i=0;i<tempChildren.size();i++){
		funcNode.jjtAddChild((Node)tempChildren.elementAt(i));
	}
	return funcNode;	
}

void getFunctionInvocations(java.util.Vector<FunctionInvocation> v, FunctionFilter filter) {
	if (filter==null || filter.accept(getName(),getFunction())){
		FunctionInvocation functionInvocation = new FunctionInvocation(this);
		if (!v.contains(functionInvocation)){
			v.add(functionInvocation);
		}
	}
	super.getFunctionInvocations(v, filter);
}

private Expression[] getArguments(){
	int numChildren = jjtGetNumChildren();
	Expression[] arguments = new Expression[numChildren];
	for (int i=0;i<numChildren;i++){
		arguments[i] = new Expression((SimpleNode)jjtGetChild(i).copyTree());
	}
	return arguments;
}


/**
 * Insert the method's description here.
 * Creation date: (12/20/2002 2:20:27 PM)
 * @return int
 */
FunctionType getFunction() {
	return funcType;
}





/**
 * Insert the method's description here.
 * Creation date: (2/8/2002 4:29:47 PM)
 * @return java.lang.String
 */
String getMathMLName() {
	if (funcType == FunctionType.USERDEFINED){
		return funcName;
	}
	return funcType.getMathMLTag();
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2002 4:01:47 PM)
 * @return java.lang.String
 */
String getName() {
	return funcName;
}

/**
 * Insert the method's description here.
 * Creation date: (2/8/2002 4:29:47 PM)
 * @return java.lang.String
 */
static String getVCellFunctionNameFromMathMLFuncName(String mathMLFunctName) {
	//
	// find the MathML function name in it's array, and return the corresponding VCell function name
	//
	for (FunctionType ft : FunctionType.values()){
		if (mathMLFunctName.equals(ft.getMathMLTag())) {
			return ft.getName();
		} 
	}
	return null;
}

public String infixString(int lang) {
	
	StringBuffer buffer = new StringBuffer();

	switch(funcType){
		//
		// pow() is treated specially, Matlab needs a^b.
		//
	 	case POW: {		      
	 		if (lang == LANGUAGE_MATLAB || lang == LANGUAGE_ECLiPSe || lang == LANGUAGE_JSCL || lang == LANGUAGE_UNITS){
				buffer.append("(");
				buffer.append(jjtGetChild(0).infixString(lang));
				buffer.append(" ^ ");
				buffer.append(jjtGetChild(1).infixString(lang));
				buffer.append(")");
			} else  if (lang == LANGUAGE_C){
				buffer.append("pow(");
				buffer.append("((double)(" + jjtGetChild(0).infixString(lang) + "))");
				buffer.append(",");
				buffer.append("((double)(" + jjtGetChild(1).infixString(lang) + "))");
				buffer.append(")");
			} else { // default behavior (like LANGUAGE_DEFAULT)
	 			buffer.append("pow(");
	 			buffer.append(jjtGetChild(0).infixString(lang));
	 			buffer.append(",");
	 			buffer.append(jjtGetChild(1).infixString(lang));
	 			buffer.append(")");
			}
			break;
		}
	 	default:{
			buffer.append(getName() + "(");
			for (int i=0;i<jjtGetNumChildren();i++){
				if (i>0) buffer.append(", ");
				if (lang == LANGUAGE_C){
					buffer.append("((double)(" + jjtGetChild(i).infixString(lang) + "))");
				} else {
					buffer.append(jjtGetChild(i).infixString(lang));
				}
			}
			buffer.append(")");
			break;
	 	}
	}
	
	return buffer.toString();
	
}

/**
 * Insert the method's description here.
 * Creation date: (6/20/01 11:04:41 AM)
 * @return boolean
 *
 */
public boolean narrow(RealInterval intervals[]) throws ExpressionBindingException{
	switch (funcType){
	case USERDEFINED: {
		throw new RuntimeException("cannot narrow user defined functions : "+funcName);
	}
	case EXP: {
		if (jjtGetNumChildren()!=1) throw new Error("exp() expects 1 argument");
		return IANarrow.narrow_exp(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.narrow_exp(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case SQRT: {
		if (jjtGetNumChildren()!=1) throw new Error("sqrt() expects 1 arguments");
		return IANarrow.narrow_power(getInterval(intervals),jjtGetChild(0).getInterval(intervals),new RealInterval(0.5))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.narrow_power(getInterval(intervals),jjtGetChild(0).getInterval(intervals),new RealInterval(0.5));
	}
	case ABS: {
		if (jjtGetNumChildren()!=1) throw new Error("abs() expects 1 arguments");
		return IANarrow.vcell_narrow_abs(getInterval(intervals),jjtGetChild(0).getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_abs(getInterval(intervals),jjtGetChild(0).getInterval(intervals));
	}
	case POW: {
		if (jjtGetNumChildren()!=2) throw new Error("pow() expects 2 arguments");
		return IANarrow.vcell_narrow_power(getInterval(intervals),jjtGetChild(0).getInterval(intervals),jjtGetChild(1).getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& jjtGetChild(1).narrow(intervals)
				&& IANarrow.vcell_narrow_power(getInterval(intervals),jjtGetChild(0).getInterval(intervals),jjtGetChild(1).getInterval(intervals));
	}
	case LOG: {
		if (jjtGetNumChildren()!=1) throw new Error("log() expects 1 argument");
		return IANarrow.narrow_log(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.narrow_log(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case SIN: {
		if (jjtGetNumChildren()!=1) throw new Error("sin() expects 1 argument");
		return IANarrow.narrow_sin(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.narrow_sin(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case COS: {
		if (jjtGetNumChildren()!=1) throw new Error("cos() expects 1 argument");
		return IANarrow.narrow_cos(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.narrow_cos(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case TAN: {
		if (jjtGetNumChildren()!=1) throw new Error("tan() expects 1 argument");
		return IANarrow.narrow_tan(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.narrow_tan(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case ASIN: {
		if (jjtGetNumChildren()!=1) throw new Error("asin() expects 1 argument");
		return IANarrow.narrow_asin(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.narrow_asin(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case ACOS: {
		if (jjtGetNumChildren()!=1) throw new Error("acos() expects 1 argument");
		return IANarrow.narrow_acos(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.narrow_acos(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case ATAN: {
		if (jjtGetNumChildren()!=1) throw new Error("atan() expects 1 argument");
		return IANarrow.narrow_atan(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.narrow_atan(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case ATAN2: {
		if (jjtGetNumChildren()!=2) throw new Error("atan2() expects 2 arguments");
		throw new RuntimeException("ASTFunctionNode.narrow(intervals) for function 'atan2()' is undefined");
	}
	case MAX: {
		if (jjtGetNumChildren()!=2) throw new Error("max() expects 2 arguments");
		return IANarrow.vcell_narrow_max(getInterval(intervals),jjtGetChild(0).getInterval(intervals),jjtGetChild(1).getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& jjtGetChild(1).narrow(intervals)
				&& IANarrow.vcell_narrow_max(getInterval(intervals),jjtGetChild(0).getInterval(intervals),jjtGetChild(1).getInterval(intervals));
	}
	case MIN: {
		if (jjtGetNumChildren()!=2) throw new Error("min() expects 2 arguments");
		return IANarrow.vcell_narrow_min(getInterval(intervals),jjtGetChild(0).getInterval(intervals),jjtGetChild(1).getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& jjtGetChild(1).narrow(intervals)
				&& IANarrow.vcell_narrow_min(getInterval(intervals),jjtGetChild(0).getInterval(intervals),jjtGetChild(1).getInterval(intervals));
	}
	case CEIL: {
		if (jjtGetNumChildren()!=1) throw new Error("ceil() expects 1 argument");
		return IANarrow.vcell_narrow_ceil(getInterval(intervals), jjtGetChild(0).getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_ceil(getInterval(intervals), jjtGetChild(0).getInterval(intervals));
	}
	case FLOOR: {
		if (jjtGetNumChildren()!=1) throw new Error("floor() expects 1 argument");
		return IANarrow.vcell_narrow_floor(getInterval(intervals), jjtGetChild(0).getInterval(intervals))	
				&& jjtGetChild(0).narrow(intervals) 
				&& IANarrow.vcell_narrow_floor(getInterval(intervals),jjtGetChild(0).getInterval(intervals));
	}
	case CSC: {
		if (jjtGetNumChildren()!=1) throw new Error("csc() expects 1 argument");
		return IANarrow.vcell_narrow_csc(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_csc(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case COT: {
		if (jjtGetNumChildren()!=1) throw new Error("cot() expects 1 argument");
		return IANarrow.vcell_narrow_cot(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_cot(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case SEC: {
		if (jjtGetNumChildren()!=1) throw new Error("sec() expects 1 argument");
		return IANarrow.vcell_narrow_sec(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_sec(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case ACSC: {
		if (jjtGetNumChildren()!=1) throw new Error("acsc() expects 1 argument");
		return IANarrow.vcell_narrow_acsc(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_acsc(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case ACOT: {
		if (jjtGetNumChildren()!=1) throw new Error("acot() expects 1 argument");
		return IANarrow.vcell_narrow_acot(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_acot(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case ASEC: {
		if (jjtGetNumChildren()!=1) throw new Error("asec() expects 1 argument");
		return IANarrow.vcell_narrow_asec(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_asec(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case SINH: {
		if (jjtGetNumChildren()!=1) throw new Error("sinh() expects 1 argument");
		return IANarrow.vcell_narrow_sinh(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_sinh(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case COSH: {
		if (jjtGetNumChildren()!=1) throw new Error("cosh() expects 1 argument");
		return IANarrow.vcell_narrow_cosh(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_cosh(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case TANH: {
		if (jjtGetNumChildren()!=1) throw new Error("tanh() expects 1 argument");
		return IANarrow.vcell_narrow_tanh(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_tanh(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case CSCH: {
		if (jjtGetNumChildren()!=1) throw new Error("csch() expects 1 argument");
		return IANarrow.vcell_narrow_csch(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_csch(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case COTH: {
		if (jjtGetNumChildren()!=1) throw new Error("coth() expects 1 argument");
		return IANarrow.vcell_narrow_coth(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_coth(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case SECH: {
		if (jjtGetNumChildren()!=1) throw new Error("sech() expects 1 argument");
		return IANarrow.vcell_narrow_sech(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_sech(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case ASINH: {
		if (jjtGetNumChildren()!=1) throw new Error("asinh() expects 1 argument");
		return IANarrow.vcell_narrow_asinh(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_asinh(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case ACOSH: {
		if (jjtGetNumChildren()!=1) throw new Error("acosh() expects 1 argument");
		return IANarrow.vcell_narrow_acosh(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_acosh(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case ATANH: {
		if (jjtGetNumChildren()!=1) throw new Error("atanh() expects 1 argument");
		return IANarrow.vcell_narrow_atanh(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_atanh(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case ACSCH: {
		if (jjtGetNumChildren()!=1) throw new Error("acsch() expects 1 argument");
		return IANarrow.vcell_narrow_acsch(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_acsch(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case ACOTH: {
		if (jjtGetNumChildren()!=1) throw new Error("acoth() expects 1 argument");
		return IANarrow.vcell_narrow_acoth(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_acoth(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case ASECH: {
		if (jjtGetNumChildren()!=1) throw new Error("asech() expects 1 argument");
		return IANarrow.vcell_narrow_asech(jjtGetChild(0).getInterval(intervals),getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_asech(jjtGetChild(0).getInterval(intervals),getInterval(intervals));
	}
	case FACTORIAL: {
		if (jjtGetNumChildren()!=1) throw new Error("factorial() expects 1 argument");
		return IANarrow.vcell_narrow_factorial(getInterval(intervals), jjtGetChild(0).getInterval(intervals))
				&& jjtGetChild(0).narrow(intervals)
				&& IANarrow.vcell_narrow_factorial(getInterval(intervals), jjtGetChild(0).getInterval(intervals));
	}
	default: {
		throw new RuntimeException("undefined function "+getName()+" in ASTFuncNode");
	}
	}
}

void setFunctionType(FunctionType funcType) {
	if (funcType == FunctionType.USERDEFINED) {
		throw new IllegalArgumentException("ASTFunctionNode.setFunctionType(), user defined funcType is not allowed");
	}
	this.funcType = funcType;
	funcName = funcType.getName();
}

/**
 * Insert the method's description here.
 * Creation date: (12/20/2002 2:10:31 PM)
 * @param parserFunction int
 */
void setFunctionFromName(String parserToken) {
	funcType = FunctionType.USERDEFINED;
	funcName = parserToken;
	for (FunctionType ft : FunctionType.values()){
		if (ft.getName().equals(parserToken)) {
			funcType = ft;
			funcName = ft.getName();
			break;
		}
	}
}

public SymbolTableFunctionEntry getSymbolTableFunctionEntry() {
	return symbolTableFunctionEntry;
}

	public Node convertToRvachevFunction() 
	{
		ASTFuncNode newfuncNode = new ASTFuncNode();
		newfuncNode.funcType = funcType;
		newfuncNode.funcName = funcName;
		newfuncNode.symbolTableFunctionEntry = symbolTableFunctionEntry;
		for (int i = 0; i < jjtGetNumChildren(); ++ i)
		{
			newfuncNode.jjtAddChild(jjtGetChild(i).convertToRvachevFunction());
		}
		
		if (funcType == FunctionType.SQRT)
		{
			ASTRelationalNode relnode = new ASTRelationalNode();
			relnode.setOperation(ASTRelationalNode.GE);
			relnode.jjtAddChild(jjtGetChild(0));
			relnode.jjtAddChild(new ASTFloatNode(0.0));
			ASTMultNode multinode = new ASTMultNode();
			multinode.jjtAddChild(relnode);
			multinode.jjtAddChild(newfuncNode);
			return multinode;
		} 
		else 
		{
			return newfuncNode;	
		}
	}
}

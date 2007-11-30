package cbit.vcell.parser;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/* JJT: 0.2.2 */
import java.util.Hashtable;

import cbit.util.TokenMangler;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.simdata.ExternalDataIdentifier;
import net.sourceforge.interval.ia_math.*;

public class ASTFuncNode extends SimpleNode {
	
	private static final int BOTH_NEIGHBORS = 0;
	private static final int NO_NEIGHBORS = 3;
	private static final int USE_ADJACENT_PLUS = 1;
	private static final int USE_ADJACENT = 2;
 	
 	private int funcType = -1;

	public final static int EXP = 0;
	public final static int SQRT = 1;
	public final static int ABS = 2;
	public final static int POW = 3;
	public final static int LOG = 4;
	public final static int SIN = 5;
	public final static int COS = 6;
	public final static int TAN = 7;
	public final static int ASIN = 8;
	public final static int ACOS = 9;
	public final static int ATAN = 10;
	public final static int ATAN2 = 11;
	public final static int MAX = 12;
	public final static int MIN = 13;
	public final static int CEIL = 14;
	public final static int FLOOR = 15;
	public final static int CSC = 16;
	public final static int	COT = 17;
	public final static int	SEC = 18;
	public final static int	ACSC = 19;
	public final static int	ACOT = 20;
	public final static int	ASEC = 21;
	public final static int	SINH = 22;
	public final static int	COSH = 23;
	public final static int	TANH = 24;
	public final static int	CSCH = 25;
	public final static int	COTH = 26;
	public final static int SECH = 27;
	public final static int	ASINH = 28;
	public final static int	ACOSH = 29;
	public final static int ATANH = 30;
	public final static int	ACSCH = 31;
	public final static int	ACOTH = 32;
	public final static int	ASECH = 33;
	public final static int	FACTORIAL = 34;	
	public final static int	LOG_10 		= 35;
	public final static int	LOGBASE 	= 36;
	public final static int	FIELD 	= 37;
	public final static int	GRAD 	= 38;
	

	private final static String[] functionNamesVCML = {
		"exp",		// 0
		"sqrt",		// 1
		"abs",		// 2
		"pow",		// 3
		"log",		// 4
		"sin",		// 5
		"cos",		// 6
		"tan",		// 7
		"asin",		// 8
		"acos",		// 9
		"atan",		// 10
		"atan2",	// 11
		"max",		// 12
		"min",		// 13
		"ceil",		// 14
		"floor",	// 15
		"csc",		// 16
		"cot",		// 17
		"sec",		// 18
		"acsc",		// 19
		"acot",		// 20
		"asec",		// 21
		"sinh",		// 22
		"cosh",		// 23
		"tanh",		// 24
		"csch",		// 25
		"coth",		// 26
		"sech",		// 27
		"asinh",	// 28
		"acosh",	// 29
		"atanh",	// 30
		"acsch",	// 31
		"acoth",	// 32
		"asech",	// 33	
		"factorial",// 34
		"log10",	// 35
		"logbase", 	// 36
		"field",     // 37
		"grad"     // 38
	};
	
	private final static String[] functionNamesMathML = {
		MathMLTags.EXP,					// 0
		null,							// 1 - sqrt(a) not supported by MathML, see MathMLTags.ROOT
		MathMLTags.ABS,					// 2
		MathMLTags.POWER,				// 3
		MathMLTags.LN,					// 4
		MathMLTags.SINE,				// 5
		MathMLTags.COSINE,				// 6
		MathMLTags.TANGENT,				// 7
		MathMLTags.INV_SINE,			// 8
		MathMLTags.INV_COSINE,			// 9	
		MathMLTags.INV_TANGENT,			// 10
		null,							// 11 - atan2(a,b) not directly supported by MathML
		null,							// 12 - max(a,b) not supported by MathML
		null,							// 13 - min(a,b) not supported by MathML
		MathMLTags.CEILING,				// 14
		MathMLTags.FLOOR,				// 15
		MathMLTags.COSECANT,			// 16
		MathMLTags.COTANGENT,			// 17
		MathMLTags.SECANT,				// 18
		MathMLTags.INV_COSECANT,		// 19
		MathMLTags.INV_COTANGENT,		// 20
		MathMLTags.INV_SECANT,			// 21
		MathMLTags.HYP_SINE,			// 22
		MathMLTags.HYP_COSINE,			// 23
		MathMLTags.HYP_TANGENT,			// 24
		MathMLTags.HYP_COSECANT,		// 25
		MathMLTags.HYP_COTANGENT,		// 26
		MathMLTags.HYP_SECANT,			// 27
		MathMLTags.INV_HYP_SINE,		// 28
		MathMLTags.INV_HYP_COSINE,		// 29
		MathMLTags.INV_HYP_TANGENT,		// 30
		MathMLTags.INV_HYP_COSECANT,	// 31
		MathMLTags.INV_HYP_COTANGENT,	// 32
		MathMLTags.INV_HYP_SECANT,		// 33
		MathMLTags.FACTORIAL,			// 34
		MathMLTags.LOG_10,				// 35
		MathMLTags.LOGBASE,		 		// 36
		MathMLTags.FIELD,                // 37
		MathMLTags.GRAD                // 38
	};
	
  ASTFuncNode() {
    super(ExpressionParserTreeConstants.JJTFUNCNODE);
  }


ASTFuncNode(int id) {
	super(id);
if (id != ExpressionParserTreeConstants.JJTFUNCNODE){ System.out.println("ASTFuncNode(), id = "+id); }
}


  /** Bind method, identifiers bind themselves to ValueObjects */
public void bind(SymbolTable symbolTable) throws ExpressionBindingException {
	if (getFunction() == FIELD){
		return;
	}else if (getFunction() == GRAD){
		jjtGetChild(jjtGetNumChildren()-1).bind(symbolTable);
		return;
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
	for (int i=0;i<jjtGetNumChildren();i++){
		node.jjtAddChild(jjtGetChild(i).copyTreeBinary());
	}
	return node;	
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Expression
 * @param independentVariable java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public Node differentiate(String independentVariable) throws ExpressionException {
	switch (funcType){
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
		powNode.funcType = POW;
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
		logNode.funcType = LOG;
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
		cosNode.funcType = COS;
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
		sinNode.funcType = SIN;
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
		cosNode.funcType = COS;
		cosNode.jjtAddChild(jjtGetChild(0).copyTree());	
		
		//
		// form   pow(cos(a),2) 
		//
		ASTFuncNode powNode = new ASTFuncNode();
		powNode.funcType = POW;
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
		powNode.setFunctionFromParserToken("pow");
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));
		ASTMinusTermNode minusNode = new ASTMinusTermNode();
		minusNode.jjtAddChild(powNode);
		ASTAddNode addNode = new ASTAddNode();
		addNode.jjtAddChild(new ASTFloatNode(1.0));
		addNode.jjtAddChild(minusNode);
		ASTFuncNode sqrtNode = new ASTFuncNode();
		sqrtNode.setFunctionFromParserToken("sqrt");
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
		powNode.setFunctionFromParserToken("pow");
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));
		ASTMinusTermNode minusNode = new ASTMinusTermNode();
		minusNode.jjtAddChild(powNode);
		ASTAddNode addNode = new ASTAddNode();
		addNode.jjtAddChild(new ASTFloatNode(1.0));
		addNode.jjtAddChild(minusNode);
		ASTFuncNode sqrtNode = new ASTFuncNode();
		sqrtNode.setFunctionFromParserToken("sqrt");
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
		powNode.setFunctionFromParserToken("pow");
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
		powNode.setFunctionFromParserToken("pow");
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
		cscNode.funcType = CSC;
		cscNode.jjtAddChild(jjtGetChild(0).copyTree());
		
		// form   cot(a) 
		ASTFuncNode cotNode = new ASTFuncNode();
		cotNode.funcType = COT;
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
		cscNode.funcType = CSC;
		cscNode.jjtAddChild(jjtGetChild(0).copyTree());	
		
		// form   pow(csc(a),2) 
		ASTFuncNode powNode = new ASTFuncNode();
		powNode.funcType = POW;
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
		secNode.funcType = SEC;
		secNode.jjtAddChild(jjtGetChild(0).copyTree());	
		
		// form   tan(a) 
		ASTFuncNode tanNode = new ASTFuncNode();
		tanNode.funcType = TAN;
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
		powNode.setFunctionFromParserToken("pow");
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));

		ASTAddNode addNode = new ASTAddNode();
		addNode.jjtAddChild(powNode);
		addNode.jjtAddChild(new ASTFloatNode(-1.0));
		
		ASTFuncNode sqrtNode = new ASTFuncNode();
		sqrtNode.setFunctionFromParserToken("sqrt");
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
		powNode.setFunctionFromParserToken("pow");
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
		powNode.setFunctionFromParserToken("pow");
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));

		ASTAddNode addNode = new ASTAddNode();
		addNode.jjtAddChild(powNode);
		addNode.jjtAddChild(new ASTFloatNode(-1.0));
		
		ASTFuncNode sqrtNode = new ASTFuncNode();
		sqrtNode.setFunctionFromParserToken("sqrt");
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
		coshNode.funcType = COSH;
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
		sinhNode.funcType = SINH;
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
		sechNode.funcType = SECH;
		sechNode.jjtAddChild(jjtGetChild(0).copyTree());	
		
		// form   pow(sech(a),2) 
		ASTFuncNode powNode = new ASTFuncNode();
		powNode.funcType = POW;
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
		cschNode.funcType = COSH;
		cschNode.jjtAddChild(jjtGetChild(0).copyTree());	

		// form   coth(a) 
		ASTFuncNode cothNode = new ASTFuncNode();
		cothNode.funcType = COTH;
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
		cschNode.funcType = CSCH;
		cschNode.jjtAddChild(jjtGetChild(0).copyTree());	
		
		// form   pow(csch(a),2) 
		ASTFuncNode powNode = new ASTFuncNode();
		powNode.funcType = POW;
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
		sechNode.funcType = SECH;
		sechNode.jjtAddChild(jjtGetChild(0).copyTree());	
		
		// form   tanh(u) 
		ASTFuncNode tanhNode = new ASTFuncNode();
		tanhNode.funcType = TANH;
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
		powNode.setFunctionFromParserToken("pow");
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));
		
		ASTAddNode addNode = new ASTAddNode();
		addNode.jjtAddChild(new ASTFloatNode(1.0));
		addNode.jjtAddChild(powNode);
		
		ASTFuncNode sqrtNode = new ASTFuncNode();
		sqrtNode.setFunctionFromParserToken("sqrt");
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
		powNode.setFunctionFromParserToken("pow");
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));

		ASTAddNode addNode = new ASTAddNode(); 		// u^2 - 1
		addNode.jjtAddChild(powNode);
		addNode.jjtAddChild(new ASTFloatNode(-1.0));
		ASTFuncNode sqrtNode = new ASTFuncNode(); 	// sqrt(u^2 - 1)
		sqrtNode.setFunctionFromParserToken("sqrt");
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
		powNode.setFunctionFromParserToken("pow");
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
		powNode.setFunctionFromParserToken("pow");
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));				
		ASTAddNode addNode = new ASTAddNode();				// 1+u^2
		addNode.jjtAddChild(new ASTFloatNode(1.0));
		addNode.jjtAddChild(powNode);
		ASTFuncNode sqrtNode = new ASTFuncNode();			// sqrt(1+u^2)
		sqrtNode.setFunctionFromParserToken("sqrt");
		sqrtNode.jjtAddChild(addNode);

		// form 1 / (|u| * sqrt(1.0 + pow(u,2)))
		ASTFuncNode absNode = new ASTFuncNode();			//  |u|
		absNode.setFunctionFromParserToken("abs");
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
		powNode.setFunctionFromParserToken("pow");
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
		powNode.setFunctionFromParserToken("pow");
		powNode.jjtAddChild(jjtGetChild(0).copyTree());
		powNode.jjtAddChild(new ASTFloatNode(2.0));
		ASTMinusTermNode minusNode = new ASTMinusTermNode();// -u^2
		minusNode.jjtAddChild(powNode);
		ASTAddNode addNode = new ASTAddNode();				// (1-u^2)
		addNode.jjtAddChild(new ASTFloatNode(1.0));
		addNode.jjtAddChild(minusNode);
		ASTFuncNode sqrtNode = new ASTFuncNode();			// sqrt(1-u^2)
		sqrtNode.setFunctionFromParserToken("sqrt");
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
	case FIELD:
		if (jjtGetChild(2) instanceof ASTFloatNode) {
			return new ASTFloatNode(0.0);
		} else {
			throw new Error("derivative not defined for field data with non-constant time argument");
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
public boolean equals(Node node) throws ExpressionException {
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

	return true;
}


public double evaluateConstant() throws ExpressionException {

	double result = 0.0;
	
	switch (funcType){
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
				throw new FunctionDomainException("pow(u,v) and u<0 and v not an integer: undefined, u="+mantissa+", v="+exponent+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
			}
			if (mantissa==0.0 && exponent<0){
				throw new FunctionDomainException("pow(u,v) and u=0 and v<0 divide by zero, u="+mantissa+", v="+exponent+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
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
			throw new FunctionDomainException("log() of 0.0 is undefined, '"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"' == 0.0");
		}
		if (argument < 0.0){
			throw new FunctionDomainException("log() of a negative number is undefined, '"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"' < 0.0");
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
			throw new FunctionDomainException("sqrt(u) where u<0 is undefined: u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"' < 0.0");
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
			throw new FunctionDomainException("asin(u) and |u|>1.0 undefined, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = Math.asin(argument);
		break;
	}
	case ACOS: {
		if (jjtGetNumChildren()!=1) throw new Error("acos() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (Math.abs(argument)>1.0){
			throw new FunctionDomainException("acos(u) and |u|>1.0 undefined, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
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
		result = Math.atan2(jjtGetChild(0).evaluateConstant(),jjtGetChild(1).evaluateConstant());
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
			throw new FunctionDomainException("csc(u) & u = 0.0 undefined, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.csc(argument);
		break;
	}
	case COT: {
		if (jjtGetNumChildren()!=1) throw new Error("cot() expects 1 argument");
		result = flanagan.math.Fmath.cot(jjtGetChild(0).evaluateConstant());
		break;
	}
	case SEC: {
		if (jjtGetNumChildren()!=1) throw new Error("sec() expects 1 argument");
		result = flanagan.math.Fmath.sec(jjtGetChild(0).evaluateConstant());
		break;
	}
	case ACSC: {
		if (jjtGetNumChildren()!=1) throw new Error("acsc() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (Math.abs(argument) < 1.0){
			throw new FunctionDomainException("acsc(u) and -1<u<1 undefined, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.acsc(argument);
		break;
	}
	case ACOT: {
		if (jjtGetNumChildren()!=1) throw new Error("acot() expects 1 argument");
		result = flanagan.math.Fmath.acot(jjtGetChild(0).evaluateConstant());
		break;
	}
	case ASEC: {
		if (jjtGetNumChildren()!=1) throw new Error("asec() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (Math.abs(argument) < 1.0){
			throw new FunctionDomainException("asec(u) and -1<u<1 undefined, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.asec(argument);
		break;
	}
	case SINH: {
		if (jjtGetNumChildren()!=1) throw new Error("sinh() expects 1 argument");
		result = flanagan.math.Fmath.sinh(jjtGetChild(0).evaluateConstant());
		break;
	}
	case COSH: {
		if (jjtGetNumChildren()!=1) throw new Error("cosh() expects 1 argument");
		result = flanagan.math.Fmath.cosh(jjtGetChild(0).evaluateConstant());
		break;
	}
	case TANH: {
		if (jjtGetNumChildren()!=1) throw new Error("tanh() expects 1 argument");
		result = flanagan.math.Fmath.tanh(jjtGetChild(0).evaluateConstant());
		break;
	}
	case CSCH: {
		if (jjtGetNumChildren()!=1) throw new Error("csch() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (argument == 0.0){
			throw new FunctionDomainException("csch(u) and |u| = 0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.csch(argument);
		break;
	}
	case COTH: {
		if (jjtGetNumChildren()!=1) throw new Error("coth() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (argument == 0.0){
			throw new FunctionDomainException("coth(u) and |u| = 0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.coth(argument);
		break;
	}
	case SECH: {
		if (jjtGetNumChildren()!=1) throw new Error("sech() expects 1 argument");
		result = flanagan.math.Fmath.sech(jjtGetChild(0).evaluateConstant());
		break;
	}
	case ASINH: {
		if (jjtGetNumChildren()!=1) throw new Error("asinh() expects 1 argument");
		result = flanagan.math.Fmath.asinh(jjtGetChild(0).evaluateConstant());
		break;
	}
	case ACOSH: {
		if (jjtGetNumChildren()!=1) throw new Error("acosh() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (argument < 1.0){
			throw new FunctionDomainException("acosh(u) and u < 1.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.acosh(argument);
		break;
	}
	case ATANH: {
		if (jjtGetNumChildren()!=1) throw new Error("atanh() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (Math.abs(argument) >= 1.0){
			throw new FunctionDomainException("atanh(u) and |u| >= 1.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.atanh(argument);
		break;
	}
	case ACSCH: {
		if (jjtGetNumChildren()!=1) throw new Error("acsch() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (argument == 0.0){
			throw new FunctionDomainException("acsch(u) and |u| = 0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.acsch(argument);
		break;
	}
	case ACOTH: {
		if (jjtGetNumChildren()!=1) throw new Error("acoth() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (Math.abs(argument) <= 1.0){
			throw new FunctionDomainException("acoth(u) and |u| <= 1.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.acoth(argument);
		break;
	}
	case ASECH: {
		if (jjtGetNumChildren()!=1) throw new Error("asech() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (argument <= 0.0 || argument > 1.0){
			throw new FunctionDomainException("asech(u) and 0.0 <= u  and u > 1.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.asech(argument);
		break;
	}
	case FACTORIAL: {
		if (jjtGetNumChildren()!=1) throw new Error("factorial() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (Math.abs(argument) < 0.0){
			throw new FunctionDomainException("factorial(u) and u < 0.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.factorial(argument);
		break;
	}
	case LOG_10: {
		if (jjtGetNumChildren()!=1) throw new Error("log10() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (argument == 0.0){
			throw new FunctionDomainException("log10() of 0.0 is undefined, '"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"' == 0.0");
		}
		if (argument < 0.0){
			throw new FunctionDomainException("log10() of a negative number is undefined, '"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"' < 0.0");
		}
		result = Math.log(argument)/Math.log(10.0);
		break;
	}
	case LOGBASE: {
		if (jjtGetNumChildren()!=1) throw new Error("logbase() expects 1 argument");
		double argument = jjtGetChild(0).evaluateConstant();
		if (argument == 0.0){
			throw new FunctionDomainException("logbase() of 0.0 is undefined, '"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"' == 0.0");
		}
		if (argument < 0.0){
			throw new FunctionDomainException("logbase() of a negative number is undefined, '"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"' < 0.0");
		}
		result = 1.0/Math.log(argument);
		break;
	}
	case FIELD: {
		throw new ExpressionException("field(A, B, time) cannot be simplified to a constant");
	}
	case GRAD: {
		throw new FunctionDomainException("grad([x,y,z,m],var) is undefined for all constants");
	}
	default: {
		throw new Error("undefined function");
	}
	}
	if (Double.isInfinite(result) || Double.isNaN(result)){
		System.out.println("ASTFuncNode.evaluateConstant("+getName()+") evaluated to "+result+", exp = "+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT));
	}
	return result;
}      


public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException {

	switch (funcType){
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
	case EXP: {
		if (jjtGetNumChildren()!=1) throw new Error("exp() expects 1 argument");
		result = Math.exp(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case SQRT: {
		if (jjtGetNumChildren()!=1) throw new Error("sqrt() expects 1 arguments");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (argument<0){
			throw new FunctionDomainException("sqrt(u) where u<0 is undefined: u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"' < 0.0");
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
			throw new FunctionDomainException("pow(u,v) and u<0 and v not an integer: undefined, u="+u+", v="+v+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		if (u==0.0 && v<0){
			throw new FunctionDomainException("pow(u,v) and u=0 and v<0 divide by zero, u="+u+", v="+v+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
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
			throw new FunctionDomainException("log() of 0.0 is undefined, '"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"' == 0.0");
		}
		if (argument < 0.0){
			throw new FunctionDomainException("log() of a negative number is undefined, '"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"' < 0.0");
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
			throw new FunctionDomainException("asin(u) and |u|>1.0 undefined, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = Math.asin(argument);
		break;
	}
	case ACOS: {
		if (jjtGetNumChildren()!=1) throw new Error("acos() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (Math.abs(argument)>1.0){
			throw new FunctionDomainException("acos(u) and |u|>1.0 undefined, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
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
		result = Math.atan2(jjtGetChild(0).evaluateVector(values),jjtGetChild(1).evaluateVector(values));
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
			throw new FunctionDomainException("csc(u) & u = 0.0 undefined, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.csc(argument);
		break;
	}
	case COT: {
		if (jjtGetNumChildren()!=1) throw new Error("cot() expects 1 argument");
		result = flanagan.math.Fmath.cot(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case SEC: {
		if (jjtGetNumChildren()!=1) throw new Error("sec() expects 1 argument");
		result = flanagan.math.Fmath.sec(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case ACSC: {
		if (jjtGetNumChildren()!=1) throw new Error("acsc() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (Math.abs(argument) < 1.0){
			throw new FunctionDomainException("acsc(u) is undefined in -1<u<1, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.acsc(argument);
		break;
	}
	case ACOT: {
		if (jjtGetNumChildren()!=1) throw new Error("acot() expects 1 argument");
		result = flanagan.math.Fmath.acot(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case ASEC: {
		if (jjtGetNumChildren()!=1) throw new Error("asec() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (Math.abs(argument) < 1.0){
			throw new FunctionDomainException("asec(u) is undefined in -1<u<1, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.asec(argument);
		break;
	}
	case SINH: {
		if (jjtGetNumChildren()!=1) throw new Error("sinh() expects 1 argument");
		result = flanagan.math.Fmath.sinh(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case COSH: {
		if (jjtGetNumChildren()!=1) throw new Error("cosh() expects 1 argument");
		result = flanagan.math.Fmath.cosh(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case TANH: {
		if (jjtGetNumChildren()!=1) throw new Error("tanh() expects 1 argument");
		result = flanagan.math.Fmath.tanh(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case CSCH: {
		if (jjtGetNumChildren()!=1) throw new Error("csch() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (argument == 0.0){
			throw new FunctionDomainException("csch(u) is not defined for |u| = 0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.csch(argument);
		break;
	}
	case COTH: {
		if (jjtGetNumChildren()!=1) throw new Error("coth() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (argument == 0.0){
			throw new FunctionDomainException("coth(u) is not defined for |u| = 0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.coth(argument);
		break;
	}
	case SECH: {
		if (jjtGetNumChildren()!=1) throw new Error("sech() expects 1 argument");
		result = flanagan.math.Fmath.sech(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case ASINH: {
		if (jjtGetNumChildren()!=1) throw new Error("asinh() expects 1 argument");
		result = flanagan.math.Fmath.asinh(jjtGetChild(0).evaluateVector(values));
		break;
	}
	case ACOSH: {
		if (jjtGetNumChildren()!=1) throw new Error("acosh() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (argument < 1.0){
			throw new FunctionDomainException("acosh(u) is not defined for u < 1.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.acosh(argument);
		break;
	}
	case ATANH: {
		if (jjtGetNumChildren()!=1) throw new Error("atanh() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (Math.abs(argument) >= 1.0){
			throw new FunctionDomainException("atanh(u) is not defined in |u| >= 1.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.atanh(argument);
		break;
	}
	case ACSCH: {
		if (jjtGetNumChildren()!=1) throw new Error("acsch() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (argument == 0.0){
			throw new FunctionDomainException("acsch(u) is not defined for |u| = 0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.acsch(argument);
		break;
	}
	case ACOTH: {
		if (jjtGetNumChildren()!=1) throw new Error("acoth() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (Math.abs(argument) <= 1.0){
			throw new FunctionDomainException("acoth(u) is not defined in |u| <= 1.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.acoth(argument);
		break;
	}
	case ASECH: {
		if (jjtGetNumChildren()!=1) throw new Error("asech() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values);
		if (argument <= 0.0 || argument > 1.0){
			throw new FunctionDomainException("asech(u) is not defined in 0.0 <= u  and u > 1.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.asech(argument);
		break;
	}
	case FACTORIAL: {
		if (jjtGetNumChildren()!=1) throw new Error("factorial() expects 1 argument");
		double argument = jjtGetChild(0).evaluateVector(values); 
		if (Math.abs(argument) < 0.0){
			throw new FunctionDomainException("factorial(u) and u < 0.0, u="+argument+", expression='"+infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"'");
		}
		result = flanagan.math.Fmath.factorial(argument);
		break;
	}
	case FIELD: {
		throw new FunctionDomainException("field(A, B, time) can't be evaluated in regular way");
	}	
	case GRAD: {
		//
		//Assumes 13 blocks of double values arranged in order given below
		//p,xm,xp,ym,yp,zm,zp,xmm,xpp,ymm,ypp,zmm,zpp
		//All 13 blocks composed of [time,x,y,z] followed by data values
		//First grad argument is a code indicating the gradient function
		//"m" = magnitude(xyz), "x" = x gradient, "y" = y gradient, "z" = z gradient,
		//Second grad argument is variable to be evaluated
		//
		if(jjtGetNumChildren() != 2){
			throw new ExpressionException("grad function expetcs 2 arguments");
		}
		Node argNode = jjtGetChild(1);
		String opName = ((ASTIdNode)jjtGetChild(0)).infixString(LANGUAGE_DEFAULT, null);
		if(((values.length%Expression.GRADIENT_NUM_SPATIAL_ELEMENTS) != 0)){
			throw new ExpressionException("number of grad values is not an even multiple of "+Expression.GRADIENT_NUM_SPATIAL_ELEMENTS+"\n"+
											"current point plus 12 spatial neighbors)");
		}
		int numInternalArgs = values.length/Expression.GRADIENT_NUM_SPATIAL_ELEMENTS;
		double[] tempArgs = new double[numInternalArgs];
		result = 0;
		boolean isMagnitude = opName.equalsIgnoreCase(Expression.GRAD_MAGNITUDE);
		double[] magnitudeComponents = (isMagnitude?new double[3]:null);
		for(int i=0;i<3;i+= 1){
			int axisCode =
				(opName.equalsIgnoreCase(Expression.GRAD_X) || (isMagnitude && i==0)?cbit.vcell.geometry.Coordinate.X_AXIS:0) +
				(opName.equalsIgnoreCase(Expression.GRAD_Y) || (isMagnitude && i==1)?cbit.vcell.geometry.Coordinate.Y_AXIS:0) +
				(opName.equalsIgnoreCase(Expression.GRAD_Z) || (isMagnitude && i==2)?cbit.vcell.geometry.Coordinate.Z_AXIS:0);

			int[] gradCase = getGradCase(values,axisCode,numInternalArgs);
			//
			if(gradCase == null){
				throw new FunctionDomainException("grad(A,B) unknown case for axis code "+axisCode);
			}else if(gradCase[0] == NO_NEIGHBORS){
				// =[p]= (bounded on both sides)
				//g(x) = 0;
				result = 0;
			}else if(gradCase[0] == USE_ADJACENT){
				// =[p0][p]= or =[p][p0]= (bound on 1 side with 1 adjacent available)
				//g(x) = (u(p0) - u(p))/(p0-p)
				System.arraycopy(values,gradCase[2]*numInternalArgs,tempArgs,0,tempArgs.length);
				double eV = argNode.evaluateVector(tempArgs);
				double eP = tempArgs[1+axisCode];
				System.arraycopy(values,gradCase[1]*numInternalArgs,tempArgs,0,tempArgs.length);
				double pV = argNode.evaluateVector(tempArgs);
				double pP = tempArgs[1+axisCode];
				result = (eV-pV)/(eP-pP);
			}else if(gradCase[0] == USE_ADJACENT_PLUS){
				// ...[p1][p0][p]= or =[p][p0][p1]... (bound on 1 side only)
				//g(x) = ((-3*u(p)) + (4*u(p0)) + (-u(p1))) / (p1-p)
				System.arraycopy(values,gradCase[3]*numInternalArgs,tempArgs,0,tempArgs.length);
				double eeV = argNode.evaluateVector(tempArgs);
				double eeP = tempArgs[1+axisCode];
				System.arraycopy(values,gradCase[2]*numInternalArgs,tempArgs,0,tempArgs.length);
				double eV = argNode.evaluateVector(tempArgs);
				//double eP = tempArgs[1+axisCode];
				System.arraycopy(values,gradCase[1]*numInternalArgs,tempArgs,0,tempArgs.length);
				double pV = argNode.evaluateVector(tempArgs);
				double pP = tempArgs[1+axisCode];
				result = ((-3*pV)+(4*eV)+(-eeV))/(eeP-pP);
			}else if(gradCase[0] == BOTH_NEIGHBORS){
				// [pm][p][pp] (not bound on either side)
				//g(x) = (u(pp) - u(pm))/(pp-pm)
				System.arraycopy(values,gradCase[3]*numInternalArgs,tempArgs,0,tempArgs.length);
				double eV = argNode.evaluateVector(tempArgs);
				double eP = tempArgs[1+axisCode];
				System.arraycopy(values,gradCase[1]*numInternalArgs,tempArgs,0,tempArgs.length);
				double wV = argNode.evaluateVector(tempArgs);
				double wP = tempArgs[1+axisCode];
				result = (eV-wV)/(eP-wP);			
			}

			if(!isMagnitude){
				break;
			}else{
				magnitudeComponents[i] = result;
			}
		}
		if(isMagnitude){
			result = Math.sqrt(
						(magnitudeComponents[0] *magnitudeComponents[0]) +
						(magnitudeComponents[1] *magnitudeComponents[1]) +
						(magnitudeComponents[2] *magnitudeComponents[2])
					);
		}
		break;
	}	
	default: {
		throw new Error("undefined function");
	}
	}
	if (Double.isNaN(result) || Double.isInfinite(result)){
		System.out.println("ASTFuncNode.evaluateVector("+functionNamesVCML[funcType]+") evaluated to "+result);
	}
	return result;
}      


/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
public Node flatten() throws ExpressionException {
	
	if (funcType!=FIELD){ // skip those that can never be constant.
		try {
			double value = evaluateConstant();
			return new ASTFloatNode(value);
		}catch (Exception e){}		
	}
	ASTFuncNode funcNode = new ASTFuncNode();
	funcNode.funcType = funcType;
	java.util.Vector<Node> tempChildren = new java.util.Vector<Node>();

	for (int i=0;i<jjtGetNumChildren();i++){
		tempChildren.addElement(jjtGetChild(i).flatten());
	}

	switch (funcType){
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
			if (((ASTFuncNode)mantissaChild).funcType == POW){
				ASTMultNode newMultNode = new ASTMultNode();
				newMultNode.jjtAddChild(mantissaChild.jjtGetChild(1));
				newMultNode.jjtAddChild(exponentChild);
				ASTFuncNode newExponentNode = new ASTFuncNode();
				newExponentNode.funcType = funcType;
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
			if (((ASTFuncNode)child).funcType == POW){
				Node childPowMantissa = child.jjtGetChild(0);
				Node childPowExponent = child.jjtGetChild(1);
				if (childPowExponent instanceof ASTFloatNode && ((ASTFloatNode)childPowExponent).value.doubleValue() == 2.0){
					ASTFuncNode newAbsNode = new ASTFuncNode();
					newAbsNode.setFunction(ABS);
					newAbsNode.jjtAddChild(childPowMantissa);
					return newAbsNode.flatten();
				}
			}
		}else if (child instanceof ASTPowerNode){
			Node childPowMantissa = child.jjtGetChild(0);
			Node childPowExponent = child.jjtGetChild(1);
			if (childPowExponent instanceof ASTFloatNode && ((ASTFloatNode)childPowExponent).value.doubleValue() == 2.0){
				ASTFuncNode newAbsNode = new ASTFuncNode();
				newAbsNode.setFunction(ABS);
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
	case FIELD: {
		if (tempChildren.size()!=3) throw new ExpressionException("field() expects 3 argument");
		break;
	}
	case GRAD: {
		if (tempChildren.size()!=2) throw new ExpressionException("grad() expects 2 arguments");
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


/**
 * Insert the method's description here.
 * Creation date: (9/15/2006 1:35:48 PM)
 * @return java.util.Vector
 */
//void getFieldDataIdentifierSpecs(java.util.Vector v) {
//	if (getFunction() == ASTFuncNode.FIELD) {
//		ASTIdNode fieldname = (ASTIdNode)jjtGetChild(0);
//		ASTIdNode variablename = (ASTIdNode)jjtGetChild(1);
//		v.add(new cbit.vcell.field.FieldDataIdentifierSpec(fieldname.name, variablename.name));
//	} else {
//		super.getFieldDataIdentifierSpecs(v);		 
//	}	
//}

void getFieldFunctionArguments(java.util.Vector<FieldFunctionArguments> v) {
	if (getFunction() == ASTFuncNode.FIELD) {
		ASTIdNode fieldname = (ASTIdNode)jjtGetChild(0);
		ASTIdNode variablename = (ASTIdNode)jjtGetChild(1);
		Expression time = null;
		try {
			time = new Expression(jjtGetChild(2).infixString(LANGUAGE_DEFAULT, null));
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("Unexpected time expression for FieldData\n"+e.getMessage());
		}
		FieldFunctionArguments fieldFuncArgs =
			new FieldFunctionArguments(fieldname.name, variablename.name,time);
		if(!v.contains(fieldFuncArgs)){
			v.add(fieldFuncArgs);
		}
	} else {
		super.getFieldFunctionArguments(v);		 
	}	
}

/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @param origExp cbit.vcell.parser.Node
 * @param newExp cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public void substitute(Node origNode, Node newNode) throws ExpressionException {
	if (getFunction() == ASTFuncNode.FIELD) {
		// only substitute time argument
		if (jjtGetChild(2).equals(origNode)){
			children[2] = newNode.copyTree();
			newNode.jjtSetParent(this);
		}else{				
			jjtGetChild(2).substitute(origNode,newNode);
		}
	} else {
		super.substitute(origNode, newNode);
	}
}

boolean hasGradient(){
	if(getFunction() == GRAD){
		return true;
	}else{
		return super.hasGradient();
	}
}

void substituteFieldFunctionFieldName(Hashtable<String, ExternalDataIdentifier> substituteNamesHash) {
	if(getFunction() == FIELD){
		ASTIdNode fieldFunctionFieldNameIDNode =
			((ASTIdNode)jjtGetChild(0));
		if(substituteNamesHash.containsKey(fieldFunctionFieldNameIDNode.name)){
			String newFieldFunctionName =
				substituteNamesHash.get(fieldFunctionFieldNameIDNode.name).getName();
//			ASTIdNode newFieldFunctionFieldNameIDNode = new ASTIdNode();
//			newFieldFunctionFieldNameIDNode.name = newFieldFunctionName;
			fieldFunctionFieldNameIDNode.name = newFieldFunctionName;		
//			try{
//				substitute(fieldFunctionFieldNameIDNode, newFieldFunctionFieldNameIDNode);
//			}catch(ExpressionException e){
//				throw new RuntimeException("ASTFuncNode.substituteFieldFunctionFieldName: Error - "+e.getMessage());
//			}
		}
	}else{
		super.substituteFieldFunctionFieldName(substituteNamesHash);
	}	
}

/**
 * Insert the method's description here.
 * Creation date: (12/20/2002 2:20:27 PM)
 * @return int
 */
int getFunction() {
	return funcType;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2007 2:47:07 PM)
 */
private static int[] getGradCase(double[] args,int axisCode,int numInternalArgs) {


	final int COORD_OFFSET = 1;
	
	int axisOffset = axisCode*2;
	int wIndex = (1+0+axisOffset);
	boolean w = Double.isNaN(args[COORD_OFFSET+numInternalArgs*wIndex]);
	int eIndex = (1+1+axisOffset);
	boolean e = Double.isNaN(args[COORD_OFFSET+numInternalArgs*eIndex]);
	//
	//Both adjacent neighbors available [ok][p][ok]
	//
	if(!w && !e){
		return new int[] {BOTH_NEIGHBORS,wIndex,0,eIndex};
	}
	//
	//Neither adjacent neighbor available [block][p][block]
	//
	if(w && e){
		return new int[] {NO_NEIGHBORS,-1,-1,-1};
	}

	int wwIndex = (1+6+axisOffset);
	boolean ww = Double.isNaN(args[COORD_OFFSET+numInternalArgs*wwIndex]);
	int eeIndex = (1+7+axisOffset);
	boolean ee = Double.isNaN(args[COORD_OFFSET+numInternalArgs*eeIndex]);
	//
	//One adjacent neighbor and One adjacent-adjacent neighbor available [block][p][ok][ok] -or- [ok][ok][p][block]
	//
	if((!e && !ee)){
		return new int[] {USE_ADJACENT_PLUS,0,eIndex,eeIndex};
	}
	if((!w && !ww)){
		return new int[] {USE_ADJACENT_PLUS,0,wIndex,wwIndex};
	}
	//
	//One adjacent neighbor is available only [block][p][ok][block] -or- [block][ok][p][block]
	//
	if(!w){
		return new int[] {USE_ADJACENT,0,wIndex,-1};
	}
	if(!e){
		return new int[] {USE_ADJACENT,0,eIndex,-1};
	}		

	return null;
	
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2002 4:29:47 PM)
 * @return java.lang.String
 */
String getMathMLName() {
	return functionNamesMathML[funcType];
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2002 4:01:47 PM)
 * @return java.lang.String
 */
String getName() {
	return functionNamesVCML[funcType];
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String[]
 */
public String[] getSymbols(int language, NameScope nameScope) {
	if (getFunction() == FIELD) {
		return new String[0];
	}else if (getFunction() == GRAD){
		return jjtGetChild(jjtGetNumChildren()-1).getSymbols(language, nameScope);
	}
	return super.getSymbols(language, nameScope);
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2002 4:29:47 PM)
 * @return java.lang.String
 */
static String getVCellFunctionName(String mathMLFunctName) {
	//
	// find the MathML function name in it's array, and return the corresponding VCell function name
	//
	for (int i = 0; i < functionNamesMathML.length; i++){
		if (mathMLFunctName.equals(functionNamesMathML[i])){
			return functionNamesVCML[i];
		} 
	}
	return null;
}


public String infixString(int lang, NameScope nameScope) {
	
	StringBuffer buffer = new StringBuffer();

	switch(funcType){
		//
		// pow() is treated specially, Matlab needs a^b.
		//
	 	case POW: {		      
			if (lang == LANGUAGE_MATLAB || lang == LANGUAGE_ECLiPSe || lang == LANGUAGE_JSCL){
				buffer.append("(");
				buffer.append(jjtGetChild(0).infixString(lang,nameScope));
				buffer.append(" ^ ");
				buffer.append(jjtGetChild(1).infixString(lang,nameScope));
				buffer.append(")");
			} else  if (lang == LANGUAGE_C){
				buffer.append("pow(");
				buffer.append("((double)(" + jjtGetChild(0).infixString(lang,nameScope) + "))");
				buffer.append(",");
				buffer.append("((double)(" + jjtGetChild(1).infixString(lang,nameScope) + "))");
				buffer.append(")");
			} else if (lang == LANGUAGE_DEFAULT){
				buffer.append("pow(");
				buffer.append(jjtGetChild(0).infixString(lang,nameScope));
				buffer.append(",");
				buffer.append(jjtGetChild(1).infixString(lang,nameScope));
				buffer.append(")");
			}
			break;
		}
	 	case FIELD: {
		 	if (lang == LANGUAGE_C){
			 	return TokenMangler.getEscapedLocalFieldVariableName_C(			 			
			 				jjtGetChild(0).infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT),
			 				jjtGetChild(1).infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT),
			 				jjtGetChild(2).infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)			 				
			 			);
		 	} else {
				buffer.append(getName() + "(");
				for (int i=0;i<jjtGetNumChildren();i++){
					if (i>0) buffer.append(", ");
					buffer.append(jjtGetChild(i).infixString(lang,nameScope));
				}
				buffer.append(")");
		 	}
		 	break;
	 	}
	 	default:{
			buffer.append(getName() + "(");
			for (int i=0;i<jjtGetNumChildren();i++){
				if (i>0) buffer.append(", ");
				if (lang == LANGUAGE_C){
					buffer.append("((double)(" + jjtGetChild(i).infixString(lang,nameScope) + "))");
				} else {
					buffer.append(jjtGetChild(i).infixString(lang,nameScope));
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


/**
 * Insert the method's description here.
 * Creation date: (12/20/2002 2:18:13 PM)
 * @param function int
 */
void setFunction(int function) {
	funcType = function;
	if(getFunction() == GRAD){
		if(jjtGetNumChildren() == 2 && jjtGetChild(1) instanceof SimpleNode){
			if(((SimpleNode)jjtGetChild(1)).hasGradient()){
				throw new RuntimeException("Gradient Function nesting not implemented");
			}
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/20/2002 2:10:31 PM)
 * @param parserFunction int
 */
void setFunctionFromParserToken(String parserToken) {
	for (int i = 0; i < functionNamesVCML.length; i++){
		String definedToken = functionNamesVCML[i];
		if (definedToken.equals(parserToken)) {
			setFunction(i);
			return;
		}
	}
	throw new RuntimeException("unsupported function type '"+parserToken+"'");
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2002 4:00:52 PM)
 * @param name java.lang.String
 */
void setName(String name) {
	for (int i = 0; i < functionNamesVCML.length; i++){
		if (functionNamesVCML[i].equals(name)){
			funcType = i;
			return;
		}
	}
	throw new RuntimeException("unknown function "+name);
}
}
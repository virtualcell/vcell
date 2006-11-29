package edu.uchc.vcell.expression.internal;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/* JJT: 0.2.2 */
import net.sourceforge.interval.ia_math.IAFunctionDomainException;
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.IANarrow;
import net.sourceforge.interval.ia_math.RealInterval;

import org.vcell.expression.DerivativePolicy;
import org.vcell.expression.ExpressionBindingException;
import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionTerm;
import org.vcell.expression.FunctionDomainException;
import org.vcell.expression.NameScope;
import org.vcell.expression.SymbolTable;
import org.vcell.expression.ExpressionTerm.Operator;

import cbit.util.xml.MathMLTags;

public class ASTRelationalNode extends SimpleNode {

  private ExpressionTerm.Operator operator = null;

public ASTRelationalNode() {
	super(-1);
}      
ASTRelationalNode(int id) {
	super(id);
}      
  public void bind(SymbolTable symbolTable) throws ExpressionBindingException
  {
	  super.bind(symbolTable);
	  setInterval(new RealInterval(0.0,1.0),null);  // either true or false
  }    
  /**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public Node copyTree() {
	ASTRelationalNode node = new ASTRelationalNode();
	node.operator = this.operator;
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
	ASTRelationalNode node = new ASTRelationalNode();
	node.operator = this.operator;
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
public Node differentiate(String independentVariable, DerivativePolicy derivativePolicy) throws ExpressionException {
	return new ASTFloatNode(0.0);
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
	// check this node for same state (operator string and integer)
	//	
	ASTRelationalNode relNode = (ASTRelationalNode)node;
	if (relNode.operator != operator){
		return false;
	}	

	return true;
}
public double evaluateConstant() throws ExpressionException {
	if (jjtGetNumChildren()!=2){
		throw new ExpressionException("Expected two children");
	}
	double first = jjtGetChild(0).evaluateConstant();
	double second = jjtGetChild(1).evaluateConstant();

	switch (operator){
		case GT:{
			if (first > second) return 1.0;
			else return 0.0;
		}
		case LT:{
			if (first < second) return 1.0;
			else return 0.0;
		}
		case GE:{
			if (first >= second) return 1.0;
			else return 0.0;
		}
		case LE:{
			if (first <= second) return 1.0;
			else return 0.0;
		}
		case EQ:{
			if (first == second) return 1.0;
			else return 0.0;
		}
		case NE:{
			if (first != second) return 1.0;
			else return 0.0;
		}
	}
	throw new ExpressionException("unsupported operator");
}    
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException {
	if (jjtGetNumChildren()!=2){
		throw new ExpressionException("Expected two children");
	}
	//
	// this is a constraint (no value really is possible, exception maybe true/false).
	// the evaluation is done on both children to propagate values into identifiers.
	//
	RealInterval first = jjtGetChild(0).evaluateInterval(intervals);
	RealInterval second = jjtGetChild(1).evaluateInterval(intervals);
	RealInterval result = null;
	try {
		switch (operator){
			case GT:{
				result = IAMath.vcell_gt(first,second);
				break;
			}
			case LT:{
				result = IAMath.vcell_lt(first,second);
				break;
			}
			case GE:{
				result = IAMath.vcell_ge(first,second);
				break;
			}
			case LE:{
				result = IAMath.vcell_le(first,second);
				break;
			}
			case EQ:{
				result = IAMath.vcell_eq(first,second);
				break;
			}
			case NE:{
				result = IAMath.vcell_ne(first,second);
				break;
			}
		}
	}catch (IAFunctionDomainException e){
		e.printStackTrace(System.out);
		throw new FunctionDomainException(e.getMessage());
	}
	setInterval(result,intervals);
	return getInterval(intervals);
}    
public double evaluateVector(double values[]) throws ExpressionException {
	if (jjtGetNumChildren()!=2){
		throw new ExpressionException("Expected two children");
	}
	double first = jjtGetChild(0).evaluateVector(values);
	double second = jjtGetChild(1).evaluateVector(values);

	switch (operator){
		case GT:{
			if (first > second) return 1.0;
			else return 0.0;
		}
		case LT:{
			if (first < second) return 1.0;
			else return 0.0;
		}
		case GE:{
			if (first >= second) return 1.0;
			else return 0.0;
		}
		case LE:{
			if (first <= second) return 1.0;
			else return 0.0;
		}
		case EQ:{
			if (first == second) return 1.0;
			else return 0.0;
		}
		case NE:{
			if (first != second) return 1.0;
			else return 0.0;
		}
	}
	throw new ExpressionException("unsupported operator");
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

	ASTRelationalNode relNode = new ASTRelationalNode(id);
	relNode.setOperator(operator);
	java.util.Vector tempChildren = new java.util.Vector();

	for (int i=0;i<jjtGetNumChildren();i++){
		relNode.jjtAddChild(jjtGetChild(i).flatten());
	}

	return relNode;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/01 12:49:24 PM)
 * @return java.lang.String
 */
public String getMathMLElementTag() {
	switch (operator){
		case GT: {
			return MathMLTags.GREATER;
		}
		case LT: {
			return MathMLTags.LESS;
		}
		case LE: {
			return MathMLTags.LESS_OR_EQUAL;
		}
		case GE: {
			return MathMLTags.GREATER_OR_EQUAL;
		}
		case EQ: {
			return MathMLTags.EQUAL;
		}
		case NE: {
			return MathMLTags.NOT_EQUAL;
		}
	}
	throw new RuntimeException("unsupported operator in ASTRelationalNode ... NEVER SUPPOSED TO HAPPEN");
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/01 12:49:24 PM)
 * @return java.lang.String
 */
public ExpressionTerm.Operator getOperator() {
	return operator;
}

/**
 * This method was created by a SmartGuide.
 * @param op java.lang.String
 */
public static Operator getOperatorFromMathML(String mathML) {
	if (mathML.equals(MathMLTags.GREATER)){
		return Operator.GT;
	}else if (mathML.equals(MathMLTags.LESS)){
		return Operator.LT;
	}else if (mathML.equals(MathMLTags.GREATER_OR_EQUAL)){
		return Operator.GE;
	}else if (mathML.equals(MathMLTags.LESS_OR_EQUAL)){
		return Operator.LE;
	}else if (mathML.equals(MathMLTags.EQUAL)){
		return Operator.EQ;
	}else if (mathML.equals(MathMLTags.NOT_EQUAL)){
		return Operator.NE;
	}else{
		return null;
	}
}
  public String infixString(int lang, NameScope nameScope)
  {
	  StringBuffer buffer = new StringBuffer();
	 
	  buffer.append("(");

	  for (int i=0;i<jjtGetNumChildren();i++){
			if (i>0) {
				buffer.append(getOpString(lang));
			}
			buffer.append(jjtGetChild(i).infixString(lang, nameScope));
	  }

	  buffer.append(")");

	  return buffer.toString();
  }   
  


	public String getOpString(int lang) {
	switch (operator){
		case GT:{
			if (lang == LANGUAGE_ECLiPSe){
				return "$>";
			}else{
				return ">";
			}
		}
		case LT:{
			if (lang == LANGUAGE_ECLiPSe){
				return "$<";
			}else{
				return "<";
			}
		}
		case GE:{
			if (lang == LANGUAGE_ECLiPSe){
				return "$>=";
			}else{
				return ">=";
			}
		}
		case LE:{
			if (lang == LANGUAGE_ECLiPSe){
				return "$=<";
			}else{
				return "<=";
			}
		}
		case EQ:{
			if (lang == LANGUAGE_ECLiPSe){
				return "$=";
			}else{
				return "==";
			}
		}
		case NE:{
			if (lang == LANGUAGE_ECLiPSe){
				return "$\\=";
			}else{
				return "!=";
			}
		}
		default:{
			throw new IllegalArgumentException("unknown relational operator id = "+operator);
		}
	}
	  
  }      

/**
 * Insert the method's description here.
 * Creation date: (6/20/01 11:04:41 AM)
 * @return boolean
 */
public boolean narrow(RealInterval intervals[]) throws ExpressionBindingException{
	if (jjtGetNumChildren()!=2){
		throw new RuntimeException("Expected two children");
	}
	Node first = jjtGetChild(0);
	Node second = jjtGetChild(1);

	switch (operator){
		case GT:{
			return IANarrow.narrow_gt(getInterval(intervals),first.getInterval(intervals),second.getInterval(intervals))
					&& first.narrow(intervals)
					&& second.narrow(intervals)
					&& IANarrow.narrow_gt(getInterval(intervals),first.getInterval(intervals),second.getInterval(intervals));
		}
		case LT:{
			return IANarrow.narrow_lt(getInterval(intervals),first.getInterval(intervals),second.getInterval(intervals))
					&& first.narrow(intervals)
					&& second.narrow(intervals)
					&& IANarrow.narrow_lt(getInterval(intervals),first.getInterval(intervals),second.getInterval(intervals));
		}
		case GE:{
			return IANarrow.narrow_ge(getInterval(intervals),first.getInterval(intervals),second.getInterval(intervals))
					&& first.narrow(intervals)
					&& second.narrow(intervals)
					&& IANarrow.narrow_ge(getInterval(intervals),first.getInterval(intervals),second.getInterval(intervals));
		}
		case LE:{
			return IANarrow.narrow_le(getInterval(intervals),first.getInterval(intervals),second.getInterval(intervals))
					&& first.narrow(intervals)
					&& second.narrow(intervals)
					&& IANarrow.narrow_le(getInterval(intervals),first.getInterval(intervals),second.getInterval(intervals));
		}
		case EQ:{
			return IANarrow.narrow_eq(getInterval(intervals),first.getInterval(intervals),second.getInterval(intervals))
					&& first.narrow(intervals)
					&& second.narrow(intervals)
					&& IANarrow.narrow_eq(getInterval(intervals),first.getInterval(intervals),second.getInterval(intervals));
		}
		case NE:{
			return IANarrow.narrow_ne(getInterval(intervals),first.getInterval(intervals),second.getInterval(intervals))
					&& first.narrow(intervals)
					&& second.narrow(intervals)
					&& IANarrow.narrow_ne(getInterval(intervals),first.getInterval(intervals),second.getInterval(intervals));
		}
		default:{
			throw new RuntimeException("unsupported operator");
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2002 12:41:02 PM)
 * @param op int
 */
void setOperator(ExpressionTerm.Operator op) {
	if (op.opType!=ExpressionTerm.OperatorType.RELATIONAL){
		throw new RuntimeException("ASTRelationalNode accepts only relational operator");
	}
	this.operator = op;
}
/**
 * This method was created by a SmartGuide.
 * @param op java.lang.String
 */
public void setOperationFromToken(String op) {
	if (op.equals(">")){
		operator = ExpressionTerm.Operator.GT;
	}else if (op.equals("<")){
		operator = ExpressionTerm.Operator.LT;
	}else if (op.equals(">=")){
		operator = ExpressionTerm.Operator.GE;
	}else if (op.equals("<=")){
		operator = ExpressionTerm.Operator.LE;
	}else if (op.equals("==")){
		operator = ExpressionTerm.Operator.EQ;
	}else if (op.equals("!=")){
		operator = ExpressionTerm.Operator.NE;
	}else{
		throw new IllegalArgumentException("unknown relational operator token = '"+op+"'");
	}
}
}

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
import java.util.Vector;

import net.sourceforge.interval.ia_math.IAFunctionDomainException;
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.IANarrow;
import net.sourceforge.interval.ia_math.RealInterval;

public class ASTRelationalNode extends SimpleNode {

  public static final int GT = 1;
  public static final int LT = 2;
  public static final int LE = 3;
  public static final int GE = 4;
  public static final int EQ = 5;
  public static final int NE = 6;
  public static final int UNKNOWN = -1;

  private int operation = 0;
  String opString = "????";

public ASTRelationalNode() {
	super(-1);
}      
ASTRelationalNode(int id) {
	super(id);
}      

public boolean isBoolean() {
	  return true;
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
	node.operation = this.operation;
	node.opString = this.opString;
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
	node.operation = this.operation;
	node.opString = this.opString;
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
	return new ASTFloatNode(0.0);
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
	// check this node for same state (operation string and integer)
	//	
	ASTRelationalNode relNode = (ASTRelationalNode)node;
	if (!relNode.opString.equals(opString) || relNode.operation!=operation){
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

	switch (operation){
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
	throw new ExpressionException("unsupported operation");
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
		switch (operation){
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

	switch (operation){
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
	throw new ExpressionException("unsupported operation");
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
	relNode.setOperation(operation);

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
	switch (operation){
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
	throw new RuntimeException("unsupported operation in ASTRelationalNode ... NEVER SUPPOSED TO HAPPEN");
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/01 12:49:24 PM)
 * @return java.lang.String
 */
public String getOperation() {
	return opString;
}

public int getOperationType() {
	return operation;
}
/**
 * This method was created by a SmartGuide.
 * @param op java.lang.String
 */
public static String getOperationFromMathML(String mathML) {
	if (mathML.equals(MathMLTags.GREATER)){
		return ">";
	}else if (mathML.equals(MathMLTags.LESS)){
		return "<";
	}else if (mathML.equals(MathMLTags.GREATER_OR_EQUAL)){
		return ">=";
	}else if (mathML.equals(MathMLTags.LESS_OR_EQUAL)){
		return "<=";
	}else if (mathML.equals(MathMLTags.EQUAL)){
		return "==";
	}else if (mathML.equals(MathMLTags.NOT_EQUAL)){
		return "!=";
	}else{
		return null;
	}
}

	public String infixString(int lang)
	{
	  StringBuffer buffer = new StringBuffer();
	 
	  buffer.append("(");
	  if(lang == LANGUAGE_VISIT){
		  if(jjtGetNumChildren() != 2){
			  throw new RuntimeException(getClass().getName()+" for VISIT expecting 2 children");
		  }
			switch (operation){
				case GT:{
					buffer.append("gt(");
					break;
				}
				case LT:{
					buffer.append("lt(");
					break;
				}
				case GE:{
					buffer.append("gte(");
					break;
				}
				case LE:{
					buffer.append("lte(");
					break;
				}
				case EQ:{
					buffer.append("equal(");
					break;
				}
				case NE:{
					buffer.append("notequal(");
					break;
				}
				default:{
					throw new IllegalArgumentException("unknown relational operator id = "+operation);
				}
			}
			buffer.append(jjtGetChild(0).infixString(lang)+","+jjtGetChild(1).infixString(lang)+")");

	  }else{
		  for (int i=0;i<jjtGetNumChildren();i++){
			if (i>0) {
				String langSpecificOpString = opString;
				if (lang == LANGUAGE_ECLiPSe){
					switch (operation){
						case GT:{
							langSpecificOpString = "$>";
							break;
						}
						case LT:{
							langSpecificOpString = "$<";
							break;
						}
						case GE:{
							langSpecificOpString = "$>=";
							break;
						}
						case LE:{
							langSpecificOpString = "$=<";
							break;
						}
						case EQ:{
							langSpecificOpString = "$=";
							break;
						}
						case NE:{
							langSpecificOpString = "$\\=";
							break;
						}
						default:{
							throw new IllegalArgumentException("unknown relational operator id = "+operation);
						}
					}
					buffer.append(" "+langSpecificOpString+" ");
				}else{
					buffer.append(" "+opString+" ");
				}
			}
			buffer.append(jjtGetChild(i).infixString(lang));
		  }
	  }
	  buffer.append(")");

	  return buffer.toString();
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

	switch (operation){
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
			throw new RuntimeException("unsupported operation");
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2002 12:41:02 PM)
 * @param op int
 */
void setOperation(int op) {
	this.operation = op;
	switch (op){
		case GT:{
			opString = ">";
			break;
		}
		case LT:{
			opString = "<";
			break;
		}
		case GE:{
			opString = ">=";
			break;
		}
		case LE:{
			opString = "<=";
			break;
		}
		case EQ:{
			opString = "==";
			break;
		}
		case NE:{
			opString = "!=";
			break;
		}
		default:{
			throw new IllegalArgumentException("unknown relational operator id = "+op);
		}
	}
}
/**
 * This method was created by a SmartGuide.
 * @param op java.lang.String
 */
public void setOperationFromToken(String op) {
	if (op.equals(">")){
		operation = GT;
	}else if (op.equals("<")){
		operation = LT;
	}else if (op.equals(">=")){
		operation = GE;
	}else if (op.equals("<=")){
		operation = LE;
	}else if (op.equals("==")){
		operation = EQ;
	}else if (op.equals("!=")){
		operation = NE;
	}else{
		throw new IllegalArgumentException("unknown relational operator token = '"+op+"'");
	}
	this.opString = op;
}

public SimpleNode getRootFindingExpression() throws ExpressionException {
	SimpleNode addNode = new ASTAddNode();
	addNode.jjtAddChild(jjtGetChild(0).copyTree());
	
	SimpleNode minusNode = new ASTMinusTermNode();
	minusNode.jjtAddChild(jjtGetChild(1).copyTree());
	
	addNode.jjtAddChild(minusNode);
	
	return addNode;
}

@Override
public void getDiscontinuities(Vector<Discontinuity> v)	throws ExpressionException {
	Discontinuity od = new Discontinuity(new Expression((SimpleNode)copyTree()), new Expression(getRootFindingExpression()), operation);			  
	v.add(od);
	super.getDiscontinuities(v);
}

	public Node convertToRvachevFunction() {
	
		Node node = null;
		switch (operation)
		{
			case GT:
			case GE:
			{
				//opString = ">";
				node = new ASTAddNode();
				node.jjtAddChild(jjtGetChild(1).convertToRvachevFunction());
				ASTMinusTermNode node1 = new ASTMinusTermNode();
				node1.jjtAddChild(jjtGetChild(0).convertToRvachevFunction());		
				node.jjtAddChild(node1);
				break;
			}
			case LT:
			case LE:
			{
				//opString = "<";
				node = new ASTAddNode();
				node.jjtAddChild(jjtGetChild(0).convertToRvachevFunction());
				ASTMinusTermNode node1 = new ASTMinusTermNode();
				node1.jjtAddChild(jjtGetChild(1).convertToRvachevFunction());
				node.jjtAddChild(node1);
				break;
			}
			case EQ:
			case NE:
			{
				throw new IllegalArgumentException(operation + " is not allowed for R function");
			}	
		}
		return node;
	}

}

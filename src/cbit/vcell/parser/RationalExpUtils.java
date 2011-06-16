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
import cbit.vcell.matrix.RationalExp;
import cbit.vcell.matrix.RationalNumber;
import cbit.vcell.parser.ASTFuncNode.FunctionType;
/**
 * This class may not exist in the future, and its functionality may be spread on one or more classes.
 
 * Creation date: (3/5/2004 4:37:34 PM)
 * @author: Rashad Badrawi
 */
public class RationalExpUtils {



	public static RationalExp getRationalExp(Expression exp) throws ExpressionException {
		return getRationalExp(exp.getRootNode(), false);
	}

	public static RationalExp getRationalExp(Expression exp, boolean bAllowFunctions) throws ExpressionException {
		return getRationalExp(exp.getRootNode(), bAllowFunctions);
	}

	private static RationalExp getRationalExp(SimpleNode node, boolean bAllowFunctions) throws ExpressionException {
		
		if (node == null) {
			return null;
		}
		if (node instanceof ASTAndNode || 
			node instanceof ASTOrNode || 
			node instanceof ASTNotNode || 
			node instanceof ASTRelationalNode ||
			node instanceof DerivativeNode ||
			node instanceof ASTLaplacianNode
			) {
			throw new ExpressionException("sub-expression "+node.infixString(SimpleNode.LANGUAGE_DEFAULT)+" cannot be translated to a Rational Expression");
		} else if (node instanceof ASTFuncNode) {
			if (((ASTFuncNode)node).getFunction()==FunctionType.POW){
				try {
					double constantExponent = node.jjtGetChild(1).evaluateConstant();
					if (constantExponent != Math.floor(constantExponent)){
						throw new ExpressionException("sub-expression "+node.infixString(SimpleNode.LANGUAGE_DEFAULT)+" cannot be translated to a Rational Expression, exponent is not an integer");
					}
					int intExponent = (int)constantExponent;
					if (intExponent == 0){
						return RationalExp.ONE;
					}
					
					RationalExp base = getRationalExp((SimpleNode)node.jjtGetChild(0), bAllowFunctions);
					if (intExponent < 0){
						base = base.inverse();
						intExponent = -intExponent;
					}
					RationalExp baseUnit = new RationalExp(base);
					for (int i = 1; i < intExponent; i++){
						base = base.mult(baseUnit);
					}
					return base;
				}catch (ExpressionException e){
					throw new ExpressionException("sub-expression "+node.infixString(SimpleNode.LANGUAGE_DEFAULT)+" cannot be translated to a Rational Expression, exponent is not constant");
				}
			}else{
				if(bAllowFunctions)
				{
					return new RationalExp(((ASTFuncNode)node).infixString(SimpleNode.LANGUAGE_DEFAULT));
				}
				else
				{
					throw new ExpressionException("sub-expression "+node.infixString(SimpleNode.LANGUAGE_DEFAULT)+" cannot be translated to a Rational Expression");
				}
			}
		} else if (node instanceof ASTPowerNode) {
			try {
				double constantExponent = node.jjtGetChild(1).evaluateConstant();
				if (constantExponent != Math.floor(constantExponent)){
					throw new ExpressionException("sub-expression "+node.infixString(SimpleNode.LANGUAGE_DEFAULT)+" cannot be translated to a Rational Expression, exponent is not an integer");
				}
				int intExponent = (int)constantExponent;
				if (intExponent == 0){
					return RationalExp.ONE;
				}
				
				RationalExp base = getRationalExp((SimpleNode)node.jjtGetChild(0), bAllowFunctions);
				if (intExponent < 0){
					base = base.inverse();
					intExponent = -intExponent;
				}
				RationalExp baseUnit = new RationalExp(base);
				for (int i = 1; i < intExponent; i++){
					base = base.mult(baseUnit);
				}
				return base;
			}catch (ExpressionException e){
				throw new ExpressionException("sub-expression "+node.infixString(SimpleNode.LANGUAGE_DEFAULT)+" cannot be translated to a Rational Expression, exponent is not constant");
			}
		} else if (node instanceof ASTAddNode) {
			RationalExp exp = RationalExp.ZERO;
			for (int i = 0; i < node.jjtGetNumChildren(); i++) {
				exp = exp.add(getRationalExp((SimpleNode)node.jjtGetChild(i), bAllowFunctions));
			}
			return exp;
		} else if (node instanceof ASTMinusTermNode) {
			return getRationalExp((SimpleNode)node.jjtGetChild(0), bAllowFunctions).minus();
		} else if (node instanceof ASTMultNode) {
			if (node.jjtGetNumChildren() == 1) {
				return getRationalExp((SimpleNode)node.jjtGetChild(0), bAllowFunctions);
			}
			RationalExp exp = RationalExp.ONE;
			for (int i = 0; i < node.jjtGetNumChildren(); i++) {             
				exp = exp.mult(getRationalExp((SimpleNode)node.jjtGetChild(i), bAllowFunctions));
			}
			return exp;
		} else if (node instanceof ASTInvertTermNode) {
			SimpleNode child = (SimpleNode)node.jjtGetChild(0);
			return getRationalExp(child, bAllowFunctions).inverse();
		} else if (node instanceof ASTFloatNode) {          //return TBD instead of dimensionless.
			RationalNumber r = RationalNumber.getApproximateFraction(((ASTFloatNode)node).value.doubleValue());
			return new RationalExp(r);
		//} else if (node instanceof ASTFuncNode) {   
			//String functionName = ((ASTFuncNode)node).getName();
			//if (functionName.equalsIgnoreCase("pow")) {       
				//SimpleNode child0 =  (SimpleNode)node.jjtGetChild(0);
				//SimpleNode child1 =  (SimpleNode)node.jjtGetChild(1);
				//VCUnitDefinition unit0 = getRationalExp(child0);
				//VCUnitDefinition unit1 = getRationalExp(child1);
				//if (!unit1.compareEqual(VCUnitDefinition.UNIT_DIMENSIONLESS) && !unit1.compareEqual(VCUnitDefinition.UNIT_TBD)){
					//throw new VCUnitException("exponent of '"+node.infixString(SimpleNode.LANGUAGE_DEFAULT,SimpleNode.NAMESCOPE_DEFAULT)+"' has units of "+unit0);
				//}
				//if (unit0.compareEqual(VCUnitDefinition.UNIT_DIMENSIONLESS) || unit0.isTBD()){
					//return unit0;
				//}
				//try {
					//double d = ((SimpleNode)node.jjtGetChild(1)).evaluateConstant();
					//RationalNumber rn = RationalNumber.getApproximateFraction(d);
					//return unit0.raiseTo(rn);
				//}catch(ExpressionException e){
					//return VCUnitDefinition.UNIT_TBD;  // ????? don't know the unit now
				//}
			//}
		//} else if (node instanceof ASTPowerNode) {
			//SimpleNode child0 =  (SimpleNode)node.jjtGetChild(0);
			//SimpleNode child1 =  (SimpleNode)node.jjtGetChild(1);
			//VCUnitDefinition unit0 = getRationalExp(child0);
			//VCUnitDefinition unit1 = getRationalExp(child1);
			//if (!unit1.compareEqual(VCUnitDefinition.UNIT_DIMENSIONLESS) && !unit1.compareEqual(VCUnitDefinition.UNIT_TBD)){
				//throw new VCUnitException("exponent of '"+node.infixString(SimpleNode.LANGUAGE_DEFAULT,SimpleNode.NAMESCOPE_DEFAULT)+"' has units of "+unit0);
			//}
			//if (unit0.compareEqual(VCUnitDefinition.UNIT_DIMENSIONLESS)){
				//return VCUnitDefinition.UNIT_DIMENSIONLESS;
			//}
			//boolean bConstantExponent = false;
			//double exponentValue = 1;
			//try {
				//exponentValue = child1.evaluateConstant();
				//bConstantExponent = true;
			//}catch(ExpressionException e){
				//bConstantExponent = false;
			//}
			//if (bConstantExponent){ //
				//if (unit0.isTBD()){
					//return VCUnitDefinition.UNIT_TBD;
				//}else{
					//RationalNumber rn = RationalNumber.getApproximateFraction(exponentValue);
					//return unit0.raiseTo(rn);
				//}
			//}else{
				//return VCUnitDefinition.UNIT_TBD;
			//}
		} else if (node instanceof ASTIdNode) {
			return new RationalExp(((ASTIdNode)node).name);
		} else {
			throw new ExpressionException("node type "+node.getClass().toString()+" not supported yet");
		}
	}
}

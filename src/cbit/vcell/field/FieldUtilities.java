/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.field;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.vcell.util.document.ExternalDataIdentifier;

import cbit.vcell.math.Equation;
import cbit.vcell.math.FastSystem;
import cbit.vcell.math.FieldFunctionDefinition;
import cbit.vcell.math.Function;
import cbit.vcell.math.JumpProcess;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.VarIniCondition;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VariableType;
import cbit.vcell.parser.ASTFuncNode.FunctionType;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.Expression.FunctionFilter;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionInvocation;

public class FieldUtilities {
	
	public static class FieldFunctionFilter implements FunctionFilter {
		public boolean accept(String functionName, FunctionType functionType) {
			return (functionName.equals(FieldFunctionDefinition.FUNCTION_name));
		}
	};


	public static void addFieldFuncArgsAndExpToCollection(Hashtable<FieldFunctionArguments, Vector<Expression>> fieldFuncArgsAndExpHash,Expression expression){
		if(expression == null){
			return;
		}
		FunctionFilter functionFilter = new FieldFunctionFilter();
		FunctionInvocation[] functionInvocations = expression.getFunctionInvocations(functionFilter);
		for (int i=0;i<functionInvocations.length;i++){
			Vector<Expression> expV = null;
			FieldFunctionArguments fieldFunctionArgs = new FieldFunctionArguments(functionInvocations[i]);
			if(fieldFuncArgsAndExpHash.contains(fieldFunctionArgs)){
				expV= fieldFuncArgsAndExpHash.get(fieldFunctionArgs);
			}else{
				expV = new Vector<Expression>();
				fieldFuncArgsAndExpHash.put(fieldFunctionArgs,expV);
				
			}
			if (!expV.contains(expression)){
				expV.add(expression);
			}
		}
	}

	public static FieldFunctionArguments[] getFieldFunctionArguments(Expression expression) {
		if(expression == null){
			return null;
		}
		FunctionFilter functionFilter = new FieldFunctionFilter();
		FunctionInvocation[] functionInvocations = expression.getFunctionInvocations(functionFilter);
		ArrayList<FieldFunctionArguments> fieldFuncArgsList = new ArrayList<FieldFunctionArguments>();
		for (int i=0;i<functionInvocations.length;i++){
			FieldFunctionArguments fieldFunctionArgs = new FieldFunctionArguments(functionInvocations[i]);
			if (!fieldFuncArgsList.contains(fieldFunctionArgs)){
				fieldFuncArgsList.add(fieldFunctionArgs);
			}
		}
		if (fieldFuncArgsList.size()==0){
			return null;
		}else{
			return fieldFuncArgsList.toArray(new FieldFunctionArguments[fieldFuncArgsList.size()]);
		}
	}

	private static String removeLiteralQuotes(Expression astLiteralExpression) {
		String infix = astLiteralExpression.infix();
		if (infix.startsWith("'") && infix.endsWith("'")) {
			return infix.substring(1, infix.length() - 1);
		}
		return infix;
	}
	public static void substituteFieldFuncNames(
			Hashtable<String, ExternalDataIdentifier> oldFieldFuncArgsNameNewID,
			Hashtable<FieldFunctionArguments, Vector<Expression>> fieldFuncArgsExpHash
			) throws MathException, ExpressionException{
	
		Enumeration<FieldFunctionArguments> keyEnum = fieldFuncArgsExpHash.keys();
		FunctionFilter functionFilter = new FieldFunctionFilter();
		while(keyEnum.hasMoreElements()){
			Vector<Expression> value = fieldFuncArgsExpHash.get(keyEnum.nextElement());
			for(int i=0;i<value.size();i++){
				Expression exp = value.elementAt(i);
				FunctionInvocation[] functionInvocations = exp.getFunctionInvocations(functionFilter);
				for (int j = 0; j < functionInvocations.length; j++) {
					Expression[] arguments = functionInvocations[j].getArguments();
					String oldFieldName = removeLiteralQuotes(arguments[0]);
					if(oldFieldFuncArgsNameNewID.containsKey(oldFieldName)){
						String varName = removeLiteralQuotes(arguments[1]);
						Expression timeExp = arguments[2];
						VariableType varType = VariableType.UNKNOWN;
						if (arguments.length>3){
							String vt = removeLiteralQuotes(arguments[3]);
							varType = VariableType.getVariableTypeFromVariableTypeName(vt);
						}
						String newFieldName = oldFieldFuncArgsNameNewID.get(oldFieldName).getName();
						FieldFunctionArguments newFieldFunctionArguments = new FieldFunctionArguments(newFieldName, varName, timeExp, varType);
						Expression newFunctionExp = new Expression(newFieldFunctionArguments.infix());
						exp.substituteInPlace(functionInvocations[j].getFunctionExpression(), newFunctionExp);
					}
				}
			}
		}
	}

	public static void substituteFieldFuncNames(MathDescription mathDesc, Hashtable<String, ExternalDataIdentifier> oldFieldFuncArgsNameNewID) throws MathException, ExpressionException{
		FieldUtilities.substituteFieldFuncNames(
				oldFieldFuncArgsNameNewID, collectFieldFuncAndExpressions(mathDesc));
	}

	public static FieldFunctionArguments[] getFieldFunctionArguments(MathDescription mathDesc) throws MathException, ExpressionException {
		return collectFieldFuncAndExpressions(mathDesc).keySet().toArray(new FieldFunctionArguments[0]);
	}

	private static Hashtable<FieldFunctionArguments, Vector<Expression>> collectFieldFuncAndExpressions(MathDescription mathDesc) throws MathException, ExpressionException {
		// make sure each field only added once
		Hashtable<FieldFunctionArguments, Vector<Expression>> fieldFuncArgsExpHash =
			new Hashtable<FieldFunctionArguments, Vector<Expression>>();
		Enumeration<SubDomain> enum1 = mathDesc.getSubDomains();
		
		Enumeration<Variable> mathDescEnumeration = mathDesc.getVariables();
		while (mathDescEnumeration.hasMoreElements()){
			Variable variable = mathDescEnumeration.nextElement();
			if(variable instanceof Function){
				Function function = (Function)variable;
				FieldUtilities.addFieldFuncArgsAndExpToCollection(fieldFuncArgsExpHash,function.getExpression());
			}
		}
		// go through each subdomain
		while (enum1.hasMoreElements()){
			SubDomain subDomain = enum1.nextElement();
			// go through each equation
			Enumeration<Equation> enum_equ = subDomain.getEquations();
			while (enum_equ.hasMoreElements()){
				Equation equation = (Equation)enum_equ.nextElement();
				Vector<Expression> exs = equation.getExpressions(mathDesc);

				// go through each expresson
				for (int i = 0; i < exs.size(); i ++) {
					Expression exp = (Expression)exs.get(i);
					FieldUtilities.addFieldFuncArgsAndExpToCollection(fieldFuncArgsExpHash,exp);
				}
			}
			// go through each Fast System
			FastSystem fastSystem = subDomain.getFastSystem();
			if(fastSystem != null){
				Expression[] fsExprArr = fastSystem.getExpressions();
				for (int i = 0; i < fsExprArr.length; i ++) {
					FieldUtilities.addFieldFuncArgsAndExpToCollection(fieldFuncArgsExpHash,fsExprArr[i]);
				}
			}
			// go through each Jump Process
			for (JumpProcess jumpProcess : subDomain.getJumpProcesses()){
				Expression[] jpExprArr = jumpProcess.getExpressions();
				for (int i = 0; i < jpExprArr.length; i ++) {
					FieldUtilities.addFieldFuncArgsAndExpToCollection(fieldFuncArgsExpHash,jpExprArr[i]);
				}
			}
			// go through VarInitConditions
			for (VarIniCondition varInitCond : subDomain.getVarIniConditions()){
				Expression[] vicExprArr =
					new Expression[] {varInitCond.getIniVal(),varInitCond.getVar().getExpression()};
				for (int i = 0; i < vicExprArr.length; i ++) {
					FieldUtilities.addFieldFuncArgsAndExpToCollection(fieldFuncArgsExpHash,vicExprArr[i]);
				}
			}
		}
		
		return fieldFuncArgsExpHash;
	}


}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * This class was generated by a SmartGuide.
 * 
 */
@SuppressWarnings("serial")
public class VolumeRegionEquation extends Equation {
	private final static Logger lg = LogManager.getLogger(VolumeRegionEquation.class);

	private Expression uniformRateExpression = new Expression(0.0);
	private Expression volumeRateExpression = new Expression(0.0);

public VolumeRegionEquation(VolumeRegionVariable var, Expression initialExp) {
	super(var, initialExp, null);
}


public boolean compareEqual(Matchable object) {
	VolumeRegionEquation equ = null;
	if (!(object instanceof VolumeRegionEquation)){
		return false;
	}else{
		equ = (VolumeRegionEquation)object;
	}
	if (!compareEqual0(equ)){
		return false;
	}
	if (!Compare.isEqualOrNull(volumeRateExpression,equ.volumeRateExpression)){
		return false;
	}
	if (!Compare.isEqualOrNull(uniformRateExpression,equ.uniformRateExpression)){
		return false;
	}
	return true;
}


void flatten(MathSymbolTable simSymbolTable, boolean bRoundCoefficients) throws cbit.vcell.parser.ExpressionException, MathException {
	super.flatten0(simSymbolTable,bRoundCoefficients);
	
	volumeRateExpression = getFlattenedExpression(simSymbolTable,volumeRateExpression,bRoundCoefficients);
	uniformRateExpression = getFlattenedExpression(simSymbolTable,uniformRateExpression,bRoundCoefficients);
}


/**
 * This method was created by a SmartGuide.
 * @return java.util.Vector
 */
public Vector<Expression> getExpressions(MathDescription mathDesc){
	Vector<Expression> list = new Vector<Expression>();
	list.addElement(getVolumeRateExpression());
	list.addElement(getUniformRateExpression());
	
	if (getRateExpression()!=null)		list.addElement(getRateExpression());
	if (getInitialExpression()!=null)	list.addElement(getInitialExpression());
	if (getExactSolution()!=null)		list.addElement(getExactSolution());
	
	//
	// get Parent Subdomain
	//
	SubDomain parentSubDomain = null;
	Enumeration<SubDomain> enum1 = mathDesc.getSubDomains();
	while (enum1.hasMoreElements()){
		SubDomain subDomain = enum1.nextElement();
		if (subDomain.getEquation(getVariable()) == this){
			parentSubDomain = subDomain;
			break;
		}
	}
	
	try {
		MembraneSubDomain membranes[] = mathDesc.getMembraneSubDomains((CompartmentSubDomain)parentSubDomain);
		for (int i = 0; membranes!=null && i < membranes.length; i++){
			JumpCondition jump = membranes[i].getJumpCondition(getVariable());
			if (jump != null) {
				if (membranes[i].getInsideCompartment()==parentSubDomain){
					list.addElement(jump.getInFluxExpression());
				}else{
					list.addElement(jump.getOutFluxExpression());
				}
			}
		}
	}catch (Exception e){
		lg.error(e.getMessage(), e);
	}
	
	return list;
}

public Enumeration<Expression> getTotalExpressions() throws ExpressionException {
	Vector<Expression> vector = new Vector<Expression>();
	Expression lvalueExp = new Expression("VolumeRate_"+getVariable().getName());
	Expression rvalueExp = new Expression(getVolumeRateExpression());
	Expression totalExp = Expression.assign(lvalueExp,rvalueExp);
	totalExp.bindExpression(null);
	totalExp.flatten();
	vector.addElement(totalExp);
	vector.addElement(getTotalInitialExpression());
	Expression solutionExp = getTotalSolutionExpression();
	if (solutionExp!=null){
		vector.addElement(solutionExp);
	}	
	return vector.elements();
}


public Expression getUniformRateExpression() {
	return uniformRateExpression;
}


public String getVCML() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("\t"+VCML.VolumeRegionEquation+" "+getVariable().getName()+" {\n");
	if (getUniformRateExpression() != null){
		buffer.append("\t\t"+VCML.UniformRate+" "+getUniformRateExpression().infix()+";\n");
	}else{
		buffer.append("\t\t"+VCML.UniformRate+" "+"0.0;\n");
	}
	if (getVolumeRateExpression() != null){
		buffer.append("\t\t"+VCML.VolumeRate+" "+getVolumeRateExpression().infix()+";\n");
	}else{
		buffer.append("\t\t"+VCML.VolumeRate+" "+"0.0;\n");
	}
	if (initialExp != null){
		buffer.append("\t\t"+VCML.Initial+"\t "+initialExp.infix()+";\n");
	}
	switch (solutionType){
		case UNKNOWN_SOLUTION:{
			if (initialExp == null){
				buffer.append("\t\t"+VCML.Initial+"\t "+"0.0;\n");
			}
			break;
		}
		case EXACT_SOLUTION:{
			buffer.append("\t\t"+VCML.Exact+" "+exactExp.infix()+";\n");
			break;
		}
	}				
		
	buffer.append("\t}\n");
	return buffer.toString();		
}


/**
 * Insert the method's description here.
 * Creation date: (7/9/01 2:05:09 PM)
 * @return cbit.vcell.parser.Expression
 */
public Expression getVolumeRateExpression() {
	return volumeRateExpression;
}


/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public void read(CommentStringTokenizer tokens, MathDescription mathDesc) throws MathFormatException, ExpressionException {
	String token = null;
	token = tokens.nextToken();
	if (!token.equalsIgnoreCase(VCML.BeginBlock)){
		throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
	}			
	while (tokens.hasMoreTokens()){
		token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.EndBlock)){
			break;
		}			
		if (token.equalsIgnoreCase(VCML.Initial)){
			initialExp = MathFunctionDefinitions.fixFunctionSyntax(tokens);
			continue;
		}
		if (token.equalsIgnoreCase(VCML.VolumeRate)){
			Expression exp = MathFunctionDefinitions.fixFunctionSyntax(tokens);
			setVolumeRateExpression(exp);
			continue;
		}
		if (token.equalsIgnoreCase(VCML.UniformRate)){
			Expression exp = MathFunctionDefinitions.fixFunctionSyntax(tokens);
			setUniformRateExpression(exp);
			continue;
		}
		if (token.equalsIgnoreCase(VCML.Exact)){
			exactExp = MathFunctionDefinitions.fixFunctionSyntax(tokens);
			solutionType = EXACT_SOLUTION;
			continue;
		}
		throw new MathFormatException("unexpected identifier "+token);
	}	
		
}

public void setUniformRateExpression(Expression newUniformRateExpression) {
	uniformRateExpression = newUniformRateExpression;
}


public void setVolumeRateExpression(Expression newVolumeRateExpression) {
	volumeRateExpression = newVolumeRateExpression;
}


@Override
public void checkValid(MathDescription mathDesc, SubDomain subDomain) throws MathException, ExpressionException {
	checkInitialCondition(mathDesc);
	ArrayList<Expression> expList = new ArrayList<Expression>();
	expList.add(getVolumeRateExpression());
	expList.add(getUniformRateExpression());
	expList.add(getRateExpression());
	expList.add(getExactSolution());
	checkValid_Volume(mathDesc, expList, (CompartmentSubDomain)subDomain);
	
	// jump condition can have membrane variable in it
	MembraneSubDomain membranes[] = mathDesc.getMembraneSubDomains((CompartmentSubDomain)subDomain);
	if (membranes!=null) {
		for (int i = 0; i < membranes.length; i++){
			JumpCondition jump = membranes[i].getJumpCondition(getVariable());
			if (jump != null) {
				jump.checkValid(mathDesc, membranes[i]);
			}
		}
	}
}
}

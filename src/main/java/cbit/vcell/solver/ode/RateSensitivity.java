/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.event.ChangeListener;

import cbit.vcell.math.Constant;
import cbit.vcell.math.Equation;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * The RateSensitivity class represents the parameter sensitivities of the rates of the nonlinear system C'(t) = f(C,t).
 * For simplicity, only ODE's are considered within a single subDomain.
 * 
 */
public class RateSensitivity implements ChangeListener {
	private MathDescription mathDesc = null;
	private SubDomain subDomain = null;
	private boolean bMathDirty = true;
	//
	// variables are only Volume Variables with ODE's
	//
	private Constant consts[] = null;
	private Expression rates[] = null;
	private Variable vars[] = null;
	private int numRates = 0;
	private int numConstants = 0;
	
	//
	//   d Ci'
	//   ---- = CPexp[i+numConsts*j] 
	//   d Pj
	//
	private Expression CPexp[] = null;
/**
 * This method was created by a SmartGuide.
 * @param mathDesc cbit.vcell.math.MathDescription
 */
public RateSensitivity (MathDescription AmathDesc, SubDomain AsubDomain) {
	this.mathDesc = AmathDesc;
	this.subDomain = AsubDomain;
	this.mathDesc.addChangeListener(this);
}
/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
private void create() {
	CPexp = new Expression[numConstants*numRates];
/*
	for (int rateIndex=0;rateIndex<numRates;rateIndex++){
		for (int constantIndex=0;constantIndex<numConstants;constantIndex++){
			Expression rateExp = rates[rateIndex];
			Constant constant = consts[constantIndex];
			rateExp.bindExpression(mathDesc);
			Expression diff = rateExp.differentiate(constant.getName());
			diff.bindExpression(null);
			diff = diff.flatten();
			setCP(rateIndex,constantIndex,diff);
			diff.bindExpression(mathDesc);
		}
	}
*/
}

/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Expression
 * @param rateIndex int
 * @param parameterIndex int
 * @exception java.lang.Exception The exception description.
 */
private Expression getCPexp(int rateIndex, int parameterIndex) throws MathException, ExpressionException, ArrayIndexOutOfBoundsException {
	refresh();
	if ((rateIndex<0) || (rateIndex>=numRates)){
		throw new ArrayIndexOutOfBoundsException("rateIndex out of range '"+rateIndex+"' from 0 to "+numRates);
	}	
	if ((parameterIndex<0) || (parameterIndex>=numConstants)){
		throw new ArrayIndexOutOfBoundsException("parameterIndex out of range '"+parameterIndex+"'");
	}	
	Expression exp = CPexp[rateIndex+parameterIndex*numRates];
	if (exp == null){
		makeCP(rateIndex,parameterIndex);
		return CPexp[rateIndex+parameterIndex*numRates];
	}else{
		return exp;
	}		
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Expression
 * @param rateIndex int
 * @param parameterIndex int
 * @exception java.lang.Exception The exception description.
 */
public Expression getCPexp(VolVariable variable, String parameterName) throws MathException, ExpressionException, ArrayIndexOutOfBoundsException {
	return getCPexp(getRateIndex(variable),getParameterIndexFromConstant(parameterName));
}
/**
 * This method was created by a SmartGuide.
 * @return int
 * @param constant cbit.vcell.math.Constant
 */
public int getParameterIndexFromConstant(String constantName) throws MathException {
	refresh();
	for (int i=0;i<consts.length;i++){
		if (consts[i].getName().equals(constantName)){
			return i;
		}
	}
	return -1;
}
/**
 * This method was created by a SmartGuide.
 * @return int
 * @param constant cbit.vcell.math.Constant
 */
public int getRateIndex(VolVariable var) throws MathException {
	refresh();
	for (int i=0;i<vars.length;i++){
		if (vars[i] == var){
			return i;
		}
	}
	return -1;
}
/**
 * This method was created by a SmartGuide.
 * @return java.util.Enumeration
 * @exception java.lang.Exception The exception description.
 */
public Enumeration getTotalExpressionsFromConstant(String constantName) throws Exception {
	refresh();
	Vector vector = new Vector();
	int constantIndex = getParameterIndexFromConstant(constantName);
	for (int rateIndex=0;rateIndex<numRates;rateIndex++){
		Expression CP = new Expression(getCPexp(rateIndex, constantIndex));
		Expression lhs = Expression.derivative(constantName,new Expression(vars[rateIndex].getName()+"_rate;"));
		Expression exp = Expression.assign(lhs,CP);
		vector.addElement(exp);
	}	
	return vector.elements();
}
/**
 * This method was created by a SmartGuide.
 * @param rateIndex int
 * @param varIndex int
 * @param exp cbit.vcell.parser.Expression
 * @exception java.lang.Exception The exception description.
 */
private void makeCP(int rateIndex, int constantIndex) throws ExpressionException, ArrayIndexOutOfBoundsException {
	if (rateIndex<0 || rateIndex>=numRates){
		throw new ArrayIndexOutOfBoundsException("rateIndex out of range '"+rateIndex+"'");
	}	
	if (constantIndex<0 || constantIndex>=numConstants){
		throw new ArrayIndexOutOfBoundsException("constantIndex out of range '"+constantIndex+"'");
	}	
	Expression rateExp = rates[rateIndex];
	Constant constant = consts[constantIndex];
	rateExp.bindExpression(mathDesc);
	Expression diff = rateExp.differentiate(constant.getName());
	diff.bindExpression(null);
	diff = diff.flatten();
	diff.bindExpression(mathDesc);

	CPexp[rateIndex+constantIndex*numRates] = diff;
}
/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
private void parseMathDesc() throws MathException {
	Vector equationList = new Vector();
	Enumeration enum1 = subDomain.getEquations();
	while (enum1.hasMoreElements()){
		Equation equ = (Equation)enum1.nextElement();
		if (equ instanceof OdeEquation){
			equationList.addElement(equ);
		}else{
			throw new MathException("encountered non-ode equation, unsupported");
		}		
	}	
	Vector constantList = new Vector();
	enum1 = mathDesc.getVariables();
	while (enum1.hasMoreElements()){
		Variable var = (Variable)enum1.nextElement();
		if (var instanceof Constant){
			constantList.addElement(var);
		}		
	}
	numConstants = constantList.size();
	numRates = equationList.size();
	rates = new Expression[numRates];
	vars = new Variable[numRates];
	consts = new Constant[numConstants];
	for (int i=0;i<numRates;i++){
		OdeEquation odeEqu = (OdeEquation)equationList.elementAt(i);
		rates[i] = odeEqu.getRateExpression();
		vars[i] = odeEqu.getVariable();
	}	
	for (int i=0;i<numConstants;i++){
		consts[i] = (Constant)constantList.elementAt(i);
	}	
}
/**
 * This method was created in VisualAge.
 */
private void refresh() throws MathException {
	if (bMathDirty){
		parseMathDesc();
		create();
		bMathDirty = false;
	}
}
/**
 * This method was created in VisualAge.
 */
public void refreshDependencies() {
	mathDesc.removeChangeListener(this);
	mathDesc.addChangeListener(this);
}
/**
 * This method was created by a SmartGuide.
 */
public void show() throws MathException, ExpressionException {
	refresh();
	for (int rateIndex=0;rateIndex<numRates;rateIndex++){
		for (int constantIndex=0;constantIndex<numConstants;constantIndex++){
			Expression rateExp = rates[rateIndex];
			Constant constant = consts[constantIndex];
			/* Expression sens = CPexp[rateIndex+constantIndex*numRates];
			if (sens == null){
				continue;
			}*/	
			Expression sens = getCPexp(rateIndex,constantIndex);
			System.out.println("RateSensitivity of "+vars[rateIndex].getName()+"' wrt "+consts[constantIndex].getName()+" is "+sens);
		}
	}
}
	/**
	 * Invoked when the target of the listener has changed its state.
	 *
	 * @param e  a ChangeEvent object
	 */
public void stateChanged(javax.swing.event.ChangeEvent e) {
	if (e.getSource() instanceof MathDescription) {
		bMathDirty = true;
	}
}
}

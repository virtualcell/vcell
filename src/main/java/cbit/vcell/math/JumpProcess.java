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
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

import java.util.*;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;

public class JumpProcess implements org.vcell.util.Matchable,java.io.Serializable {
	private String processName=null;
	private Expression  probabilityRate=null;
	private ArrayList<Action> actions = null;

/**
 * JumpProcess constructor comment.
 * @param var cbit.vcell.math.Variable
 * @param initialExp cbit.vcell.parser.Expression
 * @param rateExp cbit.vcell.parser.Expression
 */
public JumpProcess(String name, cbit.vcell.parser.Expression probRate)
{
	processName = name;
	probabilityRate = probRate;
	actions = new ArrayList<Action>();
}


/**
 * Append a new action to the end of the action list if the variable in the action is not in the list
 * if the action is already existed (e.g the species is both reactant and product), the operand should be updated.
 * Creation date: (6/21/2006 5:13:17 PM)
 */
public void addAction(Action newAction) throws MathException
{
	Action action= getAction(newAction.getVar().getName());
	
	if( action != null && action.getOperation().equals(action.ACTION_INC) && newAction.getOperation().equals(Action.ACTION_INC))
	{
		Expression orgOperand = action.getOperand();
		Expression addOperand = newAction.getOperand();
		Expression newOperand = null;
		try
		{
			newOperand = Expression.add(orgOperand,addOperand);
			newOperand = newOperand.flatten();
			actions.remove(action);
			actions.add(Action.createIncrementAction(newAction.getVar(), newOperand));
		} catch(cbit.vcell.parser.ExpressionException ex) {
			ex.printStackTrace(System.out);
			throw new MathException(ex.getMessage());
		}
	}
	else
	{
		actions.add(newAction);
	}
}


/**
 * Compare two Jump processes.
 * @return boolean
 * @param object java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable object) 
{
	if (object == null) {
		return false;
	}
	if (!(object instanceof JumpProcess)) {
		return false;
	}
	
	JumpProcess jumpProc = (JumpProcess) object;
	if(processName.compareTo(jumpProc.processName) != 0 ) return false;//processName
	if(!probabilityRate.compareEqual(jumpProc.probabilityRate)) return false; //probabilityRate
	//actions
	if((actions != null) && (jumpProc.actions != null))
	{
		Action actions1[] = (Action[]) actions.toArray(new Action[0]);
		Action actions2[] = (Action[]) jumpProc.actions.toArray(new Action[0]);

		if(!Compare.isEqualOrNull(actions1,actions2))
		{
			return false;
		}
	}
	else return false;
			
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (10/2/2006 5:20:53 PM)
 * @return double
 * @param names java.lang.String[]
 * @param values double[]
 */
public double evaluateProbabilityRate(String[] names, double[] values) 
{
	double result = 0;
	try
	{
		cbit.vcell.parser.SymbolTable symTable= new cbit.vcell.parser.SimpleSymbolTable(names);
		getProbabilityRate().bindExpression(symTable);
		result = getProbabilityRate().evaluateVector(values);
	} catch (cbit.vcell.parser.ExpressionException e) {e.printStackTrace();}

	return result;	
}


/**
 * Get an action from the list by it's index.
 * Creation date: (6/27/2006 10:10:41 AM)
 * @return cbit.vcell.math.Action
 * @param index int
 */
public Action getAction(int index) 
{
	if(index<actions.size())
 		return (Action)actions.get(index);
 	return null;
}


/**
 * Get an action from action list by it's variable name.
 * Creation date: (6/27/2006 10:12:16 AM)
 * @return cbit.vcell.math.Action
 * @param actionName java.lang.String
 */
public Action getAction(String varName) //again the problem here, do we allow same variables applear in actionlist more than once.
{
	for(int i=0; i<actions.size(); i++)
	{
		if(((Action)actions.get(i)).getVar().getName().compareTo(varName)==0)
			return (Action)actions.get(i);
	}
	return null;
}


/**
 * Return the reference of the action list.
 * Creation date: (6/27/2006 3:02:29 PM)
 * @return java.util.Vector
 */
public ArrayList<Action> getActions() {
	return actions;
}


/**
 * Getthe process name.
 * Creation date: (6/21/2006 5:31:11 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return processName;
}


/**
 * Get probability expression.
 * Creation date: (6/21/2006 5:31:11 PM)
 * @return cbit.vcell.parser.Expression
 */
public cbit.vcell.parser.Expression getProbabilityRate() {
	return probabilityRate;
}


/**
 * Write the instance of the class to VCML.
 * @return java.lang.String
 */
public String getVCML()
{
	StringBuffer buffer = new StringBuffer();
	// the jump process will be written inside compartment brackets, therefore a "\t" is needed
	buffer.append("\t"+VCML.JumpProcess+"\t"+getName()+" "+VCML.BeginBlock+"\n");
	buffer.append("\t\t"+VCML.ProbabilityRate+"\t"+getProbabilityRate().infix()+";\n");
	for(int i=0; i<getActions().size(); i++)
	{
		buffer.append(((Action)getActions().get(i)).getVCML());
	}
	buffer.append("\t"+" "+VCML.EndBlock+"\n");
	return buffer.toString();	
}

public Expression[] getExpressions()
{
	Vector<Expression> expV = new Vector<Expression>();
	expV.add(getProbabilityRate());
	for(int i=0; i<getActions().size(); i++)
	{
		expV.add(getAction(i).getOperand());
	}
	Expression[] expArr = new Expression[expV.size()];
	expV.copyInto(expArr);
	return expArr;	
}


/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public void read(CommentStringTokenizer tokens) throws Exception {}


/**
 * Remove the action from the action list by it's index
 * Creation date: (6/21/2006 5:14:47 PM)
 */
public void removeAction(int index)
{
	if(index<actions.size())
		actions.remove(index);
}

/**
 * Setthe process's name.
 * Creation date: (6/21/2006 5:32:45 PM)
 * @param newProcessName java.lang.String
 */
public void setName(java.lang.String newProcessName) {
	processName = newProcessName;
}


/**
 * Assignment the probabilityRate to a new expression.
 * Creation date: (6/21/2006 5:32:45 PM)
 * @param newProbabilityRate cbit.vcell.parser.Expression
 */
public void setProbabilityRate(cbit.vcell.parser.Expression newProbabilityRate) {
	probabilityRate = newProbabilityRate;
}


/**
 * Insert the method's description here.
 * Creation date: (9/28/2006 5:15:46 PM)
 * @return java.lang.String
 */
public String toString() {
	StringBuffer buffer = new StringBuffer();
	// the jump process will be written inside compartment brackets, therefore a "\t" is needed
	buffer.append(VCML.JumpProcess+"_"+getName());
	
	return buffer.toString();
}


public void flatten(MathSymbolTable mathSymbolTable, boolean bRoundCoefficients) throws ExpressionException, MathException {
	//
	// replace fastRates with flattended and substituted fastRates
	//
	for (int i = 0; i < actions.size(); i++) {
		Action action = actions.get(i);
		Expression oldExp = action.getOperand();
		actions.set(i,new Action(action.getVar(),action.getOperation(),Equation.getFlattenedExpression(mathSymbolTable,oldExp,bRoundCoefficients)));
	}
	
	//
	// replace fastInvariants with flattended and substituted fastInvariants
	//
	probabilityRate = Equation.getFlattenedExpression(mathSymbolTable,probabilityRate,bRoundCoefficients);
}
}

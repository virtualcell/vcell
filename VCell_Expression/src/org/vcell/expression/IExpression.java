package org.vcell.expression;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

import cbit.util.Matchable;

import net.sourceforge.interval.ia_math.RealInterval;

public interface IExpression extends Matchable, Serializable {

	public abstract void bindExpression(SymbolTable symbolTable)
			throws ExpressionBindingException;

	/**
	 * This method was created by a SmartGuide.
	 * @return cbit.vcell.model.Expression
	 * @param variable String
	 * @exception java.lang.Exception The exception description.
	 */
	public abstract IExpression differentiate(String variable)
			throws ExpressionException;

	/**
	 * This method was created by a SmartGuide.
	 * @return double
	 * @exception java.lang.Exception The exception description.
	 */
	public abstract double evaluateConstant() throws ExpressionException,
			DivideByZeroException;

	public abstract RealInterval evaluateInterval(RealInterval intervals[])
			throws ExpressionException, DivideByZeroException;

	public abstract double evaluateVector(double values[])
			throws ExpressionException, DivideByZeroException;

	/**
	 * Insert the method's description here.
	 * Creation date: (1/23/2003 7:05:26 PM)
	 * @return cbit.vcell.parser.Expression[]
	 */
	public abstract ExpressionTerm extractTopLevelTerm();

	/**
	 * This method was created by a SmartGuide.
	 */
	public abstract IExpression flatten() throws ExpressionException;

	/**
	 * This method was created by a SmartGuide.
	 */
	public abstract IExpression getBinaryExpression();
	
	public abstract String getMathML() throws ExpressionException, IOException;
	/**
	 * This method was created by a SmartGuide.
	 * @return cbit.vcell.parser.SymbolTableEntry
	 * @param symbolName java.lang.String
	 */
	public abstract SymbolTableEntry getSymbolBinding(String symbol);

	/**
	 * This method was created by a SmartGuide.
	 * @return cbit.vcell.model.String[]
	 * @exception java.lang.Exception The exception description.
	 */
	public abstract String[] getSymbols();

	/**
	 * This method was created by a SmartGuide.
	 * @return cbit.vcell.model.String[]
	 * @exception java.lang.Exception The exception description.
	 */
	public abstract String[] getSymbols(int language, NameScope nameScope);

	/**
	 * This method was created by a SmartGuide.
	 * @return cbit.vcell.model.String[]
	 * @exception java.lang.Exception The exception description.
	 */
	public abstract String[] getSymbols(NameScope nameScope);

	/**
	 * This method was created by a SmartGuide.
	 * @return cbit.vcell.model.String[]
	 * @exception java.lang.Exception The exception description.
	 */
	public abstract Iterator getSymbolsIterator();

	/**
	 * This method was created by a SmartGuide.
	 * @return cbit.vcell.model.String[]
	 * @exception java.lang.Exception The exception description.
	 */
	public abstract boolean hasSymbol(String symbolName);

	public abstract String infix();

	public abstract String infix(NameScope nameScope);

	public abstract String infix_C();

	public abstract String infix_C(NameScope nameScope);

	public abstract String infix_ECLiPSe();

	public abstract String infix_ECLiPSe(NameScope nameScope);

	public abstract String infix_JSCL();

	public abstract String infix_JSCL(NameScope nameScope);

	public abstract String infix_Matlab();

	public abstract String infix_Matlab(NameScope nameScope);

	/**
	 * Insert the method's description here.
	 * Creation date: (1/23/2003 7:19:57 PM)
	 * @return boolean
	 */
	public abstract boolean isAtomic();

	/**
	 * Insert the method's description here.
	 * Creation date: (1/23/2003 7:19:57 PM)
	 * @return boolean
	 */
	public abstract boolean isLogical();

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2005 10:43:33 AM)
	 * @return boolean
	 */
	public abstract boolean isNumeric();

	/**
	 * Insert the method's description here.
	 * Creation date: (1/23/2003 7:19:57 PM)
	 * @return boolean
	 */
	public abstract boolean isRelational();

	/**
	 * Insert the method's description here.
	 * Creation date: (5/7/2002 2:37:46 PM)
	 * @return boolean
	 */
	public abstract boolean isZero();

	public abstract boolean narrow(RealInterval intervals[])
			throws ExpressionException;

	public abstract void printTree();

	/**
	 * Insert the method's description here.
	 * Creation date: (10/11/2002 8:48:29 AM)
	 */
	public abstract void roundToFloat();

	/**
	 * This method was created by a SmartGuide.
	 * @return cbit.vcell.parser.Expression
	 * @param origExp cbit.vcell.parser.Expression
	 * @param newExp cbit.vcell.parser.Expression
	 * @exception java.lang.Exception The exception description.
	 */
	public abstract void substituteInPlace(IExpression origExp, IExpression newExp)
			throws ExpressionException;

}
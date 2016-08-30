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

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import org.vcell.util.BeanUtils;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;

public class FastSystem implements MathObject, Serializable, Matchable {
	protected MathDescription mathDesc = null;
	protected Vector<FastInvariant> fastInvariantList = new Vector<FastInvariant>();
	protected Vector<FastRate> fastRateList = new Vector<FastRate>();
	/**
 * FastSystem constructor comment.
 */
public FastSystem(MathDescription mathDesc) {
	super();
	this.mathDesc = mathDesc;
}
/**
 * @param fastInvariant cbit.vcell.math.FastInvariant
 */
public void addFastInvariant(FastInvariant fastInvariant) throws MathException {
	if (fastInvariantList.contains(fastInvariant)){
		throw new MathException("fastInvariant "+fastInvariant+" already exists");
	}
	fastInvariantList.addElement(fastInvariant);
}
/**
 * @param fastInvariant cbit.vcell.math.FastRate
 */
public void addFastRate(FastRate fastRate) throws MathException {
	if (fastRateList.contains(fastRate)){
		throw new MathException("fastRate "+fastRate+" already exists");
	}
	fastRateList.addElement(fastRate);
}
/**
 * @return boolean
 * @param object java.lang.Object
 */
public boolean compareEqual(Matchable object) {
	FastSystem fs = null;
	if (object == null){
		return false;
	}
	if (!(object instanceof FastSystem)){
		return false;
	}else{
		fs = (FastSystem)object;
	}
	BoundFunction mArray1 [] = null;
	BoundFunction mArray2 [] = null;
	if (fastInvariantList.size() > 0) {
		mArray1 = (BoundFunction [])fastInvariantList.toArray(new BoundFunction[fastInvariantList.size()]);
	}
	if (fs.fastInvariantList.size() > 0) {
		mArray2 = (BoundFunction [])fs.fastInvariantList.toArray(new BoundFunction[fs.fastInvariantList.size()]);
	}
	if (!Compare.isEqualOrNull(mArray1, mArray2))
		return false;

	mArray1 = null;
	mArray2 = null;
	if (fastRateList.size() > 0) {
		mArray1 = (BoundFunction [])fastRateList.toArray(new BoundFunction[fastRateList.size()]);
	}
	if (fs.fastRateList.size() > 0) {
		mArray2 = (BoundFunction [])fs.fastRateList.toArray(new BoundFunction[fs.fastRateList.size()]);
	}
	if (!Compare.isEqualOrNull(mArray1, mArray2))
		return false;
		
	return true;
}
public SubDomain getSubDomain(){
	Enumeration<SubDomain> enumSubDomain = mathDesc.getSubDomains();
	while (enumSubDomain.hasMoreElements()){
		SubDomain subDomain = enumSubDomain.nextElement();
		if (subDomain.getFastSystem() == this){
			return subDomain;
		}
	}
	throw new RuntimeException("couldn't find FastSystem in mathDescription");
}
/**
 * @return java.util.Enumeration
 */
public Enumeration<FastInvariant> getFastInvariants() {
	return fastInvariantList.elements();
}
/**
 * This method returns the FastRates list.
 * Creation date: (3/2/2001 5:36:48 PM)
 * @return java.util.Vector
 */
public Enumeration<FastRate> getFastRates() {
	return fastRateList.elements();
}
/**
 * @return int
 */
public int getNumFastRates() throws MathException, ExpressionException {
	return fastRateList.size();
}

public int getNumFastInvariants() throws MathException, ExpressionException {
	return fastInvariantList.size();
}
/**
 * @return java.lang.String
 */
public String getVCML() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("\t"+VCML.FastSystem+" {\n");

	Enumeration<FastInvariant> enum_fi = getFastInvariants();
	while (enum_fi.hasMoreElements()){
		FastInvariant fi = enum_fi.nextElement();
		buffer.append("\t\t"+VCML.FastInvariant+"\t"+fi.getFunction().infix()+";\n");
	}	
		
	Enumeration<FastRate> enum_fr = fastRateList.elements();
	while (enum_fr.hasMoreElements()){
		FastRate fr = enum_fr.nextElement();
		buffer.append("\t\t"+VCML.FastRate+"\t"+fr.getFunction().infix()+";\n");
	}	
		
	buffer.append("\t}\n");
	return buffer.toString();		
}
/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public void read(CommentStringTokenizer tokens, MathDescription mathDesc) throws MathException, ExpressionException {
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
		if (token.equalsIgnoreCase(VCML.FastRate)){
			Expression rate = MathFunctionDefinitions.fixFunctionSyntax(tokens);
			FastRate fr = new FastRate(rate);
			addFastRate(fr);
			continue;
		}			
		if (token.equalsIgnoreCase(VCML.FastInvariant)){
			Expression invariant = MathFunctionDefinitions.fixFunctionSyntax(tokens);
			FastInvariant fi = new FastInvariant(invariant);
			addFastInvariant(fi);
			continue;
		}			
		throw new MathFormatException("unexpected identifier "+token);
	}
}
public MathDescription getMathDesc() {
	return mathDesc;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2002 11:15:31 AM)
 * @param sim cbit.vcell.solver.Simulation
 */
void flatten(MathSymbolTable simSymbolTable, boolean bRoundCoefficients) throws ExpressionException, MathException {
	//
	// replace fastRates with flattended and substituted fastRates
	//
	for (int i = 0; i < fastRateList.size(); i++) {
		Expression oldExp = fastRateList.elementAt(i).getFunction();
		fastRateList.setElementAt(new FastRate(Equation.getFlattenedExpression(simSymbolTable,oldExp,bRoundCoefficients)),i);
	}
	
	//
	// replace fastInvariants with flattended and substituted fastInvariants
	//
	for (int i = 0; i < fastInvariantList.size(); i++) {
		Expression oldExp = fastInvariantList.elementAt(i).getFunction();
		fastInvariantList.setElementAt(new FastInvariant(Equation.getFlattenedExpression(simSymbolTable,oldExp,bRoundCoefficients)),i);
	}
}

final Enumeration<Expression> getFastRateExpressions() {
	Vector<Expression> expList = new Vector<Expression>();
	for (int i = 0; i < fastRateList.size(); i++){
		FastRate fr = (FastRate)fastRateList.elementAt(i);
		expList.add(fr.getFunction());
	}
	return expList.elements();
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2002 1:58:28 AM)
 * @return cbit.vcell.parser.Expression[]
 */
public final Expression[] getExpressions() {
	Vector<Expression> expList = new Vector<Expression>();
	for (int i = 0; i < fastInvariantList.size(); i++) {
		FastInvariant fi = fastInvariantList.elementAt(i);
		expList.add(fi.getFunction());
	}
	for (int i = 0; i < fastRateList.size(); i++) {
		FastRate fr = fastRateList.elementAt(i);
		expList.add(fr.getFunction());
	}
	
	return (Expression[])BeanUtils.getArray(expList,Expression.class);
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2002 12:46:51 PM)
 */
public void rebind() throws ExpressionBindingException {
	for (int i = 0; i < fastInvariantList.size(); i++) {
		Expression fastInvariant = fastInvariantList.elementAt(i).getFunction();
		fastInvariant.bindExpression(getMathDesc());
	}
	for (int i = 0; i < fastRateList.size(); i++) {
		Expression fastRate = fastRateList.elementAt(i).getFunction();
		fastRate.bindExpression(getMathDesc());
	}
}
final Enumeration<Expression> getFastInvariantExpressions() {
	Vector<Expression> expList = new Vector<Expression>();
	for (int i = 0; i < fastInvariantList.size(); i++){
		FastInvariant fi = fastInvariantList.elementAt(i);
		expList.add(fi.getFunction());
	}
	return expList.elements();
}
}

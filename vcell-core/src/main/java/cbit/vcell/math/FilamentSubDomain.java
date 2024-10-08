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

import java.util.Enumeration;
import java.util.List;

import cbit.vcell.parser.Expression;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.ExpressionException;
/**
 * This class was generated by a SmartGuide.
 * 
 */
public class FilamentSubDomain extends SubDomain {
	//place holder
	@Override
	protected void parse(MathDescription mathdesc, String tokenString,
			CommentStringTokenizer tokens) throws MathException,
			ExpressionException {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected String startToken() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private CompartmentSubDomain outsideCompartment = null;
/**
 * This method was created by a SmartGuide.
 * @param inside cbit.vcell.math.CompartmentSubDomain
 * @param outside cbit.vcell.math.CompartmentSubDomain
 */
public FilamentSubDomain (String name, CompartmentSubDomain outside) {
	super(name);
	this.outsideCompartment = outside;
}

	public void getAllExpressions(List<Expression> expressionList, MathDescription mathDescription){
		super.getAllExpressions0(expressionList, mathDescription);
	}

/**
 * This method was created in VisualAge.
 * @return boolean
 * @param object java.lang.Object
 */
public boolean compareEqual(Matchable object) {
	if (!super.compareEqual0(object)){
		return false;
	}
	FilamentSubDomain fsd = null;
	if (!(object instanceof FilamentSubDomain)){
		return false;
	}else{
		fsd = (FilamentSubDomain)object;
	}
	//
	// compare outsideCompartment
	//
	if (outsideCompartment==null){
		if (fsd.outsideCompartment!=null){
			return false;
		}
	}else if (!Compare.isEqual(outsideCompartment,fsd.outsideCompartment)){
		return false;
	}
	
	return true;
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.CompartmentSubDomain
 */
public CompartmentSubDomain getOutsideCompartment() {
	return outsideCompartment;
}
/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String getVCML(int spatialDimension) {
	StringBuffer buffer = new StringBuffer();
	buffer.append(VCML.FilamentSubDomain+" "+getName()+" "+outsideCompartment.getName()+" {\n");
	Enumeration enum1 = getEquations();
	while (enum1.hasMoreElements()){
		Equation equ = (Equation)enum1.nextElement();
		buffer.append(equ.getVCML());
	}	
	if (getFastSystem()!=null){
		buffer.append(getFastSystem().getVCML());
	}
	buffer.append("}\n");
	return buffer.toString();		
}
/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public void read(MathDescription mathDesc, CommentStringTokenizer tokens) throws MathException, ExpressionException {
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
		if (token.equalsIgnoreCase(VCML.OdeEquation)){
			token = tokens.nextToken();
			Variable var = mathDesc.getVariable(token);
			if (var == null){
				throw new MathFormatException("variable "+token+" not defined");
			}	
			if (!(var instanceof FilamentVariable)){
				throw new MathFormatException("variable "+token+" not a "+VCML.FilamentVariable);
			}	
			OdeEquation ode = new OdeEquation((FilamentVariable)var, null,null);
			ode.read(tokens, mathDesc);
			addEquation(ode);
			continue;
		}			
		if (token.equalsIgnoreCase(VCML.FilamentRegionEquation)){
			token = tokens.nextToken();
			Variable var = mathDesc.getVariable(token);
			if (var == null){
				throw new MathFormatException("variable "+token+" not defined");
			}	
			if (!(var instanceof FilamentRegionVariable)){
				throw new MathFormatException("variable "+token+" not a "+VCML.FilamentRegionVariable);
			}	
			FilamentRegionEquation fre = new FilamentRegionEquation((FilamentRegionVariable)var, null);
			fre.read(tokens, mathDesc);
			addEquation(fre);
			continue;
		}			
		if (token.equalsIgnoreCase(VCML.FastSystem)){
			FastSystem fs = new FastSystem(mathDesc);
			fs.read(tokens, mathDesc);
			setFastSystem(fs);
			continue;
		}			
		throw new MathFormatException("unexpected identifier "+token);
	}	
		
}
}

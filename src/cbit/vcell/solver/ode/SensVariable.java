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

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathException;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 9:00:26 PM)
 * @author: John Wagner
 */
public class SensVariable extends Variable {
	private VolVariable volVar = null;
	private Constant parameter = null;
/**
 * SensVariable constructor comment.
 * @param name java.lang.String
 */
public SensVariable(VolVariable volVariable, Constant parameter) {
	super(getSensName(volVariable,parameter),volVariable.getDomain());
	this.volVar = volVariable;
	this.parameter = parameter;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj Matchable
 */
public boolean compareEqual(Matchable obj, boolean bIgnoreMissingDomain) {
	if (!(obj instanceof SensVariable)){
		return false;
	}
	if (!compareEqual0(obj, bIgnoreMissingDomain)){
		return false;
	}
	SensVariable v = (SensVariable)obj;
	
	if (!Compare.isEqual(volVar,v.volVar)){
		return false;
	}
	if (!Compare.isEqual(parameter,v.parameter)){
		return false;
	}
	
	return true;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.Constant
 */
public Constant getParameter() {
	return parameter;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param var cbit.vcell.math.VolVariable
 * @param parm cbit.vcell.math.Constant
 */
public static String getSensName(Function function, Constant parm) {
	return "sens_"+function.getName()+"_wrt_"+parm.getName();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param var cbit.vcell.math.VolVariable
 * @param parm cbit.vcell.math.Constant
 */
public static String getSensName(VolVariable var, Constant parm) {
	return "sens_"+var.getName()+"_wrt_"+parm.getName();
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.VolVariable
 */
public VolVariable getVolVariable() {
	return volVar;
}
@Override
public String getVCML() throws MathException {
	throw new MathException("VCML not supported for " + this.getClass().getName());
}
}

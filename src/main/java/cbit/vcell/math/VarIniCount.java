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
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
/**
 * extends VarIniCondition, to distinguish iniCount from iniConcentration 
 * @author: Tracy LI
 */
public class VarIniCount extends VarIniCondition
{

public VarIniCount(Variable argVar, Expression argIniVal) {
		super(argVar, argIniVal);
		// TODO Auto-generated constructor stub
	}

/**
 * Insert the method's description here.
 * Creation date: (7/6/2006 5:42:05 PM)
 * @return java.lang.String
 */
public String getVCML() 
{
	StringBuffer buffer = new StringBuffer();
	buffer.append("\t"+VCML.VarIniCount+"\t"+getVar().getName()+"\t"+getIniVal().infix()+";\n");
	return buffer.toString();
}

@Override
public boolean compareEqual(Matchable obj) {
	if (!(obj instanceof VarIniCount)) {
		return false;
	}
	return compareEqual0((VarIniCount)obj);
}

}

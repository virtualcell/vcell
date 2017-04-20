/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;
import org.vcell.util.Compare;

import cbit.vcell.math.Variable;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTableEntry;
/**
 * Insert the type's description here.
 * Creation date: (10/11/00 4:28:07 PM)
 * @author: 
 */
public class DataSetIdentifier implements SymbolTableEntry, java.io.Serializable {
	private int symbolTableIndex = -1;
	private String name = "un-named";
	private VariableType variableType = null;
	private boolean bFunction = false;
	private Domain domain = null;

/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 10:03:57 AM)
 */
public DataSetIdentifier(String argName, VariableType argVariableType, Domain argDomain) {
	this(argName, argVariableType, argDomain, false);
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 10:04:34 AM)
 */
public DataSetIdentifier(String argName, VariableType argVariableType, Domain argDomain, boolean arg_bFunction) {
	super();
	name = argName;
	variableType = argVariableType;
	domain = argDomain;
	bFunction = arg_bFunction;
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 10:19:24 AM)
 */
public boolean equals(Object obj) {
	if (!(obj instanceof DataSetIdentifier)) {
		return false;
	}

	DataSetIdentifier dsi = (DataSetIdentifier)obj;
	if (!Compare.isEqual(dsi.name,name)) {
		return false;
	}
	if (!Compare.isEqualOrNull(dsi.domain,domain)) {
		return false;
	}
	
	return true;
}


/**
 * This method was created in VisualAge.
 * @return double
 */
public double getConstantValue() throws ExpressionException {
	throw new ExpressionException("getConstantValue() not supported for cbit.vcell.simdata.DataSetIdentifier");
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @exception java.lang.Exception The exception description.
 */
public Expression getExpression() {
	return null;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getIndex() {
	return symbolTableIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 4:46:24 PM)
 * @return java.lang.String
 */
public String getName() {
	return name;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 4:46:24 PM)
 * @return java.lang.String
 */
public Domain getDomain() {
	return domain;
}


/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 10:25:49 AM)
 * @return cbit.vcell.parser.NameScope
 */
public NameScope getNameScope() {
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (3/31/2004 12:07:46 PM)
 * @return cbit.vcell.units.VCUnitDefinition
 */
public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
	return null;
}


/**
 * This method returns the value corresponding to the symbol
 * @return Double
 * @param featureIndex int
 * @param neighborIndex int
*/
public double getValue() throws ExpressionException {
	throw new RuntimeException("getValue() not supported for cbit.vcell.simdata.DataSetIdentifier");
}


/**
 * Insert the method's description here.
 * Creation date: (7/6/01 3:01:49 PM)
 * @return cbit.vcell.simdata.VariableType
 */
public VariableType getVariableType() {
	return variableType;
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @exception java.lang.Exception The exception description.
 */
public boolean isConstant() throws ExpressionException {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2004 5:20:14 PM)
 * @return boolean
 */
public boolean isFunction() {
	return bFunction;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 4:35:41 PM)
 * @param dataIndex int
 */
public void setIndex(int argSymbolTableIndex) {
	this.symbolTableIndex = argSymbolTableIndex;
}


/**
 * Returns a String that represents the value of this object.
 * @return a string representation of the receiver
 */
public String toString() {
	return getName();
}

public String getQualifiedName(){
	if (getDomain()!=null){
		return getDomain().getName()+Variable.COMBINED_IDENTIFIER_SEPARATOR+getName();
	}else{
		return getName();
	}
}
}

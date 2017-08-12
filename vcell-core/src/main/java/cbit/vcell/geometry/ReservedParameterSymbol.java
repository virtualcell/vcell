/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTableEntry;

public class ReservedParameterSymbol implements SymbolTableEntry
{
	private String name = null;
	private int index = -1;
	
	private final static ReservedParameterSymbol U	= new ReservedParameterSymbol("u");
	private final static ReservedParameterSymbol V	= new ReservedParameterSymbol("v");
	private final static ReservedParameterSymbol W	= new ReservedParameterSymbol("w");
private ReservedParameterSymbol(String aName){
	this.name = aName;
}         
public static ReservedParameterSymbol fromString(String symbolName) {
	if (symbolName==null){
		return null;
	}else if (symbolName.equalsIgnoreCase(U.getName())){
		return U;
	}else if (symbolName.equalsIgnoreCase(V.getName())){
		return V;
	}else if (symbolName.equalsIgnoreCase(W.getName())){
		return W;
	}else{
		return null;
	}
}         
/**
 * This method was created in VisualAge.
 * @return double
 */
public double getConstantValue() throws ExpressionException {
	throw new ExpressionException(getName()+" is not constant");
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Expression
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
	return index;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 10:29:09 AM)
 * @return cbit.vcell.parser.NameScope
 */
public NameScope getNameScope() {
	return null;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.ReservedParameterSymbol
 */
public static ReservedParameterSymbol getU() {
	return U;
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/2004 2:07:23 PM)
 * @return cbit.vcell.units.VCUnitDefinition
 */
public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
	return null;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.ReservedParameterSymbol
 */
public static ReservedParameterSymbol getV() {
	return V;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.ReservedParameterSymbol
 */
public static ReservedParameterSymbol getW() {
	return W;
}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isConstant() {
	return false;
}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isU() {
	if (getName().equals(U.getName())){
		return true;
	}else{
		return false;
	}		
}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isV() {
	if (getName().equals(V.getName())){
		return true;
	}else{
		return false;
	}		
}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isW() {
	if (getName().equals(W.getName())){
		return true;
	}else{
		return false;
	}		
}
/**
 * This method was created in VisualAge.
 * @param index int
 */
public void setIndex(int index) {
	this.index = index;
}
   public String toString()
   {
	   return getName();
   }         
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.parser;

import cbit.vcell.units.VCUnitDefinition;

/**
 * A <code>SymbolTableEntry</code> describes an interface for any object that
 * can represent the value corresponding to a symbol in an expression.  
 * A <code>SymbolTableEntry</code> is returned by a <code>SymbolTable</code>
 * to resolve the reference of a symbol defined in an <code>Expression</code> 
 * or a <code>ReactionParticipant</code>
 * <p>
 */
public interface SymbolTableEntry extends Comparable<SymbolTableEntry> {

/**
 * This method was created in VisualAge.
 * @return double
 */
public double getConstantValue() throws ExpressionException;
/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @exception java.lang.Exception The exception description.
 */
public Expression getExpression();
/**
 * This method was created in VisualAge.
 * @return int
 */
int getIndex();
/**
 *
 *
 */
public String getName();    
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 10:22:46 AM)
 * @return cbit.vcell.parser.NameScope
 */
NameScope getNameScope();
default boolean hasNameScope() {
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/2004 10:33:52 AM)
 * @return cbit.vcell.units.VCUnitDefinition
 */
VCUnitDefinition getUnitDefinition();
/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @exception java.lang.Exception The exception description.
 */
public boolean isConstant() throws ExpressionException;

	@Override
	default int compareTo(SymbolTableEntry o) {
		int classCompareTo = getClass().getSimpleName().compareTo(o.getClass().getSimpleName());
		if (classCompareTo != 0){
			return classCompareTo;
		}
		String thisName = (getNameScope()!=null) ? (getNameScope().getAbsoluteScopePrefix()+"."+getName()) : getName();
		String otherName = (o.getNameScope()!=null) ? (o.getNameScope().getAbsoluteScopePrefix()+"."+o.getName()) : o.getName();
		return thisName.compareTo(otherName);
	}
}

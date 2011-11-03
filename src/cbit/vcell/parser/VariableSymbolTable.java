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

import java.util.*;
/**
 * This type was created in VisualAge.
 */
public class VariableSymbolTable implements SymbolTable {
	Vector<SymbolTableEntry> varList = new Vector<SymbolTableEntry>();
/**
 * VariableSymbolTable constructor comment.
 */
public VariableSymbolTable() {
	super();
}
/**
 * This method was created in VisualAge.
 * @param var cbit.vcell.math.Variable
 */
public void addVar(SymbolTableEntry var) {
	if (getEntry(var.getName())==null){
		varList.addElement(var);
	}
}
/**
 * getEntry method comment.
 */
public SymbolTableEntry getEntry(String identifierString) {
	for (int i=0;i<varList.size();i++){
		SymbolTableEntry var = varList.elementAt(i);
		if (var.getName().equals(identifierString)){
			return var;
		}
	}
	return null;
}
public void getEntries(Map<String, SymbolTableEntry> entryMap) {
	for (SymbolTableEntry ste : varList) {
		entryMap.put(ste.getName(), ste);
	}
}
}

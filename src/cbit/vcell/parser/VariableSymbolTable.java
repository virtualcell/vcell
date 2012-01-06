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

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
/**
 * This type was created in VisualAge.
 */
public class VariableSymbolTable implements SymbolTable {
	Map<String, SymbolTableEntry> varHash = new HashMap<String, SymbolTableEntry>();
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
	if (varHash.get(var.getName())==null){
		varHash.put(var.getName(), var);
	}
}
/**
 * getEntry method comment.
 */
public SymbolTableEntry getEntry(String identifierString) {	
	return varHash.get(identifierString);
}
public void getEntries(Map<String, SymbolTableEntry> entryMap) {
	entryMap.putAll(varHash);
}
}

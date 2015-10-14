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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cbit.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/8/2003 10:11:18 AM)
 * @author: Jim Schaff
 *
 * 
 * SimpleSymbolTable provides a simple context for expression evaluation (or other operations) that require bound expressions.
 *
 * Usage:
 *
 *   Expression exp = new Expression("a+b/c");
 *
 *   String symbols[] = exp.getSymbols();
 *   SimpleSymbolTable symbolTable = new SimpleSymbolTable(symbols);
 *   exp.bindExpression(symbolTable);
 *
 *   double values[] = new double[symbolTable.getSize()];
 *
 *   .... set values
 *
 *   double result = exp.evaluateVector(values);
 *
 *
 *   NOTE: Expression's hold their symbolTable binding within it's internal state.  So consider
 *         side-effect when binding Expressions that are externally referenced.
 */
public class SimpleSymbolTable implements ScopedSymbolTable {
	
	private SymbolTableEntry steArray[] = null;
	private NameScope nameScope = null;
	
	public static class SimpleSymbolTableFunctionEntry implements SymbolTableFunctionEntry, Serializable {
		private String funcName = null; 
		private String[] argNames = null;
		private FunctionArgType[] argTypes = null;
		private Expression expression = null;
		private NameScope nameScope = null;
		private VCUnitDefinition vcUnitDefinition = null;
		
		public SimpleSymbolTableFunctionEntry(String funcName, String[] argNames, FunctionArgType[] argTypes, Expression expression, VCUnitDefinition vcUnitDefinition, NameScope argNameScope){
			this.funcName = funcName;
			this.argNames = argNames;
			this.argTypes = argTypes;
			this.expression = expression;
			this.nameScope = argNameScope;
			this.vcUnitDefinition = vcUnitDefinition;
			if (argNames != null && argTypes != null && argNames.length!=argTypes.length){
				throw new IllegalArgumentException("length of function argument name and type arrays must be the same");
			}
		}
		
		public String[] getArgNames() {
			return argNames;
		}

		public FunctionArgType[] getArgTypes() {
			return argTypes;
		}

		public int getNumArguments() {
			return argTypes == null ? 0 : argTypes.length;
		}

		public double getConstantValue() throws ExpressionException {
			throw new ExpressionException("can't evaluate to constant");
		}

		public Expression getExpression() {
			return expression;
		}

		public int getIndex() {
			return -1;
		}

		public String getName() {
			return ASTFuncNode.getFormalDefinition(funcName, argTypes);
		}

		public NameScope getNameScope() {
			return nameScope;
		}

		public VCUnitDefinition getUnitDefinition() {
			return vcUnitDefinition;
		}

		public boolean isConstant() throws ExpressionException {
			return false;
		}

		public String getFunctionName() {
			return funcName;
		}

		public Set<String> getAllowableLiteralValues(String argumentName) {
			return new HashSet<String>();
		}
		
		public String getFunctionDeclaration() {
			StringBuffer buffer = new StringBuffer(getFunctionName());
			buffer.append("(");
			for (int i=0;i<getNumArguments();i++){
				if (i>0){
					buffer.append(",");
				}
				buffer.append(getArgNames()[i]);
			}
			buffer.append(")");
			return buffer.toString();
		}

	}
	
	public static class SimpleSymbolTableEntry implements SymbolTableEntry {
		private String name = null;
		private int index = -1;
		private NameScope nameScope = null;
		private VCUnitDefinition vcUnitDefinition = null;
		
		private SimpleSymbolTableEntry(String argName, int argIndex, NameScope argNameScope){
			this.name = argName;
			this.index = argIndex;
			this.nameScope = argNameScope;
		}
		private SimpleSymbolTableEntry(String argName, int argIndex, NameScope argNameScope, VCUnitDefinition unit){
			this.name = argName;
			this.index = argIndex;
			this.nameScope = argNameScope;
			this.vcUnitDefinition = unit;
		}
		public boolean isConstant(){
			return false;
		}
		public String getName(){
			return name;
		}
		public int getIndex(){
			return index;
		}
		public NameScope getNameScope(){
			return nameScope;
		}
		public VCUnitDefinition getUnitDefinition() {
			return vcUnitDefinition;
		}
		public Expression getExpression(){
			return null;
		}
		public double getConstantValue() throws ExpressionException {
			throw new ExpressionException("can't evaluate to constant");
		}
		public void setName(String arg_name){
			name = arg_name;
		}
	};
	
	public class DefaultNameScope implements NameScope {

		public boolean isPeer(NameScope nameScope) {
			return false;
		}		
		public boolean isAncestor(NameScope nameScope) {
			return false;
		}		
		public String getUnboundSymbolName(String unboundName) {
			return "unbound_" + unboundName;
		}		
		public String getSymbolName(SymbolTableEntry symbolTableEntry) {
			return symbolTableEntry.getName();
		}		
		public ScopedSymbolTable getScopedSymbolTable() {
			return SimpleSymbolTable.this;
		}		
		public String getRelativeScopePrefix(NameScope referenceNameScope) {
			return "";
		}		
		public NameScope getParent() {
			return null;
		}		
		public NameScope getNameScopeFromPrefix(String prefix) {
			return this;
		}		
		public String getName() {
			return "Default";
		}		
		public SymbolTableEntry getExternalEntry(String identifier, SymbolTable localSymbolTable) {
			return null;
		}		
		public void getExternalEntries(Map<String, SymbolTableEntry> entryMap) {}		
		public NameScope[] getChildren() {
			return null;
		}		
		public String getAbsoluteScopePrefix() {
			return "";
		}

		public String getPathDescription() {
			return getName();
		}
		
		@Override
		public void findReferences(SymbolTableEntry symbolTableEntry, ArrayList<SymbolTableEntry> references, HashSet<NameScope> visited) {
			throw new RuntimeException("not yet implementd");
		}
	};
	
public SimpleSymbolTable(String symbols[], SymbolTableFunctionEntry[] functionDefinitions, NameScope nameScope){
	this.nameScope = nameScope;
	ArrayList<SymbolTableEntry> stes = new ArrayList<SymbolTableEntry>();
	for (int i=0;i<symbols.length;i++){
		stes.add(new SimpleSymbolTableEntry(symbols[i],i,nameScope));
	}
	if (functionDefinitions!=null){
		for (int i=0;i<functionDefinitions.length;i++){
			stes.add(functionDefinitions[i]);
		}
	}
	steArray = stes.toArray(new SymbolTableEntry[0]);
}

public SimpleSymbolTable(String symbols[]){
	this(symbols,null);
	nameScope = new DefaultNameScope();
}


public SimpleSymbolTable(String symbols[], NameScope argNameScope){
	this(symbols, null, argNameScope);
}


public SimpleSymbolTable(String symbols[], NameScope argNameScope, VCUnitDefinition units[]){
	this.nameScope = argNameScope;
	steArray = new SimpleSymbolTableEntry[symbols.length];
	for (int i=0;i<symbols.length;i++){
		steArray[i] = new SimpleSymbolTableEntry(symbols[i],i,nameScope,units[i]);
	}
}


public SymbolTableEntry getEntry(String identifier) {
	//
	// check if in the current scope with no scoping
	//
	SymbolTableEntry ste = getLocalEntry(identifier);
	if (ste!=null){
		return ste;
	}
	if (getNameScope()!=null){
		return getNameScope().getExternalEntry(identifier,this);
	}
	return null;
}


public SymbolTableEntry getLocalEntry(String identifier) {
	//
	// check if in the current scope with no scoping
	//
	for (int i = 0; i < steArray.length; i++){
		if (steArray[i].getName().equals(identifier)){
			return steArray[i];
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (8/1/2003 10:57:10 AM)
 * @return cbit.vcell.parser.NameScope
 */
public NameScope getNameScope() {
	return nameScope;
}


/**
 * Insert the method's description here.
 * Creation date: (1/8/2003 10:18:35 AM)
 * @return int
 */
public int getSize() {
	return steArray.length;
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 12:54:26 PM)
 * @return java.lang.String
 */
public String toString() {
	return getClass().getName() + "@" + Integer.toHexString(hashCode()) + " ["+((getNameScope()!=null)?(getNameScope().getName()):("null"))+"]";
}


public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {	
	for (SymbolTableEntry ste : steArray) {
		entryMap.put(ste.getName(), ste);
	}
}


public void getEntries(Map<String, SymbolTableEntry> entryMap) {
	getNameScope().getExternalEntries(entryMap);		
}
}

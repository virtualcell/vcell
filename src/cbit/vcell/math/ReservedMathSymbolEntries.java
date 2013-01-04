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

import java.util.HashMap;
import java.util.Map;

import cbit.vcell.field.FieldFunctionDefinition;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry;

public class ReservedMathSymbolEntries {
	
	private static HashMap<String,ReservedVariable> symbolTableEntries = null;
	private static HashMap<String,SymbolTableFunctionEntry> symbolTableFunctionEntries = null;
	
	public static ReservedVariable getReservedVariableEntry(String symbolName) {
		ReservedVariable ste = getSymbolTableEntries().get(symbolName);
		return ste;
	}
	
	public static SymbolTableFunctionEntry getFunctionDefinitionEntry(String symbolName) {
		SymbolTableFunctionEntry ste = getSymbolTableFunctionEntries().get(symbolName);
		return ste;
	}
	
	public static SymbolTableEntry getEntry(String symbolName) {
		SymbolTableEntry ste = getSymbolTableEntries().get(symbolName);
		if (ste!=null){
			return ste;
		}
		SymbolTableFunctionEntry steFunction = getSymbolTableFunctionEntries().get(symbolName);
		if (steFunction!=null){
			return steFunction;
		}
		return null;
	}
	
	public static void getAll(Map<String, SymbolTableEntry> entryMap) {
		entryMap.putAll(getSymbolTableEntries());
		entryMap.putAll(getSymbolTableFunctionEntries());
	}

	private static HashMap<String,ReservedVariable> getSymbolTableEntries(){
		if (symbolTableEntries==null){
			HashMap<String,ReservedVariable> symbolTableEntries_0 = new HashMap<String, ReservedVariable>();
			symbolTableEntries_0.put(ReservedVariable.TIME.getName(),ReservedVariable.TIME);
			symbolTableEntries_0.put(ReservedVariable.X.getName(),ReservedVariable.X);
			symbolTableEntries_0.put(ReservedVariable.Y.getName(),ReservedVariable.Y);
			symbolTableEntries_0.put(ReservedVariable.Z.getName(),ReservedVariable.Z);
			symbolTableEntries = symbolTableEntries_0;
		}
		return symbolTableEntries;
	}
	
	private static HashMap<String,SymbolTableFunctionEntry> getSymbolTableFunctionEntries(){
		if (symbolTableFunctionEntries==null){
			HashMap<String,SymbolTableFunctionEntry> symbolTableFunctionEntries_0 = new HashMap<String, SymbolTableFunctionEntry>();
			symbolTableFunctionEntries_0.put(MathFunctionDefinitions.Function_regionArea_current.getName(),MathFunctionDefinitions.Function_regionArea_current);
			symbolTableFunctionEntries_0.put(MathFunctionDefinitions.Function_regionArea_indexed.getName(),MathFunctionDefinitions.Function_regionArea_indexed);
			symbolTableFunctionEntries_0.put(MathFunctionDefinitions.Function_regionVolume_current.getName(),MathFunctionDefinitions.Function_regionVolume_current);
			symbolTableFunctionEntries_0.put(MathFunctionDefinitions.Function_regionVolume_indexed.getName(),MathFunctionDefinitions.Function_regionVolume_indexed);
			symbolTableFunctionEntries_0.put(MathFunctionDefinitions.Function_convolution.getName(),MathFunctionDefinitions.Function_convolution);
			symbolTableFunctionEntries_0.put(FieldFunctionDefinition.fieldFunctionDefinition.getName(),FieldFunctionDefinition.fieldFunctionDefinition);
			symbolTableFunctionEntries_0.put(GradientFunctionDefinition.gradientFunctionDefinition.getName(),GradientFunctionDefinition.gradientFunctionDefinition);
			symbolTableFunctionEntries = symbolTableFunctionEntries_0;
		}
		return symbolTableFunctionEntries;
	}
	
	

}

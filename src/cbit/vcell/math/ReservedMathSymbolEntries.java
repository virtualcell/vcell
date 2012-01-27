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

import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry;

public class ReservedMathSymbolEntries {
	
	private static HashMap<String,ReservedVariable> symbolTableEntries = null;
	private static HashMap<String,SymbolTableFunctionEntry> mathSymbolTableFunctionEntries = null;
	private static HashMap<String,SymbolTableFunctionEntry> postProcessingSymbolTableFunctionEntries = null;
	
	public static ReservedVariable getReservedVariableEntry(String symbolName) {
		ReservedVariable ste = getSymbolTableEntries().get(symbolName);
		return ste;
	}
		
	public static SymbolTableEntry getEntry(String symbolName, boolean bIncludePostProcessing) {
		SymbolTableEntry ste = getSymbolTableEntries().get(symbolName);
		if (ste!=null){
			return ste;
		}
		SymbolTableFunctionEntry steFunction = getMathSymbolTableFunctionEntries().get(symbolName);
		if (steFunction!=null){
			return steFunction;
		}
		if (bIncludePostProcessing){
			steFunction = getPostProcessingSymbolTableFunctionEntries().get(symbolName);
			if (steFunction!=null){
				return steFunction;
			}
		}
		return null;
	}
	
	public static void getAll(Map<String, SymbolTableEntry> entryMap, boolean bIncludePostProcessing) {
		entryMap.putAll(getSymbolTableEntries());
		entryMap.putAll(getMathSymbolTableFunctionEntries());
		if (bIncludePostProcessing){
			entryMap.putAll(getPostProcessingSymbolTableFunctionEntries());
		}
	}

	private static HashMap<String,ReservedVariable> getSymbolTableEntries(){
		if (symbolTableEntries==null){
			symbolTableEntries = new HashMap<String, ReservedVariable>();
			symbolTableEntries.put(ReservedVariable.TIME.getName(),ReservedVariable.TIME);
			symbolTableEntries.put(ReservedVariable.X.getName(),ReservedVariable.X);
			symbolTableEntries.put(ReservedVariable.Y.getName(),ReservedVariable.Y);
			symbolTableEntries.put(ReservedVariable.Z.getName(),ReservedVariable.Z);
		}
		return symbolTableEntries;
	}
	
	private static HashMap<String,SymbolTableFunctionEntry> getMathSymbolTableFunctionEntries(){
		if (mathSymbolTableFunctionEntries==null){
			mathSymbolTableFunctionEntries = new HashMap<String, SymbolTableFunctionEntry>();
			mathSymbolTableFunctionEntries.put(MathFunctionDefinitions.Function_regionArea_current.getName(),MathFunctionDefinitions.Function_regionArea_current);
			mathSymbolTableFunctionEntries.put(MathFunctionDefinitions.Function_regionArea_indexed.getName(),MathFunctionDefinitions.Function_regionArea_indexed);
			mathSymbolTableFunctionEntries.put(MathFunctionDefinitions.Function_regionVolume_current.getName(),MathFunctionDefinitions.Function_regionVolume_current);
			mathSymbolTableFunctionEntries.put(MathFunctionDefinitions.Function_regionVolume_indexed.getName(),MathFunctionDefinitions.Function_regionVolume_indexed);
			mathSymbolTableFunctionEntries.put(MathFunctionDefinitions.fieldFunctionDefinition.getName(),MathFunctionDefinitions.fieldFunctionDefinition);
			mathSymbolTableFunctionEntries.put(MathFunctionDefinitions.gradientFunctionDefinition.getName(),MathFunctionDefinitions.gradientFunctionDefinition);
		}
		return mathSymbolTableFunctionEntries;
	}
	
	private static HashMap<String,SymbolTableFunctionEntry> getPostProcessingSymbolTableFunctionEntries(){
		if (postProcessingSymbolTableFunctionEntries==null){
			postProcessingSymbolTableFunctionEntries = new HashMap<String, SymbolTableFunctionEntry>();
			postProcessingSymbolTableFunctionEntries.put(MathFunctionDefinitions.convFunctionDefinition.getName(),MathFunctionDefinitions.convFunctionDefinition);
			postProcessingSymbolTableFunctionEntries.put(MathFunctionDefinitions.projectFunctionDefinition.getName(),MathFunctionDefinitions.projectFunctionDefinition);
		}
		return postProcessingSymbolTableFunctionEntries;
	}
	
	

}

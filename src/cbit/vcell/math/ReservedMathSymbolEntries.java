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
			HashMap<String,ReservedVariable> symbolTableEntries_0 = new HashMap<String, ReservedVariable>();
			symbolTableEntries_0.put(ReservedVariable.TIME.getName(),ReservedVariable.TIME);
			symbolTableEntries_0.put(ReservedVariable.X.getName(),ReservedVariable.X);
			symbolTableEntries_0.put(ReservedVariable.Y.getName(),ReservedVariable.Y);
			symbolTableEntries_0.put(ReservedVariable.Z.getName(),ReservedVariable.Z);
			symbolTableEntries = symbolTableEntries_0;
		}
		return symbolTableEntries;
	}
	
	private static HashMap<String,SymbolTableFunctionEntry> getMathSymbolTableFunctionEntries(){
		if (mathSymbolTableFunctionEntries==null){
			HashMap<String,SymbolTableFunctionEntry> mathSymbolTableFunctionEntries_0 = new HashMap<String, SymbolTableFunctionEntry>();
			mathSymbolTableFunctionEntries_0.put(MathFunctionDefinitions.Function_regionArea_current.getName(),MathFunctionDefinitions.Function_regionArea_current);
			mathSymbolTableFunctionEntries_0.put(MathFunctionDefinitions.Function_regionArea_indexed.getName(),MathFunctionDefinitions.Function_regionArea_indexed);
			mathSymbolTableFunctionEntries_0.put(MathFunctionDefinitions.Function_regionVolume_current.getName(),MathFunctionDefinitions.Function_regionVolume_current);
			mathSymbolTableFunctionEntries_0.put(MathFunctionDefinitions.Function_regionVolume_indexed.getName(),MathFunctionDefinitions.Function_regionVolume_indexed);
			mathSymbolTableFunctionEntries_0.put(MathFunctionDefinitions.fieldFunctionDefinition.getName(),MathFunctionDefinitions.fieldFunctionDefinition);
			mathSymbolTableFunctionEntries_0.put(MathFunctionDefinitions.gradientFunctionDefinition.getName(),MathFunctionDefinitions.gradientFunctionDefinition);
			mathSymbolTableFunctionEntries_0.put(MathFunctionDefinitions.Function_normalX.getName(), MathFunctionDefinitions.Function_normalX);
			mathSymbolTableFunctionEntries_0.put(MathFunctionDefinitions.Function_normalY.getName(), MathFunctionDefinitions.Function_normalY);
			mathSymbolTableFunctionEntries = mathSymbolTableFunctionEntries_0;			
		}
		return mathSymbolTableFunctionEntries;
	}
	
	private static HashMap<String,SymbolTableFunctionEntry> getPostProcessingSymbolTableFunctionEntries(){
		if (postProcessingSymbolTableFunctionEntries==null){
			HashMap<String, SymbolTableFunctionEntry> postProcessingSymbolTableFunctionEntries_0 = new HashMap<String, SymbolTableFunctionEntry>();
			postProcessingSymbolTableFunctionEntries_0.put(MathFunctionDefinitions.convFunctionDefinition.getName(),MathFunctionDefinitions.convFunctionDefinition);
			postProcessingSymbolTableFunctionEntries_0.put(MathFunctionDefinitions.projectFunctionDefinition.getName(),MathFunctionDefinitions.projectFunctionDefinition);
			postProcessingSymbolTableFunctionEntries = postProcessingSymbolTableFunctionEntries_0;
		}
		return postProcessingSymbolTableFunctionEntries;
	}
	
	

}

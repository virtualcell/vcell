/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;

import java.util.ArrayList;
import java.util.Map;

import cbit.vcell.math.*;
import org.vcell.util.BeanUtils;

import cbit.vcell.mapping.SimContextTransformer.ModelEntityMapping;
import cbit.vcell.mapping.SimContextTransformer.SimContextTransformation;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ProxyParameter;
import cbit.vcell.parser.SymbolTableEntry;

/**
 * Insert the type's description here.
 * Creation date: (5/3/2006 3:47:33 PM)
 * @author: Jim Schaff
 */
public class MathSymbolMapping implements SourceSymbolMapping {
	private java.util.HashMap<SymbolTableEntry, String> biologicalToMathSymbolNameHash = new java.util.HashMap<SymbolTableEntry, String>();
	private java.util.HashMap<SymbolTableEntry, Variable> biologicalToMathHash = new java.util.HashMap<SymbolTableEntry, Variable>();
	private java.util.HashMap<Variable, SymbolTableEntry[]> mathToBiologicalHash = new java.util.HashMap<Variable, SymbolTableEntry[]>();

/**
 * MathSymbolMapping constructor comment.
 */
public MathSymbolMapping() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (5/17/2006 12:38:53 PM)
 * @return java.lang.Object
 */
public Variable findVariableByName(String variableName) {
	
	java.util.Iterator<Variable> iter = mathToBiologicalHash.keySet().iterator();
	while (iter.hasNext()){
		Variable variable = iter.next();
		if(variable.getName().equals(variableName)){
			return variable;
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 3:49:12 PM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param var cbit.vcell.math.Variable
 */
public SymbolTableEntry[] getBiologicalSymbol(Variable var) {
	return (SymbolTableEntry[])mathToBiologicalHash.get(var);

}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 3:50:22 PM)
 * @return cbit.vcell.math.Variable
 * @param biologicalSymbol cbit.vcell.parser.SymbolTableEntry
 */
public Variable getVariable(SymbolTableEntry biologicalSymbol) {
	return (Variable)biologicalToMathHash.get(biologicalSymbol);
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 3:48:47 PM)
 * @param ste cbit.vcell.parser.SymbolTableEntry
 * @param var cbit.vcell.math.Variable
 */
public void put(SymbolTableEntry biologicalSymbol, String varName) {
	if (biologicalSymbol instanceof ProxyParameter){
		biologicalSymbol = ((ProxyParameter) biologicalSymbol).getTarget();
	}
	if(varName.endsWith(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX) || varName.endsWith(InsideVariable.INSIDE_VARIABLE_SUFFIX)){
		return;
	}
	String previousVarName = (String)biologicalToMathSymbolNameHash.get(biologicalSymbol);
	if ( (biologicalSymbol instanceof ModelParameter &&  !((ModelParameter)biologicalSymbol).getExpression().isNumeric()) ) {
		// if the biologicalSymbol is a global parameter and is not a constant, do nothing (don't check for previousVarName=varName).
		// ** the biologicalSymbol has multiple variables in the math, we will only save the last one. **
		// For all other biologicalSymbol and global parameters that are constants, go ahead as usual (else).
	} else {
		if (previousVarName != null && !varName.equals(previousVarName)){
			throw new RuntimeException("biological symbol '"+biologicalSymbol.getName()+"' mapped to two math symbols, '"+varName+"' and '"+previousVarName+"'");
		}
	}
	//System.out.println(cbit.util.BeanUtils.forceStringSize(biologicalSymbol.getName(),25," ",true)+" ---> "+varName);
	biologicalToMathSymbolNameHash.put(biologicalSymbol,varName);
	
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 4:03:51 PM)
 * @param mathDesc cbit.vcell.math.MathDescription
 */
public void reconcileVarNames(MathDescription mathDesc) {

	//
	// clear secondary hashmaps in case called multiple times.
	//
	biologicalToMathHash.clear();
	mathToBiologicalHash.clear();
	
	java.util.Set<SymbolTableEntry> keyset = biologicalToMathSymbolNameHash.keySet();
	java.util.Iterator<SymbolTableEntry> keysetIter = keyset.iterator();
	while (keysetIter.hasNext()){
		SymbolTableEntry biologicalSymbol = keysetIter.next();
		String mathVarName = (String)biologicalToMathSymbolNameHash.get(biologicalSymbol);
		Variable var = mathDesc.getVariable(mathVarName);
		if(var != null){
			biologicalToMathHash.put(biologicalSymbol,var);
			SymbolTableEntry[] previousBiologicalSymbolArr =
				(SymbolTableEntry[])mathToBiologicalHash.put(var,new SymbolTableEntry[] {biologicalSymbol});
			if(previousBiologicalSymbolArr != null){
				SymbolTableEntry[] steArr =
					(SymbolTableEntry[])BeanUtils.addElement(previousBiologicalSymbolArr,biologicalSymbol);
				mathToBiologicalHash.put(var,steArr);
			}
		}
	}
}


public void transform(SimContextTransformation transformation) {
	if (transformation == null || transformation.modelEntityMappings == null){
		return;
	}
	for (ModelEntityMapping mapping : transformation.modelEntityMappings){
		Variable var = biologicalToMathHash.remove(mapping.newModelObj);
		if (var!=null){
			biologicalToMathHash.put(mapping.origModelObj, var);
		}
		String varName = biologicalToMathSymbolNameHash.remove(mapping.newModelObj);
		if (varName!=null){
			biologicalToMathSymbolNameHash.put(mapping.origModelObj, varName);
		}
	}
	for (Map.Entry<Variable,SymbolTableEntry[]> entry : mathToBiologicalHash.entrySet()){
		Variable key = entry.getKey();
		SymbolTableEntry[] newBiologicalSymbols = entry.getValue();
		ArrayList<SymbolTableEntry> origStes = new ArrayList<SymbolTableEntry>();
		for (SymbolTableEntry newBiologicalSymbol : newBiologicalSymbols){
			// replace all array entries with those from the original model
			for (ModelEntityMapping mapping : transformation.modelEntityMappings){
				if (newBiologicalSymbol == mapping.newModelObj){
					origStes.add(mapping.origModelObj);
				}
			}
		}
		entry.setValue(origStes.toArray(new SymbolTableEntry[0]));
	}
	
}
}

package org.vcell.expression.ui;

import java.awt.datatransfer.DataFlavor;

import org.vcell.expression.IExpression;
import org.vcell.expression.SymbolTableEntry;

public class ResolvedValuesSelection{

	private SymbolTableEntry[] primarySymbolTableEntries;
	private SymbolTableEntry[] alternateSymbolTableEntries;
	private IExpression[] expressionValues;
	private String stringRepresentation;

	public ResolvedValuesSelection(
		SymbolTableEntry[] argPrimarySymbolTableEntries,
		SymbolTableEntry[] argAlternateSymbolTableEntries,
		IExpression[] argExpressionValues,String argStringRep){
			
		if(argPrimarySymbolTableEntries.length != argExpressionValues.length ||
			(argAlternateSymbolTableEntries != null && argAlternateSymbolTableEntries.length != argExpressionValues.length)){
			throw new IllegalArgumentException("ResolvedValuesSelection SymbolTableEntry array length must equal DataValues array length");
		}
		for(int i=0;i<argExpressionValues.length;i+= 1){
			if(argExpressionValues[i] == null){
				throw new IllegalArgumentException("ResolvedValuesSelection resolved value "+argPrimarySymbolTableEntries[i].getNameScope().getName()+" cannot be null.");
			}
		}
		primarySymbolTableEntries = argPrimarySymbolTableEntries;
		alternateSymbolTableEntries = argAlternateSymbolTableEntries;
		expressionValues = argExpressionValues;
		stringRepresentation = argStringRep;
	}

	public SymbolTableEntry[] getPrimarySymbolTableEntries(){
		return primarySymbolTableEntries;
	}
	public SymbolTableEntry[] getAlternateSymbolTableEntries(){
		return alternateSymbolTableEntries;
	}
	public IExpression[] getExpressionValues(){
		return expressionValues;
	}
	public String toString() {
		return stringRepresentation;
	}
}


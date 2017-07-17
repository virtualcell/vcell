/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.desktop;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.SimpleTransferable;

import cbit.vcell.model.ReactionSpeciesCopy;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;
/**
 * Insert the type's description here.
 * Creation date: (5/8/2003 2:40:40 PM)
 * @author: Frank Morgan
 */
public class VCellTransferable extends SimpleTransferable {

	public static final DataFlavor REACTION_SPECIES_ARRAY_FLAVOR = 
			new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+"; class="+ReactionSpeciesCopy.class.getName(),"ReactionSpeciesArray");
	public static final DataFlavor RESOLVED_VALUES_FLAVOR =
		new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+"; class="+ResolvedValuesSelection.class.getName(),"ResolvedValues");
	public static class ResolvedValuesSelection{
		private SymbolTableEntry[] primarySymbolTableEntries;
		private SymbolTableEntry[] alternateSymbolTableEntries;
		private Expression[] expressionValues;
		private String stringRepresentation;

		public ResolvedValuesSelection(
			SymbolTableEntry[] argPrimarySymbolTableEntries,
			SymbolTableEntry[] argAlternateSymbolTableEntries,
			Expression[] argExpressionValues,String argStringRep){
				
			if (argPrimarySymbolTableEntries.length != argExpressionValues.length ||
				(argAlternateSymbolTableEntries != null && argAlternateSymbolTableEntries.length != argExpressionValues.length)){
				throw new IllegalArgumentException("symbol array length must equal data array length");
			}
			for(int i=0;i<argExpressionValues.length;i+= 1){
				if (argExpressionValues[i] == null){
					throw new IllegalArgumentException("copied values cannot be null.");
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
		public Expression[] getExpressionValues(){
			return expressionValues;
		}
		public String toString() {
			return stringRepresentation;
		}
	}

	/**
 * Insert the method's description here.
 * Creation date: (5/8/2003 2:48:54 PM)
 */
private VCellTransferable(Object obj) {
	super(obj);
}


	/**
	 * Returns an array of DataFlavor objects indicating the flavors the data 
	 * can be provided in.  The array should be ordered according to preference
	 * for providing the data (from most richly descriptive to least descriptive).
	 * @return an array of data flavors in which this data can be transferred
	 */
public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() {
	DataFlavor flavors[] = super.getTransferDataFlavors();
	
	// add custom flavors if availlable
	if(getDataObjectClass().equals(ReactionSpeciesCopy.class)){
		flavors = (DataFlavor[]) BeanUtils.addElement(flavors,REACTION_SPECIES_ARRAY_FLAVOR);
	}

	if (getDataObjectClass().equals(VCellTransferable.ResolvedValuesSelection.class)){
		flavors = (DataFlavor[]) BeanUtils.addElement(flavors,RESOLVED_VALUES_FLAVOR);
	}
	

	return flavors;
}


/**
 * Insert the method's description here.
 * Creation date: (9/9/2004 1:17:52 PM)
 * @return boolean
 * @param dataFlavor java.awt.datatransfer.DataFlavor
 */
protected boolean isSupportedObjectFlavor(DataFlavor dataFlavor) {

	if (super.isSupportedObjectFlavor(dataFlavor)){
		return true;
	}
	
	if(dataFlavor.equals(REACTION_SPECIES_ARRAY_FLAVOR)){
		return true;
	}

	if(dataFlavor.equals(RESOLVED_VALUES_FLAVOR)){
		return true;		
	}

	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2003 8:43:19 AM)
 * @param obj java.lang.Object
 * @param flavor java.awt.datatransfer.DataFlavor
 */
public static void sendToClipboard(Object obj) {

	if(obj == null){
		return;
	}
	VCellTransferable vct = new VCellTransferable(obj);
	Clipboard clipb = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
	clipb.setContents(vct,vct);
}
}

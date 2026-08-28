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

import org.vcell.util.ArrayUtils;
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

	public static final DataFlavor REACTION_SPECIES_ARRAY_FLAVOR    = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=" + ReactionSpeciesCopy.class.getName(),"ReactionSpeciesArray");
	public static final DataFlavor RESOLVED_VALUES_FLAVOR           = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=" + ResolvedValuesSelection.class.getName(),"ResolvedValues");


	public static class ResolvedValuesSelection {
		private final SymbolTableEntry[] primarySymbolTableEntries;
		private final SymbolTableEntry[] alternateSymbolTableEntries;
		private final Object[] values;
		private final String stringRepresentation;

		public ResolvedValuesSelection(SymbolTableEntry[] primarySymbolTableEntries,
		                               SymbolTableEntry[] alternateSymbolTableEntries,
		                               Object[] values,
		                               String stringRep){
			if (primarySymbolTableEntries.length != values.length ||
				(alternateSymbolTableEntries != null && alternateSymbolTableEntries.length != values.length)){
				throw new IllegalArgumentException("symbol array length must equal data array length");
			}
			for (Object argExpressionValue : values) {
				if (argExpressionValue == null) {
					throw new IllegalArgumentException("copied values cannot be null.");
				}
			}
			this.primarySymbolTableEntries = primarySymbolTableEntries;
			this.alternateSymbolTableEntries = alternateSymbolTableEntries;
			this.values = values;
			this.stringRepresentation = stringRep;
		}

		public SymbolTableEntry[] getPrimarySymbolTableEntries(){
			return this.primarySymbolTableEntries;
		}
		public SymbolTableEntry[] getAlternateSymbolTableEntries(){
			return this.alternateSymbolTableEntries;
		}
		public Object[] getValues(){
			return this.values;
		}
		public String toString() {
			return this.stringRepresentation;
		}
	}

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
		DataFlavor[] flavors = super.getTransferDataFlavors();

		// add custom flavors if available
		if(getDataObjectClass().equals(ReactionSpeciesCopy.class)){
			flavors = ArrayUtils.addElement(flavors, REACTION_SPECIES_ARRAY_FLAVOR);
		}

		if (getDataObjectClass().equals(VCellTransferable.ResolvedValuesSelection.class)){
			flavors = ArrayUtils.addElement(flavors, RESOLVED_VALUES_FLAVOR);
		}


		return flavors;
	}


	protected boolean isSupportedObjectFlavor(DataFlavor dataFlavor) {

		if (super.isSupportedObjectFlavor(dataFlavor)){
			return true;
		}

		if(dataFlavor.equals(REACTION_SPECIES_ARRAY_FLAVOR)){
			return true;
		}

		return dataFlavor.equals(RESOLVED_VALUES_FLAVOR);
	}

	public static void sendToClipboard(Object obj) {

		if(obj == null){
			return;
		}
		VCellTransferable vct = new VCellTransferable(obj);
		Clipboard clipb = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
		clipb.setContents(vct,vct);
	}
}

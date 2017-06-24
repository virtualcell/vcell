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

import org.vcell.model.rbm.MolecularType;
import org.vcell.util.BeanUtils;
import org.vcell.util.gui.SimpleTransferable;

import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.RuleParticipantSignature;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
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

	public static class ReactionSpeciesCopy {
		
		private SpeciesContext[] speciesContextArr;
		private ReactionStep[] reactStepArr;
		private ReactionRule[] rrArr;
		private MolecularType[] mtArr;
		private Structure fromStruct;	// the structure from where we copy
		private Structure[] structArr;	// Feature, Membrane  must always have at least one element
		
		public ReactionSpeciesCopy(SpeciesContext[] speciesContextArr, ReactionStep[] reactStepArr, 
				ReactionRule[] rrArr, MolecularType[] mtArr, 
				Structure fromStruct, Structure[] structArr) {
			this.speciesContextArr = (speciesContextArr==null || speciesContextArr.length==0 ? null : speciesContextArr);
			this.reactStepArr = (reactStepArr==null || reactStepArr.length==0 ? null : reactStepArr);
			this.rrArr = (rrArr==null || rrArr.length==0 ? null : rrArr);
			this.mtArr = (mtArr==null || mtArr.length==0 ? null : mtArr);
			this.fromStruct = fromStruct;													// can't be null
			this.structArr = (structArr==null || structArr.length==0 ? null : structArr);	// can't be null
			if(this.speciesContextArr == null && this.reactStepArr == null && this.rrArr == null && this.mtArr == null) {
				throw new IllegalArgumentException(ReactionSpeciesCopy.class.getName() + " all parameters null.");
			}
		}
		public SpeciesContext[] getSpeciesContextArr() {
			return speciesContextArr;
		}
		public ReactionStep[] getReactStepArr() {
			return reactStepArr;
		}
		public ReactionRule[] getReactionRuleArr() {
			return rrArr;
		}
		public MolecularType[] getMolecularTypeArr() {
			return mtArr;
		}
		public Structure[] getStructuresArr() {
			return structArr;
		}
		public Structure getFromStructure() {
			return fromStruct;
		}
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			if(reactStepArr != null){
//				sb.append("-----Reactions-----\n");
				for (int i = 0; i < reactStepArr.length; i++) {
					sb.append(reactStepArr[i].getName()+"\n");
				}
			}
			if(reactStepArr != null && speciesContextArr != null){
				sb.append("\n");
			}
			if(speciesContextArr != null){
//				sb.append("-----SpeciesContexts-----\n");
				for (int i = 0; i < speciesContextArr.length; i++) {
					sb.append(speciesContextArr[i].getName()+"\n");
				}
			}
			if((reactStepArr != null || speciesContextArr != null) && rrArr != null) {
				sb.append("\n");
			}
			if(rrArr != null) {
//				sb.append("-----ReactionRules-----\n");
				for (int i = 0; i < rrArr.length; i++) {
					sb.append(rrArr[i].getName()+"\n");
				}
			}
			
			if((reactStepArr != null || speciesContextArr != null || rrArr != null) && mtArr != null) {
				sb.append("\n");
			}
			if(mtArr != null) {
//				sb.append("-----MolecularTypes-----\n");
				for (int i = 0; i < mtArr.length; i++) {
					sb.append(mtArr[i].getName()+"\n");
				}
			}

			if((reactStepArr != null || speciesContextArr != null || rrArr != null || mtArr != null) && structArr != null) {
				sb.append("\n");
			}
			if(structArr != null) {
//				sb.append("-----Structures-----\n");
				for (int i = 0; i < structArr.length; i++) {
					sb.append(structArr[i].getName()+"\n");
				}
			}

			System.out.println(sb.toString());
			
			return sb.toString();
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
	if(getDataObjectClass().equals(VCellTransferable.ReactionSpeciesCopy.class)){
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

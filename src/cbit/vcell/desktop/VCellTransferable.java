package cbit.vcell.desktop;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.SimpleTransferable;

import cbit.vcell.model.Species;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;
/**
 * Insert the type's description here.
 * Creation date: (5/8/2003 2:40:40 PM)
 * @author: Frank Morgan
 */
public class VCellTransferable extends SimpleTransferable {

	public static final DataFlavor SPECIES_FLAVOR =
		new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+"; class="+cbit.vcell.model.Species.class.getName(),"Species");
	public static final DataFlavor REACTIONSTEP_ARRAY_FLAVOR =
		new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+"; class=\"[Lcbit.vcell.model.ReactionStep;\"","ReactionStepArray");
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
	if (getDataObjectClass().equals(Species.class)){
		flavors = (DataFlavor[]) BeanUtils.addElement(flavors,SPECIES_FLAVOR);
	}
	
	if (getDataObjectClass().equals(cbit.vcell.model.ReactionStep[].class)){
		flavors = (DataFlavor[]) BeanUtils.addElement(flavors,REACTIONSTEP_ARRAY_FLAVOR);
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
	
	if(dataFlavor.equals(SPECIES_FLAVOR)){
		return true;		
	}

	if(dataFlavor.equals(REACTIONSTEP_ARRAY_FLAVOR)){
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
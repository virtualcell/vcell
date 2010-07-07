package cbit.vcell.modelopt.gui;

import cbit.vcell.model.Model.ModelParameter;

/**
 * Insert the type's description here.
 * Creation date: (11/30/2005 2:22:31 PM)
 * @author: Jim Schaff
 */
public class SymbolTableEntryListCellRenderer extends javax.swing.DefaultListCellRenderer {

/**
 * SymbolTableEntryListCellRenderer constructor comment.
 */
public SymbolTableEntryListCellRenderer() {
	super();
}


public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

	java.awt.Component component = super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
	
	if (value instanceof javax.swing.Icon) {
		setIcon((javax.swing.Icon) value);
	} else {
		if (value instanceof cbit.vcell.parser.SymbolTableEntry){
			cbit.vcell.parser.SymbolTableEntry ste = (cbit.vcell.parser.SymbolTableEntry)value;
			if (ste instanceof cbit.vcell.model.ReservedSymbol){
				setText(ste.getName());
			}else if (ste instanceof cbit.vcell.model.SpeciesContext){
				setText("["+ste.getName()+"]");
			}else if (ste instanceof ModelParameter){
				setText(ste.getName());
			}else if (ste instanceof cbit.vcell.model.Kinetics.KineticsParameter){
				setText(ste.getNameScope().getName()+":"+ste.getName());
			}else{
				setText(ste.getNameScope().getAbsoluteScopePrefix()+ste.getName());
			}
		}else{
			setText((value == null) ? "" : value.toString());
		}
	}

	return component;
}
}
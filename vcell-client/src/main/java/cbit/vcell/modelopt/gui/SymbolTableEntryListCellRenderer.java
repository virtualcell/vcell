/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modelopt.gui;

import cbit.vcell.math.ReservedVariable;
import cbit.vcell.model.Model;
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
			if (ste instanceof Model.ReservedSymbol){
				setText(ste.getName());
			}else if (ste instanceof cbit.vcell.model.SpeciesContext){
				setText("["+ste.getName()+"]");
			}else if (ste instanceof ModelParameter){
				setText(ste.getName());
			}else if (ste instanceof cbit.vcell.model.Kinetics.KineticsParameter){
				setText(ste.getNameScope().getName()+":"+ste.getName());
			}else if (ste instanceof ReservedVariable) {
				setText(ste.getName());
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

/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package cbit.vcell.modelopt.gui;
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
		if (value instanceof org.vcell.expression.SymbolTableEntry){
			org.vcell.expression.SymbolTableEntry ste = (org.vcell.expression.SymbolTableEntry)value;
			if (ste instanceof cbit.vcell.model.ReservedSymbol){
				setText(ste.getName());
			}else if (ste instanceof cbit.vcell.model.SpeciesContext){
				setText("["+ste.getName()+"]");
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
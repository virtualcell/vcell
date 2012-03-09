/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization.gui;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;

import cbit.vcell.math.ReservedVariable;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.SymbolTableEntry;
/**
 * Insert the type's description here.
 * Creation date: (11/30/2005 2:11:10 PM)
 * @author: Jim Schaff
 */
public class SymbolTableEntryTableCellRenderer extends DefaultScrollTableCellRenderer {
/**
 * SymbolTableEntryTableCellRenderer constructor comment.
 */
public SymbolTableEntryTableCellRenderer() {
	super();
}


		public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value == null) {
				setText("unmapped");
				return this;
			}
			SymbolTableEntry ste = (SymbolTableEntry)value;
			if (ste instanceof Model.ReservedSymbol){
				setText(ste.getName());
			}else if (ste instanceof SpeciesContext){
				setText("["+ste.getName()+"]");
			}else if (ste instanceof KineticsParameter){
				setText(ste.getNameScope().getName()+":"+ste.getName());
			}else if (ste instanceof ModelParameter){
				setText(ste.getName());
			}else if (ste instanceof ReservedVariable) {
				setText(ste.getName());
			}else{
				setText(ste.getNameScope().getAbsoluteScopePrefix()+ste.getName());
			}
			//if (ste instanceof cbit.vcell.model.Kinetics.KineticsParameter){
				//setToolTipText("Kinetic parameter \""+ste.getName()+"\" in reaction "+);
			return this;
		}
}

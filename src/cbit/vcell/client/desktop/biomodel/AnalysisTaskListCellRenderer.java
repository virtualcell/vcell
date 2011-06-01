/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

/**
 * Insert the type's description here.
 * Creation date: (11/30/2005 2:22:31 PM)
 * @author: Jim Schaff
 */
public class AnalysisTaskListCellRenderer extends javax.swing.DefaultListCellRenderer {
/**
 * SymbolTableEntryListCellRenderer constructor comment.
 */
public AnalysisTaskListCellRenderer() {
	super();
}
public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

	java.awt.Component component = super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
	
	if (value instanceof javax.swing.Icon) {
		setIcon((javax.swing.Icon) value);
	} else {
		if (value instanceof cbit.vcell.modelopt.AnalysisTask){
			cbit.vcell.modelopt.AnalysisTask analysisTask = (cbit.vcell.modelopt.AnalysisTask)value;
			setText(analysisTask.getAnalysisTypeDisplayName()+" \""+analysisTask.getName()+"\"");
		}else{
			setText((value == null) ? "" : value.toString());
		}
	}

	return component;
}
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.constraints.gui;
import cbit.vcell.constraints.GeneralConstraint;
/**
 * Insert the type's description here.
 * Creation date: (7/9/2003 2:23:48 PM)
 * @author: Jim Schaff
 */
public class ConstraintTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
	private GeneralConstraintsTableModel fieldGeneralConstraintsTableModel = null;

/**
 * ConstraintTableCellRenderer constructor comment.
 */
public ConstraintTableCellRenderer() {
	super();
}


/**
 * Gets the generalConstraintsTableModel property (cbit.vcell.constraints.gui.GeneralConstraintsTableModel) value.
 * @return The generalConstraintsTableModel property value.
 * @see #setGeneralConstraintsTableModel
 */
public GeneralConstraintsTableModel getGeneralConstraintsTableModel() {
	return fieldGeneralConstraintsTableModel;
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/2001 1:11:37 PM)
 * @return java.awt.Component
 * @param table javax.swing.JTable
 * @param value java.lang.Object
 * @param isSelected boolean
 * @param hasFocus boolean
 * @param row int
 * @param column int
 */
public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	GeneralConstraint generalConstraint = getGeneralConstraintsTableModel().getConstraintContainerImpl().getGeneralConstraints(row);
	if (!getGeneralConstraintsTableModel().getConstraintContainerImpl().getConsistent(generalConstraint)) {
		if (isSelected){
			setForeground(java.awt.Color.white);
			setBackground(java.awt.Color.red);
		}else{
			setForeground(java.awt.Color.red);
			setBackground(java.awt.Color.white);
		}
	} else {
		if (isSelected){
			setForeground(java.awt.Color.white);
			setBackground(java.awt.Color.blue.darker().darker());
		}else{
			setForeground(java.awt.Color.black);
			setBackground(java.awt.Color.white);
		}
	}
	return this;
}


/**
 * Sets the generalConstraintsTableModel property (cbit.vcell.constraints.gui.GeneralConstraintsTableModel) value.
 * @param generalConstraintsTableModel The new value for the property.
 * @see #getGeneralConstraintsTableModel
 */
public void setGeneralConstraintsTableModel(GeneralConstraintsTableModel generalConstraintsTableModel) {
	fieldGeneralConstraintsTableModel = generalConstraintsTableModel;
}
}

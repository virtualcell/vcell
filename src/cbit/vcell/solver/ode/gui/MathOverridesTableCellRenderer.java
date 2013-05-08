/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

import org.vcell.util.gui.DefaultScrollTableCellRenderer;
/**
 * Insert the type's description here.
 * Creation date: (8/7/2001 1:10:01 PM)
 * @author: Ion Moraru
 */
public class MathOverridesTableCellRenderer extends DefaultScrollTableCellRenderer {
	private MathOverridesTableModel fieldMathOverridesTableModel = null;
/**
 * MathOverridesTableCellRenderer constructor comment.
 */
public MathOverridesTableCellRenderer() {
	super();
}
/**
 * Gets the mathOverridesTableModel property (cbit.vcell.solver.ode.gui.MathOverridesTableModel) value.
 * @return The mathOverridesTableModel property value.
 * @see #setMathOverridesTableModel
 */
public MathOverridesTableModel getMathOverridesTableModel() {
	return fieldMathOverridesTableModel;
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
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	if (!getMathOverridesTableModel().isDefaultValue(row) && column != MathOverridesTableModel.COLUMN_DEFAULT) {
		setForeground(Color.red);
	} else {
		if (column == MathOverridesTableModel.COLUMN_ACTUAL) {
			setText("");
		}
	}
	if (getMathOverridesTableModel().isUnusedParameterRow(row) && column == MathOverridesTableModel.COLUMN_PARAMETER){
		setText(value + " <<NOT USED>>");
	}
	return this;
}
/**
 * Sets the mathOverridesTableModel property (cbit.vcell.solver.ode.gui.MathOverridesTableModel) value.
 * @param mathOverridesTableModel The new value for the property.
 * @see #getMathOverridesTableModel
 */
public void setMathOverridesTableModel(MathOverridesTableModel mathOverridesTableModel) {
	MathOverridesTableModel oldValue = fieldMathOverridesTableModel;
	fieldMathOverridesTableModel = mathOverridesTableModel;
	firePropertyChange("mathOverridesTableModel", oldValue, mathOverridesTableModel);
}
}

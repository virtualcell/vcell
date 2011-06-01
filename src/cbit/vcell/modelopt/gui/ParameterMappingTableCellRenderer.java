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
import javax.swing.table.*;
import java.awt.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (8/7/2001 1:10:01 PM)
 * @author: Ion Moraru
 */
public class ParameterMappingTableCellRenderer extends DefaultTableCellRenderer {
/**
 * MathOverridesTableCellRenderer constructor comment.
 */
public ParameterMappingTableCellRenderer() {
	super();
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
	setHorizontalAlignment(JLabel.RIGHT);
	if (value instanceof Double) {
		if (value != null) {
			Double doubleValue = (Double)value;
			if (doubleValue.isNaN() || doubleValue.isInfinite()) {
				setText(java.text.NumberFormat.getInstance().format(doubleValue.doubleValue()));
			} else {
				String formattedDouble = org.vcell.util.NumberUtils.formatNumber(doubleValue.doubleValue());
				setText(formattedDouble);
			}
		}
	}
	return this;
}
}

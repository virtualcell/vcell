/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;

import javax.swing.table.*;
import java.awt.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (3/27/2001 1:04:27 PM)
 * @author: Ion Moraru
 */
public class DefaultTableCellRendererEnhanced extends DefaultTableCellRenderer {
/**
 * DefaultTableCellRendererEnhanced constructor comment.
 */
public DefaultTableCellRendererEnhanced() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 1:07:02 PM)
 * @return java.awt.Component
 * @param table javax.swing.JTable
 * @param value java.lang.Object
 * @param isSelected boolean
 * @param hasFocus boolean
 * @param row int
 * @param column int
 */
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	if (value instanceof JComponent) {
		JComponent jc = (JComponent)value;
/*		if (isSelected) {
		   jc.setForeground(table.getSelectionForeground());
		   jc.setBackground(table.getSelectionBackground());
		} else {
		    jc.setForeground(table.getForeground());
		    jc.setBackground(table.getBackground());
		}
*/		if (hasFocus) {
		    jc.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder") );
		} else {
		    jc.setBorder(noFocusBorder);
		}
		return jc;
	} else {
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}
}

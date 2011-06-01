/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class StructureMappingTableRenderer extends DefaultTableCellRenderer
{
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		//  set cell color to gray if the cell is not editable
		if (!table.getModel().isCellEditable(row, column))
		{
			setBackground( new Color(239,239,239));
			setForeground(Color.BLACK);
		}
		else 
		{
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		
		if (column == StructureMappingTableModel.COLUMN_SUBDOMAIN) {
			if (value == null) {
				setText("Unmapped");
				setForeground(Color.red);
			} else {
				setForeground(table.getForeground());
			}
		}
		
		setToolTipText(StructureMappingTableModel.COLUMN_TOOLTIPS[column]);
		return this;
	}
}

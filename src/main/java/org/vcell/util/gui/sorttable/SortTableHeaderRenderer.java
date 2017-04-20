/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui.sorttable;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.vcell.util.gui.sorttable.SortArrowIcon.SortDirection;

public class SortTableHeaderRenderer implements TableCellRenderer
{
  private static Icon NOSORTING_ICON = new SortArrowIcon(SortDirection.NOSORTING);
  private static Icon ASCENDING_ICON = new SortArrowIcon(SortDirection.ASCENDING);
  private static Icon DECENDING_ICON = new SortArrowIcon(SortDirection.DECENDING);
  
  private TableCellRenderer defaultTableCellRender = null;
  
public SortTableHeaderRenderer(TableCellRenderer tableCellRenderer) {
  defaultTableCellRender = tableCellRenderer;
}
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
	Component c = defaultTableCellRender.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
	JLabel label = null;
	if (c instanceof JLabel) {
		label = (JLabel)c;
	} else {
		label = new JLabel();
		label.setText(value.toString());
		Border border = null;
		if (hasFocus) {
			border = UIManager.getBorder("TableHeader.focusCellBorder");
		}
		if (border == null) {
			border = UIManager.getBorder("TableHeader.cellBorder");
		}
		
		if (table != null) { 
			JTableHeader header = table.getTableHeader(); 
			if (header != null) { 
				Color fgColor = null;
				Color bgColor = null;
				if (hasFocus) {
					fgColor = UIManager.getColor("TableHeader.focusCellForeground");
					bgColor = UIManager.getColor("TableHeader.focusCellBackground");
				}
				if (fgColor == null) {
					fgColor = header.getForeground();
				}
				if (bgColor == null) {
					bgColor = header.getBackground();
				}
				label.setForeground(fgColor);
				label.setBackground(bgColor);
				label.setFont(header.getFont()); 
			} 
		} 
	}
	boolean ascending = false;
	Icon icon = NOSORTING_ICON;
	if (table instanceof JSortTable) {
		int columnIndex = -1;
		JSortTable sortTable = (JSortTable) table;
		SortPreference sortPreference = ((SortTableModel)sortTable.getModel()).getSortPreference();
		int modelIndex = sortPreference.getSortedColumnIndex();
		if (modelIndex >= 0) {
			for (int i = 0; i < table.getColumnModel().getColumnCount(); i ++) {
				if (table.getColumnModel().getColumn(i).getModelIndex() == modelIndex) {
					columnIndex = i;
					break;
				}
			}
			ascending = sortPreference.isSortedColumnAscending();
			if (col == columnIndex) {
				icon = ascending ? ASCENDING_ICON : DECENDING_ICON;
			}
		}
	}
	label.setHorizontalTextPosition(JLabel.LEFT);
	label.setIcon(icon);
	return label;
}
}

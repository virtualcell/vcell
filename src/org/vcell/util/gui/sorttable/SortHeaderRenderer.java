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

public class SortHeaderRenderer implements TableCellRenderer
{
  private static Icon NONSORTED = new SortArrowIcon(SortArrowIcon.NONE);
  private static Icon ASCENDING = new SortArrowIcon(SortArrowIcon.ASCENDING);
  private static Icon DECENDING = new SortArrowIcon(SortArrowIcon.DECENDING);
  
  private TableCellRenderer defaultTableCellRender = null;
  
public SortHeaderRenderer(TableCellRenderer tableCellRenderer) {
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
	Icon icon = NONSORTED;
	if (table instanceof JSortTable) {
		int columnIndex = -1;
		JSortTable sortTable = (JSortTable) table;
		SortPreference sortPreference = ((SortTableModel)sortTable.getModel()).getSortPreference();
		int modelIndex = sortPreference.getSortedColumnIndex();
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i ++) {
			if (table.getColumnModel().getColumn(i).getModelIndex() == modelIndex) {
				columnIndex = i;
				break;
			}
		}
		ascending = sortPreference.isSortedColumnAscending();
		if (col == columnIndex) {
			icon = ascending ? ASCENDING : DECENDING;
		}
	}
	label.setHorizontalTextPosition(JLabel.LEFT);
	label.setIcon(icon);
	return label;
}
}

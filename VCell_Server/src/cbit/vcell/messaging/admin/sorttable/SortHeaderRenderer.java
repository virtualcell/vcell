package cbit.vcell.messaging.admin.sorttable;

/*
=====================================================================

  SortHeaderRenderer.java
  
  Created by Claude Duguay
  Copyright (c) 2002
  
=====================================================================
*/

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class SortHeaderRenderer
  extends DefaultTableCellRenderer
{
  public static Icon NONSORTED = new SortArrowIcon(SortArrowIcon.NONE);
  public static Icon ASCENDING = new SortArrowIcon(SortArrowIcon.ASCENDING);
  public static Icon DECENDING = new SortArrowIcon(SortArrowIcon.DECENDING);
  
public SortHeaderRenderer() {
  setHorizontalTextPosition(LEFT);
  setHorizontalAlignment(CENTER);
}
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
	int modelIndex = -1;
	int columnIndex = -1;
	boolean ascending = false;
	if (table instanceof JSortTable) {
		JSortTable sortTable = (JSortTable) table;
		modelIndex = ((SortTableModel)sortTable.getModel()).getSortPreference().getSortedColumnIndex();
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i ++) {
			if (table.getColumnModel().getColumn(i).getModelIndex() == modelIndex) {
				columnIndex = i;
				break;
			}
		}
		ascending = ((SortTableModel)sortTable.getModel()).getSortPreference().isSortedColumnAscending();
	} 
	
	if (table != null) {
		JTableHeader header = table.getTableHeader();
		if (header != null) {
			setForeground(header.getForeground());
			setBackground(header.getBackground());
			setFont(header.getFont());
		}
	}
	Icon icon = ascending ? ASCENDING : DECENDING;
	setIcon(col == columnIndex ? icon : NONSORTED);
	setText((value == null) ? "" : value.toString());
	setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	return this;
}
}

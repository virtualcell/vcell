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

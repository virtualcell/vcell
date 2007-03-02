package cbit.vcell.mapping.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;;

public class StructureMappingTableRenderer extends DefaultTableCellRenderer
{
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		// Only for specific column
		if ((column == 3)||(column == 4))
		{
			setBackground( new Color(239,239,239));
			setForeground(Color.BLACK);
		}
		else 
		{
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		return this;
	}
}

package cbit.vcell.mapping.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.util.NumberUtils;

import cbit.vcell.geometry.GeometryClass;

public class StructureMappingTableRenderer extends DefaultTableCellRenderer
{
	StructureMappingTableModel structureMappingTableModel = null;
	
	public StructureMappingTableRenderer(StructureMappingTableModel m) {
		super();
		structureMappingTableModel = m;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		//  set cell color to gray if the cell is not editable
		if (table.getModel().isCellEditable(row, column))
		{
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		else 
		{
			setBackground(UIManager.getColor("TextField.inactiveBackground"));
			setForeground(UIManager.getColor("TextField.foreground"));
		}
		if (value instanceof Double) {
			double d = (Double)value;
			setText(NumberUtils.formatNumber(d, 10) + "");
		}
		if (structureMappingTableModel.isSubdomainColumn(column)) { // can be null
			if (value == null) {
				setText("Unmapped");
				setForeground(Color.red);
			} else {
				setForeground(table.getForeground());
			}
		}
		
		String toolTip = structureMappingTableModel.getToolTip(row, column);
		setToolTipText(toolTip);
		return this;
	}
}

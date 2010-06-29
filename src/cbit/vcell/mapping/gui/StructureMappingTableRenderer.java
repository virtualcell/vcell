package cbit.vcell.mapping.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

import org.vcell.util.NumberUtils;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;

public class StructureMappingTableRenderer extends DefaultScrollTableCellRenderer
{
	
	public StructureMappingTableRenderer() {
		super();
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (table.getModel() instanceof StructureMappingTableModel) {
			StructureMappingTableModel structureMappingTableModel = (StructureMappingTableModel)table.getModel();
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
		}
		return this;
	}
}

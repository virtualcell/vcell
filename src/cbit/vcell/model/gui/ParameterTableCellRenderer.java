package cbit.vcell.model.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import cbit.vcell.model.gui.TransformMassActions.TransformedReaction;

public class ParameterTableCellRenderer extends DefaultTableCellRenderer {
	
	private TableCellRenderer booleanCellRenderer;
	
	public ParameterTableCellRenderer(TableCellRenderer booleanCellRenderer) {
		super();
		this.booleanCellRenderer = booleanCellRenderer;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Component tcr = null;
		if(booleanCellRenderer != null && value instanceof Boolean){
			// if it is the Boolean (IS_GLOBAL) column, 
			tcr = booleanCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (table.isCellEditable(row, column)) {
				tcr.setEnabled(true);
			} else {
				tcr.setEnabled(false);
			}
		}else{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			tcr = this;
		}
		//  set cell color to gray if the cell is not editable
		if (!table.getModel().isCellEditable(row, column) && !isSelected) {
			setBackground(Color.WHITE);
			setForeground(new Color(128,128,128));
		} else {
			if (isSelected) {
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			} else {
				setBackground(table.getBackground());
				setForeground(table.getForeground());
			}
		}
		
		return tcr;
	}
}

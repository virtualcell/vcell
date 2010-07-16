package org.vcell.util.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.util.NumberUtils;

public class DefaultScrollTableCellRenderer extends DefaultTableCellRenderer {
	static final Border focusHighlightBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
	static final Color uneditableBackground = UIManager.getColor("TextField.inactiveBackground");
	static final Color uneditableForeground = UIManager.getColor("TextField.inactiveForeground");
	static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	
	/**
	 * DefaultTableCellRendererEnhanced constructor comment.
	 */
	public DefaultScrollTableCellRenderer() {
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
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (table.isEnabled() && table.getModel().isCellEditable(row, column)) {
			if (isSelected) {
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			} else {
				setBackground(table.getBackground());
				setForeground(table.getForeground());
			}
		}
		else {
			if (isSelected) {
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			} else {
				setBackground(uneditableBackground);
				setForeground(uneditableBackground.equals(table.getBackground()) ? uneditableForeground : table.getForeground());
			}
		}
		if (value instanceof Double) {
			Double doubleValue = (Double)value;
			if (doubleValue.isNaN() || doubleValue.isInfinite()) {
				setText(java.text.NumberFormat.getInstance().format(doubleValue.doubleValue()));
			} else {
				String formattedDouble = NumberUtils.formatNumber(doubleValue.doubleValue());
				setText(formattedDouble);
			}			
		} else if (value instanceof JComponent) {
			JComponent jc = (JComponent)value;
			if (hasFocus) {
			    jc.setBorder(focusHighlightBorder );
			} else {
			    jc.setBorder(noFocusBorder);
			}
			return jc;
		}
		return this;
	}
}

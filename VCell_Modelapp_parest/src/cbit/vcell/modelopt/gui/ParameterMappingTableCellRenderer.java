package cbit.vcell.modelopt.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import javax.swing.table.*;
import java.awt.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (8/7/2001 1:10:01 PM)
 * @author: Ion Moraru
 */
public class ParameterMappingTableCellRenderer extends DefaultTableCellRenderer {
/**
 * MathOverridesTableCellRenderer constructor comment.
 */
public ParameterMappingTableCellRenderer() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/2001 1:11:37 PM)
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
	setHorizontalAlignment(JLabel.RIGHT);
	if (value instanceof Double) {
		if (value != null) {
			Double doubleValue = (Double)value;
			if (doubleValue.isNaN() || doubleValue.isInfinite()) {
				setText(java.text.NumberFormat.getInstance().format(doubleValue.doubleValue()));
			} else {
				String formattedDouble = cbit.util.NumberUtils.formatNumber(doubleValue.doubleValue());
				setText(formattedDouble);
			}
		}
	}
	return this;
}
}
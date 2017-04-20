/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy;
import java.awt.Component;
import java.text.NumberFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
//this cell renderer is used for table in FRAPReacDiffEstimationGuidPanel.
//it has two functionalities 1) precision renderer for Double column 2)highlight estimated parameters in row
@SuppressWarnings("serial")
public class EstimatedParameterTableRenderer extends DefaultTableCellRenderer
{
	private NumberFormat format;
    
    public EstimatedParameterTableRenderer(int precision) {
        format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(precision);
        format.setMinimumFractionDigits(precision);
    }
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		//  set whole row highlighted if it is newly calculated.
		Object nameObj = table.getValueAt(row, EstimatedParameterTableModel.COLUMN_NAME); //first column should be name of the parameter
		if(nameObj instanceof String)
		{
		}
		if(column == EstimatedParameterTableModel.COLUMN_VALUE)
		{
			setText(format.format(value));
		}
		//show tool tip text if the table cell is too small to see everything
		if(value != null)
		{
			setToolTipText(value.toString());
		}
		return this;
	}
}


/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.util.DescriptiveStatistics;

import cbit.vcell.microscopy.batchrun.gui.BatchRunResultsParamTableModel;

/**
 */
@SuppressWarnings("serial")
public class NumericTableCellRenderer  extends DefaultTableCellRenderer {
	
	private NumberFormat format;
    private static final int DEFAULT_PRECISION = 6;
	public NumericTableCellRenderer() {
		this(DEFAULT_PRECISION);
	}
    public NumericTableCellRenderer(int precision) {
        super();
        setFont(new Font("Arial", Font.PLAIN, 11));
        //set double precision
        format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(precision);
        format.setMinimumFractionDigits(0);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column)    
    {
    	super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    	setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 1));
        if(value != null)
        {
        	setToolTipText(value.toString());
        }
    	if(value != null && (value instanceof Double || value instanceof Float || value instanceof Integer))
		{
			setText(format.format(value));
		}
    	
    	if(value == null)
		{
			setBackground(new Color(228,228,228)); //light gray
			setForeground(new Color(228,228,228));
		}
    	else
		{
    		if (isSelected) {
    			setBackground(table.getSelectionBackground());
    			setForeground(table.getSelectionForeground());
    		} else {
    			setBackground(table.getBackground());
    			setForeground(table.getForeground());
    		}
		}
    	
    	//the following code is to highlight the entire row of the statistics in batchrun parameters table
    	Object nameObj = table.getValueAt(row, BatchRunResultsParamTableModel.COLUMN_FILE_NAME); //first column should be name of the parameter
		if(nameObj instanceof String)
		{
			String name = (String)nameObj;
			if(name.equals(DescriptiveStatistics.MEAN_NAME) ||
			   name.equals(DescriptiveStatistics.STANDARD_DEVIATION_NAME))
			{
				
				if (isSelected) {
	    			setBackground(table.getSelectionBackground());
	    			setForeground(table.getSelectionForeground());
	    		} else {
	    			setBackground( new Color(255,255,128));//yellow
	    			setForeground(table.getForeground());
	    		}
			}
		}
    	
        return this;
    }
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.batchrun.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.util.DescriptiveStatistics;


@SuppressWarnings("serial")
public class ResultsParamTableRenderer extends DefaultTableCellRenderer
{
	private JButton button = null;
	public ResultsParamTableRenderer()
	{
		button = new JButton("Details...");
		button.setVerticalTextPosition(SwingConstants.CENTER); 
		button.setHorizontalTextPosition(SwingConstants.LEFT);
//		button.setBorderPainted(false);
        setFont(new Font("Arial", Font.PLAIN, 11));
	}
	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) 
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 1));
		if(column == BatchRunResultsParamTableModel.COLUMN_FILE_NAME)
		{
			if(value instanceof String)
			{
				setText((String)value);
			}
			else if(value instanceof File)
			{
				String fileName = ((File)value).getName();
				setText(fileName);
			}
			if(value != null)
	        {
	        	setToolTipText(value.toString());
	        }
		}
		else if(column == BatchRunResultsParamTableModel.COLUMN_DETAILS)
		{
			Object firstColStr = table.getValueAt(row, BatchRunResultsParamTableModel.COLUMN_FILE_NAME); 
			if((firstColStr instanceof String) && (firstColStr.equals(DescriptiveStatistics.MEAN_NAME)||
			   firstColStr.equals(DescriptiveStatistics.MEDIAN_NAME)||
			   firstColStr.equals(DescriptiveStatistics.MODE_NAME)||
			   firstColStr.equals(DescriptiveStatistics.MIN_NAME)||
			   firstColStr.equals(DescriptiveStatistics.MAX_NAME)||
			   firstColStr.equals(DescriptiveStatistics.STANDARD_DEVIATION_NAME)))
			{
				return null;
			}
			else
			{
				return button;
			}
		}
		
		Object nameObj = table.getValueAt(row, BatchRunResultsParamTableModel.COLUMN_FILE_NAME); //first column should be name of the parameter
		if(nameObj instanceof String) //in statistics table, in other tables, the first column class is file
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
		}
		
		return this;
	}
}

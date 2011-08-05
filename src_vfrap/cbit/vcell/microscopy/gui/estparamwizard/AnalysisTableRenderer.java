/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import cbit.vcell.microscopy.FRAPModel;


@SuppressWarnings("serial")
public class AnalysisTableRenderer extends DefaultTableCellRenderer
{
	private NumberFormat format;
	private JButton button = null;
	public AnalysisTableRenderer(int precision)
	{
		super();
        setFont(new Font("Arial", Font.PLAIN, 11));
        //set double precision
        format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(precision);
        format.setMinimumFractionDigits(0);
		//create button 
		button = new JButton("Plot...");
		button.setVerticalTextPosition(SwingConstants.CENTER); 
		button.setHorizontalTextPosition(SwingConstants.LEFT); 
		button.setBackground(Color.white);
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
    	if(value == null)
		{
			setBackground(new Color(228,228,228)); //light gray
		}
    	else
		{
    		if (isSelected) {
    			setBackground(table.getSelectionBackground());
    		} else {
    			setBackground(table.getBackground());
    		}
			setForeground(Color.black);
		}
    	
    	if(value != null && (value instanceof Double || value instanceof Float || value instanceof Integer))
		{
			setText(format.format(value));
		}
    	
		if((column == AnalysisTableModel.COLUMN_DIFF_ONE_CI_PLOT && row < FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF &&/*there is a CI*/ table.getValueAt(row , column -1) != null) ||
		   (column == AnalysisTableModel.COLUMN_DIFF_TWO_CI_PLOT && row < FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF &&/*there is a CI*/ table.getValueAt(row , column -1) != null) ||
		   (column == AnalysisTableModel.COLUMN_KOFF_CI_PLOT && row < FRAPModel.NUM_MODEL_PARAMETERS_REACTION_OFF_RATE &&/*there is a CI*/ table.getValueAt(row , column -1) != null))
		{
			return button;
		}
		
		if(value instanceof String)
		{
			if(((String)value).equals(AnalysisTableModel.STR_NOT_SIGNIFICANT))
			{
				setForeground(Color.red);
			}
		}
		
		//show the evaluated model(with confidence intervals)significan in green and not significant in red.
		if(column == AnalysisTableModel.COLUMN_DIFF_ONE_PARAMETER_VAL || column == AnalysisTableModel.COLUMN_DIFF_ONE_CI)
		{
			Object identifyStr = table.getValueAt(AnalysisTableModel.INDEX_MODEL_SIGNIFICANCE, AnalysisTableModel.COLUMN_DIFF_ONE_PARAMETER_VAL);
			if(identifyStr instanceof String)
			{
				if(((String)identifyStr).equals(AnalysisTableModel.STR_NOT_SIGNIFICANT))
				{
					setForeground(Color.red);
					setBackground(new Color(255,170,170));
				}
			}
		}	
		if(column == AnalysisTableModel.COLUMN_DIFF_TWO_PARAMETER_VAL || column == AnalysisTableModel.COLUMN_DIFF_TWO_CI)
		{
			Object identifyStr = table.getValueAt(AnalysisTableModel.INDEX_MODEL_SIGNIFICANCE, AnalysisTableModel.COLUMN_DIFF_TWO_PARAMETER_VAL);
			if(identifyStr instanceof String)
			{
				if(((String)identifyStr).equals(AnalysisTableModel.STR_NOT_SIGNIFICANT))
				{
					setForeground(Color.red);
					setBackground(new Color(255,170,170));
				}
			}
		}	
		if(column == AnalysisTableModel.COLUMN_KOFF_PARAMETER_VAL || column == AnalysisTableModel.COLUMN_KOFF_CI)
		{
			Object identifyStr = table.getValueAt(AnalysisTableModel.INDEX_MODEL_SIGNIFICANCE, AnalysisTableModel.COLUMN_KOFF_PARAMETER_VAL);
			if(identifyStr instanceof String)
			{
				if(((String)identifyStr).equals(AnalysisTableModel.STR_NOT_SIGNIFICANT))
				{
					setForeground(Color.red);
					setBackground(new Color(255,170,170));
				}
			}
		}	
		
		return this;
	}
}

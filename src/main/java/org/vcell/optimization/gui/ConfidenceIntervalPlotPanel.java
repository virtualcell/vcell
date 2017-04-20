/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.optimization.ProfileSummaryData;
import org.vcell.util.gui.AdvancedTablePanel;
import org.vcell.util.gui.StyleTable;

import cbit.plot.gui.PlotPane;
/**
 * The panel is used to display the profile distribution for a specific parameter.
 * In this panel, a plot is on top, which contains the profile distribution and confidence
 * interval lines(for 80%, 90%, 95% and 99% confidence levels). 
 * At the bottom is a table listing the confidence intervals and the best estimate in numbers.
 * author: Tracy Li
 * version: 1.0
 */
public class ConfidenceIntervalPlotPanel extends JPanel
{
	ProfileSummaryData summaryData = null;
	PlotPane plotPane = null;
	StyleTable intervalTable = null;	
	ConfidenceIntervalTableModel tableModel = null;
	AdvancedTablePanel tablePanel = null;
	
	private class ConfidenceIntervalTableCellRenderer  extends DefaultTableCellRenderer {
		
		private NumberFormat format;
	    
	    public ConfidenceIntervalTableCellRenderer(int precision) {
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
	    		} else {
	    			setBackground(table.getBackground());
	    		}
				setForeground(Color.black);
			}
	    	
	        return this;
	    }
	}
	
	public ConfidenceIntervalPlotPanel()
	{
		setLayout(new BorderLayout());
		add(getPlotPane(), BorderLayout.CENTER);
		add(getTablePanel(), BorderLayout.SOUTH);
	}
	
	private PlotPane getPlotPane()
	{
		if(plotPane == null)
		{
			plotPane = new PlotPane();
			plotPane.setBackground(Color.white);
		}
		return plotPane;
	}
	
	private StyleTable getIntervalTable()
	{
		if(intervalTable == null)
		{
			tableModel = new ConfidenceIntervalTableModel();
			intervalTable = new StyleTable(tableModel);
			//set numeic renderer to numeric table cell 
			intervalTable.getColumnModel().getColumn(ConfidenceIntervalTableModel.COLUMN_PARAMETER_VAL).setCellRenderer(new ConfidenceIntervalTableCellRenderer(5));
			
		}
		return intervalTable;
	}
	
	private AdvancedTablePanel getTablePanel()
	{
		if(tablePanel == null)
		{
			tablePanel = new AdvancedTablePanel();
			tablePanel.setLayout(new BorderLayout());
			tablePanel.setTable(getIntervalTable());
			tablePanel.add(getIntervalTable().getTableHeader(),BorderLayout.NORTH);
			tablePanel.add(getIntervalTable(),BorderLayout.SOUTH);
		}
		return tablePanel;
	}
	
	public void setProfileSummaryData(ProfileSummaryData summaryData)
	{
		this.summaryData = summaryData;
		plotPane.setPlot2D(summaryData.getPlot2D());
		tableModel.setProfileSummaryData(summaryData);
	}
}

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

import javax.swing.table.AbstractTableModel;

import org.vcell.optimization.ConfidenceInterval;
import org.vcell.optimization.ProfileSummaryData;

/*
 * The class displays the beat estimated parameter value and its confidence intervals. 
 * It contains fixed 5 cols and only 1 row of data. 
 * author: Tracy Li
 * version: 1.0 
 */
public class ConfidenceIntervalTableModel extends AbstractTableModel
{
	
	public final static int NUM_COLUMNS = 5;
	public final static int NUM_ROWS = 1;
	public final static int COLUMN_PARAMETER_VAL = 0;
	public final static int COLUMN_80PERCENT_CI = 1;
	public final static int COLUMN_90PERCENT_CI = 2;
	public final static int COLUMN_95PERCENT_CI = 3;
	public final static int COLUMN_99PERCENT_CI = 4;
	
	public final static String COL_LABELS[] = { "Best Estimate", "80% Confidence Interval", "90% Confidence Interval",
												"95% Confidence Interval", "99% Confidence Interval"};
	
	ProfileSummaryData summaryData = null;
    public ConfidenceIntervalTableModel()
    {
    	super();
    }

    public int getColumnCount() {
        return NUM_COLUMNS;
    }

    public int getRowCount() {
       return NUM_ROWS;
    }

    public Object getValueAt(int row, int col) 
    {
    	if (col<0 || col>=NUM_COLUMNS){
    		throw new RuntimeException("ConfidenceIntervalTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
    	}
    	if (summaryData == null)
    	{
    		return null;
    	}
    	if(col == COLUMN_PARAMETER_VAL)
    	{
   			return summaryData.getBestEstimate();
    	}
    	else if(col == COLUMN_80PERCENT_CI)
    	{
			return summaryData.getConfidenceIntervals()[ConfidenceInterval.IDX_DELTA_ALPHA_80].toString();
    	}
    	else if(col == COLUMN_90PERCENT_CI)
    	{
    		return summaryData.getConfidenceIntervals()[ConfidenceInterval.IDX_DELTA_ALPHA_90].toString();
    	}
    	else if(col == COLUMN_95PERCENT_CI)
    	{
    		return summaryData.getConfidenceIntervals()[ConfidenceInterval.IDX_DELTA_ALPHA_95].toString();
    	}
    	else if(col == COLUMN_99PERCENT_CI)
    	{
    		return summaryData.getConfidenceIntervals()[ConfidenceInterval.IDX_DELTA_ALPHA_99].toString();
    	}
    	
    	return null;
    }

    public Class<?> getColumnClass(int col) 
    {
    	if(col == COLUMN_PARAMETER_VAL)
    	{
   			return Double.class;
    	}
    	else
    	{
    		return String.class;
    	}
    }

    public String getColumnName(int column) {
    	if (column<0 || column>=NUM_COLUMNS){
    		throw new RuntimeException("ConfidenceIntervalTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
    	}
        return COL_LABELS[column];
    }

    public boolean isCellEditable(int rowIndex,int columnIndex) 
    {
    	return false;
    }

    public void setValueAt(Object aValue,int rowIndex, int columnIndex) 
    {
    }

	public void setProfileSummaryData(ProfileSummaryData summaryData) 
	{
		this.summaryData = summaryData;
		fireTableDataChanged();
	}
     
}

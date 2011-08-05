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

import java.io.File;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import cbit.vcell.microscopy.FRAPOptimizationUtils;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;

@SuppressWarnings("serial")
public class BatchRunMSETableModel extends AbstractTableModel{
	
	public final static String COL_LABELS[] = { "Document", "ROI_Bleached", "ROI_Ring1", "ROI_Ring2", "ROI_Ring3",
			"ROI_Ring4", "ROI_Ring5", "ROI_Ring6", "ROI_Ring7", "ROI_Ring8", "Comparable Err"};
	public final static int NUM_COLUMNS = COL_LABELS.length;
	public final static int COLUMN_FILE_NAME = 0;
	public final static int COLUMN_ROI_BLEACHED = 1;
	public final static int COLUMN_ROI_RING1 = 2;
	public final static int COLUMN_ROI_RING2 = 3;
	public final static int COLUMN_ROI_RING3 = 4;
	public final static int COLUMN_ROI_RING4 = 5;
	public final static int COLUMN_ROI_RING5 = 6;
	public final static int COLUMN_ROI_RING6 = 7;
	public final static int COLUMN_ROI_RING7 = 8;
	public final static int COLUMN_ROI_RING8 = 9;
	public final static int COLUMN_SUM_ERROR = 10;
	
	private double[][] mseSummaryData = null;
	private FRAPBatchRunWorkspace batchRunWorkspace = null;
	
	public BatchRunMSETableModel() {
		super();
	}
	
	public int getColumnCount() {
		return NUM_COLUMNS;
	}
	
	public int getRowCount() 
	{
		if(batchRunWorkspace == null)
		{
			return 0;
		}
		return batchRunWorkspace.getFrapStudies().size();
	}
	
	public Object getValueAt(int row, int col) 
	{
		ArrayList<FRAPStudy> fStudyList = batchRunWorkspace.getFrapStudies(); 
		mseSummaryData = batchRunWorkspace.getAnalysisMSESummaryData();
	
		if (col<0 || col>=NUM_COLUMNS){
			throw new RuntimeException("MSETableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
		}
		if (row<0 || row >= getRowCount()){
			throw new RuntimeException("MSETableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
		}
		if(mseSummaryData == null)
		{
			return null;
		}
		if(col == COLUMN_FILE_NAME)
		{
			return new File(fStudyList.get(row).getXmlFilename());
		}
		else
		{
			//in mseSummaryData there is no model name col, therefore we use col-1 here.
			if(mseSummaryData[row][col-1] != FRAPOptimizationUtils.largeNumber)
			{
				return mseSummaryData[row][col-1];
			}
			else
			{
				return null;
			}
		
		}	
	}
	
	public Class<?> getColumnClass(int column) {
		if(column == COLUMN_FILE_NAME)
		{
			return File.class;
		}
		else
		{
			return Double.class;
		}
	}
	
	public String getColumnName(int column) {
		return COL_LABELS[column];
	}
	
	public boolean isCellEditable(int rowIndex,int columnIndex) 
	{
		return false;
	}
	
	public void setValueAt(Object aValue,int rowIndex, int columnIndex) 
	{
	}
	
	public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace)
	{
		this.batchRunWorkspace = batchRunWorkspace;
		fireTableDataChanged();
	}
	

}


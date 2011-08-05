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

import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.opt.Parameter;
@SuppressWarnings("serial")
public class BatchRunResultsParamTableModel extends AbstractTableModel
{
	public final static int NUM_COLUMNS = 11;
	
	public final static int COLUMN_FILE_NAME = 0;
	public final static int COLUMN_PRI_DIFF_RATE = 1;
	public final static int COLUMN_PRI_MOBILE_FRACTION = 2;
	public final static int COLUMN_BMR = 3;
	public final static int COLUMN_SEC_DIFF_RATE = 4;
	public final static int COLUMN_SEC_MOBILE_FRACTION = 5;
	public final static int COLUMN_BS_CONCENTRATION = 6;
	public final static int COLUMN_ON_RATE = 7;
	public final static int COLUMN_OFF_RATE = 8;
	public final static int COLUMN_IMMOBILE_FRACTION = 9;
	public final static int COLUMN_DETAILS = 10;
	
	public final static String COL_LABELS[] = { "Document", "Primary Diff Rate", "Primary Mobile Fraction",
												"Bleach Monitor Rate", "Secondary Diff Rate",
												"Secondary Mobile Fraction", "Binding site concentration",
												"Reaction on rate", "Reaction off rate", "Immobile Fraction", "Show Details"};
	
	private FRAPBatchRunWorkspace batchRunWorkspace = null;
	private ArrayList<FRAPStudy> frapStudies = null;
	
    public BatchRunResultsParamTableModel() {
    	super();
    }

    public int getColumnCount() {
        return NUM_COLUMNS;
    }

    public int getRowCount() {
       return (getBatchRunWorkspace() == null)? 0:getBatchRunWorkspace().getFrapStudies().size();
    }

    public Object getValueAt(int row, int col) 
    {
    	if (col<0 || col>=NUM_COLUMNS){
    		throw new RuntimeException("AnalysisTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
    	}
    	    	
    	frapStudies = batchRunWorkspace.getFrapStudies();
    	
    	if (frapStudies == null)
    	{
    		return null;
    	}
    	if(col == COLUMN_FILE_NAME)
    	{
   			return new File(frapStudies.get(row).getXmlFilename());
    	}
    	else if(col == COLUMN_PRI_DIFF_RATE)
    	{
    		if(getBatchRunWorkspace().getSelectedModel() == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT || 
    		   getBatchRunWorkspace().getSelectedModel() == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
    		{
				Parameter[] modelParams = frapStudies.get(row).getFrapModel(getBatchRunWorkspace().getSelectedModel()).getModelParameters();
				if(modelParams != null)
				{
					return modelParams[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess();
				}
    		}
    	}
    	else if(col == COLUMN_PRI_MOBILE_FRACTION)
    	{
    		if(getBatchRunWorkspace().getSelectedModel() == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT || 
    	       getBatchRunWorkspace().getSelectedModel() == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
    		{
				Parameter[] modelParams = frapStudies.get(row).getFrapModel(getBatchRunWorkspace().getSelectedModel()).getModelParameters();
				if(modelParams != null)
				{
					return modelParams[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess();
				}
    		}
    	}
    	else if(col == COLUMN_SEC_DIFF_RATE)
    	{
    		if(getBatchRunWorkspace().getSelectedModel() == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
    		{
    			Parameter[] modelParams = frapStudies.get(row).getFrapModel(getBatchRunWorkspace().getSelectedModel()).getModelParameters();
    			if(modelParams != null)
    			{
    				return modelParams[FRAPModel.INDEX_SECONDARY_DIFF_RATE].getInitialGuess();
    			}
    		}
    	}
    	else if(col == COLUMN_SEC_MOBILE_FRACTION)
    	{
    		if(getBatchRunWorkspace().getSelectedModel() == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
    		{
    			Parameter[] modelParams = frapStudies.get(row).getFrapModel(getBatchRunWorkspace().getSelectedModel()).getModelParameters();
    			if(modelParams != null)
    			{
    				return modelParams[FRAPModel.INDEX_SECONDARY_FRACTION].getInitialGuess();
    			}
    		}
    	}
    	else if(col == COLUMN_BMR)
    	{
			Parameter[] modelParams = frapStudies.get(row).getFrapModel(getBatchRunWorkspace().getSelectedModel()).getModelParameters();
			if(modelParams != null)
			{
				return modelParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess();
			}
    	}
    	else if(col == COLUMN_OFF_RATE)
    	{
    		if(getBatchRunWorkspace().getSelectedModel() == FRAPModel.IDX_MODEL_REACTION_OFF_RATE)
    		{
				Parameter[] modelParams = frapStudies.get(row).getFrapModel(getBatchRunWorkspace().getSelectedModel()).getModelParameters();
				if(modelParams != null)
				{
					return modelParams[FRAPModel.INDEX_OFF_RATE].getInitialGuess();
				}
    		}
    	}
    	else if(col == COLUMN_IMMOBILE_FRACTION)
    	{
    		if(getBatchRunWorkspace().getSelectedModel() == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT || 
    	       getBatchRunWorkspace().getSelectedModel() == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
    	    {
				Parameter[] modelParams = frapStudies.get(row).getFrapModel(getBatchRunWorkspace().getSelectedModel()).getModelParameters();
				if(modelParams != null)
				{
	    			double primaryFrac = modelParams[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess();
	    			if(getBatchRunWorkspace().getSelectedModel() == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT)
	        		{
	        			return Math.max(0, (1-primaryFrac));
	        		}
	    			else
	    			{
	    				double secFrac = modelParams[FRAPModel.INDEX_SECONDARY_FRACTION].getInitialGuess();
	    				return Math.max(0, (1-primaryFrac-secFrac));
	    			}
				}
    	    }
    	}
    	else if(col == COLUMN_DETAILS)
    	{
    		return "Details";
    	}
    	return null;
    }

    public Class<?> getColumnClass(int column) {
    	switch (column){
    		case COLUMN_FILE_NAME:{
    			return File.class;
    		}
    		case COLUMN_PRI_DIFF_RATE: case COLUMN_PRI_MOBILE_FRACTION: case COLUMN_SEC_DIFF_RATE:
    		case COLUMN_SEC_MOBILE_FRACTION: case COLUMN_BMR: case COLUMN_BS_CONCENTRATION:
    		case COLUMN_ON_RATE: case COLUMN_OFF_RATE: case COLUMN_IMMOBILE_FRACTION:
    		{
    			return Double.class;
    		}
    		case COLUMN_DETAILS: {
    			return Object.class;
    		}
    		default:{
    			return Object.class;
    		}
    	}
    }

    public String getColumnName(int column) {
    	if (column<0 || column>=NUM_COLUMNS){
    		throw new RuntimeException("AnalysisTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
    	}
        return COL_LABELS[column];
    }

    public boolean isCellEditable(int rowIndex,int columnIndex) 
    {
    	if(columnIndex == COLUMN_DETAILS)
    	{
    		return true;
    	}
        return false;
    }

    public void setValueAt(Object aValue,int rowIndex, int columnIndex) 
    {
    }
     
    public FRAPBatchRunWorkspace getBatchRunWorkspace()
    {
        return batchRunWorkspace;
    }
   
    public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace)
    {
    	this.batchRunWorkspace = batchRunWorkspace;
    	fireTableDataChanged();
    }
}



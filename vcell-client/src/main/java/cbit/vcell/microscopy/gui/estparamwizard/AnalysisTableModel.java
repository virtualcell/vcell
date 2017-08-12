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

import javax.swing.table.AbstractTableModel;

import org.vcell.optimization.ConfidenceInterval;
import org.vcell.optimization.ProfileSummaryData;

import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptimizationUtils;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.opt.Parameter;

@SuppressWarnings("serial")
public class AnalysisTableModel extends AbstractTableModel 
{
	public final static int NUM_ROWS = FRAPModel.NUM_MODEL_PARAMETERS_REACTION_OFF_RATE + 2; //add one row for immobile fraction and one for model significance
	public final static int NUM_COLUMNS = 10;//name + diff 1 model params+ CI +plot +diff 2 model params + CI +plot+koff model params +CI+plot
	public final static int INDEX_IMMOBILE_FRAC = 8;
	public final static String IMMOBILE_FRAC_NAME = "Immobile Fraction";
	public final static int INDEX_MODEL_SIGNIFICANCE = 9;
	public final static String MODEL_SIGNIFICANCE_NAME = "Model Identifiability";
	public final static int COLUMN_PARAM_NAME = 0;
	public final static int COLUMN_DIFF_ONE_PARAMETER_VAL = 1;
	public final static int COLUMN_DIFF_ONE_CI = 2;
	public final static int COLUMN_DIFF_ONE_CI_PLOT = 3;
	public final static int COLUMN_DIFF_TWO_PARAMETER_VAL = 4;
	public final static int COLUMN_DIFF_TWO_CI = 5;
	public final static int COLUMN_DIFF_TWO_CI_PLOT = 6;
	public final static int COLUMN_KOFF_PARAMETER_VAL = 7;
	public final static int COLUMN_KOFF_CI = 8;
	public final static int COLUMN_KOFF_CI_PLOT = 9;
	public final static String COL_LABELS[] = {"Parameter Name", "Value","Confidence Interval", "", "Value", "Confidence Interval", "","Value", "Confidence Interval", ""};
	public final static String STR_SIGNIFICANT = "";
	public final static String STR_NOT_SIGNIFICANT = "NOT IDENTIFIABLE";
	
	private FRAPSingleWorkspace frapWorkspace = null;
	private FRAPModel[] frapModels = null;
	private ProfileSummaryData[][] allProfileSumData = null;
	private int currentConfidenceLevelIndex = 0; 
	
    public int getColumnCount() {
        return NUM_COLUMNS;
    }

    public int getRowCount() {
       return NUM_ROWS;
    }

    public Object getValueAt(int row, int col) 
    {
    	if (col<0 || col>=NUM_COLUMNS){
    		throw new RuntimeException("AnalysisTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
    	}
    	if (row<0 || row>=NUM_ROWS){
    		throw new RuntimeException("AnalysisTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(NUM_ROWS-1)+"]");
    	}
    	
    	frapModels = frapWorkspace.getWorkingFrapStudy().getModels();
    	allProfileSumData = FRAPOptimizationUtils.getAllProfileSummaryData(frapWorkspace.getWorkingFrapStudy());
    	
    	if (frapModels == null)
    	{
    		return null;
    	}
    	if(col == COLUMN_PARAM_NAME)
    	{
    		if(row < FRAPModel.MODEL_PARAMETER_NAMES.length)
			{
    			return FRAPModel.MODEL_PARAMETER_NAMES[row];
			}
    		else if(row == INDEX_IMMOBILE_FRAC)
    		{
    			return IMMOBILE_FRAC_NAME;
    		}
    		else if(row == INDEX_MODEL_SIGNIFICANCE)
    		{
    			return MODEL_SIGNIFICANCE_NAME;
    		}
    	}
    	else if(col == COLUMN_DIFF_ONE_PARAMETER_VAL)
    	{
    		if(frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] != null && frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters() != null)
    		{
    			Parameter[] params = frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters();
    			if(row == INDEX_IMMOBILE_FRAC)
    			{
    				double ff = params[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess();
    				double fimm = 1-ff;
    				if(fimm < FRAPOptimizationUtils.epsilon && fimm > (0 - FRAPOptimizationUtils.epsilon))
    				{
    					fimm = 0;
    				}
    				if(fimm < (1+FRAPOptimizationUtils.epsilon) && fimm > (1 - FRAPOptimizationUtils.epsilon))
    				{
    					fimm = 1;
    				}
    				return fimm; 
    			}
    			else if(row == INDEX_MODEL_SIGNIFICANCE)
	    		{
    				if(allProfileSumData != null && allProfileSumData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT]!= null)
    	    		{
		    			for(int i=0; i<FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF; i++)
		    			{
		    				if(allProfileSumData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT][i] != null)
	    					{
			    				ConfidenceInterval[] intervals = allProfileSumData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT][i].getConfidenceIntervals();
			    				if(intervals[currentConfidenceLevelIndex].getUpperBound() == frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters()[i].getUpperBound() && 
			    				   intervals[currentConfidenceLevelIndex].getLowerBound() == frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters()[i].getLowerBound())
			    				{
			    					return STR_NOT_SIGNIFICANT;
			    				}
	    					}
		    			}
		    			return STR_SIGNIFICANT;
    	    		}
	    		}
    			else if(row < FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF)
    			{
    				return params[row].getInitialGuess();
    			}
    		}
    	}
    	else if(col == COLUMN_DIFF_ONE_CI)
    	{
    		//make sure diff_one model is selected
    		if(frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] != null && frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters() != null)
    		{
	    		if(allProfileSumData != null && allProfileSumData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT]!= null)
	    		{
		    		if(row < FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF && allProfileSumData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT][row] != null)
					{
		    			ConfidenceInterval[] intervals = allProfileSumData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT][row].getConfidenceIntervals();
		    			return intervals[currentConfidenceLevelIndex].toString();
					}
	    		}
    		}
    	}
    	else if(col == COLUMN_DIFF_TWO_PARAMETER_VAL)
    	{
    		if(frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] != null && frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters() != null)
    		{
    			Parameter[] params = frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters();
    			if(row == INDEX_IMMOBILE_FRAC)
    			{
    				double ff = params[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess();
    				double fc =params[FRAPModel.INDEX_SECONDARY_FRACTION].getInitialGuess();
    				double fimm = 1-ff-fc;
    				if(fimm < FRAPOptimizationUtils.epsilon && fimm > (0 - FRAPOptimizationUtils.epsilon))
    				{
    					fimm = 0;
    				}
    				if(fimm < (1+FRAPOptimizationUtils.epsilon) && fimm > (1 - FRAPOptimizationUtils.epsilon))
    				{
    					fimm = 1;
    				}
    				return fimm; 
    			}
    			else if(row == INDEX_MODEL_SIGNIFICANCE)
	    		{
    				if(allProfileSumData != null && allProfileSumData[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS]!= null)
    	    		{
		    			for(int i=0; i<FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF; i++)
		    			{
		    				if(allProfileSumData[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS][i] != null)
		    				{
			    				ConfidenceInterval[] intervals = allProfileSumData[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS][i].getConfidenceIntervals();
			    				if(intervals[currentConfidenceLevelIndex].getUpperBound() == frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters()[i].getUpperBound() && 
			    				   intervals[currentConfidenceLevelIndex].getLowerBound() == frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters()[i].getLowerBound())
			    				{
			    					return STR_NOT_SIGNIFICANT;
			    				}
		    				}
		    			}
		    			return STR_SIGNIFICANT;
    	    		}
	    		}
    			else if(row < FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF)
    			{
    				return params[row].getInitialGuess();
    			}
    		}
    	}
    	else if (col == COLUMN_DIFF_TWO_CI)
    	{
    		//make sure two diff model is selected
    		if(frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] != null && frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters() != null)
    		{
    			if(allProfileSumData != null && allProfileSumData[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS]!= null)
	    		{
		    		if(row < FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF && allProfileSumData[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS][row] != null)
					{
		    			ConfidenceInterval[] intervals = allProfileSumData[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS][row].getConfidenceIntervals();
		    			return intervals[currentConfidenceLevelIndex].toString();
					}
	    		}
    		}
    		
    	}
    	else if(col == COLUMN_KOFF_PARAMETER_VAL)
    	{
    		if(frapModels[FRAPModel.IDX_MODEL_REACTION_OFF_RATE] != null && frapModels[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].getModelParameters() != null)
    		{
    			Parameter[] params = frapModels[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].getModelParameters();
    			if(row == FRAPModel.INDEX_BLEACH_MONITOR_RATE || row == FRAPModel.INDEX_OFF_RATE)
    			{
    				return params[row].getInitialGuess(); 
    			}
    			else if(row == INDEX_MODEL_SIGNIFICANCE)
	    		{
    				if(allProfileSumData != null && allProfileSumData[FRAPModel.IDX_MODEL_REACTION_OFF_RATE]!= null)
    	    		{
    					ProfileSummaryData[] ciSummaryData = allProfileSumData[FRAPModel.IDX_MODEL_REACTION_OFF_RATE];
		    			for(int i=0; i<ciSummaryData.length; i++)
		    			{
		    				if(ciSummaryData[i] != null && ciSummaryData[i].getParamName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE]))
		    				{
		    					ConfidenceInterval[] bwmIntervals = allProfileSumData[FRAPModel.IDX_MODEL_REACTION_OFF_RATE][i].getConfidenceIntervals();
			    				if(bwmIntervals[currentConfidenceLevelIndex].getUpperBound() == frapModels[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].getModelParameters()[i].getUpperBound() && 
			    				   bwmIntervals[currentConfidenceLevelIndex].getLowerBound() == frapModels[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].getModelParameters()[i].getLowerBound())
			    				{
			    					return STR_NOT_SIGNIFICANT;
			    				}
		    				}
	    					else if(ciSummaryData[i] != null && ciSummaryData[i].getParamName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_OFF_RATE]))
	    					{
	    						ConfidenceInterval[] offRateIntervals = allProfileSumData[FRAPModel.IDX_MODEL_REACTION_OFF_RATE][i].getConfidenceIntervals();
			    				if(offRateIntervals[currentConfidenceLevelIndex].getUpperBound() == frapModels[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].getModelParameters()[i].getUpperBound() && 
			    				   offRateIntervals[currentConfidenceLevelIndex].getLowerBound() == frapModels[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].getModelParameters()[i].getLowerBound())
			    				{
			    					return STR_NOT_SIGNIFICANT;
			    				}
	    					}
		    			}
		    			return STR_SIGNIFICANT;
    	    		}
	    		}
    		}
    	}
    	else if (col == COLUMN_KOFF_CI)
    	{
    		//make sure two diff model is selected
    		if(frapModels[FRAPModel.IDX_MODEL_REACTION_OFF_RATE] != null && frapModels[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].getModelParameters() != null)
    		{
    			if(allProfileSumData != null && allProfileSumData[FRAPModel.IDX_MODEL_REACTION_OFF_RATE]!= null)
	    		{
    				ProfileSummaryData[] ciSummaryData = allProfileSumData[FRAPModel.IDX_MODEL_REACTION_OFF_RATE];
    				if(row == FRAPModel.INDEX_BLEACH_MONITOR_RATE)
    				{
		    			for(int i=0; i<ciSummaryData.length; i++)
		    			{
		    				if(ciSummaryData[i] != null && ciSummaryData[i].getParamName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE]))
		    				{
		    					ConfidenceInterval[] bwmIntervals = allProfileSumData[FRAPModel.IDX_MODEL_REACTION_OFF_RATE][i].getConfidenceIntervals();
			    				return bwmIntervals[currentConfidenceLevelIndex].toString();
		    				}
		    			}
    				}
    				if(row == FRAPModel.INDEX_OFF_RATE)
    				{
    					for(int i=0; i<ciSummaryData.length; i++)
		    			{
    						if(ciSummaryData[i] != null && ciSummaryData[i].getParamName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_OFF_RATE]))
	    					{
	    						ConfidenceInterval[] offRateIntervals = allProfileSumData[FRAPModel.IDX_MODEL_REACTION_OFF_RATE][i].getConfidenceIntervals();
			    				return offRateIntervals[currentConfidenceLevelIndex].toString();
	    					}
		    			}
    				}
	    		}
    		}
    	}
    	return null;
    }

    public Class<?> getColumnClass(int column) {
    	switch (column){
    		case COLUMN_PARAM_NAME:{
    			return String.class;
    		}
    		case COLUMN_DIFF_ONE_PARAMETER_VAL:{
    			return Double.class;
    		}
    		case COLUMN_DIFF_ONE_CI:{
    			return String.class;
        	}
    		case COLUMN_DIFF_ONE_CI_PLOT:{
    			return Object.class;
        	}
    		case COLUMN_DIFF_TWO_PARAMETER_VAL:{
    			return Double.class;
    		}
    		case COLUMN_DIFF_TWO_CI:{
    			return String.class;
        	}
    		case COLUMN_DIFF_TWO_CI_PLOT:{
    			return Object.class;
        	}
    		case COLUMN_KOFF_PARAMETER_VAL:{
    			return Double.class;
    		}
    		case COLUMN_KOFF_CI:{
    			return String.class;
        	}
    		case COLUMN_KOFF_CI_PLOT:{
    			return Object.class;
        	}
//    		case COLUMN_REAC_BINDING: {
//    			return Double.class;
//    		}
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

    public boolean isCellEditable(int row,int column) 
    {
    	if((column == COLUMN_DIFF_ONE_CI_PLOT && row < FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF) ||
    	   (column == COLUMN_DIFF_TWO_CI_PLOT && row < FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF) ||
    	   (column == COLUMN_KOFF_CI_PLOT && (row == FRAPModel.INDEX_BLEACH_MONITOR_RATE || row == FRAPModel.INDEX_OFF_RATE)))
    	{
    		return true;
    	}
    	return false;
    }

    public void setValueAt(Object aValue,int row, int column) 
    {
    }
     
    public FRAPSingleWorkspace getFrapWorkspace()
    {
        return frapWorkspace;
    }
   
    public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace)
    {
    	this.frapWorkspace = frapWorkspace;
    	fireTableDataChanged();
    }
     
    public void setCurrentConfidenceLevelIndex(int index)
    {
    	currentConfidenceLevelIndex = index;
    	fireTableDataChanged();
    }
    
}


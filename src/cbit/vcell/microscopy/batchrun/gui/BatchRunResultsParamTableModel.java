package cbit.vcell.microscopy.batchrun.gui;

import java.io.File;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import sun.security.action.GetBooleanAction;

import cbit.vcell.microscopy.DescriptiveStatistics;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptimization;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.opt.Parameter;
public class BatchRunResultsParamTableModel extends AbstractTableModel
{
	public final static int NUM_COLUMNS = 8;
//	public final static int INDEX_IMMOBILE_FRAC = 8;
//	public final static String IMMOBILE_FRAC_NAME = "Immobile Fraction";
	public final static int NUM_STATISTICS = 5;
	public final static int ROW_IDX_AVERAGE = 0;
	public final static int ROW_IDX_STD = 1;
	public final static int ROW_IDX_MEDIAN = 2;
	public final static int ROW_IDX_MIN = 3;
	public final static int ROW_IDX_MAX = 4;
	
	public final static int COLUMN_FILE_NAME = 0;
	public final static int COLUMN_PRI_DIFF_RATE = 1;
	public final static int COLUMN_PRI_MOBILE_FRACTION = 2;
	public final static int COLUMN_SEC_DIFF_RATE = 3;
	public final static int COLUMN_SEC_MOBILE_FRACTION = 4;
	public final static int COLUMN_BMR = 5;
//	public final static int COLUMN_ON_RATE = 6;
//	public final static int COLUMN_OFF_RATE = 7;
	public final static int COLUMN_IMMOBILE_FRACTION = 6;
	public final static int COLUMN_DETAILS = 7;
	
	public final static String COL_LABELS[] = { "File Name", "Primary Diff Rate", "Primary Mobile Fraction",
		                                        "Secondary Diff Rate", "Secondary Mobile Fraction",
		                                        "Bleach Monitor Rate", /*"Reaction On Rate", "Reaction Off Rate",*/
		                                        "Immobile Fraction", "Details"};
	
	private FRAPBatchRunWorkspace batchRunWorkspace = null;
	private ArrayList<FRAPStudy> frapStudys = null;
	
    public BatchRunResultsParamTableModel() {
    	super();
    }

    public int getColumnCount() {
        return NUM_COLUMNS;
    }

    public int getRowCount() {
       return (getBatchRunWorkspace() == null)? 0:getBatchRunWorkspace().getFrapStudyList().size()+NUM_STATISTICS;
    }

    public Object getValueAt(int row, int col) 
    {
    	if (col<0 || col>=NUM_COLUMNS){
    		throw new RuntimeException("AnalysisTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
    	}
    	    	
    	frapStudys = batchRunWorkspace.getFrapStudyList();
    	double stat[][] = getBatchRunWorkspace().getStatisticsData();
    	
    	if (frapStudys == null)
    	{
    		return null;
    	}
    	if(col == COLUMN_FILE_NAME)
    	{
    		if(row == ROW_IDX_AVERAGE)
    		{
    			return DescriptiveStatistics.MEAN_NAME;
    		}
    		else if(row == ROW_IDX_STD)
    		{
    			return DescriptiveStatistics.STANDARD_DEVIATION_NAME;
    		}
    		else if(row == ROW_IDX_MEDIAN)
    		{
    			return DescriptiveStatistics.MEDIAN_NAME;
    		}
    		else if(row == ROW_IDX_MIN)
    		{
    			return DescriptiveStatistics.MIN_NAME;
    		}
    		else if(row == ROW_IDX_MAX)
    		{
    			return DescriptiveStatistics.MAX_NAME;
    		}
    		else
    		{
    			return new File(frapStudys.get(row-NUM_STATISTICS).getXmlFilename()).getName();
    		}
    	}
    	else if(col == COLUMN_PRI_DIFF_RATE)
    	{
    		if(ROW_IDX_AVERAGE <= row && row <= ROW_IDX_MAX )
    		{
    			if(stat[row][col-1] != FRAPOptimization.largeNumber)
    			{
    				return stat[row][col-1];//stat array doesn't have name at the first column
    			}
    			else
    			{
    				return null;
    			}
    		}
    		else
    		{
    			Parameter[] modelParams = frapStudys.get(row-NUM_STATISTICS).getFrapModel(getBatchRunWorkspace().getSelectedModel()).getModelParameters();
    			return modelParams[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess();
    		}
    	}
    	else if(col == COLUMN_PRI_MOBILE_FRACTION)
    	{
    		if(ROW_IDX_AVERAGE <= row && row <= ROW_IDX_MAX )
    		{
    			if(stat[row][col-1] != FRAPOptimization.largeNumber)
    			{
    				return stat[row][col-1];//stat array doesn't have name at the first column
    			}
    			else
    			{
    				return null;
    			}
    		}
    		else
    		{
    			Parameter[] modelParams = frapStudys.get(row-NUM_STATISTICS).getFrapModel(getBatchRunWorkspace().getSelectedModel()).getModelParameters();
    			return modelParams[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess();
    		}
    	}
    	else if(col == COLUMN_SEC_DIFF_RATE)
    	{
    		if(getBatchRunWorkspace().getSelectedModel() != FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
    		{
    			return null;
    		}
    		if(ROW_IDX_AVERAGE <= row && row <= ROW_IDX_MAX )
    		{
    			if(stat[row][col-1] != FRAPOptimization.largeNumber)
    			{
    				return stat[row][col-1];//stat array doesn't have name at the first column
    			}
    			else
    			{
    				return null;
    			}
    		}
    		else
    		{
    			Parameter[] modelParams = frapStudys.get(row-NUM_STATISTICS).getFrapModel(getBatchRunWorkspace().getSelectedModel()).getModelParameters();
    			return modelParams[FRAPModel.INDEX_SECONDARY_DIFF_RATE].getInitialGuess();
    		}
    	}
    	else if(col == COLUMN_SEC_MOBILE_FRACTION)
    	{
    		if(getBatchRunWorkspace().getSelectedModel() != FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
    		{
    			return null;
    		}
    		if(ROW_IDX_AVERAGE <= row && row <= ROW_IDX_MAX )
    		{
    			if(stat[row][col-1] != FRAPOptimization.largeNumber)
    			{
    				return stat[row][col-1];//stat array doesn't have name at the first column
    			}
    			else
    			{
    				return null;
    			}
    		}
    		else
    		{
    			Parameter[] modelParams = frapStudys.get(row-NUM_STATISTICS).getFrapModel(getBatchRunWorkspace().getSelectedModel()).getModelParameters();
    			return modelParams[FRAPModel.INDEX_SECONDARY_FRACTION].getInitialGuess();
    		}
    	}
    	else if(col == COLUMN_BMR)
    	{
    		if(ROW_IDX_AVERAGE <= row && row <= ROW_IDX_MAX )
    		{
    			if(stat[row][col-1] != FRAPOptimization.largeNumber)
    			{
    				return stat[row][col-1];//stat array doesn't have name at the first column
    			}
    			else
    			{
    				return null;
    			}
    		}
    		else
    		{
    			Parameter[] modelParams = frapStudys.get(row-NUM_STATISTICS).getFrapModel(getBatchRunWorkspace().getSelectedModel()).getModelParameters();
    			return modelParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess();
    		}
    	}
    	else if(col == COLUMN_IMMOBILE_FRACTION)
    	{
    		if(ROW_IDX_AVERAGE <= row && row <= ROW_IDX_MAX )
    		{
    			if(stat[row][col-1] != FRAPOptimization.largeNumber)
    			{
    				return stat[row][col-1];//stat array doesn't have name at the first column
    			}
    			else
    			{
    				return null;
    			}
    		}
    		else
    		{
    			Parameter[] modelParams = frapStudys.get(row-NUM_STATISTICS).getFrapModel(getBatchRunWorkspace().getSelectedModel()).getModelParameters();
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
    	else if(col == COLUMN_DETAILS)
    	{
    		return "Details";
    	}
    	return null;
    }

    public Class<?> getColumnClass(int column) {
    	switch (column){
    		case COLUMN_FILE_NAME:{
    			return String.class;
    		}
    		case COLUMN_PRI_DIFF_RATE:{
    			return Double.class;
    		}
    		case COLUMN_PRI_MOBILE_FRACTION: {
    			return Double.class;
    		}
    		case COLUMN_SEC_DIFF_RATE: {
    			return Double.class;
    		}
    		case COLUMN_SEC_MOBILE_FRACTION: {
    			return Double.class;
    		}
    		case COLUMN_BMR: {
    			return Double.class;
    		}
    		case COLUMN_IMMOBILE_FRACTION: {
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



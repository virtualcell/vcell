package cbit.vcell.microscopy.batchrun.gui;

import java.io.File;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import cbit.vcell.microscopy.DescriptiveStatistics;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptimization;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.opt.Parameter;

public class BatchRunResultsStatTableModel extends AbstractTableModel 
{
	public final static int NUM_COLUMNS = 7;
	public final static int NUM_STATISTICS = 5;
	public final static int ROW_IDX_AVERAGE = 0;
	public final static int ROW_IDX_STD = 1;
	public final static int ROW_IDX_MEDIAN = 2;
	public final static int ROW_IDX_MIN = 3;
	public final static int ROW_IDX_MAX = 4;
	
	public final static int COLUMN_STAT_NAME = 0;
	public final static int COLUMN_PRI_DIFF_RATE = 1;
	public final static int COLUMN_PRI_MOBILE_FRACTION = 2;
	public final static int COLUMN_BMR = 3;
	public final static int COLUMN_SEC_DIFF_RATE = 4;
	public final static int COLUMN_SEC_MOBILE_FRACTION = 5;
	public final static int COLUMN_IMMOBILE_FRACTION = 6;
	
	public final static String COL_LABELS[] = { "Statistics", "Primary Diff Rate", "Primary Mobile Fraction",
												"Bleach Monitor Rate", "Secondary Mobile Fraction",
												"Secondary Diff Rate", "Immobile Fraction"};
	
	private FRAPBatchRunWorkspace batchRunWorkspace = null;
	private ArrayList<FRAPStudy> frapStudys = null;
	
    public BatchRunResultsStatTableModel() {
    	super();
    }

    public int getColumnCount() {
        return NUM_COLUMNS;
    }

    public int getRowCount() {
       return NUM_STATISTICS;
    }

    public Object getValueAt(int row, int col) 
    {
    	if (col<0 || col>=NUM_COLUMNS){
    		throw new RuntimeException("AnalysisTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
    	}
    	    	
    	frapStudys = batchRunWorkspace.getFrapStudies();
    	double stat[][] = getBatchRunWorkspace().getStatisticsData();
    	
    	if (frapStudys == null)//no frap studies, then no statisttics
    	{
    		return null;
    	}
    	if(col == COLUMN_STAT_NAME)
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
    	}
    	else if(col == COLUMN_PRI_DIFF_RATE)
    	{
    		if(ROW_IDX_AVERAGE <= row && row <= ROW_IDX_MAX )
    		{
    			if(stat[row][col-1] != FRAPOptimization.largeNumber)
    			{
    				return stat[row][col-1];//stat array doesn't have name at the first column
    			}
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
    		}
    	}
    	else if(col == COLUMN_SEC_DIFF_RATE)
    	{
    		if(getBatchRunWorkspace().getSelectedModel() == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
    		{
	    		if(ROW_IDX_AVERAGE <= row && row <= ROW_IDX_MAX )
	    		{
	    			if(stat[row][col-1] != FRAPOptimization.largeNumber)
	    			{
	    				return stat[row][col-1];//stat array doesn't have name at the first column
	    			}
	    		}
    		}
    	}
    	else if(col == COLUMN_SEC_MOBILE_FRACTION)
    	{
    		if(getBatchRunWorkspace().getSelectedModel() == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
    		{
    		
	    		if(ROW_IDX_AVERAGE <= row && row <= ROW_IDX_MAX )
	    		{
	    			if(stat[row][col-1] != FRAPOptimization.largeNumber)
	    			{
	    				return stat[row][col-1];//stat array doesn't have name at the first column
	    			}
	    		}
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
    		}
    	}
    	return null;
    }

    public Class<?> getColumnClass(int column) {
    	switch (column){
    		case COLUMN_STAT_NAME:{
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

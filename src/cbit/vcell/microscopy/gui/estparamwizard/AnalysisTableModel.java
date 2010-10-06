package cbit.vcell.microscopy.gui.estparamwizard;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import cbit.vcell.microscopy.ConfidenceInterval;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPOptimization;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.ProfileSummaryData;
import cbit.vcell.opt.Parameter;

public class AnalysisTableModel extends AbstractTableModel 
{
	public final static int NUM_ROWS = FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF + 2; //add one row for immobile fraction and one for model significance
	public final static int NUM_COLUMNS = 7;//name + diff 1 model params+ CI +plot +diff 2 model params + CI +plot
	public final static int INDEX_IMMOBILE_FRAC = 5;
	public final static String IMMOBILE_FRAC_NAME = "Immobile Fraction";
	public final static int INDEX_MODEL_SIGNIFICANCE = 6;
	public final static String MODEL_SIGNIFICANCE_NAME = "Model Identifiability";
	public final static int COLUMN_PARAM_NAME = 0;
	public final static int COLUMN_DIFF_ONE_PARAMETER_VAL = 1;
	public final static int COLUMN_DIFF_ONE_CI = 2;
	public final static int COLUMN_DIFF_ONE_CI_PLOT = 3;
	public final static int COLUMN_DIFF_TWO_PARAMETER_VAL = 4;
	public final static int COLUMN_DIFF_TWO_CI = 5;
	public final static int COLUMN_DIFF_TWO_CI_PLOT = 6;
	public final static String COL_LABELS[] = { "Parameter Name", "Value","Confidence Interval", "", "Value", "Confidence Interval", ""};
	public final static String STR_SIGNIFICANT = "";
	public final static String STR_NOT_SIGNIFICANT = "NOT IDENTIFIABLE";
	
	private FRAPSingleWorkspace frapWorkspace = null;
	private FRAPModel[] frapModels = null;
	private ProfileSummaryData[][] allProfileSumData = null;
	private int currentConfidenceLevelIndex = 0; 
	
    public AnalysisTableModel() {
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
    		throw new RuntimeException("AnalysisTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
    	}
    	if (row<0 || row>=NUM_ROWS){
    		throw new RuntimeException("AnalysisTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(NUM_ROWS-1)+"]");
    	}
    	
    	frapModels = frapWorkspace.getWorkingFrapStudy().getModels();
    	allProfileSumData = frapWorkspace.getWorkingFrapStudy().getFrapOptData().getAllProfileSummaryData();
    	
    	if (frapModels == null)
    	{
    		return null;
    	}
    	if(col == COLUMN_PARAM_NAME)
    	{
    		if(row == INDEX_IMMOBILE_FRAC)
    		{
    			return IMMOBILE_FRAC_NAME;
    		}
    		else if(row == INDEX_MODEL_SIGNIFICANCE)
    		{
    			return MODEL_SIGNIFICANCE_NAME;
    		}
    		else
    		{
    			return FRAPModel.MODEL_PARAMETER_NAMES[row];
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
    				if(fimm < FRAPOptimization.epsilon && fimm > (0 - FRAPOptimization.epsilon))
    				{
    					fimm = 0;
    				}
    				if(fimm < (1+FRAPOptimization.epsilon) && fimm > (1 - FRAPOptimization.epsilon))
    				{
    					fimm = 1;
    				}
    				return fimm; 
    			}
    			else if(row == INDEX_MODEL_SIGNIFICANCE)
	    		{
    				if(allProfileSumData != null && allProfileSumData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT]!= null && 
    				   allProfileSumData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT][0] != null)
    	    		{
		    			for(int i=0; i<FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF; i++)
		    			{
		    				ConfidenceInterval[] intervals = allProfileSumData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT][i].getConfidenceIntervals();
		    				if(intervals[currentConfidenceLevelIndex].getUpperBound() == frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters()[i].getUpperBound() && 
		    				   intervals[currentConfidenceLevelIndex].getLowerBound() == frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters()[i].getLowerBound())
		    				{
		    					return STR_NOT_SIGNIFICANT;
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
    		if(allProfileSumData != null && allProfileSumData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT]!= null &&
    		   allProfileSumData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT][0] != null)
    		{
	    		if(row < FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF)
				{
	    			ConfidenceInterval[] intervals = allProfileSumData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT][row].getConfidenceIntervals();
	    			return intervals[currentConfidenceLevelIndex].toString();
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
    				if(fimm < FRAPOptimization.epsilon && fimm > (0 - FRAPOptimization.epsilon))
    				{
    					fimm = 0;
    				}
    				if(fimm < (1+FRAPOptimization.epsilon) && fimm > (1 - FRAPOptimization.epsilon))
    				{
    					fimm = 1;
    				}
    				return fimm; 
    			}
    			else if(row == INDEX_MODEL_SIGNIFICANCE)
	    		{
    				if(allProfileSumData != null && allProfileSumData[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS]!= null && 
    				   allProfileSumData[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS][0] != null)
    	    		{
		    			for(int i=0; i<FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF; i++)
		    			{
		    				ConfidenceInterval[] intervals = allProfileSumData[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS][i].getConfidenceIntervals();
		    				if(intervals[currentConfidenceLevelIndex].getUpperBound() == frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters()[i].getUpperBound() && 
		    				   intervals[currentConfidenceLevelIndex].getLowerBound() == frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters()[i].getLowerBound())
		    				{
		    					return STR_NOT_SIGNIFICANT;
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
    		if(allProfileSumData != null && allProfileSumData[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS]!= null && 
    		   allProfileSumData[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS][0] != null)
    		{
	    		if(row < FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF)
				{
	    			ConfidenceInterval[] intervals = allProfileSumData[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS][row].getConfidenceIntervals();
	    			return intervals[currentConfidenceLevelIndex].toString();
				}
    		}
    	}
//    	else if(col == COLUMN_DIFF_ONE_CI_PLOT || col == COLUMN_DIFF_TWO_CI_PLOT)
//    	{
//    		return "Plot";
//    	}
//    	else if(col == COLUMN_REAC_BINDING)
//    	{
//    		if(frapModels[FRAPModel.IDX_MODEL_DIFF_BINDING] == null || frapModels[FRAPModel.IDX_MODEL_DIFF_BINDING].getModelParameters() == null)
//    		{
//    			return null;
//    		}
//    		else
//    		{
//    			Parameter[] params = frapModels[FRAPModel.IDX_MODEL_DIFF_BINDING].getModelParameters();
//    			if(row == INDEX_IMMOBILE_FRAC)
//    			{
//    				double ff = params[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess();
//    				double fc =params[FRAPModel.INDEX_SECONDARY_FRACTION].getInitialGuess();
//    				double fimm = 1-ff-fc;
//    				if(fimm < FRAPOptimization.epsilon && fimm > (0 - FRAPOptimization.epsilon))
//    				{
//    					fimm = 0;
//    				}
//    				if(fimm < (1+FRAPOptimization.epsilon) && fimm > (1 - FRAPOptimization.epsilon))
//    				{
//    					fimm = 1;
//    				}
//    				return fimm; 
//    			}
//    			else if(row < FRAPModel.NUM_MODEL_PARAMETERS_BINDING)
//    			{
//    				return params[row].getInitialGuess();
//    			}
//    			else
//    			{
//    				return null;
//    			}
//    		}
//    	}
    	
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
    	   (column == COLUMN_DIFF_TWO_CI_PLOT && row < FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF))
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


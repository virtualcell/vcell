package cbit.vcell.microscopy.gui.estparamwizard;

import javax.swing.table.AbstractTableModel;

import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptimization;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.opt.Parameter;

public class AnalysisTableModel extends AbstractTableModel 
{
	public final static int NUM_ROWS = FRAPModel.NUM_MODEL_PARAMETERS_BINDING +1; //add one more row for immobile fraction
	public final static int NUM_COLUMNS = 4;
	public final static int INDEX_IMMOBILE_FRAC = 8;
	public final static String IMMOBILE_FRAC_NAME = "Immobile Fraction";
	public final static int COLUMN_PARAM_NAME = 0;
	public final static int COLUMN_DIFF_ONE = 1;
	public final static int COLUMN_DIFF_TWO = 2;
	public final static int COLUMN_REAC_BINDING = 3;
	public final static String COL_LABELS[] = { "Parameter Name", "Diffusion with 1 Diff. component", "Diffusiton with 2 Diff. components", "Diffusion with binding reaction"};
	
	private FRAPWorkspace frapWorkspace = null;
	private FRAPModel[] frapModels = null;
	
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
    	
    	frapModels = frapWorkspace.getFrapStudy().getModels();
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
    		else
    		{
    			return FRAPModel.MODEL_PARAMETER_NAMES[row];
    		}
    	}
    	else if(col == COLUMN_DIFF_ONE)
    	{
    		if(frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters() == null)
    		{
    			return null;
    		}
    		else
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
    			else if(row < FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF)
    			{
    				return params[row].getInitialGuess();
    			}
    			else
    			{
    				return null;
    			}
    		}
    	}
    	else if(col == COLUMN_DIFF_TWO)
    	{
    		if(frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters() == null)
    		{
    			return null;
    		}
    		else
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
    			else if(row < FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF)
    			{
    				return params[row].getInitialGuess();
    			}
    			else
    			{
    				return null;
    			}
    		}
    	}
    	else if(col == COLUMN_REAC_BINDING)
    	{
    		if(frapModels[FRAPModel.IDX_MODEL_DIFF_BINDING].getModelParameters() == null)
    		{
    			return null;
    		}
    		else
    		{
    			Parameter[] params = frapModels[FRAPModel.IDX_MODEL_DIFF_BINDING].getModelParameters();
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
    			else if(row < FRAPModel.NUM_MODEL_PARAMETERS_BINDING)
    			{
    				return params[row].getInitialGuess();
    			}
    			else
    			{
    				return null;
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
    		case COLUMN_DIFF_ONE:{
    			return Double.class;
    		}
    		case COLUMN_DIFF_TWO: {
    			return Double.class;
    		}
    		case COLUMN_REAC_BINDING: {
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
     
    public FRAPWorkspace getFrapWorkspace()
    {
        return frapWorkspace;
    }
   
    public void setFrapWorkspace(FRAPWorkspace frapWorkspace)
    {
    	this.frapWorkspace = frapWorkspace;
    	fireTableDataChanged();
    }
     
}


package cbit.vcell.microscopy.gui.estparamwizard;

import javax.swing.JLabel;
import javax.swing.table.AbstractTableModel;

import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptimization;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPSingleWorkspace;

public class MSETableModel extends AbstractTableModel {

	public final static String COL_LABELS[] = { "Model Name", "ROI_Bleached", "ROI_Ring1", "ROI_Ring2", "ROI_Ring3",
												"ROI_Ring4", "ROI_Ring5", "ROI_Ring6", "ROI_Ring7", "ROI_Ring8", "Sum Of Error"};
	public final static int NUM_ROWS = FRAPModel.NUM_MODEL_TYPES-1;//removed reaction binding
	public final static int NUM_COLUMNS = COL_LABELS.length;
	public final static int COLUMN_MODEL_NAME = 0;
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
	private FRAPSingleWorkspace frapWorkspace = null;
	
    public MSETableModel() {
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
    	mseSummaryData = getFrapWorkspace().getWorkingFrapStudy().getAnalysisMSESummaryData();
    	
    	if (col<0 || col>=NUM_COLUMNS){
    		throw new RuntimeException("MSETableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
    	}
    	if (row<0 || row>=NUM_ROWS){
    		throw new RuntimeException("MSETableModel.getValueAt(), row = "+row+" out of range ["+0+","+(NUM_ROWS-1)+"]");
    	}
    	if(mseSummaryData == null)
    	{
    		return null;
    	}
    	if(col == COLUMN_MODEL_NAME)
    	{
    		return FRAPModel.MODEL_TYPE_ARRAY[row];
    	}
    	else
    	{
    		//in mseSummaryData there is no model name col, therefore we use col-1 here.
    		if(mseSummaryData[row][col-1] != FRAPOptimization.largeNumber)
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
    	if(column == COLUMN_MODEL_NAME)
    	{
    		return String.class;
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
    
    public FRAPSingleWorkspace getFrapWorkspace()
    {
        return frapWorkspace;
    }
   
    public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace)
    {
    	this.frapWorkspace = frapWorkspace;
    	fireTableDataChanged();
    }
}
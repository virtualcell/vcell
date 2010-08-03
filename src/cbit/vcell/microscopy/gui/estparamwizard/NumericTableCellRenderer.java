package cbit.vcell.microscopy.gui.estparamwizard;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import javax.swing.*;

import cbit.vcell.microscopy.DescriptiveStatistics;
import cbit.vcell.microscopy.EstimatedParameterTableModel;
import cbit.vcell.microscopy.batchrun.gui.BatchRunResultsParamTableModel;

import java.awt.*;
import java.text.NumberFormat;

/**
 */
public class NumericTableCellRenderer  extends DefaultTableCellRenderer {
	
	private NumberFormat format;
    
    public NumericTableCellRenderer(int precision) {
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
			setBackground(new Color(238,238,238)); //light gray
			setForeground(new Color(238,238,238));
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
    	
    	//the following code is to highlight the entire row of the statistics in batchrun parameters table
    	Object nameObj = table.getValueAt(row, BatchRunResultsParamTableModel.COLUMN_FILE_NAME); //first column should be name of the parameter
		if(nameObj instanceof String)
		{
			String name = (String)nameObj;
			if(name.equals(DescriptiveStatistics.MEAN_NAME) ||
			   name.equals(DescriptiveStatistics.STANDARD_DEVIATION_NAME))
			{
				
				if (isSelected) {
	    			setBackground(table.getSelectionBackground());
	    		} else {
	    			setBackground( new Color(255,255,128));//yellow
	    		}
				setForeground(Color.black);
			}
		}
    	
        return this;
    }
}
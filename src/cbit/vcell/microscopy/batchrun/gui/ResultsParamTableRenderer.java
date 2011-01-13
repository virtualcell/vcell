package cbit.vcell.microscopy.batchrun.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.util.DescriptiveStatistics;


@SuppressWarnings("serial")
public class ResultsParamTableRenderer extends DefaultTableCellRenderer
{
	private JButton button = null;
	public ResultsParamTableRenderer()
	{
		button = new JButton("Details...");
		button.setVerticalTextPosition(SwingConstants.CENTER); 
		button.setHorizontalTextPosition(SwingConstants.LEFT);
		button.setBorderPainted(false);
        setFont(new Font("Arial", Font.PLAIN, 11));
	}
	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) 
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 1));
		if(column == BatchRunResultsParamTableModel.COLUMN_FILE_NAME)
		{
			if(value instanceof String)
			{
				setText((String)value);
			}
			else if(value instanceof File)
			{
				String fileName = ((File)value).getName();
				if(fileName.indexOf(".") > 0)
				{
					setText(fileName.substring(0,fileName.indexOf(".")));
				}
				else
				{
					setText(fileName);
				}
			}
			if(value != null)
	        {
	        	setToolTipText(value.toString());
	        }
		}
		else if(column == BatchRunResultsParamTableModel.COLUMN_DETAILS)
		{
			Object firstColStr = table.getValueAt(row, BatchRunResultsParamTableModel.COLUMN_FILE_NAME); 
			if((firstColStr instanceof String) && (firstColStr.equals(DescriptiveStatistics.MEAN_NAME)||
			   firstColStr.equals(DescriptiveStatistics.MEDIAN_NAME)||
			   firstColStr.equals(DescriptiveStatistics.MODE_NAME)||
			   firstColStr.equals(DescriptiveStatistics.MIN_NAME)||
			   firstColStr.equals(DescriptiveStatistics.MAX_NAME)||
			   firstColStr.equals(DescriptiveStatistics.STANDARD_DEVIATION_NAME)))
			{
				return null;
			}
			else
			{
				button.setBorderPainted(false);
				return button;
			}
		}
		
		Object nameObj = table.getValueAt(row, BatchRunResultsParamTableModel.COLUMN_FILE_NAME); //first column should be name of the parameter
		if(nameObj instanceof String)
		{
			String name = (String)nameObj;
			if(name.equals(DescriptiveStatistics.MEAN_NAME) ||
			   name.equals(DescriptiveStatistics.STANDARD_DEVIATION_NAME)/* ||
			   name.equals(DescriptiveStatistics.MEDIAN_NAME) ||
			   name.equals(DescriptiveStatistics.MIN_NAME) ||
			   name.equals(DescriptiveStatistics.MAX_NAME)*/)
			{
				
				if (isSelected) {
	    			setBackground(table.getSelectionBackground());
	    		} else {
	    			setBackground( new Color(255,255,128));//yellow
	    		}
				setForeground(Color.black);
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
		}
		
		return this;
	}
}

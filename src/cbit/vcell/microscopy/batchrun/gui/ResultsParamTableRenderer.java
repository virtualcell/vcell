package cbit.vcell.microscopy.batchrun.gui;

import java.awt.Component;
import java.awt.Font;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import cbit.vcell.microscopy.DescriptiveStatistics;
import cbit.vcell.microscopy.gui.estparamwizard.HyperLinkLabel;

public class ResultsParamTableRenderer extends DefaultTableCellRenderer
{
	private JButton button = new JButton("Details...");
	public ResultsParamTableRenderer()
	{
		button.setBorderPainted(false);
        setFont(new Font("Arial", Font.PLAIN, 11));
        setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 1));
//		button.setBorder(new )
	}
	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) 
	{
		if(column == BatchRunResultsParamTableModel.COLUMN_FILE_NAME)
		{
			if(value instanceof String)
			{
				setText((String)value);
			}
			else if(value instanceof File)
			{
				setText(((File)value).getName());
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
		
		return this;
	}
}

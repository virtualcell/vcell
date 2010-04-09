package cbit.vcell.microscopy.batchrun.gui;

import java.awt.Component;

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
//		button.setBorder(new )
	}
	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) 
	{
		String firstColStr = (String)table.getValueAt(row, BatchRunResultsParamTableModel.COLUMN_FILE_NAME); 
		if(firstColStr.equals(DescriptiveStatistics.MEAN_NAME)||
		   firstColStr.equals(DescriptiveStatistics.MEDIAN_NAME)||
		   firstColStr.equals(DescriptiveStatistics.MODE_NAME)||
		   firstColStr.equals(DescriptiveStatistics.MIN_NAME)||
		   firstColStr.equals(DescriptiveStatistics.MAX_NAME)||
		   firstColStr.equals(DescriptiveStatistics.STANDARD_DEVIATION_NAME))
		{
			if(column == BatchRunResultsParamTableModel.COLUMN_DETAILS)
			{
				return null;
			}
		}
		else
		{
			button.setBorderPainted(false);
			if(column == BatchRunResultsParamTableModel.COLUMN_DETAILS)
			{
				return button;
			}
		}
		return this;
	}
}

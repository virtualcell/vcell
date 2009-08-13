package cbit.vcell.microscopy;
import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import cbit.vcell.microscopy.gui.estparamwizard.FRAPReacDiffEstimationGuidePanel;
//this cell renderer is used for table in FRAPReacDiffEstimationGuidPanel.
//it has two functionalities 1) precision renderer for Double column 2)highlight estimated parameters in row
public class EstimatedParameterTableRenderer extends DefaultTableCellRenderer
{
	private NumberFormat format;
    
    public EstimatedParameterTableRenderer(int precision) {
        format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(precision);
        format.setMinimumFractionDigits(precision);
    }
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		//  set whole row highlighted if it is newly calculated.
		Object nameObj = table.getValueAt(row, EstimatedParameterTableModel.COLUMN_NAME); //first column should be name of the parameter
		if(nameObj instanceof String)
		{
			String name = (String)nameObj;
			if(name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_FreePartDiffRate]) ||
			   name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_FreePartFraction]) ||
			   name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_ComplexDiffRate]) ||
			   name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_ComplexFraction]) ||
			   name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_BleachMonitorRate]) ||
			   name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_BSConc]) ||
			   name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_ReacOnRate]) ||
			   name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_ReacOffRate]))
			{
				setBackground( new Color(255,255,128));
				setForeground(Color.black);
			}
			else
			{
				setBackground(table.getBackground());
				setForeground(Color.black);
			}
		}
		if(column == EstimatedParameterTableModel.COLUMN_VALUE)
		{
			setText(format.format(value));
		}
		return this;
	}
}


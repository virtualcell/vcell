package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableModel;

import cbit.plot.Plot2D;
import cbit.plot.PlotPane;
import cbit.vcell.microscopy.ProfileSummaryData;
import cbit.vcell.microscopy.gui.AdvancedTablePanel;

public class ConfidenceIntervalPlotPanel extends JPanel
{
	ProfileSummaryData summaryData = null;
	PlotPane plotPane = null;
	StyleTable intervalTable = null;	
	ConfidenceIntervalTableModel tableModel = null;
	AdvancedTablePanel tablePanel = null;
	
	public ConfidenceIntervalPlotPanel()
	{
		setLayout(new BorderLayout());
		add(getPlotPane(), BorderLayout.CENTER);
		add(getTablePanel(), BorderLayout.SOUTH);
	}
	
	private PlotPane getPlotPane()
	{
		if(plotPane == null)
		{
			plotPane = new PlotPane();
			plotPane.setBackground(Color.white);
		}
		return plotPane;
	}
	
	private StyleTable getIntervalTable()
	{
		if(intervalTable == null)
		{
			tableModel = new ConfidenceIntervalTableModel();
			intervalTable = new StyleTable(tableModel);
			//set numeic renderer to numeric table cell 
			intervalTable.getColumnModel().getColumn(ConfidenceIntervalTableModel.COLUMN_PARAMETER_VAL).setCellRenderer(new NumericTableCellRenderer(5));
			
		}
		return intervalTable;
	}
	
	private AdvancedTablePanel getTablePanel()
	{
		if(tablePanel == null)
		{
			tablePanel = new AdvancedTablePanel();
			tablePanel.setLayout(new BorderLayout());
			tablePanel.setTable(getIntervalTable());
			tablePanel.add(getIntervalTable().getTableHeader(),BorderLayout.NORTH);
			tablePanel.add(getIntervalTable(),BorderLayout.SOUTH);
		}
		return tablePanel;
	}
	
	public void setProfileSummaryData(ProfileSummaryData summaryData)
	{
		this.summaryData = summaryData;
		plotPane.setPlot2D(summaryData.getPlot2D());
		tableModel.setProfileSummaryData(summaryData);
	}
}

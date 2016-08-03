/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.data;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumn;

import org.vcell.util.document.VCDataIdentifier;

import cbit.plot.Plot2D;
import cbit.plot.PlotData;
import cbit.plot.SingleXPlot2D;
import cbit.plot.gui.PlotPane;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.DataManager;
import cbit.vcell.simdata.ODEDataManager;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESolverResultSet;
/**
 * Insert the type's description here.
 * Creation date: (10/17/2005 11:22:58 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class ODETimePlotMultipleScansPanel extends JPanel {
	private static final String JOB_PLOT_NAME = "Set";
	private Simulation simulation = null;
	private DataManager dataManager = null;
	private JTable scanChoiceTable = null;
	private PlotPane plotPane = null;
	private String[] variableNames = null;

/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 11:30:45 PM)
 * @param simulation cbit.vcell.solver.Simulation
 * @param vcDataManager cbit.vcell.client.server.VCDataManager
 */
public ODETimePlotMultipleScansPanel(String[] varnames, Simulation arg_simulation, DataManager arg_dataManager) {
	variableNames = varnames;
	simulation = arg_simulation;
	this.dataManager = arg_dataManager;
	initialize();
}

/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 11:37:52 PM)
 * @exception org.vcell.util.DataAccessException The exception description.
 */
private void initialize()  {

	setLayout(new BorderLayout());
	int scanCount = simulation.getScanCount();
	
	plotPane = new PlotPane();
	add(plotPane, BorderLayout.CENTER);
	
	JPanel parameterPanel = new JPanel();
	parameterPanel.setLayout(new BorderLayout());
	
	JLabel label = new JLabel();
	label.setHorizontalAlignment(SwingConstants.CENTER);
	label.setText("<html><b><u>Choose one or more Parameter Value Sets </u></b></html>");
	label.setBorder(BorderFactory.createEmptyBorder(10, 4, 10, 4));
	
	parameterPanel.add(label, BorderLayout.NORTH);
	
	String[] scanParams = simulation.getMathOverrides().getScannedConstantNames();
	Arrays.sort(scanParams);
	
	class ScanChoicesTableModel extends javax.swing.table.AbstractTableModel {
		String[] columnNames;
		Double[][] rowData;
		ScanChoicesTableModel(Double[][] argData, String[] argNames) {
			columnNames = argNames;
			rowData = argData;
		}
		public String getColumnName(int column) {
			if (column == 0) {
				return JOB_PLOT_NAME;
			}
			return columnNames[column - 1].toString(); 
		}
		public int getRowCount() { 
			return rowData.length; 
		}
		public int getColumnCount() { 
			return columnNames.length + 1; 
		}

		public Object getValueAt(int row, int col) { 
			if (col == 0) {
				return new Integer(row);
			}
			return rowData[row][col - 1]; 
		}
		@Override
		public boolean isCellEditable(int row, int column) { 
			return false; 
		}
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return Number.class;
		}
	};
	
	Double[][] values = new Double[scanCount][scanParams.length];
	for (int i = 0; i < scanCount; i ++) {
		for (int j = 0; j < scanParams.length; j ++){
		Expression scanConstantExp = simulation.getMathOverrides().getActualExpression(scanParams[j], i);
			try {
				values[i][j] = scanConstantExp.evaluateConstant();
			} catch (DivideByZeroException e1) {
				e1.printStackTrace();
			} catch (ExpressionException e1) {
				e1.printStackTrace();
			}
		}
	}

	ScanChoicesTableModel tm = new ScanChoicesTableModel(values, scanParams);
	scanChoiceTable = new JTable(tm);
	
	scanChoiceTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	JScrollPane scr = new JScrollPane(scanChoiceTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	scr.setPreferredSize(new java.awt.Dimension (100, Math.min(150, scanChoiceTable.getPreferredSize().height 
			+ scanChoiceTable.getTableHeader().getPreferredSize().height + 5)));	
	parameterPanel.add(scr, BorderLayout.CENTER);
	parameterPanel.setBorder(BorderFactory.createEtchedBorder());
	TableColumn col = scanChoiceTable.getColumnModel().getColumn(0);
	int width = 50;
	col.setMinWidth(width); 
	col.setMaxWidth(width); 
	col.setPreferredWidth(width); 

	// put things together
	add(parameterPanel, BorderLayout.SOUTH);
	
	scanChoiceTable.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				updateScanParamChoices();
			}
		}
	});
	//scanChoiceTable.getSelectionModel().setSelectionInterval(0,0);
}

/**
 * Insert the method's description here.
 * Creation date: (10/18/2005 12:44:06 AM)
 */
private void updateScanParamChoices(){	
	AsynchClientTask task1 = new AsynchClientTask("get ode results", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			int[] jobIndexes = scanChoiceTable.getSelectedRows();
			VCSimulationIdentifier vcSimulationIdentifier = simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
			int plotCount = jobIndexes.length * variableNames.length;
			SymbolTableEntry[] symbolTableEntries = new SymbolTableEntry[plotCount];
			double[][] dataValues = new double[plotCount+1][];
			PlotData[] plotDatas = new PlotData[plotCount];
			String[] plotNames = new String[plotCount];
			int plotIndex = 0;
			dataValues[0] = null;
			for (int ji = 0; ji < jobIndexes.length; ji ++) {
				int jobIndex = jobIndexes[ji];
				final VCDataIdentifier vcdid = new VCSimulationDataIdentifier(vcSimulationIdentifier, jobIndex);
				ODEDataManager odeDatamanager = ((ODEDataManager)dataManager).createNewODEDataManager(vcdid);
				ODESolverResultSet odeSolverResultSet = odeDatamanager.getODESolverResultSet();
				int tcol = odeSolverResultSet.findColumn(ReservedVariable.TIME.getName());
				double[] tdata = odeSolverResultSet.extractColumn(tcol);
				if (dataValues[0] == null) {
					dataValues[0] = tdata;
				}
				for (int v = 0; v < variableNames.length; v ++) {
					String varname = variableNames[v];
					int varcol = odeSolverResultSet.findColumn(varname);
					double[] vdata = odeSolverResultSet.extractColumn(varcol);
					dataValues[plotIndex+1] = vdata;
					plotNames[plotIndex] = varname + " -- " + JOB_PLOT_NAME + " " + jobIndex;
					plotDatas[plotIndex] = new PlotData(tdata, vdata);
					symbolTableEntries[plotIndex] = simulation.getMathDescription().getVariable(varname);
					plotIndex ++;
				}
			}
			hashTable.put("dataValues", dataValues);
			hashTable.put("plotDatas", plotDatas);
			hashTable.put("plotNames", plotNames);
			hashTable.put("symbolTableEntries", symbolTableEntries);
		}
	};
	AsynchClientTask task2 = new AsynchClientTask("show results", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {	
			double[][] dataValues = (double[][])hashTable.get("dataValues");
			PlotData[] plotDatas = (PlotData[])hashTable.get("plotDatas");
			String[] plotNames = (String[])hashTable.get("plotNames");
			SymbolTableEntry[] symbolTableEntries = (SymbolTableEntry[])hashTable.get("symbolTableEntries");
			if (plotDatas == null || plotDatas.length == 0 || (plotDatas.length==1 && plotDatas[0] == null) || plotNames == null) {
				plotPane.setPlot2D(null);
				return;
			}
			Plot2D plot2D = null;
			if(simulation.getSolverTaskDescription().getOutputTimeSpec() instanceof DefaultOutputTimeSpec)
			{
				plot2D = new Plot2D(symbolTableEntries, plotNames, plotDatas, 
						new String[] {"Time Plot", ReservedVariable.TIME.getName(), ""});
			}
			else
			{
				plot2D = new SingleXPlot2D(symbolTableEntries, ReservedVariable.TIME.getName(), plotNames, dataValues);
			}
							
			plotPane.setPlot2D(plot2D);
		}
	};
	ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2});
}
}

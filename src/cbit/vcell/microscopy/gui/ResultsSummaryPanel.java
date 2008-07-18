package cbit.vcell.microscopy.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JLabel;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import cbit.gui.DialogUtils;
import cbit.gui.SimpleTransferable;
import cbit.util.BeanUtils;
import cbit.util.Range;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.ROI;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;
import cbit.vcell.microscopy.ROI.RoiType;
import cbit.vcell.microscopy.gui.FRAPInterpolationPanel;

public class ResultsSummaryPanel extends JPanel {
	
	private FRAPStudyPanel.NewFRAPFromParameters newFRAPFromParameters;
	
	private final JRadioButton plotFRAPSimResultsRadioButton;
	private final JRadioButton plotDerivedSimResultsRadioButton;
	private ButtonGroup plotButtonGroup = new ButtonGroup();
	
	private final FRAPInterpolationPanel interpolationPanel;
	private FRAPOptData frapOptData;
	
	private final JScrollPane scrollPane;
	private JTable table;
	private MultisourcePlotPane multisourcePlotPane;
	private Hashtable<Double, DataSource[]> allDataHash;
	private Object[][] summaryData;
	
	private JPopupMenu jPopupMenu = new JPopupMenu();
	private JMenuItem copyValueJMenuItem;
	private JMenuItem copyTimeDataJMenuItem;
	
	private static String[] summaryReportColumnNames =
		FRAPStudy.SpatialAnalysisResults.getSummaryReportColumnNames();
	
	private ActionListener plotButtonActionListener = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			if(plotFRAPSimResultsRadioButton.isSelected()){
				BeanUtils.enableComponents(interpolationPanel, false);
				BeanUtils.enableComponents(scrollPane, true);
				table.setEnabled(true);
				processTableSelection();
			}else if(plotDerivedSimResultsRadioButton.isSelected()){
				BeanUtils.enableComponents(interpolationPanel, true);
				BeanUtils.enableComponents(scrollPane, false);
				table.invalidate();
				scrollPane.revalidate();
				plotDerivedSimulationResults();
			}
		}
	};
	
	public ResultsSummaryPanel() {
		super();
		JMenuItem copyReportJMenuItem = new JMenuItem("Copy Summary Report");
		copyReportJMenuItem.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					SimpleTransferable.sendToClipboard(
						FRAPStudy.SpatialAnalysisResults.createCSVSummaryReport(
							summaryReportColumnNames, summaryData));
				}
			}
		);
		copyValueJMenuItem = new JMenuItem("Copy Value");
		copyValueJMenuItem.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					SimpleTransferable.sendToClipboard(""+table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()));
				}
			}
		);
		
		copyTimeDataJMenuItem = new JMenuItem("Copy Time Data");
		copyTimeDataJMenuItem.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					try{
						Double selectedDiffusionRate =
							(Double)table.getValueAt(table.getSelectedRow(),
							FRAPStudy.SpatialAnalysisResults.COLUMN_INDEX_DIFFUSION_RATE);
						DataSource[] selectedRowDataSourceArr = allDataHash.get(selectedDiffusionRate);
						ReferenceData expDataSource =
							(ReferenceData)selectedRowDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE].getSource();
						ODESolverResultSet simDataSource =
							(ODESolverResultSet)selectedRowDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_SIMDATASOURCE].getSource();
						double[] expTimes = expDataSource.getColumnData(0);
						double[] expColumnData = expDataSource.getColumnData(table.getSelectedColumn());
						double[] simTimes = simDataSource.extractColumn(0);
						double[] simColumnData = simDataSource.extractColumn(table.getSelectedColumn());
						
						SimpleTransferable.sendToClipboard(
								FRAPStudy.SpatialAnalysisResults.createCSVTimeData(
									summaryReportColumnNames,table.getSelectedColumn(),
									expTimes,expColumnData,simTimes,simColumnData
						));
					}catch(Exception e2){
						e2.printStackTrace();
						DialogUtils.showErrorDialog(
							"Erro copying Time Data for row="+table.getSelectedRow()+", col="+table.getSelectedColumn()+".  "+e2.getMessage());
					}
				}
			}
		);

		jPopupMenu.add(copyValueJMenuItem);
		jPopupMenu.add(copyReportJMenuItem);
		jPopupMenu.add(copyTimeDataJMenuItem);
		
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {7,7,7,0,7};
		gridBagLayout.columnWidths = new int[] {7};
		setLayout(gridBagLayout);

		plotFRAPSimResultsRadioButton = new JRadioButton();
		plotFRAPSimResultsRadioButton.addActionListener(plotButtonActionListener);
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.gridy = 0;
		gridBagConstraints_7.gridx = 0;
		add(plotFRAPSimResultsRadioButton, gridBagConstraints_7);

		final JLabel diffusionRateAndLabel = new JLabel();
		diffusionRateAndLabel.setFont(new Font("", Font.BOLD, 14));
		diffusionRateAndLabel.setText("Plot FRAP SImulation Results");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.anchor = GridBagConstraints.WEST;
		gridBagConstraints_3.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_3.gridy = 0;
		gridBagConstraints_3.gridx = 1;
		add(diffusionRateAndLabel, gridBagConstraints_3);

		final JLabel standardErrorseLabel = new JLabel();
		standardErrorseLabel.setFont(new Font("", Font.PLAIN, 14));
		standardErrorseLabel.setText("FRAP Simulation Summary: Standard Error (SE) including all Times of Normalized ROI Average  (Experimental vs. Simulation Data)");
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.gridy = 1;
		gridBagConstraints_9.gridx = 1;
		add(standardErrorseLabel, gridBagConstraints_9);

		scrollPane = new JScrollPane();
		scrollPane.setMinimumSize(new Dimension(0, 100));
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridx = 1;
		add(scrollPane, gridBagConstraints);

		table = new JTable();
		table.addMouseListener(
				new MouseAdapter(){
					@Override
					public void mousePressed(MouseEvent e) {
						super.mousePressed(e);
						showPopupMenu(e);
					}
					@Override
					public void mouseReleased(MouseEvent e) {
						super.mouseReleased(e);
						showPopupMenu(e);
					}
				}
		);
		table.setCellSelectionEnabled(true);
		table.getTableHeader().addMouseListener(
				new MouseAdapter(){
					@Override
					public void mousePressed(MouseEvent e) {
						super.mousePressed(e);
						showPopupMenu(e);
					}

					@Override
					public void mouseReleased(MouseEvent e) {
						super.mouseReleased(e);
						showPopupMenu(e);
					}
					@Override
					public void mouseClicked(MouseEvent e) {
						super.mouseClicked(e);
//						System.out.println(e);
						final int columnIndex = table.getTableHeader().columnAtPoint(e.getPoint());
						sortColumn(columnIndex,true);
					}
				}
		);
		table.getTableHeader().setReorderingAllowed(false);
		table.getColumnModel().getSelectionModel().addListSelectionListener(
				new ListSelectionListener(){
					public void valueChanged(ListSelectionEvent e) {
						processTableSelection();
					}
				}
		);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		table.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener(){
					public void valueChanged(ListSelectionEvent e) {
						if(!e.getValueIsAdjusting()){
							processTableSelection();
						}
					}
				}
		);
		scrollPane.setViewportView(table);
		
		table.setModel(getTableModel(summaryReportColumnNames,new Object[][] {{"diffTest","summaryTest"}}));

		plotDerivedSimResultsRadioButton = new JRadioButton();
		plotDerivedSimResultsRadioButton.addActionListener(plotButtonActionListener);
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.gridy = 3;
		gridBagConstraints_8.gridx = 0;
		add(plotDerivedSimResultsRadioButton, gridBagConstraints_8);

		final JLabel interactiveAnalysisUsingLabel = new JLabel();
		interactiveAnalysisUsingLabel.setFont(new Font("", Font.BOLD, 14));
		interactiveAnalysisUsingLabel.setText("Plot Derived FRAP Simulation Results");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_6.anchor = GridBagConstraints.WEST;
		gridBagConstraints_6.gridy = 3;
		gridBagConstraints_6.gridx = 1;
		add(interactiveAnalysisUsingLabel, gridBagConstraints_6);

		final JLabel interactiveAnalysisUsingLabel_1 = new JLabel();
		interactiveAnalysisUsingLabel_1.setFont(new Font("", Font.PLAIN, 14));
		interactiveAnalysisUsingLabel_1.setText("Interactive Analysis using FRAP Simulation Results (Enter/Adjust FRAP Model Parameters)");
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.gridy = 4;
		gridBagConstraints_10.gridx = 1;
		add(interactiveAnalysisUsingLabel_1, gridBagConstraints_10);

		interpolationPanel = new FRAPInterpolationPanel();
		interpolationPanel.addPropertyChangeListener(
				new PropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent evt) {
						if(evt.getSource() == interpolationPanel){
							if((evt.getPropertyName().equals(FRAPInterpolationPanel.PROPERTY_CHANGE_OPTIMIZER_VALUE))){
								plotDerivedSimulationResults();
							}else if(evt.getPropertyName().equals(FRAPInterpolationPanel.PROPERTY_CHANGE_RUNSIM)){
								newFRAPFromParameters.create(interpolationPanel.getCurrentParameters());
							}
						}
					}
				}
		);
		interpolationPanel.setBorder(new LineBorder(Color.black, 2, false));
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_5.weightx = 1;
		gridBagConstraints_5.gridy = 5;
		gridBagConstraints_5.gridx = 1;
		add(interpolationPanel, gridBagConstraints_5);

		final JLabel standardErrorRoiLabel = new JLabel();
		standardErrorRoiLabel.setFont(new Font("", Font.BOLD, 14));
		standardErrorRoiLabel.setText("Plot -  ROI Average Normalized (using Pre-Bleach Average) vs. Time");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(20, 4, 4, 4);
		gridBagConstraints_4.gridy = 6;
		gridBagConstraints_4.gridx = 1;
		add(standardErrorRoiLabel, gridBagConstraints_4);

		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridwidth = 2;
		gridBagConstraints_1.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_1.fill = GridBagConstraints.BOTH;
		gridBagConstraints_1.weighty = 1;
		gridBagConstraints_1.weightx = 1;
		gridBagConstraints_1.gridy = 7;
		gridBagConstraints_1.gridx = 0;
		add(panel, gridBagConstraints_1);

		multisourcePlotPane = new MultisourcePlotPane();
		multisourcePlotPane.setRefDataLabelPrefix("exp:");
		multisourcePlotPane.setModelDataLabelPrefix("sim:");
		
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.gridx = 0;
		gridBagConstraints_2.fill = GridBagConstraints.BOTH;
		gridBagConstraints_2.weighty = 1;
		gridBagConstraints_2.weightx = 1;
		panel.add(multisourcePlotPane, gridBagConstraints_2);
		
		init();
	}
	private void init(){
		plotButtonGroup.add(plotFRAPSimResultsRadioButton);
		plotButtonGroup.add(plotDerivedSimResultsRadioButton);
		plotFRAPSimResultsRadioButton.setSelected(true);
		BeanUtils.enableComponents(interpolationPanel, false);
	}

	private void plotDerivedSimulationResults(/*boolean bBestFit*/){
		try{
			RoiType argROIType = null;
			String description = null;
			int numROITypes = (argROIType == null?ROI.RoiType.values().length:1);
			ODESolverResultSet fitOdeSolverResultSet = new ODESolverResultSet();
			fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
			for (int j = 0; j < numROITypes; j++) {
				RoiType currentROIType = (argROIType == null?ROI.RoiType.values()[j]:argROIType);
				String name = (description == null?/*"sim D="+diffusionRates[diffusionRateIndex]+"::"*/"":description)+currentROIType.toString();
				fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(name));
			}
			//
			// populate time
			//
			double[] shiftedSimTimes = frapOptData.getReducedExpTimePoints();
			int startIndexRecovery = Integer.parseInt(frapOptData.getExpFrapStudy().getFrapModelParameters().startIndexForRecovery);
			for (int j = 0; j < shiftedSimTimes.length; j++) {
				double[] row = new double[numROITypes+1];
				row[0] = shiftedSimTimes[j]+
					frapOptData.getExpFrapStudy().getFrapData().getImageDataset().getImageTimeStamps()[startIndexRecovery];
				fitOdeSolverResultSet.addRow(row);
			}
			//
			// populate values
			//
//			double[][] currentOptFitData = null;
//			if(!bBestFit){//evt.getPropertyName().equals(FRAPInterpolationPanel.PROPERTY_CHANGE_OPTIMIZER_VALUE)){
//				currentOptFitData = interpolationPanel.getCurrentFitData();
//			}else{
//				currentOptFitData = interpolationPanel.getBestFitData();
//			}
			double[][] currentOptFitData = interpolationPanel.getCurrentFitData();
			
			for (int j = 0; j < numROITypes; j++) {
//				RoiType currentROIType = (argROIType == null?FRAPStudy.SpatialAnalysisResults.ORDERED_ROITYPES[j]:argROIType);
				double[] values = currentOptFitData[j];
//				double[] values = curveHash.get(new FRAPStudy.CurveInfo(analysisParameters[analysisParametersIndex],currentROIType)); // get simulated data for this ROI
				for (int k = 0; k < values.length; k++) {
					fitOdeSolverResultSet.setValue(k, j+1, values[k]);
				}
			}
			
			DataSource[] selectedRowDataSourceArr = allDataHash.get(summaryData[0][0]);
			ReferenceData referenceData =
				(ReferenceData)selectedRowDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE].getSource();
			final DataSource expDataSource = new DataSource(referenceData,"exp");
			final DataSource simDataSource = new DataSource(fitOdeSolverResultSet, "opt");
			DataSource[] newDataSourceArr = new DataSource[2];
			newDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE] = expDataSource;
			newDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_SIMDATASOURCE] = simDataSource;
			multisourcePlotPane.setDataSources(newDataSourceArr);
			multisourcePlotPane.selectAll();
		}catch(Exception e2){
			e2.printStackTrace();
			DialogUtils.showErrorDialog("Error graphing Optimizer data "+e2.getMessage());
		}

	}
	private void showPopupMenu(MouseEvent e){
		if(e.isPopupTrigger()){
			if(table.getSelectedRow() < 0 || table.getSelectedRow() >= summaryData.length||
				table.getSelectedColumn() < 0 || table.getSelectedColumn() >= summaryReportColumnNames.length){
				copyValueJMenuItem.setEnabled(false);
				copyTimeDataJMenuItem.setEnabled(false);
			}else{
				copyValueJMenuItem.setEnabled(true);
				copyTimeDataJMenuItem.setEnabled(true);				
			}
			if(copyTimeDataJMenuItem.isEnabled() && table.getSelectedColumn() == 0){
				copyTimeDataJMenuItem.setEnabled(false);
			}
			jPopupMenu.show((Component)e.getSource(), e.getX(), e.getY());
		}

	}
	private void sortColumn(final int columnIndex,boolean bAutoReverse){
		if(summaryData != null){
			int selectedRow = table.getSelectedRow();
			int[] selectedColumns = table.getSelectedColumns();
			Object[][] sortedObjects = new Object[summaryData.length][];
			for (int i = 0; i < sortedObjects.length; i++) {
				sortedObjects[i] = new Object[] {new Integer(i),(Double)summaryData[i][columnIndex]};
			}
			Arrays.sort(sortedObjects,
				new Comparator<Object[]> (){
					public int compare(Object[] o1,Object[] o2) {
						return (int)Math.signum((Double)o1[1] - (Double)o2[1]);
					}
				}
			);
			if(bAutoReverse){
				boolean bSortOrderChanged = false;
				for (int i = 0; i < sortedObjects.length; i++) {
					if((Integer)(((Object[])sortedObjects[i])[0]) != i){
						bSortOrderChanged = true;
						break;
					}
				}
				if(!bSortOrderChanged){
					Object[][] reverseSort = new Object[sortedObjects.length][];
					for (int i = 0; i < reverseSort.length; i++) {
						reverseSort[i] = sortedObjects[sortedObjects.length-1-i];
					}
					sortedObjects = reverseSort;
				}
			}
			Object[][] summaryCopy = new Object[summaryData.length][];
			for (int i = 0; i < sortedObjects.length; i++) {
				summaryCopy[i] = summaryData[(Integer)(((Object[])sortedObjects[i])[0])];
			}
			table.setModel(getTableModel(summaryReportColumnNames,summaryCopy));
			if(selectedRow != -1){
				for (int i = 0; i < sortedObjects.length; i++) {
					if((Integer)(((Object[])sortedObjects[i])[0]) == selectedRow){
						selectedRow = i;//table.getSelectionModel().setSelectionInterval(i, i);	
						break;
					}
				}

			}
			if(selectedColumns != null && selectedColumns.length > 0){
				setTableCellSelection(selectedRow, selectedColumns);
			}
		}
	}
	private void processTableSelection(){
		if(allDataHash == null){
			return;
		}
		int selectedRow = table.getSelectedRow();
		int[] selectedColumns = table.getSelectedColumns();
		if(selectedRow != -1 && selectedColumns != null && selectedColumns.length > 0){
			Double selectedDiffusionRate =
				(Double)table.getValueAt(selectedRow,
				FRAPStudy.SpatialAnalysisResults.COLUMN_INDEX_DIFFUSION_RATE);
			DataSource[] selectedRowDataSourceArr = allDataHash.get(selectedDiffusionRate);
			multisourcePlotPane.setDataSources(selectedRowDataSourceArr);
			multisourcePlotPane.clearSelection();
			Vector<String> dataSourceColumnNamesV = new Vector<String>();
			for (int i = 0; i < selectedColumns.length; i++) {
				int selectedColumn = selectedColumns[i];
				if(selectedColumn < FRAPStudy.SpatialAnalysisResults.ANALYSISPARAMETERS_COLUMNS_COUNT/*== FRAPStudy.SpatialAnalysisResults.COLUMN_INDEX_DIFFUSION_RATE*/){
					multisourcePlotPane.selectAll();
					dataSourceColumnNamesV = null;
					break;
				}else{
					String expColName =
						((ReferenceData)selectedRowDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE].getSource()).getColumnNames()[selectedColumn-FRAPStudy.SpatialAnalysisResults.ANALYSISPARAMETERS_COLUMNS_COUNT+1];
					String simColName =
						((ODESolverResultSet)selectedRowDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_SIMDATASOURCE].getSource()).getColumnDescriptions()[selectedColumn-FRAPStudy.SpatialAnalysisResults.ANALYSISPARAMETERS_COLUMNS_COUNT+1].getName();
					dataSourceColumnNamesV.add(expColName);
					dataSourceColumnNamesV.add(simColName);
				}
			}
			if(dataSourceColumnNamesV != null && dataSourceColumnNamesV.size() > 0){
				String[] dataSourceColumnNamesArr = dataSourceColumnNamesV.toArray(new String[0]);
				multisourcePlotPane.select(dataSourceColumnNamesArr);				
			}
		}else{
			multisourcePlotPane.setDataSources(null);
		}

	}
	private TableModel getTableModel(final String[] columnNames,final Object[][] rowData){

		summaryData = rowData;
		
		TableModel tableModel = 
			new AbstractTableModel() {
			    public String getColumnName(int col) {
			        return columnNames[col].toString();
			    }
			    public int getRowCount() {
			    	return rowData.length; }
			    public int getColumnCount() {
			    	return columnNames.length; }
			    public Object getValueAt(int row, int col) {
			        return rowData[row][col];
			    }
			    public boolean isCellEditable(int row, int col){
			    	return false;
			    }
			    public void setValueAt(Object value, int row, int col) {
			        rowData[row][col] = value;
			        fireTableCellUpdated(row, col);
			    }
			};
		return tableModel;
	}
	
	public void setData(FRAPStudyPanel.NewFRAPFromParameters newFRAPFromParameters,
			final FRAPOptData frapOptData,
			FRAPStudy.SpatialAnalysisResults spatialAnalysisResults,
			final double[] frapDataTimeStamps,int startIndexForRecovery,final Double modelDiffusionRate) throws Exception{

		this.newFRAPFromParameters = newFRAPFromParameters;
		this.frapOptData = frapOptData;
		
		allDataHash =
			spatialAnalysisResults.createSummaryReportSourceData(
				frapDataTimeStamps, startIndexForRecovery, modelDiffusionRate);
		final Object[][] tableData =
			spatialAnalysisResults.createSummaryReportTableData(frapDataTimeStamps,startIndexForRecovery);

		SwingUtilities.invokeAndWait(new Runnable(){public void run(){
			try{
				plotFRAPSimResultsRadioButton.doClick();
				interpolationPanel.init(frapOptData);
				table.setModel(getTableModel(summaryReportColumnNames,tableData));
				sortColumn(FRAPStudy.SpatialAnalysisResults.COLUMN_INDEX_DIFFUSION_RATE,false);
				multisourcePlotPane.forceXYRange(new Range(frapDataTimeStamps[0],frapDataTimeStamps[frapDataTimeStamps.length-1]), new Range(0,1));
				if(modelDiffusionRate != null){
					int matchingRow = -1;
					for (int i = 0; i < summaryData.length; i++) {
						if(summaryData[i][FRAPStudy.SpatialAnalysisResults.COLUMN_INDEX_DIFFUSION_RATE].equals(modelDiffusionRate)){
							matchingRow = i;
							break;
						}
					}
					if(matchingRow != -1){
						setTableCellSelection(matchingRow,
							new int[] {FRAPStudy.SpatialAnalysisResults.COLUMN_INDEX_DIFFUSION_RATE});
					}else{
						DialogUtils.showErrorDialog("Summary Table couldn't find model diffusion rate "+modelDiffusionRate);
					}
				}
			}catch(Exception e){
				throw new RuntimeException("Error setting tableModel. "+e.getMessage());
			}
		}});
	}
	
	private void setTableCellSelection(int selectedRow, int[] selectedColumns){
		table.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
		table.getColumnModel().getSelectionModel().setSelectionInterval(selectedColumns[0], selectedColumns[0]);
		for (int i = 1; i < selectedColumns.length; i++) {
			table.getColumnModel().getSelectionModel().addSelectionInterval(selectedColumns[i], selectedColumns[i]);
		}
	}
	
	public void clearData(){
		allDataHash = null;
	}
	public boolean hasData(){
		return allDataHash != null;
	}
}

package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.border.EtchedBorder;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import cbit.gui.DialogUtils;
import cbit.gui.SimpleTransferable;
import cbit.plot.Plot2DPanel;
import cbit.util.BeanUtils;
import cbit.util.Range;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.ROI;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;
import cbit.vcell.microscopy.ROI.RoiType;
import cbit.vcell.microscopy.gui.FRAPInterpolationPanel;

public class ResultsSummaryPanel extends JPanel {
	
	private static final String SIM_RESULTS = "Plot FRAP Simulation Results";
	private static final String DERIVED_RESULTS = "Plot Derived FRAP Simulation Results";
		
	private FRAPStudyPanel.NewFRAPFromParameters newFRAPFromParameters;
	private final JPanel cardPanel = new JPanel(new CardLayout());
	private final JLabel standardErrorseLabel;
	private final JLabel interactiveAnalysisUsingLabel_1;
	
	private final JRadioButton plotFRAPSimResultsRadioButton;
	private final JRadioButton plotDerivedSimResultsRadioButton;
	private ButtonGroup plotButtonGroup = new ButtonGroup();
	
	private final FRAPInterpolationPanel interpolationPanel;
	private FRAPOptData frapOptData;
	
	private final JScrollPane scrollPane;
	private final JTable table;
	private MultisourcePlotPane multisourcePlotPane;
	private Hashtable<Double, DataSource[]> allDataHash;
	private Object[][] summaryData;
	
	private JPopupMenu jPopupMenu = new JPopupMenu();
	private JMenuItem copyValueJMenuItem;
	private JMenuItem copyTimeDataJMenuItem;
	
	private boolean B_TABLE_DISABLED = false;
	
	private static String[] summaryReportColumnNames =
		FRAPStudy.SpatialAnalysisResults.getSummaryReportColumnNames();
	
	private ActionListener plotButtonActionListener = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			CardLayout cl = ((CardLayout)(cardPanel.getLayout()));
			if(plotFRAPSimResultsRadioButton.isSelected()){
				B_TABLE_DISABLED = false;
				standardErrorseLabel.setEnabled(true);
				processTableSelection();
				cl.show(cardPanel, SIM_RESULTS);
			}else if(plotDerivedSimResultsRadioButton.isSelected()){
				B_TABLE_DISABLED = true;
				plotDerivedSimulationResults();
				cl.show(cardPanel, DERIVED_RESULTS);
			}
		}
	};
	
	public ResultsSummaryPanel() {
		super();
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel(new FlowLayout());
				
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

		final JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(Color.gray, Color.lightGray));
		panel_1.setLayout(new GridBagLayout());
		cardPanel.add(panel_1, SIM_RESULTS);

		plotFRAPSimResultsRadioButton = new JRadioButton();
		plotFRAPSimResultsRadioButton.setFont(new Font("", Font.BOLD, 12));
		buttonPanel.add(plotFRAPSimResultsRadioButton);
		plotFRAPSimResultsRadioButton.setText(SIM_RESULTS);
		plotFRAPSimResultsRadioButton.addActionListener(plotButtonActionListener);

		standardErrorseLabel = new JLabel();
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_6.gridy = 0;
		gridBagConstraints_6.gridx = 0;
		panel_1.add(standardErrorseLabel, gridBagConstraints_6);
//		standardErrorseLabel.setFont(new Font("", Font.PLAIN, 14));
		standardErrorseLabel.setText("FRAP Simulation Summary: Standard Error (se) including all Times of Normalized ROI Average  (Experimental vs. Simulation Data)");

		scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(0, 80));
		scrollPane.setMinimumSize(new Dimension(0, 80));
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
//		gridBagConstraints_7.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_7.fill = GridBagConstraints.BOTH;
		gridBagConstraints_7.weightx = 1.5;
		gridBagConstraints_7.gridy = 1;
		gridBagConstraints_7.gridx = 0;
		panel_1.add(scrollPane, gridBagConstraints_7);


//		table = new JTable();

		//Fix disable display bug--------
		table = new JTable (){
	      public Component prepareRenderer (final TableCellRenderer renderer,int row, int column){
	        Component comp = super.prepareRenderer (renderer, row, column);
	        if(!table.isEnabled()){
	        	comp.setBackground(table.getBackground());
	        }
	        comp.setEnabled (table.isEnabled());
	        return comp;
	      }
		};
		final TableCellRenderer origTableCellRenderer = table.getTableHeader().getDefaultRenderer();
		table.getTableHeader().setDefaultRenderer(
			new TableCellRenderer(){
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,boolean hasFocus, int row, int column) {
					Component comp =
						origTableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					comp.setEnabled(table.isEnabled());
					return comp;
				}
			}
		);
		//-------------------------------
				
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
		
		final JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EtchedBorder(Color.gray, Color.lightGray));
		panel_2.setLayout(new GridBagLayout());
		cardPanel.add(panel_2,DERIVED_RESULTS);
				
		plotDerivedSimResultsRadioButton = new JRadioButton();
		plotDerivedSimResultsRadioButton.setFont(new Font("", Font.BOLD, 12));
		buttonPanel.add(plotDerivedSimResultsRadioButton);
		plotDerivedSimResultsRadioButton.setText(DERIVED_RESULTS);
		plotDerivedSimResultsRadioButton.addActionListener(plotButtonActionListener);

		interactiveAnalysisUsingLabel_1 = new JLabel();
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.gridy = 0;
		gridBagConstraints_8.gridx = 0;
		panel_2.add(interactiveAnalysisUsingLabel_1, gridBagConstraints_8);
		interactiveAnalysisUsingLabel_1.setFont(new Font("", Font.PLAIN, 14));
		interactiveAnalysisUsingLabel_1.setText("Interactive Analysis using FRAP Simulation Results (Enter/Adjust FRAP Model Parameters)");

		interpolationPanel = new FRAPInterpolationPanel();
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.fill = GridBagConstraints.BOTH;
		gridBagConstraints_10.gridy = 1;
		gridBagConstraints_10.gridx = 0;
		gridBagConstraints_10.weightx = 1.5;
		panel_2.add(interpolationPanel, gridBagConstraints_10);
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

		topPanel.add(buttonPanel, BorderLayout.NORTH);
		topPanel.add(cardPanel, BorderLayout.CENTER);
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_9.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_9.gridy = 0;
		gridBagConstraints_9.gridx = 0;
		add(topPanel, gridBagConstraints_9);
		
		
		final JPanel panel_3 = new JPanel();
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.columnWidths = new int[] {0,7};
		panel_3.setLayout(gridBagLayout_1);
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.gridy = 1;
		gridBagConstraints_11.gridx = 0;
		add(panel_3, gridBagConstraints_11);

		final JLabel standardErrorRoiLabel = new JLabel();
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.gridx = 0;
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.insets = new Insets(2, 2, 2, 2);
		panel_3.add(standardErrorRoiLabel, gridBagConstraints_4);
		standardErrorRoiLabel.setFont(new Font("", Font.BOLD, 12));
		standardErrorRoiLabel.setText("Plot -  ROI Average Normalized (using Pre-Bleach Average) vs. Time");

		final JButton showROIbutton = new JButton(new ImageIcon(getClass().getResource("/images/showROI.gif")));
		showROIbutton.setToolTipText("Show ROIS");
		showROIbutton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				ROIImagePanel roiImagePanel = new ROIImagePanel();
				ROI[] allRoiArr = frapOptData.getExpFrapStudy().getFrapData().getRois();
				ROI[] plottedROIArr = new ROI[allRoiArr.length-3];
				int index = 0;
				for (int i = 0; i < allRoiArr.length; i++) {
					if(allRoiArr[i].getROIType().equals(RoiType.ROI_BACKGROUND) ||
						allRoiArr[i].getROIType().equals(RoiType.ROI_BLEACHED) ||
						allRoiArr[i].getROIType().equals(RoiType.ROI_CELL)){
						continue;
					}
					plottedROIArr[index] = allRoiArr[i];
					index++;
				}
				
				Color[] allROIColors = multisourcePlotPane.getAutoContrastColorsInListOrder();//Plot2DPanel.generateAutoColor(plottedROIArr.length,multisourcePlotPane.getBackground());
				Color[] ringROIColors = new Color[8];
				System.arraycopy(allROIColors, 1, ringROIColors, 0, ringROIColors.length);
				roiImagePanel.init(plottedROIArr,ringROIColors,
					frapOptData.getExpFrapStudy().getFrapData().getRoi(RoiType.ROI_CELL),Color.white,
					frapOptData.getExpFrapStudy().getFrapData().getRoi(RoiType.ROI_BLEACHED),allROIColors[0]);
				DialogUtils.showComponentCloseDialog(ResultsSummaryPanel.this, roiImagePanel, "FRAP Model ROIs");
			}
		});
		showROIbutton.setBorder(new EtchedBorder());
		showROIbutton.setContentAreaFilled(false);
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.insets = new Insets(0, 8, 0, 0);
		gridBagConstraints_12.gridy = 0;
		gridBagConstraints_12.gridx = 1;
		gridBagConstraints_12.anchor = GridBagConstraints.EAST;
		panel_3.add(showROIbutton, gridBagConstraints_12);

		final JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(Color.black, 1, false));
		panel.setLayout(new GridBagLayout());
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridwidth = 0;
		gridBagConstraints_1.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_1.fill = GridBagConstraints.BOTH;
		gridBagConstraints_1.weighty = 1;
		gridBagConstraints_1.weightx = 1;
		gridBagConstraints_1.gridy = 2;
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
	}

	private void plotDerivedSimulationResults(/*boolean bBestFit*/){
		try{
			RoiType argROIType = null;
			String description = null;
			int numROITypes = (argROIType == null?ROI.RoiType.values().length:1);
			boolean[] wantsROITypes = new boolean[numROITypes];
			Arrays.fill(wantsROITypes, true);
			if(argROIType == null){
				wantsROITypes[RoiType.ROI_BACKGROUND.ordinal()] = false;
				wantsROITypes[RoiType.ROI_CELL.ordinal()] = false;
			}
			ODESolverResultSet fitOdeSolverResultSet = new ODESolverResultSet();
			fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
			for (int j = 0; j < numROITypes; j++) {
				if(!wantsROITypes[j]){continue;}
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
				double[] row = new double[(argROIType == null?numROITypes-2:1)+1];
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
			
			int columncounter = 0;
			for (int j = 0; j < numROITypes; j++) {
				if(!wantsROITypes[j]){continue;}
//				RoiType currentROIType = (argROIType == null?FRAPStudy.SpatialAnalysisResults.ORDERED_ROITYPES[j]:argROIType);
				double[] values = currentOptFitData[j];
//				double[] values = curveHash.get(new FRAPStudy.CurveInfo(analysisParameters[analysisParametersIndex],currentROIType)); // get simulated data for this ROI
				for (int k = 0; k < values.length; k++) {
					fitOdeSolverResultSet.setValue(k, columncounter/*j*/+1, values[k]);
				}
				columncounter++;
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
		if(B_TABLE_DISABLED){
			return;
		}
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
		if(B_TABLE_DISABLED){
			return;
		}
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
		if(allDataHash == null || B_TABLE_DISABLED){
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
			    	if(col < FRAPStudy.SpatialAnalysisResults.ANALYSISPARAMETERS_COLUMNS_COUNT){
			        return rowData[row][col];
			    	}
			    	final double DIGIT_SCALE = 1000000.0;
			    	return ((double)((int)((Double)rowData[row][col]*DIGIT_SCALE)))/DIGIT_SCALE;
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
				multisourcePlotPane.forceXYRange(new Range(frapDataTimeStamps[0],frapDataTimeStamps[frapDataTimeStamps.length-1]), new Range(0,1.1));
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

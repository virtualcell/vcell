package cbit.vcell.microscopy.gui;

import java.awt.Color;
import java.awt.Dimension;

import cbit.gui.DialogUtils;
import cbit.vcell.client.task.UserCancelException;
import cbit.vcell.math.gui.ExpressionCanvas;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPDataAnalysis;
import cbit.vcell.microscopy.FrapDataAnalysisResults;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class FRAPEstimationPanel extends JPanel {
	private JTable table;
	private FRAPData initFRAPData;
	private JLabel plotOfAverageLabel;
	private JLabel frapModelParameterLabel;
	private MultisourcePlotPane multisourcePlotPane;
	private ExpressionCanvas expressionCanvas;
	private JComboBox bleachEstimationComboBox;
	private static final String PARAM_EST_EQUATION_STRING = "FRAP Model Parameter Estimation Equation";
	
	public static final String FRAP_PARAMETER_ESTIMATE_VALUES_PROPERTY = "FRAP_PARAMETER_ESTIMATE_VALUES_PROPERTY";
	
	private enum FRAPParameterEstimateEnum {
		DIFFUSION_RATE("Primary Diffusion Rate","um2/s"),
		MOBILE_FRACTION("Primary Mobile Fraction",""),
		IMMOBILE_FRATION("Immobile Fraction",""),
		START_TIME_RECOVERY("Start Time Recovery","s"),
		BLEACH_RATE_MONITOR("Monitor Bleach Rate","1/s");
		
	    private final String parameterTypeName;
	    private Double value;
	    private String unit;
	    
	    FRAPParameterEstimateEnum(String parameterTypeName,String unit) {
	       this.parameterTypeName = parameterTypeName;
	       this.unit = unit;
	    }
	};

	public static class FRAPParameterEstimateValues{
		public final Double diffusionRate;
		public final Double mobileFraction;
		public final Double startTimeRecovery;
		public final Double bleachWhileMonitorRate;
		public FRAPParameterEstimateValues(
			Double diffusionRate,Double mobileFraction,Double startTimeRecovery,Double bleachWhileMonitorRate){
			this.diffusionRate = diffusionRate;
			this.mobileFraction = mobileFraction;
			this.startTimeRecovery = startTimeRecovery;
			this.bleachWhileMonitorRate = bleachWhileMonitorRate;
		}
	};

	private static int PARAMETER_TYPE_COLUMN = 0;
	private static int VALUE_COLUMN = 1;
	private static int UNIT_COLUMN = 2;
	private static String[] FRAP_ESTIMATE_COLUMN_NAMES = new String[] {"Paramter Type","Estimated Value","Unit"};
	
	

	public FRAPEstimationPanel() {
		super();
		
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,7};
		gridBagLayout.columnWidths = new int[] {7,0};
		setLayout(gridBagLayout);

		final JPanel panel = new JPanel();
//		panel.setBorder(new LineBorder(Color.black, 1, false));
		panel.setBorder(new EtchedBorder());
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.columnWidths = new int[] {7,0};
		gridBagLayout_1.rowHeights = new int[] {7,7,7,7};
		panel.setLayout(gridBagLayout_1);
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 0.1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 1;
		add(panel, gridBagConstraints);

		final JLabel frapModelParameterLabel_1 = new JLabel();
		frapModelParameterLabel_1.setFont(new Font("", Font.BOLD, 12));
		frapModelParameterLabel_1.setText("FRAP Model Parameter Estimates");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.gridwidth = 2;
		gridBagConstraints_3.gridx = 0;
		gridBagConstraints_3.gridy = 0;
		panel.add(frapModelParameterLabel_1, gridBagConstraints_3);

		final JLabel frapParameterEstimatesLabel = new JLabel();
//		frapParameterEstimatesLabel.setFont(new Font("", Font.BOLD, 14));
		frapParameterEstimatesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frapParameterEstimatesLabel.setText("Estimation Method");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_1.fill = GridBagConstraints.BOTH;
		gridBagConstraints_1.weightx = 0;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 0;
		panel.add(frapParameterEstimatesLabel, gridBagConstraints_1);

		bleachEstimationComboBox = new JComboBox();
		bleachEstimationComboBox.setPreferredSize(new Dimension(225, 25));
		bleachEstimationComboBox.setMinimumSize(new Dimension(225, 25));
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_2.weightx = 1;
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.gridy = 1;
		gridBagConstraints_2.gridx = 1;
		panel.add(bleachEstimationComboBox, gridBagConstraints_2);

		final JScrollPane scrollPane = new JScrollPane();
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_4.fill = GridBagConstraints.BOTH;
		gridBagConstraints_4.weighty = 1;
		gridBagConstraints_4.weightx = 1;
		gridBagConstraints_4.gridwidth = 2;
		gridBagConstraints_4.gridy = 2;
		gridBagConstraints_4.gridx = 0;
		panel.add(scrollPane, gridBagConstraints_4);

		table = new JTable();
		table.getTableHeader().setReorderingAllowed(false);
		scrollPane.setViewportView(table);

		final JButton applyEstimatedValuesButton = new JButton();
//		applyEstimatedValuesButton.setFont(new Font("", Font.BOLD, 16));
		applyEstimatedValuesButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				Object[][] rowData = new Object[4][FRAP_ESTIMATE_COLUMN_NAMES.length];
				
				rowData[0][0] =
					FRAPParameterEstimateEnum.DIFFUSION_RATE.parameterTypeName;
				rowData[1][0] =
					FRAPParameterEstimateEnum.MOBILE_FRACTION.parameterTypeName;
				rowData[2][0] =
					FRAPParameterEstimateEnum.START_TIME_RECOVERY.parameterTypeName;
				rowData[3][0] =
					FRAPParameterEstimateEnum.BLEACH_RATE_MONITOR.parameterTypeName;

				rowData[0][2] =
					FRAPParameterEstimateEnum.DIFFUSION_RATE.unit;
				rowData[1][2] =
					FRAPParameterEstimateEnum.MOBILE_FRACTION.unit;
				rowData[2][2] =
					FRAPParameterEstimateEnum.START_TIME_RECOVERY.unit;
				rowData[3][2] =
					FRAPParameterEstimateEnum.BLEACH_RATE_MONITOR.unit;

				rowData[0][1] =
					FRAPParameterEstimateEnum.DIFFUSION_RATE.value;
				rowData[1][1] =
					FRAPParameterEstimateEnum.MOBILE_FRACTION.value;
				rowData[2][1] =
					FRAPParameterEstimateEnum.START_TIME_RECOVERY.value;
				rowData[3][1] =
					FRAPParameterEstimateEnum.BLEACH_RATE_MONITOR.value;

				try{
					int[] result = DialogUtils.showComponentOKCancelTableList(FRAPEstimationPanel.this, "Select rows to copy values to 'Initial FRAP Model Parameters'",
							FRAP_ESTIMATE_COLUMN_NAMES, rowData, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
					if(result != null && result.length > 0){
						Double selectedDiffusionRate = null;
						Double selectedMobileFraction = null;
						Double selectedStartTimeRecovery = null;
						Double bleachWhileMonitoringRate = null;
						
						for (int j = 0; j < result.length; j++) {
							switch (result[j]) {
							case 0:
								selectedDiffusionRate = FRAPParameterEstimateEnum.DIFFUSION_RATE.value;
								break;
							case 1:
								selectedMobileFraction = FRAPParameterEstimateEnum.MOBILE_FRACTION.value;
								break;
							case 2:
								selectedStartTimeRecovery = FRAPParameterEstimateEnum.START_TIME_RECOVERY.value;
								break;
							case 3:
								bleachWhileMonitoringRate = FRAPParameterEstimateEnum.BLEACH_RATE_MONITOR.value;
								break;
							default:
								break;
							}
						}
						FRAPParameterEstimateValues frapParameterEstimateValues =
							new FRAPParameterEstimateValues(
								selectedDiffusionRate,
								selectedMobileFraction,
								selectedStartTimeRecovery,
								bleachWhileMonitoringRate
							);
						firePropertyChange(FRAP_PARAMETER_ESTIMATE_VALUES_PROPERTY, null, frapParameterEstimateValues);
					}
				}catch(UserCancelException e2){
					//ignore
				}
			}
		});
		applyEstimatedValuesButton.setText("Apply Estimated Values...");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(4, 4, 4, 4);
//		gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_5.gridwidth = 2;
		gridBagConstraints_5.gridy = 3;
		gridBagConstraints_5.gridx = 0;
		panel.add(applyEstimatedValuesButton, gridBagConstraints_5);

		final JPanel panel_1 = new JPanel();
//		panel_1.setBorder(new LineBorder(Color.black, 1, false));
		panel_1.setBorder(new EtchedBorder());
		panel_1.setPreferredSize(new Dimension(350, 225));
		panel_1.setMinimumSize(new Dimension(350, 225));
		final GridBagLayout gridBagLayout_2 = new GridBagLayout();
		gridBagLayout_2.rowHeights = new int[] {0,7};
		panel_1.setLayout(gridBagLayout_2);
		final GridBagConstraints gridBagConstraints_24 = new GridBagConstraints();
		gridBagConstraints_24.weightx = 1;
		gridBagConstraints_24.fill = GridBagConstraints.BOTH;
		gridBagConstraints_24.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_24.weighty = 0.1;
		gridBagConstraints_24.gridy = 0;
		gridBagConstraints_24.gridx = 0;
		add(panel_1, gridBagConstraints_24);

		frapModelParameterLabel = new JLabel();
		frapModelParameterLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frapModelParameterLabel.setText(PARAM_EST_EQUATION_STRING);
		final GridBagConstraints gridBagConstraints_25 = new GridBagConstraints();
		gridBagConstraints_25.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_25.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_25.weighty = 0;
		gridBagConstraints_25.gridy = 0;
		gridBagConstraints_25.gridx = 0;
		panel_1.add(frapModelParameterLabel, gridBagConstraints_25);

		expressionCanvas = new ExpressionCanvas();
		final GridBagConstraints gridBagConstraints_26 = new GridBagConstraints();
		gridBagConstraints_26.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_26.fill = GridBagConstraints.BOTH;
		gridBagConstraints_26.weighty = 1;
		gridBagConstraints_26.weightx = 1;
		gridBagConstraints_26.gridy = 1;
		gridBagConstraints_26.gridx = 0;
		panel_1.add(expressionCanvas, gridBagConstraints_26);

		plotOfAverageLabel = new JLabel();
		plotOfAverageLabel.setFont(new Font("", Font.BOLD, 12));
		plotOfAverageLabel.setText("Plot -  'Bleach' ROI average (Experiment and Estimated) vs. Time");
		final GridBagConstraints gridBagConstraints_29 = new GridBagConstraints();
		gridBagConstraints_29.insets = new Insets(20, 4, 4, 4);
		gridBagConstraints_29.gridwidth = 2;
		gridBagConstraints_29.gridy = 1;
		gridBagConstraints_29.gridx = 0;
		add(plotOfAverageLabel, gridBagConstraints_29);

		multisourcePlotPane = new MultisourcePlotPane();
		multisourcePlotPane.setModelDataLabelPrefix("Estimated_");
		multisourcePlotPane.setRefDataLabelPrefix("Experiment_");
//		multisourcePlotPane.setBorder(new LineBorder(Color.black, 1, false));
		multisourcePlotPane.setBorder(new EtchedBorder());
		multisourcePlotPane.setListVisible(false);
		final GridBagConstraints gridBagConstraints_27 = new GridBagConstraints();
		gridBagConstraints_27.gridheight = 2;
		gridBagConstraints_27.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_27.fill = GridBagConstraints.BOTH;
		gridBagConstraints_27.weighty = 1;
		gridBagConstraints_27.weightx = 1;
		gridBagConstraints_27.gridwidth = 2;
		gridBagConstraints_27.gridy = 2;
		gridBagConstraints_27.gridx = 0;
		add(multisourcePlotPane, gridBagConstraints_27);
		
		initialize();
	}

	private void initTable(){
		TableModel tableModel = 
			new AbstractTableModel() {
			    public String getColumnName(int col) {
			        return FRAP_ESTIMATE_COLUMN_NAMES[col].toString();
			    }
			    public int getRowCount() {
			    	return FRAPParameterEstimateEnum.values().length; }
			    public int getColumnCount() {
			    	return FRAP_ESTIMATE_COLUMN_NAMES.length; }
			    public Object getValueAt(int row, int col) {
			    	if(col == PARAMETER_TYPE_COLUMN){
			    		return FRAPParameterEstimateEnum.values()[row].parameterTypeName;
			    	}else if(col == UNIT_COLUMN){
			    		return FRAPParameterEstimateEnum.values()[row].unit;
			    	}
			        return FRAPParameterEstimateEnum.values()[row].value;
			    }
			    public boolean isCellEditable(int row, int col){
			    	return false;
			    }
			    public void setValueAt(Object value, int row, int col) {
			    	if(col == PARAMETER_TYPE_COLUMN || col == UNIT_COLUMN){
			    		throw new IllegalArgumentException("Can't update 'Parameter Type' or 'Unit' column");
			    	}
			    	FRAPParameterEstimateEnum.values()[row].value = (Double)value;
			        fireTableCellUpdated(row, col);
			    }
			};
		table.setModel(tableModel);
		table.getTableHeader().getColumnModel().getColumn(UNIT_COLUMN).setMaxWidth(50);

	}
	private void initialize(){
		initTable();
		
		for (int i = 0; i < FrapDataAnalysisResults.BLEACH_TYPE_NAMES.length; i++) {
			bleachEstimationComboBox.addItem(FrapDataAnalysisResults.BLEACH_TYPE_NAMES[i]);
//			bleachEstimationComboBox.insertItemAt(/*"Estimation method '"+*/"'"+FrapDataAnalysisResults.BLEACH_TYPE_NAMES[i]+"'", i);
		}
		bleachEstimationComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
//				if(bleachEstimationComboBox.getSelectedIndex() == FrapDataAnalysisResults.BleachType_CirularDisk){
//					//expression on canvas
//					try{
//						String[] prefixes = new String[] { "I(t) = ", "D = " };
//						Expression[] expressions = new Expression[] { new Expression(FRAPDataAnalysis.circularDisk_IntensityFunc_display), new Expression(FRAPDataAnalysis.circularDisk_DiffFunc) };
//						String[] suffixes = new String[] { "", "[um2.s-1]" };
//						expressionCanvas.setExpressions(expressions,prefixes,suffixes);
//					}catch (ExpressionException e2){
//						e2.printStackTrace(System.out);
//					}					
//				}else
				if(bleachEstimationComboBox.getSelectedItem().equals(FrapDataAnalysisResults.BLEACH_TYPE_NAMES[FrapDataAnalysisResults.BleachType_GaussianSpot])){
					//expression on canvas
					try{
						String[] prefixes = new String[] { "I(t) = ", "u(t)= ","D = " };
						Expression[] expressions = new Expression[] { new Expression(FRAPDataAnalysis.gaussianSpot_IntensityFunc), new Expression(FRAPDataAnalysis.gaussianSpot_MuFunc), new Expression(FRAPDataAnalysis.gaussianSpot_DiffFunc) };
						String[] suffixes = new String[] { "", "", "[um2.s-1]" };
						expressionCanvas.setExpressions(expressions,prefixes,suffixes);
					}catch (ExpressionException e2){
						e2.printStackTrace(System.out);
					}
					
				}else if(bleachEstimationComboBox.getSelectedItem().equals(FrapDataAnalysisResults.BLEACH_TYPE_NAMES[FrapDataAnalysisResults.BleachType_HalfCell])){
					//expression on canvas
					try{
						String[] prefixes = new String[] { "I(t) = ", "u(t)= ","D = " };
						Expression[] expressions = new Expression[] { new Expression(FRAPDataAnalysis.halfCell_IntensityFunc), new Expression(FRAPDataAnalysis.halfCell_MuFunc), new Expression(FRAPDataAnalysis.halfCell_DiffFunc) };
						String[] suffixes = new String[] { "", "", "[um2.s-1]" };
						expressionCanvas.setExpressions(expressions,prefixes,suffixes);
					}catch (ExpressionException e2){
						e2.printStackTrace(System.out);
					}
				}
				frapModelParameterLabel.setText(
					PARAM_EST_EQUATION_STRING+"  ('"+
					bleachEstimationComboBox.getSelectedItem()+"')");
//				plotOfAverageLabel.setText(
//					PLOT_TITLE_STRING+"  ('"+
//					FrapDataAnalysisResults.BLEACH_TYPE_NAMES[bleachEstimationComboBox.getSelectedIndex()]+"')");
				try{
					refreshFRAPModelParameterEstimates(initFRAPData);
				}catch (Exception e2){
					e2.printStackTrace();
					DialogUtils.showErrorDialog(
						"Error setting estimation method "+
						FrapDataAnalysisResults.BLEACH_TYPE_NAMES[bleachEstimationComboBox.getSelectedIndex()]+
						"\n"+e2.getMessage());
				}
			}
		});
		bleachEstimationComboBox.setSelectedItem(FrapDataAnalysisResults.BLEACH_TYPE_NAMES[FrapDataAnalysisResults.BleachType_GaussianSpot]);
	}

	private void displayFit(FrapDataAnalysisResults frapDataAnalysisResults,double[] frapDataTimeStamps){
		if (frapDataAnalysisResults == null){
			FRAPParameterEstimateEnum.DIFFUSION_RATE.value = null;
			FRAPParameterEstimateEnum.MOBILE_FRACTION.value = null;
			FRAPParameterEstimateEnum.IMMOBILE_FRATION.value = null;
			FRAPParameterEstimateEnum.START_TIME_RECOVERY.value = null;
			FRAPParameterEstimateEnum.BLEACH_RATE_MONITOR.value = null;
			multisourcePlotPane.setDataSources(null);
		}else{
			FRAPParameterEstimateEnum.DIFFUSION_RATE.value =
				(frapDataAnalysisResults.getRecoveryDiffusionRate() == null
					?null
					:frapDataAnalysisResults.getRecoveryDiffusionRate());
			FRAPParameterEstimateEnum.MOBILE_FRACTION.value =
					(frapDataAnalysisResults.getMobilefraction() == null
						?null
						:frapDataAnalysisResults.getMobilefraction());
			FRAPParameterEstimateEnum.IMMOBILE_FRATION.value =
				(FRAPParameterEstimateEnum.MOBILE_FRACTION.value == null
					?null
					:1.0 - FRAPParameterEstimateEnum.MOBILE_FRACTION.value);
			FRAPParameterEstimateEnum.BLEACH_RATE_MONITOR.value =
				(frapDataAnalysisResults.getBleachWhileMonitoringTau() == null
					?null
					:frapDataAnalysisResults.getBleachWhileMonitoringTau());
			
			
			
			int startIndexForRecovery = frapDataAnalysisResults.getStartingIndexForRecovery();
			//
			//Experiment - Cell ROI Average
			//
			double[] cellRegionData = frapDataAnalysisResults.getCellRegionData();
			Expression bleachWhileMonitorCurve = frapDataAnalysisResults.getFitBleachWhileMonitorExpression();
			ReferenceData expCellAvgData =
				new SimpleReferenceData(new String[] { "t", "CellROIAvg" }, new double[] { 1.0, 1.0 }, new double[][] { frapDataTimeStamps, cellRegionData });
			DataSource expCellAvgDataSource = new DataSource(expCellAvgData,"expCellAvg");
			//
			//Analytic - Cell ROI Average with Bleach while monitor
			//
			ODESolverResultSet bleachWhileMonitorOdeSolverResultSet = new ODESolverResultSet();
			bleachWhileMonitorOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
			try {
				bleachWhileMonitorOdeSolverResultSet.addFunctionColumn(
					new FunctionColumnDescription(
						frapDataAnalysisResults.getFitBleachWhileMonitorExpression(),
						"CellROI_BleachWhileMonitor",
						null,"bleachWhileMonitorFit",true));
			} catch (ExpressionException e) {
				e.printStackTrace();
			}
			for (int i = startIndexForRecovery; i < frapDataTimeStamps.length; i++) {
				bleachWhileMonitorOdeSolverResultSet.addRow(new double[] { frapDataTimeStamps[i] });
			}
			//
			// extend if necessary to plot theoretical curve to 4*tau
			//
			{
			double T = frapDataTimeStamps[frapDataTimeStamps.length-1];
			double deltaT = frapDataTimeStamps[frapDataTimeStamps.length-1]-frapDataTimeStamps[frapDataTimeStamps.length-2];
			while (T+deltaT < 6*frapDataAnalysisResults.getRecoveryTau()){
				bleachWhileMonitorOdeSolverResultSet.addRow(new double[] { T } );
				T += deltaT;
			}
			}
			DataSource bleachWhileMonitorDataSource = new DataSource(bleachWhileMonitorOdeSolverResultSet, "bleachwm");


			
			
			
			
			
			//
			//Recovery curve
			//
			double[] bleachRegionData = frapDataAnalysisResults.getBleachRegionData();
			Expression fittedCurve = frapDataAnalysisResults.getFitExpression();
			ReferenceData expRefData = new SimpleReferenceData(new String[] { "t", "BleachROIAvg" }, new double[] { 1.0, 1.0 }, new double[][] { frapDataTimeStamps, bleachRegionData });
			DataSource expDataSource = new DataSource(expRefData,"experiment");
			ODESolverResultSet fitOdeSolverResultSet = new ODESolverResultSet();
			fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
			try {
				fitOdeSolverResultSet.addFunctionColumn(
					new FunctionColumnDescription(
						fittedCurve,
						"BleachROI_Recovery",//"('"+FrapDataAnalysisResults.BLEACH_TYPE_NAMES[bleachEstimationComboBox.getSelectedIndex()]+"')",
						null,"recoveryFit",true));
			} catch (ExpressionException e) {
				e.printStackTrace();
			}
			for (int i = startIndexForRecovery; i < frapDataTimeStamps.length; i++) {
				fitOdeSolverResultSet.addRow(new double[] { frapDataTimeStamps[i] });
			}
			//
			// extend if necessary to plot theoretical curve to 4*tau
			//
			double T = frapDataTimeStamps[frapDataTimeStamps.length-1];
			double deltaT = frapDataTimeStamps[frapDataTimeStamps.length-1]-frapDataTimeStamps[frapDataTimeStamps.length-2];

			while (T+deltaT < 6*frapDataAnalysisResults.getRecoveryTau()){
				fitOdeSolverResultSet.addRow(new double[] { T } );
				T += deltaT;
			}
			DataSource fitDataSource = new DataSource(fitOdeSolverResultSet, "fit");
			multisourcePlotPane.setDataSources(new DataSource[] {  expDataSource, fitDataSource , expCellAvgDataSource , bleachWhileMonitorDataSource} );
			multisourcePlotPane.selectAll();		
		}
		table.repaint();
	}
	
	public void refreshFRAPModelParameterEstimates(FRAPData frapData) throws Exception {
		this.initFRAPData = frapData;
		FrapDataAnalysisResults frapDataAnalysisResults = null;
		double[] frapDataTimeStamps = null;
		bleachEstimationComboBox.setEnabled(false);
		if(frapData != null){
			if(frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).isAllPixelsZero()){
				displayFit(null,null);
				throw new Exception(
					OverlayEditorPanelJAI.INITIAL_BLEACH_AREA_TEXT+" ROI not defined.\n"+
					"Use ROI tools under '"+FRAPStudyPanel.FRAPSTUDYPANEL_TABNAME_IMAGES+"' tab to define.");
			}
			frapDataTimeStamps = frapData.getImageDataset().getImageTimeStamps();
			frapDataAnalysisResults =
				FRAPDataAnalysis.fitRecovery2(frapData,
					FrapDataAnalysisResults.getBleachTypeFromBleachTypeName(bleachEstimationComboBox.getSelectedItem().toString()));
			FRAPParameterEstimateEnum.START_TIME_RECOVERY.value = frapDataTimeStamps[frapDataAnalysisResults.getStartingIndexForRecovery()];
			bleachEstimationComboBox.setEnabled(true);
		}

		displayFit(frapDataAnalysisResults,frapDataTimeStamps);
	}
}

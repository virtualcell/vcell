package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Hashtable;

import javax.swing.DefaultSingleSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import cbit.gui.DialogUtils;
import cbit.util.BeanUtils;
import cbit.util.Range;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.client.UserMessage;
import cbit.vcell.microscopy.EstimatedParameterTableModel;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPDataAnalysis;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPOptimization;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;

public class ResultsSummaryPanel extends JPanel {
	
	public static final String STR_REACTION_DIFFUSION = "Reaction Diffusion Model";
	public static final String STR_PURE_DIFFUSION = "Pure Diffusion Model";
	private static final int IDX_REACTION_DIFFUSION = 1;
	private static final int IDX_PURE_DIFFUSION = 0;	
	
	private FRAPStudyPanel.NewFRAPFromParameters newFRAPFromParameters; //will be initialized in setData
	private FRAPStudy.SpatialAnalysisResults spatialAnalysisResults; //will be initialized in setData
	private final JTabbedPane paramPanel; //exclusively display pure diffusion panel and reaction diffusion panel
//	private final JLabel standardErrorseLabel;
	private JLabel simulationParametersLabel;
	private final JLabel interactiveAnalysisUsingLabel_1;
	
//	private final JRadioButton reactionDiffusionRadioButton; 
//	private final JRadioButton pureDiffusionRadioButton;
//	private ButtonGroup plotButtonGroup = new ButtonGroup();
	
	private FRAPPureDiffusionParamPanel pureDiffusionPanel;
	private FRAPReactionDiffusionParamPanel reactionDiffusionPanel;
	private FRAPReacDiffEstimationGuidePanel estGuidePanel;
	
	private FRAPOptData frapOptData;
	private FRAPStudy frapStudy;
	
//	private final JScrollPane scrollPane;
//	private final JTable table;
	private MultisourcePlotPane multisourcePlotPane;
	private Hashtable<FRAPStudy.AnalysisParameters, DataSource[]> allDataHash;
	private Object[][] summaryData;
	
	private JPopupMenu jPopupMenu = new JPopupMenu();
	private JMenuItem copyValueJMenuItem;
	private JMenuItem copyTimeDataJMenuItem;
	
	private boolean B_TABLE_DISABLED = false;
	private boolean do_once = true;
	
	private static String[] summaryReportColumnNames =
		FRAPStudy.SpatialAnalysisResults.getSummaryReportColumnNames();
	
	//	private ActionListener plotButtonActionListener = new ActionListener(){
//		public void actionPerformed(ActionEvent e) {
//			if(reactionDiffusionRadioButton.isSelected()){
//				B_TABLE_DISABLED = false;
//				standardErrorseLabel.setEnabled(true);
//				processTableSelection();
//				cl.show(cardPanel, TAB_REACTION_DIFFUSION);
//			}else if(pureDiffusionRadioButton.isSelected()){
//				B_TABLE_DISABLED = true;
//				plotDerivedSimulationResults();
//				cl.show(cardPanel, TAB_PURE_DIFFUSION);
//			}
//		}
//	};
//	
	public ResultsSummaryPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {7,7,7,0,7};
		gridBagLayout.columnWidths = new int[] {7};
		setLayout(gridBagLayout);
		
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel(new FlowLayout());
				
//		JMenuItem copyReportJMenuItem = new JMenuItem("Copy Summary Report");
//		copyReportJMenuItem.addActionListener(
//			new ActionListener(){
//				public void actionPerformed(ActionEvent e) {
//					SimpleTransferable.sendToClipboard(
//						FRAPStudy.SpatialAnalysisResults.createCSVSummaryReport(
//							summaryReportColumnNames, summaryData));
//				}
//			}
//		);
//		copyValueJMenuItem = new JMenuItem("Copy Value");
//		copyValueJMenuItem.addActionListener(
//			new ActionListener(){
//				public void actionPerformed(ActionEvent e) {
//					SimpleTransferable.sendToClipboard(""+table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()));
//				}
//			}
//		);
//		
//		copyTimeDataJMenuItem = new JMenuItem("Copy Time Data");
//		copyTimeDataJMenuItem.addActionListener(
//			new ActionListener(){
//				public void actionPerformed(ActionEvent e) {
//					try{
//						Double selectedDiffusionRate =
//							(Double)table.getValueAt(table.getSelectedRow(),
//							FRAPStudy.SpatialAnalysisResults.COLUMN_INDEX_DIFFUSION_RATE);
//						DataSource[] selectedRowDataSourceArr = allDataHash.get(selectedDiffusionRate);
//						ReferenceData expDataSource =
//							(ReferenceData)selectedRowDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE].getSource();
//						ODESolverResultSet simDataSource =
//							(ODESolverResultSet)selectedRowDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_SIMDATASOURCE].getSource();
//						double[] expTimes = expDataSource.getColumnData(0);
//						double[] expColumnData = expDataSource.getColumnData(table.getSelectedColumn());
//						double[] simTimes = simDataSource.extractColumn(0);
//						double[] simColumnData = simDataSource.extractColumn(table.getSelectedColumn());
//						
//						SimpleTransferable.sendToClipboard(
//								FRAPStudy.SpatialAnalysisResults.createCSVTimeData(
//									summaryReportColumnNames,table.getSelectedColumn(),
//									expTimes,expColumnData,simTimes,simColumnData
//						));
//					}catch(Exception e2){
//						e2.printStackTrace();
//						DialogUtils.showErrorDialog(
//							"Erro copying Time Data for row="+table.getSelectedRow()+", col="+table.getSelectedColumn()+".  "+e2.getMessage());
//					}
//				}
//			}
//		);

//		jPopupMenu.add(copyValueJMenuItem);
//		jPopupMenu.add(copyReportJMenuItem);
//		jPopupMenu.add(copyTimeDataJMenuItem);
		
		
		//set up tabbed pane for two kinds of models.
		paramPanel=new JTabbedPane();
		paramPanel.setForeground(new Color(0,0,244));
		
		final JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EtchedBorder(Color.gray, Color.lightGray));
		panel_2.setLayout(new GridBagLayout());
		paramPanel.addTab(STR_PURE_DIFFUSION, null, panel_2, null);
		

//		pureDiffusionRadioButton = new JRadioButton();
//		pureDiffusionRadioButton.setFont(new Font("", Font.BOLD, 12));
//		buttonPanel.add(pureDiffusionRadioButton);
//		pureDiffusionRadioButton.setText(ADJUST_PARAM_PURE_DIFFUSION);
//		pureDiffusionRadioButton.addActionListener(plotButtonActionListener);
	
		final JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(Color.gray, Color.lightGray));
		panel_1.setLayout(new GridBagLayout());
		paramPanel.addTab(STR_REACTION_DIFFUSION, null, panel_1, null);
		paramPanel.setModel(
				new DefaultSingleSelectionModel(){
					@Override
					public void setSelectedIndex(int index) {
						try{
							String exitTabTitle = (getSelectedIndex() == -1?null:paramPanel.getTitleAt(getSelectedIndex()));
							if(changeTabView(exitTabTitle,paramPanel.getTitleAt(index))){
								super.setSelectedIndex(index);
							}
						}catch(Exception e){
							DialogUtils.showWarningDialog(
								ResultsSummaryPanel.this, "Can't switch view beacause:\n"+e.getMessage(),
								new String[] {UserMessage.OPTION_OK}, UserMessage.OPTION_OK);
						}finally{
						
						}

					}
				}
			);
		paramPanel.setSelectedIndex(paramPanel.indexOfTab(STR_PURE_DIFFUSION));
			
//		reactionDiffusionRadioButton = new JRadioButton();
//		reactionDiffusionRadioButton.setEnabled(false);
//		reactionDiffusionRadioButton.setFont(new Font("", Font.BOLD, 12));
//		buttonPanel.add(reactionDiffusionRadioButton);
//		reactionDiffusionRadioButton.setText(ADJUST_PARAM_REACTION_DIFFUSION);
//		reactionDiffusionRadioButton.addActionListener(plotButtonActionListener);

		/*Amended in Dec 2008. We don't want to show the standard error in adjust parameters tab
		 *From now on we have two models : pure diffusion and reaction diffusion. Therefore, 
		 *We use cardlayout either display pure diffusion parameters or reaction diffusion parameters.
		 */
		
//		table = new JTable (){
//		      public Component prepareRenderer (final TableCellRenderer renderer,int row, int column){
//		        Component comp = super.prepareRenderer (renderer, row, column);
//		        if(!table.isEnabled()){
//		        	comp.setBackground(table.getBackground());
//		        }
//		        comp.setEnabled (table.isEnabled());
//		        return comp;
//		      }
//			};
//			standardErrorseLabel=new JLabel();
//			scrollPane = new JScrollPane();
		//card panel for reaction diffusion
		JLabel reactionDiffPanelLabel = new JLabel();
		final GridBagConstraints gridBagConstraints_rdl = new GridBagConstraints();
		gridBagConstraints_rdl.gridy = 0;
		gridBagConstraints_rdl.gridx = 0;
		panel_1.add(reactionDiffPanelLabel, gridBagConstraints_rdl);
		reactionDiffPanelLabel.setFont(new Font("", Font.PLAIN, 14));
		reactionDiffPanelLabel.setText("Analysis on Reaction Diffusion Model using FRAP Simulation Results");
		
		reactionDiffusionPanel = new FRAPReactionDiffusionParamPanel();
		final GridBagConstraints gridBagConstraints_rdp = new GridBagConstraints();
		gridBagConstraints_rdp.fill = GridBagConstraints.BOTH;
		gridBagConstraints_rdp.gridy = 1;
		gridBagConstraints_rdp.gridx = 0;
		gridBagConstraints_rdp.weightx = 1.5;
		panel_1.add(reactionDiffusionPanel, gridBagConstraints_rdp);
		reactionDiffusionPanel.addPropertyChangeListener(
				new PropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent evt) {
						if(evt.getSource() == reactionDiffusionPanel){
							if((evt.getPropertyName().equals(FRAPReactionDiffusionParamPanel.PROPERTY_EST_FROM_PURE_DIFFUSION))){
								activateReacDiffEstPanel();
							}else if(evt.getPropertyName().equals(FRAPReactionDiffusionParamPanel.PROPERTY_EST_BS_CONCENTRATION)){
								if(frapStudy != null && frapStudy.getFrapData() != null)
								{
									reactionDiffusionPanel.calBSConcentration(FRAPStudy.calculatePrebleachAvg_oneValue(frapStudy.getFrapData(), FRAPDataAnalysis.getRecoveryIndex(frapStudy.getFrapData())));
								}
							}else if(evt.getPropertyName().equals(FRAPReactionDiffusionParamPanel.PROPERTY_EST_ON_RATE)){
								if(frapStudy != null && frapStudy.getFrapData() != null)
								{
									reactionDiffusionPanel.calOnRate(FRAPStudy.calculatePrebleachAvg_oneValue(frapStudy.getFrapData(), FRAPDataAnalysis.getRecoveryIndex(frapStudy.getFrapData())));
								}
							}else if(evt.getPropertyName().equals(FRAPReactionDiffusionParamPanel.PROPERTY_EST_OFF_RATE)){
								if(frapStudy != null && frapStudy.getFrapData() != null)
								{
									reactionDiffusionPanel.calOffRate(FRAPStudy.calculatePrebleachAvg_oneValue(frapStudy.getFrapData(), FRAPDataAnalysis.getRecoveryIndex(frapStudy.getFrapData())));
								}
							}else if(evt.getPropertyName().equals(FRAPReactionDiffusionParamPanel.PROPERTY_CHANGE_RUNSIM)){
								newFRAPFromParameters.create(reactionDiffusionPanel.getCurrentParameters(), STR_REACTION_DIFFUSION);
							}
						}
					}
				}
		);
		
//		table.setModel(getTableModel(summaryReportColumnNames,new Object[][] {{"diffTest","summaryTest"}}));
		//pure diffusion panel
		interactiveAnalysisUsingLabel_1 = new JLabel();
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.gridy = 0;
		gridBagConstraints_8.gridx = 0;
		panel_2.add(interactiveAnalysisUsingLabel_1, gridBagConstraints_8);
		interactiveAnalysisUsingLabel_1.setFont(new Font("", Font.PLAIN, 14));
		interactiveAnalysisUsingLabel_1.setText("Interactive Analysis on Pure Diffusion Model using FRAP Simulation Results");

		pureDiffusionPanel = new FRAPPureDiffusionParamPanel();
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.fill = GridBagConstraints.BOTH;
		gridBagConstraints_10.gridy = 1;
		gridBagConstraints_10.gridx = 0;
		gridBagConstraints_10.weightx = 1.5;
		panel_2.add(pureDiffusionPanel, gridBagConstraints_10);
		pureDiffusionPanel.addPropertyChangeListener(
				new PropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent evt) {
						if(evt.getSource() == pureDiffusionPanel){
							if((evt.getPropertyName().equals(FRAPPureDiffusionParamPanel.PROPERTY_CHANGE_OPTIMIZER_VALUE)))
							{
								plotDerivedSimulationResults(false, spatialAnalysisResults.getAnalysisParameters());
							}
//							else if(evt.getPropertyName().equals(FRAPPureDiffusionParamPanel.PROPERTY_CHANGE_RUNSIM)){
//								newFRAPFromParameters.create(pureDiffusionPanel.getCurrentParameters(), STR_PURE_DIFFUSION);
//							}
						}
					}
				}
		);

		topPanel.add(buttonPanel, BorderLayout.NORTH);
		topPanel.add(paramPanel, BorderLayout.CENTER);
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
					if(allRoiArr[i].getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()) ||
						allRoiArr[i].getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()) ||
						allRoiArr[i].getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name())){
						continue;
					}
					plottedROIArr[index] = allRoiArr[i];
					index++;
				}
				
				Color[] allROIColors = multisourcePlotPane.getAutoContrastColorsInListOrder();//Plot2DPanel.generateAutoColor(plottedROIArr.length,multisourcePlotPane.getBackground());
				Color[] ringROIColors = new Color[8];
				System.arraycopy(allROIColors, 1, ringROIColors, 0, ringROIColors.length);
				roiImagePanel.init(plottedROIArr,ringROIColors,
					frapOptData.getExpFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()),Color.white,
					frapOptData.getExpFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()),allROIColors[0]);
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
		
//		init();
	}
//	private void init(){
//		
//	}

	private void plotDerivedSimulationResults(boolean isSimData, FRAPStudy.AnalysisParameters[] anaParams)
	{
		try{
			String argROIName = null;
			String description = null;
			int numROITypes = (argROIName == null?FRAPData.VFRAP_ROI_ENUM.values().length:1);
			boolean[] wantsROITypes = new boolean[numROITypes];
			Arrays.fill(wantsROITypes, true);
			if(argROIName == null){
				wantsROITypes[FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.ordinal()] = false;
				wantsROITypes[FRAPData.VFRAP_ROI_ENUM.ROI_CELL.ordinal()] = false;
			}
			ODESolverResultSet fitOdeSolverResultSet = new ODESolverResultSet();
			fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
			for (int j = 0; j < numROITypes; j++) {
				if(!wantsROITypes[j]){continue;}
				String currentROIName = (argROIName == null?FRAPData.VFRAP_ROI_ENUM.values()[j].name():argROIName);
				String name = (description == null?/*"sim D="+diffusionRates[diffusionRateIndex]+"::"*/"":description)+currentROIName;
				fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(name));
			}
			//
			// populate time
			//
			double[] shiftedSimTimes = frapOptData.getReducedExpTimePoints();
			int startIndexRecovery = Integer.parseInt(frapOptData.getExpFrapStudy().getFrapModelParameters().getIniModelParameters().startingIndexForRecovery);
			for (int j = 0; j < shiftedSimTimes.length; j++) {
				double[] row = new double[(argROIName == null?numROITypes-2:1)+1];
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
			double[][] currentOptFitData = pureDiffusionPanel.getCurrentFitData();
			if(allDataHash != null && currentOptFitData != null)
			{
				//populate optimization data
				int columncounter = 0;
				for (int j = 0; j < numROITypes; j++) {
					if(!wantsROITypes[j]){continue;}
					if(!isSimData) //opt data
					{
						double[] values = currentOptFitData[j];
						for (int k = 0; k < values.length; k++) {
							fitOdeSolverResultSet.setValue(k, columncounter/*j*/+1, values[k]);
						}
					}
					columncounter++;
				}
				boolean hasSimData = false;
				
				if(frapStudy.getBioModel() != null && frapStudy.getBioModel().getSimulations()!=null && anaParams[0].isAllSimParamExist())
				{
					hasSimData = true;
				}
				DataSource[] selectedRowDataSourceArr = allDataHash.get(anaParams[0]);//anaParams[0] is the key in allDataHash to get the dataSource[]:exp & sim
				if(selectedRowDataSourceArr != null)
				{   //referenceData is the exp data
					ReferenceData referenceData = (ReferenceData)selectedRowDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE].getSource();
					final DataSource expDataSource = new DataSource(referenceData,"exp");
					DataSource tempSimSource  = null;
					if(isSimData && hasSimData)//from simulation
					{
						ODESolverResultSet simDataResultSet = (ODESolverResultSet)selectedRowDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_SIMDATASOURCE].getSource();
						tempSimSource = new DataSource(simDataResultSet, "sim");
					}
					else //from opt
					{
						tempSimSource = new DataSource(fitOdeSolverResultSet, "opt");
					}
					final DataSource simDataSource = tempSimSource;
					DataSource[] newDataSourceArr = new DataSource[2];
					newDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE] = expDataSource;
					newDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_SIMDATASOURCE] = simDataSource;
					if(isSimData && !hasSimData)
					{
						multisourcePlotPane.setDataSources(null);
					}
					else if(!isSimData && currentOptFitData == null)
					{
						multisourcePlotPane.setDataSources(null);
					}
					else
					{
						multisourcePlotPane.setDataSources(newDataSourceArr);
						multisourcePlotPane.selectAll();
					}
				}
			}
		}catch(Exception e2){
			e2.printStackTrace();
			DialogUtils.showErrorDialog("Error graphing Optimizer data "+e2.getMessage());
		}

	}
//	private void showPopupMenu(MouseEvent e){
//		if(B_TABLE_DISABLED){
//			return;
//		}
//		if(e.isPopupTrigger()){
//			if(table.getSelectedRow() < 0 || table.getSelectedRow() >= summaryData.length||
//				table.getSelectedColumn() < 0 || table.getSelectedColumn() >= summaryReportColumnNames.length){
//				copyValueJMenuItem.setEnabled(false);
//				copyTimeDataJMenuItem.setEnabled(false);
//			}else{
//				copyValueJMenuItem.setEnabled(true);
//				copyTimeDataJMenuItem.setEnabled(true);				
//			}
//			if(copyTimeDataJMenuItem.isEnabled() && table.getSelectedColumn() == 0){
//				copyTimeDataJMenuItem.setEnabled(false);
//			}
//			jPopupMenu.show((Component)e.getSource(), e.getX(), e.getY());
//		}
//
//	}
//	private void sortColumn(final int columnIndex,boolean bAutoReverse){
//		if(B_TABLE_DISABLED){
//			return;
//		}
//		if(summaryData != null){
//			int selectedRow = table.getSelectedRow();
//			int[] selectedColumns = table.getSelectedColumns();
//			Object[][] sortedObjects = new Object[summaryData.length][];
//			for (int i = 0; i < sortedObjects.length; i++) {
//				sortedObjects[i] = new Object[] {new Integer(i),(Double)summaryData[i][columnIndex]};
//			}
//			Arrays.sort(sortedObjects,
//				new Comparator<Object[]> (){
//					public int compare(Object[] o1,Object[] o2) {
//						return (int)Math.signum((Double)o1[1] - (Double)o2[1]);
//					}
//				}
//			);
//			if(bAutoReverse){
//				boolean bSortOrderChanged = false;
//				for (int i = 0; i < sortedObjects.length; i++) {
//					if((Integer)(((Object[])sortedObjects[i])[0]) != i){
//						bSortOrderChanged = true;
//						break;
//					}
//				}
//				if(!bSortOrderChanged){
//					Object[][] reverseSort = new Object[sortedObjects.length][];
//					for (int i = 0; i < reverseSort.length; i++) {
//						reverseSort[i] = sortedObjects[sortedObjects.length-1-i];
//					}
//					sortedObjects = reverseSort;
//				}
//			}
//			Object[][] summaryCopy = new Object[summaryData.length][];
//			for (int i = 0; i < sortedObjects.length; i++) {
//				summaryCopy[i] = summaryData[(Integer)(((Object[])sortedObjects[i])[0])];
//			}
//			table.setModel(getTableModel(summaryReportColumnNames,summaryCopy));
//			if(selectedRow != -1){
//				for (int i = 0; i < sortedObjects.length; i++) {
//					if((Integer)(((Object[])sortedObjects[i])[0]) == selectedRow){
//						selectedRow = i;//table.getSelectionModel().setSelectionInterval(i, i);	
//						break;
//					}
//				}
//
//			}
//			if(selectedColumns != null && selectedColumns.length > 0){
//				setTableCellSelection(selectedRow, selectedColumns);
//			}
//		}
//	}
//	private void processTableSelection(){
//		if(allDataHash == null || B_TABLE_DISABLED){
//			return;
//		}
//		int selectedRow = table.getSelectedRow();
//		int[] selectedColumns = table.getSelectedColumns();
//		if(selectedRow != -1 && selectedColumns != null && selectedColumns.length > 0){
//			Double selectedDiffusionRate =
//				(Double)table.getValueAt(selectedRow,
//				FRAPStudy.SpatialAnalysisResults.COLUMN_INDEX_DIFFUSION_RATE);
//			DataSource[] selectedRowDataSourceArr = allDataHash.get(selectedDiffusionRate);
//			multisourcePlotPane.setDataSources(selectedRowDataSourceArr);
//			multisourcePlotPane.clearSelection();
//			Vector<String> dataSourceColumnNamesV = new Vector<String>();
//			for (int i = 0; i < selectedColumns.length; i++) {
//				int selectedColumn = selectedColumns[i];
//				if(selectedColumn < FRAPStudy.SpatialAnalysisResults.ANALYSISPARAMETERS_COLUMNS_COUNT/*== FRAPStudy.SpatialAnalysisResults.COLUMN_INDEX_DIFFUSION_RATE*/){
//					multisourcePlotPane.selectAll();
//					dataSourceColumnNamesV = null;
//					break;
//				}else{
//					String expColName =
//						((ReferenceData)selectedRowDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE].getSource()).getColumnNames()[selectedColumn-FRAPStudy.SpatialAnalysisResults.ANALYSISPARAMETERS_COLUMNS_COUNT+1];
//					String simColName =
//						((ODESolverResultSet)selectedRowDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_SIMDATASOURCE].getSource()).getColumnDescriptions()[selectedColumn-FRAPStudy.SpatialAnalysisResults.ANALYSISPARAMETERS_COLUMNS_COUNT+1].getName();
//					dataSourceColumnNamesV.add(expColName);
//					dataSourceColumnNamesV.add(simColName);
//				}
//			}
//			if(dataSourceColumnNamesV != null && dataSourceColumnNamesV.size() > 0){
//				String[] dataSourceColumnNamesArr = dataSourceColumnNamesV.toArray(new String[0]);
//				multisourcePlotPane.select(dataSourceColumnNamesArr);				
//			}
//		}else{
//			multisourcePlotPane.setDataSources(null);
//		}
//
//	}
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
			final double[] frapDataTimeStamps,int startIndexForRecovery, boolean hasSimData) throws Exception{

		this.newFRAPFromParameters = newFRAPFromParameters;
		this.spatialAnalysisResults = spatialAnalysisResults;
		this.frapOptData = frapOptData;
		//allDataHash use AnalysisParameters as key, the value is dataSource[] which should have length as 2: expDataSource & simDataSouce
		allDataHash = spatialAnalysisResults.createSummaryReportSourceData(frapDataTimeStamps, startIndexForRecovery, hasSimData);
//		final Object[][] tableData = spatialAnalysisResults.createSummaryReportTableData(frapDataTimeStamps,startIndexForRecovery);
		final FRAPStudy.SpatialAnalysisResults finalSpatialAnalysisResults = spatialAnalysisResults;
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){
			try{
				
				//we still need to process the simResultsRadioButton does
				//but now we show Derived FRAP simulation result by default
//				processTableSelection();
			
				pureDiffusionPanel.init(frapOptData);
//				table.setModel(getTableModel(summaryReportColumnNames,tableData));				
//				sortColumn(FRAPStudy.SpatialAnalysisResults.COLUMN_INDEX_DIFFUSION_RATE,false);
				multisourcePlotPane.forceXYRange(new Range(frapDataTimeStamps[0],frapDataTimeStamps[frapDataTimeStamps.length-1]), new Range(0,1.5));
//				if(modelDiffusionRate != null){
//					int matchingRow = -1;
//					for (int i = 0; i < summaryData.length; i++) {
//						if(summaryData[i][FRAPStudy.SpatialAnalysisResults.COLUMN_INDEX_DIFFUSION_RATE].equals(modelDiffusionRate)){
//							matchingRow = i;
//							break;
//						}
//					}
//					if(matchingRow != -1){
//						setTableCellSelection(matchingRow,
//							new int[] {FRAPStudy.SpatialAnalysisResults.COLUMN_INDEX_DIFFUSION_RATE});
//					}else{
//						DialogUtils.showErrorDialog("Summary Table couldn't find model diffusion rate "+modelDiffusionRate);
//					}
//				}
				if(paramPanel.getSelectedIndex()==IDX_REACTION_DIFFUSION)
				{
					plotDerivedSimulationResults(true, finalSpatialAnalysisResults.getAnalysisParameters());
				}
				else
				{
					plotDerivedSimulationResults(false,finalSpatialAnalysisResults.getAnalysisParameters());
				}
			}catch(Exception e){
				throw new RuntimeException("Error setting data to result summary panel");
			}
		}});
	}
	
//	private void setTableCellSelection(int selectedRow, int[] selectedColumns){
//		table.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
//		table.getColumnModel().getSelectionModel().setSelectionInterval(selectedColumns[0], selectedColumns[0]);
//		for (int i = 1; i < selectedColumns.length; i++) {
//			table.getColumnModel().getSelectionModel().addSelectionInterval(selectedColumns[i], selectedColumns[i]);
//		}
//	}
	
	public void clearData(){
		if(allDataHash != null)
		{
			allDataHash.clear();
		}
		allDataHash = null;
	}
	public boolean hasData(){
		return allDataHash != null;
	}
	
	private boolean changeTabView(String exitTabTitle, String enterTabTitle) 
	{
		if(exitTabTitle != null)
		{
			if(exitTabTitle.equals(STR_PURE_DIFFUSION))
			{
				try {
					insertPureDiffusionParametersIntoFRAPStudy(frapStudy);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(exitTabTitle.equals(STR_REACTION_DIFFUSION))
			{
				System.out.println("exit reaction diffusion");
			}
			//from pure diffusion to reaction diffusion
			if(exitTabTitle.equals(STR_PURE_DIFFUSION) && enterTabTitle.equals(STR_REACTION_DIFFUSION))
			{
				//set possible estimated data from pure diffusion parameters
				Parameter[] params = pureDiffusionPanel.getCurrentParameters();
				if (params == null)
				{
					DialogUtils.showErrorDialog("Pure Diffusion parameters are empty or in illegal forms!");
					return false;
				}
				if(reactionDiffusionPanel.isAllTextFieldEmpty() && do_once)
				{
					do_once = false;
					int choice = JOptionPane.showConfirmDialog(ResultsSummaryPanel.this, "Reaction diffusion model parameters are empty. \nDo you want to get estimats from the pure diffusion model?", "Want estimates?", JOptionPane.YES_NO_OPTION);
					if(choice == JOptionPane.YES_OPTION)
					{
						activateReacDiffEstPanel();
					}
				}else
				{
					if(allDataHash != null && allDataHash.get(spatialAnalysisResults.getAnalysisParameters()[0])!= null )
					{
						DataSource[] selectedRowDataSourceArr = allDataHash.get(spatialAnalysisResults.getAnalysisParameters()[0]);
						ODESolverResultSet simDataResultSet = null;
						if(selectedRowDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_SIMDATASOURCE] != null)
						{
							simDataResultSet = (ODESolverResultSet)selectedRowDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_SIMDATASOURCE].getSource();
						}
						if(simDataResultSet != null)
						{
							plotDerivedSimulationResults(true, spatialAnalysisResults.getAnalysisParameters());
						}
						else
						{
							multisourcePlotPane.setDataSources(null);
						}
					}
					else
					{
						multisourcePlotPane.setDataSources(null);
					}
				}
			}
			else if(exitTabTitle.equals(STR_REACTION_DIFFUSION) && enterTabTitle.equals(STR_PURE_DIFFUSION))
			{
				plotDerivedSimulationResults(false, spatialAnalysisResults.getAnalysisParameters());
			}
			return true;
		}
		else return true;
		
	}	
	
	public void setFrapStudy(FRAPStudy fStudy)
	{
		this.frapStudy = fStudy;
		do_once = true;
	}
	
	public FRAPPureDiffusionParamPanel getPureDiffusionPanel() {
		return pureDiffusionPanel;
	}

	public FRAPReactionDiffusionParamPanel getReactionDiffusionPanel() {
		return reactionDiffusionPanel;
	}
	
	public void insertPureDiffusionParametersIntoFRAPStudy(FRAPStudy arg_FRAPStudy) throws Exception
	{
		getPureDiffusionPanel().insertPureDiffusionParametersIntoFRAPStudy(arg_FRAPStudy);
	}
	
	public void insertReactionDiffusionParametersIntoFRAPStudy(FRAPStudy arg_FRAPStudy) throws Exception
	{
		getReactionDiffusionPanel().insertReacDiffusionParametersIntoFRAPStudy(arg_FRAPStudy);
	}
	
	public void activateReacDiffEstPanel()
	{
		if(estGuidePanel == null)
		{
			estGuidePanel = new FRAPReacDiffEstimationGuidePanel();
		}
		
		String secDiffStr = pureDiffusionPanel.getSecondDiffString();
		String secFracStr = pureDiffusionPanel.getSecondMobileFracString();
		double secDiff = Double.parseDouble(pureDiffusionPanel.getSecondDiffString());
		double secFrac = Double.parseDouble(pureDiffusionPanel.getSecondMobileFracString());
		if(!pureDiffusionPanel.getIsSecondDiffusionApplied())
		{
			secDiffStr = null;
			secFracStr = null;
			secDiff = -1; //if set to -1, it won't be displayed in table in diffRateEstimationPanel.
			secFrac = -1; //if set to -1, it won't be displayed in table in diffRateEstimationPanel.
			BeanUtils.enableComponents(estGuidePanel.getDiffTypePanel(), true);
			estGuidePanel.updateUIForPureDiffusion();
		}
		else
		{
			BeanUtils.enableComponents(estGuidePanel.getDiffTypePanel(), false);
			estGuidePanel.updateUIForReacDiffusion();
		}
		estGuidePanel.setIniParamFromPureDiffusion(pureDiffusionPanel.getDiffusionRateString(),
									pureDiffusionPanel.getMobileFractionString(),
									pureDiffusionPanel.getIsSecondDiffusionApplied(),
	            					secDiffStr,
	            					secFracStr,
	            					pureDiffusionPanel.getBleachWhileMonitorRateString());
		try {
			estGuidePanel.updateTableParameters(Double.parseDouble(pureDiffusionPanel.getDiffusionRateString()), 
					                            Double.parseDouble(pureDiffusionPanel.getMobileFractionString()),
					                            secDiff, secFrac, Double.parseDouble(pureDiffusionPanel.getBleachWhileMonitorRateString()),
					                            -1, -1, null, -1, -1, -1, null, -1, null, -1, -1, -1, -1, null, -1, -1, null);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int choice2 = DialogUtils.showComponentOKCancelDialog(ResultsSummaryPanel.this, estGuidePanel, "FRAP Parameter Estimation Guide");
		if (choice2 == JOptionPane.OK_OPTION)
		{
			if(estGuidePanel.getParamTable() != null)
			{
				int rowLen = estGuidePanel.getParamTable().getRowCount();
				for(int i=0; i<rowLen; i++)
				{
					 String name = ((String)estGuidePanel.getParamTable().getValueAt(i, EstimatedParameterTableModel.COLUMN_NAME));
					 double val = ((Double)estGuidePanel.getParamTable().getValueAt(i, EstimatedParameterTableModel.COLUMN_VALUE)).doubleValue();
					 if(name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_FreePartDiffRate]))
					 {
						 reactionDiffusionPanel.setFreeDiffRate(val+"");
					 }
					 else if(name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_FreePartFraction]))
					 {
						 reactionDiffusionPanel.setFreeFraction(val+"");
					 }
					 else if(name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_BleachMonitorRate]))
					 {
						 reactionDiffusionPanel.setBleachMonitorRate(val+"");
					 }
					 else if(name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_ComplexDiffRate]))
					 {
						 reactionDiffusionPanel.setComplexDiffRate(val+"");
					 }
					 else if(name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_ComplexFraction]))
					 {
						 reactionDiffusionPanel.setComplexFraction(val+"");
					 }
					 else if(name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_BSConc]))
					 {
						 reactionDiffusionPanel.setBSConcentration(val+"");
					 }
					 else if(name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_ReacOnRate]))
					 {
						 reactionDiffusionPanel.setOnRate(val+"");
					 }
					 else if(name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_ReacOffRate]))
					 {
						 reactionDiffusionPanel.setOffRate(val+"");
					 }
				}
				//calculate immobile fraction
				double immFrac = 0;
				try
				{
					immFrac = 1- Double.parseDouble(reactionDiffusionPanel.getFreeFraction())- Double.parseDouble(reactionDiffusionPanel.getComplexFraction());
					if(immFrac < (1+FRAPOptimization.epsilon) && immFrac > (1-FRAPOptimization.epsilon))
					{
						immFrac = 0;
					}
					reactionDiffusionPanel.setImmobileFraction(immFrac+"");
				}catch(NumberFormatException e)
				{
					reactionDiffusionPanel.setImmobileFraction(immFrac+"");
					e.printStackTrace(System.out);
				}
				multisourcePlotPane.setDataSources(null);
			}
		}
	}
}

package cbit.vcell.microscopy.gui.estparamwizard;

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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import org.vcell.util.Range;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.ROIImagePanel;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;

public class EstParams_TwoDiffComponentPanel extends JPanel {
	
	private static final int IDX_REACTION_DIFFUSION = 1;
	private static final int IDX_PURE_DIFFUSION = 0;	
	
	private FRAPStudyPanel.NewFRAPFromParameters newFRAPFromParameters; //will be initialized in setData
	private FRAPStudy.SpatialAnalysisResults spatialAnalysisResults; //will be initialized in setData
	private final JPanel paramPanel; //exclusively display pure diffusion panel and reaction diffusion panel
	private JLabel simulationParametersLabel;
	private final JLabel interactiveAnalysisUsingLabel_1;
	

	private ROIImagePanel roiImagePanel;
	private FRAPDiffTwoParamPanel pureDiffusionPanel;
		
	private FRAPOptData frapOptData;
	private FRAPStudy frapStudy;
	
	private MultisourcePlotPane multisourcePlotPane;
	private Hashtable<FRAPStudy.AnalysisParameters, DataSource[]> allDataHash;
	
	private boolean do_once = true;
	
	private static String[] summaryReportColumnNames =
		FRAPStudy.SpatialAnalysisResults.getSummaryReportColumnNames();
	
	public EstParams_TwoDiffComponentPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {7,7,7,0,7};
		gridBagLayout.columnWidths = new int[] {7};
		setLayout(gridBagLayout);
		
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel(new FlowLayout());
				
		//set up tabbed pane for two kinds of models.
		paramPanel=new JPanel(new GridBagLayout());
		paramPanel.setForeground(new Color(0,0,244));
		paramPanel.setBorder(new EtchedBorder(Color.gray, Color.lightGray));
		paramPanel.setLayout(new GridBagLayout());
		
		//pure diffusion panel
		interactiveAnalysisUsingLabel_1 = new JLabel();
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.gridy = 0;
		gridBagConstraints_8.gridx = 0;
		paramPanel.add(interactiveAnalysisUsingLabel_1, gridBagConstraints_8);
		interactiveAnalysisUsingLabel_1.setFont(new Font("", Font.PLAIN, 14));
		interactiveAnalysisUsingLabel_1.setText("Interactive Analysis on Pure Diffusion Model using FRAP Simulation Results");

		pureDiffusionPanel = new FRAPDiffTwoParamPanel();
//		pureDiffusionPanel.setSecondDiffComponentEnabled(true);
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.fill = GridBagConstraints.BOTH;
		gridBagConstraints_10.gridy = 1;
		gridBagConstraints_10.gridx = 0;
		gridBagConstraints_10.weightx = 1.5;
		paramPanel.add(pureDiffusionPanel, gridBagConstraints_10);
		pureDiffusionPanel.addPropertyChangeListener(
				new PropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent evt) {
						if(evt.getSource() == pureDiffusionPanel){
							if((evt.getPropertyName().equals(FRAPDiffTwoParamPanel.PROPERTY_CHANGE_OPTIMIZER_VALUE)))
							{
								plotDerivedSimulationResults(false, spatialAnalysisResults.getAnalysisParameters());
							}
//							}
						}
					}
				}
		);

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
				DialogUtils.showComponentCloseDialog(EstParams_TwoDiffComponentPanel.this, roiImagePanel, "FRAP Model ROIs");
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
//					ReferenceData referenceData = (ReferenceData)selectedRowDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE].getSource();
//					final DataSource expDataSource = new DataSource(referenceData,"exp");
					final DataSource expDataSource = selectedRowDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE];
					DataSource tempSimSource  = null;
					if(isSimData && hasSimData)//from simulation
					{
//						ODESolverResultSet simDataResultSet = (ODESolverResultSet)selectedRowDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_SIMDATASOURCE].getSource();
//						tempSimSource = new DataSource(simDataResultSet, "sim");
						tempSimSource = selectedRowDataSourceArr[FRAPStudy.SpatialAnalysisResults.ARRAY_INDEX_SIMDATASOURCE];
					}
					else //from opt
					{
						tempSimSource = new DataSource.DataSourceOdeSolverResultSet("opt", fitOdeSolverResultSet);
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
			DialogUtils.showErrorDialog(this, "Error graphing Optimizer data "+e2.getMessage());
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
				multisourcePlotPane.forceXYRange(new Range(frapDataTimeStamps[0],frapDataTimeStamps[frapDataTimeStamps.length-1]), new Range(0,1.5));
				plotDerivedSimulationResults(false, finalSpatialAnalysisResults.getAnalysisParameters());
				
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
	
	public void setFrapStudy(FRAPStudy fStudy)
	{
		this.frapStudy = fStudy;
		do_once = true;
	}
	
	public FRAPDiffTwoParamPanel getPureDiffusionPanel() {
		return pureDiffusionPanel;
	}
	
	public void insertPureDiffusionParametersIntoFRAPStudy(FRAPStudy arg_FRAPStudy) throws Exception
	{
		getPureDiffusionPanel().insertPureDiffusionParametersIntoFRAPStudy(arg_FRAPStudy);
	}
}

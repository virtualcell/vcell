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
import java.io.File;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import org.vcell.util.BeanUtils;
import org.vcell.util.Range;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.microscopy.AnalysisParameters;
import cbit.vcell.microscopy.EstimatedParameterTableModel;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptimization;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.SpatialAnalysisResults;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.choosemodelwizard.ChooseModel_RoiForErrorPanel;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;
import cbit.vcell.opt.Parameter;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;

public class EstParams_ReacBindingPanel extends JPanel {
	
	private SpatialAnalysisResults spatialAnalysisResults; //will be initialized in setData
	private final JPanel paramPanel; //exclusively display pure diffusion panel and reaction diffusion panel
	private FRAPReactionDiffusionParamPanel reactionDiffusionPanel;
	private FRAPReacDiffEstimationGuidePanel estGuidePanel;
	
	private FRAPWorkspace frapWorkspace;
	private LocalWorkspace localWorkspace;
	
	private FRAPStudy fStudy = null;
	private MultisourcePlotPane multisourcePlotPane;
	private ChooseModel_RoiForErrorPanel roiPanel;
	private Hashtable<AnalysisParameters, DataSource[]> allDataHash;
	private double[][] currentSimResults = null; //a data structure used to store results according to the current params.
	private double[] currentSimTimePoints = null; //used to store simulation time points according to the current params.
	
	public EstParams_ReacBindingPanel() {
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {7,7,7,0,7};
		gridBagLayout.columnWidths = new int[] {7};
		setLayout(gridBagLayout);
		
		//set up tabbed pane for two kinds of models.
		paramPanel=new JPanel();
		paramPanel.setForeground(new Color(0,0,244));
		paramPanel.setBorder(new EtchedBorder(Color.gray, Color.lightGray));
		paramPanel.setLayout(new GridBagLayout());
	
		//card panel for reaction diffusion
		JLabel reactionDiffPanelLabel = new JLabel();
		final GridBagConstraints gridBagConstraints_rdl = new GridBagConstraints();
		gridBagConstraints_rdl.gridy = 0;
		gridBagConstraints_rdl.gridx = 0;
		paramPanel.add(reactionDiffPanelLabel, gridBagConstraints_rdl);
		reactionDiffPanelLabel.setFont(new Font("", Font.PLAIN, 14));
		reactionDiffPanelLabel.setText("Analysis on Reaction Diffusion Model using FRAP Simulation Results");
		
		reactionDiffusionPanel = new FRAPReactionDiffusionParamPanel();
		final GridBagConstraints gridBagConstraints_rdp = new GridBagConstraints();
		gridBagConstraints_rdp.fill = GridBagConstraints.BOTH;
		gridBagConstraints_rdp.gridy = 1;
		gridBagConstraints_rdp.gridx = 0;
		gridBagConstraints_rdp.weightx = 1.5;
		paramPanel.add(reactionDiffusionPanel, gridBagConstraints_rdp);
		reactionDiffusionPanel.addPropertyChangeListener(
				new PropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent evt) {
						if(evt.getSource() == reactionDiffusionPanel){
							FRAPStudy frapStudy = getFrapWorkspace().getFrapStudy();
							if((evt.getPropertyName().equals(FRAPWorkspace.PROPERTY_CHANGE_EST_BINDING_PARAMETERS))){
								activateReacDiffEstPanel();
							}else if(evt.getPropertyName().equals(FRAPWorkspace.PROPERTY_CHANGE_EST_BS_CONCENTRATION)){
								if(frapStudy != null && frapStudy.getFrapData() != null)
								{
									reactionDiffusionPanel.calBSConcentration(FRAPStudy.calculatePrebleachAvg_oneValue(frapStudy.getFrapData(), getFrapWorkspace().getFrapStudy().getStartingIndexForRecovery()));
								}
							}else if(evt.getPropertyName().equals(FRAPWorkspace.PROPERTY_CHANGE_RUN_BINDING_SIMULATION)){
								simulateWithCurrentParameters();
							}
						}
					}
				}
		);
		
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_9.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_9.gridy = 0;
		gridBagConstraints_9.gridx = 0;
		add(paramPanel, gridBagConstraints_9);
		
		
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
		standardErrorRoiLabel.setText("Plot -  ROI Average Normalized (using Pre-Bleach Average) vs. Time          ");
		
		final JButton showRoisButton = new JButton();
		showRoisButton.setFont(new Font("", Font.PLAIN, 11));
		showRoisButton.setMargin(new Insets(0, 8, 0, 8));
		showRoisButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if(frapWorkspace != null && frapWorkspace.getFrapStudy() != null &&
				   frapWorkspace.getFrapStudy().getSelectedROIsForErrorCalculation() != null)
				{
					getROIPanel().setFrapWorkspace(frapWorkspace);
					getROIPanel().setCheckboxesForDisplay(frapWorkspace.getFrapStudy().getSelectedROIsForErrorCalculation());
					getROIPanel().refreshROIImageForDisplay();
				}
				JOptionPane.showMessageDialog(EstParams_ReacBindingPanel.this, getROIPanel());
			}
		});
		showRoisButton.setText("Show ROIs");
		panel_3.add(showRoisButton, new GridBagConstraints());

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
		
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.gridx = 0;
		gridBagConstraints_2.fill = GridBagConstraints.BOTH;
		gridBagConstraints_2.weighty = 1;
		gridBagConstraints_2.weightx = 1;
		panel.add(multisourcePlotPane, gridBagConstraints_2);
	}

	private ChooseModel_RoiForErrorPanel getROIPanel()
	{
		if(roiPanel == null)
		{
			roiPanel = new ChooseModel_RoiForErrorPanel();
		}
		return roiPanel;
	}

	private void plotDerivedSimulationResults(AnalysisParameters[] anaParams)
	{
		try{
			if(getCurrentSimResults() == null || getCurrentRawSimTimePoints() == null || allDataHash == null
			   || allDataHash.get(anaParams[0]) == null || (allDataHash.get(anaParams[0])[SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE]) == null)
			{
				multisourcePlotPane.setDataSources(null);
				return;
			}
			String description = null;
			int totalROIlen = FRAPData.VFRAP_ROI_ENUM.values().length;
			boolean[] wantsROITypes = new boolean[totalROIlen];
			System.arraycopy(frapWorkspace.getFrapStudy().getSelectedROIsForErrorCalculation(), 0, wantsROITypes, 0, totalROIlen);
			
			ODESolverResultSet simSolverResultSet = new ODESolverResultSet();
			simSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
			for (int j = 0; j < totalROIlen; j++) {
				if(!wantsROITypes[j]){continue;}
				String currentROIName = FRAPData.VFRAP_ROI_ENUM.values()[j].name();
				String name = (description == null?/*"sim D="+diffusionRates[diffusionRateIndex]+"::"*/"":description)+currentROIName;
				simSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(name));
			}
			
			int totalWantedROIlen = 0;
			for(int i=0; i<wantsROITypes.length; i++)
			{
				if(wantsROITypes[i])
				{
					totalWantedROIlen ++;
				}
			}
			
			FRAPStudy fStudy = getFrapWorkspace().getFrapStudy();
			//
			// populate time
			//
			double[] shiftedSimTimes = getCurrentRawSimTimePoints();
			int startIndexRecovery = fStudy.getStartingIndexForRecovery();
			for (int j = 0; j < shiftedSimTimes.length; j++) {
				double[] row = new double[totalWantedROIlen+1];
				row[0] = shiftedSimTimes[j] + fStudy.getFrapData().getImageDataset().getImageTimeStamps()[startIndexRecovery];
				simSolverResultSet.addRow(row);
			}
			//
			// populate values
			//
			double[][] currentSimData = getCurrentSimResults();
			//populate sim data
			int columncounter = 0;
			for (int j = 0; j < totalROIlen; j++) {
				if(!wantsROITypes[j]){continue;}
					double[] values = currentSimData[j];
					for (int k = 0; k < values.length; k++) {
						simSolverResultSet.setValue(k, columncounter+1, values[k]);

		
					}
				columncounter++;
			}
			//get exp data and generate datasource to display
			DataSource[] selectedRowDataSourceArr = allDataHash.get(anaParams[0]);//anaParams[0] is the key in allDataHash to get the dataSource[]:exp & sim
			if(selectedRowDataSourceArr != null)
			{   //referenceData is the exp data
//				ReferenceData referenceData = (ReferenceData)selectedRowDataSourceArr[SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE].getSource();
				final DataSource expDataSource = /*new DataSource(referenceData,"exp")*/selectedRowDataSourceArr[SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE];
				//from simulation
				final DataSource simDataSource = new DataSource.DataSourceOdeSolverResultSet("sim", simSolverResultSet);
				DataSource[] newDataSourceArr = new DataSource[2];
				newDataSourceArr[SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE] = expDataSource;
				newDataSourceArr[SpatialAnalysisResults.ARRAY_INDEX_SIMDATASOURCE] = simDataSource;
				
				multisourcePlotPane.setDataSources(newDataSourceArr);
				multisourcePlotPane.selectAll();
				
			}
		}catch(Exception e2){
			e2.printStackTrace();
			DialogUtils.showErrorDialog(this,"Error graphing Optimizer data "+e2.getMessage());
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
		
	public void setData(final FRAPData fData, Parameter[] modelParams,final double[] frapDataTimeStamps,int startIndexForRecovery, boolean[] selectedROIs) throws Exception
	{
		double[] prebleachAverage = FRAPStudy.calculatePreBleachAverageXYZ(fData, startIndexForRecovery);
		spatialAnalysisResults = FRAPStudy.spatialAnalysis(null, startIndexForRecovery, frapDataTimeStamps[startIndexForRecovery], modelParams, fData, prebleachAverage);
		//allDataHash use AnalysisParameters as key, the value is dataSource[] which should have length as 2: expDataSource & simDataSouce
		allDataHash = spatialAnalysisResults.createSummaryReportSourceData(frapDataTimeStamps, startIndexForRecovery, selectedROIs, false);
		final SpatialAnalysisResults finalSpatialAnalysisResults = spatialAnalysisResults;
		try{
			
			multisourcePlotPane.forceXYRange(new Range(frapDataTimeStamps[0],frapDataTimeStamps[frapDataTimeStamps.length-1]), new Range(0,1.5));
	
			plotDerivedSimulationResults(finalSpatialAnalysisResults.getAnalysisParameters());
			
		}catch(Exception e){
			throw new RuntimeException("Error setting data to result panel for diffusion with one diffusing component.");
		}
	}
	
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
	

	public FRAPWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
    
	public void setFrapWorkspace(FRAPWorkspace frapWorkspace) {
		this.frapWorkspace = frapWorkspace;
//		getReactionDiffusionPanel().setFrapWorkspace(frapWorkspace);
	}
	
	public LocalWorkspace getLocalWorkspace() {
		return localWorkspace;
	}
    
	public void setLocalWorkspace(LocalWorkspace localWorkspace) {
		this.localWorkspace = localWorkspace;
//		getReactionDiffusionPanel().setFrapWorkspace(frapWorkspace);
	}
	
	public FRAPReactionDiffusionParamPanel getReactionDiffusionPanel() {
		return reactionDiffusionPanel;
	}
	
	public Parameter[] getCurrentParameters()
	{
		return getReactionDiffusionPanel().getCurrentParameters();
	}
	
		
	public void setReacBindingParams(Parameter[] parameters)
	{
		getReactionDiffusionPanel().setParameters(parameters);
	}
	
	public void activateReacDiffEstPanel()
	{
		FRAPStudy fStudy = getFrapWorkspace().getFrapStudy();
		//check if we can do auto estimation on reaction binding papameters
		Parameter[] params = null;
		if(fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] != null &&
		   fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters() != null)
		{
			params = fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters();
		}
		else
		{
			DialogUtils.showErrorDialog(EstParams_ReacBindingPanel.this, "Parameters from 'Diffusion with One Diffusing Component' model " +
					                    "is needed for automated estimation of reaction binding parameters.\n" +
					                    "Please include the above-mentioned model using 'Choose Model Types' wizard.");
			return;
		}

		if(estGuidePanel == null)
		{
			estGuidePanel = new FRAPReacDiffEstimationGuidePanel();
		}
		//set initial parameters from diffusion with one diffusing component
		estGuidePanel.setPrimaryParameters(params);
		
		int choice2 = DialogUtils.showComponentOKCancelDialog(EstParams_ReacBindingPanel.this, estGuidePanel, "FRAP Parameter Estimation Guide");
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
					 else if(name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_FreePartConc]))
					 {
						 reactionDiffusionPanel.setFreeFraction(val+"");
					 }
					 else if(name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_BWMRate]))
					 {
						 reactionDiffusionPanel.setBleachMonitorRate(val+"");
					 }
					 else if(name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_ComplexConc]))
					 {
						 reactionDiffusionPanel.setComplexFraction(val+"");
					 }
					 else if(name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_ReacPseudoOnRate]))
					 {
						 reactionDiffusionPanel.setOnRate(val+"");
					 }
					 else if(name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_ReacOffRate]))
					 {
						 reactionDiffusionPanel.setOffRate(val+"");
					 }
					 else if(name.equals(FRAPReacDiffEstimationGuidePanel.paramNames[FRAPReacDiffEstimationGuidePanel.IDX_Immobile]))
					 {
						 reactionDiffusionPanel.setImmobileFraction(val+"");
					 }
				}
				
//				multisourcePlotPane.setDataSources(null);
			}
		}
	}
	
	public double[][] getCurrentSimResults() {
		return currentSimResults;
	}

	public void setCurrentSimResults(double[][] currentSimResults) {
		this.currentSimResults = currentSimResults;
	}
	
	public double[] getCurrentRawSimTimePoints() {
		return currentSimTimePoints;
	}

	public void setCurrentRawSimTimePoints(double[] currentSimTimePoints) {
		this.currentSimTimePoints = currentSimTimePoints;
	}
	
	private void simulateWithCurrentParameters() 
	{
		fStudy = getFrapWorkspace().getFrapStudy();
		//save external files if needed
		AsynchClientTask saveTask = new AsynchClientTask("Preparing to run simulation ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				//check external data before running simulation
				if(!FRAPStudyPanel.areExternalDataOK(getLocalWorkspace(),fStudy.getFrapDataExternalDataInfo(), fStudy.getRoiExternalDataInfo()))
				{
					//if external files are missing/currupt or ROIs are changed, create keys and save them
					fStudy.setFrapDataExternalDataInfo(FRAPStudy.createNewExternalDataInfo(getLocalWorkspace(), FRAPStudy.IMAGE_EXTDATA_NAME));
					fStudy.setRoiExternalDataInfo(FRAPStudy.createNewExternalDataInfo(getLocalWorkspace(), FRAPStudy.ROI_EXTDATA_NAME));
					try {
						fStudy.saveROIsAsExternalData(getLocalWorkspace(), fStudy.getRoiExternalDataInfo().getExternalDataIdentifier(),fStudy.getStartingIndexForRecovery());
						fStudy.saveImageDatasetAsExternalData(getLocalWorkspace(), fStudy.getFrapDataExternalDataInfo().getExternalDataIdentifier(),fStudy.getStartingIndexForRecovery());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		
		//run simulation task
		AsynchClientTask runSimTask = new AsynchClientTask("Running simulation ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				BioModel bioModel = null;
				KeyValue simKey = null;
//				if(progressListener != null){
//					progressListener.updateMessage("Running Reference Simulation...");
//				}
				try{
					bioModel = FRAPStudy.createNewSimBioModel(fStudy, 
							                                  getCurrentParameters(), 
							                                  null, 
							                                  LocalWorkspace.createNewKeyValue(), 
							                                  LocalWorkspace.getDefaultOwner(),
							                                  fStudy.getStartingIndexForRecovery());
					//we don't have to set the bioModel to frap study, we just need the results.
					
					//change time bound and time step
//					Simulation sim = bioModel.getSimulations()[0];
//					sim.getSolverTaskDescription().setTimeBounds(getRefTimeBounds());
//					sim.getSolverTaskDescription().setTimeStep(getRefTimeStep());
//					sim.getSolverTaskDescription().setOutputTimeSpec(getRefTimeSpec());
					
			//		System.out.println("run FRAP Reference Simulation...");
//					final double RUN_REFSIM_PROGRESS_FRACTION = 1.0;
//					DataSetControllerImpl.ProgressListener runRefSimProgressListener =
//						new DataSetControllerImpl.ProgressListener(){
//							public void updateProgress(double progress) {
//								if(progressListener != null){
//									//To run to the steady state the time length is unpredictable. Progress increase 10 times
//									//because we manually set ending time to 1000 and usually it will reach steady state in less than 100 seconds.
//									//max allowed progress is 80%. this is heuristic.
//									progressListener.updateProgress(Math.min(0.8, (progress*10)*RUN_REFSIM_PROGRESS_FRACTION));
//								}
//							}
//							public void updateMessage(String message){
//								if(progressListener != null){
//									progressListener.updateMessage(message);
//								}
//							}
//					};
			
					//run simulation
					FRAPStudy.runFVSolverStandalone(
						new File(getLocalWorkspace().getDefaultSimDataDirectory()),
						new StdoutSessionLog(LocalWorkspace.getDefaultOwner().getName()),
						bioModel.getSimulations(0),
						fStudy.getFrapDataExternalDataInfo().getExternalDataIdentifier(),
						fStudy.getRoiExternalDataInfo().getExternalDataIdentifier(),
						this.getClientTaskStatusSupport(), false);
					
		//			//if reference simulation completes successfully, we save reference data info and remove old simulation files.
		//			getExpFrapStudy().setRefExternalDataInfo(refDataInfo);
		//			//we have to save again here, because if user doesn't press "save button" the reference simulation external info won't be saved.
		//			MicroscopyXmlproducer.writeXMLFile(getExpFrapStudy(), new File(getExpFrapStudy().getXmlFilename()), true, null, VirtualFrapMainFrame.SAVE_COMPRESSED);
		//			if(oldRefDataInfo != null && oldRefDataInfo.getExternalDataIdentifier() != null)
		//			{
		//				FRAPStudy.removeExternalDataAndSimulationFiles(oldRefDataInfo.getExternalDataIdentifier().getKey(), null, null, getLocalWorkspace());
		//			}
					
					simKey = bioModel.getSimulations()[0].getVersion().getVersionKey();
				}catch(Exception e){
					if(bioModel != null && bioModel.getSimulations() != null){
						FRAPStudy.removeExternalDataAndSimulationFiles(
							bioModel.getSimulations()[0].getVersion().getVersionKey(), null, null, getLocalWorkspace());
					}
					throw e;
				}
				
				//push sim key into hash table
				//for all loaded file
				hashTable.put(FRAPStudyPanel.SIMULATION_KEY, simKey);
			}
		};
		//generate dimension reduced sim data
		AsynchClientTask readDataTask = new AsynchClientTask("Reading simulation data ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				KeyValue simulationKey = (KeyValue)hashTable.get(FRAPStudyPanel.SIMULATION_KEY);
				if(simulationKey != null)
				{
					VCSimulationIdentifier vcSimID =
						new VCSimulationIdentifier(simulationKey,LocalWorkspace.getDefaultOwner());
					VCSimulationDataIdentifier vcSimDataID =
						new VCSimulationDataIdentifier(vcSimID,FieldDataFileOperationSpec.JOBINDEX_DEFAULT);
					double[] rawSimTimePoints = getLocalWorkspace().getVCDataManager().getDataSetTimes(vcSimDataID);
					//to store time points in frap model (simulation time points may be slightly different with exp time points)
					setCurrentRawSimTimePoints(rawSimTimePoints);
			//		refDataTimePoints = timeShiftForBaseDiffRate(rawRefDataTimePoints);
	//				System.out.println("simulation done...");
//					DataSetControllerImpl.ProgressListener reducedRefDataProgressListener =
//						new DataSetControllerImpl.ProgressListener(){
//							public void updateProgress(double progress) {
//								if(progressListener != null){
//									progressListener.setProgress((int)((.5+progress*(1-RUNSIM_PROGRESS_FRACTION))*100));
//								}
//							}
//							public void updateMessage(String message){
//								if(progressListener != null){
//									progressListener.setMessage(message);
//								}
//							}
//					};
					
					double[][] results =
						FRAPOptimization.dataReduction(getLocalWorkspace().getVCDataManager(),vcSimDataID,rawSimTimePoints,
								fStudy.getFrapData().getRois(),this.getClientTaskStatusSupport(), false);
					//to store data in frap model.
					setCurrentSimResults(results);
					System.out.println("generating dimension reduced ref data, done ....");
					
					//remove reference simulation files
					FRAPStudy.removeSimulationFiles(simulationKey, getLocalWorkspace());
				}
			}
		};
		
		//plot task
		AsynchClientTask plotTask = new AsynchClientTask("Generating plots ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				FRAPStudy fStudy = getFrapWorkspace().getFrapStudy();
				try {
					setData(fStudy.getFrapData(), 
							getCurrentParameters(), 
							fStudy.getFrapData().getImageDataset().getImageTimeStamps(),
							fStudy.getStartingIndexForRecovery(),
							fStudy.getSelectedROIsForErrorCalculation());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace(System.out);
				}
			}
		};
		
		
		//dispatch
		ClientTaskDispatcher.dispatch(EstParams_ReacBindingPanel.this, new Hashtable<String, Object>(), new AsynchClientTask[]{saveTask, runSimTask, readDataTask, plotTask}, false);
	}
	
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.Color;
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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import org.vcell.util.ColorUtil;
import org.vcell.util.Range;
import org.vcell.util.gui.DialogUtils;

import cbit.util.xml.XmlUtil;
import cbit.vcell.client.UserMessage;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.microscopy.AnalysisParameters;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.SpatialAnalysisResults;
import cbit.vcell.microscopy.batchrun.BatchRunXmlReader;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.gui.defineROIwizard.DefineROI_RoiForErrorPanel;
import cbit.vcell.microscopy.server.FrapDataUtils;
import cbit.vcell.modelopt.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;
import cbit.vcell.opt.Parameter;
import cbit.vcell.solver.ode.ODESolverResultSet;

@SuppressWarnings("serial")
public class EstParams_OneDiffComponentPanel extends JPanel {
	
	private SpatialAnalysisResults spatialAnalysisResults; //will be initialized in setData
	private JPanel paramPanel; //exclusively display pure diffusion panel and reaction diffusion panel
	

	private FRAPDiffOneParamPanel diffOnePanel;
	private JButton appParamButton;	
	private FRAPOptData frapOptData;
	private FRAPSingleWorkspace frapWorkspace;
	
	private MultisourcePlotPane multisourcePlotPane;
	private DefineROI_RoiForErrorPanel roiPanel;
	private Hashtable<AnalysisParameters, DataSource[]> allDataHash;
	private double[][] currentEstimationResults = null; //a data structure used to store results according to the current params. 
	
	public EstParams_OneDiffComponentPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {7,7,7,0,7};
		gridBagLayout.columnWidths = new int[] {7};
		setLayout(gridBagLayout);
		
		//set up tabbed pane for two kinds of models.
		paramPanel=new JPanel(new GridBagLayout());
		paramPanel.setForeground(new Color(0,0,244));
		paramPanel.setBorder(new EtchedBorder(Color.gray, Color.lightGray));
		
		//pure diffusion panel
		JLabel interactiveAnalysisLabel = new JLabel();
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.gridy = 0;
		gridBagConstraints_8.gridx = 0;
		paramPanel.add(interactiveAnalysisLabel, gridBagConstraints_8);
		interactiveAnalysisLabel.setFont(new Font("", Font.PLAIN, 14));
		interactiveAnalysisLabel.setText("Interactive Analysis on 'Diffusion with One Diffusing Component' Model using FRAP Simulation Results");

		diffOnePanel = new FRAPDiffOneParamPanel();
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.anchor = GridBagConstraints.SOUTH;
		gridBagConstraints_10.fill = GridBagConstraints.BOTH;
		gridBagConstraints_10.gridy = 1;
		gridBagConstraints_10.gridx = 0;
		gridBagConstraints_10.weightx = 1.5;
		gridBagConstraints_10.weighty = 2;
		paramPanel.add(diffOnePanel, gridBagConstraints_10);
		diffOnePanel.addPropertyChangeListener(
				new PropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent evt) {
						if(evt.getSource() == diffOnePanel){
							if((evt.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_OPTIMIZER_VALUE)))
							{
								plotDerivedSimulationResults(spatialAnalysisResults.getAnalysisParameters());
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
		gridBagLayout_1.columnWidths = new int[] {0, 0, 0, 0};
		panel_3.setLayout(gridBagLayout_1);
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.gridy = 1;
		gridBagConstraints_11.gridx = 0;
		add(panel_3, gridBagConstraints_11);

		final JLabel standardErrorRoiLabel = new JLabel();
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.gridx = 0;
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.insets = new Insets(2, 2, 2, 5);
		panel_3.add(standardErrorRoiLabel, gridBagConstraints_4);
		standardErrorRoiLabel.setFont(new Font("", Font.BOLD, 12));
		standardErrorRoiLabel.setText("Plot -  ROI Average Normalized (using Pre-Bleach Average) vs. Time          ");

		final JButton showRoisButton = new JButton();
		showRoisButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if(frapWorkspace != null && frapWorkspace.getWorkingFrapStudy() != null &&
				   frapWorkspace.getWorkingFrapStudy().getSelectedROIsForErrorCalculation() != null)
				{
					getROIPanel().setFrapWorkspace(frapWorkspace);
					getROIPanel().setCheckboxesForDisplay(frapWorkspace.getWorkingFrapStudy().getSelectedROIsForErrorCalculation());
					getROIPanel().refreshROIImageForDisplay();
				}
				JOptionPane.showMessageDialog(EstParams_OneDiffComponentPanel.this, getROIPanel());
			}
		});
		showRoisButton.setFont(new Font("", Font.PLAIN, 11));
		showRoisButton.setMargin(new Insets(0, 8, 0, 8));
		showRoisButton.setText("Show ROIs");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 0, 5);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 1;
		panel_3.add(showRoisButton, gridBagConstraints);

//		appParamButton = new JButton("Apply Batch Run Parmas");
//		appParamButton.setFont(new Font("", Font.PLAIN, 11));
//		appParamButton.setMargin(new Insets(0, 8, 0, 8));
//		final GridBagConstraints gridBagConstraints_appParam = new GridBagConstraints();
//		gridBagConstraints_appParam.gridy = 0;
//		gridBagConstraints_appParam.gridx = 4;
//		panel_3.add(appParamButton, gridBagConstraints_appParam);
//		appParamButton.addActionListener(new ActionListener() {
//			public void actionPerformed(final ActionEvent e) {
//				File inputFile = null;
//	  			int option = VirtualFrapLoader.openVFRAPBatchRunChooser.showOpenDialog(EstParams_OneDiffComponentPanel.this);
//	  			if (option == JFileChooser.APPROVE_OPTION){
//	  				inputFile = VirtualFrapLoader.openVFRAPBatchRunChooser.getSelectedFile();
//	  				loadBatchRunParameters(inputFile);
//	  			}else{
//	  				return;
//	  			}
//			}
//		});
		
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

	private DefineROI_RoiForErrorPanel getROIPanel()
	{
		if(roiPanel == null)
		{
			roiPanel = new DefineROI_RoiForErrorPanel();
		}
		return roiPanel;
	}
	
	private void plotDerivedSimulationResults(AnalysisParameters[] anaParams)
	{
		try{
			String description = null;
			int totalROIlen = FRAPData.VFRAP_ROI_ENUM.values().length;
			boolean[] wantsROITypes = new boolean[totalROIlen];
			System.arraycopy(frapWorkspace.getWorkingFrapStudy().getSelectedROIsForErrorCalculation(), 0, wantsROITypes, 0, totalROIlen);
			
			ODESolverResultSet fitOdeSolverResultSet = new ODESolverResultSet();
			fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
			for (int j = 0; j < totalROIlen; j++) {
				if(!wantsROITypes[j]){continue;}
				String currentROIName = FRAPData.VFRAP_ROI_ENUM.values()[j].name();
				String name = (description == null?/*"sim D="+diffusionRates[diffusionRateIndex]+"::"*/"":description)+currentROIName;
				fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(name));
			}
			
			int totalWantedROIlen = 0;
			for(int i=0; i<wantsROITypes.length; i++)
			{
				if(wantsROITypes[i])
				{
					totalWantedROIlen ++;
				}
			}
			//
			// populate time
			//
			double[] shiftedSimTimes = frapOptData.getReducedExpTimePoints();
			int startIndexRecovery = frapOptData.getExpFrapStudy().getStartingIndexForRecovery();
			for (int j = 0; j < shiftedSimTimes.length; j++) {
				double[] row = new double[totalWantedROIlen+1];
				row[0] = shiftedSimTimes[j] + frapOptData.getExpFrapStudy().getFrapData().getImageDataset().getImageTimeStamps()[startIndexRecovery];
				fitOdeSolverResultSet.addRow(row);
			}
			// populate values
			double[][] currentOptFitData = getPureDiffusionPanel().getCurrentFitData();
			//store results
			setCurrentEstimationResults(currentOptFitData);
			
			if(allDataHash != null && currentOptFitData != null)
			{
				//populate optimization data
				int columncounter = 0;
				for (int j = 0; j < totalROIlen; j++) {
					if(!wantsROITypes[j]){continue;}
//					if(!isSimData) //opt data
//					{
						double[] values = currentOptFitData[j];
						for (int k = 0; k < values.length; k++) {
							fitOdeSolverResultSet.setValue(k, columncounter+1, values[k]);
						}
//					}
					columncounter++;
				}
//				boolean hasSimData = false;
				
				
				DataSource[] selectedRowDataSourceArr = allDataHash.get(anaParams[0]);//anaParams[0] is the key in allDataHash to get the dataSource[]:exp & sim
				if(selectedRowDataSourceArr != null)
				{   //referenceData is the exp data
//					ReferenceData referenceData = (ReferenceData)selectedRowDataSourceArr[SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE];
					final DataSource expDataSource = selectedRowDataSourceArr[SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE];//new DataSource.DataSourceReferenceData("exp", referenceData);
					DataSource optDataSource  = new DataSource.DataSourceRowColumnResultSet("opt", fitOdeSolverResultSet);

					DataSource[] newDataSourceArr = new DataSource[2];
					newDataSourceArr[SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE] = expDataSource;
					newDataSourceArr[SpatialAnalysisResults.ARRAY_INDEX_SIMDATASOURCE] = optDataSource;
					if( currentOptFitData == null)
					{
						multisourcePlotPane.setDataSources(null);
					}
					else
					{
						//the following paragraph of code is just to get selected color for selected ROIs
						//and make them the same as we show on ChooseModel_RoiForErrorPanel/RoiForErrorPanel
						int validROISize = FRAPData.VFRAP_ROI_ENUM.values().length-2;//double valid ROI colors (not include cell and background)
						Color[] fullColors = ColorUtil.generateAutoColor(validROISize*2, getBackground(), new Integer(0));
						boolean[] selectedROIs = frapWorkspace.getWorkingFrapStudy().getSelectedROIsForErrorCalculation();
						int selectedROICounter = 0;
						for (int i=0; i<selectedROIs.length; i++)
						{
							if(selectedROIs[i])
							{
								selectedROICounter++;
							}
						}
						Color[] selectedColors = new Color[selectedROICounter*2];//double the size, each ROI is a comparison of exp and sim
						int selectedColorIdx = 0;
						for(int i=0; i<selectedROIs.length; i++)
						{
							if(selectedROIs[i] && i==0)
							{
								selectedColors[selectedColorIdx] = fullColors[i];
								selectedColors[selectedColorIdx+selectedROICounter] = fullColors[i+validROISize];
								selectedColorIdx++;
							}
							if(selectedROIs[i] && i>2) //skip cell and background ROIs
							{
								selectedColors[selectedColorIdx] = fullColors[i-2];
								selectedColors[selectedColorIdx+selectedROICounter] = fullColors[i-2+validROISize];
								selectedColorIdx++;
							}
						}
						int[] selectedIndices = multisourcePlotPane.getUnsortedSelectedIndices();
						multisourcePlotPane.setDataSources(newDataSourceArr, selectedColors);
						if(selectedIndices.length == 0)
						{
							multisourcePlotPane.selectAll();
						}
						else
						{
							multisourcePlotPane.setUnsortedSelectedIndices(selectedIndices);
						}
					}
				}
			}
		}catch(Exception e2){
			e2.printStackTrace();
			DialogUtils.showErrorDialog(this,"Error graphing Optimizer data "+e2.getMessage());
		}

	}

	public void clearSelectedPlotIndices()
	{
		multisourcePlotPane.clearSelection();
	}
	
	public void setData(final FRAPOptData frapOptData, final FRAPData fData, Parameter[] modelParams,final double[] frapDataTimeStamps,int startIndexForRecovery, boolean[] selectedROIs) throws Exception
	{
		this.frapOptData = frapOptData;
		double[] prebleachAverage = FrapDataUtils.calculatePreBleachAverageXYZ(fData, startIndexForRecovery);
		spatialAnalysisResults = FRAPStudy.spatialAnalysis(null, startIndexForRecovery, frapDataTimeStamps[startIndexForRecovery], modelParams, fData, prebleachAverage);
		//allDataHash use AnalysisParameters as key, the value is dataSource[] which should have length as 2: expDataSource & simDataSouce
		allDataHash = spatialAnalysisResults.createSummaryReportSourceData(frapDataTimeStamps, startIndexForRecovery, selectedROIs, false);
		final SpatialAnalysisResults finalSpatialAnalysisResults = spatialAnalysisResults;
		try{
			
			getPureDiffusionPanel().setData(frapOptData, modelParams);
			multisourcePlotPane.forceXYRange(new Range(frapDataTimeStamps[0],frapDataTimeStamps[frapDataTimeStamps.length-1]), new Range(0,1.5));
	
			plotDerivedSimulationResults(finalSpatialAnalysisResults.getAnalysisParameters());
			
		}catch(Exception e){
			throw new RuntimeException("Error setting data to result panel for diffusion with one diffusing component.");
		}
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
	
	public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace)
	{
		this.frapWorkspace = frapWorkspace;
		getPureDiffusionPanel().setFrapWorkspace(frapWorkspace);
	}
	
	private FRAPDiffOneParamPanel getPureDiffusionPanel() {
		return diffOnePanel;
	}
	
	public Parameter[] getCurrentParameters()
	{
		return getPureDiffusionPanel().getCurrentParameters();
	}
	
	public double[][] getCurrentEstimationResults() {
		return currentEstimationResults;
	}

	public void setCurrentEstimationResults(double[][] currentEstimationResults) {
		this.currentEstimationResults = currentEstimationResults;
	}
	
	public void setApplyBatchRunParamButtonVisible(boolean bVisible)
	{
		appParamButton.setVisible(false);
	}
	
	public void loadBatchRunParameters(File inFile)
	{ 
		String xmlString;
		try {
			xmlString = XmlUtil.getXMLString(inFile.getAbsolutePath());
			BatchRunXmlReader batchRunXmlReader = new BatchRunXmlReader();
			FRAPBatchRunWorkspace tempBatchRunWorkspace = batchRunXmlReader.getBatchRunWorkspace(XmlUtil.stringToXML(xmlString, null).getRootElement());
			Parameter[] parameters = tempBatchRunWorkspace.getAverageParameters(); 
			if(parameters != null && parameters.length >0 && parameters.length == FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF)
			{
				String paramMsg = "Replace the current parameters with the following parameter values: \n\n";
				for(int i=0; i<parameters.length; i++)
				{
					paramMsg = paramMsg + parameters[i].getName() + ": " + parameters[i].getInitialGuess() + "\n";
				}
				String choice = DialogUtils.showWarningDialog(this, paramMsg, new String[]{UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
				if(choice == UserMessage.OPTION_OK)
				{
					getPureDiffusionPanel().setParameterValues(parameters[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess(),
															   parameters[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess(), 
															   parameters[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess());
				}
			}
			else
			{
				throw new Exception("Parameters are null or number of applied parameters don't match the number (3) of parameters for diffusion with one diffusing component.");
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this, e.getMessage());
		}
	}
	
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			EstParams_OneDiffComponentPanel aPanel = new EstParams_OneDiffComponentPanel();
			frame.setContentPane(aPanel);
			frame.pack();
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
			
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}
}

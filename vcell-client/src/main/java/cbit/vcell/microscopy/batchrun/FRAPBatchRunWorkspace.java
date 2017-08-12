/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.batchrun;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Compare;
import org.vcell.util.DescriptiveStatistics;

import cbit.util.xml.XmlUtil;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPOptFunctions;
import cbit.vcell.microscopy.FRAPOptimizationUtils;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.MicroscopyXmlReader;
import cbit.vcell.microscopy.MicroscopyXmlproducer;
import cbit.vcell.microscopy.batchrun.gui.BatchRunResultsParamTableModel;
import cbit.vcell.microscopy.batchrun.gui.BatchRunResultsStatTableModel;
import cbit.vcell.microscopy.batchrun.gui.VirtualFrapBatchRunFrame;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.opt.Parameter;

public class FRAPBatchRunWorkspace extends FRAPWorkspace
{
	//Properties that are used in VFRAP Batch Run
	public static final String PROPERTY_CHANGE_BATCHRUN_DISPLAY_IMG = "BATCHRUN_DISPLAY_IMG";
	public static final String PROPERTY_CHANGE_BATCHRUN_DISPLAY_PARAM = "BATCHRUN_DISPLAY_PARAM";//property to disply results
	public static final String PROPERTY_CHANGE_BATCHRUN_DETAILS = "PROPERTY_CHANGE_BATCHRUN_DETAILS";//property when details button pressed in batch run results display panel
	public static final String PROPERTY_CHANGE_BATCHRUN_UPDATE_STATISTICS = "PROPERTY_CHANGE_BATCHRUN_UPDATE_STATISTICS";
	public static final String PROPERTY_CHANGE_BATCHRUN_CLEAR_RESULTS = "PROPERTY_CHANGE_BATCHRUN_CLEAR_RESULTS";
	
	//key string to pass variable values among client tasks
	public static final String BATCH_RUN_WORKSPACE_KEY = "Batch Run Workspace";
	
	private ArrayList<FRAPStudy> frapStudies = null;
	private PropertyChangeSupport propertyChangeSupport;
	private FRAPSingleWorkspace workingSingleWorkspace = null;
	private Object displaySelection = null;
	private int selectedModel;
	private Parameter[] averageParameters = null;

	private transient String batchRunXmlFileName = null;
	//first dimension: number of frap studys, second dimension: bleahed + 8 Rings + sum of MSE, any element that is not applicable should fill with 1e8.
	private transient double[][] analysisMSESummaryData = null;
	//first dimension: number of statistics (5, mean, std, median, min, max), second dimention: parameter sizes (6,prim diff rate, fraction,sec diff rate, fraction, bmr, immobile fraction), any element that is not applicable should fill with 1e8.
	private transient double[][] statisticsData = null;
	//temporary data structure to identify whether the current batch run doc needs a save or not
	private transient boolean bSaveNeeded = false;
	
	public FRAPBatchRunWorkspace() 
	{
		//initialize the working workspace
		workingSingleWorkspace = new FRAPSingleWorkspace();
		FRAPStudy fStudy = new FRAPStudy();
		frapStudies = new ArrayList<FRAPStudy>();
		workingSingleWorkspace.setFrapStudy(fStudy, true);
		
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener p) {
    	propertyChangeSupport.addPropertyChangeListener(p);
    }
  
    public void removePropertyChangeListener(PropertyChangeListener p) {
    	propertyChangeSupport.removePropertyChangeListener(p);
    }
    
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    	propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
	public FRAPSingleWorkspace getWorkingSingleWorkspace() {
		return workingSingleWorkspace;
	}
	
	public  ArrayList<FRAPStudy> getFrapStudies() {
		return frapStudies;
	}
	
	public FRAPStudy getFRAPStudy(String fileName) //file name(with full path info.)
	{
		for(int i=0; i<frapStudies.size(); i++)
		{
			if(Compare.isEqual(frapStudies.get(i).getXmlFilename(), fileName))
			{
				return frapStudies.get(i);
			}
		}
		return null;
	}
	
	public void clearWorkingSingleWorkspace()
	{
		FRAPStudy newFrapStudy = new FRAPStudy();
		getWorkingSingleWorkspace().setFrapStudy(newFrapStudy, true, true);
		String oldString = "Before clear all";
		String newString = "After clear all";
		firePropertyChange(PROPERTY_CHANGE_BATCHRUN_DISPLAY_IMG, oldString, newString);
	}
	
	public void setWorkingFRAPStudy(FRAPStudy fStudy)
	{
		getWorkingSingleWorkspace().setFrapStudy(fStudy, true, true);
	}
	
	public void addFrapStudy(FRAPStudy newFrapStudy) 
	{
		File newFile = new File(newFrapStudy.getXmlFilename());
		ArrayList<FRAPStudy> frapStudies = getFrapStudies();
		int size = frapStudies.size();
		int insertIdx = size;
		for(int i=0; i<size; i++)
		{
			File xmlFile = new File(frapStudies.get(i).getXmlFilename());
			if(newFile.getName().compareTo(xmlFile.getName())==0)
			{
				if(newFile.getAbsolutePath().compareTo(xmlFile.getAbsolutePath())<0)
				{
					insertIdx = i;
					break;
				}
			}
			else if(newFile.getName().compareTo(xmlFile.getName())<0)
			{
				insertIdx = i;
				break;
			}
		}
		if(insertIdx == size)
		{
			frapStudies.add(newFrapStudy);
		}
		else
		{
			frapStudies.add(insertIdx, newFrapStudy);
		}
		//enable save button
		VirtualFrapBatchRunFrame.enableSave(frapStudies != null && frapStudies.size() > 0);
	}
	
	public void removeFrapStudy(FRAPStudy frapStudy)
	{
		frapStudies.remove(frapStudy);
		if(frapStudies.size() < 1)
		{
			clearResultData();
			//disable save button
			VirtualFrapBatchRunFrame.enableSave(false);
		}
		else
		{
			if(isBatchRunResultsAvailable())
			{
				refreshBatchRunResults();
			}
		}
	}
	
	public void removeAllFrapStudies()
	{
		frapStudies.clear();
		clearResultData();
		//disable save button
		VirtualFrapBatchRunFrame.enableSave(false);
	}
	
	public FRAPStudy getWorkingFrapStudy() //the working FRAPStudy
	{
		return getWorkingSingleWorkspace().getWorkingFrapStudy();
	}
	
	public void updateDisplayForTreeSelection(Object selection)
	{
		String oldString = null;
		if (displaySelection instanceof File)//doc
		{
			oldString =  ((File)displaySelection).getAbsolutePath();
		}
		else if(displaySelection instanceof String)//results
		{
			oldString = ((String)displaySelection);
		}
		displaySelection = selection;
		if(selection instanceof File)//doc
		{
			String newString = ((File)selection).getAbsolutePath();
			FRAPStudy selectedFrapStudy = getFRAPStudy(newString);
			setWorkingFRAPStudy(selectedFrapStudy);
			firePropertyChange(PROPERTY_CHANGE_BATCHRUN_DISPLAY_IMG, oldString, newString);
		}
		else if(selection instanceof String)//results
		{
			firePropertyChange(PROPERTY_CHANGE_BATCHRUN_DISPLAY_PARAM, oldString, (selection));
		}
	}
	
	public void clearStoredTreeSelection()
	{
		displaySelection = null;
	}
	
	public int getSelectedModel() {
		return selectedModel;
	}

	public void setSelectedModel(int selectedModel) {
		this.selectedModel = selectedModel;
	}
	
	public void refreshModels(boolean[] modelBooleans)
	{
		for(int i =0; i<FRAPModel.NUM_MODEL_TYPES; i++)
		{
			if(modelBooleans[i])
			{
				setSelectedModel(i);
				break;
			}
		}
		//update FRAPModels of FrapStudies in the BatchRun
		for(int i=0; i<getFrapStudies().size(); i++)
		{
			getFrapStudies().get(i).refreshModels(modelBooleans);
		}
	}
	
	public void refreshMSESummaryData()
	{
		int studySize = getFrapStudies().size();
		analysisMSESummaryData = new double[studySize][FRAPData.VFRAP_ROI_ENUM.values().length-2+1];
		for(int i=0; i<studySize; i++)
		{
			FRAPStudy fStudy = getFrapStudies().get(i);
			fStudy.createAnalysisMSESummaryData();
			analysisMSESummaryData[i] = fStudy.getAnalysisMSESummaryData()[getSelectedModel()];
		}
	}
	
	public double[][] getAnalysisMSESummaryData() 
	{
		if(analysisMSESummaryData == null)
		{
			refreshMSESummaryData();
		}
		return analysisMSESummaryData;
	}
	
	public void setAnalysisMSESummaryData(double[][] analysisMSESummaryData) {
		this.analysisMSESummaryData = analysisMSESummaryData;
	}
	
	public void refreshStatisticsData()
	{
		int studySize = getFrapStudies().size();
		//get parameters array (column-name and column-details should be excluded)
		//parameterVals[0] primaryDiffRates, parameterVals[1] primaryMobiles,parameterVals[2]bwmRates
		//parameterVals[3] secDiffRates,parameterVals[4] secMobiles,parameterVals[5] bs_concentration
		//parameterVals[6] reaction on rate, parameterVals[7] reacton off rate, parameterVals[8] immobile
		double[][] parameterVals = new double[BatchRunResultsParamTableModel.NUM_COLUMNS-2][studySize];
		for(int i=0; i<(BatchRunResultsParamTableModel.NUM_COLUMNS-2); i++)
		{
			//fill all elements with 1e8 first
			Arrays.fill(parameterVals[i], FRAPOptimizationUtils.largeNumber);
		}
		
		for(int i=0; i<studySize; i++)
		{
			FRAPStudy fStudy = getFrapStudies().get(i);
			if(selectedModel == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT)
			{
				FRAPModel fModel = fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT];
				parameterVals[0][i] = fModel.getModelParameters()[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess();
				parameterVals[1][i] = fModel.getModelParameters()[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess();
				parameterVals[2][i] = fModel.getModelParameters()[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess();
				parameterVals[8][i] = Math.max(0, (1 - parameterVals[1][i]));//immobile fraction
			}
			else if (selectedModel == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
			{
				FRAPModel fModel = fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS];
				parameterVals[0][i] = fModel.getModelParameters()[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess();
				parameterVals[1][i] = fModel.getModelParameters()[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess();
				parameterVals[2][i] = fModel.getModelParameters()[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess();
				parameterVals[3][i] = fModel.getModelParameters()[FRAPModel.INDEX_SECONDARY_DIFF_RATE].getInitialGuess();
				parameterVals[4][i] = fModel.getModelParameters()[FRAPModel.INDEX_SECONDARY_FRACTION].getInitialGuess();
				parameterVals[8][i] = Math.max(0, (1 - parameterVals[1][i] - parameterVals[4][i]));//immobile fraction
			}
			else if (selectedModel == FRAPModel.IDX_MODEL_REACTION_OFF_RATE)
			{
				FRAPModel fModel = fStudy.getModels()[FRAPModel.IDX_MODEL_REACTION_OFF_RATE];
				parameterVals[2][i] = fModel.getModelParameters()[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess();
				parameterVals[5][i] = fModel.getModelParameters()[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION].getInitialGuess();
				parameterVals[7][i] = fModel.getModelParameters()[FRAPModel.INDEX_OFF_RATE].getInitialGuess();
			}
		}
		
		double[][] oldStatData = statisticsData;
		//stores statistics for each parameters . (column-name and column-details should be excluded)
		statisticsData = new double[BatchRunResultsStatTableModel.NUM_STATISTICS][BatchRunResultsParamTableModel.NUM_COLUMNS-2];
		
		for(int i=0; i<BatchRunResultsStatTableModel.NUM_STATISTICS; i++)
		{
			//fill all elements with 1e8 first
			Arrays.fill(statisticsData[i], FRAPOptimizationUtils.largeNumber);
		}
		
		for(int i=0; i<BatchRunResultsParamTableModel.NUM_COLUMNS-2; i++)
		{
			//statistics for parameters exist(which is not saved as 1e8)
			if(parameterVals[i][0] != FRAPOptimizationUtils.largeNumber)
			{
				DescriptiveStatistics stat = DescriptiveStatistics.CreateBasicStatistics(parameterVals[i]);
				statisticsData[BatchRunResultsStatTableModel.ROW_IDX_AVERAGE][i] = stat.getMean();
				statisticsData[BatchRunResultsStatTableModel.ROW_IDX_STD][i] = stat.getStandardDeviation();
				statisticsData[BatchRunResultsStatTableModel.ROW_IDX_MEDIAN][i] = stat.getMedian();
				statisticsData[BatchRunResultsStatTableModel.ROW_IDX_MIN][i] = stat.getMin();
				statisticsData[BatchRunResultsStatTableModel.ROW_IDX_MAX][i] = stat.getMax();
			}
		}
		updateAverageParameters();
		firePropertyChange(PROPERTY_CHANGE_BATCHRUN_UPDATE_STATISTICS, oldStatData, statisticsData);
	}
	
	private void updateAverageParameters() 
	{
		Parameter[] oldParameters = null; //used to save old parameters to check if save is needed
		if(selectedModel == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT)
		{
			Parameter diff = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE],
								FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound(), 
								FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
								FRAPModel.REF_DIFFUSION_RATE_PARAM.getScale(),
			                    statisticsData[BatchRunResultsStatTableModel.ROW_IDX_AVERAGE][BatchRunResultsParamTableModel.COLUMN_PRI_DIFF_RATE-1]);
			Parameter mobileFrac = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION],
								FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
								FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
								FRAPModel.REF_MOBILE_FRACTION_PARAM.getScale(),
			                    statisticsData[BatchRunResultsStatTableModel.ROW_IDX_AVERAGE][BatchRunResultsParamTableModel.COLUMN_PRI_MOBILE_FRACTION-1]);
			Parameter bleachWhileMonitoringRate = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE],
								FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
								FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
								FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(),
			                    statisticsData[BatchRunResultsStatTableModel.ROW_IDX_AVERAGE][BatchRunResultsParamTableModel.COLUMN_BMR-1]);
			//get old parameters
			oldParameters = getAverageParameters();
			setAverageParameters(new Parameter[]{diff, mobileFrac, bleachWhileMonitoringRate});
		}
		else if (selectedModel == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
		{
			Parameter diff = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE], 
								FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound(),
								FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
								FRAPModel.REF_DIFFUSION_RATE_PARAM.getScale(),
			                    statisticsData[BatchRunResultsStatTableModel.ROW_IDX_AVERAGE][BatchRunResultsParamTableModel.COLUMN_PRI_DIFF_RATE-1]);
			Parameter mobileFrac = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION],
								FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
								FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
								FRAPModel.REF_MOBILE_FRACTION_PARAM.getScale(), 
			                    statisticsData[BatchRunResultsStatTableModel.ROW_IDX_AVERAGE][BatchRunResultsParamTableModel.COLUMN_PRI_MOBILE_FRACTION-1]);
			Parameter bleachWhileMonitoringRate = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE], 
								FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
								FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
								FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(), 
					            statisticsData[BatchRunResultsStatTableModel.ROW_IDX_AVERAGE][BatchRunResultsParamTableModel.COLUMN_BMR-1]);
			Parameter secDiffRate = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_DIFF_RATE],
								FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound(),
								FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound(),
								FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getScale(), 
			                    statisticsData[BatchRunResultsStatTableModel.ROW_IDX_AVERAGE][BatchRunResultsParamTableModel.COLUMN_SEC_DIFF_RATE-1]);
			Parameter secMobileFrac = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_FRACTION],
								FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound(),
								FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound(),
								FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getScale(),
			                    statisticsData[BatchRunResultsStatTableModel.ROW_IDX_AVERAGE][BatchRunResultsParamTableModel.COLUMN_SEC_MOBILE_FRACTION-1]);
			//get old parameters
			oldParameters = getAverageParameters();
			setAverageParameters(new Parameter[]{diff, mobileFrac, bleachWhileMonitoringRate, secDiffRate, secMobileFrac});
		}
		else if (selectedModel == FRAPModel.IDX_MODEL_REACTION_OFF_RATE)
		{
			Parameter bleachWhileMonitoringRate = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE], 
								FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
								FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
								FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(), 
					            statisticsData[BatchRunResultsStatTableModel.ROW_IDX_AVERAGE][BatchRunResultsParamTableModel.COLUMN_BMR-1]);
			Parameter fittingParam = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION], 
								FRAPModel.REF_BS_CONCENTRATION_OR_A.getLowerBound(),
								FRAPModel.REF_BS_CONCENTRATION_OR_A.getUpperBound(),
								FRAPModel.REF_BS_CONCENTRATION_OR_A.getScale(), 
					            statisticsData[BatchRunResultsStatTableModel.ROW_IDX_AVERAGE][BatchRunResultsParamTableModel.COLUMN_BS_CONCENTRATION-1]);
			Parameter offRate = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_OFF_RATE],
								FRAPModel.REF_REACTION_OFF_RATE.getLowerBound(),
								FRAPModel.REF_REACTION_OFF_RATE.getUpperBound(),
								FRAPModel.REF_REACTION_OFF_RATE.getScale(),
			                    statisticsData[BatchRunResultsStatTableModel.ROW_IDX_AVERAGE][BatchRunResultsParamTableModel.COLUMN_OFF_RATE-1]);
			//get old parameters
			oldParameters = getAverageParameters();
			setAverageParameters(new Parameter[]{null, null, bleachWhileMonitoringRate,null, null, fittingParam, null,offRate});
		}
		//check to see if we need to set save flag
		boolean bSaveNeeded = true;
		if(oldParameters != null && getAverageParameters() != null)
		{
			if(oldParameters.length != getAverageParameters().length)
			{
				bSaveNeeded = false;
			}
			for(int i = 0; i < oldParameters.length; i++)
			{
				if(!Compare.isEqualOrNull(oldParameters[i], getAverageParameters()[i]))
				{
					bSaveNeeded = false;
				}
			}
		}
		else if(oldParameters == null && getAverageParameters() == null)
		{
			bSaveNeeded = false;
		}
		setSaveNeeded(bSaveNeeded);
	}

	public double[][] getStatisticsData()
	{
		if(statisticsData == null)
		{
			refreshStatisticsData();
		}
		return statisticsData;
	}
	
	public void setStatisticsData(double[][] statisticsData) {
		this.statisticsData = statisticsData;
	}
	
	public void refreshBatchRunResults()
	{
		refreshStatisticsData();
		refreshMSESummaryData();
	}
	
	public void clearResultData()
	{
		analysisMSESummaryData = null;
		statisticsData = null;
		setAverageParameters(null);
	}
	
	public boolean isBatchRunResultsAvailable()
	{
		return getAverageParameters()!= null && getAverageParameters().length > 0;
	}
	
	public Parameter[] getAverageParameters() {
		return averageParameters;
	}

	public void setAverageParameters(Parameter[] averageParameters) {
		this.averageParameters = averageParameters;
	}
	
	public String getBatchRunXmlFileName() {
		return batchRunXmlFileName;
	}

	public void setBatchRunXmlFileName(String batchRunXmlFileName) {
		this.batchRunXmlFileName = batchRunXmlFileName;
	}
	
	public boolean isSaveNeeded() {
		return bSaveNeeded;
	}

	public void setSaveNeeded(boolean bNeedSave) {
		this.bSaveNeeded = bNeedSave;
	}
	
	//If users select reaction off rate model for batch run, the only ROI applied must be bleached ROI
	public void setSelectedROIForReactionOffRate()
	{
		ArrayList<FRAPStudy> frapStudies = this.getFrapStudies();
		boolean[] selectedROIs = FRAPStudy.createSelectedROIsForReactionOffRateModel();
		for(FRAPStudy fStudy : frapStudies)
		{
			fStudy.setSelectedROIsForErrorCalculation(selectedROIs);
		}
	}
	
	public boolean isROISelectedApplicableToReactionOffRateModel()
	{
		ArrayList<FRAPStudy> frapStudies = this.getFrapStudies();
		for(FRAPStudy fStudy : frapStudies)
		{
			if(fStudy.getNumSelectedROIs() != 1)
			{
				return false;
			}
			else
			{	//selected ROI is not bleached.
				if(!fStudy.getSelectedROIsForErrorCalculation()[0])
				{
					return false;
				}
			}
		}
		return true;
	}
	
	/*used when opening a vfrap batch file. Batch run is read into a 
	  temp batch run workspace. If no errors occur, move the info from temp to the working batch run workspace */
	public void update(FRAPBatchRunWorkspace tempBatchRunWorkspace)
	{
		//frapStudyList
		this.frapStudies = tempBatchRunWorkspace.getFrapStudies();
		
		this.displaySelection = null;
		this.selectedModel = tempBatchRunWorkspace.getSelectedModel();
		this.averageParameters = tempBatchRunWorkspace.getAverageParameters();
		this.batchRunXmlFileName = tempBatchRunWorkspace.getBatchRunXmlFileName();
		//set save flag
		setSaveNeeded(false);
		this.analysisMSESummaryData = null;
		this.statisticsData = null;
	}
	
	//get client task for saving single vfrap files
	public AsynchClientTask getSaveSingleFilesTask()
	{
		AsynchClientTask saveSingleFileTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				int size = getFrapStudies().size();
				for(int i=0; i < getFrapStudies().size(); i++)
				{
					FRAPStudy fStudy = getFrapStudies().get(i);
					File outFile = new File(fStudy.getXmlFilename());
					this.getClientTaskStatusSupport().setMessage("Saving file " + (i+1) + " of " + size + " :"+ outFile.getAbsolutePath()+" ...");
					saveProcedure(outFile, fStudy, this.getClientTaskStatusSupport());
				}
			}
		};
		return saveSingleFileTask;
	}
	
	//get client task for saving a batch run file
	public AsynchClientTask getSaveBatchRunFileTask()
	{
		AsynchClientTask saveBatchFileTask = new AsynchClientTask("Saving Batchrun file ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				File outFile = (File)hashTable.get(FRAPStudyPanel.SAVE_FILE_NAME_KEY);
				//save a Batchrun file
				BatchRunXmlProducer.writeXMLFile(FRAPBatchRunWorkspace.this, outFile);
				setBatchRunXmlFileName(outFile.getAbsolutePath());
				//after saving singles files and the batch xml file, set save needed flag to false.
				setSaveNeeded(false);
			}
		};
		return saveBatchFileTask;
	}
	//save procedure for saving a single vfrap file
	private void saveProcedure(File xmlFrapFile, FRAPStudy fStudy, ClientTaskStatusSupport progressListener) throws Exception
	{
		//save a single vfrap file
		MicroscopyXmlproducer.writeXMLFile(fStudy, xmlFrapFile, true,progressListener,VirtualFrapMainFrame.SAVE_COMPRESSED);
		fStudy.setXmlFilename(xmlFrapFile.getAbsolutePath());
	}
	
	//get client task for loading a vfrap batch run file
	public AsynchClientTask getLoadBatchRunFileTask(File inputFile)
	{
		final File inFile = inputFile;
		AsynchClientTask loadTask = new AsynchClientTask("Loading "+inFile.getAbsolutePath()+"...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{ 
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				FRAPBatchRunWorkspace tempBatchRunWorkspace = null;
				if(inFile.getName().endsWith("."+VirtualFrapLoader.VFRAP_BATCH_EXTENSION) || inFile.getName().endsWith(".xml")) //.vfbatch
				{
					String xmlString = XmlUtil.getXMLString(inFile.getAbsolutePath());
					BatchRunXmlReader batchRunXmlReader = new BatchRunXmlReader();
					tempBatchRunWorkspace = batchRunXmlReader.getBatchRunWorkspace(XmlUtil.stringToXML(xmlString, null).getRootElement());
					tempBatchRunWorkspace.setBatchRunXmlFileName(inFile.getAbsolutePath());
					hashTable.put(BATCH_RUN_WORKSPACE_KEY, tempBatchRunWorkspace);
				}
			}
		};
		return loadTask;
	}
	
	//get client task for loading each single vfrap files in a batch run
	public AsynchClientTask getLoadSingleFilesTask(LocalWorkspace arg_localWorkspace)
	{
		final LocalWorkspace localWorkspace = arg_localWorkspace;	
		AsynchClientTask openSingleFilesTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				FRAPBatchRunWorkspace tempBatchRunWorkspace = (FRAPBatchRunWorkspace)hashTable.get(BATCH_RUN_WORKSPACE_KEY);
				if(tempBatchRunWorkspace != null)
				{
					ArrayList<FRAPStudy> fStudyList = tempBatchRunWorkspace.getFrapStudies();
					int size = fStudyList.size();
					for(int i=0; i < size; i++)
					{
						String fileName = fStudyList.get(i).getXmlFilename();
						File fStudyFile = new File(fileName);
						if(fileName.endsWith("."+VirtualFrapLoader.VFRAP_EXTENSION) || fileName.endsWith(".xml")) //.vfrap
						{
							this.getClientTaskStatusSupport().setMessage("Loading(.vfrap) " + (i+1) + " of " + size + " : " + fileName);
							FRAPStudy newFRAPStudy = null;
							String xmlString = XmlUtil.getXMLString(fStudyFile.getAbsolutePath());
							MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
							newFRAPStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null).getRootElement(),this.getClientTaskStatusSupport());
//							if((newFRAPStudy.getFrapDataExternalDataInfo() != null || newFRAPStudy.getRoiExternalDataInfo() != null) &&
//								!FRAPWorkspace.areExternalDataOK(localWorkspace,newFRAPStudy.getFrapDataExternalDataInfo(),newFRAPStudy.getRoiExternalDataInfo()))
//							{
//								throw new Exception("External Files of Frap Document " + fStudyFile.getAbsolutePath() + " are corrupted");
//							}
							newFRAPStudy.setXmlFilename(fileName);
							//get dimentsion reduced experimental data
							newFRAPStudy.getDimensionReducedExpData();
							//restore the dimension reduced fitting data(2 dimensional array).
							int selectedModelIdx = tempBatchRunWorkspace.getSelectedModel();
							FRAPModel frapModel = newFRAPStudy.getFrapModel(selectedModelIdx);
							//optimization was done but data wasn't save with file, need to restore data
							if(frapModel!= null && frapModel.getModelParameters() != null && frapModel.getModelParameters().length > 0 && frapModel.getData() == null)
							{
								if(frapModel.getModelIdentifer().equals(FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_REACTION_OFF_RATE]))
								{
									FRAPOptFunctions frapOptFunc = new FRAPOptFunctions(newFRAPStudy);
									newFRAPStudy.setFrapOptFunc(frapOptFunc);
									frapModel.setData(frapOptFunc.getFitData(frapModel.getModelParameters()));
								}
								else 
								{
									FRAPOptData optData = new FRAPOptData(newFRAPStudy, frapModel.getModelParameters().length, localWorkspace, newFRAPStudy.getStoredRefData());
									newFRAPStudy.setFrapOptData(optData);
									frapModel.setData(optData.getFitData(frapModel.getModelParameters()));
								}
							}
							tempBatchRunWorkspace.getFrapStudies().remove(i);
							tempBatchRunWorkspace.getFrapStudies().add(i, newFRAPStudy);
						}
					}
					//save loaded tempBatchRunWorkspace to hashtable
					hashTable.put(BATCH_RUN_WORKSPACE_KEY, tempBatchRunWorkspace);
				}
			}
		};
		return openSingleFilesTask;
	}
}

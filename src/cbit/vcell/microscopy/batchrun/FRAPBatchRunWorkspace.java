package cbit.vcell.microscopy.batchrun;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.vcell.util.Compare;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.DescriptiveStatistics;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptimization;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.batchrun.gui.BatchRunResultsParamTableModel;

public class FRAPBatchRunWorkspace extends FRAPWorkspace
{
	//Properties that are used in VFRAP Batch Run
	public static final String PROPERTY_CHANGE_BATCHRUN_DISPLAY_IMG = "BATCHRUN_DISPLAY_IMG";
	public static final String PROPERTY_CHANGE_BATCHRUN_DISPLAY_PARAM = "BATCHRUN_DISPLAY_PARAM";
	
	private ArrayList<FRAPStudy> frapStudyList = null;
	private PropertyChangeSupport propertyChangeSupport;
	private FRAPSingleWorkspace workingSingleWorkspace = null;
	private Object displaySelection = null;
	private int selectedModel;
	private boolean[] selectedROIsForErrCalculation = null;
	//first dimension: number of frap studys, second dimension: bleahed + 8 Rings + sum of MSE, any element that is not applicable should fill with 1e8.
	private double[][] analysisMSESummaryData = null;
	//first dimension: number of frap studys, second dimention: maximum parameter sizes (5 so far), any element that is not applicable should fill with 1e8.
	private double[][] statisticsData = null;
	
	public FRAPBatchRunWorkspace() 
	{
		//initialize the working workspace
		workingSingleWorkspace = new FRAPSingleWorkspace();
		FRAPStudy fStudy = new FRAPStudy();
		frapStudyList = new ArrayList<FRAPStudy>();
		workingSingleWorkspace.setFrapStudy(fStudy, true);
		
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener p) {
    	propertyChangeSupport.addPropertyChangeListener(p);
    }
  
    public void removePropertyChangeListener(PropertyChangeListener p) {
    	propertyChangeSupport.removePropertyChangeListener(p);
    }
    
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    	propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
	public FRAPSingleWorkspace getWorkingSingleWorkspace() {
		return workingSingleWorkspace;
	}
	
	public  ArrayList<FRAPStudy> getFrapStudyList() {
		return frapStudyList;
	}
	
	public FRAPStudy getFRAPStudy(String fileName) //file name(with full path info.)
	{
		for(int i=0; i<frapStudyList.size(); i++)
		{
			if(Compare.isEqual(frapStudyList.get(i).getXmlFilename(), fileName))
			{
				return frapStudyList.get(i);
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
		frapStudyList.add(newFrapStudy);
	}
	
	public void removeFrapStudy(FRAPStudy frapStudy)
	{
		frapStudyList.remove(frapStudy);
	}
	
	public void removeAllFrapStudies()
	{
		frapStudyList.clear();
	}
	
	public FRAPStudy getWorkingFrapStudy() //the working FRAPStudy
	{
		return getWorkingSingleWorkspace().getWorkingFrapStudy();
	}
	
	public void updateDisplayForTreeSelection(Object selection)
	{
		String oldString = null;
		if (displaySelection instanceof File)
		{
			oldString =  ((File)displaySelection).getAbsolutePath();
		}
		else if(displaySelection instanceof String)
		{
			oldString = ((String)displaySelection);
		}
		displaySelection = selection;
		if(selection instanceof File)
		{
			String newString = ((File)selection).getAbsolutePath();
			FRAPStudy selectedFrapStudy = getFRAPStudy(newString);
			setWorkingFRAPStudy(selectedFrapStudy);
			firePropertyChange(PROPERTY_CHANGE_BATCHRUN_DISPLAY_IMG, oldString, newString);
		}
		else if(selection instanceof String)
		{
			firePropertyChange(PROPERTY_CHANGE_BATCHRUN_DISPLAY_PARAM, oldString, ((String)selection));
		}
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
		for(int i=0; i<getFrapStudyList().size(); i++)
		{
			getFrapStudyList().get(i).refreshModels(modelBooleans);
		}
	}
	
	public void setSelectedROIsForErrorCalculation(boolean[] arg_selectedROIs)
	{
		selectedROIsForErrCalculation = arg_selectedROIs;
		//update selectedROIs of FrapStudies in the BatchRun
		for(int i=0; i<getFrapStudyList().size(); i++)
		{
			getFrapStudyList().get(i).setSelectedROIsForErrorCalculation(arg_selectedROIs);
		}
	}
	
	public boolean[] getSelectedROIsForErrorCalculation()
	{
		return selectedROIsForErrCalculation;
	}

	public void refreshMSESummaryData()
	{
		int studySize = getFrapStudyList().size();
		analysisMSESummaryData = new double[studySize][FRAPData.VFRAP_ROI_ENUM.values().length-2+1];
		for(int i=0; i<studySize; i++)
		{
			FRAPStudy fStudy = getFrapStudyList().get(i);
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
	
	public void refreshStatisticsData()
	{
		int studySize = getFrapStudyList().size();
		//get parameters array (column-name and column-details should be excluded)
		//parameterVals[0] primaryDiffRates, parameterVals[1] primaryMobiles,parameterVals[2]secDiffRates
		//parameterVals[3] secMobiles,parameterVals[4] bwmRates,parameterVals[5]immobiles
		double[][] parameterVals = new double[BatchRunResultsParamTableModel.NUM_COLUMNS-2][studySize];
		
		for(int i=0; i<studySize; i++)
		{
			FRAPStudy fStudy = getFrapStudyList().get(i);
			if(selectedModel == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT)
			{
				FRAPModel fModel = fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT];
				parameterVals[0][i] = fModel.getModelParameters()[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess();
				parameterVals[1][i] = fModel.getModelParameters()[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess();
				parameterVals[4][i] = fModel.getModelParameters()[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess();
				parameterVals[5][i] = Math.max(0, (1 - parameterVals[1][i]));
			}
			else if (selectedModel == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
			{
				FRAPModel fModel = fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS];
				parameterVals[0][i] = fModel.getModelParameters()[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess();
				parameterVals[1][i] = fModel.getModelParameters()[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess();
				parameterVals[2][i] = fModel.getModelParameters()[FRAPModel.INDEX_SECONDARY_DIFF_RATE].getInitialGuess();
				parameterVals[3][i] = fModel.getModelParameters()[FRAPModel.INDEX_SECONDARY_FRACTION].getInitialGuess();
				parameterVals[4][i] = fModel.getModelParameters()[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess();
				parameterVals[5][i] = Math.max(0, (1 - parameterVals[1][i] - parameterVals[3][i]));
			}
		}
		//stores statistics for each parameters . (column-name and column-details should be excluded)
		statisticsData = new double[BatchRunResultsParamTableModel.NUM_STATISTICS][BatchRunResultsParamTableModel.NUM_COLUMNS-2];
		
		for(int i=0; i<BatchRunResultsParamTableModel.NUM_STATISTICS; i++)
		{
			//fill all elements with 1e8 first
			Arrays.fill(statisticsData[i], FRAPOptimization.largeNumber);
		}
		
		for(int i=0; i<BatchRunResultsParamTableModel.NUM_COLUMNS-2; i++)
		{
			//statistics for primary diffusion rate
			DescriptiveStatistics stat = DescriptiveStatistics.CreateBasicStatistics(parameterVals[i]);
			statisticsData[BatchRunResultsParamTableModel.ROW_IDX_AVERAGE][i] = stat.getMean();
			statisticsData[BatchRunResultsParamTableModel.ROW_IDX_STD][i] = stat.getStandardDeviation();
			statisticsData[BatchRunResultsParamTableModel.ROW_IDX_MEDIAN][i] = stat.getMedian();
			statisticsData[BatchRunResultsParamTableModel.ROW_IDX_MIN][i] = stat.getMin();
			statisticsData[BatchRunResultsParamTableModel.ROW_IDX_MAX][i] = stat.getMax();
		}
	}
	
	public double[][] getStatisticsData()
	{
		if(statisticsData == null)
		{
			refreshStatisticsData();
		}
		return statisticsData;
	}
	
	public AsynchClientTask refreshBatchRunResults()
	{
		AsynchClientTask task = null;
		
		return task;
	}
}

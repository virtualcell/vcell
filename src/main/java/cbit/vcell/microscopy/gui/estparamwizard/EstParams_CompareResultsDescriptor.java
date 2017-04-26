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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import org.vcell.optimization.ConfidenceInterval;
import org.vcell.optimization.ProfileSummaryData;
import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptimizationUtils;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.modelopt.DataSource;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.solver.ode.ODESolverResultSet;

public class EstParams_CompareResultsDescriptor extends WizardPanelDescriptor
{
	public static final String IDENTIFIER = "EstimateParameters_CompareResultsAmongSelectedModels";
	private FRAPSingleWorkspace frapWorkspace = null;
	
    public EstParams_CompareResultsDescriptor () {
        super(IDENTIFIER, new EstParams_CompareResultsPanel());
    }
    
    public String getNextPanelDescriptorID() {
        return Wizard.FINISH.getPanelDescriptorIdentifier();
    }

    public void aboutToDisplayPanel() 
    {
    	FRAPStudy fStudy = frapWorkspace.getWorkingFrapStudy();
    	//create Mean square error for different models under different ROIs
//    	if(fStudy.getAnalysisMSESummaryData() == null)
//    	{
    	fStudy.createAnalysisMSESummaryData();
//    	}
    	//auto find best model for user if best model is not selected. 
    	double[][] mseSummaryData = fStudy.getAnalysisMSESummaryData();
//    	for(int i =0; i<10; i++)
//    	System.out.print(mseSummaryData[0][i]+"  ");
    	
    	//find best model with significance and has least error
    	int bestModel = FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT;
    	if(fStudy.getBestModelIndex() != null)//best model is saved and there is no model selection change
    	{
    		bestModel = fStudy.getBestModelIndex().intValue();
    	}
    	else//need to find the best model
    	{
	    	//check model significance if more than one model
	    	if(fStudy.getSelectedModels().size() > 1)
	    	{	
	    		if(getFrapWorkspace().getWorkingFrapStudy().getFrapOptData() != null || getFrapWorkspace().getWorkingFrapStudy().getFrapOptFunc() != null)
	    		{
			    	ProfileSummaryData[][] allProfileSumData = FRAPOptimizationUtils.getAllProfileSummaryData(fStudy);
					FRAPModel[] frapModels = frapWorkspace.getWorkingFrapStudy().getModels();
					int confidenceIdx = ((EstParams_CompareResultsPanel)this.getPanelComponent()).getSelectedConfidenceIndex();
					boolean[] modelSignificance = new boolean[FRAPModel.NUM_MODEL_TYPES];
					Arrays.fill(modelSignificance, true);
					if(frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] != null && 
					   frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters() != null &&
					   allProfileSumData != null && allProfileSumData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT]!= null)
					{
						for(int i=0; i<FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF; i++)
						{
							ConfidenceInterval[] intervals = allProfileSumData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT][i].getConfidenceIntervals();
							if(intervals[confidenceIdx].getUpperBound() == frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters()[i].getUpperBound() && 
							   intervals[confidenceIdx].getLowerBound() == frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters()[i].getLowerBound())
							{
								modelSignificance[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] = false;
								break;
							}
						}
					}
					if(frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] != null &&
					   frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters() != null &&
					   allProfileSumData != null && allProfileSumData[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS]!= null)
					{
						for(int i=0; i<FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF; i++)
						{
							ConfidenceInterval[] intervals = allProfileSumData[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS][i].getConfidenceIntervals();
							if(intervals[confidenceIdx].getUpperBound() == frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters()[i].getUpperBound() && 
							   intervals[confidenceIdx].getLowerBound() == frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters()[i].getLowerBound())
							{
								modelSignificance[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] = false;
								break;
							}
						}
					}
					if(frapModels[FRAPModel.IDX_MODEL_REACTION_OFF_RATE] != null &&
					   frapModels[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].getModelParameters() != null &&
					   allProfileSumData != null && allProfileSumData[FRAPModel.IDX_MODEL_REACTION_OFF_RATE]!= null )
					{
						for(int i=0; i<FRAPModel.NUM_MODEL_PARAMETERS_REACTION_OFF_RATE; i++)
						{
							if(i == FRAPModel.INDEX_BLEACH_MONITOR_RATE)
							{
								ConfidenceInterval[] intervals = allProfileSumData[FRAPModel.IDX_MODEL_REACTION_OFF_RATE][FRAPModel.INDEX_BLEACH_MONITOR_RATE].getConfidenceIntervals();
								if(intervals[confidenceIdx].getUpperBound() == frapModels[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].getModelParameters()[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getUpperBound() && 
								   intervals[confidenceIdx].getLowerBound() == frapModels[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].getModelParameters()[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getLowerBound())
								{
									modelSignificance[FRAPModel.IDX_MODEL_REACTION_OFF_RATE] = false;
									break;
								}
							}
							else if(i == FRAPModel.INDEX_OFF_RATE)
							{
								ConfidenceInterval[] intervals = allProfileSumData[FRAPModel.IDX_MODEL_REACTION_OFF_RATE][FRAPModel.INDEX_OFF_RATE].getConfidenceIntervals();
								if(intervals[confidenceIdx].getUpperBound() == frapModels[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].getModelParameters()[FRAPModel.INDEX_OFF_RATE].getUpperBound() && 
								   intervals[confidenceIdx].getLowerBound() == frapModels[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].getModelParameters()[FRAPModel.INDEX_OFF_RATE].getLowerBound())
								{
									modelSignificance[FRAPModel.IDX_MODEL_REACTION_OFF_RATE] = false;
									break;
								}
							}
						}
					}
					
					//check least error model with significance
			    	double minError = 1E8;
			    	if(mseSummaryData != null)
			    	{
			    		int secDimLen = FRAPData.VFRAP_ROI_ENUM.values().length - 2 + 1;//exclude cell and bkground ROIs, include sum of error
			    		if(modelSignificance[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] == modelSignificance[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] &&
			    		   modelSignificance[FRAPModel.IDX_MODEL_REACTION_OFF_RATE] == modelSignificance[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS])
			    		{//if all models' significance are the same, find the least error
			    			for(int i=0; i<FRAPModel.NUM_MODEL_TYPES; i++)
				    		{
				    			if((minError > mseSummaryData[i][secDimLen - 1]))
				    			{
				    				minError = mseSummaryData[i][secDimLen - 1];
				    				bestModel = i;
				    			}
				    		}
			    		}
			    		else
			    		{//if models' significance are different, find the least error with significance
				    		for(int i=0; i<FRAPModel.NUM_MODEL_TYPES; i++)
				    		{
				    			if(modelSignificance[i] && (minError > mseSummaryData[i][secDimLen - 1]))
				    			{
				    				minError = mseSummaryData[i][secDimLen - 1];
				    				bestModel = i;
				    			}
				    		}
			    		}
			    	}
	    		}
	    	}
	    	else //only one model is selected and the selected model should be the best model 
	    	{
	    		for(int i=0; i< fStudy.getModels().length; i++)
	    		{
	    			if(fStudy.getModels()[i] != null)
	    			{
	    				bestModel = i;
	    				break;
	    			}
	    		}
	    	}
    	}
    	((EstParams_CompareResultsPanel)this.getPanelComponent()).setBestModelRadioButton(bestModel);
    	//set data source to multiSourcePlotPane
    	ArrayList<DataSource> comparableDataSource = new ArrayList<DataSource>(); // length should be fStudy.getSelectedModels().size()+1, however, reaction binding may not have data
    	//add exp data
    	ReferenceData expReferenceData = FRAPOptimizationUtils.doubleArrayToSimpleRefData(fStudy.getDimensionReducedExpData(),
                                         fStudy.getFrapData().getImageDataset().getImageTimeStamps(), 
                                         fStudy.getStartingIndexForRecovery(), 
                                         fStudy.getSelectedROIsForErrorCalculation());
    	final DataSource expDataSource = new DataSource.DataSourceReferenceData("exp", expReferenceData); 
    	comparableDataSource.add(expDataSource);
    	//add opt/sim data
    	//using the same loop, disable the radio button if the model is not included
    	((EstParams_CompareResultsPanel)this.getPanelComponent()).disableAllRadioButtons();//adjust radio buttons
    	ArrayList<Integer> selectedModelIndexes = fStudy.getSelectedModels();
    	
    	for(int i = 0; i<selectedModelIndexes.size(); i++)
    	{
    		
    		DataSource newDataSource = null;
    		double[] timePoints = fStudy.getFrapData().getImageDataset().getImageTimeStamps();
			int startingIndex = fStudy.getStartingIndexForRecovery();
			double[] truncatedTimes = new double[timePoints.length-startingIndex];
			System.arraycopy(timePoints, startingIndex, truncatedTimes, 0, truncatedTimes.length);
    		if(selectedModelIndexes.get(i).equals(FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT))
    		{
    			((EstParams_CompareResultsPanel)this.getPanelComponent()).enableRadioButton(FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT);//adjust radio button
    			FRAPModel temModel = fStudy.getFrapModel(FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT);
    			ODESolverResultSet temSolverResultSet = FRAPOptimizationUtils.doubleArrayToSolverResultSet(temModel.getData(), 
    					             truncatedTimes,
    					             0,
    					             fStudy.getSelectedROIsForErrorCalculation());
    			newDataSource = new DataSource.DataSourceRowColumnResultSet("opt_DF1", temSolverResultSet);
    		}
    		else if(selectedModelIndexes.get(i).equals(FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS))
    		{
    			((EstParams_CompareResultsPanel)this.getPanelComponent()).enableRadioButton(FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS);//adjust radio button
    			FRAPModel temModel = fStudy.getFrapModel(FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS);
    			ODESolverResultSet temSolverResultSet = FRAPOptimizationUtils.doubleArrayToSolverResultSet(temModel.getData(), 
    								 truncatedTimes,
    					             0,
    					             fStudy.getSelectedROIsForErrorCalculation());
    			newDataSource = new DataSource.DataSourceRowColumnResultSet("opt_DF2", temSolverResultSet);
    		}
    		else if(selectedModelIndexes.get(i).equals(FRAPModel.IDX_MODEL_REACTION_OFF_RATE))
    		{
    			((EstParams_CompareResultsPanel)this.getPanelComponent()).enableRadioButton(FRAPModel.IDX_MODEL_REACTION_OFF_RATE);//adjust radio button
    			FRAPModel temModel = fStudy.getFrapModel(FRAPModel.IDX_MODEL_REACTION_OFF_RATE);
    			if(temModel.getData() != null)
    			{
	    			ODESolverResultSet temSolverResultSet = FRAPOptimizationUtils.doubleArrayToSolverResultSet(temModel.getData(), 
	    								 truncatedTimes,
	    					             0,
	    					             FRAPStudy.createSelectedROIsForReactionOffRateModel());//for reaction off model, display curve under bleached region only
	    			newDataSource = new DataSource.DataSourceRowColumnResultSet("sim_Koff", temSolverResultSet);
    			}
    		}
    		if(newDataSource != null)
    		{
    			comparableDataSource.add(newDataSource);
    		}
    	}
    	//set data to multiSourcePlotPane
    	((EstParams_CompareResultsPanel)this.getPanelComponent()).setPlotData(comparableDataSource.toArray(new DataSource[comparableDataSource.size()]));
	}
    
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		AsynchClientTask saveBestModelTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				int bestModelIndex = getBestModelIndex();
				getFrapWorkspace().getWorkingFrapStudy().setBestModelIndex(new Integer(bestModelIndex));
			}
		};
		
		taskArrayList.add(saveBestModelTask);
		return taskArrayList;
    }
    
    public int getBestModelIndex()
    {
    	return ((EstParams_CompareResultsPanel)this.getPanelComponent()).getRadioButtonPanel().getBestModelIndex();
    }
    
    public FRAPSingleWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
    
	public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace) {
		this.frapWorkspace = frapWorkspace;
		((EstParams_CompareResultsPanel)getPanelComponent()).setFrapWorkspace(frapWorkspace);
	}
    
}

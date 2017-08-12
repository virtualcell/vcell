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
import java.util.Hashtable;

import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.opt.Parameter;

public class EstParams_TwoDiffComponentDescriptor extends WizardPanelDescriptor
{
	public static final String IDENTIFIER = "EstimateParameters_DiffusionWithTwoDiffusingComponents";
	private FRAPSingleWorkspace frapWorkspace = null;
	
    public EstParams_TwoDiffComponentDescriptor () {
        super(IDENTIFIER, new EstParams_TwoDiffComponentPanel());
    }

    public void aboutToDisplayPanel() 
    {
    	FRAPStudy fStudy = frapWorkspace.getWorkingFrapStudy();
    	Parameter[] params = fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters();
    	try{
	    	if(params == null)
	    	{
	    		params = FRAPModel.getInitialParameters(fStudy.getFrapData(), FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS],fStudy.getStartingIndexForRecovery());
	    	}
			((EstParams_TwoDiffComponentPanel)getPanelComponent()).setData(fStudy.getFrapOptData(), fStudy.getFrapData(), params, fStudy.getFrapData().getImageDataset().getImageTimeStamps(), fStudy.getStartingIndexForRecovery(), fStudy.getSelectedROIsForErrorCalculation());
    	}catch(Exception ex)
    	{
    		ex.printStackTrace(System.out);
    		DialogUtils.showErrorDialog((getPanelComponent()), "Error getting parameters for diffusion with one diffusing component model.");
    	}
	}
    
    //save model parameters when go next
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		AsynchClientTask saveParametersTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				saveModelParameters();
			}
		};
		
		taskArrayList.add(saveParametersTask);
	
		return taskArrayList;
    }
    
    //save model parameters also when go back
    public ArrayList<AsynchClientTask> preBackProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		AsynchClientTask saveParametersTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				saveModelParameters();
			}
		};
		
		taskArrayList.add(saveParametersTask);
	
		return taskArrayList;
    }
    
    private void saveModelParameters()
    {
    	Parameter[] params = ((EstParams_TwoDiffComponentPanel)getPanelComponent()).getCurrentParameters();
		FRAPModel  frapModel = getFrapWorkspace().getWorkingFrapStudy().getFrapModel(FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS);
		frapModel.setModelParameters(params);
		frapModel.setData(((EstParams_TwoDiffComponentPanel)getPanelComponent()).getCurrentEstimationResults());
    }
    
    public FRAPSingleWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
    
	public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace) {
		this.frapWorkspace = frapWorkspace;
		((EstParams_TwoDiffComponentPanel)getPanelComponent()).setFrapWorkspace(frapWorkspace);
	}
    
	public void clearSelectedPlotIndices()
	{
		((EstParams_TwoDiffComponentPanel)getPanelComponent()).clearSelectedPlotIndices();
	}
}

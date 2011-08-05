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

public class EstParams_ReactionOffRateDescriptor extends WizardPanelDescriptor
{
	public static final String IDENTIFIER = "EstimateParameters_ReactionOffRate";
	private FRAPSingleWorkspace frapWorkspace = null;
	
    public EstParams_ReactionOffRateDescriptor () {
        super(IDENTIFIER, new EstParams_ReactionOffRatePanel());
    }

    public void aboutToDisplayPanel() 
    {
    	FRAPStudy fStudy = frapWorkspace.getWorkingFrapStudy();
    	Parameter[] params = fStudy.getModels()[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].getModelParameters();
    	try{
	    	if(params == null)
	    	{
	    		params = fStudy.getFrapOptFunc().getBestParamters(fStudy.getFrapData(), null, true);
	    	}
			((EstParams_ReactionOffRatePanel)getPanelComponent()).setData(params, fStudy.getFrapData(), /*fStudy.getFrapOptFunc().getOffRateResults(),*/ fStudy.getStartingIndexForRecovery());
    	}catch(Exception ex)
    	{
    		ex.printStackTrace(System.out);
    		DialogUtils.showErrorDialog((getPanelComponent()), "Error getting parameters for reaction dominant off rate model.");
    	}
	}
    
    //save model parameters
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		AsynchClientTask saveParametersTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				Parameter[] params = ((EstParams_ReactionOffRatePanel)getPanelComponent()).getCurrentParameters();
				FRAPModel  frapModel = getFrapWorkspace().getWorkingFrapStudy().getFrapModel(FRAPModel.IDX_MODEL_REACTION_OFF_RATE);
				frapModel.setModelParameters(params);
				frapModel.setData(((EstParams_ReactionOffRatePanel)getPanelComponent()).getCurrentEstimationResults());
			}
		};
		
		taskArrayList.add(saveParametersTask);
	
		return taskArrayList;
    } 
    
    public FRAPSingleWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
    
	public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace) {
		this.frapWorkspace = frapWorkspace;
		((EstParams_ReactionOffRatePanel)getPanelComponent()).setFrapWorkspace(frapWorkspace);
	}
	
	public void clearSelectedPlotIndices()
	{
		((EstParams_ReactionOffRatePanel)getPanelComponent()).clearSelectedPlotIndices();
	}
}

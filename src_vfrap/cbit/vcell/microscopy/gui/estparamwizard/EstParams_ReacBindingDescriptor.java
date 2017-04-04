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
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.opt.Parameter;

public class EstParams_ReacBindingDescriptor extends WizardPanelDescriptor
{
	public static final String IDENTIFIER = "EstimateParameters_ReactionPlusBinding";
	private FRAPSingleWorkspace frapWorkspace = null;
	private LocalWorkspace localWorkspace = null;
    
    public EstParams_ReacBindingDescriptor () {
        super(IDENTIFIER, new EstParams_ReacBindingPanel());
    }

    public void aboutToDisplayPanel() 
    {
    	FRAPStudy fStudy = getFrapWorkspace().getWorkingFrapStudy();
		FRAPModel[] frapModels = fStudy.getModels();
		Parameter[] params = null;
		//get parameters to display in reaction binding panel
		if(frapModels[FRAPModel.IDX_MODEL_DIFF_BINDING] != null && 
		   frapModels[FRAPModel.IDX_MODEL_DIFF_BINDING].getModelParameters()!= null)
		{
			params = frapModels[FRAPModel.IDX_MODEL_DIFF_BINDING].getModelParameters();
			((EstParams_ReacBindingPanel)getPanelComponent()).setCurrentSimResults(frapModels[FRAPModel.IDX_MODEL_DIFF_BINDING].getData());
			((EstParams_ReacBindingPanel)getPanelComponent()).setCurrentRawSimTimePoints(frapModels[FRAPModel.IDX_MODEL_DIFF_BINDING].getTimepoints());
		}
		else
		{
			((EstParams_ReacBindingPanel)getPanelComponent()).setCurrentSimResults(null);
			((EstParams_ReacBindingPanel)getPanelComponent()).setCurrentRawSimTimePoints(null);
			
			if(frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] != null && 
					frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters()!=null)
			{
				params = FRAPModel.createReacBindingParametersFromDiffusionParameters(frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters());
			}
			else // no parameters are available
			{
				try{
			    	params = FRAPModel.getInitialParameters(fStudy.getFrapData(), FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_BINDING],fStudy.getStartingIndexForRecovery());
		    	}catch(Exception ex)
		    	{
		    		ex.printStackTrace(System.out);
		    		DialogUtils.showErrorDialog((getPanelComponent()), "Error getting parameters for diffusion plus binding model." + ex.getMessage());
		    	}
			}
		}
    	((EstParams_ReacBindingPanel)getPanelComponent()).setReacBindingParams(params);
    	try {
			((EstParams_ReacBindingPanel)getPanelComponent()).setData(fStudy.getFrapData(), params, fStudy.getFrapData().getImageDataset().getImageTimeStamps(), fStudy.getStartingIndexForRecovery(), fStudy.getSelectedROIsForErrorCalculation());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
    		DialogUtils.showErrorDialog((getPanelComponent()), "Error plotting data for diffusion plus binding model." + ex.getMessage());
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
    	Parameter[] params = ((EstParams_ReacBindingPanel)getPanelComponent()).getCurrentParameters();
		FRAPModel  frapModel = getFrapWorkspace().getWorkingFrapStudy().getFrapModel(FRAPModel.IDX_MODEL_DIFF_BINDING);
		frapModel.setModelParameters(params);
		frapModel.setData(((EstParams_ReacBindingPanel)getPanelComponent()).getCurrentSimResults());
		frapModel.setTimepoints(((EstParams_ReacBindingPanel)getPanelComponent()).getCurrentRawSimTimePoints());
    }
    
    public FRAPSingleWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
    
	public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace) {
		this.frapWorkspace = frapWorkspace;
		((EstParams_ReacBindingPanel)getPanelComponent()).setFrapWorkspace(frapWorkspace);
	}
	
	public LocalWorkspace getLocalWorkspace() {
		return localWorkspace;
	}
    
	public void setLocalWorkspace(LocalWorkspace localWorkspace) {
		this.localWorkspace = localWorkspace;
		((EstParams_ReacBindingPanel)getPanelComponent()).setLocalWorkspace(localWorkspace);
	}
}

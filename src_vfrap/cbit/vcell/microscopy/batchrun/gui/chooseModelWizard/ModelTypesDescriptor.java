/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.batchrun.gui.chooseModelWizard;

import java.util.ArrayList;
import java.util.Hashtable;

import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.client.UserMessage;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;

public class ModelTypesDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "BatchRun_ModelTypes";
    private FRAPBatchRunWorkspace batchRunWorkspace = null;
    private ModelTypesPanel modelTypesPanel = new ModelTypesPanel();
    
    public ModelTypesDescriptor () {
    	super();
    	setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(modelTypesPanel);
    }
    
    public String getNextPanelDescriptorID() {
        return Wizard.FINISH.getPanelDescriptorIdentifier();
    }
    
    public String getBackPanelDescriptorID() {
        return null;
    }  
    
    public void setBatchRunWorkspace(FRAPBatchRunWorkspace arg_batchRunWorkspace)
    {
    	batchRunWorkspace = arg_batchRunWorkspace;
    }
    
    public void aboutToDisplayPanel() 
    {
    	FRAPStudy fStudy = batchRunWorkspace.getWorkingFrapStudy();
    	//if there are models selected and saved, load the model types. otherwise, apply default(diffusion with one component is selected).
    	if(fStudy.getModels() != null && fStudy.getModels().length > 0 && fStudy.getSelectedModels().size() > 0)
    	{
    		modelTypesPanel.clearAllSelected();
    		FRAPModel[] models = fStudy.getModels();
			if(models[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] != null)
			{
				modelTypesPanel.setDiffOneSelected(true);
			}
			if(models[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] != null)
			{
				modelTypesPanel.setDiffTwoSelected(true);
			}
			if(models[FRAPModel.IDX_MODEL_REACTION_OFF_RATE] != null)
			{
				modelTypesPanel.setReactionOffRateSelected(true);
			}
    	}
    	else //new frap document
    	{
    		modelTypesPanel.clearAllSelected();
    		modelTypesPanel.setDiffOneSelected(true);
    	}
    }
    
    public ArrayList<AsynchClientTask> preNextProcess()
    {   
    	ArrayList<AsynchClientTask> tasks = new ArrayList<AsynchClientTask>();
    	
    	AsynchClientTask aTask1 = new AsynchClientTask("Saving selected model types...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				if(batchRunWorkspace != null)
		    	{
					boolean[] models = modelTypesPanel.getModelTypes();
					boolean isOneSelected = false;
					for(int i = 0; i<models.length; i++)
					{
						if(models[i])
						{
							isOneSelected = true;
							break;
						}
					}
					if(isOneSelected)
					{
						if(models[FRAPModel.IDX_MODEL_REACTION_OFF_RATE] && !batchRunWorkspace.isBatchRunResultsAvailable())
						{
							String choice = DialogUtils.showWarningDialog(modelTypesPanel, "Reaction dominant off rate model can be estimated under bleached region only. \n" +
									"There is at least one document that didn't select bleached region as the only ROI.\n" +
									"Two solutions are listed below:\n\n" +
									"Select 'Continue' to automatically change selected ROI to bleached region for each document or\n" +
									"Select 'Cancel' to change selected model.", new String[]{UserMessage.OPTION_CONTINUE, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_CONTINUE);
							if(choice.equals(UserMessage.OPTION_CONTINUE))
							{
								batchRunWorkspace.setSelectedROIForReactionOffRate();
							}
							else
							{
								throw UserCancelException.CANCEL_GENERIC;
							}
						}
							
			    		//update selected models in batchRunWorkspace and all frapStudies in the BatchRun
						batchRunWorkspace.refreshModels(models);
					}
					else
					{
						throw new Exception("At least one model type has to be selected.");
					}
		    	}
				else
				{
					throw new Exception("FRAPStudy is null. Please load data.");
				}
			}
		};
		
        tasks.add(aTask1);

        return tasks;
    }
    
}//end of class


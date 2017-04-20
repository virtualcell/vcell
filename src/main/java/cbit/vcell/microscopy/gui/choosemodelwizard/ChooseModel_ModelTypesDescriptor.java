/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui.choosemodelwizard;

import java.util.ArrayList;
import java.util.Hashtable;

import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.client.UserMessage;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPStudy;

public class ChooseModel_ModelTypesDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "ChooseModel_ModelTypes";
    private FRAPSingleWorkspace frapWorkspace = null;
    private ChooseModel_ModelTypesPanel modelTypesPanel = new ChooseModel_ModelTypesPanel();
    
    public ChooseModel_ModelTypesDescriptor () {
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
    
    public void setFrapWorkspace(FRAPSingleWorkspace arg_FrapWorkspace)
    {
    	frapWorkspace = arg_FrapWorkspace;
    }
    
    public void aboutToDisplayPanel() 
    {
    	FRAPStudy fStudy = frapWorkspace.getWorkingFrapStudy();
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
				FRAPStudy fStudy = frapWorkspace.getWorkingFrapStudy();
				if(fStudy == null)
		    	{
					throw new Exception("FRAPStudy is null. Please load data.");
		    	}
				boolean[] models = modelTypesPanel.getModelTypes();
				//perform checkings...is one selected, is koffRatemodel selected(in this case force to use bleached ROI only)					
				if(modelTypesPanel.getNumUserSelectedModelTypes() < 1)
				{
					throw new Exception("At least one model type has to be selected.");
				}
				//if reaction dominant off rate model is selected, we have to make sure that the bleached ROI should be selected.
				if(models[FRAPModel.IDX_MODEL_REACTION_OFF_RATE] && !fStudy.getSelectedROIsForErrorCalculation()[FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.ordinal()])
				{
					String choice = DialogUtils.showWarningDialog(modelTypesPanel, "To evaluate Reaction Dominant Off Rate model, bleached ROI must be selected. \n\nSelecte 'Continue' to have bleached ROI automatically added and proceed. Or,\nSelecte 'Cancel' to stop proceeding.", new String[]{UserMessage.OPTION_CONTINUE, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_CONTINUE);
					if(choice.equals(UserMessage.OPTION_CONTINUE))
					{
						fStudy.getSelectedROIsForErrorCalculation()[FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.ordinal()] = true;
					}
					else
					{
						throw UserCancelException.CANCEL_GENERIC;
					}
				}
				
				//update selected models in FrapStudy
			    fStudy.refreshModels(models);
			}
		};
		
        tasks.add(aTask1);

        return tasks;
    }
    
}//end of class


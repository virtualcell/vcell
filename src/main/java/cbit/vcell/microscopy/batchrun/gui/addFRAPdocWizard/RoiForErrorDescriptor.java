/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard;

import java.util.ArrayList;
import java.util.Hashtable;

import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;

public class RoiForErrorDescriptor extends WizardPanelDescriptor{
    
    public static final String IDENTIFIER = "BatchRun_RoiForError";
    public FRAPBatchRunWorkspace bathRunWorkspace = null;
    
    public RoiForErrorDescriptor () {
        super(IDENTIFIER, new RoiForErrorPanel());
    }
    
    public String getNextPanelDescriptorID() {
        return FileSaveDescriptor.IDENTIFIER;
    }
    
    public String getBackPanelDescriptorID() {
        return ROISummaryDescriptor.IDENTIFIER;
    }
    
    public void aboutToDisplayPanel() 
    {
    	((RoiForErrorPanel)getPanelComponent()).refreshCheckboxes();
    	((RoiForErrorPanel)getPanelComponent()).refreshROIImage();
	}
    
    public void setBatchRunWorkspace(FRAPBatchRunWorkspace arg_BarchRunWorkspace)
    {
    	bathRunWorkspace = arg_BarchRunWorkspace;
    	((RoiForErrorPanel)getPanelComponent()).setFrapBatchRunWorkspace(arg_BarchRunWorkspace);
    }
    
    public ArrayList<AsynchClientTask> preNextProcess()
    {   
    	ArrayList<AsynchClientTask> tasks = new ArrayList<AsynchClientTask>();
    	
    	AsynchClientTask aTask1 = new AsynchClientTask("Saving selected ROIs...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				boolean[] selectedROIs = ((RoiForErrorPanel)getPanelComponent()).getSelectedROIs();
				boolean isOneSelected = false;
				for(int i=0; i<selectedROIs.length; i++)
				{
					if(selectedROIs[i])
					{
						isOneSelected = true;
						break;
					}
				}
				if(isOneSelected)
				{
					bathRunWorkspace.getWorkingFrapStudy().setSelectedROIsForErrorCalculation(selectedROIs);
				}
				else
				{
					throw new Exception("At least one ROI has to be selected.");
				}
			}
		};
		tasks.add(aTask1);

        return tasks;
    }
}

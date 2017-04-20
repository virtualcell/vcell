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
import cbit.vcell.microscopy.DataVerifyInfo;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_SummaryPanel;

public class FileSummaryDescriptor extends WizardPanelDescriptor{
    
    public static final String IDENTIFIER = "BATCHRUN_FileSummary";
    private FRAPBatchRunWorkspace batchRunWorkspace = null;
    
    public FileSummaryDescriptor() {
        super(IDENTIFIER, new LoadFRAPData_SummaryPanel());
        setProgressPopupShown(false); 
        setTaskProgressKnown(false);
    }
    //this method is override to make sure the next step is going to finish.
    public String getNextPanelDescriptorID() {
        return CropDescriptor.IDENTIFIER;
    }
    
	public void aboutToDisplayPanel() {
		((LoadFRAPData_SummaryPanel)getPanelComponent()).setLoadInfo(getBatchRunWorkspace().getWorkingFrapStudy());
	}
	
	//save the data before the panel disappears
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		AsynchClientTask verifyLoadedDataTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				String msg = ((LoadFRAPData_SummaryPanel)getPanelComponent()).checkInputValidity();
				if(msg.equals(""))
				{
					DataVerifyInfo dataVerifyInfo= ((LoadFRAPData_SummaryPanel)getPanelComponent()).saveDataInfo();
					if(dataVerifyInfo != null)
					{
						getBatchRunWorkspace().getWorkingSingleWorkspace().updateImages(dataVerifyInfo);
					}
				}
				else throw new Exception(msg);
			}
		};
		
		taskArrayList.add(verifyLoadedDataTask);
		return taskArrayList;
    }
    
    public FRAPBatchRunWorkspace getBatchRunWorkspace() {
		return batchRunWorkspace;
	}
    
	public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace) {
		this.batchRunWorkspace = batchRunWorkspace;
	}
}

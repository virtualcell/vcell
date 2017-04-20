/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui.loaddatawizard;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;

public class LoadFRAPData_MultiFileDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "LoadFRAPData_MultipleFiles";
    private LoadFRAPData_MultiFilePanel multiFilePanel = new LoadFRAPData_MultiFilePanel();
    private FRAPSingleWorkspace frapWorkspace = null;
    private boolean isFileLoaded = false;
    
    public LoadFRAPData_MultiFileDescriptor() {
    	super();
        setPanelComponent(multiFilePanel);
        setPanelDescriptorIdentifier(IDENTIFIER);
        setProgressPopupShown(true); 
        setTaskProgressKnown(true);
    }
    
    public String getNextPanelDescriptorID() {
        return LoadFRAPData_SummaryDescriptor.IDENTIFIER;
    }
    
    public String getBackPanelDescriptorID() {
        return LoadFRAPData_FileTypeDescriptor.IDENTIFIER;
    }
    
    //load the data before the panel disappears
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		final File[] files = multiFilePanel.getSelectedFiles();
		
    	if(files.length > 0)
    	{
    		final String filePath = files[0].getParent();
    		final String LOADING_MESSAGE = "Loading files from directory "+ filePath +"...";
    		
			AsynchClientTask updateUIBeforeLoadTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
    				try
    				{
    					VirtualFrapMainFrame.updateStatus(LOADING_MESSAGE);
    				}catch (Exception e){
    					e.printStackTrace(System.out);
    				}
    			}
    		};
			
			
    		AsynchClientTask loadTask = new AsynchClientTask(LOADING_MESSAGE, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
					FRAPStudy newFRAPStudy = null;

					newFRAPStudy = FRAPWorkspace.loadFRAPDataFromMultipleFiles(files, this.getClientTaskStatusSupport(), multiFilePanel.isTimeSeries(), multiFilePanel.getTimeInterval());
					isFileLoaded = true;
					
					//for all loaded files
					hashTable.put(FRAPStudyPanel.NEW_FRAPSTUDY_KEY, newFRAPStudy);
    					
    			}
    		};
    		
    		AsynchClientTask afterLoadingSwingTask = new AsynchClientTask(LOADING_MESSAGE, AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
    				FRAPStudy newFRAPStudy = (FRAPStudy)hashTable.get(FRAPStudyPanel.NEW_FRAPSTUDY_KEY);
    				//setFrapStudy fires property change, so we have to put it in Swing thread.
    				getFrapWorkspace().setFrapStudy(newFRAPStudy, true);
    				
    				VirtualFrapLoader.mf.setMainFrameTitle("");
    				VirtualFrapMainFrame.updateProgress(0);
    				if(isFileLoaded)
    				{
        				VirtualFrapMainFrame.updateStatus("Loaded files from " + filePath);
    				}
    				else
    				{
						VirtualFrapMainFrame.updateStatus("Failed loading files from " + filePath+".");
    				}
    			}
    		};
    		taskArrayList.add(updateUIBeforeLoadTask);
    		taskArrayList.add(loadTask);
    		taskArrayList.add(afterLoadingSwingTask);
    	}
    	else
    	{
    		DialogUtils.showErrorDialog(multiFilePanel, "No file is selected. Please input one or more file names to continue.");
    		throw new RuntimeException("No file is selected. Please input one or more file names to continue.");
    	}
		return taskArrayList;
    } 
    
    //Actions after the panel disappears
    public ArrayList<AsynchClientTask> postNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		return taskArrayList;
    }
    
    public FRAPSingleWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
    
	public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace) {
		this.frapWorkspace = frapWorkspace;
	}
}



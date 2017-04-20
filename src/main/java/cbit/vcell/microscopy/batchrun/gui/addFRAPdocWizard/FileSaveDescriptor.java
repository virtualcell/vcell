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

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.client.UserMessage;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.MicroscopyXmlReader;
import cbit.vcell.microscopy.MicroscopyXmlproducer;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;

public class FileSaveDescriptor extends WizardPanelDescriptor {

    public static final String IDENTIFIER = "BATCHRUN_SAVE_FILE";
    //    private FRAPStudy localFrapStudy = null;
    private FileSavePanel saveFilePanel = new FileSavePanel();
    private FRAPBatchRunWorkspace batchRunWorkspace = null;
    private LocalWorkspace localWorkspace = null;
   
	public FileSaveDescriptor() {
    	super();
        setPanelComponent(saveFilePanel);
        setPanelDescriptorIdentifier(IDENTIFIER);
        setProgressPopupShown(true); 
        setTaskProgressKnown(true);
    }
    //This method is override to make fure that it goes to SummaryPanel
    public String getNextPanelDescriptorID() {
    	return Wizard.FINISH.getPanelDescriptorIdentifier();
    }
    //This method is override to make sure that it backs to FileTypePanel
    public String getBackPanelDescriptorID() {
        return RoiForErrorDescriptor.IDENTIFIER;
    }
    
    public void aboutToDisplayPanel() {
    	FRAPStudy workingFrapStudy = getBatchRunWorkspace().getWorkingSingleWorkspace().getWorkingFrapStudy(); 
    	if(workingFrapStudy.getXmlFilename() != null && workingFrapStudy.getXmlFilename().length() > 0)
    	{
    		saveFilePanel.setFileName(workingFrapStudy.getXmlFilename());
    	}
	}
    
    //load the data before the panel disappears
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
    	if(saveFilePanel.getFileName().length() > 0)
    	{
    		AsynchClientTask beforeSaveTask = new AsynchClientTask("Saving file ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
    				if(getBatchRunWorkspace().getWorkingFrapStudy() == null || getBatchRunWorkspace().getWorkingFrapStudy().getFrapData() == null)
    				{
    					throw new Exception("No FRAP Data exists to save");
    				}else{
    					File outputFile = null;
    					String saveFileName = saveFilePanel.getFileName();
    		    		if(saveFileName == null)
    		    		{
    		    			throw new Exception("Please give a proper file name to save!");
    		    		}
    		    		else
    		    		{
		    				File tempOutputFile = new File(saveFileName);
				    		if(!VirtualFrapLoader.filter_vfrap.accept(tempOutputFile)){
		    					if(tempOutputFile.getName().indexOf(".") == -1){
		    						tempOutputFile = new File(tempOutputFile.getParentFile(),tempOutputFile.getName()+"."+VirtualFrapLoader.VFRAP_EXTENSION);
		    					}else{
		    						throw new Exception("Virtual FRAP document names must have an extension of ."+VirtualFrapLoader.VFRAP_EXTENSION);//return?
		    					}
		    				}
				    		if(tempOutputFile.exists())
				    		{
				    			String overwriteChoice = DialogUtils.showWarningDialog(saveFilePanel, "OverWrite file\n"+ tempOutputFile.getAbsolutePath(),
				    						new String[] {UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_CANCEL);
				    			if(overwriteChoice.equals(UserMessage.OPTION_CANCEL)){
				    				throw UserCancelException.CANCEL_GENERIC;
				    			}
				    			else
				    			{
					    			//Remove overwritten vfrap document external and simulation files
					    			try{
					    				MicroscopyXmlReader.ExternalDataAndSimulationInfo externalDataAndSimulationInfo =
					    					MicroscopyXmlReader.getExternalDataAndSimulationInfo(tempOutputFile);
					    				FRAPStudy.removeExternalDataAndSimulationFiles(
					    						externalDataAndSimulationInfo.simulationKey,
					    						(externalDataAndSimulationInfo.frapDataExtDataInfo != null
					    							?externalDataAndSimulationInfo.frapDataExtDataInfo.getExternalDataIdentifier():null),
					    						(externalDataAndSimulationInfo.roiExtDataInfo != null
					    							?externalDataAndSimulationInfo.roiExtDataInfo.getExternalDataIdentifier():null),
					    						getLocalWorkspace());
					    			}catch(Exception e){
					    				System.out.println(
					    					"Error deleting externalData and simulation files for overwritten vfrap document "+
					    					tempOutputFile.getAbsolutePath()+"  "+e.getMessage());
					    				e.printStackTrace(System.out);
					    			}
				    			}
				    		}
				    		outputFile = tempOutputFile;
    		    		}
    		    		
    		    		
    		    		if(outputFile != null)
    		    		{
    		    			hashTable.put(FRAPStudyPanel.SAVE_FILE_NAME_KEY, outputFile);
    		    		}
    				}
    			}
    		};
    		
    		AsynchClientTask saveTask = new AsynchClientTask("Saving file ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
    				File outFile = (File)hashTable.get(FRAPStudyPanel.SAVE_FILE_NAME_KEY);
    				saveProcedure(outFile, false, this.getClientTaskStatusSupport());
    			}
    		};
    		
    		AsynchClientTask afterSaveTask = new AsynchClientTask("Saving file ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
    				hashTable.get(FRAPStudyPanel.SAVE_FILE_NAME_KEY);
    			}
    		};

    		taskArrayList.add(beforeSaveTask);
    		taskArrayList.add(saveTask);
    		taskArrayList.add(afterSaveTask);
    	}
    	else
    	{
    		DialogUtils.showErrorDialog(saveFilePanel, "Save File name is empty. Please input a file name to continue.");
    		throw new RuntimeException("Save File name is empty. Please input a file name to continue.");
    	}
		return taskArrayList;
    } 
    
    private void saveProcedure(File xmlFrapFile, boolean bSaveAs, ClientTaskStatusSupport progressListener) throws Exception
	{
		
		//save
		MicroscopyXmlproducer.writeXMLFile(getBatchRunWorkspace().getWorkingFrapStudy(), xmlFrapFile, true,progressListener,VirtualFrapMainFrame.SAVE_COMPRESSED);
		getBatchRunWorkspace().getWorkingFrapStudy().setXmlFilename(xmlFrapFile.getAbsolutePath());
		getBatchRunWorkspace().getWorkingFrapStudy().setSaveNeeded(false);
	}
    
    public FRAPBatchRunWorkspace getBatchRunWorkspace() {
		return batchRunWorkspace;
	}
    
	public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace) {
		this.batchRunWorkspace = batchRunWorkspace;
	}
	
	 
	public LocalWorkspace getLocalWorkspace() {
		return localWorkspace;
	}
	public void setLocalWorkspace(LocalWorkspace localWorkspace) {
		this.localWorkspace = localWorkspace;
	}
}

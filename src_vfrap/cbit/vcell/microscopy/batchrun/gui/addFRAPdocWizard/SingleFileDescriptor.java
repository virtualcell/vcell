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

import javax.swing.ListSelectionModel;

import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.WizardPanelDescriptor;

import cbit.util.xml.XmlUtil;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.math.VariableType;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.MicroscopyXmlReader;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.batchrun.gui.VirtualFrapBatchRunFrame;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.server.FrapDataUtils;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.SimDataConstants;

public class SingleFileDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "BATCHRUN_SingleFile";
    private static final String CLEAR_RESULT_KEY = "CLEAR_RESULT";
    //    private FRAPStudy localFrapStudy = null;
    private SingleFilePanel singleFilePanel = new SingleFilePanel();
    private boolean isFileLoaded = false;
    private FRAPBatchRunWorkspace batchRunWorkspace = null;
    private LocalWorkspace localWorkspace = null;
    
	public SingleFileDescriptor() {
    	super();
        setPanelComponent(singleFilePanel);
        setPanelDescriptorIdentifier(IDENTIFIER);
        setProgressPopupShown(true); 
        setTaskProgressKnown(true);
    }
    //This method is override to make fure that it goes to SummaryPanel
    public String getNextPanelDescriptorID() {
        return FileSummaryDescriptor.IDENTIFIER;
    }
    //This method is override to make sure that it backs to FileTypePanel
    public String getBackPanelDescriptorID() {
        return FileTypeDescriptor.IDENTIFIER;
    }
    
    //load the data before the panel disappears
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
    	if(singleFilePanel.getFileName().length() > 0)
    	{
    		final String fileStr = singleFilePanel.getFileName();
    		final String LOADING_MESSAGE = "Loading "+fileStr+"...";
    		
			AsynchClientTask updateUIBeforeLoadTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
    				VirtualFrapBatchRunFrame.updateStatus(LOADING_MESSAGE);
    			}
    		};
			
			
    		AsynchClientTask loadTask = new AsynchClientTask(LOADING_MESSAGE, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
    					FRAPStudy newFRAPStudy = null;
    					
    					File inFile = new File(fileStr);
    					if(inFile.getName().endsWith(SimDataConstants.LOGFILE_EXTENSION)) //.log (vcell log file) 
    					{
							DataIdentifier[] dataIdentifiers = FrapDataUtils.getDataIdentiferListFromVCellSimulationData(inFile, 0);
							String[][] rowData = new String[dataIdentifiers.length][1];
							for (int i = 0; i < dataIdentifiers.length; i++) {
								if(dataIdentifiers[i].getVariableType().equals(VariableType.VOLUME)){
									rowData[i][0] = dataIdentifiers[i].getName();
								}
							}
							int[] selectedIndexArr = DialogUtils.showComponentOKCancelTableList(
									SingleFileDescriptor.this.getPanelComponent(),
									"Select Volume Variable",
									new String[] {"Volume Variable Name"},
									rowData, ListSelectionModel.SINGLE_SELECTION);
							if(selectedIndexArr != null && selectedIndexArr.length > 0)
							{
//								newFRAPStudy = getFrapWorkspace().loadFRAPDataFromVcellLogFile(inFile, dataIdentifiers[selectedIndexArr[0]].getName(), this.getClientTaskStatusSupport());
								isFileLoaded = true;
							}else{
								throw UserCancelException.CANCEL_GENERIC;
							}
    					}else if(inFile.getName().endsWith(VirtualFrapLoader.VFRAP_EXTENSION)) //.vfrap
    					{
   							String xmlString = XmlUtil.getXMLString(inFile.getAbsolutePath());
   							MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
   							newFRAPStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null).getRootElement(),this.getClientTaskStatusSupport());
   							newFRAPStudy.setXmlFilename(inFile.getAbsolutePath());
   							if(!FRAPWorkspace.areExternalDataOK(localWorkspace,newFRAPStudy.getFrapDataExternalDataInfo(),newFRAPStudy.getRoiExternalDataInfo()))
   							{
   								newFRAPStudy.setFrapDataExternalDataInfo(null);
   								newFRAPStudy.setRoiExternalDataInfo(null);
   							}
    					}else //.lsm or other image formatss
    					{
    							newFRAPStudy = FRAPWorkspace.loadFRAPDataFromImageFile(inFile, this.getClientTaskStatusSupport());
    							isFileLoaded = true;
    					}
    					//check if there is results, if so, if the loaded frapStudy contains the same model type(it may have more) and paramters then proceed
    					//otherwise, popup a dialog saying the result will be invalid.
    					if(!batchRunWorkspace.isBatchRunResultsAvailable())
    					{
    						hashTable.put(FRAPStudyPanel.NEW_FRAPSTUDY_KEY, newFRAPStudy);
    					}
    					else if((batchRunWorkspace.isBatchRunResultsAvailable() && inFile.getName().endsWith(VirtualFrapLoader.VFRAP_EXTENSION)) &&
    							((newFRAPStudy.getFrapModel(batchRunWorkspace.getSelectedModel()) != null) &&
    		    	    		 (newFRAPStudy.getFrapModel(batchRunWorkspace.getSelectedModel()).getModelParameters() != null)))
    					{
    						//put newfrapstudy into hashtable to be used for next task
    	    				hashTable.put(FRAPStudyPanel.NEW_FRAPSTUDY_KEY, newFRAPStudy);
    					}
    					else
    					{
    						String continueStr = "Remove results and continue";
    						String cancelStr = "Cancel loading data";
    						String msg = "Loading a new data file,\n or a vfrap file with no model data,\n or a vfrap file with different selected model type, \n will INVALID the existing resuls.";
    						String choice = DialogUtils.showWarningDialog(singleFilePanel, msg, new String[]{continueStr, cancelStr}, continueStr);
    						if(choice == continueStr)
    						{
    							hashTable.put(CLEAR_RESULT_KEY, new Boolean(true));
    							hashTable.put(FRAPStudyPanel.NEW_FRAPSTUDY_KEY, newFRAPStudy);
    						}
    						else//cancel
    						{
    							throw UserCancelException.CANCEL_GENERIC;
    						}
    					}
    			}
    		};
    		
    		AsynchClientTask afterLoadingSwingTask = new AsynchClientTask(LOADING_MESSAGE, AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
    				//check to see if results need to be cleared
    				Boolean needClear = (Boolean)hashTable.get(CLEAR_RESULT_KEY);
    				if(needClear != null && needClear.booleanValue())
    				{
    					double[][] oldAnalysisData = batchRunWorkspace.getAnalysisMSESummaryData();
					    double[][] newAnalysisData = null;
						batchRunWorkspace.firePropertyChange(FRAPBatchRunWorkspace.PROPERTY_CHANGE_BATCHRUN_CLEAR_RESULTS, oldAnalysisData, newAnalysisData);
    				}
    				FRAPStudy newFRAPStudy = (FRAPStudy)hashTable.get(FRAPStudyPanel.NEW_FRAPSTUDY_KEY);
    				//setFrapStudy fires property change, so we have to put it in Swing thread.
    				getBatchRunWorkspace().getWorkingSingleWorkspace().setFrapStudy(newFRAPStudy, true);
    				
    				VirtualFrapBatchRunFrame.updateProgress(0);
    				if(isFileLoaded)
    				{
        				VirtualFrapBatchRunFrame.updateStatus("Loaded " + fileStr);
    				}
    				else
    				{
    					VirtualFrapBatchRunFrame.updateStatus("Failed loading " + fileStr+".");
    				}
    			}
    		};
    		taskArrayList.add(updateUIBeforeLoadTask);
    		taskArrayList.add(loadTask);
    		taskArrayList.add(afterLoadingSwingTask);
    	}
    	else
    	{
    		DialogUtils.showErrorDialog(singleFilePanel, "Load File name is empty. Please input a file name to continue.");
    		throw new RuntimeException("Load File name is empty. Please input a file name to continue.");
    	}
		return taskArrayList;
    } 
    
    public FRAPBatchRunWorkspace getBatchRunWorkspace() {
		return batchRunWorkspace;
	}
    
	public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace) {
		this.batchRunWorkspace = batchRunWorkspace;
	}
	
	public void setLocalWorkspace(LocalWorkspace localWorkspace)
	{
		this.localWorkspace = localWorkspace;
	}
}

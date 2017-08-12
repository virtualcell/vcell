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

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JPanel;

import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.VFRAPPreference;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.gui.VFrap_OverlayEditorPanelJAI;
import cbit.vcell.resource.VCellConfiguration;

public class BleachedROIDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "BATCHRUN_BleachROI";
    private JPanel imgPanel = null;
    private FRAPBatchRunWorkspace batchRunWorkspace = null;
    
    public BleachedROIDescriptor (JPanel imagePanel) {
    	super();
        imgPanel = imagePanel;
        JPanel bleachPanel = new JPanel(new BorderLayout());
//        cropPanel.add(imagePanel);
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(bleachPanel);
        setProgressPopupShown(false); 
        setTaskProgressKnown(false);
    }
    
    public String getNextPanelDescriptorID() {
        return BackgroundROIDescriptor.IDENTIFIER;
    }
    
    public String getBackPanelDescriptorID() {
        return CellROIDescriptor.IDENTIFIER;
    }
    
    public void aboutToDisplayPanel()
    {
    	((JPanel)getPanelComponent()).removeAll();
    	((JPanel)getPanelComponent()).add(imgPanel);
    	((BatchRunROIImgPanel)imgPanel).setBatchRunWorkspace(getBatchRunWorkspace());
    	((BatchRunROIImgPanel)imgPanel).adjustComponents(VFrap_OverlayEditorPanelJAI.DEFINE_BLEACHEDROI);
    	((BatchRunROIImgPanel)imgPanel).refreshUI();
    }
    
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		final String nextROIStr = FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name();
		AsynchClientTask setCurrentROITask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				//save current ROI and load ROI in the panel it goes next to
				((BatchRunROIImgPanel)imgPanel).setCurrentROI(nextROIStr, true);
			}
		};
		taskArrayList.add(setCurrentROITask);
		return taskArrayList;
    } 
    
    public ArrayList<AsynchClientTask> postNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		AsynchClientTask ifNeedROIAssistTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				if(VCellConfiguration.getValue(VFRAPPreference.ROI_ASSIST_REQUIREMENT_TYPE, VFRAPPreference.ROI_ASSIST_REQUIRE_ALWAYS).equals(VFRAPPreference.ROI_ASSIST_REQUIRE_ALWAYS) &&
					((BatchRunROIImgPanel)imgPanel).getBatchRunWorkspace().getWorkingFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()).getNonzeroPixelsCount()<1)
				{
					(((BatchRunROIImgPanel)imgPanel).getCenterPanel()).getOverlayEditorPanelJAI().showROIAssist();
				}
			}
		};
		taskArrayList.add(ifNeedROIAssistTask);
		return taskArrayList;
    }
    
    public ArrayList<AsynchClientTask> preBackProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
    	
		final String backROIStr = FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name();
		AsynchClientTask setCurrentROITask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				//save current ROI and load ROI in the panel it backs to 
				((BatchRunROIImgPanel)imgPanel).setCurrentROI(backROIStr, true);
			}
		};
		taskArrayList.add(setCurrentROITask);															
		return taskArrayList;
    }
    
    public ArrayList<AsynchClientTask> postBackProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		AsynchClientTask ifNeedROIAssistTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				if(VCellConfiguration.getValue(VFRAPPreference.ROI_ASSIST_REQUIREMENT_TYPE, VFRAPPreference.ROI_ASSIST_REQUIRE_ALWAYS).equals(VFRAPPreference.ROI_ASSIST_REQUIRE_ALWAYS) &&
				   ((BatchRunROIImgPanel)imgPanel).getBatchRunWorkspace().getWorkingFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getNonzeroPixelsCount()<1)
				{
					(((BatchRunROIImgPanel)imgPanel).getCenterPanel()).getOverlayEditorPanelJAI().showROIAssist();
				}
			}
		};
		taskArrayList.add(ifNeedROIAssistTask);
		return taskArrayList;
    }
    
    public FRAPBatchRunWorkspace getBatchRunWorkspace() {
		return batchRunWorkspace;
	}

	public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace) {
		this.batchRunWorkspace = batchRunWorkspace;
	}
}

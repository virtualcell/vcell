/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui.defineROIwizard;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.VFRAPPreference;
import cbit.vcell.microscopy.gui.VFrap_OverlayEditorPanelJAI;
import cbit.vcell.resource.VCellConfiguration;

public class DefineROI_CropDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "DefineROI_Crop";
    private JPanel imgPanel = null; 
    public DefineROI_CropDescriptor(JPanel imagePanel) {
        super();
        imgPanel = imagePanel;
        JPanel cropPanel = new JPanel(new BorderLayout());
//        cropPanel.add(imagePanel, BorderLayout.CENTER);
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(cropPanel);
        setProgressPopupShown(false); 
        setTaskProgressKnown(false);
    }
    
    public String getNextPanelDescriptorID() {
        return DefineROI_CellROIDescriptor.IDENTIFIER;
    }
    
    public String getBackPanelDescriptorID() {
        return null;
    }
    
    public void aboutToDisplayPanel()
    {
    	((JPanel)getPanelComponent()).removeAll();
    	((JPanel)getPanelComponent()).add(imgPanel);
    	((DefineROI_Panel)imgPanel).adjustComponents(VFrap_OverlayEditorPanelJAI.DEFINE_CROP);
    	((DefineROI_Panel)imgPanel).setCurrentROI(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(), false);
    	(((DefineROI_Panel)imgPanel).getCenterPanel()).getOverlayEditorPanelJAI().setROIAssistVisible(false);
    	
    }
    
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		final String nextROIStr = FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name();
		AsynchClientTask setCurrentROITask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				//save current ROI and load ROI in the panel it goes next to
				((DefineROI_Panel)imgPanel).setCurrentROI(nextROIStr, true);
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
				VCellConfiguration.getValue(VFRAPPreference.ROI_ASSIST_REQUIREMENT_TYPE, VFRAPPreference.ROI_ASSIST_PREF_NOT_SET); 
				if(VCellConfiguration.getValue(VFRAPPreference.ROI_ASSIST_REQUIREMENT_TYPE, VFRAPPreference.ROI_ASSIST_PREF_NOT_SET).equals(VFRAPPreference.ROI_ASSIST_PREF_NOT_SET))
				{
					DefineROI_RequireAssistPanel assistPanel = new DefineROI_RequireAssistPanel();
					JOptionPane.showMessageDialog(imgPanel, assistPanel);
					VCellConfiguration.putValue(VFRAPPreference.ROI_ASSIST_REQUIREMENT_TYPE, assistPanel.getRequireAssistType());
//					if(VFRAPPreference.getValue(VFRAPPreference.ROI_ASSIST_REQUIREMENT_TYPE, VFRAPPreference.ROI_ASSIST_REQUIRE_ALWAYS).equals(VFRAPPreference.ROI_ASSIST_REQUIRE_ALWAYS) &&
//						((DefineROI_Panel)imgPanel).getFrapWorkspace().getWorkingFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getNonzeroPixelsCount()<1)
//					{
//						((FRAPDataPanel)((DefineROI_Panel)imgPanel).getCenterPanel()).getOverlayEditorPanelJAI().showROIAssist();
//					}
//				}
//				else
//				{
//					if(VFRAPPreference.getValue(VFRAPPreference.ROI_ASSIST_REQUIREMENT_TYPE, VFRAPPreference.ROI_ASSIST_REQUIRE_ALWAYS).equals(VFRAPPreference.ROI_ASSIST_REQUIRE_ALWAYS) &&
//					   ((DefineROI_Panel)imgPanel).getFrapWorkspace().getWorkingFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getNonzeroPixelsCount()<1)
//					{
//						((FRAPDataPanel)((DefineROI_Panel)imgPanel).getCenterPanel()).getOverlayEditorPanelJAI().showROIAssist();
//					}
				}
			}
		};
		taskArrayList.add(ifNeedROIAssistTask);
		return taskArrayList;
    }
}

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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JPanel;

import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPStudy;

public class DefineROI_SummaryDescriptor extends WizardPanelDescriptor {
	public static final String IDENTIFIER = "DefineROI_Summary";
	private JPanel imgPanel = null;
	FRAPSingleWorkspace frapWorkspace = null;
	
    public DefineROI_SummaryDescriptor (JPanel imagePanel) {
        super(IDENTIFIER, new DefineROI_SummaryPanel());
        imgPanel = imagePanel;
        setProgressPopupShown(false); 
        setTaskProgressKnown(false);
    }
    
    public String getNextPanelDescriptorID() {
        return DefineROI_RoiForErrorDescriptor.IDENTIFIER;
    }
    
    public String getBackPanelDescriptorID() {
        return DefineROI_BackgroundROIDescriptor.IDENTIFIER;
    }

    public void aboutToDisplayPanel() 
    {
    	((DefineROI_SummaryPanel)getPanelComponent()).setLoadInfo(getFrapWorkspace().getWorkingFrapStudy());
	} 
    
	public ArrayList<AsynchClientTask> preBackProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		final String nextROIStr = FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name();
		AsynchClientTask setCurrentROITask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				//save current ROI and load ROI in the panel it goes next to
				((DefineROI_Panel)imgPanel).setCurrentROI(nextROIStr, false);
			}
		};
		taskArrayList.add(setCurrentROITask);
		return taskArrayList;
    } 
	
	//save the startingIndex before the panel disappears
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		AsynchClientTask verifyLoadedDataTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				String msg = ((DefineROI_SummaryPanel)getPanelComponent()).checkInputValidity();
				if(msg.equals(""))
				{
					int startIndex = ((DefineROI_SummaryPanel)getPanelComponent()).getStartingIndex();
					FRAPStudy fStudy = getFrapWorkspace().getWorkingFrapStudy();
					
					//check ROI void/discontinuous location
					Point internalVoidLocation = ROI.findInternalVoid(fStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()));
					if(internalVoidLocation != null){
						throw new Exception("CELL ROI has unfilled internal void area at image location "+
								"x="+internalVoidLocation.x+",y="+internalVoidLocation.y+"\n"+
								"Use ROI editing tools to completely define the CELL ROI");
					}
					Point[] distinctCellAreaLocations = ROI.checkContinuity(fStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()));
					if(distinctCellAreaLocations != null){
						throw new Exception("CELL ROI has at least 2 discontinuous areas at image locations \n"+
								"x="+distinctCellAreaLocations[0].x+",y="+distinctCellAreaLocations[0].y+
								" and " + "x="+distinctCellAreaLocations[1].x+",y="+distinctCellAreaLocations[1].y+"\n"+
								"Use ROI editing tools to define a single continuous CELL ROI");				
					}
					if(fStudy.getFrapData().checkROIConstraints(imgPanel))
					{
						fStudy.setStartingIndexForRecovery(startIndex);
						getFrapWorkspace().setFrapStudy(fStudy, true);
						//generate ROI rings
						fStudy.refreshDependentROIs();
					}
					else
					{
						throw new Exception("Please fix the ROI problem or cancel the wizard.");
					}
				}
				else
				{
					throw new Exception(msg);
				}
			}
		};
		
		taskArrayList.add(verifyLoadedDataTask);
		return taskArrayList;
    }
	
    public FRAPSingleWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
    
	public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace) {
		this.frapWorkspace = frapWorkspace;
	}
}

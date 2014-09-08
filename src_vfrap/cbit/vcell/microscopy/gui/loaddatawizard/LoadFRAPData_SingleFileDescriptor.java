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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.math.VariableType;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.microscopy.server.FrapDataUtils;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.SimDataConstants;

public class LoadFRAPData_SingleFileDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "LoadFRAPData_SingleFile";
    //    private FRAPStudy localFrapStudy = null;
    private LoadFRAPData_SingleFilePanel singleFilePanel = new LoadFRAPData_SingleFilePanel();
    private ScalePanel scalePanel = null;
    private boolean isFileLoaded = false;
    private FRAPSingleWorkspace frapWorkspace = null;
    
	public LoadFRAPData_SingleFileDescriptor() {
    	super();
        setPanelComponent(singleFilePanel);
        setPanelDescriptorIdentifier(IDENTIFIER);
        setProgressPopupShown(true); 
        setTaskProgressKnown(true);
    }
    //This method is override to make fure that it goes to SummaryPanel
    public String getNextPanelDescriptorID() {
        return LoadFRAPData_SummaryDescriptor.IDENTIFIER;
    }
    //This method is override to make sure that it backs to FileTypePanel
    public String getBackPanelDescriptorID() {
        return LoadFRAPData_FileTypeDescriptor.IDENTIFIER;
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
    				VirtualFrapMainFrame.updateStatus(LOADING_MESSAGE);
    			}
    		};
			

    		AsynchClientTask loadTask = new AsynchClientTask(LOADING_MESSAGE, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
					FRAPStudy newFRAPStudy = null;
					
					File inFile = new File(fileStr);
					if(inFile != null)
					{
    					if(inFile.getName().endsWith(SimDataConstants.LOGFILE_EXTENSION)) //.log (vcell log file) 
    					{
							DataIdentifier[] dataIdentifiers = FrapDataUtils.getDataIdentiferListFromVCellSimulationData(inFile, 0);
							ArrayList<String> selectedIdentifiers = new ArrayList<String> ();
							for (int i = 0; i < dataIdentifiers.length; i++) {
								if(dataIdentifiers[i].getVariableType().equals(VariableType.VOLUME)){
									selectedIdentifiers.add(dataIdentifiers[i].getName());
								}
							}
							String[][] rowData = new String[selectedIdentifiers.size()][1];
							for (int i = 0; i < selectedIdentifiers.size(); i++) {
								rowData[i][0] = selectedIdentifiers.get(i);
							}
							int[] selectedIndexArr = DialogUtils.showComponentOKCancelTableList(
									LoadFRAPData_SingleFileDescriptor.this.getPanelComponent(),
									"Select Volume Variable",
									new String[] {"Volume Variable Name"},
									rowData, ListSelectionModel.SINGLE_SELECTION);
							if(selectedIndexArr != null && selectedIndexArr.length > 0)
							{
								scalePanel = getScalePanelForLoadingLogFile();
								int choice = DialogUtils.showComponentOKCancelDialog(LoadFRAPData_SingleFileDescriptor.this.getPanelComponent(), scalePanel, "Input image maximum intensity (max: 65535)");
								if(choice == JOptionPane.OK_OPTION)
								{
									Double maxIntensity = null;
									if(scalePanel.getInputScaleString() != null)
									{
										maxIntensity = new Double(scalePanel.getInputScaleString());
									}
									newFRAPStudy = FRAPWorkspace.loadFRAPDataFromVcellLogFile(inFile, selectedIdentifiers.get(selectedIndexArr[0]), null, maxIntensity, true, this.getClientTaskStatusSupport());
									isFileLoaded = true;
								}
								else
								{
									throw UserCancelException.CANCEL_GENERIC;
								}
							}else{
								throw UserCancelException.CANCEL_GENERIC;
							}
    					}else if(inFile.getName().endsWith(SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_HDF5))
    					{
    						scalePanel = getScalePanelForLoadingLogFile();
							int choice = DialogUtils.showComponentOKCancelDialog(LoadFRAPData_SingleFileDescriptor.this.getPanelComponent(), scalePanel, "Input data maximum value (max: 65535) which is used to avoid losing double precision when casting it to short.");
							if(choice == JOptionPane.OK_OPTION)
							{
								Double maxIntensity = null;
								if(scalePanel.getInputScaleString() != null)
								{
									maxIntensity = new Double(scalePanel.getInputScaleString());
								}
								newFRAPStudy = FRAPWorkspace.loadFRAPDataFromHDF5File(inFile,  maxIntensity, this.getClientTaskStatusSupport());
								isFileLoaded = true;
							}
							else
							{
								throw UserCancelException.CANCEL_GENERIC;
							}
    					}
    					else //.lsm or other image formatss
    					{
    							newFRAPStudy = FRAPWorkspace.loadFRAPDataFromImageFile(inFile, this.getClientTaskStatusSupport());
    							isFileLoaded = true;
    					}
					}
					else
					{
						throw new RuntimeException("Input file is null.");
					}
					//for all loaded file
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
        				VirtualFrapMainFrame.updateStatus("Loaded " + fileStr);
    				}
    				else
    				{
						VirtualFrapMainFrame.updateStatus("Failed loading " + fileStr+".");
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
    
    private ScalePanel getScalePanelForLoadingLogFile()
    {
    	if(scalePanel == null)
    	{
    		scalePanel = new ScalePanel();
    	}
    	return scalePanel;
    }
    
    public FRAPSingleWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
    
	public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace) {
		this.frapWorkspace = frapWorkspace;
	}
	
	@SuppressWarnings("serial")
	class ScalePanel extends JPanel implements ActionListener
	{
		JRadioButton noScaleButton = new JRadioButton("Use original intensity value");
		JRadioButton scaleButton = new JRadioButton("Set desired maximum intersity value");
		JTextField scaleTextField = new JTextField(8);
		
		public ScalePanel()
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			scaleButton.setSelected(true);
			scaleTextField.setEnabled(true);
			ButtonGroup bg = new ButtonGroup();
			bg.add(noScaleButton);
			bg.add(scaleButton);
			
			noScaleButton.addActionListener(this);
			scaleButton.addActionListener(this);
			scaleTextField.setText("50000");
			
			add(noScaleButton);
			add(scaleButton);
			add(scaleTextField);
		}
		
		public String getInputScaleString()
		{
			if(scaleButton.isSelected())
			{
				return scaleTextField.getText();
			}
			else
			{
				return null;
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			if(e.getSource() == scaleButton)
			{
				if(scaleButton.isSelected())
				{
					scaleTextField.setEnabled(true);
				}
			}
			else if(e.getSource() == noScaleButton)
			{
				if(noScaleButton.isSelected())
				{
					scaleTextField.setEnabled(false);
				}
			}
		}
	}
}

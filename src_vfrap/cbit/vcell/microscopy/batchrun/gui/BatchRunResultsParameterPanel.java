/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.batchrun.gui;

import java.awt.BorderLayout;

import org.vcell.util.gui.BoxPanel;

import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;

@SuppressWarnings("serial")
public class BatchRunResultsParameterPanel extends BoxPanel
{
	private BatchRunResultsParamTablePanel paramTablePanel;
	FRAPBatchRunWorkspace batchRunWorkspace = null; 
	
	public BatchRunResultsParameterPanel() 
	{
		super("Analysis Parameters for Each FRAP Document");
		setName("");
		paramTablePanel = new BatchRunResultsParamTablePanel(this);
        contentPane.setLayout(new BorderLayout());
        contentPane.add(paramTablePanel);
	}
	
	public FRAPBatchRunWorkspace getBatchRunWorkspace()
    {
    	return batchRunWorkspace;
    }
    
    public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace)
	{
		this.batchRunWorkspace = batchRunWorkspace;
		paramTablePanel.setBatchRunWorkspace(batchRunWorkspace);
	}
	
    public void updateTableData()
    {
    	paramTablePanel.updateTableData();
    }
    
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			BatchRunResultsParameterPanel aPanel = new BatchRunResultsParameterPanel();
			frame.setContentPane(aPanel);
//			frame.pack();
			frame.setSize(900,800);
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
			
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}
}

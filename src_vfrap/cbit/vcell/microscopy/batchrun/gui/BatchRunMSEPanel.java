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
public class BatchRunMSEPanel extends BoxPanel
{
	private BatchRunMSETablePanel mseTablePanel;
	private FRAPBatchRunWorkspace batchRunWorkspace = null;
	
	public BatchRunMSEPanel() 
	{
		super("Squared Error among FRAP Documents under Selected ROIs");
			mseTablePanel = new BatchRunMSETablePanel(this);
	        contentPane.setLayout(new BorderLayout());
	        contentPane.add(mseTablePanel);
	}
	
	public FRAPBatchRunWorkspace getBatchRunWorkspace()
    {
    	return batchRunWorkspace;
    }
    
    public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace)
	{
		this.batchRunWorkspace = batchRunWorkspace;
		mseTablePanel.setBatchRunWorkspace(batchRunWorkspace);
	}
	
    public void updateTableData()
    {
    	mseTablePanel.updateTableData();
    }
    
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			BatchRunMSEPanel aPanel = new BatchRunMSEPanel();
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

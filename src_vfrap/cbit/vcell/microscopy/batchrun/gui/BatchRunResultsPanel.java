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
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;

@SuppressWarnings("serial")
public class BatchRunResultsPanel extends JPanel
{
	public final static String MODEL_TYPE_PREFIX = "  Selected Model Type : ";
	private BatchRunResultsParameterPanel batchRunParamPanel = null;
	private BatchRunMSEPanel batchRunMSEPanel = null;
	private JScrollPane scrollPane = null;
	private JLabel modelTypeLabel = null;
	private JPanel centerPanel = null;
	
	public BatchRunResultsPanel()
	{
		super();
		setLayout(new BorderLayout());
		add(getModelTypeLabel(), BorderLayout.NORTH);
		
		scrollPane = new JScrollPane(getCenterPanel());
		scrollPane.setAutoscrolls(true);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    	scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    	//add scrollpane to the panel
		add(scrollPane, BorderLayout.CENTER);
	}
	
	public JLabel getModelTypeLabel()
	{
		if(modelTypeLabel == null)
		{
			modelTypeLabel = new JLabel(MODEL_TYPE_PREFIX); 
			modelTypeLabel.setBorder(new EmptyBorder(2, 0, 4, 0));
			modelTypeLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
			modelTypeLabel.setForeground(new Color(100, 100, 255));
		}
		return modelTypeLabel;
	}
	
	public void setModelTypeLabel(String modelType)
	{
		modelTypeLabel.setText(MODEL_TYPE_PREFIX +" " + modelType); 
	}
	
	private JPanel getCenterPanel()
	{
		if(centerPanel == null)
		{
			centerPanel= new JPanel(new GridBagLayout());
//			centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
			 GridBagConstraints gc1 = new GridBagConstraints();
		        gc1.gridx = 0;
		        gc1.gridy = 0;
		        gc1.weightx = 1.0;
		        gc1.anchor = GridBagConstraints.WEST;
		        gc1.fill = GridBagConstraints.HORIZONTAL;

		        GridBagConstraints gc2 = new GridBagConstraints();
		        gc2.gridx = 0;
		        gc2.gridy = 1;
		        gc2.weightx = 1.0;
		        gc1.anchor = GridBagConstraints.WEST;
		        gc2.fill = GridBagConstraints.HORIZONTAL;
			centerPanel.add(getBatchRunResultsParameterPanel(),gc1);
			centerPanel.add(getBatchRunMSEPanel(),gc2);
		}
		return centerPanel;
	}
	
	public BatchRunResultsParameterPanel getBatchRunResultsParameterPanel()
	{
		if(batchRunParamPanel == null)
		{
			batchRunParamPanel= new BatchRunResultsParameterPanel();
		}
		return batchRunParamPanel;
	}
	
	public BatchRunMSEPanel getBatchRunMSEPanel()
	{
		if(batchRunMSEPanel == null)
		{
			batchRunMSEPanel = new BatchRunMSEPanel();
		}
		return batchRunMSEPanel;
	}
	
	 public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace)
    {
    	getBatchRunResultsParameterPanel().setBatchRunWorkspace(batchRunWorkspace);
    	getBatchRunMSEPanel().setBatchRunWorkspace(batchRunWorkspace);
    }
	
	 public void updateTableData()
	 {
		 batchRunParamPanel.updateTableData();
		 batchRunMSEPanel.updateTableData();
	 }
	 
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			BatchRunResultsPanel aPanel = new BatchRunResultsPanel();
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

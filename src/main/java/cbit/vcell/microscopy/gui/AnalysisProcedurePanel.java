/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cbit.vcell.microscopy.gui.FRAPStudyPanel.WorkFlowButtonHandler;

@SuppressWarnings("serial")
public class AnalysisProcedurePanel extends JPanel
{
	public final static int STAGE_LOAD_FRAP = 0;
	public final static int STAGE_DEFINE_ROIS = 1;
	public final static int STAGE_MODEL_TYPE = 2;
	public final static int STAGE_EST_PARAMS = 3;
	
	public final static int NUM_BUTTON_IMAGES = 4;
	public final static int NUM_LABEL_IMAGES = 3;
	private URL[] iconFiles = {getClass().getResource("/images/loadDataButton.jpg"),
							   getClass().getResource("/images/arrow.gif"),
							   getClass().getResource("/images/defineROIButton.jpg"),
							   getClass().getResource("/images/arrow.gif"),
							   getClass().getResource("/images/modelTypeButton.jpg"),
							   getClass().getResource("/images/arrow.gif"),
							   getClass().getResource("/images/estimateButton.jpg")};
							   
	private static final String[] buttonTips = new String[]{"Load FRAP images", "Define regions of interest", "Choose possible models", "Estimate parameters for all selected models"};
	private static final String[] buttonActionCommands = new String[]{VirtualFrapMainFrame.LOAD_IMAGE_COMMAND,
																	  VirtualFrapMainFrame.DEFINE_ROI_COMMAND,
																	  VirtualFrapMainFrame.CHOOSE_MODEL_COMMAND,
																	  VirtualFrapMainFrame.ESTIMATE_PARAM_COMMAND};
	private ImageIcon[] icons = new ImageIcon[iconFiles.length];
    private JButton[] buttons = new JButton[NUM_BUTTON_IMAGES];
    private JLabel[] labels = new JLabel[NUM_LABEL_IMAGES];
	
	public AnalysisProcedurePanel()
	{
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,7};
		setLayout(gridBagLayout);
		initial();
//		setWorkFlowStage(STAGE_LOAD_FRAP);
	}
	private void initial()
	{
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.ipady = 0;
		gridBagConstraints1.ipadx = 0;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.ipady = 0;
		gridBagConstraints2.ipadx = 0;
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		add(getTopPanel(), gridBagConstraints1);
		add(getBotPanel(), gridBagConstraints2);
//		setWorkFlowStage(STAGE_LOAD_FRAP);
	}
	private JPanel getTopPanel()
	{
		JPanel topPanel = new JPanel();
		JLabel label = new JLabel("Data Analysis Work Flow");
		label.setFont(new Font("arial", Font.BOLD+Font.ITALIC, 14));
		label.setForeground(new Color(0,0,128));
		topPanel.add(label, BorderLayout.WEST);
		
		return topPanel;
	}
	private JPanel getBotPanel()
	{
		JPanel botPanel = new JPanel();
		botPanel.setLayout(new GridBagLayout());
		for (int i = 0; i < (buttons.length); ++i) {
            icons[2*i] = new ImageIcon(iconFiles[2*i]);//button icon
            buttons[i] = new JButton(icons[2*i]);
            buttons[i].setMargin(new Insets(0, 0, 0, 0));
            buttons[i].setToolTipText(buttonTips[i]);
            buttons[i].setActionCommand(buttonActionCommands[i]);
            GridBagConstraints butConstraint = new GridBagConstraints();
            butConstraint.gridx = 2*i;
            butConstraint.gridy = 0;
            botPanel.add(buttons[i],butConstraint);
            if(i < (buttons.length -1))
            {
	            GridBagConstraints lblConstraint = new GridBagConstraints();
	            lblConstraint.gridx = 2*i+1;
	            lblConstraint.gridy = 0;
	            icons[2*i+1] = new ImageIcon(iconFiles[2*i+1]);//label icon
	            labels[i] = new JLabel(icons[2*i+1]);
	            botPanel.add(labels[i],lblConstraint);
            }
        }
	
		return botPanel;
	}
	
	public void setWorkFlowStage(int stage)
	{
		if(stage == STAGE_LOAD_FRAP)
		{
			disableAllButtons();
			buttons[STAGE_LOAD_FRAP].setEnabled(true);
		}
		else if(stage == STAGE_DEFINE_ROIS)
		{
			disableAllButtons();
			buttons[STAGE_LOAD_FRAP].setEnabled(true);
			buttons[STAGE_DEFINE_ROIS].setEnabled(true);
		} 
		else if(stage == STAGE_MODEL_TYPE)
		{
			disableAllButtons();
			buttons[STAGE_LOAD_FRAP].setEnabled(true);
			buttons[STAGE_DEFINE_ROIS].setEnabled(true);
			buttons[STAGE_MODEL_TYPE].setEnabled(true);
		}
		else if(stage == STAGE_EST_PARAMS)
		{
			disableAllButtons();
			buttons[STAGE_LOAD_FRAP].setEnabled(true);
			buttons[STAGE_DEFINE_ROIS].setEnabled(true);
			buttons[STAGE_MODEL_TYPE].setEnabled(true);
			buttons[STAGE_EST_PARAMS].setEnabled(true);
		}
	}
	
	private void disableAllButtons()
	{
		for(int i=0; i<buttons.length; i++)
		{
			buttons[i].setEnabled(false);
		}
	}
	
	public void addButtonHandler(WorkFlowButtonHandler handler)
	{
		for(int i=0; i<buttons.length; i++)
		{
			buttons[i].addActionListener(handler);
		}
	}
}

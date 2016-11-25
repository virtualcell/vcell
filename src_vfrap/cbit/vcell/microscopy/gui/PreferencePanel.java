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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

import cbit.vcell.microscopy.VFRAPPreference;
import cbit.vcell.resource.VCellConfiguration;

@SuppressWarnings("serial")
public class PreferencePanel extends JPanel
{
	private JTabbedPane tabbedPane = null; 
	private JRadioButton alwaysAutoPopupRadioButton = null;
	private JRadioButton doNotPopupRadioButton = null;
	
	public PreferencePanel() {
		super();
		setLayout(new BorderLayout());

		tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);

		final JPanel panel = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,7};
		panel.setLayout(gridBagLayout);
		tabbedPane.addTab("ROI Assist", null, panel, null);

		alwaysAutoPopupRadioButton = new JRadioButton();
		alwaysAutoPopupRadioButton.setText("Always auto pop-up ROI Assist  for undefined ROIs");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		panel.add(alwaysAutoPopupRadioButton, gridBagConstraints);

		doNotPopupRadioButton = new JRadioButton();
		doNotPopupRadioButton.setText("Do NOT pop-up ROI Assist for undefined ROIs   ");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = GridBagConstraints.WEST;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 0;
		panel.add(doNotPopupRadioButton, gridBagConstraints_1);
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(alwaysAutoPopupRadioButton);
		bg.add(doNotPopupRadioButton);
	}

	public String getROIAssistType()
	{
		if(alwaysAutoPopupRadioButton.isSelected())
		{
			return VFRAPPreference.ROI_ASSIST_REQUIRE_ALWAYS;
		}
		else if(doNotPopupRadioButton.isSelected())
		{
			return VFRAPPreference.ROI_ASSIST_REQUIRE_NO;
		}
		return VFRAPPreference.ROI_ASSIST_PREF_NOT_SET;
	}

	public boolean isStatusSet()
	{
		return alwaysAutoPopupRadioButton.isSelected() || doNotPopupRadioButton.isSelected();
	}
	
	public void setIniStatus() 
	{
		if(VCellConfiguration.getValue(VFRAPPreference.ROI_ASSIST_REQUIREMENT_TYPE, VFRAPPreference.ROI_ASSIST_PREF_NOT_SET).equals(VFRAPPreference.ROI_ASSIST_REQUIRE_ALWAYS))
		{
			alwaysAutoPopupRadioButton.setSelected(true);
		}
		else if(VCellConfiguration.getValue(VFRAPPreference.ROI_ASSIST_REQUIREMENT_TYPE, VFRAPPreference.ROI_ASSIST_PREF_NOT_SET).equals(VFRAPPreference.ROI_ASSIST_REQUIRE_NO))
		{
			doNotPopupRadioButton.setSelected(true);
		}
	}
}

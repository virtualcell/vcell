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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import cbit.vcell.microscopy.VFRAPPreference;

@SuppressWarnings("serial")
public class RequireAssistPanel extends JPanel
{
	private JRadioButton alwaysButton = null;
	private JRadioButton noButton = null;
	
	public RequireAssistPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,7,7,7,7,7};
		setLayout(gridBagLayout);

		JLabel needAssistLabel = new JLabel();
		needAssistLabel.setText(" Auto pop-up 'ROI Assist' dialog for creating ROIs ?");
		needAssistLabel.setForeground(new Color(0, 0, 128));
		needAssistLabel.setFont(new Font("", Font.BOLD | Font.ITALIC, 15));
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		add(needAssistLabel, gridBagConstraints);

		alwaysButton = new JRadioButton();
		alwaysButton.setSelected(true);
		alwaysButton.setText("Yes. Always auto pop-up 'ROI Assist' dialog for undefined ROIs.");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = GridBagConstraints.WEST;
		gridBagConstraints_1.gridy = 6;
		gridBagConstraints_1.gridx = 0;
		add(alwaysButton, gridBagConstraints_1);

		noButton = new JRadioButton();
		noButton.setText("No thanks. Do NOT pop-up 'ROI Assist' dialog for undefined ROIs.");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.anchor = GridBagConstraints.WEST;
		gridBagConstraints_3.gridy = 7;
		gridBagConstraints_3.gridx = 0;
		add(noButton, gridBagConstraints_3);
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(alwaysButton);
		bg.add(noButton);
	}
	public String getRequireAssistType()
	{
		if(alwaysButton.isSelected())
		{
			return VFRAPPreference.ROI_ASSIST_REQUIRE_ALWAYS;
		}
		else
		{
			return VFRAPPreference.ROI_ASSIST_REQUIRE_NO;
		}
	}
}

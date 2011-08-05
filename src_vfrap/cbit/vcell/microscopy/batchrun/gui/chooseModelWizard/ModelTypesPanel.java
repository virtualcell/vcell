/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.batchrun.gui.chooseModelWizard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EtchedBorder;

import cbit.vcell.microscopy.FRAPModel;

@SuppressWarnings("serial")
public class ModelTypesPanel extends JPanel
{
	private JCheckBox diffOneCheckBox = null;
	private JCheckBox diffTwoCheckBox = null;
	private JCheckBox koffCheckBox = null;
//	private JCheckBox effectiveDiffCheckBox = null;
//	private JCheckBox diffBindingCheckBox = null;
	
	public ModelTypesPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,0,0,0,7,7,7,7,0,0,0,0,0,7,0,0,0,0,0,0,7};
		setLayout(gridBagLayout);

		final JLabel choosePossibleModelLabel = new JLabel();
		choosePossibleModelLabel.setFont(new Font("", Font.BOLD | Font.ITALIC, 16));
		choosePossibleModelLabel.setForeground(new Color(0, 0, 128));
		choosePossibleModelLabel.setText("Choose Possible Models");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		add(choosePossibleModelLabel, gridBagConstraints);

		final JLabel chooseOneOrLabel = new JLabel();
		chooseOneOrLabel.setText("Choose one or more possible models that may fit your data. A comparison of the ");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = GridBagConstraints.WEST;
		gridBagConstraints_1.gridy = 7;
		gridBagConstraints_1.gridx = 0;
		add(chooseOneOrLabel, gridBagConstraints_1);

		final JLabel selectedModelsWillLabel = new JLabel();
		selectedModelsWillLabel.setText("selected models will be given after  'Parameter Estimation'.  The possible models are");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.anchor = GridBagConstraints.WEST;
		gridBagConstraints_2.gridy = 8;
		gridBagConstraints_2.gridx = 0;
		add(selectedModelsWillLabel, gridBagConstraints_2);

		final JLabel diffusionWithOneLabel = new JLabel();
		diffusionWithOneLabel.setText("listed below :");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.anchor = GridBagConstraints.WEST;
		gridBagConstraints_3.gridy = 9;
		gridBagConstraints_3.gridx = 0;
		add(diffusionWithOneLabel, gridBagConstraints_3);

		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(450, 3));
		separator.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(EtchedBorder.RAISED),new EtchedBorder(EtchedBorder.LOWERED)));
		GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.anchor = GridBagConstraints.WEST;
		gridBagConstraints_8.gridy = 12;
		gridBagConstraints_8.gridx = 0;
		add(separator, gridBagConstraints_8);
		
		JLabel diffustionOnlyLabel = new JLabel("Diffusion Dominant Models :");
		diffustionOnlyLabel.setFont(new Font("", Font.BOLD, 12));
		diffustionOnlyLabel.setForeground(new Color(0, 0, 128));
		GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.anchor = GridBagConstraints.WEST;
		gridBagConstraints_7.gridy = 18;
		gridBagConstraints_7.gridx = 0;
		add(diffustionOnlyLabel, gridBagConstraints_7);
		
		diffOneCheckBox = new JCheckBox();
		diffOneCheckBox.setSelected(true);
		diffOneCheckBox.setText(FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT]);
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.anchor = GridBagConstraints.WEST;
		gridBagConstraints_5.gridy = 19;
		gridBagConstraints_5.gridx = 0;
		add(diffOneCheckBox, gridBagConstraints_5);

		diffTwoCheckBox = new JCheckBox();
		diffTwoCheckBox.setText(FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS]);
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.anchor = GridBagConstraints.WEST;
		gridBagConstraints_6.gridy = 20;
		gridBagConstraints_6.gridx = 0;
		add(diffTwoCheckBox, gridBagConstraints_6);
		
		JLabel reactionOnlyLabel = new JLabel("Reaction Dominant Models :");
		reactionOnlyLabel.setFont(new Font("", Font.BOLD, 12));
		reactionOnlyLabel.setForeground(new Color(0, 0, 128));
		GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.anchor = GridBagConstraints.WEST;
		gridBagConstraints_9.gridy = 25;
		gridBagConstraints_9.gridx = 0;
		add(reactionOnlyLabel, gridBagConstraints_9);
		
		koffCheckBox = new JCheckBox();
		koffCheckBox.setText(FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_REACTION_OFF_RATE]);
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.anchor = GridBagConstraints.WEST;
		gridBagConstraints_10.gridy = 26;
		gridBagConstraints_10.gridx = 0;
		add(koffCheckBox, gridBagConstraints_10);
		
		//put checkboxes into a button group
		ButtonGroup bg = new ButtonGroup();
		bg.add(diffOneCheckBox);
		bg.add(diffTwoCheckBox);
		bg.add(koffCheckBox);
	}
	
	public boolean[] getModelTypes()
	{
		boolean[] result = new boolean[FRAPModel.NUM_MODEL_TYPES];
		if(diffOneCheckBox.isSelected())
		{
			result[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] = true;
		}
		else if(diffTwoCheckBox.isSelected())
		{
			result[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] = true;
		}
		else if(koffCheckBox.isSelected())
		{
			result[FRAPModel.IDX_MODEL_REACTION_OFF_RATE] = true;
		}
		return result;
	}
	
	public void clearAllSelected()
	{
		diffOneCheckBox.setSelected(false);
		diffTwoCheckBox.setSelected(false);
		koffCheckBox.setSelected(false);
//		diffBindingCheckBox.setSelected(false);
	}
	
	public void setDiffOneSelected(boolean bSelected)
	{
		diffOneCheckBox.setSelected(bSelected);
	}
	
	public void setDiffTwoSelected(boolean bSelected)
	{
		diffTwoCheckBox.setSelected(bSelected);
	}
	
	public void setReactionOffRateSelected(boolean bSelected)
	{
		koffCheckBox.setSelected(bSelected);
	}
//	public void setDiffBindingSelected(boolean bSelected)
//	{
//		diffBindingCheckBox.setSelected(bSelected);
//	}
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.model.rbm.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.TaskCallbackProcessor;

@SuppressWarnings("serial")
public class ValidateConstraintsPanel extends DocumentEditorSubPanel  {
	
	public enum ActionButtons {
		Apply,
		Cancel
	}
	ActionButtons buttonPushed = ActionButtons.Cancel;
	
	private EventHandler eventHandler = new EventHandler();
	
	private JLabel maxIterationTextField;
	private JLabel maxMolTextField;
	private JLabel speciesLimitTextField;
	private JLabel reactionsLimitTextField;
	private JLabel somethingInsufficientLabel;
	
	private JButton applyButton;
	private JButton cancelButton;
	
	private final NetworkConstraintsPanel owner;
	private ChildWindow parentChildWindow;

	private class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		}
	}
	
public ValidateConstraintsPanel(NetworkConstraintsPanel owner) {
	super();
	this.owner = owner;
	initialize();
}


private void handleException(java.lang.Throwable exception) {
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}

private void initialize() {
	try {
		setName("ValidateConstraintsPanel");
		setLayout(new GridBagLayout());
			
		maxIterationTextField = new JLabel();
		maxMolTextField = new JLabel();
		speciesLimitTextField = new JLabel();
		reactionsLimitTextField = new JLabel();
		somethingInsufficientLabel = new JLabel();

		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();		
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(6, 8, 0, 0);				//  top, left, bottom, right 
		add(new JLabel("Max. Iterations"), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(6, 0, 0, 10);
		add(maxIterationTextField, gbc);

		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 8;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(6, 8, 0, 0);
		add(new JLabel("Max. Molecules / Species"), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(6, 0, 0, 0);
		add(maxMolTextField, gbc);
		
		// ------------------------------------------------------
		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 8;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(6, 8, 0, 0);
		add(new JLabel("Species Limit"), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(6, 0, 0, 0);
		add(speciesLimitTextField, gbc);

		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 8;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(6, 8, 4, 0);
		add(new JLabel("Reactions Limit"), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(6, 0, 4, 0);
		add(reactionsLimitTextField, gbc);
		
		// ------------------------------------------------------
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 4;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 8, 4, 10);
		add(somethingInsufficientLabel, gbc);

		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 2, 8, 2);
		add(getApplyButton(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(6, 2, 8, 10);
		add(getCancelButton(), gbc);
		
		maxIterationTextField.setText(owner.getSimulationContext().getNetworkConstraints().getTestMaxIteration() + "");
		maxMolTextField.setText(owner.getSimulationContext().getNetworkConstraints().getTestMaxMoleculesPerSpecies() + "");
		speciesLimitTextField.setText(owner.getSimulationContext().getNetworkConstraints().getTestSpeciesLimit() + "");
		reactionsLimitTextField.setText(owner.getSimulationContext().getNetworkConstraints().getTestReactionsLimit() + "");

		String s = "none";
		TaskCallbackProcessor tcbp = owner.getSimulationContext().getTaskCallbackProcessor();
		if(tcbp.getPreviousIterationSpecies()>0 && tcbp.getCurrentIterationSpecies()>0 && tcbp.getCurrentIterationSpecies()!=tcbp.getPreviousIterationSpecies()) {
			s = "<font color=#8C001A>" + SimulationContext.IssueInsufficientIterations + "</font>";
		} else if(tcbp.getPreviousIterationSpecies()>0 && tcbp.getCurrentIterationSpecies()>0 && tcbp.getCurrentIterationSpecies()==tcbp.getPreviousIterationSpecies()) {
			if(tcbp.isNeedAdjustMaxMolecules()) {
				s = "<font color=#8C001A>" + SimulationContext.IssueInsufficientMolecules + "</font>";
			}
		}
		somethingInsufficientLabel.setText("<html>Warning:  " + s + "</html>");
		
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

public ActionButtons getButtonPushed() {
	return buttonPushed;
}

private JButton getApplyButton() {
	if (applyButton == null) {
		applyButton = new javax.swing.JButton("Apply");
		applyButton.setName("ApplyButton");
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonPushed = ActionButtons.Apply;

				parentChildWindow.close();
			}
		});
	}
	return applyButton;
}
private JButton getCancelButton() {
	if (cancelButton == null) {
		cancelButton = new javax.swing.JButton("Cancel");
		cancelButton.setName("CancelButton");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonPushed = ActionButtons.Cancel;
				parentChildWindow.close();
			}
		});
	}
	return cancelButton;
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {

}

public void setChildWindow(ChildWindow childWindow) {
	this.parentChildWindow = childWindow;
}


}

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
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.vcell.model.rbm.NetworkConstraints;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.TaskCallbackProcessor;
import cbit.vcell.mapping.gui.StoichiometryTableModel;

@SuppressWarnings("serial")
public class ValidateConstraintsPanel extends DocumentEditorSubPanel  {
	
	public enum ActionButtons {
		Apply,
		Cancel
	}
	ActionButtons buttonPushed = ActionButtons.Cancel;
	
	private EventHandler eventHandler = new EventHandler();
	private boolean showStoichiometryTable = false;
	
	private EditorScrollTable stoichiometryTable = null;
	private StoichiometryTableModel stoichiometryTableModel = null;
	
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
	
public ValidateConstraintsPanel(NetworkConstraintsPanel owner, boolean showStoichiometryTable) {
	super();
	this.owner = owner;
	this.showStoichiometryTable = showStoichiometryTable;
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

		NetworkConstraints nc = owner.getSimulationContext().getNetworkConstraints();
		maxIterationTextField.setText(nc.getTestMaxIteration() + "");
		maxMolTextField.setText(nc.getTestMaxMoleculesPerSpecies() + "");
		speciesLimitTextField.setText(nc.getTestSpeciesLimit() + "");
		reactionsLimitTextField.setText(nc.getTestReactionsLimit() + "");

		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();		
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
//		gbc.weighty = 1.0;
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
//		gbc.weighty = 1.0;
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
//		gbc.weighty = 1.0;
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
//		gbc.weighty = 1.0;
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
		if(showStoichiometryTable) {
			stoichiometryTable = new EditorScrollTable();
			stoichiometryTableModel = new StoichiometryTableModel(stoichiometryTable);
			stoichiometryTable.setModel(stoichiometryTableModel);
			stoichiometryTableModel.setSimulationContext(owner.getSimulationContext());
			stoichiometryTableModel.displayTestMaxStoichiometry();
			stoichiometryTableModel.setValueEditable(false);		// we disable editing for the Value column

			JScrollPane sp = new JScrollPane(stoichiometryTable);
			sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			gridy ++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.gridwidth = 4;
			gbc.insets = new Insets(5, 8, 4, 10);
			add(sp, gbc);
		}
		// -------------------------------------------------------
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

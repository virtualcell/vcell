/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.solver.nfsim;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.math.MathDescription;
import cbit.vcell.solver.NFsimSimulationOptions;
import cbit.vcell.solver.SolverTaskDescription;


@SuppressWarnings("serial")
public class NFSimSimulationOptionsPanel extends CollapsiblePanel {

	private JCheckBox randomSeedCheckBox;
	private JTextField randomSeedTextField;
	private SolverTaskDescription solverTaskDescription = null;	
	private JButton randomSeedHelpButton = null;
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();

	private class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, PropertyChangeListener {

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == randomSeedHelpButton) {
				DialogUtils.showInfoDialog(NFSimSimulationOptionsPanel.this, "Random Seed", "<html>rand_seed <i>int</i> " +
						"<br>Seed for random number generator. If this line is not entered, the current " +
						"time is used as a seed, producing different sequences for each run.</html>");
			} else if (source == randomSeedCheckBox) {
				randomSeedTextField.setEditable(randomSeedCheckBox.isSelected());
				if(!randomSeedCheckBox.isSelected())
				{
					setNewRandomSeed();
				}
			}
		}

		public void focusGained(FocusEvent e) {
		}

		public void focusLost(FocusEvent e) {
			if (e.isTemporary()) {
				return;
			}
			if (e.getSource() == randomSeedTextField) {
				setNewRandomSeed();
			}
		}

		public void propertyChange(PropertyChangeEvent evt) {
			
		}
		
	}
	public NFSimSimulationOptionsPanel() {
		super("Advanced Solver Options", false);
		initialize();
	}
	private void initialize() {
		randomSeedCheckBox = new JCheckBox("random seed");
		randomSeedTextField = new JTextField();
	
		
		randomSeedHelpButton = new JButton(" ? ");
		Font font = randomSeedHelpButton.getFont().deriveFont(Font.BOLD);
		Border border = BorderFactory.createEmptyBorder(1, 1, 1, 1);
		randomSeedHelpButton.setFont(font);
		randomSeedHelpButton.setBorder(border);
		
		getContentPanel().setLayout(new GridBagLayout());		
		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		getContentPanel().add(randomSeedCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 4);
		getContentPanel().add(randomSeedHelpButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(randomSeedTextField, gbc);
		
}
	
	private void initConnections() {
		randomSeedCheckBox.addActionListener(ivjEventHandler);
		randomSeedTextField.addFocusListener(ivjEventHandler);
		
		randomSeedHelpButton.addActionListener(ivjEventHandler);
	}
	
	public final void setSolverTaskDescription(SolverTaskDescription newValue) {
		SolverTaskDescription oldValue = solverTaskDescription;
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(ivjEventHandler);
		}
		solverTaskDescription = newValue;

		if (newValue != null) {
			newValue.addPropertyChangeListener(ivjEventHandler);
		}		
		solverTaskDescription = newValue;
		
		refresh();
		initConnections();
	}

	private void refresh() {
		if (solverTaskDescription != null) {
			MathDescription mathDescription = solverTaskDescription.getSimulation().getMathDescription();
			if (!(mathDescription.isRuleBased())) {
				setVisible(false);
				return;
			}
		}
			
		setVisible(true);
		NFsimSimulationOptions smoldynSimulationOptions = solverTaskDescription.getNFSimSimulationOptions();
		Integer randomSeed = smoldynSimulationOptions.getRandomSeed();
		if (randomSeed == null) {
			randomSeedTextField.setEditable(false);
			randomSeedCheckBox.setSelected(false);
		} else {			
			randomSeedTextField.setEditable(true);
			randomSeedCheckBox.setSelected(true);
			randomSeedTextField.setText(randomSeed.toString());
		}
	}
	
	private void setNewRandomSeed(){
		if(!isVisible()){
			return;
		}
		
		Integer randomSeed = null;
		if (randomSeedCheckBox.isSelected()) {
			try {
				randomSeed = new Integer(randomSeedTextField.getText());
			} catch (NumberFormatException ex) {
				DialogUtils.showErrorDialog(this, "Wrong number format for random seed: " + ex.getMessage());
				return;
			}
		}
		solverTaskDescription.getSmoldynSimulationOptions().setRandomSeed(randomSeed);		
	}
}

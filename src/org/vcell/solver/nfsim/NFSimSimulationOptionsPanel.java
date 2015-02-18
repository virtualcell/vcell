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

	// TODO: all that's being set here is used in NFsimSimulationOptions
	// also synchronize with NetworkConstraints
	
	private SolverTaskDescription solverTaskDescription = null;	

	private JCheckBox observableComputationCheckBox;
	private JTextField observableComputationTextField;
	private JButton observableComputationHelpButton = null;

	private JCheckBox moleculeDistanceCheckBox;
	private JTextField moleculeDistanceTextField;
	private JButton moleculeDistanceHelpButton = null;

	private JCheckBox aggregateBookkeepingCheckBox;
	private JTextField aggregateBookkeepingTextField;
	private JButton aggregateBookkeepingHelpButton = null;

	private JCheckBox maxMoleculesPerTypeCheckBox;
	private JTextField maxMoleculesPerTypeTextField;
	private JButton maxMoleculesPerTypeHelpButton = null;

	private JCheckBox equilibrateTimeCheckBox;
	private JTextField equilibrateTimeTextField;
	private JButton equilibrateTimeHelpButton = null;

	private JCheckBox randomSeedCheckBox;
	private JTextField randomSeedTextField;
	private JButton randomSeedHelpButton = null;

	private JCheckBox preventIntraBondsCheckBox;
	private JTextField preventIntraBondsTextField;
	private JButton preventIntraBondsHelpButton = null;

	private JCheckBox gCheckBox;
	private JTextField gTextField;
	private JButton gHelpButton = null;

	private IvjEventHandler ivjEventHandler = new IvjEventHandler();

	private class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, PropertyChangeListener {

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == observableComputationHelpButton) {
				DialogUtils.showInfoDialog(NFSimSimulationOptionsPanel.this, "On-the-fly computations of observables", 
						"<html>-notf <i>boolean</i> " +
						"<br> By default, observables are calculated on-the-fly, updating all observables at each simulation step. "
						+ "This is necessary when rates of reactions depend on Observables. Otherwise, the values of Observables need "
						+ "to be computed only at output steps. This is useful when the number of simulation steps between each output "
						+ "step is greater than the number of molecules in the system. This may or may not be true for your simulation, "
						+ "so you should try turning on or off this option to see which is more efficient."
						+ "</html>");
			} else if (source == moleculeDistanceHelpButton) {
				DialogUtils.showInfoDialog(NFSimSimulationOptionsPanel.this, "Distance of neighboring molecules from the site of the reaction", 
						"<html>-utl <i>int</i> " +
						"<br> The universal traversal limit (UTL) sets the distance neighboring molecules have to be to the site of the reaction "
						+ "to be updated. The default UTL is set to the size of the largest reactant pattern, which is guaranteed to produce correct "
						+ "results because NFsim will always find the changes that apply to every reactant pattern in the system." 
						+ "<br> Sometimes however, based on the structure of the reactant patterns, the UTL may be set lower. The lower is the UTL, "
						+ "the less molecules will be checked and the faster simulation will go. If the limit is too low, not all molecules are "
						+ "correctly being updated, then results will be incorrect. "
						+ "<br> In many cases, however you may have a very large pattern, but the maximal number of bonds you need to traverse to "
						+ "make sure that pattern can always be matched is low. This will happen, for instance, when many molecules are connected "
						+ "to a single hub molecule. "
						+ "<br><br> NFSim option –utl [integer] Default: the size of the largest reactant pattern in the rule-set."
						+ "</html>");
			} else if (source == aggregateBookkeepingHelpButton) {
				DialogUtils.showInfoDialog(NFSimSimulationOptionsPanel.this, "Turn on aggregate bookkeeping", 
						"<html>-cb <i>boolean</i> "
						+ "<br> NFsim by default tracks individual molecule agents, not complete molecular complexes. This is useful and makes "
						+ "simulations very fast, but is not always appropriate. For example, in some systems it is necessary to block "
						+ "intra-molecular bonds from occurring to prevent unwanted ring formation. However, to check for intra-molecular "
						+ "bonding events, complete molecular complexes must be traversed. NFsim, however, provides an aggregate bookkeeping "
						+ "system for molecular complexes that form by assigning each connected aggregate a unique id. Then, it becomes easy "
						+ "to check if any two molecules are connected. The trade-off is that there is an overhead involved with maintaining "
						+ "the bookkeeping system with a cost that depends on the size of the molecular complexes that can form."
						+ "<br><br> NFSim option –cb Default: off"
						+ "</html>");
			} else if (source == maxMoleculesPerTypeHelpButton) {
				DialogUtils.showInfoDialog(NFSimSimulationOptionsPanel.this, "Maximal number of molecules per Molecular Type", 
						"<html>-gml <i>int</i> "
						+ "<br> To prevent your computer from running out of memory in case you accidentally create too many molecules, "
						+ "NFsim sets a default limit of 100,000 molecules of any particular Molecule Type from being created. If the limit "
						+ "is exceeded, NFsim just stops running gracefully, thereby potentially saving your computer."
						+ "<br><br> NFSim option: –gml [limit] Default: 100,000"
						+ "</html>");
			} else if (source == equilibrateTimeHelpButton) {
				DialogUtils.showInfoDialog(NFSimSimulationOptionsPanel.this, "Equilibrate for a set time", 
						"<html>-eq <i>time</i> "
						+ "<br>Equilibrate the system for a set time before the simulation begins for the amount of time given. This operates "
						+ "exactly like a normal simulation, except that the simulation time is set to zero immediately after the equilibration "
						+ "phase and no output during equilibration is generated."
						+ "</html>");
			} else 	if (source == randomSeedHelpButton) {
				DialogUtils.showInfoDialog(NFSimSimulationOptionsPanel.this, "Random Seed", 
						"<html>rand_seed <i>boolean</i> "
						+ "<br>Provide a seed to NFsim’s random number generator so exact trajectories can be reproduced. If this line is not "
						+ "entered, the current time is used as a seed, producing different sequences for each run."
						+ "</html>");
			} else if (source == preventIntraBondsHelpButton) {
				DialogUtils.showInfoDialog(NFSimSimulationOptionsPanel.this, "Prevent intra-molecular bonds from forming", 
						"<html>-bscb <i>int</i> "
						+ "<br> Block same complex binding throughout the entire system. This prevents intra-molecular bonds from forming, "
						+ "but requires complex bookkeeping to be turned on."
						+ "</html>");
			} else if (source == gHelpButton) {
				DialogUtils.showInfoDialog(NFSimSimulationOptionsPanel.this, "Test", 
						"<html>test <i>int</i> "
						+ "<br> Test"
						+ "</html>");

// ----------------------------------------------------------------------------------------------------------
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
		
		randomSeedHelpButton = new JButton(" ? ");
		Font font = randomSeedHelpButton.getFont().deriveFont(Font.BOLD);
		Border border = BorderFactory.createEmptyBorder(1, 1, 1, 1);

		observableComputationCheckBox = new JCheckBox("Turn off on-the-fly computations of observables.");
		observableComputationTextField = new JTextField();
		observableComputationHelpButton = new JButton(" ? ");
		observableComputationHelpButton.setFont(font);
		observableComputationHelpButton.setBorder(border);
		
		moleculeDistanceCheckBox = new JCheckBox("Distance of neighboring molecules from the site of the reaction.");
		moleculeDistanceTextField = new JTextField();
		moleculeDistanceHelpButton = new JButton(" ? ");
		moleculeDistanceHelpButton.setFont(font);
		moleculeDistanceHelpButton.setBorder(border);
		
		aggregateBookkeepingCheckBox = new JCheckBox("Turn on aggregate bookkeeping.");
		aggregateBookkeepingTextField = new JTextField();
		aggregateBookkeepingHelpButton = new JButton(" ? ");
		aggregateBookkeepingHelpButton.setFont(font);
		aggregateBookkeepingHelpButton.setBorder(border);
		
		maxMoleculesPerTypeCheckBox = new JCheckBox("Maximal number of molecules per Molecular Type.");
		maxMoleculesPerTypeTextField = new JTextField();
		maxMoleculesPerTypeHelpButton = new JButton(" ? ");
		maxMoleculesPerTypeHelpButton.setFont(font);
		maxMoleculesPerTypeHelpButton.setBorder(border);
		
		equilibrateTimeCheckBox = new JCheckBox("Equilibrate for a set time.");
		equilibrateTimeTextField = new JTextField();
		equilibrateTimeHelpButton = new JButton(" ? ");
		equilibrateTimeHelpButton.setFont(font);
		equilibrateTimeHelpButton.setBorder(border);
		
		randomSeedCheckBox = new JCheckBox("Random Seed.");
		randomSeedTextField = new JTextField();
		randomSeedHelpButton.setFont(font);
		randomSeedHelpButton.setBorder(border);
		
		preventIntraBondsCheckBox = new JCheckBox("Prevent intra-molecular bonds from forming.");
		preventIntraBondsTextField = new JTextField();
		preventIntraBondsHelpButton = new JButton(" ? ");
		preventIntraBondsHelpButton.setFont(font);
		preventIntraBondsHelpButton.setBorder(border);
		
		gCheckBox = new JCheckBox("Test.");
		gTextField = new JTextField();
		gHelpButton = new JButton(" ? ");
		gHelpButton.setFont(font);
		gHelpButton.setBorder(border);
		
		getContentPanel().setLayout(new GridBagLayout());		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		
		// ----------------------------------------------------------------		
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(observableComputationCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 4);
		getContentPanel().add(observableComputationHelpButton, gbc);
				
//		gbc = new GridBagConstraints();
//		gbc.gridx = 2;
//		gbc.gridy = gridy;
//		gbc.weightx = 1;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.anchor = GridBagConstraints.LINE_START;
//		getContentPanel().add(aTextField, gbc);

		// ----------------------------------------------------------------		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(moleculeDistanceCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 4);
		getContentPanel().add(moleculeDistanceHelpButton, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(moleculeDistanceTextField, gbc);

		// ----------------------------------------------------------------		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(aggregateBookkeepingCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 4);
		getContentPanel().add(aggregateBookkeepingHelpButton, gbc);
				
//		gbc = new GridBagConstraints();
//		gbc.gridx = 2;
//		gbc.gridy = gridy;
//		gbc.weightx = 1;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.anchor = GridBagConstraints.LINE_START;
//		getContentPanel().add(cTextField, gbc);

		// ----------------------------------------------------------------		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(maxMoleculesPerTypeCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 4);
		getContentPanel().add(maxMoleculesPerTypeHelpButton, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(maxMoleculesPerTypeTextField, gbc);
		
		// ----------------------------------------------------------------		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(equilibrateTimeCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 4);
		getContentPanel().add(equilibrateTimeHelpButton, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(equilibrateTimeTextField, gbc);

		// ----------------------------------------------------------------
		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
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

		// --------------------------------------------------------------------------
		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(preventIntraBondsCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 4);
		getContentPanel().add(preventIntraBondsHelpButton, gbc);
				
//		gbc = new GridBagConstraints();
//		gbc.gridx = 2;
//		gbc.gridy = gridy;
//		gbc.weightx = 1;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.anchor = GridBagConstraints.LINE_START;
//		getContentPanel().add(fTextField, gbc);

		// ----------------------------------------------------------------		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(gCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 4);
		getContentPanel().add(gHelpButton, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(gTextField, gbc);

	}
	
	private void initConnections() {
		observableComputationCheckBox.addActionListener(ivjEventHandler);
		moleculeDistanceCheckBox.addActionListener(ivjEventHandler);
		aggregateBookkeepingCheckBox.addActionListener(ivjEventHandler);
		maxMoleculesPerTypeCheckBox.addActionListener(ivjEventHandler);
		equilibrateTimeCheckBox.addActionListener(ivjEventHandler);
		randomSeedCheckBox.addActionListener(ivjEventHandler);
		preventIntraBondsCheckBox.addActionListener(ivjEventHandler);
		gCheckBox.addActionListener(ivjEventHandler);

		observableComputationTextField.addFocusListener(ivjEventHandler);
		moleculeDistanceTextField.addFocusListener(ivjEventHandler);
		aggregateBookkeepingTextField.addFocusListener(ivjEventHandler);
		maxMoleculesPerTypeTextField.addFocusListener(ivjEventHandler);
		equilibrateTimeTextField.addFocusListener(ivjEventHandler);
		randomSeedTextField.addFocusListener(ivjEventHandler);
		preventIntraBondsTextField.addFocusListener(ivjEventHandler);
		gTextField.addFocusListener(ivjEventHandler);

		observableComputationHelpButton.addActionListener(ivjEventHandler);
		moleculeDistanceHelpButton.addActionListener(ivjEventHandler);
		aggregateBookkeepingHelpButton.addActionListener(ivjEventHandler);
		maxMoleculesPerTypeHelpButton.addActionListener(ivjEventHandler);
		equilibrateTimeHelpButton.addActionListener(ivjEventHandler);
		randomSeedHelpButton.addActionListener(ivjEventHandler);
		preventIntraBondsHelpButton.addActionListener(ivjEventHandler);
		gHelpButton.addActionListener(ivjEventHandler);
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
		
		boolean notf = false;
		if (notf == false) {
			observableComputationCheckBox.setSelected(false);
		} else {			
			observableComputationCheckBox.setSelected(true);
		}
		Integer utl = 5;
		if (utl == null) {
			moleculeDistanceTextField.setEditable(false);
			moleculeDistanceCheckBox.setSelected(false);
		} else {			
			moleculeDistanceTextField.setEditable(true);
			moleculeDistanceCheckBox.setSelected(true);
			moleculeDistanceTextField.setText(""+utl);
		}
		boolean cb = false;
		if (cb == false) {
			aggregateBookkeepingCheckBox.setSelected(false);
		} else {			
			aggregateBookkeepingCheckBox.setSelected(true);
		}
		Integer gml = 100000;
		if (gml == null) {
			maxMoleculesPerTypeTextField.setEditable(false);
			maxMoleculesPerTypeCheckBox.setSelected(false);
		} else {			
			maxMoleculesPerTypeTextField.setEditable(true);
			maxMoleculesPerTypeCheckBox.setSelected(true);
			maxMoleculesPerTypeTextField.setText(""+gml);
		}
		
		
		Integer eq = 100;
		if (eq == null) {
			equilibrateTimeTextField.setEditable(false);
			equilibrateTimeCheckBox.setSelected(false);
		} else {			
			equilibrateTimeTextField.setEditable(true);
			equilibrateTimeCheckBox.setSelected(true);
			equilibrateTimeTextField.setText(""+eq);
		}
		
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

		boolean bscb = false;
		if (bscb == false) {
			preventIntraBondsCheckBox.setSelected(false);
		} else {			
			preventIntraBondsCheckBox.setSelected(true);
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
		solverTaskDescription.getNFSimSimulationOptions().setRandomSeed(randomSeed);		
	}
}

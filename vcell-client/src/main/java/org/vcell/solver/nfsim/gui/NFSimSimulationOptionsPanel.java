/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.solver.nfsim.gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

	private JCheckBox matchComplexesCheckBox;
	private JTextField matchComplexesTextField;
	private JButton matchComplexesHelpButton = null;

	private IvjEventHandler ivjEventHandler = new IvjEventHandler();

	private class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, PropertyChangeListener {

		@SuppressWarnings("deprecation")
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == observableComputationHelpButton) {
				DialogUtils.showInfoDialogAndResize(NFSimSimulationOptionsPanel.this, "Compute observables at output times only", 
						"<html><b>NFSim option</b> -notf" +
						"<br> By default, observables are calculated on-the-fly, updating all observables at each simulation step. "
						+ "This is necessary when rates of reactions depend on Observables. "
						+ "<b>By checking this box, the values of Observables will be computed only at output steps.</b>"
						+ "It <b>can not be set if functions are used</b>, as functions rely on having updated Observables at any point in a simulation."
						+ "This will allow simulations to <b>run faster</b> if the number of simulation steps between each output "
						+ "step is greater than the number of molecules in the system. This may or may not be true for your simulation, "
						+ "so you should try turning on or off this option to see which is more efficient."
						+ "</html>");
			} else if (source == moleculeDistanceHelpButton) {
				DialogUtils.showInfoDialogAndResize(NFSimSimulationOptionsPanel.this, "Distance of neighboring molecules from the site of the reaction", 
						"<html><b>NFSim option</b> -utl <i>int</i> " +
						"<br> The universal traversal limit (UTL) sets the distance neighboring molecules have to be to the site of the reaction "
						+ "to be updated. The default UTL is set to the size of the largest reactant pattern, which is guaranteed to produce correct "
						+ "results because NFsim will always find the changes that apply to every reactant pattern in the system." 
						+ "<br> Sometimes however, based on the structure of the reactant patterns, the UTL may be set lower. The lower is the UTL, "
						+ "the less molecules will be checked and the faster simulation will go. If the limit is too low, not all molecules are "
						+ "correctly being updated, then results will be incorrect. "
						+ "<br> In many cases, however you may have a very large pattern, but the maximal number of bonds you need to traverse to "
						+ "make sure that pattern can always be matched is low. This will happen, for instance, when many molecules are connected "
						+ "to a single hub molecule. "
						+ "<br><br> NFSim option -utl [integer] Default: the size of the largest reactant pattern in the rule-set."
						+ "</html>");
			} else if (source == aggregateBookkeepingHelpButton) {
				DialogUtils.showInfoDialogAndResize(NFSimSimulationOptionsPanel.this, "Turn on aggregate bookkeeping", 
						"<html><b>NFSim option</b> -cb "
						+ "<br> NFsim by default tracks individual molecule agents, not complete molecular complexes. This is useful and makes "
						+ "simulations very fast, but is not always appropriate. For example, in some systems it is necessary to block "
						+ "intra-molecular bonds from occurring to prevent unwanted ring formation. However, to check for intra-molecular "
						+ "bonding events, complete molecular complexes must be traversed. NFsim, provides an aggregate bookkeeping "
						+ "system for molecular complexes that form by assigning each connected aggregate a unique id. Then, it becomes easy "
						+ "to check if any two molecules are connected. The trade-off is that there is an overhead involved with maintaining "
						+ "the bookkeeping system with a cost that depends on the size of the molecular complexes that can form."
						+ "<br><br> NFSim option -cb Default: off"
						+ "</html>");
			} else if (source == maxMoleculesPerTypeHelpButton) {
				DialogUtils.showInfoDialogAndResize(NFSimSimulationOptionsPanel.this, "Maximal number of molecules per Molecular Type", 
						"<html><b>NFSim option</b> -gml <i>int</i> "
						+ "<br> To prevent your computer from running out of memory in case you accidentally create too many molecules, "
						+ "NFsim sets a default limit of 100,000 molecules of any particular Molecule Type from being created. If the limit "
						+ "is exceeded, NFsim just stops running gracefully, thereby potentially saving your computer."
						+ "<br><br> NFSim option: -gml [limit] Default: 100,000"
						+ "</html>");
			} else if (source == equilibrateTimeHelpButton) {
				DialogUtils.showInfoDialogAndResize(NFSimSimulationOptionsPanel.this, "Equilibrate for a set time", 
						"<html><b>NFSim option</b> -eq <i>time</i> "
						+ "<br>Equilibrate the system for a set time before the simulation begins for the amount of time given. This operates "
						+ "exactly like a normal simulation, except that the simulation time is set to zero immediately after the equilibration "
						+ "phase and no output during equilibration is generated."
						+ "</html>");
			} else 	if (source == randomSeedHelpButton) {
				DialogUtils.showInfoDialogAndResize(NFSimSimulationOptionsPanel.this, "Set specific seed", 
						"<html><b>NFSim option</b> -seed <i>integer</i> "
						+ "<br>Provide a seed to NFsim's random number generator so exact trajectories can be reproduced. If this line is not "
						+ "entered, the current time is used as a seed, producing different sequences for each run."
						+ "</html>");
			} else if (source == preventIntraBondsHelpButton) {
				DialogUtils.showInfoDialogAndResize(NFSimSimulationOptionsPanel.this, "Prevent intra-molecular bonds from forming", 
						"<html><b>NFSim option</b> -bscb"
						+ "<br> Block same complex binding throughout the entire system. This prevents intra-molecular bonds from forming, "
						+ "but requires complex bookkeeping to be turned on."
						+ "</html>");
			} else if (source == matchComplexesHelpButton) {
				DialogUtils.showInfoDialogAndResize(NFSimSimulationOptionsPanel.this, "Match complexes with product patterns", 
						"<html><b>NFSim option</b> -pcmatch"
						+ "<br> Match generated complexes to the rule product patterns, "
						+ "could detect impossible combinations."
						+ "</html>");

// ----------------------------------------------------------------------------------------------------------
			} else if (source == observableComputationCheckBox) {
				setNewObservableComputation();
			} else if (source == moleculeDistanceCheckBox) {
				moleculeDistanceTextField.setEditable(moleculeDistanceCheckBox.isSelected());
				if(!moleculeDistanceCheckBox.isSelected()) {
					setNewMoleculeDistance();
				}
			} else if (source == aggregateBookkeepingCheckBox) {
				setNewAggregateBookkeeping();
			} else if (source == maxMoleculesPerTypeCheckBox) {
				maxMoleculesPerTypeTextField.setEditable(maxMoleculesPerTypeCheckBox.isSelected());
				if(maxMoleculesPerTypeCheckBox.isSelected()) {
					Integer rs = solverTaskDescription.getNFSimSimulationOptions().getMaxMoleculesPerType();
					if(rs == null) {
						rs = new Integer(NFsimSimulationOptions.DefaultMaxMoleculesPerType);
						solverTaskDescription.getNFSimSimulationOptions().setMaxMoleculesPerType(rs);
					}
					maxMoleculesPerTypeTextField.setText(rs.toString());
				} else {
					solverTaskDescription.getNFSimSimulationOptions().setMaxMoleculesPerType(null);
					maxMoleculesPerTypeTextField.setText("");
				}
			} else if (source == equilibrateTimeCheckBox) {
				equilibrateTimeTextField.setEditable(equilibrateTimeCheckBox.isSelected());
				if(!equilibrateTimeCheckBox.isSelected()) {
					setNewEquilibrateTime();
				}
			} else if (source == randomSeedCheckBox) {
				randomSeedTextField.setEditable(randomSeedCheckBox.isSelected());
				if(randomSeedCheckBox.isSelected()) {
					
					Integer rs = solverTaskDescription.getNFSimSimulationOptions().getRandomSeed();
					if(rs == null) {
						rs = new Integer(NFsimSimulationOptions.DefaultRandomSeed);
						solverTaskDescription.getNFSimSimulationOptions().setRandomSeed(rs);
					}
					randomSeedTextField.setText(rs.toString());
				} else {
					solverTaskDescription.getNFSimSimulationOptions().setRandomSeed(null);
					randomSeedTextField.setText("");
				}
			} else if (source == preventIntraBondsCheckBox) {
				setNewPreventIntraBonds();
			} else if (source == matchComplexesCheckBox) {
				setNewMatchComplexes();
			}
		}
		public void focusGained(FocusEvent e) {
		}

		public void focusLost(FocusEvent e) {
			if (e.isTemporary()) {
				return;
			}
			if (e.getSource() == moleculeDistanceTextField) {
				setNewMoleculeDistance();
			} else if (e.getSource() == maxMoleculesPerTypeTextField) {
				setNewMaxMoleculesPerType();
			} else if (e.getSource() == equilibrateTimeTextField) {
				setNewEquilibrateTime();
			} else if (e.getSource() == randomSeedTextField) {
				setNewRandomSeed();
			}
		}
		public void propertyChange(PropertyChangeEvent evt) {
		}
	}
	
	private void setNewObservableComputation() {		// boolean
		solverTaskDescription.getNFSimSimulationOptions().setObservableComputationOff(observableComputationCheckBox.isSelected());
	}
	private void setNewMoleculeDistance() {
		Integer moleculeDistance = null;
		if (moleculeDistanceCheckBox.isSelected()) {
			try {
				moleculeDistance = new Integer(moleculeDistanceTextField.getText());				
			} catch (NumberFormatException ex) {
				DialogUtils.showErrorDialog(this, "Wrong number format: " + ex.getMessage());
				return;
			}
		}
		solverTaskDescription.getNFSimSimulationOptions().setMoleculeDistance(moleculeDistance);
	}
	private void setNewAggregateBookkeeping() {			// boolean
		solverTaskDescription.getNFSimSimulationOptions().setAggregateBookkeeping(aggregateBookkeepingCheckBox.isSelected());
	}
	private void setNewMaxMoleculesPerType() {
		Integer maxMoleculesPerType = null;
		if (maxMoleculesPerTypeCheckBox.isSelected()) {
			try {
				maxMoleculesPerType = new Integer(maxMoleculesPerTypeTextField.getText());
			} catch (NumberFormatException ex) {
				Integer mmpt = solverTaskDescription.getNFSimSimulationOptions().getMaxMoleculesPerType();
				if(mmpt != null) {
					mmpt = new Integer(NFsimSimulationOptions.DefaultMaxMoleculesPerType);
					solverTaskDescription.getNFSimSimulationOptions().setMaxMoleculesPerType(mmpt);
				}
				maxMoleculesPerTypeTextField.setText(mmpt.toString());
				DialogUtils.showErrorDialog(this, "Wrong number format: " + ex.getMessage());
				return;
			}
		}
		solverTaskDescription.getNFSimSimulationOptions().setMaxMoleculesPerType(maxMoleculesPerType);
	}
	private void setNewEquilibrateTime() {
		Integer equilibrateTime = null;
		if (equilibrateTimeCheckBox.isSelected()) {
			try {
				equilibrateTime = new Integer(equilibrateTimeTextField.getText());				
			} catch (NumberFormatException ex) {
				DialogUtils.showErrorDialog(this, "Wrong number format: " + ex.getMessage());
				return;
			}
		}
		solverTaskDescription.getNFSimSimulationOptions().setEquilibrateTime(equilibrateTime);
	}
	private void setNewRandomSeed() {
		Integer randomSeed = null;
		if (randomSeedCheckBox.isSelected()) {
			try {
				randomSeed = new Integer(randomSeedTextField.getText());
			} catch (NumberFormatException ex) {
				Integer rs = solverTaskDescription.getNFSimSimulationOptions().getRandomSeed();
				if(rs == null) {
					rs = new Integer(NFsimSimulationOptions.DefaultRandomSeed);
					solverTaskDescription.getNFSimSimulationOptions().setRandomSeed(rs);
				}
				randomSeedTextField.setText(rs.toString());
				DialogUtils.showErrorDialog(this, "Wrong number format: " + ex.getMessage());
				return;
			}
		}
		solverTaskDescription.getNFSimSimulationOptions().setRandomSeed(randomSeed);		
	}
	private void setNewPreventIntraBonds() {		// boolean
		solverTaskDescription.getNFSimSimulationOptions().setPreventIntraBonds(preventIntraBondsCheckBox.isSelected());
	}
	private void setNewMatchComplexes() {			// boolean
		solverTaskDescription.getNFSimSimulationOptions().setMatchComplexes(matchComplexesCheckBox.isSelected());
	}

	
// ----------------------------------------------------------------------------------------------------------------------------	
	public NFSimSimulationOptionsPanel() {
		super("Advanced Solver Options", false);
		initialize();
	}
	private void initialize() {
		
		randomSeedHelpButton = new JButton(" ? ");
		Font font = randomSeedHelpButton.getFont().deriveFont(Font.BOLD);
		Border border = BorderFactory.createEmptyBorder(1, 1, 1, 1);

		observableComputationCheckBox = new JCheckBox("Compute observables at output times only.");
		observableComputationTextField = new JTextField();
		observableComputationHelpButton = new JButton(" ? ");
		observableComputationHelpButton.setFont(font);
		observableComputationHelpButton.setBorder(border);
		
		moleculeDistanceCheckBox = new JCheckBox("Set distance to molecules that may be updated:");
		moleculeDistanceTextField = new JTextField(NFsimSimulationOptions.DefaultDistanceToMolecules+"");
		moleculeDistanceHelpButton = new JButton(" ? ");
		moleculeDistanceHelpButton.setFont(font);
		moleculeDistanceHelpButton.setBorder(border);
		
		aggregateBookkeepingCheckBox = new JCheckBox("Turn on aggregate bookkeeping.");
		aggregateBookkeepingTextField = new JTextField();
		aggregateBookkeepingHelpButton = new JButton(" ? ");
		aggregateBookkeepingHelpButton.setFont(font);
		aggregateBookkeepingHelpButton.setBorder(border);
		
		maxMoleculesPerTypeCheckBox = new JCheckBox("Set the max. number of Molecules per Type.");
		maxMoleculesPerTypeTextField = new JTextField();
		maxMoleculesPerTypeHelpButton = new JButton(" ? ");
		maxMoleculesPerTypeHelpButton.setFont(font);
		maxMoleculesPerTypeHelpButton.setBorder(border);
		
		equilibrateTimeCheckBox = new JCheckBox("Equilibrate for a set time.");
		equilibrateTimeTextField = new JTextField();
		equilibrateTimeHelpButton = new JButton(" ? ");
		equilibrateTimeHelpButton.setFont(font);
		equilibrateTimeHelpButton.setBorder(border);
		
		randomSeedCheckBox = new JCheckBox("Set a seed to NFsim random number generator.");
		randomSeedTextField = new JTextField();
		randomSeedHelpButton.setFont(font);
		randomSeedHelpButton.setBorder(border);
		
		preventIntraBondsCheckBox = new JCheckBox("Prevent intra-molecular bonds from forming.");
		preventIntraBondsTextField = new JTextField();
		preventIntraBondsHelpButton = new JButton(" ? ");
		preventIntraBondsHelpButton.setFont(font);
		preventIntraBondsHelpButton.setBorder(border);
		
		matchComplexesCheckBox = new JCheckBox("Match generated complexes to products.");
		matchComplexesTextField = new JTextField();
		matchComplexesHelpButton = new JButton(" ? ");
		matchComplexesHelpButton.setFont(font);
		matchComplexesHelpButton.setBorder(border);

		getContentPanel().setLayout(new GridBagLayout());		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		
		// ----------------------------------------------------------------		
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(4, 0, 0, 0);
		getContentPanel().add(observableComputationCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(4, 0, 0, 8);
		getContentPanel().add(observableComputationHelpButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(4, 4, 0, 0);
		getContentPanel().add(aggregateBookkeepingCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4, 0, 0, 6);
		getContentPanel().add(aggregateBookkeepingHelpButton, gbc);

		// --------------------------------------------------------------------------
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 6, 0);
		getContentPanel().add(preventIntraBondsCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 6, 8);
		getContentPanel().add(preventIntraBondsHelpButton, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 4, 6, 0);
		getContentPanel().add(matchComplexesCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(0, 0, 6, 6);
		getContentPanel().add(matchComplexesHelpButton, gbc);

		// ----------------------------------------------------------------		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(2, 0, 2, 0);
		getContentPanel().add(moleculeDistanceCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(2, 0, 2, 6);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(moleculeDistanceTextField, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(2, 0, 2, 6);
		getContentPanel().add(moleculeDistanceHelpButton, gbc);
				
		// ----------------------------------------------------------------		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 2, 0);
		getContentPanel().add(maxMoleculesPerTypeCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 0, 2, 6);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(maxMoleculesPerTypeTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(0, 0, 2, 6);
		getContentPanel().add(maxMoleculesPerTypeHelpButton, gbc);
				
		// ----------------------------------------------------------------
		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 6, 0);
		getContentPanel().add(randomSeedCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 0, 6, 6);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(randomSeedTextField, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(0, 0, 6, 6);
		getContentPanel().add(randomSeedHelpButton, gbc);
		
		// --------------------------------------------------------------------------
		
//		gridy++;
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = gridy;
//		gbc.anchor = GridBagConstraints.LINE_START;
//		getContentPanel().add(preventIntraBondsCheckBox, gbc);
//
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = gridy;
//		gbc.anchor = GridBagConstraints.LINE_START;
//		gbc.insets = new Insets(0, 0, 0, 4);
//		getContentPanel().add(preventIntraBondsHelpButton, gbc);
//				
////		gridy++;
//		gbc = new GridBagConstraints();
//		gbc.gridx = 3;
//		gbc.gridy = gridy;
//		gbc.anchor = GridBagConstraints.LINE_START;
//		getContentPanel().add(matchComplexesCheckBox, gbc);
//
//		gbc = new GridBagConstraints();
//		gbc.gridx = 4;
//		gbc.gridy = gridy;
//		gbc.anchor = GridBagConstraints.LINE_END;
//		gbc.insets = new Insets(0, 0, 0, 4);
//		getContentPanel().add(matchComplexesHelpButton, gbc);
				
	}
	
	private void initConnections() {
		observableComputationCheckBox.addActionListener(ivjEventHandler);
		moleculeDistanceCheckBox.addActionListener(ivjEventHandler);
		aggregateBookkeepingCheckBox.addActionListener(ivjEventHandler);
		maxMoleculesPerTypeCheckBox.addActionListener(ivjEventHandler);
		equilibrateTimeCheckBox.addActionListener(ivjEventHandler);
		randomSeedCheckBox.addActionListener(ivjEventHandler);
		preventIntraBondsCheckBox.addActionListener(ivjEventHandler);
		matchComplexesCheckBox.addActionListener(ivjEventHandler);

		observableComputationTextField.addFocusListener(ivjEventHandler);
		moleculeDistanceTextField.addFocusListener(ivjEventHandler);
		aggregateBookkeepingTextField.addFocusListener(ivjEventHandler);
		maxMoleculesPerTypeTextField.addFocusListener(ivjEventHandler);
		equilibrateTimeTextField.addFocusListener(ivjEventHandler);
		randomSeedTextField.addFocusListener(ivjEventHandler);
		preventIntraBondsTextField.addFocusListener(ivjEventHandler);
		matchComplexesTextField.addFocusListener(ivjEventHandler);

		observableComputationHelpButton.addActionListener(ivjEventHandler);
		moleculeDistanceHelpButton.addActionListener(ivjEventHandler);
		aggregateBookkeepingHelpButton.addActionListener(ivjEventHandler);
		maxMoleculesPerTypeHelpButton.addActionListener(ivjEventHandler);
		equilibrateTimeHelpButton.addActionListener(ivjEventHandler);
		randomSeedHelpButton.addActionListener(ivjEventHandler);
		preventIntraBondsHelpButton.addActionListener(ivjEventHandler);
		matchComplexesHelpButton.addActionListener(ivjEventHandler);
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
			if(mathDescription.isLangevin()) {
				setVisible(false);
				return;
			}
		}
		setVisible(true);
		NFsimSimulationOptions nfsimSimulationOptions = solverTaskDescription.getNFSimSimulationOptions();
		
		boolean notf = nfsimSimulationOptions.getObservableComputationOff();
		if (notf == false) {
			observableComputationCheckBox.setSelected(true);	// hardcoded, put it back here to false
			observableComputationCheckBox.setEnabled(false);
			nfsimSimulationOptions.setObservableComputationOff(observableComputationCheckBox.isSelected());
		} else {			
			observableComputationCheckBox.setSelected(true);
			observableComputationCheckBox.setEnabled(false);
			nfsimSimulationOptions.setObservableComputationOff(observableComputationCheckBox.isSelected());
		}
		Integer utl = nfsimSimulationOptions.getMoleculeDistance();
		if (utl == null) {
			// we always force this parameter, even for legacy simulations where it was optional
			// the default value is DefaultDistanceToMolecules but the user may still choose something else
			moleculeDistanceTextField.setEditable(true);
			moleculeDistanceTextField.setText(NFsimSimulationOptions.DefaultDistanceToMolecules+"");
			moleculeDistanceCheckBox.setSelected(true);
			moleculeDistanceCheckBox.setEnabled(false);
		} else {			
			moleculeDistanceTextField.setEditable(true);
			moleculeDistanceTextField.setText(""+utl);
			moleculeDistanceCheckBox.setSelected(true);
			moleculeDistanceCheckBox.setEnabled(false);
		}
		boolean cb = nfsimSimulationOptions.getAggregateBookkeeping();
		if (cb == false) {
			aggregateBookkeepingCheckBox.setSelected(true);		// hardcoded, put it back here to false
			aggregateBookkeepingCheckBox.setEnabled(false);
			nfsimSimulationOptions.setAggregateBookkeeping(aggregateBookkeepingCheckBox.isSelected());
		} else {			
			aggregateBookkeepingCheckBox.setSelected(true);
			aggregateBookkeepingCheckBox.setEnabled(false);
			nfsimSimulationOptions.setAggregateBookkeeping(aggregateBookkeepingCheckBox.isSelected());
		}
		Integer gml = nfsimSimulationOptions.getMaxMoleculesPerType();
		if (gml == null) {
			maxMoleculesPerTypeTextField.setEditable(false);
			maxMoleculesPerTypeCheckBox.setSelected(false);
		} else {			
			maxMoleculesPerTypeTextField.setEditable(true);
			maxMoleculesPerTypeCheckBox.setSelected(true);
			maxMoleculesPerTypeTextField.setText(""+gml);
		}
		Integer eq = nfsimSimulationOptions.getEquilibrateTime();
		if (eq == null) {
			equilibrateTimeTextField.setEditable(false);
			equilibrateTimeCheckBox.setSelected(false);
		} else {			
			equilibrateTimeTextField.setEditable(true);
			equilibrateTimeCheckBox.setSelected(true);
			equilibrateTimeTextField.setText(""+eq);
		}
		Integer randomSeed = nfsimSimulationOptions.getRandomSeed();
		if (randomSeed == null) {
			randomSeedTextField.setEditable(false);
			randomSeedCheckBox.setSelected(false);
		} else {			
			randomSeedTextField.setEditable(true);
			randomSeedCheckBox.setSelected(true);
			randomSeedTextField.setText(randomSeed.toString());
		}
		boolean bscb = nfsimSimulationOptions.getPreventIntraBonds();
		if (bscb == false) {
			preventIntraBondsCheckBox.setSelected(false);
		} else {			
			preventIntraBondsCheckBox.setSelected(true);
		}
		boolean pcmatch = nfsimSimulationOptions.getMatchComplexes();
		if (pcmatch == false) {
			matchComplexesCheckBox.setSelected(false);
		} else {			
			matchComplexesCheckBox.setSelected(true);
		}
	}
}

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

	private SolverTaskDescription solverTaskDescription = null;	

	private JCheckBox randomSeedCheckBox;
	private JTextField randomSeedTextField;
	private JButton randomSeedHelpButton = null;

	private JCheckBox aCheckBox;
	private JTextField aTextField;
	private JButton aHelpButton = null;

	private JCheckBox bCheckBox;
	private JTextField bTextField;
	private JButton bHelpButton = null;

	private JCheckBox cCheckBox;
	private JTextField cTextField;
	private JButton cHelpButton = null;

	private JCheckBox dCheckBox;
	private JTextField dTextField;
	private JButton dHelpButton = null;

	private JCheckBox eCheckBox;
	private JTextField eTextField;
	private JButton eHelpButton = null;

	private JCheckBox fCheckBox;
	private JTextField fTextField;
	private JButton fHelpButton = null;

	private JCheckBox gCheckBox;
	private JTextField gTextField;
	private JButton gHelpButton = null;

	private JCheckBox hCheckBox;
	private JTextField hTextField;
	private JButton hHelpButton = null;

	private IvjEventHandler ivjEventHandler = new IvjEventHandler();

	private class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, PropertyChangeListener {

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
//			if (source == randomSeedHelpButton) {
//				DialogUtils.showInfoDialog(NFSimSimulationOptionsPanel.this, "Random Seed", "<html>rand_seed <i>boolean</i> " +
//						"<br>Seed for random number generator. If this line is not entered, the current " +
//						"time is used as a seed, producing different sequences for each run.</html>");
//			} else 
			if (source == aHelpButton) {
				DialogUtils.showInfoDialog(NFSimSimulationOptionsPanel.this, "On-the-fly computations of observables", 
						"<html>-notf <i>boolean</i> " +
						"<br> By default, observables are calculated on-the-fly, updating all observables at each simulation step. "
						+ "This is necessary when rates of reactions depend on Observables. Otherwise, the values of Observables need "
						+ "to be computed only at output steps. This is useful when the number of simulation steps between each output "
						+ "step is greater than the number of molecules in the system. This may or may not be true for your simulation, "
						+ "so you should try turning on or off this option to see which is more efficient."
						+ "</html>");
			} else if (source == bHelpButton) {
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
			} else if (source == cHelpButton) {
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
			} else if (source == dHelpButton) {
				DialogUtils.showInfoDialog(NFSimSimulationOptionsPanel.this, "Maximal number of molecules per Molecular Type", 
						"<html>-gml <i>int</i> "
						+ "<br> To prevent your computer from running out of memory in case you accidentally create too many molecules, "
						+ "NFsim sets a default limit of 100,000 molecules of any particular Molecule Type from being created. If the limit "
						+ "is exceeded, NFsim just stops running gracefully, thereby potentially saving your computer."
						+ "<br><br> NFSim option: –gml [limit] Default: 100,000"
						+ "</html>");
			} else if (source == eHelpButton) {
				DialogUtils.showInfoDialog(NFSimSimulationOptionsPanel.this, "Test", 
						"<html>test <i>int</i> "
						+ "<br> Test"
						+ "</html>");
			} else if (source == fHelpButton) {
				DialogUtils.showInfoDialog(NFSimSimulationOptionsPanel.this, "Test", 
						"<html>test <i>int</i> "
						+ "<br> Test"
						+ "</html>");
			} else if (source == gHelpButton) {
				DialogUtils.showInfoDialog(NFSimSimulationOptionsPanel.this, "Test", 
						"<html>test <i>int</i> "
						+ "<br> Test"
						+ "</html>");
			} else if (source == hHelpButton) {
				DialogUtils.showInfoDialog(NFSimSimulationOptionsPanel.this, "Test", 
						"<html>test <i>int</i> "
						+ "<br> Test"
						+ "</html>");
				
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

//		randomSeedCheckBox = new JCheckBox("-seed  Random Seed.");
//		randomSeedTextField = new JTextField();
//		randomSeedHelpButton.setFont(font);
//		randomSeedHelpButton.setBorder(border);
		
		aCheckBox = new JCheckBox("Turn off on-the-fly computations of observables.");
		aTextField = new JTextField();
		aHelpButton = new JButton(" ? ");
		aHelpButton.setFont(font);
		aHelpButton.setBorder(border);
		
		bCheckBox = new JCheckBox("Distance of neighboring molecules from the site of the reaction.");
		bTextField = new JTextField();
		bHelpButton = new JButton(" ? ");
		bHelpButton.setFont(font);
		bHelpButton.setBorder(border);
		
		cCheckBox = new JCheckBox("Turn on aggregate bookkeeping.");
		cTextField = new JTextField();
		cHelpButton = new JButton(" ? ");
		cHelpButton.setFont(font);
		cHelpButton.setBorder(border);
		
		dCheckBox = new JCheckBox("Maximal number of molecules per Molecular Type.");
		dTextField = new JTextField();
		dHelpButton = new JButton(" ? ");
		dHelpButton.setFont(font);
		dHelpButton.setBorder(border);
		
		eCheckBox = new JCheckBox("Test.");
		eTextField = new JTextField();
		eHelpButton = new JButton(" ? ");
		eHelpButton.setFont(font);
		eHelpButton.setBorder(border);
		
		fCheckBox = new JCheckBox("Test.");
		fTextField = new JTextField();
		fHelpButton = new JButton(" ? ");
		fHelpButton.setFont(font);
		fHelpButton.setBorder(border);
		
		gCheckBox = new JCheckBox("Test.");
		gTextField = new JTextField();
		gHelpButton = new JButton(" ? ");
		gHelpButton.setFont(font);
		gHelpButton.setBorder(border);
		
		hCheckBox = new JCheckBox("Test.");
		hTextField = new JTextField();
		hHelpButton = new JButton(" ? ");
		hHelpButton.setFont(font);
		hHelpButton.setBorder(border);
	
		getContentPanel().setLayout(new GridBagLayout());		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = gridy;
//		gbc.anchor = GridBagConstraints.LINE_START;
//		getContentPanel().add(randomSeedCheckBox, gbc);
//
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = gridy;
//		gbc.anchor = GridBagConstraints.LINE_START;
//		gbc.insets = new Insets(0, 0, 0, 4);
//		getContentPanel().add(randomSeedHelpButton, gbc);
//		
//		gbc = new GridBagConstraints();
//		gbc.gridx = 2;
//		gbc.gridy = gridy;
//		gbc.weightx = 1;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.anchor = GridBagConstraints.LINE_START;
//		getContentPanel().add(randomSeedTextField, gbc);
		
		// ----------------------------------------------------------------		
//		gridy++;
//		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(aCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 4);
		getContentPanel().add(aHelpButton, gbc);
				
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
		getContentPanel().add(bCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 4);
		getContentPanel().add(bHelpButton, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(bTextField, gbc);

		// ----------------------------------------------------------------		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(cCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 4);
		getContentPanel().add(cHelpButton, gbc);
				
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
		getContentPanel().add(dCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 4);
		getContentPanel().add(dHelpButton, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(dTextField, gbc);
		
		// ----------------------------------------------------------------		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(eCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 4);
		getContentPanel().add(eHelpButton, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(eTextField, gbc);

		// ----------------------------------------------------------------		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(fCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 4);
		getContentPanel().add(fHelpButton, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(fTextField, gbc);

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

		// ----------------------------------------------------------------		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(hCheckBox, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 4);
		getContentPanel().add(hHelpButton, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		getContentPanel().add(hTextField, gbc);
	}
	
	private void initConnections() {
//		randomSeedCheckBox.addActionListener(ivjEventHandler);
		aCheckBox.addActionListener(ivjEventHandler);
		bCheckBox.addActionListener(ivjEventHandler);
		cCheckBox.addActionListener(ivjEventHandler);
		dCheckBox.addActionListener(ivjEventHandler);
		eCheckBox.addActionListener(ivjEventHandler);
		fCheckBox.addActionListener(ivjEventHandler);
		gCheckBox.addActionListener(ivjEventHandler);
		hCheckBox.addActionListener(ivjEventHandler);

//		randomSeedTextField.addFocusListener(ivjEventHandler);
		aTextField.addFocusListener(ivjEventHandler);
		bTextField.addFocusListener(ivjEventHandler);
		cTextField.addFocusListener(ivjEventHandler);
		dTextField.addFocusListener(ivjEventHandler);
		eTextField.addFocusListener(ivjEventHandler);
		fTextField.addFocusListener(ivjEventHandler);
		gTextField.addFocusListener(ivjEventHandler);
		hTextField.addFocusListener(ivjEventHandler);

//		randomSeedHelpButton.addActionListener(ivjEventHandler);
		aHelpButton.addActionListener(ivjEventHandler);
		bHelpButton.addActionListener(ivjEventHandler);
		cHelpButton.addActionListener(ivjEventHandler);
		dHelpButton.addActionListener(ivjEventHandler);
		eHelpButton.addActionListener(ivjEventHandler);
		fHelpButton.addActionListener(ivjEventHandler);
		gHelpButton.addActionListener(ivjEventHandler);
		hHelpButton.addActionListener(ivjEventHandler);
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
//		if (randomSeed == null) {
//			randomSeedTextField.setEditable(false);
//			randomSeedCheckBox.setSelected(false);
//		} else {			
//			randomSeedTextField.setEditable(true);
//			randomSeedCheckBox.setSelected(true);
//			randomSeedTextField.setText(randomSeed.toString());
//		}
		
		boolean notf = false;
		if (notf == false) {
			aCheckBox.setSelected(false);
		} else {			
			aCheckBox.setSelected(true);
		}
		Integer utl = 5;
		if (utl == null) {
			bTextField.setEditable(false);
			bCheckBox.setSelected(false);
		} else {			
			bTextField.setEditable(true);
			bCheckBox.setSelected(true);
			bTextField.setText(""+utl);
		}
		boolean cb = false;
		if (cb == false) {
			cCheckBox.setSelected(false);
		} else {			
			cCheckBox.setSelected(true);
		}
		Integer gml = 100000;
		if (gml == null) {
			dTextField.setEditable(false);
			dCheckBox.setSelected(false);
		} else {			
			dTextField.setEditable(true);
			dCheckBox.setSelected(true);
			dTextField.setText(""+gml);
		}
//		boolean x = false;
//		if (x == false) {
//			randomSeedTextField.setEditable(false);
//			randomSeedCheckBox.setSelected(false);
//		} else {			
//			randomSeedTextField.setEditable(true);
//			randomSeedCheckBox.setSelected(true);
//			randomSeedTextField.setText(randomSeed.toString());
//		}
	}
	
	private void setNewRandomSeed(){
		if(!isVisible()){
			return;
		}
		
//		Integer randomSeed = null;
//		if (randomSeedCheckBox.isSelected()) {
//			try {
//				randomSeed = new Integer(randomSeedTextField.getText());
//			} catch (NumberFormatException ex) {
//				DialogUtils.showErrorDialog(this, "Wrong number format for random seed: " + ex.getMessage());
//				return;
//			}
//		}
//		solverTaskDescription.getSmoldynSimulationOptions().setRandomSeed(randomSeed);		
	}
}

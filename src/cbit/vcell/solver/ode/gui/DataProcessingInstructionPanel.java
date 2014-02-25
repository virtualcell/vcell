/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.solver.DataProcessingInstructions;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.SolverDescription.SolverFeature;

/**
 * Insert the type's description here.
 * Creation date: (11/2/2000 4:46:30 PM)
 * @author: 
 */
public class DataProcessingInstructionPanel extends javax.swing.JPanel {
	private JCheckBox dataProcessorCheckBox = null;
	private JButton editDataProcessorButton = null;
	private SolverTaskDescription solverTaskDescription = null;

	private IvjEventHandler ivjEventHandler = new IvjEventHandler();

	class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == DataProcessingInstructionPanel.this && (evt.getPropertyName().equals("solverTaskDescription"))) { 
				refresh();
			}
			if (evt.getSource() == solverTaskDescription && evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_SOLVER_DESCRIPTION)) {
				refresh();
			}	
			
		}
		
		public void actionPerformed(java.awt.event.ActionEvent e) {			
			if (e.getSource() == dataProcessorCheckBox) {
				if (dataProcessorCheckBox.isSelected()) {
					editDataProcessor(false);
					if (solverTaskDescription.getSimulation().getDataProcessingInstructions() == null) {
						editDataProcessorButton.setEnabled(false);
						dataProcessorCheckBox.setSelected(false);
					} else {
						editDataProcessorButton.setEnabled(true);
					}
				} else {
					editDataProcessorButton.setEnabled(false);
					solverTaskDescription.getSimulation().setDataProcessingInstructions(null);
				}
			} else if (e.getSource() == editDataProcessorButton) {
				editDataProcessor(true);
				editDataProcessorButton.setEnabled(true);
			}
		}		
	}
	
/**
 * ErrorTolerancePanel constructor comment.
 */
public DataProcessingInstructionPanel() {
	super();
	this.addPropertyChangeListener(ivjEventHandler);
	initialize();
}

/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
private void editDataProcessor(boolean bEdit) {
	DataProcessingInstructions dpi = solverTaskDescription.getSimulation().getDataProcessingInstructions();
	
	JPanel mainPanel = new JPanel(new BorderLayout());
	
	JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JLabel nameLabel = new JLabel("Name");			
	panel.add(nameLabel);
	JComboBox nameComboBox = new JComboBox();
	nameComboBox.addItem(DataProcessingInstructions.ROI_TIME_SERIES);
	nameComboBox.addItem(DataProcessingInstructions.VFRAP);
	if (dpi != null) {
		nameComboBox.setSelectedItem(dpi.getScriptName());
	}
	panel.add(nameComboBox);
	mainPanel.add(panel, BorderLayout.NORTH);
	
	panel = new JPanel(new GridBagLayout());			
	JLabel label = new JLabel("Text");
	GridBagConstraints cbc = new GridBagConstraints();
	cbc.gridx = 0;
	cbc.gridy = 0;
	cbc.insets = new Insets(4,4,4,8);
	panel.add(label, cbc);
	
	JScrollPane sp = new JScrollPane();
	sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	JTextArea textArea = new JTextArea();
	if (dpi != null) {
		textArea.setText(dpi.getScriptInput());
	}
	textArea.setColumns(20);
	textArea.setRows(8);
	sp.setViewportView(textArea);			
	
	cbc = new GridBagConstraints();
	cbc.gridx = 1;
	cbc.gridy = 0;
	cbc.weightx = 1;
	cbc.weighty = 1;
	cbc.fill = GridBagConstraints.BOTH;
	panel.add(sp, cbc);			
	mainPanel.add(panel, BorderLayout.CENTER);
	
	int ok = DialogUtils.showComponentOKCancelDialog(this.getParent(), mainPanel, "Add Data Processor");	
	if (ok == JOptionPane.OK_OPTION && textArea.getText().length() > 0) {
		String name = (String) nameComboBox.getSelectedItem();
		solverTaskDescription.getSimulation().setDataProcessingInstructions(
				new DataProcessingInstructions(name, textArea.getText()));
	} else {
		if (!bEdit) {
			solverTaskDescription.getSimulation().setDataProcessingInstructions(null);
		}
	}
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
	DialogUtils.showWarningDialog(this, "Error in Tolerance value : " + exception.getMessage(), new String[] {"Ok"}, "Ok");
}

/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() {	
	dataProcessorCheckBox.addActionListener(ivjEventHandler);
	editDataProcessorButton.addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ErrorTolerancePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(160, 120);

		// 0
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 0;
		gbc.weightx = 0.2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		dataProcessorCheckBox = new JCheckBox("Data Processing Script");
		add(dataProcessorCheckBox, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = 0;
		gbc.insets = new java.awt.Insets(0, 0, 0, 0);
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		editDataProcessorButton = new JButton("Edit...");
		add(editDataProcessorButton, gbc);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Sets the errorTolerance property (cbit.vcell.solver.ErrorTolerance) value.
 * @param errorTolerance The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getErrorTolerance
 */
public void setSolverTaskDescription(SolverTaskDescription newValue) {
	SolverTaskDescription oldValue = solverTaskDescription;
	/* Stop listening for events from the current object */
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(ivjEventHandler);
	}
	solverTaskDescription = newValue;

	/* Listen for events from the new object */
	if (newValue != null) {
		newValue.addPropertyChangeListener(ivjEventHandler);
	}		
	solverTaskDescription = newValue;
	firePropertyChange("solverTaskDescription", oldValue, newValue);
	
	initConnections();
}

private void refresh() {
	if (solverTaskDescription == null) {
		return;
	}
	SolverDescription solverDesc = solverTaskDescription.getSolverDescription();
	
	if (solverDesc.supports(SolverFeature.Feature_DataProcessingInstructions)) {
		setVisible(true);
		DataProcessingInstructions dpi = solverTaskDescription.getSimulation().getDataProcessingInstructions();
		if (dpi != null) {
			dataProcessorCheckBox.setSelected(true);
			editDataProcessorButton.setEnabled(true);
		} else {
			editDataProcessorButton.setEnabled(false);
		}
	} else {
		setVisible(false);
	}
}

}

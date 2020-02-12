/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;

import org.vcell.util.document.PropertyConstants;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.desktop.BioModelCellRenderer;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public class ApplicationPropertiesPanel extends DocumentEditorSubPanel {
	private JTree tree = null;
	private SimulationContext simulationContext = null;
	private ApplicationPropertiesTreeModel treeModel = null;
	private JTextArea annotationTextArea;
	private JTextField nameTextField = null;
	private EventHandler eventHandler = new EventHandler();
	
	private class EventHandler extends MouseAdapter implements ActionListener, FocusListener, PropertyChangeListener {
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
			if (e.getSource() == annotationTextArea) {
				changeAnnotation();
			} else if (e.getSource() == nameTextField) {
				changeName();
			}
		}
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			super.mouseExited(e);
			if(e.getSource() == annotationTextArea){
				changeAnnotation();
			}
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == simulationContext) {
				if (evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_NAME) 
						|| evt.getPropertyName().equals(SimulationContext.PROPERTY_NAME_DESCRIPTION)) {
					updateInterface();
				}
			}
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == nameTextField) {
				changeName();
			}		
		}
	}	

/**
 * BioModelTreePanel constructor comment.
 */
public ApplicationPropertiesPanel() {
	super();
	initialize();
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		setName(this.getClass().getName());
		setLayout(new GridBagLayout());

		nameTextField = new JTextField();
		nameTextField.addActionListener(eventHandler);
		nameTextField.addFocusListener(eventHandler);
		
		annotationTextArea = new javax.swing.JTextArea("", 1, 30);
		annotationTextArea.setLineWrap(true);
		annotationTextArea.setWrapStyleWord(true);
		annotationTextArea.addFocusListener(eventHandler);
		annotationTextArea.addMouseListener(eventHandler);
		
		tree = new JTree();
		treeModel = new ApplicationPropertiesTreeModel();
		tree.setModel(treeModel);
		ToolTipManager.sharedInstance().registerComponent(tree);
		tree.setCellRenderer(new BioModelCellRenderer(null));
		tree.setRootVisible(false);
		
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		JLabel label = new JLabel("Application Name");
		add(label, gbc);
		
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;
		add(nameTextField, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		add(new JLabel("Description"), gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 0.3;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		add(new javax.swing.JScrollPane(annotationTextArea), gbc);
				
		gridy ++;		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		add(new JLabel("Summary"), gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		add(new JScrollPane(tree), gbc);
		
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Set the BioModel to a new value.
 * @param newValue cbit.vcell.biomodel.BioModel
 */
private void setSimulationContext(SimulationContext newValue) {
	if (simulationContext == newValue) {
		return;
	}
	simulationContext = newValue;
	treeModel.setSimulationContext(simulationContext);
	updateInterface();
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects == null || selectedObjects.length != 1) {
		setSimulationContext(null);
	} else if (selectedObjects[0] instanceof SimulationContext) {
		setSimulationContext((SimulationContext) selectedObjects[0]);
	} else {
		setSimulationContext(null);
	}
}

private void changeName() {
	if (simulationContext == null) {
		return;
	}
	String newName = nameTextField.getText();
	if (newName == null || newName.length() == 0) {
		nameTextField.setText(simulationContext.getName());
		return;
	}
	if (newName.equals(simulationContext.getName())) {
		return;
	}
	try {
		simulationContext.setName(newName);
	} catch (PropertyVetoException e1) {
		e1.printStackTrace();
		DialogUtils.showErrorDialog(this, e1.getMessage());
	}
}

private void changeAnnotation() {
	try{
		if (simulationContext == null) {
			return;
		}
		String description = annotationTextArea.getText();
		simulationContext.setDescription(description);
	} catch(Exception e) {
		e.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, e.getMessage(), e);
	}
}

private void updateInterface() {
	if (simulationContext == null) {
		return;
	}
	nameTextField.setText(simulationContext.getName());
	annotationTextArea.setText(simulationContext.getDescription());
	annotationTextArea.setCaretPosition(0);
}

}

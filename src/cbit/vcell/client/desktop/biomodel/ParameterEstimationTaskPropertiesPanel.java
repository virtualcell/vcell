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
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.modelopt.ParameterEstimationTask;
/**
 * Insert the type's description here.
 * Creation date: (8/22/2005 5:21:08 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class ParameterEstimationTaskPropertiesPanel extends DocumentEditorSubPanel {
	private IvjEventHandler eventHandler = new IvjEventHandler();
	private javax.swing.JTextArea annotationTextArea = null;
	private javax.swing.JTextField taskNameTextField = null;
	private ParameterEstimationTask fieldParameterEstimationTask = null;
	
	private class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == taskNameTextField) {
				try {
					if (fieldParameterEstimationTask == null) {
						return;
					}
					fieldParameterEstimationTask.setName(taskNameTextField.getText());
				} catch (Exception ex) {
					DialogUtils.showErrorDialog(ParameterEstimationTaskPropertiesPanel.this, ex.getMessage());
				}
			}
		};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			try {
				if (fieldParameterEstimationTask == null) {
					return;
				}
				if (e.getSource() == ParameterEstimationTaskPropertiesPanel.this.taskNameTextField) 
					fieldParameterEstimationTask.setName(taskNameTextField.getText());
				if (e.getSource() == annotationTextArea) 
					fieldParameterEstimationTask.setAnnotation(annotationTextArea.getText());
			} catch (Exception ex) {
				DialogUtils.showErrorDialog(ParameterEstimationTaskPropertiesPanel.this, ex.getMessage());
			}
		};
		
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == fieldParameterEstimationTask && (evt.getPropertyName().equals("name"))) 
				taskNameTextField.setText(fieldParameterEstimationTask.getName());
			if (evt.getSource() == fieldParameterEstimationTask && (evt.getPropertyName().equals("annotation"))) 
				annotationTextArea.setText(fieldParameterEstimationTask.getAnnotation());
		};
	};

	/**
	 * OptTestPanel constructor comment.
	 */
	public ParameterEstimationTaskPropertiesPanel() {
		super();
		initialize();
	}

	/**
	 * Return the AnnotationPanel property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		setLayout(new java.awt.GridBagLayout());

		taskNameTextField = new javax.swing.JTextField();
		annotationTextArea = new javax.swing.JTextArea();
		annotationTextArea.setMargin(new java.awt.Insets(6, 3, 3, 3));

		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Parameter Estimation Task Name"), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = 0;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(taskNameTextField, gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 1;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		add(new JLabel("Annotation"), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; gbc.gridy = 1;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(new JScrollPane(annotationTextArea), gbc);
		
		taskNameTextField.addFocusListener(eventHandler);
		taskNameTextField.addActionListener(eventHandler);
		annotationTextArea.addFocusListener(eventHandler);

	}
	private void setParameterEstimationTask(ParameterEstimationTask newValue) {
		ParameterEstimationTask oldValue = fieldParameterEstimationTask;
		fieldParameterEstimationTask = newValue;
		/* Stop listening for events from the current object */
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(eventHandler);
		}
	
		/* Listen for events from the new object */
		if (newValue != null) {
			newValue.addPropertyChangeListener(eventHandler);	
			taskNameTextField.setText(fieldParameterEstimationTask.getName());
			annotationTextArea.setText(fieldParameterEstimationTask.getAnnotation());
		} else {
			taskNameTextField.setText(null);
			annotationTextArea.setText(null);			
		}
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		ParameterEstimationTask pet = null;
		if (selectedObjects != null && selectedObjects.length == 1 && selectedObjects[0] instanceof ParameterEstimationTask) {
			pet = (ParameterEstimationTask) selectedObjects[0];
		}		
		setParameterEstimationTask(pet);
	}
}

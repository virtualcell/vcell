/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.mathmodel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.mathmodel.MathModel;
/**
 * Insert the type's description here.
 * Creation date: (2/3/2003 2:07:01 PM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class MathModelEditorAnnotationPanel extends DocumentEditorSubPanel {
	private MathModel mathModel = null;
	private EventHandler eventHandler = new EventHandler();
	private JTextArea annotationTextArea;

	private class EventHandler implements FocusListener {
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
			if (e.getSource() == annotationTextArea) {
				changeAnnotation();
			}
		}
	}

/**
 * EditSpeciesDialog constructor comment.
 */
public MathModelEditorAnnotationPanel() {
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
private void initialize() {
	try {		
		annotationTextArea = new javax.swing.JTextArea();
		annotationTextArea.setLineWrap(true);
		annotationTextArea.setWrapStyleWord(true);
		
		setLayout(new GridBagLayout());
		setBackground(Color.white);
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		add(new JLabel("Notes:"), gbc);

		javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(annotationTextArea);		
		gbc = new java.awt.GridBagConstraints();
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 20, 10);
		add(jsp, gbc);		
				
		annotationTextArea.addFocusListener(eventHandler);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		JFrame frame = new JFrame();
		MathModelEditorAnnotationPanel aEditSpeciesPanel = new MathModelEditorAnnotationPanel();
		frame.add(aEditSpeciesPanel);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			};
		});
		frame.pack();
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of cbit.gui.JInternalFrameEnhanced");
		exception.printStackTrace(System.out);
	}
}

/**
 * Comment
 */
private void changeAnnotation() {
	try{
		if (mathModel == null) {
			return;
		}
		mathModel.setDescription(annotationTextArea.getText());	
	} catch(Exception e){
		e.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this,"Edit Species Error\n"+e.getMessage(), e);
	}
}

/**
 * Sets the speciesContext property (cbit.vcell.model.SpeciesContext) value.
 * @param speciesContext The new value for the property.
 * @see #getSpeciesContext
 */
public void setMathModel(MathModel newValue) {
	if (newValue == mathModel) {
		return;
	}
	mathModel = newValue;
	updateInterface();
}

/**
 * Comment
 */
private void updateInterface() {
	if (mathModel == null) {
		return;
	}
	annotationTextArea.setText(mathModel.getDescription());
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
}

}

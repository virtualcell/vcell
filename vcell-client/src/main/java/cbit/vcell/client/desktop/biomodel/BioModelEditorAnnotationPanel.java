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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import org.vcell.util.Compare;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.PopupGenerator;
/**
 * Insert the type's description here.
 * Creation date: (2/3/2003 2:07:01 PM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class BioModelEditorAnnotationPanel extends DocumentEditorSubPanel {
	private BioModel bioModel = null;
	private EventHandler eventHandler = new EventHandler();
	private JTextPane annotationTextArea;

	private class EventHandler extends MouseAdapter implements FocusListener {
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
			if (e.getSource() == annotationTextArea) {
				changeAnnotation();
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

	}

/**
 * EditSpeciesDialog constructor comment.
 */
public BioModelEditorAnnotationPanel() {
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
		annotationTextArea = new javax.swing.JTextPane();
//		annotationTextArea.setLineWrap(true);
//		annotationTextArea.setWrapStyleWord(true);
		annotationTextArea.setContentType("text/html");
		annotationTextArea.setEditable(false);
		
		setLayout(new GridBagLayout());
		setBackground(Color.white);
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 0;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		add(new JLabel("Notes:"), gbc);

		javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(annotationTextArea);		
		gbc = new java.awt.GridBagConstraints();
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.gridx = 1; 
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 20, 10);
		add(jsp, gbc);		
				
		annotationTextArea.addFocusListener(eventHandler);
		annotationTextArea.addMouseListener(eventHandler);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
private void changeAnnotation() {
	try{
		if (bioModel == null) {
			return;
		}
		String textAreaStr = (annotationTextArea.getText() == null || annotationTextArea.getText().length()==0?null:annotationTextArea.getText());
		if(!Compare.isEqualOrNull(bioModel.getVCMetaData().getFreeTextAnnotation(bioModel),textAreaStr)){
//			bioModel.getVCMetaData().setFreeTextAnnotation(bioModel, textAreaStr);	
		}
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
public void setBioModel(BioModel newValue) {
	if (newValue == bioModel) {
		return;
	}
	bioModel = newValue;
	updateInterface();
}

private void updateInterface() {
	if (bioModel == null) {
		return;
	}
	
	annotationTextArea.setText(bioModel.getVCMetaData().getFreeTextAnnotation(bioModel));
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
//	if (selectedObjects != null && selectedObjects.length == 1) {
//		if (selectedObjects[0]  instanceof BioModel) {
//			setBioModel((BioModel)selectedObjects[0]);
//		}
//	}
}

}

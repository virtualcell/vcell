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

import java.awt.BorderLayout;

import javax.swing.JMenu;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryOwner;
import cbit.vcell.math.gui.MathDescEditor;
import cbit.vcell.mathmodel.MathModel;

/**
 * Insert the type's description here.
 * Creation date: (5/20/2004 3:35:42 PM)
 * @author: Anuradha Lakshminarayana
 */
@SuppressWarnings("serial")
public class VCMLEditorPanel extends javax.swing.JPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private MathDescEditor mathDescEditor = null;
	private MathModel fieldMathModel = new MathModel(null);

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == fieldMathModel && (evt.getPropertyName().equals(MathModel.PROPERTY_NAME_MATH_DESCRIPTION))) { 
				getMathDescEditor().setMathDescription(fieldMathModel.getMathDescription());
			}
			if (evt.getSource() == fieldMathModel && evt.getPropertyName().equals(GeometryOwner.PROPERTY_NAME_GEOMETRY)) {
				Geometry oldValue = (Geometry) evt.getOldValue();
				if (oldValue != null) {
					oldValue.getGeometrySpec().removePropertyChangeListener(this);
				}
				Geometry newValue = (Geometry) evt.getNewValue();
				if (newValue != null) {
					newValue.getGeometrySpec().addPropertyChangeListener(this);
				}
			}
			if (evt.getSource() == VCMLEditorPanel.this.getMathDescEditor() && (evt.getPropertyName().equals("mathDescription"))) {
				fieldMathModel.setMathDescription(getMathDescEditor().getMathDescription()); 
			}
		};
	};

/**
 * VCMLEditorPanel constructor comment.
 */
public VCMLEditorPanel() {
	super();
	initialize();
}

/**
 * Return the mathDescEditor property value.
 * @return cbit.vcell.math.gui.MathDescEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathDescEditor getMathDescEditor() {
	if (mathDescEditor == null) {
		try {
			mathDescEditor = new MathDescEditor();
			mathDescEditor.setName("mathDescEditor");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return mathDescEditor;
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
		// user code begin {1}
		// user code end
		setName("VCMLEditorPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(354, 336);
		add(getMathDescEditor(), BorderLayout.CENTER);
//		add(new JScrollPane(getMathWarningTextPane()), BorderLayout.SOUTH);
		
		getMathDescEditor().addPropertyChangeListener(ivjEventHandler);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		VCMLEditorPanel aVCMLEditorPanel;
		aVCMLEditorPanel = new VCMLEditorPanel();
		frame.setContentPane(aVCMLEditorPanel);
		frame.setSize(aVCMLEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Sets the mathModel property (cbit.vcell.mathmodel.MathModel) value.
 * @param mathModel The new value for the property.
 * @see #getMathModel
 */
public void setMathModel(MathModel newValue) {
	if (fieldMathModel == newValue) {
		return;
	}
	MathModel oldValue = fieldMathModel;
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(ivjEventHandler);
		if (oldValue.getGeometry() != null) {
			oldValue.getGeometry().getGeometrySpec().removePropertyChangeListener(ivjEventHandler);
		}
	}
	fieldMathModel = newValue;
	if (newValue != null) {
		newValue.addPropertyChangeListener(ivjEventHandler);
		if (newValue.getGeometry() != null) {
			newValue.getGeometry().getGeometrySpec().addPropertyChangeListener(ivjEventHandler);
		}
		getMathDescEditor().setMathDescription(fieldMathModel.getMathDescription());
	}
}

public boolean hasUnappliedChanges() {
	return getMathDescEditor().hasUnappliedChanges();
}

public JMenu getEditMenu() {
	return getMathDescEditor().getEditMenu();
}
}

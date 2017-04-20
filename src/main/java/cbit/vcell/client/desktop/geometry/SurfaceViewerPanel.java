/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.geometry;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.gui.SurfaceEditor;

/**
 * Insert the type's description here.
 * Creation date: (6/3/2004 6:01:55 PM)
 * @author: Anuradha Lakshminarayana
 */
@SuppressWarnings("serial")
public class SurfaceViewerPanel extends javax.swing.JPanel {
	private SurfaceEditor ivjsurfaceEditor = null;
	private Geometry fieldGeometry = null;

	private class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SurfaceViewerPanel.this && (evt.getPropertyName().equals("geometry"))) {
				surfaceViewerPanel_Geometry();
			}
		};
	};
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();

/**
 * SurfaceViewerPanel constructor comment.
 */
public SurfaceViewerPanel() {
	super();
	initialize();
}

/**
 * Gets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @return The geometry property value.
 * @see #setGeometry
 */
public Geometry getGeometry() {
	return fieldGeometry;
}


/**
 * Return the surfaceEditor property value.
 * @return cbit.vcell.geometry.gui.SurfaceEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SurfaceEditor getsurfaceEditor() {
	if (ivjsurfaceEditor == null) {
		try {
			ivjsurfaceEditor = new SurfaceEditor();
			ivjsurfaceEditor.setName("surfaceEditor");
//			ivjsurfaceEditor.setPreferredSize(new java.awt.Dimension(349, 200));
//			ivjsurfaceEditor.setMaximumSize(new java.awt.Dimension(350, 200));
//			ivjsurfaceEditor.setMinimumSize(new java.awt.Dimension(287, 198));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjsurfaceEditor;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SurfaceViewerPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(469, 560);
		add(getsurfaceEditor(), "Center");
		initConnections();
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
		SurfaceViewerPanel aSurfaceViewerPanel;
		aSurfaceViewerPanel = new SurfaceViewerPanel();
		frame.setContentPane(aSurfaceViewerPanel);
		frame.setSize(aSurfaceViewerPanel.getSize());
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
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setGeometry(Geometry geometry) {
	Geometry oldValue = fieldGeometry;
	fieldGeometry = geometry;
	firePropertyChange("geometry", oldValue, geometry);
}


/**
 * Comment
 */
private void surfaceViewerPanel_Geometry() {
	
	if(getGeometry() == null){
		getsurfaceEditor().setGeometrySurfaceDescription(null);
	}else{
		getsurfaceEditor().setGeometrySurfaceDescription(getGeometry().getGeometrySurfaceDescription());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 3:01:30 PM)
 */
public void updateSurfaces() throws Exception{
	
	getsurfaceEditor().updateSurface();
	
}
}

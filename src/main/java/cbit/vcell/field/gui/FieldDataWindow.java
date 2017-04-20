/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.field.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.vcell.client.logicalwindow.LWTopFrame;

import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.FieldDataWindowManager;
import cbit.vcell.client.desktop.TopLevelWindow;
import cbit.vcell.client.server.ConnectionStatus;

public class FieldDataWindow extends LWTopFrame implements TopLevelWindow {
	private FieldDataWindowManager fieldDataWindowManger;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	
	private boolean bTreeNeedsUpdate = true;
	private boolean bWindowOpened = false;
	private final ChildWindowManager childWindowManager;
	private final String  menuDesc;

/**
 * FieldDataWindow constructor comment.
 */
public FieldDataWindow() {
	super();
	initialize();
	
	addWindowListener(
			new WindowAdapter(){
				public void windowOpened(WindowEvent e) {
					bWindowOpened = true;
					updateConnectionStatus_private(ConnectionStatus.CONNECTED);
				}
			}
	);
	childWindowManager = new ChildWindowManager(this);
	menuDesc = nextSequentialDescription("Field Data Window");
}

@Override
public String menuDescription() {
	return menuDesc; 
}


/**
 * Insert the method's description here.
 * Creation date: (3/20/2007 6:26:09 AM)
 * @return java.lang.Object
 */
public FieldDataWindowManager getFieldDataWindowManger() {
	return fieldDataWindowManger;
}


/**
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new java.awt.BorderLayout());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJFrameContentPane;
}

/**
 * Insert the method's description here.
 * Creation date: (3/20/2007 6:15:14 AM)
 * @return cbit.vcell.client.desktop.TopLevelWindowManager
 */
public cbit.vcell.client.TopLevelWindowManager getTopLevelWindowManager() {
	return (cbit.vcell.client.TopLevelWindowManager)fieldDataWindowManger;
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
		setName("FieldDataWindow");
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(426, 240);
		setContentPane(getJFrameContentPane());
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
		FieldDataWindow aFieldDataWindow;
		aFieldDataWindow = new FieldDataWindow();
		aFieldDataWindow.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aFieldDataWindow.setVisible(true);
		java.awt.Insets insets = aFieldDataWindow.getInsets();
		aFieldDataWindow.setSize(aFieldDataWindow.getWidth() + insets.left + insets.right, aFieldDataWindow.getHeight() + insets.top + insets.bottom);
		aFieldDataWindow.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/20/2007 6:23:56 AM)
 */
public void setFieldDataWindowManager(FieldDataWindowManager fdwm) {
	
	fieldDataWindowManger = fdwm;	
}


/**
 * Insert the method's description here.
 * Creation date: (3/20/2007 6:29:36 AM)
 */
public void setWorkArea(java.awt.Component c) {
	getContentPane().add(c, java.awt.BorderLayout.CENTER);
}


/**
 * Insert the method's description here.
 * Creation date: (3/20/2007 6:15:14 AM)
 * @param connectionStatus cbit.vcell.client.server.ConnectionStatus
 */
public void updateConnectionStatus(cbit.vcell.client.server.ConnectionStatus connectionStatus) {
	updateConnectionStatus_private(connectionStatus.getStatus());
}

private void updateConnectionStatus_private(int connectionStatus) {
	if(connectionStatus == ConnectionStatus.CONNECTED){
		if(bTreeNeedsUpdate && bWindowOpened){
			getFieldDataWindowManger().updateJTree();
			bTreeNeedsUpdate = false;
		}
	}else{
		bTreeNeedsUpdate = true;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/20/2007 6:15:14 AM)
 * @param freeBytes long
 * @param totalBytes long
 */
public void updateMemoryStatus(long freeBytes, long totalBytes) {}


/**
 * Insert the method's description here.
 * Creation date: (3/20/2007 6:15:14 AM)
 * @param i int
 */
public void updateWhileInitializing(int i) {}

public ChildWindowManager getChildWindowManager() {
	return childWindowManager;
}

}

package org.vcell.util.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.event.*;
import java.awt.*;
/**
 * This type was created in VisualAge.
 */
public class WindowCloser extends java.awt.event.WindowAdapter {
	private Window window = null;
	private boolean bExitOnClose = false;
/**
 * WindowCloser constructor comment.
 */
public WindowCloser(Window window) {
	this.window = window;
	window.addWindowListener(this);
}
/**
 * This method was created in VisualAge.
 * @param window java.awt.Window
 * @param exitOnClose boolean
 */
public WindowCloser(Window window, boolean exitOnClose) {
	this.window = window;
	window.addWindowListener(this);
	this.bExitOnClose = exitOnClose;
}
/**
 * This method was created in VisualAge.
 * @param event WindowEvent
 */
public void windowClosing(WindowEvent event) {
	window.dispose();
	if (bExitOnClose){
		System.exit(0);
	}
}
}

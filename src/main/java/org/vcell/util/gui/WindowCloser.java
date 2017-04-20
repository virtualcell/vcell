/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;

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

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

import java.awt.*;
import javax.swing.border.*;
/**
 * Dummy subclass of EmptyBorder that has a no-parameter constructor so it can
 * be recognized by VAJava as a bean implementing the border interface.
 * Also, for VAJava to work properly with the bean, we need accessors for the
 * parameters specified in the other constructors.
 * Creation date: (2/11/2001 10:08:40 PM)
 * @author: Ion Moraru
 */
public class EmptyBorderBean extends EmptyBorder {
/**
 * Insert the method's description here.
 * Creation date: (2/11/2001 10:09:03 PM)
 */
public EmptyBorderBean() {
	this(0, 0, 0, 0);
}
/**
 * EmptyBorderBean constructor comment.
 * @param top int
 * @param left int
 * @param bottom int
 * @param right int
 */
public EmptyBorderBean(int top, int left, int bottom, int right) {
	super(top, left, bottom, right);
}
/**
 * EmptyBorderBean constructor comment.
 * @param insets java.awt.Insets
 */
public EmptyBorderBean(java.awt.Insets insets) {
	super(insets);
}
	public Insets getInsets() {
		return new Insets(top, left, bottom, right);
	}
	public void setInsets(Insets insets) {
		this.top = insets.top; 
		this.right = insets.right;
		this.bottom = insets.bottom;
		this.left = insets.left;
	}
}

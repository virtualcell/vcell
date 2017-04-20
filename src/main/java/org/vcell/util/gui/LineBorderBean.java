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

import javax.swing.border.*;
import java.awt.*;
/**
 * Dummy subclass of LineBorder that has a no-parameter constructor so it can
 * be recognized by VAJava as a bean implementing the border interface.
 * Also, for VAJava to work properly with the bean, we need accessors for the
 * parameters specified in the other constructors
 * Creation date: (2/11/2001 10:05:15 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class LineBorderBean extends LineBorder {
/**
 * Insert the method's description here.
 * Creation date: (2/11/2001 10:05:42 PM)
 */
public LineBorderBean() {
	this(Color.black);
}
/**
 * LineBorderBean constructor comment.
 * @param color java.awt.Color
 */
public LineBorderBean(java.awt.Color color) {
	super(color);
}
/**
 * LineBorderBean constructor comment.
 * @param color java.awt.Color
 * @param thickness int
 */
public LineBorderBean(java.awt.Color color, int thickness) {
	super(color, thickness);
}
	public boolean getRoundedCorners() {
		return roundedCorners;
	}
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}
	public void setRoundedCorners(boolean b) {
		this.roundedCorners = b;
	}
	public void setThickness(int lineThickness) {
		this.thickness = lineThickness;
	}
}

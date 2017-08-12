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

import java.awt.Color;

/**
 * Insert the type's description here.
 * Creation date: (4/9/01 10:43:47 AM)
 * @author: Jim Schaff
 */
public class ColorIcon implements javax.swing.Icon {
	private int sizeX = 0;
	private int sizeY = 0;
	private java.awt.Color color = null;
	private boolean drawBorder = false;
/**
 * ColorIcon constructor comment.
 */
	public ColorIcon(int argSizeX, int argSizeY, java.awt.Color argColor) {
		this(argSizeX, argSizeY, argColor, false);
	}
	public ColorIcon(int argSizeX, int argSizeY, java.awt.Color argColor, boolean argDrawBorder) {
		this.sizeX = argSizeX;
		this.sizeY = argSizeY;
		this.color = argColor;
		this.drawBorder = argDrawBorder;
	}
	/**
	 * Returns the icon's height.
	 *
	 * @return an int specifying the fixed height of the icon.
	 */
public int getIconHeight() {
	return sizeY;
}
	/**
	 * Returns the icon's width.
	 *
	 * @return an int specifying the fixed width of the icon.
	 */
public int getIconWidth() {
	return sizeX;
}
	/**
	 * Draw the icon at the specified location.  Icon implementations
	 * may use the Component argument to get properties useful for 
	 * painting, e.g. the foreground or background color.
	 */
public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
	g.setColor(color);
	g.fillRect(x,y,sizeX,sizeY);
	if(drawBorder) {
		g.setColor(Color.gray);
		g.drawRect(x, y, sizeX, sizeY);
	}
}
}

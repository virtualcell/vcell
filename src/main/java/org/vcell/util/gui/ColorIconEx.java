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

public class ColorIconEx implements javax.swing.Icon {
	private int sizeX = 0;
	private int sizeY = 0;
	private java.awt.Color color = null;
	private java.awt.Color color2 = null;

public ColorIconEx(int argSizeX, int argSizeY, java.awt.Color argColor, java.awt.Color argColor2) {
	this.sizeX = argSizeX;
	this.sizeY = argSizeY;
	this.color = argColor;
	this.color2 = argColor2;
}

public int getIconHeight() {
	return sizeY;
}

public int getIconWidth() {
	return sizeX;
}

public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
	x=1;
	y=3;
	g.setColor(color);
	g.fillRect(x,y,sizeX/2,sizeY);
	g.setColor(color2);
	g.fillRect(x+sizeX/2+1,y,sizeX/2,sizeY);
	g.setColor(Color.gray);
	g.drawRect(x, y, sizeX, sizeY);
	g.drawLine(x+sizeX/2, y, x+sizeX/2, y+sizeY);
}
}

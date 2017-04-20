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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.Icon;

public class DownArrowIcon implements Icon {

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics g1 = g.create();
		Polygon p = new Polygon();
	    p.addPoint(x, y);
	    p.addPoint(x+getIconWidth(), y);
	    p.addPoint(x+getIconWidth()/2, y+getIconHeight());
	    g1.setColor(c.getForeground());
	    g1.fillPolygon(p);
	}

	public int getIconWidth() {
		return 8;
	}

	public int getIconHeight() {
		return 4;
	}

}

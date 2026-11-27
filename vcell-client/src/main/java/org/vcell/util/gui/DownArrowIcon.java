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

import javax.swing.AbstractButton;
import javax.swing.Icon;

/**
 * The small down arrow on a button that opens a menu. Give such a button {@link #keepPushButtonLook}: on macOS, Aqua
 * draws any button with an icon in a smaller toggle style, struck through when disabled, unlike the buttons beside it.
 */
public class DownArrowIcon implements Icon {

	/** Draws the button as a push button even with an icon (other looks ignore this); returns it, to use inline. */
	public static <B extends AbstractButton> B keepPushButtonLook(B button) {
		button.putClientProperty("JButton.buttonType", "text");
		button.updateUI(); // the toggle style, applied when the icon went on, also shrank the font: reinstall the look
		return button;
	}

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

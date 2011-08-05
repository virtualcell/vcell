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
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;
/**
 * Insert the type's description here.
 * Creation date: (5/23/2004 10:49:12 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class GlassPane extends JComponent {
	// defaults
	private float opacity = 0f;
	private Color color = Color.red;
	private boolean paint = false;

/**
 * Insert the method's description here.
 * Creation date: (5/23/2004 10:53:54 PM)
 */
public GlassPane(boolean inputBlocking) {
	if (inputBlocking) {
		addMouseListener(new MouseAdapter() {});
		addMouseMotionListener(new MouseMotionAdapter() {});
		addKeyListener(new KeyAdapter() {});
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}
}		

/**
 * Return the value of the field color
 */
public Color getColor() {
	return color;
}


/**
 * Return the value of the field opacity
 */
public float getOpacity() {
	return opacity;
}


/**
 * Return the value of the field paint
 */
public boolean getPaint() {
	return paint;
}


protected void paintComponent(java.awt.Graphics g) {
	if (getPaint()) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getOpacity()));
		g2d.setColor(getColor());
		g2d.fillRect(0,0, getWidth(), getHeight());
	}
}


/**
 * Set the value of the field color
 */
public void setColor(Color aColor) {
	color = aColor;
}


/**
 * Set the value of the field opacity
 */
public void setOpacity(float anOpacity) {
	opacity = anOpacity;
}


/**
 * Set the value of the field paint
 */
public void setPaint(boolean aPaint) {
	paint = aPaint;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 12:36:31 AM)
 * @param visible boolean
 */
public void setVisible(boolean visible) {
	super.setVisible(visible);
	if (visible) requestFocus();  // removes keyboard focus from inside components
}
}

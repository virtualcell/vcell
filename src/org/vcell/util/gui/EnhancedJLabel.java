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

import javax.swing.plaf.*;
import java.awt.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (2/10/2001 1:15:48 AM)
 * @author: Ion Moraru
 */
@SuppressWarnings({ "serial" })
public class EnhancedJLabel extends JLabel {
	private boolean fieldVertical = false;
	private boolean fieldAntialiased = false;
/**
 * EnhancedJLabel constructor comment.
 */
public EnhancedJLabel() {
	super();
}
/**
 * EnhancedJLabel constructor comment.
 * @param text java.lang.String
 */
public EnhancedJLabel(String text) {
	super(text);
}
/**
 * EnhancedJLabel constructor comment.
 * @param text java.lang.String
 * @param horizontalAlignment int
 */
public EnhancedJLabel(String text, int horizontalAlignment) {
	super(text, horizontalAlignment);
}
/**
 * EnhancedJLabel constructor comment.
 * @param text java.lang.String
 * @param icon javax.swing.Icon
 * @param horizontalAlignment int
 */
public EnhancedJLabel(String text, Icon icon, int horizontalAlignment) {
	super(text, icon, horizontalAlignment);
}
/**
 * EnhancedJLabel constructor comment.
 * @param image javax.swing.Icon
 */
public EnhancedJLabel(Icon image) {
	super(image);
}
/**
 * EnhancedJLabel constructor comment.
 * @param image javax.swing.Icon
 * @param horizontalAlignment int
 */
public EnhancedJLabel(Icon image, int horizontalAlignment) {
	super(image, horizontalAlignment);
}
/**
 * Gets the antialiased property (boolean) value.
 * @return The antialiased property value.
 * @see #setAntialiased
 */
public boolean getAntialiased() {
	return fieldAntialiased;
}
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		if (getVertical()) {
			d.setSize(d.height, d.width);
		}
		return d;
	}
/**
 * Gets the vertical property (boolean) value.
 * @return The vertical property value.
 * @see #setVertical
 */
public boolean getVertical() {
	return fieldVertical;
}
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		if (getVertical()) {
			g2.translate(0, getSize().getHeight());
			g2.rotate(- Math.PI / 2);
		}
		if (getAntialiased()) {
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		super.paintComponent(g2);
	}
/**
 * Sets the antialiased property (boolean) value.
 * @param antialiased The new value for the property.
 * @see #getAntialiased
 */
public void setAntialiased(boolean antialiased) {
	fieldAntialiased = antialiased;
}
	public void setUI(LabelUI ui) {
		if (ui instanceof EnhancedLabelUI) {
		} else if (this.ui instanceof EnhancedLabelUI) {
			ui = (EnhancedLabelUI)this.ui;
		} else {
			ui = new EnhancedLabelUI();
		}
		super.setUI(ui);
	}
/**
 * Sets the vertical property (boolean) value.
 * @param vertical The new value for the property.
 * @see #getVertical
 */
public void setVertical(boolean vertical) {
	boolean oldValue = fieldVertical;
	fieldVertical = vertical;
	firePropertyChange("vertical", new Boolean(oldValue), new Boolean(vertical));
}
}

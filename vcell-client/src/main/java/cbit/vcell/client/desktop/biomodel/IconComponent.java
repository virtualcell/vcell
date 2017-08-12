/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JComponent;

@SuppressWarnings("serial")
public class IconComponent extends JComponent {
	
	public IconComponent(Icon icon) {
		this.icon = icon;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		icon.paintIcon(this, g, 0, 0);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(icon.getIconWidth(), icon.getIconHeight());
	}
	
	private Icon icon;
}

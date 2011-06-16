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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

public class TabCloseIcon implements Icon {
	private final Icon closeIcon;
	private JTabbedPane tabbedPane = null;
	private transient Rectangle iconRectangle = null;
	
	public TabCloseIcon() {
		closeIcon = new ImageIcon(getClass().getResource("/icons/tab_close.gif"));
	}

	/**
	 * when painting, remember last position painted.
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (tabbedPane == null && c instanceof JTabbedPane) {
			tabbedPane = (JTabbedPane) c;
			MouseAdapter mouseAdapter = new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					// asking for isConsumed is *very* important, otherwise more
					// than one tab might get closed!
					if (!e.isConsumed() && iconRectangle.contains(e.getX(), e.getY())) {
						final int index = tabbedPane.getSelectedIndex();
						tabbedPane.remove(index);
						e.consume();
						tabbedPane.removeMouseListener(this);
					}
				}				
			};
			tabbedPane.addMouseListener(mouseAdapter);
		}

		iconRectangle = new Rectangle(x, y, getIconWidth(), getIconHeight());
		closeIcon.paintIcon(c, g, x, y);
	}
	
	public int getIconWidth() {
		return closeIcon.getIconWidth();
	}

	public int getIconHeight() {
		return closeIcon.getIconHeight();
	}

}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui.sorttable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class SortArrowIcon implements Icon {
	public enum SortDirection {
		NOSORTING,
		ASCENDING,
		DECENDING;
	}

	protected SortDirection sortDirection;
	protected int width = 17;
	protected int height = 7;

	public SortArrowIcon(SortDirection sd) {
		this.sortDirection = sd;
	}

	public int getIconHeight() {
		return height;
	}

	public int getIconWidth() {
		return width;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Color color = new Color(172,168,153);
		g.setColor(color);
		int x1 = x + 9;
		if (sortDirection == SortDirection.ASCENDING) {
			for (int w = 0; w < 5; w ++) {
				g.fillRect(x1 - w, y + w, w * 2 + 1, 1);
			}
		} else if (sortDirection == SortDirection.DECENDING) {
			int y1 = y + 4;
			for (int w = 0; w < 5; w ++) {
				g.fillRect(x1 - w, y1 - w, w * 2 + 1, 1);
			}

		}
	}
}

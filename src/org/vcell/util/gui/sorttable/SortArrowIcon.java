package org.vcell.util.gui.sorttable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class SortArrowIcon implements Icon {
	public static final int NONE = 0;
	public static final int DECENDING = 1;
	public static final int ASCENDING = 2;

	protected int direction;
	protected int width = 17;
	protected int height = 7;

	public SortArrowIcon(int direction) {
		this.direction = direction;
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
		if (direction == ASCENDING) {
			for (int w = 0; w < 5; w ++) {
				g.fillRect(x1 - w, y + w, w * 2 + 1, 1);
			}
		} else if (direction == DECENDING) {
			int y1 = y + 4;
			for (int w = 0; w < 5; w ++) {
				g.fillRect(x1 - w, y1 - w, w * 2 + 1, 1);
			}

		}
	}
}

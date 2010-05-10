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
		int k = 7 + x + 2;
		if (direction == ASCENDING) {
			int l = y;
			g.fillRect(k, l, 1, 1);
			for (int j1 = 1; j1 < 5; j1++) {
				g.fillRect(k - j1, l + j1, j1 + j1 + 1, 1);
			}
		} else if (direction == DECENDING) {
			int i1 = (y + 5) - 1;
			g.fillRect(k, i1, 1, 1);
			for (int k1 = 1; k1 < 5; k1++) {
				g.fillRect(k - k1, i1 - k1, k1 + k1 + 1, 1);
			}

		}
	}
}

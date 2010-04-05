package org.vcell.util.gui.sorttable;

/*
=====================================================================

  SortArrowIcon.java
  
  Created by Claude Duguay
  Copyright (c) 2002
  
=====================================================================
*/

import java.awt.*;
import javax.swing.*;

public class SortArrowIcon
  implements Icon
{
  public static final int NONE = 0;
  public static final int DECENDING = 1;
  public static final int ASCENDING = 2;

  protected int direction;
  protected int width = 8;
  protected int height = 8;
  
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
	Color shade = c.getBackground().darker();

	int w = width;
	int h = height;
	int m = w / 2;
	
	if (direction == DECENDING) {
		g.setColor(shade);
		g.fillPolygon(new Polygon(new int[] {x, x + w, x + m}, new int[] {y, y, y + h}, 3));		
	} else if (direction == ASCENDING) {
		g.setColor(shade);
		g.fillPolygon(new Polygon(new int[] {x, x + w, x + m}, new int[] {y + h, y + h, y}, 3));
	}

}
}

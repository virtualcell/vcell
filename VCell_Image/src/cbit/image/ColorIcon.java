package cbit.image;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (4/9/01 10:43:47 AM)
 * @author: Jim Schaff
 */
public class ColorIcon implements javax.swing.Icon {
	private int sizeX = 0;
	private int sizeY = 0;
	private java.awt.Color color = null;
/**
 * ColorIcon constructor comment.
 */
public ColorIcon(int argSizeX, int argSizeY, java.awt.Color argColor) {
	this.sizeX = argSizeX;
	this.sizeY = argSizeY;
	this.color = argColor;
}
	/**
	 * Returns the icon's height.
	 *
	 * @return an int specifying the fixed height of the icon.
	 */
public int getIconHeight() {
	return sizeY;
}
	/**
	 * Returns the icon's width.
	 *
	 * @return an int specifying the fixed width of the icon.
	 */
public int getIconWidth() {
	return sizeX;
}
	/**
	 * Draw the icon at the specified location.  Icon implementations
	 * may use the Component argument to get properties useful for 
	 * painting, e.g. the foreground or background color.
	 */
public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
	g.setColor(color);
	g.fillRect(x,y,sizeX,sizeY);
}
}

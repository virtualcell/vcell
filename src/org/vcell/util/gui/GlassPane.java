package org.vcell.util.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
/**
 * Insert the type's description here.
 * Creation date: (5/23/2004 10:49:12 PM)
 * @author: Ion Moraru
 */
public class GlassPane extends JComponent {
	// defaults
	private float opacity = 0.3f;
	private Color color = Color.red;
	private boolean paint = false;
	// intercepts mouse input; make it fancier later (customize to dispatch to specific children etc.)
	private MouseInputAdapter listener = new javax.swing.event.MouseInputAdapter() {
		public void mouseMoved(MouseEvent e) {}
		public void mouseDragged(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {
			java.awt.Toolkit.getDefaultToolkit().beep();
		}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	};

/**
 * Insert the method's description here.
 * Creation date: (5/23/2004 10:53:54 PM)
 */
public GlassPane(boolean inputBlocking) {
	if (inputBlocking) {
		addMouseListener(listener);
		addMouseMotionListener(listener);
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
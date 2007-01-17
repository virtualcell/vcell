package cbit.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import javax.swing.text.*;
import javax.swing.*;
import java.awt.*;
/**
 * A subclass of BevelBorder that has a no-parameter constructor so it can
 * be recognized by VAJava as a bean implementing the border interface.
 * Fixes bug of not respecting insets - see overriden getBorderInsets(Component).
 * Also, a somewhat improved appearance when only the base color is specified.
 * Creation date: (7/11/2000 11:42:55 PM)
 * @author: Ion Moraru
 */
public class BevelBorderBean extends javax.swing.border.BevelBorder {
/**
 * BevelBorderBean constructor comment.
 * @param bevelType int
 */
public BevelBorderBean() {
	this(RAISED);
}
/**
 * BevelBorderBean constructor comment.
 * @param bevelType int
 */
public BevelBorderBean(int bevelType) {
	super(bevelType);
}
/**
 * BevelBorderBean constructor comment.
 * @param bevelType int
 * @param highlight java.awt.Color
 * @param shadow java.awt.Color
 */
public BevelBorderBean(int bevelType, java.awt.Color highlight, java.awt.Color shadow) {
	super(bevelType, highlight, shadow);
}
/**
 * BevelBorderBean constructor comment.
 * @param bevelType int
 * @param highlightOuter java.awt.Color
 * @param highlightInner java.awt.Color
 * @param shadowOuter java.awt.Color
 * @param shadowInner java.awt.Color
 */
public BevelBorderBean(int bevelType, java.awt.Color highlightOuter, java.awt.Color highlightInner, java.awt.Color shadowOuter, java.awt.Color shadowInner) {
	super(bevelType, highlightOuter, highlightInner, shadowOuter, shadowInner);
}
/**
 * Returns the insets of the border.
 * @param c the component for which this border insets value applies
 */

// We override this method so the border respects the margins of components that have settable margins
 
public Insets getBorderInsets(Component c) {
	Insets ins = new Insets(0, 0, 0, 0);
	if (c instanceof AbstractButton) ins = ((AbstractButton)c).getMargin();
	if (c instanceof JMenuBar) ins = ((JMenuBar)c).getMargin();
	if (c instanceof JPopupMenu) ins = ((JPopupMenu)c).getMargin();
	if (c instanceof JTextComponent) ins = ((JTextComponent)c).getMargin();
	if (c instanceof JToolBar) ins = ((JToolBar)c).getMargin();
	return new Insets(ins.top + 2, ins.left + 2, ins.bottom + 2, ins.right + 2);
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2000 12:06:34 AM)
 * @return java.awt.Color
 */
public java.awt.Color getColor() {
	return highlightInner;
}
/**
 * Insert the method's description here.
 * Creation date: (7/11/2000 11:49:43 PM)
 * @param bevelType int
 */
public void setBevelType(int bevelType) {
	if (bevelType == RAISED || bevelType == LOWERED) {
		this.bevelType = bevelType;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2000 12:00:43 AM)
 * @param color java.awt.Color
 */
public void setColor(java.awt.Color color) {
	this.highlightOuter = color.brighter().brighter();
	this.highlightInner = color.brighter();
	this.shadowInner = color.darker();
	this.shadowOuter = color.darker().darker();
}
}

package org.vcell.util.gui;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.awt.*;
/**
 * Insert the type's description here.
 * Creation date: (1/30/2001 2:23:33 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class JInternalFrameEnhanced extends javax.swing.JInternalFrame {
	private boolean stripped = false;
	public boolean isDragging = false;
	public boolean danger = false;
/**
 * JInternalFrameEnhanced constructor comment.
 */
public JInternalFrameEnhanced() {
	this("", false, false, false, false, false);
}
/**
 * JInternalFrameEnhanced constructor comment.
 * @param title java.lang.String
 */
public JInternalFrameEnhanced(String title) {
	this(title, false, false, false, false, false);
}
/**
 * JInternalFrameEnhanced constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 */
public JInternalFrameEnhanced(String title, boolean resizable) {
	this(title, resizable, false, false, false, false);
}
/**
 * JInternalFrameEnhanced constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 * @param closable boolean
 */
public JInternalFrameEnhanced(String title, boolean resizable, boolean closable) {
	this(title, resizable, closable, false, false, false);
}
/**
 * JInternalFrameEnhanced constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 * @param closable boolean
 * @param maximizable boolean
 */
public JInternalFrameEnhanced(String title, boolean resizable, boolean closable, boolean maximizable) {
	this(title, resizable, closable, maximizable, false, false);
}
/**
 * JInternalFrameEnhanced constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 * @param closable boolean
 * @param maximizable boolean
 * @param iconifiable boolean
 */
public JInternalFrameEnhanced(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
	this(title, resizable, closable, maximizable, iconifiable, false);
}
/**
 * JInternalFrameEnhanced constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 * @param closable boolean
 * @param maximizable boolean
 * @param iconifiable boolean
 * @param stripped boolean
 */
public JInternalFrameEnhanced(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable, boolean stripped) {
	super(title, resizable, closable, maximizable, iconifiable);
	setStripped(stripped);
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2001 2:27:09 PM)
 * @return boolean
 */
public boolean isStripped() {
	return stripped;
}
	protected void paintComponent(Graphics g) {
	  if (isDragging) {
	//	   System.out.println("ouch");
		 danger = true;
	  }

	  super.paintComponent(g);
   }   
/**
 * Insert the method's description here.
 * Creation date: (1/30/2001 2:27:09 PM)
 * @param newStripped boolean
 */
public void setStripped(boolean newStripped) {
	boolean oldValue = stripped;
	stripped = newStripped;
	updateUI();
	firePropertyChange("stripped", new Boolean(oldValue), new Boolean(newStripped));
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2001 2:33:46 PM)
 * @param param javax.swing.plaf.InternalFrameUI
 */
public void setUI(javax.swing.plaf.InternalFrameUI ui) {
	super.setUI(ui);
	if (isStripped()) {
		if (ui instanceof javax.swing.plaf.basic.BasicInternalFrameUI) {
			javax.swing.plaf.basic.BasicInternalFrameUI bui = (javax.swing.plaf.basic.BasicInternalFrameUI)ui;
			bui.setNorthPane(null);
			bui.setWestPane(null);
			bui.setEastPane(null);
			bui.setSouthPane(null);
		}
//		setBorder(null);
	}
}
	public void show() {
//	javax.swing.SwingUtilities.updateComponentTreeUI(this);
	super.show();
}
}

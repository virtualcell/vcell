package org.vcell.util.gui;

import java.awt.event.MouseEvent;

import javax.swing.JTable;

public interface ScrollTableActionManager {
	void triggerPopup(MouseEvent e);
	JTable getOwnerTable();
}

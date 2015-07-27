package org.vcell.util.gui;

import java.awt.LayoutManager;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class DialogMessagePanel extends JPanel {
	protected DialogMessagePanel(LayoutManager layout) {
		super(layout);
	}
	
	public abstract String getSupplemental( );

}

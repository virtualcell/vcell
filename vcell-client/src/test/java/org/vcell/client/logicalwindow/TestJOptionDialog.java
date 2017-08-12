package org.vcell.client.logicalwindow;

import javax.swing.JOptionPane;

public class TestJOptionDialog extends LWOptionPaneDialog{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TestJOptionDialog(LWContainerHandle parent, String title,JOptionPane pane) {
		//super(parent, "Yes No", new JOptionPane("Good?", JOptionPane.YES_NO_CANCEL_OPTION));
		super(parent, title, pane); 
	}

	@Override
	public String menuDescription() {
		return null;
	}

}

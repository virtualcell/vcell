package org.vcell.client.logicalwindow;

import java.awt.HeadlessException;

import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class LWModelessWarning extends LWOptionPaneFrame {

	public LWModelessWarning(LWContainerHandle parent, String message) throws HeadlessException {
		super(parent,"Warning",new JOptionPane(message,JOptionPane.WARNING_MESSAGE));
	}
	

}

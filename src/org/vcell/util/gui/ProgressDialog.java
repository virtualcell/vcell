/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;
import java.awt.Frame;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JProgressBar;

import org.vcell.util.ProgressDialogListener;
/**
 * Insert the type's description here.
 * Creation date: (5/18/2004 1:14:29 AM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public abstract class ProgressDialog extends JDialog {
	protected JProgressBar progressBar = null;
	protected transient ProgressDialogListener fieldProgressDialogListenerEventMulticaster = null;
	private JButton cancelButton = null;

/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 6:08:36 PM)
 * @param owner java.awt.Frame
 */
public ProgressDialog(Frame owner) {
	super(owner);
	getCancelButton().addActionListener(new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getCancelButton()) {
				fireCancelButton_actionPerformed(new java.util.EventObject(this));
			}
		};
	});
}

public void setToVisible( ) {
	setVisible(true);
}


/**
 * 
 * @param newListener cbit.util.ProgressDialogListener
 */
public void addProgressDialogListener(ProgressDialogListener newListener) {
	fieldProgressDialogListenerEventMulticaster = ProgressDialogListenerEventMulticaster.add(fieldProgressDialogListenerEventMulticaster, newListener);
	return;
}

/**
 * Return the JProgressBar1 property value.
 * @return javax.swing.JProgressBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
protected javax.swing.JProgressBar getProgressBar() {
	if (progressBar == null) {
		progressBar = new javax.swing.JProgressBar();
		progressBar.setName("JProgressBar1");
		progressBar.setStringPainted(true);
	}
	return progressBar;
}

/**
 * 
 * @param newListener cbit.util.ProgressDialogListener
 */
public void removeProgressDialogListener(ProgressDialogListener newListener) {
	fieldProgressDialogListenerEventMulticaster = ProgressDialogListenerEventMulticaster.remove(fieldProgressDialogListenerEventMulticaster, newListener);
	return;
}

/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 1:06:46 PM)
 * @param message java.lang.String
 */
public abstract void setMessage(String message);


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 1:06:18 PM)
 * @param progress int
 */
public void setProgress(int progress) {
	getProgressBar().setValue(progress);
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 1:06:18 PM)
 * @param progress int
 */
void setProgressBarString(String progressString) {
	getProgressBar().setString(progressString);
}

/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
private void fireCancelButton_actionPerformed(java.util.EventObject newEvent) {
	if (fieldProgressDialogListenerEventMulticaster == null) {
		return;
	};
	fieldProgressDialogListenerEventMulticaster.cancelButton_actionPerformed(newEvent);
}

/**
 * Return the CancelButton property value.
 * @return javax.swing.JButton
 */
protected javax.swing.JButton getCancelButton() {
	if (cancelButton == null) {
		cancelButton = new javax.swing.JButton("Cancel");
		cancelButton.setVisible(true);
	}
	return cancelButton;
}

/**
 * Method generated to support the promotion of the cancelButtonVisible attribute.
 * @param arg1 boolean
 */
public void setCancelButtonVisible(boolean arg1) {
	getCancelButton().setVisible(arg1);
}

}

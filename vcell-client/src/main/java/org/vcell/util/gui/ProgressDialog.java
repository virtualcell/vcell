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
import java.awt.Container;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.apache.log4j.Logger;
import org.vcell.client.logicalwindow.LWContainerHandle;
import org.vcell.client.logicalwindow.LWDialog;
import org.vcell.util.ProgressDialogListener;

import cbit.vcell.client.desktop.DocumentWindow;
/**
 * Insert the type's description here.
 * Creation date: (5/18/2004 1:14:29 AM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public abstract class ProgressDialog extends LWDialog {
	
	protected final static int GRAPHIC_SIZE = 60;			// size of rotating "wait" image panel
	protected final static int DialogWidth = 350;
	protected final static int MaxLen = 50;					// message truncation if too long
	protected final static int TruncTailLen = 0;
	protected final static int TruncHeaderLen = MaxLen - (TruncTailLen + 2);
	protected final static Logger LG = Logger.getLogger(ProgressDialog.class);
	
	protected JProgressBar progressBar = null;
	protected transient ProgressDialogListener fieldProgressDialogListenerEventMulticaster = null;
	private JButton cancelButton = null;
	protected String message = "progress";

	// the thread will display a message on the status bar and then delete it after a few seconds
	protected static class StatusBarMessageThread implements Runnable {

		private final Object lock = new Object();
		private static volatile int instanceCount = 0;
		private final DocumentWindow dw;
		private final String s;
		
		public StatusBarMessageThread(DocumentWindow dw, String s) {
			synchronized(lock) {
				instanceCount++;
			}
			this.dw = dw;
			this.s = s;
		}
		public void run() {
			try {
				JLabel warningBar = dw.getWarningBar();
				warningBar.setText(s);
				Thread.sleep(4000);
				synchronized(lock) {
					if(instanceCount == 1) {
						warningBar.setText("");
					} else {
//						System.out.println("skipping");
					}
				}
			} catch ( Throwable th ) {
				throw new RuntimeException(th);
			} finally {
				synchronized(lock) {
					instanceCount--;
				}
			}
		}
	}
	
/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 6:08:36 PM)
 * @param owner java.awt.Frame
 */
public ProgressDialog(LWContainerHandle parent) {
	super(parent);
	getCancelButton().addActionListener(new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getCancelButton()) {
				fireCancelButton_actionPerformed(new java.util.EventObject(this));
			}
		};
	});
}

@Override
public String menuDescription() {
	return message; 
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

public final void setMessage(String message) {
	setMessageImpl(message);
}
protected abstract void setMessageImpl(String message);


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

protected DocumentWindow getMainFrame() {
	Container previousParent = null;
	Container parent = getParent();
	while(parent != null) {
		previousParent = parent;
		parent = parent.getParent();
	}
	if(previousParent != null && previousParent instanceof DocumentWindow) {
		DocumentWindow mainFrame = (DocumentWindow)previousParent;
		return mainFrame;
	} else {
		return null;
	}
}

}

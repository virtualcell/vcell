package org.vcell.util.gui;
import java.awt.*;

/**
 * Insert the type's description here.
 * Creation date: (5/19/2004 3:08:59 PM)
 * @author: Ion Moraru

Swing thread-safe progress popup window, showing message and progress.
Both message and progress can be updated at any time by some other thread.
If long working thread doesn't know it's progress, automatic scrolling progress will be generated.
Popup can be modal or not.

Typical usage:

	AsynchProgressPopup pp = new AsynchProgressPopup(
			requester,			// Component - GUI parent, if applicable; can be null
			title,				// String - window title; can be null
			message,			// String - inside message; can be null
			inputBlocking,		// boolean - whether or not it should be modal
			knowsProgress		// boolean - whether or not we'll know the progress and update it ourselves 
			);
	pp.start();
	{
		// do stuff that takes a while
		....
		// call pp.setMessage(String) to update message string while doing work
		// call pp.setProgress(int) to update progress while doing work, unless automatic mode was used to construct it (will ignore in that case)
		....
	}
	pp.stop();
 
 
 */
public class AsynchProgressPopup extends AsynchGuiUpdater {
	private ProgressDialog dialog = null;
	private int progress = 0;
	private int autoProgress = 0;
	private boolean inputBlocking = false;
	private boolean knowsProgress = false;

/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:11:01 PM)
 * @param title java.lang.String
 * @param message java.lang.String
 * @param inputBlocking boolean
 * @param knowsProgress boolean
 */
public AsynchProgressPopup(Component requester, String title, String message, boolean inputBlocking, boolean knowsProgress) {
	this(requester,title,message,inputBlocking,knowsProgress,false,null);
}

/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:11:01 PM)
 * @param title java.lang.String
 * @param message java.lang.String
 * @param inputBlocking boolean
 * @param knowsProgress boolean
 */
public AsynchProgressPopup(Component requester, String title, String message, boolean inputBlocking, boolean knowsProgress, boolean cancelable, ProgressDialogListener progressDialogListener) {
	// create customized dialog
	Frame owner = javax.swing.JOptionPane.getFrameForComponent(requester);
	dialog = new ProgressDialog(owner);
	if (cancelable && progressDialogListener!=null){
		dialog.setCancelButtonVisible(true);
		dialog.addProgressDialogListener(progressDialogListener);
	}else{
		dialog.setCancelButtonVisible(false);
	}
	dialog.setLocationRelativeTo(requester);
	dialog.setResizable(false);
	if (title != null) dialog.setTitle(title);
	if (message != null) dialog.setMessage(message);
	dialog.setModal(inputBlocking);
	// store mode of operation
	this.inputBlocking = inputBlocking;
	this.knowsProgress = knowsProgress;
	if (! knowsProgress) dialog.setProgressBarString("WORKING...");
}

/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:08:59 PM)
 */
protected void guiToDo() {
	if (knowsProgress) {
		dialog.setProgress(progress);
	} else {
		dialog.setProgress(autoProgress % 100);
		autoProgress += 5;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:08:59 PM)
 */
protected void guiToDo(Object params) {
	if (params instanceof String) {
		dialog.setMessage(params.toString());
	} else if (params instanceof Integer) {
		dialog.setProgress(((Integer)params).intValue());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:24:20 PM)
 * @param newMessage java.lang.String
 */
public void setMessage(String newMessage) {
	updateNow(newMessage);
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:27:21 PM)
 * @param progress int
 */
public void setProgress(int progress) {
	// ignore if in auto mode
	if (knowsProgress) {
		updateNow(new Integer(progress));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:28:39 PM)
 */
public void start() {
	dialog.show();
	// start timer for auto progress
	if (! knowsProgress) {
		super.start();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:28:39 PM)
 */
public void stop() {
	// stop timer for auto progress
	if (! knowsProgress) {
		super.stop();
	}
	dialog.dispose();
}
}
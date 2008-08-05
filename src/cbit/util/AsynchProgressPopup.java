package cbit.util;
import java.awt.*;

import javax.swing.SwingUtilities;

import cbit.gui.ZEnforcer;

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
public AsynchProgressPopup(final Component requester, final String title, final String message, final boolean inputBlocking, final boolean knowsProgress,final  boolean cancelable,final  ProgressDialogListener progressDialogListener) {
	
	new EventDispatchRunWithException (){
		public Object runWithException() throws Exception{
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
			AsynchProgressPopup.this.inputBlocking = inputBlocking;
			AsynchProgressPopup.this.knowsProgress = knowsProgress;
			if (! knowsProgress) dialog.setProgressBarString("WORKING...");
			return null;
		}
	}.runEventDispatchThreadSafelyWrapRuntime();
}

/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:08:59 PM)
 */
protected void guiToDo() {
	new EventDispatchRunWithException (){
		public Object runWithException() throws Exception{
			if (knowsProgress) {
				dialog.setProgress(progress);
			} else {
				dialog.setProgress(autoProgress % 100);
				autoProgress += 5;
			}
			return null;
		}
	}.runEventDispatchThreadSafelyWrapRuntime();

}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:08:59 PM)
 */
protected void guiToDo(final Object params) {
	new EventDispatchRunWithException (){
		public Object runWithException() throws Exception{
			if (params instanceof String) {
				dialog.setMessage(params.toString());
			} else if (params instanceof Integer) {
				dialog.setProgress(((Integer)params).intValue());
			}
			return null;
		}
	}.runEventDispatchThreadSafelyWrapRuntime();
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:24:20 PM)
 * @param newMessage java.lang.String
 */
public void setMessage(final String newMessage) {
	new EventDispatchRunWithException (){
		public Object runWithException() throws Exception{
			updateNow(newMessage);
			return null;
		}
	}.runEventDispatchThreadSafelyWrapRuntime();

}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:27:21 PM)
 * @param progress int
 */
public void setProgress(final int argProgress) {
	new EventDispatchRunWithException (){
		public Object runWithException() throws Exception{
			// ignore if in auto mode
			if (knowsProgress) {
				int localProgress = argProgress;
				if (argProgress > 100) {
					localProgress = 100;
				}
				updateNow(new Integer(localProgress));
				AsynchProgressPopup.this.progress = localProgress;
			}
			return null;
		}
	}.runEventDispatchThreadSafelyWrapRuntime();

}
/**
 * Insert the method's description here.
 * Creation date: (5/01/2008 5:46:18 PM)
 */
public int getProgress( ) {
	if (knowsProgress) {
		return progress;
	} else {
		return 0;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:28:39 PM)
 */
private void startPrivate(final boolean bKeepOnTop) {
	new EventDispatchRunWithException (){
		public Object runWithException() throws Exception{
			if(bKeepOnTop){
				SwingUtilities.invokeLater(new Runnable(){public void run(){ZEnforcer.showModalDialogOnTop(dialog);}});
			}else{
				SwingUtilities.invokeLater(new Runnable(){public void run(){dialog.setVisible(true);}});
			}
			// start timer for auto progress
			if (! knowsProgress) {
				AsynchProgressPopup.super.start();
			}
			return null;
		}
	}.runEventDispatchThreadSafelyWrapRuntime();
}

public void start() {
	new EventDispatchRunWithException (){
		public Object runWithException() throws Exception{
			startPrivate(false);
			return null;
		}
	}.runEventDispatchThreadSafelyWrapRuntime();
}

public void startKeepOnTop() {
	new EventDispatchRunWithException (){
		public Object runWithException() throws Exception{
			startPrivate(true);
			return null;
		}
	}.runEventDispatchThreadSafelyWrapRuntime();
}

/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:28:39 PM)
 */
public void stop() {
	new EventDispatchRunWithException (){
		public Object runWithException() throws Exception{
			// stop timer for auto progress
			if (! knowsProgress) {
				AsynchProgressPopup.super.stop();
			}
			dialog.dispose();
			return null;
		}
	}.runEventDispatchThreadSafelyWrapRuntime();
}
}
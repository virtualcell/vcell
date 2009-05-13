package cbit.vcell.client.task;
import java.awt.*;
import java.util.*;
import swingthreads.*;
import javax.swing.*;

import org.vcell.util.BeanUtils;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.AsynchProgressPopup;
import org.vcell.util.gui.ProgressDialogListener;

import cbit.vcell.client.*;
/**
 * Insert the type's description here.
 * Creation date: (5/28/2004 2:44:22 AM)
 * @author: Ion Moraru
 */
public class ClientTaskDispatcher {
//	public static final String PROGRESS_POPUP = "asynchProgressPopup";
//	public static final String TASK_PROGRESS_INTERVAL = "progressRange";
	public static final String TASK_ABORTED_BY_ERROR = "abort";
	public static final String TASK_ABORTED_BY_USER = "cancel";
	public static final String TASKS_TO_BE_SKIPPED = "conditionalSkip";
	private static boolean bInProgress = false;

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 5:37:06 PM)
 * @param tasks cbit.vcell.desktop.controls.ClientTask[]
 */
public static void dispatch(final Component requester, final Hashtable<String, Object> hash, final AsynchClientTask[] tasks) {
	dispatch(requester,hash,tasks,false, false, false, null);
}
	
public static void dispatch(final Component requester, final Hashtable<String, Object> hash, final AsynchClientTask[] tasks, final boolean bKnowProgress) {
	dispatch(requester,hash,tasks, bKnowProgress,false, null);
}

public static void dispatch(final Component requester, final Hashtable<String, Object> hash, final AsynchClientTask[] tasks, final boolean bKnowProgress, 
		final boolean cancelable, final ProgressDialogListener progressDialogListener) {
	dispatch(requester,hash,tasks, true, bKnowProgress, cancelable, progressDialogListener);
}
/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 5:37:06 PM)
 * @param tasks cbit.vcell.desktop.controls.ClientTask[]
 */
public static void dispatch(final Component requester, final Hashtable<String, Object> hash, final AsynchClientTask[] tasks, 
		final boolean bShowProgressPopup, final boolean bKnowProgress, final boolean cancelable, final ProgressDialogListener progressDialogListener) {
	// check tasks - swing non-blocking can be only at the end
//	if (bInProgress) {
//		Thread.dumpStack();
//	}	
	for (int i = 0; i < tasks.length; i++){
		if (tasks[i].getTaskType() == AsynchClientTask.TASKTYPE_SWING_NONBLOCKING && i < tasks.length - 1) {
			throw new RuntimeException("SWING_NONBLOCKING task only permitted as last task");
		}
	}
	// dispatch tasks to a new worker
	SwingWorker worker = new SwingWorker() {
		private AsynchProgressPopup pp = null;
		public Object construct() {
			bInProgress = true;
			if (bShowProgressPopup) {
				pp = new AsynchProgressPopup(requester, "WORKING...", "Initializing request", false, bKnowProgress, cancelable, progressDialogListener);			
				pp.start();
			}
			for (int i = 0; i < tasks.length; i++){
				// run all tasks
				// after abort, run only non-skippable tasks
				// also skip selected tasks specified by conditionalSkip tag 
				final AsynchClientTask currentTask = tasks[i];
				currentTask.setClientTaskStatusSupport(pp);
				
//System.out.println("DISPATCHING: "+currentTask.getTaskName()+" at "+ new Date(System.currentTimeMillis()));
				if (pp != null) {
					pp.setProgress(i*100/tasks.length); // beginning of task
					pp.setMessage(currentTask.getTaskName());
				}
				boolean shouldRun = true;
				if (hash.containsKey(TASK_ABORTED_BY_ERROR) && tasks[i].skipIfAbort()) {
					shouldRun = false;
				}
				if (hash.containsKey(TASKS_TO_BE_SKIPPED)) {
					String[] toSkip = (String[])hash.get(TASKS_TO_BE_SKIPPED);
					if (BeanUtils.arrayContains(toSkip, tasks[i].getClass().getName())) {
						shouldRun = false;
					}
				}
				if (hash.containsKey(TASK_ABORTED_BY_USER)) {
					UserCancelException exc = (UserCancelException)hash.get(TASK_ABORTED_BY_USER);
					if (tasks[i].skipIfCancel(exc)) {
						shouldRun = false;
					}
				}
				if (shouldRun) {
					try {
						if (currentTask.getTaskType() == AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
							currentTask.run(hash);
						} else if (currentTask.getTaskType() == AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
							SwingUtilities.invokeAndWait(new Runnable() {
								public void run() {
									try {
										currentTask.run(hash);
									} catch (Throwable exc) {
										recordException(exc, hash);
									}
								}
							});
						} else if (currentTask.getTaskType() == AsynchClientTask.TASKTYPE_SWING_NONBLOCKING) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									try {
										currentTask.run(hash);
									} catch (Throwable exc) {
										recordException(exc, hash);
									}
								}
							});
						}
					} catch (Throwable exc) {
						recordException(exc, hash);
					}
				}
			}
			return hash;
		}
		public void finished() {
//System.out.println("DISPATCHING: finished() called at "+ new Date(System.currentTimeMillis()));
			if (pp != null) {
				pp.stop();
			}
			if (hash.containsKey(TASK_ABORTED_BY_ERROR)) {
				// something went wrong
				Throwable e = (Throwable)hash.get(TASK_ABORTED_BY_ERROR);
				String msg = e.getMessage();
				if(msg == null || msg.length() == 0)
				{
					msg = "Exception: "+e.toString();
				}
				if(e.getCause() != null && e.getCause().getMessage() != null && e.getCause().getMessage().length()>0){
					msg+="\n"+e.getCause().getMessage();
				}
				PopupGenerator.showErrorDialog(requester, msg);
			} else if (hash.containsKey(TASK_ABORTED_BY_USER)) {
				// depending on where user canceled we might want to automatically start a new job
				dispatchFollowUp(hash);
			}
			bInProgress = false;
//System.out.println("DISPATCHING: done at "+ new Date(System.currentTimeMillis()));
		}
	};
	worker.start();
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 8:58:38 PM)
 * @param hash java.util.Hashtable
 */
private static void dispatchFollowUp(Hashtable<String, Object> hash) {
	//
	// we deal with a task dispatch that aborted due to some user choice on prompts
	//
	UserCancelException e = (UserCancelException)hash.get("cancel");
	if (e == UserCancelException.CHOOSE_SAVE_AS) {
		// user chose to save as during a save or save/edition of a documnet found to be unchanged
		((DocumentWindowManager)hash.get("documentWindowManager")).saveDocumentAsNew();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 2:17:22 AM)
 * @param exc java.lang.Exception
 */
public static void recordException(Throwable exc, Hashtable<String, Object> hash) {
	if (exc instanceof UserCancelException) {
		hash.put(TASK_ABORTED_BY_USER, exc);
	} else {
		exc.printStackTrace(System.out);
		hash.put(TASK_ABORTED_BY_ERROR, exc);
	}
}
}
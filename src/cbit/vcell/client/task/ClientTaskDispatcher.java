package cbit.vcell.client.task;
import cbit.vcell.client.desktop.*;
import java.awt.*;
import java.util.*;
import cbit.vcell.desktop.controls.*;
import swingthreads.*;
import cbit.util.*;
import javax.swing.*;

import org.vcell.util.BeanUtils;
import org.vcell.util.Range;

import cbit.vcell.client.*;
/**
 * Insert the type's description here.
 * Creation date: (5/28/2004 2:44:22 AM)
 * @author: Ion Moraru
 */
public class ClientTaskDispatcher {
	public static final String PROGRESS_POPUP = "asynchProgressPopup";
	public static final String TASK_PROGRESS_INTERVAL = "progressRange";
	public static final String TASK_ABORTED_BY_ERROR = "abort";
	public static final String TASK_ABORTED_BY_USER = "cancel";
	public static final String TASKS_TO_BE_SKIPPED = "conditionalSkip";

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 5:37:06 PM)
 * @param tasks cbit.vcell.desktop.controls.ClientTask[]
 */
public static void dispatch(final Component requester, final Hashtable hash, final AsynchClientTask[] tasks, final boolean useTaskProgress) {
	dispatch(requester,hash,tasks,useTaskProgress,false,null);
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 5:37:06 PM)
 * @param tasks cbit.vcell.desktop.controls.ClientTask[]
 */
public static void dispatch(final Component requester, final Hashtable hash, final AsynchClientTask[] tasks, final boolean useTaskProgress, final boolean cancelable, final ProgressDialogListener progressDialogListener) {
	// check tasks - swing non-blocking can be only at the end
	for (int i = 0; i < tasks.length; i++){
		if (tasks[i].getTaskType() == ClientTask.TASKTYPE_SWING_NONBLOCKING && i < tasks.length - 1) {
			throw new RuntimeException("SWING_NONBLOCKING task only permitted as last task");
		}
	}
	// dispatch tasks to a new worker
	SwingWorker worker = new SwingWorker() {
		private AsynchProgressPopup pp = new AsynchProgressPopup(requester, "WORKING...", "Initializing request", false, useTaskProgress, cancelable, progressDialogListener);
		public Object construct() {
			if (useTaskProgress) {
				// make AsynchProgressPopup available for finer granularity progress update by individual ClientTasks
				hash.put(PROGRESS_POPUP, pp);
			}
			pp.start();
			for (int i = 0; i < tasks.length; i++){
				// run all tasks
				// after abort, run only non-skippable tasks
				// also skip selected tasks specified by conditionalSkip tag 
				final AsynchClientTask currentTask = tasks[i];
//System.out.println("DISPATCHING: "+currentTask.getTaskName()+" at "+ new Date(System.currentTimeMillis()));
				if (useTaskProgress) {
					// update Hash with current interval for Progress
					hash.put(TASK_PROGRESS_INTERVAL, new Range(i*100/tasks.length, (i+1)*100/tasks.length));
				}
				pp.setProgress(i*100/tasks.length); // beginning of task
				pp.setMessage(currentTask.getTaskName());
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
						if (currentTask.getTaskType() == ClientTask.TASKTYPE_NONSWING_BLOCKING) {
							currentTask.run(hash);
						} else if (currentTask.getTaskType() == ClientTask.TASKTYPE_SWING_BLOCKING) {
							SwingUtilities.invokeAndWait(new Runnable() {
								public void run() {
									try {
										currentTask.run(hash);
									} catch (Throwable exc) {
											recordException(exc, hash);
									}
								}
							});
						} else if (currentTask.getTaskType() == ClientTask.TASKTYPE_SWING_NONBLOCKING) {
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
			pp.stop();
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
private static void dispatchFollowUp(Hashtable hash) {
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
public static void recordException(Throwable exc, Hashtable hash) {
	if (exc instanceof UserCancelException) {
		hash.put(TASK_ABORTED_BY_USER, exc);
	} else {
		exc.printStackTrace(System.out);
		hash.put(TASK_ABORTED_BY_ERROR, exc);
	}
}
}
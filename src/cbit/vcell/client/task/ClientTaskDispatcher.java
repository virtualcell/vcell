/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.task;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import javax.swing.FocusManager;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.vcell.util.BeanUtils;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.AsynchProgressPopup;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ProgressDialog;

import swingthreads.SwingWorker;
import cbit.vcell.client.ClientMDIManager;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.simdata.PDEDataContext;
/**
 * Insert the type's description here.
 * Creation date: (5/28/2004 2:44:22 AM)
 * @author: Ion Moraru
 */
public class ClientTaskDispatcher {
	public static final String TASK_ABORTED_BY_ERROR = "abort";
	public static final String TASK_ABORTED_BY_USER = "cancel";
	public static final String TASKS_TO_BE_SKIPPED = "conditionalSkip";
	private static final Logger lg = Logger.getLogger(ClientTaskDispatcher.class);
	/**
	 * used to count / generate thread names
	 */
	private static long serial = 0;
	/**
	 * set of all scheduled tasks; 
	 */
	private static final Set<AsynchClientTask> allTasks; 
	
	static {
		WeakHashMap<AsynchClientTask, Boolean> whm = new WeakHashMap<AsynchClientTask,Boolean>( );
		allTasks = Collections.synchronizedSet( Collections.newSetFromMap(whm) );
	}	

/**
 * don't show popup.
 * Creation date: (5/31/2004 5:37:06 PM)
 * @param tasks cbit.vcell.desktop.controls.ClientTask[]
 */
public static void dispatch(Component requester, Hashtable<String, Object> hash, AsynchClientTask[] tasks) {
	dispatch(requester,hash,tasks,false, false, false, null, false);
}

public static void dispatch(Component requester, Hashtable<String, Object> hash, AsynchClientTask[] tasks, boolean bKnowProgress) {
	dispatch(requester,hash,tasks, true, bKnowProgress, false, null, false);
}

public static void dispatch(Component requester, Hashtable<String, Object> hash, AsynchClientTask[] tasks, boolean bKnowProgress, 
		boolean cancelable, ProgressDialogListener progressDialogListener) {
	dispatch(requester,hash,tasks, true, bKnowProgress, cancelable, progressDialogListener, false);
}

public static void dispatch(Component requester, Hashtable<String, Object> hash, AsynchClientTask[] tasks, boolean bKnowProgress, 
		boolean cancelable, ProgressDialogListener progressDialogListener, boolean bInputBlocking) {
	dispatch(requester,hash,tasks, true, bKnowProgress, cancelable, progressDialogListener, bInputBlocking);
}

public static void dispatch(final Component requester, final Hashtable<String, Object> hash, final AsynchClientTask[] tasks,
		final boolean bShowProgressPopup, final boolean bKnowProgress, final boolean cancelable, final ProgressDialogListener progressDialogListener, final boolean bInputBlocking) {
	dispatch(requester, hash, tasks, null, bShowProgressPopup, bKnowProgress, cancelable, progressDialogListener, bInputBlocking);
}

private static int entryCounter = 0;
public static boolean isBusy(){
	if(entryCounter>0){
//		System.out.println("----------Busy----------");
		return true;
	}
	return false;
}

public static class BlockingTimer extends Timer{
	private static AsynchProgressPopup pp;
	private static ArrayList<BlockingTimer> allBlockingTimers = new ArrayList<>();
	private static Timer ppStop = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized (allBlockingTimers) {
				if(pp != null && allBlockingTimers.size() == 0){
//					System.out.println("stopping "+System.currentTimeMillis());
					pp.stop();
					pp = null;
				}
				ppStop.stop();
			}
		}
	});
	private static ProgressDialogListener cancelListener = new ProgressDialogListener() {
		@Override
		public void cancelButton_actionPerformed(EventObject newEvent) {
			synchronized (allBlockingTimers) {
				while(allBlockingTimers.size() > 0){
					allBlockingTimers.remove(0);
				}
				ppStop.restart();
			}
		}
	};
	private static class ALHelper implements ActionListener {
		private ActionListener argActionListener;
		private BlockingTimer argBlockingTimer;
		public ALHelper(ActionListener argActionListener) {
			super();
			this.argActionListener = argActionListener;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String actionMessage = null;
			synchronized (allBlockingTimers) {
				//if I'm next in the list do my action else repeat timer
				if(BlockingTimer.allBlockingTimers.get(0) == argBlockingTimer){
					actionMessage = BlockingTimer.allBlockingTimers.get(0).getMessage();
				}else{
					if(allBlockingTimers.contains(argBlockingTimer)){
						argBlockingTimer.start();
					}
				}
			}
			if(actionMessage != null){
				if(pp != null){
					pp.setMessage(actionMessage);
				}
				argActionListener.actionPerformed(e);

			}
		}
		public void setBlockingTimer(BlockingTimer argBlockingTimer){
			this.argBlockingTimer = argBlockingTimer;
		}
	}
	private String message;
	public BlockingTimer(Component requester,int delay,String message){
		super(delay, new ALHelper(null));
		((ALHelper)getActionListeners()[0]).setBlockingTimer(this);
		ppStop.setRepeats(false);
		setRepeats(false);
		if(pp == null){
			pp = new AsynchProgressPopup(requester, "Waiting for actions to finish...", "busy...", null, true, false,true,cancelListener);
			pp.startKeepOnTop();
		}
		synchronized (allBlockingTimers) {
			allBlockingTimers.add(this);
		}
	}
	public String getMessage(){
		return message;
	}
	@Override
	public void start() {
		// TODO Auto-generated method stub
		super.start();
	}
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		super.stop();
		synchronized (allBlockingTimers) {
//			if(allBlockingTimers.size()>0 && allBlockingTimers.get(0) != BlockingTimer.this){
//				System.err.println("Unexpected position of stopped Blockingtimer, not beginning of list");
//			}
			allBlockingTimers.remove(BlockingTimer.this);
//			System.out.println("starting stop "+System.currentTimeMillis());
			ppStop.restart();
		}
	}
	private static void replace(BlockingTimer replaceThis, BlockingTimer withThis){
		synchronized (allBlockingTimers) {
			replaceThis.stop();
			allBlockingTimers.add(withThis);
		}
	}
}
public static boolean isBusy(PDEDataContext busyPDEDatacontext1,PDEDataContext busyPDEDatacontext2){
	return ClientTaskDispatcher.isBusy() || (busyPDEDatacontext1 != null && busyPDEDatacontext1.isBusy()) || (busyPDEDatacontext2 != null && busyPDEDatacontext2.isBusy());
}
public static BlockingTimer getBlockingTimer(Component requester,PDEDataContext busyPDEDatacontext1,PDEDataContext busyPDEDatacontext2,BlockingTimer activeTimerOld,ActionListener actionListener,String actionMessage){
	if(actionMessage == null){
		actionMessage = "no message...";
	}
	if(isBusy(busyPDEDatacontext1,busyPDEDatacontext2)){
		BlockingTimer activeTimerNew = new BlockingTimer(requester,200,actionMessage);
		if(activeTimerOld != null){
			BlockingTimer.replace(activeTimerOld, activeTimerNew);
		}
		ActionListener[] currentActionlListeners = activeTimerNew.getActionListeners();
		for (int i = 0; i < currentActionlListeners.length; i++) {
			activeTimerNew.removeActionListener(currentActionlListeners[i]);			
		}
		activeTimerNew.addActionListener(actionListener);
//		if(activeTimerNew.isRunning()){
//			System.err.println("----------getBlockingTimer: single fire timer should not be running");
//		}
		activeTimerNew.restart();
		return activeTimerNew;		
	}else if(activeTimerOld != null){
		activeTimerOld.stop();
		activeTimerOld = null;
	}
	return null;
}

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 5:37:06 PM)
 * @param tasks cbit.vcell.desktop.controls.ClientTask[]
 */
public static void dispatch(final Component requester, final Hashtable<String, Object> hash, final AsynchClientTask[] tasks, final ProgressDialog customDialog,
		final boolean bShowProgressPopup, final boolean bKnowProgress, final boolean cancelable, final ProgressDialogListener progressDialogListener, final boolean bInputBlocking) {
	// check tasks - swing non-blocking can be only at the end
	entryCounter++;
	if(entryCounter>1){
		System.out.println("Reentrant");
	}
	
	if (bShowProgressPopup && requester == null) {
		System.out.println("ClientTaskDispatcher.dispatch(), requester is null, dialog has no parent, please try best to fix it!!!");
		Thread.dumpStack();
	}
		
	final List<AsynchClientTask> taskList = new ArrayList<AsynchClientTask>();
	
	for (int i = 0; i < tasks.length; i++){
		if (tasks[i].getTaskType() == AsynchClientTask.TASKTYPE_SWING_NONBLOCKING && i < tasks.length - 1) {
			throw new RuntimeException("SWING_NONBLOCKING task only permitted as last task");
		}
		taskList.add(tasks[i]);
		if (lg.isDebugEnabled()) {
			lg.debug("added task name " + tasks[i].getTaskName());
		}
	}
	final String threadBaseName = "ClientTaskDispatcher " + ( serial++ )  + ' ';
	// dispatch tasks to a new worker
	SwingWorker worker = new SwingWorker() {
		private AsynchProgressPopup pp = null;
		private Window windowParent = null;
		private Component focusOwner = null;
		public Object construct() {
			if (bShowProgressPopup) {
				if (customDialog == null) {
					pp = new AsynchProgressPopup(requester, "WORKING...", "Initializing request", Thread.currentThread(), bInputBlocking, bKnowProgress, cancelable, progressDialogListener);
				} else {
					pp = new AsynchProgressPopup(requester, customDialog, Thread.currentThread(), bInputBlocking, bKnowProgress, cancelable, progressDialogListener);
				}
				if (bInputBlocking) {
					pp.startKeepOnTop();
				} else {
					pp.start();
				}
			}
			if (requester != null) {
				windowParent = GuiUtils.getWindowForComponent(requester);				
			}
			try {
				if (windowParent != null) {
					focusOwner = FocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							ClientMDIManager.blockWindow(windowParent);
							windowParent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						}
					});
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < taskList.size(); i++){
				// run all tasks
				// after abort, run only non-skippable tasks
				// also skip selected tasks specified by conditionalSkip tag 
				final AsynchClientTask currentTask = taskList.get(i);
				try {
					currentTask.setClientTaskStatusSupport(pp);
					setSwingWorkerThreadName(this,threadBaseName + currentTask.getTaskName());

					//System.out.println("DISPATCHING: "+currentTask.getTaskName()+" at "+ new Date(System.currentTimeMillis()));
					if (pp != null ) {
						pp.setVisible(currentTask.showProgressPopup());
						if(!bKnowProgress)
						{
							pp.setProgress(i*100/taskList.size()); // beginning of task
						}
						pp.setMessage(currentTask.getTaskName());
					}
					boolean shouldRun = true;
					if (hash.containsKey(TASK_ABORTED_BY_ERROR) && currentTask.skipIfAbort()) {
						shouldRun = false;
					}
					if (hash.containsKey(TASKS_TO_BE_SKIPPED)) {
						String[] toSkip = (String[])hash.get(TASKS_TO_BE_SKIPPED);
						if (BeanUtils.arrayContains(toSkip, currentTask.getClass().getName())) {
							shouldRun = false;
						}
					}
					if (pp != null && pp.isInterrupted()) {
						recordException(UserCancelException.CANCEL_GENERIC, hash);
					}

					if (hash.containsKey(TASK_ABORTED_BY_USER)) {
						UserCancelException exc = (UserCancelException)hash.get(TASK_ABORTED_BY_USER);
						if (currentTask.skipIfCancel(exc)) {
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
					//				AsynchClientTask[] followupTasks = currentTask.getFollowupTasks();
					//				if (followupTasks != null) {
					//					for (int j = 0; j < followupTasks.length; j++) {
					//						taskList.add(i+j+1, followupTasks[j]);
					//					}					
					//				}
				}
				finally {
					allTasks.remove(currentTask);
				}
			}
			return hash;
		}
		public void finished() {
//System.out.println("DISPATCHING: finished() called at "+ new Date(System.currentTimeMillis()));
			entryCounter--;
//			System.out.println("----------Leave----------entryCounter="+entryCounter);
			
			if (pp != null) {
				pp.stop();
			}
			if (hash.containsKey(TASK_ABORTED_BY_ERROR)) {
				// something went wrong
				StringBuffer allCausesErrorMessageSB = new StringBuffer();
				Throwable causeError = (Throwable)hash.get(TASK_ABORTED_BY_ERROR);
				do{
					allCausesErrorMessageSB.append(causeError.getClass().getSimpleName()+"-"+(causeError.getMessage()==null || causeError.getMessage().length()==0?"":causeError.getMessage()));
					allCausesErrorMessageSB.append("\n");
				}while((causeError = causeError.getCause()) != null);
				if (requester == null) {
					System.out.println("ClientTaskDispatcher.dispatch(), requester is null, dialog has no parent, please try best to fix it!!!");
					Thread.dumpStack();
				}
				PopupGenerator.showErrorDialog(requester, allCausesErrorMessageSB.toString(), (Throwable)hash.get(TASK_ABORTED_BY_ERROR));
			} else if (hash.containsKey(TASK_ABORTED_BY_USER)) {
				// depending on where user canceled we might want to automatically start a new job
				dispatchFollowUp(hash);
			}
			if (windowParent != null) {
				ClientMDIManager.unBlockWindow(windowParent);
				windowParent.setCursor(Cursor.getDefaultCursor());
				if (focusOwner != null) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							try {
								windowParent.requestFocusInWindow();
								focusOwner.requestFocusInWindow();
							} catch (Throwable exc) {
								recordException(exc, hash);
							}
						}
					});
				}
			}
//			BeanUtils.setCursorThroughout(frameParent, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//System.out.println("DISPATCHING: done at "+ new Date(System.currentTimeMillis()));
		}
	};
	setSwingWorkerThreadName(worker,threadBaseName); 
	allTasks.addAll(taskList);
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

/**
 * @return list of outstanding tasks, or empty set if none
 */
public static Collection<String> outstandingTasks( ) {
	if (allTasks.isEmpty()) {
		return Collections.emptyList();
	}
	
	synchronized(allTasks) {
		List<String> taskNames = new ArrayList<>();
		for (AsynchClientTask ct : allTasks) { 
			String tn = ct.getTaskName();
			taskNames.add(tn);
		}
		return taskNames;
	}
}

/**
 * @return true if there are uncompleted tasks
 */
public static boolean hasOutstandingTasks( ) {
	return !allTasks.isEmpty();
}

/**
 * set {@link SwingWorker} thread name
 * @param sw
 * @param name may not be null
 * @throws IllegalArgumentException if name is null
 */
private static void setSwingWorkerThreadName(SwingWorker sw, String name) {
	if (name != null ) {
		try {
			Field threadVarField = SwingWorker.class.getDeclaredField("threadVar");
			threadVarField.setAccessible(true);
			Object threadVar = threadVarField.get(sw);
			Field threadField = threadVar.getClass().getDeclaredField("thread");
			threadField.setAccessible(true);
			Thread thread = (Thread) threadField.get(threadVar);	
			thread.setName(name);
		}
		catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
			lg.warn("setSwingWorkerName fail", e);
		}
		return;
	}
	throw new IllegalArgumentException("name may not be null");
}

}

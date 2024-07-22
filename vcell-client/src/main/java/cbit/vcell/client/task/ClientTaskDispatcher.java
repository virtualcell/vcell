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

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.vcell.util.*;
import org.vcell.util.gui.AsynchProgressPopup;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ProgressDialog;

import cbit.vcell.client.ClientMDIManager;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.task.AsynchClientTask.KeyInfo;
import cbit.vcell.simdata.PDEDataContext;
import swingthreads.SwingWorker;

/**
 * Insert the type's description here.
 * Creation date: (5/28/2004 2:44:22 AM)
 *
 * @author: Ion Moraru
 */
public class ClientTaskDispatcher {
    //	public static final String PROGRESS_POPUP = "asynchProgressPopup";
//	public static final String TASK_PROGRESS_INTERVAL = "progressRange";
    public static final String TASK_ABORTED_BY_ERROR = "abort";
    public static final String TASK_ABORTED_BY_USER = "cancel";
    public static final String TASKS_TO_BE_SKIPPED = "conditionalSkip";
    /**
     * hash key to internally flag problem with hash table data
     */
    private static final String HASH_DATA_ERROR = "hdeHdeHde";
    /**
     * hash key to store stack trace if {@link #lg} enabled for INFO
     */
    private static final String STACK_TRACE_ARRAY = "clientTaskDispatcherStackTraceArray";
    private static final Logger lg = LogManager.getLogger(ClientTaskDispatcher.class);
    /**
     * used to count / generate thread names
     */
    private static long serial = 0;
    /**
     * set of all scheduled tasks; used to avoid calling System.exit() prematurely on Application exit
     */
    private static final Set<AsynchClientTask> allTasks;
    /**
     * hash key for final window
     */
    private static final String FINAL_WINDOW = "finalWindowInterface";

    static {
        WeakHashMap<AsynchClientTask, Boolean> whm = new WeakHashMap<AsynchClientTask, Boolean>();
        allTasks = Collections.synchronizedSet(Collections.newSetFromMap(whm));
    }


    /**
     * don't show popup.
     * Creation date: (5/31/2004 5:37:06 PM)
     *
     * @param tasks cbit.vcell.desktop.controls.ClientTask[]
     */
    public static void dispatch(Component requester, Hashtable<String, Object> hash, AsynchClientTask[] tasks) {
        ClientTaskDispatcher.dispatch(requester, hash, tasks, false, false, false, null, false);
    }

    public static void dispatch(Component requester, Hashtable<String, Object> hash, AsynchClientTask[] tasks, boolean bKnowProgress) {
        ClientTaskDispatcher.dispatch(requester, hash, tasks, true, bKnowProgress, false, null, false);
    }

    public static void dispatch(Component requester, Hashtable<String, Object> hash, AsynchClientTask[] tasks, boolean bKnowProgress,
                                boolean cancelable, ProgressDialogListener progressDialogListener) {
        ClientTaskDispatcher.dispatch(requester, hash, tasks, true, bKnowProgress, cancelable, progressDialogListener, false);
    }

    public static void dispatch(Component requester, Hashtable<String, Object> hash, AsynchClientTask[] tasks, boolean bKnowProgress,
                                boolean cancelable, ProgressDialogListener progressDialogListener, boolean bInputBlocking) {
        ClientTaskDispatcher.dispatch(requester, hash, tasks, true, bKnowProgress, cancelable, progressDialogListener, bInputBlocking);
    }

    public static void dispatch(final Component requester, final Hashtable<String, Object> hash, final AsynchClientTask[] tasks,
                                final boolean bShowProgressPopup, final boolean bKnowProgress, final boolean cancelable, final ProgressDialogListener progressDialogListener, final boolean bInputBlocking) {
        ClientTaskDispatcher.dispatch(requester, hash, tasks, null, bShowProgressPopup, bKnowProgress, cancelable, progressDialogListener, bInputBlocking);
    }

    private static int entryCounter = 0;

    public static boolean isBusy() {
        //		System.out.println("----------Busy----------");
        return entryCounter > 0;
    }

    public static class BlockingTimer extends Timer {
        private static AsynchProgressPopup pp;
        private static final ArrayList<BlockingTimer> allBlockingTimers = new ArrayList<>();
        private static final Timer ppStop = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (allBlockingTimers) {
                    if (pp != null && allBlockingTimers.isEmpty()) {
//					System.out.println("stopping "+System.currentTimeMillis());
                        pp.stop();
                        pp = null;
                    }
                    ppStop.stop();
                }
            }
        });
        private static final ProgressDialogListener cancelListener = new ProgressDialogListener() {
            @Override
            public void cancelButton_actionPerformed(EventObject newEvent) {
                synchronized (allBlockingTimers) {
                    while (!allBlockingTimers.isEmpty()) {
                        allBlockingTimers.remove(0);
                    }
                    ppStop.restart();
                }
            }
        };

        private static class ALHelper implements ActionListener {
            private final ActionListener argActionListener;
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
                    if (BlockingTimer.allBlockingTimers.get(0) == this.argBlockingTimer) {
                        actionMessage = BlockingTimer.allBlockingTimers.get(0).getMessage();
                    } else {
                        if (allBlockingTimers.contains(this.argBlockingTimer)) {
                            this.argBlockingTimer.start();
                        }
                    }
                }
                if (actionMessage != null) {
                    if (pp != null) {
                        pp.setMessage(actionMessage);
                    }
                    this.argActionListener.actionPerformed(e);

                }
            }

            public void setBlockingTimer(BlockingTimer argBlockingTimer) {
                this.argBlockingTimer = argBlockingTimer;
            }
        }

        private String message;

        public BlockingTimer(Component requester, int delay, String message) {
            super(delay, new ALHelper(null));
            ((ALHelper) this.getActionListeners()[0]).setBlockingTimer(this);
            ppStop.setRepeats(false);
            this.setRepeats(false);
            if (pp == null) {
                pp = new AsynchProgressPopup(requester, "Waiting for actions to finish...", message, null, true, false, true, cancelListener);
                pp.startKeepOnTop();
            }
            synchronized (allBlockingTimers) {
                allBlockingTimers.add(this);
            }
        }

        public String getMessage() {
            return this.message;
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

        private static void replace(BlockingTimer replaceThis, BlockingTimer withThis) {
            synchronized (allBlockingTimers) {
                replaceThis.stop();
                allBlockingTimers.add(withThis);
            }
        }
    }

    public static boolean isBusy(PDEDataContext busyPDEDatacontext1, PDEDataContext busyPDEDatacontext2) {
        return ClientTaskDispatcher.isBusy() || (busyPDEDatacontext1 != null && busyPDEDatacontext1.isBusy()) || (busyPDEDatacontext2 != null && busyPDEDatacontext2.isBusy());
    }

    public static BlockingTimer getBlockingTimer(Component requester, PDEDataContext busyPDEDatacontext1, PDEDataContext busyPDEDatacontext2, BlockingTimer activeTimerOld, ActionListener actionListener, String actionMessage) {
        if (actionMessage == null) {
            actionMessage = "no message...";
        }
        if (isBusy(busyPDEDatacontext1, busyPDEDatacontext2)) {
            BlockingTimer activeTimerNew = new BlockingTimer(requester, 200, actionMessage);
            if (activeTimerOld != null) {
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
        } else if (activeTimerOld != null) {
            activeTimerOld.stop();
            activeTimerOld = null;
        }
        return null;
    }

    public enum StopStrategy {
        THREAD_INTERRUPT,
        @Deprecated THREAD_KILL
    }

    public static void dispatch(final Component requester, final Hashtable<String, Object> hash, final AsynchClientTask[] tasks,
                                final ProgressDialog customDialog, final boolean bShowProgressPopup, final boolean bKnowProgress,
                                final boolean cancelable, final ProgressDialogListener progressDialogListener,
                                final boolean bInputBlocking) {
        ClientTaskDispatcher.dispatch(requester, hash, tasks, customDialog, bShowProgressPopup, bKnowProgress, cancelable, progressDialogListener,
                bInputBlocking, StopStrategy.THREAD_INTERRUPT);
    }


    public static void dispatch(final Component requester, final Hashtable<String, Object> hash, final AsynchClientTask[] tasks,
                                final ProgressDialog customDialog, final boolean bShowProgressPopup, final boolean bKnowProgress,
                                final boolean cancelable, final ProgressDialogListener progressDialogListener,
                                final boolean bInputBlocking, final StopStrategy stopStrategy) {
		final String threadBaseName = "ClientTaskDispatcher " + (serial++) + ' ';
		final List<AsynchClientTask> taskList = new ArrayList<>();

        // check tasks - swing non-blocking can be only at the end
        ClientTaskDispatcher.entryCounter++;
        if (ClientTaskDispatcher.entryCounter > 1) lg.debug("Reentrant: {}", ClientTaskDispatcher.entryCounter);

		//	if (bInProgress) {
		//		Thread.dumpStack();
		//	}
        if (lg.isInfoEnabled()) hash.put(STACK_TRACE_ARRAY, Thread.currentThread().getStackTrace());

        if (bShowProgressPopup && requester == null) {
            System.out.println("ClientTaskDispatcher.dispatch(), requester is null, dialog has no parent, please try best to fix it!!!");
            Thread.dumpStack();
        }

		for (AsynchClientTask task : tasks){
			if (AsynchClientTask.TASKTYPE_SWING_NONBLOCKING== task.getTaskType() && taskList.size() + 1 != tasks.length) {
				throw new RuntimeException("SWING_NONBLOCKING task only permitted as last task");
			}
			taskList.add(task);
			lg.debug("added task name {}", task.getTaskName());
		}

        // dispatch tasks to a new worker
        SwingWorker worker = new SwingWorker() {
            private AsynchProgressPopup pp = null;
            private Window windowParent = null;
            private Component focusOwner = null;

            public Object construct() {
                if (bShowProgressPopup) {
                    if (customDialog == null) {
                        this.pp = new AsynchProgressPopup(requester, "WORKING...", "Initializing request", Thread.currentThread(), bInputBlocking, bKnowProgress, cancelable, progressDialogListener);
                    } else {
                        this.pp = new AsynchProgressPopup(requester, customDialog, Thread.currentThread(), bInputBlocking, bKnowProgress, cancelable, progressDialogListener);
                    }
                    this.pp.setStopStrategy(stopStrategy);
                    if (bInputBlocking) {
                        this.pp.startKeepOnTop();
                    } else {
                        this.pp.start();
                    }
                }
                if (requester != null) this.windowParent = GuiUtils.getWindowForComponent(requester);

                try {
                    if (this.windowParent != null) {
						Window targetWindow = this.windowParent;
                        this.focusOwner = FocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                        SwingUtilities.invokeAndWait(() -> {
                            ClientMDIManager.blockWindow(targetWindow);
                            targetWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        });
                    }
                } catch (InterruptedException | InvocationTargetException e) {
                    lg.warn(e);
                }
                for (int i = 0; i < taskList.size(); i++) {
                    // run all tasks
                    // after abort, run only non-skippable tasks
                    // also skip selected tasks specified by conditionalSkip tag
                    final AsynchClientTask currentTask = taskList.get(i);
                    try {
                        currentTask.setClientTaskStatusSupport(this.pp);
                        ClientTaskDispatcher.setSwingWorkerThreadName(this, threadBaseName + currentTask.getTaskName());

                        //System.out.println("DISPATCHING: "+currentTask.getTaskName()+" at "+ new Date(System.currentTimeMillis()));
                        if (this.pp != null) {
                            this.pp.setVisible(currentTask.showProgressPopup());
                            if (!bKnowProgress) {
                                this.pp.setProgress(i * 100 / taskList.size()); // beginning of task
                            }
                            this.pp.setMessage(currentTask.getTaskName());
                        }
                        boolean shouldRun = !hash.containsKey(HASH_DATA_ERROR);
                        if (hash.containsKey(TASK_ABORTED_BY_ERROR) && currentTask.skipIfAbort()) {
                            shouldRun = false;
                        }
                        if (hash.containsKey(TASKS_TO_BE_SKIPPED)) {
                            String[] toSkip = (String[]) hash.get(TASKS_TO_BE_SKIPPED);
							boolean taskShouldBeSkipped = ArrayUtils.arrayContains(toSkip, currentTask.getClass().getName());
                            if (taskShouldBeSkipped) shouldRun = false;
                        }
                        if (this.pp != null && this.pp.isInterrupted()) {
                            ClientTaskDispatcher.recordException(UserCancelException.CANCEL_GENERIC, hash);
                        }

                        if (hash.containsKey(TASK_ABORTED_BY_USER)) {
                            UserCancelException exc = (UserCancelException) hash.get(TASK_ABORTED_BY_USER);
                            if (currentTask.skipIfCancel(exc)) shouldRun = false;
                        }
                        if (shouldRun) {
                            try {
                                if (currentTask.getTaskType() == AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
                                    ClientTaskDispatcher.runTask(currentTask, hash, taskList);
                                } else if (currentTask.getTaskType() == AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
                                    SwingUtilities.invokeAndWait(() -> {
                                        try {
                                            ClientTaskDispatcher.runTask(currentTask, hash, taskList);
                                        } catch (Exception exc) {
                                            ClientTaskDispatcher.recordException(exc, hash);
                                        }
                                    });
                                } else if (currentTask.getTaskType() == AsynchClientTask.TASKTYPE_SWING_NONBLOCKING) {
                                    SwingUtilities.invokeLater(() -> {
                                        try {
                                            ClientTaskDispatcher.runTask(currentTask, hash, taskList);
                                        } catch (Exception exc) {
											ClientTaskDispatcher.recordException(exc, hash);
                                        }
                                    });
                                }
                            } catch (Exception exc) {
                                ClientTaskDispatcher.recordException(exc, hash);
                            }
                        }
                    } finally {
						ClientTaskDispatcher.allTasks.remove(currentTask);
                    }
                }
                return hash;
            }

            public void finished() {
				ClientTaskDispatcher.entryCounter--;

                if (this.pp != null) {
                    this.pp.stop();
                }
                if (hash.containsKey(TASK_ABORTED_BY_ERROR)) {
                    // something went wrong
                    StringBuilder allCausesErrorMessageSB = new StringBuilder();
                    Throwable causeError = (Throwable) hash.get(TASK_ABORTED_BY_ERROR);
                    do {
                        allCausesErrorMessageSB.append(causeError.getClass().getSimpleName()).append("-")
								.append(causeError.getMessage() == null || causeError.getMessage().isEmpty() ?
										"" : causeError.getMessage());
                        allCausesErrorMessageSB.append("\n");
                    } while ((causeError = causeError.getCause()) != null);
                    if (requester == null) {
                        lg.warn("ClientTaskDispatcher.dispatch(), requester is null, dialog has no parent, please try best to fix it!!!");
                        Thread.dumpStack();
                    }
                    if (lg.isInfoEnabled()) {
                        Object obj = hash.get(STACK_TRACE_ARRAY);
                        StackTraceElement[] ste = CastingUtils.downcast(StackTraceElement[].class, obj);
                        if (ste != null) {
                            String stackTraceString = StringUtils.join(ste, '\n');
                            lg.info(stackTraceString, (Throwable) hash.get(TASK_ABORTED_BY_ERROR));
                        } else {
                            lg.info("Unexpected {} obj {}", STACK_TRACE_ARRAY, obj);
                        }
                    }
                    PopupGenerator.showErrorDialog(requester, allCausesErrorMessageSB.toString(),
							(Throwable) hash.get(TASK_ABORTED_BY_ERROR));
                } else if (hash.containsKey(TASK_ABORTED_BY_USER)) {
                    // depending on where user canceled we might want to automatically start a new job
                    ClientTaskDispatcher.dispatchFollowUp(hash);
                }

                FinalWindow fw = AsynchClientTask.fetch(hash, FINAL_WINDOW, FinalWindow.class, false);
                if (lg.isDebugEnabled() && fw != null) {
                    lg.debug("FinalWindow retrieved from hash");
                }
                //focusOwner is legacy means of shifting focus -- FinalWindow is newer explicit invocation
                if (this.windowParent != null) {
                    ClientMDIManager.unBlockWindow(this.windowParent);
                    this.windowParent.setCursor(Cursor.getDefaultCursor());
                    if (fw == null && this.focusOwner != null) {
                        fw = () -> {
                            this.windowParent.requestFocusInWindow();
                            this.focusOwner.requestFocusInWindow();
                        };
                        lg.debug("FinalWindow built from {} and {}", this.windowParent.toString(), this.focusOwner.toString());
                    }
                }
                if (fw != null) {
					lg.debug("scheduling {}.run on {}", fw.getClass().getName(), fw);
					Runnable runnable = lg.isDebugEnabled() ? ClientTaskDispatcher.debugWrapper(fw) : fw;
					SwingUtilities.invokeLater(runnable);
                } else {
                    lg.debug("no Final Window");
                }
            }
        };
        setSwingWorkerThreadName(worker, threadBaseName);
        allTasks.addAll(taskList);
        worker.start();
    }

    /**
     * wrap runnable in debug print statement
     *
     * @param r payload
     * @return wrapper
     */
    private static Runnable debugWrapper(Runnable r) {
        return () ->
        {
            lg.debug("calling " + r.getClass().getName() + ".run on " + r);
            r.run();
        };
    }

    private static void dispatchFollowUp(Hashtable<String, Object> hash) {
        // we deal with a task dispatch that aborted due to some user choice on prompts
        UserCancelException e = (UserCancelException) hash.get("cancel");
        if (e == UserCancelException.CHOOSE_SAVE_AS) {
            // user chose to save as during a save or save/edition of a document found to be unchanged
            ((DocumentWindowManager) hash.get(CommonTask.DOCUMENT_WINDOW_MANAGER.name)).saveDocumentAsNew();
        }
    }

    public static void recordException(Throwable exc, Hashtable<String, Object> hash) {
        lg.error(exc.getMessage(), exc);
        if (exc instanceof UserCancelException userCancelException) {
            hash.put(TASK_ABORTED_BY_USER, userCancelException);
        } else {
            exc.printStackTrace(System.out);
            hash.put(TASK_ABORTED_BY_ERROR, exc);
        }
    }

    /**
     * @return list of outstanding tasks, or empty set if none
     */
    public static Collection<String> outstandingTasks() {
        if (ClientTaskDispatcher.allTasks.isEmpty()) return Collections.emptyList();


        synchronized (ClientTaskDispatcher.allTasks) {
            List<String> taskNames = new ArrayList<>();
            for (AsynchClientTask ct : ClientTaskDispatcher.allTasks) {
                String tn = ct.getTaskName();
                taskNames.add(tn);
            }
            return taskNames;
        }
    }

    /**
     * @return true if there are uncompleted tasks
     */
    public static boolean hasOutstandingTasks() {
        return !ClientTaskDispatcher.allTasks.isEmpty();
    }

    //updated API
    public static void dispatchColl(Component requester, Hashtable<String, Object> hash, Collection<AsynchClientTask> coll) {
        dispatch(requester, hash, coll.toArray(AsynchClientTask[]::new));
    }

    public static void dispatchColl(Component requester, Hashtable<String, Object> hash, Collection<AsynchClientTask> coll, boolean bKnowProgress) {
        dispatch(requester, hash, coll.toArray(AsynchClientTask[]::new), bKnowProgress);
    }

    public static void dispatchColl(Component requester, Hashtable<String, Object> hash, Collection<AsynchClientTask> coll, boolean bKnowProgress,
                                    boolean cancelable, ProgressDialogListener progressDialogListener) {
        dispatch(requester, hash, coll.toArray(AsynchClientTask[]::new), bKnowProgress, cancelable, progressDialogListener);
    }

    public static void dispatchColl(Component requester, Hashtable<String, Object> hash, Collection<AsynchClientTask> coll, boolean bKnowProgress,
                                    boolean cancelable, ProgressDialogListener progressDialogListener, boolean bInputBlocking) {
        dispatch(requester, hash, coll.toArray(AsynchClientTask[]::new), bKnowProgress, cancelable, progressDialogListener, bInputBlocking);
    }

    public static void dispatchColl(final Component requester, final Hashtable<String, Object> hash, Collection<AsynchClientTask> coll,
                                    final boolean bShowProgressPopup, final boolean bKnowProgress, final boolean cancelable, final ProgressDialogListener progressDialogListener, final boolean bInputBlocking) {
        dispatch(requester, hash, coll.toArray(AsynchClientTask[]::new), bShowProgressPopup, bKnowProgress, cancelable, progressDialogListener, bInputBlocking);
    }

    public static void dispatchColl(final Component requester, final Hashtable<String, Object> hash, final AsynchClientTask[] tasks, final ProgressDialog customDialog,
                                    final boolean bShowProgressPopup, final boolean bKnowProgress, final boolean cancelable, final ProgressDialogListener progressDialogListener, final boolean bInputBlocking) {
    }


    /**
     * set {@link SwingWorker} thread name
     *
     * @param sw
     * @param name may not be null
     * @throws IllegalArgumentException if name is null
     */
    private static void setSwingWorkerThreadName(SwingWorker sw, String name) {
        if (name != null) {
            try {
                Field threadVarField = SwingWorker.class.getDeclaredField("threadVar");
                threadVarField.setAccessible(true);
                Object threadVar = threadVarField.get(sw);
                Field threadField = threadVar.getClass().getDeclaredField("thread");
                threadField.setAccessible(true);
                Thread thread = (Thread) threadField.get(threadVar);
                thread.setName(name);
            } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
                lg.warn("setSwingWorkerName fail", e);
            }
            return;
        }
        throw new IllegalArgumentException("name may not be null");
    }

    /**
     * call currentTask.run(hash) with log4j logging; check for required keys
     *
     * @param currentTask not null
     * @param hash        not null
     * @param taskList    current set of tasks being dispatched
     * @throws Exception
     */
    private static void runTask(AsynchClientTask currentTask, Hashtable<String, Object> hash, Collection<AsynchClientTask> taskList) throws Exception {
        if (lg.isDebugEnabled()) {
            String msg = "Thread " + Thread.currentThread().getName() + " calling task " + currentTask.getTaskName();
            if (lg.isDebugEnabled()) {
                Object obj = hash.get(STACK_TRACE_ARRAY);
                StackTraceElement[] ste = CastingUtils.downcast(StackTraceElement[].class, obj);
                if (ste != null) {
                    msg += '\n' + StringUtils.join(ste, '\n');
                }
                lg.debug(msg);
            } else {
                lg.debug(msg);
            }
        }
        //check required elements present
        StringBuilder sb = null;
        for (KeyInfo requiredKey : currentTask.requiredKeys()) {
            Object obj = hash.get(requiredKey.name);
            if (obj == null) {
                if (sb == null) sb = initStringBuilder(currentTask);
                sb.append("Missing required key  ").append(requiredKey.name).append('\n');
                continue;
            }
            Class<?> foundClass = obj.getClass();
            if (!requiredKey.clzz.isAssignableFrom(foundClass)) {
                if (sb == null) sb = initStringBuilder(currentTask);
                sb.append("key ").append(requiredKey.name).append(" type ").append(foundClass.getName()).append(" not of required type ").append(requiredKey.clzz.getName());
                sb.append('\n');
            }
        }

        if (sb == null) { //no problems found
            currentTask.run(hash);
            return;
        }

        sb.append("Prior tasks\n");
        for (AsynchClientTask pt : taskList) {
            if (pt == currentTask) {
                break;
            }
            sb.append('\t').append(pt.getTaskName()).append('\n');
        }
        hash.put(HASH_DATA_ERROR, HASH_DATA_ERROR);
        throw new ProgrammingException(sb.toString());
    }

    /**
     * @param currentTask non null
     * @return {@link StringBuilder} initialized with task name
     */
    private static StringBuilder initStringBuilder(AsynchClientTask currentTask) {
        StringBuilder sb = new StringBuilder();
        sb.append("Data error for task " + currentTask.getTaskName() + '\n');
        return sb;
    }

    //package
    interface FinalWindow extends Runnable {
    }

    /**
     * set final window in hash
     *
     * @param hash    non null
     * @param fWindow non null
     * @throws ProgrammingException if more than one set in the same hash
     * @see AsynchClientTask#setFinalWindow(Hashtable, cbit.vcell.client.ChildWindowManager.ChildWindow)
     * @see AsynchClientTask#setFinalWindow(Hashtable, java.awt.Container)
     */
//package
    static void setFinalWindow(Hashtable<String, Object> hash, FinalWindow fWindow) {
        if (!hash.contains(FINAL_WINDOW)) {
            hash.put(FINAL_WINDOW, fWindow);
            return;
        }
        Object existing = hash.get(FINAL_WINDOW);
        final String def = "null";
        String e = ClassUtils.getShortClassName(existing, def);
        String n = ClassUtils.getShortClassName(fWindow, def);
        throw new ProgrammingException("duplicate final windows" + e + " and " + n);
    }


}

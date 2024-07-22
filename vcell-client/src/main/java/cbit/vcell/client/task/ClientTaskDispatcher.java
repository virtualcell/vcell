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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import javax.swing.Timer;

import org.apache.commons.lang3.ClassUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.vcell.util.*;
import org.vcell.util.gui.AsynchProgressPopup;
import org.vcell.util.gui.ProgressDialog;

import cbit.vcell.simdata.PDEDataContext;
import swingthreads.StandardSwingWorker;
import swingthreads.SwingWorker;
import swingthreads.TaskEventKeys;

/**
 * Insert the type's description here.
 * Creation date: (5/28/2004 2:44:22 AM)
 *
 * @author: Ion Moraru
 */
public class ClientTaskDispatcher {
    private static final Logger lg = LogManager.getLogger(ClientTaskDispatcher.class);
    /**
     * used to count / generate thread names
     */
    private static long serial = 0;
    private static int entryCounter = 0;
    /**
     * set of all scheduled tasks; used to avoid calling System.exit() prematurely on Application exit
     */
    private static final Set<AsynchClientTask> allTasks;
    /**
     * hash key for final window
     */
    private static final String FINAL_WINDOW = "finalWindowInterface";

    static {
        WeakHashMap<AsynchClientTask, Boolean> whm = new WeakHashMap<>();
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
        private static final ProgressDialogListener cancelListener = newEvent -> {
            synchronized (allBlockingTimers) {
                while (!allBlockingTimers.isEmpty()) {
                    allBlockingTimers.remove(0);
                }
                ppStop.restart();
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
        if (lg.isInfoEnabled())
            hash.put(TaskEventKeys.STACK_TRACE_ARRAY.toString(), Thread.currentThread().getStackTrace());

        if (bShowProgressPopup && requester == null) {
            System.out.println("ClientTaskDispatcher.dispatch(), requester is null, dialog has no parent, please try best to fix it!!!");
            Thread.dumpStack();
        }

        for (AsynchClientTask task : tasks) {
            if (AsynchClientTask.TASKTYPE_SWING_NONBLOCKING == task.getTaskType() && taskList.size() + 1 != tasks.length) {
                throw new RuntimeException("SWING_NONBLOCKING task only permitted as last task");
            }
            taskList.add(task);
            lg.debug("added task name {}", task.getTaskName());
        }

        // dispatch tasks to a new worker
        SwingWorker worker = new StandardSwingWorker(threadBaseName, taskList, requester, stopStrategy, hash, bKnowProgress,
                progressDialogListener, bShowProgressPopup, customDialog, bInputBlocking, cancelable) {

            @Override
            public void removeTaskFromDispatcher(AsynchClientTask task) {
                ClientTaskDispatcher.allTasks.remove(task);
            }

            @Override
            public void announceTaskComplete() {
                ClientTaskDispatcher.entryCounter--;
            }
        };

        worker.setThreadName(threadBaseName);
        allTasks.addAll(taskList);
        worker.start();
    }

    /**
     * wrap runnable in debug print statement
     *
     * @param r payload
     * @return wrapper
     */
    public static Runnable debugWrapper(Runnable r) {
        return () ->
        {
            lg.debug("calling " + r.getClass().getName() + ".run on " + r);
            r.run();
        };
    }


    public static void recordException(Throwable exc, Hashtable<String, Object> hash) {
        lg.error(exc.getMessage(), exc);
        if (exc instanceof UserCancelException userCancelException) {
            hash.put(TaskEventKeys.TASK_ABORTED_BY_USER.toString(), userCancelException);
        } else {
            exc.printStackTrace(System.out);
            hash.put(TaskEventKeys.TASK_ABORTED_BY_ERROR.toString(), exc);
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


    //package
    public interface FinalWindow extends Runnable {
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

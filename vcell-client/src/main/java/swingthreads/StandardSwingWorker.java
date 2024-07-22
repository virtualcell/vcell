package swingthreads;

import cbit.vcell.client.ClientMDIManager;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.CommonTask;
import cbit.vcell.client.task.ResultsHashUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.*;
import org.vcell.util.gui.AsynchProgressPopup;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ProgressDialog;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

public abstract class StandardSwingWorker extends SwingWorker {
    private static final Logger lg = LogManager.getLogger(StandardSwingWorker.class);

    private AsynchProgressPopup pp = null;
    private Window windowParent = null;
    private Component focusOwner = null;

    private List<AsynchClientTask> taskList;
    private Hashtable<String, Object> resultsHash;

    private final boolean shouldShowProgressPopup;
    private final boolean shouldBlockInput;
    private final boolean shouldInformOfProgress;
    private final boolean shouldBeCancelable;
    private final String threadBaseName;
    private final ProgressDialog customProgressDialog;
    private final Component requestingComponent;
    private final ProgressDialogListener progressDialogListener;

    private final ClientTaskDispatcher.StopStrategy stopStrategy;

    public StandardSwingWorker(String threadBaseName, List<AsynchClientTask> taskList, Component requestingComponent,
                               ClientTaskDispatcher.StopStrategy stopStrategy, Hashtable<String, Object> resultsHash,
                               boolean shouldInformOfProgress, ProgressDialogListener progressDialogListener,
                               boolean shouldShowProgressPopup, ProgressDialog customProgressDialog,
                               boolean shouldBlockInput, boolean shouldBeCancelable) {
        super();
        this.threadBaseName = threadBaseName;
        this.taskList = taskList;
        this.requestingComponent = requestingComponent;
        this.stopStrategy = stopStrategy;
        this.resultsHash = resultsHash;
        this.shouldInformOfProgress = shouldInformOfProgress;
        this.progressDialogListener = progressDialogListener;
        this.shouldShowProgressPopup = shouldShowProgressPopup;
        this.customProgressDialog = customProgressDialog;
        this.shouldBlockInput = shouldBlockInput;
        this.shouldBeCancelable = shouldBeCancelable;
    }

    /**
     * call currentTask.run(hash) with log4j logging; check for required keys
     *
     * @param currentTask not null
     * @param hash        not null
     * @param taskList    current set of tasks being dispatched
     * @throws Exception
     */
    public static void runTask(AsynchClientTask currentTask, Hashtable<String, Object> hash, Collection<AsynchClientTask> taskList) throws Exception {
        if (lg.isDebugEnabled()) {
            String msg = "Thread " + Thread.currentThread().getName() + " calling task " + currentTask.getTaskName();
            Object obj = hash.get(TaskEventKeys.STACK_TRACE_ARRAY.toString());
            StackTraceElement[] ste = CastingUtils.downcast(StackTraceElement[].class, obj);
            if (ste != null) msg += '\n' + StringUtils.join(ste, '\n');
            lg.debug(msg);
        }
        //check required elements present
        StringBuilder sb = null;
        for (AsynchClientTask.KeyInfo requiredKey : currentTask.requiredKeys()) {
            Object obj = hash.get(requiredKey.name);
            if (obj == null) {
                if (sb == null) sb = StandardSwingWorker.initStringBuilder(currentTask);
                sb.append("Missing required key  ").append(requiredKey.name).append('\n');
                continue;
            }

            Class<?> foundClass = obj.getClass();
            if (requiredKey.clzz.isAssignableFrom(foundClass)) continue;

            if (sb == null) sb = StandardSwingWorker.initStringBuilder(currentTask);
            sb.append("key ").append(requiredKey.name).append(" type ").append(foundClass.getName()).append(" not of required type ").append(requiredKey.clzz.getName());
            sb.append('\n');
        }

        if (sb == null) { //no problems found
            currentTask.run(hash);
            return;
        }

        // If we got here, we had issues.
        sb.append("Prior tasks\n");
        for (AsynchClientTask pt : taskList) {
            if (pt == currentTask) {
                break;
            }
            sb.append('\t').append(pt.getTaskName()).append('\n');
        }
        hash.put(TaskEventKeys.HASH_DATA_ERROR.toString(), TaskEventKeys.HASH_DATA_ERROR.toString());
        throw new ProgrammingException(sb.toString());
    }

    public Object construct() {
        if (this.shouldShowProgressPopup) {
            if (this.customProgressDialog == null) {
                this.pp = new AsynchProgressPopup(this.requestingComponent, "WORKING...", "Initializing request", Thread.currentThread(), this.shouldBlockInput, this.shouldInformOfProgress, this.shouldBeCancelable, this.progressDialogListener);
            } else {
                this.pp = new AsynchProgressPopup(this.requestingComponent, this.customProgressDialog, Thread.currentThread(), this.shouldBlockInput, this.shouldInformOfProgress, this.shouldBeCancelable, this.progressDialogListener);
            }
            this.pp.setStopStrategy(this.stopStrategy);
            if (this.shouldBlockInput) {
                this.pp.startKeepOnTop();
            } else {
                this.pp.start();
            }
        }
        if (this.requestingComponent != null) this.windowParent = GuiUtils.getWindowForComponent(this.requestingComponent);

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
        for (int i = 0; i < this.taskList.size(); i++) {
            // run all tasks
            // after abort, run only non-skippable tasks
            // also skip selected tasks specified by conditionalSkip tag
            final AsynchClientTask currentTask = this.taskList.get(i);
            try {
                currentTask.setClientTaskStatusSupport(this.pp);
                this.setThreadName(this.threadBaseName + currentTask.getTaskName());

                //System.out.println("DISPATCHING: "+currentTask.getTaskName()+" at "+ new Date(System.currentTimeMillis()));
                if (this.pp != null) {
                    this.pp.setVisible(currentTask.showProgressPopup());
                    if (!this.shouldInformOfProgress) {
                        this.pp.setProgress(i * 100 / this.taskList.size()); // beginning of task
                    }
                    this.pp.setMessage(currentTask.getTaskName());
                }
                boolean shouldRun = !this.resultsHash.containsKey(TaskEventKeys.HASH_DATA_ERROR.toString());
                if (this.resultsHash.containsKey(TaskEventKeys.TASK_ABORTED_BY_ERROR.toString()) && currentTask.skipIfAbort()) {
                    shouldRun = false;
                }
                if (this.resultsHash.containsKey(TaskEventKeys.TASKS_TO_BE_SKIPPED.toString())) {
                    String[] toSkip = (String[]) this.resultsHash.get(TaskEventKeys.TASKS_TO_BE_SKIPPED.toString());
                    boolean taskShouldBeSkipped = ArrayUtils.arrayContains(toSkip, currentTask.getClass().getName());
                    if (taskShouldBeSkipped) shouldRun = false;
                }
                if (this.pp != null && this.pp.isInterrupted()) {
                    ClientTaskDispatcher.recordException(UserCancelException.CANCEL_GENERIC, this.resultsHash);
                }

                if (this.resultsHash.containsKey(TaskEventKeys.TASK_ABORTED_BY_USER.toString())) {
                    UserCancelException exc = (UserCancelException) this.resultsHash.get(TaskEventKeys.TASK_ABORTED_BY_USER.toString());
                    if (currentTask.skipIfCancel(exc)) shouldRun = false;
                }
                if (shouldRun) {
                    try {
                        if (currentTask.getTaskType() == AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
                            StandardSwingWorker.runTask(currentTask, this.resultsHash, this.taskList);
                        } else if (currentTask.getTaskType() == AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
                            SwingUtilities.invokeAndWait(() -> {
                                try {
                                    StandardSwingWorker.runTask(currentTask, this.resultsHash, this.taskList);
                                } catch (Exception exc) {
                                    ClientTaskDispatcher.recordException(exc, this.resultsHash);
                                }
                            });
                        } else if (currentTask.getTaskType() == AsynchClientTask.TASKTYPE_SWING_NONBLOCKING) {
                            SwingUtilities.invokeLater(() -> {
                                try {
                                    StandardSwingWorker.runTask(currentTask, this.resultsHash, this.taskList);
                                } catch (Exception exc) {
                                    ClientTaskDispatcher.recordException(exc, this.resultsHash);
                                }
                            });
                        }
                    } catch (Exception exc) {
                        ClientTaskDispatcher.recordException(exc, this.resultsHash);
                    }
                }
            } finally {
                this.removeTaskFromDispatcher(currentTask);
            }
        }
        return this.resultsHash;
    }

    public void finished() {
        this.announceTaskComplete();

        if (this.pp != null) {
            this.pp.stop();
        }
        if (this.resultsHash.containsKey(TaskEventKeys.TASK_ABORTED_BY_ERROR.toString())) {
            // something went wrong
            StringBuilder allCausesErrorMessageSB = new StringBuilder();
            Throwable causeError = (Throwable) this.resultsHash.get(TaskEventKeys.TASK_ABORTED_BY_ERROR.toString());
            do {
                allCausesErrorMessageSB.append(causeError.getClass().getSimpleName()).append("-")
                        .append(causeError.getMessage() == null || causeError.getMessage().isEmpty() ?
                                "" : causeError.getMessage());
                allCausesErrorMessageSB.append("\n");
            } while ((causeError = causeError.getCause()) != null);
            if (this.requestingComponent == null) {
                lg.warn("ClientTaskDispatcher.dispatch(), requester is null, dialog has no parent, please try best to fix it!!!");
                Thread.dumpStack();
            }
            if (lg.isInfoEnabled()) {
                Object obj = this.resultsHash.get(TaskEventKeys.STACK_TRACE_ARRAY.toString());
                StackTraceElement[] ste = CastingUtils.downcast(StackTraceElement[].class, obj);
                if (ste != null) {
                    String stackTraceString = StringUtils.join(ste, '\n');
                    lg.info(stackTraceString, (Throwable) this.resultsHash.get(TaskEventKeys.TASK_ABORTED_BY_ERROR.toString()));
                } else {
                    lg.info("Unexpected {} obj {}", TaskEventKeys.STACK_TRACE_ARRAY.toString(), obj);
                }
            }
            PopupGenerator.showErrorDialog(this.requestingComponent, allCausesErrorMessageSB.toString(),
                    (Throwable) this.resultsHash.get(TaskEventKeys.TASK_ABORTED_BY_ERROR.toString()));
        } else if (this.resultsHash.containsKey(TaskEventKeys.TASK_ABORTED_BY_USER.toString())) {
            // depending on where user canceled we might want to automatically start a new job
            StandardSwingWorker.dispatchFollowUp(this.resultsHash);
        }

        ClientTaskDispatcher.FinalWindow fw = ResultsHashUtils.fetch(this.resultsHash, TaskEventKeys.FINAL_WINDOW.toString(), ClientTaskDispatcher.FinalWindow.class, false);
        if (fw != null) lg.debug("FinalWindow retrieved from hash");

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

    private static void dispatchFollowUp(Hashtable<String, Object> hash) {
        // we deal with a task dispatch that aborted due to some user choice on prompts
        UserCancelException e = (UserCancelException) hash.get("cancel");
        if (e == UserCancelException.CHOOSE_SAVE_AS) {
            // user chose to save as during a save or save/edition of a document found to be unchanged
            ((DocumentWindowManager) hash.get(CommonTask.DOCUMENT_WINDOW_MANAGER.name)).saveDocumentAsNew();
        }
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

    public abstract void removeTaskFromDispatcher(AsynchClientTask task);

    public abstract void announceTaskComplete();
}

package org.vcell.sybil.util.state;

/*   SystemWorker  --- by Oliver Ruebenacker, UCHC --- June to September 2008
 *   A SwingWorker registering itself as a Task with a SystemState
 */

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

import javax.swing.SwingUtilities;

import org.vcell.sybil.util.exception.CatchUtil;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;

public abstract class SystemWorker {

	protected Object result;
	
//	public void run() { 
//		construct(); 
//		SwingUtil.runOnSwingThread(new RunFinished(this));
//	}
	
	public void run(Component requester, Hashtable<String, Object> hashtable) { 
		ClientTaskDispatcher.dispatch(requester, hashtable, getTasks());
	}
	
	public void run(Component requester) { 
		Hashtable<String,Object> hashtable = new Hashtable<String,Object>();
		run(requester,hashtable);
	}
	
	public Object get() { return result; }
	
//	public void perform(Task.Mode mode) {
//		if(mode == Task.Mode.RunParallel) { start(); }
//		else { construct(); finished(); }
//	}
	
//	public final void construct() { 
//		hasObtainedLocks  = requestLocks();
//		if(hasObtainedLocks) {
//			state.tasks().add(this);
//			result = doConstruct();			
//		} else {
//			result = null;			
//		}
//	}
	
	public AsynchClientTask[] getTasks(){
		AsynchClientTask nonswingTask = new AsynchClientTask(getNonSwingTaskName(), AsynchClientTask.TASKTYPE_NONSWING_BLOCKING,true,true) {
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				result = doConstruct();
			}
		};
		AsynchClientTask swingTask = new AsynchClientTask(getNonSwingTaskName(), AsynchClientTask.TASKTYPE_SWING_BLOCKING,true,true) {
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				doFinished();
			}
		};
		return new AsynchClientTask[] { nonswingTask, swingTask };
	}
	
	public abstract String getNonSwingTaskName();
	public abstract String getSwingTaskName();
	
	public abstract Object doConstruct();
	public abstract void doFinished();
		
}

package cbit.vcell.client.task;

import java.util.Hashtable;

/**
 * {@link AsynchClientTaskFunction} that records the fact that it was executed ({@link #run(Hashtable)} called).
 */
public class AsynchClientTaskFunctionTrack extends AsynchClientTaskFunction {
	
	private boolean finished = false;

	
	public AsynchClientTaskFunctionTrack(AsyncExecute ae, String name, int taskType) {
		super(ae, name, taskType);
	}

	public AsynchClientTaskFunctionTrack(AsyncExecute ae, String name, int taskType, boolean bShowPopup) {
		super(ae, name, taskType, bShowPopup);
	}

	public AsynchClientTaskFunctionTrack(AsyncExecute ae, String name, int taskType, boolean skipIfAbort, boolean skipIfCancel) {
		super(ae, name, taskType, skipIfAbort, skipIfCancel);
	}

	public AsynchClientTaskFunctionTrack(AsyncExecute ae, String name, int taskType, boolean bShowPopup, boolean skipIfAbort, boolean skipIfCancel) {
		super(ae, name, taskType, bShowPopup, skipIfAbort, skipIfCancel);
	}

	/**
	 * @return true if {@link #run(Hashtable)} was called on this
	 */
	public boolean isFinished() {
		return finished;
	}

	@Override
	public void run(Hashtable<String, Object> hashTable) throws Exception {
		try {
			super.run(hashTable);
		} finally {
			finished = true;
		}
	}
}

package cbit.vcell.client.task;

import java.util.Hashtable;

/**
 * AsyncClientTask which supports lambda 
 */
public class AsyncClientTaskFunction extends AsynchClientTask {
	public interface AsyncExecute {
		public void run(Hashtable<String, Object> hashTable) throws Exception;
	}
	private final AsyncExecute asyncExecute;

	public AsyncClientTaskFunction(AsyncExecute ae, String name, int taskType) {
		super(name, taskType);
		asyncExecute = ae;
	}

	public AsyncClientTaskFunction(AsyncExecute ae, String name, int taskType, boolean bShowPopup) {
		super(name, taskType, bShowPopup);
		asyncExecute = ae;
	}

	public AsyncClientTaskFunction(AsyncExecute ae, String name, int taskType, boolean skipIfAbort, boolean skipIfCancel) {
		super(name, taskType, skipIfAbort, skipIfCancel);
		asyncExecute = ae;
	}

	public AsyncClientTaskFunction(AsyncExecute ae, String name, int taskType, boolean bShowPopup, boolean skipIfAbort, boolean skipIfCancel) {
		super(name, taskType, bShowPopup, skipIfAbort, skipIfCancel);
		asyncExecute = ae;
	}

	@Override
	public void run(Hashtable<String, Object> hashTable) throws Exception {
		asyncExecute.run(hashTable);
	}
}

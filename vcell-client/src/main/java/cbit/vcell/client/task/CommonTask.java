package cbit.vcell.client.task;

import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.task.AsynchClientTask.KeyInfo;
import cbit.vcell.clientdb.DocumentManager;


/**
 * commonly used {@link ClientTaskDispatcher} has keys, with type information
 */
public interface CommonTask {
	public static final KeyInfo DOCUMENT_WINDOW_MANAGER = new KeyInfo(DocumentWindowManager.IDENT, DocumentWindowManager.class);
	public static final KeyInfo DOCUMENT_MANAGER = new KeyInfo(DocumentManager.IDENT, DocumentManager.class);

}

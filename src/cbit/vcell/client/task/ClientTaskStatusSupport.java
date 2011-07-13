package cbit.vcell.client.task;

import org.vcell.util.gui.ProgressDialogListener;

public interface ClientTaskStatusSupport {
	void setMessage(String message);
	void setProgress(int progress);
	int getProgress();
	boolean isInterrupted();
	
	void addProgressDialogListener(ProgressDialogListener progressDialogListener);
}

package cbit.vcell.solvers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cbit.vcell.mapping.BioNetGenUpdaterCallback;
import cbit.vcell.mapping.TaskCallbackMessage;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;

public class BioNetGenExecutable extends MathExecutable {

	private transient List<BioNetGenUpdaterCallback> callbacks = null;
	
	/**
	 * 
	 * @param command
	 * @param timeoutMS - 0 means no-timeout.
	 */
	public BioNetGenExecutable(String[] command, long timeoutMS, File workingDirectory) {
		super(command, timeoutMS, workingDirectory);
	}
	
	public void inheritCallbacks(List<BioNetGenUpdaterCallback> callbacks) {
		this.callbacks = callbacks;
	}
	private List<BioNetGenUpdaterCallback> getCallbacks() {
		if(callbacks == null) {
			callbacks = new ArrayList<BioNetGenUpdaterCallback>();
		}
		return callbacks;
	}
	
	@Override
	protected void setNewOutputString(String newOutputString) {
		if(newOutputString != null) {
			for(BioNetGenUpdaterCallback callback : getCallbacks()) {
				TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.Detail, newOutputString);
				callback.setNewCallbackMessage(tcm);
			}
		}
	}
}

package cbit.vcell.mapping;

public class TaskCallbackMessage {
	
	public enum TaskCallbackStatus {
		// commands
		Clean,
		TaskStart,
		@Deprecated
		TaskEnd,	// detects insufficient iterations / molecules, displays warning in console and adjusts simulation context flags
		TaskEndNotificationOnly,					// same as above but doesn't adjust the simulation context flags
		TaskEndAdjustSimulationContextFlagsOnly,	// // same as above but only adjust the simulation context flags
		TaskStopped,
		DetailBatch,
		Detail,

		// adjusts previous/current iteration number of species flags and insufficient molecules flag at the end of each iteration (Multipass exec service only)
		AdjustAllFlags,
		
		// mostly display related
		Notification,
		Error,			// red, bold
		Warning,		// red
		Other;
	}

	private final TaskCallbackStatus status;
	private final String text;
	public TaskCallbackMessage(TaskCallbackStatus status, String text) {
		super();
		this.status = status;
		this.text = text;
	}

	public final TaskCallbackStatus getStatus() {
		return status;
	}
	public final String getText() {
		if(text == null) {
			return "";
		}
		return text;
	}
}

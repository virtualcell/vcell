package cbit.vcell.mapping;

public class TaskCallbackMessage {
	
	public enum TaskCallbackStatus {
		Clean,
		TaskStart,
		TaskEnd,
		TaskStopped,
		Notification,
		DetailBatch,
		Detail,
		Error,		// red, bold
		Warning,	// red
		// used when testing a different set of constraints in the NetworkConstraints panel
		// upon receiving this message we move the test max iterations and max molecules per species 
		// in the NetworkConstraints object belonging to the simulation context
		ValidateConstraints,
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

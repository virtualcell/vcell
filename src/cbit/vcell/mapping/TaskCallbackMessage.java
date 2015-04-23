package cbit.vcell.mapping;

public class TaskCallbackMessage {
	
	public enum TaskCallbackStatus {
		TaskStart,
		TaskEnd,
		TaskStopped,
		Notification,
		Detail,
		Error,
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

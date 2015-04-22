package cbit.vcell.mapping;

public class TaskCallbackMessage {
	
	public enum TaskCallbackStatus {
		TaskStart,
		TaskEnd,
		TaskStopped,
		Detail,
		Error,
		Other;
	}

	TaskCallbackStatus status;
	String text;
	public TaskCallbackMessage(TaskCallbackStatus status, String text) {
		super();
		this.status = status;
		this.text = text;
	}

	public TaskCallbackStatus getStatus() {
		return status;
	}
	public void setStatus(TaskCallbackStatus status) {
		this.status = status;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}

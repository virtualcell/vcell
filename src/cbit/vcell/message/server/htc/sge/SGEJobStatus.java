package cbit.vcell.message.server.htc.sge;

public enum SGEJobStatus {
	RUNNING,
	EXITED;

	public boolean isRunning() {
		return this == RUNNING;
	}

	public boolean isExiting() {
		return this == EXITED;
	}

}
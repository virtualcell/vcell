package cbit.vcell.message.server.htc.pbs;

public enum PBSJobStatus {
	Completed("C"),
	Exiting("E"),
	Held("H"),
	Queued("Q"),
	Running("R"),
	Moving("T"),
	Waiting("W"),
	Suspended("S");
	
	private String pbsCommandLetter;
	private PBSJobStatus(String pbsCommandLetter){
		this.pbsCommandLetter = pbsCommandLetter;
	}
	public String getPBSCommandLetter(){
		return pbsCommandLetter;
	}

	public boolean isCompleted() {
		return this.equals(Completed);
	}
	public boolean isExiting() {
		return this.equals(Exiting);
	}
	public boolean isHeld() {
		return this.equals(Held);
	}
	public boolean isQueued() {
		return this.equals(Queued);
	}
	public boolean isRunning() {
		return this.equals(Running);
	}
	public boolean isMoving() {
		return this.equals(Moving);
	}
	public boolean isWaiting() {
		return this.equals(Waiting);
	}
	public boolean isSuspended() {
		return this.equals(Suspended);
	}
	public String getDescription() {
		return name();
	}
	public static PBSJobStatus fromPBSCommandLetter(String pbsCommandLetter) {
		for (PBSJobStatus status : values()){
			if (status.getPBSCommandLetter().equals(pbsCommandLetter)){
				return status;
			}
		}
		return null;
	}
}
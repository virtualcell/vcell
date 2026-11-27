package org.vcell.api.types.events;

public class SimulationJobStatusEventRepresentation {
	
	public final SimulationJobStatusRepresentation jobStatus;
	public final Double progress;
	public final Integer estimatedTime;	// estimated total duration of the job in seconds (langevin only); null or 0 = unknown
	public final Double timePoint;
	public final String username;


	private SimulationJobStatusEventRepresentation(){
		jobStatus = null;
		progress = null;
		estimatedTime = null;
		timePoint = null;
		username = null;
	}


	public SimulationJobStatusEventRepresentation(SimulationJobStatusRepresentation jobStatus, Double progress,
			Integer estimatedTime, Double timePoint, String username) {
		super();
		this.jobStatus = jobStatus;
		this.progress = progress;
		this.estimatedTime = estimatedTime;
		this.timePoint = timePoint;
		this.username = username;
	}

}

package org.vcell.api.common.events;

public class SimulationJobStatusEventRepresentation {
	
	public final SimulationJobStatusRepresentation jobStatus;
	public final Double progress;
	public final Double timePoint;
	public final String username;


	private SimulationJobStatusEventRepresentation(){
		jobStatus = null;
		progress = null;
		timePoint = null;
		username = null;
	}


	public SimulationJobStatusEventRepresentation(SimulationJobStatusRepresentation jobStatus, Double progress,
			Double timePoint, String username) {
		super();
		this.jobStatus = jobStatus;
		this.progress = progress;
		this.timePoint = timePoint;
		this.username = username;
	}

}

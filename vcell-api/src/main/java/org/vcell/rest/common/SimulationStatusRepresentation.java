package org.vcell.rest.common;

import cbit.vcell.server.SimulationStatus;

public class SimulationStatusRepresentation {
	
	public final SimulationRepresentation simRep;
	public final String statusString;
	public final String details;
	public final String failedMessage;
	public final double progress;
	public final int numberOfJobsDone;
	
	public final boolean bRunnable;
	public final boolean bStoppable;
	public final boolean bHasData;

	public final boolean bStatusActive;
	public final boolean bStatusCompleted;
	public final boolean bStatusStopped;
	public final boolean bStatusFailed;
	

	public SimulationRepresentation getSimRep() {
		return simRep;
	}


	public String getStatusString() {
		return statusString;
	}

	public String getDetails() {
		return details;
	}

	public String getFailedMessage() {
		return failedMessage;
	}
	
	public double getProgress() {
		return progress;
	}
	
	public int getNumberOfJobsDone() {
		return numberOfJobsDone;
	}


	public boolean getRunnable() {
		return bRunnable;
	}

	public boolean isStoppable() {
		return bStoppable;
	}

	public boolean getHasData() {
		return bHasData;
	}


	public boolean isbStatusActive() {
		return bStatusActive;
	}

	public boolean isbStatusCompleted() {
		return bStatusCompleted;
	}

	public boolean isbStatusStopped() {
		return bStatusStopped;
	}

	public boolean isbStatusFailed() {
		return bStatusFailed;
	}


	public SimulationStatusRepresentation(SimulationRepresentation simRepresentation, SimulationStatus simulationStatus) {

		this.simRep = simRepresentation;
		this.statusString = simulationStatus.getStatusString();
		this.details = simulationStatus.getDetails();
		this.failedMessage = simulationStatus.getFailedMessage();
		Double progressDouble = simulationStatus.getProgress();
		if (progressDouble!=null){
			this.progress = progressDouble.doubleValue();
		}else{
			this.progress = -1;
		}
		this.numberOfJobsDone = simulationStatus.numberOfJobsDone();
		
		this.bHasData = simulationStatus.getHasData();
		this.bRunnable = simulationStatus.isRunnable();
		this.bStoppable = simulationStatus.isStoppable();
		
		this.bStatusActive = simulationStatus.isActive();
		this.bStatusCompleted = simulationStatus.isCompleted();
		this.bStatusStopped = simulationStatus.isStopped();
		this.bStatusFailed = simulationStatus.isFailed();
	}	
}

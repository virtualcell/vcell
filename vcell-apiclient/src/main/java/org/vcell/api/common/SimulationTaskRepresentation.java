package org.vcell.api.common;


public class SimulationTaskRepresentation {
	
	public String simKey;
	
	public String simName;
	
	public String userName;

	public String userKey;

	public String htcJobId;

	public String status;

	public long startdate;

	public int jobIndex;

	public int taskId;

	public String message;
	
	public String site;
	
	public String computeHost;
	
	public String schedulerStatus;
	
	public boolean hasData;
	
	public int scanCount;
	
	public MathModelLink mathModelLink;
	
	public BioModelLink bioModelLink;
	
	public SimulationTaskRepresentation(){
		
	}
	
	
	
	public String getSimKey() {
		return simKey;
	}



	public String getSimName() {
		return simName;
	}



	public String getUserName() {
		return userName;
	}



	public String getUserKey() {
		return userKey;
	}



	public String getHtcJobId() {
		return htcJobId;
	}



	public String getStatus() {
		return status;
	}



	public long getStartdate() {
		return startdate;
	}



	public int getJobIndex() {
		return jobIndex;
	}



	public int getTaskId() {
		return taskId;
	}



	public String getMessage() {
		return message;
	}



	public String getSite() {
		return site;
	}



	public String getComputeHost() {
		return computeHost;
	}



	public String getSchedulerStatus() {
		return schedulerStatus;
	}



	public boolean isHasData() {
		return hasData;
	}

	public int getScanCount(){
		return scanCount;
	}

	public MathModelLink getMathModelLink(){
		return mathModelLink;
	}



	public BioModelLink getBioModelLink() {
		return bioModelLink;
	}

	
}

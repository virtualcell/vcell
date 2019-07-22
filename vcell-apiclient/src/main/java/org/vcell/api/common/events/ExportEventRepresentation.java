package org.vcell.api.common.events;

public class ExportEventRepresentation {

	public final int eventType;
	public final Double progress;
	public final String format;
	public final String location;
	public final String username;
	public final String userkey;
	public final long jobid;
	public final String dataIdString;
	public final String dataKey;
	public final ExportTimeSpecs exportTimeSpecs;
	public final ExportVariableSpecs exportVariableSpecs;
	public final String clientJobID;
	
	public ExportEventRepresentation(
			int eventType, Double progress, String format, 
			String location, String username, String userkey, long jobid,
			String dataIdString, String dataKey,
			ExportTimeSpecs exportTimeSpecs, ExportVariableSpecs exportVariableSpecs,String clientJobID) {
		
		this.eventType = eventType;
		this.progress = progress;
		this.format = format;
		this.location = location;
		this.username = username;
		this.userkey = userkey;
		this.jobid = jobid;
		this.dataIdString = dataIdString;
		this.dataKey = dataKey;
		this.exportTimeSpecs = exportTimeSpecs;
		this.exportVariableSpecs = exportVariableSpecs;
		this.clientJobID = clientJobID;
	}
	
	

}

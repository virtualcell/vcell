package org.vcell.api.common.events;

public class EventWrapper {
	
	public static enum EventType {
		SimJob,
		ExportEvent,
		DataJob
	}
	
	public final long id;
	public final long timestamp;
	public final String userid;
	public final EventType eventType;
	public final String eventJSON;
	
	public EventWrapper(long id, long timestamp, String userid, EventType eventType, String eventJSON) {
		this.id = id;
		this.timestamp = System.currentTimeMillis();
		this.userid = userid;
		this.eventType = eventType;
		this.eventJSON = eventJSON;
	}
}


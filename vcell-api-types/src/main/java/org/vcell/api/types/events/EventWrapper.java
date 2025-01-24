package org.vcell.api.types.events;

public class EventWrapper {
	
	public static enum EventType {
		SimJob,
		ExportEvent,
		DataJob,
		Broadcast
	}
	
	public final long id;
	public final long timestamp;
	public final String userid;
	public final EventType eventType;
	public final String eventJSON;
	
	public EventWrapper(long id, long timestamp, String userid, EventType eventType, String eventJSON) {
		this.id = id;
		this.timestamp = timestamp;
		this.userid = userid;
		this.eventType = eventType;
		this.eventJSON = eventJSON;
	}
}


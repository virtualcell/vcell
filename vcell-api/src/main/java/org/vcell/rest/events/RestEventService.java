package org.vcell.rest.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.api.common.events.BroadcastEventRepresentation;
import org.vcell.api.common.events.DataJobEventRepresentation;
import org.vcell.api.common.events.EventWrapper;
import org.vcell.api.common.events.EventWrapper.EventType;
import org.vcell.api.common.events.ExportEventRepresentation;
import org.vcell.api.common.events.SimulationJobStatusEventRepresentation;
import org.vcell.rest.server.ClientTopicMessageCollector;
import org.vcell.util.Compare;

import com.google.gson.Gson;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.MessageEvent;
import cbit.rmi.event.PerformanceMonitorEvent;
import cbit.rmi.event.SimulationJobStatusEvent;
import cbit.rmi.event.VCellMessageEvent;
import cbit.rmi.event.WorkerEvent;
import cbit.vcell.message.VCMessagingService;

public class RestEventService {
	private final static Logger lg = LogManager.getLogger(RestEventService.class);
	
	final static AtomicLong eventSequence = new AtomicLong(0);
	final static ConcurrentLinkedDeque<EventWrapper> events = new ConcurrentLinkedDeque<>();
	ClientTopicMessageCollector clientTopicMessageCollector = null;
	VCMessagingService vcMessagingService = null;

	public RestEventService(VCMessagingService vcMessagingService) {
		this.vcMessagingService = vcMessagingService;
		if (this.vcMessagingService!=null){
			clientTopicMessageCollector = new ClientTopicMessageCollector(vcMessagingService);
			clientTopicMessageCollector.init();
			clientTopicMessageCollector.addMessageListener((e) -> newEventMessage(e));
		}
	}
		
	public void insert(String userid, EventType eventType, String eventJSON) {
		long id = eventSequence.getAndIncrement();
		long timestamp = System.currentTimeMillis();
		EventWrapper wrapper = new EventWrapper(id, timestamp, userid, eventType, eventJSON);
		events.addFirst(wrapper);
	}
	
	private void newEventMessage(MessageEvent event) {
		if (lg.isTraceEnabled()) lg.trace("newEventMessage("+event.getClass().getSimpleName()+": "+event);
		if (event instanceof ExportEvent) {
			ExportEvent exportEvent = (ExportEvent) event;
			try {
				ExportEventRepresentation exportEventRep = exportEvent.toJsonRep();
				ExportEvent event2 = ExportEvent.fromJsonRep(this, exportEventRep);
				if (!Compare.isEqual(event2.getFormat(),exportEvent.getFormat())) {
					throw new RuntimeException("Export event round-trip failed");
				}
				if (!Compare.isEqual(event2.getJobID(),exportEvent.getJobID())) {
					throw new RuntimeException("Export event round-trip failed");
				}
				Gson gson = new Gson();
				String eventJSON = gson.toJson(exportEventRep);
				insert(exportEventRep.username,EventType.ExportEvent,eventJSON);
			}catch (Exception e) {
				lg.error(e.getMessage(), e);
			}
		}else if (event instanceof SimulationJobStatusEvent) {
			SimulationJobStatusEvent simJobEvent = (SimulationJobStatusEvent)event;
			try {
				SimulationJobStatusEventRepresentation simJobEventRep = simJobEvent.toJsonRep();
				SimulationJobStatusEvent event2 = SimulationJobStatusEvent.fromJsonRep(this, simJobEventRep);
				if (!Compare.isEqual(event2.getJobStatus(),simJobEvent.getJobStatus())) {
					throw new RuntimeException("SimulationJobStatus event round-trip failed");
				}
				if (!Compare.isEqual(event2.getProgress(),simJobEvent.getProgress())) {
					throw new RuntimeException("SimulationJobStatus <PROGRESS> event round-trip failed");
				}
				Gson gson = new Gson();
				String eventJSON = gson.toJson(simJobEventRep);
				insert(simJobEventRep.username,EventType.SimJob,eventJSON);
			}catch (Exception e) {
				lg.error(e.getMessage(), e);
			}
		}else if (event instanceof VCellMessageEvent) {
			VCellMessageEvent vcellMessageEvent = (VCellMessageEvent)event;
			if(vcellMessageEvent.getEventTypeID() == VCellMessageEvent.VCELL_MESSAGEEVENT_TYPE_BROADCAST) {
				//Remove any existing broadcast message
				Iterator<EventWrapper> iter = events.iterator();
				while (iter.hasNext()) {
					EventWrapper eventWrapper = iter.next();
					if(eventWrapper.eventType.equals(EventWrapper.EventType.Broadcast)) {
						iter.remove();
					}
				}
				BroadcastEventRepresentation broadcastEventRepresentation = new BroadcastEventRepresentation(vcellMessageEvent.getMessageData().getData().toString());
				// If 'clear' then don't add new broadcast message
				if(broadcastEventRepresentation.message.trim().equalsIgnoreCase("clear")) {
					return;
				}
				//Add new broadcast message
				Gson gson = new Gson();
				String eventJSON = gson.toJson(broadcastEventRepresentation);
				insert(null,EventType.Broadcast,eventJSON);
			}else {
				lg.error("event of type VCellMessageEvent:"+vcellMessageEvent.getEventTypeID()+" not supported");
			}
		}else if (event instanceof WorkerEvent) {
			lg.error("event of type WorkerEvent not supported");
			WorkerEvent workerEvent = (WorkerEvent)event;
		}else if (event instanceof PerformanceMonitorEvent) {
			lg.error("event of type PerformanceMonitorEvent not supported");
			PerformanceMonitorEvent performanceMonitorEvent = (PerformanceMonitorEvent)event;
		}else if (event instanceof DataJobEvent) {
			DataJobEvent dataJobEvent = (DataJobEvent)event;
			try {
				DataJobEventRepresentation dataJobEventRep = dataJobEvent.toJsonRep();
				DataJobEvent event2 = DataJobEvent.fromJsonRep(this, dataJobEventRep);
				if (!Compare.isEqual(event2.getDataIdString(),dataJobEvent.getDataIdString())) {
					throw new RuntimeException("DataJob event round-trip failed");
				}
				if (!Compare.isEqual(event2.getProgress(),dataJobEvent.getProgress())) {
					throw new RuntimeException("DataJob <PROGRESS> event round-trip failed");
				}
				Gson gson = new Gson();
				String eventJSON = gson.toJson(dataJobEventRep);
				insert(dataJobEventRep.username,EventType.DataJob,eventJSON);
			}catch (Exception e) {
				lg.error(e.getMessage(), e);
			}
		}
	}
	
	
	public EventWrapper[] query(String userid, long lasttimestamp) {
		ArrayList<EventWrapper> eventList = new ArrayList<EventWrapper>();
		Iterator<EventWrapper> iter = events.iterator();
		while (iter.hasNext()) {
			EventWrapper eventWrapper = iter.next();
			if (eventWrapper.timestamp > lasttimestamp && (eventWrapper.userid==null || eventWrapper.userid.equals(userid))) {
				if (lg.isTraceEnabled()) lg.trace("returning event to userid: ("+eventWrapper.id+", "+eventWrapper.timestamp+", "+eventWrapper.userid+", "+eventWrapper.eventJSON+")");
				eventList.add(0, eventWrapper);
			}
		}
		EventWrapper[] eventArray = eventList.toArray(new EventWrapper[0]);
		return eventArray;
	}
	
}

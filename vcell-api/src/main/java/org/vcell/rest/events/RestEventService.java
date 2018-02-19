package org.vcell.rest.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

import org.vcell.api.common.events.EventWrapper;
import org.vcell.api.common.events.EventWrapper.EventType;
import org.vcell.api.common.events.ExportEventRepresentation;
import org.vcell.api.common.events.SimulationJobStatusEventRepresentation;
import org.vcell.rest.server.ClientTopicMessageCollector;
import org.vcell.util.Compare;
import org.vcell.util.SessionLog;

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
	
	final static AtomicLong eventSequence = new AtomicLong(0);
	final static ConcurrentLinkedDeque<EventWrapper> events = new ConcurrentLinkedDeque<>();
	ClientTopicMessageCollector clientTopicMessageCollector = null;
	VCMessagingService vcMessagingService = null;
	SessionLog log = null;

	public RestEventService(VCMessagingService vcMessagingService, SessionLog log) {
		this.vcMessagingService = vcMessagingService;
		if (this.vcMessagingService!=null){
			clientTopicMessageCollector = new ClientTopicMessageCollector(vcMessagingService,log);
			clientTopicMessageCollector.init();
			clientTopicMessageCollector.addMessageListener((e) -> newEventMessage(e));
		}
		this.log = log;
	}
		
	public void insert(String userid, EventType eventType, String eventJSON) {
		long id = eventSequence.getAndIncrement();
		long timestamp = System.currentTimeMillis();
		EventWrapper wrapper = new EventWrapper(id, timestamp, userid, eventType, eventJSON);
		events.addFirst(wrapper);
	}
	
	private void newEventMessage(MessageEvent event) {
		System.out.println(getClass().getName()+".newEventMessage("+event.getClass().getSimpleName()+": "+event);
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
				e.printStackTrace();
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
				e.printStackTrace();
			}
		}else if (event instanceof VCellMessageEvent) {
			VCellMessageEvent vcellMessageEvent = (VCellMessageEvent)event;
		}else if (event instanceof WorkerEvent) {
			WorkerEvent workerEvent = (WorkerEvent)event;
		}else if (event instanceof PerformanceMonitorEvent) {
			PerformanceMonitorEvent performanceMonitorEvent = (PerformanceMonitorEvent)event;
		}else if (event instanceof DataJobEvent) {
			DataJobEvent dataJobEvent = (DataJobEvent)event;
		}
	}
	
	
	public EventWrapper[] query(String userid, long lasttimestamp) {
		ArrayList<EventWrapper> eventList = new ArrayList<EventWrapper>();
		Iterator<EventWrapper> iter = events.iterator();
		while (iter.hasNext()) {
			EventWrapper eventWrapper = iter.next();
			if (eventWrapper.timestamp > lasttimestamp) {
				System.out.println("returning event to userid: ("+eventWrapper.id+", "+eventWrapper.timestamp+", "+eventWrapper.userid+", "+eventWrapper.eventJSON+")");
				eventList.add(0, eventWrapper);
			}
		}
		EventWrapper[] eventArray = eventList.toArray(new EventWrapper[0]);
		return eventArray;
	}
	
}

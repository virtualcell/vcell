package org.vcell.rest.events;

import cbit.rmi.event.*;
import cbit.vcell.message.VCMessagingService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.api.types.events.*;
import org.vcell.rest.server.ClientTopicMessageCollector;
import org.vcell.util.Compare;
import org.vcell.api.types.utils.DTOModelTransformerV0;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

public class RestEventService {
	private final static Logger lg = LogManager.getLogger(RestEventService.class);
	
	/**
	 * Serializes events, tolerating non-finite doubles.
	 *
	 * <p>JSON has no representation for Infinity or NaN, so a stock {@link Gson}
	 * throws {@code IllegalArgumentException} on one. That used to be caught, logged
	 * and the event dropped — but the message was then redelivered, failed again, and
	 * kept failing: a single export whose {@code progress} came out Infinity produced
	 * roughly 4,900 error lines a minute on production, and because the consumer loop
	 * creates a JMS connection and temporary queue per message, each redelivery cost
	 * one of each.
	 *
	 * <p>A non-finite value is written as {@code null} instead — a last-resort net so
	 * serialization can never throw. Clients tolerate a missing value here: the Python
	 * client declares {@code Optional[float] = None}, the TypeScript client
	 * {@code progress?: number}, and the desktop client null-checks sim-job progress
	 * ({@code SimulationListPanel}).
	 *
	 * <p><b>Export progress is the exception</b> and is clamped before it reaches this
	 * net — see {@link #withFiniteProgress}. The desktop client dereferences it without
	 * a null check for EXPORT_PROGRESS events, so emitting null there would swap a
	 * server-side serialization failure for a client-side NPE.
	 *
	 * <p>Neither excuses producing a non-finite progress — the WARN below names the
	 * value so the upstream division stays findable.
	 */
	private final static Gson gson = new GsonBuilder()
			.registerTypeAdapter(Double.class, nonFiniteAsNull())
			.registerTypeAdapter(double.class, nonFiniteAsNull())
			.create();

	private static JsonSerializer<Double> nonFiniteAsNull() {
		return (src, typeOfSrc, context) -> {
			if (src == null || src.isInfinite() || src.isNaN()) {
				if (src != null) {
					lg.warn("non-finite value {} in an outgoing event; sending null instead", src);
				}
				return JsonNull.INSTANCE;
			}
			return new JsonPrimitive(src);
		};
	}

	/**
	 * A copy whose {@code progress} is guaranteed finite, or the original if it already
	 * was (or is null, which is normal for EXPORT_START / EXPORT_ASSEMBLING).
	 *
	 * <p>Clamped into [0,1] rather than nulled because the desktop client does
	 * {@code event.getProgress().doubleValue() * 100} inside {@code case EXPORT_PROGRESS}
	 * ({@code ExportMonitorTableModel}) and compares two progress values by auto-unboxing
	 * in {@code ExportEvent.isSupercededBy} — a null in either place is an NPE. A wrong
	 * but finite progress bar is a far better failure than a crashed client.
	 */
	private static ExportEventRepresentation withFiniteProgress(ExportEventRepresentation rep) {
		if (rep.progress == null || !(rep.progress.isInfinite() || rep.progress.isNaN())) {
			return rep;
		}
		double clamped = rep.progress.isNaN() ? 0.0 : (rep.progress > 0 ? 1.0 : 0.0);
		lg.warn("export job {} reported non-finite progress {}; clamping to {}",
				rep.jobid, rep.progress, clamped);
		return new ExportEventRepresentation(rep.eventType, clamped, rep.format, rep.location,
				rep.username, rep.userkey, rep.jobid, rep.dataIdString, rep.dataKey,
				rep.exportTimeSpecs, rep.exportVariableSpecs, rep.exportHumanReadableDataSpec);
	}

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
		
	public void insert(String userid, EventWrapper.EventType eventType, String eventJSON) {
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
				ExportEventRepresentation exportEventRep = DTOModelTransformerV0.exportEventToJsonRep(exportEvent);
				ExportEvent event2 = DTOModelTransformerV0.exportEventFromJsonRep(this, exportEventRep);
				if (!Compare.isEqual(event2.getFormat(),exportEvent.getFormat())) {
					throw new RuntimeException("Export event round-trip failed");
				}
				if (!Compare.isEqual(event2.getJobID(),exportEvent.getJobID())) {
					throw new RuntimeException("Export event round-trip failed");
				}
				exportEventRep = withFiniteProgress(exportEventRep);
				String eventJSON = gson.toJson(exportEventRep);
				insert(exportEventRep.username, EventWrapper.EventType.ExportEvent,eventJSON);
			}catch (Exception e) {
				lg.error(e.getMessage(), e);
			}
		}else if (event instanceof SimulationJobStatusEvent) {
			SimulationJobStatusEvent simJobEvent = (SimulationJobStatusEvent)event;
			try {
				SimulationJobStatusEventRepresentation simJobEventRep = DTOModelTransformerV0.simulationJobStatusEventToJsonRep(simJobEvent);
				SimulationJobStatusEvent event2 = DTOModelTransformerV0.simulationJobStatusEventFromJsonRep(this, simJobEventRep);
				if (!Compare.isEqual(event2.getJobStatus(),simJobEvent.getJobStatus())) {
					throw new RuntimeException("SimulationJobStatus event round-trip failed");
				}
				if (!Compare.isEqual(event2.getProgress(),simJobEvent.getProgress())) {
					throw new RuntimeException("SimulationJobStatus <PROGRESS> event round-trip failed");
				}
				String eventJSON = gson.toJson(simJobEventRep);
				insert(simJobEventRep.username, EventWrapper.EventType.SimJob,eventJSON);
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
				String eventJSON = gson.toJson(broadcastEventRepresentation);
				insert(null, EventWrapper.EventType.Broadcast,eventJSON);
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
				DataJobEventRepresentation dataJobEventRep = DTOModelTransformerV0.dataJobRepToJsonRep(dataJobEvent);
				DataJobEvent event2 = DTOModelTransformerV0.dataJobEventFromJsonRep(this, dataJobEventRep);
				if (!Compare.isEqual(event2.getDataIdString(),dataJobEvent.getDataIdString())) {
					throw new RuntimeException("DataJob event round-trip failed");
				}
				if (!Compare.isEqual(event2.getProgress(),dataJobEvent.getProgress())) {
					throw new RuntimeException("DataJob <PROGRESS> event round-trip failed");
				}
				String eventJSON = gson.toJson(dataJobEventRep);
				insert(dataJobEventRep.username, EventWrapper.EventType.DataJob,eventJSON);
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

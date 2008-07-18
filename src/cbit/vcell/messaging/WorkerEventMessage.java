package cbit.vcell.messaging;
import cbit.vcell.server.User;
import cbit.vcell.messaging.server.SimulationDispatcher;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.sql.KeyValue;
import javax.jms.*;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.Simulation;
import cbit.rmi.event.WorkerEvent;

/**
 * Insert the type's description here.
 * Creation date: (2/5/2004 12:35:20 PM)
 * @author: Fei Gao
 */
public class WorkerEventMessage {
	private WorkerEvent workerEvent = null;	
	private static final String MESSAGE_TYPE_WORKEREVENT_VALUE	= "WorkerEvent";

	private static final String WORKEREVENT_STATUS = "WorkerEvent_Status";
	private static final String WORKEREVENT_PROGRESS = "WorkerEvent_Progress";
	private static final String WORKEREVENT_TIMEPOINT = "WorkerEvent_TimePoint";
	private static final String WORKEREVENT_STATUSMSG = "WorkerEvent_StatusMsg";
	
/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public WorkerEventMessage(WorkerEvent event) {
	workerEvent = event;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public WorkerEventMessage(SimulationDispatcher dispatcher, Message message0) throws JMSException, cbit.vcell.server.DataAccessException {
	parseMessage(dispatcher, message0);
}


/**
 * Insert the method's description here.
 * Creation date: (3/11/2004 11:32:33 AM)
 * @return cbit.rmi.event.WorkerEvent
 */
public cbit.rmi.event.WorkerEvent getWorkerEvent() {
	return workerEvent;
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/2004 2:19:48 PM)
 * @param message javax.jms.Message
 */
private void parseMessage(SimulationDispatcher dispatcher, Message message) throws JMSException {
	if (message == null) {
		throw new RuntimeException("Null message");
	}	

	try {
		String msgType = (String)JmsUtils.parseProperty(message, MessageConstants.MESSAGE_TYPE_PROPERTY, String.class);
		if (msgType != null && !msgType.equals(MESSAGE_TYPE_WORKEREVENT_VALUE)) {
			throw new RuntimeException("Wrong message");
		}
	} catch (MessagePropertyNotFoundException ex) {
		throw new RuntimeException("Wrong message");
	}
			
	if (message instanceof ObjectMessage) {
		Object obj = ((ObjectMessage)message).getObject();
		if (!(obj instanceof WorkerEvent)) {
			throw new IllegalArgumentException("Expecting " + SimulationInfo.class.getName() + " in message.");
		}
		workerEvent = (WorkerEvent)obj;

		// from c++ executable
	} else if (message instanceof TextMessage) {
		try {
			String msgType = (String)JmsUtils.parseProperty(message, MessageConstants.MESSAGE_TYPE_PROPERTY, String.class);
			if (msgType != null && !msgType.equals(MESSAGE_TYPE_WORKEREVENT_VALUE)) {
				throw new RuntimeException("Wrong message"); // wrong message
			}
			int status = ((Integer)JmsUtils.parseProperty(message, WORKEREVENT_STATUS, int.class)).intValue();
			String hostname = (String)JmsUtils.parseProperty(message, MessageConstants.HOSTNAME_PROPERTY, String.class);
			String username = (String)JmsUtils.parseProperty(message, MessageConstants.USERNAME_PROPERTY, String.class);
			int taskID = ((Integer)JmsUtils.parseProperty(message, MessageConstants.TASKID_PROPERTY, int.class)).intValue();
			int jobIndex = ((Integer)JmsUtils.parseProperty(message, MessageConstants.JOBINDEX_PROPERTY, int.class)).intValue();
			Long longkey = (Long)JmsUtils.parseProperty(message, MessageConstants.SIMKEY_PROPERTY, long.class);

			KeyValue simKey = new KeyValue(longkey + "");
			Simulation sim = null;
			try {
				User user = dispatcher.getUser(simKey, username);
				sim = dispatcher.getSimulation(user, simKey);			
				if (sim == null) {
					throw new RuntimeException("Null Simulation"); //wrong message	
				}
			} catch (cbit.vcell.server.DataAccessException ex) {
				throw new RuntimeException("Null Simulation"); // wrong message
			}
			
			String statusMessage = null;
			Double progress = null;
			Double timepoint = null;
			
			try {
				statusMessage = (String)JmsUtils.parseProperty(message, WORKEREVENT_STATUSMSG, String.class);
			} catch (MessagePropertyNotFoundException ex) {
				// it's OK not to have status message
			}

			try {
				progress = (Double)JmsUtils.parseProperty(message, WORKEREVENT_PROGRESS, double.class);
				timepoint = (Double)JmsUtils.parseProperty(message, WORKEREVENT_TIMEPOINT, double.class);
			} catch (MessagePropertyNotFoundException ex) {
				// it's OK not to have progress or timepoint
			}

			workerEvent = new WorkerEvent(status, dispatcher, sim.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), jobIndex, hostname, taskID, progress, timepoint, statusMessage);
					
		} catch (cbit.vcell.messaging.MessagePropertyNotFoundException ex) {
			throw new RuntimeException("Wrong message"); //wrong message
		} 
	} else {
		throw new IllegalArgumentException("Expecting object message.");
	}

	
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public static WorkerEventMessage sendAccepted(JmsSession session, Object source, SimulationTask simTask, String hostName) throws JMSException {
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_ACCEPTED, source, simTask, hostName, null);
	WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
	workerEventMessage.sendWorkerEvent(session);

	return workerEventMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public static WorkerEventMessage sendCompleted(JmsSession session, Object source, SimulationTask simTask, String hostName, double progress, double timePoint) throws JMSException {
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_COMPLETED, source, simTask, hostName, new Double(progress), new Double(timePoint));		
	WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
	workerEventMessage.sendWorkerEvent(session);

	return workerEventMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public static WorkerEventMessage sendFailed(JmsSession session, Object source, SimulationTask simTask, String hostName, String failMessage) throws JMSException {
	String revisedFailMsg = failMessage;
	if (revisedFailMsg != null) {
		revisedFailMsg = revisedFailMsg.trim();
		if (revisedFailMsg.length() > 2048) {
			revisedFailMsg = revisedFailMsg.substring(0, 2048); //status message is only 2048 chars long in database
		}

		revisedFailMsg = revisedFailMsg.replace('\r', ' ');
		revisedFailMsg = revisedFailMsg.replace('\n', ' ');
		revisedFailMsg = revisedFailMsg.replace('\'', ' '); // these characters are not valid both in database and in messages as a property
	}
	
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_FAILURE, source, simTask,	hostName, revisedFailMsg);
	WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
	workerEventMessage.sendWorkerEvent(session);

	return workerEventMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public static WorkerEventMessage sendNewData(JmsSession session, Object source, SimulationTask simTask, String hostName, double progress, double timePoint) throws JMSException {
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_DATA, source, simTask, hostName, new Double(progress), new Double(timePoint));		
	WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
	workerEventMessage.sendWorkerEvent(session);

	return workerEventMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public static WorkerEventMessage sendProgress(JmsSession session, Object source, SimulationTask simTask, String hostName, double progress, double timePoint) throws JMSException {
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_PROGRESS, source, simTask, hostName, new Double(progress), new Double(timePoint));		
	WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
	workerEventMessage.sendWorkerEvent(session);

	return workerEventMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public static WorkerEventMessage sendStarting(JmsSession session, Object source, SimulationTask simTask, String hostName, String startMessage) throws JMSException {
	String revisedStartMsg = startMessage;
	if (revisedStartMsg != null) {
		revisedStartMsg = revisedStartMsg.trim();
		if (revisedStartMsg.length() > 2048) {
			revisedStartMsg = revisedStartMsg.substring(0, 2048); // status message is only 2048 chars long in database
		}

		revisedStartMsg = revisedStartMsg.replace('\r', ' ');
		revisedStartMsg = revisedStartMsg.replace('\n', ' ');
		revisedStartMsg = revisedStartMsg.replace('\'', ' '); // these characters are not valid both in database and in messages as a property
	}
		
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_STARTING, source, simTask, hostName, revisedStartMsg);
	WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
	workerEventMessage.sendWorkerEvent(session);

	return workerEventMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public static WorkerEventMessage sendWorkerAlive(JmsSession session, Object source, SimulationTask simTask, String hostName) throws JMSException {
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_WORKER_ALIVE, source, simTask, hostName, null);
	WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
	workerEventMessage.sendWorkerEvent(session);

	return workerEventMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 */
private void sendWorkerEvent(JmsSession session) throws JMSException {
	session.sendMessage(JmsUtils.getQueueWorkerEvent(), toMessage(session), DeliveryMode.PERSISTENT, MessageConstants.INTERVAL_SERVER_FAIL);
}


/**
 * Insert the method's description here.
 * Creation date: (5/20/2003 1:36:36 PM)
 * @return javax.jms.Message
 */
private Message toMessage(JmsSession session) throws JMSException {		
	Message message = session.createObjectMessage(workerEvent);
	message.setStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY, MESSAGE_TYPE_WORKEREVENT_VALUE);
	
	return message;
}
}
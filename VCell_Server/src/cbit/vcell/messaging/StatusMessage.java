package cbit.vcell.messaging;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import cbit.vcell.solvers.SimulationJobStatus;

/**
 * Insert the type's description here.
 * Creation date: (2/5/2004 12:35:20 PM)
 * @author: Fei Gao
 */
public class StatusMessage {
	private SimulationJobStatus jobStatus = null;
	private Double timePoint = null;
	private Double progress = null;

	private static final String SIMULATION_STATUS_PROGRESS_PROPERTY	= "SimulationStatusProgress";
	private static final String SIMULATION_STATUS_TIMEPOINT_PROPERTY = "SimulationStatusTimePoint";
	
	private java.lang.String userName = null;

/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public StatusMessage(SimulationJobStatus jobStatus0, String userName0, Double progress0, Double timepoint0) {
	jobStatus = jobStatus0;
	userName = userName0;
	progress = progress0;
	timePoint = timepoint0;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public StatusMessage(Message message) throws JMSException {
	parseMessage(message);
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/2004 2:17:03 PM)
 * @return cbit.vcell.messaging.db.SimulationJobStatus
 */
public cbit.vcell.solvers.SimulationJobStatus getJobStatus() {
	return jobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/2004 2:17:03 PM)
 * @return java.lang.Double
 */
public java.lang.Double getProgress() {
	return progress;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:56:45 PM)
 * @return cbit.vcell.solver.SimulationInfo
 */
public SimulationJobStatus getSimulationJobStatus() {
	return jobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/2004 2:17:03 PM)
 * @return java.lang.Double
 */
public java.lang.Double getTimePoint() {
	return timePoint;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 10:24:41 AM)
 * @return java.lang.String
 */
public java.lang.String getUserName() {
	return userName;
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/2004 2:19:48 PM)
 * @param message javax.jms.Message
 */
private void parseMessage(Message message) throws JMSException {
	if (message == null) {
		throw new RuntimeException("Null message");
	}

	try {
		String msgType = (String)JmsUtils.parseProperty(message, MessageConstants.MESSAGE_TYPE_PROPERTY, String.class);
		if (msgType != null && !msgType.equals(MessageConstants.MESSAGE_TYPE_SIMSTATUS_VALUE)) {
			throw new RuntimeException("Wrong message");
		}
	} catch (MessagePropertyNotFoundException ex) {
		ex.printStackTrace(System.out);
		throw new RuntimeException("Wrong message");
	}
			
	if (!(message instanceof ObjectMessage)) {
		throw new IllegalArgumentException("Expecting object message.");
	}

	Object obj = ((ObjectMessage)message).getObject();
	if (!(obj instanceof SimulationJobStatus)) {
		throw new IllegalArgumentException("Expecting " + SimulationJobStatus.class.getName() + " in message.");
	}

	jobStatus = (SimulationJobStatus)obj;
	try {
		progress = (Double)JmsUtils.parseProperty(message, SIMULATION_STATUS_PROGRESS_PROPERTY, double.class);
	} catch (MessagePropertyNotFoundException ex) {
		//ex.printStackTrace(System.out);
		//it's ok
	}
	
	try {
		timePoint = (Double)JmsUtils.parseProperty(message, SIMULATION_STATUS_TIMEPOINT_PROPERTY, double.class);
	} catch (MessagePropertyNotFoundException ex) {
		//ex.printStackTrace(System.out);
		//it's ok
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/2004 1:59:04 PM)
 * @return javax.jms.Message
 * @param session cbit.vcell.messaging.VCellSession
 */
public void sendToClient(VCellTopicSession session) throws JMSException {
	Message message = toMessage(session);
	session.publishMessage(JmsUtils.getTopicClientStatus(), message);
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/2004 1:59:04 PM)
 * @return javax.jms.Message
 * @param session cbit.vcell.messaging.VCellSession
 */
private Message toMessage(VCellJmsSession session) throws JMSException {
	Message message = session.createObjectMessage(jobStatus);
	message.setStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_SIMSTATUS_VALUE);
	message.setStringProperty(MessageConstants.USERNAME_PROPERTY, userName);
	if (progress != null) {
		message.setDoubleProperty(SIMULATION_STATUS_PROGRESS_PROPERTY, progress.doubleValue());
	}
	if (timePoint != null) {
		message.setDoubleProperty(SIMULATION_STATUS_TIMEPOINT_PROPERTY, timePoint.doubleValue());
	}

	return message;
}


/**
 * Insert the method's description here.
 * Creation date: (2/13/2004 9:55:17 AM)
 * @return java.lang.String
 */
public String toString() {
	return "StatusMessage [" + jobStatus.getStatusMessage() + "," + progress + "," + timePoint + "]";
}
}
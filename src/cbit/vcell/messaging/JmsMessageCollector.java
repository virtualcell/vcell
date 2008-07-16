package cbit.vcell.messaging;
import cbit.vcell.solver.*;
import javax.swing.event.EventListenerList;
import cbit.vcell.server.User;
import cbit.vcell.server.SessionLog;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.rmi.event.*;

/**
 * Insert the type's description here.
 * Creation date: (5/9/2003 12:05:19 PM)
 * @author: Fei Gao
 */
public class JmsMessageCollector implements ControlTopicListener, MessageSender {
	private EventListenerList listenerList = new EventListenerList();	
	private User user = null;	
	private JmsConnection jmsConn = null;
	private SessionLog log = null;

	private long timeSinceLastMessage = System.currentTimeMillis();

/**
 * ClientStatusMonitor constructor comment.
 * @param jmsFactory cbit.vcell.messaging.JmsFactory
 * @param serviceName java.lang.String
 * @param queueName java.lang.String
 */
public JmsMessageCollector(JmsConnection aTopicConn, User user0, SessionLog log0) throws javax.jms.JMSException {
	jmsConn = aTopicConn;
	user = user0;
	log = log0;
	
	reconnect();	
}


/**
 * Insert the method's description here.
 * Creation date: (3/5/2004 9:28:52 AM)
 * @param listener cbit.rmi.event.MessageListener
 */
public void addMessageListener(cbit.rmi.event.MessageListener listener) {
	listenerList.add(MessageListener.class, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (11/17/2000 11:43:22 AM)
 * @param event cbit.rmi.event.ExportEvent
 */
protected synchronized void fireExportEvent(ExportEvent event) {
	fireMessageEvent(event);
}


/**
 * Insert the method's description here.
 * Creation date: (11/17/2000 11:43:22 AM)
 * @param event cbit.rmi.event.ExportEvent
 */
protected synchronized void fireMessageEvent(MessageEvent event) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Reset the source to allow proper wiring
	event.setSource(this);
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length - 2; i >= 0; i -= 2) {
		if (listeners[i] == MessageListener.class) {
			((MessageListener) listeners[i + 1]).messageEvent(event);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 2:44:30 PM)
 * @param event cbit.rmi.event.JobProgressEvent
 */
protected synchronized void fireSimulationJobStatusEvent(SimulationJobStatusEvent event) {
	fireMessageEvent(event);
}


/**
 * Insert the method's description here.
 * Creation date: (4/19/2004 1:05:20 PM)
 * @return long
 */
public long getTimeSinceLastMessage() {
	return timeSinceLastMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 3:58:52 PM)
 * @param message javax.jms.Message
 */
public void onControlTopicMessage(javax.jms.Message message0) throws javax.jms.JMSException {	
	
	if (message0 == null) {
		return;
	}
	if(!(message0 instanceof javax.jms.ObjectMessage)){
		throw new javax.jms.JMSException(this.getClass().getName()+".onControlTopicMessage: unimplemented message class "+message0.getClass().getName());
	}
	javax.jms.ObjectMessage message = (javax.jms.ObjectMessage) message0;
	
	setTimeSinceLastMessage(System.currentTimeMillis());

	try{
		String msgType = (String)JmsUtils.parseProperty(message, MessageConstants.MESSAGE_TYPE_PROPERTY, String.class);
		if(msgType == null){
			throw new javax.jms.JMSException(this.getClass().getName()+".onControlTopicMessage: message type NULL for message "+message);
		}
		if (msgType.equals(MessageConstants.MESSAGE_TYPE_SIMSTATUS_VALUE)
			//&& 
			//message.getObject() instanceof SimulationJobStatus
			) {
					
			StatusMessage statusMessage = new StatusMessage(message);
			
			SimulationJobStatus newJobStatus = statusMessage.getJobStatus();
			log.print("---onTopicMessage[" + newJobStatus + "]");
			if (newJobStatus == null) {
				return;
			}
			
			VCSimulationIdentifier vcSimID = newJobStatus.getVCSimulationIdentifier();
			Double progress = statusMessage.getProgress();
			Double timePoint = statusMessage.getTimePoint();

			fireSimulationJobStatusEvent(new SimulationJobStatusEvent(this, vcSimID.getID(), newJobStatus, progress, timePoint));		

		} else if(msgType.equals(MessageConstants.MESSAGE_TYPE_EXPORT_EVENT_VALUE)) {			
			ExportEvent event = (ExportEvent)((javax.jms.ObjectMessage)message).getObject();
			log.print("---onTopicMessage[ExportEvent[" + event.getVCDataIdentifier().getID() + "," + event.getProgress() + "]]");
			fireExportEvent(event);
		} else if(msgType.equals(MessageConstants.MESSAGE_TYPE_DATA_EVENT_VALUE)){
			DataJobEvent event = (DataJobEvent)message.getObject();
			log.print("---onTopicMessage[DataEvent[vcdid=" + event.getVCDataIdentifier().getID() + "," + event.getProgress() + "]]");
			fireMessageEvent(event);
		} else if (msgType.equals(cbit.vcell.messaging.admin.ManageConstants.MESSAGE_TYPE_BROADCASTMESSAGE_VALUE)) {
			fireMessageEvent(new VCellMessageEvent(this, System.currentTimeMillis() + "", new MessageData((cbit.util.BigString)message.getObject()), VCellMessageEvent.VCELL_MESSAGEEVENT_TYPE_BROADCAST));
		} else{
			throw new javax.jms.JMSException(this.getClass().getName()+".onControlTopicMessage: Unimplemented message "+message);
		}
	}catch(Exception e){
		if(e instanceof javax.jms.JMSException){throw (javax.jms.JMSException)e;}
		
	}
}


/**
 * onException method comment.
 */
private void reconnect() throws javax.jms.JMSException {	
	JmsSession statusReceiver = jmsConn.getAutoSession();
	String clientMessageFilter = (user == null ? "" : MessageConstants.USERNAME_PROPERTY + "='" + user.getName() + "' OR ");
	clientMessageFilter += MessageConstants.USERNAME_PROPERTY + "='All'";
			
	statusReceiver.setupTopicListener(JmsUtils.getTopicClientStatus(), clientMessageFilter, new ControlMessageCollector(this));
	
	jmsConn.startConnection();
}


/**
 * Insert the method's description here.
 * Creation date: (3/5/2004 9:28:52 AM)
 * @param listener cbit.rmi.event.MessageListener
 */
public void removeMessageListener(cbit.rmi.event.MessageListener listener) {}


/**
 * Insert the method's description here.
 * Creation date: (4/19/2004 1:05:20 PM)
 * @param newTimeSinceLastMessage long
 */
public void setTimeSinceLastMessage(long newTimeSinceLastMessage) {
	timeSinceLastMessage = newTimeSinceLastMessage;
}
}
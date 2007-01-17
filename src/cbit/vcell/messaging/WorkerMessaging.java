package cbit.vcell.messaging;
import javax.jms.*;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.messaging.admin.ManageConstants;
import cbit.vcell.server.SessionLog;
import cbit.vcell.messaging.server.Worker;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.server.PropertyLoader;

/**
 * Insert the type's description here.
 * Creation date: (7/2/2003 3:00:59 PM)
 * @author: Fei Gao
 */
public class WorkerMessaging extends JmsServiceProviderMessaging implements ControlTopicListener {
	private VCellQueueSession jobRetriever = null;
	private VCellQueueSession workerEventSession = null;
	private String jobSelector = null;
	private Worker myWorker = null;
	private SimulationTask currentTask = null;
	private VCellTopicSession listenSession = null;
	private VCellTopicSession topicSession = null;
	private long lastMsgTimeStamp = 0;
	
class KeepAliveThread extends Thread {

public KeepAliveThread() {
	super();
	setName("WorkerKeepAliveThread");
}	
public void run() {
	while (true) {
		try {
			sleep(MessageConstants.INTERVAL_PING_SERVER);
		} catch (InterruptedException ex) {
		}

		long t = System.currentTimeMillis();
		if (myWorker.isRunning() && lastMsgTimeStamp != 0 && t - lastMsgTimeStamp > MessageConstants.INTERVAL_PING_SERVER) {
			log.print("@@@@Worker:Sending alive message");
			sendWorkerAlive();
		}
	}
}	
}	
	private boolean bProgress = false;

/**
 * WorkerMessaging constructor comment.
 */
public WorkerMessaging(Worker worker0, SessionLog log0) throws JMSException {
	super(worker0, log0);
	myWorker = worker0;
	reconnect();

	log.print("Start keep alive thread");
	new KeepAliveThread().start();	
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 */
public SimulationTask getNextTask() { 	
	//
	// create a transactional receive/send to get a "task" object (that this worker can handle) 
	// and send an "accept" status message to the SchedulerControl queue
	//
	//log.print("==GNT");
	currentTask = null;
	lastMsgTimeStamp = 0;
	
	try {			
		//log.print("Created receiver with filter = " + jobSelector);		
		Message message = jobRetriever.receiveMessage(JmsUtils.getQueueSimJob(), jobSelector, 100);
		if (message == null) { // no message
			try {
				jobRetriever.rollback(); 
			} catch (Exception ex) {
				log.exception(ex);
			}
			currentTask = null;
			
		} else { 
			log.print("received message " + JmsUtils.toString(message));
			SimulationTaskMessage taskMsg = new SimulationTaskMessage(message);
			currentTask = taskMsg.getSimulationTask();
			
			log.print("Job accepted: " + currentTask);
			WorkerEventMessage.sendAccepted(jobRetriever, this, currentTask.getSimulationInfo(), currentTask.getSimulationJob().getJobIndex(), myWorker.getHostName(), currentTask.getTaskID());
			jobRetriever.commit();
			
			lastMsgTimeStamp = System.currentTimeMillis();
		}
		
	} catch (Exception ex) {
		try {
			jobRetriever.rollback(); 
		} catch (Exception e) {
			log.exception(e);
		}
		currentTask = null;
	}
	
	return currentTask;
}


/**
 * Insert the method's description here.
 * Creation date: (7/2/2003 3:06:25 PM)
 */
protected void reconnect() throws JMSException {
	jobSelector = myWorker.getJobSelector();
	log.print("Job Selector : " + jobSelector);
	
	queueConn = jmsConnFactory.createQueueConnection();	
	jobRetriever = queueConn.getTransactedSession(); // transactional
	int workerPrefetchCount = Integer.parseInt(PropertyLoader.getProperty(PropertyLoader.jmsWorkerPrefetchCount, "-1"));
	if (workerPrefetchCount > 0) {
		log.print("workerPrefetchCount=" + workerPrefetchCount);
		jobRetriever.setPrefetchCount(workerPrefetchCount); // get messages one by one
		jobRetriever.setPrefetchThreshold(0);
	}
	
	workerEventSession = queueConn.getAutoSession();
	queueConn.startConnection();

	super.reconnect();
	listenTopicSession.setupListener(JmsUtils.getTopicServiceControl(), null, new ControlMessageCollector(myWorker));	
	topicSession = topicConn.getAutoSession();
	
	topicConn.startConnection();	
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 */
public void sendCompleted(double progress, double timeSec) {
	if (currentTask == null) {
		return;
	}

	// have to keep sending the messages because it's important
	try {
		log.print("sendComplete(" + currentTask.getSimulationJobIdentifier() + ")");
		WorkerEventMessage.sendCompleted(workerEventSession, this, currentTask.getSimulationInfo(), currentTask.getSimulationJob().getJobIndex(), myWorker.getHostName(), currentTask.getTaskID(), progress, timeSec);
		
		lastMsgTimeStamp = System.currentTimeMillis();
	} catch (JMSException jmse) {
        log.exception(jmse);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 */
public void sendFailed(String failureMessage) {
	if (currentTask == null) {
		return;
	}
		
	try {
		log.print("sendFailure(" + currentTask.getSimulationJobIdentifier() + "," + failureMessage +")");
		WorkerEventMessage.sendFailed(workerEventSession, this, currentTask.getSimulationInfo(), currentTask.getSimulationJob().getJobIndex(), myWorker.getHostName(), currentTask.getTaskID(), failureMessage);
		
		lastMsgTimeStamp = System.currentTimeMillis();
	} catch (JMSException ex) {
        log.exception(ex);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 */
public void sendNewData(double progress, double timeSec) {
	if (currentTask == null) {
		return;
	}
	
	try {
		long t = System.currentTimeMillis();
		if (bProgress || t - lastMsgTimeStamp > MessageConstants.INTERVAL_PROGRESS_MESSAGE) { // don't send data message too frequently
			log.print("sendNewData(" + currentTask.getSimulationJobIdentifier() + "," + (progress * 100) + "%," + timeSec + ")");		
			WorkerEventMessage.sendNewData(workerEventSession, this, currentTask.getSimulationInfo(), currentTask.getSimulationJob().getJobIndex(), myWorker.getHostName(), currentTask.getTaskID(), progress, timeSec);
		
			lastMsgTimeStamp = System.currentTimeMillis();
			bProgress = false;
		}
	} catch (JMSException e) {
        log.exception(e);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 */
public void sendProgress(double progress, double timeSec) {
	if (currentTask == null) {
		return;
	}

	try {
		long t = System.currentTimeMillis();
	if (!bProgress || t - lastMsgTimeStamp > MessageConstants.INTERVAL_PROGRESS_MESSAGE 
		|| ((int)(progress * 100)) % 25 == 0) { // don't send progress message too frequently
			log.print("sendProgress(" + currentTask.getSimulationJobIdentifier() + "," + (progress * 100) + "%," + timeSec + ")");
			WorkerEventMessage.sendProgress(workerEventSession, this, currentTask.getSimulationInfo(), currentTask.getSimulationJob().getJobIndex(), myWorker.getHostName(), currentTask.getTaskID(), progress, timeSec);
			
			lastMsgTimeStamp = System.currentTimeMillis();
			bProgress = true;
		}
	} catch (JMSException e) {
        log.exception(e);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 */
public void sendStarting(String startingMessage) {
	if (currentTask == null) {
		return;
	}
	
	try {
		log.print("sendStarting(" + currentTask.getSimulationJobIdentifier() + ")");
		WorkerEventMessage.sendStarting(workerEventSession, this, currentTask.getSimulationInfo(), currentTask.getSimulationJob().getJobIndex(), myWorker.getHostName(), currentTask.getTaskID(), startingMessage);
	
		lastMsgTimeStamp = System.currentTimeMillis();
	} catch (JMSException e) {
        log.exception(e);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 */
public void sendWorkerAlive() {
	if (currentTask == null) {
		return;
	}

	// have to keep sending the messages because it's important
	try {
		log.print("sendWorkerAlive(" + currentTask.getSimulationJobIdentifier() + ")");
		WorkerEventMessage.sendWorkerAlive(workerEventSession, this, currentTask.getSimulationInfo(), currentTask.getSimulationJob().getJobIndex(), myWorker.getHostName(), currentTask.getTaskID());
		
		lastMsgTimeStamp = System.currentTimeMillis();
	} catch (JMSException jmse) {
        log.exception(jmse);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2004 11:21:59 AM)
 */
public void startReceiving() throws JMSException {
	queueConn.startConnection();
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2004 11:21:59 AM)
 */
public void stopReceiving() throws JMSException {
	queueConn.stopConnection();
}
}
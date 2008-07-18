package cbit.vcell.messaging;
import javax.jms.*;
import cbit.vcell.messaging.admin.ManageUtils;
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
	private JmsSession[] jobRetrievers = null;
	private JmsSession[] workerEventSessions = null;
	private String[] jobSelectors = null;
	private Worker myWorker = null;
	private SimulationTask[] currentTasks = null;
	private long[] lastMsgTimeStamp;
	
	class KeepAliveThread extends Thread {
		int workerIndex;
		public KeepAliveThread(int wi) {
			super();
			workerIndex = wi;
			setName("KeepAliveThread_Subworker_" + workerIndex);
		}	
		public void run() {
			while (true) {
				try {
					sleep(MessageConstants.INTERVAL_PING_SERVER);
				} catch (InterruptedException ex) {
				}
		
				long t = System.currentTimeMillis();
				if (myWorker.isRunning(workerIndex) && lastMsgTimeStamp[workerIndex] != 0 && t - lastMsgTimeStamp[workerIndex] > MessageConstants.INTERVAL_PING_SERVER) {
					log.print("@@@@Worker:Sending alive message");
					sendWorkerAlive(workerIndex);
				}
			}
		}	
	}	
	private boolean[] bProgresses = null;

/**
 * WorkerMessaging constructor comment.
 */
public WorkerMessaging(Worker worker0, SessionLog log0) throws JMSException {
	super(worker0, log0);
	myWorker = worker0;
	reconnect();
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 */
public SimulationTask getNextTask(int workerIndex) { 	
	//
	// create a transactional receive/send to get a "task" object (that this worker can handle) 
	// and send an "accept" status message to the SchedulerControl queue
	//
	//log.print("==GNT");
	currentTasks[workerIndex] = null;
	lastMsgTimeStamp[workerIndex] = 0;
	
	try {			
		//log.print("Created receiver with filter = " + jobSelector);		
		Message message = jobRetrievers[workerIndex].receiveMessage(JmsUtils.getQueueSimJob(), jobSelectors[workerIndex], 100);
		if (message == null) { // no message
			try {
				jobRetrievers[workerIndex].rollback(); 
			} catch (Exception ex) {
				log.exception(ex);
			}
			currentTasks[workerIndex] = null;
			
		} else { 
			log.print("received message " + JmsUtils.toString(message));
			SimulationTaskMessage taskMsg = new SimulationTaskMessage(message);
			currentTasks[workerIndex] = taskMsg.getSimulationTask();
			
			log.print("Job accepted: " + currentTasks[workerIndex]);
			WorkerEventMessage.sendAccepted(jobRetrievers[workerIndex], this, currentTasks[workerIndex], ManageUtils.getHostName());
			jobRetrievers[workerIndex].commit();
			
			lastMsgTimeStamp[workerIndex] = System.currentTimeMillis();
		}
		
	} catch (Exception ex) {
		try {
			jobRetrievers[workerIndex].rollback(); 
		} catch (Exception e) {
			log.exception(e);
		}
		currentTasks[workerIndex] = null;
	}
	
	return currentTasks[workerIndex];
}


/**
 * Insert the method's description here.
 * Creation date: (7/2/2003 3:06:25 PM)
 */
protected void reconnect() throws JMSException {
	jobSelectors = myWorker.getJobSelectors();	
	
	super.reconnect();
	int numSubworkers = myWorker.getNumSubworkers();
	jobRetrievers = new JmsSession[numSubworkers];
	workerEventSessions = new JmsSession[numSubworkers];
	for (int i = 0; i < numSubworkers; i ++) {
		log.print("Job Selector : " + jobSelectors[i]);
		jobRetrievers[i] = jmsConn.getTransactedSession(); // transactional
		int workerPrefetchCount = Integer.parseInt(PropertyLoader.getProperty(PropertyLoader.jmsWorkerPrefetchCount, "-1"));
		if (workerPrefetchCount > 0) {
			jobRetrievers[i].setPrefetchCount(workerPrefetchCount); // get messages one by one
			jobRetrievers[i].setPrefetchThreshold(0);
		}
		workerEventSessions[i] = jmsConn.getAutoSession();
	}	
	
	JmsSession serviceListenTopicSession = jmsConn.getAutoSession();
	serviceListenTopicSession.setupTopicListener(JmsUtils.getTopicServiceControl(), null, new ControlMessageCollector(myWorker));
	jmsConn.startConnection();
	
	currentTasks = new SimulationTask[numSubworkers];
	bProgresses = new boolean[numSubworkers];
	lastMsgTimeStamp =  new long[numSubworkers];
	log.print("Start keep alive thread");
	for (int i = 0; i < numSubworkers; i ++) {
		new KeepAliveThread(i).start();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 */
public void sendCompleted(int workerIndex, double progress, double timeSec) {
	if (currentTasks[workerIndex] == null) {
		return;
	}

	// have to keep sending the messages because it's important
	try {
		log.print("sendComplete(" + currentTasks[workerIndex].getSimulationJobIdentifier() + ")");
		WorkerEventMessage.sendCompleted(workerEventSessions[workerIndex], this, currentTasks[workerIndex], ManageUtils.getHostName(),  progress, timeSec);
		
		lastMsgTimeStamp[workerIndex] = System.currentTimeMillis();
	} catch (JMSException jmse) {
        log.exception(jmse);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 */
public void sendFailed(int workerIndex, String failureMessage) {
	if (currentTasks[workerIndex] == null) {
		return;
	}
		
	try {
		log.print("sendFailure(" + currentTasks[workerIndex].getSimulationJobIdentifier() + "," + failureMessage +")");
		WorkerEventMessage.sendFailed(workerEventSessions[workerIndex], this, currentTasks[workerIndex], ManageUtils.getHostName(), failureMessage);
		
		lastMsgTimeStamp[workerIndex] = System.currentTimeMillis();
	} catch (JMSException ex) {
        log.exception(ex);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 */
public void sendNewData(int workerIndex, double progress, double timeSec) {
	if (currentTasks[workerIndex] == null) {
		return;
	}
	
	try {
		long t = System.currentTimeMillis();
		if (bProgresses[workerIndex] || t - lastMsgTimeStamp[workerIndex] > MessageConstants.INTERVAL_PROGRESS_MESSAGE) { // don't send data message too frequently
			log.print("sendNewData(" + currentTasks[workerIndex].getSimulationJobIdentifier() + "," + (progress * 100) + "%," + timeSec + ")");		
			WorkerEventMessage.sendNewData(workerEventSessions[workerIndex], this, currentTasks[workerIndex], ManageUtils.getHostName(), progress, timeSec);
		
			lastMsgTimeStamp[workerIndex] = System.currentTimeMillis();
			bProgresses[workerIndex] = false;
		}
	} catch (JMSException e) {
        log.exception(e);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 */
public void sendProgress(int workerIndex, double progress, double timeSec) {
	if (currentTasks[workerIndex] == null) {
		return;
	}

	try {
		long t = System.currentTimeMillis();
	if (!bProgresses[workerIndex] || t - lastMsgTimeStamp[workerIndex] > MessageConstants.INTERVAL_PROGRESS_MESSAGE 
		|| ((int)(progress * 100)) % 25 == 0) { // don't send progress message too frequently
			log.print("sendProgress(" + currentTasks[workerIndex].getSimulationJobIdentifier() + "," + (progress * 100) + "%," + timeSec + ")");
			WorkerEventMessage.sendProgress(workerEventSessions[workerIndex], this, currentTasks[workerIndex], ManageUtils.getHostName(), progress, timeSec);
			
			lastMsgTimeStamp[workerIndex] = System.currentTimeMillis();
			bProgresses[workerIndex] = true;
		}
	} catch (JMSException e) {
        log.exception(e);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 */
public void sendStarting(int workerIndex, String startingMessage) {
	if (currentTasks[workerIndex] == null) {
		return;
	}
	
	try {
		log.print("sendStarting(" + currentTasks[workerIndex].getSimulationJobIdentifier() + ")");
		WorkerEventMessage.sendStarting(workerEventSessions[workerIndex], this, currentTasks[workerIndex], ManageUtils.getHostName(), startingMessage);
	
		lastMsgTimeStamp[workerIndex] = System.currentTimeMillis();
	} catch (JMSException e) {
        log.exception(e);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 */
void sendWorkerAlive(int workerIndex) {
	if (currentTasks[workerIndex] == null) {
		return;
	}

	// have to keep sending the messages because it's important
	try {
		log.print("sendWorkerAlive(" + currentTasks[workerIndex].getSimulationJobIdentifier() + ")");
		WorkerEventMessage.sendWorkerAlive(workerEventSessions[workerIndex], this, currentTasks[workerIndex], ManageUtils.getHostName());
		
		lastMsgTimeStamp[workerIndex] = System.currentTimeMillis();
	} catch (JMSException jmse) {
        log.exception(jmse);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2004 11:21:59 AM)
 */
public void startReceiving() throws JMSException {
	jmsConn.startConnection();
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2004 11:21:59 AM)
 */
public void stopReceiving() throws JMSException {
	jmsConn.stopConnection();
}
}
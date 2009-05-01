package cbit.vcell.messaging.server;

import java.io.File;
import javax.jms.JMSException;
import javax.jms.Message;

import org.vcell.util.FileUtils;
import org.vcell.util.document.KeyValue;

import cbit.vcell.messaging.ControlMessageCollector;
import cbit.vcell.messaging.ControlTopicListener;
import cbit.vcell.messaging.JmsConnection;
import cbit.vcell.messaging.JmsConnectionFactory;
import cbit.vcell.messaging.JmsConnectionFactoryImpl;
import cbit.vcell.messaging.JmsSession;
import cbit.vcell.messaging.JmsUtils;
import cbit.vcell.messaging.MessageConstants;
import cbit.vcell.messaging.MessagePropertyNotFoundException;
import cbit.vcell.messaging.WorkerEventMessage;
import cbit.vcell.messaging.admin.ManageUtils;
import cbit.vcell.messaging.db.VCellServerID;
import cbit.vcell.server.PropertyLoader;
import cbit.vcell.server.SessionLog;
import cbit.vcell.server.StdoutSessionLog;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.Solver;
import cbit.vcell.solver.SolverEvent;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverFactory;
import cbit.vcell.solver.SolverListener;
import cbit.vcell.solver.SolverStatus;
import cbit.vcell.xml.XmlHelper;

public class JavaSimulationExecutable implements ControlTopicListener, SolverListener  {
	String[] arguments = null;
	
	private boolean bProgress = true;	
	private SimulationTask simulationTask = null;
	private Solver solver = null;
	private long lastMsgTimeStamp = 0;

	private JmsConnection jmsConn = null;
	private JmsSession workerEventSession = null;
	
	private String userDirectory = null;
	private String inputFile = null;
	int jobIndex = 0;
	int taskID = 0;
	
	SessionLog log = null;
	
	class KeepAliveThread extends Thread {
		public KeepAliveThread() {
			super();
			setName("KeepAliveThread_Compute");
		}	
		public void run() {
			while (true) {
				try {
					sleep(MessageConstants.INTERVAL_PING_SERVER);
				} catch (InterruptedException ex) {
				}
		
				long t = System.currentTimeMillis();
				if (lastMsgTimeStamp != 0 && t - lastMsgTimeStamp > MessageConstants.INTERVAL_PING_SERVER) {
					log.print("@@@@Worker:Sending alive message");
					sendAlive();
				}
			}
		}	
	}	
	
/**
 * SimulationWorker constructor comment.
 * @param argName java.lang.String
 * @param argParentNode cbit.vcell.appserver.ComputationalNode
 * @param argInitialContext javax.naming.Context
 */
public JavaSimulationExecutable(String[] args) {
	arguments = args;
}

private void start() {
	try {		
		reconnect();
		
		if (arguments.length != 6) {
			throw new RuntimeException("Missing arguments: " + JavaSimulationExecutable.class.getName() + "{alpha|beta|rel} userDir inputFile jobIndex -tid taskID");
		}
		
		int argCount = 0;
		String serverID = arguments[argCount ++];
		if (!serverID.equalsIgnoreCase(VCellServerID.getSystemServerID().toString())) {
			throw new IllegalArgumentException("wrong server id : " + arguments[argCount]);
		}
		userDirectory = arguments[argCount ++];
		inputFile = arguments[argCount ++];
		jobIndex = Integer.parseInt(arguments[argCount ++]);
		String tid = arguments[argCount ++];
		if (tid.equals("-tid")) {
			taskID = Integer.parseInt(arguments[argCount ++]);
		} else {
			throw new IllegalArgumentException("wrong arguments : " + tid);
		}
		
		String xmlString = FileUtils.readFileToString(new File(userDirectory, inputFile));
		Simulation simulation = XmlHelper.XMLToSim(xmlString);
		simulationTask = new SimulationTask(new SimulationJob(simulation, null, jobIndex), taskID);
		
		log = new StdoutSessionLog(simulationTask.getSimulationJobIdentifier());	
		
		log.print("Start keep alive thread");
		new KeepAliveThread().start();
		
		runSimulation();
		
		try {
			if (jmsConn != null) {
				jmsConn.close();
			}
		} catch (Exception ex) {
			log.exception(ex);
		}
	} catch (Throwable ex) {
		ex.printStackTrace();
		sendFailed(ex.getMessage());
	}
}

protected void reconnect() throws JMSException {
	JmsConnectionFactory jmsConnectorFactory = new JmsConnectionFactoryImpl();
	jmsConn = jmsConnectorFactory.createConnection();
	workerEventSession = jmsConn.getAutoSession();		
	
	JmsSession serviceListenTopicSession = jmsConn.getAutoSession();
	serviceListenTopicSession.setupTopicListener(JmsUtils.getTopicServiceControl(), null, new ControlMessageCollector(this));
	jmsConn.startConnection();
}

private void runSimulation() throws SolverException {
	solver = SolverFactory.createSolver(log, new File(userDirectory), simulationTask.getSimulationJob());
	solver.addSolverListener(this);
	solver.startSolver();
	
	while (true){
		try { 
			Thread.sleep(500); 
		} catch (InterruptedException e) {
		}

		cbit.vcell.solver.SolverStatus solverStatus = solver.getSolverStatus();
		if (solverStatus != null) {
			if (solverStatus.getStatus() != SolverStatus.SOLVER_STARTING &&
				solverStatus.getStatus() != SolverStatus.SOLVER_READY &&
				solverStatus.getStatus() != SolverStatus.SOLVER_RUNNING){
				break;
			}
		}		
	}
}

/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(String[] args) {	
	try {
		PropertyLoader.loadProperties();
	
		JavaSimulationExecutable worker = new JavaSimulationExecutable(args);
		worker.start();
	} catch (Throwable ex) {
		ex.printStackTrace();
	} finally {
		System.exit(0);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/2003 2:49:54 PM)
 * @param message javax.jms.Message
 * @exception javax.jms.JMSException The exception description.
 */
public void onControlTopicMessage(Message message) throws JMSException {
	
	log.print("SimulationWorker::onControlTopicMessage(): " + JmsUtils.toString(message));
	try {
		String msgType = (String)JmsUtils.parseProperty(message, MessageConstants.MESSAGE_TYPE_PROPERTY, String.class);

		if (msgType != null && msgType.equals(MessageConstants.MESSAGE_TYPE_STOPSIMULATION_VALUE)) {			
			Long longkey = (Long)JmsUtils.parseProperty(message, MessageConstants.SIMKEY_PROPERTY, long.class);
			KeyValue simKey = new KeyValue(longkey + "");
			int jobIndex = ((Integer)JmsUtils.parseProperty(message, MessageConstants.JOBINDEX_PROPERTY, int.class)).intValue();
			
			if (simKey.equals(simulationTask.getSimKey()) && jobIndex == simulationTask.getSimulationJob().getJobIndex()) {
				solver.stopSolver();
			}
		} 	
	} catch (MessagePropertyNotFoundException ex) {
		log.exception(ex);
		return;
	}
}

private void sendAlive() {
	// have to keep sending the messages because it's important
	try {
		log.print("sendWorkerAlive(" + simulationTask.getSimulationJobIdentifier() + ")");
		WorkerEventMessage.sendWorkerAlive(workerEventSession, this, simulationTask, ManageUtils.getHostName());
		
		lastMsgTimeStamp = System.currentTimeMillis();
	} catch (JMSException jmse) {
        log.exception(jmse);
	}
}

private void sendFailed(String failureMessage) {		
	try {
		log.print("sendFailure(" + simulationTask.getSimulationJobIdentifier() + "," + failureMessage +")");
		WorkerEventMessage.sendFailed(workerEventSession, this, simulationTask, ManageUtils.getHostName(), failureMessage);
	} catch (JMSException ex) {
        log.exception(ex);
	}
}

private void sendNewData(double progress, double timeSec) {	
	try {
		long t = System.currentTimeMillis();
		if (bProgress || t - lastMsgTimeStamp > MessageConstants.INTERVAL_PROGRESS_MESSAGE) { // don't send data message too frequently
			log.print("sendNewData(" + simulationTask.getSimulationJobIdentifier() + "," + (progress * 100) + "%," + timeSec + ")");		
			WorkerEventMessage.sendNewData(workerEventSession, this, simulationTask, ManageUtils.getHostName(), progress, timeSec);
		
			lastMsgTimeStamp = System.currentTimeMillis();
			bProgress = false;
		}
	} catch (JMSException e) {
		e.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 */
private void sendProgress(double progress, double timeSec) {
	try {
		long t = System.currentTimeMillis();
		if (!bProgress || t - lastMsgTimeStamp > MessageConstants.INTERVAL_PROGRESS_MESSAGE 
				|| ((int)(progress * 100)) % 25 == 0) { // don't send progress message too frequently
			log.print("sendProgress(" + simulationTask.getSimulationJobIdentifier() + "," + (progress * 100) + "%," + timeSec + ")");
			WorkerEventMessage.sendProgress(workerEventSession, this, simulationTask, ManageUtils.getHostName(), progress, timeSec);
			
			lastMsgTimeStamp = System.currentTimeMillis();
			bProgress = true;
		}
	} catch (JMSException e) {
		log.exception(e);
	}
}

private void sendCompleted(double progress, double timeSec) {
	// have to keep sending the messages because it's important
	try {
		log.print("sendComplete(" + simulationTask.getSimulationJobIdentifier() + ")");
		WorkerEventMessage.sendCompleted(workerEventSession, this, simulationTask, ManageUtils.getHostName(),  progress, timeSec);
	} catch (JMSException jmse) {
        log.exception(jmse);
	}
}

private void sendStarting(String startingMessage) {
	try {
		log.print("sendStarting(" + simulationTask.getSimulationJobIdentifier() + ")");
		WorkerEventMessage.sendStarting(workerEventSession, this, simulationTask, ManageUtils.getHostName(), startingMessage);
	} catch (JMSException e) {
        log.exception(e);
	}
}

/**
 * Invoked when the solver aborts a calculation (abnormal termination).
 * @param event indicates the solver and the event type
 */
public final void solverAborted(SolverEvent event) {
	String failMsg = event.getMessage();
	if (failMsg == null) {
		failMsg = "Solver aborted";
	}
		
	sendFailed(failMsg);
}

/**
 * Invoked when the solver finishes a calculation (normal termination).
 * @param event indicates the solver and the event type
 */
public final void solverFinished(SolverEvent event) {
	sendCompleted(event.getProgress(), event.getTimePoint());
}


/**
 * Invoked when the solver stores values in the result set.
 * @param event indicates the solver and the event type
 */
public final void solverPrinted(SolverEvent event) {
	// can never get data messages here
	sendNewData(event.getProgress(), event.getTimePoint());
}


/**
 * Invoked when the solver stores values in the result set.
 * @param event indicates the solver and the event type
 */
public final void solverProgress(SolverEvent event) {
	// can never get progress message here
	sendProgress(event.getProgress(), event.getTimePoint());
}


/**
 * Invoked when the solver begins a calculation.
 * @param event indicates the solver and the event type
 */
public final void solverStarting(SolverEvent event) {
	String startMsg = event.getMessage();
	if (startMsg == null) {
		startMsg = "Solver starting";
	}
	
	sendStarting(startMsg);
}


/**
 * Invoked when the solver stops a calculation, usually because
 * of a user-initiated stop call.
 * @param event indicates the solver and the event type
 */
public final void solverStopped(SolverEvent event) {		
	log.print("Caught solverStopped(" + event.getSource() + ")");
	// Don't send message anymore because the dispatcher will update the database anyway no matter if the worker responds
	//workerMessaging.sendStopped(event.getProgress(), event.getTimePoint());
}

}
package cbit.vcell.messaging.admin;
import static cbit.vcell.messaging.admin.ManageConstants.*;
import java.io.*;
import java.sql.SQLException;
import java.util.*;
import javax.jms.*;
import static cbit.htc.PBSConstants.*;
import cbit.htc.PBSConstants;
import cbit.htc.PBSUtils;
import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.util.ExecutableException;
import cbit.vcell.server.*;
import cbit.vcell.messaging.*;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.messaging.db.VCellServerID;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DbDriver;

/**
 * Insert the type's description here.
 * Creation date: (8/7/2003 1:53:27 PM)
 * @author: Fei Gao
 */
public class ServerManagerDaemon implements ControlTopicListener {
	private cbit.vcell.server.SessionLog log = null;
	private VCellTopicConnection topicConn = null;
	private VCellQueueConnection queueConn = null;
	private JmsConnectionFactory jmsConnFactory = null;
	private boolean stopped = false;
	private List<ServiceInstanceStatus> serviceAliveList = Collections.synchronizedList(new ArrayList<ServiceInstanceStatus>());
	private List<ServiceStatus> serviceList = Collections.synchronizedList(new ArrayList<ServiceStatus>());
	private cbit.vcell.messaging.VCellTopicSession topicSession = null;
	private cbit.vcell.messaging.VCellTopicSession listenSession = null;
	
	ServiceInstanceStatus serviceInstanceStatus = null;
	
	private ConnectionFactory conFactory = null;
	private KeyFactory keyFactory = null;
	private AdminDBTopLevel adminDbTop = null;	

/**
 * ServerManagerMessaging constructor comment.
 */
public ServerManagerDaemon() throws IOException, SQLException, javax.jms.JMSException {
	super();	
	
	serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID().toString(), SERVICE_TYPE_SERVERMANAGER, 0, ManageUtils.getHostName(), new Date(), true); 
	log = new StdoutSessionLog(serviceInstanceStatus.getID());
	try {
		conFactory = new cbit.sql.OraclePoolingConnectionFactory(log);
	} catch (ClassNotFoundException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	} catch (IllegalAccessException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	} catch (InstantiationException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
	keyFactory = new cbit.sql.OracleKeyFactory();	
	DbDriver.setKeyFactory(keyFactory);
	adminDbTop = new AdminDBTopLevel(conFactory,log);
	
	reconnect();
}

private void reconnect() throws JMSException {
	jmsConnFactory = new JmsConnectionFactoryImpl();
	
	queueConn = jmsConnFactory.createQueueConnection();
	new JmsFileSender(queueConn, log);
	queueConn.startConnection();
	
	topicConn = jmsConnFactory.createTopicConnection();
	topicSession = topicConn.getAutoSession();
	
	listenSession = topicConn.getAutoSession();
	listenSession.setupListener(JmsUtils.getTopicDaemonControl(), getMessageFilter(), new ControlMessageCollector(this));
	topicConn.startConnection();	
}

private void startAllServices() throws JMSException, SQLException, DataAccessException {
	log.print("Starting all the services");
	serviceList = Collections.synchronizedList(adminDbTop.getAllServiceStatus(true));	
	
	pingAll();	

	Iterator<ServiceStatus> iter = serviceList.iterator();
	while (iter.hasNext()) {
		ServiceStatus service = iter.next();		
		if (service.getServiceSpec().getStartupType() == SERVICE_STARTUPTYPE_AUTOMATIC) {			
			boolean alive = false;
			ServiceInstanceStatus foundSis = null; 
			Iterator<ServiceInstanceStatus> aliveIter = serviceAliveList.iterator();
			while (aliveIter.hasNext()) {
				ServiceInstanceStatus sis = aliveIter.next();
				if (sis.getSpecID().equals(service.getServiceSpec().getID())) {
					if (alive) { // there are more than instances for the same service
						if (foundSis.getStartDate().compareTo(sis.getStartDate()) > 0) { // kill the service with earlier start date
							stopService(sis);
						} else {
							stopService(foundSis);
							foundSis = sis;
						}
					} else {
						alive = true;
						foundSis = sis;
					}
				}
			}
			
			if (!alive) {
				try {
					startAService(service);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}	
}

private void stopService(ServiceInstanceStatus sis) {
	try {			
		Message msg = topicSession.createMessage();
			
		msg.setStringProperty(MESSAGE_TYPE_PROPERTY, MESSAGE_TYPE_STOPSERVICE_VALUE);
		msg.setStringProperty(SERVICE_ID_PROPERTY, sis.getID());
		
		log.print("sending stop service message [" + JmsUtils.toString(msg) + "]");		
		topicSession.publishMessage(JmsUtils.getTopicDaemonControl(), msg);		
		
	} catch (Exception ex) {
		ex.printStackTrace();
	}	
}

private void startAService(ServiceStatus service) throws UpdateSynchronizationException, SQLException {
	log.print("starting service " + service);
	AdminDBTopLevel.TransactionalServiceOperation tso = new AdminDBTopLevel.TransactionalServiceOperation() {
		public ServiceStatus doOperation(ServiceStatus oldStatus) throws Exception {
			String jobid = submit2PBS(oldStatus);
			ServiceStatus newServiceStatus = null;
			if (jobid == null) {
				newServiceStatus = new ServiceStatus(oldStatus.getServiceSpec(), null, SERVICE_STATUS_FAILED, "unknown pbs exception",	jobid);
			} else {
				long t = System.currentTimeMillis();
				int status;
				while (true) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException ex) {
					}
					
					status = PBSUtils.getJobStatus(jobid);
					if (PBSUtils.isJobExisting(status)){						
						if (!PBSUtils.isJobExecOK(jobid)) {
							newServiceStatus = new ServiceStatus(oldStatus.getServiceSpec(), null, SERVICE_STATUS_FAILED, 
									"Job [" + jobid + "] exited unexpectedly: " + PBSUtils.getJobExecStatus(jobid),
									jobid);
						} else {
							// should never happen
							newServiceStatus = new ServiceStatus(oldStatus.getServiceSpec(), null, SERVICE_STATUS_FAILED, 
									"unexpected exit immediately after submit",
									jobid);	
						}
						break;
					} else if (PBSUtils.isJobRunning(status)) {						
						newServiceStatus = new ServiceStatus(oldStatus.getServiceSpec(), null, SERVICE_STATUS_RUNNING, "running", jobid);	
						break;
					} else if (System.currentTimeMillis() - t > 30 * MessageConstants.SECOND) {
						String pendingReason = PBSUtils.getPendingReason(jobid);
						PBSUtils.killJob(jobid); // kill the job if it takes too long to dispatch the job.
						newServiceStatus = new ServiceStatus(oldStatus.getServiceSpec(), null, SERVICE_STATUS_FAILED, 
								"PBS Job scheduler timed out. Please try again later. (Job [" + jobid + "]: " + pendingReason + ")",
								jobid);						
						break;
					}
				}
			} 			
			return newServiceStatus;
		}
	};
	
	adminDbTop.updateServiceStatus(service, tso, true);
}	

private String submit2PBS(ServiceStatus service) throws IOException, ExecutableException {
	killService(service);
	
	String executable = PropertyLoader.getRequiredProperty(PropertyLoader.serviceSubmitScript);
	
	String type = service.getServiceSpec().getType();
	int ordinal = service.getServiceSpec().getOrdinal();
	String cmdArguments = VCellServerID.getSystemServerID().toString().toLowerCase() + " " 
		+ type + " " + ordinal + " " + service.getServiceSpec().getMemoryMB(); // site, type, ordinal, memory
	
	File sub_file = File.createTempFile("service", ".pbs.sub");
	log.print("PBS sub file  for service " + service.getServiceSpec() + " is " + sub_file.getAbsolutePath());
	return PBSUtils.submitJob((String)null, service.getServiceSpec().getID(), sub_file.getAbsolutePath(), executable, cmdArguments, 1, service.getServiceSpec().getMemoryMB(), PBSConstants.PBS_ARCH_LINUX);
}
/**
 * This method was created in VisualAge.
 * @return int
 */
private java.lang.String getMessageFilter() {
	return MESSAGE_TYPE_PROPERTY + " NOT IN (" 
		+ "'" + MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE + "'" 
		+ ")";
//		+ " OR (" + ManageConstants.MESSAGE_TYPE_PROPERTY + "='" + ManageConstants.MESSAGE_TYPE_IAMALIVE_VALUE + "'"
//		+ " AND " + ManageConstants.SERVER_NAME_PROPERTY + " IS NOT NULL"
//		+ ")";
}

/**
 * onMessage method comment.
 */
public void onControlTopicMessage(Message message) {
	try {		
		log.print("onMessage [" + JmsUtils.toString(message) + "]");		
			
		String msgType = (String)JmsUtils.parseProperty(message, MESSAGE_TYPE_PROPERTY, String.class);
		
		if (msgType.equals(MESSAGE_TYPE_ASKPERFORMANCESTATUS_VALUE)) {
			Message reply = topicSession.createObjectMessage(serviceInstanceStatus);
			reply.setStringProperty(MESSAGE_TYPE_PROPERTY, MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE);
			reply.setStringProperty(SERVICE_ID_PROPERTY, serviceInstanceStatus.getID());
			topicSession.publishMessage(JmsUtils.getTopicDaemonControl(), reply);			
			log.print("sending reply [" + JmsUtils.toString(reply) + "]");			
		} else if (msgType.equals(MESSAGE_TYPE_IAMALIVE_VALUE)) {
			on_iamalive(message);			
		} else if (msgType.equals(MESSAGE_TYPE_STARTSERVICE_VALUE)) {
		} else if (msgType.equals(MESSAGE_TYPE_STOPSERVICE_VALUE)) {
			on_stopservice(message);
		} else if (msgType.equals(MESSAGE_TYPE_REFRESHSERVERMANAGER_VALUE)) {
			synchronized (this) {
				notify();
			}						
		}
	} catch (Exception ex) {
		log.exception(ex);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 4:47:40 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {	
	try {
		if (args.length > 1) {
			System.out.println("Missing arguments: " + ServerManagerDaemon.class.getName() + " [logdir]");
			System.exit(1);
		}
		
		if (args.length == 1) {
			File logdir = new File(args[0]);
			if (!logdir.exists()) {
				throw new RuntimeException("Log directory doesn't exist");
			}
				
			// log file name:
			// hostname_A_Data_0.log : alpha first data on hostname
			// hostname_B_Db_0.log : beta first database on hostname
			// hostname_R_Export_0.log : rel first export on hostname
			File logfile = new File(logdir, "ServerManager_" + ManageUtils.getHostName() + ".log");
			java.io.PrintStream ps = new PrintStream(new FileOutputStream(logfile), true); // don't append
			System.setOut(ps);
			System.setErr(ps);			
		}
		cbit.vcell.server.PropertyLoader.loadProperties();
		new ServerManagerDaemon().start();		
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		System.exit(1);
	}
}

private void on_iamalive(Message message) throws JMSException  {
	Object obj = ((ObjectMessage)message).getObject();			
	if (obj instanceof ServiceInstanceStatus) {
		ServiceInstanceStatus status = (ServiceInstanceStatus)obj;			
		serviceAliveList.add(status);			
	}
}

/**
* This method was created in VisualAge.
* @return int
*/
private void on_stopservice(Message message) throws JMSException {
	try {
		String serviceID = (String)JmsUtils.parseProperty(message, SERVICE_ID_PROPERTY, String.class);
		
		if (serviceID != null) {
			if (serviceID.equals(serviceInstanceStatus.getID())) { // stop myself
				System.exit(0);
			}
			Iterator<ServiceStatus> iter = serviceList.iterator();
			while (iter.hasNext()) {
				ServiceStatus service = iter.next();		
				if (service.getServiceSpec().getID().equals(serviceID)) {
					String pbsJobId = service.getPbsJobId();
					if (pbsJobId != null && PBSUtils.isJobRunning(pbsJobId)) {
						try {
							Thread.sleep(5 * MessageConstants.SECOND); // wait 5 seconds
						} catch (InterruptedException ex) {							
						}					
						// if the service is not stopped, kill it from PBS
						if (PBSUtils.isJobRunning(pbsJobId)) {
							PBSUtils.killJob(pbsJobId);
						}
					}
					break;
				}
			}
		}
	} catch (MessagePropertyNotFoundException ex) {
		log.exception(ex);
	}
}


/**
* onMessage method comment.
*/
private boolean ping(ServiceSpec service) throws JMSException {
	Message msg = topicSession.createMessage();
		
	msg.setStringProperty(MESSAGE_TYPE_PROPERTY, MESSAGE_TYPE_ISSERVICEALIVE_VALUE);
	msg.setStringProperty(SERVICE_ID_PROPERTY, service.getID());

	log.print("sending ping message [" + JmsUtils.toString(msg) + "]");
	
	Message reply = topicSession.request(this, JmsUtils.getTopicDaemonControl(), msg, ManageConstants.INTERVAL_PING_RESPONSE);

	log.print("got reply message [" + JmsUtils.toString(reply) + "]");

	if (reply == null) {
		return false;
	} else {
		try {
			String msgType = (String)JmsUtils.parseProperty(reply, MESSAGE_TYPE_PROPERTY, String.class);
			if (!msgType.equals(MESSAGE_TYPE_IAMALIVE_VALUE)) {
				return false;
			}
		} catch (MessagePropertyNotFoundException ex) {
			log.exception(ex);
			return false;
		}
	}
		
	return true;
}


/**
* onMessage method comment.
*/
private void pingAll() throws JMSException {
	
	serviceAliveList.clear();
	
	Message msg = topicSession.createMessage();
		
	msg.setStringProperty(MESSAGE_TYPE_PROPERTY, MESSAGE_TYPE_ISSERVICEALIVE_VALUE);

	log.print("sending ping message [" + JmsUtils.toString(msg) + "]");
	
	topicSession.publishMessage(JmsUtils.getTopicDaemonControl(), msg);

	try {
		Thread.sleep(INTERVAL_PING_RESPONSE);
	} catch (InterruptedException ex) {
		ex.printStackTrace();
	}
}

private void killService(ServiceStatus service) {
	if (service.getPbsJobId() != null) {
		PBSUtils.killJob(service.getPbsJobId() + "");
	}
}
/**
* Insert the method's description here.
* Creation date: (8/7/2003 5:12:22 PM)
*/
public void start() {
	while (!stopped) {			
		try {
			startAllServices();
		} catch (Throwable exc) {
			log.exception(exc);
		}		
		try {
			synchronized (this) {			
				wait(INTERVAL_PING_SERVICE);
			}
		} catch (InterruptedException exc) {
		}			
	}
}
}
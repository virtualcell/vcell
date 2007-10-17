package cbit.vcell.messaging.admin;
import static cbit.htc.PBSConstants.PBS_MEM_OVERHEAD_MB;
import static cbit.vcell.messaging.admin.ManageConstants.*;
import static cbit.vcell.messaging.MessageConstants.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

import javax.jms.*;

import cbit.htc.PBSConstants;
import cbit.htc.PBSUtils;
import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.util.ExecutableException;
import cbit.vcell.server.*;
import cbit.vcell.messaging.*;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.messaging.db.VCellServerID;
import cbit.vcell.messaging.server.DatabaseServer;
import cbit.vcell.messaging.server.SimDataServer;
import cbit.vcell.messaging.server.SimulationDispatcher;
import cbit.vcell.messaging.server.SimulationWorker;
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
	private Set<String> serviceAliveSet = Collections.synchronizedSet(new HashSet<String>());
	List<ServiceStatus> serviceList = Collections.synchronizedList(new ArrayList<ServiceStatus>());
	private cbit.vcell.messaging.VCellTopicSession topicSession = null;
	private cbit.vcell.messaging.VCellTopicSession listenSession = null;
	
	ServiceSpec serviceSpec = null;
	
	private ConnectionFactory conFactory = null;
	private KeyFactory keyFactory = null;
	private AdminDBTopLevel adminDbTop = null;	

/**
 * ServerManagerMessaging constructor comment.
 */
public ServerManagerDaemon() throws IOException, SQLException, javax.jms.JMSException {
	super();	
	
	serviceSpec = new ServiceSpec(VCellServerID.getSystemServerID().toString(), ManageConstants.SERVICE_TYPE_SERVERMANAGER, 0, 
			ManageConstants.SERVICE_STARTUPTYPE_AUTOMATIC, 100, true); 
	log = new StdoutSessionLog("ServerManager");
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
	serviceList = adminDbTop.getAllServiceStatus(true);	
	
	pingAll();
	

	Iterator<ServiceStatus> iter = serviceList.iterator();
	while (iter.hasNext()) {
		ServiceStatus service = iter.next();		
		if (service.getServiceSpec().getStartupType() == ManageConstants.SERVICE_STARTUPTYPE_AUTOMATIC) {
			if (serviceAliveSet.contains(service.getServiceSpec().getID())) {
				continue;
			}
			
			startAService(service, true);
		}
	}	
}

private void startAService(ServiceStatus service, boolean boot) throws UpdateSynchronizationException, SQLException {
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
					if (status == PBSConstants.PBS_STATUS_EXITING){
						int exitCode = PBSUtils.getJobExitCode(jobid);
						if (exitCode < 0) {
							newServiceStatus = new ServiceStatus(oldStatus.getServiceSpec(), null, SERVICE_STATUS_FAILED, 
									"Job [" + jobid + "] exited unexpectedly: [" + exitCode + ":" + PBSConstants.PBS_JOB_EXEC_STATUS[-exitCode] + "]",
									jobid);
						} else if (exitCode > 0) {
							newServiceStatus = new ServiceStatus(oldStatus.getServiceSpec(), null, SERVICE_STATUS_FAILED, 
									"Job [" + jobid + "] was killed with system signal " + exitCode, 
									jobid);
						} else {
							// should never happen
							newServiceStatus = new ServiceStatus(oldStatus.getServiceSpec(), null, SERVICE_STATUS_FAILED, 
									"unexpected exit immediately after submit",
									jobid);	
						}
						break;
					} else if (status == PBSConstants.PBS_STATUS_RUNNING) {						
						newServiceStatus = new ServiceStatus(oldStatus.getServiceSpec(), null, SERVICE_STATUS_RUNNING, 
								"running",
								jobid);	
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
	if (service.getPbsJobId() != null) {
		PBSUtils.killJob(service.getPbsJobId() + "");
	}
	File sub_file = File.createTempFile("service", ".pbs.sub");
	PrintWriter pw = new PrintWriter(sub_file);
	
	pw.println("#PBS -N " + service.getServiceSpec().getID());
	pw.println("#PBS -l select=1:ncpus=1:mem=" + (service.getServiceSpec().getMemoryMB() + PBS_MEM_OVERHEAD_MB) + "mb");
	pw.println("#PBS -q workq" + service.getServiceSpec().getServerID());
	pw.println("#PBS -m a");
	pw.println("#PBS -M fgao@uchc.edu");
	pw.println("#PBS -j oe");
	pw.println();
	
	String cmdArguments = "";
	String mainclass = "";
	String logDir = PropertyLoader.getRequiredProperty(PropertyLoader.serviceLogDir);
	
	File propertyFile = new File(PropertyLoader.getRequiredProperty(PropertyLoader.servicePropertyFile));
	if (!propertyFile.exists()) {
		throw new RuntimeException("property file for service " + service + " doesn't exist");
	}
	String jvm_classpath=PropertyLoader.getRequiredProperty(PropertyLoader.serviceClassPath);
	String jvm_prop = "-Xmx" + service.getServiceSpec().getMemoryMB() + "M -Dvcell.propertyfile=" + PropertyLoader.getRequiredProperty(PropertyLoader.servicePropertyFile);
	
	String type = service.getServiceSpec().getType();
	int ordinal = service.getServiceSpec().getOrdinal();
	if (type.equals(SERVICETYPE_DB_VALUE)) {
		cmdArguments = ordinal + " " // servicename 
			+ logDir; // logfile
		mainclass = DatabaseServer.class.getName();
	} else if (type.equals(SERVICETYPE_DATA_VALUE)) {
		cmdArguments = ordinal + " " // servicename 
			+ logDir; // logfile
		mainclass = SimDataServer.class.getName();
	} else if (type.equals(SERVICETYPE_DISPATCH_VALUE)) {
		cmdArguments = ordinal + " " // servicename 
			+ logDir; // logfile
		mainclass = SimulationDispatcher.class.getName();
	} else if (type.equals(SERVICETYPE_DATAEXPORT_VALUE)) {
		cmdArguments = ordinal + " " // servicename 
			+ "exportonly "
			+ logDir; // logfile
		mainclass = SimDataServer.class.getName();		
	} else if (type.equals(SERVICETYPE_HTCCOMPUTE_VALUE)) {
		cmdArguments = "-pbs " 
			+ ordinal + " " // servicename
			+ "0 " // memory, is not useful when submit to PBS
			+ logDir; // logfile
		mainclass = SimulationWorker.class.getName();		
	} else if (type.equals(SERVICETYPE_ODECOMPUTE_VALUE)) {
		cmdArguments = "-java " 
			+ ordinal + " " // servicename
			+ "0 " // memory, is not useful for ODE
			+ logDir; // logfile
		mainclass = SimulationWorker.class.getName();		
	} else if (type.equals(SERVICETYPE_LOCALCOMPUTE_VALUE)) {
		cmdArguments = "-nohtc " 
			+ ordinal + " " // servicename
			+ "500 " // memory, used in message filter
			+ logDir; // logfile
		mainclass = SimulationWorker.class.getName();		
	}
	
	pw.println("java " + jvm_prop + " -cp " + jvm_classpath + " " + mainclass + " " + cmdArguments);	
	pw.close();
	
	log.print("PBS sub file  for service " + serviceSpec + " is " + sub_file.getAbsolutePath());
	String jobid = PBSUtils.submitJob(sub_file.getAbsolutePath());
	return jobid;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
private java.lang.String getMessageFilter() {
	return ManageConstants.MESSAGE_TYPE_PROPERTY + " NOT IN (" 
		+ "'" + ManageConstants.MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE + "'" 
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
			
		String msgType = (String)JmsUtils.parseProperty(message, ManageConstants.MESSAGE_TYPE_PROPERTY, String.class);
		
		if (msgType.equals(MESSAGE_TYPE_ASKPERFORMANCESTATUS_VALUE)) {
			Message reply = topicSession.createObjectMessage(serviceSpec);
			reply.setStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY, MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE);
			reply.setStringProperty(SERVICE_ID_PROPERTY, serviceSpec.getID());
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
	try {
		String serviceID = (String)JmsUtils.parseProperty(message, ManageConstants.SERVICE_ID_PROPERTY, String.class);
		
		if (serviceID != null) {
			serviceAliveSet.add(serviceID);
		}
	} catch (MessagePropertyNotFoundException ex) {
		log.exception(ex);
	}
}

/**
* This method was created in VisualAge.
* @return int
*/
private void on_stopservice(Message message) throws JMSException {
	try {
		String serviceID = (String)JmsUtils.parseProperty(message, ManageConstants.SERVICE_ID_PROPERTY, String.class);
		
		if (serviceID != null) {
			Iterator<ServiceStatus> iter = serviceList.iterator();
			while (iter.hasNext()) {
				ServiceStatus service = iter.next();		
				if (service.getServiceSpec().getID().equals(serviceID)) {
					String pbsJobId = service.getPbsJobId();
					if (pbsJobId != null && PBSUtils.getJobStatus(pbsJobId) == PBSConstants.PBS_STATUS_RUNNING) {
						try {
							Thread.sleep(5 * MessageConstants.SECOND); // wait 5 seconds
						} catch (InterruptedException ex) {							
						}					
						// if the service is not stopped, kill it from PBS
						if (PBSUtils.getJobStatus(pbsJobId) == PBSConstants.PBS_STATUS_RUNNING) {
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
		
	msg.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_ISSERVICEALIVE_VALUE);
	msg.setStringProperty(ManageConstants.SERVICE_ID_PROPERTY, service.getID());

	log.print("sending ping message [" + JmsUtils.toString(msg) + "]");
	
	Message reply = topicSession.request(this, JmsUtils.getTopicDaemonControl(), msg, ManageConstants.INTERVAL_PING_RESPONSE);

	log.print("got reply message [" + JmsUtils.toString(reply) + "]");

	if (reply == null) {
		return false;
	} else {
		try {
			String msgType = (String)JmsUtils.parseProperty(reply, ManageConstants.MESSAGE_TYPE_PROPERTY, String.class);
			if (!msgType.equals(ManageConstants.MESSAGE_TYPE_IAMALIVE_VALUE)) {
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
	
	serviceAliveSet.clear();
	
	Message msg = topicSession.createMessage();
		
	msg.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_ISSERVICEALIVE_VALUE);

	log.print("sending ping message [" + JmsUtils.toString(msg) + "]");
	
	topicSession.publishMessage(JmsUtils.getTopicDaemonControl(), msg);

	try {
		Thread.sleep(10 * ManageConstants.SECOND);
	} catch (InterruptedException ex) {
		ex.printStackTrace();
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
				wait(ManageConstants.INTERVAL_PING_SERVICE);
			}
		} catch (InterruptedException exc) {
		}			
	}
}}
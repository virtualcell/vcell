package cbit.vcell.messaging.admin;
import javax.jms.*;

import cbit.util.SessionLog;
import cbit.vcell.messaging.*;

import java.util.*;
import org.jdom.*;
import java.io.*;
import org.jdom.output.XMLOutputter;

/**
 * Insert the type's description here.
 * Creation date: (8/18/2003 10:16:00 AM)
 * @author: Fei Gao
 */
public abstract class MessagingDaemon implements ControlTopicListener {
	protected SessionLog log = null;
	protected VCellTopicConnection topicConn = null;
	protected VCellQueueConnection queueConn = null;
	protected List replyList = Collections.synchronizedList(new ArrayList());
	protected VCServerInfo vcServerInfo = null;
	protected JmsConnectionFactory jmsConnFactory = null;
	protected String configFileName = null;
	protected boolean stopped = false;
	protected JmsFileSender fileChannelSender = null;
	protected Map processMap = Collections.synchronizedMap(new HashMap());
	
	protected cbit.vcell.messaging.VCellTopicSession topicSession = null;
	protected cbit.vcell.messaging.VCellTopicSession listenSession = null;

/**
 * MessagingServer constructor comment.
 */
public MessagingDaemon(String serverType, String serverName, String configFileName0) throws java.net.UnknownHostException, FileNotFoundException, IOException, JMSException, JDOMException  {
	super();	

	this.configFileName = configFileName0;
	
	String hostName = ManageUtils.getLocalHostName();
	vcServerInfo = new VCServerInfo(hostName, serverType, serverName);
	vcServerInfo.setBootTime(new Date());
	vcServerInfo.setAlive(true);
	
	readConfiguration();	
	reconnect();
	
	startAllServices();
}


/**
 * Insert the method's description here.
 * Creation date: (8/26/2003 11:38:00 AM)
 * @param changes java.util.Map
 */
public void doChange(LinkedList changes, String newDir) {
	if (newDir != null) {
		vcServerInfo.setArchiveDir(newDir);
	}
	
	Iterator iter = changes.iterator();
	while (iter.hasNext()) {
		VCServiceInfo serviceInfo = (VCServiceInfo)iter.next();
		switch (serviceInfo.getModifier()) {
			case ManageConstants.SERVICE_MODIFIER_NEW: {
				vcServerInfo.addService(serviceInfo);
				startAService(serviceInfo, true);
				break;
			}
				
			case ManageConstants.SERVICE_MODIFIER_MODIFY: {
				vcServerInfo.modifyService(serviceInfo);
				if (vcServerInfo.isServerManager()) {
					//need to send message to stop all the services of the bootstrap
					((ServerManagerDaemon)this).sendStopBootstrap(listenSession, serviceInfo);
				} else {
					//need to send message to stop the service
					((BootstrapDaemon)this).sendStopService(listenSession, serviceInfo);
				}
				stopAService(serviceInfo);
				startAService(serviceInfo, true);
				break;
			}

			case ManageConstants.SERVICE_MODIFIER_DELETE: {				
				vcServerInfo.deleteService(serviceInfo);
				if (vcServerInfo.isServerManager()) {
					((ServerManagerDaemon)this).sendStopBootstrap(listenSession, serviceInfo);
				} else {
					((BootstrapDaemon)this).sendStopService(listenSession, serviceInfo);
				}
				stopAService(serviceInfo);
				break;
			}
		}
	}

}


/**
 * This method was created in VisualAge.
 * @return int
 */
protected abstract String getMessageFilter();


/**
 * This method was created in VisualAge.
 * @return int
 */
protected synchronized void on_changeconfig(Message message) throws JMSException {
	try {
		if ( !(message instanceof ObjectMessage)) {
			return;
		}

		String hostName = (String)JmsUtils.parseProperty(message, MessageConstants.HOSTNAME_PROPERTY, String.class);
		String serverType = (String)JmsUtils.parseProperty(message, ManageConstants.SERVER_TYPE_PROPERTY, String.class);
		String serverName = (String)JmsUtils.parseProperty(message, ManageConstants.SERVER_NAME_PROPERTY, String.class);
		String archiveDir = (String)JmsUtils.parseProperty(message, ManageConstants.ARCHIVE_DIR_PROPERTY, String.class);

		if (!hostName.equalsIgnoreCase(vcServerInfo.getHostName()) || !serverType.equalsIgnoreCase(vcServerInfo.getServerType()) || !serverName.equalsIgnoreCase(vcServerInfo.getServerName())) {
			return;
		}

		Object obj = ((ObjectMessage)message).getObject();
		if (!(obj instanceof LinkedList)) {
			return;
		}
		
		LinkedList changes = (LinkedList)obj;
		doChange(changes, archiveDir);
		toXML();
	} catch (MessagePropertyNotFoundException ex) {
		log.exception(ex);
	}	
}


/**
 * This method was created in VisualAge.
 * @return int
 */
protected abstract void on_iamalive(Message message)  throws JMSException ;


/**
 * This method was created in VisualAge.
 * @return int
 */
protected void on_openserverlog(Message message)  throws JMSException, IOException {
	try {
		String hostName = (String)JmsUtils.parseProperty(message, MessageConstants.HOSTNAME_PROPERTY, String.class);
		String serverType = (String)JmsUtils.parseProperty(message, ManageConstants.SERVER_TYPE_PROPERTY, String.class);
		String serverName = (String)JmsUtils.parseProperty(message, ManageConstants.SERVER_NAME_PROPERTY, String.class);
		
		if (hostName == null || !hostName.equalsIgnoreCase(vcServerInfo.getHostName())
			|| serverType == null || !serverType.equalsIgnoreCase(vcServerInfo.getServerType())
				|| serverName == null || !serverName.equalsIgnoreCase(vcServerInfo.getServerName())) {
			return;
		}

		openlog(vcServerInfo.getLogfile(), (Topic)message.getJMSReplyTo(), message.getJMSMessageID());
	} catch (MessagePropertyNotFoundException ex) {
		log.exception(ex);
	}
}


/**
 * This method was created in VisualAge.
 * @return int
 */
protected synchronized void on_performancestatus(Message message) throws JMSException {
	vcServerInfo.setPerformance(ManageUtils.getDaemonPerformance());
	Message reply = listenSession.createObjectMessage(vcServerInfo);
	reply.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE);
	log.print("sending reply [" + JmsUtils.toString(reply) + "]");
	listenSession.publishMessage(JmsUtils.getTopicDaemonControl(), reply);
}


/**
 * This method was created in VisualAge.
 * @return int
 */
protected abstract void on_stopbootstrap(Message message) throws JMSException ;


/**
 * onMessage method comment.
 */
public void onControlTopicMessage(Message message) {
	try {		
		log.print("onMessage [" + JmsUtils.toString(message) + "]");		
			
		String msgType = (String)JmsUtils.parseProperty(message, ManageConstants.MESSAGE_TYPE_PROPERTY, String.class);
		
		if (msgType.equals(ManageConstants.MESSAGE_TYPE_ISSERVERALIVE_VALUE)) {
			if (vcServerInfo.isBootstrap()) {
				((BootstrapDaemon)this).on_isserveralive(message);
			}			
			
		} else if (msgType.equals(ManageConstants.MESSAGE_TYPE_IAMALIVE_VALUE)) {
			on_iamalive(message);
			
		} else if (msgType.equals(ManageConstants.MESSAGE_TYPE_ASKPERFORMANCESTATUS_VALUE)) {
			on_performancestatus(message);
			
		} else if (msgType.equals(ManageConstants.MESSAGE_TYPE_CHANGECONFIG_VALUE)) {
			on_changeconfig(message);
			
		} else if (msgType.equals(ManageConstants.MESSAGE_TYPE_STOPBOOTSTRAP_VALUE)) {
			on_stopbootstrap(message);
			
		} else if (msgType.equals(ManageConstants.MESSAGE_TYPE_STARTBOOTSTRAP_VALUE)) {
			if (vcServerInfo.isServerManager()) {
				((ServerManagerDaemon)this).on_startbootstrap(message);
			}			

		} else if (msgType.equals(ManageConstants.MESSAGE_TYPE_STOPSERVICE_VALUE)) {
			if (vcServerInfo.isBootstrap()) {
				((BootstrapDaemon)this).on_stopservice(message);
			}			
			
		} else if (msgType.equals(ManageConstants.MESSAGE_TYPE_STARTSERVICE_VALUE)) {
			if (vcServerInfo.isBootstrap()) {
				((BootstrapDaemon)this).on_startservice(message);
			}
			
		} else if (msgType.equals(ManageConstants.MESSAGE_TYPE_OPENSERVICELOG_VALUE)) {
			if (vcServerInfo.isBootstrap() && message.getJMSReplyTo() != null) {
				((BootstrapDaemon)this).on_openservicelog(message);
			}			
			
		} else if (msgType.equals(ManageConstants.MESSAGE_TYPE_OPENSERVERLOG_VALUE)) {				
			if (message.getJMSReplyTo() != null) {
				on_openserverlog(message);
			}			
			
		}
		
	} catch (Exception ex) {
		log.exception(ex);
	}
}


/**
 * This method was created in VisualAge.
 * @return int
 */
protected void openlog(String logfilename, Topic replyTo, String corrID) throws JMSException, IOException {
	String out = null, status = null;

	final File logfile = new File(logfilename);	 //new File("d:\\vcell\\dev2\\computeService.log");
	Message reply = null;
	long filelength = 0;

	if (!logfile.exists()) {
		status = ManageConstants.STATUS_OPENLOG_NOTEXIST;
	} else {
		status = ManageConstants.STATUS_OPENLOG_EXIST;
		filelength = logfile.length();
	}

	reply = listenSession.createMessage();
	reply.setStringProperty(ManageConstants.STATUS_OPENLOG_PROPERTY, status);
	reply.setStringProperty(ManageConstants.FILE_NAME_PROPERTY, logfile.getName());
	reply.setLongProperty(ManageConstants.FILE_LENGTH_PROPERTY, filelength);	
	reply.setJMSCorrelationID(corrID);
	
	log.print("sending reply [" + JmsUtils.toString(reply) + "]");
	listenSession.publishMessage(replyTo, reply);

	if (status == ManageConstants.STATUS_OPENLOG_EXIST) {
		Thread t = new Thread() {
			public void run() {
				fileChannelSender.toSend(logfile);
			}
		};

		t.setName("SendingLogThread");
		t.start();
	}
}


/**
 * This method was created in VisualAge.
 * @return int
 */
protected abstract void readConfiguration()  throws FileNotFoundException, IOException, JDOMException ;


/**
 * Insert the method's description here.
 * Creation date: (12/24/2003 8:16:33 AM)
 */
private void reconnect() throws JMSException {
	jmsConnFactory = new JmsConnectionFactoryImpl();
	
	queueConn = jmsConnFactory.createQueueConnection();
	fileChannelSender = new JmsFileSender(queueConn, log);
	queueConn.startConnection();
	
	topicConn = jmsConnFactory.createTopicConnection();
	topicSession = topicConn.getAutoSession();
	
	listenSession = topicConn.getAutoSession();
	listenSession.setupListener(JmsUtils.getTopicDaemonControl(), getMessageFilter(), new ControlMessageCollector(this));
	topicConn.startConnection();	
}


/**
 * This method was created in VisualAge.
 * @return int
 */
protected abstract void start();


/**
 * Insert the method's description here.
 * Creation date: (8/21/2003 11:59:01 AM)
 * @param service cbit.vcell.messaging.admin.VCellService
 */
protected void startAllServices() {
	Iterator iter = vcServerInfo.getServiceList().iterator();
	while (iter.hasNext()) {		
		VCServiceInfo serviceInfo = (VCServiceInfo)iter.next();
		startAService(serviceInfo, true);
	}	
}	


/**
 * Insert the method's description here.
 * Creation date: (8/21/2003 11:59:01 AM)
 * @param service cbit.vcell.messaging.admin.VCellService
 */
protected void startAService(VCServiceInfo serviceInfo, boolean boot) {
	try {
		Process exe = vcServerInfo.startAService(serviceInfo, boot);
		if (exe != null) {
			processMap.put(serviceInfo, exe);
		}
	} catch (IOException ex) {
		log.exception(ex);
	}
}	


/**
 * Insert the method's description here.
 * Creation date: (8/21/2003 11:59:01 AM)
 * @param service cbit.vcell.messaging.admin.VCellService
 */
protected void stopAllService() {	
	Iterator iter = vcServerInfo.getServiceList().iterator();
	while (iter.hasNext()) {		
		VCServiceInfo serviceInfo = (VCServiceInfo)iter.next();
		stopAService(serviceInfo);
	}
}	


/**
 * Insert the method's description here.
 * Creation date: (8/21/2003 11:59:01 AM)
 * @param service cbit.vcell.messaging.admin.VCellService
 */
protected void stopAService(VCServiceInfo serviceInfo) {
	Process exe = (Process)processMap.get(serviceInfo);
	if ( exe != null ) {
		exe.destroy();
		processMap.remove(serviceInfo);
	}

	try {
		vcServerInfo.stopAService(serviceInfo);
	} catch (IOException ex) {
		log.exception(ex);
	}
}	


/**
 * Insert the method's description here.
 * Creation date: (8/21/2003 8:03:54 AM)
 */
protected void toXML() {
	try {
		File configFile = new File(configFileName);
		PrintWriter out = new PrintWriter(new FileOutputStream(configFile));
		XMLOutputter serializer = new XMLOutputter();
		serializer.setNewlines(true);
		serializer.setIndent("\t");
		serializer.output(vcServerInfo.toDocument(), out);
		out.flush();
		out.close();
	} catch (IOException e) {
		log.exception(e);
	}
}
}
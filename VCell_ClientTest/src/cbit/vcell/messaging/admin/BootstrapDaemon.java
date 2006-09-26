package cbit.vcell.messaging.admin;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import cbit.gui.PropertyLoader;
import cbit.util.MessageConstants;
import cbit.vcell.messaging.JmsUtils;
import cbit.vcell.messaging.ManageConstants;
import cbit.vcell.messaging.ManageUtils;
import cbit.vcell.messaging.MessagePropertyNotFoundException;
import cbit.vcell.messaging.VCServiceInfo;
import cbit.vcell.messaging.VCellTopicSession;

/**
 * Insert the type's description here.
 * Creation date: (8/7/2003 5:08:06 PM)
 * @author: Fei Gao
 */
public class BootstrapDaemon extends MessagingDaemon {
	private Map processMap = Collections.synchronizedMap(new HashMap());

/**
 * LocalVCellBootStrapMessaging constructor comment.
 */
public BootstrapDaemon(String bootStrapName) throws JMSException, IOException, java.net.UnknownHostException, FileNotFoundException, JDOMException  {
	super(ManageConstants.SERVER_TYPE_BOOTSTRAP, bootStrapName, PropertyLoader.getRequiredProperty(PropertyLoader.bootstrapConfig));	
}


/**
 * This method was created in VisualAge.
 * @return int
 */
protected java.lang.String getMessageFilter() {
	return ManageConstants.MESSAGE_TYPE_PROPERTY + " NOT IN (" 
		+ "'" + ManageConstants.MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE + "'"
		+ "," + "'" + ManageConstants.MESSAGE_TYPE_ISSERVICEALIVE_VALUE + "'"
		+ "," + "'" + ManageConstants.MESSAGE_TYPE_IAMALIVE_VALUE + "'"
		+ "," + "'" + ManageConstants.MESSAGE_TYPE_ISSERVERALIVE_VALUE + "'"
		+ ")"		
		+ " OR (" + ManageConstants.MESSAGE_TYPE_PROPERTY + "='" + ManageConstants.MESSAGE_TYPE_ISSERVERALIVE_VALUE + "'"
		+ " AND (" + ManageConstants.SERVER_NAME_PROPERTY + " IS NULL OR " + ManageConstants.SERVER_NAME_PROPERTY + "='" + vcServerInfo.getHostName() + "')" 
		+ ")"
		+ " OR (" + ManageConstants.MESSAGE_TYPE_PROPERTY + "='" + ManageConstants.MESSAGE_TYPE_IAMALIVE_VALUE + "'"
		+ " AND " + ManageConstants.HOSTNAME_PROPERTY + "='" + vcServerInfo.getHostName() + "'"
		+ ")";
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/2003 5:10:44 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	if (args.length < 1) {
		System.out.println("Usage: " + BootstrapDaemon.class.getName() + " bootstapname");
		System.exit(1);
	}
	
	System.out.println("Usage: " + BootstrapDaemon.class.getName() + " bootstapname");
	System.setSecurityManager(new SecurityManager());

	try {
		PropertyLoader.loadProperties();
		new BootstrapDaemon(args[0]).start();		
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		System.exit(1);
	}	
}


/**
 * This method was created in VisualAge.
 * @return int
 */
protected synchronized void on_iamalive(Message message) throws JMSException  {
	try {
		String hostName = (String)JmsUtils.parseProperty(message, ManageConstants.HOSTNAME_PROPERTY, String.class);
		String serviceType = (String)JmsUtils.parseProperty(message, ManageConstants.SERVICETYPE_PROPERTY, String.class);
		String serviceName = (String)JmsUtils.parseProperty(message, ManageConstants.SERVICENAME_PROPERTY, String.class);
		
		if (hostName != null && hostName.equalsIgnoreCase(vcServerInfo.getHostName()) && serviceType != null && serviceName != null) {
			VCServiceInfo serviceInfo = new VCServiceInfo(hostName, serviceType, serviceName);
			int index = vcServerInfo.indexOf(serviceInfo);
			log.print(serviceInfo + " -- alive");
			if (replyList.size() > 0) {				
				replyList.remove(serviceInfo);
				if (replyList.size() == 0) {
					notifyAll();	
				}
			}
		}
	} catch (MessagePropertyNotFoundException ex) {
		log.exception(ex);
	}
}


/**
 * This method was created in VisualAge.
 * @return int
 */
protected synchronized void on_isserveralive(Message message) throws JMSException {
	String hostName = null;	
		
	try {
		hostName = (String)JmsUtils.parseProperty(message, MessageConstants.HOSTNAME_PROPERTY, String.class);
	} catch (MessagePropertyNotFoundException ex) {
		// it's OK
	}
	
	if (hostName == null || hostName.equalsIgnoreCase(vcServerInfo.getHostName())) {
		Message reply = topicSession.createMessage();
		reply.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_IAMALIVE_VALUE);
		reply.setStringProperty(ManageConstants.HOSTNAME_PROPERTY, vcServerInfo.getHostName());
		reply.setStringProperty(ManageConstants.SERVER_TYPE_PROPERTY, ManageConstants.SERVER_TYPE_BOOTSTRAP);		
		reply.setStringProperty(ManageConstants.SERVER_NAME_PROPERTY, vcServerInfo.getServerName());		
		log.print("sending reply [" + JmsUtils.toString(reply) + "]");
		if (message.getJMSReplyTo() != null) {
			reply.setJMSCorrelationID(message.getJMSMessageID());
			listenSession.publishMessage((Topic)message.getJMSReplyTo(), reply);
		} else {
			listenSession.publishMessage(JmsUtils.getTopicDaemonControl(), reply);
		}
	}
}


/**
 * This method was created in VisualAge.
 * @return int
 */
protected void on_openservicelog(Message message) throws javax.jms.JMSException, IOException {
	try {
		String hostName = (String)JmsUtils.parseProperty(message, ManageConstants.HOSTNAME_PROPERTY, String.class);
		String serviceType = (String)JmsUtils.parseProperty(message, ManageConstants.SERVICETYPE_PROPERTY, String.class);
		String serviceName = (String)JmsUtils.parseProperty(message, ManageConstants.SERVICENAME_PROPERTY, String.class);
		
		if (hostName == null || serviceType == null || serviceName == null) {
			return;
		}

		VCServiceInfo serviceInfo0 = new VCServiceInfo(hostName, serviceType, serviceName);
		int index = vcServerInfo.indexOf(serviceInfo0);
		if (index < 0) {
			return;
		}

		VCServiceInfo serviceInfo = vcServerInfo.getServiceInfoAt(index);
		openlog(serviceInfo.getLogfile(), (Topic)message.getJMSReplyTo(), message.getJMSMessageID());
	} catch (MessagePropertyNotFoundException ex) {
		log.exception(ex);
	}
}


/**
 * This method was created in VisualAge.
 * @return int
 */
protected synchronized void on_startservice(Message message) throws JMSException {
	try {
		String hostName = (String)JmsUtils.parseProperty(message, ManageConstants.HOSTNAME_PROPERTY, String.class);
		String serviceType = (String)JmsUtils.parseProperty(message, ManageConstants.SERVICETYPE_PROPERTY, String.class);
		String serviceName = (String)JmsUtils.parseProperty(message, ManageConstants.SERVICENAME_PROPERTY, String.class);

		if (hostName == null || serviceName == null || serviceType == null) {
			return;
		}
		
		VCServiceInfo serviceInfo0 = new VCServiceInfo(hostName, serviceType, serviceName);
		startAService(serviceInfo0, false);
	} catch (MessagePropertyNotFoundException ex) {
		log.exception(ex);
	}
}


/**
 * This method was created in VisualAge.
 * @return int
 */
protected synchronized void on_stopbootstrap(Message message) throws JMSException {
	try {
		String hostName = (String)JmsUtils.parseProperty(message, MessageConstants.HOSTNAME_PROPERTY, String.class);
		String serverType = (String)JmsUtils.parseProperty(message, ManageConstants.SERVER_TYPE_PROPERTY, String.class);
		String serverName = (String)JmsUtils.parseProperty(message, ManageConstants.SERVER_NAME_PROPERTY, String.class); 		

		if (hostName == null || serverName == null || serverType == null 
			|| !hostName.equalsIgnoreCase(vcServerInfo.getHostName()) || !serverName.equalsIgnoreCase(vcServerInfo.getServerName()) 
			|| !serverType.equalsIgnoreCase(vcServerInfo.getServerType())) {
			return;
		}

		// need to destroy all the child processes.
		stopAllService();
	} catch (MessagePropertyNotFoundException ex) {
		log.exception(ex);
	}
}


/**
 * This method was created in VisualAge.
 * @return int
 */
protected synchronized void on_stopservice(Message message) throws JMSException {
	try {
		String hostName = (String)JmsUtils.parseProperty(message, ManageConstants.HOSTNAME_PROPERTY, String.class);
		String serviceType = (String)JmsUtils.parseProperty(message, ManageConstants.SERVICETYPE_PROPERTY, String.class);
		String serviceName = (String)JmsUtils.parseProperty(message, ManageConstants.SERVICENAME_PROPERTY, String.class);

		if (hostName == null || serviceName == null || serviceType == null) {
			return;
		}
		
		VCServiceInfo serviceInfo0 = new VCServiceInfo(hostName, serviceType, serviceName);
		int index = vcServerInfo.indexOf(serviceInfo0);
		if (index >= 0) {
			VCServiceInfo serviceInfo = vcServerInfo.getServiceInfoAt(index);
			if (serviceInfo.isAlive()) {
				stopAService(serviceInfo);
			} else {
				log.print("Service is not alive: [" + serviceInfo + "]");
			}
		}
	} catch (MessagePropertyNotFoundException ex) {
		log.exception(ex);
	}		
}


/**
 * onMessage method comment.
 */
private boolean ping(VCServiceInfo serviceInfo) throws JMSException {
	Message msg = topicSession.createMessage();
		
	msg.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_ISSERVICEALIVE_VALUE);
	msg.setStringProperty(ManageConstants.HOSTNAME_PROPERTY, serviceInfo.getHostName());
	msg.setStringProperty(ManageConstants.SERVICETYPE_PROPERTY, serviceInfo.getServiceType());		
	msg.setStringProperty(ManageConstants.SERVICENAME_PROPERTY, serviceInfo.getServiceName());

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
private synchronized boolean pingAll() throws JMSException, InterruptedException {
	replyList.clear();
	replyList.addAll(vcServerInfo.getServiceList());
	
	Message msg = topicSession.createMessage();
		
	msg.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_ISSERVICEALIVE_VALUE);
	msg.setStringProperty(ManageConstants.HOSTNAME_PROPERTY, vcServerInfo.getHostName());

	log.print("sending ping message [" + JmsUtils.toString(msg) + "]");
	
	topicSession.publishMessage(JmsUtils.getTopicDaemonControl(), msg);

	wait(ManageConstants.MINUTE);

	if (replyList.size() != 0) {
		return false;
	}	
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 3:34:59 PM)
 * @exception java.io.IOException The exception description.
 * @exception java.io.FileNotFoundException The exception description.
 */
protected void readConfiguration() throws FileNotFoundException, IOException, JDOMException {
	File configFile = new File(configFileName);

	SAXBuilder builder = new SAXBuilder();
	Document doc = builder.build(new FileInputStream(configFile));

	Element rootElement = doc.getRootElement();
	// log-file
	Element element = rootElement.getChild(ManageConstants.LOGFILE_TAG);
	if (element != null) {
		vcServerInfo.setLogfile(element.getText());
	}

	//archive-dir
	element = rootElement.getChild(ManageConstants.ARCHIVEDIR_TAG);
	if (element != null) {
		vcServerInfo.setArchiveDir(element.getText());
	}

	if (vcServerInfo.getLogfile() == null || vcServerInfo.getLogfile().trim().length() == 0 || vcServerInfo.getLogfile().equals("-")) {
		log = new cbit.util.StdoutSessionLog(vcServerInfo.getServerName());
	} else {
		ManageUtils.archiveByDateAndTime(vcServerInfo.getLogfile(), vcServerInfo.getArchiveDir());
		PrintStream ps = new java.io.PrintStream(new java.io.FileOutputStream(vcServerInfo.getLogfile(), true), true);
		System.setOut(ps);
		System.setErr(ps);
		log = new cbit.util.StdoutSessionLog(vcServerInfo.getServerName());
	}

	// service
	List serviceElementList = rootElement.getChildren(ManageConstants.SERVICE_TAG);
	log.print("There are " + serviceElementList.size() + " services:");

	Iterator iter = serviceElementList.iterator();
	while (iter.hasNext()) {
		element = (Element) iter.next();
		Element typeElement = element.getChild(ManageConstants.SERVICE_TYPE_TAG);			
		Element nameElement = element.getChild(ManageConstants.SERVICE_NAME_TAG);
		Element startcElement = element.getChild(ManageConstants.SERVICE_START_TAG);
		Element stopcElement = element.getChild(ManageConstants.SERVICE_STOP_TAG);
		Element logfileElement = element.getChild(ManageConstants.LOGFILE_TAG);
		Element autostartElement = element.getChild(ManageConstants.SERVICE_AUTOSTART_TAG);

		if (typeElement == null || nameElement == null || startcElement == null || logfileElement == null 
			|| stopcElement == null || autostartElement == null) {
			log.print("Missing element. Please check the configuration file [" + configFile.getAbsolutePath() + "]!");
			System.exit(1);
		}

		int pos = 0;
		for (pos = 0; pos < MessageConstants.AllServiceTypes.length; pos ++){
			if (typeElement.getText().equalsIgnoreCase(MessageConstants.AllServiceTypes[pos])) {
				break;
			}
		}
		if (pos == MessageConstants.AllServiceTypes.length) {
			log.print("Wrong service type [" + typeElement.getText() + "]. Please check the configuration file [" + configFile.getAbsolutePath() + "]!");
			System.exit(1);
		}
		
		VCServiceInfo serviceInfo = new VCServiceInfo(vcServerInfo.getHostName(), typeElement.getText(), nameElement.getText(), 
			startcElement.getText(), stopcElement.getText(), logfileElement.getText(), autostartElement.getText().equalsIgnoreCase("true"));
		log.print(serviceInfo + "");
		if (!vcServerInfo.addService(serviceInfo)) {
			log.print("You have two identical services. Please check the configuration file [" + configFile.getAbsolutePath() + "]!");
			System.exit(1);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 1:15:33 PM)
 */
public void sendStopService(VCellTopicSession session, VCServiceInfo serviceInfo) {
	try {		
		Message msg = session.createMessage();
			
		msg.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_STOPSERVICE_VALUE);
		msg.setStringProperty(ManageConstants.HOSTNAME_PROPERTY, serviceInfo.getHostName());
		msg.setStringProperty(ManageConstants.SERVICETYPE_PROPERTY, serviceInfo.getServiceType());
		msg.setStringProperty(ManageConstants.SERVICENAME_PROPERTY, serviceInfo.getServiceName());
		
		log.print("sending stop service message [" + JmsUtils.toString(msg) + "]");		
		session.publishMessage(JmsUtils.getTopicDaemonControl(), msg);
		
	} catch (Exception ex) {
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/2003 5:12:22 PM)
 */
public void start() {
	boolean alive = false;

	
	while (!stopped) {			
		try {		
			try {
				Thread.currentThread().sleep(ManageConstants.INTERVAL_PING_SERVICE);
			} catch (InterruptedException exc) {
			}

			alive = pingAll();
			if (!alive) {						
				Iterator iter = replyList.iterator();
				while (iter.hasNext()) {
					VCServiceInfo serviceInfo = (VCServiceInfo)iter.next();
					if (!serviceInfo.isAutoStart() && !serviceInfo.isAlive()) {
						continue;						
					}
					
					log.print("Service " + serviceInfo + " is not responding. ----- trying again");
					alive = ping(serviceInfo);

					if (alive) {						
						log.print("checking " + serviceInfo + " -- alive");
					} else {
						log.print("checking " + serviceInfo + " -- dead");
						
						sendStopService(topicSession, serviceInfo);
						stopAService(serviceInfo);

						// sleep 5 seconds before restarting that bootstrap
						try {
							Thread.currentThread().sleep(5 * ManageConstants.SECOND);
						} catch (InterruptedException exc) {
						}
						vcServerInfo.startAService(serviceInfo, false);
					}
				}
			}
		} catch (Throwable exc) {
			log.exception(exc);
		}
	}
}
}
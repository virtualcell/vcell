package cbit.vcell.messaging.admin;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import cbit.gui.PropertyLoader;
import cbit.vcell.messaging.JmsUtils;
import cbit.vcell.messaging.ManageConstants;
import cbit.vcell.messaging.ManageUtils;
import cbit.vcell.messaging.MessageConstants;
import cbit.vcell.messaging.MessagePropertyNotFoundException;
import cbit.vcell.messaging.VCServiceInfo;
import cbit.vcell.messaging.VCellTopicSession;

/**
 * Insert the type's description here.
 * Creation date: (8/7/2003 1:53:27 PM)
 * @author: Fei Gao
 */
public class ServerManagerDaemon extends MessagingDaemon {

/**
 * ServerManagerMessaging constructor comment.
 */
public ServerManagerDaemon(String serverName0) throws JDOMException, java.io.IOException, javax.jms.JMSException {
	super(ManageConstants.SERVER_TYPE_SERVERMANAGER, serverName0, PropertyLoader.getRequiredProperty(PropertyLoader.serverManageConfig));
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
		+ "," + "'" + ManageConstants.MESSAGE_TYPE_OPENSERVICELOG_VALUE + "'"
		+ ")"
		+ " OR (" + ManageConstants.MESSAGE_TYPE_PROPERTY + "='" + ManageConstants.MESSAGE_TYPE_IAMALIVE_VALUE + "'"
		+ " AND " + ManageConstants.SERVER_NAME_PROPERTY + " IS NOT NULL"
		+ ")";
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 4:47:40 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	if (args.length < 1) {
		System.out.println("Usage: " + ServerManagerDaemon.class.getName() + " ServerName");
		System.exit(1);
	}
	System.out.println("Usage: " + ServerManagerDaemon.class.getName() + " ServerName");
	
	System.setSecurityManager(new SecurityManager());
	try {
		PropertyLoader.loadProperties();
		new ServerManagerDaemon(args[0]).start();		
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		System.exit(1);
	}
}


/**
 * This method was created in VisualAge.
 * @return int
 */
protected synchronized void on_iamalive(Message message) throws JMSException {
	try {
		String hostName = (String)JmsUtils.parseProperty(message, MessageConstants.HOSTNAME_PROPERTY, String.class);
		String serverType = (String)JmsUtils.parseProperty(message, ManageConstants.SERVER_TYPE_PROPERTY, String.class);
		String serverName = (String)JmsUtils.parseProperty(message, ManageConstants.SERVER_NAME_PROPERTY, String.class);
		
		if (replyList.size() > 0 && hostName != null && serverName != null  && serverType != null && serverType.equals(ManageConstants.SERVER_TYPE_BOOTSTRAP)) {
			VCServiceInfo bootStrap = new VCServiceInfo(hostName, serverType, serverName);
			int index = vcServerInfo.indexOf(bootStrap);
			log.print("on_iamalive: " + bootStrap + " -- alive");
			replyList.remove(bootStrap);
			if (replyList.size() == 0) {
				notifyAll();
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
protected synchronized void on_startbootstrap(Message message) throws JMSException {
	try {
		String hostName = (String)JmsUtils.parseProperty(message, MessageConstants.HOSTNAME_PROPERTY, String.class);
		String serverType = (String)JmsUtils.parseProperty(message, ManageConstants.SERVER_TYPE_PROPERTY, String.class);
		String serverName = (String)JmsUtils.parseProperty(message, ManageConstants.SERVER_NAME_PROPERTY, String.class); 
		
		if (hostName == null || serverName == null || serverType == null) {			
			return;
		}

		VCServiceInfo serviceInfo = new VCServiceInfo(hostName, serverType, serverName);
		int index = vcServerInfo.indexOf(serviceInfo);
		if (index >= 0) {		
			startAService(vcServerInfo.getServiceInfoAt(index), false);
		}
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
		
		if (hostName != null && serverName != null && serverType != null) {
			if (serverType.equals(ManageConstants.SERVER_TYPE_RMISERVICE) || serverType.equals(ManageConstants.SERVER_TYPE_BOOTSTRAP)) {
				VCServiceInfo serviceInfo = new VCServiceInfo(hostName, serverType, serverName);
				int index = vcServerInfo.indexOf(serviceInfo);
				if (index >= 0) {
					stopAService(vcServerInfo.getServiceInfoAt(index));
				}
			}
		} 
	} catch (MessagePropertyNotFoundException ex) {
		log.exception(ex);
	}		
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 11:08:09 PM)
 * @param simulation cbit.vcell.solver.Simulation
 */
private boolean ping(VCServiceInfo bootStrap) throws JMSException  {
	Message msg = topicSession.createMessage();
		
	msg.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_ISSERVERALIVE_VALUE);
	msg.setStringProperty(ManageConstants.HOSTNAME_PROPERTY, bootStrap.getHostName());

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
 * Insert the method's description here.
 * Creation date: (10/24/2001 11:08:09 PM)
 * @param simulation cbit.vcell.solver.Simulation
 */
private synchronized boolean pingAll() throws JMSException, InterruptedException  {
	replyList.clear();
	replyList.addAll(vcServerInfo.getServiceList());

	Iterator iter = vcServerInfo.getServiceList().iterator();
	while (iter.hasNext()) {
		VCServiceInfo serviceInfo = (VCServiceInfo)iter.next();
		if (serviceInfo.getServiceType().equals(ManageConstants.SERVER_TYPE_RMISERVICE)) {
			replyList.remove(serviceInfo);
		}
	}		
	
	Message msg = topicSession.createMessage();
		
	msg.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_ISSERVERALIVE_VALUE);
	
	log.print("sending ping message [" + JmsUtils.toString(msg) + "]");	
	topicSession.publishMessage(JmsUtils.getTopicDaemonControl(), msg);

	wait(2 * ManageConstants.MINUTE);		

	if (replyList.size() == 0) {
		return true;
	}

	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 3:34:59 PM)
 * @exception java.io.IOException The exception description.
 * @exception java.io.FileNotFoundException The exception description.
 */
protected void readConfiguration() throws JDOMException, IOException, FileNotFoundException {		
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
		PrintStream ps = new java.io.PrintStream(new FileOutputStream(vcServerInfo.getLogfile(), true), true);
		System.setOut(ps);
		System.setErr(ps);
		log = new cbit.util.StdoutSessionLog(vcServerInfo.getServerName());		
	}

	//bootstraps
	List serviceElementList = rootElement.getChildren(ManageConstants.BOOTSTRAP_TAG);
	log.print("There are " + serviceElementList.size() + " bootstraps.");

	Iterator iter = serviceElementList.iterator();
	while (iter.hasNext()) {
		element = (Element) iter.next();
		Element hostnameElement = element.getChild(ManageConstants.HOST_NAME_TAG);	
		Element nameElement = element.getChild(ManageConstants.BOOTSTRAP_NAME_TAG);	
		Element startcElement = element.getChild(ManageConstants.SERVICE_START_TAG);
		Element stopcElement = element.getChild(ManageConstants.SERVICE_STOP_TAG);
		Element autostartElement = element.getChild(ManageConstants.SERVICE_AUTOSTART_TAG);
		
		if (hostnameElement == null || nameElement == null || startcElement == null || stopcElement == null || autostartElement == null) {
			log.print("Missing elements. Please check the configuration file [" + configFile.getAbsolutePath() + "]!");
			System.exit(1);
		}
		
		VCServiceInfo bootstrap = new VCServiceInfo(hostnameElement.getText(), ManageConstants.SERVER_TYPE_BOOTSTRAP, nameElement.getText(), 
			startcElement.getText(), stopcElement.getText(),  autostartElement.getText().equalsIgnoreCase("true"));
		if (!vcServerInfo.addService(bootstrap)) {
			log.print("You have two identical services. Please check the configuration file [" + configFile.getAbsolutePath() + "]!");
			System.exit(1);
		}	
	}

	//rmi
	serviceElementList = rootElement.getChildren(ManageConstants.RMISERVICE_TAG);
	log.print("There are " + serviceElementList.size() + " rmiservices.");

	iter = serviceElementList.iterator();
	while (iter.hasNext()) {
		element = (Element) iter.next();
		Element hostnameElement = element.getChild(ManageConstants.HOST_NAME_TAG);	
		Element nameElement = element.getChild(ManageConstants.RMISERVICE_NAME_TAG);	
		Element startcElement = element.getChild(ManageConstants.SERVICE_START_TAG);
		Element stopcElement = element.getChild(ManageConstants.SERVICE_STOP_TAG);
		Element autostartElement = element.getChild(ManageConstants.SERVICE_AUTOSTART_TAG);

		if (hostnameElement == null || nameElement == null || startcElement == null || stopcElement == null || autostartElement == null) {
			log.print("Missing elements. Please check the configuration file [" + configFile.getAbsolutePath() + "]!");
			System.exit(1);
		}

		VCServiceInfo bootstrap = new VCServiceInfo(hostnameElement.getText(), ManageConstants.SERVER_TYPE_RMISERVICE, nameElement.getText(), 
			startcElement.getText(), stopcElement.getText(),  autostartElement.getText().equalsIgnoreCase("true"));
		if (!vcServerInfo.addService(bootstrap)) {
			log.print("You have two identical services. Please check the configuration file [" + configFile.getAbsolutePath() + "]!");
			System.exit(1);
		}	
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2004 8:48:04 AM)
 */
public void sendStopBootstrap(VCellTopicSession session, VCServiceInfo bootstrap) {
	// first send out message to tell all the services of the bootstrap stop
	try {		
		Message msg = session.createMessage();
			
		msg.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_STOPBOOTSTRAP_VALUE);
		msg.setStringProperty(ManageConstants.HOSTNAME_PROPERTY, bootstrap.getHostName());
		msg.setStringProperty(ManageConstants.SERVER_TYPE_PROPERTY, bootstrap.getServiceType());
		msg.setStringProperty(ManageConstants.SERVER_NAME_PROPERTY, bootstrap.getServiceName());
		
		log.print("sending stop bootstrap message [" + JmsUtils.toString(msg) + "]");			
		session.publishMessage(JmsUtils.getTopicDaemonControl(), msg);
		
	} catch (JMSException ex) {
		log.exception(ex);
	}	
}


/**
 * When an object implementing interface <code>Runnable</code> is used 
 * to create a thread, starting the thread causes the object's 
 * <code>run</code> method to be called in that separately executing 
 * thread. 
 * <p>
 * The general contract of the method <code>run</code> is that it may 
 * take any action whatsoever.
 *
 * @see     java.lang.Thread#run()
 */
public void start() {
	boolean alive = false;

	while (!stopped) {			
		try {
			try {
				Thread.currentThread().sleep(ManageConstants.INTERVAL_PING_BOOTSTRAP);
			} catch (InterruptedException exc) {
			}

			alive = pingAll();
			if (!alive) {
				Iterator iter = replyList.iterator();
				while (iter.hasNext()) {
					VCServiceInfo serviceInfo = (VCServiceInfo)iter.next();
					if (serviceInfo.getServiceType().equals(ManageConstants.SERVER_TYPE_RMISERVICE) 
						|| !serviceInfo.isAutoStart() && !serviceInfo.isAlive()) {
						continue;
					}

					log.print("Host " + serviceInfo + " is not responding. ----- trying again");						
					alive = ping(serviceInfo);					
					if (alive) {
						log.print("checking " + serviceInfo + " -- alive");
					} else {
						log.print("checking " + serviceInfo + " -- dead");
						sendStopBootstrap(topicSession, serviceInfo);

						// stop the bootstrap itself
						stopAService(serviceInfo);
						
						// sleep 5 seconds before restarting that bootstrap
						try {
							Thread.currentThread().sleep(5 * ManageConstants.SECOND);
						} catch (InterruptedException exc) {
						}
						startAService(serviceInfo, false);
					}
				}
			}
		} catch (Throwable exc) {
			log.exception(exc);
		}
	}			
}
}
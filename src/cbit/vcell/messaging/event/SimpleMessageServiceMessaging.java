package cbit.vcell.messaging.event;
import cbit.vcell.messaging.*;
import cbit.vcell.server.User;
import cbit.vcell.server.SessionLog;
import cbit.rmi.event.*;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (11/14/2000 12:12:41 AM)
 * @author: 
 */
public class SimpleMessageServiceMessaging implements cbit.rmi.event.MessageService {
	private SimpleMessageCollector simpleMessageCollector = null;
	private SimpleMessageDispatcher simpleMessageDispatcher = null;
	private SimpleMessageHandler simpleMessageHandler = null;
	private JmsMessageCollector jmsMessageCollector = null;

/**
 * Insert the method's description here.
 * Creation date: (11/14/2000 12:19:31 AM)
 */
public SimpleMessageServiceMessaging(VCellTopicConnection topicConn, User user, SessionLog log) {
	initialize(topicConn, user, log);
	wireComponents();
}

/**
 * Insert the method's description here.
 * Creation date: (5/9/2002 5:07:34 PM)
 */
public void close() {
	simpleMessageHandler.close();
}


/**
 * Insert the method's description here.
 * Creation date: (4/19/2004 1:10:44 PM)
 * @return cbit.vcell.messaging.JmsMessageCollector
 */
public cbit.vcell.messaging.JmsMessageCollector getJmsMessageCollector() {
	return jmsMessageCollector;
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/2000 12:16:24 AM)
 * @return cbit.rmi.event.MessageCollector
 */
public MessageCollector getMessageCollector() {
	return simpleMessageCollector;
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/2000 12:16:24 AM)
 * @return cbit.rmi.event.MessageDispatcher
 */
public MessageDispatcher getMessageDispatcher() {
	return simpleMessageDispatcher;
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/2000 12:16:24 AM)
 * @return cbit.rmi.event.MessageHandler
 */
public MessageHandler getMessageHandler() {
	return simpleMessageHandler;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/2000 12:23:16 AM)
 */
private void initialize(VCellTopicConnection topicConn, User user, SessionLog log) {
	simpleMessageCollector = new SimpleMessageCollector();
	simpleMessageDispatcher = new SimpleMessageDispatcher();
	try {
		simpleMessageHandler = new SimpleMessageHandler(true);
		jmsMessageCollector = new JmsMessageCollector(topicConn, user, log);
	} catch (Exception exc) {
		handleException(exc);
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/2000 12:23:04 AM)
 */
private void wireComponents() {
	simpleMessageHandler.addMessageListener(simpleMessageDispatcher);
	jmsMessageCollector.addMessageListener(simpleMessageHandler);
}
}
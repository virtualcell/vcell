package cbit.rmi.event;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (11/14/2000 12:12:41 AM)
 * @author: 
 */
public class SimpleMessageService implements MessageService {
	private SimpleMessageCollector simpleMessageCollector = null;
	private SimpleMessageDispatcher simpleMessageDispatcher = null;
	private SimpleMessageHandler simpleMessageHandler = null;
/**
 * Insert the method's description here.
 * Creation date: (11/14/2000 12:19:31 AM)
 */
public SimpleMessageService() {
	initialize();
	wireComponents();
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/2002 5:07:34 PM)
 */
public void close() {
	simpleMessageHandler.close();
	try {
		java.rmi.server.UnicastRemoteObject.unexportObject(simpleMessageHandler, true);
	} catch (java.rmi.NoSuchObjectException exc) {
		exc.printStackTrace(System.out);
	}
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
private void initialize() {
	simpleMessageCollector = new SimpleMessageCollector();
	simpleMessageDispatcher = new SimpleMessageDispatcher();
	try {
		simpleMessageHandler = new SimpleMessageHandler(true);
	} catch (java.rmi.RemoteException exc) {
		handleException(exc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2000 12:23:04 AM)
 */
public void resetHandler() {
	close();
	simpleMessageHandler.removeMessageListener(simpleMessageDispatcher);
	simpleMessageCollector.removeMessageListener(simpleMessageHandler);
	try {
		simpleMessageHandler = new SimpleMessageHandler(true);
	} catch (java.rmi.RemoteException exc) {
		handleException(exc);
	}
	wireComponents();
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2000 12:23:04 AM)
 */
private void wireComponents() {
	simpleMessageHandler.addMessageListener(simpleMessageDispatcher);
	simpleMessageCollector.addMessageListener(simpleMessageHandler);
}
}

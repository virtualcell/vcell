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
public class SimpleMessageService implements MessageService, MessageListener {
	private MessageQueue messageQueue = new MessageQueue();
/**
 * Insert the method's description here.
 * Creation date: (11/14/2000 12:19:31 AM)
 */
public SimpleMessageService() {
}

public void messageEvent(MessageEvent event) {
	messageQueue.push(event);
	
}

public MessageEvent[] getMessageEvents() {
	return messageQueue.popAll();
}

public long timeSinceLastPoll() {
	return messageQueue.timeSinceLastPopAll();
}
}

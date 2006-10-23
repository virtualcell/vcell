package cbit.rmi.event;

import cbit.vcell.util.events.MessageData;
import cbit.vcell.util.events.MessageEvent;
import cbit.vcell.util.events.MessageSource;

/**
 * Insert the type's description here.
 * Creation date: (6/16/2006 3:55:11 PM)
 * @author: Jim Schaff
 */
public class VCellMessageEvent extends MessageEvent {
	public static final int VCELL_MESSAGEEVENT_TYPE_BROADCAST = 2001;
	int eventType;

/**
 * VCellMessageEvent constructor comment.
 * @param source java.lang.Object
 * @param messageSource cbit.rmi.event.MessageSource
 * @param messageData cbit.rmi.event.MessageData
 */
public VCellMessageEvent(Object source, String messageID, MessageData messageData,int messageType) {
	super(source, new MessageSource(source, messageID), messageData);
	eventType = messageType;
}


/**
 * Insert the method's description here.
 * Creation date: (6/16/2006 3:55:11 PM)
 * @return int
 */
public int getEventTypeID() {
	return eventType;
}


/**
 * Insert the method's description here.
 * Creation date: (6/16/2006 3:55:11 PM)
 * @return cbit.vcell.server.User
 */
public cbit.util.document.User getUser() {
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (6/16/2006 3:55:11 PM)
 * @return boolean
 */
public boolean isConsumable() {
	return false;
}
}
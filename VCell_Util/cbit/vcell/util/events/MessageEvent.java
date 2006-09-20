package cbit.vcell.util.events;
import cbit.util.User;
/**
 * Insert the type's description here.
 * Creation date: (11/12/2000 12:38:59 PM)
 * @author: IIM
 */
public abstract class MessageEvent extends java.util.EventObject {
	private MessageSource messageSource = null;
	private MessageData messageData = null;
	
	public static final int JOB_ACCEPTED = 998;
	public static final int JOB_STARTING = 999;
	public static final int JOB_DATA = 1000;
	public static final int JOB_PROGRESS = 1001;
	public static final int JOB_FAILURE = 1002;
	public static final int JOB_COMPLETED = 1003;
	public static final int JOB_WORKER_ALIVE = 1004;
	
	public final static int EXPORT_PROGRESS = 1004;
	public final static int EXPORT_FAILURE = 1005;
	public final static int EXPORT_COMPLETE = 1006;
	public final static int EXPORT_START = 1007;

	public final static int POLLING_STAT = 1008;
	public final static int SAVING_STAT = 1009;
	public final static int LOGON_STAT = 1010;

	public final static int DATA_PROGRESS = 1011;
	public final static int DATA_FAILURE = 1012;
	public final static int DATA_COMPLETE = 1013;
	public final static int DATA_START = 1014;


/**
 * Insert the method's description here.
 * Creation date: (11/12/2000 2:36:57 PM)
 * @param source java.lang.Object
 * @param messageSource cbit.rmi.event.MessageSource
 * @param messageData cbit.rmi.event.MessageData
 */
public MessageEvent(Object source, MessageSource messageSource, MessageData messageData) {
	super(source);
	if (messageSource == null) {
		throw new IllegalArgumentException("ERROR: messageSource cannot be null");
	}
	this.messageSource = messageSource;
	this.messageData = messageData;
}


/**
 * Insert the method's description here.
 * Creation date: (11/12/2000 3:05:50 PM)
 * @return int
 */
public abstract int getEventTypeID();


/**
 * Insert the method's description here.
 * Creation date: (11/12/2000 3:00:14 PM)
 * @return cbit.rmi.event.MessageData
 */
public MessageData getMessageData() {
	return messageData;
}


/**
 * Insert the method's description here.
 * Creation date: (11/12/2000 2:59:39 PM)
 * @return cbit.rmi.event.MessageSource
 */
public MessageSource getMessageSource() {
	return messageSource;
}


/**
 * Insert the method's description here.
 * Creation date: (7/2/2001 8:35:58 PM)
 * @return cbit.vcell.server.User
 */
public abstract User getUser();


/**
 * Insert the method's description here.
 * Creation date: (8/28/2002 2:38:25 PM)
 * @return boolean
 */
public abstract boolean isConsumable();

/**
 * Insert the method's description here.
 * Creation date: (1/10/2001 11:18:44 AM)
 * @param newSource java.lang.Object
 */
public void setSource(Object newSource) {
	source = newSource;
}
}
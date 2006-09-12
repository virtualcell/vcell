package cbit.vcell.messaging;

/**
 * Insert the type's description here.
 * Creation date: (5/21/2003 9:26:26 AM)
 * @author: Fei Gao
 */
public class ControlMessageCollector extends TopicListenerImpl {
	private ControlTopicListener controlTopicListener = null;
/**
 * ControlTopicListener constructor comment.
 * @param jFactory cbit.vcell.messaging.JmsFactory
 * @param tname java.lang.String
 * @param selector java.lang.String
 * @param slog cbit.vcell.server.SessionLog
 * @exception javax.naming.NamingException The exception description.
 * @exception javax.jms.JMSException The exception description.
 */
public ControlMessageCollector(ControlTopicListener listener) throws javax.jms.JMSException {
	super();	
	//super(MessageConstants.SERVER_CONTROL_TOPIC, slog);
	this.controlTopicListener = listener;
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2003 9:26:26 AM)
 * @param message javax.jms.Message
 */
public void onTopicMessage(javax.jms.Message message) throws javax.jms.JMSException {
	if (controlTopicListener != null) {
		controlTopicListener.onControlTopicMessage(message);
	}
}
}

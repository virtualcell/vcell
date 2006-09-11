package cbit.vcell.messaging;

import javax.jms.*;

/**
 * Insert the type's description here.
 * Creation date: (7/31/2003 8:40:09 AM)
 * @author: Fei Gao
 */
public interface VCellTopicConnection extends VCellJMSConnection {
	public VCellTopicSession getAutoSession() throws JMSException;
	public VCellTopicSession getClientAckSession() throws JMSException;
	public TopicConnection getConnection();
	public VCellTopicSession getTransactedSession() throws JMSException;
}

package cbit.vcell.messaging;
import javax.jms.*;

/**
 * Insert the type's description here.
 * Creation date: (10/8/2003 1:13:29 PM)
 * @author: Fei Gao
 */
public interface VCellJmsSession {
	public void close() throws JMSException;


	public void commit() throws JMSException;


	public Message createMessage() throws JMSException;


	public ObjectMessage createObjectMessage(java.io.Serializable obj) throws JMSException;


	public Message createTextMessage(String text) throws JMSException;


	public void rollback() throws JMSException;


	public void setupListener(String destName, String msgSelector, MessageListener listener) throws JMSException;
}
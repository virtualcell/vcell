package cbit.vcell.messaging;
import javax.jms.JMSException;

/**
 * Insert the type's description here.
 * Creation date: (2/24/2004 2:24:10 PM)
 * @author: Fei Gao
 */
public interface JmsConnectionFactory {
	public JmsXAConnection createXAConnection() throws JMSException;
	public JmsConnection createConnection() throws JMSException;
}
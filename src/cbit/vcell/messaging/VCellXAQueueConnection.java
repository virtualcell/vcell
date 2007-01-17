package cbit.vcell.messaging;

import javax.jms.*;

/**
 * Insert the type's description here.
 * Creation date: (9/2/2003 8:32:58 AM)
 * @author: Fei Gao
 */
public interface VCellXAQueueConnection extends VCellJMSConnection {
	public javax.jms.XAQueueConnection getXAConnection();
/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 1:13:52 PM)
 * @return cbit.vcell.messaging.VCellXAQueueSession
 */
public VCellXAQueueSession getXASession() throws JMSException;
}

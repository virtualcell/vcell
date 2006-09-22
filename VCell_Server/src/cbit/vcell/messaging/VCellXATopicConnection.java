package cbit.vcell.messaging;

import javax.jms.JMSException;

/**
 * Insert the type's description here.
 * Creation date: (9/2/2003 8:33:28 AM)
 * @author: Fei Gao
 */
public interface VCellXATopicConnection  extends VCellJMSConnection  {
	public javax.jms.XATopicConnection getXAConnection();
	public VCellXATopicSession getXASession() throws JMSException;
}

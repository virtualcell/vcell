package cbit.vcell.messaging;

import javax.jms.*;

/**
 * Insert the type's description here.
 * Creation date: (10/8/2003 1:13:29 PM)
 * @author: Fei Gao
 */
public interface VCellXAQueueSession extends VCellQueueSession {
	boolean joinTransaction(javax.transaction.TransactionManager tm) throws JMSException;
}

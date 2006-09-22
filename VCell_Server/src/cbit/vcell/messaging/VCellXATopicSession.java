package cbit.vcell.messaging;

import javax.jms.JMSException;
import javax.transaction.TransactionManager;

/**
 * Insert the type's description here.
 * Creation date: (10/8/2003 3:55:01 PM)
 * @author: Fei Gao
 */
public interface VCellXATopicSession extends VCellTopicSession {
	boolean joinTransaction(TransactionManager tm) throws JMSException;
}

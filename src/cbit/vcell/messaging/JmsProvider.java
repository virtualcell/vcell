package cbit.vcell.messaging;

import javax.jms.*;

/**
 * Insert the type's description here.
 * Creation date: (2/24/2004 10:31:52 AM)
 * @author: Fei Gao
 */
public interface JmsProvider {
	public final static String SONICMQ = "SonicMQ";
	//public final static String JBOSSMQ = "JBossMQ";

	public abstract Connection createConnection() throws JMSException;
	public abstract XAConnection createXAConnection() throws JMSException;
	public abstract int getErrorCode(JMSException ex);
	public abstract Connection getConnection(XAConnection xaConnection) throws JMSException;
	public boolean isBadConnection(JMSException ex);
	public abstract void setPingInterval(int pingInterval, Connection connection);
	public abstract void setPrefetchCount(MessageConsumer qr, int pc) throws JMSException;
	public abstract void setPrefetchThreshold(MessageConsumer qr, int pt) throws JMSException;
}

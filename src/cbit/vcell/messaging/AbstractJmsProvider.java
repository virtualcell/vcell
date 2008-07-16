package cbit.vcell.messaging;

import javax.jms.*;

/**
 * Insert the type's description here.
 * Creation date: (12/16/2001 9:49:37 AM)
 * @author: Jim Schaff
 */
public abstract class AbstractJmsProvider implements JmsProvider {
	protected String fieldUserid;
	protected String fieldPassword;	

	protected XAConnectionFactory xaConnectionFactory = null;
	protected ConnectionFactory connectionFactory = null;	
	
/**
 * JmsFactory constructor comment.
 */
protected AbstractJmsProvider(String userid, String password) {
	super();
	this.fieldUserid = userid;
	this.fieldPassword = password;
}
/**
 * Insert the method's description here.
 * Creation date: (6/6/2003 11:51:19 AM)
 * @return int
 */
public boolean isBadConnection(JMSException ex) {
	if (getErrorCode(ex) == JmsErrorCode.ERR_CONNECTION_DROPPED) {
		return true;
	}

	return false;
}
}

package cbit.vcell.messaging.server;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.User;
/**
 * Insert the type's description here.
 * Creation date: (7/11/2006 11:16:33 AM)
 * @author: Jim Schaff
 */
public class RpcBNGServerProxy extends AbstractRpcServerProxy implements cbit.vcell.server.bionetgen.BNGService {
/**
 * RpcBNGServerProxy constructor comment.
 * @param argUser cbit.vcell.server.User
 * @param clientMessaging0 cbit.vcell.messaging.JmsClientMessaging
 * @param queueName0 java.lang.String
 * @param argLog cbit.vcell.server.SessionLog
 * @exception javax.jms.JMSException The exception description.
 */
protected RpcBNGServerProxy(User argUser, cbit.vcell.messaging.JmsClientMessaging clientMessaging, SessionLog argLog) throws javax.jms.JMSException {
	super(argUser, clientMessaging, cbit.vcell.messaging.JmsUtils.getQueueBNGReq(), argLog);
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 11:21:19 AM)
 */
public cbit.vcell.server.bionetgen.BNGOutput executeBNG(cbit.vcell.server.bionetgen.BNGInput bngRulesInput) throws DataAccessException {
	return (cbit.vcell.server.bionetgen.BNGOutput)rpc("executeBNG",new Object[]{user, bngRulesInput});
}


/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 9:39:03 PM)
 * @return java.lang.Object
 * @param methodName java.lang.String
 * @param args java.lang.Object[]
 * @exception java.lang.Exception The exception description.
 */
private Object rpc(String methodName, Object[] args) throws DataAccessException {
	try {
		return rpc(org.vcell.util.MessageConstants.SERVICETYPE_BIONETGEN_VALUE, methodName, args, true);
	} catch (DataAccessException ex) {
		log.exception(ex);
		throw ex;
	} catch (RuntimeException e){
		log.exception(e);
		throw e;
	} catch (Exception e){
		log.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}
}
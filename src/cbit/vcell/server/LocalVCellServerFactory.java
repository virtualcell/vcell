package cbit.vcell.server;

import org.vcell.util.DataAccessException;
import org.vcell.util.PermissionException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.document.User;

import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.vcell.messaging.JmsConnectionFactory;
import cbit.vcell.modeldb.LocalAdminDbServer;
/**
 * This type was created in VisualAge.
 */
public class LocalVCellServerFactory implements VCellServerFactory {
	private VCellServer vcServer = null;
/**
 * LocalVCellConnectionFactory constructor comment.
 */
public LocalVCellServerFactory(String userid, String password, String hostName, ConnectionFactory conFactory, KeyFactory keyFactory, SessionLog sessionLog) throws java.sql.SQLException, java.io.FileNotFoundException, DataAccessException {
	this(userid, password, hostName, null, conFactory, keyFactory, sessionLog);
}
/**
 * LocalVCellConnectionFactory constructor comment.
 */
public LocalVCellServerFactory(String userid, String password, String hostName, JmsConnectionFactory jmsConnFactory, ConnectionFactory conFactory, KeyFactory keyFactory, SessionLog sessionLog) throws java.sql.SQLException, java.io.FileNotFoundException, DataAccessException {
	try {
		AdminDatabaseServer adminDbServer = new LocalAdminDbServer(conFactory,keyFactory,sessionLog);
		User adminUser = null;
		if (userid!=null && password!=null){			
			adminUser = adminDbServer.getUser(userid,password);
			if (adminUser==null){
				throw new PermissionException("failed to authenticate user userid "+userid);
			}
			if (!adminUser.getName().equals(PropertyLoader.ADMINISTRATOR_ACCOUNT)){
				throw new PermissionException("userid "+userid+" does not have sufficient privilage");
			}
		}
		vcServer = new LocalVCellServer(hostName, jmsConnFactory, adminDbServer);
	} catch (java.rmi.RemoteException e){
	}
}
/**
 * getVCellConnection method comment.
 */
public VCellServer getVCellServer() throws AuthenticationException, ConnectionException {
	return vcServer;
}
}

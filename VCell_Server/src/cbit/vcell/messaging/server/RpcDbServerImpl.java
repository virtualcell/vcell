package cbit.vcell.messaging.server;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;

import cbit.sql.ConnectionFactory;
import cbit.sql.DBCacheTable;
import cbit.sql.KeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.vcell.modeldb.DatabaseServerImpl;
/**
 * Insert the type's description here.
 * Creation date: (12/5/2001 12:00:10 PM)
 * @author: Jim Schaff
 *
 * stateless database service for any user (should be thread safe ... reentrant)
 *
 */
public class RpcDbServerImpl extends AbstractRpcServerImpl {
	private DatabaseServerImpl dbServerImpl = null;	

/**
 * DbServerImpl constructor comment.
 */
public RpcDbServerImpl(SessionLog sessionLog) throws DataAccessException {
	super(sessionLog);
	try {
		ConnectionFactory conFactory = new OraclePoolingConnectionFactory(log);
		KeyFactory	keyFactory = new OracleKeyFactory();
		DBCacheTable dbCacheTable = new DBCacheTable(1000*60*30);
		dbServerImpl = new DatabaseServerImpl(conFactory, keyFactory, dbCacheTable, sessionLog);	 
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException("Error creating DBTopLevel " + e.getMessage());
	}		
}


/**
 * Insert the method's description here.
 * Creation date: (3/16/2004 12:30:44 PM)
 * @return java.lang.Object
 */
public java.lang.Object getServerImpl() {
	return dbServerImpl;
}
}
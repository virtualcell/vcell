package cbit.sql;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.pool.OracleConnectionPoolDataSource;

import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;

/**
 * This type was created in VisualAge.y
 */
public final class OraclePoolingConnectionFactory implements ConnectionFactory  {

	private OracleConnectionPoolDataSource oracleConnectionPoolDataSource = null;
	private SessionLog log = null;

public OraclePoolingConnectionFactory(SessionLog sessionLog) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
	this(sessionLog, PropertyLoader.getRequiredProperty(PropertyLoader.dbDriverName), 
			PropertyLoader.getRequiredProperty(PropertyLoader.dbConnectURL), 
			PropertyLoader.getRequiredProperty(PropertyLoader.dbUserid), 
			PropertyLoader.getRequiredProperty(PropertyLoader.dbPassword));	
}

public OraclePoolingConnectionFactory(SessionLog sessionLog, String argDriverName, String argConnectURL, String argUserid, String argPassword) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
	this.log = sessionLog;
	if (oracleConnectionPoolDataSource==null){
		oracleConnectionPoolDataSource = new OracleConnectionPoolDataSource();
		oracleConnectionPoolDataSource.setURL(argConnectURL);
		oracleConnectionPoolDataSource.setUser(argUserid);
		oracleConnectionPoolDataSource.setPassword(argPassword);
	}
	
}

public synchronized void closeAll() throws java.sql.SQLException {
}
/**
 * This method was created in VisualAge.
 * @param con java.sql.Connection
 */
public void failed(Connection con, Object lock) throws SQLException {
	log.print("OraclePoolingConnectionFactory.failed("+con+")");
	release(con, lock);
}

public Connection getConnection(Object lock) throws SQLException {
	// possibly because OracleConnectionPoolDataSource can't generate connections at fast rate.
	// it worked before probably because our inefficient code had built-in delay.
	Connection conn = null;
	boolean bRetry = true;
	try {
		Thread.sleep(50);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	while (conn == null) {
		try {
			conn = oracleConnectionPoolDataSource.getConnection();
		} catch (SQLException x) {
			if (bRetry && x.getMessage().indexOf("ORA-12519") != -1){
				// sleep for a while
				System.out.println("failed");
				bRetry = false;
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				throw x;
			}
		}
	}
	
	return conn;
}
/**
 * This method was created in VisualAge.
 * @param con java.sql.Connection
 */
public void release(Connection con, Object lock) throws SQLException {
	con.close();
}
}

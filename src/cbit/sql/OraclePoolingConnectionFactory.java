package cbit.sql;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.pool.OracleDataSource;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;

/**
 * This type was created in VisualAge.y
 */
public final class OraclePoolingConnectionFactory implements ConnectionFactory  {

	private OracleDataSource oracleDataSource = null;
	private SessionLog log = null;

public OraclePoolingConnectionFactory(SessionLog sessionLog) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
	this(sessionLog, PropertyLoader.getRequiredProperty(PropertyLoader.dbDriverName), 
			PropertyLoader.getRequiredProperty(PropertyLoader.dbConnectURL), 
			PropertyLoader.getRequiredProperty(PropertyLoader.dbUserid), 
			PropertyLoader.getRequiredProperty(PropertyLoader.dbPassword));	
}

public OraclePoolingConnectionFactory(SessionLog sessionLog, String argDriverName, String argConnectURL, String argUserid, String argPassword) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
	this.log = sessionLog;
	if (oracleDataSource==null){
		oracleDataSource = new OracleDataSource();
		// set DataSource properties
		oracleDataSource.setURL(argConnectURL);
		oracleDataSource.setUser(argUserid);
		oracleDataSource.setPassword(argPassword);
		oracleDataSource.setConnectionCachingEnabled(true);		
		// set cache properties    
		java.util.Properties prop = new java.util.Properties();    
		prop.setProperty("MinLimit", "1");    
		prop.setProperty("MaxLimit", "20");
//		prop.setProperty("InitialLimit", "3"); // create 3 connections at startup
		prop.setProperty("InactivityTimeout", "1800");    //  seconds
		prop.setProperty("AbandonedConnectionTimeout", "900");  //  seconds
		oracleDataSource.setConnectionCacheProperties (prop);    
		oracleDataSource.setConnectionCacheName("ImplicitCache01"); // this cache's name
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
	Connection conn = oracleDataSource.getConnection();
	if (conn == null) {
		throw new SQLException("max connection limit has reached. no connections are available.");
	}
	return conn;
}
/**
 * This method was created in VisualAge.
 * @param con java.sql.Connection
 */
public void release(Connection con, Object lock) throws SQLException {
	if (con != null) {
		con.close();
	}
}

public static void main(String[] args) {
	try {
		PropertyLoader.loadProperties();
	
		OraclePoolingConnectionFactory fac = new OraclePoolingConnectionFactory(new StdoutSessionLog("aa"));
		Object lock = new Object();
		Connection conn1 = null, conn2 = null;
		int i = 0;
		while (i<2000) {
			i ++;
			try {
				conn1 = fac.getConnection(lock);
				System.out.println(conn1);
				conn2 = fac.getConnection(lock);
				System.out.println(conn2);
			} finally {
				fac.release(conn1, lock);		
				fac.release(conn2, lock);
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
}
}

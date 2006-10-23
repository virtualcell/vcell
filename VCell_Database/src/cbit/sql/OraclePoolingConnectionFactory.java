package cbit.sql;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.sql.Connection;
import java.sql.SQLException;

import cbit.util.PropertyLoader;
import cbit.util.SessionLog;
/**
 * This type was created in VisualAge.y
 */
public final class OraclePoolingConnectionFactory implements ConnectionFactory  {

	private static pool.JDCConnectionDriver jdcConnectionDriver = null;
	private SessionLog log = null;
	
/**
 * This method was created in VisualAge.
 * @param maxConnections int
 */
public OraclePoolingConnectionFactory(SessionLog sessionLog) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {

	String driverName =	PropertyLoader.getRequiredProperty(PropertyLoader.dbDriverName);
	String connectURL =	PropertyLoader.getRequiredProperty(PropertyLoader.dbConnectURL);
	String password =		PropertyLoader.getRequiredProperty(PropertyLoader.dbPassword);
	String userid =		PropertyLoader.getRequiredProperty(PropertyLoader.dbUserid);
	this.log = sessionLog;
	if (jdcConnectionDriver==null){
		jdcConnectionDriver = new pool.JDCConnectionDriver(driverName,connectURL,userid,password);
	}
	
}
/**
 * This method was created in VisualAge.
 * @param maxConnections int
 */
public OraclePoolingConnectionFactory(SessionLog sessionLog, String argDriverName,String argConnectURL,String argUserid,String argPassword) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {

	String driverName =	argDriverName;
	String connectURL =	argConnectURL;
	String password =		argPassword;
	String userid =		argUserid;
	this.log = sessionLog;
	if (jdcConnectionDriver==null){
		jdcConnectionDriver = new pool.JDCConnectionDriver(driverName,connectURL,userid,password);
	}
	
}
/**
 * This method was created in VisualAge.
 */
public synchronized void closeAll() throws java.sql.SQLException {
}
/**
 * This method was created in VisualAge.
 * @param con java.sql.Connection
 */
public void failed(Connection con, Object lock) throws SQLException {
	System.out.println("OraclePoolingConnectionFactory.failed("+con+")");
	((pool.JDCConnection)con).failed("OraclePoolingConnectionFactory.failed()");
	release(con, lock);
}
/**
 * This method was created in VisualAge.
 * @return Connection
 */
public Connection getConnection(Object lock) throws SQLException {	
	return java.sql.DriverManager.getConnection("jdbc:jdc:jdcpool");
}
/**
 * This method was created in VisualAge.
 * @param con java.sql.Connection
 */
public void release(Connection con, Object lock) throws SQLException {
	con.close();
}
}

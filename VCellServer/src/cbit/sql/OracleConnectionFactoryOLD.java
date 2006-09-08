package cbit.sql;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.sql.Connection;
import java.sql.DriverManager;
import cbit.vcell.server.PropertyLoader;
/**
 * This type was created in VisualAge.
 */
public final class OracleConnectionFactoryOLD implements ConnectionFactory {

	private static String defaultDriverName = "oracle.jdbc.driver.OracleDriver";					// default
	private static String defaultConnectURL = "jdbc:oracle:thin:@nrcamdb.uchc.edu:1521:orc0";		// default
	private static String defaultUserid = "nrcamdbdev";												// default
	private static String defaultPassword = "bogus";												// default

	private static String driverName = null;
	private static String connectURL = null;
	private static String userid = null;
	private static String password = null;
	private Connection connection = null;

/*
	private static String driverName = "oracle.jdbc.driver.OracleDriver";
	private static String connectURL = "jdbc:oracle:thin:@nrcamdb.uchc.edu:1521:orcl";
	private static String userid = "nrcamdb";
	private static String password = "bogus";
	private Connection connection = null;
	*/
/**
 * This method was created in VisualAge.
 */
public OracleConnectionFactoryOLD() {
	this.driverName =	PropertyLoader.getProperty(PropertyLoader.dbDriverName,	defaultDriverName);
	this.connectURL =	PropertyLoader.getProperty(PropertyLoader.dbConnectURL,	defaultConnectURL);
	this.password =		PropertyLoader.getProperty(PropertyLoader.dbPassword,	defaultPassword);
	this.userid =		PropertyLoader.getProperty(PropertyLoader.dbUserid,		defaultUserid);
}
/**
 * This method was created in VisualAge.
 */
public OracleConnectionFactoryOLD(String argDriverName,String argConnectURL,String argUserid,String argPassword){
	
	this.driverName =	argDriverName;
	this.connectURL =	argConnectURL;
	this.password =		argPassword;
	this.userid =		argUserid;
}
/**
 * This method was created in VisualAge.
 */
public void closeAll() throws java.sql.SQLException {
	if (connection!=null){
		if (!connection.isClosed()){
			connection.close();
		}
		connection = null;
	}
}
/**
 * This method was created in VisualAge.
 * @param con java.sql.Connection
 */
public void failed(Connection con, Object lock) {
	try {
		if (connection==con){
			connection = null;
		}
		con.close();
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
/**
 * This method was created in VisualAge.
 * @return java.sql.Connection
 */
public java.sql.Connection getConnection(Object lock) throws java.sql.SQLException {
	if (connection==null){
		connection = getNewConnection();
	}else if (connection.isClosed()){
		connection = getNewConnection();
	}
	return connection;
}
/**
 * This method was created in VisualAge.
 * @return java.sql.Connection
 */
private Connection getNewConnection() throws java.sql.SQLException {
	try {
		Class.forName(driverName);
	} catch (ClassNotFoundException e) {
		throw new RuntimeException("JDBC Driver Class " + driverName + " not found");
	}
	String url = connectURL;
	Connection c = DriverManager.getConnection(url, userid, password);
	c.setAutoCommit(false);
	return c;
}
/**
 * This method was created in VisualAge.
 * @param con java.sql.Connection
 */
public void release(Connection con, Object lock) {
	//
	// for multiple threads, release mutex on connection
	//
}
}

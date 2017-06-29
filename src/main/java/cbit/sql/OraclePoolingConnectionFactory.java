/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.vcell.util.ConfigurationException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.UserInfo;

import cbit.vcell.modeldb.UserTable;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.StdoutSessionLog;
import oracle.ucp.UniversalConnectionPoolAdapter;
import oracle.ucp.UniversalConnectionPoolException;
import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.JDBCConnectionPoolStatistics;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

/**
 * This type was created in VisualAge.y
 */
public final class OraclePoolingConnectionFactory implements ConnectionFactory  {

	private UniversalConnectionPoolManager connectionPoolManaager = null;
	private String connectionCacheName = null;
	private PoolDataSource poolDataSource = null;
	private SessionLog log = null;
	private final ScheduledExecutorService executorService;
	private static final Logger lg = Logger.getLogger(OraclePoolingConnectionFactory.class);

public OraclePoolingConnectionFactory(SessionLog sessionLog) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException, ConfigurationException, UniversalConnectionPoolException {
	this(sessionLog, PropertyLoader.getRequiredProperty(PropertyLoader.dbDriverName),
			PropertyLoader.getRequiredProperty(PropertyLoader.dbConnectURL),
			PropertyLoader.getRequiredProperty(PropertyLoader.dbUserid),
			PropertyLoader.getRequiredProperty(PropertyLoader.dbPassword));
}

public OraclePoolingConnectionFactory(SessionLog sessionLog, String argDriverName, String argConnectURL, String argUserid, String argPassword) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException, UniversalConnectionPoolException {
	this.log = sessionLog;
	connectionCacheName = "UCP_ManagedPool_" + System.nanoTime();

	connectionPoolManaager = UniversalConnectionPoolManagerImpl.getUniversalConnectionPoolManager();
	connectionPoolManaager.setJmxEnabled(true);
	poolDataSource = PoolDataSourceFactory.getPoolDataSource();
	poolDataSource.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
	poolDataSource.setConnectionPoolName(connectionCacheName);
	// set DataSource properties
	poolDataSource.setURL(argConnectURL);
	poolDataSource.setUser(argUserid);
	poolDataSource.setPassword(argPassword);
	connectionPoolManaager.createConnectionPool((UniversalConnectionPoolAdapter)poolDataSource);
	// set cache properties
	poolDataSource.setMinPoolSize(2);
	poolDataSource.setMaxPoolSize(10);
	poolDataSource.setInitialPoolSize(2);
//	java.util.Properties prop = new java.util.Properties();
//	prop.setProperty("MinLimit", "1");
//	prop.setProperty("MaxLimit", "20");
////	prop.setProperty("InitialLimit", "3"); // create 3 connections at startup
////	prop.setProperty("InactivityTimeout", "300");    //  seconds
////	prop.setProperty("TimeToLiveTimeout", "300");    //  seconds
////	prop.setProperty("AbandonedConnectionTimeout", "300");  //  seconds
////	prop.setProperty("ValidateConnection", "true");
//	oracleDataSource.setConnectionCacheProperties (prop);

	// when vcell runs in local model, every time reconnnect, it will create a new
	// OraclePoolingConnectionFactory which causes same cache error. So add current time
	// to cache name.

//	oracleDataSource.setConnectionCacheName(connectionCacheName); // this cache's name
//	connectionPoolManaager.startConnectionPool(connectionCacheName);

	executorService = new ScheduledThreadPoolExecutor(1);
	executorService.scheduleAtFixedRate(( ) -> refreshConnections( ), 2,2,TimeUnit.MINUTES);
	executorService.scheduleAtFixedRate(( ) -> statCheck( ), 1,2,TimeUnit.MINUTES);
}

/**
 * scheduled method to check connection status. Log status if either the log4j level is already
 * set to INFO or higher, or the number of connections is relatively low
 */
private void statCheck( ) {
	boolean infoOn = lg.isInfoEnabled();
	if (infoOn) {
		logStat( );
		return;
	}

}

/**
 * info log pool statistics; {@link #lg} {@link Logger#isInfoEnabled()} must be true for this to do anything
 */
private void logStat( ) {
	if (lg.isInfoEnabled()) {
		JDBCConnectionPoolStatistics stats = poolDataSource.getStatistics();
		lg.info(stats.toString());
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
	// Get singleton ConnectionCacheManager instance
	try {
		connectionPoolManaager.refreshConnectionPool(connectionCacheName);
	} catch (UniversalConnectionPoolException e) {
		log.exception(e);
	}
}

private synchronized void refreshConnections() {
	try {
		connectionPoolManaager.refreshConnectionPool(connectionCacheName);
	} catch (UniversalConnectionPoolException e) {
		log.exception(e);
	}
}

public synchronized Connection getConnection(Object lock) throws SQLException {
	Connection conn = null;
	try {
		conn = poolDataSource.getConnection();
	} catch (SQLException ex) {
		// might be invalid or stale connection
		log.print("first time #getConnection( ) fail " + ex.getMessage() + ", state " + ex.getSQLState() + ", attempting refresh");
		// refresh cache
		try {
			connectionPoolManaager.refreshConnectionPool(connectionCacheName);
		} catch (UniversalConnectionPoolException e) {
			log.exception(e);
		}
		// get connection again.
		try {
			conn = poolDataSource.getConnection();
		} catch (SQLException e) {
			log.print("refresh failed");
			log.exception(e);
		}
	}
	if (conn == null) {
		throw new SQLException("Cannot get a connection to the database. This could be caused by\n" +
				"1. Max connection limit has reached. No connections are available.\n" +
				"2. there is a problem with database server.\n" +
				"3. there is a problem with network.\n");
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

private static boolean validate(Connection conn) {
	try {
		@SuppressWarnings("unused")
		DatabaseMetaData dmd = conn.getMetaData();
	} catch (Exception e) {
		System.out.println("testing metadata...failed");
		e.printStackTrace(System.out);
		return false;
	}
	try {
		conn.getAutoCommit();
	} catch (Exception e) {
		System.out.println("testing autocommit...failed");
		e.printStackTrace(System.out);
		return false;
	}

	try {
		String sql = "SELECT * from " + UserTable.table.getTableName() +
				" WHERE " + UserTable.table.id + "=0";

		Statement stmt = conn.createStatement();
		try {
			ResultSet rset = stmt.executeQuery(sql);
			if (rset.next()){
				@SuppressWarnings("unused")
				UserInfo userInfo = UserTable.table.getUserInfo(rset);
			}
		} finally {
			stmt.close();
		}

	} catch (Exception e) {
		System.out.println("query user table...failed");
		e.printStackTrace(System.out);
		return false;
	}
	return true;
}

public static void main(String[] args) {
	try {
		PropertyLoader.loadProperties();

		StdoutSessionLog sessionLog = new StdoutSessionLog("aa");
		OraclePoolingConnectionFactory fac = new OraclePoolingConnectionFactory(sessionLog);
		Object lock = new Object();
		Connection conn1 = null, conn2 = null;
		conn1 = fac.getConnection(lock);
		sessionLog.print("got conn1 " + validate(conn1));
		Thread.sleep(60*60*1000);
		conn2 = fac.getConnection(lock);
		sessionLog.print("got conn2 " + validate(conn2));
//		int i = 0;
//		while (i<2000) {
//			i ++;
//			try {
//				conn1 = fac.getConnection(lock);
//				System.out.println(conn1);
//				conn2 = fac.getConnection(lock);
//				System.out.println(conn2);
//			} finally {
//				fac.release(conn1, lock);
//				fac.release(conn2, lock);
//			}
//		}
	} catch (Exception e) {
		e.printStackTrace();
	}
}
}

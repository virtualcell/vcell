/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.db.postgres;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.ds.PGConnectionPoolDataSource;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.util.document.UserInfo;

import cbit.vcell.modeldb.UserTable;

/**
 * This type was created in VisualAge.y
 */
public final class PostgresConnectionFactory implements ConnectionFactory  {
	private static final Logger lg = LogManager.getLogger(PostgresConnectionFactory.class);

	private PGConnectionPoolDataSource poolDataSource = null;


PostgresConnectionFactory(String argDriverName, String argConnectURL, String argUserid, String argPassword)  {
	poolDataSource = new PGConnectionPoolDataSource();
	poolDataSource.setUrl(argConnectURL);
	poolDataSource.setUser(argUserid);
	poolDataSource.setPassword(argPassword);
	poolDataSource.setLogUnclosedConnections(true);
	poolDataSource.setDefaultAutoCommit(false);
	
}

public synchronized void close() throws java.sql.SQLException {
	// TODO: how to deallocate resources
}

public void failed(Connection con, Object lock) throws SQLException {
	if (lg.isTraceEnabled()) lg.trace("OraclePoolingConnectionFactory.failed("+con+")");
	release(con, lock);
}

public synchronized Connection getConnection(Object lock) throws SQLException {
	Connection conn = null;
	try {
		conn = poolDataSource.getConnection();
		conn.setAutoCommit(false);
	} catch (SQLException ex) {
		// might be invalid or stale connection
		if (lg.isTraceEnabled()) lg.trace("first time #getConnection( ) fail " + ex.getMessage() + ", state " + ex.getSQLState() + ", attempting refresh");
		// get connection again.
		try {
			conn = poolDataSource.getConnection();
		} catch (SQLException e) {
			if (lg.isTraceEnabled()) lg.trace("refresh failed");
			lg.error(e.getMessage(),e);
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


@Override
public KeyFactory getKeyFactory() {
	return new PostgresKeyFactory();
}

@Override
public DatabaseSyntax getDatabaseSyntax() {
	return DatabaseSyntax.POSTGRES;
}

}

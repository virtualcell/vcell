/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;

import org.vcell.db.ConnectionFactory;
/**
 * This type was created in VisualAge.
 */
public final class MysqlConnectionFactory implements ConnectionFactory {

	private static String driverName = "twz1.jdbc.mysql.jdbcMysqlDriver";
	private static String connectURL = "jdbc:z1MySQL:";
	private static String userid = null;
	private static String password = "bogus";
	private Connection connection = null;
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
	Connection c = null;
	if (userid==null){
		c = DriverManager.getConnection(url);
	}else{
		c = DriverManager.getConnection(url, userid, password);
	}
//	c.setAutoCommit(false);
	return c;
}
/**
 * This method was created in VisualAge.
 * @param con java.sql.Connection
 */
public void release(Connection con,Object lock) {
	//
	// for multiple threads, release mutex on connection
	//
}
}

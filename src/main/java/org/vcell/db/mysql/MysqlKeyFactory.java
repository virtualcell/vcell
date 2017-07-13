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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.vcell.db.KeyFactory;
import org.vcell.util.document.KeyValue;
/**
 * This type was created in VisualAge.
 */
public class MysqlKeyFactory implements KeyFactory {
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getCreateSQL() {
	String sql = "CREATE TABLE MYSEQUENCE(id integer AUTO_INCREMENT PRIMARY KEY, value integer)";
	return sql;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getDestroySQL() {
	String sql = "DROP TABLE MYSEQUENCE";
	return sql;
}
/**
 * getNewKey method comment.
 */
public KeyValue getNewKey(Connection con) throws SQLException {
	return new KeyValue(getUniqueBigDecimal(con));
}
/**
 * getNewKey method comment.
 */
public synchronized java.math.BigDecimal getUniqueBigDecimal(Connection con) throws SQLException {
	//
	// insert dummy record
	//
	String sql = " INSERT INTO MYSEQUENCE (value) VALUES (2) ";
//System.out.println(sql);
	Statement stmt = null;
	try {
		stmt = con.createStatement();
		stmt.executeUpdate(sql);
	} finally {
		if (stmt!=null){
			stmt.close(); // Release resources include resultset
		}
	}
	//
	// query 'last inserted id'
	//
	sql = " SELECT LAST_INSERT_ID()";
//System.out.println(sql);
	stmt = con.createStatement();
	java.math.BigDecimal bigD = null;
	try {
		ResultSet rset = stmt.executeQuery(sql);
		if (rset.next()) {
			bigD = rset.getBigDecimal(1);
		} else {
			throw new RuntimeException("Could not get new Key value");
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	return bigD;
}
}

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

import java.sql.*;

import org.vcell.util.document.KeyValue;
/**
 * This type was created in VisualAge.
 */
public class OracleKeyFactory implements KeyFactory {
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getCreateSQL() {
	String sql = "CREATE SEQUENCE "+Table.SEQ;
	return sql;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getDestroySQL() {
	String sql = "DROP SEQUENCE "+Table.SEQ;
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
public java.math.BigDecimal getUniqueBigDecimal(Connection con) throws SQLException {
	String sql;
	sql = " SELECT " + Table.NewSEQ + " FROM DUAL";
	Statement stmt = con.createStatement();
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

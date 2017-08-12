/*
 * Copyright (C) 1999-2017 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.db.postgres;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.vcell.db.KeyFactory;
import org.vcell.util.document.KeyValue;

import cbit.sql.Table;

/**
 * oracle implementation of KeyFactory with appropriate syntax for oracle sql.
 */
public class PostgresKeyFactory implements KeyFactory {

PostgresKeyFactory() {
}

public String getCreateSQL() {
	String sql = "CREATE SEQUENCE "+Table.SEQ;
	return sql;
}

public String getDestroySQL() {
	String sql = "DROP SEQUENCE IF EXISTS "+Table.SEQ+" CASCADE";
	return sql;
}

public KeyValue getNewKey(Connection con) throws SQLException {
	return new KeyValue(getUniqueBigDecimal(con));
}

public java.math.BigDecimal getUniqueBigDecimal(Connection con) throws SQLException {
	String sql;
	sql = " SELECT " + nextSEQ();
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

@Override
public String nextSEQ() {
	return "nextval('"+Table.SEQ+"')";
}

@Override
public String currSEQ() {
	return "currval('"+Table.SEQ+"')";
}

}

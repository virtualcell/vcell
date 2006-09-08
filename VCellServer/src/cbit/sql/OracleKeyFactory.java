package cbit.sql;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.sql.*;
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

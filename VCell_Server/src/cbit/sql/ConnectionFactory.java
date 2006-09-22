package cbit.sql;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.sql.Connection;
import java.sql.SQLException;
/**
 * This type was created in VisualAge.
 */
public interface ConnectionFactory {
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
void closeAll() throws java.sql.SQLException;
/**
 * This method was created in VisualAge.
 * @param con Connection
 */
void failed(Connection con, Object lock) throws SQLException;
/**
 * This method was created in VisualAge.
 * @return java.sql.Connection
 * @exception java.sql.SQLException The exception description.
 * @exception java.lang.ClassNotFoundException The exception description.
 */
java.sql.Connection getConnection(Object lock) throws java.sql.SQLException;
/**
 * This method was created in VisualAge.
 * @param con Connection
 */
void release(java.sql.Connection con, Object lock) throws SQLException;
}

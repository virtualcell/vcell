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

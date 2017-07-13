/*
 * Copyright (C) 1999-2017 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Service interface for implementations of the database connection service
 */
public interface ConnectionFactory {

	void closeAll() throws java.sql.SQLException;
	
	void failed(Connection con, Object lock) throws SQLException;
	
	java.sql.Connection getConnection(Object lock) throws java.sql.SQLException;
	
	void release(java.sql.Connection con, Object lock) throws SQLException;
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.transaction;

/**
 * Insert the type's description here.
 * Creation date: (7/31/2003 8:35:21 AM)
 * @author: Fei Gao
 */
public interface JtaDbConnection {
	public void close() throws java.sql.SQLException;
	public void closeOnFailure() throws java.sql.SQLException;
	public java.sql.Connection getConnection() throws java.sql.SQLException;
	public boolean joinTransaction(javax.transaction.TransactionManager tm) throws java.sql.SQLException;
}

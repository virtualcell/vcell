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
 * Creation date: (7/29/2003 1:06:24 PM)
 * @author: Fei Gao
 */
public interface NonXAResource {
	public void commit() throws javax.transaction.SystemException;
	public void rollback() throws javax.transaction.SystemException;
}

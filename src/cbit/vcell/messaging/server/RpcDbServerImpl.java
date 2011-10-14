/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.messaging.server;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import org.vcell.util.DataAccessException;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.sql.OracleKeyFactory;
/**
 * Insert the type's description here.
 * Creation date: (12/5/2001 12:00:10 PM)
 * @author: Jim Schaff
 *
 * stateless database service for any user (should be thread safe ... reentrant)
 *
 */
public class RpcDbServerImpl extends AbstractRpcServerImpl {
	private DatabaseServerImpl dbServerImpl = null;	

/**
 * DbServerImpl constructor comment.
 */
public RpcDbServerImpl(org.vcell.util.SessionLog sessionLog) throws DataAccessException {
	super(sessionLog);
	try {
		ConnectionFactory conFactory = new OraclePoolingConnectionFactory(log);
		KeyFactory	keyFactory = new OracleKeyFactory();
		dbServerImpl = new DatabaseServerImpl(conFactory, keyFactory, sessionLog);	 
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException("Error creating DBTopLevel " + e.getMessage());
	}		
}


/**
 * Insert the method's description here.
 * Creation date: (3/16/2004 12:30:44 PM)
 * @return java.lang.Object
 */
public java.lang.Object getServerImpl() {
	return dbServerImpl;
}
}

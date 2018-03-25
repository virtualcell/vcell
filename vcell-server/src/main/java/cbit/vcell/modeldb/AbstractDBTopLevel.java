/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modeldb;

import org.apache.log4j.Logger;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseSyntax;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
/**
 * Insert the type's description here.
 * Creation date: (8/28/2003 4:57:38 PM)
 * @author: Frank Morgan
 */
public abstract class AbstractDBTopLevel {
	
	public static Logger lg = Logger.getLogger(AdminDBTopLevel.class);

	protected final ConnectionFactory conFactory;
/**
 * Insert the method's description here.
 * Creation date: (9/4/2003 3:02:41 PM)
 * @param confactory cbit.sql.ConnectionFactory
 * @param argSessionLog cbit.vcell.server.SessionLog
 */
AbstractDBTopLevel(ConnectionFactory argConFactory) {
	this.conFactory = argConFactory;
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2003 9:24:49 AM)
 */
protected void handle_DataAccessException_SQLException(Throwable t) throws org.vcell.util.DataAccessException, java.sql.SQLException {
	if (t == null){
		return;
	}
	t.printStackTrace(System.out);
	if (t instanceof org.vcell.util.DataAccessException){
		throw (org.vcell.util.DataAccessException)t;
	}else if (t instanceof java.sql.SQLException){
		throw (java.sql.SQLException)t;
	}else if (t instanceof Error){
		throw (Error)t;
	}else if (t instanceof RuntimeException){
		throw new DataAccessException("Unknown Database Access Error : " + t.getMessage());
		// throw (RuntimeException)t;
	}else{
		throw new RuntimeException("Unexpected \""+t.getClass().getName()+"\": "+t.getMessage());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2003 9:24:49 AM)
 */
protected void handle_SQLException(Throwable t) throws java.sql.SQLException {
	if (t == null){
		return;
	}
	t.printStackTrace(System.out);
	if (t instanceof java.sql.SQLException){
		throw (java.sql.SQLException)t;
	}else if (t instanceof Error){
		throw (Error)t;
	}else if (t instanceof RuntimeException){
		throw (RuntimeException)t;
	}else{
		throw new RuntimeException("Unexpected \""+t.getClass().getName()+"\": "+t.getMessage());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 5:08:33 PM)
 * @return boolean
 * @param con java.sql.Connection
 */
protected boolean isBadConnection(java.sql.Connection con) {

	return isBadConnection(con, lg, conFactory.getDatabaseSyntax());
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 5:08:33 PM)
 * @return boolean
 * @param con java.sql.Connection
 */
public static boolean isBadConnection(java.sql.Connection con, Logger lg, DatabaseSyntax dbSyntax) {

	//if(throwable instanceof cbit.vcell.server.DataAccessException){
	//	return false;
	//}else if(throwable instanceof cbit.vcell.server.PermissionException){
	//	return false;
	//}
	String sql = null;
	switch (dbSyntax){
	case ORACLE:{
		sql = "SELECT null FROM DUAL";
		break;
	}
	case POSTGRES:{
		sql = "SELECT null";
		break;
	}
	default:{
		throw new RuntimeException("unexpected DatabaseSyntax "+dbSyntax);
	}
	}
	java.sql.Statement stmt = null;
	try{
		stmt = con.createStatement();
		
		java.sql.ResultSet rset = stmt.executeQuery(sql);
		if(rset.next()){
			return false;
		}else{
			lg.error("AbstractDBTopLevel.isBadConnection query returned no results");
			return true;
		}
	}catch(Throwable e){
		lg.error("AbstractDBTopLevel.isBadConnection query failed - ",e);
		return true;
	}finally{
		try{
			if(stmt != null){
				stmt.close();
			}
		}catch(Throwable e){
			lg.error("AbstractDBTopLevel.isBadConnection query cleanup failed - ", e);
			return true;
		}
	}
}
}

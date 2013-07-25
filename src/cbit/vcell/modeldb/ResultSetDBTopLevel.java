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
import cbit.vcell.solver.*;
import java.sql.Connection;
import java.sql.SQLException;
import cbit.sql.*;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
/**
 * This type was created in VisualAge.
 */
public class ResultSetDBTopLevel extends AbstractDBTopLevel{
	private SimulationDbDriver simDB = null;
	
	private static final int SQL_ERROR_CODE_BADCONNECTION = 1010; //??????????????????????????????????????

/**
 * DBTopLevel constructor comment.
 */
public ResultSetDBTopLevel(ConnectionFactory aConFactory,SessionLog newLog) throws SQLException{
	super(aConFactory,newLog);
	GeomDbDriver geomDBDriver = new GeomDbDriver(newLog);
	MathDescriptionDbDriver mathDBDriver = new MathDescriptionDbDriver(geomDBDriver,newLog);
	this.simDB = new SimulationDbDriver(mathDBDriver,newLog);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
void deleteResultSetInfoSQL(User user, KeyValue simKey, boolean bEnableRetry) throws SQLException, DataAccessException, PermissionException, ObjectNotFoundException {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		simDB.deleteResultSetInfoSQL(con, user, simKey);
		con.commit();
		return;
	} catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			deleteResultSetInfoSQL(user,simKey,false);
		}else{
			handle_DataAccessException_SQLException(e);
		}
	}finally{
		conFactory.release(con,lock);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
public SolverResultSetInfo getResultSetInfo(User user, KeyValue simKey, int jobIndex, boolean bEnableRetry) throws SQLException, DataAccessException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return simDB.getResultSetInfo(con, user, simKey, jobIndex);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getResultSetInfo(user, simKey, jobIndex, false);
		}else{
			handle_SQLException(e);
			return null; // never gets here
		}
	} finally {
		conFactory.release(con,lock);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
SolverResultSetInfo[] getResultSetInfos(User user, KeyValue simKey, boolean bEnableRetry) throws SQLException, DataAccessException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return simDB.getResultSetInfos(con, user, simKey);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getResultSetInfos(user, simKey, false);
		}else{
			handle_SQLException(e);
			return null; // never gets here
		}
	} finally {
		conFactory.release(con,lock);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
SolverResultSetInfo[] getResultSetInfos(User user, boolean bAll, boolean bEnableRetry) throws SQLException, DataAccessException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return simDB.getResultSetInfos(con, log, user, bAll, null);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getResultSetInfos(user, bAll, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here
		}
	} finally {
		conFactory.release(con,lock);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.UserInfo
 * @param newUserInfo cbit.sql.UserInfo
 */
void insertResultSetInfo(User user, SolverResultSetInfo rsetInfo, boolean bEnableRetry) throws SQLException, DataAccessException, PermissionException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		simDB.insertResultSetInfoSQL(con,user,rsetInfo);
		con.commit();
		return;
	} catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			insertResultSetInfo(user,rsetInfo,false);
		}else{
			handle_DataAccessException_SQLException(e);
		}
	}finally{
		conFactory.release(con,lock);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.UserInfo
 * @param newUserInfo cbit.sql.UserInfo
 */
public void updateResultSetInfo(User user, SolverResultSetInfo rsetInfo, boolean bEnableRetry) throws SQLException, DataAccessException, PermissionException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		try {
			simDB.updateResultSetInfoSQL(con,user,rsetInfo);
			con.commit();
		}catch (ObjectNotFoundException e){
			simDB.insertResultSetInfoSQL(con,user,rsetInfo);
			con.commit();
		}
		return;
	} catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			updateResultSetInfo(user,rsetInfo,false);
		}else{
			handle_DataAccessException_SQLException(e);
		}
	}finally{
		conFactory.release(con,lock);
	}
}
}

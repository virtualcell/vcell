package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.solver.*;
import java.sql.Connection;
import java.sql.SQLException;
import cbit.sql.*;
import cbit.vcell.server.*;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.image.*;
import java.util.Vector;

import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.math.MathDescription;
/**
 * This type was created in VisualAge.
 */
public class ResultSetDBTopLevel extends AbstractDBTopLevel{
	private SimulationDbDriver simDB = null;
	
	private static final int SQL_ERROR_CODE_BADCONNECTION = 1010; //??????????????????????????????????????

/**
 * DBTopLevel constructor comment.
 */
ResultSetDBTopLevel(ConnectionFactory aConFactory,SessionLog newLog) throws SQLException{
	super(aConFactory,newLog);
	GeomDbDriver geomDBDriver = new GeomDbDriver(newLog);
	MathDescriptionDbDriver mathDBDriver = new MathDescriptionDbDriver(geomDBDriver,newLog);
	this.simDB = new SimulationDbDriver(mathDBDriver,newLog);
}


void deleteResultSetExport(User user,KeyValue eleKey,boolean bEnableRetry) throws SQLException, DataAccessException {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		simDB.deleteResultSetExport(con, user,eleKey);
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
			deleteResultSetExport(user,eleKey,false);
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
cbit.vcell.export.server.ExportLog getResultSetExport(User user, KeyValue simKey, boolean bEnableRetry) throws SQLException, DataAccessException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return simDB.getResultSetExport(con, user, simKey);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getResultSetExport(user, simKey, false);
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
cbit.vcell.export.server.ExportLog[] getResultSetExports(User user, boolean bAll, boolean bEnableRetry) throws SQLException, DataAccessException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return simDB.getResultSetExports(con,user, bAll);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getResultSetExports(user, bAll, false);
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
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
SolverResultSetInfo getResultSetInfo(User user, KeyValue simKey, int jobIndex, boolean bEnableRetry) throws SQLException, DataAccessException {
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
 * Insert the method's description here.
 * Creation date: (10/7/2003 12:57:20 PM)
 */
public void insertResultSetExport(User user,KeyValue simulationRef,String exportFormat,String exportURL,boolean bEnableRetry) throws SQLException, DataAccessException{
	
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		simDB.insertResultSetExport(con,user,simulationRef,exportFormat,exportURL);
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
			insertResultSetExport(user,simulationRef,exportFormat,exportURL,false);
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
void insertResultSetInfo(User user, KeyValue simKey, SolverResultSetInfo rsetInfo, boolean bEnableRetry) throws SQLException, DataAccessException, PermissionException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		simDB.insertResultSetInfoSQL(con,user,simKey,rsetInfo);
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
			insertResultSetInfo(user,simKey,rsetInfo,false);
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
void updateResultSetInfo(User user, KeyValue simKey, SolverResultSetInfo rsetInfo, boolean bEnableRetry) throws SQLException, DataAccessException, PermissionException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		try {
			simDB.updateResultSetInfoSQL(con,user,simKey,rsetInfo);
			con.commit();
		}catch (ObjectNotFoundException e){
			simDB.insertResultSetInfoSQL(con,user,simKey,rsetInfo);
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
			updateResultSetInfo(user,simKey,rsetInfo,false);
		}else{
			handle_DataAccessException_SQLException(e);
		}
	}finally{
		conFactory.release(con,lock);
	}
}
}
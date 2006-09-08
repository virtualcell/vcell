package cbit.vcell.modeldb;

import cbit.sql.ConnectionFactory;
import cbit.vcell.server.SessionLog;
/**
 * Insert the type's description here.
 * Creation date: (8/28/2003 4:57:38 PM)
 * @author: Frank Morgan
 */
public abstract class AbstractDBTopLevel {
	
	protected ConnectionFactory conFactory = null;
	protected SessionLog log = null;
/**
 * Insert the method's description here.
 * Creation date: (9/4/2003 3:02:41 PM)
 * @param confactory cbit.sql.ConnectionFactory
 * @param argSessionLog cbit.vcell.server.SessionLog
 */
AbstractDBTopLevel(cbit.sql.ConnectionFactory argConFactory, cbit.vcell.server.SessionLog argSessionLog) {
	this.conFactory = argConFactory;
	this.log = argSessionLog;
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2003 9:24:49 AM)
 */
protected void handle_DataAccessException_SQLException(Throwable t) throws cbit.vcell.server.DataAccessException, java.sql.SQLException {
	if (t == null){
		return;
	}
	t.printStackTrace(System.out);
	if (t instanceof cbit.vcell.server.DataAccessException){
		throw (cbit.vcell.server.DataAccessException)t;
	}else if (t instanceof java.sql.SQLException){
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

	return isBadConnection(con, log);
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 5:08:33 PM)
 * @return boolean
 * @param con java.sql.Connection
 */
public static boolean isBadConnection(java.sql.Connection con, SessionLog log) {

	//if(throwable instanceof cbit.vcell.server.DataAccessException){
	//	return false;
	//}else if(throwable instanceof cbit.vcell.server.PermissionException){
	//	return false;
	//}
	String sql = "SELECT null FROM DUAL";
	java.sql.Statement stmt = null;
	try{
		stmt = con.createStatement();
		java.sql.ResultSet rset = stmt.executeQuery(sql);
		if(rset.next()){
			return false;
		}else{
			log.alert("AbstractDBTopLevel.isBadConnection query returned no results");
			return true;
		}
	}catch(Throwable e){
		log.alert("AbstractDBTopLevel.isBadConnection query failed - "+e.getMessage());
		return true;
	}finally{
		try{
			if(stmt != null){
				stmt.close();
			}
		}catch(Throwable e){
			log.alert("AbstractDBTopLevel.isBadConnection query cleanup failed - "+e.getMessage());
			return true;
		}
	}
}
}

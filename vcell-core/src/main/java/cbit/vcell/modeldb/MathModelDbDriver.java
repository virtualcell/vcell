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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Vector;

import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.DependencyException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelChildSummary;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;
import org.vcell.util.document.Versionable;
import org.vcell.util.document.VersionableType;

import cbit.sql.Field;
import cbit.sql.InsertHashtable;
import cbit.sql.QueryHashtable;
import cbit.sql.RecordChangedException;
import cbit.sql.Table;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.modeldb.DatabasePolicySQL.OuterJoin;
/**
 * This type was created in VisualAge.
 */
public class MathModelDbDriver extends DbDriver {
	public static final MathModelTable mathModelTable = MathModelTable.table;
	public static final UserTable userTable = UserTable.table;
	public static final MathModelSimulationLinkTable mathModelSimLinkTable = MathModelSimulationLinkTable.table;
	public static final SimulationTable simTable = SimulationTable.table;

/**
 * LocalDBManager constructor comment.
 */
public MathModelDbDriver(DatabaseSyntax dbSyntax,KeyFactory keyFactory, SessionLog sessionLog) {
	super(dbSyntax,keyFactory,sessionLog);
}


/**
 * only the owner can delete a Model
 */
private void deleteMathModelMetaDataSQL(Connection con, User user, KeyValue mathModelKey) 
				throws SQLException,DependencyException,DataAccessException,PermissionException,ObjectNotFoundException {


	//
	// get key values of simulations belonging to this version of MathModel
	// these will be used later for possible deletion
	//
	//KeyValue simKeys[] = getDeletableSimulationEntriesFromMathModel(con,user,mathModelKey);
	//KeyValue mathKey = getMathKeyFromMathModel(con,mathModelKey);
	

	//
	// delete MathModel
	//
	// automatically deletes MathModel-Simulation link-table entries 		 (ON DELETE CASCADE)
	//
	String sql;
	sql = DatabasePolicySQL.enforceOwnershipDelete(user,mathModelTable,mathModelTable.id.getQualifiedColName()+" = "+mathModelKey);
	updateCleanSQL(con, sql);

	//
	// try to clean up the child simulations that are no longer pointed to by any other MathModel.
	//
	// delete simulations (which deletes ResultSetMetaData via ON DELETE CASCADE)
	//
	//for (int i=0;i<simKeys.length;i++){
		//try {
			//this.simDB.deleteVersionable(con,user,VersionableType.Simulation,simKeys[i]);
			//log.print("MathModelDbDriver.delete("+mathModelKey+") deletion of Simulation("+simKeys[i]+") succeeded");
		//}catch (cbit.vcell.server.PermissionException e){
			//log.print("MathModelDbDriver.delete("+mathModelKey+") deletion of Simulation("+simKeys[i]+") failed: "+e.getMessage());
		//}catch (cbit.vcell.server.DependencyException e){
			//log.print("MathModelDbDriver.delete("+mathModelKey+") deletion of Simulation("+simKeys[i]+") failed: "+e.getMessage());
		//}
	//}
	////
	//// try to remove MathDescription used by this MathModel
	////
	//try {
		//this.mathDescDB.deleteVersionable(con,user,VersionableType.MathDescription,mathKey);
		//log.print("MathModelDbDriver.delete("+mathModelKey+") deletion of MathDescription("+mathKey+") succeeded");
	//}catch (cbit.vcell.server.PermissionException e){
		//log.print("MathModelDbDriver.delete("+mathModelKey+") deletion of MathDescription("+mathKey+") failed: "+e.getMessage());
	//}catch (cbit.vcell.server.DependencyException e){
		//log.print("MathModelDbDriver.delete("+mathModelKey+") deletion of MathDescription("+mathKey+") failed: "+e.getMessage());
	//}

}


/**
 * This method was created in VisualAge.
 * @param user cbit.vcell.server.User
 * @param vType int
 * @param versionKey cbit.sql.KeyValue
 */
public void deleteVersionable(Connection con, User user, VersionableType vType, KeyValue vKey) 
				throws DependencyException, ObjectNotFoundException,
						SQLException,DataAccessException,PermissionException {

	deleteVersionableInit(con, user, vType, vKey);
	if (vType.equals(VersionableType.MathModelMetaData)){
		deleteMathModelMetaDataSQL(con, user, vKey);
	}else{
		throw new IllegalArgumentException("vType "+vType+" not supported by "+this.getClass());
	}
}


/**
 * getModels method comment.
 */
KeyValue[] getDeletableSimulationEntriesFromMathModel(Connection con,User user,KeyValue mathModelKey) throws SQLException, DataAccessException {
//	log.print("MathModelDbDriver.getSimulationEntriesFromMathModel(mathModelKey=" + mathModelKey + ")");
	String sql;
	
	sql = 	" SELECT " + mathModelSimLinkTable.simRef.getQualifiedColName() +
			" FROM " + mathModelSimLinkTable.getTableName() + "," + simTable.getTableName() +
			" WHERE " + mathModelSimLinkTable.mathModelRef.getQualifiedColName() + " = " + mathModelKey +
			" AND " + mathModelSimLinkTable.simRef.getQualifiedColName() + " = " + simTable.id.getQualifiedColName() +
			" AND " + simTable.versionFlag.getQualifiedColName() + " <> " + VersionFlag.Archived.getIntValue() +
			" AND " + simTable.ownerRef.getQualifiedColName() + " = " + user.getID();
			
	Statement stmt = con.createStatement();
	java.util.Vector<KeyValue> keyList = new Vector<KeyValue>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		
		//showMetaData(rset);

		//
		// get all keys
		//
		while (rset.next()) {
			KeyValue key = mathModelSimLinkTable.getSimulationKey(rset);
			keyList.addElement(key);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	//
	// put results in an array
	//
	KeyValue keyArray[] = new KeyValue[keyList.size()];
	keyList.copyInto(keyArray);
	return keyArray;
}

/**
 * getModel method comment.
 */
private MathModelMetaData getMathModelMetaData(Connection con,User user, KeyValue mathModelKey) 
					throws SQLException, DataAccessException, ObjectNotFoundException {
	if (user == null || mathModelKey == null) {
		throw new IllegalArgumentException("Improper parameters for getMathModelMetaData");
	}
	log.print("MathModelDbDriver.getMathModelMetaData(user=" + user + ", id=" + mathModelKey + ")");

	//
	// to construct a MathModelMetaData as an immutable object, lets collect all keys first
	// (even before authentication).  If the user doesn't authenticate, then throw away the
	// child keys (from link tables).
	//

	//
	// get Simulation Keys for mathModelKey
	//
	KeyValue simKeys[] = getSimulationEntriesFromMathModel(con, mathModelKey);

	//
	// get MathModelMetaData object for mathModelKey
	//
	String sql;
	Field[] f = {new cbit.sql.StarField(mathModelTable),userTable.userid};
	Table[] t = {mathModelTable,userTable};
	String condition =	mathModelTable.id.getQualifiedColName() + " = " + mathModelKey + 
					" AND " + 
						userTable.id.getQualifiedColName() + " = " + mathModelTable.ownerRef.getQualifiedColName();
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,(OuterJoin)null,condition,null,dbSyntax,true);

	Statement stmt = con.createStatement();
	MathModelMetaData mathModelMetaData = null;
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);

		if (rset.next()) {
			mathModelMetaData = mathModelTable.getMathModelMetaData(rset,con,log,simKeys,dbSyntax);
		} else {
			throw new ObjectNotFoundException("MathModel id=" + mathModelKey + " not found for user '" + user + "'");
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	return mathModelMetaData;
}


/**
 * getModel method comment.
 */
MathModelMetaData[] getMathModelMetaDatas(Connection con,User user, boolean bAll) 
					throws SQLException, DataAccessException, ObjectNotFoundException {
	if (user == null) {
		throw new IllegalArgumentException("Improper parameters for getMathModelMetaDatas");
	}
	log.print("MathModelDbDriver.getMathModelMetaData(user=" + user + ", bAll=" + bAll + ")");

	//
	// to construct a MathModelMetaData as an immutable object, lets collect all keys first
	// (even before authentication).  If the user doesn't authenticate, then throw away the
	// child keys (from link tables).
	//

	//
	// get BioModelMetaData object for bioModelKey
	//
	String sql;
	Field[] f = {new cbit.sql.StarField(mathModelTable),userTable.userid};
	Table[] t = {mathModelTable,userTable};
	String condition =	userTable.id.getQualifiedColName() + " = " + mathModelTable.ownerRef.getQualifiedColName();
	if (!bAll) {
		condition += " AND " + userTable.id.getQualifiedColName() + " = " + user.getID();
	}
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,(OuterJoin)null,condition,null,dbSyntax,true);
	//
	StringBuffer newSQL = new StringBuffer(sql);
	newSQL.insert(7,Table.SQL_GLOBAL_HINT);
	sql = newSQL.toString();
	//
	Statement stmt = con.createStatement();
	Vector<MathModelMetaData> mathModelMetaDataList = new Vector<MathModelMetaData>();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);

		while (rset.next()) {
			MathModelMetaData mathModelMetaData = mathModelTable.getMathModelMetaData(rset,log,this,con,dbSyntax);
			mathModelMetaDataList.addElement(mathModelMetaData);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	MathModelMetaData mathModelMetaDataArray[] = new MathModelMetaData[mathModelMetaDataList.size()];
	mathModelMetaDataList.copyInto(mathModelMetaDataArray);
	return mathModelMetaDataArray;
}


/**
 * getModels method comment.
 */
KeyValue[] getSimulationEntriesFromMathModel(Connection con,KeyValue mathModelKey) throws SQLException, DataAccessException {
//	log.print("MathModelDbDriver.getSimulationEntriesFromMathModel(mathModelKey=" + mathModelKey + ")");
	String sql;
	
	sql = 	" SELECT " + mathModelSimLinkTable.simRef +
			" FROM " + mathModelSimLinkTable.getTableName() + 
			" WHERE " + mathModelSimLinkTable.mathModelRef + " = " + mathModelKey +
			" ORDER BY " + mathModelSimLinkTable.id;
			
	Statement stmt = con.createStatement();
	java.util.Vector<KeyValue> keyList = new Vector<KeyValue>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		
		//showMetaData(rset);

		//
		// get all keys
		//
		while (rset.next()) {
			KeyValue key = mathModelSimLinkTable.getSimulationKey(rset);
			keyList.addElement(key);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	//
	// put results in an array
	//
	KeyValue keyArray[] = new KeyValue[keyList.size()];
	keyList.copyInto(keyArray);
	return keyArray;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param user cbit.vcell.server.User
 * @param versionable cbit.sql.Versionable
 */
public Versionable getVersionable(QueryHashtable dbc, Connection con, User user, VersionableType vType, KeyValue vKey) 
			throws ObjectNotFoundException, SQLException, DataAccessException {
				
	Versionable versionable = (Versionable) dbc.get(vKey);
	if (versionable != null) {
		return versionable;
	} else {
		if (vType.equals(VersionableType.MathModelMetaData)){
			versionable = getMathModelMetaData(con, user, vKey);
		}else{
			throw new IllegalArgumentException("vType " + vType + " not supported by " + this.getClass());
		}
		dbc.put(versionable.getVersion().getVersionKey(),versionable);
	}
	return versionable;
}


/**
 * This method was created in VisualAge.
 * @param model cbit.vcell.model.Model
 */
private void insertMathModelMetaData(Connection con,User user ,MathModelMetaData mathModel,MathModelChildSummary mmcs,Version newVersion) 
						throws SQLException, DataAccessException, RecordChangedException {
	
	//
	// insert MathModel (with MathDescription reference)
	//
	insertMathModelMetaDataSQL(con,user,mathModel,mmcs,newVersion);
	KeyValue mathModelKey = newVersion.getVersionKey();
	
	//
	// insert Simulation Links
	//
	Enumeration<KeyValue> simEnum = mathModel.getSimulationKeys();
	while (simEnum.hasMoreElements()){
		KeyValue simKey = (KeyValue)simEnum.nextElement();
		insertSimulationEntryLinkSQL(con, keyFactory.getNewKey(con), mathModelKey, simKey);
	}
	
	ApplicationMathTable.table.saveOutputFunctionsMathModel(con, mathModelKey, mathModel.getOutputFunctions(),dbSyntax,keyFactory);
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private void insertMathModelMetaDataSQL(Connection con,User user, MathModelMetaData mathModel,MathModelChildSummary mmcs,Version newVersion) 
					throws SQLException, DataAccessException {

	String sql;
	String mmcs_serialization = null;
	if (mmcs!=null){
		mmcs_serialization = mmcs.toDatabaseSerialization();
	}
	Object[] o = {mathModel,mmcs_serialization};
	sql = DatabasePolicySQL.enforceOwnershipInsert(user,mathModelTable,o,newVersion,dbSyntax);

	if (mmcs_serialization!=null){
		
		varchar2_CLOB_update(
			con,
			sql,
			mmcs_serialization,
			MathModelTable.table,
			newVersion.getVersionKey(),
			MathModelTable.table.childSummaryLarge,
			MathModelTable.table.childSummarySmall,
			dbSyntax);
	}else{
		updateCleanSQL(con,sql);
	}
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private void insertSimulationEntryLinkSQL(Connection con, KeyValue key, KeyValue mathModelKey, KeyValue simKey) throws SQLException, DataAccessException {
	String sql;
	sql = 	"INSERT INTO " + mathModelSimLinkTable.getTableName() + " " + 
				mathModelSimLinkTable.getSQLColumnList() + 
			" VALUES " + mathModelSimLinkTable.getSQLValueList(key, mathModelKey, simKey);
//System.out.println(sql);

	updateCleanSQL(con,sql);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 * @param versionable cbit.sql.Versionable
 * @param pRef cbit.sql.KeyValue
 * @param bCommit boolean
 */
public KeyValue insertVersionable(InsertHashtable hash, Connection con, User user, MathModelMetaData mathModelMetaData,MathModelChildSummary mmcs, String name, boolean bVersion) 
					throws DataAccessException, SQLException, RecordChangedException {
						
	Version newVersion = insertVersionableInit(hash, con, user, mathModelMetaData, name, mathModelMetaData.getDescription(), bVersion);
	insertMathModelMetaData(con, user, mathModelMetaData,mmcs, newVersion);
	return newVersion.getVersionKey();
}


/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 * @param user cbit.vcell.server.User
 * @param image cbit.image.VCImage
 */
public KeyValue updateVersionable(InsertHashtable hash, Connection con, User user, MathModelMetaData mathModelMetaData,MathModelChildSummary mmcs, boolean bVersion) 
			throws DataAccessException, SQLException, RecordChangedException {
				
	Version newVersion = updateVersionableInit(hash, con, user, mathModelMetaData, bVersion);
	insertMathModelMetaData(con, user, mathModelMetaData, mmcs,newVersion);
	return newVersion.getVersionKey();
}
}

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.DependencyException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;
import org.vcell.util.document.Versionable;
import org.vcell.util.document.VersionableType;

import cbit.sql.Field;
import cbit.sql.InsertHashtable;
import cbit.sql.QueryHashtable;
import cbit.sql.RecordChangedException;
import cbit.sql.StarField;
import cbit.sql.Table;
import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.modeldb.DatabasePolicySQL.JoinOp;
import cbit.vcell.modeldb.DatabasePolicySQL.OuterJoin;
import cbit.vcell.modeldb.DatabaseServerImpl.OrderBy;
/**
 * This type was created in VisualAge.
 */
public class BioModelDbDriver extends DbDriver {
	public static final BioModelTable bioModelTable = BioModelTable.table;
	public static final PublicationTable publicationTable = PublicationTable.table;
	public static final UserTable userTable = UserTable.table;
	public static final BioModelSimulationLinkTable bioModelSimLinkTable = BioModelSimulationLinkTable.table;
	public static final BioModelSimContextLinkTable bioModelSimContextLinkTable = BioModelSimContextLinkTable.table;
	public static final SimulationTable simTable = SimulationTable.table;
	public static final SimContextTable simContextTable = SimContextTable.table;

/**
 * LocalDBManager constructor comment.
 */
public BioModelDbDriver(DatabaseSyntax dbSyntax, KeyFactory keyFactory) {
	super(dbSyntax, keyFactory);
}


/**
 * only the owner can delete a Model
 */
private void deleteBioModelMetaDataSQL(Connection con, User user, KeyValue bioModelKey) 
				throws SQLException,DependencyException,PermissionException,DataAccessException,ObjectNotFoundException {

	//
	// get key values of simulations and simulationContexts belonging to this version of BioModel
	// these will be used later for possible deletion
	//
	//KeyValue simContextKeys[] = getDeletableSimContextEntriesFromBioModel(con,user,bioModelKey);
	//KeyValue simKeys[] = getDeletableSimulationEntriesFromBioModel(con,user,bioModelKey);
	//KeyValue modelKey = getModelKeyFromBioModel(con,bioModelKey);

	//
	// delete BioModel
	//
	// automatically deletes BioModel-Simulation link-table entries 		 (ON DELETE CASCADE)
	// automatically deletes BioModel-SimulationContext link-table entries   (ON DELETE CASCADE)
	//
	String sql;
	sql = DatabasePolicySQL.enforceOwnershipDelete(user,bioModelTable,bioModelTable.id.getQualifiedColName()+" = "+bioModelKey);
	updateCleanSQL(con, sql);

	//
	// try to clean up the child simulations and simulationContexts 
	// that are no longer pointed to by any other BioModel.
	//
	// delete simulations first (which deletes ResultSetMetaData via ON DELETE CASCADE)
	//
	//for (int i=0;i<simKeys.length;i++){
		//try {
			//this.simDB.deleteVersionable(con,user,VersionableType.Simulation,simKeys[i]);
			//if (lg.isTraceEnabled()) lg.trace("BioModelDbDriver.delete("+bioModelKey+") deletion of Simulation("+simKeys[i]+") succeeded");
		//}catch (cbit.vcell.server.PermissionException e){
			//if (lg.isTraceEnabled()) lg.trace("BioModelDbDriver.delete("+bioModelKey+") deletion of Simulation("+simKeys[i]+") failed: "+e.getMessage());
		//}catch (cbit.vcell.server.DependencyException e){
			//if (lg.isTraceEnabled()) lg.trace("BioModelDbDriver.delete("+bioModelKey+") deletion of Simulation("+simKeys[i]+") failed: "+e.getMessage());
		//}
	//}
	////
	//// then delete simulationContexts (which in turn deletes it's MathDescription MANUALLY)
	////
	//for (int i=0;i<simContextKeys.length;i++){
		//try {
			//this.simContextDB.deleteVersionable(con,user,VersionableType.SimulationContext,simContextKeys[i]);
			//if (lg.isTraceEnabled()) lg.trace("BioModelDbDriver.delete("+bioModelKey+") deletion of SimulationContext("+simContextKeys[i]+") succeeded");
		//}catch (cbit.vcell.server.PermissionException e){
			//if (lg.isTraceEnabled()) lg.trace("BioModelDbDriver.delete("+bioModelKey+") deletion of SimulationContext("+simContextKeys[i]+") failed: "+e.getMessage());
		//}catch (cbit.vcell.server.DependencyException e){
			//if (lg.isTraceEnabled()) lg.trace("BioModelDbDriver.delete("+bioModelKey+") deletion of SimulationContext("+simContextKeys[i]+") failed: "+e.getMessage());
		//}
	//}
	////
	//// try to remove Model (physiology) used by this BioModel
	////
	//try {
		//this.modelDB.deleteVersionable(con,user,VersionableType.Model,modelKey);
		//if (lg.isTraceEnabled()) lg.trace("BioModelDbDriver.delete("+bioModelKey+") deletion of Model("+modelKey+") succeeded");
	//}catch (cbit.vcell.server.PermissionException e){
		//if (lg.isTraceEnabled()) lg.trace("BioModelDbDriver.delete("+bioModelKey+") deletion of Model("+modelKey+") failed: "+e.getMessage());
	//}catch (cbit.vcell.server.DependencyException e){
		//if (lg.isTraceEnabled()) lg.trace("BioModelDbDriver.delete("+bioModelKey+") deletion of Model("+modelKey+") failed: "+e.getMessage());
	//}

}


/**
 * This method was created in VisualAge.
 * @param user cbit.vcell.server.User
 * @param vType int
 * @param versionKey cbit.sql.KeyValue
 */
@Override
public void deleteVersionable(Connection con, User user, VersionableType vType, KeyValue vKey) 
				throws DependencyException, ObjectNotFoundException,
						SQLException,DataAccessException,PermissionException {

	deleteVersionableInit(con, user, vType, vKey);
	if (vType.equals(VersionableType.BioModelMetaData)){
		deleteBioModelMetaDataSQL(con, user, vKey);
	}else{
		throw new IllegalArgumentException("vType "+vType+" not supported by "+this.getClass());
	}
}


/**
 * getModel method comment.
 */
private BioModelMetaData getBioModelMetaData(Connection con,User user, KeyValue bioModelKey, DatabaseSyntax dbSyntax) 
					throws SQLException, DataAccessException, ObjectNotFoundException {
	if (user == null || bioModelKey == null) {
		throw new IllegalArgumentException("Improper parameters for getBioModelMetaData");
	}
	//if (lg.isTraceEnabled()) lg.trace("BioModelDbDriver.getBioModelMetaData(user=" + user + ", id=" + bioModelKey + ")");

	//
	// to construct a BioModelMetaData as an immutable object, lets collect all keys first
	// (even before authentication).  If the user doesn't authenticate, then throw away the
	// child keys (from link tables).
	//

	//
	// get Simulation Keys for bioModelKey
	//
	KeyValue simKeys[] = getSimulationEntriesFromBioModel(con, bioModelKey);

	//
	// get SimulationContext Keys for bioModelKey
	//
	KeyValue simContextKeys[] = getSimContextEntriesFromBioModel(con, bioModelKey);

	//
	// get BioModelMetaData object for bioModelKey
	//
	String sql;
	VCMetaDataTable vcMetadataTable = VCMetaDataTable.table;
	Field[] f = {new StarField(bioModelTable),userTable.userid,new StarField(vcMetadataTable)};
	Table[] t = {bioModelTable,userTable,vcMetadataTable};
	
	switch (dbSyntax){
	case ORACLE:{
		String condition =	bioModelTable.id.getQualifiedColName() + " = " + bioModelKey + 
						" AND " + 
							userTable.id.getQualifiedColName() + " = " + bioModelTable.ownerRef.getQualifiedColName()+
						" AND "+
							vcMetadataTable.bioModelRef.getQualifiedColName() + "(+) = " + bioModelTable.id.getQualifiedColName();
		sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,null,condition,null,dbSyntax,true);
		break;
	}
	case POSTGRES:{
		String condition =	bioModelTable.id.getQualifiedColName() + " = " + bioModelKey + 
						" AND " + 
							userTable.id.getQualifiedColName() + " = " + bioModelTable.ownerRef.getQualifiedColName()+" ";
//						" AND "+ VCMetaDataTable.table.bioModelRef.getQualifiedColName() + "(+) = " + bioModelTable.id.getQualifiedColName();
		OuterJoin outerJoin = new OuterJoin(vcMetadataTable,bioModelTable,JoinOp.RIGHT_OUTER_JOIN,vcMetadataTable.bioModelRef,bioModelTable.id);
		sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,outerJoin,condition,null,dbSyntax,true);
		break;
	}
	default:{
		throw new RuntimeException("unexpected DatabaseSyntax "+dbSyntax);
	}
	}
	
	Statement stmt = con.createStatement();
	BioModelMetaData bioModelMetaData = null;
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);

		if (rset.next()) {
			bioModelMetaData = bioModelTable.getBioModelMetaData(rset,con,simContextKeys,simKeys,dbSyntax);
		} else {
			throw new ObjectNotFoundException("BioModel id=" + bioModelKey + " not found for user '" + user + "'");
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	return bioModelMetaData;
}


/**
 * getModel method comment.
 */
BioModelMetaData[] getBioModelMetaDatas(Connection con,User user, boolean bAll,DatabaseSyntax dbSyntax) 
					throws SQLException, DataAccessException, ObjectNotFoundException {
	if (user == null) {
		throw new IllegalArgumentException("Improper parameters for getBioModelMetaDatas");
	}
	if (lg.isTraceEnabled()) lg.trace("BioModelDbDriver.getBioModelMetaDatas(user=" + user + ", bAll=" + bAll + ")");

	//
	// to construct a BioModelMetaData as an immutable object, lets collect all keys first
	// (even before authentication).  If the user doesn't authenticate, then throw away the
	// child keys (from link tables).
	//

	//
	// get BioModelMetaData object for bioModelKey
	//
	String sql;
	Field[] f = {new cbit.sql.StarField(bioModelTable),userTable.userid};
	Table[] t = {bioModelTable,userTable};
	String condition =	userTable.id.getQualifiedColName() + " = " + bioModelTable.ownerRef.getQualifiedColName();
	if (!bAll) {
		condition += " AND " + userTable.id.getQualifiedColName() + " = " + user.getID();
	}
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,(OuterJoin)null,condition,null,dbSyntax);
	//
	StringBuffer newSQL = new StringBuffer(sql);
	newSQL.insert(7,Table.SQL_GLOBAL_HINT);
	sql = newSQL.toString();
	//
	Statement stmt = con.createStatement();
	Vector<BioModelMetaData> bioModelMetaDataList = new Vector<BioModelMetaData>();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);

		while (rset.next()) {
			BioModelMetaData bioModelMetaData = bioModelTable.getBioModelMetaData(rset,this,con, dbSyntax);
			bioModelMetaDataList.addElement(bioModelMetaData);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	BioModelMetaData bioModelMetaDataArray[] = new BioModelMetaData[bioModelMetaDataList.size()];
	bioModelMetaDataList.copyInto(bioModelMetaDataArray);
	return bioModelMetaDataArray;
}


/**
 * getModels method comment.
 */
KeyValue[] getDeletableSimContextEntriesFromBioModel(Connection con,User user,KeyValue bioModelKey) throws SQLException, DataAccessException {
//	if (lg.isTraceEnabled()) lg.trace("BioModelDbDriver.getSimContextEntriesFromBioModel(bioModelKey=" + bioModelKey + ")");
	String sql;
	
	sql = 	" SELECT " + bioModelSimContextLinkTable.simContextRef.getQualifiedColName() +
			" FROM " + bioModelSimContextLinkTable.getTableName() + "," + simContextTable.getTableName() +
			" WHERE " + bioModelSimContextLinkTable.bioModelRef.getQualifiedColName() + " = " + bioModelKey +
			" AND " + bioModelSimContextLinkTable.simContextRef + " = " + simContextTable.id.getQualifiedColName() +
			" AND " + simContextTable.versionFlag.getQualifiedColName() + " != " + VersionFlag.Archived.getIntValue() +
			" AND " + simContextTable.ownerRef.getQualifiedColName() + " = " + user.getID();
			
	Statement stmt = con.createStatement();
	Vector<KeyValue> keyList = new Vector<KeyValue>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		
		//showMetaData(rset);

		//
		// get all keys
		//
		while (rset.next()) {
			KeyValue key = bioModelSimContextLinkTable.getSimContextKey(rset);
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
 * getModels method comment.
 */
KeyValue[] getDeletableSimulationEntriesFromBioModel(Connection con,User user,KeyValue bioModelKey) throws SQLException, DataAccessException {
//	if (lg.isTraceEnabled()) lg.trace("BioModelDbDriver.getSimulationEntriesFromBioModel(bioModelKey=" + bioModelKey + ")");
	String sql;
	
	sql = 	" SELECT " + bioModelSimLinkTable.simRef.getQualifiedColName() +
			" FROM " + bioModelSimLinkTable.getTableName() + "," + simTable.getTableName() +
			" WHERE " + bioModelSimLinkTable.bioModelRef.getQualifiedColName() + " = " + bioModelKey +
			" AND " + bioModelSimLinkTable.simRef.getQualifiedColName() + " = " + simTable.id.getQualifiedColName() +
			" AND " + simTable.versionFlag.getQualifiedColName() + " != " + VersionFlag.Archived.getIntValue() +
			" AND " + simTable.ownerRef.getQualifiedColName() + " = " + user.getID();

	Statement stmt = con.createStatement();
	Vector<KeyValue> keyList = new Vector<KeyValue>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		
		//showMetaData(rset);

		//
		// get all keys
		//
		while (rset.next()) {
			KeyValue key = bioModelSimLinkTable.getSimulationKey(rset);
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
 * getModels method comment.
 */
KeyValue[] getSimContextEntriesFromBioModel(Connection con,KeyValue bioModelKey) throws SQLException, DataAccessException {
//	if (lg.isTraceEnabled()) lg.trace("BioModelDbDriver.getSimContextEntriesFromBioModel(bioModelKey=" + bioModelKey + ")");
	String sql;
	
	sql = 	" SELECT " + bioModelSimContextLinkTable.simContextRef +
			" FROM " + bioModelSimContextLinkTable.getTableName() + 
			" WHERE " + bioModelSimContextLinkTable.bioModelRef + " = " + bioModelKey +
			" ORDER BY " + bioModelSimContextLinkTable.id;
			
	Statement stmt = con.createStatement();
	Vector<KeyValue> keyList = new Vector<KeyValue>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		
		//showMetaData(rset);

		//
		// get all keys
		//
		while (rset.next()) {
			KeyValue key = bioModelSimContextLinkTable.getSimContextKey(rset);
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
 * getModels method comment.
 */
KeyValue[] getSimulationEntriesFromBioModel(Connection con,KeyValue bioModelKey) throws SQLException, DataAccessException {
//	if (lg.isTraceEnabled()) lg.trace("BioModelDbDriver.getSimulationEntriesFromBioModel(bioModelKey=" + bioModelKey + ")");
	String sql;
	
	sql = 	" SELECT " + bioModelSimLinkTable.simRef +
			" FROM " + bioModelSimLinkTable.getTableName() + 
			" WHERE " + bioModelSimLinkTable.bioModelRef + " = " + bioModelKey +
			" ORDER BY " + bioModelSimLinkTable.id;
			
	Statement stmt = con.createStatement();
	Vector<KeyValue> keyList = new Vector<KeyValue>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		
		//showMetaData(rset);

		//
		// get all keys
		//
		while (rset.next()) {
			KeyValue key = bioModelSimLinkTable.getSimulationKey(rset);
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
		if (vType.equals(VersionableType.BioModelMetaData)){
			versionable = getBioModelMetaData(con, user, vKey, dbSyntax);
		}else{
			throw new IllegalArgumentException("vType " + vType + " not supported by " + this.getClass());
		}
		dbc.put(versionable.getVersion().getVersionKey(),versionable);
	}
//	MIRIAMTable.table.setMIRIAMAnnotation(con, (BioModelMetaData)versionable, versionable.getVersion().getVersionKey());
	return versionable;
}


/**
 * This method was created in VisualAge.
 * @param model cbit.vcell.model.Model
 */
private void insertBioModelMetaData(Connection con,User user ,BioModelMetaData bioModel,BioModelChildSummary bmcs,Version newVersion) 
						throws SQLException, DataAccessException, RecordChangedException {
	
	//
	// insert BioModel (with Model reference)
	//
	insertBioModelMetaDataSQL(con,user,bioModel,bmcs,newVersion);
	KeyValue bioModelKey = newVersion.getVersionKey();
	
	//
	// insert Simulation Links
	//
	Enumeration<KeyValue> simEnum = bioModel.getSimulationKeys();
	while (simEnum.hasMoreElements()){
		KeyValue simKey = (KeyValue)simEnum.nextElement();
		insertSimulationEntryLinkSQL(con, keyFactory.getNewKey(con), bioModelKey, simKey);
	}
	//
	// insert SimulationContext Links
	//
	Enumeration<KeyValue> scEnum = bioModel.getSimulationContextKeys();
	while (scEnum.hasMoreElements()){
		KeyValue scKey = (KeyValue)scEnum.nextElement();
		insertSimContextEntryLinkSQL(con, keyFactory.getNewKey(con), bioModelKey, scKey);
	}
	//
	//Insert VCMetaData
	//
//	MIRIAMTable.table.insertMIRIAM(con, bioModel, newVersion.getVersionKey());
	VCMetaDataTable.insertVCMetaData(con,bioModel.getVCMetaDataXML(),bioModelKey,keyFactory.getNewKey(con),dbSyntax);
	
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private void insertBioModelMetaDataSQL(Connection con,User user, BioModelMetaData bioModel,BioModelChildSummary bmcs,Version newVersion) 
					throws SQLException, DataAccessException {

	String sql;
	String bmcs_serialization = null;
	if (bmcs!=null){
		bmcs_serialization = bmcs.toDatabaseSerialization();
	}
	Object[] o = {bioModel,bmcs_serialization};
	sql = DatabasePolicySQL.enforceOwnershipInsert(user,bioModelTable,o,newVersion,dbSyntax);

	if (bmcs_serialization!=null){
		
		varchar2_CLOB_update(
			con,
			sql,
			bmcs_serialization,
			BioModelTable.table,
			newVersion.getVersionKey(),
			BioModelTable.table.childSummaryLarge,
			BioModelTable.table.childSummarySmall,
			dbSyntax
			);
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
private void insertSimContextEntryLinkSQL(Connection con, KeyValue key, KeyValue bioModelKey, KeyValue scKey) throws SQLException, DataAccessException {
	String sql;
	sql = 	"INSERT INTO " + bioModelSimContextLinkTable.getTableName() + " " + 
				bioModelSimContextLinkTable.getSQLColumnList() + 
			" VALUES " + bioModelSimContextLinkTable.getSQLValueList(key, bioModelKey, scKey);
//System.out.println(sql);

	updateCleanSQL(con,sql);
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private void insertSimulationEntryLinkSQL(Connection con, KeyValue key, KeyValue bioModelKey, KeyValue simKey) throws SQLException, DataAccessException {
	String sql;
	sql = 	"INSERT INTO " + bioModelSimLinkTable.getTableName() + " " + 
				bioModelSimLinkTable.getSQLColumnList() + 
			" VALUES " + bioModelSimLinkTable.getSQLValueList(key, bioModelKey, simKey);
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
public KeyValue insertVersionable(InsertHashtable hash, Connection con, User user, BioModelMetaData bioModelMetaData, BioModelChildSummary bmcs,String name, boolean bVersion) 
					throws DataAccessException, SQLException, RecordChangedException {
						
	Version newVersion = insertVersionableInit(hash, con, user, bioModelMetaData, name, bioModelMetaData.getDescription(), bVersion);
	insertBioModelMetaData(con, user, bioModelMetaData, bmcs,newVersion);
	return newVersion.getVersionKey();
}


/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 * @param user cbit.vcell.server.User
 * @param image cbit.image.VCImage
 */
public KeyValue updateVersionable(InsertHashtable hash, Connection con, User user, BioModelMetaData bioModelMetaData, BioModelChildSummary bmcs,boolean bVersion) 
			throws DataAccessException, SQLException, RecordChangedException {
				
	Version newVersion = updateVersionableInit(hash, con, user, bioModelMetaData, bVersion);
	insertBioModelMetaData(con, user, bioModelMetaData, bmcs,newVersion);
	return newVersion.getVersionKey();
}


public BioModelRep[] getBioModelReps(Connection con, User user, String conditions, OrderBy orderBy, int startRow, int numRows)
		throws SQLException, DataAccessException, ObjectNotFoundException {
	if (user == null) {
		throw new IllegalArgumentException("Improper parameters for getBioModelMetaDatas");
	}
	if (lg.isTraceEnabled()) lg.trace("BioModelDbDriver.getBioModelReps(user=" + user + ", conditions=" + conditions + ")");
	
	String sql = bioModelTable.getPreparedStatement_BioModelReps(conditions, orderBy, startRow, numRows);
	
	PreparedStatement stmt = con.prepareStatement(sql);
//	System.out.println(sql);
	bioModelTable.setPreparedStatement_BioModelReps(stmt, user, startRow, numRows);

	ArrayList<BioModelRep> bioModelReps = new ArrayList<BioModelRep>();
	try {
		ResultSet rset = stmt.executeQuery();

		//showMetaData(rset);

		while (rset.next()) {
			BioModelRep bioModelRep = bioModelTable.getBioModelRep(user,rset);
			bioModelReps.add(bioModelRep);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	return bioModelReps.toArray(new BioModelRep[bioModelReps.size()]);
}

public PublicationRep[] getPublicationReps(Connection con, User user, String conditions, OrderBy orderBy)
		throws SQLException, DataAccessException, ObjectNotFoundException {
	if (user == null) {
		throw new IllegalArgumentException("Improper parameters for getPublicationReps");
	}
	if (lg.isTraceEnabled()) lg.trace("BioModelDbDriver.getPublicationReps(user=" + user + ", conditions=" + conditions + ")");
	
	String sql = publicationTable.getPreparedStatement_PublicationReps(conditions, orderBy);
	
	PreparedStatement stmt = con.prepareStatement(sql);
//	System.out.println(sql);
	publicationTable.setPreparedStatement_PublicationReps(stmt, user);

	ArrayList<PublicationRep> publicationReps = new ArrayList<PublicationRep>();
	try {
		ResultSet rset = stmt.executeQuery();

		//showMetaData(rset);

		while (rset.next()) {
			PublicationRep publicationRep = publicationTable.getPublicationRep(user,rset);
			publicationReps.add(publicationRep);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	return publicationReps.toArray(new PublicationRep[publicationReps.size()]);
}

}

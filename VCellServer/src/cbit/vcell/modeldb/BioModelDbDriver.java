package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.DataAccessException;
import cbit.util.KeyValue;
import cbit.util.ObjectNotFoundException;
import cbit.util.SessionLog;
import cbit.util.User;
import cbit.util.Version;
import cbit.util.VersionFlag;
import cbit.util.Versionable;
import cbit.vcell.biomodel.*;
import java.beans.*;
import java.sql.*;
import java.sql.Statement;
import java.util.*;
import cbit.sql.*;
import cbit.vcell.model.*;
import cbit.vcell.mapping.*;
/**
 * This type was created in VisualAge.
 */
public class BioModelDbDriver extends DbDriver {
	public static final BioModelTable bioModelTable = BioModelTable.table;
	public static final UserTable userTable = UserTable.table;
	public static final BioModelSimulationLinkTable bioModelSimLinkTable = BioModelSimulationLinkTable.table;
	public static final BioModelSimContextLinkTable bioModelSimContextLinkTable = BioModelSimContextLinkTable.table;
	public static final SimulationTable simTable = SimulationTable.table;
	public static final SimContextTable simContextTable = SimContextTable.table;
	private SimulationDbDriver simDB = null;
	private SimulationContextDbDriver simContextDB = null;
	private ModelDbDriver modelDB = null;

/**
 * LocalDBManager constructor comment.
 */
public BioModelDbDriver(DBCacheTable argdbc,SimulationDbDriver argSimDB, SimulationContextDbDriver argSimContextDB, ModelDbDriver argModelDB, SessionLog sessionLog) {
	super(argdbc,sessionLog);
	this.simDB = argSimDB;
	this.simContextDB = argSimContextDB;
	this.modelDB = argModelDB;
}


/**
 * only the owner can delete a Model
 */
private void deleteBioModelMetaDataSQL(Connection con, User user, KeyValue bioModelKey) 
				throws SQLException,cbit.vcell.server.DependencyException,cbit.vcell.server.PermissionException,DataAccessException,ObjectNotFoundException {

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
			//log.print("BioModelDbDriver.delete("+bioModelKey+") deletion of Simulation("+simKeys[i]+") succeeded");
		//}catch (cbit.vcell.server.PermissionException e){
			//log.print("BioModelDbDriver.delete("+bioModelKey+") deletion of Simulation("+simKeys[i]+") failed: "+e.getMessage());
		//}catch (cbit.vcell.server.DependencyException e){
			//log.print("BioModelDbDriver.delete("+bioModelKey+") deletion of Simulation("+simKeys[i]+") failed: "+e.getMessage());
		//}
	//}
	////
	//// then delete simulationContexts (which in turn deletes it's MathDescription MANUALLY)
	////
	//for (int i=0;i<simContextKeys.length;i++){
		//try {
			//this.simContextDB.deleteVersionable(con,user,VersionableType.SimulationContext,simContextKeys[i]);
			//log.print("BioModelDbDriver.delete("+bioModelKey+") deletion of SimulationContext("+simContextKeys[i]+") succeeded");
		//}catch (cbit.vcell.server.PermissionException e){
			//log.print("BioModelDbDriver.delete("+bioModelKey+") deletion of SimulationContext("+simContextKeys[i]+") failed: "+e.getMessage());
		//}catch (cbit.vcell.server.DependencyException e){
			//log.print("BioModelDbDriver.delete("+bioModelKey+") deletion of SimulationContext("+simContextKeys[i]+") failed: "+e.getMessage());
		//}
	//}
	////
	//// try to remove Model (physiology) used by this BioModel
	////
	//try {
		//this.modelDB.deleteVersionable(con,user,VersionableType.Model,modelKey);
		//log.print("BioModelDbDriver.delete("+bioModelKey+") deletion of Model("+modelKey+") succeeded");
	//}catch (cbit.vcell.server.PermissionException e){
		//log.print("BioModelDbDriver.delete("+bioModelKey+") deletion of Model("+modelKey+") failed: "+e.getMessage());
	//}catch (cbit.vcell.server.DependencyException e){
		//log.print("BioModelDbDriver.delete("+bioModelKey+") deletion of Model("+modelKey+") failed: "+e.getMessage());
	//}

}


/**
 * This method was created in VisualAge.
 * @param user cbit.vcell.server.User
 * @param vType int
 * @param versionKey cbit.sql.KeyValue
 */
public void deleteVersionable(Connection con, User user, VersionableType vType, KeyValue vKey) 
				throws cbit.vcell.server.DependencyException, ObjectNotFoundException,
						SQLException,DataAccessException,cbit.vcell.server.PermissionException {

	deleteVersionableInit(con, user, vType, vKey);
	if (vType.equals(VersionableType.BioModelMetaData)){
		deleteBioModelMetaDataSQL(con, user, vKey);
		dbc.remove(vKey);
	}else{
		throw new IllegalArgumentException("vType "+vType+" not supported by "+this.getClass());
	}
}


/**
 * getModel method comment.
 */
private BioModelMetaData getBioModelMetaData(Connection con,User user, KeyValue bioModelKey) 
					throws SQLException, DataAccessException, ObjectNotFoundException {
	if (user == null || bioModelKey == null) {
		throw new IllegalArgumentException("Improper parameters for getBioModelMetaData");
	}
	//log.print("BioModelDbDriver.getBioModelMetaData(user=" + user + ", id=" + bioModelKey + ")");

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
	Field[] f = {new cbit.sql.StarField(bioModelTable),userTable.userid};
	Table[] t = {bioModelTable,userTable};
	String condition =	bioModelTable.id.getQualifiedColName() + " = " + bioModelKey + 
					" AND " + 
						userTable.id.getQualifiedColName() + " = " + bioModelTable.ownerRef.getQualifiedColName();
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,null,true);

	Statement stmt = con.createStatement();
	BioModelMetaData bioModelMetaData = null;
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);

		if (rset.next()) {
			bioModelMetaData = bioModelTable.getBioModelMetaData(rset,con,log,simContextKeys,simKeys);
		} else {
			throw new cbit.util.ObjectNotFoundException("BioModel id=" + bioModelKey + " not found for user '" + user + "'");
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	return bioModelMetaData;
}


/**
 * getModel method comment.
 */
BioModelMetaData[] getBioModelMetaDatas(Connection con,User user, boolean bAll) 
					throws SQLException, DataAccessException, ObjectNotFoundException {
	if (user == null) {
		throw new IllegalArgumentException("Improper parameters for getBioModelMetaDatas");
	}
	log.print("BioModelDbDriver.getBioModelMetaDatas(user=" + user + ", bAll=" + bAll + ")");

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
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,null,true);
	//
	StringBuffer newSQL = new StringBuffer(sql);
	newSQL.insert(7,Table.SQL_GLOBAL_HINT);
	sql = newSQL.toString();
	//
	Statement stmt = con.createStatement();
	Vector bioModelMetaDataList = new Vector();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);

		while (rset.next()) {
			BioModelMetaData bioModelMetaData = bioModelTable.getBioModelMetaData(rset,log,this,con);
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
//	log.print("BioModelDbDriver.getSimContextEntriesFromBioModel(bioModelKey=" + bioModelKey + ")");
	String sql;
	
	sql = 	" SELECT " + bioModelSimContextLinkTable.simContextRef.getQualifiedColName() +
			" FROM " + bioModelSimContextLinkTable.getTableName() + "," + simContextTable.getTableName() +
			" WHERE " + bioModelSimContextLinkTable.bioModelRef.getQualifiedColName() + " = " + bioModelKey +
			" AND " + bioModelSimContextLinkTable.simContextRef + " = " + simContextTable.id.getQualifiedColName() +
			" AND " + simContextTable.versionFlag.getQualifiedColName() + " != " + VersionFlag.Archived.getIntValue() +
			" AND " + simContextTable.ownerRef.getQualifiedColName() + " = " + user.getID();
			
	Statement stmt = con.createStatement();
	java.util.Vector keyList = new Vector();
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
//	log.print("BioModelDbDriver.getSimulationEntriesFromBioModel(bioModelKey=" + bioModelKey + ")");
	String sql;
	
	sql = 	" SELECT " + bioModelSimLinkTable.simRef.getQualifiedColName() +
			" FROM " + bioModelSimLinkTable.getTableName() + "," + simTable.getTableName() +
			" WHERE " + bioModelSimLinkTable.bioModelRef.getQualifiedColName() + " = " + bioModelKey +
			" AND " + bioModelSimLinkTable.simRef.getQualifiedColName() + " = " + simTable.id.getQualifiedColName() +
			" AND " + simTable.versionFlag.getQualifiedColName() + " != " + VersionFlag.Archived.getIntValue() +
			" AND " + simTable.ownerRef.getQualifiedColName() + " = " + user.getID();

	Statement stmt = con.createStatement();
	java.util.Vector keyList = new Vector();
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
private KeyValue getModelKeyFromBioModel(Connection con,KeyValue bioModelKey) throws SQLException, DataAccessException {

	KeyValue modelKey = null;
	String sql;
	
	sql = 	" SELECT " + BioModelTable.table.modelRef  +
			" FROM " + BioModelTable.table.getTableName() + 
			" WHERE " + BioModelTable.table.id + " = " + bioModelKey;
			
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		
		//showMetaData(rset);

		//
		// get all keys
		//
		if (rset.next()) {
			modelKey = new KeyValue(rset.getBigDecimal(BioModelTable.table.modelRef.getUnqualifiedColName()));
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}

	return modelKey;
}


/**
 * getModels method comment.
 */
KeyValue[] getSimContextEntriesFromBioModel(Connection con,KeyValue bioModelKey) throws SQLException, DataAccessException {
//	log.print("BioModelDbDriver.getSimContextEntriesFromBioModel(bioModelKey=" + bioModelKey + ")");
	String sql;
	
	sql = 	" SELECT " + bioModelSimContextLinkTable.simContextRef +
			" FROM " + bioModelSimContextLinkTable.getTableName() + 
			" WHERE " + bioModelSimContextLinkTable.bioModelRef + " = " + bioModelKey +
			" ORDER BY " + bioModelSimContextLinkTable.id;
			
	Statement stmt = con.createStatement();
	java.util.Vector keyList = new Vector();
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
//	log.print("BioModelDbDriver.getSimulationEntriesFromBioModel(bioModelKey=" + bioModelKey + ")");
	String sql;
	
	sql = 	" SELECT " + bioModelSimLinkTable.simRef +
			" FROM " + bioModelSimLinkTable.getTableName() + 
			" WHERE " + bioModelSimLinkTable.bioModelRef + " = " + bioModelKey +
			" ORDER BY " + bioModelSimLinkTable.id;
			
	Statement stmt = con.createStatement();
	java.util.Vector keyList = new Vector();
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
public Versionable getVersionable(Connection con, User user, VersionableType vType, KeyValue vKey) 
			throws ObjectNotFoundException, SQLException, DataAccessException {
				
	Versionable versionable = (Versionable) dbc.get(vKey);
	if (versionable != null) {
		return versionable;
	} else {
		if (vType.equals(VersionableType.BioModelMetaData)){
			versionable = getBioModelMetaData(con, user, vKey);
		}else{
			throw new IllegalArgumentException("vType " + vType + " not supported by " + this.getClass());
		}
		dbc.putUnprotected(versionable.getVersion().getVersionKey(),versionable);
	}
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
	Enumeration simEnum = bioModel.getSimulationKeys();
	while (simEnum.hasMoreElements()){
		KeyValue simKey = (KeyValue)simEnum.nextElement();
		insertSimulationEntryLinkSQL(con, getNewKey(con), bioModelKey, simKey);
	}
	//
	// insert SimulationContext Links
	//
	Enumeration scEnum = bioModel.getSimulationContextKeys();
	while (scEnum.hasMoreElements()){
		KeyValue scKey = (KeyValue)scEnum.nextElement();
		insertSimContextEntryLinkSQL(con, getNewKey(con), bioModelKey, scKey);
	}
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
	sql = DatabasePolicySQL.enforceOwnershipInsert(user,bioModelTable,o,newVersion);

	if (bmcs_serialization!=null){
		
		varchar2_CLOB_update(
			con,
			sql,
			bmcs_serialization,
			BioModelTable.table,
			newVersion.getVersionKey(),
			BioModelTable.table.childSummaryLarge,
			BioModelTable.table.childSummarySmall
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
}
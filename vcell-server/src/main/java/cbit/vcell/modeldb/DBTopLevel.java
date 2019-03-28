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
import java.sql.SQLException;
import java.util.TreeMap;
import java.util.Vector;

import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.DependencyException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.Preference;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.CurateSpec;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelChildSummary;
import org.vcell.util.document.ReferenceQueryResult;
import org.vcell.util.document.ReferenceQuerySpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.VCInfoContainer;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.Versionable;
import org.vcell.util.document.VersionableFamily;
import org.vcell.util.document.VersionableRelationship;
import org.vcell.util.document.VersionableType;
import org.vcell.util.document.VersionableTypeVersion;

import cbit.image.VCImage;
import cbit.sql.InsertHashtable;
import cbit.sql.QueryHashtable;
import cbit.sql.RecordChangedException;
import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.model.Model;
import cbit.vcell.modeldb.DatabaseServerImpl.OrderBy;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestSuiteNew;
import cbit.vcell.numericstest.TestSuiteOPResults;
import cbit.vcell.server.UserRegistrationOP;
import cbit.vcell.server.UserRegistrationResults;
import cbit.vcell.solver.Simulation;
/**
 * This type was created in VisualAge.
 */
public class DBTopLevel extends AbstractDBTopLevel{
	private final GeomDbDriver geomDB;
	private final MathDescriptionDbDriver mathDB;
	private final ModelDbDriver modelDB;
	private final ReactStepDbDriver reactStepDB;
	private final SimulationDbDriver simulationDB;
	private final SimulationContextDbDriver simContextDB;
	private final BioModelDbDriver bioModelDB;
	private final MathModelDbDriver mathModelDB;
	private final UserDbDriver userDB;
//	private DBCacheTable dbCacheTable = null;

	private static final int SQL_ERROR_CODE_BADCONNECTION = 1010; //??????????????????????????????????????

/**
 * DBTopLevel constructor comment.
 */
DBTopLevel(ConnectionFactory aConFactory) throws SQLException{
	super(aConFactory);
	
	KeyFactory keyFactory = conFactory.getKeyFactory();
	DatabaseSyntax databaseSyntax = conFactory.getDatabaseSyntax();
	this.geomDB = new GeomDbDriver(databaseSyntax, keyFactory);
	this.mathDB = new MathDescriptionDbDriver(this.geomDB);
	this.reactStepDB = new ReactStepDbDriver(databaseSyntax, keyFactory, null, new DictionaryDbDriver(keyFactory, databaseSyntax));
	this.modelDB = new ModelDbDriver(this.reactStepDB);
	this.reactStepDB.init(modelDB);
	this.simulationDB = new SimulationDbDriver(this.mathDB);
	this.simContextDB = new SimulationContextDbDriver(this.geomDB,this.modelDB,this.mathDB);
	this.userDB = new UserDbDriver(); 
	this.bioModelDB = new BioModelDbDriver(databaseSyntax,keyFactory);
	this.mathModelDB = new MathModelDbDriver(databaseSyntax, keyFactory);
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:16:41 AM)
 * @return cbit.vcell.modeldb.ReferenceQueryResult
 * @param user cbit.vcell.server.User
 * @param rqs cbit.vcell.modeldb.ReferenceQuerySpec
 */
VCDocumentInfo curate(User user,CurateSpec curateSpec) throws DataAccessException,java.sql.SQLException{
	return curate0(user,curateSpec,true);
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:16:41 AM)
 * @return cbit.vcell.modeldb.ReferenceQueryResult
 * @param user cbit.vcell.server.User
 * @param rqs cbit.vcell.modeldb.ReferenceQuerySpec
 */
private VCDocumentInfo curate0(User user,CurateSpec curateSpec,boolean bEnableRetry) throws DataAccessException,java.sql.SQLException{

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		VCDocumentInfo newVCDocumentInfo = DbDriver.curate(curateSpec,con,user,conFactory.getDatabaseSyntax());
		con.commit();
		return newVCDocumentInfo;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return curate0(user,curateSpec,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null;
		}
	}finally{
		conFactory.release(con,lock);
	}

}


void cleanupDatabase(boolean bEnableRetry) throws DataAccessException,java.sql.SQLException{

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		StringBuffer stringBuffer = new StringBuffer();
		DBBackupAndClean.cleanupDatabase(con, stringBuffer);
		if (lg.isDebugEnabled()) lg.debug(stringBuffer.toString());
	
		con.commit();
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			cleanupDatabase(false);
		}else{
			handle_DataAccessException_SQLException(e);
		}
	}finally{
		conFactory.release(con,lock);
	}

}


/**
 * Insert the method's description here.
 * Creation date: (9/25/2003 7:57:54 AM)
 * @return cbit.vcell.modeldb.VCInfoContainer
 * @param user cbit.vcell.server.User
 */
FieldDataDBOperationResults fieldDataDBOperation(User user, FieldDataDBOperationSpec fieldDataDBOperationSpec, boolean bEnableRetry) throws SQLException, DataAccessException {
	
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		FieldDataDBOperationResults fieldDataDBOperationResults =
			DbDriver.fieldDataDBOperation(con, conFactory.getKeyFactory(), user,fieldDataDBOperationSpec);
		con.commit();
		return fieldDataDBOperationResults;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return fieldDataDBOperation(user,fieldDataDBOperationSpec,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null;
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
void deleteVersionable(User user, VersionableType versionableType, KeyValue key, boolean bEnableRetry) 
		throws java.sql.SQLException, DataAccessException, DependencyException, PermissionException  {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	DbDriver driver = getDbDriver(versionableType);
	try {
		driver.deleteVersionable(con, user, versionableType, key);
		con.commit();
		return;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			deleteVersionable(user,versionableType,key,false);
		}else{
			handle_DataAccessException_SQLException(e);
		}
	}finally{
		conFactory.release(con,lock);
	}
}


UserRegistrationResults userRegistrationOP(User user, UserRegistrationOP userRegistrationOP, boolean bEnableRetry) 
throws java.sql.SQLException, DataAccessException, DependencyException, PermissionException  {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue userKey = null;
//		if(userRegistrationOP.getOperationType().equals(UserRegistrationOP.USERREGOP_NEWREGISTER)){
//			userKey = userDB.insertUserInfo(con, userRegistrationOP.getUserInfo());
//		}
		if(userRegistrationOP.getOperationType().equals(UserRegistrationOP.USERREGOP_LOSTPASSWORD)){
			userDB.sendLostPassword(con, userRegistrationOP.getUserid());
			return null;
		}else if(userRegistrationOP.getOperationType().equals(UserRegistrationOP.USERREGOP_GETINFO)){
			userKey = userRegistrationOP.getUserKey();
//			UserInfo userInfo = null;
//			if(userRegistrationOP.getUserKey() != null){
//				userInfo = userDB.getUserInfo(con, userRegistrationOP.getUserKey());
//			}
//			else{
//				User validatedUser = userDB.getUserFromUseridAndPassword(con, userRegistrationOP.getUserid(), userRegistrationOP.getPassword());				
//				userInfo = userDB.getUserInfo(con, validatedUser.getID());
//			}
//			return new UserRegistrationResults(userInfo);
		}else if(userRegistrationOP.getOperationType().equals(UserRegistrationOP.USERREGOP_UPDATE)){
			userKey = userRegistrationOP.getUserKey();
			userDB.updateUserInfo(con, userRegistrationOP.getUserInfo());
			con.commit();
		}else if(userRegistrationOP.getOperationType().equals(UserRegistrationOP.USERREGOP_ISUSERIDUNIQUE)){
			return new UserRegistrationResults(userDB.getUserFromUserid(con, userRegistrationOP.getUserid()) == null);
		}else{
			throw new IllegalArgumentException(this.getClass().getName()+".userRegistrationOP Unknown operationType="+userRegistrationOP.getOperationType());
		}
		return new UserRegistrationResults(userDB.getUserInfo(con, userKey));
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return userRegistrationOP(user,userRegistrationOP,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null;
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
TestSuiteOPResults doTestSuiteOP(cbit.vcell.numericstest.TestSuiteOP tsop,User user,boolean bEnableRetry) 
				throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		TestSuiteOPResults tsor  = DbDriver.testSuiteOP(tsop,con,user,conFactory.getKeyFactory());
		con.commit();
		return tsor;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return doTestSuiteOP(tsop,user,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	}finally{
		conFactory.release(con,lock);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:16:41 AM)
 * @return cbit.vcell.modeldb.ReferenceQueryResult
 * @param user cbit.vcell.server.User
 * @param rqs cbit.vcell.modeldb.ReferenceQuerySpec
 */
ReferenceQueryResult findReferences(User user, ReferenceQuerySpec rqs2, boolean bEnableRetry) throws DataAccessException,java.sql.SQLException{

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		
		//Use MathDescription for search if ExternalData
		VersionableType rqsVType = null;
		KeyValue[] vTypeKeys = null;
		if(rqs2.isVersionableType()){
			rqsVType = rqs2.getVersionableType();
			vTypeKeys = new KeyValue[] {rqs2.getKeyValue()};
		}else if(rqs2.isExternalDataIdentiferType()){
			DbDriver.cleanupDeletedReferences(con, user, rqs2.getExternalDataIdentifier(),false);
			con.commit();
			rqsVType = VersionableType.MathDescription;
			vTypeKeys = getMathDescKeysForExternalData(rqs2.getKeyValue(), user,true);
			if(vTypeKeys == null || vTypeKeys.length == 0){
				return null;
			}
		}
		if(rqsVType == null){
			throw new DataAccessException("findAllReferences error: Couldn't determine Query type");
		}
		VersionableFamily finalVersionalbeFamily = null;
		for(int k=0;k<vTypeKeys.length;k+= 1){
			//Find references
			VersionableFamily vf = getAllReferences(user,vTypeKeys[k],rqsVType,true);
			//Check permission
			if(vf.bDependants()){
				VersionableTypeVersion[] vtvArr = vf.getUniqueDependants();
				for(int i=0;i<vtvArr.length;i+= 1){
					if(vtvArr[i].getVType().equals(VersionableType.BioModelMetaData)){
						Vector<VersionInfo> checkedVInfos = getVersionableInfos(user,vtvArr[i].getVersion().getVersionKey(),VersionableType.BioModelMetaData,false,true,true);
						if(checkedVInfos == null || checkedVInfos.size() == 0){throw new DataAccessException("References Not Accessible");}
					}else if(vtvArr[i].getVType().equals(VersionableType.MathModelMetaData)){
						Vector<VersionInfo> checkedVInfos = getVersionableInfos(user,vtvArr[i].getVersion().getVersionKey(),VersionableType.MathModelMetaData,false,true,true);
						if(checkedVInfos == null || checkedVInfos.size() == 0){throw new DataAccessException("References Not Accessible");}
					}
				}
			}
			if(finalVersionalbeFamily == null){
				finalVersionalbeFamily = vf;
			}else{
				VersionableRelationship[] versRelArr = vf.getDependantRelationships();
				for(int r=0;r<versRelArr.length;r+= 1){
					finalVersionalbeFamily.addDependantRelationship(versRelArr[r]);
				}
			}
		}
		
		return new ReferenceQueryResult(finalVersionalbeFamily);
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return findReferences(user,rqs2,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
VersionableFamily getAllReferences(User user,KeyValue key, VersionableType versionableType, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
				
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return DbDriver.getAllReferences(con,versionableType, key);
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getAllReferences(user,key,versionableType,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
BioModelMetaData getBioModelMetaData(QueryHashtable dbc, User user, KeyValue key) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
	return (BioModelMetaData)getVersionable(dbc, user, key, VersionableType.BioModelMetaData, true, true);
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
BioModelMetaData[] getBioModelMetaDatas(User user, boolean bAll, boolean bEnableRetry) 
				throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {

	//New
	//Versionable cacheObj = (Versionable) dbCacheTable.get(key);
	//if (cacheObj != null) {
	//	return cacheObj;
	//}
	//NewEnd

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		//New
		//Object dbV = driver.getVersionable(con, user, versionableType, key);
		//dbCacheTable.put((Cacheable) dbV);
		//return (Versionable) dbV;
		//NewEnd
		return bioModelDB.getBioModelMetaDatas(con,user,bAll,conFactory.getDatabaseSyntax());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getBioModelMetaDatas(user, bAll, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
String getBioModelXML(User user, KeyValue key, boolean bEnableRetry) 
				throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	DbDriver driver = getDbDriver(VersionableType.BioModelMetaData);
	try {
		//
		// Getting the corresponding VersionInfo will fail if you don't have permission to the object.
		// This is needed because the DbDriver-level services can return objects directly from the
		// cache without checking for permissions first.
		//
		// This check is placed in DbTopLevel because this is the client API entry point.
		// Child objects (of the requested object) are given permission by reachablity anyway, 
		// so if the user is allowed permission to the parent, no further checks are necessary.
		//
		Vector<VersionInfo> vInfos = getVersionableInfos(user,key,VersionableType.BioModelMetaData,true,true,false);
		if (vInfos.size()==0){
			throw new ObjectNotFoundException(VersionableType.BioModelMetaData.getTypeName()+" not found");
		}
		
		return driver.getVersionableXML(con,VersionableType.BioModelMetaData, key);
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getBioModelXML(user, key, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.DbDriver
 * @param vType int
 */
private DbDriver getDbDriver(VersionableType vType) {
	if (vType.equals(VersionableType.VCImage)){
		return geomDB;
	}else if (vType.equals(VersionableType.Geometry)){
		return geomDB;
	}else if (vType.equals(VersionableType.SimulationContext)){
		return simContextDB;
	}else if (vType.equals(VersionableType.Model)){
		return modelDB;
	}else if (vType.equals(VersionableType.MathDescription)){
		return mathDB;
	}else if (vType.equals(VersionableType.Simulation)){
		return simulationDB;
	}else if (vType.equals(VersionableType.BioModelMetaData)){
		return bioModelDB;
	}else if (vType.equals(VersionableType.MathModelMetaData)){
		return mathModelDB;
	}else{
		throw new IllegalArgumentException("DBTopLevel.getDbDriver(vType): Unknown vType");
	}
}

KeyValue[] getMathDescKeysForExternalData(KeyValue extDataKey, User owner,boolean bEnableRetry) throws SQLException,DataAccessException{
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return DbDriver.getMathDescKeysForExternalData(con, owner,extDataKey);
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getMathDescKeysForExternalData(extDataKey,owner, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
Geometry getGeometry(QueryHashtable dbc, User user, KeyValue key, boolean bCheckPermission) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
	return (Geometry)getVersionable(dbc, user, key, VersionableType.Geometry, bCheckPermission, true);
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
MathDescription getMathDescription(QueryHashtable dbc, User user, KeyValue key) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
	return (MathDescription)getVersionable(dbc, user, key, VersionableType.MathDescription, false, true);
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
MathModelMetaData getMathModelMetaData(QueryHashtable dbc, User user, KeyValue key) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
	return (MathModelMetaData)getVersionable(dbc, user, key, VersionableType.MathModelMetaData, true, true);
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
MathModelMetaData[] getMathModelMetaDatas(User user, boolean bAll, boolean bEnableRetry) 
				throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {

	//New
	//Versionable cacheObj = (Versionable) dbCacheTable.get(key);
	//if (cacheObj != null) {
	//	return cacheObj;
	//}
	//NewEnd

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		//New
		//Object dbV = driver.getVersionable(con, user, versionableType, key);
		//dbCacheTable.put((Cacheable) dbV);
		//return (Versionable) dbV;
		//NewEnd
		return mathModelDB.getMathModelMetaDatas(con,user,bAll);
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getMathModelMetaDatas(user, bAll, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
String getMathModelXML(User user, KeyValue key, boolean bEnableRetry) 
				throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {

	Object lock = new Object();
	VersionableType versionableType = VersionableType.MathModelMetaData;
	Connection con = conFactory.getConnection(lock);
	DbDriver driver = getDbDriver(versionableType);
	try {
		//
		// Getting the corresponding VersionInfo will fail if you don't have permission to the object.
		// This is needed because the DbDriver-level services can return objects directly from the
		// cache without checking for permissions first.
		//
		// This check is placed in DbTopLevel because this is the client API entry point.
		// Child objects (of the requested object) are given permission by reachablity anyway, 
		// so if the user is allowed permission to the parent, no further checks are necessary.
		//
		Vector<VersionInfo> vInfos = getVersionableInfos(user,key,versionableType,true,true,false);
		if (vInfos.size()==0){
			throw new ObjectNotFoundException(versionableType.getTypeName()+" not found");
		}
		
		return driver.getVersionableXML(con,versionableType, key);
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getMathModelXML(user, key, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
Model getModel(QueryHashtable dbc, User user, KeyValue key) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
	return (Model)getVersionable(dbc, user, key, VersionableType.Model, false, true);
}


/**
 * publish method comment.
 */
public Preference[] getPreferences(User user,boolean bEnableRetry) throws DataAccessException,SQLException {
	
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		Preference[] preferences = DbDriver.getPreferences(con,user);
		return preferences;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getPreferences(user,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	}finally{
		conFactory.release(con,lock);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 5:16:48 PM)
 */
Model getReactionStepAsModel(QueryHashtable dbc, User user,KeyValue reactionStepKey,boolean bEnableRetry) throws DataAccessException, java.sql.SQLException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return reactStepDB.getReactionStepAsModel(dbc, con,user,reactionStepKey);
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getReactionStepAsModel(dbc, user,reactionStepKey,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
Simulation getSimulation(QueryHashtable dbc, User user, KeyValue key) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
	return (Simulation)getVersionable(dbc, user, key, VersionableType.Simulation, false, true);
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
SimulationContext getSimulationContext(QueryHashtable dbc, User user, KeyValue key) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
	return (SimulationContext)getVersionable(dbc, user, key, VersionableType.SimulationContext, false, true);
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
TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS,User user,boolean bEnableRetry) 
				throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return DbDriver.testSuiteGet(getThisTS,con,user,conFactory.getDatabaseSyntax());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getTestSuite(getThisTS,user, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
TestSuiteInfoNew[] getTestSuiteInfos(User user,boolean bEnableRetry) 
				throws java.sql.SQLException,DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return DbDriver.testSuiteInfosGet(con,user);
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getTestSuiteInfos(user,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
VCImage getVCImage(QueryHashtable dbc, User user, KeyValue key, boolean bCheckPermission) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
	return (VCImage)getVersionable(dbc, user, key, VersionableType.VCImage, bCheckPermission, true);
}


/**
 * Insert the method's description here.
 * Creation date: (9/25/2003 7:57:54 AM)
 * @return cbit.vcell.modeldb.VCInfoContainer
 * @param user cbit.vcell.server.User
 */
TreeMap<User.SPECIALS,TreeMap<User,String>>  getSpecialUsers(User user,boolean bEnableRetry) throws DataAccessException, java.sql.SQLException{
	
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return DbDriver.getSpecialUsers(user,con,conFactory.getDatabaseSyntax());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getSpecialUsers(user,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	}finally{
		conFactory.release(con,lock);
	}
}

VCInfoContainer getVCInfoContainer(User user, boolean bEnableRetry) throws DataAccessException, java.sql.SQLException{
	
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return DbDriver.getVCInfoContainer(user,con,conFactory.getDatabaseSyntax());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getVCInfoContainer(user,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
private Versionable getVersionable(QueryHashtable dbc, User user, KeyValue key, VersionableType versionableType, boolean bCheckPermission, boolean bEnableRetry) 
				throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		//
		// Getting the corresponding VersionInfo will fail if you don't have permission to the object.
		// This is needed because the DbDriver-level services can return objects directly from the
		// cache without checking for permissions first.
		//
		// This check is placed in DbTopLevel because this is the client API entry point.
		// Child objects (of the requested object) are given permission by reachablity anyway, 
		// so if the user is allowed permission to the parent, no further checks are necessary.
		//
		//Vector vInfos = getVersionableInfo(user,key,versionableType,true,true);
		//if (vInfos.size()==0){
			//throw new ObjectNotFoundException(versionableType.getTypeName()+" not found");
		//}
		if (versionableType.equals(VersionableType.BioModelMetaData)) {
			return bioModelDB.getVersionable(dbc, con, user, versionableType, key);
		} else if (versionableType.equals(VersionableType.Geometry) || versionableType.equals(VersionableType.VCImage)) {
			return geomDB.getVersionable(dbc, con, user, versionableType, key, bCheckPermission);
		} else if (versionableType.equals(VersionableType.MathModelMetaData)) {
			return mathModelDB.getVersionable(dbc, con, user, versionableType, key);
		} else if (versionableType.equals(VersionableType.Simulation)) {
			return simulationDB.getVersionable(dbc, con, user, versionableType, key);
		} else if (versionableType.equals(VersionableType.SimulationContext)) {
			return simContextDB.getVersionable(dbc, con, user, versionableType, key);
		} else if (versionableType.equals(VersionableType.Model)) {
			return modelDB.getVersionable(dbc, con, user, versionableType, key);
		} else if (versionableType.equals(VersionableType.MathDescription)) {
			return mathDB.getVersionable(dbc, con, user, versionableType, key);
		} else {
			throw new IllegalArgumentException("Wrong VersinableType vType:" + versionableType);
		}				
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getVersionable(dbc, user, key, versionableType, bCheckPermission, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
Vector<VersionInfo> getVersionableInfos(User user, KeyValue key, VersionableType versionableType, boolean bAll, boolean bCheckPermission, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
				
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return DbDriver.getVersionableInfos(con,user,versionableType, bAll, key, bCheckPermission,conFactory.getDatabaseSyntax());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getVersionableInfos(user,key,versionableType,bAll,bCheckPermission, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
void groupAddUser(User user, VersionableType versionableType, KeyValue key, boolean bEnableRetry,String userAddToGroup,boolean isHidden) 
		throws java.sql.SQLException, ObjectNotFoundException, DataAccessException, DependencyException  {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		DbDriver.groupAddUser(con, conFactory.getKeyFactory(), user, versionableType, key, userAddToGroup,isHidden,conFactory.getDatabaseSyntax());
		con.commit();
		return;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			groupAddUser(user,versionableType,key,false,userAddToGroup,isHidden);
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
void groupRemoveUser(User user, VersionableType versionableType, KeyValue key, boolean bEnableRetry,String userRemoveFromGroup,boolean isHiddenFromOwner) 
		throws java.sql.SQLException, ObjectNotFoundException, DataAccessException, DependencyException  {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		DbDriver.groupRemoveUser(con, conFactory.getKeyFactory(), user, versionableType, key,userRemoveFromGroup,isHiddenFromOwner,conFactory.getDatabaseSyntax());
		con.commit();
		return;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			groupRemoveUser(user,versionableType,key,false,userRemoveFromGroup,isHiddenFromOwner);
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
void groupSetPrivate(User user, VersionableType versionableType, KeyValue key, boolean bEnableRetry) 
		throws java.sql.SQLException, ObjectNotFoundException, DataAccessException  {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		DbDriver.groupSetPrivate(con, user, versionableType, key, conFactory.getDatabaseSyntax());
		con.commit();
		return;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			groupSetPrivate(user,versionableType,key,false);
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
void groupSetPublic(User user, VersionableType versionableType, KeyValue key, boolean bEnableRetry) 
		throws java.sql.SQLException, ObjectNotFoundException, DataAccessException  {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		DbDriver.groupSetPublic(con, user, versionableType, key, conFactory.getDatabaseSyntax());
		con.commit();
		return;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			groupSetPublic(user,versionableType,key,false);
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
KeyValue insertVersionable(User user, VCImage vcImage, String name, boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = geomDB.insertVersionable(new InsertHashtable(),con,user,vcImage,name,bVersion);
		con.commit();
		return versionKey;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return insertVersionable(user,vcImage,name,bVersion,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
KeyValue insertVersionable(User user, BioModelMetaData bioModelMetaData,BioModelChildSummary bmcs, String name, boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = bioModelDB.insertVersionable(new InsertHashtable(),con,user,bioModelMetaData,bmcs,name,bVersion);
		con.commit();
		return versionKey;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return insertVersionable(user,bioModelMetaData,bmcs,name,bVersion,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
KeyValue insertVersionable(QueryHashtable dbc, User user, Geometry geometry, KeyValue updatedImageKey, String name, boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = geomDB.insertVersionable(new InsertHashtable(), dbc, con,user,geometry,updatedImageKey,name,bVersion);
		con.commit();
		return versionKey;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return insertVersionable(dbc, user,geometry,updatedImageKey,name,bVersion,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
KeyValue insertVersionable(User user, SimulationContext simulationContext, KeyValue updatedMathDescriptionKey, Model updatedModel, KeyValue updatedGeometryKey, String name, boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = simContextDB.insertVersionable(new InsertHashtable(),con,user,simulationContext,updatedMathDescriptionKey,updatedModel,updatedGeometryKey,name,bVersion);
		con.commit();
		return versionKey;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return insertVersionable(user,simulationContext,updatedMathDescriptionKey,updatedModel,updatedGeometryKey,name,bVersion,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
KeyValue insertVersionable(User user, MathDescription mathDescription, KeyValue updatedGeometryKey, String name, boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = mathDB.insertVersionable(new InsertHashtable(),con,user,mathDescription,updatedGeometryKey,name,bVersion);
		con.commit();
		return versionKey;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return insertVersionable(user,mathDescription,updatedGeometryKey,name,bVersion,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
KeyValue insertVersionable(User user, MathModelMetaData mathModelMetaData, MathModelChildSummary mmcs,String name, boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = mathModelDB.insertVersionable(new InsertHashtable(),con,user,mathModelMetaData,mmcs,name,bVersion);
		con.commit();
		return versionKey;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return insertVersionable(user,mathModelMetaData,mmcs,name,bVersion,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
KeyValue insertVersionable(User user, Model model, String name, boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = modelDB.insertVersionable(new InsertHashtable(),con,user,model,name,bVersion);
		con.commit();
		return versionKey;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return insertVersionable(user,model,name,bVersion,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
KeyValue insertVersionable(User user, Simulation simulation, KeyValue updatedMathDescriptionKey, String name, boolean bVersion, boolean bMathematicallyEquivalent, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = simulationDB.insertVersionable(new InsertHashtable(),con,user,simulation,updatedMathDescriptionKey,name,bVersion,bMathematicallyEquivalent);
		con.commit();
		return versionKey;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return insertVersionable(user,simulation,updatedMathDescriptionKey,name,bVersion,bMathematicallyEquivalent,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
void insertVersionableChildSummary(User user,VersionableType vType,KeyValue vKey,String serialDBChildSummary,boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		DbDriver.insertVersionableChildSummary(con,serialDBChildSummary,vType,vKey,conFactory.getDatabaseSyntax());
		con.commit();
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			insertVersionableChildSummary(user,vType,vKey,serialDBChildSummary,false);
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
void insertVersionableXML(User user,VersionableType vType,KeyValue vKey,String xml,boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		DbDriver.insertVersionableXML(con,xml,vType,vKey,conFactory.getKeyFactory(),conFactory.getDatabaseSyntax());
		con.commit();
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			insertVersionableXML(user,vType,vKey,xml,false);
		}else{
			handle_DataAccessException_SQLException(e);
		}
	}finally{
		conFactory.release(con,lock);
	}
}


/**
 *
 * Test if there are any records of type 'vType' that use the name 'vName' owned by 'owner'
 *
 * @return boolean
 * @param vType cbit.sql.VersionableType
 * @param owner cbit.vcell.server.User
 * @param vName java.lang.String
 */
boolean isNameUsed(User owner, VersionableType vType, String vName, boolean bEnableRetry) throws SQLException, DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return DbDriver.isNameUsed(con,vType,owner,vName);
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return isNameUsed(owner,vType,vName,false);
		}else{
			handle_DataAccessException_SQLException(e);
			throw new RuntimeException("DBTopLevel.isNameUsed(): unexpected SQL exception: "+e.getMessage());
		}
	}finally{
		conFactory.release(con,lock);
	}
}


/**
 * publish method comment.
 */
public void replacePreferences(User user,Preference[] preferences,boolean bEnableRetry) throws DataAccessException,java.sql.SQLException {
	
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		DbDriver.replacePreferences(con,user,preferences);
		con.commit();
		return;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			replacePreferences(user,preferences,false);
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
KeyValue updateVersionable(User user, VCImage vcImage, boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException{
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = geomDB.updateVersionable(new InsertHashtable(),con,user,vcImage,bVersion);
		con.commit();
		return versionKey;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return updateVersionable(user,vcImage,bVersion,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
KeyValue updateVersionable(User user, BioModelMetaData bioModelMetaData, BioModelChildSummary bmcs,boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException{
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = bioModelDB.updateVersionable(new InsertHashtable(),con,user,bioModelMetaData,bmcs,bVersion);
		con.commit();
		return versionKey;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return updateVersionable(user,bioModelMetaData,bmcs,bVersion,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
KeyValue updateVersionable(QueryHashtable dbc, User user, Geometry geometry, KeyValue updatedImageKey, boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException{
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = geomDB.updateVersionable(new InsertHashtable(), dbc, con,user,geometry,updatedImageKey,bVersion);
		con.commit();
		return versionKey;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return updateVersionable(dbc, user,geometry,updatedImageKey,bVersion,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
KeyValue updateVersionable(User user, SimulationContext simulationContext, KeyValue updatedMathDescriptionKey, Model updatedModel, KeyValue updatedGeometryKey, boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException{
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = simContextDB.updateVersionable(new InsertHashtable(),con,user,simulationContext,updatedMathDescriptionKey,updatedModel,updatedGeometryKey,bVersion);
		con.commit();
		return versionKey;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return updateVersionable(user,simulationContext,updatedMathDescriptionKey,updatedModel,updatedGeometryKey,bVersion,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
KeyValue updateVersionable(User user, MathDescription mathDescription, KeyValue updatedGeometryKey, boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException{
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = mathDB.updateVersionable(new InsertHashtable(),con,user,mathDescription,updatedGeometryKey,bVersion);
		con.commit();
		return versionKey;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return updateVersionable(user,mathDescription,updatedGeometryKey,bVersion,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
KeyValue updateVersionable(User user, MathModelMetaData mathModelMetaData, MathModelChildSummary mmcs,boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException{
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = mathModelDB.updateVersionable(new InsertHashtable(),con,user,mathModelMetaData,mmcs,bVersion);
		con.commit();
		return versionKey;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return updateVersionable(user,mathModelMetaData,mmcs,bVersion,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
KeyValue updateVersionable(User user, Model model, boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException{
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = modelDB.updateVersionable(new InsertHashtable(),con,user,model,bVersion);
		con.commit();
		return versionKey;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return updateVersionable(user,model,bVersion,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
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
KeyValue updateVersionable(User user, Simulation simulation, KeyValue updatedMathDescriptionKey, boolean bVersion, boolean bMathematicallyEquivalent, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException{
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = simulationDB.updateVersionable(new InsertHashtable(),con,user,simulation,updatedMathDescriptionKey,bVersion,bMathematicallyEquivalent);
		con.commit();
		return versionKey;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return updateVersionable(user,simulation,updatedMathDescriptionKey,bVersion,bMathematicallyEquivalent,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	}finally{
		conFactory.release(con,lock);
	}
}


public BioModelRep[] getBioModelReps(User user, String conditions, OrderBy orderBy, int startRow, int numRows, boolean bEnableRetry) throws SQLException, DataAccessException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		BioModelRep[] biomodelreps = bioModelDB.getBioModelReps(con,user,conditions, orderBy, startRow, numRows);
		return biomodelreps;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getBioModelReps(user, conditions, orderBy, startRow, numRows, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	}finally{
		conFactory.release(con,lock);
	}
}

public KeyValue savePublicationRep(PublicationRep publicationRep, User user, boolean bEnableRetry) throws SQLException, DataAccessException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue publicationKey = DbDriver.savePublicationRep(con,publicationRep,user,conFactory.getDatabaseSyntax());
		con.commit();
		return publicationKey;
	}catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return savePublicationRep(publicationRep,user, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null;
		}
	}finally{
		conFactory.release(con,lock);
	}
}
	
public PublicationRep[] getPublicationReps(User user, String conditions, OrderBy orderBy, boolean bEnableRetry) throws SQLException, DataAccessException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		PublicationRep[] publicationreps = bioModelDB.getPublicationReps(con,user,conditions, orderBy);
		return publicationreps;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getPublicationReps(user, conditions, orderBy, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	}finally{
		conFactory.release(con,lock);
	}
}


public SimContextRep[] getSimContextReps(KeyValue startingSimContextKey, int numRows, boolean bEnableRetry) throws SQLException, DataAccessException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		SimContextRep[] simContextReps = simContextDB.getSimContextReps(con,startingSimContextKey, numRows);
		return simContextReps;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getSimContextReps(startingSimContextKey, numRows, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	}finally{
		conFactory.release(con,lock);
	}
}

public SimContextRep getSimContextRep(KeyValue simContextKey, boolean bEnableRetry) throws SQLException, DataAccessException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		SimContextRep simContextRep = simContextDB.getSimContextRep(con,simContextKey);
		return simContextRep;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getSimContextRep(simContextKey, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	}finally{
		conFactory.release(con,lock);
	}
}

public SimulationRep[] getSimulationReps(KeyValue startingSimKey, int numRows, boolean bEnableRetry) throws SQLException, DataAccessException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		SimulationRep[] simulationReps = simulationDB.getSimulationReps(con,startingSimKey, numRows);
		return simulationReps;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getSimulationReps(startingSimKey, numRows, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	}finally{
		conFactory.release(con,lock);
	}
}

public SimulationRep getSimulationRep(KeyValue simKey, boolean bEnableRetry) throws SQLException, DataAccessException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		SimulationRep simulationRep = simulationDB.getSimulationRep(con,simKey);
		return simulationRep;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			lg.error("exception during rollback, bEnableRetry = "+bEnableRetry, rbe);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getSimulationRep(simKey, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	}finally{
		conFactory.release(con,lock);
	}
}
}

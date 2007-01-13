package cbit.vcell.modeldb;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import cbit.image.VCImage;
import cbit.sql.ConnectionFactory;
import cbit.sql.DBCacheTable;
import cbit.sql.InsertHashtable;
import cbit.sql.RecordChangedException;
import cbit.util.DataAccessException;
import cbit.util.ObjectNotFoundException;
import cbit.util.PermissionException;
import cbit.util.ReferenceQueryResult;
import cbit.util.ReferenceQuerySpec;
import cbit.util.SessionLog;
import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.model.Model;
import cbit.vcell.modelapp.SimulationContext;
import cbit.util.DependencyException;
import cbit.util.document.CurateSpec;
import cbit.util.document.FieldDataIdentifierSpec;
import cbit.util.document.KeyValue;
import cbit.util.document.User;
import cbit.util.document.Versionable;
import cbit.util.document.VersionableFamily;
import cbit.util.document.VersionableType;
import cbit.util.document.VersionableTypeVersion;
import cbit.vcell.simulation.Simulation;
/**
 * This type was created in VisualAge.
 */
public class DBTopLevel extends AbstractDBTopLevel{
	private GeomDbDriver geomDB = null;
	private MathDescriptionDbDriver mathDB = null;
	private ModelDbDriver modelDB = null;
	private ReactStepDbDriver reactStepDB = null;
	private SimulationDbDriver simulationDB = null;
	private SimulationContextDbDriver simContextDB = null;
	private BioModelDbDriver bioModelDB = null;
	private MathModelDbDriver mathModelDB = null;
	private UserDbDriver userDB = null;
	private DBCacheTable dbCacheTable = null;

	private static final int SQL_ERROR_CODE_BADCONNECTION = 1010; //??????????????????????????????????????

/**
 * DBTopLevel constructor comment.
 */
public DBTopLevel(ConnectionFactory aConFactory, SessionLog newLog, DBCacheTable argDBCacheTable) throws SQLException{
	super(aConFactory,newLog);
	this.dbCacheTable = argDBCacheTable;
	this.geomDB = new GeomDbDriver(this.dbCacheTable,log);
	this.mathDB = new MathDescriptionDbDriver(this.dbCacheTable,this.geomDB,log);
	this.reactStepDB = new ReactStepDbDriver(this.dbCacheTable,null,log,new DictionaryDbDriver(newLog,argDBCacheTable));
	this.modelDB = new ModelDbDriver(this.dbCacheTable,this.reactStepDB,log);
	this.reactStepDB.init(modelDB);
	this.simulationDB = new SimulationDbDriver(this.dbCacheTable,this.mathDB,log);
	this.simContextDB = new SimulationContextDbDriver(this.dbCacheTable,this.geomDB,this.modelDB,this.mathDB,this.reactStepDB,log);
	this.userDB = new UserDbDriver(log); 
	this.bioModelDB = new BioModelDbDriver(this.dbCacheTable,simulationDB,simContextDB,modelDB,log);
	this.mathModelDB = new MathModelDbDriver(this.dbCacheTable,simulationDB,mathDB,log);
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:16:41 AM)
 * @return cbit.vcell.modeldb.ReferenceQueryResult
 * @param user cbit.vcell.server.User
 * @param rqs cbit.vcell.modeldb.ReferenceQuerySpec
 */
public cbit.util.document.VCDocumentInfo curate(User user,CurateSpec curateSpec) throws DataAccessException,java.sql.SQLException{
	return curate0(user,curateSpec,true);
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:16:41 AM)
 * @return cbit.vcell.modeldb.ReferenceQueryResult
 * @param user cbit.vcell.server.User
 * @param rqs cbit.vcell.modeldb.ReferenceQuerySpec
 */
private cbit.util.document.VCDocumentInfo curate0(User user,CurateSpec curateSpec,boolean bEnableRetry) throws DataAccessException,java.sql.SQLException{

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		cbit.util.document.VCDocumentInfo newVCDocumentInfo = DbDriver.curate(curateSpec,con,user,dbCacheTable);
		con.commit();
		return newVCDocumentInfo;
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
			return curate0(user,curateSpec,false);
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
 * @exception cbit.util.DataAccessException The exception description.
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
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
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


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
cbit.vcell.numericstest.TestSuiteOPResults doTestSuiteOP(cbit.vcell.numericstest.TestSuiteOP tsop,User user,boolean bEnableRetry) 
				throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		cbit.vcell.numericstest.TestSuiteOPResults tsor  = DbDriver.testSuiteOP(tsop,con,user,log);
		con.commit();
		return tsor;
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
public ReferenceQueryResult findReferences(User user, ReferenceQuerySpec rqs) throws DataAccessException,java.sql.SQLException{

	VersionableFamily vf = getAllReferences(rqs.getKeyValue(),rqs.getVersionableType(),true);
	//Check permission
	if(vf.bDependants()){
		VersionableTypeVersion[] vtvArr = vf.getUniqueDependants();
		for(int i=0;i<vtvArr.length;i+= 1){
			if(vtvArr[i].getVType().equals(VersionableType.BioModelMetaData)){
				Vector checkedVInfos = getVersionableInfos(user,vtvArr[i].getVersion().getVersionKey(),VersionableType.BioModelMetaData,false,true,true);
				if(checkedVInfos == null || checkedVInfos.size() == 0){throw new DataAccessException("References Not Accessible");}
			}else if(vtvArr[i].getVType().equals(VersionableType.MathModelMetaData)){
				Vector checkedVInfos = getVersionableInfos(user,vtvArr[i].getVersion().getVersionKey(),VersionableType.MathModelMetaData,false,true,true);
				if(checkedVInfos == null || checkedVInfos.size() == 0){throw new DataAccessException("References Not Accessible");}
			}
		}
	}
	
	return new ReferenceQueryResult(vf);

}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
VersionableFamily getAllReferences(KeyValue key, VersionableType versionableType, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
				
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return DbDriver.getAllReferences(con,versionableType, key);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getAllReferences(key,versionableType,false);
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
BioModelMetaData getBioModelMetaData(User user, KeyValue key) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
	return (BioModelMetaData)getVersionable(user, key, VersionableType.BioModelMetaData, true, true);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
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
		return bioModelDB.getBioModelMetaDatas(con,user,bAll);
	} catch (Throwable e) {
		log.exception(e);
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
 * @exception cbit.util.DataAccessException The exception description.
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
		Vector vInfos = getVersionableInfos(user,key,VersionableType.BioModelMetaData,false,true,false);
		if (vInfos.size()==0){
			throw new ObjectNotFoundException(VersionableType.BioModelMetaData.getTypeName()+" not found");
		}
		
		return driver.getVersionableXML(con,VersionableType.BioModelMetaData, key);
	} catch (Throwable e) {
		log.exception(e);
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


/**
 * Insert the method's description here.
 * Creation date: (9/25/2003 7:57:54 AM)
 * @return cbit.vcell.modeldb.VCInfoContainer
 * @param user cbit.vcell.server.User
 */
cbit.vcell.simdata.FieldDataIdentifier[] getFieldDataIdentifiers(User user, FieldDataIdentifierSpec[] fieldDataIDSpecs, boolean bEnableRetry) throws SQLException, DataAccessException {
	
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return DbDriver.getFieldDataIdentifiers(con, user,fieldDataIDSpecs);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getFieldDataIdentifiers(user,fieldDataIDSpecs, false);
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
Geometry getGeometry(User user, KeyValue key, boolean bCheckPermission) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
	return (Geometry)getVersionable(user, key, VersionableType.Geometry, bCheckPermission, true);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
MathDescription getMathDescription(User user, KeyValue key) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
	return (MathDescription)getVersionable(user, key, VersionableType.MathDescription, false, true);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
MathModelMetaData getMathModelMetaData(User user, KeyValue key) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
	return (MathModelMetaData)getVersionable(user, key, VersionableType.MathModelMetaData, true, true);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
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
		log.exception(e);
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
 * @exception cbit.util.DataAccessException The exception description.
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
		Vector vInfos = getVersionableInfos(user,key,versionableType,false,true,false);
		if (vInfos.size()==0){
			throw new ObjectNotFoundException(versionableType.getTypeName()+" not found");
		}
		
		return driver.getVersionableXML(con,versionableType, key);
	} catch (Throwable e) {
		log.exception(e);
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
Model getModel(User user, KeyValue key) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
	return (Model)getVersionable(user, key, VersionableType.Model, false, true);
}


/**
 * publish method comment.
 */
public cbit.util.Preference[] getPreferences(User user,boolean bEnableRetry) throws DataAccessException,SQLException {
	
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		cbit.util.Preference[] preferences = DbDriver.getPreferences(con,user);
		return preferences;
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
cbit.vcell.model.ReactionStep getReactionStep(User user,cbit.util.document.KeyValue reactionStepKey,boolean bEnableRetry) throws DataAccessException, java.sql.SQLException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return reactStepDB.getReactionStep(con,user,reactionStepKey);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getReactionStep(user,reactionStepKey,false);
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
Simulation getSimulation(User user, KeyValue key) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
	return (Simulation)getVersionable(user, key, VersionableType.Simulation, false, true);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
SimulationContext getSimulationContext(User user, KeyValue key) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
	return (SimulationContext)getVersionable(user, key, VersionableType.SimulationContext, false, true);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
cbit.vcell.numericstest.TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS,User user,boolean bEnableRetry) 
				throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return DbDriver.testSuiteGet(getThisTS,con,user,log);
	} catch (Throwable e) {
		log.exception(e);
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
cbit.vcell.numericstest.TestSuiteInfoNew[] getTestSuiteInfos(User user,boolean bEnableRetry) 
				throws java.sql.SQLException,DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return DbDriver.testSuiteInfosGet(con,user,log);
	} catch (Throwable e) {
		log.exception(e);
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
VCImage getVCImage(User user, KeyValue key, boolean bCheckPermission) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
	return (VCImage)getVersionable(user, key, VersionableType.VCImage, bCheckPermission, true);
}


/**
 * Insert the method's description here.
 * Creation date: (9/25/2003 7:57:54 AM)
 * @return cbit.vcell.modeldb.VCInfoContainer
 * @param user cbit.vcell.server.User
 */
VCInfoContainer getVCInfoContainer(User user, boolean bEnableRetry) throws DataAccessException, java.sql.SQLException{
	
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return DbDriver.getVCInfoContainer(user,con,log);
	} catch (Throwable e) {
		log.exception(e);
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
private Versionable getVersionable(User user, KeyValue key, VersionableType versionableType, boolean bCheckPermission, boolean bEnableRetry) 
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
			return bioModelDB.getVersionable(con, user, versionableType, key);
		} else if (versionableType.equals(VersionableType.Geometry) || versionableType.equals(VersionableType.VCImage)) {
			return geomDB.getVersionable(con, user, versionableType, key, bCheckPermission);
		} else if (versionableType.equals(VersionableType.MathModelMetaData)) {
			return mathModelDB.getVersionable(con, user, versionableType, key);
		} else if (versionableType.equals(VersionableType.Simulation)) {
			return simulationDB.getVersionable(con, user, versionableType, key);
		} else if (versionableType.equals(VersionableType.SimulationContext)) {
			return simContextDB.getVersionable(con, user, versionableType, key);
		} else if (versionableType.equals(VersionableType.Model)) {
			return modelDB.getVersionable(con, user, versionableType, key);
		} else if (versionableType.equals(VersionableType.MathDescription)) {
			return mathDB.getVersionable(con, user, versionableType, key);
		} else {
			throw new IllegalArgumentException("Wrong VersinableType vType:" + versionableType);
		}				
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getVersionable(user, key, versionableType, bCheckPermission, false);
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
public Vector getVersionableInfos(User user, KeyValue key, VersionableType versionableType, boolean bAll, boolean bCheckPermission, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
				
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return DbDriver.getVersionableInfos(con,log,user,versionableType, bAll, key, bCheckPermission);
	} catch (Throwable e) {
		log.exception(e);
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
void groupAddUser(User user, VersionableType versionableType, KeyValue key, boolean bEnableRetry,String userAddToGroup,boolean isHidden) 
		throws java.sql.SQLException, ObjectNotFoundException, DataAccessException, DependencyException  {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		DbDriver.groupAddUser(con, log, user, versionableType, key,dbCacheTable,userAddToGroup,isHidden);
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
void groupRemoveUser(User user, VersionableType versionableType, KeyValue key, boolean bEnableRetry,String userRemoveFromGroup,boolean isHiddenFromOwner) 
		throws java.sql.SQLException, ObjectNotFoundException, DataAccessException, DependencyException  {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		DbDriver.groupRemoveUser(con, log, user, versionableType, key,dbCacheTable,userRemoveFromGroup,isHiddenFromOwner);
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
void groupSetPrivate(User user, VersionableType versionableType, KeyValue key, boolean bEnableRetry) 
		throws java.sql.SQLException, ObjectNotFoundException, DataAccessException  {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		DbDriver.groupSetPrivate(con, log, user, versionableType, key,dbCacheTable);
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
void groupSetPublic(User user, VersionableType versionableType, KeyValue key, boolean bEnableRetry) 
		throws java.sql.SQLException, ObjectNotFoundException, DataAccessException  {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		DbDriver.groupSetPublic(con, log, user, versionableType, key,dbCacheTable);
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
 * @exception cbit.util.DataAccessException The exception description.
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
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
KeyValue insertVersionable(User user, BioModelMetaData bioModelMetaData,cbit.util.document.BioModelChildSummary bmcs, String name, boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = bioModelDB.insertVersionable(new InsertHashtable(),con,user,bioModelMetaData,bmcs,name,bVersion);
		con.commit();
		return versionKey;
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
KeyValue insertVersionable(User user, Geometry geometry, KeyValue updatedImageKey, String name, boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = geomDB.insertVersionable(new InsertHashtable(),con,user,geometry,updatedImageKey,name,bVersion);
		con.commit();
		return versionKey;
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
			return insertVersionable(user,geometry,updatedImageKey,name,bVersion,false);
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
 * @exception cbit.util.DataAccessException The exception description.
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
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
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
 * @exception cbit.util.DataAccessException The exception description.
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
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
KeyValue insertVersionable(User user, MathModelMetaData mathModelMetaData, cbit.util.document.MathModelChildSummary mmcs,String name, boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = mathModelDB.insertVersionable(new InsertHashtable(),con,user,mathModelMetaData,mmcs,name,bVersion);
		con.commit();
		return versionKey;
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
 * @exception cbit.util.DataAccessException The exception description.
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
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
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
 * @exception cbit.util.DataAccessException The exception description.
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
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
void insertVersionableChildSummary(User user,VersionableType vType,KeyValue vKey,String serialDBChildSummary,boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	DbDriver driver = getDbDriver(vType);
	try {
		driver.insertVersionableChildSummary(con,serialDBChildSummary,vType,vKey);
		con.commit();
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
void insertVersionableXML(User user,VersionableType vType,KeyValue vKey,String xml,boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException {
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	DbDriver driver = getDbDriver(vType);
	try {
		driver.insertVersionableXML(con,xml,vType,vKey);
		con.commit();
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
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
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
public void replacePreferences(User user,cbit.util.Preference[] preferences,boolean bEnableRetry) throws DataAccessException,java.sql.SQLException {
	
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		DbDriver.replacePreferences(con,user,preferences);
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
 * @exception cbit.util.DataAccessException The exception description.
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
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
KeyValue updateVersionable(User user, BioModelMetaData bioModelMetaData, cbit.util.document.BioModelChildSummary bmcs,boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException{
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = bioModelDB.updateVersionable(new InsertHashtable(),con,user,bioModelMetaData,bmcs,bVersion);
		con.commit();
		return versionKey;
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
KeyValue updateVersionable(User user, Geometry geometry, KeyValue updatedImageKey, boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException{
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = geomDB.updateVersionable(new InsertHashtable(),con,user,geometry,updatedImageKey,bVersion);
		con.commit();
		return versionKey;
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
			return updateVersionable(user,geometry,updatedImageKey,bVersion,false);
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
 * @exception cbit.util.DataAccessException The exception description.
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
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
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
 * @exception cbit.util.DataAccessException The exception description.
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
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
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
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
KeyValue updateVersionable(User user, MathModelMetaData mathModelMetaData, cbit.util.document.MathModelChildSummary mmcs,boolean bVersion, boolean bEnableRetry) 
		throws DataAccessException, java.sql.SQLException, RecordChangedException{
			
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue versionKey = mathModelDB.updateVersionable(new InsertHashtable(),con,user,mathModelMetaData,mmcs,bVersion);
		con.commit();
		return versionKey;
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
 * @exception cbit.util.DataAccessException The exception description.
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
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
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
 * @exception cbit.util.DataAccessException The exception description.
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
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
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
}
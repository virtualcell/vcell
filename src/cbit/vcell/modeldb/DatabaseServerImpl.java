package cbit.vcell.modeldb;
import cbit.vcell.solver.ode.gui.SimulationStatus;
import java.util.Vector;
import cbit.image.VCImage;
import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.export.server.ExportLog;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.mathmodel.*;
import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.solver.SolverResultSetInfo;
import java.sql.*;

import org.vcell.util.BigString;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.sql.*;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.server.ObjectNotFoundException;
import cbit.vcell.server.UserRegistrationOP;
import cbit.vcell.server.UserRegistrationResults;
import cbit.vcell.dictionary.DBSpecies;
import cbit.vcell.dictionary.DBFormalSpecies;
import cbit.vcell.dictionary.FormalSpeciesType;
/**
 * This type was created in VisualAge.
 */
public class DatabaseServerImpl {
	private DBTopLevel dbTop = null;
	private ResultSetDBTopLevel rsetDbTop = null;
	private DictionaryDBTopLevel dictionaryTop = null;
	private AdminDBTopLevel adminDbTop = null;
	private cbit.vcell.server.SessionLog log = null;
	private ServerDocumentManager serverDocumentManager = new ServerDocumentManager(this);

/**
 * This method was created in VisualAge.
 */
public DatabaseServerImpl(ConnectionFactory conFactory, KeyFactory keyFactory, DBCacheTable dbCacheTable, cbit.vcell.server.SessionLog sessionLog) 
						throws DataAccessException {
	super();
	this.log = sessionLog;
	DbDriver.setKeyFactory(keyFactory);
	try {
		dbTop = new DBTopLevel(conFactory,log,dbCacheTable);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException("Error creating DBTopLevel " + e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException("Error creating DBTopLevel " + e.getMessage());
	}		
	try {
		rsetDbTop = new ResultSetDBTopLevel(conFactory,log,dbCacheTable);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException("Error creating ResultSetDBTopLevel " + e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException("Error creating ResultSetDBTopLevel " + e.getMessage());
	}		
	try {
		dictionaryTop = new DictionaryDBTopLevel(conFactory,log,dbCacheTable);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException("Error creating DictionaryDbTopLevel " + e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException("Error creating DictionaryDbTopLevel " + e.getMessage());
	}		
	try {
		adminDbTop = new AdminDBTopLevel(conFactory,log);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException("Error creating AdminDbTopLevel " + e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException("Error creating AdminDbTopLevel " + e.getMessage());
	}		
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:04:45 AM)
 */
public cbit.vcell.document.VCDocumentInfo curate(User user,cbit.vcell.server.CurateSpec curateSpec) throws DataAccessException{
	
	try {
		log.print("DatabaseServerImpl.curate(user="+user+" curateSpec="+curateSpec+")");
		return dbTop.curate(user,curateSpec);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
	catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} 
	catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
	
}


/**
 * delete method comment.
 */
void delete(User user, cbit.sql.VersionableType vType, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.delete(vType="+vType.getTypeName()+", Key="+key+")");
		dbTop.deleteVersionable(user, vType, key, true);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}

public UserRegistrationResults userRegistrationOP(User user, UserRegistrationOP userRegistrationOP) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.userRegistrationOP(...)");
		return dbTop.userRegistrationOP(user,userRegistrationOP,true);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * delete method comment.
 */
public void deleteBioModel(User user, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	delete(user,VersionableType.BioModelMetaData, key);
}


/**
 * delete method comment.
 */
public FieldDataDBOperationResults fieldDataDBOperation(User user, FieldDataDBOperationSpec fieldDataDBOperationSpec) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.fieldDataDBOperation opType="+fieldDataDBOperationSpec.opType);
		return dbTop.fieldDataDBOperation(user, fieldDataDBOperationSpec, true);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * delete method comment.
 */
public void deleteGeometry(User user, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	delete(user,VersionableType.Geometry, key);
}


/**
 * delete method comment.
 */
public void deleteMathModel(User user, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	delete(user,VersionableType.MathModelMetaData, key);
}


/**
 * delete method comment.
 */
public void deleteResultSetExport(User user, org.vcell.util.document.KeyValue eleKey) throws DataAccessException{
	try {
		log.print("DatabaseServerImpl.deleteResultSetExport(Key="+eleKey+")");
		rsetDbTop.deleteResultSetExport(user, eleKey, true);
	}catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * delete method comment.
 */
public void deleteVCImage(User user, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	delete(user,VersionableType.VCImage, key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteOPResults doTestSuiteOP(User user, cbit.vcell.numericstest.TestSuiteOP tsop)
	throws DataAccessException {
		
	try {
		log.print("DatabaseServerImpl.doTestSuiteOP(op="+tsop+")");
	return dbTop.doTestSuiteOP(tsop,user,true);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:04:45 AM)
 */
public ReferenceQueryResult findReferences(User user, ReferenceQuerySpec rqs) throws DataAccessException{
	
	try {
		log.print("DatabaseServerImpl.findReferences(user="+user+" refQuerySpec="+rqs+")");
		return dbTop.findReferences(user,rqs,true);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
	//catch (ObjectNotFoundException e) {
		//log.exception(e);
		//throw new ObjectNotFoundException(e.getMessage());
	//} 
	catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
	
}


/**
 * getVersionable method comment.
 */
public cbit.vcell.modeldb.VersionableFamily getAllReferences(User user, cbit.sql.VersionableType vType, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.getAllReferences(vType="+vType.getTypeName()+", Key="+key+")");
		log.alert("DatabaseServerImpl.getAllReferences() can return 'version' objects that aren't viewable to user !!!!!!!!!!!!!!!! ");
		return dbTop.getAllReferences(user,key,vType,true);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelInfo getBioModelInfo(User user, org.vcell.util.document.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return ((cbit.vcell.biomodel.BioModelInfo[])getVersionInfos(user, key, VersionableType.BioModelMetaData, true, true))[0];
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelInfo[] getBioModelInfos(User user, boolean bAll) throws cbit.vcell.server.DataAccessException {
	return (cbit.vcell.biomodel.BioModelInfo[])getVersionInfos(user, null, VersionableType.BioModelMetaData, bAll, true);
}


/**
 * getVersionable method comment.
 */
public BioModelMetaData getBioModelMetaData(User user, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.getBioModelMetaData(key="+key+")");
		BioModelMetaData bioModelMetaData = dbTop.getBioModelMetaData(user,key);
		return bioModelMetaData;
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 */
public BioModelMetaData[] getBioModelMetaDatas(User user, boolean bAll) throws DataAccessException {
	try {
		log.print("DatabaseServerImpl.getBioModelMetaDatas(bAll="+bAll+")");
		BioModelMetaData bioModelMetaDataArray[] = dbTop.getBioModelMetaDatas(user,bAll,true);
		return bioModelMetaDataArray;
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionable method comment.
 */
public BigString getBioModelXML(User user, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.getBioModelXML(user="+user+", Key="+key+")");
		return new BigString(serverDocumentManager.getBioModelXML(user, key));
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2003 3:26:10 PM)
 */
public DBSpecies getBoundSpecies(User user, DBFormalSpecies dbfs) throws DataAccessException{
	try {
		log.print("DatabaseServerImpl.getBoundSpecies");
		return dictionaryTop.getBoundSpecies(dbfs,true);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/20/2003 2:11:12 PM)
 */
public DBFormalSpecies[] getDatabaseSpecies(User argUser,String likeString,boolean isBound,FormalSpeciesType speciesType,int restrictSearch,int rowLimit, boolean bOnlyUser) throws DataAccessException{
	try {
		log.print("DatabaseServerImpl.getDatabaseSpecies");
		return dictionaryTop.getDatabaseSpecies(true,argUser,likeString,isBound,speciesType,restrictSearch,rowLimit,bOnlyUser);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	}
}


/**
 * update method comment.
 */
DBTopLevel getDBTopLevel() {
	return dbTop;
}


/**
 * getDictionaryReactions method comment.
 */
public cbit.vcell.dictionary.ReactionDescription[] getDictionaryReactions(User user, ReactionQuerySpec reactionQuerySpec) throws cbit.vcell.server.DataAccessException{
		
	try {
		log.print("DatabaseServerImpl.getDictionaryReactions");
		return dictionaryTop.getDictionaryReactions(true,reactionQuerySpec);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 */
public ExportLog getExportLog(User user, KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.getExportLog(simulationKey="+simulationKey+")");
		return rsetDbTop.getResultSetExport(user, simulationKey,true);
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 */
public ExportLog[] getExportLogs(User user, boolean bAll) throws DataAccessException {
	try {
		log.print("DatabaseServerImpl.getExportLogs()");
		return rsetDbTop.getResultSetExports(user,bAll,true);
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo getGeometryInfo(User user, org.vcell.util.document.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return ((cbit.vcell.geometry.GeometryInfo[])getVersionInfos(user, key, VersionableType.Geometry, false, true))[0];
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo[] getGeometryInfos(User user, boolean bAll) throws cbit.vcell.server.DataAccessException {
	return (cbit.vcell.geometry.GeometryInfo[])getVersionInfos(user, null, VersionableType.Geometry, bAll, true);
}


/**
 * getVersionable method comment.
 */
public BigString getGeometryXML(User user, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.getGeometryXML(user="+user+", Key="+key+")");
		boolean bCheckPermission = true;
		Geometry geometry = dbTop.getGeometry(user,key,true);
		return new BigString(cbit.vcell.xml.XmlHelper.geometryToXML(geometry));
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelInfo getMathModelInfo(User user, org.vcell.util.document.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return ((cbit.vcell.mathmodel.MathModelInfo[])getVersionInfos(user, key, VersionableType.MathModelMetaData, false, true))[0];
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelInfo[] getMathModelInfos(User user, boolean bAll) throws cbit.vcell.server.DataAccessException {
	return (cbit.vcell.mathmodel.MathModelInfo[])getVersionInfos(user, null, VersionableType.MathModelMetaData, bAll, true);
}


/**
 * getVersionable method comment.
 */
public MathModelMetaData getMathModelMetaData(User user, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.getMathModelMetaData(key="+key+")");
		MathModelMetaData mathModelMetaData = dbTop.getMathModelMetaData(user,key);
		return mathModelMetaData;
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 */
public MathModelMetaData[] getMathModelMetaDatas(User user, boolean bAll) throws DataAccessException {
	try {
		log.print("DatabaseServerImpl.getMathModelMetaDatas(bAll="+bAll+")");
		MathModelMetaData mathModelMetaDataArray[] = dbTop.getMathModelMetaDatas(user,bAll,true);
		return mathModelMetaDataArray;
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionable method comment.
 */
public BigString getMathModelXML(User user, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.getMathModelXML(user="+user+", Key="+key+")");
		return new BigString(serverDocumentManager.getMathModelXML(user, key));
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * publish method comment.
 */
public org.vcell.util.Preference[] getPreferences(User user) throws DataAccessException {
	try {
		log.print("DatabaseServerImpl.getPreferences(User="+user.getName()+")");
		return dbTop.getPreferences(user,true);
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getReactionStepFromRXid method comment.
 */
public cbit.vcell.model.ReactionStep getReactionStep(User user, org.vcell.util.document.KeyValue reactionStepKey) throws cbit.vcell.server.DataAccessException {
	try {
		log.print("DatabaseServerImpl.getReactionStep");
		return dbTop.getReactionStep(user,reactionStepKey,true);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	}
}


/**
 * getUserReactions method comment.
 */
public cbit.vcell.model.ReactionStepInfo[] getReactionStepInfos(User user, KeyValue reactionStepKeys[]) throws cbit.vcell.server.DataAccessException {
	try {
		log.print("DatabaseServerImpl.getReactionStepInfos()");
		return dictionaryTop.getReactionStepInfos(user, true,reactionStepKeys);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 */
public SolverResultSetInfo getResultSetInfo(User user, KeyValue simulationKey, int jobIndex) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.getResultSetInfo(simulationKey="+simulationKey+",jobIndex="+jobIndex+")");
		SolverResultSetInfo rsInfo = rsetDbTop.getResultSetInfo(user,simulationKey,jobIndex,true);
		return rsInfo;
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 */
public SolverResultSetInfo[] getResultSetInfos(User user, boolean bAll) throws DataAccessException {
	try {
		log.print("DatabaseServerImpl.getResultSetInfos(bAll="+bAll+")");
		SolverResultSetInfo rsInfos[] = rsetDbTop.getResultSetInfos(user,bAll,true);
		return rsInfos;
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 3:46:02 PM)
 * @return cbit.vcell.modeldb.ServerDocumentManager
 */
public ServerDocumentManager getServerDocumentManager() {
	return serverDocumentManager;
}


/**
 * getVersionInfo method comment.
 */
public SimulationStatus[] getSimulationStatus(KeyValue simulationKeys[]) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.getSimulationStatus(simulationKey="+simulationKeys+")");
		return adminDbTop.getSimulationStatus(simulationKeys,true);
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 */
public SimulationStatus getSimulationStatus(KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.getSimulationStatus(simulationKey="+simulationKey+")");
		return adminDbTop.getSimulationStatus(simulationKey,true);
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionable method comment.
 */
public BigString getSimulationXML(User user, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.getSimulationXML(user="+user+", Key="+key+")");
		boolean bCheckPermission = false;
		cbit.vcell.solver.Simulation sim = dbTop.getSimulation(user,key);
		return new BigString(cbit.vcell.xml.XmlHelper.simToXML(sim));
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteNew getTestSuite(User user, java.math.BigDecimal getThisTS)
	throws DataAccessException {
		
	try {
		log.print("DatabaseServerImpl.getTestSuite(tsin="+getThisTS+")");
	return dbTop.getTestSuite(getThisTS,user,true);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteInfoNew[] getTestSuiteInfos(User user) throws DataAccessException {
	
	
	try {
		log.print("DatabaseServerImpl.getTestSuiteInfos(user="+user+")");
		return dbTop.getTestSuiteInfos(user,true);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getUserReactions method comment.
 */
public cbit.vcell.dictionary.ReactionDescription[] getUserReactionDescriptions(User user, ReactionQuerySpec reactionQuerySpec) throws cbit.vcell.server.DataAccessException {
	try {
		log.print("DatabaseServerImpl.getUserReactions");
		return dictionaryTop.getUserReactionDescriptions(user, true,reactionQuerySpec);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo getVCImageInfo(User user, org.vcell.util.document.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return ((cbit.image.VCImageInfo[])getVersionInfos(user, key, VersionableType.VCImage, false, true))[0];
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo[] getVCImageInfos(User user, boolean bAll) throws cbit.vcell.server.DataAccessException {
	return (cbit.image.VCImageInfo[])getVersionInfos(user, null, VersionableType.VCImage, bAll, true);
}


/**
 * getVersionable method comment.
 */
public BigString getVCImageXML(User user, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.getVCImageXML(user="+user+", Key="+key+")");
		boolean bCheckPermission = true;
		VCImage image = dbTop.getVCImage(user,key,bCheckPermission);
		return new BigString(cbit.vcell.xml.XmlHelper.imageToXML(image));
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 */
public VCInfoContainer getVCInfoContainer(User user) throws DataAccessException {
	try {
		log.print("DatabaseServerImpl.getVCInfoContainer()");
		VCInfoContainer vcInfoContainer = dbTop.getVCInfoContainer(user,true);
		return vcInfoContainer;
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
private VersionInfo[] getVersionInfos(User user, KeyValue key, VersionableType vType, boolean bAll, boolean bCheckPermission) throws cbit.vcell.server.DataAccessException {
	try {
		log.print("DatabaseServerImpl.getVersionInfos(User="+user+",vType="+vType+",bAll="+bAll+")");
		Vector vector = dbTop.getVersionableInfos(user, key, vType, bAll, bCheckPermission, true);
		if (vType.equals(VersionableType.BioModelMetaData)) {
			cbit.vcell.biomodel.BioModelInfo[] bioModelInfos = new cbit.vcell.biomodel.BioModelInfo[vector.size()];
			vector.copyInto(bioModelInfos);
			return bioModelInfos;
		} else if (vType.equals(VersionableType.Geometry)) {
			cbit.vcell.geometry.GeometryInfo[] geoInfos = new cbit.vcell.geometry.GeometryInfo[vector.size()];
			vector.copyInto(geoInfos);
			return geoInfos;
		} else if (vType.equals(VersionableType.MathModelMetaData)) {
			MathModelInfo[] mathInfos = new MathModelInfo[vector.size()];
			vector.copyInto(mathInfos);
			return mathInfos;
		} else if (vType.equals(VersionableType.VCImage)) {
			cbit.image.VCImageInfo[] imgInfos = new cbit.image.VCImageInfo[vector.size()];
			vector.copyInto(imgInfos);
			return imgInfos;
		} else {
			throw new IllegalArgumentException("Wrong VersinableType vType:" + vType);
		}		
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupAddUser(User user, cbit.sql.VersionableType vType, org.vcell.util.document.KeyValue key,String addUserToGroup, boolean isHidden) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.groupAddUser(vType="+vType.getTypeName()+", Key="+key+", userToAdd="+addUserToGroup+", isHidden="+isHidden+")");
		dbTop.groupAddUser(user,vType,key,true,addUserToGroup,isHidden);
		VersionInfo newVersionInfo = (VersionInfo)(dbTop.getVersionableInfos(user,key,vType,false,true, true).elementAt(0));
		return newVersionInfo;
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupRemoveUser(User user, cbit.sql.VersionableType vType, org.vcell.util.document.KeyValue key,String userRemoveFromGroup,boolean isHiddenFromOwner) 
			throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.groupRemoveUser(vType="+vType.getTypeName()+", Key="+key+", userRemoveFromGroup="+userRemoveFromGroup+")");
		dbTop.groupRemoveUser(user,vType,key,true,userRemoveFromGroup,isHiddenFromOwner);
		VersionInfo newVersionInfo = (VersionInfo)(dbTop.getVersionableInfos(user,key,vType,false,true, true).elementAt(0));
		return newVersionInfo;
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupSetPrivate(User user, cbit.sql.VersionableType vType, org.vcell.util.document.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.groupSetPrivate(vType="+vType.getTypeName()+", Key="+key+")");
		dbTop.groupSetPrivate(user,vType,key,true);
		VersionInfo newVersionInfo = (VersionInfo)(dbTop.getVersionableInfos(user,key,vType,false,true, true).elementAt(0));
		return newVersionInfo;
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupSetPublic(User user, cbit.sql.VersionableType vType, org.vcell.util.document.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	try {
		log.print("DatabaseServerImpl.groupSetPublic(vType="+vType.getTypeName()+", Key="+key+")");
		dbTop.groupSetPublic(user,vType,key,true);
		VersionInfo newVersionInfo = (VersionInfo)(dbTop.getVersionableInfos(user,key,vType,false,true, true).elementAt(0));
		return newVersionInfo;
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * insert method comment.
 */
void insertVersionableChildSummary(User user, VersionableType vType,KeyValue vKey,String serialDBChildSummary) throws DataAccessException {
	try {
		log.print("DatabaseServerImpl.insertVersionableChildSummary(vType="+vType.getTypeName()+", key="+vKey);
		dbTop.insertVersionableChildSummary(user,vType,vKey,serialDBChildSummary,true);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * insert method comment.
 */
void insertVersionableXML(User user, VersionableType vType,KeyValue vKey,String xml) throws DataAccessException {
	try {
		log.print("DatabaseServerImpl.insertVersionableXML(vType="+vType.getTypeName()+", key="+vKey);
		dbTop.insertVersionableXML(user,vType,vKey,xml,true);
	} catch (SQLException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		log.exception(e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * publish method comment.
 */
public void replacePreferences(User user, org.vcell.util.Preference[] preferences) throws DataAccessException {
	try {
		log.print("DatabaseServerImpl.replacePreferences(User="+user.getName()+",preferenceCount="+preferences.length+")");
		dbTop.replacePreferences(user,preferences,true);
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveBioModel(User user, BigString bioModelXML, String independentSims[]) throws cbit.vcell.server.DataAccessException {
	log.print("DatabaseServerImpl.saveBioModel()");
	try {
		return new BigString(getServerDocumentManager().saveBioModel(user, bioModelXML.toString(), null, independentSims));
	}catch (cbit.vcell.mapping.MappingException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}catch (java.beans.PropertyVetoException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}catch (cbit.vcell.xml.XmlParseException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}catch (SQLException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveBioModelAs(User user, BigString bioModelXML, java.lang.String newName, String independentSims[]) throws cbit.vcell.server.DataAccessException {
	log.print("DatabaseServerImpl.saveBioModelAs("+newName+")");
	try {
		return new BigString(getServerDocumentManager().saveBioModel(user, bioModelXML.toString(), newName, independentSims));
	}catch (cbit.vcell.mapping.MappingException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}catch (java.beans.PropertyVetoException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}catch (SQLException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}catch (cbit.vcell.xml.XmlParseException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}

/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveGeometry(User user, BigString geometryXML) throws cbit.vcell.server.DataAccessException {
	log.print("DatabaseServerImpl.saveGeometry()");
	try {
		return new BigString(getServerDocumentManager().saveGeometry(user, geometryXML.toString(), null));
	}catch (cbit.vcell.xml.XmlParseException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}catch (SQLException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveGeometryAs(User user, BigString geometryXML, java.lang.String newName) throws cbit.vcell.server.DataAccessException {
	log.print("DatabaseServerImpl.saveGeometryAs("+newName+")");
	try {
		return new BigString(getServerDocumentManager().saveGeometry(user, geometryXML.toString(), newName));
	}catch (cbit.vcell.xml.XmlParseException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}catch (SQLException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveMathModel(User user, BigString mathModelXML, String independentSims[]) throws cbit.vcell.server.DataAccessException {
	log.print("DatabaseServerImpl.saveMathModel()");
	try {
		return new BigString(getServerDocumentManager().saveMathModel(user, mathModelXML.toString(), null, independentSims));
	}catch (java.beans.PropertyVetoException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}catch (SQLException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}catch (cbit.vcell.xml.XmlParseException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveMathModelAs(User user, BigString mathModelXML, java.lang.String newName, String independentSims[]) throws cbit.vcell.server.DataAccessException {
	log.print("DatabaseServerImpl.saveMathModelAs(" + newName + ")");
	try {
		return new BigString(getServerDocumentManager().saveMathModel(user, mathModelXML.toString(), newName, independentSims));
	}catch (java.beans.PropertyVetoException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}catch (SQLException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}catch (cbit.vcell.xml.XmlParseException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveSimulation(User user, BigString simulationXML, boolean bForceIndependent) throws cbit.vcell.server.DataAccessException {
	log.print("DatabaseServerImpl.saveSimulation()");
	try {
		return new BigString(getServerDocumentManager().saveSimulation(user, simulationXML.toString(), bForceIndependent));
	}catch (java.beans.PropertyVetoException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}catch (SQLException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}catch (cbit.vcell.xml.XmlParseException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveVCImage(User user, BigString vcImageXML) throws cbit.vcell.server.DataAccessException {
	log.print("DatabaseServerImpl.saveVCImage()");
	try {
		return new BigString(getServerDocumentManager().saveVCImage(user, vcImageXML.toString(), null));
	}catch (cbit.vcell.xml.XmlParseException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}catch (SQLException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveVCImageAs(User user, BigString vcImageXML, java.lang.String newName) throws cbit.vcell.server.DataAccessException {
	log.print("DatabaseServerImpl.saveVCImageAs(" + newName + ")");
	try {
		return new BigString(getServerDocumentManager().saveVCImage(user, vcImageXML.toString(), newName));
	}catch (cbit.vcell.xml.XmlParseException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}catch (SQLException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}
}
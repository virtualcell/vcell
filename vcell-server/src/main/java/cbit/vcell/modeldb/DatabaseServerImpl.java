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
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.KeyFactory;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.Preference;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.CurateSpec;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.ReferenceQueryResult;
import org.vcell.util.document.ReferenceQuerySpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.VCInfoContainer;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.VersionableFamily;
import org.vcell.util.document.VersionableType;

import cbit.image.VCImage;
import cbit.image.VCImageInfo;
import cbit.sql.QueryHashtable;
import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.message.server.dispatcher.SimulationDatabaseDirect;
import cbit.vcell.model.DBFormalSpecies;
import cbit.vcell.model.DBSpecies;
import cbit.vcell.model.FormalSpeciesType;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionDescription;
import cbit.vcell.model.ReactionQuerySpec;
import cbit.vcell.model.ReactionStepInfo;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestSuiteNew;
import cbit.vcell.numericstest.TestSuiteOP;
import cbit.vcell.numericstest.TestSuiteOPResults;
import cbit.vcell.server.SimpleJobStatusPersistent;
import cbit.vcell.server.SimpleJobStatusQuerySpec;
import cbit.vcell.server.SimulationJobStatusPersistent;
import cbit.vcell.server.SimulationStatusPersistent;
import cbit.vcell.server.UserRegistrationOP;
import cbit.vcell.server.UserRegistrationResults;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
/**
 * This type was created in VisualAge.
 */
public class DatabaseServerImpl {
	public static final Logger lg = LogManager.getLogger(DatabaseServerImpl.class);

	private DBTopLevel dbTop = null;
	private DictionaryDBTopLevel dictionaryTop = null;
	private AdminDBTopLevel adminDbTop = null;
	private ServerDocumentManager serverDocumentManager = new ServerDocumentManager(this);

	public enum OrderBy {
		name_asc,
		name_desc,
		date_asc,
		date_desc,
		year_asc,
		year_desc
	};
/**
 * This method was created in VisualAge.
 */
public DatabaseServerImpl(ConnectionFactory conFactory, KeyFactory keyFactory) 
						throws DataAccessException {
	super();
	try {
		dbTop = new DBTopLevel(conFactory);
	} catch (Exception e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException("Error creating DBTopLevel " + e.getMessage());
	}		
	try {
		dictionaryTop = new DictionaryDBTopLevel(conFactory);
	} catch (Exception e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException("Error creating DictionaryDbTopLevel " + e.getMessage());
	}		
	try {
		adminDbTop = new AdminDBTopLevel(conFactory);
	} catch (Exception e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException("Error creating AdminDbTopLevel " + e.getMessage());
	}		
}

public void cleanupDatabase() throws DataAccessException {
	try {
		dbTop.cleanupDatabase(true);
	} catch (DataAccessException | SQLException e) {
		e.printStackTrace();
		throw new DataAccessException(e.getMessage(),e);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:04:45 AM)
 */
public VCDocumentInfo curate(User user,CurateSpec curateSpec) throws DataAccessException{
	
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.curate(user="+user+" curateSpec="+curateSpec+")");
		return dbTop.curate(user,curateSpec);
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage(),e);
	}
	catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage(),e);
	} 
	catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage(),e);
	}
	
}


/**
 * delete method comment.
 */
void delete(User user, VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.delete(vType="+vType.getTypeName()+", Key="+key+")");
		dbTop.deleteVersionable(user, vType, key, true);
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}

public UserRegistrationResults userRegistrationOP(User user, UserRegistrationOP userRegistrationOP) throws DataAccessException, ObjectNotFoundException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.userRegistrationOP(...)");
		return dbTop.userRegistrationOP(user,userRegistrationOP,true);
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * delete method comment.
 */
public void deleteBioModel(User user, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	delete(user,VersionableType.BioModelMetaData, key);
}


/**
 * delete method comment.
 */
public FieldDataDBOperationResults fieldDataDBOperation(User user, FieldDataDBOperationSpec fieldDataDBOperationSpec) throws DataAccessException, ObjectNotFoundException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.fieldDataDBOperation opType="+fieldDataDBOperationSpec.opType);
		return dbTop.fieldDataDBOperation(user, fieldDataDBOperationSpec, true);
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * delete method comment.
 */
public void deleteGeometry(User user, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	delete(user,VersionableType.Geometry, key);
}


/**
 * delete method comment.
 */
public void deleteMathModel(User user, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	delete(user,VersionableType.MathModelMetaData, key);
}


/**
 * delete method comment.
 */
public void deleteVCImage(User user, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	delete(user,VersionableType.VCImage, key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public TestSuiteOPResults doTestSuiteOP(User user, TestSuiteOP tsop)
	throws DataAccessException {
		
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.doTestSuiteOP(op="+tsop+")");
	return dbTop.doTestSuiteOP(tsop,user,true);
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:04:45 AM)
 */
public ReferenceQueryResult findReferences(User user, ReferenceQuerySpec rqs) throws DataAccessException{
	
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.findReferences(user="+user+" refQuerySpec="+rqs+")");
		return dbTop.findReferences(user,rqs,true);
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
	//catch (ObjectNotFoundException e) {
		//lg.error(e.getMessage(),e);
		//throw new ObjectNotFoundException(e.getMessage());
	//} 
	catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
	
}


/**
 * getVersionable method comment.
 */
public VersionableFamily getAllReferences(User user, VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getAllReferences(vType="+vType.getTypeName()+", Key="+key+")");
		if (lg.isWarnEnabled()) lg.warn("DatabaseServerImpl.getAllReferences() can return 'version' objects that aren't viewable to user !!!!!!!!!!!!!!!! ");
		return dbTop.getAllReferences(user,key,vType,true);
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BioModelInfo getBioModelInfo(User user, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return ((BioModelInfo[])getVersionInfos(user, key, VersionableType.BioModelMetaData, true, true))[0];
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BioModelInfo[] getBioModelInfos(User user, boolean bAll) throws DataAccessException {
	return (BioModelInfo[])getVersionInfos(user, null, VersionableType.BioModelMetaData, bAll, true);
}


public BioModelRep[] getBioModelReps(User user, String conditions, OrderBy orderBy, int startRow, int numRows) throws DataAccessException, SQLException {
	return dbTop.getBioModelReps(user, conditions, orderBy, startRow, numRows, true);
}

public PublicationRep[] getPublicationReps(User vcellUser, String conditions, OrderBy orderBy) throws SQLException, DataAccessException {
	return dbTop.getPublicationReps(vcellUser, conditions, orderBy, true);
}


public SimContextRep[] getSimContextReps(KeyValue startingSimContextKey, int numRows) throws DataAccessException, SQLException {
	return dbTop.getSimContextReps(startingSimContextKey, numRows, true);
}

public SimContextRep getSimContextRep(KeyValue simContextKey) throws DataAccessException, SQLException {
	return dbTop.getSimContextRep(simContextKey, true);
}

public SimulationRep[] getSimulationReps(KeyValue startingSimKey, int numRows) throws DataAccessException, SQLException {
	return dbTop.getSimulationReps(startingSimKey, numRows, true);
}

public SimulationRep getSimulationRep(KeyValue simKey) throws DataAccessException, SQLException {
	return dbTop.getSimulationRep(simKey, true);
}


/**
 * This method was created in VisualAge.
 * @return SimulationInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationInfo getSimulationInfo(User user, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return ((SimulationInfo[])getVersionInfos(user, key, VersionableType.Simulation, true, true))[0];
}

public SimulationInfo[] getSimulationInfos(User user, boolean bAll) throws DataAccessException {
	return (SimulationInfo[])getVersionInfos(user, null, VersionableType.Simulation, bAll, true);
}


/**
 * getVersionable method comment.
 */
public BioModelMetaData getBioModelMetaData(User user, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getBioModelMetaData(key="+key+")");
		BioModelMetaData bioModelMetaData = dbTop.getBioModelMetaData(new QueryHashtable(), user, key);
		return bioModelMetaData;
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 */
public BioModelMetaData[] getBioModelMetaDatas(User user, boolean bAll) throws DataAccessException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getBioModelMetaDatas(bAll="+bAll+")");
		BioModelMetaData bioModelMetaDataArray[] = dbTop.getBioModelMetaDatas(user,bAll,true);
		return bioModelMetaDataArray;
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionable method comment.
 */
public BigString getBioModelXML(User user, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getBioModelXML(user="+user+", Key="+key+")");
		return new BigString(serverDocumentManager.getBioModelXML(new QueryHashtable(), user, key, false));
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2003 3:26:10 PM)
 */
public DBSpecies getBoundSpecies(User user, DBFormalSpecies dbfs) throws DataAccessException{
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getBoundSpecies");
		return dictionaryTop.getBoundSpecies(dbfs,true);
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/20/2003 2:11:12 PM)
 */
public DBFormalSpecies[] getDatabaseSpecies(User argUser,String likeString,boolean isBound,FormalSpeciesType speciesType,int restrictSearch,int rowLimit, boolean bOnlyUser) throws DataAccessException{
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getDatabaseSpecies");
		return dictionaryTop.getDatabaseSpecies(true,argUser,likeString,isBound,speciesType,restrictSearch,rowLimit,bOnlyUser);
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	}
}


/**
 * update method comment.
 */
DBTopLevel getDBTopLevel() {
	return dbTop;
}
public AdminDBTopLevel getAdminDBTopLevel() {
	return adminDbTop;
}


/**
 * getDictionaryReactions method comment.
 */
public ReactionDescription[] getDictionaryReactions(User user, ReactionQuerySpec reactionQuerySpec) throws DataAccessException{
		
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getDictionaryReactions");
		return dictionaryTop.getDictionaryReactions(true,reactionQuerySpec);
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	}
}

/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public GeometryInfo getGeometryInfo(User user, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return ((GeometryInfo[])getVersionInfos(user, key, VersionableType.Geometry, false, true))[0];
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public GeometryInfo[] getGeometryInfos(User user, boolean bAll) throws DataAccessException {
	return (GeometryInfo[])getVersionInfos(user, null, VersionableType.Geometry, bAll, true);
}


/**
 * getVersionable method comment.
 */
public BigString getGeometryXML(User user, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getGeometryXML(user="+user+", Key="+key+")");
		Geometry geometry = dbTop.getGeometry(new QueryHashtable(), user,key,true);
		return new BigString(XmlHelper.geometryToXML(geometry));
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public MathModelInfo getMathModelInfo(User user, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return ((MathModelInfo[])getVersionInfos(user, key, VersionableType.MathModelMetaData, false, true))[0];
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public MathModelInfo[] getMathModelInfos(User user, boolean bAll) throws DataAccessException {
	return (MathModelInfo[])getVersionInfos(user, null, VersionableType.MathModelMetaData, bAll, true);
}


/**
 * getVersionable method comment.
 */
public MathModelMetaData getMathModelMetaData(User user, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getMathModelMetaData(key="+key+")");
		MathModelMetaData mathModelMetaData = dbTop.getMathModelMetaData(new QueryHashtable(), user,key);
		return mathModelMetaData;
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 */
public MathModelMetaData[] getMathModelMetaDatas(User user, boolean bAll) throws DataAccessException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getMathModelMetaDatas(bAll="+bAll+")");
		MathModelMetaData mathModelMetaDataArray[] = dbTop.getMathModelMetaDatas(user,bAll,true);
		return mathModelMetaDataArray;
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionable method comment.
 */
public BigString getMathModelXML(User user, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getMathModelXML(user="+user+", Key="+key+")");
		return new BigString(serverDocumentManager.getMathModelXML(new QueryHashtable(), user, key, false));
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * publish method comment.
 */
public Preference[] getPreferences(User user) throws DataAccessException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getPreferences(User="+user.getName()+")");
		return dbTop.getPreferences(user,true);
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getReactionStepFromRXid method comment.
 */
public Model getReactionStepAsModel(User user, KeyValue reactionStepKey) throws DataAccessException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getReactionStep");
		return dbTop.getReactionStepAsModel(new QueryHashtable(), user,reactionStepKey,true);
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	}
}


/**
 * getUserReactions method comment.
 */
public ReactionStepInfo[] getReactionStepInfos(User user, KeyValue reactionStepKeys[]) throws DataAccessException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getReactionStepInfos()");
		return dictionaryTop.getReactionStepInfos(user, true,reactionStepKeys);
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
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
public SimulationStatusPersistent[] getSimulationStatus(KeyValue simulationKeys[]) throws DataAccessException, ObjectNotFoundException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getSimulationStatus(simulationKey="+simulationKeys+")");
		return adminDbTop.getSimulationStatus(simulationKeys,true);
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


public List<SimulationJobStatusPersistent> getSimulationJobStatus(SimpleJobStatusQuerySpec simStatusQuerySpec) throws DataAccessException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getSimulationStatus(simStatusQuerySpec="+simStatusQuerySpec+")");
		return adminDbTop.getSimulationJobStatus(simStatusQuerySpec,true);
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


public List<SimpleJobStatusPersistent> getSimpleJobStatus(SimpleJobStatusQuerySpec simStatusQuerySpec) throws DataAccessException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getSimulationStatus(simStatusQuerySpec="+simStatusQuerySpec+")");
		return adminDbTop.getSimpleJobStatus(simStatusQuerySpec,true);
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 */
public SimulationStatusPersistent getSimulationStatus(KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getSimulationStatus(simulationKey="+simulationKey+")");
		return adminDbTop.getSimulationStatus(simulationKey,true);
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionable method comment.
 */
public BigString getSimulationXML(User user, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getSimulationXML(user="+user+", Key="+key+")");
		Simulation sim = dbTop.getSimulation(new QueryHashtable(), user,key);
		return new BigString(XmlHelper.simToXML(sim));
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public TestSuiteNew getTestSuite(User user, java.math.BigDecimal getThisTS)
	throws DataAccessException {
		
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getTestSuite(tsin="+getThisTS+")");
	return dbTop.getTestSuite(getThisTS,user,true);
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public TestSuiteInfoNew[] getTestSuiteInfos(User user) throws DataAccessException {
	
	
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getTestSuiteInfos(user="+user+")");
		return dbTop.getTestSuiteInfos(user,true);
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getUserReactions method comment.
 */
public ReactionDescription[] getUserReactionDescriptions(User user, ReactionQuerySpec reactionQuerySpec) throws DataAccessException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getUserReactions");
		return dictionaryTop.getUserReactionDescriptions(user, true,reactionQuerySpec);
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VCImageInfo getVCImageInfo(User user, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return ((VCImageInfo[])getVersionInfos(user, key, VersionableType.VCImage, false, true))[0];
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VCImageInfo[] getVCImageInfos(User user, boolean bAll) throws DataAccessException {
	return (VCImageInfo[])getVersionInfos(user, null, VersionableType.VCImage, bAll, true);
}


/**
 * getVersionable method comment.
 */
public BigString getVCImageXML(User user, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getVCImageXML(user="+user+", Key="+key+")");
		boolean bCheckPermission = true;
		VCImage image = dbTop.getVCImage(new QueryHashtable(), user,key,bCheckPermission);
		return new BigString(XmlHelper.imageToXML(image));
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 */
public VCInfoContainer getVCInfoContainer(User user) throws DataAccessException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getVCInfoContainer()");
		VCInfoContainer vcInfoContainer = dbTop.getVCInfoContainer(user,true);
		return vcInfoContainer;
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
private VersionInfo[] getVersionInfos(User user, KeyValue key, VersionableType vType, boolean bAll, boolean bCheckPermission) throws DataAccessException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.getVersionInfos(User="+user+",vType="+vType+",bAll="+bAll+")");
		Vector<VersionInfo> vector = dbTop.getVersionableInfos(user, key, vType, bAll, bCheckPermission, true);
		if (vType.equals(VersionableType.BioModelMetaData)) {
			BioModelInfo[] bioModelInfos = new BioModelInfo[vector.size()];
			vector.copyInto(bioModelInfos);
			return bioModelInfos;
		} else if (vType.equals(VersionableType.Geometry)) {
			GeometryInfo[] geoInfos = new GeometryInfo[vector.size()];
			vector.copyInto(geoInfos);
			return geoInfos;
		} else if (vType.equals(VersionableType.MathModelMetaData)) {
			MathModelInfo[] mathInfos = new MathModelInfo[vector.size()];
			vector.copyInto(mathInfos);
			return mathInfos;
		} else if (vType.equals(VersionableType.VCImage)) {
			VCImageInfo[] imgInfos = new VCImageInfo[vector.size()];
			vector.copyInto(imgInfos);
			return imgInfos;
		} else if (vType.equals(VersionableType.Simulation)) {
			SimulationInfo[] simInfos = new SimulationInfo[vector.size()];
			vector.copyInto(simInfos);
			return simInfos;
		} else {
			throw new IllegalArgumentException("Wrong VersinableType vType:" + vType);
		}		
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VersionInfo groupAddUser(User user, VersionableType vType, KeyValue key,String addUserToGroup, boolean isHidden) throws DataAccessException, ObjectNotFoundException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.groupAddUser(vType="+vType.getTypeName()+", Key="+key+", userToAdd="+addUserToGroup+", isHidden="+isHidden+")");
		dbTop.groupAddUser(user,vType,key,true,addUserToGroup,isHidden);
		VersionInfo newVersionInfo = (VersionInfo)(dbTop.getVersionableInfos(user,key,vType,false,true, true).elementAt(0));
		return newVersionInfo;
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VersionInfo groupRemoveUser(User user, VersionableType vType, KeyValue key,String userRemoveFromGroup,boolean isHiddenFromOwner) 
			throws DataAccessException, ObjectNotFoundException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.groupRemoveUser(vType="+vType.getTypeName()+", Key="+key+", userRemoveFromGroup="+userRemoveFromGroup+")");
		dbTop.groupRemoveUser(user,vType,key,true,userRemoveFromGroup,isHiddenFromOwner);
		VersionInfo newVersionInfo = (VersionInfo)(dbTop.getVersionableInfos(user,key,vType,false,true, true).elementAt(0));
		return newVersionInfo;
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VersionInfo groupSetPrivate(User user, VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.groupSetPrivate(vType="+vType.getTypeName()+", Key="+key+")");
		dbTop.groupSetPrivate(user,vType,key,true);
		VersionInfo newVersionInfo = (VersionInfo)(dbTop.getVersionableInfos(user,key,vType,false,true, true).elementAt(0));
		return newVersionInfo;
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VersionInfo groupSetPublic(User user, VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.groupSetPublic(vType="+vType.getTypeName()+", Key="+key+")");
		dbTop.groupSetPublic(user,vType,key,true);
		VersionInfo newVersionInfo = (VersionInfo)(dbTop.getVersionableInfos(user,key,vType,false,true, true).elementAt(0));
		return newVersionInfo;
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * insert method comment.
 */
void insertVersionableChildSummary(User user, VersionableType vType,KeyValue vKey,String serialDBChildSummary) throws DataAccessException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.insertVersionableChildSummary(vType="+vType.getTypeName()+", key="+vKey);
		dbTop.insertVersionableChildSummary(user,vType,vKey,serialDBChildSummary,true);
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * insert method comment.
 */
void insertVersionableXML(User user, VersionableType vType,KeyValue vKey,String xml) throws DataAccessException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.insertVersionableXML(vType="+vType.getTypeName()+", key="+vKey);
		dbTop.insertVersionableXML(user,vType,vKey,xml,true);
	} catch (SQLException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e) {
		lg.error(e.getMessage(),e);
		throw new ObjectNotFoundException(e.getMessage());
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * publish method comment.
 */
public void replacePreferences(User user, Preference[] preferences) throws DataAccessException {
	try {
		if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.replacePreferences(User="+user.getName()+",preferenceCount="+preferences.length+")");
		dbTop.replacePreferences(user,preferences,true);
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveBioModel(User user, BigString bioModelXML, String independentSims[]) throws DataAccessException {
	if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.saveBioModel()");
	try {
		return new BigString(getServerDocumentManager().saveBioModel(new QueryHashtable(), user, bioModelXML.toString(), null, independentSims));
	}catch (MappingException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}catch (java.beans.PropertyVetoException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}catch (XmlParseException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}catch (SQLException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveBioModelAs(User user, BigString bioModelXML, String newName, String independentSims[]) throws DataAccessException {
	if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.saveBioModelAs("+newName+")");
	try {
		return new BigString(getServerDocumentManager().saveBioModel(new QueryHashtable(), user, bioModelXML.toString(), newName, independentSims));
	}catch (MappingException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e);
	}catch (java.beans.PropertyVetoException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e);
	}catch (SQLException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e);
	}catch (XmlParseException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e);
	}
}

/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveGeometry(User user, BigString geometryXML) throws DataAccessException {
	if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.saveGeometry()");
	try {
		return new BigString(getServerDocumentManager().saveGeometry(new QueryHashtable(), user, geometryXML.toString(), null));
	}catch (XmlParseException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}catch (SQLException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveGeometryAs(User user, BigString geometryXML, java.lang.String newName) throws DataAccessException {
	if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.saveGeometryAs("+newName+")");
	try {
		return new BigString(getServerDocumentManager().saveGeometry(new QueryHashtable(), user, geometryXML.toString(), newName));
	}catch (XmlParseException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}catch (SQLException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveMathModel(User user, BigString mathModelXML, String independentSims[]) throws DataAccessException {
	if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.saveMathModel()");
	try {
		return new BigString(getServerDocumentManager().saveMathModel(new QueryHashtable(), user, mathModelXML.toString(), null, independentSims));
	}catch (java.beans.PropertyVetoException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}catch (SQLException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}catch (XmlParseException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveMathModelAs(User user, BigString mathModelXML, java.lang.String newName, String independentSims[]) throws DataAccessException {
	if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.saveMathModelAs(" + newName + ")");
	try {
		return new BigString(getServerDocumentManager().saveMathModel(new QueryHashtable(), user, mathModelXML.toString(), newName, independentSims));
	}catch (java.beans.PropertyVetoException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}catch (SQLException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}catch (XmlParseException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveSimulation(User user, BigString simulationXML, boolean bForceIndependent) throws DataAccessException {
	if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.saveSimulation()");
	try {
		return new BigString(getServerDocumentManager().saveSimulation(new QueryHashtable(), user, simulationXML.toString(), bForceIndependent));
	}catch (java.beans.PropertyVetoException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}catch (SQLException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}catch (XmlParseException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveVCImage(User user, BigString vcImageXML) throws DataAccessException {
	if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.saveVCImage()");
	try {
		return new BigString(getServerDocumentManager().saveVCImage(user, vcImageXML.toString(), null));
	}catch (XmlParseException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}catch (SQLException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveVCImageAs(User user, BigString vcImageXML, java.lang.String newName) throws DataAccessException {
	if (lg.isTraceEnabled()) lg.trace("DatabaseServerImpl.saveVCImageAs(" + newName + ")");
	try {
		return new BigString(getServerDocumentManager().saveVCImage(user, vcImageXML.toString(), newName));
	}catch (XmlParseException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}catch (SQLException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}

}

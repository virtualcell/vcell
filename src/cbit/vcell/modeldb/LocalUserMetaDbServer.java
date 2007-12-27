package cbit.vcell.modeldb;
import cbit.vcell.solver.ode.gui.SimulationStatus;
import cbit.util.BigString;
import java.io.*;
import cbit.vcell.export.server.ExportLog;
import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.solver.SolverResultSetInfo;
import java.rmi.*;
import java.sql.*;
import cbit.sql.*;
import cbit.vcell.server.User;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.server.ObjectNotFoundException;
import cbit.vcell.server.UserRegistrationOP;
import cbit.vcell.server.UserRegistrationResults;
import cbit.vcell.dictionary.DBSpecies;
import cbit.vcell.dictionary.DBFormalSpecies;
import cbit.vcell.dictionary.FormalSpeciesType;
import cbit.vcell.server.PropertyLoader;

/**
 * This type was created in VisualAge.
 */
public class LocalUserMetaDbServer extends java.rmi.server.UnicastRemoteObject implements cbit.vcell.server.UserMetaDbServer {
	private DatabaseServerImpl dbServerImpl = null;
	private User user = null;

/**
 * This method was created in VisualAge.
 */
public LocalUserMetaDbServer(ConnectionFactory conFactory, KeyFactory keyFactory, DBCacheTable dbCacheTable, User argUser, cbit.vcell.server.SessionLog sessionLog) 
						throws RemoteException, DataAccessException {
	super(PropertyLoader.getIntProperty(PropertyLoader.rmiPortUserMetaDbServer,0));
	this.user = argUser;
	dbServerImpl = new DatabaseServerImpl(conFactory,keyFactory, dbCacheTable, sessionLog);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.document.VCDocumentInfo curate(cbit.vcell.server.CurateSpec curateSpec) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return dbServerImpl.curate(user,curateSpec);
}

public UserRegistrationResults userRegistrationOP(UserRegistrationOP userRegistrationOP) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return dbServerImpl.userRegistrationOP(user,userRegistrationOP);
}

/**
 * delete method comment.
 */
public void deleteBioModel(cbit.sql.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	dbServerImpl.deleteBioModel(user, key);
}


/**
 * delete method comment.
 */
public FieldDataDBOperationResults fieldDataDBOperation(FieldDataDBOperationSpec fieldDataDBOperationSpec) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.fieldDataDBOperation(user,fieldDataDBOperationSpec);
}


/**
 * delete method comment.
 */
public void deleteGeometry(cbit.sql.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	dbServerImpl.deleteGeometry(user, key);
}


/**
 * delete method comment.
 */
public void deleteMathModel(cbit.sql.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	dbServerImpl.deleteMathModel(user, key);
}


/**
 * delete method comment.
 */
public void deleteResultSetExport(cbit.sql.KeyValue eleKey) throws DataAccessException{
	dbServerImpl.deleteResultSetExport(user, eleKey);
}


/**
 * delete method comment.
 */
public void deleteVCImage(cbit.sql.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	dbServerImpl.deleteVCImage(user, key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteOPResults doTestSuiteOP(cbit.vcell.numericstest.TestSuiteOP tsop) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {

	return dbServerImpl.doTestSuiteOP(user, tsop);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public ReferenceQueryResult findReferences(ReferenceQuerySpec rqs) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return dbServerImpl.findReferences(user, rqs);
}


/**
 * getVersionable method comment.
 */
public cbit.vcell.modeldb.VersionableFamily getAllReferences(cbit.sql.VersionableType vType, cbit.sql.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.getAllReferences(user, vType, key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelInfo getBioModelInfo(cbit.sql.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.getBioModelInfo(user,key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelInfo[] getBioModelInfos(boolean bAll) throws DataAccessException {
	return dbServerImpl.getBioModelInfos(user, bAll);
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelMetaData getBioModelMetaData(cbit.sql.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.getBioModelMetaData(user, key);
}


/**
 * getVersionInfo method comment.
 */
public BioModelMetaData[] getBioModelMetaDatas(boolean bAll) throws DataAccessException {
	return dbServerImpl.getBioModelMetaDatas(user, bAll);
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getBioModelXML(cbit.sql.KeyValue key) throws DataAccessException {
	return dbServerImpl.getBioModelXML(user, key);
}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2003 3:26:10 PM)
 */
public DBSpecies getBoundSpecies(DBFormalSpecies dbfs) throws DataAccessException{
	return dbServerImpl.getBoundSpecies(user, dbfs);
}


/**
 * Insert the method's description here.
 * Creation date: (2/20/2003 2:11:12 PM)
 */
public DBFormalSpecies[] getDatabaseSpecies(String likeString,boolean isBound,FormalSpeciesType speciesType,int restrictSearch,int rowLimit,boolean bOnlyUser) throws DataAccessException{
	return dbServerImpl.getDatabaseSpecies(user,likeString, isBound, speciesType, restrictSearch, rowLimit, bOnlyUser);
}


/**
 * getDictionaryReactions method comment.
 */
public cbit.vcell.dictionary.ReactionDescription[] getDictionaryReactions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException {
	return dbServerImpl.getDictionaryReactions(user, reactionQuerySpec);
}


/**
 * getVersionInfo method comment.
 */
public ExportLog getExportLog(KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.getExportLog(user, simulationKey);
}


/**
 * getVersionInfo method comment.
 */
public ExportLog[] getExportLogs(boolean bAll) throws DataAccessException {
	return dbServerImpl.getExportLogs(user, bAll);
}

/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo getGeometryInfo(cbit.sql.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.getGeometryInfo(user,key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo[] getGeometryInfos(boolean bAll) throws DataAccessException {
	return dbServerImpl.getGeometryInfos(user,bAll);
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getGeometryXML(cbit.sql.KeyValue key) throws DataAccessException {
	return dbServerImpl.getGeometryXML(user, key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelInfo getMathModelInfo(cbit.sql.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.getMathModelInfo(user,key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelInfo[] getMathModelInfos(boolean bAll) throws DataAccessException {
	return dbServerImpl.getMathModelInfos(user,bAll);
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelMetaData getMathModelMetaData(cbit.sql.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.getMathModelMetaData(user, key);
}


/**
 * getVersionInfo method comment.
 */
public MathModelMetaData[] getMathModelMetaDatas(boolean bAll) throws DataAccessException {
	return dbServerImpl.getMathModelMetaDatas(user, bAll);
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getMathModelXML(cbit.sql.KeyValue key) throws DataAccessException {
	return dbServerImpl.getMathModelXML(user, key);
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.Preference[] getPreferences() throws DataAccessException {
	
	return dbServerImpl.getPreferences(user);
}


/**
 * getReactionStepFromRXid method comment.
 */
public cbit.vcell.model.ReactionStep getReactionStep(cbit.sql.KeyValue reactionStepKey) throws DataAccessException {
	return dbServerImpl.getReactionStep(user, reactionStepKey);
}


/**
 * getUserReactions method comment.
 */
public cbit.vcell.model.ReactionStepInfo[] getReactionStepInfos(KeyValue reactionStepKeys[]) throws DataAccessException {
	return dbServerImpl.getReactionStepInfos(user, reactionStepKeys);
}


/**
 * getVersionInfo method comment.
 */
public SolverResultSetInfo[] getResultSetInfos(boolean bAll) throws DataAccessException {
	return dbServerImpl.getResultSetInfos(user, bAll);
}


/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 11:17:37 AM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
public SimulationStatus[] getSimulationStatus(cbit.sql.KeyValue simulationKeys[]) throws DataAccessException {
	return dbServerImpl.getSimulationStatus(simulationKeys);
}


/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 11:17:37 AM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
public SimulationStatus getSimulationStatus(cbit.sql.KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.getSimulationStatus(simulationKey);
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getSimulationXML(cbit.sql.KeyValue key) throws DataAccessException {
	return dbServerImpl.getSimulationXML(user, key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {

	return dbServerImpl.getTestSuite(user,getThisTS);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteInfoNew[] getTestSuiteInfos() throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {

	return dbServerImpl.getTestSuiteInfos(user);
}


/**
 * getUserReactions method comment.
 */
public cbit.vcell.dictionary.ReactionDescription[] getUserReactionDescriptions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException {
	return dbServerImpl.getUserReactionDescriptions(user, reactionQuerySpec);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo getVCImageInfo(cbit.sql.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.getVCImageInfo(user,key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo[] getVCImageInfos(boolean bAll) throws DataAccessException {
	return dbServerImpl.getVCImageInfos(user,bAll);
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getVCImageXML(cbit.sql.KeyValue key) throws DataAccessException {
	return dbServerImpl.getVCImageXML(user, key);
}


/**
 * getVersionInfo method comment.
 */
public VCInfoContainer getVCInfoContainer() throws DataAccessException {
	return dbServerImpl.getVCInfoContainer(user);
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupAddUser(cbit.sql.VersionableType vType, cbit.sql.KeyValue key,String addUserToGroup, boolean isHidden) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.groupAddUser(user, vType, key, addUserToGroup, isHidden);
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupRemoveUser(cbit.sql.VersionableType vType, cbit.sql.KeyValue key,String userRemoveFromGroup,boolean isHiddenFromOwner) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.groupRemoveUser(user, vType, key, userRemoveFromGroup, isHiddenFromOwner);
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupSetPrivate(cbit.sql.VersionableType vType, cbit.sql.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.groupSetPrivate(user, vType, key);
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupSetPublic(cbit.sql.VersionableType vType, cbit.sql.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.groupSetPublic(user, vType, key);
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public void replacePreferences(cbit.util.Preference[] preferences) throws DataAccessException {

	dbServerImpl.replacePreferences(user,preferences);	
}


/**
 * publish method comment.
 */
public BigString saveBioModel(BigString bioModelXML, String independentSims[]) throws DataAccessException {
	return dbServerImpl.saveBioModel(user, bioModelXML, independentSims);
}


/**
 * publish method comment.
 */
public BigString saveBioModelAs(BigString bioModelXML, String newName, String independentSims[]) throws DataAccessException {
	return dbServerImpl.saveBioModelAs(user, bioModelXML, newName, independentSims);
}

/**
 * publish method comment.
 */
public BigString saveGeometry(BigString geometryXML) throws DataAccessException {
	return dbServerImpl.saveGeometry(user, geometryXML);
}


/**
 * publish method comment.
 */
public BigString saveGeometryAs(BigString geometryXML, String newName) throws DataAccessException {
	return dbServerImpl.saveGeometryAs(user, geometryXML, newName);
}


/**
 * publish method comment.
 */
public BigString saveMathModel(BigString mathModelXML, String independentSims[]) throws DataAccessException {
	return dbServerImpl.saveMathModel(user, mathModelXML, independentSims);
}


/**
 * publish method comment.
 */
public BigString saveMathModelAs(BigString mathModelXML, String newName, String independentSims[]) throws DataAccessException {
	return dbServerImpl.saveMathModelAs(user, mathModelXML, newName, independentSims);
}


/**
 * publish method comment.
 */
public BigString saveSimulation(BigString simulationXML, boolean bForceIndependent) throws DataAccessException {
	return dbServerImpl.saveSimulation(user, simulationXML, bForceIndependent);
}


/**
 * publish method comment.
 */
public BigString saveVCImage(BigString vcImageXML) throws DataAccessException {
	return dbServerImpl.saveVCImage(user, vcImageXML);
}


/**
 * publish method comment.
 */
public BigString saveVCImageAs(BigString vcImageXML, String newName) throws DataAccessException {
	return dbServerImpl.saveVCImageAs(user, vcImageXML, newName);
}
}
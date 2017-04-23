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
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.Preference;
import org.vcell.util.SessionLog;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.CurateSpec;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.ReferenceQueryResult;
import org.vcell.util.document.ReferenceQuerySpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.VersionableFamily;
import org.vcell.util.document.VersionableType;

import cbit.image.VCImageInfo;
import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.dictionary.db.ReactionDescription;
import cbit.vcell.field.db.FieldDataDBOperationResults;
import cbit.vcell.field.db.FieldDataDBOperationSpec;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.model.DBFormalSpecies;
import cbit.vcell.model.DBSpecies;
import cbit.vcell.model.FormalSpeciesType;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionStepInfo;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestSuiteNew;
import cbit.vcell.numericstest.TestSuiteOP;
import cbit.vcell.numericstest.TestSuiteOPResults;
import cbit.vcell.server.SimulationStatusPersistent;
import cbit.vcell.server.UserMetaDbServer;
import cbit.vcell.server.UserRegistrationOP;
import cbit.vcell.server.UserRegistrationResults;

/**
 * This type was created in VisualAge.
 */
public class LocalUserMetaDbServer implements UserMetaDbServer {
	private DatabaseServerImpl dbServerImpl = null;
	private User user = null;

/**
 * This method was created in VisualAge.
 */
public LocalUserMetaDbServer(ConnectionFactory conFactory, KeyFactory keyFactory, User argUser, SessionLog sessionLog) throws DataAccessException {
	this.user = argUser;
	dbServerImpl = new DatabaseServerImpl(conFactory,keyFactory, sessionLog);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VCDocumentInfo curate(CurateSpec curateSpec) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.curate(user,curateSpec);
}

public UserRegistrationResults userRegistrationOP(UserRegistrationOP userRegistrationOP) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.userRegistrationOP(user,userRegistrationOP);
}

/**
 * delete method comment.
 */
public void deleteBioModel(KeyValue key) throws DataAccessException, ObjectNotFoundException {
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
public void deleteGeometry(KeyValue key) throws DataAccessException, ObjectNotFoundException {
	dbServerImpl.deleteGeometry(user, key);
}


/**
 * delete method comment.
 */
public void deleteMathModel(KeyValue key) throws DataAccessException, ObjectNotFoundException {
	dbServerImpl.deleteMathModel(user, key);
}

/**
 * delete method comment.
 */
public void deleteVCImage(KeyValue key) throws DataAccessException, ObjectNotFoundException {
	dbServerImpl.deleteVCImage(user, key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public TestSuiteOPResults doTestSuiteOP(TestSuiteOP tsop) throws DataAccessException {

	return dbServerImpl.doTestSuiteOP(user, tsop);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public ReferenceQueryResult findReferences(ReferenceQuerySpec rqs) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.findReferences(user, rqs);
}


/**
 * getVersionable method comment.
 */
public VersionableFamily getAllReferences(VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.getAllReferences(user, vType, key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BioModelInfo getBioModelInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.getBioModelInfo(user,key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BioModelInfo[] getBioModelInfos(boolean bAll) throws DataAccessException {
	return dbServerImpl.getBioModelInfos(user, bAll);
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BioModelMetaData getBioModelMetaData(KeyValue key) throws DataAccessException, ObjectNotFoundException {
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
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getBioModelXML(KeyValue key) throws DataAccessException {
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
public ReactionDescription[] getDictionaryReactions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException {
	return dbServerImpl.getDictionaryReactions(user, reactionQuerySpec);
}

/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public GeometryInfo getGeometryInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.getGeometryInfo(user,key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public GeometryInfo[] getGeometryInfos(boolean bAll) throws DataAccessException {
	return dbServerImpl.getGeometryInfos(user,bAll);
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getGeometryXML(KeyValue key) throws DataAccessException {
	return dbServerImpl.getGeometryXML(user, key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public MathModelInfo getMathModelInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.getMathModelInfo(user,key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public MathModelInfo[] getMathModelInfos(boolean bAll) throws DataAccessException {
	return dbServerImpl.getMathModelInfos(user,bAll);
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public MathModelMetaData getMathModelMetaData(KeyValue key) throws DataAccessException, ObjectNotFoundException {
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
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getMathModelXML(KeyValue key) throws DataAccessException {
	return dbServerImpl.getMathModelXML(user, key);
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.Preference[] getPreferences() throws DataAccessException {
	
	return dbServerImpl.getPreferences(user);
}


/**
 * getReactionStepFromRXid method comment.
 */
public Model getReactionStepAsModel(KeyValue reactionStepKey) throws DataAccessException {
	return dbServerImpl.getReactionStepAsModel(user, reactionStepKey);
}


/**
 * getUserReactions method comment.
 */
public ReactionStepInfo[] getReactionStepInfos(KeyValue reactionStepKeys[]) throws DataAccessException {
	return dbServerImpl.getReactionStepInfos(user, reactionStepKeys);
}


/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 11:17:37 AM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
public SimulationStatusPersistent[] getSimulationStatus(KeyValue simulationKeys[]) throws DataAccessException {
	return dbServerImpl.getSimulationStatus(simulationKeys);
}


/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 11:17:37 AM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
public SimulationStatusPersistent getSimulationStatus(KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.getSimulationStatus(simulationKey);
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getSimulationXML(KeyValue key) throws DataAccessException {
	return dbServerImpl.getSimulationXML(user, key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws DataAccessException {

	return dbServerImpl.getTestSuite(user,getThisTS);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public TestSuiteInfoNew[] getTestSuiteInfos() throws DataAccessException {

	return dbServerImpl.getTestSuiteInfos(user);
}


/**
 * getUserReactions method comment.
 */
public ReactionDescription[] getUserReactionDescriptions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException {
	return dbServerImpl.getUserReactionDescriptions(user, reactionQuerySpec);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VCImageInfo getVCImageInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.getVCImageInfo(user,key);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VCImageInfo[] getVCImageInfos(boolean bAll) throws DataAccessException {
	return dbServerImpl.getVCImageInfos(user,bAll);
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getVCImageXML(KeyValue key) throws DataAccessException {
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
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VersionInfo groupAddUser(VersionableType vType, KeyValue key,String addUserToGroup, boolean isHidden) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.groupAddUser(user, vType, key, addUserToGroup, isHidden);
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VersionInfo groupRemoveUser(VersionableType vType, KeyValue key,String userRemoveFromGroup,boolean isHiddenFromOwner) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.groupRemoveUser(user, vType, key, userRemoveFromGroup, isHiddenFromOwner);
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VersionInfo groupSetPrivate(VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.groupSetPrivate(user, vType, key);
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VersionInfo groupSetPublic(VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return dbServerImpl.groupSetPublic(user, vType, key);
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public void replacePreferences(Preference[] preferences) throws DataAccessException {

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

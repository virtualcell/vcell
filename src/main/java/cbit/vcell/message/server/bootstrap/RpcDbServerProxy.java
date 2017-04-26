/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.bootstrap;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCInfoContainer;
import org.vcell.util.document.VersionableFamily;

import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.server.ServiceSpec.ServiceType;
import cbit.vcell.model.ReactionQuerySpec;
import cbit.vcell.server.SimulationStatusPersistent;
import cbit.vcell.server.UserRegistrationOP;
import cbit.vcell.server.UserRegistrationResults;

/**
 * Insert the type's description here.
 * Creation date: (12/5/2001 12:00:10 PM)
 * @author: Jim Schaff
 *
 * stateless database service for any user (should be thread safe ... reentrant)
 *
 */
public class RpcDbServerProxy extends AbstractRpcServerProxy implements cbit.vcell.server.UserMetaDbServer {
/**
 * DataServerProxy constructor comment.
 */
public RpcDbServerProxy(UserLoginInfo userLoginInfo, VCMessageSession vcMessageSession, org.vcell.util.SessionLog log) {
	super(userLoginInfo, vcMessageSession, VCellQueue.DbRequestQueue, log);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.document.VCDocumentInfo curate(org.vcell.util.document.CurateSpec curateSpec) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (org.vcell.util.document.VCDocumentInfo)rpc("curate",new Object[]{userLoginInfo.getUser(),curateSpec});
}


public UserRegistrationResults userRegistrationOP(UserRegistrationOP userRegistrationOP) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (UserRegistrationResults)rpc("userRegistrationOP",new Object[]{userLoginInfo.getUser(),userRegistrationOP});
}

/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 4:31:48 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteBioModel(org.vcell.util.document.KeyValue bioModelKey) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	rpc("deleteBioModel",new Object[]{userLoginInfo.getUser(), bioModelKey});
}


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 4:31:48 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public FieldDataDBOperationResults fieldDataDBOperation(FieldDataDBOperationSpec fieldDataDBOperationSpec) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (FieldDataDBOperationResults)rpc("fieldDataDBOperation",new Object[]{userLoginInfo.getUser(), fieldDataDBOperationSpec});
}


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 4:31:48 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteGeometry(org.vcell.util.document.KeyValue geometryKey) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	rpc("deleteGeometry",new Object[]{userLoginInfo.getUser(), geometryKey});
}


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 4:31:48 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteMathModel(org.vcell.util.document.KeyValue mathModelKey) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	rpc("deleteMathModel",new Object[]{userLoginInfo.getUser(), mathModelKey});
}


/**
* Insert the method's description here.
* Creation date: (10/22/2003 10:28:00 AM)
*/
public void deleteResultSetExport(org.vcell.util.document.KeyValue eleKey) throws org.vcell.util.DataAccessException {
	rpc("deleteResultSetExport",new Object[]{userLoginInfo.getUser(), eleKey});
}


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 4:31:48 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteVCImage(org.vcell.util.document.KeyValue imageKey) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	rpc("deleteVCImage",new Object[]{userLoginInfo.getUser(), imageKey});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteOPResults doTestSuiteOP(cbit.vcell.numericstest.TestSuiteOP tsop) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {

	return (cbit.vcell.numericstest.TestSuiteOPResults ) rpc("doTestSuiteOP",new Object[] {userLoginInfo.getUser(),tsop});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.document.ReferenceQueryResult findReferences(org.vcell.util.document.ReferenceQuerySpec rqs) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (org.vcell.util.document.ReferenceQueryResult)rpc("findReferences",new Object[]{userLoginInfo.getUser(),rqs});
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.VersionableFamily
 * @param vType cbit.sql.VersionableType
 * @param key cbit.sql.KeyValue
 */
public org.vcell.util.document.VersionableFamily getAllReferences(org.vcell.util.document.VersionableType vType, org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (VersionableFamily)rpc("getAllReferences",new Object[]{userLoginInfo.getUser(), vType,key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.document.BioModelInfo getBioModelInfo(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (org.vcell.util.document.BioModelInfo)rpc("getBioModelInfo",new Object[]{userLoginInfo.getUser(),key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.document.BioModelInfo[] getBioModelInfos(boolean bAll) throws org.vcell.util.DataAccessException {
	return (org.vcell.util.document.BioModelInfo[])rpc("getBioModelInfos",new Object[]{userLoginInfo.getUser(), new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelMetaData getBioModelMetaData(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (BioModelMetaData)rpc("getBioModelMetaData",new Object[]{userLoginInfo.getUser(), key});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelMetaData[] getBioModelMetaDatas(boolean bAll) throws org.vcell.util.DataAccessException {
	return (BioModelMetaData[])rpc("getBioModelMetaDatas",new Object[]{userLoginInfo.getUser(), new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getBioModelXML(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException {
	return (BigString)rpc("getBioModelXML",new Object[]{userLoginInfo.getUser(), key});
}


/**
 * getBoundSpecies method comment.
 */
public cbit.vcell.model.DBSpecies getBoundSpecies(cbit.vcell.model.DBFormalSpecies dbfs) throws org.vcell.util.DataAccessException {
	return (cbit.vcell.model.DBSpecies)rpc("getBoundSpecies",new Object[]{userLoginInfo.getUser(), dbfs});
}


/**
 * getDatabaseSpecies method comment.
 */
public cbit.vcell.model.DBFormalSpecies[] getDatabaseSpecies(java.lang.String likeString, boolean isBound, cbit.vcell.model.FormalSpeciesType speciesType, int restrictSearch, int rowLimit, boolean bOnlyUser) throws org.vcell.util.DataAccessException {
	return (cbit.vcell.model.DBFormalSpecies[])rpc("getDatabaseSpecies",new Object[]{userLoginInfo.getUser(), likeString,new Boolean(isBound),speciesType,new Integer(restrictSearch),new Integer(rowLimit), new Boolean(bOnlyUser)});
}


/**
 * getDictionaryReactions method comment.
 */
public cbit.vcell.model.ReactionDescription[] getDictionaryReactions(ReactionQuerySpec reactionQuerySpec) throws org.vcell.util.DataAccessException {
	return (cbit.vcell.model.ReactionDescription[])rpc("getDictionaryReactions",new Object[]{userLoginInfo.getUser(), reactionQuerySpec});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo getGeometryInfo(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (cbit.vcell.geometry.GeometryInfo)rpc("getGeometryInfo",new Object[]{userLoginInfo.getUser(),key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo[] getGeometryInfos(boolean bAll) throws org.vcell.util.DataAccessException {
	return (cbit.vcell.geometry.GeometryInfo[])rpc("getGeometryInfos",new Object[]{userLoginInfo.getUser(), new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getGeometryXML(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException {
	return (BigString)rpc("getGeometryXML",new Object[]{userLoginInfo.getUser(), key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.document.MathModelInfo getMathModelInfo(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (org.vcell.util.document.MathModelInfo)rpc("getMathModelInfo",new Object[]{userLoginInfo.getUser(),key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.document.MathModelInfo[] getMathModelInfos(boolean bAll) throws org.vcell.util.DataAccessException {
	return (org.vcell.util.document.MathModelInfo[])rpc("getMathModelInfos",new Object[]{userLoginInfo.getUser(), new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelMetaData getMathModelMetaData(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (MathModelMetaData)rpc("getMathModelMetaData",new Object[]{userLoginInfo.getUser(), key});
}


/**
 * This method was created in VisualAge.
 * @return MathModelMetaData[]
 * @param bAll boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelMetaData[] getMathModelMetaDatas(boolean bAll) throws org.vcell.util.DataAccessException {
	return (MathModelMetaData[])rpc("getMathModelMetaDatas",new Object[]{userLoginInfo.getUser(), new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getMathModelXML(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException {
	return (BigString)rpc("getMathModelXML",new Object[]{userLoginInfo.getUser(), key});
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:54:49 PM)
 * @return cbit.util.Preference
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.Preference[] getPreferences() throws org.vcell.util.DataAccessException {
	return (org.vcell.util.Preference[])rpc("getPreferences",new Object[]{userLoginInfo.getUser()});
}


/**
 * getReactionStep method comment.
 */
public cbit.vcell.model.Model getReactionStepAsModel(org.vcell.util.document.KeyValue rxID) throws org.vcell.util.DataAccessException {
	return (cbit.vcell.model.Model)rpc("getReactionStepAsModel",new Object[]{userLoginInfo.getUser(), rxID});
}


/**
 * getReactionStepInfos method comment.
 */
public cbit.vcell.model.ReactionStepInfo[] getReactionStepInfos(org.vcell.util.document.KeyValue[] reactionStepKeys) throws org.vcell.util.DataAccessException {
	return (cbit.vcell.model.ReactionStepInfo[])rpc("getReactionStepInfos",new Object[]{userLoginInfo.getUser(), reactionStepKeys});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationStatusPersistent[] getSimulationStatus(org.vcell.util.document.KeyValue simulationKeys[]) throws DataAccessException, ObjectNotFoundException {
	return (SimulationStatusPersistent[])rpc("getSimulationStatus",new Object[]{simulationKeys});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationStatusPersistent getSimulationStatus(org.vcell.util.document.KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException {
	return (SimulationStatusPersistent)rpc("getSimulationStatus",new Object[]{simulationKey});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getSimulationXML(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException {
	return (BigString)rpc("getSimulationXML",new Object[]{userLoginInfo.getUser(), key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {

	return (cbit.vcell.numericstest.TestSuiteNew ) rpc("getTestSuite",new Object[] {userLoginInfo.getUser(),getThisTS});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteInfoNew[] getTestSuiteInfos() throws org.vcell.util.DataAccessException, java.rmi.RemoteException {

	return (cbit.vcell.numericstest.TestSuiteInfoNew[] ) rpc("getTestSuiteInfos",new Object[] {userLoginInfo.getUser()});
}


/**
 * getUserReactionDescriptions method comment.
 */
public cbit.vcell.model.ReactionDescription[] getUserReactionDescriptions(ReactionQuerySpec reactionQuerySpec) throws org.vcell.util.DataAccessException {
	return (cbit.vcell.model.ReactionDescription[])rpc("getUserReactionDescriptions",new Object[]{userLoginInfo.getUser(), reactionQuerySpec});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo getVCImageInfo(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (cbit.image.VCImageInfo)rpc("getVCImageInfo",new Object[]{userLoginInfo.getUser(),key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo[] getVCImageInfos(boolean bAll)
    throws org.vcell.util.DataAccessException {
    return (cbit.image.VCImageInfo[]) rpc(
        "getVCImageInfos",
        new Object[] { userLoginInfo.getUser(), new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getVCImageXML(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException {
	return (BigString)rpc("getVCImageXML",new Object[]{userLoginInfo.getUser(), key});
}


/**
 * getVCInfoContainer method comment.
 */
public VCInfoContainer getVCInfoContainer() throws org.vcell.util.DataAccessException {
	return (VCInfoContainer)rpc("getVCInfoContainer",new Object[]{userLoginInfo.getUser()});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.document.VersionInfo groupAddUser(org.vcell.util.document.VersionableType vType, org.vcell.util.document.KeyValue key, java.lang.String addUserToGroup, boolean isHidden) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (org.vcell.util.document.VersionInfo)rpc("groupAddUser",new Object[]{userLoginInfo.getUser(), vType,key,addUserToGroup,new Boolean(isHidden)});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.document.VersionInfo groupRemoveUser(org.vcell.util.document.VersionableType vType, org.vcell.util.document.KeyValue key, java.lang.String userRemoveFromGroup, boolean isHiddenFromOwner) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (org.vcell.util.document.VersionInfo)rpc("groupRemoveUser",new Object[]{userLoginInfo.getUser(), vType,key,userRemoveFromGroup,new Boolean(isHiddenFromOwner)});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.document.VersionInfo groupSetPrivate(org.vcell.util.document.VersionableType vType, org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (org.vcell.util.document.VersionInfo)rpc("groupSetPrivate",new Object[]{userLoginInfo.getUser(), vType,key});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.document.VersionInfo groupSetPublic(org.vcell.util.document.VersionableType vType, org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (org.vcell.util.document.VersionInfo)rpc("groupSetPublic",new Object[]{userLoginInfo.getUser(), vType,key});
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:54:49 PM)
 * @param preferences cbit.util.Preference[]
 * @exception java.rmi.RemoteException The exception description.
 */
public void replacePreferences(org.vcell.util.Preference[] preferences) throws org.vcell.util.DataAccessException {
	rpc("replacePreferences",new Object[]{userLoginInfo.getUser(), preferences});
}


/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 9:39:03 PM)
 * @return java.lang.Object
 * @param methodName java.lang.String
 * @param args java.lang.Object[]
 * @exception java.lang.Exception The exception description.
 */
private Object rpc(String methodName, Object[] args) throws org.vcell.util.ObjectNotFoundException, DataAccessException {
	try {
		return rpc(ServiceType.DB, methodName, args, true);
	} catch (org.vcell.util.ObjectNotFoundException ex) {
		log.exception(ex);
		throw ex;
	} catch (DataAccessException ex) {
		log.exception(ex);
		throw ex;
	} catch (RuntimeException e){
		log.exception(e);
		throw e;
	} catch (Exception e){
		log.exception(e);
		throw new RuntimeException(e.getMessage());
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
public BigString saveBioModel(BigString bioModelXML, String independentSims[]) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (BigString)rpc("saveBioModel",new Object[]{userLoginInfo.getUser(), bioModelXML, independentSims});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveBioModelAs(BigString bioModelXML, java.lang.String newName, String independentSims[]) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (BigString)rpc("saveBioModelAs",new Object[]{userLoginInfo.getUser(), bioModelXML, newName, independentSims});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveGeometry(BigString geometryXML) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (BigString)rpc("saveGeometry",new Object[]{userLoginInfo.getUser(), geometryXML});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveGeometryAs(BigString geometryXML, java.lang.String newName) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (BigString)rpc("saveGeometryAs",new Object[]{userLoginInfo.getUser(), geometryXML, newName});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveMathModel(BigString mathModelXML, String independentSims[]) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (BigString)rpc("saveMathModel",new Object[]{userLoginInfo.getUser(), mathModelXML, independentSims});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveMathModelAs(BigString mathModelXML, java.lang.String newName, String independentSims[]) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (BigString)rpc("saveMathModelAs",new Object[]{userLoginInfo.getUser(), mathModelXML, newName, independentSims});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.BigString saveSimulation(org.vcell.util.BigString simulationXML, boolean bForceIndependent) throws DataAccessException {
	return (BigString)rpc("saveSimulation",new Object[]{userLoginInfo.getUser(), simulationXML, new Boolean(bForceIndependent)});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveVCImage(BigString vcImageXML) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (BigString)rpc("saveVCImage",new Object[]{userLoginInfo.getUser(), vcImageXML});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveVCImageAs(BigString vcImageXML, java.lang.String newName) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return (BigString)rpc("saveVCImageAs",new Object[]{userLoginInfo.getUser(), vcImageXML, newName});
}
}

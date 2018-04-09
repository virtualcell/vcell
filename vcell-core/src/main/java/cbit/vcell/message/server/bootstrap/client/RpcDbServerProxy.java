/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.bootstrap.client;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.CurateSpec;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCInfoContainer;
import org.vcell.util.document.VersionableFamily;

import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.message.VCRpcRequest.RpcServiceType;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.model.ReactionQuerySpec;
import cbit.vcell.server.SimulationStatusPersistent;
import cbit.vcell.server.UserRegistrationOP;
import cbit.vcell.server.UserRegistrationResults;

public class RpcDbServerProxy extends AbstractRpcServerProxy implements cbit.vcell.server.UserMetaDbServer {

public RpcDbServerProxy(UserLoginInfo userLoginInfo, RpcSender rpcSender) {
	super(userLoginInfo, rpcSender, VCellQueue.DbRequestQueue);
}


public org.vcell.util.document.VCDocumentInfo curate(CurateSpec curateSpec) throws DataAccessException, ObjectNotFoundException {
	return (org.vcell.util.document.VCDocumentInfo)rpc("curate",new Object[]{userLoginInfo.getUser(),curateSpec});
}


public UserRegistrationResults userRegistrationOP(UserRegistrationOP userRegistrationOP) throws DataAccessException, ObjectNotFoundException {
	return (UserRegistrationResults)rpc("userRegistrationOP",new Object[]{userLoginInfo.getUser(),userRegistrationOP});
}

public void deleteBioModel(org.vcell.util.document.KeyValue bioModelKey) throws DataAccessException, ObjectNotFoundException {
	rpc("deleteBioModel",new Object[]{userLoginInfo.getUser(), bioModelKey});
}

public FieldDataDBOperationResults fieldDataDBOperation(FieldDataDBOperationSpec fieldDataDBOperationSpec) throws DataAccessException, ObjectNotFoundException {
	return (FieldDataDBOperationResults)rpc("fieldDataDBOperation",new Object[]{userLoginInfo.getUser(), fieldDataDBOperationSpec});
}

public void deleteGeometry(org.vcell.util.document.KeyValue geometryKey) throws DataAccessException, ObjectNotFoundException {
	rpc("deleteGeometry",new Object[]{userLoginInfo.getUser(), geometryKey});
}

public void deleteMathModel(org.vcell.util.document.KeyValue mathModelKey) throws DataAccessException, ObjectNotFoundException {
	rpc("deleteMathModel",new Object[]{userLoginInfo.getUser(), mathModelKey});
}

public void deleteResultSetExport(org.vcell.util.document.KeyValue eleKey) throws DataAccessException {
	rpc("deleteResultSetExport",new Object[]{userLoginInfo.getUser(), eleKey});
}

public void deleteVCImage(org.vcell.util.document.KeyValue imageKey) throws DataAccessException, ObjectNotFoundException {
	rpc("deleteVCImage",new Object[]{userLoginInfo.getUser(), imageKey});
}

public cbit.vcell.numericstest.TestSuiteOPResults doTestSuiteOP(cbit.vcell.numericstest.TestSuiteOP tsop) throws DataAccessException {

	return (cbit.vcell.numericstest.TestSuiteOPResults ) rpc("doTestSuiteOP",new Object[] {userLoginInfo.getUser(),tsop});
}

public org.vcell.util.document.ReferenceQueryResult findReferences(org.vcell.util.document.ReferenceQuerySpec rqs) throws DataAccessException, ObjectNotFoundException {
	return (org.vcell.util.document.ReferenceQueryResult)rpc("findReferences",new Object[]{userLoginInfo.getUser(),rqs});
}

public org.vcell.util.document.VersionableFamily getAllReferences(org.vcell.util.document.VersionableType vType, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (VersionableFamily)rpc("getAllReferences",new Object[]{userLoginInfo.getUser(), vType,key});
}

public org.vcell.util.document.BioModelInfo getBioModelInfo(org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (org.vcell.util.document.BioModelInfo)rpc("getBioModelInfo",new Object[]{userLoginInfo.getUser(),key});
}

public org.vcell.util.document.BioModelInfo[] getBioModelInfos(boolean bAll) throws DataAccessException {
	return (org.vcell.util.document.BioModelInfo[])rpc("getBioModelInfos",new Object[]{userLoginInfo.getUser(), new Boolean(bAll)});
}

public cbit.vcell.biomodel.BioModelMetaData getBioModelMetaData(org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (BioModelMetaData)rpc("getBioModelMetaData",new Object[]{userLoginInfo.getUser(), key});
}

public cbit.vcell.biomodel.BioModelMetaData[] getBioModelMetaDatas(boolean bAll) throws DataAccessException {
	return (BioModelMetaData[])rpc("getBioModelMetaDatas",new Object[]{userLoginInfo.getUser(), new Boolean(bAll)});
}

public BigString getBioModelXML(org.vcell.util.document.KeyValue key) throws DataAccessException {
	return (BigString)rpc("getBioModelXML",new Object[]{userLoginInfo.getUser(), key});
}

public cbit.vcell.model.DBSpecies getBoundSpecies(cbit.vcell.model.DBFormalSpecies dbfs) throws DataAccessException {
	return (cbit.vcell.model.DBSpecies)rpc("getBoundSpecies",new Object[]{userLoginInfo.getUser(), dbfs});
}

public cbit.vcell.model.DBFormalSpecies[] getDatabaseSpecies(java.lang.String likeString, boolean isBound, cbit.vcell.model.FormalSpeciesType speciesType, int restrictSearch, int rowLimit, boolean bOnlyUser) throws DataAccessException {
	return (cbit.vcell.model.DBFormalSpecies[])rpc("getDatabaseSpecies",new Object[]{userLoginInfo.getUser(), likeString,new Boolean(isBound),speciesType,new Integer(restrictSearch),new Integer(rowLimit), new Boolean(bOnlyUser)});
}

public cbit.vcell.model.ReactionDescription[] getDictionaryReactions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException {
	return (cbit.vcell.model.ReactionDescription[])rpc("getDictionaryReactions",new Object[]{userLoginInfo.getUser(), reactionQuerySpec});
}

public cbit.vcell.geometry.GeometryInfo getGeometryInfo(org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (cbit.vcell.geometry.GeometryInfo)rpc("getGeometryInfo",new Object[]{userLoginInfo.getUser(),key});
}

public cbit.vcell.geometry.GeometryInfo[] getGeometryInfos(boolean bAll) throws DataAccessException {
	return (cbit.vcell.geometry.GeometryInfo[])rpc("getGeometryInfos",new Object[]{userLoginInfo.getUser(), new Boolean(bAll)});
}

public BigString getGeometryXML(org.vcell.util.document.KeyValue key) throws DataAccessException {
	return (BigString)rpc("getGeometryXML",new Object[]{userLoginInfo.getUser(), key});
}

public org.vcell.util.document.MathModelInfo getMathModelInfo(org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (org.vcell.util.document.MathModelInfo)rpc("getMathModelInfo",new Object[]{userLoginInfo.getUser(),key});
}

public org.vcell.util.document.MathModelInfo[] getMathModelInfos(boolean bAll) throws DataAccessException {
	return (org.vcell.util.document.MathModelInfo[])rpc("getMathModelInfos",new Object[]{userLoginInfo.getUser(), new Boolean(bAll)});
}

public cbit.vcell.mathmodel.MathModelMetaData getMathModelMetaData(org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (MathModelMetaData)rpc("getMathModelMetaData",new Object[]{userLoginInfo.getUser(), key});
}

public cbit.vcell.mathmodel.MathModelMetaData[] getMathModelMetaDatas(boolean bAll) throws DataAccessException {
	return (MathModelMetaData[])rpc("getMathModelMetaDatas",new Object[]{userLoginInfo.getUser(), new Boolean(bAll)});
}

public BigString getMathModelXML(org.vcell.util.document.KeyValue key) throws DataAccessException {
	return (BigString)rpc("getMathModelXML",new Object[]{userLoginInfo.getUser(), key});
}

public org.vcell.util.Preference[] getPreferences() throws DataAccessException {
	return (org.vcell.util.Preference[])rpc("getPreferences",new Object[]{userLoginInfo.getUser()});
}

public cbit.vcell.model.Model getReactionStepAsModel(org.vcell.util.document.KeyValue rxID) throws DataAccessException {
	return (cbit.vcell.model.Model)rpc("getReactionStepAsModel",new Object[]{userLoginInfo.getUser(), rxID});
}

public cbit.vcell.model.ReactionStepInfo[] getReactionStepInfos(org.vcell.util.document.KeyValue[] reactionStepKeys) throws DataAccessException {
	return (cbit.vcell.model.ReactionStepInfo[])rpc("getReactionStepInfos",new Object[]{userLoginInfo.getUser(), reactionStepKeys});
}

public SimulationStatusPersistent[] getSimulationStatus(org.vcell.util.document.KeyValue simulationKeys[]) throws DataAccessException, ObjectNotFoundException {
	return (SimulationStatusPersistent[])rpc("getSimulationStatus",new Object[]{simulationKeys});
}

public SimulationStatusPersistent getSimulationStatus(org.vcell.util.document.KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException {
	return (SimulationStatusPersistent)rpc("getSimulationStatus",new Object[]{simulationKey});
}

public BigString getSimulationXML(org.vcell.util.document.KeyValue key) throws DataAccessException {
	return (BigString)rpc("getSimulationXML",new Object[]{userLoginInfo.getUser(), key});
}

public cbit.vcell.numericstest.TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws DataAccessException {
	return (cbit.vcell.numericstest.TestSuiteNew ) rpc("getTestSuite",new Object[] {userLoginInfo.getUser(),getThisTS});
}

public cbit.vcell.numericstest.TestSuiteInfoNew[] getTestSuiteInfos() throws DataAccessException {

	return (cbit.vcell.numericstest.TestSuiteInfoNew[] ) rpc("getTestSuiteInfos",new Object[] {userLoginInfo.getUser()});
}

public cbit.vcell.model.ReactionDescription[] getUserReactionDescriptions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException {
	return (cbit.vcell.model.ReactionDescription[])rpc("getUserReactionDescriptions",new Object[]{userLoginInfo.getUser(), reactionQuerySpec});
}

public cbit.image.VCImageInfo getVCImageInfo(org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (cbit.image.VCImageInfo)rpc("getVCImageInfo",new Object[]{userLoginInfo.getUser(),key});
}

public cbit.image.VCImageInfo[] getVCImageInfos(boolean bAll)
    throws DataAccessException {
    return (cbit.image.VCImageInfo[]) rpc(
        "getVCImageInfos",
        new Object[] { userLoginInfo.getUser(), new Boolean(bAll)});
}

public BigString getVCImageXML(org.vcell.util.document.KeyValue key) throws DataAccessException {
	return (BigString)rpc("getVCImageXML",new Object[]{userLoginInfo.getUser(), key});
}

public VCInfoContainer getVCInfoContainer() throws DataAccessException {
	return (VCInfoContainer)rpc("getVCInfoContainer",new Object[]{userLoginInfo.getUser()});
}

public org.vcell.util.document.VersionInfo groupAddUser(org.vcell.util.document.VersionableType vType, org.vcell.util.document.KeyValue key, java.lang.String addUserToGroup, boolean isHidden) throws DataAccessException, ObjectNotFoundException {
	return (org.vcell.util.document.VersionInfo)rpc("groupAddUser",new Object[]{userLoginInfo.getUser(), vType,key,addUserToGroup,new Boolean(isHidden)});
}

public org.vcell.util.document.VersionInfo groupRemoveUser(org.vcell.util.document.VersionableType vType, org.vcell.util.document.KeyValue key, java.lang.String userRemoveFromGroup, boolean isHiddenFromOwner) throws DataAccessException, ObjectNotFoundException {
	return (org.vcell.util.document.VersionInfo)rpc("groupRemoveUser",new Object[]{userLoginInfo.getUser(), vType,key,userRemoveFromGroup,new Boolean(isHiddenFromOwner)});
}

public org.vcell.util.document.VersionInfo groupSetPrivate(org.vcell.util.document.VersionableType vType, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (org.vcell.util.document.VersionInfo)rpc("groupSetPrivate",new Object[]{userLoginInfo.getUser(), vType,key});
}

public org.vcell.util.document.VersionInfo groupSetPublic(org.vcell.util.document.VersionableType vType, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (org.vcell.util.document.VersionInfo)rpc("groupSetPublic",new Object[]{userLoginInfo.getUser(), vType,key});
}

public void replacePreferences(org.vcell.util.Preference[] preferences) throws DataAccessException {
	rpc("replacePreferences",new Object[]{userLoginInfo.getUser(), preferences});
}

private Object rpc(String methodName, Object[] args) throws ObjectNotFoundException, DataAccessException {
	try {
		return rpc(RpcServiceType.DB, methodName, args, true);
	} catch (ObjectNotFoundException ex) {
		lg.error(ex.getMessage(),ex);
		throw ex;
	} catch (DataAccessException ex) {
		lg.error(ex.getMessage(),ex);
		throw ex;
	} catch (RuntimeException e){
		lg.error(e);
		throw e;
	} catch (Exception e){
		lg.error(e);
		throw new RuntimeException(e.getMessage());
	}
}

public BigString saveBioModel(BigString bioModelXML, String independentSims[]) throws DataAccessException, ObjectNotFoundException {
	return (BigString)rpc("saveBioModel",new Object[]{userLoginInfo.getUser(), bioModelXML, independentSims});
}

public BigString saveBioModelAs(BigString bioModelXML, java.lang.String newName, String independentSims[]) throws DataAccessException, ObjectNotFoundException {
	return (BigString)rpc("saveBioModelAs",new Object[]{userLoginInfo.getUser(), bioModelXML, newName, independentSims});
}

public BigString saveGeometry(BigString geometryXML) throws DataAccessException, ObjectNotFoundException {
	return (BigString)rpc("saveGeometry",new Object[]{userLoginInfo.getUser(), geometryXML});
}

public BigString saveGeometryAs(BigString geometryXML, java.lang.String newName) throws DataAccessException, ObjectNotFoundException {
	return (BigString)rpc("saveGeometryAs",new Object[]{userLoginInfo.getUser(), geometryXML, newName});
}

public BigString saveMathModel(BigString mathModelXML, String independentSims[]) throws DataAccessException, ObjectNotFoundException {
	return (BigString)rpc("saveMathModel",new Object[]{userLoginInfo.getUser(), mathModelXML, independentSims});
}

public BigString saveMathModelAs(BigString mathModelXML, java.lang.String newName, String independentSims[]) throws DataAccessException, ObjectNotFoundException {
	return (BigString)rpc("saveMathModelAs",new Object[]{userLoginInfo.getUser(), mathModelXML, newName, independentSims});
}

public org.vcell.util.BigString saveSimulation(org.vcell.util.BigString simulationXML, boolean bForceIndependent) throws DataAccessException {
	return (BigString)rpc("saveSimulation",new Object[]{userLoginInfo.getUser(), simulationXML, new Boolean(bForceIndependent)});
}

public BigString saveVCImage(BigString vcImageXML) throws DataAccessException, ObjectNotFoundException {
	return (BigString)rpc("saveVCImage",new Object[]{userLoginInfo.getUser(), vcImageXML});
}

public BigString saveVCImageAs(BigString vcImageXML, java.lang.String newName) throws DataAccessException, ObjectNotFoundException {
	return (BigString)rpc("saveVCImageAs",new Object[]{userLoginInfo.getUser(), vcImageXML, newName});
}
}

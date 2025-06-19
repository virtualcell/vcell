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

import cbit.vcell.clientdb.ServerRejectedSaveException;
import cbit.vcell.message.VCRpcRequest.RpcServiceType;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.model.ReactionQuerySpec;
import cbit.vcell.server.SimulationStatusPersistent;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.*;

import java.util.TreeMap;

public class RpcDbServerProxy extends AbstractRpcServerProxy {

public RpcDbServerProxy(UserLoginInfo userLoginInfo, RpcSender rpcSender) {
	super(userLoginInfo, rpcSender, VCellQueue.DbRequestQueue);
}

public TreeMap<SpecialUser.SPECIAL_CLAIM,TreeMap<User,String>> getSpecialUsers() throws DataAccessException{
	return (TreeMap<SpecialUser.SPECIAL_CLAIM,TreeMap<User,String>>)rpc("getSpecialUsers",new Object[0]);
}

public org.vcell.util.document.VCDocumentInfo curate(CurateSpec curateSpec) throws DataAccessException, ObjectNotFoundException {
	return (org.vcell.util.document.VCDocumentInfo)rpc("curate",new Object[]{userLoginInfo.getUser(),curateSpec});
}


	public void deleteGeometry(org.vcell.util.document.KeyValue geometryKey) throws DataAccessException, ObjectNotFoundException {
	rpc("deleteGeometry",new Object[]{userLoginInfo.getUser(), geometryKey});
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

public cbit.vcell.model.DBSpecies getBoundSpecies(cbit.vcell.model.DBFormalSpecies dbfs) throws DataAccessException {
	return (cbit.vcell.model.DBSpecies)rpc("getBoundSpecies",new Object[]{userLoginInfo.getUser(), dbfs});
}

public cbit.vcell.model.DBFormalSpecies[] getDatabaseSpecies(java.lang.String likeString, boolean isBound, cbit.vcell.model.FormalSpeciesType speciesType, int restrictSearch, int rowLimit, boolean bOnlyUser) throws DataAccessException {
	return (cbit.vcell.model.DBFormalSpecies[])rpc("getDatabaseSpecies",new Object[]{userLoginInfo.getUser(), likeString,new Boolean(isBound),speciesType,new Integer(restrictSearch),new Integer(rowLimit), new Boolean(bOnlyUser)});
}

public cbit.vcell.model.ReactionDescription[] getDictionaryReactions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException {
	return (cbit.vcell.model.ReactionDescription[])rpc("getDictionaryReactions",new Object[]{userLoginInfo.getUser(), reactionQuerySpec});
}

public org.vcell.util.Preference[] getPreferences() throws DataAccessException {
	return (org.vcell.util.Preference[])rpc("getPreferences",new Object[]{userLoginInfo.getUser()});
}

public String getReactionStepAsModel(org.vcell.util.document.KeyValue rxID) throws DataAccessException {
	return (String)rpc("getReactionStepAsModel",new Object[]{userLoginInfo.getUser(), rxID});
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
		lg.error(e.getMessage(), e);
		throw e;
	} catch (Exception e){
		if(e.getCause() instanceof ServerRejectedSaveException) {
			ServerRejectedSaveException serverRejectedSaveException = (ServerRejectedSaveException)e.getCause();
			lg.info("caught and rethrowing wrapped ServerRejectedSaveException, original exception: "+e.getMessage(), e);
			lg.warn("rethrowing ServerRejectedSaveException:" +serverRejectedSaveException.getMessage(), serverRejectedSaveException);
			throw serverRejectedSaveException;
		}else{
			lg.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
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

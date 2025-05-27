/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.api.messaging;

import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.field.FieldDataAllDBEntries;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.message.server.bootstrap.client.RpcDbServerProxy;
import cbit.vcell.message.server.bootstrap.client.RpcSender;
import cbit.vcell.model.DBFormalSpecies;
import cbit.vcell.model.DBSpecies;
import cbit.vcell.model.FormalSpeciesType;
import cbit.vcell.model.ReactionQuerySpec;
import cbit.vcell.server.SimulationStatusPersistent;
import cbit.vcell.server.UserMetaDbServer;
import cbit.vcell.server.UserRegistrationOP;
import cbit.vcell.server.UserRegistrationResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.api.client.ExceptionHandler;
import org.vcell.api.client.VCellApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.model.*;
import org.vcell.restclient.utils.DtoModelTransforms;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.*;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.rmi.RemoteException;
import java.util.*;


/**
 * This type was created in VisualAge.
 */
public class LocalUserMetaDbServerMessaging implements UserMetaDbServer {
	private RpcDbServerProxy dbServerProxy = null;
	private final VCellApiClient vCellApiClient;
	private static Logger lg = LogManager.getLogger(LocalUserMetaDbServerMessaging.class);

/**
 * This method was created in VisualAge.
 */
public LocalUserMetaDbServerMessaging(UserLoginInfo userLoginInfo, RpcSender rpcSender, VCellApiClient vCellApiClient) {
	this.dbServerProxy = new RpcDbServerProxy(userLoginInfo, rpcSender);
	this.vCellApiClient = vCellApiClient;
}

public TreeMap<User.SPECIAL_CLAIM,TreeMap<User,String>> getSpecialUsers() throws DataAccessException{
	try {
	return dbServerProxy.getSpecialUsers();
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage(),e);
	}
}

public org.vcell.util.document.VCDocumentInfo curate(CurateSpec curateSpec) throws DataAccessException, ObjectNotFoundException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.curate(curatespec="+curateSpec.toString()+")");
		return dbServerProxy.curate(curateSpec);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}

public UserRegistrationResults userRegistrationOP(UserRegistrationOP userRegistrationOP) throws DataAccessException, ObjectNotFoundException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.userRegistrationOP(...)");
		return dbServerProxy.userRegistrationOP(userRegistrationOP);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}

/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 10:23:06 AM)
 * @throws RemoteException 
 */
public void deleteBioModel(org.vcell.util.document.KeyValue key) throws DataAccessException {
	if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.deleteBioModel(Key="+key+")");
    try {
        vCellApiClient.getBioModelApi().deleteBioModel(key.toString());
    } catch (ApiException e) {
		ExceptionHandler.onlyDataAccessException(e);
	}
}

public FieldDataAllDBEntries getAllFieldDataIDs() throws DataAccessException {
    List<FieldDataReference> fieldDataReferences = null;
    try {
        fieldDataReferences = vCellApiClient.getFieldDataApi().getAllIDs();
    } catch (ApiException e) {
        ExceptionHandler.onlyDataAccessException(e);
    }
    return DtoModelTransforms.fieldDataReferencesToDBResults(fieldDataReferences);
}


public void fieldDataFromSimulation(KeyValue sourceSim, int jobIndex, String newFieldDataName) {
    try {
        vCellApiClient.getFieldDataApi().createFromSimulation(sourceSim.toString(), jobIndex, newFieldDataName);
    } catch (ApiException e) {
        throw new RuntimeException(e);
    }
}

	@Override
	public Hashtable<String, ExternalDataIdentifier> copyModelsFieldData(String modelKey, VersionableType modelType) {
		try {
			SourceModel copyFieldData = new SourceModel().modelID(modelKey.toString());

			ModelType modelType1 = VersionableType.MathModelMetaData == modelType ? ModelType.MATHMODEL : ModelType.BIOMODEL;
			copyFieldData.modelType(modelType1);
			Map<String, org.vcell.restclient.model.ExternalDataIdentifier> result = vCellApiClient.getFieldDataApi().copyModelsFieldData(copyFieldData);
			Hashtable<String, ExternalDataIdentifier> toBeReturned = new Hashtable<>();
			for (String key : result.keySet()){
				toBeReturned.put(key, DtoModelTransforms.dtoToExternalDataIdentifier(result.get(key)));
			}
			return toBeReturned;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 10:23:06 AM)
 * @throws RemoteException 
 */
public void deleteGeometry(org.vcell.util.document.KeyValue key) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.deleteGeometry(Key="+key+")");
		dbServerProxy.deleteGeometry(key);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 10:23:06 AM)
 * @throws RemoteException 
 */
public void deleteMathModel(org.vcell.util.document.KeyValue key) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.deleteMathModel(Key="+key+")");
		dbServerProxy.deleteMathModel(key);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 10:23:06 AM)
 * @throws RemoteException 
 */
public void deleteResultSetExport(org.vcell.util.document.KeyValue eleKey) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.deleteResultSetExport(Key="+eleKey+")");
		dbServerProxy.deleteResultSetExport(eleKey);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 10:23:06 AM)
 * @throws RemoteException 
 */
public void deleteVCImage(org.vcell.util.document.KeyValue key) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.deleteVCImage(Key="+key+")");
		dbServerProxy.deleteVCImage(key);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


public cbit.vcell.numericstest.TestSuiteOPResults doTestSuiteOP(cbit.vcell.numericstest.TestSuiteOP tsop) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.doTestSuiteOP("+tsop+")");
		return dbServerProxy.doTestSuiteOP(tsop);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


public org.vcell.util.document.ReferenceQueryResult findReferences(org.vcell.util.document.ReferenceQuerySpec rqs) throws DataAccessException, ObjectNotFoundException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.findReferences(rqs="+rqs+")");
		return dbServerProxy.findReferences(rqs);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionable method comment.
 * @throws RemoteException 
 */
public org.vcell.util.document.VersionableFamily getAllReferences(org.vcell.util.document.VersionableType vType, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getAllReferences(vType="+vType.getTypeName()+", Key="+key+")");
		if (lg.isInfoEnabled()) lg.info("LocalUserMetaDbServerMessaging.getAllReferences() can return 'version' objects that aren't viewable to user !!!!!!!!!!!!!!!! ");
		return dbServerProxy.getAllReferences(vType,key);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


public org.vcell.util.document.BioModelInfo getBioModelInfo(org.vcell.util.document.KeyValue bioModelKey) throws DataAccessException, ObjectNotFoundException {

	try {
		BioModelContext context = vCellApiClient.getBioModelApi().getBioModelContext(bioModelKey.toString());
		return DtoModelTransforms.bioModelContextToBioModelInfo(context);
	} catch (ApiException e) {
		ExceptionHandler.onlyDataAccessException(e);
		throw new RuntimeException("Exception handler did not throw an exception.");
	}
}


public org.vcell.util.document.BioModelInfo[] getBioModelInfos(boolean bAll) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getBioModelInfos(bAll="+bAll+")");
		List<BioModelContext> contexts = vCellApiClient.getBioModelApi().getAllBioModelContexts(bAll);
		BioModelInfo[] infos = new BioModelInfo[contexts.size()];
		for(int i = 0; i < contexts.size(); i++){
			infos[i] = DtoModelTransforms.bioModelContextToBioModelInfo(contexts.get(i));
		}
		return infos;
	} catch (ApiException e) {
		ExceptionHandler.onlyDataAccessException(e);
		throw new RuntimeException("Exception handler did not throw an exception.");
	}
}


/**
 * getVersionInfo method comment.
 * @throws RemoteException 
 */
public BigString getBioModelXML(KeyValue bioModelKey) throws DataAccessException {
	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getBioModelXML(key="+bioModelKey+")");
        return new BigString(vCellApiClient.getBioModelApi().getBioModelVCML(bioModelKey.toString()));
	} catch (ApiException e) {
		ExceptionHandler.onlyDataAccessException(e);
		throw new RuntimeException("Exception handler did not throw an error.", e);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2003 3:26:10 PM)
 * @throws RemoteException 
 */
public DBSpecies getBoundSpecies(DBFormalSpecies dbfs) throws DataAccessException{

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getBoundSpecies");
		return dbServerProxy.getBoundSpecies(dbfs);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/20/2003 2:11:12 PM)
 * @throws RemoteException 
 */
public DBFormalSpecies[] getDatabaseSpecies(String likeString,boolean isBound,FormalSpeciesType speciesType,int restrictSearch,int rowLimit,boolean bOnlyUser) throws DataAccessException{

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getDatabaseSpecies");
		return dbServerProxy.getDatabaseSpecies(likeString,isBound,speciesType,restrictSearch,rowLimit,bOnlyUser);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getDictionaryReactions method comment.
 * @throws RemoteException 
 */
public cbit.vcell.model.ReactionDescription[] getDictionaryReactions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getDictionaryReactions");
		return dbServerProxy.getDictionaryReactions(reactionQuerySpec);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}

public cbit.vcell.geometry.GeometryInfo getGeometryInfo(org.vcell.util.document.KeyValue geoKey) throws DataAccessException, ObjectNotFoundException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getGeometryInfo(key="+geoKey+")");
		return dbServerProxy.getGeometryInfo(geoKey);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


public cbit.vcell.geometry.GeometryInfo[] getGeometryInfos(boolean bAll) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getGeometryInfos(bAll="+bAll+")");
		return dbServerProxy.getGeometryInfos(bAll);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 * @throws RemoteException 
 */
public BigString getGeometryXML(KeyValue geometryKey) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getGeometryXML(key="+geometryKey+")");
		BigString geometryXML = dbServerProxy.getGeometryXML(geometryKey);
		return geometryXML;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


public org.vcell.util.document.MathModelInfo getMathModelInfo(org.vcell.util.document.KeyValue mathModelKey) throws DataAccessException, ObjectNotFoundException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getMathModelInfo(key="+mathModelKey+")");
		return dbServerProxy.getMathModelInfo(mathModelKey);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


public org.vcell.util.document.MathModelInfo[] getMathModelInfos(boolean bAll) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getMathModelInfos(bAll="+bAll+")");
		return dbServerProxy.getMathModelInfos(bAll);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 * @throws RemoteException 
 */
public MathModelMetaData getMathModelMetaData(KeyValue mathModelKey) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getMathModelMetaData(key="+mathModelKey+")");
		MathModelMetaData mathModelMetaData = dbServerProxy.getMathModelMetaData(mathModelKey);
		return mathModelMetaData;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 * @throws RemoteException 
 */
public MathModelMetaData[] getMathModelMetaDatas(boolean bAll) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getMathModelMetaDatas(bAll="+bAll+")");
		MathModelMetaData mathModelMetaDataArray[] = dbServerProxy.getMathModelMetaDatas(bAll);
		return mathModelMetaDataArray;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 * @throws RemoteException 
 */
public BigString getMathModelXML(KeyValue mathModelKey) throws DataAccessException, ObjectNotFoundException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getMathModelXML(mathModelKey="+mathModelKey+")");
		BigString xml = dbServerProxy.getMathModelXML(mathModelKey);
		return xml;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:51:49 PM)
 * @return cbit.util.Preference
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.Preference[] getPreferences() throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getPreferences()");
		org.vcell.util.Preference[] preferences = dbServerProxy.getPreferences();
		return preferences;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getReactionStep method comment.
 * @throws RemoteException 
 */
public String getReactionStepAsModel(org.vcell.util.document.KeyValue rxID) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getReactionStepAsModel()");
		return dbServerProxy.getReactionStepAsModel(rxID);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getReactionStepInfos method comment.
 * @throws RemoteException 
 */
public cbit.vcell.model.ReactionStepInfo[] getReactionStepInfos(org.vcell.util.document.KeyValue[] reactionStepKeys) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getReactionStepInfos()");
		return dbServerProxy.getReactionStepInfos(reactionStepKeys);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


public SimulationStatusPersistent[] getSimulationStatus(org.vcell.util.document.KeyValue simulationKeys[]) throws DataAccessException, ObjectNotFoundException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getSimulationStatus(key="+simulationKeys+")");
		SimulationStatusPersistent simulationStatus[] = dbServerProxy.getSimulationStatus(simulationKeys);
		return simulationStatus;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


public SimulationStatusPersistent getSimulationStatus(org.vcell.util.document.KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getSimulationStatus(key="+simulationKey+")");
		SimulationStatusPersistent simulationStatus = dbServerProxy.getSimulationStatus(simulationKey);
		return simulationStatus;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 * @throws RemoteException 
 */
public BigString getSimulationXML(KeyValue simKey) throws DataAccessException, ObjectNotFoundException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getSimulationXML(simKey="+simKey+")");
		BigString xml = dbServerProxy.getSimulationXML(simKey);
		return xml;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


public cbit.vcell.numericstest.TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getTestSuite("+getThisTS+")");
		return dbServerProxy.getTestSuite(getThisTS);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


public cbit.vcell.numericstest.TestSuiteInfoNew[] getTestSuiteInfos() throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getTestSuiteInfos()");
		return dbServerProxy.getTestSuiteInfos();
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 10:23:06 AM)
 * @return cbit.vcell.dictionary.ReactionDescription[]
 * @param reactionQuerySpec cbit.vcell.modeldb.ReactionQuerySpec
 * @throws RemoteException 
 */
public cbit.vcell.model.ReactionDescription[] getUserReactionDescriptions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getUserReactionDescriptions()");
		return dbServerProxy.getUserReactionDescriptions(reactionQuerySpec);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


public cbit.image.VCImageInfo getVCImageInfo(org.vcell.util.document.KeyValue imgKey) throws DataAccessException, ObjectNotFoundException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getVCImageInfo(key="+imgKey+")");
		return dbServerProxy.getVCImageInfo(imgKey);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


public cbit.image.VCImageInfo[] getVCImageInfos(boolean bAll) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getVCImageInfos(bAll="+bAll+")");
		return dbServerProxy.getVCImageInfos(bAll);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 * @throws RemoteException 
 */
public BigString getVCImageXML(KeyValue imageKey) throws DataAccessException, ObjectNotFoundException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getSimulationXML(imageKey="+imageKey+")");
		BigString xml = dbServerProxy.getVCImageXML(imageKey);
		return xml;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 10:23:06 AM)
 * @return cbit.vcell.modeldb.VCInfoContainer
 * @throws RemoteException 
 */
public VCInfoContainer getVCInfoContainer() throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.getVCInfoContainer()");
		return dbServerProxy.getVCInfoContainer();
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.document.VersionInfo groupAddUser(org.vcell.util.document.VersionableType vType, org.vcell.util.document.KeyValue key,String addUserToGroup, boolean isHidden) throws DataAccessException, ObjectNotFoundException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.groupAddUser(vType="+vType.getTypeName()+", Key="+key+", userToAdd="+addUserToGroup+", isHidden="+isHidden+")");
		VersionInfo newVersionInfo = dbServerProxy.groupAddUser(vType,key,addUserToGroup,isHidden);
		return newVersionInfo;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.document.VersionInfo groupRemoveUser(org.vcell.util.document.VersionableType vType, org.vcell.util.document.KeyValue key,String userRemoveFromGroup,boolean isHiddenFromOwner) throws DataAccessException, ObjectNotFoundException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.groupRemoveUser(vType="+vType.getTypeName()+", Key="+key+", userRemoveFromGroup="+userRemoveFromGroup+")");
		VersionInfo newVersionInfo = dbServerProxy.groupRemoveUser(vType,key,userRemoveFromGroup,isHiddenFromOwner);
		return newVersionInfo;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.document.VersionInfo groupSetPrivate(org.vcell.util.document.VersionableType vType, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.groupSetPrivate(vType="+vType.getTypeName()+", Key="+key+")");
		VersionInfo newVersionInfo = dbServerProxy.groupSetPrivate(vType,key);
		return newVersionInfo;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.document.VersionInfo groupSetPublic(org.vcell.util.document.VersionableType vType, org.vcell.util.document.KeyValue key) throws DataAccessException, ObjectNotFoundException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.groupSetPublic(vType="+vType.getTypeName()+", Key="+key+")");
		VersionInfo newVersionInfo = dbServerProxy.groupSetPublic(vType,key);
		return newVersionInfo;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:51:49 PM)
 * @param preferences cbit.util.Preference[]
 * @exception java.rmi.RemoteException The exception description.
 */
public void replacePreferences(org.vcell.util.Preference[] preferences) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.replacePreferences()");
		dbServerProxy.replacePreferences(preferences);
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}


}


/**
 * getVersionInfo method comment.
 * @throws RemoteException 
 */
public BigString saveBioModel(BigString bioModelXML, String independentSims[]) throws DataAccessException {
	if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.saveBioModel()");
    String savedBioModelXML = null;
    try {
        savedBioModelXML = vCellApiClient.getBioModelApi().saveBioModel(
                        new SaveBioModel().bioModelXML(bioModelXML.toString())
                                .simsRequiringUpdates(independentSims == null ? null : Arrays.asList(independentSims))
        );
    } catch (ApiException e) {
        ExceptionHandler.onlyDataAccessException(e);
    }
    return new BigString(savedBioModelXML);
}


/**
 * getVersionInfo method comment.
 * @throws RemoteException 
 */
public BigString saveBioModelAs(BigString bioModelXML, String newName, String independentSims[]) throws DataAccessException {

	if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.saveBioModel(newName="+newName+")");
    String savedBioModelXML = null;
    try {
        savedBioModelXML = vCellApiClient.getBioModelApi().saveBioModel(
                        new SaveBioModel().bioModelXML(bioModelXML.toString())
                                .name(newName)
                                .simsRequiringUpdates(independentSims == null ? null : Arrays.asList(independentSims)));
    } catch (ApiException e) {
        ExceptionHandler.onlyDataAccessException(e);
    }
    return new BigString(savedBioModelXML);

}


/**
 * getVersionInfo method comment.
 * @throws RemoteException 
 */
public BigString saveGeometry(BigString geometryXML) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.saveGeometry()");
		BigString savedGeometryXML = dbServerProxy.saveGeometry(geometryXML);
		return savedGeometryXML;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 * @throws RemoteException 
 */
public BigString saveGeometryAs(BigString geometryXML, String newName) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.saveGeometryAs(newName="+newName+")");
		BigString savedGeometryXML = dbServerProxy.saveGeometryAs(geometryXML,newName);
		return savedGeometryXML;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 * @throws RemoteException 
 */
public BigString saveMathModel(BigString mathModelXML, String independentSims[]) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.saveMathModel()");
		BigString savedMathModelXML = dbServerProxy.saveMathModel(mathModelXML,independentSims);
		return savedMathModelXML;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 * @throws RemoteException 
 */
public BigString saveMathModelAs(BigString mathModelXML, String newName, String independentSims[]) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.saveMathModel(newName="+newName+")");
		BigString savedMathModelXML = dbServerProxy.saveMathModelAs(mathModelXML,newName, independentSims);
		return savedMathModelXML;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


public org.vcell.util.BigString saveSimulation(org.vcell.util.BigString simulationXML, boolean bForceIndependent) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.saveSimulation()");
		BigString savedSimulationXML = dbServerProxy.saveSimulation(simulationXML,bForceIndependent);
		return savedSimulationXML;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 * @throws RemoteException 
 */
public BigString saveVCImage(BigString vcImageXML) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.saveVCImage()");
		BigString savedVCImageXML = dbServerProxy.saveVCImage(vcImageXML);
		return savedVCImageXML;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 * @throws RemoteException 
 */
public BigString saveVCImageAs(BigString vcImageXML, String newName) throws DataAccessException {

	try {
		if (lg.isTraceEnabled()) lg.trace("LocalUserMetaDbServerMessaging.saveVCImage(newName="+newName+")");
		BigString savedVCImageXML = dbServerProxy.saveVCImageAs(vcImageXML,newName);
		return savedVCImageXML;
	} catch (DataAccessException e) {
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	}

}

}

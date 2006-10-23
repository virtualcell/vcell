package cbit.vcell.messaging.server;
import java.rmi.RemoteException;

import cbit.util.BigString;
import cbit.util.DataAccessException;
import cbit.util.ObjectNotFoundException;
import cbit.util.PropertyLoader;
import cbit.util.ReferenceQueryResult;
import cbit.util.ReferenceQuerySpec;
import cbit.util.SessionLog;
import cbit.util.document.CurateSpec;
import cbit.util.document.KeyValue;
import cbit.util.document.User;
import cbit.util.document.VCDocumentInfo;
import cbit.util.document.VersionInfo;
import cbit.util.document.VersionableFamily;
import cbit.util.document.VersionableType;
import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.dictionary.DBFormalSpecies;
import cbit.vcell.dictionary.DBSpecies;
import cbit.vcell.dictionary.FormalSpeciesType;
import cbit.vcell.dictionary.ReactionQuerySpec;
import cbit.vcell.export.ExportLog;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.modeldb.SolverResultSetInfo;
import cbit.vcell.modeldb.VCInfoContainer;
import cbit.vcell.solvers.SimulationStatus;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

/**
 * This type was created in VisualAge.
 */
public class LocalUserMetaDbServerMessaging extends java.rmi.server.UnicastRemoteObject implements cbit.vcell.server.UserMetaDbServer {
	private RpcDbServerProxy dbServerProxy = null;
	private User user = null;
	private SessionLog log = null;

/**
 * This method was created in VisualAge.
 */
public LocalUserMetaDbServerMessaging(cbit.vcell.messaging.JmsClientMessaging clientMessaging, User argUser, SessionLog sessionLog) throws RemoteException, DataAccessException {
	super(PropertyLoader.getIntProperty(PropertyLoader.rmiPortUserMetaDbServer,0));
	this.user = argUser;
	this.log = sessionLog;
	try {
		this.dbServerProxy = new RpcDbServerProxy(user, clientMessaging, log);
	} catch (javax.jms.JMSException e){
		e.printStackTrace(System.out);
		throw new RuntimeException("JMSException: "+e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VCDocumentInfo curate(CurateSpec curateSpec) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException {
	try {
		log.print("LocalUserMetaDbServerMessaging.curate(curatespec="+curateSpec.toString()+")");
		return dbServerProxy.curate(curateSpec);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 10:23:06 AM)
 */
public void deleteBioModel(KeyValue key) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.deleteBioModel(Key="+key+")");
		dbServerProxy.deleteBioModel(key);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 10:23:06 AM)
 */
public void deleteGeometry(KeyValue key) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.deleteGeometry(Key="+key+")");
		dbServerProxy.deleteGeometry(key);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 10:23:06 AM)
 */
public void deleteMathModel(KeyValue key) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.deleteMathModel(Key="+key+")");
		dbServerProxy.deleteMathModel(key);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 10:23:06 AM)
 */
public void deleteResultSetExport(KeyValue eleKey) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.deleteResultSetExport(Key="+eleKey+")");
		dbServerProxy.deleteResultSetExport(eleKey);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 10:23:06 AM)
 */
public void deleteVCImage(KeyValue key) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.deleteVCImage(Key="+key+")");
		dbServerProxy.deleteVCImage(key);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteOPResults doTestSuiteOP(cbit.vcell.numericstest.TestSuiteOP tsop) throws DataAccessException, java.rmi.RemoteException {
	try {
		log.print("LocalUserMetaDbServerMessaging.doTestSuiteOP("+tsop+")");
		return dbServerProxy.doTestSuiteOP(tsop);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public ReferenceQueryResult findReferences(ReferenceQuerySpec rqs) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException {
	try {
		log.print("LocalUserMetaDbServerMessaging.findReferences(rqs="+rqs+")");
		return dbServerProxy.findReferences(rqs);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionable method comment.
 */
public VersionableFamily getAllReferences(VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getAllReferences(vType="+vType.getTypeName()+", Key="+key+")");
		log.alert("LocalUserMetaDbServerMessaging.getAllReferences() can return 'version' objects that aren't viewable to user !!!!!!!!!!!!!!!! ");
		return dbServerProxy.getAllReferences(vType,key);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.document.BioModelInfo getBioModelInfo(KeyValue bioModelKey) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getBioModelInfo(key="+bioModelKey+")");
		return dbServerProxy.getBioModelInfo(bioModelKey);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.document.BioModelInfo[] getBioModelInfos(boolean bAll) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getBioModelInfos(bAll="+bAll+")");
		return dbServerProxy.getBioModelInfos(bAll);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 */
public BioModelMetaData getBioModelMetaData(KeyValue bioModelKey) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getBioModelMetaData(key="+bioModelKey+")");
		BioModelMetaData bioModelMetaData = dbServerProxy.getBioModelMetaData(bioModelKey);
		return bioModelMetaData;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 */
public BioModelMetaData[] getBioModelMetaDatas(boolean bAll) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getBioModelMetaDatas(bAll="+bAll+")");
		BioModelMetaData bioModelMetaDataArray[] = dbServerProxy.getBioModelMetaDatas(bAll);
		return bioModelMetaDataArray;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 */
public BigString getBioModelXML(KeyValue bioModelKey) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getBioModelXML(key="+bioModelKey+")");
		BigString bioModelXML = dbServerProxy.getBioModelXML(bioModelKey);
		return bioModelXML;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2003 3:26:10 PM)
 */
public DBSpecies getBoundSpecies(DBFormalSpecies dbfs) throws DataAccessException{
	try {
		log.print("LocalUserMetaDbServerMessaging.getBoundSpecies");
		return dbServerProxy.getBoundSpecies(dbfs);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/20/2003 2:11:12 PM)
 */
public DBFormalSpecies[] getDatabaseSpecies(String likeString,boolean isBound,FormalSpeciesType speciesType,int restrictSearch,int rowLimit,boolean bOnlyUser) throws DataAccessException{
	try {
		log.print("LocalUserMetaDbServerMessaging.getDatabaseSpecies");
		return dbServerProxy.getDatabaseSpecies(likeString,isBound,speciesType,restrictSearch,rowLimit,bOnlyUser);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getDictionaryReactions method comment.
 */
public cbit.vcell.dictionary.ReactionDescription[] getDictionaryReactions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getDictionaryReactions");
		return dbServerProxy.getDictionaryReactions(reactionQuerySpec);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 */
public ExportLog getExportLog(KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getExportLog(simulationKey="+simulationKey+")");
		ExportLog exportLog = dbServerProxy.getExportLog(simulationKey);
		return exportLog;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 */
public ExportLog[] getExportLogs(boolean bAll) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getExportLogs()");
		ExportLog exportLogs[] = dbServerProxy.getExportLogs(bAll);
		return exportLogs;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo getGeometryInfo(KeyValue geoKey) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getGeometryInfo(key="+geoKey+")");
		return dbServerProxy.getGeometryInfo(geoKey);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo[] getGeometryInfos(boolean bAll) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getGeometryInfos(bAll="+bAll+")");
		return dbServerProxy.getGeometryInfos(bAll);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 */
public BigString getGeometryXML(KeyValue geometryKey) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getGeometryXML(key="+geometryKey+")");
		BigString geometryXML = dbServerProxy.getGeometryXML(geometryKey);
		return geometryXML;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.document.MathModelInfo getMathModelInfo(KeyValue mathModelKey) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getMathModelInfo(key="+mathModelKey+")");
		return dbServerProxy.getMathModelInfo(mathModelKey);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.document.MathModelInfo[] getMathModelInfos(boolean bAll) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getMathModelInfos(bAll="+bAll+")");
		return dbServerProxy.getMathModelInfos(bAll);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 */
public MathModelMetaData getMathModelMetaData(KeyValue mathModelKey) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getMathModelMetaData(key="+mathModelKey+")");
		MathModelMetaData mathModelMetaData = dbServerProxy.getMathModelMetaData(mathModelKey);
		return mathModelMetaData;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 */
public MathModelMetaData[] getMathModelMetaDatas(boolean bAll) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getMathModelMetaDatas(bAll="+bAll+")");
		MathModelMetaData mathModelMetaDataArray[] = dbServerProxy.getMathModelMetaDatas(bAll);
		return mathModelMetaDataArray;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 */
public BigString getMathModelXML(KeyValue mathModelKey) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getMathModelXML(mathModelKey="+mathModelKey+")");
		BigString xml = dbServerProxy.getMathModelXML(mathModelKey);
		return xml;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
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
public cbit.util.Preference[] getPreferences() throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getPreferences()");
		cbit.util.Preference[] preferences = dbServerProxy.getPreferences();
		return preferences;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getReactionStep method comment.
 */
public cbit.vcell.model.ReactionStep getReactionStep(KeyValue rxID) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getReactionStep()");
		return dbServerProxy.getReactionStep(rxID);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getReactionStepInfos method comment.
 */
public cbit.vcell.model.ReactionStepInfo[] getReactionStepInfos(KeyValue[] reactionStepKeys) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getReactionStepInfos()");
		return dbServerProxy.getReactionStepInfos(reactionStepKeys);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 */
public SolverResultSetInfo[] getResultSetInfos(boolean bAll) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getResultSetInfos(bAll="+bAll+")");
		SolverResultSetInfo rsInfos[] = dbServerProxy.getResultSetInfos(bAll);
		return rsInfos;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 11:27:01 AM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey KeyValue
 */
public SimulationStatus[] getSimulationStatus(KeyValue simulationKeys[]) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getSimulationStatus(key="+simulationKeys+")");
		SimulationStatus simulationStatus[] = dbServerProxy.getSimulationStatus(simulationKeys);
		return simulationStatus;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 11:27:01 AM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey KeyValue
 */
public SimulationStatus getSimulationStatus(KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getSimulationStatus(key="+simulationKey+")");
		SimulationStatus simulationStatus = dbServerProxy.getSimulationStatus(simulationKey);
		return simulationStatus;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 */
public BigString getSimulationXML(KeyValue simKey) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getSimulationXML(simKey="+simKey+")");
		BigString xml = dbServerProxy.getSimulationXML(simKey);
		return xml;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws DataAccessException, java.rmi.RemoteException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getTestSuite("+getThisTS+")");
		return dbServerProxy.getTestSuite(getThisTS);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteInfoNew[] getTestSuiteInfos() throws DataAccessException, java.rmi.RemoteException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getTestSuiteInfos()");
		return dbServerProxy.getTestSuiteInfos();
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 10:23:06 AM)
 * @return cbit.vcell.dictionary.ReactionDescription[]
 * @param reactionQuerySpec cbit.vcell.modeldb.ReactionQuerySpec
 */
public cbit.vcell.dictionary.ReactionDescription[] getUserReactionDescriptions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getUserReactionDescriptions()");
		return dbServerProxy.getUserReactionDescriptions(reactionQuerySpec);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo getVCImageInfo(KeyValue imgKey) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getVCImageInfo(key="+imgKey+")");
		return dbServerProxy.getVCImageInfo(imgKey);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo[] getVCImageInfos(boolean bAll) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getVCImageInfos(bAll="+bAll+")");
		return dbServerProxy.getVCImageInfos(bAll);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * getVersionInfo method comment.
 */
public BigString getVCImageXML(KeyValue imageKey) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getSimulationXML(imageKey="+imageKey+")");
		BigString xml = dbServerProxy.getVCImageXML(imageKey);
		return xml;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 10:23:06 AM)
 * @return cbit.vcell.modeldb.VCInfoContainer
 */
public VCInfoContainer getVCInfoContainer() throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.getVCInfoContainer()");
		return dbServerProxy.getVCInfoContainer();
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
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
public cbit.util.document.VersionInfo groupAddUser(VersionableType vType, KeyValue key,String addUserToGroup, boolean isHidden) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("LocalUserMetaDbServerMessaging.groupAddUser(vType="+vType.getTypeName()+", Key="+key+", userToAdd="+addUserToGroup+", isHidden="+isHidden+")");
		VersionInfo newVersionInfo = dbServerProxy.groupAddUser(vType,key,addUserToGroup,isHidden);
		return newVersionInfo;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
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
public cbit.util.document.VersionInfo groupRemoveUser(VersionableType vType, KeyValue key,String userRemoveFromGroup,boolean isHiddenFromOwner) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("LocalUserMetaDbServerMessaging.groupRemoveUser(vType="+vType.getTypeName()+", Key="+key+", userRemoveFromGroup="+userRemoveFromGroup+")");
		VersionInfo newVersionInfo = dbServerProxy.groupRemoveUser(vType,key,userRemoveFromGroup,isHiddenFromOwner);
		return newVersionInfo;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
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
public cbit.util.document.VersionInfo groupSetPrivate(VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("LocalUserMetaDbServerMessaging.groupSetPrivate(vType="+vType.getTypeName()+", Key="+key+")");
		VersionInfo newVersionInfo = dbServerProxy.groupSetPrivate(vType,key);
		return newVersionInfo;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
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
public cbit.util.document.VersionInfo groupSetPublic(VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	try {
		log.print("LocalUserMetaDbServerMessaging.groupSetPublic(vType="+vType.getTypeName()+", Key="+key+")");
		VersionInfo newVersionInfo = dbServerProxy.groupSetPublic(vType,key);
		return newVersionInfo;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:51:49 PM)
 * @param preferences cbit.util.Preference[]
 * @exception java.rmi.RemoteException The exception description.
 */
public void replacePreferences(cbit.util.Preference[] preferences) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.replacePreferences()");
		dbServerProxy.replacePreferences(preferences);
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}


}


/**
 * getVersionInfo method comment.
 */
public BigString saveBioModel(BigString bioModelXML, String independentSims[]) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.saveBioModel()");
		BigString savedBioModelXML = dbServerProxy.saveBioModel(bioModelXML, independentSims);
		return savedBioModelXML;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 */
public BigString saveBioModelAs(BigString bioModelXML, String newName, String independentSims[]) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.saveBioModel(newName="+newName+")");
		BigString savedBioModelXML = dbServerProxy.saveBioModelAs(bioModelXML,newName,independentSims);
		return savedBioModelXML;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 */
public BigString saveGeometry(BigString geometryXML) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.saveGeometry()");
		BigString savedGeometryXML = dbServerProxy.saveGeometry(geometryXML);
		return savedGeometryXML;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 */
public BigString saveGeometryAs(BigString geometryXML, String newName) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.saveGeometryAs(newName="+newName+")");
		BigString savedGeometryXML = dbServerProxy.saveGeometryAs(geometryXML,newName);
		return savedGeometryXML;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 */
public BigString saveMathModel(BigString mathModelXML, String independentSims[]) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.saveMathModel()");
		BigString savedMathModelXML = dbServerProxy.saveMathModel(mathModelXML,independentSims);
		return savedMathModelXML;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 */
public BigString saveMathModelAs(BigString mathModelXML, String newName, String independentSims[]) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.saveMathModel(newName="+newName+")");
		BigString savedMathModelXML = dbServerProxy.saveMathModelAs(mathModelXML,newName, independentSims);
		return savedMathModelXML;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
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
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveSimulation(cbit.util.BigString simulationXML, boolean bForceIndependent) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.saveSimulation()");
		BigString savedSimulationXML = dbServerProxy.saveSimulation(simulationXML,bForceIndependent);
		return savedSimulationXML;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 */
public BigString saveVCImage(BigString vcImageXML) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.saveVCImage()");
		BigString savedVCImageXML = dbServerProxy.saveVCImage(vcImageXML);
		return savedVCImageXML;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}


/**
 * getVersionInfo method comment.
 */
public BigString saveVCImageAs(BigString vcImageXML, String newName) throws DataAccessException {
	try {
		log.print("LocalUserMetaDbServerMessaging.saveVCImage(newName="+newName+")");
		BigString savedVCImageXML = dbServerProxy.saveVCImageAs(vcImageXML,newName);
		return savedVCImageXML;
	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

}
}
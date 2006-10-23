package cbit.vcell.anonymizer;
import cbit.util.PropertyLoader;
import cbit.util.ReferenceQueryResult;
import cbit.util.ReferenceQuerySpec;
import cbit.util.SessionLog;
import cbit.util.document.KeyValue;
import cbit.util.document.VersionableFamily;
import cbit.util.document.VersionableType;
/**
 * Insert the type's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @author: Jim Schaff
 */
public class AnonymizerUserMetaDbServer extends AnonymizerService implements cbit.vcell.server.UserMetaDbServer {
/**
 * AnomymizerUserMetaDbServer constructor comment.
 * @exception java.rmi.RemoteException The exception description.
 */
protected AnonymizerUserMetaDbServer(AnonymizerVCellConnection arg_anonymizerVCellConnection, SessionLog arg_sessionLog) throws java.rmi.RemoteException {
	super(PropertyLoader.getIntProperty(PropertyLoader.rmiPortUserMetaDbServer,0), arg_anonymizerVCellConnection, arg_sessionLog);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.document.VCDocumentInfo curate(cbit.util.document.CurateSpec curateSpec) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.util.document.VCDocumentInfo)remoteCall("curate", new Class[] {cbit.util.document.CurateSpec.class}, new Object[] {curateSpec});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @param bioModelKey KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteBioModel(KeyValue bioModelKey) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	remoteCall("deleteBioModel", new Class[] {KeyValue.class}, new Object[] {bioModelKey});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @param bioModelKey KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteGeometry(KeyValue geometryKey) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	remoteCall("deleteGeometry", new Class[] {KeyValue.class}, new Object[] {geometryKey});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @param bioModelKey KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteMathModel(KeyValue mathModelKey) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	remoteCall("deleteMathModel", new Class[] {KeyValue.class}, new Object[] {mathModelKey});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 */
public void deleteResultSetExport(KeyValue eleKey) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	remoteCall("deleteResultSetExport", new Class[] {KeyValue.class}, new Object[] {eleKey});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @param bioModelKey KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteVCImage(KeyValue imageKey) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	remoteCall("deleteVCImage", new Class[] {KeyValue.class}, new Object[] {imageKey});	
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteOPResults doTestSuiteOP(cbit.vcell.numericstest.TestSuiteOP tsop) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.numericstest.TestSuiteOPResults)remoteCall("doTestSuiteOP", new Class[] {cbit.vcell.numericstest.TestSuiteOP.class}, new Object[] {tsop});	
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public ReferenceQueryResult findReferences(ReferenceQuerySpec rqs) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (ReferenceQueryResult)remoteCall("findReferences", new Class[] {ReferenceQuerySpec.class}, new Object[] {rqs});	
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.VersionableFamily
 * @param vType VersionableType
 * @param key KeyValue
 */
public VersionableFamily getAllReferences(VersionableType vType, KeyValue key) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (VersionableFamily)remoteCall("getAllReferences", 
		new Class[] {VersionableType.class, KeyValue.class}, new Object[] {vType, key});	
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.document.BioModelInfo getBioModelInfo(KeyValue key) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.util.document.BioModelInfo)remoteCall("getBioModelInfo", new Class[] {KeyValue.class}, new Object[] {key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.document.BioModelInfo[] getBioModelInfos(boolean bAll) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.document.BioModelInfo[])remoteCall("getBioModelInfos", new Class[] {boolean.class}, new Object[] {new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelMetaData getBioModelMetaData(KeyValue key) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.biomodel.BioModelMetaData)remoteCall("getBioModelMetaData", new Class[] {KeyValue.class}, new Object[] {key});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelMetaData[] getBioModelMetaDatas(boolean bAll) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.biomodel.BioModelMetaData[])remoteCall("getBioModelMetaDatas", new Class[] {boolean.class}, new Object[] {new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString getBioModelXML(KeyValue key) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("getBioModelXML", new Class[] {KeyValue.class}, new Object[] {key});
}


/**
 * getBoundSpecies method comment.
 */
public cbit.vcell.dictionary.DBSpecies getBoundSpecies(cbit.vcell.dictionary.DBFormalSpecies dbfs) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.dictionary.DBSpecies)remoteCall("getBoundSpecies", new Class[] {cbit.vcell.dictionary.DBFormalSpecies.class}, new Object[] {dbfs});
}


/**
 * getDatabaseSpecies method comment.
 */
public cbit.vcell.dictionary.DBFormalSpecies[] getDatabaseSpecies(String likeString, boolean isBound, cbit.vcell.dictionary.FormalSpeciesType speciesType, int restrictSearch, int rowLimit, boolean bUserOnly) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.dictionary.DBSpecies[])remoteCall("getDatabaseSpecies", 
		new Class[] {String.class, boolean.class, cbit.vcell.dictionary.FormalSpeciesType.class, int.class, int.class, boolean.class}, 
		new Object[] {likeString, new Boolean(isBound), speciesType, new Integer(restrictSearch), new Integer(rowLimit), new Boolean(bUserOnly)});
}


/**
 * getDictionaryReactions method comment.
 */
public cbit.vcell.dictionary.ReactionDescription[] getDictionaryReactions(cbit.vcell.dictionary.ReactionQuerySpec reactionQuerySpec) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.dictionary.ReactionDescription[])remoteCall("getDictionaryReactions", new Class[] {cbit.vcell.dictionary.ReactionQuerySpec.class}, new Object[] {reactionQuerySpec});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.export.server.ExportLog
 * @param simKey KeyValue
 */
public cbit.vcell.export.ExportLog getExportLog(KeyValue simulationKey) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.export.ExportLog)remoteCall("getExportLog", new Class[] {KeyValue.class}, new Object[] {simulationKey});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.export.server.ExportLog[]
 * @param simKey KeyValue
 */
public cbit.vcell.export.ExportLog[] getExportLogs(boolean bAll) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.export.ExportLog[])remoteCall("getExportLogs", new Class[] {boolean.class}, new Object[] {new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo getGeometryInfo(KeyValue key) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.geometry.GeometryInfo)remoteCall("getGeometryInfo", new Class[] {KeyValue.class}, new Object[] {key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo[] getGeometryInfos(boolean bAll) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.geometry.GeometryInfo[])remoteCall("getGeometryInfos", new Class[] {boolean.class}, new Object[] {new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString getGeometryXML(KeyValue key) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("getGeometryXML", new Class[] {KeyValue.class}, new Object[] {key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.document.MathModelInfo getMathModelInfo(KeyValue key) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.util.document.MathModelInfo)remoteCall("getMathModelInfo", new Class[] {KeyValue.class}, new Object[] {key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.document.MathModelInfo[] getMathModelInfos(boolean bAll) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.document.MathModelInfo[])remoteCall("getMathModelInfos", new Class[] {boolean.class}, new Object[] {new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelMetaData getMathModelMetaData(KeyValue key) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.mathmodel.MathModelMetaData)remoteCall("getMathModelMetaData", new Class[]{KeyValue.class}, new Object[]{key});
}


/**
 * This method was created in VisualAge.
 * @return MathModelMetaData[]
 * @param bAll boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelMetaData[] getMathModelMetaDatas(boolean bAll) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.mathmodel.MathModelMetaData[])remoteCall("getMathModelMetaDatas", new Class[]{boolean.class}, new Object[]{new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString getMathModelXML(KeyValue key) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("getMathModelXML", new Class[]{KeyValue.class}, new Object[]{key});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.util.Preference
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.Preference[] getPreferences() throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.Preference[])remoteCall("getPreferences", new Class[]{}, new Object[]{});
}


/**
 * getReactionStep method comment.
 */
public cbit.vcell.model.ReactionStep getReactionStep(KeyValue rxID) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.model.ReactionStep)remoteCall("getReactionStep", new Class[]{KeyValue.class}, new Object[]{rxID});
}


/**
 * getReactionStepInfos method comment.
 */
public cbit.vcell.model.ReactionStepInfo[] getReactionStepInfos(KeyValue[] reactionStepKeys) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.model.ReactionStepInfo[])remoteCall("getReactionStepInfos", new Class[]{KeyValue[].class}, new Object[]{reactionStepKeys});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey KeyValue
 */
public cbit.vcell.modeldb.SolverResultSetInfo[] getResultSetInfos(boolean bAll) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.modeldb.SolverResultSetInfo[])remoteCall("getResultSetInfos", new Class[]{boolean.class}, new Object[]{new Boolean(bAll)});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey KeyValue
 */
public cbit.vcell.solvers.SimulationStatus[] getSimulationStatus(KeyValue[] simulationKeys) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.solvers.SimulationStatus[])remoteCall("getSimulationStatus", new Class[]{KeyValue[].class}, new Object[]{simulationKeys});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey KeyValue
 */
public cbit.vcell.solvers.SimulationStatus getSimulationStatus(KeyValue simulationKey) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.solvers.SimulationStatus)remoteCall("getSimulationStatus", new Class[]{KeyValue.class}, new Object[]{simulationKey});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString getSimulationXML(KeyValue key) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("getSimulationXML", new Class[]{KeyValue.class}, new Object[]{key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.numericstest.TestSuiteNew)remoteCall("getTestSuite", new Class[]{java.math.BigDecimal.class}, new Object[]{getThisTS});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteInfoNew[] getTestSuiteInfos() throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.numericstest.TestSuiteInfoNew[])remoteCall("getTestSuiteInfos", new Class[]{}, new Object[]{});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.dictionary.ReactionDescription[]
 * @param reactionQuerySpec cbit.vcell.modeldb.ReactionQuerySpec
 */
public cbit.vcell.dictionary.ReactionDescription[] getUserReactionDescriptions(cbit.vcell.dictionary.ReactionQuerySpec reactionQuerySpec) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.dictionary.ReactionDescription[])remoteCall("getUserReactionDescriptions", new Class[] {cbit.vcell.dictionary.ReactionQuerySpec.class}, new Object[] {reactionQuerySpec});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo getVCImageInfo(KeyValue key) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.image.VCImageInfo)remoteCall("getVCImageInfo", new Class[]{KeyValue.class}, new Object[]{key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo[] getVCImageInfos(boolean bAll) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.image.VCImageInfo[])remoteCall("getVCImageInfos", new Class[]{boolean.class}, new Object[]{new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString getVCImageXML(KeyValue key) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("getVCImageXML", new Class[]{KeyValue.class}, new Object[]{key});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.modeldb.VCInfoContainer
 */
public cbit.vcell.modeldb.VCInfoContainer getVCInfoContainer() throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.modeldb.VCInfoContainer)remoteCall("getVCInfoContainer", new Class[]{}, new Object[]{});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.document.VersionInfo groupAddUser(VersionableType vType, KeyValue key, String addUserToGroup, boolean isHidden) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.util.document.VersionInfo)remoteCall("groupAddUser", new Class[]{VersionableType.class, KeyValue.class, String.class, boolean.class}, 
		new Object[]{vType, key, addUserToGroup, new Boolean(isHidden)});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.document.VersionInfo groupRemoveUser(VersionableType vType, KeyValue key, String userRemoveFromGroup, boolean isHiddenFromOwner) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.util.document.VersionInfo)remoteCall("groupRemoveUser", new Class[]{VersionableType.class, KeyValue.class, String.class, boolean.class}, 
		new Object[]{vType, key, userRemoveFromGroup, new Boolean(isHiddenFromOwner)});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.document.VersionInfo groupSetPrivate(VersionableType vType, KeyValue key) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.util.document.VersionInfo)remoteCall("groupSetPrivate", new Class[]{VersionableType.class, KeyValue.class}, 
		new Object[]{vType, key});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.document.VersionInfo groupSetPublic(VersionableType vType, KeyValue key) throws cbit.util.DataAccessException, cbit.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.util.document.VersionInfo)remoteCall("groupSetPublic", new Class[]{VersionableType.class, KeyValue.class}, 
		new Object[]{vType, key});
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @param function cbit.vcell.math.Function
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
private Object remoteCall(String methodName, Class[] argClasses, Object[] args) throws java.rmi.RemoteException, cbit.util.DataAccessException, cbit.util.ObjectNotFoundException {
	return remoteCall(anonymizerVCellConnection.getRemoteUserMetaDbServer(), methodName, argClasses, args);
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @param preferences cbit.util.Preference[]
 * @exception java.rmi.RemoteException The exception description.
 */
public void replacePreferences(cbit.util.Preference[] preferences) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	remoteCall("replacePreferences", new Class[]{cbit.util.Preference[].class}, new Object[]{preferences});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveBioModel(cbit.util.BigString bioModelXML, java.lang.String[] independentSims) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveBioModel", new Class[]{cbit.util.BigString.class, String[].class}, 
		new Object[]{bioModelXML, independentSims});

}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveBioModelAs(cbit.util.BigString bioModelXML, String newName, java.lang.String[] independentSims) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveBioModelAs", new Class[]{cbit.util.BigString.class, String.class, String[].class}, 
		new Object[]{bioModelXML, newName, independentSims});

}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveGeometry(cbit.util.BigString geometryXML) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveGeometry", new Class[]{cbit.util.BigString.class}, new Object[]{geometryXML});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveGeometryAs(cbit.util.BigString geometryXML, String newName) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveGeometryAs", new Class[]{cbit.util.BigString.class, String.class}, 
		new Object[]{geometryXML, newName});

}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveMathModel(cbit.util.BigString mathModelXML, java.lang.String[] independentSims) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveMathModel", new Class[]{cbit.util.BigString.class, String[].class}, 
		new Object[]{mathModelXML, independentSims});

}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveMathModelAs(cbit.util.BigString mathModelXML, String newName, java.lang.String[] independentSims) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveMathModelAs", new Class[]{cbit.util.BigString.class, String.class, String[].class}, 
		new Object[]{mathModelXML, newName, independentSims});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveSimulation(cbit.util.BigString simulationXML, boolean forceIndependent) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveSimulation", new Class[]{cbit.util.BigString.class, boolean.class}, new Object[]{simulationXML, new Boolean(forceIndependent)});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveVCImage(cbit.util.BigString vcImageXML) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveVCImage", new Class[]{cbit.util.BigString.class}, new Object[]{vcImageXML});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveVCImageAs(cbit.util.BigString vcImageXML, String newName) throws cbit.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveVCImageAs", new Class[]{cbit.util.BigString.class, String.class}, new Object[]{vcImageXML, newName});
}
}
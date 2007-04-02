package cbit.vcell.anonymizer;
import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.server.ObjectNotFoundException;
import cbit.vcell.server.SessionLog;
import cbit.vcell.simdata.ExternalDataIdentifier;;
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
	super(cbit.vcell.server.PropertyLoader.getIntProperty(cbit.vcell.server.PropertyLoader.rmiPortUserMetaDbServer,0), arg_anonymizerVCellConnection, arg_sessionLog);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.document.VCDocumentInfo curate(cbit.vcell.server.CurateSpec curateSpec) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.document.VCDocumentInfo)remoteCall("curate", new Class[] {cbit.vcell.server.CurateSpec.class}, new Object[] {curateSpec});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteBioModel(cbit.sql.KeyValue bioModelKey) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	remoteCall("deleteBioModel", new Class[] {cbit.sql.KeyValue.class}, new Object[] {bioModelKey});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteExternalDataIdentifiers(cbit.sql.KeyValue[] fdiKeys) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	remoteCall("deleteFieldDataIdentifiers", new Class[] {cbit.sql.KeyValue[].class}, new Object[] {fdiKeys});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteGeometry(cbit.sql.KeyValue geometryKey) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	remoteCall("deleteGeometry", new Class[] {cbit.sql.KeyValue.class}, new Object[] {geometryKey});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteMathModel(cbit.sql.KeyValue mathModelKey) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	remoteCall("deleteMathModel", new Class[] {cbit.sql.KeyValue.class}, new Object[] {mathModelKey});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 */
public void deleteResultSetExport(cbit.sql.KeyValue eleKey) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	remoteCall("deleteResultSetExport", new Class[] {cbit.sql.KeyValue.class}, new Object[] {eleKey});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteVCImage(cbit.sql.KeyValue imageKey) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	remoteCall("deleteVCImage", new Class[] {cbit.sql.KeyValue.class}, new Object[] {imageKey});	
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteOPResults doTestSuiteOP(cbit.vcell.numericstest.TestSuiteOP tsop) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.numericstest.TestSuiteOPResults)remoteCall("doTestSuiteOP", new Class[] {cbit.vcell.numericstest.TestSuiteOP.class}, new Object[] {tsop});	
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.modeldb.ReferenceQueryResult findReferences(cbit.vcell.modeldb.ReferenceQuerySpec rqs) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.modeldb.ReferenceQueryResult)remoteCall("findReferences", new Class[] {cbit.vcell.modeldb.ReferenceQuerySpec.class}, new Object[] {rqs});	
}

public FieldDataDBOperationResults fieldDataDBOperation(FieldDataDBOperationSpec fieldDataDBOperationSpec) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (FieldDataDBOperationResults)remoteCall("fieldDataDBOperation", new Class[] {FieldDataDBOperationSpec.class}, new Object[] {fieldDataDBOperationSpec});	
}

/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.VersionableFamily
 * @param vType cbit.sql.VersionableType
 * @param key cbit.sql.KeyValue
 */
public cbit.vcell.modeldb.VersionableFamily getAllReferences(cbit.sql.VersionableType vType, cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.modeldb.VersionableFamily)remoteCall("getAllReferences", 
		new Class[] {cbit.sql.VersionableType.class, cbit.sql.KeyValue.class}, new Object[] {vType, key});	
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelInfo getBioModelInfo(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.biomodel.BioModelInfo)remoteCall("getBioModelInfo", new Class[] {cbit.sql.KeyValue.class}, new Object[] {key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelInfo[] getBioModelInfos(boolean bAll) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.biomodel.BioModelInfo[])remoteCall("getBioModelInfos", new Class[] {boolean.class}, new Object[] {new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelMetaData getBioModelMetaData(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.biomodel.BioModelMetaData)remoteCall("getBioModelMetaData", new Class[] {cbit.sql.KeyValue.class}, new Object[] {key});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelMetaData[] getBioModelMetaDatas(boolean bAll) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.biomodel.BioModelMetaData[])remoteCall("getBioModelMetaDatas", new Class[] {boolean.class}, new Object[] {new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString getBioModelXML(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("getBioModelXML", new Class[] {cbit.sql.KeyValue.class}, new Object[] {key});
}


/**
 * getBoundSpecies method comment.
 */
public cbit.vcell.dictionary.DBSpecies getBoundSpecies(cbit.vcell.dictionary.DBFormalSpecies dbfs) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.dictionary.DBSpecies)remoteCall("getBoundSpecies", new Class[] {cbit.vcell.dictionary.DBFormalSpecies.class}, new Object[] {dbfs});
}


/**
 * getDatabaseSpecies method comment.
 */
public cbit.vcell.dictionary.DBFormalSpecies[] getDatabaseSpecies(String likeString, boolean isBound, cbit.vcell.dictionary.FormalSpeciesType speciesType, int restrictSearch, int rowLimit, boolean bUserOnly) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.dictionary.DBSpecies[])remoteCall("getDatabaseSpecies", 
		new Class[] {String.class, boolean.class, cbit.vcell.dictionary.FormalSpeciesType.class, int.class, int.class, boolean.class}, 
		new Object[] {likeString, new Boolean(isBound), speciesType, new Integer(restrictSearch), new Integer(rowLimit), new Boolean(bUserOnly)});
}


/**
 * getDictionaryReactions method comment.
 */
public cbit.vcell.dictionary.ReactionDescription[] getDictionaryReactions(cbit.vcell.modeldb.ReactionQuerySpec reactionQuerySpec) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.dictionary.ReactionDescription[])remoteCall("getDictionaryReactions", new Class[] {cbit.vcell.modeldb.ReactionQuerySpec.class}, new Object[] {reactionQuerySpec});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.export.server.ExportLog
 * @param simKey cbit.sql.KeyValue
 */
public cbit.vcell.export.server.ExportLog getExportLog(cbit.sql.KeyValue simulationKey) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.export.server.ExportLog)remoteCall("getExportLog", new Class[] {cbit.sql.KeyValue.class}, new Object[] {simulationKey});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.export.server.ExportLog[]
 * @param simKey cbit.sql.KeyValue
 */
public cbit.vcell.export.server.ExportLog[] getExportLogs(boolean bAll) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.export.server.ExportLog[])remoteCall("getExportLogs", new Class[] {boolean.class}, new Object[] {new Boolean(bAll)});
}

/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo getGeometryInfo(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.geometry.GeometryInfo)remoteCall("getGeometryInfo", new Class[] {cbit.sql.KeyValue.class}, new Object[] {key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo[] getGeometryInfos(boolean bAll) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.geometry.GeometryInfo[])remoteCall("getGeometryInfos", new Class[] {boolean.class}, new Object[] {new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString getGeometryXML(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("getGeometryXML", new Class[] {cbit.sql.KeyValue.class}, new Object[] {key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelInfo getMathModelInfo(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.mathmodel.MathModelInfo)remoteCall("getMathModelInfo", new Class[] {cbit.sql.KeyValue.class}, new Object[] {key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelInfo[] getMathModelInfos(boolean bAll) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.mathmodel.MathModelInfo[])remoteCall("getMathModelInfos", new Class[] {boolean.class}, new Object[] {new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelMetaData getMathModelMetaData(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.mathmodel.MathModelMetaData)remoteCall("getMathModelMetaData", new Class[]{cbit.sql.KeyValue.class}, new Object[]{key});
}


/**
 * This method was created in VisualAge.
 * @return MathModelMetaData[]
 * @param bAll boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelMetaData[] getMathModelMetaDatas(boolean bAll) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.mathmodel.MathModelMetaData[])remoteCall("getMathModelMetaDatas", new Class[]{boolean.class}, new Object[]{new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString getMathModelXML(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("getMathModelXML", new Class[]{cbit.sql.KeyValue.class}, new Object[]{key});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.util.Preference
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.Preference[] getPreferences() throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.Preference[])remoteCall("getPreferences", new Class[]{}, new Object[]{});
}


/**
 * getReactionStep method comment.
 */
public cbit.vcell.model.ReactionStep getReactionStep(cbit.sql.KeyValue rxID) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.model.ReactionStep)remoteCall("getReactionStep", new Class[]{cbit.sql.KeyValue.class}, new Object[]{rxID});
}


/**
 * getReactionStepInfos method comment.
 */
public cbit.vcell.model.ReactionStepInfo[] getReactionStepInfos(cbit.sql.KeyValue[] reactionStepKeys) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.model.ReactionStepInfo[])remoteCall("getReactionStepInfos", new Class[]{cbit.sql.KeyValue[].class}, new Object[]{reactionStepKeys});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
public cbit.vcell.solver.SolverResultSetInfo[] getResultSetInfos(boolean bAll) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.solver.SolverResultSetInfo[])remoteCall("getResultSetInfos", new Class[]{boolean.class}, new Object[]{new Boolean(bAll)});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
public cbit.vcell.solver.ode.gui.SimulationStatus[] getSimulationStatus(cbit.sql.KeyValue[] simulationKeys) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.solver.ode.gui.SimulationStatus[])remoteCall("getSimulationStatus", new Class[]{cbit.sql.KeyValue[].class}, new Object[]{simulationKeys});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
public cbit.vcell.solver.ode.gui.SimulationStatus getSimulationStatus(cbit.sql.KeyValue simulationKey) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.solver.ode.gui.SimulationStatus)remoteCall("getSimulationStatus", new Class[]{cbit.sql.KeyValue.class}, new Object[]{simulationKey});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString getSimulationXML(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("getSimulationXML", new Class[]{cbit.sql.KeyValue.class}, new Object[]{key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.numericstest.TestSuiteNew)remoteCall("getTestSuite", new Class[]{java.math.BigDecimal.class}, new Object[]{getThisTS});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteInfoNew[] getTestSuiteInfos() throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.numericstest.TestSuiteInfoNew[])remoteCall("getTestSuiteInfos", new Class[]{}, new Object[]{});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.dictionary.ReactionDescription[]
 * @param reactionQuerySpec cbit.vcell.modeldb.ReactionQuerySpec
 */
public cbit.vcell.dictionary.ReactionDescription[] getUserReactionDescriptions(cbit.vcell.modeldb.ReactionQuerySpec reactionQuerySpec) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.dictionary.ReactionDescription[])remoteCall("getUserReactionDescriptions", new Class[] {cbit.vcell.modeldb.ReactionQuerySpec.class}, new Object[] {reactionQuerySpec});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo getVCImageInfo(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.image.VCImageInfo)remoteCall("getVCImageInfo", new Class[]{cbit.sql.KeyValue.class}, new Object[]{key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo[] getVCImageInfos(boolean bAll) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.image.VCImageInfo[])remoteCall("getVCImageInfos", new Class[]{boolean.class}, new Object[]{new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString getVCImageXML(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("getVCImageXML", new Class[]{cbit.sql.KeyValue.class}, new Object[]{key});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.modeldb.VCInfoContainer
 */
public cbit.vcell.modeldb.VCInfoContainer getVCInfoContainer() throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.modeldb.VCInfoContainer)remoteCall("getVCInfoContainer", new Class[]{}, new Object[]{});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupAddUser(cbit.sql.VersionableType vType, cbit.sql.KeyValue key, String addUserToGroup, boolean isHidden) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.sql.VersionInfo)remoteCall("groupAddUser", new Class[]{cbit.sql.VersionableType.class, cbit.sql.KeyValue.class, String.class, boolean.class}, 
		new Object[]{vType, key, addUserToGroup, new Boolean(isHidden)});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupRemoveUser(cbit.sql.VersionableType vType, cbit.sql.KeyValue key, String userRemoveFromGroup, boolean isHiddenFromOwner) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.sql.VersionInfo)remoteCall("groupRemoveUser", new Class[]{cbit.sql.VersionableType.class, cbit.sql.KeyValue.class, String.class, boolean.class}, 
		new Object[]{vType, key, userRemoveFromGroup, new Boolean(isHiddenFromOwner)});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupSetPrivate(cbit.sql.VersionableType vType, cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.sql.VersionInfo)remoteCall("groupSetPrivate", new Class[]{cbit.sql.VersionableType.class, cbit.sql.KeyValue.class}, 
		new Object[]{vType, key});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupSetPublic(cbit.sql.VersionableType vType, cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.sql.VersionInfo)remoteCall("groupSetPublic", new Class[]{cbit.sql.VersionableType.class, cbit.sql.KeyValue.class}, 
		new Object[]{vType, key});
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @param function cbit.vcell.math.Function
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
private Object remoteCall(String methodName, Class[] argClasses, Object[] args) throws java.rmi.RemoteException, cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return remoteCall(anonymizerVCellConnection.getRemoteUserMetaDbServer(), methodName, argClasses, args);
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @param preferences cbit.util.Preference[]
 * @exception java.rmi.RemoteException The exception description.
 */
public void replacePreferences(cbit.util.Preference[] preferences) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	remoteCall("replacePreferences", new Class[]{cbit.util.Preference[].class}, new Object[]{preferences});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveBioModel(cbit.util.BigString bioModelXML, java.lang.String[] independentSims) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveBioModel", new Class[]{cbit.util.BigString.class, String[].class}, 
		new Object[]{bioModelXML, independentSims});

}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveBioModelAs(cbit.util.BigString bioModelXML, String newName, java.lang.String[] independentSims) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveBioModelAs", new Class[]{cbit.util.BigString.class, String.class, String[].class}, 
		new Object[]{bioModelXML, newName, independentSims});

}

/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveGeometry(cbit.util.BigString geometryXML) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveGeometry", new Class[]{cbit.util.BigString.class}, new Object[]{geometryXML});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveGeometryAs(cbit.util.BigString geometryXML, String newName) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveGeometryAs", new Class[]{cbit.util.BigString.class, String.class}, 
		new Object[]{geometryXML, newName});

}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveMathModel(cbit.util.BigString mathModelXML, java.lang.String[] independentSims) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveMathModel", new Class[]{cbit.util.BigString.class, String[].class}, 
		new Object[]{mathModelXML, independentSims});

}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveMathModelAs(cbit.util.BigString mathModelXML, String newName, java.lang.String[] independentSims) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveMathModelAs", new Class[]{cbit.util.BigString.class, String.class, String[].class}, 
		new Object[]{mathModelXML, newName, independentSims});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveSimulation(cbit.util.BigString simulationXML, boolean forceIndependent) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveSimulation", new Class[]{cbit.util.BigString.class, boolean.class}, new Object[]{simulationXML, new Boolean(forceIndependent)});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveVCImage(cbit.util.BigString vcImageXML) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveVCImage", new Class[]{cbit.util.BigString.class}, new Object[]{vcImageXML});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.BigString saveVCImageAs(cbit.util.BigString vcImageXML, String newName) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.BigString)remoteCall("saveVCImageAs", new Class[]{cbit.util.BigString.class, String.class}, new Object[]{vcImageXML, newName});
}
}
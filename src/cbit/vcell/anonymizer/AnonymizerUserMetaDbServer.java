package cbit.vcell.anonymizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.SessionLog;

import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.server.UserRegistrationOP;
import cbit.vcell.server.UserRegistrationResults;
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
	super(org.vcell.util.PropertyLoader.getIntProperty(org.vcell.util.PropertyLoader.rmiPortUserMetaDbServer,0), arg_anonymizerVCellConnection, arg_sessionLog);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.document.VCDocumentInfo curate(cbit.vcell.server.CurateSpec curateSpec) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.document.VCDocumentInfo)remoteCall("curate", new Class[] {cbit.vcell.server.CurateSpec.class}, new Object[] {curateSpec});	
}

public UserRegistrationResults userRegistrationOP(UserRegistrationOP userRegistrationOP) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (UserRegistrationResults)remoteCall("userRegistrationOP", new Class[] {UserRegistrationOP.class}, new Object[] {userRegistrationOP});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteBioModel(org.vcell.util.document.KeyValue bioModelKey) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	remoteCall("deleteBioModel", new Class[] {org.vcell.util.document.KeyValue.class}, new Object[] {bioModelKey});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteExternalDataIdentifiers(org.vcell.util.document.KeyValue[] fdiKeys) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	remoteCall("deleteFieldDataIdentifiers", new Class[] {org.vcell.util.document.KeyValue[].class}, new Object[] {fdiKeys});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteGeometry(org.vcell.util.document.KeyValue geometryKey) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	remoteCall("deleteGeometry", new Class[] {org.vcell.util.document.KeyValue.class}, new Object[] {geometryKey});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteMathModel(org.vcell.util.document.KeyValue mathModelKey) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	remoteCall("deleteMathModel", new Class[] {org.vcell.util.document.KeyValue.class}, new Object[] {mathModelKey});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 */
public void deleteResultSetExport(org.vcell.util.document.KeyValue eleKey) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	remoteCall("deleteResultSetExport", new Class[] {org.vcell.util.document.KeyValue.class}, new Object[] {eleKey});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteVCImage(org.vcell.util.document.KeyValue imageKey) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	remoteCall("deleteVCImage", new Class[] {org.vcell.util.document.KeyValue.class}, new Object[] {imageKey});	
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteOPResults doTestSuiteOP(cbit.vcell.numericstest.TestSuiteOP tsop) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.numericstest.TestSuiteOPResults)remoteCall("doTestSuiteOP", new Class[] {cbit.vcell.numericstest.TestSuiteOP.class}, new Object[] {tsop});	
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.ReferenceQueryResult findReferences(org.vcell.util.ReferenceQuerySpec rqs) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (org.vcell.util.ReferenceQueryResult)remoteCall("findReferences", new Class[] {org.vcell.util.ReferenceQuerySpec.class}, new Object[] {rqs});	
}

public FieldDataDBOperationResults fieldDataDBOperation(FieldDataDBOperationSpec fieldDataDBOperationSpec) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (FieldDataDBOperationResults)remoteCall("fieldDataDBOperation", new Class[] {FieldDataDBOperationSpec.class}, new Object[] {fieldDataDBOperationSpec});	
}

/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.VersionableFamily
 * @param vType cbit.sql.VersionableType
 * @param key cbit.sql.KeyValue
 */
public cbit.vcell.modeldb.VersionableFamily getAllReferences(cbit.sql.VersionableType vType, org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.modeldb.VersionableFamily)remoteCall("getAllReferences", 
		new Class[] {cbit.sql.VersionableType.class, org.vcell.util.document.KeyValue.class}, new Object[] {vType, key});	
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelInfo getBioModelInfo(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.biomodel.BioModelInfo)remoteCall("getBioModelInfo", new Class[] {org.vcell.util.document.KeyValue.class}, new Object[] {key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelInfo[] getBioModelInfos(boolean bAll) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.biomodel.BioModelInfo[])remoteCall("getBioModelInfos", new Class[] {boolean.class}, new Object[] {new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelMetaData getBioModelMetaData(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.biomodel.BioModelMetaData)remoteCall("getBioModelMetaData", new Class[] {org.vcell.util.document.KeyValue.class}, new Object[] {key});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelMetaData[] getBioModelMetaDatas(boolean bAll) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.biomodel.BioModelMetaData[])remoteCall("getBioModelMetaDatas", new Class[] {boolean.class}, new Object[] {new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.BigString getBioModelXML(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (org.vcell.util.BigString)remoteCall("getBioModelXML", new Class[] {org.vcell.util.document.KeyValue.class}, new Object[] {key});
}


/**
 * getBoundSpecies method comment.
 */
public cbit.vcell.dictionary.DBSpecies getBoundSpecies(cbit.vcell.dictionary.DBFormalSpecies dbfs) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.dictionary.DBSpecies)remoteCall("getBoundSpecies", new Class[] {cbit.vcell.dictionary.DBFormalSpecies.class}, new Object[] {dbfs});
}


/**
 * getDatabaseSpecies method comment.
 */
public cbit.vcell.dictionary.DBFormalSpecies[] getDatabaseSpecies(String likeString, boolean isBound, cbit.vcell.dictionary.FormalSpeciesType speciesType, int restrictSearch, int rowLimit, boolean bUserOnly) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.dictionary.DBSpecies[])remoteCall("getDatabaseSpecies", 
		new Class[] {String.class, boolean.class, cbit.vcell.dictionary.FormalSpeciesType.class, int.class, int.class, boolean.class}, 
		new Object[] {likeString, new Boolean(isBound), speciesType, new Integer(restrictSearch), new Integer(rowLimit), new Boolean(bUserOnly)});
}


/**
 * getDictionaryReactions method comment.
 */
public cbit.vcell.dictionary.ReactionDescription[] getDictionaryReactions(cbit.vcell.modeldb.ReactionQuerySpec reactionQuerySpec) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.dictionary.ReactionDescription[])remoteCall("getDictionaryReactions", new Class[] {cbit.vcell.modeldb.ReactionQuerySpec.class}, new Object[] {reactionQuerySpec});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.export.server.ExportLog
 * @param simKey cbit.sql.KeyValue
 */
public cbit.vcell.export.server.ExportLog getExportLog(org.vcell.util.document.KeyValue simulationKey) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.export.server.ExportLog)remoteCall("getExportLog", new Class[] {org.vcell.util.document.KeyValue.class}, new Object[] {simulationKey});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.export.server.ExportLog[]
 * @param simKey cbit.sql.KeyValue
 */
public cbit.vcell.export.server.ExportLog[] getExportLogs(boolean bAll) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.export.server.ExportLog[])remoteCall("getExportLogs", new Class[] {boolean.class}, new Object[] {new Boolean(bAll)});
}

/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo getGeometryInfo(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.geometry.GeometryInfo)remoteCall("getGeometryInfo", new Class[] {org.vcell.util.document.KeyValue.class}, new Object[] {key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo[] getGeometryInfos(boolean bAll) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.geometry.GeometryInfo[])remoteCall("getGeometryInfos", new Class[] {boolean.class}, new Object[] {new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.BigString getGeometryXML(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (org.vcell.util.BigString)remoteCall("getGeometryXML", new Class[] {org.vcell.util.document.KeyValue.class}, new Object[] {key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelInfo getMathModelInfo(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.mathmodel.MathModelInfo)remoteCall("getMathModelInfo", new Class[] {org.vcell.util.document.KeyValue.class}, new Object[] {key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelInfo[] getMathModelInfos(boolean bAll) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.mathmodel.MathModelInfo[])remoteCall("getMathModelInfos", new Class[] {boolean.class}, new Object[] {new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelMetaData getMathModelMetaData(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.mathmodel.MathModelMetaData)remoteCall("getMathModelMetaData", new Class[]{org.vcell.util.document.KeyValue.class}, new Object[]{key});
}


/**
 * This method was created in VisualAge.
 * @return MathModelMetaData[]
 * @param bAll boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelMetaData[] getMathModelMetaDatas(boolean bAll) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.mathmodel.MathModelMetaData[])remoteCall("getMathModelMetaDatas", new Class[]{boolean.class}, new Object[]{new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.BigString getMathModelXML(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (org.vcell.util.BigString)remoteCall("getMathModelXML", new Class[]{org.vcell.util.document.KeyValue.class}, new Object[]{key});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.util.Preference
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.Preference[] getPreferences() throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (org.vcell.util.Preference[])remoteCall("getPreferences", new Class[]{}, new Object[]{});
}


/**
 * getReactionStep method comment.
 */
public cbit.vcell.model.ReactionStep getReactionStep(org.vcell.util.document.KeyValue rxID) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.model.ReactionStep)remoteCall("getReactionStep", new Class[]{org.vcell.util.document.KeyValue.class}, new Object[]{rxID});
}


/**
 * getReactionStepInfos method comment.
 */
public cbit.vcell.model.ReactionStepInfo[] getReactionStepInfos(org.vcell.util.document.KeyValue[] reactionStepKeys) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.model.ReactionStepInfo[])remoteCall("getReactionStepInfos", new Class[]{org.vcell.util.document.KeyValue[].class}, new Object[]{reactionStepKeys});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
public cbit.vcell.solver.SolverResultSetInfo[] getResultSetInfos(boolean bAll) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.solver.SolverResultSetInfo[])remoteCall("getResultSetInfos", new Class[]{boolean.class}, new Object[]{new Boolean(bAll)});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
public cbit.vcell.solver.ode.gui.SimulationStatus[] getSimulationStatus(org.vcell.util.document.KeyValue[] simulationKeys) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.solver.ode.gui.SimulationStatus[])remoteCall("getSimulationStatus", new Class[]{org.vcell.util.document.KeyValue[].class}, new Object[]{simulationKeys});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
public cbit.vcell.solver.ode.gui.SimulationStatus getSimulationStatus(org.vcell.util.document.KeyValue simulationKey) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.solver.ode.gui.SimulationStatus)remoteCall("getSimulationStatus", new Class[]{org.vcell.util.document.KeyValue.class}, new Object[]{simulationKey});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.BigString getSimulationXML(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (org.vcell.util.BigString)remoteCall("getSimulationXML", new Class[]{org.vcell.util.document.KeyValue.class}, new Object[]{key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.numericstest.TestSuiteNew)remoteCall("getTestSuite", new Class[]{java.math.BigDecimal.class}, new Object[]{getThisTS});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteInfoNew[] getTestSuiteInfos() throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.numericstest.TestSuiteInfoNew[])remoteCall("getTestSuiteInfos", new Class[]{}, new Object[]{});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.dictionary.ReactionDescription[]
 * @param reactionQuerySpec cbit.vcell.modeldb.ReactionQuerySpec
 */
public cbit.vcell.dictionary.ReactionDescription[] getUserReactionDescriptions(cbit.vcell.modeldb.ReactionQuerySpec reactionQuerySpec) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.dictionary.ReactionDescription[])remoteCall("getUserReactionDescriptions", new Class[] {cbit.vcell.modeldb.ReactionQuerySpec.class}, new Object[] {reactionQuerySpec});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo getVCImageInfo(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.image.VCImageInfo)remoteCall("getVCImageInfo", new Class[]{org.vcell.util.document.KeyValue.class}, new Object[]{key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo[] getVCImageInfos(boolean bAll) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.image.VCImageInfo[])remoteCall("getVCImageInfos", new Class[]{boolean.class}, new Object[]{new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.BigString getVCImageXML(org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (org.vcell.util.BigString)remoteCall("getVCImageXML", new Class[]{org.vcell.util.document.KeyValue.class}, new Object[]{key});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @return cbit.vcell.modeldb.VCInfoContainer
 */
public cbit.vcell.modeldb.VCInfoContainer getVCInfoContainer() throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.modeldb.VCInfoContainer)remoteCall("getVCInfoContainer", new Class[]{}, new Object[]{});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupAddUser(cbit.sql.VersionableType vType, org.vcell.util.document.KeyValue key, String addUserToGroup, boolean isHidden) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.sql.VersionInfo)remoteCall("groupAddUser", new Class[]{cbit.sql.VersionableType.class, org.vcell.util.document.KeyValue.class, String.class, boolean.class}, 
		new Object[]{vType, key, addUserToGroup, new Boolean(isHidden)});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupRemoveUser(cbit.sql.VersionableType vType, org.vcell.util.document.KeyValue key, String userRemoveFromGroup, boolean isHiddenFromOwner) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.sql.VersionInfo)remoteCall("groupRemoveUser", new Class[]{cbit.sql.VersionableType.class, org.vcell.util.document.KeyValue.class, String.class, boolean.class}, 
		new Object[]{vType, key, userRemoveFromGroup, new Boolean(isHiddenFromOwner)});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupSetPrivate(cbit.sql.VersionableType vType, org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.sql.VersionInfo)remoteCall("groupSetPrivate", new Class[]{cbit.sql.VersionableType.class, org.vcell.util.document.KeyValue.class}, 
		new Object[]{vType, key});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupSetPublic(cbit.sql.VersionableType vType, org.vcell.util.document.KeyValue key) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.sql.VersionInfo)remoteCall("groupSetPublic", new Class[]{cbit.sql.VersionableType.class, org.vcell.util.document.KeyValue.class}, 
		new Object[]{vType, key});
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @param function cbit.vcell.math.Function
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
private Object remoteCall(String methodName, Class[] argClasses, Object[] args) throws java.rmi.RemoteException, org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return remoteCall(anonymizerVCellConnection.getRemoteUserMetaDbServer(), methodName, argClasses, args);
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2006 10:57:49 PM)
 * @param preferences cbit.util.Preference[]
 * @exception java.rmi.RemoteException The exception description.
 */
public void replacePreferences(org.vcell.util.Preference[] preferences) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	remoteCall("replacePreferences", new Class[]{org.vcell.util.Preference[].class}, new Object[]{preferences});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.BigString saveBioModel(org.vcell.util.BigString bioModelXML, java.lang.String[] independentSims) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (org.vcell.util.BigString)remoteCall("saveBioModel", new Class[]{org.vcell.util.BigString.class, String[].class}, 
		new Object[]{bioModelXML, independentSims});

}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.BigString saveBioModelAs(org.vcell.util.BigString bioModelXML, String newName, java.lang.String[] independentSims) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (org.vcell.util.BigString)remoteCall("saveBioModelAs", new Class[]{org.vcell.util.BigString.class, String.class, String[].class}, 
		new Object[]{bioModelXML, newName, independentSims});

}

/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.BigString saveGeometry(org.vcell.util.BigString geometryXML) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (org.vcell.util.BigString)remoteCall("saveGeometry", new Class[]{org.vcell.util.BigString.class}, new Object[]{geometryXML});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.BigString saveGeometryAs(org.vcell.util.BigString geometryXML, String newName) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (org.vcell.util.BigString)remoteCall("saveGeometryAs", new Class[]{org.vcell.util.BigString.class, String.class}, 
		new Object[]{geometryXML, newName});

}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.BigString saveMathModel(org.vcell.util.BigString mathModelXML, java.lang.String[] independentSims) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (org.vcell.util.BigString)remoteCall("saveMathModel", new Class[]{org.vcell.util.BigString.class, String[].class}, 
		new Object[]{mathModelXML, independentSims});

}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.BigString saveMathModelAs(org.vcell.util.BigString mathModelXML, String newName, java.lang.String[] independentSims) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (org.vcell.util.BigString)remoteCall("saveMathModelAs", new Class[]{org.vcell.util.BigString.class, String.class, String[].class}, 
		new Object[]{mathModelXML, newName, independentSims});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.BigString saveSimulation(org.vcell.util.BigString simulationXML, boolean forceIndependent) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (org.vcell.util.BigString)remoteCall("saveSimulation", new Class[]{org.vcell.util.BigString.class, boolean.class}, new Object[]{simulationXML, new Boolean(forceIndependent)});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.BigString saveVCImage(org.vcell.util.BigString vcImageXML) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (org.vcell.util.BigString)remoteCall("saveVCImage", new Class[]{org.vcell.util.BigString.class}, new Object[]{vcImageXML});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.BigString saveVCImageAs(org.vcell.util.BigString vcImageXML, String newName) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (org.vcell.util.BigString)remoteCall("saveVCImageAs", new Class[]{org.vcell.util.BigString.class, String.class}, new Object[]{vcImageXML, newName});
}
}
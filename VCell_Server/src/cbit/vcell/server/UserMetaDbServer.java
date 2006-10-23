package cbit.vcell.server;
import java.rmi.RemoteException;

import cbit.util.BigString;
import cbit.util.DataAccessException;
import cbit.util.ObjectNotFoundException;
import cbit.util.ReferenceQueryResult;
import cbit.util.ReferenceQuerySpec;
import cbit.util.document.CurateSpec;
import cbit.util.document.KeyValue;
import cbit.util.document.MathModelInfo;
import cbit.util.document.VersionableFamily;
import cbit.util.document.VersionableType;
import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.dictionary.DBFormalSpecies;
import cbit.vcell.dictionary.DBSpecies;
import cbit.vcell.dictionary.FormalSpeciesType;
import cbit.vcell.dictionary.ReactionQuerySpec;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.modeldb.SolverResultSetInfo;
import cbit.vcell.modeldb.VCInfoContainer;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestSuiteNew;
import cbit.vcell.numericstest.TestSuiteOP;
import cbit.vcell.numericstest.TestSuiteOPResults;
import cbit.vcell.solvers.SimulationStatus;

/**
 * This type was created in VisualAge.
 */
public interface UserMetaDbServer extends java.rmi.Remote {
/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 9:42:56 AM)
 */
cbit.util.document.VCDocumentInfo curate(CurateSpec curateSpec) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 1:03:11 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
void deleteBioModel(KeyValue bioModelKey) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 1:03:11 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
void deleteGeometry(KeyValue geometryKey) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 1:03:11 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
void deleteMathModel(KeyValue mathModelKey) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 5:37:35 PM)
 */
public void deleteResultSetExport(cbit.util.document.KeyValue eleKey) throws DataAccessException,java.rmi.RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 1:03:11 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
void deleteVCImage(KeyValue imageKey) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
TestSuiteOPResults doTestSuiteOP(TestSuiteOP tsop) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
ReferenceQueryResult findReferences(ReferenceQuerySpec rqs) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.VersionableFamily
 * @param vType cbit.sql.VersionableType
 * @param key cbit.sql.KeyValue
 */
VersionableFamily getAllReferences(VersionableType vType, KeyValue key) throws RemoteException, DataAccessException, ObjectNotFoundException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.util.document.BioModelInfo getBioModelInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.util.document.BioModelInfo[] getBioModelInfos(boolean bAll) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BioModelMetaData getBioModelMetaData(KeyValue key) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BioModelMetaData[] getBioModelMetaDatas(boolean bAll) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString getBioModelXML(KeyValue key) throws DataAccessException, java.rmi.RemoteException;


public DBSpecies getBoundSpecies(DBFormalSpecies dbfs) throws DataAccessException, java.rmi.RemoteException;


public DBFormalSpecies[] getDatabaseSpecies(String likeString,boolean isBound,FormalSpeciesType speciesType,int restrictSearch,int rowLimit, boolean bUserOnly) throws DataAccessException, java.rmi.RemoteException;


public cbit.vcell.dictionary.ReactionDescription[] getDictionaryReactions(cbit.vcell.dictionary.ReactionQuerySpec reactionQuerySpec) throws DataAccessException, java.rmi.RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (2/4/01 5:19:25 PM)
 * @return cbit.vcell.export.server.ExportLog
 * @param simKey cbit.sql.KeyValue
 */
cbit.vcell.export.ExportLog getExportLog(KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException, RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (2/4/01 5:19:25 PM)
 * @return cbit.vcell.export.server.ExportLog[]
 * @param simKey cbit.sql.KeyValue
 */
cbit.vcell.export.ExportLog[] getExportLogs(boolean bAll) throws DataAccessException, RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.vcell.geometry.GeometryInfo getGeometryInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.vcell.geometry.GeometryInfo[] getGeometryInfos(boolean bAll) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString getGeometryXML(KeyValue key) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
MathModelInfo getMathModelInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
MathModelInfo[] getMathModelInfos(boolean bAll) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
MathModelMetaData getMathModelMetaData(KeyValue key) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return MathModelMetaData[]
 * @param bAll boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
MathModelMetaData[] getMathModelMetaDatas(boolean bAll) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString getMathModelXML(KeyValue key) throws DataAccessException, java.rmi.RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:29:11 PM)
 * @return cbit.util.Preference
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.util.Preference[] getPreferences() throws DataAccessException, java.rmi.RemoteException;


public cbit.vcell.model.ReactionStep getReactionStep(KeyValue rxID) throws DataAccessException, java.rmi.RemoteException;


public cbit.vcell.model.ReactionStepInfo[] getReactionStepInfos(KeyValue reactionStepKeys[]) throws DataAccessException, java.rmi.RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (2/4/01 5:19:25 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
cbit.vcell.modeldb.SolverResultSetInfo[] getResultSetInfos(boolean bAll) throws DataAccessException, RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (2/4/01 5:19:25 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
SimulationStatus[] getSimulationStatus(KeyValue simulationKeys[]) throws DataAccessException, RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (2/4/01 5:19:25 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
SimulationStatus getSimulationStatus(KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException, RemoteException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString getSimulationXML(KeyValue key) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
TestSuiteInfoNew[] getTestSuiteInfos() throws DataAccessException, java.rmi.RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 5:05:56 PM)
 * @return cbit.vcell.dictionary.ReactionDescription[]
 * @param reactionQuerySpec cbit.vcell.modeldb.ReactionQuerySpec
 */
cbit.vcell.dictionary.ReactionDescription[] getUserReactionDescriptions(cbit.vcell.dictionary.ReactionQuerySpec reactionQuerySpec) throws DataAccessException, RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.image.VCImageInfo getVCImageInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.image.VCImageInfo[] getVCImageInfos(boolean bAll) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString getVCImageXML(KeyValue key) throws DataAccessException, java.rmi.RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (9/25/2003 7:52:33 AM)
 * @return cbit.vcell.modeldb.VCInfoContainer
 */
cbit.vcell.modeldb.VCInfoContainer getVCInfoContainer() throws DataAccessException, RemoteException;


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.util.document.VersionInfo groupAddUser(VersionableType vType, KeyValue key,String addUserToGroup,boolean isHidden) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.util.document.VersionInfo groupRemoveUser(VersionableType vType, KeyValue key,String userRemoveFromGroup,boolean isHiddenFromOwner) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.util.document.VersionInfo groupSetPrivate(VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.util.document.VersionInfo groupSetPublic(VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:28:18 PM)
 * @param preferences cbit.util.Preference[]
 * @exception java.rmi.RemoteException The exception description.
 */
void replacePreferences(cbit.util.Preference[] preferences) throws java.rmi.RemoteException, DataAccessException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveBioModel(BigString bioModelXML, String independentSims[]) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveBioModelAs(BigString bioModelXML, String newName, String independentSims[]) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveGeometry(BigString geometryXML) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveGeometryAs(BigString geometryXML, String newName) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveMathModel(BigString mathModelXML, String independentSims[]) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveMathModelAs(BigString mathModelXML, String newName, String independentSims[]) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveSimulation(BigString simulationXML, boolean forceIndependent) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveVCImage(BigString vcImageXML) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveVCImageAs(BigString vcImageXML, String newName) throws DataAccessException, java.rmi.RemoteException;
}
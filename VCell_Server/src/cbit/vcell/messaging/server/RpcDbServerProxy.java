package cbit.vcell.messaging.server;
import cbit.util.BigString;
import cbit.util.DataAccessException;
import cbit.util.ObjectNotFoundException;
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
import cbit.vcell.dictionary.ReactionQuerySpec;
import cbit.vcell.export.ExportLog;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.messaging.JmsClientMessaging;
import cbit.vcell.messaging.JmsUtils;
import cbit.vcell.modeldb.SolverResultSetInfo;
import cbit.vcell.modeldb.VCInfoContainer;
import cbit.vcell.server.UserMetaDbServer;
import cbit.vcell.solvers.SimulationStatus;

/**
 * Insert the type's description here.
 * Creation date: (12/5/2001 12:00:10 PM)
 * @author: Jim Schaff
 *
 * stateless database service for any user (should be thread safe ... reentrant)
 *
 */
public class RpcDbServerProxy extends AbstractRpcServerProxy implements UserMetaDbServer {
/**
 * DataServerProxy constructor comment.
 */
public RpcDbServerProxy(User argUser, JmsClientMessaging clientMessaging, SessionLog log) throws javax.jms.JMSException {
	super(argUser, clientMessaging, JmsUtils.getQueueDbReq(), log);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VCDocumentInfo curate(CurateSpec curateSpec) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException {
	return (VCDocumentInfo)rpc("curate",new Object[]{user,curateSpec});
}


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 4:31:48 PM)
 * @param bioModelKey KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteBioModel(KeyValue bioModelKey) throws DataAccessException, ObjectNotFoundException {
	rpc("deleteBioModel",new Object[]{user, bioModelKey});
}


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 4:31:48 PM)
 * @param bioModelKey KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteGeometry(KeyValue geometryKey) throws DataAccessException, ObjectNotFoundException {
	rpc("deleteGeometry",new Object[]{user, geometryKey});
}


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 4:31:48 PM)
 * @param bioModelKey KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteMathModel(KeyValue mathModelKey) throws DataAccessException, ObjectNotFoundException {
	rpc("deleteMathModel",new Object[]{user, mathModelKey});
}


/**
* Insert the method's description here.
* Creation date: (10/22/2003 10:28:00 AM)
*/
public void deleteResultSetExport(KeyValue eleKey) throws DataAccessException {
	rpc("deleteResultSetExport",new Object[]{user, eleKey});
}


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 4:31:48 PM)
 * @param bioModelKey KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteVCImage(KeyValue imageKey) throws DataAccessException, ObjectNotFoundException {
	rpc("deleteVCImage",new Object[]{user, imageKey});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteOPResults doTestSuiteOP(cbit.vcell.numericstest.TestSuiteOP tsop) throws DataAccessException, java.rmi.RemoteException {

	return (cbit.vcell.numericstest.TestSuiteOPResults ) rpc("doTestSuiteOP",new Object[] {user,tsop});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public ReferenceQueryResult findReferences(ReferenceQuerySpec rqs) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException {
	return (ReferenceQueryResult)rpc("findReferences",new Object[]{user,rqs});
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.VersionableFamily
 * @param vType VersionableType
 * @param key KeyValue
 */
public VersionableFamily getAllReferences(VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (VersionableFamily)rpc("getAllReferences",new Object[]{user, vType,key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.document.BioModelInfo getBioModelInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (cbit.util.document.BioModelInfo)rpc("getBioModelInfo",new Object[]{user,key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.document.BioModelInfo[] getBioModelInfos(boolean bAll) throws DataAccessException {
	return (cbit.util.document.BioModelInfo[])rpc("getBioModelInfos",new Object[]{user, new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelMetaData getBioModelMetaData(KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (BioModelMetaData)rpc("getBioModelMetaData",new Object[]{user, key});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelMetaData[] getBioModelMetaDatas(boolean bAll) throws DataAccessException {
	return (BioModelMetaData[])rpc("getBioModelMetaDatas",new Object[]{user, new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getBioModelXML(KeyValue key) throws DataAccessException {
	return (BigString)rpc("getBioModelXML",new Object[]{user, key});
}


/**
 * getBoundSpecies method comment.
 */
public cbit.vcell.dictionary.DBSpecies getBoundSpecies(cbit.vcell.dictionary.DBFormalSpecies dbfs) throws DataAccessException {
	return (cbit.vcell.dictionary.DBSpecies)rpc("getBoundSpecies",new Object[]{user, dbfs});
}


/**
 * getDatabaseSpecies method comment.
 */
public cbit.vcell.dictionary.DBFormalSpecies[] getDatabaseSpecies(java.lang.String likeString, boolean isBound, cbit.vcell.dictionary.FormalSpeciesType speciesType, int restrictSearch, int rowLimit, boolean bOnlyUser) throws DataAccessException {
	return (cbit.vcell.dictionary.DBFormalSpecies[])rpc("getDatabaseSpecies",new Object[]{user, likeString,new Boolean(isBound),speciesType,new Integer(restrictSearch),new Integer(rowLimit), new Boolean(bOnlyUser)});
}


/**
 * getDictionaryReactions method comment.
 */
public cbit.vcell.dictionary.ReactionDescription[] getDictionaryReactions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException {
	return (cbit.vcell.dictionary.ReactionDescription[])rpc("getDictionaryReactions",new Object[]{user, reactionQuerySpec});
}


/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 12:00:10 PM)
 * @return ExportLog
 * @param simKey KeyValue
 */
public ExportLog getExportLog(KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException {
	return (ExportLog)rpc("getExportLog",new Object[]{user, simulationKey});
}


/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 12:00:10 PM)
 * @return ExportLog[]
 * @param simKey KeyValue
 */
public ExportLog[] getExportLogs(boolean bAll) throws DataAccessException {
	return (ExportLog[])rpc("getExportLogs",new Object[]{user, new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo getGeometryInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (cbit.vcell.geometry.GeometryInfo)rpc("getGeometryInfo",new Object[]{user,key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo[] getGeometryInfos(boolean bAll) throws DataAccessException {
	return (cbit.vcell.geometry.GeometryInfo[])rpc("getGeometryInfos",new Object[]{user, new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getGeometryXML(KeyValue key) throws DataAccessException {
	return (BigString)rpc("getGeometryXML",new Object[]{user, key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.document.MathModelInfo getMathModelInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (cbit.util.document.MathModelInfo)rpc("getMathModelInfo",new Object[]{user,key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.document.MathModelInfo[] getMathModelInfos(boolean bAll) throws DataAccessException {
	return (cbit.util.document.MathModelInfo[])rpc("getMathModelInfos",new Object[]{user, new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelMetaData getMathModelMetaData(KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (MathModelMetaData)rpc("getMathModelMetaData",new Object[]{user, key});
}


/**
 * This method was created in VisualAge.
 * @return MathModelMetaData[]
 * @param bAll boolean
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelMetaData[] getMathModelMetaDatas(boolean bAll) throws DataAccessException {
	return (MathModelMetaData[])rpc("getMathModelMetaDatas",new Object[]{user, new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getMathModelXML(KeyValue key) throws DataAccessException {
	return (BigString)rpc("getMathModelXML",new Object[]{user, key});
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:54:49 PM)
 * @return cbit.util.Preference
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.Preference[] getPreferences() throws DataAccessException {
	return (cbit.util.Preference[])rpc("getPreferences",new Object[]{user});
}


/**
 * getReactionStep method comment.
 */
public cbit.vcell.model.ReactionStep getReactionStep(KeyValue rxID) throws DataAccessException {
	return (cbit.vcell.model.ReactionStep)rpc("getReactionStep",new Object[]{user, rxID});
}


/**
 * getReactionStepInfos method comment.
 */
public cbit.vcell.model.ReactionStepInfo[] getReactionStepInfos(KeyValue[] reactionStepKeys) throws DataAccessException {
	return (cbit.vcell.model.ReactionStepInfo[])rpc("getReactionStepInfos",new Object[]{user, reactionStepKeys});
}


/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 12:00:10 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey KeyValue
 */
public SolverResultSetInfo[] getResultSetInfos(boolean bAll) throws DataAccessException {
	return (SolverResultSetInfo[])rpc("getResultSetInfos",new Object[]{user, new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationStatus[] getSimulationStatus(KeyValue simulationKeys[]) throws DataAccessException, ObjectNotFoundException {
	return (SimulationStatus[])rpc("getSimulationStatus",new Object[]{simulationKeys});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationStatus getSimulationStatus(KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException {
	return (SimulationStatus)rpc("getSimulationStatus",new Object[]{simulationKey});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getSimulationXML(KeyValue key) throws DataAccessException {
	return (BigString)rpc("getSimulationXML",new Object[]{user, key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws DataAccessException, java.rmi.RemoteException {

	return (cbit.vcell.numericstest.TestSuiteNew ) rpc("getTestSuite",new Object[] {user,getThisTS});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteInfoNew[] getTestSuiteInfos() throws DataAccessException, java.rmi.RemoteException {

	return (cbit.vcell.numericstest.TestSuiteInfoNew[] ) rpc("getTestSuiteInfos",new Object[] {user});
}


/**
 * getUserReactionDescriptions method comment.
 */
public cbit.vcell.dictionary.ReactionDescription[] getUserReactionDescriptions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException {
	return (cbit.vcell.dictionary.ReactionDescription[])rpc("getUserReactionDescriptions",new Object[]{user, reactionQuerySpec});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo getVCImageInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (cbit.image.VCImageInfo)rpc("getVCImageInfo",new Object[]{user,key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo[] getVCImageInfos(boolean bAll)
    throws DataAccessException {
    return (cbit.image.VCImageInfo[]) rpc(
        "getVCImageInfos",
        new Object[] { user, new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getVCImageXML(KeyValue key) throws DataAccessException {
	return (BigString)rpc("getVCImageXML",new Object[]{user, key});
}


/**
 * getVCInfoContainer method comment.
 */
public VCInfoContainer getVCInfoContainer() throws DataAccessException {
	return (VCInfoContainer)rpc("getVCInfoContainer",new Object[]{user});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VersionInfo groupAddUser(VersionableType vType, KeyValue key, java.lang.String addUserToGroup, boolean isHidden) throws DataAccessException, ObjectNotFoundException {
	return (VersionInfo)rpc("groupAddUser",new Object[]{user, vType,key,addUserToGroup,new Boolean(isHidden)});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VersionInfo groupRemoveUser(VersionableType vType, KeyValue key, java.lang.String userRemoveFromGroup, boolean isHiddenFromOwner) throws DataAccessException, ObjectNotFoundException {
	return (VersionInfo)rpc("groupRemoveUser",new Object[]{user, vType,key,userRemoveFromGroup,new Boolean(isHiddenFromOwner)});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VersionInfo groupSetPrivate(VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (VersionInfo)rpc("groupSetPrivate",new Object[]{user, vType,key});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public VersionInfo groupSetPublic(VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException {
	return (VersionInfo)rpc("groupSetPublic",new Object[]{user, vType,key});
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:54:49 PM)
 * @param preferences cbit.util.Preference[]
 * @exception java.rmi.RemoteException The exception description.
 */
public void replacePreferences(cbit.util.Preference[] preferences) throws DataAccessException {
	rpc("replacePreferences",new Object[]{user, preferences});
}


/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 9:39:03 PM)
 * @return java.lang.Object
 * @param methodName java.lang.String
 * @param args java.lang.Object[]
 * @exception java.lang.Exception The exception description.
 */
private Object rpc(String methodName, Object[] args) throws ObjectNotFoundException, DataAccessException {
	try {
		return rpc(cbit.util.MessageConstants.SERVICETYPE_DB_VALUE, methodName, args, true);
	} catch (ObjectNotFoundException ex) {
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
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveBioModel(BigString bioModelXML, String independentSims[]) throws DataAccessException, ObjectNotFoundException {
	return (BigString)rpc("saveBioModel",new Object[]{user, bioModelXML, independentSims});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveBioModelAs(BigString bioModelXML, java.lang.String newName, String independentSims[]) throws DataAccessException, ObjectNotFoundException {
	return (BigString)rpc("saveBioModelAs",new Object[]{user, bioModelXML, newName, independentSims});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveGeometry(BigString geometryXML) throws DataAccessException, ObjectNotFoundException {
	return (BigString)rpc("saveGeometry",new Object[]{user, geometryXML});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveGeometryAs(BigString geometryXML, java.lang.String newName) throws DataAccessException, ObjectNotFoundException {
	return (BigString)rpc("saveGeometryAs",new Object[]{user, geometryXML, newName});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveMathModel(BigString mathModelXML, String independentSims[]) throws DataAccessException, ObjectNotFoundException {
	return (BigString)rpc("saveMathModel",new Object[]{user, mathModelXML, independentSims});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveMathModelAs(BigString mathModelXML, java.lang.String newName, String independentSims[]) throws DataAccessException, ObjectNotFoundException {
	return (BigString)rpc("saveMathModelAs",new Object[]{user, mathModelXML, newName, independentSims});
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
	return (BigString)rpc("saveSimulation",new Object[]{user, simulationXML, new Boolean(bForceIndependent)});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveVCImage(BigString vcImageXML) throws DataAccessException, ObjectNotFoundException {
	return (BigString)rpc("saveVCImage",new Object[]{user, vcImageXML});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveVCImageAs(BigString vcImageXML, java.lang.String newName) throws DataAccessException, ObjectNotFoundException {
	return (BigString)rpc("saveVCImageAs",new Object[]{user, vcImageXML, newName});
}
}
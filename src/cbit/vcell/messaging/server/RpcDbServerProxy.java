package cbit.vcell.messaging.server;
import cbit.vcell.solver.ode.gui.SimulationStatus;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.util.BigString;
import cbit.sql.Versionable;
import cbit.vcell.solver.SolverResultSetInfo;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.server.ObjectNotFoundException;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.server.User;
import cbit.vcell.messaging.JmsClientMessaging;
import cbit.vcell.messaging.MessageConstants;
import cbit.vcell.modeldb.*;
import cbit.vcell.messaging.JmsUtils;

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
public RpcDbServerProxy(User argUser, JmsClientMessaging clientMessaging, cbit.vcell.server.SessionLog log) throws javax.jms.JMSException {
	super(argUser, clientMessaging, JmsUtils.getQueueDbReq(), log);
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.document.VCDocumentInfo curate(cbit.vcell.server.CurateSpec curateSpec) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.document.VCDocumentInfo)rpc("curate",new Object[]{user,curateSpec});
}


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 4:31:48 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteBioModel(cbit.sql.KeyValue bioModelKey) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	rpc("deleteBioModel",new Object[]{user, bioModelKey});
}


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 4:31:48 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteGeometry(cbit.sql.KeyValue geometryKey) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	rpc("deleteGeometry",new Object[]{user, geometryKey});
}


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 4:31:48 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteMathModel(cbit.sql.KeyValue mathModelKey) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	rpc("deleteMathModel",new Object[]{user, mathModelKey});
}


/**
* Insert the method's description here.
* Creation date: (10/22/2003 10:28:00 AM)
*/
public void deleteResultSetExport(cbit.sql.KeyValue eleKey) throws cbit.vcell.server.DataAccessException {
	rpc("deleteResultSetExport",new Object[]{user, eleKey});
}


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 4:31:48 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
public void deleteVCImage(cbit.sql.KeyValue imageKey) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	rpc("deleteVCImage",new Object[]{user, imageKey});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteOPResults doTestSuiteOP(cbit.vcell.numericstest.TestSuiteOP tsop) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {

	return (cbit.vcell.numericstest.TestSuiteOPResults ) rpc("doTestSuiteOP",new Object[] {user,tsop});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.modeldb.ReferenceQueryResult findReferences(cbit.vcell.modeldb.ReferenceQuerySpec rqs) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException, java.rmi.RemoteException {
	return (cbit.vcell.modeldb.ReferenceQueryResult)rpc("findReferences",new Object[]{user,rqs});
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.VersionableFamily
 * @param vType cbit.sql.VersionableType
 * @param key cbit.sql.KeyValue
 */
public cbit.vcell.modeldb.VersionableFamily getAllReferences(cbit.sql.VersionableType vType, cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (VersionableFamily)rpc("getAllReferences",new Object[]{user, vType,key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelInfo getBioModelInfo(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (cbit.vcell.biomodel.BioModelInfo)rpc("getBioModelInfo",new Object[]{user,key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelInfo[] getBioModelInfos(boolean bAll) throws cbit.vcell.server.DataAccessException {
	return (cbit.vcell.biomodel.BioModelInfo[])rpc("getBioModelInfos",new Object[]{user, new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelMetaData getBioModelMetaData(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (BioModelMetaData)rpc("getBioModelMetaData",new Object[]{user, key});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.biomodel.BioModelMetaData[] getBioModelMetaDatas(boolean bAll) throws cbit.vcell.server.DataAccessException {
	return (BioModelMetaData[])rpc("getBioModelMetaDatas",new Object[]{user, new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getBioModelXML(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException {
	return (BigString)rpc("getBioModelXML",new Object[]{user, key});
}


/**
 * getBoundSpecies method comment.
 */
public cbit.vcell.dictionary.DBSpecies getBoundSpecies(cbit.vcell.dictionary.DBFormalSpecies dbfs) throws cbit.vcell.server.DataAccessException {
	return (cbit.vcell.dictionary.DBSpecies)rpc("getBoundSpecies",new Object[]{user, dbfs});
}


/**
 * getDatabaseSpecies method comment.
 */
public cbit.vcell.dictionary.DBFormalSpecies[] getDatabaseSpecies(java.lang.String likeString, boolean isBound, cbit.vcell.dictionary.FormalSpeciesType speciesType, int restrictSearch, int rowLimit, boolean bOnlyUser) throws cbit.vcell.server.DataAccessException {
	return (cbit.vcell.dictionary.DBFormalSpecies[])rpc("getDatabaseSpecies",new Object[]{user, likeString,new Boolean(isBound),speciesType,new Integer(restrictSearch),new Integer(rowLimit), new Boolean(bOnlyUser)});
}


/**
 * getDictionaryReactions method comment.
 */
public cbit.vcell.dictionary.ReactionDescription[] getDictionaryReactions(ReactionQuerySpec reactionQuerySpec) throws cbit.vcell.server.DataAccessException {
	return (cbit.vcell.dictionary.ReactionDescription[])rpc("getDictionaryReactions",new Object[]{user, reactionQuerySpec});
}


/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 12:00:10 PM)
 * @return cbit.vcell.export.server.ExportLog
 * @param simKey cbit.sql.KeyValue
 */
public cbit.vcell.export.server.ExportLog getExportLog(cbit.sql.KeyValue simulationKey) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (cbit.vcell.export.server.ExportLog)rpc("getExportLog",new Object[]{user, simulationKey});
}


/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 12:00:10 PM)
 * @return cbit.vcell.export.server.ExportLog[]
 * @param simKey cbit.sql.KeyValue
 */
public cbit.vcell.export.server.ExportLog[] getExportLogs(boolean bAll) throws cbit.vcell.server.DataAccessException {
	return (cbit.vcell.export.server.ExportLog[])rpc("getExportLogs",new Object[]{user, new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.simdata.FieldDataIdentifier[] getFieldDataIdentifiers(cbit.vcell.field.FieldDataIdentifierSpec[] fieldDataIDSpecs) throws cbit.vcell.server.DataAccessException {
	return (cbit.vcell.simdata.FieldDataIdentifier[])rpc("getFieldDataIdentifiers",new Object[]{user,fieldDataIDSpecs});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo getGeometryInfo(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (cbit.vcell.geometry.GeometryInfo)rpc("getGeometryInfo",new Object[]{user,key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.geometry.GeometryInfo[] getGeometryInfos(boolean bAll) throws cbit.vcell.server.DataAccessException {
	return (cbit.vcell.geometry.GeometryInfo[])rpc("getGeometryInfos",new Object[]{user, new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getGeometryXML(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException {
	return (BigString)rpc("getGeometryXML",new Object[]{user, key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelInfo getMathModelInfo(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (cbit.vcell.mathmodel.MathModelInfo)rpc("getMathModelInfo",new Object[]{user,key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelInfo[] getMathModelInfos(boolean bAll) throws cbit.vcell.server.DataAccessException {
	return (cbit.vcell.mathmodel.MathModelInfo[])rpc("getMathModelInfos",new Object[]{user, new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelMetaData getMathModelMetaData(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (MathModelMetaData)rpc("getMathModelMetaData",new Object[]{user, key});
}


/**
 * This method was created in VisualAge.
 * @return MathModelMetaData[]
 * @param bAll boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.mathmodel.MathModelMetaData[] getMathModelMetaDatas(boolean bAll) throws cbit.vcell.server.DataAccessException {
	return (MathModelMetaData[])rpc("getMathModelMetaDatas",new Object[]{user, new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getMathModelXML(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException {
	return (BigString)rpc("getMathModelXML",new Object[]{user, key});
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:54:49 PM)
 * @return cbit.util.Preference
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.util.Preference[] getPreferences() throws cbit.vcell.server.DataAccessException {
	return (cbit.util.Preference[])rpc("getPreferences",new Object[]{user});
}


/**
 * getReactionStep method comment.
 */
public cbit.vcell.model.ReactionStep getReactionStep(cbit.sql.KeyValue rxID) throws cbit.vcell.server.DataAccessException {
	return (cbit.vcell.model.ReactionStep)rpc("getReactionStep",new Object[]{user, rxID});
}


/**
 * getReactionStepInfos method comment.
 */
public cbit.vcell.model.ReactionStepInfo[] getReactionStepInfos(cbit.sql.KeyValue[] reactionStepKeys) throws cbit.vcell.server.DataAccessException {
	return (cbit.vcell.model.ReactionStepInfo[])rpc("getReactionStepInfos",new Object[]{user, reactionStepKeys});
}


/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 12:00:10 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
public cbit.vcell.solver.SolverResultSetInfo[] getResultSetInfos(boolean bAll) throws cbit.vcell.server.DataAccessException {
	return (SolverResultSetInfo[])rpc("getResultSetInfos",new Object[]{user, new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationStatus[] getSimulationStatus(cbit.sql.KeyValue simulationKeys[]) throws DataAccessException, ObjectNotFoundException {
	return (SimulationStatus[])rpc("getSimulationStatus",new Object[]{simulationKeys});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationStatus getSimulationStatus(cbit.sql.KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException {
	return (SimulationStatus)rpc("getSimulationStatus",new Object[]{simulationKey});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getSimulationXML(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException {
	return (BigString)rpc("getSimulationXML",new Object[]{user, key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {

	return (cbit.vcell.numericstest.TestSuiteNew ) rpc("getTestSuite",new Object[] {user,getThisTS});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.numericstest.TestSuiteInfoNew[] getTestSuiteInfos() throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {

	return (cbit.vcell.numericstest.TestSuiteInfoNew[] ) rpc("getTestSuiteInfos",new Object[] {user});
}


/**
 * getUserReactionDescriptions method comment.
 */
public cbit.vcell.dictionary.ReactionDescription[] getUserReactionDescriptions(ReactionQuerySpec reactionQuerySpec) throws cbit.vcell.server.DataAccessException {
	return (cbit.vcell.dictionary.ReactionDescription[])rpc("getUserReactionDescriptions",new Object[]{user, reactionQuerySpec});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo getVCImageInfo(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (cbit.image.VCImageInfo)rpc("getVCImageInfo",new Object[]{user,key});
}


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.image.VCImageInfo[] getVCImageInfos(boolean bAll)
    throws cbit.vcell.server.DataAccessException {
    return (cbit.image.VCImageInfo[]) rpc(
        "getVCImageInfos",
        new Object[] { user, new Boolean(bAll)});
}


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString getVCImageXML(cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException {
	return (BigString)rpc("getVCImageXML",new Object[]{user, key});
}


/**
 * getVCInfoContainer method comment.
 */
public VCInfoContainer getVCInfoContainer() throws cbit.vcell.server.DataAccessException {
	return (VCInfoContainer)rpc("getVCInfoContainer",new Object[]{user});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupAddUser(cbit.sql.VersionableType vType, cbit.sql.KeyValue key, java.lang.String addUserToGroup, boolean isHidden) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (cbit.sql.VersionInfo)rpc("groupAddUser",new Object[]{user, vType,key,addUserToGroup,new Boolean(isHidden)});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupRemoveUser(cbit.sql.VersionableType vType, cbit.sql.KeyValue key, java.lang.String userRemoveFromGroup, boolean isHiddenFromOwner) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (cbit.sql.VersionInfo)rpc("groupRemoveUser",new Object[]{user, vType,key,userRemoveFromGroup,new Boolean(isHiddenFromOwner)});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupSetPrivate(cbit.sql.VersionableType vType, cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (cbit.sql.VersionInfo)rpc("groupSetPrivate",new Object[]{user, vType,key});
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.sql.VersionInfo groupSetPublic(cbit.sql.VersionableType vType, cbit.sql.KeyValue key) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (cbit.sql.VersionInfo)rpc("groupSetPublic",new Object[]{user, vType,key});
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:54:49 PM)
 * @param preferences cbit.util.Preference[]
 * @exception java.rmi.RemoteException The exception description.
 */
public void replacePreferences(cbit.util.Preference[] preferences) throws cbit.vcell.server.DataAccessException {
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
private Object rpc(String methodName, Object[] args) throws cbit.vcell.server.ObjectNotFoundException, DataAccessException {
	try {
		return rpc(cbit.vcell.messaging.MessageConstants.SERVICETYPE_DB_VALUE, methodName, args, true);
	} catch (cbit.vcell.server.ObjectNotFoundException ex) {
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
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveBioModel(BigString bioModelXML, String independentSims[]) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (BigString)rpc("saveBioModel",new Object[]{user, bioModelXML, independentSims});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveBioModelAs(BigString bioModelXML, java.lang.String newName, String independentSims[]) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (BigString)rpc("saveBioModelAs",new Object[]{user, bioModelXML, newName, independentSims});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveGeometry(BigString geometryXML) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (BigString)rpc("saveGeometry",new Object[]{user, geometryXML});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveGeometryAs(BigString geometryXML, java.lang.String newName) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (BigString)rpc("saveGeometryAs",new Object[]{user, geometryXML, newName});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveMathModel(BigString mathModelXML, String independentSims[]) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (BigString)rpc("saveMathModel",new Object[]{user, mathModelXML, independentSims});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveMathModelAs(BigString mathModelXML, java.lang.String newName, String independentSims[]) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (BigString)rpc("saveMathModelAs",new Object[]{user, mathModelXML, newName, independentSims});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
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
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveVCImage(BigString vcImageXML) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (BigString)rpc("saveVCImage",new Object[]{user, vcImageXML});
}


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public BigString saveVCImageAs(BigString vcImageXML, java.lang.String newName) throws cbit.vcell.server.DataAccessException, cbit.vcell.server.ObjectNotFoundException {
	return (BigString)rpc("saveVCImageAs",new Object[]{user, vcImageXML, newName});
}
}
package cbit.vcell.client.database;
import cbit.vcell.desktop.controls.SessionManager;
import cbit.vcell.dictionary.FormalSpeciesType;
import cbit.vcell.dictionary.DBFormalSpecies;
import cbit.util.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.mathmodel.*;
import cbit.vcell.geometry.*;
import cbit.vcell.server.*;
import cbit.vcell.model.*;
import java.rmi.*;
import cbit.vcell.solver.*;
import cbit.image.VCImage;
import cbit.image.VCImageInfo;
import cbit.sql.*;
import java.util.*;
import cbit.vcell.server.UserMetaDbServer;
import cbit.vcell.vcml.compare.VCMLComparator;
import cbit.vcell.xml.XmlDialect;
import cbit.vcell.biomodel.*;
import cbit.vcell.xml.XmlHelper;
/**
 * Insert the type's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 * @author: 
 */
public class ClientDocumentManager implements DocumentManager{
	//
	//
	private SessionManager sessionManager = null;

	//
	// cacheTable holds xml for (BioModel, MathModel, Simulation, Geometry, VCImage)
	//
	private Hashtable xmlHash = new Hashtable();
	//
	// hashTables holds all info objects (including BioModelMetaData WITH info objects)
	//
	private boolean bMathModelInfosDirty = true;
	private boolean bImageInfosDirty = true;
	private boolean bBioModelInfosDirty = true;
	private boolean bGeometryInfosDirty = true;
	
	private Hashtable geoInfoHash = new Hashtable();
	private Hashtable imgInfoHash = new Hashtable();
	private Hashtable mathModelInfoHash = new Hashtable();
	private Hashtable bioModelInfoHash = new Hashtable();
	private HashMap simulationStatusHash = new HashMap();
	
	private Preference preferences[] = null;
	
	protected transient cbit.vcell.client.database.DatabaseListener aDatabaseListener = null;

/**
 * ClientDocumentManager constructor comment.
 */
public ClientDocumentManager(SessionManager argSessionManager, long cacheSize) {
	this.sessionManager = argSessionManager;
//	this.xmlCacheTable = new DBCacheTable(0,cacheSize);
}


/**
 * 
 * @param newListener cbit.vcell.clientdb.DatabaseListener
 */
public void addDatabaseListener(cbit.vcell.client.database.DatabaseListener newListener) {
	aDatabaseListener = cbit.vcell.client.database.DatabaseEventMulticaster.add(aDatabaseListener, newListener);
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public VCImageInfo addUserToGroup(VCImageInfo imageInfo, String userToAdd) throws DataAccessException {

	try {
		//
		// publish from database
		//
		VCImageInfo newImageInfo = (VCImageInfo)addUserToGroup0(imageInfo,VersionableType.VCImage,imgInfoHash,userToAdd);
		//
		// delete Image from cache
		//
		xmlHash.remove(imageInfo.getVersion().getVersionKey());

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, imageInfo, newImageInfo));

		return newImageInfo;
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public BioModelInfo addUserToGroup(BioModelInfo bioModelInfo, String userToAdd) throws DataAccessException {

	try {
		//
		// publish from database
		//
		BioModelInfo newBioModelInfo = (BioModelInfo)addUserToGroup0(bioModelInfo,VersionableType.BioModelMetaData,bioModelInfoHash,userToAdd);
		//
		// delete BioModelMetaData from cache
		//
		xmlHash.remove(bioModelInfo.getVersion().getVersionKey());

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, bioModelInfo, newBioModelInfo));

		return newBioModelInfo;
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public GeometryInfo addUserToGroup(GeometryInfo geometryInfo, String userToAdd) throws DataAccessException {

	try {
		//
		// publish from database
		//
		GeometryInfo newGeometryInfo = (GeometryInfo)addUserToGroup0(geometryInfo,VersionableType.Geometry,geoInfoHash,userToAdd);
		////
		//// delete Geometry from cache
		////
		//xmlHash.remove(geometryInfo.getVersion().getVersionKey());
		////
		//// delete any MathModelMetaData's that use this GeometryInfo from cache
		////
		//MathModelInfo referencedMathModelInfos[] = getMathModelReferences(geometryInfo);
		//for (int i = 0; i < referencedMathModelInfos.length; i++){
			//xmlHash.remove(referencedMathModelInfos[i].getVersion().getVersionKey());
		//}
		////
		//// delete any BioModelMetaData's that use this GeometryInfo from cache
		////
		//BioModelInfo referencedBioModelInfos[] = getBioModelReferences(geometryInfo);
		//for (int i = 0; i < referencedBioModelInfos.length; i++){
			//xmlHash.remove(referencedBioModelInfos[i].getVersion().getVersionKey());
		//}

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, geometryInfo, newGeometryInfo));

		return newGeometryInfo;
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public MathModelInfo addUserToGroup(MathModelInfo mathModelInfo, String userToAdd) throws DataAccessException {

	try {
		//
		// publish from database
		//
		MathModelInfo newMathModelInfo = (MathModelInfo)addUserToGroup0(mathModelInfo,VersionableType.MathModelMetaData,mathModelInfoHash, userToAdd);
		//
		// delete MathModelMetaData from cache
		//
		xmlHash.remove(mathModelInfo.getVersion().getVersionKey());

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, mathModelInfo, newMathModelInfo));

		return newMathModelInfo;
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
private VersionInfo addUserToGroup0(VersionInfo versionInfo, VersionableType vType, Hashtable vInfoHash, String userToAdd) throws RemoteException, DataAccessException {

	//
	// unpublish from database
	//
	UserMetaDbServer dbServer = sessionManager.getUserMetaDbServer();
	VersionInfo newVersionInfo = dbServer.groupAddUser(vType,versionInfo.getVersion().getVersionKey(),userToAdd,false);
	
	//
	// replace versionInfo in hashTable
	//
	vInfoHash.remove(versionInfo.getVersion().getVersionKey());
	vInfoHash.put(newVersionInfo.getVersion().getVersionKey(),newVersionInfo);

	//
	// refresh versionInfos of child Geometries and Images (refresh all for now)
	//
	//
	// Removed because of deep cloning of children
	//
	//if (vType.equals(VersionableType.MathModelMetaData) || vType.equals(VersionableType.BioModelMetaData)){
	//	reloadInfos(VersionableType.Geometry,geoInfoHash);
	//	reloadInfos(VersionableType.VCImage,imgInfoHash);
	//}

	return newVersionInfo;
}


/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 5:29:35 PM)
 * @param sims cbit.vcell.solver.Simulation[]
 */
private void cacheSimulations(Simulation[] sims) throws DataAccessException{
	System.out.println("ClientDocumentManager.cacheSimulations() has been disabled until needed");
	//try{
		//for(int i=0;i<sims.length;i+= 1){
			//xmlHash.put(sims[i].getVersion().getVersionKey(),cbit.vcell.xml.XmlHelper.simToXML(sims[i]));
		//}
	//}catch(Exception e){
		//e.printStackTrace();
		//throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	//}
}


/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 9:23:34 AM)
 */
public void curate(CurateSpec curateSpec) throws DataAccessException{
	
	try{
		cbit.util.VCDocumentInfo newVCDocumentInfo = getSessionManager().getUserMetaDbServer().curate(curateSpec);
		
		xmlHash.remove(curateSpec.getVCDocumentInfo().getVersion().getVersionKey());
		
		if(curateSpec.getVCDocumentInfo() instanceof MathModelInfo){
			mathModelInfoHash.remove(curateSpec.getVCDocumentInfo().getVersion().getVersionKey());
			mathModelInfoHash.put(newVCDocumentInfo.getVersion().getVersionKey(),newVCDocumentInfo);
		}else if(curateSpec.getVCDocumentInfo() instanceof BioModelInfo){
			bioModelInfoHash.remove(curateSpec.getVCDocumentInfo().getVersion().getVersionKey());
			bioModelInfoHash.put(newVCDocumentInfo.getVersion().getVersionKey(),newVCDocumentInfo);
		}

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, curateSpec.getVCDocumentInfo(), newVCDocumentInfo));
		
	}catch(Exception e){
		e.printStackTrace();
		throw new DataAccessException(e.getClass().getName()+" "+e.getMessage());
	}

}


/**
 * Insert the method's description here.
 * Creation date: (2/5/01 4:58:40 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
public void delete(cbit.image.VCImageInfo vcImageInfo) throws cbit.util.DataAccessException {

	try {
		//
		// delete from database
		//
		sessionManager.getUserMetaDbServer().deleteVCImage(vcImageInfo.getVersion().getVersionKey());
		//
		// delete VCImage from cache and VCImageInfo from imgInfo list
		//
		xmlHash.remove(vcImageInfo.getVersion().getVersionKey());
		imgInfoHash.remove(vcImageInfo.getVersion().getVersionKey());
		
		fireDatabaseDelete(new DatabaseEvent(this, DatabaseEvent.DELETE, vcImageInfo, null));
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public void delete(BioModelInfo bioModelInfo) throws DataAccessException {
	
	try {
		//BioModelMetaData bioModelMetaData = getBioModelMetaData(bioModelInfo);
		//
		// delete from database
		//
		sessionManager.getUserMetaDbServer().deleteBioModel(bioModelInfo.getVersion().getVersionKey());
		//
		// delete BioModel from cache and BioModelInfo from bioModelInfo list
		//
		xmlHash.remove(bioModelInfo.getVersion().getVersionKey());
		bioModelInfoHash.remove(bioModelInfo.getVersion().getVersionKey());

		fireDatabaseDelete(new DatabaseEvent(this, DatabaseEvent.DELETE, bioModelInfo, null));

		////
		//// go through list of Simulations in the BioModelMetaData and remove unreferenced Simulations
		////
		//Enumeration enumSim = bioModelMetaData.getSimulationInfos();
		//while (enumSim.hasMoreElements()){
			//SimulationInfo simInfo = (SimulationInfo)enumSim.nextElement();
			//if (isSimulationReferenced(simInfo)==false){
				//simInfoHash.remove(simInfo.getVersion().getVersionKey());
				//resultSetInfoHash.remove(simInfo.getVersion().getVersionKey());
				//fireDatabaseDelete(new DatabaseEvent(this, DatabaseEvent.DELETE, simInfo, null));
			//}
		//}
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	} 

}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public void delete(GeometryInfo geometryInfo) throws DataAccessException {

	try {
		//
		// delete from database
		//
		sessionManager.getUserMetaDbServer().deleteGeometry(geometryInfo.getVersion().getVersionKey());
		//
		// delete Geometry from cache and GeometryInfo from geometryInfo list
		//
		xmlHash.remove(geometryInfo.getVersion().getVersionKey());
		geoInfoHash.remove(geometryInfo.getVersion().getVersionKey());

		fireDatabaseDelete(new DatabaseEvent(this, DatabaseEvent.DELETE, geometryInfo, null));
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 11:26:16 PM)
 * @param bioModelInfo cbit.vcell.mathmodel.MathModelInfo
 */
public void delete(MathModelInfo mathModelInfo) throws DataAccessException {

	try {
//		MathModelMetaData mathModelMetaData = getMathModelMetaData(mathModelInfo);
		
		//
		// delete from database
		//
		sessionManager.getUserMetaDbServer().deleteMathModel(mathModelInfo.getVersion().getVersionKey());
		//
		// delete MathModel from cache and MathModelInfo from mathModelInfo list
		//
		xmlHash.remove(mathModelInfo.getVersion().getVersionKey());
		mathModelInfoHash.remove(mathModelInfo.getVersion().getVersionKey());

		fireDatabaseDelete(new DatabaseEvent(this, DatabaseEvent.DELETE, mathModelInfo, null));
		
		////
		//// go through list of Simulations in the MathModelMetaData and remove unreferenced Simulations
		////
		//Enumeration enumSim = mathModelMetaData.getSimulationInfos();
		//while (enumSim.hasMoreElements()){
			//SimulationInfo simInfo = (SimulationInfo)enumSim.nextElement();
			//if (isSimulationReferenced(simInfo)==false){
				//simInfoHash.remove(simInfo.getVersion().getVersionKey());
				//resultSetInfoHash.remove(simInfo.getVersion().getVersionKey());
				//fireDatabaseDelete(new DatabaseEvent(this, DatabaseEvent.DELETE, simInfo, null));
			//}
		//}
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2004 11:27:37 AM)
 */
public cbit.vcell.numericstest.TestSuiteOPResults doTestSuiteOP(cbit.vcell.numericstest.TestSuiteOP tsop) throws cbit.util.DataAccessException {
	if(!getUser().isTestAccount()){
		throw new PermissionException("User="+getUser().getName()+" not allowed TestSuiteInfo");
	}
	try {
		cbit.vcell.numericstest.TestSuiteOPResults tsopr = getSessionManager().getUserMetaDbServer().doTestSuiteOP(tsop);
		//fireDatabaseRefresh(new DatabaseEvent(this, DatabaseEvent.REFRESH, null, null));
		return tsopr;
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 9:41:21 AM)
 * @return cbit.sql.Versionable
 * @param vType cbit.sql.VersionableType
 * @param key cbit.sql.KeyValue
 */
public cbit.vcell.modeldb.ReferenceQueryResult findReferences(cbit.vcell.modeldb.ReferenceQuerySpec rqs) throws cbit.util.DataAccessException {

	try{
		return getSessionManager().getUserMetaDbServer().findReferences(rqs);
	}catch(Exception e){
		e.printStackTrace();
		throw new DataAccessException(e.getClass().getName()+" "+e.getMessage());
	}
}


/**
 * Method to support listener events.
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
protected void fireDatabaseDelete(cbit.vcell.client.database.DatabaseEvent event) {
	if (aDatabaseListener == null) {
		return;
	};
	if (javax.swing.SwingUtilities.isEventDispatchThread()){
		aDatabaseListener.databaseDelete(event);
	}else{
		final cbit.vcell.client.database.DatabaseEvent evt = event;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				aDatabaseListener.databaseDelete(evt);
			}
		});
	}
}


/**
 * Method to support listener events.
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
protected void fireDatabaseInsert(cbit.vcell.client.database.DatabaseEvent event) {
	if (aDatabaseListener == null) {
		return;
	};
	if (javax.swing.SwingUtilities.isEventDispatchThread()){
		aDatabaseListener.databaseInsert(event);
	}else{
		final cbit.vcell.client.database.DatabaseEvent evt = event;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				aDatabaseListener.databaseInsert(evt);
			}
		});
	}
}


/**
 * Method to support listener events.
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
protected void fireDatabaseRefresh(cbit.vcell.client.database.DatabaseEvent event) {
	if (aDatabaseListener == null) {
		return;
	};
	if (javax.swing.SwingUtilities.isEventDispatchThread()){
		aDatabaseListener.databaseRefresh(event);
	}else{
		final cbit.vcell.client.database.DatabaseEvent evt = event;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				aDatabaseListener.databaseRefresh(evt);
			}
		});
	}
}


/**
 * Method to support listener events.
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
protected void fireDatabaseUpdate(cbit.vcell.client.database.DatabaseEvent event) {
	if (aDatabaseListener == null) {
		return;
	};
	if (javax.swing.SwingUtilities.isEventDispatchThread()){
		aDatabaseListener.databaseUpdate(event);
	}else{
		final cbit.vcell.client.database.DatabaseEvent evt = event;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				aDatabaseListener.databaseUpdate(evt);
			}
		});
	}
}


	public void generatePDF(BioModel biomodel, java.io.FileOutputStream fos) throws Exception {

		java.awt.print.PageFormat pageFormat = java.awt.print.PrinterJob.getPrinterJob().defaultPage();
		pageFormat.setOrientation(java.awt.print.PageFormat.PORTRAIT);
		generatePDF(biomodel, fos, pageFormat);
	}


	public void generatePDF(BioModel biomodel, java.io.FileOutputStream fos, java.awt.print.PageFormat pageFormat) throws Exception {

		cbit.vcell.publish.ITextWriter pdfWriter = cbit.vcell.publish.ITextWriter.getInstance(cbit.vcell.publish.ITextWriter.PDF_WRITER);
		pdfWriter.writeBioModel(biomodel, fos, pageFormat);
	}


	public void generatePDF(Geometry geom, java.io.FileOutputStream fos) throws Exception {

		java.awt.print.PageFormat pageFormat = java.awt.print.PrinterJob.getPrinterJob().defaultPage();
		pageFormat.setOrientation(java.awt.print.PageFormat.PORTRAIT);
		generatePDF(geom, fos, pageFormat);
	}


	public void generatePDF(Geometry geom, java.io.FileOutputStream fos, java.awt.print.PageFormat pageFormat) throws Exception {

		cbit.vcell.publish.ITextWriter pdfWriter = cbit.vcell.publish.ITextWriter.getInstance(cbit.vcell.publish.ITextWriter.PDF_WRITER);
		pdfWriter.writeGeometry(geom, fos, pageFormat);
	}


	public void generatePDF(MathModel mathmodel, java.io.FileOutputStream fos) throws Exception {

		java.awt.print.PageFormat pageFormat = java.awt.print.PrinterJob.getPrinterJob().defaultPage();
		pageFormat.setOrientation(java.awt.print.PageFormat.PORTRAIT);
		generatePDF(mathmodel, fos, pageFormat);
	}


	public void generatePDF(MathModel mathmodel, java.io.FileOutputStream fos, java.awt.print.PageFormat pageFormat) throws Exception {

		cbit.vcell.publish.ITextWriter pdfWriter = cbit.vcell.publish.ITextWriter.getInstance(cbit.vcell.publish.ITextWriter.PDF_WRITER);
		pdfWriter.writeMathModel(mathmodel, fos, pageFormat);
	}


	public void generateReactionsImage(Model model, Structure struct, String resolution, java.io.FileOutputStream fos) throws Exception {

		java.io.ByteArrayOutputStream bos = cbit.vcell.publish.ITextWriter.generateReactionsImage(model, struct, resolution);
		try {
			bos.flush();
			bos.writeTo(fos);
			fos.flush();
			fos.close();
			bos.close();
		} catch (java.io.IOException e) {
			System.err.println("Unable to print image to file.");
			e.printStackTrace();
			throw e;
		}
	}


	public void generateStructureImage(Model model, String resolution, java.io.FileOutputStream fos) throws Exception {
		
		java.io.ByteArrayOutputStream bos = cbit.vcell.publish.ITextWriter.generateStructureImage(model, resolution);
		try {
			bos.flush();
			bos.writeTo(fos);
			fos.flush();
			fos.close();
			bos.close();
		} catch (java.io.IOException e) {
			System.err.println("Unable to print image to file.");
			e.printStackTrace();
			throw e;
		}
	}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:02:44 PM)
 * @return cbit.vcell.biomodel.BioModel
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public BioModel getBioModel(KeyValue bioModelKey) throws DataAccessException {

	String bioModelXML = getBioModelXML(bioModelKey);

	BioModel bioModel = getBioModelFromDatabaseXML(bioModelXML);

	//
	// preload SimulationJobStatus for all simulations if any missing from hash.
	// 
	Simulation simulations[] = bioModel.getSimulations();
	KeyValue simKeys[] = new KeyValue[simulations.length];
	for (int i = 0; i < simulations.length; i++){
		VCSimulationIdentifier vcSimulationIdentifier = simulations[i].getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
		simKeys[i] = vcSimulationIdentifier.getSimulationKey();
	}
	preloadSimulationStatus(simKeys);
	
	return bioModel;
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:02:44 PM)
 * @return cbit.vcell.biomodel.BioModel
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public BioModel getBioModel(BioModelInfo bioModelInfo) throws DataAccessException {

	return getBioModel(bioModelInfo.getVersion().getVersionKey());
}


/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 5:22:40 PM)
 * @return cbit.vcell.mathmodel.MathModel
 * @param mathModelXML java.lang.String
 */
private BioModel getBioModelFromDatabaseXML(String bioModelXML) throws DataAccessException{

	try{
		BioModel bm = cbit.vcell.xml.XmlHelper.XMLToBioModel(bioModelXML);
		cacheSimulations(bm.getSimulations());
		bm.refreshDependencies();
		return bm;
	}catch(cbit.vcell.xml.XmlParseException e){
		e.printStackTrace();
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 2:50:07 PM)
 * @return cbit.sql.Versionable
 * @param vType cbit.sql.VersionableType
 * @param key cbit.sql.KeyValue
 */
public BioModelInfo getBioModelInfo(KeyValue key) throws DataAccessException {
	if (key==null){
System.out.println("<<<NULL>>>> ClientDocumentManager.getBioModelInfo("+key+")");
		return null;
	}
	//
	// first look in local cache for the Info object
	//
	BioModelInfo bioModelInfo = (BioModelInfo)bioModelInfoHash.get(key);
	if (bioModelInfo!=null){
		return bioModelInfo;
	}

	//
	// else refresh cache
	//
	reloadBioModelInfos();
	bBioModelInfosDirty = false;

	//
	// now look in cache again (should be in there unless it was deleted from database).
	//
	bioModelInfo = (BioModelInfo)bioModelInfoHash.get(key);
	if (bioModelInfo!=null){
		return bioModelInfo;
	}

	System.out.println("BioModelInfo("+key+") not found");
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 5:33:21 PM)
 * @return cbit.vcell.biomodel.BioModelInfo[]
 */
public BioModelInfo[] getBioModelInfos() throws DataAccessException {
	if (bBioModelInfosDirty){
		reloadBioModelInfos();
		bBioModelInfosDirty = false;
	}
	ArrayList arrayList = new ArrayList(bioModelInfoHash.values());
	Collections.sort(arrayList,new VersionInfoComparator());
	return (BioModelInfo[])arrayList.toArray(new BioModelInfo[bioModelInfoHash.size()]);
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 4:04:16 PM)
 * @return java.lang.String
 * @param vType cbit.sql.VersionableType
 * @param vKey cbit.sql.KeyValue
 */
private String getBioModelXML(KeyValue vKey) throws DataAccessException {

	try{
		String xmlString = (String)xmlHash.get(vKey);
		if (xmlString==null){
			BigString xmlBS = sessionManager.getUserMetaDbServer().getBioModelXML(vKey);
			xmlString = (xmlBS != null?xmlBS.toString():null);
			if(xmlString != null){
				xmlHash.put(vKey,xmlString);
				return xmlString;
			}else{
				throw new RuntimeException("unexpected: UserMetaDbServer.getBioModelXML() returned null");
			}
		}else{
			return xmlString;
		}
	}catch (ObjectNotFoundException e){
		return null;
	}catch(Exception e){
		e.printStackTrace(System.out);
		throw new DataAccessException("Error getting XML document from server: "+e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/8/2003 4:55:39 PM)
 */
public cbit.vcell.dictionary.DBSpecies getBoundSpecies(cbit.vcell.dictionary.DBFormalSpecies dbfs) throws DataAccessException {
	try {
		return sessionManager.getUserMetaDbServer().getBoundSpecies(dbfs);
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/8/2003 4:55:39 PM)
 */
public DBFormalSpecies[] getDatabaseSpecies(String likeString, boolean isBound, FormalSpeciesType speciesType, int restrictSearch, int rowLimit, boolean bOnlyUser)	throws DataAccessException {

	try {
		return sessionManager.getUserMetaDbServer().getDatabaseSpecies(likeString,isBound,speciesType,restrictSearch,rowLimit,bOnlyUser);
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/30/2003 10:26:17 PM)
 */
public cbit.vcell.dictionary.ReactionDescription[] getDictionaryReactions(cbit.vcell.modeldb.ReactionQuerySpec reactionQuerySpec) throws DataAccessException {
	try {
		return sessionManager.getUserMetaDbServer().getDictionaryReactions(reactionQuerySpec);
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:02:44 PM)
 * @return cbit.vcell.biomodel.BioModel
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public Geometry getGeometry(KeyValue geometryKey) throws DataAccessException {

	String geometryXML = getGeometryXML(geometryKey);
	
	Geometry geometry = getGeometryFromDatabaseXML(geometryXML);
	
	return geometry;
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:02:44 PM)
 * @return cbit.vcell.biomodel.BioModel
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public Geometry getGeometry(GeometryInfo geometryInfo) throws DataAccessException {

	Geometry geometry = null;
	try {
		geometry = cbit.vcell.xml.XmlHelper.XMLToGeometry(getGeometryXML(geometryInfo.getVersion().getVersionKey()));
	}catch (cbit.vcell.xml.XmlParseException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
	
	try {
		if (geometry.getDimension()>0 && geometry.getGeometrySurfaceDescription().getGeometricRegions()==null){
			geometry.getGeometrySurfaceDescription().updateAll();
		}
	}catch (Exception e){
		e.printStackTrace(System.out);
		//throw new DataAccessException("Geometric surface generation error:\n"+e.getMessage());
	}
	
	return geometry;
}


/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 5:22:40 PM)
 * @return cbit.vcell.mathmodel.MathModel
 * @param mathModelXML java.lang.String
 */
private Geometry getGeometryFromDatabaseXML(String geometryXML) throws DataAccessException{

	try{
		Geometry geometry = cbit.vcell.xml.XmlHelper.XMLToGeometry(geometryXML);
		geometry.refreshDependencies();

		try {
			if (geometry.getDimension()>0){
				geometry.getGeometrySurfaceDescription().updateAll();
			}
		}catch (Exception e){
			e.printStackTrace(System.out);
			throw new DataAccessException("Geometric surface generation error:\n"+e.getMessage());
		}

		return geometry;
	}catch(cbit.vcell.xml.XmlParseException e){
		e.printStackTrace();
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 2:50:07 PM)
 * @return cbit.sql.Versionable
 * @param vType cbit.sql.VersionableType
 * @param key cbit.sql.KeyValue
 */
public GeometryInfo getGeometryInfo(KeyValue key) throws DataAccessException {
	if (key==null){
System.out.println("<<<NULL>>>> ClientDocumentManager.getGeometryInfo("+key+")");
		return null;
	}
	//
	// first look in local cache for the Info object
	//
	GeometryInfo geometryInfo = (GeometryInfo)geoInfoHash.get(key);
	if (geometryInfo!=null){
//System.out.println("<<<IN CACHE>>> ClientDocumentManager.getGeometryInfo("+key+")");
		return geometryInfo;
	}
	
	//
	// else get new list of info objects from database and stick in cache
	//
	reloadGeometryInfos();
	bGeometryInfosDirty = false;

	//
	// now look in cache again (should be in there unless it was deleted from database).
	//
	geometryInfo = (GeometryInfo)geoInfoHash.get(key);
	if (geometryInfo!=null){
		return geometryInfo;
	}else{
		throw new ObjectNotFoundException("GeometryInfo("+key+") not found");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 5:33:21 PM)
 * @return cbit.vcell.biomodel.BioModelInfo[]
 */
public GeometryInfo[] getGeometryInfos() throws DataAccessException {
	if (bGeometryInfosDirty){
		reloadGeometryInfos();
		bGeometryInfosDirty = false;
	}
	ArrayList arrayList = new ArrayList(geoInfoHash.values());
	Collections.sort(arrayList,new VersionInfoComparator());
	return (GeometryInfo[])arrayList.toArray(new GeometryInfo[geoInfoHash.size()]);
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 4:04:16 PM)
 * @return java.lang.String
 * @param vType cbit.sql.VersionableType
 * @param vKey cbit.sql.KeyValue
 */
private String getGeometryXML(KeyValue vKey) throws DataAccessException {

	try{
		String xmlString = (String)xmlHash.get(vKey);
		if (xmlString==null){
			xmlString = sessionManager.getUserMetaDbServer().getGeometryXML(vKey).toString();
			if(xmlString != null){
				xmlHash.put(vKey,xmlString);
				return xmlString;
			}else{
				throw new RuntimeException("unexpected: UserMetaDbServer.getGeometryXML() returned null");
			}
		}else{
			return xmlString;
		}
	}catch (ObjectNotFoundException e){
		return null;
	}catch(Exception e){
		e.printStackTrace(System.out);
		throw new DataAccessException("Error getting XML document from server: "+e.getMessage());
		//return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:02:44 PM)
 * @return cbit.vcell.biomodel.BioModel
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public VCImage getImage(VCImageInfo vcImageInfo) throws DataAccessException {
	
	VCImage vcImage = null;
	try {
		vcImage = cbit.vcell.xml.XmlHelper.XMLToImage(getImageXML(vcImageInfo.getVersion().getVersionKey()));
	}catch (cbit.vcell.xml.XmlParseException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
	
	return vcImage;
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 2:50:07 PM)
 * @return cbit.sql.Versionable
 * @param vType cbit.sql.VersionableType
 * @param key cbit.sql.KeyValue
 */
private VCImageInfo getImageInfo(KeyValue key) throws DataAccessException {
	if (key==null){
System.out.println("<<<NULL>>>> ClientDocumentManager.getImageInfo("+key+")");
		return null;
	}
	//
	// first look in local cache for the Info object
	//
	VCImageInfo imageInfo = (VCImageInfo)imgInfoHash.get(key);
	if (imageInfo!=null){
//System.out.println("<<<IN CACHE>>> ClientDocumentManager.getImageInfo("+key+")");
		return imageInfo;
	}
	
	//
	// else get new list of info objects from database and stick in cache
	//
	reloadVCImageInfos();
	bImageInfosDirty = false;

	//
	// now look in cache again (should be in there unless it was deleted from database).
	//
	imageInfo = (VCImageInfo)imgInfoHash.get(key);
	if (imageInfo!=null){
		return imageInfo;
	}else{
		throw new ObjectNotFoundException("VCImageInfo("+key+") not found");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/01 4:58:40 PM)
 */
public cbit.image.VCImageInfo[] getImageInfos() throws cbit.util.DataAccessException {
	if (bImageInfosDirty){
		reloadVCImageInfos();
		bImageInfosDirty = false;
	}
	ArrayList arrayList = new ArrayList(imgInfoHash.values());
	Collections.sort(arrayList,new VersionInfoComparator());
	return (VCImageInfo[])arrayList.toArray(new VCImageInfo[imgInfoHash.size()]);
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 4:04:16 PM)
 * @return java.lang.String
 * @param vType cbit.sql.VersionableType
 * @param vKey cbit.sql.KeyValue
 */
private String getImageXML(KeyValue vKey) throws DataAccessException {

	try{
		String xmlString = (String)xmlHash.get(vKey);
		if (xmlString==null){
			xmlString = sessionManager.getUserMetaDbServer().getVCImageXML(vKey).toString();
			if(xmlString != null){
				xmlHash.put(vKey,xmlString);
				return xmlString;
			}else{
				throw new RuntimeException("unexpected: UserMetaDbServer.getVCImageXML() returned null");
			}
		}else{
			return xmlString;
		}
	}catch (ObjectNotFoundException e){
		return null;
	}catch(Exception e){
		e.printStackTrace(System.out);
		throw new DataAccessException("Error getting XML document from server: "+e.getMessage());
		//return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 5:30:03 PM)
 * @return cbit.sql.KeyValue[]
 * @param enum1 java.util.Enumeration
 */
private KeyValue[] getKeyArrayFromEnumeration(Enumeration enum1) {
	Vector temp = new Vector();
	while (enum1.hasMoreElements()){
		temp.addElement(enum1.nextElement());
	}
	KeyValue keyArray[] = new KeyValue[temp.size()];
	temp.copyInto(keyArray);
	return keyArray;
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:02:44 PM)
 * @return cbit.vcell.biomodel.BioModel
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public MathModel getMathModel(KeyValue mathModelKey) throws DataAccessException {

	String mathModelXML = getMathModelXML(mathModelKey);
	MathModel mathModel = getMathModelFromDatabaseXML(mathModelXML);

	//
	// preload SimulationJobStatus for all simulations if any missing from hash.
	//
	Simulation simulations[] = mathModel.getSimulations();
	KeyValue simKeys[] = new KeyValue[simulations.length];
	for (int i = 0; i < simulations.length; i++){
		VCSimulationIdentifier vcSimulationIdentifier = simulations[i].getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
		simKeys[i] = vcSimulationIdentifier.getSimulationKey();
	}
	preloadSimulationStatus(simKeys);
	return mathModel;
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:02:44 PM)
 * @return cbit.vcell.biomodel.BioModel
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public MathModel getMathModel(MathModelInfo mathModelInfo) throws DataAccessException {
	
	return getMathModel(mathModelInfo.getVersion().getVersionKey());
}


/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 5:22:40 PM)
 * @return cbit.vcell.mathmodel.MathModel
 * @param mathModelXML java.lang.String
 */
private MathModel getMathModelFromDatabaseXML(String mathModelXML) throws DataAccessException{

	try{
		MathModel mm = cbit.vcell.xml.XmlHelper.XMLToMathModel(mathModelXML);
		cacheSimulations(mm.getSimulations());
		mm.refreshDependencies();

		try {
			if (mm.getMathDescription().getGeometry().getDimension()>0 && mm.getMathDescription().getGeometry().getGeometrySurfaceDescription().getGeometricRegions() == null){
				mm.getMathDescription().getGeometry().getGeometrySurfaceDescription().updateAll();
			}
		}catch (Exception e){
			e.printStackTrace(System.out);
			throw new DataAccessException("Geometric surface generation error:\n"+e.getMessage());
		}

		return mm;
	}catch(cbit.vcell.xml.XmlParseException e){
		e.printStackTrace();
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 2:50:07 PM)
 * @return cbit.sql.Versionable
 * @param vType cbit.sql.VersionableType
 * @param key cbit.sql.KeyValue
 */
public MathModelInfo getMathModelInfo(KeyValue key) throws DataAccessException {
	if (key==null){
System.out.println("<<<NULL>>>> ClientDocumentManager.getMathModelInfo("+key+")");
		return null;
	}
	//
	// first look in local cache for the Info object
	//
	//
	// first look in local cache for the Info object
	//
	MathModelInfo mathModelInfo = (MathModelInfo)mathModelInfoHash.get(key);
	if (mathModelInfo!=null){
//System.out.println("<<<IN CACHE>>> ClientDocumentManager.getMathModelInfo("+key+")");
		return mathModelInfo;
	}

	//
	// else refresh cache
	//
	reloadMathModelInfos();
	bMathModelInfosDirty = false;
	

	//
	// now look in cache again (should be in there unless it was deleted from database).
	//
	mathModelInfo = (MathModelInfo)mathModelInfoHash.get(key);
	if (mathModelInfo!=null){
		return mathModelInfo;
	}

	System.out.println("MathModelInfo("+key+") not found");
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 5:33:21 PM)
 * @return cbit.vcell.biomodel.BioModelInfo[]
 */
public MathModelInfo[] getMathModelInfos() throws DataAccessException {
	if (bMathModelInfosDirty){
		reloadMathModelInfos();
		bMathModelInfosDirty = false;
	}
	ArrayList arrayList = new ArrayList(mathModelInfoHash.values());
	Collections.sort(arrayList,new VersionInfoComparator());
	return (MathModelInfo[])arrayList.toArray(new MathModelInfo[mathModelInfoHash.size()]);
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 4:04:16 PM)
 * @return java.lang.String
 * @param vType cbit.sql.VersionableType
 * @param vKey cbit.sql.KeyValue
 */
private String getMathModelXML(KeyValue vKey) throws DataAccessException {

	try{
		String xmlString = (String)xmlHash.get(vKey);
		if (xmlString==null){
			xmlString = sessionManager.getUserMetaDbServer().getMathModelXML(vKey).toString();
			if(xmlString != null){
				xmlHash.put(vKey,xmlString);
				return xmlString;
			}else{
				throw new RuntimeException("unexpected: UserMetaDbServer.getMathModelXML() returned null");
			}
		}else{
			return xmlString;
		}
	}catch (ObjectNotFoundException e){
		return null;
	}catch(Exception e){
		e.printStackTrace(System.out);
		throw new DataAccessException("Error getting XML document from server: "+e.getMessage());
		//return null;
	}
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public Preference[] getPreferences() throws cbit.util.DataAccessException{

	System.out.println("ClientDocumentManager.getPreferences()");
	if (preferences!=null){
		return preferences;
	}else{
		try{
			preferences = sessionManager.getUserMetaDbServer().getPreferences();
			return preferences;
		}catch (RemoteException e){
			handleRemoteException(e);
			throw new DataAccessException(e.getMessage());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 5:10:41 PM)
 */
public cbit.vcell.model.ReactionStep getReactionStep(cbit.util.KeyValue reactionStepKey) throws cbit.util.DataAccessException {
	try {
		ReactionStep rStep = sessionManager.getUserMetaDbServer().getReactionStep(reactionStepKey);
		if(rStep != null){
			try{
				rStep = (ReactionStep)BeanUtils.cloneSerializable(rStep);
			}catch(Exception e){
				e.printStackTrace(System.out);
				throw new DataAccessException(e.getMessage());
			}
		}
		return rStep;
		
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 10:48:52 AM)
 * @return cbit.vcell.solver.ode.gui.SimulationStatus
 * @param vcSimulationIdentifier cbit.vcell.solver.VCSimulationIdentifier
 */
public SimulationStatus getServerSimulationStatus(VCSimulationIdentifier vcSimulationIdentifier) throws DataAccessException {
	if (simulationStatusHash.containsKey(vcSimulationIdentifier.getSimulationKey())){
		return (SimulationStatus)simulationStatusHash.get(vcSimulationIdentifier.getSimulationKey());
	}else{
		SimulationStatus simulationStatus = null;
		try {
			simulationStatus = sessionManager.getUserMetaDbServer().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
		}catch (RemoteException e){
			handleRemoteException(e);
			try {
				simulationStatus = sessionManager.getUserMetaDbServer().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
			}catch (RemoteException e2){
				handleRemoteException(e2);
				throw new DataAccessException("SimulationStatus inquiry for '"+vcSimulationIdentifier+"' failed\n"+e2.getMessage());
			}
		}
		simulationStatusHash.put(vcSimulationIdentifier.getSimulationKey(),simulationStatus);
		return simulationStatus;
	}
}


	public SessionManager getSessionManager() {

		return sessionManager;
	}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:02:44 PM)
 * @return cbit.vcell.biomodel.BioModel
 * @param simulationInfo cbit.vcell.solver.SimulationInfo
 */
public Simulation getSimulation(SimulationInfo simulationInfo) throws DataAccessException {
	
	Simulation simulation = null;
	try {
		simulation = cbit.vcell.xml.XmlHelper.XMLToSim(getSimulationXML(simulationInfo.getVersion().getVersionKey()));
	}catch (cbit.vcell.xml.XmlParseException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
	
	return simulation;
}


/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 5:22:40 PM)
 * @return cbit.vcell.mathmodel.MathModel
 * @param mathModelXML java.lang.String
 */
private Simulation getSimulationFromDatabaseXML(String simulationXML) throws DataAccessException{

	try{
		Simulation sim = cbit.vcell.xml.XmlHelper.XMLToSim(simulationXML);
		cacheSimulations(new Simulation[] {sim});
		sim.refreshDependencies();
		return sim;
	}catch(cbit.vcell.xml.XmlParseException e){
		e.printStackTrace();
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 4:04:16 PM)
 * @return java.lang.String
 * @param vType cbit.sql.VersionableType
 * @param vKey cbit.sql.KeyValue
 */
private String getSimulationXML(KeyValue vKey) throws DataAccessException {

	try{
		String xmlString = (String)xmlHash.get(vKey);
		if (xmlString==null){
			xmlString = sessionManager.getUserMetaDbServer().getSimulationXML(vKey).toString();
			if(xmlString != null){
				xmlHash.put(vKey,xmlString);
				return xmlString;
			}else{
				throw new RuntimeException("unexpected: UserMetaDbServer.getSimulationXML() returned null");
			}
		}else{
			return xmlString;
		}
	}catch (ObjectNotFoundException e){
		return null;
	}catch(Exception e){
		e.printStackTrace(System.out);
		throw new DataAccessException("Error getting XML document from server: "+e.getMessage());
		//return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2004 11:27:37 AM)
 */
public cbit.vcell.numericstest.TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws cbit.util.DataAccessException {
	if(!getUser().isTestAccount()){
		throw new PermissionException("User="+getUser().getName()+" not allowed TestSuiteInfo");
	}
	try {
		return getSessionManager().getUserMetaDbServer().getTestSuite(getThisTS);
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2004 11:27:37 AM)
 */
public cbit.vcell.numericstest.TestSuiteInfoNew[] getTestSuiteInfos() throws cbit.util.DataAccessException {
	
	if(!getUser().isTestAccount()){
		throw new PermissionException("User="+getUser().getName()+" not allowed TestSuiteInfo");
	}
	try {
		return getSessionManager().getUserMetaDbServer().getTestSuiteInfos();
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/01 10:54:29 AM)
 */
public cbit.util.User getUser() {
	return sessionManager.getUser();
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 3:33:10 PM)
 */
public cbit.vcell.dictionary.ReactionDescription[] getUserReactionDescriptions(cbit.vcell.modeldb.ReactionQuerySpec reactionQuerySpec) throws cbit.util.DataAccessException {
	try {
		return sessionManager.getUserMetaDbServer().getUserReactionDescriptions(reactionQuerySpec);
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 3:33:10 PM)
 */
public cbit.vcell.model.ReactionStepInfo[] getUserReactionStepInfos(cbit.util.KeyValue[] reactionStepKeys) throws cbit.util.DataAccessException {
	try {
		return sessionManager.getUserMetaDbServer().getReactionStepInfos(reactionStepKeys);
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
}


public String getXML(BioModelInfo bmInfo) throws cbit.util.DataAccessException, cbit.vcell.xml.XmlParseException{
	return getBioModelXML(bmInfo.getVersion().getVersionKey()); // faster ... this is how it's cached.
}


/**
 * This method returns a XML String of the given bioModel object with a specific type.
 * Creation date: (2/4/2002 5:17:15 PM)
 * @return java.lang.String
 * @param bioModel cbit.vcell.biomodel.BioModel
 */
public java.lang.String getXML(cbit.vcell.biomodel.BioModelInfo bioModelInfoArg, XmlDialect toDialect) throws java.io.IOException, cbit.vcell.xml.XmlParseException, DataAccessException {
	return XmlHelper.exportXML(getBioModel(bioModelInfoArg), toDialect);
}


public String getXML(MathModelInfo mmInfo) throws cbit.util.DataAccessException, cbit.vcell.xml.XmlParseException{
	
	return getMathModelXML(mmInfo.getVersion().getVersionKey());
	
}


/**
 * This method returns a XML String of the given MathModel object with a specific type.
 * Creation date: (2/4/2002 5:17:15 PM)
 * @return java.lang.String
 * @param bioModel cbit.vcell.biomodel.BioModel
 */
public java.lang.String getXML(cbit.vcell.mathmodel.MathModelInfo mathModelInfoArg, XmlDialect toDialect) throws java.io.IOException, cbit.vcell.xml.XmlParseException, DataAccessException {
	return XmlHelper.exportXML(getMathModel(mathModelInfoArg), toDialect);
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 5:24:29 PM)
 * @param e java.rmi.RemoteException
 */
private void handleRemoteException(RemoteException e) {
	System.out.println("\n\n.... Handling RemoteException ...\n");
	e.printStackTrace(System.out);
	System.out.println("\n\n");
}


/**
 * Insert the method's description here.
 * Creation date: (9/25/2003 7:45:19 AM)
 * @return cbit.vcell.modeldb.VCInfoContainer
 */
public void initAllDatabaseInfos() throws DataAccessException {

	cbit.vcell.modeldb.VCInfoContainer vcInfoContainer = null;
	try{
		System.out.println("ClientDocumentManager.initAllDatabaseInfos()");
		long time1 = System.currentTimeMillis();
		//
		// gets BioModelMetaDatas, MathModelMetaDatas, all VersionInfos, and ResultSetInfos
		//
		vcInfoContainer = sessionManager.getUserMetaDbServer().getVCInfoContainer();
		bMathModelInfosDirty = false;
		bImageInfosDirty = false;
		bBioModelInfosDirty = false;
		bGeometryInfosDirty = false;

		cbit.rmi.event.PerformanceMonitorEvent pme = new cbit.rmi.event.PerformanceMonitorEvent(this,getUser(),
			new cbit.rmi.event.PerformanceData("ClientDocumentManager.initAllDatabaseInfos()",
			    cbit.rmi.event.MessageEvent.LOGON_STAT,
			    new cbit.rmi.event.PerformanceDataEntry[] {
				    new cbit.rmi.event.PerformanceDataEntry("remote call duration", Double.toString(((double)System.currentTimeMillis()-time1)/1000.0))
				    }
		    )
    	);
		((cbit.vcell.client.server.ClientServerManager)getSessionManager()).getAsynchMessageManager().performanceMonitorEvent(pme);

	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException("RemoteException: "+e.getMessage());
	}
	
	//
	// BioModelInfos
	//
	{
	bioModelInfoHash.clear();
	BioModelInfo bioModelInfos[] = vcInfoContainer.getBioModelInfos();
	if (bioModelInfos!=null){
		for (int i=0;i<bioModelInfos.length;i++){
			bioModelInfoHash.put(bioModelInfos[i].getVersion().getVersionKey(),bioModelInfos[i]);
		}
	}
	}
	//
	// Geometries
	//
	{
	geoInfoHash.clear();
	GeometryInfo geometryInfos[] = vcInfoContainer.getGeometryInfos();
	if (geometryInfos!=null){
		for (int i=0;i<geometryInfos.length;i++){
			geoInfoHash.put(geometryInfos[i].getVersion().getVersionKey(),geometryInfos[i]);
		}
	}
	}
	//
	// MathModelInfos
	//
	{
	mathModelInfoHash.clear();
	MathModelInfo mathModelInfos[] = vcInfoContainer.getMathModelInfos();
	if (mathModelInfos!=null){
		for (int i=0;i<mathModelInfos.length;i++){
			mathModelInfoHash.put(mathModelInfos[i].getVersion().getVersionKey(),mathModelInfos[i]);
		}
	}
	}
	//
	// VCImageInfos
	//
	{
	imgInfoHash.clear();
	VCImageInfo vcImageInfos[] = vcInfoContainer.getVCImageInfos();
	if (vcImageInfos!=null){
		for (int i=0;i<vcImageInfos.length;i++){
			imgInfoHash.put(vcImageInfos[i].getVersion().getVersionKey(),vcImageInfos[i]);
		}
	}
	}

	fireDatabaseRefresh(new DatabaseEvent(this, DatabaseEvent.REFRESH, null, null));
		
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/01 4:58:40 PM)
 */
public boolean isChanged(cbit.image.VCImage vcImage, String vcImageXML) throws cbit.util.DataAccessException {
	//
	// get versionable from database or from cache (should be in cache)
	//
	if(isChangedVersion(vcImage.getVersion())){
		return true;
	}
	
	String savedImageXML = null;

	try {
		savedImageXML = getImageXML(vcImage.getVersion().getVersionKey());
	}catch (Throwable e){
		e.printStackTrace(System.out);
		//
		// loaded version has been deleted
		//
		return true;
	}

	//
	// if never saved, then it has changed (from null to something)
	//
	if (savedImageXML == null){
		return true;
	}

	//
	// if comparison fails, then it changed
	//
	try {
		if (vcImageXML==null){
			vcImageXML = XmlHelper.imageToXML(vcImage);
		}
		if (!VCMLComparator.compareEquals(savedImageXML,vcImageXML)){
			return true;
		}
	}catch (cbit.vcell.xml.XmlParseException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}


	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
public boolean isChanged(BioModel bioModel, String bioModelXML) throws DataAccessException {

	if(isChangedVersion(bioModel.getVersion())){
		//
		// never been saved before (has changed with respect to database)
		//
		return true;
		
	}else{
		//
		// check for name change
		//
		if (!bioModel.getVersion().getName().equals(bioModel.getName())){
			return true;
		}
		//
		// check for annotation change
		//
		if (!bioModel.getVersion().getAnnot().equals(bioModel.getDescription())){
			return true;
		}
		
		////
		//// check for same number of simulations and simulationContexts as saved version
		////
		//BioModelInfo savedBioModelInfo = getBioModelInfo(bioModel.getVersion().getVersionKey());
		//if (savedBioModelInfo==null){
			////
			//// if savedBioModelInfo is null, then the record was deleted
			//// while it was loaded in client (changed is true)
			////
			//System.out.println("BioModel("+bioModel.getVersion().getVersionKey()+") must have been deleted, therefore isChanged() is true");
			//return true;
		//}
		//BioModelMetaData savedBioModelMetaData = getBioModelMetaData(savedBioModelInfo);
			
		//if (savedBioModelMetaData.getNumSimulationContexts() != bioModel.getNumSimulationContexts()){
			//return true;
		//}
		//if (savedBioModelMetaData.getNumSimulations() != bioModel.getNumSimulations()){
			//return true;
		//}

		//
		// compare saved and this bioModel
		//
		String savedBioModelXML = getBioModelXML(bioModel.getVersion().getVersionKey());
		if (savedBioModelXML == null){
			// must have been deleted
			return true;
		}
		try {
			if (bioModelXML==null){
				bioModelXML = XmlHelper.bioModelToXML(bioModel);
			}
			if (VCMLComparator.compareEquals(savedBioModelXML,bioModelXML)){
				return false;
			}else{
				return true;
			}
		}catch (cbit.vcell.xml.XmlParseException e){
			e.printStackTrace(System.out);
			throw new DataAccessException(e.getMessage());
		}
	}

}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 6:56:58 AM)
 * @return boolean
 * @param vcDocument cbit.vcell.document.VCDocument
 */
public boolean isChanged(cbit.util.VCDocument vcDocument) throws DataAccessException {
	if (vcDocument instanceof BioModel) {
		return isChanged((BioModel)vcDocument,null);
	} else if (vcDocument instanceof MathModel) {
		return isChanged((MathModel)vcDocument,null);
	} else if (vcDocument instanceof Geometry) {
		return isChanged((Geometry)vcDocument,null);
	} else if (vcDocument instanceof VCImage) {
		return isChanged((VCImage)vcDocument,null);
	} else {
		throw new RuntimeException("Unknown VCDocument class: " + vcDocument.getClass());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
public boolean isChanged(Geometry geometry, String geometryXML) throws DataAccessException {
	//
	// get versionable from database or from cache (should be in cache)
	//
	if(isChangedVersion(geometry.getVersion())){
		return true;
	}
	
	String savedGeometryXML = null;

	try {
		savedGeometryXML = getImageXML(geometry.getVersion().getVersionKey());
	}catch (Throwable e){
		e.printStackTrace(System.out);
		//
		// loaded version has been deleted
		//
		return true;
	}

	//
	// if never saved, then it has changed (from null to something)
	//
	if (savedGeometryXML == null){
		return true;
	}

	//
	// if comparison fails, then it changed
	//
	try{
		if (geometryXML==null){
			geometryXML = XmlHelper.geometryToXML(geometry);
		}
		if (!VCMLComparator.compareEquals(savedGeometryXML,geometryXML)){
			return true;
		}
	}catch (cbit.vcell.xml.XmlParseException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}

	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
public boolean isChanged(MathModel mathModel, String mathModelXML) throws DataAccessException {

	if(isChangedVersion(mathModel.getVersion())){
		//
		// never been saved before (has changed with respect to database)
		//
		return true;
		
	}else{
		//
		// check for name change
		//
		if (!mathModel.getVersion().getName().equals(mathModel.getName())){
			return true;
		}
		//
		// check for annotation change
		//
		if (!mathModel.getVersion().getAnnot().equals(mathModel.getDescription())){
			return true;
		}
		
		//
		// check for same number of simulations as saved version
		//
		MathModelInfo savedMathModelInfo = getMathModelInfo(mathModel.getVersion().getVersionKey());
		if (savedMathModelInfo==null){
			//
			// if savedMathModelInfo is null, then the record was deleted
			// while it was loaded in client (changed is true)
			//
			System.out.println("MathModel("+mathModel.getVersion().getVersionKey()+") must have been deleted, therefore isChanged() is true");
			return true;
		}
		//MathModelMetaData savedMathModelMetaData = getMathModelMetaData(savedMathModelInfo);
			
		//if (savedMathModelMetaData.getNumSimulations() != mathModel.getNumSimulations()){
			//return true;
		//}
		//
		// compare saved and this bioModel
		//
		String savedMathModelXML = getMathModelXML(mathModel.getVersion().getVersionKey());
		if (savedMathModelXML == null){
			// must have been deleted
			return true;
		}
		try {
			if (mathModelXML==null){
				mathModelXML = XmlHelper.mathModelToXML(mathModel);
			}
			if (VCMLComparator.compareEquals(savedMathModelXML,mathModelXML)){
				return false;
			}else{
				return true;
			}
		}catch (cbit.vcell.xml.XmlParseException e){
			e.printStackTrace(System.out);
			throw new DataAccessException(e.getMessage());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/01 4:58:40 PM)
 */
public boolean isChanged(Simulation sim) throws DataAccessException{
	
	// Assumes all Simulations from Loaded (MM.BM) are cached inside ClientDocumentManager

	if(isChangedVersion(sim.getVersion())){
		return true;
	}

	KeyValue simKey = sim.getVersion().getVersionKey();
	if(simKey == null){
		throw new RuntimeException(this.getClass().getName()+".isChanged(Simulation): Sim Version not null, Simulation key is null");
	}
	
	String loadedSimXML = (String)xmlHash.get(simKey);
	if(loadedSimXML == null){
		throw new RuntimeException("Expecting simulation key="+simKey+" to be in ClientDocumentManager cache");
	}

	try{
		return !VCMLComparator.compareEquals(cbit.vcell.xml.XmlHelper.simToXML(sim),loadedSimXML);
	}catch (cbit.vcell.xml.XmlParseException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 5:57:00 PM)
 * @return boolean
 * @param v cbit.sql.Version
 */
private boolean isChangedVersion(Version v) {

	if (v == null || !v.getOwner().equals(sessionManager.getUser())){
		return true;
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 12:33:16 PM)
 * @param simKeys cbit.sql.KeyValue[]
 */
private void preloadSimulationStatus(KeyValue[] simKeys) {
System.out.println("should thread pre-fetch of SimulationJobStatus in ClientDocumentManager.preloadSimulationJobStatus()");
	boolean bNeedRefreshStatus = false;
	for (int i = 0; i < simKeys.length; i++){
		if (!simulationStatusHash.containsKey(simKeys[i])){
			bNeedRefreshStatus = true;
		}
	}
	if (bNeedRefreshStatus){
		try {
			SimulationStatus[] simulationStatusArray = null;
			try {
				simulationStatusArray = sessionManager.getUserMetaDbServer().getSimulationStatus(simKeys);
			}catch (RemoteException e){
				handleRemoteException(e);
				try {
					simulationStatusArray = sessionManager.getUserMetaDbServer().getSimulationStatus(simKeys);
				}catch (RemoteException e2){
					handleRemoteException(e2);
				}
			}
			for (int i = 0; i < simKeys.length; i++){
				SimulationStatus simulationStatus = null;
				for (int j = 0; simulationStatusArray!=null && j < simulationStatusArray.length; j++){
					// these are server-supplied statuses, jobStatus array is not null
					if (simulationStatusArray[j] != null && simulationStatusArray[j].getVCSimulationIdentifier().getSimulationKey().equals(simKeys[i])){
						simulationStatus = simulationStatusArray[j];
					}
				}
				simulationStatusHash.put(simKeys[i],simulationStatus);
			}
		}catch (DataAccessException e){
			e.printStackTrace(System.out);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 5:33:21 PM)
 * @return cbit.vcell.biomodel.BioModelInfo[]
 */
private void reloadBioModelInfos() throws DataAccessException {
	try {
		System.out.println("ClientDocumentManager.reloadBioModelInfos()");
		BioModelInfo bioModelInfos[] = sessionManager.getUserMetaDbServer().getBioModelInfos(true);
		if (bioModelInfos!=null){
			bioModelInfoHash.clear();
			for (int i=0;i<bioModelInfos.length;i++){
				bioModelInfoHash.put(bioModelInfos[i].getVersion().getVersionKey(),bioModelInfos[i]);
			}
		}
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException("RemoteException: "+e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 5:33:21 PM)
 * @return cbit.vcell.biomodel.BioModelInfo[]
 */
private void reloadGeometryInfos() throws DataAccessException {
	try {
		System.out.println("ClientDocumentManager.reloadGeometryInfos()");
		GeometryInfo geometryInfos[] = sessionManager.getUserMetaDbServer().getGeometryInfos(true);
		if (geometryInfos!=null){
			geoInfoHash.clear();
			for (int i=0;i<geometryInfos.length;i++){
				geoInfoHash.put(geometryInfos[i].getVersion().getVersionKey(),geometryInfos[i]);
			}
		}
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException("RemoteException: "+e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 5:33:21 PM)
 * @return cbit.vcell.biomodel.BioModelInfo[]
 */
private void reloadMathModelInfos() throws DataAccessException {
	try {
		System.out.println("ClientDocumentManager.reloadMathModelInfos()");
		MathModelInfo mathModelInfos[] = sessionManager.getUserMetaDbServer().getMathModelInfos(true);
		if (mathModelInfos!=null){
			mathModelInfoHash.clear();
			for (int i=0;i<mathModelInfos.length;i++){
				mathModelInfoHash.put(mathModelInfos[i].getVersion().getVersionKey(),mathModelInfos[i]);
			}
		}
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException("RemoteException: "+e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 5:33:21 PM)
 * @return cbit.vcell.biomodel.BioModelInfo[]
 */
private void reloadVCImageInfos() throws DataAccessException {
	try {
		System.out.println("ClientDocumentManager.reloadVCImageInfos()");
		VCImageInfo vcImageInfos[] = sessionManager.getUserMetaDbServer().getVCImageInfos(true);
		if (vcImageInfos!=null){
			imgInfoHash.clear();
			for (int i=0;i<vcImageInfos.length;i++){
				imgInfoHash.put(vcImageInfos[i].getVersion().getVersionKey(),vcImageInfos[i]);
			}
		}
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException("RemoteException: "+e.getMessage());
	}
}


/**
 * 
 * @param newListener cbit.vcell.clientdb.DatabaseListener
 */
public void removeDatabaseListener(cbit.vcell.client.database.DatabaseListener newListener) {
	aDatabaseListener = cbit.vcell.client.database.DatabaseEventMulticaster.remove(aDatabaseListener, newListener);
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public VCImageInfo removeUserFromGroup(VCImageInfo imageInfo, String userToRemove) throws DataAccessException {

	try {
		//
		// publish from database
		//
		VCImageInfo newImageInfo = (VCImageInfo)removeUserFromGroup0(imageInfo,VersionableType.VCImage,imgInfoHash,userToRemove);
		//
		// delete Image from cache
		//
		xmlHash.remove(imageInfo.getVersion().getVersionKey());

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, imageInfo, newImageInfo));

		return newImageInfo;
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public BioModelInfo removeUserFromGroup(BioModelInfo bioModelInfo, String userToRemove) throws DataAccessException {

	try {
		//
		// publish from database
		//
		BioModelInfo newBioModelInfo = (BioModelInfo)removeUserFromGroup0(bioModelInfo,VersionableType.BioModelMetaData,bioModelInfoHash,userToRemove);
		//
		// delete BioModelMetaData from cache
		//
		xmlHash.remove(bioModelInfo.getVersion().getVersionKey());

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, bioModelInfo, newBioModelInfo));

		return newBioModelInfo;
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public GeometryInfo removeUserFromGroup(GeometryInfo geometryInfo, String userToRemove) throws DataAccessException {

	try {
		//
		// publish from database
		//
		GeometryInfo newGeometryInfo = (GeometryInfo)removeUserFromGroup0(geometryInfo,VersionableType.Geometry,geoInfoHash,userToRemove);
		////
		//// delete Geometry from cache
		////
		//xmlHash.remove(geometryInfo.getVersion().getVersionKey());
		////
		//// delete any MathModelMetaData's that use this GeometryInfo from cache
		////
		//MathModelInfo referencedMathModelInfos[] = getMathModelReferences(geometryInfo);
		//for (int i = 0; i < referencedMathModelInfos.length; i++){
			//xmlHash.remove(referencedMathModelInfos[i].getVersion().getVersionKey());
		//}
		////
		//// delete any BioModelMetaData's that use this GeometryInfo from cache
		////
		//BioModelInfo referencedBioModelInfos[] = getBioModelReferences(geometryInfo);
		//for (int i = 0; i < referencedBioModelInfos.length; i++){
			//xmlHash.remove(referencedBioModelInfos[i].getVersion().getVersionKey());
		//}

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, geometryInfo, newGeometryInfo));

		return newGeometryInfo;
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public MathModelInfo removeUserFromGroup(MathModelInfo mathModelInfo, String userToRemove) throws DataAccessException {

	try {
		//
		// publish from database
		//
		MathModelInfo newMathModelInfo = (MathModelInfo)removeUserFromGroup0(mathModelInfo,VersionableType.MathModelMetaData,mathModelInfoHash, userToRemove);
		//
		// delete MathModelMetaData from cache
		//
		xmlHash.remove(mathModelInfo.getVersion().getVersionKey());

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, mathModelInfo, newMathModelInfo));

		return newMathModelInfo;
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
private VersionInfo removeUserFromGroup0(VersionInfo versionInfo, VersionableType vType, Hashtable vInfoHash, String userToAdd) throws RemoteException, DataAccessException {

	//
	// unpublish from database
	//
	UserMetaDbServer dbServer = sessionManager.getUserMetaDbServer();
	VersionInfo newVersionInfo = dbServer.groupRemoveUser(vType,versionInfo.getVersion().getVersionKey(),userToAdd,false);
	
	//
	// replace versionInfo in hashTable
	//
	vInfoHash.remove(versionInfo.getVersion().getVersionKey());
	vInfoHash.put(newVersionInfo.getVersion().getVersionKey(),newVersionInfo);

	//
	// refresh versionInfos of child Geometries and Images (refresh all for now)
	//
	//
	// Removed because of deep cloning of children
	//
	//if (vType.equals(VersionableType.MathModelMetaData) || vType.equals(VersionableType.BioModelMetaData)){
	//	reloadInfos(VersionableType.Geometry,geoInfoHash);
	//	reloadInfos(VersionableType.VCImage,imgInfoHash);
	//}

	return newVersionInfo;
}


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public void replacePreferences(Preference[] argPreferences) throws cbit.util.DataAccessException{

	if (argPreferences==null){
		throw new IllegalArgumentException("preferences were null");
	}
	System.out.println("ClientDocumentManager.replacePreferences()");
	if (!Compare.isEqual(argPreferences,getPreferences())){		
		try{
			sessionManager.getUserMetaDbServer().replacePreferences(argPreferences);
			preferences = argPreferences;
		}catch (RemoteException e){
			handleRemoteException(e);
			throw new DataAccessException(e.getMessage());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/01 4:58:40 PM)
 */
public cbit.image.VCImage save(cbit.image.VCImage vcImage) throws cbit.util.DataAccessException {
	try {
		String vcImageXML = null;
		try {
			vcImageXML = cbit.vcell.xml.XmlHelper.imageToXML(vcImage);
		}catch (cbit.vcell.xml.XmlParseException e){
			e.printStackTrace(System.out);
			throw new DataAccessException(e.getMessage());
		}

		String savedVCImageXML = sessionManager.getUserMetaDbServer().saveVCImage(new BigString(vcImageXML)).toString();

		VCImage savedVCImage = null;
		try {
			savedVCImage = cbit.vcell.xml.XmlHelper.XMLToImage(savedVCImageXML);
		}catch (cbit.vcell.xml.XmlParseException e){
			e.printStackTrace(System.out);
			throw new DataAccessException(e.getMessage());
		}
		savedVCImage.refreshDependencies();
		
		KeyValue savedKey = savedVCImage.getVersion().getVersionKey();

		if (xmlHash.get(savedKey)==null){
			xmlHash.put(savedKey, savedVCImageXML);
		}

		try {
			ISize size = new ISize(savedVCImage.getNumX(),savedVCImage.getNumY(),savedVCImage.getNumZ());
			cbit.image.GIFImage browseData = cbit.image.BrowseImage.makeBrowseGIFImage(savedVCImage);
			VCImageInfo savedVCImageInfo = new VCImageInfo(savedVCImage.getVersion(),size,savedVCImage.getExtent(),browseData);
			imgInfoHash.put(savedKey,savedVCImageInfo);
			
			fireDatabaseInsert(new DatabaseEvent(this, DatabaseEvent.INSERT, null, savedVCImageInfo));
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
		
		return savedVCImage;
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
public BioModel save(BioModel bioModel, String independentSims[]) throws DataAccessException {
	
	try {
		String bioModelXML = null;
		try {
			bioModelXML = cbit.vcell.xml.XmlHelper.bioModelToXML(bioModel);
		}catch (cbit.vcell.xml.XmlParseException e){
			e.printStackTrace(System.out);
			throw new DataAccessException(e.getMessage());
		}
		
		String savedBioModelXML = sessionManager.getUserMetaDbServer().saveBioModel(new BigString(bioModelXML),independentSims).toString();

		BioModel savedBioModel = getBioModelFromDatabaseXML(savedBioModelXML);
		
		KeyValue savedKey = savedBioModel.getVersion().getVersionKey();

		if (xmlHash.get(savedKey)==null){
			xmlHash.put(savedKey, savedBioModelXML);
		}

		BioModelInfo savedBioModelInfo = new BioModelInfo(savedBioModel.getVersion(),savedBioModel.getModel().getKey(),BioModelChildSummary.fromDatabaseBioModel(savedBioModel));
		bioModelInfoHash.put(savedKey,savedBioModelInfo);
		fireDatabaseInsert(new DatabaseEvent(this, DatabaseEvent.INSERT, null, savedBioModelInfo));

		return savedBioModel;
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
public Geometry save(Geometry geometry) throws DataAccessException {
	try {
		String geometryXML = null;
		try {
			geometryXML = cbit.vcell.xml.XmlHelper.geometryToXML(geometry);
		}catch (cbit.vcell.xml.XmlParseException e){
			e.printStackTrace(System.out);
			throw new DataAccessException(e.getMessage());
		}

		String savedGeometryXML = sessionManager.getUserMetaDbServer().saveGeometry(new BigString(geometryXML)).toString();

		Geometry savedGeometry = getGeometryFromDatabaseXML(savedGeometryXML);
		
		KeyValue savedKey = savedGeometry.getVersion().getVersionKey();

		if (xmlHash.get(savedKey)==null){
			xmlHash.put(savedKey, savedGeometryXML);
		}

		KeyValue imageRef = (savedGeometry.getGeometrySpec().getImage()!=null)?(savedGeometry.getGeometrySpec().getImage().getKey()):(null);
		GeometryInfo savedGeometryInfo = new GeometryInfo(savedGeometry.getVersion(),savedGeometry.getDimension(),savedGeometry.getExtent(),savedGeometry.getOrigin(),imageRef);
		geoInfoHash.put(savedKey,savedGeometryInfo);
		
		fireDatabaseInsert(new DatabaseEvent(this, DatabaseEvent.INSERT, null, savedGeometryInfo));

		return savedGeometry;
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
public MathModel save(MathModel mathModel, String independentSims[]) throws DataAccessException {
	
	try {
		String mathModelXML = null;
		try {
			mathModelXML = cbit.vcell.xml.XmlHelper.mathModelToXML(mathModel);
		}catch (cbit.vcell.xml.XmlParseException e){
			e.printStackTrace(System.out);
			throw new DataAccessException(e.getMessage());
		}
			
		String savedMathModelXML = sessionManager.getUserMetaDbServer().saveMathModel(new BigString(mathModelXML),independentSims).toString();

		MathModel savedMathModel = getMathModelFromDatabaseXML(savedMathModelXML);
		
		KeyValue savedKey = savedMathModel.getVersion().getVersionKey();

		if (xmlHash.get(savedKey)==null){
			xmlHash.put(savedKey, savedMathModelXML);
		}

		MathModelInfo savedMathModelInfo = new MathModelInfo(savedMathModel.getVersion(),savedMathModel.getMathDescription().getKey(),MathModelChildSummary.fromDatabaseMathModel(savedMathModel));
		mathModelInfoHash.put(savedKey,savedMathModelInfo);
		
		fireDatabaseInsert(new DatabaseEvent(this, DatabaseEvent.INSERT, null, savedMathModelInfo));

		return savedMathModel;
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 * @deprecated  for testing purposes only
 */
public Simulation save(Simulation simulation, boolean bForceIndependent) throws DataAccessException {
	
	try {
		String simulationXML = null;
		try {
			simulationXML = cbit.vcell.xml.XmlHelper.simToXML(simulation);
		}catch (cbit.vcell.xml.XmlParseException e){
			e.printStackTrace(System.out);
			throw new DataAccessException(e.getMessage());
		}
			
		String savedSimulationXML = sessionManager.getUserMetaDbServer().saveSimulation(new BigString(simulationXML),bForceIndependent).toString();

		Simulation savedSimulation = getSimulationFromDatabaseXML(savedSimulationXML);

		return savedSimulation;
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/01 4:58:40 PM)
 */
public cbit.image.VCImage saveAsNew(cbit.image.VCImage vcImage, java.lang.String newName) throws cbit.util.DataAccessException {
	try {		
		String vcImageXML = null;
		try {
			vcImageXML = cbit.vcell.xml.XmlHelper.imageToXML(vcImage);
		}catch (cbit.vcell.xml.XmlParseException e){
			e.printStackTrace(System.out);
			throw new DataAccessException(e.getMessage());
		}

		String savedVCImageXML = sessionManager.getUserMetaDbServer().saveVCImageAs(new BigString(vcImageXML), newName).toString();

		VCImage savedVCImage = null;
		try {
			savedVCImage = cbit.vcell.xml.XmlHelper.XMLToImage(savedVCImageXML);
		}catch (cbit.vcell.xml.XmlParseException e){
			e.printStackTrace(System.out);
			throw new DataAccessException(e.getMessage());
		}
		savedVCImage.refreshDependencies();
		
		KeyValue savedKey = savedVCImage.getVersion().getVersionKey();

		if (xmlHash.get(savedKey)==null){
			xmlHash.put(savedKey, savedVCImageXML);
		}

		try {
			ISize size = new ISize(savedVCImage.getNumX(),savedVCImage.getNumY(),savedVCImage.getNumZ());
			cbit.image.GIFImage browseData = cbit.image.BrowseImage.makeBrowseGIFImage(savedVCImage);
			VCImageInfo savedVCImageInfo = new VCImageInfo(savedVCImage.getVersion(),size,savedVCImage.getExtent(),browseData);
			imgInfoHash.put(savedKey,savedVCImageInfo);
			
			fireDatabaseInsert(new DatabaseEvent(this, DatabaseEvent.INSERT, null, savedVCImageInfo));
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
		
		return savedVCImage;
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/01 11:27:52 AM)
 */
public cbit.vcell.biomodel.BioModel saveAsNew(cbit.vcell.biomodel.BioModel bioModel, java.lang.String newName, String independentSims[]) throws cbit.util.DataAccessException {
	
	try {
		String bioModelXML = null;
		try {
			bioModelXML = cbit.vcell.xml.XmlHelper.bioModelToXML(bioModel);
		}catch (cbit.vcell.xml.XmlParseException e){
			e.printStackTrace(System.out);
			throw new DataAccessException(e.getMessage());
		}

		String savedBioModelXML = sessionManager.getUserMetaDbServer().saveBioModelAs(new BigString(bioModelXML), newName, independentSims).toString();

		BioModel savedBioModel = getBioModelFromDatabaseXML(savedBioModelXML);
		
		KeyValue savedKey = savedBioModel.getVersion().getVersionKey();

		if (xmlHash.get(savedKey)==null){
			xmlHash.put(savedKey, savedBioModelXML);
		}

		BioModelInfo savedBioModelInfo = new BioModelInfo(savedBioModel.getVersion(),savedBioModel.getModel().getKey(),BioModelChildSummary.fromDatabaseBioModel(savedBioModel));
		bioModelInfoHash.put(savedKey,savedBioModelInfo);
		
		fireDatabaseInsert(new DatabaseEvent(this, DatabaseEvent.INSERT, null, savedBioModelInfo));

		return savedBioModel;
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/01 11:27:52 AM)
 */
public cbit.vcell.geometry.Geometry saveAsNew(cbit.vcell.geometry.Geometry geometry, java.lang.String newName) throws cbit.util.DataAccessException {
	try {
		String geometryXML = null;
		try {
			geometryXML = cbit.vcell.xml.XmlHelper.geometryToXML(geometry);
		}catch (cbit.vcell.xml.XmlParseException e){
			e.printStackTrace(System.out);
			throw new DataAccessException(e.getMessage());
		}

		if (isChanged(geometry,geometryXML)==false){
			System.out.println("<<<<<WARNING>>>>>>>ClientDocumentManger.save(Geometry), called on unchanged geometry");
			return geometry;
		}
		
		String savedGeometryXML = sessionManager.getUserMetaDbServer().saveGeometryAs(new BigString(geometryXML), newName).toString();

		Geometry savedGeometry = getGeometryFromDatabaseXML(savedGeometryXML);
		
		KeyValue savedKey = savedGeometry.getVersion().getVersionKey();

		if (xmlHash.get(savedKey)==null){
			xmlHash.put(savedKey, savedGeometryXML);
		}

		KeyValue imageRef = (savedGeometry.getGeometrySpec().getImage()!=null)?(savedGeometry.getGeometrySpec().getImage().getKey()):(null);
		GeometryInfo savedGeometryInfo = new GeometryInfo(savedGeometry.getVersion(),savedGeometry.getDimension(),savedGeometry.getExtent(),savedGeometry.getOrigin(),imageRef);
		geoInfoHash.put(savedKey,savedGeometryInfo);
		
		fireDatabaseInsert(new DatabaseEvent(this, DatabaseEvent.INSERT, null, savedGeometryInfo));

		return savedGeometry;
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/01 11:27:52 AM)
 */
public cbit.vcell.mathmodel.MathModel saveAsNew(cbit.vcell.mathmodel.MathModel mathModel, java.lang.String newName, String independentSims[]) throws cbit.util.DataAccessException {
	try {
		String mathModelXML = null;
		try {
			mathModelXML = cbit.vcell.xml.XmlHelper.mathModelToXML(mathModel);
		}catch (cbit.vcell.xml.XmlParseException e){
			e.printStackTrace(System.out);
			throw new DataAccessException(e.getMessage());
		}

		String savedMathModelXML = sessionManager.getUserMetaDbServer().saveMathModelAs(new BigString(mathModelXML), newName, independentSims).toString();

		MathModel savedMathModel = getMathModelFromDatabaseXML(savedMathModelXML);
		
		KeyValue savedKey = savedMathModel.getVersion().getVersionKey();

		if (xmlHash.get(savedKey)==null){
			xmlHash.put(savedKey, savedMathModelXML);
		}

		MathModelInfo savedMathModelInfo = new MathModelInfo(savedMathModel.getVersion(),savedMathModel.getMathDescription().getKey(),MathModelChildSummary.fromDatabaseMathModel(savedMathModel));
		mathModelInfoHash.put(savedKey,savedMathModelInfo);
		
		fireDatabaseInsert(new DatabaseEvent(this, DatabaseEvent.INSERT, null, savedMathModelInfo));

		return savedMathModel;
	}catch (RemoteException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public VCImageInfo setGroupPrivate(VCImageInfo imageInfo) throws DataAccessException {

	try {
		//
		// unpublish from database
		//
		VCImageInfo newImageInfo = (VCImageInfo)setGroupPrivate0(imageInfo,VersionableType.VCImage,imgInfoHash);
		//
		// delete Image from cache
		//
		xmlHash.remove(imageInfo.getVersion().getVersionKey());

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, imageInfo, newImageInfo));

		return newImageInfo;
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public BioModelInfo setGroupPrivate(BioModelInfo bioModelInfo) throws DataAccessException {

	try {
		//
		// unpublish from database
		//
		BioModelInfo newBioModelInfo = (BioModelInfo)setGroupPrivate0(bioModelInfo,VersionableType.BioModelMetaData,bioModelInfoHash);
		//
		// delete BioModelMetaData from cache
		//
		xmlHash.remove(bioModelInfo.getVersion().getVersionKey());

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, bioModelInfo, newBioModelInfo));

		return newBioModelInfo;
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public GeometryInfo setGroupPrivate(GeometryInfo geometryInfo) throws DataAccessException {

	try {
		//
		// unpublish from database
		//
		GeometryInfo newGeometryInfo = (GeometryInfo)setGroupPrivate0(geometryInfo,VersionableType.Geometry,geoInfoHash);
		////
		//// delete Geometry from cache
		////
		//xmlHash.remove(geometryInfo.getVersion().getVersionKey());
		////
		//// delete any MathModelMetaData's that use this GeometryInfo from cache
		////
		//MathModelInfo referencedMathModelInfos[] = getMathModelReferences(geometryInfo);
		//for (int i = 0; i < referencedMathModelInfos.length; i++){
			//xmlHash.remove(referencedMathModelInfos[i].getVersion().getVersionKey());
		//}
		////
		//// delete any BioModelMetaData's that use this GeometryInfo from cache
		////
		//BioModelInfo referencedBioModelInfos[] = getBioModelReferences(geometryInfo);
		//for (int i = 0; i < referencedBioModelInfos.length; i++){
			//xmlHash.remove(referencedBioModelInfos[i].getVersion().getVersionKey());
		//}

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, geometryInfo, newGeometryInfo));

		return newGeometryInfo;
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public MathModelInfo setGroupPrivate(MathModelInfo mathModelInfo) throws DataAccessException {

	try {
		//
		// unpublish from database
		//
		MathModelInfo newMathModelInfo = (MathModelInfo)setGroupPrivate0(mathModelInfo,VersionableType.MathModelMetaData,mathModelInfoHash);
		//
		// delete MathModelMetaData from cache
		//
		xmlHash.remove(mathModelInfo.getVersion().getVersionKey());

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, mathModelInfo, newMathModelInfo));

		return newMathModelInfo;
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
private VersionInfo setGroupPrivate0(VersionInfo versionInfo, VersionableType vType, Hashtable vInfoHash) throws RemoteException, DataAccessException {

	//
	// unpublish from database
	//
	UserMetaDbServer dbServer = sessionManager.getUserMetaDbServer();
	VersionInfo newVersionInfo = dbServer.groupSetPrivate(vType,versionInfo.getVersion().getVersionKey());
	
	//
	// replace versionInfo in hashTable
	//
	vInfoHash.remove(versionInfo.getVersion().getVersionKey());
	vInfoHash.put(newVersionInfo.getVersion().getVersionKey(),newVersionInfo);

	//
	// refresh versionInfos of child Geometries and Images (refresh all for now)
	//
	//
	// Removed because of deep cloning of children
	//
	//if (vType.equals(VersionableType.MathModelMetaData) || vType.equals(VersionableType.BioModelMetaData)){
	//	reloadInfos(VersionableType.Geometry,geoInfoHash);
	//	reloadInfos(VersionableType.VCImage,imgInfoHash);
	//}

	return newVersionInfo;
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public VCImageInfo setGroupPublic(VCImageInfo imageInfo) throws DataAccessException {

	try {
		//
		// publish from database
		//
		VCImageInfo newImageInfo = (VCImageInfo)setGroupPublic0(imageInfo,VersionableType.VCImage,imgInfoHash);
		//
		// delete Image from cache
		//
		xmlHash.remove(imageInfo.getVersion().getVersionKey());

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, imageInfo, newImageInfo));

		return newImageInfo;
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public BioModelInfo setGroupPublic(BioModelInfo bioModelInfo) throws DataAccessException {

	try {
		//
		// publish from database
		//
		BioModelInfo newBioModelInfo = (BioModelInfo)setGroupPublic0(bioModelInfo,VersionableType.BioModelMetaData,bioModelInfoHash);
		//
		// delete BioModelMetaData from cache
		//
		xmlHash.remove(bioModelInfo.getVersion().getVersionKey());

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, bioModelInfo, newBioModelInfo));

		return newBioModelInfo;
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public GeometryInfo setGroupPublic(GeometryInfo geometryInfo) throws DataAccessException {

	try {
		//
		// publish from database
		//
		GeometryInfo newGeometryInfo = (GeometryInfo)setGroupPublic0(geometryInfo,VersionableType.Geometry,geoInfoHash);
		////
		//// delete Geometry from cache
		////
		//xmlHash.remove(geometryInfo.getVersion().getVersionKey());
		////
		//// delete any MathModelMetaData's that use this GeometryInfo from cache
		////
		//MathModelInfo referencedMathModelInfos[] = getMathModelReferences(geometryInfo);
		//for (int i = 0; i < referencedMathModelInfos.length; i++){
			//xmlHash.remove(referencedMathModelInfos[i].getVersion().getVersionKey());
		//}
		////
		//// delete any BioModelMetaData's that use this GeometryInfo from cache
		////
		//BioModelInfo referencedBioModelInfos[] = getBioModelReferences(geometryInfo);
		//for (int i = 0; i < referencedBioModelInfos.length; i++){
			//xmlHash.remove(referencedBioModelInfos[i].getVersion().getVersionKey());
		//}

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, geometryInfo, newGeometryInfo));

		return newGeometryInfo;
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
public MathModelInfo setGroupPublic(MathModelInfo mathModelInfo) throws DataAccessException {

	try {
		//
		// publish from database
		//
		MathModelInfo newMathModelInfo = (MathModelInfo)setGroupPublic0(mathModelInfo,VersionableType.MathModelMetaData,mathModelInfoHash);
		//
		// delete MathModelMetaData from cache
		//
		xmlHash.remove(mathModelInfo.getVersion().getVersionKey());

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, mathModelInfo, newMathModelInfo));

		return newMathModelInfo;
		
	}catch (RemoteException e){
		handleRemoteException(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
private VersionInfo setGroupPublic0(VersionInfo versionInfo, VersionableType vType, Hashtable vInfoHash) throws RemoteException, DataAccessException {

	//
	// publish from database
	//
	UserMetaDbServer dbServer = sessionManager.getUserMetaDbServer();
	VersionInfo newVersionInfo = dbServer.groupSetPublic(vType,versionInfo.getVersion().getVersionKey());
	
	//
	// replace versionInfo in hashtable
	//
	vInfoHash.remove(versionInfo.getVersion().getVersionKey());
	vInfoHash.put(newVersionInfo.getVersion().getVersionKey(),newVersionInfo);

	//
	// refresh versionInfos of child Geometries and Images (refresh all for now)
	//
	//
	// Removed because of deep cloning of children
	//
	//if (vType.equals(VersionableType.MathModelMetaData) || vType.equals(VersionableType.BioModelMetaData)){
	//	reloadInfos(VersionableType.Geometry,geoInfoHash);
	//	reloadInfos(VersionableType.VCImage,imgInfoHash);
	//}
	
	return newVersionInfo;
}


/**
 * Insert the method's description here.
 * Creation date: (10/14/2005 1:40:39 AM)
 * @param jobEvent cbit.rmi.event.SimulationJobStatusEvent
 * @exception cbit.util.DataAccessException The exception description.
 */
public void updateServerSimulationStatusFromJobEvent(cbit.rmi.event.SimulationJobStatusEvent jobEvent) throws cbit.util.DataAccessException {
	simulationStatusHash.put(jobEvent.getVCSimulationIdentifier().getSimulationKey(), SimulationStatus.updateFromJobEvent(getServerSimulationStatus(jobEvent.getVCSimulationIdentifier()), jobEvent));
}
}
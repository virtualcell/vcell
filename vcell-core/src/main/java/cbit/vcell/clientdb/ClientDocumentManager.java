/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.clientdb;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import cbit.vcell.message.server.bootstrap.client.RemoteProxyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import org.vcell.util.BeanUtils;
import org.vcell.util.BigString;
import org.vcell.util.Compare;
import org.vcell.util.DataAccessException;
import org.vcell.util.ISize;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.Preference;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.CurateSpec;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.ReferenceQueryResult;
import org.vcell.util.document.ReferenceQuerySpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.VCInfoContainer;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.VersionableType;
import org.vcell.util.document.VersionableTypeVersion;

import cbit.image.BrowseImage;
import cbit.image.GIFImage;
import cbit.image.VCImage;
import cbit.image.VCImageInfo;
import cbit.rmi.event.MessageEvent;
import cbit.rmi.event.PerformanceData;
import cbit.rmi.event.PerformanceDataEntry;
import cbit.rmi.event.PerformanceMonitorEvent;
import cbit.rmi.event.SimulationJobStatusEvent;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.DBFormalSpecies;
import cbit.vcell.model.DBSpecies;
import cbit.vcell.model.FormalSpeciesType;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionDescription;
import cbit.vcell.model.ReactionQuerySpec;
import cbit.vcell.model.ReactionStepInfo;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestSuiteNew;
import cbit.vcell.numericstest.TestSuiteOP;
import cbit.vcell.numericstest.TestSuiteOPResults;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.publish.ITextWriter;
import cbit.vcell.server.SessionManager;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.server.UserMetaDbServer;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.xml.VCMLComparator;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.xml.XmlReader;

public class ClientDocumentManager implements DocumentManager{
	private static final Logger lg = LogManager.getLogger(ClientDocumentManager.class);
	//
	//
	private SessionManager sessionManager = null;

	//
	// cacheTable holds xml for (BioModel, MathModel, Simulation, Geometry, VCImage)
	//
	private Hashtable<KeyValue, String> xmlHash = new Hashtable<KeyValue, String>();
	//
	// hashTables holds all info objects (including BioModelMetaData WITH info objects)
	//
//	private boolean bMathModelInfosDirty = true;
//	private boolean bImageInfosDirty = true;
//	private boolean bBioModelInfosDirty = true;
//	private boolean bGeometryInfosDirty = true;
	
	private Hashtable<KeyValue, GeometryInfo> geoInfoHash = new Hashtable<KeyValue, GeometryInfo>();
	private Hashtable<KeyValue, VCImageInfo> imgInfoHash = new Hashtable<KeyValue, VCImageInfo>();
	private Hashtable<KeyValue, MathModelInfo> mathModelInfoHash = new Hashtable<KeyValue, MathModelInfo>();
	private Hashtable<KeyValue, BioModelInfo> bioModelInfoHash = new Hashtable<KeyValue, BioModelInfo>();
	private HashMap<KeyValue, SimulationStatus> simulationStatusHash = new HashMap<KeyValue, SimulationStatus>();
	
	private Preference preferences[] = null;
	
	protected transient DatabaseListener aDatabaseListener = null;
	private transient HashSet<FieldDataDBEventListener> fieldDataDBEventListenerH = null;
	
	private class XMLHolder<T extends VCDocument> {
		private String xmlString;
		private T document;
		
		public XMLHolder(String xmlString){
			this(xmlString, null);
		}
		
		public XMLHolder(String xmlString, T document){
			if (xmlString == null){
				throw new IllegalArgumentException("xmlString cannot be null");
			}
			this.xmlString = xmlString;
			this.document = document;
		}
		
		public final String getXmlString() {
			return xmlString;
		}
		public final T getDocument() {
			return document;
		}
	}


	
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
public void addDatabaseListener(DatabaseListener newListener) {
	aDatabaseListener = DatabaseEventMulticaster.add(aDatabaseListener, newListener);
	return;
}

public void addFieldDataDBListener(FieldDataDBEventListener newFieldDataDBEventListener) {
	if(fieldDataDBEventListenerH == null){
		fieldDataDBEventListenerH = new HashSet<FieldDataDBEventListener>();
	}
	fieldDataDBEventListenerH.add(newFieldDataDBEventListener);
}

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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}	
}

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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}	
}

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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}	
}

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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}	
}

private <T extends VersionInfo> T addUserToGroup0(VersionInfo versionInfo, VersionableType vType, Hashtable<KeyValue,T> vInfoHash, String userToAdd) throws RemoteProxyException, DataAccessException {

	//
	// unpublish from database
	//
	UserMetaDbServer dbServer = sessionManager.getUserMetaDbServer();
	@SuppressWarnings("unchecked")
	T newVersionInfo = (T) dbServer.groupAddUser(vType,versionInfo.getVersion().getVersionKey(),userToAdd,false);
	
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

private void cacheSimulations(Simulation[] sims) throws DataAccessException{
	System.out.println("ClientDocumentManager.cacheSimulations() has been disabled until needed");
	//try{
		//for(int i=0;i<sims.length;i+= 1){
			//xmlHash.put(sims[i].getVersion().getVersionKey(),cbit.vcell.xml.XmlHelper.simToXML(sims[i]));
		//}
	//}catch(Exception e){
		//lg.error(e);
		//throw new DataAccessException(e.getClass().getName()+": "+e.getMessage());
	//}
}

public void curate(CurateSpec curateSpec) throws DataAccessException{
	
	try{
		VCDocumentInfo newVCDocumentInfo = getSessionManager().getUserMetaDbServer().curate(curateSpec);
		
		xmlHash.remove(curateSpec.getVCDocumentInfo().getVersion().getVersionKey());
		
		if(curateSpec.getVCDocumentInfo() instanceof MathModelInfo){
			mathModelInfoHash.remove(curateSpec.getVCDocumentInfo().getVersion().getVersionKey());
			mathModelInfoHash.put(newVCDocumentInfo.getVersion().getVersionKey(), (MathModelInfo)newVCDocumentInfo);
		}else if(curateSpec.getVCDocumentInfo() instanceof BioModelInfo){
			bioModelInfoHash.remove(curateSpec.getVCDocumentInfo().getVersion().getVersionKey());
			bioModelInfoHash.put(newVCDocumentInfo.getVersion().getVersionKey(), (BioModelInfo)newVCDocumentInfo);
		}

		fireDatabaseUpdate(new DatabaseEvent(this, DatabaseEvent.UPDATE, curateSpec.getVCDocumentInfo(), newVCDocumentInfo));
		
	}catch(Exception e){
		throw new DataAccessException(e.getClass().getName()+" "+e.getMessage(), e);
	}

}


public void delete(VCImageInfo vcImageInfo) throws DataAccessException {

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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}
}


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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	} 

}


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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}
}


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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}	
}

public ExternalDataIdentifier saveFieldData(FieldDataFileOperationSpec fdos, String fieldDataBaseName) throws DataAccessException {
	FieldDataDBOperationResults fieldDataResults = fieldDataDBOperation(FieldDataDBOperationSpec.createGetExtDataIDsSpec(getUser()));
	ExternalDataIdentifier[] existingExternalIDs = fieldDataResults.extDataIDArr;
	// find available field data name (starting from init).
	for (ExternalDataIdentifier externalID : existingExternalIDs){
		if (externalID.getName().equals(fieldDataBaseName)){
			fieldDataBaseName = TokenMangler.getNextEnumeratedToken(fieldDataBaseName);
		}
	}
   	FieldDataDBOperationSpec newExtDataIDSpec = FieldDataDBOperationSpec.createSaveNewExtDataIDSpec(getUser(),fieldDataBaseName, "");
	ExternalDataIdentifier eid = fieldDataDBOperation(newExtDataIDSpec).extDataID; 
	fdos.specEDI = eid;
	fdos.annotation = "";
	try {
		// Add to Server Disk
		FieldDataFileOperationResults fdor = fieldDataFileOperation(fdos);
		lg.debug(fdor);
	} catch (DataAccessException e) {
		try{
			// try to cleanup new ExtDataID
			fieldDataDBOperation(FieldDataDBOperationSpec.createDeleteExtDataIDSpec(fdos.specEDI));
		}catch(Exception e2){
			// ignore
		}
		fdos.specEDI = null;
		throw e;
	}
	return eid;
}

public FieldDataDBOperationResults fieldDataDBOperation(FieldDataDBOperationSpec fieldDataDBOperationSpec) throws DataAccessException {

	try{
		return sessionManager.getUserMetaDbServer().fieldDataDBOperation(fieldDataDBOperationSpec);	
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}
}

public FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFileOperationSpec) throws DataAccessException {

	return sessionManager.fieldDataFileOperation(fieldDataFileOperationSpec);	
}


public void fieldDataFromSimulation(KeyValue sourceSim, int jobIndex, String newFieldDataName) throws DataAccessException {
	sessionManager.getUserMetaDbServer().fieldDataFromSimulation(sourceSim, jobIndex, newFieldDataName);
}

	public TestSuiteOPResults doTestSuiteOP(TestSuiteOP tsop) throws DataAccessException {
	if(!getUser().isTestAccount()){
		throw new PermissionException("User="+getUser().getName()+" not allowed TestSuiteInfo");
	}
	try {
		TestSuiteOPResults tsopr = getSessionManager().getUserMetaDbServer().doTestSuiteOP(tsop);
		//fireDatabaseRefresh(new DatabaseEvent(this, DatabaseEvent.REFRESH, null, null));
		return tsopr;
	}catch (RemoteProxyException e){
		throw new DataAccessException(e.getMessage(), e);
	}
}


public ReferenceQueryResult findReferences(ReferenceQuerySpec rqs) throws DataAccessException {

	try{
		return getSessionManager().getUserMetaDbServer().findReferences(rqs);
	}catch(Exception e){
		throw new DataAccessException(e.getClass().getName()+" "+e.getMessage(), e);
	}
}


protected void fireDatabaseDelete(DatabaseEvent event) {
	if (aDatabaseListener == null) {
		return;
	};
	if (javax.swing.SwingUtilities.isEventDispatchThread()){
		aDatabaseListener.databaseDelete(event);
	}else{
		final DatabaseEvent evt = event;
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
protected void fireDatabaseInsert(DatabaseEvent event) {
	if (aDatabaseListener == null) {
		return;
	};
	if (javax.swing.SwingUtilities.isEventDispatchThread()){
		aDatabaseListener.databaseInsert(event);
	}else{
		final DatabaseEvent evt = event;
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
protected void fireDatabaseRefresh(DatabaseEvent event) {
	if (aDatabaseListener == null) {
		return;
	};
	if (javax.swing.SwingUtilities.isEventDispatchThread()){
		aDatabaseListener.databaseRefresh(event);
	}else{
		final DatabaseEvent evt = event;
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
protected void fireDatabaseUpdate(DatabaseEvent event) {
	if (aDatabaseListener == null) {
		return;
	};
	if (javax.swing.SwingUtilities.isEventDispatchThread()){
		aDatabaseListener.databaseUpdate(event);
	}else{
		final DatabaseEvent evt = event;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				aDatabaseListener.databaseUpdate(evt);
			}
		});
	}
}


protected void fireFieldDataDB(final FieldDataDBEvent fieldDataDBEvent) {
	if (fieldDataDBEventListenerH == null) {
		return;
	};
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			Iterator<FieldDataDBEventListener> fieldDataDBListenersIter =
				fieldDataDBEventListenerH.iterator();
			while(fieldDataDBListenersIter.hasNext()){
				fieldDataDBListenersIter.next().fieldDataDBEvent(fieldDataDBEvent);
			}
		}
	});
//	}
}


	public void generatePDF(BioModel biomodel, java.io.FileOutputStream fos) throws Exception {

		java.awt.print.PageFormat pageFormat = java.awt.print.PrinterJob.getPrinterJob().defaultPage();
		pageFormat.setOrientation(java.awt.print.PageFormat.PORTRAIT);
		generatePDF(biomodel, fos, pageFormat);
	}


	public void generatePDF(BioModel biomodel, java.io.FileOutputStream fos, java.awt.print.PageFormat pageFormat) throws Exception {

		ITextWriter pdfWriter = ITextWriter.getInstance(ITextWriter.PDF_WRITER);
		pdfWriter.writeBioModel(biomodel, fos, pageFormat);
	}


	public void generatePDF(Geometry geom, java.io.FileOutputStream fos) throws Exception {

		java.awt.print.PageFormat pageFormat = java.awt.print.PrinterJob.getPrinterJob().defaultPage();
		pageFormat.setOrientation(java.awt.print.PageFormat.PORTRAIT);
		generatePDF(geom, fos, pageFormat);
	}


	public void generatePDF(Geometry geom, java.io.FileOutputStream fos, java.awt.print.PageFormat pageFormat) throws Exception {

		ITextWriter pdfWriter = ITextWriter.getInstance(ITextWriter.PDF_WRITER);
		pdfWriter.writeGeometry(geom, fos, pageFormat);
	}


	public void generatePDF(MathModel mathmodel, java.io.FileOutputStream fos) throws Exception {

		java.awt.print.PageFormat pageFormat = java.awt.print.PrinterJob.getPrinterJob().defaultPage();
		pageFormat.setOrientation(java.awt.print.PageFormat.PORTRAIT);
		generatePDF(mathmodel, fos, pageFormat);
	}


	public void generatePDF(MathModel mathmodel, java.io.FileOutputStream fos, java.awt.print.PageFormat pageFormat) throws Exception {

		ITextWriter pdfWriter = ITextWriter.getInstance(ITextWriter.PDF_WRITER);
		pdfWriter.writeMathModel(mathmodel, fos, pageFormat);
	}


//	public void generateReactionsImage(FileOutputStream fos,ReactionCartoonTool reactionCartoonToolIN/*tif,png,jpg,... javax.imageio.spi.ImageReaderSpi#getFormatNames*/) throws Exception {
//		BufferedImage reactionsImage = ITextWriter.generateDocReactionsImage(reactionCartoonToolIN.getModel(), ITextWriter.HIGH_RESOLUTION);
//		try {
//			ImageIO.write(reactionsImage,"jpg", fos);
//		} catch (java.io.IOException e) {
//			System.err.println("Unable to save image to file.");
//			lg.error(e);
//			throw e;
//		} finally {
//			try{fos.close();}catch(Exception e){lg.error(e);}
//		}
//	}


	public void generateStructureImage(Model model, String resolution, java.io.FileOutputStream fos) throws Exception {
		
//		java.io.ByteArrayOutputStream bos = ITextWriter.generateStructureImage(model, resolution);
//		try {
//			bos.flush();
//			bos.writeTo(fos);
//			fos.flush();
//			fos.close();
//			bos.close();
//		} catch (java.io.IOException e) {
//			System.err.println("Unable to print image to file.");
//			lg.error(e);
//			throw e;
//		}
	}


	public BioModel getBioModel(KeyValue bioModelKey) throws DataAccessException {

		XMLHolder<BioModel> bioModelXML = getBioModelXML(bioModelKey);

		BioModel bioModel = getBioModelFromDatabaseXML(bioModelXML);
		// if the positive & negative features for the membranes are not already set, set them using struct topology.
		bioModel.getModel().getElectricalTopology().populateFromStructureTopology();
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

private BioModel getBioModelFromDatabaseXML(XMLHolder<BioModel> bioModelXMLHolder) throws DataAccessException{

	try{
		BioModel bm = bioModelXMLHolder.getDocument();
		if (bm==null){
			bm = XmlHelper.XMLToBioModel(new XMLSource(bioModelXMLHolder.getXmlString()));
		}
		cacheSimulations(bm.getSimulations());
		// XmlHelper.XMLToBioModel() already calls BioModel.refreshDependencies()
		//bm.refreshDependencies(); 
		return bm;
	}catch(XmlParseException e){
		throw new DataAccessException(e.getMessage(), e);
	}
}

public synchronized BioModelInfo getBioModelInfo(KeyValue key) throws DataAccessException {
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
//	bBioModelInfosDirty = false;

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
public BioModelInfo[] getBioModelInfos() {
	ArrayList<BioModelInfo> arrayList = new ArrayList<BioModelInfo>(bioModelInfoHash.values());
	Collections.sort(arrayList,new VersionInfoComparator());
	return (BioModelInfo[])arrayList.toArray(new BioModelInfo[bioModelInfoHash.size()]);
}


private XMLHolder<BioModel> getBioModelXML(KeyValue vKey) throws DataAccessException {

	try{
		String xmlString = (String)xmlHash.get(vKey);
		if (xmlString==null){
			BigString xmlBS = sessionManager.getUserMetaDbServer().getBioModelXML(vKey);
			xmlString = (xmlBS != null?xmlBS.toString():null);
			if(xmlString != null){
				BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(xmlString));
				String newXmlString = XmlHelper.bioModelToXML(bioModel);
				xmlHash.put(vKey,newXmlString);
				return new XMLHolder<BioModel>(newXmlString,bioModel);
			}else{
				throw new RuntimeException("unexpected: UserMetaDbServer.getBioModelXML() returned null");
			}
		}else{
			return new XMLHolder<BioModel>(xmlString);
		}
	}catch (ObjectNotFoundException e){
		throw new DataAccessException("BioModel (id=" + vKey + ") does not exist. It either " +
			"has been deleted or its reference is outdated. Please use menu 'Server->Reconnect' to update document references.");
	}catch(Exception e){
		lg.error(e.getMessage(), e);
		throw FailToLoadDocumentExc.createException(e, vKey, this);
	}
}

public static class FailToLoadDocumentExc extends DataAccessException {
//	private VCellSoftwareVersion documentSoftwareVersion;
//	private VCellSoftwareVersion runningSoftwareVersion;
	public FailToLoadDocumentExc(Throwable cause,VCellSoftwareVersion runningSoftwareVersion,VCellSoftwareVersion documentSoftwareVersion){
		super(VCellErrorMessages.FAIL_LOAD_MESSAGE+"\n"+getVersionsString(runningSoftwareVersion,documentSoftwareVersion),cause);
//		this.runningSoftwareVersion = runningSoftwareVersion;
//		this.documentSoftwareVersion = documentSoftwareVersion;
	}
	public static FailToLoadDocumentExc createException(Throwable cause,KeyValue documentKey,ClientDocumentManager clientDocumentManager){
		VCellSoftwareVersion documentSoftwareVersion = null;
		try{
			VCDocumentInfo docInfo = clientDocumentManager.getBioModelInfo(documentKey);
			documentSoftwareVersion = docInfo.getSoftwareVersion();
		}catch(Exception e){
			lg.error(e.getMessage(), e);
		}
		return new FailToLoadDocumentExc(cause,VCellSoftwareVersion.fromSystemProperty(),documentSoftwareVersion);
	}
	public static String getVersionsString(VCellSoftwareVersion runningSoftwareVersion,VCellSoftwareVersion documentSoftwareVersion){
		return "(run="+(runningSoftwareVersion==null?"unknown":runningSoftwareVersion.getSoftwareVersionString())+", doc="+(documentSoftwareVersion==null?"unknown":documentSoftwareVersion.getSoftwareVersionString())+")";
	}
//	public VCellSoftwareVersion getRunningSoftwareVersion(){
//		return runningSoftwareVersion;
//	}
//	public VCellSoftwareVersion getDocumentSoftwareVersion(){
//		return documentSoftwareVersion;
//	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/8/2003 4:55:39 PM)
 */
public DBSpecies getBoundSpecies(DBFormalSpecies dbfs) throws DataAccessException {
	try {
		return sessionManager.getUserMetaDbServer().getBoundSpecies(dbfs);
	}catch (RemoteProxyException e){
		throw new DataAccessException(e.getMessage(), e);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/8/2003 4:55:39 PM)
 */
public DBFormalSpecies[] getDatabaseSpecies(String likeString, boolean isBound, FormalSpeciesType speciesType, int restrictSearch, int rowLimit, boolean bOnlyUser)	throws DataAccessException {

	try {
		return sessionManager.getUserMetaDbServer().getDatabaseSpecies(likeString,isBound,speciesType,restrictSearch,rowLimit,bOnlyUser);
	}catch (RemoteProxyException e){
		throw new DataAccessException(e.getMessage(), e);
	}
}


public ReactionDescription[] getDictionaryReactions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException {
	try {
		return sessionManager.getUserMetaDbServer().getDictionaryReactions(reactionQuerySpec);
	}catch (RemoteProxyException e){
		throw new DataAccessException(e.getMessage(), e);
	}
}


public Geometry getGeometry(KeyValue geometryKey) throws DataAccessException {

	String geometryXML = getGeometryXML(geometryKey);
	
	Geometry geometry = getGeometryFromDatabaseXML(geometryXML);
	
	return geometry;
}

public Geometry getGeometry(GeometryInfo geometryInfo) throws DataAccessException {

	Geometry geometry = null;
	try {
		XMLSource geomSource = new XMLSource(getGeometryXML(geometryInfo.getVersion().getVersionKey()));
		geometry = XmlHelper.XMLToGeometry(geomSource);
	}catch (XmlParseException e){
		throw new DataAccessException(e.getMessage(), e);
	}
	
	try {
		if (geometry.getDimension()>0 && geometry.getGeometrySurfaceDescription().getGeometricRegions()==null){
			geometry.getGeometrySurfaceDescription().updateAll();
		}
	}catch (Exception e){
		lg.error(e.getMessage(), e);
		//throw new DataAccessException("Geometric surface generation error:\n"+e.getMessage());
	}
	
	return geometry;
}

private Geometry getGeometryFromDatabaseXML(String geometryXML) throws DataAccessException{

	try{
		Geometry geometry = XmlHelper.XMLToGeometry(new XMLSource(geometryXML));
		geometry.refreshDependencies();

		try {
			if (geometry.getDimension()>0){
				geometry.getGeometrySurfaceDescription().updateAll();
			}
		}catch (Exception e){
			throw new DataAccessException("Geometric surface generation error:\n"+e.getMessage(), e);
		}

		return geometry;
	}catch(XmlParseException e){
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage(), e);
	}
}


public synchronized GeometryInfo getGeometryInfo(KeyValue key) throws DataAccessException {
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
//	bGeometryInfosDirty = false;

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
public GeometryInfo[] getGeometryInfos() {
	ArrayList<GeometryInfo> arrayList = new ArrayList<GeometryInfo>(geoInfoHash.values());
	Collections.sort(arrayList,new VersionInfoComparator());
	return (GeometryInfo[])arrayList.toArray(new GeometryInfo[geoInfoHash.size()]);
}

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
		throw new DataAccessException("Geometry (id=" + vKey + ") does not exist. It either " +
			"has been deleted or its reference is outdated. Please use menu 'Server->Reconnect' to update document references.");
	}catch(Exception e){
		lg.error(e.getMessage(), e);
		throw FailToLoadDocumentExc.createException(e, vKey, this);
	}
}

public VCImage getImage(VCImageInfo vcImageInfo) throws DataAccessException {
	
	VCImage vcImage = null;
	try {
		vcImage = XmlHelper.XMLToImage(getImageXML(vcImageInfo.getVersion().getVersionKey()));
	}catch (XmlParseException e){
		throw new DataAccessException(e.getMessage(), e);
	}
	
	return vcImage;
}

public VCImageInfo[] getImageInfos() throws DataAccessException {
	ArrayList<VCImageInfo> arrayList = new ArrayList<VCImageInfo>(imgInfoHash.values());
	Collections.sort(arrayList,new VersionInfoComparator());
	return (VCImageInfo[])arrayList.toArray(new VCImageInfo[imgInfoHash.size()]);
}

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
		lg.error(e.getMessage(), e);
		throw FailToLoadDocumentExc.createException(e, vKey, this);
	}
}

public MathModel getMathModel(KeyValue mathModelKey) throws DataAccessException {

	XMLHolder<MathModel> mathModelXML = getMathModelXML(mathModelKey);
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

public MathModel getMathModel(MathModelInfo mathModelInfo) throws DataAccessException {
	
	return getMathModel(mathModelInfo.getVersion().getVersionKey());
}


private MathModel getMathModelFromDatabaseXML(XMLHolder<MathModel> mathModelXML) throws DataAccessException{

	try{
		MathModel mm = mathModelXML.getDocument();
		if (mm==null){
			mm = XmlHelper.XMLToMathModel(new XMLSource(mathModelXML.getXmlString()));
		}
		cacheSimulations(mm.getSimulations());
		mm.refreshDependencies();

		try {
			if (mm.getMathDescription().getGeometry().getDimension()>0 && mm.getMathDescription().getGeometry().getGeometrySurfaceDescription().getGeometricRegions() == null){
				mm.getMathDescription().getGeometry().getGeometrySurfaceDescription().updateAll();
			}
		}catch (Exception e){
			throw new DataAccessException("Geometric surface generation error:\n"+e.getMessage(), e);
		}

		return mm;
	}catch(XmlParseException e){
		throw new DataAccessException(e.getClass().getName()+": "+e.getMessage(), e);
	}
}

public synchronized MathModelInfo getMathModelInfo(KeyValue key) throws DataAccessException {
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
//	bMathModelInfosDirty = false;

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

public MathModelInfo[] getMathModelInfos() {
	ArrayList<MathModelInfo> arrayList = new ArrayList<MathModelInfo>(mathModelInfoHash.values());
	Collections.sort(arrayList,new VersionInfoComparator());
	return (MathModelInfo[])arrayList.toArray(new MathModelInfo[mathModelInfoHash.size()]);
}

private XMLHolder<MathModel> getMathModelXML(KeyValue vKey) throws DataAccessException {

	try{
		String xmlString = (String)xmlHash.get(vKey);
		if (xmlString==null){
			xmlString = sessionManager.getUserMetaDbServer().getMathModelXML(vKey).toString();
			if(xmlString != null){
				MathModel mathModel = XmlHelper.XMLToMathModel(new XMLSource(xmlString));
				String newXmlString = XmlHelper.mathModelToXML(mathModel);
				xmlHash.put(vKey,newXmlString);
				return new XMLHolder<MathModel>(newXmlString,mathModel);
			}else{
				throw new RuntimeException("unexpected: UserMetaDbServer.getMathModelXML() returned null");
			}
		}else{
			return new XMLHolder<MathModel>(xmlString);
		}
	}catch (ObjectNotFoundException e){
		throw new DataAccessException("MathModel (id=" + vKey + ") does not exist. It either " +
			"has been deleted or its reference is outdated. Please use menu 'Server->Reconnect' to update document references.");
	}catch(Exception e){
		lg.error(e.getMessage(), e);
		throw FailToLoadDocumentExc.createException(e, vKey, this);
	}
}

public Preference[] getPreferences() throws DataAccessException{

	System.out.println("ClientDocumentManager.getPreferences()");
	if (preferences!=null){
		return preferences;
	}else{
		try{
			preferences = sessionManager.getUserMetaDbServer().getPreferences();
			return preferences;
		}catch (RemoteProxyException e){
			handleRemoteProxyException(e);
			throw new DataAccessException(e.getMessage());
		}
	}
}

public Model getReactionStepAsModel(KeyValue reactionStepKey) throws DataAccessException {
	try {
		String str = sessionManager.getUserMetaDbServer().getReactionStepAsModel(reactionStepKey);
		Element element = XmlUtil.stringToXML(str, null).getRootElement();
		Model reactionModel = new XmlReader(true).getModel(element);
		if(reactionModel != null){
			try{
				reactionModel = (Model)BeanUtils.cloneSerializable(reactionModel);
				reactionModel.refreshDependencies();
			}catch(Exception e){
				throw new DataAccessException(e.getMessage(), e);
			}
		}
		return reactionModel;
		
	}catch (Exception e){
		throw new DataAccessException(e.getMessage(), e);
	}
}


public SimulationStatus getServerSimulationStatus(VCSimulationIdentifier vcSimulationIdentifier) throws DataAccessException {
	if (simulationStatusHash.containsKey(vcSimulationIdentifier.getSimulationKey())){
		return (SimulationStatus)simulationStatusHash.get(vcSimulationIdentifier.getSimulationKey());
	}else{
		SimulationStatus simulationStatus = null;
		try {
			simulationStatus = sessionManager.getSimulationController().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
		}catch (RemoteProxyException e){
			handleRemoteProxyException(e);
			try {
				simulationStatus = sessionManager.getSimulationController().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
			}catch (RemoteProxyException e2){
				handleRemoteProxyException(e2);
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

public Simulation getSimulation(SimulationInfo simulationInfo) throws DataAccessException {
	
	Simulation simulation = null;
	try {
		simulation = XmlHelper.XMLToSim(getSimulationXML(simulationInfo.getVersion().getVersionKey()));
	}catch (XmlParseException e){
		throw new DataAccessException(e.getMessage(), e);
	}
	
	return simulation;
}

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
		throw new DataAccessException("Error getting XML document from server: "+e.getMessage(), e);
		//return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2004 11:27:37 AM)
 */
public TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws DataAccessException {
	if(!getUser().isTestAccount()){
		throw new PermissionException("User="+getUser().getName()+" not allowed TestSuiteInfo");
	}
	try {
		return getSessionManager().getUserMetaDbServer().getTestSuite(getThisTS);
	}catch (RemoteProxyException e){
		throw new DataAccessException(e.getMessage(), e);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2004 11:27:37 AM)
 */
public TestSuiteInfoNew[] getTestSuiteInfos() throws DataAccessException {
	
	if(!getUser().isTestAccount()){
		throw new PermissionException("User="+getUser().getName()+" not allowed TestSuiteInfo");
	}
	try {
		return getSessionManager().getUserMetaDbServer().getTestSuiteInfos();
	}catch (RemoteProxyException e){
		throw new DataAccessException(e.getMessage(), e);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/01 10:54:29 AM)
 */
public User getUser() {
	return sessionManager.getUser();
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 3:33:10 PM)
 */
public ReactionDescription[] getUserReactionDescriptions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException {
	try {
		return sessionManager.getUserMetaDbServer().getUserReactionDescriptions(reactionQuerySpec);
	}catch (RemoteProxyException e){
		throw new DataAccessException(e.getMessage(), e);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 3:33:10 PM)
 */
public ReactionStepInfo[] getUserReactionStepInfos(KeyValue[] reactionStepKeys) throws DataAccessException {
	try {
		return sessionManager.getUserMetaDbServer().getReactionStepInfos(reactionStepKeys);
	}catch (RemoteProxyException e){
		throw new DataAccessException(e.getMessage(), e);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (11/14/00 5:24:29 PM)
 * @param e RemoteProxyException
 */
private void handleRemoteProxyException(cbit.vcell.message.server.bootstrap.client.RemoteProxyException e) {
	lg.error("Handling RemoteProxyException", e);
}


private void clear() {
	xmlHash.clear();
	geoInfoHash.clear();
	imgInfoHash.clear();
	mathModelInfoHash.clear();
	bioModelInfoHash.clear();
	simulationStatusHash.clear();	
	preferences = null;

}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2003 7:45:19 AM)
 * @return cbit.vcell.modeldb.VCInfoContainer
 */
public synchronized void initAllDatabaseInfos() throws DataAccessException {
	clear();
	
	VCInfoContainer vcInfoContainer = null;
	try{
		System.out.println("ClientDocumentManager.initAllDatabaseInfos()");
		long time1 = System.currentTimeMillis();
		//
		// gets BioModelMetaDatas, MathModelMetaDatas, all VersionInfos, and ResultSetInfos
		//
		vcInfoContainer = sessionManager.getUserMetaDbServer().getVCInfoContainer();

		PerformanceMonitorEvent pme = new PerformanceMonitorEvent(this,getUser(),
			new PerformanceData("ClientDocumentManager.initAllDatabaseInfos()",
			    MessageEvent.LOGON_STAT,
			    new PerformanceDataEntry[] {
				    new PerformanceDataEntry("remote call duration", Double.toString(((double)System.currentTimeMillis()-time1)/1000.0))
				    }
		    )
    	);

	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException("RemoteProxyException: "+e.getMessage());
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
public boolean isChanged(VCImage vcImage, String vcImageXML) throws DataAccessException {
	//
	// get versionable from database or from cache (should be in cache)
	//
	if(isChangedVersion(vcImage.getVersion())){
		return true;
	}
	
	String savedImageXML = null;

	try {
		savedImageXML = getImageXML(vcImage.getVersion().getVersionKey());
	}catch (Exception e){
		lg.error(e.getMessage(), e);
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
		if (!VCMLComparator.compareEquals(savedImageXML,vcImageXML, false)){
			return true;
		}
	}catch (XmlParseException e){
		throw new DataAccessException(e.getMessage(), e);
	}


	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
public boolean isChanged(BioModel bioModel, String bioModelXML) throws DataAccessException {

	if(bioModel.getVersion() == null){ 
		// if owner is not the user, if no changes were made, isChanged=false
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
		// compare saved and this bioModel
		//
		XMLHolder<BioModel> savedBioModelXML = getBioModelXML(bioModel.getVersion().getVersionKey());
		if (savedBioModelXML == null){
			// must have been deleted
			return true;
		}
		try {
			if (bioModelXML==null){
				bioModelXML = XmlHelper.bioModelToXML(bioModel);
			}
			// compare everything except vcmetadata
			if (!VCMLComparator.compareEquals(savedBioModelXML.getXmlString(),bioModelXML, true)){
				return true;
			}
			//
			// compare vcmetadata
			//
			BioModel savedBioModel = savedBioModelXML.getDocument();
			if (savedBioModel==null){
				savedBioModel = XmlHelper.XMLToBioModel(new XMLSource(savedBioModelXML.getXmlString()));
			}
			if (!savedBioModel.getVCMetaData().compareEquals(bioModel.getVCMetaData())) {
				return true;
			}
			return false;			
		}catch (XmlParseException e){
			throw new DataAccessException(e.getMessage(), e);
		}
	}

}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 6:56:58 AM)
 * @return boolean
 * @param vcDocument cbit.vcell.document.VCDocument
 */
public boolean isChanged(VCDocument vcDocument) throws DataAccessException {
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
	}catch (Exception e){
		lg.error(e.getMessage(), e);
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
		if (!VCMLComparator.compareEquals(savedGeometryXML,geometryXML, false)){
			return true;
		}
	}catch (XmlParseException e){
		throw new DataAccessException(e.getMessage(), e);
	}

	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
public boolean isChanged(MathModel mathModel, String mathModelXML) throws DataAccessException {

	if(mathModel.getVersion() == null){
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
		XMLHolder<MathModel> savedMathModelXML = getMathModelXML(mathModel.getVersion().getVersionKey());
		if (savedMathModelXML == null){
			// must have been deleted
			return true;
		}
		try {
			if (mathModelXML==null){
				mathModelXML = XmlHelper.mathModelToXML(mathModel);
			}
			return !VCMLComparator.compareEquals(savedMathModelXML.getXmlString(),mathModelXML, true);
		}catch (XmlParseException e){
			throw new DataAccessException(e.getMessage(), e);
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
		return !VCMLComparator.compareEquals(XmlHelper.simToXML(sim),loadedSimXML, false);
	}catch (XmlParseException e){
		throw new DataAccessException(e.getMessage(), e);
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


public void preloadSimulationStatus(VCSimulationIdentifier[] simIDs) {
	KeyValue simKeys[] = new KeyValue[simIDs.length];
	for (int i = 0; i < simIDs.length; i++){
		simKeys[i] = simIDs[i].getSimulationKey();
	}
	preloadSimulationStatus(simKeys);
}
/**
 * Insert the method's description here.
 * Creation date: (9/1/2004 12:33:16 PM)
 * @param simKeys cbit.sql.KeyValue[]
 */
private void preloadSimulationStatus(KeyValue[] simKeys) {
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
				simulationStatusArray = sessionManager.getSimulationController().getSimulationStatus(simKeys);
			}catch (RemoteProxyException e){
				handleRemoteProxyException(e);
				try {
					simulationStatusArray = sessionManager.getSimulationController().getSimulationStatus(simKeys);
				}catch (RemoteProxyException e2){
					handleRemoteProxyException(e2);
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
			lg.error(e);
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
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException("RemoteProxyException: "+e.getMessage());
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
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException("RemoteProxyException: "+e.getMessage());
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
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException("RemoteProxyException: "+e.getMessage());
	}
}

/**
 * 
 * @param newListener cbit.vcell.clientdb.DatabaseListener
 */
public void removeDatabaseListener(DatabaseListener newListener) {
	aDatabaseListener = DatabaseEventMulticaster.remove(aDatabaseListener, newListener);
	return;
}

public void removeFieldDataDBListener(EventListener oldFieldDataDBListener) {
	fieldDataDBEventListenerH.remove(oldFieldDataDBListener);
}

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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}	
}

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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}	
}

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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}	
}

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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}	
}

private <T extends VersionInfo> T removeUserFromGroup0(VersionInfo versionInfo, VersionableType vType, Hashtable<KeyValue,T> vInfoHash, String userToAdd) throws RemoteProxyException, DataAccessException {

	//
	// unpublish from database
	//
	UserMetaDbServer dbServer = sessionManager.getUserMetaDbServer();
	@SuppressWarnings("unchecked")
	T newVersionInfo = (T) dbServer.groupRemoveUser(vType,versionInfo.getVersion().getVersionKey(),userToAdd,false);
	
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

public void replacePreferences(Preference[] argPreferences) throws DataAccessException{

	if (argPreferences==null){
		throw new IllegalArgumentException("preferences were null");
	}
	System.out.println("ClientDocumentManager.replacePreferences()");
	if (!Compare.isEqual(argPreferences,getPreferences())){		
		try{
			sessionManager.getUserMetaDbServer().replacePreferences(argPreferences);
			preferences = argPreferences;
		}catch (RemoteProxyException e){
			handleRemoteProxyException(e);
			throw new DataAccessException(e.getMessage());
		}
	}
}

public VCImage save(VCImage vcImage) throws DataAccessException {
	try {
		String vcImageXML = null;
		try {
			vcImageXML = XmlHelper.imageToXML(vcImage);
		}catch (XmlParseException e){
			throw new DataAccessException(e.getMessage(), e);
		}

		String savedVCImageXML = sessionManager.getUserMetaDbServer().saveVCImage(new BigString(vcImageXML)).toString();

		VCImage savedVCImage = null;
		try {
			savedVCImage = XmlHelper.XMLToImage(savedVCImageXML);
		}catch (XmlParseException e){
			throw new DataAccessException(e.getMessage(), e);
		}
		savedVCImage.refreshDependencies();
		
		KeyValue savedKey = savedVCImage.getVersion().getVersionKey();

		if (xmlHash.get(savedKey)==null){
			xmlHash.put(savedKey, savedVCImageXML);
		}

		try {
			ISize size = new ISize(savedVCImage.getNumX(),savedVCImage.getNumY(),savedVCImage.getNumZ());
			GIFImage browseData = BrowseImage.makeBrowseGIFImage(savedVCImage);
			VCImageInfo savedVCImageInfo = new VCImageInfo(savedVCImage.getVersion(),size,savedVCImage.getExtent(),browseData,VCellSoftwareVersion.fromSystemProperty());
			imgInfoHash.put(savedKey,savedVCImageInfo);
			
			fireDatabaseInsert(new DatabaseEvent(this, DatabaseEvent.INSERT, null, savedVCImageInfo));
		}catch (Exception e){
			lg.error(e.getMessage(), e);
		}
		
		return savedVCImage;
	}catch (RemoteProxyException e){
		throw new DataAccessException(VCellErrorMessages.FAIL_SAVE_MESSAGE + "\n\n" + e.getMessage(), e);
	}	
}

// the version time is assumed to be EDT (or whatever locale the user is at) but it really is UTC
// hence, the apparent time we display in the biomodel nodes is 4 hours in advance
// we use the converter below to get the current date; used only for display purposes (in the BioModelInfo)
private static Version convertVersionToLocalTime(Version utcVersion) {
	Date date = null;
	if (utcVersion.getDate() != null) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");  
		String strDate = dateFormat.format(utcVersion.getDate());
		strDate = strDate + " UTC";
		dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss zzz");
		try {
			date = dateFormat.parse(strDate);
		} catch (ParseException e) {
			date = null;
		}
	} else {
		date = null;
	}
	Version localVersion = new Version(	utcVersion.getVersionKey(), utcVersion.getName(), utcVersion.getOwner(),
			utcVersion.getGroupAccess(), utcVersion.getBranchPointRefKey(),
			utcVersion.getBranchID(), date, utcVersion.getFlag(), utcVersion.getAnnot() );
	return localVersion;
}
/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
public BioModel save(BioModel bioModel, String independentSims[]) throws DataAccessException {
	
	try {
		String bioModelXML = null;
		try {
			bioModel.getVCMetaData().cleanupMetadata();
			bioModelXML = XmlHelper.bioModelToXML(bioModel);
		}catch (XmlParseException e){
			throw new DataAccessException(e.getMessage(), e);
		}
		
		String savedBioModelXML = sessionManager.getUserMetaDbServer().saveBioModel(new BigString(bioModelXML),independentSims).toString();

		BioModel savedBioModel = getBioModelFromDatabaseXML(new XMLHolder<BioModel>(savedBioModelXML));
		
		KeyValue savedKey = savedBioModel.getVersion().getVersionKey();
		if (xmlHash.get(savedKey)==null){
			xmlHash.put(savedKey, savedBioModelXML);
		}

		Version localTimeVersion = convertVersionToLocalTime(savedBioModel.getVersion());
		BioModelInfo savedBioModelInfo = new BioModelInfo(localTimeVersion,savedBioModel.getModel().getKey(),savedBioModel.createBioModelChildSummary(),VCellSoftwareVersion.fromSystemProperty());
		bioModelInfoHash.put(savedKey,savedBioModelInfo);
		
		SimulationContext[] scArr = savedBioModel.getSimulationContexts();
		for (int i = 0; i < scArr.length; i++) {
			updateGeometryRelatedHashes(scArr[i].getGeometry());
		}
		
		// copy some transient info from the old model to the new one
		for (SimulationContext newsc : scArr) {
			SimulationContext oldsc = bioModel.getSimulationContext(newsc.getName());
			newsc.getTaskCallbackProcessor().initialize((oldsc.getTaskCallbackProcessor()));
			newsc.setMostRecentlyCreatedOutputSpec(oldsc.getMostRecentlyCreatedOutputSpec());
			newsc.setMd5hash(oldsc.getMd5hash());
		}

		fireDatabaseInsert(new DatabaseEvent(this, DatabaseEvent.INSERT, null, savedBioModelInfo));

		return savedBioModel;
	}catch (RemoteProxyException e){
		throw new DataAccessException(VCellErrorMessages.FAIL_SAVE_MESSAGE + "\n\n" + e.getMessage(), e);
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
			geometryXML = XmlHelper.geometryToXML(geometry);
		}catch (XmlParseException e){
			throw new DataAccessException(e.getMessage(), e);
		}

		String savedGeometryXML = sessionManager.getUserMetaDbServer().saveGeometry(new BigString(geometryXML)).toString();

		Geometry savedGeometry = getGeometryFromDatabaseXML(savedGeometryXML);
		
		KeyValue savedKey = savedGeometry.getVersion().getVersionKey();

		if (xmlHash.get(savedKey)==null){
			xmlHash.put(savedKey, savedGeometryXML);
		}

		updateGeometryRelatedHashes(savedGeometry);
//		KeyValue imageRef = (savedGeometry.getGeometrySpec().getImage()!=null)?(savedGeometry.getGeometrySpec().getImage().getKey()):(null);
//		GeometryInfo savedGeometryInfo = new GeometryInfo(savedGeometry.getVersion(),savedGeometry.getDimension(),savedGeometry.getExtent(),savedGeometry.getOrigin(),imageRef);
//		geoInfoHash.put(savedKey,savedGeometryInfo);
		
//		fireDatabaseInsert(new DatabaseEvent(this, DatabaseEvent.INSERT, null, geoInfoHash.get(savedKey)/*savedGeometryInfo*/));

		return savedGeometry;
	}catch (RemoteProxyException e){
		throw new DataAccessException(VCellErrorMessages.FAIL_SAVE_MESSAGE + "\n\n" + e.getMessage(), e);
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
			mathModelXML = XmlHelper.mathModelToXML(mathModel);
			if (lg.isInfoEnabled()) {
				lg.info(XmlUtil.beautify(mathModelXML));
			}
		}catch (XmlParseException e){
			throw new DataAccessException(e.getMessage(), e);
		}
			
		String savedMathModelXML = sessionManager.getUserMetaDbServer().saveMathModel(new BigString(mathModelXML),independentSims).toString();

		MathModel savedMathModel = getMathModelFromDatabaseXML(new XMLHolder<MathModel>(savedMathModelXML));
		if (lg.isInfoEnabled()) {
			lg.info(XmlUtil.beautify(savedMathModelXML));
		}
		
		KeyValue savedKey = savedMathModel.getVersion().getVersionKey();

		if (xmlHash.get(savedKey)==null){
			xmlHash.put(savedKey, savedMathModelXML);
		}

		Version localTimeVersion = convertVersionToLocalTime(savedMathModel.getVersion());
		MathModelInfo savedMathModelInfo = new MathModelInfo(localTimeVersion,savedMathModel.getMathDescription().getKey(),savedMathModel.createMathModelChildSummary(),VCellSoftwareVersion.fromSystemProperty());
		mathModelInfoHash.put(savedKey,savedMathModelInfo);
		updateGeometryRelatedHashes(savedMathModel.getMathDescription().getGeometry());
		
		fireDatabaseInsert(new DatabaseEvent(this, DatabaseEvent.INSERT, null, savedMathModelInfo));

		return savedMathModel;
	}catch (RemoteProxyException e){
		throw new DataAccessException(VCellErrorMessages.FAIL_SAVE_MESSAGE + "\n\n" + e.getMessage(), e);
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/01 4:58:40 PM)
 */
public VCImage saveAsNew(VCImage vcImage, java.lang.String newName) throws DataAccessException {
	try {		
		String vcImageXML = null;
		try {
			vcImageXML = XmlHelper.imageToXML(vcImage);
		}catch (XmlParseException e){
			throw new DataAccessException(e.getMessage(), e);
		}

		String savedVCImageXML = sessionManager.getUserMetaDbServer().saveVCImageAs(new BigString(vcImageXML), newName).toString();

		VCImage savedVCImage = null;
		try {
			savedVCImage = XmlHelper.XMLToImage(savedVCImageXML);
		}catch (XmlParseException e){
			throw new DataAccessException(e.getMessage(), e);
		}
		savedVCImage.refreshDependencies();
		
		KeyValue savedKey = savedVCImage.getVersion().getVersionKey();

		if (xmlHash.get(savedKey)==null){
			xmlHash.put(savedKey, savedVCImageXML);
		}

		try {
			ISize size = new ISize(savedVCImage.getNumX(),savedVCImage.getNumY(),savedVCImage.getNumZ());
			GIFImage browseData = BrowseImage.makeBrowseGIFImage(savedVCImage);
			VCImageInfo savedVCImageInfo = new VCImageInfo(savedVCImage.getVersion(),size,savedVCImage.getExtent(),browseData,VCellSoftwareVersion.fromSystemProperty());
			imgInfoHash.put(savedKey,savedVCImageInfo);
			
			fireDatabaseInsert(new DatabaseEvent(this, DatabaseEvent.INSERT, null, savedVCImageInfo));
		}catch (Exception e){
			lg.error(e.getMessage(), e);
		}
		
		return savedVCImage;
	}catch (RemoteProxyException e){
		throw new DataAccessException(VCellErrorMessages.FAIL_SAVE_MESSAGE + "\n\n" + e.getMessage(), e);
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/01 11:27:52 AM)
 */
public BioModel saveAsNew(BioModel bioModel, java.lang.String newName, String independentSims[]) throws DataAccessException {
	
	try {

		String bioModelXML = null;
		try {
			bioModel.getVCMetaData().cleanupMetadata();
			bioModelXML = XmlHelper.bioModelToXML(bioModel);
		}catch (XmlParseException e){
			throw new DataAccessException(e.getMessage(), e);
		}

		String savedBioModelXML = sessionManager.getUserMetaDbServer().saveBioModelAs(new BigString(bioModelXML), newName, independentSims).toString();

		BioModel savedBioModel = getBioModelFromDatabaseXML(new XMLHolder<BioModel>(savedBioModelXML));		
		
		KeyValue savedKey = savedBioModel.getVersion().getVersionKey();

		if (xmlHash.get(savedKey)==null){
			xmlHash.put(savedKey, savedBioModelXML);
		}

		Version localTimeVersion = convertVersionToLocalTime(savedBioModel.getVersion());
		BioModelInfo savedBioModelInfo = new BioModelInfo(localTimeVersion,savedBioModel.getModel().getKey(),savedBioModel.createBioModelChildSummary(),VCellSoftwareVersion.fromSystemProperty());
		bioModelInfoHash.put(savedKey,savedBioModelInfo);

		SimulationContext[] scArr = savedBioModel.getSimulationContexts();
		for (int i = 0; i < scArr.length; i++) {
			updateGeometryRelatedHashes(scArr[i].getGeometry());
		}

		fireDatabaseInsert(new DatabaseEvent(this, DatabaseEvent.INSERT, null, savedBioModelInfo));

		return savedBioModel;
	}catch (RemoteProxyException e){
		throw new DataAccessException(VCellErrorMessages.FAIL_SAVE_MESSAGE + "\n\n" + e.getMessage(), e);
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/01 11:27:52 AM)
 */
public Geometry saveAsNew(Geometry geometry, java.lang.String newName) throws DataAccessException {
	try {
		String geometryXML = null;
		try {
			geometryXML = XmlHelper.geometryToXML(geometry);
		}catch (XmlParseException e){
			throw new DataAccessException(e.getMessage(), e);
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

		updateGeometryRelatedHashes(savedGeometry);
//		KeyValue imageRef = (savedGeometry.getGeometrySpec().getImage()!=null)?(savedGeometry.getGeometrySpec().getImage().getKey()):(null);
//		GeometryInfo savedGeometryInfo = new GeometryInfo(savedGeometry.getVersion(),savedGeometry.getDimension(),savedGeometry.getExtent(),savedGeometry.getOrigin(),imageRef);
//		geoInfoHash.put(savedKey,savedGeometryInfo);
		
//		fireDatabaseInsert(new DatabaseEvent(this, DatabaseEvent.INSERT, null, geoInfoHash.get(savedKey)/*savedGeometryInfo*/));

		return savedGeometry;
	}catch (RemoteProxyException e){
		throw new DataAccessException(VCellErrorMessages.FAIL_SAVE_MESSAGE + "\n\n" + e.getMessage(), e);
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/01 11:27:52 AM)
 */
public MathModel saveAsNew(MathModel mathModel, java.lang.String newName, String independentSims[]) throws DataAccessException {
	try {
		
		String mathModelXML = null;
		try {
			mathModelXML = XmlHelper.mathModelToXML(mathModel);
		}catch (XmlParseException e){
			throw new DataAccessException(e.getMessage(), e);
		}
//		System.out.println(mathModelXML);
		String savedMathModelXML = sessionManager.getUserMetaDbServer().saveMathModelAs(new BigString(mathModelXML), newName, independentSims).toString();

		MathModel savedMathModel = getMathModelFromDatabaseXML(new XMLHolder<MathModel>(savedMathModelXML));
		
		KeyValue savedKey = savedMathModel.getVersion().getVersionKey();

		if (xmlHash.get(savedKey)==null){
			xmlHash.put(savedKey, savedMathModelXML);
		}

		Version localTimeVersion = convertVersionToLocalTime(savedMathModel.getVersion());
		MathModelInfo savedMathModelInfo = new MathModelInfo(localTimeVersion,savedMathModel.getMathDescription().getKey(),savedMathModel.createMathModelChildSummary(),VCellSoftwareVersion.fromSystemProperty());
		mathModelInfoHash.put(savedKey,savedMathModelInfo);
		
		updateGeometryRelatedHashes(savedMathModel.getMathDescription().getGeometry());
		
		fireDatabaseInsert(new DatabaseEvent(this, DatabaseEvent.INSERT, null, savedMathModelInfo));

		return savedMathModel;
	}catch (RemoteProxyException e){
		throw new DataAccessException(VCellErrorMessages.FAIL_SAVE_MESSAGE + "\n\n" + e.getMessage(), e);
	}	
}

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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}	
}

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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}	
}

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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}	
}

private <T extends VersionInfo> T setGroupPrivate0(VersionInfo versionInfo, VersionableType vType, Hashtable<KeyValue,T> vInfoHash) throws RemoteProxyException, DataAccessException {

	//
	// unpublish from database
	//
	UserMetaDbServer dbServer = sessionManager.getUserMetaDbServer();
	@SuppressWarnings("unchecked")
	T newVersionInfo = (T) dbServer.groupSetPrivate(vType,versionInfo.getVersion().getVersionKey());
	
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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}	
}

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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}	
}

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
		
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		throw new DataAccessException(e.getMessage());
	}	
}

private <T extends VersionInfo> T setGroupPublic0(VersionInfo versionInfo, VersionableType vType, Hashtable<KeyValue,T> vInfoHash) throws RemoteProxyException, DataAccessException {

	//
	// publish from database
	//
	UserMetaDbServer dbServer = sessionManager.getUserMetaDbServer();
	@SuppressWarnings("unchecked")
	T newVersionInfo = (T) dbServer.groupSetPublic(vType,versionInfo.getVersion().getVersionKey());
	
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


public void substituteFieldFuncNames(VCDocument vcDocument,VersionableTypeVersion originalOwner)
		throws DataAccessException,MathException,ExpressionException{

	Vector<ExternalDataIdentifier> errorCleanupExtDataIDV = new Vector<ExternalDataIdentifier>();
	try{
		if(originalOwner == null || originalOwner.getVersion().getOwner().compareEqual(getUser())){
			//Substitution for FieldFunc not needed for new doc or if we own doc
			return;
		}
		//Get Objects from Document that might need to have FieldFuncs replaced
		Vector<Object> fieldFunctionContainer_mathDesc_or_simContextV = new Vector<Object>();
		if(vcDocument instanceof MathModel){
			fieldFunctionContainer_mathDesc_or_simContextV.add(((MathModel)vcDocument).getMathDescription());
		}else if(vcDocument instanceof BioModel){
			SimulationContext[] simContextArr = ((BioModel)vcDocument).getSimulationContexts();
			for(int i=0;i<simContextArr.length;i+= 1){
				fieldFunctionContainer_mathDesc_or_simContextV.add(simContextArr[i]);
			}
		}
		//Get original Field names
		Vector<String> origFieldFuncNamesV = new Vector<String>();
		for(int i=0;i<fieldFunctionContainer_mathDesc_or_simContextV.size();i+= 1){
			Object fieldFunctionContainer = fieldFunctionContainer_mathDesc_or_simContextV.elementAt(i);
			FieldFunctionArguments[] fieldFuncArgsArr = null;
			if (fieldFunctionContainer instanceof MathDescription){
				fieldFuncArgsArr = FieldUtilities.getFieldFunctionArguments((MathDescription)fieldFunctionContainer);
			}else if (fieldFunctionContainer instanceof SimulationContext){
				fieldFuncArgsArr = ((SimulationContext)fieldFunctionContainer).getFieldFunctionArguments();
			}
			for(int j=0;j<fieldFuncArgsArr.length;j+= 1){
				if(!origFieldFuncNamesV.contains(fieldFuncArgsArr[j].getFieldName())){
					origFieldFuncNamesV.add(fieldFuncArgsArr[j].getFieldName());
				}
			}
		}
		if(origFieldFuncNamesV.size() == 0){//No FieldFunctions to substitute
			return;
		}

		VersionableType modelType = vcDocument instanceof MathModel ? VersionableType.MathModelMetaData : VersionableType.BioModelMetaData;
		Hashtable<String, ExternalDataIdentifier> oldNameNewID = getSessionManager().getUserMetaDbServer().copyModelsFieldData(
				vcDocument.getVersion().getVersionKey().toString(), modelType);


		//Finally substitute new Field names
		for(int i=0;i<fieldFunctionContainer_mathDesc_or_simContextV.size();i+= 1){
			Object fieldFunctionContainer = fieldFunctionContainer_mathDesc_or_simContextV.elementAt(i);
			if (fieldFunctionContainer instanceof MathDescription){
				MathDescription mathDesc = (MathDescription)fieldFunctionContainer;
				FieldUtilities.substituteFieldFuncNames(mathDesc, oldNameNewID);
			}else if (fieldFunctionContainer instanceof SimulationContext){
				SimulationContext simContext = (SimulationContext)fieldFunctionContainer;
				simContext.substituteFieldFuncNames(oldNameNewID);
			}
		}
		fireFieldDataDB(new FieldDataDBEvent(this));
	}catch(Exception e){
		lg.error(e.getMessage(), e);
		throw new RuntimeException("Error copying Field Data \n"+e.getMessage());
	}
}

/**
 * Insert the method's description here.
 * Creation date: (10/14/2005 1:40:39 AM)
 * @param jobEvent cbit.rmi.event.SimulationJobStatusEvent
 * @exception org.vcell.util.DataAccessException The exception description.
 */
public void updateServerSimulationStatusFromJobEvent(SimulationJobStatusEvent jobEvent) throws DataAccessException {
	simulationStatusHash.put(jobEvent.getVCSimulationIdentifier().getSimulationKey(), SimulationStatus.updateFromJobEvent(getServerSimulationStatus(jobEvent.getVCSimulationIdentifier()), jobEvent));
}
private void updateGeometryRelatedHashes(Geometry savedGeometry){
	if(savedGeometry == null || (savedGeometry.getVersion() != null && geoInfoHash.get(savedGeometry.getVersion().getVersionKey()) != null)){
		return;
	}
	KeyValue imageRef = (savedGeometry.getGeometrySpec().getImage()!=null)?(savedGeometry.getGeometrySpec().getImage().getKey()):(null);
	if(imageRef != null && imgInfoHash.get(imageRef) == null){
		VCImage savedVCImage = savedGeometry.getGeometrySpec().getImage();
		ISize size = new ISize(savedVCImage.getNumX(),savedVCImage.getNumY(),savedVCImage.getNumZ());
		VCImageInfo savedVCImageInfo = new VCImageInfo(savedVCImage.getVersion(), size, savedVCImage.getExtent(), null,VCellSoftwareVersion.fromSystemProperty());
		imgInfoHash.put(savedVCImage.getVersion().getVersionKey(),savedVCImageInfo);
	}
	Version localTimeVersion = convertVersionToLocalTime(savedGeometry.getVersion());
	GeometryInfo savedGeometryInfo = new GeometryInfo(localTimeVersion,savedGeometry.getDimension(),savedGeometry.getExtent(),savedGeometry.getOrigin(),imageRef,VCellSoftwareVersion.fromSystemProperty());
	geoInfoHash.put(savedGeometry.getVersion().getVersionKey(),savedGeometryInfo);
	
	fireDatabaseInsert(new DatabaseEvent(this, DatabaseEvent.INSERT, null, geoInfoHash.get(savedGeometry.getVersion().getVersionKey())));
}

}

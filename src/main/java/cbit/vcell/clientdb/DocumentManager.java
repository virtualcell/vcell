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
import org.vcell.util.DataAccessException;
import org.vcell.util.DependencyException;
import org.vcell.util.Preference;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.CurateSpec;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.ReferenceQueryResult;
import org.vcell.util.document.ReferenceQuerySpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VersionableTypeVersion;

import cbit.image.VCImage;
import cbit.image.VCImageInfo;
import cbit.rmi.event.SimulationJobStatusEvent;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.task.CommonTask;
import cbit.vcell.desktop.controls.SessionManager;
import cbit.vcell.dictionary.db.ReactionDescription;
import cbit.vcell.field.db.FieldDataDBOperationResults;
import cbit.vcell.field.db.FieldDataDBOperationSpec;
import cbit.vcell.field.gui.FieldDataDBEventListener;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.math.MathException;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.DBFormalSpecies;
import cbit.vcell.model.DBSpecies;
import cbit.vcell.model.FormalSpeciesType;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionStepInfo;
import cbit.vcell.modeldb.ReactionQuerySpec;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestSuiteNew;
import cbit.vcell.numericstest.TestSuiteOP;
import cbit.vcell.numericstest.TestSuiteOPResults;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.solver.VCSimulationIdentifier;
/**
 * Insert the type's description here.
 * Creation date: (10/28/00 12:05:57 AM)
 * @author: 
 */
public interface DocumentManager {
	
	/**
	 * identifying string, for hashtable passing, et. al
	 */
	public static final String IDENT = CommonTask.DOCUMENT_MANAGER.name;

/**
 * 
 * @param newListener cbit.vcell.clientdb.DatabaseListener
 */
public void addDatabaseListener(DatabaseListener newListener);

public void addFieldDataDBListener(FieldDataDBEventListener newFieldDataDBEventListener);


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
VCImageInfo addUserToGroup(VCImageInfo imageInfo,String user) throws DataAccessException, DependencyException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
BioModelInfo addUserToGroup(BioModelInfo bioModelInfo,String user) throws DataAccessException, DependencyException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
GeometryInfo addUserToGroup(GeometryInfo geometryInfo,String user) throws DataAccessException, DependencyException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
MathModelInfo addUserToGroup(MathModelInfo mathModelInfo,String user) throws DataAccessException, DependencyException;


/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 9:28:15 AM)
 * @param vcDocumentInfo cbit.vcell.document.VCDocumentInfo
 * @param curateFunction int
 */
void curate(CurateSpec curateSpec) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
void delete(VCImageInfo vcImageInfo) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
void delete(BioModelInfo bioModelInfo) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
void delete(GeometryInfo geometryInfo) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 11:20:28 PM)
 * @param mathModelInfo cbit.vcell.mathmodel.MathModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
void delete(MathModelInfo mathModelInfo) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
FieldDataDBOperationResults fieldDataDBOperation(FieldDataDBOperationSpec fieldDataDBOperationSpec) throws DataAccessException;

ExternalDataIdentifier saveFieldData(FieldDataFileOperationSpec fdos, String fieldDataBaseName) throws DataAccessException;


FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFileOperationSpec) throws DataAccessException;

/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public TestSuiteOPResults doTestSuiteOP(TestSuiteOP tsop) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 2:50:07 PM)
 * @return cbit.sql.Versionable
 * @param vType cbit.sql.VersionableType
 * @param key cbit.sql.KeyValue
 */
ReferenceQueryResult findReferences(ReferenceQuerySpec sqs) throws DataAccessException;

	public void generatePDF(BioModel biomodel, java.io.FileOutputStream fos) throws Exception;


	public void generatePDF(BioModel biomodel, java.io.FileOutputStream fos, java.awt.print.PageFormat pageFormat) throws Exception;


	public void generatePDF(Geometry geom, java.io.FileOutputStream fos) throws Exception;


	public void generatePDF(Geometry geom, java.io.FileOutputStream fos, java.awt.print.PageFormat pageFormat) throws Exception;


	public void generatePDF(MathModel mathmodel, java.io.FileOutputStream fos) throws Exception;


	public void generatePDF(MathModel mathmodel, java.io.FileOutputStream fos, java.awt.print.PageFormat pageFormat) throws Exception;


//	public void generateReactionsImage(java.io.FileOutputStream fos,ReactionCartoonTool reactionCartoonToolIN) throws Exception;


	public void generateStructureImage(Model model, String resolution, java.io.FileOutputStream fos) throws Exception;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public BioModel getBioModel(KeyValue bioModelKey) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public BioModel getBioModel(BioModelInfo bioModelInfo) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public BioModelInfo getBioModelInfo(KeyValue bioModelKey) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public BioModelInfo[] getBioModelInfos();


/**
 * Insert the method's description here.
 * Creation date: (1/29/01 10:10:54 AM)
 */
public DBSpecies getBoundSpecies(DBFormalSpecies dbfs) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (1/29/01 10:10:54 AM)
 */
public DBFormalSpecies[] getDatabaseSpecies(String likeString, boolean isBound, FormalSpeciesType speciesType, int restrictSearch, int rowLimit, boolean bOnlyUser) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (4/30/2003 10:25:07 PM)
 */
public ReactionDescription[] getDictionaryReactions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException;

/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public Geometry getGeometry(KeyValue geometryKey) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public Geometry getGeometry(GeometryInfo geometryInfo) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 2:50:07 PM)
 * @return cbit.sql.Versionable
 * @param vType cbit.sql.VersionableType
 * @param key cbit.sql.KeyValue
 */
GeometryInfo getGeometryInfo(KeyValue key) throws DataAccessException;

/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public GeometryInfo[] getGeometryInfos();


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public VCImage getImage(VCImageInfo vcImageInfo) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public VCImageInfo[] getImageInfos() throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public MathModel getMathModel(KeyValue mathModelKey) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public MathModel getMathModel(MathModelInfo mathModelInfo) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public MathModelInfo getMathModelInfo(KeyValue mathModelKey) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public MathModelInfo[] getMathModelInfos();


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:26:38 PM)
 * @param preferences cbit.util.Preference[]
 * @exception java.rmi.RemoteException The exception description.
 */
Preference[] getPreferences() throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (4/30/2003 10:25:07 PM)
 */
public Model getReactionStepAsModel(KeyValue reactionStepKey) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public SimulationStatus getServerSimulationStatus(VCSimulationIdentifier vcSimulationIdentifier) throws DataAccessException;


	public SessionManager getSessionManager();


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public TestSuiteInfoNew[] getTestSuiteInfos() throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public User getUser();


/**
 * Insert the method's description here.
 * Creation date: (4/30/2003 10:25:07 PM)
 */
public ReactionDescription[] getUserReactionDescriptions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (4/30/2003 10:25:07 PM)
 */
public ReactionStepInfo[] getUserReactionStepInfos(KeyValue reactionStepKeys[]) throws DataAccessException;


	/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 *
 * String vcImageXML is optional
 */
public boolean isChanged(VCImage vcImage, String vcImageXML) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 *
 * String bioModelXML is optional
 */
public boolean isChanged(BioModel bioModel, String bioModelXML) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 6:56:30 AM)
 * @return boolean
 * @param vcDocument cbit.vcell.document.VCDocument
 */
boolean isChanged(VCDocument vcDocument) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 *
 * String geometryXML is optional
 */
public boolean isChanged(Geometry geometry, String geometryXML) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 *
 * String mathModelXML is optional
 */
public boolean isChanged(MathModel bioModel, String mathModelXML) throws DataAccessException;


/**
 * 
 * @param newListener cbit.vcell.clientdb.DatabaseListener
 */
public void removeDatabaseListener(DatabaseListener newListener);


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
VCImageInfo removeUserFromGroup(VCImageInfo imageInfo,String user) throws DataAccessException, DependencyException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
BioModelInfo removeUserFromGroup(BioModelInfo bioModelInfo,String user) throws DataAccessException, DependencyException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
GeometryInfo removeUserFromGroup(GeometryInfo geometryInfo,String user) throws DataAccessException, DependencyException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
MathModelInfo removeUserFromGroup(MathModelInfo mathModelInfo,String user) throws DataAccessException, DependencyException;


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:26:38 PM)
 * @param preferences cbit.util.Preference[]
 * @exception java.rmi.RemoteException The exception description.
 */
void replacePreferences(Preference[] preferences) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public VCImage save(VCImage vcImage) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public BioModel save(BioModel bioModel, String independentSims[]) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public Geometry save(Geometry geometry) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public MathModel save(MathModel bioModel, String independentSims[]) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public VCImage saveAsNew(VCImage vcImage, String newName) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public BioModel saveAsNew(BioModel bioModel, String newName, String independentSims[]) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public Geometry saveAsNew(Geometry geometry, String newName) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public MathModel saveAsNew(MathModel mathModel, String newName, String independentSims[]) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
VCImageInfo setGroupPrivate(VCImageInfo imageInfo) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
BioModelInfo setGroupPrivate(BioModelInfo bioModelInfo) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
GeometryInfo setGroupPrivate(GeometryInfo geometryInfo) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
MathModelInfo setGroupPrivate(MathModelInfo mathModelInfo) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
VCImageInfo setGroupPublic(VCImageInfo imageInfo) throws DataAccessException;


public void substituteFieldFuncNames(VCDocument vcDocument,VersionableTypeVersion originalOwner) throws DataAccessException,MathException,ExpressionException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
BioModelInfo setGroupPublic(BioModelInfo bioModelInfo) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
GeometryInfo setGroupPublic(GeometryInfo geometryInfo) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
MathModelInfo setGroupPublic(MathModelInfo mathModelInfo) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/14/2005 1:40:15 AM)
 * @param jobEvent cbit.rmi.event.SimulationJobStatusEvent
 * @exception org.vcell.util.DataAccessException The exception description.
 */
void updateServerSimulationStatusFromJobEvent(SimulationJobStatusEvent jobEvent) throws DataAccessException;

void preloadSimulationStatus(VCSimulationIdentifier[] simIDs);
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;
import cbit.vcell.solver.ode.gui.SimulationStatusPersistent;
import cbit.vcell.field.db.FieldDataDBOperationResults;
import cbit.vcell.field.db.FieldDataDBOperationSpec;
import cbit.vcell.mathmodel.*;
import cbit.vcell.model.DBFormalSpecies;
import cbit.vcell.model.DBSpecies;
import cbit.vcell.model.FormalSpeciesType;
import cbit.vcell.biomodel.BioModelMetaData;

import java.rmi.*;

import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.CurateSpec;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.VersionableType;


/**
 * This type was created in VisualAge.
 */
public interface UserMetaDbServer extends java.rmi.Remote {
/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 9:42:56 AM)
 */
org.vcell.util.document.VCDocumentInfo curate(CurateSpec curateSpec) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


UserRegistrationResults userRegistrationOP(UserRegistrationOP userRegistrationOP) throws org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException, java.rmi.RemoteException;

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
public FieldDataDBOperationResults fieldDataDBOperation(FieldDataDBOperationSpec fieldDataDBOperationSpec) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


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
 * Creation date: (4/29/2004 1:03:11 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception java.rmi.RemoteException The exception description.
 */
void deleteVCImage(KeyValue imageKey) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.vcell.numericstest.TestSuiteOPResults doTestSuiteOP(cbit.vcell.numericstest.TestSuiteOP tsop) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
org.vcell.util.document.ReferenceQueryResult findReferences(org.vcell.util.document.ReferenceQuerySpec rqs) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.VersionableFamily
 * @param vType cbit.sql.VersionableType
 * @param key cbit.sql.KeyValue
 */
org.vcell.util.document.VersionableFamily getAllReferences(VersionableType vType, KeyValue key) throws RemoteException, DataAccessException, ObjectNotFoundException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
org.vcell.util.document.BioModelInfo getBioModelInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * @param bAll -- get all models user has access to 
 * @return information about models owned by logged in user (bAll == false) or all models user has access to (bAll == true)
 */
org.vcell.util.document.BioModelInfo[] getBioModelInfos(boolean bAll) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BioModelMetaData getBioModelMetaData(KeyValue key) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BioModelMetaData[] getBioModelMetaDatas(boolean bAll) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString getBioModelXML(KeyValue key) throws DataAccessException, java.rmi.RemoteException;


public DBSpecies getBoundSpecies(DBFormalSpecies dbfs) throws DataAccessException, java.rmi.RemoteException;


public DBFormalSpecies[] getDatabaseSpecies(String likeString,boolean isBound,FormalSpeciesType speciesType,int restrictSearch,int rowLimit, boolean bUserOnly) throws DataAccessException, java.rmi.RemoteException;


public cbit.vcell.dictionary.db.ReactionDescription[] getDictionaryReactions(cbit.vcell.modeldb.ReactionQuerySpec reactionQuerySpec) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.vcell.geometry.GeometryInfo getGeometryInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.vcell.geometry.GeometryInfo[] getGeometryInfos(boolean bAll) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString getGeometryXML(KeyValue key) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
MathModelInfo getMathModelInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
MathModelInfo[] getMathModelInfos(boolean bAll) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
MathModelMetaData getMathModelMetaData(KeyValue key) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return MathModelMetaData[]
 * @param bAll boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
MathModelMetaData[] getMathModelMetaDatas(boolean bAll) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString getMathModelXML(KeyValue key) throws DataAccessException, java.rmi.RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:29:11 PM)
 * @return cbit.util.Preference
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
org.vcell.util.Preference[] getPreferences() throws DataAccessException, java.rmi.RemoteException;


public cbit.vcell.model.Model getReactionStepAsModel(KeyValue rxID) throws DataAccessException, java.rmi.RemoteException;


public cbit.vcell.model.ReactionStepInfo[] getReactionStepInfos(KeyValue reactionStepKeys[]) throws DataAccessException, java.rmi.RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (2/4/01 5:19:25 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
SimulationStatusPersistent[] getSimulationStatus(KeyValue simulationKeys[]) throws DataAccessException, RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (2/4/01 5:19:25 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
SimulationStatusPersistent getSimulationStatus(KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException, RemoteException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString getSimulationXML(KeyValue key) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.vcell.numericstest.TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.vcell.numericstest.TestSuiteInfoNew[] getTestSuiteInfos() throws DataAccessException, java.rmi.RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 5:05:56 PM)
 * @return cbit.vcell.dictionary.ReactionDescription[]
 * @param reactionQuerySpec cbit.vcell.modeldb.ReactionQuerySpec
 */
cbit.vcell.dictionary.db.ReactionDescription[] getUserReactionDescriptions(cbit.vcell.modeldb.ReactionQuerySpec reactionQuerySpec) throws DataAccessException, RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.image.VCImageInfo getVCImageInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.image.VCImageInfo[] getVCImageInfos(boolean bAll) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
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
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
org.vcell.util.document.VersionInfo groupAddUser(VersionableType vType, KeyValue key,String addUserToGroup,boolean isHidden) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
org.vcell.util.document.VersionInfo groupRemoveUser(VersionableType vType, KeyValue key,String userRemoveFromGroup,boolean isHiddenFromOwner) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
org.vcell.util.document.VersionInfo groupSetPrivate(VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
org.vcell.util.document.VersionInfo groupSetPublic(VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException, java.rmi.RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:28:18 PM)
 * @param preferences cbit.util.Preference[]
 * @exception java.rmi.RemoteException The exception description.
 */
void replacePreferences(org.vcell.util.Preference[] preferences) throws java.rmi.RemoteException, DataAccessException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveBioModel(BigString bioModelXML, String independentSims[]) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveBioModelAs(BigString bioModelXML, String newName, String independentSims[]) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveGeometry(BigString geometryXML) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveGeometryAs(BigString geometryXML, String newName) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveMathModel(BigString mathModelXML, String independentSims[]) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveMathModelAs(BigString mathModelXML, String newName, String independentSims[]) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveSimulation(BigString simulationXML, boolean forceIndependent) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveVCImage(BigString vcImageXML) throws DataAccessException, java.rmi.RemoteException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
BigString saveVCImageAs(BigString vcImageXML, String newName) throws DataAccessException, java.rmi.RemoteException;
}

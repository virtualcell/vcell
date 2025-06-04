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

import cbit.image.VCImageInfo;
import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.field.FieldDataAllDBEntries;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyException;
import cbit.vcell.model.*;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestSuiteNew;
import cbit.vcell.numericstest.TestSuiteOP;
import cbit.vcell.numericstest.TestSuiteOPResults;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.Preference;
import org.vcell.util.document.*;

import java.util.Hashtable;
import java.util.TreeMap;


/**
 * This type was created in VisualAge.
 */
public interface UserMetaDbServer {
	
TreeMap<User.SPECIAL_CLAIM,TreeMap<User,String>> getSpecialUsers() throws DataAccessException;

/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 9:42:56 AM)
 */
VCDocumentInfo curate(CurateSpec curateSpec) throws DataAccessException, ObjectNotFoundException, RemoteProxyException;


UserRegistrationResults userRegistrationOP(UserRegistrationOP userRegistrationOP) throws DataAccessException, ObjectNotFoundException, RemoteProxyException;

/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 1:03:11 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception RemoteProxyException The exception description.
 */
void deleteBioModel(KeyValue bioModelKey) throws DataAccessException, ObjectNotFoundException, RemoteProxyException;


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 1:03:11 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception RemoteProxyException The exception description.
 */
public FieldDataAllDBEntries getAllFieldDataIDs() throws DataAccessException, ObjectNotFoundException, RemoteProxyException;


public void fieldDataFromSimulation(KeyValue sourceSim, int jobIndex, String newFieldDataName);

public Hashtable<String, ExternalDataIdentifier> copyModelsFieldData(String modelKey, VersionableType modelType);

/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 1:03:11 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception RemoteProxyException The exception description.
 */
void deleteGeometry(KeyValue geometryKey) throws DataAccessException, ObjectNotFoundException, RemoteProxyException;


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 1:03:11 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception RemoteProxyException The exception description.
 */
void deleteMathModel(KeyValue mathModelKey) throws DataAccessException, ObjectNotFoundException, RemoteProxyException;


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 1:03:11 PM)
 * @param bioModelKey cbit.sql.KeyValue
 * @exception RemoteProxyException The exception description.
 */
void deleteVCImage(KeyValue imageKey) throws DataAccessException, ObjectNotFoundException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
TestSuiteOPResults doTestSuiteOP(TestSuiteOP tsop) throws DataAccessException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
org.vcell.util.document.ReferenceQueryResult findReferences(ReferenceQuerySpec rqs) throws DataAccessException, ObjectNotFoundException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.VersionableFamily
 * @param vType cbit.sql.VersionableType
 * @param key cbit.sql.KeyValue
 */
VersionableFamily getAllReferences(VersionableType vType, KeyValue key) throws RemoteProxyException, DataAccessException, ObjectNotFoundException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
BioModelInfo getBioModelInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException, RemoteProxyException;


/**
 * @param bAll -- get all models user has access to 
 * @return information about models owned by logged in user (bAll == false) or all models user has access to (bAll == true)
 */
BioModelInfo[] getBioModelInfos(boolean bAll) throws DataAccessException, RemoteProxyException;

/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
BigString getBioModelXML(KeyValue key) throws DataAccessException, RemoteProxyException;


public DBSpecies getBoundSpecies(DBFormalSpecies dbfs) throws DataAccessException, RemoteProxyException;


public DBFormalSpecies[] getDatabaseSpecies(String likeString,boolean isBound,FormalSpeciesType speciesType,int restrictSearch,int rowLimit, boolean bUserOnly) throws DataAccessException, RemoteProxyException;


public ReactionDescription[] getDictionaryReactions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
GeometryInfo getGeometryInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
GeometryInfo[] getGeometryInfos(boolean bAll) throws DataAccessException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
BigString getGeometryXML(KeyValue key) throws DataAccessException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
MathModelInfo getMathModelInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
MathModelInfo[] getMathModelInfos(boolean bAll) throws DataAccessException, RemoteProxyException;

/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
BigString getMathModelXML(KeyValue key) throws DataAccessException, RemoteProxyException;


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:29:11 PM)
 * @return cbit.util.Preference
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
Preference[] getPreferences() throws DataAccessException, RemoteProxyException;


public String getReactionStepAsModel(KeyValue rxID) throws DataAccessException, RemoteProxyException;


public ReactionStepInfo[] getReactionStepInfos(KeyValue reactionStepKeys[]) throws DataAccessException, RemoteProxyException;


/**
 * Insert the method's description here.
 * Creation date: (2/4/01 5:19:25 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
SimulationStatusPersistent[] getSimulationStatus(KeyValue simulationKeys[]) throws DataAccessException, RemoteProxyException;


/**
 * Insert the method's description here.
 * Creation date: (2/4/01 5:19:25 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo
 * @param simKey cbit.sql.KeyValue
 */
SimulationStatusPersistent getSimulationStatus(KeyValue simulationKey) throws DataAccessException, ObjectNotFoundException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
BigString getSimulationXML(KeyValue key) throws DataAccessException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws DataAccessException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
TestSuiteInfoNew[] getTestSuiteInfos() throws DataAccessException, RemoteProxyException;


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 5:05:56 PM)
 * @return cbit.vcell.dictionary.ReactionDescription[]
 * @param reactionQuerySpec cbit.vcell.modeldb.ReactionQuerySpec
 */
ReactionDescription[] getUserReactionDescriptions(ReactionQuerySpec reactionQuerySpec) throws DataAccessException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
VCImageInfo getVCImageInfo(KeyValue key) throws DataAccessException, ObjectNotFoundException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return GeometryInfo
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
VCImageInfo[] getVCImageInfos(boolean bAll) throws DataAccessException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return Geometry
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
BigString getVCImageXML(KeyValue key) throws DataAccessException, RemoteProxyException;


/**
 * Insert the method's description here.
 * Creation date: (9/25/2003 7:52:33 AM)
 * @return cbit.vcell.modeldb.VCInfoContainer
 */
VCInfoContainer getVCInfoContainer() throws DataAccessException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
VersionInfo groupAddUser(VersionableType vType, KeyValue key,String addUserToGroup,boolean isHidden) throws DataAccessException, ObjectNotFoundException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
VersionInfo groupRemoveUser(VersionableType vType, KeyValue key,String userRemoveFromGroup,boolean isHiddenFromOwner) throws DataAccessException, ObjectNotFoundException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
VersionInfo groupSetPrivate(VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return void
 * @param key KeyValue
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
VersionInfo groupSetPublic(VersionableType vType, KeyValue key) throws DataAccessException, ObjectNotFoundException, RemoteProxyException;


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:28:18 PM)
 * @param preferences cbit.util.Preference[]
 * @exception RemoteProxyException The exception description.
 */
void replacePreferences(Preference[] preferences) throws RemoteProxyException, DataAccessException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
BigString saveBioModel(BigString bioModelXML, String independentSims[]) throws DataAccessException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
BigString saveBioModelAs(BigString bioModelXML, String newName, String independentSims[]) throws DataAccessException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
BigString saveGeometry(BigString geometryXML) throws DataAccessException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
BigString saveGeometryAs(BigString geometryXML, String newName) throws DataAccessException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
BigString saveMathModel(BigString mathModelXML, String independentSims[]) throws DataAccessException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
BigString saveMathModelAs(BigString mathModelXML, String newName, String independentSims[]) throws DataAccessException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
BigString saveSimulation(BigString simulationXML, boolean forceIndependent) throws DataAccessException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
BigString saveVCImage(BigString vcImageXML) throws DataAccessException, RemoteProxyException;


/**
 * This method was created in VisualAge.
 * @return Versionable
 * @param versionable Versionable
 * @param bVersion boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception RemoteProxyException The exception description.
 */
BigString saveVCImageAs(BigString vcImageXML, String newName) throws DataAccessException, RemoteProxyException;
}

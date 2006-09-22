package cbit.vcell.client.database;
import java.rmi.RemoteException;

import cbit.image.VCImage;
import cbit.image.VCImageInfo;
import cbit.util.CurateSpec;
import cbit.util.DataAccessException;
import cbit.util.KeyValue;
import cbit.util.User;
import cbit.util.VCDocument;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.dictionary.DBFormalSpecies;
import cbit.vcell.dictionary.DBSpecies;
import cbit.vcell.dictionary.FormalSpeciesType;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.mathmodel.MathModelInfo;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simulation.Simulation;
import cbit.vcell.simulation.VCSimulationIdentifier;
import cbit.vcell.xml.XmlDialect;
/**
 * Insert the type's description here.
 * Creation date: (10/28/00 12:05:57 AM)
 * @author: 
 */
public interface DocumentManager {

/**
 * 
 * @param newListener cbit.vcell.clientdb.DatabaseListener
 */
public void addDatabaseListener(cbit.vcell.client.database.DatabaseListener newListener);


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
VCImageInfo addUserToGroup(VCImageInfo imageInfo,String user) throws cbit.util.DataAccessException, cbit.vcell.server.DependencyException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
BioModelInfo addUserToGroup(BioModelInfo bioModelInfo,String user) throws cbit.util.DataAccessException, cbit.vcell.server.DependencyException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
GeometryInfo addUserToGroup(GeometryInfo geometryInfo,String user) throws cbit.util.DataAccessException, cbit.vcell.server.DependencyException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
MathModelInfo addUserToGroup(MathModelInfo mathModelInfo,String user) throws cbit.util.DataAccessException, cbit.vcell.server.DependencyException;


/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 9:28:15 AM)
 * @param vcDocumentInfo cbit.vcell.document.VCDocumentInfo
 * @param curateFunction int
 */
void curate(CurateSpec curateSpec) throws cbit.util.DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
void delete(VCImageInfo vcImageInfo) throws cbit.util.DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
void delete(BioModelInfo bioModelInfo) throws cbit.util.DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
void delete(GeometryInfo geometryInfo) throws cbit.util.DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 11:20:28 PM)
 * @param mathModelInfo cbit.vcell.mathmodel.MathModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
void delete(MathModelInfo mathModelInfo) throws cbit.util.DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public cbit.vcell.numericstest.TestSuiteOPResults doTestSuiteOP(cbit.vcell.numericstest.TestSuiteOP tsop) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 2:50:07 PM)
 * @return cbit.sql.Versionable
 * @param vType cbit.sql.VersionableType
 * @param key cbit.sql.KeyValue
 */
cbit.vcell.modeldb.ReferenceQueryResult findReferences(cbit.vcell.modeldb.ReferenceQuerySpec sqs) throws DataAccessException;

	public void generatePDF(BioModel biomodel, java.io.FileOutputStream fos) throws Exception;


	public void generatePDF(BioModel biomodel, java.io.FileOutputStream fos, java.awt.print.PageFormat pageFormat) throws Exception;


	public void generatePDF(Geometry geom, java.io.FileOutputStream fos) throws Exception;


	public void generatePDF(Geometry geom, java.io.FileOutputStream fos, java.awt.print.PageFormat pageFormat) throws Exception;


	public void generatePDF(MathModel mathmodel, java.io.FileOutputStream fos) throws Exception;


	public void generatePDF(MathModel mathmodel, java.io.FileOutputStream fos, java.awt.print.PageFormat pageFormat) throws Exception;


	public void generateReactionsImage(cbit.vcell.model.Model model, cbit.vcell.model.Structure struct, String resolution, java.io.FileOutputStream fos) throws Exception;


	public void generateStructureImage(cbit.vcell.model.Model model, String resolution, java.io.FileOutputStream fos) throws Exception;


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
public BioModelInfo[] getBioModelInfos() throws DataAccessException;


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
public cbit.vcell.dictionary.ReactionDescription[] getDictionaryReactions(cbit.vcell.modeldb.ReactionQuerySpec reactionQuerySpec) throws DataAccessException;


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
public GeometryInfo[] getGeometryInfos() throws DataAccessException;


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
public MathModelInfo[] getMathModelInfos() throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:26:38 PM)
 * @param preferences cbit.util.Preference[]
 * @exception java.rmi.RemoteException The exception description.
 */
cbit.util.Preference[] getPreferences() throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (4/30/2003 10:25:07 PM)
 */
public cbit.vcell.model.ReactionStep getReactionStep(KeyValue reactionStepKey) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public SimulationStatus getServerSimulationStatus(VCSimulationIdentifier vcSimulationIdentifier) throws DataAccessException;


	public cbit.vcell.desktop.controls.SessionManager getSessionManager();


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public cbit.vcell.numericstest.TestSuiteNew getTestSuite(java.math.BigDecimal getThisTS) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public cbit.vcell.numericstest.TestSuiteInfoNew[] getTestSuiteInfos() throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:07:09 AM)
 */
public User getUser();


/**
 * Insert the method's description here.
 * Creation date: (4/30/2003 10:25:07 PM)
 */
public cbit.vcell.dictionary.ReactionDescription[] getUserReactionDescriptions(cbit.vcell.modeldb.ReactionQuerySpec reactionQuerySpec) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (4/30/2003 10:25:07 PM)
 */
public cbit.vcell.model.ReactionStepInfo[] getUserReactionStepInfos(KeyValue reactionStepKeys[]) throws DataAccessException;


	public String getXML(BioModelInfo bmInfo) throws cbit.util.DataAccessException, java.rmi.RemoteException, cbit.util.xml.XmlParseException;


/**
 * This method returns a XML String of the given bioModel object with a specific type.
 * Creation date: (8/22/2000 3:30:43 PM)
 * @return java.lang.String
 * @param bioModel cbit.vcell.biomodel.BioModel
 */
String getXML(cbit.vcell.biomodel.BioModelInfo bioModelInfoArg, XmlDialect toDialect) throws RemoteException, cbit.util.xml.XmlParseException, java.io.IOException, cbit.util.DataAccessException;


	public String getXML(MathModelInfo mmInfo) throws cbit.util.DataAccessException, java.rmi.RemoteException, cbit.util.xml.XmlParseException;


/**
 * This method returns a XML String of the given MathModel object with a specific type.
 * Creation date: (8/22/2000 3:30:43 PM)
 * @return java.lang.String
 * @param bioModel cbit.vcell.biomodel.BioModel
 */
String getXML(MathModelInfo mathModelInfoArg, XmlDialect toDialect) throws RemoteException, cbit.util.xml.XmlParseException, java.io.IOException,cbit.util.DataAccessException;


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
public void removeDatabaseListener(cbit.vcell.client.database.DatabaseListener newListener);


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
VCImageInfo removeUserFromGroup(VCImageInfo imageInfo,String user) throws cbit.util.DataAccessException, cbit.vcell.server.DependencyException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
BioModelInfo removeUserFromGroup(BioModelInfo bioModelInfo,String user) throws cbit.util.DataAccessException, cbit.vcell.server.DependencyException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
GeometryInfo removeUserFromGroup(GeometryInfo geometryInfo,String user) throws cbit.util.DataAccessException, cbit.vcell.server.DependencyException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
MathModelInfo removeUserFromGroup(MathModelInfo mathModelInfo,String user) throws cbit.util.DataAccessException, cbit.vcell.server.DependencyException;


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:26:38 PM)
 * @param preferences cbit.util.Preference[]
 * @exception java.rmi.RemoteException The exception description.
 */
void replacePreferences(cbit.util.Preference[] preferences) throws DataAccessException;


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
 * @deprecated for testing purposes only.
 */
public Simulation save(Simulation simulation, boolean bForceIndependent) throws DataAccessException;


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
 * @exception cbit.util.DataAccessException The exception description.
 */
VCImageInfo setGroupPrivate(VCImageInfo imageInfo) throws cbit.util.DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
BioModelInfo setGroupPrivate(BioModelInfo bioModelInfo) throws cbit.util.DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
GeometryInfo setGroupPrivate(GeometryInfo geometryInfo) throws cbit.util.DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
MathModelInfo setGroupPrivate(MathModelInfo mathModelInfo) throws cbit.util.DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
VCImageInfo setGroupPublic(VCImageInfo imageInfo) throws cbit.util.DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
BioModelInfo setGroupPublic(BioModelInfo bioModelInfo) throws cbit.util.DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
GeometryInfo setGroupPublic(GeometryInfo geometryInfo) throws cbit.util.DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:16 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @exception cbit.util.DataAccessException The exception description.
 */
MathModelInfo setGroupPublic(MathModelInfo mathModelInfo) throws cbit.util.DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/14/2005 1:40:15 AM)
 * @param jobEvent cbit.rmi.event.SimulationJobStatusEvent
 * @exception cbit.util.DataAccessException The exception description.
 */
void updateServerSimulationStatusFromJobEvent(cbit.rmi.event.SimulationJobStatusEvent jobEvent) throws cbit.util.DataAccessException;
}
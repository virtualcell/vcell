package cbit.vcell.client;
import cbit.util.DataAccessException;
import cbit.util.VCDataIdentifier;
import cbit.util.VCDocument;
import cbit.util.VCDocumentInfo;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.export.ExportSpecs;
import cbit.vcell.export.server.*;
import cbit.vcell.mapping.SimulationContext;

import cbit.vcell.client.server.*;
import cbit.vcell.client.data.DynamicDataManager;
import cbit.vcell.client.database.DocumentManager;
import cbit.vcell.solver.*;
import cbit.vcell.desktop.controls.*;
import cbit.vcell.server.*;
import cbit.vcell.simdata.DataManager;
import cbit.vcell.xml.merge.gui.TMLPanel;
import cbit.vcell.mathmodel.MathModelInfo;
/**
 * Insert the type's description here.
 * Creation date: (5/21/2004 2:36:40 AM)
 * @author: Ion Moraru
 */
public interface RequestManager {
/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 2:12:25 AM)
 * @param clientServerInfo cbit.vcell.client.server.ClientServerInfo
 */
void changeGeometry(DocumentWindowManager requester, SimulationContext simContext);


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 2:51:33 AM)
 * @param windowID java.lang.String
 */
boolean closeWindow(String managerID, boolean exitIfLast);


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 2:59:24 PM)
 * @param vcDocument cbit.vcell.document.VCDocument
 */
TMLPanel compareWithOther(VCDocumentInfo vcDoc1, VCDocumentInfo vcDoc2);


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 2:59:24 PM)
 * @param vcDocument cbit.vcell.document.VCDocument
 */
TMLPanel compareWithSaved(VCDocument document);


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 2:12:25 AM)
 * @param clientServerInfo cbit.vcell.client.server.ClientServerInfo
 */
void connectAs(String user, String password, TopLevelWindowManager requester);


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 2:12:25 AM)
 * @param clientServerInfo cbit.vcell.client.server.ClientServerInfo
 */
void connectToServer(ClientServerInfo clientServerInfo);


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 2:37:55 AM)
 * @param documentType int
 */
void createMathModelFromApplication(String name, SimulationContext simContext);


/**
 * Insert the method's description here.
 * Creation date: (5/29/2006 11:16:29 AM)
 */
void curateDocument(VCDocumentInfo vcDocInfo, int curateType, final TopLevelWindowManager requester);


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 2:38:26 AM)
 * @param documentInfo cbit.vcell.document.VCDocumentInfo
 */
void deleteDocument(VCDocumentInfo documentInfo, TopLevelWindowManager requester);


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 2:53:15 AM)
 */
void exitApplication();


/**
 * Comment
 */
void exportDocument(TopLevelWindowManager manager);

/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 3:20:26 PM)
 * @return cbit.vcell.client.AsynchMessageManager
 */
AsynchMessageManager getAsynchMessageManager();


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 3:20:26 PM)
 * @return cbit.vcell.client.AsynchMessageManager
 */
cbit.vcell.server.bionetgen.BNGService getBNGService();


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 6:40:26 AM)
 * @return cbit.vcell.desktop.controls.DataManager
 * @param vcDataIdentifier cbit.vcell.server.VCDataIdentifier
 */
DataManager getDataManager(VCDataIdentifier vcDataID, boolean isSpatial) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 9:56:48 AM)
 * @return cbit.vcell.clientdb.DocumentManager
 */
DocumentManager getDocumentManager();


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 6:40:26 AM)
 * @return cbit.vcell.desktop.controls.DataManager
 * @param vcDataIdentifier cbit.vcell.server.VCDataIdentifier
 */
DynamicDataManager getDynamicDataManager(VCDataIdentifier vcdId) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 6:40:26 AM)
 * @return cbit.vcell.desktop.controls.DataManager
 * @param vcDataIdentifier cbit.vcell.server.VCDataIdentifier
 */
DynamicDataManager getDynamicDataManager(Simulation simulation) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (11/16/2004 7:55:08 AM)
 */
SimulationStatus getServerSimulationStatus(SimulationInfo simInfo);

/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 5:54:11 PM)
 * @return cbit.vcell.client.UserPreferences
 */
UserPreferences getUserPreferences();


/**
 * Insert the method's description here.
 * Creation date: (8/26/2005 3:16:18 PM)
 * @return boolean
 */
boolean isApplet();


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 2:38:26 AM)
 * @param documentInfo cbit.vcell.document.VCDocumentInfo
 */
void managerIDchanged(String oldID, String newID);


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 2:37:55 AM)
 * @param documentType int
 */
void newDocument(int documentType, int option);


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 2:38:26 AM)
 * @param documentInfo cbit.vcell.document.VCDocumentInfo
 */
void openDocument(int documentType, DocumentWindowManager requester);


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 2:38:26 AM)
 * @param documentInfo cbit.vcell.document.VCDocumentInfo
 */
void openDocument(VCDocumentInfo documentInfo, TopLevelWindowManager requester, boolean inNewWindow);


	public void processComparisonResult(TMLPanel comparePanel, TopLevelWindowManager requester);


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 2:12:25 AM)
 * @param clientServerInfo cbit.vcell.client.server.ClientServerInfo
 */
void reconnect();


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 2:59:24 PM)
 * @param vcDocument cbit.vcell.document.VCDocument
 */
void revertToSaved(DocumentWindowManager documentWindowManager);


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 2:23:41 AM)
 * @param documentWindowManager cbit.vcell.client.DocumentWindowManager
 * @param simulation cbit.vcell.solver.Simulation
 */
void runSimulation(SimulationInfo simInfo) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 2:23:41 AM)
 * @param documentWindowManager cbit.vcell.client.DocumentWindowManager
 * @param simulation cbit.vcell.solver.Simulation
 */
void runSimulations(ClientSimManager clientSimManager, Simulation[] simulations);


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 2:58:20 PM)
 * @param vcDocument cbit.vcell.document.VCDocument
 * @param replace boolean
 */
void saveDocument(DocumentWindowManager documentWindowManager, boolean replace);


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 2:59:24 PM)
 * @param vcDocument cbit.vcell.document.VCDocument
 */
void saveDocumentAsNew(DocumentWindowManager documentWindowManager);


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:18:53 AM)
 */
BioModelInfo selectBioModelInfo(TopLevelWindowManager tfWindowManager);


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:18:53 AM)
 */
MathModelInfo selectMathModelInfo(TopLevelWindowManager tfWindowManager);


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:18:53 AM)
 */
void showBNGWindow();


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:18:53 AM)
 */
void showDatabaseWindow();


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:18:53 AM)
 */
void showTestingFrameworkWindow();


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 9:58:46 PM)
 */
public void startExport(TopLevelWindowManager windowManager, ExportSpecs exportSpecs);


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 2:23:41 AM)
 * @param documentWindowManager cbit.vcell.client.DocumentWindowManager
 * @param simulation cbit.vcell.solver.Simulation
 */
void stopSimulations(ClientSimManager clientSimManager, Simulation[] simulations);


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 12:09:57 PM)
 */
void updateStatusNow();
}
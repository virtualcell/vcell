package cbit.vcell.client;
import cbit.vcell.server.bionetgen.BNGService;
import cbit.vcell.numericstest.AddTestCasesOP;
import cbit.vcell.numericstest.AddTestSuiteOP;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestSuiteNew;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCaseNewMathModel;
import cbit.vcell.numericstest.TestCaseNewBioModel;
import cbit.vcell.numericstest.TestSuiteOP;
import java.awt.event.*;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import cbit.xml.merge.*;
import cbit.image.*;
import cbit.vcell.export.server.*;
import cbit.vcell.xml.XMLInfo;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.solver.ode.gui.*;
import cbit.vcell.messaging.db.*;
import cbit.vcell.solver.*;
import cbit.vcell.client.task.*;
import cbit.vcell.desktop.controls.*;
import cbit.sql.*;
import cbit.vcell.math.*;
import cbit.vcell.mapping.*;
import java.beans.*;
import cbit.util.*;
import swingthreads.*;
import java.awt.*;
import cbit.vcell.client.server.*;
import cbit.vcell.server.*;
import cbit.vcell.geometry.*;
import cbit.vcell.mathmodel.*;
import cbit.vcell.biomodel.*;
import cbit.vcell.document.*;
import cbit.vcell.client.desktop.*;
import java.util.*;
import javax.swing.*;
import cbit.vcell.xml.XMLTags;
/**
 * Insert the type's description here.
 * Creation date: (5/21/2004 2:42:55 AM)
 * @author: Ion Moraru
 */
public class ClientRequestManager implements RequestManager, PropertyChangeListener, cbit.rmi.event.ExportListener, cbit.rmi.event.VCellMessageEventListener {
	private VCellClient vcellClient = null;
	private boolean bOpening = false;
	private boolean bExiting = false;

/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:22:13 AM)
 * @param vcellClient cbit.vcell.client.VCellClient
 */
public ClientRequestManager(VCellClient vcellClient) {
	setVcellClient(vcellClient);
	// listen to connectionStatus events
	getClientServerManager().addPropertyChangeListener(this);
	getClientServerManager().getAsynchMessageManager().addExportListener(this);
	getClientServerManager().getAsynchMessageManager().addVCellMessageEventListener(this);
	
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:20:47 AM)
 * @param windowManager cbit.vcell.client.desktop.DocumentWindowManager
 */
public void changeGeometry(DocumentWindowManager requester, SimulationContext simContext) {
	GeometryInfo geometryInfo = null;
	try {
		geometryInfo = (GeometryInfo)getMdiManager().getDatabaseWindowManager().selectDocument(VCDocument.GEOMETRY_DOC, requester);
	} catch (UserCancelException uexc) {
		System.out.println(uexc);
		return;
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(requester, "Failed to retrieve geometry\n"+exc);
		return;
	}
	changeGeometryAfterChecking(geometryInfo, requester, simContext);
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:20:47 AM)
 * @param windowManager cbit.vcell.client.desktop.DocumentWindowManager
 */
private void changeGeometryAfterChecking(final GeometryInfo geoInfo, final DocumentWindowManager requester, final SimulationContext simContext) {
	/* asynchronous and not blocking any window */
		
	// start a thread that gets it and updates the GUI
	SwingWorker worker = new SwingWorker() {
		private Geometry geometry = null;
		private AsynchProgressPopup pp = new AsynchProgressPopup(requester.getComponent(), "Changing geometry", "Document: '", false, false);
		private Throwable exc = null;
		public Object construct() {
			pp.start();
			getMdiManager().blockWindow(requester.getManagerID());
			try {
				geometry = getDocumentManager().getGeometry(geoInfo);
			} catch (Throwable e) {
				exc = e;
			}
			return geometry;
		}
		public void finished() {
			if (exc != null) {
				pp.stop();
				exc.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(requester, "Changing Geometry failed:\n\n" + exc.getMessage());
				getMdiManager().unBlockWindow(requester.getManagerID());
			} else {
				// reset Geometry
				if (requester instanceof BioModelWindowManager) {
					try {
						simContext.setGeometry(geometry);
					} catch (Exception e) {
						PopupGenerator.showErrorDialog(requester, "Changing Geometry failed:\n\n" + e.getMessage());
					}
				} else if (requester instanceof MathModelWindowManager) {
					try {
						MathModel mathModel = (MathModel)((MathModelWindowManager)requester).getVCDocument();
						mathModel.getMathDescription().setGeometry(geometry);						
					} catch (Exception e) {
						PopupGenerator.showErrorDialog(requester, "Changing Geometry failed:\n\n" + e.getMessage());
					}
				}
				getMdiManager().unBlockWindow(requester.getManagerID());
				pp.stop();
			}
		}
	};
	worker.start();
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:20:47 AM)
 * @param windowManager cbit.vcell.client.desktop.DocumentWindowManager
 */
private boolean checkBeforeClosing(DocumentWindowManager windowManager) {
	getMdiManager().showWindow(windowManager.getManagerID());
	// we need to check for changes and get user confirmation...
	boolean isChanged = true;
	try {
		isChanged = getDocumentManager().isChanged(windowManager.getVCDocument());
	} catch (Exception exc) {
		String choice = PopupGenerator.showWarningDialog(windowManager, getUserPreferences(), UserMessage.warn_UnableToCheckForChanges, null);
		if (choice.equals(UserMessage.OPTION_CANCEL)){
			// user canceled
			return false;
		}
	}
	// warn if necessary
	if (isChanged) {
		String choice = PopupGenerator.showWarningDialog(windowManager, getUserPreferences(), UserMessage.warn_closeWithoutSave,null);
		if (choice.equals(UserMessage.OPTION_CANCEL)){
			// user canceled
			return false;
		}
	}
	// nothing changed, or user confirmed, close it
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (5/10/2004 3:48:16 PM)
 */
private boolean checkDifferentFromBlank(int documentType, VCDocument vcDocument) {
	// Handle Bio/Math models different from Geometry since createDefaultDoc for Geometry
	// will bring up the NewGeometryEditor which is unnecessary.
	// figure out if we come from a blank new document; if so, replace it inside same window

	if (documentType != vcDocument.getDocumentType()) {
		// If the docType we are trying to open is not the same as the requesting windowmanager's docType
		// we have to open the doc in a new window, hence return true;
		return true;
	}
	VCDocument blank = null;
	if (vcDocument.getDocumentType() != VCDocument.GEOMETRY_DOC) {
		// BioModel/MathModel
		blank = createDefaultDocument(vcDocument.getDocumentType());
		try {
			blank.setName(vcDocument.getName());
		} catch (PropertyVetoException e) {} // ignore. doesn't happen
		return !blank.compareEqual(vcDocument);
	} else {
		// Geometry
		blank = new Geometry(vcDocument.getName(), 1);
		if (blank.compareEqual(vcDocument)) {
			return false;
		}
		
		blank = new Geometry(vcDocument.getName(), 2);
		if (blank.compareEqual(vcDocument)) {
			return false;
		}
		
		blank = new Geometry(vcDocument.getName(), 3);
		if (blank.compareEqual(vcDocument)) {
			return false;
		}
		return true;
	} 
}

/**
 * Insert the method's description here.
 * Creation date: (6/16/2004 11:17:18 AM)
 */
private boolean closeAllWindows(boolean duringExit) {
	Enumeration en = getMdiManager().getWindowManagers();
	while (en.hasMoreElements()) {
		TopLevelWindowManager windowManager = (TopLevelWindowManager)en.nextElement();
		boolean closed = closeWindow(windowManager.getManagerID(), duringExit);
		if (!closed) {
			// user canceled, don't keep going...
			return false;
		}
	}
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2005 1:48:09 PM)
 * @param windowID java.lang.String
 */
public boolean closeWindow(java.lang.String windowID, boolean exitIfLast) {
	// called from DocumentWindow or from DatabaseWindow
	TopLevelWindowManager windowManager = getMdiManager().getWindowManager(windowID);
	if (windowManager instanceof DocumentWindowManager) {
		// we'll need to run some checks first
		getMdiManager().showWindow(windowID);
		getMdiManager().blockWindow(windowID);
		if (checkBeforeClosing((DocumentWindowManager)windowManager)) {
			int openWindows = getMdiManager().closeWindow(windowID);
			if (exitIfLast && (openWindows == 0)) {
				setBExiting(true);
				exitApplication();
			}
			return true;
		} else {
			// user canceled
			getMdiManager().unBlockWindow(windowID);
			return false;
		}
	} else if (windowManager instanceof DatabaseWindowManager) {
		// nothing to check here, just close it
		int openWindows = getMdiManager().closeWindow(windowID);
		if (exitIfLast && (openWindows == 0)) {
			setBExiting(true);
			exitApplication();
		}
		return true;
	} else if (windowManager instanceof TestingFrameworkWindowManager) {
		// nothing to check here, just close it
		int openWindows = getMdiManager().closeWindow(windowID);
		if (exitIfLast && (openWindows == 0)) {
			setBExiting(true);
			exitApplication();
		}
		return true;
	} else if (windowManager instanceof BNGWindowManager) {
		// nothing to check here, just close it
		int openWindows = getMdiManager().closeWindow(windowID);
		if (exitIfLast && (openWindows == 0)) {
			setBExiting(true);
			exitApplication();
		}
		return true;
	} else {
		return false;
	}
}


private TMLPanel compareDocuments(VCDocument doc1, VCDocument doc2, String comparisonSetting) throws DataAccessException, XmlParseException {

	if (!TMLPanel.COMPARE_DOCS_SAVED.equals(comparisonSetting) && 
		!TMLPanel.COMPARE_DOCS_OTHER.equals(comparisonSetting)) {
		throw new RuntimeException("Unsupported comparison setting: " + comparisonSetting);
	} 
	if (doc1.getDocumentType() != doc2.getDocumentType()) { 
		throw new RuntimeException("Only comparison of documents of the same type is currently supported");
	}
	String doc1XML = null;
	String doc2XML = null;
	switch (doc1.getDocumentType()) {
		case VCDocument.BIOMODEL_DOC: {
			doc1XML = XmlHelper.bioModelToXML((BioModel)doc1);
			doc2XML = XmlHelper.bioModelToXML((BioModel)doc2);
			break;			 
		}
		case VCDocument.MATHMODEL_DOC: {
			doc1XML = XmlHelper.mathModelToXML((MathModel)doc1);
			doc2XML = XmlHelper.mathModelToXML((MathModel)doc2);
			break;			
		}
		case VCDocument.GEOMETRY_DOC: {
			doc1XML = XmlHelper.geometryToXML((Geometry)doc1);
			doc2XML = XmlHelper.geometryToXML((Geometry)doc2);
			break;			
		}
	}
	XmlTreeDiff diffTree = XmlHelper.compareMerge(doc1XML, doc2XML, comparisonSetting, true);
	TMLPanel aTMLPanel = new cbit.xml.merge.TMLPanel();
	aTMLPanel.setXmlTreeDiff(diffTree);
	String date1 = doc1.getVersion() == null ? "not saved" : doc1.getVersion().getDate().toString();
	String date2 = doc2.getVersion() == null ? "not saved" : doc2.getVersion().getDate().toString();
	aTMLPanel.setBaselineVersionDescription(doc1.getName()+", "+date1);
	aTMLPanel.setModifiedVersionDescription(doc2.getName()+", "+date2);
	
	return aTMLPanel;
}


/**
 Processes the model comparison request.
 * Creation date: (6/9/2004 1:07:09 PM)
 * @param vcDocument cbit.vcell.document.VCDocument
 */
public TMLPanel compareWithOther(final VCDocumentInfo docInfo1, final VCDocumentInfo docInfo2) {

	VCDocument document1, document2;

	try {
		// get the VCDocument versions from documentManager
		if (docInfo1 instanceof BioModelInfo && docInfo2 instanceof BioModelInfo) {
			document1 = getDocumentManager().getBioModel((BioModelInfo)docInfo1);
			document2 = getDocumentManager().getBioModel((BioModelInfo)docInfo2);					
		} else if (docInfo1 instanceof MathModelInfo && docInfo2 instanceof MathModelInfo) {
			document1 = getDocumentManager().getMathModel((MathModelInfo)docInfo1);
			document2 = getDocumentManager().getMathModel((MathModelInfo)docInfo2);					
		} else if (docInfo1 instanceof GeometryInfo && docInfo2 instanceof GeometryInfo) {
			document1 = getDocumentManager().getGeometry((GeometryInfo)docInfo1);
			document2 = getDocumentManager().getGeometry((GeometryInfo)docInfo2);					
		} else {
			throw new IllegalArgumentException("The two documents are invalid or of different types.");
		}
		TMLPanel comparePanel = compareDocuments(document1, document2, TMLPanel.COMPARE_DOCS_OTHER);
		comparePanel.setBaselineVersionDescription(document2.getVersion().getName()+", "+document2.getVersion().getDate().toString());
		comparePanel.setModifiedVersionDescription(document1.getVersion().getName()+", "+document1.getVersion().getDate().toString());

		return comparePanel;
	} catch (Exception e) {
		System.err.println("Unable to process model comparison request.");
		e.printStackTrace();
		return null;
	}
}


/**
   Processes the comparison (XML based) of the loaded model with its saved edition.
 * Creation date: (6/9/2004 1:07:09 PM)
 * @param vcDocument cbit.vcell.document.VCDocument
 */
public TMLPanel compareWithSaved(VCDocument document) {
	
	VCDocument savedVersion = null;
	try {
		if (document == null) {
			throw new IllegalArgumentException("Invalid VC document: " + document);
		}
		// make the info and get saved version
		switch (document.getDocumentType()) {
			case VCDocument.BIOMODEL_DOC: {
				BioModel bioModel = (BioModel)document;
				savedVersion = getDocumentManager().getBioModel(bioModel.getVersion().getVersionKey());
				break;
			}
			case VCDocument.MATHMODEL_DOC: {
				MathModel mathModel = (MathModel)document;
				savedVersion = getDocumentManager().getMathModel(mathModel.getVersion().getVersionKey());
				break;
			}
			case VCDocument.GEOMETRY_DOC: {
				Geometry geometry = (Geometry)document;
				savedVersion = getDocumentManager().getGeometry(geometry.getKey());
				break;
			}
		}
		TMLPanel comparePanel = compareDocuments(savedVersion, document, TMLPanel.COMPARE_DOCS_SAVED);
		comparePanel.setModifiedVersionDescription("Opened document instance");

		return comparePanel;
	} catch (Throwable e) {
		System.err.println("Unable to compare models.");
		e.printStackTrace();
		return null;
	}				
}


/**
 * Insert the method's description here.
 * Creation date: (6/16/2004 11:07:33 AM)
 * @param clientServerInfo cbit.vcell.client.server.ClientServerInfo
 */
public void connectAs(final String user,  final String password, TopLevelWindowManager requester) {
	String confirm = PopupGenerator.showWarningDialog(requester, getUserPreferences(), UserMessage.warn_changeUser,null);
	if (confirm.equals(UserMessage.OPTION_CANCEL)){
		return;
	}
	else {
		boolean closedAllWindows = closeAllWindows(false);
		if (! closedAllWindows) {
			// user bailed out on closing some window
			return;
		} else {
			// ok, connect as a different user
			// asynch & nothing to do on Swing queue (updates handled by events)
			Thread worker = new Thread(new Runnable() {
				public void run() {
					newDocument(VCDocument.BIOMODEL_DOC, 0);
					getClientServerManager().connectAs(user, password);
					getMdiManager().refreshRecyclableWindows();
				}
			});
			worker.start();
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 2:18:16 AM)
 * @param clientServerInfo cbit.vcell.client.server.ClientServerInfo
 */
public void connectToServer(final ClientServerInfo clientServerInfo) {
	// asynch & nothing to do on Swing queue (updates handled by events)
	Thread worker = new Thread(new Runnable() {
		public void run() {
			getClientServerManager().connect(clientServerInfo);
		}
	});
	worker.start();
}


/**
 * Insert the method's description here.
 * Creation date: (5/10/2004 3:48:16 PM)
 */
VCDocument createDefaultDocument(int docType) {
	if (docType != VCDocument.BIOMODEL_DOC && docType != VCDocument.MATHMODEL_DOC) {
		throw new RuntimeException("default document can only be BioModel or MathModel");
	}
	VCDocument defaultDocument = null;
	try {
		defaultDocument = createNewDocument(docType, 0,null);
	} catch (Exception e) {
		// ignore, does not happen on defaults
	}
	return defaultDocument;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 12:22:11 PM)
 * @param windowID java.lang.String
 */
private MathModel createMathModel(String name, Geometry geometry) {
	MathModel mathModel = new MathModel(null);
	MathDescription mathDesc = mathModel.getMathDescription();
	try {
		mathDesc.setGeometry(geometry);
		if (geometry.getDimension()==0){
			mathDesc.addSubDomain(new cbit.vcell.math.CompartmentSubDomain("Compartment",CompartmentSubDomain.NON_SPATIAL_PRIORITY));
		}else{
			try {
				if (geometry.getDimension()>0 && geometry.getGeometrySurfaceDescription().getGeometricRegions() == null){
					geometry.getGeometrySurfaceDescription().updateAll();
				}
			}catch (cbit.image.ImageException e){
				e.printStackTrace(System.out);
				throw new RuntimeException("Geometric surface generation error: \n"+e.getMessage());
			}catch (cbit.vcell.geometry.GeometryException e){
				e.printStackTrace(System.out);
				throw new RuntimeException("Geometric surface generation error: \n"+e.getMessage());
			}

			SubVolume subVolumes[] = geometry.getGeometrySpec().getSubVolumes();
			for (int i=0;i<subVolumes.length;i++){
				mathDesc.addSubDomain(new cbit.vcell.math.CompartmentSubDomain(subVolumes[i].getName(),subVolumes[i].getHandle()));
			}
			//
			// add only those MembraneSubDomains corresponding to surfaces that acutally exist in geometry.
			//
			GeometricRegion regions[] = geometry.getGeometrySurfaceDescription().getGeometricRegions();
			for (int i = 0; i < regions.length; i++){
				if (regions[i] instanceof SurfaceGeometricRegion){
					SurfaceGeometricRegion surfaceRegion = (SurfaceGeometricRegion)regions[i];
					CompartmentSubDomain compartment1 = mathDesc.getCompartmentSubDomain(((VolumeGeometricRegion)surfaceRegion.getAdjacentGeometricRegions()[0]).getSubVolume().getName());
					CompartmentSubDomain compartment2 = mathDesc.getCompartmentSubDomain(((VolumeGeometricRegion)surfaceRegion.getAdjacentGeometricRegions()[1]).getSubVolume().getName());
					MembraneSubDomain membraneSubDomain = mathDesc.getMembraneSubDomain(compartment1,compartment2);
					if (membraneSubDomain==null){
						membraneSubDomain = new MembraneSubDomain(compartment1,compartment2);
						mathDesc.addSubDomain(membraneSubDomain);
					}
				}
			}
		}
		mathDesc.isValid();
		mathModel.setName(name);
	} catch (Exception e) {
		e.printStackTrace(System.out);
	}
	return mathModel;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 12:22:11 PM)
 * @param windowID java.lang.String
 */
public void createMathModelFromApplication(final String name, final SimulationContext simContext) {
	if (simContext == null) {
		PopupGenerator.showErrorDialog("Selected Application is null, cannot generate corresponding math model");
		return;
	}
		
	SwingWorker worker = new SwingWorker() {
		private AsynchProgressPopup pp = new AsynchProgressPopup(null, "Creating new document", "", false, false);
		private Throwable exc = null;
		private DocumentWindowManager windowManager = null;
		public Object construct() {
			pp.start();
			MathModel newMathModel = new MathModel(null);
			try {
				//Get corresponding mathDesc to create new mathModel.
				MathDescription mathDesc = simContext.getMathDescription();
				MathDescription newMathDesc = null;
				newMathDesc = new MathDescription(name+"_"+(new java.util.Random()).nextInt());
				try {
					if (mathDesc.getGeometry().getDimension()>0 && mathDesc.getGeometry().getGeometrySurfaceDescription().getGeometricRegions() == null){
						mathDesc.getGeometry().getGeometrySurfaceDescription().updateAll();
					}
				}catch (cbit.image.ImageException e){
					e.printStackTrace(System.out);
					throw new RuntimeException("Geometric surface generation error:\n"+e.getMessage());
				}catch (cbit.vcell.geometry.GeometryException e){
					e.printStackTrace(System.out);
					throw new RuntimeException("Geometric surface generation error:\n"+e.getMessage());
				}
				newMathDesc.setGeometry(mathDesc.getGeometry());
				newMathDesc.read_database(new CommentStringTokenizer(mathDesc.getVCML_database()));
				newMathDesc.isValid();

				newMathModel.setName(name);
				newMathModel.setMathDescription(newMathDesc);
				
				windowManager = new MathModelWindowManager(new JPanel(), ClientRequestManager.this, newMathModel, getMdiManager().getNewlyCreatedDesktops());
			} catch (Throwable e) {
				exc = e;
			}
			return newMathModel;
		}
		public void finished() {
			if (exc != null) {
				pp.stop();
				if (exc instanceof UserCancelException) {
					System.out.println(exc);
				} else {
					exc.printStackTrace(System.out);
					PopupGenerator.showErrorDialog("Creating new document failed:\n\n" + exc.getMessage());
				}
			} else {
				getMdiManager().createNewDocumentWindow(windowManager);
				pp.stop();
			}
		}
	};
	worker.start();
}


/**
 * Insert the method's description here.
 * Creation date: (5/10/2004 3:48:16 PM)
 */
private VCDocument createNewDocument(int docType, int option,AsynchProgressPopup pp) throws UserCancelException, DataAccessException, java.io.FileNotFoundException, java.io.IOException, ImageException {
	switch (docType) {
		case VCDocument.BIOMODEL_DOC: {
			// blank
			BioModel bioModel = new BioModel(null);
			try {
				bioModel.setName("BioModel" + (getMdiManager().getNewlyCreatedDesktops() + 1));
				bioModel.getModel().addFeature("Unnamed compartment", null, "Unnamed membrane");
			} catch (Exception exc) {
				System.out.println("This exception should not happen - brand new BioModel!");
				exc.printStackTrace(System.out);
			}
			return bioModel;
		}
		case VCDocument.MATHMODEL_DOC: {
			if ((option == 0) || (option == 1)) {
				// spatial or non-spatial
				Geometry geometry = getMathModelGeometry(option);
				MathModel mathModel = createMathModel("Untitled", geometry);
				try {
					mathModel.setName("MathModel" + (getMdiManager().getNewlyCreatedDesktops() + 1));
				} catch (Exception exc) {
					System.out.println("This exception should not happen - brand new MathModel!");
					exc.printStackTrace(System.out);
				}
				return mathModel;
			} else {
				// from BioModel MathDescription  
				MathModel mathFromBioModel = getMdiManager().getDatabaseWindowManager().selectMathFromBio();
				return mathFromBioModel;
			}
		}
		case VCDocument.GEOMETRY_DOC: {
			Geometry geometry = null;
			if (option < 4) {
				// analytic
				try {
					geometry = new Geometry("Geometry" + (getMdiManager().getNewlyCreatedDesktops() + 1), option);
					geometry.getGeometrySpec().addSubVolume(new AnalyticSubVolume("subVolume1",new cbit.vcell.parser.Expression(1.0)));					
				} catch (Exception exc) {
					System.out.println("This exception should not happen - brand new Geometry!");
					exc.printStackTrace(System.out);
				}
				return geometry;
			} else  {
				// image-based
				VCImage image = null;
				if (option == 4) {
					// Get image from database
					image = getMdiManager().getDatabaseWindowManager().selectImageFromDatabase();
				} else {
					// Get image from file --- INCOMPLETE
					image = getMdiManager().getDatabaseWindowManager().selectImageFromFile(pp);
				}
				if (image == null){
					throw new RuntimeException("failed to create new Geometry, no image");
				}
				return new Geometry("Untitled", image);
			}
		}
		default:
			throw new RuntimeException("Unknown default document type: " + docType);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 10:50:34 PM)
 * @param documentInfo cbit.vcell.document.VCDocumentInfo
 */
public void curateDocument(final VCDocumentInfo documentInfo, final int curateType, final TopLevelWindowManager requester) {

	if (documentInfo != null) {
		// see if we have this open
		String documentID = documentInfo.getVersion().getVersionKey().toString();
		if (getMdiManager().haveWindow(documentID)) {
			// already open, refuse
			PopupGenerator.showErrorDialog(requester, "Selected edition is open, cannot "+CurateSpec.CURATE_TYPE_NAMES[curateType]);
			return;
		} else {
			// don't have it open, try to CURATE it
			int confirm = PopupGenerator.showComponentOKCancelDialog(
				requester.getComponent(),
				new JTextArea(CurateSpec.CURATE_TYPE_ACTIONS[curateType]+" cannot be undone without VCELL administrative assistance.\n"+
					CurateSpec.CURATE_TYPE_STATES[curateType]+" versions of documents cannot be deleted without VCELL administrative assistance.\n"+
					(curateType == CurateSpec.PUBLISH?CurateSpec.CURATE_TYPE_STATES[curateType]+" versions of documents MUST remain publically accessible to other VCELL users.\n":"")+
					"Do you want to "+CurateSpec.CURATE_TYPE_NAMES[curateType]+" document '"+documentInfo.getVersion().getName()+"'"+
					"\nwith version date '"+documentInfo.getVersion().getDate().toString()+"'?")
				,"WARNING -- "+CurateSpec.CURATE_TYPE_ACTIONS[curateType]+" operation cannot be undone");
			if (confirm == JOptionPane.OK_OPTION){
				SwingWorker worker = new SwingWorker() {
					AsynchProgressPopup pp = new AsynchProgressPopup(requester.getComponent(), "WORKING", CurateSpec.CURATE_TYPE_ACTIONS[curateType]+" document...", false, false);
					Throwable exc = null;
					public Object construct() {
						pp.start();
						try {
							if (documentInfo instanceof BioModelInfo) {
								getDocumentManager().curate(new CurateSpec((BioModelInfo)documentInfo,curateType));
							} else if (documentInfo instanceof MathModelInfo) {
								getDocumentManager().curate(new CurateSpec((MathModelInfo)documentInfo,curateType));
							} else {
								throw new RuntimeException(CurateSpec.CURATE_TYPE_ACTIONS[curateType]+" not supported for VCDocumentInfo type "+documentInfo.getClass().getName());
							}
						} catch (Throwable e) {
							exc = e;
						}
						return null;
					}
					public void finished() {
						if (exc != null) {
							exc.printStackTrace(System.out);
							PopupGenerator.showErrorDialog(requester, CurateSpec.CURATE_TYPE_ACTIONS[curateType]+" failed\n"+exc);
						}
						pp.stop();
					}
				};
				worker.start();
			} else {
				// user canceled
				return;
			}
		}
	} else {
		// nothing selected
		return;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 10:50:34 PM)
 * @param documentInfo cbit.vcell.document.VCDocumentInfo
 */
public void deleteDocument(final VCDocumentInfo documentInfo, final TopLevelWindowManager requester) {
	if (documentInfo != null) {
		// see if we have this open
		String documentID = documentInfo.getVersion().getVersionKey().toString();
		if (getMdiManager().haveWindow(documentID)) {
			// already open, refuse
			PopupGenerator.showErrorDialog(requester, "Selected edition is open, cannot delete");
			return;
		} else {
			// don't have it open, try to delete it
			String confirm = PopupGenerator.showWarningDialog(requester, getUserPreferences(), UserMessage.warn_deleteDocument,documentInfo.getVersion().getName());
			if (confirm.equals(UserMessage.OPTION_DELETE)){
				SwingWorker worker = new SwingWorker() {
					AsynchProgressPopup pp = new AsynchProgressPopup(requester.getComponent(), "WORKING", "Deleting document...", false, false);
					Throwable exc = null;
					public Object construct() {
						pp.start();
						try {
							if (documentInfo instanceof BioModelInfo) {
								getDocumentManager().delete((BioModelInfo)documentInfo);
							} else if (documentInfo instanceof MathModelInfo) {
								getDocumentManager().delete((MathModelInfo)documentInfo);
							} else if (documentInfo instanceof GeometryInfo) {
								getDocumentManager().delete((GeometryInfo)documentInfo);
							} else {
								throw new RuntimeException("delete not supported for VCDocumentInfo type "+documentInfo.getClass().getName());
							}
						} catch (Throwable e) {
							exc = e;
						}
						return null;
					}
					public void finished() {
						if (exc != null) {
							exc.printStackTrace(System.out);
							PopupGenerator.showErrorDialog(requester, "Delete failed\n"+exc);
						}
						pp.stop();
					}
				};
				worker.start();
			} else {
				// user canceled
				return;
			}
		}
	} else {
		// nothing selected
		return;
	}
}


/**
 * Comment
 */
protected void downloadExportedData(final cbit.rmi.event.ExportEvent evt) {
	java.net.URL location = null;
	try {
		location = new java.net.URL(evt.getLocation());
	} catch (java.net.MalformedURLException exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog((Component)null, "Reported file location does not seem to be a valid URL\n"+exc.getMessage());
		return;
	}
	final java.net.URL url = location;
	// now download it
	SwingWorker worker = new SwingWorker() {
		AsynchProgressPopup pp = new AsynchProgressPopup((Component)null, "Downloading exported dataset", "Retrieving data from "+url, false, false);
		public Object construct() {
			pp.start();
			String defaultPath = getUserPreferences().getGenPref(UserPreferences.GENERAL_LAST_PATH_USED);
			final cbit.gui.VCFileChooser fileChooser = new cbit.gui.VCFileChooser(defaultPath);
			try {
				// get it
			    java.net.URLConnection connection = url.openConnection();
			    byte[] bytes = new byte[connection.getContentLength()];
			    java.io.InputStream is = connection.getInputStream();
			    int bytesRead = 0;
			    int offset = 0;
			    while (bytesRead >= 0 && offset<(bytes.length-1)) {
			        System.out.println("offset: " + offset + ", bytesRead: " + bytesRead);
			        bytesRead = is.read(bytes, offset, bytes.length - offset);
			        offset += bytesRead;
			    }
			    is.close();
			    // prepare chooser
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setMultiSelectionEnabled(false);
				fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_ZIP);
				fileChooser.setFileFilter(FileFilters.FILE_FILTER_ZIP);
			    String name = evt.getVCSimulationIdentifier().getID();
			    //if (evt.getVCDataIdentifier() instanceof cbit.vcell.solver.SimulationInfo){
				    //cbit.vcell.solver.SimulationInfo simInfo = (cbit.vcell.solver.SimulationInfo)evt.getVCDataIdentifier();
				    //name = simInfo.getName();
			    //}
			    String suffix = "_exported.zip";
			    java.io.File file = new java.io.File(name + suffix);
			    if (file.exists()) {
				    int count = 0;
				    do {
				    	file = new java.io.File(name + "_" + count + suffix);
				    } while (file.exists());
			    }
			    fileChooser.setSelectedFile(file);
				fileChooser.setDialogTitle("Save exported dataset...");
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						int approve = fileChooser.showSaveDialog((Component)null);
						if (approve != JFileChooser.APPROVE_OPTION) {
							fileChooser.setSelectedFile(null);
							return;
						}
					}
				});
				java.io.File selectedFile = fileChooser.getSelectedFile();
				if (selectedFile != null) {
			        String newPath = selectedFile.getParent();
			        if (!newPath.equals(defaultPath)) {
						getUserPreferences().setGenPref(UserPreferences.GENERAL_LAST_PATH_USED, newPath);
			        }
			        System.out.println("New preferred file path: " + newPath + ", Old preferred file path: " + defaultPath);
					//
					if (selectedFile.exists()) {
						String question = PopupGenerator.showWarningDialog((Component)null, getUserPreferences(), UserMessage.warn_OverwriteFile,selectedFile.getAbsolutePath());
						if (question.equals(UserMessage.OPTION_CANCEL)){
							return null;
						}
					}
		            java.io.FileOutputStream fo = new java.io.FileOutputStream(selectedFile);
		            fo.write(bytes);
		            fo.close();
				}
			} catch (Throwable exc) {
				return exc;
			}
			return null;
		}
		public void finished() {
			pp.stop();
			if (get() != null) {
				Throwable e = (Throwable)get();
				e.printStackTrace(System.out);
				cbit.gui.DialogUtils.showErrorDialog("Downloading failed\n"+e.getMessage());
			}
		}
	};
	worker.start();
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:20:47 AM)
 */
public void exitApplication() {
	if (!bExiting) {
		// close all windows - this will run checks
		boolean closedAllWindows = closeAllWindows(true);
		if (! closedAllWindows) {
			// user bailed out at some point, we're not gonna exit
			return;
		}
	}
	// ready to exit
	if (getVcellClient().isApplet()) {
		// can't just exit, since it will take down other applets and possibly the browser, try cleanup
		// if all end up being closed, we should take care of threads and object references so that the application exits
		
		/* more work needed here... */
		
		((ClientMDIManager)getMdiManager()).cleanup();
		getClientServerManager().cleanup();
		getVcellClient().getStatusUpdater().stop();
		System.gc();
	} else {
		// simply exit in this case
		System.exit(0);
	}
}


/**
 * Comment
 */
public void exportDocument(TopLevelWindowManager manager) {
	/* block window */
	JFrame currentWindow = getMdiManager().blockWindow(manager.getManagerID());
	/* prepare hashtable for tasks */
	Hashtable hash = new Hashtable();
	hash.put("mdiManager", getMdiManager());
	hash.put("documentManager", getDocumentManager());
	hash.put("topLevelWindowManager", manager);
	hash.put("currentWindow", currentWindow);
	hash.put("userPreferences", getUserPreferences());
	/* create tasks */
	// get document to be exported
	AsynchClientTask documentToExport = new DocumentToExport();
	// get file
	AsynchClientTask chooseFile = new ChooseFile();
	// export it
	AsynchClientTask exportDocument = new ExportToXML();
	// clean-up
	AsynchClientTask finishExport = new FinishExport();
	// assemble array
	AsynchClientTask[] tasks = null;
	tasks = new AsynchClientTask[] {
		documentToExport,
		chooseFile,
		exportDocument,
		finishExport
	};
	/* run tasks */
	ClientTaskDispatcher.dispatch(currentWindow, hash, tasks, false);
}


/**
 * Insert the method's description here.
 * Creation date: (1/18/2005 3:14:12 PM)
 * @param event cbit.rmi.event.ExportEvent
 */
public void exportMessage(cbit.rmi.event.ExportEvent event) {
	if (event.getEventTypeID() == cbit.rmi.event.ExportEvent.EXPORT_COMPLETE) {
		// update document manager
		//try {
			//((ClientDocumentManager)getRequestManager().getDocumentManager()).reloadExportLog(exportEvent.getVCDataIdentifier());
		//}catch (Throwable e){
			//e.printStackTrace(System.out);
		//}
		// try to download the thing
		downloadExportedData(event);
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 4:45:51 PM)
 * @return cbit.vcell.client.AsynchMessageManager
 */
public cbit.vcell.client.server.AsynchMessageManager getAsynchMessageManager() {
	return getClientServerManager().getAsynchMessageManager();
}


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 4:45:51 PM)
 * @return cbit.vcell.client.AsynchMessageManager
 */
public cbit.vcell.server.bionetgen.BNGService getBNGService() {
	BNGService bngService = null;
	try {
		bngService = getClientServerManager().getBNGSerivce();
	} catch (DataAccessException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Could not launch BNGService : " + e.getMessage());
	}
	return bngService;
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:21:50 AM)
 */
private ClientServerManager getClientServerManager() {
	// shorthand
	return getVcellClient().getClientServerManager();
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 10:53:47 AM)
 * @return cbit.vcell.desktop.controls.DataManager
 * @param vcDataIdentifier cbit.vcell.server.VCDataIdentifier
 */
public DataManager getDataManager(VCDataIdentifier vcDataId, boolean isSpatial) throws DataAccessException {
	//
	// Create ODE or PDE or Merged Datamanager depending on ODE or PDE or Merged data.
	//
	if (vcDataId instanceof cbit.vcell.simdata.MergedDataInfo) {
		return new MergedDataManager(getClientServerManager().getVCDataManager(), vcDataId);
	} else if (!isSpatial) {
		return new ODEDataManager(getClientServerManager().getVCDataManager(), vcDataId);
	} else {
		return new PDEDataManager(getClientServerManager().getVCDataManager(), vcDataId);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 9:57:39 AM)
 * @return cbit.vcell.clientdb.DocumentManager
 */
public cbit.vcell.clientdb.DocumentManager getDocumentManager() {
	// this should not be exposed here, but needs many changes outside project in order to live without it...
	// will eliminate when finishing up new client
	return getVcellClient().getClientServerManager().getDocumentManager();
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 10:53:47 AM)
 * @return cbit.vcell.desktop.controls.DataManager
 * @param vcDataIdentifier cbit.vcell.server.VCDataIdentifier
 */
public DynamicDataManager getDynamicDataManager(VCDataIdentifier vcdId) throws DataAccessException {
	if (vcdId instanceof cbit.vcell.simdata.MergedDataInfo) {
		return new MergedDynamicDataManager(getClientServerManager().getVCDataManager(), vcdId);
	} else {
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 10:53:47 AM)
 * @return cbit.vcell.desktop.controls.DataManager
 * @param vcDataIdentifier cbit.vcell.server.VCDataIdentifier
 */
public DynamicDataManager getDynamicDataManager(Simulation simulation) throws DataAccessException {
	return new SimulationDataManager(getClientServerManager().getVCDataManager(), simulation);
}


	//utility method
	private VCDocumentInfo getMatchingDocumentInfo(VCDocument vcDoc) throws DataAccessException {

		VCDocumentInfo vcDocInfo = null;
		
		switch (vcDoc.getDocumentType()) {
			case VCDocument.BIOMODEL_DOC: {
				BioModel bm = ((BioModel)vcDoc);
				vcDocInfo = getDocumentManager().getBioModelInfo(bm.getVersion().getVersionKey());
				break;			 
			}
			case VCDocument.MATHMODEL_DOC: {
				MathModel mm = ((MathModel)vcDoc);
				vcDocInfo = getDocumentManager().getMathModelInfo(mm.getVersion().getVersionKey());
				break;			
			}
			case VCDocument.GEOMETRY_DOC: {
				Geometry geom = ((Geometry)vcDoc);
				vcDocInfo = getDocumentManager().getGeometryInfo(geom.getKey());
				break;			
			}
			default: {
				throw new IllegalArgumentException("Invalid VC document: " + vcDoc.getDocumentType());
			}
		}

		return vcDocInfo;
	}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:21:50 AM)
 */
private Geometry getMathModelGeometry(int option) throws DataAccessException, UserCancelException, java.io.FileNotFoundException, java.io.IOException {

	if (option == 0) {
		// non-spatial geometry
		return new Geometry("Untitled", 0);
	} else {
		// spatial
		VCDocumentInfo vcDocInfo = getMdiManager().getDatabaseWindowManager().selectDocument(VCDocument.GEOMETRY_DOC, getMdiManager().getDatabaseWindowManager());
		return (Geometry)getDocumentManager().getGeometry((GeometryInfo)vcDocInfo);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:21:50 AM)
 */
private MDIManager getMdiManager() {
	// shorthand
	return getVcellClient().getMdiManager();
}


/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 1:27:25 PM)
 * @return cbit.vcell.solver.ode.gui.SimulationStatus
 * @param simulation cbit.vcell.solver.Simulation
 */
public SimulationStatus getServerSimulationStatus(SimulationInfo simInfo) {
	
	SimulationStatus simStatus = null;
	try {
		VCSimulationIdentifier vcSimulationIdentifier = simInfo.getAuthoritativeVCSimulationIdentifier();
		simStatus = getClientServerManager().getJobManager().getServerSimulationStatus(vcSimulationIdentifier);
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
	}
	return simStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:21:50 AM)
 */
public UserPreferences getUserPreferences() {
	return getVcellClient().getClientServerManager().getUserPreferences();
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:21:50 AM)
 * @return cbit.vcell.client.VCellClient
 */
private VCellClient getVcellClient() {
	return vcellClient;
}


/**
 * Insert the method's description here.
 * Creation date: (8/26/2005 3:14:24 PM)
 * @return boolean
 */
public boolean isApplet() {
	return getVcellClient().isApplet();
}


/**
 * Insert the method's description here.
 * Creation date: (6/30/2004 12:30:51 PM)
 * @param documentInfo cbit.vcell.document.VCDocumentInfo
 */
public void managerIDchanged(java.lang.String oldID, java.lang.String newID) {
	if (oldID != null) {
		getMdiManager().updateDocumentID(oldID, newID);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:20:47 AM)
 * @param documentType int
 */
public void newDocument(final int documentType, final int option) {
	/* asynchronous and not blocking any window */
		
	// start a thread that makes it and updates the GUI by creating a new document desktop
	SwingWorker worker = new SwingWorker() {
		private VCDocument doc = null;
		private AsynchProgressPopup pp = new AsynchProgressPopup(null, "Creating new document", "", false, false);
		private Throwable exc = null;
		private DocumentWindowManager windowManager = null;
		public Object construct() {
			pp.start();
			try {
				VCDocument doc = createNewDocument(documentType, option,pp);
				switch (doc.getDocumentType()) {
					case VCDocument.BIOMODEL_DOC: {
						windowManager = new BioModelWindowManager(new JPanel(), ClientRequestManager.this, (BioModel)doc, getMdiManager().getNewlyCreatedDesktops());
						break;
					}
					case VCDocument.MATHMODEL_DOC: {
						windowManager = new MathModelWindowManager(new JPanel(), ClientRequestManager.this, (MathModel)doc, getMdiManager().getNewlyCreatedDesktops());
						break;
					}
					case VCDocument.GEOMETRY_DOC: {
						windowManager = new GeometryWindowManager(new JPanel(), ClientRequestManager.this, (Geometry)doc, getMdiManager().getNewlyCreatedDesktops());
						break;
					}
				}
			} catch (Throwable e) {
				exc = e;
			}
			return doc;
		}
		public void finished() {
			if (exc != null) {
				pp.stop();
				if (exc instanceof UserCancelException) {
					System.out.println(exc);
				} else {
					exc.printStackTrace(System.out);
					PopupGenerator.showErrorDialog("Creating new document failed:\n\n" + exc.getMessage());
				}
			} else {
				getMdiManager().createNewDocumentWindow(windowManager);
				pp.stop();
			}
		}
	};
	worker.start();
}


/**
 * onVCellMessageEvent method comment.
 */
public void onVCellMessageEvent(final cbit.rmi.event.VCellMessageEvent event) {
	if (event.getEventTypeID() == cbit.rmi.event.VCellMessageEvent.VCELL_MESSAGEEVENT_TYPE_BROADCAST) {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
			    PopupGenerator.showErrorDialog(event.getMessageData().getData().toString());
		    }
		});
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 9:37:46 PM)
 */
private void openAfterChecking(final VCDocumentInfo documentInfo, final TopLevelWindowManager requester, final boolean inNewWindow) {

	/* asynchronous and not blocking any window */
		
	// start a thread that gets it and updates the GUI by creating a new document desktop
	SwingWorker worker = new SwingWorker() {
		private VCDocument doc = null;
		private AsynchProgressPopup pp = new AsynchProgressPopup(requester.getComponent(), "Loading from "+(documentInfo instanceof XMLInfo?"XML":"database"), "Document: '" + documentInfo.getVersion().getName() + "'", false, false);
		private Throwable exc = null;
		private DocumentWindowManager windowManager = null;
		public Object construct() {
			pp.start();
			if (! inNewWindow) {
				// request was to replace the document in an existing window
				getMdiManager().blockWindow(requester.getManagerID());
			}
			if (documentInfo instanceof BioModelInfo) {
				BioModelInfo bmi = (BioModelInfo)documentInfo;
				try {
					doc = getDocumentManager().getBioModel(bmi);
					if (inNewWindow) {
						windowManager = new BioModelWindowManager(new JPanel(), ClientRequestManager.this, (BioModel)doc, getMdiManager().getNewlyCreatedDesktops());
						((BioModelWindowManager)windowManager).preloadApps();
					}
				} catch (Throwable e) {
					exc = e;
				}
			} else if (documentInfo instanceof MathModelInfo) {
				MathModelInfo mmi = (MathModelInfo)documentInfo;
				try {
					doc = getDocumentManager().getMathModel(mmi);
					if (inNewWindow) {
						windowManager = new MathModelWindowManager(new JPanel(), ClientRequestManager.this, (MathModel)doc, getMdiManager().getNewlyCreatedDesktops());
					}
				} catch (Throwable e) {
					exc = e;
				}
			} else if (documentInfo instanceof GeometryInfo) {
				GeometryInfo gmi = (GeometryInfo)documentInfo;
				try {
					doc = getDocumentManager().getGeometry(gmi);
					if (inNewWindow) {
						windowManager = new GeometryWindowManager(new JPanel(), ClientRequestManager.this, (Geometry)doc, getMdiManager().getNewlyCreatedDesktops());
					}
				} catch (Throwable e) {
					exc = e;
				}
			} else if (documentInfo instanceof XMLInfo) {
				String xmlStr = ((XMLInfo)documentInfo).getXmlString();
				try {
					org.jdom.Element rootElement = cbit.util.xml.XmlUtil.stringToXML(xmlStr, null);         //some overhead.
					String xmlType = rootElement.getName();
					if (xmlType.equals(XMLTags.BioModelTag)) {
						doc = XmlHelper.XMLToBioModel(xmlStr);
						windowManager = new BioModelWindowManager(new JPanel(), ClientRequestManager.this, (BioModel)doc, getMdiManager().getNewlyCreatedDesktops());
						((BioModelWindowManager)windowManager).preloadApps();
					} else if (xmlType.equals(XMLTags.MathModelTag)) {
						doc = XmlHelper.XMLToMathModel(xmlStr);
						MathModel mathModel = (MathModel)doc;
						Geometry geometry = mathModel.getMathDescription().getGeometry();
						if (geometry.getDimension()>0 && geometry.getGeometrySurfaceDescription().getGeometricRegions()==null){
							geometry.getGeometrySurfaceDescription().updateAll();
						}
						windowManager = new MathModelWindowManager(new JPanel(), ClientRequestManager.this, (MathModel)doc, getMdiManager().getNewlyCreatedDesktops());
					} else if (xmlType.equals(XMLTags.GeometryTag)) {
						doc = XmlHelper.XMLToGeometry(xmlStr);
						Geometry geometry = (Geometry)doc;
						if (geometry.getDimension()>0){
							geometry.getGeometrySurfaceDescription().updateAll();
						}
						windowManager = new GeometryWindowManager(new JPanel(), ClientRequestManager.this, (Geometry)doc, getMdiManager().getNewlyCreatedDesktops());
					} else if (xmlType.equals(XMLTags.SbmlRootNodeTag)) {
						cbit.vcell.xml.XmlDialect fromDialect = cbit.vcell.xml.XmlDialect.getTargetDialect(rootElement.getName(), rootElement.getNamespaceURI(), XMLTags.BioModelTag);
						TranslationLogger transLogger = new TranslationLogger(requester);
						doc = XmlHelper.importXMLVerbose(transLogger, xmlStr, fromDialect);
						windowManager = new BioModelWindowManager(new JPanel(), ClientRequestManager.this, (BioModel)doc, getMdiManager().getNewlyCreatedDesktops());
					} else if (xmlType.equals(XMLTags.CellmlRootNodeTag)) {
						if (requester instanceof BioModelWindowManager){
							cbit.vcell.xml.XmlDialect fromDialect = cbit.vcell.xml.XmlDialect.getTargetDialect(rootElement.getName(), rootElement.getNamespaceURI(), XMLTags.BioModelTag);
							TranslationLogger transLogger = new TranslationLogger(requester);
							doc = XmlHelper.importXMLVerbose(transLogger, xmlStr, fromDialect);
							windowManager = new BioModelWindowManager(new JPanel(), ClientRequestManager.this, (BioModel)doc, getMdiManager().getNewlyCreatedDesktops());
						}else{
							cbit.vcell.xml.XmlDialect fromDialect = cbit.vcell.xml.XmlDialect.getTargetDialect(rootElement.getName(), rootElement.getNamespaceURI(), XMLTags.MathModelTag);
							TranslationLogger transLogger = new TranslationLogger(requester);
							doc = XmlHelper.importXMLVerbose(transLogger, xmlStr, fromDialect);
							windowManager = new MathModelWindowManager(new JPanel(), ClientRequestManager.this, (MathModel)doc, getMdiManager().getNewlyCreatedDesktops());
						}
					} else { // unknown XML format
						throw new RuntimeException("unsupported XML format, first element tag is <"+rootElement.getName()+">");
					}
				} catch (Throwable e) {
					exc = e;
				}
			}
			return doc;
		}
		public void finished() {

			try {
				if (exc != null) {
					exc.printStackTrace(System.out);
					if (!(exc instanceof UserCancelException)) {                      //allow 
						PopupGenerator.showErrorDialog(requester, "Loading document failed:\n\n" + exc.getMessage());
					}
					if (!inNewWindow) {
						getMdiManager().unBlockWindow(requester.getManagerID());
					}
				} else {
					if (inNewWindow) {
						// request was to create a new top-level window with this doc
						getMdiManager().createNewDocumentWindow(windowManager);
					} else {
						// request was to replace the document in an existing window
						((DocumentWindowManager)requester).resetDocument(doc);
						getMdiManager().setCanonicalTitle(requester.getManagerID());
						getMdiManager().unBlockWindow(requester.getManagerID());
					}
				}
			} finally {
				pp.stop();
				bOpening = false;
			}
		}
	};
	bOpening = true;
	worker.start();
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 8:53:05 PM)
 * @param documentInfo cbit.vcell.document.VCDocumentInfo
 */
public void openDocument(int documentType, DocumentWindowManager requester) {
	/* trying to open from database; called by DocumentWindow */
	// get an info first
	VCDocumentInfo documentInfo = null;
	try {
		documentInfo = getMdiManager().getDatabaseWindowManager().selectDocument(documentType, requester);
		// check whether request comes from a blank, unchanged document window; if so, open in same window, otherwise in a new window
		boolean inNewWindow = checkDifferentFromBlank(documentType, requester.getVCDocument());
		openDocument(documentInfo, requester, inNewWindow);
	} catch (UserCancelException uexc) {
		System.out.println(uexc);
		return;
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(requester, "Open document failed\n"+exc);
	}
}

	
	/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 8:53:05 PM)
 * @param documentInfo cbit.vcell.document.VCDocumentInfo
 */
public void openDocument(VCDocumentInfo documentInfo, TopLevelWindowManager requester, boolean inNewWindow) {
	// called directly from DatabaseWindow or after invoking an open dialog (see openDocument(int, Component))
	// need to check whether we opened this before and we still have it open
	if (documentInfo != null) {
		String documentID = null;
		if (documentInfo.getVersion()!=null && documentInfo.getVersion().getVersionKey()!=null){ // CLEAN UP BOGUS VERSION in XmlInfo !!!
			documentID = documentInfo.getVersion().getVersionKey().toString();
		}
	
		// see if we have this open
		if (documentID != null && getMdiManager().haveWindow(documentID)) {
			// already open, block it
			getMdiManager().blockWindow(documentID);
			// check for changes
			VCDocument openedDoc = ((DocumentWindowManager)getMdiManager().getWindowManager(documentID)).getVCDocument();
			boolean isChanged = true;
			try {
				isChanged = getDocumentManager().isChanged(openedDoc);
			} catch (DataAccessException exc) {
				// *maybe* something wrong trying to go to database, may not be able to load in the end, but warn anyway and try
				String choice = PopupGenerator.showWarningDialog(requester, getUserPreferences(), UserMessage.warn_UnableToCheckForChanges,null);
				if (choice.equals(UserMessage.OPTION_CANCEL)){
					// user canceled, just show existing document
					getMdiManager().unBlockWindow(documentID);
					getMdiManager().showWindow(documentID);
					return;
				}
			}
			// we managed to check
			if (isChanged) {
				// it changed, warn the user
				String choice = PopupGenerator.showWarningDialog(requester, getUserPreferences(), UserMessage.choice_AlreadyOpened,null);
				if (choice.equals(UserMessage.OPTION_CANCEL)){
					// user canceled, just show existing document
					getMdiManager().unBlockWindow(documentID);
					getMdiManager().showWindow(documentID);
					return;
				} else {
					// user confirmed, close existing window first
					getMdiManager().closeWindow(documentID);
					// we are ready to try to get the new document
				}
			} else {
				// nothing changed, just show that window
				getMdiManager().unBlockWindow(documentID);
				getMdiManager().showWindow(documentID);
				return;
			}
		} else {
			// don't have it open, try to get it
		}
		openAfterChecking(documentInfo, requester, inNewWindow);
	} else {
		// nothing selected
		return;
	}
}


	public void processComparisonResult(TMLPanel comparePanel, TopLevelWindowManager requester) {
		// this is called from the EventDispatchQueue, so take care with threading...

		if (comparePanel == null || requester == null) {
			throw new IllegalArgumentException("Invalid params: " + comparePanel + " " + requester);
		}
		try {
			final VCDocument vcDoc = comparePanel.processComparisonResult();
			if (requester instanceof DatabaseWindowManager) {
				final DatabaseWindowManager dataWinManager = (DatabaseWindowManager)requester;
				final VCDocumentInfo vcDocInfo = getMatchingDocumentInfo(vcDoc);
				this.openDocument(vcDocInfo, dataWinManager, true);
				Thread waiter = new Thread() {
					public void run() {
						try {
							BeanUtils.setCursorThroughout((Container)dataWinManager.getComponent(), Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							while (bOpening){
								try { 
									Thread.sleep(100);
								} catch (InterruptedException e) {}	
							}
							String ID = vcDocInfo.getVersion().getVersionKey().toString();
							DocumentWindowManager dwm = (DocumentWindowManager)ClientRequestManager.this.getMdiManager().getWindowManager(ID);
							dwm.resetDocument(vcDoc);
						}finally{
							BeanUtils.setCursorThroughout((Container)dataWinManager.getComponent(), Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}
					}
				};
				waiter.start();
			} else if (requester instanceof DocumentWindowManager) {
				DocumentWindowManager docWinManager = (DocumentWindowManager)requester;
				docWinManager.resetDocument(vcDoc);
			} else {
				throw new IllegalArgumentException("Invalid TopLevelWindowManager instance: " + requester.getClass().getName());
			}
			System.out.println("Processing new model ..." + vcDoc.getVersion().getName());
		} catch (Exception e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(final PropertyChangeEvent evt) {
	if (evt.getSource() == getVcellClient().getClientServerManager() && evt.getPropertyName().equals("connectionStatus")) {
		// update status display
		updateStatusNow(); // this is already thread-safe
		// other updates
		AsynchGuiUpdater updater = new AsynchGuiUpdater() {
			public void guiToDo() {}
			public void guiToDo(Object params) {
				// so far just update the DatabaseWindow
				// need logic to deal with disconnection, different credentials etc.

				// only when NOT initializing
				if (((ConnectionStatus)evt.getNewValue()).getStatus() != ConnectionStatus.INITIALIZING) {
					getMdiManager().getDatabaseWindowManager().initializeAll();
					if (getMdiManager().getTestingFrameworkWindowManager() != null) {
						getMdiManager().getTestingFrameworkWindowManager().initializeAllPanels();
					}
				}
			}
		};
		updater.updateNow("some arg needed...");
	}
}	


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 2:18:16 AM)
 */
public void reconnect() {
	// asynch & nothing to do on Swing queue (updates handled by events)
	Thread worker = new Thread(new Runnable() {
		public void run() {
			getClientServerManager().reconnect();
		}
	});
	worker.start();
}


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 1:07:09 PM)
 * @param vcDocument cbit.vcell.document.VCDocument
 */
public void revertToSaved(DocumentWindowManager documentWindowManager) {
	getMdiManager().blockWindow(documentWindowManager.getManagerID());
	// make the info
	VCDocument document = documentWindowManager.getVCDocument();
	VCDocumentInfo info = null;
	try {
		switch (document.getDocumentType()) {
			case VCDocument.BIOMODEL_DOC: {
				BioModel bioModel = (BioModel)document;
				info = getDocumentManager().getBioModelInfo(bioModel.getVersion().getVersionKey());
				break;
			}
			case VCDocument.MATHMODEL_DOC: {
				MathModel mathModel = (MathModel)document;
				info = getDocumentManager().getMathModelInfo(mathModel.getVersion().getVersionKey());
				break;
			}
			case VCDocument.GEOMETRY_DOC: {
				Geometry geometry = (Geometry)document;
				info = getDocumentManager().getGeometryInfo(geometry.getKey());
				break;
			}
		}
	}catch (DataAccessException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
	// reload and reset into same window
	openAfterChecking(info, documentWindowManager, false);
}


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 2:29:35 AM)
 * @param documentWindowManager cbit.vcell.client.DocumentWindowManager
 * @param simulations cbit.vcell.solver.Simulation[]
 */
public void runSimulation(final SimulationInfo simInfo) throws DataAccessException{

	getClientServerManager().
		getJobManager().
			startSimulation(simInfo.getAuthoritativeVCSimulationIdentifier());
}		


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 2:29:35 AM)
 * @param documentWindowManager cbit.vcell.client.DocumentWindowManager
 * @param simulations cbit.vcell.solver.Simulation[]
 */
public void runSimulations(final ClientSimManager clientSimManager, final Simulation[] simulations) {
	DocumentWindowManager documentWindowManager = clientSimManager.getDocumentWindowManager();
	/*	run some quick checks to see if we need to do a SaveAs */
	boolean needSaveAs = false;
	if (documentWindowManager.getVCDocument().getVersion() == null) {
		// never saved
		needSaveAs = true;
	} else if (!documentWindowManager.getVCDocument().getVersion().getOwner().compareEqual(getDocumentManager().getUser())) {
		// not the owner
		// keep the user informed this time
		String choice = PopupGenerator.showWarningDialog(documentWindowManager, getUserPreferences(), UserMessage.warn_SaveNotOwner,null);
		if (choice.equals(UserMessage.OPTION_SAVE_AS_NEW)){
			needSaveAs = true;
		} else {
			// user canceled, just show existing document
			getMdiManager().showWindow(documentWindowManager.getManagerID());
			return;
		}
	}
	//
	// when we run simulations, we want to force these exact editions to be run (not their older "equivalent" simulations).
	//
	// rather than trying to update immutable objects (e.g. directly clear parent references), we can do two things:
	//
	// 1) if simulation already points back to a previous "equivalent" edition, 
	//       clear the version to force a clean save with no equivalency relationship
	// 2) and, to prevent a simulation save from creating a new equivalency relationship, 
	//       send the list of simulations to be run to force independence (see SaveDocument).
	//
	for (int i = 0;simulations!=null && i < simulations.length; i++){
		if (simulations[i].getSimulationVersion() != null && simulations[i].getSimulationVersion().getParentSimulationReference()!=null){
			simulations[i].clearVersion();
		}
	}
	/* now start the dirty work */
	
	/* block document window */
	JFrame currentDocumentWindow = getMdiManager().blockWindow(documentWindowManager.getManagerID());
	/* prepare hashtable for tasks */
	Hashtable hash = new Hashtable();
	hash.put("mdiManager", getMdiManager());
	hash.put("documentManager", getDocumentManager());
	hash.put("documentWindowManager", documentWindowManager);
	hash.put("currentDocumentWindow", currentDocumentWindow);
	hash.put("clientSimManager", clientSimManager);
	hash.put("simulations", simulations);
	hash.put("jobManager", getClientServerManager().getJobManager());
	/* create tasks */
	AsynchClientTask[] tasks = null;
	if (needSaveAs) {
		// check document consistency first
		AsynchClientTask documentValid = new DocumentValid();
		// get a new name
		AsynchClientTask newName = new NewName();
		// save it
		AsynchClientTask saveDocument = new SaveDocument();
		// clean up
		AsynchClientTask finishSave = new FinishSave();
		// run the simulations
		AsynchClientTask runSims = new RunSims();
		// assemble array
		tasks = new AsynchClientTask[] {
			documentValid,
			newName,
			saveDocument,
			finishSave,
			runSims
			};
	} else {
		// check document consistency first
		AsynchClientTask documentValid = new DocumentValid();
		// check if unchanged document
		AsynchClientTask checkUnchanged = new CheckUnchanged(true);
		// save it
		AsynchClientTask saveDocument = new SaveDocument();
		// check for lost results
		AsynchClientTask checkBeforeDelete = new CheckBeforeDelete();
		// delete old document
		AsynchClientTask deleteOldDocument = new DeleteOldDocument();
		// clean up
		AsynchClientTask finishSave = new FinishSave();
		// run the simulations
		AsynchClientTask runSims = new RunSims();
		// assemble array
		tasks = new AsynchClientTask[] {
			documentValid,
			checkUnchanged,
			saveDocument,
			checkBeforeDelete,
			deleteOldDocument,
			finishSave,
			runSims
			};
	}
	/* run the tasks */
	ClientTaskDispatcher.dispatch(currentDocumentWindow, hash, tasks, true);
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 3:09:25 PM)
 * @param vcDocument cbit.vcell.document.VCDocument
 * @param replace boolean
 */
public void saveDocument(DocumentWindowManager documentWindowManager, boolean replace) {
	
	/*	run some quick checks first to validate request to save or save edition */
	
	if (documentWindowManager.getVCDocument().getVersion() == null) {
		// never saved - can't see this happening, but check anyway and default to save as
		// (save/save edition buttons should have not been enabled upon document window creation)
		System.out.println("\nIGNORED ERROR: should not have been able to use save/save edition on doc with no version key\n");
		saveDocumentAsNew(documentWindowManager);
	}
	if(!documentWindowManager.getVCDocument().getVersion().getOwner().compareEqual(getDocumentManager().getUser())) {
		// not the owner - this should also not happen, but check anyway...
		// keep the user informed this time
		System.out.println("\nIGNORED ERROR: should not have been able to use save/save edition on doc with different owner\n");
		String choice = PopupGenerator.showWarningDialog(documentWindowManager, getUserPreferences(), UserMessage.warn_SaveNotOwner,null);
		if (choice.equals(UserMessage.OPTION_SAVE_AS_NEW)){
			// user chose to Save As
			saveDocumentAsNew(documentWindowManager);
			return;
		} else {
			// user canceled, just show existing document
			getMdiManager().showWindow(documentWindowManager.getManagerID());
			return;
		}
	}
	
	/* request is valid, go ahead with save */
	
	/* block document window */
	JFrame currentDocumentWindow = getMdiManager().blockWindow(documentWindowManager.getManagerID());
	/* prepare hashtable for tasks */
	Hashtable hash = new Hashtable();
	hash.put("mdiManager", getMdiManager());
	hash.put("documentManager", getDocumentManager());
	hash.put("documentWindowManager", documentWindowManager);
	hash.put("currentDocumentWindow", currentDocumentWindow);
	/* create tasks */
	// check document consistency first
	AsynchClientTask documentValid = new DocumentValid();
	// check if unchanged document
	AsynchClientTask checkUnchanged = new CheckUnchanged(false);
	// save it
	AsynchClientTask saveDocument = new SaveDocument();
	// check for lost results
	AsynchClientTask checkBeforeDelete = new CheckBeforeDelete();
	// delete old document
	AsynchClientTask deleteOldDocument = new DeleteOldDocument();
	// clean up
	AsynchClientTask finishSave = new FinishSave();
	// assemble array
	AsynchClientTask[] tasks = null;
	if (replace) {
		tasks = new AsynchClientTask[] {
			documentValid,
			checkUnchanged,
			saveDocument,
			checkBeforeDelete,
			deleteOldDocument,
			finishSave
			};
	} else {
		tasks = new AsynchClientTask[] {
			documentValid,
			checkUnchanged,
			saveDocument,
			finishSave
			};
	}
	/* run tasks */
	ClientTaskDispatcher.dispatch(currentDocumentWindow, hash, tasks, false);
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 3:09:25 PM)
 * @param vcDocument cbit.vcell.document.VCDocument
 */
public void saveDocumentAsNew(DocumentWindowManager documentWindowManager) {
	/* block document window */
	JFrame currentDocumentWindow = getMdiManager().blockWindow(documentWindowManager.getManagerID());
	/* prepare hashtable for tasks */
	Hashtable hash = new Hashtable();
	hash.put("mdiManager", getMdiManager());
	hash.put("documentManager", getDocumentManager());
	hash.put("documentWindowManager", documentWindowManager);
	hash.put("currentDocumentWindow", currentDocumentWindow);
	/* create tasks */
	// check document consistency first
	AsynchClientTask documentValid = new DocumentValid();
	// get a new name
	AsynchClientTask newName = new NewName();
	// save it
	AsynchClientTask saveDocument = new SaveDocument();
	// clean up
	AsynchClientTask finishSave = new FinishSave();
	// assemble array
	AsynchClientTask[] tasks = new AsynchClientTask[] {
		documentValid,
		newName,
		saveDocument,
		finishSave
		};
	/* run tasks */
	ClientTaskDispatcher.dispatch(currentDocumentWindow, hash, tasks, false);
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:20:47 AM)
 * @param windowManager cbit.vcell.client.desktop.DocumentWindowManager
 */
public BioModelInfo selectBioModelInfo(TopLevelWindowManager requester) {
	VCDocumentInfo documentInfo = null;
	try {
		documentInfo = getMdiManager().getDatabaseWindowManager().selectDocument(VCDocument.BIOMODEL_DOC, requester);
	} catch (UserCancelException uexc) {
		System.out.println(uexc);
		return null;
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(requester, "Selection of BioModel failed\n"+exc);
	}
	return (BioModelInfo)documentInfo;
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:20:47 AM)
 * @param windowManager cbit.vcell.client.desktop.DocumentWindowManager
 */
public MathModelInfo selectMathModelInfo(TopLevelWindowManager requester) {
	VCDocumentInfo documentInfo = null;
	try {
		documentInfo = getMdiManager().getDatabaseWindowManager().selectDocument(VCDocument.MATHMODEL_DOC, requester);
	} catch (UserCancelException uexc) {
		System.out.println(uexc);
		return null;
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(requester, "Selection of MathModel failed\n"+exc);
	}
	return (MathModelInfo)documentInfo;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2005 2:02:23 PM)
 * @param newBExiting boolean
 */
private void setBExiting(boolean newBExiting) {
	bExiting = newBExiting;
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:21:50 AM)
 * @param newVcellClient cbit.vcell.client.VCellClient
 */
private void setVcellClient(VCellClient newVcellClient) {
	vcellClient = newVcellClient;
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:20:48 AM)
 */
public void showBNGWindow() {
	getMdiManager().showWindow(ClientMDIManager.BIONETGEN_WINDOW_ID);
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:20:48 AM)
 */
public void showDatabaseWindow() {
	getMdiManager().showWindow(ClientMDIManager.DATABASE_WINDOW_ID);
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:20:48 AM)
 */
public void showTestingFrameworkWindow() {
	getMdiManager().showWindow(ClientMDIManager.TESTING_FRAMEWORK_WINDOW_ID);
}


/**
 * Insert the method's description here.
 * Creation date: (6/15/2004 2:37:01 AM)
 */
public void startExport(final TopLevelWindowManager windowManager, final ExportSpecs exportSpecs) {
	// start a thread to get it; not blocking any window/frame
	SwingWorker worker = new SwingWorker() {
		public Object construct() {
			try {
				getClientServerManager().getJobManager().startExport(exportSpecs);
			} catch (DataAccessException exc) {
				exc.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(windowManager, exc.getMessage());
			}
			return null;
		}
		public void finished() {}
	};
	worker.start();
}


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 2:29:35 AM)
 * @param documentWindowManager cbit.vcell.client.DocumentWindowManager
 * @param simulations cbit.vcell.solver.Simulation[]
 */
public void stopSimulations(final ClientSimManager clientSimManager, final Simulation[] simulations) {
	// stop is single step operation, don't bother with tasks, thread inline
	SwingWorker worker = new SwingWorker() {
		public Object construct() {
			Hashtable failures = new Hashtable();
			if (simulations != null && simulations.length > 0) {
				for (int i = 0; i < simulations.length; i++){
					try {
						SimulationInfo simInfo = simulations[i].getSimulationInfo();
						if (simInfo != null) {
							// check for running once more... directly from job status
							SimulationStatus serverSimulationStatus = getServerSimulationStatus(simInfo);
							if (serverSimulationStatus != null && serverSimulationStatus.numberOfJobsDone() < simulations[i].getScanCount()) {
								getClientServerManager().getJobManager().stopSimulation(simInfo.getAuthoritativeVCSimulationIdentifier());
								// updateStatus
								clientSimManager.updateStatusFromStopRequest(simulations[i]);
							}
						} else {
							// this should really not happen...
							throw new RuntimeException(">>>>>>>>>> trying to stop an unsaved simulation...");
						}	
					} catch (Throwable exc) {
						exc.printStackTrace(System.out);
						failures.put(simulations[i], exc);
					}
				}
			}
			return failures;
		}
		public void finished() {
			Hashtable failures = (Hashtable)get();
			if (! failures.isEmpty()) {
				Enumeration en = failures.keys();
				while (en.hasMoreElements()) {
					Simulation sim = (Simulation)en.nextElement();
					Throwable exc = (Throwable)failures.get(sim);
					// notify user
					PopupGenerator.showErrorDialog(clientSimManager.getDocumentWindowManager(), "Failed to dispatch stop request for simulation'"+sim.getName()+"'\n"+exc.getMessage());
				}
			}
		}
	};
	worker.start();	
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 12:10:07 PM)
 */
public void updateStatusNow() {
	// thread safe update of gui
	getVcellClient().getStatusUpdater().updateNow(getVcellClient().getClientServerManager().getConnectionStatus());
}
}
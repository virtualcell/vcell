package cbit.vcell.client;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.zip.DataFormatException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import org.jdom.Element;
import org.jdom.Namespace;
import org.vcell.util.BeanUtils;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.TokenMangler;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.CurateSpec;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelChildSummary;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.VersionableType;
import org.vcell.util.document.VersionableTypeVersion;
import org.vcell.util.document.VCDocument.DocumentCreationInfo;
import org.vcell.util.gui.AsynchGuiUpdater;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.FileFilters;
import org.vcell.util.gui.UtilCancelException;
import org.vcell.util.gui.VCFileChooser;
import org.vcell.util.gui.ZEnforcer;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageInfo;
import cbit.image.VCImageUncompressed;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.rmi.event.VCellMessageEvent;
import cbit.rmi.event.VCellMessageEventListener;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReader;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.VirtualMicroscopy.importer.MicroscopyXMLTags;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.FieldDataWindowManager.SimInfoHolder;
import cbit.vcell.client.data.OutputContext;
import cbit.vcell.client.desktop.mathmodel.VCMLEditorPanel;
import cbit.vcell.client.server.AsynchMessageManager;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.server.DataManager;
import cbit.vcell.client.server.DataViewerController;
import cbit.vcell.client.server.MergedDatasetViewerController;
import cbit.vcell.client.server.ODEDataManager;
import cbit.vcell.client.server.PDEDataManager;
import cbit.vcell.client.server.SimResultsViewerController;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.server.VCDataManager;
import cbit.vcell.client.server.VCellThreadChecker;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.CheckBeforeDelete;
import cbit.vcell.client.task.CheckUnchanged;
import cbit.vcell.client.task.ChooseFile;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.DeleteOldDocument;
import cbit.vcell.client.task.DocumentToExport;
import cbit.vcell.client.task.DocumentValid;
import cbit.vcell.client.task.ExportToXML;
import cbit.vcell.client.task.FinishExport;
import cbit.vcell.client.task.FinishSave;
import cbit.vcell.client.task.NewName;
import cbit.vcell.client.task.RunSims;
import cbit.vcell.client.task.SaveDocument;
import cbit.vcell.client.task.SetMathDescription;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.ImageDbTreePanel;
import cbit.vcell.desktop.LoginDialog;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.geometry.ROIMultiPaintManager;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.gui.GeometrySummaryPanel;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.Model;
import cbit.vcell.parser.Expression;
import cbit.vcell.server.UserRegistrationOP;
import cbit.vcell.simdata.MergedDataInfo;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.gui.SimulationStatus;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.xml.XMLInfo;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlHelper;
import cbit.xml.merge.TMLPanel;
import cbit.xml.merge.XmlTreeDiff;
/**
 * Insert the type's description here.
 * Creation date: (5/21/2004 2:42:55 AM)
 * @author: Ion Moraru
 */
public class ClientRequestManager implements RequestManager, PropertyChangeListener, ExportListener, VCellMessageEventListener {
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

private static final String GEOMETRY_KEY = "geometry";
private void changeGeometry0(final TopLevelWindowManager requester, final SimulationContext simContext) {
	AsynchClientTask selectDocumentTypeTask = new AsynchClientTask("Select/Load geometry", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			int[] geomType = null;
			geomType = DialogUtils.showComponentOKCancelTableList(
				JOptionPane.getFrameForComponent(requester.getComponent()), 
				"Select different Geometry",
				new String[] {"Search by"}, 
				new String[][] {{"BioModel names"},{"MathModel names"},{"Geometry names"}}, ListSelectionModel.SINGLE_SELECTION);
			VCDocumentInfo vcDocumentInfo = selectDocumentFromType(geomType[0], requester);
			hashTable.put("vcDocumentInfo", vcDocumentInfo);
		}		
	};

	AsynchClientTask selectLoadGeomTask = new AsynchClientTask("Select/Load geometry...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			VCDocumentInfo vcDocumentInfo = (VCDocumentInfo)hashTable.get("vcDocumentInfo");
			Geometry geom = getGeometryFromDocumentSelection(requester.getComponent(),vcDocumentInfo, false);
			geom.precomputeAll();//pregenerate sampled image, cpu intensive
			hashTable.put(GEOMETRY_KEY, geom);
		}		
	};

	AsynchClientTask confirmGeomTask = new AsynchClientTask("Review Geometry...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(final Hashtable<String, Object> hashTable) throws Exception {
			Geometry geom = (Geometry)hashTable.get(GEOMETRY_KEY);
			GeometrySummaryPanel geometrySummaryPanel = new GeometrySummaryPanel();
			geometrySummaryPanel.setGeometry(geom);
			int result = DialogUtils.showComponentOKCancelDialog(JOptionPane.getFrameForComponent(requester.getComponent()), geometrySummaryPanel, "Confirm Geometry Selection");
			if(result != JOptionPane.OK_OPTION){
				throw UserCancelException.CANCEL_GENERIC;
			}
		}		
	};
	AsynchClientTask processGeometryTask = new AsynchClientTask("Processing geometry...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			Geometry newGeometry = (Geometry)hashTable.get(GEOMETRY_KEY);
			if(requester instanceof MathModelWindowManager){
				//User can cancel here
				continueAfterMathModelGeomChangeWarning((MathModelWindowManager)requester, newGeometry);
			}
			if (newGeometry.getDimension()>0 && newGeometry.getGeometrySurfaceDescription().getGeometricRegions()==null){
				newGeometry.getGeometrySurfaceDescription().updateAll();				
			}			
		}		
	};
	AsynchClientTask setNewGeometryTask = new AsynchClientTask("Setting new Geometry...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			Geometry newGeometry = (Geometry)hashTable.get(GEOMETRY_KEY);
			if (newGeometry != null) {
				if (requester instanceof BioModelWindowManager) {
					simContext.setGeometry(newGeometry);
				} else if (requester instanceof MathModelWindowManager) {
					MathModel mathModel = (MathModel)((MathModelWindowManager)requester).getVCDocument();
					mathModel.getMathDescription().setGeometry(newGeometry);
				}
			}
		}
	};

	Hashtable<String, Object> hashTable = new Hashtable<String, Object>();
	ClientTaskDispatcher.dispatch(requester.getComponent(),hashTable,
			new AsynchClientTask[] {selectDocumentTypeTask, selectLoadGeomTask, confirmGeomTask,processGeometryTask,setNewGeometryTask}, false);
	
}

public void changeGeometry(DocumentWindowManager requester, SimulationContext simContext) {
	changeGeometry0(requester, simContext);
}

public static void continueAfterMathModelGeomChangeWarning(MathModelWindowManager mathModelWindowManager,Geometry newGeometry) throws UserCancelException{

	MathModel mathModel = mathModelWindowManager.getMathModel();
	if(mathModel != null && mathModel.getMathDescription() != null){
		Geometry oldGeometry = mathModel.getMathDescription().getGeometry();
		if(oldGeometry != null && oldGeometry.getDimension() == 0){
			return;
		}
		boolean bMeshResolutionChange = true;
		if(oldGeometry == null){
			bMeshResolutionChange = false;
		}
		if(newGeometry != null && oldGeometry != null && oldGeometry.getDimension() == newGeometry.getDimension()){
			bMeshResolutionChange = false;
		}
		boolean bHasSims = (mathModel.getSimulations() != null) && (mathModel.getSimulations().length > 0);
		StringBuffer meshResolutionChangeSB = new StringBuffer();
		if(bHasSims && bMeshResolutionChange){
			ISize newGeomISize = MeshSpecification.calulateResetSamplingSize(newGeometry);
			for (int i = 0; i < mathModel.getSimulations().length; i++) {
				if (mathModel.getSimulations()[i].getMeshSpecification() != null) {
					String simName = mathModel.getSimulations()[i].getName();
					ISize simMeshSize = mathModel.getSimulations()[i].getMeshSpecification().getSamplingSize();
					meshResolutionChangeSB.append((i!=0?"\n":"")+
						"'"+simName+"' Mesh"+simMeshSize+" will be reset to "+newGeomISize+"");
				}
			}
		}

		String result = DialogUtils.showWarningDialog(JOptionPane.getFrameForComponent(mathModelWindowManager.getComponent()),
				"After changing MathModel geometry please note:\n"+
				"  1.  Check Geometry subvolume names match MathModel compartment names."+
				(bHasSims && bMeshResolutionChange?"\n"+
						"  2.  All existing simulations mesh sizes will be reset"+
						" because the new Geometry spatial dimension("+newGeometry.getDimension()+"D)"+
						" does not equal the current Geometry spatial dimension("+oldGeometry.getDimension()+"D)"+
						"\n"+meshResolutionChangeSB.toString():""),
				new String[] {"Continue","Cancel"},
				"Continue");
		if(result != null && result.equals("Continue")){
			return;
		}
	}else{
		return;
	}

	throw UserCancelException.CANCEL_GENERIC;
}

enum CloseOption {
	SAVE_AND_CLOSE,
	CLOSE_IN_ANY_CASE,
	CANCEL_CLOSE,
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:20:47 AM)
 * @param windowManager cbit.vcell.client.desktop.DocumentWindowManager
 */
private CloseOption checkBeforeClosing(DocumentWindowManager windowManager) {
	getMdiManager().showWindow(windowManager.getManagerID());
	// we need to check for changes and get user confirmation...
	VCDocument vcDocument = windowManager.getVCDocument();
	if (vcDocument.getVersion() == null && !isDifferentFromBlank(vcDocument.getDocumentType(), vcDocument)) {
		return CloseOption.CLOSE_IN_ANY_CASE;
	}
	boolean isChanged = true;
	try {
		isChanged = getDocumentManager().isChanged(vcDocument);
	} catch (Exception exc) {
		String choice = PopupGenerator.showWarningDialog(windowManager, getUserPreferences(), UserMessage.warn_UnableToCheckForChanges, null);
		if (choice.equals(UserMessage.OPTION_CANCEL)){
			// user canceled
			return CloseOption.CANCEL_CLOSE;
		}
	}
	// warn if necessary
	if (isChanged) {
		String choice = PopupGenerator.showWarningDialog(windowManager, getUserPreferences(), UserMessage.warn_close,null);
		if (choice.equals(UserMessage.OPTION_CANCEL)){
			// user canceled
			return CloseOption.CANCEL_CLOSE;
		}
		if (choice.equals(UserMessage.OPTION_YES)) {
			return CloseOption.SAVE_AND_CLOSE;
		}
	}
	// nothing changed, or user confirmed, close it
	return CloseOption.CLOSE_IN_ANY_CASE;
}


/**
 * Insert the method's description here.
 * Creation date: (5/10/2004 3:48:16 PM)
 */
public boolean isDifferentFromBlank(int documentType, VCDocument vcDocument) {
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
	Enumeration<TopLevelWindowManager> en = getMdiManager().getWindowManagers();
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
public boolean closeWindow(final java.lang.String windowID, final boolean exitIfLast) {
	// called from DocumentWindow or from DatabaseWindow
	final TopLevelWindowManager windowManager = getMdiManager().getWindowManager(windowID);
	if (windowManager instanceof DocumentWindowManager) {
		// we'll need to run some checks first
		getMdiManager().showWindow(windowID);
		getMdiManager().blockWindow(windowID);
		CloseOption closeOption = checkBeforeClosing((DocumentWindowManager)windowManager);
		if (closeOption.equals(CloseOption.CANCEL_CLOSE)) {
			// user canceled
			getMdiManager().unBlockWindow(windowID);
			return false;
		} else if (closeOption.equals(CloseOption.SAVE_AND_CLOSE)) {
			AsynchClientTask closeTask = new AsynchClientTask("closing document", AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, false) {

				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					// if there is error saving the document, try to unblock the window
					if (hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR) != null) {
						getMdiManager().unBlockWindow(windowID);
						getMdiManager().showWindow(windowID);
					} else {
						int openWindows = getMdiManager().closeWindow(windowManager.getManagerID());
						if (exitIfLast && (openWindows == 0)) {
							setBExiting(true);
							exitApplication();
						}
					}
				}

				@Override
				public boolean skipIfCancel(UserCancelException exc) {
					// if save as new edition, don't skip
					if (exc == UserCancelException.CANCEL_DELETE_OLD || exc == UserCancelException.CANCEL_NEW_NAME) {
						return false;
					} else {
						return true;
					}
				}
			};

			saveDocument((DocumentWindowManager)windowManager, true, closeTask);
			return true; 
		} else {
			int openWindows = getMdiManager().closeWindow(windowID);
			if (exitIfLast && (openWindows == 0)) {
				setBExiting(true);
				exitApplication();
			}
			return true;
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
	} else if (windowManager instanceof FieldDataWindowManager) {
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


private XmlTreeDiff compareDocuments(final VCDocument doc1, final VCDocument doc2, String comparisonSetting) throws Exception {

	VCellThreadChecker.checkCpuIntensiveInvocation();
	
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
	final XmlTreeDiff diffTree = XmlHelper.compareMerge(doc1XML, doc2XML, comparisonSetting, true);
	return diffTree;
}


/**
 Processes the model comparison request.
 * Creation date: (6/9/2004 1:07:09 PM)
 * @param vcDocument cbit.vcell.document.VCDocument
 */
public XmlTreeDiff compareWithOther(final VCDocumentInfo docInfo1, final VCDocumentInfo docInfo2) {

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
		return compareDocuments(document1, document2, TMLPanel.COMPARE_DOCS_OTHER);
	} catch (Exception e) {
		e.printStackTrace();
		throw new RuntimeException(e.getMessage());
	}
}


/**
   Processes the comparison (XML based) of the loaded model with its saved edition.
 * Creation date: (6/9/2004 1:07:09 PM)
 * @param vcDocument cbit.vcell.document.VCDocument
 */
public XmlTreeDiff compareWithSaved(VCDocument document) {
	
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
		return compareDocuments(savedVersion, document, TMLPanel.COMPARE_DOCS_SAVED);
	} catch (Exception e) {
		e.printStackTrace();
		throw new RuntimeException(e.getMessage());
	}				
}


/**
 * Insert the method's description here.
 * Creation date: (6/16/2004 11:07:33 AM)
 * @param clientServerInfo cbit.vcell.client.server.ClientServerInfo
 */
public void connectAs(final String user,  final String password, final TopLevelWindowManager requester) {
	String confirm = PopupGenerator.showWarningDialog(requester, getUserPreferences(), UserMessage.warn_changeUser,null);
	if (confirm.equals(UserMessage.OPTION_CANCEL)){
		return;
	} else {
		boolean closedAllWindows = closeAllWindows(false);
		if (! closedAllWindows) {
			// user bailed out on closing some window
			return;
		} else {
			AsynchClientTask waitTask = new AsynchClientTask("wait for window closing", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					// wait until all windows are closed.
					long startTime = System.currentTimeMillis();
					while (true) {
						int numOpenWindows = getMdiManager().closeWindow(ClientMDIManager.DATABASE_WINDOW_ID);
						if (numOpenWindows == 0) {
							break;
						}
						// if can't save all the documents, don't connect as thus throw exception.
						if (System.currentTimeMillis() - startTime > 60000) {
							throw UserCancelException.CANCEL_GENERIC;
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}					
				}
			};
			// ok, connect as a different user
			// asynch & nothing to do on Swing queue (updates handled by events)
			String taskName = "Connecting as " + user;
			AsynchClientTask[] newTasks = newDocument(requester, new VCDocument.DocumentCreationInfo(VCDocument.BIOMODEL_DOC, 0));
			AsynchClientTask task1 = new AsynchClientTask(taskName, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					getClientServerManager().connectAs(requester, user, password);
				}
			};
			AsynchClientTask task2 = new AsynchClientTask(taskName, AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					getMdiManager().refreshRecyclableWindows();
				}
			};
			AsynchClientTask[] taskArray = new AsynchClientTask[newTasks.length + 3];
			taskArray[0] = waitTask;
			System.arraycopy(newTasks, 0, taskArray, 1, newTasks.length);
			taskArray[taskArray.length - 2] = task1;
			taskArray[taskArray.length - 1] = task2;
			
			Hashtable<String, Object> hash = new Hashtable<String, Object>();
			hash.put("guiParent", requester.getComponent());
			hash.put("requestManager", this);
			ClientTaskDispatcher.dispatch(requester.getComponent(), new Hashtable<String, Object>(), taskArray, false, false, null);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 2:18:16 AM)
 * @param clientServerInfo cbit.vcell.client.server.ClientServerInfo
 */
public void connectToServer(TopLevelWindowManager requester, ClientServerInfo clientServerInfo) throws Exception {
	getClientServerManager().connect(requester, clientServerInfo);
}


/**
 * Insert the method's description here.
 * Creation date: (5/10/2004 3:48:16 PM)
 */
VCDocument createDefaultDocument(int docType) {
	VCDocument defaultDocument = null;
	try {
		switch (docType) {
			case VCDocument.BIOMODEL_DOC: {
				// blank			
				return createDefaultBioModelDocument();
			}
			case VCDocument.MATHMODEL_DOC: {			
				return createDefaultMathModelDocument();
			}
			default: {
				throw new RuntimeException("default document can only be BioModel or MathModel");
			}
		}
	} catch (Exception e) {
		e.printStackTrace(System.out);
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
			mathDesc.addSubDomain(new CompartmentSubDomain("Compartment",CompartmentSubDomain.NON_SPATIAL_PRIORITY));
		}else{
			try {
				if (geometry.getDimension()>0 && geometry.getGeometrySurfaceDescription().getGeometricRegions() == null){
					geometry.getGeometrySurfaceDescription().updateAll();
				}
			}catch (ImageException e){
				e.printStackTrace(System.out);
				throw new RuntimeException("Geometric surface generation error: \n"+e.getMessage());
			}catch (GeometryException e){
				e.printStackTrace(System.out);
				throw new RuntimeException("Geometric surface generation error: \n"+e.getMessage());
			}

			SubVolume subVolumes[] = geometry.getGeometrySpec().getSubVolumes();
			for (int i=0;i<subVolumes.length;i++){
				mathDesc.addSubDomain(new CompartmentSubDomain(subVolumes[i].getName(),subVolumes[i].getHandle()));
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
public void createMathModelFromApplication(final BioModelWindowManager requester, final String name, final SimulationContext simContext) {
	if (simContext == null) {
		PopupGenerator.showErrorDialog(requester, "Selected Application is null, cannot generate corresponding math model");
		return;
	}
	AsynchClientTask task1 = new AsynchClientTask("Creating MathModel from BioModel Applicaiton", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			MathModel newMathModel = new MathModel(null);
			//Get corresponding mathDesc to create new mathModel.
			MathDescription mathDesc = simContext.getMathDescription();
			MathDescription newMathDesc = null;
			newMathDesc = new MathDescription(name+"_"+(new java.util.Random()).nextInt());
			try {
				if (mathDesc.getGeometry().getDimension()>0 && mathDesc.getGeometry().getGeometrySurfaceDescription().getGeometricRegions() == null){
					mathDesc.getGeometry().getGeometrySurfaceDescription().updateAll();
				}

			}catch (ImageException e){
				e.printStackTrace(System.out);
				throw new RuntimeException("Geometric surface generation error:\n"+e.getMessage());
			}catch (GeometryException e){
				e.printStackTrace(System.out);
				throw new RuntimeException("Geometric surface generation error:\n"+e.getMessage());
			}
			newMathDesc.setGeometry(mathDesc.getGeometry());
			newMathDesc.read_database(new CommentStringTokenizer(mathDesc.getVCML_database()));
			newMathDesc.isValid();

			newMathModel.setName(name);
			newMathModel.setMathDescription(newMathDesc);
			hashTable.put("newMathModel", newMathModel);
		}		
	};
	
	AsynchClientTask task2 = new AsynchClientTask("Creating MathModel from BioModel Application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			MathModel newMathModel = (MathModel)hashTable.get("newMathModel");
			DocumentWindowManager windowManager = createDocumentWindowManager(newMathModel);
			if(simContext.getBioModel().getVersion() != null){
				((MathModelWindowManager)windowManager). setCopyFromBioModelAppVersionableTypeVersion(
							new VersionableTypeVersion(VersionableType.BioModelMetaData, simContext.getBioModel().getVersion()));
			}
			getMdiManager().createNewDocumentWindow(windowManager);
		}
	};
	ClientTaskDispatcher.dispatch(requester.getComponent(), new Hashtable<String, Object>(),  new AsynchClientTask[]{task1, task2}, false);
}

private BioModel createDefaultBioModelDocument() throws Exception {
	BioModel bioModel = new BioModel(null);
	bioModel.setName("BioModel" + (getMdiManager().getNewlyCreatedDesktops() + 1));
	Model model = bioModel.getModel();
	model.createFeature(null);
	return bioModel;
}

private MathModel createDefaultMathModelDocument() throws Exception {
	Geometry geometry = new Geometry("Untitled", 0);
	MathModel mathModel = createMathModel("Untitled", geometry);
	mathModel.setName("MathModel" + (getMdiManager().getNewlyCreatedDesktops() + 1));
	return mathModel;
}

public VCDocumentInfo selectDocumentFromType(int documentType, TopLevelWindowManager requester) throws Exception,UserCancelException{
	return
		getMdiManager().getDatabaseWindowManager().selectDocument(documentType, requester);
}

public Geometry getGeometryFromDocumentSelection(Component parentComponent,VCDocumentInfo vcDocumentInfo,boolean bClearVersion) throws Exception,UserCancelException{
	Geometry geom = null;
	if(vcDocumentInfo.getVersionType().equals(VersionableType.BioModelMetaData)/*documentType == VCDocument.BIOMODEL_DOC*/){
		BioModelInfo bioModelInfo =
			getDocumentManager().getBioModelInfo(vcDocumentInfo.getVersion().getVersionKey());
		BioModelChildSummary bioModelChildSummary = bioModelInfo.getBioModelChildSummary();
		if(bioModelChildSummary != null && bioModelChildSummary.getSimulationContextNames() != null){
			Vector<Integer> spatialV = new Vector<Integer>();
			for (int i = 0; i < bioModelChildSummary.getSimulationContextNames().length; i++) {
				if(bioModelChildSummary.getGeometryDimensions()[i] > 0){spatialV.add(i);}
			}
			if(spatialV.size() > 0){
				String[] columnNames = new String[]{"Application","Geometry","Dimension"};
				String[][] rowData = new String[spatialV.size()][3];
				for (int i = 0; i < spatialV.size(); i++) {
					rowData[i][0] = bioModelChildSummary.getSimulationContextNames()[spatialV.elementAt(i)];
					rowData[i][1] = bioModelChildSummary.getGeometryNames()[spatialV.elementAt(i)];
					rowData[i][2] = bioModelChildSummary.getGeometryDimensions()[spatialV.elementAt(i)]+"";
				}
				int[] selection = DialogUtils.showComponentOKCancelTableList(JOptionPane.getFrameForComponent(parentComponent),
						"Select Geometry", columnNames, rowData, ListSelectionModel.SINGLE_SELECTION);
				BioModel bioModel = getDocumentManager().getBioModel((BioModelInfo)vcDocumentInfo);
				for (int i = 0; i < bioModel.getSimulationContexts().length; i++) {
					if(bioModel.getSimulationContexts()[i].getName().equals(rowData[selection[0]][0])){
						geom = bioModel.getSimulationContexts()[i].getGeometry();
						break;
					}
				}
			}else{
				throw new Exception("BioModel '"+bioModelInfo.getVersion().getName()+"' contains no spatial geometries.");
			}
		}else{
			throw new Exception("BioModel '"+bioModelInfo.getVersion().getName()+"' contains no spatial geometries.");
		}
	}else if(vcDocumentInfo.getVersionType().equals(VersionableType.MathModelMetaData)/*documentType == VCDocument.MATHMODEL_DOC*/){
		MathModelInfo mathModelInfo =
			getDocumentManager().getMathModelInfo(vcDocumentInfo.getVersion().getVersionKey());
		MathModelChildSummary mathModelChildSummary = mathModelInfo.getMathModelChildSummary();
		if(mathModelChildSummary != null){
			if(mathModelChildSummary.getGeometryDimension() > 0){
				MathModel mathModel = getDocumentManager().getMathModel(mathModelInfo);
				geom = mathModel.getMathDescription().getGeometry();
			}else{
				throw new Exception("MathModel '"+mathModelInfo.getVersion().getName()+"' contains no spatial geometry.");				
			}
		}else{
			throw new Exception("MathModel '"+mathModelInfo.getVersion().getName()+"' contains no spatial geometry.");
		}
	}else if(vcDocumentInfo.getVersionType().equals(VersionableType.Geometry)){
		geom = getDocumentManager().getGeometry((GeometryInfo)vcDocumentInfo);
		if(geom.getDimension() == 0){
			throw new Exception("Error, Only spatial geometries allowed (dimension > 0).");
		}
	}else{
		throw new IllegalArgumentException("Error selecting geometry from document type "+vcDocumentInfo.getVersionType()+". Must be BioModel,MathModel or Geometry.");
	}
	if(geom == null){
		throw new Exception("error selecting geometry");
	}
	if(bClearVersion){
		geom.clearVersion();
	}
	return geom;
}
public static final String GUI_PARENT = "guiParent";

public static boolean isImportGeometryType(DocumentCreationInfo documentCreationInfo){
	return
	documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_DBIMAGE ||
	documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FIELDDATA ||
	documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FILE ||
	documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FROM_SCRATCH;
}

private static boolean askMergeChannels(Component guiParent,File imageFile,int numChannels) throws UserCancelException{
	final String keepChannels = "Keep Channels";
	final String mergeGrayscale = "Merge Grayscale";
	final String cancelOption = "Cancel";
	String result = DialogUtils.showWarningDialog(guiParent, 
			"Import "+(imageFile.isDirectory()?"directory":"file")+
			" '"+imageFile.getAbsolutePath()+"' image has "+numChannels+" color channels.  Choose an import option.",
			new String[] {keepChannels,mergeGrayscale,cancelOption}, keepChannels);
	if(result.equals(cancelOption)){
		throw UserCancelException.CANCEL_GENERIC;
	}else if(result.equals(mergeGrayscale)){
		return true;
	}
	return false;
}

private static void throwImportWholeDirectoryException(File invalidFile,String extraInfo) throws Exception{
	throw new Exception("Import whole directory failed: directory '"+invalidFile.getAbsolutePath()+"' "+
			"contains invalid file.  Import from whole directory can contain only files for "+
			"a single z-series, each file must be 2D, single time.  "+
			"All files must be the same size and have the same number color channels."+
			(extraInfo==null?"":"\n"+extraInfo));

}

public static final String IMAGE_FROM_DB = "IMAGE_FROM_DB";
public AsynchClientTask[] createNewGeometryTasks(final TopLevelWindowManager requester,
		final VCDocument.DocumentCreationInfo documentCreationInfo,
		final AsynchClientTask[] afterTasks,
		final String okButtonText){
	
	if(!isImportGeometryType(documentCreationInfo)){
		throw new IllegalArgumentException("Analytic geometry not implemented.");
		
	}
	final String IMPORT_SOURCE_NAME = "IMPORT_SOURCE_NAME";
	
	AsynchClientTask loadImgFromDBTask = new AsynchClientTask("Select/Load database image...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if(hashTable.get(ClientRequestManager.IMAGE_FROM_DB) != null){
				//Previous task already loaded our dbImage
				return;
			}
			//show list of image names
			ImageDbTreePanel imageDbTreePanel = new ImageDbTreePanel();
			imageDbTreePanel.setDocumentManager(getDocumentManager());
			imageDbTreePanel.setPreferredSize(new java.awt.Dimension(200, 400));
			int result = DialogUtils.showComponentOKCancelDialog(requester.getComponent(), imageDbTreePanel, "Select Image:");
			if(result != JOptionPane.OK_OPTION){
				throw UserCancelException.CANCEL_GENERIC;
			}
			VCImageInfo vcImageInfo = (VCImageInfo)imageDbTreePanel.getSelectedVersionInfo();
			if(vcImageInfo == null){
				throw UserCancelException.CANCEL_GENERIC;
			}
			VCImage image = getDocumentManager().getImage(vcImageInfo);
			if (image == null){
				throw new Exception("Database Image '"+vcImageInfo.getVersion().getName()+"' couldn't be loaded.");
			}
			hashTable.put(IMAGE_FROM_DB, image);
		}
	};

	// Get image from file
	AsynchClientTask selectImageFileTask = new AsynchClientTask("select image file", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			File imageFile = DatabaseWindowManager.showFileChooserDialog(
					requester, null,
					getUserPreferences(),JFileChooser.FILES_AND_DIRECTORIES);
			hashTable.put("imageFile", imageFile);
			hashTable.put(IMPORT_SOURCE_NAME, "File: "+imageFile.getName());
		}
	};
	
	final String FDFOS = "FDFOS";
	final String INITIAL_ANNOTATION	 = "INITIAL_ANNOTATION";
	AsynchClientTask parseImageTask = new AsynchClientTask("read and parse image file", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(final Hashtable<String, Object> hashTable) throws Exception {
			final Component guiParent =(Component)hashTable.get(ClientRequestManager.GUI_PARENT);
			try {
				FieldDataFileOperationSpec fdfos = null;
				if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_DBIMAGE){
					VCImage dbImage = (VCImage)hashTable.get(IMAGE_FROM_DB);
					hashTable.put(IMPORT_SOURCE_NAME,"Img Database: "+dbImage.getName());

					if(dbImage.getDescription() != null){
						hashTable.put(INITIAL_ANNOTATION, dbImage.getDescription());
					}
					short[] templateShorts = new short[dbImage.getNumXYZ()];
					for (int i = 0; i < dbImage.getPixels().length; i++) {
						templateShorts[i] = (short)(0x00FF&dbImage.getPixels()[i]);
					}
					fdfos = new FieldDataFileOperationSpec();
					fdfos.origin = new Origin(0,0,0);
					fdfos.extent = dbImage.getExtent();
					fdfos.isize = new ISize(dbImage.getNumX(), dbImage.getNumY(), dbImage.getNumZ());
					fdfos.shortSpecData = new short[][][] {{templateShorts}};
				}else if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FILE){
					File imageFile = (File)hashTable.get("imageFile");
					if(imageFile == null){
						throw new Exception("No file selected");
					}
					File[] dirFiles = null;
					int numChannels = 0;
					if(imageFile.isDirectory()){
						dirFiles = imageFile.listFiles(new java.io.FileFilter(){
							public boolean accept(File pathname) {
								return pathname.isFile() && !pathname.isHidden();//exclude windows Thumbs.db
							}});
						if(dirFiles.length == 0){
							throw new Exception("No valid files in selected directory");
						}
//						for (int i = 0; i < dirFiles.length; i++) {
//							if(!dirFiles[i].isFile()){
//								throwImportWholeDirectoryException(dirFiles[i],null);
//							}
//						}
						hashTable.put(IMPORT_SOURCE_NAME,"Directory: "+imageFile.getAbsolutePath());
						if(dirFiles.length > 1){
							numChannels = ImageDatasetReader.getChannelCount(dirFiles[0].getAbsolutePath());
							final String importZ = "Import Z-Sections";
							final String cancelOption = "Cancel";
							String result = DialogUtils.showWarningDialog(guiParent, 
									"Import all files in directory '"+imageFile.getAbsolutePath()+"' as Z-Sections",
									new String[] {importZ,cancelOption}, importZ);
							if(result.equals(cancelOption)){
								throw UserCancelException.CANCEL_GENERIC;
							}
						}
					}else{
						numChannels = ImageDatasetReader.getChannelCount(imageFile.getAbsolutePath());
						hashTable.put(IMPORT_SOURCE_NAME,"File: "+imageFile.getAbsolutePath());
					}
					
					boolean bMergeChannels = false;
					if(dirFiles != null){
						Arrays.sort(dirFiles, new Comparator<File>(){
							public int compare(File o1, File o2) {
								return o1.getName().compareToIgnoreCase(o2.getName());
							}});
						if(numChannels > 1){
							bMergeChannels = ClientRequestManager.askMergeChannels(guiParent, imageFile, numChannels);
						}
						hashTable.put(INITIAL_ANNOTATION, dirFiles[0].getAbsolutePath()+"\n.\n.\n.\n"+dirFiles[dirFiles.length-1].getAbsolutePath());
						short[][] dataToSegment = null;
						ISize isize = null;
						Origin origin = null;
						Extent extent = null;
						int sizeXY = 0;
						ISize firstImageISize = null;
						for (int i = 0; i < dirFiles.length; i++) {
							ImageDataset[] imageDatasets = ImageDatasetReader.readImageDatasetChannels(dirFiles[i].getAbsolutePath(), null,bMergeChannels);
							for (int c = 0; c < imageDatasets.length; c++) {
								if(imageDatasets[c].getSizeZ() != 1 || imageDatasets[c].getSizeT() != 1){
									throwImportWholeDirectoryException(imageFile,
											dirFiles[i].getAbsolutePath()+" has Z="+imageDatasets[c].getSizeZ()+" T="+imageDatasets[c].getSizeT());
								}
								if(isize == null){
									firstImageISize = imageDatasets[c].getISize();
									sizeXY = imageDatasets[c].getISize().getX()*imageDatasets[c].getISize().getY();
									dataToSegment = new short[imageDatasets.length][sizeXY*dirFiles.length];
									isize = new ISize(imageDatasets[c].getISize().getX(),imageDatasets[c].getISize().getY(),dirFiles.length);
									origin = imageDatasets[c].getAllImages()[0].getOrigin();
									extent = imageDatasets[c].getExtent();
								}
								if(!firstImageISize.compareEqual(imageDatasets[c].getISize())){
									throwImportWholeDirectoryException(imageFile,
											dirFiles[0].getAbsolutePath()+" "+firstImageISize+" does not equal "+dirFiles[i].getAbsolutePath()+" "+imageDatasets[c].getISize());	
								}
								System.arraycopy(imageDatasets[c].getImage(0, 0, 0).getPixels(), 0, dataToSegment[c], sizeXY*i, sizeXY);								
								
							}
						}
						fdfos = new FieldDataFileOperationSpec();
						fdfos.origin = origin;
						fdfos.extent = extent;
						fdfos.isize = isize;
						fdfos.shortSpecData = new short[][][] {dataToSegment};

					}else{
						if(numChannels > 1){
							bMergeChannels = ClientRequestManager.askMergeChannels(guiParent, imageFile, numChannels);
						}
						hashTable.put(INITIAL_ANNOTATION, imageFile.getAbsolutePath());
						int userPreferredTime = 0;
						try{
							getClientTaskStatusSupport().setMessage("Checking file for time information...");
							double[] allTimes = ImageDatasetReader.getTimesOnly(imageFile.getAbsolutePath());
							if(allTimes.length > 1){
								String[][] rowData = new String[allTimes.length][1];
								for (int i = 0; i < rowData.length; i++) {
									rowData[i][0] = allTimes[i]+"";
								}
								userPreferredTime = DialogUtils.showComponentOKCancelTableList(
										guiParent, "File contains data in multiple timepoints, select 1 timepoint for import",
										new String[] {"times"}, rowData, new Integer(ListSelectionModel.SINGLE_SELECTION))[0];
							}
						}catch(UserCancelException uce){
							throw uce;
						}catch(Exception e){
							e.printStackTrace();
							//ignore, try to load without checking for times and use the first time if successful
						}
						getClientTaskStatusSupport().setMessage("Reading file...");
						ImageDataset[] imageDatasets =
							ImageDatasetReader.readImageDatasetChannels(imageFile.getAbsolutePath(), null,bMergeChannels);
						fdfos = ClientRequestManager.createFDOSWithChannels(imageDatasets,new Integer(userPreferredTime));
					}
					
				}else if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FIELDDATA){
					getClientTaskStatusSupport().setMessage("Reading data from VCell server.");
					VCDocument.GeomFromFieldDataCreationInfo docInfo = (VCDocument.GeomFromFieldDataCreationInfo)documentCreationInfo;
					hashTable.put(IMPORT_SOURCE_NAME,
							"FieldData: "+docInfo.getExternalDataID().getName()+" varName="+
							docInfo.getVarName()+" timeIndex="+docInfo.getTimeIndex());
					hashTable.put(INITIAL_ANNOTATION, hashTable.get(IMPORT_SOURCE_NAME));
					PDEDataContext pdeDataContext =	getMdiManager().getFieldDataWindowManager().getPDEDataContext(docInfo.getExternalDataID());
					pdeDataContext.setVariableNameAndTime(docInfo.getVarName(), pdeDataContext.getTimePoints()[docInfo.getTimeIndex()]);
					CartesianMesh mesh = pdeDataContext.getCartesianMesh();
					ISize meshISize = new ISize(mesh.getSizeX(),mesh.getSizeY(),mesh.getSizeZ());
					double[] data = pdeDataContext.getDataValues();
					double minValue = Double.POSITIVE_INFINITY;
					double maxValue = Double.NEGATIVE_INFINITY;
					for (int i = 0; i < data.length; i++) {
						minValue = Math.min(minValue,data[i]);
						maxValue = Math.max(maxValue,data[i]);
					}
					short[] dataToSegment = new short[data.length];
					double scaleShort = Math.pow(2, Short.SIZE)-1;
					for (int i = 0; i < data.length; i++) {
						dataToSegment[i]|= (int)((data[i]-minValue)/(maxValue-minValue)*scaleShort);
					}
					fdfos = new FieldDataFileOperationSpec();
					fdfos.origin = mesh.getOrigin();
					fdfos.extent = mesh.getExtent();
					fdfos.isize = meshISize;
					fdfos.shortSpecData = new short[][][] {{dataToSegment}};

				}else if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FROM_SCRATCH){
					try{
						do{
							String result = "256,256,1";
							result = DialogUtils.showInputDialog0(guiParent,
									"Enter number of pixels for x,y,z to start", result);
							String tempResult = result;
							try{
								if(result == null || result.length() == 0){
									result = "";
									throw new Exception("No size values entered.");
								}
								int xsize = Integer.parseInt(tempResult.substring(0, tempResult.indexOf(",")));
								tempResult = tempResult.substring(tempResult.indexOf(",")+1, tempResult.length());
								int ysize = Integer.parseInt(tempResult.substring(0, tempResult.indexOf(",")));
								tempResult = tempResult.substring(tempResult.indexOf(",")+1, tempResult.length());
								int zsize = Integer.parseInt(tempResult);
								int totalSize = xsize*ysize*zsize;
								final int SCRATCH_SIZE_LIMIT = 512*512*20;
								if(totalSize <=0 || totalSize > (SCRATCH_SIZE_LIMIT)){
									throw new Exception("Total pixels (x*y*z) cannot be <=0 or >"+SCRATCH_SIZE_LIMIT+".");
								}
								fdfos = new FieldDataFileOperationSpec();
								fdfos.origin = new Origin(0, 0, 0);
								fdfos.extent = new Extent(1, 1, 1);
								fdfos.isize = new ISize(xsize, ysize, zsize);
								hashTable.put(IMPORT_SOURCE_NAME,
										"Scratch: New Geometry");
								break;
							}catch(Exception e){
								DialogUtils.showErrorDialog(guiParent, "Error entering starting sizes\n"+e.getMessage(), e);
							}
						}while(true);
					}catch(UtilCancelException e2){
						throw UserCancelException.CANCEL_GENERIC;
					}
				}
				hashTable.put(FDFOS, fdfos);
			} catch (DataFormatException ex) {
				throw new Exception("Cannot read image file.\n"+ex.getMessage());
			}
		}
	};
	AsynchClientTask finishTask = new AsynchClientTask("Finishing...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(final Hashtable<String, Object> hashTable) throws Exception {
			getClientTaskStatusSupport().setMessage("Initializing...");
			final ROIMultiPaintManager roiMultiPaintManager = new ROIMultiPaintManager();
			roiMultiPaintManager.initROIData((FieldDataFileOperationSpec)hashTable.get(FDFOS));
			final Geometry[] geomHolder = new Geometry[1];
			AsynchClientTask task1 = new AsynchClientTask("edit geometry", AsynchClientTask.TASKTYPE_SWING_BLOCKING, false) {
				
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					geomHolder[0] = roiMultiPaintManager.showGUI(
							okButtonText,
							(String)hashTable.get(IMPORT_SOURCE_NAME),
							(Component)hashTable.get(GUI_PARENT),
							(String)hashTable.get(INITIAL_ANNOTATION)
						);
				}
			};
			AsynchClientTask task2 = new AsynchClientTask("update geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					//Create default name for image
					geomHolder[0].getGeometrySpec().getImage().setName("img_"+ClientRequestManager.generateDateTimeString());
					//cause update in this thread so later swing threads won't be delayed
					geomHolder[0].precomputeAll();
					hashTable.put("doc", geomHolder[0]);
				}
			};
			
			AsynchClientTask[] finalTasks = afterTasks;
			if(finalTasks == null){
				finalTasks = new AsynchClientTask[] {
						getSaveImageAndGeometryTask()};
			}
			AsynchClientTask[] tasks = new AsynchClientTask[2 + finalTasks.length];
			tasks[0] = task1;
			tasks[1] = task2;
			System.arraycopy(finalTasks, 0, tasks, 2, finalTasks.length);
			ClientTaskDispatcher.dispatch(
					(Component)hashTable.get(GUI_PARENT),
					hashTable,tasks, false, false, null, true);
		}
	};
	Vector<AsynchClientTask> tasksV = new Vector<AsynchClientTask>();
	if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_DBIMAGE){
		tasksV.addAll(Arrays.asList(new AsynchClientTask[] {loadImgFromDBTask,parseImageTask,finishTask}));
	}else if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FROM_SCRATCH){
		tasksV.addAll(Arrays.asList(new AsynchClientTask[] {parseImageTask,finishTask}));
	}else if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FILE){
		tasksV.addAll(Arrays.asList(new AsynchClientTask[] {selectImageFileTask,parseImageTask,finishTask}));
	}else if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FIELDDATA){
		tasksV.addAll(Arrays.asList(new AsynchClientTask[] {parseImageTask,finishTask}));
	}
	return tasksV.toArray(new AsynchClientTask[0]);
}
/**
 * Insert the method's description here.
 * Creation date: (5/10/2004 3:48:16 PM)
 */
public AsynchClientTask[] createNewDocument(final TopLevelWindowManager requester, final VCDocument.DocumentCreationInfo documentCreationInfo) {//throws UserCancelException, Exception {
	/* asynchronous and not blocking any window */
	AsynchClientTask[] taskArray =  null;

	final int createOption = documentCreationInfo.getOption();
	switch (documentCreationInfo.getDocumentType()) {
		case VCDocument.BIOMODEL_DOC: {		
			AsynchClientTask task1 = new AsynchClientTask("creating biomodel", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					BioModel bioModel = createDefaultBioModelDocument();
					hashTable.put("doc", bioModel);
				}			
			};
			taskArray = new AsynchClientTask[] {task1};
			break;
		}
		case VCDocument.MATHMODEL_DOC: {
			if ((createOption == VCDocument.MATH_OPTION_NONSPATIAL) || (createOption == VCDocument.MATH_OPTION_SPATIAL_EXISTS)) {
				AsynchClientTask task1 = new AsynchClientTask("asking for geometry", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {		
						// spatial or non-spatial
						if (createOption == VCDocument.MATH_OPTION_SPATIAL_EXISTS) {
							GeometryInfo geometryInfo = (GeometryInfo)getMdiManager().getDatabaseWindowManager().selectDocument(VCDocument.GEOMETRY_DOC, getMdiManager().getDatabaseWindowManager());
							hashTable.put("geometryInfo", geometryInfo);
						}
					}
				};
				AsynchClientTask task2 = new AsynchClientTask("creating mathmodel", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						Geometry geometry = null;
						if (createOption == VCDocument.MATH_OPTION_NONSPATIAL) {
							geometry = new Geometry("Untitled", 0);
						} else {
							GeometryInfo geometryInfo = (GeometryInfo)hashTable.get("geometryInfo");
							geometry = (Geometry)getDocumentManager().getGeometry(geometryInfo);
						}
						MathModel mathModel = createMathModel("Untitled", geometry);
						mathModel.setName("MathModel" + (getMdiManager().getNewlyCreatedDesktops() + 1));
						hashTable.put("doc", mathModel);
					}
				};
				taskArray = new AsynchClientTask[] {task1, task2};
				break;
			}else if (createOption == VCDocument.MATH_OPTION_FROMBIOMODELAPP){
			
				AsynchClientTask task1 = new AsynchClientTask("select biomodel application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {		
						// spatial or non-spatial
						BioModelInfo bioModelInfo = getMdiManager().getDatabaseWindowManager().selectBioModelInfo();
						if (bioModelInfo != null) { // may throw UserCancelException
							hashTable.put("bioModelInfo", bioModelInfo);
						}
					}
				};
				AsynchClientTask task2 = new AsynchClientTask("create math model from biomodel application", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {		
						// spatial or non-spatial
						// Get the simContexts in the corresponding BioModel 
						BioModelInfo bioModelInfo = (BioModelInfo)hashTable.get("bioModelInfo");						
						SimulationContext[] simContexts = getDocumentManager().getBioModel(bioModelInfo).getSimulationContexts();
						if (simContexts != null) { // may throw UserCancelException
							hashTable.put("simContexts", simContexts);
						}
					}
				};
				AsynchClientTask task3 = new AsynchClientTask("create math model from biomodel application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {								
						SimulationContext[] simContexts = (SimulationContext[])hashTable.get("simContexts");
						String[] simContextNames = new String[simContexts.length];
						
						if (simContextNames.length == 0) {
							throw new RuntimeException("no application is available");
						} else {
							for (int i = 0; i < simContexts.length; i++){
								simContextNames[i] = simContexts[i].getName();
							}
							// Get the simContext names, so that user can choose which simContext math to import
							String simContextChoice = (String)PopupGenerator.showListDialog(getMdiManager().getDatabaseWindowManager().getComponent(), simContextNames, "Please select Application");
							if (simContextChoice == null) {
								throw UserCancelException.CANCEL_DB_SELECTION;
							}
							SimulationContext chosenSimContext = null;
							for (int i = 0; i < simContexts.length; i++){
								if (simContexts[i].getName().equals(simContextChoice)) {
									chosenSimContext = simContexts[i];
									break;
								}
							}
							BioModelInfo bioModelInfo = (BioModelInfo)hashTable.get("bioModelInfo");
							//Get corresponding mathDesc to create new mathModel and return.
							String newName = bioModelInfo.getVersion().getName()+"_"+chosenSimContext.getName();
							MathDescription bioMathDesc = chosenSimContext.getMathDescription();
							MathDescription newMathDesc = null;
							newMathDesc = new MathDescription(newName+"_"+(new Random()).nextInt());

							newMathDesc.setGeometry(bioMathDesc.getGeometry());
							newMathDesc.read_database(new CommentStringTokenizer(bioMathDesc.getVCML_database()));
							newMathDesc.isValid();							
							
							MathModel newMathModel = new MathModel(null);
							newMathModel.setName(newName);
							newMathModel.setMathDescription(newMathDesc);
							hashTable.put("doc", newMathModel);
						}						
					}
				};
				taskArray = new AsynchClientTask[] {task1, task2, task3};
				break;
			} else {
				throw new RuntimeException("Unknown MathModel Document creation option value="+documentCreationInfo.getOption());
			}			
		}
		case VCDocument.GEOMETRY_DOC: {
			if (createOption == VCDocument.GEOM_OPTION_1D ||
					createOption == VCDocument.GEOM_OPTION_2D ||
					createOption == VCDocument.GEOM_OPTION_3D) {
				// analytic
				AsynchClientTask task1 = new AsynchClientTask("creating analytic geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						Geometry geometry = new Geometry("Geometry" + (getMdiManager().getNewlyCreatedDesktops() + 1), documentCreationInfo.getOption());
						geometry.getGeometrySpec().addSubVolume(new AnalyticSubVolume("subdomain0",new Expression(1.0)));
						geometry.precomputeAll();
						hashTable.put("doc", geometry);
					}
				};
				taskArray = new AsynchClientTask[] {task1};
				break;
			} else  {
				throw new RuntimeException("Unknown Geometry Document creation option value="+documentCreationInfo.getOption());
			}
		}
		default: {
			throw new RuntimeException("Unknown default document type: " + documentCreationInfo.getDocumentType());
		}
	}
	return taskArray;
}

private static final int MAX_NUMBER_OF_COLORS_IMPORTED_FILE = 256;
public static VCImage createVCImageFromUnsignedShorts(short[] dataToSegment,Extent extent,ISize isize,BitSet uniquePixelBS) throws Exception{
	//auto segment

	int minVal = dataToSegment[0]&0x0000FFFF;
	int maxVal = minVal;
	for (int i = 0; i < dataToSegment.length; i++) {
		int usIntVal = (int)(dataToSegment[i]&0x0000FFFF);
		minVal = Math.min(usIntVal, minVal);
		maxVal = Math.max(usIntVal, maxVal);
	}
	byte[] byteData = new byte[dataToSegment.length];

	if(maxVal >= MAX_NUMBER_OF_COLORS_IMPORTED_FILE){
		if(uniquePixelBS.cardinality() <= MAX_NUMBER_OF_COLORS_IMPORTED_FILE){
			int index = 0;
			int[] indexRef = new int[(int)Math.pow(2, Short.SIZE)];
			Arrays.fill(indexRef, -1);
			for (int i = 0; i < indexRef.length; i++) {
				if(uniquePixelBS.get(i)){
					indexRef[i] = index;
					index++;
				}
			}
			for (int i = 0; i < dataToSegment.length; i++) {
				byteData[i] = (byte)indexRef[(int)(dataToSegment[i]&0x0000FFFF)];
			}
		}else{
			for (int i = 0; i < dataToSegment.length; i++) {
				byteData[i] =
					(byte)(0xFF & (int)((double)(dataToSegment[i]-minVal)/(double)(maxVal-minVal)*(MAX_NUMBER_OF_COLORS_IMPORTED_FILE-1)));
			}
		}
	}else{
		for (int i = 0; i < byteData.length; i++) {
			byteData[i] = (byte)(dataToSegment[i]&0xFF);
		}
	}
	VCImage autoSegmentVCImage =
		new VCImageUncompressed(null,byteData,extent,isize.getX(),isize.getY(),isize.getZ());
	return autoSegmentVCImage;

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
				AsynchClientTask task1 = new AsynchClientTask(CurateSpec.CURATE_TYPE_ACTIONS[curateType]+" document...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						if (documentInfo instanceof BioModelInfo) {
							getDocumentManager().curate(new CurateSpec((BioModelInfo)documentInfo,curateType));
						} else if (documentInfo instanceof MathModelInfo) {
							getDocumentManager().curate(new CurateSpec((MathModelInfo)documentInfo,curateType));
						} else {
							throw new RuntimeException(CurateSpec.CURATE_TYPE_ACTIONS[curateType]+" not supported for VCDocumentInfo type "+documentInfo.getClass().getName());
						}
					}
				};
				ClientTaskDispatcher.dispatch(requester.getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] {task1}, false);
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


public void updateUserRegistration(final DocumentWindowManager currWindowManager, final boolean bNewUser){
	try {
		UserRegistrationOP.registrationOperationGUI(this, currWindowManager, 
				getClientServerManager().getClientServerInfo(),
				(bNewUser?LoginDialog.USERACTION_REGISTER:LoginDialog.USERACTION_EDITINFO),
				(bNewUser?null:getClientServerManager()));
	} catch (UserCancelException e) {
		return;
	} catch (Exception e) {
		e.printStackTrace();
		PopupGenerator.showErrorDialog(currWindowManager, (bNewUser?"Create new":"Update")+" user Registration error:\n"+e.getMessage(), e);
		return;
	}
}

public void sendLostPassword(final DocumentWindowManager currWindowManager, final String userid){
	try {
		UserRegistrationOP.registrationOperationGUI(this, currWindowManager,
				VCellClient.createClientServerInfo(
					getClientServerManager().getClientServerInfo(), userid, null),
				LoginDialog.USERACTION_LOSTPASSWORD,
				null);
	} catch (Exception e) {
		e.printStackTrace();
		PopupGenerator.showErrorDialog(currWindowManager, "Update user Registration error:\n" + e.getMessage(), e);
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
				AsynchClientTask task1 = new AsynchClientTask("Deleting document...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						if (documentInfo instanceof BioModelInfo) {
							getDocumentManager().delete((BioModelInfo)documentInfo);
						} else if (documentInfo instanceof MathModelInfo) {
							getDocumentManager().delete((MathModelInfo)documentInfo);
						} else if (documentInfo instanceof GeometryInfo) {
							getDocumentManager().delete((GeometryInfo)documentInfo);
						} else {
							throw new RuntimeException("delete not supported for VCDocumentInfo type "+documentInfo.getClass().getName());
						}
					}
				};
				ClientTaskDispatcher.dispatch(requester.getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] {task1}, false);
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
protected void downloadExportedData(final TopLevelWindowManager requester, final ExportEvent evt) {
	URL location = null;
	try {
		location = new URL(evt.getLocation());
	} catch (java.net.MalformedURLException exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(requester.getComponent(), "Reported file location does not seem to be a valid URL\n"+exc.getMessage());
		return;
	}
	final URL url = location;
	AsynchClientTask task1 = new AsynchClientTask("Retrieving data from "+url, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			// get it
		    URLConnection connection = url.openConnection();
		    byte[] bytes = new byte[connection.getContentLength()];
		    java.io.InputStream is = connection.getInputStream();
		    int bytesRead = 0;
		    int offset = 0;
		    while (bytesRead >= 0 && offset<(bytes.length-1)) {
		        //System.out.println("offset: " + offset + ", bytesRead: " + bytesRead);
		        bytesRead = is.read(bytes, offset, bytes.length - offset);
		        offset += bytesRead;
		    }
		    is.close();
		    hashTable.put("bytes", bytes);
		}		
	};
	AsynchClientTask task2 = new AsynchClientTask("selecting file to save", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			String defaultPath = getUserPreferences().getGenPref(UserPreferences.GENERAL_LAST_PATH_USED);
			final VCFileChooser fileChooser = new VCFileChooser(defaultPath);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setMultiSelectionEnabled(false);
		    String name = evt.getVCDataIdentifier().getID();
		    String suffix = null;
		    if(evt.getLocation().toLowerCase().endsWith(".mov")){
				fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_MOV);
				fileChooser.setFileFilter(FileFilters.FILE_FILTER_MOV);

		    	suffix = "_exported.mov";
		    }else{
				fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_ZIP);
				fileChooser.setFileFilter(FileFilters.FILE_FILTER_ZIP);

		    	suffix = "_exported.zip";
		    }
		    File file = new File(name + suffix);
		    if (file.exists()) {
			    int count = 0;
			    do {
			    	file = new File(name + "_" + count + suffix);
			    	count++;
			    } while (file.exists());
		    }

		    fileChooser.setSelectedFile(file);
			fileChooser.setDialogTitle("Save exported dataset...");
			int approve = fileChooser.showSaveDialog(requester.getComponent());
			if (approve == JFileChooser.APPROVE_OPTION) {
				hashTable.put("selectedFile", fileChooser.getSelectedFile());
			} else {
				fileChooser.setSelectedFile(null);
			}
		}
	};
	AsynchClientTask task3 = new AsynchClientTask("saving to file", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {			
			File selectedFile = (File)hashTable.get("selectedFile");
			if (selectedFile == null) {
				return;
			}
			String defaultPath = getUserPreferences().getGenPref(UserPreferences.GENERAL_LAST_PATH_USED);
			String newPath = selectedFile.getParent();
	        if (!newPath.equals(defaultPath)) {
				getUserPreferences().setGenPref(UserPreferences.GENERAL_LAST_PATH_USED, newPath);
	        }
//	        System.out.println("New preferred file path: " + newPath + ", Old preferred file path: " + defaultPath);
			//
			if (selectedFile.exists()) {
				String question = PopupGenerator.showWarningDialog(requester.getComponent(), getUserPreferences(), UserMessage.warn_OverwriteFile,selectedFile.getAbsolutePath());
				if (question.equals(UserMessage.OPTION_CANCEL)){
					return;
				}
			}
			byte[] bytes = (byte[])hashTable.get("bytes");
            FileOutputStream fo = new FileOutputStream(selectedFile);
            fo.write(bytes);
            fo.close();
		}
	};
	ClientTaskDispatcher.dispatch(requester.getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2, task3}, false);
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
	Hashtable<String,Object> hash = new Hashtable<String,Object>();
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
public void exportMessage(ExportEvent event) {
	if (event.getEventTypeID() == ExportEvent.EXPORT_COMPLETE) {
		// update document manager
		//try {
			//((ClientDocumentManager)getRequestManager().getDocumentManager()).reloadExportLog(exportEvent.getVCDataIdentifier());
		//}catch (Throwable e){
			//e.printStackTrace(System.out);
		//}
		// try to download the thing
		downloadExportedData(getMdiManager().getFocusedWindowManager(), event);
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 4:45:51 PM)
 * @return cbit.vcell.client.AsynchMessageManager
 */
public AsynchMessageManager getAsynchMessageManager() {
	return getClientServerManager().getAsynchMessageManager();
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:21:50 AM)
 */
private ClientServerManager getClientServerManager() {
	// shorthand
	return getVcellClient().getClientServerManager();
}

public ConnectionStatus getConnectionStatus(){
	return getClientServerManager().getConnectionStatus();
}
/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 10:53:47 AM)
 * @return cbit.vcell.desktop.controls.DataManager
 * @param vcDataIdentifier cbit.vcell.server.VCDataIdentifier
 */
public DataManager getDataManager(OutputContext outputContext, VCDataIdentifier vcDataId, boolean isSpatial) throws DataAccessException {
	//
	// Create ODE or PDE or Merged Datamanager depending on ODE or PDE or Merged data.
	//
	DataManager dataManager = null;
	VCDataManager vcDataManager = getClientServerManager().getVCDataManager();
	if (isSpatial) {
		dataManager = new PDEDataManager(outputContext,vcDataManager, vcDataId);
	} else {
		dataManager = new ODEDataManager(outputContext,vcDataManager, vcDataId);
	}
//	dataManager.connect();
	return dataManager;
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 9:57:39 AM)
 * @return cbit.vcell.clientdb.DocumentManager
 */
public DocumentManager getDocumentManager() {
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
public MergedDatasetViewerController getMergedDatasetViewerController(OutputContext outputContext,VCDataIdentifier vcdId,boolean expectODEData) throws DataAccessException {
	if (vcdId instanceof MergedDataInfo) {
		DataManager dataManager = getDataManager(outputContext,vcdId, !expectODEData);
		return new MergedDatasetViewerController(dataManager);
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
public DataViewerController getDataViewerController(OutputContext outputContext, Simulation simulation, int jobIndex) throws DataAccessException {
	VCSimulationIdentifier vcSimulationIdentifier = simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
	final VCDataIdentifier vcdataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, jobIndex);	
	DataManager dataManager = getDataManager(outputContext,vcdataIdentifier, simulation.isSpatial());
	return new SimResultsViewerController(dataManager, simulation);
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
public AsynchClientTask[] newDocument(TopLevelWindowManager requester,
		final VCDocument.DocumentCreationInfo documentCreationInfo) {
	
	AsynchClientTask createNewDocumentTask =
		new AsynchClientTask("Creating New Document", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			VCDocument doc = (VCDocument)hashTable.get("doc");
			DocumentWindowManager windowManager = createDocumentWindowManager(doc);
			getMdiManager().createNewDocumentWindow(windowManager);
		}
	};

	if(documentCreationInfo.getDocumentType() == VCDocument.MATHMODEL_DOC &&
			documentCreationInfo.getOption() == VCDocument.MATH_OPTION_SPATIAL_NEW){
		final AsynchClientTask createSpatialMathModelTask = new AsynchClientTask("creating mathmodel", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				Geometry geometry = null;
				geometry = (Geometry)hashTable.get("doc");
				MathModel mathModel = createMathModel("Untitled", geometry);
				mathModel.setName("MathModel" + (getMdiManager().getNewlyCreatedDesktops() + 1));
				hashTable.put("doc", mathModel);
			}
		};
		
		getMdiManager().getDatabaseWindowManager().createGeometry(
				null, new AsynchClientTask[] {createSpatialMathModelTask,createNewDocumentTask},
				"Choose geometry type to start MathModel creation","Create MathModel");
		return null;
	}

	/* asynchronous and not blocking any window */
	AsynchClientTask[] taskArray = null;
	if(documentCreationInfo.getPreCreatedDocument() == null){
		AsynchClientTask[] taskArray1 =  createNewDocument(requester, documentCreationInfo);
		taskArray = new AsynchClientTask[taskArray1.length + 1];
		System.arraycopy(taskArray1, 0, taskArray, 0, taskArray1.length);	
	}else{
		taskArray = new AsynchClientTask[2];
		taskArray[0] = new AsynchClientTask("Setting document...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				hashTable.put("doc", documentCreationInfo.getPreCreatedDocument());
			}
		};
	}
	
	taskArray[taskArray.length-1] = createNewDocumentTask;
	return taskArray;
}

private static VCImage saveImageAutoName(RequestManager requestManager,VCImage vcImage) throws Exception{
	VCImageInfo[] imageInfos = null;
	try {
		imageInfos = requestManager.getDocumentManager().getImageInfos();
	}catch (DataAccessException e){
		e.printStackTrace(System.out);
	}
	String newName = null;
	boolean bNameIsGood = false;
//	Calendar calendar = Calendar.getInstance();
	newName = "image_"+ClientRequestManager.generateDateTimeString();
	while (!bNameIsGood){
		if (imageInfos==null){
			bNameIsGood = true; // if no image information assume image name is good
		}else{	
			boolean bNameExists = false;
			for (int i = 0; i < imageInfos.length; i++){
				if (imageInfos[i].getVersion().getName().equals(newName)){
					bNameExists = true;
					break;
				}
			}
			if(!bNameExists){
				bNameIsGood = true;
			}
		}
		if(!bNameIsGood){
			newName = TokenMangler.getNextEnumeratedToken(newName);
		}
	}

	return requestManager.getDocumentManager().saveAsNew(vcImage,newName);					

}

public static String generateDateTimeString(){
	Calendar calendar = Calendar.getInstance();
	int year = calendar.get(Calendar.YEAR);
	int month = calendar.get(Calendar.MONTH)+1;
	int day = calendar.get(Calendar.DAY_OF_MONTH);
	int hour = calendar.get(Calendar.HOUR_OF_DAY);
	int min = calendar.get(Calendar.MINUTE);
	int sec = calendar.get(Calendar.SECOND);
	String imageName =
	year+""+
	(month < 10?"0"+month:month)+""+
	(day < 10?"0"+day:day)+
	"_"+
	(hour < 10?"0"+hour:hour)+""+
	(min < 10?"0"+min:min)+""+
	(sec < 10?"0"+sec:sec);

	return imageName;
}
private AsynchClientTask getSaveImageAndGeometryTask(){
	
	final AsynchClientTask saveImageAndGeometryTask =  new AsynchClientTask("creating geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			getClientTaskStatusSupport().setMessage("Getting new Geometry name...");
			String newGeometryName = null;
			while(true){
			newGeometryName =
				ClientRequestManager.this.getMdiManager().getDatabaseWindowManager().showSaveDialog(
						((Geometry)hashTable.get("doc")).getDocumentType(),
						(Component)hashTable.get(ClientRequestManager.GUI_PARENT),
						(newGeometryName==null?"NewGeometry":newGeometryName));
				if (newGeometryName == null || newGeometryName.trim().length()==0){
					newGeometryName = null;
					DialogUtils.showWarningDialog((Component)hashTable.get(ClientRequestManager.GUI_PARENT),
							"New Geometry name cannot be empty.");
					continue;
				}
				//Check name conflict
				GeometryInfo[] geometryInfos = ClientRequestManager.this.getDocumentManager().getGeometryInfos();
				boolean bNameConflict = false;
				for (int i = 0; i < geometryInfos.length; i++) {
					if(geometryInfos[i].getVersion().getOwner().equals(ClientRequestManager.this.getDocumentManager().getUser())){
						if(geometryInfos[i].getVersion().getName().equals(newGeometryName)){
							bNameConflict = true;
							break;
						}
					}
				}
				if(bNameConflict){
					DialogUtils.showWarningDialog((Component)hashTable.get(ClientRequestManager.GUI_PARENT),
					"A Geometry with name "+newGeometryName+" already exists.  Choose a different name.");
					continue;
				}else{
					break;
				}
			}
			getClientTaskStatusSupport().setMessage("Saving image portion of Geometry...");
			saveImageAutoName(ClientRequestManager.this, ((Geometry)hashTable.get("doc")).getGeometrySpec().getImage());
			getClientTaskStatusSupport().setMessage("Saving final Geometry...");
			ClientRequestManager.this.getDocumentManager().saveAsNew((Geometry)hashTable.get("doc"), newGeometryName);
		}
	};
	
	return saveImageAndGeometryTask;
}

/**
 * onVCellMessageEvent method comment.
 */
public void onVCellMessageEvent(final VCellMessageEvent event) {
	if (event.getEventTypeID() == VCellMessageEvent.VCELL_MESSAGEEVENT_TYPE_BROADCAST) {
	    PopupGenerator.showErrorDialog(getMdiManager().getFocusedWindowManager(), event.getMessageData().getData().toString());
	}
}

/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 9:37:46 PM)
 */
private void openAfterChecking(final VCDocumentInfo documentInfo, final TopLevelWindowManager requester, final boolean inNewWindow) {

	/* asynchronous and not blocking any window */
	bOpening = true;
	
	// start a thread that gets it and updates the GUI by creating a new document desktop
	String taskName = null;
	if (documentInfo instanceof XMLInfo) {
		taskName = "Importing XML document";
	} else {
		taskName = "Loading document '" + documentInfo.getVersion().getName() + "' from database";
	}
	
	AsynchClientTask task0 = new AsynchClientTask(taskName, AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if (! inNewWindow) {
				// request was to replace the document in an existing window
				getMdiManager().blockWindow(requester.getManagerID());
			}
		}
	};
	AsynchClientTask task1 = new AsynchClientTask(taskName, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			VCDocument doc = null;
			if (documentInfo instanceof BioModelInfo) {
				BioModelInfo bmi = (BioModelInfo)documentInfo;
				doc = getDocumentManager().getBioModel(bmi);
			} else if (documentInfo instanceof MathModelInfo) {
				MathModelInfo mmi = (MathModelInfo)documentInfo;
				doc = getDocumentManager().getMathModel(mmi);
			} else if (documentInfo instanceof GeometryInfo) {
				GeometryInfo gmi = (GeometryInfo)documentInfo;
				doc = getDocumentManager().getGeometry(gmi);
			} else if (documentInfo instanceof XMLInfo) {
				XMLInfo xmlInfo = (XMLInfo)documentInfo;
				org.jdom.Element rootElement = xmlInfo.getXmlDoc().getRootElement();
				String xmlType = rootElement.getName();
				String modelXmlType = null;
				if (xmlType.equals(XMLTags.VcmlRootNodeTag)) {
					// For now, assuming that <vcml> element has only one child (biomodel, mathmodel or geometry). 
					// Will deal with multiple children of <vcml> Element when we get to model composition.
					List<Element> childElementList = rootElement.getChildren();
					Element modelElement = childElementList.get(0);	// assuming first child is the biomodel, mathmodel or geometry.
					modelXmlType = modelElement.getName();
				}
				if (xmlType.equals(XMLTags.BioModelTag) || (xmlType.equals(XMLTags.VcmlRootNodeTag) && modelXmlType.equals(XMLTags.BioModelTag))) {
					doc = XmlHelper.XMLToBioModel(xmlInfo);
				} else if (xmlType.equals(XMLTags.MathModelTag) || (xmlType.equals(XMLTags.VcmlRootNodeTag) && modelXmlType.equals(XMLTags.MathModelTag))) {
					doc = XmlHelper.XMLToMathModel(xmlInfo);					
				} else if (xmlType.equals(XMLTags.GeometryTag) || (xmlType.equals(XMLTags.VcmlRootNodeTag) && modelXmlType.equals(XMLTags.GeometryTag))) {
					doc = XmlHelper.XMLToGeometry(xmlInfo);
				} else if (xmlType.equals(XMLTags.SbmlRootNodeTag)) {
					Namespace namespace = rootElement.getNamespace(XMLTags.SBML_SPATIAL_NS_PREFIX);
					boolean bIsSpatial = (namespace==null) ? false : true; 
					TranslationLogger transLogger = new TranslationLogger(requester);
					doc = XmlHelper.importSBML(transLogger, xmlInfo, bIsSpatial);
				} else if (xmlType.equals(XMLTags.CellmlRootNodeTag)) {
					if (requester instanceof BioModelWindowManager){
						TranslationLogger transLogger = new TranslationLogger(requester);
						doc = XmlHelper.importBioCellML(transLogger, xmlInfo);
					}else{
						TranslationLogger transLogger = new TranslationLogger(requester);
						doc = XmlHelper.importMathCellML(transLogger, xmlInfo);
					}
				} else if (xmlType.equals(MicroscopyXMLTags.FRAPStudyTag)) {
					doc = XmlHelper.VFRAPToBioModel(hashTable, xmlInfo, getDocumentManager(), requester);
				} else { // unknown XML format
					throw new RuntimeException("unsupported XML format, first element tag is <"+rootElement.getName()+">");
				}
				if(xmlInfo.getDefaultName() != null){
					doc.setName(xmlInfo.getDefaultName());
				}
			}
			requester.prepareDocumentToLoad(doc, inNewWindow);
			hashTable.put("doc", doc);
		}
	};
		
	AsynchClientTask task2 = new AsynchClientTask("Showing document", AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, false) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			try {
				Throwable exc = (Throwable)hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR);
				if (exc == null) {
					VCDocument doc = (VCDocument)hashTable.get("doc");
					DocumentWindowManager windowManager = null;
					if (inNewWindow) {
						windowManager = createDocumentWindowManager(doc);
						// request was to create a new top-level window with this doc
						getMdiManager().createNewDocumentWindow(windowManager);						
//						if (windowManager instanceof BioModelWindowManager) {
//							((BioModelWindowManager)windowManager).preloadApps();
//						}
					} else {
						// request was to replace the document in an existing window
						windowManager = (DocumentWindowManager)requester;
						getMdiManager().setCanonicalTitle(requester.getManagerID());
						windowManager.resetDocument(doc);
					}
				}
			} finally {
				if (!inNewWindow) {
					getMdiManager().unBlockWindow(requester.getManagerID());
				}
				bOpening = false;
			}
		}		
	};
	ClientTaskDispatcher.dispatch(requester.getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[]{task0, task1, task2}, false);
}

private DocumentWindowManager createDocumentWindowManager(final VCDocument doc){
	JPanel newJPanel = new JPanel();
	if(doc instanceof BioModel){
		return new BioModelWindowManager(newJPanel, ClientRequestManager.this, (BioModel)doc, getMdiManager().getNewlyCreatedDesktops());
	}else if(doc instanceof MathModel){
		return new MathModelWindowManager(newJPanel, ClientRequestManager.this, (MathModel)doc, getMdiManager().getNewlyCreatedDesktops());
	}else if(doc instanceof Geometry){
		return new GeometryWindowManager(newJPanel, ClientRequestManager.this, (Geometry)doc, getMdiManager().getNewlyCreatedDesktops());
	}
	throw new RuntimeException("Unknown VCDocument type "+doc);
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
		boolean inNewWindow = isDifferentFromBlank(documentType, requester.getVCDocument());
		openDocument(documentInfo, requester, inNewWindow);
	} catch (UserCancelException uexc) {
		System.out.println(uexc);
		return;
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(requester, "Open document failed\n" + exc.getMessage(), exc);
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
	if (documentInfo == null) {
		return;
	}
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
	}
	openAfterChecking(documentInfo, requester, inNewWindow);
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
public void reconnect(final TopLevelWindowManager requester) {
	// asynch & nothing to do on Swing queue (updates handled by events)
	AsynchClientTask task1 = new AsynchClientTask("reconnect", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				getClientServerManager().reconnect(requester);
				
			}
	};
	ClientTaskDispatcher.dispatch(null, new Hashtable<String, Object>(), new AsynchClientTask[] { task1 });
}


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 1:07:09 PM)
 * @param vcDocument cbit.vcell.document.VCDocument
 */
public void revertToSaved(DocumentWindowManager documentWindowManager) {
	// make the info
	VCDocument document = documentWindowManager.getVCDocument();
	VCDocumentInfo info = null;
	try {
		KeyValue versionKey = document.getVersion().getVersionKey();
		switch (document.getDocumentType()) {
			case VCDocument.BIOMODEL_DOC: {
				info = getDocumentManager().getBioModelInfo(versionKey);
				break;
			}
			case VCDocument.MATHMODEL_DOC: {
				info = getDocumentManager().getMathModelInfo(versionKey);
				break;
			}
			case VCDocument.GEOMETRY_DOC: {
				info = getDocumentManager().getGeometryInfo(versionKey);
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

	// Before running the simulation, check if all the sizes of structures are set
	if(simulations != null && simulations.length > 0)
	{
		VCDocument vcd = documentWindowManager.getVCDocument();
		if(vcd instanceof BioModel)
		{
			String stochChkMsg = null;
			//we want to check when there is stochastic application if the rate laws set in model can be automatically transformed.
			for(int i=0; i<simulations.length; i++)
			{
				if(simulations[i].getMathDescription().isNonSpatialStoch() || simulations[i].getMathDescription().isSpatialStoch())
				{
					if (stochChkMsg == null) {
						stochChkMsg = ((BioModel)vcd).getModel().isValidForStochApp();
					}
					if(!(stochChkMsg.equals("")))
					{
						DialogUtils.showErrorDialog(documentWindowManager.getComponent(), "Problem in simulation: "+simulations[i].getName()+".\n"+stochChkMsg);
						throw new RuntimeException("Problem in simulation: "+simulations[i].getName()+"\n"+stochChkMsg);
					}
				}
			}
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
	Hashtable<String, Object> hash = new Hashtable<String, Object>();
	hash.put("mdiManager", getMdiManager());
	hash.put("documentManager", getDocumentManager());
	hash.put("documentWindowManager", documentWindowManager);
	hash.put("currentDocumentWindow", currentDocumentWindow);
	hash.put("clientSimManager", clientSimManager);
	hash.put("simulations", simulations);
	hash.put("jobManager", getClientServerManager().getJobManager());
	hash.put("requestManager", this);
	
	/* create tasks */
	AsynchClientTask[] tasks = null;
	if (needSaveAs) {
		// check document consistency first
		AsynchClientTask documentValid = new DocumentValid();
		AsynchClientTask setMathDescription = new SetMathDescription();
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
			setMathDescription,
			newName,
			saveDocument,
			finishSave,
			runSims
			};
	} else {
		// check document consistency first
		AsynchClientTask documentValid = new DocumentValid();
		AsynchClientTask setMathDescription = new SetMathDescription();
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
			setMathDescription,
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

public void saveDocument(DocumentWindowManager documentWindowManager, boolean replace) {
	saveDocument(documentWindowManager, replace, null);
}
/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 3:09:25 PM)
 * @param vcDocument cbit.vcell.document.VCDocument
 * @param replace boolean
 */
public void saveDocument(final DocumentWindowManager documentWindowManager, boolean replace, AsynchClientTask closeWindowTask) {
	
	/*	run some quick checks first to validate request to save or save edition */
	if (documentWindowManager.getVCDocument().getVersion() == null) {
		// it can never see this happening before, but check anyway and default to save as
		// but since we can allow user to save during closing, now it can happen
		// (save/save edition buttons should have not been enabled upon document window creation)
		System.out.println("\nIGNORED ERROR: should not have been able to use save/save edition on doc with no version key\n");
		saveDocumentAsNew(documentWindowManager, closeWindowTask);
		return;
	}
	if(!documentWindowManager.getVCDocument().getVersion().getOwner().compareEqual(getDocumentManager().getUser())) {
		// not the owner - this should also not happen, but check anyway...
		// keep the user informed this time
		System.out.println("\nIGNORED ERROR: should not have been able to use save/save edition on doc with different owner\n");
		String choice = PopupGenerator.showWarningDialog(documentWindowManager, getUserPreferences(), UserMessage.warn_SaveNotOwner,null);
		if (choice.equals(UserMessage.OPTION_SAVE_AS_NEW)){
			// user chose to Save As
			saveDocumentAsNew(documentWindowManager, closeWindowTask);
			return;
		} else {
			if (closeWindowTask == null) {
				// user canceled, just show existing document
				getMdiManager().showWindow(documentWindowManager.getManagerID());
				return;
			} else {
				ClientTaskDispatcher.dispatch(documentWindowManager.getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] {closeWindowTask}, false);
			}
		}
	}
		
	/* request is valid, go ahead with save */
	
	/* block document window */
	JFrame currentDocumentWindow = getMdiManager().blockWindow(documentWindowManager.getManagerID());
	/* prepare hashtable for tasks */
	Hashtable<String, Object> hash = new Hashtable<String, Object>();
	hash.put("mdiManager", getMdiManager());
	hash.put("documentManager", getDocumentManager());
	hash.put("documentWindowManager", documentWindowManager);
	hash.put("currentDocumentWindow", currentDocumentWindow);
	hash.put("requestManager", this);
	
	/* create tasks */
	// check document consistency first
	AsynchClientTask documentValid = new DocumentValid();
	AsynchClientTask setMathDescription = new SetMathDescription();
	// check if unchanged document
	AsynchClientTask checkUnchanged = new CheckUnchanged(false);
	// save it
	AsynchClientTask saveDocument = new SaveDocument();
	// clean up
	AsynchClientTask finishSave = new FinishSave();
	// assemble array
	AsynchClientTask[] tasks = null;
	if (replace) {
		// check for lost results
		AsynchClientTask checkBeforeDelete = new CheckBeforeDelete();
		// delete old document
		AsynchClientTask deleteOldDocument = new DeleteOldDocument();
		tasks = new AsynchClientTask[] {
			documentValid,
			setMathDescription,
			checkUnchanged,
			saveDocument,
			checkBeforeDelete,
			deleteOldDocument,
			finishSave
		};
	} else {
		tasks = new AsynchClientTask[] {
			documentValid,
			setMathDescription,
			checkUnchanged,
			saveDocument,
			finishSave
		};
	}
	if (closeWindowTask != null) {
		// replace finishSave 
		tasks[tasks.length - 1] = closeWindowTask;
	}
	/* run tasks */
	ClientTaskDispatcher.dispatch(currentDocumentWindow, hash, tasks, false);
}


public void saveDocumentAsNew(DocumentWindowManager documentWindowManager) {
	saveDocumentAsNew(documentWindowManager, null);
}
/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 3:09:25 PM)
 * @param vcDocument cbit.vcell.document.VCDocument
 */
public void saveDocumentAsNew(DocumentWindowManager documentWindowManager, AsynchClientTask closeWindowTask) {
	
	/* block document window */
	JFrame currentDocumentWindow = getMdiManager().blockWindow(documentWindowManager.getManagerID());
	/* prepare hashtable for tasks */
	Hashtable<String, Object> hash = new Hashtable<String, Object>();
	hash.put("mdiManager", getMdiManager());
	hash.put("documentManager", getDocumentManager());
	hash.put("documentWindowManager", documentWindowManager);
	hash.put("currentDocumentWindow", currentDocumentWindow);
	hash.put("requestManager", this);
	
	/* create tasks */
	// check document consistency first
	AsynchClientTask documentValid = new DocumentValid();
	AsynchClientTask setMathDescription = new SetMathDescription();
	// get a new name
	AsynchClientTask newName = new NewName();
	// save it
	AsynchClientTask saveDocument = new SaveDocument();
	// clean up
	AsynchClientTask finishSave = new FinishSave();
	// assemble array
	AsynchClientTask[] tasks = new AsynchClientTask[] {
		documentValid,
		setMathDescription,
		newName,
		saveDocument,
		finishSave
	};
	
	if (closeWindowTask != null) {
		// replace finishSave 
		tasks[tasks.length - 1] = closeWindowTask;
	}
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
		PopupGenerator.showErrorDialog(requester, "Selection of BioModel failed\n"+exc.getMessage(), exc);
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
		PopupGenerator.showErrorDialog(requester, "Selection of MathModel failed\n" + exc.getMessage(), exc);
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

public void showFieldDataWindow(FieldDataWindowManager.DataSymbolCallBack dataSymbolCallBack) {
	FieldDataWindowManager fdwm = (FieldDataWindowManager)getMdiManager().getWindowManager(ClientMDIManager.FIELDDATA_WINDOW_ID);
	fdwm.getFieldDataGUIPanel().setCreateDataSymbolCallBack(dataSymbolCallBack);
	Window win = (Window)BeanUtils.findTypeParentOfComponent(fdwm.getFieldDataGUIPanel(), Window.class);
	if(win != null){
		win.setVisible(false);
	}
	getMdiManager().showWindow(ClientMDIManager.FIELDDATA_WINDOW_ID);
	
//	if(windowID.equals(ClientMDIManager.FIELDDATA_WINDOW_ID)){
//	FieldDataWindow fdw = (FieldDataWindow)frame;
//	FieldDataGUIPanel fdgp = (FieldDataGUIPanel)fdw.getContentPane().getComponent(0);
//	if(fdgp.getDisplayMode() == FieldDataGUIPanel.DISPLAY_NORMAL){
//		fdgp.setCreateDataSymbolCallBack(new FieldDataGUIPanel.DataSymbolCallBack(){
//			public void createDataSymbol() {
//			}});
//	}else{
//		fdgp.setCreateDataSymbolCallBack(null);
//	}
//}

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
public void startExport(final OutputContext outputContext,final TopLevelWindowManager windowManager, final ExportSpecs exportSpecs) {
	// start a thread to get it; not blocking any window/frame
	AsynchClientTask task1 = new AsynchClientTask("starting exporting", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			getClientServerManager().getJobManager().startExport(outputContext,exportSpecs);
		}
	};
	ClientTaskDispatcher.dispatch(windowManager.getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] { task1 });
}


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 2:29:35 AM)
 * @param documentWindowManager cbit.vcell.client.DocumentWindowManager
 * @param simulations cbit.vcell.solver.Simulation[]
 */
public void stopSimulations(final ClientSimManager clientSimManager, final Simulation[] simulations) {
	// stop is single step operation, don't bother with tasks, thread inline
	AsynchClientTask task1 = new AsynchClientTask("stopping simulations", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			Hashtable<Simulation, Throwable> failures = new Hashtable<Simulation, Throwable>();
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
				hashTable.put("failures", failures);
			}
		}
	};
	
	AsynchClientTask task2 = new AsynchClientTask("stopping simulations", AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, false) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			Hashtable<Simulation,Throwable> failures = (Hashtable<Simulation, Throwable>)hashTable.get("failures");
			if (failures != null && ! failures.isEmpty()) {
				Enumeration<Simulation> en = failures.keys();
				while (en.hasMoreElements()) {
					Simulation sim = (Simulation)en.nextElement();
					Throwable exc = (Throwable)failures.get(sim);
					// notify user
					PopupGenerator.showErrorDialog(clientSimManager.getDocumentWindowManager(), "Failed to dispatch stop request for simulation'"+sim.getName()+"'\n" + exc.getMessage(), exc);
				}
			}
		}
	};
	ClientTaskDispatcher.dispatch(clientSimManager.getDocumentWindowManager().getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2 });	
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 12:10:07 PM)
 */
public void updateStatusNow() {
	// thread safe update of gui
	AsynchClientTask task1 = new AsynchClientTask("updateStatusNow", AsynchClientTask.TASKTYPE_SWING_NONBLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			getVcellClient().getStatusUpdater().updateNow(getVcellClient().getClientServerManager().getConnectionStatus());
			
		}
	};
	ClientTaskDispatcher.dispatch(null, new Hashtable<String, Object>(), new AsynchClientTask[] {task1});
}

public SimInfoHolder[] getOpenDesktopDocumentInfos() throws DataAccessException{
	Vector<SimInfoHolder> simInfoHolderV = new Vector<SimInfoHolder>();
	Enumeration<TopLevelWindowManager> dwmEnum = getMdiManager().getWindowManagers();
	while(dwmEnum.hasMoreElements()){
		TopLevelWindowManager tlwm = dwmEnum.nextElement();
		if(tlwm instanceof DocumentWindowManager){
			DocumentWindowManager dwm = (DocumentWindowManager)tlwm;
			VCDocument vcDoc = dwm.getVCDocument();
			if(vcDoc.getVersion() != null){
				if(vcDoc.getDocumentType() == VCDocument.BIOMODEL_DOC){
					BioModel bioModel =
						getDocumentManager().getBioModel(vcDoc.getVersion().getVersionKey());
					SimulationContext[] simContexts = bioModel.getSimulationContexts();
					for(int i=0;i<simContexts.length;i+= 1){
						if(simContexts[i].getGeometry() == null){
							throw new DataAccessException("Error gathering document info (isCompartmental check failed):\nOpen BioModel document "+bioModel.getName()+" has no Geometry");
						}
						Simulation[] sims = simContexts[i].getSimulations();
						for(int j=0;j<sims.length;j+= 1){
							for(int k=0;k<sims[j].getScanCount();k+= 1){
								FieldDataWindowManager.SimInfoHolder simInfoHolder =
									new FieldDataWindowManager.FDSimBioModelInfo(
											bioModel.getVersion().getVersionKey(),
											simContexts[i].getName(),sims[j].getSimulationInfo(),
											k,
											//!sims[j].getSolverTaskDescription().getSolverDescription().hasVariableTimestep(),
											simContexts[i].getGeometry().getDimension() == 0
									);
								simInfoHolderV.add(simInfoHolder);
							}
						}
					}
				}else if(vcDoc.getDocumentType() == VCDocument.MATHMODEL_DOC) {
					MathModel mathModel =
						getDocumentManager().getMathModel(vcDoc.getVersion().getVersionKey());
					if(mathModel.getMathDescription() == null || mathModel.getMathDescription().getGeometry() == null){
						throw new DataAccessException("Error gathering document info (isCompartmental check failed):\nOpen MathModel document "+mathModel.getName()+" has either no MathDescription or no Geometry");
					}
					Simulation[] sims = mathModel.getSimulations();
					for(int i=0;i<sims.length;i+= 1){
						for(int k=0;k<sims[i].getScanCount();k+= 1){
							FieldDataWindowManager.SimInfoHolder simInfoHolder =
								new FieldDataWindowManager.FDSimMathModelInfo(
										mathModel.getVersion().getVersionKey(),
										sims[i].getSimulationInfo(),
										k,
										//!sims[i].getSolverTaskDescription().getSolverDescription().hasVariableTimestep(),
										mathModel.getMathDescription().getGeometry().getDimension() == 0
								);
							simInfoHolderV.add(simInfoHolder);
						}
					}
				}
			}
		}
		
	}
	SimInfoHolder[] simInfoHolderArr = new SimInfoHolder[simInfoHolderV.size()];
	simInfoHolderV.copyInto(simInfoHolderArr);
	return simInfoHolderArr;
}

public void showComparisonResults(TopLevelWindowManager requester, XmlTreeDiff diffTree, String baselineDesc, String modifiedDesc) {
	TMLPanel comparePanel = new TMLPanel();
	comparePanel.setXmlTreeDiff(diffTree);
	comparePanel.setBaselineVersionDescription(baselineDesc);
	comparePanel.setModifiedVersionDescription(modifiedDesc);
	
	JOptionPane comparePane = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {"Apply Changes", "Close"});
	comparePane.setMessage(comparePanel);
	JDialog compareDialog = comparePane.createDialog(requester.getComponent(), "Compare Models");
	compareDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	ZEnforcer.showModalDialogOnTop(compareDialog,requester.getComponent());
	if ("Apply Changes".equals(comparePane.getValue())) {
		if (!comparePanel.tagsResolved()) {
			JOptionPane messagePane = new JOptionPane("Please resolve all tagged elements/attributes before proceeding.");
			JDialog messageDialog = messagePane.createDialog(comparePanel, "Error");
			messageDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			ZEnforcer.showModalDialogOnTop(messageDialog,comparePanel);
		} else {
			BeanUtils.setCursorThroughout((Container)requester.getComponent(), Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try {
				processComparisonResult(comparePanel, requester);
			} catch (RuntimeException e) {
				throw e;
			} finally {
				BeanUtils.setCursorThroughout((Container)requester.getComponent(), Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		} 
	}
}

//public void prepareDocumentToLoad(VCDocument doc) throws Exception {
//	Simulation[] simulations = null;
//	if (doc instanceof MathModel) {
//		Geometry geometry = ((MathModel)doc).getMathDescription().getGeometry();
//		geometry.precomputeAll();
//		simulations = ((MathModel)doc).getSimulations();		
//	} else if (doc instanceof Geometry) {
//		((Geometry)doc).precomputeAll();		
//	} else if (doc instanceof BioModel) {
//		BioModel bioModel = (BioModel)doc;
//		SimulationContext[] simContexts = bioModel.getSimulationContexts();
//		for (SimulationContext simContext : simContexts) {
//			simContext.getGeometry().precomputeAll();
//		}
//		simulations = ((BioModel)doc).getSimulations();		
//	}
//	if (simulations != null) {	
//		// preload simulation status
//		VCSimulationIdentifier simIDs[] = new VCSimulationIdentifier[simulations.length];
//		for (int i = 0; i < simulations.length; i++){
//			simIDs[i] = simulations[i].getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
//		}
//		getDocumentManager().preloadSimulationStatus(simIDs);
//	}
//}


public static FieldDataFileOperationSpec createFDOSFromImageFile(File imageFile, boolean bCropOutBlack, Integer saveOnlyThisTimePointIndex) throws DataFormatException,ImageException{
	try{
		ImageDataset imagedataSet = ImageDatasetReader.readImageDataset(imageFile.getAbsolutePath(),null);
		if (imagedataSet!=null && bCropOutBlack){
			Rectangle nonZeroRect = imagedataSet.getNonzeroBoundingRectangle();
			if(nonZeroRect != null){
				imagedataSet = imagedataSet.crop(nonZeroRect);
			}
		}
		return createFDOSWithChannels(new ImageDataset[] {imagedataSet},saveOnlyThisTimePointIndex);
	}catch (Exception e){
		e.printStackTrace(System.out);
		throw new DataFormatException(e.getMessage());
	}
}

public static FieldDataFileOperationSpec createFDOSWithChannels(ImageDataset[] imagedataSets,Integer saveOnlyThisTimePointIndex){
	final FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();

	//[time][var][data]
	int numXY = imagedataSets[0].getISize().getX()*imagedataSets[0].getISize().getY();
	int numXYZ = imagedataSets[0].getSizeZ()*numXY;
	fdos.variableTypes = new VariableType[imagedataSets.length];
	fdos.varNames = new String[imagedataSets.length];
	short[][][] shortData =
		new short[(saveOnlyThisTimePointIndex != null?1:imagedataSets[0].getSizeT())][imagedataSets.length][numXYZ];
	for(int c=0;c<imagedataSets.length;c+= 1){
		fdos.variableTypes[c] = VariableType.VOLUME;
		fdos.varNames[c] = "Channel"+c;
		for(int t=0;t<imagedataSets[c].getSizeT();t+=1){
			if(saveOnlyThisTimePointIndex != null && saveOnlyThisTimePointIndex.intValue() != t){
				continue;
			}
			int zOffset = 0;
			for(int z=0;z<imagedataSets[c].getSizeZ();z+=1){
				UShortImage ushortImage = imagedataSets[c].getImage(z,0,t);
				System.arraycopy(ushortImage.getPixels(), 0, shortData[(saveOnlyThisTimePointIndex != null?0:t)][c], zOffset, numXY);
//				shortData[t][c] = ushortImage.getPixels();
				zOffset+= numXY;
			}
		}
	}
	fdos.shortSpecData = shortData;
	fdos.times = imagedataSets[0].getImageTimeStamps();
	if(fdos.times == null){
		fdos.times = new double[imagedataSets[0].getSizeT()];
		for(int i=0;i<fdos.times.length;i+= 1){
			fdos.times[i] = i;
		}
	}

	fdos.origin = (imagedataSets[0].getAllImages()[0].getOrigin() != null?imagedataSets[0].getAllImages()[0].getOrigin():new Origin(0,0,0));
	fdos.extent = (imagedataSets[0].getExtent()!=null)?(imagedataSets[0].getExtent()):(new Extent(1,1,1));
	fdos.isize = imagedataSets[0].getISize();
	
	return fdos;

}

public void accessPermissions(Component requester, VCDocument vcDoc) {
	VersionInfo selectedVersionInfo = null;
	switch (vcDoc.getDocumentType()) {
		case VCDocument.BIOMODEL_DOC:
			BioModelInfo[] bioModelInfos = getDocumentManager().getBioModelInfos();
			for (BioModelInfo bioModelInfo : bioModelInfos) {
				if (bioModelInfo.getVersion().getVersionKey().equals(vcDoc.getVersion().getVersionKey())) {
					selectedVersionInfo = bioModelInfo;
					break;
				}
			}
			break;
		case VCDocument.MATHMODEL_DOC:
			MathModelInfo[] mathModelInfos = getDocumentManager().getMathModelInfos();
			for (MathModelInfo mathModelInfo : mathModelInfos) {
				if (mathModelInfo.getVersion().getVersionKey().equals(vcDoc.getVersion().getVersionKey())) {
					selectedVersionInfo = mathModelInfo;
					break;
				}
			}
			break;
		case VCDocument.GEOMETRY_DOC:
			GeometryInfo[] geoInfos = getDocumentManager().getGeometryInfos();
			for (GeometryInfo geoInfo : geoInfos) {
				if (geoInfo.getVersion().getVersionKey().equals(vcDoc.getVersion().getVersionKey())) {
					selectedVersionInfo = geoInfo;
					break;
				}
			}
			break;
	}
	getMdiManager().getDatabaseWindowManager().accessPermissions(requester, selectedVersionInfo);
	
}
}
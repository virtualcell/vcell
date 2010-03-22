package cbit.vcell.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.DataBufferByte;
import java.awt.image.FilteredImageSource;
import java.awt.image.IndexColorModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.Vector;
import java.util.zip.DataFormatException;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.UndoableEditSupport;

import org.jdom.Element;
import org.vcell.util.BeanUtils;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.Hex;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
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
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.VersionableType;
import org.vcell.util.document.VersionableTypeVersion;
import org.vcell.util.gui.AsynchGuiUpdater;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.FileFilters;
import org.vcell.util.gui.UtilCancelException;
import org.vcell.util.gui.VCFileChooser;
import org.vcell.util.gui.ZEnforcer;

import com.ctc.wstx.io.MergedReader;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.image.VCPixelClass;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.rmi.event.VCellMessageEvent;
import cbit.rmi.event.VCellMessageEventListener;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReader;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.FieldDataWindowManager.SimInfoHolder;
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
import cbit.vcell.client.task.EditImageAttributes;
import cbit.vcell.client.task.ExportToXML;
import cbit.vcell.client.task.FinishExport;
import cbit.vcell.client.task.FinishSave;
import cbit.vcell.client.task.NewName;
import cbit.vcell.client.task.RunSims;
import cbit.vcell.client.task.SaveDocument;
import cbit.vcell.client.task.SetMathDescription;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.LoginDialog;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.RegionImage.RegionInfo;
import cbit.vcell.geometry.gui.GeometrySummaryPanel;
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.geometry.gui.OverlayImageDisplayJAI;
import cbit.vcell.geometry.gui.ROISourceData;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.parser.Expression;
import cbit.vcell.server.UserRegistrationOP;
import cbit.vcell.simdata.MergedDataInfo;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.VariableType;
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


private void changeGeometry0(final DocumentWindowManager requester, final SimulationContext simContext,final VCMLEditorPanel vcmlEditorPanel) {
	AsynchClientTask selectDocumentTypeTask = new AsynchClientTask("Select/Load geometry", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			int[] geomType = null;
			geomType = DialogUtils.showComponentOKCancelTableList(
				JOptionPane.getRootFrame(), 
				"Choose new geometry type",
				new String[] {"Geometry Type"}, 
				new String[][] {{"Select from BioModels"},{"Select from MathModels"},{"Select from existing Geometries"}}, ListSelectionModel.SINGLE_SELECTION);
			VCDocumentInfo vcDocumentInfo = selectDocumentFromType(geomType[0]);
			hashTable.put("vcDocumentInfo", vcDocumentInfo);
		}		
	};

	AsynchClientTask selectLoadGeomTask = new AsynchClientTask("Select/Load geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			VCDocumentInfo vcDocumentInfo = (VCDocumentInfo)hashTable.get("vcDocumentInfo");
			Geometry geom = getGeometryFromDocumentSelection(vcDocumentInfo, false);
			geom.getGeometrySpec().getSampledImage();//pregenerate sampled image, cpu intensive
			hashTable.put("geometry", geom);
			GeometryInfo geometryInfo = getDocumentManager().getGeometryInfo(geom.getVersion().getVersionKey());
			hashTable.put("geometryInfo", geometryInfo);
		}		
	};

	AsynchClientTask confirmGeomTask = new AsynchClientTask("Changing geometry", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(final Hashtable<String, Object> hashTable) throws Exception {
			Geometry geom = (Geometry)hashTable.get("geometry");
			GeometrySummaryPanel geometrySummaryPanel = new GeometrySummaryPanel();
			geometrySummaryPanel.setGeometry(geom);
			int result = DialogUtils.showComponentOKCancelDialog(JOptionPane.getRootFrame(), geometrySummaryPanel, "Confirm Geometry Selection");
			if(result != JOptionPane.OK_OPTION){
				return;
			}
			
			//Change geometry confirmed, branch off and start changeGeometryAfterChecking (starts new ClientTaskDispatcher)
			new Thread(new Runnable() {
				public void run() {
					changeGeometryAfterChecking((GeometryInfo)hashTable.get("geometryInfo"), requester, simContext,vcmlEditorPanel);
				}
			}).start();
		}		
	};

	Hashtable<String, Object> hashTable = new Hashtable<String, Object>();
	ClientTaskDispatcher.dispatch(requester.getComponent(),hashTable,
			new AsynchClientTask[] {selectDocumentTypeTask, selectLoadGeomTask, confirmGeomTask}, false);
	
}

public void changeGeometry(DocumentWindowManager requester, SimulationContext simContext) {
	changeGeometry0(requester, simContext,null);
}

public void changeGeometry(DocumentWindowManager requester,VCMLEditorPanel vcmlEditorPanel) {
	changeGeometry0(requester, null,vcmlEditorPanel);
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 4:20:47 AM)
 * @param windowManager cbit.vcell.client.desktop.DocumentWindowManager
 */
private void changeGeometryAfterChecking(final GeometryInfo geoInfo, final DocumentWindowManager requester, final SimulationContext simContext,final VCMLEditorPanel vcmlEditorPanel) {
	/* asynchronous and not blocking any window */
	AsynchClientTask task1 = new AsynchClientTask("Blocking window", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			getMdiManager().blockWindow(requester.getManagerID());
		}		
	};
	
	AsynchClientTask task2 = new AsynchClientTask("Getting new geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			Geometry newGeometry = getDocumentManager().getGeometry(geoInfo);
			
			if(requester instanceof MathModelWindowManager){
				continueAfterMathModelGeomChangeWarning((MathModelWindowManager)requester, newGeometry);
			}

			newGeometry.getGeometrySpec().getSampledImage();
			if (newGeometry.getDimension()>0 && newGeometry.getGeometrySurfaceDescription().getGeometricRegions()==null){
				newGeometry.getGeometrySurfaceDescription().updateAll();				
			}			
			hashTable.put("geometry", newGeometry);
		}		
	};
	
	AsynchClientTask task3 = new AsynchClientTask("Changing geometry", AsynchClientTask.TASKTYPE_SWING_BLOCKING/*, false, false*/) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
//			try {
				Geometry geometry = (Geometry)hashTable.get("geometry");
				if (geometry != null) {
					if (requester instanceof BioModelWindowManager) {
						simContext.setGeometry(geometry);
					} else if (requester instanceof MathModelWindowManager) {
						MathModel mathModel = (MathModel)((MathModelWindowManager)requester).getVCDocument();
						mathModel.getMathDescription().setGeometry(geometry);
						if(vcmlEditorPanel != null){
							vcmlEditorPanel.updateWarningText(mathModel.getMathDescription());
						}
					}
				}
//			} finally {
//				getMdiManager().unBlockWindow(requester.getManagerID());
//			}
		}
	};
	
	AsynchClientTask task4 = new AsynchClientTask("Unblocking window", AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, false) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
				getMdiManager().unBlockWindow(requester.getManagerID());
		}
	};

	
	ClientTaskDispatcher.dispatch(requester.getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2, task3, task4}, false);

}

public static void continueAfterMathModelGeomChangeWarning(MathModelWindowManager mathModelWindowManager,Geometry newGeometry) throws UserCancelException{

	MathModel mathModel = mathModelWindowManager.getMathModel();
	if(mathModel != null && mathModel.getMathDescription() != null){
		Geometry oldGeometry = mathModel.getMathDescription().getGeometry();
		boolean bHasSims = (mathModel.getSimulations() != null) && (mathModel.getSimulations().length > 0);
		boolean bMeshResolutionChange = true;
		if(oldGeometry == null){
			bMeshResolutionChange = false;
		}
		if(newGeometry != null && oldGeometry.getDimension() == newGeometry.getDimension()){
			bMeshResolutionChange = false;
		}
		String result = DialogUtils.showWarningDialog(JOptionPane.getRootFrame(),
				"After changing MathModel geometry please note:\n"+
				"  1.  Check Geometry subvolume names match MathModel compartment names."+
				(bHasSims && bMeshResolutionChange?"\n  2.  All existing simulations mesh resolutions will be reset.":""),
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
	}
	else {
		boolean closedAllWindows = closeAllWindows(false);
		if (! closedAllWindows) {
			// user bailed out on closing some window
			return;
		} else {
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
			AsynchClientTask[] taskArray = new AsynchClientTask[newTasks.length + 2];
			System.arraycopy(newTasks, 0, taskArray, 0, newTasks.length);
			taskArray[newTasks.length] = task1;
			taskArray[newTasks.length + 1] = task2;
			
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
	bioModel.getModel().addFeature("Unnamed compartment", null, "Unnamed membrane");
	return bioModel;
}

private MathModel createDefaultMathModelDocument() throws Exception {
	Geometry geometry = new Geometry("Untitled", 0);
	MathModel mathModel = createMathModel("Untitled", geometry);
	mathModel.setName("MathModel" + (getMdiManager().getNewlyCreatedDesktops() + 1));
	return mathModel;
}

public VCDocumentInfo selectDocumentFromType(int documentType) throws Exception,UserCancelException{
	return
		getMdiManager().getDatabaseWindowManager().selectDocument(documentType, getMdiManager().getFocusedWindowManager());
}
public Geometry getGeometryFromDocumentSelection(VCDocumentInfo vcDocumentInfo,boolean bClearVersion) throws Exception,UserCancelException{
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
				int[] selection = DialogUtils.showComponentOKCancelTableList(JOptionPane.getRootFrame(),
						"Select Geometry", columnNames, rowData, ListSelectionModel.SINGLE_SELECTION);
				BioModel bioModel = getDocumentManager().getBioModel((BioModelInfo)vcDocumentInfo);
				for (int i = 0; i < bioModel.getSimulationContexts().length; i++) {
					if(bioModel.getSimulationContexts()[i].getName().equals(rowData[selection[0]][0])){
						geom = bioModel.getSimulationContexts()[i].getGeometry();
					}
				}
			}else{
				throw new Exception("BioModel '"+bioModelInfo.getVersion().getName()+"' contains no spatial geometries.");
//				DialogUtils.showErrorDialog(JOptionPane.getRootFrame(), "BioModel '"+bioModelInfo.getVersion().getName()+"' contains no spatial geometries.");
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
//				DialogUtils.showErrorDialog(JOptionPane.getRootFrame(), "MathModel '"+mathModelInfo.getVersion().getName()+"' contains no spatial geometry.");				
			}
		}else{
			throw new Exception("MathModel '"+mathModelInfo.getVersion().getName()+"' contains no spatial geometry.");
		}
	}else if(vcDocumentInfo.getVersionType().equals(VersionableType.Geometry)/*documentType == VCDocument.GEOMETRY_DOC*/){
		geom = getDocumentManager().getGeometry((GeometryInfo)vcDocumentInfo);
		if(geom.getDimension() == 0){
			throw new Exception("Error, Only spatial geometries allowed (dimesnion > 0).");
//			geom = null;
//			DialogUtils.showErrorDialog(JOptionPane.getRootFrame(), "Geometry '"+geom.getName()+"' is not a spatial geometry.");							
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
					BioModel bioModel = new BioModel(null);
					bioModel.setName("BioModel" + (getMdiManager().getNewlyCreatedDesktops() + 1));
					bioModel.getModel().addFeature("Unnamed compartment", null, "Unnamed membrane");
					hashTable.put("doc", bioModel);
				}			
			};
			taskArray = new AsynchClientTask[] {task1};
			break;
		}
		case VCDocument.MATHMODEL_DOC: {
			if ((createOption == VCDocument.MATH_OPTION_NONSPATIAL) || (createOption == VCDocument.MATH_OPTION_SPATIAL)) {
				AsynchClientTask task1 = new AsynchClientTask("asking for geometry", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {		
						// spatial or non-spatial
						if (createOption == VCDocument.MATH_OPTION_SPATIAL) {
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
			} else if (createOption == VCDocument.MATH_OPTION_FROMBIOMODELAPP){
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
						hashTable.put("doc", geometry);
					}
				};
				taskArray = new AsynchClientTask[] {task1};
				break;
			} else  {
				// image-based
				final AsynchClientTask saveImage = new AsynchClientTask("saving image", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						RequestManager theRequestManager = (RequestManager)hashTable.get("requestManager");
						VCImage image = (VCImage)hashTable.get("vcImage");
						String newName = (String)hashTable.get("newName");
						
						VCImage editedImage = theRequestManager.getDocumentManager().saveAsNew(image,newName);					
				
						getClientTaskStatusSupport().setMessage("finished saving " + newName);
						//
						// check that save actually occured (it should have since an insert should be new)
						//
						Version newVersion = editedImage.getVersion();
						Version oldVersion = image.getVersion();
						if ((oldVersion != null) && newVersion.getVersionKey().compareEqual(oldVersion.getVersionKey())){
							throw new DataAccessException("Save New Image failed, Old version has same id as New");
						}
						hashTable.put("image", editedImage);
					}
				};	
				
				final AsynchClientTask createGeometry =  new AsynchClientTask("creating geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						VCImage image = (VCImage)hashTable.get("image");
						Geometry newGeom = new Geometry("Untitled", image);
						newGeom.setDescription(image.getDescription());
						hashTable.put("doc", newGeom);
					}
				};
				if (documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_DBIMAGE) {
					// Get image from database
					AsynchClientTask task1 = new AsynchClientTask("select from database", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
						@Override
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							Object option = getMdiManager().getDatabaseWindowManager().showImageSelectorDialog(requester);
							hashTable.put("selectOption", option);
						}
					};
					AsynchClientTask task2 = new AsynchClientTask("creating geometry from database image", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
						@Override
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							Object choice = hashTable.get("selectOption");
							if (choice != null && choice.equals("OK")) {
								VCImage image = getMdiManager().getDatabaseWindowManager().selectImageFromDatabase();
								if (image == null){
									throw new RuntimeException("failed to create new Geometry, no image");
								}
								hashTable.put("image", image);
							} else {
								throw UserCancelException.CANCEL_DB_SELECTION;
							}
						}
					};
					taskArray = new AsynchClientTask[] {task1, task2, createGeometry};
					break;
				} else if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FILE) {
					// Get image from file --- INCOMPLETE
					AsynchClientTask selectImageFileTask = new AsynchClientTask("select image file", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
						@Override
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							File imageFile = DatabaseWindowManager.showFileChooserDialog(requester, FileFilters.FILE_FILTER_FIELDIMAGES, getUserPreferences());
							hashTable.put("imageFile", imageFile);
						}
					};
					AsynchClientTask parseImageFileTask = new AsynchClientTask("read and parse image file", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
						@Override
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							File imageFile = (File)hashTable.get("imageFile");
							try {
								FieldDataFileOperationSpec fdfos = ClientRequestManager.createFDOSFromImageFile(imageFile,false);
								hashTable.put("fdfos", fdfos);
								
								
								
								Component guiParent =(Component)hashTable.get("guiParent");
//								FieldDataFileOperationSpec fdfos = (FieldDataFileOperationSpec)hashTable.get("fdfos");
								short[] dataToSegment = fdfos.shortSpecData[0][0];//[time 0][channel 0]
								hashTable.put("dataToSegment",dataToSegment);

								//find unique pixel values
//								BitSet uniquePixelBS = new BitSet((int)Math.pow(2, Short.SIZE));
//								for (int i = 0; i < dataToSegment.length; i++) {
//									uniquePixelBS.set((int)(dataToSegment[i]&0x0000FFFF));
//								}
								BitSet uniquePixelBS = getUniquePixelValuesFromUnsignedShorts(dataToSegment);
								//ask user if want to manual segment
								if (askAboutSegmentation(guiParent, uniquePixelBS.cardinality()).equals(SEGMENT_KEEP_IMPORTED)) {
//									//auto segment
//									int minVal = dataToSegment[0]&0x0000FFFF;
//									int maxVal = minVal;
//									for (int i = 0; i < dataToSegment.length; i++) {
//										int usIntVal = (int)(dataToSegment[i]&0x0000FFFF);
//										minVal = Math.min(usIntVal, minVal);
//										maxVal = Math.max(usIntVal, maxVal);
//									}
//									byte[] byteData = new byte[dataToSegment.length];
//						
//									if(maxVal >= MAX_NUMBER_OF_COLORS_IMPORTED_FILE){
//										if(uniquePixelBS.cardinality() <= MAX_NUMBER_OF_COLORS_IMPORTED_FILE){
//											int index = 0;
//											int[] indexRef = new int[(int)Math.pow(2, Short.SIZE)];
//											Arrays.fill(indexRef, -1);
//											for (int i = 0; i < indexRef.length; i++) {
//												if(uniquePixelBS.get(i)){
//													indexRef[i] = index;
//													index++;
//												}
//											}
//											for (int i = 0; i < dataToSegment.length; i++) {
//												byteData[i] = (byte)indexRef[(int)(dataToSegment[i]&0x0000FFFF)];
//											}
//										}else{
//											for (int i = 0; i < dataToSegment.length; i++) {
//												byteData[i] =
//													(byte)(0xFF & (int)((double)(dataToSegment[i]-minVal)/(double)(maxVal-minVal)*(MAX_NUMBER_OF_COLORS_IMPORTED_FILE-1)));
//											}
//										}
//									}else{
//										for (int i = 0; i < byteData.length; i++) {
//											byteData[i] = (byte)(dataToSegment[i]&0xFF);
//										}
//									}
//									VCImage autoSegmentVCImage =
//										new VCImageUncompressed(null,byteData,fdfos.extent,fdfos.isize.getX(),fdfos.isize.getY(),fdfos.isize.getZ());
									hashTable.put("vcImage", createVCImageFromUnsignedShorts(dataToSegment, fdfos.extent, fdfos.isize, uniquePixelBS));
									hashTable.put("bManualSegment", new Boolean(false));
									Rectangle unCroppedRect = new Rectangle(0,0,fdfos.isize.getX(),fdfos.isize.getY());
									hashTable.put("previousCrop", unCroppedRect);
								}else{
									hashTable.put("bManualSegment", new Boolean(true));
								}

								
								
							} catch (DataFormatException ex) {
								throw new Exception("Cannot read image file '" + imageFile.getAbsolutePath()+"'\n"+ex.getMessage());
							}
						}
					};

					final AsynchClientTask taskSegment = new AsynchClientTask("Segment image", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
						@Override
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							if(!((Boolean)hashTable.get("bManualSegment")).booleanValue()){
								return;
							}
							short[] dataToSegment = (short[])hashTable.get("dataToSegment");
							VCImage previouslyEditedVCImage = (VCImage)hashTable.remove("vcImage");//can be null
							Rectangle previousCropRectangle = (Rectangle)hashTable.remove("previousCrop");//can be null

							Component guiParent =(Component)hashTable.get("guiParent");
							FieldDataFileOperationSpec fdfos = (FieldDataFileOperationSpec)hashTable.get("fdfos");
							
							//manual segment
							Hashtable<VCImage, Rectangle> segmentedAndCropped =
								ClientRequestManager.segmentRawImage(
										guiParent,fdfos.origin,fdfos.extent, fdfos.isize,
										dataToSegment,previouslyEditedVCImage,previousCropRectangle);
							hashTable.put("vcImage", segmentedAndCropped.keySet().toArray(new VCImage[0])[0]);
							hashTable.put("previousCrop", segmentedAndCropped.values().toArray(new Rectangle[0])[0]);
						}
					};

					final AsynchClientTask backToSegmentTask = new AsynchClientTask("return to segmentation", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING,true,false) {
						@Override
						public void run(final Hashtable<String, Object> hashTable) throws Exception {
							Exception e = (Exception)hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_USER);
							if(e != null && e == UserCancelException.CANCEL_EDIT_IMG_ATTR){
								final AsynchClientTask[] backToSegmentTasks =
									new AsynchClientTask[] {taskSegment,new EditImageAttributes(), saveImage, createGeometry, this};
								new Thread(new Runnable() {
									public void run() {
										// TODO Auto-generated method stub
										Hashtable<String, Object> newHash = new Hashtable<String, Object>();
										newHash.putAll(hashTable);
										newHash.remove(ClientTaskDispatcher.TASK_ABORTED_BY_USER);
										final String backToSegment = "Show segmentation tool";
										final String quit = "Quit geometry import";
										String result = DialogUtils.showWarningDialog((Component)newHash.get("guiParent"),										
												"Do you want to show the segmentation tool or just quit?",
												new String[] {backToSegment,quit}, quit);
										if(result != null && result.equals(backToSegment)){
											newHash.put("bManualSegment", new Boolean(true));
											ClientTaskDispatcher.dispatch((Component)newHash.get("guiParent"), newHash, backToSegmentTasks);
										}
									}
								}).start();
							}
						}
					};
					
					taskArray = new AsynchClientTask[] {selectImageFileTask,parseImageFileTask, taskSegment,new EditImageAttributes(), saveImage, createGeometry,backToSegmentTask};
					break;
				} else if (documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FIELDDATA){
					AsynchClientTask task1 = new AsynchClientTask("retrieving data", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

						@Override
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							VCDocument.GeomFromFieldDataCreationInfo docInfo = (VCDocument.GeomFromFieldDataCreationInfo)documentCreationInfo;
							PDEDataContext pdeDataContext =	getMdiManager().getFieldDataWindowManager().getPDEDataContext(docInfo.getExternalDataID());
							pdeDataContext.setVariableAndTime(docInfo.getVarName(), pdeDataContext.getTimePoints()[docInfo.getTimeIndex()]);
							double[] data = pdeDataContext.getDataValues();
							CartesianMesh mesh = pdeDataContext.getCartesianMesh();
							hashTable.put("mesh", mesh);
							byte[] segmentedData = new byte[data.length];
							Vector<Double> distinctValues = new Vector<Double>();
							int index = -1;
							boolean bTooManyColors = false;
							for (int i = 0; i < data.length; i++) {
								if((index = distinctValues.indexOf(data[i])) == -1){
									index = distinctValues.size();
									distinctValues.add(data[i]);
									if(distinctValues.size() > MAX_NUMBER_OF_COLORS_IMPORTED_FILE){
										bTooManyColors = true;
										break;
									}
								}
								segmentedData[i] = (byte)index;
							}
							
							if (!bTooManyColors) {
								VCImage initImage = new VCImageUncompressed(null,segmentedData, mesh.getExtent(), mesh.getSizeX(), mesh.getSizeY(), mesh.getSizeZ());
								hashTable.put("vcImage", initImage);
							}else{
								double minValue = Double.POSITIVE_INFINITY;
								double maxValue = Double.NEGATIVE_INFINITY;
								for (int i = 0; i < data.length; i++) {
									minValue = Math.min(minValue,data[i]);
									maxValue = Math.max(maxValue,data[i]);
								}
								short[] dataToSegment = new short[data.length];
								double scaleShort = Math.pow(2, Short.SIZE)-1;
								for (int i = 0; i < data.length; i++) {
									dataToSegment[i] = (short)((data[i]-minValue)/(maxValue-minValue)*scaleShort);
								}
								hashTable.put("dataToSegment", dataToSegment);
							}
						}
					};
					
					AsynchClientTask segmentTask = new AsynchClientTask("segmenting data", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

						@Override
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							CartesianMesh mesh = (CartesianMesh)hashTable.get("mesh");
							if (mesh==null){
								throw new Exception("mesh not found");
							}

							Component parent = (Component)hashTable.get("parent");
							VCImage vcImage = (VCImage)hashTable.get("vcImage");
							if (vcImage != null) {	
								if (askAboutSegmentation(parent, vcImage.getPixelClasses().length).equals(SEGMENT_KEEP_IMPORTED)) {
									return;
								}
							} else {
								PopupGenerator.showInfoDialog(parent, "The image contains more than "+MAX_NUMBER_OF_COLORS_IMPORTED_FILE+" colors. User will define regions manually.");
							}
							short[] dataToSegment = (short[])hashTable.get("dataToSegment");
							hashTable.put("vcImage", ClientRequestManager.segmentRawImage(parent,mesh.getOrigin(),mesh.getExtent(),
									new ISize(mesh.getSizeX(),mesh.getSizeY(),mesh.getSizeZ()),dataToSegment,
									null,null).keySet().toArray(new VCImage[0])[0]);
						}
					};
					
					taskArray = new AsynchClientTask[] {task1, segmentTask, new EditImageAttributes(), saveImage, createGeometry};
					break;
				} else{
					throw new RuntimeException("Unknown Geometry Document creation option value="+documentCreationInfo.getOption());
				}
			}
		}
		default: {
			throw new RuntimeException("Unknown default document type: " + documentCreationInfo.getDocumentType());
		}
	}
	return taskArray;
}

private static final int MAX_NUMBER_OF_COLORS_IMPORTED_FILE = 256;
private final String SEGMENT_USER_WILL_EDIT = "User will edit segmentation";
private final String SEGMENT_KEEP_IMPORTED = "Keep imported segmentation";
private final String SEGMENT_CANCEL = "Cancel";

private String askAboutSegmentation(Component parent,int numPixelClasses) throws UserCancelException{
	String choice = PopupGenerator.showWarningDialog(parent,
			"The imported image contains "+numPixelClasses+" colors.\nIf '"+SEGMENT_KEEP_IMPORTED+"' is selected each color will be a different subdomain in the geometry."+
			"\nChoose '"+SEGMENT_USER_WILL_EDIT+"' to manually define/edit geometry subdomains.", 
			new String[] {SEGMENT_USER_WILL_EDIT, SEGMENT_KEEP_IMPORTED, SEGMENT_CANCEL}, SEGMENT_USER_WILL_EDIT);
	if(choice.equals(SEGMENT_CANCEL)){
		throw UserCancelException.CANCEL_GENERIC;
	}
	return choice;

}

public static int[] calculateEdgeIndexes(int xSize,int ySize,int zSize){
	if((xSize!=1 && xSize<3) || (ySize!=1 && ySize<3) ||(zSize!=1 && zSize<3)){
		throw new IllegalArgumentException("Sizes CANNOT be negative or 0 or 2");
	}
	int XYSIZE = xSize*ySize;
	int numEdgeIndexes = xSize*ySize*zSize - ((xSize==1?1:xSize-2)*(ySize==1?1:ySize-2)*(zSize == 1?1:zSize-2));
	if(numEdgeIndexes == 0){
		return new int[0];
	}
	int[] edgeIndexes = new int[numEdgeIndexes];
	int index = 0;
	for (int z = 0; z < zSize; z++) {
		boolean bZEdge = (z==0) || (z==(zSize-1));
		bZEdge = bZEdge && zSize!=1;
		for (int y = 0; y < ySize; y++) {
			boolean bYEdge = (y==0) || (y==ySize-1);
			bYEdge = bYEdge && ySize!=1;
			int xIncr = (bYEdge||bZEdge?1:xSize-1);
			for (int x = 0; x < xSize; x+= xIncr) {
				int edgeIndex = x+(y*xSize)+(z*XYSIZE);
				edgeIndexes[index] = edgeIndex;
				index++;
			}
		}
	}
	if(index != numEdgeIndexes){
		throw new RuntimeException("final count not match calulated");
	}
	return edgeIndexes;
}

public static BitSet getUniquePixelValuesFromUnsignedShorts(short[] dataToSegment){
	BitSet uniquePixelBS = new BitSet((int)Math.pow(2, Short.SIZE));
	for (int i = 0; i < dataToSegment.length; i++) {
		uniquePixelBS.set((int)(dataToSegment[i]&0x0000FFFF));
	}
	return uniquePixelBS;
}
public static VCImage createVCImageFromBufferedImages(Extent extent,BufferedImage[] bufferedImages) throws Exception{
	//collect z-sections into 1 array for VCImage
	ISize isize = new ISize(bufferedImages[0].getWidth(), bufferedImages[0].getHeight(), bufferedImages.length);
	int sizeXY = isize.getX()*isize.getY();
	byte[] segmentedData = new byte[isize.getXYZ()];
	int index = 0;
	for (int i = 0; i < bufferedImages.length; i++) {
		System.arraycopy(
				((DataBufferByte)bufferedImages[i].getRaster().getDataBuffer()).getData(),0,
				segmentedData, index,
				sizeXY);
		index+= sizeXY;
	}
	
	return new VCImageUncompressed(null,segmentedData, extent,isize.getX(),isize.getY(),isize.getZ());

}
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
public static Hashtable<VCImage, Rectangle> segmentRawImage(Component guiParent,
		final Origin origin,final Extent extent,final ISize isize,final short[] dataToSegment,VCImage previouslyEditedVCImage,Rectangle previouslyCropRectangle) throws Exception{
	
	if((previouslyCropRectangle == null && previouslyEditedVCImage != null) ||
			(previouslyCropRectangle != null && previouslyEditedVCImage == null)){
		throw new IllegalArgumentException("Previous VCImage and Crop must both be null or both be not null.");
	}
	final OverlayEditorPanelJAI overlayEditorPanelJAI = new OverlayEditorPanelJAI();
	overlayEditorPanelJAI.setModeRemoveROIWhenPainting(true);
	overlayEditorPanelJAI.setUndoableEditSupport(new UndoableEditSupport());
	overlayEditorPanelJAI.setROITimePlotVisible(false);
	UShortImage[] zImageSet = new UShortImage[isize.getZ()];
	Extent newExtent = new Extent(extent.getX(),extent.getY(),extent.getZ()/isize.getZ());
	if(previouslyCropRectangle != null){
		new Extent(previouslyCropRectangle.width*(extent.getX()/isize.getX()),
				previouslyCropRectangle.height*(extent.getY()/isize.getY()),
				extent.getZ()/isize.getZ());
	}
	for (int i = 0; i < zImageSet.length; i++) {
		Origin newOrigin = new Origin(origin.getX(),origin.getY(),origin.getZ()+i*newExtent.getZ());
		short[] shortData = new short[isize.getX()*isize.getY()];
		System.arraycopy(dataToSegment, shortData.length*i, shortData, 0, shortData.length);
		zImageSet[i] = new UShortImage(shortData,newOrigin,newExtent,isize.getX(),isize.getY(),1);
	}
	    
	final ImageDataset[] imageDatasetHolder = new ImageDataset[] { new ImageDataset(zImageSet, new double[] { 0.0 }, isize.getZ())};
	overlayEditorPanelJAI.setImages(imageDatasetHolder[0], true, OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR, OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR);
	overlayEditorPanelJAI.setROITimePlotVisible(false);

	//------------------------Create ROI composite image array
		int[] cmap = new int[256];
		for(int i=0;i<256;i+= 1){
			cmap[i] = OverlayEditorPanelJAI.CONTRAST_COLORS[i].getRGB();
			if(i==0){
				cmap[i] = new Color(0, 0, 0, 0).getRGB();
			}
		}
		final IndexColorModel indexColorModel =
			new java.awt.image.IndexColorModel(
				8, cmap.length,cmap,0,
				false /*false means NOT USE alpha*/   ,
				-1/*NO transparent single pixel*/,
				java.awt.image.DataBuffer.TYPE_BYTE);
		final BufferedImage[] roiComposite = new BufferedImage[imageDatasetHolder[0].getISize().getZ()];
		for (int i = 0; i < roiComposite.length; i++) {
			roiComposite[i] = 
				new BufferedImage(imageDatasetHolder[0].getISize().getX(), imageDatasetHolder[0].getISize().getY(),
						BufferedImage.TYPE_BYTE_INDEXED, indexColorModel);
		}
		overlayEditorPanelJAI.setAllROICompositeImage(roiComposite);
	//------------------------
	
	final Rectangle mergedCropRectangle = new Rectangle();
//	if(previouslyCropRectangle != null){
//		mergedCropRectangle.setBounds(previouslyCropRectangle);
//	}else{
//		mergedCropRectangle.setBounds(0, 0, isize.getX(), isize.getY());
//	}
	final String RESERVED_NAME_BACKGROUND = "background";
	final PropertyChangeListener[] roiPropertyChangeListenerHolder = new PropertyChangeListener[1];
	roiPropertyChangeListenerHolder[0] =
			new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CROP_PROPERTY)){
						try{
							Rectangle cropRectangle = (Rectangle)evt.getNewValue();
							//crop underlying image
							imageDatasetHolder[0] = imageDatasetHolder[0].crop(cropRectangle);
							//Crop ROI zsections
							for (int i = 0; i < roiComposite.length; i++) {
								Image croppedROI = 
									Toolkit.getDefaultToolkit().createImage(
										new FilteredImageSource(roiComposite[i].getSource(),
											new CropImageFilter(cropRectangle.x, cropRectangle.y, cropRectangle.width, cropRectangle.height))
									);
								roiComposite[i] =
									new BufferedImage(cropRectangle.width, cropRectangle.height,
											BufferedImage.TYPE_BYTE_INDEXED, indexColorModel);
								roiComposite[i].getGraphics().drawImage(croppedROI, 0, 0, null);
							}
							//Update display with cropped images
							overlayEditorPanelJAI.setAllROICompositeImage(roiComposite);
							overlayEditorPanelJAI.setImages(imageDatasetHolder[0], true,
									OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR, OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR);
							mergedCropRectangle.setBounds(
									mergedCropRectangle.x+cropRectangle.x,
									mergedCropRectangle.y+cropRectangle.y,
									cropRectangle.width,cropRectangle.height);
						}catch(Exception e){
							DialogUtils.showErrorDialog(overlayEditorPanelJAI, "Crop failed:\n"+e.getMessage());
						}
					}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CURRENTROI_PROPERTY)){
						
					}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_DELETEROI_PROPERTY)){
						final String deleteCurrentROI = "Delete only current ROI";
						final String deleteAllROI = "Delete all ROIs";
						final String cancel = "Cancel";
						String result =
							DialogUtils.showWarningDialog(overlayEditorPanelJAI, "Choose delete option.",
									new String[] {deleteCurrentROI,deleteAllROI,cancel}, deleteCurrentROI);
						
						if(result.equals(deleteCurrentROI)){
							PropertyChangeEvent propertyChangeEvent =
								new PropertyChangeEvent(this,
										OverlayEditorPanelJAI.FRAP_DATA_CLEARROI_PROPERTY, evt.getOldValue(), evt.getNewValue());
							roiPropertyChangeListenerHolder[0].propertyChange(propertyChangeEvent);
							
							overlayEditorPanelJAI.deleteROIName((OverlayEditorPanelJAI.ComboboxROIName)evt.getOldValue());

						}else if(result.equals(deleteAllROI)){
							for (int i = 0; i < roiComposite.length; i++) {
								Arrays.fill(((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData(), (byte)0);
							}
							overlayEditorPanelJAI.deleteROIName(null);
							overlayEditorPanelJAI.setROI(null);//force update
						}
					}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_ADDNEWROI_PROPERTY)){
						try{
							String newROIName = null;
							OverlayEditorPanelJAI.ComboboxROIName[] comboboxROINameArr =
								(OverlayEditorPanelJAI.ComboboxROIName[])evt.getOldValue();
							boolean bNameOK;
							do{
								bNameOK = true;
								if(newROIName == null){
									newROIName = PopupGenerator.showInputDialog0((Component)evt.getSource(), "New ROI Name", "");
								}
								if(newROIName == null || newROIName.length() == 0){
									bNameOK = false;
									PopupGenerator.showErrorDialog((Component)evt.getSource(), "No ROI Name entered, try again.");
								}else{
									if(newROIName.equals(RESERVED_NAME_BACKGROUND)){
										DialogUtils.showWarningDialog(overlayEditorPanelJAI,
												"Cannot use the name '"+RESERVED_NAME_BACKGROUND+"'.  That name is reserved by the system to refer to unassigned pixels");
										newROIName = null;
										continue;
									}
									for (int i = 0; i < comboboxROINameArr.length; i++) {
										if(comboboxROINameArr[i].getROIName().equals(newROIName)){
											bNameOK = false;
											break;
									}
								}
								if(bNameOK){
//										JColorChooser jColorChooser = new JColorChooser();
//										DialogUtils.showComponentOKCancelDialog(JOptionPane.getRootFrame(), jColorChooser, "Select ROI Color");
									Color newROIColor = Color.black;
									for (int i = 1; i < OverlayEditorPanelJAI.CONTRAST_COLORS.length; i++) {
										boolean bColorUsed = false;
										for (int j = 0; j < comboboxROINameArr.length; j++) {
											Color nextColor = comboboxROINameArr[j].getHighlightColor();
											if(nextColor.equals(OverlayEditorPanelJAI.CONTRAST_COLORS[i])){
												bColorUsed = true;
												break;
											}
										}
										if(!bColorUsed){
											newROIColor = OverlayEditorPanelJAI.CONTRAST_COLORS[i];
											break;
										}
									}

									overlayEditorPanelJAI.addROIName(newROIName, true, newROIName,true,/*true,true,*/newROIColor);
								}else{
									PopupGenerator.showErrorDialog((Component)evt.getSource(), "ROI Name "+newROIName+" already used, try again.");
									newROIName = null;
								}
									
								}
							}while(!bNameOK);
						}catch(UtilCancelException cancelExc){
							//do Nothing
						}

					}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_SHOWROIASSIST_PROPERTY)){
						if(imageDatasetHolder[0] == null){
							DialogUtils.showErrorDialog(JOptionPane.getRootFrame(), "No ImageData available for ROIAssistant.");
							return;
						}
						try {
							OverlayEditorPanelJAI.ComboboxROIName comboboxROIName = (OverlayEditorPanelJAI.ComboboxROIName)evt.getNewValue();
							Color roiColor = comboboxROIName.getHighlightColor();
							int roiColorIndex = -1;
							for (int i = 0; i < OverlayEditorPanelJAI.CONTRAST_COLORS.length; i++) {
								if(OverlayEditorPanelJAI.CONTRAST_COLORS[i].equals(roiColor)){
									roiColorIndex = i;
									break;
								}
							}

							//Create empty ROI
							UShortImage[] roiZ = new UShortImage[imageDatasetHolder[0].getSizeZ()];
							for (int i = 0; i < roiZ.length; i++) {
								short[] pixels = new short[imageDatasetHolder[0].getISize().getX()*imageDatasetHolder[0].getISize().getY()];
								Origin origin = imageDatasetHolder[0].getImage(i, 0, 0).getOrigin();
								Extent extent = imageDatasetHolder[0].getImage(i, 0, 0).getExtent();
								roiZ[i] = new UShortImage(pixels,origin,extent,
										imageDatasetHolder[0].getISize().getX(),imageDatasetHolder[0].getISize().getY(),1);
							}
							ROI originalROI = new ROI(roiZ,"Assist ROI");
							ROI finalROI = overlayEditorPanelJAI.showAssistDialog(originalROI,null, false/*, false, false*/);
							
							if(finalROI != null){
								boolean bOverWrite = true;
								roiZ = finalROI.getRoiImages();
								//Check for existing ROI
								final String OVERWRITE_ALL = "Overwrite any existing ROIs";
								final String KEEP_EXISTING = "Keep existing ROIs when overlapping";
								final String CANCEL_ROI_UPDATE = "Cancel";
								for (int i = 0; i < roiZ.length; i++) {
									boolean bDone = false;
									short[] pixels = roiZ[i].getPixels();
									byte[] compositePixels = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
									for (int j = 0; j < compositePixels.length; j++) {
										if(compositePixels[j] != 0 && pixels[j] != 0/* && compositePixels[j] != (byte)roiColorIndex*/){
											bDone = true;
											String result = DialogUtils.showWarningDialog((Component)evt.getSource(),
													"Some areas of the new ROI overlap with existing ROIs.",
													new String[] {OVERWRITE_ALL,KEEP_EXISTING,CANCEL_ROI_UPDATE},OVERWRITE_ALL);
											if(result.equals(KEEP_EXISTING)){
												bOverWrite = false;
											}else if(result.equals(CANCEL_ROI_UPDATE)){
												overlayEditorPanelJAI.setROI(null);//Clear highlight ROI leftover from ROIAssistPanel
												return;
											}
											break;
										}
									}
									if(bDone){
										break;
									}
								}
								//Update composite ROI
								for (int i = 0; i < roiZ.length; i++) {
									short[] pixels = roiZ[i].getPixels();
									byte[] compositePixels = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
									for (int j = 0; j < pixels.length; j++) {
										if(pixels[j] != 0){
											compositePixels[j] =
												(bOverWrite?
												(byte)roiColorIndex:
													(compositePixels[j] == 0?(byte)roiColorIndex:compositePixels[j]));
										}
									}
								}
							}
							overlayEditorPanelJAI.setROI(null);//Clear highlight ROI leftover from ROIAssistPanel

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							DialogUtils.showErrorDialog((Component)evt.getSource(), "Error in ROI Assistant:\n"+e.getMessage());
						}
					}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CLEARROI_PROPERTY)){
						boolean clearIsFromDelete = evt.getSource().equals(this);
						int roiCount = overlayEditorPanelJAI.getAllCompositeROINamesAndColors().size();
						if(!clearIsFromDelete && roiCount > 1){
							final String clearAll = "Clear all ROIs";
							final String clearCurrentOnly = "Clear only current ROI";
							final String cancel = "Cancel";
							String result = DialogUtils.showWarningDialog(
									JOptionPane.getRootFrame(),
									"Clear only current ROI or clear all ROIs?",
									new String[] {clearCurrentOnly,clearAll,cancel},
									clearCurrentOnly);
							if(result == null){return;}
							if(result.equals(clearAll)){
								for (int i = 0; i < roiComposite.length; i++) {
									byte[] roiData = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
									Arrays.fill(roiData, (byte)0);
								}
								overlayEditorPanelJAI.setROI(null);
							}else if (result.equals(cancel)){
								return;
							}
						}
						//Clear current ROI
//						OverlayEditorPanelJAI.ComboboxROIName comboboxROIName =
//							(OverlayEditorPanelJAI.ComboboxROIName)evt.getOldValue();
						Color roiColor = ((OverlayEditorPanelJAI.ComboboxROIName)evt.getOldValue()).getHighlightColor();
						int roiColorIndex = -1;
						for (int i = 0; i < OverlayEditorPanelJAI.CONTRAST_COLORS.length; i++) {
							if(OverlayEditorPanelJAI.CONTRAST_COLORS[i].equals(roiColor)){
								roiColorIndex = i;
								break;
							}
						}
						for (int i = 0; i < roiComposite.length; i++) {
							byte[] roiData = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
							for (int j = 0; j < roiData.length; j++) {
								if((roiData[j]&0x000000FF) == roiColorIndex){
									roiData[j] = 0;
								}
							}
						}
						overlayEditorPanelJAI.setROI(null);
					}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_BLEND_PROPERTY)){
						overlayEditorPanelJAI.setBlendPercent((Integer)evt.getNewValue());
					}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CHECKROI_PROPERTY)){

						overlayEditorPanelJAI.setROI(null);
						//Check for disconnected regions------------------------------------------------------
						try {
							VCImage checkImage = createVCImageFromBufferedImages(imageDatasetHolder[0].getExtent(), roiComposite);
							RegionImage regionImage =
								new RegionImage(checkImage, 0 /*0 means generate no surfacecollection*/,
										checkImage.getExtent(),imageDatasetHolder[0].getAllImages()[0].getOrigin(), RegionImage.NO_SMOOTHING);
							RegionImage.RegionInfo[] allRegionInfos = regionImage.getRegionInfos();
//						if(allRegionInfos.length != vcPixelClassesFromROINames.length){
								//find multiple regions with same pixel value
								Vector<Integer> existingPixelValueV = new Vector<Integer>();
								TreeSet<Integer> duplicatePixelValueV = new TreeSet<Integer>();
								StringBuffer sb =new StringBuffer();
								for (int i = 0; i < allRegionInfos.length; i++) {
									if(!existingPixelValueV.contains(allRegionInfos[i].getPixelValue())){
										existingPixelValueV.add(allRegionInfos[i].getPixelValue());
									}else{
										duplicatePixelValueV.add(allRegionInfos[i].getPixelValue());
									}
								}
								//sort by pixel value (color) and region size for later display
								Arrays.sort(allRegionInfos,new Comparator<RegionImage.RegionInfo>() {
									public int compare(RegionImage.RegionInfo o1, RegionImage.RegionInfo o2) {
										if(o1.getPixelValue() == o2.getPixelValue()){
											return -(o1.getNumPixels() - o2.getNumPixels());
										}
										return o1.getPixelValue() - o2.getPixelValue();
									}
								});
								//construct warning list
								Vector<String> colROIName = new Vector<String>();
								final Vector<RegionImage.RegionInfo> colRegionInfo = new Vector<RegionImage.RegionInfo>();
								Hashtable<String, Color> allROINamesAndColors = overlayEditorPanelJAI.getAllCompositeROINamesAndColors();
								for (int i = 0; i < allRegionInfos.length; i++) {
									if(!duplicatePixelValueV.contains(allRegionInfos[i].getPixelValue())){
										continue;
									}
									if(allRegionInfos[i].getPixelValue() == 0){
										sb.append("name= '"+RESERVED_NAME_BACKGROUND+"' region size= "+allRegionInfos[i].getNumPixels()+"\n");
										colROIName.add(RESERVED_NAME_BACKGROUND);
										colRegionInfo.add(allRegionInfos[i]);
									}else{
										Enumeration<String> roiNameEnum2 = allROINamesAndColors.keys();
										while(roiNameEnum2.hasMoreElements()){
											String roiName = roiNameEnum2.nextElement();
											Color roiColor = allROINamesAndColors.get(roiName);
											if(OverlayEditorPanelJAI.CONTRAST_COLORS[allRegionInfos[i].getPixelValue()].equals(roiColor)){
												sb.append("name= '"+roiName+"' region size= "+allRegionInfos[i].getNumPixels()+"\n");
												colROIName.add(roiName);
												colRegionInfo.add(allRegionInfos[i]);
												break;
											}
										}
									}
								}
								//Show list
								String[][] rowData = new String[colROIName.size()][2];
								for (int i = 0; i < rowData.length; i++) {
									rowData[i][0] = colROIName.elementAt(i);
									rowData[i][1] = colRegionInfo.elementAt(i).getNumPixels()+"";
								}
								
								
								final Vector<RegionImage.RegionInfo> selectedRegionsV = new Vector<RegionImage.RegionInfo>();
								DialogUtils.showComponentOKCancelTableList(
									overlayEditorPanelJAI,
									"Select region to highlight in ROI Editor",
									new String[] {"ROI Name","ROI size (pixels)"},
									rowData,
									ListSelectionModel.MULTIPLE_INTERVAL_SELECTION,
									new ListSelectionListener() {
										public void valueChanged(ListSelectionEvent e) {
											if(!e.getValueIsAdjusting()){
												DefaultListSelectionModel defaultListSelectionModel = (DefaultListSelectionModel)e.getSource();										
												for (int i = defaultListSelectionModel.getMinSelectionIndex(); i <= defaultListSelectionModel.getMaxSelectionIndex(); i++) {
													if(defaultListSelectionModel.isSelectedIndex(i)){
														selectedRegionsV.add(colRegionInfo.elementAt(i));
													}
												}
											}
										}
									}
								);
								
								
								//Highlight selected regions
								UShortImage[] ushortRegionHighlightArr = new UShortImage[roiComposite.length];
								for (int i = 0; i < ushortRegionHighlightArr.length; i++) {
									ushortRegionHighlightArr[i] =
										new UShortImage(
												new short[imageDatasetHolder[0].getISize().getX()*imageDatasetHolder[0].getISize().getY()],
												new Origin(0, 0, 0),new Extent(1,1,1),
												imageDatasetHolder[0].getISize().getX(),
												imageDatasetHolder[0].getISize().getY(),
												1);
								}
								final ROI newCellROI = new ROI(ushortRegionHighlightArr,"highlightRegion");

									
									for (int z = 0; z < roiComposite.length; z++) {
										int index = 0;
										for (int y = 0; y < roiComposite[0].getHeight(); y++) {
											for (int x = 0; x < roiComposite[0].getWidth(); x++) {
												for (int i = 0; i < selectedRegionsV.size(); i++) {
													if(selectedRegionsV.elementAt(i).isIndexInRegion(index)){
//														System.out.println(selectedRegionsV.elementAt(i).getPixelValue()+"   "+x+" "+y+" "+z);
														newCellROI.getRoiImages()[z].getPixels()[index] = 1;
														break;
													}
												}
												index++;
											}
										}
									}

								overlayEditorPanelJAI.getRoiSouceData().addReplaceRoi(newCellROI);
								
								DialogUtils.showInfoDialog(overlayEditorPanelJAI,
										"Note: Use the 'blend' value selector (found near the 'Check...' button)"+
										" to increase the displayed intensity of the chosen ROI regions."+
										" Lower values highlight the chosen ROI regions more.");
								
						}catch (UserCancelException e) {
							//ignore
						}catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							DialogUtils.showErrorDialog(overlayEditorPanelJAI, "Error checking ROIs\n"+e.getMessage());
						}
						//---------------------------------------------------------------------------------------------------

					}
				}
			};

	overlayEditorPanelJAI.addPropertyChangeListener(roiPropertyChangeListenerHolder[0]);
	
	//Crop with mergedCropRectangle
	if(previouslyCropRectangle != null){
		roiPropertyChangeListenerHolder[0].propertyChange(
			new PropertyChangeEvent(overlayEditorPanelJAI, OverlayEditorPanelJAI.FRAP_DATA_CROP_PROPERTY, null, previouslyCropRectangle));
	}else{
		mergedCropRectangle.setBounds(0, 0, isize.getX(), isize.getY());
	}
	
	//Sanity check crop
	if(mergedCropRectangle.width != roiComposite[0].getWidth() ||
			mergedCropRectangle.height != roiComposite[0].getHeight()){
		throw new Exception("initial cropping failed");
	}

	//initialize the new roicomposite with previouslyEditedVCImage
	int index = 0;
	if(previouslyEditedVCImage != null){
		//sanity check sizes
		int totalSize = roiComposite[0].getWidth()*roiComposite[0].getHeight()*roiComposite.length;
		if(previouslyEditedVCImage.getPixels().length != totalSize){
			throw new Exception("Initial ROI composite size does not match previouslyEditedVCImage");
		}
		//copy previous to current roicomposite
		for (int i = 0; i < roiComposite.length; i++) {
			byte[] pixdata = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
			System.arraycopy(previouslyEditedVCImage.getPixels(), index, pixdata, 0, pixdata.length);
			index+= pixdata.length;
		}
		VCPixelClass[] previousPixelClasses = previouslyEditedVCImage.getPixelClasses();
		String firstROI = null;
		for (int i = 0; i < previousPixelClasses.length; i++) {
			if(previousPixelClasses[i].getPixel() == 0){//don't add background
//				if(!previousPixelClasses[i].getPixelClassName().equals(RESERVED_NAME_BACKGROUND)){
//					DialogUtils.showWarningDialog(overlayEditorPanelJAI,
//						"ROI name "+previousPixelClasses[i].getPixelClassName()+" assumed to be background and will not be initialized.");
//				}
				continue;
			}
			String nextName = previousPixelClasses[i].getPixelClassName();
			if(nextName.equals(RESERVED_NAME_BACKGROUND)){
				//Change reserved background name that didn't have a value of 0
				nextName = "ROI"+(new Random().nextInt());
			}
			if(firstROI == null){
				firstROI = nextName;
			}
			overlayEditorPanelJAI.addROIName(
					previousPixelClasses[i].getPixelClassName(), true, firstROI,
					true, OverlayEditorPanelJAI.CONTRAST_COLORS[0x000000FF&previousPixelClasses[i].getPixel()]);
		}
	}
	
	do{
		int retCode = DialogUtils.showComponentOKCancelDialog(guiParent, overlayEditorPanelJAI, "segment image for geometry");
		if (retCode == JOptionPane.OK_OPTION){
			VCImage initImage = createVCImageFromBufferedImages(imageDatasetHolder[0].getExtent(), roiComposite);
			
			//Check for unassigned "background" pixels
			boolean bHasUnassignedBackground = false;
			for (int i = 0; i < roiComposite.length; i++) {
				byte[] pixData = ((DataBufferByte)roiComposite[i].getRaster().getDataBuffer()).getData();
				for (int j = 0; j < pixData.length; j++) {
					if(pixData[j] == 0){
						bHasUnassignedBackground = true;
						break;
					}
				}
				if(bHasUnassignedBackground){
					break;
				}
			}
			//Create PixelClasses
			Hashtable<String, Color> roiNamesAndColorsHash = overlayEditorPanelJAI.getAllCompositeROINamesAndColors();
			VCPixelClass[] vcPixelClassesFromROINames = null;
			boolean bForceAssignBackground = false;
			if(bHasUnassignedBackground){
				final String assignToBackground = "Assign as default 'background'";
//				final String assignToNeighbors = "Merge gaps with neighboring ROI";
				final String cancelAssign = "Cancel, back to segmentation...";
				String result = DialogUtils.showWarningDialog(
						JOptionPane.getRootFrame(),
						"Warning: some areas of image segmentation have not been assigned to an ROI."+
						"  This can happen when small unintended gaps are left between adjacent ROIs"+
						" or areas around the edges were intentionally left as background.  Choose an action:\n"+
						"1.  Leave as is, unassigned areas should be treated as 'background'.\n"+
//						"2.  Merge unassigned gaps with a neighboring ROI.\n"+
						"2.  Go back to segmentation tool. (note: look for areas with no color)",
						new String[] {assignToBackground,/*assignToNeighbors,*/cancelAssign}, assignToBackground);
				if(result.equals(assignToBackground)){
					bForceAssignBackground = true;
				}
//				else if(result.equals(assignToNeighbors)){
//					RegionImage regionImage =
//						new RegionImage(initImage, 0 /*0 means generate no surfacecollection*/,
//								initImage.getExtent(),imageDatasetHolder[0].getAllImages()[0].getOrigin(), RegionImage.NO_SMOOTHING);
//					//Remove regions that are not background or that are adjacent to the edge of the dataset
//					RegionImage.RegionInfo[] allRegionInfos = regionImage.getRegionInfos();
//					int[] edgePixelIndexes =
//						ClientRequestManager.calculateEdgeIndexes(initImage.getNumX(),initImage.getNumY(),initImage.getNumZ());
//					for (int i = 0; i < allRegionInfos.length; i++) {
//						//exclude non-background
//						if(allRegionInfos[i].getPixelValue() == 0){
//							//exclude edge adjacent
//							for (int j = 0; j < edgePixelIndexes.length; j++) {
//								if(allRegionInfos[i] != null && allRegionInfos[i].isIndexInRegion(edgePixelIndexes[j])){
//									allRegionInfos[i] = null;
//									bForceAssignBackground = true;
//								}
//							}
//						}else{
//							allRegionInfos[i] = null;
//						}
//					}
//					//Fill with first non-zero neighbor we find
//					for (int i = 0; i < allRegionInfos.length; i++) {
//						if(allRegionInfos[i] != null){
//							Byte lastNonZeroValue = null;
//							for (int j = 0; j < initImage.getPixels().length; j++) {
//								if(allRegionInfos[i].isIndexInRegion(j)){
//									initImage.getPixels()[j] = lastNonZeroValue;
////									int z= j/(XYSIZE);
////									int y = (j-(z*XYSIZE))/initImage.getNumX();
////									int x = j-(z*XYSIZE)-y*initImage.getNumX();
//								}else if(lastNonZeroValue == null){
//									lastNonZeroValue = initImage.getPixels()[j];
//								}
//							}
//						}
//					}
//				}
				else{
					continue;
				}
				if(bForceAssignBackground){
					vcPixelClassesFromROINames = new VCPixelClass[roiNamesAndColorsHash.size()+1];
					vcPixelClassesFromROINames[0] = new VCPixelClass(null, RESERVED_NAME_BACKGROUND, 0);					
				}
			}else{
				vcPixelClassesFromROINames = new VCPixelClass[roiNamesAndColorsHash.size()];
			}
			
			
			//find pixel indexes corresponding to colors for ROIs
			index = (bForceAssignBackground?1:0);
			Enumeration<String> roiNameEnum = roiNamesAndColorsHash.keys();
			while(roiNameEnum.hasMoreElements()){
				String roiNameString = roiNameEnum.nextElement();
				Color roiColor = roiNamesAndColorsHash.get(roiNameString);
				int colorIndex = -1;
				for (int i = 0; i < cmap.length; i++) {
					if(cmap[i] == roiColor.getRGB()){
						colorIndex = i;
						break;
					}
				}
				if(colorIndex == -1){
					throw new Exception("Couldn't find colormap index for ROI "+roiNameString+" with color "+Hex.toString(roiColor.getRGB()));
				}
				vcPixelClassesFromROINames[index] =
					new VCPixelClass(null, roiNameString, colorIndex);
				index++;
			}
	
			//Sanity check VCImage vcPixelClassesFromROINames and new vcPixelClassesFromVCImage found same pixel values
			VCPixelClass[] vcPixelClassesFromVCImage = initImage.getPixelClasses();
			for (int i = 0; i < vcPixelClassesFromVCImage.length; i++) {
				boolean bFound = false;
				for (int j = 0; j < vcPixelClassesFromROINames.length; j++) {
					if(vcPixelClassesFromROINames[j].getPixel() == vcPixelClassesFromVCImage[i].getPixel()){
						bFound = true;
						break;
					}
				}
				if(!bFound){
					throw new Exception("Error processing ROI Image.  Pixels found having no matching ROI.");
				}
			}
			Vector<String> missingROINames = new Vector<String>();
			StringBuffer missingROISB = new StringBuffer();
			for (int i = 0; i < vcPixelClassesFromROINames.length; i++) {
				boolean bFound = false;
				for (int j = 0; j < vcPixelClassesFromVCImage.length; j++) {
					if(vcPixelClassesFromROINames[i].getPixel() == vcPixelClassesFromVCImage[j].getPixel()){
						bFound = true;
						break;
					}
				}
				if(!bFound){
					missingROISB.append((missingROINames.size()>0?",":"")+"'"+vcPixelClassesFromROINames[i].getPixelClassName()+"'");
					missingROINames.add(vcPixelClassesFromROINames[i].getPixelClassName());
				}
			}
			if(missingROINames.size() > 0){
				final String removeROI = "Remove ROI"+(missingROINames.size()>1?"s":"")+" and continue";
				final String backtoSegment = "Return to segmentation";
				String result = DialogUtils.showWarningDialog(
						JOptionPane.getRootFrame(), 
						"ROI"+(missingROINames.size()>1?"s":"")+" named "+missingROISB.toString()+" have no pixels defined",
						new String[] {removeROI,backtoSegment}, removeROI);
				if(result.equals(removeROI)){
					Vector<VCPixelClass> vcPixelClassV = new Vector<VCPixelClass>();
					vcPixelClassV.addAll(Arrays.asList(vcPixelClassesFromROINames));
					Hashtable<String, Color> allROINameAndColors = overlayEditorPanelJAI.getAllCompositeROINamesAndColors();
					roiNameEnum = allROINameAndColors.keys();
					while(roiNameEnum.hasMoreElements()){
						String roiName = roiNameEnum.nextElement();
						for (int i = 0; i < missingROINames.size(); i++) {
							if(missingROINames.elementAt(i).equals(roiName)){
								Color deleteThisColor = allROINameAndColors.get(roiName);
								PropertyChangeEvent propertyChangeEvent = 
									new PropertyChangeEvent(roiPropertyChangeListenerHolder[0],
											OverlayEditorPanelJAI.FRAP_DATA_CLEARROI_PROPERTY,deleteThisColor,null);
								roiPropertyChangeListenerHolder[0].propertyChange(propertyChangeEvent);
								for (int j = 0; j < vcPixelClassV.size(); j++) {
									if(vcPixelClassV.elementAt(j).getPixelClassName().equals(roiName)){
										vcPixelClassV.remove(j);
										break;
									}
								}
								break;
							}
						}
					}
					vcPixelClassesFromROINames = vcPixelClassV.toArray(new VCPixelClass[0]);
				}else{
					//return to editing segmentation
					continue;
				}
			}
	
			
//			//Check for disconnected regions------------------------------------------------------
//			PropertyChangeEvent propertyChangeEvent =
//				new PropertyChangeEvent(overlayEditorPanelJAI,
//						OverlayEditorPanelJAI.FRAP_DATA_CHECKROI_PROPERTY, null,null);
//			roiPropertyChangeListenerHolder[0].propertyChange(propertyChangeEvent);
//			//---------------------------------------------------------------------------------------------------
			
			initImage.setPixelClasses(vcPixelClassesFromROINames);
			
			Hashtable<VCImage, Rectangle> results = new Hashtable<VCImage, Rectangle>();
			results.put(initImage, mergedCropRectangle);
			//Crop rectangle included in case we need to return to this method and re-initialize the crop
			return results;
		} else{
			throw UserCancelException.CANCEL_GENERIC;
		}
	}while(true);

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
		PopupGenerator.showErrorDialog(currWindowManager, (bNewUser?"Create new":"Update")+" user Registration error:\n"+e.getMessage());
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
		PopupGenerator.showErrorDialog(currWindowManager, "Update user Registration error:\n"+e.getMessage());
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
			fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_ZIP);
			fileChooser.setFileFilter(FileFilters.FILE_FILTER_ZIP);
		    String name = evt.getVCDataIdentifier().getID();
		    String suffix = "_exported.zip";
		    File file = new File(name + suffix);
		    if (file.exists()) {
			    int count = 0;
			    do {
			    	file = new File(name + "_" + count + suffix);
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
public DataManager getDataManager(VCDataIdentifier vcDataId, boolean isSpatial) throws DataAccessException {
	//
	// Create ODE or PDE or Merged Datamanager depending on ODE or PDE or Merged data.
	//
	DataManager dataManager = null;
	VCDataManager vcDataManager = getClientServerManager().getVCDataManager();
	if (isSpatial) {
		dataManager = new PDEDataManager(vcDataManager, vcDataId);
	} else {
		dataManager = new ODEDataManager(vcDataManager, vcDataId);
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
public MergedDatasetViewerController getMergedDatasetViewerController(VCDataIdentifier vcdId, boolean expectODEData) throws DataAccessException {
	if (vcdId instanceof MergedDataInfo) {
		return new MergedDatasetViewerController(getClientServerManager().getVCDataManager(), vcdId, expectODEData);
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
public DataViewerController getDataViewerController(Simulation simulation, int jobIndex) throws DataAccessException {
	VCSimulationIdentifier vcSimulationIdentifier = simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
	final VCDataIdentifier vcdataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, jobIndex);	
	DataManager dataManager = getDataManager(vcdataIdentifier, simulation.isSpatial());
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
public AsynchClientTask[] newDocument(TopLevelWindowManager requester, final VCDocument.DocumentCreationInfo documentCreationInfo) {
	/* asynchronous and not blocking any window */
	AsynchClientTask[] taskArray1 =  createNewDocument(requester, documentCreationInfo);
	AsynchClientTask[] taskArray = new AsynchClientTask[taskArray1.length + 1];
	System.arraycopy(taskArray1, 0, taskArray, 0, taskArray1.length);
	
	AsynchClientTask taskNew1 = new AsynchClientTask("Creating New Document", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			VCDocument doc = (VCDocument)hashTable.get("doc");			
			DocumentWindowManager windowManager = createDocumentWindowManager(doc);
			getMdiManager().createNewDocumentWindow(windowManager);
		}
	};
	taskArray[taskArray1.length] = taskNew1;
	return taskArray;
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
					TranslationLogger transLogger = new TranslationLogger(requester);
					doc = XmlHelper.importSBML(transLogger, xmlInfo);
				} else if (xmlType.equals(XMLTags.CellmlRootNodeTag)) {
					if (requester instanceof BioModelWindowManager){
						TranslationLogger transLogger = new TranslationLogger(requester);
						doc = XmlHelper.importBioCellML(transLogger, xmlInfo);
					}else{
						TranslationLogger transLogger = new TranslationLogger(requester);
						doc = XmlHelper.importMathCellML(transLogger, xmlInfo);
					}
				} else { // unknown XML format
					throw new RuntimeException("unsupported XML format, first element tag is <"+rootElement.getName()+">");
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
			//we want to check when there is stochastic application if the rate laws set in model can be automatically transformed.
			String stochChkMsg =((BioModel)vcd).getModel().isValidForStochApp();
			for(int i=0; i<simulations.length; i++)
			{
				if(simulations[i].getMathDescription().isStoch())
				{
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

public void showFieldDataWindow() {
	getMdiManager().showWindow(ClientMDIManager.FIELDDATA_WINDOW_ID);
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
	AsynchClientTask task1 = new AsynchClientTask("starting exporting", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			getClientServerManager().getJobManager().startExport(exportSpecs);
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
					PopupGenerator.showErrorDialog(clientSimManager.getDocumentWindowManager(), "Failed to dispatch stop request for simulation'"+sim.getName()+"'\n"+exc.getMessage());
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


public static FieldDataFileOperationSpec createFDOSFromImageFile(File imageFile,boolean bCropOutBlack) throws DataFormatException,ImageException{
	ImageDataset imagedataSet = null;
	final FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
	try{
		imagedataSet = ImageDatasetReader.readImageDataset(imageFile.getAbsolutePath(),null);
		if (imagedataSet!=null && bCropOutBlack){
//			System.out.println("FieldDataGUIPanel.jButtonFDFromFile_ActionPerformed(): BEFORE CROPPING, size="+imagedataSet.getISize().toString());
			Rectangle nonZeroRect = imagedataSet.getNonzeroBoundingRectangle();
			if(nonZeroRect != null){
				imagedataSet = imagedataSet.crop(nonZeroRect);
			}
//			System.out.println("FieldDataGUIPanel.jButtonFDFromFile_ActionPerformed(): AFTER CROPPING, size="+imagedataSet.getISize().toString());
		}
	}catch (Exception e){
		e.printStackTrace(System.out);
		throw new DataFormatException(e.getMessage());
	}
	//[time][var][data]
	int numXY = imagedataSet.getISize().getX()*imagedataSet.getISize().getY();
	int numXYZ = imagedataSet.getSizeZ()*numXY;
	fdos.variableTypes = new VariableType[imagedataSet.getSizeC()];
	fdos.varNames = new String[imagedataSet.getSizeC()];
	short[][][] shortData =
		new short[imagedataSet.getSizeT()][imagedataSet.getSizeC()][numXYZ];
	for(int c=0;c<imagedataSet.getSizeC();c+= 1){
		fdos.variableTypes[c] = VariableType.VOLUME;
		fdos.varNames[c] = "Channel"+c;
		for(int t=0;t<imagedataSet.getSizeT();t+=1){
			int zOffset = 0;
			for(int z=0;z<imagedataSet.getSizeZ();z+=1){
				UShortImage ushortImage = imagedataSet.getImage(z,c,t);
				System.arraycopy(ushortImage.getPixels(), 0, shortData[t][c], zOffset, numXY);
//				shortData[t][c] = ushortImage.getPixels();
				zOffset+= numXY;
			}
		}
	}
	fdos.shortSpecData = shortData;
	fdos.times = imagedataSet.getImageTimeStamps();
	if(fdos.times == null){
		fdos.times = new double[imagedataSet.getSizeT()];
		for(int i=0;i<fdos.times.length;i+= 1){
			fdos.times[i] = i;
		}
	}

	fdos.origin = (imagedataSet.getAllImages()[0].getOrigin() != null?imagedataSet.getAllImages()[0].getOrigin():new Origin(0,0,0));
	fdos.extent = (imagedataSet.getExtent()!=null)?(imagedataSet.getExtent()):(new Extent(1,1,1));
	fdos.isize = imagedataSet.getISize();
	
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
/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jlibsedml.AbstractTask;
import org.jlibsedml.ArchiveComponents;
import org.jlibsedml.Libsedml;
import org.jlibsedml.SEDMLDocument;
import org.jlibsedml.SedML;
import org.vcell.imagej.ImageJHelper;
import org.vcell.imagej.ImageJHelper.ImageJConnection;
import org.vcell.model.bngl.ASTModel;
import org.vcell.model.bngl.BngUnitSystem;
import org.vcell.model.bngl.BngUnitSystem.BngUnitOrigin;
import org.vcell.model.bngl.gui.BNGLDebuggerPanel;
import org.vcell.model.bngl.gui.BNGLUnitsPanel;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.RbmUtils.BnglObjectConstructionVisitor;
import org.vcell.sedml.SEDMLChooserPanel;
import org.vcell.util.BeanUtils;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Issue;
import org.vcell.util.Origin;
import org.vcell.util.ProgrammingException;
import org.vcell.util.TokenMangler;
import org.vcell.util.UserCancelException;
import org.vcell.util.VCAssert;
import org.vcell.util.VCellThreadChecker;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.CurateSpec;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelChildSummary;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocument.DocumentCreationInfo;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.VersionableType;
import org.vcell.util.document.VersionableTypeVersion;
import org.vcell.util.gui.AsynchGuiUpdater;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.SimpleUserMessage;
import org.vcell.util.gui.UtilCancelException;
import org.vcell.util.gui.VCFileChooser;
import org.vcell.util.gui.exporter.ExtensionFilter;
import org.vcell.util.gui.exporter.FileFilters;
import org.vcell.util.importer.PathwayImportPanel.PathwayImportOption;

import cbit.gui.ImageResizePanel;
import cbit.image.ImageException;
import cbit.image.ImageSizeInfo;
import cbit.image.VCImage;
import cbit.image.VCImageInfo;
import cbit.image.VCImageUncompressed;
import cbit.image.VCPixelClass;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportEvent.AnnotatedExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.rmi.event.VCellMessageEvent;
import cbit.rmi.event.VCellMessageEventListener;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReader;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReaderFactory;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.VirtualMicroscopy.importer.MicroscopyXMLTags;
import cbit.vcell.VirtualMicroscopy.importer.VFrapXmlHelper;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.TopLevelWindowManager.OpenModelInfoHolder;
import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.client.desktop.biomodel.DocumentEditor;
import cbit.vcell.client.server.AsynchMessageManager;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.server.DataViewerController;
import cbit.vcell.client.server.MergedDatasetViewerController;
import cbit.vcell.client.server.SimResultsViewerController;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.AsynchClientTaskFunction;
import cbit.vcell.client.task.CheckBeforeDelete;
import cbit.vcell.client.task.CheckUnchanged;
import cbit.vcell.client.task.ChooseFile;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.CommonTask;
import cbit.vcell.client.task.DeleteOldDocument;
import cbit.vcell.client.task.DocumentToExport;
import cbit.vcell.client.task.DocumentValid;
import cbit.vcell.client.task.ExportDocument;
import cbit.vcell.client.task.FinishExport;
import cbit.vcell.client.task.FinishSave;
import cbit.vcell.client.task.NewName;
import cbit.vcell.client.task.RunSims;
import cbit.vcell.client.task.SaveDocument;
import cbit.vcell.client.task.SetMathDescription;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.ImageDbTreePanel;
import cbit.vcell.desktop.LoginManager;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.CSGObject;
import cbit.vcell.geometry.CSGPrimitive;
import cbit.vcell.geometry.CSGScale;
import cbit.vcell.geometry.CSGTranslation;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.gui.ROIMultiPaintManager;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.RayCaster;
import cbit.vcell.geometry.surface.StlReader;
import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.gui.MathMappingCallbackTaskAdapter;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.VariableType;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Structure;
import cbit.vcell.numericstest.ModelGeometryOP;
import cbit.vcell.numericstest.ModelGeometryOPResults;
import cbit.vcell.parser.Expression;
import cbit.vcell.render.Vect3d;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simdata.DataManager;
import cbit.vcell.simdata.MergedDataInfo;
import cbit.vcell.simdata.ODEDataManager;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.simdata.VtkManager;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.xml.ExternalDocInfo;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlHelper;
import cbit.xml.merge.XmlTreeDiff;
import cbit.xml.merge.XmlTreeDiff.DiffConfiguration;
import cbit.xml.merge.gui.TMLPanel;
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
private static final String VERSIONINFO_KEY = "VERSIONINFO_KEY";
private AsynchClientTask createSelectDocTask(final TopLevelWindowManager requester){
	AsynchClientTask selectDocumentTypeTask = new AsynchClientTask("Select/Load geometry", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			String[][] docTypeoptions = new String[][] {{"BioModel names"},{"MathModel names"}};
			VCDocumentType[] sourceDocumentTypes = new VCDocumentType[] { VCDocumentType.BIOMODEL_DOC, VCDocumentType.MATHMODEL_DOC};
			VCAssert.assertTrue(docTypeoptions.length == sourceDocumentTypes.length , "Label and types mismatch" );
			int[] geomType = DialogUtils.showComponentOKCancelTableList(
				JOptionPane.getFrameForComponent(requester.getComponent()),
				"Select different Geometry",
				new String[] {"Search by"},
				docTypeoptions,
				ListSelectionModel.SINGLE_SELECTION);
			final int selectedType = geomType[0];
			VCDocumentType sourceDocumentType = sourceDocumentTypes[selectedType];
			VersionInfo vcVersionInfo = null;
			if(geomType[0] == 3){
				ImageDbTreePanel imageDbTreePanel = new ImageDbTreePanel();
				imageDbTreePanel.setDocumentManager(getDocumentManager());
				imageDbTreePanel.setPreferredSize(new java.awt.Dimension(200, 400));
				vcVersionInfo = DialogUtils.getDBTreePanelSelection(requester.getComponent(), imageDbTreePanel,"OK","Select Image:");
			}else{
				vcVersionInfo = selectDocumentFromType(sourceDocumentType, requester);
			}
			hashTable.put(VERSIONINFO_KEY, vcVersionInfo);
		}
	};
	return selectDocumentTypeTask;
}

private AsynchClientTask createSelectLoadGeomTask(final TopLevelWindowManager requester){
	AsynchClientTask selectLoadGeomTask = new AsynchClientTask("Select/Load geometry...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			VersionInfo vcVersionInfo = (VersionInfo)hashTable.get(VERSIONINFO_KEY);
			Geometry geom = null;
			if(vcVersionInfo instanceof VCDocumentInfo){
				geom = getGeometryFromDocumentSelection(requester.getComponent(),(VCDocumentInfo)vcVersionInfo, false);
			}else if(vcVersionInfo instanceof VCImageInfo){
				VCImage img = getDocumentManager().getImage((VCImageInfo)vcVersionInfo);
				geom = new Geometry("createSelectLoadGeomTask", img);
			}else{
				throw new Exception("Unexpected versioninfo type.");
			}
			geom.precomputeAll(new GeometryThumbnailImageFactoryAWT());//pregenerate sampled image, cpu intensive
			hashTable.put(GEOMETRY_KEY, geom);
		}
	};
	return selectLoadGeomTask;
}

private void changeGeometry0(final TopLevelWindowManager requester, final SimulationContext simContext) {

	AsynchClientTask selectDocumentTypeTask = createSelectDocTask(requester);

	AsynchClientTask selectLoadGeomTask = createSelectLoadGeomTask(requester);

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
			new AsynchClientTask[] {selectDocumentTypeTask, selectLoadGeomTask,processGeometryTask,setNewGeometryTask}, false);

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
		exc.printStackTrace();
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
public boolean isDifferentFromBlank(VCDocumentType documentType, VCDocument vcDocument) {
	// Handle Bio/Math models different from Geometry since createDefaultDoc for Geometry
	// will bring up the NewGeometryEditor which is unnecessary.
	// figure out if we come from a blank new document; if so, replace it inside same window

	if (documentType != vcDocument.getDocumentType()) {
		// If the docType we are trying to open is not the same as the requesting windowmanager's docType
		// we have to open the doc in a new window, hence return true;
		return true;
	}
	VCDocument blank = null;
	if (vcDocument.getDocumentType() != VCDocumentType.GEOMETRY_DOC) {
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
	//create copy to avoid ConcurrentModification exception caused by closing window
	ArrayList<TopLevelWindowManager> modificationSafeCopy = new ArrayList<>(getMdiManager().getWindowManagers());
	for (TopLevelWindowManager windowManager  : modificationSafeCopy) {
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
						long openWindows = getMdiManager().closeWindow(windowManager.getManagerID());
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
			long openWindows = getMdiManager().closeWindow(windowID);
			if (exitIfLast && (openWindows == 0)) {
				setBExiting(true);
				exitApplication();
			}
			return true;
		}
	} else if (windowManager instanceof DatabaseWindowManager) {
		// nothing to check here, just close it
		long openWindows = getMdiManager().closeWindow(windowID);
		if (exitIfLast && (openWindows == 0)) {
			setBExiting(true);
			exitApplication();
		}
		return true;
	} else if (windowManager instanceof TestingFrameworkWindowManager) {
		// nothing to check here, just close it
		long openWindows = getMdiManager().closeWindow(windowID);
		if (exitIfLast && (openWindows == 0)) {
			setBExiting(true);
			exitApplication();
		}
		return true;
	} else if (windowManager instanceof BNGWindowManager) {
		// nothing to check here, just close it
		long openWindows = getMdiManager().closeWindow(windowID);
		if (exitIfLast && (openWindows == 0)) {
			setBExiting(true);
			exitApplication();
		}
		return true;
	} else if (windowManager instanceof FieldDataWindowManager) {
		// nothing to check here, just close it
		long openWindows = getMdiManager().closeWindow(windowID);
		if (exitIfLast && (openWindows == 0)) {
			setBExiting(true);
			exitApplication();
		}
		return true;
	} else {
		return false;
	}
}


private XmlTreeDiff compareDocuments(final VCDocument doc1, final VCDocument doc2, DiffConfiguration comparisonSetting) throws Exception {

	VCellThreadChecker.checkCpuIntensiveInvocation();

	if ((DiffConfiguration.COMPARE_DOCS_SAVED != comparisonSetting) &&
		(DiffConfiguration.COMPARE_DOCS_OTHER != comparisonSetting)) {
		throw new RuntimeException("Unsupported comparison setting: " + comparisonSetting);
	}
	if (doc1.getDocumentType() != doc2.getDocumentType()) {
		throw new RuntimeException("Only comparison of documents of the same type is currently supported");
	}
	String doc1XML = null;
	String doc2XML = null;
	switch (doc1.getDocumentType()) {
		case BIOMODEL_DOC: {
			doc1XML = XmlHelper.bioModelToXML((BioModel)doc1);
			doc2XML = XmlHelper.bioModelToXML((BioModel)doc2);
			break;
		}
		case MATHMODEL_DOC: {
			doc1XML = XmlHelper.mathModelToXML((MathModel)doc1);
			doc2XML = XmlHelper.mathModelToXML((MathModel)doc2);
			break;
		}
		case GEOMETRY_DOC: {
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
		return compareDocuments(document1, document2, DiffConfiguration.COMPARE_DOCS_OTHER);
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
			case BIOMODEL_DOC: {
				BioModel bioModel = (BioModel)document;
				savedVersion = getDocumentManager().getBioModel(bioModel.getVersion().getVersionKey());
				break;
			}
			case MATHMODEL_DOC: {
				MathModel mathModel = (MathModel)document;
				savedVersion = getDocumentManager().getMathModel(mathModel.getVersion().getVersionKey());
				break;
			}
			case GEOMETRY_DOC: {
				Geometry geometry = (Geometry)document;
				savedVersion = getDocumentManager().getGeometry(geometry.getKey());
				break;
			}
		}
		return compareDocuments(savedVersion, document, DiffConfiguration.COMPARE_DOCS_SAVED);
	} catch (Exception e) {
		e.printStackTrace();
		throw new RuntimeException(e.getMessage());
	}
}


public XmlTreeDiff compareApplications(BioModel bioModel, String appName1, String appName2) throws Exception {

	// clone BioModel as bioModel1 and remove all but appName1
	BioModel bioModel1 = (BioModel)BeanUtils.cloneSerializable(bioModel);
	bioModel1.refreshDependencies();
	SimulationContext[] allSimContexts1 = bioModel1.getSimulationContexts();
	for (SimulationContext sc : allSimContexts1){
		if (!sc.getName().equals(appName1)){
			bioModel1.removeSimulationContext(sc);
		}
	}

	// clone BioModel as bioModel2 and remove all but appName2
	BioModel bioModel2 = (BioModel)BeanUtils.cloneSerializable(bioModel);
	bioModel2.refreshDependencies();
	SimulationContext[] allSimContexts2 = bioModel2.getSimulationContexts();
	for (SimulationContext sc : allSimContexts2){
		if (!sc.getName().equals(appName2)){
			bioModel2.removeSimulationContext(sc);
		}
	}

	return compareDocuments(bioModel1, bioModel2, DiffConfiguration.COMPARE_DOCS_SAVED);
}


/**
 * Insert the method's description here.
 * Creation date: (6/16/2004 11:07:33 AM)
 * @param clientServerInfo cbit.vcell.client.server.ClientServerInfo
 */
public void connectAs(final String user,  final DigestedPassword digestedPassword, final TopLevelWindowManager requester) {
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
						long numOpenWindows = getMdiManager().closeWindow(ClientMDIManager.DATABASE_WINDOW_ID);
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
			AsynchClientTask[] newTasks = newDocument(requester, new VCDocument.DocumentCreationInfo(VCDocumentType.BIOMODEL_DOC, 0));
			AsynchClientTask task0 = new AsynchClientTask("preparing", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					DocumentWindowManager windowManager = (DocumentWindowManager) hashTable.get("windowManager");
					if (windowManager != null) {
				    	Frame frameParent = JOptionPane.getFrameForComponent(windowManager.getComponent());
				    	ClientMDIManager.blockWindow(frameParent);
				    	frameParent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				    }
				}
			};
			AsynchClientTask task1 = new AsynchClientTask(taskName, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					getClientServerManager().connectAs(requester, user, digestedPassword);
				}
			};
			AsynchClientTask task2 = new AsynchClientTask(taskName, AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, false) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					getMdiManager().refreshRecyclableWindows();
					DocumentWindowManager windowManager = (DocumentWindowManager) hashTable.get("windowManager");
					if (windowManager != null) {
				    	Frame frameParent = JOptionPane.getFrameForComponent(windowManager.getComponent());
				    	ClientMDIManager.unBlockWindow(frameParent);
				    	frameParent.setCursor(Cursor.getDefaultCursor());
				    }
				}
			};
			AsynchClientTask[] taskArray = new AsynchClientTask[newTasks.length + 4];
			taskArray[0] = waitTask;
			System.arraycopy(newTasks, 0, taskArray, 1, newTasks.length);
			taskArray[taskArray.length - 3] = task0;
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
	getClientServerManager().connectNewServer(requester,clientServerInfo);
}


/**
 * Insert the method's description here.
 * Creation date: (5/10/2004 3:48:16 PM)
 */
VCDocument createDefaultDocument(VCDocumentType docType) {
	VCDocument defaultDocument = null;
	try {
		switch (docType) {
			case BIOMODEL_DOC: {
				// blank
				return createDefaultBioModelDocument(null);
			}
			case MATHMODEL_DOC: {
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

	switch (simContext.getApplicationType()) {
	case NETWORK_STOCHASTIC:
		break;
	case RULE_BASED_STOCHASTIC:
	case NETWORK_DETERMINISTIC:
	}

	AsynchClientTask task1 = new AsynchClientTask("Creating MathModel from BioModel Application", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
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
			DocumentWindow dw = getMdiManager().createNewDocumentWindow(windowManager);
			setFinalWindow(hashTable, dw);
		}
	};
	ClientTaskDispatcher.dispatch(requester.getComponent(), new Hashtable<String, Object>(),  new AsynchClientTask[]{task1, task2}, false);
}
public void createBioModelFromApplication(final BioModelWindowManager requester, final String name, final SimulationContext simContext) {
	if (simContext == null) {
		PopupGenerator.showErrorDialog(requester, "Selected Application is null, cannot generate corresponding bio model");
		return;
	}
	if(simContext.isRuleBased()) {
		createRuleBasedBioModelFromApplication(requester, name, simContext);
		return;
	}
	AsynchClientTask task1 = new AsynchClientTask("Creating BioModel from BioModel Application", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {

			MathMappingCallback dummyCallback = new MathMappingCallback() {
					public void setProgressFraction(float percentDone) {}
					public void setMessage(String message) {}
					public boolean isInterrupted() { return false; }
				};
			MathMapping transformedMathMapping = simContext.createNewMathMapping(dummyCallback, NetworkGenerationRequirements.ComputeFullStandardTimeout);

			BioModel newBioModel = new BioModel(null);
			SimulationContext transformedSimContext = transformedMathMapping.getTransformation().transformedSimContext;
			Model newModel = transformedSimContext.getModel();
			newBioModel.setModel(newModel);

			RbmModelContainer rbmmc = newModel.getRbmModelContainer();
			for(RbmObservable o : rbmmc.getObservableList()) {
				rbmmc.removeObservable(o);
			}
			for(ReactionRule r : rbmmc.getReactionRuleList()) {
				rbmmc.removeReactionRule(r);
			}
			for(ReactionStep rs : newModel.getReactionSteps()) {
				String oldName = rs.getName();
				if(oldName.startsWith("_reverse_")) {
					String newName = newModel.getReactionName("rev", oldName.substring("_reverse_".length()));
					rs.setName(newName);
				}
			}

			hashTable.put("newBioModel", newBioModel);
		}
	};
	AsynchClientTask task2 = new AsynchClientTask("Creating BioModel from BioModel Application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			BioModel newBioModel = (BioModel)hashTable.get("newBioModel");
			DocumentWindowManager windowManager = createDocumentWindowManager(newBioModel);
//			if(simContext.getBioModel().getVersion() != null){
//				((BioModelWindowManager)windowManager). setCopyFromBioModelAppVersionableTypeVersion(
//							new VersionableTypeVersion(VersionableType.BioModelMetaData, simContext.getBioModel().getVersion()));
//			}
			getMdiManager().createNewDocumentWindow(windowManager);
		}
	};
	ClientTaskDispatcher.dispatch(requester.getComponent(), new Hashtable<String, Object>(),  new AsynchClientTask[]{task1, task2}, false);
}
public void createRuleBasedBioModelFromApplication(final BioModelWindowManager requester, final String name, final SimulationContext simContext) {
	if (simContext == null) {
		PopupGenerator.showErrorDialog(requester, "Selected Application is null, cannot generate corresponding bio model");
		return;
	}
	AsynchClientTask task1 = new AsynchClientTask("Creating BioModel from BioModel Application", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {

			MathMappingCallback dummyCallback = new MathMappingCallback() {
					public void setProgressFraction(float percentDone) {}
					public void setMessage(String message) {}
					public boolean isInterrupted() { return false; }
				};
			MathMapping transformedMathMapping = simContext.createNewMathMapping(dummyCallback, NetworkGenerationRequirements.ComputeFullStandardTimeout);
//			simContext.setMathDescription(transformedMathMapping.getMathDescription());

			BioModel newBioModel = new BioModel(null);
			SimulationContext transformedSimContext = transformedMathMapping.getTransformation().transformedSimContext;
			Model model = transformedSimContext.getModel();

//			for(ReactionStep rs : model.getReactionSteps()) {
//				model.removeReactionStep(rs);
//			}

			newBioModel.setModel(model);

			hashTable.put("newBioModel", newBioModel);
		}
	};
	AsynchClientTask task2 = new AsynchClientTask("Creating BioModel from BioModel Application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			BioModel newBioModel = (BioModel)hashTable.get("newBioModel");
			DocumentWindowManager windowManager = createDocumentWindowManager(newBioModel);
			getMdiManager().createNewDocumentWindow(windowManager);
		}
	};
	ClientTaskDispatcher.dispatch(requester.getComponent(), new Hashtable<String, Object>(),  new AsynchClientTask[]{task1, task2}, false);
}
//public void createBioModelFromApplication(final BioModelWindowManager requester, final String name, final SimulationContext simContext) {
//	if (simContext == null) {
//		PopupGenerator.showErrorDialog(requester, "Selected Application is null, cannot generate corresponding bio model");
//		return;
//	}
//	AsynchClientTask task1 = new AsynchClientTask("Creating BioModel from BioModel Application", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
//		@Override
//		public void run(Hashtable<String, Object> hashTable) throws Exception {
//
//
//			BioModel newBioModel = new BioModel(null);
//
//			boolean bStochastic = false;
//			boolean bRuleBased = false;
//			newBioModel.setModel(simContext.getModel());
//			newBioModel.addSimulationContext(simContext);
//
//			hashTable.put("newBioModel", newBioModel);
//		}
//	};
//
//	AsynchClientTask task2 = new AsynchClientTask("Creating BioModel from BioModel Application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
//		@Override
//		public void run(Hashtable<String, Object> hashTable) throws Exception {
//			BioModel newBioModel = (BioModel)hashTable.get("newBioModel");
//			DocumentWindowManager windowManager = createDocumentWindowManager(newBioModel);
////			if(simContext.getBioModel().getVersion() != null){
////				((BioModelWindowManager)windowManager). setCopyFromBioModelAppVersionableTypeVersion(
////							new VersionableTypeVersion(VersionableType.BioModelMetaData, simContext.getBioModel().getVersion()));
////			}
//			getMdiManager().createNewDocumentWindow(windowManager);
//		}
//	};
//	ClientTaskDispatcher.dispatch(requester.getComponent(), new Hashtable<String, Object>(),  new AsynchClientTask[]{task1, task2}, false);
//}
private BioModel createDefaultBioModelDocument(BngUnitSystem bngUnitSystem) throws Exception {
	BioModel bioModel = new BioModel(null);
	bioModel.setName("BioModel" + (getMdiManager().getNumCreatedDocumentWindows() + 1));

	Model model;
	if(bngUnitSystem == null) {
		model = new Model("model");
	} else {
		model = new Model("model", bngUnitSystem.createModelUnitSystem());
	}
	bioModel.setModel(model);

	model.createFeature();
	return bioModel;
}

private MathModel createDefaultMathModelDocument() throws Exception {
	Geometry geometry = new Geometry("Untitled", 0);
	MathModel mathModel = createMathModel("Untitled", geometry);
	mathModel.setName("MathModel" + (getMdiManager().getNumCreatedDocumentWindows() + 1));
	return mathModel;
}

public VCDocumentInfo selectDocumentFromType(VCDocumentType documentType, TopLevelWindowManager requester) throws Exception,UserCancelException{
	return getMdiManager().getDatabaseWindowManager().selectDocument(documentType, requester);
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
				ModelGeometryOPResults modelGeometryOPResults =
					(ModelGeometryOPResults)getDocumentManager().getSessionManager().getUserMetaDbServer().doTestSuiteOP(new ModelGeometryOP((BioModelInfo)vcDocumentInfo, rowData[selection[0]][0]));
				geom = getDocumentManager().getGeometry(modelGeometryOPResults.getGeometryKey());
//				BioModel bioModel = getDocumentManager().getBioModel((BioModelInfo)vcDocumentInfo);
//				for (int i = 0; i < bioModel.getSimulationContexts().length; i++) {
//					if(bioModel.getSimulationContexts()[i].getName().equals(rowData[selection[0]][0])){
//						geom = bioModel.getSimulationContexts()[i].getGeometry();
//						break;
//					}
//				}
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
				ModelGeometryOPResults modelGeometryOPResults =
					(ModelGeometryOPResults)getDocumentManager().getSessionManager().getUserMetaDbServer().doTestSuiteOP(new ModelGeometryOP((MathModelInfo)vcDocumentInfo));
				geom = getDocumentManager().getGeometry(modelGeometryOPResults.getGeometryKey());
//				MathModel mathModel = getDocumentManager().getMathModel(mathModelInfo);
//				geom = mathModel.getMathDescription().getGeometry();
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
	documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FIELDDATA ||
	documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FILE ||
	documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FROM_SCRATCH ||
	documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FROM_WORKSPACE_ANALYTIC ||
	documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FROM_WORKSPACE_IMAGE ||
	documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FIJI_IMAGEJ ||
	documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_BLENDER
	;
}

private static void throwImportWholeDirectoryException(File invalidFile,String extraInfo) throws Exception{
	throw new Exception("Import whole directory failed: directory '"+invalidFile.getAbsolutePath()+"' "+
			"contains invalid file.  Import from whole directory can contain only files for "+
			"a single z-series, each file must be 2D, single time.  "+
			"All files must be the same size and have the same number color channels."+
			(extraInfo==null?"":"\n"+extraInfo));

}

private static FieldDataFileOperationSpec createFDOSFromVCImage(VCImage dbImage) throws ImageException{
	int[] temp = new int[256];
	short[] templateShorts = new short[dbImage.getNumXYZ()];
	for (int i = 0; i < dbImage.getPixels().length; i++) {
		templateShorts[i] = (short)(0x00FF&dbImage.getPixels()[i]);
		temp[templateShorts[i]]++;
	}
	for (int j = 0; j < dbImage.getPixelClasses().length; j++) {
		short tempshort = (short)(0x00FF&dbImage.getPixelClasses()[j].getPixel());
	}
	FieldDataFileOperationSpec fdfos = null;
	fdfos = new FieldDataFileOperationSpec();
	fdfos.origin = new Origin(0,0,0);
	fdfos.extent = dbImage.getExtent();
	fdfos.isize = new ISize(dbImage.getNumX(), dbImage.getNumY(), dbImage.getNumZ());
	fdfos.shortSpecData = new short[][][] {{templateShorts}};
	return fdfos;
}

public static SurfaceCollection createSurfaceCollectionFromSurfaceFile(File surfaceFile) throws Exception{
	SurfaceCollection surfaceCollection = null;
	if(ExtensionFilter.isMatchingExtension(surfaceFile, ".stl")){
		surfaceCollection = StlReader.readStl(surfaceFile);
	}else if(ExtensionFilter.isMatchingExtension(surfaceFile, ".mesh")){//NOT VCell mesh, salk Hughes Hoppe mesh
		//convert to .stl and read
		BufferedWriter stlBufferedWriter = null;
		File tempstlFile = File.createTempFile("salk",".stl");
		try{
			try (BufferedReader salkBufferedReader = new BufferedReader(new FileReader(surfaceFile))) {
				String line;
				ArrayList<double[]> vertList = new ArrayList<double[]>();
				//read vertices
				while((line = salkBufferedReader.readLine()) != null){
					StringTokenizer st = new StringTokenizer(line," ");
					String firstToken = st.nextToken();
					int vertIndex = Integer.parseInt(st.nextToken());
					if(firstToken.equals("Vertex")){
						vertList.add(new double[] {Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken())});
						if(vertList.size() != vertIndex){//numbering starts at 1
							throw new Exception("Index not match position in list");
						}
					}else if(firstToken.equals("Face")){
						break;//read all vertices
					}else{
						return null;
					}
				}
				stlBufferedWriter = new BufferedWriter(new FileWriter(tempstlFile));
				stlBufferedWriter.write("solid VCell salk convert\n");
				//read faces
				do{
					stlBufferedWriter.write("facet normal 0 0 0\n");
					stlBufferedWriter.write("outer loop\n");
					StringTokenizer st = new StringTokenizer(line," ");
					String firstToken = st.nextToken();
					Integer.parseInt(st.nextToken());//ignore index token
					if(firstToken.equals("Face")){
						for (int i = 0; i < 3; i++) {
							int vertListIndex = Integer.parseInt(st.nextToken())-1;
							double[] vertCoordinatges = vertList.get(vertListIndex);
							stlBufferedWriter.write("vertex "+vertCoordinatges[0]+" "+vertCoordinatges[1]+" "+vertCoordinatges[2]+"\n");//indexes start at 1, not 0
						}
					}else{
						throw new Exception("Expecting token 'Face' but got "+firstToken);
					}
					stlBufferedWriter.write("endloop\n");
					stlBufferedWriter.write("endfacet\n");
				}while((line = salkBufferedReader.readLine()) != null);
				stlBufferedWriter.write("endsolid VCell salk convert\n");

				stlBufferedWriter.close();
				surfaceCollection = StlReader.readStl(tempstlFile);
			}
		}catch(Exception e){
			//we couldn't read this for some reason
			e.printStackTrace();
		}finally{
			if(stlBufferedWriter != null){try{stlBufferedWriter.close();}catch(Exception e){e.printStackTrace();}}
			if(tempstlFile != null){tempstlFile.delete();}
		}
	}
	return surfaceCollection;
}

public static FieldDataFileOperationSpec createFDOSFromSurfaceFile(File surfaceFile) throws Exception{
	SurfaceCollection surfaceCollection = createSurfaceCollectionFromSurfaceFile(surfaceFile);
	if(surfaceCollection != null){
		Geometry geometry = RayCaster.createGeometryFromSTL(new GeometryThumbnailImageFactoryAWT(), surfaceCollection, 1000000);
		FieldDataFileOperationSpec fdfos = new FieldDataFileOperationSpec();
		fdfos.origin = geometry.getOrigin();
		fdfos.extent = geometry.getExtent();
		VCImage image = geometry.getGeometrySpec().getImage();
		if(image.getNumPixelClasses() == 1){
			throw new Exception("STL import failed during processing, pixelclass count=1");
		}
		fdfos.isize = new ISize(image.getNumX(),image.getNumY(),image.getNumZ());
		byte[] pixels = image.getPixels();
		short[] dataToSegment = new short[image.getNumXYZ()];
		for (int i=0;i<pixels.length;i++){
			dataToSegment[i] = pixels[i];
		}
		fdfos.shortSpecData = new short[][][] {{dataToSegment}};
		return fdfos;
	}
	return null;
}


public static final String GEOM_FROM_WORKSPACE = "GEOM_FROM_WORKSPACE";
public static final String VCPIXELCLASSES = "VCPIXELCLASSES";
private enum NRRDTYPE {DOUBLE,FLOAT,UNSIGNEDCHAR};
private enum NRRDENCODING {RAW,GZIP};

public AsynchClientTask[] createNewGeometryTasks(final TopLevelWindowManager requester,
		final VCDocument.DocumentCreationInfo documentCreationInfo,
		final AsynchClientTask[] afterTasks,
		final String okButtonText){

	if(!isImportGeometryType(documentCreationInfo)){
		throw new IllegalArgumentException("Analytic geometry not implemented.");

	}
	final String IMPORT_SOURCE_NAME = "IMPORT_SOURCE_NAME";

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
	final String ORIG_IMAGE_SIZE_INFO = "ORIG_IMAGE_SIZE_INFO";
	final String NEW_IMAGE_SIZE_INFO = "NEW_IMAGE_SIZE_INFO";
	final String DIR_FILES = "DIR_FILES";
	final String FD_MESH = "FD_MESH";
	final String FD_MESHISIZE = "FD_MESHISIZE";
	final String FD_TIMEPOINTS = "FD_TIMEPOINTS";
	AsynchClientTask parseImageTask = new AsynchClientTask("read and parse image file", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(final Hashtable<String, Object> hashTable) throws Exception {

			final Component guiParent =(Component)hashTable.get(ClientRequestManager.GUI_PARENT);
			try {
				FieldDataFileOperationSpec fdfos = null;
				if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FIJI_IMAGEJ){
					hashTable.put("imageFile",ImageJHelper.vcellWantImage(getClientTaskStatusSupport(),"Image for new VCell geometry"));
				}
				if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_BLENDER){
					hashTable.put("imageFile",ImageJHelper.vcellWantSurface(getClientTaskStatusSupport(),"Image for new VCell geometry"));
				}
				if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FILE ||
					documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FIJI_IMAGEJ ||
					documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_BLENDER){
					
					File imageFile = (File)hashTable.get("imageFile");
					if(imageFile == null){
						throw new Exception("No file selected");
					}
					if (ExtensionFilter.isMatchingExtension(imageFile, ".nrrd")){


						DataInputStream dis = null;
						try{
							dis = new DataInputStream(new BufferedInputStream(new FileInputStream(imageFile)));
							int xsize = 1;
							int ysize = 1;
							int zsize = 1;
							double xspace = 1.0;
							double yspace = 1.0;
							double zspace = 1.0;
							NRRDTYPE type = null;
							NRRDENCODING encoding = null;
							int dimension = -1;
							//read header lines
							while(true){
								@SuppressWarnings("deprecation")
								String line = dis.readLine();
								if(line == null || line.length() == 0){
									break;
								}
								StringTokenizer stringTokenizer = new StringTokenizer(line, ": ");
								String headerParam = stringTokenizer.nextToken();
//								System.out.println(headerParam);
								if(headerParam.equals("sizes")){
									if(dimension != -1){
										xsize = Integer.parseInt(stringTokenizer.nextToken());
										if(dimension >= 2){ysize = Integer.parseInt(stringTokenizer.nextToken());}
										if(dimension >= 3){zsize = Integer.parseInt(stringTokenizer.nextToken());}
										for (int i = 4; i < dimension; i++) {
											if(Integer.parseInt(stringTokenizer.nextToken()) != 1){throw new Exception("Dimensions > 3 not supported");}
										}
									}else{
										throw new Exception("dimension expected to be set before reading sizes");
									}
								}else if(headerParam.equals("spacings")){
									if(dimension != -1){
										xspace = Double.parseDouble(stringTokenizer.nextToken());
										if(dimension >= 2){yspace = Double.parseDouble(stringTokenizer.nextToken());}
										if(dimension >= 3){zspace = Double.parseDouble(stringTokenizer.nextToken());}
										//ignore other dimension spacings
									}else{
										throw new Exception("dimension expected to be set before reading spacings");
									}
								}else if(headerParam.equals("type")){
									String nextToken = stringTokenizer.nextToken();
									if(nextToken.equalsIgnoreCase("double")){
										type = NRRDTYPE.DOUBLE;
									}else if(nextToken.equalsIgnoreCase("float")){
										type = NRRDTYPE.FLOAT;
									}else if(nextToken.equalsIgnoreCase("unsigned")){
										nextToken = stringTokenizer.nextToken();
										if(nextToken.equalsIgnoreCase("char")){
											type = NRRDTYPE.UNSIGNEDCHAR;
										}else{
											throw new Exception("Unknown nrrd data type="+nextToken);
										}
									}else{
										throw new Exception("Unknown nrrd data type="+nextToken);
									}
								}else if(headerParam.equals("dimension")){
									dimension = Integer.parseInt(stringTokenizer.nextToken());
									if(dimension < 1){
										throw new Exception("unexpected dimension="+dimension);
									}
								}else if(headerParam.equals("encoding")){
									encoding = NRRDENCODING.valueOf(stringTokenizer.nextToken().toUpperCase());
								}
							}
							BufferedInputStream bis = null;
							if(encoding == NRRDENCODING.GZIP){
								dis.close();
								bis = new BufferedInputStream(new FileInputStream(imageFile));
								boolean bnewLine = false;
								while(true){
									int currentChar = bis.read();
									if(currentChar == '\n'){
										if(bnewLine){
											break;//2 newlines end header
										}
										bnewLine = true;
									}else{
										bnewLine = false;
									}
								}
								GZIPInputStream gzipInputStream = new GZIPInputStream(bis);
								dis = new DataInputStream(gzipInputStream);
							}
							double[] data = new double[xsize*ysize*zsize];
							double minValue = Double.POSITIVE_INFINITY;
							double maxValue = Double.NEGATIVE_INFINITY;
							for (int i = 0; i < data.length; i++) {
								if(i % 262144 == 0){
									if(getClientTaskStatusSupport() != null){
										getClientTaskStatusSupport().setMessage("Reading "+encoding+" "+type+" NRRD data "+(((long)i*(long)100)/(long)data.length)+" % done.");
									}
								}
								if(type == NRRDTYPE.DOUBLE){
									data[i] = dis.readDouble();
								}else if(type == NRRDTYPE.FLOAT){
									data[i] = dis.readFloat();
								}else if(type == NRRDTYPE.UNSIGNEDCHAR){
									data[i] = dis.readUnsignedByte();
								}else{
									throw new Exception("Unexpected data type="+type.toString());
								}

								minValue = Math.min(minValue,data[i]);
								maxValue = Math.max(maxValue,data[i]);
							}
							dis.close();
							if(getClientTaskStatusSupport() != null){
								getClientTaskStatusSupport().setMessage("Scaling "+encoding+" "+type+" NRRD data.");
							}

							short[] dataToSegment = new short[data.length];
							double scaleShort = Math.pow(2, Short.SIZE)-1;
							for (int i = 0; i < data.length; i++) {
								dataToSegment[i]|= (int)((data[i]-minValue)/(maxValue-minValue)*scaleShort);
							}
							fdfos = new FieldDataFileOperationSpec();
							fdfos.origin = new Origin(0, 0, 0);
							fdfos.extent = new Extent((xsize==1?.5:(xsize)*xspace), (ysize==1?.5:(ysize)*yspace), (zsize==1?.5:(zsize)*zspace));
							fdfos.isize = new ISize(xsize, ysize, zsize);
							fdfos.shortSpecData = new short[][][] {{dataToSegment}};
						}finally{
							if(dis != null){try{dis.close();}catch(Exception e){e.printStackTrace();}}
						}
					}else if ((fdfos = createFDOSFromSurfaceFile(imageFile)) != null){//try surface file formats
						//work already done at this point
					}else{
						File[] dirFiles = null;
						ImageSizeInfo origImageSizeInfo = null;
						if(imageFile.isDirectory()){
							dirFiles = imageFile.listFiles(new java.io.FileFilter(){
								public boolean accept(File pathname) {
									return pathname.isFile() && !pathname.isHidden();//exclude windows Thumbs.db
								}});
							if(dirFiles.length == 0){
								throw new Exception("No valid files in selected directory");
							}
							hashTable.put(IMPORT_SOURCE_NAME,"Directory: "+imageFile.getAbsolutePath());
							origImageSizeInfo = ImageDatasetReaderFactory.createImageDatasetReader().getImageSizeInfo(dirFiles[0].getAbsolutePath(),dirFiles.length);
							if(dirFiles.length > 1){
								final String importZ = "Import Z-Sections";
								final String cancelOption = "Cancel";
								String result = DialogUtils.showWarningDialog(guiParent,
										"Import all files in directory '"+imageFile.getAbsolutePath()+"' as Z-Sections",
										new String[] {importZ,cancelOption}, importZ);
								if(result.equals(cancelOption)){
									throw UserCancelException.CANCEL_GENERIC;
								}
							}
							hashTable.put(DIR_FILES, dirFiles);
						}else{
							origImageSizeInfo = ImageDatasetReaderFactory.createImageDatasetReader().getImageSizeInfo(imageFile.getAbsolutePath(),null);
							hashTable.put(IMPORT_SOURCE_NAME,"File: "+imageFile.getAbsolutePath());
						}
						hashTable.put(ORIG_IMAGE_SIZE_INFO, origImageSizeInfo);
						return;
					}
				}else if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FIELDDATA){
					getClientTaskStatusSupport().setMessage("Reading data from VCell server.");
					VCDocument.GeomFromFieldDataCreationInfo docInfo = (VCDocument.GeomFromFieldDataCreationInfo)documentCreationInfo;
					PDEDataContext pdeDataContext =	getMdiManager().getFieldDataWindowManager().getPDEDataContext(docInfo.getExternalDataID(),null);
					ImageSizeInfo newImageSizeInfo = (ImageSizeInfo)hashTable.get(NEW_IMAGE_SIZE_INFO);
					pdeDataContext.setVariableNameAndTime(docInfo.getVarName(), newImageSizeInfo.getTimePoints()[newImageSizeInfo.getSelectedTimeIndex()]);
					double[] data = pdeDataContext.getDataValues();
					hashTable.put(INITIAL_ANNOTATION, hashTable.get(IMPORT_SOURCE_NAME));
					CartesianMesh mesh = (CartesianMesh)hashTable.get(FD_MESH);
					ISize meshISize = (ISize)hashTable.get(FD_MESHISIZE);
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
					ISize isize = getISizeFromUser(guiParent,new ISize(256,256,8),"Enter # of pixels for  x,y,z (e.g. 3D{256,256,8}, 2D{256,256,1}, 1D{256,1,1})");
					fdfos = new FieldDataFileOperationSpec();
					fdfos.origin = new Origin(0, 0, 0);
					fdfos.extent = new Extent(1, 1, 1);
					fdfos.isize = isize;
					hashTable.put(IMPORT_SOURCE_NAME,"Scratch: New Geometry");
//					final int SCRATCH_SIZE_LIMIT = 512*512*20;
//					if(isize.getXYZ() > (SCRATCH_SIZE_LIMIT)){
//						throw new Exception("Total pixels (x*y*z) cannot be >"+SCRATCH_SIZE_LIMIT+".");
//					}
				}else if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FROM_WORKSPACE_ANALYTIC){
					if(hashTable.get(ClientRequestManager.GEOM_FROM_WORKSPACE) != null){
						Geometry workspaceGeom = (Geometry)hashTable.get(ClientRequestManager.GEOM_FROM_WORKSPACE);
						ISize defaultISize = workspaceGeom.getGeometrySpec().getDefaultSampledImageSize();
						ISize isize = getISizeFromUser(guiParent,defaultISize,
							"Warning: converting analytic expression geometry into an image based geometry\nwill remove analytic expressions after new image is created.\n\n"+
							"Enter size (x,y,z) for new geometry image (e.g. "+defaultISize.getX()+","+defaultISize.getY()+","+defaultISize.getZ()+")");
						hashTable.put(IMPORT_SOURCE_NAME,"Workspace from Analytic Geometry");
						VCImage img = workspaceGeom.getGeometrySpec().createSampledImage(isize);
						Enumeration<SubVolume> enumSubvolume = workspaceGeom.getGeometrySpec().getAnalyticOrCSGSubVolumes();
						ArrayList<VCPixelClass> vcPixelClassArrList = new ArrayList<VCPixelClass>();
						while(enumSubvolume.hasMoreElements()){
							SubVolume subVolume = enumSubvolume.nextElement();
							vcPixelClassArrList.add(new VCPixelClass(null, subVolume.getName(), subVolume.getHandle()));
						}
						if(vcPixelClassArrList.size() > img.getPixelClasses().length){
							String result = DialogUtils.showOKCancelWarningDialog(requester.getComponent(), null, "Warning: sampling size is too small to include all subvolumes.");
							if(result == null || !result.equals(SimpleUserMessage.OPTION_OK)){
								throw UserCancelException.CANCEL_GENERIC;
							}
						}
						hashTable.put(VCPIXELCLASSES,vcPixelClassArrList.toArray(new VCPixelClass[0]));
						fdfos = createFDOSFromVCImage(img);
					}else{
						throw new Exception("Expecting image source for GEOM_OPTION_FROM_WORKSPACE_ANALYTIC");
					}
				}else if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FROM_WORKSPACE_IMAGE){
					if(hashTable.get(ClientRequestManager.GEOM_FROM_WORKSPACE) != null){
						Geometry workspaceGeom = (Geometry)hashTable.get(ClientRequestManager.GEOM_FROM_WORKSPACE);
						hashTable.put(IMPORT_SOURCE_NAME,"Workspace Image");
						fdfos = createFDOSFromVCImage(workspaceGeom.getGeometrySpec().getImage());
						if(workspaceGeom.getGeometrySpec().getImage().getDescription() != null){
							hashTable.put(INITIAL_ANNOTATION, workspaceGeom.getGeometrySpec().getImage().getDescription());
						}
						hashTable.put(VCPIXELCLASSES,workspaceGeom.getGeometrySpec().getImage().getPixelClasses());
					}else{
						throw new Exception("Expecting image source for GEOM_OPTION_FROM_WORKSPACE");
					}
				}
				hashTable.put(FDFOS, fdfos);
			} catch (DataFormatException ex) {
				throw new Exception("Cannot read image file.\n"+ex.getMessage());
			}
		}
	};
	AsynchClientTask getFieldDataImageParams = new AsynchClientTask("Getting DB Image parameters...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			VCDocument.GeomFromFieldDataCreationInfo docInfo = (VCDocument.GeomFromFieldDataCreationInfo)documentCreationInfo;
			PDEDataContext pdeDataContext =	getMdiManager().getFieldDataWindowManager().getPDEDataContext(docInfo.getExternalDataID(),null);
			CartesianMesh mesh = pdeDataContext.getCartesianMesh();
			ISize meshISize = new ISize(mesh.getSizeX(),mesh.getSizeY(),mesh.getSizeZ());
			double[] timePoints = pdeDataContext.getTimePoints();
			hashTable.put(FD_MESH, mesh);
			hashTable.put(FD_MESHISIZE, meshISize);
			hashTable.put(FD_TIMEPOINTS, timePoints);
}
	};
	AsynchClientTask queryImageResizeTask = new AsynchClientTask("Query File Image Resize...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			String importSourceName = (String)hashTable.get(IMPORT_SOURCE_NAME);
			if((ImageSizeInfo)hashTable.get(ORIG_IMAGE_SIZE_INFO) != null){//from file
				ImageSizeInfo newImagesiSizeInfo = queryImageResize(requester.getComponent(), (ImageSizeInfo)hashTable.get(ORIG_IMAGE_SIZE_INFO),true);
				hashTable.put(NEW_IMAGE_SIZE_INFO, newImagesiSizeInfo);
			}else if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FIELDDATA){//from fielddata
				VCDocument.GeomFromFieldDataCreationInfo docInfo = (VCDocument.GeomFromFieldDataCreationInfo)documentCreationInfo;
				double[] fieldDataTimes = (double[])hashTable.get(FD_TIMEPOINTS);
				hashTable.remove(FD_TIMEPOINTS);
				ISize fieldDataISize = (ISize)hashTable.get(FD_MESHISIZE);
				ImageSizeInfo origImageSizeInfo = new ImageSizeInfo(importSourceName,fieldDataISize,1,fieldDataTimes,null);
				ImageSizeInfo newImagesiSizeInfo = queryImageResize(requester.getComponent(), origImageSizeInfo,true);
				hashTable.put(NEW_IMAGE_SIZE_INFO, newImagesiSizeInfo);
				hashTable.put(IMPORT_SOURCE_NAME,
						"FieldData: "+docInfo.getExternalDataID().getName()+" varName="+
						docInfo.getVarName()+" timeIndex="+newImagesiSizeInfo.getTimePoints()[newImagesiSizeInfo.getSelectedTimeIndex()]);
			}
		}
	};
	AsynchClientTask importFileImageTask = new AsynchClientTask("Importing Image from File...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FILE && hashTable.get(FDFOS) == null){
				ImageSizeInfo origImageSizeInfo = (ImageSizeInfo)hashTable.get(ORIG_IMAGE_SIZE_INFO);
				ImageSizeInfo newImageSizeInfo = (ImageSizeInfo)hashTable.get(NEW_IMAGE_SIZE_INFO);
				File[] dirFiles = (File[])hashTable.get(DIR_FILES);
				File imageFile = (File)hashTable.get("imageFile");
				FieldDataFileOperationSpec fdfos = null;
				boolean bMergeChannels = origImageSizeInfo.getNumChannels() != newImageSizeInfo.getNumChannels();
				ISize resize = (origImageSizeInfo.getiSize().compareEqual(newImageSizeInfo.getiSize())?null:newImageSizeInfo.getiSize());
				if(dirFiles != null){
					Arrays.sort(dirFiles, new Comparator<File>(){
						public int compare(File o1, File o2) {
							return o1.getName().compareToIgnoreCase(o2.getName());
						}});
					hashTable.put(INITIAL_ANNOTATION, dirFiles[0].getAbsolutePath()+"\n.\n.\n.\n"+dirFiles[dirFiles.length-1].getAbsolutePath());
					short[][] dataToSegment = null;
					ISize isize = null;
					Origin origin = null;
					Extent extent = null;
					int sizeXY = 0;
					ISize firstImageISize = null;
					for (int i = 0; i < dirFiles.length; i++) {
						ImageDataset[] imageDatasets = ImageDatasetReaderFactory.createImageDatasetReader().readImageDatasetChannels(dirFiles[i].getAbsolutePath(), null,bMergeChannels,null,resize);
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
					hashTable.put(INITIAL_ANNOTATION, imageFile.getAbsolutePath());
					Integer userPreferredTimeIndex = null;
					if(origImageSizeInfo.getTimePoints().length > 1){
						userPreferredTimeIndex = newImageSizeInfo.getSelectedTimeIndex();
					}
					getClientTaskStatusSupport().setMessage("Reading file...");
					ImageDataset[] imageDatasets =
						ImageDatasetReaderFactory.createImageDatasetReader().readImageDatasetChannels(imageFile.getAbsolutePath(), null,bMergeChannels,userPreferredTimeIndex,resize);
					fdfos = ClientRequestManager.createFDOSWithChannels(imageDatasets,null);
				}
				hashTable.put(FDFOS, fdfos);
				hashTable.remove(NEW_IMAGE_SIZE_INFO);
				hashTable.remove(ORIG_IMAGE_SIZE_INFO);
				hashTable.remove(DIR_FILES);
			}
		}
	};

	AsynchClientTask resizeImageTask = new AsynchClientTask("Resizing Image...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			ImageSizeInfo newImageSizeInfo = (ImageSizeInfo)hashTable.get(NEW_IMAGE_SIZE_INFO);
			FieldDataFileOperationSpec fdfos = (FieldDataFileOperationSpec)hashTable.get(FDFOS);
			if(newImageSizeInfo != null && fdfos != null && !fdfos.isize.compareEqual(newImageSizeInfo.getiSize())){
				resizeImage((FieldDataFileOperationSpec)hashTable.get(FDFOS), newImageSizeInfo.getiSize(),documentCreationInfo.getOption());
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
			final VCPixelClass[] postProcessPixelClasses = (VCPixelClass[])hashTable.get(VCPIXELCLASSES);
			AsynchClientTask task1 = new AsynchClientTask("edit geometry", AsynchClientTask.TASKTYPE_SWING_BLOCKING, false) {

				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					geomHolder[0] = roiMultiPaintManager.showGUI(
							okButtonText,
							(String)hashTable.get(IMPORT_SOURCE_NAME),
							(Component)hashTable.get(GUI_PARENT),
							(String)hashTable.get(INITIAL_ANNOTATION),
							postProcessPixelClasses,
							getUserPreferences()
						);
				}
			};
			AsynchClientTask task2 = new AsynchClientTask("update geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					//Create default name for image
					String dateTimeString = BeanUtils.generateDateTimeString();
					geomHolder[0].getGeometrySpec().getImage().setName("img_"+dateTimeString);
					geomHolder[0].setName("geom_"+dateTimeString);
					//cause update in this thread so later swing threads won't be delayed
					geomHolder[0].precomputeAll(new GeometryThumbnailImageFactoryAWT());
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
	if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FROM_SCRATCH){
		tasksV.addAll(Arrays.asList(new AsynchClientTask[] {parseImageTask,finishTask}));
	}else if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FROM_WORKSPACE_ANALYTIC){
		tasksV.addAll(Arrays.asList(new AsynchClientTask[] {parseImageTask,finishTask}));
	}else if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FROM_WORKSPACE_IMAGE){
		tasksV.addAll(Arrays.asList(new AsynchClientTask[] {parseImageTask,finishTask}));
	}else if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FILE){
		tasksV.addAll(Arrays.asList(new AsynchClientTask[] {selectImageFileTask,parseImageTask,queryImageResizeTask,importFileImageTask/*resizes*/,finishTask}));
	}else if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FIJI_IMAGEJ || documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_BLENDER){
		tasksV.addAll(Arrays.asList(new AsynchClientTask[] {parseImageTask,queryImageResizeTask,importFileImageTask/*resizes*/,finishTask}));
	}else if(documentCreationInfo.getOption() == VCDocument.GEOM_OPTION_FIELDDATA){
		tasksV.addAll(Arrays.asList(new AsynchClientTask[] {getFieldDataImageParams,queryImageResizeTask,parseImageTask,resizeImageTask,finishTask}));
	}
	return tasksV.toArray(new AsynchClientTask[0]);
}

public static ISize getISizeFromUser(Component guiParent,ISize initISize,String textMessage) throws UserCancelException{
	Integer imageDimension = (initISize==null?null:(initISize.getX()!=1?1:0)+(initISize.getY()!=1?1:0)+(initISize.getZ()!=1?1:0));
	if(imageDimension != null && (imageDimension < 1 || imageDimension > 3)){
		throw new IllegalArgumentException("Dimension must be 1, 2 or 3.");
	}
	try{
		int xsize = (imageDimension==null?-1:initISize.getX());
		int ysize = (imageDimension==null?-1:initISize.getY());
		int zsize = (imageDimension==null?-1:initISize.getZ());
		do{
			String result = (imageDimension==null?"256,256,8":xsize+","+ysize+","+zsize);
			result = DialogUtils.showInputDialog0(guiParent,textMessage, result);
			String tempResult = result;
			try{
				if(result == null || result.length() == 0){
					result = "";
					throw new Exception("No size values entered.");
				}
				xsize = Integer.parseInt(tempResult.substring(0, tempResult.indexOf(",")));
				tempResult = tempResult.substring(tempResult.indexOf(",")+1, tempResult.length());
				ysize = Integer.parseInt(tempResult.substring(0, tempResult.indexOf(",")));
				tempResult = tempResult.substring(tempResult.indexOf(",")+1, tempResult.length());
				zsize = Integer.parseInt(tempResult);
				if(imageDimension != null){
					if(imageDimension == 2 && zsize != 1){
						throw new Exception("Dimension "+imageDimension+" must have z = 1.");
					}else if(imageDimension == 1 && zsize != 1 && ysize != 1){
						throw new Exception("Dimension "+imageDimension+" must have z = 1 and y = 1.");
					}
				}
				ISize isize = new ISize(xsize, ysize, zsize);
				if(isize.getXYZ() <=0){
					throw new Exception("Total pixels ("+xsize+"*"+ysize+"*"+zsize+") cannot be <=0.");
				}
				return isize;
			}catch(Exception e){
				DialogUtils.showErrorDialog(guiParent, "Error entering starting sizes\n"+e.getMessage(), e);
			}
		}while(true);
	}catch(UtilCancelException e2){
		throw UserCancelException.CANCEL_GENERIC;
	}

}

public static ImageSizeInfo queryImageResize(final Component requester,final ImageSizeInfo origImageSizeInfo,boolean bFullMode){
	ImageResizePanel imageResizePanel = new ImageResizePanel();
	imageResizePanel.init(origImageSizeInfo,bFullMode);
	imageResizePanel.setPreferredSize(new Dimension(400, 200));
	while(true){
		int flag = DialogUtils.showComponentOKCancelDialog(requester, imageResizePanel, "Optionally convert imported images.");
		if(flag != JOptionPane.OK_OPTION){
			throw UserCancelException.CANCEL_GENERIC;
		}
		try{
			ImageSizeInfo imagesizeInfo = imageResizePanel.getNewImageSizeInfo();
			return imagesizeInfo;
		}catch(Exception e){
			e.printStackTrace();
			DialogUtils.showErrorDialog(requester, "Error getting x,y,z: "+e.getMessage());
		}
	}
}
private static void resizeImage(FieldDataFileOperationSpec fdfos,ISize newImagesISize,int imageType) throws Exception{
	final int ORIG_XYSIZE = fdfos.isize.getX()*fdfos.isize.getY();
	try {
		int xsize = newImagesISize.getX();
		int ysize = newImagesISize.getY();
		double scaleFactor = (double)newImagesISize.getX()/(double)fdfos.isize.getX();
		if(xsize != fdfos.isize.getX() || ysize != fdfos.isize.getY()){
			int numChannels = fdfos.shortSpecData[0].length;//this normally contains different variables but is used for channels here
			//resize each z section to xsize,ysize
		    AffineTransform scaleAffineTransform = AffineTransform.getScaleInstance(scaleFactor,scaleFactor);
		    AffineTransformOp scaleAffineTransformOp = new AffineTransformOp( scaleAffineTransform,AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			short[][][] resizeData = new short[1][numChannels][fdfos.isize.getZ()*xsize*ysize];
			for (int c = 0; c < numChannels; c++) {
				BufferedImage originalImage = new BufferedImage(fdfos.isize.getX(), fdfos.isize.getY(), BufferedImage.TYPE_USHORT_GRAY);
				BufferedImage scaledImage = new BufferedImage(xsize,ysize, BufferedImage.TYPE_USHORT_GRAY);
				for (int z = 0; z < fdfos.isize.getZ(); z++) {
					short[] originalImageBuffer = ((DataBufferUShort)(originalImage.getRaster().getDataBuffer())).getData();
					System.arraycopy(fdfos.shortSpecData[0][c], z*ORIG_XYSIZE, originalImageBuffer, 0, ORIG_XYSIZE);
					scaleAffineTransformOp.filter( originalImage,scaledImage);
				    short[] scaledImageBuffer = ((DataBufferUShort)(scaledImage.getRaster().getDataBuffer())).getData();
				    System.arraycopy(scaledImageBuffer, 0, resizeData[0][c], z*xsize*ysize, xsize*ysize);
				}
			}
			fdfos.isize = new ISize(xsize, ysize, fdfos.isize.getZ());
			fdfos.shortSpecData = resizeData;
		}
	} catch (Exception e) {
		throw new Exception("Error scaling imported image:\n"+e.getMessage());
	}
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
		case BIOMODEL_DOC: {
			AsynchClientTask task1 = new AsynchClientTask("creating biomodel", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					BioModel bioModel = createDefaultBioModelDocument(null);
					hashTable.put("doc", bioModel);
				}
			};
			taskArray = new AsynchClientTask[] {task1};
			break;
		}
		case MATHMODEL_DOC: {
			if ((createOption == VCDocument.MATH_OPTION_NONSPATIAL) || (createOption == VCDocument.MATH_OPTION_SPATIAL_EXISTS)) {
				AsynchClientTask task2 = new AsynchClientTask("creating mathmodel", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						Geometry geometry = null;
						if (createOption == VCDocument.MATH_OPTION_NONSPATIAL) {
							geometry = new Geometry("Untitled", 0);
						} else {
							geometry = (Geometry)hashTable.get(GEOMETRY_KEY);
						}
						MathModel mathModel = createMathModel("Untitled", geometry);
						mathModel.setName("MathModel" + (getMdiManager().getNumCreatedDocumentWindows() + 1));
						hashTable.put("doc", mathModel);
					}
				};
				if (createOption == VCDocument.MATH_OPTION_SPATIAL_EXISTS){
					AsynchClientTask task1 = createSelectDocTask(requester);
					AsynchClientTask task1b = createSelectLoadGeomTask(requester);
					taskArray = new AsynchClientTask[] {task1, task1b,task2};
				}else{
					taskArray = new AsynchClientTask[] {task2};
				}
				break;
			}else if (createOption == VCDocument.MATH_OPTION_FROMBIOMODELAPP){

				AsynchClientTask task1 = new AsynchClientTask("select biomodel application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						// spatial or non-spatial
						BioModelInfo bioModelInfo = (BioModelInfo)DialogUtils.getDBTreePanelSelection(requester.getComponent(), getMdiManager().getDatabaseWindowManager().getBioModelDbTreePanel(), "Open", "Select BioModel");
						if (bioModelInfo != null) { // may throw UserCancelException
							hashTable.put("bioModelInfo", bioModelInfo);
						}
					}
				};
				AsynchClientTask task2 = new AsynchClientTask("find sim contexts in biomodel application", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
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
							Component component = requester.getComponent();
							// Get the simContext names, so that user can choose which simContext math to import
							String simContextChoice = (String)PopupGenerator.showListDialog(component, simContextNames, "Please select Application");
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
							Objects.requireNonNull(chosenSimContext);

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
		case GEOMETRY_DOC: {
			if (createOption == VCDocument.GEOM_OPTION_1D ||
					createOption == VCDocument.GEOM_OPTION_2D ||
					createOption == VCDocument.GEOM_OPTION_3D) {
				// analytic
				AsynchClientTask task1 = new AsynchClientTask("creating analytic geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						Geometry geometry = new Geometry("Geometry" + (getMdiManager().getNumCreatedDocumentWindows() + 1), documentCreationInfo.getOption());
						geometry.getGeometrySpec().addSubVolume(new AnalyticSubVolume("subdomain0",new Expression(1.0)));
						geometry.precomputeAll(new GeometryThumbnailImageFactoryAWT());
						hashTable.put("doc", geometry);
					}
				};
				taskArray = new AsynchClientTask[] {task1};
				break;
			} if (createOption == VCDocument.GEOM_OPTION_CSGEOMETRY_3D) {
				// constructed solid geometry
				AsynchClientTask task1 = new AsynchClientTask("creating constructed solid geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						Geometry geometry = new Geometry("Geometry" + (getMdiManager().getNumCreatedDocumentWindows() + 1), 3);
						Extent extent = geometry.getExtent();
						if (extent != null) {
							// create a CSGPrimitive of type cube and scale it to the 'extent' components. Use this as the default or background CSGObject (subdomain).
							// This can be considered as the equivalent of subdomain (with expression) 1.0 for analyticSubvolume.
							// basic cube
							CSGPrimitive cube = new CSGPrimitive("cube", CSGPrimitive.PrimitiveType.CUBE);
							// scaled cube
							double x = extent.getX();
							double y = extent.getY();
							double z = extent.getZ();
							CSGScale scaledCube = new CSGScale("scale", new Vect3d(x/2.0, y/2.0, z/2.0));
							scaledCube.setChild(cube);
							// translated scaled cube
							CSGTranslation translatedScaledCube = new CSGTranslation("translation", new Vect3d(x/2, y/2, z/2));
							translatedScaledCube.setChild(scaledCube);
							CSGObject csgObject = new CSGObject(null, "subdomain0", 0);
							csgObject.setRoot(translatedScaledCube);
							geometry.getGeometrySpec().addSubVolume(csgObject, false);
							geometry.precomputeAll(new GeometryThumbnailImageFactoryAWT());
							hashTable.put("doc", geometry);
						}
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
		UserRegistrationManager.registrationOperationGUI(this, currWindowManager,
				getClientServerManager().getClientServerInfo(),
				(bNewUser?LoginManager.USERACTION_REGISTER:LoginManager.USERACTION_EDITINFO),
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
		UserRegistrationManager.registrationOperationGUI(this, currWindowManager,
				VCellClient.createClientServerInfo(
					getClientServerManager().getClientServerInfo(), userid, null),
					LoginManager.USERACTION_LOSTPASSWORD,
				null);
	} catch (UserCancelException e) {
		return;
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
 * hashtable key for data bytes of file
 */
private final static String BYTES_KEY = "bytes";
/**
 * Comment
 */
public static void downloadExportedData(final Component requester, final UserPreferences userPrefs, final ExportEvent evt) {
	AsynchClientTask task1 = new AsynchClientTask("Retrieving data from '"+evt.getLocation()+"'", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			final Exception[] excArr = new Exception[] {null};
			final boolean[] bFlagArr = new boolean[] {false};
			final ByteArrayOutputStream[] baosArr = new ByteArrayOutputStream[1];
			final HttpGet[] httpGetArr = new HttpGet[1];
			final ImageJConnection[] imagejConnetArr = new ImageJConnection[1];
			//Start download of exported file in separate thread that is interruptible (apache HTTPClient)
			Thread interruptible = new Thread(new Runnable() {
				@Override
				public void run() {
			    	if(getClientTaskStatusSupport() != null){
			    		getClientTaskStatusSupport().setMessage("downloading data...");
			    	}
					CloseableHttpClient httpclient = HttpClients.createDefault();
					httpGetArr[0] = new HttpGet(evt.getLocation());
					CloseableHttpResponse response = null;
					try {
						response = httpclient.execute(httpGetArr[0]);
					    HttpEntity entity = response.getEntity();
					    if (entity != null) {
					    	long size = entity.getContentLength();
					        InputStream instream = entity.getContent();
					        try {
//								URLConnection connection = new URL(evt.getLocation()).openConnection();
//							    Thread.sleep(60000);
							    if (size > 0) {
							    	baosArr[0] = new ByteArrayOutputStream((int)size);
							    }
							    else {
							    	baosArr[0] = new ByteArrayOutputStream( );
							    }
							    IOUtils.copy(instream, baosArr[0]);
							}finally {
					            instream.close();
					        }
					    }

					}catch(Exception e){
						excArr[0] = e;
					}finally {
					    if(imagejConnetArr[0] != null){imagejConnetArr[0].closeConnection();}
					    if(response != null){try{response.close();}catch(Exception e){}}
					    if(httpclient != null){try{httpclient.close();}catch(Exception e){}}
					    bFlagArr[0] = true;
					}
				}
			});
			interruptible.start();
			//Wait for download to 1-finish, 2-fail or 3-be cancelled by user
			while(!bFlagArr[0]){
				if(getClientTaskStatusSupport() != null && getClientTaskStatusSupport().isInterrupted()){//user cancelled
					if(httpGetArr[0] != null){httpGetArr[0].abort();}
					if(imagejConnetArr[0] != null){imagejConnetArr[0].closeConnection();}
					throw UserCancelException.CANCEL_GENERIC;
				}
				try{
					Thread.sleep(500);
				}catch(InterruptedException e){// caused by pressing 'cancel' button on progresspopup
					if(httpGetArr[0] != null){httpGetArr[0].abort();}
					if(imagejConnetArr[0] != null){imagejConnetArr[0].closeConnection();}
					if(getClientTaskStatusSupport() != null && getClientTaskStatusSupport().isInterrupted()){
						throw UserCancelException.CANCEL_GENERIC;
					}
				}
			}
		    if(excArr[0] != null){//download failed
		    	throw excArr[0];
		    }
//			//-------------------------
//			String filePart = new URL(evt.getLocation()).getFile();
//			File f = new File("c:/temp/exportdir",filePart);
//			FileInputStream fis = new FileInputStream(f);
//			baosArr[0] = new ByteArrayOutputStream();
//			IOUtils.copy(fis, baosArr[0]);
//			fis.close();
//			//------------------------------
		    //
		    //finished downloading, either save to file or send to ImageJ directly
		    //
		    if(evt.getFormat() == null || !evt.getFormat().equals("IMAGEJ")){
		    	//save for file save operations
		    	hashTable.put(BYTES_KEY, baosArr[0].toByteArray());
		    }else{
		    	//Send to ImageJ directly
//		    	int response = DialogUtils.showComponentOKCancelDialog(requester, new JLabel("Open ImageJ->File->Export->VCellUtil... to begin data transefer"), "Sending data to ImageJ...");
//		    	if(response != JOptionPane.OK_OPTION){
//		    		throw UserCancelException.CANCEL_GENERIC;
//		    	}
		    	//NRRD format send to ImageJ
		    	if(getClientTaskStatusSupport() != null){
		    		getClientTaskStatusSupport().setMessage("unpacking data...");
		    	}
		    	ByteArrayInputStream bais = new ByteArrayInputStream( baosArr[0].toByteArray());
		    	ZipInputStream zis = null;
		    	BufferedInputStream bis = null;
		    	try{
			    	zis = new ZipInputStream(bais);
			    	ZipEntry entry = zis.getNextEntry();
//			    	System.out.println("zipfile entry name="+entry.getName()+"zipfile entry size="+entry.getSize());
//					    	File tempf = new File("C:\\temp\\tempf.nrrd");
//					    	FileOutputStream fos = new FileOutputStream(tempf);
//					    	byte[] mybuf = new byte[1000];
//					    	int numread = 0;
//					    	while((numread = zis.read(mybuf)) != -1){
//					    		fos.write(mybuf, 0, numread);
//					    	}
//					    	fos.close();
			    	AnnotatedExportEvent annotatedExportEvent = (AnnotatedExportEvent)evt;
			    	TimeSpecs timeSpecs = annotatedExportEvent.getExportSpecs().getTimeSpecs();
			    	double[] timePoints = new double[timeSpecs.getEndTimeIndex()-timeSpecs.getBeginTimeIndex()+1];
			    	for (int tp = timeSpecs.getBeginTimeIndex(); tp <= timeSpecs.getEndTimeIndex(); tp++){
			    		timePoints[tp-timeSpecs.getBeginTimeIndex()] = timeSpecs.getAllTimes()[tp];
			    	}
			    	imagejConnetArr[0] = new ImageJConnection(ImageJHelper.ExternalCommunicator.IMAGEJ);//doesn't open connection until later
			    	bis = new BufferedInputStream(zis);
			    	ImageJHelper.vcellSendNRRD(requester,bis,getClientTaskStatusSupport(),imagejConnetArr[0],"VCell exported data '"+entry.getName()+"'",timePoints,annotatedExportEvent.getExportSpecs().getVariableSpecs().getVariableNames());
		    	}finally{
		    		if(zis != null){try{zis.closeEntry();zis.close();}catch(Exception e){e.printStackTrace();}}
		    		if(bis != null){try{bis.close();}catch(Exception e){e.printStackTrace();}}
		    	}
		    	throw UserCancelException.CANCEL_GENERIC;//finished, exit all further tasks
		    }
		}
	};
	AsynchClientTask task2 = new AsynchClientTask("selecting file to save", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			// user pref could be null if trying local export
			String defaultPath = null;
			if (userPrefs == null) {
				defaultPath = ResourceUtil.getLastUserLocalDir();
				if (defaultPath == null || defaultPath.length() == 0) {
					defaultPath = ResourceUtil.getUserHomeDir().getAbsolutePath();
				}
			} else {
				defaultPath = userPrefs.getCurrentDialogPath().getAbsolutePath();
			}
			if (defaultPath==null || ResourceUtil.getVCellInstall().getAbsolutePath().contains(defaultPath)) {
				defaultPath = ResourceUtil.getUserHomeDir().getAbsolutePath();
			}
			final VCFileChooser fileChooser = new VCFileChooser(defaultPath);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setMultiSelectionEnabled(false);
		    String name = evt.getVCDataIdentifier().getID();
		    String suffix = null;
		    if(evt.getLocation().toLowerCase().endsWith(".mov")){
				fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_MOV);
				fileChooser.setFileFilter(FileFilters.FILE_FILTER_MOV);

		    	suffix = "_exported.mov";
		    }else if(evt.getLocation().toLowerCase().endsWith(".gif")){
				fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_GIF);
				fileChooser.setFileFilter(FileFilters.FILE_FILTER_GIF);

		    	suffix = "_exported.gif";
		    }else if(evt.getLocation().toLowerCase().endsWith(".jpeg")){
				fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_JPEG);
				fileChooser.setFileFilter(FileFilters.FILE_FILTER_JPEG);

		    	suffix = "_exported.jpeg";
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
			int approve = fileChooser.showSaveDialog(requester);
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
			File defaultPath = null;
			File newPath = null;

			if (userPrefs != null) {
				defaultPath = userPrefs.getCurrentDialogPath();
				newPath = selectedFile.getParentFile();
		        if (!newPath.equals(defaultPath)) {
		        	userPrefs.setCurrentDialogPath(newPath);
		        }
			} else {
				// if export is local export, userPrefs = null
				defaultPath = new File(ResourceUtil.getLastUserLocalDir());
				newPath = selectedFile.getParentFile();
	        if (!newPath.equals(defaultPath)) {
		        	ResourceUtil.setLastUserLocalDir(newPath.getAbsolutePath());
		        }
	        }
//	        System.out.println("New preferred file path: " + newPath + ", Old preferred file path: " + defaultPath);
			if (selectedFile.exists()) {
				String question = null;
				if (userPrefs != null) {
					question = PopupGenerator.showWarningDialog(requester, userPrefs, UserMessage.warn_OverwriteFile,selectedFile.getAbsolutePath());
				} else {
					question = DialogUtils.showWarningDialog(requester, "Overwrite File?", "Overwrite file '" + selectedFile.getAbsolutePath() + "'?", new String[] {UserMessage.OPTION_OVERWRITE_FILE, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OVERWRITE_FILE);
				}
				if (question != null && question.equals(UserMessage.OPTION_CANCEL)){
					return;
				}
			}
			byte[] bytes = (byte[])hashTable.get(BYTES_KEY);
            FileOutputStream fo = new FileOutputStream(selectedFile);
            fo.write(bytes);
            fo.close();
		}
	};
	ClientTaskDispatcher.dispatch(requester, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2, task3}, false,true,null);
}

public void exitApplication() {
	try (VCellThreadChecker.SuppressIntensive si = new VCellThreadChecker.SuppressIntensive()) {
	if (!bExiting) {
		// close all windows - this will run checks
		boolean closedAllWindows = closeAllWindows(true);
		if (! closedAllWindows) {
			// user bailed out at some point, we're not gonna exit
			return;
		}
	}
	// ready to exit
	if (!ClientTaskDispatcher.hasOutstandingTasks()) {
		// simply exit in this case
		System.exit(0);
	}
	new Timer(1000, evt -> checkTasksDone() ).start();
	}
}

/**
 * see if there are outstanding tasks ... if not, exit the application
 */
private void checkTasksDone( ) {
	Collection<String> ot = ClientTaskDispatcher.outstandingTasks();
	if (ot.isEmpty()) {
		//ready to exit now
		System.exit(0);
	}
	System.out.println("waiting for " + ot.toString());
}



/**
 * Comment
 */
public void exportDocument(TopLevelWindowManager manager,FileFilter forceFilefilter) {
	/* block window */
	JFrame currentWindow = getMdiManager().blockWindow(manager.getManagerID());
	/* prepare hashtable for tasks */
	Hashtable<String,Object> hash = new Hashtable<String,Object>();
	hash.put("mdiManager", getMdiManager());
	hash.put(DocumentManager.IDENT, getDocumentManager());
	hash.put("topLevelWindowManager", manager);
	hash.put("currentWindow", currentWindow);
	hash.put("userPreferences", getUserPreferences());
	if(forceFilefilter != null){
		hash.put(ChooseFile.FORCE_FILE_FILTER, forceFilefilter);
	}
	/* create tasks */
	// get document to be exported
	AsynchClientTask documentToExport = new DocumentToExport();
	// get file
	AsynchClientTask chooseFile = new ChooseFile();
	// export it
	AsynchClientTask exportDocument = new ExportDocument();
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
		// try to download the thing
		downloadExportedData(getMdiManager().getFocusedWindowManager().getComponent(), getUserPreferences(), event);
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

@Override
public VtkManager getVtkManager(OutputContext outputContext, VCDataIdentifier vcDataID) throws DataAccessException {
	VCDataManager vcDataManager = getClientServerManager().getVCDataManager();
	return new VtkManager(outputContext, vcDataManager, vcDataID);
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
			case BIOMODEL_DOC: {
				BioModel bm = ((BioModel)vcDoc);
				vcDocInfo = getDocumentManager().getBioModelInfo(bm.getVersion().getVersionKey());
				break;
			}
			case MATHMODEL_DOC: {
				MathModel mm = ((MathModel)vcDoc);
				vcDocInfo = getDocumentManager().getMathModelInfo(mm.getVersion().getVersionKey());
				break;
			}
			case GEOMETRY_DOC: {
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
	if (simInfo==null){
		// unsaved simulation ... won't have simulation status
		return null;
	}
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
 *
 * @return false
 */
@Deprecated
public boolean isApplet() {
	return false;
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
	//gcwtodo

	AsynchClientTask createNewDocumentTask =
		new AsynchClientTask("Creating New Document", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			VCDocument doc = (VCDocument)hashTable.get("doc");
			DocumentWindowManager windowManager = createDocumentWindowManager(doc);
			DocumentWindow dw = getMdiManager().createNewDocumentWindow(windowManager);
			if (windowManager != null) {
				hashTable.put("windowManager", windowManager);
			}
			setFinalWindow(hashTable, dw);
		}
	};

	if(documentCreationInfo.getDocumentType() == VCDocumentType.MATHMODEL_DOC &&
			documentCreationInfo.getOption() == VCDocument.MATH_OPTION_SPATIAL_NEW){
		final AsynchClientTask createSpatialMathModelTask = new AsynchClientTask("creating mathmodel", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				Geometry geometry = null;
				geometry = (Geometry)hashTable.get("doc");
				MathModel mathModel = createMathModel("Untitled", geometry);
				mathModel.setName("MathModel" + (getMdiManager().getNumCreatedDocumentWindows() + 1));
				hashTable.put("doc", mathModel);
			}
		};

		requester.createGeometry(
				null, new AsynchClientTask[] {createSpatialMathModelTask,createNewDocumentTask},
				"Choose geometry type to start MathModel creation","Create MathModel",null);
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
	newName = "image_"+BeanUtils.generateDateTimeString();
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
	    PopupGenerator.showInfoDialog(getMdiManager().getFocusedWindowManager(), event.getMessageData().getData().toString());
	}
}

//private void openBnglDebugger() {
//try {
//	javax.swing.JFrame frame = new javax.swing.JFrame();
//	BNGOutputPanel aBNGOutputPanel;
//	aBNGOutputPanel = new BNGOutputPanel();
//	frame.setContentPane(aBNGOutputPanel);
//	frame.setSize(aBNGOutputPanel.getSize());
//	frame.addWindowListener(new java.awt.event.WindowAdapter() {
//		public void windowClosing(java.awt.event.WindowEvent e) {
//			// System.exit(0);
//		};
//	});
//	java.awt.Insets insets = frame.getInsets();
//	frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
//	frame.setVisible(true);
//} catch (Throwable exception) {
//	System.err.println("Exception occurred in main() of javax.swing.JPanel");
//	exception.printStackTrace(System.out);
//}
//}

/**
 * DocumentWindowManager hash table key
 */
private final static String WIN_MGR_KEY = "WIN_MGR_KY";
private void openAfterChecking(VCDocumentInfo documentInfo, final TopLevelWindowManager requester, final boolean inNewWindow) {

	final String DOCUMENT_INFO = "documentInfo";
	final String SEDML_TASK = "SedMLTask";
	final String SEDML_MODEL = "SedMLModel";
	final String BNG_UNIT_SYSTEM = "bngUnitSystem";
	/* asynchronous and not blocking any window */
	bOpening = true;
	Hashtable<String,Object> hashTable = new Hashtable<String, Object>();

	// may want to insert corrected VCDocumentInfo later if our import debugger corrects it (BNGL Debugger).
	hashTable.put(DOCUMENT_INFO, documentInfo);


	// start a thread that gets it and updates the GUI by creating a new document desktop
	String taskName = null;
	if (documentInfo instanceof ExternalDocInfo) {
		taskName = "Importing document";
		ExternalDocInfo externalDocInfo = (ExternalDocInfo)documentInfo;

		File file = externalDocInfo.getFile();
		if(file != null && !file.getName().isEmpty() && file.getName().endsWith("bngl")) {

			BngUnitSystem bngUnitSystem = new BngUnitSystem(BngUnitOrigin.DEFAULT);
			String fileText;
			String originalFileText;
			try {
				fileText = BeanUtils.readBytesFromFile(file, null);
				originalFileText = new String(fileText);
			} catch (IOException e1) {
				e1.printStackTrace();
				DialogUtils.showErrorDialog(requester.getComponent(),
						"<html>Error reading file "+file.getPath()+"</html>");
				return;
			}
			Reader reader = externalDocInfo.getReader();
			boolean bException = true;
			while (bException){
				try {
					BioModel bioModel = createDefaultBioModelDocument(bngUnitSystem);
					boolean bStochastic = true;
					boolean bRuleBased = true;
					SimulationContext ruleBasedSimContext = bioModel.addNewSimulationContext("temp NFSim app", SimulationContext.Application.RULE_BASED_STOCHASTIC);
					List<SimulationContext> appList = new ArrayList<SimulationContext>();
					appList.add(ruleBasedSimContext);

					RbmModelContainer rbmModelContainer = bioModel.getModel().getRbmModelContainer();
					RbmUtils.reactionRuleLabelIndex = 0;
					RbmUtils.reactionRuleNames.clear();
					ASTModel astModel = RbmUtils.importBnglFile(reader);
					// TODO: if we imported a unit system from the bngl file, update bngUnitSystem at this point
					// for now, hasUnitSystem() always returns false
					if(astModel.hasUnitSystem()) {
						bngUnitSystem = astModel.getUnitSystem();
					}
					if(astModel.hasCompartments()) {
						Structure struct = bioModel.getModel().getStructure(0);
						if(struct != null) {
							bioModel.getModel().removeStructure(struct);
						}
					}
					BnglObjectConstructionVisitor constructionVisitor = null;
					if(!astModel.hasMolecularDefinitions()) {
						System.out.println("Molecular Definition Block missing.");
						constructionVisitor = new BnglObjectConstructionVisitor(bioModel.getModel(), appList, bngUnitSystem, false);
					} else {
						constructionVisitor = new BnglObjectConstructionVisitor(bioModel.getModel(), appList, bngUnitSystem, true);
					}
					astModel.jjtAccept(constructionVisitor, rbmModelContainer);
					bException = false;
				} catch (final Exception e) {
					e.printStackTrace(System.out);
					BNGLDebuggerPanel panel = new BNGLDebuggerPanel(fileText, e);
					int oKCancel = DialogUtils.showComponentOKCancelDialog(requester.getComponent(), panel, "Bngl Debugger: " + file.getName());
					if (oKCancel == JOptionPane.CANCEL_OPTION || oKCancel == JOptionPane.DEFAULT_OPTION) {
						throw new UserCancelException("Canceling Import");
					}

					// inserting <potentially> corrected DocumentInfo
					fileText = panel.getText();
					externalDocInfo = new ExternalDocInfo(panel.getText());
					reader = externalDocInfo.getReader();
					hashTable.put(DOCUMENT_INFO, externalDocInfo);
				}
			}

			if(!originalFileText.equals(fileText)) {		// file has been modified
		        String message = "Importing <b>" + file.getName() + "</b> into vCell. <br>Overwrite the file on the disk?<br>";
		        message = "<html>" + message + "</html>";
				Object[] options = {"Overwrite and Import", "Import Only", "Cancel"};
				int returnCode = JOptionPane.showOptionDialog(requester.getComponent(), message, "Bngl Debugger",
				    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				    null, options, options[2]);
				if (returnCode == JOptionPane.YES_OPTION) {
					try {
			            FileWriter fw = new FileWriter(file);
			            fw.write(fileText);
			            fw.close();
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
				} else if(returnCode == JOptionPane.CANCEL_OPTION || returnCode == JOptionPane.CLOSED_OPTION) {
					return;
				}
			}

			if(!(bngUnitSystem.getOrigin() == BngUnitOrigin.PARSER)) {
				BNGLUnitsPanel panel = new BNGLUnitsPanel(bngUnitSystem);
				int oKCancel = DialogUtils.showComponentOKCancelDialog(requester.getComponent(), panel, " Bngl Units Selector", null, false);
				if (oKCancel == JOptionPane.CANCEL_OPTION || oKCancel == JOptionPane.DEFAULT_OPTION) {
					return;		// TODO: or do nothing and continue with default values?
				} else {
					bngUnitSystem = panel.getUnits();
				}
			}
			hashTable.put(BNG_UNIT_SYSTEM, bngUnitSystem);
		} else if(file != null && !file.getName().isEmpty() && file.getName().toLowerCase().endsWith(".sedml")) {
			try {
				XMLSource xmlSource = externalDocInfo.createXMLSource();
				File sedmlFile = xmlSource.getXmlFile();
				
				SedML sedml = Libsedml.readDocument(sedmlFile).getSedMLModel();
				if(sedml == null || sedml.getModels().isEmpty()) {
					return;
				}
				AbstractTask chosenTask = SEDMLChooserPanel.chooseTask(sedml, requester.getComponent(), file.getName());
				
				hashTable.put(SEDML_MODEL, sedml);
				hashTable.put(SEDML_TASK, chosenTask);
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("failed to read document: "+e.getMessage(),e);
			}
		} else if(file != null && !file.getName().isEmpty() && (file.getName().toLowerCase().endsWith(".sedx") || file.getName().toLowerCase().endsWith(".omex"))) {
			try {
				ArchiveComponents ac = null;
				ac = Libsedml.readSEDMLArchive(new FileInputStream(file));
				SEDMLDocument doc = ac.getSedmlDocument();
			
				SedML sedml = doc.getSedMLModel();
				if(sedml == null) {
					throw new RuntimeException("Failed importing " + file.getName());
				}
				if(sedml.getModels().isEmpty()) {
					throw new RuntimeException("Unable to find any model in " + file.getName());
				}
		        AbstractTask chosenTask = SEDMLChooserPanel.chooseTask(sedml, requester.getComponent(), file.getName());
		        
				hashTable.put(SEDML_MODEL, sedml);
				hashTable.put(SEDML_TASK, chosenTask);
			
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("failed to read archive: "+e.getMessage(),e);
			}
		}
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
			VCDocumentInfo documentInfo = (VCDocumentInfo)hashTable.get(DOCUMENT_INFO);
			if (documentInfo instanceof BioModelInfo) {
				BioModelInfo bmi = (BioModelInfo)documentInfo;
				doc = getDocumentManager().getBioModel(bmi);
			} else if (documentInfo instanceof MathModelInfo) {
				MathModelInfo mmi = (MathModelInfo)documentInfo;
				doc = getDocumentManager().getMathModel(mmi);
			} else if (documentInfo instanceof GeometryInfo) {
				GeometryInfo gmi = (GeometryInfo)documentInfo;
				doc = getDocumentManager().getGeometry(gmi);
			} else if (documentInfo instanceof ExternalDocInfo){
				ExternalDocInfo externalDocInfo = (ExternalDocInfo)documentInfo;
				File file = externalDocInfo.getFile();
				if(file != null && !file.getName().isEmpty() && (file.getName().toLowerCase().endsWith(".sedx") || file.getName().toLowerCase().endsWith(".omex"))) {
					TranslationLogger transLogger = new TranslationLogger(requester);
					doc = XmlHelper.sedmlToBioModel(transLogger, externalDocInfo, (SedML)hashTable.get(SEDML_MODEL), (AbstractTask)hashTable.get(SEDML_TASK));
				} else if (!externalDocInfo.isXML()) {
					if (hashTable.containsKey(BNG_UNIT_SYSTEM)) { 			// not XML, look for BNGL etc.
					// we use the BngUnitSystem already created during the 1st pass
					BngUnitSystem bngUnitSystem = (BngUnitSystem)hashTable.get(BNG_UNIT_SYSTEM);
					BioModel bioModel = createDefaultBioModelDocument(bngUnitSystem);

					SimulationContext ruleBasedSimContext = bioModel.addNewSimulationContext("NFSim app", SimulationContext.Application.RULE_BASED_STOCHASTIC);
					SimulationContext odeSimContext = bioModel.addNewSimulationContext("BioNetGen app", SimulationContext.Application.NETWORK_DETERMINISTIC);
					List<SimulationContext> appList = new ArrayList<SimulationContext>();
					appList.add(ruleBasedSimContext);
					appList.add(odeSimContext);
					// set convention for initial conditions in generated application for seed species (concentration or count)
					ruleBasedSimContext.setUsingConcentration(bngUnitSystem.isConcentration());
					odeSimContext.setUsingConcentration(bngUnitSystem.isConcentration());

					RbmModelContainer rbmModelContainer = bioModel.getModel().getRbmModelContainer();
					RbmUtils.reactionRuleLabelIndex = 0;
					RbmUtils.reactionRuleNames.clear();
					Reader reader = externalDocInfo.getReader();
					ASTModel astModel = RbmUtils.importBnglFile(reader);
					if(bioModel.getModel() != null && bioModel.getModel().getVcMetaData() != null){
						VCMetaData vcMetaData = bioModel.getModel().getVcMetaData();
						vcMetaData.setFreeTextAnnotation(bioModel, astModel.getProlog());
					}
					if(astModel.hasCompartments()) {
						Structure struct = bioModel.getModel().getStructure(0);
						if(struct != null) {
							bioModel.getModel().removeStructure(struct);
						}
					}

					BnglObjectConstructionVisitor constructionVisitor = null;
					if(!astModel.hasMolecularDefinitions()) {
						System.out.println("Molecular Definition Block missing. Extracting it from Species, Reactions, Obserbables.");
						constructionVisitor = new BnglObjectConstructionVisitor(bioModel.getModel(), appList, bngUnitSystem, false);
					} else {
						constructionVisitor = new BnglObjectConstructionVisitor(bioModel.getModel(), appList, bngUnitSystem, true);
					}
					// we'll convert the kinetic parameters to BngUnitSystem inside the visit(ASTKineticsParameter...)
					astModel.jjtAccept(constructionVisitor, rbmModelContainer);

					// set the volume in the newly created application to BngUnitSystem.bnglModelVolume
					// TODO: set the right values if we import compartments from the bngl file!
//					if(!bngUnitSystem.isConcentration()) {
						Expression sizeExpression = new Expression(bngUnitSystem.getVolume());
						ruleBasedSimContext.getGeometryContext().getStructureMapping(0).getSizeParameter().setExpression(sizeExpression);
						odeSimContext.getGeometryContext().getStructureMapping(0).getSizeParameter().setExpression(sizeExpression);
//					}

					// we remove the NFSim application if any seed species is clamped because NFSim doesn't know what to do with it
					boolean bClamped = false;
					for(SpeciesContextSpec scs : ruleBasedSimContext.getReactionContext().getSpeciesContextSpecs()) {
						if(scs.isConstant()) {
							bClamped = true;
							break;
						}
					}
					if(bClamped) {
						bioModel.removeSimulationContext(ruleBasedSimContext);
					}

//					// TODO: DON'T delete this code
//					// the code below is needed if we also want to create simulations, example for 1 rule based simulation
//					// it is rule-based so it wont have to flatten, should be fast.
//					MathMappingCallback callback = new MathMappingCallbackTaskAdapter(getClientTaskStatusSupport());
//					NetworkGenerationRequirements networkGenerationRequirements = null; // network generation should not be executed.
//					ruleBasedSimContext.refreshMathDescription(callback,networkGenerationRequirements);
//					Simulation sim = ruleBasedSimContext.addNewSimulation(SimulationOwner.DEFAULT_SIM_NAME_PREFIX,callback,networkGenerationRequirements);

					doc = bioModel;
					}
				}else{ // is XML
					try (TranslationLogger transLogger = new TranslationLogger(requester)) {
						XMLSource xmlSource = externalDocInfo.createXMLSource();
						org.jdom.Element rootElement = xmlSource.getXmlDoc().getRootElement();
						String xmlType = rootElement.getName();
						String modelXmlType = null;
						if (xmlType.equals(XMLTags.VcmlRootNodeTag)) {
							// For now, assuming that <vcml> element has only one child (biomodel, mathmodel or geometry).
							// Will deal with multiple children of <vcml> Element when we get to model composition.
							@SuppressWarnings("unchecked")
							List<Element> childElementList = rootElement.getChildren();
							Element modelElement = childElementList.get(0);	// assuming first child is the biomodel, mathmodel or geometry.
							modelXmlType = modelElement.getName();
						}
						if (xmlType.equals(XMLTags.BioModelTag) || (xmlType.equals(XMLTags.VcmlRootNodeTag) && modelXmlType.equals(XMLTags.BioModelTag))) {
							doc = XmlHelper.XMLToBioModel(xmlSource);
						} else if (xmlType.equals(XMLTags.MathModelTag) || (xmlType.equals(XMLTags.VcmlRootNodeTag) && modelXmlType.equals(XMLTags.MathModelTag))) {
							doc = XmlHelper.XMLToMathModel(xmlSource);
						} else if (xmlType.equals(XMLTags.GeometryTag) || (xmlType.equals(XMLTags.VcmlRootNodeTag) && modelXmlType.equals(XMLTags.GeometryTag))) {
							doc = XmlHelper.XMLToGeometry(xmlSource);
						} else if (xmlType.equals(XMLTags.SbmlRootNodeTag)) {
							Namespace namespace = rootElement.getNamespace(XMLTags.SBML_SPATIAL_NS_PREFIX);
							boolean bIsSpatial = (namespace==null) ? false : true;
							doc = XmlHelper.importSBML(transLogger, xmlSource, bIsSpatial);
						} else if (xmlType.equals(XMLTags.CellmlRootNodeTag)) {
							if (requester instanceof BioModelWindowManager){
								doc = XmlHelper.importBioCellML(transLogger, xmlSource);
							} else {
								doc = XmlHelper.importMathCellML(transLogger, xmlSource);
							}
						} else if (xmlType.equals(MicroscopyXMLTags.FRAPStudyTag)) {
							doc = VFrapXmlHelper.VFRAPToBioModel(hashTable, xmlSource, getDocumentManager(), requester);
						} else if (xmlType.equals(XMLTags.SedMLTypeTag)) {
							doc = XmlHelper.sedmlToBioModel(transLogger, externalDocInfo, (SedML)hashTable.get(SEDML_MODEL), (AbstractTask)hashTable.get(SEDML_TASK));
						} else { // unknown XML format
							throw new RuntimeException("unsupported XML format, first element tag is <"+rootElement.getName()+">");
						}
						if(externalDocInfo.getDefaultName() != null){
							doc.setName(externalDocInfo.getDefaultName());
						}
					}
				}
				if (doc == null) {
					File f = externalDocInfo.getFile();
					if (f != null) {
						throw new RuntimeException("Unable to determine type of file " + f.getCanonicalPath());
					}
					throw new ProgrammingException();
				}
			}
			// create biopax objects using annotation
			if (doc instanceof BioModel) {
				BioModel bioModel = (BioModel) doc;
				try{
					bioModel.getVCMetaData().createBioPaxObjects(bioModel);
				}catch(Exception e){
					e.printStackTrace();
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
					hashTable.put(WIN_MGR_KEY, windowManager);
				}
			} finally {
				if (!inNewWindow) {
					getMdiManager().unBlockWindow(requester.getManagerID());
				}
				bOpening = false;
			}
		}
	};
	AsynchClientTask task3 = new AsynchClientTask("Special Layout", AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, false) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			// TODO Auto-generated method stub
			if (documentInfo instanceof ExternalDocInfo) {
				ExternalDocInfo externalDocInfo = (ExternalDocInfo)documentInfo;
				if(externalDocInfo.isBioModelsNet()){
					DocumentWindowManager windowManager = (DocumentWindowManager)hashTable.get(WIN_MGR_KEY);
					if(windowManager instanceof BioModelWindowManager){
						((BioModelWindowManager)windowManager).specialLayout();
					}
				}
			}
		}
	};
	AsynchClientTask task4 = new AsynchClientTaskFunction(ClientRequestManager::setWindowFocus, "Set window focus", AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, false);
	ClientTaskDispatcher.dispatch(requester.getComponent(), hashTable, new AsynchClientTask[]{task0, task1, task2,task3, task4}, false);
}

/**
 * raise issue tab if error is present
 * @param hashTable
 * @throws Exception
 */
private static void setWindowFocus(Hashtable<String, Object> hashTable) throws Exception {
	DocumentWindowManager windowManager = (DocumentWindowManager)hashTable.get(WIN_MGR_KEY);
	if (windowManager != null) {
		DocumentEditor de = windowManager.getDocumentEditor();
		if (de != null) {
			de.setWindowFocus();
		}
	}
}

private DocumentWindowManager createDocumentWindowManager(final VCDocument doc){
	JPanel newJPanel = new JPanel();
	if(doc instanceof BioModel){
		return new BioModelWindowManager(newJPanel, ClientRequestManager.this, (BioModel)doc);
	}else if(doc instanceof MathModel){
		return new MathModelWindowManager(newJPanel, ClientRequestManager.this, (MathModel)doc);
	}else if(doc instanceof Geometry){
		return new GeometryWindowManager(newJPanel, ClientRequestManager.this, (Geometry)doc);
	}
	throw new RuntimeException("Unknown VCDocument type "+doc);
}

public void openDocument(VCDocumentType documentType, DocumentWindowManager requester) {
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

public void openPathway(DocumentWindowManager windowManager, PathwayImportOption pathwayImportOption) {
	if(windowManager instanceof BioModelWindowManager) {
		BioModelWindowManager bioModelWindowManager = (BioModelWindowManager) windowManager;
		bioModelWindowManager.importPathway(pathwayImportOption);
	} else {
		DialogUtils.showErrorDialog(windowManager.getComponent(),
				"<html>Pathways can only be imported into a BioModel. " +
				"To import a pathway, switch to a BioModel window or open a new" +
				"one by opening a biomodel. This option should not even be available otherwise.</html>");
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
public void reconnect(final TopLevelWindowManager requester) {
	// asynch & nothing to do on Swing queue (updates handled by events)
	AsynchClientTask task1 = new AsynchClientTask("reconnect", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				getClientServerManager().reconnect(requester);

			}
	};
	ClientTaskDispatcher.dispatch(requester.getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] { task1 });
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
			case BIOMODEL_DOC: {
				info = getDocumentManager().getBioModelInfo(versionKey);
				break;
			}
			case MATHMODEL_DOC: {
				info = getDocumentManager().getMathModelInfo(versionKey);
				break;
			}
			case GEOMETRY_DOC: {
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


public SimulationStatus runSimulation(final SimulationInfo simInfo, int numSimulationScanJobs) throws DataAccessException{

	SimulationStatus simStatus = getClientServerManager().getJobManager().startSimulation(simInfo.getAuthoritativeVCSimulationIdentifier(), numSimulationScanJobs);
	return simStatus;
}


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
			throw new UserCancelException("user canceled");
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
				if(simulations[i].getMathDescription().isNonSpatialStoch() || simulations[i].getMathDescription().isSpatialStoch() || simulations[i].getMathDescription().isSpatialHybrid())
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
	hash.put(DocumentManager.IDENT, getDocumentManager());
	hash.put(CommonTask.DOCUMENT_WINDOW_MANAGER.name, documentWindowManager);
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
	hash.put(DocumentManager.IDENT, getDocumentManager());
	hash.put(CommonTask.DOCUMENT_WINDOW_MANAGER.name, documentWindowManager);
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
	hash.put(DocumentManager.IDENT, getDocumentManager());
	hash.put(CommonTask.DOCUMENT_WINDOW_MANAGER.name, documentWindowManager);
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
		documentInfo = getMdiManager().getDatabaseWindowManager().selectDocument(VCDocumentType.BIOMODEL_DOC, requester);
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
		documentInfo = getMdiManager().getDatabaseWindowManager().selectDocument(VCDocumentType.MATHMODEL_DOC, requester);
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


///**
// * Insert the method's description here.
// * Creation date: (5/21/2004 4:20:48 AM)
// */
//public void showBNGWindow() {
//	getMdiManager().showWindow(ClientMDIManager.BIONETGEN_WINDOW_ID);
//}

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
public void showTestingFrameworkWindow() {
	getMdiManager().showWindow(ClientMDIManager.TESTING_FRAMEWORK_WINDOW_ID);
}


/**
 * Insert the method's description here.
 * Creation date: (6/15/2004 2:37:01 AM)
 */
public void startExport(final OutputContext outputContext,Component requester, final ExportSpecs exportSpecs) {
	// start a thread to get it; not blocking any window/frame
	AsynchClientTask task1 = new AsynchClientTask("starting exporting", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			getClientServerManager().getJobManager().startExport(outputContext,exportSpecs);
		}
	};
	ClientTaskDispatcher.dispatch(requester, new Hashtable<String, Object>(), new AsynchClientTask[] { task1 });
}



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
								SimulationStatus simStatus = getClientServerManager().getJobManager().stopSimulation(simInfo.getAuthoritativeVCSimulationIdentifier());
								// updateStatus
								clientSimManager.updateStatusFromStopRequest(simulations[i],simStatus);
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
			@SuppressWarnings("unchecked")
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

public OpenModelInfoHolder[] getOpenDesktopDocumentInfos(boolean bIncludeSimulations) throws DataAccessException{
	Vector<OpenModelInfoHolder> simInfoHolderV = new Vector<OpenModelInfoHolder>();
	for (TopLevelWindowManager tlwm : getMdiManager().getWindowManagers() ) {
		if(tlwm instanceof DocumentWindowManager){
			DocumentWindowManager dwm = (DocumentWindowManager)tlwm;
			VCDocument vcDoc = dwm.getVCDocument();
			//if(vcDoc.getVersion() != null){
				if(vcDoc.getDocumentType() == VCDocumentType.BIOMODEL_DOC){
					BioModel bioModel = (BioModel)vcDoc;
						//getDocumentManager().getBioModel(vcDoc.getVersion().getVersionKey());
					SimulationContext[] simContexts = bioModel.getSimulationContexts();
					for(int i=0;i<simContexts.length;i+= 1){
						if(bIncludeSimulations){
							if(simContexts[i].getGeometry() == null){
								throw new DataAccessException("Error gathering document info (isCompartmental check failed):\nOpen BioModel document "+bioModel.getName()+" has no Geometry");
							}
							Simulation[] sims = simContexts[i].getSimulations();
							for(int j=0;j<sims.length;j+= 1){
								for(int k=0;k<sims[j].getScanCount();k+= 1){
									FieldDataWindowManager.OpenModelInfoHolder simInfoHolder =
										new FieldDataWindowManager.FDSimBioModelInfo(
												sims[j].getName(),
												bioModel.getVersion(),
												simContexts[i],sims[j].getSimulationInfo(),
												k,
												//!sims[j].getSolverTaskDescription().getSolverDescription().hasVariableTimestep(),
												simContexts[i].getGeometry().getDimension() == 0
										);
									simInfoHolderV.add(simInfoHolder);
								}
							}
						}else{
							FieldDataWindowManager.OpenModelInfoHolder simInfoHolder =
								new FieldDataWindowManager.FDSimBioModelInfo(
										null,
										bioModel.getVersion(),
										simContexts[i],
										null,-1,simContexts[i].getGeometry().getDimension() == 0
							);
							simInfoHolderV.add(simInfoHolder);
						}
					}
				}else if(vcDoc.getDocumentType() == VCDocumentType.MATHMODEL_DOC) {
					MathModel mathModel = (MathModel) vcDoc;
						//getDocumentManager().getMathModel(vcDoc.getVersion().getVersionKey());
					if(bIncludeSimulations){
						if(mathModel.getMathDescription() == null || mathModel.getMathDescription().getGeometry() == null){
							throw new DataAccessException("Error gathering document info (isCompartmental check failed):\nOpen MathModel document "+mathModel.getName()+" has either no MathDescription or no Geometry");
						}
						Simulation[] sims = mathModel.getSimulations();
						for(int i=0;i<sims.length;i+= 1){
							for(int k=0;k<sims[i].getScanCount();k+= 1){
								FieldDataWindowManager.OpenModelInfoHolder simInfoHolder =
									new FieldDataWindowManager.FDSimMathModelInfo(
											sims[i].getName(),
											mathModel.getVersion(),
											mathModel.getMathDescription(),
											sims[i].getSimulationInfo(),
											k,
											//!sims[i].getSolverTaskDescription().getSolverDescription().hasVariableTimestep(),
											mathModel.getMathDescription().getGeometry().getDimension() == 0
									);
								simInfoHolderV.add(simInfoHolder);
							}
						}
					}else{
						FieldDataWindowManager.OpenModelInfoHolder simInfoHolder =
							new FieldDataWindowManager.FDSimMathModelInfo(
									null,
									mathModel.getVersion(),
									mathModel.getMathDescription(),
									null,-1,mathModel.getMathDescription().getGeometry().getDimension() == 0
						);
						simInfoHolderV.add(simInfoHolder);
					}
				}
			//}
		}

	}
	OpenModelInfoHolder[] simInfoHolderArr = new OpenModelInfoHolder[simInfoHolderV.size()];
	simInfoHolderV.copyInto(simInfoHolderArr);
	return simInfoHolderArr;
}

public void showComparisonResults(TopLevelWindowManager requester, XmlTreeDiff diffTree, String baselineDesc, String modifiedDesc) {
	TMLPanel comparePanel = new TMLPanel();
	comparePanel.setXmlTreeDiff(diffTree);
	comparePanel.setBaselineVersionDescription(baselineDesc);
	comparePanel.setModifiedVersionDescription(modifiedDesc);

	JOptionPane comparePane = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {/*"Apply Changes", */"Close"});
	comparePane.setMessage(comparePanel);
	JDialog compareDialog = comparePane.createDialog(requester.getComponent(), "Compare Models");
	compareDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	compareDialog.setResizable(true);
	compareDialog.pack();
	compareDialog.setVisible(true);
	//ZEnforcer.showModalDialogOnTop(compareDialog,JOptionPane.getFrameForComponent(requester.getComponent()));
	if ("Apply Changes".equals(comparePane.getValue())) {
		if (!comparePanel.tagsResolved()) {
			DialogUtils.showErrorDialog(comparePanel, "Please resolve all tagged elements/attributes before proceeding.");
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
		ImageDatasetReader imageDatasetReader = ImageDatasetReaderFactory.createImageDatasetReader();
		ImageDataset[] imagedataSets = imageDatasetReader.readImageDatasetChannels(imageFile.getAbsolutePath(),null,false,saveOnlyThisTimePointIndex,null);
		if (imagedataSets!=null && bCropOutBlack){
			for (int i = 0; i < imagedataSets.length; i++) {
				Rectangle nonZeroRect = imagedataSets[i].getNonzeroBoundingRectangle();
				if(nonZeroRect != null){
					imagedataSets[i] = imagedataSets[i].crop(nonZeroRect);
				}
			}
		}
		return createFDOSWithChannels(imagedataSets,null);
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
		case BIOMODEL_DOC:
			BioModelInfo[] bioModelInfos = getDocumentManager().getBioModelInfos();
			for (BioModelInfo bioModelInfo : bioModelInfos) {
				if (bioModelInfo.getVersion().getVersionKey().equals(vcDoc.getVersion().getVersionKey())) {
					selectedVersionInfo = bioModelInfo;
					break;
				}
			}
			break;
		case MATHMODEL_DOC:
			MathModelInfo[] mathModelInfos = getDocumentManager().getMathModelInfos();
			for (MathModelInfo mathModelInfo : mathModelInfos) {
				if (mathModelInfo.getVersion().getVersionKey().equals(vcDoc.getVersion().getVersionKey())) {
					selectedVersionInfo = mathModelInfo;
					break;
				}
			}
			break;
		case GEOMETRY_DOC:
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

public static Collection<AsynchClientTask> updateMath(final Component requester, final SimulationContext simulationContext, final boolean bShowWarning, final NetworkGenerationRequirements networkGenerationRequirements) {
	ArrayList<AsynchClientTask> rval = new ArrayList<>();
	AsynchClientTask task1 = new AsynchClientTask("generating math", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			Geometry geometry = simulationContext.getGeometry();
			if (geometry.getDimension()>0 && geometry.getGeometrySurfaceDescription().getGeometricRegions()==null){
				geometry.getGeometrySurfaceDescription().updateAll();
			}
			// Use differnt mathmapping for different applications (stoch or non-stoch)
			simulationContext.checkValidity();
			MathMappingCallback callback = new MathMappingCallbackTaskAdapter(getClientTaskStatusSupport());

			MathMapping mathMapping = simulationContext.createNewMathMapping(callback, networkGenerationRequirements);
			MathDescription mathDesc = mathMapping.getMathDescription(callback);
			callback.setProgressFraction(1.0f/3.0f*2.0f);
			hashTable.put("mathMapping", mathMapping);
			hashTable.put("mathDesc", mathDesc);
		}
	};
	rval.add(task1);

	AsynchClientTask task2 = new AsynchClientTask("formating math", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			MathDescription mathDesc = (MathDescription)hashTable.get("mathDesc");
			if (mathDesc != null) {
				simulationContext.setMathDescription(mathDesc);
			}
		}
	};
	rval.add(task2);

	if (bShowWarning) {
		AsynchClientTask task3 = new AsynchClientTask("showing issues", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				MathMapping mathMapping = (MathMapping)hashTable.get("mathMapping");
				MathDescription mathDesc = (MathDescription)hashTable.get("mathDesc");
				if (mathDesc != null) {
					//
					// inform user if any issues
					//
					Issue issues[] = mathMapping.getIssues();
					if (issues!=null && issues.length>0){
						StringBuffer messageBuffer = new StringBuffer("Issues encountered during Math Generation:\n");
						int issueCount=0;
						for (int i = 0; i < issues.length; i++){
							if (issues[i].getSeverity()==Issue.SEVERITY_ERROR || issues[i].getSeverity()==Issue.SEVERITY_WARNING){
								messageBuffer.append(issues[i].getCategory()+" "+issues[i].getSeverityName()+" : "+issues[i].getMessage()+"\n");
								issueCount++;
							}
						}
						if (issueCount>0){
							PopupGenerator.showWarningDialog(requester,messageBuffer.toString(),new String[] { "OK" }, "OK");
						}
					}
				}
			}
		};
		rval.add(task3);
	}
	return rval;
}

}

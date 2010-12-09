package cbit.vcell.client;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import org.vcell.util.BeanUtils;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.VersionableType;
import org.vcell.util.gui.JDesktopPaneEnhanced;
import org.vcell.util.gui.JInternalFrameEnhanced;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.PerformanceMonitorEvent;
import cbit.rmi.event.PerformanceMonitorListener;
import cbit.vcell.client.data.OutputContext;
import cbit.vcell.client.desktop.geometry.GeometrySummaryViewer;
import cbit.vcell.client.desktop.geometry.SurfaceViewerPanel;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.xml.merge.XmlTreeDiff;
/**
 * Insert the type's description here.
 * Creation date: (5/5/2004 1:01:37 PM)
 * @author: Ion Moraru
 */
public abstract class DocumentWindowManager extends TopLevelWindowManager implements PerformanceMonitorListener, java.awt.event.ActionListener, DataViewerManager {
	private JPanel jPanel = null;
	private String documentID = null;
	
	public static class GeometrySelectionInfo{
		private VCDocumentInfo vcDocumentInfo;
		private VCDocument.DocumentCreationInfo selectedGeometryDocument;
		private boolean bFromCurrentGeom = false;
		
		public GeometrySelectionInfo(VCDocumentInfo vcDocumentInfo){
			if(!vcDocumentInfo.getVersionType().equals(VersionableType.BioModelMetaData) && 
				!vcDocumentInfo.getVersionType().equals(VersionableType.MathModelMetaData) &&
				!vcDocumentInfo.getVersionType().equals(VersionableType.Geometry)){
				throw new IllegalArgumentException("GeometrySelectionInfo(VCDocumentInfo vcDocumentInfo) must be of VersionableType BioModelMetaData,MathModelMetaData or Geometry");				
			}
			this.vcDocumentInfo = vcDocumentInfo;
		}
		public GeometrySelectionInfo(VCDocument.DocumentCreationInfo selectedGeometryDocument){
			if(selectedGeometryDocument.getDocumentType() != VCDocument.GEOMETRY_DOC){
				throw new IllegalArgumentException("GeometrySelectionInfo(VCDocument.DocumentCreationInfo selectedGeometryDocument) must be of type VCDocument.GEOMETRY_DOC");
			}
			this.selectedGeometryDocument = selectedGeometryDocument;
		}
		public GeometrySelectionInfo(){
			this.bFromCurrentGeom = true;
		}
		public VCDocumentInfo getVCDocumentInfo(){
			return vcDocumentInfo;
		}
		public VCDocument.DocumentCreationInfo getDocumentCreationInfo(){
			return selectedGeometryDocument;
		}
		public boolean bFromCurrentGeom(){
			return bFromCurrentGeom;
		}
	}
/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 5:14:36 PM)
 * @param documentWindow cbit.vcell.client.desktop.DocumentWindow
 */
public DocumentWindowManager(JPanel panel, RequestManager requestManager, VCDocument vcDocument, int newlyCreatedDesktops) {
	super(requestManager);
	setJPanel(panel);
	// figure out unique documentID
	setDocumentID(vcDocument);
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:32:43 AM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public abstract void addResultsFrame(SimulationWindow simWindow);


/**
 * Comment
 */
public void cascadeWindows() {
	JInternalFrame[] iframes = getOpenWindows();
	int dx = getJDesktopPane().getWidth() / iframes.length;
	int dy = getJDesktopPane().getHeight() / iframes.length;
	dx = (dx > 0) ? dx : 1; dx = (dx > 28) ? 28 : dx;
	dy = (dy > 0) ? dy : 1; dy = (dy > 28) ? 28 : dy;
	for (int i=0;i<iframes.length;i++) {
		iframes[i].setLocation(i * dx, i * dy);
		iframes[i].show();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 2:50:14 AM)
 */
public static void close(JInternalFrame[] editorFrames, JDesktopPane pane) {
	if (editorFrames != null && editorFrames.length > 0) {
		for (int i = 0; i < editorFrames.length; i++){
			close(editorFrames[i], pane);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 2:50:14 AM)
 */
public static void close(JInternalFrame editorFrame, JDesktopPane pane) {
	pane.getDesktopManager().closeFrame(editorFrame);
	editorFrame.dispose();
}


/**
Processes the comparison (XML based) of the loaded model with its saved edition.
 * Creation date: (6/1/2004 9:58:46 PM)
 */
public void compareWithSaved() {

	if (getVCDocument().getVersion() == null) { 
		// shouldn't happen (menu should not be available), but check anyway...
		PopupGenerator.showErrorDialog(this, "There is no saved version of this document");
		return;
	}
	final MDIManager mdiManager = new ClientMDIManager(getRequestManager());        
	mdiManager.blockWindow(getManagerID()); 
	
	String taskName = "Comparing with saved"; 
	AsynchClientTask task1 = new AsynchClientTask(taskName, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			XmlTreeDiff xmlTreeDiff = getRequestManager().compareWithSaved(getVCDocument());
			hashTable.put("xmlTreeDiff", xmlTreeDiff);
		}			
	};
	AsynchClientTask task2 = new AsynchClientTask(taskName, AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, false) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			try {
				if (hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR) == null) {
					XmlTreeDiff xmlTreeDiff = (XmlTreeDiff)hashTable.get("xmlTreeDiff");
					String baselineDesc = getVCDocument()+ ", " + (getVCDocument().getVersion() == null ? "not saved" : getVCDocument().getVersion().getDate());
					String modifiedDesc = "Opened document instance";
					getRequestManager().showComparisonResults(DocumentWindowManager.this, xmlTreeDiff, baselineDesc, modifiedDesc);
				}
			} finally {
				mdiManager.unBlockWindow(getManagerID());
			}
		}
	};
	ClientTaskDispatcher.dispatch(getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2}, false);
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 2:48:52 AM)
 * @return java.lang.String
 */
public void connectAs(String user, String password) {
	getRequestManager().connectAs(user, password, this);
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2005 6:30:13 AM)
 * @return cbit.gui.JInternalFrameEnhanced
 * @param frameContent javax.swing.JPanel
 */
public static JInternalFrameEnhanced createDefaultFrame(JPanel frameContent) {

	JInternalFrameEnhanced jif = null;
	if(frameContent instanceof SurfaceViewerPanel){
		jif = new JInternalFrameEnhanced("Surface Viewer", true, true, true, true);
		jif.setContentPane(frameContent);
		jif.setSize(500,500);
		jif.setMinimumSize(new Dimension(400,400));
		jif.setLocation(550, 350);
	}else if(frameContent instanceof GeometrySummaryViewer){
		jif = new JInternalFrameEnhanced("Geometry Summary", true, true, true, true);
		jif.setContentPane(frameContent);
		jif.setSize(700,600);
		jif.setMinimumSize(new Dimension(600,400));
		jif.setLocation(200, 300);
	}

	return jif;
	
}


/**
 * Insert the method's description here.
 * Creation date: (5/6/2004 2:31:32 PM)
 */
private String createTempID() {
	return "TempID" + System.currentTimeMillis();
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 6:27:03 PM)
 * @param event cbit.rmi.event.ExportEvent
 */
public void dataJobMessage(DataJobEvent event){

	// just pass them along...
	fireDataJobMessage(event);
}


/**
 * Insert the method's description here.
 * Creation date: (6/15/2004 2:55:34 AM)
 * @param exportEvent cbit.rmi.event.ExportEvent
 */
public void exportMessage(ExportEvent exportEvent) {
	if(exportEvent.getVCDataIdentifier() instanceof VCSimulationDataIdentifier){
		VCSimulationDataIdentifier vcSimulationDataIdentifier = (VCSimulationDataIdentifier)(exportEvent.getVCDataIdentifier());
		if (haveSimulationWindow(vcSimulationDataIdentifier.getVcSimID()) == null) {// && exportEvent.getEventTypeID() != ExportEvent.EXPORT_COMPLETE) {
			return;
		}		
	}
	// just pass them along...
	fireExportMessage(exportEvent);
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 1:46:17 PM)
 * @return javax.swing.JPanel
 */
public Component getComponent() {
	return jPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (6/21/2005 1:05:03 PM)
 * @return javax.swing.JDesktopPane
 */
protected abstract JDesktopPaneEnhanced getJDesktopPane();


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 2:48:52 AM)
 * @return java.lang.String
 */
public String getManagerID() {
	return documentID;
}


/**
 * Comment
 */
private JInternalFrame[] getOpenWindows() {
	JInternalFrame[] allFrames = getJDesktopPane().getAllFrames();
	Vector<JInternalFrame> openWindows = new Vector<JInternalFrame>();
	for (int i=0;i<allFrames.length;i++) {
		if ((! allFrames[i].isClosed()) && (allFrames[i].isVisible())) {
			openWindows.add(allFrames[i]);
		}
	}
	return (JInternalFrame[])openWindows.toArray(new JInternalFrame[openWindows.size()]);
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 1:46:17 PM)
 * @return javax.swing.JPanel
 */
public User getUser() {
	return getRequestManager().getDocumentManager().getUser();
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 3:40:15 PM)
 * @return cbit.vcell.document.VCDocument
 */
public abstract VCDocument getVCDocument();


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 3:40:15 PM)
 * @return cbit.vcell.document.VCDocument
 */
abstract SimulationWindow haveSimulationWindow(VCSimulationIdentifier vcSimulationIdentifier);


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 9:58:46 PM)
 */
public void openDocument(int documentType) {
	getRequestManager().openDocument(documentType, this);
}


/**
 * Insert the method's description here.
 * Creation date: (1/21/2006 10:45:13 AM)
 */
public void openGeometryDocumentWindow(final Geometry geom) {
	
	AsynchClientTask task1 = new AsynchClientTask("opening geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			try{
				GeometryInfo geomInfo = getRequestManager().getDocumentManager().getGeometryInfo(geom.getVersion().getVersionKey());
				hashTable.put("geomInfo", geomInfo);
			}catch(ObjectNotFoundException e){
				if(getVCDocument().getVersion() != null && !getVCDocument().getVersion().getOwner().equals(getRequestManager().getDocumentManager().getUser())){
					throw new RuntimeException (
						"Opening a geometry document window for '"+geom.getName()+"' from\n"+
						"Model '"+getVCDocument().getName()+"' owned by user ("+getVCDocument().getVersion().getOwner().getName()+")\n"+
						"FAILED because user ("+getRequestManager().getDocumentManager().getUser().getName()+") does not have permission.\n"+
						"Save Model '"+getVCDocument().getName()+"' to your account to have full access to the geometry."
					);
				} else{
					throw e;
				}
			}
		}
	};
	AsynchClientTask task2 = new AsynchClientTask("opening geometry", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			GeometryInfo geomInfo = (GeometryInfo)hashTable.get("geomInfo");
			getRequestManager().openDocument(geomInfo,DocumentWindowManager.this,true);
		}
	};
	ClientTaskDispatcher.dispatch(this.getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2 });
}


/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 3:13:55 PM)
 * @param pme cbit.rmi.event.PerformanceMonitorEvent
 */
public void performanceMonitorEvent(PerformanceMonitorEvent pme) {
	// just pass it to the the message manager
	getRequestManager().getAsynchMessageManager().performanceMonitorEvent(pme);
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 2:48:52 AM)
 * @return java.lang.String
 */
public void reconnect() {
	getRequestManager().reconnect(this);
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:32:43 AM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public abstract void resetDocument(VCDocument newDocument);


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 9:58:46 PM)
 */
public void revertToSaved() {
	if (getVCDocument().getVersion() == null) {
		// shouldn't happen (menu should not be available), but check anyway...
		PopupGenerator.showErrorDialog(this, "There is no saved version of this document");
		return;
	}
	String confirm = PopupGenerator.showWarningDialog(this, getRequestManager().getUserPreferences(), UserMessage.warn_RevertToSaved,null);
	if (confirm.equals(UserMessage.OPTION_CANCEL)){
		//user canceled
		return;
	}
	getRequestManager().revertToSaved(this);
}


/**
 * Comment
 */
public void saveDocument(boolean replace) {
	getRequestManager().saveDocument(this, replace);
}


/**
 * Comment
 */
public void saveDocumentAsNew() {
	getRequestManager().saveDocumentAsNew(this);
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2005 6:58:03 AM)
 * @param frame java.awt.Frame
 */
public static void setDefaultTitle(JInternalFrame jif) {
	

	Container contentPane = jif.getContentPane();
	if(contentPane instanceof GeometrySummaryViewer){
		Geometry geom = ((GeometrySummaryViewer)contentPane).getGeometryOwner().getGeometry();
		jif.setTitle("Viewer/Editor for Geometry "+
				(geom != null?"'"+geom.getName()+"'"+(geom.getVersion() != null?" "+geom.getVersion().getDate():""):""));
	}else if(contentPane instanceof SurfaceViewerPanel){
		Geometry geom = ((SurfaceViewerPanel)contentPane).getGeometry();
		jif.setTitle("Surface for Geometry "+
				(geom != null?"'"+geom.getName()+"'"+(geom.getVersion() != null?" "+geom.getVersion().getDate():""):""));
	}
	
	
	
}


/**
 * Insert the method's description here.
 * Creation date: (6/30/2004 12:10:47 PM)
 */
protected void setDocumentID(VCDocument vcDocument) {
	String oldID = getManagerID();
	if (vcDocument.getVersion() != null) {
		setDocumentID(vcDocument.getVersion().getVersionKey().toString());
	} else {
		// if vcDocument has no Version it was never saved, it was created in this session
		// we generate a temporary ID (until first save occurs, if ever)
		setDocumentID(createTempID());
	}
	getRequestManager().managerIDchanged(oldID, getManagerID());
}


/**
 * Insert the method's description here.
 * Creation date: (5/6/2004 2:24:10 PM)
 * @param newDocumentID java.lang.String
 */
private void setDocumentID(java.lang.String newDocumentID) {
	documentID = newDocumentID;
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 1:46:17 PM)
 * @param newJPanel javax.swing.JPanel
 */
private void setJPanel(javax.swing.JPanel newJPanel) {
	jPanel = newJPanel;
}


/**
 * Comment
 */
public void showBNGWindow() {
	getRequestManager().showBNGWindow();
}


/**
 * Comment
 */
public void showDatabaseWindow() {
	getRequestManager().showDatabaseWindow();
}


/**
 * Insert the method's description here.
 * Creation date: (6/14/2004 10:55:40 PM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public abstract void showDataViewerPlotsFrames(javax.swing.JInternalFrame[] plotFrames);
	
/**
 * Insert the method's description here.
 * Creation date: (1/22/2007 7:52:25 AM)
 */
public void showFieldDataWindow() {

	getRequestManager().showFieldDataWindow(null);
	
//	try{
////		if(pdeDataViewerJIF == null){
////			pdeDataViewer = new PDEDataViewer();
////			pdeDataViewerJIF = createDefaultFrame(pdeDataViewer);
////			pdeDataViewerJIF.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
////				public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
////					close(pdeDataViewerJIF, getJDesktopPane());
////				};
////			});
////		}
//		if(fieldDataGUIPanelJIF == null){
//			fieldDataGUIPanel = new FieldDataGUIPanel();
//			fieldDataGUIPanelJIF = createDefaultFrame(fieldDataGUIPanel);
//			fieldDataGUIPanelJIF.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
//				public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
//					close(fieldDataGUIPanelJIF, getJDesktopPane());
//				};
//			});
//			fieldDataGUIPanel.setFieldDataViewManager(
//				new FieldDataGUIPanel.FieldDataViewManager(){
//					public void show(ExternalDataIdentifier eDI){
//						if( pdeDataViewer == null ||
//							!pdeDataViewer.getPdeDataContext().getVCDataIdentifier().equals(eDI)){
//							if(pdeDataViewerJIF != null && pdeDataViewerJIF.isVisible()){
//								PopupGenerator.showInfoDialog("Only 1 Field data can be viewed at a time.");
//								return;
//							}
//							if(pdeDataViewerJIF != null){
//								getJDesktopPane().remove(pdeDataViewerJIF);
//							}
//							//Create new Viewer
//							pdeDataViewer = new PDEDataViewer();
//							try{
//								pdeDataViewer.setDataViewerManager(DocumentWindowManager.this);
//							}catch(PropertyVetoException e){
//								e.printStackTrace();
//								//ignore
//							}
//							pdeDataViewerJIF = createDefaultFrame(pdeDataViewer);
//							pdeDataViewerJIF.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
//								public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
//									close(pdeDataViewerJIF, getJDesktopPane());
//								};
//							});
//							//
//							VCDataManager vcdm = getRequestManager().getVCDataManager();
//							PDEDataManager pdeDatamanager = new PDEDataManager(vcdm, eDI);
//							pdeDataViewer.setPdeDataContext(pdeDatamanager.getPDEDataContext());
//						}
//						showFrame(pdeDataViewerJIF, getJDesktopPane());
//					}
//				}
//			);
//		}
//		if(!fieldDataGUIPanel.isInitialized()){
//			fieldDataGUIPanel.setClientRequestManager((ClientRequestManager)getRequestManager());
//		}
//		showFrame(fieldDataGUIPanelJIF, getJDesktopPane());
//	}catch(Exception e){
//		e.printStackTrace();
//		PopupGenerator.showErrorDialog("Error showing FieldDataWindow\n"+e.getMessage());
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	try{
//		final JList fdList = new JList();
//
//		final java.util.Vector<ExternalDataIdentifier> fdiArrV = new java.util.Vector<ExternalDataIdentifier>(java.util.Arrays.asList(getRequestManager().getDocumentManager().getExternalDataIdentifiers()));
//		//final cbit.vcell.simdata.FieldDataIdentifier[] fdiArr =
//			//getRequestManager().getDocumentManager().getFieldDataIdentifiers(null);
//
//		JPanel fdPanel = new JPanel(new java.awt.GridBagLayout());
//		
////		JButton importBioformatsJButton = new JButton("Import bioformats...");
////		importBioformatsJButton.addActionListener(
////			new java.awt.event.ActionListener(){
////				public void actionPerformed(java.awt.event.ActionEvent actionEvent){
////					try{
////						cbit.vcell.simdata.ExternalDataIdentifier[] fdiArr = new cbit.vcell.simdata.ExternalDataIdentifier[fdiArrV.size()];
////						fdiArrV.copyInto(fdiArr);
////						
////						File importFile = DatabaseWindowManager.showFileChooserDialog(false,getUserPreferences());						
////						String imageID = importFile.getPath();
////						ImageDataset imageDataset = VirtualFrapTest.readImageDataset(imageID);
////						int numX = imageDataset.getImages()[0].getNumX();
////						int numY = imageDataset.getImages()[0].getNumY();
////						int numZ = imageDataset.getSizeZ();
////						// view image
//////						ImageViewer imageViewer = new ImageViewer();
//////						imageViewer.setImages(imageDataset);
//////						imageViewer.setVisible(true);
////						OverlayEditorPanelJAI overlayEditorPanel = new OverlayEditorPanelJAI();
////						Extent extent = imageDataset.getImages()[0].getExtent();
////						UShortImage roiImage = new UShortImage(new short[numX*numY*numZ],extent,numX,numY,numZ);
////						overlayEditorPanel.setROI(roiImage);
////						overlayEditorPanel.setImages(imageDataset);
////						int retcode = DialogUtils.showComponentOKCancelDialog((Component)null, overlayEditorPanel, "continue to save?");
////						if (retcode != JOptionPane.OK_OPTION){
////							return;
////						}
////									
////						
////					    //Save DB
////						cbit.vcell.simdata.ExternalDataIdentifier fdi = null;
////						String fieldDataInfo = "FieldDataName,VarName,0,0,0,"+extent.getX()+","+extent.getY()+","+extent.getZ()+","+numX+","+numY+","+numZ;
////						String fieldDataName = null;
////						Origin origin = null;
////						ISize isize = null;
////						cbit.vcell.simdata.ExternalDataIdentifier fdiDB = null;
////						String varName = null;
////						while(fdi == null){
////							fieldDataInfo = PopupGenerator.showInputDialog((Component)null,"Enter Field Data (name,var,O,E,S)",fieldDataInfo);
////							if(fieldDataInfo == null || fieldDataInfo.length() == 0){
////								PopupGenerator.showErrorDialog("Field Data info cannot be empty");
////								continue;
////							}
////							java.util.StringTokenizer st = new java.util.StringTokenizer(fieldDataInfo, ",");
////							fieldDataName = null;
////							try{
////								fieldDataName = st.nextToken();
////							}catch(Exception e){
////								PopupGenerator.showErrorDialog("Error parsing FieldDataName "+e.getMessage());
////								continue;
////							}
////							for(int i=0;i<fdiArr.length;i+= 1){
////								if(fdiArr[i].getName().equals(fieldDataName)){
////									fieldDataName = null;
////									PopupGenerator.showErrorDialog("Field Dataname "+fdiArr[i].getName()+" already exists,");
////									break;
////								}
////							}
////							if(fieldDataName != null){
////								try{
////									varName = st.nextToken();
////									origin = new Origin(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
////									extent = new Extent(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
////									isize = new ISize(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
////									break;
////								}catch(Exception e){
////									PopupGenerator.showErrorDialog("Error parsing FieldData Info "+e.getMessage());
////								}
////							}
////						}
////						fdiDB = getRequestManager().getDocumentManager().saveExternalDataIdentifier(fieldDataName);
////					    //Save Disk
////					    try{
////					    	short[][][] pixData = new short[imageDataset.getImages().length][1][];
////					    	for (int imageNum = 0; imageNum < imageDataset.getImages().length; imageNum++) {
////					    		short[] imageData = imageDataset.getImages()[imageNum].getPixels();
////					    		pixData[imageNum][0] = imageData;
////					    	}
////					    	cbit.vcell.client.server.FieldDataFileOperationSpec fdos = new cbit.vcell.client.server.FieldDataFileOperationSpec();
////					    	fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
////					    	fdos.shortSpecData =  pixData;
////					    	fdos.specFDI = fdiDB;
////					    	fdos.varNames = new String[] {varName};
////					    	fdos.owner = getUser();
////					    	fdos.times = imageDataset.getImageTimeStamps();
////					    	fdos.variableTypes = new VariableType[] {VariableType.VOLUME};
////					    	fdos.origin = origin;
////					    	fdos.extent = extent;
////					    	fdos.isize = isize;
////					    	cbit.vcell.client.server.FieldDataFileOperationResults fdor =  getRequestManager().getVCDataManager().fieldDataFileOperation(fdos);
////					    }catch(Exception e){
////						    getRequestManager().getDocumentManager().deleteExternalDataIdentifiers(new KeyValue[] {fdiDB.getKey()});
////						    throw e;
////					    }
////
////					    fdiArrV.clear();
////					    cbit.vcell.simdata.ExternalDataIdentifier[] fdiArrTemp = getRequestManager().getDocumentManager().getExternalDataIdentifiers();
////					    fdiArrV.addAll(java.util.Arrays.asList(fdiArrTemp));
////					    fdList.removeAll();
////					    fdList.setListData(fdiArrV);
////					}catch(cbit.vcell.client.task.UserCancelException e){
////					}catch(Exception e){
////						PopupGenerator.showErrorDialog("Error importing Field Data "+e.getMessage());
////					}
////				}
////			}
////		);
//
//		JButton importJButton = new JButton("Import...");
//		importJButton.addActionListener(
//			new java.awt.event.ActionListener(){
//				public void actionPerformed(java.awt.event.ActionEvent actionEvent){
//					try{
//						cbit.vcell.simdata.ExternalDataIdentifier[] fdiArr = new cbit.vcell.simdata.ExternalDataIdentifier[fdiArrV.size()];
//						fdiArrV.copyInto(fdiArr);
//						File importFile = DatabaseWindowManager.showFileChooserDialog(false,getUserPreferences());						
//					    java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().getImage(importFile.getAbsolutePath());
//					    java.awt.image.PixelGrabber pg =
//					        new java.awt.image.PixelGrabber(
//					            image,
//					            0,
//					            0,
//					            image.getWidth(null),
//					            image.getHeight(null),
//					            false);
//					    pg.grabPixels();
//					    int width = image.getWidth(null);
//					    int height = image.getHeight(null);
//					    System.out.println(width+ " " + height+ " "+ importFile.getAbsolutePath());
//					    Object pixData = pg.getPixels();
//
//					    //Save DB
//						cbit.vcell.simdata.ExternalDataIdentifier fdi = null;
//						String fieldDataInfo = "FieldDataName,VarName,0,0,0,1,1,1,"+width+","+height+",1";
//						String fieldDataName = null;
//						Origin origin = null;
//						Extent extent = null;
//						ISize isize = null;
//						cbit.vcell.simdata.ExternalDataIdentifier fdiDB = null;
//						String varName = null;
//						while(fdi == null){
//							fieldDataInfo = PopupGenerator.showInputDialog((Component)null,"Enter Field Data (name,var,O,E,S)",fieldDataInfo);
//							if(fieldDataInfo == null || fieldDataInfo.length() == 0){
//								PopupGenerator.showErrorDialog("Field Data info cannot be empty");
//								continue;
//							}
//							java.util.StringTokenizer st = new java.util.StringTokenizer(fieldDataInfo, ",");
//							fieldDataName = null;
//							try{
//								fieldDataName = st.nextToken();
//							}catch(Exception e){
//								PopupGenerator.showErrorDialog("Error parsing FieldDataName "+e.getMessage());
//								continue;
//							}
//							for(int i=0;i<fdiArr.length;i+= 1){
//								if(fdiArr[i].getName().equals(fieldDataName)){
//									fieldDataName = null;
//									PopupGenerator.showErrorDialog("Field Dataname "+fdiArr[i].getName()+" already exists,");
//									break;
//								}
//							}
//							if(fieldDataName != null){
//								try{
//									varName = st.nextToken();
//									origin = new Origin(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
//									extent = new Extent(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
//									isize = new ISize(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
//									break;
//								}catch(Exception e){
//									PopupGenerator.showErrorDialog("Error parsing FieldData Info "+e.getMessage());
//								}
//							}
//						}
//						fdiDB = getRequestManager().getDocumentManager().saveExternalDataIdentifier(fieldDataName);
//					    //Save Disk
//					    try{
//					    	cbit.vcell.client.server.FieldDataFileOperationSpec fdos = new cbit.vcell.client.server.FieldDataFileOperationSpec();
//					    	fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
//					    	fdos.byteSpecData =  new byte[][][] {{(byte[])pixData}};
//					    	fdos.specEDI = fdiDB;
//					    	fdos.varNames = new String[] {varName};
//					    	fdos.owner = getUser();
//					    	fdos.times = new double[] {0};
//					    	fdos.variableTypes = new VariableType[] {VariableType.VOLUME};
//					    	fdos.origin = origin;
//					    	fdos.extent = extent;
//					    	fdos.isize = isize;
//					    	//Create Mesh
//					    	byte[] riBytes = new byte[isize.getX()*isize.getY()*isize.getZ()];
//					    	Arrays.fill(riBytes, (byte)0);
//					    	VCImage riVCImage = new VCImageUncompressed(null,riBytes,extent,isize.getX(),isize.getY(),isize.getZ());
//					    	RegionImage regionImage = new RegionImage(riVCImage);
//					    	fdos.cartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, extent, isize,regionImage);
//					    	//
//					    	fdos.cartesianMesh.write(System.out);
//					    	//
//					    	//Do Operation
//					    	cbit.vcell.client.server.FieldDataFileOperationResults fdor =  getRequestManager().getVCDataManager().fieldDataFileOperation(fdos);
//					    }catch(Exception e){
//						    getRequestManager().getDocumentManager().deleteExternalDataIdentifiers(new KeyValue[] {fdiDB.getKey()});
//						    throw e;
//					    }
//
//					    fdiArrV.clear();
//					    cbit.vcell.simdata.ExternalDataIdentifier[] fdiArrTemp = getRequestManager().getDocumentManager().getExternalDataIdentifiers();
//					    fdiArrV.addAll(java.util.Arrays.asList(fdiArrTemp));
//					    fdList.removeAll();
//					    fdList.setListData(fdiArrV);
//					}catch(cbit.vcell.client.task.UserCancelException e){
//					}catch(Exception e){
//						PopupGenerator.showErrorDialog("Error importing Field Data "+e.getMessage());
//					}
//				}
//			}
//		);
//
//		JButton deleteJButton = new JButton("Delete");
//		deleteJButton.addActionListener(
//			new java.awt.event.ActionListener(){
//				public void actionPerformed(java.awt.event.ActionEvent actionEvent){
//					int selectedIndex = fdList.getSelectedIndex();
//					if(selectedIndex != -1){
//						cbit.vcell.simdata.ExternalDataIdentifier selectedFDI = (cbit.vcell.simdata.ExternalDataIdentifier)fdiArrV.elementAt(selectedIndex);
//						try{
//							//Delete from DB
//							getRequestManager().getDocumentManager().deleteExternalDataIdentifiers(new KeyValue[] {selectedFDI.getKey()});
//							//Delete from Disk
//							cbit.vcell.client.server.FieldDataFileOperationSpec fdos = new cbit.vcell.client.server.FieldDataFileOperationSpec();
//					    	fdos.opType = FieldDataFileOperationSpec.FDOS_DELETE;
//					    	fdos.specEDI = selectedFDI;
//					    	fdos.owner = getUser();
//					    	getRequestManager().getVCDataManager().fieldDataFileOperation(fdos);
//
//					    	//Update GUI
//						    fdiArrV.clear();
//						    cbit.vcell.simdata.ExternalDataIdentifier[] fdiArrTemp = getRequestManager().getDocumentManager().getExternalDataIdentifiers();
//						    fdiArrV.addAll(java.util.Arrays.asList(fdiArrTemp));
//						    fdList.removeAll();
//						    fdList.setListData(fdiArrV);
//						}catch(Exception e){
//							PopupGenerator.showErrorDialog("Error deleting Field Data "+e.getMessage());
//						}
//					}
//				}
//			}
//		);
//
//		fdList.setListData(fdiArrV);
//		fdList.addListSelectionListener(
//			new javax.swing.event.ListSelectionListener(){
//				public void valueChanged(javax.swing.event.ListSelectionEvent e){
//					if(!e.getValueIsAdjusting()){
//						System.out.println("index ="+fdList.getSelectedIndex());
//						if(fdList.getSelectedIndex() != -1){
//						}
//					}
//				}
//			}
//		);
//		
//		java.awt.GridBagConstraints constraintsFDPanel = new java.awt.GridBagConstraints();
//		constraintsFDPanel.gridx = 0; constraintsFDPanel.gridy = 0;
//		//constraintsGrantAccessJPanel.fill = java.awt.GridBagConstraints.BOTH;
//		//constraintsGrantAccessJPanel.anchor = java.awt.GridBagConstraints.WEST;
//		//constraintsGrantAccessJPanel.weightx = 1.0;
//		//constraintsGrantAccessJPanel.weighty = 1.0;
//		//constraintsGrantAccessJPanel.insets = new java.awt.Insets(5, 10, 5, 10);
//		Box buttonBox = new Box(BoxLayout.X_AXIS);
//		//buttonBox.add(importBioformatsJButton);
//		buttonBox.add(importJButton);
//		buttonBox.add(deleteJButton);
//		fdPanel.add(buttonBox, constraintsFDPanel);
//		//fdPanel.add(importJButton, constraintsFDPanel);
//
//		//constraintsFDPanel.gridx = 1; constraintsFDPanel.gridy = 0;
//		//fdPanel.add(deleteJButton, constraintsFDPanel);
//
//		constraintsFDPanel.gridx = 0; constraintsFDPanel.gridy = 1;
//		constraintsFDPanel.gridwidth = 1;
//		constraintsFDPanel.weightx = 1;
//		fdPanel.add(fdList, constraintsFDPanel);
//
//		fdPanel.setSize(500, 300);
//		PopupGenerator.showComponentCloseDialog(null,fdPanel,"Field Data Wiondow");
//
//	}catch(Exception e){
//		PopupGenerator.showErrorDialog("Error getting Field Data Info\n"+e.getMessage());
//	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (6/4/2004 4:42:35 AM)
 */
public abstract void showFrame(JInternalFrame frame);

/**
 * Insert the method's description here.
 * Creation date: (6/4/2004 4:42:35 AM)
 */
public static void showFrame(JInternalFrame frame, JDesktopPaneEnhanced pane) {
	if (!BeanUtils.arrayContains(pane.getAllFrames(),frame)){
		pane.add(frame);
	}
	
	if (frame.isClosed()) {
		try {
			frame.setClosed(false);
		} catch (java.beans.PropertyVetoException exc) {
		}
	}
	if (frame.isIcon()) {
		try {
			frame.setIcon(false);
		} catch (java.beans.PropertyVetoException exc) {
		}
	}
	//frame.pack();
	frame.setSize(600,500);
	BeanUtils.centerOnComponent(frame, pane);
	frame.show();
	try {
		frame.setSelected(true);
	} catch (PropertyVetoException e) {
		e.printStackTrace();
	}
}


/**
 * Comment
 */
public void showTestingFrameworkWindow() {
	getRequestManager().showTestingFrameworkWindow();
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 9:58:46 PM)
 */
public void startExport(
		OutputContext outputContext,ExportSpecs exportSpecs) {
	getRequestManager().startExport(outputContext,this, exportSpecs);
}


/**
 * Comment
 */
public void tileWindows(boolean horizontal) {
	JInternalFrame[] iframes = getOpenWindows();
	Rectangle[] bounds = BeanUtils.getTiledBounds(iframes.length, getJDesktopPane().getWidth(), getJDesktopPane().getHeight(), horizontal);
	for (int i=0;i<iframes.length;i++) {
		iframes[i].setBounds(bounds[i]);
		iframes[i].show();
	}
}

//GeometrySelectionInfo selectGeometry(boolean bShowCurrentGeomChoice) throws Exception,UserCancelException{
//	final int ANALYTIC_1D = 0;
//	final int ANALYTIC_2D = 1;
//	final int ANALYTIC_3D = 2;
//	final int IMAGE_DB = 3;
//	final int IMAGE_FILE = 4;
//	final int COPY_FROM_BIOMODEL = 5;
//	final int COPY_FROM_MATHMODEL = 6;
//	final int COPY_FROM_GEOMETRY = 7;
//	final int FROM_SCRATCH = 8;
//	final int FROM_CURRENT_GEOM = 9;
//	int[] geomType = null;
//
//	String[][] choices = new String[][] {{"Analytic Equations (1D)"},{"Analytic Equations (2D)"},{"Analytic Equations (3D)"},
//			{"Image based (legacy from database)"},{"Image based (import from file, zip or directory)"},
//			{"Copy from BioModel application"},{"Copy from MathModel"},{"Copy from saved Geometry"},
//			{"From scratch"}};
//	if(bShowCurrentGeomChoice){
//		Vector<String[]> choiceV = new Vector<String[]>();
//		choiceV.addAll(Arrays.asList(choices));
//		choiceV.add(new String[] {"From Current Geometry"});
//		choices = choiceV.toArray(new String[0][]);
//	}
//	geomType = DialogUtils.showComponentOKCancelTableList(
//			getComponent(), 
//			"Choose new geometry type to create",
//			new String[] {"Geometry Type"}, 
//			choices, ListSelectionModel.SINGLE_SELECTION);
//
//	VCDocument.DocumentCreationInfo documentCreationInfo = null;
//	VCDocumentInfo vcDocumentInfo = null;
//	if(geomType[0] == ANALYTIC_1D){
//		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocument.GEOMETRY_DOC, VCDocument.GEOM_OPTION_1D);
//	}else if(geomType[0] == ANALYTIC_2D){
//		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocument.GEOMETRY_DOC, VCDocument.GEOM_OPTION_2D);
//	}else if(geomType[0] == ANALYTIC_3D){
//		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocument.GEOMETRY_DOC, VCDocument.GEOM_OPTION_3D);
//	}else if(geomType[0] == IMAGE_DB){
//		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocument.GEOMETRY_DOC, VCDocument.GEOM_OPTION_DBIMAGE);
//	}else if(geomType[0] == IMAGE_FILE){
//		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocument.GEOMETRY_DOC, VCDocument.GEOM_OPTION_FILE);
//	}else if(geomType[0] == COPY_FROM_BIOMODEL){
//		vcDocumentInfo = ((ClientRequestManager)getRequestManager()).selectDocumentFromType(VCDocument.BIOMODEL_DOC, this);
//	}else if(geomType[0] == COPY_FROM_MATHMODEL){
//		vcDocumentInfo = ((ClientRequestManager)getRequestManager()).selectDocumentFromType(VCDocument.MATHMODEL_DOC, this);
//	}else if(geomType[0] == COPY_FROM_GEOMETRY){
//		vcDocumentInfo = ((ClientRequestManager)getRequestManager()).selectDocumentFromType(VCDocument.GEOMETRY_DOC, this);
//	}else if(geomType[0] == FROM_SCRATCH){
//		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocument.GEOMETRY_DOC, VCDocument.GEOM_OPTION_FROM_SCRATCH);
//	}else if(geomType[0] == FROM_CURRENT_GEOM){
//		return new DocumentWindowManager.GeometrySelectionInfo();
//	}else{
//		throw new IllegalArgumentException("Error selecting geometry, Unknown Geometry type "+geomType[0]);
//	}
//	DocumentWindowManager.GeometrySelectionInfo geometrySelectionInfo = null;
//	if(documentCreationInfo != null){
//		geometrySelectionInfo = new DocumentWindowManager.GeometrySelectionInfo(documentCreationInfo);
//	}else{
//		geometrySelectionInfo = new DocumentWindowManager.GeometrySelectionInfo(vcDocumentInfo);
//	}
//	
//	return geometrySelectionInfo;
//}

//public static final String B_SHOW_OLD_GEOM_EDITOR = "B_SHOW_OLD_GEOM_EDITOR";
//void createGeometry(final Geometry currentGeometry,final AsynchClientTask[] afterTasks){
//	
//	try{
//		final Hashtable<String, Object> hash = new Hashtable<String, Object>();
//		Vector<AsynchClientTask> createGeomTaskV = new Vector<AsynchClientTask>();
//		final DocumentWindowManager.GeometrySelectionInfo geometrySelectionInfo =
//			selectGeometry(currentGeometry != null && currentGeometry.getDimension() >0);
//		hash.put(B_SHOW_OLD_GEOM_EDITOR, false);
//		if(geometrySelectionInfo.getDocumentCreationInfo() != null){
//			if(ClientRequestManager.isImportGeometryType(geometrySelectionInfo.getDocumentCreationInfo())){
//				//Create imported Geometry
//				createGeomTaskV.addAll(Arrays.asList(
//					((ClientRequestManager)getRequestManager()).createNewGeometryTasks(this,
//						geometrySelectionInfo.getDocumentCreationInfo(),
//						afterTasks,
//						"Apply Geometry")));
//			}else{//Create Analytic Geometry
//				hash.put(B_SHOW_OLD_GEOM_EDITOR, true);
//				createGeomTaskV.addAll(Arrays.asList(((ClientRequestManager)getRequestManager()).createNewDocument(this,
//						geometrySelectionInfo.getDocumentCreationInfo())));
//				createGeomTaskV.addAll(Arrays.asList(afterTasks));
//			}
//			hash.put("guiParent", (Component)getComponent());
//			hash.put("requestManager", getRequestManager());
//		}else{//Copy from existing BioModel,MathModel,Geometry
//			createGeomTaskV.add(new AsynchClientTask("loading Geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
//				@Override
//				public void run(Hashtable<String, Object> hashTable) throws Exception {
//					Geometry copiedGeom = null;
//					if(geometrySelectionInfo.bFromCurrentGeom){
//						copiedGeom = new Geometry(currentGeometry);
//					}else{
//						copiedGeom =
//							((ClientRequestManager)getRequestManager()).getGeometryFromDocumentSelection(
//								getComponent(),geometrySelectionInfo.getVCDocumentInfo(),true);
//					}
//					final Vector<AsynchClientTask> runtimeTasksV = new Vector<AsynchClientTask>();
//					if(copiedGeom.getGeometrySpec().getImage() != null &&
//						copiedGeom.getGeometrySpec().getNumAnalyticSubVolumes() == 0){
//						runtimeTasksV.addAll(Arrays.asList(((ClientRequestManager)getRequestManager()).createNewGeometryTasks(DocumentWindowManager.this,
//								new VCDocument.DocumentCreationInfo(VCDocument.GEOMETRY_DOC, VCDocument.GEOM_OPTION_DBIMAGE),
//								afterTasks,
//								"Apply Geometry")));
//						hashTable.put("guiParent", (Component)getComponent());
//						hashTable.put("requestManager", getRequestManager());
//						hashTable.put(ClientRequestManager.IMAGE_FROM_DB, copiedGeom.getGeometrySpec().getImage());
//					}else{
//						hashTable.put(B_SHOW_OLD_GEOM_EDITOR, true);
//						//preload sampledimage to prevent gui delay later
//						copiedGeom.getGeometrySpec().getSampledImage();
//						hashTable.put("doc",copiedGeom);
//						runtimeTasksV.addAll(Arrays.asList(afterTasks));
//					}
//					new Thread(
//						new Runnable() {
//							public void run() {
//								ClientTaskDispatcher.dispatch(getComponent(),
//										hash,runtimeTasksV.toArray(new AsynchClientTask[0]), false,false,null,true);
//							}
//						}
//					).start();
//				}			
//			});
//		}
//		ClientTaskDispatcher.dispatch(getComponent(), hash, createGeomTaskV.toArray(new AsynchClientTask[0]), false,false,null,true);
//		
//	} catch (UserCancelException e1) {
//		return;
//	} catch (Exception e1) {
//		e1.printStackTrace();
//		DialogUtils.showErrorDialog(getComponent(), e1.getMessage(), e1);
//	}
//	
//
//
//}

}
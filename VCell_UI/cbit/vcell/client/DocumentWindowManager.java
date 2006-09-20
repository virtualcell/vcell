package cbit.vcell.client;
import cbit.xml.merge.TMLPanel;
import swingthreads.*;
import java.net.*;
import cbit.vcell.clientdb.*;
import cbit.vcell.server.*;
import cbit.vcell.simdata.DataJobEvent;

import java.io.*;

import cbit.vcell.export.ExportEvent;
import cbit.vcell.export.ExportSpecs;
import cbit.vcell.export.server.*;
import cbit.rmi.event.*;
import cbit.vcell.client.data.*;
import cbit.plot.*;
import cbit.vcell.desktop.controls.*;
import cbit.gui.*;
import cbit.sql.*;
import cbit.util.*;
import cbit.vcell.geometry.*;
import cbit.vcell.mathmodel.*;

import java.awt.*;
import cbit.vcell.biomodel.*;
import cbit.vcell.client.*;
import cbit.vcell.document.*;
import java.beans.*;
import javax.swing.*;
import cbit.vcell.client.server.*;
import cbit.vcell.solver.*;
/**
 * Insert the type's description here.
 * Creation date: (5/5/2004 1:01:37 PM)
 * @author: Ion Moraru
 */
public abstract class DocumentWindowManager extends cbit.vcell.client.TopLevelWindowManager implements PerformanceMonitorListener, java.awt.event.ActionListener, DataViewerManager {
	private JPanel jPanel = null;
	private String documentID = null;

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
public abstract void addResultsFrame(cbit.vcell.client.desktop.simulation.SimulationWindow simWindow);


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
	final MDIManager mdiManager = new ClientMDIManager(DocumentWindowManager.this.getRequestManager());        
	mdiManager.blockWindow(DocumentWindowManager.this.getManagerID()); 
	SwingWorker worker = new SwingWorker() {
		AsynchProgressPopup pp = new AsynchProgressPopup(DocumentWindowManager.this.getComponent(), "Working", "Retrieving saved version...", false, false);
		Throwable e = null;
		VCDocument document = DocumentWindowManager.this.getVCDocument();
		public Object construct() {
			try {
				pp.start();
				// compare
				pp.setMessage("Comparing...");
				TMLPanel comparePanel = DocumentWindowManager.this.getRequestManager().compareWithSaved(document);
				return comparePanel;
			} catch (Throwable exc) {
				e = exc;
				return null;
			}
		}
		public void finished() {
			if (e != null) {
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(DocumentWindowManager.this, "Unable to compare to saved version\n"+e);
			} 
			// done
			pp.stop();
			//display the comparison window.
			JOptionPane comparePane = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {"Apply Changes", "Discard"});
			TMLPanel comparePanel = (TMLPanel)get();
			comparePane.setMessage(comparePanel);
			JDialog compareDialog = comparePane.createDialog(DocumentWindowManager.this.getComponent(), "Compare Models");      
			compareDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			cbit.gui.ZEnforcer.showModalDialogOnTop(compareDialog,DocumentWindowManager.this.getComponent());
			if ("Apply Changes".equals(comparePane.getValue())) {
				if (!comparePanel.tagsResolved()) {
					JOptionPane messagePane = new JOptionPane("Please resolve all tagged elements/attributes before proceeding.");
					JDialog messageDialog = messagePane.createDialog(comparePanel, "Error");
					messageDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					cbit.gui.ZEnforcer.showModalDialogOnTop(messageDialog,comparePanel);
				} else {
					BeanUtils.setCursorThroughout((Container)getComponent(), Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					try {
						getRequestManager().processComparisonResult(comparePanel, DocumentWindowManager.this);
					} catch (RuntimeException e) {
						throw e;
					} finally {
						BeanUtils.setCursorThroughout((Container)getComponent(), Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				} 
			}     
			mdiManager.unBlockWindow(DocumentWindowManager.this.getManagerID());
		}
	};
	worker.start();
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
	if(frameContent instanceof cbit.vcell.client.desktop.geometry.SurfaceViewerPanel){
		jif = new JInternalFrameEnhanced("Surface Viewer", true, true, true, true);
		jif.setContentPane(frameContent);
		jif.setSize(500,500);
		jif.setMinimumSize(new Dimension(400,400));
		jif.setLocation(550, 350);
	}else if(frameContent instanceof cbit.vcell.client.desktop.geometry.GeometrySummaryViewer){
		jif = new JInternalFrameEnhanced("Geometry Summary", true, true, true, true);
		jif.setContentPane(frameContent);
		jif.setSize(700,400);
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
	if (haveSimulationWindow(exportEvent.getVCSimulationIdentifier()) == null) {// && exportEvent.getEventTypeID() != ExportEvent.EXPORT_COMPLETE) {
		return;
	}
	// just pass them along...
	fireExportMessage(exportEvent);
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 1:46:17 PM)
 * @return javax.swing.JPanel
 */
protected Component getComponent() {
	return jPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (6/21/2005 1:05:03 PM)
 * @return javax.swing.JDesktopPane
 */
protected abstract JDesktopPane getJDesktopPane();


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
	java.util.Vector openWindows = new java.util.Vector();
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
abstract cbit.vcell.client.desktop.simulation.SimulationWindow haveSimulationWindow(VCSimulationIdentifier vcSimulationIdentifier);


/**
 * Comment
 */
public void newDocument(int documentType, int option) {
	getRequestManager().newDocument(documentType, option);
}


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
public void openGeometryDocumentWindow(Geometry geom) {
	
	try{
		cbit.vcell.geometry.GeometryInfo geomInfo = null;
		try{
			geomInfo = getRequestManager().getDocumentManager().getGeometryInfo(geom.getVersion().getVersionKey());
		}catch(ObjectNotFoundException e){
			if(!getVCDocument().getVersion().getOwner().equals(getRequestManager().getDocumentManager().getUser())){
				DialogUtils.showErrorDialog(
					"Opening a geometry document window for '"+geom.getName()+"' from\n"+
					"Model '"+getVCDocument().getName()+"' owned by "+getVCDocument().getVersion().getOwner().getName()+"\n"+
					"FAILED because requester ("+getRequestManager().getDocumentManager().getUser()+") does not have permission.\n"+
					"Save Model '"+getVCDocument().getName()+"' to your account to have full access to the geometry."
				);
				return;
			}else{
				throw e;
			}
		}
		getRequestManager().openDocument(geomInfo,this,true);
	}catch(Exception e){
		e.printStackTrace();
		DialogUtils.showErrorDialog("Error opening Geometry\n"+e.getMessage());
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 3:13:55 PM)
 * @param pme cbit.rmi.event.PerformanceMonitorEvent
 */
public void performanceMonitorEvent(cbit.rmi.event.PerformanceMonitorEvent pme) {
	// just pass it to the the message manager
	getRequestManager().getAsynchMessageManager().performanceMonitorEvent(pme);
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 2:48:52 AM)
 * @return java.lang.String
 */
public void reconnect() {
	getRequestManager().reconnect();
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
	

	if(jif.getContentPane() instanceof cbit.vcell.client.desktop.geometry.GeometrySummaryViewer){
		Geometry geom = ((cbit.vcell.client.desktop.geometry.GeometrySummaryViewer)jif.getContentPane()).getGeometry();
		jif.setTitle("Info for Geometry "+(geom != null?geom.getName():""));
	}else if(jif.getContentPane() instanceof cbit.vcell.client.desktop.geometry.SurfaceViewerPanel){
		Geometry geom = ((cbit.vcell.client.desktop.geometry.SurfaceViewerPanel)jif.getContentPane()).getGeometry();
		jif.setTitle("Surface for Geometry "+(geom != null?geom.getName():""));
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
 * Creation date: (6/4/2004 4:42:35 AM)
 */
public abstract void showFrame(JInternalFrame frame);

/**
 * Insert the method's description here.
 * Creation date: (6/4/2004 4:42:35 AM)
 */
public static void showFrame(JInternalFrame frame, JDesktopPane pane) {
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
	frame.show();
	pane.getDesktopManager().activateFrame(frame);
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
public void startExport(ExportSpecs exportSpecs) {
	getRequestManager().startExport(this, exportSpecs);
}


/**
 * Comment
 */
public void tileWindows(boolean horizontal) {
	JInternalFrame[] iframes = getOpenWindows();
	Rectangle[] bounds = cbit.util.BeanUtils.getTiledBounds(iframes.length, getJDesktopPane().getWidth(), getJDesktopPane().getHeight(), horizontal);
	for (int i=0;i<iframes.length;i++) {
		iframes[i].setBounds(bounds[i]);
		iframes[i].show();
	}
}
}
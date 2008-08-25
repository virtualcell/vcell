package cbit.vcell.client;
import cbit.util.EventDispatchRunWithException;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.gui.JInternalFrameEnhanced;

import java.awt.*;
import javax.swing.*;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.client.desktop.geometry.*;

/**
 * Insert the type's description here.
 * Creation date: (5/25/2004 2:46:07 AM)
 * @author: Ion Moraru
 */
public class GeometryWindowManager extends DocumentWindowManager implements java.beans.PropertyChangeListener {
	private Geometry geometry = null;
	private JDesktopPane jDesktopPane = null;
	private GeometryEditor geometryEditor = null;

	private SurfaceViewerPanel surfaceViewer;
	private GeometryDisplayPanel geoViewer;

	JInternalFrameEnhanced geometryViewerEditorFrame = null;		
	JInternalFrameEnhanced surfaceViewerEditorFrame = null;

/**
 * GeometryWindowManager constructor comment.
 * @param pane javax.swing.JDesktopPane
 * @param requestManager cbit.vcell.client.RequestManager
 * @param vcDocument cbit.vcell.document.VCDocument
 * @param newlyCreatedDesktops int
 */
public GeometryWindowManager(JPanel panel, cbit.vcell.client.RequestManager requestManager, final Geometry aGeometry, int newlyCreatedDesktops) {
	super(panel, requestManager, aGeometry, newlyCreatedDesktops);

	new EventDispatchRunWithException (){
		public Object runWithException() throws Exception{
			surfaceViewer = new SurfaceViewerPanel();
			geoViewer = new GeometryDisplayPanel();
			setGeometry(aGeometry);
			setJDesktopPane(new JDesktopPane());
			getJPanel().setLayout(new BorderLayout());
			getJPanel().add(getJDesktopPane(), BorderLayout.CENTER);
			createGeometryEditor();
			initializeInternalFrames();
			getJPanel().add(getGeometryEditor(), BorderLayout.NORTH);
			if (System.getProperty("java.version").compareTo("1.3") >= 0) {
				showGeometryViewer(true);
			}
			return null;
		}
	}.runEventDispatchThreadSafelyWrapRuntime();

}


	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 7:26:51 AM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public void addResultsFrame(cbit.vcell.client.desktop.simulation.SimulationWindow simWindow) {
	// ignore
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void createGeometryEditor() {
	geometryEditor = new GeometryEditor();
	geometryEditor.setGeometry(getGeometry());
	geometryEditor.setGeometryWindowManager(this);
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:19:49 AM)
 * @param newJDesktopPane javax.swing.JDesktopPane
 */
public void geometryEditorButtonPressed(boolean bGeoEditorButtonSelected) {
	showGeometryViewer(bGeoEditorButtonSelected);
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:19:49 AM)
 * @return cbit.vcell.geometry.Geometry
 */
private cbit.vcell.geometry.Geometry getGeometry() {
	return geometry;
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:19:49 AM)
 * @return cbit.vcell.client.desktop.geometry.GeometryEditor
 */
private cbit.vcell.client.desktop.geometry.GeometryEditor getGeometryEditor() {
	return geometryEditor;
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:19:49 AM)
 * @return javax.swing.JDesktopPane
 */
protected javax.swing.JDesktopPane getJDesktopPane() {
	return jDesktopPane;
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 1:46:17 PM)
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel() {
	return (JPanel)getComponent();
}


/**
 * Insert the method's description here.
 * Creation date: (5/25/2004 2:46:07 AM)
 * @return cbit.vcell.document.VCDocument
 */
public cbit.vcell.document.VCDocument getVCDocument() {
	return getGeometry();
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 7:57:23 AM)
 * @return cbit.vcell.document.VCDocument
 */
cbit.vcell.client.desktop.simulation.SimulationWindow haveSimulationWindow(VCSimulationIdentifier vcSimulationIdentifier) {
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void initializeInternalFrames() {

	// Initialize Geometry Editor internal frame
	String geoViewerTitle = "Geometry Editor";
	geoViewer.setGeometry(getGeometry());
	geometryViewerEditorFrame = new JInternalFrameEnhanced(geoViewerTitle, true, true, true, true);
	geometryViewerEditorFrame.setContentPane(geoViewer);
	geometryViewerEditorFrame.setSize(600, 600);
	geometryViewerEditorFrame.setLocation(10, 10);
	geometryViewerEditorFrame.setMinimumSize(new Dimension(450, 450));
	geometryViewerEditorFrame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
		public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
			getGeometryEditor().setToggleButtonSelected("Geometry Editor", false);
		};
	});		
	
	
	// Initialize Surface Viewer internal frame
	String surfaceViewerTitle = "Surface Viewer";
	surfaceViewer.setGeometry(getGeometry());
	surfaceViewerEditorFrame = new JInternalFrameEnhanced(surfaceViewerTitle, true, true, true, true);
	surfaceViewerEditorFrame.setContentPane(surfaceViewer);
	surfaceViewerEditorFrame.setSize(600, 600);
	surfaceViewerEditorFrame.setLocation(300, 100);
	surfaceViewerEditorFrame.setMinimumSize(new Dimension(450, 450));
	surfaceViewerEditorFrame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
		public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
			getGeometryEditor().setToggleButtonSelected("Surface Viewer", false);
		};
	});
	
}


/**
 * Insert the method's description here.
 * Creation date: (5/25/2004 2:46:07 AM)
 * @return boolean
 */
public boolean isRecyclable() {
	return false;
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {

	if(evt.getSource() instanceof cbit.vcell.geometry.GeometrySpec){
		getGeometryEditor().setToggleButtonSelected("Surface Viewer",false);
		//do this because button.setSelected does not fire action event
		if(surfaceViewerEditorFrame != null){
			surfaceViewerButtonPressed(false);
		}
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 2:00:39 PM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public void resetDocument(cbit.vcell.document.VCDocument newDocument) {
	setGeometry((Geometry)newDocument);
	setDocumentID(getGeometry());
	geoViewer.setGeometry(getGeometry());
	surfaceViewer.setGeometry(getGeometry());
	getRequestManager().updateStatusNow();
	System.out.println("XXXXXXXXXXXXXXXXXXXX not yet finished");
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
 * Creation date: (6/3/2004 10:19:49 AM)
 * @param newGeometry cbit.vcell.geometry.Geometry
 */
private void setGeometry(cbit.vcell.geometry.Geometry newGeometry) {
	if(geometry != null && geometry.getGeometrySpec() != null){
		geometry.getGeometrySpec().removePropertyChangeListener(this);
	}
	
	geometry = newGeometry;

	if(newGeometry != null && newGeometry.getGeometrySpec() != null){
		newGeometry.getGeometrySpec().addPropertyChangeListener(this);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:19:49 AM)
 * @param newGeometryEditor cbit.vcell.client.desktop.geometry.GeometryEditor
 */
private void setGeometryEditor(cbit.vcell.client.desktop.geometry.GeometryEditor newGeometryEditor) {
	geometryEditor = newGeometryEditor;
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:19:49 AM)
 * @param newJDesktopPane javax.swing.JDesktopPane
 */
private void setJDesktopPane(javax.swing.JDesktopPane newJDesktopPane) {
	jDesktopPane = newJDesktopPane;
}


/**
 * Insert the method's description here.
 * Creation date: (6/14/2004 11:30:24 PM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public void showDataViewerPlotsFrames(javax.swing.JInternalFrame[] plotFrames) {
	// ignore
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (6/14/2004 10:54:11 PM)
 */
public void showFrame(javax.swing.JInternalFrame frame) {
	showFrame(frame, getJDesktopPane());
}	


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:19:49 AM)
 * @param newJDesktopPane javax.swing.JDesktopPane
 */
private void showGeometryViewer(boolean bGeoEditorButtonSelected) {
	if (bGeoEditorButtonSelected) {
		// If toggleButton is selected, check if it is open. If not, open it, add it to desktopPane
		// If it is iconized, 'de-iconify' it.
		showFrame(geometryViewerEditorFrame);
	} else {
		// If toggleButton is unselected, check if vcmlEditor is iconized. If not closed, dispose it.
		// If it is iconized on the desktop, de-iconify vcmlEditor & dispose it.
		close(geometryViewerEditorFrame, getJDesktopPane());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:19:49 AM)
 * @param newJDesktopPane javax.swing.JDesktopPane
 */
private void showSurfaceViewer(boolean bSurfaceViewerButtonSelected) {
	if (bSurfaceViewerButtonSelected) {
		// If toggleButton is selected, check if it is open. If not, open it, add it to desktopPane
		// If it is iconized, 'de-iconify' it.
		showFrame(surfaceViewerEditorFrame);
		//
		if(getGeometry() != null && getGeometry().getGeometrySurfaceDescription() != null && getGeometry().getGeometrySurfaceDescription().getSurfaceCollection() == null){
			try{
				surfaceViewer.updateSurfaces();
			}catch(Exception e){
				cbit.gui.DialogUtils.showErrorDialog("Error initializing Surfaces\n"+e.getClass().getName()+"\n"+e.getMessage());
			}
		}
	} else {
		// If toggleButton is unselected, check if vcmlEditor is iconized. If not closed, dispose it.
		// If it is iconized on the desktop, de-iconify vcmlEditor & dispose it.
		close(surfaceViewerEditorFrame, getJDesktopPane());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 1:38:49 AM)
 * @param newJobStatus cbit.vcell.messaging.db.SimulationJobStatus
 * @param progress java.lang.Double
 * @param timePoint java.lang.Double
 */
public void simStatusChanged(SimStatusEvent simStatusEvent) {
	// ignore, geometries don't have simulations to worry about...
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:19:49 AM)
 * @param newJDesktopPane javax.swing.JDesktopPane
 */
public void surfaceViewerButtonPressed(boolean bSurfaceViewerButtonSelected) {
	showSurfaceViewer(bSurfaceViewerButtonSelected);
}
}
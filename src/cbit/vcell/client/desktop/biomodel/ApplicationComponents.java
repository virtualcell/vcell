package cbit.vcell.client.desktop.biomodel;
import java.awt.Dimension;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.JInternalFrameEnhanced;

import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.ClientSimManager;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.desktop.geometry.SurfaceViewerPanel;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.client.desktop.simulation.SimulationWorkspace;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationIdentifier;
/**
 * Insert the type's description here.
 * Creation date: (6/3/2004 4:37:44 PM)
 * @author: Ion Moraru
 */
public class ApplicationComponents {
	private JInternalFrameEnhanced appEditorFrame = null;
//	private JInternalFrameEnhanced geometrySummaryViewerFrame = null;
	private JInternalFrameEnhanced surfaceViewerFrame = null;	
	private ApplicationEditor appEditor = null;
//	private GeometrySummaryViewer geometrySummaryViewer = null;
	private SurfaceViewerPanel surfaceViewer = null;
	private Hashtable<VCSimulationIdentifier, SimulationWindow> simulationWindowsHash = new Hashtable<VCSimulationIdentifier, SimulationWindow>();

/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 4:41:32 PM)
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
public ApplicationComponents(SimulationContext simContext, BioModelWindowManager bioModelWindowManager, final JDesktopPane pane) {
	// make the app editor
	setAppEditor(new ApplicationEditor());
	getAppEditor().setSimulationWorkspace(new SimulationWorkspace(bioModelWindowManager, simContext));
	getAppEditor().setOptimizationService(bioModelWindowManager.getOptimizationService());
	getAppEditor().setUserPreferences(bioModelWindowManager.getUserPreferences());
	setAppEditorFrame(new JInternalFrameEnhanced("APPLICATION: "+simContext.getName(), true, true, true, true));
	getAppEditorFrame().setContentPane(getAppEditor());
	getAppEditorFrame().setSize(800,650);
	getAppEditorFrame().setMinimumSize(new Dimension(500,500));
	getAppEditorFrame().setLocation(400,200);
	getAppEditorFrame().addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
		public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
//			DocumentWindowManager.close(getGeometrySummaryViewerFrame(), pane);
			DocumentWindowManager.close(getSurfaceViewerFrame(), pane);
			DocumentWindowManager.close(getDataViewerFrames(), pane);
		};
	});
	
//	// make the geometry viewer
//	GeometrySummaryViewer geoViewer = new GeometrySummaryViewer();
//	geoViewer.setStochastic(simContext.isStoch());
//
//	setGeometrySummaryViewer(geoViewer);
//	getGeometrySummaryViewer().setGeometry(simContext.getGeometry());
//	getGeometrySummaryViewer().setStochastic(simContext.isStoch());
//	setGeometrySummaryViewerFrame(DocumentWindowManager.createDefaultFrame(getGeometrySummaryViewer()));

	// make the surface viewer
	setSurfaceViewer(new SurfaceViewerPanel());
	getSurfaceViewer().setGeometry(simContext.getGeometry());
	setSurfaceViewerFrame(DocumentWindowManager.createDefaultFrame(getSurfaceViewer()));
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 4:40:40 PM)
 */
public void addDataViewer(SimulationWindow simWindow) {
	simulationWindowsHash.put(simWindow.getVcSimulationIdentifier(), simWindow);
}


/**
 * Insert the method's description here.
 * Creation date: (6/21/2005 12:36:41 PM)
 */
public void cleanSimWindowsHash() {

	Enumeration<VCSimulationIdentifier> enum1 = simulationWindowsHash.keys();
	Vector<VCSimulationIdentifier> toRemove = new Vector<VCSimulationIdentifier>();
	while(enum1.hasMoreElements()){
		VCSimulationIdentifier vcsid = enum1.nextElement();
		Simulation[] sims = getAppEditor().getSimulationWorkspace().getSimulations();
		boolean bFound = false;
		for(int i=0;i<sims.length;i+= 1){
			if(sims[i].getSimulationInfo() != null && sims[i].getSimulationInfo().getAuthoritativeVCSimulationIdentifier().equals(vcsid)){
				bFound = true;
				break;
			}
		}
		if(!bFound){
			toRemove.add(vcsid);
		}
	}
	if(toRemove.size() > 0){
		for(int i=0;i<toRemove.size();i+= 1){
			simulationWindowsHash.remove(toRemove.elementAt(i));
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 4:40:40 PM)
 * @return cbit.vcell.client.desktop.biomodel.ApplicationEditor
 */
public ApplicationEditor getAppEditor() {
	return appEditor;
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 4:40:40 PM)
 * @return cbit.gui.JInternalFrameEnhanced
 */
public JInternalFrameEnhanced getAppEditorFrame() {
	return appEditorFrame;
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 4:40:40 PM)
 */
public JInternalFrame[] getDataViewerFrames() {
	SimulationWindow[] simWindows = (SimulationWindow[])BeanUtils.getArray(simulationWindowsHash.elements(), SimulationWindow.class);
	JInternalFrame[] frames = new JInternalFrame[simWindows.length];
	for (int i = 0; i < simWindows.length; i++){
		frames[i] = simWindows[i].getFrame();
	}
	return frames;
}


///**
// * Insert the method's description here.
// * Creation date: (6/4/2004 1:26:00 AM)
// * @return cbit.vcell.client.desktop.biomodel.GeometrySummaryViewer
// */
//public GeometrySummaryViewer getGeometrySummaryViewer() {
//	return geometrySummaryViewer;
//}
//
//
///**
// * Insert the method's description here.
// * Creation date: (6/4/2004 1:27:33 AM)
// * @return cbit.gui.JInternalFrameEnhanced
// */
//public JInternalFrameEnhanced getGeometrySummaryViewerFrame() {
//	return geometrySummaryViewerFrame;
//}

/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 4:40:40 PM)
 */
public SimulationWindow[] getSimulationWindows() {
	return (SimulationWindow[])BeanUtils.getArray(simulationWindowsHash.elements(), SimulationWindow.class);
}


/**
 * Insert the method's description here.
 * Creation date: (6/4/2004 1:26:00 AM)
 * @return cbit.vcell.client.desktop.biomodel.MathViewer
 */
public SurfaceViewerPanel getSurfaceViewer() {
	return surfaceViewer;
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 4:40:40 PM)
 * @return cbit.gui.JInternalFrameEnhanced
 */
public org.vcell.util.gui.JInternalFrameEnhanced getSurfaceViewerFrame() {
	return surfaceViewerFrame;
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 7:55:48 AM)
 * @return boolean
 * @param vcSimulationIdentifier cbit.vcell.server.VCSimulationIdentifier
 */
public SimulationWindow haveSimulationWindow(VCSimulationIdentifier vcSimulationIdentifier) {
	if (simulationWindowsHash.containsKey(vcSimulationIdentifier)) {
		return (SimulationWindow)simulationWindowsHash.get(vcSimulationIdentifier);
	} else {
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 4:41:32 PM)
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
public void resetSimulationContext(SimulationContext simContext) {
	// the app editor
	getAppEditor().resetSimContext(simContext);
//	// the geometry viewer
//	getGeometrySummaryViewer().setGeometry(simContext.getGeometry());
//	getGeometrySummaryViewer().setStochastic(simContext.isStoch());
	// the surface viewer
	getSurfaceViewer().setGeometry(simContext.getGeometry());
	
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 4:40:40 PM)
 * @param newAppEditor cbit.vcell.client.desktop.biomodel.ApplicationEditor
 */
private void setAppEditor(ApplicationEditor newAppEditor) {
	appEditor = newAppEditor;
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 4:40:40 PM)
 * @param newAppEditorFrame cbit.gui.JInternalFrameEnhanced
 */
private void setAppEditorFrame(org.vcell.util.gui.JInternalFrameEnhanced newAppEditorFrame) {
	appEditorFrame = newAppEditorFrame;
}


///**
// * Insert the method's description here.
// * Creation date: (6/4/2004 1:26:00 AM)
// * @param newGeometrySummaryViewer cbit.vcell.client.desktop.biomodel.GeometrySummaryViewer
// */
//private void setGeometrySummaryViewer(GeometrySummaryViewer newGeometrySummaryViewer) {
//	geometrySummaryViewer = newGeometrySummaryViewer;
//}
//
//
///**
// * Insert the method's description here.
// * Creation date: (6/4/2004 1:27:33 AM)
// * @param newGeometrySummaryViewerFrame cbit.gui.JInternalFrameEnhanced
// */
//private void setGeometrySummaryViewerFrame(org.vcell.util.gui.JInternalFrameEnhanced newGeometrySummaryViewerFrame) {
//	geometrySummaryViewerFrame = newGeometrySummaryViewerFrame;
//}

/**
 * Insert the method's description here.
 * Creation date: (6/4/2004 1:26:00 AM)
 * @param newMathViewer cbit.vcell.client.desktop.biomodel.MathViewer
 */
private void setSurfaceViewer(SurfaceViewerPanel newSurfaceViewer) {
	surfaceViewer = newSurfaceViewer;
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 4:40:40 PM)
 * @param newMathViewerFrame cbit.gui.JInternalFrameEnhanced
 */
private void setSurfaceViewerFrame(org.vcell.util.gui.JInternalFrameEnhanced newSurfaceViewerFrame) {
	surfaceViewerFrame = newSurfaceViewerFrame;
}

public void preloadSimulationStatus(Simulation[] simulations) {
	ClientSimManager clientSimManager = getAppEditor().getSimulationWorkspace().getClientSimManager();
	if (clientSimManager != null) {
		clientSimManager.preloadSimulationStatus(simulations);
	}
}
}
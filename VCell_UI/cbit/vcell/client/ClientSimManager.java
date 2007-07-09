package cbit.vcell.client;
import cbit.vcell.client.server.*;
import cbit.vcell.solvers.SimulationStatus;
import cbit.vcell.simulation.*;

import cbit.vcell.client.data.*;
import javax.swing.*;

import org.vcell.util.*;
import org.vcell.util.gui.AsynchProgressPopup;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.SwingWorker;

import java.awt.Dimension;
import java.beans.*;
import cbit.vcell.client.desktop.simulation.*;

import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (6/7/2004 10:31:36 AM)
 * @author: Ion Moraru
 */
public class ClientSimManager implements java.beans.PropertyChangeListener {
	private DocumentWindowManager documentWindowManager = null;
	private SimulationWorkspace simWorkspace = null;
	private SimulationStatusHash simHash = new SimulationStatusHash();

/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 10:48:50 AM)
 * @param documentWindowManager cbit.vcell.client.DocumentWindowManager
 * @param simulationOwner cbit.vcell.document.SimulationOwner
 */
public ClientSimManager(DocumentWindowManager documentWindowManager, SimulationWorkspace simWorkspace) {
	this.documentWindowManager = documentWindowManager;
	this.simWorkspace = simWorkspace;
	getSimWorkspace().addPropertyChangeListener(this);
	initHash(getSimWorkspace().getSimulations());
}


/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 12:50:45 PM)
 * @return cbit.vcell.client.DocumentWindowManager
 */
DocumentWindowManager getDocumentWindowManager() {
	return documentWindowManager;
}


/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 10:31:36 AM)
 * @return cbit.vcell.solver.ode.gui.SimulationStatus
 * @param simulation cbit.vcell.solver.Simulation
 */
public SimulationStatus getSimulationStatus(cbit.vcell.simulation.Simulation simulation) {
	SimulationStatus cachedSimStatus = simHash.getSimulationStatus(simulation);
	if (cachedSimStatus!=null){
		if (simulation.getIsDirty()) {
			return SimulationStatus.newNeverRan(simulation.getScanCount());
		} else {
			return cachedSimStatus;
		}
	} else {
		// shouldn't really happen
		try {
			throw new RuntimeException("shouldn't really happen");
		} catch (Throwable e) {
			e.printStackTrace(System.out);
		}
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 1:17:36 PM)
 * @return cbit.vcell.client.desktop.simulation.SimulationWorkspace
 */
public cbit.vcell.client.desktop.simulation.SimulationWorkspace getSimWorkspace() {
	return simWorkspace;
}


/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 12:55:18 PM)
 * @param simulations cbit.vcell.solver.Simulation[]
 */
private void initHash(Simulation[] simulations) {
	simHash.changeSimulationInstances(simulations);
	if (simulations != null) {
		for (int i = 0; i < simulations.length; i++){
			SimulationStatus simStatus = simHash.getSimulationStatus(simulations[i]);
			if (simStatus==null || simStatus.isUnknown()){
				// try to get status from server
				simStatus = getDocumentWindowManager().getRequestManager().getServerSimulationStatus(simulations[i].getSimulationInfo());
				if (simStatus != null) {
					simHash.setSimulationStatus(simulations[i], simStatus);
				} else {
					simHash.setSimulationStatus(simulations[i], SimulationStatus.newNeverRan(simulations[i].getScanCount()));
				}
			}
		}
	}
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getPropertyName().equals("simulations")) {
		simHash.changeSimulationInstances((Simulation[])evt.getNewValue());
	}
	if (evt.getPropertyName().equals("simulationOwner")) {
		initHash(((SimulationOwner)evt.getNewValue()).getSimulations());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 3:01:29 AM)
 * @param simulation cbit.vcell.solver.Simulation[]
 */
public void runSimulations(Simulation[] simulations) {
	getDocumentWindowManager().getRequestManager().runSimulations(this, simulations);
}

/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 10:31:36 AM)
 * @param simulations cbit.vcell.solver.Simulation[]
 */
public void showSimulationStatusDetails(Simulation[] simulations) {
	if (simulations != null) {
		//Vector v = new Vector();
		//for (int i = 0; i < simulations.length; i++){
			//if (simulations[i].getSimulationInfo() != null) {
				//v.add(simulations[i]);
			//}
		//}
		final Simulation[] simsToShow = simulations; //(Simulation[])cbit.util.BeanUtils.getArray(v, Simulation.class);
		for (int i = 0; i < simsToShow.length; i ++) {
			SimulationStatusDetailsPanel ssdp = new SimulationStatusDetailsPanel();
			ssdp.setPreferredSize(new Dimension(800, 350));
			ssdp.setSimulationStatusDetails(new SimulationStatusDetails(getSimWorkspace(), simsToShow[i]));
			DialogUtils.showComponentCloseDialog(null, ssdp, "Simulation Status Details");			
			ssdp.setSimulationStatusDetails(null);
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 10:31:36 AM)
 * @param simulations cbit.vcell.solver.Simulation[]
 */
public void showSimulationResults(Simulation[] simulations) {
	if (simulations != null) {
		Vector<Simulation> v = new Vector<Simulation>();
		for (int i = 0; i < simulations.length; i++){
			if (simulations[i].getSimulationInfo() != null && getSimulationStatus(simulations[i]).getHasData()) {
				v.add(simulations[i]);
			}
		}
		final Simulation[] simsToShow = (Simulation[])BeanUtils.getArray(v, Simulation.class);
		SwingWorker worker = new SwingWorker() {
			AsynchProgressPopup pp = new AsynchProgressPopup(getDocumentWindowManager().getComponent(), "Show simulation data:", "Preparing to fetch...", false, false);
			Hashtable<Simulation,Exception> failures = new Hashtable<Simulation,Exception>();
			Hashtable<VCSimulationIdentifier,SimulationWindow> viewers = new Hashtable<VCSimulationIdentifier,SimulationWindow>();
			public Object construct() {
				pp.start();
				for (int i = 0; i < simsToShow.length; i++){
					pp.setMessage("Retrieveing data for simulation '"+simsToShow[i].getName()+"'");
					pp.setProgress(100 * i / simsToShow.length);
					try {
						VCSimulationIdentifier vcSimulationIdentifier = simsToShow[i].getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
						SimulationWindow simWindow = getDocumentWindowManager().haveSimulationWindow(vcSimulationIdentifier);
						if (simWindow != null) {
							// just show it right now...
							final JInternalFrame existingFrame = simWindow.getFrame();
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									getDocumentWindowManager().showFrame(existingFrame);
								}
							});
						} else {
							// get the data manager and wire it up
							DynamicDataManager dataManager = getDocumentWindowManager().getRequestManager().getDynamicDataManager(simsToShow[i]);
							getDocumentWindowManager().addDataListener(dataManager);//For changes in time or variable
							// make the viewer and wire it up
							boolean expectODEdata = simsToShow[i].getMathDescription().getGeometry().getDimension() == 0;
							DataViewer viewer = dataManager.createViewer(expectODEdata);
							viewer.setSimulationModelInfo(new SimulationWorkspaceModelInfo(getSimWorkspace().getSimulationOwner(),simsToShow[i].getName()));
							viewer.setDataViewerManager(getDocumentWindowManager());
							getDocumentWindowManager().addExportListener(viewer);
							getDocumentWindowManager().addDataJobListener(viewer);//For data related activities such as calculating statistics
							simWindow = new SimulationWindow(vcSimulationIdentifier, simsToShow[i], getSimWorkspace().getSimulationOwner(), viewer);
							viewers.put(vcSimulationIdentifier, simWindow);
						}
					} catch (Exception exc) {
						exc.printStackTrace(System.out);
						failures.put(simsToShow[i], exc);
					}
				}
				return failures;
			}
			public void finished() {
				// send it to the manager to show it
				if (! viewers.isEmpty()) {
					Enumeration en = viewers.keys();
					while (en.hasMoreElements()) {
						VCSimulationIdentifier vcSimulationIdentifier = (VCSimulationIdentifier)en.nextElement();
						getDocumentWindowManager().addResultsFrame((SimulationWindow)viewers.get(vcSimulationIdentifier));
					}
				}
				pp.stop();
				// notify of errrors
				if (! failures.isEmpty()) {
					Enumeration en = failures.keys();
					while (en.hasMoreElements()) {
						Simulation sim = (Simulation)en.nextElement();
						Throwable exc = (Throwable)failures.get(sim);
						// notify user
						PopupGenerator.showErrorDialog(ClientSimManager.this.getDocumentWindowManager(), "Failed to retrieve results for simulation '"+sim.getName()+"'\n"+exc.getMessage());
					}
				}
			}
		};
		worker.start();	
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 3:01:29 AM)
 * @param simulations cbit.vcell.solver.Simulation[]
 */
public void stopSimulations(Simulation[] simulations) {
	getDocumentWindowManager().getRequestManager().stopSimulations(this, simulations);
}


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 3:04:12 PM)
 */
void updateStatusFromServer(Simulation simulation) {
	// 
	// get cached status
	//
	SimulationStatus oldStatus = getSimulationStatus(simulation);
	SimulationStatus serverStatus = getDocumentWindowManager().getRequestManager().getServerSimulationStatus(simulation.getSimulationInfo());

	SimulationStatus newStatus = null;
	if (oldStatus.isStopRequested() && serverStatus.numberOfJobsDone() < simulation.getScanCount()) {
		// if stop requested but still going, get updated server info but adjust status
		newStatus = SimulationStatus.newStopRequest(serverStatus);
	} else {
		// otherwise accept server information
		newStatus = serverStatus;
	}

	// update cache
	simHash.setSimulationStatus(simulation,newStatus);
	
	System.out.println("---ClientSimManager.updateStatusFromServer[newStatus=" + newStatus + "], simulation="+simulation.toString());
	if (oldStatus!=newStatus){
		int simIndex = getSimWorkspace().getSimulationIndex(simulation);
		getSimWorkspace().firePropertyChange(new PropertyChangeEvent(getSimWorkspace(), "status", new Integer(-1), new Integer(simIndex)));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 3:01:29 AM)
 * @param simulations cbit.vcell.solver.Simulation[]
 */
public void updateStatusFromStartRequest(final Simulation simulation, boolean failed, String failureMessage) {
	// asynchronous call - from start request worker thread
	SimulationStatus newStatus = failed ? SimulationStatus.newStartRequestFailure(failureMessage, simulation.getScanCount()) : SimulationStatus.newStartRequest(simulation.getScanCount());
	simHash.setSimulationStatus(simulation,newStatus);
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			int simIndex = getSimWorkspace().getSimulationIndex(simulation);
			getSimWorkspace().firePropertyChange(new PropertyChangeEvent(getSimWorkspace(), "status", new Integer(-1), new Integer(simIndex)));
		}
	});
}


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 3:01:29 AM)
 * @param simulations cbit.vcell.solver.Simulation[]
 */
public void updateStatusFromStopRequest(final Simulation simulation) {
	// asynchronous call - from stop request worker thread
	SimulationStatus currentStatus = getSimulationStatus(simulation);
	SimulationStatus newStatus = SimulationStatus.newStopRequest(currentStatus);
	simHash.setSimulationStatus(simulation,newStatus);
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			int simIndex = getSimWorkspace().getSimulationIndex(simulation);
			getSimWorkspace().firePropertyChange(new PropertyChangeEvent(getSimWorkspace(), "status", new Integer(-1), new Integer(simIndex)));
		}
	});
}
}
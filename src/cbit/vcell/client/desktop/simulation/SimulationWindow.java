package cbit.vcell.client.desktop.simulation;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.vcell.util.gui.JInternalFrameEnhanced;

import cbit.vcell.client.data.DataViewer;
import cbit.vcell.document.SimulationOwner;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationIdentifier;

public class SimulationWindow {
	private VCSimulationIdentifier vcSimulationIdentifier = null;
	private javax.swing.JInternalFrame frame = null;
	private Simulation simulation = null;
	private SimulationOwner simOwner = null;
	private transient PropertyChangeListener pcl = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt){
			if (evt.getSource() == getSimulation() && evt.getPropertyName().equals("name")){
				String newName = (String)evt.getNewValue();
				getFrame().setTitle("SIMULATION: " + newName);
			}
		}
	};

/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 11:38:49 AM)
 * @param vcDataIdentifier cbit.vcell.server.VCDataIdentifier
 * @param simulation cbit.vcell.solver.Simulation
 * @param simOwner cbit.vcell.document.SimulationOwner
 * @param dataViewer cbit.vcell.client.data.DataViewer
 */
public SimulationWindow(VCSimulationIdentifier vcSimulationIdentifier, Simulation simulation, SimulationOwner simOwner, DataViewer dataViewer) {
	setVcSimulationIdentifier(vcSimulationIdentifier);
	setSimulation(simulation);
	setSimOwner(simOwner);
	initialize(dataViewer);
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2004 5:16:51 PM)
 * @return javax.swing.JInternalFrame
 */
public javax.swing.JInternalFrame getFrame() {
	return frame;
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2004 5:16:51 PM)
 * @return cbit.vcell.document.SimulationOwner
 */
public SimulationOwner getSimOwner() {
	return simOwner;
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2004 5:16:51 PM)
 * @return cbit.vcell.solver.Simulation
 */
public Simulation getSimulation() {
	return simulation;
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2004 5:16:51 PM)
 * @return cbit.vcell.server.VCSimulationIdentifier
 */
public VCSimulationIdentifier getVcSimulationIdentifier() {
	return vcSimulationIdentifier;
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 11:50:03 AM)
 * @param dataViewer cbit.vcell.client.data.DataViewer
 */
private void initialize(DataViewer dataViewer) {
	// create frame
	setFrame(new JInternalFrameEnhanced("SIMULATION: " + getSimulation().getName(), true, true, true, true));
	getFrame().setContentPane(dataViewer);
	getFrame().setSize(850, 650);
	getSimulation().addPropertyChangeListener(pcl);
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2004 5:16:51 PM)
 * @param newSimulation cbit.vcell.solver.Simulation
 */
public void resetSimulation(Simulation newSimulation) {
	if (getSimulation() != null) {
		getSimulation().removePropertyChangeListener(pcl);
	}
	setSimulation(newSimulation);
	if (getSimulation() != null) {
		getSimulation().addPropertyChangeListener(pcl);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2004 5:16:51 PM)
 * @param newFrame javax.swing.JInternalFrame
 */
private void setFrame(javax.swing.JInternalFrame newFrame) {
	frame = newFrame;
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2004 5:16:51 PM)
 * @param newSimOwner cbit.vcell.document.SimulationOwner
 */
private void setSimOwner(SimulationOwner newSimOwner) {
	simOwner = newSimOwner;
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2004 5:16:51 PM)
 * @param newSimulation cbit.vcell.solver.Simulation
 */
private void setSimulation(Simulation newSimulation) {
	simulation = newSimulation;
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2004 5:16:51 PM)
 * @param newVcSimulationIdentifier cbit.vcell.server.VCSimulationIdentifier
 */
private void setVcSimulationIdentifier(VCSimulationIdentifier newVcSimulationIdentifier) {
	vcSimulationIdentifier = newVcSimulationIdentifier;
}
}
package cbit.vcell.client.data;
import cbit.util.DataAccessException;
import cbit.vcell.simulation.Simulation;
import cbit.vcell.simulation.VCSimulationIdentifier;
import cbit.vcell.client.server.VCDataManager;
import cbit.vcell.solvers.VCSimulationDataIdentifier;
/**
 * Insert the type's description here.
 * Creation date: (10/16/2005 2:42:43 PM)
 * @author: Ion Moraru
 */
public class SimulationDataManager implements DynamicDataManager {
	private VCDataManager vcDataManager = null;
	private Simulation simulation = null;
	private VCSimulationIdentifier vcSimulationIdentifier = null;
	private SimDataViewer simDataViewer = null;

/**
 * Insert the method's description here.
 * Creation date: (10/16/2005 3:00:47 PM)
 * @param vcDataManager cbit.vcell.client.server.VCDataManager
 * @param vcSimulationIdentifier cbit.vcell.solver.VCSimulationIdentifier
 */
public SimulationDataManager(VCDataManager vcDataManager, Simulation simulation) {
	this.vcDataManager = vcDataManager;
	this.simulation = simulation;
	this.vcSimulationIdentifier = simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/2005 2:42:43 PM)
 * @return javax.swing.JPanel
 */
public cbit.vcell.client.data.DataViewer createViewer(boolean expectODEData) throws cbit.util.DataAccessException {
	simDataViewer = new SimDataViewer(simulation, vcDataManager, expectODEData);
	return simDataViewer;
}


/**
 * 
 * @param event cbit.vcell.desktop.controls.SimulationEvent
 */
public void newData(cbit.vcell.desktop.controls.DataEvent event) {
	if (event.getVcDataIdentifier().equals(new VCSimulationDataIdentifier(vcSimulationIdentifier, 0))) {
		try {
			refreshData();
		} catch (DataAccessException exc) {
			exc.printStackTrace(System.out);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 2:43:49 PM)
 * @exception cbit.util.DataAccessException The exception description.
 */
public void refreshData() throws cbit.util.DataAccessException {
	simDataViewer.refreshData();
}
}
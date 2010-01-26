package cbit.vcell.client.server;

import org.vcell.util.DataAccessException;

import cbit.vcell.client.data.DataViewer;
import cbit.vcell.client.data.SimResultsViewer;
import cbit.vcell.desktop.controls.DataEvent;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
/**
 * Insert the type's description here.
 * Creation date: (10/16/2005 2:42:43 PM)
 * @author: Ion Moraru
 */
public class SimResultsViewerController implements DataViewerController {
	private DataManager dataManager = null;
	private Simulation simulation = null;
	private VCSimulationIdentifier vcSimulationIdentifier = null;
	private SimResultsViewer simResultsViewer = null;

/**
 * Insert the method's description here.
 * Creation date: (10/16/2005 3:00:47 PM)
 * @param vcDataManager cbit.vcell.client.server.VCDataManager
 * @param vcSimulationIdentifier cbit.vcell.solver.VCSimulationIdentifier
 */
public SimResultsViewerController(DataManager arg_dataManager, Simulation simulation) {
	this.dataManager = arg_dataManager;
	this.simulation = simulation;
	this.vcSimulationIdentifier = simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
}

/**
 * Insert the method's description here.
 * Creation date: (10/16/2005 2:42:43 PM)
 * @return javax.swing.JPanel
 */
public DataViewer createViewer() throws DataAccessException {
	try{
		simResultsViewer = new SimResultsViewer(simulation, dataManager);
		return simResultsViewer;
	}catch(Exception e){
		if(e instanceof DataAccessException){
			throw (DataAccessException)e;
		}
		throw new DataAccessException(e.getMessage(),e);
	}

}


/**
 * 
 * @param event cbit.vcell.desktop.controls.SimulationEvent
 */
public void newData(DataEvent event) {
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
 * @exception org.vcell.util.DataAccessException The exception description.
 */
public void refreshData() throws DataAccessException {
	if (simResultsViewer != null) {
		simResultsViewer.refreshData();
	}
}
}
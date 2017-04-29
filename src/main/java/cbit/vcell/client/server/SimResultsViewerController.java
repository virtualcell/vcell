/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.server;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import org.vcell.util.DataAccessException;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.data.DataViewer;
import cbit.vcell.client.data.DataViewerController;
import cbit.vcell.client.data.SimResultsViewer;
import cbit.vcell.simdata.DataEvent;
import cbit.vcell.simdata.DataManager;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.solver.AnnotatedFunction;
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


public void propertyChange(PropertyChangeEvent evt) {
	// update functions
	if (simResultsViewer != null && evt.getPropertyName().equals("outputFunctions")){
		try {
			ArrayList<AnnotatedFunction> outputFunctionsList = (ArrayList<AnnotatedFunction>)evt.getNewValue();
			dataManager.setOutputContext(new OutputContext(outputFunctionsList.toArray(new AnnotatedFunction[outputFunctionsList.size()])));
			simResultsViewer.refreshFunctions();
		} catch (Exception e) {
			e.printStackTrace();
			DialogUtils.showErrorDialog(simResultsViewer, "Failed to update viewer after function change: "+e.getMessage(), e);
		}
	}
}
}

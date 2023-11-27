/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;
import java.awt.Component;
import java.util.*;
import java.util.Map.Entry;

import org.vcell.util.BeanUtils;

import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.ClientSimManager;
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
	private Hashtable<VCSimulationIdentifier, SimulationWindow> simulationWindowsHash = new Hashtable<VCSimulationIdentifier, SimulationWindow>();
	private SimulationWorkspace simulationWorkspace = null;

/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 4:41:32 PM)
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
public ApplicationComponents(SimulationContext simContext, BioModelWindowManager bioModelWindowManager) {
	// make the surface viewer
	simulationWorkspace = new SimulationWorkspace(bioModelWindowManager, simContext);
}


/**
 * @param simWindow non  null
 */
public void addDataViewer(SimulationWindow simWindow) {
	Objects.requireNonNull(simWindow);
	simulationWindowsHash.put(simWindow.getVcSimulationIdentifier(), simWindow);
}

public SimulationWorkspace getSimulationWorkspace() {
	return simulationWorkspace;
}

public void resetSimulationContext(SimulationContext simContext) {
	getSimulationWorkspace().setSimulationOwner(simContext);	
}

/**
 * Remove server side sim results windows that are no longer in use 
 */
public void cleanSimWindowsHash() {

	Iterator<Entry<VCSimulationIdentifier, SimulationWindow>> iter = simulationWindowsHash.entrySet().iterator();
	while (iter.hasNext()) {
		Entry<VCSimulationIdentifier, SimulationWindow> es = iter.next();
		SimulationWindow sw = es.getValue();
		if (!sw.isShowingLocalSimulation()) {
			VCSimulationIdentifier vcsid = es.getKey(); 
			Simulation[] sims = simulationWorkspace.getSimulations();
			boolean bFound = false;
            for (Simulation sim : sims) {
				if (sim.getSimulationInfo() == null || !sim.getSimulationInfo().getAuthoritativeVCSimulationIdentifier().equals(vcsid)) continue;
				bFound = true;
				break;
            }
			if(!bFound){
				iter.remove();
			}
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 4:40:40 PM)
 */
public ChildWindow[] getDataViewerFrames(Component component) {
	SimulationWindow[] simWindows = Collections.list(simulationWindowsHash.elements()).toArray(SimulationWindow[]::new);
	List<ChildWindow> childWindows = new ArrayList<>();
	ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(component);
    for (SimulationWindow simWindow : simWindows) {
        ChildWindow childWindow = childWindowManager.getChildWindowFromContext(simWindow);
        if (childWindow != null) {
            childWindows.add(childWindow);
        }
    }
	return childWindows.toArray(ChildWindow[]::new);
}

/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 4:40:40 PM)
 */
public SimulationWindow[] getSimulationWindows() {
	return Collections.list(simulationWindowsHash.elements()).toArray(SimulationWindow[]::new);
}

/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 7:55:48 AM)
 * @return boolean
 * @param vcSimulationIdentifier cbit.vcell.server.VCSimulationIdentifier
 */
public SimulationWindow haveSimulationWindow(VCSimulationIdentifier vcSimulationIdentifier) {
	return simulationWindowsHash.get(vcSimulationIdentifier);
}

public void preloadSimulationStatus(Simulation[] simulations) {
	ClientSimManager clientSimManager = simulationWorkspace.getClientSimManager();
	if (clientSimManager != null) {
		clientSimManager.preloadSimulationStatus(simulations);
	}
}
}

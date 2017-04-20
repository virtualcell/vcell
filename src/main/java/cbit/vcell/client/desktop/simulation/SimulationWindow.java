/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.simulation;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.data.DataViewer;
import cbit.vcell.client.title.TitleChanger;
import cbit.vcell.client.title.TitleEvent;
import cbit.vcell.client.title.TitleListener;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.VCSimulationIdentifier;

public class SimulationWindow implements TitleChanger {
	private VCSimulationIdentifier vcSimulationIdentifier = null;
	private Simulation simulation = null;
	private SimulationOwner simOwner = null;
	private ChildWindow childWindow = null;
	private DataViewer dataViewer = null;
	public enum LocalState {
		SERVER,
		LOCAL,
		LOCAL_SIMMODFIED
	}
	private LocalState localState; 
	private List<TitleListener> titleListeners;
	
	private transient PropertyChangeListener pcl = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt){
			if (evt.getSource() == getSimulation() && evt.getPropertyName().equals("name")){
				String newName = (String)evt.getNewValue();
				getChildWindow().setTitle("Simulation: " + newName);
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
	getSimulation().addPropertyChangeListener(pcl);
	this.dataViewer = dataViewer;
	localState = LocalState.SERVER; 
	titleListeners = new LinkedList<>();
}

public LocalState getLocalState() {
	return localState;
}

public void setLocalState(LocalState localState) {
	if (this.localState != localState) {
		String  old = getTitle();
		this.localState = localState;
		//did this change title?
		String now = getTitle();
		if (!now.equals(old)) {
			TitleEvent te = new TitleEvent(now);
			for (TitleListener tl : titleListeners) {
				tl.titleChanged(te);
			}
		}
	}
}

@Override
public void addTitleListener(TitleListener listener) {
	titleListeners.add(listener);
}

@Override
public void removeTitleListener(TitleListener listener) {
	titleListeners.remove(listener);
}

private ChildWindow getChildWindow() {
	return childWindow;
}

public void setChildWindow(ChildWindow childWindow) {
	this.childWindow = childWindow;
}

public DataViewer getDataViewer(){
	return dataViewer;
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

public String getTitle() {
	String b =  "Results for Simulation "+simulation.getName();
	switch (localState) {
	case SERVER:
	case LOCAL:
		return b; 
	case LOCAL_SIMMODFIED:
		return b + " *";
	}
	return null; //keep compiler happy
}

public boolean isShowingLocalSimulation() {
	switch (localState) {
	case SERVER:
		return false;
	case LOCAL:
	case LOCAL_SIMMODFIED:
		return true;
	}
	return false; //keep compiler happy
}

}

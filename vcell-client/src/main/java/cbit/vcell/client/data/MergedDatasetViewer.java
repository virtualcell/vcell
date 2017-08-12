/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.data;

import org.vcell.util.DataAccessException;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.export.gui.ExportMonitorPanel;
import cbit.vcell.simdata.DataManager;
import cbit.vcell.simdata.ODEDataManager;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.solver.ode.ODESolverResultSet;
/**
 * Insert the type's description here.
 * Creation date: (10/17/2005 11:22:58 PM)
 * @author: Ion Moraru
 */
public class MergedDatasetViewer extends DataViewer {
	private DataManager dataManager = null;
	private DataViewer mainViewer = null;
	private boolean isODEData;
	private ODEDataViewer odeDataViewer = null;
	private PDEDataViewer pdeDataViewer = null;

/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 11:30:45 PM)
 * @param simulation cbit.vcell.solver.Simulation
 * @param vcDataManager cbit.vcell.client.server.VCDataManager
 */
public MergedDatasetViewer(DataManager argDataManager) throws DataAccessException {
	super();
	setDataManager(argDataManager);
	this.isODEData = argDataManager instanceof ODEDataManager;
	initialize();
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 2:33:44 PM)
 * @return javax.swing.JPanel
 */
private DataViewer createDataViewer() {
	try {
		if (isODEData) {
			ODEDataManager odeDataManager = (ODEDataManager)dataManager;
			odeDataViewer = new ODEDataViewer();
			odeDataViewer.setOdeSolverResultSet(odeDataManager.getODESolverResultSet());
			odeDataViewer.setVcDataIdentifier(dataManager.getVCDataIdentifier());
			return odeDataViewer;
		} else {
			PDEDataManager pdeDataManager = (PDEDataManager)dataManager;
			pdeDataViewer = new PDEDataViewer();
			pdeDataViewer.setPdeDataContext(pdeDataManager.getPDEDataContext());
			return pdeDataViewer;
		}
	} catch (org.vcell.util.DataAccessException exc) {
		DialogUtils.showErrorDialog(this, "Could not fetch requested data.\nJCompare may have failed.\n" + exc.getMessage());
		exc.printStackTrace();
	}
	return null;
}


/**
 * Method generated to support the promotion of the exportMonitorPanel attribute.
 * @return cbit.vcell.export.ExportMonitorPanel
 */
public ExportMonitorPanel getExportMonitorPanel() {
	return getMainViewer().getExportMonitorPanel();
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 11:36:17 PM)
 * @return cbit.vcell.client.data.DataViewer
 */
private DataViewer getMainViewer() {
	return mainViewer;
}

/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 11:37:52 PM)
 * @exception org.vcell.util.DataAccessException The exception description.
 */
private void initialize() throws org.vcell.util.DataAccessException {
	
	// create main viewer and wire it up
	setMainViewer(createDataViewer());
	java.beans.PropertyChangeListener pcl = new java.beans.PropertyChangeListener() {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == MergedDatasetViewer.this && (evt.getPropertyName().equals("dataViewerManager"))) {
				try {
					getMainViewer().setDataViewerManager(getDataViewerManager());
				} catch (java.beans.PropertyVetoException exc) {
					exc.printStackTrace();
				}
			}
		}
	};
	addPropertyChangeListener(pcl);
		
	// put things together
	setLayout(new java.awt.BorderLayout());
	add(getMainViewer(), java.awt.BorderLayout.CENTER);
}

/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 2:43:49 PM)
 * @exception org.vcell.util.DataAccessException The exception description.
 */
public void refreshData() throws org.vcell.util.DataAccessException {
	if (isODEData) {
		ODESolverResultSet osr = null;
		osr = odeDataViewer.getOdeSolverResultSet();
		if (osr != null) {
			odeDataViewer.setOdeSolverResultSet(osr);
		}
	} else {
		pdeDataViewer.getPdeDataContext().refreshTimes();
	}
}
public void refreshFunctions() throws DataAccessException {
	if (isODEData) {
		refreshData();
	} else {
		pdeDataViewer.getPdeDataContext().refreshIdentifiers();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 11:36:17 PM)
 * @param newMainViewer cbit.vcell.client.data.DataViewer
 */
private void setMainViewer(DataViewer newMainViewer) {
	mainViewer = newMainViewer;
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 11:36:17 PM)
 * @param newVcDataManager cbit.vcell.client.server.VCDataManager
 */
private void setDataManager(DataManager newDataManager) {
	dataManager = newDataManager;
}
}

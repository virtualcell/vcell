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

import cbit.vcell.simdata.DataEvent;
import cbit.vcell.simdata.DataManager;
import cbit.vcell.simdata.MergedDataInfo;
import cbit.vcell.simdata.ODEDataManager;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.client.data.DataViewer;
import cbit.vcell.client.data.MergedDatasetViewer;
/**
 * Insert the type's description here.
 * Creation date: (11/30/2005 5:26:31 PM)
 * @author: Anuradha Lakshminarayana
 */
public class MergedDatasetViewerController implements DataViewerController {
	private DataManager dataManager = null;
	private MergedDatasetViewer mergedDatasetViewer = null;
	private boolean expectODEData;

/**
 * MergedDynamicDataManager constructor comment.
 */
public MergedDatasetViewerController(DataManager argDataManager) {
	super();
	this.dataManager = argDataManager;
	this.expectODEData = dataManager instanceof ODEDataManager;
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/2005 2:42:43 PM)
 * @return javax.swing.JPanel
 */
public DataViewer createViewer() throws DataAccessException {
	mergedDatasetViewer = new MergedDatasetViewer(dataManager);
	return mergedDatasetViewer;
}


/**
 * 
 * @param event cbit.vcell.desktop.controls.SimulationEvent
 */
public void newData(DataEvent event) {
	if (event.getVcDataIdentifier() instanceof MergedDataInfo) {
		try {
			refreshData();
		} catch (DataAccessException exc) {
			exc.printStackTrace(System.out);
		}
	}
}

public void propertyChange(PropertyChangeEvent evt) {
	// update functions
	if (mergedDatasetViewer != null && evt.getPropertyName().equals("outputFunctions")){
		try {
			ArrayList<AnnotatedFunction> outputFunctionsList = (ArrayList<AnnotatedFunction>)evt.getNewValue();
			dataManager.setOutputContext(new OutputContext(outputFunctionsList.toArray(new AnnotatedFunction[outputFunctionsList.size()])));
			mergedDatasetViewer.refreshFunctions();
		} catch (Exception e) {
			e.printStackTrace();
			DialogUtils.showErrorDialog(mergedDatasetViewer, "Failed to update viewer after function change: "+e.getMessage(), e);
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 2:43:49 PM)
 * @exception org.vcell.util.DataAccessException The exception description.
 */
public void refreshData() throws DataAccessException {
	mergedDatasetViewer.refreshData();
}

}

package cbit.vcell.client.server;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.simdata.MergedDataInfo;
import cbit.vcell.client.data.DataViewer;
import cbit.vcell.client.data.MergedDatasetViewer;
import cbit.vcell.client.data.OutputContext;
import cbit.vcell.desktop.controls.DataEvent;
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
			DialogUtils.showErrorDialog(mergedDatasetViewer, "Failed to update viewer after function change: "+e.getMessage());
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
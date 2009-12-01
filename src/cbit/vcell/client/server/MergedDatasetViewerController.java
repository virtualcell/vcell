package cbit.vcell.client.server;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.simdata.MergedDataInfo;
import cbit.vcell.client.data.DataViewer;
import cbit.vcell.client.data.MergedDatasetViewer;
import cbit.vcell.desktop.controls.DataEvent;
/**
 * Insert the type's description here.
 * Creation date: (11/30/2005 5:26:31 PM)
 * @author: Anuradha Lakshminarayana
 */
public class MergedDatasetViewerController implements DataViewerController {
	private VCDataManager vcDataManager = null;
	private VCDataIdentifier mergedDataIdentifier = null;
	private MergedDatasetViewer mergedDatasetViewer = null;
	private boolean expectODEData;

/**
 * MergedDynamicDataManager constructor comment.
 */
public MergedDatasetViewerController(VCDataManager argVCDataManager, VCDataIdentifier argMergedDataID, boolean argExpectODEData) {
	super();
	vcDataManager = argVCDataManager;
	mergedDataIdentifier = argMergedDataID;
	expectODEData = argExpectODEData;
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/2005 2:42:43 PM)
 * @return javax.swing.JPanel
 */
public DataViewer createViewer() throws DataAccessException {
	mergedDatasetViewer = new MergedDatasetViewer(vcDataManager, mergedDataIdentifier, expectODEData);
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


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 2:43:49 PM)
 * @exception org.vcell.util.DataAccessException The exception description.
 */
public void refreshData() throws DataAccessException {
	mergedDatasetViewer.refreshData();
}

}
package cbit.vcell.client.server;

import org.vcell.util.DataAccessException;
import org.vcell.util.VCDataIdentifier;
import cbit.vcell.simdata.MergedDataInfo;
import cbit.vcell.client.data.DataViewer;
import cbit.vcell.client.data.MergedDataViewer;
import cbit.vcell.desktop.controls.DataEvent;
import cbit.vcell.desktop.controls.DataManager;
/**
 * Insert the type's description here.
 * Creation date: (11/30/2005 5:26:31 PM)
 * @author: Anuradha Lakshminarayana
 */
public class MergedDynamicDataManager implements DynamicDataManager {
	private VCDataManager vcDataManager = null;
	private VCDataIdentifier mergedDataIdentifier = null;
	private MergedDataViewer mergedDataViewer = null;

/**
 * MergedDynamicDataManager constructor comment.
 */
public MergedDynamicDataManager(VCDataManager argVCDataManager, VCDataIdentifier argMergedDataID) {
	super();
	vcDataManager = argVCDataManager;
	mergedDataIdentifier = argMergedDataID;
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/2005 2:42:43 PM)
 * @return javax.swing.JPanel
 */
public DataViewer createViewer(boolean expectODEData, DataManager dataManager) throws DataAccessException {
	mergedDataViewer = new MergedDataViewer(vcDataManager, mergedDataIdentifier, expectODEData, dataManager);
	return mergedDataViewer;
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
	mergedDataViewer.refreshData();
}

}
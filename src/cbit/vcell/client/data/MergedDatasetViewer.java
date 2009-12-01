package cbit.vcell.client.data;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.server.ODEDataManager;
import cbit.vcell.client.server.PDEDataManager;
import cbit.vcell.client.server.VCDataManager;
import cbit.vcell.export.ExportMonitorPanel;
import cbit.vcell.solver.ode.ODESolverResultSet;
/**
 * Insert the type's description here.
 * Creation date: (10/17/2005 11:22:58 PM)
 * @author: Ion Moraru
 */
public class MergedDatasetViewer extends DataViewer {
	private VCDataManager vcDataManager = null;
	private VCDataIdentifier vcDataId = null;
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
public MergedDatasetViewer(VCDataManager argVcDataManager, VCDataIdentifier argMergedDataID, boolean argIsODEData) throws DataAccessException {
	super();
	setVcDataId(argMergedDataID);
	setVcDataManager(argVcDataManager);
	this.isODEData = argIsODEData;
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
			ODEDataManager odeDataManager = new ODEDataManager(vcDataManager, vcDataId);
			odeDataViewer = new ODEDataViewer();
			odeDataViewer.setOdeSolverResultSet(odeDataManager.getODESolverResultSet());
			odeDataViewer.setVcDataIdentifier(vcDataId);
			return odeDataViewer;
		} else {
			PDEDataManager pdeDataManager = new PDEDataManager(vcDataManager, vcDataId);
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
private void setVcDataId(VCDataIdentifier newVcDataId) {
	vcDataId = newVcDataId;
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 11:36:17 PM)
 * @param newVcDataManager cbit.vcell.client.server.VCDataManager
 */
private void setVcDataManager(cbit.vcell.client.server.VCDataManager newVcDataManager) {
	vcDataManager = newVcDataManager;
}
}
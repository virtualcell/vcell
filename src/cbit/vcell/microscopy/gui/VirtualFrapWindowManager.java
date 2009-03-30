package cbit.vcell.microscopy.gui;

import java.awt.Component;
import java.io.FileNotFoundException;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.vcell.client.DataViewerManager;
import cbit.vcell.client.RequestManager;
import cbit.vcell.client.SimStatusEvent;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.desktop.controls.DataEvent;
import cbit.vcell.desktop.controls.DataListener;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.server.SessionLog;
import cbit.vcell.server.StdoutSessionLog;
import cbit.vcell.server.User;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.solver.VCSimulationDataIdentifier;



public class VirtualFrapWindowManager implements DataViewerManager {

	protected transient java.util.Vector aDataListener = null;
	protected transient java.util.Vector aExportListener = null;
	protected transient java.util.Vector aDataJobListener = null;
	private LocalWorkspace localWorkSpace = null;

	/**
	 * Method to support listener events.
	 */
	protected void fireDataJobMessage(cbit.rmi.event.DataJobEvent event) {
		if (aDataJobListener == null) {
			return;
		};
		int currentSize = aDataJobListener.size();
		cbit.rmi.event.DataJobListener tempListener = null;
		for (int index = 0; index < currentSize; index++){
			tempListener = (cbit.rmi.event.DataJobListener)aDataJobListener.elementAt(index);
			if (tempListener != null) {
				tempListener.dataJobMessage(event);
			};
		};
	}


	/**
	 * Method to support listener events.
	 */
	protected void fireExportMessage(ExportEvent event) {
		if (aExportListener == null) {
			return;
		};
		int currentSize = aExportListener.size();
		ExportListener tempListener = null;
		for (int index = 0; index < currentSize; index++){
			tempListener = (ExportListener)aExportListener.elementAt(index);
			if (tempListener != null) {
				tempListener.exportMessage(event);
			};
		};
	}


	/**
	 * Method to support listener events.
	 */
	protected void fireNewData(DataEvent event) {
		if (aDataListener == null) {
			return;
		};
		int currentSize = aDataListener.size();
		cbit.vcell.desktop.controls.DataListener tempListener = null;
		for (int index = 0; index < currentSize; index++){
			tempListener = (cbit.vcell.desktop.controls.DataListener)aDataListener.elementAt(index);
			if (tempListener != null) {
				tempListener.newData(event);
			};
		};
	}
	
	public void dataJobMessage(DataJobEvent event){
		// just pass them along...
		fireDataJobMessage(event);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (6/15/2004 2:55:34 AM)
	 * @param exportEvent cbit.rmi.event.ExportEvent
	 */
	public void exportMessage(ExportEvent exportEvent) {
		if(exportEvent.getVCDataIdentifier() instanceof VCSimulationDataIdentifier){
			VCSimulationDataIdentifier vcSimulationDataIdentifier = (VCSimulationDataIdentifier)(exportEvent.getVCDataIdentifier());
		}
		// just pass them along...
		fireExportMessage(exportEvent);
	}
	/**
	 * Add a cbit.vcell.desktop.controls.ExportListener.
	 */
	public void addDataJobListener(cbit.rmi.event.DataJobListener newListener) {
		if (aDataJobListener == null) {
			aDataJobListener = new java.util.Vector();
		};
		aDataJobListener.addElement(newListener);
	}


	/**
	 * Add a cbit.vcell.desktop.controls.DataListener.
	 */
	public void addDataListener(DataListener newListener) {
		if (aDataListener == null) {
			aDataListener = new java.util.Vector();
		};
		aDataListener.addElement(newListener);
	}


	/**
	 * Add a cbit.vcell.desktop.controls.ExportListener.
	 */
	public void addExportListener(ExportListener newListener) {
		if (aExportListener == null) {
			aExportListener = new java.util.Vector();
		};
		aExportListener.addElement(newListener);
	}
	public UserPreferences getUserPreferences(){
		return localWorkSpace.getUserPreferences();
	}
	public void removeDataListener(DataListener newListener){
	}
	public void showDataViewerPlotsFrames(javax.swing.JInternalFrame[] plotFrames){
//		for(int i=0;i<plotFrames.length;i+= 1){
//			plotFrames[i].setLocation(100,100);
//			DocumentWindowManager.showFrame(plotFrames[i], jdp);
//		}
	}
	public void startExport(ExportSpecs exportSpecs){
		try {
			SessionLog log = new StdoutSessionLog("export");
			ExportServiceImpl exportServiceImpl = new ExportServiceImpl(new StdoutSessionLog("export"));
			DataServerImpl dataServerImpl = new DataServerImpl(log,localWorkSpace.getDataSetControllerImpl(),exportServiceImpl);
			exportServiceImpl.addExportListener(new ExportListener() {
				public void exportMessage(ExportEvent event) {
					System.out.println(event.toString());
				}
			});
			exportServiceImpl.makeRemoteFile(LocalWorkspace.getDefaultOwner(), dataServerImpl, exportSpecs);
		}catch (DataAccessException e){
			e.printStackTrace(System.out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ExportEvent startExportMovie(ExportSpecs exportSpecs){
		ExportEvent exportEvt = null;
		try {
			SessionLog log = new StdoutSessionLog("export");
			ExportServiceImpl exportServiceImpl = new ExportServiceImpl(new StdoutSessionLog("export"));
			DataServerImpl dataServerImpl = new DataServerImpl(log,localWorkSpace.getDataSetControllerImpl(),exportServiceImpl);
			exportServiceImpl.addExportListener(new ExportListener() {
				public void exportMessage(ExportEvent event) {
					System.out.println(event.toString());
				}
			});
			//the last parameter denotes whether the saved file is comporessed or not.
			exportEvt = exportServiceImpl.makeRemoteFile(LocalWorkspace.getDefaultOwner(), dataServerImpl, exportSpecs, false);
		}catch (DataAccessException e){
			e.printStackTrace(System.out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return exportEvt;
	}
	
	public void simStatusChanged(SimStatusEvent simStatusEvent) {
	}
	public User getUser() {
		return LocalWorkspace.getDefaultOwner();
	}


	public LocalWorkspace getLocalWorkSpace() {
		return localWorkSpace;
	}


	public void setLocalWorkSpace(LocalWorkspace arg_localWorkSpace) {
		this.localWorkSpace = arg_localWorkSpace;
	}
	
}


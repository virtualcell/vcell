/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui;

import java.awt.Component;
import java.io.FileNotFoundException;
import java.util.Vector;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.vcell.client.DataViewerManager;
import cbit.vcell.client.RequestManager;
import cbit.vcell.client.server.SimStatusEvent;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.simdata.DataEvent;
import cbit.vcell.simdata.DataListener;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.OutputContext;



public class VirtualFrapWindowManager implements DataViewerManager {

	protected transient Vector<DataListener> aDataListener = null;
	protected transient Vector<ExportListener> aExportListener = null;
	protected transient Vector<DataJobListener> aDataJobListener = null;
	private LocalWorkspace localWorkSpace = null;
	/**
	 * Method to support listener events.
	 */
	protected void fireDataJobMessage(DataJobEvent event) {
		if (aDataJobListener == null) {
			return;
		};
		int currentSize = aDataJobListener.size();
		DataJobListener tempListener = null;
		for (int index = 0; index < currentSize; index++){
			tempListener = (DataJobListener)aDataJobListener.elementAt(index);
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
		DataListener tempListener = null;
		for (int index = 0; index < currentSize; index++){
			tempListener = (DataListener)aDataListener.elementAt(index);
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
		// just pass them along...
		fireExportMessage(exportEvent);
	}
	/**
	 * Add a cbit.vcell.desktop.controls.ExportListener.
	 */
	public void addDataJobListener(DataJobListener newListener) {
		if (aDataJobListener == null) {
			aDataJobListener = new Vector<DataJobListener>();
		};
		aDataJobListener.addElement(newListener);
	}


	/**
	 * Add a cbit.vcell.desktop.controls.DataListener.
	 */
	public void addDataListener(DataListener newListener) {
		if (aDataListener == null) {
			aDataListener = new Vector<DataListener>();
		};
		aDataListener.addElement(newListener);
	}


	/**
	 * Add a cbit.vcell.desktop.controls.ExportListener.
	 */
	public void addExportListener(ExportListener newListener) {
		if (aExportListener == null) {
			aExportListener = new Vector<ExportListener>();
		};
		aExportListener.addElement(newListener);
	}
	public UserPreferences getUserPreferences(){
		return localWorkSpace.getUserPreferences();
	}
	public void removeDataListener(DataListener newListener){
	}
	
	public void startExport(Component requester,OutputContext outContext, ExportSpecs exportSpecs){
		try {
			ExportServiceImpl exportServiceImpl = new ExportServiceImpl();
			DataServerImpl dataServerImpl = new DataServerImpl(localWorkSpace.getDataSetControllerImpl(),exportServiceImpl);
			exportServiceImpl.addExportListener(new ExportListener() {
				public void exportMessage(ExportEvent event) {
					System.out.println(event.toString());
				}
			});
			exportServiceImpl.makeRemoteFile(null, LocalWorkspace.getDefaultOwner(), dataServerImpl, exportSpecs);
		}catch (DataAccessException e){
			e.printStackTrace(System.out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ExportEvent startExportMovie(ExportSpecs exportSpecs, OutputContext outputContext, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception
	{
		ExportEvent exportEvt = null;
		
		ExportServiceImpl exportServiceImpl = new ExportServiceImpl();
		DataServerImpl dataServerImpl = new DataServerImpl(localWorkSpace.getDataSetControllerImpl(),exportServiceImpl);
		exportServiceImpl.addExportListener(new ExportListener() {
			public void exportMessage(ExportEvent event) {
				System.out.println(event.toString());
			}
		});
		//the last parameter denotes whether the saved file is comporessed or not.
		exportEvt = exportServiceImpl.makeRemoteFile(outputContext, LocalWorkspace.getDefaultOwner(), dataServerImpl, exportSpecs, false, clientTaskStatusSupport);
		
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

	//New method added in DataViewerManager that it has to be implemented here but VFRAP doesn't use it. 
	public RequestManager getRequestManager() {
		throw new RuntimeException("Not implemented in VFRAP");
	}
	
}

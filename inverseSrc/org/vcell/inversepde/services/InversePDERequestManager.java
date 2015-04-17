/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package org.vcell.inversepde.services;

import java.awt.Component;
import java.io.File;
import java.rmi.RemoteException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.server.DataSetControllerProvider;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.server.DataSetController;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.gui.SimulationStatusPersistent;
import cbit.vcell.solvers.CartesianMesh;

public class InversePDERequestManager {

	private ClientServerManager clientServerManager = null;
	private File workingDirectory = null;
	
	
	public File getSelectedOpenFile(Component parent, FileFilter fileFilter) throws UserCancelException {
		JFileChooser openFileChooser = new JFileChooser(); 
	    if (workingDirectory!=null){
	    	openFileChooser.setCurrentDirectory(workingDirectory);
	    }
	    openFileChooser.addChoosableFileFilter(fileFilter); 
	    int retcode = openFileChooser.showOpenDialog(parent);
	    if (retcode==JFileChooser.APPROVE_OPTION){
	    	workingDirectory = openFileChooser.getCurrentDirectory();
	    	return openFileChooser.getSelectedFile();
	    }else{
	    	throw UserCancelException.CANCEL_FILE_SELECTION;
	    }
	}

	public File getSelectedSaveFile(Component parent, FileFilter fileFilter, String title) throws UserCancelException {
		JFileChooser saveFileChooser = new JFileChooser(); 
	    if (workingDirectory!=null){
	    	saveFileChooser.setCurrentDirectory(workingDirectory);
	    }
	    saveFileChooser.addChoosableFileFilter(fileFilter); 
	    saveFileChooser.setDialogTitle("save referenceData as a Matlab file");
	    int retcode = saveFileChooser.showSaveDialog(parent);
	    if (retcode==JFileChooser.APPROVE_OPTION){
	    	workingDirectory = saveFileChooser.getCurrentDirectory();
	    	return saveFileChooser.getSelectedFile();
	    }else{
	    	throw UserCancelException.CANCEL_FILE_SELECTION;
	    }
	}
	
	public InversePDERequestManager(ClientServerManager arg_clientServerManager, File arg_workingDirectory) {
		this.clientServerManager = arg_clientServerManager;
		this.workingDirectory = arg_workingDirectory;
	}

	public DocumentManager getDocumentManager() {
		return clientServerManager.getDocumentManager();
	}

	public User getUser() {
		return clientServerManager.getUser();
	}

	public void startSimulation(VCSimulationIdentifier vcSimID, int numScanJobs) throws DataAccessException {
		clientServerManager.getJobManager().startSimulation(vcSimID, numScanJobs);
	}

	public SimulationStatusPersistent getSimulationStatus(KeyValue versionKey) throws ObjectNotFoundException, RemoteException, DataAccessException {
		return clientServerManager.getUserMetaDbServer().getSimulationStatus(versionKey);
	}

	public CartesianMesh getMesh(VCDataIdentifier vcdataID) throws RemoteException, DataAccessException {
		return clientServerManager.getDataSetController().getMesh(vcdataID);
	}

	public double[] getDataSetTimes(VCDataIdentifier vcdataID) throws RemoteException, DataAccessException {
		return clientServerManager.getDataSetController().getDataSetTimes(vcdataID);
	}

	public SimDataBlock getSimDataBlock(OutputContext outputContext, VCDataIdentifier vcdataID, String varName, double time) throws RemoteException, DataAccessException {
		return clientServerManager.getDataSetController().getSimDataBlock(outputContext, vcdataID, varName, time);
	}
	
	public DataIdentifier[] getDataIdentifiers(OutputContext outputContext,	VCDataIdentifier vcDataId) throws RemoteException, DataAccessException {
		return clientServerManager.getDataSetController().getDataIdentifiers(outputContext, vcDataId);
	}

	public DataSetControllerProvider getDataSetControllerProvider() {
		DataSetControllerProvider dataSetControllerProvider = new DataSetControllerProvider(){
			public DataSetController getDataSetController() throws DataAccessException {
				try {
					return clientServerManager.getDataSetController();
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}
			}
		};
		return dataSetControllerProvider;
	}

	public File getWorkingDirectory() {
		return workingDirectory;
	}

}

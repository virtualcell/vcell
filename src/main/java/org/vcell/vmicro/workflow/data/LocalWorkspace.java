/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.vmicro.workflow.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.resource.StdoutSessionLog;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataSetControllerImpl;

/**
 */
public class LocalWorkspace implements LocalContext, Serializable {
	private transient DataSetControllerImpl dataSetControllerImpl = null;
	
	private static long LAST_GENERATED_KEY = System.currentTimeMillis();
	
	private transient StdoutSessionLog sessionLog = null;
	
	private static final String SIMULATION_DATA_SUBDIRECTORY = "SimulationData";
    private static final User SIMULATION_OWNER = new User(SIMULATION_DATA_SUBDIRECTORY,new KeyValue("0"));
    private static final String APPLICATION_SUBDIRECTORY = "VirtualMicroscopy";

    private File workingDirectory;
    
	public LocalWorkspace(File workingDirectory){
		this.workingDirectory = workingDirectory;
		this.sessionLog = new StdoutSessionLog(LocalWorkspace.SIMULATION_OWNER.getName());
	}

	public static final KeyValue createNewKeyValue(){
		if(LAST_GENERATED_KEY != System.currentTimeMillis()){
			LAST_GENERATED_KEY = System.currentTimeMillis();
		}else{
			LAST_GENERATED_KEY+= 1;
		}
		return new KeyValue(LAST_GENERATED_KEY+"");
	}

	/**
	 * Method getWorkspaceDirectory.
	 * @return String
	 */
	public String getDefaultWorkspaceDirectory() {
		return
			workingDirectory.getAbsolutePath()+System.getProperty("file.separator")+
			APPLICATION_SUBDIRECTORY+System.getProperty("file.separator");
	}

	/**
	 * Method getOwner.
	 * @return User
	 */
	public static User getDefaultOwner() {
		return SIMULATION_OWNER;
	}

	
	/**
	 * Method getSimDataDirectory.
	 * @return String
	 */
	public String getDefaultSimDataDirectory() {
		File simulationDataDir = new File(getDefaultWorkspaceDirectory()+SIMULATION_OWNER.getName()+System.getProperty("file.separator"));
		//if directory doesn't exist, make one
		if(!simulationDataDir.exists()){
			simulationDataDir.mkdirs();	
		}
		return simulationDataDir.getAbsolutePath();
	}

	/**
	 * Method getDataSetControllerImpl.
	 * @return DataSetControllerImpl
	 * @throws FileNotFoundException
	 */
	public DataSetControllerImpl getDataSetControllerImpl() throws FileNotFoundException{ 
		if (dataSetControllerImpl==null){
			File rootDir = new File(getDefaultWorkspaceDirectory());
			dataSetControllerImpl = new DataSetControllerImpl(sessionLog,new Cachetable(10000),rootDir,rootDir);
		}
		return dataSetControllerImpl;
	}
	
}

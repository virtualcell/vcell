/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy;

import java.io.File;
import java.io.FileNotFoundException;

import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.server.DataSetController;
import cbit.vcell.server.DataSetControllerProvider;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.LocalDataSetController;
import cbit.vcell.simdata.VCDataManager;

/**
 */
public class LocalWorkspace {
//	private String workspaceDirectory = null;
//	private String simDataDirectory = null;
//	private User owner = null;
	private String mostRecentFilename = null;
	private DataSetControllerImpl dataSetControllerImpl = null;
	private VCDataManager vcDataManager = null;
	private UserPreferences userPreferences = null;
	
	private static long LAST_GENERATED_KEY = System.currentTimeMillis();
	
	private StdoutSessionLog sessionLog = null;
	private LocalDataSetController localDataSetController = null;
	
	private static final String SIMULATION_DATA_SUBDIRECTORY = "SimulationData";
    private static final User SIMULATION_OWNER = new User(SIMULATION_DATA_SUBDIRECTORY,new KeyValue("0"));
    private static final String APPLICATION_SUBDIRECTORY = "VirtualMicroscopy";

    private File workingDirectory;
    
	public LocalWorkspace(File workingDirectory){
		this.workingDirectory = workingDirectory;
		this.userPreferences = new UserPreferences(null);
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
	 * Method getUserPreferences.
	 * @return UserPreferences
	 */
	public UserPreferences getUserPreferences(){
		return userPreferences;
	}
	
	/**
	 * Method getMostRecentFilename.
	 * @return String
	 */
	public String getMostRecentFilename() {
		return mostRecentFilename;
	}

	/**
	 * Method setMostRecentFilename.
	 * @param mostRecentFilename String
	 */
	public void setMostRecentFilename(String mostRecentFilename) {
		this.mostRecentFilename = mostRecentFilename;
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
	
	/**
	 * Method getVCDataManager.
	 * @return VCDataManager
	 */
	public VCDataManager getVCDataManager() throws Exception{
		if (vcDataManager==null){
			localDataSetController =
				new LocalDataSetController(
						null,sessionLog,
						getDataSetControllerImpl(),
						null,getDefaultOwner()
					);

			vcDataManager =
				new VCDataManager(
					new DataSetControllerProvider(){
						public DataSetController getDataSetController(){
							return localDataSetController;
						}
					}
				);
		}
		return vcDataManager;
	}

}

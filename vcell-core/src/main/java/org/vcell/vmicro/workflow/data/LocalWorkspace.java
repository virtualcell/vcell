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
import java.io.IOException;
import java.io.Serializable;

import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.Origin;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solvers.CartesianMesh;

/**
 */
public class LocalWorkspace implements LocalContext, Serializable {
	private transient DataSetControllerImpl dataSetControllerImpl = null;
	
	private static long LAST_GENERATED_KEY = System.currentTimeMillis();
	
	private static final String SIMULATION_DATA_SUBDIRECTORY = "SimulationData";
    private static final User SIMULATION_OWNER = new User(SIMULATION_DATA_SUBDIRECTORY,new KeyValue("0"));
    private static final String APPLICATION_SUBDIRECTORY = "VirtualMicroscopy";

    private File workingDirectory;
    
	public LocalWorkspace(File workingDirectory){
		this.workingDirectory = workingDirectory;
	}

	public static ExternalDataInfo createNewExternalDataInfo(File targetDir, String extDataIDName){
		KeyValue key = LocalWorkspace.createNewKeyValue();
		User owner = LocalWorkspace.getDefaultOwner();
		
		ExternalDataIdentifier newImageDataExtDataID = new ExternalDataIdentifier(key, owner,extDataIDName);
		String filename = new File(targetDir,newImageDataExtDataID.getID()+SimDataConstants.LOGFILE_EXTENSION).getAbsolutePath();
		ExternalDataInfo newImageDataExtDataInfo = new ExternalDataInfo(newImageDataExtDataID, filename);
		
		return newImageDataExtDataInfo;
	}

	public static FieldDataFileOperationResults saveExternalData(Extent extent, Origin origin,ISize isize,double[] pixels, User dataSubDirOwner,String varName,ExternalDataIdentifier newROIExtDataID, DataSetControllerImpl dataSetControllerImpl) throws ObjectNotFoundException, ImageException, IOException 
	{
		VCImage vcImage = new VCImageUncompressed(null, new byte[isize.getXYZ()], extent, isize.getX(),isize.getY(),isize.getZ());
		RegionImage regionImage = new RegionImage(vcImage,0,null,null,RegionImage.NO_SMOOTHING);
		CartesianMesh simpleCartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, extent, isize, regionImage);
		int NumTimePoints = 1; 
		int NumChannels = 1;
		double[][][] pixData = new double[NumTimePoints][NumChannels][]; // dimensions: time points, channels, whole image ordered by z slices. 
		
		pixData[0][0] = pixels;

		FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
		fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
		fdos.cartesianMesh = simpleCartesianMesh;
		fdos.doubleSpecData =  pixData;
		fdos.specEDI = newROIExtDataID;
		fdos.varNames = new String[] { varName };
		fdos.owner = dataSubDirOwner;
		fdos.times = new double[] { 0.0 };
		fdos.variableTypes = new VariableType[] { VariableType.VOLUME };
		fdos.origin = origin;
		fdos.extent = extent;
		fdos.isize = isize;
		return dataSetControllerImpl.fieldDataFileOperation(fdos);
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
			dataSetControllerImpl = new DataSetControllerImpl(new Cachetable(10000,1000000L),rootDir,rootDir);
		}
		return dataSetControllerImpl;
	}
	
}

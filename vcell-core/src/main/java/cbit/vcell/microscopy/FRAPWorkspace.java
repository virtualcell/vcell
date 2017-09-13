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
import java.util.ArrayList;

import org.vcell.service.VCellServiceHelper;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.document.KeyValue;
import org.vcell.vcellij.ImageDatasetReader;
import org.vcell.vcellij.ImageDatasetReaderService;

import cbit.image.SourceDataInfo;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.microscopy.server.FrapDataUtils;
import cbit.vcell.simdata.SimDataConstants;

public abstract class FRAPWorkspace 
{
	public static FRAPStudy loadFRAPDataFromImageFile(File inputFile, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception
	{
		FRAPStudy newFrapStudy = new FRAPStudy();
		FRAPData newFrapData = null;
		newFrapStudy.setXmlFilename(null);
		ImageDataset imageDataset = ImageDatasetReaderService.getInstance().getImageDatasetReader().readImageDataset(inputFile.getAbsolutePath(), clientTaskStatusSupport);
		newFrapData = FrapDataUtils.importFRAPDataFromImageDataSet(imageDataset);
		newFrapStudy.setFrapData(newFrapData);
		
		return newFrapStudy;
	}
	
	public static FRAPStudy loadFRAPDataFromVcellLogFile(File inputFile, String identifierName, String bleachedMaskVarName, Double maxIntensity, boolean bNoise, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception
	{
		FRAPStudy newFrapStudy = new FRAPStudy();
		FRAPData newFrapData = null;
		newFrapStudy.setXmlFilename(null);
		try {
			newFrapData = FrapDataUtils.importFRAPDataFromVCellSimulationData(inputFile, identifierName, bleachedMaskVarName, maxIntensity, bNoise, clientTaskStatusSupport);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		newFrapStudy.setFrapData(newFrapData);
		
		return newFrapStudy;
	}
	
	public static FRAPStudy loadFRAPDataFromHDF5File(File inputFile, Double maxIntensity, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception
	{
		FRAPStudy newFrapStudy = new FRAPStudy();
		FRAPData newFrapData = null;
		newFrapStudy.setXmlFilename(null);
		try {
			newFrapData = FrapDataUtils.importFRAPDataFromHDF5Data(inputFile, maxIntensity, clientTaskStatusSupport);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		newFrapStudy.setFrapData(newFrapData);
		
		return newFrapStudy;
	}
	public static FRAPStudy loadFRAPDataFromDataProcessingOutput(ArrayList<SourceDataInfo> sdInfo,double[] times, int slice,Double maxIntensity, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception
	{
		FRAPStudy newFrapStudy = new FRAPStudy();
		FRAPData newFrapData = null;
		newFrapStudy.setXmlFilename(null);
		newFrapData = FrapDataUtils.createFrapData(sdInfo, times, slice, maxIntensity, clientTaskStatusSupport);
		newFrapStudy.setFrapData(newFrapData);
		return newFrapStudy;
	}

	public static FRAPStudy loadFRAPDataFromMultipleFiles(File[] inputFiles, ClientTaskStatusSupport clientTaskStatusSupport, boolean isTimeSeries, double timeInterval) throws Exception
	{
		FRAPStudy newFrapStudy = new FRAPStudy();
		FRAPData newFrapData = null;
		newFrapStudy.setXmlFilename(null);
		ImageDataset imageDataset;
		imageDataset = ImageDatasetReaderService.getInstance().getImageDatasetReader().readImageDatasetFromMultiFiles(inputFiles, clientTaskStatusSupport, isTimeSeries, timeInterval);
		newFrapData = new FRAPData(imageDataset, new String[] { FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name(),FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(),FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()});
		newFrapStudy.setFrapData(newFrapData);
		
		return newFrapStudy;
	}
	
	public abstract FRAPStudy getWorkingFrapStudy();

	public static boolean areSimulationFilesOK(LocalWorkspace localWorkspace,KeyValue key){
		
		String[] EXPECTED_SIM_EXTENSIONS =
			new String[] {
				SimDataConstants.ZIPFILE_EXTENSION,//may be more than 1 for big files
				SimDataConstants.FUNCTIONFILE_EXTENSION,
				".fvinput",
				SimDataConstants.LOGFILE_EXTENSION,
				SimDataConstants.MESHFILE_EXTENSION,
				".meshmetrics",
				".vcg",
				SimDataConstants.FIELDDATARESAMP_EXTENSION,//prebleach avg
				SimDataConstants.FIELDDATARESAMP_EXTENSION//postbleach avg
			};
		File[] simFiles = FRAPStudy.getSimulationFileNames(	new File(localWorkspace.getDefaultSimDataDirectory()),	key);
		//prebleach.fdat,postbleach.fdat,.vcg,.meshmetrics,.mesh,.log,.fvinput,.functions,.zip
		if(simFiles == null || simFiles.length < EXPECTED_SIM_EXTENSIONS.length){
			return false;
		}
		for (int i = 0; i < EXPECTED_SIM_EXTENSIONS.length; i++) {
			boolean bFound = false;
			for (int j = 0; j < simFiles.length; j++) {
				if(simFiles[j] != null && simFiles[j].getName().endsWith(EXPECTED_SIM_EXTENSIONS[i])){
					simFiles[j] = null;
					bFound = true;
					break;
				}
			}
			if(!bFound){
				return false;
			}
		}
		return true;
	}

	public static boolean areExternalDataOK(LocalWorkspace localWorkspace,ExternalDataInfo imgDataExtDataInfo,ExternalDataInfo roiExtDataInfo)
	{
		if(imgDataExtDataInfo == null || imgDataExtDataInfo.getExternalDataIdentifier() == null){
			return false;
		}
		File[] frapDataExtDataFiles =
			FRAPStudy.getCanonicalExternalDataFiles(localWorkspace,
					imgDataExtDataInfo.getExternalDataIdentifier());
		for (int i = 0;frapDataExtDataFiles != null && i < frapDataExtDataFiles.length; i++) {
			if(!frapDataExtDataFiles[i].exists()){
				return false;
			}
		}
		if(roiExtDataInfo == null || roiExtDataInfo.getExternalDataIdentifier() == null){
			return false;
		}
		File[] roiDataExtDataFiles =
			FRAPStudy.getCanonicalExternalDataFiles(localWorkspace,
					roiExtDataInfo.getExternalDataIdentifier());
		for (int i = 0;roiDataExtDataFiles != null && i < roiDataExtDataFiles.length; i++) {
			if(!roiDataExtDataFiles[i].exists()){
				return false;
			}
		}
	
		return true;
	}
}

package cbit.vcell.microscopy;

import java.io.File;
import java.io.IOException;

import org.vcell.util.document.KeyValue;

import loci.formats.FormatException;
import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReader;
import cbit.vcell.client.task.ClientTaskStatusSupport;
import cbit.vcell.simdata.SimDataConstants;

public abstract class FRAPWorkspace 
{
	public static FRAPStudy loadFRAPDataFromImageFile(File inputFile, ClientTaskStatusSupport clientTaskStatusSupport) throws ImageException, IOException, FormatException
	{
		FRAPStudy newFrapStudy = new FRAPStudy();
		FRAPData newFrapData = null;
		newFrapStudy.setXmlFilename(null);
		try{
			ImageDataset imageDataset = ImageDatasetReader.readImageDataset(inputFile.getAbsolutePath(), clientTaskStatusSupport);
			newFrapData = FRAPData.importFRAPDataFromImageDataSet(imageDataset);
		}catch(ImageException ie)
		{
			throw new ImageException(ie.getMessage());
		}catch(IOException ioe)
		{
			throw new IOException(ioe.getMessage());
		}catch(FormatException fe)
		{
			throw new FormatException(fe.getMessage());
		}
		newFrapStudy.setFrapData(newFrapData);
		
		return newFrapStudy;
	}
	
	public static FRAPStudy loadFRAPDataFromVcellLogFile(File inputFile, String identifierName, Double maxIntensity, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception
	{
		FRAPStudy newFrapStudy = new FRAPStudy();
		FRAPData newFrapData = null;
		newFrapStudy.setXmlFilename(null);
		try {
			newFrapData = FRAPData.importFRAPDataFromVCellSimulationData(inputFile, identifierName, maxIntensity, clientTaskStatusSupport);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		newFrapStudy.setFrapData(newFrapData);
		
		return newFrapStudy;
	}
	
	public static FRAPStudy loadFRAPDataFromMultipleFiles(File[] inputFiles, ClientTaskStatusSupport clientTaskStatusSupport, boolean isTimeSeries, double timeInterval) throws ImageException, IOException, FormatException
	{
		FRAPStudy newFrapStudy = new FRAPStudy();
		FRAPData newFrapData = null;
		newFrapStudy.setXmlFilename(null);
		ImageDataset imageDataset;
		try {
			imageDataset = ImageDatasetReader.readImageDatasetFromMultiFiles(inputFiles, clientTaskStatusSupport, isTimeSeries, timeInterval);
			newFrapData = new FRAPData(imageDataset, new String[] { FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name(),FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(),FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()});
		} catch(ImageException ie)
		{
			throw new ImageException(ie.getMessage());
		}catch(IOException ioe)
		{
			throw new IOException(ioe.getMessage());
		}catch(FormatException fe)
		{
			throw new FormatException(fe.getMessage());
		}
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

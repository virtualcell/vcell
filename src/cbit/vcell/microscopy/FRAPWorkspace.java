package cbit.vcell.microscopy;

import java.io.File;
import java.io.IOException;

import loci.formats.FormatException;
import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReader;
import cbit.vcell.client.task.ClientTaskStatusSupport;

public abstract class FRAPWorkspace 
{
	public FRAPStudy loadFRAPDataFromImageFile(File inputFile, ClientTaskStatusSupport clientTaskStatusSupport) throws ImageException, IOException, FormatException
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
	
	public FRAPStudy loadFRAPDataFromVcellLogFile(File inputFile, String identifierName, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception
	{
		FRAPStudy newFrapStudy = new FRAPStudy();
		FRAPData newFrapData = null;
		newFrapStudy.setXmlFilename(null);
		try {
			newFrapData = FRAPData.importFRAPDataFromVCellSimulationData(inputFile, identifierName, clientTaskStatusSupport);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		newFrapStudy.setFrapData(newFrapData);
		
		return newFrapStudy;
	}
	
	public FRAPStudy loadFRAPDataFromMultipleFiles(File[] inputFiles, ClientTaskStatusSupport clientTaskStatusSupport, boolean isTimeSeries, double timeInterval) throws ImageException, IOException, FormatException
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
}

package cbit.vcell.microscopy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;

import org.vcell.util.Extent;

import loci.formats.FormatException;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReader;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.client.task.ClientTaskStatusSupport;


public class FRAPWorkspace {
	
	//property names
	public static final String FRAPSTUDY_CHANGE_NEW_PROPERTY = "FRAPSTUDY_CHANGE_NEW_PROPERTY";
	public static final String FRAPSTUDY_CHANGE_NOTNEW_PROPERTY = "FRAPSTUDY_CHANGE_NOTNEW_PROPERTY";
	public static final String FRAPDATA_VERIFY_INFO_PROPERTY = "FRAPDATA_VERIFY_INFO_PROPERTY";
	
	private FRAPStudy frapStudy = null;
	private PropertyChangeSupport propertyChangeSupport;
	
	public FRAPWorkspace()
	{
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	public FRAPStudy getFrapStudy() {
		return frapStudy;
	}
	
	public void setFrapStudy(FRAPStudy arg_frapStudy, boolean bNew) {
		FRAPStudy oldFrapStudy = getFrapStudy();
		this.frapStudy = arg_frapStudy;
		if(bNew)
		{
			firePropertyChange(FRAPSTUDY_CHANGE_NEW_PROPERTY, oldFrapStudy, arg_frapStudy);
		}
		else
		{
			firePropertyChange(FRAPSTUDY_CHANGE_NOTNEW_PROPERTY, oldFrapStudy, arg_frapStudy);
		}
	}

	public void updateImages(DataVerifyInfo dataVerifyInfo)
	{
		FRAPData fData = null;
		FRAPStudy fStudy = getFrapStudy();
		if(fStudy != null && fStudy.getFrapData() !=null)
		{
			fData = fStudy.getFrapData();
		}
		//set verify info
		if(fData != null)
		{
			ImageDataset oldImageSet = fData.getImageDataset();
			Extent oldExtent = oldImageSet.getExtent();
			
			double[] timeSteps = oldImageSet.getImageTimeStamps();
			UShortImage[] images = oldImageSet.getAllImages();
			boolean isChanged = false;
			if(dataVerifyInfo.getStartTimeIndex() > 0 || dataVerifyInfo.getEndTimeIndex() < (images.length-1))
			{
				fData.chopImages(dataVerifyInfo.getStartTimeIndex(), dataVerifyInfo.getEndTimeIndex());
				isChanged = true;
			}
			
			if(dataVerifyInfo.getImageSizeX()!= images[0].getExtent().getX() ||
					dataVerifyInfo.getImageSizeY()!= images[0].getExtent().getY())
			{
				fData.changeImageExtent(dataVerifyInfo.getImageSizeX(), dataVerifyInfo.getImageSizeY());
				isChanged = true;
			}
			if(isChanged)
			{
				firePropertyChange(FRAPDATA_VERIFY_INFO_PROPERTY, null, fData);
			}
		}
	}
	
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
	
	public void addPropertyChangeListener(PropertyChangeListener p) {
    	propertyChangeSupport.addPropertyChangeListener(p);
    }
  
    public void removePropertyChangeListener(PropertyChangeListener p) {
    	propertyChangeSupport.removePropertyChangeListener(p);
    }
    
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    	propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}

package cbit.vcell.microscopy;

import java.beans.PropertyChangeEvent;
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


public class FRAPWorkspace implements PropertyChangeListener{
	
	private FRAPStudy frapStudy = null;
	private PropertyChangeSupport propertyChangeSupport;
	
	//Properties that are used in VFRAP
	public static final String PROPERTY_CHANGE_FRAPSTUDY_NEW = "FRAPSTUDY_NEW";
	public static final String PROPERTY_CHANGE_FRAPSTUDY_UPDATE = "FRAPSTUDY_UPDATE";
	public static final String FRAPDATA_VERIFY_INFO_PROPERTY = "FRAPDATA_VERIFY_INFO_PROPERTY";
	public static final String PROPERTY_CHANGE_EST_OFF_RATE = "EST_OFF_RATE";
	public static final String PROPERTY_CHANGE_EST_ON_RATE = "EST_ON_RATE";
	public static final String PROPERTY_CHANGE_EST_BS_CONCENTRATION = "EST_BS_CONCENTRATION";
	public static final String PROPERTY_CHANGE_RUN_BINDING_SIMULATION = "RUN_BINDING_SIMULATION";
	public static final String PROPERTY_CHANGE_EST_BINDING_PARAMETERS = "EST_BINDING_PARAMETERS";
	public static final String PROPERTY_CHANGE_PARAMETER_ESTIMATE_VALUES = "PARAMETER_ESTIMATE_VALUES_CHANGE";
	public static final String PROPERTY_CHANGE_CURRENTLY_DISPLAYED_ROI_WITHOUT_SAVE = "CURRENTLY_DISPLAYED_ROI_WITHOUT_SAVE";
	public static final String PROPERTY_CHANGE_CURRENTLY_DISPLAYED_ROI_WITH_SAVE = "CURRENTLY_DISPLAYED_ROI_WITH_SAVE";
	public static final String PROPERTY_CHANGE_OPTIMIZER_VALUE = "OPTIMIZER_VALUE_CHANGE";
	public static final String PROPERTY_CHANGE_BEST_MODEL = "BEST_MODEL_CHANGE";
	
	public FRAPWorkspace()
	{
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	public FRAPStudy getFrapStudy() {
		return frapStudy;
	}
	
	public void setFrapStudy(FRAPStudy arg_frapStudy, boolean bNew) {
		FRAPStudy oldFrapStudy = getFrapStudy();
		if(oldFrapStudy != null)
		{
			oldFrapStudy.removePropertyChangeListener(this);
		}
		arg_frapStudy.addPropertyChangeListener(this);
		this.frapStudy = arg_frapStudy;
		if(bNew)
		{
			firePropertyChange(PROPERTY_CHANGE_FRAPSTUDY_NEW, oldFrapStudy, arg_frapStudy);
		}
		else
		{
			firePropertyChange(PROPERTY_CHANGE_FRAPSTUDY_UPDATE, oldFrapStudy, arg_frapStudy);
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

	public void propertyChange(PropertyChangeEvent evt) 
	{
		if(evt.getPropertyName().equals(PROPERTY_CHANGE_BEST_MODEL))
		{
			firePropertyChange(PROPERTY_CHANGE_BEST_MODEL, evt.getOldValue(), evt.getNewValue());
		}
		
	}
}

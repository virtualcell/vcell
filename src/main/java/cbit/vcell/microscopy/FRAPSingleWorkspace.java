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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;


public class FRAPSingleWorkspace extends FRAPWorkspace implements PropertyChangeListener{
	
	private FRAPStudy frapStudy = null;
	private PropertyChangeSupport propertyChangeSupport;
	
	//Properties that are used in VFRAP
	public static final String PROPERTY_CHANGE_FRAPSTUDY_NEW = "FRAPSTUDY_NEW";
	public static final String PROPERTY_CHANGE_FRAPSTUDY_UPDATE = "FRAPSTUDY_UPDATE";
	public static final String PROPERTY_CHANGE_FRAPSTUDY_BATCHRUN = "FRAPSTUDY_BATCHRUN";
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
	public static final String PROPERTY_CHANGE_BEST_MODEL_WITH_SIGNIFICANCE = "BEST_MODEL_WITH_SIGNIFICANCE";
	public static final String PROPERTY_CHANGE_CONFIDENCEINTERVAL_DETAILS = "PROPERTY_CHANGE_CONFIDENCEINTERVAL_DETAILS";
	
	public FRAPSingleWorkspace()
	{
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	public FRAPStudy getWorkingFrapStudy() {
		return frapStudy;
	}
	
	public void setFrapStudy(FRAPStudy arg_frapStudy, boolean bNew) {
		setFrapStudy(arg_frapStudy, bNew, false);
	}

	public void setFrapStudy(FRAPStudy arg_frapStudy, boolean bNew, boolean bFromBatchRun)
	{
		FRAPStudy oldFrapStudy = getWorkingFrapStudy();
		if(oldFrapStudy != null)
		{
			oldFrapStudy.removePropertyChangeListener(this);
		}
		if(arg_frapStudy != null)
		{
			arg_frapStudy.addPropertyChangeListener(this);
		}
		this.frapStudy = arg_frapStudy;
		if(bFromBatchRun)
		{
			firePropertyChange(PROPERTY_CHANGE_FRAPSTUDY_BATCHRUN, oldFrapStudy, arg_frapStudy);
		}
		else
		{
			if(bNew)
			{
				firePropertyChange(PROPERTY_CHANGE_FRAPSTUDY_NEW, oldFrapStudy, arg_frapStudy);
			}
			else
			{
				firePropertyChange(PROPERTY_CHANGE_FRAPSTUDY_UPDATE, oldFrapStudy, arg_frapStudy);
			}
		}
	}
	
	public void updateImages(DataVerifyInfo dataVerifyInfo)
	{
		FRAPData fData = null;
		FRAPStudy fStudy = getWorkingFrapStudy();
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

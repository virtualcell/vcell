/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package org.vcell.inversepde.microscopy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.microscopy.ExternalDataInfo;

public class FRAPStudy implements Matchable{
	private String name = null;
	private String description = null;
	private String originalImageFilePath = null;
	private AnnotatedImageDataset_inv frapData = null;
	private BioModel bioModel = null;
	private ExternalDataInfo frapDataExternalDataInfo = null;
	private ExternalDataInfo roiExternalDataInfo = null;
	private ExternalDataInfo refExternalDataInfo = null;
	public ExternalDataInfo getRefExternalDataInfo() {
		return refExternalDataInfo;
	}


	public void setRefExternalDataInfo(ExternalDataInfo refExternalDataInfo) {
		this.refExternalDataInfo = refExternalDataInfo;
	}
	PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private FRAPModelParameters frapModelParameters;
	
	public ExternalDataInfo getFrapDataExternalDataInfo() {
		return frapDataExternalDataInfo;
	}
	
	
	public void setFrapDataExternalDataInfo(ExternalDataInfo imageDatasetExternalDataInfo) {
		ExternalDataInfo oldValue = this.frapDataExternalDataInfo;
		this.frapDataExternalDataInfo = imageDatasetExternalDataInfo;
		propertyChangeSupport.firePropertyChange("imageDatasetExternalDataInfo", oldValue, imageDatasetExternalDataInfo);
	}

	public ExternalDataInfo getRoiExternalDataInfo() {
		return roiExternalDataInfo;
	}

	public void setRoiExternalDataInfo(ExternalDataInfo roiExternalDataInfo) {
		ExternalDataInfo oldValue = this.roiExternalDataInfo;
		this.roiExternalDataInfo = roiExternalDataInfo;
		propertyChangeSupport.firePropertyChange("roiExternalDataInfo", oldValue, roiExternalDataInfo);
	}
		
	public BioModel getBioModel() {
		return bioModel;
	}
	
	public void setBioModel(BioModel argBioModel) {
		BioModel oldValue = this.bioModel;
		this.bioModel = argBioModel;
		propertyChangeSupport.firePropertyChange("bioModel", oldValue, argBioModel);
	}
	public AnnotatedImageDataset_inv getFrapData() {
		return frapData;
	}
	
	public void setFrapData(AnnotatedImageDataset_inv frapData) {
		this.frapData = frapData;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		String oldValue = this.description;
		this.description = description;
		propertyChangeSupport.firePropertyChange("description", oldValue, description);
	}

	public String getOriginalImageFilePath() {
		return originalImageFilePath;
	}

	public void setOriginalImageFilePath(String originalImageFilePath) {
		String oldValue = this.originalImageFilePath;
		this.originalImageFilePath = originalImageFilePath;
		propertyChangeSupport.firePropertyChange("originalImageFilePath", oldValue, originalImageFilePath);
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = this.name;
		this.name = name;
		propertyChangeSupport.firePropertyChange("name", oldValue, name);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public boolean compareEqual(Matchable obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj instanceof FRAPStudy) 
		{
			FRAPStudy fStudy = (FRAPStudy)obj;
			if(!Compare.isEqualOrNull(name, fStudy.name))
			{
				return false;
			}
			if(!Compare.isEqualOrNull(description, fStudy.description))
			{
				return false;
			}
			if(!Compare.isEqualOrNull(originalImageFilePath, fStudy.originalImageFilePath))
			{
				return false;
			}
			if(!this.getFrapData().compareEqual(fStudy.getFrapData()))
			{
				return false;
			}
			if(!Compare.isEqualOrNull(getBioModel(), fStudy.getBioModel()))
			{
				return false;
			}
			if(!Compare.isEqualOrNull(getFrapDataExternalDataInfo(),fStudy.getFrapDataExternalDataInfo()))
			{
				return false;
			}
			if (!Compare.isEqualOrNull(getRoiExternalDataInfo(),fStudy.getRoiExternalDataInfo()))
			{
				return false;
			}
			if (!Compare.isEqualOrNull(getRefExternalDataInfo(),fStudy.getRefExternalDataInfo()))
			{
				return false;
			}
			return true;
		}

		return false;
	}


	public FRAPModelParameters getFrapModelParameters() {
		return frapModelParameters;
	}


	public void setFrapModelParameters(FRAPModelParameters frapModelParameters) {
		this.frapModelParameters = frapModelParameters;
	}

}

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

import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Vector;

import org.vcell.inversepde.microscopy.ROIImage.ROIImageComponent;
import org.vcell.util.Compare;
import org.vcell.util.ISize;
import org.vcell.util.Issue;
import org.vcell.util.Matchable;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.ImageDataset;

/**
 */
public class AnnotatedImageDataset_inv implements Matchable {

	private ImageDataset imageDataset = null;
	private ROIImage[] roiImages = null;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private OriginalGlobalScaleInfo originalGlobalScaleInfo;
	private transient ROIImageComponent currentlyDisplayedROI = null;

	public static final String PROPERTY_NAME_CURRENTLY_DISPLAYED_ROI = "currentlyDisplayedROI";
	/*
	 * Constructor for AnnotatedImageDataset.
	 * @param argImageDataset ImageDataset
	 * @param argROIs ROI[]
	 */
	public AnnotatedImageDataset_inv(ImageDataset argImageDataset, ROIImage[] argROIImages) {
		// Error checking
		if (argImageDataset.getAllImages().length == 0) {
			throw new RuntimeException("image dataset is empty");
		}
		// Now initialize
		this.imageDataset = argImageDataset;
		this.roiImages = argROIImages;
		for (int i = 0; i < argROIImages.length; i++) {
			verifyROIdimensions(imageDataset, roiImages[i]);
		}
	}


	/**
	 * Method verifyROIdimensions.
	 * @param argImageDataset ImageDataset
	 * @param argROIs ArrayList<ROI>
	 */
	private void verifyROIdimensions(ImageDataset argImageDataset, ROIImage roiImage){
		if (roiImage!=null){
			int imgNumX = argImageDataset.getImage(0,0,0).getNumX();
			int imgNumY = argImageDataset.getImage(0,0,0).getNumY();
			int imgNumZ = argImageDataset.getSizeZ();
			if (!roiImage.getISize().compareEqual(new ISize(imgNumX,imgNumY,imgNumZ))){
				throw new RuntimeException("ROI size ("+roiImage.getISize().toString()+") doesn't match image size ("+imgNumX+","+imgNumY+","+imgNumZ+")");
			}
		}
	}
	
	/**
	 * Method getRoi.
	 * @param roiType RoiType
	 * @return ROI
	 */
	public ROIImageComponent getRoi(String roiName) {
		for (int i = 0; i < roiImages.length; i++) {
			ROIImageComponent rOIImageComponent = roiImages[i].getROI(roiName);
			if (rOIImageComponent!=null){
				return rOIImageComponent;
			}
		}
		return null;
	}

	public int getDimension(){
		final int dim;
		if (getImageDataset().getISize().getZ()>1){
			dim = 3;
		}else if (getImageDataset().getISize().getY()>1){
			dim = 2;
		}else{
			dim = 1;
		}
		return dim;
	}
	
	/**
	 * Method getNumRois.
	 * @return int
	 */
	public int getNumRois() {
		ROIImageComponent[] rois = getRois();
		return rois.length;
	}

	/**
	 * Method removeRoi.
	 * @param rOIImageComponent ROI
	 */
	public void removeRoi(ROIImageComponent rOIImageComponent) {
		for (int i = 0; i < roiImages.length; i++) {
			if (rOIImageComponent.getROIImage()==roiImages[i]){
				roiImages[i].removeROI(rOIImageComponent);
			}
		}
	}

	/**
	 * Method addPropertyChangeListener.
	 * @param listener PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Method removePropertyChangeListener.
	 * @param listener PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * Method getImageDataset.
	 * @return ImageDataset
	 */
	public ImageDataset getImageDataset() {
		return imageDataset;
	}

	/**
	 * Method gatherIssues.
	 * @param issueList Vector<Issue>
	 */
	public void gatherIssues(Vector<Issue> issueList) {
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/24/2007 4:40:44 PM)
	 * @return java.lang.Float
	 */
	public ROIImageComponent[] getRois() {
		ArrayList<ROIImageComponent> roiList = new ArrayList<ROIImageComponent>();
		for (int i = 0; i < roiImages.length; i++) {
			ROIImageComponent[] rois = roiImages[i].getROIs();
			for (int j = 0; j < rois.length; j++) {
				roiList.add(rois[j]);
			}
		}
		return roiList.toArray(new ROIImageComponent[roiList.size()]);
	}
	
	public void replaceROIImage(ROIImage roiImage) {
		for (int i = 0; i < roiImages.length; i++) {
			if (roiImages[i].getName().equals(roiImage.getName())){
				if (!roiImages[i].getISize().compareEqual(roiImage.getISize())){
					throw new RuntimeException("roiImages don't match");
				}
				if (!Compare.isEqual(roiImages[i].getROIs(),roiImage.getROIs())){
					throw new RuntimeException("rois don't match");
				}
				roiImages[i] = roiImage;
			}
		}
	}
	
	
	public ROIImage[] getROIImages() {
		return roiImages.clone();
	}

	public AnnotatedImageDataset_inv crop(Rectangle rect) throws ImageException {
		ImageDataset croppedImageDataset = getImageDataset().crop(rect);
		ROIImage[] roiImages = getROIImages();
		ROIImage[] croppedROIImages = new ROIImage[roiImages.length];
		for (int i = 0; i < croppedROIImages.length; i++) {
			croppedROIImages[i] = roiImages[i].crop(rect);
		}
		AnnotatedImageDataset_inv croppedFrapData = new AnnotatedImageDataset_inv(croppedImageDataset,croppedROIImages);
		if (currentlyDisplayedROI!=null){
			croppedFrapData.setCurrentlyDisplayedROI(croppedFrapData.getRoi(currentlyDisplayedROI.getName()));
		}
		return croppedFrapData;
	}


	public boolean compareEqual(Matchable obj) 
	{
		if (this == obj) {
			return true;
		}
		if (obj != null && obj instanceof AnnotatedImageDataset_inv) 
		{
			AnnotatedImageDataset_inv data = (AnnotatedImageDataset_inv) obj;
			if (!Compare.isEqualOrNull(getImageDataset(), data.getImageDataset())){
				return false;
			}
			if (!Compare.isEqualOrNull(roiImages, data.roiImages)){
				return false;
			}
			if (!Compare.isEqualOrNull(getOriginalGlobalScaleInfo(), data.getOriginalGlobalScaleInfo())){
				return false;
			}
			return true;
		}
		return false;
	}

	public OriginalGlobalScaleInfo getOriginalGlobalScaleInfo() {
		return originalGlobalScaleInfo;
	}

	public void setOriginalGlobalScaleInfo(OriginalGlobalScaleInfo originalGlobalScaleInfo) {
		this.originalGlobalScaleInfo = originalGlobalScaleInfo;
	}


	public ROIImage getROIImage(String name) {
		for (int i = 0; i < roiImages.length; i++) {
			if (roiImages[i].getName().equals(name)){
				return roiImages[i];
			}
		}
		return null;
	}


	public ROIImageComponent getCurrentlyDisplayedROI() {
		return currentlyDisplayedROI;
	}


	private void setCurrentlyDisplayedROI(ROIImageComponent argCurrentlyDisplayedROI) {
		ROIImageComponent oldDisplayedROI = this.currentlyDisplayedROI;
		this.currentlyDisplayedROI = argCurrentlyDisplayedROI;
		propertyChangeSupport.firePropertyChange(PROPERTY_NAME_CURRENTLY_DISPLAYED_ROI, null, currentlyDisplayedROI);
	}
	
	public void setCurrentlyDisplayedROI(String roiName){
		setCurrentlyDisplayedROI(getRoi(roiName));
	}
}
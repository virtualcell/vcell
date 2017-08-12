/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.VirtualMicroscopy.importer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import org.vcell.util.Issue;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;

/**
 */
public class AnnotatedImageDataset {

	private ImageDataset imageDataset = null;
	private ArrayList<ROI> rois = new ArrayList<ROI>();
	private transient ROI currentlyDisplayedROI = null;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private String PROPERTY_CHANGE_CURRENTLY_DISPLAYED_ROI_WITH_SAVE = "PROPERTY_CHANGE_CURRENTLY_DISPLAYED_ROI_WITH_SAVE";
	private String PROPERTY_CHANGE_CURRENTLY_DISPLAYED_ROI_WITHOUT_SAVE = "PROPERTY_CHANGE_CURRENTLY_DISPLAYED_ROI_WITHOUT_SAVE";
	
	public static enum VFRAP_ROI_ENUM {
        ROI_BLEACHED,
        ROI_BACKGROUND,
        ROI_CELL,
        ROI_BLEACHED_RING1,
        ROI_BLEACHED_RING2,
        ROI_BLEACHED_RING3,
        ROI_BLEACHED_RING4,
        ROI_BLEACHED_RING5,
        ROI_BLEACHED_RING6,
        ROI_BLEACHED_RING7,
        ROI_BLEACHED_RING8
	}

	/**
	 * Constructor for AnnotatedImageDataset.
	 * @param argImageDataset ImageDataset
	 * @param argROIs ROI[]
	 */
	public AnnotatedImageDataset(ImageDataset argImageDataset, ROI[] argROIs) {
		// Error checking
		if (argImageDataset.getAllImages().length == 0) {
			throw new RuntimeException("image dataset is empty");
		}
		// Now initialize
		this.imageDataset = argImageDataset;
		rois = new ArrayList<ROI>(Arrays.asList(argROIs));	
		verifyROIdimensions(imageDataset,rois);
		if (rois.size()>0){
			setCurrentlyDisplayedROI(rois.get(0));
		}
	}

	/**
	 * Constructor for AnnotatedImageDataset.
	 * @param argImageDataset ImageDataset
	 * @param argROITypes ROI.RoiType[]
	 */
	public AnnotatedImageDataset(ImageDataset argImageDataset, String[] argROINames) {
		// Error checking
		if (argImageDataset.getAllImages().length == 0) {
			throw new RuntimeException("image dataset is empty");
		}
		// Now initialize
		this.imageDataset = argImageDataset;
		
		rois = new ArrayList<ROI>();
		for (int i = 0;argROINames!=null && i < argROINames.length; i++) {
			UShortImage[] roiImages = new UShortImage[imageDataset.getSizeZ()];
			try {
				for (int j = 0; j < roiImages.length; j++) {
					roiImages[j] = new UShortImage(imageDataset.getImage(j,0,0));//getImage(z,c,t), it gets a 2D image at z=j 
					java.util.Arrays.fill(roiImages[j].getPixels(),(short)0);
				}
			} catch (ImageException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
			//comment added in Feb, 2008. Each roi contains images, whoes size equals to size of z slices.
			ROI roi = new ROI(roiImages,argROINames[i]);
			rois.add(roi);//rois contains different types of roi, 11 types. 3 primary + 8 generated.
		}
		verifyROIdimensions(imageDataset, rois);
		if (rois.size()>0){
			setCurrentlyDisplayedROI(rois.get(0));
		}
	}

	/**
	 * Method verifyROIdimensions.
	 * @param argImageDataset ImageDataset
	 * @param argROIs ArrayList<ROI>
	 */
	private void verifyROIdimensions(ImageDataset argImageDataset, ArrayList<ROI> argROIs){
		if (rois!=null){
			int imgNumX = argImageDataset.getImage(0,0,0).getNumX();
			int imgNumY = argImageDataset.getImage(0,0,0).getNumY();
			int imgNumZ = argImageDataset.getSizeZ();
			for (int i = 0; i < argROIs.size(); i++) {
				UShortImage firstROIImage = argROIs.get(i).getRoiImages()[0];
				int roiNumX = firstROIImage.getNumX();
				int roiNumY = firstROIImage.getNumY();
				int roiNumZ = argROIs.get(i).getRoiImages().length;
				if (roiNumX!=imgNumX || roiNumY!=imgNumY || roiNumZ!=imgNumZ){
					throw new RuntimeException("ROI size ("+roiNumX+","+roiNumY+","+roiNumZ+") doesn't match image size ("+imgNumX+","+imgNumY+","+imgNumZ+")");
				}
			}
		}
	}
	
	/**
	 * Method getRoi.
	 * @param roiType RoiType
	 * @return ROI
	 */
	public ROI getRoi(String roiName) {
		for (int i = 0;i<rois.size(); i++) {
			if (rois.get(i).getROIName().equals(roiName)){
				return rois.get(i);
			}
		}
		return null;
	}

	public static short[] collectAllZAtOneTimepointIntoOneArray(ImageDataset sourceImageDataSet,int timeIndex){
		short[] collectedPixels = new short[sourceImageDataSet.getISize().getXYZ()];
		int pixelIndex = 0;
		for (int z = 0; z < sourceImageDataSet.getSizeZ(); z++) {
			short[] slicePixels = sourceImageDataSet.getImage(z, 0, timeIndex).getPixels();
			System.arraycopy(slicePixels, 0, collectedPixels, pixelIndex, slicePixels.length);
			pixelIndex+= slicePixels.length;
		}
		return collectedPixels;
	}

	//reorder ROIs according to the order of FRAPData.VFRAP_ROI_ENUM
	public static ROI[] reorderROIs(ROI[] origROIs)
	{
		ROI[] resultROIs = new ROI[origROIs.length];
		for(int i=0; i<VFRAP_ROI_ENUM.values().length; i++)
		{
			for(int j=0; j<origROIs.length; j++)
			{
				if(VFRAP_ROI_ENUM.values()[i].name().equals(origROIs[j].getROIName()))
				{
					resultROIs[i] = origROIs[j];
					break;
				}
			}
		}
		return resultROIs;
	}
	
	/**
	 * Method getAverageUnderROI.
	 * @param channelIndex int
	 * @param timeIndex int
	 * @param roi ROI
	 * @return double
	 */
	//average under roi at each time point
	public double getAverageUnderROI(int timeIndex, ROI roi,double[] normalizeFactorXYZ,double preNormalizeOffset){
		short[] dataArray = AnnotatedImageDataset.collectAllZAtOneTimepointIntoOneArray(imageDataset, timeIndex);
		short[] roiArray = roi.getPixelsXYZ();
		return AnnotatedImageDataset.getAverageUnderROI(dataArray,roiArray,normalizeFactorXYZ,preNormalizeOffset);
	}
	//NOTE: the normalized fractor (prebleachaverage) should have background subtracted.
	public static double getAverageUnderROI(Object dataArray,short[] roi,double[] normalizeFactorXYZ,double preNormalizeOffset){
		
		if(!(dataArray instanceof short[]) && !(dataArray instanceof double[])){
			throw new IllegalArgumentException("getAverageUnderROI: Only short[] and double[] implemented");	
		}
		if(normalizeFactorXYZ == null && preNormalizeOffset != 0){
			throw new IllegalArgumentException("preNormalizeOffset must be 0 if normalizeFactorXYZ is null");
		}
		
		int arrayLength = Array.getLength(dataArray);
		
		if(normalizeFactorXYZ != null && arrayLength != normalizeFactorXYZ.length){
			throw new IllegalArgumentException("Data array length and normalize length do not match");	
		}
		if(roi != null && roi.length != arrayLength){
			throw new IllegalArgumentException("Data array length and roi length do not match");	
		}
		
		double intensityVal = 0.0;
		long numPixelsInMask = 0;

//		System.out.println("prenormalizeoffset="+preNormalizeOffset);

		for (int i = 0; i < arrayLength; i++) {
			double imagePixel = (dataArray instanceof short[]?(((short[])dataArray)[i]) & 0x0000FFFF:((double[])dataArray)[i]);
			if (roi == null || roi[i] != 0){
				if(normalizeFactorXYZ == null){
					intensityVal += imagePixel;
					
				}else{
					//if pixel value after background subtraction is <=0, clamp it to 0
					//the whole image add up 1 after background subtraction
					if(((double)imagePixel-preNormalizeOffset) > 0)
					{
						intensityVal += ((double)imagePixel+1-preNormalizeOffset)/(normalizeFactorXYZ[i]);
					}
					else
					{
						intensityVal += 1/(normalizeFactorXYZ[i]);
					}
				}
				numPixelsInMask++;
			}
		}
		if (numPixelsInMask==0){
			return 0.0;
		}
		
		return intensityVal/numPixelsInMask;

	}
	/**
	 * Method getNumRois.
	 * @return int
	 */
	public int getNumRois() {
		return rois.size();
	}

	/**
	 * Method addRoi.
	 * @param roi ROI
	 */
	public void addReplaceRoi(ROI roi) {
		ROI[] oldROIs = getRois();
		rois = new ArrayList<ROI>();
		boolean isCurrentlyDisplayed = false;
		for (int i = 0; i < oldROIs.length; i++) {
			if(!oldROIs[i].getROIName().equals(roi.getROIName())){
				rois.add(oldROIs[i]);
			}else{
				if(currentlyDisplayedROI == oldROIs[i]){
					isCurrentlyDisplayed = true;
				}
			}
		}
		rois.add(roi);
		if(isCurrentlyDisplayed){
			setCurrentlyDisplayedROI(roi);
		}
		//property is not being listened anywhere, commented on 15 Setp, 2009
//		propertyChangeSupport.firePropertyChange("rois", oldROIs, newROIs);
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
	 * This method returns a ROI array of rois.
	 * In this method, we loop through rois to put rois according to the order of FRAPData.VFRAP_ROI_ENUM
	 * Please note : Use getROILength() method if simply need the length of rois.
	 * Create a local variable if need to use getRois() multiple times.
	 */
	public ROI[] getRois()
	{
		ROI[] oldROIs = rois.toArray(new ROI[rois.size()]);
		return reorderROIs(oldROIs);
	}

	public int getROILength()
	{
		return rois.size();
	}
	/**
	 * Method getCurrentlyDisplayedROI.
	 * @return ROI
	 */
	public ROI getCurrentlyDisplayedROI() {
		return currentlyDisplayedROI;
	}

	/**
	 * Method setCurrentlyDisplayedROI.
	 * @param argCurrentlyDisplayedROI ROI
	 */
	public void setCurrentlyDisplayedROI(ROI argCurrentlyDisplayedROI) {
		setCurrentlyDisplayedROI(argCurrentlyDisplayedROI, true);
	}

	public void setCurrentlyDisplayedROI(ROI argCurrentlyDisplayedROI, boolean bSave) {
		ROI oldDisplayedROI = this.currentlyDisplayedROI;
		this.currentlyDisplayedROI = argCurrentlyDisplayedROI;
		if(bSave)
		{
			propertyChangeSupport.firePropertyChange(PROPERTY_CHANGE_CURRENTLY_DISPLAYED_ROI_WITH_SAVE, oldDisplayedROI, currentlyDisplayedROI);
		}
		else
		{
			propertyChangeSupport.firePropertyChange(PROPERTY_CHANGE_CURRENTLY_DISPLAYED_ROI_WITHOUT_SAVE, oldDisplayedROI, currentlyDisplayedROI);
		}
	}
	
	//have to use this method, because the when switching between different frapStudies,
	//the old and current displayedROI are the same(e.g study1@cellROI --> study2@cellROI --> study1@cellROI),it will not fire property change.
	//however, we want to change the image when shift from one frapStudy to another.
	public void setCurrentlyDisplayedROIForBatchRun(ROI argCurrentlyDisplayedROI)
	{
		this.currentlyDisplayedROI = argCurrentlyDisplayedROI;
		propertyChangeSupport.firePropertyChange(PROPERTY_CHANGE_CURRENTLY_DISPLAYED_ROI_WITHOUT_SAVE, null, currentlyDisplayedROI);
	}
	
	public void setImageDataset(ImageDataset newImgDataset)
	{
		imageDataset = newImgDataset;
	}
}

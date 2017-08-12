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

import java.awt.Rectangle;

import org.vcell.util.Compare;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Matchable;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.Image.ImageStatistics;

/**
 * Insert the type's description here.
 * Creation date: (1/24/2007 4:18:01 PM)
 * @author: Anuradha Lakshminarayana
 * 
 * The image dataset works with 2D images (the third dimension is formed from multiple images)
 * 
 * 
 * 
 */

public class ImageTimeSeries<ImageType extends Image> implements Matchable{
	private ImageType[] images = null;
	private double[] imageTimeStamps = null;
	private final Class<ImageType> type;
	int numZ = -1;

	/**
	 * FRAPData constructor comment.
	 */
	public ImageTimeSeries(Class<ImageType> cls, ImageType[] argImages, double[] argTimeStamps, int argNumZ) {
		super();
		this.type = cls;

		// Error checking
		if (argImages.length == 0) {
			throw new RuntimeException("Cannot perform FRAP analysis on null image.");
		}
		int tempNumX = argImages[0].getNumX();
		int tempNumY = argImages[0].getNumY();
		int tempNumZ = argImages[0].getNumZ();
		if (tempNumZ!=1){
			throw new RuntimeException("ImageDataset sub-images must be 2D");
		}
		for (int i = 1; i < argImages.length; i++) {
			ImageType img = argImages[i];
			if (img.getNumX()!=tempNumX || img.getNumY()!=tempNumY || img.getNumZ()!=tempNumZ){
				throw new RuntimeException("ImageDataset sub-images not same dimension");
			}
		}
		int numTimes = (argTimeStamps!=null && argTimeStamps.length>0)?(argTimeStamps.length):(1);
		int expectedNumberOfImages = argNumZ * numTimes;
		if (expectedNumberOfImages != argImages.length) {
			throw new RuntimeException("incorrect number of images ("+argImages.length+") doesn't match numZ ("+tempNumZ+") * numTimes ("+numTimes+")");
		}

		// Now initialize
		images = argImages;
		imageTimeStamps = argTimeStamps;
		this.numZ = argNumZ;
	}
	
	public Class<ImageType> getType(){
		return type;
	}
	
	public Extent getExtent(){
		return images[0].getExtent();
	}
	
	public void setExtent(Extent extent){
		for (int i = 0; i < images.length; i++) {
			images[i].setExtent(extent);
		}
	}
	
	public ISize getISize(){
		return new ISize(images[0].getNumX(),images[0].getNumY(),getSizeZ());
	}	


/**
 * Insert the method's description here.
 * Creation date: (1/24/2007 4:19:16 PM)
 * @return java.lang.Float
 */
public ImageType[] getAllImages() {
	return images;
}

public ImageStatistics getImageStatistics() {
	ImageStatistics allImageStatistics = new ImageStatistics();
	for (int i = 0; i < getAllImages().length; i++) {
		ImageStatistics imageStatistics  = getAllImages()[i].getImageStatistics();
		if(i==0){
			allImageStatistics.maxValue = imageStatistics.maxValue;
			allImageStatistics.minValue = imageStatistics.minValue;
			allImageStatistics.meanValue = imageStatistics.meanValue/getAllImages().length;
		}else{
			allImageStatistics.maxValue = Math.max(allImageStatistics.maxValue, imageStatistics.maxValue);
			allImageStatistics.minValue = Math.min(allImageStatistics.minValue, imageStatistics.minValue);
			allImageStatistics.meanValue+= imageStatistics.meanValue/getAllImages().length;
		}
	}
	return allImageStatistics;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2007 4:33:01 PM)
 * @return float[]
 */
public double[] getImageTimeStamps() {
	return imageTimeStamps;
}


public int getSizeZ() {
	return numZ;
}

public int getSizeC() {
	return 1; // until we support "channels"
}

public int getSizeT() {
	return (imageTimeStamps!=null)?(imageTimeStamps.length):(1);
}

public int[] getZCT(int index) {
	int zIndex = index%(getSizeZ());
	int cIndex = 0; // until we support "channels"
	int tIndex = (index-zIndex*1 - cIndex*getSizeZ())/getSizeT();
	return new int[] { zIndex, cIndex, tIndex };
}

public int getIndexFromZCT(int z, int c, int t) {
	return z+c*getSizeZ()+t*getSizeZ()*getSizeC();
}

public ImageType getImage(int z, int c, int t){
	int index = getIndexFromZCT(z, c, t);
	return images[index];
}

public int getIndexFromZCT(int[] zctArray) {
	return getIndexFromZCT(zctArray[0], zctArray[1], zctArray[2]);
}


public Rectangle getNonzeroBoundingRectangle() throws ImageException {
	Rectangle wholeBoundingRect = null;
	for (int i = 0; i < images.length; i++) {
		Rectangle boundingRect = images[i].getNonzeroBoundingBox();
		if(boundingRect != null){
			if(wholeBoundingRect == null){
				wholeBoundingRect = boundingRect;
			}else{
				wholeBoundingRect = wholeBoundingRect.union(boundingRect);
			}
		}
	}
	return wholeBoundingRect;
}

public Rectangle getNonzeroBoundingRectangle(int channel,int time) throws ImageException {
	Rectangle wholeBoundingRect = null;
	for (int z = 0; z < getSizeZ(); z++) {
		Rectangle boundingRect = images[getIndexFromZCT(z,channel,time)].getNonzeroBoundingBox();
		if(boundingRect != null){
			if(wholeBoundingRect == null){
				wholeBoundingRect = boundingRect;
			}else{
				wholeBoundingRect = wholeBoundingRect.union(boundingRect);
			}
		}
	}
	return wholeBoundingRect;
}

public boolean compareEqual(Matchable obj) {
	if(this == obj){
		return true;
	}
	if(obj instanceof ImageTimeSeries<?>){
		ImageTimeSeries compareToImageDataset = (ImageTimeSeries)obj;
		if(numZ == compareToImageDataset.numZ){
			if(Compare.isEqualOrNull(imageTimeStamps, compareToImageDataset.imageTimeStamps)){
				if(Compare.isEqualOrNull(images, compareToImageDataset.images)){
					return true;
				}
			}
		}
	}
	return false;
}

}

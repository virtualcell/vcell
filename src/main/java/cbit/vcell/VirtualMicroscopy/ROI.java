/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.VirtualMicroscopy;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Vector;

import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Matchable;
import org.vcell.util.Origin;

import cbit.image.ImageException;

// This represents the ROI - Region Of Interest in a given image : NOTE : subject to change!!
/**
 */
public class ROI implements Matchable {
	// This specifies the region of interest in an image/stack - contains all the images in the stack
	// Hence storing it in FloatImage. The extent/numX, numY, numZ will be smaller than the original image.
	// UShortImage used as 2D here, with Z slices in the array. (not related to T, which imageDataset has to store)
	//
	// nonzero pixel values are considered part of the ROI, zero pixels are not.
	//
	private UShortImage[] roiImages = null;
	private String roiName;
	
	public ROI(ROI origROI) throws ImageException{
		super();
		this.roiName = origROI.roiName;
		this.roiImages = new UShortImage[(origROI.roiImages != null?origROI.roiImages.length:0)];
		for (int i = 0; i < this.roiImages.length; i++) {
			this.roiImages[i] = new UShortImage(origROI.roiImages[i]);
		}
	}

	/**
	 * Constructor for 2D or 3D ROI.  for 3D, each sub-image must be same dimension
	 * and numZ should be 1.
	 * @param argRoiImages
	 * @param argROIType
	 */
	public ROI(UShortImage[] argRoiImages, String argROIName) {
		super();
		init(argRoiImages,argROIName);
	}	

	private void init(UShortImage[] argRoiImages, String argROIName){
		int numX = argRoiImages[0].getNumX();
		int numY = argRoiImages[0].getNumY();
		int numZ = argRoiImages[0].getNumZ();
		if (numZ!=1){
			throw new RuntimeException("ROI sub-images must be 2D");
		}
		for (int i = 1; i < argRoiImages.length; i++) {
			UShortImage img = argRoiImages[i];
			if (img.getNumX()!=numX || img.getNumY()!=numY || img.getNumZ()!=numZ){
				throw new RuntimeException("ROI sub-images not same dimension");
			}
		}
		this.roiImages = argRoiImages;
		this.roiName = argROIName;

	}
	/**
	 * Constructor for 2D ROI
	 * @param argRoiImage
	 * @param argROIType
	 */
	public ROI(UShortImage argRoiImage, String argROIName){
		super();
		UShortImage[] roiSubImages = new UShortImage[] { argRoiImage };
		if(argRoiImage.getNumZ() > 1){
			try{
				roiSubImages = new UShortImage[argRoiImage.getNumZ()];
				Extent newExtent =
					new Extent(argRoiImage.getExtent().getX(),
						argRoiImage.getExtent().getY(),
						argRoiImage.getExtent().getZ()/argRoiImage.getNumZ());
				for (int z = 0; z < argRoiImage.getNumZ(); z++) {
					short[] slicePixels = new short[argRoiImage.getNumX()*argRoiImage.getNumY()];
					System.arraycopy(argRoiImage.getPixels(), z*slicePixels.length, slicePixels, 0, slicePixels.length);
					Origin newOrigin =
						new Origin(argRoiImage.getOrigin().getX(),argRoiImage.getOrigin().getY(),
								Image.calcOriginPosition(argRoiImage.getOrigin().getZ(), z, newExtent.getZ(), argRoiImage.getNumZ()));
					UShortImage uShortImage =
						new UShortImage(slicePixels,newOrigin,newExtent,argRoiImage.getNumX(),argRoiImage.getNumY(),1);
					roiSubImages[z] = uShortImage;
				}
			}catch(Exception e){
				e.printStackTrace();
				throw new RuntimeException("Error ROI constructor. "+e.getMessage(),e);
			}
		}
		init(roiSubImages,argROIName);
//		if (argRoiImage.getNumZ()!=1){
//			throw new RuntimeException("ROI sub-images must be 2D");
//		}
//		this.roiImages = new UShortImage[] { argRoiImage };
//		this.roiType = argROIType;
	}	
	/**
	 * @return the roiImage
	 */
	public UShortImage[] getRoiImages() {
		return roiImages;
	}
	//copy ROI images when they are the exactly same size.
	public void copyROIImages(UShortImage[] argImages)
	{
		UShortImage[] localImages = getRoiImages();
		if(localImages.length == argImages.length)
		{
			for(int i=0; i<localImages.length; i++)
			{
				localImages[i].copyImage(argImages[i]);
			}
		}
	}
	
	public void setROIImages(UShortImage[] argImages)
	{
		roiImages = argImages;
	}
	/**
	 * Method getISize.
	 * @return ISize
	 */
	public ISize getISize(){
		return new ISize(roiImages[0].getNumX(),roiImages[0].getNumY(),roiImages.length);
	}
	
	/**
	 * Method getBinaryPixelsXYZ.
	 * @param threshold int
	 * @return short[]
	 */
	public short[] getBinaryPixelsXYZ(int threshold){
		// note changing in place data returned by getPixelsXYZ()
		short[] pixels = new short[getISize().getXYZ()];
		for (int zIndex = 0; zIndex < roiImages.length; zIndex++) {
			short[] slicePixels = roiImages[zIndex].getBinaryPixels(threshold);
			System.arraycopy(slicePixels, 0, pixels, zIndex*slicePixels.length, slicePixels.length);
		}
		return pixels;
	}

	/**
	 * Method getPixelsXYZ.
	 * @return short[]
	 */
	public short[] getPixelsXYZ(){
		// always returns a copy
		short[] pixels = new short[getISize().getXYZ()];
		for (int zIndex = 0; zIndex < roiImages.length; zIndex++) {
			short[] slicePixels = roiImages[zIndex].getPixels();
			System.arraycopy(slicePixels, 0, pixels, zIndex*slicePixels.length, slicePixels.length);
		}
		return pixels;
	}
	
	public String getROIName(){
		return roiName;
	}
	
	public void setRoiName(String roiName) {
		this.roiName = roiName;
	}

	/**
	 * Method crop.
	 * @param rect Rectangle
	 * @return ROI
	 * @throws ImageException
	 */
	public ROI crop(Rectangle rect) throws ImageException {
		UShortImage[] croppedImages = new UShortImage[roiImages.length];
		for (int i = 0; i < croppedImages.length; i++) {
			croppedImages[i] = roiImages[i].crop(rect);
		}
		return new ROI(croppedImages,roiName);
	}
	
	/**
	 * Method clear.
	 * @param value short
	 */
	public void clear(short value) {
		for (int i = 0; i < roiImages.length; i++) {
			Arrays.fill(roiImages[i].getPixels(),value);
		}
	}
	
	/*public boolean isAllPixelsZero()
	{
		UShortImage[] images = getRoiImages();
		for(int i=0; i<images.length; i++)
		{
			if(images[i].getNonzeroIndices().length >0 )
			{
				return false;
			}
		}
		return true;
	}*/

	public int getNonzeroPixelsCount()
	{
		UShortImage[] images = getRoiImages();
		int totalPixelsCount = 0;
		for(int i=0; i<images.length; i++)
		{
			totalPixelsCount += images[i].getNonzeroIndices().length;
		}
		return totalPixelsCount;
	}
	
	public static void fillAtPoint(int startX, int startY,BufferedImage roiImage,int boundaryColor){
			if(startX <0 ||startX>=roiImage.getWidth() || startY <0 ||startY>=roiImage.getHeight() ){
				return;
			}
			int TARGET_COLOR = roiImage.getRGB(startX,startY);
			if(TARGET_COLOR == boundaryColor){
				return;
			}
			int XINDEX = 0;
			int YINDEX = 1;
			Vector<int[]> pendingChecks = new Vector<int[]>();
			pendingChecks.add(new int[] {startX,startY});
			roiImage.setRGB(startX, startY, boundaryColor);
			while(pendingChecks.size() > 0){
				int[] data = pendingChecks.remove(0);
					if(data[XINDEX]-1 >= 0 && roiImage.getRGB(data[XINDEX]-1, data[YINDEX]) == TARGET_COLOR){
						pendingChecks.add(new int[] {data[XINDEX]-1,data[YINDEX]});
						roiImage.setRGB(data[XINDEX]-1, data[YINDEX], boundaryColor);
					}
					if(data[YINDEX]-1 >= 0 && roiImage.getRGB(data[XINDEX], data[YINDEX]-1) == TARGET_COLOR){
						pendingChecks.add(new int[] {data[XINDEX],data[YINDEX]-1});
						roiImage.setRGB(data[XINDEX], data[YINDEX]-1, boundaryColor);
					}
					if(data[XINDEX]+1 < roiImage.getWidth() && roiImage.getRGB(data[XINDEX]+1, data[YINDEX]) == TARGET_COLOR){
						pendingChecks.add(new int[] {data[XINDEX]+1,data[YINDEX]});
						roiImage.setRGB(data[XINDEX]+1, data[YINDEX], boundaryColor);
					}
					if(data[YINDEX]+1 < roiImage.getHeight() && roiImage.getRGB(data[XINDEX], data[YINDEX]+1) == TARGET_COLOR){
						pendingChecks.add(new int[] {data[XINDEX],data[YINDEX]+1});
						roiImage.setRGB(data[XINDEX], data[YINDEX]+1, boundaryColor);
					}
			}
	
	}

	public static Point findInternalVoid(ROI roi){
		if(roi.getISize().getZ() > 1){
			throw new IllegalArgumentException(ROI.class.getName()+".findInternalVoid: Only 2D ROIs allowed");
		}
		int xSize = roi.getISize().getX();
		int ySize = roi.getISize().getY();
		BufferedImage roiImage = UShortImage.createUnsignedBufferedImage(roi.getPixelsXYZ(),xSize,ySize);
		Integer boundaryColor = null;
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				if((0x00FFFFFF&roiImage.getRGB(x, y)) != 0){
					boundaryColor = new Integer(roiImage.getRGB(x, y));
					break;
				}
			}
			if(boundaryColor != null){
				break;
			}
		}
		if(boundaryColor == null){
			return null;
		}
		//fill inward from edges
		for (int x = 0; x < xSize; x++) {
			ROI.fillAtPoint(x, 0, roiImage, boundaryColor);
			ROI.fillAtPoint(x, ySize-1, roiImage, boundaryColor);
		}
		for (int y = 0; y < ySize; y++) {
			ROI.fillAtPoint(0, y, roiImage, boundaryColor);
			ROI.fillAtPoint(xSize-1,y, roiImage, boundaryColor);		
		}
		//Any non-zero now is an internal void
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				if((0x00FFFFFF&roiImage.getRGB(x, y)) == 0){
					return new Point(x,y);
				}
			}
		}
		return null;
	}
	
	public static Point[] checkContinuity(ROI roi){
		if(roi.getISize().getZ() > 1){
			throw new IllegalArgumentException(ROI.class.getName()+".findDistinctAreas: Only 2D ROIs allowed");
		}
		int xSize = roi.getISize().getX();
		int ySize = roi.getISize().getY();
		BufferedImage roiImage = UShortImage.createUnsignedBufferedImage(roi.getPixelsXYZ(),xSize,ySize);
		Integer boundaryColor = null;
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				if((0x00FFFFFF&roiImage.getRGB(x, y)) == 0){
					boundaryColor = new Integer(roiImage.getRGB(x, y));
					break;
				}
			}
			if(boundaryColor != null){
				break;
			}
		}
		if(boundaryColor == null){
			return null;
		}
		Point firstLocation = null;
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				if(roiImage.getRGB(x, y) != boundaryColor.intValue()){
					if(firstLocation != null){
						return new Point[] {firstLocation,new Point(x,y)};
					}
					firstLocation = new Point(x,y);
					ROI.fillAtPoint(x, y, roiImage, boundaryColor.intValue());
				}
			}
		}
		return null;
	}

	public boolean compareEqual(Matchable obj) 
	{
		if (this == obj) {
			return true;
		}
		if (obj != null && obj instanceof ROI) 
		{
			ROI roi = (ROI) obj;
			if (!org.vcell.util.Compare.isEqualOrNull(getRoiImages(), roi.getRoiImages())){
				return false;
			}
			if (!getROIName().equals(roi.getROIName())){
				return false;
			}
			return true;
		}
		return false;
	}
}

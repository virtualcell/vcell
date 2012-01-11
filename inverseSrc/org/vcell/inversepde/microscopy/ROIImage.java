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
import java.util.ArrayList;

import org.vcell.util.Compare;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Matchable;
import org.vcell.util.Origin;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class ROIImage implements Matchable {
	
	public final static String ROI_OLD_CELL = "ROI_CELL";
	public final static String ROI_OLD_BLEACHED = "ROI_BLEACHED";
	public final static String ROI_OLD_BACKGROUND = "ROI_BACKGROUND";
	public final static String ROI_OLD_BLEACHED_RING1 = "ROI_BLEACHED_RING1";
	public final static String ROI_OLD_BLEACHED_RING2 = "ROI_BLEACHED_RING2";
	public final static String ROI_OLD_BLEACHED_RING3 = "ROI_BLEACHED_RING3";
	public final static String ROI_OLD_BLEACHED_RING4 = "ROI_BLEACHED_RING4";
	public final static String ROI_OLD_BLEACHED_RING5 = "ROI_BLEACHED_RING5";
	public final static String ROI_OLD_BLEACHED_RING6 = "ROI_BLEACHED_RING6";
	public final static String ROI_OLD_BLEACHED_RING7 = "ROI_BLEACHED_RING7";
	public final static String ROI_OLD_BLEACHED_RING8 = "ROI_BLEACHED_RING8";
	
	
	
	public final static String ROI_BLEACHED			= "bleached";
	public final static String ROI_BLEACHEDCELL		= "bleachedcell";
	public final static String ROI_BACKGROUND		= "background";
	public final static String ROI_CELL				= "cell";
	public final static String ROI_NUCLEUS			= "nucleus";
	public final static String ROI_EXTRACELLULAR	= "extracellular";
	public final static String ROI_BLEACHED_RING1	= "ring1";
	public final static String ROI_BLEACHED_RING2	= "ring2";
	public final static String ROI_BLEACHED_RING3	= "ring3";
	public final static String ROI_BLEACHED_RING4	= "ring4";
	public final static String ROI_BLEACHED_RING5	= "ring5";
	public final static String ROI_BLEACHED_RING6	= "ring6";
	public final static String ROI_BLEACHED_RING7	= "ring7";
	public final static String ROI_BLEACHED_RING8	= "ring8";
	
	public final static String[] ROI_NAMES = {
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
	};
	
	public final static String ROIIMAGE_SEGMENTATION = "segmentation";
	public final static String ROIIMAGE_PROTOCOL     = "protocol";
	public final static String ROIIMAGE_IMAGEROIS    = "imagerois";
	public final static String ROIIMAGE_BASIS		 = "basis";
	
	private UShortImage maskImage = null;
	private ArrayList<ROIImageComponent> rois = new ArrayList<ROIImageComponent>();
	private String name = null;
	private short backgroundColor = 0;
	
	public class ROIImageComponent implements Matchable {
		private final short pixelValue;
		private final String name;
		private ROIImageComponent(short argPixelValue, String argName){
			if (argName==null || argName.length()==0){
				throw new IllegalArgumentException("ROI name cannot be empty");
			}
			pixelValue = argPixelValue;
			name = argName;
		}
		public short getPixelValue(){
			return pixelValue;
		}
		public String getName(){
			return name;
		}
		public int countPixels(){
			return (int)maskImage.countPixelsByValue(pixelValue);
		}
		public ROIImage getROIImage(){
			return ROIImage.this;
		}
		public short[] getPixelsXYZ(){
			short[] maskPixels = maskImage.getPixels();
			short[] roiPixels = new short[maskPixels.length];
			for (int i = 0; i < roiPixels.length; i++) {
				if (maskPixels[i] == pixelValue){
					roiPixels[i] = 1;
				}
			}
			return roiPixels;
		}
		public ISize getISize(){
			return getROIImage().getISize();
		}
		public int[] getIndices(){
			int[] indices = new int[countPixels()];
			int count = 0;
			short[] pixels = maskImage.getPixels();
			for (int i = 0; i < pixels.length; i++) {
				if (pixels[i] == pixelValue){
					indices[count++] = i;
				}
			}
			return indices;
		}
		public UShortImage makeBinaryImage() throws ImageException {
	   		UShortImage image = new UShortImage(getPixelsXYZ(),maskImage.getOrigin(),maskImage.getExtent(),maskImage.getNumX(),maskImage.getNumY(),maskImage.getNumZ());
	   		return image;
		}
		public void setPixel(int index){
			maskImage.getPixels()[index] = pixelValue;
		}
		public void setPixels(UShortImage image){
			short[] pixels = image.getPixels();
			short[] maskPixels = maskImage.getPixels();
			if (pixels.length!=maskPixels.length){
				throw new RuntimeException("images not the same size");
			}
			for (int i = 0; i < pixels.length; i++) {
				if (pixels[i] != 0){
					maskPixels[i] = pixelValue;
				}
			}
		}
		public void setPixels(short[] pixels){
			short[] maskPixels = maskImage.getPixels();
			if (pixels.length!=maskPixels.length){
				throw new RuntimeException("images not the same size");
			}
			for (int i = 0; i < pixels.length; i++) {
				if (pixels[i] != 0){
					maskPixels[i] = pixelValue;
				}
			}
		}
		public boolean isPixelInside(int index){
			return maskImage.getPixels()[index] == pixelValue;
		}
		public boolean isAllPixelsZero(){
			short[] pixels = maskImage.getPixels();
			for (int i = 0; i < pixels.length; i++) {
				if (pixels[i]==pixelValue){
					return false;
				}
			}
			return true;
		}
		public void clearPixel(int index){
			maskImage.getPixels()[index] = backgroundColor;
		}
		public void clear(){
			short[] pixels = maskImage.getPixels();
			for (int i = 0; i < pixels.length; i++) {
				if (pixels[i]==pixelValue){
					pixels[i] = backgroundColor;
				}
			}
		}
		public boolean compareEqual(Matchable obj) {
			if (obj instanceof ROIImageComponent){
				ROIImageComponent rOIImageComponent = (ROIImageComponent)obj;
				if (!getName().equals(rOIImageComponent.getName())){
					return false;
				}
				if (getPixelValue() != rOIImageComponent.getPixelValue()){
					return false;
				}
				return true;
			}
			return false;
		}
	}
	
	public ROIImage(String argName,ISize size,Extent extent,Origin origin) throws ImageException {
		maskImage = new UShortImage(new short[size.getXYZ()],origin,extent,size.getX(),size.getY(),size.getZ());
		this.name = argName;
	}
	
	public ROIImage(String argName, UShortImage ushortImage){
		maskImage = ushortImage;
		this.name = argName;
	}
	
	public ROIImage(ROIImage roiImage) throws ImageException {
		maskImage = new UShortImage(roiImage.maskImage);
		rois = new ArrayList<ROIImageComponent>(roiImage.rois);
		name = roiImage.name;
		backgroundColor = roiImage.backgroundColor;
	}
	
	public ROIImageComponent[] getROIs(){
		return rois.toArray(new ROIImageComponent[rois.size()]);
	}
	
	public UShortImage getMaskImage(){
		return maskImage;
	}
	
	public ROIImageComponent addROI(short pixelValue, String name){
		for (int i = 0; i < rois.size(); i++) {
			if (pixelValue == rois.get(i).getPixelValue()){
				throw new RuntimeException("ROI with pixel value "+pixelValue+" already exists");
			}
			if (name.equals(rois.get(i).getName())){
				throw new RuntimeException("ROI with name "+name+" already exists");
			}
		}
		ROIImageComponent newROI = new ROIImageComponent(pixelValue,name);
		rois.add(newROI);
		return newROI;
	}
	
	public void removeROI(ROIImageComponent rOIImageComponent){
		if (rois.contains(rOIImageComponent)){
			rois.remove(rOIImageComponent);
			short[] maskPixels = maskImage.getPixels();
			for (int i = 0; i < maskPixels.length; i++) {
				if (maskPixels[i] == rOIImageComponent.pixelValue){
					maskPixels[i] = backgroundColor;
				}
			}
		}else{
			throw new RuntimeException("ROI not found");
		}
	}
	
	public ROIImageComponent getROI(String name){
		for (int i = 0; i < rois.size(); i++) {
			if (rois.get(i).getName().equals(name)){
				return rois.get(i);
			}
		}
		return null;
	}
	
	public ROIImageComponent getROI(short pixelValue){
		for (int i = 0; i < rois.size(); i++) {
			if (rois.get(i).pixelValue == pixelValue){
				return rois.get(i);
			}
		}
		return null;
	}
	
	public ISize getISize(){
		return new ISize(maskImage.getNumX(),maskImage.getNumY(),maskImage.getNumZ());
	}

	public boolean compareEqual(Matchable obj) {
		if (obj instanceof ROIImage){
			ROIImage roiImage = (ROIImage)obj;
			if (!maskImage.compareEqual(roiImage.maskImage)){
				return false;
			}
			if (!Compare.isEqual(rois.toArray(new ROIImageComponent[rois.size()]), roiImage.rois.toArray(new ROIImageComponent[roiImage.rois.size()]))){
				return false;
			}
			if (!name.equals(roiImage.name)){
				return false;
			}
			if (backgroundColor!=roiImage.backgroundColor){
				return false;
			}
			return true;
		}
		return false;
	}
	
	public ROIImage crop(Rectangle rect) throws ImageException {
		ROIImage roiImage = new ROIImage(this);
		roiImage.maskImage = maskImage.crop(rect);
		return roiImage;
	}

	public String getName() {
		return name;
	}

	public short getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(short backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	

}

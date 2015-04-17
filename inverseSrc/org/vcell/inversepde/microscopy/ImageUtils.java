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

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.BitSet;

import javax.media.jai.BorderExtender;
import javax.media.jai.KernelJAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.AddConstDescriptor;
import javax.media.jai.operator.AndDescriptor;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.DilateDescriptor;
import javax.media.jai.operator.ErodeDescriptor;
import javax.media.jai.operator.ExtremaDescriptor;
import javax.media.jai.operator.LookupDescriptor;

import org.vcell.util.Extent;
import org.vcell.util.Origin;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class ImageUtils {

	public static KernelJAI createCircularBinaryKernel(int radius){
		int enclosingBoxSideLength = radius*2+1;
		float[] kernalData = new float[enclosingBoxSideLength*enclosingBoxSideLength];
		Point2D kernalPoint = new Point2D.Float(0f,0f);
		int index = 0;
		for (int y = -radius; y <= radius; y++) {
			for (int x = -radius; x <= radius; x++) {
				if(kernalPoint.distance(x, y) <= radius){
					kernalData[index] = 1.0f;
				}
				index++;
			}
		}
		return new KernelJAI(enclosingBoxSideLength,enclosingBoxSideLength,radius,radius,kernalData);
	}

	public static PlanarImage binarize(BufferedImage source){
		PlanarImage planarSource = PlanarImage.wrapRenderedImage(source);
		double[][] minmaxArr = (double[][])ExtremaDescriptor.create(planarSource, null, 1, 1, false, 1,null).getProperty("extrema");
		short[] lookupData = new short[(int)0x010000];
		lookupData[(int)minmaxArr[1][0]] = 1;
		LookupTableJAI lookupTable = new LookupTableJAI(lookupData,true);
		planarSource = LookupDescriptor.create(planarSource, lookupTable, null).createInstance();
		return planarSource;		
	}

	public static UShortImage convertToUShortImage(PlanarImage source,Origin origin, Extent extent) throws ImageException{
		short[] shortData = new short[source.getWidth() * source.getHeight()];
		source.getData().getDataElements(0, 0, source.getWidth(),source.getHeight(), shortData);
		return new UShortImage(shortData,origin,extent,source.getWidth(),source.getHeight(),1);	
	}

	public static UShortImage erodeDilate(UShortImage source,KernelJAI dilateErodeKernel,UShortImage mask,boolean bErode) throws ImageException{
			PlanarImage completedImage = null;
			PlanarImage operatedImage = null;
			PlanarImage planarSource = UShortImage.binarize(source);
			Integer borderPad = dilateErodeKernel.getWidth()/2;
			planarSource = 
				BorderDescriptor.create(planarSource,
					borderPad, borderPad, borderPad, borderPad,
					BorderExtender.createInstance(BorderExtender.BORDER_ZERO), null).createInstance();
			if(bErode){
				planarSource = AddConstDescriptor.create(planarSource, new double[] {1.0}, null).createInstance();
		    	RenderedOp erodeOP = ErodeDescriptor.create(planarSource, dilateErodeKernel, null);
		    	operatedImage = erodeOP.createInstance();
				
			}else{
		    	RenderedOp dilationOP = DilateDescriptor.create(planarSource, dilateErodeKernel, null);
		    	operatedImage = dilationOP.createInstance();
			}
			operatedImage =
	    		CropDescriptor.create(operatedImage,
	    			new Float(0), new Float(0),
	    			new Float(source.getNumX()), new Float(source.getNumY()), null);
	    	operatedImage = binarize(operatedImage.getAsBufferedImage());
			if (mask != null) {
				RenderedOp andDescriptor = AndDescriptor.create(operatedImage,UShortImage.binarize(mask), null);
				completedImage = andDescriptor.createInstance();
			}else{
				completedImage = operatedImage;
			}
			return convertToUShortImage(completedImage, source.getOrigin(), source.getExtent());
	    	
	}

	//	private void writeUShortFile(UShortImage uShortImage,File outFile){
	//		writeBufferedImageFile(
	//			ImageTools.makeImage(uShortImage.getPixels(),uShortImage.getNumX(), uShortImage.getNumY()),outFile);
	//
	//	}
	//	private void writeBufferedImageFile(BufferedImage bufferedImage,File outFile){
	//		try{
	//		ImageIO.write(
	//			FormatDescriptor.create(bufferedImage, DataBuffer.TYPE_BYTE,null).createInstance(),
	//			"bmp", outFile);
	//		}catch(Exception e){
	//			e.printStackTrace();
	//		}
	//		
	//	}
		public static UShortImage fastDilate(UShortImage dilateSource,int radius,UShortImage mask) throws ImageException{
			short[] sourcePixels = dilateSource.getPixels();
			short[] targetPixels = dilateSource.getPixels().clone();
			KernelJAI dilateKernel = createCircularBinaryKernel(radius);
			float[] kernelData = dilateKernel.getKernelData();
			BitSet kernelBitSet = new BitSet(kernelData.length);
			for (int i = 0; i < kernelData.length; i++) {
				if(kernelData[i] == 1.0f){
					kernelBitSet.set(i);
				}
			}
			boolean bNeedsFill = false;
			for (int y = 0; y < dilateSource.getNumY(); y++) {
				int yOffset = y*dilateSource.getNumX();
				int yMinus = yOffset-dilateSource.getNumX();
				int yPlus = yOffset+dilateSource.getNumX();
				for (int x = 0; x < dilateSource.getNumX(); x++) {
					bNeedsFill = false;
					if(sourcePixels[x+yOffset] != 0){
						if(x-1 >= 0 && sourcePixels[(x-1)+yOffset] == 0){
							bNeedsFill = true;
						}else
						if(y-1 >= 0 && sourcePixels[x+yMinus] == 0){
							bNeedsFill = true;
						}else
						if(x+1 < dilateSource.getNumX() && sourcePixels[(x+1)+yOffset] == 0){
							bNeedsFill = true;
						}else
						if(y+1 < dilateSource.getNumY() && sourcePixels[x+yPlus] == 0){
							bNeedsFill = true;
						}
						if(bNeedsFill){
							int masterKernelIndex = 0;
							for (int y2 = y-radius; y2 <= y+radius; y2++) {
								if(y2>= 0 && y2 <dilateSource.getNumY()){
									int kernelIndex = masterKernelIndex;
									int targetYIndex = y2*dilateSource.getNumX();
									for (int x2 = x-radius; x2 <= x+radius; x2++) {
										if(kernelBitSet.get(kernelIndex) &&
											x2>= 0 && x2 <dilateSource.getNumX()){
											targetPixels[targetYIndex+x2] = 1;
										}
										kernelIndex++;
									}
								}
								masterKernelIndex+= dilateKernel.getWidth();
							}
						}
					}
				}
			}
			UShortImage resultImage =
				new UShortImage(targetPixels,
						dilateSource.getOrigin(),
						dilateSource.getExtent(),
						dilateSource.getNumX(),
						dilateSource.getNumY(),
						dilateSource.getNumZ());
			resultImage.and(mask);
			return resultImage;
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

}

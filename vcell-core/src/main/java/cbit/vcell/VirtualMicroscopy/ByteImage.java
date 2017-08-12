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

import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;

import org.vcell.util.Origin;

import cbit.image.ImageException;
/**
 * This type was created in VisualAge.
 */
public class ByteImage extends Image {
	private byte pixels[] = null;
/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 */
public ByteImage(ByteImage image) throws ImageException {
	super(image);
	this.pixels = image.getPixels();
}
/**
 * This method was created in VisualAge.
 * @param pix byte[]
 * @param x int
 * @param y int
 * @param z int
 * @param name java.lang.String
 * @param annot java.lang.String
 */
public ByteImage(byte pixels[], Origin aOrigin, org.vcell.util.Extent aExtent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	super(aOrigin, aExtent, aNumX, aNumY, aNumZ);
	if (aNumX*aNumY*aNumZ != pixels.length){
		throw new IllegalArgumentException("size ("+aNumX+","+aNumY+","+aNumZ+") not consistent with "+pixels.length+" pixels");
	}
	this.pixels = pixels;
}
/**
 * This method was created in VisualAge.
 * @param pix byte[]
 * @param x int
 * @param y int
 * @param z int
 * @param name java.lang.String
 * @param annot java.lang.String
 */
public ByteImage(int sourceValues[], Origin aOrigin, org.vcell.util.Extent aExtent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	super(aOrigin, aExtent, aNumX, aNumY, aNumZ);
	if (aNumX*aNumY*aNumZ != sourceValues.length){
		throw new IllegalArgumentException("size ("+aNumX+","+aNumY+","+aNumZ+") not consistent with "+sourceValues.length+" pixels");
	}

	
	byte[] convertedPixels = new byte[sourceValues.length];
	int uniqueValuesArray[] = new int[0];

	for (int i=0;i<sourceValues.length;i++){
		int currPixel = sourceValues[i];

		//
		// look for current value in list
		//
		int uniqueValue = 0;
		boolean found = false;
		for (int j=0;j<uniqueValuesArray.length;j++){
			if (uniqueValuesArray[j]==currPixel){
				found = true;
				uniqueValue = j;
			}
		}
		//
		// if current value not found, extend list and add value to end
		//
		if (!found){
			int newArray[] = new int[uniqueValuesArray.length+1];
			System.arraycopy(uniqueValuesArray,0,newArray,0,uniqueValuesArray.length);
			newArray[newArray.length-1] = currPixel;
			uniqueValuesArray = newArray;
			//
			uniqueValue = (uniqueValuesArray.length-1);
		}

		convertedPixels[i] = (byte)uniqueValue;
	}
	
	this.pixels = convertedPixels;
}
public byte getPixel(int x, int y, int z) throws ImageException {
	if (x<0||x>=getNumX()||y<0||y>=getNumY()||z<0||z>=getNumZ()){
		throw new IllegalArgumentException("("+x+","+y+","+z+") is not inside (0,0,0) and ("+(getNumX()-1)+","+(getNumY()-1)+","+(getNumZ()-1)+")");
	}
	int index = x + getNumX()*(y + z*getNumY()); 
	return (byte) getPixels()[index];
}
/**
 * getPixels method comment.
 */
public byte[] getPixels() {
	return pixels;
}

public Object getPixelArray(){
	return getPixels();
}

@Override
public Rectangle getNonzeroBoundingBox() {
	int minX = Integer.MAX_VALUE;
	int maxX = 0;
	int minY = Integer.MAX_VALUE;
	int maxY = 0;
	for (int i = 0; i < getNumX(); i++) {
		for (int j = 0; j < getNumY(); j++) {
			if (pixels[i+getNumX()*j]!=0){
				minX = Math.min(minX, i);
				maxX = Math.max(maxX, i);
				minY = Math.min(minY, j);
				maxY = Math.max(maxY, j);
			}
		}
	}
	return new Rectangle(minX,minY,maxX-minX+1,maxY-minY+1);
}

@Override
public int[] getNonzeroIndices() {
	int[] buffer = new int[pixels.length];
	int count=0;
	for (int i = 0; i < pixels.length; i++) {
		if (pixels[i]!=0){
			buffer[count++] = i;
		}
	}
	int[] nonzeroIndices = new int[count];
	System.arraycopy(buffer, 0, nonzeroIndices, 0, count);
	return nonzeroIndices;
}

/**
 * This method was created in VisualAge.
 * @return byte[]
 */
public byte[] getPixelsCompressed() throws ImageException {
	try {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DeflaterOutputStream dos = new DeflaterOutputStream(bos);
		//DeflaterOutputStream dos = new DeflaterOutputStream(bos,new Deflater(5,false));
		dos.write(pixels,0,pixels.length);
		dos.close();
		return bos.toByteArray();
	}catch (IOException e){
		e.printStackTrace(System.out);
		throw new ImageException(e.getMessage());
	}
}
/**
 * This method was created in VisualAge.
 * @return byte
 * @param x int
 * @param y int
 * @param z int
 */
public void setPixel(int x, int y, int z, byte newValue) throws ImageException {
	if (x<0||x>=getNumX()||y<0||y>=getNumY()||z<0||z>=getNumZ()){
		throw new IllegalArgumentException("("+x+","+y+","+z+") is not inside (0,0,0) and ("+(getNumX()-1)+","+(getNumY()-1)+","+(getNumZ()-1)+")");
	}
	int index = x + getNumX()*(y + z*getNumY()); 
	getPixels()[index] = newValue;
}


/**
 * This method was created in VisualAge.
 * @return int[]
 */
public int[] getUniquePixelValues() throws ImageException{
	byte pixels[] = getPixels();
	int imageLength = pixels.length;
	if (imageLength==0){
		return null;
	}
	
	int pixelValueArray[] = new int[1];
	pixelValueArray[0] = 0xff&(int)pixels[0];

	for (int i=0;i<imageLength;i++){
		int currPixel = 0xff&(int)pixels[i];

		//
		// look for current pixel in list
		//
		boolean found = false;
		for (int j=0;j<pixelValueArray.length;j++){
			if (pixelValueArray[j]==currPixel){
				found = true;
			}
		}
		//
		// if current pixel not found, extend list and add pixel to end
		//
		if (!found){
			int newArray[] = new int[pixelValueArray.length+1];
			for (int j=0;j<pixelValueArray.length;j++){
				newArray[j] = pixelValueArray[j];
			}
			newArray[pixelValueArray.length] = currPixel;
			pixelValueArray = newArray;
		}
	}
	return pixelValueArray;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.image.FileImage
 * @param images cbit.image.FileImage[]
 * @exception java.lang.Exception The exception description.
 */
public static ByteImage concatenateZSeries(ByteImage images[]) throws ImageException {
	if (images.length == 1){
		return images[0];
	}	
	int nX = images[0].getNumX();
	int nY = images[0].getNumY();
	int nZ = images[0].getNumZ();
	org.vcell.util.Extent extent0 = images[0].getExtent();
	for (int i=1;i<images.length;i++){
		if (images[i].getNumX() != nX){
			throw new ImageException("image "+(i+1)+" x dimension doesn't match the first image");
		}	
		if (images[i].getNumY() != nY){
			throw new ImageException("image "+(i+1)+" y dimension doesn't match the first image");
		}	
		if (images[i].getNumZ() < 1){
			throw new ImageException("image "+(i+1)+" z dimension must be at least 1");
		}	
		nZ += images[i].getNumZ();
	}
	int nTotal = nX*nY*nZ;
	byte bigBuffer[] = new byte[nTotal];
	int index = 0;
	for (int i=0;i<images.length;i++){
		byte currPix[] = images[i].getPixels();
		int currTotal = images[i].getNumX()*images[i].getNumY()*images[i].getNumZ();
		for (int j=0;j<currTotal;j++){
			bigBuffer[index++] = currPix[j];
		}
	}		
	ByteImage byteImage = new ByteImage(bigBuffer,images[0].getOrigin(), new org.vcell.util.Extent(extent0.getX(),extent0.getY(),extent0.getZ()*images.length),nX,nY,nZ);
	return byteImage;
}

public ByteImage crop(Rectangle rect) throws ImageException{
	return (ByteImage)Image.crop(this, rect);
}

@Override
public ImageStatistics getImageStatistics() {
	ImageStatistics stats = new ImageStatistics();
	stats.minValue = pixels[0];
	stats.maxValue = pixels[0];
	stats.meanValue = 0.0;
	stats.sum = 0.0;
	for (int i = 0; i < pixels.length; i++) {
		double value = 0xff&((int)pixels[i]);
		stats.minValue = Math.min(stats.minValue,value);
		stats.maxValue = Math.max(stats.maxValue,value);
		stats.sum += value;
	}
	stats.meanValue = stats.sum/pixels.length;
	
	return stats;
}

@Override
public void reverse() {
	int minValue = (0xff&((int)pixels[0]));
	int maxValue = (0xff&((int)pixels[0]));
	for (int i = 1; i < pixels.length; i++) {
		int value = 0xff&((int)pixels[i]);
		minValue = Math.min(minValue, value);
		maxValue = Math.min(maxValue, value);
	}
	for (int i = 0; i < pixels.length; i++) {
		pixels[i] = (byte)((int)(maxValue - ((0xff&((int)pixels[0]))-minValue)));
	}
	System.out.println("reversing image, pixel values in range ["+minValue+","+maxValue+"]");
}


@Override
public double[] getDoublePixels() {
	double[] doublePixels = new double[pixels.length];
	for (int i=0;i<doublePixels.length;i++){
		doublePixels[i] = pixels[i];
	}
	return doublePixels;
}

@Override
public float[] getFloatPixels() {
	float[] floatPixels = new float[pixels.length];
	for (int i=0;i<floatPixels.length;i++){
		floatPixels[i] = pixels[i];
	}
	return floatPixels;
}


}

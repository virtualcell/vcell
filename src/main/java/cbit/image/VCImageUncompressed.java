/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
/**
 * This type was created in VisualAge.
 */
public class VCImageUncompressed extends VCImage {
	private byte pixels[] = null;
/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 */
public VCImageUncompressed(VCImage vcimage) throws ImageException{
	super(vcimage);
	this.pixels = vcimage.getPixels().clone();
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
public VCImageUncompressed(org.vcell.util.document.Version aVersion,byte pixels[], org.vcell.util.Extent aExtent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	super(aVersion, aExtent, aNumX, aNumY, aNumZ);
	if (aNumX*aNumY*aNumZ != pixels.length){
		throw new IllegalArgumentException("size ("+aNumX+","+aNumY+","+aNumZ+") not consistent with "+pixels.length+" pixels");
	}
	this.pixels = pixels;
	initPixelClasses();
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
public VCImageUncompressed(org.vcell.util.document.Version aVersion,int sourceValues[], org.vcell.util.Extent aExtent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	super(aVersion, aExtent, aNumX, aNumY, aNumZ);
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
	initPixelClasses();

}
/**
 * getPixels method comment.
 */
public byte[] getPixels() {
	return pixels;
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
}

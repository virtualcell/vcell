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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.Hex;

import java.io.IOException;
import java.security.MessageDigest;
/**
 * This type was created in VisualAge.
 */
public class VCImageUncompressed extends VCImage {
	private final static Logger lg = LogManager.getLogger(VCImageUncompressed.class);
	private final byte pixels[];
/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 */
public VCImageUncompressed(VCImage vcimage) throws ImageException{
	super(vcimage);
	this.pixels = vcimage.getPixels().clone();
}

public VCImageUncompressed(org.vcell.util.document.Version aVersion,byte pixels[], org.vcell.util.Extent aExtent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	super(aVersion, aExtent, aNumX, aNumY, aNumZ);
	if (aNumX*aNumY*aNumZ != pixels.length){
		throw new IllegalArgumentException("size ("+aNumX+","+aNumY+","+aNumZ+") not consistent with "+pixels.length+" pixels");
	}
	this.pixels = pixels;
	initPixelClasses();
	if (lg.isTraceEnabled()) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String hashUncompressed = Hex.toString(digest.digest(pixels));
			lg.trace("Constructor(byte[]): uncompressed pixels(" + pixels.length + "): hash=" + hashUncompressed.substring(0, 6));
		} catch (Exception e) {
		}
	}
}

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
	if (lg.isInfoEnabled()) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String hashUncompressed = Hex.toString(digest.digest(pixels));
			lg.info("Constructor(int[]): uncompressed pixels(" + pixels.length + "): hash=" + hashUncompressed.substring(0, 6));
		} catch (Exception e) {
		}
	}
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
		byte[] compressed = VCImage.deflate(pixels);
		return compressed;
	}catch (IOException e){
		e.printStackTrace(System.out);
		throw new ImageException(e.getMessage());
	}
}
}

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

import java.io.IOException;
import java.security.MessageDigest;
import java.util.zip.DataFormatException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.Hex;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class VCImageCompressed extends VCImage {
	private final byte compressedPixels[];
	private transient byte uncompressed[] = null;
	private static Logger lg = LogManager.getLogger(VCImageCompressed.class);
/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 */
public VCImageCompressed(VCImage vcimage) throws ImageException {
	super(vcimage);
	this.compressedPixels = vcimage.getPixelsCompressed().clone();
}

public VCImageCompressed(org.vcell.util.document.Version aVersion, byte pixels[], org.vcell.util.Extent extent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	super(aVersion,extent,aNumX,aNumY,aNumZ);
	this.compressedPixels = pixels;
	initPixelClasses();
	if (lg.isTraceEnabled()) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String hashCompressed = Hex.toString(digest.digest(compressedPixels));
			lg.trace("Constructor(byte[]): compressed pixels(" + compressedPixels.length + "): hash=" + hashCompressed.substring(0, 6));
		} catch (Exception e) {
		}
	}
}
public void nullifyUncompressedPixels(){
	uncompressed = null;
}

/**
 * getPixels method comment.
 */
public byte[] getPixels() throws ImageException {
	try {
		if (uncompressed == null){
			uncompressed = VCImage.inflate(compressedPixels,getNumXYZ());
		}
		return uncompressed;
	} catch (IOException | DataFormatException e){
		throw new ImageException(e.getMessage(), e);
	}
}
/**
 * This method was created in VisualAge.
 * @return byte[]
 */
public byte[] getPixelsCompressed() {
	return compressedPixels;
}
}

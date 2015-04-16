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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;

import org.apache.log4j.Logger;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class VCImageCompressed extends VCImage {
	private byte compressedPixels[] = null;
	private transient byte uncompressed[] = null;
	private static Logger lg = Logger.getLogger(VCImageCompressed.class);
/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 */
public VCImageCompressed(VCImage vcimage) throws ImageException {
	super(vcimage);
	this.compressedPixels = vcimage.getPixelsCompressed().clone();
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
public VCImageCompressed(org.vcell.util.document.Version aVersion, byte pixels[], org.vcell.util.Extent extent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	super(aVersion,extent,aNumX,aNumY,aNumZ);
	this.compressedPixels = pixels;
	initPixelClasses();
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
			lg.trace("VCImageCompressed.getPixels()  <<<<<<UNCOMPRESSING>>>>>>");
			ByteArrayInputStream bis = new ByteArrayInputStream(compressedPixels);
			InflaterInputStream iis = new InflaterInputStream(bis);
			int temp;
			byte buf[] = new byte[65536];
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while((temp = iis.read(buf,0,buf.length)) != -1){
				bos.write(buf,0,temp);
			}
			//byte uncompressed[] = new byte[getSizeX()*getSizeY()*getSizeZ()];
			//int result = iis.read(uncompressed,0,getSizeX()*getSizeY()*getSizeZ());
			//return uncompressed;
			uncompressed = bos.toByteArray();
		}
		return uncompressed;
	} catch (IOException e){
		lg.debug("getPixels( )",e);
		throw new ImageException(e.getMessage());
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

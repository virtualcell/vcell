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
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import org.vcell.util.ISize;

/**
 * This type was created in VisualAge.
 */
public class GIFImage implements Serializable {
	private byte gifEncodedData[] = null;
	private ISize size = null;

/**
 * GIFImage constructor comment.
 */
public GIFImage(byte AgifEncodedData[]) throws GifParsingException{
	this.gifEncodedData = AgifEncodedData;
	try{
		getJavaImage();
	}catch(Throwable e){
		e.printStackTrace(System.out);
		throw new GifParsingException("Error parsing gifEncodedData");
	}
}


/**
 * This method was created in VisualAge.
 * @return byte[]
 */
public byte[] getGifEncodedData() {
	return gifEncodedData;
}


/**
 * This method was created in VisualAge.
 * @return java.awt.Image
 */
public BufferedImage getJavaImage() {
	try {
		return ImageIO.read(new ByteArrayInputStream(gifEncodedData));
	} catch (IOException e) {
		e.printStackTrace();
		throw new RuntimeException("error reading gif image: "+e.getMessage(),e);
	}
}


/**
 * This method was created in VisualAge.
 * @return ISize
 */
public ISize getSize() {

	if(size != null){
		return size;
	}
	BufferedImage javaImage = getJavaImage();
	int height = javaImage.getHeight();
	int width = javaImage.getHeight();
	this.size = new ISize(width,height,1);
	return this.size;
}
}

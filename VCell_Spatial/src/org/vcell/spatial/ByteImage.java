package org.vcell.spatial;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
import java.util.zip.*;

import org.vcell.util.Extent;
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
public ByteImage(byte pixels[], org.vcell.util.Extent aExtent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	super(aExtent, aNumX, aNumY, aNumZ);
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
public ByteImage(int sourceValues[], org.vcell.util.Extent aExtent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	super(aExtent, aNumX, aNumY, aNumZ);
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
	ByteImage byteImage = new ByteImage(bigBuffer,new org.vcell.util.Extent(extent0.getX(),extent0.getY(),extent0.getZ()*images.length),nX,nY,nZ);
	return byteImage;
}
}

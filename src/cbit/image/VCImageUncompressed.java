package cbit.image;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
import java.util.zip.*;
/**
 * This type was created in VisualAge.
 */
public class VCImageUncompressed extends VCImage {
	private byte pixels[] = null;
/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 */
public VCImageUncompressed(VCImage vcimage) throws ImageException {
	super(vcimage);
	this.pixels = vcimage.getPixels();
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
public VCImageUncompressed(cbit.sql.Version aVersion,byte pixels[], cbit.util.Extent aExtent, int aNumX, int aNumY, int aNumZ) throws ImageException {
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
public VCImageUncompressed(cbit.sql.Version aVersion,int sourceValues[], cbit.util.Extent aExtent, int aNumX, int aNumY, int aNumZ) throws ImageException {
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

package cbit.vcell.VirtualMicroscopy;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;

import cbit.image.ImageException;
import cbit.util.Extent;
import cbit.util.Matchable;
/**
 * This type was created in VisualAge.
 */
public class FloatImage extends Image implements Serializable {
	private float pixels[] = null;

	public FloatImage(Image image) throws ImageException {
		super(image);
		if (image instanceof FloatImage){
			this.pixels = ((FloatImage)image).getPixels().clone();
		}else if (image instanceof UShortImage){
			short[] shortPixels = ((UShortImage)image).getPixels().clone(); 
			this.pixels = new float[shortPixels.length];
			for (int i = 0; i < shortPixels.length; i++) {
				this.pixels[i] = shortPixels[i];
			}
		}
	}
/**
 * This method was created in VisualAge.
 * @param pix float[]
 * @param x int
 * @param y int
 * @param z int
 * @param name java.lang.String
 * @param annot java.lang.String
 */
public FloatImage(float pixels[], cbit.util.Extent aExtent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	super(aExtent, aNumX, aNumY, aNumZ);
	if (aNumX*aNumY*aNumZ != pixels.length){
		throw new IllegalArgumentException("size ("+aNumX+","+aNumY+","+aNumZ+") not consistent with "+pixels.length+" pixels");
	}
	this.pixels = pixels;
}
public float getPixel(int x, int y, int z) throws ImageException {
	if (x<0||x>=getNumX()||y<0||y>=getNumY()||z<0||z>=getNumZ()){
		throw new IllegalArgumentException("("+x+","+y+","+z+") is not inside (0,0,0) and ("+(getNumX()-1)+","+(getNumY()-1)+","+(getNumZ()-1)+")");
	}
	int index = x + getNumX()*(y + z*getNumY()); 
	return (float) getPixels()[index];
}

@Override
public void reverse() {
	float minValue = pixels[0];
	float maxValue = pixels[0];
	for (int i = 0; i < pixels.length; i++) {
		minValue = Math.min(minValue, pixels[i]);
		maxValue = Math.min(maxValue, pixels[i]);
	}
	for (int i = 0; i < pixels.length; i++) {
		pixels[i] = maxValue - (pixels[i]-minValue);
	}
	System.out.println("reversing image, pixel values in range ["+minValue+","+maxValue+"]");
}

public void and(UShortImage imageMask) {
	short[] maskPixels = imageMask.getPixels();
	for (int i = 0; i < pixels.length; i++) {
		if (maskPixels[i]==0){
			pixels[i] = 0;
		}
	}
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
 * getPixels method comment.
 */
public float[] getPixels() {
	return pixels;
}
/**
 * This method was created in VisualAge.
 * @return float
 * @param x int
 * @param y int
 * @param z int
 */
public void setPixel(int x, int y, int z, float newValue) throws ImageException {
	if (x<0||x>=getNumX()||y<0||y>=getNumY()||z<0||z>=getNumZ()){
		throw new IllegalArgumentException("("+x+","+y+","+z+") is not inside (0,0,0) and ("+(getNumX()-1)+","+(getNumY()-1)+","+(getNumZ()-1)+")");
	}
	int index = x + getNumX()*(y + z*getNumY()); 
	getPixels()[index] = newValue;
}

public boolean compareEqual(Matchable obj) {
	if (!(obj instanceof FloatImage)){
		return false;
	}
	FloatImage usImage = (FloatImage)obj;

	if (!super.compareEqual(obj)){
		return false;
	}

	if(!cbit.util.Compare.isEqual(pixels,usImage.pixels)){
		return false;
	}
	return true;
}

public FloatImage crop(Rectangle rect) throws ImageException {
	float[] croppedPixels = new float[rect.width*rect.height*getNumZ()];
	for (int k = 0; k < getNumZ(); k++) {
		for (int j = 0; j < rect.height; j++) {
			for (int i = 0; i < rect.width; i++) {
				croppedPixels[i+j*rect.width+(k*rect.width*rect.height)] = pixels[rect.x+i+(j+rect.y)*getNumX()+k*getNumX()*getNumY()];
			}
		}
	}
	Extent croppedExtent = null;
	if (getExtent()!=null){
		croppedExtent = new Extent(getExtent().getX()*rect.width/getNumX(),getExtent().getX()*rect.height/getNumY(),getExtent().getZ());
	}
	return new FloatImage(croppedPixels,croppedExtent,rect.width,rect.height,getNumZ());
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


/**
 * This method was created in VisualAge.
 * @return int[]
 */
public float[] getUniquePixelValues() throws ImageException{
	float pixels[] = getPixels();
	int imageLength = pixels.length;
	if (imageLength==0){
		return null;
	}
	
	ArrayList<Float> pixelValueArray = new ArrayList<Float>();
	pixelValueArray.add(pixels[0]);

	for (int i=0;i<imageLength;i++){
		float currPixel = pixels[i];

		//
		// look for current pixel in list
		//
		boolean found = false;
		for (int j=0;j<pixelValueArray.size();j++){
			if (pixelValueArray.get(j)==currPixel){
				found = true;
			}
		}
		//
		// if current pixel not found, extend list and add pixel to end
		//
		if (!found){
			pixelValueArray.add(currPixel);
		}
	}
	float[] uniquePixelValues = new float[pixelValueArray.size()];
	for (int i = 0; i < uniquePixelValues.length; i++) {
		uniquePixelValues[i] = pixelValueArray.get(i).floatValue();
	}
	return uniquePixelValues;
}

public short[] getBinaryPixels(float threshold){
	short[] binaryPixels = new short[pixels.length];
	for (int i = 0; i < pixels.length; i++) {
		if (pixels[i]>=threshold){
			binaryPixels[i] = 1;
		}
	}
	return binaryPixels;
}

public long countPixelsByValue(float value){
	long count = 0;
	for (int i = 0; i < pixels.length; i++) {
		if (pixels[i]==value){
			count++;
		}
	}
	return count;
}

public void showPixelsAsMatrix(){
	System.out.println("image ("+getNumX()+","+getNumY()+","+getNumZ()+"), "+getExtent());
	int index=0;
	for (int k = 0; k < getNumZ(); k++) {
		for (int j = 0; j < getNumY(); j++) {
			for (int i = 0; i < getNumX(); i++) {
				System.out.print(pixels[index++]+" ");
			}
			System.out.println();
		}
		System.out.println();
	}
	System.out.println();
}
@Override
public ImageStatistics getImageStatistics() {
	ImageStatistics stats = new ImageStatistics();
	stats.minValue = pixels[0];
	stats.maxValue = pixels[0];
	stats.meanValue = 0.0;
	for (int i = 0; i < pixels.length; i++) {
		stats.minValue = Math.min(stats.minValue,pixels[i]);
		stats.maxValue = Math.max(stats.maxValue,pixels[i]);
		stats.meanValue += pixels[i];
	}
	stats.meanValue = stats.meanValue/pixels.length;
	
	return stats;
}



}

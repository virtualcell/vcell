package cbit.vcell.VirtualMicroscopy;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.Serializable;
import java.util.ArrayList;

import org.vcell.util.Matchable;
import org.vcell.util.Origin;

//import com.sun.java.util.jar.pack.Attribute.FormatException;

import cbit.image.ImageException;

/**
 * This type was created in VisualAge.
 */
public class UShortImage extends Image implements Serializable {
	private short pixels[] = null;

	public UShortImage(Image image) throws ImageException {
		super(image);
		if (image instanceof UShortImage){
			this.pixels = ((UShortImage)image).getPixels().clone();
		}else if (image instanceof FloatImage){
			float[] floatPixels = ((FloatImage)image).getPixels().clone(); 
			this.pixels = new short[floatPixels.length];
			for (int i = 0; i < floatPixels.length; i++) {
				this.pixels[i] = (short)((int)Math.round(floatPixels[i]));
			}
		}
	}
/**
 * This method was created in VisualAge.
 * @param pix short[]
 * @param x int
 * @param y int
 * @param z int
 * @param name java.lang.String
 * @param annot java.lang.String
 */
public UShortImage(short pixels[], Origin aOrigin, org.vcell.util.Extent aExtent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	super(aOrigin, aExtent, aNumX, aNumY, aNumZ);
	if (aNumX*aNumY*aNumZ != pixels.length){
		throw new IllegalArgumentException("size ("+aNumX+","+aNumY+","+aNumZ+") not consistent with "+pixels.length+" pixels");
	}
	this.pixels = pixels;
}
public short getPixel(int x, int y, int z) throws ImageException {
	if (x<0||x>=getNumX()||y<0||y>=getNumY()||z<0||z>=getNumZ()){
		throw new IllegalArgumentException("("+x+","+y+","+z+") is not inside (0,0,0) and ("+(getNumX()-1)+","+(getNumY()-1)+","+(getNumZ()-1)+")");
	}
	int index = x + getNumX()*(y + z*getNumY()); 
	return (short) getPixels()[index];
}

public boolean compareEqual(Matchable obj) {
	if (!(obj instanceof UShortImage)){
		return false;
	}
	UShortImage usImage = (UShortImage)obj;

	if (!super.compareEqual(obj)){
		return false;
	}

	if(!org.vcell.util.Compare.isEqual(pixels,usImage.pixels)){
		return false;
	}
	return true;
}


public void reverse() {
	int minValue = 0xffff&((int)pixels[0]);
	int maxValue = 0xffff&((int)pixels[0]);
	for (int i = 0; i < pixels.length; i++) {
		int intPixelValue = 0xffff&((int)pixels[i]);
		minValue = Math.min(minValue, intPixelValue);
		maxValue = Math.max(maxValue, intPixelValue);
	}
	for (int i = 0; i < pixels.length; i++) {
		pixels[i] = (short)(maxValue - (0xffff&((int)pixels[i])-minValue));
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
public short[] getPixels() {
	return pixels;
}

public Object getPixelArray(){
	return getPixels();
}

public static BufferedImage Blurring(BufferedImage bi)
{
	BufferedImage buff = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
	
	Kernel kernel = new Kernel(3, 3, new float[] {
													1f/9f, 1f/9f, 1f/9f, 
													1f/9f, 1f/9f, 1f/9f, 
													1f/9f, 1f/9f, 1f/9f
												 });
	
	ConvolveOp op = new ConvolveOp(kernel);
	op.filter(bi, buff);
	
	return buff;
}




//Filtro Sharpening
public static BufferedImage Sharpening(BufferedImage bi)
{
	BufferedImage buff = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
	
	Kernel kernel = new Kernel(3, 3, new float[] {
													-1f, -1f, -1f, 
													-1f,  9f, -1f, 
													-1f, -1f, -1f
												 });
	
	ConvolveOp op = new ConvolveOp(kernel);
	op.filter(bi, buff);
	
	return buff;
}

public static BufferedImage Embrossing(BufferedImage bi)
{
	BufferedImage buff = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
	
	Kernel kernel = new Kernel(3, 3, new float[] {
													-2f, 0f, 0f, 
													 0f, 1f, 0f, 
													 0f, 0f, 2f
												 });
	
	ConvolveOp op = new ConvolveOp(kernel);
	op.filter(bi, buff);
	
	return buff;
}
public static BufferedImage EdgeW(BufferedImage bi)
{
	BufferedImage buff = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
	
	Kernel kernel = new Kernel(3, 3, new float[] {
													-1f, 0f, 1f, 
													-2f, 0f, 2f, 
													-1f, 0f, 1f
												 });
	
	ConvolveOp op = new ConvolveOp(kernel);
	op.filter(bi, buff);
	
	return buff;
}


public static BufferedImage EdgeH(BufferedImage bi)
{
	BufferedImage buff = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
	
	Kernel kernel = new Kernel(3, 3, new float[] {
													-1f, -2f, -1f, 
													 0f,  0f,  0f, 
													 1f,  2f,   1f
												 });
	
	ConvolveOp op = new ConvolveOp(kernel);
	op.filter(bi, buff);
	
	return buff;
}
//pixel before the filter
//|--|--|--|
//|  |  |  |
//|--|--|--|
//|  | *|  |
//|--|--|--|
//|  |  |  |
//|--|--|--|

//pixel after the filter
//|--|--|--|
//| *| *| *|
//|--|--|--|
//| *| *| *|
//|--|--|--|
//| *| *| *|
//|--|--|--|

public short[] getBinaryPixels(int threshold){
	short[] binaryPixels = new short[pixels.length];
	for (int i = 0; i < pixels.length; i++) {
		int pixel = 0xffff&((int)pixels[i]);
		if (pixel>=threshold){
			binaryPixels[i] = 1;
		}
	}
	return binaryPixels;
}

public long countPixelsByValue(short value){
	long count = 0;
	for (int i = 0; i < pixels.length; i++) {
		if (pixels[i]==value){
			count++;
		}
	}
	return count;
}

//private static BufferedImage dilate3(BufferedImage bi)
//	{
//		BufferedImage buff = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
//		
//		Kernel kernel = new Kernel(3, 3, new float[] {
//														1f, 1f, 1f, 
//														1f, 1f, 1f, 
//														1f, 1f, 1f
//													 });
//		
//		ConvolveOp op = new ConvolveOp(kernel);
//		op.filter(bi, buff);
//		
//		return buff;
//	}
//

////Use JAI filter
//
//public static RenderedImage subTraction(BufferedImage img1, BufferedImage img2)
//	{
//		ParameterBlock pb = new ParameterBlock();
//		pb.addSource(img1);
//		pb.addSource(img2);
//		
//		return JAI.create("subtract", pb);
//	}

////JAI Filter erode
//
//private static RenderedImage erode(BufferedImage img)
//	{
//		KernelJAI kernel = new KernelJAI(7, 7, new float[]{
//															0, 0, 0, 0, 0, 0, 0,
//															0, 1, 1, 1, 1, 1, 0,
//															0, 1, 1, 1, 1, 1, 0,
//															0, 1, 1, 1, 1, 1, 0,
//															0, 1, 1, 1, 1, 1, 0,
//															0, 1, 1, 1, 1, 1, 0,
//															0, 0, 0, 0, 0, 0, 0
//															});
//		ParameterBlock pb = new ParameterBlock();
//		pb.addSource(img);
//		pb.add(kernel);
//		
//		return JAI.create("erode", pb);
//	}


//the input it must be a b/w image

public static BufferedImage showBitPlanes(BufferedImage bi, int lv)
	{
		int level = 0;
		
		switch(level)
		{
			case 0:
				level = 128;
				break;
			case 1:
				level = 64;
				break;
			case 2:
				level = 32;
				break;
			case 3:
				level = 16;
				break;
			case 4:
				level = 8;
				break;
			case 5:
				level = 4;
				break;
			case 6:
				level = 2;
				break;
			case 7:
				level = 1;
				break;
			default:
					return null;
		}
		
		int width = bi.getWidth();
		int height = bi.getHeight();
		
		BufferedImage img = new BufferedImage(width, height, bi.getType());
		
		for(int x=0; x<width; x++)
			for(int y=0; y<height; y++)
				img.setRGB(x, y, ((bi.getRGB(x, y) & level)/level)*255);
		
		return img;
	}

/**
 * This method was created in VisualAge.
 * @return short
 * @param x int
 * @param y int
 * @param z int
 */
public void setPixel(int x, int y, int z, short newValue) throws ImageException {
	if (x<0||x>=getNumX()||y<0||y>=getNumY()||z<0||z>=getNumZ()){
		throw new IllegalArgumentException("("+x+","+y+","+z+") is not inside (0,0,0) and ("+(getNumX()-1)+","+(getNumY()-1)+","+(getNumZ()-1)+")");
	}
	int index = x + getNumX()*(y + z*getNumY()); 
	getPixels()[index] = newValue;
}

public void showPixelsAsMatrix(){
	System.out.println("image ("+getNumX()+","+getNumY()+","+getNumZ()+"), "+getExtent());
	int index=0;
	for (int k = 0; k < getNumZ(); k++) {
		for (int j = 0; j < getNumY(); j++) {
			for (int i = 0; i < getNumX(); i++) {
				System.out.print((int)(0xffff&pixels[index++])+" ");
			}
			System.out.println();
		}
		System.out.println();
	}
	System.out.println();
}

/**
 * This method was created in VisualAge.
 * @return int[]
 */
public short[] getUniquePixelValues() throws ImageException{
	short pixels[] = getPixels();
	int imageLength = pixels.length;
	if (imageLength==0){
		return null;
	}
	
	ArrayList<Short> pixelValueArray = new ArrayList<Short>();
	pixelValueArray.add(pixels[0]);

	for (int i=0;i<imageLength;i++){
		short currPixel = pixels[i];

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
	short[] uniquePixelValues = new short[pixelValueArray.size()];
	for (int i = 0; i < uniquePixelValues.length; i++) {
		uniquePixelValues[i] = pixelValueArray.get(i).shortValue();
	}
	return uniquePixelValues;
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

public UShortImage crop(Rectangle rect) throws ImageException{
	return (UShortImage)Image.crop(this, rect);
}


@Override
public ImageStatistics getImageStatistics() {
	ImageStatistics stats = new ImageStatistics();
	stats.minValue = 0xffff&((int)pixels[0]);
	stats.maxValue = 0xffff&((int)pixels[0]);
	stats.meanValue = 0.0;
	for (int i = 0; i < pixels.length; i++) {
		int value = 0xffff&((int)pixels[i]);
		stats.minValue = Math.min(stats.minValue,value);
		stats.maxValue = Math.max(stats.maxValue,value);
		stats.meanValue += value;
	}
	stats.meanValue = stats.meanValue/pixels.length;
	
	return stats;
}
}

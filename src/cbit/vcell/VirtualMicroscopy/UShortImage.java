package cbit.vcell.VirtualMicroscopy;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Hashtable;

//import com.sun.java.util.jar.pack.Attribute.FormatException;

import cbit.image.ImageException;
import cbit.util.Extent;

//import javax.media.jai.JAI;
//import javax.media.jai.KernelJAI;
//
//import loci.formats.DataTools;
//import loci.formats.FormatException;
//import loci.formats.ImageTools;
//
//import visad.data.jai.JAIForm;
//
//import cbit.image.ImageException;
//import cbit.util.Extent;
//import cbit.vcell.virtualmicroscopy.imaging.ItkUtils;
//import cbit.vcell.virtualmicroscopy.imaging.ItkUtils.FilterOp;
//import cbit.vcell.virtualmicroscopy.imaging.ItkUtils.FilterParameter;
/**
 * This type was created in VisualAge.
 */
public class UShortImage extends Image implements Serializable {
	private short pixels[] = null;

	public UShortImage(UShortImage image) throws ImageException {
		super(image);
		this.pixels = image.getPixels().clone();
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
public UShortImage(short pixels[], cbit.util.Extent aExtent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	super(aExtent, aNumX, aNumY, aNumZ);
	if (aNumX*aNumY*aNumZ != pixels.length){
		throw new IllegalArgumentException("size ("+aNumX+","+aNumY+","+aNumZ+") not consistent with "+pixels.length+" pixels");
	}
	this.pixels = pixels.clone();
}
public short getPixel(int x, int y, int z) throws ImageException {
	if (x<0||x>=getNumX()||y<0||y>=getNumY()||z<0||z>=getNumZ()){
		throw new IllegalArgumentException("("+x+","+y+","+z+") is not inside (0,0,0) and ("+(getNumX()-1)+","+(getNumY()-1)+","+(getNumZ()-1)+")");
	}
	int index = x + getNumX()*(y + z*getNumY()); 
	return (short) getPixels()[index];
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

public double getPixelAreaXY(){
	double deltaX = getExtent().getX()/getNumX();
	double deltaY = getExtent().getX()/getNumY();
	//double deltaZ = getExtent().getX()/getNumZ();
	return (deltaX*deltaY);
}

//public void dilate(int radius) throws FormatException, ImageException, IOException {
//	Hashtable<FilterParameter, Number> paramHash = new Hashtable<FilterParameter,Number>();
//	paramHash.put(FilterParameter.FILTERPARAM_RADIUS, radius);
//	paramHash.put(FilterParameter.FILTERPARAM_MASKVALUE, 0xffff);
//	ItkUtils.MaskImageFilter(this, this, FilterOp.FILTER_DILATE, paramHash);
//}
//
//public void erode(int radius) throws FormatException, IOException, ImageException{
//	Hashtable<FilterParameter, Number> paramHash = new Hashtable<FilterParameter,Number>();
//	paramHash.put(FilterParameter.FILTERPARAM_RADIUS, radius);
//	paramHash.put(FilterParameter.FILTERPARAM_MASKVALUE, 0xffff);
//	ItkUtils.MaskImageFilter(this, this, FilterOp.FILTER_ERODE, paramHash);
//}

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

public UShortImage crop(Rectangle rect) throws ImageException {
	short[] croppedPixels = new short[rect.width*rect.height];
	for (int i = 0; i < rect.width; i++) {
		for (int j = 0; j < rect.height; j++) {
			croppedPixels[i+j*rect.width] = pixels[rect.x+i+(j+rect.y)*getNumX()];
		}
	}
	Extent croppedExtent = null;
	if (getExtent()!=null){
		croppedExtent = new Extent(getExtent().getX()*rect.width/getNumX(),getExtent().getX()*rect.height/getNumY(),getExtent().getZ());
	}
	return new UShortImage(croppedPixels,croppedExtent,rect.width,rect.height,getNumZ());
}

///**
// * This method was created by a SmartGuide.
// * @return cbit.image.FileImage
// * @param images cbit.image.FileImage[]
// * @exception java.lang.Exception The exception description.
// */
//public TemplatedImage(TemplatedImage<short> images[], Extent newExtent) throws ImageException {
//	super(newExtent,images[0].getNumX(),images[0].getNumY(),images[0].getNumZ()*images.length);
//	for (int i=1;i<images.length;i++){
//		if (images[i].getNumX() != nX){
//			throw new ImageException("image "+(i+1)+" x dimension doesn't match the first image");
//		}	
//		if (images[i].getNumY() != nY){
//			throw new ImageException("image "+(i+1)+" y dimension doesn't match the first image");
//		}	
//		if (images[i].getNumZ() < 1){
//			throw new ImageException("image "+(i+1)+" z dimension must be at least 1");
//		}	
//		nZ += images[i].getNumZ();
//	}
//	int nTotal = nX*nY*nZ;
//	short bigBuffer[] = new short[nTotal];
//	int index = 0;
//	for (int i=0;i<images.length;i++){
//		short currPix[] = images[i].getPixels();
//		int currTotal = images[i].getNumX()*images[i].getNumY()*images[i].getNumZ();
//		for (int j=0;j<currTotal;j++){
//			bigBuffer[index++] = currPix[j];
//		}
//	}		
//	TemplatedImage<short> byteImage = new TemplatedImage<short>(bigBuffer,new cbit.util.Extent(extent0.getX(),extent0.getY(),extent0.getZ()*images.length),nX,nY,nZ);
//	return byteImage;
//}
}

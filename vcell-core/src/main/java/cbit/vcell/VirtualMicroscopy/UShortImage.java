/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.VirtualMicroscopy;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBufferUShort;
import java.awt.image.Kernel;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.BitSet;

import javax.media.jai.BorderExtender;
import javax.media.jai.KernelJAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.AddConstDescriptor;
import javax.media.jai.operator.AndDescriptor;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.DilateDescriptor;
import javax.media.jai.operator.ErodeDescriptor;
import javax.media.jai.operator.ExtremaDescriptor;
import javax.media.jai.operator.LookupDescriptor;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.vcell.util.Extent;
import org.vcell.util.Matchable;
import org.vcell.util.Origin;

//import com.sun.java.util.jar.pack.Attribute.FormatException;

import cbit.image.ImageException;

/**
 * This type was created in VisualAge.
 */

public class UShortImage extends Image implements Serializable {
	@XmlElement
	@XmlJavaTypeAdapter(UShortImage.UShortXmlAdapter.class)
	private short pixels[] = null;
	public UShortImage() {}//For jaxb
	
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
public static class UShortXmlAdapter extends XmlAdapter<String, short[]>{

	@Override
	public short[] unmarshal(String v) throws Exception {
		byte[] bytes = Base64.getDecoder().decode(v);
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		short[] shorts = new short[bytes.length/Short.BYTES];
		for (int i = 0; i < shorts.length; i++) {
			shorts[i] = byteBuffer.getShort();
		}
		return shorts;
	}

	@Override
	public String marshal(short[] v) throws Exception {
		ByteBuffer byteBuf = ByteBuffer.allocate(v.length*Short.BYTES);
		for (int i = 0; i < v.length; i++) {
			byteBuf.putShort(v[i]);
		}
		byte[] bytes = byteBuf.array();
		return Base64.getEncoder().encodeToString(bytes);
	}
	
}
public static BufferedImage createUnsignedBufferedImage(UShortImage uShortImage){
	return createUnsignedBufferedImage(uShortImage.getPixels(), uShortImage.getNumX(), uShortImage.getNumY());
}
public static BufferedImage createUnsignedBufferedImage(short[] shortArr,int numX,int numY){
	if(shortArr.length != (numX*numY)){
		throw new RuntimeException("UShortImage.createBufferedImage: X*Y size does not match array length");
	}
	BufferedImage bufferedImage = new BufferedImage(numX,numY, BufferedImage.TYPE_USHORT_GRAY);
	short[] bufferedImageArr = ((DataBufferUShort)bufferedImage.getRaster().getDataBuffer()).getData();
	System.arraycopy(shortArr, 0, bufferedImageArr, 0, shortArr.length);
	return bufferedImage;

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
	if(minX == Integer.MAX_VALUE){
		return null;
	}
	return new Rectangle(minX,minY,maxX-minX+1,maxY-minY+1);
}

public UShortImage crop(Rectangle rect) throws ImageException{
	return (UShortImage)Image.crop(this, rect);
}

public void blur() throws ImageException{
	if (getNumZ()!=1){
		throw new RuntimeException("not yet implemented");
	}
	int numY = getNumY();
	int numX = getNumX();
	short[] blurred = new short[numX*numY];
	int pixelIndex = 0;
	for (int j=0;j<numY;j++){
		for (int i=0;i<numX;i++){
			int count = 0;
			int sum = 0;
			for (int ii=-1;ii<2;ii++){
				int iIndex = i + ii;
				if (iIndex>=0 && iIndex<numX){
					for (int jj=-1;jj<2;jj++){
						int jIndex = j+jj;
						if (jIndex>=0 && jIndex<numY){
							sum +=  this.pixels[iIndex + numX*jIndex];
							count++;
						}
					}
				}
			}
			blurred[pixelIndex] = ((short)(0xffff & (sum/count)));
			pixelIndex++;
		}
	}
	pixels = blurred;
}

public static KernelJAI createCircularBinaryKernel(int radius){
	int enclosingBoxSideLength = radius*2+1;
	float[] kernalData = new float[enclosingBoxSideLength*enclosingBoxSideLength];
	Point2D kernalPoint = new Point2D.Float(0f,0f);
	int index = 0;
	for (int y = -radius; y <= radius; y++) {
		for (int x = -radius; x <= radius; x++) {
			if(kernalPoint.distance(x, y) <= radius){
				kernalData[index] = 1.0f;
			}
			index++;
		}
	}
	return new KernelJAI(enclosingBoxSideLength,enclosingBoxSideLength,radius,radius,kernalData);
}

public static UShortImage fastDilate(UShortImage dilateSource,int radius,UShortImage mask) throws ImageException{
	short[] sourcePixels = dilateSource.getPixels();
	short[] targetPixels = dilateSource.getPixels().clone();
	KernelJAI dilateKernel = createCircularBinaryKernel(radius);
	float[] kernelData = dilateKernel.getKernelData();
	BitSet kernelBitSet = new BitSet(kernelData.length);
	for (int i = 0; i < kernelData.length; i++) {
		if(kernelData[i] == 1.0f){
			kernelBitSet.set(i);
		}
	}
	boolean bNeedsFill = false;
	for (int y = 0; y < dilateSource.getNumY(); y++) {
		int yOffset = y*dilateSource.getNumX();
		int yMinus = yOffset-dilateSource.getNumX();
		int yPlus = yOffset+dilateSource.getNumX();
		for (int x = 0; x < dilateSource.getNumX(); x++) {
			bNeedsFill = false;
			if(sourcePixels[x+yOffset] != 0){
				if(x-1 >= 0 && sourcePixels[(x-1)+yOffset] == 0){
					bNeedsFill = true;
				}else
				if(y-1 >= 0 && sourcePixels[x+yMinus] == 0){
					bNeedsFill = true;
				}else
				if(x+1 < dilateSource.getNumX() && sourcePixels[(x+1)+yOffset] == 0){
					bNeedsFill = true;
				}else
				if(y+1 < dilateSource.getNumY() && sourcePixels[x+yPlus] == 0){
					bNeedsFill = true;
				}
				if(bNeedsFill){
					int masterKernelIndex = 0;
					for (int y2 = y-radius; y2 <= y+radius; y2++) {
						if(y2>= 0 && y2 <dilateSource.getNumY()){
							int kernelIndex = masterKernelIndex;
							int targetYIndex = y2*dilateSource.getNumX();
							for (int x2 = x-radius; x2 <= x+radius; x2++) {
								if(kernelBitSet.get(kernelIndex) &&
									x2>= 0 && x2 <dilateSource.getNumX()){
									targetPixels[targetYIndex+x2] = 1;
								}
								kernelIndex++;
							}
						}
						masterKernelIndex+= dilateKernel.getWidth();
					}
				}
			}
		}
	}
	UShortImage resultImage =
		new UShortImage(targetPixels,
				dilateSource.getOrigin(),
				dilateSource.getExtent(),
				dilateSource.getNumX(),
				dilateSource.getNumY(),
				dilateSource.getNumZ());
	resultImage.and(mask);
	return resultImage;
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

//copy from another image with the same size.
public void copyImage(UShortImage argImage)
{
	short[] localShortArray = getPixels();
	short[] argShortArray = argImage.getPixels();
	if(localShortArray.length == argShortArray.length)
	{
		System.arraycopy(argShortArray, 0, localShortArray, 0, argShortArray.length);
	}
}
public static PlanarImage binarize(UShortImage source)
{
	return binarize(createUnsignedBufferedImage(source));
}
private static PlanarImage binarize(BufferedImage source)
{
	PlanarImage planarSource = PlanarImage.wrapRenderedImage(source);
	double[][] minmaxArr = (double[][])ExtremaDescriptor.create(planarSource, null, 1, 1, false, 1,null).getProperty("extrema");
	short[] lookupData = new short[0x010000];
	lookupData[(int)minmaxArr[1][0]] = 1;
	LookupTableJAI lookupTable = new LookupTableJAI(lookupData,true);
	planarSource = LookupDescriptor.create(planarSource, lookupTable, null).createInstance();
	return planarSource;		
}
public static UShortImage convertToUShortImage(PlanarImage source,Origin origin,Extent extent) throws ImageException
{
	short[] shortData = new short[source.getWidth() * source.getHeight()];
	source.getData().getDataElements(0, 0, source.getWidth(),source.getHeight(), shortData);
	return new UShortImage(shortData,origin,extent,source.getWidth(),source.getHeight(),1);	
}
public static UShortImage erodeDilate(UShortImage source,KernelJAI dilateErodeKernel,UShortImage mask,boolean bErode) throws ImageException{
	PlanarImage completedImage = null;
	PlanarImage operatedImage = null;
	PlanarImage planarSource = binarize(source);
	Integer borderPad = dilateErodeKernel.getWidth()/2;
	planarSource = 
		BorderDescriptor.create(planarSource,
			borderPad, borderPad, borderPad, borderPad,
			BorderExtender.createInstance(BorderExtender.BORDER_ZERO), null).createInstance();
	if(bErode){
		planarSource = AddConstDescriptor.create(planarSource, new double[] {1.0}, null).createInstance();
    	RenderedOp erodeOP = ErodeDescriptor.create(planarSource, dilateErodeKernel, null);
    	operatedImage = erodeOP.createInstance();
		
	}else{
    	RenderedOp dilationOP = DilateDescriptor.create(planarSource, dilateErodeKernel, null);
    	operatedImage = dilationOP.createInstance();
	}
	operatedImage =
		CropDescriptor.create(operatedImage,
			new Float(0), new Float(0),
			new Float(source.getNumX()), new Float(source.getNumY()), null);
	operatedImage = binarize(operatedImage.getAsBufferedImage());
	if (mask != null) {
		RenderedOp andDescriptor = AndDescriptor.create(operatedImage,binarize(mask), null);
		completedImage = andDescriptor.createInstance();
	}else{
		completedImage = operatedImage;
	}
	return convertToUShortImage(completedImage, source.getOrigin(),source.getExtent());
}

@Override
public double[] getDoublePixels() {
	double[] doublePixels = new double[pixels.length];
	for (int i=0;i<doublePixels.length;i++){
		doublePixels[i] = 0x0000ffff & ((int)pixels[i]);
	}
	return doublePixels;
}
@Override
public float[] getFloatPixels() {
	float[] floatPixels = new float[pixels.length];
	for (int i=0;i<floatPixels.length;i++){
		floatPixels[i] = 0x0000ffff & ((int)pixels[i]);
	}
	return floatPixels;
}

}

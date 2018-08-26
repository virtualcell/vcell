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
import java.io.Serializable;
import java.lang.reflect.Array;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Matchable;
import org.vcell.util.Origin;

import cbit.image.ImageException;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Image implements Serializable, Matchable {
	@XmlAttribute
	private int numX = 0;
	@XmlAttribute
	private int numY = 0;
	@XmlAttribute
	private int numZ = 0;
	@XmlElement
	private Extent extent = new Extent(10, 10, 10);
	@XmlElement
	private Origin origin = new Origin(0,0,0);
	@XmlAttribute
	private String fieldName = new String();
	@XmlAttribute
	private String fieldDescription = new String("NoName");
	public Image() {}//For jaxb

public static class ImageStatistics {
	public double minValue;
	public double maxValue;
	public double meanValue;
	public Double sigma;
	public Double sum;
	public Double variance;
	public String toString(){
		return "min="+minValue+", max="+maxValue+", mean="+meanValue+", sigma="+sigma+", sum="+sum+", variance="+variance;
	}
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
protected Image(Image vci) {
	this.numX = vci.getNumX();
	this.numY = vci.getNumY();
	this.numZ = vci.getNumZ();
	this.extent = vci.getExtent();
	this.origin = vci.getOrigin();
	this.fieldName = vci.getName();
	this.fieldDescription = vci.getDescription();
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
protected Image(Origin aOrigin, org.vcell.util.Extent aExtent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	
	if (aNumX<1 || aNumY<1 || aNumZ<1){
		throw new ImageException("numPixels ("+aExtent.getX()+","+aExtent.getY()+","+aExtent.getZ()+")  must all be >= 1");
	}
	if (aExtent!=null && (aExtent.getX()<=0 || aExtent.getY()<=0 || aExtent.getZ()<=0)){
		throw new ImageException("extent ("+aExtent.getX()+","+aExtent.getY()+","+aExtent.getZ()+")  must all be > 0");
	}
	this.numX = aNumX;
	this.numY = aNumY;
	this.numZ = aNumZ;
	setExtent(aExtent);
	setOrigin(aOrigin);
}

public abstract ImageStatistics getImageStatistics();

public abstract Rectangle getNonzeroBoundingBox();

public abstract int[] getNonzeroIndices();

public abstract void reverse();

public abstract Object getPixelArray();

public final double getPixelAreaXY(){
	double deltaX = getExtent().getX()/getNumX();
	double deltaY = getExtent().getY()/getNumY();
	//double deltaZ = getExtent().getX()/getNumZ();
	return (deltaX*deltaY);
}

public static double calcOriginPosition(double originStart,int pixelOffset,double extentSize,int extentPixels){
	return originStart+(pixelOffset*(extentSize/extentPixels));
}
static Image crop(Image origImage, Rectangle rect) throws ImageException{

	Object inArray = origImage.getPixelArray();
	Object outArray = Array.newInstance(inArray.getClass().getComponentType(), rect.width*rect.height*origImage.getNumZ());
	for (int k = 0; k < origImage.getNumZ(); k++) {
		for (int j = 0; j < rect.height; j++) {
			for (int i = 0; i < rect.width; i++) {
				int inIndex = rect.x+i+(j+rect.y)*origImage.getNumX()+k*origImage.getNumX()*origImage.getNumY();
				int outIndex = i+j*rect.width+(k*rect.width*rect.height);
				Array.set(outArray, outIndex, Array.get(inArray, inIndex));
			}
		}
	}
	Extent croppedExtent = null;
	if (origImage.getExtent()!=null){
		croppedExtent = new Extent(origImage.getExtent().getX()*rect.width/origImage.getNumX(),origImage.getExtent().getY()*rect.height/origImage.getNumY(),origImage.getExtent().getZ());
	}
	Origin croppedOrigin = null;
	if(origImage.getOrigin() != null){
		croppedOrigin =
			new Origin(
					calcOriginPosition(origImage.getOrigin().getX(), rect.x, origImage.getExtent().getX(), origImage.getNumX()),
					calcOriginPosition(origImage.getOrigin().getY(), rect.y, origImage.getExtent().getY(), origImage.getNumY()),
//					origImage.getOrigin().getX()+(rect.x*(origImage.getExtent().getX()/origImage.getNumX())),
//					origImage.getOrigin().getY()+(rect.y*(origImage.getExtent().getY()/origImage.getNumY())),
					origImage.getExtent().getZ());
	}
	if(origImage instanceof UShortImage){
		return new UShortImage((short[])outArray,croppedOrigin,croppedExtent,rect.width,rect.height,origImage.getNumZ());
	}else if(origImage instanceof ByteImage){
		return new ByteImage((byte[])outArray,croppedOrigin,croppedExtent,rect.width,rect.height,origImage.getNumZ());
	}else if(origImage instanceof FloatImage){
		return new FloatImage((float[])outArray,croppedOrigin,croppedExtent,rect.width,rect.height,origImage.getNumZ());
	}
	throw new ImageException("Crop if Image type "+origImage.getClass().getName()+" not implemented.");
}

/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (!(obj instanceof Image)){
		return false;
	}
	Image vci = (Image)obj;

	if(!org.vcell.util.Compare.isEqualOrNull(getName(),vci.getName())){
		return false;
	}
	if(!org.vcell.util.Compare.isEqualOrNull(getDescription(),vci.getDescription())){
		return false;
	}
	
	if(!org.vcell.util.Compare.isEqualOrNull(getExtent(),vci.getExtent())){
		return false;
	}

	if (getNumX() != vci.getNumX()){
		return false;
	}

	if (getNumY() != vci.getNumY()){
		return false;
	}

	if (getNumZ() != vci.getNumZ()){
		return false;
	}
	return true;
}

public abstract Image crop(Rectangle rect) throws ImageException;

/**
 * Gets the description property (java.lang.String) value.
 * @return The description property value.
 * @see #setDescription
 */
public final java.lang.String getDescription() {
	return fieldDescription;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public final org.vcell.util.Extent getExtent() {
	return extent;
}

public final org.vcell.util.Origin getOrigin() {
	return origin;
}


/**
 * This method was created in VisualAge.
 * @return byte
 * @param x int
 * @param y int
 * @param z int
 */
public final int getIndex(int x, int y, int z) throws ImageException {
	if (x<0||x>=numX||y<0||y>=numY||z<0||z>=numZ){
		throw new IllegalArgumentException("("+x+","+y+","+z+") is not inside (0,0,0) and ("+(numX-1)+","+(numY-1)+","+(numZ-1)+")");
	}
	return x + numX*(y + z*numY); 
}


/**
 * Gets the name property (java.lang.String) value.
 * @return The name property value.
 * @see #setName
 */
public final java.lang.String getName() {
	return fieldName;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public final int getNumX() {
	return numX;
}


/**
 * Insert the method's description here.
 * Creation date: (9/30/2005 10:45:30 AM)
 * @return int
 */
public final int getNumXYZ() {
	return numX*numY*numZ;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public final int getNumY() {
	return numY;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public final int getNumZ() {
	return numZ;
}


/**
 * Sets the description property (java.lang.String) value.
 * @param description The new value for the property.
 * @see #getDescription
 */
public final void setDescription(java.lang.String description) {
	fieldDescription = description;
}

public final void setExtent(Extent newExtent){
	this.extent = newExtent;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public final void setOrigin(Origin newOrigin) {
	this.origin = newOrigin;
}

public ISize getISize() {
	return new ISize(numX,numY,numZ);
}

/**
 * Sets the name property (java.lang.String) value.
 * @param name The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getName
 */
public final void setName(java.lang.String name) throws java.beans.PropertyVetoException {
	fieldName = name;
}


/**
 * Insert the method's description here.
 * Creation date: (12/18/00 2:31:07 PM)
 * @return java.lang.String
 */
public String toString() {
	return getClass().getName()+"@"+Integer.toHexString(hashCode())+"("+getName()+")";
}


public abstract double[] getDoublePixels();

public abstract float[] getFloatPixels();

}

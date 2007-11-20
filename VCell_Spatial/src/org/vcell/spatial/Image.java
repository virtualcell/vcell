package org.vcell.spatial;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.Serializable;

import org.vcell.util.Matchable;

public abstract class Image implements Serializable {
	private int numX = 0;
	private int numY = 0;
	private int numZ = 0;
	private org.vcell.util.Extent extent = new org.vcell.util.Extent(10, 10, 10);
	private java.lang.String fieldName = new String();
	private java.lang.String fieldDescription = new String("NoName");

/**
 * This method was created in VisualAge.
 * @param pix byte[]
 * @param x int
 * @param y int
 * @param z int
 * @param name java.lang.String
 * @param annot java.lang.String
 */
protected Image(Image vci) throws ImageException {
	this.numX = vci.getNumX();
	this.numY = vci.getNumY();
	this.numZ = vci.getNumZ();
	this.extent = vci.getExtent();
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
protected Image(org.vcell.util.Extent aExtent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	
	if (aNumX<1 || aNumY<1 || aNumZ<1){
		throw new ImageException("numPixels ("+aExtent.getX()+","+aExtent.getY()+","+aExtent.getZ()+")  must all be >= 1");
	}
	if (aExtent.getX()<=0 || aExtent.getY()<=0 || aExtent.getZ()<=0){
		throw new ImageException("extent ("+aExtent.getX()+","+aExtent.getY()+","+aExtent.getZ()+")  must all be > 0");
	}
	this.numX = aNumX;
	this.numY = aNumY;
	this.numZ = aNumZ;
	setExtent(aExtent);
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

	if(!org.vcell.util.Compare.isEqual(getName(),vci.getName())){
		return false;
	}
	if(!org.vcell.util.Compare.isEqual(getDescription(),vci.getDescription())){
		return false;
	}
	
	if(!org.vcell.util.Compare.isEqual(getExtent(),vci.getExtent())){
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


/**
 * Gets the description property (java.lang.String) value.
 * @return The description property value.
 * @see #setDescription
 */
public java.lang.String getDescription() {
	return fieldDescription;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public org.vcell.util.Extent getExtent() {
	return extent;
}


/**
 * This method was created in VisualAge.
 * @return byte
 * @param x int
 * @param y int
 * @param z int
 */
public int getIndex(int x, int y, int z) throws ImageException {
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
public java.lang.String getName() {
	return fieldName;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumX() {
	return numX;
}


/**
 * Insert the method's description here.
 * Creation date: (9/30/2005 10:45:30 AM)
 * @return int
 */
public int getNumXYZ() {
	return numX*numY*numZ;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumY() {
	return numY;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumZ() {
	return numZ;
}


/**
 * Sets the description property (java.lang.String) value.
 * @param description The new value for the property.
 * @see #getDescription
 */
public void setDescription(java.lang.String description) {
	fieldDescription = description;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public void setExtent(org.vcell.util.Extent newExtent) {
	this.extent = newExtent;
}


/**
 * Sets the name property (java.lang.String) value.
 * @param name The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getName
 */
public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
	fieldName = name;
}


/**
 * Insert the method's description here.
 * Creation date: (12/18/00 2:31:07 PM)
 * @return java.lang.String
 */
public String toString() {
	return "VCImage@"+Integer.toHexString(hashCode())+"("+getName()+")";
}

}
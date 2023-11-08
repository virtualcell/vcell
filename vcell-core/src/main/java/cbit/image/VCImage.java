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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Vector;
import java.util.function.BiPredicate;
import java.util.zip.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.CompressionUtils;
import org.vcell.util.Hex;
import org.vcell.util.Matchable;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.Version;


public abstract class VCImage implements Serializable, org.vcell.util.document.Versionable, java.beans.VetoableChangeListener {
	private final static Logger lg = LogManager.getLogger(VCImage.class);
	//	private KeyValue key = null; //Deprecated
	//	private User owner = null; //Deprecated
	private int numX = 0;
	private int numY = 0;
	private int numZ = 0;
	private org.vcell.util.Extent extent = new org.vcell.util.Extent(10, 10, 10);
	//	private String imageName = null;
	//	private String annot = ""; //Deprecated
	private Version version = null;
	private java.lang.String fieldName = "";
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private java.lang.String fieldDescription = new String("NoName");
	private cbit.image.VCPixelClass[] fieldPixelClasses = null;

protected VCImage(VCImage vci){
	this.numX = vci.getNumX();
	this.numY = vci.getNumY();
	this.numZ = vci.getNumZ();
	this.extent = vci.getExtent();
	this.version = vci.getVersion();
	this.fieldName = vci.getName();
	this.fieldDescription = vci.getDescription();
	VCPixelClass[] newVCPixelClasses = new VCPixelClass[vci.fieldPixelClasses.length];
	for (int i = 0; i < newVCPixelClasses.length; i++) {
		newVCPixelClasses[i] = new VCPixelClass(vci.fieldPixelClasses[i]);
	}
	this.fieldPixelClasses = newVCPixelClasses;
	addVetoableChangeListener(this);
}


protected VCImage(Version aVersion, org.vcell.util.Extent aExtent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	
	this.version = aVersion;
	if (aNumX<1 || aNumY<1 || aNumZ<1){
		throw new ImageException("numPixels ("+aExtent.getX()+","+aExtent.getY()+","+aExtent.getZ()+")  must all be >= 1");
	}
	if (aExtent.getX()<=0 || aExtent.getY()<=0 || aExtent.getZ()<=0){
		throw new ImageException("extent ("+aExtent.getX()+","+aExtent.getY()+","+aExtent.getZ()+")  must all be > 0");
	}
	this.numX = aNumX;
	this.numY = aNumY;
	this.numZ = aNumZ;
	if (version!=null){
		this.fieldName = version.getName();
		this.fieldDescription = version.getAnnot();
	}
	setExtent(aExtent);
	addVetoableChangeListener(this);
}


public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}


public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}


public synchronized void addVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
}


public void clearVersion() {
	version = null;
	VCPixelClass[] pcArr =  getPixelClasses();
	for(int i=0;i < pcArr.length;i+= 1){
		pcArr[i].clearKey();
	}
}


public boolean compareEqual(Matchable obj) {
	return compareEqual(obj, 3, false);
}


public boolean compareEqual(Matchable obj, int dimension, boolean bIgnoreMetadata) {
	if (!(obj instanceof VCImage)){
		return false;
	}
	VCImage vci = (VCImage)obj;

	if(!bIgnoreMetadata && !org.vcell.util.Compare.isEqual(getName(),vci.getName())){
		return false;
	}
	if(!bIgnoreMetadata && !org.vcell.util.Compare.isEqual(getDescription(),vci.getDescription())){
		return false;
	}

	if(!org.vcell.util.Compare.isEqual(getExtent().getAsClipped(dimension),vci.getExtent().getAsClipped(dimension))){
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

	BiPredicate<Matchable, Matchable> predicate = (pc1, pc2) -> ((VCPixelClass)pc1).compareEqual(pc2,false);
	if(!org.vcell.util.Compare.isEqual(fieldPixelClasses,vci.fieldPixelClasses,predicate)){
		return false;
	}

	//if (!cbit.util.Compare.isEqualOptional(getVersion(),vci.getVersion())){
	//	return false;
	//}
	try {
		byte array1[] = getPixelsCompressed();
		byte array2[] = vci.getPixelsCompressed();

		if (array1.length!=array2.length){
			return false;
		}

		for (int i=0;i<array1.length;i++){
			if (array1[i]!=array2[i]){
				return false;
			}
		}

		return true;
	}catch (ImageException e){
		return false;
	}
}

/**
 * This method was created by a SmartGuide.
 * @return cbit.image.FileImage
 * @param images cbit.image.FileImage[]
 * @exception java.lang.Exception The exception description.
 */
public static VCImage concatenateZSeries(VCImage images[]) throws ImageException {
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
	VCImage vcImage = new VCImageUncompressed(null,bigBuffer,new org.vcell.util.Extent(extent0.getX(),extent0.getY(),extent0.getZ()*images.length),nX,nY,nZ);
	return vcImage;
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(evt);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, int oldValue, int newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, boolean oldValue, boolean newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
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
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 */
public KeyValue getKey() {
	if(version != null){
		return version.getVersionKey();
	}
	return null;
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
 * Insert the method's description here.
 * Creation date: (6/5/00 2:38:10 PM)
 * @return int
 */
public int getNumPixelClasses() {
	return fieldPixelClasses.length;
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
 * This method was created in VisualAge.
 * @return byte
 * @param x int
 * @param y int
 * @param z int
 */
public byte getPixel(int x, int y, int z) throws ImageException {
	if (x<0||x>=numX||y<0||y>=numY||z<0||z>=numZ){
		throw new IllegalArgumentException("("+x+","+y+","+z+") is not inside (0,0,0) and ("+(numX-1)+","+(numY-1)+","+(numZ-1)+")");
	}
	int index = x + numX*(y + z*numY); 
	return (byte) getPixels()[index];
}


/**
 * Gets the pixelClasses property (cbit.image.VCPixelClass[]) value.
 * @return The pixelClasses property value.
 * @see #setPixelClasses
 */
public cbit.image.VCPixelClass[] getPixelClasses() {
	return fieldPixelClasses;
}


public VCPixelClass getPixelClasses(int index) {
	return getPixelClasses()[index];
}


public VCPixelClass getPixelClassFromName(String pixelClassName) {
	for (int i = 0; i < fieldPixelClasses.length; i++){
		VCPixelClass pixelClass = fieldPixelClasses[i];
		if (pixelClass.getPixelClassName().equals(pixelClassName)){
			return pixelClass;
		}
	}
	return null;
}


public VCPixelClass getPixelClassFromPixelValue(int pixelValue) {
	for (int i = 0; i < fieldPixelClasses.length; i++){
		VCPixelClass pixelClass = fieldPixelClasses[i];
		if (pixelClass.getPixel()==pixelValue){
			return pixelClass;
		}
	}
	return null;
}


public abstract byte[] getPixels() throws ImageException;


public abstract byte[] getPixelsCompressed() throws ImageException;

public static byte[] deflate(byte[] uncompressed) throws IOException {
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	DeflaterOutputStream dos = new DeflaterOutputStream(bos);
	//DeflaterOutputStream dos = new DeflaterOutputStream(bos,new Deflater(5,false));
	dos.write(uncompressed,0,uncompressed.length);
	dos.close();
	byte[] compressed = bos.toByteArray();
	if (lg.isTraceEnabled()) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String hashUncompressed = Hex.toString(digest.digest(uncompressed));
			String hashCompressed = Hex.toString(digest.digest(compressed));
			lg.trace("Deflating using DeflaterOutputStream: compressed pixels(" + compressed.length + "): hash=" + hashCompressed.substring(0, 6) +
					", uncompressed pixels(" + uncompressed.length + "): hash=" + hashUncompressed.substring(0, 6));
		} catch (Exception e) {}
	}
	return compressed;
}

@Deprecated
private static byte[] deflate0(byte[] uncompressed) throws IOException {
	byte[] output = new byte[uncompressed.length];
	Deflater deflater = new Deflater();
	deflater.setInput(uncompressed);
	deflater.finish();
	int compressedDataLength = deflater.deflate(output,0 , output.length, Deflater.SYNC_FLUSH);
	byte[] compressed = new byte[compressedDataLength];
	System.arraycopy(output,0,compressed,0,compressedDataLength);
	if (lg.isTraceEnabled()) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String hashUncompressed = Hex.toString(digest.digest(uncompressed));
			String hashCompressed = Hex.toString(digest.digest(compressed));
			lg.trace("Deflating using Deflater: compressed pixels(" + compressed.length + "): hash=" + hashCompressed.substring(0, 6) +
					", uncompressed pixels(" + uncompressed.length + "): hash=" + hashUncompressed.substring(0, 6));
		} catch (Exception e) {}
	}
	return compressed;
}

public static byte[] inflate(byte[] compressed) throws IOException {
	byte[] uncompressed = CompressionUtils.uncompress(compressed);
	if (lg.isTraceEnabled()) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String hashUncompressed = Hex.toString(digest.digest(uncompressed));
			String hashCompressed = Hex.toString(digest.digest(compressed));
			lg.trace("Inflating using InflaterInputStream: compressed pixels(" + compressed.length + "): hash=" + hashCompressed.substring(0, 6) +
					", uncompressed pixels(" + uncompressed.length + "): hash=" + hashUncompressed.substring(0, 6));
		} catch (Exception e) {
			lg.warn("Unexpected Exception occurred while parsing inflation results: " + e.getMessage(), e);
		}
	}
	return uncompressed;
}

public static byte[] inflate(byte[] compressed, int uncompressedSize) throws IOException, DataFormatException {
	Inflater inflater = new Inflater();
	inflater.setInput(compressed);

	byte[] uncompressed = new byte[uncompressedSize];
	int inflated_size = inflater.inflate(uncompressed);
	if (inflated_size != uncompressedSize){
		throw new RuntimeException("unexpected uncompressed size");
	}
	if (lg.isTraceEnabled()) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String hashUncompressed = Hex.toString(digest.digest(uncompressed));
			String hashCompressed = Hex.toString(digest.digest(compressed));
			lg.trace("Inflating using Inflater: compressed pixels(" + compressed.length + "): hash=" + hashCompressed.substring(0, 6) +
					", uncompressed pixels(" + uncompressed.length + "): hash=" + hashUncompressed.substring(0, 6));
		} catch (Exception e) {}
	}
	return uncompressed;
}


	protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


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


public Version getVersion() {
	return version;
}


protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
	if (vetoPropertyChange == null) {
		vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	};
	return vetoPropertyChange;
}


public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


protected final void initPixelClasses() throws ImageException {
	Vector pixelClassList = new Vector();
	int uniquePixels[] = getUniquePixelValues();
	if(uniquePixels.length > 256){
		throw new PixelClassLimitException(this.getClass().getName()+" has "+uniquePixels.length+" distinct values, exceeding limit of 256");
	}
	VCPixelClass pixelClasses[] = new VCPixelClass[uniquePixels.length];
	for (int i=0;i<uniquePixels.length;i++){
		pixelClasses[i] = new VCPixelClass(null,VCPixelClass.DEFAULT_BASE_NAME+(i+1),uniquePixels[i]);
	}
	this.fieldPixelClasses = pixelClasses;
}


public void refreshDependencies() {
	//
	// this is where you refresh volatile (e.g. propertyChangeListeners and vetoableChangeListeners)
	//
	//removePropertyChangeListener(this);
	//addPropertyChangeListener(this);
	removeVetoableChangeListener(this);
	addVetoableChangeListener(this);
}


public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}


public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}


public synchronized void removeVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
}


public void setDescription(java.lang.String description) {
	String oldValue = fieldDescription;
	fieldDescription = description;
	firePropertyChange("description", oldValue, description);
}


public void setExtent(org.vcell.util.Extent newExtent) {
	org.vcell.util.Extent oldExtent = this.extent;
	this.extent = newExtent;
	firePropertyChange("extent",oldExtent,newExtent);
}


public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
	String oldValue = fieldName;
	fireVetoableChange("name", oldValue, name);
	fieldName = name;
	firePropertyChange("name", oldValue, name);
}

public void setPixelClasses(cbit.image.VCPixelClass[] pixelClasses) throws java.beans.PropertyVetoException {
	cbit.image.VCPixelClass[] oldValue = fieldPixelClasses;
	fireVetoableChange("pixelClasses", oldValue, pixelClasses);
	fieldPixelClasses = pixelClasses;
	firePropertyChange("pixelClasses", oldValue, pixelClasses);
}


public String toString() {
	return "VCImage@"+Integer.toHexString(hashCode())+"("+((version!=null)?version.toString():getName())+")";
}

public long countPixelsByValue(byte value) throws ImageException {
	byte[] pixels = getPixels();
	long count = 0;
	for (int i = 0; i < pixels.length; i++) {
		if (pixels[i]==value){
			count++;
		}
	}
	return count;
}

public void vetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
	
	if (evt.getSource() == this && evt.getPropertyName().equals("pixelClasses")){

		VCPixelClass newPixelClasses[] = (VCPixelClass[])evt.getNewValue();
		//
		// Check image region names are non-null and unique
		//
		for(int i=0;i<newPixelClasses.length;i+= 1){
			if(newPixelClasses[i].getPixelClassName() == null || newPixelClasses[i].getPixelClassName().length() == 0){
				throw new java.beans.PropertyVetoException("VCPixelClass names index="+i+" must be non-null",evt);
			}
		}
		for(int i=0;i<newPixelClasses.length;i+= 1){
			for(int j=0;j<newPixelClasses.length;j+= 1){
				if((i != j) && newPixelClasses[i].getPixelClassName().equals(newPixelClasses[j].getPixelClassName())){
					throw new java.beans.PropertyVetoException("VCPixelClass names id="+i+" and id="+j+" ("+newPixelClasses[i].getPixelClassName()+") cannot be the same",evt);
				}
			}
		}
		//
		// Check all unique pixels values have a VCPixelClass
		// 
		boolean flag = false;
		int pixelVals[] = null;
		try {
			pixelVals = getUniquePixelValues();
		}catch (ImageException e){
			throw new RuntimeException("unexpected exception: "+e.getMessage(), e);
		}
		for (int c = 0; c < pixelVals.length; c++) {
			flag = false;
			for (int d = 0; d < newPixelClasses.length; d++) {
				if (newPixelClasses[d].getPixel() == pixelVals[c]){
					flag = true;
					break;
				}
			}
			if (flag == false){
				throw new java.beans.PropertyVetoException("No VCPixelValue for pixel value " + pixelVals[c],evt);
			}
		}

		//
		// Check all VCPixelClasses have a unique pixel in this image
		// 
		for (int c = 0; c < newPixelClasses.length; c++) {
			flag = false;
			for (int d = 0; d < pixelVals.length; d++) {
				if (newPixelClasses[c].getPixel() == pixelVals[d]){
					flag = true;
					break;
				}
			}
			if (flag == false){
				throw new java.beans.PropertyVetoException("No pixel value " + newPixelClasses[c].getPixel() + " found in image (for VCPixelValue)",evt);
			}
		}
		
	}
}
}

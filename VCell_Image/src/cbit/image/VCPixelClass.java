package cbit.image;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.Cacheable;
import cbit.util.KeyValue;
import cbit.util.Matchable;
import cbit.util.Compare;
import cbit.util.Immutable;
/**
 * This type was created in VisualAge.
 */
public class VCPixelClass implements java.io.Serializable, Cacheable, Immutable, Matchable {
	private int pixel;
	private String pixelClassName;
	private KeyValue key = null;

	public final static String DEFAULT_BASE_NAME = "PixelClass";

/**
 * VCImageRegion constructor comment.
 */
public VCPixelClass(KeyValue key,String argPixelClassName,int pixel) {
	this.key = key;
	this.pixelClassName = argPixelClassName;
	this.pixel = pixel;
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2004 2:03:32 PM)
 */
public void clearKey() {
	key = null;
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (!(obj instanceof VCPixelClass)){
		return false;
	}
	VCPixelClass pc = (VCPixelClass)obj;

	if (pixel != pc.pixel){
		return false;
	}
	if (!Compare.isEqual(pixelClassName,pc.pixelClassName)){
		return false;
	}
	return true;
}


/**
 * This method was created in VisualAge.
 * @param newKey cbit.sql.KeyValue
 */
public KeyValue getKey() {
	return key;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getPixel() {
	return pixel;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getPixelClassName() {
	return pixelClassName;
}
}
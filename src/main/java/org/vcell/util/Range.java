/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;
/**
 * Contains public double variables min and max. 
 */
public class Range implements java.io.Serializable, Cloneable, org.vcell.util.Matchable{
	private double min = 0;
	private double max = 0;

/**
 * Range constructor comment.
 */
public Range() {
}


/**
 * This method was created in VisualAge.
 * @param min double
 * @param max double
 */
public Range(double min, double max) {
	this.min = Math.min(min, max);
	this.max = Math.max(min, max);
	//if (min<0){
		//System.out.println("min="+min+", max="+max);
	//}
}


/**
 * Insert the method's description here.
 * Creation date: (5/25/2001 5:25:14 PM)
 * @return java.lang.Object
 */
public Object clone() {
	Range newRange = null;
	try {
		newRange = (Range)super.clone();
	} catch (CloneNotSupportedException exc) {
		// this should never happen; ignore
	}
	return newRange;
}

public boolean isValid(){
	return !(Double.isNaN(min) || Double.isInfinite(min) || Double.isNaN(max) || Double.isInfinite(max) || (min > max));
}

/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	return equals(obj);
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 1:31:07 AM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if(this == object){
		return true;
	}
	if (object instanceof Range) {
		Range range = (Range)object;
		return (getMin() == range.getMin() && getMax() == range.getMax());
	}
	return false;
}


public double getMax() {
	return max;
}


public double getMin() {
	return min;
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:33:23 PM)
 * @return int
 */
public int hashCode() {
	return toString().hashCode();
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2002 4:21:56 PM)
 * @return cbit.image.Range
 * @param range cbit.image.Range
 * @param factor double
 */
public final static Range multiplyRange(Range range, double factor) {
    if (range != null) {
        return new Range(
            (range.getMax() + range.getMin()) / 2
                - (range.getMax() - range.getMin()) * Math.abs(factor) / 2,
            (range.getMax() + range.getMin()) / 2
                + (range.getMax() - range.getMin()) * Math.abs(factor) / 2);
    } else {
        return null;
    }
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:23:04 PM)
 * @return java.lang.String
 */
public String toString() {
	return "Range: [min: " + min + ", max: " + max + "]";
}
}

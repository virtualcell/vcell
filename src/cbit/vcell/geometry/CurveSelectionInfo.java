/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry;

/**
 * Insert the type's description here.
 * Creation date: (10/15/00 1:10:11 PM)
 * @author: 
 */
public class CurveSelectionInfo implements java.io.Serializable, org.vcell.util.Matchable {
	//
	public static final int NONE_SELECTED = -1;
	//
	public static final int TYPE_CURVE = 0;
	public static final int TYPE_CONTROL_POINT = 1;
	public static final int TYPE_SEGMENT = 2;
	public static final int TYPE_U = 3;

	private static final String TYPE_LABEL[] = { "curve","controlPoint","segment","u" };
	//
	private Curve fieldCurve = null;
	private int fieldType;
	private int fieldControlPoint = NONE_SELECTED;
	private int fieldSegment = NONE_SELECTED;
	private double fieldU = NONE_SELECTED;
	private double fieldUExtended = NONE_SELECTED;
	private int fieldControlPointExtended = NONE_SELECTED;
	private int fieldSegmentExtended = NONE_SELECTED;
	private boolean fieldDirectionNegative = false;
/**
 * CurveSelectionInfo constructor comment.
 */
public CurveSelectionInfo(Curve curve) {
	//curve Selection
	super();
	setCurve(curve);
	setType(TYPE_CURVE);
}
/**
 * CurveSelectionInfo constructor comment.
 */
public CurveSelectionInfo(Curve curve, int type, double index) {
	//Segment or ControlPoint selection
	super();
	if (type == TYPE_CONTROL_POINT) {
		if (!(curve instanceof ControlPointCurve)) {
			throw new IllegalArgumentException("TYPE_CONTROL_POINT must have ControlPointCurve");
		}
		if (index < 0 || index >= ((ControlPointCurve) curve).getControlPointCount()) {
			throw new IllegalArgumentException("index out of range for ControlPointCurve");
		}
		setControlPoint((int) index);
	} else if (type == TYPE_SEGMENT) {
		if (index < 0 || index >= curve.getSegmentCount()) {
			throw new IllegalArgumentException("Segment index out of range for Curve");
		}
		setSegment((int) index);
	} else if (type == TYPE_U) {
		if (index < 0 || index > 1) {
			throw new IllegalArgumentException("U must be between 0 and 1");
		}
		setU(index);
	} else {
		throw new IllegalArgumentException("type must be either TYPE_CONTROL_POINT or TYPE_SEGMENT or TYPE_U");
	}

	setCurve(curve);
	setType(type);
}
/**
 * CurveSelectionInfo constructor comment.
 */
public CurveSelectionInfo(Curve curve,int argBeginSegment,int argEndSegment,boolean bDirectionNegative) {
	//curve Selection
	super();
	setCurve(curve);
	setType(TYPE_SEGMENT);
	this.fieldSegment = argBeginSegment;
	if(argBeginSegment != argEndSegment){
		this.fieldSegmentExtended = argEndSegment;
	}
	this.fieldDirectionNegative = bDirectionNegative;
}
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (this == obj) {
		return true;
	}
	if (obj instanceof CurveSelectionInfo) {
		CurveSelectionInfo csi = (CurveSelectionInfo)obj;
		if (
			getControlPoint() == csi.getControlPoint() &&
			getControlPointExtended() == csi.getControlPointExtended() &&
			getDirectionNegative() == csi.getDirectionNegative() &&
			getSegment() == csi.getSegment() &&
			getSegmentExtended() == csi.getSegmentExtended() &&
			getType() == csi.getType() &&
			getU() == csi.getU() &&
			getUExtended() == csi.getUExtended() &&
			getCurve().compareEqual(csi.getCurve()) 
		) {
			return true;
		}
	}
	return false;
}
/**
 * Gets the controlPoint property (int) value.
 * @return The controlPoint property value.
 * @see #setControlPoint
 */
public int getControlPoint() {
	return fieldControlPoint;
}
/**
 * Gets the controlPointExtended property (int) value.
 * @return The controlPointExtended property value.
 * @see #setControlPointExtended
 */
public int getControlPointExtended() {
	return fieldControlPointExtended;
}
/**
 * Gets the curve property (cbit.vcell.geometry.Curve) value.
 * @return The curve property value.
 * @see #setCurve
 */
public Curve getCurve() {
	return fieldCurve;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/01 5:34:07 PM)
 * @return double
 * @param selectionU double
 */
public double getCurveUfromSelectionU(double selectionU) {
	if (getType() != TYPE_SEGMENT){
		throw new RuntimeException("CurveSelectionInfo.getCurveUfromSelectionU(), cannot call when type!=TYPE_SEGMENT");
	}
	
	int beginSeg = getSegment();
	int endSeg = getSegmentExtended();
	Curve curve = getCurve();
	
	double selectionTransformedU = -1;
	
	if (endSeg == NONE_SELECTED){
		double beginSelectionU = curve.getNonLengthNormalizedBeginU(beginSeg);
		double endSelectionU = curve.getNonLengthNormalizedEndU(beginSeg);
		selectionTransformedU = beginSelectionU + selectionU*(endSelectionU-beginSelectionU);
	}else if (beginSeg<endSeg && getDirectionNegative()==false){
		//
		// from low to high in positive direction
		//
		double beginSelectionU = curve.getNonLengthNormalizedBeginU(beginSeg);
		double endSelectionU = curve.getNonLengthNormalizedEndU(endSeg);
		selectionTransformedU = beginSelectionU + selectionU*(endSelectionU-beginSelectionU);
		
	}else if (beginSeg>endSeg && getDirectionNegative()==true){
		//
		// from high to low in negative direction
		//
		double beginSelectionU = curve.getNonLengthNormalizedEndU(beginSeg);
		double endSelectionU = curve.getNonLengthNormalizedBeginU(endSeg);
		selectionTransformedU = beginSelectionU + selectionU*(endSelectionU-beginSelectionU);
		
	}else if (beginSeg<endSeg && getDirectionNegative()==true){
		//
		// from low to high in negative direction (only works for closed curves)
		//
		if (!curve.isClosed()){
			throw new RuntimeException("SpatialSelectionContour.getIndex(), curve selection is corrupted, begin<end, neg dir, not closed");
		}
		double beginSelectionU = curve.getNonLengthNormalizedEndU(beginSeg);
		double endSelectionU = curve.getNonLengthNormalizedBeginU(endSeg);
		double length = (1.0 - endSelectionU) + beginSelectionU;
		selectionTransformedU = beginSelectionU + selectionU*length;
		if (selectionTransformedU<0){  // modulo wrap around
			selectionTransformedU = 1.0 - selectionTransformedU;
		}
		
	}else if (beginSeg>endSeg && getDirectionNegative()==false){
		//
		// from high to low in positive direction (only works for closed curves)
		//
		if (!curve.isClosed()){
			throw new RuntimeException("SpatialSelectionContour.getIndex(), curve selection is corrupted, begin>end, pos dir, not closed");
		}
		double beginSelectionU = curve.getNonLengthNormalizedBeginU(beginSeg);
		double endSelectionU = curve.getNonLengthNormalizedEndU(endSeg);
		double length = (1.0 - beginSelectionU) + endSelectionU;
		selectionTransformedU = beginSelectionU + selectionU*length;
		if (selectionTransformedU>1.0){  // modulo wrap around
			selectionTransformedU = selectionTransformedU - 1.0;
		}
		
	}
	
	return selectionTransformedU;
}
/**
 * Gets the directionNegative property (boolean) value.
 * @return The directionNegative property value.
 * @see #setDirectionNegative
 */
public boolean getDirectionNegative() {
	return fieldDirectionNegative;
}
/**
 * Gets the segment property (int) value.
 * @return The segment property value.
 * @see #setSegment
 */
public int getSegment() {
	return fieldSegment;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/01 5:49:24 PM)
 * @return int
 */
public int getSegmentCount() {
	int beginSeg = getSegment();
	int endSeg = getSegmentExtended();
	if (endSeg==-1){
		endSeg = beginSeg;  // this means that it is a single point
	}
	
	if (beginSeg==endSeg){
		//
		// single segment selected
		//
		if (beginSeg==NONE_SELECTED){
			return -1;
		}else{
			return 1;
		}
		
	}else if (beginSeg<endSeg && getDirectionNegative()==false){
		//
		// from low to high in positive direction
		//
		return endSeg - beginSeg + 1;
		
	}else if (beginSeg>endSeg && getDirectionNegative()==true){
		//
		// from high to low in negative direction
		//
		return beginSeg - endSeg + 1;
		
	}else if (beginSeg<endSeg && getDirectionNegative()==true){
		//
		// from low to high in negative direction (only works for closed curves)
		//
		if (!getCurve().isClosed()){
			throw new RuntimeException("CurveSelectionInfo.getSegmentCount(), curve selection is corrupted, begin<end, neg dir, not closed");
		}
		return beginSeg + (getCurve().getSegmentCount() - endSeg) + 1;
		
	}else if (beginSeg>endSeg && getDirectionNegative()==false){
		//
		// from high to low in positive direction (only works for closed curves)
		//
		if (!getCurve().isClosed()){
			throw new RuntimeException("CurveSelectionInfo.getIndex(), curve selection is corrupted, begin>end, pos dir, not closed");
		}
		return endSeg + (getCurve().getSegmentCount() - beginSeg) + 1;
		
	}else{
		throw new RuntimeException("cannot happen");
	}
}
/**
 * Gets the segmentExtended property (int) value.
 * @return The segmentExtended property value.
 * @see #setSegmentExtended
 */
public int getSegmentExtended() {
	return fieldSegmentExtended;
}
/**
 * Gets the segment property (int) value.
 * @return The segment property value.
 * @see #setSegment
 */
public int[] getSegmentsInSelectionOrder() {

	if(getSegmentCount() <= 0){
		return null;
	}
	int beginSeg = getSegment();
	int endSeg = getSegmentExtended();
	if (endSeg==-1){
		endSeg = beginSeg;  // this means that it is a single point
	}

	int indexes[] = new int[getSegmentCount()];
	
	if (beginSeg==endSeg){
		indexes[0] = beginSeg;
		
	}else if (beginSeg<endSeg && getDirectionNegative()==false){
		//
		// from low to high in positive direction
		//
		int count = 0;
		for (int seg = beginSeg; seg <= endSeg; seg++){
			indexes[count++] = seg;
		}
		
	}else if (beginSeg>endSeg && getDirectionNegative()==true){
		//
		// from high to low in negative direction
		//
		int count = 0;
		for (int seg = beginSeg; seg >= endSeg; seg--){
			indexes[count++] = seg;
		}
		
	}else if (beginSeg<endSeg && getDirectionNegative()==true){
		//
		// from low to high in negative direction (only works for closed curves)
		//
		if (!getCurve().isClosed()){
			throw new RuntimeException("CurveSelectionInfo.getSegmentCount(), curve selection is corrupted, begin<end, neg dir, not closed");
		}
		int count = 0;
		for (int seg = beginSeg; seg >= 0; seg--){
			indexes[count++] = seg;
		}
		for (int seg = getCurve().getSegmentCount()-1; seg >= endSeg; seg--){
			indexes[count++] = seg;
		}
		
	}else if (beginSeg>endSeg && getDirectionNegative()==false){
		//
		// from high to low in positive direction (only works for closed curves)
		//
		if (!getCurve().isClosed()){
			throw new RuntimeException("CurveSelectionInfo.getIndex(), curve selection is corrupted, begin>end, pos dir, not closed");
		}
		int count = 0;
		for (int seg = beginSeg; seg <= getCurve().getSegmentCount()-1; seg++){
			indexes[count++] = seg;
		}
		for (int seg = 0; seg <= endSeg; seg++){
			indexes[count++] = seg;
		}
	}else{
		throw new RuntimeException("Segment selection order Error");
	}
	
	return indexes;
}
/**
 * Gets the type property (int) value.
 * @return The type property value.
 * @see #setType
 */
public int getType() {
	return fieldType;
}
/**
 * Gets the u property (double) value.
 * @return The u property value.
 * @see #setU
 */
public double getU() {
	return fieldU;
}
/**
 * Gets the uExtended property (double) value.
 * @return The uExtended property value.
 * @see #setUExtended
 */
public double getUExtended() {
	return fieldUExtended;
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/00 1:46:06 PM)
 * @return boolean
 */
private boolean isCrossing() {
	if (getCurve().isClosed()) {
		if (getSegment() != NONE_SELECTED && getSegmentExtended() != NONE_SELECTED) {
			if (getDirectionNegative()) {
				return (getSegment() - getSegmentExtended()) < 0;
			} else {
				return (getSegmentExtended() - getSegment()) < 0;
			}
		}
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/00 1:43:03 PM)
 * @return boolean
 * @param segmentOfInterest int
 */
public boolean isSegmentSelected(int soi) {
	//soi SegmentOfInterest
	if (getSegment() == NONE_SELECTED || soi < 0 || soi >= getCurve().getSegmentCount()) {
		return false;
	}
	if (getSegmentExtended() == NONE_SELECTED) {
		if (soi == getSegment()) {
			return true;
		}
	} else {
		if (isCrossing()) {
			if (getDirectionNegative()) {
				if (soi >= getSegmentExtended() || soi <= getSegment()) {
					return true;
				}
			} else {
				if (soi >= getSegment() || soi <= getSegmentExtended()) {
					return true;
				}
			}
		} else {
			if (getDirectionNegative()) {
				if (soi >= getSegmentExtended() && soi <= getSegment()) {
					return true;
				}
			} else {
				if (soi >= getSegment() && soi <= getSegmentExtended()) {
					return true;
				}
			}
		}
	}
	//
	return false;
}
/**
 * Sets the controlPoint property (int) value.
 * @param controlPoint The new value for the property.
 * @see #getControlPoint
 */
private void setControlPoint(int controlPoint) {
	fieldControlPoint = controlPoint;
}
/**
 * Sets the curve property (cbit.vcell.geometry.Curve) value.
 * @param curve The new value for the property.
 * @see #getCurve
 */
private void setCurve(Curve curve) {
	Curve oldValue = fieldCurve;
	fieldCurve = curve;
	//firePropertyChange("curve", oldValue, curve);
}
/**
 * Sets the directionNegative property (boolean) value.
 * @param directionNegative The new value for the property.
 * @see #getDirectionNegative
 */
private void setDirectionNegative(boolean directionNegative) {
	fieldDirectionNegative = directionNegative;
}
/**
 * Sets the segment property (int) value.
 * @param segment The new value for the property.
 * @see #getSegment
 */
private void setSegment(int segment) {
	fieldSegment = segment;
}
/**
 * Sets the segmentExtended property (int) value.
 * @param segmentExtended The new value for the property.
 * @see #getSegmentExtended
 */
public void setSegmentExtended(int segmentExtended) {
	if (getSegment() == NONE_SELECTED) {
		setSegment(segmentExtended);
		return;
	}
	if (getSegment() == segmentExtended) {
		fieldSegmentExtended = NONE_SELECTED;
		return;
	}
	//Calulate direction
	if (fieldSegmentExtended == NONE_SELECTED || !getCurve().isClosed()) {
		int diff = segmentExtended - getSegment();
		if (getCurve().isClosed() && (Math.abs(diff) + 1) == getCurve().getSegmentCount()) {
			setDirectionNegative((diff * -1) < 0);
		} else {
			setDirectionNegative(diff < 0);
		}
	}
	//
	fieldSegmentExtended = segmentExtended;
}
/**
 * Sets the type property (int) value.
 * @param type The new value for the property.
 * @see #getType
 */
private void setType(int type) {
	int oldValue = fieldType;
	fieldType = type;
	//firePropertyChange("type", new Integer(oldValue), new Integer(type));
}
/**
 * Sets the u property (double) value.
 * @param u The new value for the property.
 * @see #getU
 */
private void setU(double u) {
	fieldU = u;
}
/**
 * Sets the uExtended property (double) value.
 * @param uExtended The new value for the property.
 * @see #getUExtended
 */
public void setUExtended(double uExtended) {
	if (getU() == NONE_SELECTED) {
		setU(uExtended);
		return;
	}
	if (getU() == uExtended) {
		return;
	}
	fieldUExtended = uExtended;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/01 3:14:59 PM)
 * @return java.lang.String
 */
public String toString() {
	return "CurveSelectionInfo(type = "+TYPE_LABEL[fieldType]+", curve="+getCurve()+")";
}
}

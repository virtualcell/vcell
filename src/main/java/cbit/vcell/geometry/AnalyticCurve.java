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

import java.util.Map;
import org.vcell.util.Coordinate;
import org.vcell.util.Matchable;

import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
/**
 * This type was created in VisualAge.
 *  JMW : we need to fix this class so that all exceptions/error
 *  handling is handled elegantly...either the object creation
 *  should fail, else everything should work fine...else we
 *  can use assertions...but getBeginningCoordinate () and
 *  getEndingCoordinate () must not throw exceptions.
 *  The problem was that we were declaring AnalyticCurve-specific
 *  exceptions in the abstract base class Curve...which violates
 *  a fundamental OO design principle.
 */
public class AnalyticCurve extends Curve implements SymbolTable {
	private Expression expX = null;
	private Expression expY = null;
	private Expression expZ = null;
	private Coordinate offset = new Coordinate(0,0,0);	
/**
 * AnalyticCurve constructor comment.
 */
public AnalyticCurve(Expression x, Expression y, Expression z) throws ExpressionBindingException {
	// cbit.util.Assertion.assert (x != null && y != null && z != null);
	this.expX = x;
	this.expY = y;
	this.expZ = z;
	expX.bindExpression(this);
	expY.bindExpression(this);
	expZ.bindExpression(this);
}
/**
 * addOffset method comment.
 */
protected void addOffsetPrivate(Coordinate offset) {
    this.offset = new Coordinate(
		this.offset.getX() + offset.getX(),
		this.offset.getY() + offset.getY(),
		this.offset.getZ() + offset.getZ());
}
/**
 * Insert the method's description here.
 * Creation date: (8/8/00 3:45:00 PM)
 * @return java.lang.Object
 */
public Object clone() {
    AnalyticCurve ac = (AnalyticCurve) super.clone();
    ac.expX = expX;
    ac.expY = expY;
    ac.expZ = expZ;
    ac.offset = new Coordinate(offset.getX(), offset.getY(), offset.getZ());
    return ac;
}
/**
 * compareEqual method comment.
 */
public boolean compareEqual(Matchable obj) {
	if (!super.compareEqual(obj)) {
		return false;
	}
	if(!(obj instanceof AnalyticCurve)){
		return false;
	}
	AnalyticCurve ac = (AnalyticCurve)obj;
	if(!expX.compareEqual(ac.expX)){
		return false;
	}
	if(!expY.compareEqual(ac.expY)){
		return false;
	}
	if(!expZ.compareEqual(ac.expZ)){
		return false;
	}
	if(!offset.compareEqual(ac.offset)){
		return false;
	}
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 7:05:31 PM)
 */
protected SampledCurve createSampledCurve(int numSamples) {
	SampledCurve sc = new SampledCurve(this, numSamples);
	sc.addOffset(offset);
	return sc;
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/00 6:33:32 PM)
 * @return cbit.vcell.geometry.Coordinate
 */
public Coordinate getAnalyticOffset() {
	return offset;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 6:49:09 PM)
 * @return int
 */
protected int getDefaultNumSamples() {
	return 20;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public SymbolTableEntry getEntry(String identifier) {
	ReservedParameterSymbol symbol = ReservedParameterSymbol.fromString(identifier);
	if (!symbol.isU()){
		throw new RuntimeException("found identifier "+identifier+", expecting "+ReservedParameterSymbol.getU());
	}
	return symbol;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:52:39 PM)
 * @return int
 */
public int getSegmentCount() {
	return 1;
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param values double[]
 */
private double getX(double values[]) {
    double returnValue = 0.0;
    try {
        returnValue = expX.evaluateVector(values);
    } catch (ExpressionException r) {
        throw (new RuntimeException(r.getMessage()));
    }
    return (returnValue + offset.getX());
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param u double
 */
public double getX(double u) {
	ReservedParameterSymbol.getU().setIndex(0);
	return getX(new double[] { u });
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param values double[]
 */
private double getY(double values[]) {
    double returnValue = 0.0;
    try {
        returnValue = expY.evaluateVector(values);
    } catch (ExpressionException r) {
        throw (new RuntimeException(r.getMessage()));
    }
    return (returnValue + offset.getY());
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param u double
 */
public double getY(double u) {
	ReservedParameterSymbol.getU().setIndex(0);
	return getY(new double[] { u });
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param values double[]
 */
private double getZ(double values[]) {
    double returnValue = 0.0;
    try {
        returnValue = expZ.evaluateVector(values);
    } catch (ExpressionException r) {
        throw (new RuntimeException(r.getMessage()));
    }
    return (returnValue + offset.getZ());
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param u double
 */
public double getZ(double u) {
	ReservedParameterSymbol.getU().setIndex(0);
	return getZ(new double[] { u });
}
/**
 * isValid method comment.
 */
public boolean isValid() {
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 12:51:05 PM)
 * @return int
 * @param pickCoord cbit.vcell.geometry.Coordinate
 */
public int pickSegment(Coordinate pickCoord,double minPickDistance) {
	return getSampledCurve().getDistanceTo(pickCoord) <= minPickDistance ? 0 : Curve.NONE_SELECTED;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 5:44:46 PM)
 */
public int setDesiredSampling(int argNumSamplePoints) {
	setNumSamplePoints(argNumSamplePoints);
	return getNumSamplePoints();
}
public void getEntries(Map<String, SymbolTableEntry> entryMap) {
	entryMap.put(ReservedParameterSymbol.getU().getName(), ReservedParameterSymbol.getU());	
}

}

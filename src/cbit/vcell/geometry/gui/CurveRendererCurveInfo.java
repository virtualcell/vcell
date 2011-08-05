/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;
import cbit.vcell.geometry.Curve;
import cbit.vcell.geometry.SampledCurve;
import java.awt.geom.*;

import org.vcell.util.Coordinate;
//
public class CurveRendererCurveInfo {
	//
	public static final double LWM_NONE = 0;
	//
	private Curve curve;
	private double lineWidthMultiplier = LWM_NONE;
	private int[] segmentColors = null;
	private int[] segmentIndexes = null;
	private boolean bEditable = true;
	private boolean bSelectable = true;
	private int subSelectionType = CurveRenderer.SUBSELECTION_NONE;
	private boolean bVisible = true;
	//cacheing stuff
	private Point2D.Double[] projectedCurvePoints = null;
	private int projectedFromSampleID;
	private Point2D.Double scalingForSample = null;
	private int normalAxisForSample;

	public CurveRendererCurveInfo(Curve argCurve) {
		curve = argCurve;
	}


private void calculateProjectedPoints(SampledCurve projectedFromSample) {
	//projectedFromSample = null;
	//projectedCurvePoints = null;
	//scalingForSample = scaling2D;
	//normalAxisForSample = axis;
	//
	//projectedFromSample = getCurve().getSampledCurve();
	int cpCount = projectedFromSample.getControlPointCount();
	projectedCurvePoints = new Point2D.Double[cpCount];
	java.util.Vector controlPointsV = projectedFromSample.getControlPointsVector();
	for (int i = 0; i < cpCount; i += 1) {
		projectedCurvePoints[i] = Coordinate.get2DProjection((Coordinate) controlPointsV.elementAt(i), normalAxisForSample);
	}
	for (int c = 0; c < projectedCurvePoints.length; c += 1) {
		scale2DPoint(projectedCurvePoints[c], scalingForSample);
	}
}


public Point2D.Double[] fetchProjectedCurvePoints(Point2D.Double scaling2D, int axis) {
	//
	SampledCurve currentSample = curve.getSampledCurve();
	//
	if (currentSample.getControlPointCount() != 0 && scaling2D != null) {
		if (projectedCurvePoints == null || projectedFromSampleID != curve.getSampledCurveID() || scalingForSample != scaling2D || axis != normalAxisForSample) {
			scalingForSample = scaling2D;
			normalAxisForSample = axis;
			calculateProjectedPoints(currentSample);
			projectedFromSampleID = curve.getSampledCurveID();
		}
	}else{
		projectedCurvePoints = null;
	}
	return projectedCurvePoints;
}


	public Curve getCurve() {
		return curve;
	}


	public double getLineWidthMultiplier() {
		return lineWidthMultiplier;
	}


	public int[] getSegmentColors() {
		return segmentColors;
	}


	public int[] getSegmentIndexes() {
		return segmentIndexes;
	}


	public int getSubSelectionType() {
		return subSelectionType;
	}


	public boolean isEditable() {
		return bEditable && isSelectable();
	}


	public boolean isSelectable() {
		return bSelectable && isVisible();
	}


	public boolean isVisible() {
		return bVisible;
	}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 4:18:38 PM)
 */
public static final Point2D.Double projectAndScale3DPoint(Coordinate point3D, int normalAxis, Point2D.Double scaling2D) {
	Point2D.Double point2D = Coordinate.get2DProjection(point3D, normalAxis);
	scale2DPoint(point2D, scaling2D);
	return point2D;
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 4:18:38 PM)
 */
public static final void scale2DPoint(Point2D.Double p2d, Point2D.Double scaling2D) {
	double pX = p2d.getX() * scaling2D.getX();
	double pY = p2d.getY() * scaling2D.getY();
	p2d.setLocation(pX, pY);
}


	public void setEditable(boolean editable) {
		bEditable = editable && bVisible;
	}


	public void setLineWidthMultiplier(double argLineWidthMultiplier) {
		lineWidthMultiplier = argLineWidthMultiplier;
	}


	public void setSegmentColors(int[] argSegmentColors) {
		segmentColors = argSegmentColors;
	}


	public void setSegmentIndexes(int[] argSegmentIndexes) {
		segmentIndexes = argSegmentIndexes;
	}


	public void setSelectable(boolean selectable) {
		bSelectable = selectable;
	}


	public void setSubSelectionType(int argSubSelectionType) {
		subSelectionType = argSubSelectionType;
	}


	public void setVisible(boolean visible) {
		bVisible = visible;
	}
}

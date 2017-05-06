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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.vcell.util.BeanUtils;
import org.vcell.util.Coordinate;
import org.vcell.util.DrawPaneModel;

import cbit.image.DisplayAdapterService;
import cbit.vcell.solvers.CartesianMesh;

public class CurveRenderer implements DrawPaneModel {
	//
	DisplayAdapterService displayAdapterService;
	java.awt.TexturePaint highlightTexture = null;
	//
	protected Hashtable<Curve, CurveRendererCurveInfo> curveTable = new Hashtable<Curve, CurveRendererCurveInfo>();
	//
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private int fieldNormalAxis;
	private boolean fieldSelectionValid = false;
	private cbit.vcell.geometry.CurveSelectionInfo fieldSelection = null;
	private cbit.vcell.geometry.CurveChecker fieldCurveChecker = null;
	private org.vcell.util.Coordinate fieldWorldDelta = null;
	private org.vcell.util.Coordinate fieldWorldOrigin = null;
	private java.awt.geom.Point2D.Double fieldScaling2D = null;
	private java.awt.geom.Point2D.Double fieldOrigin2D = null;
	//
	public static final int SUBSELECTION_NONE = 0;	
	public static final int SUBSELECTION_SEGMENT = 1;	
	public static final int SUBSELECTION_U = 2;
	public static final int SUBSELECTION_CONTROL_POINT = 3;
	//
	private double fieldDefaultLineWidthMultiplier;
	private boolean fieldAntialias = true;
	
	private CartesianMesh cartesianMesh = null;
	
	public void setCartesianMesh(CartesianMesh cartmesh){
		cartesianMesh = cartmesh;
	}


/**
 * CurvePainter constructor comment.
 */
public CurveRenderer(DisplayAdapterService displayAdapterService) {
	super();
	this.displayAdapterService = displayAdapterService;
	setNormalAxis(Coordinate.Z_AXIS);
	setWorldDelta(new Coordinate(1,1,1));
	setWorldOrigin(new Coordinate(0,0,0));
	setDefaultLineWidthMultiplier(1.0);
	//pattern for highlightTexture
	java.awt.image.BufferedImage pattern1 = new java.awt.image.BufferedImage(4,4,java.awt.image.BufferedImage.TYPE_INT_RGB);
	pattern1.setRGB(0,0,4,4,
		new int[] {
				Color.lightGray.getRGB(),Color.lightGray.getRGB(),Color.white.getRGB(),Color.white.getRGB(),
				Color.white.getRGB(),Color.white.getRGB(),Color.lightGray.getRGB(),Color.lightGray.getRGB(),
				Color.lightGray.getRGB(),Color.lightGray.getRGB(),Color.white.getRGB(),Color.white.getRGB(),
				Color.white.getRGB(),Color.white.getRGB(),Color.lightGray.getRGB(),Color.lightGray.getRGB()
			},
		0,4);
	highlightTexture = new java.awt.TexturePaint(pattern1,new java.awt.Rectangle(0,0,4,4));
	//pattern for nonHighlightTexture
	java.awt.image.BufferedImage pattern2 = new java.awt.image.BufferedImage(4,4,java.awt.image.BufferedImage.TYPE_INT_RGB);
	pattern2.setRGB(0,0,4,4,
		new int[] {
				Color.gray.getRGB(),Color.gray.getRGB(),Color.lightGray.getRGB(),Color.lightGray.getRGB(),
				Color.lightGray.getRGB(),Color.lightGray.getRGB(),Color.gray.getRGB(),Color.gray.getRGB(),
				Color.gray.getRGB(),Color.gray.getRGB(),Color.lightGray.getRGB(),Color.lightGray.getRGB(),
				Color.lightGray.getRGB(),Color.lightGray.getRGB(),Color.gray.getRGB(),Color.gray.getRGB()
			},
		0,4);
//	nonHighlightTexture = new java.awt.TexturePaint(pattern2,new java.awt.Rectangle(0,0,4,4));
	////pattern for nonHighlightTexture
	//java.awt.image.BufferedImage pattern2 = new java.awt.image.BufferedImage(4,4,java.awt.image.BufferedImage.TYPE_INT_ARGB);
	//pattern2.setRGB(0,0,4,4,
		//new int[] {
				//Color.gray.getRGB()&0xFFFFFFFF,Color.gray.getRGB()&0xFFFFFFFF,Color.lightGray.getRGB()&0xFFFFFFFF,Color.lightGray.getRGB()&0xFFFFFFFF,
				//Color.lightGray.getRGB()&0xFFFFFFFF,Color.lightGray.getRGB()&0xFFFFFFFF,Color.gray.getRGB()&0xFFFFFFFF,Color.gray.getRGB()&0xFFFFFFFF,
				//Color.gray.getRGB()&0xFFFFFFFF,Color.gray.getRGB()&0xFFFFFFFF,Color.lightGray.getRGB()&0xFFFFFFFF,Color.lightGray.getRGB()&0xFFFFFFFF,
				//Color.lightGray.getRGB()&0xFFFFFFFF,Color.lightGray.getRGB()&0xFFFFFFFF,Color.gray.getRGB()&0xFFFFFFFF,Color.gray.getRGB()&0xFFFFFFFF,
			//},
		//0,4);
	//nonHighlightTexture = new java.awt.TexturePaint(pattern2,new java.awt.Rectangle(0,0,4,4));
}


public static String getROIDescriptions(Coordinate pointOfInterest,CurveRenderer curveRenderer){
	CurveSelectionInfo[] curveCSIArr =
		curveRenderer.getCloseCurveSelectionInfos(pointOfInterest);
	String infoS = "";
	boolean bMultiple = false;
	if(curveCSIArr != null){
		for(int i = 0;i < curveCSIArr.length;i+= 1){
			if(!curveRenderer.getRenderPropertySelectable(curveCSIArr[i].getCurve())){
				continue;
			}
			if(infoS.length()> 0 && curveCSIArr[i].getCurve().getDescription() != null){
				bMultiple = true;
				infoS+=",";
			}
			infoS+= (curveCSIArr[i].getCurve().getDescription() != null?curveCSIArr[i].getCurve().getDescription():"");
		}
	}
	return (infoS.length() == 0?null:"roi"+(bMultiple?"s":"")+"("+infoS+")");
}

/**
 * This method was created in VisualAge.
 * @param curve cbit.vcell.geometry.Curve
 */
public void addCurve(Curve curve) {
	if (curve != null) {
		CurveRendererCurveInfo crci = new CurveRendererCurveInfo(curve);
		curveTable.put(curve, crci);
	}
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (8/8/00 5:17:17 PM)
 * @return boolean
 * @param curve cbit.vcell.geometry.Curve
 */
public boolean curveSatisfyWorldConstraints(Curve curve) {
	if(getCurveChecker() != null){
		return getCurveChecker().curveSatisfyWorldConstraints(curve);
	}
	return true;
}


/**
 * This method was created in VisualAge.
 * @param coord cbit.vcell.geometry.Coordinate
 */
private double distanceToProjectedCurve(java.awt.geom.Point2D.Double pickPoint, CurveRendererCurveInfo crci) {
	Curve curve = crci.getCurve();
	Point2D.Double[] p2d = crci.fetchProjectedCurvePoints(getScaling2D(),getNormalAxis());
	if(curve instanceof SinglePoint){
		return p2d[0].distance(pickPoint);
	}
	int segmentCount = curve.getSampledCurve().getSegmentCount();
	int samplePointCount = p2d.length;
	double shortestDistance = Double.MAX_VALUE;
	Point2D.Double p0 = null;
	Point2D.Double p1 = null;
	for (int i = 0; i < segmentCount; i += 1) {
		p0 = p2d[i];
		p1 = p2d[(i + 1) % samplePointCount];
		//Calculate U
		double v2XLength = (p1.getX() - p0.getX());
		double v2YLength = (p1.getY() - p0.getY());
		//DotProduct v1*v2
		double dotProduct = (pickPoint.getX() - p0.getX()) * v2XLength + (pickPoint.getY() - p0.getY()) * v2YLength;
		//Magnitude v2 squared, don't take square root so that u return is normalized relative to vertex->v2
		double magnitudeSquared = v2XLength * v2XLength + v2YLength * v2YLength;
		double u = dotProduct / magnitudeSquared;
		//double u = calculateUOfV1AlongV2(p0.getX(), p0.getY(), 0, pickPoint.getX(), pickPoint.getY(), 0, p1.getX(), p1.getY(), 0);
		double distance = shortestDistance;
		if (u >= 0.0 && u <= 1.0) {
			double uX = p0.getX() + ((p1.getX() - p0.getX()) * u);
			double uY = p0.getY() + ((p1.getY() - p0.getY()) * u);
			distance = pickPoint.distance(uX, uY);
		}else{
			distance = Math.min(pickPoint.distance(p0.getX(), p0.getY()),pickPoint.distance(p1.getX(), p1.getY()));
		}
		if (distance < shortestDistance) {
			shortestDistance = distance;
		}
	}
	return shortestDistance;
}


/**
 * This is called when the canvas repaint.
 */
public void draw(java.awt.Graphics g) {
	try {
		if (curveTable.size() == 0) {
			return;
		}
		Point2D.Double scaling2D = getScaling2D();
		Point2D.Double origin2D = getOrigin2D();
		if (scaling2D == null || origin2D == null) {
			return;
		}
		java.awt.Graphics2D g2D = (java.awt.Graphics2D) g;
		//Set clipping so we don't draw off the image in case the image doesn't fill
		//whatever component we are sitting in
		//g2D.setClip(g.getClip().getBounds().intersection(new java.awt.Rectangle(getCrDrawAreaDimension())));
		//
		//
	g2D.scale(1,1);
	g2D.translate(-origin2D.getX(),-origin2D.getY());
		//
		//
		//g2D.translate(xOrigin, yOrigin);
		//java.awt.BasicStroke bs = new java.awt.BasicStroke((float)getLineWidth()); 
		//After transform
		if (getAntialias()){
			g2D.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
		}else{		
			g2D.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_OFF);
		}

		java.awt.BasicStroke basicStroke = null;
		//
		//Draw in order so curves aren't hidden by other curves
		//
		for(int i = 0;i < 4;i+= 1){
			java.util.Enumeration<CurveRendererCurveInfo> curveEnum = curveTable.elements();
			while (curveEnum.hasMoreElements()) {
				CurveRendererCurveInfo crci = curveEnum.nextElement();
				if (!crci.isVisible() ||
					//Selection above everything
					(i == 3 && (getSelection() == null || getSelection().getCurve() != crci.getCurve())) ||
					(i != 3 && getSelection() != null && getSelection().getCurve() == crci.getCurve()) ||
					//SinglePoint above others
					(i == 2 && !(crci.getCurve() instanceof SinglePoint)) ||
					//Selectable things
					(i == 1 && ((crci.getCurve() instanceof SinglePoint) || !crci.isSelectable())) ||
					//not selected,point,selectable
					(i == 0 && ((crci.getCurve() instanceof SinglePoint) || crci.isSelectable()))
					//(i == 0 && (crci.getCurve()instanceof SinglePoint) || (i == 1 && !(crci.getCurve()instanceof SinglePoint)))
					){
					continue;
				}
				if (basicStroke == null || basicStroke.getLineWidth() != getLineWidthMultiplier(crci.getCurve())) {
					//Set LineWidth
					basicStroke = new BasicStroke((float) getLineWidthMultiplier(crci.getCurve()),BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER);
					//basicStroke = new java.awt.BasicStroke((float) getLineWidthMultiplier(crci.getCurve()));
					g2D.setStroke(basicStroke);
				}
				//Do this once because it can be expensive, should be cached better
				boolean bSatisfyWorldConstraints = curveSatisfyWorldConstraints(crci.getCurve());
				drawCurve(g2D, crci, bSatisfyWorldConstraints);
				drawControlPoints(g2D, crci, bSatisfyWorldConstraints);
			}
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/13/00 3:59:42 PM)
 * @param g2d java.awt.Graphics2D
 */
private void drawControlPoints(java.awt.Graphics2D g2D, CurveRendererCurveInfo crci, boolean bSatisfyWorldConstraints) {
	if (getSelection() != null && getSelection().getCurve() == crci.getCurve()) {
		if (crci.getSubSelectionType() != SUBSELECTION_CONTROL_POINT) {
			return;
		}
		if (crci.getCurve() instanceof ControlPointCurve) {
			ControlPointCurve cpCurve = (ControlPointCurve) crci.getCurve();
			int controlPointCount = cpCurve.getControlPointCount();
			double mpd = getLineWidthMultiplier(crci.getCurve()) / 2;
			for (int j = 0; j < controlPointCount; j += 1) {
				Point2D.Double p2d = CurveRendererCurveInfo.projectAndScale3DPoint(cpCurve.getControlPoint(j),getNormalAxis(),getScaling2D());
				double p2dXS = p2d.getX();
				double p2dYS = p2d.getY();
				//
				g2D.setColor(Color.black);
				g2D.draw(new java.awt.geom.Ellipse2D.Double(p2dXS - (mpd / 2)-1, p2dYS - (mpd / 2)-1, mpd+2, mpd+2));
				//
				java.awt.geom.Ellipse2D.Double circle = new java.awt.geom.Ellipse2D.Double(p2dXS - mpd / 2, p2dYS - mpd / 2, mpd, mpd);
				if (!bSatisfyWorldConstraints) {
					g2D.setColor(Color.blue);
				} else {
					g2D.setColor(Color.white);
				}
				g2D.draw(circle);
				if (getSelection().getType() == CurveSelectionInfo.TYPE_CONTROL_POINT && j == getSelection().getControlPoint()) {
					g2D.setColor(Color.red);
					g2D.fill(circle);
				}
			}
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/13/00 3:59:42 PM)
 * @param g2d java.awt.Graphics2D
 */
private void drawCurve(
	java.awt.Graphics2D g2D,
	CurveRendererCurveInfo crci,
	boolean bSatisfyWorldConstraints) {
	if (crci.getCurve().isValid()) {
		SampledCurve sampledCurve = crci.getCurve().getSampledCurve();
		int cpCount = sampledCurve.getControlPointCount();
		Point2D.Double[] p2d = crci.fetchProjectedCurvePoints(getScaling2D(), getNormalAxis());
		//Draw SinglePoint specially
		if (crci.getCurve() instanceof SinglePoint) {
						double mpd = getLineWidthMultiplier(crci.getCurve()) / 2;
						//contrast border
						g2D.setColor(Color.black);
						g2D.draw(new java.awt.geom.Ellipse2D.Double(p2d[0].getX() - (mpd / 2)-1, p2d[0].getY() - (mpd / 2)-1, mpd+2, mpd+2));
						//
						g2D.setColor(Color.gray);
						g2D.draw(new java.awt.geom.Ellipse2D.Double(p2d[0].getX() - mpd / 2, p2d[0].getY() - mpd / 2, mpd, mpd));
						//
						g2D.setColor(Color.white);
						g2D.fill(new java.awt.geom.Ellipse2D.Double(p2d[0].getX() - (mpd / 2)+1, p2d[0].getY() - (mpd / 2)+1, mpd-1, mpd-1));
		} else { //Draw all other SampledCurve as line segments
			Color nonHighlightColor = null;
			if(displayAdapterService != null && displayAdapterService.getSpecialColors() != null){
				nonHighlightColor =
					new Color(displayAdapterService.getSpecialColors()[DisplayAdapterService.FOREGROUND_NONHIGHLIGHT_COLOR_OFFSET]);				
			}
			int segmentCount = sampledCurve.getSegmentCount();
			for (int c = 0; c < segmentCount; c += 1) {
				//Set color
				if (!bSatisfyWorldConstraints) {
					g2D.setColor(Color.blue);
				} else {
					int[] segmentColors = crci.getSegmentColors();
					if (segmentColors == null) {
						if(nonHighlightColor != null){
							g2D.setPaint(nonHighlightColor);
						}else{
							g2D.setPaint(Color.LIGHT_GRAY);
						}
					} else {
						g2D.setColor(new Color(segmentColors[c % segmentCount]));
					}
					if (getSelection() != null && getSelection().getCurve() == crci.getCurve()) {
						g2D.setPaint(highlightTexture);
						//if (crci.getSubSelectionType() == SUBSELECTION_SEGMENT
							//&& getSelection().getType() == CurveSelectionInfo.TYPE_SEGMENT) {
							//if (getSelection().isSegmentSelected(c)) {
								////g2D.setColor(Color.gray);
								//g2D.setPaint(tp);
							//}
						//} else {
							////g2D.setColor(Color.gray);
							//g2D.setPaint(tp);
						//}
					}
				}
				//g2D.drawLine((int) p2dXSOld, (int) p2dYSOld, (int) p2dXSNew, (int) p2dYSNew);
				g2D.drawLine(
					(int) p2d[c].getX(),
					(int) p2d[c].getY(),
					(int) p2d[(c + 1) % cpCount].getX(),
					(int) p2d[(c + 1) % cpCount].getY());

			}
			/*
					if (!bSatisfyWorldConstraints) {
						g2D.setColor(Color.blue);
					} else {
						if (getSelection() != null && getSelection().getCurve() == crci.getCurve()) {
							g2D.setColor(Color.white);
						} else {
							g2D.setColor(Color.black);
						}
					}
					g2D.draw(get2DShape(crci));
				}
				*/
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/15/00 1:40:02 PM)
 * @param mousePoint java.awt.Point
 */
public CurveSelectionInfo extend(Coordinate pickPoint) {
	if(getSelection() == null){
		return null;
	}
	CurveSelectionInfo csi = null;
	if (getSelection().getType() == CurveSelectionInfo.TYPE_SEGMENT) {
		csi = getClosestSegmentSelectionInfo(pickPoint, getSelection().getCurve());
		if (csi != null) {
			if (csi.getCurve() == getSelection().getCurve() && csi.getSegment() != getSelection().getSegmentExtended()) {
				getSelection().setSegmentExtended(csi.getSegment());
			}
		}
	} else if (getSelection().getType() == CurveSelectionInfo.TYPE_U) {
		csi = pickU(pickPoint, getSelection().getCurve());
	}
	return null;
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
 * Insert the method's description here.
 * Creation date: (4/26/2001 3:13:10 PM)
 * @return cbit.vcell.geometry.Curve[]
 */
public Curve[] getAllCurves() {
	Enumeration<Curve> en = curveTable.keys();
	Vector<Curve> v = new Vector<Curve>();
	while (en.hasMoreElements()) {
		v.add(en.nextElement());
	}
	return (Curve[])BeanUtils.getArray(v, Curve.class);
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2001 3:13:10 PM)
 * @return cbit.vcell.geometry.Curve[]
 */
public Curve[] getAllUserCurves() {
	Enumeration<Curve> en = curveTable.keys();
	Vector<Curve> v = new Vector<Curve>();
	while (en.hasMoreElements()) {
		Curve curve = (Curve)en.nextElement();
		CurveRendererCurveInfo crci = (CurveRendererCurveInfo)curveTable.get(curve);
		if (crci.isEditable()) {
			v.add(curve);
		}
	}
	return (Curve[])BeanUtils.getArray(v, Curve.class);
}


/**
 * Gets the antialias property (boolean) value.
 * @return The antialias property value.
 * @see #setAntialias
 */
public boolean getAntialias() {
	return fieldAntialias;
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/00 1:59:06 PM)
 * @return cbit.vcell.geometry.CurveSelectionInfo
 * @param pickPoint cbit.vcell.geometry.Coordinate
 */
public CurveSelectionInfo[] getCloseCurveSelectionInfos(Coordinate pickPoint) {
	if (curveTable.size() == 0) {
		return null;
	}
	//
	Vector<CurveSelectionInfo> closestCSIV = new Vector<CurveSelectionInfo>();
	Vector<Double> closestDistanceV = new Vector<Double>();
	//
	Point2D.Double pickPoint2D = CurveRendererCurveInfo.projectAndScale3DPoint(pickPoint,getNormalAxis(),getScaling2D());
	//
	java.util.Enumeration<CurveRendererCurveInfo> curveEnum = curveTable.elements();
	//Get all curves within minPickDistance and sort from closest to farthest
	while (curveEnum.hasMoreElements()) {
		CurveRendererCurveInfo crci = (CurveRendererCurveInfo) curveEnum.nextElement();
		Curve curve = crci.getCurve();
		if(!curve.isValid()){
			continue;
		}
		double distance = distanceToProjectedCurve(pickPoint2D, crci);
		double minPickDistance = getMinPickDistance(curve);//
		if (distance <= minPickDistance) {
			if(closestCSIV.size() == 0){
				closestCSIV.add(new CurveSelectionInfo(curve));
				closestDistanceV.add(new Double(distance));
			}else{
				for(int i = 0;i < closestCSIV.size();i+= 1){
					if(distance < (((Double)(closestDistanceV.get(i))).doubleValue())){
						closestCSIV.add(i,new CurveSelectionInfo(curve));
						closestDistanceV.add(i,new Double(distance));
						break;
					}else if(i == (closestCSIV.size()-1)){
						closestCSIV.add(new CurveSelectionInfo(curve));
						closestDistanceV.add(new Double(distance));
						break;
					}
				}
			}
		}
	}
	//
	CurveSelectionInfo[] csiArr = null;
	if (closestCSIV.size() > 0) {
		csiArr = new CurveSelectionInfo[closestCSIV.size()];
		closestCSIV.copyInto(csiArr);
	}
	return csiArr;
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/00 1:59:06 PM)
 * @return cbit.vcell.geometry.CurveSelectionInfo
 * @param pickPoint cbit.vcell.geometry.Coordinate
 */
public CurveSelectionInfo getClosestCurveSelectionInfo(Coordinate pickPoint) {
	
	CurveSelectionInfo[] csiArr = getCloseCurveSelectionInfos(pickPoint);
	if(csiArr != null){
		//If there are many within minDistance then choose according to heirarchy rules
		for(int i =0;i < 3;i+= 1){
			for(int j =0;j < csiArr.length;j+= 1){
				switch(i){
					case 0:
						if(getRenderPropertySelectable(csiArr[j].getCurve()) && (csiArr[j].getCurve() instanceof SinglePoint)){
							return csiArr[j];
						}
						break;
					case 1:
						if(getRenderPropertySelectable(csiArr[j].getCurve())){
							return csiArr[j];
						}
						break;
					case 2:
						return csiArr[j];
					
				}
			}
		}
	}
	return null;
	
	//if (curveTable.size() == 0) {
		//return null;
	//}
	////
	//Curve closestCurve = null;
	//Point2D.Double pickPoint2D = CurveRendererCurveInfo.projectAndScale3DPoint(pickPoint,getNormalAxis(),getScaling2D());
	//double shortestDistance = Double.MAX_VALUE;
	////
	//java.util.Enumeration curveEnum = curveTable.elements();
	//while (curveEnum.hasMoreElements()) {
		//CurveRendererCurveInfo crci = (CurveRendererCurveInfo) curveEnum.nextElement();
		////if (!crci.isSelectable()) {
			////continue;
		////}
		//Curve curve = crci.getCurve();
		//double distance = distanceToProjectedCurve(pickPoint2D, crci);
		//if (distance < shortestDistance) {
			//double minPickDistance = getLineWidthMultiplier(curve)/2;
			//if(distance <= minPickDistance){
				//closestCurve = curve;
				//shortestDistance = distance;
			//}
		//}
	//}
	////
	//CurveSelectionInfo csi = null;
	//if (closestCurve != null) {
		//csi = new CurveSelectionInfo(closestCurve);
	//}
	//return csi;
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/00 1:59:06 PM)
 * @return cbit.vcell.geometry.CurveSelectionInfo
 * @param pickPoint cbit.vcell.geometry.Coordinate
 */
public CurveSelectionInfo getClosestSegmentSelectionInfo(Coordinate pickPoint,Curve pickCurve) {
	//
	CurveSelectionInfo csi = null;
	//Point2D.Double pickPoint2D = Coordinate.get2DProjection(pickPoint, getNormalAxis());
	//CurveRendererCurveInfo crci = (CurveRendererCurveInfo)curveTable.get(pickCurve);
	double pickedSegment = pickSegmentProjected(pickPoint,pickCurve);
	if (pickedSegment != Curve.NONE_SELECTED) {
		csi = new CurveSelectionInfo(pickCurve, CurveSelectionInfo.TYPE_SEGMENT, pickedSegment);
	}
	return csi;
}


/**
 * Gets the curveChecker property (cbit.vcell.geometry.CurveChecker) value.
 * @return The curveChecker property value.
 * @see #setCurveChecker
 */
public cbit.vcell.geometry.CurveChecker getCurveChecker() {
	return fieldCurveChecker;
}


/**
 * Gets the defaultLineWidthMultiplier property (double) value.
 * @return The defaultLineWidthMultiplier property value.
 * @see #setDefaultLineWidthMultiplier
 */
public double getDefaultLineWidthMultiplier() {
	return fieldDefaultLineWidthMultiplier;
}


/**
 * Insert the method's description here.
 * Creation date: (10/27/00 4:39:31 PM)
 * @return double
 */
private double getLineWidthMultiplier(Curve curve) {
	CurveRendererCurveInfo crci = (CurveRendererCurveInfo) curveTable.get(curve);
	double lwm = crci.getLineWidthMultiplier();
	if (lwm != CurveRendererCurveInfo.LWM_NONE) {
		lwm = crci.getLineWidthMultiplier();
	}else{
		lwm = getDefaultLineWidthMultiplier();
	}
	if(lwm > 10){lwm = 10;}
	return lwm;
}

private double getMinPickDistance(Curve curve){
//	return getLineWidthMultiplier(curve)/2;
	return
		Math.max(3.0,
		(cartesianMesh == null || getWorldDelta() == null
			? getLineWidthMultiplier(curve)/2
			: cartesianMesh.getExtent().getX()/cartesianMesh.getSizeX()/getWorldDelta().getX()/2 * (cartesianMesh.isChomboMesh() ? Math.sqrt(2) : 1)
		)
	);

}

/**
 * Sets the normalAxis property (int) value.
 * @param normalAxis The new value for the property.
 */
public int getNormalAxis() {
	return fieldNormalAxis;
}


/**
 * Gets the scaling2D property (java.awt.geom.Point2D.Double) value.
 * @return The scaling2D property value.
 * @see #setScaling2D
 */
public java.awt.geom.Point2D.Double getOrigin2D() {
	return fieldOrigin2D;
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Insert the method's description here.
 * Creation date: (10/19/00 3:29:59 PM)
 * @return boolean
 */
public boolean getRenderPropertyEditable(Curve curve) {
	boolean result = false;
	if (curve != null) {
		CurveRendererCurveInfo crci = (CurveRendererCurveInfo) curveTable.get(curve);
		if (crci != null) {
			result = crci.isEditable();
		}
	}
	return result;
}


/**
 * Insert the method's description here.
 * Creation date: (10/19/00 3:29:59 PM)
 * @return boolean
 */
public boolean getRenderPropertySelectable(Curve curve) {

	if (curve != null && curveTable.size() != 0) {
		CurveRendererCurveInfo crci = (CurveRendererCurveInfo) curveTable.get(curve);
		if (crci != null) {
			return crci.isSelectable();
		}
	}
	throw new RuntimeException("CurveRenderer does not containe curve");
}


/**
 * Insert the method's description here.
 * Creation date: (10/19/00 3:29:59 PM)
 * @return boolean
 */
public int getRenderPropertySubSelectionType(Curve curve) {

	if (curve != null && curveTable.size() != 0) {
		CurveRendererCurveInfo crci = (CurveRendererCurveInfo) curveTable.get(curve);
		if (crci != null) {
			return crci.getSubSelectionType();
		}
	}
	throw new RuntimeException("CurveRenderer does not containe curve");
}


/**
 * Insert the method's description here.
 * Creation date: (10/19/00 3:29:59 PM)
 * @return boolean
 */
public boolean getRenderPropertyVisible(Curve curve) {

	if (curve != null && curveTable.size() != 0) {
		CurveRendererCurveInfo crci = (CurveRendererCurveInfo) curveTable.get(curve);
		if (crci != null) {
			return crci.isVisible();
		}
	}
	throw new RuntimeException("CurveRenderer does not containe curve");
}


/**
 * Gets the scaling2D property (java.awt.geom.Point2D.Double) value.
 * @return The scaling2D property value.
 * @see #setScaling2D
 */
public java.awt.geom.Point2D.Double getScaling2D() {
	return fieldScaling2D;
}


/**
 * Gets the selection property (cbit.vcell.geometry.CurveSelectionInfo) value.
 * @return The selection property value.
 * @see #setSelection
 */
public cbit.vcell.geometry.CurveSelectionInfo getSelection() {
	return fieldSelection;
}


/**
 * Gets the selectionValid property (boolean) value.
 * @return The selectionValid property value.
 * @see #setSelectionValid
 */
public boolean getSelectionValid() {
	return fieldSelectionValid;
}


/**
 * Gets the worldDelta property (cbit.vcell.geometry.Coordinate) value.
 * @return The worldDelta property value.
 * @see #setWorldDelta
 */
public org.vcell.util.Coordinate getWorldDelta() {
	return fieldWorldDelta;
}


/**
 * Gets the worldDelta property (cbit.vcell.geometry.Coordinate) value.
 * @return The worldDelta property value.
 * @see #setWorldDelta
 */
public org.vcell.util.Coordinate getWorldOrigin() {
	return fieldWorldOrigin;
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * Insert the method's description here.
 * Creation date: (10/15/00 5:43:14 PM)
 * @return boolean
 */
public boolean isControlPointAddable() {
	return getSelection() != null && getSelection().getCurve() instanceof ControlPointCurve && ((ControlPointCurve)getSelection().getCurve()).isControlPointAddable();
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/00 4:03:17 PM)
 * @return cbit.vcell.geometry.CurveSelectionInfo
 * @param pickPoint cbit.vcell.geometry.Coordinate
 */
public CurveSelectionInfo pick(Coordinate pickPoint) {
	CurveSelectionInfo csi = null;
	if (getSelection() != null) {
		//Try to make a special selection on the currently selected curve
		switch (getRenderPropertySubSelectionType(getSelection().getCurve())) {
			case SUBSELECTION_CONTROL_POINT :
				csi = pickControlPoint(pickPoint, (ControlPointCurve)getSelection().getCurve());
				break;
			case SUBSELECTION_SEGMENT :
				csi = getClosestSegmentSelectionInfo(pickPoint, getSelection().getCurve());
				break;
			case SUBSELECTION_U :
				csi = pickU(pickPoint, getSelection().getCurve());
				break;
		}
	}
	//
	if (csi == null) {
		//Try to pick a curve if there were no special selections
		csi = getClosestCurveSelectionInfo(pickPoint);
	}
	//
	if (csi != null && csi != getSelection()) {
		//We picked a different curve from the currentlySelectedCurve so try to automatically
		//select the SEGMENT or U if appropriate
		CurveSelectionInfo newCSI = null;
		switch (getRenderPropertySubSelectionType(csi.getCurve())) {
			case SUBSELECTION_SEGMENT :
				newCSI = getClosestSegmentSelectionInfo(pickPoint, csi.getCurve());
				break;
			case SUBSELECTION_U :
				newCSI = pickU(pickPoint, csi.getCurve());
				break;
		}
		if (newCSI != null) {
			//Set our final selection to the new special CSI
			csi = newCSI;
		}
	}
	//
	return csi;
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/00 1:59:06 PM)
 * @return cbit.vcell.geometry.CurveSelectionInfo
 * @param pickPoint cbit.vcell.geometry.Coordinate
 */
private CurveSelectionInfo pickControlPoint(Coordinate pickPoint, ControlPointCurve pickCurve) {
	CurveSelectionInfo csi = null;
	if (pickCurve instanceof ControlPointCurve) {
		double pickedCP = pickControlPointProjected(pickPoint, pickCurve);
		if (pickedCP != Curve.NONE_SELECTED) {
			csi = new CurveSelectionInfo(pickCurve, CurveSelectionInfo.TYPE_CONTROL_POINT, pickedCP);
		}
	}
	return csi;
}


/**
 * Insert the method's description here.
 * Creation date: (7/18/00 4:08:13 PM)
 * @return int
 * @param point java.awt.Point
 */
private int pickControlPointProjected(Coordinate pickPoint3D, ControlPointCurve pickCurve) {
	Point2D.Double pickPoint2D = CurveRendererCurveInfo.projectAndScale3DPoint(pickPoint3D, getNormalAxis(), getScaling2D());
	double minPickDistance = getMinPickDistance(pickCurve);
	double shortestDistance = Double.MAX_VALUE;
	int controlPointIndex = Curve.NONE_SELECTED;
	int cpCount = pickCurve.getControlPointCount();
	for (int i = 0; i < cpCount; i++) {
		Point2D.Double p2d = CurveRendererCurveInfo.projectAndScale3DPoint(pickCurve.getControlPoint(i),getNormalAxis(),getScaling2D());
		double distance = pickPoint2D.distance(p2d);
		if (distance <= minPickDistance && distance < shortestDistance) {
			controlPointIndex = i;
			shortestDistance = distance;
		}
	}
	return controlPointIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (10/18/00 2:51:32 PM)
 */
private int pickSegmentProjected(Coordinate pickPoint3D, Curve pickCurve) {
	//
	//Changes made 7/15/2004 by frm because
	//java.awt.geom.Line2D.ptSegDist(...) would return NAN because a bug in
	//java.awt.geom.Line2D.ptSegDistSq(...) would sometimes return very small NEGATIVE number
	//
	Point2D.Double pickPoint2D = CurveRendererCurveInfo.projectAndScale3DPoint(pickPoint3D, getNormalAxis(), getScaling2D());
	CurveRendererCurveInfo crci = (CurveRendererCurveInfo) curveTable.get(pickCurve);
	//double minPickDistance = getLineWidthMultiplier(pickCurve) / 2;
	double minPickDistanceSqr = Math.pow(getMinPickDistance(pickCurve),2);
	Point2D.Double[] p2d = crci.fetchProjectedCurvePoints(getScaling2D(), getNormalAxis());
	//
	int segmentCount = pickCurve.getSegmentCount();
	int samplePointCount = p2d.length;
	double shortestDistance = Double.MAX_VALUE;
	int closestSegment = Curve.NONE_SELECTED;
	Point2D.Double p0 = null;
	Point2D.Double p1 = null;
	for (int i = 0; i < segmentCount; i += 1) {
		p0 = p2d[i];
		p1 = p2d[(i + 1) % samplePointCount];
		//double distance = java.awt.geom.Line2D.ptSegDist(p0.getX(), p0.getY(), p1.getX(), p1.getY(), pickPoint2D.getX(), pickPoint2D.getY());
		double distanceSqr = java.awt.geom.Line2D.ptSegDistSq(p0.getX(), p0.getY(), p1.getX(), p1.getY(), pickPoint2D.getX(), pickPoint2D.getY());
		distanceSqr = Math.abs(distanceSqr);
		//if (distance <= minPickDistance && distance < shortestDistance) {
		if (distanceSqr <= minPickDistanceSqr && distanceSqr < shortestDistance) {
			shortestDistance = distanceSqr;
			closestSegment = i;
		}
	}
	if (closestSegment != Curve.NONE_SELECTED) {
		return closestSegment;
	}
	return Curve.NONE_SELECTED;
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/00 1:59:06 PM)
 * @return cbit.vcell.geometry.CurveSelectionInfo
 * @param pickPoint cbit.vcell.geometry.Coordinate
 */
private CurveSelectionInfo pickU(Coordinate pickPoint,Curve pickCurve) {
	//
	CurveSelectionInfo csi = null;
	//Point2D.Double pickPoint2D = Coordinate.get2DProjection(pickPoint, getNormalAxis());
	//CurveRendererCurveInfo crci = (CurveRendererCurveInfo)curveTable.get(pickCurve);
	double pickedU = pickUProjected(pickPoint,pickCurve);
	if (pickedU != Curve.NONE_SELECTED) {
		csi = new CurveSelectionInfo(pickCurve, CurveSelectionInfo.TYPE_U, pickedU);
	}
	return csi;
}


/**
 * Insert the method's description here.
 * Creation date: (10/10/00 12:50:48 PM)
 * @return int
 * @param pickCoord cbit.vcell.geometry.Coordinate
 */
public double pickUProjected(Coordinate pickPoint3D, Curve pickCurve){
	//Must implement
	/*
	Point2D.Double pickPoint2D = CurveRendererCurveInfo.projectAndScale3DPoint(pickPoint3D, getNormalAxis(), getScaling2D());
	CurveRendererCurveInfo crci = (CurveRendererCurveInfo) curveTable.get(pickCurve);
	double minPickDistance = getLineWidthMultiplier(pickCurve) / 2;
	Point2D.Double[] p2d = crci.fetchProjectedCurvePoints(getScaling2D(), getNormalAxis());
	//
	//Point2D.Double[] p2d = getProjectedSamplePoints(axis);
	int segmentCount = crci.getCurve().getSampledCurve().getSegmentCount();
	int samplePointCount = p2d.length;
	double shortestDistance = Double.MAX_VALUE;
	int closestSegment = Curve.NONE_SELECTED;
	double closestSegmentU = Curve.NONE_SELECTED;
	Point2D.Double p0 = null;
	Point2D.Double p1 = null;
	for (int i = 0; i < segmentCount; i += 1) {
		p0 = p2d[i];
		p1 = p2d[(i + 1) % samplePointCount];
		double u = calculateUOfV1AlongV2(p0.getX(),p0.getY(),0, pickPoint.getX(),pickPoint.getY(),0, p1.getX(),p1.getY(),0);
		if (u >= 0.0 && u <= 1.0) {
			double distance = java.awt.geom.Line2D.ptSegDist(p0.getX(),p0.getY(),p1.getX(),p1.getY(),pickPoint.getX(),pickPoint.getY());
			if (distance <= minPickDistance && distance < shortestDistance) {
				shortestDistance = distance;
				closestSegment = i;
				closestSegmentU = u;
			}
		}
	}
	if (closestSegment != Curve.NONE_SELECTED) {
	    //Calculate Non-LengthNormalized U
		double finalU = (closestSegment + closestSegmentU) / getSegmentCount();
		return finalU;
	} else {
		return Curve.NONE_SELECTED;
	}
	*/
	return 0;
}


/**
 * This method was created in VisualAge.
 * @param curve cbit.vcell.geometry.Curve
 */
public void removeAllCurves() {
	curveTable.clear();
	selectNothing();
}


/**
 * This method was created in VisualAge.
 * @param curve cbit.vcell.geometry.Curve
 */
public void removeCurve(Curve curve) {
	curveTable.remove(curve);
	if (getSelection() != null && getSelection().getCurve() == curve) {
		selectNothing();
	}
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (8/15/00 11:29:42 AM)
 */
public void removeSelected(int operation) {
	if (getSelection() != null) {
		Curve selected = getSelection().getCurve();
		if (getSelection().getType() == CurveSelectionInfo.TYPE_CONTROL_POINT) {
			boolean bDeleteKeyPressed = (operation == java.awt.event.KeyEvent.VK_DELETE);
			int newSelectedControlPoint = ((ControlPointCurve) selected).removeControlPoint(getSelection().getControlPoint(), bDeleteKeyPressed);
			if (newSelectedControlPoint != CurveSelectionInfo.NONE_SELECTED) {
				setSelection(new CurveSelectionInfo((ControlPointCurve) selected, CurveSelectionInfo.TYPE_CONTROL_POINT, newSelectedControlPoint));
				return;
			}
		}
		//selectNothing();
		removeCurve(selected);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/19/00 3:04:55 PM)
 * @param curve cbit.vcell.geometry.Curve
 * @param lwm double
 */
public void renderPropertyEditable(Curve curve, boolean editable) {
	if (curveTable.containsKey(curve)) {
		CurveRendererCurveInfo crci = (CurveRendererCurveInfo) curveTable.get(curve);
		crci.setEditable(editable);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/19/00 3:04:55 PM)
 * @param curve cbit.vcell.geometry.Curve
 * @param lwm double
 */
public void renderPropertyLineWidthMultiplier(Curve curve, double lwm) {
	if (curveTable.containsKey(curve)) {
		CurveRendererCurveInfo crci = (CurveRendererCurveInfo) curveTable.get(curve);
		crci.setLineWidthMultiplier(lwm);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/19/00 3:04:55 PM)
 * @param curve cbit.vcell.geometry.Curve
 * @param lwm double
 */
public void renderPropertySegmentColors(Curve curve, int[] segmentColors) {
	if (curveTable.containsKey(curve)) {
		CurveRendererCurveInfo crci = (CurveRendererCurveInfo) curveTable.get(curve);
		crci.setSegmentColors(segmentColors);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/19/00 3:04:55 PM)
 * @param curve cbit.vcell.geometry.Curve
 * @param lwm double
 */
public void renderPropertySegmentIndexes(Curve curve, int[] segmentIndexes) {
	if (curveTable.containsKey(curve)) {
		CurveRendererCurveInfo crci = (CurveRendererCurveInfo) curveTable.get(curve);
		crci.setSegmentIndexes(segmentIndexes);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/19/00 3:04:55 PM)
 * @param curve cbit.vcell.geometry.Curve
 * @param lwm double
 */
public void renderPropertySelectable(Curve curve, boolean selectable) {
	if (curveTable.containsKey(curve)) {
		CurveRendererCurveInfo crci = (CurveRendererCurveInfo) curveTable.get(curve);
		crci.setSelectable(selectable);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/19/00 3:04:55 PM)
 * @param curve cbit.vcell.geometry.Curve
 * @param lwm double
 */
public void renderPropertySubSelectionType(Curve curve, int subSelectionType) {
	if (subSelectionType == SUBSELECTION_NONE || 
		subSelectionType == SUBSELECTION_SEGMENT || 
		subSelectionType == SUBSELECTION_U ||
		subSelectionType == SUBSELECTION_CONTROL_POINT) {
		if(subSelectionType == SUBSELECTION_CONTROL_POINT && !(curve instanceof ControlPointCurve)){
			return;
		}
		if (curveTable.containsKey(curve)) {
			CurveRendererCurveInfo crci = (CurveRendererCurveInfo) curveTable.get(curve);
			crci.setSubSelectionType(subSelectionType);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/19/00 3:04:55 PM)
 * @param curve cbit.vcell.geometry.Curve
 * @param lwm double
 */
public void renderPropertyVisible(Curve curve, boolean visible) {
	if (curveTable.containsKey(curve)) {
		CurveRendererCurveInfo crci = (CurveRendererCurveInfo) curveTable.get(curve);
		crci.setVisible(visible);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/12/00 5:43:16 PM)
 */
public void selectNext() {
	if (getSelection() != null) {
		if (getSelection().getType() == CurveSelectionInfo.TYPE_CONTROL_POINT) {
			int scp = getSelection().getControlPoint() + 1;
			if (scp >= ((ControlPointCurve) getSelection().getCurve()).getControlPointCount()) {
				scp = 0;
			}
			setSelection(new CurveSelectionInfo((ControlPointCurve) getSelection().getCurve(), CurveSelectionInfo.TYPE_CONTROL_POINT, scp));
			return;
		}
		//else if (getSelection().getType() == CurveSelectionInfo.TYPE_SEGMENT) {
			//int scp = getSelection().getSegment() + 1;
			//if (scp >= getSelection().getCurve().getSegmentCount()) {
				//scp = 0;
			//}
			//setSelection(new CurveSelectionInfo(getSelection().getCurve(), CurveSelectionInfo.TYPE_SEGMENT, scp));
			//return;
		//}
	}
	if (curveTable.size() != 0) {
		Object[] crciArr = curveTable.values().toArray();
		Curve currSelection = (getSelection() != null?getSelection().getCurve():null);
		int bScan = (currSelection == null?0:-1);
		int count = 0;
		while(true){
			CurveRendererCurveInfo nextcrci = (CurveRendererCurveInfo)(crciArr[count]);
			if(bScan != -1 && nextcrci.isSelectable()){
				setSelection(new CurveSelectionInfo(nextcrci.getCurve()));
				return;
			}
			if(nextcrci.getCurve() == currSelection){
				bScan = count;
			}
			count+= 1;
			if(count >= crciArr.length){
				count = 0;
			}
			if(bScan == count){return;}
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/15/00 11:37:57 AM)
 */
public void selectNothing() {
	setSelection(null);
}


/**
 * Sets the antialias property (boolean) value.
 * @param antialias The new value for the property.
 * @see #getAntialias
 */
public void setAntialias(boolean antialias) {
	boolean oldValue = fieldAntialias;
	fieldAntialias = antialias;
	firePropertyChange("antialias", new Boolean(oldValue), new Boolean(antialias));
}


/**
 * Sets the curveChecker property (cbit.vcell.geometry.CurveChecker) value.
 * @param curveChecker The new value for the property.
 * @see #getCurveChecker
 */
public void setCurveChecker(cbit.vcell.geometry.CurveChecker curveChecker) {
	CurveChecker oldValue = fieldCurveChecker;
	fieldCurveChecker = curveChecker;
	firePropertyChange("curveChecker", oldValue, curveChecker);
}


/**
 * Sets the defaultLineWidthMultiplier property (double) value.
 * @param defaultLineWidthMultiplier The new value for the property.
 * @see #getDefaultLineWidthMultiplier
 */
public void setDefaultLineWidthMultiplier(double defaultLineWidthMultiplier) {
	double oldValue = fieldDefaultLineWidthMultiplier;
	fieldDefaultLineWidthMultiplier = defaultLineWidthMultiplier;
	firePropertyChange("defaultLineWidthMultiplier", new Double(oldValue), new Double(defaultLineWidthMultiplier));
}


/**
 * Sets the normalAxis property (int) value.
 * @param normalAxis The new value for the property.
 */
public void setNormalAxis(int normalAxis) {
	fieldNormalAxis = normalAxis;
	updateOriginAndScaling2D();
}


/**
 * Sets the scaling2D property (java.awt.geom.Point2D.Double) value.
 * @param scaling2D The new value for the property.
 * @see #getScaling2D
 */
private void setOrigin2D(Point2D.Double origin2D) {
	if (origin2D == null && fieldOrigin2D == null) {
		return;
	}
	if (origin2D != null && fieldOrigin2D != null) {
		if (origin2D.getX() == fieldOrigin2D.getX() && origin2D.getY() == fieldOrigin2D.getY()) {
			return;
		}
	}
	Point2D.Double oldValue = fieldOrigin2D;
	fieldOrigin2D = origin2D;
	firePropertyChange("origin2D", oldValue, origin2D);
}


/**
 * Sets the scaling2D property (java.awt.geom.Point2D.Double) value.
 * @param scaling2D The new value for the property.
 * @see #getScaling2D
 */
private void setScaling2D(Point2D.Double scaling2D) {
	if (scaling2D == null && fieldScaling2D == null) {
		return;
	}
	if (scaling2D != null && fieldScaling2D != null) {
		if (scaling2D.getX() == fieldScaling2D.getX() && scaling2D.getY() == fieldScaling2D.getY()) {
			return;
		}
	}
	Point2D.Double oldValue = fieldScaling2D;
	fieldScaling2D = scaling2D;
	//
	//java.util.Enumeration curveEnum = curveTable.elements();
	//while (curveEnum.hasMoreElements()) {
	//CurveRendererCurveInfo crci = (CurveRendererCurveInfo) curveEnum.nextElement();
	//calculateProjectedPoints(crci);
	//}
	//
	firePropertyChange("scaling2D", oldValue, scaling2D);
}


/**
 * Sets the selection property (cbit.vcell.geometry.CurveSelectionInfo) value.
 * @param selection The new value for the property.
 * @see #getSelection
 */
public void setSelection(cbit.vcell.geometry.CurveSelectionInfo selection) {
	CurveSelectionInfo oldValue = fieldSelection;
	//Remove current selection curve if curve is invalid
	if (fieldSelection != null && (selection == null || fieldSelection.getCurve() != selection.getCurve())) {
		if (!fieldSelection.getCurve().isValid()) {
			curveTable.remove(fieldSelection.getCurve());
			//removeCurve(fieldSelection.getCurve());
		}
	}
	fieldSelection = selection;
	setSelectionValid(fieldSelection != null);
	firePropertyChange("selection", oldValue, selection);
}


/**
 * Sets the selectionValid property (boolean) value.
 * @param selectionValid The new value for the property.
 * @see #getSelectionValid
 */
private void setSelectionValid(boolean selectionValid) {
	boolean oldValue = fieldSelectionValid;
	fieldSelectionValid = selectionValid;
	firePropertyChange("selectionValid", new Boolean(oldValue), new Boolean(selectionValid));
}


/**
 * Sets the worldDelta property (cbit.vcell.geometry.Coordinate) value.
 * @param worldDelta The new value for the property.
 * @see #getWorldDelta
 */
public void setWorldDelta(org.vcell.util.Coordinate wd) {
	if(wd == null && fieldWorldDelta == null){
		return;
	}
	Coordinate oldValue = fieldWorldDelta;
	fieldWorldDelta = wd;
	//Calc smallest 1 Pixel distance of the 3 axes
	//unitDistance[Coordinate.X_AXIS] = Math.sqrt((wd.getZ() * wd.getZ()) + (wd.getY() * wd.getY()));
	//unitDistance[Coordinate.Y_AXIS] = Math.sqrt((wd.getX() * wd.getX()) + (wd.getZ() * wd.getZ()));
	//unitDistance[Coordinate.Z_AXIS] = Math.sqrt((wd.getX() * wd.getX()) + (wd.getY() * wd.getY()));
	//
	updateOriginAndScaling2D();
	firePropertyChange("worldDelta", oldValue, wd);
}


/**
 * Sets the worldDelta property (cbit.vcell.geometry.Coordinate) value.
 * @param worldDelta The new value for the property.
 * @see #getWorldDelta
 */
public void setWorldOrigin(org.vcell.util.Coordinate wo) {
	if(wo == null && fieldWorldOrigin == null){
		return;
	}
	Coordinate oldValue = fieldWorldOrigin;
	fieldWorldOrigin = wo;
	updateOriginAndScaling2D();
	firePropertyChange("worldOrigin", oldValue, wo);
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/00 6:45:56 PM)
 */
private void updateOriginAndScaling2D() {
	//
	if (getWorldDelta() == null) {
		setScaling2D(null);
	} else {
		Coordinate wd = getWorldDelta();
		double wd_x = Coordinate.convertAxisFromStandardXYZToNormal(wd.getX(), wd.getY(), wd.getZ(), Coordinate.X_AXIS, getNormalAxis());
		double wd_y = Coordinate.convertAxisFromStandardXYZToNormal(wd.getX(), wd.getY(), wd.getZ(), Coordinate.Y_AXIS, getNormalAxis());
		setScaling2D(new Point2D.Double(1 / wd_x, 1 / wd_y));
	}
	//
	if (getWorldOrigin() == null || getWorldDelta() == null) {
		setOrigin2D(null);
	} else {
		Point2D.Double origin2DScaled = CurveRendererCurveInfo.projectAndScale3DPoint(getWorldOrigin(),getNormalAxis(),getScaling2D());
		setOrigin2D(origin2DScaled);
	}

}
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import cbit.gui.graph.visualstate.EdgeVisualState;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.DefaultEdgeVisualState;

public abstract class EdgeShape extends Shape implements EdgeVisualState.Owner {

	public static final int LINE_STYLE_SOLID = 0;
	public static final int LINE_STYLE_DASHED = 1;

	protected Shape startShape = null;
	public Shape endShape = null;
	public Point start = new Point();
	public Point end = new Point();

	protected static final double CONTROL_WEIGHT = 25.0;
	protected static final double FRACT_WEIGHT = 0.25;
	protected Point lastCurve_Start = null;
	protected Point lastCurve_End = null;
	protected CubicCurve2D.Double lastCurve = null;
	protected static final BasicStroke DASHED_STROKE = new BasicStroke(1,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[] { 5,	3 } , 10f);

	public EdgeShape(Shape startShape, Shape endShape,
			GraphModel graphModel) {
		super(graphModel);
		this.startShape = startShape;
		this.endShape = endShape;
		defaultFG = java.awt.Color.black;
		defaultFGselect = java.awt.Color.red;
	}

	public EdgeShape(Point start, Point end, GraphModel graphModel) {
		super(graphModel);
		this.startShape = null;
		this.endShape = null;
		this.start = start;
		this.end = end;
		defaultFG = Color.black;
		defaultFGselect = Color.red;
	}

	@Override
	public VisualState createVisualState() {
		return new DefaultEdgeVisualState(this);
	}

	public Shape getStartShape() {
		return startShape;
	}

	public Shape getEndShape() {
		return endShape;
	}

	protected static Point2D evaluate(CubicCurve2D curve, double t) {
		double u = 1 - t;
		double x = curve.getX1() * u * u * u + 3 * curve.getCtrlX1() * t * u
				* u + 3 * curve.getCtrlX2() * t * t * u + curve.getX2() * t * t
				* t;
		double y = curve.getY1() * u * u * u + 3 * curve.getCtrlY1() * t * u
				* u + 3 * curve.getCtrlY2() * t * t * u + curve.getY2() * t * t
				* t;
		return new Point2D.Double(x, y);
	}

	protected static GeneralPath getArrow(Point2D front, Point2D back,
			double width) {
		double deltaX = back.getX() - front.getX();
		double deltaY = back.getY() - front.getY();
		double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		// take straight-line approximation for direction (given right.p1 and
		// right.p2 as end points)
		// use 10 and 30 "pixels" away for the arrowhead.
		double X1 = front.getX();
		double Y1 = front.getY();
		double X2 = front.getX() + 1.0 * deltaX;
		double Y2 = front.getY() + 1.0 * deltaY;
		double X3 = X2 + 0.5 * width * deltaY / distance;
		double Y3 = Y2 - 0.5 * width * deltaX / distance;
		double X4 = X2 - 0.5 * width * deltaY / distance;
		double Y4 = Y2 + 0.5 * width * deltaX / distance;
		GeneralPath arrow = new GeneralPath();
		arrow.moveTo((float) X1, (float) Y1);
		arrow.lineTo((float) X3, (float) Y3);
		arrow.lineTo((float) X4, (float) Y4);
		arrow.lineTo((float) X1, (float) Y1);
		arrow.setWindingRule(GeneralPath.WIND_NON_ZERO);
		return arrow;
	}

	protected CubicCurve2D.Double getCurve() {
		if (lastCurve != null && lastCurve_Start != null
				&& lastCurve_Start.equals(start) && lastCurve_End != null
				&& lastCurve_End.equals(end)) {
		} else {
			// default behavior of control points is for direction at ends to
			// follow secant between end-points.
			Point2D.Double p1ctrl = new Point2D.Double((1.0 - FRACT_WEIGHT)
					* start.getX() + FRACT_WEIGHT * end.getX(),
					(1.0 - FRACT_WEIGHT) * start.getY() + FRACT_WEIGHT
							* end.getY());
			Point2D.Double p2ctrl = new Point2D.Double(FRACT_WEIGHT
					* start.getX() + (1.0 - FRACT_WEIGHT) * end.getX(),
					FRACT_WEIGHT * start.getY() + (1.0 - FRACT_WEIGHT)
							* end.getY());
			lastCurve = new CubicCurve2D.Double(start.getX(), start.getY(),
					p1ctrl.getX(), p1ctrl.getY(), p2ctrl.getX(), p2ctrl.getY(),
					end.getX(), end.getY());
			lastCurve_Start = new Point(start);
			lastCurve_End = new Point(end);
		}

		return lastCurve;
	}

	protected int getEndAttachment() {
		return ATTACH_CENTER;
	}

	public int getLineStyle() {
		return LINE_STYLE_SOLID;
	}

	protected static double getParameterAtArcLength(CubicCurve2D curve,
			double startT, double endT, double arcLength, int numIntervals) {
		double currentT = startT;
		double totalLength = 0.0;
		double deltaT = (endT - startT) / numIntervals;
		Point2D curr = evaluate(curve, currentT);
		for (int i = 0; i < numIntervals && totalLength < arcLength; i++) {
			currentT = startT + i * deltaT;
			Point2D next = evaluate(curve, currentT);
			double dx = next.getX() - curr.getX();
			double dy = next.getY() - curr.getY();
			totalLength += Math.sqrt(dx * dx + dy * dy);
			curr = next;
		}
		return currentT;
	}

	@Override
	public Dimension getPreferedSizeSelf(Graphics2D g) {
		FontMetrics fm = g.getFontMetrics();
		setLabelSize(fm.stringWidth(getLabel()), fm.getMaxAscent() + fm.getMaxDescent());
		getSpaceManager().setSizePreferred((getLabelSize().height + 10), (getLabelSize().width + 10));
		return getSpaceManager().getSizePreferred();
	}

	protected int getStartAttachment() {
		return ATTACH_CENTER;
	}

	protected static double integrateArcLength(CubicCurve2D cubicCurve,
			double t1, double t2, int numIntervals) {
		double arcLength = 0.0;
		double currentT = t1;
		double deltaT = (t2 - t1) / numIntervals;
		Point2D curr = evaluate(cubicCurve, currentT);
		for (int i = 0; i < numIntervals; i++) {
			Point2D next = evaluate(cubicCurve, currentT + deltaT);
			double dx = next.getX() - curr.getX();
			double dy = next.getY() - curr.getY();
			arcLength += Math.sqrt(dx * dx + dy * dy);
			curr = next;
			currentT = t1 + i * deltaT;
		}
		return arcLength;
	}

	protected static double intersectWithCircle(CubicCurve2D curve,
			double startT, double endT, double centerX, double centerY,
			double radius) {
		double radiusSquared = radius * radius;
		double bestT = -1.0;
		double bestError = 10e10;
		int iMax = 200;
		if(startT == endT) { return endT; }
		double relDistT = Math.abs(startT - endT) / (Math.abs(startT) + Math.abs(endT));
		if(relDistT < 1e-8) { return (startT + endT) / 2.0; }
		for(int i = 0; i <= iMax; ++i) {
			double iFraction = ((double) i) /((double) iMax);
			double t = startT*(1.0 - iFraction) + endT*iFraction;
			double u = 1 - t;
			double x = curve.getX1() * u * u * u + 3 * curve.getCtrlX1() * t
					* u * u + 3 * curve.getCtrlX2() * t * t * u + curve.getX2()
					* t * t * t;
			double y = curve.getY1() * u * u * u + 3 * curve.getCtrlY1() * t
					* u * u + 3 * curve.getCtrlY2() * t * t * u + curve.getY2()
					* t * t * t;
			double tempRadiusSquared = (x - centerX) * (x - centerX)
					+ (y - centerY) * (y - centerY);
			double error = Math.abs(tempRadiusSquared - radiusSquared);
			if (error < bestError) {
				bestError = error;
				bestT = t;
			} else {
				// break;
			}
		}
		return bestT;
	}

	@Override
	public final boolean isInside(Point p) {
		CubicCurve2D curve = getCurve();
		Point absLocation = this.spaceManager.getAbsLoc();
		double xAbs = p.getX() + absLocation.x - getSpaceManager().getRelX();
		double yAbs = p.getY() + absLocation.y - getSpaceManager().getRelY();
		return curve.intersects(xAbs - 2, yAbs - 2, 4, 4);
	}

	@Override
	public void refreshLayoutSelf() {
		if (startShape != null) {
			start = startShape.getAttachmentLocation(getStartAttachment());
			start.x += startShape.getSpaceManager().getAbsLoc().x;
			start.y += startShape.getSpaceManager().getAbsLoc().y;
		}
		if (endShape != null) {
			end = endShape.getAttachmentLocation(getEndAttachment());
			end.x += endShape.getSpaceManager().getAbsLoc().x;
			end.y += endShape.getSpaceManager().getAbsLoc().y;
		}
		getSpaceManager().setRelPos(Math.min(start.x, end.x), Math.min(start.y, end.y));
		getSpaceManager().setSize(Math.abs(start.x - end.x), Math.abs(start.y - end.y));
		// this is like a row/column layout (1 column)
		int centerX = getSpaceManager().getSize().width / 2;
		int centerY = getSpaceManager().getSize().height / 2;
		// position label
		labelPos.x = centerX - getLabelSize().width / 2; 
		labelPos.y = centerY - getLabelSize().height / 2;
	}
	
	@Override
	public void paintSelf(Graphics2D g2D, int xPos, int yPos) {
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		paint0(g2D, xPos, yPos);
	}

	public void paint_NoAntiAlias(Graphics2D g2D, int xPos, int yPos) {
		// to accommdate a Machintosh Java 1.4.2 bug (can't properly XOR paint
		// with anti-aliasing)
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		paint0(g2D, xPos, yPos);
	}

	private void paint0(Graphics2D g2D, int xPos, int yPos) {
		// render curve (make CatalystShapes draw with a dashed line)
		g2D.setColor(forgroundColor);
		if (getLineStyle() == LINE_STYLE_DASHED) {
			Stroke oldStroke = g2D.getStroke();
			g2D.setStroke(DASHED_STROKE);
			g2D.draw(getCurve());
			g2D.setStroke(oldStroke);
		} else {
			g2D.draw(getCurve());
		}
		// draw label
		if (getLabel() != null && getLabel().length() > 0) {
			g2D.drawString(getLabel(), (start.x + end.x) / 2, (start.y + end.y) / 2);
		}
		return;
	}

	public void setEnd(Shape shape) throws Exception {
		endShape = shape;
		refreshLayoutSelf();
	}

	public void setEnd(Point end) throws Exception {
		this.end = end;
		endShape = null;
		refreshLayoutSelf();
	}

	public void setStart(Point end) throws Exception {
		this.start = end;
		startShape = null;
		refreshLayoutSelf();
	}
	
	public Point getStart() {
		return start;
	}
	
	public Point getEnd() {
		return end;
	}
	
	public boolean isDirectedForward() { return true; }
	
}

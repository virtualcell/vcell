/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.plot.gui;

import java.awt.geom.Point2D;
/**
 * Just a wrapper so we can collect arrays of different lengths.
 * Creation date: (2/9/2001 2:17:26 AM)
 * @author: Ion Moraru
 */
public class Point2DSet {
	private Point2D[] points = new Point2D[0];
	Point2DSet(Point2D[] points) {
		if (points != null) {
			this.points = points;
		}
	}
	public int getNumberOfPoints() {
		return points.length;
	}
	public Point2D[] getPoints() {
		return points;
	}
	public void setPoints(Point2D[] newPoints) {
		if (newPoints == null) {
			newPoints = new Point2D[0];
		}
		points = newPoints;
	}
}

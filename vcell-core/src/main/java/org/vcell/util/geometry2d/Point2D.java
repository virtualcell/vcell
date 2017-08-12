/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.geometry2d;

public class Point2D {

	public double x, y;
	
	public Point2D() {}
	public Point2D(double x, double y) { this.x = x; this.y = y; }
	public Point2D(Point2D p) { x = p.x; y = p.y; }
	public Point2D(Vector2D v) { x = v.x; y = v.y; }
	
	public void add(Vector2D v) { x += v.x; y += v.y; }
	public void subtract(Vector2D v) { x -= v.x; y -= v.y; }
	
	public Vector2D getVector() { return new Vector2D(x, y); }
	public Vector2D getVectorTo(Point2D p) { return new Vector2D(p.x - x, p.y - y); }
	
	public double getDistanceTo(Point2D p) {
		double dx = p.x -x;
		double dy = p.y -y;
		return Math.sqrt(dx*dx + dy*dy);
	}
	
}

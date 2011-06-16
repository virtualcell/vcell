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

public class Vector2D {
	
	public double x, y;
	
	public Vector2D() {}
	public Vector2D(double x, double y) { this.x = x; this.y = y; }
	public Vector2D(Vector2D v) { this(v.x, v.y); }
	
	public void add(Vector2D v) { x += v.x; y += v.y; }
	public void subtract(Vector2D v) { x -= v.x; y -= v.y; }
	public void multiply(double a) { x *= a; y *= a; }
	public void divide(double a) { x /= a; y /= a; }
	public void invert() { x = -x; y = -y; }
	
	public void normalize() {
		double length = Math.sqrt(x*x + y*y);
		x = x/length;
		y = y/length;
	}


	public Vector2D getInverse() { return new Vector2D(-x, -y); }
	public double getLength() { return Math.sqrt(x*x + y*y); }

	public static Vector2D getSum(Vector2D ... vList) { 
		Vector2D sum = new Vector2D();
		for(Vector2D v : vList) { sum.add(v); }
		return sum; 
	}
	
	public static Vector2D getDifference(Vector2D v1, Vector2D v2) { 
		return new Vector2D(v1.x - v2.x, v1.y - v2.y); 
	}
	
	public static Vector2D getProduct(Vector2D v, double a) { return new Vector2D(a*v.x, a*v.y); }
	public static Vector2D getProduct(double a, Vector2D v) { return new Vector2D(a*v.x, a*v.y); }
	public double getDotProduct(Vector2D v) { return x*v.x + y*v.y; }
	
	
	
}

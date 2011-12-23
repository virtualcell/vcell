/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.render;

import java.io.Serializable;

/**
 * Insert the type's description here.
 * Creation date: (11/29/2003 1:04:15 PM)
 * @author: Jim Schaff
 */
public class Vect3d implements Serializable {
	double q[] = new double[3];

/**
 * Vect3d constructor comment.
 */
public Vect3d() {
	super();
}
/**
 * Vect3d constructor comment.
 */
public Vect3d(double x, double y, double z) {
	q[0] = x;
	q[1] = y;
	q[2] = z;
}
/**
 * Vect3d constructor comment.
 */
public Vect3d(Vect3d v) {
	q[0] = v.q[0];
	q[1] = v.q[1];
	q[2] = v.q[2];
}

public Vect3d cross(Vect3d v){
	return new Vect3d(q[1]*v.q[2] - q[2]*v.q[1], q[2]*v.q[0] - q[0]*v.q[2], q[0]*v.q[1] - q[1]*v.q[0]);
}
public double dot(Vect3d v){
	return q[0]*v.q[0] + q[1]*v.q[1] + q[2]*v.q[2];
}

public double getX() {
	return q[0];
}
public double getY() {
	return q[1];
}
public double getZ() {
	return q[2];
}
public double length() {
	return Math.sqrt(q[0]*q[0] + q[1]*q[1] + q[2]*q[2]);
}
public double lengthSquared() {
	return q[0]*q[0] + q[1]*q[1] + q[2]*q[2];
}
public void scale(double s) {
	q[0] *= s;
	q[1] *= s;
	q[2] *= s;
}

/**
 * Insert the method's description here.
 * Creation date: (11/29/2003 1:20:37 PM)
 * @param x double
 * @param y double
 * @param z double
 */
public void set(double x, double y, double z) {
	q[0] = x;
	q[1] = y;
	q[2] = z;
}
/**
 * Vect3d constructor comment.
 */
public void set(Vect3d v) {
	q[0] = v.q[0];
	q[1] = v.q[1];
	q[2] = v.q[2];
}
public void add(Vect3d v) {
	q[0] += v.q[0];
	q[1] += v.q[1];
	q[2] += v.q[2];
}
public static Vect3d add(Vect3d v1, Vect3d v2) {
    return new Vect3d(v1.q[0] + v2.q[0], v1.q[1] + v2.q[1], v1.q[2] + v2.q[2]);
}
public void sub(Vect3d v) {
	q[0] -= v.q[0];
	q[1] -= v.q[1];
	q[2] -= v.q[2];
}
public static Vect3d sub(Vect3d v1, Vect3d v2) {
    return new Vect3d(v1.q[0] - v2.q[0], v1.q[1] - v2.q[1], v1.q[2] - v2.q[2]);
}
public boolean equals(Vect3d that)
{
	return (q[0] == that.q[0] && q[1] == that.q[1] && q[2] == that.q[2]);
}  
public String toString(){
	return getClass().getName() + "@" + Integer.toHexString(hashCode())+" ("+q[0]+", "+q[1]+", "+q[2]+")";
}
/**
 * Insert the method's description here.
 * Creation date: (11/29/2003 3:34:05 PM)
 * @return cbit.vcell.render.Vect3d
 */
public Vect3d uminus() {
	return new Vect3d(-q[0],-q[1],-q[2]);
}
/**           
* Normalizes the vector.
*/          
public void unit() {
	double s = length();
	scale(1.0/s);
}
/**
 * Insert the method's description here.
 * Creation date: (11/29/2003 1:19:27 PM)
 */
public void zero() {
	q[0] = 0.0;
	q[1] = 0.0;
	q[2] = 0.0;
}

//public static double DistanceToPlane(Vect3d point, Vect3d t0, Vect3d t1, Vect3d t2)
//{
//	Vect3d n = cross(sub(t1,t0), sub(t2,t0));
//	n.unit();
//	return Math.abs(dot(n, point) + dot(n, t0));
//}
public static double DistanceToPlane(Vect3d point, Vect3d t0, Vect3d t1, Vect3d t2)
{
	Vect3d tmp1 = new Vect3d(t1);
	tmp1.sub(t0);
	Vect3d tmp2 = new Vect3d(t2);
	tmp2.sub(t0);
	Vect3d n = tmp1.cross(tmp2);
	n.unit();
	Vect3d disp = new Vect3d(point);
	disp.sub(t0);
	return Math.abs(disp.dot(n));
}

public static void main(String args[]){
	try {
		Vect3d t1 = new Vect3d(1, 0, 0);
		Vect3d t2 = new Vect3d(0, 2, 0);
		Vect3d t3 = new Vect3d(0, 0, 0);
		Vect3d p0 = new Vect3d(5, 5, 2);		// clearly outside the triangle
		Vect3d p1 = new Vect3d(0.5, 0.5, 1);	// clearly inside the triangle
		double distanceToTriangle3d = 0;
		distanceToTriangle3d = distanceToTriangle3d(p0, t1, t2, t3);
		System.out.println("Distance to triangle = " + distanceToTriangle3d);
		System.out.println(" -------------------------------------------------- ");
		distanceToTriangle3d = distanceToTriangle3d(p1, t1, t2, t3);
		System.out.println("Distance to triangle = " + distanceToTriangle3d);
		System.out.println(" -------------------------------------------------- ");
	}catch (Exception e){
		e.printStackTrace(System.out);
	}
}
public static double distanceToTriangle3d(Vect3d p0, Vect3d t1, Vect3d t2, Vect3d t3) {
	
	Vect3d tmp = new Vect3d(t1);
	tmp.sub(t2);
	Vect3d tmp1 = new Vect3d(t1);
	tmp1.sub(t3);
	Vect3d normal = tmp.cross(tmp1);
	normal.unit();												// 1  - the normal
	System.out.println("the normal = " + normal + ", length = " + normal.length());

	tmp = new Vect3d(p0);
	tmp.sub(t1);
	double cosalpha = tmp.dot(normal) / (tmp.length()*normal.length());	// 2  - cosalpha
	System.out.println("cos alpha = " + cosalpha);

	double projectionLength = tmp.length() * cosalpha;			// 3    - projection length
	System.out.println("projectionLength = " + projectionLength);
				
	Vect3d projection =  new Vect3d(normal);
	projection.uminus();
	projection.scale(projectionLength / normal.length());		// 4    - projection vector
	System.out.println("projection = " + projection);
		
	Vect3d projected = new Vect3d(p0);
	projected.sub(projection);									// 5    - projection of p0 onto the triangle plane
	System.out.println("projected = " + projected);
		
	Vect3d v1 = new Vect3d(t2);					// 6a    - t2t1		
	v1.sub(t1);
	v1.unit();
	Vect3d t3t1 = new Vect3d(t3);
	t3t1.sub(t1);
	t3t1.unit();
	v1.add(t3t1);
	System.out.println("v1 = " + v1);
		
	Vect3d v2 = new Vect3d(t3);					// 6b    - t3t2
	v2.sub(t2);
	v2.unit();
	Vect3d t1t2 = new Vect3d(t1);
	t1t2.sub(t2);
	t1t2.unit();
	v2.add(t1t2);
	System.out.println("v2 = " + v2);
		
	Vect3d v3 = new Vect3d(t1);					// 6c    - t1t3
	v3.sub(t3);
	v3.unit();
	Vect3d t2t3 = new Vect3d(t2);
	t2t3.sub(t3);
	t2t3.unit();
	v3.add(t2t3);
	System.out.println("v3 = " + v3);
		
	tmp = new Vect3d(t1);
	tmp.sub(projected);
	Vect3d f1v = v1.cross(tmp);
	double f1 = f1v.dot(normal);		// f1 > 0 means projected is ^ of v1 (anticlockwise)
	System.out.println("f1 = " + f1);
		
	tmp = new Vect3d(t2);
	tmp.sub(projected);
	Vect3d f2v = v2.cross(tmp);
	double f2 = f2v.dot(normal);
	System.out.println("f2 = " + f2);

	tmp = new Vect3d(t3);
	tmp.sub(projected);
	Vect3d f3v = v3.cross(tmp);
	double f3 = f3v.dot(normal);
	System.out.println("f3 = " + f3);

	boolean bInside = false;
	double distanceToTriangle = 0;
	if(f1 >= 0 && f2 < 0) {
		System.out.println("Projection of point is inside the v1, v2 quadrant...");
		bInside = isInsideTriangle(projected, normal, t1, t2);
		if(bInside == true) {		// easy case, length of projection already known
			System.out.println("Distance from point to triangle is " + projectionLength);
			distanceToTriangle = projectionLength;
		} else {
			distanceToTriangle = distanceToTriangleUtil(projected, t1, t2, p0, projectionLength);
		}
	}
	else if(f2 >= 0 && f3 < 0) {
		System.out.println("Projection of point is inside the v2, v3 quadrant...");
		bInside = isInsideTriangle(projected, normal, t2, t3);
		if(bInside == true) {
			System.out.println("Distance from point to triangle is " + projectionLength);
			distanceToTriangle = projectionLength;
		} else {
			distanceToTriangle = distanceToTriangleUtil(projected, t2, t3, p0, projectionLength);
		}
	}
	else if(f1 < 0 && f3 >= 0) {
		System.out.println("Projection of point is inside the v1, v3 quadrant...");
		bInside = isInsideTriangle(projected, normal, t3, t1);
		if(bInside == true) {
			System.out.println("Distance from point to triangle is " + projectionLength);
			distanceToTriangle = projectionLength;
		} else {
			distanceToTriangle = distanceToTriangleUtil(projected, t3, t1, p0, projectionLength);
		}
	}
	return distanceToTriangle;
}

public static double distanceToTriangleUtil(Vect3d projected, Vect3d left, Vect3d right,
			Vect3d p0, double projectionLength) {
	double distanceToTriangle = 0;
	Vect3d tmp1 = new Vect3d(projected);
	tmp1.sub(left);
	Vect3d tmp2 = new Vect3d(projected);
	tmp2.sub(right);
	Vect3d tmp3 = new Vect3d(left);
	tmp3.sub(right);
	Vect3d r = tmp2.cross(tmp1).cross(tmp3);							// 8    - r
	double cosgamma = tmp1.dot(r) / (tmp1.length() * r.length());		// 9
	double projectionToSegmentLength = tmp1.length() * cosgamma;		// 10
	Vect3d projectedToSegment = new Vect3d(r);
	projectedToSegment.scale(projectionToSegmentLength / r.length());	// 11
	Vect3d p2 = new Vect3d(projected);		// projection of 'projected' onto the segment
	p2.add(projectedToSegment);									// 12
		
	double d1 = Math.sqrt( (p2.getX()-left.getX())*(p2.getX()-left.getX()) +
			(p2.getY()-left.getY())*(p2.getY()-left.getY()) +
			(p2.getZ()-left.getZ())*(p2.getZ()-left.getZ())
	);
	double d2 = Math.sqrt( (right.getX()-left.getX())*(right.getX()-left.getX()) +
			(right.getY()-left.getY())*(right.getY()-left.getY()) +
			(right.getZ()-left.getZ())*(right.getZ()-left.getZ())
	);
	double t = d1/d2;
	if(t<0) {			// p0 is closest to vertex left
		distanceToTriangle = Math.sqrt( (left.getX()-p0.getX())*(left.getX()-p0.getX()) +
				(left.getY()-p0.getY())*(left.getY()-p0.getY()) +
				(left.getZ()-p0.getZ())*(left.getZ()-p0.getZ())
		);
	} else if(t>0) {	// p0 is closest to vertex right
		distanceToTriangle = Math.sqrt( (right.getX()-p0.getX())*(right.getX()-p0.getX()) +
				(right.getY()-p0.getY())*(right.getY()-p0.getY()) +
				(right.getZ()-p0.getZ())*(right.getZ()-p0.getZ())
		);
	} else {			// p0 is closest to line
		distanceToTriangle = Math.sqrt(projectionToSegmentLength*projectionToSegmentLength +
				projectionLength*projectionLength);
	}
	return distanceToTriangle;
}
public static boolean isInsideTriangle(Vect3d point, Vect3d normal, Vect3d left, Vect3d right) {
	Vect3d tmp1 = new Vect3d(point);
	tmp1.sub(left);
	Vect3d tmp2 = new Vect3d(point);
	tmp2.sub(right);
	Vect3d tmp3 = tmp1.cross(tmp2);
	double loc = tmp3.dot(normal);		// 7
	if(loc < 0) {
		System.out.println("        ... and outside the triangle.");
		return false;
	} else {
		System.out.println("        ... and inside the triangle.");
		return true;
	}
}

}

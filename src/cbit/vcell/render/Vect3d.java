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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

import cbit.vcell.geometry.surface.Node;

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
/**
 * Vect3d constructor comment.
 */
public Vect3d(Node p) {
	q[0] = p.getX();
	q[1] = p.getY();
	q[2] = p.getZ();
}

public Vect3d cross(Vect3d v){
	return new Vect3d(q[1]*v.q[2] - q[2]*v.q[1], q[2]*v.q[0] - q[0]*v.q[2], q[0]*v.q[1] - q[1]*v.q[0]);
}
public void crossFast(Vect3d v){
	double x = q[0];
	double y = q[1];
	double z = q[2];
	q[0] = y*v.q[2] - z*v.q[1];
	q[1] = z*v.q[0] - x*v.q[2];
	q[2] = x*v.q[1] - y*v.q[0];
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
public void set(Vect3d v) {
	q[0] = v.q[0];
	q[1] = v.q[1];
	q[2] = v.q[2];
}
public void set(Node p) {
	q[0] = p.getX();
	q[1] = p.getY();
	q[2] = p.getZ();
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
public void uminusFast() {
	q[0] = -q[0];
	q[1] = -q[1];
	q[2] = -q[2];
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

public static double distanceToTriangle3d(Vect3d p0, Vect3d t1, Vect3d t2, Vect3d t3) {
	// we allocate these here once and reuse them (through set()) as much as we can
	// because allocations are very expensive and this method is called many many times
	Vect3d tmp = new Vect3d();
	Vect3d tmp1 = new Vect3d();
	
	tmp.set(t2);
	tmp.sub(t1);
	tmp1.set(t3);
	tmp1.sub(t1);
	Vect3d normal = tmp.cross(tmp1);						// 1  - the normal
	double normalLength = normal.length();
//	System.out.println("the normal = " + normal + ", length = " + normal.length());

	tmp.set(p0);
	tmp.sub(t1);
	double tmpLength = tmp.length();
	double cosalpha = tmp.dot(normal) / (tmpLength*normalLength);	// 2  - cosalpha   
//	System.out.println("cos alpha = " + cosalpha);

	double projectionLength = tmpLength * cosalpha;			// 3    - projection length
//	double otherMethod = DistanceToPlane(p0, t1, t2, t3);
//	System.out.println("projectionLength = " + projectionLength);
				
	Vect3d projection =  new Vect3d(normal);
	projection.uminusFast();
	projection.scale(projectionLength / normalLength);		// 4    - projection vector
//	System.out.println("projection = " + projection);
		
	Vect3d projected = new Vect3d(p0);
	projected.add(projection);									// 5    - projection of p0 onto the triangle plane
//	System.out.println("projected = " + projected);
		
	Vect3d v1 = new Vect3d(t1);					// 6a    - t2t1		
	v1.sub(t2);
	v1.unit();
	tmp.set(t1);								// t3t1
	tmp.sub(t3);
	tmp.unit();
	v1.add(tmp);
//	System.out.println("v1 = " + v1);
		
	Vect3d v2 = new Vect3d(t2);					// 6b    - t3t2
	v2.sub(t3);
	v2.unit();
	tmp.set(t2);								// t1t2
	tmp.sub(t1);
	tmp.unit();
	v2.add(tmp);
//	System.out.println("v2 = " + v2);
		
	Vect3d v3 = new Vect3d(t3);					// 6c    - t1t3
	v3.sub(t1);
	v3.unit();
	tmp.set(t3);								// t2t3
	tmp.sub(t2);
	tmp.unit();
	v3.add(tmp);
//	System.out.println("v3 = " + v3);
		
	tmp.set(projected);
	tmp.sub(t1);
	tmp1.set(v1);						// f1v
	tmp1.crossFast(tmp);
	double f1 = tmp1.dot(normal);		// f1 > 0 means projected is anticlockwise of v1
//	System.out.println("f1 = " + f1);
		
	tmp.set(projected);
	tmp.sub(t2);
	tmp1.set(v2);						// f2v
	tmp1.crossFast(tmp);
	double f2 = tmp1.dot(normal);
//	System.out.println("f2 = " + f2);

	tmp.set(projected);
	tmp.sub(t3);
	tmp1.set(v3);
	tmp1.crossFast(tmp);				// f3v
	double f3 = tmp1.dot(normal);
//	System.out.println("f3 = " + f3);

	boolean bInside = false;
	double distanceToTriangle = 0;
	if(f1 >= 0 && f2 < 0) {
//		System.out.println("Projection of point is inside the t1, t2 quadrant...");
		bInside = isInsideTriangle(projected, normal, t1, t2, tmp, tmp1);
		if(bInside == true) {		// easy case, length of projection already known
//			System.out.println("Distance from point to triangle is " + projectionLength);
			distanceToTriangle = projectionLength;
		} else {
			distanceToTriangle = distanceToTriangleUtil(projected, t1, t2, p0, projectionLength, tmp, tmp1);
		}
	}
	else if(f2 >= 0 && f3 < 0) {
//		System.out.println("Projection of point is inside the t2, t3 quadrant...");
		bInside = isInsideTriangle(projected, normal, t2, t3, tmp, tmp1);
		if(bInside == true) {
//			System.out.println("Distance from point to triangle is " + projectionLength);
			distanceToTriangle = projectionLength;
		} else {
			distanceToTriangle = distanceToTriangleUtil(projected, t2, t3, p0, projectionLength, tmp, tmp1);
		}
	}
	else if(f1 < 0 && f3 >= 0) {
//		System.out.println("Projection of point is inside the t1, t3 quadrant...");
		bInside = isInsideTriangle(projected, normal, t3, t1, tmp, tmp1);
		if(bInside == true) {
//			System.out.println("Distance from point to triangle is " + projectionLength);
			distanceToTriangle = projectionLength;
		} else {
			distanceToTriangle = distanceToTriangleUtil(projected, t3, t1, p0, projectionLength, tmp, tmp1);
		}
	}
	return Math.abs(distanceToTriangle);
}

public static double distanceToTriangleUtil(Vect3d projected, Vect3d left, Vect3d right,
			Vect3d p0, double projectionLength,
			Vect3d tmp1, Vect3d tmp2) {		// transmitted as working buffers, to avoid expensive allocations
	double distanceToTriangle = 0;
	
	tmp1.set(right);
	tmp1.sub(projected);
	tmp2.set(left);
	tmp2.sub(projected);
	double tmp2Length = tmp2.length();
	Vect3d tmp3 = new Vect3d(right);
	tmp3.sub(left);
	tmp1.crossFast(tmp2);												// 8    - r
	tmp1.crossFast(tmp3);
	
	double len = tmp1.length();
	double cosgamma = tmp2.dot(tmp1) / (tmp2Length * len);				// 9
	double projectionToSegmentLength = tmp2Length * cosgamma;			// 10
																	// projectedToSegment (reuse tmp1 for speed)
	tmp1.scale(projectionToSegmentLength / len);						// 11
	tmp3.set(projected);												// projection of 'projected' onto the segment (reuse tmp3 for speed)
	tmp3.add(tmp1);														// 12
	
	double rx = right.getX();
	double ry = right.getY();
	double rz = right.getZ();
	double lx = left.getX();
	double ly = left.getY();
	double lz = left.getZ();
	double px = p0.getX();
	double py = p0.getY();
	double pz = p0.getZ();
	double tx = tmp3.getX();
	double ty = tmp3.getY();
	double tz = tmp3.getZ();
	
	
	double d1 = (tx-lx)*(tx-lx) + (ty-ly)*(ty-ly) + (tz-lz)*(tz-lz);	// distance to left
	double d2 = (tx-rx)*(tx-rx) + (ty-ry)*(ty-ry) + (tz-rz)*(tz-rz);	// distance to right
	double d = (rx-lx)*(rx-lx) + (ry-ly)*(ry-ly) + (rz-lz)*(rz-lz);		// distance between left and right
	
	
	if(d1<=d && d2<=d) {
//		System.out.println(" closest to line ");
		distanceToTriangle = Math.sqrt(projectionToSegmentLength*projectionToSegmentLength +
				projectionLength*projectionLength);
	} else {
		if(d1<d2) {
//			System.out.println(" closest to vertex left " + left);
			distanceToTriangle = Math.sqrt( (lx-px)*(lx-px) + (ly-py)*(ly-py) + (lz-pz)*(lz-pz) );
		} else {
//			System.out.println(" closest to vertex right " + right);
			distanceToTriangle = Math.sqrt( (rx-px)*(rx-px) + (ry-py)*(ry-py) + (rz-pz)*(rz-pz) );
		}
		
	}
	return distanceToTriangle;
}
public static boolean isInsideTriangle(Vect3d point, Vect3d normal, Vect3d left, Vect3d right, 
		Vect3d tmp1, Vect3d tmp2) {		// transmitted as working buffers, to avoid expensive allocations
	tmp1 .set(left);
	tmp1.sub(point);
	tmp2.set(right);
	tmp2.sub(point);
	tmp1.crossFast(tmp2);
	double loc = tmp1.dot(normal);		// 7
	if(loc < 0) {
//		System.out.println("        ... and outside the triangle.");
		return false;
	} else {
//		System.out.println("        ... and inside the triangle.");
		return true;
	}
}

public static void main(String args[]){
	try {
		double distanceToTriangle3d = 0;
		
		Node nt1 = new Node(1, 0, 0);
		Node nt2 = new Node(0, 2, 0);
		Node nt3 = new Node(0, 0, 0.05);
		
		Vect3d t1 = new Vect3d(nt1);
		Vect3d t2 = new Vect3d(nt2);
		Vect3d t3 = new Vect3d(nt3);
		
		{
			Vect3d xt1 = new Vect3d(1,0,5);
			Vect3d xt2 = new Vect3d(0,2,5);
			Vect3d xt3 = new Vect3d(0,0,5);

			Vect3d p1 = new Vect3d(-1,-1,8);
			Vect3d p2 = new Vect3d(-1,-1,2);

			double d1 = DistanceToPlane(p1, xt1, xt2, xt3);
			double d2 = DistanceToPlane(p2, xt1, xt2, xt3);
			System.out.println(d1 + ", " + d2);
			
			d1 = distanceToTriangle3d(p1, xt1, xt2, xt3);
			d2 = distanceToTriangle3d(p2, xt1, xt2, xt3);
			System.out.println(d1 + ", " + d2);


		}
		
		
		{		// quadrant test
		Vect3d a = new Vect3d(0.5, -1, 3);		// inside t1, t3 quadrant
		Vect3d b = new Vect3d(-1, 1, 3);		// inside t2, t3 quadrant
		Vect3d c = new Vect3d(2, 2, 3);			// inside t1, t2 quadrant
		
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t1, t3 quadrant");
		distanceToTriangle3d = distanceToTriangle3d(b, t1, t2, t3);
		System.out.println("Should say it's inside t2, t3 quadrant");
		distanceToTriangle3d = distanceToTriangle3d(c, t1, t2, t3);
		System.out.println("Should say it's inside t1, t2 quadrant");
		}
		
		{
		Vect3d a = new Vect3d();
		a.set(-0.5, -4, 3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t1, t3 quadrant, closest to t3 vertex");
		System.out.println(" -------------------------------------------------- ");
		a.set(0.5, -1, 3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t1, t3 quadrant, closest to line");
		System.out.println(" -------------------------------------------------- ");
		a.set(1.2, -2, 3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t1, t3 quadrant, closest to t1 vertex");
		System.out.println(" -------------------------------------------------- ");

		a.set(-0.5, -4, -3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t1, t3 quadrant, closest to t3 vertex");
		System.out.println(" -------------------------------------------------- ");
		a.set(0.5, -1, -3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t1, t3 quadrant, closest to line");
		System.out.println(" -------------------------------------------------- ");
		a.set(1.2, -2, -3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t1, t3 quadrant, closest to t1 vertex");
		System.out.println(" ==================================================== ");
	
		a.set(3, 0.5, 3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t1, t2 quadrant, closest to t1 vertex");
		System.out.println(" -------------------------------------------------- ");
		a.set(2, 2, 3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t1, t2 quadrant, closest to line");
		System.out.println(" -------------------------------------------------- ");
		a.set(0.5, 4, 3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t1, t2 quadrant, closest to t2 vertex");
		System.out.println(" -------------------------------------------------- ");

		a.set(3, 0.5, -3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t1, t2 quadrant, closest to t1 vertex");
		System.out.println(" -------------------------------------------------- ");
		a.set(2, 2, -3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t1, t2 quadrant, closest to line");
		System.out.println(" -------------------------------------------------- ");
		a.set(0.5, 4, -3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t1, t2 quadrant, closest to t2 vertex");
		System.out.println(" ================================================== ");

		a.set(-1.5, 3, 3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t2, t3 quadrant, closest to t2 vertex");
		System.out.println(" -------------------------------------------------- ");
		a.set(-1, 1, 3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t2, t3 quadrant, closest to line");
		System.out.println(" -------------------------------------------------- ");
		a.set(-3, -0.5, 3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t2, t3 quadrant, closest to t3 vertex");
		System.out.println(" -------------------------------------------------- ");

		a.set(-1.5, 3, -3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t2, t3 quadrant, closest to t2 vertex");
		System.out.println(" -------------------------------------------------- ");
		a.set(-1, 1, -3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t2, t3 quadrant, closest to line");
		System.out.println(" -------------------------------------------------- ");
		a.set(-3, -0.5, -3);
		distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
		System.out.println("Should say it's inside t2, t3 quadrant, closest to t3 vertex");
		System.out.println(" ===================================================== ");

		}
		
		{		// exact distance test
		double distanceToTriangleExperimental = 0;
		Node np1 = new Node(0.3, 0.3, 3);	// inside the triangle
		Node np2 = new Node(-1, -1, 3);		// closest to vertex
		Node np3 = new Node(1, 1, 3);		// closest to line
		Node np4 = new Node(0.5, -0.5, 3);	// closest to line
		
		Vect3d p1 = new Vect3d(np1);
		Vect3d p2 = new Vect3d(np2);
		Vect3d p3 = new Vect3d(np3);
		Vect3d p4 = new Vect3d(np4);

		distanceToTriangleExperimental = distanceToTriangleExperimental(np1, nt1, nt2, nt3);
		distanceToTriangle3d = distanceToTriangle3d(p1, t1, t2, t3);
		System.out.println("Distance to triangle = " + distanceToTriangle3d + "  (inside the triangle) - should be " + distanceToTriangleExperimental);
		System.out.println(" -------------------------------------------------- ");
		
		distanceToTriangleExperimental = distanceToTriangleExperimental(np2, nt1, nt2, nt3);
		distanceToTriangle3d = distanceToTriangle3d(p2, t1, t2, t3);
		System.out.println("Distance to triangle = " + distanceToTriangle3d + "  (closest to vertex) - should be " + distanceToTriangleExperimental);
		System.out.println(" -------------------------------------------------- ");
		
		distanceToTriangleExperimental = distanceToTriangleExperimental(np3, nt1, nt2, nt3);
		distanceToTriangle3d = distanceToTriangle3d(p3, t1, t2, t3);
		System.out.println("Distance to triangle = " + distanceToTriangle3d + "  (closest to line) - should be " + distanceToTriangleExperimental);
		System.out.println(" -------------------------------------------------- ");
		
		distanceToTriangleExperimental = distanceToTriangleExperimental(np4, nt1, nt2, nt3);
		distanceToTriangle3d = distanceToTriangle3d(p4, t1, t2, t3);
		System.out.println("Distance to triangle = " + distanceToTriangle3d + "  (closest to line) - should be " + distanceToTriangleExperimental);
		System.out.println(" -------------------------------------------------- ");
		}
		
		{		// an error case, to be fixed
//				47, 37, 39
//				44, 44, 38	closest
//				56, 38, 49
//				17, 60, 40	test point
//				- from points: 31.692043572731926
//				- exact comp : 45.74405664170047
//				- error: 14.052013068968542
			
			Node ntt1 = new Node(47, 37, 39);
			Node ntt2 = new Node(44, 44, 38);		// closest to this one
			Node ntt3 = new Node(56, 38, 49);
			Node naa = new Node(17, 60, 40);
			
			Vect3d tt1 = new Vect3d(ntt1);
			Vect3d tt2 = new Vect3d(ntt2);
			Vect3d tt3 = new Vect3d(ntt3);
			Vect3d aa = new Vect3d(naa);
			
			double x1 = distanceBetweenPoints(naa, ntt1);
			double x2 = distanceBetweenPoints(naa, ntt2);
			double x3 = distanceBetweenPoints(naa, ntt3);
			System.out.println("Distance to verteces: " + x1 +", " + x2 + "' " + x3);
			double distanceToTriangleExperimental = distanceToTriangleExperimental(naa, ntt1, ntt2, ntt3);
			distanceToTriangle3d = distanceToTriangle3d(aa, tt1, tt2, tt3);
			System.out.println("Distance to triangle = " + distanceToTriangle3d + "  (closest to vertex t2) - should be " + distanceToTriangleExperimental);
		}
	} catch (Exception e) {
		e.printStackTrace(System.out);
	}
	
	// we work inside a cube of 100x100x100
	// we pick a triangle inside and we fill it with random points
	// we randomly generate points within the cube and we compute the distance to triangle in 2 ways:
	// using distanceToTriangle3d()
	// we compute the distance from the point to each of the points within the triangle and we keep the smallest result
	// different results mean that there are errors in distanceToTriangle3d()
	Random rand = new Random();
	Node testPoint = new Node();
	Node A = new Node(30+rand.nextDouble()*30, 30+rand.nextDouble()*30, 30+rand.nextDouble()*30);
	Node B = new Node(30+rand.nextDouble()*30, 30+rand.nextDouble()*30, 30+rand.nextDouble()*30);
	Node C = new Node(30+rand.nextDouble()*30, 30+rand.nextDouble()*30, 30+rand.nextDouble()*30);
	System.out.println("Node A:  " + A.getX() + ", " + A.getY() + ", " + A.getZ());
	System.out.println("Node B:  " + B.getX() + ", " + B.getY() + ", " + B.getZ());
	System.out.println("Node C:  " + C.getX() + ", " + C.getY() + ", " + C.getZ());
	
	Vect3d vTestPoint = new Vect3d();
	Vect3d vA = new Vect3d(A);
	Vect3d vB = new Vect3d(B);
	Vect3d vC = new Vect3d(C);
	
	int counter = 0;
	for(int i=0; i<1000; i++) {			// randomly generate some points and compute the distance from them to the triangle
	
		testPoint.setX(rand.nextDouble()*100);
		testPoint.setY(rand.nextDouble()*100);
		testPoint.setZ(rand.nextDouble()*100);
		vTestPoint.set(testPoint);
//		System.out.println("testPoint:  " + testPoint.getX() + ", " + testPoint.getY() + ", " + testPoint.getZ());

		double eD = distanceToTriangleExperimental(testPoint, A, B, C);
		double eE = distanceToTriangle3d(vTestPoint, vA, vB, vC);
		if(Math.abs(eD-eE) > 0.05) {
//			System.out.println("- from points: " + eD);
//			System.out.println("- exact comp : " + eE);
			System.out.println("- error: " + Math.abs(eD-eE));
			counter++;
		}
	
	}
	System.out.println(counter + " errors");
}

private static double distanceToTriangleExperimental(Node p, Node A, Node B, Node C) {
	double d = Double.MAX_VALUE;
	Node closestNode = null;
	
	Random rand = new Random();
	try {
	BufferedWriter out = new BufferedWriter(new FileWriter("c:\\TEMP\\triangle.3D"));
	out.write("x y z value\n");
	out.write(p.getX()+1 + " " + p.getY() + " " + p.getZ() + " 1\n");
	out.write(p.getX() + " " + p.getY()+1 + " " + p.getZ() + " 1\n");
	out.write(p.getX() + " " + p.getY() + " " + p.getZ()+1 + " 1\n");

	for(int i=0; i<10000; i++) {
		double a = rand.nextDouble();
		double b = rand.nextDouble();
		Node r = PointInTriangle(a, b, A, B, C);
		String line = new String(r.getX() + " " + r.getY() + " " + r.getZ() + " 2\n");
		out.write(line);

		double dd = distanceBetweenPoints(p, r);
		if(dd < d) {
			d = dd;
			closestNode = r;
		}
	}
	
	Node[] NA = new Node[3];		// check the vertexes as well
	NA[0] = A;
	NA[1] = B;
	NA[2] = C;
	for(int i=0; i<3; i++) {
		String line = new String(NA[i].getX() + " " + NA[i].getY() + " " + NA[i].getZ() + " 2\n");
		out.write(line);
		double dd = distanceBetweenPoints(p, NA[i]);
		if(dd < d) {
			d = dd;
			closestNode = NA[i];
		}
	}

	String line1 = new String(closestNode.getX() + " " + closestNode.getY() + " " + closestNode.getZ() + " 3\n");
	out.write(line1);
//	System.out.println("closestNode:  " + closestNode.getX() + ", " + closestNode.getY() + ", " + closestNode.getZ());
	out.close();
	} catch (IOException e) {
	}
	return d;
}
private static double distanceBetweenPoints(Node p, Node r) {
	double dd = Math.sqrt(	(p.getX()-r.getX())*(p.getX()-r.getX()) + 
							(p.getY()-r.getY())*(p.getY()-r.getY()) + 
							(p.getZ()-r.getZ())*(p.getZ()-r.getZ()) );
	return dd;
}

private static Node PointInTriangle(double a, double b, Node A, Node B, Node C)
{
	double c = 0;
	double px, py, pz;
	if (a + b > 1)
	{
		a = 1 - a;
		b = 1 - b;
	}
	c = 1 - a - b;

	px = (a * A.getX()) + (b * B.getX()) + (c * C.getX());
	py = (a * A.getY()) + (b * B.getY()) + (c * C.getY());
	pz = (a * A.getZ()) + (b * B.getZ()) + (c * C.getZ());
	Node point = new Node(px, py, pz);
	return point;
}

}

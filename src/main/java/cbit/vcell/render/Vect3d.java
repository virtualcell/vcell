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

import cbit.vcell.geometry.concept.ThreeSpacePoint;
import cbit.vcell.geometry.surface.Node;

/**
 * Insert the type's description here.
 * Creation date: (11/29/2003 1:04:15 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class Vect3d implements ThreeSpacePoint, Serializable {
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
public Vect3d(ThreeSpacePoint p) {
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

}

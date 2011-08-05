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

/**
 * Insert the type's description here.
 * Creation date: (11/29/2003 11:36:53 AM)
 * @author: Jim Schaff
 */
public class Quaternion {
	private double q[] = new double[4];  // X,Y,Z,W   W is scalar, XYZ imag vect
      

	public static final int X = 0;
	public static final int Y = 1;
	public static final int Z = 2;
	public static final int W = 3;
	public static final int next[] = {Y,Z,X};

	private static int count=0;
public Quaternion()
{
  q[0]=0.0;
  q[1]=0.0;
  q[2]=0.0;
  q[3]=1.0;
}
/**
 * Quaternion constructor comment.
 */
public Quaternion(double x,double y, double z, double w)
{
  q[0]=x;
  q[1]=y;
  q[2]=z;
  q[3]=w;
}
	public double abs() { return Math.sqrt(norm()); }
///
//  q3 = q1 + q2
//
//  This doesn't have a rotational counterpart that I know of.
//
public Quaternion add(Quaternion q1, Quaternion q2)
{
Quaternion dest = new Quaternion();

   dest.q[0] = q1.q[0] + q2.q[0];
   dest.q[1] = q1.q[1] + q2.q[1];
   dest.q[2] = q1.q[2] + q2.q[2];
   dest.q[3] = q1.q[3] + q2.q[3];
   return dest;
}
///
//  q3 = q1 / s
//
//  This doesn't have a rotational counterpart that I know of.
//
public Quaternion divide(Quaternion q1, double s)
{
Quaternion dest = new Quaternion();

   dest.q[0] = q1.q[0] / s;
   dest.q[1] = q1.q[1] / s;
   dest.q[2] = q1.q[2] / s;
   dest.q[3] = q1.q[3] / s;
   return dest;
}
public double getComponent(int i)
{
   //assert(i>=0 && i<=3);
   return q[i];   
}
	public double getScalar() { return q[3]; }
public Vect3d getVector() {
    return new Vect3d(q[0], q[1], q[2]);
}
/* len = |a|        */
public double length()
{
double mag;

   mag = q[0]*q[0] + q[1]*q[1] + q[2]*q[2] + q[3]*q[3];
   return mag;
}
///
//  q3 = q1 * s
//
//  This doesn't have a rotational counterpart that I know of.
//
public static Quaternion mult(Quaternion q1, double s)
{
Quaternion dest = new Quaternion();

   dest.q[0] = q1.q[0] * s;
   dest.q[1] = q1.q[1] * s;
   dest.q[2] = q1.q[2] * s;
   dest.q[3] = q1.q[3] * s;
   return dest;
}
///
// Given two rotations, q1 and q2, expressed as quaternion rotations,
// and return the equivalent single rotation.
// 
// This routine also normalizes the result every time it is
// called, to keep error from creeping in.
//
public static Quaternion mult(Quaternion q1, Quaternion q2) {
    Vect3d v1 = q1.getVector();
    Vect3d v2 = q2.getVector();
    double s1 = q1.getScalar();
    double s2 = q2.getScalar();

    double scalar = (s1 * s2) - v1.dot(v2);

    Vect3d v3 = v1.cross(v2);
    v1.scale(s2);
    v2.scale(s1);

    Vect3d destVect = v1;
    destVect.add(v2);
    destVect.add(v3);

    Quaternion dest = new Quaternion();
    dest.setVector(destVect);
    dest.setScalar(scalar);

    //
    // Since we are only using Quaternions for rotations, and since
    // rotations are only mapped to Quaternions of unit length, 
    // every so often they should be normalized.
    //
    count++;
    if (count > 100) {
        count = 0;
        dest.normalize();
    }

    return dest;
}
	public double norm() { return length(); }
/* a = a/|a|        */
public void normalize()
{
double mag;

   mag = Math.sqrt(length());

   q[0] /= mag;
   q[1] /= mag;
   q[2] /= mag;
   q[3] /= mag;
}
/* a = [x y z w]    */
public void set(double x,double y,double z,double w)
{
   q[0] = x;
   q[1] = y;
   q[2] = z;
   q[3] = w;
}
//
// a = [v, w]
//
public void setAxis(Vect3d axis, double phi)
{
   Vect3d tempAxis = new Vect3d(axis);
   tempAxis.unit();
   tempAxis.scale(Math.sin(phi/2.0));
   setVector(tempAxis);
   setScalar(Math.cos(phi/2.0));
}
	public void setScalar(double s) { q[3] = s; }
	public void setVector(Vect3d v) { 
		q[0]=v.q[0]; q[1]=v.q[1]; q[2]=v.q[2];
	}
///
//  q3 = q1 - q2
//
//  This doesn't have a rotational counterpart that I know of.
//
public static Quaternion sub(Quaternion q1, Quaternion q2)
{
Quaternion dest = new Quaternion();

   dest.q[0] = q1.q[0] - q2.q[0];
   dest.q[1] = q1.q[1] - q2.q[1];
   dest.q[2] = q1.q[2] - q2.q[2];
   dest.q[3] = q1.q[3] - q2.q[3];
   return dest;
}
public String toString()
{
	return getClass().getName() + "@" + Integer.toHexString(hashCode())+" ("+q[0]+", "+q[1]+", "+q[2]+", "+q[3]+")";
}
public void zero()
{
   q[0] = 0.0; 
   q[1] = 0.0; 
   q[2] = 0.0; 
   q[3] = 1.0; 
}

/**
 * Quaternion constructor comment.
 */
public Quaternion(Quaternion r)
{
  q[0]=r.q[0];
  q[1]=r.q[1];
  q[2]=r.q[2];
  q[3]=r.q[3];
}


public Matrix3d getMatrix()
{
Matrix3d mat = new Matrix3d();

   double s = 2.0/length();

   mat.m[0][0] = 1.0 - s * (q[Y]*q[Y] + q[Z]*q[Z]);
   mat.m[0][1] = s * (q[X]*q[Y] + q[W]*q[Z]);
   mat.m[0][2] = s * (q[X]*q[Z] - q[W]*q[Y]);

   mat.m[1][0] = s * (q[X]*q[Y] - q[W]*q[Z]);
   mat.m[1][1] = 1.0 - s * (q[X]*q[X] + q[Z]*q[Z]);
   mat.m[1][2] = s * (q[Y]*q[Z] + q[W]*q[X]);

   mat.m[2][0] = s * (q[X]*q[Z] + q[W]*q[Y]);
   mat.m[2][1] = s * (q[Y]*q[Z] - q[W]*q[X]);
   mat.m[2][2] = 1.0 - s * (q[X]*q[X] + q[Y]*q[Y]);

   return mat;
}


//
// where mat[16] is in column major order
//  this format is used in OpenGL
//                
public void loadAffine(Affine mat)
{
   Matrix3d mat4 = getMatrix();

   mat.m[0][0]  = mat4.m[0][0];
   mat.m[1][0]  = mat4.m[1][0];
   mat.m[2][0]  = mat4.m[2][0];
   mat.m[3][0]  = 0.0;

   mat.m[0][1]  = mat4.m[0][1];
   mat.m[1][1]  = mat4.m[1][1];
   mat.m[2][1]  = mat4.m[2][1];
   mat.m[3][1]  = 0.0;

   mat.m[0][2]  = mat4.m[0][2];
   mat.m[1][2]  = mat4.m[1][2];
   mat.m[2][2] = mat4.m[2][2];
   mat.m[3][2] = 0.0;

   mat.m[0][3] = 0.0;   
   mat.m[1][3] = 0.0;   
   mat.m[2][3] = 0.0;   
   mat.m[3][3] = 1.0;
}


//
// where mat[16] is in column major order
//  this format is used in OpenGL
//                
public void loadMatrix16(double mat[])
{
Matrix3d mat4 = new Matrix3d();

   mat4 = getMatrix();

   mat[0]  = mat4.m[0][0];
   mat[1]  = mat4.m[1][0];
   mat[2]  = mat4.m[2][0];
   mat[3]  = 0.0;

   mat[4]  = mat4.m[0][1];
   mat[5]  = mat4.m[1][1];
   mat[6]  = mat4.m[2][1];
   mat[7]  = 0.0;

   mat[8]  = mat4.m[0][2];
   mat[9]  = mat4.m[1][2];
   mat[10] = mat4.m[2][2];
   mat[11] = 0.0;

   mat[12] = 0.0;   
   mat[13] = 0.0;   
   mat[14] = 0.0;   
   mat[15] = 1.0;
}


/**
 * Quaternion constructor comment.
 */
public void set(Quaternion r)
{
  q[0]=r.q[0];
  q[1]=r.q[1];
  q[2]=r.q[2];
  q[3]=r.q[3];
}


// 
// a = M
//
public void setMatrix(Matrix3d mat)
{
double tr,s;
int i,j,k;

   tr = mat.m[0][0] + mat.m[1][1] + mat.m[2][2];
   if (tr > 0.0) {
     s = Math.sqrt(tr + 1.0);
     q[W] = s * 0.5;
     s = 0.5 / s;

     q[X] = (mat.m[1][2] - mat.m[2][1])*s;
     q[Y] = (mat.m[2][0] - mat.m[0][2])*s;
     q[Z] = (mat.m[0][1] - mat.m[1][0])*s;
   }else{
     i = X;
     if (mat.m[Y][Y] > mat.m[X][X]) i = Y;
     if (mat.m[Z][Z] > mat.m[i][i]) i = Z;
     j = next[i];
     k = next[j];

     s = Math.sqrt( (mat.m[i][i] - (mat.m[j][j] + mat.m[k][k])) + 1.0);
     q[i] = s*0.5;
     s = 0.5/s;
     q[W] = (mat.m[j][k] - mat.m[k][j])*s;
     q[j] = (mat.m[i][j] - mat.m[j][i])*s;
     q[k] = (mat.m[i][k] - mat.m[k][i])*s;
   }
}
}

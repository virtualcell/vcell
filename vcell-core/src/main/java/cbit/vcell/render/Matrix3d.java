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
 * Creation date: (11/29/2003 1:14:29 PM)
 * @author: Jim Schaff
 */
public class Matrix3d {
	double m[][] = new double[3][3];  // array defined in row major form
/**
 * Matrix4d constructor comment.
 */
public Matrix3d() {
	zero();
}
public Matrix3d(Matrix3d a) {
    m[0][0] = a.m[0][0];
    m[0][1] = a.m[0][1];
    m[0][2] = a.m[0][2];
    m[1][0] = a.m[1][0];
    m[1][1] = a.m[1][1];
    m[1][2] = a.m[1][2];
    m[2][0] = a.m[2][0];
    m[2][1] = a.m[2][1];
    m[2][2] = a.m[2][2];
}
/*  A += B            */
public void add(Matrix3d b)
{        
 for (int i=0;i<3;i++) 
   for (int j=0;j<3;j++)
     m[i][j] += b.m[i][j];
}
/* A /= b            */
public void div(double b)
{        
 for (int i=0;i<3;i++)
   for (int j=0;j<3;j++)
     m[i][j] /= b;
}
public void identity()
{
  m[0][0]=1.0;
  m[0][1]=0.0;
  m[0][2]=0.0;
  m[1][0]=0.0;
  m[1][1]=1.0;
  m[1][2]=0.0;
  m[2][0]=0.0;
  m[2][1]=0.0;
  m[2][2]=1.0;
}
/* A = inv(A)        */
public void invert()
{
  final int N = 3;
  int i, j, k;
  double temp;
  double holdingMat[][] = new double[2*N][N];

  Matrix3d mat= new Matrix3d(this);      // mat has copy of original matrix, result = 'this'
  identity();                          // initialize 'result' to identity matrix
  
  //
  // declare identity matrix
  //
  for (i=0;i<N;i++){
    for (j=0;j<N;j++){
       holdingMat[i][j] = mat.m[i][j];
       holdingMat[i+N][j] = m[i][j];
    }
  }
  //
  // work across by columns
  //
  for (i=0;i<N;i++){
     for (j=i;(holdingMat[i][j] == 0.0)&&(j<N);j++) {}
     if (j==N){
		throw new RuntimeException("error: cannot do inverse matrix\n");
     }else{
	if (i != j){
	   for (k=0;k<(2*N);k++){
	       temp = holdingMat[k][i];
	       holdingMat[k][i] = holdingMat[k][j];
	       holdingMat[k][j] = temp;
	   }
	}
     }
     //
     // divide original row
     //
     for (j=2*N-1;j>=i;j--){
	holdingMat[j][i] /= holdingMat[i][i];
     }
     //
     // subtract other rows
     //
     for (j=0;j<N;j++){
	if (i != j){
	   for (k=2*N-1;k>=i;k--){
	      holdingMat[k][j] -= holdingMat[k][i] * holdingMat[i][j];
	   }
	}
     }
  }
  for (i=0;i<N;i++){
     for (j=0;j<N;j++){
	m[i][j] = holdingMat[i+N][j];
     }
  }
}
public static Matrix3d makeXRotation(double angleRad)
{
double s = Math.sin(angleRad);
double c = Math.cos(angleRad);
Matrix3d mat = new Matrix3d();
  mat.identity();
  mat.m[0][0]=1.0;  mat.m[0][1]=0.0;  mat.m[0][2]=0.0;
  mat.m[1][0]=0.0;  mat.m[1][1]=  c;  mat.m[1][2]=  s;
  mat.m[2][0]=0.0;  mat.m[2][1]= -s;  mat.m[2][2]=  c;
  return mat;
}
public static Matrix3d makeYRotation(double angleRad)
{
double s = Math.sin(angleRad);
double c = Math.cos(angleRad);
Matrix3d mat = new Matrix3d();
  mat.m[0][0]=  c;  mat.m[0][1]=0.0;  mat.m[0][2]= -s;
  mat.m[1][0]=0.0;  mat.m[1][1]=1.0;  mat.m[1][2]=0.0;
  mat.m[2][0]=  s;  mat.m[2][1]=0.0;  mat.m[2][2]=  c;
  return mat;
}
public static Matrix3d makeZRotation(double angleRad)
{
double s = Math.sin(angleRad);
double c = Math.cos(angleRad);
Matrix3d mat = new Matrix3d();
  mat.m[0][0]=  c;  mat.m[0][1]=  s;  mat.m[0][2]=0.0;
  mat.m[1][0]= -s;  mat.m[1][1]=  c;  mat.m[1][2]=0.0;
  mat.m[2][0]=0.0;  mat.m[2][1]=0.0;  mat.m[2][2]=1.0;
  return mat;
}
/* A *= b            */
public void mult(double b)
{        
 for (int i=0;i<3;i++)
   for (int j=0;j<3;j++)
      m[i][j] *= b;
}
/* A *= B            */
public void mult(Matrix3d b)
{
 Matrix3d c= new Matrix3d(this);
       
 this.zero();
 for (int i=0;i<3;i++){
   for (int j=0;j<3;j++){
     for (int k=0;k<3;k++){
       m[i][j] += (c.m[i][k] * b.m[k][j]);
     }
   }                   
 }
}
/* c = A * b            */
public static Vect3d mult(Matrix3d a, Vect3d b)
{
 double temp;      
 Vect3d c = new Vect3d();

 for (int i=0;i<3;i++) {
   temp=0.0;
   for (int j=0;j<3;j++) {
     temp += a.m[i][j] * b.q[j];
   }
   c.q[i] = temp;
 }
 return c;
}
public void set(double x11, double x21, double x31,
		    double x12, double x22, double x32,
		    double x13, double x23, double x33)
{
  m[0][0]=x11;
  m[0][1]=x12;
  m[0][2]=x13;
  m[1][0]=x21;
  m[1][1]=x22;
  m[1][2]=x23;
  m[2][0]=x31;
  m[2][1]=x32;
  m[2][2]=x33;
}
public void set(Vect3d v1, Vect3d v2, Vect3d v3)
{
  m[0][0]=v1.q[0];  m[0][1]=v2.q[0];  m[0][2]=v3.q[0];
  m[1][0]=v1.q[1];  m[1][1]=v2.q[1];  m[1][2]=v3.q[1];
  m[2][0]=v1.q[2];  m[2][1]=v2.q[2];  m[2][2]=v3.q[2];
}
public void setCosineRotation(Vect3d anglesRad)
{
	//
	// postmultiply X*Y*Z = RotationMatrix
	//
	identity();
	mult(Matrix3d.makeXRotation(anglesRad.q[0]));
	mult(Matrix3d.makeYRotation(anglesRad.q[1]));
	mult(Matrix3d.makeZRotation(anglesRad.q[2]));
	
	//
	// premultiply Z*Y*X = RotationMatrix
	//
	//Matrix3d rot = Matrix3d.makeZRotation(anglesRad.getZ());
	//rot.mult(Matrix3d.makeYRotation(anglesRad.getY()));
	//rot.mult(Matrix3d.makeXRotation(anglesRad.getX()));
	//identity();
	//mult(rot);
}
/**
 * Insert the method's description here.
 * Creation date: (11/29/2003 3:05:13 PM)
 * @return cbit.vcell.render.Matrix4d
 * @param axis cbit.vcell.render.Vect3d
 * @param angleRad double
 */
public void setEulerRotation(Vect3d axis, double angleRad) {
	Matrix3d rotation = new Matrix3d();

  double x = axis.q[0];
  double y = axis.q[1];
  double z = axis.q[2];

  // find angle from the z-axis to the axis of rotation
  //
  //  <axis,k> = |axis| |k| cos(phi) 
  //                               where k is unit vector for z
  //  axis_z = |axis| * cos(phi)
  //  phi = acos(axis_z/|axis|)   
  //
  double phi = Math.acos(z/axis.length());

  // find angle from the x-axis to the projection of the 
  // axis of rotation on the x,y plane
  //
  //  projection:
  //
  //     (x,y,0)
  //
  //  angle:
  //
  //     y/x = tan(theta)
  //
  double theta = Math.atan2(y,x);

  identity();
  mult(rotation.makeZRotation(-theta));
  mult(rotation.makeYRotation(-phi));
  mult(rotation.makeZRotation(angleRad));
  mult(rotation.makeYRotation(phi));
  mult(rotation.makeZRotation(theta));

}
public void setInvCosineRotation(Vect3d anglesRad)
{
  identity();
  mult(Matrix3d.makeZRotation(-anglesRad.q[2]));
  mult(Matrix3d.makeYRotation(-anglesRad.q[1]));
  mult(Matrix3d.makeXRotation(-anglesRad.q[0]));
  //
  // ??? must analyse this whole thing ... premultiply vs. postmultiply.
  //
  //identity();
  //mult(Matrix3d.makeZRotation(-anglesRad.getX()));
  //mult(Matrix3d.makeYRotation(-anglesRad.getY()));
  //mult(Matrix3d.makeXRotation(-anglesRad.getZ()));
}
public void show()
{
System.out.println("M = "+m[0][0]+"  "+m[1][0]+"  "+m[2][0]);
System.out.println("    "+m[0][1]+"  "+m[1][1]+"  "+m[2][1]);
System.out.println("    "+m[0][2]+"  "+m[1][2]+"  "+m[2][2]);
}
/* A -= B            */
public void sub(Matrix3d b)
{        
 for (int i=0;i<3;i++)
   for (int j=0;j<3;j++)
     m[i][j] -= b.m[i][j];
}
/* A = A'            */
public void transpose()
{
  for (int i=0;i<3;i++){
    for (int j=0;j<3;j++){
	    double temp = m[i][j];
	    m[i][j] = m[j][i];
	    m[j][i] = temp;
    }
  }
}
	public void zero()
{
  m[0][0]=0.0;
  m[1][0]=0.0;
  m[2][0]=0.0;
  m[0][1]=0.0;
  m[1][1]=0.0;
  m[2][1]=0.0;
  m[0][2]=0.0;
  m[1][2]=0.0;
  m[2][2]=0.0;
}
}

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
 * Creation date: (11/29/2003 1:59:38 PM)
 * @author: Jim Schaff
 */
//
// in order to accommodate OpenGL, all operations are performed in column
// major form (this allows passing matrices between this library and OpenGL
// as double * without translation).
//
// the order should be transparent to the client unless the 
//   ptr() or Affine(double[16])
// member functions are used.   stored as m[col][row]
//
// note: traditional subscript notation for matrices is row major
//
public class Affine implements Serializable{
	double m[][] = new double[4][4];

public Affine()
{
  zero();
}
public Affine(double d[])
{                       
 for (int i=0;i<4;i++){  // column of final answer
   for (int j=0;j<4;j++){  // row of final answer
		m[i][j] = d[i+4*j];
	}
 }
}
public Affine(Affine a)
{
	for (int i=0;i<4;i++){
		for (int j=0;j<4;j++){
			m[i][j] = a.m[i][j];
		}
	}
}
/* c = a + b            */
public void add(Affine b)
{        
 for (int col=0;col<4;col++) {
   for (int row=0;row<4;row++) {
     m[col][row] += b.m[col][row];
   }
 }
}
/* c = a / b            */
public void div(double b)
{        
 for (int i=0;i<4;i++)
   for (int j=0;j<4;j++)
     m[i][j] /= b;
}
void getAffine(double d[])
{
 for (int i=0;i<4;i++){
   for (int j=0;j<4;j++){
      d[i] *= m[i][j]; // unsure about order (maybe transpose)
   }
 }
}
public void identity()
{
  zero();  
  m[0][0]=1.0;
  m[1][1]=1.0;
  m[2][2]=1.0;
  m[3][3]=1.0;
}
/* c = a * b            */
public Vect3d mult(Vect3d v)
{

	double[] vin = new double[4];
	vin[0] = v.getX();
	vin[1] = v.getY();
	vin[2] = v.getZ();
	vin[3] = 1.0;
	double[] vout = new double[4];
	for (int i=0;i<4;i++){
		for (int j=0;j<4;j++){
			vout[i] += vin[j] * m[j][i];
		}
	}
	return new Vect3d(vout[0]/vout[3],vout[1]/vout[3],vout[2]/vout[3]);
}
/* c = a * b            */
public void mult(double b)
{        
 for (int i=0;i<4;i++)
   for (int j=0;j<4;j++)
      m[i][j] *= b;
}
/* c *= b               */
public void mult(Affine b)
{
 Affine c= new Affine(this);
       
 zero();
 for (int i=0;i<4;i++){  // column of final answer
   for (int j=0;j<4;j++){  // row of final answer
     for (int k=0;k<4;k++){
       m[i][j] += (c.m[k][j] * b.m[i][k]);
     }
   }                   
 }
}
/* c *= b               */
public static Affine mult(Affine a, Affine b)
{
 Affine c = new Affine(a);
       
 c.zero();
 for (int i=0;i<4;i++){  // column of final answer
   for (int j=0;j<4;j++){  // row of final answer
     for (int k=0;k<4;k++){
       c.m[i][j] += (a.m[k][j] * b.m[i][k]);
     }
   }                   
 }
 return c;
}
/*
matrix getMatrix()
{
 static matrix mat;

 mat.m[0][0]=m[0][0]; mat.m[0][1]=m[1][0]; mat.m[0][2]=m[2][0]; 
 mat.m[1][0]=m[0][1]; mat.m[1][1]=m[1][1]; mat.m[1][2]=m[2][1]; 
 mat.m[2][0]=m[0][2]; mat.m[2][1]=m[1][2]; mat.m[2][2]=m[2][2]; 

 return mat;
}

vector getVector()
{
 static vector c;

 c.p[0] = m[3][0];
 c.p[1] = m[3][0];
 c.p[2] = m[3][0];

 c /= m[3][3];

 return c;
}
*/

/* c = A * b            */
static Vect3d mult(Affine a, Vect3d b)
{
 double temp;      
 double vect_in[] = new double[4];
 double vect_out[] = new double[4];
 vect_in[0]=b.q[0];
 vect_in[1]=b.q[1];
 vect_in[2]=b.q[2];
 vect_in[3]=1.0;

 for (int row=0;row<4;row++) {
   temp=0.0;
   for (int col=0;col<4;col++) {
     temp += (a.m[col][row] * vect_in[col]);
   }
   vect_out[row] = temp;
 }
 return new Vect3d(vect_out[0]/vect_out[3],
       				vect_out[1]/vect_out[3],
       				vect_out[2]/vect_out[3]);
}
/*
matrix getMatrix()
{
 static matrix mat;

 mat.m[0][0]=m[0][0]; mat.m[0][1]=m[1][0]; mat.m[0][2]=m[2][0]; 
 mat.m[1][0]=m[0][1]; mat.m[1][1]=m[1][1]; mat.m[1][2]=m[2][1]; 
 mat.m[2][0]=m[0][2]; mat.m[2][1]=m[1][2]; mat.m[2][2]=m[2][2]; 

 return mat;
}

vector getVector()
{
 static vector c;

 c.p[0] = m[3][0];
 c.p[1] = m[3][0];
 c.p[2] = m[3][0];

 c /= m[3][3];

 return c;
}
*/

/* c = A * b            */
static Vect3d mult(Vect3d b, Affine a)
{
 double temp;      
 double vect_in[] = new double[4];
 double vect_out[] = new double[4];
 vect_in[0]=b.q[0];
 vect_in[1]=b.q[1];
 vect_in[2]=b.q[2];
 vect_in[3]=1.0;

 for (int col=0;col<4;col++) {
   temp=0.0;
   for (int row=0;row<4;row++) {
     temp += (a.m[col][row] * vect_in[row]);
   }
   vect_out[col] = temp;
 }
 return new Vect3d(vect_out[0]/vect_out[3],
       				vect_out[1]/vect_out[3],
       				vect_out[2]/vect_out[3]);
}
public void set(Affine a)
{
	for (int i=0;i<4;i++){
		for (int j=0;j<4;j++){
			m[i][j] = a.m[i][j];
		}
	}
}
public void set(Matrix3d mat, Vect3d v)
{
  m[0][0]=mat.m[0][0]; m[1][0]=mat.m[0][1]; m[2][0]=mat.m[0][2]; m[3][0]=v.q[0];
  m[0][1]=mat.m[1][0]; m[1][1]=mat.m[1][1]; m[2][1]=mat.m[1][2]; m[3][1]=v.q[1];
  m[0][2]=mat.m[2][0]; m[1][2]=mat.m[2][1]; m[2][2]=mat.m[2][2]; m[3][2]=v.q[2];
  m[0][3]=0.0;         m[1][3]=0.0;         m[2][3]=0.0;         m[3][3]=1.0;
}
public void setCol(Vect3d v1, Vect3d v2, Vect3d v3, Vect3d v4)
{
  m[0][0]=v1.q[0];       m[1][0]=v2.q[0];       m[2][0]=v3.q[0];       m[3][0]=v4.q[0];
  m[0][1]=v1.q[1];       m[1][1]=v2.q[1];       m[2][1]=v3.q[1];       m[3][1]=v4.q[1];
  m[0][2]=v1.q[2];       m[1][2]=v2.q[2];       m[2][2]=v3.q[2];       m[3][2]=v4.q[2];
  m[0][3]=0;           m[1][3]=0;           m[2][3]=0;           m[3][3]=1;
}
public void setFrustum(double left,double right,
                        double bottom, double top,
                        double aNear, double aFar)
{
  m[0][0] = (2*aNear)/(right-left); 
  m[1][0] = 0.0; 
  m[2][0] = (right+left)/(right-left); 
  m[3][0] = 0.0;
  m[0][1] = 0.0; 
  m[1][1] = (2*aNear)/(top-bottom); 
  m[2][1] = (top+bottom)/(top-bottom); 
  m[3][1] = 0.0;
  m[0][2] = 0.0; 
  m[1][2] = 0.0; 
  m[2][2] = -(aFar+aNear)/(aFar-aNear); 
  m[3][2] = -(2*aFar*aNear)/(aFar-aNear);
  m[0][3] = 0.0;      
  m[1][3] = 0.0;      
  m[2][3] = -1.0;      
  m[3][3] = 0.0;
}
void setInvFrustum(double left,double right,
                           double bottom, double top,
                           double aNear, double aFar)
{
  m[0][0] = (right-left)/(2*aNear); 
  m[1][0] = 0.0; 
  m[2][0] = 0.0; 
  m[3][0] = (right+left)/(2*aNear);
  m[0][1] = 0.0; 
  m[1][1] = (top-bottom)/(2*aNear); 
  m[2][1] = 0.0; 
  m[3][1] = (top+bottom)/(2*aNear);
  m[0][2] = 0.0; 
  m[1][2] = 0.0; 
  m[2][2] = 0.0; 
  m[3][2] = -1.0;
  m[0][3] = 0.0;      
  m[1][3] = 0.0;      
  m[2][3] = -(aFar-aNear)/(2*aFar*aNear);      
  m[3][3] = (aFar+aNear)/(2*aFar*aNear);
}
void setInvOrtho(double left,double right,
                         double bottom, double top,
                         double aNear, double aFar)
{
  m[0][0] = (right-left)/2; 
  m[1][0] = 0.0; 
  m[2][0] = 0.0; 
  m[3][0] = (right+left)/2;
  m[0][1] = 0.0; 
  m[1][1] = (top-bottom)/2; 
  m[2][1] = 0.0; 
  m[3][1] = (top+bottom)/2;
  m[0][2] = 0.0; 
  m[1][2] = 0.0; 
  m[2][2] = -(aFar-aNear)/2; 
  m[3][2] = (aFar+aNear)/2;
  m[0][3] = 0.0;      
  m[1][3] = 0.0;      
  m[2][3] = 0.0;      
  m[3][3] = 1.0;
}
void setOrtho(double left,double right,
                      double bottom, double top,
                      double aNear, double aFar)
{
  m[0][0] = 2/(right-left); 
  m[1][0] = 0.0; 
  m[2][0] = 0.0; 
  m[3][0] = -(right+left)/(right-left);
  m[0][1] = 0.0; 
  m[1][1] = 2/(top-bottom); 
  m[2][1] = 0.0; 
  m[3][1] = -(top+bottom)/(top-bottom);
  m[0][2] = 0.0; 
  m[1][2] = 0.0; 
  m[2][2] = -2/(aFar-aNear); 
  m[3][2] = -(aFar+aNear)/(aFar-aNear);
  m[0][3] = 0.0;      
  m[1][3] = 0.0;      
  m[2][3] = 0.0;      
  m[3][3] = 1.0;
}
public void setRotate(Vect3d axis, double angle)
{
Matrix3d mat = new Matrix3d();
Vect3d vect = new Vect3d();
  identity();
  mat.setEulerRotation(axis,angle);
  vect.zero();
  set(mat,vect);
}
public void setRow(Vect3d v1, Vect3d v2, Vect3d v3, Vect3d v4)
{
  m[0][0]=v1.q[0];       m[1][0]=v1.q[1];       m[2][0]=v1.q[2];       m[3][0]=0;
  m[0][1]=v2.q[0];       m[1][1]=v2.q[1];       m[2][1]=v2.q[2];       m[3][1]=0;
  m[0][2]=v3.q[0];       m[1][2]=v3.q[1];       m[2][2]=v3.q[2];       m[3][2]=0;
  m[0][3]=v4.q[0];       m[1][3]=v4.q[1];       m[2][3]=v4.q[2];       m[3][3]=1;
}
public void setScale(Vect3d s)
{
  identity(); 
  m[0][0] = s.q[0];      
  m[1][1] = s.q[1];      
  m[2][2] = s.q[2];      
}
public void setTranslate(Vect3d v)
{
  identity(); 
  m[3][0] = v.q[0];      
  m[3][1] = v.q[1];      
  m[3][2] = v.q[2];      
}
public void show()
{
System.out.println("A = "+m[0][0]+"  "+m[1][0]+"  "+m[2][0]+"  "+m[3][0]);
System.out.println("    "+m[0][1]+"  "+m[1][1]+"  "+m[2][1]+"  "+m[3][1]);
System.out.println("    "+m[0][2]+"  "+m[1][2]+"  "+m[2][2]+"  "+m[3][2]);
System.out.println("    "+m[0][3]+"  "+m[1][3]+"  "+m[2][3]+"  "+m[3][3]);
}
/* c = a - b            */
public void sub(Affine b)
{        
 for (int col=0;col<4;col++){
   for (int row=0;row<4;row++){
     m[col][row] -= b.m[col][row];
     }
   }
}
public String toString()
{
	return getClass().getName() + "@" + Integer.toHexString(hashCode())+" = \n"+
		   "    "+m[0][0]+"  "+m[1][0]+"  "+m[2][0]+"  "+m[3][0]+"\n"+
		   "    "+m[0][1]+"  "+m[1][1]+"  "+m[2][1]+"  "+m[3][1]+"\n"+
		   "    "+m[0][2]+"  "+m[1][2]+"  "+m[2][2]+"  "+m[3][2]+"\n"+
		   "    "+m[0][3]+"  "+m[1][3]+"  "+m[2][3]+"  "+m[3][3];
}
public void transpose()
{
double temp;
  temp=m[0][1]; m[0][1]=m[1][0]; m[1][0]=temp;
  temp=m[0][2]; m[0][2]=m[2][0]; m[2][0]=temp;
  temp=m[0][3]; m[0][3]=m[3][0]; m[3][0]=temp;
  temp=m[1][2]; m[1][2]=m[2][1]; m[2][1]=temp;
  temp=m[1][3]; m[1][3]=m[3][1]; m[3][1]=temp;
  temp=m[2][3]; m[2][3]=m[3][2]; m[3][2]=temp;
}
public void zero()
{
	for (int i=0;i<4;i++){
		for (int j=0;j<4;j++){
			m[i][j] = 0.0;
		}
	}
}
}

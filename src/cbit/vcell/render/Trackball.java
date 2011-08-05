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
 * Creation date: (11/29/2003 11:23:03 AM)
 * @author: Jim Schaff
 */

public class Trackball {
	
	public final static double TRACKBALLSIZE = 0.8;
	private final double M_SQRT1_2 = Math.pow(0.5,0.5);  // sqrt(1/2)
	private final double M_SQRT2   = Math.pow(2,0.5);    // sqrt(2)

	
    private Quaternion currQuat = new Quaternion();
    private Quaternion currInvQuat = new Quaternion();
    private Vect3d     displacement = new Vect3d();
    private Vect3d     center = new Vect3d();
    private Vect3d     Size = new Vect3d();
    private double     scale = 1.0;

	private Affine forwardAffine = new Affine();
	private Affine inverseAffine = new Affine();

     
    private boolean    bAnimate;
    private Quaternion incQuat = new Quaternion();
    private Quaternion incInvQuat = new Quaternion();
    private double     sizeTrackball;
	private class TrackballState {
		private Quaternion quat;
		private Quaternion iquat;
		private Vect3d displacement;
	};
    private Camera    camera = null;
	private static final int MAX_STATES = 50;
	
    private TrackballState trackballState[] = new TrackballState[MAX_STATES];
    private int       numStates;
    private int       currState;

public Trackball(Camera cam) {
    camera = cam;
    bAnimate = false;
    currQuat.zero();
    incQuat.zero();
    currInvQuat.zero();
    incInvQuat.zero();
    displacement.zero();
    center.zero();
    Size.set(2.0, 2.0, 2.0);
    sizeTrackball = TRACKBALLSIZE;
    numStates = currState = 0;

}


public Vect3d calculateNewDisplacement_pan_xy(Vect3d oldTranslation, double p1x, double p1y, double p2x, double p2y) {
    return new Vect3d(oldTranslation.getX() + (p2x - p1x), oldTranslation.getY() + (p2y - p1y), oldTranslation.getZ());
}


public double calculateNewScale_zoom_z(double oldScale, double p1y, double p2y) {
	if (oldScale<=0.0){
		throw new IllegalArgumentException("scale ("+oldScale+") should be positive");
	}
	if (getCamera().getProjectMode().isParallel()){
		//
		// just change scale don't translate 
		//
		double log_of_scale = Math.log(oldScale);
		log_of_scale += (p1y-p2y);
		return Math.exp(log_of_scale);
	}
	return oldScale;
}


public void erase()
{
System.out.println("trackball::erase()\n");
   numStates=0;
   currState=0;
}


/**
 * Insert the method's description here.
 * Creation date: (11/30/2003 12:53:22 PM)
 * @return cbit.vcell.render.Camera
 */
public Camera getCamera() {
	return camera;
}


public Vect3d getCenter() {
    return center;
}


public void getInvMatrixGL(Affine mat) {
    currInvQuat.loadAffine(mat);
}


public Matrix3d getMatrix() {
    return currQuat.getMatrix();
}


public void getMatrixGL(Affine mat) {
    currQuat.loadAffine(mat);
}


/**
 * Insert the method's description here.
 * Creation date: (9/23/2005 2:14:18 PM)
 * @return cbit.vcell.render.Vect3d
 */
public Vect3d getRotation() {

	//
	// from http://www.mathworks.com/access/helpdesk/help/toolbox/aeroblks/quaternionstoeulerangles.html
	//
	Quaternion quat = new Quaternion(currQuat);
	quat.normalize();
	double q0 = quat.getScalar();
	double q1 = quat.getVector().getX();
	double q2 = quat.getVector().getY();
	double q3 = quat.getVector().getZ();

	double phi = Math.atan2(2*(q2*q3 + q0*q1), (q0*q0-q1*q1-q2*q2+q3*q3));
	double theta = Math.asin(-2*(q1*q3-q0*q2));
	double psi = Math.atan2(2*(q1*q2+q0*q3), (q0*q0+q1*q1-q2*q2-q3*q3));
	
	Vect3d angles = new Vect3d(phi,theta,psi);

	return angles;
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/2005 10:45:56 AM)
 * @return double
 */
public double getScale() {
	return scale;
}


public Vect3d getSize() {
    return Size;
}


public Vect3d getTranslation() {
    return displacement;
}


public void pan_xy(double p1x, double p1y, double p2x, double p2y) {
    displacement = calculateNewDisplacement_pan_xy(displacement,p1x,p1y,p2x,p2y);
    updateCamera();
}


public void pan_yz(double p1x,double p1y,double p2x,double p2y)
{
	Vect3d incremental = new Vect3d(0.0, -(p1x-p2x), (p1y-p2y));
	displacement.add(incremental);
	updateCamera();
}

public boolean play()
{         
System.out.println("play() ... numStates="+numStates+", currState="+currState);
   if (numStates==0) return false;          // empty tape
   if (currState>=numStates) return false;  // past end of tape

   currQuat.set(trackballState[currState].quat);
   currInvQuat.set(trackballState[currState].iquat);
   displacement.set(trackballState[currState].displacement);

   updateCamera();
   currState++;
   return true;
}


//
// Project an xy pair onto a sphere of radius r OR a hyperbolic sheet
// if we are away from the center of the sphere.
//
private Vect3d projectToSphere_xy(double x, double y)
{
double radius = sizeTrackball;
double distance_xy, t, z;

   distance_xy = Math.sqrt(x*x + y*y);

   if (distance_xy < radius*M_SQRT1_2){  	/* Inside sphere */
      //
      // x^2 + y^2 + z^2 = r^2
      // distance_xy^2 + z^2 = r^2
      // z^2 = r^2 - distance_xy^2
      //
      z = Math.sqrt(radius*radius - distance_xy*distance_xy);
//System.out.println("Trackball.projectToSphere("+x+","+y+") 'sphere' --> z = "+z);
	}else { 					/* On hyperbola */
      t = radius / M_SQRT2;
      z = t*t / distance_xy;
//System.out.println("Trackball.projectToSphere("+x+","+y+") 'hyperbola' --> z = "+z);
   }
//   vect.set(x,y,-z);
   return new Vect3d(x,y,-z);
}


//
// Project an yz pair onto a sphere of radius r OR a hyperbolic sheet
// if we are away from the center of the sphere.
//
private Vect3d projectToSphere_yz(double y, double z)
{
double radius = sizeTrackball;
double distance_yz, t, x;


   distance_yz = Math.sqrt(y*y + z*z);

   if (distance_yz < radius*M_SQRT1_2)  	/* Inside sphere */
      //
      // x^2 + y^2 + z^2 = r^2
      // distance_yz^2 + x^2 = r^2
      // x^2 = r^2 - distance_yz^2
      //
      x = Math.sqrt(radius*radius - distance_yz*distance_yz);
   else { 					/* On hyperbola */
      t = radius / M_SQRT2;
      x = t*t / distance_yz;
   }
   return new Vect3d(x,y,z);
}

public boolean record()
{
   if (numStates>=MAX_STATES) return false;  // past end of tape
   trackballState[numStates].quat.set(currQuat);
   trackballState[numStates].iquat.set(currInvQuat);
   trackballState[numStates].displacement.set(displacement);
   numStates++;
   return true;
}


public void rewind()
{
System.out.println("trackball::rewind()\n");
   currState=0;
}


//
//  assume screen is in xy plane
//
public boolean rotate_xy(double p1x, double p1y, double p2x, double p2y)
{
   if (p1x == p2x && p1y == p2y){
      incQuat.zero(); /* Zero rotation */
      return false;
   }

   //
   // First, figure out z-coordinates for projection of P1 and P2 to
   // deformed sphere
   //
   Vect3d p1 = projectToSphere_xy(p1x,p1y);
   Vect3d p2 = projectToSphere_xy(p2x,p2y);
   //
   // Now, we want the cross product of P1 and P2
   //
//   Vect3d axis = p1.cross(p2);  // axis of rotation
   Vect3d axis = p2.cross(p1);  // axis of rotation
   Affine affine = new Affine();
   getInvMatrixGL(affine);
   axis = Affine.mult(affine,axis);
   axis.unit();

   //
   // Figure out how much to rotate around that axis.
   //
   Vect3d d = new Vect3d(p1); 
   d.sub(p2);
   double t = d.length() / (2.0 * sizeTrackball);

   //
   // Avoid problems with out-of-control values...
   //
   if (t > 1.0) 
      t = 1.0;
   if (t < -1.0) 
      t = -1.0;
   double phi = 2.0 * Math.asin(t);  // how much to rotate about axis
//System.out.println("Trackball.rotate_xy() axis = ("+axis.getX()+", "+axis.getY()+", "+axis.getZ()+"),  phi = "+phi);
   incQuat.setAxis(axis,-phi);
   incInvQuat.setAxis(axis,phi);

   currQuat = Quaternion.mult(currQuat,incQuat);
   currInvQuat = Quaternion.mult(incInvQuat,currInvQuat);
   updateCamera();
   return true;
}

// Trackball Rotation Function
//
// Project the normalized mouse points onto the virtual trackball, 
// then figure out the axis of rotation, which is the cross
// product of P1 P2 and O P1 (O is the center of the ball, 0,0,0)
// Note:  This is a deformed trackball-- is a trackball in the center,
// but is deformed into a hyperbolic sheet of rotation away from the
// center.  
// 
// It is assumed that the arguments to this routine are in the range
// (-1.0 ... 1.0)
//

//
//  assume screen is in yz plane
//
public boolean rotate_yz(double p1y, double p1z, double p2y, double p2z)
{
Vect3d axis;	/* Axis of rotation */
double phi;	/* how much to rotate about axis */
Vect3d p1, p2, d;
double t;

   if (p1y == p2y && p1z == p2z){
      incQuat.zero(); /* Zero rotation */
      return false;
   }

   //
   // First, figure out x-coordinates for projection of P1 and P2 to
   // deformed sphere
   //
   p1 = projectToSphere_yz(p1y,p1z);
   p2 = projectToSphere_yz(p2y,p2z);

   //
   // Now, we want the cross product of P1 and P2
   //
   axis = p2.cross(p1);
   axis.unit();

   //
   // Figure out how much to rotate around that axis.
   //
   d = new Vect3d(p1);
   d.sub(p2);
   t = d.length() / (2.0 * sizeTrackball);

   //
   // Avoid problems with out-of-control values...
   //
   if (t > 1.0) 
      t = 1.0;
   if (t < -1.0) 
      t = -1.0;
   phi = 2.0 * Math.asin(t);

   incQuat.setAxis(axis,phi);
   incInvQuat.setAxis(axis,-phi);

   currQuat = Quaternion.mult(currQuat,incQuat);
   currInvQuat = Quaternion.mult(incInvQuat,currInvQuat);
   updateCamera();
   return true;
}

public void scaleToFit(Vect3d size) {
	throw new RuntimeException("not yet implemented");
}


public void setCenter(Vect3d Center) {
    center = Center;
    updateCamera();
}


/**
 * Insert the method's description here.
 * Creation date: (9/22/2005 4:02:09 PM)
 * @param angleRadX double
 * @param angleRadY double
 * @param angleRadZ double
 */
public void setRotation(double angleRadX, double angleRadY, double angleRadZ) {
	cbit.vcell.render.Vect3d rotationAngles = new cbit.vcell.render.Vect3d(angleRadX,angleRadY,angleRadZ);

	cbit.vcell.render.Matrix3d rotationMatrix = new cbit.vcell.render.Matrix3d();
	rotationMatrix.setCosineRotation(rotationAngles);

	cbit.vcell.render.Matrix3d invRotationMatrix = new cbit.vcell.render.Matrix3d();
	invRotationMatrix.setInvCosineRotation(rotationAngles);

	currQuat.setMatrix(rotationMatrix);
	currInvQuat.setMatrix(invRotationMatrix);
	incQuat.zero();
	incInvQuat.zero();

	updateCamera();
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/2005 11:03:34 AM)
 * @param scale double
 */
public void setScale(double argScale) {
	scale = argScale;
	updateCamera();
}


public void setSize(Vect3d size) {
    Size = size;
    updateCamera();
}


public void setTranslation(Vect3d disp) {
    displacement = disp;
    updateCamera();
}


//
//  spins trackball by angular velocity of last rotate
//
public void spin() {
   currQuat = Quaternion.mult(currQuat,incQuat);
   currInvQuat = Quaternion.mult(incInvQuat,currInvQuat);
   updateCamera();
}

private void updateCamera() {

   camera.resetView();
   if (camera.getProjectMode().isPerspective()){
      camera.translateView(Vect3d.sub(displacement,new Vect3d(0.0, 0.0, 10.0)));
   }else{      
      camera.translateView(displacement);
   }
   
   getMatrixGL(forwardAffine);
   getInvMatrixGL(inverseAffine);
   camera.multView(forwardAffine,inverseAffine);
   
   double scaleFactor = 2.0/Math.max(Size.q[0], Math.max(Size.q[1], Size.q[2]));
   
   scaleFactor *= this.scale;

   camera.scaleView(new Vect3d(scaleFactor,scaleFactor,scaleFactor));
   
   camera.translateView(center.uminus());
}

public void zoom_x(double p1y, double p2y) {
	if (getCamera().getProjectMode().isParallel()){
		//
		// just change scale don't translate 
		//
		double log_of_scale = Math.log(this.scale);
		log_of_scale += (p1y-p2y);
		this.scale = Math.exp(log_of_scale);
	}else if (getCamera().getProjectMode().isPerspective()){
		//
		// don't scale, just translate
		//
	    Vect3d incremental = new Vect3d(p1y - p2y, 0.0, 0.0);
		displacement.add(incremental);
	}
}


public void zoom_z(double p1y, double p2y) {
	if (getCamera().getProjectMode().isParallel()){
		//
		// just change scale don't translate 
		//
		this.scale = calculateNewScale_zoom_z(this.scale,p1y,p2y);
	}else if (getCamera().getProjectMode().isPerspective()){
		//
		// don't scale, just translate
		//
	    Vect3d incremental = new Vect3d(0.0, 0.0, p1y - p2y);
		displacement.add(incremental);
	}
    updateCamera();
}
}

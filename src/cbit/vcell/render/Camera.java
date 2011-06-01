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
 * Creation date: (11/29/2003 1:54:54 PM)
 * @author: Jim Schaff
 */
public class Camera {
	
	private final int MAX_STACK_SIZE = 20;

	private double _left;
	private double _right;
	private double _bottom;
	private double _top;
	private double _nearOrtho;
	private double _farOrtho;
	private double _nearFrustum;
	private double _farFrustum;
	private ProjectMode projectMode;

    private MatrixState stack[] = new MatrixState[MAX_STACK_SIZE];
    private int tos;

    private Affine _Projection = new Affine();
    private Affine _InvProjection = new Affine();
    private Affine _View = new Affine();			// should equal Top Of Model/View Stack
    private Affine _InvView = new Affine();			// should equal Top Of Model/View Stack
    private Affine _ProjView = new Affine();		// combined forward matrix
    private Affine _InvProjView = new Affine();		// combined inverse matrix

    
    private class MatrixState {
		Affine mat = new Affine();
		Affine imat = new Affine();
	}

    
    public static class ProjectMode {
	    private int mode = -1;
	    private String name = null;
	    public static final ProjectMode PROJECT_PARALLEL = new ProjectMode(1,"parallel");
	    public static final ProjectMode PROJECT_PERSPECTIVE = new ProjectMode(2,"perspective");
	    private ProjectMode(int argMode, String argName){
		    mode = argMode;
		    name = argName;
	    }
	    public String toString(){
		    return name;
	    }
	    public boolean equals(Object obj){
		    if (obj instanceof Camera.ProjectMode){
			    Camera.ProjectMode projMode = (Camera.ProjectMode)obj;
			    if (projMode.mode == mode){
				    return true;
			    }
		    }
		    return false;
	    }
	    public int hashcode() {
		    return mode;
	    }
	    public boolean isParallel(){
		    return mode == PROJECT_PARALLEL.mode;
	    }
	    public boolean isPerspective(){
		    return mode == PROJECT_PERSPECTIVE.mode;
	    }
    }


/**
 * Camera constructor comment.
 */
public Camera() {
   _left   =-10.0;
   _right  = 10.0;
   _bottom =-10.0;
   _top    = 10.0;
   _nearFrustum = 10.0;
   _farFrustum  = 100.0;
   _nearOrtho   = 1000.0;
   _farOrtho    = -1000.0;
   _View.identity();
   _InvView.identity();
   _Projection.identity();
   _InvProjection.identity();
   _ProjView.identity();
   _InvProjView.identity();
   setProjectMode(ProjectMode.PROJECT_PARALLEL);
   updateCombined();
   tos=-1;
   for (int i=0;i<MAX_STACK_SIZE;i++){
	   stack[i] = new MatrixState();
      stack[i].mat.identity();
      stack[i].imat.identity();
   }
}
public Affine getInvProjView() {
    return _InvProjView;
}
public ProjectMode getProjectMode() {
    return projectMode;
}
public Affine getProjView() {
    return _ProjView;
}
public void multView(Affine  view, Affine  iview)
{
   _View.mult(view);
   Affine tmp = new Affine(iview);
   tmp.mult(_InvView);
   _InvView.set(tmp);
   updateCombined();
}
public void popModelXform()
{
   //assert(tos>=0);
   _View = stack[tos].mat;
   _InvView = stack[tos].imat;
   tos--;
   updateCombined();
}
//-------------------------------------------------------------
// output projected vectors on normalized view_plane
// based on camera parameters.
//
//  returns TRUE if object can be rendered.
//-------------------------------------------------------------
public boolean projectLine(Vect3d Vect1,Vect3d Vect2,
                         Vect3d Proj1,Vect3d Proj2)
{                   
final int INFRONT = 0x01;
final int BEHIND = 0x02;
final int TOLEFT = 0x04;
final int TORIGHT = 0x08;
final int ABOVE = 0x10;
final int BELOW = 0x20;

    //
    // put into eye coordinates/and then clip Coordinates
    //
    Vect3d cam1 = Affine.mult(_ProjView,Vect1);
    Vect3d cam2 = Affine.mult(_ProjView,Vect2);

   //
   // clip in Z (near,far)
   //
   int clip1=0; int clip2=0;
   if (cam1.q[0] < -1.0) clip1 |= TOLEFT;
   if (cam1.q[0] > 1.0) clip1 |= TORIGHT;
   if (cam1.q[1] < -1.0) clip1 |= BELOW;
   if (cam1.q[1] > 1.0) clip1 |= ABOVE;
   if (cam1.q[2] < -1.0) clip1 |= INFRONT;
   if (cam1.q[2] > 1.0) clip1 |= BEHIND;
   if (cam2.q[0] < -1.0) clip2 |= TOLEFT;
   if (cam2.q[0] > 1.0) clip2 |= TORIGHT;
   if (cam2.q[1] < -1.0) clip2 |= BELOW;
   if (cam2.q[1] > 1.0) clip2 |= ABOVE;
   if (cam2.q[2] < -1.0) clip2 |= INFRONT;
   if (cam2.q[2] > 1.0) clip2 |= BEHIND;

   //
   // check if totally outside clipping volume
   //
   if ((clip1 & clip2) != 0) 
      return false;

   //
   // check if totally within Z range (X and Y are clipped in 2D)
   //
   if (clip1==0 && clip2==0){
      Proj1.set(cam1);
      Proj2.set(cam2);
      return true;
   }

   //
   // if cam1 outside volume, clip to appropriate Z-plane
   //
   if (clip1==INFRONT){
      //
      // find intersection with near plane
      //
      Vect3d diff = new Vect3d(cam2);
      diff.sub(cam1);
      double t = (-1.0 - cam1.q[2])/diff.q[2];
      diff.scale(t);
      cam1.add(diff);
      clip1=0;
   }else{
      if (clip1==BEHIND){
      //
      // find intersection with far plane
      //
      Vect3d diff = new Vect3d(cam2);
      diff.sub(cam1);
      double t = (1.0 - cam1.q[2])/diff.q[2];
      diff.scale(t);
      cam1.add(diff);
      clip1=0;
      }
   }
   //
   // if cam2 outside volume, clip to appropriate plane
   //
   if (clip2==INFRONT){
      //
      // find intersection with near plane
      //
      Vect3d diff = new Vect3d(cam1);
      diff.sub(cam2);
      double t = (-1.0 - cam2.q[2])/diff.q[2];
      diff.scale(t);
      cam2.add(diff);
      clip2=0;
   }else{
      if (clip2==BEHIND){
      //
      // find intersection with far plane
      //
      Vect3d diff = new Vect3d(cam1);
      diff.sub(cam2);
      double t = (1.0 - cam2.q[2])/diff.q[2];
      diff.scale(t);
      cam2.add(diff);
      clip2=0;
      }
   }
   
   //
   // cam1 and cam2 are clipped in Z
   //
   Proj1.set(cam1);
   Proj2.set(cam2);
   return true;
}
//-------------------------------------------------------------
// output projected vectors on normalized view_plane
// based on camera parameters.
//
//  returns TRUE if object can be rendered.
//-------------------------------------------------------------
public boolean projectPoint(Vect3d Vect1,Vect3d Proj1)
{                   
final int INFRONT = 0x01;
final int BEHIND = 0x02;
final int TOLEFT = 0x04;
final int TORIGHT = 0x08;
final int ABOVE = 0x10;
final int BELOW = 0x20;

    //
    // put into eye coordinates/and then clip Coordinates
    //
    Vect3d cam1 = Affine.mult(_ProjView,Vect1);

   //
   // clip in Z (near,far)
   //
   int clip1=0;
   if (cam1.q[0] < -1.0) clip1 |= TOLEFT;
   if (cam1.q[0] > 1.0) clip1 |= TORIGHT;
   if (cam1.q[1] < -1.0) clip1 |= BELOW;
   if (cam1.q[1] > 1.0) clip1 |= ABOVE;
   if (cam1.q[2] < -1.0) clip1 |= INFRONT;
   if (cam1.q[2] > 1.0) clip1 |= BEHIND;

   //
   // check if outside clipping volume
   //
   if (clip1!=0) {
      return false;
   }

   Proj1.set(cam1);
   return true;
}
public void pushModelXform()
{
   tos++;
   //assert(tos<MAX_STACK_SIZE);
   stack[tos].mat = _View;
   stack[tos].imat = _InvView;
}
public void resetView()
{
   _View.identity();
   _InvView.identity();
   updateCombined();
}
public void rotateView(Vect3d axis, double angle)
{
Affine tempAffine = new Affine();

   // get rotation
   // note: that inv(A) = transpose(A)
   tempAffine.setRotate(axis,angle);
   _View.mult(tempAffine);
   tempAffine.transpose();
   tempAffine.mult(_InvView);
   _InvView.set(tempAffine);
   updateCombined();
}
public void scaleView(Vect3d scale)
{
Affine tempAffine = new Affine();

   tempAffine.setScale(scale);
   _View.mult(tempAffine);
   tempAffine.setScale(new Vect3d(1.0/scale.q[0],1.0/scale.q[1],1.0/scale.q[2]));
   tempAffine.mult(_InvView);
   _InvView.set(tempAffine);
   updateCombined();
}
public void setFrustum(double left,double right,
                        double bottom, double top,
                        double aNear, double aFar)
{
   if (aNear==aFar) throw new IllegalArgumentException("Camera.setFrustum(): aNear == aFar");
   if (right==left) throw new IllegalArgumentException("Camera.setFrustum(): right == left");
   if (top==bottom) throw new IllegalArgumentException("Camera.setFrustum(): top == bottom");
   _left=left;
   _right=right;
   _top=top;
   _bottom=bottom;
   _nearFrustum=aNear;
   _farFrustum=aFar;
   _Projection.setFrustum(left,right,bottom,top,aNear,aFar);
   _InvProjection.setInvFrustum(left,right,bottom,top,aNear,aFar);
   updateCombined();
   projectMode = ProjectMode.PROJECT_PERSPECTIVE;
}
public void setOrtho(double left,double right,
                      double bottom, double top,
                      double aNear, double aFar)
{
   if (aNear==aFar) throw new IllegalArgumentException("Camera.setOrtho(): aNear == aFar");
   if (right==left) throw new IllegalArgumentException("Camera.setOrtho(): right == left");
   if (top==bottom) throw new IllegalArgumentException("Camera.setOrtho(): top == bottom");
   _left=left;
   _right=right;
   _top=top;
   _bottom=bottom;
   _nearOrtho=aNear;
   _farOrtho=aFar;
   _Projection.setOrtho(left,right,bottom,top,aNear,aFar);
   _InvProjection.setInvOrtho(left,right,bottom,top,aNear,aFar);
   updateCombined();
   projectMode = ProjectMode.PROJECT_PARALLEL;
}
public void setProjectMode(ProjectMode mode)
{
	if (mode.equals(ProjectMode.PROJECT_PARALLEL)) {
       setOrtho(_left,_right,_bottom,_top,_nearOrtho,_farOrtho);
	}else if (mode.equals(ProjectMode.PROJECT_PERSPECTIVE)) {
       setFrustum(_left,_right,_bottom,_top,_nearFrustum,_farFrustum);
	}else{
       throw new RuntimeException("ProjectMode "+mode+" not expected");
	}
	projectMode = mode;
}
public void setView(Affine view, Affine iview)
{
   _View.set(view);
   _InvView.set(iview);
   updateCombined();
}
public void translateView(Vect3d v)
{
Affine tempAffine = new Affine();

   tempAffine.setTranslate(v);
   _View.mult(tempAffine);
   tempAffine.setTranslate(v.uminus());
   tempAffine.mult(_InvView);
   _InvView.set(tempAffine);
   updateCombined();
}
//-------------------------------------------------------------
// output Scene vector based on normalized view_plane point
// based on camera parameters
//
//-------------------------------------------------------------
public void unProjectPoint(Vect3d Proj,Vect3d Vect)
{                   
	//
	// put into eye coordinates/and then clip Coordinates
	//
	Vect.set(Affine.mult(_InvProjView,Proj));

}
public void updateCombined()
{                   
   _ProjView.set(_Projection);
   _ProjView.mult(_View);
   _InvProjView.set(_InvView);
   _InvProjView.mult(_InvProjection);
}
}

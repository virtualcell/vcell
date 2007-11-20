package org.vcell.render;

import javax.media.opengl.GL;

import org.vcell.spatial.Vect3d;


public class GLCamera extends Camera {
	
	private GL gl = null;
	
	
	public GLCamera(GL argGL){
		super();
		this.gl = argGL;
	}

	public void rotateView(Vect3d axis, double angle)
	{
	   gl.glMatrixMode(GL.GL_MODELVIEW);
	   gl.glRotated(angle,axis.q[0],axis.q[1],axis.q[2]);
	   super.rotateView(axis,angle);
	}

	public void scaleView(Vect3d scale)
	{
	   gl.glMatrixMode(GL.GL_MODELVIEW);
	   gl.glScaled(scale.q[0],scale.q[1],scale.q[2]);
	   super.scaleView(scale);
	}

	public void setFrustum(double left,double right, double bottom, double top, double near, double far)
	{
	   gl.glMatrixMode(GL.GL_PROJECTION);
	   gl.glLoadIdentity();
	   gl.glFrustum(left,right,bottom,top,near,far);
	   super.setFrustum(left,right,bottom,top,near,far);
	}

	public void setOrtho(double left,double right, double bottom, double top,  double near, double far)
	{
	   gl.glMatrixMode(GL.GL_PROJECTION);
	   gl.glLoadIdentity();
	   gl.glOrtho(left,right,bottom,top,near,far);
	   super.setOrtho(left,right,bottom,top,near,far);
	}

	public void translateView(Vect3d v)
	{
	   gl.glMatrixMode(GL.GL_MODELVIEW);
	   gl.glTranslated(v.q[0],v.q[1],v.q[2]);
	   super.translateView(v);
	}

	public void setView(Affine view, Affine iview)
	{
	   gl.glMatrixMode(GL.GL_MODELVIEW);
	   gl.glLoadMatrixd(view.getGLMatrix(),0);
	   super.setView(view,iview);
	}

	public void multView(Affine view, Affine iview)
	{
	   gl.glMatrixMode(GL.GL_MODELVIEW);
	   gl.glMultMatrixd(view.getGLMatrix(),0);
	   super.multView(view,iview);
	}

	public void popModelXform()
	{
	   super.popModelXform();
	   gl.glMatrixMode(GL.GL_MODELVIEW);
	   gl.glPopMatrix();      
	}

	public void pushModelXform()
	{
	   super.pushModelXform();
	   gl.glMatrixMode(GL.GL_MODELVIEW);
	   gl.glPushMatrix();      
	}

	public void resetView()
	{
	   gl.glMatrixMode(GL.GL_MODELVIEW);
	   gl.glLoadIdentity();
	   super.resetView();
	}


}

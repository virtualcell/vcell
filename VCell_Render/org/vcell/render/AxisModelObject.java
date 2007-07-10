package org.vcell.render;

import javax.media.opengl.GL;

import org.vcell.util.Extent;

import cbit.render.objects.BoundingBox;
import cbit.render.objects.Vect3d;

import com.sun.opengl.util.GLUT;

public class AxisModelObject extends ModelObject {
	private Vect3d size = null;
	
	public AxisModelObject(Vect3d argSize){
		size = argSize;
		boundingBox = new BoundingBox(0,size.getX(),0,size.getY(),0,size.getZ());
	}
	
	public void draw0(GL gl) {
		GLUT glut = new GLUT();
    	double axisScale = Math.min(Math.sqrt(size.length()),size.length()/50.0);
    	gl.glLineWidth(2.0f);
    	
	    gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, new float[] { 0.2f, 0.2f, 0.2f, 1.0f }, 0);
		gl.glPushMatrix();
		gl.glScaled(axisScale,axisScale,axisScale);
		glut.glutSolidCube(1f);
		gl.glPopMatrix();
		
		//
		// X-axis
		//
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, new float[] { 1.0f, 0.0f, 0.0f, 1.0f }, 0);
		gl.glColor4fv(new float[] { 1.8f, 0.2f, 0.2f, 1.0f }, 0);
		gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(0f,0f,0f);
			gl.glVertex3d(size.getX(),0f,0f);
		gl.glEnd();
		gl.glPushMatrix();
			gl.glTranslated(size.getX(), 0, 0);
			gl.glScaled(axisScale,axisScale,axisScale);
			gl.glRotated(90,0,1,0);
			glut.glutSolidCone(1,2,20,20);
		gl.glPopMatrix();
		
		//
		// Y-axis
		//
	    gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, new float[] { 0.0f, 1.0f, 0.0f, 1.0f }, 0);
	    gl.glColor4fv(new float[] { 0.2f, 1.8f, 0.2f, 1.0f }, 0);
		gl.glBegin(GL.GL_LINES);
			gl.glVertex3d(0,0,0);
			gl.glVertex3d(0,size.getY(),0);
		gl.glEnd();
		gl.glPushMatrix();
			gl.glTranslated(0, size.getY(), 0);
			gl.glScaled(axisScale,axisScale,axisScale);
			gl.glRotated(90,-1,0,0);
			glut.glutSolidCone(1,2,20,20);
		gl.glPopMatrix();
		
		//
		// Z-axis
		//
	    gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, new float[] { 0.0f, 0.0f, 1.0f, 1.0f }, 0);
	    gl.glColor4fv(new float[] { 0.2f, 0.2f, 1.8f, 1.0f }, 0);
		gl.glBegin(GL.GL_LINES);
			gl.glVertex3d(0,0,0);
			gl.glVertex3d(0,0,size.getZ());
		gl.glEnd();
	    gl.glPushMatrix();
		gl.glTranslated(0, 0, size.getZ());
		gl.glScaled(axisScale,axisScale,axisScale);
	    glut.glutSolidCone(1,2,20,20);
		gl.glPopMatrix();
	}
}

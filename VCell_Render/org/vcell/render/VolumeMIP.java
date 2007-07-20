package org.vcell.render;


import java.nio.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

import cbit.render.objects.BoundingBox;
import cbit.render.objects.ByteImage;
import cbit.render.objects.ImageException;
import cbit.render.objects.Vect3d;



/**
 * This program uses evaluators to generate a curved surface and automatically
 * generated texture coordinates.
 * 
 * @author Kiet Le (Java conversion)
 */

public class VolumeMIP extends ModelObject {
	
    private int imageDepth;
    private int imageWidth;
    private int imageHeight;
    private ByteImage redImage = null;
    private ByteImage greenImage = null;
    private ByteImage blueImage = null;
    private ByteBuffer[] imageBufXYZs = null;
    private ByteBuffer[] imageBufYZXs = null;
    private ByteBuffer[] imageBufZXYs = null;
    private int callListID_X=-1;
    private int callListID_Y=-1;
    private int callListID_Z=-1;
    private double projectionMatrix[] = new double[16];
    private double modelviewMatrix[] = new double[16];
    private int viewport[] = new int[4];
    private Vect3d unprojected0 = new Vect3d();
    private Vect3d unprojected1 = new Vect3d();

    //
    public VolumeMIP(ByteImage argRedImage, ByteImage argGreenImage, ByteImage argBlueImage) {
    	this.redImage = argRedImage;
    	this.greenImage = argGreenImage;
    	this.blueImage = argBlueImage;
    	this.imageWidth = redImage.getNumX();
    	this.imageHeight = redImage.getNumY();
    	this.imageDepth = redImage.getNumZ();
    	imageBufXYZs = new ByteBuffer[imageDepth];
    	for (int i = 0; i < imageBufXYZs.length; i++) {
			imageBufXYZs[i] = ByteBuffer.allocateDirect(3*imageWidth*imageHeight);
		}
    	imageBufYZXs = new ByteBuffer[imageWidth];
    	for (int i = 0; i < imageBufYZXs.length; i++) {
			imageBufYZXs[i] = ByteBuffer.allocateDirect(3*imageHeight*imageDepth);
		}
    	imageBufZXYs = new ByteBuffer[imageHeight];
    	for (int i = 0; i < imageBufZXYs.length; i++) {
			imageBufZXYs[i] = ByteBuffer.allocateDirect(3*imageDepth*imageWidth);
		}
    	boundingBox = new BoundingBox(0,redImage.getExtent().getX(),0,redImage.getExtent().getY(),0,redImage.getExtent().getZ());
    	try {
			makeImages();
		} catch (ImageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void makeImages() throws ImageException {
 		byte[] redPixels = redImage.getPixels();
		byte[] greenPixels = greenImage.getPixels();
		byte[] bluePixels = blueImage.getPixels();
        
		for (int k=0;k<imageDepth;k++){
			for (int j=0;j<imageHeight;j++){
		       	for (int i=0;i<imageWidth;i++){
    				int index = redImage.getIndex(i,j,k);
    				imageBufXYZs[k].put(redPixels[index]);
    				imageBufXYZs[k].put(greenPixels[index]);
    				imageBufXYZs[k].put(bluePixels[index]);
            	}
        	}
			imageBufXYZs[k].rewind();
    	}
       
       	for (int i=0;i<imageWidth;i++){
    		for (int k=0;k<imageDepth;k++){
    			for (int j=0;j<imageHeight;j++){
    				int index = redImage.getIndex(i,j,k);
		            imageBufYZXs[i].put(redPixels[index]);
		            imageBufYZXs[i].put(greenPixels[index]);
		            imageBufYZXs[i].put(bluePixels[index]);
            	}
        	}
            imageBufYZXs[i].rewind();
    	}
        
		for (int j=0;j<imageHeight;j++){
			for (int i=0;i<imageWidth;i++){
				for (int k=0;k<imageDepth;k++){
    				int index = redImage.getIndex(i,j,k);
    				imageBufZXYs[j].put(redPixels[index]);
    				imageBufZXYs[j].put(greenPixels[index]);
    				imageBufZXYs[j].put(bluePixels[index]);
            	}
        	}
	        imageBufZXYs[j].rewind();
    	}
        
    }

	public void draw(GL gl){
		GLU glu = new GLU();
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projectionMatrix, 0);
		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelviewMatrix, 0);
		gl.glGetIntegerv(GL.GL_VIEWPORT,viewport,0);
		glu.gluUnProject(0,0,0,modelviewMatrix,0,projectionMatrix,0,viewport,0,unprojected0.q,0);
		glu.gluUnProject(0,0,1,modelviewMatrix,0,projectionMatrix,0,viewport,0,unprojected1.q,0);
		Vect3d eyeVector = Vect3d.sub(unprojected0, unprojected1);
		eyeVector.unit();
		double abs_dotX = Math.abs(eyeVector.q[0]); 
		double abs_dotY = Math.abs(eyeVector.q[1]); 
		double abs_dotZ = Math.abs(eyeVector.q[2]);
		if (abs_dotX >= abs_dotY && abs_dotX >= abs_dotZ){
	    	if (callListID_X<0 || isDirty()){
			    if (callListID_X == -1){
			    	callListID_X = gl.glGenLists(1);
			    }
			    gl.glNewList(callListID_X, GL.GL_COMPILE);
			    drawDirection(gl,0);
			    setDirty(false);
			    gl.glEndList();
			}
			gl.glCallList(callListID_X);
		}else if (abs_dotY >= abs_dotX && abs_dotY >= abs_dotZ){
	    	if (callListID_Y<0 || isDirty()){
			    if (callListID_Y == -1){
			    	callListID_Y = gl.glGenLists(1);
			    }
			    gl.glNewList(callListID_Y, GL.GL_COMPILE);
			    drawDirection(gl,1);
			    setDirty(false);
			    gl.glEndList();
			}
			gl.glCallList(callListID_Y);
		}else if (abs_dotZ >= abs_dotX && abs_dotZ >= abs_dotY){
	    	if (callListID_Z<0 || isDirty()){
			    if (callListID_Z == -1){
			    	callListID_Z = gl.glGenLists(1);
			    }
			    gl.glNewList(callListID_Z, GL.GL_COMPILE);
			    drawDirection(gl,2);
			    setDirty(false);
			    gl.glEndList();
			}
			gl.glCallList(callListID_Z);
			
		}
		drawChildren(gl);
	}
    
    
	public void drawDirection(GL gl, int direction) {
        gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_DECAL);
        int repeat = GL.GL_CLAMP;
        int interpolation = GL.GL_LINEAR;
//      interpolation = GL.GL_NEAREST;
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, repeat);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, repeat);
        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, interpolation);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, interpolation);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glBlendEquation(GL.GL_MAX);
        gl.glEnable(GL.GL_BLEND);
        gl.glShadeModel(GL.GL_FLAT);

		float ex = (float)redImage.getExtent().getX();
		float ey = (float)redImage.getExtent().getY();
		float ez = (float)redImage.getExtent().getZ();

        switch (direction){
        case 0:{
            for (int i=0;i<imageWidth;i++){
                gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, imageHeight, imageDepth, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, imageBufYZXs[i]);
                gl.glBegin(GL.GL_QUADS);
    	    	gl.glTexCoord2f(0f, 0f);	    gl.glVertex3f(i*ex/(imageWidth-1),0f,0f);
    	    	gl.glTexCoord2f(1f, 0f);    	gl.glVertex3f(i*ex/(imageWidth-1),ey,0f);
    	    	gl.glTexCoord2f(1f, 1f);    	gl.glVertex3f(i*ex/(imageWidth-1),ey,ez);
    	    	gl.glTexCoord2f(0f, 1f);	 	gl.glVertex3f(i*ex/(imageWidth-1),0f,ez);
    	        gl.glEnd();
            }
            break;
        }
        case 1:{
	        for (int i=0;i<imageHeight;i++){
	            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, imageDepth, imageWidth, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, imageBufZXYs[i]);
	            gl.glBegin(GL.GL_QUADS);
		    	gl.glTexCoord2f(0f, 0f);	    gl.glVertex3f(0f,i*ey/(imageHeight-1),0f);
		    	gl.glTexCoord2f(1f, 0f);    	gl.glVertex3f(0f,i*ey/(imageHeight-1),ez);
		    	gl.glTexCoord2f(1f, 1f);    	gl.glVertex3f(ex,i*ey/(imageHeight-1),ez);
		    	gl.glTexCoord2f(0f, 1f);	 	gl.glVertex3f(ex,i*ey/(imageHeight-1),0f);
		        gl.glEnd();
	        }
	        break;
        }
        case 2: {
            for (int i=0;i<imageDepth;i++){
                gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, imageWidth, imageHeight, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, imageBufXYZs[i]);
                gl.glBegin(GL.GL_QUADS);
    	    	gl.glTexCoord2f(0f, 0f);	   	gl.glVertex3f(0f,0f,i*ez/(imageDepth-1));
    	    	gl.glTexCoord2f(0f, 1f);   		gl.glVertex3f(0f,ey,i*ez/(imageDepth-1));
    	    	gl.glTexCoord2f(1f, 1f);   		gl.glVertex3f(ex,ey,i*ez/(imageDepth-1));
    	    	gl.glTexCoord2f(1f, 0f);	   	gl.glVertex3f(ex,0f,i*ez/(imageDepth-1));
    	        gl.glEnd();
           }
            break;
        }
        }
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDisable(GL.GL_BLEND);
	}

}

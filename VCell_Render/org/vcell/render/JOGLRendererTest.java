package org.vcell.render;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.media.opengl.*;

import org.vcell.util.ISize;

import cbit.render.objects.AnalyticField;
import cbit.render.objects.BoundingBox;
import cbit.render.objects.ByteImage;
import cbit.render.objects.ByteImageTest;
import cbit.render.objects.SpatialFunction;
import cbit.render.objects.SurfaceCollection;
import cbit.render.objects.SurfaceCollectionTest;
import cbit.render.objects.Vect3d;

/**
 * JGears.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel) <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */

public class JOGLRendererTest extends GLJPanel {
  private static GLCapabilities caps;
  private JOGLRenderer joglRenderer = new JOGLRenderer();

  static {
    caps = new GLCapabilities();
    //caps.setAlphaBits(8);
  }
  
  public JOGLRendererTest() throws Exception {
    super(caps, null, null);
 	    addGLEventListener(joglRenderer);
	    addMouseMotionListener(joglRenderer);
	    addMouseListener(joglRenderer);
   }


   // Helper routine for various demos
   public static void main(String[] args) {
	  try {
	    SurfaceCollection geoSurfaceCollection = SurfaceCollectionTest.getCubeExample();
	    SurfaceCollectionModelObject surfaceModelObject = new SurfaceCollectionModelObject(geoSurfaceCollection);
		AxisModelObject axisModelObject = new AxisModelObject(new Vect3d(1,1,1));
		surfaceModelObject.addChild(axisModelObject);
		
		ModelObject modelObject = null;
		//modelObject = surfaceModelObject;
//		Expression redExp = new Expression(
//				"0.5*exp(-25*((x-0.3,2)+(y-.3,2)+(z-.3,2)))+" +
//    			"0.5*exp(-25*((x-0.7,2)+(y-0.7,2)+(z-0.7,2)))+" +
//    			"0.5*exp(-25*((x-0.3,2)+(y-0.7,2)+(z-0.7,2)))+" +
//    			"0.5*exp(-20*((x-0.5,2)+(y-0.5,2)+(z-0.5,2)))+" +
//    			"0.01");
//		Expression greenExp = new Expression("0.5*exp(-10*((x-0.5,2)+(y-.5,2)))");
//		Expression blueExp = new Expression("exp(-5*(x^2+y^2+z^2))");
		SpatialFunction redExp = new SpatialFunction(){
			public double evaluate(Vect3d v){
				double x = v.getX();
				double y = v.getY();
				double z = v.getZ();
				return 0.4*Math.exp(-2*(Math.pow(x-3,2)+Math.pow(y-3,2)+Math.pow(z-3,2)))+
						0.4*Math.exp(-2*(Math.pow(x-7,2)+Math.pow(y-7,2)+Math.pow(z-7,2)))+
						0.4*Math.exp(-2*(Math.pow(x-3,2)+Math.pow(y-7,2)+Math.pow(z-7,2)))+
						0.4*Math.exp(-2*(Math.pow(x-5,2)+Math.pow(y-5,2)+Math.pow(z-5,2)))+
						0.2;
			}
		};
		SpatialFunction greenExp = new SpatialFunction(){
			public double evaluate(Vect3d v){
				double x = v.getX();
				double y = v.getY();
				return 0.5*Math.exp(-((x-5)*(x-5)+(y-5)*(y-5)));
			}
		};
		SpatialFunction blueExp = new SpatialFunction(){
			public double evaluate(Vect3d v){
				double x = v.getX();
				double y = v.getY();
				double z = v.getZ();
				return Math.exp(-5*(x*x+y*y+z*z));
			}
		};
		
		ISize size = new ISize(32,32,64);
		BoundingBox boundingBox = new BoundingBox(-1,3,-1,5,-1,7);
		ByteImage redImage = ByteImageTest.createSampledImage(size,boundingBox,new AnalyticField(redExp));
		ByteImage greenImage = ByteImageTest.createSampledImage(size,boundingBox,new AnalyticField(greenExp));
		ByteImage blueImage = ByteImageTest.createSampledImage(size,boundingBox,new AnalyticField(blueExp));
		VolumeMIP volumeMIP = new VolumeMIP(redImage,greenImage,blueImage);
		AxisModelObject axis = new AxisModelObject(volumeMIP.getBoundingBox().getSize());

//		axis.addChild(volumeMIP);
//		modelObject = axis;
		volumeMIP.addChild(axis);
		modelObject = volumeMIP;
		
		JFrame frame = new JFrame("Gear Demo");
	    frame.getContentPane().setLayout(new BorderLayout());
	    final JOGLRendererTest jgears = new JOGLRendererTest();
	    jgears.setOpaque(true);
	
	    frame.getContentPane().add(jgears, BorderLayout.CENTER);
	
	
	    
	    frame.setSize(800, 800);
//	    final Animator animator = new Animator(jgears);
	    frame.addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
	          // Run this on another thread than the AWT event queue to
	          // make sure the call to Animator.stop() completes before
	          // exiting
	          new Thread(new Runnable() {
	              public void run() {
//	                animator.stop();
	                System.exit(0);
	              }
	            }).start();
	        }
	      });
	    jgears.joglRenderer.setModelObject(modelObject);
	    frame.setVisible(true);
//	    animator.start();
	  
	  }catch (Exception e){
		  e.printStackTrace(System.out);
	  }
  }


public ModelObject getModelObject() {
	return joglRenderer.getModelObject();
}


public void setModelObject(ModelObject argModelRoot){
	joglRenderer.setModelObject(argModelRoot);
	
}
}

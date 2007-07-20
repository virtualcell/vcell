package org.vcell.render;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;

import cbit.render.objects.BoundingBox;
import cbit.render.objects.Vect3d;

import com.sun.opengl.util.GLUT;


public class JOGLRenderer implements GLEventListener, MouseListener,
		MouseMotionListener {
	
	private ModelObject modelRoot = null;
//	private SurfaceCollection surfaceCollection = null;
//	private BoundingBox surfaceBoundingBox = new BoundingBox(-1,1,-1,1,-1,1);
	private Trackball trackball = null;
	public final static int MANIPULATOR_NONE = 0;
	public final static int MANIPULATOR_ROTATE = 1;
	public final static int MANIPULATOR_PAN = 2;
	public final static int MANIPULATOR_ZOOM = 3;
	
	private int fieldCurrentManipulation = MANIPULATOR_NONE;
	private int viewportW;
	private int viewportH;
	private java.awt.Point pick = new java.awt.Point();
	private java.awt.Point oldPick = new java.awt.Point();


	public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        GLUT glut = new GLUT();
        
//int answer[] = new int[5];     
//gl.glSelectBuffer(1000, ByteBuffer.allocateDirect(1000).order(ByteOrder.nativeOrder()).asIntBuffer());
//gl.glRenderMode(GL.GL_SELECT);
//gl.glInitNames();
//gl.glGetIntegerv(GL.GL_NAME_STACK_DEPTH,answer,0);
//System.out.println("stack depth = "+answer[0]);
        
//gl.glRenderMode(GL.GL_RENDER);
        //
        BoundingBox boundingBox = null;
        if (modelRoot!=null){
        	boundingBox = modelRoot.getSubTreeBoundingBox();
        }else{
        	boundingBox = new BoundingBox(-1,1,-1,1,-1,1);
        }
        gl.glShadeModel(GL.GL_FLAT);
        float ratio = (float)viewportH / (float)viewportW;
		trackball.setCenter(boundingBox.getCenter());
		trackball.setSize(boundingBox.getSize());
		double centerX = boundingBox.getCenter().getX();
		double centerY = boundingBox.getCenter().getX();
		double centerZ = boundingBox.getCenter().getX();
		double maxLength = boundingBox.getSize().length();
		trackball.getCamera().resetView();
        trackball.getCamera().setOrtho(centerX-maxLength/2.0, centerX+maxLength/2.0, centerY-maxLength/2*ratio, centerY+maxLength/2*ratio,centerZ-10*maxLength,centerZ+10*maxLength);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[] { 0f, 0f, -10f, 0f }, 0);  // light from infinity toward positive z
	    gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, new float[] { 0f, 0f, 0f, 1f }, 0); 
	    gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, new float[] { 1f, 1f, 1f, 1f }, 0); 
	    gl.glEnable(GL.GL_LIGHTING);
	    gl.glEnable(GL.GL_LIGHT0);
	    gl.glEnable(GL.GL_DEPTH_TEST);  	                  
	    gl.glEnable(GL.GL_NORMALIZE);
//        gl.glEnable(GL.GL_CULL_FACE_MODE);
//        gl.glCullFace(GL.GL_BACK);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[] { 0f, 0f, 10f, 0f }, 0);  // light from infinity toward positive z
        trackball.getCamera().applyProjection(gl);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_ACCUM_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
	    gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[] { 0f, 0f, 10f, 0f }, 0);
       trackball.updateView(gl);
	    
      
        if (modelRoot!=null){
//        	if (modelRoot.getCallListID()<0 || modelRoot.isDirty()){
//	    	    /* make the gears */
//	    	    int callListID = modelRoot.getCallListID();
//	    	    if (callListID == -1){
//	    	    	callListID = gl.glGenLists(1);
//		    	    modelRoot.setCallListID(callListID);
//	    	    }
//	    	    gl.glNewList(callListID, GL.GL_COMPILE);
//	    	    modelRoot.draw(gl);
//	    	    modelRoot.setDirty(false);
//	    	    gl.glEndList();
//        	}
//       		gl.glCallList(modelRoot.getCallListID());
        	modelRoot.draw(gl);
        }
       
	}

	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
	}

	public void init(GLAutoDrawable drawable) {
		// Use debug pipeline
		if (!(drawable.getGL() instanceof DebugGL)){
		    drawable.setGL(new DebugGL(drawable.getGL()));
		}
		GL gl = drawable.getGL();
		System.err.println("INIT GL IS: " + gl.getClass().getName());
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        viewportW=w;
        viewportH=h;
        gl.glViewport(x,y,w,h);
        System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
		System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
		System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));
	}

	  // Methods required for the implementation of MouseListener
	  public void mouseEntered(MouseEvent e) {}
	  public void mouseExited(MouseEvent e) {}

	  public void mousePressed(MouseEvent e) {
			pick.x = oldPick.x = e.getX();
			pick.y = oldPick.y = e.getY();

			fieldCurrentManipulation = getManipulationOPFromMouseButtons(e);
	  }
	    
	  public void mouseReleased(MouseEvent e) {
	  }
	    
	  public void mouseClicked(MouseEvent  e) {}
	    
	    
	public void mouseDragged(MouseEvent e) {
	
	
		int currManip = getManipulationOPFromMouseButtons(e);
		if(currManip != getCurrentManipulation()){
			fieldCurrentManipulation = currManip;
		}
		
		int x = e.getX();
		int y = e.getY();
		int sizex = viewportW;
		int sizey = viewportH;
	
	
		double zoomgain = 1.0;
		double rotategain =1.0;
	
		sizey=Math.abs(sizey);
		sizex=Math.abs(sizex);
	
	   double oldx = ( ((2.0*pick.x) - sizex ) / sizex );
	   double oldy = ( ((2.0*pick.y) - sizey ) / sizey );
	   double newx = ( ((2.0*x) - sizex ) / sizex );
	   double newy = ( ((2.0*y) - sizey ) / sizey );
	
	   //cbit.vcell.render.Vect3d oldValue = fieldViewAngleRadians;
	
	    
		switch(getCurrentManipulation()){
			case MANIPULATOR_ROTATE: {
				//if (getDimension().intValue() == 3){
					trackball.rotate_xy(oldx*rotategain,-oldy*rotategain,newx*rotategain,-newy*rotategain);
				  // fieldViewAngleRadians = trackball.getRotation();
				   //firePropertyChange("viewAngleRadians", oldValue, fieldViewAngleRadians);
					//setViewAngleRadians(fieldSurfaceCanvas.getTrackball().getRotation());
				//}
				break;
			}
			case MANIPULATOR_ZOOM: { 
				//getSurfaceCanvas().setBQuickRender(true);
				double oldScale = trackball.getScale();
				trackball.setScale(trackball.calculateNewScale_zoom_z(oldScale, -oldy*zoomgain, -newy*zoomgain));
				break;
			}
			case MANIPULATOR_PAN: {
				double pangain = 12.0; //viewportW/(trackball.getSize().length()*trackball.getScale());
				System.out.println("joglRender.mouseDragged(): trackballSize.length="+trackball.getSize().length()+", trackballScale = "+trackball.getScale()+", cameraScale = "+trackball.getCamera().getProjectionScale()+", trackball size = "+trackball.getSize().toString()+", panGain="+pangain);
				trackball.pan_xy(oldx*pangain,-oldy*pangain,newx*pangain,-newy*pangain);
				//trackball.setTranslation(trackball.calculateNewDisplacement_pan_xy(oldTranslation,oldx*panScale,oldy*panScale,newx*panScale,newy*panScale));
				break;
			}
		} // end switch
	
	   //if (_bRecord){
	      //if (!_trackball->record()){
	         //printf("recorded past end of buffer\n");
	         //_bRecord = FALSE;
	      //}
	   //}
		
	   oldPick.x = pick.x;
	   oldPick.y = pick.y;
	   pick.x = x;
	   pick.y = y;
	   
	   ((GLJPanel)e.getSource()).display();
	}

	public void mouseMoved(MouseEvent e) {}

//	public SurfaceCollection getSurfaceCollection() {
//		return surfaceCollection;
//	}
	public ModelObject getModelObject() {
		return modelRoot;
	}
	public int getManipulationOPFromMouseButtons(java.awt.event.MouseEvent e) {
		
		int manipOP = MANIPULATOR_NONE;

		if((e.getModifiers() & ~(MouseEvent.BUTTON1_MASK|MouseEvent.BUTTON3_MASK)) == 0){
			boolean bButton1 = (e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK;
			boolean bButton3 = (e.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK;
			if(e.getID() == MouseEvent.MOUSE_DRAGGED){
				if (bButton1 && bButton3){
					manipOP=MANIPULATOR_ZOOM;
				}else if (bButton1){
					manipOP=MANIPULATOR_ROTATE;
				}else if (bButton3){
					manipOP=MANIPULATOR_PAN;
				}
			}else if(e.getID() == MouseEvent.MOUSE_PRESSED){
				manipOP = (getCurrentManipulation() == MANIPULATOR_NONE?(bButton1?MANIPULATOR_ROTATE:MANIPULATOR_PAN):MANIPULATOR_ZOOM);
			}else if(e.getID() == MouseEvent.MOUSE_RELEASED){
				manipOP =
					(getCurrentManipulation() == MANIPULATOR_PAN || getCurrentManipulation() == MANIPULATOR_ROTATE?
						MANIPULATOR_NONE
					:
						(bButton1?MANIPULATOR_PAN:MANIPULATOR_ROTATE)
					);
			}
		}

		//System.out.println(manipOP+" "+(e.getModifiers()&(MouseEvent.BUTTON1_MASK|MouseEvent.BUTTON3_MASK))+" "+e);
		return manipOP;
	}

	public int getCurrentManipulation() {
		return fieldCurrentManipulation;
	}

	public JOGLRenderer() {
		super();
       	Camera camera = new Camera();
    	trackball = new Trackball(camera);
    	camera.resetView();
        trackball.getCamera().setOrtho(-1,1,-1,1,-1,1);
	}

//	public void setSurfaceCollection(SurfaceCollection surfaceCollection) {
//		this.surfaceCollection = surfaceCollection;
//		resetCameraAndTrackball();
//		surfaceID = -1;
//	}

	public void setModelObject(ModelObject argModelRoot){
		this.modelRoot = argModelRoot;
		resetCameraAndTrackball();
		if (modelRoot!=null){
			modelRoot.clearCallLists();
		}
	}
	public void resetCameraAndTrackball(){
//		if (surfaceCollection!=null){
//			Node nodes[] = surfaceCollection.getNodes();
//			surfaceBoundingBox = BoundingBox.fromNodes(nodes);
//			trackball.setScale(1.0);
//			trackball.setTranslation(new Vect3d(0,0,0));
//			surfaceID = -1;
//		}
		trackball.setScale(1.0);
		trackball.setTranslation(new Vect3d(0,0,0));
	}

}

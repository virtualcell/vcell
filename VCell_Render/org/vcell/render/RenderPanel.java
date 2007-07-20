package org.vcell.render;

import javax.media.opengl.*;


/**
 * JGears.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel) <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */

public class RenderPanel extends GLJPanel {
  private static GLCapabilities caps;
  private JOGLRenderer joglRenderer = new JOGLRenderer();

  static {
    caps = new GLCapabilities();
    //caps.setAlphaBits(8);
  }
  
  public RenderPanel() {
    super(caps, null, null);
 	    addGLEventListener(joglRenderer);
	    addMouseMotionListener(joglRenderer);
	    addMouseListener(joglRenderer);
   }

  

public ModelObject getModelObject() {
	return joglRenderer.getModelObject();
}


public void setModelObject(ModelObject argModelRoot){
	joglRenderer.setModelObject(argModelRoot);
	
}
}

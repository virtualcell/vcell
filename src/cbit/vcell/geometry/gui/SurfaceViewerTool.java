/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
/**
 * Insert the type's description here.
 * Creation date: (11/30/2003 9:33:58 PM)
 * @author: Jim Schaff
 */
public class SurfaceViewerTool {

	//
	private java.awt.Container canvasParentWindow = null;
	//
	private javax.swing.Timer resizeTimer =
		new javax.swing.Timer(1000,
			new java.awt.event.ActionListener(){
					public void actionPerformed(java.awt.event.ActionEvent e){
						if(fieldSurfaceCanvas != null){
							resizeTimer.stop();
							SurfaceViewerTool.this.fullRepaint();
						}
					}
			}
			);
	//
	private class EventHandler implements java.awt.event.ComponentListener,java.awt.event.MouseMotionListener, java.awt.event.MouseListener, java.awt.event.KeyListener {

		public void componentHidden(java.awt.event.ComponentEvent e){}
		public void componentMoved(java.awt.event.ComponentEvent e){}
		public void componentResized(java.awt.event.ComponentEvent e){
			if(fieldSurfaceCanvas.getBQuickRender() == false){
				fieldSurfaceCanvas.setBQuickRender(true);
			}
			//System.out.println("START QuickRender");
			resizeTimer.restart();
			fieldSurfaceCanvas.clearCachedImage();
		}
		public void componentShown(java.awt.event.ComponentEvent e){}

		
		public void mouseDragged(MouseEvent e) {
			SurfaceViewerTool.this.mouseDragged(e);
		}
		public void mouseMoved(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			SurfaceViewerTool.this.mousePressed(e);
		}
		public void mouseReleased(MouseEvent e) {
			SurfaceViewerTool.this.mouseReleased(e);
		}
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void keyPressed(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
	}
	private SurfaceCanvas fieldSurfaceCanvas = null;
	private EventHandler eventHandler = new EventHandler();
	private java.awt.Point pick = new java.awt.Point();
	private java.awt.Point oldPick = new java.awt.Point();
	private boolean bAnimate = false;
	public final static int MANIPULATOR_NONE = 0;
	public final static int MANIPULATOR_ROTATE = 1;
	public final static int MANIPULATOR_PAN = 2;
	public final static int MANIPULATOR_ZOOM = 3;
	private java.lang.Integer fieldDimension = new Integer(3);
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.render.Vect3d fieldViewAngleRadians = null;
	private int fieldCurrentManipulation = MANIPULATOR_NONE;

/**
 * SurfaceViewerTool constructor comment.
 */
public SurfaceViewerTool() {
	super();
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Insert the method's description here.
 * Creation date: (1/21/2006 11:33:05 AM)
 */
public void fullRepaint() {

	if(canvasParentWindow == null){
		canvasParentWindow = org.vcell.util.BeanUtils.findTypeParentOfComponent(getSurfaceCanvas(),java.awt.Window.class);
	}
	
	try{
		if(canvasParentWindow != null){
			org.vcell.util.BeanUtils.setCursorThroughout(canvasParentWindow,java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		}
		getSurfaceCanvas().setBQuickRender(false);
		//getSurfaceCanvas().invalidate();
		getSurfaceCanvas().repaint();
	}finally{
		if(canvasParentWindow != null){
			org.vcell.util.BeanUtils.setCursorThroughout(canvasParentWindow,java.awt.Cursor.getDefaultCursor());
		}
	}
	
	
}


/**
 * Gets the currentManipulation property (int) value.
 * @return The currentManipulation property value.
 * @see #setCurrentManipulation
 */
public int getCurrentManipulation() {
	return fieldCurrentManipulation;
}


/**
 * Gets the dimension property (java.lang.Integer) value.
 * @return The dimension property value.
 * @see #setDimension
 */
public java.lang.Integer getDimension() {
	return fieldDimension;
}


/**
 * Insert the method's description here.
 * Creation date: (9/24/2005 12:59:22 PM)
 * @return int
 * @param inputEvent java.awt.event.InputEvent
 */
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


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Gets the surfaceCanvas property (cbit.vcell.geometry.gui.SurfaceCanvas) value.
 * @return The surfaceCanvas property value.
 * @see #setSurfaceCanvas
 */
public SurfaceCanvas getSurfaceCanvas() {
	return fieldSurfaceCanvas;
}


/**
 * Gets the viewAngleRadians property (cbit.vcell.render.Vect3d) value.
 * @return The viewAngleRadians property value.
 * @see #setViewAngleRadians
 */
public cbit.vcell.render.Vect3d getViewAngleRadians() {
	return fieldViewAngleRadians;
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * Insert the method's description here.
 * Creation date: (9/24/2005 12:40:07 PM)
 * @return boolean
 */
public boolean isPanning() {
	return getCurrentManipulation() == MANIPULATOR_PAN;
}


/**
 * Insert the method's description here.
 * Creation date: (9/24/2005 12:40:07 PM)
 * @return boolean
 */
public boolean isRotating() {
	return getCurrentManipulation() == MANIPULATOR_ROTATE;
}


/**
 * Insert the method's description here.
 * Creation date: (9/24/2005 12:40:07 PM)
 * @return boolean
 */
public boolean isZooming() {
	return getCurrentManipulation() == MANIPULATOR_ZOOM;
}


/**
 * Insert the method's description here.
 * Creation date: (11/30/2003 10:48:40 PM)
 * @param e java.awt.event.MouseEvent
 */
private void mouseDragged(MouseEvent e) {


	int currManip = getManipulationOPFromMouseButtons(e);
	if(currManip != getCurrentManipulation()){
		setCurrentManipulation(currManip);
	}
	
	java.awt.Dimension size = fieldSurfaceCanvas.getSize();
	int x = e.getX();
	int y = e.getY();
	int sizex = size.width;
	int sizey = size.height;


	double zoomgain = 1.0;
	double pangain = 12.0;
	double rotategain =1.0;

	sizey=Math.abs(sizey);
	sizex=Math.abs(sizex);

   double oldx = ( ((2.0*pick.x) - sizex ) / sizex );
   double oldy = ( ((2.0*pick.y) - sizey ) / sizey );
   double newx = ( ((2.0*x) - sizex ) / sizex );
   double newy = ( ((2.0*y) - sizey ) / sizey );

   cbit.vcell.render.Vect3d oldValue = fieldViewAngleRadians;

   getSurfaceCanvas().setBQuickRender(true);
   
	switch(getCurrentManipulation()){
		case MANIPULATOR_ROTATE: {
			if (getDimension().intValue() == 3){
				fieldSurfaceCanvas.getTrackball().rotate_xy(oldx*rotategain,oldy*rotategain,newx*rotategain,newy*rotategain);
			   fieldViewAngleRadians = fieldSurfaceCanvas.getTrackball().getRotation();
			   firePropertyChange("viewAngleRadians", oldValue, fieldViewAngleRadians);
				//setViewAngleRadians(fieldSurfaceCanvas.getTrackball().getRotation());
			}
			break;
		}
		case MANIPULATOR_ZOOM: { 
			//getSurfaceCanvas().setBQuickRender(true);
			double oldScale = fieldSurfaceCanvas.getScale();
			cbit.vcell.render.Trackball trackball = fieldSurfaceCanvas.getTrackball();
			fieldSurfaceCanvas.setScale(trackball.calculateNewScale_zoom_z(oldScale, -oldy*zoomgain, -newy*zoomgain));
			break;
		}
		case MANIPULATOR_PAN: {
			//getSurfaceCanvas().setBQuickRender(true);
			cbit.vcell.render.Vect3d oldTranslation = fieldSurfaceCanvas.getDisplacement();
			cbit.vcell.render.Trackball trackball = fieldSurfaceCanvas.getTrackball();
			//cbit.vcell.render.Vect3d trSize = trackball.getSize();
			//double panScale = 2.0/Math.max(trSize.getX(), Math.max(trSize.getY(), trSize.getZ()));
			//panScale*= pangain;
		//trackball.pan_xy(oldx*pangain,oldy*pangain,newx*pangain,newy*pangain);
			//fieldSurfaceCanvas.setDisplacement(trackball.calculateNewDisplacement_pan_xy(oldTranslation,oldx*panScale,oldy*panScale,newx*panScale,newy*panScale));
			fieldSurfaceCanvas.setDisplacement(new cbit.vcell.render.Vect3d(oldTranslation.getX()+x-pick.x,oldTranslation.getY()+y-pick.y,oldTranslation.getZ()));
			break;
		}
	} // end switch

   //if (_bRecord){
      //if (!_trackball->record()){
         //printf("recorded past end of buffer\n");
         //_bRecord = FALSE;
      //}
   //}

	
   fieldSurfaceCanvas.repaint();

   oldPick.x = pick.x;
   oldPick.y = pick.y;
   pick.x = x;
   pick.y = y;
}


/**
 * Insert the method's description here.
 * Creation date: (11/30/2003 10:48:40 PM)
 * @param e java.awt.event.MouseEvent
 */
private void mousePressed(MouseEvent e) {
	pick.x = oldPick.x = e.getX();
	pick.y = oldPick.y = e.getY();
	bAnimate=false;

	setCurrentManipulation(getManipulationOPFromMouseButtons(e));
	
}


/**
 * Insert the method's description here.
 * Creation date: (11/30/2003 10:48:40 PM)
 * @param e java.awt.event.MouseEvent
 */
private void mouseReleased(MouseEvent e) {
	
	if (getSurfaceCanvas().getBQuickRender()){
		fullRepaint();
		//getSurfaceCanvas().setBQuickRender(false);
		//getSurfaceCanvas().invalidate();
		//getSurfaceCanvas().repaint();
	}
	if (getCurrentManipulation() == MANIPULATOR_ROTATE){
		//if (Math.abs(oldPick.x-pick.x)+Math.abs(oldPick.y-pick.y)<=2){
			//return;
		//}
		bAnimate=true;
	}
	
	setCurrentManipulation(getManipulationOPFromMouseButtons(e));
	
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (11/30/2003 11:29:41 PM)
 */
public void resetView() {
	if (fieldSurfaceCanvas==null || fieldSurfaceCanvas.getSurfaceCollection()==null){
		return;
	}

	cbit.vcell.render.Vect3d oldViewAngle = fieldSurfaceCanvas.getTrackball().getRotation();
	
	org.vcell.util.Extent extent = getSurfaceCanvas().getExtent();
	org.vcell.util.Origin origin = getSurfaceCanvas().getOrigin();
	double scale = 0.2;
	cbit.vcell.render.Vect3d center = new cbit.vcell.render.Vect3d(origin.getX()+extent.getX()/2.0, origin.getY()+extent.getY()/2.0, origin.getZ()+extent.getZ()/2.0); 
	cbit.vcell.render.Vect3d size = new cbit.vcell.render.Vect3d(extent.getX()*scale, extent.getY()*scale, extent.getZ()*scale); 
    fieldSurfaceCanvas.getTrackball().setSize(size);
    fieldSurfaceCanvas.getTrackball().setCenter(center);
    fieldSurfaceCanvas.setDisplacement(new cbit.vcell.render.Vect3d(0.0,0.0,0.0));
    double angleX = (getDimension() == null || getDimension().intValue() < 3?0:Math.PI/8.0);
    double angleY = (getDimension() == null || getDimension().intValue() < 3?0:Math.PI/8.0);
    double angleZ = (getDimension() == null || getDimension().intValue() < 3?0:Math.PI/8.0);
	fieldSurfaceCanvas.getTrackball().setRotation(angleX,angleY,angleZ);
	setViewAngleRadians(fieldSurfaceCanvas.getTrackball().getRotation());

	fieldSurfaceCanvas.setScale(1.0); // fake out trackball to update camera

	if(fieldSurfaceCanvas.getBQuickRender()){
		fieldSurfaceCanvas.repaint();
	}else{
		fullRepaint();
	}
}


/**
 * Sets the currentManipulation property (int) value.
 * @param currentManipulation The new value for the property.
 * @see #getCurrentManipulation
 */
public void setCurrentManipulation(int currentManipulation) {
	int oldValue = fieldCurrentManipulation;
	fieldCurrentManipulation = currentManipulation;
	firePropertyChange("currentManipulation", new Integer(oldValue), new Integer(currentManipulation));
}


/**
 * Sets the dimension property (java.lang.Integer) value.
 * @param dimension The new value for the property.
 * @see #getDimension
 */
public void setDimension(java.lang.Integer dimension) {
	fieldDimension = dimension;
	if (dimension!=null && getSurfaceCanvas()!=null){
		if (dimension.intValue()<3){
			getSurfaceCanvas().setEnableDepthCueing(false);
			//getSurfaceCanvas().setShowWireframe(true);
		}else{
			getSurfaceCanvas().setEnableDepthCueing(true);
			//getSurfaceCanvas().setShowWireframe(false);
		}
	}		
}


/**
 * Sets the surfaceCanvas property (cbit.vcell.geometry.gui.SurfaceCanvas) value.
 * @param surfaceCanvas The new value for the property.
 * @see #getSurfaceCanvas
 */
public void setSurfaceCanvas(SurfaceCanvas surfaceCanvas) {
	SurfaceCanvas oldSurfaceCanvas = fieldSurfaceCanvas;
	if (oldSurfaceCanvas!=null){
		oldSurfaceCanvas.removeComponentListener(eventHandler);
		
		oldSurfaceCanvas.removeMouseListener(eventHandler);
		oldSurfaceCanvas.removeMouseMotionListener(eventHandler);
		oldSurfaceCanvas.removeKeyListener(eventHandler);
		removePropertyChangeListener(oldSurfaceCanvas);
	}
	fieldSurfaceCanvas = surfaceCanvas;
	if (fieldSurfaceCanvas!=null){
		fieldSurfaceCanvas.addComponentListener(eventHandler);
		
		fieldSurfaceCanvas.addMouseListener(eventHandler);
		fieldSurfaceCanvas.addMouseMotionListener(eventHandler);
		fieldSurfaceCanvas.addKeyListener(eventHandler);
		addPropertyChangeListener(fieldSurfaceCanvas);
	}
}


/**
 * Sets the viewAngleRadians property (cbit.vcell.render.Vect3d) value.
 * @param viewAngleRadians The new value for the property.
 * @see #getViewAngleRadians
 */
public void setViewAngleRadians(cbit.vcell.render.Vect3d viewAngleRadians) {
	cbit.vcell.render.Vect3d oldValue = fieldViewAngleRadians;
	fieldViewAngleRadians = viewAngleRadians;
	fieldSurfaceCanvas.getTrackball().setRotation(viewAngleRadians.getX(),viewAngleRadians.getY(),viewAngleRadians.getZ());
	firePropertyChange("viewAngleRadians", oldValue, viewAngleRadians);
}
}

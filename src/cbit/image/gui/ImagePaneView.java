/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import cbit.image.ZoomEvent;
import cbit.image.ZoomListener;
import cbit.vcell.geometry.gui.DrawPaneModel;
import cbit.vcell.geometry.gui.VCellDrawable;

/**
 * Insert the type's description here.
 * Creation date: (9/3/00 1:57:50 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class ImagePaneView extends javax.swing.JPanel implements VCellDrawable {
	private ImagePaneModel ivjImagePaneModel = null;
	private DrawPaneModel fieldDrawPaneModel = null;
	//
	private transient PanListener panListener = null;
	private transient ZoomListener zoomListener = null;
	//
	private int mode = NO_OP;
	private Point anchorPoint = null;
	private static final int ZOOM_MOUSE_DELTA = 10;
	//
	private static final int NO_OP = 0;
	private static final int PANNING = 1;
	private static final int ZOOMING = 2;
	private boolean fieldForceZoom = false;
	private boolean fieldForcePan = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

class IvjEventHandler implements java.awt.event.MouseListener, java.beans.PropertyChangeListener {
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == ImagePaneView.this) 
				connEtoC3(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ImagePaneView.this.getImagePaneModel() && (evt.getPropertyName().equals("viewPortImage"))) 
				connEtoC1(evt);
			if (evt.getSource() == ImagePaneView.this.getImagePaneModel() && (evt.getPropertyName().equals("dimension"))) 
				connEtoM1(evt);
		};
	};
/**
 * ImagePaneView constructor comment.
 */
public ImagePaneView() {
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 1:19:11 PM)
 * @param panListener cbit.image.PanListener
 */
public synchronized void addPanListener(PanListener l) {
	if (l == null) {
		return;
	}
	panListener = ImagePaneViewMulticaster.add(panListener, l);
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 1:19:11 PM)
 * @param panListener cbit.image.PanListener
 */
public synchronized void addZoomListener(ZoomListener l) {
	if (l == null) {
		return;
	}
	zoomListener = ImagePaneViewMulticaster.add(zoomListener, l);
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2000 2:04:50 PM)
 * @return java.awt.Point
 * @param pixelPoint java.awt.Point
 */
private Point adjustPointPosition(Point pixelPoint) {
	//Do nothing for now
	return pixelPoint;
}
/**
 * Insert the method's description here.
 * Creation date: (9/4/00 4:46:01 PM)
 */
public void clear() {
	if (getImagePaneModel() != null) {
		getImagePaneModel().setSourceData(null);
	}
	repaint();
}
/**
 * connEtoC1:  (ImagePaneModel.viewPortImage --> ImagePaneView.repaint()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.repaint();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (ImagePaneView.mouse.mouseEntered(java.awt.event.MouseEvent) --> ImagePaneView.requestFocus()V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.MouseEvent arg1) {
	
	try {
		Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		if(focusOwner != null && (focusOwner instanceof cbit.image.gui.ImagePaneView)) {
			Component focusOwnerTopComponent = null;
			for (Container p = focusOwner.getParent(); p != null; p = p.getParent()) {
				if(p.getParent() == null) {
					focusOwnerTopComponent = p;
					break;
				}
			}
			Component thisTopComponent = null;
			for (Container p = this.getParent(); p != null; p = p.getParent()) {
				if(p.getParent() == null) {
					thisTopComponent = p;
					break;
				}
			}
			if(focusOwnerTopComponent == thisTopComponent){
				this.requestFocus();
			}
		}else{
			this.requestFocus();
			
		}
	
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (ImagePaneView.initialize() --> ImagePaneView.imagePaneView_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4() {
	try {
		// user code begin {1}
		// user code end
		this.imagePaneView_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (ImagePaneModel.dimension --> ImagePaneView.invalidate()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.invalidate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/4/00 4:30:35 PM)
 * @return cbit.vcell.geometry.DrawPaneModel
 */
public cbit.vcell.geometry.gui.DrawPaneModel getDrawPaneModel() {
	return fieldDrawPaneModel;
}
/**
 * Gets the forcePan property (boolean) value.
 * @return The forcePan property value.
 * @see #setForcePan
 */
public boolean getForcePan() {
	return fieldForcePan;
}
/**
 * Gets the forceZoom property (boolean) value.
 * @return The forceZoom property value.
 * @see #setForceZoom
 */
public boolean getForceZoom() {
	return fieldForceZoom;
}
/**
 * Return the ImagePaneModel1 property value.
 * @return imagescroller.ImagePaneModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public ImagePaneModel getImagePaneModel() {
	if (ivjImagePaneModel == null) {
		try {
			ivjImagePaneModel = new cbit.image.gui.ImagePaneModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImagePaneModel;
}
/**
 * Insert the method's description here.
 * Creation date: (9/4/00 4:30:35 PM)
 * @return java.awt.Point
 * @param p java.awt.Point
 */
public java.awt.Point getImagePoint(java.awt.Point pixelPoint) {
	if(getImagePaneModel() != null){
		return getImagePaneModel().getImagePoint(adjustPointPosition(pixelPoint));
	}
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (10/15/00 2:00:08 PM)
 * @return java.awt.Point
 * @param p java.awt.Point
 */
public java.awt.geom.Point2D.Double getImagePointUnitized(java.awt.Point pixelPoint) {
	return getImagePaneModel().calculateImagePointUnitized(adjustPointPosition(pixelPoint));
}
/**
 * Insert the method's description here.
 * Creation date: (7/8/2003 5:22:16 PM)
 */
public java.awt.Dimension getPreferredSize() {
	if(getImagePaneModel() != null && getImagePaneModel().getDimension() != null){
		return getImagePaneModel().getDimension();
	}
	return new java.awt.Dimension(50,50);
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Comment
 */
private void imagePaneView_Initialize() {
	//This gives us events even if there are no listeners registered
	//This is done as part of the process of consuming events that turn into pan and zoom
	//processMouseEvent and processMouseMotionEvent are overriden to detect pan and zoom
	enableEvents(java.awt.AWTEvent.MOUSE_EVENT_MASK | java.awt.AWTEvent.MOUSE_MOTION_EVENT_MASK);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addMouseListener(ivjEventHandler);
	getImagePaneModel().addPropertyChangeListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ImagePaneView");
		setLayout(null);
		setSize(160, 120);
		initConnections();
		connEtoC4();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2000 2:03:02 PM)
 * @return boolean
 * @param pixelPoint java.awt.Point
 */
public boolean isPointOnImage(Point pixelPoint) {
	return getImagePaneModel().isPointOnImage(adjustPointPosition(pixelPoint));
}
/**
 * Insert the method's description here.
 * Creation date: (7/8/2003 5:22:16 PM)
 */
public void newMethod() {}
/**
 * Insert the method's description here.
 * Creation date: (9/3/00 2:16:31 PM)
 */
public void paintComponent(java.awt.Graphics g) {
	super.paintComponent(g);
	if (getImagePaneModel() != null) {
		java.awt.image.BufferedImage viewportImage = getImagePaneModel().getViewPortImage();
		if (viewportImage != null) {
			//
			//do all this because we can only draw an image the size of the whole viewport
			//
			//java.awt.Rectangle gClipBounds = g.getClip().getBounds();
			java.awt.Rectangle gClipBounds = g.getClipBounds();
			java.awt.Rectangle viewPortBounds = getImagePaneModel().getViewport();
			if (gClipBounds.getWidth() != viewPortBounds.getWidth() || gClipBounds.getHeight() != viewPortBounds.getHeight()) {
	            //Do this to deal with partial window exposure
				//g.setClip(viewPortBounds.x, viewPortBounds.y, viewPortBounds.width, viewPortBounds.height);
				g.drawImage(viewportImage, viewPortBounds.x, viewPortBounds.y, this);
				//g.setClip(gClipBounds);
			} else {
	            //Normal scrolling or zooming
				g.drawImage(viewportImage, gClipBounds.x, gClipBounds.y, this);
			}
		}
	}
	if (fieldDrawPaneModel != null && getImagePaneModel() != null && getImagePaneModel().getDimension() != null) {
		fieldDrawPaneModel.draw(g);
	}
}
/**
 * Comment
 */
private boolean panAndZoom(java.awt.event.MouseEvent mouseEvent) {
	//
	boolean bConsumed = false;
	//
	if (mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_PRESSED) {
		//set anchor point for all operations
		anchorPoint = mouseEvent.getPoint();
		if (getForceZoom() || (mouseEvent.getModifiers() & InputEvent.ALT_MASK) != 0) {
			mode = ZOOMING;
		} else if (getForcePan() || (mouseEvent.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
			mode = PANNING;
		}
		if (mode != NO_OP) {
			bConsumed = true;
		}
	} else if (mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_RELEASED) {
		if (mode != NO_OP) {
			mode = NO_OP;
			bConsumed = true;
		}
	} else if (mode == PANNING) {
		if (mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_DRAGGED) {
			if (panListener != null) {
				int deltaX = mouseEvent.getPoint().x - anchorPoint.x;
				int deltaY = mouseEvent.getPoint().y - anchorPoint.y;
				PanEvent panEvent = new PanEvent(this, 0, deltaX, deltaY);
				panListener.panning(panEvent);
				anchorPoint.x = mouseEvent.getPoint().x - deltaX;
				anchorPoint.y = mouseEvent.getPoint().y - deltaY;
			}
			bConsumed = true;
		}
	} else if (mode == ZOOMING) {
		if (mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_DRAGGED) {
			if (anchorPoint != null) {
				int zoomDelta = (mouseEvent.getPoint().x - anchorPoint.x) / ZOOM_MOUSE_DELTA;
				if (Math.abs(zoomDelta) > 0 && zoomListener != null) {
					anchorPoint = null;
					ZoomEvent zoomEvent = new ZoomEvent(this, 0, zoomDelta);
					zoomListener.zooming(zoomEvent);
				}
			} else {
				anchorPoint = mouseEvent.getPoint();
			}
			bConsumed = true;
		}
		//
	}
	return bConsumed;
}
/**
 * Insert the method's description here.
 * Creation date: (10/8/00 11:43:51 AM)
 */
protected void processMouseEvent(MouseEvent e) {
	if (!panAndZoom(e)) {
		super.processMouseEvent(e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/8/00 11:47:15 AM)
 */
protected void processMouseMotionEvent(MouseEvent e) {
	if (!panAndZoom(e)) {
		super.processMouseMotionEvent(e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 1:51:37 PM)
 */
public synchronized void removePanListener(PanListener l) {
	if (l == null) {
		return;
	}
	panListener = ImagePaneViewMulticaster.remove(panListener, l);
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 1:51:37 PM)
 */
public synchronized void removeZoomListener(ZoomListener l) {
	if (l == null) {
		return;
	}
	zoomListener = ImagePaneViewMulticaster.remove(zoomListener, l);
}
/**
 * Insert the method's description here.
 * Creation date: (9/4/00 4:30:35 PM)
 * @param dpm cbit.vcell.geometry.DrawPaneModel
 */
public void setDrawPaneModel(cbit.vcell.geometry.gui.DrawPaneModel drawPaneModel) {
	fieldDrawPaneModel = drawPaneModel;
}
/**
 * Sets the forcePan property (boolean) value.
 * @param forcePan The new value for the property.
 * @see #getForcePan
 */
public void setForcePan(boolean forcePan) {
	boolean oldValue = fieldForcePan;
	fieldForcePan = forcePan;
	firePropertyChange("forcePan", new Boolean(oldValue), new Boolean(forcePan));
}
/**
 * Sets the forceZoom property (boolean) value.
 * @param forceZoom The new value for the property.
 * @see #getForceZoom
 */
public void setForceZoom(boolean forceZoom) {
	boolean oldValue = fieldForceZoom;
	fieldForceZoom = forceZoom;
	firePropertyChange("forceZoom", new Boolean(oldValue), new Boolean(forceZoom));
}
}

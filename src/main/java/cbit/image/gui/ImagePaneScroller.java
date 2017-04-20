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

import java.awt.BorderLayout;
import java.awt.Component;

import cbit.image.SourceDataInfo;
import cbit.image.ZoomEvent;
import cbit.image.ZoomListener;

/**
 * Insert the type's description here.
 * Creation date: (9/3/00 3:14:36 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class ImagePaneScroller extends javax.swing.JPanel implements PanListener, ZoomListener, java.awt.event.AdjustmentListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
	private ImagePaneView ivjImagePaneView = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	//
	private double viewCenterX = .5;
	private double viewCenterY = .5;
	private boolean hasUserSetZoom = false;
	private boolean ivjConnPtoP1Aligning = false;
	private javax.swing.JViewport ivjviewport1 = null;
	private boolean ivjConnPtoP2Aligning = false;
	private boolean ivjConnPtoP3Aligning = false;
	private javax.swing.JScrollBar ivjhorizontalScrollBar1 = null;
	private javax.swing.JScrollBar ivjverticalScrollBar1 = null;
	private ImagePlaneManager fieldImagePlaneManager = new ImagePlaneManager();
/**
 * ImagePaneScroller constructor comment.
 */
public ImagePaneScroller() {
	super();
	initialize();
}

/**
 * Insert the method's description here.
 * Creation date: (10/9/00 2:27:49 PM)
 * @param location java.awt.Point
 */
private void adjustLocationForEdge(java.awt.Point location,java.awt.Rectangle viewportSize,java.awt.Dimension viewSize) {
	//Coordinates if the view that appear in the ULC of the JViewPort
	if (location.x < 0) {
		location.x = 0;
	}
	if (location.y < 0) {
		location.y = 0;
	}
	if(viewSize.width <= viewportSize.width){
		location.x = 0;
	}else if (location.x + viewportSize.width >= viewSize.width) {
		location.x = viewSize.width - viewportSize.width;
	}
	if(viewSize.height <= viewportSize.height){
		location.y = 0;
	}else if (location.y + viewportSize.height >= viewSize.height) {
		location.y = viewSize.height - viewportSize.height;
	}
}
/**
 * Method to handle events for the AdjustmentListener interface.
 * @param e java.awt.event.AdjustmentEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void adjustmentValueChanged(java.awt.event.AdjustmentEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == gethorizontalScrollBar1()) 
		connEtoC4(e);
	if (e.getSource() == getverticalScrollBar1()) 
		connEtoC6(e);
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (10/8/00 5:02:18 PM)
 * @return java.awt.Point
 */
private java.awt.Point calculateULCFromCenter(java.awt.Rectangle viewportSize,java.awt.Dimension viewSize) {
	//
	java.awt.Point location = new java.awt.Point(0, 0);
	//
	int desiredX = (int) ((double) viewSize.width * viewCenterX) - (viewportSize.width / 2);
	int desiredY = (int) ((double) viewSize.height * viewCenterY) - (viewportSize.height / 2);
	location.setLocation(desiredX, desiredY);
	//
	adjustLocationForEdge(location,viewportSize,viewSize);
	//
	return location;
}
/**
 * connEtoC1:  (viewport1.change. --> ImagePaneScroller.viewport1_Change()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.viewport1_Change();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (ImagePaneView.pan.panning(cbit.image.PanEvent) --> ImagePaneScroller.imagePaneView_Panning(Lcbit.image.PanEvent;)V)
 * @param arg1 cbit.image.PanEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(PanEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.imagePaneView_Panning(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (ImagePaneScroller.initialize() --> ImagePaneScroller.imagePaneScroller_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3() {
	try {
		// user code begin {1}
		// user code end
		this.imagePaneScroller_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (horizontalScrollBar1.adjustment.adjustmentValueChanged(java.awt.event.AdjustmentEvent) --> ImagePaneScroller.scrollbarValue()V)
 * @param arg1 java.awt.event.AdjustmentEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.AdjustmentEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.scrollbarValue();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (ImagePaneView.zoom.zooming(cbit.image.ZoomEvent) --> ImagePaneScroller.imagePaneView_Zooming(Lcbit.image.ZoomEvent;)V)
 * @param arg1 cbit.image.ZoomEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(ZoomEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.imagePaneView_Zooming(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (verticalScrollBar1.adjustment.adjustmentValueChanged(java.awt.event.AdjustmentEvent) --> ImagePaneScroller.scrollbarValue()V)
 * @param arg1 java.awt.event.AdjustmentEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.AdjustmentEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.scrollbarValue();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (ImagePaneScroller.imagePlaneManager --> ImagePaneScroller.imagePaneScroller_ImagePlaneManager()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.imagePaneScroller_ImagePlaneManager();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetSource:  (JScrollPane1.viewport <--> viewport1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getviewport1() != null)) {
				getJScrollPane1().setViewport(getviewport1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetTarget:  (JScrollPane1.viewport <--> viewport1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setviewport1(getJScrollPane1().getViewport());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP2SetSource:  (JScrollPane1.horizontalScrollBar <--> horizontalScrollBar1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((gethorizontalScrollBar1() != null)) {
				getJScrollPane1().setHorizontalScrollBar(gethorizontalScrollBar1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP2SetTarget:  (JScrollPane1.horizontalScrollBar <--> horizontalScrollBar1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			sethorizontalScrollBar1(getJScrollPane1().getHorizontalScrollBar());
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP3SetSource:  (JScrollPane1.verticalScrollBar <--> verticalScrollBar1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getverticalScrollBar1() != null)) {
				getJScrollPane1().setVerticalScrollBar(getverticalScrollBar1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP3SetTarget:  (JScrollPane1.verticalScrollBar <--> verticalScrollBar1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setverticalScrollBar1(getJScrollPane1().getVerticalScrollBar());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the horizontalScrollBar1 property value.
 * @return javax.swing.JScrollBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollBar gethorizontalScrollBar1() {
	// user code begin {1}
	// user code end
	return ivjhorizontalScrollBar1;
}
/**
 * Insert the method's description here.
 * Creation date: (7/8/2003 5:48:16 PM)
 */
public ImagePaneModel getImagePaneModel(){

	return getImagePaneView().getImagePaneModel();
}
/**
 * Return the ImagePaneView property value.
 * @return imagescroller.ImagePaneView
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public ImagePaneView getImagePaneView() {
	if (ivjImagePaneView == null) {
		try {
			ivjImagePaneView = new cbit.image.gui.ImagePaneView();
			ivjImagePaneView.setName("ImagePaneView");
			ivjImagePaneView.setBounds(20, 20, 100, 100);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImagePaneView;
}
/**
 * Gets the imagePlaneManager property (cbit.image.ImagePlaneManager) value.
 * @return The imagePlaneManager property value.
 * @see #setImagePlaneManager
 */
public ImagePlaneManager getImagePlaneManager() {
	return fieldImagePlaneManager;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
/**
 * Return the verticalScrollBar1 property value.
 * @return javax.swing.JScrollBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollBar getverticalScrollBar1() {
	// user code begin {1}
	// user code end
	return ivjverticalScrollBar1;
}
/**
 * Return the viewport1 property value.
 * @return javax.swing.JViewport
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JViewport getviewport1() {
	// user code begin {1}
	// user code end
	return ivjviewport1;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}
/**
 * Comment
 */
private void imagePaneScroller_ImagePlaneManager() {
	((ImagePaneRuler)getJScrollPane1().getColumnHeader().getView()).setImagePlaneManager(getImagePlaneManager());
	((ImagePaneRuler)getJScrollPane1().getRowHeader().getView()).setImagePlaneManager(getImagePlaneManager());
}
/**
 * Comment
 */
private void imagePaneScroller_Initialize() {
	getJScrollPane1().getViewport().setView(getImagePaneView());
	//
	//javax.swing.JPanel jpRowH = new javax.swing.JPanel();
	//jpRowH.setMinimumSize(new java.awt.Dimension(20,3));
	//jpRowH.setBackground(java.awt.Color.red);
	//getJScrollPane1().setRowHeaderView(jpRowH);
	getJScrollPane1().setRowHeaderView(new ImagePaneRuler(ImagePaneRuler.ORIENT_VERTICAL));
	//
	//javax.swing.JPanel jpcolH = new javax.swing.JPanel();
	//jpcolH.setMinimumSize(new java.awt.Dimension(3,20));
	//jpcolH.setBackground(java.awt.Color.red);
	//getJScrollPane1().setColumnHeaderView(jpcolH);
	getJScrollPane1().setColumnHeaderView(new ImagePaneRuler(ImagePaneRuler.ORIENT_HORIZONTAL));
	//
	//getJScrollPane1().setCorner(javax.swing.JScrollPane.UPPER_LEFT_CORNER,new javax.swing.JLabel("x-y"));
}
/**
 * Comment
 */
private void imagePaneView_Panning(cbit.image.gui.PanEvent panEvent) {
	//
	java.awt.Point oldPosition = getJScrollPane1().getViewport().getViewPosition();
	java.awt.Point newPosition = getJScrollPane1().getViewport().getViewPosition();
	newPosition.translate(-panEvent.getDeltaX(), -panEvent.getDeltaY());
	//Make sure we don't display past the edges
	java.awt.Rectangle viewportSize = getJScrollPane1().getViewportBorderBounds();
	java.awt.Dimension viewSize = getImagePaneView().getSize();
	adjustLocationForEdge(newPosition,viewportSize,viewSize);
	if(oldPosition.equals(newPosition)){
		return;
	}
	//
	viewCenterX = (double) (newPosition.x + (viewportSize.width / 2)) / (double) viewSize.width;
	viewCenterY = (double) (newPosition.y + (viewportSize.height / 2)) / (double) viewSize.height;
	//
	getJScrollPane1().getViewport().setViewPosition(newPosition);
	getJScrollPane1().getColumnHeader().setViewPosition(new java.awt.Point((int)newPosition.getX(),0));
	getJScrollPane1().getRowHeader().setViewPosition(new java.awt.Point(0,(int)newPosition.getY()));
}
/**
 * Comment
 */
private void imagePaneView_Zooming(cbit.image.ZoomEvent zoomEvent) {
	getImagePaneView().getImagePaneModel().deltaZoom(zoomEvent.getZoomDelta());
	java.awt.Point desiredLocation =
		calculateULCFromCenter(
			getJScrollPane1().getViewportBorderBounds(),
			getImagePaneView().getImagePaneModel().getDimension());
	getJScrollPane1().getViewport().setViewPosition(desiredLocation);
	hasUserSetZoom = true;
	//
	//getJScrollPane1().getColumnHeader().setViewSize(
		//new java.awt.Dimension((int)getImagePaneView().getImagePaneModel().getDimension().getWidth(),1));
	getJScrollPane1().getColumnHeader().setViewPosition(new java.awt.Point((int)desiredLocation.getX(),0));
	//getJScrollPane1().getColumnHeader().getView().invalidate();
	//
	//getJScrollPane1().getRowHeader().setViewSize(
		//new java.awt.Dimension(1,(int)getImagePaneView().getImagePaneModel().getDimension().getHeight()));
	getJScrollPane1().getRowHeader().setViewPosition(new java.awt.Point(0,(int)desiredLocation.getY()));
	//getJScrollPane1().getRowHeader().getView().invalidate();
	//
	sizeHeaders();
	//
	getJScrollPane1().revalidate();
	//
	if(getJScrollPane1().getColumnHeader() != null){getJScrollPane1().getColumnHeader().getView().repaint();}
	if(getJScrollPane1().getRowHeader() != null){getJScrollPane1().getRowHeader().getView().repaint();}

}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getImagePaneView().addPanListener(this);
	getImagePaneView().addZoomListener(this);
	getJScrollPane1().addPropertyChangeListener(this);
	this.addPropertyChangeListener(this);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ImagePaneScroller");
		setLayout(new BorderLayout());
		setSize(354, 305);
		add(getJScrollPane1(), BorderLayout.CENTER);
		initConnections();
		connEtoC3();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (12/15/2004 7:50:58 AM)
 * @param rowDesc java.lang.String
 * @param colDesc java.lang.String
 */
public void initRowColumnDescriptions(String rowDesc, String colDesc) {
	
	((ImagePaneRuler)getJScrollPane1().getColumnHeader().getView()).setDescription(colDesc);
	((ImagePaneRuler)getJScrollPane1().getRowHeader().getView()).setDescription(rowDesc);

}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 4:04:48 PM)
 * @param panEvent cbit.image.PanEvent
 */
public void panning(PanEvent panEvent) {
	// user code begin {1}
	// user code end
	if (panEvent.getSource() == getImagePaneView()) 
		connEtoC2(panEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	// user code begin {1}
	// user code end
	if (evt.getSource() == getJScrollPane1() && (evt.getPropertyName().equals("viewport"))) 
		connPtoP1SetTarget();
	if (evt.getSource() == getJScrollPane1() && (evt.getPropertyName().equals("horizontalScrollBar"))) 
		connPtoP2SetTarget();
	if (evt.getSource() == getJScrollPane1() && (evt.getPropertyName().equals("verticalScrollBar"))) 
		connPtoP3SetTarget();
	if (evt.getSource() == this && (evt.getPropertyName().equals("imagePlaneManager"))) 
		connEtoC7(evt);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
private void scrollbarValue() {
		//System.out.println("ImagePaneScroller.srollbar viewpostion=      "+getJScrollPane1().getViewport().getViewPosition()+" "+viewCenterX+" "+viewCenterY);
	if(getviewport1() != null && getJScrollPane1().getViewport() != null){
		//This is to avoid viewport changes during revalidation events
		if(getviewport1().getBounds().equals(getJScrollPane1().getViewportBorderBounds())){
			java.awt.Point newPosition = getJScrollPane1().getViewport().getViewPosition();
			java.awt.Rectangle viewportSize = getJScrollPane1().getViewportBorderBounds();
			java.awt.Dimension viewSize = getImagePaneView().getSize();
			viewCenterX = (double) (newPosition.x + (viewportSize.width / 2)) / (double) viewSize.width;
			viewCenterY = (double) (newPosition.y + (viewportSize.height / 2)) / (double) viewSize.height;
		//System.out.println("ImagePaneScroller.srollbar viewpostion=      "+getJScrollPane1().getViewport().getViewPosition()+" "+
		//	viewCenterX+" "+
		//	viewCenterY+" "+
		}
	}
}
/**
 * Set the horizontalScrollBar1 to a new value.
 * @param newValue javax.swing.JScrollBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void sethorizontalScrollBar1(javax.swing.JScrollBar newValue) {
	if (ivjhorizontalScrollBar1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjhorizontalScrollBar1 != null) {
				ivjhorizontalScrollBar1.removeAdjustmentListener(this);
			}
			ivjhorizontalScrollBar1 = newValue;

			/* Listen for events from the new object */
			if (ivjhorizontalScrollBar1 != null) {
				ivjhorizontalScrollBar1.addAdjustmentListener(this);
			}
			connPtoP2SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}
/**
 * Sets the imagePlaneManager property (cbit.image.ImagePlaneManager) value.
 * @param imagePlaneManager The new value for the property.
 * @see #getImagePlaneManager
 */
public void setImagePlaneManager(ImagePlaneManager imagePlaneManager) {
	ImagePlaneManager oldValue = fieldImagePlaneManager;
	fieldImagePlaneManager = imagePlaneManager;
	firePropertyChange("imagePlaneManager", oldValue, imagePlaneManager);
}
/**
 * Set the verticalScrollBar1 to a new value.
 * @param newValue javax.swing.JScrollBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setverticalScrollBar1(javax.swing.JScrollBar newValue) {
	if (ivjverticalScrollBar1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjverticalScrollBar1 != null) {
				ivjverticalScrollBar1.removeAdjustmentListener(this);
			}
			ivjverticalScrollBar1 = newValue;

			/* Listen for events from the new object */
			if (ivjverticalScrollBar1 != null) {
				ivjverticalScrollBar1.addAdjustmentListener(this);
			}
			connPtoP3SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}
/**
 * Set the viewport1 to a new value.
 * @param newValue javax.swing.JViewport
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setviewport1(javax.swing.JViewport newValue) {
	if (ivjviewport1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjviewport1 != null) {
				ivjviewport1.removeChangeListener(this);
			}
			ivjviewport1 = newValue;

			/* Listen for events from the new object */
			if (ivjviewport1 != null) {
				ivjviewport1.addChangeListener(this);
			}
			connPtoP1SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (7/22/2003 10:17:06 AM)
 */
private void sizeHeaders() {
	//
	Component columnHeaderView = getJScrollPane1().getColumnHeader().getView();
	Component rowHeaderView = getJScrollPane1().getRowHeader().getView();
	if(getImagePaneModel().getSourceData() == null){
		if(columnHeaderView.isVisible()){columnHeaderView.setVisible(false);}
		if(rowHeaderView.isVisible()){rowHeaderView.setVisible(false);}
		return;
	}else if(getImagePaneModel().getSourceData().getYSize() == 1){
		if(!columnHeaderView.isVisible()){columnHeaderView.setVisible(true);}
		if(rowHeaderView.isVisible()){rowHeaderView.setVisible(false);}
	}else{
		if(!columnHeaderView.isVisible()){columnHeaderView.setVisible(true);}
		if(!rowHeaderView.isVisible()){rowHeaderView.setVisible(true);}
	}
	//
	double worldExtentHorz = getImagePaneModel().getSourceData().getExtent().getX();
	double worldOriginHorz = getImagePaneModel().getSourceData().getOrigin().getX();
	int worldPixelHorz = (int)getImagePaneModel().getDimension().getWidth();
	int selipHorz = getImagePaneModel().getZoom()*2;
	((ImagePaneRuler)columnHeaderView).setWorldOrigin(worldOriginHorz);
	((ImagePaneRuler)columnHeaderView).setWorldExtent(worldExtentHorz);
	((ImagePaneRuler)columnHeaderView).setWorldPixelSize(worldPixelHorz);
	((ImagePaneRuler)columnHeaderView).setSingleElementLengthInPixels(selipHorz);
	columnHeaderView.invalidate();
	//
	if(rowHeaderView.isVisible()){
		double worldExtentVert = getImagePaneModel().getSourceData().getExtent().getY();
		double worldOriginVert = getImagePaneModel().getSourceData().getOrigin().getY();
		int worldPixelVert = (int)getImagePaneModel().getDimension().getHeight();
		int selipVert = getImagePaneModel().getZoom()*2;
		((ImagePaneRuler)rowHeaderView).setWorldOrigin(worldOriginVert);
		((ImagePaneRuler)rowHeaderView).setWorldExtent(worldExtentVert);
		((ImagePaneRuler)rowHeaderView).setWorldPixelSize(worldPixelVert);
		((ImagePaneRuler)rowHeaderView).setSingleElementLengthInPixels(selipVert);
		rowHeaderView.invalidate();
	}
	
}
/**
 * Method to handle events for the ChangeListener interface.
 * @param e javax.swing.event.ChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void stateChanged(javax.swing.event.ChangeEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getviewport1()) 
		connEtoC1();
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
private void viewport1_Change() {

	
	//
	//-----Begin Fix for Jumping scroll bars
	//This CRRRAAAPPP!!!!!! is here because of an apparent bug in ScrollPaneLayout that
	//does not take into account the scrollbar sizes when calculating the size of the viewport
	java.awt.Rectangle availR = new java.awt.Rectangle(getJScrollPane1().getSize());

	java.awt.Insets insets = getJScrollPane1().getInsets();
	availR.x = insets.left;
	availR.y = insets.top;
	availR.width -= insets.left + insets.right;
	availR.height -= insets.top + insets.bottom;

	/* If there's a visible column header remove the space it 
	 * needs from the top of availR.  The column header is treated 
	 * as if it were fixed height, arbitrary width.
	 */

	java.awt.Rectangle colHeadR = new java.awt.Rectangle(0, availR.y, 0, 0);
	javax.swing.JViewport colHead = getJScrollPane1().getColumnHeader();
	if ((colHead != null) && (colHead.isVisible())) {
	    int colHeadHeight = colHead.getPreferredSize().height;
	    colHeadR.height = colHeadHeight; 
	    availR.y += colHeadHeight;
	    availR.height -= colHeadHeight;
	}

	/* If there's a visible row header remove the space it needs
	 * from the left of availR.  The row header is treated 
	 * as if it were fixed width, arbitrary height.
	 */

	java.awt.Rectangle rowHeadR = new java.awt.Rectangle(availR.x, 0, 0, 0);
	javax.swing.JViewport rowHead = getJScrollPane1().getRowHeader();
	boolean leftToRight = true;
	if ((rowHead != null) && (rowHead.isVisible())) {
	    int rowHeadWidth = rowHead.getPreferredSize().width;
	    rowHeadR.width = rowHeadWidth;
	    availR.width -= rowHeadWidth;
			if ( leftToRight ) {                                      //ibm.597
				rowHeadR.x = availR.x;                                //ibm.597
				availR.x += rowHeadWidth;                             //ibm.597
			} else {                                                  //ibm.597
				rowHeadR.x = availR.x + availR.width;                 //ibm.597
	    }                                                         //ibm.597
	}
//System.out.println("availR="+availR);
//System.out.println("viewportBounds="+getJScrollPane1().getViewport().getBounds());
//System.out.println("hsb="+getJScrollPane1().getHorizontalScrollBar().getSize()+" "+getJScrollPane1().getHorizontalScrollBar().getInsets());
//System.out.println("vsb="+getJScrollPane1().getVerticalScrollBar().getSize()+" "+getJScrollPane1().getVerticalScrollBar().getInsets());
//System.out.println();
getJScrollPane1().getViewport().reshape(
									availR.x,
									availR.y,
									(int)availR.getWidth()-(int)getJScrollPane1().getVerticalScrollBar().getSize().getWidth(),
									(int)availR.getHeight()-(int)getJScrollPane1().getHorizontalScrollBar().getSize().getHeight());
	//
	//-----End Fix for Jumping scroll bars

	
	//
	//
	if(getviewport1() != null && getJScrollPane1().getViewport() != null){
		//System.out.println("ImagePaneScroller.viewport bounds=           "+getviewport1().getBounds());
		//System.out.println("ImagePaneScroller.viewport borderbounds *** ="+getJScrollPane1().getViewportBorderBounds());
		//System.out.println("ImagePaneScroller.viewport size=             "+getJScrollPane1().getViewport().getSize());
		//System.out.println("ImagePaneScroller.viewport viewpostion=      "+getJScrollPane1().getViewport().getViewPosition());
		//System.out.println("ImagePaneScroller.viewport viewsize=         "+getJScrollPane1().getViewport().getViewSize());
		//if(getviewport1().getBounds().equals(getJScrollPane1().getViewportBorderBounds())){
			java.awt.Point viewULC = getJScrollPane1().getViewport().getViewPosition();
			java.awt.Rectangle visibleArea = getJScrollPane1().getViewportBorderBounds();
			visibleArea.setLocation(getJScrollPane1().getViewport().getViewPosition());
			getImagePaneView().getImagePaneModel().setViewport(visibleArea);
			//if(getImagePaneModel().getViewport() == null || !getImagePaneModel().getViewport().equals(visibleArea)){
				//getImagePaneModel().setViewport(visibleArea);
			//}
			
			//System.out.println("---IPS VP Change---");
			//getImagePaneModel().setViewport(getJScrollPane1().getViewportBorderBounds());
			//getImagePaneView().repaint();
			//
			//
			if(!hasUserSetZoom && getImagePaneModel() != null && getImagePaneModel().getSourceData() != null){
				SourceDataInfo sdi = getImagePaneModel().getSourceData();
				int boundsXSize = (int)getJScrollPane1().getViewportBorderBounds().getWidth();
				int boundsYSize = (int)getJScrollPane1().getViewportBorderBounds().getHeight();
				int zoom = Math.min(boundsXSize/(sdi.getXSize()*2),boundsYSize/(sdi.getYSize()*2));
				zoom = (zoom < 1?1:zoom);
				getImagePaneModel().setZoom(zoom);
				
			}
			//
			if(getImagePaneModel().getSourceData() != null){
				//java.awt.Point vpPos = getJScrollPane1().getViewport().getViewPosition();
				//java.awt.Dimension vpDim = getJScrollPane1().getViewport().getSize();
				//java.awt.geom.Point2D.Double unitOrigin = 
					//getImagePaneModel().calculateImagePointUnitized(
						//new java.awt.Point((int)vpPos.getX(),(int)vpPos.getY()));
				//java.awt.geom.Point2D.Double unitend = 
					//getImagePaneModel().calculateImagePointUnitized(
						//new java.awt.Point((int)(vpPos.getX()+vpDim.getWidth()),(int)(vpPos.getY()+vpDim.getHeight())));
				//double worldExtent = getImagePaneModel().getSourceData().getExtent().getX()*(unitend.getX()-unitOrigin.getX());
				//double worldOrigin =
					//getImagePaneModel().getSourceData().getOrigin().getX()+
					//getImagePaneModel().getSourceData().getExtent().getX()*unitOrigin.getX();
				//((ImagePaneRuler)getJScrollPane1().getColumnHeader().getView()).setWorldOrigin(worldOrigin);
				//((ImagePaneRuler)getJScrollPane1().getColumnHeader().getView()).setWorldExtent(worldExtent);
				//
				//
				//
				//double worldExtent = getImagePaneModel().getSourceData().getExtent().getX();
				//double worldOrigin = getImagePaneModel().getSourceData().getOrigin().getX();
				//((ImagePaneRuler)getJScrollPane1().getColumnHeader().getView()).setWorldOrigin(worldOrigin);
				//((ImagePaneRuler)getJScrollPane1().getColumnHeader().getView()).setWorldExtent(worldExtent);
			sizeHeaders();
			}
			getJScrollPane1().getColumnHeader().getView().repaint();
			getJScrollPane1().getRowHeader().getView().repaint();
			//getJScrollPane1().getColumnHeader().getView().invalidate();
			//revalidate();
		//}
		//System.out.println();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/21/2003 4:24:14 PM)
 */
public void zeroView(boolean resetZoomPref) {

	if(resetZoomPref){
		hasUserSetZoom = false;
	}
	getJScrollPane1().getViewport().setViewPosition(new java.awt.Point(0,0));
	getJScrollPane1().getColumnHeader().setViewPosition(new java.awt.Point(0,0));
	getJScrollPane1().getRowHeader().setViewPosition(new java.awt.Point(0,0));
	//
	sizeHeaders();
	//
	getJScrollPane1().getColumnHeader().repaint();
	getJScrollPane1().getRowHeader().repaint();
	getJScrollPane1().revalidate();
	
}
/**
 * Method to handle events for the ZoomListener interface.
 * @param zoomEvent cbit.image.ZoomEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void zooming(ZoomEvent zoomEvent) {
	// user code begin {1}
	// user code end
	if (zoomEvent.getSource() == getImagePaneView()) 
		connEtoC5(zoomEvent);
	// user code begin {2}
	// user code end
}
}

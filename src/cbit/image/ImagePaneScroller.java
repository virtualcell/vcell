package cbit.image;

import java.awt.BorderLayout;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (9/3/00 3:14:36 PM)
 * @author: 
 */
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
 * ImagePaneScroller constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ImagePaneScroller(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * ImagePaneScroller constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ImagePaneScroller(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * ImagePaneScroller constructor comment.
 * @param isDoubleBuffered boolean
 */
public ImagePaneScroller(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
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
			ivjImagePaneView = new cbit.image.ImagePaneView();
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
private void imagePaneView_Panning(cbit.image.PanEvent panEvent) {
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
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ImagePaneScroller aImagePaneScroller;
		aImagePaneScroller = new ImagePaneScroller();
		frame.setContentPane(aImagePaneScroller);
		frame.setSize(aImagePaneScroller.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
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
	if(getImagePaneModel().getSourceData() == null){
		if(getJScrollPane1().getColumnHeader().getView().isVisible()){getJScrollPane1().getColumnHeader().getView().setVisible(false);}
		if(getJScrollPane1().getRowHeader().getView().isVisible()){getJScrollPane1().getRowHeader().getView().setVisible(false);}
		return;
	}else if(getImagePaneModel().getSourceData().getYSize() == 1){
		if(!getJScrollPane1().getColumnHeader().getView().isVisible()){getJScrollPane1().getColumnHeader().getView().setVisible(true);}
		if(getJScrollPane1().getRowHeader().getView().isVisible()){getJScrollPane1().getRowHeader().getView().setVisible(false);}
	}else{
		if(!getJScrollPane1().getColumnHeader().getView().isVisible()){getJScrollPane1().getColumnHeader().getView().setVisible(true);}
		if(!getJScrollPane1().getRowHeader().getView().isVisible()){getJScrollPane1().getRowHeader().getView().setVisible(true);}
	}
	//
	double worldExtentHorz = getImagePaneModel().getSourceData().getExtent().getX();
	double worldOriginHorz = getImagePaneModel().getSourceData().getOrigin().getX();
	int worldPixelHorz = (int)getImagePaneModel().getDimension().getWidth();
	int selipHorz = getImagePaneModel().getZoom()*2;
	((ImagePaneRuler)getJScrollPane1().getColumnHeader().getView()).setWorldOrigin(worldOriginHorz);
	((ImagePaneRuler)getJScrollPane1().getColumnHeader().getView()).setWorldExtent(worldExtentHorz);
	((ImagePaneRuler)getJScrollPane1().getColumnHeader().getView()).setWorldPixelSize(worldPixelHorz);
	((ImagePaneRuler)getJScrollPane1().getColumnHeader().getView()).setSingleElementLengthInPixels(selipHorz);
	getJScrollPane1().getColumnHeader().getView().invalidate();
	//
	if(getJScrollPane1().getRowHeader().getView().isVisible()){
		double worldExtentVert = getImagePaneModel().getSourceData().getExtent().getY();
		double worldOriginVert = getImagePaneModel().getSourceData().getOrigin().getY();
		int worldPixelVert = (int)getImagePaneModel().getDimension().getHeight();
		int selipVert = getImagePaneModel().getZoom()*2;
		((ImagePaneRuler)getJScrollPane1().getRowHeader().getView()).setWorldOrigin(worldOriginVert);
		((ImagePaneRuler)getJScrollPane1().getRowHeader().getView()).setWorldExtent(worldExtentVert);
		((ImagePaneRuler)getJScrollPane1().getRowHeader().getView()).setWorldPixelSize(worldPixelVert);
		((ImagePaneRuler)getJScrollPane1().getRowHeader().getView()).setSingleElementLengthInPixels(selipVert);
		getJScrollPane1().getRowHeader().getView().invalidate();
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
				boolean bMeshMode = getImagePaneModel().getMode() == ImagePaneModel.MESH_MODE;
				int zoom = Math.min(boundsXSize/(sdi.getXSize()*(bMeshMode?2:1)),boundsYSize/(sdi.getYSize()*(bMeshMode?2:1)));
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
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA9FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DF4D465156A46AE2D9C1BED3122253B2CE29BEB6A32DDBD32AE5B459A37E83B6AE6917198F653B5FBE477002232DB5A263BAF13G09868802B41E860DB5C01098C8C8CC88C172B37C8C0CA4A4C398C2A073E6664D64A5EF66CD5E3CB181516E3D5F4F1B1719B78966141CF3F9735D1F6F5E6F5E7B5D6F67BD04ED6F4EDDD7E05BA688856B847657D4A088F98D02704A5B7B9F6238665D59A502696F
	BD00ADC2E5D191BCCFG4DFEA13B24CCE80E95C3FBG5AF97D59A53F055FDB0543312B8760C78ABDAE205DBF7C355D136BF94A4575ECA06DEFDEAC0567A6G8F607019399B755FF7311C6153981EC301CD902ED6A34FBFDE2CE0385540D38C38E6GE7B5657743139AF0F3CBB3639B78338CE14D931AEDA376E36A13C39613DD411E15422F8A5277202E17091DA8D389EDB7G8E3ED0583D7D7770EC6D9E89FE6C734AFA2D9C9203D2CB0B839F5DE2D86A75E92A22C81AA014FE007DB4DDAAA6F20230847A18CDF0
	129F7B6E065FC3GB7FC40710F8EA3BE87FE2F81A85D4B47F3F8F7AB33577D59C342127AD051669463295ACB47615C9B9FCFA43C5C76B6717D66BE74718C20E582AC824883A82A49AE2982789D097976339F40335D2BF72A21109AEE0FC4BA943F76096A1794785D528294066B11FC2A6697849A577F3ED8E3E14FE4C0658AAFFDD9B96A130C83BC4619BD778BB9FF73AD5B868B1F64E4FCE6EB2D213EF05544FDC2F53726542D57CC4C41093A4F89E65D0B3F3234F01D056E3C685242992C1FB9A65DB9B5986B67
	C88EAC045FA877865ACA70AF92FC6507F1BC4D47910247F69B5066FD48F3433D095B62AC3ACBA82B3B5D528F33FFBC53D6DC4BEC28CD34E5E4847BF656D21DEF9936FCC170B71870541647C91EE27B8120F976E517507CAAF7637CAC5D1FDDD28D508660869887188510311F67586059FD5718E35D2226F744E4458F930A4A1677ED0727B42EEB22A39C554530CF6A93B5D974AAD2D4E0BC6F75218F3C9364F6115C77010E63D2D852C4DDD643503B9812F4C90BD21EFDFDDB10C7138855A507A4D0900A883C5F26
	3E2D70D44428FEB66297F5A94EC169C77AB0A61F0AFD50A3F4G3F29DF0E1EC7FFAD067167822CB67C70B0696F142405A5252545292AA331088BC7A23455211FFBC96DA8025F95F55C476D7D08CB03761CBA5E4FABFDEF1A7A314BBEB4DF54AE73B976EFFEA85E42FC60FF9AE0A5C0D1BD4C79FA1EC3AF39F98E59B77EC028F966B64B9CF26BCB8B6D75B4F7866A936752AA22836DCC83DA8640E20095753C6E0F7A5D967389EDC94421EDE8EBD5FD1C763485DFF2AD791A250E8DA966F1A11BCF9C37EE127EB799
	317C2B8B984B6283E09F007DG0FC14D39CA1A539DE2D462D95C25C182CA7EF8C418AFC62A6109EB527BGCD4C4692E68F2E1B1B899F764B4716C6E486C0DF9AC0A6409200FC34238156C0G9DE08EC0D6837779D343F3ADFC4175ECCC5013EA5C6FB1BB2F25B6B7DDA3DFF26CFE15E42B594EC2131DEFCD525766EB0863E4F9B19550D87F5E0DFA24CF2530EE17A2D2588FBF220C36470DF6337A94D08D19FD849F01FA568985A4EA609B5DB8AED157B5599B5325DEC95765F0B0CEBFCCF4C5E35EC83C94F2DDF6
	B70ED59D8B63F603619CEE2C85BEB59C16C85299A20CFE0A54A2C911C2E0F4279A8B6B94FF66BC6E53C6244B2C961EBD3FDE40DA2644302E523D4FFC91639151E04F23DD4652AC34B6E21D9A3ED4EE7251CA48BF5C4315812D2A1157183177F90D293AF22F30E8B0E233DAB3E75F73F5DBDAA31D6B339B63B5065AF42C5F5AF6A729098C9F25365E9AC63FDAEC4BC8AEED95BEAF5BE82677495218C11B75812963000A281ADEE760DFAE429AB724EA7295B52C0B8A6D34C3546AA87D167750771FCA1AAE7B4CD4DE
	BBD68C207D8BE0DCBF8348EB6435E3F1BF52F97DE6C523936DA5A1849A75F303E476F3FEA375F375C759A52DGD7BFC21EF0F09B233B202DFCC4F930CD69E3B227E7C1BBA7093EE31853D7C23B28A9CE2736BEF8218C73D19A0DC9307462E2F54A481905975ECC2075317C21742C8B18BF0AE91D0BB4214E9F0D70FD7DB6262F06BD6DCDD4B7659BD7B85F42A63A4EACE1FCC233192FA94479DA1BA91F2B197225B13E4A0348778B03EF9160178164B753352B8C686F987DBD9266FC4AC1CACFBBC87B1BFD505C5F
	951573ADB8C87BFB7AA0DD87F9FF142FADE21CE398DFF18BDDABA76ADDBF4A792ADBE8BFEDADD4EFFA0BF93CCF9AFC73D9BF56FCB7E91CEFB123673227F008725139735A9E4CB3F2469996BF9547EB23E3D013DADACE62CCD16839E689591BA0FF43A1FEFE913C185F8A3453G669C62F37F1FB772734B2C118CE15E1FBED3186ABC76G33E35921785CDF736CD8A11DBF75C4E7F9AB64FCAB1FBBC7864CFB2876E8D47549E47B46F26E2581F3ED20385786DE07E7C056223A830A77B84F4FDF8E203D383FE3E866
	0F13975F91A6BB735162C17D22C87C3C575A4A7D72F781740B8E6D19G73G68BE7A36E0A963DD8838B5GB507911F9D44DA0C39C87D67BB407D17FE659E41F35B1D36B7D278AFF2187AAD7DF05CFFD9E79B8B293EBF17393E45C097BE667A1E17D19F66AA65338FF33EE6600998FCB78EA39F66AA65FB111C8F50C79940138BC06B60E98571F2D856E5D1112FC855FB289F6B3B5FC5FCE2CD9EF418B87B58996B2E2DEDECCCBF3B7FAE21758F17ECD6E75E991B07EC65C768D85B0E44476CFFE639ED9C005A7917
	8A37F3A670E49D61F69607AA51E7E23896A08CA9DE9D8FEFADD8F894D7F8E67B5E5E8F4D36E348873B191453C26C900C041F17BCA12EBF9FF4B734F17DBF8961DD4295D58D5562BF898674830ADA407370EF07EB13EC68E702CEABC9E2843521B24CE32A007EF9GF4CDF950EBDED3F25A68D923389D1E6D5C4E95C14CE93E9ED25CFDE14C85FDC80E52F83D5AEE0E57028DB3AD63B5743D593606F6F606E80F472BBBEF394D1F476375DFE16E2FCC6049B36CB823621E4DB7A406E1104CA78BF7EFB559D6B758C9
	2818C7342F3BD5BE6E0A8ED887BAF8DF2F48A8E73116B3B9B94265E602CC16A1F77BB06EAB1256F8A3567BC60DD803CCC3A7177B56E8BBBCC57FF0AC2AE3A92A0D7FA40BA4355BDF0D8537D69C53EB497632365D60622940467DE8906D08927DDED1A3FDD41BF26E97069D8A58B02F134F597BB40E4F815CB6BB37EFD9EC0B11DB8AA457A7E298FEEA063E1787F7D94F5DC489E2845D53BF17F43F6234474FF19EFB72190E5EBB68E49D516D745E215040FFC170E9A6BC9D53FAAF0F55AC202D35733372C58F6676
	40516C12F100F4008C0045GAB0E727563460BF845B969DD04A32C437282411E308967F78217D0AF6C1BD97B50A5749759B78B5C8F2352E4F7DCC547D279E19FD99F4A0F5171969878E6022FB261299F9E8970BB4B7DC01BF50C1F833F9DC47DEBAD74CCFEA63B1748159D3FF6B9DA8B76741B6FDF0B361C3495DA5482D233D95F5D4763350046EE1397772169004F844882488148BF4E633608645624F16377C2ACE0F4CC7364697962077217758515BBB9BCBDFD6E616969DBB51218EB95A7A676511C5026F23F
	CE122BCDE88FA44851B879FD38275401168E1081108530822048817BB4870F57464143D3450BD4B8175A893893F0C668CCBC439A36FECF195E989FCC12FBA721FD42E1A59788CDCF1F1EA417284FE329EFD7F82ABBF1EBFD8DD34A0DA7682335BDF8F407E0395F27D59E6B3D4077372B3C45CCDF3A036FEFFF68A3771B504E85D86B607577F9BF66C659C958BF825881BCGE9GF38132CE725C708CF6CC159B6D7C0244259ABFAD92047AE1F5129F2EADDE7F1BA417B887F2CEDA495D2BCEAFDE8FCDA93746D21F
	9A191E3E4F2714ABBDE5A557B8BABDFFF6A649A57A334AD25FEF26296F8DEDAAFD2D167A46E25353F7F312DC22BED702BEFA2F3F1A1CE395205DGC0F17F64C15CBCE8AFBC45576DABDE5CE3679A6DB452467B825AAE75DA5DF7FFD948BDE53D46D09DBB7FA8BA8AD360697C8FF8D25F2706BC567729D10F75FD6AE54F960A07BE4CEF0FB87DC64F64772DDF75D85D3752984D10F80C569AB1E98C8A062FBEB67D7EC9600F6137D4C0F564DED79DBBC85C165A3036EB3130AE07A462EA3AE6C86378DEGAAF3CFAC
	CC4BB6FABB1A38779278BE616DG66EE0513577CF745FAA139669F754B3A2A71CEDC1228F58582C6416F0DC8BE9E276D5E54B93343EB1DB36FFB2DF3E60FF7721C2973264A996A234737EFA672512894A51B983B280B0C76220E6FAB126E63CF0BA1C960774AD5CA8963B92A2B1D878C3DE1DF6AB146FC56E37C4C07F7EBD1B526792444F15C64ABB7E871455321CBA1B407725CC264F5D18BCAC9FE7886112734D4725F762572959D6B3FCE98EB3D017FCE480CEFB47097439C5F2D2B5DF5C66E3F6A65353799F2
	4A691CD843BC4E641AC64FCE3B48FBF45D19F8265AC770B7B8934FD48BFC7CCCB593E82B0D7C7D8FFF6A582C74DB4766D5FF6A587C52BFF5ECECD26A5814C8D34766CD896F8D3D32DF56581B856A635D2A597771779D392A597771D8FDB7E2469FB0704A28391F86835F2019719F99787BE3E6FC13015FB0086F1E9471329A5385E151A79807F78679BD158B5A8A40AC000CCF68FD952DC9FFBD06638BE8A25E0811E2B167B44A66F938ECB134ABGDA8186G2E1EB66E164EE21CCDBB5EBAAA7B9746BB17BCE82F
	85A86F4AAEE986F0F771DC589158E84EF9B637691AD16243B5E39CF867F471353E5C0F387950DEE660B4BFDFDBD6F671352546AF98795EE87A7DA35F75DFDB1E3D2EBA685CBAB64867D6EBF7F6095E4D63D8672990CC67913C8EEBE9B92E497E8EB168A4D9C14FA2378E96B3794CEEFE96D9C87A4C03F6BE4036B37C8EFDFC973FC34F7C6EDD426CBF31FE87116B7A5CD6F3065DC91C095FC92405175B284E57C52EF3A650334E107B98F11B298F4CA71F0D6E85A9FDF8829D6B63F385E67A1C89F43477318972CF
	CE20230F9E93A81D0E6D27BB795872FFBADFD09ECCCB793DDBE98F9D53FE76CC4F3B23E07E6E4F8A69589E13785806FA7858DE1392477638E036EDE7A0F1ECFBA6504F859247B6D17E2B014431F1F9BAAFABFD78ADE252FDEB372A483E4BC2EE8F76F1084CB13C1F1F5B436773BEBF6EBBAC6EF7192440E4CF11F907F733D53D59A5193D44A63F1D59636FCDDEC7787A403FA199871E19GBBD9FBEEEFFC8D21F349B9B83DEF637A27A9774DAFADC75740DBBDB8F75674A62E0D37933C60CADC9BFFEC4C5FD220C5
	DC7C5C1F9D441A39845A2B810A4FE617D48234818C1C65B5731E40FEE10AB33FCB0DB8E101D5CCDBB2BA3E4D62743E99FCE4831FA78FBC7CFDE141D76C295F231DE57303BD2BDF3823G1FF4FC120C63DB84349CG218F5684009AGFB9F9F5F2AG3B1F44ADAD6CE0C55F90F983E9EC10E5E9AC0E46450A0E6D09C96F0A2D003D4B72633AED3A3E29B52DE535FDC7FBDC0E4EF667E0EFE7CF175359515EB3586EBC57FE3E57704D5CB1731DED736BB7D9FE1F01606E23BED158B37F2DBB8B288DDE7F01C9ECB851
	5563686FBA6D323202561F077D7C1D72BC687BDD01FE6FD3AC703F55C72CF0B9FD785DF19EFE2647467273D29E670A7B6E966C4F5AED6F2798CB65B976FE7CDC3C56176A1505B49633BC02B1273EE37A3DF8707A2F3FBF3F2EBAE80EBE9FCC3D077D7720759EF6C5507A8C75D2F072F3757F84D3FF47741B2079BBA6BA47FEBD0A6BC28644A58BE085C0BEC0191B4F2FDB06705DA93D99CC1CDBC77DC15CC36357213AF11612A7B9CB49D3FFD37A050C5F8CF8D5DDD7C358BF47274998AFDD0D90A43BC31051C71A
	9C9C52A9AB43FFDDA677BED2002059B733D144554038CE98E32B9C5E8B4FBEF1B879D48C48EECD0D40F2F819E2490BBF16B37F60B7570EA1686F2AFB727787F43EBCF541BC774B6ED4EC3C4EA77227337E6638935F575C21631A79B460D782141D4FAE2986E885F0818C81DC85C88748GD8843084E085C0BEC0E9BF2C91G8D7D5C87FBC677CC62833699A4C156C499434CFCDC28250E736B5A54F15E2A116FC5A0BE2E4B91F6EEAA5770BC228682D14948279DDA1901F3C43B55280C87AAC60BEEB24F859AA2A6
	D795453CB63E0DA01F2A7482E3B8C84F81CBC98DBC51CF57DA0AFB1E60BC8CC77D7DFFB14198737723BBD3798AE643C92847DD5EE1B871394420A17383BD351F9E502776535F6B567361D1BDF9BEAC562D6743133AF9BED05C3F68D88B0290DF3741374C401DB7F04BC91F08BBA77B75A1837F82318B71A7A4D448717FC96C9AA3B8B72FD9A65CF906FBCD47D84310E16B48E5576AF8F7951095053673890F9C06509B7EA808210DF2C4B4593D5E40215D74ECA36EB2FD9BC3CC27780A710DF1BCB7DD207F8F6789
	DB8FBCB3D7896B221A305978C240C18C487D74C0CB618BF7975A04A207301E8644A19421D79D42BA62F407D0888199479532E8A2307735FFB04F7F35B77BD22E7DB3AE3079FFA1F17E7FF69D46F2996057DC60F3B8D87461A4F9599B929585F666A36DE1BF66E847E5DD0A2F97956B307FA867E1C4F60F378E6D9EB6497CBFD0CB87882FF35794CD92GG8CB5GGD0CB818294G94G88G88GA9FBB0B62FF35794CD92GG8CB5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4
	E1D0CB8586GGGG81G81GBAGGG0792GGGG
**end of data**/
}
}

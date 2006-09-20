package cbit.image.gui;

import cbit.vcell.simdata.SourceDataInfo;


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
private void imagePaneView_Zooming(cbit.image.gui.ZoomEvent zoomEvent) {
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
		setLayout(new java.awt.GridBagLayout());
		setSize(354, 305);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 0;
		constraintsJScrollPane1.gridwidth = 4;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJScrollPane1(), constraintsJScrollPane1);
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
	D0CB838494G88G88G380171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD4D4671528E8CD4404D4D4D00CC64D56E4C9EBCF6DAE5D4D1EE39BF30E091E2E595AB4B9CBCF5C563424A7291EAD3B6B691A6D47000A459F2CB621DBCCF0CD9493CA7C21554312C684B71212B042B87C884E3C19F9B08FE666CD5EBC64A7A8FB6F777366B173861413F04E754DF7FF3EFB3FFB6FF73F1F77A4C75EDC36AB5FF604107CDD047F1D4FA7E4EBB5A1EDB73EFEDA60D2C34EE2E27ABB86
	F0081031C2F8AE83DA66F2E7F189A98D97C1FB8D34EB217DBA7CBECC0E06C74F410F84FA9CC0BB56736A4109751CEB96FA7650F61EE39FBC8FGE4838E1F05AA6A5FF72D1463F3B8DEC03E0D104D14676435B20E3B82EDB7C0AA40CA1571BF8E4F3DD4734C1A8B1C2F6B01D5A46FE94D369F7BB175A920AC4CE4AF72E7B979E5FEEE98F5592F6D63B2C350DE8D0003AFA0A7DF7B9FF8565A7BFB7F6C75A8FA159214FA651A1AC6FC5825105C6A5554C0C0568871FD0A7D24DFAB22F204D4C2FBBB45BD5D0DFD2F02
	5FEB811EB37095BE44D7EB4E62A6G1FA646737E49DAEEEF7BE7FFCBCAA3CF6EBD18E0BC239A9B47CAADBA1E403B1E025FD15FC76C6863ADC0AB0CB80BCB81EA813AGC681FECFE3BE527C8EBC6BBCFA3D9A8C2A213AF078D24027FD2C7A64G7C2E29818A473548DED5739142626A3CD6E9E14FC44064FCF7AD47FC326A1C08F1D64BD7C94EB7965876D878A4E755E736348873C5D6A46A9326FBC9C25D9BA363F3F03C6EB6E256CDDEBA3BE717056E32E74F6E59427B49B3692E56B156FF3E0131BE823FD18EA7
	8E43B7D17C10894F72F1934D99EC2783ED27AEF2237D00302529F0B9A9B93350528F695FC9313987188DC3833136EC22F937F200697C2DE14B0FA97EB9931E59D2C571585EF753D91C8D4072EB34856727EF50D91C84108E309AE093404BGE4C86458934D6F5DE10E59A5CD3FB42084FCB021186C287DB5F84AC33AA6B506A23A94724ACEC9D3A4CFC00E904EB3530EBE700C133B17663E97F4FCA807E4CD5295B5843DCBC1D91735886319E7BF04BC1ACC298EA5A80302E01808FE975B8F43B3A0C574EBE11F24
	4BD18EC6DFE147185C141C50A3F4G3F19DF9EE9C5FF154378AFGDCB17C7046D56C6FAF32961283B5B5CD2A5ABF90F660C8C87A907AF98D2D9DDD70DB9F92BEFE5801389CE867997DDC3B7A9BD3BF8D0A974D1734E1B1479AEEC071A6C5434E628A008600AE0051E111C3BFEF97B954307FB1D2797D7916B9148A791CB94CF2E74DF06CDCEA21BA504E9C201191E7F1B9404591D1771F6DEE3718CF8D4371738CEDAB627689DC0585DFFCAD59E629E3CF02F9DC406713406D1A207F83C6AC8FBBB116EE9857A8C0
	660808C16A55F21653172408AC3238D9038414FD5108F0DF78AA6009EB52F100735C463D5C9EDCB78FD2BE6CD70C6DA5AA33867465G94FE66AC2E84F0G0C822C8458881087D0B6AAFCDE6DDBE6618B21E7FF0C1EC463BE466D3C135AFC7E8E7962E3670A3355ECE701494E5FCE505741BB0863C4F9B19930D84FEAC1BD72CDB924B748E1B964039F91CE1B5B02F6737A54239AB273A8BE8C752C9E8AC84440E7346038A4DD57944F00AE374A3A2E04FA237407282E4800A79CAD05C2572A969C2BBA98426D8647
	BD560235402B06C2B2CDBAC304533FC6EB119C1003E0F43DBA9052997E1B2D38CF6B1707F9AD7CD66BEE0235ACB000F5156DFDD6CB980F3086FB9EED98CBB3C99B45BA3541583B51B98DB85C437961B9BAAAEA4C60F1D1E35E9AF9042CF505EDD6EBE67A43775BF2C659DC5FBE9A2DB14C26C79D56362F26B51163A34C567BC268D70BED994D254364D649FEB35D29480386AD6D9DDA478195D6B57D0C015FD608B54E2FEA4A089A5225G6B7412241DE174B9475077B7E5CDD73CE62A289DCDEDE8FF49ADE771DB
	G3537C46D283C06F4D13FF95128C7FBE98801467C1CF1436CE747AD6667A4F82681EC3E05BC5FF39F6174ACE82F63BC58E6749C0F20175DF696D75F0E25CF778AFA83503ACCF4E66B6F1CA5180F722783B2AC3D38D87D454819F267EFE2E8CE1EBF0CDEC18F888153BA377EB66ABCAE0BFDFD006BCB664F4C5BCCB7637B2141F7E40C2DB315E30C2FFD4C4C1759A37852B8DF96674B61FCC3C9485755AB78CA21FD9A40114456AEBFBC5FB07ABB6197FC6BB8BDA7097537BD495C5F8B86DF49B456DF45B4368E0A
	7E985F430AF10E61FC6EE9EC2D9C2F373FCF70A571FE5227B13D39534C63BD57AF780A2633FE2C79FEEA7015CFE7748B7C59BE1D713139730BD318E7740C53A75D14062AA203D013EAEABE421992E0671ABFBA0BB8FF4AF4F1FE11BAB03F57E11FGF953455CFFE63FB83F24752FA2B9AF1D591368BC36EF863363740C685C4F5BDE371B4D1F856D28B38C342499E26E2CF01A77D0F511086AD568760D67DCCB1B39B6B0DCDB5B2F615923E8913D910A7710484F6EB6349777F79C4D7D71D1779BE422B39FAB9E4C
	AF4F5C90673D349942AF65EE744BC6E8EF8558894076513F24F864BD128CE7BE0064E4441F173096E3AEB27FF94F897F250EBCCC5C7397590A9378EFFDB273DBEEF254FF1537F76CE27A8A3CC2DF79CC0879CC216F229775E12EB23EC78C3ED460D9EF707D0472398D3EA6F99F779119853984406A60B7A8DE89A93AA28514913962947363D97B1B080F2D492EC69327131F31169F3E484734712B4BC96D588D1B5519F776C13FADBC0B0DB5FDD6F44CE973BC852BG181D3BFB041DDB01276CCB424EE0CFB96613
	942A8288C10AD7C443DB8596DE46951E5B7EEE6B9F4C36E348DDF6AE55E4A1763FB092F1DEFA52506FG5DA9067E4ADE3CCB98D155E0957E93E3C0A7201885A28F3F55D795E7C3A797EC321224C6B09BE6760A182E837DBB81581A72DA27F9CD291E4D4E9AEEFC4E96F6FEACE1CE0B751061CA7C188B3ADF0930F87DEB1DB9DE59FBD2AC63657FCA3AADE5B6BFC34C0E46ABE93927A0ED3908D733DF782B709ED8A76F91F67C0D9F77ECDE3F9402C1F21FBCFE72304936B32EFACA45BCE2FD95A9E25CBA7413E574
	6574221C45DA4E653EEB48ED851932FB055CCEAF6EAB6256F8A356E9FDC62CC1A645107B4F3EBAF8CA3E3E01080E2528AA7A13AE124CEEDF85965CAAE9D02F225B4B2ABA03CB24829F77933DE8C7046A77C89A6D234214F3DD7D420EF5E0434EFB451CAD8988FC759CE7F1E00E306FFD751011DB81C82E0F25907C548CFD5BDCA72C67EE2C84B5026D69EB1C3177ABAB67C44FF159F3624FF46C5E617BF49D59B8075DBB94987897A8BE4704E7E36A6D9031FA99E87694F1D67EB64D6DB5505E84108BC06642BA8A
	F0F12ED8BFDEEC43AB4E896FA29AC3BAACAF906CF11BF0F1A7500EFAE15F4C5BCB5B51DFF45FCC049F1E3DB1519DD7575CC4FEC8236BC3F8AE9BEF3E01DFC27123A6BC7343893738331C8D3417670AF3A011D03F7D3E094F5071E73283D44EFF97F22C961C6AB45F3F969E7A48D6E0D18BE84D66FD2F3AAFDA8BD86C34AE5C0766823E70FE18FFG55G0E7BC55C5E6A281CACEE62DE08870C0DE92FF7EA3EF85DFB373EE0F2DF1B223EFF18223E96B9B657747B4777119A53E6F26EB8392A18761A98B99627755D
	38275C8834DCG128A3182388850851014AA6275E9477B13450BD6B807DA8F38ABF0C6280FBD439A366E6B195A987FBBCE6E0D187632D4AB398DD354777C247A32AD754D691D6CCE5CDADF4624F21BE274315A5EFB79A8315C6F33AA0F751E087D6DA71DC5DCDFEE2A585F3ECE6F264B9F00BDA0007D81D1FFBFF0E1EE78211D8C1089108D10833083206C4BA2B756F5DC1AACB76A448509C3B5FEDAA4887343B57F5462650D130B1D8355DF3612BB20CCADDEE5134AB5D96A5BBCC5FDBF1AD44EE729EFD15F547C
	39AACEAE561F23167ABC7DD353970E130B5517E629AFA7B4B5FDBF0B130B551795230F5D6BDF25E767F5C0FB8E00612A6859F6A7340F4C936B76BFF7609E7B0251FE1136713E0035039DD6775DF78B583F951E6918753968A8C800E7737F694E4477294FF4DA5F277E20537ABE75071D07989E7AB03FBD927497BBA73EEFFD25536A3E154568159BA2C6F6A3A60B5C4470558A536FCA77676137C440F4DC7EC2F59C25F1FB560635DD9B8869CAD0262EE6EB06BC046F8520B2378C04D859C6EFC7E277DEC46C93EE
	3969F9F11E2879B32E1FA571B57F32CF51D5CDF46210A52D392747A8782DE159AB62B45215B8E726F5D9674C173A2CF3E6EE5744B9B33FABD14EB09FBD755AC1AA9F0948913A09E910F40953FE2A637B0A387B78CF2420CC443D72BD72DE4EF3D9D76B4F99FA7F29BB71981F6F369E638B5DF83796D187B42F9CBB0E1FF4179A34686259284BC1B40771EC23723A24754AF1FE78F7AA4FE80964FF551D48D7EC2C75BD66311EB57036DEB37E0F867EBB865E2E2B76B3C66E37F60A5A1B8AB935B22606E5DB54B4F6
	F61AC377AA9B67451E2952A87E39F931E72A235D62CC3595E8D752C47EB63A9247466E320E0D5315B8B65D2E49E35363CA9C1B20EB72585CF4613D21C771A99AFF3340FC1C62B77BBE7A3E6332E276FDB4D645FDE67CB9837FCC1F391FEA831F51EF467F49409FD64D7873865E6F44F7CF81E9D89D5089A91D0FF198F30AFB2AAC0841BA00178148FCF6DF45EA525787F1FCBD1A04B7E234D864D1594B6DC2560D7CGE9GEBGB64F97779FD76977BE269D6F99A6DB689232358B206E830481D281D6AF9039B04D
	3D5F1C73FCEE33B5230F6E17B781EF5E8231568FDFC7DC51C2E771690582773D6BE2EDE9D8A85616873A09116F0BCC3F0FDB3E677EFC57165AAFD4871BDBDFF10A391586E35E38D044710C9B4F8D46F9842F43EAEABE54945FA5293709E685BB0BEC237BA814AFCC97E71123749DDBAD349DG01F4F107BEF4C25C21E77D55F23269C56BF790DB9C37EC49697CCEA25DF4BF792E2700696CEF97BA37823DAC033E17EBBFE26A837349EBE3FBC1C65FB00E0E757179FCB37D55F1F4347749F1726FF518696823A789
	233331EDF8DB0CED670655A4704D64045F3B79B25818E673E779F7B3726D99765DECEC6F38445856E60831353BE2477694B15BF62FABF6EC2746519FF7450EED3C7C4FDD31E39372ECDECE6B44EF91636EDB6DEAC0718E138B0B687BBEBA4770FEFEC706184F2957F15FE1F13F4BA589174D22738E6FE6C721DD3898F1BF3B5E406DF9FCD17CBAA25687718D49A660598A70B6EF6FD894DDC358DCFAAC6E9E744E66605FCFD1AE6F2E65589A380D4E1D264531EB638E0AEFDF9C3BB69EEF9373578734750B45397F
	CF9E2C19151930DE82388106G528156E40A1A39D7428FD4A7BC73BB54F093AC308153160C1F735A27764D60377708F932666F9EA559339A923FC74B646F5178B3FBE3C6BEBE59781E726278CA17B80B2B815A8142G49G19CB44782ECA7CFE9237343003153CFE7A865258A0AB72E0940D0B959B5BFB2E3B9D9B4BF765BA2E5B266B1BAA53DAD6653C5C62E82C2FEBF2355637B4B7B5DD2AEBF155B53555F5349A3ED9B6E83E333D706B69165FE7A02CDE42FC320EBFD3FE15114FECF8627A3989EC38525C5258
	597C09434A8AD6FFFE5FA55EA96F043E5FA46CFB1FA2A27E36FFE0052BFE90BFAB3852CF0C317C4BBE9167323FDEC19AFE50E0BB1EE0ACC38F7277638FC6EBFDF8C975DE960BFFEBA3461C7AAF536FD3EDDF7C7AFB698B5541F27483CF62BD6C7FF92C7730CD9E6BB3D40BE762F3F527A771F7CC12477C9D931BE32EG2E8BE4A92CC3G97819CG7E25E2FEED73603BD3F6B398BB37AE7BFAF18F0DDF076A46D94ABB41D94ABB79B725BF7262B7839ED55755A076AF7039F45E6AEA18A2799D0297FD24A93DFE1D
	31F27CAF3C18378139072279B733C144A543381699E3CB763D8BCF275497FFAA8624DDD343309C8EB3ACFD7147F3263C4BDCBB56C2FF1B17CE7C7E004D17FF3CEE1E7BA50B82B6D167E379F3F9FFF9CBE35F573C9A42B533E2996CDF817CGC9GE9GD9GEB81B683648210071C4525G15G97819CGBE00A1001407048F66761F1A4087FCB3C8032CC98A06197B38B710B84E214064F19E8C506FC5A0BE0E61B0BFB70D8670BC227674C4E4A31F2685CA8CDCE3442EC694BCD0F1DA7000F9AE3090F13959C14C
	EB635B887A29CAABB006FA59B960BC2D014B9EE2EBAD4355D2DCB647B1FFFB7133E45E6701505B09FC853361A328474D1EBEB871B924DEC366101A58CF256A64FEBA26DA4F0713EA7CFC28D42D6743D955BC9F986E3C0A352097625BEE70559A388E83D7C77BC4DC1B62537D867EC3EA97622F4828D060BF26F68DD2DC3B28D9A6DC8747352998EB98B2EC9D05EC170AF7D7BDCAA0405AEE4A2304A07406BFE4EA239216CCF67799B8341B1DED249326EFE328698CDFB634BF0A972693767FF09E3D55824F2CD748
	2E08C68E9ADF88B4D283365CBCD7D370639585B6D258C8F63300B412C274EAA359C51D5EC88AA0A0C318B10563013F2F9DB34F7F9DB33D89577EAD4B797CDF9EBB7F7FE38CE3F9FA053338E90518439F0E7DE1023CEC8DCA01G6C4C7B6BC2BE4C51CB433A9CDDAFF60EE17F914143090CD6C06D6EB3497CBFD0CB8788467FD079C792GG8CB5GGD0CB818294G94G88G88G380171B4467FD079C792GG8CB5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586G
	GGG81G81GBAGGG0192GGGG
**end of data**/
}
}

package cbit.image;

import java.awt.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (10/11/00 6:39:29 PM)
 * @author: 
 */
public class ImagePlaneManagerPanel extends javax.swing.JPanel {
	//
	private static final String defaultInfoString = "Info";
	//
	private Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	private Cursor panCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pan.gif")), new Point(12, 12), "PanCursor");
	private Cursor zoomCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/zoom.gif")), new Point(6, 6), "ZoomCursor");
	private ImagePaneScroller ivjImagePaneScroller1 = null;
	private SourceDataInfo fieldSourceDataInfo = null;
	private ImagePlaneManager ivjImagePlaneManager = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private int fieldMode = 0;
	private ImagePaneModel ivjimagePaneModel = null;
	private cbit.vcell.geometry.gui.CurveEditorToolPanel ivjCurveEditorToolPanel1 = null;
	private cbit.vcell.geometry.gui.CurveEditorTool ivjCurveEditorTool = null;
	private cbit.vcell.geometry.gui.CurveRenderer ivjCurveRenderer = null;
	private DisplayAdapterServicePanel ivjDisplayAdapterServicePanel = null;
	private boolean ivjConnPtoP3Aligning = false;
	private DisplayAdapterService ivjdisplayAdapterService1 = null;
	private boolean ivjConnPtoP5Aligning = false;
	private ImagePaneView ivjimagePaneView1 = null;
	private javax.swing.JLabel ivjInfoJlabel = null;
	private cbit.vcell.simdata.gui.CurveValueProvider fieldCurveValueProvider = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JPanel ivjJPanel3 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JToolBar ivjJToolBar1 = null;
	private ImagePlanePanel ivjImagePlanePanel1 = null;
	private Boolean ivjBZeroView = null;
	private javax.swing.JCheckBox ivjSpatialProjectionJCheckBox = null;

class IvjEventHandler implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.beans.PropertyChangeListener {
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseDragged(java.awt.event.MouseEvent e) {
			if (e.getSource() == ImagePlaneManagerPanel.this.getimagePaneView1()) 
				connEtoC7(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {
			if (e.getSource() == ImagePlaneManagerPanel.this.getImagePaneScroller1()) 
				connEtoC11(e);
			if (e.getSource() == ImagePlaneManagerPanel.this.getimagePaneView1()) 
				connEtoC18(e);
		};
		public void mouseExited(java.awt.event.MouseEvent e) {
			if (e.getSource() == ImagePlaneManagerPanel.this.getimagePaneView1()) 
				connEtoC17(e);
		};
		public void mouseMoved(java.awt.event.MouseEvent e) {
			if (e.getSource() == ImagePlaneManagerPanel.this.getimagePaneView1()) 
				connEtoC16(e);
		};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == ImagePlaneManagerPanel.this.getimagePaneView1()) 
				connEtoC15(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == ImagePlaneManagerPanel.this.getimagePaneView1()) 
				connEtoC6(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ImagePlaneManagerPanel.this) 
				connEtoC19(evt);
			if (evt.getSource() == ImagePlaneManagerPanel.this && (evt.getPropertyName().equals("mode"))) 
				connEtoC3(evt);
			if (evt.getSource() == ImagePlaneManagerPanel.this.getImagePlaneManager() && (evt.getPropertyName().equals("imagePlaneData"))) 
				connEtoM1(evt);
			if (evt.getSource() == ImagePlaneManagerPanel.this.getimagePaneModel() && (evt.getPropertyName().equals("dimension"))) 
				connEtoC5(evt);
			if (evt.getSource() == ImagePlaneManagerPanel.this.getImagePlaneManager() && (evt.getPropertyName().equals("normalAxis"))) 
				connEtoM2(evt);
			if (evt.getSource() == ImagePlaneManagerPanel.this.getCurveEditorTool() && (evt.getPropertyName().equals("curveRenderer"))) 
				connPtoP3SetSource();
			if (evt.getSource() == ImagePlaneManagerPanel.this.getdisplayAdapterService1() && (evt.getPropertyName().equals("activeColorModelID"))) 
				connEtoC12(evt);
			if (evt.getSource() == ImagePlaneManagerPanel.this.getdisplayAdapterService1() && (evt.getPropertyName().equals("activeScaleRange"))) 
				connEtoC13(evt);
			if (evt.getSource() == ImagePlaneManagerPanel.this.getdisplayAdapterService1() && (evt.getPropertyName().equals("valueDomain"))) 
				connEtoC14(evt);
			if (evt.getSource() == ImagePlaneManagerPanel.this.getImagePaneScroller1() && (evt.getPropertyName().equals("imagePaneView"))) 
				connPtoP5SetTarget();
			if (evt.getSource() == ImagePlaneManagerPanel.this.getCurveRenderer() && (evt.getPropertyName().equals("selection"))) 
				connEtoC8(evt);
			if (evt.getSource() == ImagePlaneManagerPanel.this.getCurveRenderer() && (evt.getPropertyName().equals("selectionValid"))) 
				connEtoC9(evt);
			if (evt.getSource() == ImagePlaneManagerPanel.this.getCurveEditorTool() && (evt.getPropertyName().equals("tool"))) 
				connEtoC10(evt);
			if (evt.getSource() == ImagePlaneManagerPanel.this && (evt.getPropertyName().equals("curveValueProvider"))) 
				connEtoM7(evt);
			if (evt.getSource() == ImagePlaneManagerPanel.this.getDisplayAdapterServicePanel()) 
				connEtoM3(evt);
			if (evt.getSource() == ImagePlaneManagerPanel.this.getImagePlaneManager() && (evt.getPropertyName().equals("normalAxis"))) 
				connEtoM4(evt);
		};
	};
/**
 * ImagePlaneManagerPanel constructor comment.
 */
public ImagePlaneManagerPanel() {
	super();
	initialize();
}
/**
 * Comment
 */
private void calculateScaling(java.awt.Dimension ipmDimension) {
	cbit.vcell.geometry.Coordinate worldDelta = null;
	if (getSourceDataInfo() != null && getimagePaneModel() != null) {
		double wd_x = getSourceDataInfo().getExtent().getX() / getimagePaneModel().getScaledLength(getSourceDataInfo().getXSize());
		double wd_y = getSourceDataInfo().getExtent().getY() / getimagePaneModel().getScaledLength(getSourceDataInfo().getYSize());
		double wd_z = getSourceDataInfo().getExtent().getZ() / getimagePaneModel().getScaledLength(getSourceDataInfo().getZSize());
		getCurveRenderer().setWorldDelta(new cbit.vcell.geometry.Coordinate(wd_x, wd_y, wd_z));
		cbit.util.Origin o = getSourceDataInfo().getOrigin();
		getCurveRenderer().setWorldOrigin(new cbit.vcell.geometry.Coordinate(o.getX(), o.getY(), o.getZ()));
		getCurveRenderer().setDefaultLineWidthMultiplier((double) getimagePaneModel().getZoom());
	} else {
		getCurveRenderer().setWorldDelta(null);
	}
	//
	if(getBZeroView() != null && getBZeroView().booleanValue()){
		getImagePaneScroller1().zeroView(false);
	}
	setBZeroView(new Boolean(false));
	//
}
/**
 * connEtoC1:  (CurveRenderer.this --> ImagePlaneManagerPanel.curveRenderer_This(Lcbit.vcell.geometry.gui.CurveRenderer;)V)
 * @param value cbit.vcell.geometry.gui.CurveRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(cbit.vcell.geometry.gui.CurveRenderer value) {
	try {
		// user code begin {1}
		// user code end
		this.curveRenderer_This(getCurveRenderer());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (CurveEditorTool.tool --> ImagePlaneManagerPanel.updatePanZoom()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updatePanZoom();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC11:  (imagePaneView1.mouse.mouseEntered(java.awt.event.MouseEvent) --> ImagePlaneManagerPanel.setToolCursor()V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setToolCursor();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC12:  (displayAdapterService1.activeColorModelID --> ImagePlaneManagerPanel.fireVetoableChange(Ljava.lang.String;Ljava.lang.Object;Ljava.lang.Object;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.firePropertyChange("activeColorModelID", arg1.getOldValue(), arg1.getNewValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC13:  (displayAdapterService1.activeScaleRange --> ImagePlaneManagerPanel.firePropertyChange(Ljava.lang.String;Ljava.lang.Object;Ljava.lang.Object;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.firePropertyChange("activeScaleRange", arg1.getOldValue(), arg1.getNewValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC14:  (displayAdapterService1.valueDomain --> ImagePlaneManagerPanel.firePropertyChange(Ljava.lang.String;Ljava.lang.Object;Ljava.lang.Object;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.firePropertyChange("valueDomain", arg1.getOldValue(), arg1.getNewValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC15:  (imagePaneView1.mouse.mousePressed(java.awt.event.MouseEvent) --> ImagePlaneManagerPanel.imagePaneView1_MouseMoved(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateInfo(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC16:  (imagePaneView1.mouseMotion.mouseMoved(java.awt.event.MouseEvent) --> ImagePlaneManagerPanel.updateInfo(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateInfo(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC17:  (imagePaneView1.mouse.mouseExited(java.awt.event.MouseEvent) --> ImagePlaneManagerPanel.updateInfo(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateInfo(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC18:  (imagePaneView1.mouse.mouseEntered(java.awt.event.MouseEvent) --> ImagePlaneManagerPanel.updateInfo(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC18(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateInfo(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC19:  (ImagePlaneManagerPanel.propertyChange.propertyChange(java.beans.PropertyChangeEvent) --> ImagePlaneManagerPanel.shouldZeroView(Ljava.beans.PropertyChangeEvent;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC19(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.shouldZeroView(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (ImagePlaneManagerPanel.mode --> ImagePlaneManagerPanel.mode_set()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.mode_set();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (ImagePlaneManagerPanel.initialize() --> ImagePlaneManagerPanel.imagePlaneManagerPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4() {
	try {
		// user code begin {1}
		// user code end
		this.imagePlaneManagerPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (imagePaneModel.dimension --> ImagePlaneManagerPanel.calculateScaling(Ljava.awt.Dimension;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.calculateScaling(getimagePaneModel().getDimension());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (imagePaneView1.mouse.mousePressed(java.awt.event.MouseEvent) --> ImagePlaneManagerPanel.imagePaneView1_MouseMoved(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateInfo(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (imagePaneView1.mouseMotion.mouseMoved(java.awt.event.MouseEvent) --> ImagePlaneManagerPanel.imagePaneView1_MouseMoved(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateInfo(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC8:  (CurveRenderer.selection --> ImagePlaneManagerPanel.firePropertyChange(Ljava.lang.String;Ljava.lang.Object;Ljava.lang.Object;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.firePropertyChange("curveRendererSelection", arg1.getOldValue(), arg1.getNewValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (CurveRenderer.selectionValid --> ImagePlaneManagerPanel.firePropertyChange(Ljava.lang.String;Ljava.lang.Object;Ljava.lang.Object;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.firePropertyChange("curveRendererSelectionValid", arg1.getOldValue(), arg1.getNewValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (ImagePlaneManager.imagePlaneData --> imagePaneModel.sourceData)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getimagePaneModel().setSourceData(getImagePlaneManager().getImagePlaneData());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (ImagePlaneManager.normalAxis --> MyCurveRenderer.normalAxis)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getCurveRenderer().setNormalAxis(getImagePlaneManager().getNormalAxis());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (DisplayAdapterServicePanel.propertyChange.propertyChange(java.beans.PropertyChangeEvent) --> imagePaneModel.updateViewPortImage()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getimagePaneModel().updateViewPortImage();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM4:  (ImagePlaneManager.normalAxis --> NormalAxisChangeInProgress.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setBZeroView(new java.lang.Boolean(true));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM7:  (ImagePlaneManagerPanel.curveValueProvider --> CurveEditorTool.curveValueProvider)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getCurveEditorTool().setCurveValueProvider(this.getCurveValueProvider());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetTarget:  (ImagePlaneManager.this <--> ImagePlanePanel1.imagePlaneMananager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getImagePlanePanel1().setImagePlaneMananager(getImagePlaneManager());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP2SetTarget:  (CurveEditorTool.this <--> CurveEditorToolPanel1.curveEditorTool)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		getCurveEditorToolPanel1().setCurveEditorTool(getCurveEditorTool());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP3SetSource:  (CurveRenderer.this <--> CurveEditorTool.curveRenderer)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setCurveRenderer(getCurveEditorTool().getCurveRenderer());
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
 * connPtoP3SetTarget:  (MyCurveRenderer.this <--> CurveEditorTool.curveRenderer)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getCurveRenderer() != null)) {
				getCurveEditorTool().setCurveRenderer(getCurveRenderer());
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
 * connPtoP4SetTarget:  (DisplayAdapterServicePanel.displayAdapterService <--> displayAdapterService1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		setdisplayAdapterService1(getDisplayAdapterServicePanel().getDisplayAdapterService());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP5SetTarget:  (ImagePaneScroller1.imagePaneView <--> imagePaneView1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			setimagePaneView1(getImagePaneScroller1().getImagePaneView());
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
private void curveRenderer_This(cbit.vcell.geometry.gui.CurveRenderer arg1) {
	getImagePaneScroller1().getImagePaneView().setDrawPaneModel(getCurveRenderer());
}
/**
 * Insert the method's description here.
 * Creation date: (6/4/2001 5:26:53 PM)
 * @param enable boolean
 */
public void enableDrawingTools(boolean enable) {
	getCurveEditorTool().setEnableDrawingTools(enable);
	boolean is3D = (getSourceDataInfo() != null?getSourceDataInfo().getZSize() > 1:false);
	getSpatialProjectionJCheckBox().setVisible(enable && is3D);
}
/**
 * Return the NormalAxisChangeInProgress property value.
 * @return java.lang.Boolean
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.lang.Boolean getBZeroView() {
	// user code begin {1}
	// user code end
	return ivjBZeroView;
}
/**
 * Return the CurveEditorTool property value.
 * @return cbit.vcell.geometry.gui.CurveEditorTool
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.gui.CurveEditorTool getCurveEditorTool() {
	if (ivjCurveEditorTool == null) {
		try {
			ivjCurveEditorTool = new cbit.vcell.geometry.gui.CurveEditorTool();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCurveEditorTool;
}
/**
 * Return the CurveEditorToolPanel1 property value.
 * @return cbit.vcell.geometry.gui.CurveEditorToolPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.gui.CurveEditorToolPanel getCurveEditorToolPanel1() {
	if (ivjCurveEditorToolPanel1 == null) {
		try {
			ivjCurveEditorToolPanel1 = new cbit.vcell.geometry.gui.CurveEditorToolPanel();
			ivjCurveEditorToolPanel1.setName("CurveEditorToolPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCurveEditorToolPanel1;
}
/**
 * Gets the curveEditorToolPanelVisible property (boolean) value.
 * @return The curveEditorToolPanelVisible property value.
 * @see #setCurveEditorToolPanelVisible
 */
public boolean getCurveEditorToolPanelVisible() {
	return getCurveEditorToolPanel1().isVisible();
}
/**
 * Return the MyCurveRenderer property value.
 * @return cbit.vcell.geometry.gui.CurveRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public cbit.vcell.geometry.gui.CurveRenderer getCurveRenderer() {
	// user code begin {1}
	// user code end
	return ivjCurveRenderer;
}
/**
 * Method generated to support the promotion of the curveRendererSelection attribute.
 * @return cbit.vcell.geometry.CurveSelectionInfo
 */
public cbit.vcell.geometry.CurveSelectionInfo getCurveRendererSelection() {
	return getCurveRenderer().getSelection();
}
/**
 * Method generated to support the promotion of the curveRendererSelectionValid attribute.
 * @return boolean
 */
public boolean getCurveRendererSelectionValid() {
	return getCurveRenderer().getSelectionValid();
}
/**
 * Gets the curveValueProvider property (cbit.vcell.simdata.gui.CurveValueProvider) value.
 * @return The curveValueProvider property value.
 * @see #setCurveValueProvider
 */
public cbit.vcell.simdata.gui.CurveValueProvider getCurveValueProvider() {
	return fieldCurveValueProvider;
}
/**
 * Return the displayAdapterService1 property value.
 * @return cbit.image.DisplayAdapterService
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DisplayAdapterService getdisplayAdapterService1() {
	// user code begin {1}
	// user code end
	return ivjdisplayAdapterService1;
}
/**
 * Return the DisplayAdapterServicePanel1 property value.
 * @return cbit.image.DisplayAdapterServicePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DisplayAdapterServicePanel getDisplayAdapterServicePanel() {
	if (ivjDisplayAdapterServicePanel == null) {
		try {
			ivjDisplayAdapterServicePanel = new cbit.image.DisplayAdapterServicePanel();
			ivjDisplayAdapterServicePanel.setName("DisplayAdapterServicePanel");
			ivjDisplayAdapterServicePanel.setPreferredSize(new java.awt.Dimension(185, 280));
			ivjDisplayAdapterServicePanel.setMinimumSize(new java.awt.Dimension(185, 280));
			ivjDisplayAdapterServicePanel.setMaximumSize(new java.awt.Dimension(185, 280));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisplayAdapterServicePanel;
}
/**
 * Method generated to support the promotion of the displayAdapterServicePanelVisible attribute.
 * @return boolean
 */
public boolean getDisplayAdapterServicePanelVisible() {
	return getDisplayAdapterServicePanel().isVisible();
}
/**
 * Return the imagePaneModel1 property value.
 * @return cbit.image.ImagePaneModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ImagePaneModel getimagePaneModel() {
	// user code begin {1}
	// user code end
	return ivjimagePaneModel;
}
/**
 * Return the ImagePaneScroller1 property value.
 * @return cbit.image.ImagePaneScroller
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ImagePaneScroller getImagePaneScroller1() {
	if (ivjImagePaneScroller1 == null) {
		try {
			ivjImagePaneScroller1 = new cbit.image.ImagePaneScroller();
			ivjImagePaneScroller1.setName("ImagePaneScroller1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImagePaneScroller1;
}
/**
 * Return the imagePaneView1 property value.
 * @return cbit.image.ImagePaneView
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ImagePaneView getimagePaneView1() {
	// user code begin {1}
	// user code end
	return ivjimagePaneView1;
}
/**
 * Return the ImagePlaneManager property value.
 * @return cbit.image.ImagePlaneManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public ImagePlaneManager getImagePlaneManager() {
	if (ivjImagePlaneManager == null) {
		try {
			ivjImagePlaneManager = new cbit.image.ImagePlaneManager();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImagePlaneManager;
}
/**
 * Return the ImagePlanePanel2 property value.
 * @return cbit.image.ImagePlanePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ImagePlanePanel getImagePlanePanel1() {
	if (ivjImagePlanePanel1 == null) {
		try {
			ivjImagePlanePanel1 = new cbit.image.ImagePlanePanel();
			ivjImagePlanePanel1.setName("ImagePlanePanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImagePlanePanel1;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getInfoJlabel() {
	if (ivjInfoJlabel == null) {
		try {
			ivjInfoJlabel = new javax.swing.JLabel();
			ivjInfoJlabel.setName("InfoJlabel");
			ivjInfoJlabel.setBorder(new cbit.gui.LineBorderBean());
			ivjInfoJlabel.setText("Info");
			ivjInfoJlabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjInfoJlabel;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.BorderLayout());
			getJPanel1().add(getJToolBar1(), "North");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsImagePaneScroller1 = new java.awt.GridBagConstraints();
			constraintsImagePaneScroller1.gridx = 0; constraintsImagePaneScroller1.gridy = 0;
			constraintsImagePaneScroller1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsImagePaneScroller1.weightx = 1.0;
			constraintsImagePaneScroller1.weighty = 1.0;
			getJPanel2().add(getImagePaneScroller1(), constraintsImagePaneScroller1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}
/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDisplayAdapterServicePanel = new java.awt.GridBagConstraints();
			constraintsDisplayAdapterServicePanel.gridx = 0; constraintsDisplayAdapterServicePanel.gridy = 0;
			constraintsDisplayAdapterServicePanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDisplayAdapterServicePanel.insets = new java.awt.Insets(3, 0, 0, 0);
			getJPanel3().add(getDisplayAdapterServicePanel(), constraintsDisplayAdapterServicePanel);

			java.awt.GridBagConstraints constraintsCurveEditorToolPanel1 = new java.awt.GridBagConstraints();
			constraintsCurveEditorToolPanel1.gridx = 0; constraintsCurveEditorToolPanel1.gridy = 1;
			constraintsCurveEditorToolPanel1.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsCurveEditorToolPanel1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCurveEditorToolPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getCurveEditorToolPanel1(), constraintsCurveEditorToolPanel1);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 3;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.weighty = 1.0;
			constraintsJPanel1.insets = new java.awt.Insets(0, 4, 0, 4);
			getJPanel3().add(getJPanel1(), constraintsJPanel1);

			java.awt.GridBagConstraints constraintsImagePlanePanel1 = new java.awt.GridBagConstraints();
			constraintsImagePlanePanel1.gridx = 0; constraintsImagePlanePanel1.gridy = 4;
			constraintsImagePlanePanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsImagePlanePanel1.weightx = 1.0;
			constraintsImagePlanePanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getImagePlanePanel1(), constraintsImagePlanePanel1);

			java.awt.GridBagConstraints constraintsSpatialProjectionJCheckBox = new java.awt.GridBagConstraints();
			constraintsSpatialProjectionJCheckBox.gridx = 0; constraintsSpatialProjectionJCheckBox.gridy = 2;
			constraintsSpatialProjectionJCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSpatialProjectionJCheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getSpatialProjectionJCheckBox(), constraintsSpatialProjectionJCheckBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}
/**
 * Return the JToolBar1 property value.
 * @return javax.swing.JToolBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToolBar getJToolBar1() {
	if (ivjJToolBar1 == null) {
		try {
			ivjJToolBar1 = new javax.swing.JToolBar();
			ivjJToolBar1.setName("JToolBar1");
			ivjJToolBar1.setFloatable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToolBar1;
}
/**
 * Gets the mode property (int) value.
 * @return The mode property value.
 * @see #setMode
 */
public int getMode() {
	return fieldMode;
}
/**
 * Gets the sourceDataInfo property (cbit.image.SourceDataInfo) value.
 * @return The sourceDataInfo property value.
 * @see #setSourceDataInfo
 */
public SourceDataInfo getSourceDataInfo() {
	return fieldSourceDataInfo;
}
/**
 * Return the SpatialProjectionJCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBox getSpatialProjectionJCheckBox() {
	if (ivjSpatialProjectionJCheckBox == null) {
		try {
			ivjSpatialProjectionJCheckBox = new javax.swing.JCheckBox();
			ivjSpatialProjectionJCheckBox.setName("SpatialProjectionJCheckBox");
			ivjSpatialProjectionJCheckBox.setText("Use 3D Sampling");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSpatialProjectionJCheckBox;
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
private void imagePlaneManagerPanel_Initialize() {
	setimagePaneModel(getImagePaneScroller1().getImagePaneModel());
	getCurveEditorTool().setWorldCoordinateCalculator(getImagePlaneManager());
	getCurveEditorTool().setVcellDrawable(getImagePaneScroller1().getImagePaneView());
	getCurveEditorTool().setKeyboardAndMouseEvents(getImagePaneScroller1().getImagePaneView());
	getImagePaneScroller1().getImagePaneModel().setDisplayAdapterService(getDisplayAdapterServicePanel().getDisplayAdapterService());
	setCurveRenderer(new cbit.vcell.geometry.gui.CurveRenderer());
	getImagePaneScroller1().setImagePlaneManager(getImagePlanePanel1().getImagePlaneMananager());
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	getImagePlaneManager().addPropertyChangeListener(ivjEventHandler);
	getCurveEditorTool().addPropertyChangeListener(ivjEventHandler);
	getImagePaneScroller1().addPropertyChangeListener(ivjEventHandler);
	getImagePaneScroller1().addMouseListener(ivjEventHandler);
	getDisplayAdapterServicePanel().addPropertyChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ImagePlaneManagerPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(560, 544);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 0;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel2.weightx = 1.0;
		constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel2(), constraintsJPanel2);

		java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
		constraintsJPanel3.gridx = 1; constraintsJPanel3.gridy = 0;
		constraintsJPanel3.fill = java.awt.GridBagConstraints.VERTICAL;
		constraintsJPanel3.weighty = 1.0;
		constraintsJPanel3.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel3(), constraintsJPanel3);

		java.awt.GridBagConstraints constraintsInfoJlabel = new java.awt.GridBagConstraints();
		constraintsInfoJlabel.gridx = 0; constraintsInfoJlabel.gridy = 1;
		constraintsInfoJlabel.gridwidth = 2;
		constraintsInfoJlabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsInfoJlabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getInfoJlabel(), constraintsInfoJlabel);
		initConnections();
		connEtoC4();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ImagePlaneManagerPanel aImagePlaneManagerPanel;
		aImagePlaneManagerPanel = new ImagePlaneManagerPanel();
		frame.setContentPane(aImagePlaneManagerPanel);
		frame.setSize(aImagePlaneManagerPanel.getSize());
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
 * Comment
 */
private void mode_set() {
	getImagePaneScroller1().getImagePaneModel().setMode(getMode());
	getImagePaneScroller1().getImagePaneView().invalidate();
	getImagePaneScroller1().validate();
}
/**
 * Set the NormalAxisChangeInProgress to a new value.
 * @param newValue java.lang.Boolean
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setBZeroView(java.lang.Boolean newValue) {
	if (ivjBZeroView != newValue) {
		try {
			ivjBZeroView = newValue;
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
 * Sets the curveEditorToolPanelVisible property (boolean) value.
 * @param curveEditorToolPanelVisible The new value for the property.
 * @see #getCurveEditorToolPanelVisible
 */
public void setCurveEditorToolPanelVisible(boolean curveEditorToolPanelVisible) {
	getCurveEditorToolPanel1().setVisible(curveEditorToolPanelVisible);
}
/**
 * Set the CurveRenderer to a new value.
 * @param newValue cbit.vcell.geometry.gui.CurveRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void setCurveRenderer(cbit.vcell.geometry.gui.CurveRenderer newValue) {
	if (ivjCurveRenderer != newValue) {
		try {
			cbit.vcell.geometry.gui.CurveRenderer oldValue = getCurveRenderer();
			/* Stop listening for events from the current object */
			if (ivjCurveRenderer != null) {
				ivjCurveRenderer.removePropertyChangeListener(ivjEventHandler);
			}
			ivjCurveRenderer = newValue;

			/* Listen for events from the new object */
			if (ivjCurveRenderer != null) {
				ivjCurveRenderer.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP3SetTarget();
			connEtoC1(ivjCurveRenderer);
			firePropertyChange("curveRenderer", oldValue, newValue);
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
 * Method generated to support the promotion of the curveRendererSelection attribute.
 * @param arg1 cbit.vcell.geometry.CurveSelectionInfo
 */
public void setCurveRendererSelection(cbit.vcell.geometry.CurveSelectionInfo arg1) {
	getCurveRenderer().setSelection(arg1);
}
/**
 * Sets the curveValueProvider property (cbit.vcell.simdata.gui.CurveValueProvider) value.
 * @param curveValueProvider The new value for the property.
 * @see #getCurveValueProvider
 */
public void setCurveValueProvider(cbit.vcell.simdata.gui.CurveValueProvider curveValueProvider) {
	cbit.vcell.simdata.gui.CurveValueProvider oldValue = fieldCurveValueProvider;
	fieldCurveValueProvider = curveValueProvider;
	firePropertyChange("curveValueProvider", oldValue, curveValueProvider);
}
/**
 * Set the displayAdapterService1 to a new value.
 * @param newValue cbit.image.DisplayAdapterService
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setdisplayAdapterService1(DisplayAdapterService newValue) {
	if (ivjdisplayAdapterService1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjdisplayAdapterService1 != null) {
				ivjdisplayAdapterService1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjdisplayAdapterService1 = newValue;

			/* Listen for events from the new object */
			if (ivjdisplayAdapterService1 != null) {
				ivjdisplayAdapterService1.addPropertyChangeListener(ivjEventHandler);
			}
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
 * Method generated to support the promotion of the displayAdapterServicePanelVisible attribute.
 * @param arg1 boolean
 */
public void setDisplayAdapterServicePanelVisible(boolean arg1) {
	getDisplayAdapterServicePanel().setVisible(arg1);
}
/**
 * Set the imagePaneModel1 to a new value.
 * @param newValue cbit.image.ImagePaneModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setimagePaneModel(ImagePaneModel newValue) {
	if (ivjimagePaneModel != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjimagePaneModel != null) {
				ivjimagePaneModel.removePropertyChangeListener(ivjEventHandler);
			}
			ivjimagePaneModel = newValue;

			/* Listen for events from the new object */
			if (ivjimagePaneModel != null) {
				ivjimagePaneModel.addPropertyChangeListener(ivjEventHandler);
			}
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
 * Set the imagePaneView1 to a new value.
 * @param newValue cbit.image.ImagePaneView
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setimagePaneView1(ImagePaneView newValue) {
	if (ivjimagePaneView1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjimagePaneView1 != null) {
				ivjimagePaneView1.removeMouseListener(ivjEventHandler);
				ivjimagePaneView1.removeMouseMotionListener(ivjEventHandler);
			}
			ivjimagePaneView1 = newValue;

			/* Listen for events from the new object */
			if (ivjimagePaneView1 != null) {
				ivjimagePaneView1.addMouseListener(ivjEventHandler);
				ivjimagePaneView1.addMouseMotionListener(ivjEventHandler);
			}
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
 * Sets the mode property (int) value.
 * @param mode The new value for the property.
 * @see #getMode
 */
public void setMode(int mode) {
	int oldValue = fieldMode;
	fieldMode = mode;
	firePropertyChange("mode", new Integer(oldValue), new Integer(mode));
}
/**
 * Sets the sourceDataInfo property (cbit.image.SourceDataInfo) value.
 * @param sourceDataInfo The new value for the property.
 * @see #getSourceDataInfo
 */
public void setSourceDataInfo(SourceDataInfo sourceDataInfo) {
	SourceDataInfo oldValue = fieldSourceDataInfo;
	fieldSourceDataInfo = sourceDataInfo;
	firePropertyChange("sourceDataInfo", oldValue, sourceDataInfo);
}
/**
 * Insert the method's description here.
 * Creation date: (7/4/2003 12:04:20 PM)
 */
private void setToolCursor() {

	Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	if(getCurveEditorTool()!= null){
		if(getCurveEditorTool().getTool() == cbit.vcell.geometry.gui.CurveEditorTool.TOOL_PAN){
			cursor = panCursor;
			//cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		}else if(getCurveEditorTool().getTool() == cbit.vcell.geometry.gui.CurveEditorTool.TOOL_ZOOM){
			cursor = zoomCursor;
			//cursor = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
		}
	}
	cbit.util.BeanUtils.setCursorThroughout(getImagePaneScroller1(), cursor);
}
/**
 * Comment
 */
private void shouldZeroView(java.beans.PropertyChangeEvent propertyChangeEvent) {
	if(propertyChangeEvent.getSource() == this){
		if(propertyChangeEvent.getPropertyName().equals("sourceDataInfo")){
			if(propertyChangeEvent.getNewValue() == null){
				getImagePaneScroller1().zeroView(true);
			}
			sourceDataInfo_set();
			if(propertyChangeEvent.getOldValue() == null){
				getImagePaneScroller1().zeroView(true);
			}
			
		}
	}
}
/**
 * Comment
 */
private void sourceDataInfo_set() {
	SourceDataInfo sdi = getSourceDataInfo();
	getdisplayAdapterService1().setValueDomain(sdi != null?sdi.getMinMax():null);
	getImagePlaneManager().setSourceDataInfo(sdi);
	if(sdi == null || sdi.getZSize() <= 1){
		getSpatialProjectionJCheckBox().setVisible(false);
		getImagePlanePanel1().setVisible(false);
	}else{
		getSpatialProjectionJCheckBox().setVisible(true && getCurveEditorTool().getEnableDrawingTools());
		getImagePlanePanel1().setVisible(true);
	}
}
/**
 * Comment
 */
private void updateInfo(java.awt.event.MouseEvent mouseEvent) {
	String infoS = null;
	//if(mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_RELEASED || mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_PRESSED){
		//if(getInfoJlabel().getText() != null && getInfoJlabel().getText().length() > 0 ){
			//cbit.util.VCellTransferable.sendToClipboard(getInfoJlabel().getText()+" "+(getSourceDataInfo() != null?getSourceDataInfo().getMinMax().toString():""));
			//return;
		//}
	//}else
	if(mouseEvent.getID() != java.awt.event.MouseEvent.MOUSE_EXITED){
		cbit.vcell.geometry.Coordinate wc = null;
		boolean bNeedsMembraneCursor = false;
		if( getCurveEditorTool().getTool() == cbit.vcell.geometry.gui.CurveEditorTool.TOOL_ZOOM ||
			getCurveEditorTool().getTool() == cbit.vcell.geometry.gui.CurveEditorTool.TOOL_PAN ){
			infoS = getCurveEditorTool().getToolDescription(getCurveEditorTool().getTool());
			setToolCursor();
		}else if(mouseEvent.getID() != java.awt.event.MouseEvent.MOUSE_ENTERED){
			if (getimagePaneView1().isPointOnImage(mouseEvent.getPoint())) {
				java.awt.geom.Point2D unitP = getimagePaneView1().getImagePointUnitized(mouseEvent.getPoint());
				wc = getImagePlaneManager().getWorldCoordinateFromUnitized2D(unitP.getX(), unitP.getY());		
				if(wc != null){
					if(getCurveValueProvider() != null){
						cbit.vcell.geometry.CurveSelectionInfo curveCSI = null;
						cbit.vcell.geometry.CurveSelectionInfo[] curveCSIArr = getCurveRenderer().getCloseCurveSelectionInfos(wc);
						if(curveCSIArr != null){
							for(int i = 0;i < curveCSIArr.length;i+= 1){
								cbit.vcell.geometry.CurveSelectionInfo csiSegment = getCurveRenderer().getClosestSegmentSelectionInfo(wc,curveCSIArr[i].getCurve());
								if(csiSegment != null){
									
									String infoTemp = getCurveValueProvider().getCurveValue(csiSegment);
									if(infoTemp != null){infoS = infoTemp;bNeedsMembraneCursor = true;break;}
								}
							}
						}
					}
					if (infoS == null && getSourceDataInfo() != null) {
						cbit.vcell.math.CoordinateIndex ci = getImagePlaneManager().getDataIndexFromUnitized2D(unitP.getX(), unitP.getY());
						cbit.vcell.geometry.Coordinate quantizedWC = getSourceDataInfo().getWorldCoordinateFromIndex(ci);
						String xCoordString = cbit.util.NumberUtils.formatNumber(quantizedWC.getX());
						String yCoordString = cbit.util.NumberUtils.formatNumber(quantizedWC.getY());
						String zCoordString = cbit.util.NumberUtils.formatNumber(quantizedWC.getZ());
						infoS = 
							"(" + xCoordString +
							(getSourceDataInfo().getYSize() > 1?"," + yCoordString:"") +
							(getSourceDataInfo().getZSize() > 1?"," + zCoordString:"") + ") "+
							"["+ci.x+
							(getSourceDataInfo().getYSize() > 1?","+ci.y:"")+
							(getSourceDataInfo().getZSize() > 1?","+ci.z:"")+"] "+
							(getSourceDataInfo().isDataNull()?"empty":getSourceDataInfo().getDataValueAsString(ci.x, ci.y, ci.z));
					}
					if(infoS == null){
						infoS = "Unknown";
					}
				}
			}
			if(bNeedsMembraneCursor){getimagePaneView1().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));}
			else{setToolCursor();}
		}
	}
	
	//if(mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_DRAGGED || 
		//mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_PRESSED ||
		//mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_EXITED ||
		//mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_ENTERED){
			//getInfoJlabel().setText((infoS == null?defaultInfoString:infoS));
	//}
	getInfoJlabel().setText((infoS == null?defaultInfoString:infoS));
}
/**
 * Comment
 */
private void updatePanZoom() {
	// update the controller
	getimagePaneView1().setForcePan(getCurveEditorTool().getTool() == cbit.vcell.geometry.gui.CurveEditorTool.TOOL_PAN);
	getimagePaneView1().setForceZoom(getCurveEditorTool().getTool() == cbit.vcell.geometry.gui.CurveEditorTool.TOOL_ZOOM);
	// update the viewer
	setToolCursor();
	//if (getCurveEditorTool().getTool() == cbit.vcell.geometry.gui.CurveEditorTool.TOOL_PAN) {
		//cbit.util.BeanUtils.setCursorThroughout(getImagePaneScroller1(), panCursor);
	//} else if (getCurveEditorTool().getTool() == cbit.vcell.geometry.gui.CurveEditorTool.TOOL_ZOOM) {
		//cbit.util.BeanUtils.setCursorThroughout(getImagePaneScroller1(), zoomCursor);
	//} else {
		//cbit.util.BeanUtils.setCursorThroughout(getImagePaneScroller1(), defaultCursor);
	//}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC0FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E165BD8DD8D4571544E8C292CDB092E3925350446C5A16F4EDEADB5BB5DB1A3AD9135A549212B5DD521A8DE9ED56F65D064D12565DBE7E14D1D40C785316A8AAC694C4D0B400287C292028882392031101F9B08373474C830604681E7BF36E3CF97386F0DA7B6D772D5FF7FC6F1EFB4F4FBD675C7F7B86A9FBDBD4FAF2CA36A4A527CB7C5F07491254BEA4C99B7EFD6FDB08AB5B9DEB10D47F36832C177E51
	1C864FFA480BF9AC5610A5B555E5C03A8D52B39F0DB5EC06778D52453A2A81F889A2E78B64398D26341165CC590BF2D652F4D3FEA6BC578234830EBC23F79379167CAD9CBF1E639112D3A4A9AC1F1439BDBF076316C1198C40D582B465337296F8B285A6D41471F2ED13E7CA09AF38D35690BEAA1E8873B96DD27E4C15FE1B9C3907480A26FA921ABCC83781104A27CA0BBC3B61D959545BFD2C33432A94D96D26EE39222201BC1AECA607FC566400D7F7933C5A242C3D04D7E2FE86251524C540E7BA458D74927E
	C6F837810C5F0BF83B13609743FB92C997E01D0E6E2E64BA37FE76ADA96E00BDEDDD10BA6597303AB495786AB44B7EDA4A9F287DDFAAA0F61E88F933G6681AC8148A80CB564837C097A7DA7FB7742333AC329F55A6DCEC7354BD5E3B33B4FBA4D328D5EABAAA007631A65CE275BACC94C37CF67976A68B392B03A181B26E3B619F9907D9C5D7CD5A97E6BD3D3D66A58A4FE66E7A9D5054C962D05BE1BB0596F86156DAD740FC3FF5917A5356C1CD32F24266B48AEABFDA5F5A2679325123D341078FA050D78BA86
	5E1329AE9007943F19620B777970ACA607E94C10F49B64AD590731513A96F5E9CCFBDC4AAAF9D0578E531E1D182294B19DA696E9F579150B701ED344E4168BDD5E6BA378527DBEBC5345C3631424E7C15EF0C92C0145D77CBE52C66B8F469A4CG43G11G33819682A49C44985BD930FF0CB156E4F2ABB57DD61BD9F6CB0CB63EE093BCE52F62B6B5B8BC0A4951A9DFB2392D268E1B6C11F819B78B088DBA7C685E22315F89B2CE498E59EDD22CCE87F0B759E5C5F6FBD819378B08FDBB5DB24DB5DA6DB288303B
	A4647B3B028D703419BC4AC5175924483E92ACBF351078E440F489B88283F8E7F6B16CA776CAAB0DB564G9416229D2E9792FEE7E4B7F49795950DCEE7EF3F4BC8EAA28D1792BBE76DA1FDC7953C3716220D530B89AE86520B841FEF943E2762D3E76DA46A1B5CC35846B2ED394427046721D8C3B1C0A30085E0BC40CC00F907B01EFE5D0A71D43766A929706587F463A99AE2BB7E900B2344C35AF675BE15C7F44EB98C7DB4C09B40B040F40039G7107F1CC98579B86CF8B57CBADA30B27D13F7107745BE4E390
	3C96C87DC76E0B1E243A2C66E9F5FEBAFF2E63EDF0CD90BE06B168C1A0F504BC66E7CF89091965E027AD9F469A4ABEC4DFAFAB4CE5ED2746641131351CF7C3204BE61F67D9FD5EF639386E653CCE24EE5B387E6BA9BFD6A7B22EED84A8G5887D082703E4446711DGC7G0E821C82A0ED7704C4FAC1C92A85B888F08860C392034216DB7A086CA6505B8B9085B09FE099401632D8C3950085A082E08E40E2000CF2088100D600B00019G8B8112GF2ABE28D75GAE00E9GB1GCBG320E00EDG5A81420FE0CC
	8DB9E3127DFD077EDC4D6B0EFE5C406B2D367F7B5CC6D877355CBEEBD576594FED242EFB394ACE482B165B6A94CF1F8962734BE30C1B7FCF40FC39544F1729AADF9A38AFB10609BFB7EBF8EC63BED77B927DCD5A591F38CF5F577812444CAE155F57F2FF935F6F616972A0FA6F7FBFE03B7FEB407A2E1F6FA3FE1287E407D2A73BE40799DEBCBC6FD77B08BF79385E6594B4EFD33C8B668135B070FA847E5DFD445FA6C5F1DBBB7A9539C5D6942B235B171FDCC4E4F97ABBDC3EA9844A4AA8A27EF58E42BCC014DF
	D3C44650CE2743A153CED410707CF5F48C17ED329D142EF576BB94065F381F2CF1FA65A1BE0778437E95921983587A49FC042D9BE619C88C3A5C30DEF08F11A98DACA248783EFB4F96150DE6C17FC556BFF170DCF2844763411DB89E67FD7AA5A9661AABC5EF2EB96D497BD26AAB597856D6699B0F19CE2B0A75F54F22F3890E77B0DD3FFD0A4C2F02ACE9E87B5BA0CD7BFC3DDE998973FFB1C864535591212393BB4CCBBAC67CD25B6F9E10CF182D0A53EDF4BAEDBC6F8B1B57E0DEB3098E370A676BF45EE736FA
	DCB653D035596402E9DB0B6C9E30F64AA5D8263359CF6EA52BBCA872B66590FB56DC115DCE0217F01C0DBEC0ECB38CB60BG18D909636C44E2120FF3AABE4056BAEDB6969E10370C7A305B162D72C3CCA573A1F99FBECA7227087CB2C8B79D7565B3FA03DDCDBF6F28167E1048170E4158F9CC1B7F95115F84F916007CE68766FBA1AFA2A07F3FC4FE94644DD165B3BB8C15E411F6A477754BB055A61353B3A25643CA5E5364DD62F14F7243CBC85C5BD4735A7A63C426398F57727147589CB1911EE910178F3015
	582A0A14BB6C46F2114719DE517C19F41C69484A7D37A8B73E0A65CF674F68AAF539EFF8305CBC1E9F4F1F093C1C2B1A14F3AAD8AE2F1A4FE12B59BC36351A15E375286947F2F32B599CB78E1E89G99B56AFAEC98907CEA981F4A9AA657D823167BC3D1AE3C06490D2AE17366B9B5EA390F8DE23905BCBF0E4BDF62A7576645F2E9352CDCCEAD13DBD82B6677656BD8CE817CC400D935EC2E9ED72B66275C40F289BCFFB96717D397EBD0GD8B9F33228C79DAB97D567ABB7D714EBC99575606516284AC9A7E04E
	C7752BCE47F25900AB84E8BA4156913693EA7DD62F40F243A7D8FDA3CF7878C51FC0396F2E44F2B13C5CA2D539C4D1AE5E0065B2CEB27B661EE4650ACF2A636A78D6AC57F81269E7666522CE2A7D1B32CD448B4F0F63FC937D4AAD926532CFB1FF941EE2F163BD25167BD8AE161BFE0A59E376A946F719DF390E6DA27E6AD9394AFA3DF23BF6E0B9AFAF9721DB6E17225CC2DEEE09EE3927F6E2397C86D62E2AC12F1CB3CF486565A2B46518DD8E6C42F28B9AD87BC8E8E0EB4965704CBC0D7BF4EF53F1156E5375
	1886CC5EA24FA04C8DAAAACE53D10C6D4BCD26EBEBD2BE77B46E3FBDD3C84602D6C83BG42CF63987C63B5387F9659BBD3CABA75F2EA30BD452853CC5FF927FDE37070F787D359D8734EC1A2B36DCC2CA17B8C0EB3298754FBG559E0F3353CA379FF89F7A748175984DF05FBA308A1EDDD637C7E900E1520B7D6DB387083EE4FF0223797C638787F6C8E35937447D2D4FCF2A6BBBF75D67A92B0354B77F8C6FC74E786A9B3F733FD259BE51F768DE9699FF43A1BF668C7A67271B378557C7CC9818AE1D87D11708
	CF1F145A9FF8B828AEC9DC07AD0DBEDD9A0F9ECAE5B2E78F6D7313B950A95BECC55D3253AEAB6E21226EFEEBC505F6D6027BDDEDF65C8FEE825E610DD80F7FD8DDB0B61EE2B64378455A51AEB140AB892078AC412FF31039A469FBD939A18716D3A0BF7AAC167BA6ADC77AC0D6EE2B9375CB007C1CF3285F643A4D6AF6D0842E06D6D083D513CD8E2E4B24BE14E1843A09GEC3E1458C7E4A8962B074DD5EF3328672A73205CB2006C73B0E61FC71A9CB709E52BDFD83140E23E7CCDF595365F2FBEAE4DD9B9B1C5
	EF9F5B723753D22A4E73FD7D73BEDFC63C71DA4A8CG2677B5F7A60F6FB9D0E6195061B9B7D9FF3B5CCE176CD606EAADD0E53948BF19CF0D5181B67094B579651CA0CB13BD225DECABA3EBBE0F4559EFB36344B13FF1E46AA3FBAEC9580E9E5F202EEF650DCFF46B9B3E4E12125F44776D1BFC751D9177DA4A1CB82C6F879E2C6FF4A8935704751D64A1FE32C30B61B1292C6669EB9ED94987ED987DCF85FD6605D8C35D8524BF21903C55E1D52CA61B75D3B9FF8F63730F39F9813E44F5403586D571CB0C7FB485
	798F836F59027F6F1535FEFC6ACC0A896764FB7BC9BF66F1763BBBB91E596D292DEA3BE518A6685AED62AF3BD316DEE0764AEE7659AD736557D2F2DFC63B7576E3FC37C11948E654AB365FBF3E6FB12B63FBC1B30B6F4C969873DB1066377DC40FCECDAF4164D48EE07D4B203CCB50BCB1C06AE436C2774D770419FDEF37133D05CE132D335FE60275AB3C419845234BB4289455A149916143F7040C1940FF3110F1F510585161F45BCD36EAAF54085B774EGBC63B3FF90ED324588F3A9A372E9712E417A2956DF
	0C6681AF4A8E0772B102E639976D486A55E22B547238E6045C7C46A0DDE411DF8FF9846369256B28E39248AA3E8873C16A27229A351FAC97191F2260B97BA26A344B31D18A32D61450B61F5CC03D193CC7EFE07D96G1F34CB48ABCDA2EB9C93AC7A86E4D87EB959B1C5C39D7AB12D116C6941D8AC7BF7865C13244BADEAD15C605BA3AA4C790E9ED8C76AE04057ECFEF2C4A263BE596B2A3C047D7C24E454D181DC3450B1B1B9CB68C882C9EEA69AB0BEFBD310CF0260B31C0AFCF2AE439A64B272F9A315749DF4
	6F224EE9B7D99DDC17ADE948E362E56451130E7E198738651751BFCF5A547EA9ED0539B0C0B84074D614334031C6522C5305DF7EB35D5FAF25ABD05759C03FD470F8E485E9D79E59379EE73AAEDC013A66FD043A6E48C09EED8477917238ABE32D1A87ACDE2DE664735D8C6433C070C9DF05FC522E409C7A8A72593C156A8C1D810B096AEE620FFEFAC684953C62F45AC5BFE959AAFC88745102470F366E237DF63FC7AE227F1FF0A8E44BA1DFF488F45F2C68AC4D739F273ADB599ADDA17D91B4A70F53AD6495EC
	C3F989A0AB67E314F77F3677301D52C5BBC46F00159C17F1BF7CC350FC46B7B9D74C9B0067C44157127B179BFF9F5D39C9581184C913532D50C9A2995630AF4B457A4D839D168BBD76EF57583349ADFBBCE333671B6F678B39E4B3D255ABEB09C4FFF54FF65423348D5625ED2847EF3697796B518C61E69ACD9146735C8E64B98378458B1E5F58F190F91EF51248E51C6B5C266E6E1199B33E6F6DC43E59D7E38D0DD7116F504E92BD3EE71D8323590D71FDC5709D8FBC678A3EC7738AB5316D35AAA33264F30ABC
	6417883C8ABF91F30A3CD05A8A637971AE64E986FE53854FE7F7690F21CBF7E1FF447A05B59F60FD0E38CF703CE96A9E6DBD024CEB3E3346DC55BB82BBDFAF59C74F572F3173751582FF14629BD5F8267B77697A14244D10B77F9A1E89BFF3187439056D3106FA00F6GAF40B400B96D384EBCF110444C08E76E8D440A8E132D45EF539C4F3E8F12BA774AC3BC7D7B0369B8D6C83817DAD0C6769263C066B2008C934C1DG2ACC28439D25236AF0422B2B833FABE2A3F6EEB70D6D3CF72D04E7C8AFD2BA6FCD5031
	7A0417137A4C843AF9G71G09G599DD81F0525052355876FA6700AB0FDBA6C215D15F105C837489E1A5DFEEE3FD93B31797B1EAB6ABBD8E96BCF27105851DB3317F6B05EF59D5ABB84B79C2169BC49F133BAB33ABCE7E836BD90A25D143E516846F778570151FDF1D43A199ABA3EAE5C18AB0D388744CEF8689C6E19E66D3994394B9183FC63GD8BF78ED8B7603CBBBF07F6CE48F691F72BAE1EE84E06C447EE96F9132EFE703F4B8C09440DC0045G4B81F24D581E369E2A11C63B970487FEC627F82D559E5A
	212DE43756D6E5E6FF5B685B5893C037D513B6EA7830FABED6C96B89F961GD1GF3819683AC874815311EFD070E0ED6CFBAEA991D35002BF7BAFBF52A48F5AD7404D6470AG3AA2CD3ACA5623CB88D15EEFC615572AAB6F8BCAE87222836834BEF449FABE5CF40C78B09C72E6G4C85D884309CE0CB9744F5977A7059C3C7466843EA6A37C08FB2BD7B7AC36B87EE0CCA575625578FDC2BBFA05D64BEA8BF7BE56D7E7BE6EC77CA97367B5EAE526E23A1BD8FA03E8B67C5B58321792EA900CE6B3B44AEBD3A65A1
	4ADBB5B81A3C4CEEBD3A97836846663B78D16972F465BD628D2DFEDF8A2053562FD2D75E2B57C32B5FAFC625B3F66B45E6619A7F7DF4BEC65569ED64286FD8F755101824E79F5D98130BBAC9CC4E0074DC0038EE0C49A7EE04E6432F8550E9ED38D45706D59221CB337033894B487312C28B6AB9A5B9B4BD3F98C0D724C9D7D974687627903AD61EE79BC54FB02167385450740C8820536A99292B677BE904AE1A67450C22E71C5053159EDAACDF9F15EE09C5AF16F7EF9A614C8DE2372553CDAE1310011C4E3986
	76694D39A4AB2CB72CB867FA4D06FDEF219563FCD5B309F3B3240781A22DD85F0A95217925BE00CE9B6751D6BD3A17A95DBC1E3748BA32DF168ABD73B3C25373F0G1DD6CF29C70FEE9E255B42738AFBC656332A8775DC3DAAB4BD7F90C0270DF3232E1E0E2D21499B8E20534A33694AAB59961A3C6381F4DABF0C571537B2B7347638F1D43A693A72DE8C209BDB7D62836834751B2DABEF72765064BD96C0271537C0D71EF1C7E872BA8268347292F465ED59991A3C8F82683472166B4A0B8FD15EDB234A4B6E55
	DDF76405386E8820534AAB5415E75A951ABCE7GDD11A6DD27AB6F3D8F4632EF512622E55F24BCC56F4D38GBF9E00616EAEA03819100E69457E72C33A0719G69A400CC9B62EBA91E1C8911F33BBC01FF3E30D5A517148F26D38257A929B7F899ADB0997F6AA7C37B0D079A707B0C74B172A74056054F9D8EFE47790707756FB83FF4D87F0E732B0757B3BC70D0FF8905792F9F9E798E74AF8F873F83FD30AC381E95E57AFAD615696BF932ECE4BD4F15855373FDEA6BD7D2C8CC387B9D0A55AED356EC8EA5FB49
	3DFDD8C9B777BB58B21BF077E877D6051C087260F5FA30DC3FCE0F166B57690972116B741572E0F5E27B88170E13F303BA9B6E9754D49C1082778B58A29BABE414CD6E73DDDDE2E32B45A5F7220DFE2110B36D007B6967CCF65F19768BFD86DE26C9F1B611339CB6EF7C4FE63C6B56EE43F9636AAAE273E9101E85B0DF3443713DA3758B0BF55AA5BB33788E5D7BCB3431B30BE4017FBE45CBF69F1E1DE5B476601D25CC48F35951CE25C7027B704891FD9FD69FA167496C9E0956CF75C73608BC5F9ECC03A25B09
	69D8198B14DEB1393B65GBF37D2FA16970C5EF4041C1BF7D84DD6B7BF0DE6BEFBFE53BA9AC39E0F6C211BC174128B7355F477AA6E2B930A3336249C65FD29B2F87DE7D56A577F1B15416B7FCC656875FF2EB2F87DFFD8B9FA7DDF298C5696783DFC0FC1D55703827FA7C50DAF957817557833A5C2175CE341ED337B183EED769D8BEE1B52E3235B666831603629BBB63AED4E9E9B59B6B37B55ED1674912C9D4EB70F548E636C415A614F68DE5C923B369DAE21784400F65857056DD0F2449A4C8EF196BC20764D
	A1216F41C135EF8E88FC3AD7DD8F1F0FE33DEABEA5821FF5C3DD3ED4543B37866BBD5F91D847F840AD83C8F37A72D87D9A4D249EDB1C5AFADF2478BC27365EBF6D44FA97C3DE24936B7D139BFE6D72G4EFBAE537BB8C9D0AE4305731E3994178D69BC01DB19CCF025102EF3A14F97127DF89EC41EA7695D9CA314B38B7A2FD01C8B52E182778E45C5C0BACA707CF62A9F4FD264D9CA6F6A4C02F2B1027EA10ADBG69F801CB20382510DEAEF87EED3A5AA74467EC7C786ABE9CBFB27BF07CD8C63FC72F03F49B00
	520763473F5AC60A5B09FD416276BA5D47086A537A6FCE0A0F6E537AAF2B997DB78772F25CD8073E956AF8A367102C3ECB693D20B0A89761C69BDCD8C9F053A19DADF053A9EEAE2497881E57B254BC4B854FFF49C01E71023E5200BC17885C6D866419A4F8562C7273DFB1360BA7377A614577CB16ED6AF67721E8C747F255F8DF3BCB5F6E4767906267EFD7576530A8FF4F8EB55E477F7CCEBFBE07913FA14F8F7F21683B7D71E5621B31DDEABDCB058D77507B94999E702187EDB3EF8F79064EE69AF276ABE0CB
	8F353578D62E985275GAE00719E36CF46660BDF9BA463F0175BE407AE16CE1E3C8A217DE28152A6C079A400AD00AF83E8D5D066DB9F1278E5DF889C60BA1FBA6C439D6438168FC87D4890F35AE66AA067A26CBB2EB02B5FF7E08A53ABC141F94D1563E49C9163G1F3731B6735BE6ED1BC9D2FC6DA433BFD81BF9331A34015CFE56B6D68B7C5B94DF254233B6331E4E79C83A9D726675E3FCBE520B6B377C81EC47B1BD12506FF955FBF3CF2B943CFD7F39404700DBAA035579E7E62C33D754794DAEDFBDFF2DFA
	9F6C3A75F50E3825B2D81D4751F31279D05F048174FDD6B95AA1C960BE216B797A41D8C37B20A8C747A08552938751DE4DE61F0DBED63DA7F45EFAFB7D77AD15415A3AC3A2EBB8185E15C48D125FB74ABFA9093C17B06FE01A17FE93C5538903581FECCD667987D8FEA9CD57FB451D6D94FF5E6D27FC3C1FCD71673D0826AD02F6CD2ABF6F1DB49DAE724F2479733E3D5E47FBD61ABF6F67E8FA26207DDD1ABF6F7574BE6EFCAF7A7C6B7B34FD55922F2FFFDA6E8D56D7196937C0D9C32CCFCA95F88745672A707C
	2EFBAF5EAFAD063C48A10C43F7EDA2DE07441A355997FB13D46F1B1AEFFD9C6E3F25B218CFD2D77A7B73878DBEFFDAD67A7B73BACD57FF0A7E7C7ACAFFFFBECF539648E73CFEF71A70E2F9320146916431F4B4CD4F7C9447576CE36ABBFF4BBFA5E52A6878BD9F5E63BE45FB5229FBF1EED67599742F1FA1FDC9BB39DB617B9E49B428D4D41CF2DB4DB5266EC6BA5A337966193D991CFE56E7B85F9C22BC17C0DA9A0671FB983F4D73FE005F07C57F4D635252DA7DEF73969BBFCFA99EE631E99C765DB51BE7FFAD
	05497C2782149981797307693EC901FACCA7762CCEE37B2BACFFF721BA1F2C731E1054799F796593FDCDFE74777865939BD5F3FAD6379F6C423AAD7F41AC49760D71C1FF4BAC6BF3D627D27E0CC8F8A839EC5DE77C5B37373B02FC8F59C8E6B02C7E5F320AB3744F312D2DEFA1380510CE9038FB0C587EB22EE37B7B37EAD20FD6C807814CG188390A7725F257964BBD672ED74726B6CDB6F5C9B183F398A5BD0AB278B63BC88EE4675316F7386EF2745C2867163EAEEEF032A4C1FA3A37C86314D533DF5BCDDF5
	A3D067E4AEAF1503914AD8GB67274708D5F9A0771FB470C7C968661172A0284A81384301357A7B34C470F75858F551078F13965AE596D164DAD56CFFD6D78E9BAB75882B4F9GCC7E6E72BAA9F81D13B5BAB4811DB94CD79FEF007C7E827AED1A55E135775B29F4A6FB92EDFB6321FCC49876DB3B69F71EF613D7D376B81D074C05F27343308F7851476A7B26D98F5BD2D02ED66744B0E6C769B6DF9F406F14336FFC200FAD6A1615F372A07D6AA1FF8F36452332DAC671E70D416F34F25EF57C39FC6343DC4669
	E4350C73B633104172FF1D92CC8716DF1EBAB2FDFC5A48743B53035133367D42BEEC6F4AED585EFF3F9F5B7B345B303D7658483830C424951AA65F3D73FBD83DFF4E18386436115BBBBF87367DB9B2D2C749E7711FE53F19784F9887EB58F13E78AF9E270D7F4815A37B67E7ABC7766FE70611691FB58423E7E37D70D9C968F2FD182CCB9D72E02E6F9BB47EE5F03E626E177770380F9AD447FD592AF13A3FBFC320F29C7F06F51CAF6E192F8CFBC33BA33DB9C43A261B26E3F351B385E40E91BECE3B87E5247848
	F15ABD289BE21E9385F9E93763FD673FB9C746DB8B244783CC87188D9087306CF6F19EF426D89A657E285169EA1487E41B6A780955EFC1414D560F7972FBABF1BEB17BBBDF16665CD19774DB7A34714C07F97C197F46C34964496AD75EC46AE704BC97C0A4C0B4C08C402271D83F3B4F70EFA34891D7352218BAAD74978C44611DD59E742149E68D2B5BDC2B3ADDAF65722589B7BF874866B479AADA967B2F6F8B5CDF2AE841BD224A89ECFF4988CF4B84F6CE4D7A03470D589F046968036381768711D0E6668456
	9F103A4D1D206D8FFE1375C7F80EF03D913F45EBA4BC74EE657A7653CF1E736DD515F05C249D643BC04D957F923E4FDDC1E43738CC64C36EA63733079DDE1C2E35481D3DB5CE726391F4DFEBEB3776DE9A3147FC28CBBC318DAD73EE0FFA9E1B48ED16F3875EDBFB2397770F0B81370D4A17685EB97B376CF8A08E7B2CDFDC10C47C3E1E09711B7315A7243AFF2ECB5999A4FEADF7B07F4C3943B74EAFF06726B2FDF64B284FB2DDFD586F46696BF32145773E6E22A46C1FDE1EAB297AD33FAF3873CB4A8E95669E
	A8AB5EF3F3328EDC723DDF3C2C1215B296D90C4E5B6A237B7B4E315031B63E6BA3C2473FF3BAC3FE8B6F1229C777547AB470E2DFAF9BCD5D9276916B2E10B9C17E1D589FE4B76E8846831064BB877615B43647A4F19F3272CE5E9E61E93913FFCFCC5B6326E2519E6F541F9F7B35C7A8B373CE5F78BC77CEEDFBCC2FA663E740954D92BD9F69456C32634C667D9FCB428EBB5A687A98E4A588DBFC6FEC41883640CFE299168E61FCDC3BC048787B95557B043D37FE1F638B37D486334B5F1D8BFE3E7B4DF37A673B
	F34F695F5FF8765C48F7E3DEB8977CEE4C0F4F29FF9F1079316AAA69635242E15D8AD087508AE08BC71FDEBBCF621BFD21249D6BCE183B4979B27935E2856557B4852FEBC353683FF15C5CC44EA1BA1C0A6234937E623EC293711762F4D1A443DDEDA2B6F2DB3BAD8AAB4A711DCDA4B6EDF297C533797ECF69BEC0B854EBFAB82E812E527EBB86528B856ED9F1E690AFF06BA92E702ED8C363DD3897545102F324F640CD3B8B4704AF3760FE4B423B441DFDDAB663EE30B1C0595DD876A5A37657CDF70F353F4636
	38799AC9B1BE173A11CF785DA8F3BFBD7F0E0174A200E5F763DE5531CB44C6BAFBA6E200E4BC0B455816F18F2EFF16785D2129823C711E1167AF7C371D5BC9FFE601326381E68244G4481AC8510A6469A328172812AGDA81ECGE1G11GB3G66GAC1808B1D958CC3EFB8E564F700DB79A18EE131504A6FED3A62B75DF8C7C92A70E2CBF1337618A19D7F9F8BF4E3F89AFC27FE7CC823DA721CF376EC77FFBA721AF9A69EFEC4E0274FC0045226C29E234F152241B1DBF3CEE1204FE3B5B562A7B845635723C
	22B6523E456F60501F60E2A3923FC7F4B5F8FBBDFFF574765AFA157E0E9734B363100B5F8D39FA15584B59556511C53F50F1B5CB609ABCCDCE0F15E842FF9B433A0236F9B73D044246494C9EF1AEFAAF0C13G8AC0783D6CEEA523D3FA69B76C20A033C9DAA731899BC3DF95FBD6D177865FE3CA674F59D00640C60E1BFF2FF68C4D6FA5FE30B85D56CF1D8E45E42B36D93B9D24B7647BB7B5223D445D0B6D4552925ABAEFA0C43A3F3BE9BAFE4744C86F325D97F0470462B36F8B38E3A27A3EAD10B7FC9F56F711
	512FFD81BE713E316CF9FC5C2D55BBA342C71793916C6C65895A4795C7E84FDE1E22782A886D594B21AEBCFBE9023C59912877A15D3B01DBA673EFA9A6874A67FBC85467D5105F120C38B4FA0E538A3857A40C7DBF3487EFE3396D233731BD6D7AE3627E7640B13134DDFFCC2CE8D70F098CF73C1D44EBB774172D22DC1D40FDA4F08D14A741DD361A950B401F27FA91FC3DCC84A27EA255EB1062DAB96E0A8A779147DDEBA76D9E2A8C4D8AE94D6D24CDF7D9EDB6163650B2D687F49B429EF62A2355E5D2696D96
	380F04FFEA65C07F544B681FDE91FF71C521353737C2245B39BF34F69AC34791E532361DBECF71E113356D34F9BF3653091037ECB246FBE331DA3E71FED8FF5CBFF2BB653FD954036DE7984A4F3E9F6794D31B493DD05FB7E174E35BC62BC32E21DDFA8D79D5BE1C2B8C7760783874FE9C9F3F4C3E2F1F92EB2884B0CE4171F1158D7BD45B947D79916B77F753B3FA56CFFF54CB4A2926B6C5AD64FF1C26FA0A9ACF1CB4621D8F23A4FC72316ABD21652FF04EFFCBE530D87FF7A346FF3240A59AB17EFDB8B71DBF
	4EG9B4F1902F35E7F3008F970940C1BAFF5517D70C8F1E6CE53F5A2FD64AFF657034850371B71AFA6C3EF2F1C553D11766B9650419B0976781AD8970C0F4438B7FDC062164F2DE8574870B9747766B89E3BC62E7FF37BFDF176A355FB6B7EDB9FF3FD37D48633D3E7F1E04CD90A55B1473F636C992D8F0C5344G7F0EF3D4BA6DF7C90C6E36D16934407A562D743E4632C8ED1F3B1B6217C7EA7B5CD9627EFF4683300EF9G5B46BA3F7B238B811F704048FDAE6B4F6E6A8E5C530D92FB17C940A3EBAA2CA9819A
	2732B3E8B67F4D30627C57BCD5FF7E3BD2726DA1F9214C44293EBD2428295A796FEDD7489C60B459212DB1112DA33E0F1A50255EC70D66BA4441F349D41C27F7EA6669274934044F53670BF93A74203E1E69AABD33218C815C9F4DFF5027A77BB6A1E683EBFBB698FEEA59DFC592B110C706FC7E3ECF757E4D3F425D3F9F7D956E7EFDE08BECFB85B6F55B633FAD570DB1DA8A7633BD087D594F0B7C768658BD96B60633EBACFC8C4F326098BE63C19C430FD9884FF8C8A782E4CE43B13C5C0AE3F85E34312EF1F1
	0F43CE6FAAB25D5AEF7CD1525D37609109730B9C319752B68DF5FB1156B7925251G7304EE6F0AB5DB3C2EEEB83FF840620B15978356AAE373FFFC08F49BAD2175DBCFD3FFE4BF246D377E1E62F39F5276DBF5960C09C2480BF8887B2DE3D6357CC440E7BCBC16B55DF6DA5F1CA0E50BF570CC3F9A2AC75D43DA3D4FD0FC5343DA3D63C4AC37C1DE4C4328777F782D69729E01BE72113168BDC7656B6F295ECFDBEEFD9F51F9CBE5307E6022B5300F38E28D1C931651F65488B6B3BFA26E7BF70B3B450F201D6BD5
	B63A20FA7FC9772D37575B37D4862B73C61DBE750FFEFDAA7F1E201B0C0FDDB627C9615FEE32FD216EEE51D7024D96BCC2F08FF7670EA0F78E0FD3FE1F211B74CFDDA61B4777BD6437A86E1C53CD57627C6F69486AB3441CEDD5A96BA57DB3446C690CFF55F45F99A2F9323D5A6FDA09EE8A64C5G4C86D8G3098A089A07351D8C35E23388F7B2A057C1EE830FDD8B6F07836E1797904B578DE480F2D236F057C442A3F9732589A38977226D5FFAF645F2C01FBA14BB8DF37787814615F311279841BFF797E9E90
	7BDB32491D2BD3E04E73ABA9DD115E8B7CA9E598AE353FDD2CC82BD5BFC32CC81B7CFED03A85FF31D7117E241FC1FF4A5763165687DCEE79E12EBB6C65693F7DF50A14FCC5CA27C23C2EE4A83ACE1C09B650CD14458387ABD25FF8A2B5C5CAEB10D6B0C3B6C8E9449A8DD2BAB5D603148A067412BFF4172686CF1FF41DB6BDFF2CDBFAAF705C357A5ED77FB97ADADDF60A1482E2ACB2631BBEC8D6C904F3B219B6FEDBF0D47FCB2558D5FEE5D60DC8A18551CC972BC0459DDD4EE0196BAEFA645C03F539ADA63B0B
	7CF4391FC0AC97C4A07ECE648D755D2224891DC16F9625C5715FCF0A7235C5568E6752FDAAA360DDD1585EB265FD415ADBF50752E2B759EC0A4956DB6DB013336B1AA1C5761D8BFECFA67CBDD806E7323C79B2513BC7C5733FD0CB878863C1E76467A4GG08F9GGD0CB818294G94G88G88GC0FBB0B663C1E76467A4GG08F9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA1A5GGGG
**end of data**/
}
}

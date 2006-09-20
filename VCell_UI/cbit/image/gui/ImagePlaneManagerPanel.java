package cbit.image.gui;

import java.awt.*;

import cbit.vcell.simdata.DisplayAdapterService;
import cbit.vcell.simdata.SourceDataInfo;

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
	private cbit.vcell.geometry.CurveRenderer ivjCurveRenderer = null;
	private DisplayAdapterServicePanel ivjDisplayAdapterServicePanel = null;
	private boolean ivjConnPtoP3Aligning = false;
	private DisplayAdapterService ivjdisplayAdapterService1 = null;
	private boolean ivjConnPtoP5Aligning = false;
	private ImagePaneView ivjimagePaneView1 = null;
	private javax.swing.JLabel ivjInfoJlabel = null;
	private cbit.vcell.simdata.CurveValueProvider fieldCurveValueProvider = null;
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
private void connEtoC1(cbit.vcell.geometry.CurveRenderer value) {
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
private void curveRenderer_This(cbit.vcell.geometry.CurveRenderer arg1) {
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
public cbit.vcell.geometry.CurveRenderer getCurveRenderer() {
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
public cbit.vcell.simdata.CurveValueProvider getCurveValueProvider() {
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
			ivjDisplayAdapterServicePanel = new cbit.image.gui.DisplayAdapterServicePanel();
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
			ivjImagePaneScroller1 = new cbit.image.gui.ImagePaneScroller();
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
			ivjImagePlaneManager = new cbit.image.gui.ImagePlaneManager();
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
			ivjImagePlanePanel1 = new cbit.image.gui.ImagePlanePanel();
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
	setCurveRenderer(new cbit.vcell.geometry.CurveRenderer());
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
public void setCurveRenderer(cbit.vcell.geometry.CurveRenderer newValue) {
	if (ivjCurveRenderer != newValue) {
		try {
			cbit.vcell.geometry.CurveRenderer oldValue = getCurveRenderer();
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
public void setCurveValueProvider(cbit.vcell.simdata.CurveValueProvider curveValueProvider) {
	cbit.vcell.simdata.CurveValueProvider oldValue = fieldCurveValueProvider;
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
	D0CB838494G88G88G4A0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E165BD8DD8D4E53A16D6D8E8DAE658354D4CDAB4F2295048E5DBAAB55C5028502C28B06DE6773265365E3B6C3D6E5E43G0A8DAAA695AAAADBE6D808227807D400D820D8DC4111121201B94068400CB38785853D6F7773FEF3664C99405975BE77F92E4F73FA4E77FE5F777EFF7F5F99244CCDE3D2138C19121414AA71FFFB13A42921CB12F28F8F08C45C30F613D1D27D7B9B40EA695ED3A9709C83F9D9EF
	1A0C99520E2F53A09DC64A3EE1B2FE846FEB245DDF171C03979FFC32A06F2D372F4F6E1D0F6BD364330C26C7CA6970DC89908EB872CC31937E9325AC0E0F63F804A403A44D25E5E6C8599CD78169860091G41F6D6BE921E4BA9676B0A7671F275430325453FF39AD690BAAA1A88B98ED6371CBFF3247FC8CAF090DE6FD2B9C91DEEC8078190651325392E4F60D9DCFD26652B26C62B12EFEDB33748C5C51564D1EDB3374BC74D6D706A2C06D71B54FA1650AACACA23F5A5E9872497D35CBF4B04FE883CCF850893
	78D72C843F4FE9B2D68334BAD127AFBFAB66B257DDFCC4DA50B2ED79AA9FBA8DF0B2DD021DEE1D86FD5E183C165A7F61AD444E8BA0AF4DE5B2E68314GB4G8CGD8CF7DBE65534F61D95A289C343735595BCB9D0EB21B45F954EE11ED70DED484B99CF7CCEE32BBAD1244FC1BA8656948539B30FACB132E349E33C970AE74F3483109D25C43A38D4BF4EC9297FC519064E23688F539ED42F8FF6D13F70C4BB38EBDF91F10543C2537F6AECB55611DBDE76732851CCE220AF739C2FCFD2E1978BA8B5E1328AC9007
	94FF8345872870ACA693E84C10F4986415F4E0EC542DC4D92AD246CA9985376B5AA16809C0C3F48713E1C107D6161F4E905A9967984F7DC216EBA9BED005E7324C27F1CA52E967CD4658CE131145D7C4AEE923E3AE180C91G31G89G6997CD46DC00620B98E349DB3677B3462A4DCE252C43EA3348CE09550D5872A1BC65CE45E92EECF7A96676A659E4F6DA4D0DB659A571B25336909BB4FA547B9D0D7DA660712D5CAEBB4D0A555E8E544DED32A2BBDD2C4C73DB08FD1B1CB24D2D35364940204DA1A15D1737
	2C01274D6CD20EBBACE6C5F617E0797337901F1CB31B00A29000F7E617373692FB55037E8A40A0E1071FBFA3740E48CE68AE0A0A2A6C76B39D0EDA220994FB115859C47B0EA0F88F3D08B6FE63F3024B6AB699F7F4A31D5BBEFBDFC5275C5AC444B7BB3B300DF5B767901FC279D600G00F1G11GF1GD28F7485BD98CFEF57E1BC152FF8C04A1BFD1BEEBC8D0358AE6CE1F1D45523EDD7C3A8BFA23382F9A3G42GE2819683E4DEB2998BAF6118702EBCG1E11DCAEB50FD61EC679623A755B64B89FF8AD1477
	D20E4972C1B3114548536A7CD47EDC455B608A9FF41677C38E82493D64B1BF576411182985BBB9G06DDC2DF47FC16435ACE1959A5E3EB795E8901AEDB5C1EE77AB41DF1F05977F31D08EE1B387C2BA9BD26939957BEG7882E09BC0814086090D631FG9CG789260DBG52F68FC92497142403G3BGF6836CA5B1A8EC99C0F987035CB1G09GB917CD468AG87C090C0A440C2000C81A663BE008600G00D00039G52B550EFGD483F4820C810882D88410F5AD7401G2DG43G4281622F4518FA4B9A1964
	69BB7427116B0EFEDC4375D65BFF8337916A3E125BE7254ABE5B390D543A6FD75989E99D64367A16270F78707909FE464D7FA7E03E2CBF2D76E5324A174B39AFB10609BFBF52505844FD2E76A57A1B3433755C279BB43EA4B173294A6FAB393F096F377274FE9FF2EF7FBFE03B7FEB407A2E693944CF72B939DDA9179DF23B85DEDCBCEFE6AE71A79F571B6D224E73946F00F940C198F8DD827FE2AE7137D9D11C56468EC52E1195455A5E624E1FC7F939BA9A9D6EA98472DA18CB7CEBBF8F73GD17E7739E48CED
	3237374B349395D5F87EBBF48C17EDF29B88FD505E512EB07C3FEDA5EB1CB3F2971FC37CF96BD2094C81EC9DE4BE42568D21E692038EA72C971CDDE4CAA3558DA463FBDBD2164AC6E900A36B1FBCF8168C4471787CA7B89EEF3EB0DE0ABC65B0684DB5036E3D59B0E6A09B5F42863A47E3A6533FFC2EAF7BA2BA1760F897137551EF497C4A471206363FB5D2D04FEA3DB292663FF91E70272BA3D20FCE6CB0AF71AB6217039D4EF372A10BD531BBEB6DF69B4F7B45C7ABB06F98098E270A663CAF08DC962B4BE1B3
	F715DA4C8E183655484EF356A639G4BB49D7360EB324A67C55E0759441EE5BF4ACEBB41CBB84E86EEA33609851BAD84108661B8EB7B0264631C0A8F30876DB69B8B8F482B98C4FC78C2F3264A8FD903188F49FBAC4DCFEE417CE1108ED665337ADDAD6A7A5957E96B5FAB726BA04F61153F3E957303A1AF52AB7F89119F83F98B3D72DB2C181F88F999573B73199DFE484BA06DC8BE5BA143D41BCCCE0F08D8EF48FBDF13E762F14F72DB73C85C5BD4735AB157931E31E7F0ADDFF8BD1BA3D641339EE0G40C6E2
	2B9BC8397BECD8AE014B15F283FBD65F40E4E4655A5A30DC9C4FDF4C1FA9816AF21F36E3396CG16DF481FD53CDCD4GA9771AA8579D40673081EC9E9B4A4BB1BD9E32E3394C41EC0E1B874FE2000641EABD86BA843D410C4E08410CEF48E0B55FAF4FE23978410CEF62E0B6EF4E38D14DF715934B655E487273B87F129B55FC63C439FADECE3911719D64C1EFDD97160B867C8200341B58DCBD6FA6B53D8522DCB14F2F3D0951D360998D404A45DE94FA70F2092AF21901D86E499E21C7A0ABD79268AED7876FCC
	3E87AFE3B99B608681848732F54454C035FC97459ED1ECA053B7C1C5AFE58872359A30DC5690D6EE4790F739AAD12EAA854BB58CE1766D6465820628636A750F305C38A1CC3E88DEAEF1085A3F77E609F8994A7B7A210CEE55D0F5390BEB053EC319BF868DE5F193B3D44D37EC9D16DBBC1459A37DE6C63762E6F5395475A27EEEE665C668160B9265E2F839053A658233055F0631F2A543744AD5EF40F283F839A05DF26FEF94FCF939051AF24CAE33C5394D43D97BA89E4E56123570348C47FD3ABFEEA563AA5D
	27BBEDBEE76E4CF71D0739C1D151E1BA0A31FD3997685A1A146F9C0E7BEF4929E4AC8805F494C07CF09C031FDD017BEFA34E84CBF1EF95AC73352718B81C491BFD0BFB8C9E3628F4A99BEB6664931E7510E73B85471979F96ABD00D2174B5EE4255B8F3C8F3DE51BFA0CE6385B373D874FE62B5325D442B059097D6D1D5B083CE4FF0223797C63691D9FCB7D5937447D2D1EEF547AC62C6AB198FD68BB006BB9C625EF093DF6995BA77A3CA5DDE263EFBC64E75D0A7EF97523CD3E65919386A6CB53AE14E55805FB
	25065B6E70A9CB752DCC86472DEED972AEA526B21EE1DD5BBCF81EEB12ED367C96595EA6AB4E2E7C168EEBD111F6D6027BDD51AD389F9C8C3463059EFFB0FE51BF1AE2B64368153522DD32C640D88690B00260AF371239A469FBD93937C439E848CF398D4BED3112F2248FE46506DAD13EE2A8235C06728DAF7FC85D8E724155508A4AC0B5595C4EE5794B996491827596G30791245C6F8A82DD6971B2B9EEBD24FD533C7C25B8430818C9809F582EDA4162D9EE145820B79725D5292DEBFFA62D8A9FCD920C1EF
	9F3B7517C1062011FCDFFF245B17C1F7B7A607GB0391F312573784E389DE4399DE55853C6565F8E275DA1BB152E032D20321C6F194C2546E8849B38722BBDF28E112549D651EEB66DA3EBBED72B3D43E64109E3EED56F358FECB5C9580E462ED16BDBFC79E7DDFD83D6359A865C4E776DEFF76B9BF2CBE3F264AD286F48B654F7B114498BC2FD7F52C67C5486AD064724E26469D3AED94985E9D87D6F5A313E856A0E9675A7D13C355D2AD84DB66B85B9F7AB23B3ADE73317AFF19DF02AD2D55C4468275911FEAC
	50CE9F05746D76159EF44A4D0A996764F758C9BF6632F7B81BB81E596D010DEA3B25192F5335DB60DBAD0672D14CDE36D1EE3BE55E5A181CFBAB5A6DE5BB46F79814C990F215B9BC637B42A9F5FCEF3E034537851E1DF7E01D538EA2C7132617E0FCC29C287FB0A89FA56A2CB9CBF432D8217B66FB424C3E835B485EC213595654E1B3437A955EE00C6251E5BE2F6417E3158342074DE7110774CFA6633EFFC29EB34F92BB365B1DEDE6DBE9275505767D13971E515967C41BB800C630207324F3856A27DAFF31BA
	29AE649D8F6533C6E39D1B8B6D48742A3195EBE91C2A055C5CAA9FDDE43EC78FF9G6369E0974AD88D3C82G181F26F72B7D94B91A79A9911E69F722CC3735FEA079D8ABCAE81BD9DDA8B7633762826A379968548BDA4A853246B143226F1C8C4BBFBBBB2628ACC7BF26D411BDBD980BE54F4E00FB12F43979B50A93FCFBC005793E71B42CA3F5B060EBB6BF397F82997749DE5708BB311F7F4BC514B19AF0A93F90FDF5F70610118412FC0CC840685C510DF40AFF01F492FA100E823890C127669269BB685EC539
	3D4DECED673284DCC29A8B840D17AE23FF326F027957DD681F7FEAD17BA7907091G71G0B6FC2BE45569512E61DAE7CA2CB1EFE99A7212C69E3CC4672B1C84318C45A15CBF62F47192CA5C9A8EB7798147596835288A3B8C1634F0615EA9A30F835DA104E76E4243379EE24531102F46A81B762EE24B365A3AAB3F486ACA6CADB08BFBA689991A878235D5EA67AC9E31670A154CF990BB41CD95BE83F5D611273697F075A95326510ABBA843AEF16FF1466F90ED3ADF5EC0D2E107E881A134B6E947CE689FE4540
	CB917CF62FFD9F5BA9DD34C3741E331263B26E87334FFC6E1735E25EGB497883A46F57F3871774ECFBE94F6A4C1D2EDF7AAF412C806B5AE4719F528DF76BD900777209C4F2D5358335AA93BDC7D33679B9BF285DF3299296A153515C4FF153F9E65888499268AB9EEDB1F6FA947B188B7F3DF02B01AFF90B417G3D42F1C833AEFB9752BCEAA7114BA817BB4DADAD3D93E6F4A7E5A3DD9B509CA768E6EFA85023FB54FE2EAF3BB13ADD9B10EE9C504C3C976946EF485344F627D56915A423F7E0A3522B82DA0384
	3D319B7DE9AB0C66DF854D882037D85014B7690F21B7EF427E08758B211B70BEC75EFDF85E9467F50F40F21F7B2C31D3750E404E574F5375E660FD6CFCFD19408F2478F1AABC13FDB94513F48464657C924F046FACA0FD6EA0C80F8188870881D88410910C6B4CCF73C94C74FA66DEC92C58EE3655F8EC1A6359F7BE51790C5C45532F652762D8A161DE2AF19759CB4C831E95G8DG1DGC1C206666DFD4AF028D3D786E6679DAD444E61417DBB6FDDA9619952D734DE4C9554E37A349692FD2471A6E3B6C0A1C0
	9500EDBC6A933ABD2FAFFD70EE82D7044973DC0BFFF7E5627D2CD7516A1F5DCC2DD7EAB7B6FF5F7A237A8ED64A6A438692BBFAEB7640710C7668715ABB846F78A973DF2ED8E6D6EF0455BF5B0E7533DE322D2FFAF163BDF5E075D677D9CF1A60D90F2F8BBF4811FA5D8322ABBBB6074BBF2EBD17A2F7B93200EE9EG6B87EFEC42FE30FC826E1FCD30107E299B52A3GC2A6E07F745E9E32EFB7955271G09G1977437A8520962073FEECCFFF5DDEA675F5AF888F7CEA6D62752076508EEDB55B4BD6436E77340D
	3E0D67FB555B28C907E868B0BDAF6F25FAC2DEBCC0A2C0E6886889D08B509902FA7E770EAF7B52130EDA357603002B305B4F682848E59D5D661F0E933C6A65EB52C1A1FA75EA5B7D635768D5CF4BAFD4175FCA3BFF7C323D6AE9FD9895226743FF5FC7FC988FF9CBGB29FB099F7G5482B8GC6BC00BE9C3B63C0BFFDD8CA7D666DC1A667BCAFB97B578F3C53E73D3087747A01D395BB25AB5C8765E73F2C5DBFF48A5BFD7483586E27B710F61FB29146C9004209B8AFFA48611F6FA23D6AE9FDD7B5D12F1E6D2C
	FF7CAEF85553723368727B4A691F6F2A7A2C572D4B4F6872CF3F35DE75347A0D5065D759651FFEA77A2C97B2D1AFB673D6F86E2773B12ADCEFA3C7FD477A2FF40F034CBD26CE4418FC6AE79213CBA01D79AB9837FE05B1396602FFB67C1BD7BD2D8D4BFF25D7AF14562B67F98ECD99AD8C92F2A6F77BA767EA2FFA791AF4102E1CF751FA21BCEFEA9FF24E95F23E5B631F1C52253E64CC5015F3882D17924A7232C2FB17B3AF94650C3F6CDFAC3F5DE73D12D03DD87E6C43DE4E5CA0F6EB1A1C64B2898C64EC4E95
	112AB7672A835A36D01CF36DED453EF7D0A8467945DA926791100E85C8907ACE703A0F5FBF3F04FB555346F94A03FA758E51B5F0B64F5B71E06FFEA9FF90658CB278A767FDDE7534F256694AD918CC6AB9F85E20BE648C92F2DECC71CF4E41DE7534F19E22AB676AAC7F78ED722A2765B7D517DF2C1F7CDE772A2775C31CAE3F89EB7DEB0F61FD56DB2C4B6F6CBA7F743B6CD5CF2BDF7AC33AF1365EBFFEE5DE75347CB66B727B131F7CD2337B62D72C4B6F57DE757A47EFC61F7CEAF579DD5E609F3FA1DE75347C
	EC3A7CCAB77A476FFB2FFADAFE03F47925EC720F5F7BDE7572B569513A7CA6F9555303B0D5DD76CD4A9F6979CA946063G98AE061E15C88F1B0CD98FE3FF59C377AA0BA1DD8DE091784194CF4E044839DD37402FCA2DD371A565FD49D44C732AFBA9238546A3570307769B8FB5607799297D24CF002D8B6FAB70FD477901827DBB4E8F97685FF1FE34E0B543838D7517D0187FF8C16FF720E794783E833DE617EFB9576D5217B3E7173E1CDBF675AEE77EAEDFF2EE20361EC34F671C9D6D0A35CD2624599CCA6EA4
	7776E1A5FD2C231DAD3389F517F6EFD570F1947A5669FC213ECEBD057ABA8DAC6CDD271B8AFD69447691769411F303518F63FE41473BF7CA5E7B85EC110D8A554AE667774D4DE2E32B46A1B7210D5ED4481936577D746F4CED6EB36DA25BF2DE26DA31D713339CB6EFECBE0EF75D429F46F963BD743CFB91245342CC461CB0EC07471BFB6B97760579BA4B58C83FCF2C8AE3E796C9827F99455729706CACA35402F716AC10979506F64A5C635B076B77687B70EFFB48F9B23BC72275D36E1EAC11675E0329D464B6
	E2BAD6260056D74C4E96594B4F7BE8FD16672BFE699EF2EE5EE835D81D7CB41A79EC46072BE88C39DC320BEE8651CBAE4CD769ED6FF1DF9DD26C878B76A33F41FBFD6BBFEC2F3E7EA3777A567F4E3DFD6BFF5FDE5F7ABF3037EF7DA76F755596182E4F37ADD7693ACB60EFEAD7638B857E10DD05BFDAA0E411767B364D327D7A36D9315F37EDB27777ED1B8D7BFD5BE6737E3EED33EDFF6F3649F2285BAC69A3D8BBFC64D4EF6DB0EF122FF6D8E4A66D2AE412369D9E24782AC95AF6985D006D308E72A2A661F968
	DB8E35EFF68BF96FB92B764DCE016FF02A75F07B384C2526D3A070833A54658B055E49E2BFA1E732370E05002BG28D765B17D8E1CA2FAB8A6EB75AE27786E49DA3DE77E0CFA87C0DE42E4547B70850FF6399367BDCF517BB8550F180C8D0F603CA76FA24159A05DADF067A9AE9052239FC11AC55D9EB4F7A14D49F45FAA844AC5087A6BFB687C8A52F3852E196296C2BAD1505CFA4903E6A1529CC76F6A240543DCAC9C6BAF3FCCF01BA1DDA8F03594D78E695AF024791124768971B99BBF96276278E189477163
	B03D93BB9A52E1G5161B8FE94377696378B42FD456DBBF49FA3B1DC6B3FC40ACFF9D46B3FEBEA51FF991027BC0ABA4C135471C64EA1193E75F45EBA974AADFC94ED70B845AD06F44A9444259B88AE93521B27A04DE78CEA1A7B854DA349C833D0548FC9C61AA5827747E424D9ADE8BE1462613F9D58AEAA32BC70627BA563DAF53B5BAB5A51BC8F3C3B5D852F7320339B71E7562BF559A34A67E72B71EE7A6FE6FB50590378499BBC70FB915F3951833F8F7187B62965AC94B60C2677A99A40AE0A304DFB86728D
	1D4D5CE56FD04016D3C8393F99F0FFACG52E3G22G622630FDB2B6DFFC70BC99071B1D66B668E26964A96657246E1B49D837985255G8E00E1G213FC61E7F321744AF7BC2E0A717795BBDEE5CAE0E2B59C274A3C34CE11B39111C0B306F381EED72788EAC0249D59C01731A22A2B20E08F1004F5BD81BB9F3DC5BE62AA35C6D4492612B4D74F413B65099415A06D160EF2278A0951E351971F44EC7526110177D9B0CCFDBB32E5F867C865B71E6A29E17EF1B6AFD3225CE725D3E7FDEE0BC1E392ABC184E33CE
	214EB1C2679233DB4FCA557BF38DD7DF67FF3D2ABC184E6B68B81273984465E3687BDF5A508E558277D28B410D01F438408D21760A0674024750DE25275CB6BA2CFAFF7E672BEF2F042B4A033575D7AF10B59CCC6F8A929FA33FEF14D6AA093C0397F95E2E7A5F927B7CC053453F457EE4A8666FE47963E8FA0C481F596DC93B244CCDFBE70FA76D8A1A0E94F5FBFABCE95FCA5371A27F5717BCE9B79DF4534E396CC9FB8FCDCB11D8774CE5CF5A5750F4CEA47A7C4F295A3E2AA4525DBF55C67A6A2B9E23730556
	C856A7A58BFC9445F72A702C2F2A965F8E85C0DEC2A446E1CD2B085747D126FF2BF5475EDFD56FB754DE7DB89CF3D5F9B01F3812BC7DF9FF395B1FF313BC7D79B6CD0FF99C7D790941531F7BE9BA9272992D17494FD3C8ACEFF6B20DA348E3696C949A9BCF60781A79157A4EDF6D93244CFCBAFE6740FB5E93F8CF3A5200F333A04045087A858D64EE057BFBA473F9252868DB2755D2E6EE292223BD1BEF8E27F3B0D2BF6DC91CEF7E1652AC01F49D0063C97CB62FF38BFE9F96F25FD8A9668D7DEF7362EBFB8C81
	CF32588CF9D275CD51670D490C670A9464399072F32652B5C50AFACCA776ACCDE17B2BAC3F4DA31F2C736E115479F7F96493F94D9E751F7548A7B6AA657519EECFFF0A3AADF9BAD432CD9A6473374CDA27B21D8279B3E756282404206A257CFB48E69F5FC3D611998C53BF4DA24E5027E2DB3B166E9B65CE0371E29A621618307DB5CC4376B7301B68918A6939GCBGB226C3BCCC477C61B41FFC47CA3E0D2E1D4E3E756E94793F3A00ED0850A07566F29AD477E97D5F67755DCE23848F62C7A33777F2D5193F07
	C77CF4FAEF37391C270326FB4B1C4479A5F38801B211G9F70F46CF4779A0751FB6394524BFDCA1FDE328A0A21CCB540A7DC9F4BD3EEFA2CAFA868A17163F04A4D3253A9DBEA2C975C6D78B01D9BB820CEB7G637F182DDC722DF312C606E028977114DB1F98AF7EFFE8215F26D95B2DED9DED14BB633D1C7669F1D0FE61D358EF0DBBC54B1ABBB5E5A368BCA4B38A5AE89476814F1CD45FB74D3843E6C03EDA192B22189D6B225CFDG7FCE6DA27D4E877A587C96D979CEBECF3FFA485D0AED71CBD94DE34745AA
	5FF7DAB96D517C9978DF23B80FA99EBC3E37D9848F167FF30FAF99D87E781E5E6BD7DD6E3D7E6D17FD55E7EDBBE3A9367768A8EC6F0752303DAF0A4276BA35050C8BA5B3B0BD0B2649F76F7C5EF04B5FB3A6923AFD37E77B5F45A3390FFC967F27DB2FA47E9BC0EEE506BB7E83E6E863BF45503BFF3EB7746E5FFFCD6E3D7E4E94DF7559D8DFDFA189D9B61FA36B52F679FC0E7B9BB47EE5F02E626C103772389FF3DE9D777B5E3BD67777E7880C60FE8B1E610EFBFEA66AF5D723FFFD76CFFE569BF9457558DCF4
	F8B219E3444F506EC15DC17189B334FBD05344BCA791726AFF07771D2F2FA463EDA42463G968324BF8D639AC0455362BC681B9DD29F77C7EB6D0EAA791CECD39DBF71B3DF2FBBA5FD817365634BF0BE91B6E50294FEFD394FEF696B1FE6BE6C664F7068D1C9644974DBFB04689782F9D1G89GA951A6E396400EE8546F74B77C5B88F244D52AA86626D67A8B8662704EAA1FF72349E68D53ED05475A3FBC1A775751D7BE873071BA83D4F5D96CFF3654FBFF69FEF167FDC4B45BDF8A01E7E4B4BB27E67D411A93
	589F4C55118747836C8F92200CB4137587C4374C195A7E605D0CF5C4C75F579BF939472A888D3DDB396E7D74EF3EF36FD595F05C100F49F7011AAB7E85AC6F25A2423B46E1A69FF2D7BB6D2759614561032DF25319B2FBA75ECB6BB2EB6F2591FB6400AE05GEC6F6BA7D9BD0F2D1A494AA8B3715EDAD8B36E9F87GEE93658F34A47C3768EBEF9C76D9CFD5C9A2FE6725E37CE65FFF0FD47EC23961939F719BB91379A7E316FB1C2FEBAEDC4E6479308165291825A78F7B5DB8FDF98A3EF33F3F733DA46CBF5A16
	A3297AD30FAF38F38B766D4E4B5939EF4756AB63757EFF3B5FF79FF3733A76D4FFF831FAF5B56EFA93FE6ECFBD5646D369BA1AFF67748D79ADBC1379346E2975E120453EDE2EB537C858C73CD3CB668483E6E1FF30781B0FFD51GA4794E01FDA50D6D712B2522BD4E626D911E113378774434BD56270976B8CBFFFE6C519E210C740CFBFC4EFCC65B9ED3CB497869F5C533C04FC7FAB13B68EBE6736364A7A0389D8CA7687A98F895BF0336383B620BDEEC011F44B2AC9D427938F6BB9193779DD46FCF9B2E7EBE
	47022B4A03596566CA5F673BA3AB754FF7C7D76A5F5F98DB597B5D1889153E6F46BCD8297EFDC066471CBA524755038FBB81C6830482CC95BE7D7AB009EF760512F62CBBE4E9A1674B64570A95647F71915F3AEEBD5277EF9C6FBCC24EA19A6D0AE2EFA37445FD05A344DF0A5DC1118C776591E2A3273525D5E1C5B97E6091FAFEA9B7D3B41B6FEF277BG71205762E7F08DB0D1263FB99CE3B26646A0EE0DB8B3A89438EBE839C1109E9703FBC14F9F47B9D2B86096456018305E047BAD394F629ECF250914ED00
	F4A74030E731EC79896C2F031F6DEFFF0DED71CFBF10942373209969448B1E92BD974FFA8E66AEG954F61DE55D7A6E2A31DBD93B1C0B21AA3E42459709C2EFF9E7538C39384781067FA1F3F705FF67E1174E711D0B68EC0FA9E66D4GF9G65GF5GB60081GC1G21GD3816682A4GAC814808B599B747E2CC269FA55FBD7B6AE778469B8DCC2759CAC2133775758DEA7977813D2A585E65E77C7ED8CB66D5AE5E0FB37FE7AFC37FB7G0D20D87429E5997ABFA696FDF18BAD1BB69B6697G7BE6E359A1E9E8
	636A59D7BAFFF83CDE9272ADBB31D25DA7302E15672DBCC15A37789D9C7A93DCECC46277086AFC37576DF5FD3757FDF574F73C201D55F6B9785D10AF6B083D6C4D4DAED9748BE574379C982E52D5EDF7D909A4FC0EE7D9CA5B3C13DEC2E16364CF96F1AE8A369983908D90BF1B5D2DE4752649749BF6901059E441CF44A6EC8C7DDC6CD9A54E763D47144A1F69F3E0ADB3070D218417B3C7BB068649448F2DF62775023DDDB15BCAED5616F6529B727D1B07C4FB491B036D65C52F734F7E0DC9EF7AD9AF47645F7A
	70B734FF2A1B23DD9FCE27F84B9C6D7A70FB93463E8372E2053E15A7BC5A578B503EDE684F1E47E433D66E86D5BDC5078613CF21FDDC408B5A3317CB949F7402766CE529994FDE02A1AFFDAE4ABDCA56331BE3AE7F16E22EB7FFFEBF180E95C110DF1304385994978A38D7A40C7DBF7F683B0DC9A77BEEE34BCF6A0F092BCEFA0F0919A7754744F5A755E3A243EDBAC96235857A4BBAD1EE33407DA0F0DBA9CD02BBE135A82D823F1D4AC57095B2E108785DD42E7394D747F1C5AA5C8F9C7775C9526EC1E5E8D6D8
	377CA4E9534DD61B0D25ABE899EBBBF49B429EC7290CD607D9A577B1017BC178E7E70337FF76B620FF4E08787BF819FF6DED071F752CD7DC0F457552B4923F51F3356DF495454F1D2BED27C1E20CDAGF995AFE23C5F1226669F82784897FBEF270C5EF596ECBF31D0BE7DA51CD30CBCC66E053A3F89239F5BD6D95B65B25A251711DF6543394ACC8B0E0F65AF6178781F6C7BFAC80FG88F989474713E2FDBD75A57D79916B771751B3FA56CF4F10C9B95554A63F067C0F53D4D7FE5521EFEA399F1E246B99769E
	2DFABFEE3A7A6B0E762B4A0345FA43890C7FA40113CFE07C3BF1B3697CF18958B863E51C73D6B509F9704B98B7974CF4BFDC24875174E811CEB77F23F4A2BC746D7669BF0C075EDEB953BD140E6511A0C30C304787E2DD9077B2463DF98B09DBBE3722DDA343E7F72EF06331EB6472272739636C83557BAF16DD7D18FB642A72E0F69A1F669DF3935354B147E4394952D79F18972777FD4F28BE6BE93FCB62F71D7A2C278556375A69BD7F0AB8ED1F5BC97135F15ABEB7C9966B82481B9A07ED2341E36D147B0A49
	D87CCA6FFDAE6B4F96183D77F47FD36CDDD6830DD600E1G63DEE1E750EC7E5B5404735F889DBE68875CC30A01B28BDEF16FA1A53E221D7FDE73A319839CA6BB34E5E632F54477D1A7F96C23263C4AE44803E7492BB8CFFFCAB3CF7F3C8112FC1E1EA76669F52F6A4B192A125386E588607E6800D75DF232EF93A2573036E7036167A07B2BC8A286BAD4777DAE295E1749D73F6D65DCD59E7C0EF633F75B6BEED637BD96231FB4E20C86027D263E0A7D59BF67FB6C8D30FBACEC8CE757D87898FE26914770A52F62
	98BE0A52ACFC8D667CG1657F08C9F5104E3F877EB7DDD63629E47D1FAD7114956F0F91D243BEF41A39267973F97FBA1E12F21ECF7D15992A01D92EFB2E64723ECBF0979C5E1FCEF730B0EA6F72CECED74EF4E37574FFA0EAB2E477A2795CDC4E7DB3C365F7A1062BB63357D56DDA2A686C15E42F831B6EAD2732F1AE7B2B64C6B4F1A6EDD3A26D3FC148D5041B37956D079C64F534A1DCD71417334F25FAB648E033C2C57D16EC60F7E369B7043DE6F0F5CEBD43E4ED63D5F5BF8757B08472EAA8FBE37E9726EA3
	A6B5F94F891753B6BE8EEC96713A783622D15CAD96F6DE2B3251472A777B7F976CB575FF41DEDDBAFD6AA50FBE155FBFEDA463E3334DEED678371BECDFE81F3B2F841BED1E4F7A4F1CDE78E64CE7F14A6FCEB4127E2959ECF3393F078C20386F6CCE3A9667FFCFC7D61FA1E6EFAAB12C16744F90ED1CFE50FC7799A2F9323D5A3BACC436E848DB88103EGE4865887D08DE08168DE007B300FB6115F9375358F4B868E77B6ACBF1F30785E8BF9505277DE48940B7EDEC82445FBAFE41AC5FFAF64E90B77DEC88C27
	6B949F1FB27C9C8B19CF3079177B6F81313FA5BB6FA72381564F3F17D29569FD6F1FD2D6248C6DEF97AB12D175B3448A74466A9F092E41DF6CD524757A997427FCDDCEE93557651619B94E8133C77F475B86A969C7A915B269F4A4C151D562CC3412EE22441F5BD514BC7F1EE40314D2A9ADE5062C14D208B5AA25D4EA2CCAA9998C59C97E50DD0A9ADCE725CB3469F9E21D527B5E672E25C3DFFCA164D4F926C1B2G1BD61951CDBDCFD6C904B27D93D10F8A0A6AFF49947B1EC71977FA2DA17910CC972B00626D
	4DF6DF192B0E3B641CDD65B9B566B6877969F28F06D84E87C37C1D484B6A3BC50957B5793CDBD47FA67F7D24B75DED11354351F41FAA8470D1EFE2FBCBB8354DD7FBABEDD4EA5A4CB61BE2361DA9ED3710336B32AEC5F61F8B5E5DC0683B308C4FE4F9418DC46E532ABA7F83D0CB878876D5E1AB7AA4GG08F9GGD0CB818294G94G88G88G4A0171B476D5E1AB7AA4GG08F9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB4A5
	GGGG
**end of data**/
}
}

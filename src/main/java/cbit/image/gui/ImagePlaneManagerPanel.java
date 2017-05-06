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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

import org.vcell.util.Coordinate;
import org.vcell.util.CoordinateIndex;
import org.vcell.util.NumberUtils;

import cbit.image.DisplayAdapterService;
import cbit.image.ImagePaneModel;
import cbit.image.SourceDataInfo;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.geometry.Curve;
import cbit.vcell.geometry.CurveSelectionInfo;
import cbit.vcell.geometry.gui.CurveEditorTool;
import cbit.vcell.geometry.gui.CurveEditorToolPanel;
import cbit.vcell.geometry.gui.CurveRenderer;
import cbit.vcell.solvers.CartesianMeshChombo;
import cbit.vcell.solvers.CartesianMeshChombo.StructureMetricsEntry;

/**
 * Insert the type's description here.
 * Creation date: (10/11/00 6:39:29 PM)
 * @author: 
 */
public class ImagePlaneManagerPanel extends javax.swing.JPanel {
	//
	private PDEDataViewer.DataInfoProvider dataInfoProvider;
	//
	private static final String defaultInfoString = "Info";
	//
	private Cursor panCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pan.gif")), new Point(12, 12), "PanCursor");
	private Cursor zoomCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/zoom.gif")), new Point(6, 6), "ZoomCursor");
	private ImagePaneScroller ivjImagePaneScroller1 = null;
	private SourceDataInfo fieldSourceDataInfo = null;
	private ImagePlaneManager ivjImagePlaneManager = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private int fieldMode = 0;
	private ImagePaneModel ivjimagePaneModel = null;
	private CurveEditorToolPanel ivjCurveEditorToolPanel1 = null;
	private CurveEditorTool ivjCurveEditorTool = null;
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
		public void propertyChange(final java.beans.PropertyChangeEvent evt) {
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
			if (evt.getSource() == ImagePlaneManagerPanel.this.getdisplayAdapterService1() && (evt.getPropertyName().equals(DisplayAdapterService.VALUE_DOMAIN_PROP))) 
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
		}
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
	if (getSourceDataInfo() != null && getimagePaneModel() != null) {
		double wd_x = getSourceDataInfo().getExtent().getX() / getimagePaneModel().getScaledLength(getSourceDataInfo().getXSize());
		double wd_y = getSourceDataInfo().getExtent().getY() / getimagePaneModel().getScaledLength(getSourceDataInfo().getYSize());
		double wd_z = getSourceDataInfo().getExtent().getZ() / getimagePaneModel().getScaledLength(getSourceDataInfo().getZSize());
		getCurveRenderer().setWorldDelta(new org.vcell.util.Coordinate(wd_x, wd_y, wd_z));
		org.vcell.util.Origin o = getSourceDataInfo().getOrigin();
		getCurveRenderer().setWorldOrigin(new org.vcell.util.Coordinate(o.getX(), o.getY(), o.getZ()));
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
		this.firePropertyChange(DisplayAdapterService.VALUE_DOMAIN_PROP, arg1.getOldValue(), arg1.getNewValue());
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
public cbit.vcell.geometry.gui.CurveEditorToolPanel getCurveEditorToolPanel1() {
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
public DisplayAdapterServicePanel getDisplayAdapterServicePanel() {
	if (ivjDisplayAdapterServicePanel == null) {
		try {
			ivjDisplayAdapterServicePanel = new DisplayAdapterServicePanel();
			ivjDisplayAdapterServicePanel.setName("DisplayAdapterServicePanel");
		} catch (java.lang.Throwable ivjExc) {
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
public int getViewZoom(){
	return getimagePaneModel().getZoom();
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
			ivjImagePlanePanel1 = new ImagePlanePanel(){
				@Override
				public Dimension getPreferredSize() {
					// TODO Auto-generated method stub
					return new Dimension(getDisplayAdapterServicePanel().getPreferredSize().width,super.getPreferredSize().height);
				}
			};
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
			ivjInfoJlabel = new javax.swing.JLabel(){
				@Override
				public Dimension getPreferredSize() {
					Dimension size = new Dimension(getJPanel3().getWidth()+getImagePaneScroller1().getWidth()-10,22);
					return size;
				}
			};
			ivjInfoJlabel.setName("InfoJlabel");
			//ivjInfoJlabel.setMinimumSize(new Dimension(50,15));
//			ivjInfoJlabel.setBorder(BorderFactory.createEtchedBorder());
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
			getJPanel3().add(getDisplayAdapterServicePanel(), constraintsDisplayAdapterServicePanel);

			java.awt.GridBagConstraints constraintsCurveEditorToolPanel1 = new java.awt.GridBagConstraints();
			constraintsCurveEditorToolPanel1.gridx = 0; constraintsCurveEditorToolPanel1.gridy = 1;
			constraintsCurveEditorToolPanel1.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsCurveEditorToolPanel1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCurveEditorToolPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			constraintsCurveEditorToolPanel1.weighty = 1.0;
			getJPanel3().add(getCurveEditorToolPanel1(), constraintsCurveEditorToolPanel1);

			java.awt.GridBagConstraints constraintsImagePlanePanel1 = new java.awt.GridBagConstraints();
			constraintsImagePlanePanel1.gridx = 0; constraintsImagePlanePanel1.gridy = 2;
			constraintsImagePlanePanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsImagePlanePanel1.ipadx = 20;
			getJPanel3().add(getImagePlanePanel1(), constraintsImagePlanePanel1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
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
	setCurveRenderer(
		new cbit.vcell.geometry.gui.CurveRenderer(getDisplayAdapterServicePanel().getDisplayAdapterService()));
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
		setSize(813, 544);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 0;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel2.weightx = 1.0;
		constraintsJPanel2.insets = new java.awt.Insets(4, 4, 0, 0);
		add(getImagePaneScroller1(), constraintsJPanel2);

		java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
		constraintsJPanel3.gridx = 1; constraintsJPanel3.gridy = 0;
		constraintsJPanel3.fill = java.awt.GridBagConstraints.VERTICAL;
		constraintsJPanel3.weightx = 0.0;
		constraintsJPanel3.weighty = 1.0;
		add(getJPanel3(), constraintsJPanel3);

		java.awt.GridBagConstraints constraintsInfoJlabel = new java.awt.GridBagConstraints();
		constraintsInfoJlabel.gridx = 0; constraintsInfoJlabel.gridy = 1;
		constraintsInfoJlabel.gridwidth = GridBagConstraints.REMAINDER;
//		constraintsInfoJlabel.weighty = 0.1;
//		constraintsInfoJlabel.weightx = 1.0;
		constraintsInfoJlabel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsInfoJlabel.anchor=GridBagConstraints.WEST;
		constraintsInfoJlabel.insets = new java.awt.Insets(0, 4, 0, 4);
		getInfoJlabel().setHorizontalAlignment(SwingConstants.LEFT);
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
		frame.setVisible(true);
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
	//
	//Sometimes if a change of data timepoint,variable,paramscan takes a long time and the user moves the mouse
	//into the data display before the new data has fully updated the wrong value will be displayed
	updateInfo(lastValidMouseEvent);//make sure the data value text display is always updated if the mouse is in the data display
	if(lastValidMouseEvent != null){//make sure the tooltip is updated if the mouse is in the data display
		ToolTipManager.sharedInstance().mouseMoved(lastValidMouseEvent);
	}
	//
	getImagePaneScroller1().repaint();
	

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
	org.vcell.util.BeanUtils.setCursorThroughout(getImagePaneScroller1(), cursor);
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
		getImagePlanePanel1().setVisible(false);
	}else{
		getImagePlanePanel1().setVisible(true);
	}
}
/**
 * Comment
 */
private MouseEvent lastValidMouseEvent;
private void updateInfo(MouseEvent mouseEvent) {
	if(mouseEvent == null){
		return;
	}
	String infoS = null;
	//if(mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_RELEASED || mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_PRESSED){
		//if(getInfoJlabel().getText() != null && getInfoJlabel().getText().length() > 0 ){
			//cbit.util.VCellTransferable.sendToClipboard(getInfoJlabel().getText()+" "+(getSourceDataInfo() != null?getSourceDataInfo().getMinMax().toString():""));
			//return;
		//}
	//}else
	if(mouseEvent.getID() != java.awt.event.MouseEvent.MOUSE_EXITED){
		Coordinate wc = null;
		boolean bNeedsMembraneCursor = false;
		if( getCurveEditorTool().getTool() == CurveEditorTool.TOOL_ZOOM ||
			getCurveEditorTool().getTool() == CurveEditorTool.TOOL_PAN ){
			infoS = getCurveEditorTool().getToolDescription(getCurveEditorTool().getTool());
			setToolCursor();
		}else if(mouseEvent.getID() != java.awt.event.MouseEvent.MOUSE_ENTERED){
			lastValidMouseEvent = mouseEvent;
			if (getimagePaneView1().isPointOnImage(mouseEvent.getPoint())) {
				java.awt.geom.Point2D unitP = getimagePaneView1().getImagePointUnitized(mouseEvent.getPoint());
				wc = getImagePlaneManager().getWorldCoordinateFromUnitized2D(unitP.getX(), unitP.getY());
				if(wc != null){
					if(getCurveValueProvider() != null){
						if (getSourceDataInfo() != null && getSourceDataInfo().isChombo()) {
							// for chombo, can't use closest curve method, one irregular point has one curve, it can be very far
							CoordinateIndex ci = getImagePlaneManager().getDataIndexFromUnitized2D(unitP.getX(), unitP.getY());
							CurveSelectionInfo csiSegment = getCurveValueProvider().findChomboCurveSelectionInfoForPoint(ci);
							if (csiSegment != null) {
								String infoTemp = getCurveValueProvider().getCurveValue(csiSegment);
								if(infoTemp != null){
									infoS = infoTemp;
									bNeedsMembraneCursor = true;
								}
							}
						} else {
							CurveSelectionInfo[] curveCSIArr = getCurveRenderer().getCloseCurveSelectionInfos(wc);
							if(curveCSIArr != null){
								for(int i = 0;i < curveCSIArr.length;i+= 1){
									CurveSelectionInfo csiSegment = getCurveRenderer().getClosestSegmentSelectionInfo(wc,curveCSIArr[i].getCurve());
									if(csiSegment != null){
										String infoTemp = getCurveValueProvider().getCurveValue(csiSegment);
										if(infoTemp != null){
											infoS = infoTemp;
											bNeedsMembraneCursor = true;
											break;
										}
									}
								}
							}
						}
					}
					if (infoS == null && getSourceDataInfo() != null) {
						CoordinateIndex ci = getImagePlaneManager().getDataIndexFromUnitized2D(unitP.getX(), unitP.getY());
						int volumeIndex = getSourceDataInfo().calculateWorldIndex(ci);
						Coordinate quantizedWC = getSourceDataInfo().getWorldCoordinateFromIndex(ci);
						boolean bUndefined = getSourceDataInfo().isDataNull()||(getDataInfoProvider() != null && !getDataInfoProvider().isDefined(volumeIndex));
						String xCoordString = NumberUtils.formatNumber(quantizedWC.getX());
						String yCoordString = NumberUtils.formatNumber(quantizedWC.getY());
						String zCoordString = NumberUtils.formatNumber(quantizedWC.getZ());
						infoS = 
							"(" + xCoordString +
							(getSourceDataInfo().getYSize() > 1?"," + yCoordString:"") +
							(getSourceDataInfo().getZSize() > 1?"," + zCoordString:"") + ") "+
							"["+volumeIndex+"]"+
							" ["+ci.x+
							(getSourceDataInfo().getYSize() > 1?","+ci.y:"")+
							(getSourceDataInfo().getZSize() > 1?","+ci.z:"")+"] "+
							(bUndefined?"Undefined":getSourceDataInfo().getDataValueAsString(ci.x, ci.y, ci.z));
						if(getDataInfoProvider() != null ){
							if(getDataInfoProvider().getPDEDataContext().getCartesianMesh().isChomboMesh()){
								if (!bUndefined)
								{
									StructureMetricsEntry structure = ((CartesianMeshChombo)getDataInfoProvider().getPDEDataContext().getCartesianMesh()).getStructureInfo(getDataInfoProvider().getPDEDataContext().getDataIdentifier());
									if (structure != null)
									{
										infoS += " || " + structure.getDisplayLabel();
									}
								}
							}else if(getDataInfoProvider() != null){
								infoS+= "          ";
								try{
									PDEDataViewer.VolumeDataInfo volumeDataInfo =
										getDataInfoProvider().getVolumeDataInfo(volumeIndex);
									if(volumeDataInfo.subvolumeID0 != null){
										infoS+= " \""+volumeDataInfo.volumeNamePhysiology+"\""+" (\""+volumeDataInfo.volumeNameGeometry+"\")";
										infoS+= " svID="+volumeDataInfo.subvolumeID0;
										infoS+= " vrID="+volumeDataInfo.volumeRegionID;
									}
								}catch(Exception e){
									//This can happen with FieldData viewer
									e.printStackTrace();
								}
							}
						}
						String curveDescr = CurveRenderer.getROIDescriptions(wc,getCurveRenderer());
						if(curveDescr != null){infoS+= "     "+curveDescr;}
					}
					if(infoS == null){
						infoS = "Unknown";
					}
				}
			}
			if(bNeedsMembraneCursor){
				getimagePaneView1().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			}
			else{
				getimagePaneView1().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				setToolCursor();
			}
		}else{
			lastValidMouseEvent = null;
		}
	}else{
		lastValidMouseEvent = null;
	}
	
	//if(mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_DRAGGED || 
		//mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_PRESSED ||
		//mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_EXITED ||
		//mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_ENTERED){
			//getInfoJlabel().setText((infoS == null?defaultInfoString:infoS));
	//}
	getimagePaneView1().setToolTipText(infoS == null?defaultInfoString:infoS);
	
	//make sure the vertical space for the infoText is sufficient to avoid resizing
	FontMetrics fontMetrics = getInfoJlabel().getGraphics().getFontMetrics();
	getInfoJlabel().setMinimumSize(new Dimension(50, (fontMetrics.getMaxAscent()+fontMetrics.getMaxDescent()+1)));
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
private PDEDataViewer.DataInfoProvider getDataInfoProvider() {
	return dataInfoProvider;
}
public void setDataInfoProvider(PDEDataViewer.DataInfoProvider dataInfoProvider) {
	this.dataInfoProvider = dataInfoProvider;
}
}

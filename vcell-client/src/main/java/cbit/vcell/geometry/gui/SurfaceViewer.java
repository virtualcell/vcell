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

import cbit.gui.graph.gui.CartoonTool.Mode;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.surface.TaubinSmoothingSpecification;
/**
 * Insert the type's description here.
 * Creation date: (11/30/2003 9:25:02 PM)
 * @author: Jim Schaff
 */
public class SurfaceViewer extends javax.swing.JPanel {
	private javax.swing.JButton ivjDefaultToolBarButton = null;
	private javax.swing.JToolBar ivjJToolBar1 = null;
	private SurfaceCanvas ivjSurfaceCanvas1 = null;
	private SurfaceViewerTool ivjSurfaceViewerTool1 = null;
	private cbit.vcell.geometry.surface.SurfaceCollection fieldSurfaceCollection = null;
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JButton ivjDefaultToolBarButton1 = null;
	private cbit.vcell.geometry.Geometry fieldGeometry = null;
	private boolean ivjConnPtoP2Aligning = false;
	private javax.swing.JButton ivjDefaultToolBarButton11 = null;
	private cbit.vcell.geometry.Geometry ivjgeometry1 = null;
	private javax.swing.JButton ivjJButton1 = null;
	private javax.swing.JButton ivjJButton2 = null;
	private javax.swing.JLabel ivjJLabel = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjJLabel3 = null;
	private javax.swing.JLabel ivjJLabel4 = null;
	private javax.swing.JLabel ivjJLabel5 = null;
	private javax.swing.JLabel ivjJLabel6 = null;
	private javax.swing.JLabel ivjJLabel7 = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JTextField ivjpassbandFreqTextField = null;
	private javax.swing.JTextField ivjpassbandRippleTextField = null;
	private javax.swing.JTextField ivjstopbandFreqTextField = null;
	private javax.swing.JTextField ivjstopbandRippleTextField = null;
	private cbit.vcell.geometry.surface.TaubinSmoothing ivjTaubinSmoothing1 = null;
	private cbit.vcell.geometry.surface.TaubinSmoothingSpecification ivjTaubinSmoothingSpecification1 = null;
	private javax.swing.JTextField ivjJTextFieldIterations = null;
	private javax.swing.JTextField ivjJTextFieldLambda = null;
	private javax.swing.JTextField ivjJTextFieldMu = null;
	private javax.swing.JButton ivjJButton3 = null;
	private javax.swing.JLabel ivjJLabel8 = null;
	private javax.swing.JTextField ivjTextFieldCutoffFrequency = null;
	private cbit.vcell.geometry.surface.FilterSpecification ivjFilterSpecification1 = null;
	private cbit.vcell.geometry.surface.SurfaceCollection ivjQuadSurfaceCollection1 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SurfaceViewer.this.getDefaultToolBarButton1()) 
				connEtoM4(e);
			if (e.getSource() == SurfaceViewer.this.getDefaultToolBarButton11()) 
				connEtoM5(e);
			if (e.getSource() == SurfaceViewer.this.getJButton2()) 
				connEtoM7(e);
			if (e.getSource() == SurfaceViewer.this.getJButton1()) 
				connEtoC1(e);
			if (e.getSource() == SurfaceViewer.this.getJButton3()) 
				connEtoC2(e);
			if (e.getSource() == SurfaceViewer.this.getDefaultToolBarButton()) 
				connEtoM1(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SurfaceViewer.this && (evt.getPropertyName().equals("surfaceCollection"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == SurfaceViewer.this && (evt.getPropertyName().equals("geometry"))) 
				connPtoP2SetTarget();
		};
	};
/**
 * SurfaceViewer constructor comment.
 */
public SurfaceViewer() {
	super();
	initialize();
}
/**
 * SurfaceViewer constructor comment.
 * @param layout java.awt.LayoutManager
 */
public SurfaceViewer(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * SurfaceViewer constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public SurfaceViewer(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * SurfaceViewer constructor comment.
 * @param isDoubleBuffered boolean
 */
public SurfaceViewer(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * connEtoC1:  (JButton1.action.actionPerformed(java.awt.event.ActionEvent) --> SurfaceViewer.updateTaubinFromFilterSpec()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateTaubinFromFilterSpec();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButton3.action.actionPerformed(java.awt.event.ActionEvent) --> SurfaceViewer.updateTaubinFromKpb()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateTaubinFromKpb();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (DefaultToolBarButton.action.actionPerformed(java.awt.event.ActionEvent) --> SurfaceCanvas1.surfaceCollection)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getSurfaceCanvas1().setSurfaceCollection(this.getCentroidSurface(getQuadSurfaceCollection1()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM10:  (TaubinSmoothingSpecification1.this --> JTextField1.text)
 * @param value cbit.vcell.geometry.surface.TaubinSmoothingSpecification
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(cbit.vcell.geometry.surface.TaubinSmoothingSpecification value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTaubinSmoothingSpecification1() != null)) {
			getJTextFieldMu().setText(String.valueOf(getTaubinSmoothingSpecification1().getMu()));
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM11:  (TaubinSmoothingSpecification1.this --> JTextField2.text)
 * @param value cbit.vcell.geometry.surface.TaubinSmoothingSpecification
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11(cbit.vcell.geometry.surface.TaubinSmoothingSpecification value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTaubinSmoothingSpecification1() != null)) {
			getJTextFieldIterations().setText(String.valueOf(getTaubinSmoothingSpecification1().getIterations()));
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM13:  (FilterSpecification1.this --> passbandFreqTextField.text)
 * @param value cbit.vcell.geometry.surface.FilterSpecification
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM13(cbit.vcell.geometry.surface.FilterSpecification value) {
	try {
		// user code begin {1}
		// user code end
		if ((getFilterSpecification1() != null)) {
			getpassbandFreqTextField().setText(String.valueOf(getFilterSpecification1().getKpb()));
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM14:  (FilterSpecification1.this --> stopbandFreqTextField.text)
 * @param value cbit.vcell.geometry.surface.FilterSpecification
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM14(cbit.vcell.geometry.surface.FilterSpecification value) {
	try {
		// user code begin {1}
		// user code end
		if ((getFilterSpecification1() != null)) {
			getstopbandFreqTextField().setText(String.valueOf(getFilterSpecification1().getKsb()));
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM15:  (FilterSpecification1.this --> passbandRippleTextField.text)
 * @param value cbit.vcell.geometry.surface.FilterSpecification
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM15(cbit.vcell.geometry.surface.FilterSpecification value) {
	try {
		// user code begin {1}
		// user code end
		if ((getFilterSpecification1() != null)) {
			getpassbandRippleTextField().setText(String.valueOf(getFilterSpecification1().getDpb()));
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM16:  (FilterSpecification1.this --> stopbandRippleTextField.text)
 * @param value cbit.vcell.geometry.surface.FilterSpecification
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM16(cbit.vcell.geometry.surface.FilterSpecification value) {
	try {
		// user code begin {1}
		// user code end
		if ((getFilterSpecification1() != null)) {
			getstopbandRippleTextField().setText(String.valueOf(getFilterSpecification1().getDsb()));
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (SurfaceViewer.initialize() --> SurfaceViewerTool1.surfaceCanvas)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getSurfaceViewerTool1().setSurfaceCanvas(getSurfaceCanvas1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (surfaceCollection1.this --> SurfaceCanvas1.surfaceCollection)
 * @param value cbit.vcell.geometry.surface.SurfaceCollection
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(cbit.vcell.geometry.surface.SurfaceCollection value) {
	try {
		// user code begin {1}
		// user code end
		if ((getQuadSurfaceCollection1() != null)) {
			getSurfaceCanvas1().setSurfaceCollection(getQuadSurfaceCollection1());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM4:  (DefaultToolBarButton1.action.actionPerformed(java.awt.event.ActionEvent) --> SurfaceViewerTool1.resetView()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getSurfaceViewerTool1().resetView();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM5:  (DefaultToolBarButton11.action.actionPerformed(java.awt.event.ActionEvent) --> surfaceCollection1.this)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setQuadSurfaceCollection1(this.createQuadSurface(getgeometry1()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM7:  (JButton2.action.actionPerformed(java.awt.event.ActionEvent) --> TaubinSmoothing1.smooth(Lcbit.vcell.geometry.surface.SurfaceCollection;Lcbit.vcell.geometry.surface.TaubinSmoothingSpecification;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getQuadSurfaceCollection1() != null)) {
			getTaubinSmoothing1().smooth(getQuadSurfaceCollection1(), this.getTaubinSmoothingSpecification());
		}
		connEtoM8();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM8:  ( (JButton2,action.actionPerformed(java.awt.event.ActionEvent) --> TaubinSmoothing1,smooth(Lcbit.vcell.geometry.surface.SurfaceCollection;Lcbit.vcell.geometry.surface.TaubinSmoothingSpecification;)V).normalResult --> SurfaceCanvas1.repaint()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8() {
	try {
		// user code begin {1}
		// user code end
		getSurfaceCanvas1().repaint();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM9:  (TaubinSmoothingSpecification1.this --> JTextField.text)
 * @param value cbit.vcell.geometry.surface.TaubinSmoothingSpecification
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9(cbit.vcell.geometry.surface.TaubinSmoothingSpecification value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTaubinSmoothingSpecification1() != null)) {
			getJTextFieldLambda().setText(String.valueOf(getTaubinSmoothingSpecification1().getLambda()));
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetSource:  (SurfaceViewer.surfaceCollection <--> surfaceCollection1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getQuadSurfaceCollection1() != null)) {
				this.setSurfaceCollection(getQuadSurfaceCollection1());
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
 * connPtoP1SetTarget:  (SurfaceViewer.surfaceCollection <--> surfaceCollection1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setQuadSurfaceCollection1(this.getSurfaceCollection());
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
 * connPtoP2SetSource:  (SurfaceViewer.geometry <--> geometry1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getgeometry1() != null)) {
				this.setGeometry(getgeometry1());
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
 * connPtoP2SetTarget:  (SurfaceViewer.geometry <--> geometry1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setgeometry1(this.getGeometry());
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
 * Comment
 */
private cbit.vcell.geometry.surface.SurfaceCollection createQuadSurface(cbit.vcell.geometry.Geometry geometry) throws cbit.image.ImageException, cbit.vcell.parser.ExpressionException, cbit.vcell.geometry.GeometryException {
	cbit.vcell.geometry.GeometrySpec geometrySpec = geometry.getGeometrySpec();
	if (geometrySpec.getDimension()>0){
		org.vcell.util.ISize sampleSize = geometrySpec.getDefaultSampledImageSize();
		// force to be 3D if at all spatial
		if (geometrySpec.getDimension()<3){
			sampleSize = new org.vcell.util.ISize(Math.max(2,sampleSize.getX()),Math.max(2,sampleSize.getY()),Math.max(2,sampleSize.getZ()));
			System.out.println("SurfaceViewer.createSurface(): padding geometry from "+geometrySpec.getDimension()+"D to 3D for surface generation - region determination");
		}
		System.out.println("SurfaceViewer.createSurface(): size = "+sampleSize);
		
		cbit.image.VCImage image = geometrySpec.createSampledImage(sampleSize);
		cbit.vcell.geometry.RegionImage regionImage =
			new cbit.vcell.geometry.RegionImage(
					image,
					3, geometrySpec.getExtent(), geometrySpec.getOrigin(),RegionImage.NO_SMOOTHING);

		//
		// get the surfaces
		//
//		cbit.vcell.geometry.surface.SurfaceGenerator surfaceGenerator = new cbit.vcell.geometry.surface.SurfaceGenerator(new cbit.vcell.server.StdoutSessionLog("suface generator"));
//		cbit.vcell.geometry.surface.SurfaceCollection surfaceCollection = surfaceGenerator.generateSurface(regionImage, 3, geometrySpec.getExtent(), geometrySpec.getOrigin());
//		return surfaceCollection;
		return regionImage.getSurfacecollection();
	}else{
		return null;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 3:47:53 PM)
 * @param quadSurfaceCollection cbit.vcell.geometry.surface.SurfaceCollection
 */
private cbit.vcell.geometry.surface.SurfaceCollection getCentroidSurface(cbit.vcell.geometry.surface.SurfaceCollection quadSurfaceCollection) {
	if(quadSurfaceCollection != null && quadSurfaceCollection.getSurfaceCount() > 0){
		cbit.vcell.geometry.surface.OrigSurface quadSurface = (cbit.vcell.geometry.surface.OrigSurface)quadSurfaceCollection.getSurfaces(0);
		cbit.vcell.geometry.surface.OrigSurface quadSurfaceCopy = null;
		try {
			quadSurfaceCopy = (cbit.vcell.geometry.surface.OrigSurface)org.vcell.util.BeanUtils.cloneSerializable(quadSurface);
		}catch (Throwable e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
		cbit.vcell.geometry.surface.SlowSurface slowQuadSurface = new cbit.vcell.geometry.surface.SlowSurface(quadSurfaceCopy.getInteriorRegionIndex(),quadSurfaceCopy.getExteriorRegionIndex());
		slowQuadSurface.addSurface(quadSurfaceCopy);
		cbit.vcell.geometry.surface.SlowSurface cSurface = slowQuadSurface.createCentroidSurface();
		cbit.vcell.geometry.surface.FastSurface finalSurface = new cbit.vcell.geometry.surface.FastSurface(quadSurfaceCopy.getInteriorRegionIndex(),quadSurfaceCopy.getExteriorRegionIndex());
		finalSurface.addSurface(cSurface);
		finalSurface.addSurface(quadSurface);
		cbit.vcell.geometry.surface.Node nodes[] = finalSurface.getfindAllNodes();
		cbit.vcell.geometry.surface.SurfaceCollection centroidSurfaceCollection = new cbit.vcell.geometry.surface.SurfaceCollection(finalSurface);
		centroidSurfaceCollection.setNodes(nodes);
		//cbit.vcell.geometry.surface.SurfaceCollection centroidSurfaceCollection = new cbit.vcell.geometry.surface.SurfaceCollection(cSurface);
		return centroidSurfaceCollection; //centroidSurfaceCollection;
//System.out.println("SurfaceCount="+getsurfaceCollection1().getSurfaceCount());
		//cbit.vcell.geometry.surface.Surface cSurface = new cbit.vcell.geometry.surface.Surface(0,1);
		//for(int i=0;i < getsurfaceCollection1().getSurfaceCount();i+= 1){
			//cbit.vcell.geometry.surface.Surface surface = getsurfaceCollection1().getSurfaces(i);
//System.out.println("Surface "+i+" IneriorRegionIndex="+surface.getInteriorRegionIndex()+" ExteriorRegionIndex="+surface.getExteriorRegionIndex());
			//cSurface.addSurface(surface);
			
		//}
		//return new cbit.vcell.geometry.surface.SurfaceCollection(cSurface);
	}else{
		return null;
	}
	//}
}
/**
 * Return the DefaultToolBarButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getDefaultToolBarButton() {
	if (ivjDefaultToolBarButton == null) {
		try {
			ivjDefaultToolBarButton = new javax.swing.JButton();
			ivjDefaultToolBarButton.setName("DefaultToolBarButton");
			ivjDefaultToolBarButton.setText("T");
			ivjDefaultToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjDefaultToolBarButton.setActionCommand(Mode.SELECT.getActionCommand());
			ivjDefaultToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjDefaultToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/run_16x16.gif")));
			ivjDefaultToolBarButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultToolBarButton;
}
/**
 * Return the DefaultToolBarButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getDefaultToolBarButton1() {
	if (ivjDefaultToolBarButton1 == null) {
		try {
			ivjDefaultToolBarButton1 = new javax.swing.JButton();
			ivjDefaultToolBarButton1.setName("DefaultToolBarButton1");
			ivjDefaultToolBarButton1.setText("H");
			ivjDefaultToolBarButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjDefaultToolBarButton1.setActionCommand(Mode.SELECT.getActionCommand());
			ivjDefaultToolBarButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjDefaultToolBarButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/select.gif")));
			ivjDefaultToolBarButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultToolBarButton1;
}
/**
 * Return the DefaultToolBarButton11 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getDefaultToolBarButton11() {
	if (ivjDefaultToolBarButton11 == null) {
		try {
			ivjDefaultToolBarButton11 = new javax.swing.JButton();
			ivjDefaultToolBarButton11.setName("DefaultToolBarButton11");
			ivjDefaultToolBarButton11.setText("G");
			ivjDefaultToolBarButton11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjDefaultToolBarButton11.setActionCommand(Mode.SELECT.getActionCommand());
			ivjDefaultToolBarButton11.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjDefaultToolBarButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/run_16x16.gif")));
			ivjDefaultToolBarButton11.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultToolBarButton11;
}
/**
 * Return the FilterSpecification1 property value.
 * @return cbit.vcell.geometry.surface.FilterSpecification
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.surface.FilterSpecification getFilterSpecification1() {
	// user code begin {1}
	// user code end
	return ivjFilterSpecification1;
}
/**
 * Gets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @return The geometry property value.
 * @see #setGeometry
 */
public cbit.vcell.geometry.Geometry getGeometry() {
	return fieldGeometry;
}
/**
 * Return the geometry1 property value.
 * @return cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.Geometry getgeometry1() {
	// user code begin {1}
	// user code end
	return ivjgeometry1;
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButton1() {
	if (ivjJButton1 == null) {
		try {
			ivjJButton1 = new javax.swing.JButton();
			ivjJButton1.setName("JButton1");
			ivjJButton1.setText("update Taubin");
			ivjJButton1.setActionCommand("updateTaubin");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton1;
}
/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButton2() {
	if (ivjJButton2 == null) {
		try {
			ivjJButton2 = new javax.swing.JButton();
			ivjJButton2.setName("JButton2");
			ivjJButton2.setText("smooth");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton2;
}
/**
 * Return the JButton3 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButton3() {
	if (ivjJButton3 == null) {
		try {
			ivjJButton3 = new javax.swing.JButton();
			ivjJButton3.setName("JButton3");
			ivjJButton3.setText("update Taubin");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton3;
}
/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel() {
	if (ivjJLabel == null) {
		try {
			ivjJLabel = new javax.swing.JLabel();
			ivjJLabel.setName("JLabel");
			ivjJLabel.setText("stopband freq [0,2]");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("filter specs");
			ivjJLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("passband freq [0,2]");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setText("passband ripple");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
}
/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel4() {
	if (ivjJLabel4 == null) {
		try {
			ivjJLabel4 = new javax.swing.JLabel();
			ivjJLabel4.setName("JLabel4");
			ivjJLabel4.setText("stopband ripple");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel4;
}
/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel5() {
	if (ivjJLabel5 == null) {
		try {
			ivjJLabel5 = new javax.swing.JLabel();
			ivjJLabel5.setName("JLabel5");
			ivjJLabel5.setText("lambda");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel5;
}
/**
 * Return the JLabel6 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel6() {
	if (ivjJLabel6 == null) {
		try {
			ivjJLabel6 = new javax.swing.JLabel();
			ivjJLabel6.setName("JLabel6");
			ivjJLabel6.setText("mu");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel6;
}
/**
 * Return the JLabel7 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel7() {
	if (ivjJLabel7 == null) {
		try {
			ivjJLabel7 = new javax.swing.JLabel();
			ivjJLabel7.setName("JLabel7");
			ivjJLabel7.setText("Num iterations");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel7;
}
/**
 * Return the JLabel8 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel8() {
	if (ivjJLabel8 == null) {
		try {
			ivjJLabel8 = new javax.swing.JLabel();
			ivjJLabel8.setName("JLabel8");
			ivjJLabel8.setText("cutoff [0,0.5]");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel8;
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
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.gridwidth = 2;
			constraintsJLabel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabel1.weightx = 1.0;
			getJPanel1().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 3;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintspassbandFreqTextField = new java.awt.GridBagConstraints();
			constraintspassbandFreqTextField.gridx = 1; constraintspassbandFreqTextField.gridy = 3;
			constraintspassbandFreqTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintspassbandFreqTextField.weightx = 1.0;
			constraintspassbandFreqTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getpassbandFreqTextField(), constraintspassbandFreqTextField);

			java.awt.GridBagConstraints constraintsJLabel = new java.awt.GridBagConstraints();
			constraintsJLabel.gridx = 0; constraintsJLabel.gridy = 4;
			constraintsJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel(), constraintsJLabel);

			java.awt.GridBagConstraints constraintsstopbandFreqTextField = new java.awt.GridBagConstraints();
			constraintsstopbandFreqTextField.gridx = 1; constraintsstopbandFreqTextField.gridy = 4;
			constraintsstopbandFreqTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsstopbandFreqTextField.weightx = 1.0;
			constraintsstopbandFreqTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getstopbandFreqTextField(), constraintsstopbandFreqTextField);

			java.awt.GridBagConstraints constraintspassbandRippleTextField = new java.awt.GridBagConstraints();
			constraintspassbandRippleTextField.gridx = 1; constraintspassbandRippleTextField.gridy = 5;
			constraintspassbandRippleTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintspassbandRippleTextField.weightx = 1.0;
			constraintspassbandRippleTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getpassbandRippleTextField(), constraintspassbandRippleTextField);

			java.awt.GridBagConstraints constraintsstopbandRippleTextField = new java.awt.GridBagConstraints();
			constraintsstopbandRippleTextField.gridx = 1; constraintsstopbandRippleTextField.gridy = 6;
			constraintsstopbandRippleTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsstopbandRippleTextField.weightx = 1.0;
			constraintsstopbandRippleTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getstopbandRippleTextField(), constraintsstopbandRippleTextField);

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 5;
			constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel3(), constraintsJLabel3);

			java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
			constraintsJLabel4.gridx = 0; constraintsJLabel4.gridy = 6;
			constraintsJLabel4.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel4(), constraintsJLabel4);

			java.awt.GridBagConstraints constraintsJButton1 = new java.awt.GridBagConstraints();
			constraintsJButton1.gridx = 0; constraintsJButton1.gridy = 7;
			constraintsJButton1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJButton1(), constraintsJButton1);

			java.awt.GridBagConstraints constraintsJLabel5 = new java.awt.GridBagConstraints();
			constraintsJLabel5.gridx = 0; constraintsJLabel5.gridy = 8;
			constraintsJLabel5.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel5(), constraintsJLabel5);

			java.awt.GridBagConstraints constraintsJTextFieldLambda = new java.awt.GridBagConstraints();
			constraintsJTextFieldLambda.gridx = 1; constraintsJTextFieldLambda.gridy = 8;
			constraintsJTextFieldLambda.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldLambda.weightx = 1.0;
			constraintsJTextFieldLambda.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJTextFieldLambda(), constraintsJTextFieldLambda);

			java.awt.GridBagConstraints constraintsJLabel6 = new java.awt.GridBagConstraints();
			constraintsJLabel6.gridx = 0; constraintsJLabel6.gridy = 9;
			constraintsJLabel6.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel6(), constraintsJLabel6);

			java.awt.GridBagConstraints constraintsJLabel7 = new java.awt.GridBagConstraints();
			constraintsJLabel7.gridx = 0; constraintsJLabel7.gridy = 10;
			constraintsJLabel7.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel7(), constraintsJLabel7);

			java.awt.GridBagConstraints constraintsJTextFieldMu = new java.awt.GridBagConstraints();
			constraintsJTextFieldMu.gridx = 1; constraintsJTextFieldMu.gridy = 9;
			constraintsJTextFieldMu.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldMu.weightx = 1.0;
			constraintsJTextFieldMu.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJTextFieldMu(), constraintsJTextFieldMu);

			java.awt.GridBagConstraints constraintsJTextFieldIterations = new java.awt.GridBagConstraints();
			constraintsJTextFieldIterations.gridx = 1; constraintsJTextFieldIterations.gridy = 10;
			constraintsJTextFieldIterations.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldIterations.weightx = 1.0;
			constraintsJTextFieldIterations.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJTextFieldIterations(), constraintsJTextFieldIterations);

			java.awt.GridBagConstraints constraintsJButton2 = new java.awt.GridBagConstraints();
			constraintsJButton2.gridx = 1; constraintsJButton2.gridy = 7;
			constraintsJButton2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJButton2(), constraintsJButton2);

			java.awt.GridBagConstraints constraintsJLabel8 = new java.awt.GridBagConstraints();
			constraintsJLabel8.gridx = 0; constraintsJLabel8.gridy = 1;
			constraintsJLabel8.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel8(), constraintsJLabel8);

			java.awt.GridBagConstraints constraintsTextFieldCutoffFrequency = new java.awt.GridBagConstraints();
			constraintsTextFieldCutoffFrequency.gridx = 1; constraintsTextFieldCutoffFrequency.gridy = 1;
			constraintsTextFieldCutoffFrequency.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTextFieldCutoffFrequency.weightx = 1.0;
			constraintsTextFieldCutoffFrequency.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getTextFieldCutoffFrequency(), constraintsTextFieldCutoffFrequency);

			java.awt.GridBagConstraints constraintsJButton3 = new java.awt.GridBagConstraints();
			constraintsJButton3.gridx = 0; constraintsJButton3.gridy = 2;
			constraintsJButton3.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJButton3(), constraintsJButton3);
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
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldIterations() {
	if (ivjJTextFieldIterations == null) {
		try {
			ivjJTextFieldIterations = new javax.swing.JTextField();
			ivjJTextFieldIterations.setName("JTextFieldIterations");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldIterations;
}
/**
 * Return the JTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldLambda() {
	if (ivjJTextFieldLambda == null) {
		try {
			ivjJTextFieldLambda = new javax.swing.JTextField();
			ivjJTextFieldLambda.setName("JTextFieldLambda");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldLambda;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMu() {
	if (ivjJTextFieldMu == null) {
		try {
			ivjJTextFieldMu = new javax.swing.JTextField();
			ivjJTextFieldMu.setName("JTextFieldMu");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMu;
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
			ivjJToolBar1.setMaximumSize(new java.awt.Dimension(32, 396));
			ivjJToolBar1.setFloatable(false);
			ivjJToolBar1.setPreferredSize(new java.awt.Dimension(32, 396));
			ivjJToolBar1.setMinimumSize(new java.awt.Dimension(32, 396));
			ivjJToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
			ivjJToolBar1.add(getDefaultToolBarButton());
			getJToolBar1().add(getDefaultToolBarButton1(), getDefaultToolBarButton1().getName());
			getJToolBar1().add(getDefaultToolBarButton11(), getDefaultToolBarButton11().getName());
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
 * Return the passbandFreqTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getpassbandFreqTextField() {
	if (ivjpassbandFreqTextField == null) {
		try {
			ivjpassbandFreqTextField = new javax.swing.JTextField();
			ivjpassbandFreqTextField.setName("passbandFreqTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjpassbandFreqTextField;
}
/**
 * Return the passbandRippleTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getpassbandRippleTextField() {
	if (ivjpassbandRippleTextField == null) {
		try {
			ivjpassbandRippleTextField = new javax.swing.JTextField();
			ivjpassbandRippleTextField.setName("passbandRippleTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjpassbandRippleTextField;
}
/**
 * Return the surfaceCollection1 property value.
 * @return cbit.vcell.geometry.surface.SurfaceCollection
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.surface.SurfaceCollection getQuadSurfaceCollection1() {
	// user code begin {1}
	// user code end
	return ivjQuadSurfaceCollection1;
}
/**
 * Return the stopbandFreqTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getstopbandFreqTextField() {
	if (ivjstopbandFreqTextField == null) {
		try {
			ivjstopbandFreqTextField = new javax.swing.JTextField();
			ivjstopbandFreqTextField.setName("stopbandFreqTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjstopbandFreqTextField;
}
/**
 * Return the stopbandRippleTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getstopbandRippleTextField() {
	if (ivjstopbandRippleTextField == null) {
		try {
			ivjstopbandRippleTextField = new javax.swing.JTextField();
			ivjstopbandRippleTextField.setName("stopbandRippleTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjstopbandRippleTextField;
}
/**
 * Return the SurfaceCanvas1 property value.
 * @return cbit.vcell.geometry.gui.SurfaceCanvas
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SurfaceCanvas getSurfaceCanvas1() {
	if (ivjSurfaceCanvas1 == null) {
		try {
			ivjSurfaceCanvas1 = new cbit.vcell.geometry.gui.SurfaceCanvas();
			ivjSurfaceCanvas1.setName("SurfaceCanvas1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSurfaceCanvas1;
}
/**
 * Gets the surfaceCollection property (cbit.vcell.geometry.surface.SurfaceCollection) value.
 * @return The surfaceCollection property value.
 * @see #setSurfaceCollection
 */
public cbit.vcell.geometry.surface.SurfaceCollection getSurfaceCollection() {
	return fieldSurfaceCollection;
}
/**
 * Return the SurfaceViewerTool1 property value.
 * @return cbit.vcell.geometry.gui.SurfaceViewerTool
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SurfaceViewerTool getSurfaceViewerTool1() {
	if (ivjSurfaceViewerTool1 == null) {
		try {
			ivjSurfaceViewerTool1 = new cbit.vcell.geometry.gui.SurfaceViewerTool();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSurfaceViewerTool1;
}
/**
 * Return the TaubinSmoothing1 property value.
 * @return cbit.vcell.geometry.surface.TaubinSmoothing
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.surface.TaubinSmoothing getTaubinSmoothing1() {
	if (ivjTaubinSmoothing1 == null) {
		try {
			ivjTaubinSmoothing1 = new cbit.vcell.geometry.surface.TaubinSmoothing();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTaubinSmoothing1;
}
/**
 * Comment
 */
public cbit.vcell.geometry.surface.TaubinSmoothingSpecification getTaubinSmoothingSpecification() {
	double lambda = Double.parseDouble(getJTextFieldLambda().getText());
	double mu = Double.parseDouble(getJTextFieldMu().getText());
	int iterations = Integer.parseInt(getJTextFieldIterations().getText());
	return new cbit.vcell.geometry.surface.TaubinSmoothingSpecification(lambda, mu, iterations, null);
}
/**
 * Return the TaubinSmoothingSpecification1 property value.
 * @return cbit.vcell.geometry.surface.TaubinSmoothingSpecification
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.surface.TaubinSmoothingSpecification getTaubinSmoothingSpecification1() {
	// user code begin {1}
	// user code end
	return ivjTaubinSmoothingSpecification1;
}
/**
 * Return the TextFieldCutoffFrequency property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getTextFieldCutoffFrequency() {
	if (ivjTextFieldCutoffFrequency == null) {
		try {
			ivjTextFieldCutoffFrequency = new javax.swing.JTextField();
			ivjTextFieldCutoffFrequency.setName("TextFieldCutoffFrequency");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTextFieldCutoffFrequency;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	getDefaultToolBarButton1().addActionListener(ivjEventHandler);
	getDefaultToolBarButton11().addActionListener(ivjEventHandler);
	getJButton2().addActionListener(ivjEventHandler);
	getJButton1().addActionListener(ivjEventHandler);
	getJButton3().addActionListener(ivjEventHandler);
	getDefaultToolBarButton().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SurfaceViewer");
		setLayout(new java.awt.BorderLayout());
		setSize(386, 536);
		add(getJToolBar1(), "West");
		add(getSurfaceCanvas1(), "Center");
		add(getJPanel1(), "South");
		initConnections();
		connEtoM2();
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
		SurfaceViewer aSurfaceViewer;
		aSurfaceViewer = new SurfaceViewer();
		frame.setContentPane(aSurfaceViewer);
		frame.setSize(aSurfaceViewer.getSize());
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
 * Set the FilterSpecification1 to a new value.
 * @param newValue cbit.vcell.geometry.surface.FilterSpecification
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setFilterSpecification1(cbit.vcell.geometry.surface.FilterSpecification newValue) {
	if (ivjFilterSpecification1 != newValue) {
		try {
			ivjFilterSpecification1 = newValue;
			connEtoM13(ivjFilterSpecification1);
			connEtoM14(ivjFilterSpecification1);
			connEtoM15(ivjFilterSpecification1);
			connEtoM16(ivjFilterSpecification1);
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
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setGeometry(cbit.vcell.geometry.Geometry geometry) {
	cbit.vcell.geometry.Geometry oldValue = fieldGeometry;
	fieldGeometry = geometry;
	firePropertyChange("geometry", oldValue, geometry);
}
/**
 * Set the geometry1 to a new value.
 * @param newValue cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setgeometry1(cbit.vcell.geometry.Geometry newValue) {
	if (ivjgeometry1 != newValue) {
		try {
			cbit.vcell.geometry.Geometry oldValue = getgeometry1();
			ivjgeometry1 = newValue;
			connPtoP2SetSource();
			firePropertyChange("geometry", oldValue, newValue);
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
 * Set the surfaceCollection1 to a new value.
 * @param newValue cbit.vcell.geometry.surface.SurfaceCollection
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setQuadSurfaceCollection1(cbit.vcell.geometry.surface.SurfaceCollection newValue) {
	if (ivjQuadSurfaceCollection1 != newValue) {
		try {
			cbit.vcell.geometry.surface.SurfaceCollection oldValue = getQuadSurfaceCollection1();
			ivjQuadSurfaceCollection1 = newValue;
			connPtoP1SetSource();
			connEtoM3(ivjQuadSurfaceCollection1);
			firePropertyChange("surfaceCollection", oldValue, newValue);
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
 * Sets the surfaceCollection property (cbit.vcell.geometry.surface.SurfaceCollection) value.
 * @param surfaceCollection The new value for the property.
 * @see #getSurfaceCollection
 */
public void setSurfaceCollection(cbit.vcell.geometry.surface.SurfaceCollection surfaceCollection) {
	cbit.vcell.geometry.surface.SurfaceCollection oldValue = fieldSurfaceCollection;
	fieldSurfaceCollection = surfaceCollection;
	firePropertyChange("surfaceCollection", oldValue, surfaceCollection);
}
/**
 * Set the TaubinSmoothingSpecification1 to a new value.
 * @param newValue cbit.vcell.geometry.surface.TaubinSmoothingSpecification
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setTaubinSmoothingSpecification1(cbit.vcell.geometry.surface.TaubinSmoothingSpecification newValue) {
	if (ivjTaubinSmoothingSpecification1 != newValue) {
		try {
			ivjTaubinSmoothingSpecification1 = newValue;
			connEtoM9(ivjTaubinSmoothingSpecification1);
			connEtoM10(ivjTaubinSmoothingSpecification1);
			connEtoM11(ivjTaubinSmoothingSpecification1);
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
 * Comment
 */
private void smoothSurface(cbit.vcell.geometry.surface.SurfaceCollection surfaceCollection, cbit.vcell.geometry.surface.TaubinSmoothingSpecification taubinSmoothingSpecification) {
	cbit.vcell.geometry.surface.TaubinSmoothing taubinSmoothing = new cbit.vcell.geometry.surface.TaubinSmoothing();
	taubinSmoothing.smooth(surfaceCollection,taubinSmoothingSpecification);
	getSurfaceCanvas1().repaint();
}
/**
 * Comment
 */
private void updateTaubinFromFilterSpec() throws cbit.vcell.geometry.surface.SurfaceGeneratorException {
	double Kpb = Double.parseDouble(getpassbandFreqTextField().getText());
	double Ksb = Double.parseDouble(getstopbandFreqTextField().getText());
	double Dpb = Double.parseDouble(getpassbandRippleTextField().getText());
	double Dsb = Double.parseDouble(getstopbandRippleTextField().getText());
	cbit.vcell.geometry.surface.FilterSpecification filterSpec = new cbit.vcell.geometry.surface.FilterSpecification(Kpb,Ksb,Dpb,Dsb);
	setFilterSpecification1(filterSpec);
	setTaubinSmoothingSpecification1(cbit.vcell.geometry.surface.TaubinSmoothingSpecification.fromFilterSpecification(filterSpec));
}
/**
 * Comment
 */
private void updateTaubinFromKpb() throws cbit.vcell.geometry.surface.SurfaceGeneratorException {
	double Kpb = Double.parseDouble(getTextFieldCutoffFrequency().getText());
	//TaubinSmoothingSpecification taubinSpec = TaubinSmoothingSpecification.fromFilterSpecification(Kpb);
	TaubinSmoothingSpecification taubinSpec = TaubinSmoothingSpecification.getInstance(Kpb);
	setFilterSpecification1(taubinSpec.getFilterSpecification());
	setTaubinSmoothingSpecification1(taubinSpec);
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA8FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E165FD8DF8D4D735A85AD43126162BE8E9C50DAD6D0BBE6C451ADEF3B5AA7A70094A3D0CCA15DE531AEBD345E7D42CE91BEA5AFB7283E4A004G0126EDA883841A14B4648F88A1C08293C8685026A405500612C9F2A6B3497CB1F3A6197CGF76D3D4F5A674C4CB9B3A13D3E6FFD6F72FD0BB3FB6D753757DE7BFF1F9321E4E7421A1C5C92C148D9A3487F8E6788C24F9D02F07B1DC71A90A77CE939D1D07D
	5B8530D958BF118F4FE2481BF54DE0AC96D2F28BA0DD8EE9E750E0FC81FEEF952EF7B5FF8CFE6868A9013C0E4FBEF8D7F4BD1F2EC5BD6BE83AFF38901EC5G9500A3CF891E454244F0290C3748F8041CDC402DA6B41F9EAE13F149C0E3GA8GFC59EAC65FGCFE6402DCD0DB2DD6FBFA48A19BF7265EEA4F2D4B291664A3C497253A47CB5276F4FC45737291D04E7B564D5G0E94BECF387F7CFEF8B6F70E8C9D9F68F7C85523832253D9BDA4FADC22649B2F9E8AB81A1A3A82BE1BF5C03C609047C41F10CA652D
	9DA9207C0230G5276A789AECFA4BACC703B912073C9447B6D843F90FEAF81C8FD924BF56C55E6596E1E492F8BAB6ADC791BF44A1579A4ABCF59DF14F2F95D0F663EC86B6061BA626B9E481B82188310843082A09360E5DA7729352F413335DFBA65F139BC6ED62F7724F350F74EB3A8BA61F7D39364483873620047B7A8882CFE178CD7E958938D985FCFEF1A0F79A4319E6BFA61798704542FFDA6F71D06CFD293A7F38BB319AF4AB3951FB05D9BC674F41BB3C3E3B1D477C5C12DBB69B3BF4FDB23215BF04B4F
	73FAE4B9CE156E24CCD2579F25B1908F3F899F29CF063F136233D4F89617C60AA7E953D34B0D731F425868A9C2DBAC7977884535F7EA7AE15E7762F3CB1FE6B654BC9DEE4B819A733DCFB31D2738AD17A8BEC305E7360C53B8A56929E7169B67BD3B5C48626B318652CE137E86ED94E09500103D5CE882E886684C46985BDBF7E01AB156E975C9A7838E67A034A7467BD85DF6F80AC149E76DF07BA52B9B5A1B55673076BBC53FA0537C360E7820BF04AF03467EG68B8A33AC51FD5F2F85CA05D8A4DD77479994D
	D3F51B890DCF24395D8E17888ADCDE8165BED73715542555AF3D639D34CA22C24172575413BA9935DEG09A0GFEB33FECB8C47C6505724F8618477DB02B1E48FBC37439C5E7D31345639989F83BC9C904D559444F3BE95F3190FEAF49C69F2FBBCCF0D54FADB71A1FC3B9DF2F5F2212E3F68C90732D3EF1ECE3370B24DEFA00DE82188B108CE0G48GA8FD9EE3690FBD98CB660DDF912A9E7E34E6AC1963FE1E5778BC0B214E67435B546728AEE2EF90726681A4832C844882A87D8F9C8FB6095E986D1C582ACE
	93DBFBE4FBE97B8A4B1FAB27131F53EE3BCA1FD39DD3771A30B499C7726476C7529BB464E27F20A71355DF201144C2A378219BC07A8F2C43A76BCD2CCD1C347AC5EC85EF79A00045C125C6197D870763E49B5749BAB748B6EE2279C41EA0EC83A06379DE00D781487875BAGE9FB35G75G87818E135881E883B8835081F085609C40D100E3GA7G3AG06G5E8618A436F2FFDE217644C3D912GD281728503318A209BE08AE08140B200CC00329C03518C6084188B1082108ED018EBB0B6G74824481AC84D8
	81108DD01EE7B0DAGA40079GCBGB2GCA728D46E600C100F8GE64F8DFBF1EEE8DDCE47CFFA75067EAB0A614322B0FF354AB835DF5F107DDAA47B31CBC767153074D00CF8C5181CA65DFFC7E0313156210E8D522627939FFAB2C95C3CA87FA66DF88740CE014D017788213103F1F2C4E0E3E7F8BCD58AAC1E881E444FD19D1D35E169E353ACFF6BB4697EBB826B03D7B510FA97C7C537E4963D22FB90FE78653CBFB5107810679DB68F67F90A623DB0CFB9859383BF477F2D01440FD512FC0E7E00A4F6091264
	F08FA979F98709AEFF205F2BCCF1D0577A03A4A6BDE3B0CF61741B8E12B1FE4063F60BF4B060ACF27EE6BA47901D228B0CBE658938A506FF6190D9070D0863729C6765C3EB85B2C7F18648FC092DED96D9C95CFBFD8E978CB1E44AA5642FA1EB43A3A325AA9FA5C17FCA56E8AB61198140668CE3FBF04ED0BE71A5E17195EF2E56DCF85EFDF764DAC0A6795DA7BF491C0159D4F8D85B76ADF42EA3637D4CD6290A344F501596EB47DB05AB63DB42733ABD9EE7AD66FF7DAA19BB3E9D308E4AB427BCCEA7F3A72759
	BBC24A056B3ADAAEFBD3EEB1BC3B2D01FE073B4B6571C8F6284EDA16E7BA3EA3B22F4BAB8EB8EC0E812BACDE16F36F91D8D08B279DCE18F6061230794183C708AFE6010F6682AC84E03E7844E1120FF3BCF9E22058CF2AE2AD29335F08A5AA3FAFD9436A0C7C76527CADB64CEF07F45F5A707CE41E1F8CE903AA1F49FFEFC8ADBFAD027F23BC5F344EE0ECDC971E7F9C4F0F033C792AFCD64E49C662E73FF8B5A042541ECC065F60317B6146ADE1F99764B8E67937B512B8F62A6651D585C467A23EFF10A66B4B02
	A7191F969730D8DCC969EE99C13AA02449FCE68EBC97GAC852886FC2F11509DF5A2DD391149EB06A7196B923AD7G4F74663810AED115CF64ADB62A75AE67F4E900A7F3A72297692B393CDB5CC817C56CD8EFB0562CE765702EE77218FDE7BCC8B7FBBD33AF811E499176ED74A2DD9264AF136521DEDAE79B885D4FB8DD46FAB617A3769139799B546588DCC53A148DEC3E37E20322D7A947899F5225C3FEB6C0D921012E91FA8B5572727CC817DA48660D19054C3E7C0DEAF9BF61F4A59B99DD55C666E773C6A6
	0F5179A5246B85FC16ACC782183BD12D77D8G6996ECE4F3D4D2DEC21F922277E9CEE7D049DB95A1AFE594698A0A58DC9765190A547224B1242BA9D2643597054BEB8AA21D3D084D19D15EACD99E2B37CCCEB72F0879E5D1912B3745C5EA3FAC9CC73A0ACD863AC6EB01E7A700F713DA6F4484524D5A446667C4AF211F37C9DD0E3AC99E77AAF90BA364E5F13A34CDEC1E0F7232C264AD1A627EABD664190A43650DDEC33A16E236DEC0F9BD45EAF98DC66EBF153CD991727E4A69920A593A8365A54B721C1B891D
	4D04F4CB811F86A0ECE6FEEE594C64B13A57F671FDCCC89BB6ABF42BB72B6B63B1CED73285E29940320545F37C96F53BCC590DF48B3730F590364BA5DB5465F8FF8F5219373235D26FD6560E1CDBD9B958587A17CAB25E53BD4EE16B28B5D86D9F03012C2969EC2795C69D361FF9AF5D13A074D3404B76AD173B4918158469E5G69DBF1EE70130D38EFB9F7A4D118F36743F9FA7B3159DB65B9C109B2B730BFB2164746442CA622338F723CA5B89E2EB9225EBBE9757BBD838EBA0E4AE3C15291755C01617E7948
	FAF85A9CBE3F5481133BA00E9B5FB9C26CA57BBAB2DA76C7CE5781612676FC49ECC35EA33CF6DADD7614CD57F2B76814BDCEAE7382D559E77B1F49E3FBEDC7EC05821BBB24C3FE59B6D8D3EF2331EEA373BAB26EB0FD837528EF4E44FDC25F276F5655B7EF9B53133CCD5157F871202C6FD7DC5FEA482F580E7A4A0608BEB20EB05F1C3655C6730D1F392529A9ECD6A4609E6283C3384F5E87BA66EDC739F56F9E1F095C1029946AA837238E8348AFF881F53CD2FFF0DABAB4E6E95C7EC70651CFADA0B7884066F7
	ADA3846FF0BBA40755691890ABAA99FEF804443EAC7A1455BDEAA5EB81D6F7DFF925C52E1BE58F5CA394D8EF4D555A5F0F7F5DD06E229764F67D02D2F74B9EFEB4B78D005935F0846DB2159A0CBD25D866126364C9677D70E4737BE5C36A797D9C20A57BF6CB6119DA0A65596BA4758EBE76B3DB27AE212D3316A58925DBEF573415C0E6A933316CC54556F90FBF1A3B70F134F5500936BA01E6410B28B35E36D965AB759C175970E42B5ADF496B62B5ED307F0FF93929326E7C17949BBA539F4D9DCCC79BDEF321
	8D5DC0937F925A7010CB4F864617457916GCFB6006CFFB7629BDEB6981DGEC5EBC6AD74F1B97GBE156415213E2D1EBAF8DA2912EA766894FDB60F4FA58ED6105E385ABAA6D55335E9F5AB4DEEA33FAB312FFA66BA516B937D22C47A220A4A8B4C9E2F876DA981DD962E2FEE08740F98730C761097E96300AE258CED7FC7D14B764CB2ACF3B665A3F6946CB098DBF6E04CAD3E54A7487B6922EA55D5E151EAF5E7649FC7ABB533651A280ED8399DE5B171379D9893656685C2E36608EEDC86F730D8C8582144C4
	72EF8E66AD7B8D76011F3F4A7BC02029F885FD3646C75A0255B7D44B686CBE246B831AF92F203FBEB62C76178170D9GE5BBD14EF25F872B6BCC8BD9737AE95F276DCCFDEFC5A50F56299E2D3C0031DF6547F257C0196C3CDC1F7223FFD87A1BFE6CBB985FDFB9DF82702C5C09F1F27CED02F7C3792D4E7322BF601498FE3EE7BD0DE92F556116AA947D73A514D3EC027931897DEEF2287DBE4B447C1EE8C27BDE8DD0FB442044645882A8A785E8B2B95D2F82E40F44E9F5758FDAD1674ED1242D5A85F328DD5AF5
	0DBA676F4236718467DB8438D53BD0477BE33970F485D07E6746F83F3D9B7A6D5DA8FF0DE619666CC67983C164DBG38545DA8BFB5C8FC67106423AEBF6A59B10EED35E48F7487FB10BEEEDC2E3BBA667B6C1E77E87FA0C50B090A68EDB5EAB84976BCBC4E7B9C30A5055B53BC0131437C7038261FB3B97D60C4E85D5EBB01F28B4A21CFAAC73A32C9729C71764B7A5F1BC43AA050ACAAC73FFFD2D35F2AF2747B2EA9644B86DC43DE14FF7794156FC7790FF0BAA750AC580B725F532C57543DA87F8B57F89F8438
	0AFDA87F957A9C6476DFB672BE88E8666DC379291A769B76217CBD6B793C92F0E52F227CCE93154F6D3F430471528DB4712F227CEA1BD69F37044BF95A74417A381335E4CFAD404ED9E99C1D76F9DC4A0C0CF68772FC601E2D58771379C0730D77B57B7E591B6C39ABDFE5FD3ED021747D03B0F78A7279D357AEAC2F85E862AA303CCF5A354A1BD201FDD85B616D9A763E616DD775DB6BF6234F8D40D7D009321F9B539ADFDBAA51176E5DB375A5633FE7F774394C933BF99D034E78CAAC5367DF7631BE6094C873
	F99C5187F07DB167E6C7F4346B23FB789C8BEC4A667E78631E50F1E6FBB976956C0CA7B162DEC743EB86FEA65C2E7A0D406EBB1C22F325DE48A731330E635F22F8C905177BAEBA57A369F848DB8D40562BC347493C3AFBBF2CD781E683A4GAC86C85B0F6B57B3CD642CA76A9D088EB7443A5B6A6C8AB9A4403B88CD341F914765F44ED1757CDE3E6FF30244D5B668AC2DB298EBG5A816CD5E8436C23B1EDE88BEA5A407CFC42C67CBC3BAA521FFA40784ED13E041B60E36D3C72DD757D2E7C4DE7F373B45AB9ED
	1F326C34AA251DB31FDCEFA5BEB13DEEB0B683F483B88162DFC71F7C74E8D5AC1F60FD9359992CCCCF4C5097FF39E9DFB03E2421195D377A5E8C79DE334F0C2FF106FC3F0AF9AFEC616B21BEE3FCFF0849E78863E3F13162A459A3DB85F97955B09E82B4827482848166D7E3FC3C52FCB4D6FC504E375B43FA62D361A7F6321D8F6A5EBD0BDE3E45B179D22AC379D8DF1EFFE35A7797655D89365F17EF477B0BAB2AF13F6F813A2FD2F840E0AC87E8BE007DDE371978309B52DE0059G8BG168224839454208F3D
	4DA7E37910DF4D82BF6299EA24ABE51F8CBBE7664BE0CCBED30DD62C7C300D143391723A811CG33819682AC85486065DC923B1C3CF053A9E70DEBE665BC9E13AF3FD60B6F79996AABF44762AB535477E0CCBE1DB691132F21D62BCDE414D672B661F1D5BB7ADD5501D1D735359FA6D3038E292969AC5950BDE9751D8CC8921DAF31FD33599E753E1925166D1BC9701C8D405A4DD77C245D10F9E8C2AD5ECB9A754C2CFCF1B179726B34781A3DB353F7B6A6DFD918BED6665F08D86606BAACF36E55F0D97342F835
	EDA80E602B8ECBA755E9354DB3E7C85BDC86F92BG04FA684B81DAGFA81E655E35BACBDC66E43CC378FBA8578F60FE7C423E94A76AE0830F7FA3EFEB0A6DFC23D961F5BB7B3FDD3B179126B355A4A26AE62DF0D33213036E1F5045F99A1ED43GB2536B31EDAC9E46B3246CFA0C13BA7F4C4A5392132F21C10BCF10E6262FA8A61FC5D35FBDB35437A8A65FE003D6FD6D2DA8532E2FEEB1A81DF6084EC1D697979DB83ECFB5E0DDB883B333F5A2A6DF1226EF760E4ECCDFFDCC3EE58DDA7D940BBF3A77414B7C7B
	995A70ECCC3E3203DAFC770E4DCC5FD7E372B59C54AAF3B32FE74BC1AC33A3B8B39B46E27245E916F94F784C46025A883E6A3074FCCDFD3F0C601BDE79FE9713AFC95347AF71F7FD16F19FFFE9E2E6B6FCBDA6DF7EA1ADBE6B644C74F9E272159D52AA738FF9DBEAB804E5FEF5EAE6B69C0C496754AC732FE6286F8FB17962B44B7CC95E166673B25FF7EDE6B6FCADA65F8A4DB28F98E7266FEACC3E8C4DB2EF65311DFF984BDC39FEE6B69C0E4957FCD80B6F532619759F77C670D507253B8FEB15D9349548B21C
	07F10D7B6DF6B22EAE0074E20015G19G250D86E383C0CFA34EAFC70E11FB595359A708BD373C34EBE6659E0A608BAFF730D12B5C158E1C1F4DE9443A5E38FBE6F57DF2CC3E251AB6B484098D644EC49A3761779132266707E7A378427DE0BE2245F77B1E19157933B179FA437431773D3EC35B1884F9338198EE3E0B3E838669A5C7508F9DF4BFFA952405A6D85FB4A17E9D0AA7E787642E98B91F1047C1F7CF94DB88FFB4DB91082ED5C7A2E9190E4C28BA56CDC3FE1100F357C83C3CEFF4DC7FFE7E58F16D7B
	79570EEB5F4F3F6578E60687996A378CB17F6347235F5F7F54F17D7B7B0F1E5037B37D04361D7F6704361D2BCFC437B36B041E1DBB28AFFF1ECB6F05845C1243A5D2514C47E2103C23823D5079001BF5D1C43ABFFC0FDB40F575739D640C2526897B37BBDA6A04487E2DED50A1F9FCA824DB347A5E3259784696B937C33BBF522A6F235BDB35FD7429D6ED9F5D559A5DC7DFEC0D6E235FCA649D1F08F7AB5E04553F00E7E85B69FB810426CD721C2B63329FBB29DF0EA7CEEA176377A769DDB0CF40B7A0065B7A64
	49D21E270C889D1268A266486F16D0FE496A9B92A34A1AFB2A14676971EFB825678FD6565F0DEC104B5AE973F9DC1DF5F29BB7C65D4B37E874A36C7CEC370344E0DF93BBBF4B61787D946FD4615939DA923F3797043CD49EF36F1B75FDAD1A35FDBDE25677354F9C5B5713E6FDDFFF589C5B579FB793DF76BB869DBE79541579780BCE0C27CE4953D94BE57E2CCD3F0C7F5626DD4634B67DB27E36ADF699D737691771193658E594CEEB17F14825EEB375BC3E8E3AC3632B9E634E2D26EF90F0BC683B7112203D17
	4136E7793A5E03F3061623F8D6E12677EA96C0BA85E045D19C939F74EB0DD799C7C3E3D8E8B698CBGAAG1A1B354749CE408F06653172FC45232E63BA0E77FB5565A4F7D165790D28E5D3B0CCF6BC2493G12G166A58148A7855E1F92C2F7B61F692DFDE68BEC5BF1D001A2D12D5366B485510FAB9D5AB47405BE774E332670CF6CC3EFFC6BFA645B331E352FDC6BFA683E7E2476475B3517B38DF06163533C1465F67D3578DF9DF134556DDFCCE1CFF8CE36B863DEB5B8C69EEG67B10C2D6FEB1EC1469DE375
	B16FD8E41DB15D8EFF28CDC7E47CE1C9EDD3A327FFA484FF0447D12FA3DABF3DD0D77F3D74BE1C223FD63EDF51BB3ADE2527368675E81793E1312E1E5DE3E1FA6A987E5F46C27454221E562865D9212BA7A19826271E615F9D8F0D73BA19FE4BC4087EFA543FB36A196BAADD7DE9932175F9D0464F1B8C515300FA7EB52ABF33F5753CBF952A67904ED92E0568B908FA6E0C6A4F6263FAFAD22F056AB9AC636FB60668B904FACA237A2DDCD7CF5F7AD0BD0DB2FE19C9956F27EAF93C8F5B3474B44A72AD7233CF25
	0F7591CB5B757B35656D5A7D5A436D7A7DDAEAFB6CFE6D09F67DFEAD23BDF63F76D4FB74FEED4EAE350F6A380F2AB56BDC12FDB2DB37AE4C3B55FD0C6273A3C12D31EAFE181CC510DE823092A0C3C387816104EEBF31A7341D62BCE1C9A89EE6CF8C7F7391F6EFF75C931084E15689F29E75877E8EF389242BG3A816CA75839941BDFFCF50C4833792CAE79162A505BC2F83F66C25E25C01F8A108DD086F94DADF8976D896A9F1C09A86F79DE32903E109B957C9D63477A098F59DBD5B846661E5D282ED7F96D41
	76GCCF65C83D85002E3CC05C360F5D12B7A7D8DFB34F57887852663E77F97F4FC5F93CD0721A5B2E65281172501AFECB598CD2D8A3E81FE3375C7B29D075ADB436F7BAD21783E56707BFEE747703EDF9072D2DBF10FE685BD6BEFB9E9B0F61EC45C910960FAA1BD0B63BEC1F1F3A13D08631E263894C827F25C59G412D0634F08AF1F67A3EC68924ABB86E1A08F15140F19B691EE58F246D9C6723B8895273B86E8794379052CBB96EG55318252199C57C177778B4C86E32999F1BF26F321AAC837F05C5DC3E8
	0B450CB17A7908BE47EBD66AA14E2C37DE7C82FDAFE81E19793F1263D3A8BED305E775F2AB1D0711F4E71B41B87FB45A14CAEDDFF58662628C620E0D201D796D0873BB90575D9E4ACB5B9C4775F03ACC0EBB446519BB907784671D4BF18FF05EF40EBB40F99B4EA26EC3B634B90E63FEEDC33AB99CD7C1778553A01D4DF1EDD4C7419BD0D7EFA06E4BDCEFB9473DC777D6A5C847F15C3E89021B8369A40E1B24382510CE673847A9EEB5240BAD08BBBCC93FA18869860E738F90DCBB24FBADF867FC1B935775F640
	A5D8701E71EF2AC99F4857F764B6F3D353C98F79460105F566EC8EFE59D9A0732F34609C7C63D4E671B9E897GAD6770FD4860BEFC9FF2617D7788F11F52FE77B42D7BDAEE4FB99607D367143B22F16EC7F3194EC7DC28B39972D71FA36342BA377A3EB6695349478E2B09DFE87E11107C4DF2ECAA79A3A179445EB5F96A7C4F05642FD17133327DF8AF16AD7B470B8467C3F13A5FB9ECFC131529D7FE4EC93FABE7617E493C14B7091E43F613CCB7774D483618A863D8FD7E162E2D735F02716FAD9E53B40EEAA0
	5D4EF14FD2DCAF24039C37F98CE3B01E63BAA8AE9952860ECB9BA538F4C8E73F05FD4837E91FDB5CE9B056GF4F7A27EC794CF5E0DAE163FDD40701DE39FE4ECAA16F5105FB51DFAFB5D0E8F24A3CFD07643433FB30679DB055F7B71FD16A058A188E7486722643CCE093CEBE077789C939E37E4F512AB9D1D9E3F03DD6A4F6824B1A6E15C26F462DC65BE094EB164FB29AE1755BDC8DA8A15FF7D3CA2FFE300445F2868139C832152196CD783F8AED673B64A3652F72B9C83C8B3C6E97AA05F49E9EE8C1179198B
	66378EB76ADD57CD74AEFE1B704C95CBB57C916A17F2DB28DF8E3F23587DB1DBEC3FAC3021DF8A4E23DD0F59747D726E85C57E692168FE59B70CFEE964327B06C37DB2CAE96C10AFF11ADFBBA27DF26D2262172F8EC5738BEBBB8F51FEFE9948CCBB0F6D29C1996B3A78B9353313549B244DGBDGD3GF3G12GD682E48394F743F8815088E0813083CC814C81C884D88A108A309A20649D147DBDAA3B82526D6F306FC7585FE15FFDA05FEAA05FD7188D6944F758B792163E433E3D318A1E859758F7B34837AE
	4877C0AAA05DFE01FDA301497E78C88BD49A5129AE3B25DBFBCD416793DD5A79B5F219496F55E1B2C859FAD4FCE69D9909BA3A09BFE65AB7E8C1F6187E29B03D24CEC89D123A42FA8B17C16A1554A17365E35417FB43E8DE0CE14746A86D923F0778EAD85E9EA1746CCF2B3FA330CB87DF79DF6847A2010D456A740E30B479F69B99E359F710353F7DC6768CDE9630CF703AC27B2384B2B48873AF10F65BBDE216F96C973457381B64A73DFF87B48408CE52BF4D39201C0BB0F937B9D0DE3A0E3C4DAAF9D9C0D3F8
	1149A3FEB7DD8C177717E114D7F5D1EF8D2E48EB811AEE15BCFB04BC8F37CF521137CDA5EFB6504CD749DB94A16F9C77DF0A0E3CF5AAF9060B0A7F083C7488F9593C3C2BF56499D57272FBE09E5D23482B6A891777FDDE5E469EEDF99BD472ACC0532712A7C548BB4D4BBBCBC75EC6153C39C013281217221247E27135F715D8FC56063217EA4846EF8D6B45E22ACAB60B0B93AE7DB1739CF9F5D8266BF504360F0F74AAB6FD177BEF150ECDEBD4B6E583CDF60FE2D37125F01BFC41D0DDDF3A2C58F1ECA2765C20
	E7826786E517F0EC7E6404FEB9173D2F487F6BC47439C149A44E8DACDC761B1321F303C1CAB3857933B94DED8311F303BF114F03627D1A186F300403FCF7EB06FC7DB74D47566E176977B252AE051F815BA8BE63D2789970BF7035E29664753E0B772FFE5AC766C20BA11D8A1089D0781E41D885507E9E1E935FF636C608F1772A5B6335082322D3F5DDC09E9BE33E73928EECCD765DF538A6CB7A461705640F18F53F3F537B1E3C5E141F950F5F15C31E2CFCE2BFA95FC248DB8A1081105F8B7D90C0CDAFFFC771
	2C7C0EA239EE51AAC95681BB7D4A913F3C61904794B45950E4EB59ABF6BC43B277E2FC5DE9A771C5DE70A59B5172B79DC5ACCB597F3CD7A85B59123BD927ACD33D2C8C093D4A5AD999BB1E77EC20319BF438822EAE4704A837336DDEE2C7AA702C62F6FCBFC8F45B1C9E2BA45FBBE1340F8631CD16DCC65A62F152A63DBE51A67AFC62A0150C741563043E82E89BG58BA37E24C94257E7AAEB35B6579BA5D8BF6B95CE1B68769FA558B34D35C0EE7C689ADF4A960BD76F5A0C63BE98C6FCAA6F25A29C952BF18C5
	1BB56014C2DEABE2798D395BF47215BD651FDE592EC7D30BF5377CBD75BB3245F7BBF331DD0517FB25DC6E744BCA5DB1BD9F1648FCCCF554DE5DC57EBF65219FDC177C55275A5E6CEEBB4FE3BFE1CC2D33F17D07B53F81CA2074FD262BCAFEE6EF3BDB56F9BC90C36749373A3B5FA25FCEE6FDE02BCD60FE699ABA83CF37B8E6D273F7382CC3E287F42E9564B805DC982A282C061F67C5F6C2D3317B000310788F78C57266F9751043363B32D2AE53524B6ABD96537E37B477D832AECF648E4AE5197D3E6247AC3F
	A90F5976CD5BF4FDA90FD343B76707E70794BFBC64B87B017C608B382F549E8A569EC2D760391BA372DC65F17E1ECF8A14FB85C0B6C07195793B88345D3DB1922D5DD5DCD1373BCD6F53B3910B351F1C09B05C1FDFD7F0F8C67B337D241D107B8B7DB0941F7609D7798BAD824E9736CF2E65FCA8FF5F8D426717BCDEADBEC6F3575E97D4324FBB3CDE2728D049E744EDAF2864E853748EA8F66319556B1202433B862D349F145F20124F5A53CA95BABCAF6E2DA55FC4D55E5C3150AF53303C44FD8ABD1E7BA62AE4
	60992D0DFEE3C711F1AE40701565A5A1788E7E4598791E52BA150D72B97D70D1C5BE1E4DEEBFC03FF900E2CE85A40F4DC65CCC3E55BAAE1FFBDFD1492AE75F3ED86BD26FF5B64B7153F7853F0D7110C3ABDF62798FEA664F3E227DCDA04C1F4F733FBB2C153F08675F23E95F921EBF21493F720A7677FBB0BF0367EF534C977AB07F931A5FF1A96943F769EE655FE32CG5CCE012D598A84659F6BA74A6899A16B5B7E3DD0BD3E1BFFE14E5D2351375131310F3551C5FDCA5FD6A81972186EEF72BBB12BB4F52FBE
	21EF4FC70649EFA6271C1741E43DD9B98F0FA8F29238BDBDB7AD27562D48394E6D1923A30771F4FB941E57B94F521B56FD0BD71173B4AF0350FF33F26E3F2A48996376B45E341C9F7A94B90B1CA8478BF2D8FE06DF49FF125B1B282987E5F68748EF568FF8A6EEEECC5CBFF9F3E3628FC695DD17877E2B47C4797D9B9751A13FAF297AFD27736FFFB63E686F2A032DB1FE5927FFB76657FD5AF7E3D675E95F4F7EE3DF743B74CF77695F25FF2ECF7DADFC36C63A13FEAFA98D62AD9B40B4GF3FD007681DC9FFD5C
	3ADB206BA30D35D15B60100801037A5D7D7AE58D74477EFBC3577B497D27FE8F8CEBAEA29F71F1741E87CC95A81261BE56CFFC64F38C59A5C6AA636F68A7ED46A95AA81A5573E048FD6BC1A8DFF0A07A5EB273CFC650844FF820CD84D886108E1085D0BCE8B01683B48F222FF68ED4407302F5B87286BFA059A1AC75170FFC040F352F3993CA7B3AB738D3553E6807284C8E70B87983C89575F5D5267AA307AAE55A4CA029CB1B556997F9DBFDE6D21135F6EC7A325A46485E49C551AF5B7452B5C54E4323531733
	EFECCF743E65C25B796E0ED32D965937BF20FA186F8687766A798EA26DAC48FC2BFF98D6965D56A1DEB7DF3DCE6FE7007FA5DE87DF9E589F258EBAFD9E2F6813469916FE71DE1675488DB2671A8BF296812CG48G90C40331D4C459375A2255AFBBDED76A176D3DEC60FF832F8A648C0AF8CEDF5B9772CEB9BB27BF63F38C1E348EB1C9ECCC689A46B57BFC914F69BFC4E52E00F486C03E8D57457691A23B8BF86D827F8E35CD67BB54F44649F49C23677224AD1559F0AE14414722EE0E7B9D4745F35CC50EDB42
	F1D94AB789B80E1D7D93DC4390623E4A691C9C574C65AD60B88B4725F25C77793B32A5F6BC0B39AD640E248570FD76693471E3B9E46FD4825AB9GC9G4BG52G32G8A9D50AF82B4GD8G868102G71G89G0BG16G2CF4E01CD89D15D162C42EE4A5D0847E6D1510BBBE99A0CF980EEE3F3C7F98D26E92602918965FFDA17A1A0127F3DAFC3704689B841E6034786AC2786201A7E1DAFCFF8B31B389F816CE0BEFAAC4DFAA702C1E965FC3A17B3279A350870CCC076F1990BB2B0027A5869F0B17AF0C690F
	19770F459EB37F116E1109B056F50FFB653B2DC9E3E45D8E6BC63F484766FF9EAB66388EBF6E294B771368FEE6B7585B1A03676033A84E8938BFF3DB1F896A5B7AFCB0362D6B463547770D6311637B16F16D713DF4DCBD3EB35C8ED92E0F3FEC4670FB687EA65BEFB9E7F543BCD439A37CCF57C99D7039949B8E643CEFDDA773A27E6DF13A0C6F82C27710BCEF3B216F075F5D086D07A7EFE87BA16BC6249F1E3B216D07BCA1F21E33F906FFBBF4678C79FAEE1A0F1DD5BCC5473059A361E7984FD37C5C1170B30C
	0F72F1B48172721D58671F54BCE330BB65F38179B9871E8B645F0C6F4F1AF73813E51AE5CE3D7BCAFF5464CB1369B3F579345FA3A9F0B17AB2179E5FF31A7E2D11694D3AFC5AE7AFBDB23DD3176F1FB4754D1269676A72E93FD712A8532768723D2D49E7106953F579FE94B56E32B478D81F7645916C67F2B86E7E916C6788CEFECFABA74AFBDAB9315B37B5C73BFD8F65C436EFE70EF67B7665C476F3E3748FC88F419C220753CDF15CA50E7B9015C9F0979D03F4BE48709F25F691FC3BC894A27EF6EA57984575
	4838CF2AF017E45C1DB9E4DC01A25BBDBE641D1FC3568FB6075349528914466106E1067B63BEEA2343EBD5597DE50E3B446B62970E487AC9F5286B0761DAB46A31EDA412AEC5C35EF7B5645D2DA16F8B9A72BAB4685E54205BB29CC97742F0A45D27B4644D5310374C99C97793E7A4DD11065E2DA1FA593A403A2FD060639B8D9106AF8BEED47098A2B88F237B0C6CF77E08727B76613F7FBE4D7DFFD79D4CCF17DCF8DF3C500D777E3E4D3F99D14AF1FF2573A2BB24E7F35C1BB42666C3FAB1479570BB73ABB84E
	416F3327F15C9A0A2B7140FC510338E1CEE761B8F637FEBE2497F25CCAFEA7BD1963CAE87C94F841E6AF6246797D45F20EFB0A17D762388BEE241B45F17FC2EDB1C0BA0D636A383C8C0EE3F77A1B2F024DD79177ADEEDF8F4759A9AE9152499C77AF1CEE89477DBB77C1098F77CBDFA4CBBE794E4B43AEF314BA4579835EFB9974055EE98A7A426F3CECF6107DD665DECB2B53B164A6BBDE72D974FE872E4563FC3866969D316F424C62FCC93C9C4D79CAB9E6713BD469BE6D3BBB6A3BD961770932A24AD1B69CFA
	F7674A9AC55707382EFC3F36AE753D2DD220A1003A2A7C613AEE8B3B137461350A2EBFA9F73AF4F4055C69829A0BCAD7DF042E7DA3213A6EDB27681A436FFBB9F5F4296FFB4D821AD9AADDF37D61773D1696A832DDEE141D20A3DBFDD7B03CFE12A24A119ED6BF3FB7AA3ABAF8B9966B68D25FA3DC81B4ABD465D895214B93D6BFC59B94DDABF87DE46A68D25FB1AC108CC682282BCC8A57F5A3CCD7ED2122AB1D6B2A1034F565292099E81AD53ABAD53A183CAC9E473D9A727242408BB4F1125276672964C5B913
	A2A1A3176D2463664ED96E1CB6FD78DE5882897B08B1D294B97FC11E1FAAC5BB9B3AFB1342E362BCA501E8BC5FAAD6F83E4B4F3AACD1F952B6ABBC1FF7A3CFDCGED4F5B2A64CFF019A9D1E56EABD1F856F05BB323727C688505E79B67299A0D467307D20527125BB62849936DEC4FCDBE252139EE1A8F32960DCEE74F686BA1FCCB00E7653478760670E5G0FB0B69D3E7F9D3AD788BC9553623BB7643B504D4053B9AD3E9B21FB0540931C965F7A10724583CF423478DE8A61CB821E2553622B8E5DAB841E55B1
	7854F323507976CFB566797F2AC177B38D3AE68D3AD68D3AC7B5568D3F56D8B7FC439DC9779DF7A45DE7B464FDD6C35EAE8D7B76E958D72BC1F7C8032ECDC36F59903D6C7D01443182E8248B7FA02C11041CF6C9D84BAE1A79FD42A6FEB055C1D71CE92375CDF90F5F1B17AB64F788EB598A3CC348A74B688EE18DDDE5F788F930828F522B826AFFA07A0CA27A2AF0DD480DA4D94B7ED818A4ECE4FF2146447EC40DA4942A7FE2CDE4F6CEF78C51D016BDDA5FCE2FA67B19037C187D5D7F5517EC7F61365D39421A
	8EE1030D5C906A22876D962118264C22FF406770129928A4EC3190779CF0DA5DC387587E65D10B3019526181BD91CF8C5AE491362A441DF6B8C53712D5244AFA673CC5920A18E437A89D0074D16A4D9CC9F8293A3A177EC104DEF66A5F2B5C8884EBA22F899271DB3C54A3C5CC420EF089A17786A4E11B66FD82A2E01B17FBB6DF8D7F9FE1BF300FF6FBA14E36C49E6636FE6211DFAC3CE2AE4995F2215958C556CE560C112D81D237B9712059C4EDA857CEC18B30513FA9E572FB45813FA4EC456BFFA61BCF3CEA
	FA2F262A6EB2E07142DF88F693277551FB000061F408D94B7E0C1CA4643A8290FFEF86DCA6650F3281E200DE14A392EB2A8FDE56E9E47F2FD46487424AFF4367067A2EF0562D833AF70517DCE3E3594AEB4A5DB3F676391B3EA7D1F25DE02C390E67173E0957857D3378AE17556904156EC82BFB101C4B1F9C17C4657E492B13C43E9FE964CC16D7BDC96C9ED6717CA7D0CB8788D3141F834EA9GG000FGGD0CB818294G94G88G88GA8FBB0B6D3141F834EA9GG000FGG8CGGGGGGGGGGG
	GGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG88AAGGGG
**end of data**/
}
}

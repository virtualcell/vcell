package cbit.vcell.geometry.gui;
import cbit.vcell.parser.*;
import cbit.vcell.simdata.DisplayAdapterService;
import cbit.vcell.simdata.SourceDataInfo;
import cbit.image.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.event.*;
import java.awt.*;

import org.vcell.expression.ExpressionException;

import cbit.vcell.geometry.*;
import cbit.image.gui.ImagePaneModel;
import cbit.image.gui.ImagePlaneManagerPanel;
/**
 * This type was created in VisualAge.
 */
public class GeometryViewer extends javax.swing.JPanel implements ActionListener, java.beans.PropertyChangeListener {
	private java.awt.Point beginPick = null;
	private java.awt.Point endPick = null;
	private boolean bPickMode = true;
	private GeometrySubVolumePanel ivjGeometrySubVolumePanel = null;
	private javax.swing.JLabel ivjSizeLabel = null;
	private Geometry ivjGeometry = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private javax.swing.JLabel ivjJLabel1 = null;
	private CurveRendererGeometry ivjCurveRendererGeometry1 = null;
	private GeometryFilamentCurveDialog ivjGeometryFilamentCurveDialog1 = null;
	private ImagePlaneManagerPanel ivjImagePlaneManagerPanel1 = null;
	private GeometrySizeDialog ivjGeometrySizeDialog1 = null;
	private GeometrySpec ivjGeometrySpec = null;
	private javax.swing.JButton ivjJButtonChangeDomain = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public GeometryViewer() {
	super();
	initialize();
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJButtonChangeDomain()) 
		connEtoM1(e);
	// user code begin {2}
	// user code end
}

/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * connEtoC1:  (Geometry.this --> GeometryViewer.refreshSize()V)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		this.refreshSize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC4:  (GeometrySpec.origin --> GeometryViewer.refreshSize()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refreshSize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (GeometrySpec.extent --> GeometryViewer.refreshSize()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refreshSize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (GeometryViewer.initialize() --> GeometryViewer.setColorMap()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7() {
	try {
		// user code begin {1}
		// user code end
		this.setColorMap();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  ( (Button1,action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySizeDialog1,init(Lcbit.vcell.geometry.Geometry;)V).normalResult --> GeometryViewer.showSizeDialog()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8() {
	try {
		// user code begin {1}
		// user code end
		this.showSizeDialog();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (Button1.action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySizeDialog.init(Lcbit.vcell.geometry.Geometry;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getGeometry() != null)) {
			getGeometrySizeDialog1().init(getGeometry());
		}
		connEtoC8();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM10:  (GeometryViewer.initialize() --> GeometrySizeDialog2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10() {
	try {
		// user code begin {1}
		// user code end
		setGeometrySizeDialog1(this.createGeometrySizeDialog());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM11:  (Geometry.this --> Button1.enabled)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		getJButtonChangeDomain().setEnabled(this.isGeometryNotNull());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM12:  (Geometry.this --> GeometrySpec.this)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM12(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		if ((getGeometry() != null)) {
			setGeometrySpec(getGeometry().getGeometrySpec());
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
 * connEtoM2:  (Geometry.this --> ImagePlaneManagerPanel1.sourceDataInfo)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		getImagePlaneManagerPanel1().setSourceDataInfo(this.createSourceDataInfo(getGeometry()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (Geometry.this --> CurveRendererGeometry1.geometry)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		if ((getGeometry() != null)) {
			getCurveRendererGeometry1().setGeometry(getGeometry());
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
 * connEtoM4:  (Geometry.sampledImage --> ImagePlaneManagerPanel1.sourceDataInfo)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getImagePlaneManagerPanel1().setSourceDataInfo(this.createSourceDataInfo(getGeometry()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM5:  (GeometryViewer.initialize() --> ImagePlaneManagerPanel1.enableDrawingTools(Z)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5() {
	try {
		// user code begin {1}
		// user code end
		getImagePlaneManagerPanel1().enableDrawingTools(false);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM6:  (Geometry.this --> GeometrySubVolumePanel.geometry)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		if ((getGeometry() != null)) {
			getGeometrySubVolumePanel().setGeometry(getGeometry());
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
 * connEtoM7:  (GeometryViewer.initialize() --> ImagePlaneManagerPanel1.mode)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7() {
	try {
		// user code begin {1}
		// user code end
		getImagePlaneManagerPanel1().setMode(this.meshmode());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM8:  (Geometry.this --> ImagePlaneManagerPanel1.sourceDataInfo)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		getImagePlaneManagerPanel1().setSourceDataInfo(null);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM9:  (Geometry.sampledImage --> ImagePlaneManagerPanel1.sourceDataInfo)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getImagePlaneManagerPanel1().setSourceDataInfo(null);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (CurveRendererGeometry1.this <--> ImagePlaneManagerPanel1.curveRenderer)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getImagePlaneManagerPanel1().setCurveRenderer(getCurveRendererGeometry1());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
private cbit.vcell.geometry.gui.GeometrySizeDialog createGeometrySizeDialog() {
	//cbit.vcell.desktop.controls.ClientDisplayManager.getClientDisplayManager().getMainClientWindow()
	Frame frame = (Frame)cbit.util.BeanUtils.findTypeParentOfComponent(this,Frame.class);
	GeometrySizeDialog gsd = new cbit.vcell.geometry.gui.GeometrySizeDialog(frame,false);
	gsd.setName("GeometrySizeDialog1");
	gsd.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
	gsd.setResizable(false);
	return gsd;
}


/**
 * Comment
 */
private SourceDataInfo createSourceDataInfo(Geometry geometry) throws ImageException, ExpressionException, GeometryException {
	if (geometry == null){
		return null;
	}else{
		GeometrySpec geometrySpec = geometry.getGeometrySpec();
		cbit.image.VCImage sampledImage = null;
		try {
			sampledImage = geometrySpec.getSampledImage();
			return new SourceDataInfo(SourceDataInfo.INDEX_TYPE, 
									sampledImage.getPixels(),
									geometrySpec.getExtent(),
									geometrySpec.getOrigin(),
									null,
									0,
									sampledImage.getNumX(),
									1,
									sampledImage.getNumY(),
									sampledImage.getNumX(),
									sampledImage.getNumZ(),
									sampledImage.getNumX() * sampledImage.getNumY());
			
		}catch (GeometryException exc){
			javax.swing.JOptionPane.showMessageDialog(this, "Error: sampling geometry for display " + exc.getMessage(), "Failed to display geometry!", javax.swing.JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Comment
 */
private void geometry_Changed() {
	if (getGeometry() != null) {
		try {
			GeometrySpec geometrySpec = getGeometry().getGeometrySpec();
			cbit.image.VCImage sampledImage = geometrySpec.getSampledImage();
			SourceDataInfo sdi = 
				new SourceDataInfo(
					SourceDataInfo.INDEX_TYPE, 
					sampledImage.getPixels(), 
					geometrySpec.getExtent(), 
					geometrySpec.getOrigin(), 
					null, 
					0, 
					sampledImage.getNumX(), 
					1, 
					sampledImage.getNumY(), 
					sampledImage.getNumX(), 
					sampledImage.getNumZ(), 
					sampledImage.getNumX() * sampledImage.getNumY()); 
			// 
			//double wd_x = sdi.getExtent().getX() / getImagePlaneManagerPanel1().gets.getScaledLength(sdi.getXSize());
			//double wd_y = sdi.getExtent().getY() / getimagePaneModel().getScaledLength(sdi.getYSize());
			//double wd_z = sdi.getExtent().getZ() / getimagePaneModel().getScaledLength(sdi.getZSize());
			//getCurveRendererGeometry1().setWorldDelta(new cbit.vcell.geometry.Coordinate(wd_x, wd_y, wd_z));
			//cbit.util.Origin o = sdi.getOrigin();
			//getCurveRenderer().setWorldOrigin(new cbit.vcell.geometry.Coordinate(o.getX(), o.getY(), o.getZ()));
			getImagePlaneManagerPanel1().setSourceDataInfo(sdi);
			return;
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	getCurveRendererGeometry1().setWorldDelta(null);
	getCurveRendererGeometry1().setWorldOrigin(null);
	getImagePlaneManagerPanel1().setSourceDataInfo(null);
}


/**
 * Comment
 */
private void geometry_This() {
	getCurveRendererGeometry1().setGeometry(getGeometry());
	geometry_Changed();
}


/**
 * Return the CurveRendererGeometry1 property value.
 * @return cbit.vcell.geometry.CurveRendererGeometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private CurveRendererGeometry getCurveRendererGeometry1() {
	if (ivjCurveRendererGeometry1 == null) {
		try {
			ivjCurveRendererGeometry1 = new cbit.vcell.geometry.CurveRendererGeometry();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCurveRendererGeometry1;
}

/**
 * Gets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @return The geometry property value.
 * @see #setGeometry
 */
public cbit.vcell.geometry.Geometry getGeometry() {
	// user code begin {1}
	// user code end
	return ivjGeometry;
}

/**
 * Return the GeometryFilamentCurveDialog1 property value.
 * @return cbit.vcell.geometry.GeometryFilamentCurveDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public GeometryFilamentCurveDialog getGeometryFilamentCurveDialog1() {
	if (ivjGeometryFilamentCurveDialog1 == null) {
		try {
			ivjGeometryFilamentCurveDialog1 = new cbit.vcell.geometry.gui.GeometryFilamentCurveDialog();
			ivjGeometryFilamentCurveDialog1.setName("GeometryFilamentCurveDialog1");
			ivjGeometryFilamentCurveDialog1.setLocation(618, 437);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGeometryFilamentCurveDialog1;
}

/**
 * Return the GeometrySizeDialog2 property value.
 * @return cbit.vcell.geometry.gui.GeometrySizeDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GeometrySizeDialog getGeometrySizeDialog1() {
	// user code begin {1}
	// user code end
	return ivjGeometrySizeDialog1;
}

/**
 * Return the GeometrySpec property value.
 * @return cbit.vcell.geometry.GeometrySpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.GeometrySpec getGeometrySpec() {
	// user code begin {1}
	// user code end
	return ivjGeometrySpec;
}


/**
 * Return the GeometrySubVolumePanel property value.
 * @return cbit.vcell.geometry.GeometrySubVolumePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GeometrySubVolumePanel getGeometrySubVolumePanel() {
	if (ivjGeometrySubVolumePanel == null) {
		try {
			cbit.gui.BevelBorderBean ivjLocalBorder;
			ivjLocalBorder = new cbit.gui.BevelBorderBean();
			ivjLocalBorder.setColor(new java.awt.Color(160,160,255));
			ivjGeometrySubVolumePanel = new cbit.vcell.geometry.gui.GeometrySubVolumePanel();
			ivjGeometrySubVolumePanel.setName("GeometrySubVolumePanel");
			ivjGeometrySubVolumePanel.setBorder(ivjLocalBorder);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGeometrySubVolumePanel;
}

/**
 * Return the ImagePlaneManagerPanel1 property value.
 * @return cbit.image.ImagePlaneManagerPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.image.gui.ImagePlaneManagerPanel getImagePlaneManagerPanel1() {
	if (ivjImagePlaneManagerPanel1 == null) {
		try {
			ivjImagePlaneManagerPanel1 = new cbit.image.gui.ImagePlaneManagerPanel();
			ivjImagePlaneManagerPanel1.setName("ImagePlaneManagerPanel1");
			ivjImagePlaneManagerPanel1.setDisplayAdapterServicePanelVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImagePlaneManagerPanel1;
}

/**
 * Return the Button1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonChangeDomain() {
	if (ivjJButtonChangeDomain == null) {
		try {
			cbit.gui.BevelBorderBean ivjLocalBorder1;
			ivjLocalBorder1 = new cbit.gui.BevelBorderBean();
			ivjLocalBorder1.setColor(new java.awt.Color(160,160,255));
			ivjJButtonChangeDomain = new javax.swing.JButton();
			ivjJButtonChangeDomain.setName("JButtonChangeDomain");
			ivjJButtonChangeDomain.setBorder(ivjLocalBorder1);
			ivjJButtonChangeDomain.setText("Change domain");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonChangeDomain;
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
			ivjJLabel1.setFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjJLabel1.setText("Domain:");
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
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Return the SizeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSizeLabel() {
	if (ivjSizeLabel == null) {
		try {
			ivjSizeLabel = new javax.swing.JLabel();
			ivjSizeLabel.setName("SizeLabel");
			ivjSizeLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjSizeLabel.setText(" ");
			ivjSizeLabel.setForeground(java.awt.Color.black);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSizeLabel;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in GeometryViewer");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButtonChangeDomain().addActionListener(this);
	connPtoP1SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ImageView3d");
		setLayout(new java.awt.GridBagLayout());
		setSize(506, 564);

		java.awt.GridBagConstraints constraintsGeometrySubVolumePanel = new java.awt.GridBagConstraints();
		constraintsGeometrySubVolumePanel.gridx = 0; constraintsGeometrySubVolumePanel.gridy = 1;
		constraintsGeometrySubVolumePanel.gridwidth = 4;
		constraintsGeometrySubVolumePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsGeometrySubVolumePanel.insets = new java.awt.Insets(5, 5, 0, 5);
		add(getGeometrySubVolumePanel(), constraintsGeometrySubVolumePanel);

		java.awt.GridBagConstraints constraintsSizeLabel = new java.awt.GridBagConstraints();
		constraintsSizeLabel.gridx = 1; constraintsSizeLabel.gridy = 0;
		constraintsSizeLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsSizeLabel.weightx = 1.0;
		constraintsSizeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSizeLabel(), constraintsSizeLabel);

		java.awt.GridBagConstraints constraintsJButtonChangeDomain = new java.awt.GridBagConstraints();
		constraintsJButtonChangeDomain.gridx = 3; constraintsJButtonChangeDomain.gridy = 0;
		constraintsJButtonChangeDomain.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJButtonChangeDomain.insets = new java.awt.Insets(5, 5, 0, 5);
		add(getJButtonChangeDomain(), constraintsJButtonChangeDomain);

		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
		constraintsJLabel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel1.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getJLabel1(), constraintsJLabel1);

		java.awt.GridBagConstraints constraintsImagePlaneManagerPanel1 = new java.awt.GridBagConstraints();
		constraintsImagePlaneManagerPanel1.gridx = 0; constraintsImagePlaneManagerPanel1.gridy = 2;
		constraintsImagePlaneManagerPanel1.gridwidth = 4;
		constraintsImagePlaneManagerPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsImagePlaneManagerPanel1.weightx = 1.0;
		constraintsImagePlaneManagerPanel1.weighty = 1.0;
		constraintsImagePlaneManagerPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getImagePlaneManagerPanel1(), constraintsImagePlaneManagerPanel1);
		initConnections();
		connEtoC7();
		connEtoM5();
		connEtoM7();
		connEtoM10();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
private boolean isGeometryNotNull() {
	return getGeometry() != null;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		GeometryViewer aGeometryViewer;
		aGeometryViewer = new GeometryViewer();
		frame.setContentPane(aGeometryViewer);
		frame.setSize(aGeometryViewer.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
private int meshmode() {
	return ImagePaneModel.MESH_MODE;
}


/**
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	// user code begin {1}
	// user code end
	if (evt.getSource() == getGeometrySpec() && (evt.getPropertyName().equals("origin"))) 
		connEtoC4(evt);
	if (evt.getSource() == getGeometrySpec() && (evt.getPropertyName().equals("extent"))) 
		connEtoC5(evt);
	if (evt.getSource() == getGeometrySpec() && (evt.getPropertyName().equals("sampledImage"))) 
		connEtoM9(evt);
	if (evt.getSource() == getGeometrySpec() && (evt.getPropertyName().equals("sampledImage"))) 
		connEtoM4(evt);
	// user code begin {2}
	// user code end
}

/**
 * This method was created in VisualAge.
 */
private void refreshSize() {
	if (getGeometry()==null){
		getSizeLabel().setText("no geometry defined");
		return;
	}
	
	int dim = getGeometry().getDimension();
	
	double sizeX = getGeometry().getExtent().getX();
	double sizeY = getGeometry().getExtent().getY();
	double sizeZ = getGeometry().getExtent().getZ();
	double originX = getGeometry().getOrigin().getX();
	double originY = getGeometry().getOrigin().getY();
	double originZ = getGeometry().getOrigin().getZ();

	if (dim==0){
		getSizeLabel().setText("compartmental model");
	}else{
		getSizeLabel().setText(dim+" dimensional,"+
							"  size=("+sizeX+(dim > 1?","+sizeY:"")+(dim > 2?","+sizeZ:"")+")"+
							//","+sizeY+","+sizeZ+")"+
							"  origin=("+originX+","+originY+","+originZ+")");
	}
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * Comment
 */
private void setColorMap() {
	DisplayAdapterService displayAdapterService = getImagePlaneManagerPanel1().getDisplayAdapterServicePanel().getDisplayAdapterService();
	displayAdapterService.addColorModelForIndexes(DisplayAdapterService.createContrastColorModel(),"Contrast");
	displayAdapterService.setActiveColorModelID("Contrast");
}


/**
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setGeometry(cbit.vcell.geometry.Geometry newValue) {
	if (ivjGeometry != newValue) {
		try {
			cbit.vcell.geometry.Geometry oldValue = getGeometry();
			ivjGeometry = newValue;
			connEtoC1(ivjGeometry);
			connEtoM6(ivjGeometry);
			connEtoM8(ivjGeometry);
			connEtoM2(ivjGeometry);
			connEtoM3(ivjGeometry);
			connEtoM11(ivjGeometry);
			connEtoM12(ivjGeometry);
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
 * Set the GeometrySizeDialog2 to a new value.
 * @param newValue cbit.vcell.geometry.gui.GeometrySizeDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setGeometrySizeDialog1(GeometrySizeDialog newValue) {
	if (ivjGeometrySizeDialog1 != newValue) {
		try {
			ivjGeometrySizeDialog1 = newValue;
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
 * Set the GeometrySpec to a new value.
 * @param newValue cbit.vcell.geometry.GeometrySpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setGeometrySpec(cbit.vcell.geometry.GeometrySpec newValue) {
	if (ivjGeometrySpec != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjGeometrySpec != null) {
				ivjGeometrySpec.removePropertyChangeListener(this);
			}
			ivjGeometrySpec = newValue;

			/* Listen for events from the new object */
			if (ivjGeometrySpec != null) {
				ivjGeometrySpec.addPropertyChangeListener(this);
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
 * Comment
 */
private void showSizeDialog() {
	cbit.util.BeanUtils.centerOnComponent(getGeometrySizeDialog1(), this);
	//getGeometrySizeDialog1().show();
	cbit.gui.ZEnforcer.showModalDialogOnTop(getGeometrySizeDialog1(),this);
}


/**
 * Comment
 */
public void viewSurface() {
	//getSurfaceViewer1().setGeometry(getGeometry());
	if (getGeometry()==null || getGeometry().getDimension()<1){
		return;
	}
	
	SurfaceEditor surfaceEditor = new SurfaceEditor();
	surfaceEditor.setGeometrySurfaceDescription(getGeometry().getGeometrySurfaceDescription());
	BorderLayout borderLayout = new BorderLayout();
	javax.swing.JPanel panel = new javax.swing.JPanel(borderLayout);
	panel.add(surfaceEditor,BorderLayout.CENTER);
	panel.setMinimumSize(new Dimension(900,900));
	panel.setPreferredSize(new Dimension(900,900));
	javax.swing.JOptionPane.showMessageDialog(this,panel,"Surface Viewer",javax.swing.JOptionPane.INFORMATION_MESSAGE);
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G490171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DBC8DD8D4E53AEEC1E921D2EAE1D1116926C506C5461A3BD1E92939FB5D2E355EDDF7576E3A1B3554FAB75AF5DBDA8FFF0A02C9C9E51BA6AAAAA8A27F923A4A1F88C6C502A3A2A98766B0CC8EB35319B38A8AF55F6F67BDF3E6668CC35C47675E677278FA4EF93F777FFB3F5F731D91B23605A547A7E488C2FC32407F8E468BC237D1907EF4DF5F9D088B369AD2854D5FF600F704092713609A86E5194F
	99D253058B07D260B9921EF3979AD2BF007BCD027D1065AB38712127864A4614CE0D9ADC4F6AAFD14FDA7ABC7DD89ADC5F8608829C39A659087E270FED6678259C0F901FGB8CA736231AD9CD7874F6DGA1G61B6C6BF872E6B28664072B2CE57F653D4A1F6011C3001485148C44832B35EBAFE4D945E0CDFE9A73A565726F11E91709C85C01CCF940A65DC389EEE3CE0BA5259E1D672AEF6CA96CB1EC93275C80A5C17E7F21A4B4B0F73A703D93AA44916EF083C1335A914DF900A61F9954589BB080E88381F87
	30CC451F4AA6784332A1359940A223DF7F5AF3185B5DF265D1E139E96E3A0DBE7C8A1019BF61324B2F003D9D099F52BAB85AC4E23D824A529CD07FGB5GC600G000FE85D571E5C8B574A8E255A5653E333D65A6DD596237C194DA8D9603E3C9CCAB86E2454E913A167D87D2657666A58B398B03E0F3FB79F0B4954A22C6B70135305258F5F1230D6A7A6CB27DEC988F530D8C4BADCB1E13ACF7B543D58611E0B6E3ACF8BDA5D424B05EB13F5F4EFF921F06D8AAEA7CE233BC2A1F55DCBF3E08B5C93BE12278CFF
	BD4587E870ACAF57D0BCF91E8AE599CE4C0D163751160624C9C2FA412D3AF188F9B2A8E10413599062743465459CA2FB0E1369BC245A32F6AF4157DCF46119ADE7E91E1267B9D09656EBC8E579D5F70A34537E4B50BE81A2G66812CGC83AE2C85DFC85F3EC6C4983C34C31C6D1D62A1CE60BD1128546DB5774BEDC25DEC5966B2D8EC534F6CA86D1B60B9D9649A1F01AAF1BC88CBA5C788CB477BBC147F149AA4922E236D9C13A888DD6129D0C66FC53BB04C616E8E93339C78285BDF681651A1AB64155A2BA14
	D3F62328C8AE8AD6EEEBA2F5F2D1B400C490G77ACAECE8309D799785F88502E466195AA6FD3C936CA167272861B6D02535ECCBC91A22E10B8DBE95F2140FD50950C71B72789AE961E537AD14E7666F7B5F2EA4C1D44FCD16E43B6766D8E62FBA6501781B4G5881C2G227AB10FDEEB41BC2A597000107B7C845DBC8A061CDE544F72E7C53FE7FB9ACD7590DB53878C2979G0DGF60060819C8752F634EB6421BD3E5AFA26268CED9E2CEF086265CC579EB7DD499C6F0BB751231D278EC27BB6EF0BEF2BF57C5B
	965A27031F8B81E28630EE5A1AB3D92ED7098E89337B8B9992D3B23AEA0A597BD20E82571D5C96523EBE60723FG58CF64G6C83B885F0146765D600EDGC5829BFB4DG4781CAG8E826C8258831083C05ADD8140D900FA12B3G1F28715A301768CF7A966682G4DG7DG138196824482EC798E468CG8B40F80059G4B8152C698D20B815AG82G428196834481E47DG6A98C08188851883908310F10DA175B000719A4C8DB1FBD13CF7DD8C86989FED9C367398EA7D5E4763077E97E96273890F755BBC26
	159A7947B5F1FA1B5783161D525C1F9D44C67390FD797F8A2CEE1728F51BBCC4BEEC035A7AA5F51B68D11F248F5D282957ED1AFABFA4385A7C87548E177CDD1A7A5736E7826FE96ECB8631F1677F01787EEF826B63CE1EA27117AECAD625C632CBD6A35CB8F85919D3249E79F85DE5D3F94ED33C9D4677EA98D09DAADEBAC56AC8D494595C61D424A6C9D14CD6132B3C076AF2B8BB6C2E29816AD2CE115C32DD0271DD256FBBC54646CE1B55AA514ED6E56165D76858ACD9249EB03A5A6634AA8C7F8383D93FDC10
	7A785CA05030C6A0E33B45C96699ECCD90A1127C344B669E684249D4C5D8FA8D993783EBB7EBE29484B8323609006B1CEBF03C3D3493475B2C4B5304687376843DB9E448143189D95732B12D6CDA57F84BEC3AF8DA5F76FEBAC760F8873375C9AB31353EC7B4C9E4F9D2E4246DE813503B0650624A056196B8489C2B5AA9DF14CE125A14A599898A844E5738F30B062F56EC912991042746ACDAEC2682C6774CF71BB4F4CD664BD84C65FCFCA8CDDBEE17BA59F83A391560DB40E78BC04035B81E2EBACD4AF12E43
	8752EA1B45422A974A16DFCB6A60799D99BC0621709CF9AD2B8372EC89A0651F6E4C5044B9B740337C12DBF9A4AF0F03AB13EF5E0365690106546CC097BFAB7F2DDAEE0432FECDB9736377AD69A40F25EF1C92CCE149246FD3B557E2DA5E75A8B3703CE36531AD24AEAD1A79E2ECA051593A9757499101EC8EB60F6BDD9248F447DCC7685E5D07F495705C8250FB9D1B27C5DD47686C57933A97D43A326B591C2071FAA6379D2EF92A3C4939C8B77BFA175E45F03F6AFA2DBC5BFE24CB9B496696D9A3DD74C4DE98
	F996F66629FAC772F9A4DC5B8182GBE86BC73B7D6250B99497CCC9A452EDBC6B13D0CEE7A81D55EA86E87276BF623EB4CC73A1123581CB5EC14FB7C985D3A82240B64F48BC7694579C915EE59A866672AD14C0F149B40F7GC677D621EA5F8D0C2E7186C657FE8313371260051F95A1DD7F8DEC6E15457D9EFF03B64E76E224BBFCA31B1FB55F68126B2A37729224B3EA4AC93C7BEF544ACBAEC53A609B59BC8F6B4DDD5E0FD43A29B73238C45F6872F7490D686F1F8E2A7546691202985D16A02D3FF5C7543884
	31B9253E3F2F28F40DC1EC5E599D446C6B8D525A97F5D42D5FA0A6A70C6B0DA24FC1E8DFEC85522D64F4A92399DD66E82DFDF3AA11AEFFB41B6322FDF52335F950D505F46D23D9F96FE8565ED60D524AAB2CD663BC1A4D15D15ED4B7F90BD53A38514C4F0CB12C9DE70D514A3B27C60D5F98B667C6F90DE334720E297BEB76B1CC5E48B12CDEC60E5146F9E3AD52050CE1F3F74C0308B1AC4EEC2CF9BD070CFFF42F6CEB7122581B673884E3F8F97909C691FAED36AF76A73A36A5747346607E57C7A7C81FAF0CB5
	24EE86489F0BE3654FB7607E57788BD30525AF972C75352FD7B7167761E3DDE3E5704A4AB5ECCC69B8CBF406C3596C31B81E584EE85760158E072D53CC177F3C2F7D38D5BB16B25C4E5675F06DB24B8E259E0647DE6C17F3DA093DE4FF002379FEC99C5D3BA1635DA2501B920CFB0A77A56D90FC6FA93A7693F15F2933886390FCF90A50BE613604D49FF12888E67E37873B62107B6D2AE426774E2222C174323DCC5D511F5BB1D02B2D0F599B87FC5A914275C7E96C2830B52FE5F6FC177309DFBB8619E110D853
	3CD1F6639E6BAA5043E29E3C8777F173EF027E60A60C3971337CA16BF44DD6789EDCB64A8C83F9CBD419B7176C9ED2BD12598D664435F4EF0D70275F8C7D85G1B8F3D32174CBB15EEB31F37FD1BC062A6CBDD326468A686E56798B09FA6ED2A60F1DEB8FD12F0783BF389FA7B42A3B7F6A70C381945FF6A4D2EFA883F39A3B19A0059F3E89F5A938BB43963E05EC567C197CA3573A063B8369795B48E6D1D342F902EA21DFC6531CB23A4F759649E4918CDDAFF1EF8C94923EB033CCADAFC0C5C67E0DFB1F7002C
	434CD633125D20D7A1983BC344ED6E77839FEB7DCE9183F57D8EFA591490B60E79BBEF1C4B6F0CF19D095963506F557B30ED260F07FA980FFEF56512FA90E51C470E4945FAE1B6741FC19BC6AC0C94B6EF9A2DEB83AD9F4FE3BF5EE54332300E4458B034612739E843AA20491FGFDB4DDC7ACB921DDC7F4CFE0311F8A575993504E167DA477B1DCCCDE48FE1437986852EE41BC4DB8C22EF48D2376CD8DF9C8DB83F47D37205C077350DFE6CB67FE2DAD1137B0DBD640B5CE6579D59E6917BB35BD07DAE7FF2E54
	56D9545AA05DF8F55F9B1210F1AB0B5361DBDD718A393BA3B17CEE0C575683182BC1C0B367D6549FC071A41720515226427CBE54C764B8A4D8A3D9EC72E7221D36A1A66B972A2CB810D39C0231BA72A541DBA10FC54BC9496134A88CBF6BB4D91FBA3AED17DC3D83954765756523BC8B481A9C0236AD4BA76BC71BECB6192DC8DBD80034CB01AEE3A252CEACA03430E606060234FFD4E99B00AEC025BDD90875441EAF97D2DFEDCE39D32A91953E68E67D641D0558CF4E1E087D646EA22C7FD840655E06F2C79739
	4BFD24C8CFAE137157A2344D887C2137E18CBFE9A77BE5F4BFDDEAF263745756D931192CD6735CB98F71FC7A7BD64C272C1A49C2D96C851F6DEF71ED7C9D4CED1A3EEF435F122381187D930BB1868D37C3ECEF479888A55887B03A4E92241B8DB431AA5DEBA55EEDB02F94E9F3C38D2946D00C77FF16E22CC28137B894E5549FA4F80758E337C8C63ACCE7F596FC906BAC6D8E143139CCED33006B3F83E5FCD766A903618F9C71DDF75F9CC1FBA2C1CE0C2AE353D15411F527A1356DCE54F17928FB3FB869A86689
	63FB71A84A9B8FBC8BD53E3A8A7D36F95FAE7ABE47CA1631B532C8A60A4DB61B45115DCA9A950B41C08546A069AE34EFEBA55AF798F04ADDEA9FD06929076127D792BBFBECC609713FDA09F606836F723BB0E7238F1139CC8F8C3604B6BB271E47E9EC956A5B92866B29B0547717AACFFD7C9DD9355AC7816D1CB0147F376ADA35CDF84FB332F3F49B864CCA723C098FE19D3ED50D3645011E623B51B6E30DFB9B8E2AD92B465A48F99756287D9570CD3E9B6D8C2BA5633E5901EABF37A91FBBAD166C1CD646E7BD
	06BA1782CF46A454797CB177FCA82BD55B96508428F4B3A85E24199931FD62D82FF73233A7395E79AC525C2BF3DD7A2EF2ACDDBBAC1F445ED5262A78BBA8BE699E971E59351D6249F396140D3C875F2F7D7C9C9967E260B9E5B22C77G4AGDAGFAA7631AE1C6AB19BF8F7A7E325E2A4012C234B439EDD462FB4433A4869724BE7EFC612CF6DCE6F1C858C962B0FE72606F3B34405F43D33E706F41477CAEEDA7753D9478D68124CFB124E68354CDC11FD71E4D7567B33E07654EB23921A2115B8DF282GC281A2
	81962BF24B5B7C4672D82FEEAC193FFD3B87BBBF90BB45BB8E0C6F16BD43BB2FB0F918FCE95943637B67B079C66D7564CB7921FBAC32BC1E995F2DDEFCF99E4FF59EFC2C1E7F5CC176865A212C9FE0BCC08440E20015G9977E2FD0FEA7B175F7AA68B04E6DBB5606AEC368B551EFB772A2D157B06676397DEFC1EBE965F6B4E477A440DCD44D71DFD0DAA2722503DE7B6DECDAACD613A9A6E45FE45ECA471890067B000A80045G2BGB226C29FB39563734B36C3C30C8FDBCDF9C70779181C6BAFEF74E33349AF
	DF4BD43D7AFFD9A27E5921AC88E0B2C0B440F2GE11AA1B5EB9A7A177BD515BF7F54E39960A33E8752C982E66F3F8F534F977D729D1E2617833F2AAA96063CFF606CB058AC4E9EC9336F157EA96EFBB5CF43FD2F9B4E1131A8881EA7834C1E066F436F5CBFBC5F427D72AD1E2647F7B2EFF87A5A7D72456846F209E30FDBAC69FC3F3C1C4EDC9BAD90334FC4AB5C4AB4FC3CED5D3E9F6317F49F466F336D745C9CBCB783D86E43B6F74EC45B9CBC0781C481ACA6BCG9977C31B3B9FF3F2047F1CD493F1A8B93973
	40707A2582AFBE4FFE2971FEBD3EDF8FD35F9F7D6A6B565577DD7E70740D76627354B7C2D7DFD941707455FA71F96A8B515577DF4354375A2F3E88DDFD77948EE50C1F2761E5FC39C543EB2F65FE79166A5A7916975F506212DA6CAFAE31776B0DA9CB3BC97BCD8B0739A8C085C09B408800D00068F0EC3F0B3FA2E7F606BA26781FD75C65E56F50627A00DF3EE561FAFC661261693B6417EF152E3E625261692B724B177E009E5F8AAF3E21654DDF3C78BC73A6DBD75F0403C3E9CF958FF836270F4B069717DC3F
	FC4D3AF6DEBAB23C3884FA71F94645222B6F43234373EF0FDF3EGDDFDAFF9718D4D3FB73C78BC7D8B5555F7CD85610B74A89B8C78F8DDB93C389478651B27EB676BDEFCC30BCBFC153F38AC53EF8FDEFCC3736F9E3FFC2BF475095543734F66456769DF7AF4BD3E441A6179770EDF3EECDDFD4FF9718D4D3FE5DEFC1E7ED5686AD30E8D4F3FEB7C7235686ABB62771B8CFDFD8DFE796C9E7A58B9775463E4CEBF924AC28198EEC59D41C543731269389E1AF0026CA54541F36603B0FF87E88130BC08657750F2F2
	6EA878C1F69EA262C1F6EE0377E3A7DA60CA78887FE0F63A4588E863385D16C178180E8A2A035C8F751CAB8172CE834FBC8FF66E1C6F911C73FD5E7245F37A67ADDFBA27FF5E7255F36FB0BC4850FEED0165EF1C9B7CBC66DB67744EE3EE257E3C10C056E532532A18FBA42A162D7B24DEF20E97E6E5A71DD6B6E5A31A9D1E7B082A0EA36D3E7DBD562E6F6F276D7A7EFE59BE383FE75A8777F732685B167BC5FDDBE6087A36BCAA8EEE4B6322AFDBD89B895E0DEDE49164A50B797C77B7D2198E0764209BC56405
	8F2F0F1794F22E596BAC6C67B0B39670DCE7745EF51C6618E22BAED0ED096B706DF7E2273E5F6BBA497BE0765E495337744E4DEA19EBA6DE2FC8BD449CC673A165D7C459A4F945A6137233B2DF7CB91D3E62474F3E6E53787AD9015A066FF51BF74672B61F92412EDBA23C7BG264FE1749D234BC67D980DB07A0E5175C67FB18AB67A0E512DC67FB18AB38E9E23433929BCC60DDD322D2731004BFEDD726D6B1B123E2FC217EFDF57F67977B53D4B372F9FF47977B5330B7852E1B61AE5FEDA187938FE3FB68FCA
	55BCF86FD3ED9E64FB54FB0DCE9E30F7CE7F207BF9AD916C1DD33C0ADFFB1E600D9ABCBF2BF48E4F955921ECF1047AAEED3FFB6CCB393D2372505EC64556684A5B30C1777D4AE698D29BE6385B4C6CFA026E1F354F7034F78145DBE6F85A5B349D6D6D053225B3F09D5EED721D93B613FECEB8CD3EF3225F64BFA782CD3EF3A24864BFA7A61874F3227500369FBC2836070CFCAD3ECC454FF443972BF8DB0196DF2262338B35B9C76430BEFD96BD23C14ED5D6BC04F950D92425ADD4E594973B65C7B5364DBFF47B
	2E07D73B756B6175EE5F7570E6377FFAC8B47B2E0775E67F753049BCF8BF74023B2F0D059C9FD6220DCD11CAFF21541D3E0863379D5452FF2246BD7CA0463DD10D7B29B2AD2D2BCE279FF10FBB5A52F854DDE7B1FE63D02115D3226A1CC84FB53403BE3B2A3335D2CB6B4A3B3DD56EBA511F05D56BB5748579C89FD26DCE0F36145738512B5FF32CBB66CEDF4271779FF323AFC4FAE72D3B2FC83F63B8B93BE4917BECCEF2F669A16267A747716CD8GBC07824C81D87290BBC3466604B3AE91F9DD32588355CE8F
	CAAEFC181E4F2BC35EB2F8EEG30G848104BF0CB11B5DC64E10683FF5C1FB7F59C562CA8ECFB4089D844D71DF1CA367FF799B3C6AEE51EA12EAECBD22997B0109DF123E7584E542EFD6BEFB07FCCB267F1A2200FFBF722F735AF31399114C5732C8BCD739ABC72F3C812E4C6FFBF7235F6D112C4FD678F5FCA42E11FEC25FB3C64273BC957786BD0B94834FF1AA2E35946709A90FA0AE173E07EA01E70B0A5BFF9969029F41B5D352F9386077211A7BDB4E35883E471A7FA9B09D8FDED59D4C67C777234F4B54B8
	3C12C9F079B3E16DB89363704CF6176F0BB477E736DF7DB848D7D5C740CC62734F59B9C238E74FCF9DA44F73E6E29B0B2840F3DFB1347CE95AF711B9401219F8A6CBBC2E1E39FE54105A7DA8366D02F6320F2F3EB7A7A7024B4B0F4BE6E315E8EA20BD85FB37B76DB84A8CFB945F6D7D024ADC824FAB81D222701B005E5DF886BD7C071304C53F577FA6E0D973C0C2E694EBB7B5D11A7324FBBB92194EDD7528F38414C7C55179CD3D36ED927F4DC9EC2F0615873A1513F16BA6C1DB7E28DBB931770DC4ED794BEE
	65A4C6C7B9BF736D59DD68DB5C3391026511G1F3FE3A27C087914452F19BF1B989F93523806FF07516563BB8C5A1B71337367D3385EB61041F0EDB9F8FEE13C0A7B69EEFC6F3A70C7982764BD1EF13860666741BD1EFEBE4F6348673725E42C683049F43240460A18D26C7712E699D237G5481344DE27BB90CAF2D1466AACCE214D96437E78249CFB488B13368F946529A4E9FB5CB7F6CCFBC57CFE0A15090F80F5B37EC16EB2E4BF4D5FF6AEDE36B27E8E371E34C46863836BF463EF9E3F654DCC6BBFA9F7336
	8365633ECF905090F80FBF07BD663203497B59FE14976EC3DE32C6DEB431C5A3EF45E31EFE45EFA71D18516C3043D8DDE9946D0AA4B7C972C5F3A79BB78DE607196D4130763EEDBB36135847309EDE22FD0655E931705F2911343F3F13FF25A1A151C75E264CE6BEEC665720559363194C7FB808B259732FAB08DFDD2245A1296DA36C12D6CF597AEBF43F1DA5D0476537E97430BE55F4FCF8672A1C43641B7E3D79589AECDA9D697F464F76DC1BBDC471E133BD57E63FD67B60F0A8CB7FB11E057873B7F48E8E4F
	41GD38122811681447D9857EFB34D645B18C15F23B65B6C8D303030E8362B18FFFB6B3F2FFF2CAE1FD80BFDDD642C7B04286BEAFCFECB147E93D607797C9A35F0E2BC39B27F829D443FF6A86B8788830882D888307CA7685FBF4C7C8C63B123191C6890BB3B69D7DD6A4623D93A64C23B4E24761D577A360A6BCFFB1C7739F0ADFEDC77BDC80E162F0E5334712B7DF17D7E699D61B937B30D81C09722C32B85D6779987712CEDB850AFFF9C475FE77EC93E19F51D332107142AC8B5D651FE2DCA922DF8F62DCDBD
	EF3EA59A4760AAFA6E3D991EADG8151B8867F5BD9EDBBCC3F4D12003968E9DFE8B47F86A65AB586EB4F4A51EFA020EC9B2DA7689F84579F6B6327B985B5E7DECC431C59F27FBDC24DAFEA92F67A501B718453E7FC422537252BF89DBF1B5C00718A057245CF60D8B722C1BDF37FA44660F29F212D0167B6G65C924F5DEC67B031E9C4C7ECD1748BD63D92D1EE11A234B3372289E0E4929A61F9572BE3CF43B36EFE46DE0E5AF69BF631E441C1FE7A75F989A442FF9EBA629EF13E94683121C05E802A53755047C
	4B7A084E74278C2939CF210C8D5F6C9BC4C623EC33CB3252473074CBF51C3F5789EACEC7E96EFFF77C6A4F19632E2A8E96173FFF63FB1FA5C1565FE7C911755F853CAD8F7E5E61FD59776FDEEC15353FFB416A719F17495C2B816A309BA088A08CA0D22D537BE512976CE41BE7BFF84CE8A27BBC6457359454BF46615B57898E7F3F49F527036CA3F458E0195DC3642B6FD39C243E941B1DA299EE9A7D46CEB61B3A95C64A718FBAC8EEDA24AE0A666FC0687CF59178356CA9DC3FA550EFF6E361B9E98E62CC2D38
	4ECBD7F111E5F47F851EFBD5DCAF1D7B8641F3280ABBD20DEB6A8895F7D7B54A1B2D62DE26F36434390654D309383EFC0D6232G77E53CFA265B6F79140A39FA63C936DF3E0FF55B02284B57B6577718426698B3E93FEF1F6BB977F81C627B67FA4EBD6ACAF06EB1924AE266625C6323FEC12D43238A795ED337AF014CBB814B14AFBA3E16BA15E651A4683F0FE9FC5A10EAFCFA707110692D9DA0F366DE209D8F90853088E0B9C09CC07AB30654AC00B200C600EE00FE00E00049G11G73G16BE03ED66B5E70E
	AF5B814997343461482219B49DFC1F5EB03C7AEE704B27DF5F013AFCAB1F71D75F2F51B1AEE91EE7FD7F15625367F9567708BE2C6FADD0B6E29E6E15DC3E2C553F9C702B668DDEDFFC2F496D9CF15AFCC3EA667C4179786F217412B51A030FE37C77D568B71FEAFF4A06A5DE9653C73FED42EF3668677EEC94E465EB2E786E6BB6DC715F573DFF057EE6G74D14DFDF63E3F7D51957A7DE7D717C3D27B544CAB69AA2E5E51E8F31809A5ECEC3F77BC99473BECD6052D3FFECC7B39E208C745FCDCFFFD586A3E8E5E
	719DE9E7EC3D78EDAB2E976DBAB144F5992E0383663B2F03C366FB2E971FAD233FF9E013A513ECF3DA0D38AE7C9D6DC3A783FD04EA57589CF75B2750BEF59114AF85E0369E93DC364E2CC6DB5397682FED3536E6818D813435EC014BD6B65F95F7EB3E49BBEDB6AA5D8C3F25F7038BDFA71171C590783B7168CD2C4CE236CA556CD7A2856C43FB691C38867434AC409C1FCD3671F95E4E555CFFDEF27567B6262B2AC33B371C467BF806FB2A944732F8356D358E78EEAB62007F367275007E3C40BE60BDAFF08E68
	4F8B2E8CE867857C3D7AG29F3937449ADAADD200ABB2362EE20B2890EA60A0A8F26F691BCA68A435FC26D3AC4F1AD9CF73B86F706634286C8FB87173BEDB272CE99206D42EC3170F98E25B1DB21792B7118CEEDB45BC50D5D8F2B38B3EADD0452B50A8572F1041A1331FD2EBCFCCBF3FFDD5F554F493B2E2A8E6673CD173D67D737DC461CFCB19EF19F65A15DDFD53A4CBC24A39FC533BEE854D992A37E9D6F99B29F36CA17B2559F2D28058EACFB0711FEEB3BA3B7B3BFB73320B08757F08F36629AEE7C827DB5
	9C53915EC61E587D69F3AEFDDF525F55D0F551DFA6486B30081D9798EDC0270B3620FA4FE03665F558ACC6B030G7E9567F07BDE2AD677EBF56DC31B6EB53A740CBDC868BB09A982766FBF24FDDF5233F07F2C7A5B908D5EF48BE8BE9E861A3AE7B19FA3DB3563398278114F8EE59E9063F68EB494F8A27C70B1DFBEB439FC59D5324DB3E6B4465985257939848ACBCAF2B85FDDE6975FD7FDC361E3B975EC2BF79F3928D55BC7B2DCE20DB7DDCA0D160E7D6E7AEF1AD7C0E043DF951295E183FB9719E924AFA39D
	3230D135271EF6984BAE9615A77E6E1E4484A129DED843BA50FAA109740275C2B26DA46B05C468C0FB6D3C4213919C5F885F5261D29F1B2485C5084FD404356C1DE8B9583141AD87675EDD7207053FB1B89205B5754207E448EF12E476A53C111CBC63BF05DCAB2C335BE0CACC5E3F02118EFAC9F6A8FD9629C14868BAC9FE6A4E21C8C6755BAA07A23C57C0B49DF0AAE64B01BA51512D10F163D00310E4A5DFF2886F32E28BC469G1B3C9D221E46539F9A01F0137F405ECD54DEA2A5CF153269310D3B361FD89E
	15A8A4548A6BADB651D8ABF642D43EDED827F4139F5A018655EBBF4A8425DCA2B5C5F6A593C15CC72A7B44593CE6985DD423AF745E9134E145065A84D8F4484281CAC9E6E7F9AA0591EAD586D512DC2F109F213649D9EA598FBED6F67F747887ED89C2BCF712D4483ADE3B42822666GF1B92F962CD6FE796A7CC93FBA3CB286D8EA05E47A81B9A8CEF7D1FD6E6C6910646B42A7ECCD9D3F6CC6E2171D0407D67586C5D853A57618ADFDF043FED1G716BE04436BA480F780052CC8F0F31C8B5BCEF5C7C7C89535E
	EC25063BD895E19B2BA2E0BDC0BE10BCD059558533ABD5G5497085F2A0AA7B4F99EB46F765E745E0967D7F7C0FE558809BD7979747F827AFFA1793F00E28AA8262090AAED25B0681FE7F9EFAF71797DDE7D6C877B5EF66F876227FCF0ED7A58A2E70210D2CB62E731F70B75BC768DC2EA87EDF8128C340A106E721F72C272A6F4C2E6FF644253CE1415B65945CE125F6B0C22FC01CAA0A6E51E9556F549268E2AC091924E561E85357409946FE89056AB5DE43706AE83048FDC229B499A14973026A12C863053BF
	6AAEFD9BD659CE63C0E75A941B6A1577C9843D11963EC34849CD06D7522CE193A0C6C6C4101B8F686307747FAD432B1BECD75DA48B1E7D55B66AD6D858EA4D5372655AA77833EB7EB4A5F67BC3EE4F1FFF5E6F03722A48E7FB59B95FE97750D785F67AFC17E5FC0E0D013D4F39763259BEC7F08BBD878278C54F61DE453253647711FD6D33B47508960BA2DAAED4DA0DE44F252ACF11DCFB0D93DA08FC875270C2D6965AC26C7EDA43735FD0CB878893F26AFB09A0GG10E4GGD0CB818294G94G88G88G49
	0171B493F26AFB09A0GG10E4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG43A0GGGG
**end of data**/
}
}
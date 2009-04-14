package cbit.vcell.geometry.gui;
import cbit.vcell.parser.*;
import cbit.image.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.image.DisplayAdapterService;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import cbit.vcell.geometry.*;
import cbit.image.SourceDataInfo;
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
			ivjCurveRendererGeometry1 = new cbit.vcell.geometry.gui.CurveRendererGeometry();
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
private cbit.image.ImagePlaneManagerPanel getImagePlaneManagerPanel1() {
	if (ivjImagePlaneManagerPanel1 == null) {
		try {
			ivjImagePlaneManagerPanel1 = new cbit.image.ImagePlaneManagerPanel();
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
	getGeometrySizeDialog1().init(getGeometry());
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
	D0CB838494G88G88GBDFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DFC8DF89457F5280C15BCE2139A6761183844CFF6E45735E5175478D5C90943EBE9C3E2652B2B6214D763BEA566EBF0CBD25260045A24992DA4A45982E4A3EAB966C73131AD0CA2A41030AC490024C5C836G01E490A00CC65A11F6516C4EB2BB2BDD0100F74E3D774C4E6E4EEAE57563EB3F6A63B0B3671E7FFB6E6F5CD929ECC7DAC12E23CC12F28BA47177FE2EA439DD127405AFFEF802F08D6FE697
	CB163F5F823CA26D3D1E8F578EA8DBF2CBF6F12914BFD4844F7D703CB4B53B78B5385FAC958F296761A6019EA716B52E75CF2E276CB469A9E44F77F41540F5A340G60701A71AE6A7FC6D73940278ABCC12E83F015C83334EB2B402D861AA2409D82682D6474AADC39811FEBEC90F47D37BFA02D791E6E5800F2ACB28932846FEAF12D10DE481D3B8BF57D0A5909BC1570BCG004E67C9F5FA95DC1B3BC7C78E8C8DFA0C6A31A1C5D52BC7944D2B987AF875C8505358F8C4BC1D72A8A1C5AFFB8F659DBEDA4478A5
	E9B948CAE338FB06D1C79F5C7B81E63EC778DF2808DF81776BG4AF613DF9F3E5BAC6C6E3B72A752523D5E7CCD897C2A5A4D7D695D9D71EB3E7799476B2C8EFA7BB0563321EC81409200D5GA5D55945D5GEF303ABFF77ABD38368C9AED1A572B79DA7C7ED655259F55DC0A8A770D0DD0A2F0A715A1CD071C63751BF7344A461E4900733D721979F8CC9E58C7F51DF17291E97923DFF6945A44E47983D79C4EAA9E0B7E2AC8CC386E0F926A1E280A4E45E85D27A52B6E2DC7FE10D7E0233B216E87F933051CF40B
	6E15D5D8572F0FE0DDAF06FB6443BC6578CA062F7ADD844F73728FD84E60F3B714AD7C9D65C65FC63225AB7FDE293476CE5BB84C7D33D90E4AEAEE43216AD8DBE678D036DA4DF5B61936BC40702BACF8EE4BBA1627782C6E49AE6E284DAE6679F57EAC36531D7B20FD8274817881E683E4GAC5AC7B916FEFA4F94F32CDB560D5620C7F5A93A44F94F777DBBDC1530214B1D3E00A17B0614D3326E1187D5A5A0891A61BE0C41E0945FA84B7DA150F1C471A93AECF8B49FC8172141AAFA005398FD2FA00D2E3052DE
	0FD78185DE6C68F879C45FE674D98E981F78DD3221C4A8F8F9EA9F564918FC8AA402G3867F139659C46AB877CDF8BD0D4C7F178B5137731227B94353131CB53C6037EDE74C46A2B43B84B2C6F588A77F5F594634F77A3EE9EBCAFB0656CBF732AC50E53B304664B7AB835319DA3687B92204F81D883D0DE8FF584B0D0CFF97453BE4AA3670607252A276E304D238C4869F0BD4F1F5975316D291A69C1DBB321EC9940DA00727D59458D7BE99CF8F8F840A20F6CC9545617D84A4866497A0681D14EF5AD0952D5A0
	7009F873E25A794B1350EE94EDF123D947EB4E33BE9D7CB4G666C273A39F4260267FA2B9CD0A83B0F6B10180AABD2D35C5E19BE832EEF89DB30FD3DA6649F87789D4A81588D708940C111175BGF6G6C1378586B81B882D08F70BE405BG6F826C8240F6D78BF08E2093F3866083B3DE0FB07D99E07BD200F5GBB5F4FAE6E82B0G6681AC86D885D05610DD5C8C6082188530GE0B9C07E87B016G7482CCG2483E4812C81585A18DD6C84D08166GAC84D881D052C4396170BE199BDF97138145479A075F0A
	98DA7D5EAD62C77E6F33446783916B0DA226AD9679C7ACF15AA86A014ABE315C1F1B44C64F94FD796F8A3CEE474D3AAD18A29F35C1EB7DE25D664554A776211BAC753A43D26FCDD2244D3F466C0848FF5BD27F56760C3045F2DFBF090DEF7D97086FFFA670BEEE64AC46DF99D3FC06D371ABBE975C84C4196FAC5623982F07B51347E0F8BF0C6FEDB02086CC7C6533D8C732E1681E41202174A80661710DC44AE7B0DD016020BFB2B5A0DDDFB80B392505E0FCB769FF6FAC0E0DC31A4F27304E56E491653359582C
	280A970CEE5302BE03633FFC8E57AF234A38189B5CF5EE3D04E33B9A44F986DF934C17B1BF7D3A478BDDB8CED52454A69C3743C74BADB12A839C2EED7A602AB651F89BFA0B465B1D177FD0DAF4416F301BC34E3D6FB6C7D6939F53F21AA263AD376956FEFB5BEFE7F384018FF0DB7F4C0736F6FA65918517A77BDC2C9DED16426B1116D6AE9C773DG4E315A027A18F292EBD3D7F4A2281584DF77DBDBADFC6D9ED5E6C6A00F53A32B5AC8AD277B4E754D963A9E4FE5AA96F236B715D84B7D4A909FCFFFF78171D2
	B30CB5GD54DB41EEEB80F65B4579183E91B262A3CFA21ECD6B3564158C8190821931E7B1BF99D60F35907D83E4AACC7D89AD7DE6E361677FF484B53614A653F24D2F9A6601E7CB0424F4BD34C721283B00E9E0814F3BFDEB8DF0AF92CDC8AAAB00545C95F47E62E65FE7AEACC59A911F73C3C78D32CCB55B2DF1CF7GF53E6823F5F25F81BE87738B3DA987386EB987116ED10D68D682DE82DC45C1BECF9BB848694A8FA1DD48CFF4B987791CE06DC1AE37884A2BCDF907AF911D6BD0C46F845C279D324AAB5509
	EE41A1BE37483250233C0E9624DBEC52659C9273C83896C1D99540F640F3FF6F88905D1C966EE70638AEEE61FAB95DF1431457A27C90F4252DD63A1F19F4B52DFC4E5A519A9DBFCE77E010687A851D51EA976721B1221B594A7DCCEB65FE4C07EB96G273BBBE45AA768568A3A22B6AEEFEE9B52E9E1225B594667DE98BF545B5CE60D73AB63C437220D4F4F5635C564C66A6DE9132E4499A947F86FF4DA655DFF19689A1CFC1EC77596AD6F6C95226BF67238381D91FFD31C646F6C89335E84DD0620DB6C347A7B
	1C430C0B134FA96D7D3DB50F6856BA793C3374B0372F6230553EB379E67D9E66F2BA8EF33D8378FC186C3B230068668A3A7902EE49E12BFD4AFA22DBF6184FF1493E55072DF9D0D8C8F4C56D3C3C221D373734D62B3C3FB1699A5A79DC1964F537DB65850B08AE3D1D7B39301D37632CF62B3CFF59E6462F1D4F39C95E5AA8F9F33693DD7991AE2F66882F973CC6627C698E22BBF4044F5DA98F7A0E70B8733166673BF07CE3FBE59765B1B9DC9D8841985E5878D1378C3DB65F977B91DB5BA23D7F886DFFEDBC01
	FD7E83703C88E059919AAB7FFA836DFF4D99FDC0DAFB64293CC47BFA2B053DE59D1131F2625B21BCBE260486D0E7AF1439BAE8BCC97D543A86EF898434A18FDB7E0B3EF65F856BD84AF18D97DE026B30C78F981DB0BC0629DFBEF0816D457D8101967BA5DFE5FBBAB85E05C16F7C8E5AD3FCB07FCDA9711EE2E4BF11761D0676D19CE6DF3ECF9A3863AE474B896230D278DF549909C3570775F9DC6F5D7B76CD22176FE55A0E7E420E2B6D567AD83869EAC2BB8E897D8396BB163F75CBE14775DD9FA435E3129986
	56B34B1BAD2334471A86FAF84CEBD55A47DD8638520FA8662E23B5D35699192D08BDB8D3E68748CBB1E5FE297E1DA955A34EEEA8A7F6EAA5A27FB3C14EEAGBE9F3AD543F927617608F95BB58746CDD706F5A560C603AAF71D22FC38F773A191672CC76E151A2FFF6A305B971E39496D287C18473F7B63C8BD24AFFD461189406DD9EE5AB38FE8168270F910315EBA8FAA6962FBD1F5DDE4EF1BFFAFDCE5B6792A6617EEC59F56F42F622A4456DFAD070CEA36B628EEE1450771FE977595FFF19557E19E1F47286C
	322B900ADD933AAD7CFEF83B556FA279F336FE4FFAEE445151457D75F7C57CAEF96A99C745D364F71A1F5AE6A6502CB67DAA3C0475A06BB40F5DF3096A055BB0F106ECC849DAA015EF7E222D8D8815C7C56C0FC6ECD87623E79C2BFEC4B678AE118DE9C0338C002FA31EB8E1DDC7149E63316F062B6B9859792F17B077A9DCDC5E87BA491BG3A8547A8CF4B8E60152DE14C3E69A7A66DAA205BF91C64768648DFEECBCA1155167E635C1659F0CDB7F9A68258AF8FD9FB8E334E7E2545DAE7190533EC63657E0339
	0E0547F91CD69C0F44EB76334FB8521F25F8BD9E20DC2D6B06B5D5B769FF5BA89139840D16B5956EF753B84A89A830C6D2B57D286CE7ED084B8A9BA4AB9D64AC6F26D89DE0E3118F72D8D6CFAA0120EAF07CB7CF637AB46056C2115E01099372FE93A4F9E5A7320B3BCE10EDB785F17D28691E910F0FE87F4624CD853A05A6ED639852421AD9418DB9CEBBEB0CE857GDD55C9227D79985593FFFEA344FC55027A1062148D3168667D64419075132E1354CFFEA7C475BF8FF0CBCD395561E8392742F6F2390C39E1
	322D248766ADBD94438F86F03F0C6D27ABBDD11C495ABAAF7660EA35BA1A33C964530B5BA81FF6BA532506B523895B5FC40FF887539349272C2C7A3C9CGEEFF63B845E08D50D475D28C6E9927BE005365DFA6BA97504CB369EE3B9C5F867F4A24DD8AF4A51FD03CEF3EC231F282EE629312710F8C9F103DFED5F131E5BA2F335A892A3385260CEFCC18ED96F0BBCF110C0A2B31B2B87EC9C7623AFBB50F6C6987B9F3CE110E85F924A38BF07927C9479B79517DE2DBBE6589671B11CF721A0147B0791E4B37EF1B
	8F3E4D5E6778F0996B54E51CA876EA1A9A28BC0B0D0A47E0FB8145A063B45977F881593782F0DB7B4CBEE0FD2C9E0EBF3E9E6D74EAAE05737F5EFA323397F8E775D14EAEEA42390C9786DB242D5C55A962D4D3C87A96836DBAD35F1D05317A44BB32C2330FBA83FD5499127F2FED6DE61B081FE7D46E32ED98B0AB290EA7EE22BA3C3B08ECCB87BD4B4F10ED0FEE0BEE43CFEFABB4E36D923CEFECB77B2B3350DF1DA5BB535AF15C7784C86DB14DB896D4554ADDE7B95F3736134ED460D9F816F46E5C911D8F7FE3
	07593600266A9C51C995A516B603B3A33ECF7C6338F7322EF311F7BEE14B3DB957656FB0D9FFBD6B9CFFD77932091F4D709996BC372B0C6171B98B4AEA7A697D5ABF8C61B8B7871E6783AC814881104E431A6DBC2D997E72824E1FA7FDFF5969B3E0C9A12BBDD19B15749EF1GE3B02A0C0B6714816B384C63703591F65E607C646F3B2C40793E4E78FABF839F777BE3856BBB957852GB2811E84D8ED7ADCA0D7A573195E438AE7395C2F8F235C52CF216E811CG7DG931F125C63F252D89E8E5B46127B7B1B51
	494E8F4C7BB4BE8E1C6FFDF5FA67958ECF136F61E9727D69B4795E53E279664744A2AB661973B54471D547BC2F0E616375DC68463D01228BB09E81B4837481CCG4C85D8F8016A7BEE794324750D8B04DE2D8DF09D1AB65A963BF7EF5A7A23E97A38BA290F4BAFC47371BEF1D38F7AEA332F519AB48C36774C472B07581EBA6ADAF3017A15EB9E0CCF55C0F6F18740G4084C09A40C2001C810A4FBF4BCDD30C8FDFCD45C7077BF83FBFD95E5847E6C1D2BEC9362B7FFF3B087E15C3D99DC097009BE0964083GD9
	B27957B2581A4CBF73D88678C86F01EC12005BEBDC1A1E1FB312722D106DF2606956BAE94A7B874141D31A9A74AA16FD2F3C13346F35CE26FD2F5B59591E3AC1089B00EB105E079F5427675B5124FC9303F6FCBF1F263E5CC0B23EB903F631DC96F8B7AA16EC3E5F5848E62E5DAA446C286C03DB1D05CF342D4CA20ADF46A0456F278ABBB7874F6BG4A06284D85D45666603983E0GE082A08DE0A1C04E9065649D49F352CC442964646238F8CC2DDF4A0E630B6D17568E5971DDB32627EFD69CDF2C3ED2179DDF
	C5F0FA7A2A6378E275D55A6A7B3F53547763247A8E596A7B52587474254571456A6B3355678CCDE50C77DBF8B95F771372595B79F4D23ED4DBBB6F8ECFAFAE99F1FC31F11967329BD3FE6645763BG4A1E84D889102FE497D782B8815C8A355F6786714C4ED4471464730AC363530B6B3124FCB395BB3E17AECFCFDFF9D23EB4DBFDCFCDD35F33C979B2ED75FD614A7472664EB83E583CF952D65F7E0929342715CAECFB7AF6D2BEFBBB3F1F14EF1D2D1D65F9530B4BBBF1FC31F1A99B36637BE69C5F547CDB1214
	2F4AD65F4C7C69797765B83ED87F1C367A5EAAC03E7E183249C00C5785530BCBF6D2BE3F2D1DF356CFAFAE7746714546E5262D3E7D0553734F1914AF4DD61F23E8FA7E154671457A17E92BAFBD0EEFEA7E7DF1D23EA7ED757DF67B747C5B9347976B5FCADBFDFF9E47B7B57F7EBAA91FB4E24777459D5353777BC9794AE37471F36E7F464E8254C0199300631EBE0EB8B7BC270C50FAE866895CCBCA0767A5GB9G129B667BEEAA7FB2AB47F3C78DEEFE9E224F4D4FED083D2293FDF04D917C13599995A31011AE
	68D1D7A2BA2EE39F530177D3BD670A006FB4684C73E4674E459E41D06273163F9832BFEF39EE487E3C2543758A47038C6B5796D47E12EB7273182F3A6C4EE3EEE37E7C40016BB2BD68B3BCDE0529656BBEA50C67F8E1D6F6B26863D3B6549C085DC7B4F574A8097DBD2B587BBB20587BBB2CCC6E2FCF195C5FEF8CA736E551303DADFFB9ECEFCB56706436AC9DCEE48BEFA3352354C6421017BC665F7D77CDCCE6A0208458C6913E70917571E3834FB5471D05BD86B3E3094EF54ADA3120B9ECE8ED3526AD2F3893
	7BDD6E3677FB3B9B5F877377CE313E3D6DAEB74BA2B371CEC37122B91C261A719B32BE224445261E717332C47C875C096247FDFD5DEF7175E82D590667C70D3373C41B1FAF2E0BED7AG2EEF26A7F10CEE735847688ECF62984D73A40F51FD1E44B14A70A40F51E31E49E3740C7FE591236EE1DD73F6578A59C52309FD5DB4EA6F6B1651443EEE9DCD6E6B5B2309FD2D9ACD6EEB7DA87AB268F1F9F4F1DA187B7850A5EB9E6CB773206824B58F1645547BAA1BBC606F1C8A58F9AF49435FB9651A780D8CDFE24173
	F7D1F78E533922F2A81B7050BB32B1BDBA767B053D6F85485EEEC36B0E646D37A61D4F64005CB51EE81B39DD03CCFF5145D8FBC7993E6CE22C3DEB943237824AD2AF52BA7C2A1AB8A7D2D57B1C38C5CD1C93372B49F362AEB5F1CE5C23A64F0987D57B1CF8B1E06D875FB75B43E306955FE062CF872D78BA93DFBAE645571B786FDA71C7D18E6F534F31B39AF82EF265C54A037C3015F62FA96329F0D4FE34D15BFC511B389E9CBE7BFAA872A52E07CD3E6475D06ECBDC8FBBFC496B2152B7F9BF949A0FEE8BFB85
	3E6532B5B67BCC7A12AB51747B84FE5195AB7D87E65C0FCED05C571AF17F450415B6D22747732263CE367CAC9A5FDDA7705F4E374A29B7F5B632F38DC5235945652324734582ABEDA46F1ED89F2D137C71963EE4212F2DA17A8F0A22694916270B22684D6FB97A36C753578B7C8B5B236877927D629D513E927D16E3F8F6C9154735A01EDD9AC5BF2B0E515931AAF8F68228GA9237C8C991F93FEBD047206F5598B554E8ECA9AAABB43FC1CF8F300FE8DC099606BGFAD50A5942FEBCC3E27F56056C7D8D1B0361
	6109AEF99051D4176C7C2FF803576616FDA30AD3734A9E6A873EF2827B560F9893FD33F274957C164C7EB5C52D78FE449975FD49C2157B1A235239CAD933ABDF83D76EF747A879DD6465FD76D6F1ED76529A69BB6CBDE3BFBC7BCD5CCF2FA3EE8EBC271B389E09660973CD5C8E769ECA7201BDBE42556492DD030F56CCAF63B1DBB13E14D96E3FBA5CA7A59EEB7E2340F57C6F9B2A037B3C3408FC1EE94661D685F14B6039C823B83807A33EDF325C3F205C78B8EC3E21F1285250E7BD0467F63642BDFFF6CD6033
	DF23B6F622004EFD4DE165A32C6F42B9C00AC6E732FA0F19E72E1146CFED3BF6G77714D7766F8A2383171086EF1354AA3DD2C27606F76669DA3199DFEFA3777BDA6B3851E67824C7753B78161F7688CFA467D77CA2B5A6C3F8948693D6AD862676DE615BFF27EEF3177998757D95ECDBAABAFE5978FDCC2DCFB3735ED227F1EFC3ED7434B43D165B8EE5DAED94B9F0CAAC7FB1F4F3316AF0FAA47989D947C5C37A75EA65F56BDB1DFD29FCBCD78BBA68FDC62BEE5096B6C95DF49ED58F4C51C2F7F79F0026FB0D8
	EF467D3FB2D2A474666B5097B05CDC0D4EAFB41BB85FA83DF7B5F40A537D2331F1980D72F359E82C1FCF09B8721C7A7B75B8D68CEABA1BAC7031627975546FE5001E45G2B817283FCBF077395C86C0C99CCE236A2DE3AAB08D11B93C07E0B12D3708F846C4F7E648A7D888650A0EC91764D8CC4663ADC577E1371B69EBDC9B6AE8FF09B57403548605F3CF1BB0E64129D95C63C9DA41F76FD6A0086E10BF86EB0A2F6F0F97FD4C472FA9348AB3048F323AD96F9330DD83F6ED5F08F46658978E12CEEF149FEC351
	FB94FD4CB344474DD31E00076F4170763EC821F6B24F20FA18497A8CDFD0D5456F54A8565F5F2939526548CB103773058F0B44F55D163BF2394C4B93A413BFDFABC03F06E5B520186DA3ADE4555370528C5BEFE791D68B797941089E5E271EBDB63DF3D56EE9725D731979789AEC5EF16C7F1A0331EB337B983EA3983BB6FB526C03FB21ACB3C8E7A18AC31833DB47E0EC876886F0834C84C89F23755B93BE7CB6E65277283D1A3F8B96962AE53B0A7B372D7B337A476B727F94D2DF37601B8FCA191FF7A67C16A8
	F30C5761B2F12DFC76AB39F8657E5D95C67F0AC25945BB81BAG86G8C00D9A172EF33A64EB09EF6F970C407BC64E6DFF5199B0F9EA594C1C74E24260CD8FDCB8BF17D8B44F589DC17076C76433F9AF54EEC3520498F0BEF6A42094EA686224EB4D6815DA19BDAAB703A5FE61ED16E857AD9E19AFF3F739B7CE6B6F24E069DD2EA45EAECE57DDA2BA27B686C1AD2C8E72D9707E98CEE3A0EF25741F359B84C8F46E98C7E2BF356F6D8FA176A20DC0C354FB9AE3E01990F0C41DC5F6FCBB42E24C2598ED6CF924351
	9F6F639F3FAC19B97343924A192D8FFDCDF27E2D5371D6823D8B053E124B16337FFA05B8773F3B0762650472094BB45685FA4CB37717A986D7A524DD854F79D7320B37DEA15AF0AE59DFF7E5B27BABA7701E73ECADA69E5516E775C1BB9C1753F7D512288F7FDB455AB772F6D0F08D7B4F74AB14734F84719B43D372C5511AB175B51DE5BCA071ACC48FAC39FDA3447F7CB55419897CCBCD99EF0E6D1EC4C6372E79955D9867D87625BA3D2F444FBF447DC3167B9F9C3B71F366FF3E21BAF8DCCAC30977D9DE8B59
	6F33EC8B593F8BF8ABB479FB075D21443FFB31B7E47D5D8BDE0F1BA51CFB2D01BAAC1D003985C087C07F84556922906685BF59965B8F9EF60D60BE8F7E3A16C17A3F96CE6C6B0361643F4975E898779186B5D8E6FBD13E79BEA50C75E5E8FE0664384759FE176E99F19B1CD4609707B1B7D5E5182179DA6DCEB6FF8D03DFB3A7E87D566DE53F11854F99A66EB1AF2D73B2CD5C738E367FF2B53B3862AA61963039EF9DBCBBCD5CF3B534266EB3F19F5512BC17093B054D11974073A7F9343E3C0D6132GF7A257BC
	2710743D7A4A2BF663C9EDD23E6D36ED6122ADDF7E3544E38A1FE37C916B774B2F454EBDB299FE67355839C74BF51AFB54C0591CEBB47728C11428435EF17C5E5336AF014C7B8816A94787AFAAC3C62FBCE25A1FA6D96DDF8BF2CB2ECFBE9EF23D679CB8E72EG5AE6008100B040AC00F400CC00AC009C0035G25A9B0F7GE8G6882688770832426D01B493B32AB116D00948BDA56F0F45943FECBCB54DB4F746A7BE0D2BE7B7A8E5B724DCDC9D65FBFE3E3DCC6CAECFD3F407019A93175FD33C4753D984AAAEF
	223D122B39D67D33G1FF6536475A54ED594DB7996G4F12A4FC6277D02E619AADA046B1717BAA57305C6CCF7930A44AF22F33EF1B681BAD7639BF9F85F9799BD2623E6ECDA9F9DF371B0D5B8A74D13D63FE313FDDA3316FBF0707830A592756CB25A62EB3502D85BCE8899F5B9F9941F1FCD87399FC7D351875F34BA19EABEF22755708B7FA9D7CBEBE0A75E21617560B65B362E3C86BB2DA87D74D08DE879F1A913BDEAC49E33FF920694A082E85FDAEDA976E4CC33B3A003EEF8659750496ED7B8FD91F9A0672
	D4GEEAB7B29A6E12BDCCB36E65A581A9BE3EB9650A0102DB996DB79FCD7FE4772CD5EE90F4BF0F37C567006883EC34171C512443B71C51BF9196A71A9ED7CD7A2A56A43EFE2F362D520C73A19F27C316B1219370FDB6E0FDC3F71F31B019B2A037B7CA7920DE539A6EE21C4E3D92E5976B4C76236B266C85ED62EBB6C67851FF3444F8BEEF1584F8BE6BB2C738271DE1D6D890CC01F5CE7525DE5624E18387F45E4A20EA50A093F0F5905F8CA940EFF085995E238BE011BEF411D9138479C585E41E53726936FB7
	9D2CDDF8D4D54CF3980D47874D5F0C471FB39BBDFE59E2779293F7462C0B5B589A258C7231524C49DFCA11BCE44D014E5FCAB7BEA76F3D21BA384FF3F26367D7DF49251C7CE1AE619A0A096E17A65D0745C4079FC573BE687F4998A35EE7B93DB89F76A9218A73C7AB5A21832BFC53453E35FD332A22262A22F66FAEDA437D4ACBEB38661B6D57F0DC07E3881F787D432E083E336CF7B5CCDD6C17892A87D5F9E814537E1DA7C23B3C665D49EC2B9E54D49798D88B7F6AF6897B3ED4EB6E575A5AC7B67D6AE2C44F
	5CC2249FC2D3A46A5F7F0875FD99A0A373E66A5F6F6F09277B964B4795C0335A4CC7074FBA1EEFCD4DAE2EC91D4ABC604EDAAB1F93F87A1270F1DFB4EF44176D57F744460C453832F6FFCD95425E7A7ADD026FF9AD42B7A0CD050F67546B3E78BEF23B4F5AC7F25CA33571F40F56DA69786F2E07A6D6C2E0B3FEA2959852867EAE324245DEC686F4E913E9CFA76BB0F24676B566BD7B35BC07145FA92D679DE827140F3DE027D440BA49CEA98FBA50B07DD2FB81C16012F40D778F3658FCAB98D2EE05A19572F722
	0DE04706289C7C0BFB6A7FA16B6FCF857224751D526BB86477A8BA7F925E05A74F44CFA137CB45FE8D2644787E950C8C30CBC14098D715AE29EC78A47E54DD40D0DC6637D581C35A52051A76848D0F3A27C38E388D9CB71A3A24FC9FFE49A13D4A0BD508529EBEF9EBE21E66329F9A115854D482FBB7B3FBD1CA35A9E5731FEEFA7B379F2D484C139C6D52CB2AA63B5A65A1184AF7CA45069BFFE8879AD458FF108BAAE2A7B2F0D7B28F443DE13A0F4ED6774268E69ABD9A7E6A2C8DABB734BBE03223CBFB98A54E
	4E2ACD8A97542A8E2A943D53409F2156741DE659CD5B0DF73EFF6435FE0714AB1C448AA98E7B8DE97D306C752863F0433F7CE7D53541GEBF55997409F5B814BAAE2AC23A2D3C1757F7CEE4D9DFF7842D926A042EAC8BBF8A801F58FFE483827E5F898E6C126G08AB085FE60AC71A6A981AD74337EF796829DF8FC29EB825BCEFCD8D7B3F167D3F977F2FE518DA06295D8B41DDADCD7A97DB9EA61FD972186D812B3F3A9DEA50783B1FFC775E271BD72F826F5A258276B1BDD4C2E9046AD850BB28681F4F38E35B
	4BF3F2EE45BA72E32A04D16EABE17B6C87BD9B237B015C7BDE3B397436FDC107D4540E75227A5DF2275057A53DBC489A1E22832DA115C6624AF8A1F99DC31059EFC4702C936525BDFEF988733B58A56B23CC821AD4F1CEAA9E56C7861982C3F21CEBBF87EA5993963F59A53DE438F13786934D10DE0B086E46B528A860CDC37AB5001F7DB1F7595B30168196DFB653E658176372BE9F511B6232EFBD1F713047B71087EFDEE50FE56C7F57BEF3A5A3576BDCA05A451E5F0D54CD01945BDF6DE0EE25257D5A7234E2
	0575897E7C16BFCB095F7F47D14F470ECDA4203CA17279DE762E6B56BD74351F9BCA78AE2B64D63107FDEBE4AF1B6FF36CE263FABF60433752DE4578F57CFD64C47BACBDDED9D58DD99DED7139F04F25F55CD0A2FB0D55CCDF00E8C4A12DD9516E0B961E7F8FD0CB8788DCE00796C4A0GG10E4GGD0CB818294G94G88G88GBDFBB0B6DCE00796C4A0GG10E4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGFEA0GGGG
**end of data**/
}
}
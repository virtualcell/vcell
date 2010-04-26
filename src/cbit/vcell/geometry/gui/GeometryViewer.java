package cbit.vcell.geometry.gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.BevelBorderBean;

import cbit.gui.PropertyChangeListenerProxyVCell;
import cbit.image.DisplayAdapterService;
import cbit.image.ImagePaneModel;
import cbit.image.ImagePlaneManagerPanel;
import cbit.image.SourceDataInfo;
import cbit.image.VCImage;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.graph.SubVolumeContainerShape;
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
	private PropertyChangeListenerProxyVCell listenerProxy = new PropertyChangeListenerProxyVCell(this);
	
	public static final String SUBVOLCNTRSHP_CHANGED = "SUBVOLCNTRSHP_CHANGED";


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
	getPropertyChange().removePropertyChangeListener(listener);
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * connEtoC1:  (Geometry.this --> GeometryViewer.refreshSize()V)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(Geometry value) {
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
private void connEtoM11(Geometry value) {
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
private void connEtoM12(Geometry value) {
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
private void refreshSourceDataInfo() {
	if ( getGeometry() == null){
		return;
	}
	final String SUBVOLUME_CNTRSHP_DISPIMG_KEY = "SUBVOLUME_CNTRSHP_DISPIMG_KEY";
	AsynchClientTask task1 = new AsynchClientTask("generating sampled image", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			GeometrySpec geometrySpec = getGeometry().getGeometrySpec();
			VCImage sampledImage = geometrySpec.getSampledImage();
			SourceDataInfo sdi = new SourceDataInfo(SourceDataInfo.INDEX_TYPE, 
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
			hashTable.put("sdi", sdi);
			BufferedImage subvolumeContainerShapeDisplayImage = SubVolumeContainerShape.CreateDisplayImage(getGeometry());
			if(subvolumeContainerShapeDisplayImage != null){
				hashTable.put(SUBVOLUME_CNTRSHP_DISPIMG_KEY, subvolumeContainerShapeDisplayImage);
			}
		}	
	};
	
	AsynchClientTask task2 = new AsynchClientTask("displaying image", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			SourceDataInfo sdi = (SourceDataInfo)hashTable.get("sdi");
			getImagePlaneManagerPanel1().setSourceDataInfo(sdi);
			if(hashTable.get(SUBVOLUME_CNTRSHP_DISPIMG_KEY) != null){
				getPropertyChange().firePropertyChange(
					GeometryViewer.SUBVOLCNTRSHP_CHANGED, null, hashTable.get(SUBVOLUME_CNTRSHP_DISPIMG_KEY));
			}
		}	
	};
	
	ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2}, false);	
}

/**
 * connEtoM3:  (Geometry.this --> CurveRendererGeometry1.geometry)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(Geometry value) {
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
private void connEtoM6(Geometry value) {
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
private void connEtoM8(Geometry value) {
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
private GeometrySizeDialog createGeometrySizeDialog() {
	//cbit.vcell.desktop.controls.ClientDisplayManager.getClientDisplayManager().getMainClientWindow()
	Frame frame = (Frame)BeanUtils.findTypeParentOfComponent(this,Frame.class);
	GeometrySizeDialog gsd = new GeometrySizeDialog(frame,false);
	gsd.setName("GeometrySizeDialog1");
	gsd.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
	gsd.setResizable(false);
	return gsd;
}

/**
 * Return the CurveRendererGeometry1 property value.
 * @return cbit.vcell.geometry.CurveRendererGeometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private CurveRendererGeometry getCurveRendererGeometry1() {
	if (ivjCurveRendererGeometry1 == null) {
		try {
			ivjCurveRendererGeometry1 = new CurveRendererGeometry();
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
public Geometry getGeometry() {
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
private GeometrySpec getGeometrySpec() {
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
			BevelBorderBean ivjLocalBorder;
			ivjLocalBorder = new BevelBorderBean();
			ivjLocalBorder.setColor(new java.awt.Color(160,160,255));
			ivjGeometrySubVolumePanel = new GeometrySubVolumePanel();
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
private ImagePlaneManagerPanel getImagePlaneManagerPanel1() {
	if (ivjImagePlaneManagerPanel1 == null) {
		try {
			ivjImagePlaneManagerPanel1 = new ImagePlaneManagerPanel();
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
			BevelBorderBean ivjLocalBorder1;
			ivjLocalBorder1 = new BevelBorderBean();
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
		//setSize(506, 564);

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
	if (evt.getSource() == getGeometrySpec() && (evt.getPropertyName().equals("sampledImage"))){ 
		refreshSourceDataInfo();
	}
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
public void setGeometry(Geometry newValue) {
	if (ivjGeometry != newValue) {
		try {
			Geometry oldValue = getGeometry();
			ivjGeometry = newValue;
			connEtoC1(ivjGeometry);
			connEtoM6(ivjGeometry);
			connEtoM8(ivjGeometry);
			refreshSourceDataInfo();
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
private void setGeometrySpec(GeometrySpec newValue) {
	if (ivjGeometrySpec != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjGeometrySpec != null) {
				ivjGeometrySpec.removePropertyChangeListener(listenerProxy);
			}
			ivjGeometrySpec = newValue;

			/* Listen for events from the new object */
			if (ivjGeometrySpec != null) {
				ivjGeometrySpec.addPropertyChangeListener(listenerProxy);
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
	org.vcell.util.BeanUtils.centerOnComponent(getGeometrySizeDialog1(), this);
	getGeometrySizeDialog1().init(getGeometry());
	org.vcell.util.gui.ZEnforcer.showModalDialogOnTop(getGeometrySizeDialog1(),this);
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

}
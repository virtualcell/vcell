package cbit.vcell.geometry.gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ZEnforcer;

import cbit.image.DisplayAdapterService;
import cbit.image.ImageException;
import cbit.image.ImagePaneModel;
import cbit.image.ImagePlaneManagerPanel;
import cbit.image.SourceDataInfo;
import cbit.image.VCImage;
import cbit.vcell.client.desktop.geometry.SurfaceViewerPanel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class GeometryViewer extends javax.swing.JPanel implements ActionListener, java.beans.PropertyChangeListener, ChangeListener {
	private GeometrySubVolumePanel ivjGeometrySubVolumePanel = null;
	private javax.swing.JLabel ivjSizeLabel = null;
	private Geometry ivjGeometry = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private javax.swing.JLabel ivjJLabel1 = null;
	private CurveRendererGeometry ivjCurveRendererGeometry1 = null;
	private GeometryFilamentCurveDialog ivjGeometryFilamentCurveDialog1 = null;
	private ImagePlaneManagerPanel ivjImagePlaneManagerPanel1 = null;
	private GeometrySizeDialog ivjGeometrySizeDialog1 = null;
	private javax.swing.JButton ivjJButtonChangeDomain = null;
	private SurfaceViewerPanel surfaceViewer = null;
	private JTabbedPane tabbedPane = null;
	private ResolvedLocationTablePanel resolvedLocationTablePanel = null;

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
 * connEtoM11:  (Geometry.this --> Button1.enabled)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11(Geometry value) {
	try {
		// user code begin {1}
		// user code end
		getJButtonChangeDomain().setEnabled(getGeometry() != null);
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
	GeometrySpec geometrySpec = getGeometry().getGeometrySpec();	
	if (geometrySpec.getSampledImage().isDirty()) {
		return;
	}

	VCImage sampledImage = geometrySpec.getSampledImage().getCurrentValue();
	try {
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
		getImagePlaneManagerPanel1().setSourceDataInfo(sdi);
	} catch (ImageException e) {
		e.printStackTrace();
		DialogUtils.showErrorDialog(this, e.getMessage());
	} catch (Exception e) {
		e.printStackTrace();
		DialogUtils.showErrorDialog(this, e.getMessage(), e);
	}
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
			ivjGeometryFilamentCurveDialog1 = new GeometryFilamentCurveDialog();
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
	if (ivjGeometrySizeDialog1 == null) {
		Frame frame = JOptionPane.getFrameForComponent(this);
		ivjGeometrySizeDialog1 = new GeometrySizeDialog(frame,false);
		ivjGeometrySizeDialog1.setName("GeometrySizeDialog1");
		ivjGeometrySizeDialog1.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		ivjGeometrySizeDialog1.setResizable(false);
	}
	return ivjGeometrySizeDialog1;
}

/**
 * Return the GeometrySubVolumePanel property value.
 * @return cbit.vcell.geometry.GeometrySubVolumePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GeometrySubVolumePanel getGeometrySubVolumePanel() {
	if (ivjGeometrySubVolumePanel == null) {
		try {
			ivjGeometrySubVolumePanel = new GeometrySubVolumePanel();
			ivjGeometrySubVolumePanel.setName("GeometrySubVolumePanel");
			ivjGeometrySubVolumePanel.setBorder(BorderFactory.createEtchedBorder());
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
			ivjJButtonChangeDomain = new javax.swing.JButton();
			ivjJButtonChangeDomain.setName("JButtonChangeDomain");
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
	tabbedPane.addChangeListener(this);
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
		constraintsGeometrySubVolumePanel.weighty = 0.15;
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

		resolvedLocationTablePanel = new ResolvedLocationTablePanel();
		surfaceViewer = new SurfaceViewerPanel();
		tabbedPane = new JTabbedPane();
		tabbedPane.add("Slice View", getImagePlaneManagerPanel1());
		tabbedPane.add("Surface View", surfaceViewer);
		tabbedPane.add("Geometric Region Details", resolvedLocationTablePanel);
		
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 2;
		gbc.gridwidth = 4;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(tabbedPane, gbc);
		
		initConnections();
		connEtoC7();
		connEtoM5();
		connEtoM7();
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
	if (evt.getSource() == getGeometry().getGeometrySpec() && (evt.getPropertyName().equals("origin"))) 
		connEtoC4(evt);
	if (evt.getSource() == getGeometry().getGeometrySpec() && (evt.getPropertyName().equals("extent"))) 
		connEtoC5(evt);
	if (evt.getSource() == getGeometry().getGeometrySpec() && (evt.getPropertyName().equals("sampledImage"))){
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
				", origin=("+originX+(dim>1?","+originY:"")+(dim>2?","+originZ:"")+")");
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
			if (oldValue != null) {
				oldValue.getGeometrySpec().removePropertyChangeListener(this);
			}
			ivjGeometry = newValue;
			if (newValue != null) {
				refreshSize();
				getImagePlaneManagerPanel1().setSourceDataInfo(null);
				refreshSourceDataInfo();
				getGeometrySubVolumePanel().setGeometry(getGeometry());
				getCurveRendererGeometry1().setGeometry(getGeometry());
				getJButtonChangeDomain().setEnabled(getGeometry() != null);
				surfaceViewer.setGeometry(ivjGeometry);
				resolvedLocationTablePanel.setGeometrySurfaceDescription(ivjGeometry.getGeometrySurfaceDescription());
				newValue.getGeometrySpec().addPropertyChangeListener(this);
			}
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
 * Comment
 */
private void showSizeDialog() {	
	getGeometrySizeDialog1().init(getGeometry());
	ZEnforcer.showModalDialogOnTop(getGeometrySizeDialog1(),this);
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


public void stateChanged(ChangeEvent e) {
	if (e.getSource() == tabbedPane) {
		if (tabbedPane.getSelectedComponent() == surfaceViewer) {
			try {
				AsynchClientTask surfaceGenerationTask = new AsynchClientTask ("creating new smoothed surface", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {		
					
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						getGeometry().precomputeAll(true);
					}
				};
				AsynchClientTask tasks[] = new AsynchClientTask[] {surfaceGenerationTask};
				ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), tasks);
			} catch (Exception e1) {
				DialogUtils.showErrorDialog(this, e1.getMessage());
				e1.printStackTrace();
			}
		}
	}
}

}
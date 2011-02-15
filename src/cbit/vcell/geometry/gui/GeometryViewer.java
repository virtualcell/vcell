package cbit.vcell.geometry.gui;
import java.awt.AWTEventMulticaster;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DownArrowIcon;
import org.vcell.util.gui.ZEnforcer;

import cbit.image.DisplayAdapterService;
import cbit.image.ImageException;
import cbit.image.ImagePaneModel;
import cbit.image.ImagePlaneManagerPanel;
import cbit.image.SourceDataInfo;
import cbit.image.VCImage;
import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.desktop.geometry.SurfaceViewerPanel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.document.GeometryOwner;
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
	private CurveRendererGeometry ivjCurveRendererGeometry1 = null;
	private GeometryFilamentCurveDialog ivjGeometryFilamentCurveDialog1 = null;
	private ImagePlaneManagerPanel ivjImagePlaneManagerPanel1 = null;
	private GeometrySizeDialog ivjGeometrySizeDialog1 = null;
	private javax.swing.JButton ivjJButtonChangeDomain = null;
	private SurfaceViewerPanel surfaceViewer = null;
	private JTabbedPane tabbedPane = null;
	private ResolvedLocationTablePanel resolvedLocationTablePanel = null;
	
	private JButton ivjJButtonReplace = null;
    protected transient ActionListener actionListener = null;
	private GeometryOwner geometryOwner = null;
	private JMenuItem newGeometryMenuItem = null;
	private JMenuItem existingGeometryMenuItem = null;
	private JPopupMenu popupMenu = null;

	public static final String REPLACE_GEOMETRY_SPATIAL_LABEL = "Select Different Geometry";
	public static final String REPLACE_GEOMETRY_NONSPATIAL_LABEL = "Add Geometry";
	private boolean bShowReplaceButton = true;

/**
 * Constructor
 */
public GeometryViewer() {
	this(true);
}

public GeometryViewer(boolean bShowReplaceButton) {
	super();
	this.bShowReplaceButton = bShowReplaceButton;
	initialize();
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	if (e.getSource() == getJButtonChangeDomain())
		this.showSizeDialog();
	else if (e.getSource() == newGeometryMenuItem || e.getSource() == existingGeometryMenuItem) {
		refireActionPerformed(e);
	} else if (e.getSource() == getJButtonReplace()) {
		getPopupMenu().show(getJButtonReplace(), getJButtonReplace().getWidth()/2, getJButtonReplace().getHeight());
	}
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	removePropertyChangeListener(listener);
	super.addPropertyChangeListener(listener);
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
			ivjJButtonChangeDomain = new javax.swing.JButton("Change domain");
			ivjJButtonChangeDomain.setName("JButtonChangeDomain");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButtonChangeDomain;
}

/**
 * Return the SizeLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getSizeLabel() {
	if (ivjSizeLabel == null) {
		try {
			ivjSizeLabel = new javax.swing.JLabel();
			ivjSizeLabel.setName("SizeLabel");
		} catch (java.lang.Throwable ivjExc) {
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
private void initConnections() throws java.lang.Exception {
	getJButtonChangeDomain().addActionListener(this);
	getImagePlaneManagerPanel1().setCurveRenderer(getCurveRendererGeometry1());
	tabbedPane.addChangeListener(this);
	getJButtonReplace().addActionListener(this);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		setName("ImageView3d");
		setLayout(new java.awt.GridBagLayout());
		//setSize(506, 564);
		
		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
		constraintsJLabel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel1.insets = new java.awt.Insets(2, 4, 0, 4);
		JLabel label = new javax.swing.JLabel("Domain:");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		add(label, constraintsJLabel1);

		java.awt.GridBagConstraints constraintsSizeLabel = new java.awt.GridBagConstraints();
		constraintsSizeLabel.gridx = 1; constraintsSizeLabel.gridy = 0;
		constraintsSizeLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsSizeLabel.weightx = 1.0;
		constraintsSizeLabel.insets = new java.awt.Insets(2, 4, 0, 4);
		add(getSizeLabel(), constraintsSizeLabel);

		java.awt.GridBagConstraints constraintsJButtonChangeDomain = new java.awt.GridBagConstraints();
		constraintsJButtonChangeDomain.gridx = 3; constraintsJButtonChangeDomain.gridy = 0;
		constraintsJButtonChangeDomain.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJButtonChangeDomain.insets = new java.awt.Insets(2, 4, 0, 4);
		add(getJButtonChangeDomain(), constraintsJButtonChangeDomain);
		
		if (bShowReplaceButton) {
			java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 4; gbc.gridy = 0;
			gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gbc.insets = new java.awt.Insets(2, 4, 0, 4);
			add(getJButtonReplace(), gbc);
		}

		java.awt.GridBagConstraints constraintsGeometrySubVolumePanel = new java.awt.GridBagConstraints();
		constraintsGeometrySubVolumePanel.gridx = 0; constraintsGeometrySubVolumePanel.gridy = 1;
		constraintsGeometrySubVolumePanel.gridwidth = 5;
		constraintsGeometrySubVolumePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsGeometrySubVolumePanel.insets = new java.awt.Insets(0, 4, 0, 4);
		constraintsGeometrySubVolumePanel.weighty = 0.15;
		add(getGeometrySubVolumePanel(), constraintsGeometrySubVolumePanel);
		
		resolvedLocationTablePanel = new ResolvedLocationTablePanel();
		surfaceViewer = new SurfaceViewerPanel();
		tabbedPane = new JTabbedPane();
		getImagePlaneManagerPanel1().setBorder(GuiConstants.TAB_PANEL_BORDER);
		surfaceViewer.setBorder(GuiConstants.TAB_PANEL_BORDER);
		resolvedLocationTablePanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		tabbedPane.add("Slice View", getImagePlaneManagerPanel1());
		tabbedPane.add("Surface View", surfaceViewer);
		tabbedPane.add("Geometric Region Details", resolvedLocationTablePanel);
		
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 2;
		gbc.gridwidth = 5;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(tabbedPane, gbc);
		
		initConnections();
		this.setColorMap();
		getImagePlaneManagerPanel1().enableDrawingTools(false);
		getImagePlaneManagerPanel1().setMode(this.meshmode());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
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
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == getGeometry().getGeometrySpec() && (evt.getPropertyName().equals("origin"))) {
		this.refreshSize();
	}
	if (evt.getSource() == getGeometry().getGeometrySpec() && (evt.getPropertyName().equals("extent"))) {
		this.refreshSize();
	}
	if (evt.getSource() == getGeometry().getGeometrySpec() && (evt.getPropertyName().equals("sampledImage"))){
		refreshSourceDataInfo();
	}
	if (evt.getSource() == geometryOwner && evt.getPropertyName().equals("geometry")) {
		onGeometryChange();
	}
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

	String text = dim+"D,";
	if (dim==0){
		text += " compartmental";
	}else{
		text += "  size=("+sizeX+(dim > 1?","+sizeY:"")+(dim > 2?","+sizeZ:"")+")"+
				", origin=("+originX+(dim>1?","+originY:"")+(dim>2?","+originZ:"")+")";
	}
	getSizeLabel().setText(text);
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
				surfaceViewer.setGeometry(ivjGeometry);
				resolvedLocationTablePanel.setGeometrySurfaceDescription(ivjGeometry.getGeometrySurfaceDescription());
				newValue.getGeometrySpec().addPropertyChangeListener(this);
			}
			getJButtonChangeDomain().setEnabled(ivjGeometry != null);
			firePropertyChange("geometry", oldValue, newValue);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}

/**
 * Comment
 */
private void showSizeDialog() {	
	getGeometrySizeDialog1().init(getGeometry());
	ZEnforcer.showModalDialogOnTop(getGeometrySizeDialog1(),this);
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

private javax.swing.JButton getJButtonReplace() {
	if (ivjJButtonReplace == null) {
		try {
			ivjJButtonReplace = new javax.swing.JButton(REPLACE_GEOMETRY_SPATIAL_LABEL);
			ivjJButtonReplace.setIcon(new DownArrowIcon());
			ivjJButtonReplace.setHorizontalTextPosition(SwingConstants.LEFT);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButtonReplace;
}

private JPopupMenu getPopupMenu() {
	if (popupMenu == null) {
		try {
			popupMenu = new JPopupMenu();
			newGeometryMenuItem = new JMenuItem("New...");
			newGeometryMenuItem.setActionCommand(GuiConstants.ACTIONCMD_CREATE_GEOMETRY);
			newGeometryMenuItem.addActionListener(this);
			existingGeometryMenuItem = new JMenuItem("Open...");
			existingGeometryMenuItem.setActionCommand(GuiConstants.ACTIONCMD_CHANGE_GEOMETRY);
			existingGeometryMenuItem.addActionListener(this);
			popupMenu.add(newGeometryMenuItem);
			popupMenu.add(existingGeometryMenuItem);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return popupMenu;
}


public synchronized void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
}


protected void fireActionPerformed(ActionEvent e) {
	if (actionListener != null) {
		actionListener.actionPerformed(e);
	}         
}

private void refireActionPerformed(ActionEvent e) {
	// relays an action event with this as the source
	fireActionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand(), e.getModifiers()));
}


public synchronized void removeActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.remove(actionListener, l);
}

public void setGeometryOwner(GeometryOwner newValue) {
	if (geometryOwner == newValue) {
		return;
	}
	GeometryOwner oldValue = geometryOwner;
	if (oldValue != null){
		oldValue.removePropertyChangeListener(this);
	}
	geometryOwner = newValue;	
	if (newValue != null ){
		newValue.addPropertyChangeListener(this);
	}	
	onGeometryChange();
}

public void onGeometryChange() {
	if (geometryOwner == null || geometryOwner.getGeometry() == null) {
		setGeometry(null);
	} else {
		setGeometry(geometryOwner.getGeometry());
		if (geometryOwner.getGeometry().getDimension() == 0){
			getJButtonChangeDomain().setVisible(false);
//			getGeometrySubVolumePanel().setVisible(false);
			tabbedPane.setVisible(false);
			getJButtonReplace().setText(REPLACE_GEOMETRY_NONSPATIAL_LABEL);
		} else {
//			getGeometrySubVolumePanel().setVisible(true);
			getJButtonReplace().setText(REPLACE_GEOMETRY_SPATIAL_LABEL);
			getJButtonChangeDomain().setVisible(true);
			tabbedPane.setVisible(true);
		}
	}
}

public GeometryOwner getGeometryOwner() {
	return geometryOwner; 
}
}
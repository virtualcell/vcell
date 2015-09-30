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
import java.awt.AWTEventMulticaster;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.vcell.util.ISize;
import org.vcell.util.NumberUtils;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DownArrowIcon;

import cbit.image.DisplayAdapterService;
import cbit.image.ImageException;
import cbit.image.SourceDataInfo;
import cbit.image.VCImage;
import cbit.image.gui.ImagePaneModel;
import cbit.image.gui.ImagePlaneManagerPanel;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager;
import cbit.vcell.client.desktop.geometry.SurfaceViewerPanel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ChooseFile;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.CommonTask;
import cbit.vcell.client.task.ExportDocument;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryOwner;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.surface.RayCaster;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mathmodel.MathModel;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class GeometryViewer extends DocumentEditorSubPanel implements ActionListener, java.beans.PropertyChangeListener, ChangeListener {
	private GeometrySubVolumePanel ivjGeometrySubVolumePanel = null;
	private javax.swing.JLabel ivjSizeLabel = null;
	private Geometry ivjGeometry = null;
	private CurveRendererGeometry ivjCurveRendererGeometry1 = null;
	private ImagePlaneManagerPanel ivjImagePlaneManagerPanel1 = null;
	private GeometrySizeDialog ivjGeometrySizeDialog1 = null;
	private javax.swing.JButton ivjJButtonChangeDomain = null;
	private SurfaceViewerPanel surfaceViewer = null;
	private JTabbedPane tabbedPane = null;
	private ResolvedLocationTablePanel resolvedLocationTablePanel = null;
	
	private JButton ivjJButtonReplace = null;
	private JButton resampleButton = null;
    protected transient ActionListener actionListener = null;
	private GeometryOwner geometryOwner = null;
	private JMenuItem newGeometryMenuItem = null;
	private JMenuItem existingGeometryMenuItem = null;
	private JPopupMenu popupMenu = null;

	public static final String REPLACE_GEOMETRY_SPATIAL_LABEL = "Replace Geometry";
	public static final String EDIT_IMAGE_SPATIAL_LABEL = "Edit Image";
	public static final String REPLACE_GEOMETRY_NONSPATIAL_LABEL = "Add Geometry";
	private boolean bShowReplaceButton = true;
	
	public static boolean debug_bShowResampleButton = false;

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
	if (e.getSource() == getJButtonChangeDomain()){
		this.showSizeDialog();
	}else if (e.getSource() == newGeometryMenuItem || e.getSource() == existingGeometryMenuItem || e.getSource() == getIvjButtonEditImage()) {
		refireActionPerformed(e);
	} else if (e.getSource() == getJButtonReplace()) {
		getPopupMenu().show(getJButtonReplace(), getJButtonReplace().getWidth()/2, getJButtonReplace().getHeight());
	} else if (e.getSource() == getResampleButton()) {
		try {
			resample();
		} catch (Exception e2) {
			e2.printStackTrace();
			DialogUtils.showErrorDialog(GeometryViewer.this, e2.getMessage(), e2);
		}
	}else if (e.getSource() == getJButtonExport()) {
		exportGeometry();
//		getPopupMenuExport().show(getJButtonExport(), getJButtonReplace().getWidth()/2, getJButtonReplace().getHeight());
	}
//	else if (e.getSource() == plyMenuItem || e.getSource() == stlMenuItem || e.getSource() == avsMenuItem) {
//		exportGeometry(e.getActionCommand());
//	}
}

private void resample(){
	AsynchClientTask precomputeCurrentGeometryTask = new AsynchClientTask("computing surfaces for existing geometry",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if (getGeometry().getGeometrySurfaceDescription().getRegionImage()==null){
				getGeometry().precomputeAll(new GeometryThumbnailImageFactoryAWT(),true,true);
			}
		}
	};
	AsynchClientTask sampleSizeTask = new AsynchClientTask("specify sample size",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			VCImage currSampledImage = getGeometry().getGeometrySpec().getSampledImage().getCurrentValue();
			int currNumSamples = currSampledImage.getNumXYZ();
			
			ISize currSize = new ISize(currSampledImage.getNumX(),currSampledImage.getNumY(),currSampledImage.getNumZ());
			ISize sampleSize_times_ten = GeometrySpec.calulateResetSamplingSize(3, getGeometry().getExtent(), currNumSamples*10);
			ISize sampleSize_div_ten = GeometrySpec.calulateResetSamplingSize(3, getGeometry().getExtent(), currNumSamples/10);
			ISize sampleSize_1e2 = GeometrySpec.calulateResetSamplingSize(3, getGeometry().getExtent(), (int)1e2);
			ISize sampleSize_1e3 = GeometrySpec.calulateResetSamplingSize(3, getGeometry().getExtent(), (int)1e3);
			ISize sampleSize_1e4 = GeometrySpec.calulateResetSamplingSize(3, getGeometry().getExtent(), (int)1e4);
			ISize sampleSize_1e5 = GeometrySpec.calulateResetSamplingSize(3, getGeometry().getExtent(), (int)1e5);
			ISize sampleSize_1e6 = GeometrySpec.calulateResetSamplingSize(3, getGeometry().getExtent(), (int)1e6);
			ISize sampleSize_1e7 = GeometrySpec.calulateResetSamplingSize(3, getGeometry().getExtent(), (int)1e7);
			ISize sampleSize_1e8 = GeometrySpec.calulateResetSamplingSize(3, getGeometry().getExtent(), (int)1e8);
			
			String[] columnNames = new String[] { "description","num samples", "(X,Y,Z)" };
			Object[][] rowData = new Object[][] { 
					new Object[] { "same size",				new Integer(currSize.getXYZ()),				currSize },
					new Object[] { "10 times smaller",		new Integer(sampleSize_div_ten.getXYZ()),	sampleSize_div_ten },
					new Object[] { "10 times bigger",		new Integer(sampleSize_times_ten.getXYZ()),	sampleSize_times_ten },
					new Object[] { "        100 samples",	new Integer(sampleSize_1e2.getXYZ()),		sampleSize_1e2 },
					new Object[] { "      1,000 samples",	new Integer(sampleSize_1e3.getXYZ()),		sampleSize_1e3 },
					new Object[] { "     10,000 samples",	new Integer(sampleSize_1e4.getXYZ()),		sampleSize_1e4 },
					new Object[] { "    100,000 samples",	new Integer(sampleSize_1e5.getXYZ()),		sampleSize_1e5 },
					new Object[] { "  1,000,000 samples",	new Integer(sampleSize_1e6.getXYZ()),		sampleSize_1e6 },
					new Object[] { " 10,000,000 samples",	new Integer(sampleSize_1e7.getXYZ()),		sampleSize_1e7 },
					new Object[] { "100,000,000 samples",	new Integer(sampleSize_1e8.getXYZ()),		sampleSize_1e8 },
			};
			
			int[] selectedRows = DialogUtils.showComponentOKCancelTableList(GeometryViewer.this, "sample size", columnNames, rowData, ListSelectionModel.SINGLE_SELECTION);
			ISize sampleSize = (ISize)rowData[selectedRows[0]][2];
			hashTable.put("sampleSize", sampleSize);
		}
	};
	AsynchClientTask resampleTask = new AsynchClientTask("resample geometry",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			ISize sampleSize = (ISize)hashTable.get("sampleSize");
			Geometry newGeometry = RayCaster.resampleGeometry(new GeometryThumbnailImageFactoryAWT(), getGeometry(), sampleSize);
			hashTable.put("newGeometry", newGeometry);
		}
	};
	AsynchClientTask setGeometryTask = new AsynchClientTask("loading geometry",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			Geometry newGeometry = (Geometry)hashTable.get("newGeometry");
			if (getGeometryOwner() instanceof SimulationContext){
				((SimulationContext)getGeometryOwner()).setGeometry(newGeometry);
			}else if (getGeometryOwner() instanceof MathModel){
				((MathModel)getGeometryOwner()).getMathDescription().setGeometry(newGeometry);
			}else{
				throw new RuntimeException("unexpected geometry owner, could not set resampled geometry");
			}
		}
	};
	ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[]{ precomputeCurrentGeometryTask, sampleSizeTask, resampleTask, setGeometryTask },false);
}


private void exportGeometry(/*String exportType*/){
	Objects.requireNonNull(documentWindowManager);
	AsynchClientTask computeSurface = new AsynchClientTask("computing Surface...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if(getGeometry().getGeometrySurfaceDescription() == null || getGeometry().getGeometrySurfaceDescription().getSurfaceCollection() == null){
				getGeometry().getGeometrySurfaceDescription().updateAll();
			}
		}
	};
	AsynchClientTask[] tasks = new AsynchClientTask[] {
		new ChooseFile(),
		computeSurface,
		new ExportDocument()
	};

	Hashtable<String, Object> hash = new Hashtable<String, Object>();
	hash.put("documentToExport",getGeometry());
	hash.put("userPreferences",documentWindowManager.getUserPreferences());
	hash.put(CommonTask.DOCUMENT_MANAGER.name,documentWindowManager.getRequestManager().getDocumentManager());
	hash.put("component", this);
	ClientTaskDispatcher.dispatch(this, hash, tasks, false);
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
			ivjJButtonChangeDomain = new javax.swing.JButton("Edit Domain...");
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
	getResampleButton().addActionListener(this);
	getJButtonReplace().addActionListener(this);
	getJButtonExport().addActionListener(this);
	getIvjButtonEditImage().addActionListener(this);
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
		
		JPanel topHeader = new JPanel();
		topHeader.setLayout(new java.awt.GridBagLayout());
		java.awt.GridBagConstraints topH1 = new java.awt.GridBagConstraints();
		topH1.weightx = 1.0;
		topH1.gridx = 0; topH1.gridy = 0;
		topH1.gridwidth = 1;
		topH1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		topH1.insets = new java.awt.Insets(2, 0, 2, 0);
		add(topHeader, topH1);

		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
		constraintsJLabel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel1.insets = new Insets(0, 2, 0, 5);
		JLabel label = new javax.swing.JLabel("Domain:");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		topHeader.add(label, constraintsJLabel1);

		java.awt.GridBagConstraints constraintsSizeLabel = new java.awt.GridBagConstraints();
		constraintsSizeLabel.anchor = GridBagConstraints.WEST;
		constraintsSizeLabel.gridx = 1; constraintsSizeLabel.gridy = 0;
		constraintsSizeLabel.insets = new Insets(0, 2, 0, 5);
		topHeader.add(getSizeLabel(), constraintsSizeLabel);

		
		java.awt.GridBagConstraints constraintsJButtonExport = new java.awt.GridBagConstraints();
		constraintsJButtonExport.weightx = 1.0;
		constraintsJButtonExport.anchor = GridBagConstraints.WEST;
		constraintsJButtonExport.gridx = 2; constraintsJButtonExport.gridy = 0;
		constraintsJButtonExport.insets = new Insets(0, 2, 0, 5);
		topHeader.add(getJButtonChangeDomain(), constraintsJButtonExport);
		
		java.awt.GridBagConstraints constraintsJButtonChangeDomain = new java.awt.GridBagConstraints();
		constraintsJButtonChangeDomain.gridx = 3; constraintsJButtonChangeDomain.gridy = 0;
		constraintsJButtonChangeDomain.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJButtonChangeDomain.insets = new Insets(0, 2, 0, 5);
		topHeader.add(getJButtonExport(), constraintsJButtonChangeDomain);
		GridBagConstraints gbc_ivjButtonEditImage = new GridBagConstraints();
		gbc_ivjButtonEditImage.insets = new Insets(0, 0, 0, 5);
		gbc_ivjButtonEditImage.gridx = 4;
		gbc_ivjButtonEditImage.gridy = 0;
		topHeader.add(getIvjButtonEditImage(), gbc_ivjButtonEditImage);
		
		java.awt.GridBagConstraints gbcr = new java.awt.GridBagConstraints();
		gbcr.gridx = 5; gbcr.gridy = 0;
		gbcr.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbcr.insets = new java.awt.Insets(0, 0, 0, 2);
		topHeader.add(getJButtonReplace(), gbcr);
		getJButtonReplace().setVisible(bShowReplaceButton);

		java.awt.GridBagConstraints gbcr2 = new java.awt.GridBagConstraints();
		gbcr2.gridx = 5; gbcr2.gridy = 0;
		gbcr2.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbcr2.insets = new java.awt.Insets(0, 0, 0, 2);
		if (debug_bShowResampleButton){
			topHeader.add(getResampleButton(), gbcr2);
		}

		java.awt.GridBagConstraints constraintsGeometrySubVolumePanel = new java.awt.GridBagConstraints();
		constraintsGeometrySubVolumePanel.weightx = 1.0;
		constraintsGeometrySubVolumePanel.gridx = 0; constraintsGeometrySubVolumePanel.gridy = 1;
		constraintsGeometrySubVolumePanel.gridwidth = 1;
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
		gbc.gridwidth = 1;
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
	if (getGeometry() == null) {
		return;
	}
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
		onGeometryOwnerGeometryChange();
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

	final String PREFIX = dim+"D,";;
	String text = PREFIX;
	String labelText = null;
	if (dim==0){
		text += " compartmental";
		labelText = new String(text);
	}else{
		text += "  size=("+sizeX+(dim > 1?","+sizeY:"")+(dim > 2?","+sizeZ:"")+")"+
				", origin=("+originX+(dim>1?","+originY:"")+(dim>2?","+originZ:"")+")";
		if(text.length() > 50){
			final int NUMDIGITS = 6;
			labelText = PREFIX +
				"  size=("+NumberUtils.formatNumber(sizeX, NUMDIGITS)+(dim > 1?","+NumberUtils.formatNumber(sizeY, NUMDIGITS):"")+(dim > 2?","+NumberUtils.formatNumber(sizeZ, NUMDIGITS):"")+")"+
				", origin=("+NumberUtils.formatNumber(originX, NUMDIGITS)+(dim>1?","+NumberUtils.formatNumber(originY, NUMDIGITS):"")+(dim>2?","+NumberUtils.formatNumber(originZ, NUMDIGITS):"")+")";
		}else{
			labelText = text;
		}
	}
	getSizeLabel().setText(labelText);
	getSizeLabel().setToolTipText(text);
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
			if (newValue != null) {
				newValue.getGeometrySpec().addPropertyChangeListener(this);
			}
			ivjGeometry = newValue;
			if (ivjGeometry != null) {
				refreshSize();
				getImagePlaneManagerPanel1().setSourceDataInfo(null);
				refreshSourceDataInfo();
				getGeometrySubVolumePanel().setGeometry(ivjGeometry);
				getCurveRendererGeometry1().setGeometry(ivjGeometry);
				surfaceViewer.setGeometry(ivjGeometry);
				resolvedLocationTablePanel.setGeometrySurfaceDescription(ivjGeometry.getGeometrySurfaceDescription());
				if (ivjGeometry.getDimension() == 0){
					getJButtonChangeDomain().setEnabled(false);
					tabbedPane.setVisible(false);
					getJButtonReplace().setText(REPLACE_GEOMETRY_NONSPATIAL_LABEL);
					getJButtonExport().setEnabled(false);
					getIvjButtonEditImage().setEnabled(false);//compartmental
				} else {
					getJButtonReplace().setText(REPLACE_GEOMETRY_SPATIAL_LABEL);
					getJButtonChangeDomain().setEnabled(true);
					getJButtonExport().setEnabled(documentWindowManager != null);
					getIvjButtonEditImage().setEnabled(true);
					tabbedPane.setVisible(true);
				}
				if (getGeometryOwner() instanceof SimulationContext){
					SimulationContext sc = (SimulationContext)getGeometryOwner();
					boolean replaceEnabled = false;
					switch (sc.getApplicationType()) {
					case RULE_BASED_STOCHASTIC:
						replaceEnabled = false;
						break;
					case NETWORK_DETERMINISTIC:
					case NETWORK_STOCHASTIC:
						replaceEnabled = true;
						break;
					}
					getJButtonReplace().setEnabled(replaceEnabled);
				}
				updateSurfaceView();
			}
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
	DialogUtils.showModalJDialogOnTop(getGeometrySizeDialog1(),this);
}

public void stateChanged(ChangeEvent e) {
	if (e.getSource() == tabbedPane) {		
		updateSurfaceView();
	}
}

private void updateSurfaceView() {
	if (tabbedPane.getSelectedComponent() == surfaceViewer) {	
		AsynchClientTask surfaceGenerationTask = new AsynchClientTask ("creating new smoothed surface", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {		
			
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				getGeometry().precomputeAll(new GeometryThumbnailImageFactoryAWT(),true, true);
			}
		};
		AsynchClientTask tasks[] = new AsynchClientTask[] {surfaceGenerationTask};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), tasks);
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

private javax.swing.JButton getResampleButton() {
	if (resampleButton == null) {
		try {
			resampleButton = new javax.swing.JButton("resample");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return resampleButton;
}

private JButton ivjJButtonExport;
private javax.swing.JButton getJButtonExport() {
	if (ivjJButtonExport == null) {
		try {
			ivjJButtonExport = new javax.swing.JButton("Export...");
//			ivjJButtonExport.setIcon(new DownArrowIcon());
//			ivjJButtonExport.setHorizontalTextPosition(SwingConstants.LEFT);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButtonExport;
}

private JPopupMenu getPopupMenu() {
	if (popupMenu == null) {
		try {
			popupMenu = new JPopupMenu();
			newGeometryMenuItem = new JMenuItem("New...");
			newGeometryMenuItem.setActionCommand(GuiConstants.ACTIONCMD_CREATE_GEOMETRY);
			newGeometryMenuItem.addActionListener(this);
			existingGeometryMenuItem = new JMenuItem("Open from...");
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

//private JMenuItem plyMenuItem;
//private JMenuItem stlMenuItem;
//private JMenuItem avsMenuItem;
//private JPopupMenu getPopupMenuExport() {
//	if (popupMenu == null) {
//		try {
//			popupMenu = new JPopupMenu();
//			plyMenuItem = new JMenuItem(FileFilters.FILE_FILTER_PLY.getDescription());
//			plyMenuItem.setActionCommand("ply");
//			plyMenuItem.addActionListener(this);
//			popupMenu.add(plyMenuItem);
//			
//			stlMenuItem = new JMenuItem(FileFilters.FILE_FILTER_STL.getDescription());
//			stlMenuItem.setActionCommand("stl");
//			stlMenuItem.addActionListener(this);
//			popupMenu.add(stlMenuItem);
//
//			avsMenuItem = new JMenuItem(FileFilters.FILE_FILTER_AVS.getDescription());
//			avsMenuItem.setActionCommand("avs");
//			avsMenuItem.addActionListener(this);
//			popupMenu.add(avsMenuItem);
//		} catch (java.lang.Throwable ivjExc) {
//			handleException(ivjExc);
//		}
//	}
//	return popupMenu;
//}

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

private DocumentWindowManager documentWindowManager;
private JButton ivjButtonEditImage;
public void setGeometryOwner(GeometryOwner newValue,DocumentWindowManager documentWindowManager) {
	this.documentWindowManager = documentWindowManager;
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
	onGeometryOwnerGeometryChange();
}

public void onGeometryOwnerGeometryChange() {
	if (geometryOwner == null || geometryOwner.getGeometry() == null) {
		setGeometry(null);
	} else {
		setGeometry(geometryOwner.getGeometry());
	}
}

public GeometryOwner getGeometryOwner() {
	return geometryOwner; 
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
}

@Override
public void setIssueManager(IssueManager issueManager) {
	super.setIssueManager(issueManager);
	ivjGeometrySubVolumePanel.setIssueManager(issueManager);
}

@Override
public void setSelectionManager(SelectionManager selectionManager) {
	super.setSelectionManager(selectionManager);
	ivjGeometrySubVolumePanel.setSelectionManager(selectionManager);
}
	private JButton getIvjButtonEditImage() {
		if (ivjButtonEditImage == null) {
			ivjButtonEditImage = new JButton(EDIT_IMAGE_SPATIAL_LABEL);
			ivjButtonEditImage.setActionCommand(GuiConstants.ACTIONCMD_EDITCURRENTSPATIAL_GEOMETRY);
		}
		return ivjButtonEditImage;
	}
}

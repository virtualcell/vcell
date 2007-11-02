package cbit.vcell.geometry.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.Origin;
import cbit.util.Extent;
import java.awt.image.MemoryImageSource;
import cbit.vcell.geometry.*;
import cbit.image.*;
/**
 * Insert the type's description here.
 * Creation date: (4/9/01 8:06:53 AM)
 * @author: Jim Schaff
 */
public class GeometrySummaryPanel extends javax.swing.JPanel {
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.ListCellRenderer ivjcellRenderer1 = null;
	private boolean ivjConnPtoP2Aligning = false;
	private javax.swing.JLabel ivjJLabelExtent = null;
	private javax.swing.JLabel ivjJLabelExtentTitle = null;
	private javax.swing.JLabel ivjJLabelOrigin = null;
	private javax.swing.JLabel ivjJLabelOriginTitle = null;
	private javax.swing.JList ivjJList1 = null;
	private cbit.vcell.geometry.Geometry fieldGeometry = null;
	private boolean ivjConnPtoP3Aligning = false;
	private Geometry ivjgeometry1 = null;
	private cbit.gui.DefaultListModelCivilized ivjDefaultListModelCivilized = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjGeometryVersionLabel = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private GeometrySpec ivjGeometrySpec = null;
	private ImagePlaneManagerPanel ivjImagePlaneManagerPanel1 = null;
	private javax.swing.JPanel ivjJPanelOrigin = null;
	private javax.swing.JPanel ivjJPanelSize = null;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == GeometrySummaryPanel.this.getJList1() && (evt.getPropertyName().equals("cellRenderer"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == GeometrySummaryPanel.this && (evt.getPropertyName().equals("geometry"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == GeometrySummaryPanel.this.getGeometrySpec() && (evt.getPropertyName().equals("extent"))) 
				connEtoM4(evt);
			if (evt.getSource() == GeometrySummaryPanel.this.getGeometrySpec() && (evt.getPropertyName().equals("origin"))) 
				connEtoM6(evt);
		};
	};

/**
 * GeometrySummaryPanel constructor comment.
 */
public GeometrySummaryPanel() {
	super();
	initialize();
}

/**
 * connEtoC1:  (geometry1.this --> GeometrySummaryPanel.initGeometry(Lcbit.vcell.geometry.Geometry;)V)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		this.initGeometry(getgeometry1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (GeometrySummaryPanel.initialize() --> ImagePlaneManagerPanel1.displayAdapterServicePanelVisible)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getImagePlaneManagerPanel1().setDisplayAdapterServicePanelVisible(false);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM10:  (geometry1.this --> GeometrySpec.this)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		if ((getgeometry1() != null)) {
			setGeometrySpec(getgeometry1().getGeometrySpec());
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
 * connEtoM2:  (GeometrySummaryPanel.initialize() --> ImagePlaneManagerPanel1.enableDrawingTools(Z)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
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
 * connEtoM3:  (geometry1.this --> JLabelExtent.text)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		getJLabelExtent().setText(this.getExtentString());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM4:  (geometry1.extent --> JLabelExtent.text)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJLabelExtent().setText(this.getExtentString());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM5:  (geometry1.this --> JLabelOrigin.text)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		getJLabelOrigin().setText(this.getOriginString());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM6:  (geometry1.origin --> JLabelOrigin.text)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJLabelOrigin().setText(this.getOriginString());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM7:  (GeometrySpec.this --> DefaultListModelCivilized.contents)
 * @param value cbit.vcell.geometry.GeometrySpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(cbit.vcell.geometry.GeometrySpec value) {
	try {
		// user code begin {1}
		// user code end
		if ((getGeometrySpec() != null)) {
			getDefaultListModelCivilized().setContents(getGeometrySpec().getSubVolumes());
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
 * connEtoM8:  (geometry1.this --> cellRenderer1.this)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		setcellRenderer1(this.createListCellRenderer());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM9:  (geometry1.this --> GeometryVersionLabel.text)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		getGeometryVersionLabel().setText(this.getGeometryVersionString(getgeometry1()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (JList1.model <--> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getJList1().setModel(getDefaultListModelCivilized());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetSource:  (JList1.cellRenderer <--> cellRenderer1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getcellRenderer1() != null)) {
				getJList1().setCellRenderer(getcellRenderer1());
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
 * connPtoP2SetTarget:  (JList1.cellRenderer <--> cellRenderer1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setcellRenderer1(getJList1().getCellRenderer());
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
 * connPtoP3SetSource:  (GeometrySummaryPanel.geometry <--> geometry1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getgeometry1() != null)) {
				this.setGeometry(getgeometry1());
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
 * connPtoP3SetTarget:  (GeometrySummaryPanel.geometry <--> geometry1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setgeometry1(this.getGeometry());
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
 * Comment
 */
public javax.swing.Icon createIcon(VCImage vcImage) throws cbit.image.GifParsingException, ImageException {
	if (vcImage==null){
		return null;
	}else{
		GIFImage gifImage = new cbit.image.GIFImage(cbit.image.BrowseImage.gifFromVCImage(vcImage));
		return new javax.swing.ImageIcon(gifImage.getJavaImage(),"preview of Geometry");
	}
}


/**
 * Comment
 */
public javax.swing.Icon createIcon(VCImage sampledImage, Geometry geom) throws cbit.image.GifParsingException, ImageException {
	if (sampledImage==null){
		return null;
	}else{
		VCImage sampledGeometryHandles = null;
		if (geom.getDimension() > 0) {
			//project and scale geometry
			int SAMPLED_GEOM_SIZE = 150;
			sampledGeometryHandles = cbit.image.DitherIndexImage.dither(sampledImage, SAMPLED_GEOM_SIZE, SAMPLED_GEOM_SIZE);
			if (sampledGeometryHandles.getNumPixelClasses()!=sampledImage.getNumPixelClasses()){
				throw new RuntimeException("DitherIndexImage.dither()-->image.numPixelClasses = "+sampledGeometryHandles.getNumPixelClasses()+", and Geometry.getSampledImage()-->image.numPixelClasses = "+sampledImage.getNumPixelClasses());
			}
		} else {
			//Make bogus box for compartmental geometries
			int COMPARTMENT_SIZE_X = 10;
			int COMPARTMENT_SIZE_Y = 10;
			int compartmentalXY = COMPARTMENT_SIZE_X * COMPARTMENT_SIZE_Y;
			byte[] compartmentalPixels = new byte[compartmentalXY];
			for (int c = 0; c < compartmentalXY; c += 1) {
				compartmentalPixels[c] = ((byte[]) (sampledImage.getPixels()))[0];
			}
			sampledGeometryHandles = new cbit.image.VCImageUncompressed(null, compartmentalPixels, new cbit.util.Extent(1, 1, 1), COMPARTMENT_SIZE_X, COMPARTMENT_SIZE_Y, 1);
		}
		MemoryImageSource mis = new MemoryImageSource(	sampledGeometryHandles.getNumX(), 
														sampledGeometryHandles.getNumY(), 
														geom.getGeometrySpec().getHandleColorMap(), 
														sampledGeometryHandles.getPixels(), 
														0, sampledGeometryHandles.getNumX());
		java.awt.Image sampledGeometryImage = java.awt.Toolkit.getDefaultToolkit().createImage(mis);

		return new javax.swing.ImageIcon(sampledGeometryImage,"preview of Geometry");
	}
}


/**
 * Comment
 */
private javax.swing.ListCellRenderer createListCellRenderer() {
	javax.swing.DefaultListCellRenderer cellRenderer = null;
	
	cellRenderer = new javax.swing.DefaultListCellRenderer() {
		public java.awt.Component getListCellRendererComponent(javax.swing.JList list,Object value,int index,boolean isSelected,boolean cellHasFocus){

			setComponentOrientation(list.getComponentOrientation());
			setFont(list.getFont());
			
		    setBackground(list.getBackground());
		    setForeground(list.getForeground());
			setBorder(noFocusBorder);

			if (value instanceof SubVolume){
				SubVolume subVolume = (SubVolume)value;
				
				setText(subVolume.getName());
				int colorInt = getGeometry().getGeometrySpec().getHandleColorMap().getRGB(subVolume.getHandle());
				java.awt.Color handleColor = new java.awt.Color(colorInt);
				setIcon(new ColorIcon(20,20,handleColor));
			}else{
			    setText((value == null) ? "" : value.toString());
			}


			return this;
		}
	};
	return cellRenderer;
}


/**
 * Return the cellRenderer1 property value.
 * @return javax.swing.ListCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ListCellRenderer getcellRenderer1() {
	// user code begin {1}
	// user code end
	return ivjcellRenderer1;
}


/**
 * Return the DefaultListModelCivilized property value.
 * @return cbit.gui.DefaultListModelCivilized
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.DefaultListModelCivilized getDefaultListModelCivilized() {
	if (ivjDefaultListModelCivilized == null) {
		try {
			ivjDefaultListModelCivilized = new cbit.gui.DefaultListModelCivilized();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultListModelCivilized;
}


/**
 * Comment
 */
public java.lang.String getExtentString() {
	if (getGeometry()==null){
		return " ";
	}
	Extent extent = getGeometry().getExtent();
	switch (getGeometry().getDimension()){
		case 0: {
			return " ";
		}
		case 1: {
			return Double.toString(extent.getX());
		}
		case 2: {
			return "("+extent.getX()+","+extent.getY()+")";
		}
		case 3: {
			return "("+extent.getX()+","+extent.getY()+","+extent.getZ()+")";
		}
	}
	return " ";
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
 * Return the GeometryVersionLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getGeometryVersionLabel() {
	if (ivjGeometryVersionLabel == null) {
		try {
			ivjGeometryVersionLabel = new javax.swing.JLabel();
			ivjGeometryVersionLabel.setName("GeometryVersionLabel");
			ivjGeometryVersionLabel.setBorder(new cbit.gui.LineBorderBean());
			ivjGeometryVersionLabel.setText("GeometryName");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGeometryVersionLabel;
}

/**
 * Comment
 */
public java.lang.String getGeometryVersionString(Geometry geometry) {
	if (geometry == null){
		return "";
	}else if (geometry.getDimension() == 0){
		return "Compartmental";
	}else if (geometry.getVersion()==null){
		return "Unsaved";
	}else{
		cbit.sql.Version version = geometry.getVersion();
		return version.getName()+" ("+version.getDate()+")";
	}
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
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("SubDomains");
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
			ivjJLabel2.setText("Geometry: ");
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
 * Return the JLabelExtent property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelExtent() {
	if (ivjJLabelExtent == null) {
		try {
			ivjJLabelExtent = new javax.swing.JLabel();
			ivjJLabelExtent.setName("JLabelExtent");
			ivjJLabelExtent.setText(" ");
			ivjJLabelExtent.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjJLabelExtent.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelExtent;
}

/**
 * Return the JLabelExtentTitle property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelExtentTitle() {
	if (ivjJLabelExtentTitle == null) {
		try {
			ivjJLabelExtentTitle = new javax.swing.JLabel();
			ivjJLabelExtentTitle.setName("JLabelExtentTitle");
			ivjJLabelExtentTitle.setText("Size (µm)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelExtentTitle;
}

/**
 * Return the JLabelOrigin property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOrigin() {
	if (ivjJLabelOrigin == null) {
		try {
			ivjJLabelOrigin = new javax.swing.JLabel();
			ivjJLabelOrigin.setName("JLabelOrigin");
			ivjJLabelOrigin.setText(" ");
			ivjJLabelOrigin.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjJLabelOrigin.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOrigin;
}

/**
 * Return the JLabelOriginTitle property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOriginTitle() {
	if (ivjJLabelOriginTitle == null) {
		try {
			ivjJLabelOriginTitle = new javax.swing.JLabel();
			ivjJLabelOriginTitle.setName("JLabelOriginTitle");
			ivjJLabelOriginTitle.setText("Origin (µm)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOriginTitle;
}

/**
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJList1() {
	if (ivjJList1 == null) {
		try {
			ivjJList1 = new javax.swing.JList();
			ivjJList1.setName("JList1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJList1;
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

			java.awt.GridBagConstraints constraintsJList1 = new java.awt.GridBagConstraints();
			constraintsJList1.gridx = 0; constraintsJList1.gridy = 1;
			constraintsJList1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJList1.weightx = 1.0;
			constraintsJList1.weighty = 1.0;
			constraintsJList1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJList1(), constraintsJList1);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsJPanelSize = new java.awt.GridBagConstraints();
			constraintsJPanelSize.gridx = 0; constraintsJPanelSize.gridy = 2;
			constraintsJPanelSize.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanelSize.weightx = 1.0;
			getJPanel1().add(getJPanelSize(), constraintsJPanelSize);

			java.awt.GridBagConstraints constraintsJPanelOrigin = new java.awt.GridBagConstraints();
			constraintsJPanelOrigin.gridx = 0; constraintsJPanelOrigin.gridy = 3;
			constraintsJPanelOrigin.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanelOrigin.weightx = 1.0;
			getJPanel1().add(getJPanelOrigin(), constraintsJPanelOrigin);
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

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 0;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsGeometryVersionLabel = new java.awt.GridBagConstraints();
			constraintsGeometryVersionLabel.gridx = 1; constraintsGeometryVersionLabel.gridy = 0;
			constraintsGeometryVersionLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsGeometryVersionLabel.weightx = 1.0;
			constraintsGeometryVersionLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getGeometryVersionLabel(), constraintsGeometryVersionLabel);
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
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelOrigin() {
	if (ivjJPanelOrigin == null) {
		try {
			ivjJPanelOrigin = new javax.swing.JPanel();
			ivjJPanelOrigin.setName("JPanelOrigin");
			ivjJPanelOrigin.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelOriginTitle = new java.awt.GridBagConstraints();
			constraintsJLabelOriginTitle.gridx = 0; constraintsJLabelOriginTitle.gridy = 0;
			constraintsJLabelOriginTitle.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelOrigin().add(getJLabelOriginTitle(), constraintsJLabelOriginTitle);

			java.awt.GridBagConstraints constraintsJLabelOrigin = new java.awt.GridBagConstraints();
			constraintsJLabelOrigin.gridx = 1; constraintsJLabelOrigin.gridy = 0;
			constraintsJLabelOrigin.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelOrigin.weightx = 1.0;
			constraintsJLabelOrigin.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelOrigin().add(getJLabelOrigin(), constraintsJLabelOrigin);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelOrigin;
}

/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelSize() {
	if (ivjJPanelSize == null) {
		try {
			ivjJPanelSize = new javax.swing.JPanel();
			ivjJPanelSize.setName("JPanelSize");
			ivjJPanelSize.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelExtentTitle = new java.awt.GridBagConstraints();
			constraintsJLabelExtentTitle.gridx = 0; constraintsJLabelExtentTitle.gridy = 0;
			constraintsJLabelExtentTitle.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSize().add(getJLabelExtentTitle(), constraintsJLabelExtentTitle);

			java.awt.GridBagConstraints constraintsJLabelExtent = new java.awt.GridBagConstraints();
			constraintsJLabelExtent.gridx = 1; constraintsJLabelExtent.gridy = 0;
			constraintsJLabelExtent.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelExtent.weightx = 1.0;
			constraintsJLabelExtent.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSize().add(getJLabelExtent(), constraintsJLabelExtent);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelSize;
}

/**
 * Comment
 */
public java.lang.String getOriginString() {
	if (getGeometry()==null){
		return " ";
	}
	Origin origin = getGeometry().getOrigin();
	switch (getGeometry().getDimension()){
		case 0: {
			return " ";
		}
		case 1: {
			return Double.toString(origin.getX());
		}
		case 2: {
			return "("+origin.getX()+","+origin.getY()+")";
		}
		case 3: {
			return "("+origin.getX()+","+origin.getY()+","+origin.getZ()+")";
		}
	}
	return " ";
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION in GeometrySummaryPanel ---------");
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
	getJList1().addPropertyChangeListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
}

/**
 * Comment
 */
private void initGeometry(cbit.vcell.geometry.Geometry arg1) {
	boolean bSpatial = getGeometry() != null && getGeometry().getDimension() > 0;
	getImagePlaneManagerPanel1().setVisible(bSpatial);
	getJPanelOrigin().setVisible(bSpatial);
	getJPanelSize().setVisible(bSpatial);
	
	if(getGeometry() != null){
		try{
			cbit.image.VCImage vcImage = getGeometry().getGeometrySpec().getSampledImage();
			byte[] pixels = vcImage.getPixels();
			
			cbit.image.DisplayAdapterService das = new cbit.image.DisplayAdapterService();
			das.setActiveScaleRange(new cbit.util.Range(0, 255));
			das.setValueDomain(new cbit.util.Range(0, 255));
			das.addColorModelForValues(cbit.image.DisplayAdapterService.createContrastColorModel(), cbit.image.DisplayAdapterService.createGraySpecialColors(), "Contrast");
			das.setActiveColorModelID("Contrast");
			int[] rgb = new int[pixels.length];
			for(int i=0;i<rgb.length;i+= 1){
				rgb[i] = das.getColorFromIndex(pixels[i]);
			}
		
			cbit.image.SourceDataInfo sdi =
				new cbit.image.SourceDataInfo(
					cbit.image.SourceDataInfo.INT_RGB_TYPE,
					rgb,
					getGeometry().getExtent(),
					getGeometry().getOrigin(),
					new cbit.util.Range(0,255),
					0,
					vcImage.getNumX(),1,
					vcImage.getNumY(),vcImage.getNumX(),
					vcImage.getNumZ(),vcImage.getNumX()*vcImage.getNumY()
				);
			getImagePlaneManagerPanel1().setSourceDataInfo(sdi);
		}catch(Exception e){
			cbit.vcell.client.PopupGenerator.showErrorDialog(e.getMessage());
		}
	}else{
		getImagePlaneManagerPanel1().setSourceDataInfo(null);
	}
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GeometrySummaryPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(821, 451);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 1;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weighty = 1.0;
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 0;
		constraintsJPanel2.gridwidth = 2;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanel2.weightx = 1.0;
		add(getJPanel2(), constraintsJPanel2);

		java.awt.GridBagConstraints constraintsImagePlaneManagerPanel1 = new java.awt.GridBagConstraints();
		constraintsImagePlaneManagerPanel1.gridx = 1; constraintsImagePlaneManagerPanel1.gridy = 1;
		constraintsImagePlaneManagerPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsImagePlaneManagerPanel1.weightx = 1.0;
		constraintsImagePlaneManagerPanel1.weighty = 1.0;
		constraintsImagePlaneManagerPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getImagePlaneManagerPanel1(), constraintsImagePlaneManagerPanel1);
		initConnections();
		connPtoP1SetTarget();
		connEtoM1();
		connEtoM2();
		getImagePlaneManagerPanel1().setMode(ImagePaneModel.MESH_MODE);
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
		GeometrySummaryPanel aGeometrySummaryPanel;
		aGeometrySummaryPanel = new GeometrySummaryPanel();
		frame.setContentPane(aGeometrySummaryPanel);
		frame.setSize(aGeometrySummaryPanel.getSize());
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
 * Set the cellRenderer1 to a new value.
 * @param newValue javax.swing.ListCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setcellRenderer1(javax.swing.ListCellRenderer newValue) {
	if (ivjcellRenderer1 != newValue) {
		try {
			ivjcellRenderer1 = newValue;
			connPtoP2SetSource();
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
			connPtoP3SetSource();
			connEtoM3(ivjgeometry1);
			connEtoM5(ivjgeometry1);
			connEtoM8(ivjgeometry1);
			connEtoM9(ivjgeometry1);
			connEtoM10(ivjgeometry1);
			connEtoC1(ivjgeometry1);
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
 * Set the GeometrySpec to a new value.
 * @param newValue cbit.vcell.geometry.GeometrySpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setGeometrySpec(cbit.vcell.geometry.GeometrySpec newValue) {
	if (ivjGeometrySpec != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjGeometrySpec != null) {
				ivjGeometrySpec.removePropertyChangeListener(ivjEventHandler);
			}
			ivjGeometrySpec = newValue;

			/* Listen for events from the new object */
			if (ivjGeometrySpec != null) {
				ivjGeometrySpec.addPropertyChangeListener(ivjEventHandler);
			}
			connEtoM7(ivjGeometrySpec);
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF8FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DDD8DFCD455150FB5C85408D12922CB5754461A3A285452166DAFEDD34BEF4BAAF5B1E2CBDB5C46CA3758DAB75D45B6EDE9FD7902C4A38489A5DA44D8D18204FCC010108F88C95084C60849048C90B1134C4B4CCB66ABB3AF9F1084765C0F735E1BB7EFE61274476E6677BB3C774EBD775EF37E675C0FF76FFD03D078D6FCCEC6E621A0E464887C6FD006A0E4BB8541724DBD5FC1DE752E14BCC17377B6
	50E6C1B4E743358552EC3726649588FB8E6542F3B7BC2F3CB9A5EF9B5CEF914A8FF99DF093221E96C8FB7871CFE24257D3378B6B59C01F5FE84F076BC6209E60116B02DD247E7D6DC51C9F4D79C899190290DBC2E4BEEA5F4EF9E9A0138B3C96206E92A6AF4315A9B02B261A4BF55F12A82CFD541779AAA9C7D3A6528A1EB71DDF0B05BFE4446FA6F50DD0BDC91E8AF8F6G91633304574F6F07EBDD5B207DC8DF2FA4170D7409CEE719DD7438C459F7314CBEAC5554FC481FBA06DDAE2B6FE21B55AD02CF366FA1
	65FE2BBD1716A3882B214CC44AFBCDA2F575407D98D05C9E64BFBAC8784F43FD765E143C6DFB513E7ADDF5DCFF4B785705957BDD591BC258D7351759553DD7354FB670F4666B549793A58473F9101684349CE8ADD0E1E9CADE9550DFE98CDC3BEB8FDC0F754ACD9E174B63BE6675B6BAED3E0FBCB6518977B5B51042F96DE21F47E7938466670976D283FD42914BF7433473B1CC928F204F9734BFA82C7A4A6D199B8CB0D915B81EE9AAE5D858CAD5CCD85D5F8DD9F7F4E9E0CC86565DA5E86B0EBE75D4D60EC15D
	7135CFE54D6365AC50547DFCA97175F5B4861641BD49778A71BB654F257C0AFDAA1F45E791651367B6C8DB348FE3433291F5B1E55FA394D45EE108433CC7E2B30B4A188E95E5FADDB63AC8595DE52C4EF2C517B24ADF2261B3DD1653B8A54F2565903B95A9F9AC3EE66DA16DF5677E143C86A08B1097A88EE881D072FE0C31E53B4A2798E3EDD61F5CB8ACB9ED22CFE0F9E7157C852E62186C33363A7D32555DA71E317AA4EB2FD3748BDCE6CE894120B7A05FEDB4767B200E8FC53768334A12478D25DB21610ABE
	BF1319DF32194878C41AEA16DCA2D460728AD8EEC2499638BA2DFE3953EB334A22AA41529714901F0CD84FC009D0G5CB3DC9E5ECB70CA857B5301F28FA08E3BF61372CE0ABE68AFEAEACC9E4F603057CCAC91AC87884EBF26FD47F6382FBA0098BFD8CAF87361F911D24E055D2FE94AE9167A087A5091E19B0B1520B39716027C9A206CAA68DB001A2BB006DE30E08CB53F7A00D07A54E78DE328306E292C6EAA5E77D6695B52DD348E2267FCC8CB82DA85342E8A47019D12C5D75EF6DE865189F94DD5416DEEA9
	57B9F2DFF2A8280EBCFE4D5170DEB1481F454B88D78743FFFB9971657603A9F955C016036803BBF7973318EE347AC50C6253BE88C05126FA0469D9686A6678E6F15DC89CEC63F1769650AE205DC0FB01767136F68C68A8D0A7D093D0A5508120D662FB20FA208EA0C941230A5663859D6781A5832D81AAF89FF4876A810A81DA883492C8B81412D7825486B486948F3494A88D28289A5AB41083A88EE8B1D0EAB57A7687035E0CC0FBC2E13805DF37716BDF7975AD1D1C117DE82F3E4CFA0E4BB10E8B7291972DEC
	8747EFAA630CB4C5397FABE2589FF4926C9177F0B10CED81637FD5FE5D24135B4C7D34157B09447DBBC06F9A14C97C73A677635B867E534A6EB370F3A8AA7FFF00EFB8E2ED7E66BD84BBF1C4F44B4D22D7F45B60464F53EE5FC3706663D43FC749B31F723DB02EB541C062D778776CA138DBE559A7758E4BE207A84B125B2E265FCF6B728F77FA55A1916B7A729E62FB4FA80CEB0A7C577610B1214F63F60B3473D132707445F4CC921D228B14EE728C3BE5467F56DEB2FF9F94AF72B171113D6B85B226B9074978
	4A66448B2DA4063CBE094DF6FD32E0B9CC462B5FA8F3FAC23940A3F37BD238B69C4631E6749D9CEBF6DE7A12107C29B753E86EB46F5E1BB3638F33BEFF51E1F52CE1BAFD2554D877FF26E3A3677B192E57B410F149E8061EC5737C6AF5528E48243E1DB850A77AAA85561E36885E9A22B34E75D53E39183409E6315FBA6C14CD12DF26D317A6E9C4F2CA17C41BA277336EFCED5DDE310F0DA11D65043F826CDA83A4546098F2F09FC947F11C8F9ECD9E27133990529AEA884E4F503116E0D2844F95B58C670DCA7A
	2C81CC0F0167F8CD7AEA1ADEB2086904968665B7AB692535B0CF28D553191EFFABABA031A88E8D0BB07DA2931613CA3C6CAEFBCD17F606478ECB2FACA33163544CF5D65412BA47DD782EB7D64B668EF1F00D87CA865AC17022F23DEE14AB2EE36955F5EC4E9197A0D762C13936BA263F230E4DCBD0EEC99D11FBC1111B50244F057BC5F54CE6A637580BF28575ECDED3D2EFA4F76D904A55552B65196B75F22DBE1413F9F93175C6F205FE140B67F2F19C1F4575CC2E6088117B29A2372C1E0D6B2B39FCBADC4B14
	726E14D12E7B082A9F62C7784BE9F91F8EAB7A9DE17383D29E41B17608D6EE76B84AEDBF4A6696A8D7F5D4AB37FB8265ECC759BC44D86EB1C5EE62A81B27205C5CGB957A44AADB94A66B2FAB9E66F264BA8376AA833F7ADDC339BD272F6B6E84BFBDA112BEAE0739EAC4F542045794A9514CBE8E0F323248635DC9567D6C17187CFDF5D407C31BA203CAD99A83736014D31D6733849BF26AD6F7B0A5C4EE3CC2FBA381A0146009201185C66AC14CB52492DGCA8772B692398F33D1AE3F11553733117B23116947
	7A4F7F5ECD7AF43A7EB1E09D310E1579C7E1DC2A29B9C17BD03646F13EA3174BA7B562DA466CB3240FDB8B4F79CDD0F693767FCF3C0AEB99F38793051E53AB32C22D5554B5B1BDFA1A547EFFDE57E8966BC36F2EA0F546C35A42A66CBF9714E95F270E797D1EBE093E4A713E67FC39F6FCE03C1E7217615AAF797CF2AB747FE358CF751793FD493B9EE773F75F5C01FC5E3FAF01FA5781D5B5937E7181B2EE123E085959F7G6D0C3BF42F5073593BB2DF89E167D8B333EFDE33EA675A272A32D8FD8B147A16C3FA
	6EF12C6FB9DA9F6953181FBED7DCA2E857296842D448A10A69B879015697C91BA6C36A58F01C7B62382AE35C1E5FF29D4787F16DAC9E52D79EC71DEEBD781E90FA6DCC33EE86A3A3621BC0579C48BC366003143C96A0B6163E63A6764A8E494F4630BF77EB4730A810CBGDA2A481FF4939DDC44EA8157ED7E6BD8833649876F9196EF084DB4DAC7F35CB7AFF355874C4E6C8FD5FB1BD31F4E34GB1BDEFF423DF4CA0937BA156FB1E7ACBBB1FE0729FFB14388159F50A7CCB83A4A69193A63B4D0B32D5ADA9F9CE
	A0E6F3EB0E56668460AF85FA3E854B3A47CBB102C9A73377019DDAFBF32D338C6D0DFD4E1EA934B2BBCBDAD5FB131FF8BAF34593E86FB30ACEDE10C9EC45BADB06487C902A04A605BEDCB0043229A0D7F48275FF5B21573F8D52E4A0164F6743FCB140CBBE01F57C48C76A7078A43B6446BA6A7DA81B8EF295A7D1F6291F608973DE22101F37D999E59DA097FF92633370883952392D00F336EBE4529F40BC7B0C47B96CA27396066544D944B2EA59A221E84BCD06D892DAF912E1A818D4ACE3FF8AB87E9431FCC4
	51278DE4E2CCE8FF4CB061931D120117EEC2BD3FBB743E405725C452161BB451D5325B2469174B744935287F9F5FC47DF7B6A78855EB87C36ADF7191535B72112A3FAC9E4C0A81E2FAAE9CC7BD93C1A675A354738E67BBB4166563B49CBA60DD46ED678A16B92D209EE355F29F36CFE0B9C527A096CEA18EEB95FE8C7012CFE179970E7272CF53D008D87E6D13D8CEBA14D1F19A4B2F3E2C4483611D4658397BF2E09F73C8D03C2E8459F50A7C77AEE3FBE3652E3F02759534C15F5526744DE5A7185E58617185C2
	EE0049E8E381C5EB0D4C6376A5DEC1BB22202E45ED2857A60168E57559ABF93B92D0AE8DE4CABED65A150076B2393A8C146B8119F90A5CD7A95F2E692D197CEFB3505E65A01B5B0E721FE4923E6416E449CA061B125D6C7D7039816D7BE1C3BBDB5714619A23641D1DC95691EC125F6B34DEBCE633FAE55157A17AC624BE114EB84EC8FE093CB5607E4A42CC5CDF09EFC77F56E421DEAB01D75001FE387FDD3AF66CA62F9D4DBEABE9B0E60F4769AFB9377B8C1637BC8B4BEB6940723C59D8DE947096F7202EBF4C
	C67CD8798376FD5CEE74DEA45FC2353CFD5E33855BA7692B6B2EDCB0EC1FB11B9C19E99D2CDD96195576E901FE5A89445604FABA747B9AB6333AFEBA2639C7E26B7DFF24F336D8B3DB6F5F2070B7F491FE02064FF039698C0E55C910D65C09EB549B2B081F2361B99EE8B150F220B42002B3B8D7FB211C2C65055D83E805BE5C6736BABB8296BDF0AD3E0278E0D03C481F93AB34FDB943E163G412174CC7835E3AD31FC5BE93E66E964E3BE5CFDCE3B8F193D79C4E616018F69BB95AFBB7A0C6AC3065D378F12BE
	E78970D30152018A3A206F6AC25C76D516C6428D77C3B8E04C2607E708C5723431E0F63CFB08585186FABB0122016683A5A9F60CD6C674BF99C58C7C4F743AE6F0E67B0DB74FB05F7D2EF079D601DD02855EF3ACAACEAC5F623079DAC03E87E8CC130F75A50F6DA6EB2F866FDDE404676BED8DDD7A75B6521F46C0D97301D8FFF6500E7D59A28B364F71EA726E77BCBC671E05F68BD4875483B48194FF96FD743D4A46C8BEBA068B16E60FF25B24DFF4C4ACEEF1C742DE308446994BF7F744FC0B4F9A656B724C2C
	BEB1E23E6506753D691DD9FDFBA366DB232B0F3F1B7707F8B7B7D9FBC5A70B1102DCFD0C1077A461DCCADE61B90C11289C92A3248FABBD07B112F61844088D1E23006681AD82DA8934962868BC46486B7BEBA34548F132C08E71C1C3A2B8B898863F18A1F6AFC64CD7FD5EA85F43C3B32BAFB9E23EB6DDFD4C8F0F8E99794181329367518F96877AE16EF95CDB9B734DCC4F6BA266DBE608CB13FFE675FD9CB15FEA437AB64AB32BEFFB44FC6BF475313876561238AE6C06B19528854886948314G34349B63FAF0
	FF443ECFB9BEB0153EEF698C6D7CC144FC295DC6798A47757952F5F246757DADA8DF196E397093A3DCDF28A33816C2DA8BD08F5084D0BCD092D06AA7086B37779F1B862ECD40EF71F8868DE0657AFEED06F6AE09E827F041A81FE5E2E6757587655357D7E4D85F9FE6D8DF6EE4247AAA8C6B1B9F14EFEA755D9F313E96437A0EDC1ED93BB0C54C57F341289FDEECB71AB30D01EC6C856C07FFCF7BE1D2CF42856C07732F4C8C17B7027269F1D9E5084B03C1792606CBD244FCEB8D6B9B93E6B84EC44C177F29D13E
	460C19555796B15FCE437A5685651B1A7FDE4E0C643FBADDFDECEE3652CF4EC028EB5F124BEA97EBEADA49254DE9F50B9FD95DF06BE307C5D95C3DBD08F3F57327380F7229033E1343F382D05227980FDF8D524B08D6E8F4E3799A32E606432920FCFA9C5698627E944DA774CC773DF3A4FBE6FAC687655B21FBAE54694246AF71889D3FA02D852887E882A89EA889A83587472F578E1073C6D3196F069B3B58793ED13AEFA6D8E1AE8E44F866CE422B0667B6AB7ABA032E3F48709C83B4DF613F4A56E5BEE06B12
	E40F0F71EFBB23BF8727A5D28E5E47D80D7DB2BF84DF45F9C158BA32F46DA1D4B92172B32C37D505BEDF33234A78FC4D4EAA6373B5FB2AB6B3BE1421BDD50A697B2B421F3F2929B2BAFF73A6357587F43D53B76C16A517C82BE5FD00B876B60B1576E1B78B97D233DF3FCE2344603DC749FAE23295E3EDF955FE03D8BBEE13E40F8F8BB10BD65F697EFEE5824A7652787B7B415078BDFE50983F95870D717B714170787D6CE0F87C769D8A2DCB55A1E3DDEA8E996B52F0A83CAEAD07C269427CD5FC052C2D78BD43
	3EBE11B65653A3224FA75944D3B06F64B23F104979372073D3D4824BF9563D0D36A79F597288AA072D91BF63522F05B37E6FDDF93C7CB65953D6291419F71C44005B63965918B05605FB6B2B2CB8A6B47513B28BFBD372CA011AFB315D5758430D895D3D41ED1A1F292E8E6D1B58EAE35F5CDA7D2A02219E7FBB2B0B14B43577EB15C59781061FF7237999F67A7CDF2279D9DA287C8FD7133D6ADE49A6797869A83E7F6756623ADF40FD3DB99EAD7F0042B7FB11FFDC76FCC4788C7716DC445D5B0B382767905C93
	61B999E825027B2C9CA35C53B85E59FDFC3D9E2E25FD5AF3D4FFB89C9A770C9AE35CB7540446FDE3CDE45C37550446FDC7CDE45CF75504EADB8C4F9DC3DABC2B947EF3817C038A4E19BE4439218FF12E27FB65E3709CC7F8FD0873A78EA31C13B926AB78F58DDC5771FBD677C3BEED5D6FAB7CCBFEAD7F1042BFAE3F0231526F73385A8EF3FCAE55067657B535467E1ADD9B5ADFF3EAA37B6B0E5A507E7A475A487E3A2F56381D3CA4EBED2FD67A1FFF7D8B31518B5D2D6827FDDA33D53672BCA74783F0E9BA4075
	C82F8B0D4BCBF5463864540746A53FBEB2AEDB6BC363323DBEB2AE6F56070F63DF6BEC2D627C1BA7827987B93FF2B2107FBE67BF29639F627C614BDA7C8FAB6DA14D0E6D2140066D619F6837BFAD705C83B4E643767067007690E7E3B11F402F0BED417DBD2B3B69CA20CE551C7F73C0BE74123C7D88AFEBF42D2C40F2AEE6844AA3C687B2353611310D697BDE583D17E5867A327D47A769BBD02A0D6DBFE6A87CD5147F3C064F76A5C5871EC7CA0734EE9167564B68DEB1B9DF17AC629E6EE816D6D7B2DEB05967
	69BE708A10DB2D48BE102D11ED2AD4624DF3A4F47C0F9CB10E7F09A321637F5AA31163BF76C868783F6DC864781FFFA4FC7C4F69A0E7EE1C560B1EE1D990EC7D8413FBBBF06DA49DB0A9GDEB510291F2D2130B96F17C7C9CC747B2CAEA80ECEC69B6CA46FE0A766DDG7249C029C08224955B915FF76BC81C30130515DC0F0EA215371F73E60D10F917712BF4A54EA10E3E2A350F4F5BD89CCEF66963B0462E465EFCFB28B87CBA1DF7AD3433F84BD378CBA8FF19064F6230157249739AC8B3B9B08EBF3663BB5E
	C28736650FF275BA89122AC72194CAA767FAD2C709446A4ED1782314DF2761B31D7E1C03E7995AA0ED1104BA4D27FBC0C583A9F9A583487BD38E6AD92570B68F91DEB4BCA7A8BC9B65AD0667D583E84F06207D07348155066C41D076FC190E7DC503FAFB12A83FF4D0EFCF13876DE9003404C154C9F620BD39CE64BD65C0FB8A951E599EEC4F24BD581E97036656B62766CC05B314BD1B68F940D8275E1EB7A8BF5E29376773925A3388520ADC2853D60968B4814FF18AEFAEBDBB38881E17A83C97875046658A2F
	FE90F945EE3467C764CBEF5E9EFE26399FF404FB2F7FFB092F51DC55BA225D4466E7E9DFCF30753A31FF494A46F3D1CC6675EC14D96A4633BE5F6F4031E395708ABD582FD5761075A1E52F354CBAAA5754FC88AFFB0DD63B097612EC7C5EEA46B2CD9E65FD4DCC4A0C066779C00BBCF8C6F86CBDBCFF3B600B7788661346E721D34D13194BBCAC3E52BC6AF911310127B3D91D37F4E21D255E14BC1B17703E5D29BD3BC66C1F1D45566CD87AEF825249D8C03E57D2535FB923CDA77A8E2E5726F7862467E872B35B
	9EFB97EDDB7758C241795568105F64AF7072F3787C9A377A4E8C73FD1732F80CF7619AD96E9046731233984F25C3984F5F6CA75F24CC40F39CD082D0121276A8CDA35F0A2C98E2E7E64939D316F63A1F44A2118F95CFC986E9F1863C89839E2B43C16B184A37F9DAB2DAB74BB260157888A68EFBF31872715BCC243520EBAE645B4A1F377B54B9946F53697B2F977851BE447DEBE508FB228F71732DA73CF4F8AE72A3EF1272AA7C6C7B98464BDE9F2E4D137C64CA4A18F93F72E658BA903BF05F2706A356075CEA
	47B31FEDFE727B97AFFCA0A8BEA8DC0FBE18EBE0871E85C39FA402CC121F79001E21742BBEE0F5158C116F999C9E1FF44963162D4EE3CE496EA65FAE70BE2EF1C8D99B72E39FB7BE741700BCE6222947AF318F48D83E5B15FC39B27AF687FD8F2F0267960577B9BF7A5BA9239F3F6EA13CA4F8DE25709E213C35B27BFE0971DABDC63EA079260A772AB0322C0EDE43BA0C5AC8A88A978B4C3F1BDBD47FBE6FC07F968EC776EF8948548D2B7EED9B563731FCBA9F69863EF798B13FD1C24C678EA316DB69183E9A1E
	5395DEB165950D30EF19986F3E206F5F035A19015EDADA9DA61D1FFF09D8C72476954EBFAC06CBA5726E516B7151179A766E51A4617BC3B5585C86B48194476DE7791286825B66472D2A6F728751F709A311FD1784B24BC7D45F2D9ED1FD474AB3AB652505A8CFCB592350BF0E2A7DED69283E2D7F01FE875BEFF57A49FB8A9BC363C73567C92BDF7E0C6137329413D1260309DF57ED3DAB0315DB112DAD17797049207341110865FBE606795EB0CFB79F1BA3EF6D247326D17DFB7F8E4A0F9B553F77371AF16EB6
	9F5204B1653C638992BBB6F80E821A873490E8B95073E33857D259D0A1C45857B3FB3CA6F849F7EA369C18FD37F7CE57BE665F6FEC40B95222EF5CAFAC3E2EB96477D742C5BE6F635712E76F4CA0D7FE5E5AC46CEB03B4A7D09CD0A2D092500A0BE85F4586FEEE18EC9F9D13E5EB1F03FE8527ECC6C962284AA6AF6E2C5DFDD3B9E33A66A2FB6797AE413C7892BB3F465A42CF4EE2DBA83D945C9674F30F8610B1DFD25B0263123EAD7C241634FFFA523592F96F3552F5GFA32915786BE49A46BD2ECED20C33AA4
	22ECC6E53E42E71F0A706F1E6D7A6FB9088DB2547F964D87632100FFE9C70DF82CDF91CE890A8F1F49C79FEE7F27AF884DBFEC4EFCA7048FE3B8B6ABAF29F378C2D7F1969F37F30357DBEED54EEE960CB36CCB60DAB74E66308C7B6F17A17666F1E36CF3B458BBC0E6E2DC6D5762467558279D5E2C604CBE63B1CB32D37DBECA3C102FCBE758BE345E685B347963782E73EF6735FDD941DD4ECC2CD72F73C2EEC752380A93FB4FB93E9E67ADE963D375990E638F1D96949B4E8E91B96DE7D39D645FA68F7DC1A159
	DFE6BA7E01194BBEB08D59033EE09F1AFC68431289664386381AA758BB887361A2BF7A50B9E16C438D9A9FC6014C5C8955070993FA9F5EFBDC75A10B7FC09F3EA5656B52198F57FA0C3E4FDBB40167225E14907F25935345BF25CD457F9DC7B0CEB58E44C918E4B8954135F4121DEDE0B85DA3A1CE4D1346B8E9635D82B24EC9952728C9BDCE433DEA1F02EB0BF1D95A5F0B609FEAD18906D17B0051B7E1311338FEB0A861B7D471C477C9DCBF383D1D6C2F683E4FB5C9EE3111CEF39AC52BDB40BD002FC878BEDF
	F09957901EE1FBG705C83B4F699FD4256F6E8DB3EBCDD1F3C78312054B9CBA9E769144B417E70FA7233BAFCACFA1F2EFF88D7C03FABB8765C5ECAFEA7660CF5A0788483B05BFC9E2F6813AFB2AE7D3588BEEF78AB79299EFE3FD3F3FF5AFC7557289CD7358E06CB7409507B86B71EB05EB7386504711908BBCE04BFFFF17709503F9F7345935A5F0FE1FEB4537521BA702185E882A88EA8C171E955493F89F4BEE1B017B8EE330B58C1AAE7F9CCE14E7218A27F265BAE93996BFBBD326CF1117215F3A5A662AF59
	6325CCFE1647C4B0020E4DA1B3D14E2FB61158F40A7D144D5A6B4F3BF04E188C3625DF41F6752C855BE0C59436414DE7092C831E230066C521EC7EB9ECBB8B230C5A8E7B5DA36336738B7289A0EF031FAAFBFFEB22F06E7AD49769730A2F01F19F2887E88CA896A881E8B150B220D420F420024F24646D842A86B281F583F90122BF03FEBBFEEAF718364897B42973FCD609380F4F2D3F110BB84D033216FF86ED7714A2CE3957A2CED974B7529A605982643D96E57FDC067645DCBB5D3E6AFBE7D51C7EC31987A4
	DF0BB8496784454FB71EFAB7140D901DA7E0ACBF5DBBA07649E62BDD4076FEE23DF68E1F8E6566C707FFD7E63848BE44259864AD51E8EB128FF1090BC6DCFEC9E51742F3AAD03AA27BF33F3267BBEB3A382CB82F6232570B65344C42B501B581FB2ED1401FBBEBAAF675AAFB2809A01FBA8BF53D5F01F695DD07F62D2432ED706CG0A3A8EE51FD4668B732E1B2EDD3F6BD66D3AFEG4BD9FE9D5AB5A4C5FA674A1DEDF4BE74CA44FC7A73227CBB3E087974445ED5BB69BE4F4E597AF7588B14DFB1DB7F8E7B8B89
	F12F03347859E8EFE3009F33E3E0FE94935E0FAC5F8D8179CAA1CFC304FC2C8D551A493DB21E30E11927B51A4958201CE923BF3142BECDE7695D1D217B7A3E4E48FD3D136EE70850C71BAFFA79FE75D0A77D8D087EFE3F280CA9A31D858A2F550F2B1C4C6E4FDBE636D671508C732D19F6BE9687336818E20E5147479C4A6F095147473633989FCEC8DB9E037131791C367E2A6BD372DA2E1FCAFC588C4F8CF6733CDE03B298764FF50576F1DAD7E49F7F3E4BF8BCFF29ABF8BC4F36980F67AFDB3463B963ED3210
	FE428E6318C511ABD4F8E7955E6B34CC426B12EC32C3613FE5F91973DBC4D2A1725F35903DC6A94F02E70AB53C331CD7EEA1F18AA6BBBCBE4CDBE5A16B127D12534967A1D4C6F2C358AAF854D39DA52FD523F72342BB2B78ABB9681C405462737199667B713E194575A81D83C4DF2F0F6BA84A0F3BDE9F57F9E54A5A9D2489B7E0DCE715EB6B3781FF6C062944752F569B699D4B73469B1441565F2DF4EE316886269F6375D35E329B545FBF58ACDC0A78FD2531DEB1B34C771DE96763638C7D1D1CB5B7687DE027
	7C35B7687D10264CE7049BD372ECB7229FFE9970FD67D2602F3AF1AAFE28B07CBEB70D674D0E8D65071786037D106D8C76034370CCE8E43DC6E6186F21E967E3F81B6838DE942B77C3A7656F0C557B615F1579D785244D0DC5BF2C8C18072483BF7F262978E14E00115E45BCEF15C1996CBCE8729632EFAF391D12DBECE23F1E2D14F9FEC0D06EED1A7BDFC75CA37AFB085531FE60EA5641626D2CC18CDE985446A063FDDE1133E6A36F0B0A5C31EC54794633AAC6F3B577EF1E3D7AF83DFFD56BE0B6171C8B46EB
	5F39E03C466DF8B62B65A65C77CCD54E4419EFD24E91142998DD27395FD4F67571AA392AF5B01B5FA88F466B2D72E03CB2B7A0DE898ADEAFA8E773965E04EB0C5677B45F1A50E98D63EF9FFBD5656334865B2DDF455525397F2D6F6AE33C49FF75B14E1003B1DEAF87E37C2197B19666A046CFA867857367E0CC36CBAAC667B5772B7F974E4D7D692A5641EC7ECFA9982F35D2B0DE77AAE78F3B953CDED64E99BA00475E635F38C0B0E26B773F0BF8C6C93BD738984AA0341573174DD147C8D6761C9E356C9C656C
	4D4AB9217786147DFD4D599BBA9687157DAFD6356CB50E6968DD27B977C14F2087157DDC2FDA765B0353A9FB3966DC8261E75F2C169DFAADA51F7CA4B5DFCBF9DFD9BBAB3899FDD4AE2A326D7D6A7DB0D99E6331D79BB01FEE013C5DB7071FF73018E8B718CFF7854C27597FBD945DFEA5D390967CDA4811050CE2C679388DC3CEEF7BFD42A6BCF9D956CA5F1DD2C78E54E4BD7B052CCCA13BD5D84F5EA5DBE11688AF042DC28EFDDFEC953260DDF20C7C9F9E59DA728F891759490C2B448D787B7B393270F247F0
	EF3347E50557C600E2D71F47D5A6753A4A06C7DCE556DE39CC34C9F21959E7F57B1DD6193CE93385991EBDA3D348B9A7EC6917C4276D943C347BD0D2EC95B6D3EE3B483EB7A0ABAEA6E1DBG2FF158EDF352A37432709A7DB942F272F304657C6788CDC2C10078C971A231E11309D708FCA6CE1328C384416BA5F6C9174462124713DC3B29699B1514FC36B7D9DC7248D7DD3BC19023212678EAF98FFF476D0AF62FBAFDD6DF48F3B70937723D6ADB553DEA369E5DCD57305201DFF81B727B44E6727B00215625BB
	DCD627D336BA870F39EDE40D3A7122AC2A7BB756CED23E9FE5F8A2CBF3F4923D87B4F97E87D0CB8788F0DA04DC4BA0GGACECGGD0CB818294G94G88G88GF8FBB0B6F0DA04DC4BA0GGACECGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG85A1GGGG
**end of data**/
}
}
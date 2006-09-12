package cbit.vcell.geometry.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.Origin;
import cbit.util.Extent;
import cbit.util.Version;

import java.awt.image.MemoryImageSource;
import cbit.vcell.geometry.*;
import cbit.image.*;
import cbit.image.gui.ImagePlaneManagerPanel;
import cbit.image.render.DisplayAdapterService;
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
 * GeometrySummaryPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public GeometrySummaryPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * GeometrySummaryPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public GeometrySummaryPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * GeometrySummaryPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public GeometrySummaryPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
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
														DisplayAdapterService.getHandleColorMap(), 
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
				int colorInt = DisplayAdapterService.getHandleColorMap().getRGB(subVolume.getHandle());
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
		Version version = geometry.getVersion();
		return version.getName()+" ("+version.getDate()+")";
	}
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
			
			cbit.image.render.DisplayAdapterService das = new cbit.image.render.DisplayAdapterService();
			das.setActiveScaleRange(new cbit.util.Range(0, 255));
			das.setValueDomain(new cbit.util.Range(0, 255));
			das.addColorModelForValues(cbit.image.render.DisplayAdapterService.createContrastColorModel(), cbit.image.render.DisplayAdapterService.createGraySpecialColors(), "Contrast");
			das.setActiveColorModelID("Contrast");
			int[] rgb = new int[pixels.length];
			for(int i=0;i<rgb.length;i+= 1){
				rgb[i] = das.getColorFromIndex(pixels[i]);
			}
		
			cbit.image.render.SourceDataInfo sdi =
				new cbit.image.render.SourceDataInfo(
					cbit.image.render.SourceDataInfo.INT_RGB_TYPE,
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
	D0CB838494G88G88G730171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8DD8D5D53AB099A695CDE4D4D4160C239395B924B8111103CAB3CE19D1D157E93CB71A6C9B3A6357B873B9B3B443F76F66474440442422461272A7A8C4C4D2C4277C0184C5B983F422E23A9EB89BB8F2B867F44E8679D36E3BFE5E35B77B6CF30EB00F777BF81E57FD563B5E355E5F753F76D6AA589A39B6A333C012B256CA7CEFFF06A4E55BA56953219B62919766B067CA1A3F7781B6C9A79B3361
	9989F9713FB06766CB7995B9100E05F4793F18F35F025F1B25B7AADC5D7043871FBA488BFE6F5D3D7E79EC79G792C2369FF2B4B0367EBGF100A34FECA7617F473AC20EDF417188991912341252EC2A5F42F1F510BE839089906BE474CB61391EF21EDCDD45695A2E0B1256BC6C4E5CC06A515409D06AE2E5CD7CD9A47DB9A34DC5F8D5576771B2A110CEGA04AE7C9EFFFD3814FC3261E2ECFBB5AEDCAD9FF07EC3717F5494EDED9F18F16F575592A2B0F73D4F3DFEF2F45BDE832B8E47009729D29F7D2FD8E2D
	876A003464A63813D642AB8E52C9G29821F55C570B5703B8DC0F123FEFF7B7090173FF578BEE9E557C36BB77A50AF544D740AF52B7AC51434E73DCDFD51360D58FCB56495F84C3945G8DGF600D0003F5298687860A3F89EE9D70EB9FBFB1D0EA3AE57D13B55FD4AE9156D703B3A9AF2B82EC96EF03A2D12447CFC383E54C09EFF404A1D9EF7B9E613283D6873682659520A39B7E62EB330490A28614C199EE60BF80FEA9346FB0ACF5EABBCE3E3F2AC6FAFA5ADEF69450AF5EB8DF8EFF93AE25DEADECF362272
	2ED1082FFFC2E3E0BB7CA665C8C3E278C7A9BEC403E77179F70AA769A8484B6B4358E8FD8DE5E9481EA1656F3949508E910BC2B3DDFDCC0610BE3DAC473BC95D31FD0C67EEA1CB9745EF69D771CC1675B4CEC9BA98722481F3AE0BAFF987E92FA303665C88009800A5G29G59C3665C42A10C31688FF6DFE40C19ACEE65E81F4DEE155D92ABABEFFB931E7200623654BBBC0A4551A11BADEE1B255DAEFBA4CE6358C6EC50BE261C0746FE8770B8AEBBE437C531B99DD03B859A2E6C76B01A11ED1B880DDB2639AD
	36DE9998743AA42CB7F85BE6F85AAD9E658B17552248AA854B8F5DC6FC52EFB1C30DD0817CE6F6193A13582B8A74B7811C91F6D8371D54F7D2F6C3FFD1DD5D60F4767439DA08A6D24C9031736D346FD060F768905A78FB9F925C9AC8678DE3BD1FEEFFDDD3CF0D2D03088F9D913631072C85445FC0FF98208DC08118B60CB174D2AB46D04D06FB245227EEB00C21B0086758E15E778E6B5B5213148711F38D64950C18F3AB81DAC6F09C78525A2A2BCF61F59C9ED65B5A34E16FF6D744E58E5C17383CF82C674F35
	9ADC2EC179ACDE07BF9E4C7E694544178A68958690B302BE985CDE44E27A2845A3E394B73AA1GE52B6A91A667296EB6EE5FACAE9B090337F81CED857890208460E300DD3C2D9D81788C608B00E3GFBG48E0DCCFFC8F70B700E6G1B30C7AF653384E4DC8DD0F89E7C8F508D90869087108210F38166GG2DGC1GD1GC9GE9GC523665CBAG97C084C082C0AAC0FE10B937AA88FD3B314B15B1D69FDFB65C4C1FEF7167DF79F32B0E4EC8FF54D7DF675F38DD0EF03BA09E6D2225ED66763B18F146F611F4
	7F2F00595EC1ED0FF67797435896B07EB77067C69D5DA66E27B7381FC85CEF8358E1D0A7714F3B5C0F6F9B78CFCB3B4B404F3EE0777F877675872C4DBBF7905B497D32C32911DD32438ABFBCBC2FFF8731B79F27BA1D224C88453BE0DCBB868309C760A76DA0F637A80A5B565E27484D3222589CDDEA7ED51417272F5D258E09486B3A9D44774EF3B02E897A9BF710B1214369F048347391C5F8FE849D13E43B5C8BC29FF376B994063FFDA7193F774803FCCC1C317395090CE976BEB23E32B9F10C0544104BEDE3
	33DD37A245841171EADEFD2146C6E7GC7667641700C884231665CB69CEB368F5DA9A57C17AB53E86E94B16B5A4C34A0566767DD260EB5CC266BBFB4167DE6BAB6F23C0749FA59E1B2AE994D5033E8195F3CCD5A811954B791873AE5779E0935274D122B1A480CF3FD955FD2C45AC40D5CE96933AB8DB60FC227AE47EC7DB63BEDC8368A3ADF3565E9F93B648EB606FC7291411702DE0781DAAF43B1640DE2120F63B89FBC0EB96DF666C2E2174B081D5F118B381DDC108E390C5979B5113FC06427C0BACD13FFE8
	924937F4E2BE011CC97A7257087CE0481B2649E7F2E6166413D8143F6B13E17AC5A6ACA7C53C2CAFF9DD17E766314372B715105831EB66BA2517931E315D3856CB1A44668E2970CC83A8047C77083DA85D2DB6249B381C6507DD4E669C04DE25331CC53A284B197C891733F98952ED89A6F49F76A0DD32A6FF957C4E8BE6BAB33A5F893A6EE0B62F898AB6220B37A3DDF830DADFF4301EEED2AF52AD65752D8CB622BB65C03AB4CE174A6D13BF1951F5CFA6F405CE245BBE190D6B07A6B3FA93BC4BC4FDCF883A58
	492AFCE8BF02AF3E0250C53804FC13597C0054C76C38F23216AE219F6914AB585C8269C22F5052351FC33A78AB58BC4418AEFBC078638AB6CFC13AD5E368FEA86836CCE1F399BD9D5337EE9069AA27B0FD9B605986B0B2C5DB5F1BC3C897BA054DFB303E19D334F60E93F469D3585C28A0C42DD7357324E1618F1EFFA8047903BC557A9AC6102EA1044D318EF13FD8C334753DF29E69C6C218DC61700C86C882102EB467B23A9397102E604A31F42510B681AC391250850DA21D75CA46EF045307DE4964E37D677F
	A9A1FDBA5D7FB8EB69378C14F94E4138D4DDFD027621EC0FE36B099CCEDFF0155863B8C57A3886C8DB81C62E427E7F718D3817915E93A52DF8F14FBADFFBB561D7B1B9622ED27BFF6937C7DEE1FD68684704E79A6465DC0D7DE7E809F6BDF54463F1F65868D20E77BD87BF520E8F8CF77823D76159E9F3FB14FA687F87301F7A7CA3A2AFD96BF1B4DF7B9E1173F87F3E857836G04DECD7097E4B2EE123E0869593197758C9B1AA51D3961164CDC9FFAA6DD4D74DBFD352AE7690534350C5F8B02DFF1A82C9DC311
	5F871D049F6953181FEEABAA16347BD4F4E34A679045E4BC7F395697719B4F7B14B1A2147BA2D415B1415E320E49985B09FBE7E910DFFE8D4AB475131D126F3DB34D3E990C0CE85FFF25FB8EE49E5B8D75C4823031344DC674D53AED9EB6069DBD299D431683DDBAC0517710BE58C6E468A5DACB38EF737BA30731CF1BBDC30ADB971AE9340F56FDC7C4E66577181EED5FD3750D0EEC4FCAGE0F2A65A50AF51C033D2708D277E524EA7987DD5E7C55CDC8BF1F3AD52D7F51218C41BB0DAD38F520682DDA2G5379
	1BCC2D4E6900AF8AB3675604E1DD3F6C21B602C9A753771E77347A66D8A69B6A9B7AE2D7E6EB9853B3A8CC5537F8EAFB562129286FBBF614E98950C857A14F4B6CE4FE88AC21C9218FDF9034D5C0673A8E65BF532E17BF8A72168230F2F777E239944095CEC59E2FBB888F275B56E5F3A00FFE87521A00AEC450E6B809BDE15ECB847270B66BC45A8420CB1B0A71D970A9F952392D04F33679CE529F40BC5B6C34777512F98B336548D7E84B202531D26166EB8CEDC9207CFAE643566BD5DB2E195E1E15A3E267AF
	C21EA820C9399E75DF68A2F8A2D3E1B8745561A867626F76C9FCDFCAA6ED7918A63A0ACB9AB47DF219BE7BA04A7F17F7D17E6DB5B3252AB5BDBE658F89E7F24704EBE2FF434BEB93G181CAF76231C528D665C2A9BD04E1B6C5BE8ACAB35B49C1AE1AD636862821659ADA09EC39D64BE3C7A9C566302BA22EEC0BB6C9AC0FC8A608AEF447A3F7D0C575FC8C3A1E07D0F09FACCD0C7480DD87FF703A29E886EC60C1D6503E37B189BBB7471DAFE93345D1B10DE9A4276466A7DEC8879F583CD64CD22EFAEBB416446
	8E0FEF90F2859A0C8E9610742011FADC3F6706D00F6540ABBF82652A9BA6F2D95CDDFBF83B9AC63ABA208992F42F0F203E0C2EEF84696200E63520FB09623BB43DB5235FFD9E75AD3E9946261B117EC60A37B9EC0A4DC2061B629236BE3C472ADD9FC65C4C76B51742B3C514CDB8CF76912CB60F4BEE99BCE2353894595DAC3B7BED9DB21DF118ED9E9BD9B560794A0B6771FCA56DE6742F67824AD5FE8B4CC9EFC1BF5C35036E9DBB48322346EDA18D26456934FB0A3FAEB1E3FD6BC7313E48DB303EBBC7313E65
	004B3F95E50559AA379F2B7FEC57AE2EB7FAAF10EF01ADEF1FB3B6E37BA4FD752151EF8D5BE748466E4C3ADBD93BF45D2A990B2FEB4FCA86E0FBC25BCF684FB562EFD577CF13B43F917819841D332D3C156D772F9378C7A8BEDD0367E79227F02CAE18E64E9D1806FB54AF1591BF2F00F49AC07EED665CE200BA006E5BF02E366CA3321767778C209E7AF03743E2EF9E336901FB719F939F744803BC9D723136AFE7F6B8AE93BB845F66FF6FD88B2C5C97345C34F114E3BEAC79DAFB8E193D69C4E616018F290FF9
	5DABEED3FD48ECB73D1C74B9DBEE07FE954084508D90F2BB5AEDC3E9E9A03B61F988B7985369B79334454B63368553E3ED8551A38A64CE84D881308620E0BA6A71E5E9C07F13D144407FCC2E791D93BBEFDCB241F24FF77BABD789FA35820CCCD76D444A7D4EEF39C8200F83C85214E3FD49A3A5E46F55E05DC5C6F83E5F16FDDA3F5FC67A539428EB8DG6B4F86CFE2FF1697096D731B3DE46DD78369B3G83G61GF1G49GE9116823BBCA0F867251915C30ECF10A1F47741B0EE80B07ED01EC5FBAFDEC1C31
	F24B83164B7F3ED13930339363F7FB40F245067C4CBD9363E78DD86E300E9FDF1BF77AD81BB7D85AE5BB0B117ED37A98A16B24D6284F7EFD0C112919A4C6C89F967CFD0C1124CA92A37110DE8E309AA0EF864CFFG9AGDCB3B0C6DEAEBD98A8C6EA4986B944878D896F60E0B6A832CF4CF6E5814B054DB0AA771B8972FBB9E039A89DBF66078849488F89C01BBC837DF0D3BB7AE1558CBCDB3B37F7E2F27EA4E0396DBFB0AAB76218983F2B8216BBE4486F38F3E27C3E88D82EC5470F4535E99F09EBBB6405GC4
	82448324G2483944D4438BE39ABE05FA72E8FDCCC5F17611A181E9B83162B1AE9D46ED43F3E1CC9C7E74C6F9B2FF2E53A34FD2611DD176DA7F68D063CC800B800E400B40002D9B0171905F61D3E6B48B86CFA8C70F5CEE70F01D9393C3F1D201E7FB7201E2D330C4ADDF7EEE27C26FB155373F399725BBBB0B1FEC73C4A6979059872FBEA027C1E8F48AF52105F394109350B49814B454DB26A07158E23B9D392502E1C057D70239DB91CCF7AAC6C07EB07A6E617E62FF2FA3BD47E50285C2F3D4ADD1CDD7E9030
	DC03A13FFB07A7B84E84ACE7B564B7BCB2B1FED786ACB7E2486F137393735FB12FF2FA7F056B78313959F28F3983216EFD5BFAADDDF2F5F5BDF9186C9607FC4A62001FEEF6D914455D8BD61C2BC77F904FD1A2BA681A9C5269G85F7E0BC3E64A51791145E21DF0F8CDE18189D26F815535B61709DC665DE9BA565DAF5F97E00157B51684464FC502B5CFB3A34DDA78B9B3FEA8F50718B72A28162G128152G8A22E07C0A42716B773B48FD230B196F7A9B3B587D3E03745C2C956A3683B0DCE1834105C1BAAA8A
	FD7D905DE7D98A699400B5827FB85B1739066DCB12B3BEFE576E147E9E1C96C8BD78BBA54A58AFEBFC60D5BB3B7C7248523587DF7578AA4FEC7D27B25F77EB7E234C78FECD76EE637BB52F6E5E4470D00776D6A9663F3E5B7F7D1BF7F69B5D3FF9176A7AF4A671033B4F2158FAE54A16758172407BACD61A7A9CACDC88E70FFE1FC6446064EA321FD8F8A746DA4C6E8A03D82B355A94279BABE911AD6E464ECEB181E5E7E9FC7DDE6E5BFE334B0D6D97DBEEEC3F79657E6D37285C3F7DB6D47816252042D816F7AA
	0CE5A92A70AFCBF105AFD9183F0AC6495E0A4759676E10E9E3ED6C175DEE1BD5BE8D73CECE73EB055CFF733ABFC5A9301E679D249E4562A6C79EDE7530BD62F7EC7A3DF0062F302D67751B9427E90F28F3FDAD09810753A133B16150E9BCDB2F3C934704A9A7C81DF6C88783CC3B935B7D05137E460458BB3D5BB43FD33D5737EF6CFB0DFD635E3BC158D0EF7F413D05A2CF6D7D6A95391798065FF72365196D74652F2465D91E2F72D37712336AF61B556666372378795FD92DDDABA4BC577B59987CDE013F2687
	71350A739441B33B4FBE05F6DFA26C6ED4085D253B4C3905G65F7215DEF4AB432FB5DDD7C8C19BFDD708C3ECBFB0F6A19CA5FF6FF2E52586E3F2E74ED7717AA835B7DCF153E6D7E9F15016D3E6E93DFED0B5973CB3B561E15827FC12F967F0930F31204F60E388B6D3C0A1E83A7C1BA15606EC6BBCFEBB732F3615D4C26257CF9981EAD7CB7631DBA0677BE011F6B5062778B7C85E7AE46CA275B59EBBA406D7375BE5F7E326CB376D757BE5F7EF26C8B6C2FF37BFC7B6B423E407E1A324F381DD439343AD7097E
	6767EF929DDD505D4A9E5A2755D8948BAF935CBF46AE4776F2B9166F77ED17E7779B5B65057D3E6D32EAFFE03B7CE13FEF3B246F8FEC1735D57E63F86739313AD6F27C929D7E930EBFBBB0963F0F637386476277F37C6CC12D7D8F0876905D016D217BEEEC8FEF53F7FFA2A19D8710A45A43939D5A76104AE3BE1DBF73233D7BFB46FBE4E82CCCD59C7F5E7058385F0B6DE778D50D2CFB4A311EB9A3E36951C67673DA5D485846647DF72B3F71E93B013C6C7CB10F6ADF954D4E9FB3847E8D0A2F51605939646D9D
	F89F4984F93151B837DEFB0158165C2FAB3C874FF0E3AEE8E5A56385235DC14F01CB016E10207DDF239A5AE3FBC43CB59E709D7F668346715FF640F77CF79C889C7F76833E635FF3A0F07C0F9C709F7FCDA7481D9B3BE5505927C8D27CBD44A6369338F7E202F4B7C098404CFB589E8A1B735ECB5BC7275B528B555149E844ECFAD720814BE6C33A90208A2095E0E0B65AF747A192A76CE6619EAEC7F3210A2B6038497DE45EE53C145E03F3086A8DDA7D783C0D6F0D1D5647E14AECB576564C769507FF21732E1C
	9F31F8DBAF70B9A7693BFC9ABC0B43EBA8BDC99F063C19BF42B83C61A42E71F2E230AD4FBD251729B5C615439E63CB2667691CA4A80671DEAB70AFD1FC38864F6FF8A8F817A18A72726EC519E653B3A097240384EEE3964A99AAF04F52F63182526982D7C147707CB9665C4AB9284FD312DE1F3AB92A8EEDF3FC69F3065E6BF24D516B53C37141F3747A7C3E977509003C74B9A8535CF654674CDC4495F420BEF6014B6F705667C00737BE77F66875091F2B39D3B15717BE5750BEEA65DC3DBE91941FB6D72F4FD6
	AB6A1397EB4EF54522CCAFD21912A11DAAF05DB241654DB367EE1907381F4B28E33140E5885C40BC5467F5721A82EF8FFF557C1E5961EFDD7F4F825F233924BCD64CA3BAEF21FDBD316D12F9583F9C9A45FBD10C26E994E90AFE0CF7FD2EBB01E3C7A5606CBF46FEED4F9932BFA44EDA4BAC67146A6A633058BBEA69EA203DA49B3FFFF1826B1C79E39C3F57517EE1852457G645D07F70487F6627D5B689F4E1012DEB03E8B1D52F2BEF37BFDAC3E6A6E535C49ABE94FE2BC3FE8C01E41109FFF9F41DD5E203D3B
	C6741F1245766CD87E3CB179E4ACA06FEB29793F9E13CF646DF9C51B7F76A9ED7EDACDF9265BA3BBD03774C7E2A47B3CE01F6F64E74771FB787CD9346C660C361BCC2F3058EDBD0DFBE4E762B01EC3CC984F41F1984F99A749BBA94910CE85C887A8389F73DE23F964DD11527B591DD9F26F1465DDFF0244E2FA1C6F7DAED20F9E17EAC01FEC00E3BC22A90F0BF9B7CF8BC67BE6D98638207B693C71E40D1F7A715DCC0486A8F386608D1ED66ED767D04C468F53756F92402F389F6D7EA3E1F7E9BE5A6FC50560CC
	10F6895C9A0A8B194F5E0F617B7D0A3FB6CF4A1327EB7E456F93FB5BB95CAF8F341D3F77D37D811F8B1F46BB1FD1734977AF161163CC6E031DD96803D586FA60DDB0740174G4468834C87748E65832A8F982FBF0E1277993A1DEE5B105321D86CC76C36AE87F9F70177F179E26DDC79G76F175521BE34A340024A6274746DEA0E3652C225C198750372BA8AE945211825713097ECEFCG7D5869A03802F8989B6391779D45B54433771798AE2D574897245C455A3B528FAD633149E14443280D7882FF31407C7B
	3B232AFFBF6EC07F5A8DE4537BB788E84263D57FC6456B5B5892BA264782FEC9BC5A7C38982BD7897BDECF4F058EBDE84EB5BD08385BA94E75A0FB170961F6F83D7F6E554E9E74EF7FC3FE72990FCF837288543E7C79075F2B361235C73B53CD97B5EC6D711638BB9C86B2C681A48324F27DD939EAF9EC5B5CD3237AAEC5C65FC98B827B2EGE80A97283EBB34C0759D2B2FD054D76723BEAD34810DEB015A5F86AF5037753D97881FCE0B5DC356A9EC8C0DBC273DCFDA756AA443F7E529CD388CB379B374BFEF4E
	E0755AC63575727DCA2F7B410100151BB541F27FB66EF2EC0E7C8BB6EFDA20DF777F0A62D39768577D9BCE625CED8D64357E846F32F5561258090774F20055GB98960DF001A84BCEB59F430DC8AF02E5762F4B540A25F2EB9F2E07AFD5DB0DE7D18FF972E43B9D26C7C3B2438ABEAFC3EFF551A407CBA401FF1CBEF4EA0CF265F4B1F937D22A0AF91A095C0DA88E38BC069C2546F2B037C5EB0B9BEBA22A8160EEE7A961CB80C324967D4B4D93833F6F71938E3FAF8A1DB733742533E105DDFE3EDE13A895BC270
	C26F36201FFBC4GCD74C235ADA4AC543705DF9EA46D1F5EF45DA376636A69BEG3D5908FB837F48A47BD2ECEF2059B6A4A3ED461EBC01E72F0A3077B9AE1C563F4FC1F4D88A7C3752F2929D83595F6A4F0CF02CDF79CD1DA4FC78DC9E7AF04B5DBF10EA7E25A6F31B8F9F26F05B14AFD26770F5AD156B194C4F1D725EEFC993F7B73B97B15B8741B3FC911B43B25B7FD458BEFA11316D57EAEC1FGB4490B54FEADF5115E762B693E9033B3FB0D2745265855772364EF73F4794C36BB7B0C5ECDDB338857BA0FFD
	236D4B72EF31E7A2DF3D4CB90B199E850BD5BB31F54E17D9B8EF29DBFC31BE43F17C96728A8E5761C37A9D8A6DEBD34D645FE3CE7AC1A145D356D07BF98B279D9E87EDD606378F5F49C09F86ADE6BE0C00E774E23686E1BE1C1409BECCDCEC6C43F59A9FAE871AD50BD59FCA09FA9F4E2AD5FD4862FF2C8F375A72F4794C07BFF0983D1F17170877225A445EF4D162F86D7F60C9557E4FF6F85B690F62DED6EBA233138B1E41096CEE83335337E24C1D16E8ECA7ED3C47GCDE222EA2765DEF66AEBD77B945CDB8C
	4B52FEAF023F28C5A9180D122CC66F042DCC447D034F2C788ED59A117DA15CBF3831091C2F685E4FED30B96423F41AF3D436B8A4BC8318E24575FC77C33807B00F1D81C0BA8EA069A1744926CE74C96AC36375C9CA03A4F8564B4237BF9DEFFF281CA6A9B6963D5FC8576BD00772D39CFB7AEB48F7E24C16335EB798GE9F2BBDD32DB99E4D87A3588BEEFF816468C7B1D2A793D7964255F23AA3D24BC7877E3EA7DFCBF26564777E3EAFDFCBF26B64077E3EA7DFCBF26D67B7D9866474FCCA44E434107B1G49G
	29G694227DBBF7FC02273890339C42D35CB468ED2FCB36438EFDD579F8F7CCD37570F13313E5D29A84EDED2BF628B0F93FFA9CE97C5B25C3B47090D20E36BD698A947EFBBCEE253AEF7D2B4FF376FB44E998BFF866B2B1FE13B3A23915BE0484F308DBE55C8E893A03D9CE03520CDB2E15B49D9E254F658F70F0C5B4E93FFA7A9D64FBCF1576370921C3BA6B411BEEFG5261GF1GC9GAB81528172FFEE4E5D8ED085E0826886988188831889908B3084E0454F51EF6575A5FE5AA25F50244EF3DBEC44FDFCEE
	DDA46C349A6AAAFE98F56FBE0DF6BA73B05AA91D5AA98252B1GCB846D1F041DD29E9EEFDFB527C53553BF7A301E42C750CE7FC5BE6B05ED27FE07AF9DA1BACF40D85E58FED66ED0DAACDD92367736AC6D9C5E8475DA9F713FD6E6F6B91A01F699G7A18C7D0572BB250AE290F20DD62A8ED51D2709700E9A9524E4D94E73ECB47EB171F18D53BBC6340FAA217621E4007E35EF7DB8E78D5CBAFC62F772C281774A8487BA84A5AA946A6572328D7B8250D02F48240F2C1FB2D98D3D6BFBADE3DFE75252A57BAD1CF
	71B2F19F5B6FD9B201B34B8C6FE387AC273FAF4A4A0586AC278736D6AD215F0398D926DF43D6D0FC48B27D9A7681E177F048CB937A3EB9460FED0F417C68B17FFEE465CE4B5AF241D0A6A2C0B95606F6B6125FE2BCE143B24F5B55C84686F1270DFEE2053D1A4E720F1BFC777527CD017B7AAFE9BFA1C39F5DB268626755EDA67A8D084ECE0FAC46940BA9DF606ABD384B4974169BA736D76119E03968F117E3F1D0EFA27E0EFECC9F9FCD949F7718BEBEFE5908711188F945C9623C5524659F8A7848240B090F79
	DEE76184E2F959A586F5B05BA7B7797671AF1B827BF8E51371F87E6FCD5E6379EF1B0C4773BFB4E947F3067BF39369A73AE09CEB95F47FA9F0DF89DCF673FA0E7B52E6D53A857E5566D7B93ECEA68C911F5FCC64BAC7F12D9C77068677955ED9EDA6F18AAAF7BB5DD8F6EBB359176C34596DFC9EC2E9EC8E88DBE10F62E6A2234DE551483DCB603E927E5AE9682F40FE2E1CE039A7A79857AE9A3FAB1274F1BDC87129C97A384EB4093DBB48EBFD9C63BAFDCCDC4783BE69710B096B76BEA339D77232E986F5307D
	F78F5DE348FB024947F08394377D89757B871B243A0C095953BC41F2570E3B9C33F7A93D1BFE78893D9FBE217806A774FED8A066B32D10977F847AA1EE4C9D5722A74D3915CFDE0C9F1EB5FCAF340E17EDB32803597C53CEEFBF9C6B74764376F10F4B2CDC45844B75CCF09E10C347FB5713FABFECE27303A775FE38C54C83C2A0EF5513680770B16339A999662949976307BC59C86E81DEB654208EF69FB4E1B3B93737B96CB607FC0CFDBDDB54391BF4457CF71566774200E7C47F8CB09E4FDCD29EAC5EF69944
	E0451898E4B8D9C6BACBB6624E4AC8F7A49BE5EEB429B6EA557CFE2E71525B6B4F171487BFC3B6F95B6BF7A6EFFBBDFE9A6FE6C5A66339673EC6BC770CCE46D86F5758E8D473BB47F4696D757EA56541F45E7CF7EFFB957E5D5BDE03FDE82FF4E12FE1F1B7AF67A95CE3346C543CEBC227B58C3FE5E0030A47E98D7603192ADD8FEAFEA7E6DCFA9B7F6BA565416C74D826370D13B33DED7CC7875A3875A934F176D9343175A9F117C8534F3D25799D79BF50675D7FBF50675DADFB5BABC6763657B6F177B0D658AB
	C45CDDC8G9CDB472FA5D3D7FEE6FE2AB1509DA56DD9E17E53D066E936FFCF705B1FD647C8D67760B7EA5DD3455D1B7227FD1F8D0873FD4D5D9BBA96FB55FDEF1BDA77DF4F0EC76EF04D3D8FFA875D2B6E64FF28F5CF1747D3F73166DE824137E96A763D177227EF49EF361732D81C21F5BF0DBEFA670CCA7BB70B7A7BCC3BA4E22F65E36D7CA1924A469A5859BB1EFABE760EA757475AF8E27F775059D323507BC47FEFE92DA2E594B14843E398F2FB5B6316B6624D4B32FA3AF6CA695FDB1D757C8F32B2256CFA
	699536162C1732490230DEDACB570B75D2962CA587487F61112D854FF75285EA51CB059D73774FE395D818763557B8FBAD30EC84D3FC58616CAD3335771675757716D95A15B259EAD34ADA5C960747EED148CA1BED4870629919525A2F254D1DB659EEBD8D0BF6B7D24A7552A60AED12597B86E44725C1FAEB8C6EE81F43EA27D7689569F57AB9425D64F3043B7967889B247CB164A765C1224346864E90710C1CE6D107C0F76BA2FA5906642262E5713DA5D47557C4CE9EBB1BACAADEB42F378488B19A2A0BAE15
	7770BBEE235A336A34499DBE6F5DC84F70336AE75433EA369FFD144EFBCC0037BF03FB6FD39B497781FD6DCBB777DA6CF645E26FB9623012BD6A23030A2C1E5F9CB7117ABDC843B3F1CF0D48FDD6D3663F81D0CB8788BD97B6EA78A0GGACECGGD0CB818294G94G88G88G730171B4BD97B6EA78A0GGACECGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB2A1GGGG
**end of data**/
}
}
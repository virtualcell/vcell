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

import java.awt.image.MemoryImageSource;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import org.vcell.util.Extent;
import org.vcell.util.Origin;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.gui.ColorIcon;
import org.vcell.util.gui.DefaultListModelCivilized;

import cbit.image.DisplayAdapterService;
import cbit.image.GIFImage;
import cbit.image.ImageException;
import cbit.image.SourceDataInfo;
import cbit.image.VCImage;
import cbit.image.gui.ImagePaneModel;
import cbit.image.gui.ImagePlaneManagerPanel;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.SubVolume;
/**
 * Insert the type's description here.
 * Creation date: (4/9/01 8:06:53 AM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
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
	private Geometry fieldGeometry = null;
	private boolean ivjConnPtoP3Aligning = false;
	private Geometry ivjgeometry1 = null;
	private DefaultListModelCivilized ivjDefaultListModelCivilized = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
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
			sampledGeometryHandles = new cbit.image.VCImageUncompressed(null, compartmentalPixels, new org.vcell.util.Extent(1, 1, 1), COMPARTMENT_SIZE_X, COMPARTMENT_SIZE_Y, 1);
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
private org.vcell.util.gui.DefaultListModelCivilized getDefaultListModelCivilized() {
	if (ivjDefaultListModelCivilized == null) {
		try {
			ivjDefaultListModelCivilized = new org.vcell.util.gui.DefaultListModelCivilized();
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
			ivjGeometryVersionLabel.setBorder(BorderFactory.createCompoundBorder(GuiConstants.TAB_PANEL_BORDER, BorderFactory.createEmptyBorder(4, 4, 4, 4)));
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
		return BioModelChildSummary.COMPARTMENTAL_GEO_STR;
	}else if (geometry.getVersion()==null){
		return "Unsaved";
	}else{
		org.vcell.util.document.Version version = geometry.getVersion();
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
			ivjJLabel1.setText("Subdomains");
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
			ivjJLabelExtentTitle.setText("Size (\u03BCm)");
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
			ivjJLabelOriginTitle.setText("Origin (\u03BCm)");
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
			getJPanel1().add(new JScrollPane(getJList1()), constraintsJList1);

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
private void initGeometry(Geometry arg1) {
	final boolean bSpatial = getGeometry() != null && getGeometry().getDimension() > 0;
	getImagePlaneManagerPanel1().setVisible(bSpatial);
	getJPanelOrigin().setVisible(bSpatial);
	getJPanelSize().setVisible(bSpatial);
	
	if(getGeometry() != null){
		try{
			if (getGeometry().getGeometrySpec().getSampledImage().isDirty()) {
				return;
			}
			VCImage vcImage = getGeometry().getGeometrySpec().getSampledImage().getCurrentValue();
			byte[] pixels = vcImage.getPixels();
			
			DisplayAdapterService das = new DisplayAdapterService();
			das.setActiveScaleRange(new org.vcell.util.Range(0, 255));
			das.setValueDomain(new org.vcell.util.Range(0, 255));
			das.addColorModelForValues(DisplayAdapterService.createContrastColorModel(), DisplayAdapterService.createGraySpecialColors(), "Contrast");
			das.setActiveColorModelID("Contrast");
			int[] rgb = new int[pixels.length];
			for(int i=0;i<rgb.length;i+= 1){
				rgb[i] = das.getColorFromIndex(pixels[i]);
			}
		
			SourceDataInfo sdi =
				new SourceDataInfo(SourceDataInfo.INT_RGB_TYPE,
					rgb,
					getGeometry().getExtent(),
					getGeometry().getOrigin(),
					new org.vcell.util.Range(0,255),
					0,
					vcImage.getNumX(),1,
					vcImage.getNumY(),vcImage.getNumX(),
					vcImage.getNumZ(),vcImage.getNumX()*vcImage.getNumY()
				);
			getImagePlaneManagerPanel1().setSourceDataInfo(sdi);
		}catch(Exception e){
			PopupGenerator.showErrorDialog(GeometrySummaryPanel.this, e.getMessage(), e);
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
		//setSize(821, 451);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 0;
		constraintsJPanel2.gridwidth = 2;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanel2.weightx = 1.0;
		add(getJPanel2(), constraintsJPanel2);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 1;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weighty = 1.0;
		add(getJPanel1(), constraintsJPanel1);
		
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
			getJLabelExtent().setText(this.getExtentString());
			getJLabelOrigin().setText(this.getOriginString());
			setcellRenderer1(this.createListCellRenderer());
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
}

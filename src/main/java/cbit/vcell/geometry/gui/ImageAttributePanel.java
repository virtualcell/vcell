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

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import org.vcell.util.Extent;

import cbit.image.VCImage;
import cbit.image.VCPixelClass;
import cbit.vcell.client.PopupGenerator;
/**
 * Insert the type's description here.
 * Creation date: (6/10/2002 3:26:22 PM)
 * @author: Frank Morgan
 */
public class ImageAttributePanel extends javax.swing.JPanel {
	//
	cbit.image.VCPixelClass[] vcPixelClassArr;
	//
	private java.awt.image.WritableRaster pixelWR = null;
	private java.awt.image.WritableRaster smallPixelWR = null;
	private int xSide;
	private int ySide;
	private double displayScale;
	private int[] cmap = new int[256];
	private javax.swing.JLabel ivjAnnotationJLabel = null;
	private javax.swing.JLabel ivjMicronJLabel = null;
	private javax.swing.JLabel ivjPixelSizeJLabel = null;
	private javax.swing.JLabel ivjXMicronJLabel = null;
	private javax.swing.JTextField ivjXMicronJTextField = null;
	private javax.swing.JLabel ivjYMicronJLabel = null;
	private javax.swing.JTextField ivjYMicronJTextField = null;
	private javax.swing.JLabel ivjZMicronJLabel = null;
	private javax.swing.JTextField ivjZMicronJTextField = null;
	private javax.swing.JTextArea ivjAnnotationJTextArea = null;
	private javax.swing.JLabel ivjRegionJLabel = null;
	private javax.swing.JPanel ivjRegionJPanel = null;
	private javax.swing.JLabel ivjRegionCountJLabel = null;
	private javax.swing.JLabel ivjRegionNameJLabel = null;
	private javax.swing.JTextField ivjRegionNameJTextField = null;
	private javax.swing.JButton ivjRegionNextJButton = null;
	private javax.swing.JButton ivjRegionPrevJButton = null;
	private javax.swing.JLabel ivjJLabel = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjPixelSizeXJLabel = null;
	private javax.swing.JLabel ivjPixelSizeYJLabel = null;
	private javax.swing.JLabel ivjPixelSizeZJLabel = null;
	private cbit.image.VCImage fieldImage = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjPixelClassImageLabel = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JCheckBox ivjFullSizeJCheckBox = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private Integer ivjCurrentPixelClassIndex = null;
	private javax.swing.JButton ivjCancelJButton = null;
	private javax.swing.JButton ivjImportJButton = null;
	private javax.swing.JPanel ivjJPanel3 = null;
	private javax.swing.JDialog fieldDialogParent = new javax.swing.JDialog();
	private String fieldStatus;
	private javax.swing.JLabel ivjJLabel3 = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ImageAttributePanel.this.getRegionNextJButton()) 
				connEtoC2(e);
			if (e.getSource() == ImageAttributePanel.this.getRegionPrevJButton()) 
				connEtoC1(e);
			if (e.getSource() == ImageAttributePanel.this.getImportJButton()) 
				connEtoC9(e);
			if (e.getSource() == ImageAttributePanel.this.getCancelJButton()) 
				connEtoC10(e);
			if (e.getSource() == ImageAttributePanel.this.getFullSizeJCheckBox()) 
				connEtoC3(e);
		};
	};
/**
 * CreateImagePanel constructor comment.
 */
public ImageAttributePanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (RegionPrevJButton.action.actionPerformed(java.awt.event.ActionEvent) --> ImageAttributePanel.regionPrevJButton_ActionPerformed()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.regionPrevJButton_ActionPerformed();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (CancelJButton.action.actionPerformed(java.awt.event.ActionEvent) --> ImageAttributePanel.done(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.done(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (RegionNextJButton.action.actionPerformed(java.awt.event.ActionEvent) --> ImageAttributePanel.regionNextJButton_ActionPerformed()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.regionNextJButton_ActionPerformed();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (FullSizeJCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> ImageAttributePanel.createRegionImageIcon()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.createRegionImageIcon();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (ImportJButton.action.actionPerformed(java.awt.event.ActionEvent) --> ImageAttributePanel.done(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.done(arg1);
		// user code begin {2}
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
private void createRegionImageIcon() throws Exception{

	final int DISPLAY_DIM_MAX = 256;

	if(getImage() == null){
		throw new Exception("CreateRegionImageIcon error No Image");
	}
	
	try{
		// int RGB interpretation as follows:
		// int bits(32): (alpha)31-24,(red)23-16,(green)15-8,(blue)7-0
		// for alpha: 0-most transparent(see-through), 255-most opaque(solid)
		
		//Reset colormap (grayscale)
		for(int i=0;i<cmap.length;i+= 1){
			int iv = (int)(0x000000FF&i);
			cmap[i] = 0xFF<<24 | iv<<16 | iv<<8 | i;
		}
		//stretch cmap grays
		if(getImage() != null && getImage().getPixelClasses().length < 32){
			for(int i=0;i< getImage().getPixelClasses().length;i+= 1){
				int stretchIndex = (int)(0xFF&getImage().getPixelClasses()[i].getPixel());
				int newI = 32+(i*((256-32)/getImage().getPixelClasses().length));
				cmap[stretchIndex] = 0xFF<<24 | newI<<16 | newI<<8 | newI;
			}
		}
		//Highlight the current region
		if(getImage() != null && getCurrentPixelClassIndex() != null){
			int index = getImage().getPixelClasses(getCurrentPixelClassIndex().intValue()).getPixel();
			cmap[index] = java.awt.Color.red.getRGB();
		}

		//Make ColorModel, re-use colormap
		java.awt.image.IndexColorModel icm =
				new java.awt.image.IndexColorModel(8, cmap.length,cmap,0,  false /*false means NOT USE alpha*/   ,-1/*NO transparent single pixel*/, java.awt.image.DataBuffer.TYPE_BYTE);

		
		//Initialize image data
		if(pixelWR == null){
			cbit.image.VCImage sampledImage = getImage();
			double side = Math.sqrt(sampledImage.getNumX()*sampledImage.getNumY()*sampledImage.getNumZ());
			xSide = (int)Math.round(side/(double)sampledImage.getNumX());
			if(xSide == 0){xSide = 1;}
			if(xSide > sampledImage.getNumZ()){
				xSide = sampledImage.getNumZ();
			}
			ySide = (int)Math.ceil((double)sampledImage.getNumZ()/(double)xSide);
			if(ySide == 0){ySide = 1;}
			if(ySide > sampledImage.getNumZ()){
				ySide = sampledImage.getNumZ();
			}
			pixelWR = icm.createCompatibleWritableRaster(xSide*sampledImage.getNumX(),ySide*sampledImage.getNumY());
			byte[] sib = sampledImage.getPixels();

			//write the image to buffer
			int ystride = sampledImage.getNumX();
			int zstride = sampledImage.getNumX()*sampledImage.getNumY();
			for(int row=0;row < ySide;row+= 1){
				for(int col=0;col<xSide;col+= 1){
					int xoffset = col*sampledImage.getNumX();
					int yoffset = (row*sampledImage.getNumY());
					int zoffset = (col+(row*xSide))*zstride;
					if(zoffset >= sib.length){
						for(int x=0;x<sampledImage.getNumX();x+= 1){
							for(int y=0;y<sampledImage.getNumY();y+= 1){
								pixelWR.setSample(x+xoffset,y+yoffset,0,cmap.length-1);
							}
						}
					}else{
						for(int x=0;x<sampledImage.getNumX();x+= 1){
							for(int y=0;y<sampledImage.getNumY();y+= 1){
								pixelWR.setSample(x+xoffset,y+yoffset,0,(int)(0xFF&sib[x+(ystride*y)+zoffset]));
							}
						}
					}
				}
			}
			// scale if necessary
			displayScale = 1.0;
			if(pixelWR.getWidth() < DISPLAY_DIM_MAX || pixelWR.getHeight() < DISPLAY_DIM_MAX){
				displayScale = (int)Math.min((DISPLAY_DIM_MAX/pixelWR.getWidth()),(DISPLAY_DIM_MAX/pixelWR.getHeight()));
				if(displayScale == 0){displayScale = 1;}
			}
			if((displayScale == 1) && (pixelWR.getWidth() > DISPLAY_DIM_MAX || pixelWR.getHeight() > DISPLAY_DIM_MAX)){
				displayScale = Math.max((pixelWR.getWidth()/DISPLAY_DIM_MAX),(pixelWR.getHeight()/DISPLAY_DIM_MAX));
				if(displayScale == 0){displayScale = 1;}
				displayScale = 1.0/displayScale;
			}
			if(displayScale != 1){
				java.awt.geom.AffineTransform at = new java.awt.geom.AffineTransform();
				at.setToScale(displayScale,displayScale);
				java.awt.image.AffineTransformOp ato = new java.awt.image.AffineTransformOp(at,java.awt.image.AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				smallPixelWR = ato.createCompatibleDestRaster(pixelWR);
				ato.filter(pixelWR,smallPixelWR);
				getFullSizeJCheckBox().setEnabled(true);
			}else{
				getFullSizeJCheckBox().setEnabled(false);
			}
		}

		//Create display image, re-use image data and colormap
		if(pixelWR != null){
			java.awt.image.BufferedImage bi = null;
			if(!getFullSizeJCheckBox().isEnabled() || getFullSizeJCheckBox().isSelected()){
				bi = new java.awt.image.BufferedImage(icm,pixelWR,false,null);
			}else{
				bi = new java.awt.image.BufferedImage(icm,smallPixelWR,false,null);
			}
			
			javax.swing.ImageIcon rii = new javax.swing.ImageIcon(bi);

			getPixelClassImageLabel().setText(null);
			getPixelClassImageLabel().setIcon(rii);
		}else{
			getPixelClassImageLabel().setIcon(null);
			getPixelClassImageLabel().setText("No Image");
		}
	}catch(Throwable e){
		throw new Exception("CreateRegionImageIcon error\n"+(e.getMessage()!=null?e.getMessage():e.getClass().getName()));
	}
}

private Point2D.Double getStart(Graphics g){
	int width = ((ImageIcon)getPixelClassImageLabel().getIcon()).getIconWidth();
	int height = ((ImageIcon)getPixelClassImageLabel().getIcon()).getIconHeight();
	double startX = g.getClipBounds().x;
	double startY = g.getClipBounds().y;
	//Adjust for cases where IconImage is larger or smaller than panel display
	if(width < g.getClipBounds().width){
		startX+= (g.getClipBounds().width-width)/(double)2;
	}else{
		startX=0; 
	}
	if(height < g.getClipBounds().height){
		startY+= (g.getClipBounds().height-height)/(double)2;
	}else{
		startY = 0;
	}
	return new Point2D.Double(startX,startY);
}
private void drawAnnotations(Graphics g){
	int width = ((ImageIcon)getPixelClassImageLabel().getIcon()).getIconWidth();
	int height = ((ImageIcon)getPixelClassImageLabel().getIcon()).getIconHeight();
	Point2D.Double p2d = getStart(g);
	double startX = p2d.x;
	double startY = p2d.y;
	
	//Draw lines separating z-sections.
	//Draw z-section label n corner
	if(xSide > 0 || ySide > 0){
		double gridXBlockLen = (width/(double)xSide);
		double gridYBlockLen = (height/(double)ySide);
		
		g.setColor(java.awt.Color.green);
		// horiz lines
		for(int row=0;row < ySide;row+= 1){
			if(row > 0){
				g.drawLine((int)startX,(int)(startY+row*gridYBlockLen),(int)(startX+width),(int)(startY+row*gridYBlockLen));
			}
		}
		// vert lines
		for(int row=0;row < ySide;row+= 1){
			for(int col=0;col < xSide;col+= 1){
				if(col > 0){
					int zIndex = col+(row*xSide);
					if(zIndex <= getImage().getNumZ()){
						g.drawLine(
							(int)(startX+col*gridXBlockLen),
							(int)(startY+row*gridYBlockLen),
							(int)(startX+col*gridXBlockLen),
							(int)(startY+row*gridYBlockLen+gridYBlockLen));
					}
				}
			}
		}

		// z markers
		if(xSide > 1 || ySide > 1){
			for(int row=0;row < xSide;row+= 1){
				for(int col=0;col<ySide;col+= 1){
					int zIndex = row+(col*xSide);
					if(zIndex < getImage().getNumZ()){
						g.drawString(""+(1+zIndex),(int)(startX+row*gridXBlockLen+3),(int)(startY+col*gridYBlockLen+12));
					}
				}
			}
		}
	}

}

/**
 * Comment
 */
private void done(java.awt.event.ActionEvent actionEvent) {

//	if(actionEvent.getSource() == getImportJButton()){
//		try{
//			synchronize(getImage(),this);
//			setStatus(EditImageAttributes.STATUS_IMPORT);
//			getDialogParent().dispose();
//		}catch(Exception e){
//			PopupGenerator.showErrorDialog(this, 
//				"Error setting IMAGE values:\n"+(e.getMessage() != null?e.getMessage():e.getClass().getName()));
//		}
//	}else if(actionEvent.getSource() == getCancelJButton()){
//		setStatus(EditImageAttributes.STATUS_MANUAL_SEGMENT);
//	}else if(actionEvent.getSource() == getBtnCancel()){
//		setStatus(EditImageAttributes.STATUS_CANCEL);
//	}
	getDialogParent().dispose();
}
/**
 * Return the AnnotationJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAnnotationJLabel() {
	if (ivjAnnotationJLabel == null) {
		try {
			ivjAnnotationJLabel = new javax.swing.JLabel();
			ivjAnnotationJLabel.setName("AnnotationJLabel");
			ivjAnnotationJLabel.setText("Annotation");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnnotationJLabel;
}
/**
 * Return the JTextArea1 property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getAnnotationJTextArea() {
	if (ivjAnnotationJTextArea == null) {
		try {
			ivjAnnotationJTextArea = new javax.swing.JTextArea();
			ivjAnnotationJTextArea.setName("AnnotationJTextArea");
			ivjAnnotationJTextArea.setRows(3);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnnotationJTextArea;
}
/**
 * Return the CancelJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCancelJButton() {
	if (ivjCancelJButton == null) {
		try {
			ivjCancelJButton = new javax.swing.JButton();
			ivjCancelJButton.setName("CancelJButton");
			ivjCancelJButton.setText("Manual Segment......");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCancelJButton;
}
/**
 * Return the CurrentPixelClassIndex property value.
 * @return java.lang.Integer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.lang.Integer getCurrentPixelClassIndex() {
	// user code begin {1}
	// user code end
	return ivjCurrentPixelClassIndex;
}
/**
 * Gets the dialogParent property (javax.swing.JDialog) value.
 * @return The dialogParent property value.
 * @see #setDialogParent
 */
public javax.swing.JDialog getDialogParent() {
	return fieldDialogParent;
}
/**
 * Return the JCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getFullSizeJCheckBox() {
	if (ivjFullSizeJCheckBox == null) {
		try {
			ivjFullSizeJCheckBox = new javax.swing.JCheckBox();
			ivjFullSizeJCheckBox.setName("FullSizeJCheckBox");
			ivjFullSizeJCheckBox.setText("View Unscaled");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFullSizeJCheckBox;
}
/**
 * Gets the image property (cbit.image.VCImage) value.
 * @return The image property value.
 * @see #setImage
 */
public cbit.image.VCImage getImage() {
	return fieldImage;
}
/**
 * Return the ImportJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getImportJButton() {
	if (ivjImportJButton == null) {
		try {
			ivjImportJButton = new javax.swing.JButton();
			ivjImportJButton.setName("ImportJButton");
			ivjImportJButton.setText("Import");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImportJButton;
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
			ivjJLabel.setText("X:");
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
			ivjJLabel1.setText("Y:");
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
			ivjJLabel2.setText("Z:");
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
			ivjJLabel3.setText("Highlited in Color");
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

			java.awt.GridBagConstraints constraintsRegionNextJButton = new java.awt.GridBagConstraints();
			constraintsRegionNextJButton.gridx = 1; constraintsRegionNextJButton.gridy = 0;
			constraintsRegionNextJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getRegionNextJButton(), constraintsRegionNextJButton);

			java.awt.GridBagConstraints constraintsRegionPrevJButton = new java.awt.GridBagConstraints();
			constraintsRegionPrevJButton.gridx = 0; constraintsRegionPrevJButton.gridy = 0;
			constraintsRegionPrevJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getRegionPrevJButton(), constraintsRegionPrevJButton);
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

			java.awt.GridBagConstraints constraintsRegionCountJLabel = new java.awt.GridBagConstraints();
			constraintsRegionCountJLabel.gridx = 0; constraintsRegionCountJLabel.gridy = 0;
			constraintsRegionCountJLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRegionCountJLabel.weightx = 1.0;
			constraintsRegionCountJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getRegionCountJLabel(), constraintsRegionCountJLabel);

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 0;
			constraintsJLabel3.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getJLabel3(), constraintsJLabel3);
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
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsImportJButton = new java.awt.GridBagConstraints();
			constraintsImportJButton.gridx = 0; constraintsImportJButton.gridy = 0;
			constraintsImportJButton.insets = new Insets(4, 4, 4, 5);
			getJPanel3().add(getImportJButton(), constraintsImportJButton);

			java.awt.GridBagConstraints constraintsCancelJButton = new java.awt.GridBagConstraints();
			constraintsCancelJButton.gridx = 1; constraintsCancelJButton.gridy = 0;
			constraintsCancelJButton.insets = new Insets(4, 4, 4, 5);
			getJPanel3().add(getCancelJButton(), constraintsCancelJButton);
			GridBagConstraints gbc_btnCancel = new GridBagConstraints();
			gbc_btnCancel.gridx = 2;
			gbc_btnCancel.gridy = 0;
			ivjJPanel3.add(getBtnCancel(), gbc_btnCancel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			getJScrollPane1().setViewportView(getPixelClassImageLabel());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
/**
 * Return the MicronJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMicronJLabel() {
	if (ivjMicronJLabel == null) {
		try {
			ivjMicronJLabel = new javax.swing.JLabel();
			ivjMicronJLabel.setName("MicronJLabel");
			ivjMicronJLabel.setText("Total Size (microns):");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMicronJLabel;
}
/**
 * Return the PixelClassImageLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPixelClassImageLabel() {
	if (ivjPixelClassImageLabel == null) {
		try {
			ivjPixelClassImageLabel = new javax.swing.JLabel(){

				@Override
				protected void paintComponent(Graphics g) {
					// TODO Auto-generated method stub
					super.paintComponent(g);
					drawAnnotations(g);
				}
				
			};
			ivjPixelClassImageLabel.setName("PixelClassImageLabel");
			ivjPixelClassImageLabel.setText("No Image");
			ivjPixelClassImageLabel.setBounds(0, 0, 377, 254);
			ivjPixelClassImageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPixelClassImageLabel;
}
/**
 * Return the PixelSizeJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPixelSizeJLabel() {
	if (ivjPixelSizeJLabel == null) {
		try {
			ivjPixelSizeJLabel = new javax.swing.JLabel();
			ivjPixelSizeJLabel.setName("PixelSizeJLabel");
			ivjPixelSizeJLabel.setText("Pixel Count:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPixelSizeJLabel;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPixelSizeXJLabel() {
	if (ivjPixelSizeXJLabel == null) {
		try {
			ivjPixelSizeXJLabel = new javax.swing.JLabel();
			ivjPixelSizeXJLabel.setName("PixelSizeXJLabel");
			ivjPixelSizeXJLabel.setText("Pixel X");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPixelSizeXJLabel;
}
/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPixelSizeYJLabel() {
	if (ivjPixelSizeYJLabel == null) {
		try {
			ivjPixelSizeYJLabel = new javax.swing.JLabel();
			ivjPixelSizeYJLabel.setName("PixelSizeYJLabel");
			ivjPixelSizeYJLabel.setText("Pixel Y");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPixelSizeYJLabel;
}
/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPixelSizeZJLabel() {
	if (ivjPixelSizeZJLabel == null) {
		try {
			ivjPixelSizeZJLabel = new javax.swing.JLabel();
			ivjPixelSizeZJLabel.setName("PixelSizeZJLabel");
			ivjPixelSizeZJLabel.setText("Pixel Z");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPixelSizeZJLabel;
}
/**
 * Return the RegionCountJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRegionCountJLabel() {
	if (ivjRegionCountJLabel == null) {
		try {
			ivjRegionCountJLabel = new javax.swing.JLabel();
			ivjRegionCountJLabel.setName("RegionCountJLabel");
			ivjRegionCountJLabel.setText("Region 1 of X");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRegionCountJLabel;
}
/**
 * Return the RegionJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRegionJLabel() {
	if (ivjRegionJLabel == null) {
		try {
			ivjRegionJLabel = new javax.swing.JLabel();
			ivjRegionJLabel.setName("RegionJLabel");
			ivjRegionJLabel.setText("Defined Regions");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRegionJLabel;
}
/**
 * Return the RegionJPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRegionJPanel() {
	if (ivjRegionJPanel == null) {
		try {
			ivjRegionJPanel = new javax.swing.JPanel();
			ivjRegionJPanel.setName("RegionJPanel");
			ivjRegionJPanel.setBorder(new javax.swing.border.EtchedBorder());
			ivjRegionJPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsRegionNameJLabel = new java.awt.GridBagConstraints();
			constraintsRegionNameJLabel.gridx = 1; constraintsRegionNameJLabel.gridy = 0;
			constraintsRegionNameJLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsRegionNameJLabel.insets = new java.awt.Insets(4, 4, 4, 0);
			getRegionJPanel().add(getRegionNameJLabel(), constraintsRegionNameJLabel);

			java.awt.GridBagConstraints constraintsRegionNameJTextField = new java.awt.GridBagConstraints();
			constraintsRegionNameJTextField.gridx = 2; constraintsRegionNameJTextField.gridy = 0;
			constraintsRegionNameJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRegionNameJTextField.weightx = 1.0;
			constraintsRegionNameJTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getRegionJPanel().add(getRegionNameJTextField(), constraintsRegionNameJTextField);

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 2;
			constraintsJScrollPane1.gridwidth = 4;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
			getRegionJPanel().add(getJScrollPane1(), constraintsJScrollPane1);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
			constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getRegionJPanel().add(getJPanel1(), constraintsJPanel1);

			java.awt.GridBagConstraints constraintsFullSizeJCheckBox = new java.awt.GridBagConstraints();
			constraintsFullSizeJCheckBox.gridx = 0; constraintsFullSizeJCheckBox.gridy = 1;
			constraintsFullSizeJCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFullSizeJCheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getRegionJPanel().add(getFullSizeJCheckBox(), constraintsFullSizeJCheckBox);

			java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
			constraintsJPanel2.gridx = 1; constraintsJPanel2.gridy = 1;
			constraintsJPanel2.gridwidth = 2;
			constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel2.weightx = 1.0;
			constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getRegionJPanel().add(getJPanel2(), constraintsJPanel2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRegionJPanel;
}
/**
 * Return the RegionNameJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRegionNameJLabel() {
	if (ivjRegionNameJLabel == null) {
		try {
			ivjRegionNameJLabel = new javax.swing.JLabel();
			ivjRegionNameJLabel.setName("RegionNameJLabel");
			ivjRegionNameJLabel.setText("Name:");
			ivjRegionNameJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRegionNameJLabel;
}
/**
 * Return the RegionNameJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getRegionNameJTextField() {
	if (ivjRegionNameJTextField == null) {
		try {
			ivjRegionNameJTextField = new javax.swing.JTextField();
			ivjRegionNameJTextField.setName("RegionNameJTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRegionNameJTextField;
}
/**
 * Return the RegionNextJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRegionNextJButton() {
	if (ivjRegionNextJButton == null) {
		try {
			ivjRegionNextJButton = new javax.swing.JButton();
			ivjRegionNextJButton.setName("RegionNextJButton");
			ivjRegionNextJButton.setText("Next");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRegionNextJButton;
}
/**
 * Return the RegionPrevJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRegionPrevJButton() {
	if (ivjRegionPrevJButton == null) {
		try {
			ivjRegionPrevJButton = new javax.swing.JButton();
			ivjRegionPrevJButton.setName("RegionPrevJButton");
			ivjRegionPrevJButton.setText("Prev");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRegionPrevJButton;
}
/**
 * Gets the status property (java.lang.Object) value.
 * @return The status property value.
 * @see #setStatus
 */
public String getStatus() {
	return fieldStatus;
}
/**
 * Return the XMicronJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getXMicronJLabel() {
	if (ivjXMicronJLabel == null) {
		try {
			ivjXMicronJLabel = new javax.swing.JLabel();
			ivjXMicronJLabel.setName("XMicronJLabel");
			ivjXMicronJLabel.setText("X:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjXMicronJLabel;
}

private UndoableEditListener uel = 
	new UndoableEditListener() {
		private boolean bBusy = false;
		public synchronized void undoableEditHappened(UndoableEditEvent e) {
			if(bBusy || getImage() == null){
				return;
			}
			bBusy = true;
			try{
			if(e.getSource() == getXMicronJTextField().getDocument()){
				double perPixelMicron = -1;
				try{
					perPixelMicron = Double.parseDouble(getXMicronJTextField().getText());
					perPixelMicron/=getImage().getNumX();
				}catch(Exception exc){
					getXPerPixelJextField().setText("");
					return;
				}
				getXPerPixelJextField().setText(perPixelMicron+"");
			}else if(e.getSource() == getYMicronJTextField().getDocument()){
				double perPixelMicron = -1;
				try{
					perPixelMicron = Double.parseDouble(getYMicronJTextField().getText());
					perPixelMicron/=getImage().getNumY();
				}catch(Exception exc){
					getYPerPixelTextField().setText("");
					return;
				}
				getYPerPixelTextField().setText(perPixelMicron+"");
			}else if(e.getSource() == getZMicronJTextField().getDocument()){
				double perPixelMicron = -1;
				try{
					perPixelMicron = Double.parseDouble(getZMicronJTextField().getText());
					perPixelMicron/=getImage().getNumZ();
				}catch(Exception exc){
					getZPerPixelJextField().setText("");
					return;
				}
				getZPerPixelJextField().setText(perPixelMicron+"");
			}else if(e.getSource() == getZPerPixelJextField().getDocument()){
				double perPixelMicron = -1;
				try{
					perPixelMicron = Double.parseDouble(getZPerPixelJextField().getText());
					perPixelMicron*= getImage().getNumZ();
				}catch(Exception exc){
					getZMicronJTextField().setText("");
					return;
				}
				getZMicronJTextField().setText(perPixelMicron+"");
			}else if(e.getSource() == getYPerPixelTextField().getDocument()){
				double perPixelMicron = -1;
				try{
					perPixelMicron = Double.parseDouble(getYPerPixelTextField().getText());
					perPixelMicron*= getImage().getNumY();
				}catch(Exception exc){
					getYMicronJTextField().setText("");
					return;
				}
				getYMicronJTextField().setText(perPixelMicron+"");
			}else if(e.getSource() == getXPerPixelJextField().getDocument()){
				double perPixelMicron = -1;
				try{
					perPixelMicron = Double.parseDouble(getXPerPixelJextField().getText());
					perPixelMicron*= getImage().getNumX();
				}catch(Exception exc){
					getXMicronJTextField().setText("");
					return;
				}
				getXMicronJTextField().setText(perPixelMicron+"");
			}
			}catch(Exception exc){
				exc.printStackTrace();
			}finally{
				bBusy  = false;
			}

		}
	};
private JTextField xPerPixelJextField;
private JTextField yPerPixelTextField;
private JTextField zPerPixelJextField;
private JLabel ivjPerPixelMicronJLabel;
private JButton btnCancel;
/**
 * Return the XMicronJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getXMicronJTextField() {
	if (ivjXMicronJTextField == null) {
		try {
			ivjXMicronJTextField = new javax.swing.JTextField();
			ivjXMicronJTextField.getDocument().addUndoableEditListener(uel);
			ivjXMicronJTextField.setName("XMicronJTextField");
			ivjXMicronJTextField.setToolTipText("Microns for whole X axis");
			ivjXMicronJTextField.setText("1.0");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjXMicronJTextField;
}
/**
 * Return the YMicronJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getYMicronJLabel() {
	if (ivjYMicronJLabel == null) {
		try {
			ivjYMicronJLabel = new javax.swing.JLabel();
			ivjYMicronJLabel.setName("YMicronJLabel");
			ivjYMicronJLabel.setText("Y:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYMicronJLabel;
}
/**
 * Return the YMicronJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getYMicronJTextField() {
	if (ivjYMicronJTextField == null) {
		try {
			ivjYMicronJTextField = new javax.swing.JTextField();
			ivjYMicronJTextField.getDocument().addUndoableEditListener(uel);
			ivjYMicronJTextField.setName("YMicronJTextField");
			ivjYMicronJTextField.setToolTipText("Microns for Whole Y axis");
			ivjYMicronJTextField.setText("1.0");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYMicronJTextField;
}
/**
 * Return the ZMicronJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getZMicronJLabel() {
	if (ivjZMicronJLabel == null) {
		try {
			ivjZMicronJLabel = new javax.swing.JLabel();
			ivjZMicronJLabel.setName("ZMicronJLabel");
			ivjZMicronJLabel.setText("Z:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjZMicronJLabel;
}
/**
 * Return the ZMicronJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getZMicronJTextField() {
	if (ivjZMicronJTextField == null) {
		try {
			ivjZMicronJTextField = new javax.swing.JTextField();
			ivjZMicronJTextField.getDocument().addUndoableEditListener(uel);
			ivjZMicronJTextField.setName("ZMicronJTextField");
			ivjZMicronJTextField.setToolTipText("Microns for Whole Z axis");
			ivjZMicronJTextField.setText("1.0");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjZMicronJTextField;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	getImportJButton().setEnabled(false);
	
	PopupGenerator.showErrorDialog(this, 
		(exception.getMessage() != null?exception.getMessage():exception.getClass().getName())+"\n"+
		this.getClass().getName()+" internal error\n"+"Please Cancel and try again", exception
		);

	//exception.printStackTrace();
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getRegionNextJButton().addActionListener(ivjEventHandler);
	getRegionPrevJButton().addActionListener(ivjEventHandler);
	getImportJButton().addActionListener(ivjEventHandler);
	getCancelJButton().addActionListener(ivjEventHandler);
	getFullSizeJCheckBox().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AttributePanel");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0};
		setLayout(gridBagLayout);
		setSize(411, 522);

		java.awt.GridBagConstraints constraintsPixelSizeJLabel = new java.awt.GridBagConstraints();
		constraintsPixelSizeJLabel.gridx = 0; constraintsPixelSizeJLabel.gridy = 0;
		constraintsPixelSizeJLabel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsPixelSizeJLabel.insets = new Insets(4, 4, 4, 4);
		add(getPixelSizeJLabel(), constraintsPixelSizeJLabel);

		java.awt.GridBagConstraints constraintsMicronJLabel = new java.awt.GridBagConstraints();
		constraintsMicronJLabel.gridx = 0; constraintsMicronJLabel.gridy = 1;
		constraintsMicronJLabel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsMicronJLabel.insets = new Insets(4, 4, 4, 4);
		add(getMicronJLabel(), constraintsMicronJLabel);
		GridBagConstraints gbc_ivjPerPixelMicronJLabel = new GridBagConstraints();
		gbc_ivjPerPixelMicronJLabel.insets = new Insets(4, 4, 4, 4);
		gbc_ivjPerPixelMicronJLabel.gridx = 0;
		gbc_ivjPerPixelMicronJLabel.gridy = 2;
		add(getIvjPerPixelMicronJLabel(), gbc_ivjPerPixelMicronJLabel);
		GridBagConstraints gbc_xPerPixelJextField = new GridBagConstraints();
		gbc_xPerPixelJextField.insets = new Insets(4, 4, 5, 5);
		gbc_xPerPixelJextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_xPerPixelJextField.gridx = 2;
		gbc_xPerPixelJextField.gridy = 2;
		add(getXPerPixelJextField(), gbc_xPerPixelJextField);
		GridBagConstraints gbc_yPerPixelTextField = new GridBagConstraints();
		gbc_yPerPixelTextField.insets = new Insets(4, 4, 5, 5);
		gbc_yPerPixelTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_yPerPixelTextField.gridx = 4;
		gbc_yPerPixelTextField.gridy = 2;
		add(getYPerPixelTextField(), gbc_yPerPixelTextField);
		GridBagConstraints gbc_zPerPixelJextField = new GridBagConstraints();
		gbc_zPerPixelJextField.insets = new Insets(4, 4, 5, 4);
		gbc_zPerPixelJextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_zPerPixelJextField.gridx = 6;
		gbc_zPerPixelJextField.gridy = 2;
		add(getZPerPixelJextField(), gbc_zPerPixelJextField);

		java.awt.GridBagConstraints constraintsAnnotationJTextArea = new java.awt.GridBagConstraints();
		constraintsAnnotationJTextArea.gridx = 0; constraintsAnnotationJTextArea.gridy = 4;
		constraintsAnnotationJTextArea.gridwidth = 7;
		constraintsAnnotationJTextArea.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsAnnotationJTextArea.weightx = 1.0;
		constraintsAnnotationJTextArea.insets = new Insets(0, 4, 5, 4);
		add(getAnnotationJTextArea(), constraintsAnnotationJTextArea);

		java.awt.GridBagConstraints constraintsAnnotationJLabel = new java.awt.GridBagConstraints();
		constraintsAnnotationJLabel.gridx = 0; constraintsAnnotationJLabel.gridy = 3;
		constraintsAnnotationJLabel.gridwidth = 7;
		constraintsAnnotationJLabel.insets = new Insets(4, 4, 5, 4);
		add(getAnnotationJLabel(), constraintsAnnotationJLabel);

		java.awt.GridBagConstraints constraintsXMicronJTextField = new java.awt.GridBagConstraints();
		constraintsXMicronJTextField.gridx = 2; constraintsXMicronJTextField.gridy = 1;
		constraintsXMicronJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsXMicronJTextField.weightx = 1.0;
		constraintsXMicronJTextField.insets = new Insets(4, 4, 5, 5);
		add(getXMicronJTextField(), constraintsXMicronJTextField);

		java.awt.GridBagConstraints constraintsXMicronJLabel = new java.awt.GridBagConstraints();
		constraintsXMicronJLabel.gridx = 1; constraintsXMicronJLabel.gridy = 1;
		constraintsXMicronJLabel.insets = new Insets(4, 4, 5, 5);
		add(getXMicronJLabel(), constraintsXMicronJLabel);

		java.awt.GridBagConstraints constraintsYMicronJLabel = new java.awt.GridBagConstraints();
		constraintsYMicronJLabel.gridx = 3; constraintsYMicronJLabel.gridy = 1;
		constraintsYMicronJLabel.insets = new Insets(4, 4, 5, 5);
		add(getYMicronJLabel(), constraintsYMicronJLabel);

		java.awt.GridBagConstraints constraintsYMicronJTextField = new java.awt.GridBagConstraints();
		constraintsYMicronJTextField.gridx = 4; constraintsYMicronJTextField.gridy = 1;
		constraintsYMicronJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsYMicronJTextField.weightx = 1.0;
		constraintsYMicronJTextField.insets = new Insets(4, 4, 5, 5);
		add(getYMicronJTextField(), constraintsYMicronJTextField);

		java.awt.GridBagConstraints constraintsZMicronJLabel = new java.awt.GridBagConstraints();
		constraintsZMicronJLabel.gridx = 5; constraintsZMicronJLabel.gridy = 1;
		constraintsZMicronJLabel.insets = new Insets(4, 4, 5, 5);
		add(getZMicronJLabel(), constraintsZMicronJLabel);

		java.awt.GridBagConstraints constraintsZMicronJTextField = new java.awt.GridBagConstraints();
		constraintsZMicronJTextField.gridx = 6; constraintsZMicronJTextField.gridy = 1;
		constraintsZMicronJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsZMicronJTextField.weightx = 1.0;
		constraintsZMicronJTextField.insets = new Insets(4, 4, 5, 4);
		add(getZMicronJTextField(), constraintsZMicronJTextField);

		java.awt.GridBagConstraints constraintsRegionJLabel = new java.awt.GridBagConstraints();
		constraintsRegionJLabel.gridx = 0; constraintsRegionJLabel.gridy = 5;
		constraintsRegionJLabel.gridwidth = 7;
		constraintsRegionJLabel.weightx = 1.0;
		constraintsRegionJLabel.insets = new Insets(4, 4, 5, 4);
		add(getRegionJLabel(), constraintsRegionJLabel);

		java.awt.GridBagConstraints constraintsRegionJPanel = new java.awt.GridBagConstraints();
		constraintsRegionJPanel.gridx = 0; constraintsRegionJPanel.gridy = 6;
		constraintsRegionJPanel.gridwidth = 7;
		constraintsRegionJPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsRegionJPanel.weightx = 1.0;
		constraintsRegionJPanel.weighty = 1.0;
		constraintsRegionJPanel.insets = new Insets(0, 4, 5, 4);
		add(getRegionJPanel(), constraintsRegionJPanel);

		java.awt.GridBagConstraints constraintsJLabel = new java.awt.GridBagConstraints();
		constraintsJLabel.gridx = 1; constraintsJLabel.gridy = 0;
		constraintsJLabel.insets = new Insets(4, 4, 5, 5);
		add(getJLabel(), constraintsJLabel);

		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 3; constraintsJLabel1.gridy = 0;
		constraintsJLabel1.insets = new Insets(4, 4, 5, 5);
		add(getJLabel1(), constraintsJLabel1);

		java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
		constraintsJLabel2.gridx = 5; constraintsJLabel2.gridy = 0;
		constraintsJLabel2.insets = new Insets(4, 4, 5, 5);
		add(getJLabel2(), constraintsJLabel2);

		java.awt.GridBagConstraints constraintsPixelSizeXJLabel = new java.awt.GridBagConstraints();
		constraintsPixelSizeXJLabel.gridx = 2; constraintsPixelSizeXJLabel.gridy = 0;
		constraintsPixelSizeXJLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPixelSizeXJLabel.insets = new Insets(4, 4, 5, 5);
		add(getPixelSizeXJLabel(), constraintsPixelSizeXJLabel);

		java.awt.GridBagConstraints constraintsPixelSizeYJLabel = new java.awt.GridBagConstraints();
		constraintsPixelSizeYJLabel.gridx = 4; constraintsPixelSizeYJLabel.gridy = 0;
		constraintsPixelSizeYJLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPixelSizeYJLabel.insets = new Insets(4, 4, 5, 5);
		add(getPixelSizeYJLabel(), constraintsPixelSizeYJLabel);

		java.awt.GridBagConstraints constraintsPixelSizeZJLabel = new java.awt.GridBagConstraints();
		constraintsPixelSizeZJLabel.gridx = 6; constraintsPixelSizeZJLabel.gridy = 0;
		constraintsPixelSizeZJLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPixelSizeZJLabel.insets = new Insets(4, 4, 5, 4);
		add(getPixelSizeZJLabel(), constraintsPixelSizeZJLabel);

		java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
		constraintsJPanel3.gridx = 0; constraintsJPanel3.gridy = 7;
		constraintsJPanel3.gridwidth = 7;
		constraintsJPanel3.weightx = 1.0;
		constraintsJPanel3.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel3(), constraintsJPanel3);
		initConnections();
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
		ImageAttributePanel aCreateImagePanel;
		aCreateImagePanel = new ImageAttributePanel();
		frame.setContentPane(aCreateImagePanel);
		frame.setSize(aCreateImagePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
private void regionNextJButton_ActionPerformed() {
	
	try {
		saveRegionName();
		if(getImage() != null){
			if (getCurrentPixelClassIndex().intValue() >= getImage().getPixelClasses().length-1){
				setCurrentPixelClassIndex(new Integer(0));
			}else{
				setCurrentPixelClassIndex(new Integer(getCurrentPixelClassIndex().intValue()+1));
			}
			updateRegionCountLabel(getCurrentPixelClassIndex().intValue());
			getRegionNameJTextField().setText(vcPixelClassArr[getCurrentPixelClassIndex().intValue()].getPixelClassName());
			createRegionImageIcon();
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
private void regionPrevJButton_ActionPerformed() {
	
	try{
		saveRegionName();
		if(getImage() != null){
			if (getCurrentPixelClassIndex().intValue()==0){
				setCurrentPixelClassIndex(new Integer(getImage().getPixelClasses().length-1));
			}else{
				setCurrentPixelClassIndex(new Integer(getCurrentPixelClassIndex().intValue()-1));
			}
			updateRegionCountLabel(getCurrentPixelClassIndex().intValue());
			getRegionNameJTextField().setText(vcPixelClassArr[getCurrentPixelClassIndex().intValue()].getPixelClassName());
			createRegionImageIcon();
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
private void saveRegionName(){
	
	if(getCurrentPixelClassIndex() != null){
		int currentRegion = getCurrentPixelClassIndex().intValue();
		VCPixelClass orig = vcPixelClassArr[currentRegion];
		vcPixelClassArr[currentRegion] = new cbit.image.VCPixelClass(orig.getKey(),getRegionNameJTextField().getText(),orig.getPixel());
	}
}
/**
 * Set the CurrentPixelClassIndex to a new value.
 * @param newValue java.lang.Integer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setCurrentPixelClassIndex(java.lang.Integer newValue) {
	if (ivjCurrentPixelClassIndex != newValue) {
		try {
			ivjCurrentPixelClassIndex = newValue;
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
 * Sets the dialogParent property (javax.swing.JDialog) value.
 * @param dialogParent The new value for the property.
 * @see #getDialogParent
 */
public void setDialogParent(javax.swing.JDialog dialogParent) {
	javax.swing.JDialog oldValue = fieldDialogParent;
	fieldDialogParent = dialogParent;
	firePropertyChange("dialogParent", oldValue, dialogParent);
}
/**
 * Comment
 */
public void setImage(VCImage vcImage) throws Exception{

	pixelWR = null;
	smallPixelWR = null;
	xSide = 1;
	ySide = 1;
	displayScale = 1.0;
	vcPixelClassArr = null;
	//
	cbit.image.VCImage oldValue = fieldImage;
	fieldImage = vcImage;
	//
	if(vcImage != null){
		getImportJButton().setEnabled(true);		
		getPixelSizeXJLabel().setText((getImage() != null?getImage().getNumX()+"":null));
		getPixelSizeYJLabel().setText((getImage() != null?getImage().getNumY()+"":null));
		getPixelSizeZJLabel().setText((getImage() != null?getImage().getNumZ()+"":null));

		getXMicronJTextField().setText((getImage() != null?getImage().getExtent().getX()+"":null));
		String yExtent = (getImage() != null?(getImage().getNumY() > 1?getImage().getExtent().getY()+"":"1.0"):null);
		getYMicronJTextField().setText(yExtent);
		String zExtent = (getImage() != null?(getImage().getNumZ() > 1?getImage().getExtent().getZ()+"":"1.0"):null);
		getZMicronJTextField().setText(zExtent);

		getYMicronJTextField().setEnabled(vcImage.getNumY() > 1);
		getZMicronJTextField().setEnabled(vcImage.getNumZ() > 1);
		getYPerPixelTextField().setEnabled(vcImage.getNumY() > 1);
		getZPerPixelJextField().setEnabled(vcImage.getNumZ() > 1);

		getAnnotationJTextArea().setText((getImage() != null?getImage().getDescription():null));

		if(getImage() != null){
			cbit.image.VCPixelClass[] orig = getImage().getPixelClasses();
			cbit.image.VCPixelClass[] temp = new cbit.image.VCPixelClass[orig.length];
			for(int i=0;i<orig.length;i+= 1){
				temp[i] = new cbit.image.VCPixelClass(orig[i].getKey(),orig[i].getPixelClassName(),orig[i].getPixel());
			}
			vcPixelClassArr = temp;
		}else{
			vcPixelClassArr = null;
		}
		
		final int INIT_REGION = 0;
		setCurrentPixelClassIndex((getImage() != null?new Integer(INIT_REGION):null));
		getRegionNameJTextField().setText((getCurrentPixelClassIndex() != null && vcPixelClassArr != null?vcPixelClassArr[INIT_REGION].getPixelClassName():null));

		updateRegionCountLabel(INIT_REGION);

		getFullSizeJCheckBox().setSelected(false);

		createRegionImageIcon();
	}
	
	firePropertyChange("image", oldValue, vcImage);
}
/**
 * Sets the status property (java.lang.Object) value.
 * @param status The new value for the property.
 * @see #getStatus
 */
private void setStatus(String status) {
	String oldValue = fieldStatus;
	fieldStatus = status;
	firePropertyChange("status", oldValue, status);
}
/**
 * Comment
 */
public static void synchronize(VCImage syncVCImage,ImageAttributePanel imgAttrPanel) throws Exception{

	if(syncVCImage != null){
		
		//Set Description
		try{
			String newAnnot = imgAttrPanel.getAnnotationJTextArea().getText();
			if(newAnnot != null && newAnnot.length() == 0){newAnnot = null;}
			if(!org.vcell.util.Compare.isEqualOrNull(newAnnot,syncVCImage.getDescription())){
				syncVCImage.setDescription(newAnnot);
			}
		}catch(Throwable e){
			e.printStackTrace();
			throw new Exception("Error setting Annotation\n"+(e.getMessage() != null?e.getMessage():e.getClass().getName()));
		}
		
		//Set Extent
		try{
			double newX = Double.valueOf(imgAttrPanel.getXMicronJTextField().getText()).doubleValue();
			double newY = Double.valueOf(imgAttrPanel.getYMicronJTextField().getText()).doubleValue();
			double newZ = Double.valueOf(imgAttrPanel.getZMicronJTextField().getText()).doubleValue();
			Extent newExtent = new Extent(newX,newY,newZ);
			if(!newExtent.compareEqual(syncVCImage.getExtent())){syncVCImage.setExtent(newExtent);}
		}catch(Throwable e){
			e.printStackTrace();
			throw new Exception("Error setting extent\n"+(e.getMessage() != null?e.getMessage():e.getClass().getName()));
		}
		
		//Set VCPixelClass
		try{
			imgAttrPanel.saveRegionName();
			//int currentPCIndex = getCurrentPixelClassIndex().intValue();
			//vcPixelClassArr[currentPCIndex] =
				//new VCPixelClass(vcPixelClassArr[currentPCIndex].getKey(),getRegionNameJTextField().getText(),vcPixelClassArr[currentPCIndex].getPixel());
			syncVCImage.setPixelClasses(imgAttrPanel.vcPixelClassArr);
		}catch(Throwable e){
			e.printStackTrace();
			throw new Exception("Error setting PixelClass names\n"+(e.getMessage() != null?e.getMessage():e.getClass().getName()));
		}
}
}
/**
 * Insert the method's description here.
 * Creation date: (10/13/2004 12:45:31 PM)
 * @param currentRegion int
 * @param regionCount int
 */
private void updateRegionCountLabel(int currentRegionIndex) {

	getRegionCountJLabel().setText((vcPixelClassArr != null?"(Region "+(currentRegionIndex+1)+" of "+vcPixelClassArr.length+")":null));
}

	private JTextField getXPerPixelJextField() {
		if (xPerPixelJextField == null) {
			xPerPixelJextField = new JTextField();
			xPerPixelJextField.getDocument().addUndoableEditListener(uel);
			xPerPixelJextField.setColumns(10);
		}
		return xPerPixelJextField;
	}
	private JTextField getYPerPixelTextField() {
		if (yPerPixelTextField == null) {
			yPerPixelTextField = new JTextField();
			yPerPixelTextField.getDocument().addUndoableEditListener(uel);
			yPerPixelTextField.setColumns(10);
		}
		return yPerPixelTextField;
	}
	private JTextField getZPerPixelJextField() {
		if (zPerPixelJextField == null) {
			zPerPixelJextField = new JTextField();
			zPerPixelJextField.getDocument().addUndoableEditListener(uel);
			zPerPixelJextField.setColumns(10);
		}
		return zPerPixelJextField;
	}
	private JLabel getIvjPerPixelMicronJLabel() {
		if (ivjPerPixelMicronJLabel == null) {
			ivjPerPixelMicronJLabel = new JLabel();
			ivjPerPixelMicronJLabel.setText("Pixel Size (microns):");
			ivjPerPixelMicronJLabel.setName("MicronJLabel");
		}
		return ivjPerPixelMicronJLabel;
	}
	private JButton getBtnCancel() {
		if (btnCancel == null) {
			btnCancel = new JButton("Cancel");
			btnCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					done(e);
				}
			});
		}
		return btnCancel;
	}
}

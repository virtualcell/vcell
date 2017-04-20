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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

import cbit.vcell.client.PopupGenerator;
/**
 * Insert the type's description here.
 * Creation date: (6/10/2002 3:26:22 PM)
 * @author: Frank Morgan
 */
public class CopyOfImageAttributePanel extends javax.swing.JPanel {
//	//
//	cbit.image.VCPixelClass[] vcPixelClassArr;
//	//
//	private java.awt.image.WritableRaster pixelWR = null;
//	private java.awt.image.WritableRaster smallPixelWR = null;
//	private int xSide;
//	private int ySide;
//	private double displayScale;
//	private int[] cmap = new int[256];
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
//	private javax.swing.JPanel ivjRegionJPanel = null;
//	private javax.swing.JLabel ivjRegionCountJLabel = null;
//	private javax.swing.JLabel ivjRegionNameJLabel = null;
//	private javax.swing.JTextField ivjRegionNameJTextField = null;
//	private javax.swing.JButton ivjRegionNextJButton = null;
//	private javax.swing.JButton ivjRegionPrevJButton = null;
	private javax.swing.JLabel ivjJLabel = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjPixelSizeXJLabel = null;
	private javax.swing.JLabel ivjPixelSizeYJLabel = null;
	private javax.swing.JLabel ivjPixelSizeZJLabel = null;
//	private cbit.image.VCImage fieldImage = null;
//	IvjEventHandler ivjEventHandler = new IvjEventHandler();
//	private javax.swing.JCheckBox ivjFullSizeJCheckBox = null;
//	private javax.swing.JPanel ivjJPanel1 = null;
//	private javax.swing.JPanel ivjJPanel2 = null;
//	private Integer ivjCurrentPixelClassIndex = null;
//	private javax.swing.JButton ivjCancelJButton = null;
//	private javax.swing.JButton ivjImportJButton = null;
//	private javax.swing.JPanel ivjJPanel3 = null;
//	private javax.swing.JDialog fieldDialogParent = new javax.swing.JDialog();
//	private String fieldStatus;
//	private javax.swing.JLabel ivjJLabel3 = null;

//class IvjEventHandler implements java.awt.event.ActionListener {
//		public void actionPerformed(java.awt.event.ActionEvent e) {
////			if (e.getSource() == CopyOfImageAttributePanel.this.getRegionNextJButton()) 
////				connEtoC2(e);
////			if (e.getSource() == CopyOfImageAttributePanel.this.getRegionPrevJButton()) 
////				connEtoC1(e);
////			if (e.getSource() == CopyOfImageAttributePanel.this.getImportJButton()) 
////				connEtoC9(e);
////			if (e.getSource() == CopyOfImageAttributePanel.this.getCancelJButton()) 
////				connEtoC10(e);
//			if (e.getSource() == CopyOfImageAttributePanel.this.getFullSizeJCheckBox()) 
//				connEtoC3(e);
//		};
//	};
/**
 * CreateImagePanel constructor comment.
 */
public CopyOfImageAttributePanel() {
	super();
	initialize();
}
///**
// * connEtoC1:  (RegionPrevJButton.action.actionPerformed(java.awt.event.ActionEvent) --> ImageAttributePanel.regionPrevJButton_ActionPerformed()V)
// * @param arg1 java.awt.event.ActionEvent
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private void connEtoC1(java.awt.event.ActionEvent arg1) {
//	try {
//		// user code begin {1}
//		// user code end
//		this.regionPrevJButton_ActionPerformed();
//		// user code begin {2}
//		// user code end
//	} catch (java.lang.Throwable ivjExc) {
//		// user code begin {3}
//		// user code end
//		handleException(ivjExc);
//	}
//}
///**
// * connEtoC10:  (CancelJButton.action.actionPerformed(java.awt.event.ActionEvent) --> ImageAttributePanel.done(Ljava.awt.event.ActionEvent;)V)
// * @param arg1 java.awt.event.ActionEvent
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private void connEtoC10(java.awt.event.ActionEvent arg1) {
//	try {
//		// user code begin {1}
//		// user code end
//		this.done(arg1);
//		// user code begin {2}
//		// user code end
//	} catch (java.lang.Throwable ivjExc) {
//		// user code begin {3}
//		// user code end
//		handleException(ivjExc);
//	}
//}
///**
// * connEtoC2:  (RegionNextJButton.action.actionPerformed(java.awt.event.ActionEvent) --> ImageAttributePanel.regionNextJButton_ActionPerformed()V)
// * @param arg1 java.awt.event.ActionEvent
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private void connEtoC2(java.awt.event.ActionEvent arg1) {
//	try {
//		// user code begin {1}
//		// user code end
//		this.regionNextJButton_ActionPerformed();
//		// user code begin {2}
//		// user code end
//	} catch (java.lang.Throwable ivjExc) {
//		// user code begin {3}
//		// user code end
//		handleException(ivjExc);
//	}
//}
///**
// * connEtoC3:  (FullSizeJCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> ImageAttributePanel.createRegionImageIcon()V)
// * @param arg1 java.awt.event.ActionEvent
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private void connEtoC3(java.awt.event.ActionEvent arg1) {
//	try {
//		// user code begin {1}
//		// user code end
//		this.createRegionImageIcon();
//		// user code begin {2}
//		// user code end
//	} catch (java.lang.Throwable ivjExc) {
//		// user code begin {3}
//		// user code end
//		handleException(ivjExc);
//	}
//}
///**
// * connEtoC9:  (ImportJButton.action.actionPerformed(java.awt.event.ActionEvent) --> ImageAttributePanel.done(Ljava.awt.event.ActionEvent;)V)
// * @param arg1 java.awt.event.ActionEvent
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private void connEtoC9(java.awt.event.ActionEvent arg1) {
//	try {
//		// user code begin {1}
//		// user code end
//		this.done(arg1);
//		// user code begin {2}
//		// user code end
//	} catch (java.lang.Throwable ivjExc) {
//		// user code begin {3}
//		// user code end
//		handleException(ivjExc);
//	}
//}
/**
 * Comment
 */
//private void createRegionImageIcon() throws Exception{
//
//	final int DISPLAY_DIM_MAX = 256;
//
//	if(getImage() == null){
//		throw new Exception("CreateRegionImageIcon error No Image");
//	}
//	
//	try{
//		// int RGB interpretation as follows:
//		// int bits(32): (alpha)31-24,(red)23-16,(green)15-8,(blue)7-0
//		// for alpha: 0-most transparent(see-through), 255-most opaque(solid)
//		
//		//Reset colormap (grayscale)
//		for(int i=0;i<cmap.length;i+= 1){
//			int iv = (int)(0x000000FF&i);
//			cmap[i] = 0xFF<<24 | iv<<16 | iv<<8 | i;
//		}
//		//stretch cmap grays
//		if(getImage() != null && getImage().getPixelClasses().length < 32){
//			for(int i=0;i< getImage().getPixelClasses().length;i+= 1){
//				int stretchIndex = (int)(0xFF&getImage().getPixelClasses()[i].getPixel());
//				int newI = 32+(i*((256-32)/getImage().getPixelClasses().length));
//				cmap[stretchIndex] = 0xFF<<24 | newI<<16 | newI<<8 | newI;
//			}
//		}
//		//Highlight the current region
//		if(getImage() != null && getCurrentPixelClassIndex() != null){
//			int index = getImage().getPixelClasses(getCurrentPixelClassIndex().intValue()).getPixel();
//			cmap[index] = java.awt.Color.red.getRGB();
//		}
//
//		//Make ColorModel, re-use colormap
//		java.awt.image.IndexColorModel icm =
//				new java.awt.image.IndexColorModel(8, cmap.length,cmap,0,  false /*false means NOT USE alpha*/   ,-1/*NO transparent single pixel*/, java.awt.image.DataBuffer.TYPE_BYTE);
//
//		
//		//Initialize image data
//		if(pixelWR == null){
//			cbit.image.VCImage sampledImage = getImage();
//			double side = Math.sqrt(sampledImage.getNumX()*sampledImage.getNumY()*sampledImage.getNumZ());
//			xSide = (int)Math.round(side/(double)sampledImage.getNumX());
//			if(xSide == 0){xSide = 1;}
//			if(xSide > sampledImage.getNumZ()){
//				xSide = sampledImage.getNumZ();
//			}
//			ySide = (int)Math.ceil((double)sampledImage.getNumZ()/(double)xSide);
//			if(ySide == 0){ySide = 1;}
//			if(ySide > sampledImage.getNumZ()){
//				ySide = sampledImage.getNumZ();
//			}
//			pixelWR = icm.createCompatibleWritableRaster(xSide*sampledImage.getNumX(),ySide*sampledImage.getNumY());
//			byte[] sib = sampledImage.getPixels();
//
//			//write the image to buffer
//			int ystride = sampledImage.getNumX();
//			int zstride = sampledImage.getNumX()*sampledImage.getNumY();
//			for(int row=0;row < ySide;row+= 1){
//				for(int col=0;col<xSide;col+= 1){
//					int xoffset = col*sampledImage.getNumX();
//					int yoffset = (row*sampledImage.getNumY());
//					int zoffset = (col+(row*xSide))*zstride;
//					if(zoffset >= sib.length){
//						for(int x=0;x<sampledImage.getNumX();x+= 1){
//							for(int y=0;y<sampledImage.getNumY();y+= 1){
//								pixelWR.setSample(x+xoffset,y+yoffset,0,cmap.length-1);
//							}
//						}
//					}else{
//						for(int x=0;x<sampledImage.getNumX();x+= 1){
//							for(int y=0;y<sampledImage.getNumY();y+= 1){
//								pixelWR.setSample(x+xoffset,y+yoffset,0,(int)(0xFF&sib[x+(ystride*y)+zoffset]));
//							}
//						}
//					}
//				}
//			}
//			// scale if necessary
//			displayScale = 1.0;
//			if(pixelWR.getWidth() < DISPLAY_DIM_MAX || pixelWR.getHeight() < DISPLAY_DIM_MAX){
//				displayScale = (int)Math.min((DISPLAY_DIM_MAX/pixelWR.getWidth()),(DISPLAY_DIM_MAX/pixelWR.getHeight()));
//				if(displayScale == 0){displayScale = 1;}
//			}
//			if((displayScale == 1) && (pixelWR.getWidth() > DISPLAY_DIM_MAX || pixelWR.getHeight() > DISPLAY_DIM_MAX)){
//				displayScale = Math.max((pixelWR.getWidth()/DISPLAY_DIM_MAX),(pixelWR.getHeight()/DISPLAY_DIM_MAX));
//				if(displayScale == 0){displayScale = 1;}
//				displayScale = 1.0/displayScale;
//			}
//			if(displayScale != 1){
//				java.awt.geom.AffineTransform at = new java.awt.geom.AffineTransform();
//				at.setToScale(displayScale,displayScale);
//				java.awt.image.AffineTransformOp ato = new java.awt.image.AffineTransformOp(at,java.awt.image.AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
//				smallPixelWR = ato.createCompatibleDestRaster(pixelWR);
//				ato.filter(pixelWR,smallPixelWR);
//				getFullSizeJCheckBox().setEnabled(true);
//			}else{
//				getFullSizeJCheckBox().setEnabled(false);
//			}
//		}
//
//		//Create display image, re-use image data and colormap
//		if(pixelWR != null){
//			java.awt.image.BufferedImage bi = null;
//			if(!getFullSizeJCheckBox().isEnabled() || getFullSizeJCheckBox().isSelected()){
//				bi = new java.awt.image.BufferedImage(icm,pixelWR,false,null);
//			}else{
//				bi = new java.awt.image.BufferedImage(icm,smallPixelWR,false,null);
//			}
//			
//			javax.swing.ImageIcon rii = new javax.swing.ImageIcon(bi);
//
//			getPixelClassImageLabel().setText(null);
//			getPixelClassImageLabel().setIcon(rii);
//		}else{
//			getPixelClassImageLabel().setIcon(null);
//			getPixelClassImageLabel().setText("No Image");
//		}
//	}catch(Throwable e){
//		throw new Exception("CreateRegionImageIcon error\n"+(e.getMessage()!=null?e.getMessage():e.getClass().getName()));
//	}
//}


//private Point2D.Double getStart(Graphics g){
//	int width = ((ImageIcon)getPixelClassImageLabel().getIcon()).getIconWidth();
//	int height = ((ImageIcon)getPixelClassImageLabel().getIcon()).getIconHeight();
//	double startX = g.getClipBounds().x;
//	double startY = g.getClipBounds().y;
//	//Adjust for cases where IconImage is larger or smaller than panel display
//	if(width < g.getClipBounds().width){
//		startX+= (g.getClipBounds().width-width)/(double)2;
//	}else{
//		startX=0; 
//	}
//	if(height < g.getClipBounds().height){
//		startY+= (g.getClipBounds().height-height)/(double)2;
//	}else{
//		startY = 0;
//	}
//	return new Point2D.Double(startX,startY);
//}
//private void drawAnnotations(Graphics g){
//	int width = ((ImageIcon)getPixelClassImageLabel().getIcon()).getIconWidth();
//	int height = ((ImageIcon)getPixelClassImageLabel().getIcon()).getIconHeight();
//	Point2D.Double p2d = getStart(g);
//	double startX = p2d.x;
//	double startY = p2d.y;
//	
//	//Draw lines separating z-sections.
//	//Draw z-section label n corner
//	if(xSide > 0 || ySide > 0){
//		double gridXBlockLen = (width/(double)xSide);
//		double gridYBlockLen = (height/(double)ySide);
//		
//		g.setColor(java.awt.Color.green);
//		// horiz lines
//		for(int row=0;row < ySide;row+= 1){
//			if(row > 0){
//				g.drawLine((int)startX,(int)(startY+row*gridYBlockLen),(int)(startX+width),(int)(startY+row*gridYBlockLen));
//			}
//		}
//		// vert lines
//		for(int row=0;row < ySide;row+= 1){
//			for(int col=0;col < xSide;col+= 1){
//				if(col > 0){
//					int zIndex = col+(row*xSide);
//					if(zIndex <= isize.getZ()){
//						g.drawLine(
//							(int)(startX+col*gridXBlockLen),
//							(int)(startY+row*gridYBlockLen),
//							(int)(startX+col*gridXBlockLen),
//							(int)(startY+row*gridYBlockLen+gridYBlockLen));
//					}
//				}
//			}
//		}
//
//		// z markers
//		if(xSide > 1 || ySide > 1){
//			for(int row=0;row < xSide;row+= 1){
//				for(int col=0;col<ySide;col+= 1){
//					int zIndex = row+(col*xSide);
//					if(zIndex < isize.getZ()){
//						g.drawString(""+(1+zIndex),(int)(startX+row*gridXBlockLen+3),(int)(startY+col*gridYBlockLen+12));
//					}
//				}
//			}
//		}
//	}
//
//}

///**
// * Comment
// */
//private void done(java.awt.event.ActionEvent actionEvent) {
//
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
//	getDialogParent().dispose();
//}
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
///**
// * Return the CancelJButton property value.
// * @return javax.swing.JButton
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private javax.swing.JButton getCancelJButton() {
//	if (ivjCancelJButton == null) {
//		try {
//			ivjCancelJButton = new javax.swing.JButton();
//			ivjCancelJButton.setName("CancelJButton");
//			ivjCancelJButton.setText("Manual Segment......");
//			// user code begin {1}
//			// user code end
//		} catch (java.lang.Throwable ivjExc) {
//			// user code begin {2}
//			// user code end
//			handleException(ivjExc);
//		}
//	}
//	return ivjCancelJButton;
//}
/**
 * Return the CurrentPixelClassIndex property value.
 * @return java.lang.Integer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
//private java.lang.Integer getCurrentPixelClassIndex() {
//	// user code begin {1}
//	// user code end
//	return ivjCurrentPixelClassIndex;
//}
///**
// * Gets the dialogParent property (javax.swing.JDialog) value.
// * @return The dialogParent property value.
// * @see #setDialogParent
// */
//public javax.swing.JDialog getDialogParent() {
//	return fieldDialogParent;
//}
/**
 * Return the JCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
//private javax.swing.JCheckBox getFullSizeJCheckBox() {
//	if (ivjFullSizeJCheckBox == null) {
//		try {
//			ivjFullSizeJCheckBox = new javax.swing.JCheckBox();
//			ivjFullSizeJCheckBox.setName("FullSizeJCheckBox");
//			ivjFullSizeJCheckBox.setText("View Unscaled");
//			// user code begin {1}
//			// user code end
//		} catch (java.lang.Throwable ivjExc) {
//			// user code begin {2}
//			// user code end
//			handleException(ivjExc);
//		}
//	}
//	return ivjFullSizeJCheckBox;
//}
///**
// * Gets the image property (cbit.image.VCImage) value.
// * @return The image property value.
// * @see #setImage
// */
//public cbit.image.VCImage getImage() {
//	return fieldImage;
//}
///**
// * Return the ImportJButton property value.
// * @return javax.swing.JButton
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private javax.swing.JButton getImportJButton() {
//	if (ivjImportJButton == null) {
//		try {
//			ivjImportJButton = new javax.swing.JButton();
//			ivjImportJButton.setName("ImportJButton");
//			ivjImportJButton.setText("Import");
//			// user code begin {1}
//			// user code end
//		} catch (java.lang.Throwable ivjExc) {
//			// user code begin {2}
//			// user code end
//			handleException(ivjExc);
//		}
//	}
//	return ivjImportJButton;
//}
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
///**
// * Return the JLabel3 property value.
// * @return javax.swing.JLabel
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private javax.swing.JLabel getJLabel3() {
//	if (ivjJLabel3 == null) {
//		try {
//			ivjJLabel3 = new javax.swing.JLabel();
//			ivjJLabel3.setName("JLabel3");
//			ivjJLabel3.setText("Highlited in Color");
//			// user code begin {1}
//			// user code end
//		} catch (java.lang.Throwable ivjExc) {
//			// user code begin {2}
//			// user code end
//			handleException(ivjExc);
//		}
//	}
//	return ivjJLabel3;
//}
///**
// * Return the JPanel1 property value.
// * @return javax.swing.JPanel
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private javax.swing.JPanel getJPanel1() {
//	if (ivjJPanel1 == null) {
//		try {
//			ivjJPanel1 = new javax.swing.JPanel();
//			ivjJPanel1.setName("JPanel1");
//			ivjJPanel1.setLayout(new java.awt.GridBagLayout());
//
//			java.awt.GridBagConstraints constraintsRegionNextJButton = new java.awt.GridBagConstraints();
//			constraintsRegionNextJButton.gridx = 1; constraintsRegionNextJButton.gridy = 0;
//			constraintsRegionNextJButton.insets = new java.awt.Insets(4, 4, 4, 4);
//			getJPanel1().add(getRegionNextJButton(), constraintsRegionNextJButton);
//
//			java.awt.GridBagConstraints constraintsRegionPrevJButton = new java.awt.GridBagConstraints();
//			constraintsRegionPrevJButton.gridx = 0; constraintsRegionPrevJButton.gridy = 0;
//			constraintsRegionPrevJButton.insets = new java.awt.Insets(4, 4, 4, 4);
//			getJPanel1().add(getRegionPrevJButton(), constraintsRegionPrevJButton);
//			// user code begin {1}
//			// user code end
//		} catch (java.lang.Throwable ivjExc) {
//			// user code begin {2}
//			// user code end
//			handleException(ivjExc);
//		}
//	}
//	return ivjJPanel1;
//}
///**
// * Return the JPanel2 property value.
// * @return javax.swing.JPanel
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private javax.swing.JPanel getJPanel2() {
//	if (ivjJPanel2 == null) {
//		try {
//			ivjJPanel2 = new javax.swing.JPanel();
//			ivjJPanel2.setName("JPanel2");
//			ivjJPanel2.setLayout(new java.awt.GridBagLayout());
//
//			java.awt.GridBagConstraints constraintsRegionCountJLabel = new java.awt.GridBagConstraints();
//			constraintsRegionCountJLabel.gridx = 0; constraintsRegionCountJLabel.gridy = 0;
//			constraintsRegionCountJLabel.anchor = java.awt.GridBagConstraints.WEST;
//			constraintsRegionCountJLabel.weightx = 1.0;
//			constraintsRegionCountJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
//			getJPanel2().add(getRegionCountJLabel(), constraintsRegionCountJLabel);
//
//			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
//			constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 0;
//			constraintsJLabel3.anchor = java.awt.GridBagConstraints.EAST;
//			constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
//			getJPanel2().add(getJLabel3(), constraintsJLabel3);
//			// user code begin {1}
//			// user code end
//		} catch (java.lang.Throwable ivjExc) {
//			// user code begin {2}
//			// user code end
//			handleException(ivjExc);
//		}
//	}
//	return ivjJPanel2;
//}


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
//private javax.swing.JLabel getRegionCountJLabel() {
//	if (ivjRegionCountJLabel == null) {
//		try {
//			ivjRegionCountJLabel = new javax.swing.JLabel();
//			ivjRegionCountJLabel.setName("RegionCountJLabel");
//			ivjRegionCountJLabel.setText("Region 1 of X");
//			// user code begin {1}
//			// user code end
//		} catch (java.lang.Throwable ivjExc) {
//			// user code begin {2}
//			// user code end
//			handleException(ivjExc);
//		}
//	}
//	return ivjRegionCountJLabel;
//}
///**
// * Return the RegionJPanel property value.
// * @return javax.swing.JPanel
// */
///**
// * Return the RegionNameJLabel property value.
// * @return javax.swing.JLabel
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private javax.swing.JLabel getRegionNameJLabel() {
//	if (ivjRegionNameJLabel == null) {
//		try {
//			ivjRegionNameJLabel = new javax.swing.JLabel();
//			ivjRegionNameJLabel.setName("RegionNameJLabel");
//			ivjRegionNameJLabel.setText("Name:");
//			ivjRegionNameJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
//			// user code begin {1}
//			// user code end
//		} catch (java.lang.Throwable ivjExc) {
//			// user code begin {2}
//			// user code end
//			handleException(ivjExc);
//		}
//	}
//	return ivjRegionNameJLabel;
//}
/**
 * Return the RegionNameJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
//private javax.swing.JTextField getRegionNameJTextField() {
//	if (ivjRegionNameJTextField == null) {
//		try {
//			ivjRegionNameJTextField = new javax.swing.JTextField();
//			ivjRegionNameJTextField.setName("RegionNameJTextField");
//			// user code begin {1}
//			// user code end
//		} catch (java.lang.Throwable ivjExc) {
//			// user code begin {2}
//			// user code end
//			handleException(ivjExc);
//		}
//	}
//	return ivjRegionNameJTextField;
//}
/**
 * Return the RegionNextJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
//private javax.swing.JButton getRegionNextJButton() {
//	if (ivjRegionNextJButton == null) {
//		try {
//			ivjRegionNextJButton = new javax.swing.JButton();
//			ivjRegionNextJButton.setName("RegionNextJButton");
//			ivjRegionNextJButton.setText("Next");
//			// user code begin {1}
//			// user code end
//		} catch (java.lang.Throwable ivjExc) {
//			// user code begin {2}
//			// user code end
//			handleException(ivjExc);
//		}
//	}
//	return ivjRegionNextJButton;
//}
/**
 * Return the RegionPrevJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
//private javax.swing.JButton getRegionPrevJButton() {
//	if (ivjRegionPrevJButton == null) {
//		try {
//			ivjRegionPrevJButton = new javax.swing.JButton();
//			ivjRegionPrevJButton.setName("RegionPrevJButton");
//			ivjRegionPrevJButton.setText("Prev");
//			// user code begin {1}
//			// user code end
//		} catch (java.lang.Throwable ivjExc) {
//			// user code begin {2}
//			// user code end
//			handleException(ivjExc);
//		}
//	}
//	return ivjRegionPrevJButton;
//}
///**
// * Gets the status property (java.lang.Object) value.
// * @return The status property value.
// * @see #setStatus
// */
//public String getStatus() {
//	return fieldStatus;
//}
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
private Origin origin;
private Extent extent;
private ISize isize;
private JLabel originJLabel;
private JTextField originZTextField;
private JTextField originYTextField;
private JTextField originXtextField;
//private JButton btnCancel;
/**
 * Return the XMicronJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getXMicronJTextField() {
	if (ivjXMicronJTextField == null) {
		try {
			ivjXMicronJTextField = new javax.swing.JTextField();
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

//	getImportJButton().setEnabled(false);
	
	PopupGenerator.showErrorDialog(this, 
		(exception.getMessage() != null?exception.getMessage():exception.getClass().getName())+"\n"+
		this.getClass().getName()+" internal error\n"+"Please Cancel and try again", exception
		);

	//exception.printStackTrace();
}
///**
// * Initializes connections
// * @exception java.lang.Exception The exception description.
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private void initConnections() throws java.lang.Exception {
//	// user code begin {1}
//	// user code end
//	getRegionNextJButton().addActionListener(ivjEventHandler);
//	getRegionPrevJButton().addActionListener(ivjEventHandler);
//	getImportJButton().addActionListener(ivjEventHandler);
//	getCancelJButton().addActionListener(ivjEventHandler);
//	getFullSizeJCheckBox().addActionListener(ivjEventHandler);
//}
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
		setSize(411, 264);

		java.awt.GridBagConstraints constraintsPixelSizeJLabel = new java.awt.GridBagConstraints();
		constraintsPixelSizeJLabel.gridx = 0; constraintsPixelSizeJLabel.gridy = 0;
		constraintsPixelSizeJLabel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsPixelSizeJLabel.insets = new Insets(4, 4, 5, 5);
		add(getPixelSizeJLabel(), constraintsPixelSizeJLabel);

		java.awt.GridBagConstraints constraintsMicronJLabel = new java.awt.GridBagConstraints();
		constraintsMicronJLabel.gridx = 0; constraintsMicronJLabel.gridy = 1;
		constraintsMicronJLabel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsMicronJLabel.insets = new Insets(4, 4, 5, 5);
		add(getMicronJLabel(), constraintsMicronJLabel);
		GridBagConstraints gbc_originJLabel = new GridBagConstraints();
		gbc_originJLabel.anchor = GridBagConstraints.EAST;
		gbc_originJLabel.insets = new Insets(0, 0, 5, 5);
		gbc_originJLabel.gridx = 0;
		gbc_originJLabel.gridy = 2;
		add(getOriginJLabel(), gbc_originJLabel);
		
		GridBagConstraints gbc_000 = new GridBagConstraints();
		gbc_000.insets = new Insets(4, 4, 5, 5);
		gbc_000.fill = GridBagConstraints.HORIZONTAL;
		gbc_000.gridx = 1;
		gbc_000.gridy = 2;
		add(new JLabel("X:"), gbc_000);

		GridBagConstraints gbc_001 = new GridBagConstraints();
		gbc_001.insets = new Insets(4, 4, 5, 5);
		gbc_001.fill = GridBagConstraints.HORIZONTAL;
		gbc_001.gridx = 3;
		gbc_001.gridy = 2;
		add(new JLabel("Y:"), gbc_001);

		GridBagConstraints gbc_002 = new GridBagConstraints();
		gbc_002.insets = new Insets(4, 4, 5, 5);
		gbc_002.fill = GridBagConstraints.HORIZONTAL;
		gbc_002.gridx = 5;
		gbc_002.gridy = 2;
		add(new JLabel("Z:"), gbc_002);

		GridBagConstraints gbc_originXtextField = new GridBagConstraints();
		gbc_originXtextField.insets = new Insets(4, 4, 5, 5);
		gbc_originXtextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_originXtextField.gridx = 2;
		gbc_originXtextField.gridy = 2;
		add(getOriginXtextField(), gbc_originXtextField);
		GridBagConstraints gbc_originYTextField = new GridBagConstraints();
		gbc_originYTextField.insets = new Insets(4, 4, 5, 5);
		gbc_originYTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_originYTextField.gridx = 4;
		gbc_originYTextField.gridy = 2;
		add(getOriginYTextField(), gbc_originYTextField);
		GridBagConstraints gbc_originZTextField = new GridBagConstraints();
		gbc_originZTextField.insets = new Insets(4, 4, 5, 0);
		gbc_originZTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_originZTextField.gridx = 6;
		gbc_originZTextField.gridy = 2;
		add(getOriginZTextField(), gbc_originZTextField);

		java.awt.GridBagConstraints constraintsAnnotationJTextArea = new java.awt.GridBagConstraints();
		constraintsAnnotationJTextArea.weighty = 1.0;
		constraintsAnnotationJTextArea.gridx = 0; constraintsAnnotationJTextArea.gridy = 4;
		constraintsAnnotationJTextArea.gridwidth = 7;
		constraintsAnnotationJTextArea.fill = GridBagConstraints.BOTH;
		constraintsAnnotationJTextArea.weightx = 1.0;
		constraintsAnnotationJTextArea.insets = new Insets(4, 4, 4, 4);
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

//		java.awt.GridBagConstraints constraintsRegionJPanel = new java.awt.GridBagConstraints();
//		constraintsRegionJPanel.gridx = 0; constraintsRegionJPanel.gridy = 6;
//		constraintsRegionJPanel.gridwidth = 7;
//		constraintsRegionJPanel.fill = java.awt.GridBagConstraints.BOTH;
//		constraintsRegionJPanel.weightx = 1.0;
//		constraintsRegionJPanel.weighty = 1.0;
//		constraintsRegionJPanel.insets = new Insets(0, 4, 5, 4);
//		add(getRegionJPanel(), constraintsRegionJPanel);

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

//		java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
//		constraintsJPanel3.gridx = 0; constraintsJPanel3.gridy = 7;
//		constraintsJPanel3.gridwidth = 7;
//		constraintsJPanel3.weightx = 1.0;
//		constraintsJPanel3.insets = new java.awt.Insets(4, 4, 4, 4);
//		add(getJPanel3(), constraintsJPanel3);
//		initConnections();
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
		CopyOfImageAttributePanel aCreateImagePanel;
		aCreateImagePanel = new CopyOfImageAttributePanel();
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
//	private JButton getBtnCancel() {
//		if (btnCancel == null) {
//			btnCancel = new JButton("Cancel");
//			btnCancel.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					done(e);
//				}
//			});
//		}
//		return btnCancel;
//	}
	public void init(Origin origin,Extent extent,ISize isize,String annotation){
		this.origin = origin;
		this.extent = extent;
		this.isize = isize;
		
		getOriginXtextField().setText(origin.getX()+"");
		String yOrigin = (isize.getY() > 1?origin.getY()+"":"0");
		getOriginYTextField().setText(yOrigin);
		String zOrigin = (isize.getZ() > 1?origin.getZ()+"":"0");
		getOriginZTextField().setText(zOrigin);
		
		getPixelSizeXJLabel().setText(isize.getX()+"");
		getPixelSizeYJLabel().setText(isize.getY()+"");
		getPixelSizeZJLabel().setText(isize.getZ()+"");

		getXMicronJTextField().setText(extent.getX()+"");
		String yExtent = (isize.getY() > 1?extent.getY()+"":"1.0");
		getYMicronJTextField().setText(yExtent);
		String zExtent = (isize.getZ() > 1?extent.getZ()+"":"1.0");
		getZMicronJTextField().setText(zExtent);

		getYMicronJTextField().setEnabled(isize.getY() > 1);
		getZMicronJTextField().setEnabled(isize.getZ() > 1);
		getOriginYTextField().setEnabled(isize.getY() > 1);
		getOriginZTextField().setEnabled(isize.getZ() > 1);

		getAnnotationJTextArea().setText(annotation);

	}
	public String getEditedAnnotation(){
		return getAnnotationJTextArea().getText();
	}
	public Origin getEditedOrigin() throws Exception{
		return new Origin(Double.parseDouble(getOriginXtextField().getText()), 
					Double.parseDouble(getOriginYTextField().getText()),
					Double.parseDouble(getOriginZTextField().getText()));
	}
	public Extent getEditedExtent() throws Exception{
		return new Extent(Double.parseDouble(getXMicronJTextField().getText()), 
				Double.parseDouble(getYMicronJTextField().getText()),
				Double.parseDouble(getZMicronJTextField().getText()));
	}
	private JLabel getOriginJLabel() {
		if (originJLabel == null) {
			originJLabel = new JLabel();
			originJLabel.setText("Origin (microns):");
			originJLabel.setName("MicronJLabel");
		}
		return originJLabel;
	}
	private JTextField getOriginZTextField() {
		if (originZTextField == null) {
			originZTextField = new JTextField();
			originZTextField.setColumns(10);
		}
		return originZTextField;
	}
	private JTextField getOriginYTextField() {
		if (originYTextField == null) {
			originYTextField = new JTextField();
			originYTextField.setColumns(10);
		}
		return originYTextField;
	}
	private JTextField getOriginXtextField() {
		if (originXtextField == null) {
			originXtextField = new JTextField();
			originXtextField.setColumns(10);
		}
		return originXtextField;
	}
}

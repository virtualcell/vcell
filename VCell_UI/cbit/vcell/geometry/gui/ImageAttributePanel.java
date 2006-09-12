package cbit.vcell.geometry.gui;

import cbit.util.Extent;
import cbit.image.VCPixelClass;
import cbit.image.VCImage;
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
	private java.lang.Object fieldStatus = new Object();
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
 * CreateImagePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ImageAttributePanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * CreateImagePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ImageAttributePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * CreateImagePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ImageAttributePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
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
			if(index > 254){throw new Exception("PixelClass indexes must be less than 255");}//need to save last(255) for (grid,blanck,etc...)
			cmap[index] = java.awt.Color.red.getRGB();
		}
		//Set grid color
		cmap[cmap.length-1] = 0xFFFFFFFF; //white

		//Make ColorModel, re-use colormap
		java.awt.image.IndexColorModel icm =
				new java.awt.image.IndexColorModel(8, cmap.length,cmap,0,  false /*false means NOT USE alpha*/   ,-1/*NO transparent single pixel*/, java.awt.image.DataBuffer.TYPE_BYTE);

		
		//Initialize image data
		if(pixelWR == null){
			//cbit.vcell.geometry.GeometrySpec gs = new cbit.vcell.geometry.GeometrySpec((cbit.sql.Version)null,getImage());
			//cbit.image.VCImage sampledImage = gs.getSampledImage();
			//if(sampledImage.getNumX() != getImage().getNumX() ||
				//sampledImage.getNumY() != getImage().getNumY() ||
				//sampledImage.getNumZ() != getImage().getNumZ()){
					//cbit.vcell.client.PopupGenerator.showInfoDialog(
						//"Image was too large ("+getImage().getNumX()+","+getImage().getNumY()+","+getImage().getNumZ()+") and has been down-sampled.\n"+
						//"The new size will be "+sampledImage.getNumX()+","+sampledImage.getNumY()+","+sampledImage.getNumZ()+")\n"+
						//"Features may have been distorted or removed.  If displayed image is not acceptable"+
						//"To prevent sampling, image length should be less than "+cbit.vcell.geometry.GeometrySpec.GS_3D_MAX
						//);
			//}
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
			int rowStride = xSide*sampledImage.getNumX()*sampledImage.getNumY();
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
				//displayScale = Math.min(((double)DISPLAY_DIM_MAX/(double)pixelWR.getWidth()),((double)DISPLAY_DIM_MAX/(double)pixelWR.getHeight()));
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
		// draw labels and grid
		if(pixelWR != null){
			java.awt.image.BufferedImage bi = null;
			if(!getFullSizeJCheckBox().isEnabled() || getFullSizeJCheckBox().isSelected()){
				bi = new java.awt.image.BufferedImage(icm,pixelWR,false,null);
			}else{
				bi = new java.awt.image.BufferedImage(icm,smallPixelWR,false,null);
			}

			if(xSide > 0 || ySide > 0){
				int gridXBlockLen = (bi.getWidth()/xSide);
				int gridYBlockLen = (bi.getHeight()/ySide);
				
				java.awt.Graphics g = bi.getGraphics();
				g.setColor(java.awt.Color.white);
				// horiz lines
				for(int row=0;row < ySide;row+= 1){
					if(row > 0){
						g.drawLine(0,row*gridYBlockLen,bi.getWidth(),row*gridYBlockLen);
					}
				}
				// vert lines
				for(int col=0;col<xSide;col+= 1){
					if(col > 0){
						g.drawLine(col*gridXBlockLen,0,col*gridXBlockLen,bi.getHeight());
					}
				}
				// z markers
				if(xSide > 1 || ySide > 1){
					for(int row=0;row < xSide;row+= 1){
						for(int col=0;col<ySide;col+= 1){
							g.drawString(""+(1+row+(col*xSide)),row*gridXBlockLen+3,col*gridYBlockLen+12);
						}
					}
				}
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
/**
 * Comment
 */
private void done(java.awt.event.ActionEvent actionEvent) {

	if(actionEvent.getSource() == getImportJButton()){
		try{
			synchronize();
			setStatus("Import");
			getDialogParent().dispose();
		}catch(Exception e){
			cbit.vcell.client.PopupGenerator.showErrorDialog(
				"Error setting IMAGE values:\n"+(e.getMessage() != null?e.getMessage():e.getClass().getName()));
		}
	}else if(actionEvent.getSource() == getCancelJButton()){
		setStatus("Cancel");
		getDialogParent().dispose();
	}
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
			ivjAnnotationJLabel.setText("Image Annotation");
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
			ivjCancelJButton.setText("Cancel");
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
			constraintsImportJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getImportJButton(), constraintsImportJButton);

			java.awt.GridBagConstraints constraintsCancelJButton = new java.awt.GridBagConstraints();
			constraintsCancelJButton.gridx = 1; constraintsCancelJButton.gridy = 0;
			constraintsCancelJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getCancelJButton(), constraintsCancelJButton);
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
			ivjMicronJLabel.setText("Size (microns):");
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
			ivjPixelClassImageLabel = new javax.swing.JLabel();
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
			ivjPixelSizeJLabel.setText("Pixel Size:");
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
			ivjRegionCountJLabel.setText("PixelClass 1 of X");
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
			ivjRegionJLabel.setText("Image PixelClasses");
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
public java.lang.Object getStatus() {
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

	getImportJButton().setEnabled(false);
	
	cbit.vcell.client.PopupGenerator.showErrorDialog(
		(exception.getMessage() != null?exception.getMessage():exception.getClass().getName())+"\n"+
		this.getClass().getName()+" internal error\n"+"Please Cancel and try again"
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
		setLayout(new java.awt.GridBagLayout());
		setSize(411, 522);

		java.awt.GridBagConstraints constraintsPixelSizeJLabel = new java.awt.GridBagConstraints();
		constraintsPixelSizeJLabel.gridx = 0; constraintsPixelSizeJLabel.gridy = 0;
		constraintsPixelSizeJLabel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsPixelSizeJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getPixelSizeJLabel(), constraintsPixelSizeJLabel);

		java.awt.GridBagConstraints constraintsMicronJLabel = new java.awt.GridBagConstraints();
		constraintsMicronJLabel.gridx = 0; constraintsMicronJLabel.gridy = 1;
		constraintsMicronJLabel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsMicronJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getMicronJLabel(), constraintsMicronJLabel);

		java.awt.GridBagConstraints constraintsAnnotationJTextArea = new java.awt.GridBagConstraints();
		constraintsAnnotationJTextArea.gridx = 0; constraintsAnnotationJTextArea.gridy = 3;
		constraintsAnnotationJTextArea.gridwidth = 7;
		constraintsAnnotationJTextArea.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsAnnotationJTextArea.weightx = 1.0;
		constraintsAnnotationJTextArea.insets = new java.awt.Insets(0, 4, 4, 4);
		add(getAnnotationJTextArea(), constraintsAnnotationJTextArea);

		java.awt.GridBagConstraints constraintsAnnotationJLabel = new java.awt.GridBagConstraints();
		constraintsAnnotationJLabel.gridx = 0; constraintsAnnotationJLabel.gridy = 2;
		constraintsAnnotationJLabel.gridwidth = 7;
		constraintsAnnotationJLabel.insets = new java.awt.Insets(4, 4, 0, 4);
		add(getAnnotationJLabel(), constraintsAnnotationJLabel);

		java.awt.GridBagConstraints constraintsXMicronJTextField = new java.awt.GridBagConstraints();
		constraintsXMicronJTextField.gridx = 2; constraintsXMicronJTextField.gridy = 1;
		constraintsXMicronJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsXMicronJTextField.weightx = 1.0;
		constraintsXMicronJTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getXMicronJTextField(), constraintsXMicronJTextField);

		java.awt.GridBagConstraints constraintsXMicronJLabel = new java.awt.GridBagConstraints();
		constraintsXMicronJLabel.gridx = 1; constraintsXMicronJLabel.gridy = 1;
		constraintsXMicronJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getXMicronJLabel(), constraintsXMicronJLabel);

		java.awt.GridBagConstraints constraintsYMicronJLabel = new java.awt.GridBagConstraints();
		constraintsYMicronJLabel.gridx = 3; constraintsYMicronJLabel.gridy = 1;
		constraintsYMicronJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getYMicronJLabel(), constraintsYMicronJLabel);

		java.awt.GridBagConstraints constraintsYMicronJTextField = new java.awt.GridBagConstraints();
		constraintsYMicronJTextField.gridx = 4; constraintsYMicronJTextField.gridy = 1;
		constraintsYMicronJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsYMicronJTextField.weightx = 1.0;
		constraintsYMicronJTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getYMicronJTextField(), constraintsYMicronJTextField);

		java.awt.GridBagConstraints constraintsZMicronJLabel = new java.awt.GridBagConstraints();
		constraintsZMicronJLabel.gridx = 5; constraintsZMicronJLabel.gridy = 1;
		constraintsZMicronJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getZMicronJLabel(), constraintsZMicronJLabel);

		java.awt.GridBagConstraints constraintsZMicronJTextField = new java.awt.GridBagConstraints();
		constraintsZMicronJTextField.gridx = 6; constraintsZMicronJTextField.gridy = 1;
		constraintsZMicronJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsZMicronJTextField.weightx = 1.0;
		constraintsZMicronJTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getZMicronJTextField(), constraintsZMicronJTextField);

		java.awt.GridBagConstraints constraintsRegionJLabel = new java.awt.GridBagConstraints();
		constraintsRegionJLabel.gridx = 0; constraintsRegionJLabel.gridy = 4;
		constraintsRegionJLabel.gridwidth = 7;
		constraintsRegionJLabel.weightx = 1.0;
		constraintsRegionJLabel.insets = new java.awt.Insets(4, 4, 0, 4);
		add(getRegionJLabel(), constraintsRegionJLabel);

		java.awt.GridBagConstraints constraintsRegionJPanel = new java.awt.GridBagConstraints();
		constraintsRegionJPanel.gridx = 0; constraintsRegionJPanel.gridy = 5;
		constraintsRegionJPanel.gridwidth = 7;
		constraintsRegionJPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsRegionJPanel.weightx = 1.0;
		constraintsRegionJPanel.weighty = 1.0;
		constraintsRegionJPanel.insets = new java.awt.Insets(0, 4, 4, 4);
		add(getRegionJPanel(), constraintsRegionJPanel);

		java.awt.GridBagConstraints constraintsJLabel = new java.awt.GridBagConstraints();
		constraintsJLabel.gridx = 1; constraintsJLabel.gridy = 0;
		constraintsJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel(), constraintsJLabel);

		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 3; constraintsJLabel1.gridy = 0;
		constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel1(), constraintsJLabel1);

		java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
		constraintsJLabel2.gridx = 5; constraintsJLabel2.gridy = 0;
		constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel2(), constraintsJLabel2);

		java.awt.GridBagConstraints constraintsPixelSizeXJLabel = new java.awt.GridBagConstraints();
		constraintsPixelSizeXJLabel.gridx = 2; constraintsPixelSizeXJLabel.gridy = 0;
		constraintsPixelSizeXJLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPixelSizeXJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getPixelSizeXJLabel(), constraintsPixelSizeXJLabel);

		java.awt.GridBagConstraints constraintsPixelSizeYJLabel = new java.awt.GridBagConstraints();
		constraintsPixelSizeYJLabel.gridx = 4; constraintsPixelSizeYJLabel.gridy = 0;
		constraintsPixelSizeYJLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPixelSizeYJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getPixelSizeYJLabel(), constraintsPixelSizeYJLabel);

		java.awt.GridBagConstraints constraintsPixelSizeZJLabel = new java.awt.GridBagConstraints();
		constraintsPixelSizeZJLabel.gridx = 6; constraintsPixelSizeZJLabel.gridy = 0;
		constraintsPixelSizeZJLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPixelSizeZJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getPixelSizeZJLabel(), constraintsPixelSizeZJLabel);

		java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
		constraintsJPanel3.gridx = 0; constraintsJPanel3.gridy = 6;
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
		getYMicronJTextField().setText((getImage() != null?getImage().getExtent().getY()+"":null));
		getZMicronJTextField().setText((getImage() != null?getImage().getExtent().getZ()+"":null));

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
private void setStatus(java.lang.Object status) {
	Object oldValue = fieldStatus;
	fieldStatus = status;
	firePropertyChange("status", oldValue, status);
}
/**
 * Comment
 */
private void synchronize() throws Exception{

	if(getImage() != null){
		
		//Set Description
		try{
			String newAnnot = getAnnotationJTextArea().getText();
			if(newAnnot != null && newAnnot.length() == 0){newAnnot = null;}
			if(!cbit.util.Compare.isEqualOrNull(newAnnot,getImage().getDescription())){
				getImage().setDescription(newAnnot);
			}
		}catch(Throwable e){
			throw new Exception("Error setting Annotation\n"+(e.getMessage() != null?e.getMessage():e.getClass().getName()));
		}
		
		//Set Extent
		try{
			double newX = Double.valueOf(getXMicronJTextField().getText()).doubleValue();
			double newY = Double.valueOf(getYMicronJTextField().getText()).doubleValue();
			double newZ = Double.valueOf(getZMicronJTextField().getText()).doubleValue();
			Extent newExtent = new Extent(newX,newY,newZ);
			if(!newExtent.compareEqual(getImage().getExtent())){getImage().setExtent(newExtent);}
		}catch(Throwable e){
			throw new Exception("Error setting extent\n"+(e.getMessage() != null?e.getMessage():e.getClass().getName()));
		}
		
		//Set VCPixelClass
		try{
			saveRegionName();
			//int currentPCIndex = getCurrentPixelClassIndex().intValue();
			//vcPixelClassArr[currentPCIndex] =
				//new VCPixelClass(vcPixelClassArr[currentPCIndex].getKey(),getRegionNameJTextField().getText(),vcPixelClassArr[currentPCIndex].getPixel());
			getImage().setPixelClasses(vcPixelClassArr);
		}catch(Throwable e){
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

	getRegionCountJLabel().setText((vcPixelClassArr != null?"(PixelClass "+(currentRegionIndex+1)+" of "+vcPixelClassArr.length+")":null));
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G380171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8FDCD5D536B095755144CCDA14D8D4E4E8B4D2514BA6AA1AD051ACE942864D8C9BE6F27A1C173DC7935F4BFA766B88189818D854E3E62C1834520970CF4C97B20E0122018212DE71EA54F039775CBFF061DE6FBD88A86ADBFB6F3376B9777C3920BD5FEF7C7D16676E35575AEB2F357F2D3D76BE87216C4378954B734B84E1798AC17E7765F2C168689704FB1EFEE99762467435978B2AFFFF86D8AB
	4C69AA04E7BC6415BD5BDEDCAA4C59D58469A9104EF8263D783F6077BB423CDDA5FE78E1A2E711CF90DE7E63932B434B49EBC4B9ABE95A95AC0167DBGA900A34FC29F11FFB6D8AE6333E5BC4272FCA20B50DC97DCA7639AA15D89908FB0454768B360390ACA3E3436C6266B981BA864BD9A48A715DC2EAA9321524FF85B64E705706A7225FEA2EBBA2DA7610902F49AGD13EC010DA3742F3C7DB0FEB273D4BA3D51D300BDEEF15CB74750AD2E0284A556F292DED6A353944FAC98AF83A7AA5314D56A7FA01C7BA
	CE4AAD160AE8B9D0862405G41DD7F8FA2AB855219G0BB9FEC1A741B7406F8EGA9007A7D7D539DF27D2D276E954A03B3D62DB151AFBA40741A92D07473FE56D570875A96557B084DF3A12FAC58DE3C912099408B908D70A75A877E567497F856F7C93BFD3D3D3E3EFA3FFF175791E871B9C4AF7C2E2D059C99F7D0347B828EC1E06D7CC8305220BE610071BDF94EFC4CA609DFE01BA79D3CC3483E7B5A7C1586B649CEBC151F90E436C88DAAB6E132CBA5B359594150BE99AA7B082016AD7CEE5B4A958632574D
	5B36B2D7AE27D0D2E457C8242D5750365EG3F899F61E778BFD27C308A4F7A674D344F107498484B1330EFD85F423AB497CE90CA370EB334C36C3468FCEBBF2B033FDFDB17D69BA9BB2E1F49AC66F5692078D4951E55A50756112433A12F6EC4FBB16BDFFFEFA663B58152E9G7381F281CA875A0BAB816A86300FC5B6ED9EE59FEB338524DD7D9E2FC38C880C776F7B5E0327B8A885ECCDFDC14956E7975BED810F2D4BAB868519E66FBEE2032E903E7D346F5BC14657E21F9830C99EDF9F14EE03E1AC86020C46
	32EFAD218908B457626995C1C0AF1992D9FE473EF7606935852543FE07CD92958A166F58C75A64042D9DCA048260B733CBF78B31D7A768BF8890456D70ABDA5EFEB1G93C6EDED334F57536F3790CD047983444E1F5239A38EFECF99C09BBBDB89EE5DE0FBF175A016735E7E37D565B4F86C247A3640900E31F7BB89CFA350BB86315F7C1B957BCD436A5B054A395798761BB150072386D9FF099F540E1F8ADAAE295B2CC11C6FEF7D07B54C58F58C9A0FABADB41A50B199E9E1E52C98C579848ACC704C4ED987C8
	1BAD06FA948E3597EF98C2DBFF333F02755DDD3620083D75C0GBA1A68D0AC4F6AE9311549F23E233C02702EBCEED8FE974D5F81E5BBG228126GE48F21AD1F6B342A6A7A1D2A0E451ABAE35967BE7F3E9EA2E3C5989904FE55B94AE0B67B3A192CD7628931CFEA907DE21F83FE84653C56E6D226BC1E1DBE4EE321F8BF0C7F5DB060029CFF2C190CD19BAE3607C4C9727439147CAEAAAB585F65D7268E1465E9A67A7986E07CF37A3EE6B2F66C3E3EBE11B69EE711737DF46C0ADE3197AA3D5B575FA7B17C0996
	62677408C3725CF132658D010CFDEFBF1907186F10ECA3B67487BC3D50A548D4A6D8CE12F13DD3AAD7592890F044875A884F9DA7F1FC8E2C4771396164ED50637D79C6EBCC6C44AB7363CE323112FCD2991F2CCEBD2D46F5BFCE6790999FAC2038899F92DE2D9343722EF112B9F7F7FFA0G74ED1EC151CB5B24899AF290474B6AEF082CCC2843A200E5A7F13C3C50CA72F1EE12874AEE1F574B4C8DF9ED2708CD9C1DE532CEE5102EBC45EC2274218587CAC93B0A477BC5187249A4391F5B7E57875E5664354B6D
	4072FFF7003403D7B53F568D931963EC68DF46023CE400CCD9EE69B0134D683E6DC23AE8C8A7G248FB33A4253EA3A8F6DC8D78D78E6G6FE9C61794C277AC274B86FC1EA95DB88752151D811BGB41FE1F451E7989D6BE7AFEDA27D097A285D369336412A60G0C095A5AFD3495199F1AC85792C21FFC867D4D2C81BA47117459766272335877662CC6FFB326A7D1487E5D5615E67E7416332C9E1633CA5F4B4B2DFF037509A387094C9848CBB80B7D415126DE736A03C11F5DC317DB392D5E79C65DB7992E7C1B
	B76169748402125C67987E03EFC8FD49FAAC23E57F640ACE7465D3C0EE6ED974653F787C3F54F62A725A08151A7AA45185B382535D7E856ABE6664C421731A6B720BCDF45F9841F4EE0CD0F42FBC33F4852BC31A8D7D358848CF81E83B0860D31DE52AB2F281DF8E789DGEC2CFD5A350D4CEFF4F0D431C71B98F07A823D22E3A32D35EDC02A22F3E9D5BD4D5EC3FEEF423E704B1A8D708C08AE48E905E9F55F2EFEC972757D23DED35426F6266F04F76A643AE45CB1C158F1767BFC233DC5549AF7FE6CC5CC4F34
	0B94FDE32E6EAAC884E0BAC7F6214EA5973797B7DC0CBACF940DF49E3E98F5FE7E876AFC76EDC5673600F842DCE7D64FC6BB7725218E33F8BDDF5355D33884567ECB301EF75AFFD8BDF73D379E1E8EDF1F3831591C7CEF1B5A65FEDC6240FA56C09D7C17E0BD97F49A55B391C06EFB1CAF95F0F91C6F66103ED79959DEEC81980C64FDCF7CE17A6D7D8B7531097FAC9E242DC1F746CD303A026D193FF13BEE4F959D2978FA7111FA3F0F6DC5968F921D26C4323D48279CDFB8C470A517AAF8E60385F4EEA3E98764
	45DF067EF3150554B17B7F002DGCA23E05D85E884F0C7619CB5691BAF049176A7E4528874593C07C29C8D5CA79CA476845FC0CEF79EA476213E0100FE57584E735BAF5FF44EFCCC6F2B8F13360F849D6381D2GB281F2394E3FBDD8B9124E384F1215E57519E9535627747250B62C542499DF160E2FCA13EE537031BE3666905147E02DE3E39E72180F9565547AD8242F3B214CE1G36A63E6444B5B166F2DC7BCE6FD52FFD29EBCE1B4E7F99F27D16DC2E4C07B9BE4BCAE66F3F9EA1EBF7650F20EF81F4828C83
	44832482647C886D3E6D505FC732BB9DE4965FEE40B5028F33DB6B2BF21B7E286B7C5AA2D64727ED8B610AD0BEE63FEC916DD7FE856ED9DA6D67D7078E9D5F875A7E202983EB6747C4EDBB137D289BE8072F40F62ED65A195773ED47795533C247272DE706212D565A308E0BF89DE61EE79D32F4FC5A76DA97EDB4F6F6367F49F86C6CF60B761EDD3EC15926C7ECC6B62D06B29B2351267339CDBB814762956954470C05F492475DFC0260B2A13DB89A757E3741E6F8167C9866DE00BA00CE00E100B8009400AC00
	BC00521F3497EF81E884688410G22G62GD2G32GF2FF02653EB9C44AAD3C9256EC0086G87C0A4C082C0BA40A20042B1108F50B0064D898EF8C682A4G2483AC82A89C4B56D4D6EE4E409618G08BC2379155453EC6E0DB261D1C31C4C1FE5C01B25AA3BD37EBD9CC69EC2090AE60BECBB723BCE050F8BD3370DB2DD2E0626D415CEB129C7235CCE4CF61B87096D561B48F9A70C8E9F0E20A301557273E3033C352A5F98375046B40CC09D97A27B05BF894A58C37C1F4370E38C65D38DDE9D1FD9E342CB560E
	77855C5FFCE9B10F1B6C349847CD9AAC46F113FD16358CCF3670AA0F85738FDA4247D50ED94C63AA579FB62F6704434675CCBCECDC4FBB8E072F674F8E1B5573FD5AF60F3C3706728703E210AE1C8DB649A6677DDF093401AE5653EA6B95850CF53CE6DBA5536C11FC3B09BF4C663BB4BE5FED980B7B6795CE62E3F6C0DA82089E0BFDFE295348C7CB984B46CB4AD87D38E1BEC32A1578E899103F1848388A76C9GB5D7212F5052BE22DF3AE7B00CDF7A559173F65AFB44381D1A0F10B1967475876C2236AD8E9D
	A967F90AD752A4093D442432AFCC79A5DB40A56A5A52CE79D91E993F7708D9FB33761A5A25EE2F2D822E113F960D5A40FA95333D77AAFD9B3072AE352B4B5B4E5BFF85EF7F082B307D770A749C8F5219G0B2F42765FEE587E4255CCE65955E6321B9CEF2AF559023A3C277315699CAD173343343C579CEADD3E603ADCC9FD91D2F65B55284BA5F4CF9C8569F800142BD11705063AE44AB297194AFEDAA271D02FED4857AF419EAC064878BD0FFBAE853ED24055GB447B07F0279A8F78D10B95A993075C2B39F72
	0BF6216E9A421BF282F91300BE8DA087C0003C0AEBF07F7A07CFC87F26814AC31E13623EE6DB1768156B735BB5C43F960FBD606BE3998C9F2CA0EBD0FDDF1FCF22E128FD969883753005157976FCD79A1A2F62F557123EE9158BA50CFBBD225741ED307B8F2BD5792CB61857FB16649DB54CEBF42CD565694B352FA23C478CF97FC6474BB1D35E575FA4F6E03BF335AE7B2CAABC0D9B3276F86F94E9C79442E88FFC381263364A3802C38AEE1B0C1BA5152A5B432A1657B39412F7D41D377C2D103CE36A3C03EEAA
	0756708B0145B5DA0654F10D28EBD8FFCA3A86639E15B6237CB41EBF3DCE1D1FA567AF63795D7B0D78CB2F45FCFB0BBAFF4335AC3F19671FEEB5626F6479D7B69B65CBBC3F2C46287CA81E6F2FB2620F6379B7FEE4441F4C73671CB662CF677917390D7267AB65BB0D7217A87A390C7205F1189FE148DF4673738D73B7727CC98E237C9D63F08E1B211B435A46A97316E31CFE8EE33125A13A3E8F0E531E51C7D2FC74B86D99FD319363EE7110D7980BFB20B5AEB25F3AA1BD48F1128B77C51131383E27DB4D5767
	59D66375F90E55580FFA529A5E0F7A1555ECDDE575DBCA7776D9D037C53C4EC71DD8675CD83E67525936EE3CE2CF4BF8B35B5ECF57CE77F82DEDE7D07C70F82DED1BC59E33053C3C71D827B6DAA76BF530673F8EF17770FACA9C77398D6952B96EA7B6244B3C8E750989D9EBAB2E07FD6F750A8E0D571B69731FF47D6C381E55FBB5473FC9E33191AABC5367F2CACF528910D79A07F54A23F59F06F4B4479D65FDA5B68EE33CFF6C47B30E45002B3881574F2D1DA46E241CF15886245A5A2F839E47AE1B2B192E46
	4C87686AAF12792DB7208FB00616B9865209GE9B7E0CCEC70930C09A5CD1AA0E47E5678BCA847F2BAFF7E8D4CBE4BEED0E2E2911FF595B0194F1CC019B5B736977BEF24B16E817558A5F37C3B05AC1E4172F7066413BE7B2D204E770764137AFED9204E1F9012CFEC7454F2164FF41B7DB16A36ECF632603DA752746ED84A0DCC27EC79D9710B714B5B46373DA11F41B9CD4E60685A45740F9CC29F326CA60C2358F9DC2879A6EC4347CE1B44E938EF42F8DF2DC35EC8DE5E4D7B91174A4B7BF863BA737228FB40
	F82FEBC13FB00F17B725954B2B0CC75C4CE644B9B8EEFF8D7246F15C2DD5C81745F1F3BFC23A121B91D7F486699AB86EC1B76206B9EEAE774127F05CCC976216F05C3D4ADEED826236F3DC8747ADF4703860849CEB5914AE8352B99C772D9B47DF61ADB8CF1C25F496C8CB9C77A30AA36748640C186156BA71FE8521D377A74BADA347F1C682A663AFDC06766E0B599D17F38166F7FD62A2779DA468B0B13710FB1FDFF61251D312C04FDBDD8DB26DFC831D8A6471D4A0439220A1702E0C9792585C381C4B7A5B19
	D0D947EC0AAC3F93E5D9927432160FA04B2D12455A764F344DA440C7A5E0FB477379B6A9815BB10D2E074BA0DDBE91F133A92ED9BE43E73803E2383BC9C6C04A435F4B8CF45237F74FB94BD0770B117A84EB8317B3348DCEF7A9ED10415B20FA6248ED5088B484308DBAA6AAED404A3E472E14BD5106E53B8D4A5676A5ED594393356DBB194E2FD1000F1B086D7B97BB462953B8AE4101ED3E08376FF28719532BA7011F89608708820883C81D04B42514A6935239G25373233EDF2D7237AD62439525E29520114
	C11E7EC92AD86C24D0DD5BB4696AC9467DA2D505E732FE9AA2EB15AA8FF15A9868E8670C559A1AF704E24D1CD0A0AA6D6867F30275D663BED22082AF5090D8AF67C55C2A3423FC6E52FFFE6725D5674967BAE7BE666FF51D20FB83D57D993E0762E3D5F86647DD4B7D388448ABCA44F36A676839169752D1G71GA9G73817292F13F50FFF40BB0C2BC506273B74306422B5A82B07D1EBDF1FE772E272EC4FFE94A7DBF95D2AEEBB0BD9FAD3A0D357BC6791912B1FEB9F9B27D06DCC4BFAB64C9G31G49G69G
	593721FE8F9E154F2477B8BCD23DA4596CEEFAAB089FC8FB448185CD02C5CC374B070CF44B156BD178D3FDFFE4FCEE43B8DB39CCDFE94A77FA1D1A2FCEC3E7B1651B345FC81EDB269FB6653B2905700D5164A7486994D33E7BDA0D64E54874B926FC331B0D7872E47A2249E6FCEDB504EF1DA6FF0B1CEEB065CB2AB20C5D4A74DED33E859F913E88CDFE0C1CCEB465DBFD46C8DE2ACC1FE94AB74DED4437C826DFEADECF4318EAC9123C2FC8B2ED871791DF35CC5FE84A17EAA82FC326771B726DB4640B1469E3CD
	79FE23BB1FA610A453279970317564A527BEE67C268B633ED9C9ACE63C841E426DEC1DE5EBCD400DEBCD796DA37BA315C0D3F73B62B3D8EE577A1F9D7DE41FF9D0FB070E67EF5B2B642B6E1B89B8C7BE7E2D7AFD24526B3C79D83E36EE1DF2FD3D37AB7BE536B7C9657B1AF8487BD0E0E725C502720F2D39D3BD82177B6B921C1B57CD3EC5E8F832A1FF3D095C79323C0DF7A8F27DB7D42F1277104A59BE64B3B91F7588DC7F9A03F6BA406F5B445E415AA9991E69F730F3F456CE77F21FA07B8E63F6A2BE469AD9
	CEAE50945E2934D3791D5AF6BA341350B7757AFD81DD9B959DA5F93B497BA1DEA50F69771A32BF3CB31CED6DA42CAE734C637B740EBB51A61775AAB619E6A3B6697285E8100D5964A9FE0FA286F812GF2G726E146F9DD01AC3F624A9C9E6F65BG4F1AE4C6436C7600836D5616ECEC37E2155D9CC0B3184C6CC6525149DA3B8D84CA157EED6B95557175053DE521F9BCC64072276FA17E4A3EC376004F6BA5D1782D32AF38CB094B6319400FCB48B8595B6FE547B17C168956637F3D2570EC13E379FBF531DCE2
	0758E40C754E33EBE359A4BF09580B5EDD09F1286F2E2449F6DC82CF61AE24195DEDF4B72C9C72AB6F62F194BE7E9A6E8A57C79EA25D8A7D07934A6F632A5FF17D673ADFBB97E0B26E3920B218BF766F22F9FC7ABFC4637874B251B8BE1D6F8CFF5F60CD2779FD0335CE75FB9C4C1F3C52C77A45B034D5ACC0AAC0A640223B50173C497911C0FDC983BFF20F43C54E2F495BE4924A3F58E52E6B652E115FC13B4AC54EF23BFCB071741272919F6BA26DA5797C9449F0B738080D829E17DBE224B27E965A873D2213
	226558840DA18A7F823F7F85772E8F1C443D6BBA0EBBEBC3BAAF476570F877A0473DD7CFF033C81A633E2FC73AA59C7760FEACEF475D087B2099699AB96EE7B406988B69E40EFB1F4690D3A11D41F19737A26FFC0E4BECC5999B27A0EE6B81242B66385FD03911100E61382FDB10AE1E63DCB5D817DC0E1BC5F14BA0DDF28FBF9B580E3C659CB71D46AE5D109E64380D1B79198D473D78914A486238036B89AE8752399CF76FFA64DD46F19B4E6099CB734F90F779992433F25CD3B47E188869940E5B69C13AF40E
	FB112EEBE577429AF2AF625AF9ACEB8B477D0662A2A0BD06633E66B14EB80E7BA5ADEF892417F1DC8DAF2FA885F10759998C245D9CF73FF28643F12F5098CBBA2433B86EFB9169F2D270ECE31B8B7744DB6E03B26F43B979248B4F8F626F43B30BC4CABB9F5239GC577A36DADBCFEDCF13F717CEDBE27FFF4DC60734A6374B79B0FDF3849BD33F6DB373C53A65BD2DFGE711FD36933683DD5D22DD3258DC824E4BD3C3FCF6AB5445FBFF78989F137BEA002C7991C09B871086B09FE089C0618350CF81AA816AG
	2CGFE00C800D800A400D400CC00C58F607C77C677A6333A83D2BEA02213E04066A153A06ED1C376GCB213C12547075E7EDB86044B62CGFAEBAA364BC3CEEC43B1295806AFD05AD9104E81D84AE99773B53874C123B64C7D4A3C8D3F6D97388E79A1ED5088E5F5BCB81AB86B53A171GBF70C40E4047F4DFCE5F03A1BECEAC50E7BE0867B0ABAAC87F511E43D4B1273136F60FE4F70B0EDD328BA97B1DCDDDE82F221F233D86E879F510368278FF0E6366141DFF43GF0E9BFC7BBCE37E399CBF999AFD25AAD8F
	415C8D50799052AE61E7A2038F0D566639DFB11B1F21BE8FAB6734834FF912F9592B9D78AD04458F21CDDCFB5EB730C9ED2D4A4364E5E6F07E0AB45463AC55438AE9BFC0D49A4AAA68617BA7CEFB27074E95104E85A81A0A7159C9A17D23827055D34337B39BD363FC66BE420D3E11FD04DBFD74DDB7D85BADC3FE793EDB120F6CA3FCCEE7D0643E48DD3ED20EEB8A3679029EF293097950778E0546E0EF9EA0ED40768DCF1C44FDC32301BE5AFDF18750782732790260A327EA63746903CA59AF73B87D98133255
	715D84CD59A9D335FB124F6B099E815FC0B0EBAAA9FFF7BD761F7429388F6A2F677B7CE946B289BD6E03AA0126EE9A5B8711FEE01926556733130ABEFFED4632BBCD4ADE21AA5BAF6B03FBD3E91AD61F47DA481ECC72793C960F1F6C21E4192D2784CED35D9A5AF64707157A0CEF457AC49854E705463EB153147A103CC41D2EB12715329B8FE059D38C4AAE5000D657D9BADDDFE98E55635EB30A2C6FDBD0D616012C159AD90B01E671B4C556B21D9E8B4EAA767BD5CD285C5784C5EE43F614DBB4DDAFF715017D
	56CDD76C37E53AD64785D52132AA7295D9DFEDC6D9BB8CE495EBE43581CD5BF4C5C747F42D0E3B8B949DBD6BC3653A8FA8F2FF319E657A8D642EB650B1D225E32CCE6E95EDCA5971E7306C840332174BB644711082B499AA1DF2F4764BF5076AF147B70A2C439E143544C016764CCFC8877EF4C5D6D93AD6D61166DCEA61C1C5D6AF0FC1D5249B77C7352CEA2029D649EA5449FAC5A3CB30A8323E67F1C90B012CE20DACB75038D53206F5327ED5F3A63B7E30A26BG57AB4AC0568A0D2CB820A1007141E41DAC2F
	C6565EF6C5569CFE7E1BEAA0EB15C6D6A650E42AE4AD5249AA95C3E55DEDD5E4750BA8AB57C056EA0D2C4299609F4FD0E4154F501EA356984650C77685771EA75F59F366E367056B5C747BC0B33467089FD37C16995AF344BB781EE98764454FC05FA6B1640C22F0A66C9DE60677ED583EECBE753DABE7B2B98C3710629AE49C73CD5E8A1A7BC06F86C77601BE889A47C9B68475F112CDC163B8496620BACE42F055C15267DD302F31F23A6D9CF71463F652B2896E0847A139B93E01560B609BC5A2103FC7C36BB5
	C0F1D699F7C005BB0A5FAF8992BF8ED4F67B8248FBB4C87A395363753274F714465387EE9D37C7972D2347EFD3555B45F1C7F9DBBC6854374FB4273AFDD87B1FB6BC4399395FDEF11EFC69674C477A350D765F4E195A7E5ECD715E195A7E7E925FDF8EC2DE76CC6C6FAF040C373A075B0BAD8F0F263F7F41203FFF90525F996E77B6BD5D4BB63D5DD76A5E295502DBD3AF79DBCDA372E95FB7155F3B9D114F586E4F533DE46443DA3B67D17C180735F6BF5D05F60F03BCE1965A7DA25D7B3B236BAFD71EA75F2CF3
	66E3FA0974BE2EE316D65FBE0A774F526A7BB25FD38FC3DE8E57774510B34E06C7E0AF7E48E8E28A979B1E0DFAE55E88799923AA4B6CFDE5B5A49A48E6FCF34272259A70B1FB3CC4754EFC0459230063DF23789C951E5969D2BB46BCF2A14F72A85A2928C7ABBF65D1C5E64623E6722B3A291CC735774337D3FC1E8AAFFF73420377430BE63797FBE773383541381E96B22E65F841A90C97E44EBE57F861BCB2AD4B65EC6C42F20AB2421D9B3DF2CC90507FB9D6CF6AC15E4FE2673871836A73631AB7AFB67C7693
	1D5FB2187DAC72B37A757172797124FA2CC734E1BD42454E6C9D0ABECEFE57B7BD031FFBFE2764BF4A6F87AFB31143F8EE7BDE6199BA00BC550FE119A51DCA7E15AD186FFDAC1C8DB75B941E7E6D4813901667E01742635F0CBC59E1F97A6D8A4F0CCF789D6EDF046309F5A8BCF3787D66E6C31EF0ED71002814F319075F778EAB7B992742F3A30F21260665D96ED6591F474E734272EC70A8BC91DCCE656361F83E6ED6F872B90FA3ACCFF70F42B309FFF3A2AEAC4F0F6959BD1B03BEF2AB3FBFD37D1E623A7067
	320FDDD099ECBEFB482D1F63525D6AB90E450BEC1F10B952536711BCB6AFF5F799FE5D60EA850F6EAE5E03F4A976722BFE3F623C7036FB7BFF41F6058636DB95E2BB766EF85ABB4CC6DECF1F381BFDC514577345AE45AE2F2AFEDF52F561ED94773FA023472168A429FE3FE63F70325FF3DC78BED0A46A7B40EAD16F7B0FF760F9ED566368DF3C4A6F0D67F05CF1FA5E32A5333D38AD93F11BB81D036396F35C14CC7417DE3113BDE2619CCC97B8C8FA83CFDFF1DEB6A7650D5E668967A5E3346F1AB05DBF2467B3
	8DD0AF6B9C34470DDE3447B057373A1B60B2A03D04633E22B8610976620AA77837F4BCBF341FE448B6DAE2E0ABA663D7BFD8865A48689B35323FFCAAE47F8A7AD91E984D3E6273107D1F9BF806C760E3674C353D4416E300B66989BCAB5E796DE70279D9F1DB4067978352904352AFED4AF518DC2F2EC3861417B32A3A4FE9D1736581CF51AFC74377F848FD66F54033E5D4FCE78E046C5D0047BAAA3E1810FAFA01A7E2D4FCA7372B79E200A7F1D4FC7D1B55FC294013B9AA3EC7BFD173AD821E2523621B97F28F
	3AA48B46D756E8782E7028792A0127F1D4FC370444EABA0047BFAA3E181018E6A470440E0A6F5290F9C94013B6AA3E15A1FCD94033F8D4FC13C33EC1328CF8CA670667E33E6B64BEC1407877DA9E7F5EE0402BBD9B49104FE1701C35E62EB6569EE5A77E325B97701C7475C9B6EF3D57636AA3376184F66EBA012EDDE49F5AB0974FDD6B787DD33FC19D34E78B11F31533A0FA4E242A83AB6FF5DEDE02C1F9682F6299DD8AA9EF2EF21FB5C7275336D3646E1F6A4344642B1374CEAB3BB3DA2D3BD3CA5E53DAB297
	3F9B727FFB747719DBFA700E28B02F1D5E4BAD07E765BC76DE9753639EAF6A51B04F7C9C954FEA2DC0631D276891B1CF2B4764BFA9774A69671AB55F76F837D1794686FE7B62F837D6B7D23F68F9F8DF76EE5D379BC8FE7CBCDCF3BFBC2577FDB61C527BBE8DDDFABAF6B7A574BC602BDEA2075F6BE44B83FEE3085EAD1677991E1374FBAA0700304FA547E56A75B29E2E5757E5F10B1E6E0596BD5DF32DFA3A67DB75F4579C5053DDF7C0CFB759C0EE32015C315B75F457EE5753C5EF56530D5D2C277B75A7FA3A
	67BE51533DF0C6CF777BB3FA3A899EBD5D2D9EBD5D7D863133078CE262138D76D049EEBDDD1CC1F9B79B147701815DFA833AE986FE7B43A1FEBB0B996C62B103A973B05E748BF2C5D99ED77759AB60293E12F5087C0FD70203D5871B3EEE3430320E737BD1CB66050BF73DC87EEC04ACFF8B173F6149F33DD3D596D04A6961F72ABA1E44BBDF7DBD981FCEA338A7797B73BDF867EB4BFC3C7F74BC25F5C3BA82A0F6BE52BE67457AA54FBF57785417C125FE095DD84EE2DE763FAA77B01FC2DF7D6B10F5518AF86F
	D323D9CFCF044432A300A7E6D4FCFF8D111788BC292362739B6EBDB3C760B502C5B24FD295AF13F1CA979F9F8964F79C4F13EF5AB97331387BBF3C246F14AC501E1B38A93EDC05E77D72C55EAFB7C0DE5482EC7B673DEA79CBGAFBCBD1A36383D5B285EE5B26FC603B2985F3F9B3E2337C326EFB360D35FCB8E1DDB6C86B1019FBB74F4D7B974F44D8665FDE3D09E63F5C03D2427F10DFE194789A2B86EA3FA561BG697427513622AA6659277A7DF24F050F5314FAAFFC1CA63FD7EF43153DFA9B26F7E31CE6BE
	375747BDE843C59C7786DD732A335B0B9B329157436F5537E523FFF81BC373BD8A929E153FCB77967F7EF0BC50CF8148824883A8DB087C5B1D61FD6EC37CCEF475C2DC1F62687DE566E76E68E988E39B358FC95F7FDA987AAE7D60C22D1F793EA7D49F753D4E0B79DD2C4805A35F99457776F13F912B12A5FFFB192FFD898B711BBFDED17B4D1F57162B3F59F323287D668FA9CF494F94355F7CF9A9DF1D9F7A3D3095AAFE26FF37734FC25875FE771ED64B9EF2E94CFCDD5EC36F0EB29D6F6667B2593C7D5C741E
	A79B9B5BC3561F32E75A0BB7BEB31AB9AFA5E4DF3D83F85AC660133F0945679AE297799D8DBE5790DBB35C6386FEE0D6089F487EAE60C51D5B2143A43DA02C1004C272C2ACBC48FB2F127086FB7992FE30B7ADA5E1B5F9A93F62F0DF50EE730A0EE0C0D8437FF0C0933D6812F3620B5A02E7EFA948978A1B04B758451FA621105C5EE992D6504BBDCDC20164738F928F2AD08D41634299EA66FFB62CA08945F47BD9C1DEF42C1504B564D9313117FE05B13889B025F4D3DA21FCC8D2924A59C79A0395CEDF20E240
	6D730A95568A5B20A7A88985DB2B36C8C23E9598730F56EAC80FD052232124470C290E05D03D4DAA216C144520DA8AADE4A5D3444AFF9D653F0E7DF0C3F12C70CF5604057F3CB513049564CD5CDADD2E70BFD5FED12B2F02F68DC9D82BF40E0A2D95BEA76988EFB7422874FAA451D161692B586D737A8267D23E00779AB61DD55FEBD8FA295D74DE0375D9793D78E7157753D93CFA960DCFA582BE7DD90CD7175BBF974C5F4BBA54EB73FAE1556D296FF310F734F68DC922721E69E3F6D2FE90E964CC1617C53F85
	5C2D6279EFD0CB878862F1890983A3GG04F5GGD0CB818294G94G88G88G380171B462F1890983A3GG04F5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGBDA3GGGG
**end of data**/
}
}

package cbit.vcell.geometry.gui;

import org.vcell.util.Extent;

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
			if(!org.vcell.util.Compare.isEqualOrNull(newAnnot,getImage().getDescription())){
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
	D0CB838494G88G88GA8FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8DD8D45719B0EDC9174D5A166E12064D4726A621794C46F659AD49C73B36CB933725DF6D6334B5DD1AA58DED5C54E7BF5AD20389A6A4DE81F5C6D1C1D1317E110A0A89A2285998028679D1D4E2908684C5E5E4AE4C8CF7FE1C397223285FFB4E396F39F76E5C99502C7B5467F93DF35E1F731E731E3F773C675C0BD03AE37A322579250230F419207CFBFF29A074DD9384D36BBF5890A77CFE2EC550
	7CFB9B20C4987B20901E45C00B9EB0D90A05314DE6C8D7C03A7E32493289FE2F97EEEEAE72410FB0FA66FB8133677E9FC656937F896AD9C15362E891BC57GD4820EBCE5F8968BB7C74B94FC1B02C7D81AAF8856EC4293BF3AD54125G0F0948813EA21B7157431395603E3A438ADF5F17E788B97F571F3F1A64234993A1C111CDD51E65426BCB9DFFA03A3EC74BC9E4F201D683B8D27982C1BED3CBF4358F8F9D9D3862142BC786C4C92A9E923DEED1760FD78FDDF356551DF45B0644E3326CF7DE39A60B6DB60F
	A8014CFC1A2FE54CCC739104191076ADA4380F86092ECA785DG50339071370688BE99FE4F81183F906BF7E4CF3DD27E1E9B7FC718F740DD38B6CC7D729632FAD53C22564F67F9A1FFB3ED0B74F6E273FE20C58344834C8248G488358C27B400B1F3C834FE3D764A62F5B6D759C73799AA53B7FE32FDD1460F7DD9DD094DC07B860755B850135738FC62B8C4A9389185CF3F7AC47ECB263A036794C0EEF88995F7CCA7E8A831BE44C3811DF1A43ECD115235A0469DEB396CEF7DBCEF01F8C56FDCE506ACE7E4A4F
	8B169968B6FD6E67857DCABE32C6770C9C52565F24FDA08EFE93B9A24F705F2578AC8D1E754F960A2783F251DC0BFD91760D1EB5D8163642C705625A878D6D907F4C347C34C52C8C190B74E5D9C87BDD512BCC67AADE9641C170D59ABCABCBB465A769B620A581307E1555C146EB69EBB0FEGDA817A8122G92G12DE43BED6593EFF0AFD2C5D66179B2FB9A53B6897186C2F5AB742D39C137D36131E00EC738C085DB63F53F6C592830242731F6D4486D70264FECF7B7EG68B8A1FAC43FCDF6FABD103B8D0631
	688FB01E5C7692426397A9556AF40B2040CD26C4C6974E828812ADA0F7796CB6D9D4B9985DFC163449082D9BF2048C60B7334BE6AB3117896A3FGA0175B610BB43F53229FA60C3A3AB62FF7781A4FCAEAA2346692BB6F22F3C7917C2E48C59BEF68A238E910CE4C45FC7E756CBACDBE4D4E81D2FC1BFF9C4758378609CC8A704F4B45FE733B9E6CB74D2B3FAED4BD7700E13FE90E7EF9C1EEAE6BAF450B7563E7B64D17146D70E21C6FCF8F76C4983B73F20D47159ED24270B19D7608BA16CDA1FF8285E1704C4E
	1F6BA6ED56837512816296232D050EF256F79BED8191FB6BA7FE68E822DD35BCABE76ED0292267A21395B6A863065117BB88FDA664BD8FA08F206275391636575116778F75E84AFAD1D346D53AB2E35EF7BE7FBE9624E3D9849D045FF207BA184DDE6DA06B15B8A2FA64E651A7FA6C70A320505E6CA0F9AA63F9504BE58ABA895E87633F8986DC0063AD1DE40C5AF0316D94E5596999D269A51DC4D7605A951FBAF5202E32CED2BF6FA80CFF4E3F2D130C1D812F47A3524663A28AFD879D3B22A43A2150CD5EEB9E
	1961F7D9091FB3AC0EABF347DE6BF2010CFD699A1907186F10E4A3B674791DEE6892E4AA93E63FCE46F57DD81946C6B2E9F370ED6261B973F59C1F23BBF1FCD6DCFFC2C83D6C4BB7DAE3623F76257C22B714F17906BABED9197E58E5DC76F2BA07A878C08145BD3A03486A1D98C6FBE0104C39CD577CFE60EFF70E0992ED13135010E3B8DEE61DA33A9A208CD6G698D9CAF89DD040EF313B2D01A3C12444C8D3454B708CD4CF42C10B2C4C3BA618DE693358F7DE5F7B1E9D7716AB59126FCB2C91E66360F6DDE27
	23F5AB6D4068715D249DA44D7C1A14C7F4FE68C07F528C69F20006BC26B7AA0F69E6FCCBB8DF9E24CB5F1CEBB974A663135F5472BD6BC43EC4404F8248D078363E25657B8B977235815E7ED6B83E8F0711AF9A7089G335EE2FCF98A9F6BE72F6CA57D097A28AE5B08ED2CBAB08AE3222E6E94EDC56607BECE579242DF3E847D4D1F5E20F39CC98344AC413E77135568EF468D4F90E29FFC2EA01CBFBDFD89AB476CA59AFF7A9723852CCFAC6FA5BA8B851325940035457AF35AB56FD8A0609DF05265D6E92BE74E
	E97BA6437D605CCAF88EBA7D81D969F38C7F63F3243CE4BDD6500AFF32FF88FD79CA505B8F406CF4F05FA62D1D2AA59B3152C90FAC8E410C406ABEF0906B9EFB7DEBC27F838F652F8AD377F848172ED54A13547D7039778AD8996C8E74573201DE3954E4C9DDCA76DD4F38CAB5F97483AE86E0A6G9BEBBFF29CA0739B9D9C556C51AE7A873DFE37E82F2425360D4A55F4AE2DBEC649AD6477DE6C8BBFBBDC81CF3FB88414B3B02D1EEA3CA64BDE4F65E33A2C76F6337ABE3A3EC1A94B1CEFBCAA545F3E14EF3437
	08D96B48B76733FA56642B7535BF77C27E98G2BF325936BBC81BC4979D867E649284ED979D867AFB9BFDD1DEF2FD36B5C6E97C742571915B30B17D3A8B0D98E97E0B913C24A6983DADC8116335375694A5938F1A7BC6DDE0FD859961E7D433D5DCABFCEF2E1B9E7C099B2F9B9975A0D4AD9D6E832A8FDEF98652AGE7AFC43907037ADE8260E783ACA864FDEF7853556F78BB5447A67E3358C1DB036E0CCF426A8A36E77E4613A1FB2E3CC29377750A1719C27CBE3697F9F9025429826824EF6E61787C1B84BF21
	41B39B2453390D2467812DF805C9711FDFB8CF4A58E686DB81C481448324G241BF10E6A6AB9A8CC32BFA113065FE313BA039C8DE5DEDA55CB6C893E0112DE5BCB6CC3FD83817D2E77066EEE3F5CF447F22C5E0DFD246DF3200E4596180FG9AG7AADD867385E2A496A0C7BAC25322CBCAE073EBCD11660B6CC5025195CD808DC35AE1D2A13E3FDECEDA7290F41DA4746BC50180FD565567BD8242F27C31ED9GECCDDC6C42B531F025C9D97BEE9E572EFD33565E8CBB7F572FE4656BDB294E07B101571435EF7E
	C532F6A7GAD8540841085D03C4AE42982E8DF05F67FD56F11496CCE8719555B8438D670E11A743EAA37E935736E5A22BEC4CE5F963ED541F24CFEB7A434DF8C50D9DE8B6F328C02CBAF37DD5F9FF4E5E06D3C4029EFE7329FCD875E2CD5584E3F6165ACAC42F2BE9D22EFEA651CBDE9B96B0B0CEC35D744B2D8F999DC43F7B9F6C264746DB52D48E86C9C6D5EE2BCF61A9C6240F023F7CC31E9074348260910E7CA915A74B9BEF6CC00E3710AE7280FE9DEED32ECDD0D382FDEA73886C8772C46FAE7CD3441F382
	5271GC9GA600AC0022B5B0B781B483588122G268324G18G66836482942D85BE00E6007E35182F79A649D706F49CC0B240BC009C0052E21365900095C0860883C8AEE66B5FBCF866G1416G8F0095C0AEE1EBAA4B7717B7EAE082A07A0C66D7D24EF0F3EFEE849AC2114253EC10FF33C61E5808BC33D6CF1E670406E73AE2BB72BBC903AF8AD39F82718ADF3F0EA7CAD31E4AB0F5CBD15A0959EE5F84315D4EB0FA56C728430EC96AC8E035725CE5C0AB517C463805BE26E1845A389059AFEC915431077837
	A348E38CE50F8E2F0D4F2C8DA3CB560EED826EEFDEBE9FBEEE725B7346F113D74E9B47CD961FAFE1F8320557F8ACC8DFFAA1F2DC45F2A1FCDC65D4DF78F21E6DB3AE6739BE63F2DE6C0BDCCE31AFDCB93751367B614635D4BE909083F461EC3649B605769B1934C1C82C670C4DAD8A986BF844E1D1F8DAE4EF93710759FCE7F763FC97C766863AFFDEC57D73B9101E8F10D702FD7E2D10B502C069BAB6DEAA57050E9B66B38CDFA6BEDABD50FBGFCGB1GB356212F1057B729DF5AB29641AF7D5D45706D346822
	F1BB3DF1110C3100771AFFC05437C57E25B2CED33D12133268A6A6D5FC61CBC4DE367907441036DCC76599AD1C7C16CB615A1B3517685436D72D00EBE414E45486E90A6DB38C5A00653757254D6F835E7E65FC3D4BDE076D3F186E33AA5703AF8D50331E2FEF063AFD6B194E687561F47F5635D2DB179A6E5709C67945AB794C8C1B5FA3435A3A9C64F5395FE1D66C143A9E6B72B00D1964C23A38945656D22C4B7306FD39211469341606537D0BB1928F15EC635EEBB26C41CA497EB31B47BD9D100E821881B08B
	0078974CC77947D1B2C78F7AEDEEE866CE1FB8A0A4EDA032CFDFC7592D102E81E887708144EE407D6B665D24FF53GE527733AF82A4DF6C51414727C47DAD23F0F1D83FE2F079198BED0CE5620E39E0FD726E128D3D6988347E08B2B4835DCAC8E26EBE49DF524EF76A819924163CED132F39BB4EDDE2D213352A04DFD1B50FA8DE92D76928DADB45F818B11BDEFA87B6139928DADD4F649CAE28736BB575665D40F86CF63062CBDB65EA06D08CA986FA7BBD6F0DC2D02AB68D4F18794DC1ADC2CED0F9E2D3E6171
	A0DA2F1636F4CD906D3C16566120FAE8898F8AAC2EF17A26B62E113B0175272D9BB16E713E5308DE4369BB3B3474660D0CAEF17AB3E70D6423B83D4D2A2547A97233B87D579DC672A6CE9F309A5167F3FAF507D17E391C3E7B0811FC51A624E7D7994917F3FA2959C87E9027CF130C682D1C7E115B085E47690F8F9B51FD1C3E47D0BE1A538F3B0C68711CAE9A66BFF3934EE1CE37FE8ECB5D244EDB73B6054EE1AC36F41B2E6F8BB619F4E77477D3FC1E864FE2CEA5BCDED7DC86FB08B25C83ED98A673EDBA2497
	F05C75E15C976514617A9E389CFEFD3EFE59F8FD0E3AEC6CC7FD7EF2E4BF6A0B17432D2B2CFC8F52BDF373E6588FED46B267CAD8667E4DE85BDDA16BC352E6551E33B707332DDBA236CA5F2C376D9845E7ED565BF6890FE98AFF043D619F31CC8554AFCB03F4BA47B95CD84E799C57A2A25F21AD08FBDCC43E06ADD81FAF854525E2811F38C52DC34A16F07559C8637DF33630F22F6678DD941F2D41337AA452751624CB3742DA38954B74876AE7E4C1BA0F633AF9DFB1EF4398EF59B81EF174GAEF69B1EF15476
	133813FA46E19B156B6ACE781D76C65BD09BDD0D198F50BFEED6645336218F70C51A27305DE4A98328590EB131315D98931B797823C2745F981F87E5DAEF6637EEE77611362BB131E84F8B79CCE746F554B98368195B4958DDF9C3BBF6499C3F211045B3983DBE08CE7A6C85C1CB778451C9F95FAF5052278751090DFE311451D95DFE348B6B167723A4C1FAAABA6C5D314A9D2CCEED4AB3F67E5FAE1D796E0D82658CEEB04C999CDD3BD87DBFFB93FD48689D98C771B891B7EB8736610FEF060953F05F04491E6F
	C2599C1E5F6233082BAA477CF6D5EE8D1F9FF58F186C672C68975A4BB13F4D1D18DF8247BDE4C55CBC0E93BBD03668ED44DDBC02FC4D9C37E79F72CDF05CE9B372A5F35C33BCE61745F1A3FC8FD671A7443DB00C38BE0E3B246E55B84E4B7D77B99C1740E58BF762D80B22327510EE6738A59E9CFF72CE1CA76E23794D0674FC0E0B2538F2650C1861B63970FE856153762759BBA70F63CC86CCC78D57213F7B926E0E4B1DG733B2A0649FD8799BACCE10589064E4B03E474CCD410B1766561E605373542F8BF30
	D4F99268839E829B943C2F024D0DCB392EDB66E0DD96072AEB2D9BF54DB65035F492DD699ADD2CED6BE91B4D87FCEE853677BF7379F66BAEEC5BG6D0F9224E3B86E964511F3FEF2064FF04B24C8F7130C0064073F25DD117B8453317D0EF5E87B45E4FD0235419F5D41EDD065D45B60B6EF03C403726A5BA085F888E09B4C592536814B5B6ED2737E0E08F9279B642D6FCB7A3C33F6695B77F13A574D85FC51EEEC5FDDAE0CD357F05CD7F91BDBF7E37BEE25F170C4C82782E4826482946DB1D92A76A04F3B1427
	81527DGD1FB5859B6392B1148F97EFE28DFD3871287DDAFF7EBE231FB026B1A3ABBB81D385B38DFD4E964182EFF8D52E5515090270F01CEF54ED82D63D9AF18F5F342BF8D2B6D6866F3C25A9E63BED220018C60A130D321E56FD15BD1697363F7F7DEDAF517F2629D4BB1FF2F7FBA5D9BE84A4F70CE0AB7D72AF8664745F1BF2E94E8E315F8CE7D00C47AD58624F3810A771AAC15G2DG763D38DF28682F91A60987DA3D3EB658D0C81AADG2B5F2F2E5F5D3D6B6F2DC0FFA9795BFFAF24FC3EB96C796858DE56
	6E714A3372253FDDCA1E2CFE159ED23FB4204D87B03FE33214839C82E8FB876BA775ABE752ADF627FCCC16ED838EFAAB089FC8BB45D195CD02C52CEEFFF15328EE7D6F30F2486F0476C7E50E8FB93BA4902370A704153B5C2515CB52714D8EAB7756D9A3FD698AFFD6D83998AB11935E8D2617AA694AF74349ED6FB452D72F703707157B2A55C84E2E700F0515F3F59039E9BA7AF4A51D9CD66E4A9143582D421F91D62EEA9F114B56518B2BD83A2CAA1CDC3B59C8DF15425F90D6AE4DB04EEBD5789DE165EE0444
	A388CCA87C31E165FE9DF26ECBA0D161CF89ABB7E028EF0E421F99D6CEB60C7D66A87C66FD61649EB1AC67D6053F46C00E2DA78F3AC3E3465F6077B81A773118F19FBCFD7B58BA4B561A0DDEDCEBE28C72567BA389401334CF7599E66F537B1FFD5748BE33C3FF070E538F9CD7691A7BE6824E11F3AFE85FC7AAFEC84A477C75E5B3A9654D58276E17595E64993EB7A92EB6D9F6886C2C54AC287F581A6BA32FA7A8FAFFD504F37356A79F931A7F2DB9FFE7983D2D554A5CDC2D6AAD124B8B186E863EFFB2811D69
	F97E2A406B5F698AEDA7178B5B493C1F35D3B9BC8F6DE767682C1DFE4AFD02367D466DC4FC0C350A1EFE6011772B6D9433DF5FCE1DC7897FC9374F6B8FE9A3F3AF21B51177C3A415466A17246E8F77C7326DBA928ED3E4FE476FEF4C590FB6F9CFD6ED729D873149952F1F865918CD6671FB9405B53017GE8873057A8778EA84F99EE37099AE637B8F84ED0F8185D1E66F6CB2DB1365BAA0D5D66814F029AE6B7124E2B515BED54DF2C76EF1BDB544657DF2031D88D0D4788987D5996622F1C6A9C70FBA509C461
	EB99FED5239A1747B301AF9411F1F27C1A440EE378AD93AC4722B52A4C81A516EFF368E339448E66DA0C755EF269E359043E95686C6E4AE3C1F7D7EAEA993D8F1EBE4EE33FEAF4B7AC86688935989BF8180F3F645AC8FD44F30D7CE6735E60F57537D7737B2171BB5D2F5D89B09D5F3C27BA18BF763094BEBE7DB549B8BE7D24E49C1F7EC7A972FD03EFC9616F9B24CA5A77B818BFF9F404740BACE8AB7381581F81B4GD88F20AFF9467DA7017A1286FEE40BFD081CDF133749E454DF6B89DF57431E495FC1BB66
	A1E739D73CB07138C97E08BF61A16DA5FBFD9449F0ED9EE2A33FF348A1B3D6855FC163CE12B8C851EC1F7AB00DA17A20DED187F06F7A2DDB38F71D46F177BB11AF03637E1F937996F05C880DA19EBE886903087BEE977275F15C068E4CEFA6470DF4A0DF8A47FD1546864D07E07EBF043865B43FAAC8579F8A56CBE4DBB96E3DCE54914FF1DF69C43EC40EFB1A6A4801F461FB085B4273AB66384FF4E0D97AB96E5794A7C1FA0263CEF09DB19CB7CEE3176910DE40F13FBC424FECB82EE99F6AE87EAF44A5D2DCBB
	247BB94ED20D3292478D19710CE596474DB1A3DF9A473DC863CCE56F439E70FD44B573B30EC39C775794978D69B80EFB0D73CD673845F4EE4B0634F098F1177859CE9147DD2732FD1016B86EC51E5F98477D950D2724C1BA1D6332DD7C8C0663D2285EC39F002DBEC05CB62E377D83BC5B78399B77445381177E814E495BBDF8FED0DC07E796ED9E425B8A69FE0031BA64BD614579BB364EF87E8EBF277FE4D460734AAD32DCA96371579EF24F2C5B66D2F65AE4DB6A7563ACF24AB6E27B640ACB9C102D36A1013F
	979D648B27C1D9B26AA24778185EA746491A1F8D3CC59F1AACB5G2DGFDGB2C08CC082C092C09AC0A6C08E00399E56C200AA0086GEBBD4EFFCF797606ABBBA01583A2BA897AEDCEB28DAA65EF895ABBB8A03F097A4865E7ED7857AEEC43D860CF2B47F6F95E05EDA89C41B6B453333D4310EE87F09CC15E82FE8690F544288D33BF8A5F06ABA784DE0727026A1082F94DB9B215B86BABC177EEB2C1A6E792B9D677BCFA66C7FC9C73D1E80723F88E332C1C749F7DB9CCB5F39A6B6ADA648107E8EFD4DCC8456F
	ECF6223D460E223D7C747D15A4C82781E49E45F1B342ED1E8B381A0F500E5F65B6F7FC04F9E4D15E6910CE81B0F15E5FF03FE741C7D335F976C74C667BEE8A3C5C717C0ED1F983667D148B3F05505300B699EA59E6E0133ABA0D0749737CF7874A47B6E0BDF652FA24C1BA93A0176BCA384A77CF471077E9BA2F34C23A9FE06C9846E79F8B9A233100CFBC96391D5918BABE925EC778F8E4F29F21FB043E6B86EB3BF55C275CF73BB0C276915E41410048FD117E11E20EBB99E877861C64A69273211F3A999C03ED
	3FC55A006D9BCC37F05F10E2D09F7D3EF88E70E49EE3738541679C5347698337553C73DD183750B8F9FC3734B1B86F4AC67D1E648BDD249EFE6FE8203911647FD897761FC30D388FFA2E0B6F734368A47C388FCAG1E24C6368FA27DE0F6233EBE4F8BEAFD3E5001F91B42643DCC13F726E2AB5C1B4E8F294F53D632A7133DDE496A7411BD142273CDBA75B11EEDDD41EDD7DE20A98F2FEB36C1F916696CDB58241607504A1A74F5EDAAD4737EFAA766DD51E49C0B51023E2E071B74F57D31B5389E034BD4DD87F8
	BD1A8DF42D50696A819E8228CB8A2947DFAED76D3738A3D86F4C952A5ECB3C0EE386FAAD86761B56245AEFFAC89DE30F846B1AEBD6F5ADBA023AE69A68DA2553158ABC291ABA4E8B2963C20BDA47FF5E972CB72AD355DBDE0DFAB38D742EB628E30E260E66E63D5E472CEA5E87CBB06F52E66371C08A09632192F86A1B55BA35B76B6D772A94DC0F6FF7293AAE707344BE83DD7AB3BF9F7078B43A22C3F4BD21BB3BDC542D6ADA63C1DD31863AD668F4A582CF22C6D7CA082EE2DD3D4AFAD4DD57A4FED6ED20EB15
	CED7BA7024EBF4E505687A322EDE9F762A3ADE65B64CB55035CC272B28857C4A96753C2D3CC52F6BD1D7302ECB67D5DDABF8BC2A2A45F8ECE9F5B5GCF03C617B5C457DC5DB9620F7AD4DD07388D7B8DF42D56691201C756680AE9511FA3E6984642A777857F73AE65F6FA6E6E9CB1156EEB62DA746708BF247869AD7AF3448E8F7AFEB301D6FC9CFD1BB62FD63F8C78186311FD9BE52FCB7D400463CC8F433DC4F1498A0E79A65F998F6F834D9E1F5C877A7138F11C64E76321F11274F163B849AF4735F19206FB
	E91C7479A1585774F03E859C574BF13F25F9925CB927DDF6F07CAB34DC845FAA92057CBD9ADA2ED10A6BD1F0EFE9F03D787D126BE42102AABB3CFE14B5DFA77DFC50A9C9ACDDC4F91C9EF06B38BDCA2E13B2BAFDB6CD39B7F2DCAFEF0BBEF7E87B585C5A76E16D9FEFF806B3F93FFD7CAE65DE3CE3B9562FA7689E50F4DC5F5F6F23780C637A7E3EE6987B7B8220351D606FG861D49A581FE7609297477B986767CC910BD99EE65E0A85F5A41D03B679B1EA5EBA1DDD7AEA6D7BC291C7EFDD3A6F7F2D2B9E33B3F
	CC7712B9A774F67FBD458B2DFA3B0FB9516EC5C07335225DA76E323F5CFF17F24F5EE5BF6B277DE6DE2B3E3ECE0A4F8C296FC23E274E82DA7BC97E0ECF501EB19970E9A72792D3783B10775E89E4A832594A3370147EDE4F37A356376C1431AFD3A2E49A6AC328B210E376F810563B619433C78147BFC2716D9ABC3353DE9746BC7A01B67B945A69112BFA7D1527D51D75274369FF072EE76D277577435F23F83B864F744FF2637D70B120E51C467862378D5631FF89DA47D83CE069ED0C97B4345DE93C70F76453
	DCCABE8779BD5331B079B019124B02007E4F373BC8B9487BD96C9CF77A28767C78704A4F9AFE7B094EEFED4CFE3315E75E0607147363655DD80EBC43F2C40A1D5D68D76B735D8E4C675047F86E7930CDC367F710250F0D75B0196FDDD1E5FE496F94A772BCAB6CAAFD006719E118A7167304284AFC0967D9FAA6120CEBD01559FD94E55AA24AFCE1C81559D44B6FF0C714C9F228B27692FE475BD0A6D2DB7C4C2966B30D1FDB67C4547D2ACB15794803B2D51FC41259AC29B20F73382BBD22CC3DDB15594375A4C4
	14396CD1E58E73D8633C08B21F752AB2A2AFDBD1FBA4192F51FB91EC8E7A3EC77DBDD7733B47F36F4FE52F5ED39DECBE9B70064EF18E2FF60EE371A25BEEB2C7BABDCE59E913283B4B70DB47D62BF8F4F71572FFF6D83557DFE9FE178E5FFB5B6D3B27BA181DF6C821362B103436E36F0E272EE7B6121C9E3109FDC51417B34B295AE5116677AD673D3751037F83BA86B56D6E557C4EF55DFB5DA57F83FD2040208F18037AG43A5BB703C36391D1F0B73B375F60E5BCCFD006910CE65381F707350F99C771A83F1
	95E751DF7ABB96F761697FCD53F1BC3D7F2EECCE721B3A4DDB6FCA47D45FB5D14E026979CCB214AB6DAC5A637DG5AA30B5737062EAB759DA6CBDF8762BE24B89F24E3B96E9977276DA7C487F976F50473257F7DD36BC09B99FD231669D8FEBBE87F8AE519EDD01ED03943C17B03F410491AC40E1DB3F750F690BACD162D1DF8D65CFD79DDA17CD9F13B5F6B937D72B84352AFEDAAE5D8552DADC3BD64575EB915326F8D3ADFEC871931A949BD9BF46FFA9A48CC1F125CCBC1772013C1A6EDCAF20EA0FD99A013BD
	A53938A0FD05D613254CBA9539F70FEA652AC026E1CAF2DBEB35F2D610F1CCC94ED922151BG1958A949FDB9A8B61A88B2A9D312EB8E0A0D4E81194CA949BD999463498199F357D4642A0274ED85191AA9499D895A6BB703CC4F1464DCC16514C0A6EA92B9663BBECB0E7F1578F71593635FF186327A3311FA659C864FD9E7F469E36DB5AE62AFBB3CFE67F52FC736C947A46710075C069358396BA93ACE11FDE8F2971E3BAE60E73D1986E5501FAD64F429E7C1741C29DBAD03F286436BD45A9D7E2C954F68AA01
	273EDB3D4F5A5E2D2F53817A0E2F664344642B1374CEAB3BB31A9E727DG721ED6DFB7FEB7E46E55507B4CAF73BB223EEEF6AFB7861E89G643DAED60FB3812CC732C1BD509EF8D61B86BC991AFAE407546349AD6A3DF27A39E65D37BDB6342A5F5840EFDF38BD7A3A117265F563FD59F12F913D789C3FFFF5BB547779536DD05F6793D7A81F5595FA9EF06EAA51436FF53265C121DD3ACA4A226CB31C576977D4BA0151B324686D8E553127BB34AC8D56D03E96EBA85F73DD21FCAFF605723D5E994A3724B3146F
	36C1FE518679BDE910DF12C1FE0DC7C379CE9C8D65BBD89B4AF738B614AF2AA4146F3E12D03E9986316E2F9B443AAFF8C2797ABD21FC4F98784FBFB0701F1B8C72EBB5484FEA90336B9D8E657B0C015E4F8769D55EC3676F58D71C43F8539F685DFEB62EEC2EF2F8EA2FE4F5127F71CAF0203A636409D62B72BE3C7A3E7939C8712ED2FAAFDC0941F07DF1E1E44267737E081A4F5AE13C93B3679C5E79DA634778F4CD8F60FA707ED3379F6FFCCD6F417BCF5114B79D5259G66DE643D9D407215775EE9FC6A7C28
	DA3EC49E836A61F9A72977B0FB51D73F92F4D61186780C5EA93D779C348EE703CC6179A97999C17A4AC026EACAF246B10E06C9E40D402A48B8B4320A8E7F5D1DFF7C69AE65AE5C319C0B3B67523EB3F1DEFFEE32B4C070B19ABC6B177773FE9987345C73587651812D7E3E8B50B7AFCC25AD1EB0BCC70FD6E4638D72D06EFEFA0D64E6AA7C2986F24A7DEFE7685CF24B99BA873DE7B0D755994CD5978C726BB7486F3F286CBCA8577C8B38C67FAF7EEEFAB647FD1F4EF325FDA64B21BE7E5DC16AB7325FAB826A6F
	D87F3D0F53BCF1CFF5A8F688045AF0FAA05406299E0C533476213D5E76228D2D9CB7076292A11D4CF135BE64CB6DC37F70E35D7D141B74CEB87BAE5DBF8D231FD8FC51E429GE8863083C4DFC479972448BE77EB924E550997F1FDCA9EA7BA181FF9183F5FE884DA9F123E7FF5B178DD7A859775FE66CB5E607AE4CC283ADAFD28AB676264F7C6713DFD5CEF18AF293A587C1F4E574B52CB784D1FE2C97F4D9FF2AB32DA40EF76B4CA7AEF7E107CD43AAC693F7973CA3E167E38247F668F4A337A2FF13FADC4DC6F
	1BDA4ED8DBBA8423B3BFDC17AF52FB362C0E63BEFE0EF4895BAF0A5E15E5E3630520F5AB9AF862AFCDE54E3B65534A4D841954C964D89F4E61F392310B761D8FB2CE082D996E81DF68F8FA48279DCF6C6F82D63ECF7EF8604C5F8A4BE421103C908B8F725E2BACACE7AFDF428F762625AC2CA6AF6517F7F98283B6C934877C42DA7E0783CE520BAE19A3876B8ADEFA2CA0DFA8BCA9ACE797FFCE8A0564764EC9E1993D5CF3D2A8103D3EB1F202D3280540D561967BDC4F1F99D61005D5F47BD9CEDEF42C13053564
	D9DE6926DFE18C6C85CCB15D14162B9F121405B2761146C0792057DFBE6A70CAE2F9CF39ED4C991005025A6A9AD9486F81417C5EBA9D6BB94A5A9B4CFA5E186BFC9057BAD688F527AC8634DAE8A6ABD8C5FA782FDE7E6B7C27B794478AFFE6CDD87867DBB2D9D8C65E442D8B218A7FDD791B4FF84BE95710059235F3145716FB87C9C7D8578A23D2F24A223D5C69A9EF72CADE7F1D64AF603D063D3735779AF26F9B88FB2FA1EDC0F9AFFEC0FDBF1D452B3DF5E4FEAA33035FE347F8753FFC30CF887FDED6275BA6
	C9306A8E9F735849BBDA0D6332283EE7AA57117C83482390996DC69DA937CBA3737F81D0CB87882C0D3FABA1A3GG04F5GGD0CB818294G94G88G88GA8FBB0B62C0D3FABA1A3GG04F5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGDBA3GGGG
**end of data**/
}
}

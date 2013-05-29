/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
/**
 * This type was created in VisualAge.
 */
public class BrowseImage {
	public static final int BROWSE_XSIZE = 150;
	public static final int BROWSE_YSIZE = 150;

/**
 * BrowseImage constructor comment.
 */
private BrowseImage() {
	super();
}

private static byte[] createGifFromImage(Image image) throws IOException{

	image = new ImageIcon(image).getImage();
	BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
	Graphics g = bi.createGraphics();
	g.drawImage(image, 0, 0, null);
	g.dispose();
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	ImageIO.write(bi, "gif", bos);
	return bos.toByteArray();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 * @param vci VCImage
 */
private static byte[] gifFromVCImage(VCImage vci) throws ImageException, IOException {
	byte[] grey = new byte[256];
	for (int c = 0; c < 256; c += 1){
		grey[c] = (byte) c;
	}
	IndexColorModel icm = new IndexColorModel(8, 256, grey, grey, grey);
	BufferedImage bufferedImage = new BufferedImage(vci.getNumX(), vci.getNumY()*vci.getNumZ(), BufferedImage.TYPE_BYTE_INDEXED, icm);
	WritableRaster raster = bufferedImage.getRaster();
	DataBufferByte dataBuffer = (DataBufferByte)raster.getDataBuffer();
	System.arraycopy(vci.getPixels(), 0, dataBuffer.getData(), 0,  vci.getPixels().length);
	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	ImageIO.write(bufferedImage, "gif", byteArrayOutputStream);
	byteArrayOutputStream.close();
	return byteArrayOutputStream.toByteArray();
}

//public static void main(String args[]){
//	try {
//		byte[] imagePixels = new byte[100*100];
//		for (int i=0;i<100;i++){
//			for (int j=0;j<100;j++){
//				double radius = Math.sqrt(((i-50)*(i-50)) + ((j-50)*(j-50)));
//				imagePixels[i+100*j] = (byte)(3.5*radius);
//			}
//		}
//		cbit.image.VCImageUncompressed vcImageUncompressed = new cbit.image.VCImageUncompressed(null,imagePixels,new org.vcell.util.Extent(1,1,1),100,100,1);
//		byte[] gifImageBytes = cbit.image.BrowseImage.gifFromVCImage(vcImageUncompressed);
//		java.io.FileOutputStream fos = new java.io.FileOutputStream(new java.io.File("c:\\temp\\darkCenter.gif"));
//		fos.write(gifImageBytes);
//		fos.close();
//		
//		System.out.println("done");
//	}catch (Exception e){
//		e.printStackTrace(System.out);
//	}
//}

/**
 * This method was created in VisualAge.
 * @return byte[]
 * @param vci VCImage
 */
public static GIFImage makeBrowseGIFImage(cbit.image.VCImage vci) throws cbit.image.ImageException ,cbit.image.GifParsingException{
	if (vci == null) {
		throw new ImageException("ImageAttributes.makeBrowseImage: Bad parameters");
	}
	try{
		return new GIFImage(makeBrowseImage(vci));
	}catch(Exception e){
		e.printStackTrace(System.out);
		throw new cbit.image.GifParsingException("Error creating gif image: "+e.getMessage(),e);
	}
}
/**
 * This method was created in VisualAge.
 * @return byte[]
 * @param vci VCImage
 */
private static byte[] makeBrowseImage(cbit.image.VCImage vci) throws cbit.image.ImageException {
	if (vci == null){
		throw new ImageException("ImageAttributes.makeBrowseImage: Bad parameters");
	}
	try {
		byte[] newgif = gifFromVCImage(vci);
		BufferedImage imageTemp = ImageIO.read(new ByteArrayInputStream(newgif));
		int xw = -1;
		int yw = -1;
		if (vci.getNumX() < vci.getNumY()){
			yw = 150;
		}else{
			xw = 150;
		}
		java.awt.Image browseImage = imageTemp.getScaledInstance(xw, yw, java.awt.Image.SCALE_REPLICATE);

		// Make sure width and height are a minimum of 1 pixel
		if (browseImage.getWidth(null)==0){
			browseImage = imageTemp.getScaledInstance(1, 150, java.awt.Image.SCALE_REPLICATE);
		}
		if (browseImage.getHeight(null)==0){
			browseImage = imageTemp.getScaledInstance(150, 1, java.awt.Image.SCALE_REPLICATE);
		}
		return createGifFromImage(browseImage);
	}catch (IOException e){
		e.printStackTrace(System.out);
		throw new ImageException(e.getMessage());
	}
}
}

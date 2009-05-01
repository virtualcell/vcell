package cbit.image;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.image.ImageObserver;
import java.io.Serializable;

import org.vcell.util.ISize;

/**
 * This type was created in VisualAge.
 */
public class GIFImage implements Serializable,ImageObserver{
	private byte gifEncodedData[] = null;
	private ISize size = null;

/**
 * GIFImage constructor comment.
 */
public GIFImage(byte AgifEncodedData[]) throws GifParsingException{
	this.gifEncodedData = AgifEncodedData;
	try{
		getJavaImage();
	}catch(Throwable e){
		throw new GifParsingException("Error parsing gifEncodedData");
	}
}


/**
 * This method was created in VisualAge.
 * @return byte[]
 */
public byte[] getGifEncodedData() {
	return gifEncodedData;
}


/**
 * This method was created in VisualAge.
 * @return java.awt.Image
 */
public java.awt.Image getJavaImage() {
	return java.awt.Toolkit.getDefaultToolkit().createImage(gifEncodedData, 0, gifEncodedData.length);
}


/**
 * This method was created in VisualAge.
 * @return ISize
 */
public ISize getSize() {

	if(size != null){
		return size;
	}
	java.awt.Image javaImage = getJavaImage();
	
	int height;
	do{
		height = javaImage.getHeight(this);
	}while(height == -1);
	
	int width;
	do{
		width = javaImage.getHeight(this);
	}while(width == -1);
	
	this.size = new ISize(width,height,1);
	return this.size;
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param img Image
 * @param info int
 * @param x int
 * @param y int
 * @param width int
 * @param height int
 */
public boolean imageUpdate(java.awt.Image img,int info,int x,int y,int width,int height) {
	if((info & (ImageObserver.WIDTH+ImageObserver.HEIGHT)) != 0){
		return true;
	}
	return false;
}
}
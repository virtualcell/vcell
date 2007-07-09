package cbit.image;


/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.lang.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

import org.vcell.util.Extent;

import cbit.image.*;


public class DisplayImage implements ImageObserver
{
	private int originalRGB[] = null;
	private double originalDouble[] = null;
	private int sizeX = 0;
	private int sizeY = 0;
	private int sizeZ = 0;
	private boolean bColor = false;
	private String imageName = null;
/**
 * This method was created by a SmartGuide.
 * @param rgb int[]
 * @param x int
 * @param y int
 * @param z int
 * @exception java.lang.Exception The exception description.
 */
public DisplayImage (double doubles[], int x, int y, int z) throws ImageException {
	if (x<1 || y<1 || z<1){
		throw new ImageException("x="+x+", y="+y+", z="+z+"  all should be >= 1");
	}
	if (doubles.length != x*y*z){
		throw new ImageException("pixel array length = "+doubles.length+",   x="+x+", y="+y+", z="+z+"  x*y*z="+(x*y*z));
	}
	originalDouble = doubles;
	originalRGB = null;
	sizeX = x;
	sizeY = y;
	sizeZ = z;
	imageName = "un-named";
}
/**
 * This method was created by a SmartGuide.
 * @param rgb int[]
 * @param x int
 * @param y int
 * @param z int
 * @exception java.lang.Exception The exception description.
 */
public DisplayImage (int rgb[], int x, int y, int z) throws ImageException {
	if (x<1 || y<1 || z<1){
		throw new ImageException("x="+x+", y="+y+", z="+z+"  all should be >= 1");
	}
	if (rgb.length != x*y*z){
		throw new ImageException("pixel array length = "+rgb.length+",   x="+x+", y="+y+", z="+z+"  x*y*z="+(x*y*z));
	}
	originalRGB = rgb;
	originalDouble = null;
	sizeX = x;
	sizeY = y;
	sizeZ = z;
	imageName = "un-named";

	//
	// load in double values
	//
	originalDouble = new double[originalRGB.length];
	for (int i=0;i<originalDouble.length;i++){
		Color c = new Color(originalRGB[i]);
		originalDouble[i] = Math.max(c.getRed(),Math.max(c.getGreen(),c.getBlue()));
	}
}
/**
 * This method was created in VisualAge.
 * @param x int
 * @param y int
 * @param z int
 */
public DisplayImage(int x, int y, int z) throws ImageException {
	this(new int[x*y*z],x,y,z);
}
/**
 * This method was created in VisualAge.
 * @param image image.Image
 */
public DisplayImage(VCImage vcImage) throws ImageException {
	sizeX = vcImage.getNumX();
	sizeY = vcImage.getNumY();
	sizeZ = vcImage.getNumZ();
	byte vciPixels[] = vcImage.getPixels();
	if (sizeX*sizeY*sizeZ!=vciPixels.length){
		throw new ImageException("pixelData not properly formed");
	}
	originalRGB = new int[sizeX*sizeY*sizeZ];
	for (int i=0;i<vcImage.getPixels().length;i++){
		int pixel = ((int)vciPixels[i])&0xff;
		originalRGB[i] = new java.awt.Color(pixel,pixel,pixel).getRGB();
	}
	imageName = (vcImage.getVersion()!=null)?vcImage.getVersion().getName():"unnamedImage";
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.image.FileImage
 * @param images cbit.image.FileImage[]
 * @exception java.lang.Exception The exception description.
 */
public static DisplayImage concatenateZSeries(DisplayImage images[]) throws ImageException {
	if (images.length == 1){
		return images[0];
	}	
	int sizeX = images[0].getSizeX();
	int sizeY = images[0].getSizeY();
	int sizeZ = images[0].getSizeZ();
	for (int i=1;i<images.length;i++){
		if (images[i].getSizeX() != sizeX){
			throw new ImageException("image "+(i+1)+" x dimension doesn't match the first image");
		}	
		if (images[i].getSizeY() != sizeY){
			throw new ImageException("image "+(i+1)+" y dimension doesn't match the first image");
		}	
		if (images[i].getSizeZ() < 1){
			throw new ImageException("image "+(i+1)+" z dimension must be at least 1");
		}	
		sizeZ += images[i].getSizeZ();
	}
	int size = sizeX*sizeY*sizeZ;
	int bigRGB[] = new int[size];
	int index = 0;
	for (int i=0;i<images.length;i++){
		int currRGB[] = images[i].getRGB();
		int currSize = images[i].getSizeX()*images[i].getSizeY()*images[i].getSizeZ();
		for (int j=0;j<currSize;j++){
			bigRGB[index++] = currRGB[j];
		}
	}		
	DisplayImage displayImage = new DisplayImage(bigRGB,sizeX,sizeY,sizeZ);
	displayImage.imageName = "Z-series";
	return displayImage;
}
/**
 * This method was created by a SmartGuide.
 * @param urlString java.lang.String
 */
public static DisplayImage createFromURL(String urlString) throws ImageException, java.net.MalformedURLException, InterruptedException {
	DisplayImage displayImage = new DisplayImage(10,10,1);
System.out.print("creating URL = "+urlString+"...");
	java.net.URL url = new java.net.URL(urlString);
System.out.println("done");
System.out.print("creating image from url ...");
	java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().getImage(url);
System.out.println("done");
System.out.print("getting image width ...");
	while (image.getWidth(displayImage)<0){}
System.out.println("done");
	while (image.getHeight(displayImage)<0){}
	displayImage.sizeX = image.getWidth(displayImage);
	displayImage.sizeY = image.getHeight(displayImage);
System.out.println("width="+displayImage.sizeX+" height="+displayImage.sizeY);
	displayImage.originalRGB = new int[displayImage.sizeX*displayImage.sizeY]; 
	PixelGrabber pg = new PixelGrabber(image,0,0,displayImage.sizeX,displayImage.sizeY,displayImage.getRGB(),0,displayImage.sizeX);
	pg.grabPixels();
	if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
		throw new ImageException("image fetch aborted or errored");
	}
	displayImage.originalDouble = new double[displayImage.originalRGB.length];
	for (int i=0;i<displayImage.originalDouble.length;i++){
		Color c = new Color(displayImage.originalRGB[i]);
		displayImage.originalDouble[i] = Math.max(c.getRed(),Math.max(c.getGreen(),c.getBlue()));
	}
	return displayImage;
}
/**
 * This method was created in VisualAge.
 * @return double[]
 */
public double[] getDoubles() {
	return originalDouble;
}
/**
 * This method was created in VisualAge.
 * @return cbit.image.FileImage
 */
public static DisplayImage getExample() throws ImageException {
	int xsize = 100;
	int ysize = 100;
	int zsize = 1;
	int data[] = new int[xsize*ysize*zsize];
	for (int i=0;i<xsize;i++){ 
		for (int j=0;j<ysize;j++){
			for (int k=0;k<zsize;k++){
				int num = (int)(128.0 + 127.0*Math.sin(((float)(i*j))/((xsize*ysize)/10)));
				data[i+j*xsize+k*xsize*ysize] = (new java.awt.Color(num,num,num)).getRGB();
			}
		}
	}
	return new DisplayImage(data, xsize, ysize, zsize);
}
/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String getName() {
	return imageName;
}
/**
 * This method was created by a SmartGuide.
 * @return int[]
 */
public int[] getRGB() {
	return originalRGB;
}
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getSizeX() {
	return sizeX;
}
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getSizeY() {
	return sizeY;
}
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getSizeZ() {
	return sizeZ;
}
/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 */
public VCImage getVCImage() throws ImageException {
	if (originalRGB==null){
		throw new RuntimeException("rgb values not availlable, can't create VCImage");
	}
	//
	// load in double values
	//
	byte data[] = new byte[originalRGB.length];
	for (int i=0;i<data.length;i++){
		Color c = new Color(originalRGB[i]);
		data[i] = (byte)(0xff & Math.max(c.getRed(),Math.max(c.getGreen(),c.getBlue())));
	}
	return new cbit.image.VCImageUncompressed(null,data, new Extent(1,1,1),getSizeX(),getSizeY(),getSizeZ());
}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @param image java.awt.Image
 * @param infoflags int
 * @param x int
 * @param y int
 * @param width int
 * @param height int
 */
public final boolean imageUpdate(java.awt.Image image, int infoflags, int x, int y, int width, int height) {
	return false;
}
}

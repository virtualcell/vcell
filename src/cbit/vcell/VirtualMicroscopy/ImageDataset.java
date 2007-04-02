package cbit.vcell.VirtualMicroscopy;

import java.awt.Rectangle;

import cbit.image.ImageException;

/**
 * Insert the type's description here.
 * Creation date: (1/24/2007 4:18:01 PM)
 * @author: Anuradha Lakshminarayana
 */

public class ImageDataset {
	private UShortImage[] images = null;
	private double[] imageTimeStamps = null;

/**
 * FRAPData constructor comment.
 */
public ImageDataset(UShortImage[] argImages, double[] argTimeStamps) {
	super();

	// Error checking
	if (argImages.length == 0) {
		throw new RuntimeException("Cannot perform FRAP analysis on null image.");
	}
	if (argTimeStamps!=null && (argImages.length != argTimeStamps.length)) {
		throw new RuntimeException("Cannot have unequal images and timeStamps.");
	}

	// Now initialize
	images = argImages;
	imageTimeStamps = argTimeStamps;
}


/**
 * Insert the method's description here.
 * Creation date: (1/24/2007 4:19:16 PM)
 * @return java.lang.Float
 */
public UShortImage[] getImages() {
	return images;
}


/**
 * Insert the method's description here.
 * Creation date: (1/24/2007 4:33:01 PM)
 * @return float[]
 */
public double[] getImageTimeStamps() {
	return imageTimeStamps;
}


public int getSizeZ() {
	return 1;
}


public int getSizeT() {
	return images.length;
}


public int getSizeC() {
	return 1;
}


public int[] getZCT(int ndx) {
	// TODO Auto-generated method stub
	return new int[] { 0, 0, ndx };
}


public int getIndexFromZCT(int z, int c, int t) {
	// TODO Auto-generated method stub
	return z+c*getSizeZ()+t*getSizeZ()*getSizeC();
}


public ImageDataset crop(Rectangle rect) throws ImageException {
	UShortImage[] croppedImages = new UShortImage[images.length];
	for (int i = 0; i < croppedImages.length; i++) {
		croppedImages[i] = images[i].crop(rect);
	}
	double[] clonedImageTimeStamps = new double[imageTimeStamps.length];
	System.arraycopy(imageTimeStamps, 0, clonedImageTimeStamps, 0, imageTimeStamps.length);
	return new ImageDataset(croppedImages,clonedImageTimeStamps);
}

}
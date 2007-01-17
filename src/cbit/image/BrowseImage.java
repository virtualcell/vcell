package cbit.image;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.image.*;
import cbit.image.*;
import java.io.*;
import java.util.*;
/**
 * This type was created in VisualAge.
 */
public class BrowseImage {
	public static final int BROWSE_XSIZE = 150;
	public static final int BROWSE_YSIZE = 150;

/**
 * BrowseImage constructor comment.
 */
public BrowseImage() {
	super();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 * @param vci VCImage
 */
private static byte[] gifFromImage(byte[] image, ImageFilter imageFilter) throws IOException {
	java.awt.Image imageTemp = java.awt.Toolkit.getDefaultToolkit().createImage(image);
	return gifFromImageProducer(imageTemp.getSource(), imageFilter);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 * @param vci VCImage
 */
private static byte[] gifFromImageProducer(ImageProducer ip, ImageFilter imageFilter) throws IOException {
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	FilteredImageSource fis = new FilteredImageSource(ip, imageFilter);
	Acme.JPM.Encoders.ImageEncoder ge = new Acme.JPM.Encoders.GifEncoder(fis, bos);
	ge.encode();
	byte[] newgif = bos.toByteArray();
	return newgif;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 * @param vci VCImage
 */
public static byte[] gifFromVCImage(VCImage vci) throws ImageException {
	try {
		return gifFromVCImage(vci, new ImageFilter());
	}catch (IOException e){
		e.printStackTrace(System.out);
		throw new ImageException(e.getMessage());
	}
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 * @param vci VCImage
 */
private static byte[] gifFromVCImage(VCImage vci, ImageFilter imageFilter) throws ImageException, IOException {
	byte[] grey = new byte[256];
	for (int c = 0; c < 256; c += 1)
		grey[c] = (byte) c;
	IndexColorModel icm = new IndexColorModel(8, 256, grey, grey, grey);
	MemoryImageSource mis = new MemoryImageSource(vci.getNumX(), vci.getNumY(), icm, vci.getPixels(), 0, vci.getNumX());
	return gifFromImageProducer(mis, imageFilter);
}
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
		throw new cbit.image.GifParsingException(e.getMessage());
	}
}
/**
 * This method was created in VisualAge.
 * @return byte[]
 * @param vci VCImage
 */
public static byte[] makeBrowseImage(cbit.image.VCImage vci) throws cbit.image.ImageException {
	if (vci == null){
		throw new ImageException("ImageAttributes.makeBrowseImage: Bad parameters");
	}
	try {
		byte[] newgif = gifFromVCImage(vci, new java.awt.image.ImageFilter());
		java.awt.Image imageTemp = java.awt.Toolkit.getDefaultToolkit().createImage(newgif);
		int xw = -1;
		int yw = -1;
		if (vci.getNumX() < vci.getNumY()){
			yw = 150;
		}else{
			xw = 150;
		}
		java.awt.Image browseImage = imageTemp.getScaledInstance(xw, yw, java.awt.Image.SCALE_REPLICATE);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
		byte[] browseGif;
		Acme.JPM.Encoders.GifEncoder ge = new Acme.JPM.Encoders.GifEncoder(browseImage, bos);
		ge.encode();
		bos.flush();
		browseGif = bos.toByteArray();
		return browseGif;
	}catch (IOException e){
		e.printStackTrace(System.out);
		throw new ImageException(e.getMessage());
	}
}
/**
 * This method was created in VisualAge.
 * @return byte[][]
 * @param vci cbit.image.VCImageCompressed
 */
public static GIFImage makePixelClassImage(VCImage vci, byte[] browseImage, VCPixelClass pixelClass) throws ImageException, cbit.image.GifParsingException {
	try {
		VCImageUncompressed vciu = new VCImageUncompressed(vci);
		int pixel = pixelClass.getPixel();
		HighlightImageFilter hif = new HighlightImageFilter((byte) pixel, (byte) pixel, (byte) pixel, (byte) 255, (byte) 0, (byte) 0);
		if (browseImage != null) {
			return new GIFImage(gifFromImage(browseImage, hif));
		} else {
			return new GIFImage(gifFromVCImage(vciu, hif));
		}
	}catch (IOException e){
		e.printStackTrace(System.out);
		throw new ImageException(e.getMessage());
	}
}
}

package cbit.image;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.awt.image.PixelGrabber;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.DirectColorModel;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

import org.vcell.util.ISize;
/**
 * This type was created in VisualAge.
 */
public class NativeImage implements Serializable,ImageObserver{
	private byte nativeData[] = null;
	private ISize size = null;
	private boolean imageError = false;

	//Do this stuff instead of using IndexColorModel and DirectColorModel directly because they aren't Serializable
	private byte[] newIndexPixels = null;	//index color pixels
	IndexColorMap colorMap = null;
	private static final int colorMapLimit = 256;	//When converting a DirectColorModel, only this many index colors are allowed
	
	private int[] newRGBAPixels = null;		//rgba color pixels

/**
 * GIFImage constructor comment.
 */
public NativeImage(byte[] AgifEncodedData) throws Exception{
	this.nativeData = (byte[])AgifEncodedData.clone();
	try{
		getSize();
	}catch(Throwable e){
		throw new Exception("Error Parsing Data, Format not understood by java.awt.Image.");
	}
}


/**
 * This method was created in VisualAge.
 * @return boolean
 */
private boolean bPixelsExist() {
	if ((newIndexPixels == null) && (newRGBAPixels == null)) {
		return false;
	}
	return true;
}


/**
 * This method was created in VisualAge.
 */
private byte[] createIndexPixels() throws Exception {
	createPixels();
	if (newIndexPixels != null) {
		return newIndexPixels;
	}
	Vector indexes = new Vector(); // distinct pixel colors(Color Map)
	byte[] tryIndexPixels = new byte[newRGBAPixels.length]; //create space for new pixels
	for (int c = 0; c < newRGBAPixels.length; c += 1) {
		Integer currentPixelColor = new Integer(newRGBAPixels[c]);
		byte index = (byte) indexes.indexOf(currentPixelColor); //Get index of current pixel color
		if (index == -1) { //Didn't find index for pixel color,add it to index
			if (indexes.size() >= colorMapLimit) { //Colormap full,can't go any further
				colorMap = null;
				throw new Exception("Create index pixels exceeded limit of " + colorMapLimit);
			}
			indexes.addElement(currentPixelColor); //add new Pixel Color to Color Map
			index = (byte) (indexes.size() - 1);
		}
		tryIndexPixels[c] = index; //add new Pixel
	}
	//make packed colormap array for IndexColorMap R,G,B
	byte[] trycolorMap = new byte[indexes.size() * IndexColorMap.RGB_PACK_SIZE];
	for (int c = 0; c < indexes.size(); c += 1) {
		int rgba = ((Integer) indexes.elementAt(c)).intValue();
		int packCount = c * IndexColorMap.RGB_PACK_SIZE;
		trycolorMap[packCount] = (byte) ((rgba >> 16) & 0xff); //red
		trycolorMap[packCount + 1] = (byte) ((rgba >> 8) & 0xff);//green
		trycolorMap[packCount + 2] = (byte) (rgba & 0xff);//blue
	}
	newIndexPixels = tryIndexPixels;
	colorMap = new IndexColorMap(trycolorMap);
	return newIndexPixels;
}


/**
 * This method was created in VisualAge.
 * @return byte[]
 */
private void createPixels() throws Exception {
	if(bPixelsExist()){
		return;
	}
	PixelGrabber pg = new PixelGrabber(getJavaImage(), 0, 0, getSize().getX(), getSize().getX(), false);
	if (!pg.grabPixels()) {
		throw new Exception("Error getting pixels, status = " + pg.getStatus());
	}
	Object pixels = pg.getPixels();
	ColorModel colorModel = null;
	while (colorModel == null) {
		colorModel = pg.getColorModel();
	}
	
	if ((pixels instanceof int[]) && (colorModel instanceof DirectColorModel)) {
		newRGBAPixels = (int[])pixels;
	} else
		if ((pixels instanceof byte[]) && (colorModel instanceof IndexColorModel)) {
			newIndexPixels = (byte[])pixels;
			IndexColorModel indexColorModel = (IndexColorModel)colorModel;
			int colorMapSize = indexColorModel.getMapSize();
			byte[] temp_colorMap = new byte[colorMapSize*IndexColorMap.RGB_PACK_SIZE];
			byte[] reds = new byte[colorMapSize];
			indexColorModel.getReds(reds);
			byte[] greens = new byte[colorMapSize];
			indexColorModel.getGreens(greens);
			byte[] blues = new byte[colorMapSize];
			indexColorModel.getBlues(blues);
			for(int c = 0;c<colorMapSize;c+= 1){//make packed R,G,B array for ColorIndexModel
				int packCount = c*IndexColorMap.RGB_PACK_SIZE;
				temp_colorMap[packCount] = reds[c];
				temp_colorMap[packCount+1] = greens[c];
				temp_colorMap[packCount+2] = blues[c];
			}
			colorMap = new IndexColorMap(temp_colorMap);
		} else {
			throw new Exception("Unknown combination of data type=" + pixels.getClass().toString() + " and ColorModel=" + colorModel.getClass().toString());
		}
}


/**
 * This method was created in VisualAge.
 * @return int[]
 */
private int[] createRGBAPixels() throws Exception{
	createPixels();
	if(newRGBAPixels != null){
		return newRGBAPixels;
	}
	//Get rgb colors from index color pixels
	IndexColorModel colorModel = getIndexColorModel();
	int[] tryRGBAPixels = new int[newIndexPixels.length];
	for(int c = 0;c<tryRGBAPixels.length;c+= 1){
			tryRGBAPixels[c] = colorModel.getRGB((int)(0xFF&newIndexPixels[c]));
	}
	newRGBAPixels = tryRGBAPixels;
	return newRGBAPixels;
}


/**
 * This method was created in VisualAge.
 * @return java.awt.image.ColorModel
 */
public IndexColorModel getIndexColorModel() throws Exception{
	createPixels();
	createIndexPixels();
	return colorMap.getIndexColorModel();
}


/**
 * This method was created in VisualAge.
 */
public byte[] getIndexPixels() throws Exception {
	return createIndexPixels();

}


/**
 * This method was created in VisualAge.
 * @return java.awt.Image
 */
public java.awt.Image getJavaImage() {
	return java.awt.Toolkit.getDefaultToolkit().createImage(nativeData, 0, nativeData.length);
}


/**
 * This method was created in VisualAge.
 * @return byte[]
 */
public byte[] getNativeData() {
	return nativeData;
}


/**
 * This method was created in VisualAge.
 */
public int[] getRGBAPixels() throws Exception {
	return createRGBAPixels();

}


/**
 * This method was created in VisualAge.
 * @return ISize
 */
public ISize getSize() {

	if(this.size != null){
		return size;
	}
	java.awt.Image javaImage = getJavaImage();
	
	int height;
	int width;
	do{
		height = javaImage.getHeight(this);
		width = javaImage.getWidth(this);
		if(imageError){
			throw new RuntimeException();
		}
	}while((height == -1) || (width == -1));
		
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
public boolean imageUpdate(java.awt.Image img,int info,int x,int y,int width,int height){
	if((info & (ImageObserver.ERROR + ImageObserver.ABORT)) != 0){
		imageError = true;
		return false;//No further update needed
	}

	if(((info & ImageObserver.WIDTH) != 0) && ((info & ImageObserver.HEIGHT) != 0)){
		return false;//No further update needed, have width and height
	}
	return true;//More Info needed
}
}
package cbit.image;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.image.IndexColorModel;
/**
 * This type was created in VisualAge.
 */
public class IndexColorMap implements java.io.Serializable,java.lang.Cloneable{
	private byte[] colorMapPacked = null;		//packed RGB 8bit pixels for colormap(refer to IndexColorModel constructor)
	public static final int RGB_PACK_SIZE = 3;	//3 pixels(bytes) per color 1Red, 1Green, 1Blue in the pack

/**
 * IndexColorMap constructor comment.
 */
public IndexColorMap(byte[]  cmapPacked) {
	super();
	colorMapPacked = cmapPacked;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.image.IndexColorModel
 */
public IndexColorModel getIndexColorModel() {
	return new IndexColorModel(8,colorMapPacked.length/RGB_PACK_SIZE,colorMapPacked,0,false);
}
}

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

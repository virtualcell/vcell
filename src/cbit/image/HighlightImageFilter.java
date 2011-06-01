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

/**
 * This type was created in VisualAge.
 */
public class HighlightImageFilter extends java.awt.image.ImageFilter{
	byte rin;
	byte gin;
	byte bin;
	byte rout;
	byte gout;
	byte bout;
	private java.awt.image.ColorModel highlightColorModel = null;
/**
 * HighlightImageFilter constructor comment.
 */
public HighlightImageFilter(byte rin,byte gin,byte bin,byte rout,byte gout,byte bout) {
	super();
	this.rin = rin;
	this.gin = gin;
	this.bin = bin;
	this.rout = rout;
	this.gout = gout;
	this.bout = bout;
}
/**
 * This method was created in VisualAge.
 * @param cm ColorModel
 */
public void setColorModel(java.awt.image.ColorModel cm) {
	if (cm instanceof java.awt.image.IndexColorModel) {
		//System.out.println("ColorModel called on HighlighImageFilter ColorModel(IndexColorModel) " + cm.toString());
		java.awt.image.IndexColorModel icm = (java.awt.image.IndexColorModel) cm;
		int mapSize = icm.getMapSize();
		System.out.println("MapSize = " + mapSize);
		byte[] r = new byte[mapSize];
		icm.getReds(r);
		byte[] g = new byte[mapSize];
		icm.getGreens(g);
		byte[] b = new byte[mapSize];
		icm.getBlues(b);
		for (int c = 0; c < mapSize; c += 1) {
			//System.out.println("Pix " + c + " R " + r[c] + " G " + g[c] + " B " + b[c]);
			if ((rin == r[c]) && (gin == g[c]) && (bin == b[c])) {
				r[c] = rout;
				g[c] = gout;
				b[c] = bout;
				//System.out.println("Replaced with R "+r[c]+" G "+g[c]+" B "+b[c]);
			}
		}
		highlightColorModel = new java.awt.image.IndexColorModel(8, mapSize, r, g, b);
	} else {
		highlightColorModel = cm;
	}
	super.setColorModel(highlightColorModel);
}
/**
 * This method was created in VisualAge.
 */
public void setPixels(int x, int y, int w, int h, java.awt.image.ColorModel model, byte pixels[], int off, int scansize) {
	if (highlightColorModel == null)
		super.setPixels(x, y, w, h, model, pixels, off, scansize);
	else
		super.setPixels(x, y, w, h, highlightColorModel, pixels, off, scansize);
}
}

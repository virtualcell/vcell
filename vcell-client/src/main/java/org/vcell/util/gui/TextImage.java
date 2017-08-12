/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
/**
 * This type was created in VisualAge.
 */
public class TextImage {
	private Font font = null;
	private String text = null;
	private Image image = null;
	private Image verticalImage = null;
	private int width = -1;  // horizontal image
	private int height = -1; // horizontal image
/**
 * TextImage constructor comment.
 */
public TextImage(String aText, Font aFont) {
	this.text = aText;
	this.font = aFont;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Image
 */
public Image getTextImage(Component component) {
	refreshImage(component);
	return image;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Image
 */
public Image getVerticalTextImage(Component component) {
	refreshVerticalImage(component);
	return verticalImage;
}
/**
 * This method was created by a SmartGuide.
 * @param g java.awt.Graphics
 */
private void refreshImage(Component component) {
	if (image != null) {
		return;
	}
	FontMetrics fontMetrics = component.getFontMetrics(font);
	int strWidth = fontMetrics.stringWidth(text);
	int strHeight = fontMetrics.getHeight();
	int strAscent = fontMetrics.getMaxAscent();
	width = strWidth + 4;
	height = strHeight + 4;
	image = component.createImage(width,height);
	if (image == null) {
		throw new RuntimeException("error creating horizontal image");
	}
	java.awt.Graphics g = image.getGraphics();
//	g.setPaintMode();
	g.setClip(0, 0, width, height);
	g.setColor(Color.white);
	g.fillRect(0, 0, width, height);
	g.setColor(Color.black);
	int posY = 2 + strAscent;
	int posX = 2;
	g.setFont(font);
	g.drawString(text, posX, posY);
}
/**
 * This method was created by a SmartGuide.
 * @param g java.awt.Graphics
 */
private void refreshVerticalImage(Component component) {
	refreshImage(component);
	if (verticalImage!=null){
		return;
	}
	//
	// copy image from horizontal image (image) to vertical image (verticalImage) (rotate 90 degrees)
	//
	int[] pixels = new int[width * height];
	PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
	try {
		pg.grabPixels();
	} catch (InterruptedException e) {
		throw new RuntimeException("interrupted waiting for pixels!");
	}
	if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
		throw new RuntimeException("image fetch aborted or errored");
	}
	int[] transposedPixels = new int[width * height];
	int transposed_i = 0;
	int transposed_j = 0;
	for (int j = 0; j < height; j++) {
		for (int i = 0; i < width; i++) {
			transposed_i = j;
			transposed_j = width - 1 - i;
			int newPixel = pixels[j * width + i];
			//
			// make background (non-foreground) pixels transparent (alpha==0)
			// choose to compare with foreground (black) because it is preserved better in color maps
			//
			if (newPixel != Color.black.getRGB()){
				newPixel = 0x00000000;
			}
			transposedPixels[transposed_i + transposed_j * height] = newPixel; // pixels[j * width + i];
		}
	}
	java.awt.image.MemoryImageSource memoryImageSource = new java.awt.image.MemoryImageSource(height, width, transposedPixels, 0, height);
	Image tempImage = component.createImage(memoryImageSource);
	int verticalWidth = -1;
	int verticalHeight = -1;
	while (verticalWidth == -1 || verticalHeight == -1) {
		verticalWidth = tempImage.getWidth(component);
		verticalHeight = tempImage.getHeight(component);
	}
	verticalImage = tempImage;
}
}

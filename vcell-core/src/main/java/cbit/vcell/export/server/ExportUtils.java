/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.server;
import java.util.zip.DataFormatException;
/**
 * Insert the type's description here.
 * Creation date: (9/8/2004 11:56:31 AM)
 * @author: Jim Schaff
 */
public class ExportUtils {
/**
 * This method was created in VisualAge.
 * @return int[]
 * @param pixels int[]
 */
public static int[] extendMirrorPixels(int[] pixels, int width, int height, int mode) throws DataFormatException {
	if (pixels.length != width * height) throw new DataFormatException("Pixel number incompatible with given width, height");
	int[] mirroredPixels;
	switch (mode) {
		case ExportConstants.MIRROR_LEFT:
			mirroredPixels = new int[pixels.length * 2];
			for (int i=0;i<height;i++) {
				for (int j=0;j<width;j++) {
					mirroredPixels[j + width + i * 2 * width] = pixels[j + i * width];
					mirroredPixels[width - j - 1 + i * 2 * width] = pixels[j + i * width];
				}
			}
			break;
		case ExportConstants.MIRROR_TOP:
			mirroredPixels = new int[pixels.length * 2];
			for (int i=0;i<height;i++) {
				for (int j=0;j<width;j++) {
					mirroredPixels[j + (i + height) * width] = pixels[j + i * width];
					mirroredPixels[j + (height - i - 1) * width] = pixels[j + i * width];
				}
			}
			break;
		case ExportConstants.MIRROR_RIGHT:
			mirroredPixels = new int[pixels.length * 2];
			for (int i=0;i<height;i++) {
				for (int j=0;j<width;j++) {
					mirroredPixels[j + i * 2 * width] = pixels[j + i * width];
					mirroredPixels[2 * width - j - 1 + i * 2 * width] = pixels[j + i * width];
				}
			}
			break;
		case ExportConstants.MIRROR_BOTTOM:
			mirroredPixels = new int[pixels.length * 2];
			for (int i=0;i<height;i++) {
				for (int j=0;j<width;j++) {
					mirroredPixels[j + i * width] = pixels[j + i * width];
					mirroredPixels[j + (2 * height - i - 1) * width] = pixels[j + i * width];
				}
			}
			break;
		default:
			mirroredPixels = pixels;
			break;
	}
	return mirroredPixels;
}
}

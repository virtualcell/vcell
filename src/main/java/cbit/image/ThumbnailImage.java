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

public class ThumbnailImage 
{
	private int rgb[] = null;
	private int sizeX = 0;
	private int sizeY = 0;
	private Object nativeImage = null;

public ThumbnailImage (int argRgb[], int x, int y, Object nativeImage) throws ImageException {
	if (x<1 || y<1){
		throw new ImageException("x="+x+", y="+y+", all should be >= 1");
	}
	if (argRgb.length != x*y){
		throw new ImageException("pixel array length = "+argRgb.length+",   x="+x+", y="+y+" x*y="+(x*y));
	}
	this.rgb = argRgb;
	sizeX = x;
	sizeY = y;
	this.nativeImage = nativeImage;
}


public int[] getRGB() {
	return rgb;
}

public int getSizeX() {
	return sizeX;
}

public int getSizeY() {
	return sizeY;
}

public Object getNativeImage(){
	return nativeImage;
}

}

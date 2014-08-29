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
import java.awt.Color;

import org.vcell.util.Range;
/**
 * This type was created in VisualAge.
 */
public abstract class DisplayAdapter {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private Range fieldScale = new Range();
	private int[] fieldPixelValues = new int[1];
	private boolean fieldAutoMode = true;
	private String fieldColorMode = new String();
	private int fieldColorModeID = 0;
	private Range fieldScaleRange = new Range();
	private Range fieldDataRange = new Range();
	public final static Color negative = Color.yellow;
	public final static Color over = Color.red;
	public final static Color under = Color.blue;
	public final static Color noRange = Color.gray;
	public final static String grayscale = "GRAYSCALE";
	public final static String color1 = "BLUE_TO_RED";
	private int[] scaledValues;


	public final static double UNDEFINED_DOMAIN_FLAG = -10e-10;
	
	protected final static int MAX_VALID_PIXEL		= 249;
	
	protected final static int PIXEL_BELOW_MIN		= 250; 
	protected final static int PIXEL_ABOVE_MAX		= 251;
	protected final static int PIXEL_NEGATIVE		= 252;
	protected final static int PIXEL_NO_RANGE		= 253;
	protected final static int PIXEL_NAN			= 254;
	protected final static int PIXEL_NOT_DEFINED	= 255;
	
/**
 * DisplayAdapter constructor comment.
 */
public DisplayAdapter() {
	super();
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * applyColorMap method comment.
 */
private void applyColorMap(int[] scaledValues) {
	int[] pixelValues = new int[scaledValues.length];
	Color[] map = new Color[1];
	Color color = new Color(0);
	switch (getColorModeID()) {
		case 0:
			map = getGrayScaleColorMap();
			break;
		case 1:
			map = getBlueToRedColorMap();
			break;
	}
	for (int i=0;i<scaledValues.length;i++) {
		pixelValues[i] = map[scaledValues[i]].getRGB();
	}
	setPixelValues(pixelValues);
}


/**
 * This method was created in VisualAge.
 */
protected int[] applyScale() {
	scaledValues = new int[getDataValues().length];
	double min = 0;
	double max = 0;
	if (getAutoMode()) {
		min = getDataRange().getMin();
		max = getDataRange().getMax();
	} else {
		min = getScaleRange().getMin();
		max = getScaleRange().getMax();
	}
/*	scales to 0 - 249
*/
	if (min == max) {
		for (int i=0;i<scaledValues.length;i++) {
			scaledValues[i] = PIXEL_NO_RANGE;
		}
	} else {
		int scaled = 0;
		for (int i=0;i<scaledValues.length;i++) {
			double value = getDataValues()[i];
			if (Double.isNaN(value)){
				scaled = PIXEL_NAN;
			}else if (value == UNDEFINED_DOMAIN_FLAG){
				scaled = PIXEL_NOT_DEFINED;
			}else if (value < 0) {
				scaled = PIXEL_NEGATIVE;
			}else{
				scaled = (int) (MAX_VALID_PIXEL * (value - min) / (max - min));
				if (scaled < 0) {
					scaled = PIXEL_BELOW_MIN;
				}else if (scaled > MAX_VALID_PIXEL) {
					scaled = PIXEL_ABOVE_MAX;
				}
			}
			scaledValues[i] = scaled;
		}
	}
	return scaledValues;
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Gets the autoMode property (boolean) value.
 * @return The autoMode property value.
 * @see #setAutoMode
 */
public final boolean getAutoMode() {
	return fieldAutoMode;
}


/**
 * This method was created in VisualAge.
 * @return java.awt.Color[]
 */
public final Color[] getBlueToRedColorMap() {
	Color[] fullMap = new Color[1148];
	// dark blue to blue
	for (int i=128;i<256;i++) {
		fullMap[i-128] = new Color(0, 0, i);
	}
	// blue to cyan
	for (int i=1;i<256;i++) {
		fullMap[i+127] = new Color(0, i, 255);
	}
	// cyan to green
	for (int i=1;i<256;i++) {
		fullMap[i+382] = new Color(0, 255, 255 - i);
	}
	// green to yellow
	for (int i=1;i<256;i++) {
		fullMap[i+637] = new Color(i, 255, 0);
	}
	// yellow to red
	for (int i=1;i<256;i++) {
		fullMap[i+892] = new Color(255, 255 - i, 0);
	}
	// now scale down to 256 colors
	Color[] colorMap = new Color[256];
	for (int i=0;i<250;i++) {
		colorMap[i] = fullMap[i * 1147 / 249];
	}
	colorMap[PIXEL_NO_RANGE]	= Color.gray;
	colorMap[PIXEL_NEGATIVE]	= Color.darkGray;
	colorMap[PIXEL_NAN]			= Color.gray;
	colorMap[PIXEL_ABOVE_MAX]	= Color.white;
	colorMap[PIXEL_BELOW_MIN]	= Color.black;
	colorMap[PIXEL_NOT_DEFINED]	= Color.cyan;
	
	return colorMap;
}


/**
 * Gets the colorMode property (java.lang.String) value.
 * @return The colorMode property value.
 * @see #setColorMode
 */
public final String getColorMode() {
	return fieldColorMode;
}


/**
 * Gets the colorModeID property (int) value.
 * @return The colorModeID property value.
 * @see #setColorModeID
 */
public final int getColorModeID() {
	return fieldColorModeID;
}


/**
 * Gets the dataRange property (cbit.image.Range) value.
 * @return The dataRange property value.
 * @see #setDataRange
 */
public final Range getDataRange() {
	return fieldDataRange;
}


/**
 * This method was created in VisualAge.
 * @return double[]
 */
public abstract double[] getDataValues();


/**
 * This method was created in VisualAge.
 * @return java.awt.Color[]
 */
public final Color[] getGrayScaleColorMap() {
	Color[] colorMap = new Color[256];
	for (int i=0;i<250;i++) {
		colorMap[i] = new Color(i*255/249, i*255/249, i*255/249);
	}
	colorMap[PIXEL_NO_RANGE]	= Color.gray;
	colorMap[PIXEL_NEGATIVE]	= Color.yellow;
	colorMap[PIXEL_NAN]			= Color.yellow;
	colorMap[PIXEL_ABOVE_MAX]	= Color.red;
	colorMap[PIXEL_BELOW_MIN]	= Color.blue;
	colorMap[PIXEL_NOT_DEFINED]	= Color.green;
	return colorMap;
}


/**
 * Gets the pixelValues property (int[]) value.
 * @return The pixelValues property value.
 * @see #setPixelValues
 */
public final int[] getPixelValues() {
	return fieldPixelValues;
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Gets the scaleRange property (cbit.image.Range) value.
 * @return The scaleRange property value.
 * @see #setScaleRange
 */
public final Range getScaleRange() {
	return fieldScaleRange;
}


/**
 * refresh method comment.
 */
public void refresh() {
	applyColorMap(applyScale());
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * Sets the autoMode property (boolean) value.
 * @param autoMode The new value for the property.
 * @see #getAutoMode
 */
public final void setAutoMode(boolean autoMode) {
	boolean oldValue = fieldAutoMode;
	fieldAutoMode = autoMode;
	firePropertyChange("autoMode", new Boolean(oldValue), new Boolean(autoMode));
}


/**
 * Sets the colorMode property (java.lang.String) value.
 * @param colorMode The new value for the property.
 * @see #getColorMode
 */
public final void setColorMode(String colorMode) {
	boolean good = false;
	if (colorMode.equals(grayscale)) {
		setColorModeID(0);
		good = true;
	}
	if (colorMode.equals(color1)) {
		setColorModeID(1);
		good = true;
	}
	if (good) {
		String oldValue = fieldColorMode;
		fieldColorMode = colorMode;
		firePropertyChange("colorMode", oldValue, colorMode);
	}
}


/**
 * Sets the colorModeID property (int) value.
 * @param colorModeID The new value for the property.
 * @see #getColorModeID
 */
public final void setColorModeID(int colorModeID) {
	fieldColorModeID = colorModeID;
}


/**
 * Sets the dataRange property (cbit.image.Range) value.
 * @param dataRange The new value for the property.
 * @see #getDataRange
 */
public final void setDataRange(Range dataRange) {
	Range oldValue = fieldDataRange;
	fieldDataRange = dataRange;
	firePropertyChange("dataRange", oldValue, dataRange);
}


/**
 * Sets the pixelValues property (int[]) value.
 * @param pixelValues The new value for the property.
 * @see #getPixelValues
 */
public final void setPixelValues(int[] pixelValues) {
	int[] oldValue = fieldPixelValues;
	fieldPixelValues = pixelValues;
	firePropertyChange("pixelValues", oldValue, pixelValues);
}


/**
 * Sets the scaleRange property (cbit.image.Range) value.
 * @param scaleRange The new value for the property.
 * @see #getScaleRange
 */
public final void setScaleRange(Range scaleRange) {
	Range oldValue = fieldScaleRange;
	fieldScaleRange = scaleRange;
	firePropertyChange("scaleRange", oldValue, scaleRange);
}
}

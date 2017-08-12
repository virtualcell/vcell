/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image.gui;
import org.vcell.util.Range;
/**
 * This type was created in VisualAge.
 */
public class DoubleDisplayAdapter extends cbit.image.DisplayAdapter {
	private Range fieldScale = new Range();
	private double[] fieldDataValues = new double[1];

/**
 * DataSetDisplayAdapter constructor comment.
 */
public DoubleDisplayAdapter() {
	super();
	setColorMode(grayscale);
}


/**
 * Gets the dataValues property (double[]) value.
 * @return The dataValues property value.
 * @see #setDataValues
 */
public final double[] getDataValues() {
	return fieldDataValues;
}


/**
 * Sets the dataValues property (double[]) value.
 * @param dataValues The new value for the property.
 * @see #getDataValues
 */
public final void setDataValues(double[] dataValues) {
	double dataMin = 0;
	double dataMax = 0;
	if (dataValues != null){
		dataMin = dataValues[0];
		dataMax = dataMin;
		for (int i=0;i<dataValues.length;i++){
			dataMin = Math.min(dataMin, dataValues[i]);
			dataMax = Math.max(dataMax, dataValues[i]);
		}	
	}
	setDataRange(new Range(dataMin, dataMax));
	double[] oldValue = fieldDataValues;
	fieldDataValues = dataValues;
	firePropertyChange("dataValues", oldValue, dataValues);
}
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.bionetgen;

import java.io.Serializable;

/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 1:19:06 PM)
 * @author: Jim Schaff
 */
public class BNGParameter implements Serializable {
	private String name = null;
	private double value = 0.0;

/**
 * BNGParameter constructor comment.
 */
public BNGParameter(String argName, double argValue) {
	super();
	name = argName;
	value = argValue;
}


/**
 * BNGParameter constructor comment.
 */
public String getName() {
	return name;
}


/**
 * BNGParameter constructor comment.
 */
public double getValue() {
	return value;
}


/**
 * BNGParameter constructor comment.
 */
public void setName(String argName) {
	name = argName;
}


/**
 * BNGParameter constructor comment.
 */
public void setValue(double argValue) {
	value = argValue;
}


/**
 * BNGParameter constructor comment.
 */
public String toString() {
	return new String(getName() + ";\t" + getValue());
}
}

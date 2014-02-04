/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.vis.io;
import cbit.vcell.math.VariableType;
/**
 * This type was created in VisualAge.
 */
public class SimDataBlock {

	private double data[] = null;
	private VariableType varType = null;

	public SimDataBlock(double data[], VariableType argVarType) {
		this.data = data;
		this.varType = argVarType;
	}
	
	public double[] getData() {
		return data;
	}
	
	public VariableType getVariableType() {
		return varType;
	}
	
}

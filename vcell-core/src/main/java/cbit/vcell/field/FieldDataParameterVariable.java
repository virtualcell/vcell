/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.field;

import cbit.vcell.math.ParameterVariable;

public class FieldDataParameterVariable extends ParameterVariable {

	private double[] resampledFileData;
	
	public FieldDataParameterVariable(String name,double[] argResampledFieldData) {
		super(name);
		resampledFileData = argResampledFieldData;
	}

	@Override
	public String getVCML() {
		return "FieldDataParameter  "+getName();
	}

	@Override
	public String toString() {
		return "FieldDataParameter("+hashCode()+") <"+getName()+">";
	}

	public double[] getResampledFieldData(){
		return resampledFileData;
	}
}

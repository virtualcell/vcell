/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.opt;

import java.io.Serializable;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;
/**
 * Assign different weights for data of different variables.
 * Can be used for SimpleReferenceData, SpatialReferenceData.
 * It can be considered as the columnWeights in SimpleReferenceData.
 * @author Tracy Li
 */
@SuppressWarnings("serial")
public class VariableWeights implements Weights, Serializable {
	double[] weights = null;
	
	public VariableWeights(double[] argVarWeights)
	{
		this.weights = argVarWeights;
	}

	public VariableWeights(VariableWeights argVariableWeights)
	{
		this.weights = argVariableWeights.getWeightData().clone();
	}
	
	public int getNumWeights() {
		return weights.length;
	}

	public double getWeightByVarIdx(int varIndex)
	{
		return weights[varIndex];
	}
	
	public double[] getWeightData()
	{
		return weights;
	}
	
	public boolean compareEqual(Matchable obj) {
		if(obj != null && obj instanceof VariableWeights)
		{
			VariableWeights otherVarWeights = (VariableWeights)obj;
			if(getWeightData() != null && otherVarWeights.getWeightData() != null)
			{
				return Compare.isEqual(getWeightData(), otherVarWeights.getWeightData());
			}
		}
		return false;
	}
	
}

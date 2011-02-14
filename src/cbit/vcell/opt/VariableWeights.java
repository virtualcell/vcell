package cbit.vcell.opt;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;
/**
 * Assign different weights for data of different variables.
 * Can be used for SimpleReferenceData, SpatialReferenceData.
 * It can be considered as the columnWeights in SimpleReferenceData.
 * @author Tracy Li
 */
public class VariableWeights implements Weights {
	double[] weights = null;
	
	public VariableWeights(double[] argVarWeights)
	{
		this.weights = argVarWeights;
	}

	public VariableWeights(VariableWeights argVariableWeights)
	{
		double[] argWeightData = argVariableWeights.getWeightData();
		this.weights = argWeightData.clone();
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

	public VariableWeights clone()
	{
		return new VariableWeights(this);
	}
}

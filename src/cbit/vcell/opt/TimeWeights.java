package cbit.vcell.opt;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;
/**
 * Assign different weights for data at different time.
 * Can be used for SimpleReferenceData, SpatialReferenceData
 * @author Tracy Li
 */
public class TimeWeights implements Weights {

	double[] weights = null;
	
	public TimeWeights(double[] argTimeWeights)
	{
		this.weights = argTimeWeights;
	}
	     
	public TimeWeights(TimeWeights argTimeWeights)
	{
		double[] argWeightData = argTimeWeights.getWeightData();
		this.weights = argWeightData.clone();
	}
	
	public int getNumWeights()
	{
		return weights.length;
	}

	public double getWeightByTimeIdx(int timeIndex)
	{
		return weights[timeIndex];
	}
	
	public double[] getWeightData()
	{
		return weights;
	}
	
	public boolean compareEqual(Matchable obj) {
		if(obj != null && obj instanceof TimeWeights)
		{
			TimeWeights otherTimeWeights = (TimeWeights)obj;
			if(getWeightData() != null && otherTimeWeights.getWeightData() != null)
			{
				return Compare.isEqual(getWeightData(), otherTimeWeights.getWeightData());
			}
		}
		return false;
	}
	
	public TimeWeights clone()
	{
		return new TimeWeights(this);
	}
}

package org.vcell.vmicro.workflow.data;

import cbit.vcell.opt.Parameter;

public abstract class RefSimOptModel extends OptModel{
	private static double epsilon = 1e-8;

	private double[][] refData = null;
	private double[] refTimePoints = null;
	private double refDiffusionRate = 0;
	
	public RefSimOptModel(String name, Parameter[] parameters, double[][] refData, double[] refTimePoints, double refDiffusionRate){
		super(name,parameters);
		this.refData = refData;
		this.refTimePoints = refTimePoints;
		this.refDiffusionRate = refDiffusionRate;
	}
	
	public double[][] getValueByDiffRate(double newDiffRate, double[] solutionTimePoints) {
		int roiLen = refData.length;
		double[][] result = new double[roiLen][solutionTimePoints.length];
		int preTimeIndex = 0;
		int postTimeIndex = 0;
		int idx = 0;
		for(int j = 0; j < solutionTimePoints.length; j++)
		{	
			// find corresponding time points in reference data 
			double estimateTime = (newDiffRate/refDiffusionRate) * solutionTimePoints[j];
			for( ;idx < refTimePoints.length; idx ++)
			{
				if(estimateTime < (refTimePoints[idx] + epsilon))
				{
					break;
				}
			}
			postTimeIndex = idx;
			
			if(postTimeIndex <= 0)//negtive newDiffRate will cause array index out of bound exception
			{
				preTimeIndex = 0;
				postTimeIndex = preTimeIndex;
			}
			else if(postTimeIndex > 0 && postTimeIndex < refTimePoints.length )
			{
				if((estimateTime > (refTimePoints[postTimeIndex] - epsilon)) &&  (estimateTime  < (refTimePoints[postTimeIndex] + epsilon)))
				{
					preTimeIndex = postTimeIndex;
				}
				else 
				{
					preTimeIndex = postTimeIndex - 1;
				}
			}
			else if(postTimeIndex >= refTimePoints.length)  
			{
				preTimeIndex = refTimePoints.length -1;
				postTimeIndex = preTimeIndex;
			}
			double preTimeInRefData = refTimePoints[preTimeIndex];
			double postTimeInRefData = refTimePoints[postTimeIndex];
			double proportion = 0;
			
			if((postTimeInRefData-preTimeInRefData) != 0)
			{
				proportion = ((estimateTime-preTimeInRefData)/(postTimeInRefData-preTimeInRefData));
			}
			
			for(int i = 0; i < roiLen; i++) 
			{
				//get data from reference data according to the estimate time.
				if(preTimeIndex == postTimeIndex)
				{
					result[i][j] = refData[i][preTimeIndex];
				}
				else 
				{
					double preDataValue = refData[i][preTimeIndex];
					double postDataValue = refData[i][postTimeIndex];
					double estimateDataValue = preDataValue + proportion *(postDataValue-preDataValue);
					result[i][j] = estimateDataValue;
				}
			}
		}
		return result;
	}

	public double[][] getRefData() {
		return refData;
	}

	public double[] getRefTimePoints() {
		return refTimePoints;
	}

	public double getRefDiffusionRate() {
		return refDiffusionRate;
	}
	
	public int getNumROIs(){
		return refData.length;
	}

}

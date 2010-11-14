package org.vcell.optimization;


import cbit.plot.Plot2D;
import cbit.vcell.opt.Parameter;

/*
 * The class is made for properly display a profile distribution with confidence intervals at the
 * different confidence levels in the ConfidenceIntervalPlotPanel. It has the parameter name, the 
 * profile distribution with condidence interval line saved in a Plot2D. The best estimate of the 
 * tuning parameter and the confidence intervals. A ProfileSummaryData corresponds to one tuning parameter.
 * author: Tracy Li
 * version: 1.1
 */
public class ProfileSummaryData 
{
	private String paramName = null;
	private Plot2D plot2D = null;
	private double bestEstimate = 0;
	private ConfidenceInterval[] intervals = null;
	
	public ProfileSummaryData(Plot2D plot2D, double bestEstimate, ConfidenceInterval[] intervals, String paramName)
	{
		this.plot2D = plot2D;
		this.bestEstimate = bestEstimate;
		this.intervals = intervals;
		this.paramName = paramName;
	}
	
	public Plot2D getPlot2D() {
		return plot2D;
	}
	
	public double getBestEstimate()
	{
		return bestEstimate;
	}
	public ConfidenceInterval[] getConfidenceIntervals()
	{
		return intervals;
	}
	public String getParamName()
	{
		return paramName;
	}
}

package cbit.vcell.microscopy;

import cbit.plot.Plot2D;
import cbit.vcell.opt.Parameter;

public class ProfileSummaryData 
{
	private Plot2D plot2D = null;
	private double bestEstimate = 0;
	private ConfidenceInterval[] intervals = null;
	
	public ProfileSummaryData(Plot2D plot2D, double bestEstimate, ConfidenceInterval[] intervals)
	{
		this.plot2D = plot2D;
		this.bestEstimate = bestEstimate;
		this.intervals = intervals;
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
}

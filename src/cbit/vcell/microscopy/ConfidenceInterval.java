package cbit.vcell.microscopy;

import org.vcell.util.NumberUtils;

public class ConfidenceInterval 
{
	double lowerBound = Double.NEGATIVE_INFINITY;
	boolean bLowerBoundOpen = true;
	double upperBound = Double.POSITIVE_INFINITY;
	boolean bUpperBoundOpen = true;
	
	public ConfidenceInterval(double lowerBound, boolean bLowerBoundOpen, double upperBound, boolean bUpperBoundOpen)
	{
		this.lowerBound = lowerBound;
		this.bLowerBoundOpen = bLowerBoundOpen;
		this.upperBound = upperBound;
		this.bUpperBoundOpen = bUpperBoundOpen;
	}
	
	public double getLowerBound() {
		return lowerBound;
	}

	public boolean isLowerBoundOpen() {
		return bLowerBoundOpen;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public boolean isUpperBoundOpen() {
		return bUpperBoundOpen;
	}
	
	public String toString()
	{
		String result = "";
		if(bLowerBoundOpen)//close lower bound
		{
			result = result + "(";
		}
		else
		{
			result = result + "[";
		}
		if(lowerBound == Double.NEGATIVE_INFINITY)
		{
			result = result + "-\u221E, ";
		}
		else
		{
			result = result + NumberUtils.formatNumber(lowerBound, 5) + ", ";
		}
		if(upperBound == Double.POSITIVE_INFINITY)
		{
			result = result + "+\u221E";
		}
		else
		{
			result = result + NumberUtils.formatNumber(upperBound, 5);
		}
		if(bUpperBoundOpen)//close upper bound
		{
			result = result + ")";
		}
		else
		{
			result = result + "]";
		}
		return result; 
	}
}

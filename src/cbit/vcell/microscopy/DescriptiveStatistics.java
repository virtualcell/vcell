package cbit.vcell.microscopy;

import java.util.Arrays;

public class DescriptiveStatistics 
{
	public static final String MEAN_NAME = "Mean";
	public static final String STANDARD_DEVIATION_NAME = "Standard Deviation";
	public static final String MEDIAN_NAME = "Median";
	public static final String MODE_NAME = "Mode";
	public static final String MIN_NAME = "Minimum";
	public static final String MAX_NAME = "Maximum";
	private double min = 0;
	private double max = 0;
	private double mean = 0;
	private double median = 0;
	private double mode = 0;
	private double standardDeviation = 0;
	

	public static DescriptiveStatistics CreateBasicStatistics(double[] array)
	{
		DescriptiveStatistics stat = new DescriptiveStatistics();
		if(array != null && array.length > 0)
		{
			Arrays.sort(array);
			//min, max, median
			stat.setMin(array[0]);
			stat.setMax(array[array.length - 1]);
			if((array.length % 2) != 0 )
			{
				stat.setMedian(array[(array.length/2)]);
			}
			else
			{
				stat.setMedian((array[(array.length/2)-1]+ array[array.length/2])/2);
			}
			//mean
			double average = 0;
			for(int i=0; i<array.length; i++)
			{
				average = average + array[i];
			}
			average = (average*1.0)/array.length;
			stat.setMean(average);
			//standard deviation
			double std = 0;
			if(array.length >1)
			{
				for( int i = 0; i < array.length; i++ )
				{
					std = std + ( Math.pow( ( array[i] - average ), 2) / (array.length - 1) );
				}
				std = Math.sqrt( std );
			}
			stat.setStandardDeviation(std);

		}
		return stat;
	}
	
	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getMean() {
		return mean;
	}

	public void setMean(double average) {
		this.mean = average;
	}

	public double getMedian() {
		return median;
	}

	public void setMedian(double median) {
		this.median = median;
	}

	public double getMode() {
		return mode;
	}

	public void setMode(double mode) {
		this.mode = mode;
	}
	
	public double getStandardDeviation() {
		return standardDeviation;
	}

	public void setStandardDeviation(double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

}

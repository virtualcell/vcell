package cbit.vcell.client;

import java.util.ArrayList;

import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.parser.ExpressionException;

public class TimeSeriesMultitrialData {

	public static class MinMaxHelp {
		public double min,max,diff;
		public MinMaxHelp(double[] data) {
			min=data[0];
			max=data[0];
			for (int i = 0; i < data.length; i++) {
				if(data[i] < min){min = data[i];}
				if(data[i] > max){max = data[i];}
			}
			diff = ((max-min)==0?1:max-min);
		}
	}

	public final String datasetName;
	public final String[] varNames;
	public final double[] times;
	public final int numTrials;
	public final double[][][] data;
	
	public TimeSeriesMultitrialData(String datasetName, String[] varNames, double[] times, int numTrials){
		this.datasetName = datasetName;
		this.varNames = varNames;
		this.times = times;
		this.numTrials = numTrials;
		data = new double[varNames.length][times.length][numTrials];
	}
	
	public void addDataSet(RowColumnResultSet simData, int trialIndex) throws ExpressionException{
		for (int varNameIndex=0; varNameIndex<varNames.length; varNameIndex++){
			String varName = varNames[varNameIndex];
			int columnIndex = simData.findColumn(varName);
			if (columnIndex<0){
				throw new RuntimeException("cannot find variable "+varName+" in dataset "+datasetName);
			}
			double[] data = simData.extractColumn(columnIndex);
			if (data.length != this.times.length){
				throw new RuntimeException("data length of variable "+varName+" in dataset "+datasetName+" is "+data.length+", expecting "+this.times.length);
			}
			for (int timeIndex=0; timeIndex<data.length; timeIndex++){
				this.data[varNameIndex][timeIndex][trialIndex] = data[timeIndex];
			}
		}
	}

	public double[] getMeanTrajectory(String varName) {
		int varNameIndex = findVarNameIndex(varName);
		double[] meanValues = new double[this.times.length];
		for (int timeIndex=0; timeIndex<this.times.length; timeIndex++){
			meanValues[timeIndex] = getMean(this.data[varNameIndex][timeIndex]);
		}
		return meanValues;
	}

	private int findVarNameIndex(String varName) {
		int varNameIndex = -1;
		for (int i=0;i<varNames.length;i++){
			if (varNames[i].equals(varName)){
				varNameIndex = i;
				break;
			}
		}
		if (varNameIndex<0){
			throw new RuntimeException("variable "+varName+" not found");
		}
		return varNameIndex;
	}

	public double[] getVarTimeData(String varName, int timeIndex) {
		int varNameIndex = findVarNameIndex(varName);
		return this.data[varNameIndex][timeIndex];
	}
	private static double getMean(double[] data) {
		double mean = 0;
		for (double d : data){
			mean += d;
		}
		return mean/data.length;
	}

	public static double kolmogorovSmirnovTest(double[] rawData1, double[] rawData2) {
		try {
			int numBins = 1 + (int)Math.ceil(Math.sqrt(rawData1.length));
//rawData2 = ramp(0,10,rawData2.length);
			TimeSeriesMultitrialData.MinMaxHelp minMaxHelp1 = new TimeSeriesMultitrialData.MinMaxHelp(rawData1);
			TimeSeriesMultitrialData.MinMaxHelp minMaxHelp2 = new TimeSeriesMultitrialData.MinMaxHelp(rawData2);
			double min = Math.min(minMaxHelp1.min, minMaxHelp2.min);
			double max = Math.max(minMaxHelp1.max, minMaxHelp2.max);
			double[] cdf1 = calculateCDF(rawData1, min, max, numBins);
			double[] cdf2 = calculateCDF(rawData2, min, max, numBins);
			KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
			return test.kolmogorovSmirnovStatistic(cdf1, cdf2);
		}catch (Exception e){
			e.printStackTrace(System.out);
			return -1;
		}
	}
	
	public static double chiSquaredTest(double[] rawData1, double[] rawData2) {
		try {
			int numBins = 1 + (int)Math.ceil(Math.sqrt(rawData1.length));
//rawData2 = ramp(0,10,rawData2.length);
			TimeSeriesMultitrialData.MinMaxHelp minMaxHelp1 = new TimeSeriesMultitrialData.MinMaxHelp(rawData1);
			TimeSeriesMultitrialData.MinMaxHelp minMaxHelp2 = new TimeSeriesMultitrialData.MinMaxHelp(rawData2);
			double min = Math.min(minMaxHelp1.min, minMaxHelp2.min);
			double max = Math.max(minMaxHelp1.max, minMaxHelp2.max);
			long[] histogram1 = calcHistogram(rawData1, min, max, numBins);
			long[] histogram2 = calcHistogram(rawData2, min, max, numBins);
			
			//
			// remove histogram indices where both bins are zero
			//
			ArrayList<Long> histogram1List = new ArrayList<Long>();
			ArrayList<Long> histogram2List = new ArrayList<Long>();
			for (int i=0;i<histogram1.length;i++){
				if (histogram1[i] != 0 || histogram2[i] != 0){
					histogram1List.add(histogram1[i]);
					histogram2List.add(histogram2[i]);
//					}else{
//						histogram1List.add(new Long(1));
//						histogram2List.add(new Long(1));
				}
			}
			histogram1 = new long[histogram1List.size()];
			histogram2 = new long[histogram2List.size()];
			for (int i=0;i<histogram1List.size();i++){
				histogram1[i] = histogram1List.get(i);
				histogram2[i] = histogram2List.get(i);
			}
			
			if (histogram1.length==1){
				return 0.0;
			}
			ChiSquareTest chiSquareTest = new ChiSquareTest();
			
			return chiSquareTest.chiSquareTestDataSetsComparison(histogram1, histogram2);
		}catch (Exception e){
			e.printStackTrace(System.out);
			return -1;
		}
	}
	
	private static double[] ramp(double min, double max, int length){
		double[] data = new double[length];
		for (int i=0;i<data.length;i++){
			data[i] = min + i*(max-min)/(length-1);
		}
		return data;
	}
	
	private static double[] getData(ArrayList<Double> doubleList){
		double[] data = new double[doubleList.size()];
		for (int i=0;i<data.length;i++){
			data[i] = doubleList.get(i);
		}
		return data;
	}
	
	private static double[] calculateCDF(double[] data, double min, double max, int numBins){
		long[] histogram = calcHistogram(data, min, max, numBins);
		int totalCount = 0;
		for (long bin : histogram){
			totalCount += bin;
		}
		double[] cdf = new double[histogram.length];
		int cumulativeCount = 0;
		for (int i=0;i<numBins;i++){
			cumulativeCount += histogram[i];
			cdf[i] = cumulativeCount/totalCount;
		}
		return cdf;
	}
	
	private static long[] calcHistogram(double[] data, double min, double max, int numBins) {
		  final long[] result = new long[numBins];
		  final double binSize = (max - min)/numBins;

		  for (double d : data) {
		    int bin = (int) ((d - min) / binSize);
		    if (bin < 0) { /* this data is smaller than min */ }
		    else if (bin >= numBins) { /* this data point is bigger than max */ }
		    else {
		      result[bin] += 1;
		    }
		  }
		  return result;
	}

}
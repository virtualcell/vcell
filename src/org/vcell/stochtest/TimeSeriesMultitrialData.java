package org.vcell.stochtest;

import java.util.ArrayList;

import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.apache.commons.math3.stat.inference.TTest;

import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.parser.ExpressionException;

public class TimeSeriesMultitrialData {

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
			Max max = new Max();
			max.incrementAll(rawData1);
			max.incrementAll(rawData2);
			Min min = new Min();
			min.incrementAll(rawData1);
			min.incrementAll(rawData2);
			double[] cdf1 = calculateCDF(rawData1, min.getResult(), max.getResult(), numBins);
			double[] cdf2 = calculateCDF(rawData2, min.getResult(), max.getResult(), numBins);
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
			Max max = new Max();
			max.incrementAll(rawData1);
			max.incrementAll(rawData2);
			Min min = new Min();
			min.incrementAll(rawData1);
			min.incrementAll(rawData2);
			long[] histogram1 = calcHistogram(rawData1, min.getResult(), max.getResult(), numBins);
			long[] histogram2 = calcHistogram(rawData2, min.getResult(), max.getResult(), numBins);
			
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

	public static class SummaryStatistics {
		int numExperiments;
		int size1;
		int size2;
		int numFail_95;
		int numFail_99;
		int numFail_999;
		int numFail_9999;
		String varSmallestPValue;
		double smallestPValue;
		double timeSmallestPValue;
//		public double maxTTest;
//		public double maxDiffMeans;
//		public double maxChiSquare;
//		public double maxKolmogorovSmirnov;
		public String toString(){
			return super.toString();
		}
		public boolean pass(){
			if (((double)numFail_95)/numExperiments > 3*0.05){
				return false;
			}
			if (((double)numFail_99)/numExperiments > 3*0.01){
				return false;
			}
			if (((double)numFail_999)/numExperiments > 3*0.001){
				return false;
			}
			return true;
		}
		public String results() {
			return "pass="+pass()+",   smallest p-value="+smallestPValue+" for var "+varSmallestPValue+"@t="+timeSmallestPValue+"     N1="+size1+",N2="+size2+",Nexperiments="+numExperiments+",num fail 95%="+numFail_95+",num fail 99%="+numFail_99+",num fail 999%="+numFail_999+",num fail 9999%="+numFail_9999;
		}
	}
	
	public static SummaryStatistics statisticsSummary(TimeSeriesMultitrialData data1, TimeSeriesMultitrialData data2) {
		SummaryStatistics ss = new SummaryStatistics();
//		ss.maxTTest = Double.NEGATIVE_INFINITY;
//		ss.maxDiffMeans = Double.NEGATIVE_INFINITY;
//		ss.maxChiSquare = Double.NEGATIVE_INFINITY;
//		ss.maxKolmogorovSmirnov = Double.NEGATIVE_INFINITY;
		ss.size1 = data1.numTrials;
		ss.size2 = data2.numTrials;
		ss.smallestPValue = 1.0;
		ss.timeSmallestPValue = -1.0;
		for (int varIndex=0; varIndex<data1.varNames.length; varIndex++){
			String varName = data1.varNames[varIndex];
//			double[] trajectory1 = data1.getMeanTrajectory(varName);
//			double[] trajectory2 = data2.getMeanTrajectory(varName);
//			StochtestFileUtils.MinMaxHelp minmaxStoch = new StochtestFileUtils.MinMaxHelp(trajectory1);
			for (int timeIndex = 0; timeIndex < data1.times.length; timeIndex++) {
//				double diffMeans = Math.abs((trajectory1[timeIndex]/minmaxStoch.diff)-(trajectory2[timeIndex]/minmaxStoch.diff));
				double[] varTimeData1 = data1.getVarTimeData(varName,timeIndex);
				double[] varTimeData2 = data2.getVarTimeData(varName,timeIndex);
				TTest ttest = new TTest();
				ss.numExperiments++;
				double pValue = ttest.tTest(varTimeData1, varTimeData2);
				if (pValue < 0.05){
					ss.numFail_95++;
				}
				if (pValue < 0.01){
					ss.numFail_99++;
				}
				if (pValue < 0.001){
					ss.numFail_999++;
				}
				
				if (pValue < ss.smallestPValue){
					ss.smallestPValue = pValue;
					ss.varSmallestPValue = varName;
					ss.timeSmallestPValue = data1.times[timeIndex];
				}
				
//				double chiSquared = TimeSeriesMultitrialData.chiSquaredTest(varTimeData1, varTimeData2);
//				double ks = TimeSeriesMultitrialData.kolmogorovSmirnovTest(varTimeData1, varTimeData2);
//				ss.maxTTest = Math.max(ss.maxTTest,ttest_p);
//				ss.maxDiffMeans = Math.max(ss.maxDiffMeans,diffMeans);
//				ss.maxChiSquare = Math.max(ss.maxChiSquare,chiSquared);
//				ss.maxKolmogorovSmirnov = Math.max(ss.maxKolmogorovSmirnov,ks);
			}
		}
		return ss;
	}

}
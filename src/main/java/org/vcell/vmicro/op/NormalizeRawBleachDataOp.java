package org.vcell.vmicro.op;

import cbit.vcell.math.RowColumnResultSet;

public class NormalizeRawBleachDataOp {
	
	public RowColumnResultSet normalizeRawBleachData(RowColumnResultSet rawExpDataset) throws Exception {
		if (rawExpDataset.getColumnDescriptionsCount()!=2){
			throw new Exception("expecting 2 columns in rawExpData input");
		}
		double[] rawExpTimes = rawExpDataset.extractColumn(0);
		double[] rawFluor = rawExpDataset.extractColumn(1);
		
		int firstPostbleachIndex = findFirstPostbleachIndex(rawFluor);
		
		double prebleachAvg = 0;
		for (int i=0;i<firstPostbleachIndex;i++){
			prebleachAvg += rawFluor[i];
		}
		prebleachAvg /= firstPostbleachIndex;
		
		RowColumnResultSet normExpDataset = new RowColumnResultSet(new String[] { "t", "normFluor"});
		for (int rawIndex=firstPostbleachIndex; rawIndex<rawFluor.length; rawIndex++){
			double normTime = rawExpTimes[rawIndex]-rawExpTimes[firstPostbleachIndex];
			double normFluor = rawFluor[rawIndex]/prebleachAvg;
			normExpDataset.addRow(new double[] { normTime, normFluor } );
		}
		return normExpDataset;
	}
	
	private int findFirstPostbleachIndex(double[] rawFluor){
		double minFluor = Double.MAX_VALUE;
		int minIndex = -1;
		for (int i=0;i<rawFluor.length;i++){
			if (rawFluor[i] < minFluor){
				minFluor = rawFluor[i];
				minIndex = i;
			}
		}
		return minIndex;
	}

}

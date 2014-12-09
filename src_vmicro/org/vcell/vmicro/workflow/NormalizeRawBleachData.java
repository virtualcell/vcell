package org.vcell.vmicro.workflow;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

import cbit.vcell.math.RowColumnResultSet;

public class NormalizeRawBleachData extends Task {
	
	//
	// inputs
	//
	public final DataInput<RowColumnResultSet> rawExpData;
	//
	// outputs
	//
	public final DataHolder<RowColumnResultSet> normExpData;
	
	

	public NormalizeRawBleachData(String id){
		super(id);
		rawExpData = new DataInput<RowColumnResultSet>(RowColumnResultSet.class,"rawExpData",this);
		normExpData = new DataHolder<RowColumnResultSet>(RowColumnResultSet.class,"normExpData",this);
		addInput(rawExpData);
		addOutput(normExpData);
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

	@Override
	protected void compute0(final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		RowColumnResultSet rawExpDataset = rawExpData.getData();
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
		normExpData.setData(normExpDataset);
	}
	
}

package org.vcell.vmicro.workflow;

import java.io.File;
import java.io.FileReader;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

import cbit.vcell.math.CSV;
import cbit.vcell.math.RowColumnResultSet;

public class ImportTimeSeriesFromCSV extends Task {
	
	//
	// inputs
	//
	public final DataInput<String> csvFile;
	
	//
	// outputs
	//
	public final DataHolder<RowColumnResultSet> timeSeries;
	

	public ImportTimeSeriesFromCSV(String id){
		super(id);
		csvFile = new DataInput<String>(String.class,"csvFile",this);
		timeSeries = new DataHolder<RowColumnResultSet>(RowColumnResultSet.class,"timeSeries",this);
		addInput(csvFile);
		addOutput(timeSeries);
	}

	@Override
	protected void compute0(ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		CSV csv = new CSV();
		File inputFile = new File(csvFile.getData());
		RowColumnResultSet resultSet = csv.importFrom(new FileReader(inputFile));
		timeSeries.setData(resultSet);
	}

}

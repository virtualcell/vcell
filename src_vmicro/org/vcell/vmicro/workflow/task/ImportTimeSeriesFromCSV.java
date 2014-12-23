package org.vcell.vmicro.workflow.task;

import java.io.File;
import java.io.FileReader;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

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
	public final DataOutput<RowColumnResultSet> timeSeries;
	

	public ImportTimeSeriesFromCSV(String id){
		super(id);
		csvFile = new DataInput<String>(String.class,"csvFile",this);
		timeSeries = new DataOutput<RowColumnResultSet>(RowColumnResultSet.class,"timeSeries",this);
		addInput(csvFile);
		addOutput(timeSeries);
	}

	@Override
	protected void compute0(TaskContext context, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		CSV csv = new CSV();
		File inputFile = new File(context.getData(csvFile));
		RowColumnResultSet resultSet = csv.importFrom(new FileReader(inputFile));
		context.setData(timeSeries,resultSet);
	}

}

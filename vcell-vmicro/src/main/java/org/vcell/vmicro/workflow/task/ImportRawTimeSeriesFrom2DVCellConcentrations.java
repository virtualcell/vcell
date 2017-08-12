package org.vcell.vmicro.workflow.task;

import java.io.File;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.ImportRawTimeSeriesFrom2DVCellConcentrationsOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.UShortImage;

public class ImportRawTimeSeriesFrom2DVCellConcentrations extends Task {
	
	//
	// inputs
	//
	public final DataInput<File> vcellSimLogFile;
	public final DataInput<String> fluorFunctionName;
	public final DataInput<Double> maxIntensity;
	public final DataInput<Boolean> bNoise;
	
	//
	// outputs
	//
	public final DataOutput<ImageTimeSeries> rawTimeSeriesImages;
	

	public ImportRawTimeSeriesFrom2DVCellConcentrations(String id){
		super(id);
		vcellSimLogFile = new DataInput<File>(File.class,"vcellSimLogFile",this);
		fluorFunctionName = new DataInput<String>(String.class,"fluorFunctionName",this);
		maxIntensity = new DataInput<Double>(Double.class,"maxIntensity",this);
		bNoise = new DataInput<Boolean>(Boolean.class,"bNoise",this);
		rawTimeSeriesImages = new DataOutput<ImageTimeSeries>(ImageTimeSeries.class,"rawTimeSeriesImages",this);
		addInput(vcellSimLogFile);
		addInput(fluorFunctionName);
		addInput(maxIntensity);
		addInput(bNoise);
		addOutput(rawTimeSeriesImages);
	}


	@Override
	protected void compute0(TaskContext context, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// set inputs
		File vcellSimLog = context.getData(vcellSimLogFile);
		String fluorFuncName = context.getData(fluorFunctionName);
		Double maximumIntensity = context.getData(maxIntensity);
		Boolean enableNoise = context.getData(bNoise);
		
		// do op
		ImportRawTimeSeriesFrom2DVCellConcentrationsOp op = new ImportRawTimeSeriesFrom2DVCellConcentrationsOp();
		ImageTimeSeries<UShortImage> imageTimeSeries = op.importRawTimeSeries(vcellSimLog, fluorFuncName, maximumIntensity, enableNoise);
		
		// set output
		context.setData(rawTimeSeriesImages,imageTimeSeries);
	}

}

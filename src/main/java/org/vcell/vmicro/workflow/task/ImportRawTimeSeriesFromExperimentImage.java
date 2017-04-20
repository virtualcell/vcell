package org.vcell.vmicro.workflow.task;

import java.io.File;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.ImportRawTimeSeriesFromExperimentImageOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.UShortImage;

public class ImportRawTimeSeriesFromExperimentImage extends Task {
	
	//
	// inputs
	//
	public final DataInput<File> expTimeSeriesFile;
	
	//
	// outputs
	//
	public final DataOutput<ImageTimeSeries> rawTimeSeriesImages;
	

	public ImportRawTimeSeriesFromExperimentImage(String id){
		super(id);
		expTimeSeriesFile = new DataInput<File>(File.class,"expTimeSeriesFile",this);
		rawTimeSeriesImages = new DataOutput<ImageTimeSeries>(ImageTimeSeries.class,"rawTimeSeriesImages",this);
		addInput(expTimeSeriesFile);
		addOutput(rawTimeSeriesImages);
	}

	@Override
	protected void compute0(TaskContext context, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get input
		File expTimeSeriesFile = context.getData(this.expTimeSeriesFile);
		
		// do op
		ImportRawTimeSeriesFromExperimentImageOp op = new ImportRawTimeSeriesFromExperimentImageOp();
		ImageTimeSeries<UShortImage> imageTimeSeries = op.importRawTimeSeries(expTimeSeriesFile);
		
		// set output
		context.setData(rawTimeSeriesImages,imageTimeSeries);
	}

}

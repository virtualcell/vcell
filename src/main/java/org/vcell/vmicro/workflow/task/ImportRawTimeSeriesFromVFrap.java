package org.vcell.vmicro.workflow.task;

import java.io.File;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.ImportRawTimeSeriesFromVFrapOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.UShortImage;

public class ImportRawTimeSeriesFromVFrap extends Task {
	
	//
	// inputs
	//
	public final DataInput<File> vfrapFile;
	
	//
	// outputs
	//
	public final DataOutput<ImageTimeSeries> rawTimeSeriesImages;
	

	public ImportRawTimeSeriesFromVFrap(String id){
		super(id);
		vfrapFile = new DataInput<File>(File.class,"vfrapFile",this);
		rawTimeSeriesImages = new DataOutput<ImageTimeSeries>(ImageTimeSeries.class,"rawTimeSeriesImages",this);
		addInput(vfrapFile);
		addOutput(rawTimeSeriesImages);
	}

	@Override
	protected void compute0(TaskContext context, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get input
		File vfrap_file = context.getData(vfrapFile);
		
		// do op
		ImportRawTimeSeriesFromVFrapOp op = new ImportRawTimeSeriesFromVFrapOp();
		ImageTimeSeries<UShortImage> imageTimeSeries = op.importRawTimeSeriesFromVFrap(vfrap_file);
		
		// set output		
		context.setData(rawTimeSeriesImages,imageTimeSeries);
	}

}

package org.vcell.vmicro.workflow.task;

import java.io.File;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.ExportRawTimeSeriesToVFrapOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class ExportRawTimeSeriesToVFrap extends Task {
	
	//
	// inputs
	//
	public final DataInput<File> vfrapFile;
	public final DataInput<ImageTimeSeries> rawTimeSeriesImages;
	
	//
	// outputs
	//
	public final DataOutput<Boolean> written;
	
	public ExportRawTimeSeriesToVFrap(String id){
		super(id);
		vfrapFile = new DataInput<File>(File.class,"vfrapFile",this);
		rawTimeSeriesImages = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"rawTimeSeriesImages",this);
		addInput(vfrapFile);
		addInput(rawTimeSeriesImages);
		written = new DataOutput<Boolean>(Boolean.class,"written",this);
		addOutput(written);
	}

	@Override
	protected void compute0(TaskContext context, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {

		// get inputs
		File outputFile = context.getData(vfrapFile);
		ImageTimeSeries imageTimeSeries = context.getData(rawTimeSeriesImages);
		// validate that imageTimeSeries is of type ImageTimeSeries<UShortImage>
		Image[] images = imageTimeSeries.getAllImages();
		UShortImage[] usImages = new UShortImage[images.length];
		for (int i=0;i<usImages.length;i++){
			if (!(images[i] instanceof UShortImage)){
				throw new Exception("type mismatch, expecting only UShortImages in time series");
			}
			usImages[i] = (UShortImage)images[i];
		}
		ImageTimeSeries<UShortImage> ushortImageTimeSeries = new ImageTimeSeries<UShortImage>(UShortImage.class,usImages,imageTimeSeries.getImageTimeStamps(),imageTimeSeries.getSizeZ());
		ExportRawTimeSeriesToVFrapOp op = new ExportRawTimeSeriesToVFrapOp();
		op.exportToVFRAP(outputFile, ushortImageTimeSeries, clientTaskStatusSupport);

		// write output (just boolean "done" flag)
		context.setData(written, true);
	}

}

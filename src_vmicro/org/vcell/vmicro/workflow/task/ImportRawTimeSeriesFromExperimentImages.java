package org.vcell.vmicro.workflow.task;

import java.io.File;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReaderFactory;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class ImportRawTimeSeriesFromExperimentImages extends Task {
	
	//
	// inputs
	//
	public final DataInput<File[]> expTimeSeriesFiles;
	public final DataInput<Double> timeInterval;
	
	//
	// outputs
	//
	public final DataOutput<ImageTimeSeries> rawTimeSeriesImages;
	

	public ImportRawTimeSeriesFromExperimentImages(String id){
		super(id);
		expTimeSeriesFiles = new DataInput<File[]>(File[].class,"expTimeSeriesFile",this);
		timeInterval = new DataInput<Double>(Double.class,"timeInterval",this);
		rawTimeSeriesImages = new DataOutput<ImageTimeSeries>(ImageTimeSeries.class,"rawTimeSeriesImages",this);
		addInput(expTimeSeriesFiles);
		addInput(timeInterval);
		addOutput(rawTimeSeriesImages);
	}

	@Override
	protected void compute0(TaskContext context, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		boolean isTimeSeries = true;
		File[] files = context.getData(expTimeSeriesFiles);
		ImageDataset rawTimeData = ImageDatasetReaderFactory.createImageDatasetReader().readImageDatasetFromMultiFiles(files, clientTaskStatusSupport, isTimeSeries, context.getData(timeInterval));
		ImageTimeSeries<UShortImage> imageTimeSeries = new ImageTimeSeries<UShortImage>(UShortImage.class,rawTimeData.getAllImages(),rawTimeData.getImageTimeStamps(),1);
		context.setData(rawTimeSeriesImages,imageTimeSeries);
	}

}

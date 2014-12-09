package org.vcell.vmicro.workflow;

import java.io.File;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReaderFactory;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class ImportRawTimeSeriesFromExperimentImage extends Task {
	
	//
	// inputs
	//
	public final DataInput<File> expTimeSeriesFile;
	
	//
	// outputs
	//
	public final DataHolder<ImageTimeSeries> rawTimeSeriesImages;
	

	public ImportRawTimeSeriesFromExperimentImage(String id){
		super(id);
		expTimeSeriesFile = new DataInput<File>(File.class,"expTimeSeriesFile",this);
		rawTimeSeriesImages = new DataHolder<ImageTimeSeries>(ImageTimeSeries.class,"rawTimeSeriesImages",this);
		addInput(expTimeSeriesFile);
		addOutput(rawTimeSeriesImages);
	}

	@Override
	protected void compute0(ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		ImageDataset rawTimeData = ImageDatasetReaderFactory.createImageDatasetReader().readImageDataset(expTimeSeriesFile.getData().getAbsolutePath(), clientTaskStatusSupport);
		ImageTimeSeries<UShortImage> imageTimeSeries = new ImageTimeSeries<UShortImage>(UShortImage.class,rawTimeData.getAllImages(),rawTimeData.getImageTimeStamps(),1);
		rawTimeSeriesImages.setData(imageTimeSeries);
	}

}

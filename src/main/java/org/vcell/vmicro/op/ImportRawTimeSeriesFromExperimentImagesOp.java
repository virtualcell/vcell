package org.vcell.vmicro.op;

import java.io.File;

import org.vcell.service.VCellServiceHelper;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;

import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReader;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class ImportRawTimeSeriesFromExperimentImagesOp {
	
	public ImageTimeSeries<UShortImage> importRawTimeSeries(File[] expTimeSeriesFiles, double timeInterval) throws Exception {
		boolean isTimeSeries = true;
		ClientTaskStatusSupport clientTaskStatusSupport = null;

		ImageDataset rawTimeData = VCellServiceHelper.getInstance().loadService(ImageDatasetReader.class).readImageDatasetFromMultiFiles(expTimeSeriesFiles, clientTaskStatusSupport , isTimeSeries, timeInterval);

		ImageTimeSeries<UShortImage> imageTimeSeries = new ImageTimeSeries<UShortImage>(UShortImage.class,rawTimeData.getAllImages(),rawTimeData.getImageTimeStamps(),1);
		return imageTimeSeries;
	}

}

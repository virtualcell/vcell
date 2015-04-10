package org.vcell.vmicro.workflow.task;

import java.io.File;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.ImportRawTimeSeriesFromHdf5FluorOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.UShortImage;

public class ImportRawTimeSeriesFromHdf5Fluor extends Task {
	
	//
	// inputs
	//
	public final DataInput<String> vcellHdf5File;
	public final DataInput<String> fluorDataName;
	public final DataInput<Integer> zSliceIndex;
	public final DataInput<Double> maxIntensity;
	public final DataInput<Boolean> bNoise;
	
	//
	// outputs
	//
	public final DataOutput<ImageTimeSeries> rawTimeSeriesImages;
	
	public ImportRawTimeSeriesFromHdf5Fluor(String id){
		super(id);
		vcellHdf5File = new DataInput<String>(String.class,"vcellHdf5File",this);
		fluorDataName = new DataInput<String>(String.class,"fluorDataName",this);
		zSliceIndex = new DataInput<Integer>(Integer.class,"zSliceIndex",this,true);
		maxIntensity = new DataInput<Double>(Double.class,"maxIntensity",this);
		bNoise = new DataInput<Boolean>(Boolean.class,"bNoise",this);
		rawTimeSeriesImages = new DataOutput<ImageTimeSeries>(ImageTimeSeries.class,"rawTimeSeriesImages",this);
		addInput(vcellHdf5File);
		addInput(fluorDataName);
		addInput(zSliceIndex);
		addInput(maxIntensity);
		addInput(bNoise);
		addOutput(rawTimeSeriesImages);
	}

	@Override
	protected void compute0(TaskContext context, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get input
		int zSlice = context.getDataWithDefault(zSliceIndex,0);
		File hdf5File = new File(context.getData(this.vcellHdf5File));
		String fluorName = context.getData(this.fluorDataName);
		Double maxFluor = context.getData(this.maxIntensity);
		Boolean isNoise = context.getData(this.bNoise);
		
		// do op
		ImportRawTimeSeriesFromHdf5FluorOp op = new ImportRawTimeSeriesFromHdf5FluorOp();
		ImageTimeSeries<UShortImage> timeRawData = op.importTimeSeriesFromHDF5Data(hdf5File, fluorName, maxFluor, isNoise, zSlice);
		
		// set output
		context.setData(rawTimeSeriesImages,timeRawData);
	}

}

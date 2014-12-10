package org.vcell.vmicro.workflow.task;

import java.io.File;
import java.util.ArrayList;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

import cbit.image.ImageException;
import cbit.image.SourceDataInfo;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.simdata.DataOperation;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.DataIndexHelper;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.TimePointHelper;
import cbit.vcell.simdata.DataOperationResults;
import cbit.vcell.simdata.DataSetControllerImpl;

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
	public final DataHolder<ImageTimeSeries> rawTimeSeriesImages;
	
	public ImportRawTimeSeriesFromHdf5Fluor(String id){
		super(id);
		vcellHdf5File = new DataInput<String>(String.class,"vcellHdf5File",this);
		fluorDataName = new DataInput<String>(String.class,"fluorDataName",this);
		zSliceIndex = new DataInput<Integer>(Integer.class,"zSliceIndex",this);
		maxIntensity = new DataInput<Double>(Double.class,"maxIntensity",this);
		bNoise = new DataInput<Boolean>(Boolean.class,"bNoise",this);
		rawTimeSeriesImages = new DataHolder<ImageTimeSeries>(ImageTimeSeries.class,"rawTimeSeriesImages",this);
		addInput(vcellHdf5File);
		addInput(fluorDataName);
		addInput(zSliceIndex);
		addInput(maxIntensity);
		addInput(bNoise);
		addOutput(rawTimeSeriesImages);
	}

	@Override
	protected void compute0(ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		int zSlice = 0;
		if (zSliceIndex.getSource()!=null){
			zSlice = zSliceIndex.getData();
		}
		ImageTimeSeries<UShortImage> timeRawData = importTimeSeriesFromHDF5Data(new File(vcellHdf5File.getData()), fluorDataName.getData(), maxIntensity.getData(), bNoise.getData(), zSlice, clientTaskStatusSupport);
		rawTimeSeriesImages.setData(timeRawData);
	}

	public static ImageTimeSeries<UShortImage> importTimeSeriesFromHDF5Data(File inputHDF5File, String fluorDataName, Double maxIntensity, boolean bNoise, int zSliceIndex, ClientTaskStatusSupport progressListener) throws Exception
	{
		if(progressListener != null){
			progressListener.setMessage("Loading HDF5 file " + inputHDF5File.getAbsolutePath() + "...");
		}		
		DataOperationResults.DataProcessingOutputInfo dataProcessingOutputInfo =
			(DataOperationResults.DataProcessingOutputInfo)DataSetControllerImpl.getDataProcessingOutput(new DataOperation.DataProcessingOutputInfoOP(null/*no vcDataIdentifier OK*/,false,null), inputHDF5File);
		DataOperationResults.DataProcessingOutputDataValues dataProcessingOutputDataValues =
			(DataOperationResults.DataProcessingOutputDataValues)DataSetControllerImpl.getDataProcessingOutput(
				new DataOperation.DataProcessingOutputDataValuesOP(null/*no vcDataIdentifier OK*/, SimulationContext.FLUOR_DATA_NAME,TimePointHelper.createAllTimeTimePointHelper(),DataIndexHelper.createSliceDataIndexHelper(0),null,null), inputHDF5File);
		ArrayList<SourceDataInfo> sdiArr =
			dataProcessingOutputDataValues.createSourceDataInfos(
				dataProcessingOutputInfo.getVariableISize(SimulationContext.FLUOR_DATA_NAME),
				dataProcessingOutputInfo.getVariableOrigin(SimulationContext.FLUOR_DATA_NAME),
				dataProcessingOutputInfo.getVariableExtent(SimulationContext.FLUOR_DATA_NAME));
		double[] times = dataProcessingOutputInfo.getVariableTimePoints();
		if(sdiArr.size() != times.length){
			throw new ImageException("Error FRAPData.createFrapData: times array length must equal SourceDataInfo vector size");
		}
		// construct
		int XY_SIZE = sdiArr.get(0).getXSize()*sdiArr.get(0).getYSize();
		int SLICE_OFFSET = 0*XY_SIZE;
		int Z_SIZE = 1;//slice always 2D data
		// find scale factor to scale up the data to avoid losing precision when casting double to short
		double linearScaleFactor = 1;
		if(maxIntensity != null){
			double maxDataValue = 0;
			for (int i = 0; i < times.length; i++) {
				if(sdiArr.get(i).getMinMax() != null){
					maxDataValue = Math.max(maxDataValue, sdiArr.get(i).getMinMax().getMax());
				}else{
					double[] doubleData = (double[])sdiArr.get(i).getData();
					for(int j=0; j<doubleData.length; j++){
						maxDataValue = Math.max(maxDataValue, doubleData[j]);
					}
				}
			}
			linearScaleFactor = maxIntensity.doubleValue()/maxDataValue;
		}
		//saving each time step 2D double array to a UShortImage
		UShortImage[] dataImages1 = new UShortImage[times.length];
		for (int i = 0; i < times.length; i++) {
			double[] doubleData = (double[])sdiArr.get(i).getData();
			short[] shortData = new short[XY_SIZE];
			for(int j=0; j<shortData.length; j++)
			{
				shortData[j] = (short)(doubleData[j+(SLICE_OFFSET)]*linearScaleFactor);
			}
			dataImages1[i] = new UShortImage(
						shortData,
						sdiArr.get(i).getOrigin(),
						sdiArr.get(i).getExtent(),
						sdiArr.get(i).getXSize(),sdiArr.get(i).getYSize(),Z_SIZE);
			
			if(progressListener != null){
				int progress = (int)(((i+1)*1.0/times.length)*100);
				progressListener.setProgress(progress);
			}
		}
		
		ImageDataset imageDataSet = new ImageDataset(dataImages1,times,Z_SIZE);
		UShortImage[] dataImages = imageDataSet.getAllImages();
		double[] timeStamps = imageDataSet.getImageTimeStamps();
		ImageTimeSeries<UShortImage> rawImageTimeSeries = new ImageTimeSeries<UShortImage>(UShortImage.class,dataImages,timeStamps,1 /*sdInfo.get(0).getZSize()*/);
		
		return rawImageTimeSeries;
	}

	@Override
	public void gatherIssues(IssueContext issueContext, ArrayList<Issue> issues) {
		super.gatherIssues(issueContext, issues);
		// here put bounds checking ... etc.
	}
}

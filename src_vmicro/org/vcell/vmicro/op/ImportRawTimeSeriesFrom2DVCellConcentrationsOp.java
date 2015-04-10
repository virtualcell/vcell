package org.vcell.vmicro.op;

import java.io.File;
import java.util.BitSet;
import java.util.Random;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.document.TSJobResultsSpaceStats;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.VCDataJobID;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.VCellSimReader;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.DataJobListener;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.CartesianMesh;

public class ImportRawTimeSeriesFrom2DVCellConcentrationsOp {
	
	public ImageTimeSeries<UShortImage> importRawTimeSeries(File vcellSimLogFile, String fluorFunctionName, double maxIntensity, boolean bNoise) throws Exception {
		ClientTaskStatusSupport clientTaskStatusSupport = null;
		ImageDataset timeRawData = importRawTimeSeries(vcellSimLogFile, fluorFunctionName, maxIntensity, bNoise, clientTaskStatusSupport );
		ImageTimeSeries<UShortImage> imageTimeSeries = new ImageTimeSeries<UShortImage>(UShortImage.class, timeRawData.getAllImages(),timeRawData.getImageTimeStamps(),1);
		return imageTimeSeries;
	}

	private static ImageDataset importRawTimeSeries(
			File vcellSimLogFile,
			String fluorFunctionName, 
			Double maxIntensity, 
			boolean bNoise,
			final ClientTaskStatusSupport progressListener
			) throws Exception
	{
		VCSimulationIdentifier vcSimulationIdentifier = VCellSimReader.getVCSimulationIdentifierFromVCellSimulationData(vcellSimLogFile);
		VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier,0);
		
		DataSetControllerImpl dataSetControllerImpl = VCellSimReader.getDataSetControllerImplFromVCellSimulationData(vcellSimLogFile);

		final DataJobEvent[] bStatus = new DataJobEvent[] {null};
		DataJobListener dataJobListener =
			new DataJobListener(){
				public void dataJobMessage(DataJobEvent event) {
					bStatus[0] = event;
					if(progressListener != null){
						progressListener.setProgress((int)(event.getProgress()/100.0*.75));
					}
				}
			};
		dataSetControllerImpl.addDataJobListener(dataJobListener);
		
		DataIdentifier[] dataIdentifiers = VCellSimReader.getDataIdentiferListFromVCellSimulationData(vcellSimLogFile, 0);
		DataIdentifier variableNameDataIdentifier = null;
		for (int i = 0; i < dataIdentifiers.length; i++) {
			if(dataIdentifiers[i].getName().equals(fluorFunctionName)){
				variableNameDataIdentifier = dataIdentifiers[i];
				break;
			}
		}
		if(variableNameDataIdentifier == null){
			throw new IllegalArgumentException("Variable "+fluorFunctionName+" not found.");
		}
		if(!variableNameDataIdentifier.getVariableType().equals(VariableType.VOLUME)){
			throw new IllegalArgumentException("Variable "+fluorFunctionName+" is not VOLUME type.");
		}
		double[] times = dataSetControllerImpl.getDataSetTimes(vcSimulationDataIdentifier);
		CartesianMesh cartesianMesh = dataSetControllerImpl.getMesh(vcSimulationDataIdentifier);
		BitSet allBitset = new BitSet(cartesianMesh.getNumVolumeElements());
		allBitset.set(0, cartesianMesh.getNumVolumeElements()-1);
		TimeSeriesJobSpec timeSeriesJobSpec = 
			new TimeSeriesJobSpec(
					new String[]{fluorFunctionName},
					new BitSet[] {allBitset},
					times[0],1,times[times.length-1],
					true,false,VCDataJobID.createVCDataJobID(VCellSimReader.getDotUser(), true)
					);
		TSJobResultsSpaceStats tsJobResultsSpaceStats =
			(TSJobResultsSpaceStats)dataSetControllerImpl.getTimeSeriesValues(null, vcSimulationDataIdentifier, timeSeriesJobSpec);
		//wait for job to finish
		while(bStatus[0] == null || bStatus[0].getEventTypeID() != DataJobEvent.DATA_COMPLETE){
			Thread.sleep(100);
			if(bStatus[0].getEventTypeID() == DataJobEvent.DATA_FAILURE){
				throw bStatus[0].getFailedJobException();
			}
		}
		double allTimesMin = tsJobResultsSpaceStats.getMinimums()[0][0];
		double allTimesMax = allTimesMin;
		for (int i = 0; i < times.length; i++) {
			allTimesMin = Math.min(allTimesMin, tsJobResultsSpaceStats.getMinimums()[0][i]);
			allTimesMax = Math.max(allTimesMax, tsJobResultsSpaceStats.getMaximums()[0][i]);
		}
//		double SCALE_MAX = maxIntensity.doubleValue();/*Math.pow(2,16)-1;*///Scale to 16 bits
		double linearScaleFactor = 1;
		if(maxIntensity != null)
		{
			linearScaleFactor = maxIntensity.doubleValue()/allTimesMax;
		}
		System.out.println("alltimesMin="+allTimesMin+" allTimesMax="+allTimesMax + " linearScaleFactor=" + linearScaleFactor);
		UShortImage[] scaledDataImages = new UShortImage[times.length];
		Random rnd = new Random();
		int shortMax = 65535;
		//set messge to load variable
		if(progressListener != null)
		{
			progressListener.setMessage("Loading variable " + fluorFunctionName + "...");
		}
		for (int i = 0; i < times.length; i++) {
			double[] rawData =
				dataSetControllerImpl.getSimDataBlock(null,vcSimulationDataIdentifier,fluorFunctionName,times[i]).getData();
			short[] scaledDataShort = new short[rawData.length];
			for (int j = 0; j < scaledDataShort.length; j++) {
				double scaledRawDataJ = rawData[j]*linearScaleFactor;
				if(bNoise)
				{
					double ran = rnd.nextGaussian();
					double scaledRawDataJ_withNoise = Math.max(0, (scaledRawDataJ + ran*Math.sqrt(scaledRawDataJ)));
					scaledRawDataJ_withNoise = Math.min(shortMax, scaledRawDataJ_withNoise);
					int scaledValue = (int)(scaledRawDataJ_withNoise);
					scaledDataShort[j]&= 0x0000;
					scaledDataShort[j]|= 0x0000FFFF & scaledValue;
				}
				else
				{
					int scaledValue = (int)(scaledRawDataJ);
					scaledDataShort[j]&= 0x0000;
					scaledDataShort[j]|= 0x0000FFFF & scaledValue;
				}
			}
			scaledDataImages[i] =
				new UShortImage(
					scaledDataShort,
					cartesianMesh.getOrigin(),
					cartesianMesh.getExtent(),
					cartesianMesh.getSizeX(),cartesianMesh.getSizeY(),cartesianMesh.getSizeZ());
			if(progressListener != null){
				int progress = (int)(((i+1)*1.0/times.length)*100);
				progressListener.setProgress(progress);
			}
		}
	
		ImageDataset rawImageDataSet = new ImageDataset(scaledDataImages,times,cartesianMesh.getSizeZ());

		return rawImageDataSet;
	}
}

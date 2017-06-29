package cbit.vcell.microscopy.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.NullSessionLog;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.Origin;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.TSJobResultsSpaceStats;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataJobID;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLDouble;

import cbit.image.ImageException;
import cbit.image.SourceDataInfo;
import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.DataJobListener;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.VariableType;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPOptimizationUtils;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataOperation;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.DataIndexHelper;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.TimePointHelper;
import cbit.vcell.simdata.DataOperationResults;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.CartesianMesh;

public class FrapDataUtils {

	public static DataSetControllerImpl getDataSetControllerImplFromVCellSimulationData(File vcellSimLogFile) throws Exception{
		File primaryDir = vcellSimLogFile.getParentFile();
		return new DataSetControllerImpl(
			new NullSessionLog(),new Cachetable(Cachetable.minute*10),primaryDir,primaryDir);
	}

	public static FRAPData importFRAPDataFromImageDataSet(ImageDataset imageDataSet){
			FRAPData frapData =
				new FRAPData(imageDataSet,
					new String[]{ FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name(),FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(),FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()}
				);
	//		frapData.setOriginalGlobalScaleInfo(null);
			return frapData;
		}

	public static DataIdentifier[] getDataIdentiferListFromVCellSimulationData(File vcellSimLogFile,int jobIndex) throws Exception{
		return
			getDataSetControllerImplFromVCellSimulationData(vcellSimLogFile).getDataIdentifiers(null,
				new VCSimulationDataIdentifier(
					getVCSimulationIdentifierFromVCellSimulationData(vcellSimLogFile),jobIndex)
			);
	}

	public static User getDotUser(){
		String bogusUserName = ".";
		return new User(bogusUserName,new KeyValue("0"));
	}

	public static VCSimulationIdentifier getVCSimulationIdentifierFromVCellSimulationData(File vcellSimLogFile){
		final String SIMID_PREFIX = "SimID_";
		String simulationKeyS =
			vcellSimLogFile.getName().substring(
					SIMID_PREFIX.length(),
					vcellSimLogFile.getName().indexOf('_', SIMID_PREFIX.length()));
		KeyValue simulationKey = new KeyValue(simulationKeyS);
		return new VCSimulationIdentifier(simulationKey,getDotUser());
	
	}

	public static FRAPData importFRAPDataFromVCellSimulationData(File vcellSimLogFile,String variableName, String bleachedMaskVarName, Double maxIntensity, boolean bNoise,
				final ClientTaskStatusSupport progressListener) throws Exception
		{
	//		bleachedMaskVarName = "laserMask_cell";
			VCSimulationIdentifier vcSimulationIdentifier = getVCSimulationIdentifierFromVCellSimulationData(vcellSimLogFile);
			VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier,0);
			
			DataSetControllerImpl dataSetControllerImpl = getDataSetControllerImplFromVCellSimulationData(vcellSimLogFile);
	
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
			
			DataIdentifier[] dataIdentifiers = getDataIdentiferListFromVCellSimulationData(vcellSimLogFile, 0);
			DataIdentifier variableNameDataIdentifier = null;
			for (int i = 0; i < dataIdentifiers.length; i++) {
				if(dataIdentifiers[i].getName().equals(variableName)){
					variableNameDataIdentifier = dataIdentifiers[i];
					break;
				}
			}
			if(variableNameDataIdentifier == null){
				throw new IllegalArgumentException("Variable "+variableName+" not found.");
			}
			if(!variableNameDataIdentifier.getVariableType().equals(VariableType.VOLUME)){
				throw new IllegalArgumentException("Variable "+variableName+" is not VOLUME type.");
			}
			double[] times = dataSetControllerImpl.getDataSetTimes(vcSimulationDataIdentifier);
			CartesianMesh cartesianMesh = dataSetControllerImpl.getMesh(vcSimulationDataIdentifier);
			BitSet allBitset = new BitSet(cartesianMesh.getNumVolumeElements());
			allBitset.set(0, cartesianMesh.getNumVolumeElements()-1);
			TimeSeriesJobSpec timeSeriesJobSpec = 
				new TimeSeriesJobSpec(
						new String[]{variableName},
						new BitSet[] {allBitset},
						times[0],1,times[times.length-1],
						true,false,VCDataJobID.createVCDataJobID(getDotUser(), true)
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
				progressListener.setMessage("Loading variable " + variableName + "...");
			}
			for (int i = 0; i < times.length; i++) {
				double[] rawData =
					dataSetControllerImpl.getSimDataBlock(null,vcSimulationDataIdentifier,variableName,times[i]).getData();
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
		
			ImageDataset imageDataSet = new ImageDataset(scaledDataImages,times,cartesianMesh.getSizeZ());
			FRAPData frapData = new FRAPData(imageDataSet, new String[]{ FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name(),FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(),FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()});
			
			
			 //get rois from log file
			if(bleachedMaskVarName != null)
			{
				//set message to load cell ROI variable 
				if(progressListener != null)
				{
					progressListener.setMessage("Loading ROIs...");
				}
				double[] rawROIBleached = dataSetControllerImpl.getSimDataBlock(null,vcSimulationDataIdentifier, bleachedMaskVarName, 0).getData();
				short[] scaledCellDataShort = new short[rawROIBleached.length];
				short[] scaledBleachedDataShort = new short[rawROIBleached.length];
				short[] scaledBackgoundDataShort = new short[rawROIBleached.length];
				for (int j = 0; j < scaledCellDataShort.length; j++) {
					boolean isCell = cartesianMesh.getCompartmentSubdomainNamefromVolIndex(j).equals("subVolume1");
					boolean isBackground = cartesianMesh.getCompartmentSubdomainNamefromVolIndex(j).equals("subVolume0");
					if(isCell)
					{
						scaledCellDataShort[j]= 1;
					}
					if(isBackground)
					{
						scaledBackgoundDataShort[j]= 1;
					}
					if(rawROIBleached[j] > 0.2)
					{
						
						scaledBleachedDataShort[j]= 1;
					}
					
				}
				UShortImage cellImage =
					new UShortImage(
						scaledCellDataShort,
						cartesianMesh.getOrigin(),
						cartesianMesh.getExtent(),
						cartesianMesh.getSizeX(),cartesianMesh.getSizeY(),cartesianMesh.getSizeZ());
				UShortImage bleachedImage =
					new UShortImage(
							scaledBleachedDataShort,
						cartesianMesh.getOrigin(),
						cartesianMesh.getExtent(),
						cartesianMesh.getSizeX(),cartesianMesh.getSizeY(),cartesianMesh.getSizeZ());
				UShortImage backgroundImage =
					new UShortImage(
							scaledBackgoundDataShort,
						cartesianMesh.getOrigin(),
						cartesianMesh.getExtent(),
						cartesianMesh.getSizeX(),cartesianMesh.getSizeY(),cartesianMesh.getSizeZ());
	
				if(progressListener != null){progressListener.setProgress(100);}
				
				frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).setROIImages(new UShortImage[]{cellImage});
				frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).setROIImages(new UShortImage[]{bleachedImage});
				frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()).setROIImages(new UShortImage[]{backgroundImage});
			}
	
			return frapData;
		}

	public static FRAPData importFRAPDataFromHDF5Data(File inputHDF5File, Double maxIntensity, ClientTaskStatusSupport progressListener) throws Exception{
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
		return createFrapData(sdiArr, dataProcessingOutputInfo.getVariableTimePoints(), 0, maxIntensity, progressListener);
	}

	public static FRAPData createFrapData(ArrayList<SourceDataInfo> sourceDataInfoArr,double[] times,int slice,Double maxIntensity,ClientTaskStatusSupport progressListener) throws ImageException{
		if(sourceDataInfoArr.size() != times.length){
			throw new ImageException("Error FRAPData.createFrapData: times array length must equal SourceDataInfo vector size");
		}
		// construct
		int XY_SIZE = sourceDataInfoArr.get(0).getXSize()*sourceDataInfoArr.get(0).getYSize();
		int SLICE_OFFSET = slice*XY_SIZE;
		int Z_SIZE = 1;//slice always 2D data
		// find scale factor to scale up the data to avoid losing precision when casting double to short
		double linearScaleFactor = 1;
		if(maxIntensity != null){
			double maxDataValue = 0;
			for (int i = 0; i < times.length; i++) {
				if(sourceDataInfoArr.get(i).getMinMax() != null){
					maxDataValue = Math.max(maxDataValue, sourceDataInfoArr.get(i).getMinMax().getMax());
				}else{
					double[] doubleData = (double[])sourceDataInfoArr.get(i).getData();
					for(int j=0; j<doubleData.length; j++){
						maxDataValue = Math.max(maxDataValue, doubleData[j]);
					}
				}
			}
			linearScaleFactor = maxIntensity.doubleValue()/maxDataValue;
		}
		//saving each time step 2D double array to a UShortImage
		UShortImage[] dataImages = new UShortImage[times.length];
		for (int i = 0; i < times.length; i++) {
			double[] doubleData = (double[])sourceDataInfoArr.get(i).getData();
			short[] shortData = new short[XY_SIZE];
			for(int j=0; j<shortData.length; j++)
			{
				shortData[j] = (short)(doubleData[j+(SLICE_OFFSET)]*linearScaleFactor);
			}
			dataImages[i] = new UShortImage(
						shortData,
						sourceDataInfoArr.get(i).getOrigin(),
						sourceDataInfoArr.get(i).getExtent(),
						sourceDataInfoArr.get(i).getXSize(),sourceDataInfoArr.get(i).getYSize(),Z_SIZE);
			
			if(progressListener != null){
				int progress = (int)(((i+1)*1.0/times.length)*100);
				progressListener.setProgress(progress);
			}
		}
		
		ImageDataset imageDataSet = new ImageDataset(dataImages,times,Z_SIZE);
		FRAPData frapData = new FRAPData(imageDataSet, new String[]{ FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name(),FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(),FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()});
		return frapData;
	}

	//this method calculates prebleach average for each pixel at different time points. back ground has been subtracted.
	//should not subtract background from it when using it as a normalized factor.
	public static double[] calculatePreBleachAverageXYZ(FRAPData frapData,int startingIndexForRecovery){
		long[] accumPrebleachImage = new long[frapData.getImageDataset().getISize().getXYZ()];//ISize: Image size including x, y, z. getXYZ()=x*y*z
		double[] avgPrebleachDouble = new double[accumPrebleachImage.length];
		double[] backGround = frapData.getAvgBackGroundIntensity();
		double accumAvgBkGround = 0; //accumulate background before starting index for recovery. used to subtract back ground.
		// changed in June, 2008 average prebleach depends on if there is prebleach images. 
		// Since the initial condition is normalized by prebleach avg, we have to take care the divided by zero error.
		if(startingIndexForRecovery > 0){
			if(backGround != null)//subtract background
			{
				for (int timeIndex = 0; timeIndex < startingIndexForRecovery; timeIndex++) {
					short[] pixels = frapData.getImageDataset().getPixelsZ(0, timeIndex);//channel index is 0. it is not supported yet. get image size X*Y*Z stored by time index. image store by UShortImage[Z*T]
					for (int i = 0; i < accumPrebleachImage.length; i++) {
						accumPrebleachImage[i] += 0x0000ffff&pixels[i];
					}
					accumAvgBkGround += backGround[timeIndex];
				}
				for (int i = 0; i < avgPrebleachDouble.length; i++) {
					avgPrebleachDouble[i] = ((double)accumPrebleachImage[i] - accumAvgBkGround)/startingIndexForRecovery;
				}
			}
			else //don't subtract background
			{
				for (int timeIndex = 0; timeIndex < startingIndexForRecovery; timeIndex++) {
					short[] pixels = frapData.getImageDataset().getPixelsZ(0, timeIndex);//channel index is 0. it is not supported yet. get image size X*Y*Z stored by time index. image store by UShortImage[Z*T]
					for (int i = 0; i < accumPrebleachImage.length; i++) {
						accumPrebleachImage[i] += 0x0000ffff&pixels[i];
					}
				}
				for (int i = 0; i < avgPrebleachDouble.length; i++) {
					avgPrebleachDouble[i] = (double)accumPrebleachImage[i]/(double)startingIndexForRecovery;
				}
			}
		}
		else{
			//if no prebleach image, use the last recovery image intensity as prebleach average.
			System.err.println("need to determine factor for prebleach average if no pre bleach images.");
			short[] pixels = frapData.getImageDataset().getPixelsZ(0, (frapData.getImageDataset().getSizeT() - 1));
			for (int i = 0; i < pixels.length; i++) {
				avgPrebleachDouble[i] = ((double)(0x0000ffff&pixels[i]) - backGround[frapData.getImageDataset().getSizeT() - 1]);
			}
		}
		//for each pixel if it's grater than 0, we add 1 offset to it. 
		//if it is smaller or equal to 0 , we set it to 1.
		for (int i = 0; i < avgPrebleachDouble.length; i++) {
			if(avgPrebleachDouble[i] <= FRAPOptimizationUtils.epsilon){
				avgPrebleachDouble[i] = 1;
			}
			else
			{
				avgPrebleachDouble[i]=avgPrebleachDouble[i] - FRAPOptimizationUtils.epsilon +1;
			}
		}
		return avgPrebleachDouble;
	}

	public static void saveImageDatasetAsExternalData(FRAPData frapData, LocalWorkspace localWorkspace, ExternalDataIdentifier newImageExtDataID, int startingIndexForRecovery, CartesianMesh cartesianMesh) throws ObjectNotFoundException, FileNotFoundException 
	{
		ImageDataset imageDataset = frapData.getImageDataset();
		if (imageDataset.getSizeC()>1){
			throw new RuntimeException("FRAPData.saveImageDatasetAsExternalData(): multiple channels not yet supported");
		}
		Extent extent = imageDataset.getExtent();
		ISize isize = imageDataset.getISize();
		int numImageToStore = imageDataset.getSizeT()-startingIndexForRecovery; //not include the prebleach 
		double[][][] pixData = new double[numImageToStore][2][]; //original fluor data and back ground average
		double[] timesArray = new double[numImageToStore];
		double[] bgAvg = frapData.getAvgBackGroundIntensity();
		
		for (int tIndex = startingIndexForRecovery; tIndex < imageDataset.getSizeT(); tIndex++) {
			short[] originalData = imageDataset.getPixelsZ(0, tIndex);// images according to zIndex at specific time points(tIndex)
			double[] doubleData = new double[originalData.length];
			double[] expandBgAvg = new double[originalData.length];
			for(int i = 0; i < originalData.length; i++)
			{
				doubleData[i] = 0x0000ffff & originalData[i];
				expandBgAvg[i] = bgAvg[tIndex];
			}
			pixData[tIndex-startingIndexForRecovery][0] = doubleData;
			pixData[tIndex-startingIndexForRecovery][1] = expandBgAvg;
			timesArray[tIndex-startingIndexForRecovery] = imageDataset.getImageTimeStamps()[tIndex]-imageDataset.getImageTimeStamps()[startingIndexForRecovery];
		}
		//changed in March 2008. Though biomodel is not created, we still let user save to xml file.
		Origin origin = new Origin(0,0,0);
		
		FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
		fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
		fdos.cartesianMesh = cartesianMesh;
		fdos.doubleSpecData =  pixData;
		fdos.specEDI = newImageExtDataID;
		fdos.varNames = new String[] {SimulationContext.FLUOR_DATA_NAME,"bg_average"};
		fdos.owner = LocalWorkspace.getDefaultOwner();
		fdos.times = timesArray;
		fdos.variableTypes = new VariableType[] {VariableType.VOLUME,VariableType.VOLUME};
		fdos.origin = origin;
		fdos.extent = extent;
		fdos.isize = isize;
		localWorkspace.getDataSetControllerImpl().fieldDataFileOperation(fdos);	
	}

	// export the frap data to matlab file. The matlab file contains timestamps(1*Tn) , mask(numImgX * numImgY), 
	// ImageDataSet(1*Tn) each cell of (1*Tn) point to a 2d image(numImgX * numImgY)
	public static void saveImageDatasetAsExternalMatlabData(FRAPData frapData, LocalWorkspace localWorkspace, String matlabFileName,  int startingIndexForRecovery, CartesianMesh cartesianMesh) throws IOException
	{
		ImageDataset imageDataset = frapData.getImageDataset();
		if (imageDataset.getSizeC()>1){
			throw new RuntimeException("FRAPData.saveImageDatasetAsExternalMatlabData(): multiple channels not yet supported");
		}
		int numX = cartesianMesh.getSizeX();
		int numY = cartesianMesh.getSizeY();
		//prepare variable to write into matlab file, listOfVars is the outmost structure to write to Matlab file.
		ArrayList<MLArray> listOfVars = new ArrayList<MLArray>();
		double[] timeArray = imageDataset.getImageTimeStamps();
		ROI cellROI = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
		short[] shortCellMask  = cellROI.getPixelsXYZ();
		
		//add image data set to Matlab cell, each cell points to a numX*numY array
		MLCell imageCell =  new MLCell("ImageDataSet",new int[] {timeArray.length, 1});
		for (int tIndex = 0; tIndex < imageDataset.getSizeT(); tIndex++) {
			short[] originalData = imageDataset.getPixelsZ(0, tIndex);// images according to zIndex at specific time points(tIndex)
			double[] doubleImgData = new double[originalData.length];
			for(int i = 0; i < originalData.length; i++)
			{
				doubleImgData[i] = 0x0000ffff & originalData[i];
			}
			MLDouble mlDoublImgData = new MLDouble("ImageDataAtTime_" + tIndex, doubleImgData, numX);
			imageCell.set(mlDoublImgData, tIndex , 0);
		}
		listOfVars.add(imageCell);
		
		//create mask in a Matlab 2D double(numX*numY)
		double[] doubleCellMask = new double[shortCellMask.length];
		for(int i = 0; i < shortCellMask.length; i++)
		{
			doubleCellMask[i] = 0x0000ffff & shortCellMask[i];
		}
		MLDouble cellMask = new MLDouble("CellMask", doubleCellMask, numX);
		listOfVars.add(cellMask);
		
		//create times in a Matlab 2D double(1*numTimePoints)
		MLDouble times = new MLDouble("ExperimentalTimeStamps",new double[][]{timeArray});
		listOfVars.add(times);
	
		MatFileWriter writer = new MatFileWriter();
		writer.write(matlabFileName, listOfVars);
	}

	//when creating double array for firstPostBleach and last PostBleach, etc images
	//We'll clamp all pixel value <= 0 to 0 and add offset 1 to the whole image.
	//For ROI images, we don't have to do so.
	public static double[] createDoubleArray(short[] shortData, double bkGround, boolean isOffset1ProcessNeeded){
		double[] doubleData = new double[shortData.length];
		for (int i = 0; i < doubleData.length; i++) {
			doubleData[i] = ((0x0000FFFF&shortData[i]) - bkGround);
			if(isOffset1ProcessNeeded)
			{
				if(doubleData[i] <= FRAPOptimizationUtils.epsilon)
				{
					doubleData[i] = 1;
				}
				else
				{
					doubleData[i] = doubleData[i] - FRAPOptimizationUtils.epsilon + 1;
				}
			}
		}
		return doubleData;
	}

	public static void saveROIsAsExternalData(FRAPData frapData, LocalWorkspace localWorkspace, ExternalDataIdentifier newROIExtDataID, int startingIndexForRecovery,	CartesianMesh cartesianMesh) throws ObjectNotFoundException, FileNotFoundException 
	{
		ImageDataset imageDataset = frapData.getImageDataset();
		Extent extent = imageDataset.getExtent();
		ISize isize = imageDataset.getISize();
		int NumTimePoints = 1; 
		int NumChannels = 13;//actually it is total number of ROIs(cell,bleached + 8 rings)+prebleach+firstPostBleach+lastPostBleach
		double[][][] pixData = new double[NumTimePoints][NumChannels][]; // dimensions: time points, channels, whole image ordered by z slices. 
		
	
		double[] temp_background = frapData.getAvgBackGroundIntensity();
		double[] avgPrebleachDouble = calculatePreBleachAverageXYZ(frapData, startingIndexForRecovery);
		
		pixData[0][0] = avgPrebleachDouble; // average of prebleach with background subtracted
		// first post-bleach with background subtracted
		pixData[0][1] = createDoubleArray(imageDataset.getPixelsZ(0, startingIndexForRecovery), temp_background[startingIndexForRecovery], true);
	//	adjustPrebleachAndPostbleachData(avgPrebleachDouble, pixData[0][1]);
		// last post-bleach image (at last time point) with background subtracted
		pixData[0][2] = createDoubleArray(imageDataset.getPixelsZ(0, imageDataset.getSizeT()-1), temp_background[imageDataset.getSizeT()-1], true);
		//below are ROIs, we don't need to subtract background for them.
		pixData[0][3] = createDoubleArray(frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).getBinaryPixelsXYZ(1), 0, false);
		pixData[0][4] = createDoubleArray(frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getBinaryPixelsXYZ(1), 0, false);
		if (frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name()) == null){
			//throw new RuntimeException("must first generate \"derived masks\"");
			pixData[0][5] = new double[imageDataset.getISize().getXYZ()];
			pixData[0][6] = new double[imageDataset.getISize().getXYZ()];
			pixData[0][7] = new double[imageDataset.getISize().getXYZ()];
			pixData[0][8] = new double[imageDataset.getISize().getXYZ()];
			pixData[0][9] = new double[imageDataset.getISize().getXYZ()];
			pixData[0][10] = new double[imageDataset.getISize().getXYZ()];
			pixData[0][11] = new double[imageDataset.getISize().getXYZ()];
			pixData[0][12] = new double[imageDataset.getISize().getXYZ()];
		}
		else{
			pixData[0][5] = createDoubleArray(frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name()).getBinaryPixelsXYZ(1), 0, false);
			pixData[0][6] = createDoubleArray(frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2.name()).getBinaryPixelsXYZ(1), 0, false);
			pixData[0][7] = createDoubleArray(frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3.name()).getBinaryPixelsXYZ(1), 0, false);
			pixData[0][8] = createDoubleArray(frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4.name()).getBinaryPixelsXYZ(1), 0, false);
			pixData[0][9] = createDoubleArray(frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5.name()).getBinaryPixelsXYZ(1), 0, false);
			pixData[0][10] = createDoubleArray(frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6.name()).getBinaryPixelsXYZ(1), 0, false);
			pixData[0][11] = createDoubleArray(frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7.name()).getBinaryPixelsXYZ(1), 0, false);
			pixData[0][12] = createDoubleArray(frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8.name()).getBinaryPixelsXYZ(1), 0, false);
		}
	
		Origin origin = new Origin(0,0,0);
		FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
		fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
		fdos.cartesianMesh = cartesianMesh;
		fdos.doubleSpecData =  pixData;
		fdos.specEDI = newROIExtDataID;
		fdos.varNames = new String[] {
				"prebleach_avg",
				"postbleach_first",
				"postbleach_last",
				"bleached_mask", 
				"cell_mask", 
				"ring1_mask",
				"ring2_mask",
				"ring3_mask",
				"ring4_mask",
				"ring5_mask",
				"ring6_mask",
				"ring7_mask",
				"ring8_mask"};
		fdos.owner = LocalWorkspace.getDefaultOwner();
		fdos.times = new double[] { 0.0 };
		fdos.variableTypes = new VariableType[] {
				VariableType.VOLUME,
				VariableType.VOLUME,
				VariableType.VOLUME,
				VariableType.VOLUME,
				VariableType.VOLUME,
				VariableType.VOLUME,
				VariableType.VOLUME,
				VariableType.VOLUME,
				VariableType.VOLUME,
				VariableType.VOLUME,
				VariableType.VOLUME,
				VariableType.VOLUME,
				VariableType.VOLUME};
		fdos.origin = origin;
		fdos.extent = extent;
		fdos.isize = isize;
		localWorkspace.getDataSetControllerImpl().fieldDataFileOperation(fdos);
	}

}

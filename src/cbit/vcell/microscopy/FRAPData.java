package cbit.vcell.microscopy;

import java.awt.Rectangle;
import java.io.File;
import java.util.BitSet;

import org.vcell.util.Compare;
import org.vcell.util.Extent;
import org.vcell.util.NullSessionLog;
import org.vcell.util.TSJobResultsSpaceStats;
import org.vcell.util.TimeSeriesJobSpec;

import cbit.image.ImageException;
import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.DataJobListener;
import org.vcell.util.document.KeyValue;
import org.vcell.util.Matchable;
import org.vcell.util.document.VCDataJobID;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.client.task.ClientTaskStatusSupport;
import cbit.vcell.geometry.gui.ROISourceData;

import org.vcell.util.document.User;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.CartesianMesh;

/**
 * Insert the type's description here.
 * Creation date: (1/24/2007 4:18:01 PM)
 * @author schaff
 * @version $Revision: 1.0 $
 */

public class FRAPData extends AnnotatedImageDataset implements Matchable, ROISourceData{

	public static class OriginalGlobalScaleInfo{
		public final int originalGlobalScaledMin;
		public final int originalGlobalScaledMax;
		public final double originalScaleFactor;
		public final double originalOffsetFactor;
		public OriginalGlobalScaleInfo(
			int originalGlobalScaledMin,int originalGlobalScaledMax,
			double originalScaleFactor,double originalOffsetFactor){
			this.originalGlobalScaledMin = originalGlobalScaledMin;
			this.originalGlobalScaledMax = originalGlobalScaledMax;
			this.originalScaleFactor = originalScaleFactor;
			this.originalOffsetFactor = originalOffsetFactor;
		}
	};
	private OriginalGlobalScaleInfo originalGlobalScaleInfo;
	
	public static enum VFRAP_ROI_ENUM {
	ROI_BLEACHED,
	ROI_BACKGROUND,
	ROI_CELL,
	ROI_BLEACHED_RING1,
	ROI_BLEACHED_RING2,
	ROI_BLEACHED_RING3,
	ROI_BLEACHED_RING4,
	ROI_BLEACHED_RING5,
	ROI_BLEACHED_RING6,
	ROI_BLEACHED_RING7,
	ROI_BLEACHED_RING8
//	ROI_NUCLEUS,
//	ROI_EXTRACELLULAR
}

/**
 * FRAPData constructor comment.
 * @param argImageDataset ImageDataset
 * @param argROIs ROI[]
 */
	public FRAPData(ImageDataset argImageDataset, ROI[] argROIs) {
		super(argImageDataset, argROIs);
	}

	public static FRAPData importFRAPDataFromImageDataSet(ImageDataset imageDataSet){
		FRAPData frapData =
			new FRAPData(imageDataSet,
				new String[]{ FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name(),FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(),FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()}
			);
		frapData.setOriginalGlobalScaleInfo(null);
		return frapData;
	}
	public static DataIdentifier[] getDataIdentiferListFromVCellSimulationData(File vcellSimLogFile,int jobIndex) throws Exception{
		return
			getDataSetControllerImplFromVCellSimulationData(vcellSimLogFile).getDataIdentifiers(
				new VCSimulationDataIdentifier(
					getVCSimulationIdentifierFromVCellSimulationData(vcellSimLogFile),jobIndex)
			);
	}
	public static DataSetControllerImpl getDataSetControllerImplFromVCellSimulationData(File vcellSimLogFile) throws Exception{
		File primaryDir = vcellSimLogFile.getParentFile();
		return new DataSetControllerImpl(
			new NullSessionLog(),new Cachetable(Cachetable.minute*10),primaryDir,primaryDir);
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
	public static FRAPData importFRAPDataFromVCellSimulationData(File vcellSimLogFile,String variableName,
			final ClientTaskStatusSupport progressListener) throws Exception{
		if(!vcellSimLogFile.getName().endsWith(SimDataConstants.LOGFILE_EXTENSION)){
			throw new IllegalArgumentException("VCell simulation logfile with extension "+SimDataConstants.LOGFILE_EXTENSION+" expected.");
		}
		VCSimulationIdentifier vcSimulationIdentifier =
			getVCSimulationIdentifierFromVCellSimulationData(vcellSimLogFile);
		VCSimulationDataIdentifier vcSimulationDataIdentifier = 
			new VCSimulationDataIdentifier(vcSimulationIdentifier,0);
		
		DataSetControllerImpl dataSetControllerImpl =
			getDataSetControllerImplFromVCellSimulationData(vcellSimLogFile);

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
		
		DataIdentifier[] dataIdentifiers =
			getDataIdentiferListFromVCellSimulationData(vcellSimLogFile, 0);
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
			(TSJobResultsSpaceStats)dataSetControllerImpl.getTimeSeriesValues(
				vcSimulationDataIdentifier, timeSeriesJobSpec);
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
		double SCALE_MAX = Math.pow(2,16)-1;//Scale to 16 bits
		double linearaScaleFactor = SCALE_MAX/allTimesMax;
		System.out.println("alltimesMin="+allTimesMin+" allTimesMax="+allTimesMax);
		UShortImage[] scaledDataImages = new UShortImage[times.length];
		for (int i = 0; i < times.length; i++) {
			double[] rawData =
				dataSetControllerImpl.getSimDataBlock(
						vcSimulationDataIdentifier,
						variableName,
						times[i]).getData();
			short[] scaledDataShort = new short[rawData.length];
//			if(allTimesMax> SCALE_MAX){
			for (int j = 0; j < scaledDataShort.length; j++) {
				int scaledValue = (int)(rawData[j]*linearaScaleFactor);
				scaledDataShort[j]&= 0x0000;
				scaledDataShort[j]|= 0x0000FFFF & scaledValue;
					
			}
//			}
			scaledDataImages[i] =
				new UShortImage(
					scaledDataShort,
					cartesianMesh.getOrigin(),
					cartesianMesh.getExtent(),
					cartesianMesh.getSizeX(),cartesianMesh.getSizeY(),cartesianMesh.getSizeZ());
			if(progressListener != null){progressListener.setProgress((int)(.75+(.25*(double)(i+1)/times.length)));}
		}
		ImageDataset imageDataSet = new ImageDataset(scaledDataImages,times,cartesianMesh.getSizeZ());
		FRAPData frapData = new FRAPData(imageDataSet,
				new String[]{ FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name(),FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(),FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()}
		);
		frapData.setOriginalGlobalScaleInfo(
			new FRAPData.OriginalGlobalScaleInfo(
				(int)(allTimesMin*linearaScaleFactor),
				(int)(allTimesMax*linearaScaleFactor),
				linearaScaleFactor,0));
		
		return frapData;
	}
	/**
	 * Constructor for FRAPData.
	 * @param argImageDataset ImageDataset
	 * @param argROITypes ROI.RoiType[]
	 */
	public FRAPData(ImageDataset argImageDataset, String[] argROINames) {
		super(argImageDataset, argROINames);
	}

/**
 * Method crop.
 * @param rect Rectangle
 * @return FRAPData
 * @throws ImageException
 */
@Override
public FRAPData crop(Rectangle rect) throws ImageException {
	ImageDataset croppedImageDataset = getImageDataset().crop(rect);
	ROI[] rois = getRois();
	ROI[] croppedROIs = new ROI[rois.length];
	ROI currentlyDisplayedROI = getCurrentlyDisplayedROI();
	ROI croppedCurrentROI = null;
	for (int i = 0; i < croppedROIs.length; i++) {
		croppedROIs[i] = rois[i].crop(rect);
		if (currentlyDisplayedROI == rois[i]){
			croppedCurrentROI = croppedROIs[i];
		}
	}
	FRAPData croppedFrapData = new FRAPData(croppedImageDataset,croppedROIs);
	setCurrentlyDisplayedROI(croppedCurrentROI);
	return croppedFrapData;
}

public OriginalGlobalScaleInfo getOriginalGlobalScaleInfo() {
	return originalGlobalScaleInfo;
}

public void setOriginalGlobalScaleInfo(OriginalGlobalScaleInfo originalGlobalScaleInfo) {
	this.originalGlobalScaleInfo = originalGlobalScaleInfo;
}

public double[] getAvgBackGroundIntensity()
{
	return FRAPDataAnalysis.getAverageROIIntensity(this,this.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()),null,null);
}

public boolean compareEqual(Matchable obj) 
{
	if (this == obj) {
		return true;
	}
	if (obj != null && obj instanceof FRAPData) 
	{
		FRAPData fData = (FRAPData) obj;
		if (!Compare.isEqualOrNull(getImageDataset(), fData.getImageDataset())){
			return false;
		}
		if (!Compare.isEqualOrNull(getRois(), fData.getRois())){
			return false;
		}
		return true;
	}
	return false;
}

public void chopImages(int startTimeIndex, int endTimeIndex) 
{
	UShortImage[] origImages = getImageDataset().getAllImages();
	double[] origTimeSteps = getImageDataset().getImageTimeStamps();
	
	UShortImage[] newImages = new UShortImage[endTimeIndex - startTimeIndex + 1];
	double[] newTimeSteps = new double[endTimeIndex - startTimeIndex + 1];
	System.arraycopy(origImages, startTimeIndex, newImages, 0, (endTimeIndex - startTimeIndex + 1));
	System.arraycopy(origTimeSteps, startTimeIndex, newTimeSteps, 0, (endTimeIndex - startTimeIndex + 1));
	//shift time to start from 0, it's not necessary 
//	double firstTimePoint = newTimeSteps[0];
//	for(int i = 0; i < newTimeSteps.length; i++)
//	{
//		newTimeSteps[i] = newTimeSteps[i] - firstTimePoint;
//	}
	
	int numOfZ = getImageDataset().getSizeZ();
	ImageDataset imgDataset = new ImageDataset(newImages, newTimeSteps, numOfZ);
	setImageDataset(imgDataset);
}

public void changeImageExtent(double imageSizeX, double imageSizeY) {
	UShortImage[] images = getImageDataset().getAllImages();
	double imageSizeZ = images[0].getExtent().getZ();
	for(int i = 0; i < images.length; i++)
	{
		images[i].setExtent(new Extent(imageSizeX, imageSizeY, imageSizeZ));
	}
}

}
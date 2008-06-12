package cbit.vcell.microscopy;

import java.awt.Rectangle;
import java.io.File;
import java.util.Arrays;
import java.util.BitSet;
import cbit.image.ImageException;
import cbit.sql.KeyValue;
import cbit.util.Matchable;
import cbit.util.TSJobResultsSpaceStats;
import cbit.util.TimeSeriesJobSpec;
import cbit.util.VCDataJobID;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.microscopy.ROI.RoiType;
import cbit.vcell.modeldb.NullSessionLog;
import cbit.vcell.server.User;
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

public class FRAPData extends AnnotatedImageDataset implements Matchable{

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
				new ROI.RoiType[] { RoiType.ROI_BLEACHED,RoiType.ROI_CELL,RoiType.ROI_BACKGROUND });
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
	public static FRAPData importFRAPDataFromVCellSimulationData(File vcellSimLogFile,String variableName) throws Exception{
		if(!vcellSimLogFile.getName().endsWith(SimDataConstants.LOGFILE_EXTENSION)){
			throw new IllegalArgumentException("VCell simulation logfile with extension "+SimDataConstants.LOGFILE_EXTENSION+" expected.");
		}
		VCSimulationIdentifier vcSimulationIdentifier =
			getVCSimulationIdentifierFromVCellSimulationData(vcellSimLogFile);
		VCSimulationDataIdentifier vcSimulationDataIdentifier = 
			new VCSimulationDataIdentifier(vcSimulationIdentifier,0);
		
		DataSetControllerImpl dataSetControllerImpl =
			getDataSetControllerImplFromVCellSimulationData(vcellSimLogFile);
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
					true,false,VCDataJobID.createVCDataJobID(getDotUser(), false)
					);
		TSJobResultsSpaceStats tsJobResultsSpaceStats =
			(TSJobResultsSpaceStats)dataSetControllerImpl.getTimeSeriesValues(
				vcSimulationDataIdentifier, timeSeriesJobSpec);
		double allTimesMin = tsJobResultsSpaceStats.getMinimums()[0][0];
		double allTimesMax = allTimesMin;
		for (int i = 0; i < times.length; i++) {
			allTimesMin = Math.min(allTimesMin, tsJobResultsSpaceStats.getMinimums()[0][i]);
			allTimesMax = Math.max(allTimesMax, tsJobResultsSpaceStats.getMaximums()[0][i]);
		}
		double scaleFactor = 1.0;
		double offsetFactor = 0;
		boolean IS_SCALEABLE = allTimesMax != allTimesMin;
		double SCALE_MAX = Math.pow(2,16)-1;//Scale to 16 bits
		if(IS_SCALEABLE){
			scaleFactor = SCALE_MAX/(allTimesMax-allTimesMin);
			offsetFactor = (SCALE_MAX*allTimesMin)/(allTimesMin-allTimesMax);
		}
		System.out.println("min="+allTimesMin+" max="+allTimesMax+" scale="+scaleFactor+" offset="+offsetFactor);
		UShortImage[] scaledDataImages = new UShortImage[times.length];
		for (int i = 0; i < times.length; i++) {
			double[] rawData =
				dataSetControllerImpl.getSimDataBlock(
						vcSimulationDataIdentifier,
						variableName,
						times[i]).getData();
			short[] scaledDataShort = new short[rawData.length];
			if(IS_SCALEABLE){
				for (int j = 0; j < scaledDataShort.length; j++) {
					int scaledValue = (int)(rawData[j]*scaleFactor + offsetFactor);
					scaledDataShort[j]&= 0x0000;
					scaledDataShort[j]|= 0x0000FFFF & scaledValue;
						
				}
			}else{
				Arrays.fill(scaledDataShort,(short)0);
			}
			scaledDataImages[i] =
				new UShortImage(
					scaledDataShort,
					cartesianMesh.getExtent(),
					cartesianMesh.getSizeX(),cartesianMesh.getSizeY(),cartesianMesh.getSizeZ());
		}
		ImageDataset imageDataSet = new ImageDataset(scaledDataImages,times,cartesianMesh.getSizeZ());
		FRAPData frapData = new FRAPData(imageDataSet, new ROI.RoiType[] { RoiType.ROI_BLEACHED,RoiType.ROI_CELL,RoiType.ROI_BACKGROUND});
		frapData.setOriginalGlobalScaleInfo(
			new FRAPData.OriginalGlobalScaleInfo(
				(int)(allTimesMin*scaleFactor + offsetFactor),
				(int)(allTimesMax*scaleFactor + offsetFactor),
				scaleFactor,offsetFactor
			)
		);
		return frapData;
	}
	/**
	 * Constructor for FRAPData.
	 * @param argImageDataset ImageDataset
	 * @param argROITypes ROI.RoiType[]
	 */
	public FRAPData(ImageDataset argImageDataset, ROI.RoiType[] argROITypes) {
		super(argImageDataset, argROITypes);
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

public boolean compareEqual(Matchable obj) 
{
	if (this == obj) {
		return true;
	}
	if (obj != null && obj instanceof FRAPData) 
	{
		FRAPData fData = (FRAPData) obj;
		if (!cbit.util.Compare.isEqualOrNull(getImageDataset(), fData.getImageDataset())){
			return false;
		}
		if (!cbit.util.Compare.isEqualOrNull(getRois(), fData.getRois())){
			return false;
		}
		return true;
	}
	return false;
}

}
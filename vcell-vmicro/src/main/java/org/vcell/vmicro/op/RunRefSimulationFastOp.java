package org.vcell.vmicro.op;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import cbit.vcell.solver.*;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.Origin;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.User;
import org.vcell.util.document.VersionFlag;
import org.vcell.vmicro.workflow.data.ExternalDataInfo;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.LocalContext;
import org.vcell.vmicro.workflow.data.LocalWorkspace;
import org.vcell.vmicro.workflow.data.ROIDataGenerator;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.ImageSubVolume;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.math.VariableType;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
import cbit.vcell.simdata.DataOperation;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.DataIndexHelper;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.TimePointHelper;
import cbit.vcell.simdata.DataOperationResults;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.FVSolverStandalone;

public class RunRefSimulationFastOp {
	
	private static final String ROI_MASK_NAME_PREFIX = "mask_";
	private static final String PSF_EXTDATA_NAME = "psfdata";
	private static final String PSF_EXTDATA_VARNAME = "psfVar";
	private static final String INITCONC_EXTDATA_NAME = "initConc";
	private static final String INITCONC_EXTDATA_VARNAME = "conc";
	private static final String ROI_EXTDATA_NAME = "roidata";
	public static final String ROI_SUMDATA_NAME = "sumROIData";
	private static final String ROI_SUMDATA_VARNAME = "roiSumDataVar";
	private static final String VAR_NAME = "species";
	private static final double REFERENCE_DIFF_DELTAT = 0.05;
	private static final double REFERENCE_DIFF_RATE_COEFFICIENT = 1;
	private static double MAX_DIFF_RATE_FOR_TIMEBOUNDS = 50;
	public static double REF_STARTINGTIME = 0;

	public RowColumnResultSet runRefSimFast(ROI cellROI_2D, ImageTimeSeries<FloatImage> normalizedTimeSeries, ROI[] imageDataROIs, UShortImage psf, LocalWorkspace localWorkspace, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		double[] timeStamps = normalizedTimeSeries.getImageTimeStamps();
		FloatImage initRefConc = normalizedTimeSeries.getAllImages()[0];
		double experimentalRecoveryTime = timeStamps[timeStamps.length-1] - timeStamps[0];
		RowColumnResultSet reducedData = runRefSimulation(cellROI_2D, imageDataROIs, psf, initRefConc, experimentalRecoveryTime, localWorkspace, clientTaskStatusSupport);
		
		return reducedData;
	}
	
	public double getReferenceDiffusionCoef(){
		return REFERENCE_DIFF_RATE_COEFFICIENT;
	}
	
	private ExternalDataInfo createNewExternalDataInfo(LocalContext localWorkspace, String extDataIDName){
		return LocalWorkspace.createNewExternalDataInfo(new File(localWorkspace.getDefaultSimDataDirectory()), extDataIDName);
	}

	private RowColumnResultSet runRefSimulation(ROI cellROI, ROI[] imageDataROIs, UShortImage psf, FloatImage initRefConc, double experimentalRecoveryTime, LocalWorkspace localWorkspace, ClientTaskStatusSupport progressListener) throws Exception
	{
		User owner = LocalWorkspace.getDefaultOwner();
		KeyValue simKey = LocalWorkspace.createNewKeyValue();
		
		// 
		// save first image from normalized time series as the initial concentration field data
		//
		ExternalDataInfo initialConcentrationExtData = createNewExternalDataInfo(localWorkspace, INITCONC_EXTDATA_NAME);
		Extent extent = initRefConc.getExtent();
		Origin origin = initRefConc.getOrigin();
		ISize isize = new ISize(initRefConc.getNumX(),initRefConc.getNumY(),initRefConc.getNumZ());		
		saveExternalData(initRefConc, INITCONC_EXTDATA_VARNAME, initialConcentrationExtData.getExternalDataIdentifier(), localWorkspace);
		FieldFunctionArguments initConditionFFA = new FieldFunctionArguments(INITCONC_EXTDATA_NAME, INITCONC_EXTDATA_VARNAME, new Expression(0.0), VariableType.VOLUME);

		//
		// save ROIs as a multivariate field data
		//
		ExternalDataInfo roiExtData = createNewExternalDataInfo(localWorkspace, ROI_EXTDATA_NAME);
		saveROIsAsExternalData(imageDataROIs, localWorkspace, roiExtData.getExternalDataIdentifier());
		ArrayList<FieldFunctionArguments> roiFFAs = new ArrayList<FieldFunctionArguments>();
		for (ROI roi : imageDataROIs){
			roiFFAs.add(new FieldFunctionArguments(ROI_EXTDATA_NAME, ROI_MASK_NAME_PREFIX+roi.getROIName(), new Expression(0.0), VariableType.VOLUME));
		}

		//
		// save PSF as a field data
		//
		ExternalDataInfo psfExtData = createNewExternalDataInfo(localWorkspace, PSF_EXTDATA_NAME);
		savePsfAsExternalData(psf, PSF_EXTDATA_VARNAME, psfExtData.getExternalDataIdentifier(), localWorkspace);
		FieldFunctionArguments psfFFA = new FieldFunctionArguments(PSF_EXTDATA_NAME, PSF_EXTDATA_VARNAME, new Expression(0.0), VariableType.VOLUME);
		
		
		TimeBounds timeBounds = getEstimatedRefTimeBound(experimentalRecoveryTime);
		double timeStepVal = REFERENCE_DIFF_DELTAT;
		Expression chirpedDiffusionRate = new Expression(REFERENCE_DIFF_RATE_COEFFICIENT +"*(t+"+ REFERENCE_DIFF_DELTAT +")");
		BioModel bioModel = createRefSimBioModel(simKey,owner,origin,extent,cellROI,timeStepVal,timeBounds,VAR_NAME,new Expression(initConditionFFA.infix()),psfFFA,chirpedDiffusionRate);
		
		if(progressListener != null){
			progressListener.setMessage("Running Reference Simulation...");
		}
		
		//run simulation
		Simulation simulation = bioModel.getSimulation(0);
		
		ROIDataGenerator roiDataGenerator = getROIDataGenerator(localWorkspace,imageDataROIs);
		simulation.getMathDescription().getPostProcessingBlock().addDataGenerator(roiDataGenerator);

		
		
		runFVSolverStandalone(
			new File(localWorkspace.getDefaultSimDataDirectory()),
			simulation,
			initialConcentrationExtData.getExternalDataIdentifier(),
			roiExtData.getExternalDataIdentifier(),
			psfExtData.getExternalDataIdentifier(),
			progressListener, true);

		
		KeyValue referenceSimKeyValue = simulation.getVersion().getVersionKey();

		VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(referenceSimKeyValue,LocalWorkspace.getDefaultOwner());
		VCSimulationDataIdentifier vcSimDataID = new VCSimulationDataIdentifier(vcSimID,0);
		File hdf5File = new File(localWorkspace.getDefaultSimDataDirectory(), vcSimDataID.getID()+SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_HDF5);
		
		// get post processing info (time points, variable sizes)
		DataOperation.DataProcessingOutputInfoOP dataOperationInfo = new DataOperation.DataProcessingOutputInfoOP(null/*no vcDataIdentifier OK*/,false,null);
		DataOperationResults.DataProcessingOutputInfo dataProcessingOutputInfo = (DataOperationResults.DataProcessingOutputInfo)DataSetControllerImpl.getDataProcessingOutput(dataOperationInfo, hdf5File);
		// get post processing data
		DataOperation.DataProcessingOutputDataValuesOP dataOperationDataValues = new DataOperation.DataProcessingOutputDataValuesOP(null/*no vcDataIdentifier OK*/,ROI_EXTDATA_NAME,TimePointHelper.createAllTimeTimePointHelper(),DataIndexHelper.createSliceDataIndexHelper(0),null,null);
		DataOperationResults.DataProcessingOutputDataValues dataProcessingOutputDataValues = (DataOperationResults.DataProcessingOutputDataValues)DataSetControllerImpl.getDataProcessingOutput(dataOperationDataValues, hdf5File);

		//
		// delete the simulation files
		//
		//
		// remove reference simulation files and field data files
		//
		File userDir = new File(localWorkspace.getDefaultSimDataDirectory());
		File[] oldSimFilesToDelete = getSimulationFileNames(userDir,referenceSimKeyValue);
		for (int i = 0; oldSimFilesToDelete != null && i < oldSimFilesToDelete.length; i++) {
			oldSimFilesToDelete[i].delete();
		}
		deleteCanonicalExternalData(localWorkspace, initialConcentrationExtData.getExternalDataIdentifier());
		deleteCanonicalExternalData(localWorkspace, roiExtData.getExternalDataIdentifier());
		deleteCanonicalExternalData(localWorkspace, roiExtData.getExternalDataIdentifier());

		
		//get ref sim time points (distorted by time dilation acceleration)
		double[] rawRefDataTimePoints = dataProcessingOutputInfo.getVariableTimePoints();
		//get shifted time points
		double[] correctedRefDataTimePoints = shiftTimeForBaseDiffRate(rawRefDataTimePoints);
		double[][] refData = dataProcessingOutputDataValues.getDataValues();
		
		//
		// for rowColumnResultSet with { "t", "roi1", .... , "roiN" } for reference data
		//
		int numROIs = imageDataROIs.length;
		String[] columnNames = new String[numROIs+1];
		columnNames[0] = "t";
		for (int i=0; i<numROIs; i++){
			columnNames[i+1] = imageDataROIs[i].getROIName();
		}
		RowColumnResultSet reducedData = new RowColumnResultSet(columnNames);
		for(int i=0; i<correctedRefDataTimePoints.length; i++){
			double[] row = new double[numROIs+1];
			row[0] = correctedRefDataTimePoints[i];
			double[] roiData = refData[i];
			for(int j=0; j<numROIs; j++){
				row[j+1] = roiData[j+1]; // roiData[0] is the average over the cell .. postbleach this shouldn't change for pure diffusion
			}
			reducedData.addRow(row);
		}
		
		return reducedData;
	}
	
	private static void deleteCanonicalExternalData(LocalContext localWorkspace,ExternalDataIdentifier originalExtDataID) throws Exception{
		File[] externalDataFiles = getCanonicalExternalDataFiles(localWorkspace, originalExtDataID);
		for (int i = 0;externalDataFiles != null && i < externalDataFiles.length; i++) {
			externalDataFiles[i].delete();
		}
	}

	private static File[] getCanonicalExternalDataFiles(LocalContext localWorkspace,ExternalDataIdentifier originalExtDataID){
		if(originalExtDataID != null){
			File userDir = new File(localWorkspace.getDefaultSimDataDirectory());
			File fdLogFile =
				new File(userDir,
						SimulationData.createCanonicalSimLogFileName(originalExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT,false));
			File fdMeshFile =
				new File(userDir,
						SimulationData.createCanonicalMeshFileName(originalExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT,false));
			File fdFunctionFile =
				new File(userDir,
						SimulationData.createCanonicalFunctionsFileName(originalExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT,false));
			File fdZipFile =
				new File(userDir,
						SimulationData.createCanonicalSimZipFileName(originalExtDataID.getKey(), 0,FieldDataFileOperationSpec.JOBINDEX_DEFAULT,false,false));
			return new File[] {fdLogFile,fdMeshFile,fdFunctionFile,fdZipFile};
		}
		return null;
	}

	private TimeBounds getEstimatedRefTimeBound(double experimentalRecoveryTime)
	{
		double timePeriod_D1 = MAX_DIFF_RATE_FOR_TIMEBOUNDS * experimentalRecoveryTime;
		double refEndTime = REFERENCE_DIFF_DELTAT * Math.sqrt((timePeriod_D1 * 2)/(REFERENCE_DIFF_DELTAT * REFERENCE_DIFF_DELTAT));
		return new TimeBounds(REF_STARTINGTIME, refEndTime);
	}

	private File[] getSimulationFileNames(File rootDir,KeyValue simKey){
		final String deleteTheseSimID = Simulation.createSimulationID(simKey);
		return rootDir.listFiles(new FileFilter(){
				public boolean accept(File pathname) {
					if (pathname.getName().startsWith(deleteTheseSimID)){
						return true;
					}else{
						return false;
					}
				}
			}
		);
	}

	private double[] shiftTimeForBaseDiffRate(double[] timePoints)
	{ 
		double delT = REFERENCE_DIFF_DELTAT;
		double s = REFERENCE_DIFF_RATE_COEFFICIENT;
		double[] shiftedTimePoints = new double[timePoints.length];
		shiftedTimePoints[0] = 0;
		for(int i=1; i< timePoints.length; i++)
		{
			shiftedTimePoints[i] = shiftedTimePoints[i-1]+s*(timePoints[i-1]+delT)*delT;
		}
		return shiftedTimePoints;
	}
		
	private ROIDataGenerator getROIDataGenerator(LocalContext localWorkspace, ROI[] rois) throws ImageException, IOException
	{
		//create ROI image
		short[] roiFieldData = null;
		if(rois.length > 0)
		{
			Origin origin = new Origin(0,0,0);
			Extent extent = rois[0].getRoiImages()[0].getExtent();
			ISize isize = rois[0].getISize();
			int numROIX = rois[0].getISize().getX();
			int numROIY = rois[0].getISize().getY();
			roiFieldData = new short[numROIX * numROIY];
			short regionCounter = 1;
			for(int roiIdx = 0; roiIdx<rois.length; roiIdx++)
			{
				short[] roiImg = rois[roiIdx].getPixelsXYZ();
				for(int pixelIdx=0; pixelIdx<(numROIX*numROIY); pixelIdx++)
				{
					if(roiImg[pixelIdx] > 0)
					{
						roiFieldData[pixelIdx] = regionCounter;
					}
				}
				regionCounter ++;
			}
			//create field data
			int NumTimePoints = 1;
			int NumChannels = 1; //8 rois integrated into 1 image
			short[][][] pixData = new short[NumTimePoints][NumChannels][];
			pixData[0][0] = roiFieldData;
			//get extental data id
			
			VCImage vcImage = new VCImageUncompressed(null, new byte[isize.getXYZ()], extent, isize.getX(),isize.getY(),isize.getZ());
			RegionImage regionImage = new RegionImage(vcImage,0,null,null,RegionImage.NO_SMOOTHING);
			CartesianMesh simpleCartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, extent, isize, regionImage);

			
			ExternalDataIdentifier newROIExtDataID = createNewExternalDataInfo(localWorkspace, ROI_SUMDATA_NAME).getExternalDataIdentifier();
			try {
		    		    	
		    	FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
		    	fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
		    	fdos.cartesianMesh = simpleCartesianMesh;
		    	fdos.shortSpecData =  pixData;
		    	fdos.specEDI = newROIExtDataID;
		    	fdos.varNames = new String[] {"roiSumDataVar"};
		    	fdos.owner = LocalWorkspace.getDefaultOwner();
		    	fdos.times = new double[] { 0.0 };
		    	fdos.variableTypes = new VariableType[] {VariableType.VOLUME};
		    	fdos.origin = origin;
		    	fdos.extent = extent;
		    	fdos.isize = isize;
		    	localWorkspace.getDataSetControllerImpl().fieldDataFileOperation(fdos);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new ROIDataGenerator(ROI_EXTDATA_NAME, /*name*/
					                    new int[] {0}/* volumePoints*/, 
					                                               new int[0]/* membranePoints*/,
					                                               regionCounter /*numRegions*/, 
					                                               0 /*zSlice*/, 
					                                               newROIExtDataID.getKey()/* fieldDataKey, sample image*/, 
					                                               new FieldFunctionArguments(ROI_SUMDATA_NAME, ROI_SUMDATA_VARNAME, new Expression(0), VariableType.VOLUME)/*FieldFunctionArguments, sample image*/,
					                                               false/* bStoreEnabled*/); 
					                                               
		}
		return null;
	}
	

	

	private void runFVSolverStandalone(
			File simulationDataDir,
			Simulation sim,
			ExternalDataIdentifier initialConditionExtDataID,
			ExternalDataIdentifier roiExtDataID,
			ExternalDataIdentifier psfExtDataID,
			ClientTaskStatusSupport progressListener,
			boolean bCheckSteadyState) throws Exception{

			FieldFunctionArguments[] fieldFunctionArgs = FieldUtilities.getFieldFunctionArguments(sim.getMathDescription());
			FieldDataIdentifierSpec[] fieldDataIdentifierSpecs = new FieldDataIdentifierSpec[fieldFunctionArgs.length];
			for (int i = 0; i < fieldDataIdentifierSpecs.length; i++) {
				if (fieldFunctionArgs[i].getFieldName().equals(initialConditionExtDataID.getName())){
					fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],initialConditionExtDataID);
				}else if (fieldFunctionArgs[i].getFieldName().equals(roiExtDataID.getName())){
					fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],roiExtDataID);
				}else if (fieldFunctionArgs[i].getFieldName().equals(psfExtDataID.getName())){
					fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],psfExtDataID);
				}else{
					throw new RuntimeException("failed to resolve field named "+fieldFunctionArgs[i].getFieldName());
				}
			}
			
			int jobIndex = 0;
			SimulationTask simTask = new SimulationTask(new SimulationJob(sim,jobIndex, fieldDataIdentifierSpecs),0);
			SolverUtilities.prepareSolverExecutable(sim.getSolverTaskDescription().getSolverDescription());
			
			//if we need to check steady state, do the following two lines
			if(bCheckSteadyState)
			{
				simTask.getSimulation().getSolverTaskDescription().setStopAtSpatiallyUniformErrorTolerance(ErrorTolerance.getDefaultSpatiallyUniformErrorTolerance());
			}
			
			FVSolverStandalone fvSolver = new FVSolverStandalone(simTask,simulationDataDir,false);		
			fvSolver.startSolver(); 
//			fvSolver.runSolver();
			
			SolverStatus status = fvSolver.getSolverStatus();
			while (status.getStatus() != SolverStatus.SOLVER_FINISHED && status.getStatus() != SolverStatus.SOLVER_ABORTED  && status.getStatus() != SolverStatus.SOLVER_STOPPED )
			{
				if(progressListener != null)
				{
					progressListener.setProgress((int)(fvSolver.getProgress()*100));
					if (progressListener.isInterrupted())
					{
						fvSolver.stopSolver();
						throw UserCancelException.CANCEL_GENERIC;
					}
				}
				try{
					Thread.sleep(1000);
				}catch(InterruptedException ex)
				{
					ex.printStackTrace(System.out);
					//catch interrupted exception and ignore it, otherwise it will popup a dialog in user interface saying"sleep interrupted"
				}
				status = fvSolver.getSolverStatus();
			}

			if(status.getStatus() != SolverStatus.SOLVER_FINISHED){
				throw new Exception("Sover did not finish normally." + status);
			}
		}
	
	private void savePsfAsExternalData(UShortImage psf, String varName, ExternalDataIdentifier newPsfExtDataID, LocalWorkspace localWorkspace) throws IOException, ImageException, ObjectNotFoundException{

		Origin origin = new Origin(0,0,0);
		Extent ext =new Extent(1, 1, 1);
		ISize isize = new ISize(3, 3, 1);
		VCImageUncompressed vcImage = new VCImageUncompressed(null, new byte[isize.getXYZ()], ext, isize.getX(),isize.getY(),isize.getZ());
		RegionImage regionImage = new RegionImage( vcImage,0,null,null,RegionImage.NO_SMOOTHING);
		CartesianMesh cartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, ext, isize, regionImage);	
		int NumTimePoints = 1;
		int NumChannels = 1; //8 rois integrated into 1 image
		short[][][] pixData = new short[NumTimePoints][NumChannels][1];

		pixData[0][0] = psf.getPixels();

    	FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
    	fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
    	fdos.cartesianMesh = cartesianMesh;
    	fdos.shortSpecData =  pixData;
    	fdos.specEDI = newPsfExtDataID;
    	fdos.varNames = new String[] {varName};
    	fdos.owner = LocalWorkspace.getDefaultOwner();
    	fdos.times = new double[] { 0.0 };
    	fdos.variableTypes = new VariableType[] {VariableType.VOLUME};
    	fdos.origin = origin;
    	fdos.extent = ext;
    	fdos.isize = isize;
    	localWorkspace.getDataSetControllerImpl().fieldDataFileOperation(fdos);
	}

	private void saveExternalData(Image image, String varName, ExternalDataIdentifier newROIExtDataID, LocalWorkspace localWorkspace) throws ObjectNotFoundException, ImageException, IOException 
	{
		LocalWorkspace.saveExternalData(image.getExtent(),image.getOrigin(),image.getISize(),image.getDoublePixels(),LocalWorkspace.getDefaultOwner(), varName, newROIExtDataID, localWorkspace.getDataSetControllerImpl());
	}
	
	private double[] createDoubleArray(short[] shortData){
		double[] doubleData = new double[shortData.length];
		for (int i = 0; i < doubleData.length; i++) {
			doubleData[i] = 0x0000FFFF & shortData[i];
		}
		return doubleData;
	}

	private void saveROIsAsExternalData(ROI[] rois, LocalWorkspace localWorkspace, ExternalDataIdentifier newROIExtDataID) throws ObjectNotFoundException, ImageException, IOException 
	{
		ISize isize = rois[0].getISize();
		Origin origin = rois[0].getRoiImages()[0].getOrigin();
		Extent extent = rois[0].getRoiImages()[0].getExtent();
		VCImage vcImage = new VCImageUncompressed(null, new byte[isize.getXYZ()], extent, isize.getX(),isize.getY(),isize.getZ());
		RegionImage regionImage = new RegionImage(vcImage,0,null,null,RegionImage.NO_SMOOTHING);
		CartesianMesh cartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, extent, isize, regionImage);
		int NumTimePoints = 1; 
		int NumChannels = rois.length;
		double[][][] pixData = new double[NumTimePoints][NumChannels][]; // dimensions: time points, channels, whole image ordered by z slices. 
		
		int index = 0;
		for (ROI roi : rois){
			pixData[0][index++] = createDoubleArray(roi.getBinaryPixelsXYZ(1));
		}
	
//		Origin origin = new Origin(0,0,0);
		FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
		fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
		fdos.cartesianMesh = cartesianMesh;
		fdos.doubleSpecData =  pixData;
		fdos.specEDI = newROIExtDataID;
		ArrayList<String> varNames = new ArrayList<String>();
		ArrayList<VariableType> varTypes = new ArrayList<VariableType>();
		for (ROI roi : rois){
			varNames.add(ROI_MASK_NAME_PREFIX+roi.getROIName());
			varTypes.add(VariableType.VOLUME);
		}
		fdos.varNames = varNames.toArray(new String[0]);
		fdos.owner = LocalWorkspace.getDefaultOwner();
		fdos.times = new double[] { 0.0 };
		fdos.variableTypes = varTypes.toArray(new VariableType[0]);
		fdos.origin = origin;
		fdos.extent = extent;
		fdos.isize = isize;
		localWorkspace.getDataSetControllerImpl().fieldDataFileOperation(fdos);
	}


	private BioModel createRefSimBioModel(KeyValue simKey, User owner, Origin origin, Extent extent, ROI cellROI_2D, double timeStepVal, TimeBounds timeBounds, String varName, Expression initialConcentration, FieldFunctionArguments psfFFA, Expression chirpedDiffusionRate) throws Exception {

		int numX = cellROI_2D.getRoiImages()[0].getNumX();
		int numY = cellROI_2D.getRoiImages()[0].getNumY();
		int numZ = cellROI_2D.getRoiImages().length;
		short[] shortPixels = cellROI_2D.getRoiImages()[0].getPixels();
		byte[] bytePixels = new byte[numX*numY*numZ];
		final byte EXTRACELLULAR_PIXVAL = 0;
		final byte CYTOSOL_PIXVAL = 1;
		for (int i = 0; i < bytePixels.length; i++) {
			if (shortPixels[i]!=0){
				bytePixels[i] = CYTOSOL_PIXVAL;
			}
		}
		VCImage maskImage;
		try {
			maskImage = new VCImageUncompressed(null,bytePixels,extent,numX,numY,numZ);
		} catch (ImageException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create mask image for geometry");
		}
		Geometry geometry = new Geometry("geometry",maskImage);
		geometry.getGeometrySpec().setOrigin(origin);
		if(geometry.getGeometrySpec().getNumSubVolumes() != 2){
			throw new Exception("Cell ROI has no ExtraCellular.");
		}
		String EXTRACELLULAR_NAME = "ec";
		String CYTOSOL_NAME = "cyt";
		String PLASMAMEMBRANE_NAME = "pm";
		
		ImageSubVolume subVolume0 = (ImageSubVolume)geometry.getGeometrySpec().getSubVolume(0);
		ImageSubVolume subVolume1 = (ImageSubVolume)geometry.getGeometrySpec().getSubVolume(1);
		if (subVolume0.getPixelValue() == EXTRACELLULAR_PIXVAL){
			subVolume0.setName(EXTRACELLULAR_NAME);
			subVolume1.setName(CYTOSOL_NAME);
		}else{
			subVolume0.setName(CYTOSOL_NAME);
			subVolume1.setName(EXTRACELLULAR_NAME);
		}
		geometry.getGeometrySurfaceDescription().updateAll();

		BioModel bioModel = new BioModel(null);
		bioModel.setName("unnamed");
		Model model = new Model("model");
		bioModel.setModel(model);
		Feature extracellular = model.addFeature(EXTRACELLULAR_NAME);
		Feature cytosol = model.addFeature(CYTOSOL_NAME);
		Membrane plasmaMembrane = model.addMembrane(PLASMAMEMBRANE_NAME);

		SimulationContext simContext = new SimulationContext(bioModel.getModel(),geometry);
		
		bioModel.addSimulationContext(simContext);
		FeatureMapping cytosolFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(cytosol);
		FeatureMapping extracellularFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(extracellular);
		MembraneMapping plasmaMembraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(plasmaMembrane);
		
		SubVolume cytSubVolume = geometry.getGeometrySpec().getSubVolume(CYTOSOL_NAME);
		SubVolume exSubVolume = geometry.getGeometrySpec().getSubVolume(EXTRACELLULAR_NAME);
		SurfaceClass pmSurfaceClass = geometry.getGeometrySurfaceDescription().getSurfaceClass(exSubVolume, cytSubVolume);
		
		cytosolFeatureMapping.setGeometryClass(cytSubVolume);
		extracellularFeatureMapping.setGeometryClass(exSubVolume);
		plasmaMembraneMapping.setGeometryClass(pmSurfaceClass);
		
		cytosolFeatureMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
		extracellularFeatureMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
		plasmaMembraneMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
		
		
		//Mobile Species
		Species diffusingSpecies = model.addSpecies(new Species("species", "Mobile bleachable species"));
		SpeciesContext diffusingSpeciesContext = model.addSpeciesContext(diffusingSpecies,cytosol);
		diffusingSpeciesContext.setName(varName);

		SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(diffusingSpeciesContext);
		scs.getInitialConditionParameter().setExpression(initialConcentration);
		chirpedDiffusionRate.bindExpression(scs);
		scs.getDiffusionParameter().setExpression(chirpedDiffusionRate);
		
//		simContext.getMicroscopeMeasurement().addFluorescentSpecies(speciesContexts[0]);
//		simContext.getMicroscopeMeasurement().setConvolutionKernel(new MicroscopeMeasurement.ProjectionZKernel());
		MathDescription mathDescription = simContext.createNewMathMapping().getMathDescription();
		
		// maybe there is a way that works for simContext.getMicroscopeMeasurement().... but this is needed now.
		mathDescription.addVariable(new Function(Simulation.PSF_FUNCTION_NAME, new Expression(psfFFA.infix()), null));
		
		
		simContext.setMathDescription(mathDescription);
				
		
		SimulationVersion simVersion = new SimulationVersion(simKey,"sim1",owner,new GroupAccessNone(),new KeyValue("0"),new BigDecimal(0),new Date(),VersionFlag.Current,"",null);
		Simulation newSimulation = new Simulation(simVersion,simContext.getMathDescription(), new SimulationOwner.StandaloneSimulationOwner());
		newSimulation.getSolverTaskDescription().setSolverDescription(SolverDescription.FiniteVolumeStandalone);
		simContext.addSimulation(newSimulation);
		newSimulation.getSolverTaskDescription().setTimeBounds(timeBounds);
		newSimulation.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec(timeStepVal));
		newSimulation.getMeshSpecification().setSamplingSize(cellROI_2D.getISize());
		newSimulation.getSolverTaskDescription().setTimeStep(new TimeStep(timeStepVal, timeStepVal, timeStepVal));
		
		return bioModel;
	}
}

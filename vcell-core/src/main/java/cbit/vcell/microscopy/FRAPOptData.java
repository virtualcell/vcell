/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy;

import java.io.File;
import java.util.Arrays;

import org.vcell.optimization.ProfileData;
import org.vcell.optimization.ProfileDataElement;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.KeyValue;

import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.resource.StdoutSessionLog;
import cbit.vcell.simdata.DataOperation;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.DataIndexHelper;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.TimePointHelper;
import cbit.vcell.simdata.DataOperationResults;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;


public class FRAPOptData {
	/*----------------for reference simulation-------------------*/
	//The time bounds for reference simulation, simulation will stop at spatial uniform or the ending time(if uniform hasn't reached yet). 
	public static double REF_STARTINGTIME = 0;
	public static double MAX_DIFF_RATE_FOR_TIMEBOUNDS = 50;
	//Variable reference diffusion rate actually used in simulation in order to reach spatial uniform faster
	//after the simulation, the results will convert to the results as if it was run by diffusion rate  1.
	public static final double REFERENCE_DIFF_DELTAT = 0.05;
	private static final double REFERENCE_DIFF_RATE_COEFFICIENT = 1;
	private static final String REFERENCE_DIFF_RATE_STR = REFERENCE_DIFF_RATE_COEFFICIENT +"*(t+"+ REFERENCE_DIFF_DELTAT +")";
	
	/*----------------for calculating confidence intervals of estimated parameters-----------------*/
	//initial increasing/decreasing steps
	public static final double[] DEFAULT_CI_STEPS = new double[]{0.04, 0.004, 0.04, 0.04, 0.04};
	public static final double[] DEFAULT_CI_STEPS_OFF_RATE = new double[]{0.04, 0.04};
	public static final int MAX_ITERATION = 100;
	public static final double MIN_LIKELIHOOD_CHANGE = 0.01;
	
	private int numEstimatedParams = 0;
	private double leastError = 0;
	private boolean bApplyMeasurementError = false;
	
	private FRAPStudy expFrapStudy = null;
	private LocalWorkspace localWorkspace = null;
	
	private double[][] dimensionReducedRefData = null;
	private double[] refDataTimePoints = null;
	private Parameter fixedParam = null;
	
	
	//used for optimization when taking measurement error into account.
	//first dimension length 11, according to the order in FRAPData.VFRAP_ROI_ENUM
	//second dimension time, total time length - starting index for recovery 
	private double[][] measurementErrors = null;
	
	public FRAPOptData(FRAPStudy argExpFrapStudy, int numberOfEstimatedParams, LocalWorkspace argLocalWorkSpace,
			ClientTaskStatusSupport progressListener) throws Exception
	{
		expFrapStudy = argExpFrapStudy;
		setNumEstimatedParams(numberOfEstimatedParams);
		localWorkspace = argLocalWorkSpace;
		if(progressListener != null){
			progressListener.setMessage("Getting experimental data ROI averages...");
		}
		dimensionReducedRefData = getDimensionReducedRefData(progressListener, null);
	}
	
	public FRAPOptData(FRAPStudy argExpFrapStudy, int numberOfEstimatedParams, LocalWorkspace argLocalWorkSpace, SimpleReferenceData simRefData) throws Exception
	{
		if(simRefData.getNumDataColumns()!= (1+argExpFrapStudy.getFrapData().getROILength()))
		{
			throw new Exception("Stored reference data is illegal. ");
		}
		expFrapStudy = argExpFrapStudy;
		setNumEstimatedParams(numberOfEstimatedParams);
		localWorkspace = argLocalWorkSpace;
		dimensionReducedRefData = getDimensionReducedRefData(null, simRefData); 
	}
	
	public static TimeStep getRefTimeStep()
	{
		//time step is estimated as deltaX^2/(4*D)
//		if(refTimeStep == null)
//		{
//			int numX = getExpFrapStudy().getFrapData().getImageDataset().getAllImages()[0].getNumX();
//			double deltaX = getExpFrapStudy().getFrapData().getImageDataset().getAllImages()[0].getExtent().getX()/(numX-1);
//			double timeStep = (deltaX*deltaX /REF_DIFFUSION_RATE_PARAM.getInitialGuess()) * 0.25;
//			refTimeStep = new TimeStep(timeStep, timeStep, timeStep);
//		}
//		return refTimeStep;
		double timeStep = REFERENCE_DIFF_DELTAT; 
		return new TimeStep(timeStep, timeStep, timeStep);
	}
	
	public static DefaultOutputTimeSpec getRefTimeSpec()
	{
//		if(refTimeSpec == null)
//		{
//			int numSavePoints = (int)Math.ceil((getRefTimeBounds().getEndingTime() - getRefTimeBounds().getStartingTime())/getRefTimeStep().getDefaultTimeStep());
//			if(numSavePoints <= FRAPOptData.maxRefSavePoints)
//			{
//				refTimeSpec = new DefaultOutputTimeSpec(1, FRAPOptData.maxRefSavePoints);
//			}
//			else
//			{
//				int keepEvery = (int)Math.ceil(numSavePoints/FRAPOptData.maxRefSavePoints);
//				refTimeSpec = new DefaultOutputTimeSpec(keepEvery, FRAPOptData.maxRefSavePoints);
//			}
//			
//		}
		return new DefaultOutputTimeSpec(1, 10000);
//		int startIdx = FRAPDataAnalysis.getRecoveryIndex(getExpFrapStudy().getFrapData());
//		double[] timeStamps = getExpFrapStudy().getFrapData().getImageDataset().getImageTimeStamps();
//		return new UniformOutputTimeSpec(timeStamps[startIdx+1]-timeStamps[startIdx]);
	}
	
	//estimated timebounds by knowing the image data time bounds 
	//and the possible diffusion rate upper bound (e.g 50)
	// g[0..n] = time points for diff =1, t[0..n) = time points for adaptive diff rate from ref sim, a=deltaT time interval in ref sim which is 0.05 by default
	// g_n = g_0 + (a^2*n +a^2*n^2)/2, solve n then use n*deltaT to get ref sim time bounds
	public static TimeBounds getEstimatedRefTimeBound(FRAPStudy fStudy)
	{
		int startIdx = fStudy.getStartingIndexForRecovery();
		double[] timeStamps = fStudy.getFrapData().getImageDataset().getImageTimeStamps();
		double imgTimePeriod = timeStamps[timeStamps.length -1] - timeStamps[startIdx];
		double timePeriod_D1 = MAX_DIFF_RATE_FOR_TIMEBOUNDS * imgTimePeriod;
		double refEndTime = REFERENCE_DIFF_DELTAT * Math.sqrt((timePeriod_D1 * 2)/(REFERENCE_DIFF_DELTAT * REFERENCE_DIFF_DELTAT));
		return new TimeBounds(REF_STARTINGTIME, refEndTime);
	}
	
	public double[] getRefDataTimePoints() {
		return refDataTimePoints;
	}

	public double[][] getDimensionReducedRefData(ClientTaskStatusSupport progressListener, SimpleReferenceData srData) throws Exception
	{
		if(dimensionReducedRefData == null)
		{
			if(srData != null)
			{
				getFromStoredRefData(srData);
			}
			else
			{
				refreshDimensionReducedRefData(progressListener);
			}
		}
		return dimensionReducedRefData;
	}
	
	public double[][] getDimensionReducedExpData() throws Exception
	{
		return expFrapStudy.getDimensionReducedExpData();
	}
	
	public double[] getReducedExpTimePoints() {
		
		return expFrapStudy.getReducedExpTimePoints();
	}
	
	public void refreshDimensionReducedRefData(final ClientTaskStatusSupport progressListener) throws Exception
	{
		
		System.out.println("run simulation...");
		KeyValue referenceSimKeyValue = null;
		referenceSimKeyValue = runRefSimulation(progressListener);
 		System.out.println("simulation done...");
		
		VCSimulationIdentifier vcSimID =
			new VCSimulationIdentifier(referenceSimKeyValue,LocalWorkspace.getDefaultOwner());
		VCSimulationDataIdentifier vcSimDataID =
			new VCSimulationDataIdentifier(vcSimID,FieldDataFileOperationSpec.JOBINDEX_DEFAULT);
		//read results from netCDF file
		File hdf5File = new File(getLocalWorkspace().getDefaultSimDataDirectory(), vcSimDataID.getID()+SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_HDF5);
		//get dataprocessing output
		DataOperationResults.DataProcessingOutputInfo dataProcessingOutputInfo =
				(DataOperationResults.DataProcessingOutputInfo)DataSetControllerImpl.getDataProcessingOutput(new DataOperation.DataProcessingOutputInfoOP(null/*no vcDataIdentifier OK*/,false,null), hdf5File);
		DataOperationResults.DataProcessingOutputDataValues dataProcessingOutputDataValues =
				(DataOperationResults.DataProcessingOutputDataValues)DataSetControllerImpl.getDataProcessingOutput(
					new DataOperation.DataProcessingOutputDataValuesOP(null/*no vcDataIdentifier OK*/,FRAPStudy.ROI_EXTDATA_NAME,TimePointHelper.createAllTimeTimePointHelper(),DataIndexHelper.createSliceDataIndexHelper(0),null,null), hdf5File);

//		DataProcessingOutput dataProcessingOutput = getRawReferenceDataFromHDF5(hdf5File);
		//get ref sim time points
		double[] rawRefDataTimePoints = dataProcessingOutputInfo.getVariableTimePoints();
		//get shifted time points
		refDataTimePoints = shiftTimeForBaseDiffRate(rawRefDataTimePoints);
		//get summarized raw ref data
		double[][] rawData = new double[dataProcessingOutputInfo.getVariableISize(FRAPStudy.ROI_EXTDATA_NAME).getXYZ()][rawRefDataTimePoints.length];
		for(int i=0; i<rawRefDataTimePoints.length; i++){
			double[] temp = dataProcessingOutputDataValues.getDataValues()[i];
			for(int j=0; j<temp.length; j++){
				rawData[j][i] = temp[j];
			}
		}

		 //contains only 8rois +1(the area that beyond 8 rois)
		//extend to whole roi data
		dimensionReducedRefData = FRAPOptimizationUtils.extendSimToFullROIData(expFrapStudy.getFrapData(), rawData, refDataTimePoints.length);
		
		System.out.println("generating dimension reduced ref data, done ....");
		
		//if reference simulation completes successfully, we save reference data info and remove old simulation files.
		boolean[] selectedROIs = new boolean[FRAPData.VFRAP_ROI_ENUM.values().length];
		Arrays.fill(selectedROIs, true);
		getExpFrapStudy().setStoredRefData(FRAPOptimizationUtils.doubleArrayToSimpleRefData(dimensionReducedRefData, getRefDataTimePoints(), 0, selectedROIs));

		//remove reference simulation files
		FRAPStudy.removeSimulationFiles(referenceSimKeyValue, getLocalWorkspace()); 
		//remove experimental and roi external files
		FRAPStudy.removeExternalFiles(getExpFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(), 
				                      getExpFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(), getLocalWorkspace());
	}
	
//	public double[][] getDataFromSourceDataInfo(DataProcessingOutput dataProcessingOutput)
//	{
//		double[][] results = null;
//		Vector<SourceDataInfo> sourceDataInfoList = dataProcessingOutput.getDataGenerators().get(FRAPStudy.ROI_EXTDATA_NAME);
//		if(sourceDataInfoList != null && sourceDataInfoList.size() > 0)
//		{
//			double[] rowData = (double[])sourceDataInfoList.get(0).getData();
//			results = new double[rowData.length][sourceDataInfoList.size()];// rois * timePoints
//			for(int i=0; i<sourceDataInfoList.size(); i++)
//			{
//				double[] temp = (double[])sourceDataInfoList.get(i).getData();
//				for(int j=0; j<temp.length; j++)
//				{
//					results[j][i] = temp[j];
//				}
//			}
//		}
//		return results;
//	}
//	
//	public DataProcessingOutput getRawReferenceDataFromHDF5(File hdf5File) throws DataAccessException {
//		try {
//			DataProcessingOutput dataProcessingOutput = null;
//			
//			if (hdf5File.exists()) {
//				dataProcessingOutput = new DataProcessingOutput();
//				// retrieve an instance of H5File
//				FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
//				if (fileFormat == null){
//					throw new Exception("Cannot find HDF5 FileFormat.");
//				}
//				// open the file with read-only access	
//				FileFormat testFile = null;
//				try{
//					testFile = fileFormat.open(hdf5File.getAbsolutePath(), FileFormat.READ);
//					// open the file and retrieve the file structure
//					testFile.open();
//					Group root = (Group)((javax.swing.tree.DefaultMutableTreeNode)testFile.getRootNode()).getUserObject();
//					DataSetControllerImpl.populateHDF5(root, "",dataProcessingOutput,false,null,null,null);
//				}catch(Exception e){
//					throw new IOException("Error reading file");
//				}finally{
//					if(testFile != null){testFile.close();}
//				}
//				//uncomment it for Debug
//				//DataSetControllerImpl.do_iterate(hdf5File);
//			}else{
//				throw new FileNotFoundException("file not found");
//			}
//
//			return dataProcessingOutput;
//		}catch (Exception e){
//			e.printStackTrace(System.out);
//			throw new DataAccessException(e.getMessage(),e);
//		}
//	}
	
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
		
	public KeyValue runRefSimulation(final ClientTaskStatusSupport progressListener) throws Exception
	{
		BioModel bioModel = null;
		if(progressListener != null){
			progressListener.setMessage("Running Reference Simulation...");
		}
		try{
			FieldDataIdentifierSpec psfFieldFunc = FRAPStudy.getPSFFieldData(getLocalWorkspace());
			bioModel = FRAPStudy.createNewRefBioModel(expFrapStudy,
													REFERENCE_DIFF_RATE_STR, 
													getRefTimeStep(), 
													LocalWorkspace.createNewKeyValue(), 
													LocalWorkspace.getDefaultOwner(),
													psfFieldFunc,
													expFrapStudy.getStartingIndexForRecovery());
			
			//change time bound and time step
			Simulation sim = bioModel.getSimulations()[0];
			
			ROIDataGenerator roiDataGenerator = getExpFrapStudy().getROIDataGenerator(getLocalWorkspace());
			sim.getMathDescription().getPostProcessingBlock().addDataGenerator(roiDataGenerator);
			System.out.println("run FRAP Reference Simulation...");

			//run simulation
			FRAPStudy.runFVSolverStandalone_ref(
				new File(getLocalWorkspace().getDefaultSimDataDirectory()),
				new StdoutSessionLog(LocalWorkspace.getDefaultOwner().getName()),
				bioModel.getSimulation(0),
				getExpFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),
				getExpFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),
				psfFieldFunc.getExternalDataIdentifier(),
				progressListener, true);

			KeyValue referenceSimKeyValue = sim.getVersion().getVersionKey();
			
			return referenceSimKeyValue;
		}catch(Exception e){
			e.printStackTrace(System.out);
			if(bioModel != null && bioModel.getSimulations() != null){
				FRAPStudy.removeExternalDataAndSimulationFiles(
					bioModel.getSimulations()[0].getVersion().getVersionKey(), null, null, getLocalWorkspace());
			}
			throw e;
		}
	}
	
	public double computeError(double newParamVals[], boolean[] eoi) throws Exception
	{
		double error = 0;
		int numberFixedParam = (fixedParam == null)? 0:1;
		if((getNumEstimatedParams() + numberFixedParam) == FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF)
		{
			error = FRAPOptimizationUtils.getErrorByNewParameters_oneDiffRate(FRAPModel.REF_DIFFUSION_RATE_PARAM.getInitialGuess(), 
					                                              newParamVals,
					                                              getDimensionReducedRefData(null, null),
					                                              getDimensionReducedExpData(),
					                                              refDataTimePoints,
					                                              getReducedExpTimePoints(),
					                                              getExpFrapStudy().getFrapData().getROILength(), 
					                                              eoi,
					                                              measurementErrors,
					                                              fixedParam,
					                                              isApplyMeasurementError());
			
			//uncomment for debug. The following code is to test if the error calculation functions correctly.
			//compare error calculated by one diff rate and another error with second diff rate and fraction both as 0s, we should get the same errors.
//			double[] paramVals2 = new double[FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF];
//			for(int i=0; i<newParamVals.length; i++)
//			{
//				paramVals2[i] = newParamVals[i];
//			}
//			
//			double error2 = FRAPOptimization.getErrorByNewParameters_twoDiffRates(REF_DIFFUSION_RATE_PARAM.getInitialGuess(),
//																  paramVals2,
//																  getDimensionReducedRefData(null, null),
//																  getDimensionReducedExpData(),
//																  refDataTimePoints,
//																  getReducedExpTimePoints(),
//																  getExpFrapStudy().getFrapData().getROILength(),
//																  eoi,
//																  measurementErrors,
//																  fixedParam,
//																  bApplyMeasurementError);
//			System.out.println("error: " + error + "   error2: " + error2);
		}
		else if((getNumEstimatedParams() + numberFixedParam) == FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF)
		{
			error = FRAPOptimizationUtils.getErrorByNewParameters_twoDiffRates(FRAPModel.REF_DIFFUSION_RATE_PARAM.getInitialGuess(),
																  newParamVals,
																  getDimensionReducedRefData(null, null),
																  getDimensionReducedExpData(),
																  refDataTimePoints,
																  getReducedExpTimePoints(),
																  getExpFrapStudy().getFrapData().getROILength(),
																  eoi,
																  measurementErrors,
																  fixedParam,
																  isApplyMeasurementError());
		}
		else
		{
			throw new Exception("Wrong parameter size in FRAP optimazition.");
		}
		//uncomment for debug
//		for(int i=0; i<newParamVals.length; i++)
//		{
//			System.out.println("Parameter "+ i + " is: " + newParamVals[i]);
//		}
//		System.out.println("squared error:" + error);
//		System.out.println("--------------------------------");
		return error;
	}

	
	
	public double[][] getFitData(Parameter[] newParams) throws Exception
	{
		double[][] result = null;
		if(newParams.length == FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF)
		{
			result = getFitData_oneDiffRate(newParams);
		}
		else if(newParams.length == FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF)
		{
			result = getFitData_twoDiffRates(newParams);
		}
		else
		{
			throw new Exception("Wrong parameter size in FRAP optimazition.");
		}
		
		return result;
	}
	
	private double[][] getFitData_oneDiffRate(Parameter[] newParams) throws Exception
	{
		double[] reducedExpTimePoints = getReducedExpTimePoints();
		int roiLen = getExpFrapStudy().getFrapData().getROILength();
		
		double[][] newData = new double[roiLen][reducedExpTimePoints.length];
		double diffRate = 0;
		double[][] diffData = null;
		double mobileFrac = 1;
		double bleachWhileMonitoringRate = 0;
		
		if(newParams != null && newParams.length > 0)
		{
			for(int i=0; i<newParams.length; i++)
			{
				if(newParams[i].getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE]))
				{
					diffRate = newParams[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess();
				}
				else if(newParams[i].getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION]))
				{
					mobileFrac = newParams[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess();
				}
				else if(newParams[i].getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE]))
				{
					bleachWhileMonitoringRate = newParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess();
				}
			}
			
			diffData = FRAPOptimizationUtils.getValueByDiffRate(FRAPModel.REF_DIFFUSION_RATE_PARAM.getInitialGuess(),
                    									   diffRate,
                    									   getDimensionReducedRefData(null, null),
                    									   refDataTimePoints,
                    									   reducedExpTimePoints,
                    									   roiLen);
			
			// get diffusion initial condition for immobile part
			double[] firstPostBleach = new double[roiLen];
			for(int i = 0; i < roiLen; i++)
			{
				firstPostBleach[i] = diffData[i][0];
			}
			
			for(int i=0; i<roiLen; i++)
			{
				for(int j=0; j<reducedExpTimePoints.length; j++)
				{
//					newData[i][j] = (mobileFrac * diffData[i][j] + imMobielFrac * firstPostBleach[i]) * Math.exp(-(bleachWhileMonitoringRate * reducedExpTimePoints[j]));
					newData[i][j] = FRAPOptimizationUtils.getValueFromParameters_oneDiffRate(diffData[i][j],
																						mobileFrac, 
																						bleachWhileMonitoringRate,
																						firstPostBleach[i],
																						reducedExpTimePoints[j]);
				}
			}
			

			//print out error
//			double error = 0;
//			double[][] expData = getDimensionReducedExpData();
//			for(int i=0; i<getExpFrapStudy().getFrapData().getRois().length; i++)
//			{
//				if(errorOfInterest != null && errorOfInterest[i])
//				{				
//					for(int j=0; j<getReducedExpTimePoints().length; j++)
//					{
//						double difference = (expData[i][j] - newData[i][j]);
//						error = error + difference * difference;
//					}
//				}
//			}
//			System.out.println("##################################");
//			System.out.println("In getFitData() the variance computed is :" + error);
			
			return newData;
		}
		else
		{
			throw new Exception("Cannot get fit data because there is no required parameters.");
		}
	}
	
	private double[][] getFitData_twoDiffRates(Parameter[] newParams) throws Exception
	{
		double[] reducedExpTimePoints = getReducedExpTimePoints();
		int roiLen = getExpFrapStudy().getFrapData().getROILength();
		
		double diffFastRate = 0;
		double mFracFast = 1;
		double diffSlowRate = 0;
		double mFracSlow = 0;
		double monitoringRate = 0;
		
		double[][] fastData = null;
		double[][] slowData = null;
		double[][] newData = new double[roiLen][reducedExpTimePoints.length];
		
		if(newParams != null && newParams.length > 0)   
		{
			for(int i=0; i<newParams.length; i++)
			{
				if(newParams[i].getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE]))
				{   
					diffFastRate = newParams[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess();
				}
				else if(newParams[i].getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION]))
				{
					mFracFast = newParams[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess();
				}
				else if(newParams[i].getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE]))
				{
					monitoringRate = newParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess();
				}
				else if(newParams[i].getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_DIFF_RATE]))
				{
					diffSlowRate = newParams[FRAPModel.INDEX_SECONDARY_DIFF_RATE].getInitialGuess();
				}
				else if(newParams[i].getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_FRACTION]))
				{
					mFracSlow = newParams[FRAPModel.INDEX_SECONDARY_FRACTION].getInitialGuess();
				}
				
			}
			
			fastData = FRAPOptimizationUtils.getValueByDiffRate(FRAPModel.REF_DIFFUSION_RATE_PARAM.getInitialGuess(),
                    									   diffFastRate,
                    									   getDimensionReducedRefData(null, null),
                    									   refDataTimePoints,
                    									   reducedExpTimePoints,
                    									   roiLen);
			
			slowData = FRAPOptimizationUtils.getValueByDiffRate(FRAPModel.REF_DIFFUSION_RATE_PARAM.getInitialGuess(),
                    									   diffSlowRate,
                    									   getDimensionReducedRefData(null, null),
                    									   refDataTimePoints,
                    									   reducedExpTimePoints,
                    									   roiLen);
			
			//get diffusion initial condition for immobile part
			double[] firstPostBleach = new double[roiLen];
			if(fastData != null)
			{
				for(int i = 0; i < roiLen; i++)
				{
					firstPostBleach[i] = fastData[i][0];
				}
			}
			
			
			for(int i=0; i<roiLen; i++)
			{
				for(int j=0; j<reducedExpTimePoints.length; j++)
				{
					newData[i][j] = FRAPOptimizationUtils.getValueFromParameters_twoDiffRates(mFracFast, 
							                                              fastData[i][j],
							                                              mFracSlow, 
							                                              slowData[i][j],
							                                              monitoringRate,
							                                              firstPostBleach[i],
							                                              reducedExpTimePoints[j]);
//					double newValue = (mFracFast * fastData[i][j] + mFracSlow * slowData[i][j] + immobileFrac * firstPostBleach[i]) * Math.exp(-(monitoringRate * expTimePoints[j]));
				}
				
			}
			
			return newData;
		}
		else
		{
			throw new Exception("Cannot perform optimization because there is no parameters to be evaluated.");
		}
	}
	
	private void setFixedParameter(Parameter arg_fixedParam)
	{
		fixedParam = arg_fixedParam;
	}
	
	public Parameter[] getBestParamters(Parameter[] inParams, boolean[] errorOfInterest) throws Exception
	{
		return getBestParamters(inParams, errorOfInterest, null, true);
	}
	
	//The best parameters will return a whole set for one diffusing component or two diffusing components (including the fixed parameter)
	public Parameter[] getBestParamters(Parameter[] inParams, boolean[] errorOfInterest, Parameter fixedParam, boolean bApplyMeasurementError) throws Exception
	{
		//refresh number of pixels in each roi 
		if(measurementErrors == null)
		{
			measurementErrors = FRAPOptimizationUtils.refreshNormalizedMeasurementError(getExpFrapStudy());
		}
		int numFixedParam = (fixedParam == null)? 0:1;
		Parameter[] outputParams = new Parameter[inParams.length + numFixedParam];//return to the caller, parameter should be a whole set including the fixed parameter
		String[] outParaNames = new String[inParams.length];//send to optimizer
		double[] outParaVals = new double[inParams.length];//send to optimizer
		//set fixed parameter
		setFixedParameter(fixedParam);
		//set if apply measurement error
		setIsApplyMeasurementError(bApplyMeasurementError);
		//optimization
		double lestError = FRAPOptimizationUtils.estimate(this, inParams, outParaNames, outParaVals, errorOfInterest);
		setLeastError(lestError);
		//get results as Parameters (take fixed parameter into account)
		for(int i = 0; i < outParaNames.length; i++)
		{
			if(outParaNames[i].equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE]))
			{
				double primaryDiffRate = outParaVals[i];
				if(primaryDiffRate > FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound())
				{
					primaryDiffRate = FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound(); 
				}
				if(primaryDiffRate < FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound())
				{
					primaryDiffRate = FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound();
				}
	
				outputParams[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = new Parameter(outParaNames[i], FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound(), FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound(), 1.0, primaryDiffRate);
			}
			else if(outParaNames[i].equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION]))
			{
				double primaryFraction = outParaVals[i];
				if(primaryFraction > FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound())
				{
					primaryFraction = FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound(); 
				}
				if(primaryFraction < FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound())
				{
					primaryFraction = FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound();
				}
	
				outputParams[FRAPModel.INDEX_PRIMARY_FRACTION] = new Parameter(outParaNames[i], FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound(), FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound(), 1.0, primaryFraction);
			}
			else if(outParaNames[i].equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE]))
			{
				double bwmRate = outParaVals[i];
				if(bwmRate > FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound())
				{
					bwmRate = FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(); 
				}
				if(bwmRate < FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound())
				{
					bwmRate = FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound();
				}
	
				outputParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE] = new Parameter(outParaNames[i], FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(), FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(), 1.0, bwmRate);
			}
			else if(outParaNames[i].equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_DIFF_RATE]))
			{
				double secDiffRate = outParaVals[i];
				if(secDiffRate > FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound())
				{
					secDiffRate = FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound(); 
				}
				if(secDiffRate < FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound())
				{
					secDiffRate = FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound();
				}
	
				outputParams[FRAPModel.INDEX_SECONDARY_DIFF_RATE] = new Parameter(outParaNames[i], FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound(), FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound(), 1.0, secDiffRate);
			}
			else if(outParaNames[i].equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_FRACTION]))
			{
				double secFraction = outParaVals[i];
				if(secFraction > FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound())
				{
					secFraction = FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound(); 
				}
				if(secFraction < FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound())
				{
					secFraction = FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound();
				}
	
				outputParams[FRAPModel.INDEX_SECONDARY_FRACTION] = new Parameter(outParaNames[i], FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound(), FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound(), 1.0, secFraction);
			}
		}
		//add fixed parameter to the best parameter output to make a whole set
		if(fixedParam != null)
		{
			if(fixedParam.getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE]))
			{
				outputParams[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = fixedParam;
			}
			else if(fixedParam.getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION]))
			{
				outputParams[FRAPModel.INDEX_PRIMARY_FRACTION] = fixedParam;
			}
			else if(fixedParam.getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE]))
			{
				outputParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE] = fixedParam;
			}
			else if(fixedParam.getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_DIFF_RATE]))
			{
				outputParams[FRAPModel.INDEX_SECONDARY_DIFF_RATE] = fixedParam;
			}
			else if(fixedParam.getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_FRACTION]))
			{
				outputParams[FRAPModel.INDEX_SECONDARY_FRACTION] = fixedParam;
			}
		}
		//for debug purpose
//		for(int i = 0; i < outParams.length; i++)
//		{
//			System.out.println("Estimate result for "+outParams[i].getName()+ " is: "+outParams[i].getInitialGuess());
//		}
		return outputParams;
	}
	
	public boolean isApplyMeasurementError() {
		return bApplyMeasurementError;
	}

	public void setIsApplyMeasurementError(boolean bApplyMeasurementError) {
		this.bApplyMeasurementError = bApplyMeasurementError;
	}
	
	public int getNumEstimatedParams() {
		return numEstimatedParams;
	}

	public void setNumEstimatedParams(int numEstimatedParams) {
		this.numEstimatedParams = numEstimatedParams;
	}

	public double getLeastError() {
		return leastError;
	}

	public void setLeastError(double leastError) {
		this.leastError = leastError;
	}
	
	public LocalWorkspace getLocalWorkspace() {
		return localWorkspace;
	}
	
	public FRAPStudy getExpFrapStudy() 
	{
		return expFrapStudy;
	}
	
	public static void main(String[] args) 
	{
		String workingDir = "C:\\";
				
		LocalWorkspace localWorkspace = new LocalWorkspace(new File(workingDir));
		String xmlString;
		try {
			//read original data from file
			System.out.println("start loading original data....");
			String expFileName = "C:/VirtualMicroscopy/testFastD_4_fastFrac_0p5_slowD_1_slowFrac_0p3_ImmobileFrac_0p2_mwb_0p0015_new.vfrap";
			xmlString = XmlUtil.getXMLString(expFileName);
			MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
			FRAPStudy expFrapStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null).getRootElement(), null);
			expFrapStudy.setXmlFilename(expFileName);
			System.out.println("experimental data time points"+expFrapStudy.getFrapData().getImageDataset().getSizeT());
			System.out.println("finish loading original data....");
			
			//create reference data
			System.out.println("creating rederence data....");
			
			FRAPOptData optData = new FRAPOptData(expFrapStudy, 5, localWorkspace, new ClientTaskStatusSupport(){
				
				public void setProgress(int progress) {
					// TODO Auto-generated method stub
					
				}
				
				public void setMessage(String message) {
					// TODO Auto-generated method stub
					
				}
				
				public boolean isInterrupted() {
					// TODO Auto-generated method stub
					return false;
				}
				
				public int getProgress() {
					// TODO Auto-generated method stub
					return 0;
				}

				public void addProgressDialogListener(ProgressDialogListener progressDialogListener) {
					throw new RuntimeException("not yet implemented");					
				}
			});
				
			//trying 3 parameters
//			Parameter diff = new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.DIFFUSION_RATE_INDEX], 0, 100, 1.0, Double.parseDouble(expFrapStudy.getFrapModelParameters().diffusionRate));
//			Parameter mobileFrac = new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.MOBILE_FRACTION_INDEX], 0, 1, 1.0, 1/*Double.parseDouble(expFrapStudy.getFrapModelParameters().mobileFraction)*/);
//			Parameter bleachWhileMonitoringRate = new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.BLEACH_WHILE_MONITOR_INDEX], 0, 10, 1.0,  0/*Double.parseDouble(expFrapStudy.getFrapModelParameters().monitorBleachRate)*/);
			
			//trying 5 parameters
			Parameter diffFastOffset = new cbit.vcell.opt.Parameter("fastDiffOffset", 0, 50, 1.0, 10);
			Parameter mFracFast = new cbit.vcell.opt.Parameter("fastMobileFrac", 0, 1, 1.0, 0.5);
			Parameter diffSlow = new cbit.vcell.opt.Parameter("slowDiffRate", 0, 10, 1.0, 0.1);
			Parameter mFracSlow = new cbit.vcell.opt.Parameter("slowMobileFrac", 0, 1, 1.0, 0.5);
			Parameter bleachWhileMonitoringRate = new cbit.vcell.opt.Parameter("bleachWhileMonitoringRate", 0, 20, 1.0, 0.1);
			Parameter[] inParams = new Parameter[]{diffFastOffset, mFracFast, diffSlow, mFracSlow, bleachWhileMonitoringRate};
						
//			Parameter diffFast = new cbit.vcell.opt.Parameter("fast_diff_rate", 0, 100, 1.0, 5);
//			Parameter mFracFast = new cbit.vcell.opt.Parameter("fast_mobile_fraction", 0, 1, 1.0, 0.5);
//			Parameter diffSlowFactor = new cbit.vcell.opt.Parameter("slow_diff_factor", 0, 1, 1.0, 0.2);
//			Parameter mFracSlow = new cbit.vcell.opt.Parameter("slow_mobile_fraction", 0, 1, 1.0, 0.5);
//			Parameter bleachWhileMonitoringRate = new cbit.vcell.opt.Parameter("bleach_while_monitoring_rate", 0, 1, 1.0,  0.1);
//			Parameter[] inParams = new Parameter[]{diffFast, mFracFast, diffSlowFactor, mFracSlow, bleachWhileMonitoringRate};
			
			//add some constraints, like fast mobile fraction + slow mobile fraction should smaller than 1.
			//slower rate should be 5 times smaller than fast rate.....??? 5*slowDiffRate < fastDiffRate
//			Expression exp1 = new Expression("("+mFracFast.getName()+" + "+mFracSlow.getName()+" - 1)");
//			Constraint con1 = new Constraint(ConstraintType.LinearInequality, exp1);
//			Expression exp2 = new Expression("( 4 * "+diffSlow.getName()+" - "+ diffFastOffset.getName() +")");
//			Constraint con2 = new Constraint(ConstraintType.LinearInequality, exp2);
//			Constraint[] cons = new Constraint[2];
//			cons[0] = con1;
//			cons[1] = con2;
			
			ROI[] rois = expFrapStudy.getFrapData().getRois();
			boolean[] errorOfInterest = new boolean[rois.length];
			for(int i=0; i<rois.length; i++)
			{
				if(/*!rois[i].getROIType().equals(RoiType.ROI_BLEACHED)*/rois[i].getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()) || rois[i].getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()))
				{
					errorOfInterest[i] = false;
				}
				else
				{
					errorOfInterest[i] = true;
				}
			}
			optData.getBestParamters(inParams, errorOfInterest);
			
//			double[][] result = optData.getFitData(inParams, errorOfInterest); // double[roiLen][timePoints]
//			int indexROI = -1;
//			for(int j = 0; j < expFrapStudy.getFrapData().getRois().length; j++)
//			{
//				if(expFrapStudy.getFrapData().getRois()[j].getROIType().equals(RoiType.ROI_BLEACHED))
//				{
//					indexROI = j;
//					break;
//				}
//			}
//			int index = Integer.parseInt(expFrapStudy.getFrapModelParameters().startIndexForRecovery);
//			for(int i = 0; i < (expFrapStudy.getFrapData().getImageDataset().getImageTimeStamps().length - index); i++)
//			{
//				System.out.println(expFrapStudy.getFrapData().getImageDataset().getImageTimeStamps()[i+index]+"\t"+result[indexROI][i]);
//			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	public ProfileData[] evaluateParameters(Parameter[] currentParams, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception
	{
//		long startTime =System.currentTimeMillis();
		int totalParamLen = currentParams.length;
		ProfileData[] resultData = new ProfileData[totalParamLen];
		FRAPStudy frapStudy = getExpFrapStudy();
		for(int j=0; j<totalParamLen; j++)
		{
			ProfileData profileData = new ProfileData();
			//add the fixed parameter to profileData, output exp data and opt results
			setNumEstimatedParams(totalParamLen);
			Parameter[] newBestParameters = getBestParamters(currentParams, frapStudy.getSelectedROIsForErrorCalculation(), null, true);
			double iniError = getLeastError();
			//fixed parameter
			Parameter fixedParam = newBestParameters[j];
			if(fixedParam.getInitialGuess() == 0)//log function cannot take 0 as parameter
			{
				fixedParam = new Parameter (fixedParam.getName(),
	                        fixedParam.getLowerBound(),
	                        fixedParam.getUpperBound(),
	                        fixedParam.getScale(),
	                        FRAPOptimizationUtils.epsilon);
			}
			if(clientTaskStatusSupport != null)
			{
				if(totalParamLen == FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF)
				{
					clientTaskStatusSupport.setMessage("<html>Evaluating confidence intervals of \'" + fixedParam.getName() + "\' <br> of diffusion with one diffusing component model.</html>");
				}
				else if(totalParamLen == FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF)
				{
					clientTaskStatusSupport.setMessage("<html>Evaluating confidence intervals of \'" + fixedParam.getName() + "\' <br> of diffusion with two diffusing components model.</html>");
				}
				clientTaskStatusSupport.setProgress(0);//start evaluation of a parameter.
			}
			ProfileDataElement pde = new ProfileDataElement(fixedParam.getName(), Math.log10(fixedParam.getInitialGuess()), iniError, newBestParameters);
			profileData.addElement(pde);
			
			Parameter[] unFixedParams = new Parameter[totalParamLen - 1];
			int indexCounter = 0;
			for(int i=0; i<totalParamLen; i++)
			{
				if(!newBestParameters[i].getName().equals(fixedParam.getName()))
				{
					unFixedParams[indexCounter] = newBestParameters[i];
					indexCounter++;
				}
				else continue;
			}
			//increase
			int iterationCount = 1;
			double paramLogVal = Math.log10(fixedParam.getInitialGuess());
			double lastError = iniError;
			boolean isBoundReached = false;
			double incrementStep = DEFAULT_CI_STEPS[j];
			int stepIncreaseCount = 0;
			while(true)
			{
				if(iterationCount > MAX_ITERATION)//if exceeds the maximum iterations, break;
				{
					break;
				}
				if(isBoundReached)
				{
					break;
				}
				paramLogVal = paramLogVal + incrementStep ;
				double paramVal = Math.pow(10,paramLogVal);
				if(paramVal > (fixedParam.getUpperBound() - FRAPOptimizationUtils.epsilon))
				{
					paramVal = fixedParam.getUpperBound();
					paramLogVal = Math.log10(fixedParam.getUpperBound());
					isBoundReached = true;
				}
				Parameter increasedParam = new Parameter (fixedParam.getName(),
	                                                      fixedParam.getLowerBound(),
	                                                      fixedParam.getUpperBound(),
	                                                      fixedParam.getScale(),
	                                                      paramVal);
				//getBestParameters returns the whole set of parameters including the fixed parameters
				setNumEstimatedParams(totalParamLen-1);
				Parameter[] newParameters = getBestParamters(unFixedParams, frapStudy.getSelectedROIsForErrorCalculation(), increasedParam, true);
				for(int i=0; i < newParameters.length; i++)//use last step unfixed parameter values to optimize
				{
					for(int k=0; k<unFixedParams.length; k++)
					{
						if(newParameters[i].getName().equals(unFixedParams[k].getName()))
						{
							Parameter tempParameter = new Parameter(unFixedParams[k].getName(),
																	unFixedParams[k].getLowerBound(),
																	unFixedParams[k].getUpperBound(),
																	unFixedParams[k].getScale(),
									                                newParameters[i].getInitialGuess());
							unFixedParams[k] = tempParameter;
						}
					}
				}
				double error = getLeastError();
				pde = new ProfileDataElement(increasedParam.getName(), paramLogVal, error, newParameters);
				profileData.addElement(pde);
				//check if the we run enough to get confidence intervals(99% @6.635, we plus 10 over the min error)
				if(error > (iniError+10))
				{
					break;
				}
				if(Math.abs((error-lastError)/lastError) < MIN_LIKELIHOOD_CHANGE)
				{
					stepIncreaseCount ++;
					incrementStep = DEFAULT_CI_STEPS[j] * Math.pow(2, stepIncreaseCount);
				}
				else
				{
					if(stepIncreaseCount > 1)
					{
						incrementStep = DEFAULT_CI_STEPS[j] / Math.pow(2, stepIncreaseCount);
						stepIncreaseCount --;
					}
				}
				//use first derivative
//				double yPrime = Math.abs((error-lastError)/(paramLogVal - lastLogVal));
//				if(yPrime < (0.1763+FRAPOptimization.epsilon) /*< 10 degree angle*/|| yPrime > (56.9168 + FRAPOptimization.epsilon) /*>89 degree angle*/)
//				{
//					stepIncreaseCount ++;
//					incrementStep = DEFAULT_CI_STEPS[j] * Math.pow(2, stepIncreaseCount);
//				}
//				else
//				{
//					stepIncreaseCount = 0;
//					incrementStep = DEFAULT_CI_STEPS[j];
//				}
				
				if (clientTaskStatusSupport.isInterrupted())
				{
					throw UserCancelException.CANCEL_GENERIC;
				}

				lastError = error;
				iterationCount++;
				clientTaskStatusSupport.setProgress((int)((iterationCount*1.0/MAX_ITERATION) * 0.5 * 100));
			}
			clientTaskStatusSupport.setProgress(50);//half way through evaluation of a parameter.
			//decrease
			iterationCount = 1;
			paramLogVal = Math.log10(fixedParam.getInitialGuess());;
			;
			lastError = iniError;
			isBoundReached = false;
			double decrementStep = DEFAULT_CI_STEPS[j];
			stepIncreaseCount = 0;
			while(true)
			{
				if(iterationCount > MAX_ITERATION)//if exceeds the maximum iterations, break;
				{
					break;
				}
				if(isBoundReached)
				{
					break;
				}
				paramLogVal = paramLogVal - decrementStep;
				double paramVal = Math.pow(10,paramLogVal);
				if(paramVal < (fixedParam.getLowerBound() + FRAPOptimizationUtils.epsilon))
				{
					paramVal = FRAPOptimizationUtils.epsilon;
					paramLogVal = Math.log10(FRAPOptimizationUtils.epsilon);
					isBoundReached = true;
				}
				Parameter decreasedParam = new Parameter (fixedParam.getName(),
	                                            fixedParam.getLowerBound(),
	                                            fixedParam.getUpperBound(),
	                                            fixedParam.getScale(),
	                                            paramVal);
				//getBestParameters returns the whole set of parameters including the fixed parameters
				setNumEstimatedParams(totalParamLen-1);
				Parameter[] newParameters = getBestParamters(unFixedParams, frapStudy.getSelectedROIsForErrorCalculation(), decreasedParam, true);
				for(int i=0; i < newParameters.length; i++)//use last step unfixed parameter values to optimize
				{
					for(int k=0; k<unFixedParams.length; k++)
					{
						if(newParameters[i].getName().equals(unFixedParams[k].getName()))
						{
							Parameter tempParameter = new Parameter(unFixedParams[k].getName(),
																	unFixedParams[k].getLowerBound(),
																	unFixedParams[k].getUpperBound(),
																	unFixedParams[k].getScale(),
									                                newParameters[i].getInitialGuess());
							unFixedParams[k] = tempParameter;
						}
					}
				}
				double error = getLeastError();
				pde = new ProfileDataElement(decreasedParam.getName(), paramLogVal, error, newParameters);
				profileData.addElement(0,pde);
				if(error > (iniError+10))
				{
					break;
				}
				if(Math.abs((error-lastError)/lastError) < MIN_LIKELIHOOD_CHANGE)
				{
					stepIncreaseCount ++;
					decrementStep = DEFAULT_CI_STEPS[j] * Math.pow(2, stepIncreaseCount);
				}
				else
				{
					if(stepIncreaseCount > 1)
					{
						incrementStep = DEFAULT_CI_STEPS[j] / Math.pow(2, stepIncreaseCount);
						stepIncreaseCount --;
					}
				}
				// use first derivative
//				double yPrime = Math.abs((error-lastError)/(paramLogVal -lastLogVal));
//				if(yPrime < (0.0875 + FRAPOptimization.epsilon)/*5 degree angle*/ || yPrime > (56.9168 + FRAPOptimization.epsilon)/*89 degree angle*/ )
//				{
//					stepIncreaseCount++;
//					decrementStep = DEFAULT_CI_STEPS[j] * Math.pow(2, stepIncreaseCount);
//				}
//				else
//				{
//					stepIncreaseCount = 0;
//					decrementStep = DEFAULT_CI_STEPS[j];
//				}
				
				if (clientTaskStatusSupport.isInterrupted())
				{
					throw UserCancelException.CANCEL_GENERIC;
				}
				lastError = error;
				iterationCount++;
				clientTaskStatusSupport.setProgress((int)(((iterationCount+MAX_ITERATION)*1.0/MAX_ITERATION) * 0.5 * 100));
			}
			resultData[j] = profileData;
			clientTaskStatusSupport.setProgress(100);//finish evaluation of a parameter
		}
		//this message is specifically set for batchrun, the message will stay in the status panel. It doesn't affect single run,which disappears quickly that user won't notice.
		clientTaskStatusSupport.setMessage("Evaluating confidence intervals ...");
//		long endTime =System.currentTimeMillis();
//		System.out.println("total time used:" + (endTime - startTime));
		return resultData;
	}
	
	public void getFromStoredRefData(SimpleReferenceData argSimRefData)
	{
		int roiLen = getExpFrapStudy().getFrapData().getROILength();
		if(argSimRefData.getNumDataColumns() == (1+roiLen))
		{
			//t is in the first column of simpleReferenceData
			refDataTimePoints = argSimRefData.getDataByColumn(0);
			double[][] result = new double[roiLen][];
			for(int i=0; i<roiLen; i++)
			{
				result[i]=argSimRefData.getDataByColumn(i+1); //first column is t, so the roi data starts from second column
			}
			dimensionReducedRefData = result;
		}
	}
}

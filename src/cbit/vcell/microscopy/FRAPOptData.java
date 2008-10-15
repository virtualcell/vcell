package cbit.vcell.microscopy;

import java.awt.Rectangle;
import java.io.File;

import cbit.sql.KeyValue;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.client.server.VCDataManager;
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.microscopy.ROI.RoiType;
import cbit.vcell.opt.Parameter;
import cbit.vcell.server.StdoutSessionLog;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

public class FRAPOptData {
	public static String[] ONEDIFFRATE_PARAMETER_NAMES = new String[]{"diffRate",
														  "mobileFrac",
														  "bleachWhileMonitoringRate"};
	public static final int ONEDIFFRATE_DIFFUSION_RATE_INDEX = 0;
	public static final int ONEDIFFRATE_MOBILE_FRACTION_INDEX = 1;
	public static final int ONEDIFFRATE_BLEACH_WHILE_MONITOR_INDEX = 2;
	
	public static String[] TWODIFFRATES_PARAMETER_NAMES = new String[]{"fastDiffRate",
		  															   "fastMobileFrac",
		  															   "bleachWhileMonitoringRate",
		  															   "slowDiffRate",
		  															   "slowMobileFrac"};
	public static final int TWODIFFRATES_FAST_DIFFUSION_RATE_INDEX = 0;
	public static final int TWODIFFRATES_FAST_MOBILE_FRACTION_INDEX = 1;
	public static final int TWODIFFRATES_BLEACH_WHILE_MONITOR_INDEX = 2;
	public static final int TWODIFFRATES_SLOW_DIFFUSION_RATE_INDEX = 3;
	public static final int TWODIFFRATES_SLOW_MOBILE_FRACTION_INDEX = 4;
		
	public static final int NUM_PARAMS_FOR_ONE_DIFFUSION_RATE = 3;//diffusion rate, mobile fraction, bleach while monitoring rate
	public static final int NUM_PARAMS_FOR_TWO_DIFFUSION_RATE = 5;//fast diff rate, fast mobile fraction, slow diff rate, slow mobile fraction, bleach while monitoring rate
	
	/*----------------reference data by diffusion rate=1, mobileFrac=1 and bwmRate = 0-------------------*/
	public static final Parameter REF_DIFFUSION_RATE_PARAM =
		new cbit.vcell.opt.Parameter(FRAPOptData.ONEDIFFRATE_PARAMETER_NAMES[FRAPOptData.ONEDIFFRATE_DIFFUSION_RATE_INDEX], 0, 40, 1.0, 1.0);
	public static final Parameter REF_MOBILE_FRACTION_PARAM =
		new cbit.vcell.opt.Parameter(FRAPOptData.ONEDIFFRATE_PARAMETER_NAMES[FRAPOptData.ONEDIFFRATE_MOBILE_FRACTION_INDEX], 0, 1, 1.0, 1.0);
	public static final Parameter REF_BLEACH_WHILE_MONITOR_PARAM =
		new cbit.vcell.opt.Parameter(FRAPOptData.ONEDIFFRATE_PARAMETER_NAMES[FRAPOptData.ONEDIFFRATE_BLEACH_WHILE_MONITOR_INDEX], 0, 20, 1.0,  0);
	public static final Parameter REF_SECOND_DIFFUSION_RATE_PARAM =
		new cbit.vcell.opt.Parameter(FRAPOptData.TWODIFFRATES_PARAMETER_NAMES[FRAPOptData.TWODIFFRATES_SLOW_DIFFUSION_RATE_INDEX], 0, 20, 1.0, 1.0);
	public static final Parameter REF_SECOND_MOBILE_FRACTION_PARAM =
		new cbit.vcell.opt.Parameter(FRAPOptData.TWODIFFRATES_PARAMETER_NAMES[FRAPOptData.TWODIFFRATES_SLOW_MOBILE_FRACTION_INDEX], 0, 1, 1.0, 1.0);
	
	
	
	private static int maxRefSavePoints = 500;
	private static int startingTime = 0;
	
	private Boolean bRunRefSim = null;
	
	private FRAPStudy expFrapStudy = null;
	private LocalWorkspace localWorkspace = null;
	private TimeBounds refTimeBounds = null;
	private TimeStep refTimeStep = null;
	private DefaultOutputTimeSpec  refTimeSpec = null;
	private int numEstimatedParams = 0;
	private double[][] dimensionReducedRefData = null;
	private double[] refDataTimePoints = null;
	private double[][] dimensionReducedExpData = null;
	private double[] reducedExpTimePoints = null;
	
	
	public FRAPOptData(FRAPStudy argExpFrapStudy, int numberOfEstimatedParams, LocalWorkspace argLocalWorkSpace,
			DataSetControllerImpl.ProgressListener progressListener, boolean bRefSim) throws Exception
	{
		expFrapStudy = argExpFrapStudy;
		setNumEstimatedParams(numberOfEstimatedParams);
		localWorkspace = argLocalWorkSpace;
		bRunRefSim = new Boolean(bRefSim); 
		if(progressListener != null){
			progressListener.updateMessage("Getting experimental data ROI averages...");
		}
		dimensionReducedExpData = getDimensionReducedExpData();
		dimensionReducedRefData = getDimensionReducedRefData(progressListener);
	}
	
	public TimeBounds getRefTimeBounds()
	{
		if(refTimeBounds == null)
		{
			//estimated t = ( bleach area max width^2 /(4*D)) * ln(1/delta), use bleah area width as length.
			ROI bleachedROI = getExpFrapStudy().getFrapData().getRoi(RoiType.ROI_BLEACHED);
			
			Rectangle bleachRect = bleachedROI.getRoiImages()[0].getNonzeroBoundingBox();
			double width = ((double)bleachRect.width/getExpFrapStudy().getFrapData().getImageDataset().getISize().getX()) * 
			               getExpFrapStudy().getFrapData().getImageDataset().getExtent().getX();
			double height = ((double)bleachRect.height/getExpFrapStudy().getFrapData().getImageDataset().getISize().getY()) * 
            			   getExpFrapStudy().getFrapData().getImageDataset().getExtent().getY();
			
			double bleachWidth = Math.max(width, height);
			final double  unrecovery_threshold = .01;
			double refEndingTime = (bleachWidth * bleachWidth/(4*REF_DIFFUSION_RATE_PARAM.getInitialGuess())) * Math.log(1/unrecovery_threshold);
			
			refTimeBounds = new TimeBounds(FRAPOptData.startingTime, refEndingTime);
		}
		return refTimeBounds;
	}
	
	public TimeStep getRefTimeStep()
	{
		//time step is estimated as deltaX^2/(4*D)
		if(refTimeStep == null)
		{
			int numX = getExpFrapStudy().getFrapData().getImageDataset().getAllImages()[0].getNumX();
			double deltaX = getExpFrapStudy().getFrapData().getImageDataset().getAllImages()[0].getExtent().getX()/(numX-1);
			double timeStep = (deltaX*deltaX /REF_DIFFUSION_RATE_PARAM.getInitialGuess()) * 0.25;
			refTimeStep = new TimeStep(timeStep, timeStep, timeStep);
		}
		return refTimeStep;
	}
	
	public DefaultOutputTimeSpec getRefTimeSpec()
	{
		if(refTimeSpec == null)
		{
			int numSavePoints = (int)Math.ceil((getRefTimeBounds().getEndingTime() - getRefTimeBounds().getStartingTime())/getRefTimeStep().getDefaultTimeStep());
			if(numSavePoints <= FRAPOptData.maxRefSavePoints)
			{
				refTimeSpec = new DefaultOutputTimeSpec(1, FRAPOptData.maxRefSavePoints);
			}
			else
			{
				int keepEvery = (int)Math.ceil(numSavePoints/FRAPOptData.maxRefSavePoints);
				refTimeSpec = new DefaultOutputTimeSpec(keepEvery, FRAPOptData.maxRefSavePoints);
			}
			
		}
		return refTimeSpec;
	}
	
	public double[][] getDimensionReducedRefData(DataSetControllerImpl.ProgressListener progressListener) throws Exception
	{
		if(dimensionReducedRefData == null){
			refreshDimensionReducedRefData(progressListener);
		}
		return dimensionReducedRefData;
	}
	
	public double[][] getDimensionReducedExpData() throws Exception
	{
		if(dimensionReducedExpData == null){
			//normalize the experimental data, because the reference data is normalized
			VCDataManager vcManager = getLocalWorkspace().getVCDataManager();
			double[] prebleachAvg = vcManager.getSimDataBlock(getExpFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(), "prebleach_avg", 0).getData();
			int startRecoveryIndex = Integer.parseInt(getExpFrapStudy().getFrapModelParameters().startIndexForRecovery);
			dimensionReducedExpData = FRAPOptimization.dataReduction(getExpFrapStudy().getFrapData(),startRecoveryIndex, getExpFrapStudy().getFrapData().getRois(), prebleachAvg);
		}
		return dimensionReducedExpData;
	}
	
	public double[] getReducedExpTimePoints() {
		if(reducedExpTimePoints == null)
		{
			int startRecoveryIndex = Integer.parseInt(getExpFrapStudy().getFrapModelParameters().startIndexForRecovery);
			reducedExpTimePoints = FRAPOptimization.timeReduction(getExpFrapStudy().getFrapData().getImageDataset().getImageTimeStamps(), startRecoveryIndex); 
		}
		return reducedExpTimePoints;
	}
	
	public void refreshDimensionReducedRefData(final DataSetControllerImpl.ProgressListener progressListener) throws Exception
	{
		final double RUNSIM_PROGRESS_FRACTION = .5;
		DataSetControllerImpl.ProgressListener runSimProgressListener =
			new DataSetControllerImpl.ProgressListener(){
				public void updateProgress(double progress) {
					if(progressListener != null){
						progressListener.updateProgress(progress*RUNSIM_PROGRESS_FRACTION);
					}
				}
				public void updateMessage(String message){
					if(progressListener != null){
						progressListener.updateMessage(message);
					}
				}
		};
		System.out.println("run simulation...");
		KeyValue referenceSimKeyValue = null;
		if(isRunRefSim().booleanValue())
		{
			referenceSimKeyValue = runRefSimulation(runSimProgressListener);
		}
		else
		{
			referenceSimKeyValue = getExpFrapStudy().getRefExternalDataInfo().getExternalDataIdentifier().getKey();
		}
		//loading data only if the reference data is null or a new reference simulation is done
		if(isLoadRefDataNeeded(isRunRefSim().booleanValue()))
		{
			VCSimulationIdentifier vcSimID =
				new VCSimulationIdentifier(referenceSimKeyValue,LocalWorkspace.getDefaultOwner());
			VCSimulationDataIdentifier vcSimDataID =
				new VCSimulationDataIdentifier(vcSimID,FieldDataFileOperationSpec.JOBINDEX_DEFAULT);
			refDataTimePoints = getLocalWorkspace().getVCDataManager().getDataSetTimes(vcSimDataID);
			System.out.println("simulation done...");
			int startRecoveryIndex = Integer.parseInt(getExpFrapStudy().getFrapModelParameters().startIndexForRecovery);
			
			DataSetControllerImpl.ProgressListener reducedRefDataProgressListener =
				new DataSetControllerImpl.ProgressListener(){
					public void updateProgress(double progress) {
						if(progressListener != null){
							progressListener.updateProgress(.5+progress*(1-RUNSIM_PROGRESS_FRACTION));
						}
					}
					public void updateMessage(String message){
						if(progressListener != null){
							progressListener.updateMessage(message);
						}
					}
			};
			dimensionReducedRefData =
				FRAPOptimization.dataReduction(getLocalWorkspace().getVCDataManager(),vcSimDataID,
						startRecoveryIndex, getExpFrapStudy().getFrapData().getRois(),reducedRefDataProgressListener);
			System.out.println("generating dimension reduced ref data, done ....");
		}
	}
	
	public boolean isLoadRefDataNeeded(boolean isRunRefSim)
	{
		if(dimensionReducedRefData == null || isRunRefSim)
		{
			return true;
		}
		return false;
	}
	
	public KeyValue runRefSimulation(final DataSetControllerImpl.ProgressListener progressListener) throws Exception
	{
		BioModel bioModel = null;
		if(progressListener != null){
			progressListener.updateMessage("Running Reference Simulation...");
		}
		try{
			ExternalDataInfo oldRefDataInfo = getExpFrapStudy().getRefExternalDataInfo();
			ExternalDataInfo refDataInfo = FRAPStudy.createNewExternalDataInfo(getLocalWorkspace(),FRAPStudy.REF_EXTDATA_NAME);
			
			bioModel =
				FRAPStudy.createNewBioModel(
					expFrapStudy,
					new Double(REF_DIFFUSION_RATE_PARAM.getInitialGuess()),
					REF_BLEACH_WHILE_MONITOR_PARAM.getInitialGuess()+"",
					REF_MOBILE_FRACTION_PARAM.getInitialGuess()+"",
					null,
					null,
					refDataInfo.getExternalDataIdentifier().getKey(),
					LocalWorkspace.getDefaultOwner(),
					new Integer(expFrapStudy.getFrapModelParameters().startIndexForRecovery));
			
			//change time bound and time step
			Simulation sim = bioModel.getSimulations()[0];
			sim.getSolverTaskDescription().setTimeBounds(getRefTimeBounds());
			sim.getSolverTaskDescription().setTimeStep(getRefTimeStep());
			sim.getSolverTaskDescription().setOutputTimeSpec(getRefTimeSpec());
			
	//		System.out.println("run FRAP Reference Simulation...");
			final double RUN_REFSIM_PROGRESS_FRACTION = 1.0;
			DataSetControllerImpl.ProgressListener runRefSimProgressListener =
				new DataSetControllerImpl.ProgressListener(){
					public void updateProgress(double progress) {
						if(progressListener != null){
							progressListener.updateProgress(progress*RUN_REFSIM_PROGRESS_FRACTION);
						}
					}
					public void updateMessage(String message){
						if(progressListener != null){
							progressListener.updateMessage(message);
						}
					}
			};
	
			//run simulation
			FRAPStudy.runFVSolverStandalone(
				new File(getLocalWorkspace().getDefaultSimDataDirectory()),
				new StdoutSessionLog(LocalWorkspace.getDefaultOwner().getName()),
				bioModel.getSimulations(0),
				getExpFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),
				getExpFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),
				runRefSimProgressListener);
			
			//if reference simulation completes successfully, we save reference data info and remove old simulation files.
			getExpFrapStudy().setRefExternalDataInfo(refDataInfo);
			//we have to save again here, because if user doesn't press "save button" the reference simulation external info won't be saved.
			MicroscopyXmlproducer.writeXMLFile(getExpFrapStudy(), new File(getExpFrapStudy().getXmlFilename()), true, null, VirtualFrapMainFrame.SAVE_COMPRESSED);
			if(oldRefDataInfo != null && oldRefDataInfo.getExternalDataIdentifier() != null)
			{
				FRAPStudy.removeExternalDataAndSimulationFiles(oldRefDataInfo.getExternalDataIdentifier().getKey(), null, null, getLocalWorkspace());
			}
			
			return sim.getVersion().getVersionKey();
		}catch(Exception e){
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
		if(getNumEstimatedParams() == FRAPOptData.NUM_PARAMS_FOR_ONE_DIFFUSION_RATE)
		{
			error = FRAPOptimization.getErrorByNewParameters_oneDiffRate(REF_DIFFUSION_RATE_PARAM.getInitialGuess(), 
					                                              newParamVals,
					                                              getDimensionReducedRefData(null),
					                                              getDimensionReducedExpData(),
					                                              refDataTimePoints,
					                                              getReducedExpTimePoints(),
					                                              getExpFrapStudy().getFrapData().getRois().length, 
					                                              (getRefTimeStep().getDefaultTimeStep() * getRefTimeSpec().getKeepEvery()),
					                                              eoi);
		}
		else if(getNumEstimatedParams() == FRAPOptData.NUM_PARAMS_FOR_TWO_DIFFUSION_RATE)
		{
			error = FRAPOptimization.getErrorByNewParameters_twoDiffRates(REF_DIFFUSION_RATE_PARAM.getInitialGuess(),
																  newParamVals,
																  getDimensionReducedRefData(null),
																  getDimensionReducedExpData(),
																  refDataTimePoints,
																  getReducedExpTimePoints(),
																  getExpFrapStudy().getFrapData().getRois().length,
																  (getRefTimeStep().getDefaultTimeStep() * getRefTimeSpec().getKeepEvery()),
																  eoi);
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
//		System.out.println("Variance:" + error);
//		System.out.println("--------------------------------");
		return error;
	}

	public double[][] getFitData(Parameter[] newParams, boolean[] errorOfInterest) throws Exception
	{
		double[][] result = null;
		if(getNumEstimatedParams() == newParams.length)
		{
			if(getNumEstimatedParams() == FRAPOptData.NUM_PARAMS_FOR_ONE_DIFFUSION_RATE)
			{
				result = getFitData_oneDiffRate(newParams, errorOfInterest);
			}
			else if(getNumEstimatedParams() == FRAPOptData.NUM_PARAMS_FOR_TWO_DIFFUSION_RATE)
			{
				result = getFitData_twoDiffRates(newParams, errorOfInterest);
			}
			else
			{
				throw new Exception("Wrong parameter size in FRAP optimazition.");
			}
		}
		else
		{
			throw new Exception("Input parameter size is different from the parameter size setting in Optimization Data.");
		}
		return result;
	}
	
	private double[][] getFitData_oneDiffRate(Parameter[] newParams, boolean[] errorOfInterest) throws Exception
	{
		double[][] reducedExpData = getDimensionReducedExpData();
		double[] reducedExpTimePoints = getReducedExpTimePoints();
		int roiLen = getExpFrapStudy().getFrapData().getRois().length;
		double refTimeInterval = (getRefTimeStep().getDefaultTimeStep() * getRefTimeSpec().getKeepEvery());
		
		double[][] newData = new double[roiLen][reducedExpTimePoints.length];
		double diffRate = 0;
		double[][] diffData = null;
		double mobileFrac = 1;
		double bleachWhileMonitoringRate = 0;
		
		if(newParams != null && newParams.length > 0)
		{
			for(int i=0; i<newParams.length; i++)
			{
				if(newParams[i].getName().equals(FRAPOptData.ONEDIFFRATE_PARAMETER_NAMES[FRAPOptData.ONEDIFFRATE_DIFFUSION_RATE_INDEX]))
				{
					diffRate = newParams[FRAPOptData.ONEDIFFRATE_DIFFUSION_RATE_INDEX].getInitialGuess();
				}
				else if(newParams[i].getName().equals(FRAPOptData.ONEDIFFRATE_PARAMETER_NAMES[FRAPOptData.ONEDIFFRATE_MOBILE_FRACTION_INDEX]))
				{
					mobileFrac = newParams[FRAPOptData.ONEDIFFRATE_MOBILE_FRACTION_INDEX].getInitialGuess();
				}
				else if(newParams[i].getName().equals(FRAPOptData.ONEDIFFRATE_PARAMETER_NAMES[FRAPOptData.ONEDIFFRATE_BLEACH_WHILE_MONITOR_INDEX]))
				{
					bleachWhileMonitoringRate = newParams[FRAPOptData.ONEDIFFRATE_BLEACH_WHILE_MONITOR_INDEX].getInitialGuess();
				}
			}
			
			diffData = FRAPOptimization.getValueByDiffRate(REF_DIFFUSION_RATE_PARAM.getInitialGuess(),
                    									   diffRate,
                    									   getDimensionReducedRefData(null),
                    									   reducedExpData,
                    									   refDataTimePoints,
                    									   reducedExpTimePoints,
                    									   roiLen,
                    									   refTimeInterval);
			
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
					newData[i][j] = FRAPOptimization.getValueFromParameters_oneDiffRate(diffData[i][j],
																						mobileFrac, 
																						bleachWhileMonitoringRate,
																						firstPostBleach[i],
																						reducedExpTimePoints[j]);
				}
			}
			
			//REORder according to roiTypes
			double[][] fitDataInROITypeOrder = new double[newData.length][];
			for (int i = 0; i < getExpFrapStudy().getFrapData().getRois().length; i++) {
				for (int j = 0; j < ROI.RoiType.values().length; j++) {
					if(getExpFrapStudy().getFrapData().getRois()[i].getROIType().equals(ROI.RoiType.values()[j])){
						fitDataInROITypeOrder[j] = newData[i];
						break;
					}
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
			
			return fitDataInROITypeOrder;
		}
		else
		{
			throw new Exception("Cannot get fit data because there is no required parameters.");
		}
	}
	
	private double[][] getFitData_twoDiffRates(Parameter[] newParams, boolean[] errorOfInterest) throws Exception
	{
		double[][] reducedExpData = getDimensionReducedExpData();
		double[] reducedExpTimePoints = getReducedExpTimePoints();
		int roiLen = getExpFrapStudy().getFrapData().getRois().length;
		double refTimeInterval = (getRefTimeStep().getDefaultTimeStep() * getRefTimeSpec().getKeepEvery());
		
//		double diffFastOffset = 0;
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
				if(newParams[i].getName().equals(FRAPOptData.TWODIFFRATES_PARAMETER_NAMES[FRAPOptData.TWODIFFRATES_FAST_DIFFUSION_RATE_INDEX]))
				{   
					diffFastRate = newParams[FRAPOptData.TWODIFFRATES_FAST_DIFFUSION_RATE_INDEX].getInitialGuess();
				}
				else if(newParams[i].getName().equals(FRAPOptData.TWODIFFRATES_PARAMETER_NAMES[FRAPOptData.TWODIFFRATES_FAST_MOBILE_FRACTION_INDEX]))
				{
					mFracFast = newParams[FRAPOptData.TWODIFFRATES_FAST_MOBILE_FRACTION_INDEX].getInitialGuess();
				}
				else if(newParams[i].getName().equals(FRAPOptData.TWODIFFRATES_PARAMETER_NAMES[FRAPOptData.TWODIFFRATES_SLOW_DIFFUSION_RATE_INDEX]))
				{
					diffSlowRate = newParams[FRAPOptData.TWODIFFRATES_SLOW_DIFFUSION_RATE_INDEX].getInitialGuess();
				}
				else if(newParams[i].getName().equals(FRAPOptData.TWODIFFRATES_PARAMETER_NAMES[FRAPOptData.TWODIFFRATES_SLOW_MOBILE_FRACTION_INDEX]))
				{
					mFracSlow = newParams[FRAPOptData.TWODIFFRATES_SLOW_MOBILE_FRACTION_INDEX].getInitialGuess();
				}
				else if(newParams[i].getName().equals(FRAPOptData.TWODIFFRATES_PARAMETER_NAMES[FRAPOptData.TWODIFFRATES_BLEACH_WHILE_MONITOR_INDEX]))
				{
					monitoringRate = newParams[FRAPOptData.TWODIFFRATES_BLEACH_WHILE_MONITOR_INDEX].getInitialGuess();
				}
			}
			
			
//			double diffFastRate = diffFastOffset; //diffSlowRate + diffFastOffset;
						   
			fastData = FRAPOptimization.getValueByDiffRate(REF_DIFFUSION_RATE_PARAM.getInitialGuess(),
                    									   diffFastRate,
                    									   getDimensionReducedRefData(null),
                    									   reducedExpData,
                    									   refDataTimePoints,
                    									   reducedExpTimePoints,
                    									   roiLen,
                    									   refTimeInterval);
			
			slowData = FRAPOptimization.getValueByDiffRate(REF_DIFFUSION_RATE_PARAM.getInitialGuess(),
                    									   diffSlowRate,
                    									   getDimensionReducedRefData(null),
                    									   reducedExpData,
                    									   refDataTimePoints,
                    									   reducedExpTimePoints,
                    									   roiLen,
                    									   refTimeInterval);
			
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
					newData[i][j] = FRAPOptimization.getValueFromParameters_twoDiffRates(mFracFast, 
							                                              fastData[i][j],
							                                              mFracSlow, 
							                                              slowData[i][j],
							                                              monitoringRate,
							                                              firstPostBleach[i],
							                                              reducedExpTimePoints[j]);
//						double newValue = (mFracFast * fastData[i][j] + mFracSlow * slowData[i][j] + immobileFrac * firstPostBleach[i]) * Math.exp(-(monitoringRate * expTimePoints[j]));
				}
				
			}
			// REORder according to roiTypes
			double[][] fitDataInROITypeOrder = new double[newData.length][];
			for (int i = 0; i < getExpFrapStudy().getFrapData().getRois().length; i++) {
				for (int j = 0; j < ROI.RoiType.values().length; j++) {
					if(getExpFrapStudy().getFrapData().getRois()[i].getROIType().equals(ROI.RoiType.values()[j])){
						fitDataInROITypeOrder[j] = newData[i];
						break;
					}
				}
			}
			
			return fitDataInROITypeOrder;
		}
		else
		{
			throw new Exception("Cannot perform optimization because there is no parameters to be evaluated.");
		}
	}
	
	public Parameter[] getBestParamters(Parameter[] inParams, boolean[] errorOfInterest) throws Exception
	{
		Parameter[] outParams = new Parameter[inParams.length];
		String[] outParaNames = new String[inParams.length];
		double[] outParaVals = new double[inParams.length];
		
		FRAPOptimization.estimate(this, inParams, outParaNames, outParaVals, errorOfInterest);
		for(int i = 0; i < outParams.length; i++)
		{
			outParams[i] = new Parameter(outParaNames[i], Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0, outParaVals[i]);
		}
		for(int i = 0; i < outParams.length; i++)
		{
			System.out.println("Estimate result for "+outParams[i].getName()+ " is: "+outParams[i].getInitialGuess());
		}
		return outParams;
	}
	
	private void checkValidityOfRefData() throws Exception 
	{
		double[] portion = new double[]{0.8, 0.9};
		double[][] refData = getDimensionReducedRefData(null);
		double[] refTimePoints = FRAPOptimization.timeReduction(refDataTimePoints, Integer.parseInt(getExpFrapStudy().getFrapModelParameters().startIndexForRecovery));
		for(int i = 0 ; i < getExpFrapStudy().getFrapData().getRois().length; i++)
		{
			for(int k = 0 ; k < portion.length; k++)
			{
				int startingTimeIndex = (int)Math.round(refTimePoints.length * portion[k]);
				double max = 0;
				double avg = 0;
				double std = 0;
				for(int j = startingTimeIndex; j < (refTimePoints.length); j++ )
				{
					if(refData[i][j] > max)
					{
						max = refData[i][j];
					}
					avg = avg + refData[i][j];
				}
				avg = avg / (refTimePoints.length - startingTimeIndex);
				for(int j = startingTimeIndex; j < (refTimePoints.length); j++ )
				{
					std = std + (refData[i][j] - avg)*(refData[i][j] - avg);
				}
				std = Math.sqrt(std);
				System.out.println("In ROI Type " + getExpFrapStudy().getFrapData().getRois()[i].getROIType().name() + ".   Max of last "+ (1-portion[k])*100+"% data is:" + max +".  Average is:" + avg +". Standard Deviation is:" + std + ".    Std is "+ ((std/max)*100) + "% of max.");
			}
		}
		
		System.out.println("End of check validity of reference data");
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
			FRAPStudy expFrapStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null), null);
			expFrapStudy.setXmlFilename(expFileName);
			System.out.println("experimental data time points"+expFrapStudy.getFrapData().getImageDataset().getSizeT());
			System.out.println("finish loading original data....");
			
			//create reference data
			System.out.println("creating rederence data....");
			
			DataSetControllerImpl.ProgressListener progressListener =
			new DataSetControllerImpl.ProgressListener(){
				public void updateProgress(double progress){
					System.out.println((int)Math.round(progress*100));
				}
				public void updateMessage(String message){
					//ignore
				}
			};

			FRAPOptData optData = new FRAPOptData(expFrapStudy, 5, localWorkspace,progressListener, true);
			
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
				if(/*!rois[i].getROIType().equals(RoiType.ROI_BLEACHED)*/rois[i].getROIType().equals(RoiType.ROI_BACKGROUND) || rois[i].getROIType().equals(RoiType.ROI_CELL))
				{
					errorOfInterest[i] = false;
				}
				else
				{
					errorOfInterest[i] = true;
				}
			}
			Parameter[] bestParams = optData.getBestParamters(inParams, errorOfInterest);
//			for(int i=0; i<bestParams.length; i++)
//			{
//				System.out.println("Best estimation for " + bestParams[i].getName()+" is: " + bestParams[i].getInitialGuess());
//			}
			
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

	public int getNumEstimatedParams() {
		return numEstimatedParams;
	}

	public void setNumEstimatedParams(int numEstimatedParams) {
		this.numEstimatedParams = numEstimatedParams;
	}
	public Boolean isRunRefSim() {
		return bRunRefSim;
	}
}

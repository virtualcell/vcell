package cbit.vcell.microscopy;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import org.vcell.util.Origin;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.KeyValue;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.client.server.VCDataManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskStatusSupport;
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solver.DataProcessingInstructions;
import cbit.vcell.solver.DataProcessingOutput;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.CartesianMesh;


public class FRAPOptData {
	public static final int NUM_PARAMS_FOR_ONE_DIFFUSION_RATE = 3;//diffusion rate, mobile fraction, bleach while monitoring rate
	public static final int NUM_PARAMS_FOR_TWO_DIFFUSION_RATE = 5;//fast diff rate, fast mobile fraction, slow diff rate, slow mobile fraction, bleach while monitoring rate
	
	/*----------------reference data by diffusion rate=1, mobileFrac=1 and bwmRate = 0-------------------*/
	public static final Parameter REF_DIFFUSION_RATE_PARAM =
		new cbit.vcell.opt.Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE], 0, 200, 1.0, 1.0);
	public static final Parameter REF_MOBILE_FRACTION_PARAM =
		new cbit.vcell.opt.Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION], 0, 1, 1.0, 1.0);
	public static final Parameter REF_BLEACH_WHILE_MONITOR_PARAM =
		new cbit.vcell.opt.Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE], 0, 1, 1.0,  0);
	public static final Parameter REF_SECOND_DIFFUSION_RATE_PARAM =
		new cbit.vcell.opt.Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_DIFF_RATE], 0, 100, 1.0, 1.0);
	public static final Parameter REF_SECOND_MOBILE_FRACTION_PARAM =
		new cbit.vcell.opt.Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_FRACTION], 0, 1, 1.0, 1.0);
	
	public static final int REF_BWM_LOG_VAL_MIN = -5;
	public static final int REF_BWM_LOG_VAL_MAX = 0;
	
	
//	private static int maxRefSavePoints = 500;
	private static double REF_STARTINGTIME = 0;
	private static double REF_ENDINGTIME = 500;
	
//	private Boolean bRunRefSim = null;
	
	private FRAPStudy expFrapStudy = null;
	private LocalWorkspace localWorkspace = null;
	private TimeBounds refTimeBounds = null;
	private TimeStep refTimeStep = null;
	private DefaultOutputTimeSpec  refTimeSpec = null;
	private int numEstimatedParams = 0;
	private double[][] dimensionReducedRefData = null;
	private double[] refDataTimePoints = null;
	
	private static final String REFERENCE_DIFF_DELTAT = "0.1";
	private static final String REFERENCE_DIFF_RATE_COEFFICIENT = "1";
	private static final String REFERENCE_DIFF_RATE_STR = REFERENCE_DIFF_RATE_COEFFICIENT +"*(t+"+ REFERENCE_DIFF_DELTAT +")";
		
	public FRAPOptData(FRAPStudy argExpFrapStudy, int numberOfEstimatedParams, LocalWorkspace argLocalWorkSpace,
			ClientTaskStatusSupport progressListener) throws Exception
	{
		expFrapStudy = argExpFrapStudy;
		setNumEstimatedParams(numberOfEstimatedParams);
		localWorkspace = argLocalWorkSpace;
//		bRunRefSim = new Boolean(bRefSim); 
		if(progressListener != null){
			progressListener.setMessage("Getting experimental data ROI averages...");
		}
		dimensionReducedRefData = getDimensionReducedRefData(progressListener, null);
	}
	
	public FRAPOptData(FRAPStudy argExpFrapStudy, int numberOfEstimatedParams, LocalWorkspace argLocalWorkSpace, SimpleReferenceData simRefData) throws Exception
	{
		if(simRefData.getNumColumns()!= (1+argExpFrapStudy.getFrapData().getROILength()))
		{
			throw new Exception("Stored reference data is illegal. ");
		}
		expFrapStudy = argExpFrapStudy;
		setNumEstimatedParams(numberOfEstimatedParams);
		localWorkspace = argLocalWorkSpace;
		dimensionReducedRefData = getDimensionReducedRefData(null, simRefData); 
	}
	
	public TimeBounds getRefTimeBounds()
	{
//		if(refTimeBounds == null)
//		{
//			//estimated t = ( bleach area max width^2 /(4*D)) * ln(1/delta), use bleah area width as length.
//			ROI bleachedROI = getExpFrapStudy().getFrapData().getRoi(RoiType.ROI_BLEACHED);
//			
//			Rectangle bleachRect = bleachedROI.getRoiImages()[0].getNonzeroBoundingBox();
//			double width = ((double)bleachRect.width/getExpFrapStudy().getFrapData().getImageDataset().getISize().getX()) * 
//			               getExpFrapStudy().getFrapData().getImageDataset().getExtent().getX();
//			double height = ((double)bleachRect.height/getExpFrapStudy().getFrapData().getImageDataset().getISize().getY()) * 
//            			   getExpFrapStudy().getFrapData().getImageDataset().getExtent().getY();
//			
//			double bleachWidth = Math.max(width, height);
//			final double  unrecovery_threshold = .01;
//			double refEndingTime = (bleachWidth * bleachWidth/(4*REF_DIFFUSION_RATE_PARAM.getInitialGuess())) * Math.log(1/unrecovery_threshold);
			
//			refTimeBounds = new TimeBounds(FRAPOptData.startingTime, refEndingTime);
//		}
		TimeBounds refTimeBounds = new TimeBounds(FRAPOptData.REF_STARTINGTIME, FRAPOptData.REF_ENDINGTIME);
		return refTimeBounds;
	}
	
	public TimeStep getRefTimeStep()
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
		double timeStep = Double.parseDouble(REFERENCE_DIFF_DELTAT); 
		return new TimeStep(timeStep, timeStep, timeStep);
	}
	
	public DefaultOutputTimeSpec getRefTimeSpec()
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
		String ncFileStr = new File(getLocalWorkspace().getDefaultSimDataDirectory(), vcSimDataID.getID()+SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION).getAbsolutePath();
		NetCDFRefDataReader refDataReader = new NetCDFRefDataReader(ncFileStr);
		//get ref sim time points
		double[] rawRefDataTimePoints = refDataReader.getTimePoints();
		//get shifted time points
		refDataTimePoints = timeShiftForBaseDiffRate(rawRefDataTimePoints);
		//get summarized raw ref data
		double[][] rawData = refDataReader.getRegionVar(); //contains only 8rois +1(the area that beyond 8 rois)
		//extend to whole roi data
		dimensionReducedRefData = FRAPOptimization.extendSimToFullROIData(expFrapStudy.getFrapData(), rawData, refDataTimePoints.length);
		
		System.out.println("generating dimension reduced ref data, done ....");
		
		//if reference simulation completes successfully, we save reference data info and remove old simulation files.
		boolean[] selectedROIs = new boolean[FRAPData.VFRAP_ROI_ENUM.values().length];
		Arrays.fill(selectedROIs, true);
		getExpFrapStudy().setStoredRefData(FRAPOptimization.doubleArrayToSimpleRefData(dimensionReducedRefData, getRefDataTimePoints(), 0, selectedROIs));

		//remove reference simulation files
//		FRAPStudy.removeSimulationFiles(referenceSimKeyValue, getLocalWorkspace()); 
		//remove experimental and roi external files
//		FRAPStudy.removeExternalFiles(getExpFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(), 
//				                      getExpFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(), getLocalWorkspace());
	}
	
	private double[] timeShiftForBaseDiffRate(double[] timePoints)
	{ 
		double delT = Double.parseDouble(REFERENCE_DIFF_DELTAT);
		double s = Double.parseDouble(REFERENCE_DIFF_RATE_COEFFICIENT);
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
			sim.getSolverTaskDescription().setTimeBounds(getRefTimeBounds());
			sim.getSolverTaskDescription().setTimeStep(getRefTimeStep());
			sim.getSolverTaskDescription().setOutputTimeSpec(getRefTimeSpec());
			
			DataProcessingInstructions dpi = getExpFrapStudy().getDataProcessInstructions(getLocalWorkspace());
			sim.setDataProcessingInstructions(dpi);
			System.out.println("run FRAP Reference Simulation...");

			//run simulation
			FRAPStudy.runFVSolverStandalone_ref(
				new File(getLocalWorkspace().getDefaultSimDataDirectory()),
				new StdoutSessionLog(LocalWorkspace.getDefaultOwner().getName()),
				bioModel.getSimulations(0),
				getExpFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),
				getExpFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),
				psfFieldFunc.getExternalDataIdentifier(),
				progressListener, true);

			KeyValue referenceSimKeyValue = sim.getVersion().getVersionKey();
			
			return referenceSimKeyValue;
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
		if(getNumEstimatedParams() == FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF)
		{
			error = FRAPOptimization.getErrorByNewParameters_oneDiffRate(REF_DIFFUSION_RATE_PARAM.getInitialGuess(), 
					                                              newParamVals,
					                                              getDimensionReducedRefData(null, null),
					                                              getDimensionReducedExpData(),
					                                              refDataTimePoints,
					                                              getReducedExpTimePoints(),
					                                              getExpFrapStudy().getFrapData().getROILength(), 
					                                              eoi);
		}
		else if(getNumEstimatedParams() == FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF)
		{
			error = FRAPOptimization.getErrorByNewParameters_twoDiffRates(REF_DIFFUSION_RATE_PARAM.getInitialGuess(),
																  newParamVals,
																  getDimensionReducedRefData(null, null),
																  getDimensionReducedExpData(),
																  refDataTimePoints,
																  getReducedExpTimePoints(),
																  getExpFrapStudy().getFrapData().getROILength(),
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

	public double[][] getFitData(Parameter[] newParams) throws Exception
	{
		double[][] result = null;
		if(getNumEstimatedParams() == newParams.length)
		{
			if(getNumEstimatedParams() == FRAPOptData.NUM_PARAMS_FOR_ONE_DIFFUSION_RATE)
			{
				result = getFitData_oneDiffRate(newParams);
			}
			else if(getNumEstimatedParams() == FRAPOptData.NUM_PARAMS_FOR_TWO_DIFFUSION_RATE)
			{
				result = getFitData_twoDiffRates(newParams);
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
	
	private double[][] getFitData_oneDiffRate(Parameter[] newParams) throws Exception
	{
		double[][] reducedExpData = getDimensionReducedExpData();
		double[] reducedExpTimePoints = getReducedExpTimePoints();
		int roiLen = getExpFrapStudy().getFrapData().getROILength();
		double refTimeInterval = (getRefTimeStep().getDefaultTimeStep() * getRefTimeSpec().getKeepEvery());
//		double refTimeInterval = getRefTimeSpec().getOutputTimeStep();
		
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
			
			diffData = FRAPOptimization.getValueByDiffRate(REF_DIFFUSION_RATE_PARAM.getInitialGuess(),
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
					newData[i][j] = FRAPOptimization.getValueFromParameters_oneDiffRate(diffData[i][j],
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
		double[][] reducedExpData = getDimensionReducedExpData();
		double[] reducedExpTimePoints = getReducedExpTimePoints();
		int roiLen = getExpFrapStudy().getFrapData().getROILength();
		double refTimeInterval = (getRefTimeStep().getDefaultTimeStep() * getRefTimeSpec().getKeepEvery());
		
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
			
			fastData = FRAPOptimization.getValueByDiffRate(REF_DIFFUSION_RATE_PARAM.getInitialGuess(),
                    									   diffFastRate,
                    									   getDimensionReducedRefData(null, null),
                    									   refDataTimePoints,
                    									   reducedExpTimePoints,
                    									   roiLen);
			
			slowData = FRAPOptimization.getValueByDiffRate(REF_DIFFUSION_RATE_PARAM.getInitialGuess(),
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
			
			return newData;
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
		for(int i = 0; i < outParaNames.length; i++)
		{
			if(outParaNames[i].equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE]))
			{
				double primaryDiffRate = outParaVals[i];
				if(primaryDiffRate > REF_DIFFUSION_RATE_PARAM.getUpperBound())
				{
					primaryDiffRate = REF_DIFFUSION_RATE_PARAM.getUpperBound(); 
				}
				if(primaryDiffRate < REF_DIFFUSION_RATE_PARAM.getLowerBound())
				{
					primaryDiffRate = REF_DIFFUSION_RATE_PARAM.getLowerBound();
				}
	
				outParams[i] = new Parameter(outParaNames[i], REF_DIFFUSION_RATE_PARAM.getLowerBound(), REF_DIFFUSION_RATE_PARAM.getUpperBound(), 1.0, primaryDiffRate);
			}
			else if(outParaNames[i].equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION]))
			{
				double primaryFraction = outParaVals[i];
				if(primaryFraction > REF_MOBILE_FRACTION_PARAM.getUpperBound())
				{
					primaryFraction = REF_MOBILE_FRACTION_PARAM.getUpperBound(); 
				}
				if(primaryFraction < REF_MOBILE_FRACTION_PARAM.getLowerBound())
				{
					primaryFraction = REF_MOBILE_FRACTION_PARAM.getLowerBound();
				}
	
				outParams[i] = new Parameter(outParaNames[i], REF_MOBILE_FRACTION_PARAM.getLowerBound(), REF_MOBILE_FRACTION_PARAM.getUpperBound(), 1.0, primaryFraction);
			}
			else if(outParaNames[i].equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE]))
			{
				double bwmRate = outParaVals[i];
				if(bwmRate > REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound())
				{
					bwmRate = REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(); 
				}
				if(bwmRate < REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound())
				{
					bwmRate = REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound();
				}
	
				outParams[i] = new Parameter(outParaNames[i], REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(), REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(), 1.0, bwmRate);
			}
			else if(outParaNames[i].equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_DIFF_RATE]))
			{
				double secDiffRate = outParaVals[i];
				if(secDiffRate > REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound())
				{
					secDiffRate = REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound(); 
				}
				if(secDiffRate < REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound())
				{
					secDiffRate = REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound();
				}
	
				outParams[i] = new Parameter(outParaNames[i], REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound(), REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound(), 1.0, secDiffRate);
			}
			else if(outParaNames[i].equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_FRACTION]))
			{
				double secFraction = outParaVals[i];
				if(secFraction > REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound())
				{
					secFraction = REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound(); 
				}
				if(secFraction < REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound())
				{
					secFraction = REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound();
				}
	
				outParams[i] = new Parameter(outParaNames[i], REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound(), REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound(), 1.0, secFraction);
			}
		}
		//for debug purpose
//		for(int i = 0; i < outParams.length; i++)
//		{
//			System.out.println("Estimate result for "+outParams[i].getName()+ " is: "+outParams[i].getInitialGuess());
//		}
		return outParams;
	}
	
	private void checkValidityOfRefData() throws Exception 
	{
		double[] portion = new double[]{0.8, 0.9};
		double[][] refData = getDimensionReducedRefData(null, null);
		double[] refTimePoints = FRAPOptimization.timeReduction(refDataTimePoints, getExpFrapStudy().getStartingIndexForRecovery());
		for(int i = 0 ; i < getExpFrapStudy().getFrapData().getROILength(); i++)
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
				System.out.println("In ROI Name " + getExpFrapStudy().getFrapData().getRois()[i].getROIName() + ".   Max of last "+ (1-portion[k])*100+"% data is:" + max +".  Average is:" + avg +". Standard Deviation is:" + std + ".    Std is "+ ((std/max)*100) + "% of max.");
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
			FRAPStudy expFrapStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null).getRootElement(), null);
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
	
	public void getFromStoredRefData(SimpleReferenceData argSimRefData)
	{
		int roiLen = getExpFrapStudy().getFrapData().getROILength();
		if(argSimRefData.getNumColumns() == (1+roiLen))
		{
			//t is in the first column of simpleReferenceData
			refDataTimePoints = argSimRefData.getColumnData(0);
			double[][] result = new double[roiLen][];
			for(int i=0; i<roiLen; i++)
			{
				result[i]=argSimRefData.getColumnData(i+1); //first column is t, so the roi data starts from second column
			}
			dimensionReducedRefData = result;
		}
	}
}

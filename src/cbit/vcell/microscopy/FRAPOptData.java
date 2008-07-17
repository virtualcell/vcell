package cbit.vcell.microscopy;

import java.awt.Rectangle;
import java.io.File;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.server.VCDataManager;
import cbit.vcell.microscopy.ROI.RoiType;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.opt.Parameter;
import cbit.vcell.server.StdoutSessionLog;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.DataSetControllerImpl.ProgressListener;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;

public class FRAPOptData {
	public static String[] PARAMETER_NAMES = new String[]{ "diffRate",
														  "mobileFrac",
														  "bleachWhileMonitoringRate"};
	public static final int DIFFUSION_RATE_INDEX = 0;
	public static final int MOBILE_FRACTION_INDEX = 1;
	public static final int BLEACH_WHILE_MONITOR_INDEX = 2;
			
	public static final Parameter REF_DIFFUSION_RATE_PARAM =
		new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.DIFFUSION_RATE_INDEX], 0, 20, 1.0, 1.0);
	public static final Parameter REF_MOBILE_FRACTION_PARAM =
		new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.MOBILE_FRACTION_INDEX], 0, 1, 1.0, 1.0);
	public static final Parameter REF_BLEACH_WHILE_MONITOR_PARAM =
		new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.BLEACH_WHILE_MONITOR_INDEX], 0, 1, 1.0,  0);

	
	private static int maxRefSavePoints = 500;
	private static int startingTime = 0;
	
	private FRAPStudy expFrapStudy = null;
	private LocalWorkspace localWorkspace = null;
	private FRAPData refFrapData = null;
	private TimeBounds refTimeBounds = null;
	private TimeStep refTimeStep = null;
	private DefaultOutputTimeSpec  refTimeSpec = null;
	private double[][] dimensionReducedRefData = null;
	private double[][] dimensionReducedExpData = null;
	private double[] reducedExpTimePoints = null;
	
	public FRAPOptData(FRAPStudy argExpFrapStudy, LocalWorkspace argLocalWorkSpace,
			DataSetControllerImpl.ProgressListener progressListener) throws Exception
	{
		expFrapStudy = argExpFrapStudy;
//		REF_DIFFUSION_RATE = Double.parseDouble(expFrapStudy.getFrapModelParameters().diffusionRate);
//		REF_MOBILE_FRACTION =
//			(expFrapStudy.getFrapModelParameters().mobileFraction != null
//				?Double.parseDouble(expFrapStudy.getFrapModelParameters().mobileFraction)
//				:1.0);
//
//		REF_BLEACH_WHILE_MONITOR = 
//			(expFrapStudy.getFrapModelParameters().monitorBleachRate != null
//				?Double.parseDouble(expFrapStudy.getFrapModelParameters().monitorBleachRate)
//				:0);
		
		localWorkspace = argLocalWorkSpace;
		dimensionReducedExpData = getDimensionReducedExpData();
		dimensionReducedRefData = getDimensionReducedRefData(progressListener);
	}
	
	public TimeBounds getRefTimeBounds()
	{
		if(refTimeBounds == null)
		{
			//estimated t = ( bleach area max width /(4*D)) * ln(1/delta), use bleah area width as length.
			ROI bleachedROI = getExpFrapStudy().getFrapData().getRoi(RoiType.ROI_BLEACHED);
			
			Rectangle bleachRect = bleachedROI.getRoiImages()[0].getNonzeroBoundingBox();
			double width = ((double)bleachRect.width/getExpFrapStudy().getFrapData().getImageDataset().getISize().getX()) * 
			               getExpFrapStudy().getFrapData().getImageDataset().getExtent().getX();
			double height = ((double)bleachRect.height/getExpFrapStudy().getFrapData().getImageDataset().getISize().getY()) * 
            			   getExpFrapStudy().getFrapData().getImageDataset().getExtent().getY();
			
			double bleachWidth = Math.sqrt(width * width + height*height);
			final double ERROR_TOLERANCE = .01;
			double refEndingTime = (bleachWidth * bleachWidth/(4*REF_DIFFUSION_RATE_PARAM.getInitialGuess())) * Math.log(1/ERROR_TOLERANCE);
			
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
		if(dimensionReducedRefData == null)
		{
			refreshDimensionReducedRefData(progressListener);
		}
		return dimensionReducedRefData;
	}
	
	public double[][] getDimensionReducedExpData() throws Exception
	{
		if(dimensionReducedExpData == null)
		{
			//normalize the experimental data, because the reference data is normalized
			VCDataManager vcManager = getLocalWorkspace().getVCDataManager();
			double[] prebleachAvg = vcManager.getSimDataBlock(getExpFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(), "prebleach_avg", 0).getData();
			// use 1 as scale factor becase we don't need to unscale the exp data. (expdata/prebleachAvg cancells the scale factor)
			double scaleFactor = 1; // no need to scale back.
			int startRecoveryIndex = Integer.parseInt(getExpFrapStudy().getFrapModelParameters().startIndexForRecovery);
			dimensionReducedExpData = FRAPOptimization.dataReduction(getExpFrapStudy().getFrapData(),true, scaleFactor, startRecoveryIndex, getExpFrapStudy().getFrapData().getRois(), prebleachAvg);
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
	
	public void refreshDimensionReducedRefData(DataSetControllerImpl.ProgressListener progressListener) throws Exception
	{
		System.out.println("run simulation...");
		runRefSimulation(progressListener);
		System.out.println("simulation done...");
		double scaleFactor = getRefFrapData().getOriginalGlobalScaleInfo().originalScaleFactor;
		int startRecoveryIndex = Integer.parseInt(getExpFrapStudy().getFrapModelParameters().startIndexForRecovery);
		dimensionReducedRefData = FRAPOptimization.dataReduction(getRefFrapData(), false,scaleFactor, startRecoveryIndex, getExpFrapStudy().getFrapData().getRois(), null);
		System.out.println("generating dimension reduced ref data, done ....");
	}
	
	public void runRefSimulation(final DataSetControllerImpl.ProgressListener progressListener) throws Exception
	{
		//create biomodel
		BioModel bioModel =
			FRAPStudy.createNewBioModel(
				expFrapStudy,
				new Double(REF_DIFFUSION_RATE_PARAM.getInitialGuess()),
				REF_BLEACH_WHILE_MONITOR_PARAM.getInitialGuess()+"",
				REF_MOBILE_FRACTION_PARAM.getInitialGuess()+"",
				LocalWorkspace.createNewKeyValue(),
				LocalWorkspace.getDefaultOwner(),
				new Integer(expFrapStudy.getFrapModelParameters().startIndexForRecovery));
		
		//change time bound and time step
		Simulation sim = bioModel.getSimulations()[0];
		sim.getSolverTaskDescription().setTimeBounds(getRefTimeBounds());
		sim.getSolverTaskDescription().setTimeStep(getRefTimeStep());
		sim.getSolverTaskDescription().setOutputTimeSpec(getRefTimeSpec());
		
		System.out.println("run simulation....");
		final double RUN_REFSIM_PROGRESS_FRACTION = .5;
		DataSetControllerImpl.ProgressListener runRefSimProgressListener =
			new DataSetControllerImpl.ProgressListener(){
				public void updateProgress(double progress) {
					progressListener.updateProgress(progress*RUN_REFSIM_PROGRESS_FRACTION);
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
		
		DataSetControllerImpl.ProgressListener importSimDataProgressListener =
			new DataSetControllerImpl.ProgressListener(){
				public void updateProgress(double progress) {
					progressListener.updateProgress(.5+progress*(1.0-RUN_REFSIM_PROGRESS_FRACTION));
				}
		};

		String simStr = "SimID_"+sim.getVersion().getVersionKey().toString()+"_0_"+SimDataConstants.LOGFILE_EXTENSION;//"SimID_1215098533149_0_.log";// "SimID_"+sim.getVersion().getVersionKey().toString()+"_0_"+SimDataConstants.LOGFILE_EXTENSION;
		File inFile = new File(getLocalWorkspace().getDefaultSimDataDirectory()+simStr);//new File("C:\\VirtualMicroscopy\\SimulationData\\SimID_1215098533149_0_.log");
		String identifierName = FRAPStudy.SPECIES_NAME_PREFIX_COMBINED;// + TokenMangler.fixTokenStrict(NumberUtils.formatNumber(refDiffRate,3));
		FRAPData fData = FRAPData.importFRAPDataFromVCellSimulationData(inFile, identifierName, importSimDataProgressListener);
		setRefFrapData(fData);

		System.out.println("finish loading simulation results to reference data....");
	}
	
	public double computeError(double newParamVals[]) throws Exception
	{
		double error = FRAPOptimization.getErrorByNewParameters(REF_DIFFUSION_RATE_PARAM.getInitialGuess(), 
				                                              newParamVals,
				                                              getDimensionReducedRefData(null),
				                                              getDimensionReducedExpData(),
				                                              getRefFrapData().getImageDataset().getImageTimeStamps(),
				                                              getReducedExpTimePoints(),
				                                              getExpFrapStudy().getFrapData().getRois().length, 
				                                              (getRefTimeStep().getDefaultTimeStep() * getRefTimeSpec().getKeepEvery()));
		
		for(int i=0; i<newParamVals.length; i++)
		{
			System.out.println("Parameter "+ FRAPOptData.PARAMETER_NAMES[i]+ " is: " + newParamVals[i]);
		}
		System.out.println("error:" + error);
		System.out.println("--------------------------------");
		return error;
	}

	public double[][] getFitData(Parameter[] newParams) throws Exception
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
				if(newParams[i].getName().equals(FRAPOptData.PARAMETER_NAMES[FRAPOptData.DIFFUSION_RATE_INDEX]))
				{
					diffRate = newParams[FRAPOptData.DIFFUSION_RATE_INDEX].getInitialGuess();
				}
				else if(newParams[i].getName().equals(FRAPOptData.PARAMETER_NAMES[FRAPOptData.MOBILE_FRACTION_INDEX]))
				{
					mobileFrac = newParams[FRAPOptData.MOBILE_FRACTION_INDEX].getInitialGuess();
					mobileFrac = Math.min(mobileFrac, 1);
				}
				else if(newParams[i].getName().equals(FRAPOptData.PARAMETER_NAMES[FRAPOptData.BLEACH_WHILE_MONITOR_INDEX]))
				{
					bleachWhileMonitoringRate = newParams[FRAPOptData.BLEACH_WHILE_MONITOR_INDEX].getInitialGuess();
				}
			}
			
			diffData = FRAPOptimization.getValueByDiffRate(REF_DIFFUSION_RATE_PARAM.getInitialGuess(),
                    diffRate,
                    getDimensionReducedRefData(null),
                    reducedExpData,
                    getRefFrapData().getImageDataset().getImageTimeStamps(),
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
				for(int j=0; j<getReducedExpTimePoints().length; j++)
				{
//					newData[i][j] = (mobileFrac * diffData[i][j] + imMobielFrac * firstPostBleach[i]) * Math.exp(-(bleachWhileMonitoringRate * reducedExpTimePoints[j]));
					newData[i][j] = FRAPOptimization.getValueFromParameters(diffData[i][j], mobileFrac, bleachWhileMonitoringRate, firstPostBleach[i], reducedExpTimePoints[j]);
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
			return fitDataInROITypeOrder;
		}
		else
		{
			throw new Exception("Cannot get fit data because there is no required parameters.");
		}
	}
	
	public Parameter[] getBestParamters(Parameter[] inParams) throws Exception
	{
		Parameter[] outParams = new Parameter[inParams.length];
		String[] outParaNames = new String[inParams.length];
		double[] outParaVals = new double[inParams.length];
		FRAPOptimization.estimate(this, inParams, outParaNames, outParaVals);
		for(int i = 0; i < inParams.length; i++)
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
		double[] refTimePoints = FRAPOptimization.timeReduction(getRefFrapData().getImageDataset().getImageTimeStamps(), Integer.parseInt(getExpFrapStudy().getFrapModelParameters().startIndexForRecovery));
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
	
	public FRAPData getRefFrapData() {
		return refFrapData;
	}

	public void setRefFrapData(FRAPData frapData) {
		this.refFrapData = frapData;
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
			String expFileName = "C:/VirtualMicroscopy/forOptTest_cell_10_blc_2_d_1p0_t_10_saveEvery_10_another.vfrap";
			xmlString = XmlUtil.getXMLString(expFileName);
			MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
			FRAPStudy expFrapStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null), null);
			expFrapStudy.setXmlFilename(expFileName);
			System.out.println("experimental data time points"+expFrapStudy.getFrapData().getImageDataset().getSizeT());
			System.out.println("finish loading original data....");
			
			//create rederence data
			System.out.println("creating rederence data....");
			
			DataSetControllerImpl.ProgressListener progressListener =
			new DataSetControllerImpl.ProgressListener(){
				public void updateProgress(double progress){
					System.out.println((int)Math.round(progress*100));
				}
			};

			FRAPOptData optData = new FRAPOptData(expFrapStudy, localWorkspace,progressListener);
			
			Parameter diff = new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.DIFFUSION_RATE_INDEX], 0, 100, 1.0, Double.parseDouble(expFrapStudy.getFrapModelParameters().diffusionRate));
			Parameter mobileFrac = new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.MOBILE_FRACTION_INDEX], 0, 1, 1.0, 1/*Double.parseDouble(expFrapStudy.getFrapModelParameters().mobileFraction)*/);
			Parameter bleachWhileMonitoringRate = new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.BLEACH_WHILE_MONITOR_INDEX], 0, 10, 1.0,  0/*Double.parseDouble(expFrapStudy.getFrapModelParameters().monitorBleachRate)*/);
			Parameter[] inParams = new Parameter[]{diff, mobileFrac, bleachWhileMonitoringRate};
			Parameter[] bestParams = optData.getBestParamters(inParams);
						
			
			double[][] result = optData.getFitData(inParams); // double[roiLen][timePoints]
			int indexROI = -1;
			for(int j = 0; j < expFrapStudy.getFrapData().getRois().length; j++)
			{
				if(expFrapStudy.getFrapData().getRois()[j].getROIType().equals(RoiType.ROI_BLEACHED))
				{
					indexROI = j;
					break;
				}
			}
			int index = Integer.parseInt(expFrapStudy.getFrapModelParameters().startIndexForRecovery);
//			for(int i = 0; i < (expFrapStudy.getFrapData().getImageDataset().getImageTimeStamps().length - index); i++)
//			{
//				System.out.println(expFrapStudy.getFrapData().getImageDataset().getImageTimeStamps()[i+index]+"\t"+result[indexROI][i]);
//			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
}

package cbit.vcell.microscopy;

import java.io.File;

import org.xml.sax.XMLReader;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import cbit.util.NumberUtils;
import cbit.util.TokenMangler;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.server.VCDataManager;
import cbit.vcell.server.StdoutSessionLog;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.xml.XMLTags;

public class FRAPOptData {
	public static int idxOptDiffRate = 0;
	public static int idxMinError = 1;
	public static double refDiffRate = 1;
	public static double delta = 0.01;
	public static double refBleachWhileMonitoringRate = 0;
	
	public static int maxRefSavePoints = 200;
	public static int startingTime = 0;
	public static int numSpecies = 1;
	
	FRAPStudy expFrapStudy = null;
	LocalWorkspace localWorkspace = null;
	FRAPData refFrapData = null;
	TimeBounds refTimeBounds = null;
	TimeStep refTimeStep = null;
	DefaultOutputTimeSpec  refTimeSpec = null;
	double[][] dimensionReducedRefData = null;
	double[][] dimensionReducedExpData = null;
		
	public FRAPOptData(FRAPStudy argExpFrapStudy, LocalWorkspace argLocalWorkSpace) throws Exception
	{
		expFrapStudy = argExpFrapStudy;
		localWorkspace = argLocalWorkSpace;
		dimensionReducedRefData = getDimensionReducedRefData();
	}
	
	public TimeBounds getRefTimeBounds()
	{
		if(refTimeBounds == null)
		{
			//estimated t = (Max(Length in cell)/(4*D)) * ln(1/delta), use image width as length.
			double imgWidth =  getExpFrapStudy().getFrapData().getImageDataset().getAllImages()[0].getExtent().getX();
			double refEndingTime = (imgWidth/(4*FRAPOptData.refDiffRate)) * Math.log(1/FRAPOptData.delta);
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
			double timeStep = (deltaX*deltaX /FRAPOptData.refDiffRate) * 0.25;
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
	
	public double[][] getDimensionReducedRefData() throws Exception
	{
		if(dimensionReducedRefData == null)
		{
			refreshDimensionReducedRefData();
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
	
	public void refreshDimensionReducedRefData() throws Exception
	{
		System.out.println("run simulation...");
		runRefSimulation();
		System.out.println("simulation done...");
		double scaleFactor = getRefFrapData().getOriginalGlobalScaleInfo().originalScaleFactor;
		int startRecoveryIndex = Integer.parseInt(getExpFrapStudy().getFrapModelParameters().startIndexForRecovery);
		dimensionReducedRefData = FRAPOptimization.dataReduction(getRefFrapData(), false,scaleFactor, startRecoveryIndex, getExpFrapStudy().getFrapData().getRois(), null);
		System.out.println("generating dimension reduced ref data, done ....");
	}
	
	public void runRefSimulation() throws Exception
	{
		//create biomodel
		BioModel bioModel =
			FRAPStudy.createNewBioModel(
				expFrapStudy,
				numSpecies,
				new Double(FRAPOptData.refDiffRate),
				String.valueOf(FRAPOptData.refBleachWhileMonitoringRate),
				LocalWorkspace.createNewKeyValue(),
				LocalWorkspace.getDefaultOwner(),
				new Integer(expFrapStudy.getFrapModelParameters().startIndexForRecovery));
		
		//change time bound and time step
		Simulation sim = bioModel.getSimulations()[0];
		sim.getSolverTaskDescription().setTimeBounds(getRefTimeBounds());
		sim.getSolverTaskDescription().setTimeStep(getRefTimeStep());
		sim.getSolverTaskDescription().setOutputTimeSpec(getRefTimeSpec());
		System.out.println("finish creating reference data bioModel....");
		
		DataSetControllerImpl.ProgressListener progressListener =
			new DataSetControllerImpl.ProgressListener(){
				public void updateProgress(double progress){
					System.out.println((int)Math.round(progress*100));
				}
			};
		System.out.println("run simulation....");
		//run simulation
		FRAPStudy.runFVSolverStandalone(
			new File(getLocalWorkspace().getDefaultSimDataDirectory()),
			new StdoutSessionLog(LocalWorkspace.getDefaultOwner().getName()),
			bioModel.getSimulations(0),
			getExpFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),
			getExpFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),
			progressListener);
		
		String simStr = "SimID_"+sim.getVersion().getVersionKey().toString()+"_0_"+SimDataConstants.LOGFILE_EXTENSION;//"SimID_1215098533149_0_.log";// "SimID_"+sim.getVersion().getVersionKey().toString()+"_0_"+SimDataConstants.LOGFILE_EXTENSION;
//		String str = getLocalWorkspace().getDefaultSimDataDirectory()+simStr;
		File inFile = new File(getLocalWorkspace().getDefaultSimDataDirectory()+simStr);//new File("C:\\VirtualMicroscopy\\SimulationData\\SimID_1215098533149_0_.log");
		String identifierName = FRAPStudy.species_Prefix + TokenMangler.fixTokenStrict(NumberUtils.formatNumber(refDiffRate,3));
		FRAPData fData = FRAPData.importFRAPDataFromVCellSimulationData(inFile, identifierName, null);
		setRefFrapData(fData);
		
		//for test
//		String metaDataFileName = "C:/VirtualMicroscopy/forOptTest_meta_cell_10_blc_2_d_0p2_t_80.vfrap";
//		String xmlString = XmlUtil.getXMLString(metaDataFileName);
//		MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
//		FRAPStudy metaFrapStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null), null);
//		metaFrapStudy.setXmlFilename("meta_file");
//		setRefFrapData(metaFrapStudy.getFrapData());
		System.out.println("finish generating reference data....");
	}
	
	public double computeError(double newDiffRate) throws Exception
	{
		if(newDiffRate <= 0)
		{
			return FRAPOptimization.bigValue;
		}
		int startRecoveryIndex = Integer.parseInt(getExpFrapStudy().getFrapModelParameters().startIndexForRecovery);				
		double error = FRAPOptimization.getErrorByNewDiffRate(FRAPOptData.refDiffRate, 
				                                              newDiffRate,
				                                              getDimensionReducedRefData(),
				                                              getDimensionReducedExpData(),
				                                              getRefFrapData().getImageDataset().getImageTimeStamps(),
				                                              FRAPOptimization.timeReduction(getExpFrapStudy().getFrapData().getImageDataset().getImageTimeStamps(), startRecoveryIndex),
				                                              getExpFrapStudy().getFrapData().getRois().length, 
				                                              (getRefTimeStep().getDefaultTimeStep() * getRefTimeSpec().getKeepEvery()));
		
		System.out.println("diffusion rate:" + newDiffRate);
		System.out.println("error:" + error);
		System.out.println("--------------------------------");
		return error;
	}

	private void checkValidityOfRefData() throws Exception 
	{
		double[] portion = new double[]{0.8, 0.9};
		double[][] refData = getDimensionReducedRefData();
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
			FRAPOptData refData = new FRAPOptData(expFrapStudy, localWorkspace);
			
			refData.checkValidityOfRefData();
			double[] params = new double[2]; // to take the minError and diffusionrate back.
			FRAPOptimization.estimate(refData, Double.parseDouble(expFrapStudy.getFrapModelParameters().diffusionRate), params);
			System.out.println("min Error:" + params[FRAPOptData.idxMinError] +"   best estimate of diff rate:" + params[FRAPOptData.idxOptDiffRate]);		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
}

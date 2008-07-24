package cbit.vcell.microscopy;

import cbit.function.DefaultScalarFunction;
import cbit.vcell.client.server.VCDataManager;
import cbit.vcell.opt.ImplicitObjectiveFunction;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.solvers.OptSolverCallbacks;
import cbit.vcell.opt.solvers.PowellOptimizationSolver;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solver.VCSimulationDataIdentifier;

public class FRAPOptimization {
	
	static double FTOL = 1.0e-6;
	static double epsilon = 1e-8;
		
	public static double[][] dataReduction(FRAPData argFrapData, boolean bRemovePrebleach,int argStartRecoveryIndex, ROI[] expRois, double[] normFactor) 
	{ 
		int roiLen = expRois.length;
		int numRefTimePoints = argFrapData.getImageDataset().getSizeT();
		// data set which may has prebleach average and may need to be normalized.
		double[][] baseData = new double[roiLen][numRefTimePoints];
        // data set which is normalized and without prebleach.
		double[][] newData = null;
		
		if(bRemovePrebleach)
		{
			newData = new double[roiLen][numRefTimePoints-argStartRecoveryIndex];
		}
		else
		{
			newData = new double[roiLen][numRefTimePoints];
		}
		for(int i = 0; i < roiLen; i++)
		{
			baseData[i] = FRAPDataAnalysis.getAverageROIIntensity(argFrapData, expRois[i], normFactor,null);

			//remove prebleach
			if(bRemovePrebleach)
			{   
				for(int j = 0; j < numRefTimePoints - argStartRecoveryIndex; j++)
				{
					newData[i][j]  = baseData[i][j+argStartRecoveryIndex];
				}
			}
			else
			{
				for(int j = 0; j < numRefTimePoints; j++)
				{
					newData[i][j]  = baseData[i][j];
				}
			}
		}
		return newData;
	}

	
	
	
	public static double[][] dataReduction(
			VCDataManager vcDataManager,VCSimulationDataIdentifier vcSimdataID,int argStartRecoveryIndex,
			ROI[] expRois,DataSetControllerImpl.ProgressListener progressListener) throws Exception{ 

		if(progressListener != null){
			progressListener.updateMessage("Reading Reference data, generating ROI averages");
		}
		int roiLen = expRois.length;
		double[] simTimes = vcDataManager.getDataSetTimes(vcSimdataID);
		double[][] newData = new double[roiLen][simTimes.length];

		for (int j = 0; j < simTimes.length; j++) {
			double[] simData = vcDataManager.getSimDataBlock(vcSimdataID, FRAPStudy.SPECIES_NAME_PREFIX_COMBINED,simTimes[j]).getData();
			for(int i = 0; i < roiLen; i++){
				newData[i][j] = AnnotatedImageDataset.getAverageUnderROI(simData, expRois[i].getPixelsXYZ(), null,0.0);
			}
			if(progressListener != null){
				progressListener.updateProgress(((double)(j+1))/(double)simTimes.length);
			}
		}
		return newData;
	}
	
	 //remove prebleach time points
	public static double[] timeReduction(double[] argTimeStamps, int argStartRecoveryIndex )
	{
		double[] newTimeStamps = new double[argTimeStamps.length - argStartRecoveryIndex];
		for(int i = 0; i < (argTimeStamps.length - argStartRecoveryIndex); i++)
		{
			newTimeStamps[i] = argTimeStamps[i+argStartRecoveryIndex]-argTimeStamps[argStartRecoveryIndex];
		}
		return newTimeStamps;
	}
	
	public static double getValueFromParameters(double diffData, double mobileFrac, double bleachWhileMonitoringRate, double  firstPostBleach, double timePoint)
	{
		double imMobileFrac = 1 - mobileFrac;
		double result = (mobileFrac * diffData + imMobileFrac * firstPostBleach) * Math.exp(-(bleachWhileMonitoringRate * timePoint));
		
		return result;
	}
	
	public static double getErrorByNewParameters(double refDiffRate, double[] newParams, double[][] refData, double[][] expData, double[] refTimePoints, double[] expTimePoints, int roiLen, double refTimeInterval, boolean[] errorOfInterest) throws Exception
	{
		double error = 0;
		double diffRate = 0;
		double[][] diffData = null;
		double mobileFrac = 1;
		double bleachWhileMonitoringRate = 0;
		if(newParams != null && newParams.length > 0)
		{
			diffRate = newParams[FRAPOptData.DIFFUSION_RATE_INDEX];
			mobileFrac = Math.min(newParams[FRAPOptData.MOBILE_FRACTION_INDEX], 1);
			bleachWhileMonitoringRate = newParams[FRAPOptData.BLEACH_WHILE_MONITOR_INDEX];
			double imMobileFrac = Math.max((1 - mobileFrac), 0);
			
			diffData = FRAPOptimization.getValueByDiffRate(refDiffRate,
                    diffRate,
                    refData,
                    expData,
                    refTimePoints,
                    expTimePoints,
                    roiLen,
                    refTimeInterval);
			//get diffusion initial condition for immobile part
			double[] firstPostBleach = new double[roiLen];
			if(diffData != null)
			{
				for(int i = 0; i < roiLen; i++)
				{
					firstPostBleach[i] = diffData[i][0];
				}
			}
			//compute error against exp data
			if(errorOfInterest != null)
			{
				for(int i=0; i<roiLen; i++)
				{
					if(errorOfInterest[i])
					{
						for(int j=0; j<expTimePoints.length; j++)
						{
							double difference = expData[i][j] - getValueFromParameters(diffData[i][j], mobileFrac, bleachWhileMonitoringRate, firstPostBleach[i], expTimePoints[j]);
//							double difference = expData[i][j]- (mobileFrac * diffData[i][j] + imMobileFrac * firstPostBleach[i]) * Math.exp(-(bleachWhileMonitoringRate*expTimePoints[j]));
							error = error + difference * difference;
						}
					}
				}
			}
			return error;
		}
		else
		{
			throw new Exception("Cannot perform optimization because there is no parameters to be evaluated.");
		}
		
		//trying 5 parameters
		/*double diffFastOffset = newParams[0];
		double mFracFast = newParams[1];
		double diffSlowRate = newParams[2];
		double mFracSlow = newParams[3];
		double monitoringRate = newParams[4];
		
		double[][] fastData = null;
		double[][] slowData = null;
		
		if(newParams != null && newParams.length > 0)
		{
			double diffFastRate = diffSlowRate + diffFastOffset;
			double immobileFrac = 1- mFracFast - mFracSlow;
						
			fastData = FRAPOptimization.getValueByDiffRate(refDiffRate,
                    diffFastRate,
                    refData,
                    expData,
                    refTimePoints,
                    expTimePoints,
                    roiLen,
                    refTimeInterval);
			
			slowData = FRAPOptimization.getValueByDiffRate(refDiffRate,
                    diffSlowRate,
                    refData,
                    expData,
                    refTimePoints,
                    expTimePoints,
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
			//compute error against exp data
			for(int i=0; i<roiLen; i++)
			{
				if(errorOfInterest != null && errorOfInterest[i])
				{
					for(int j=0; j<expTimePoints.length; j++)
					{
						double newValue = (mFracFast * fastData[i][j] + mFracSlow * slowData[i][j] + immobileFrac * firstPostBleach[i]) * Math.exp(-(monitoringRate * expTimePoints[j]));
						double difference = expData[i][j] - newValue;
						error = error + difference * difference;
					}
				}
			}
			return error;
		}
		else
		{
			throw new Exception("Cannot perform optimization because there is no parameters to be evaluated.");
		}*/
		
	}
	
	public static double[][] getValueByDiffRate(double refDiffRate, double newDiffRate, double[][] refData, double[][] expData, double[] refTimePoints, double[] expTimePoints, int roiLen, double refTimeInterval) throws Exception
	{
		double[][] result = new double[roiLen][expTimePoints.length];
		int preTimeIndex = 0;
		int postTimeIndex = 0;
				
		for(int j = 0; j < expTimePoints.length; j++)
		{	
			// find corresponding time points in reference data by diffusion rate
			double estimateTime = (newDiffRate/refDiffRate) * expTimePoints[j];
			preTimeIndex =(int) (estimateTime / refTimeInterval);
			
			if(preTimeIndex < 0)//negtive newDiffRate will cause array index out of bound exception
			{
				preTimeIndex = 0;
				postTimeIndex = preTimeIndex;
			}
			else if(preTimeIndex >= 0 && preTimeIndex < (refTimePoints.length - 1))
			{
				if((estimateTime > (refTimePoints[preTimeIndex] - FRAPOptimization.epsilon)) &&  (estimateTime  < (refTimePoints[preTimeIndex] + FRAPOptimization.epsilon)))
				{
					postTimeIndex = preTimeIndex;
				}
				else 
				{
					postTimeIndex = preTimeIndex + 1;
				}
			}
			else//set value to last time point in reference data if the time is already the last ref time or exceed it  
			{
				preTimeIndex = refTimePoints.length -1;
				postTimeIndex = preTimeIndex;
			}
			double preTimeInRefData = refTimePoints[preTimeIndex];
			double postTimeInRefData = refTimePoints[postTimeIndex];
			double proportion = 0;
			if((postTimeInRefData-preTimeInRefData) == 0)
			{
				proportion = ((estimateTime-preTimeInRefData)/FRAPOptimization.epsilon);
			}
			else
			{
				proportion = ((estimateTime-preTimeInRefData)/(postTimeInRefData-preTimeInRefData));
			}
			
			for(int i = 0; i < roiLen; i++) 
			{
				//get data from reference data according to the estimate time.
				if(preTimeIndex == postTimeIndex)
				{
					result[i][j] = refData[i][preTimeIndex];
				}
				else 
				{
					double preDataValue = refData[i][preTimeIndex];
					double postDataValue = refData[i][postTimeIndex];
					double estimateDataValue = preDataValue + proportion *(postDataValue-preDataValue);
					result[i][j] = estimateDataValue;
				}
			}
		}
		return result;
	}
	
	public static void estimate(FRAPOptData argOptData, Parameter[] inParams, String[] outParaNames, double[] outParaVals, boolean[] eoi) throws Exception
	{
		/*PowellSolver  solver = new PowellSolver();
		LookupTableObjectiveFunction func = new LookupTableObjectiveFunction(argOptData);
		
		//best point found. we have only one dimension here.
		double[] p = new double[1];
		p[0] = iniDiffGuess;
		//current direction set
		double[][] xi = new double[1][1];
		for (int i=0;i<1;i++)
		{
		   for (int j=0;j<1;j++)
		   {
		       xi[i][j]=(i == j ? 1.0 : 0.0);
		   }
		}
		//run powell with initial guess, initial direction set and objective function
		double minError = solver.powell(1, p, xi, FTOL, func);
		parameters[FRAPOptData.idxOptDiffRate] = p[0];
		parameters[FRAPOptData.idxMinError] = minError;*/
		
		// create optimization solver 
		PowellOptimizationSolver optSolver = new PowellOptimizationSolver();
		// create optimization spec
		OptimizationSpec optSpec = new OptimizationSpec();
		DefaultScalarFunction scalarFunc = new LookupTableObjectiveFunction(argOptData, eoi); 
		optSpec.setObjectiveFunction(new ImplicitObjectiveFunction(scalarFunc));
		
		for (int i = 0; i < inParams.length; i++) {
			optSpec.addParameter(inParams[i]);
		}
		// create solver spec 
		OptimizationSolverSpec optSolverSpec = new OptimizationSolverSpec(OptimizationSolverSpec.SOLVERTYPE_POWELL, FRAPOptimization.FTOL);
		// create solver call back
		OptSolverCallbacks optSolverCallbacks = new OptSolverCallbacks();
		// create optimization result set
		OptimizationResultSet optResultSet = null;
		optResultSet = optSolver.solve(optSpec, optSolverSpec, optSolverCallbacks);
		 
		// copy results to output parameters
		String[] names = optResultSet.getParameterNames();
		double[] values = optResultSet.getParameterValues();
		for (int i = 0; i < names.length; i++) 
		{
			outParaNames[i] = names[i];
			outParaVals[i] = values[i];
		}
	}
	
}

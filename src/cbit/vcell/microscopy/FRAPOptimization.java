package cbit.vcell.microscopy;

import cbit.function.DefaultScalarFunction;
import cbit.vcell.opt.ImplicitObjectiveFunction;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.solvers.OptSolverCallbacks;
import cbit.vcell.opt.solvers.PowellOptimizationSolver;

public class FRAPOptimization {
	
	static double FTOL = 1.0e-6;
	static double epsilon = 1e-8;
		
	public static double[][] dataReduction(FRAPData argFrapData, boolean bRemovePrebleach, double argScaleFactor, int argStartRecoveryIndex, ROI[] expRois, double[] normFactor) 
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
			baseData[i] = FRAPDataAnalysis.getAverageROIIntensity(argFrapData, expRois[i], normFactor);
			// unscale the data from vfrap
			if(argScaleFactor != 1 && argScaleFactor != 0)
			{
				for(int j=0; j<baseData[i].length; j++)
				{
					baseData[i][j] = baseData[i][j]/argScaleFactor;
				}
			}
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
	
	public static double getErrorByNewParameters(double refDiffRate, double[] newParams, double[][] refData, double[][] expData, double[] refTimePoints, double[] expTimePoints, int roiLen, double refTimeInterval) throws Exception
	{
		double error = 0;
		double diffRate = 0;
		double[][] diffData = null;
		double mobileFrac = 1;
		double bleachWhileMonitoringRate = 0;
		if(newParams != null && newParams.length > 0)
		{
			diffRate = newParams[FRAPOptData.idxDiffRate];
			mobileFrac = Math.min(newParams[FRAPOptData.idxMobileFrac], 1);
			bleachWhileMonitoringRate = newParams[FRAPOptData.idxBleachWhileMonitoringRate];
			double imMobileFrac = Math.max((1 - mobileFrac), 0);
			
			diffData = FRAPOptimization.getValueByDiffRate(FRAPOptData.refDiffRate,
                    diffRate,
                    refData,
                    expData,
                    refTimePoints,
                    expTimePoints,
                    roiLen,
                    refTimeInterval);
			//get diffusion initial condition for immobile part
			double[] firstPostBleach = new double[roiLen];
			for(int i = 0; i < roiLen; i++)
			{
				firstPostBleach[i] = diffData[i][0];
			}
			//compute error against exp data
			for(int i=0; i<roiLen; i++)
			{
				for(int j=0; j<expTimePoints.length; j++)
				{
					double difference = expData[i][j] - getValueFromParameters(diffData[i][j], mobileFrac, bleachWhileMonitoringRate, firstPostBleach[i], expTimePoints[j]);
//					double difference = expData[i][j]- (mobileFrac * diffData[i][j] + imMobileFrac * firstPostBleach[i]) * Math.exp(-(bleachWhileMonitoringRate*expTimePoints[j]));

					error = error + difference * difference;
				}
			}
			return Math.sqrt(error);
		}
		else
		{
			throw new Exception("Cannot perform optimization because there is no parameters to be evaluated.");
		}
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
			
			if(preTimeIndex < (refTimePoints.length - 1))
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
			double proportion = ((estimateTime-preTimeInRefData)/(postTimeInRefData-preTimeInRefData));
			
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
	
	public static void estimate(FRAPOptData argOptData, Parameter[] inParams, String[] outParaNames, double[] outParaVals) throws Exception
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
		DefaultScalarFunction scalarFunc = new LookupTableObjectiveFunction(argOptData); 
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

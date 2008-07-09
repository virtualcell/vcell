package cbit.vcell.microscopy;

import cbit.vcell.opt.solvers.PowellSolver;

public class FRAPOptimization {
	
	static double FTOL = 1.0e-6;
	static double epsilon = 1e-8;
	static double bigValue = 1e8;
	
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
	
	public static double getErrorByNewDiffRate(double refDiffRate, double newDiffRate, double[][] refData, double[][] expData, double[] refTimePoints, double[] expTimePoints, int roiLen, double refTimeInterval) throws Exception
	{
		double error = 0;
		double[][] result = new double[roiLen][expTimePoints.length];
		int preTimeIndex = 0;
		int postTimeIndex = 0;
		
		for(int j = 0; j < expTimePoints.length; j++)
		{	
			// find corresponding time points in meta data.
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
				//get data from meta data according to the estimate time.
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
				double difference = result[i][j] - expData[i][j];
				error = error + difference * difference;
			}
		}
		return Math.sqrt(error);
	}
	
	public static void estimate(FRAPOptData argOptData, double iniDiffGuess, double[] parameters)
	{
		PowellSolver  solver = new PowellSolver();
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
		parameters[FRAPOptData.idxMinError] = minError;
	}
	
}

package org.vcell.vmicro.workflow.data;

import cbit.vcell.opt.Parameter;

public class OptModelTwoDiffWithoutPenalty extends RefSimOptModel {
	
	private static final String NAME = "diffusion with two diffusing components";
	private final static int INDEX_PRIMARY_DIFF_RATE = 0;
	private final static int INDEX_TOTAL_MOBILE_FRACTION = 1;
	private final static int INDEX_BLEACH_MONITOR_RATE = 2;
	private final static int INDEX_SECONDARY_DIFF_RATE_MULTIPLIER = 3;
	private final static int INDEX_SECONDARY_FRACTION_OF_MOBILE = 4;
	
	private final static String[] MODEL_PARAMETER_NAMES = {
		"Primary_diffusion_rate",
		"Primary_mobile_fraction",
		"Bleach_while_monitoring_rate",
		"Secondary_diffusion_rate", // Secondary_diffusion_rate_multiplier
		"Secondary_mobile_fraction",
	};
	private final static double penalty = 1e4;


	public OptModelTwoDiffWithoutPenalty(double[][] refData, double[] refTimePoints, double refDiffusionRate) {
		super(NAME, new Parameter[] {
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_DIFF_RATE], 				0.5,	200,	1.0,	1.0),
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_TOTAL_MOBILE_FRACTION],			0.1,	1.0,	1.0,	1.0),
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_MONITOR_RATE],				0.0,	1.0,	1.0,	0),
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_DIFF_RATE_MULTIPLIER],	2.0,	100,	1.0,	2.0),
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_FRACTION_OF_MOBILE],	0.1,	0.9,	1.0,	0.5),
			},
			refData, refTimePoints, refDiffusionRate);
	}

	private static double getValueFromParameters_twoDiffRates(double mFracFast, double fastData, double mFracSlow, double slowData, double bleachWhileMonitoringRate, double  firstPostBleach, double timePoint)
	{
		double immobileFrac = 1 - mFracFast - mFracSlow;
		double result = (mFracFast * fastData + mFracSlow * slowData + immobileFrac * firstPostBleach) * Math.exp(-(bleachWhileMonitoringRate * timePoint));
		
		return result;
	}
	
	@Override
	protected double[][] getSolution0(double[] newParams, double[] solutionTimePoints) {

		double primaryDiffRate = newParams[INDEX_PRIMARY_DIFF_RATE];
		double totalMobileFraction = newParams[INDEX_TOTAL_MOBILE_FRACTION];
		double monitoringRate = newParams[INDEX_BLEACH_MONITOR_RATE];
		double secondaryDiffRateMultiplier = newParams[INDEX_SECONDARY_DIFF_RATE_MULTIPLIER];
		double secondaryMobileFractionOfMobile = newParams[INDEX_SECONDARY_FRACTION_OF_MOBILE];
		
		double secondaryDiffRate = primaryDiffRate * secondaryDiffRateMultiplier;
		double primaryMobileFraction = totalMobileFraction * (1.0 - secondaryMobileFractionOfMobile);
		double secondaryMobileFraction = totalMobileFraction * secondaryMobileFractionOfMobile;
		
		double[][] fastData = getValueByDiffRate(primaryDiffRate,solutionTimePoints);

		double[][] slowData = getValueByDiffRate(secondaryDiffRate,solutionTimePoints);
		
		int numROIs = getNumROIs();
		// get initial condition for immobile part
		double[] firstPostBleach = new double[numROIs];
		if (fastData != null) {
			for (int i = 0; i < numROIs; i++) {
				firstPostBleach[i] = fastData[i][0];
			}
		}

		double[][] solutionData = new double[numROIs][solutionTimePoints.length];
		
		// evaluate solution at each point in time and roi
		for (int i = 0; i < numROIs; i++) {
			for (int j = 0; j < solutionTimePoints.length; j++) {
				double value = getValueFromParameters_twoDiffRates(
						primaryMobileFraction, 
						fastData[i][j], 
						secondaryMobileFraction, 
						slowData[i][j], 
						monitoringRate, 
						firstPostBleach[i], 
						solutionTimePoints[j]);
				solutionData[i][j] = value;
			}
		}
		return solutionData;
	}

	@Override
	public double getPenalty(double[] newParams) {
		return 0.0;
	
//		double primaryDiffRate = 0;
//		double totalMobileFraction = 1;
//		double monitoringRate = 0;
//		double secondaryDiffRateMultiplier = 0;
//		double secondaryMobileFractionOfMobile = 1;
//
//		if (newParams.length==4){
//			if(fixedParameter.getName().equals(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_DIFF_RATE]))
//			{
//				primaryDiffRate = fixedParameter.getInitialGuess();
//				totalMobileFraction = newParams[INDEX_TOTAL_MOBILE_FRACTION - 1];
//				monitoringRate = newParams[INDEX_BLEACH_MONITOR_RATE - 1];
//				secondaryDiffRateMultiplier = newParams[INDEX_SECONDARY_DIFF_RATE_MULTIPLIER - 1];
//				secondaryMobileFractionOfMobile = newParams[INDEX_SECONDARY_FRACTION_OF_MOBILE - 1];
//			}
//			else if(fixedParameter.getName().equals(MODEL_PARAMETER_NAMES[INDEX_TOTAL_MOBILE_FRACTION]))
//			{
//				primaryDiffRate = newParams[INDEX_PRIMARY_DIFF_RATE];
//				totalMobileFraction = fixedParameter.getInitialGuess();
//				monitoringRate = newParams[INDEX_BLEACH_MONITOR_RATE - 1];
//				secondaryDiffRateMultiplier = newParams[INDEX_SECONDARY_DIFF_RATE_MULTIPLIER - 1];
//				secondaryMobileFractionOfMobile = newParams[INDEX_SECONDARY_FRACTION_OF_MOBILE - 1];
//			}
//			else if(fixedParameter.getName().equals(MODEL_PARAMETER_NAMES[INDEX_BLEACH_MONITOR_RATE]))
//			{
//				primaryDiffRate = newParams[INDEX_PRIMARY_DIFF_RATE];
//				totalMobileFraction = newParams[INDEX_TOTAL_MOBILE_FRACTION];
//				monitoringRate = fixedParameter.getInitialGuess(); 
//				secondaryDiffRateMultiplier = newParams[INDEX_SECONDARY_DIFF_RATE_MULTIPLIER - 1];
//				secondaryMobileFractionOfMobile = newParams[INDEX_SECONDARY_FRACTION_OF_MOBILE - 1];
//			} 
//			else if(fixedParameter.getName().equals(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_DIFF_RATE_MULTIPLIER]))
//			{
//				primaryDiffRate = newParams[INDEX_PRIMARY_DIFF_RATE];
//				totalMobileFraction = newParams[INDEX_TOTAL_MOBILE_FRACTION];
//				monitoringRate = newParams[INDEX_BLEACH_MONITOR_RATE];
//				secondaryDiffRateMultiplier = fixedParameter.getInitialGuess();
//				secondaryMobileFractionOfMobile = newParams[INDEX_SECONDARY_FRACTION_OF_MOBILE - 1];
//			}
//			else if(fixedParameter.getName().equals(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_FRACTION_OF_MOBILE]))
//			{
//				primaryDiffRate = newParams[INDEX_PRIMARY_DIFF_RATE];
//				totalMobileFraction = newParams[INDEX_TOTAL_MOBILE_FRACTION];
//				monitoringRate = newParams[INDEX_BLEACH_MONITOR_RATE];
//				secondaryDiffRateMultiplier = newParams[INDEX_SECONDARY_DIFF_RATE_MULTIPLIER];
//				secondaryMobileFractionOfMobile = fixedParameter.getInitialGuess();
//			}
//		}else{
//			primaryDiffRate = newParams[INDEX_PRIMARY_DIFF_RATE];
//			totalMobileFraction = newParams[INDEX_TOTAL_MOBILE_FRACTION];
//			monitoringRate = newParams[INDEX_BLEACH_MONITOR_RATE];
//			secondaryDiffRateMultiplier = newParams[INDEX_SECONDARY_DIFF_RATE_MULTIPLIER];
//			secondaryMobileFractionOfMobile = newParams[INDEX_SECONDARY_FRACTION_OF_MOBILE];
//		}
//		
//		double secondaryDiffRate = primaryDiffRate * secondaryDiffRateMultiplier;
//		double primaryMobileFraction = totalMobileFraction * (1.0 - secondaryMobileFractionOfMobile);
//		double secondaryMobileFraction = totalMobileFraction * secondaryMobileFractionOfMobile;
//		//add penalty for wrong parameter set
//		double error = 0.0;
//		if(primaryMobileFraction + secondaryMobileFraction > 1)
//		{
//			double mFracError = (primaryMobileFraction + secondaryMobileFraction - 1);
//			error = error + (mFracError + mFracError * mFracError) * penalty;
//		}
//		if(secondaryDiffRate > primaryDiffRate)
//		{
//			double rateError = secondaryDiffRate - primaryDiffRate;
//			error = error + (rateError + rateError * rateError) * penalty;
//		}
//		return error;
	}

}

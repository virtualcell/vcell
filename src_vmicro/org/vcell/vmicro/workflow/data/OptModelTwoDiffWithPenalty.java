package org.vcell.vmicro.workflow.data;

import cbit.vcell.opt.Parameter;

public class OptModelTwoDiffWithPenalty extends RefSimOptModel {
	
	private static final String NAME = "diffusion with two diffusing components";
	private final static int INDEX_PRIMARY_DIFF_RATE = 0;
	private final static int INDEX_PRIMARY_MOBILE_FRACTION = 1;
	private final static int INDEX_BLEACH_MONITOR_RATE = 2;
	private final static int INDEX_SECONDARY_DIFF_RATE = 3;
	private final static int INDEX_SECONDARY_MOBILE_FRACTION = 4;

	private final static String[] MODEL_PARAMETER_NAMES = {
		"Primary_diffusion_rate",
		"Primary_mobile_fraction",
		"Bleach_while_monitoring_rate",
		"Secondary_diffusion_rate",
		"Secondary_mobile_fraction",
	};
	private final static double penalty = 1e4;


	public OptModelTwoDiffWithPenalty(double[][] refData, double[] refTimePoints, double refDiffusionRate) {
		super(NAME, new Parameter[] {
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_DIFF_RATE], 				0.0,	200,	1.0,	1.0),
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_MOBILE_FRACTION],			0.0,	1.0,	1.0,	1.0),
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_MONITOR_RATE],				0.0,	1.0,	1.0,	0),
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_DIFF_RATE],				0.0,	100,	1.0,	1.0),
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_MOBILE_FRACTION],		0.0,	1.0,	1.0,	1.0),
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
	public double[][] getSolution0(double[] newParams, double[] solutionTimePoints) {

		double primaryDiffRate = 0;
		double primaryMobileFraction = 0;
		double monitoringRate = 0;
		double secondaryDiffRate = 0;
		double secondaryMobileFraction = 0;

		primaryDiffRate = newParams[INDEX_PRIMARY_DIFF_RATE];
		primaryMobileFraction = newParams[INDEX_PRIMARY_MOBILE_FRACTION];
		monitoringRate = newParams[INDEX_BLEACH_MONITOR_RATE];
		secondaryDiffRate = newParams[INDEX_SECONDARY_DIFF_RATE];
		secondaryMobileFraction = newParams[INDEX_SECONDARY_MOBILE_FRACTION];
		
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
		double primaryDiffRate = 0;
		double primaryMobileFraction = 0;
		double monitoringRate = 0;
		double secondaryDiffRate = 0;
		double secondaryMobileFraction = 0;

		if (newParams.length==4){
			if(isFixedParameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_DIFF_RATE]))
			{
				primaryDiffRate = getFixedParameterValue();
				primaryMobileFraction = newParams[INDEX_PRIMARY_MOBILE_FRACTION - 1];
				monitoringRate = newParams[INDEX_BLEACH_MONITOR_RATE - 1];
				secondaryDiffRate = newParams[INDEX_SECONDARY_DIFF_RATE - 1];
				secondaryMobileFraction = newParams[INDEX_SECONDARY_MOBILE_FRACTION - 1];
			}
			else if(isFixedParameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_MOBILE_FRACTION]))
			{
				primaryDiffRate = newParams[INDEX_PRIMARY_DIFF_RATE];
				primaryMobileFraction = getFixedParameterValue();
				monitoringRate = newParams[INDEX_BLEACH_MONITOR_RATE - 1];
				secondaryDiffRate = newParams[INDEX_SECONDARY_DIFF_RATE - 1];
				secondaryMobileFraction = newParams[INDEX_SECONDARY_MOBILE_FRACTION - 1];
			}
			else if(isFixedParameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_MONITOR_RATE]))
			{
				primaryDiffRate = newParams[INDEX_PRIMARY_DIFF_RATE];
				primaryMobileFraction = newParams[INDEX_PRIMARY_MOBILE_FRACTION];
				monitoringRate = getFixedParameterValue(); 
				secondaryDiffRate = newParams[INDEX_SECONDARY_DIFF_RATE - 1];
				secondaryMobileFraction = newParams[INDEX_SECONDARY_MOBILE_FRACTION - 1];
			} 
			else if(isFixedParameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_DIFF_RATE]))
			{
				primaryDiffRate = newParams[INDEX_PRIMARY_DIFF_RATE];
				primaryMobileFraction = newParams[INDEX_PRIMARY_MOBILE_FRACTION];
				monitoringRate = newParams[INDEX_BLEACH_MONITOR_RATE];
				secondaryDiffRate = getFixedParameterValue();
				secondaryMobileFraction = newParams[INDEX_SECONDARY_MOBILE_FRACTION - 1];
			}
			else if(isFixedParameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_MOBILE_FRACTION]))
			{
				primaryDiffRate = newParams[INDEX_PRIMARY_DIFF_RATE];
				primaryMobileFraction = newParams[INDEX_PRIMARY_MOBILE_FRACTION];
				monitoringRate = newParams[INDEX_BLEACH_MONITOR_RATE];
				secondaryDiffRate = newParams[INDEX_SECONDARY_DIFF_RATE];
				secondaryMobileFraction = getFixedParameterValue();
			}
		}else{
			primaryDiffRate = newParams[INDEX_PRIMARY_DIFF_RATE];
			primaryMobileFraction = newParams[INDEX_PRIMARY_MOBILE_FRACTION];
			monitoringRate = newParams[INDEX_BLEACH_MONITOR_RATE];
			secondaryDiffRate = newParams[INDEX_SECONDARY_DIFF_RATE];
			secondaryMobileFraction = newParams[INDEX_SECONDARY_MOBILE_FRACTION];
		}

		//add penalty for wrong parameter set
		double error = 0.0;
		if(primaryMobileFraction + secondaryMobileFraction > 1)
		{
			double mFracError = (primaryMobileFraction + secondaryMobileFraction - 1);
			error = error + (mFracError + mFracError * mFracError) * penalty;
		}
		if(secondaryDiffRate > primaryDiffRate)
		{
			double rateError = secondaryDiffRate - primaryDiffRate;
			error = error + (rateError + rateError * rateError) * penalty;
		}
		return error;
	}

}

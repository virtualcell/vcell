package org.vcell.vmicro.workflow.data;

import cbit.vcell.opt.Parameter;

public class OptModelOneDiff extends RefSimOptModel {

	private static final String NAME = "diffusion with one diffusing component";
	private final static int INDEX_PRIMARY_DIFF_RATE = 0;
	private final static int INDEX_PRIMARY_FRACTION = 1;
	private final static int INDEX_BLEACH_MONITOR_RATE = 2;
	
	private final static String[] MODEL_PARAMETER_NAMES = {
		"Primary_diffusion_rate",
		"Primary_mobile_fraction",
		"Bleach_while_monitoring_rate",
	};


	public OptModelOneDiff(double[][] refData, double[] refTimePoints, double refDiffusionRate) {
		super(NAME, new Parameter[] {
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_DIFF_RATE], 0, 200, 1.0, 1.0),
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_FRACTION], 0, 1, 1.0, 1.0),
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_MONITOR_RATE], 0, 1, 1.0,  0),
			},
			refData, refTimePoints, refDiffusionRate);
	}

	private static double getValueFromParameters_oneDiffRate(double diffData, double mobileFrac, double bleachWhileMonitoringRate, double  firstPostBleach, double timePoint)
	{
		double imMobileFrac = 1 - mobileFrac;
		double result = (mobileFrac * diffData + imMobileFrac * firstPostBleach) * Math.exp(-(bleachWhileMonitoringRate * timePoint));
		
		return result;
	}

	@Override
	public double[][] getSolution0(double[] newParams, double[] solutionTimePoints) {

		double diffRate = newParams[INDEX_PRIMARY_DIFF_RATE];
		double mobileFrac = newParams[INDEX_PRIMARY_FRACTION];
		double bleachWhileMonitoringRate = newParams[INDEX_BLEACH_MONITOR_RATE];
		
		double[][] diffData = getValueByDiffRate(diffRate,solutionTimePoints);
		
		int numROIs = getNumROIs();
		// get initial condition for immobile part
		double[] firstPostBleach = new double[numROIs];
		if (diffData != null) {
			for (int i = 0; i < numROIs; i++) {
				firstPostBleach[i] = diffData[i][0];
			}
		}

		double[][] solutionData = new double[numROIs][solutionTimePoints.length];
		
		// evaluate solution at each point in time and roi
		for (int i = 0; i < numROIs; i++) {
			for (int j = 0; j < solutionTimePoints.length; j++) {
				double value = getValueFromParameters_oneDiffRate(
									diffData[i][j], 
									mobileFrac,
									bleachWhileMonitoringRate,
									firstPostBleach[i],
									solutionTimePoints[j]);
				solutionData[i][j] = value;
			}
		}
		return solutionData;
	}

	@Override
	public double getPenalty(double[] parameters2) {
		return 0;
	}

}

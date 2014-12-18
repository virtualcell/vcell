package org.vcell.vmicro.workflow.data;

import cbit.vcell.opt.Parameter;
import cern.jet.math.Bessel;

public class OptModelKenworthyUniformDisk4P extends OptModel {

	private static final String NAME = "uniform disk bleach area fluorescence with bleach while monitoring";
	private final static int INDEX_DIFFUSION_RATE = 0;
	private final static int INDEX_BLEACH_AMPLITUDE = 1;
	private final static int INDEX_TIME_OFFSET = 2;
	private final static int INDEX_BLEACH_MONITOR_RATE = 3;
	
	private final static String[] MODEL_PARAMETER_NAMES = {
		"DiffusionRate",
		"BleachAmplitude",
		"TimeOffset",
		"BleachWhileMonitoringRate",
	};
	
	private final double bleachingRadius;


	public OptModelKenworthyUniformDisk4P(double bleachingRadius) {
		super(NAME, new Parameter[] {
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_DIFFUSION_RATE], 0.1, 200, 1.0, 1.0),
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_AMPLITUDE], 0.01, 1, 1.0, 0.5),
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_TIME_OFFSET], 0.0, 3.0, 1.0,  0.5),
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_MONITOR_RATE], 0.0001, 0.1, 1.0,  0.001),
			});
		this.bleachingRadius = bleachingRadius;
	}

	private double getValueFromParameters(double diffusion, double bleachAmplitude, double bleachWhileMonitoringRate, double t)
	{
		if (diffusion<=-1e-5){
			throw new RuntimeException("diffusion must be non-negative, diff = "+diffusion);
		}
		double tau = bleachingRadius*bleachingRadius/(4*diffusion);
		double arg = 2*tau/t;
		//
		// F(t) = exp(-2*tau/t)*(I0(2*tau/t) + I1(2*tau/t))
		//
		double I0e_value = Bessel.i0e(arg);  // exp(-x)*I0(x)
		double I1e_value = Bessel.i1e(arg);  // exp(-x)*I1(x)
		double result = Math.exp(-t*bleachWhileMonitoringRate) * ((1-bleachAmplitude) + bleachAmplitude*(I0e_value + I1e_value));
		
		return result;
	}

	/**
	 * returns the expected fluorescence under the bleaching area for a uniform disk
	 */
	@Override
	public double[][] getSolution(double[] newParams, double[] solutionTimePoints) {

		double diffusionRate;
		double bleachAmplitude; // 0 to 1 (1 means complete bleaching)
		double timeOffset;
		double bleachWhileMonitoringRate;

		if (newParams.length==3){
			if (isFixedParameter(MODEL_PARAMETER_NAMES[INDEX_DIFFUSION_RATE])){
				diffusionRate = getFixedParameterValue();
				bleachAmplitude = newParams[0];
				timeOffset = newParams[1];
				bleachWhileMonitoringRate = newParams[2];
			}else if (isFixedParameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_AMPLITUDE])){
				diffusionRate = newParams[0];
				bleachAmplitude = getFixedParameterValue();
				timeOffset = newParams[1];
				bleachWhileMonitoringRate = newParams[2];
			}else if (isFixedParameter(MODEL_PARAMETER_NAMES[INDEX_TIME_OFFSET])){
				diffusionRate = newParams[0];
				bleachAmplitude = newParams[1];
				timeOffset = getFixedParameterValue();
				bleachWhileMonitoringRate = newParams[2];
			}else if (isFixedParameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_MONITOR_RATE])){
				diffusionRate = newParams[0];
				bleachAmplitude = newParams[1];
				timeOffset = newParams[2];
				bleachWhileMonitoringRate = getFixedParameterValue();
			}else{
				throw new RuntimeException("unexpected fixed parameter");
			}
		}else{
			diffusionRate = newParams[INDEX_DIFFUSION_RATE];
			bleachAmplitude = newParams[INDEX_BLEACH_AMPLITUDE];
			timeOffset = newParams[INDEX_TIME_OFFSET];
			bleachWhileMonitoringRate = newParams[INDEX_BLEACH_MONITOR_RATE];
		}
		
		double[][] solutionData = new double[1][solutionTimePoints.length];
		
		for (int j = 0; j < solutionTimePoints.length; j++) {
			double value = getValueFromParameters(diffusionRate, bleachAmplitude, bleachWhileMonitoringRate, solutionTimePoints[j]+timeOffset);
			solutionData[0][j] = value;
		}
		return solutionData;
	}

	@Override
	public double getPenalty(double[] parameters2) {
		return 0;
	}

}

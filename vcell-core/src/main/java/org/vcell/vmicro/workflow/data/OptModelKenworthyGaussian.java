package org.vcell.vmicro.workflow.data;

import cbit.vcell.opt.Parameter;
import cern.jet.stat.Gamma;

public class OptModelKenworthyGaussian extends OptModel {

	private static final String NAME = "uniform disk bleach area fluorescence with bleach while monitoring";
	private final static int INDEX_DIFFUSION_RATE = 0;
	private final static int INDEX_BLEACH_DEPTH_K = 1;
//	private final static int INDEX_BLEACH_WHILE_MONITORING_RATE = 2;
	
	private final static String[] MODEL_PARAMETER_NAMES = {
		"DiffusionRate",
		"BleachDepthK",
//		"BleachWhileMonitoringRate",
	};
	
	private final double Fi; // prebleach fluorescence
	private final double re; // effective radius
	private final double rn; // nominal radius
	
	public OptModelKenworthyGaussian(double prebleachFluorescence, double bleachingDepthParameter, double effectiveRadius, double nominalRadius) {
		super(NAME, new Parameter[] {
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_DIFFUSION_RATE], 0.1, 200, 1.0, 1.0),
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_DEPTH_K], 0.000001, 40, 1.0, 1.0),
//				new Parameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_WHILE_MONITORING_RATE], 0.000001, 1, 1.0, 0.5),
			});
		this.Fi = prebleachFluorescence;
		this.rn = nominalRadius;
		this.re = effectiveRadius;
	}

	/**
	 * see Supplementary Materials - Appendix B "Derivation of Closed Form" from
	 * "A Generalization of Theory for 2D Fluorescence Recovery after Photobleaching Applicable to Confocal Laser Scanning Microscopes"
	 * 		Minchul Kang, Charles A. Day, Kimberly Drake, Anne K. Kenworthy, and Emmanuele DiBenedetto, Biophysical Journal, Volume 97
	 * 
	 * @param Fi - prebleach fluorescence 
	 * @param K - bleaching depth parameter
	 * @param re - effective radius
	 * @param rn - nominal radius
	 * @param w - bleaching width ????
	 * @param D - diffusion rate
	 * @param t - time
	 * @return
	 */
	private double getValueFromParameters(double Fi, double K, double re, double rn, double D, double bleachWhileMonitoringRate, double t)
	{
		if (D<=-1e-7){
			throw new RuntimeException("diffusion must be non-negative, diff = "+D);
		}
		double re2 = re*re;
		double rn2 = rn*rn;
		//
		// tauD = re2/(4*D)
		// nu = 1.0 / (2*t/tauD + rn2/re2)
		//
		// instead, let's try to remove any problem with D=0,
		//
		// nu = 1.0 / ((2*t*4*D)/re2 + rn2/re2)
		// nu = re2 / (8*D*t + rn2)
		//
		double nu = re2 / (8*D*t + rn2);
		//
		// F(t) = (Fi * nu) / K^nu  * incomplete_lower_gamma(nu,K)
		//
		double result = (Fi * nu) / Math.pow(K, nu) * Gamma.incompleteGamma(nu, K);
		
		//
		// add bleach while monitoring
		//
		result = Math.exp(-t*bleachWhileMonitoringRate) * result;
		
		return result;
	}

	/**
	 * returns the expected fluorescence under the bleaching area for a uniform disk
	 */
	@Override
	public double[][] getSolution0(double[] parameterValues, double[] solutionTimePoints) {

		double diffusionRate = parameterValues[INDEX_DIFFUSION_RATE];
		double bleachDepthK = parameterValues[INDEX_BLEACH_DEPTH_K];
//		double bleachWhileMonitoringRate = parameterValues[INDEX_BLEACH_WHILE_MONITORING_RATE];
		double bleachWhileMonitoringRate = 0;
		
		double[][] solutionData = new double[1][solutionTimePoints.length];
		
		for (int j = 0; j < solutionTimePoints.length; j++) {
			double value = getValueFromParameters(Fi, bleachDepthK, re, rn, diffusionRate, bleachWhileMonitoringRate, solutionTimePoints[j]);
			solutionData[0][j] = value;
		}
		return solutionData;
	}

	@Override
	public double getPenalty(double[] parameters2) {
		return 0;
	}

}

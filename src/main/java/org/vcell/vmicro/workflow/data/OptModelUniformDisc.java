package org.vcell.vmicro.workflow.data;

import cbit.vcell.opt.Parameter;
import cbit.vcell.psf.BesselFunctionsIntegerOrder;

/**
 * uniform disk laser profile
 * @author schaff
 *
 */

public class OptModelUniformDisc extends OptModel {

	private static final double epsilon = 1e-4;
	private static final String NAME = "uniform disc, infinite domain";
	private final static int INDEX_DISC_RADIUS = 0;
	private final static int INDEX_DIFF_RATE = 1;
	
	private final static String[] MODEL_PARAMETER_NAMES = {
		"disc_radius",
		"diff_rate",
	};
	
	public OptModelUniformDisc(double discRadius) {
		super(NAME, new Parameter[] {
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_DISC_RADIUS], discRadius*(1-epsilon), discRadius*(1+epsilon), 1.0, discRadius),
				new Parameter(MODEL_PARAMETER_NAMES[INDEX_DIFF_RATE], 0, 200, 1.0, 1.0),
			});
	}

	@Override
	public double[][] getSolution0(double[] newParams, double[] solutionTimePoints) {
		double discRadius = newParams[INDEX_DISC_RADIUS];
		double diffusionRate = newParams[INDEX_DIFF_RATE];
		
		double tau = discRadius*discRadius/(4*diffusionRate);

		double[][] solutionData = new double[1][solutionTimePoints.length];
		
		// evaluate solution only under bleach spot for each time point
		for (int j = 0; j < solutionTimePoints.length; j++) {
			double t = solutionTimePoints[j];
			double arg = 2*tau/t;
			double value = Math.exp(-arg)*(BesselFunctionsIntegerOrder.I0(arg) + BesselFunctionsIntegerOrder.I1(arg));
			solutionData[0][j] = value;
		}
		return solutionData;
	}

	@Override
	public double getPenalty(double[] parameters2) {
		// TODO Auto-generated method stub
		return 0;
	}

}

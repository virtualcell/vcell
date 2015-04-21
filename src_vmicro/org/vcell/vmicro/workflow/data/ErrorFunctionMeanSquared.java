package org.vcell.vmicro.workflow.data;


public class ErrorFunctionMeanSquared implements ErrorFunction {
	
	@Override
	public double getFittingErrorMetric(double[][] solution, double[][] measurementErrors, double[][] expData, double[] expTimePoints) {	
		double error = 0.0;
		int count = 0;
		for (int i = 0; i < expData.length; i++) {
			for (int j = 0; j < expTimePoints.length; j++) {
				double diff = expData[i][j] - solution[i][j];
				error += diff*diff;
				count++;
			}
		}
		return error/count;
	}
	
}


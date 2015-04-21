package org.vcell.vmicro.workflow.data;


public class ErrorFunctionNoiseWeightedL2 implements ErrorFunction {
	
	@Override
	public double getFittingErrorMetric(double[][] solution, double[][] measurementErrors, double[][] expData, double[] expTimePoints) {	
		double error = 0.0;
		for (int i = 0; i < expData.length; i++) {
			for (int j = 0; j < expTimePoints.length; j++) {
				double difference = expData[i][j] - solution[i][j];
				if (measurementErrors != null) {
					difference = difference	/ measurementErrors[i][j];
				}
				error = error + difference * difference;
			}
		}
		return error;
	}
	
}
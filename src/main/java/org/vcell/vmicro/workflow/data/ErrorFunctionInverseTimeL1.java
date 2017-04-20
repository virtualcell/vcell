package org.vcell.vmicro.workflow.data;


public class ErrorFunctionInverseTimeL1 implements ErrorFunction {
	
	private final double timeOffset;
	
	public ErrorFunctionInverseTimeL1(double timeOffset) {
		this.timeOffset = timeOffset;
	}
	
	@Override
	public double getFittingErrorMetric(double[][] solution, double[][] measurementErrors, double[][] expData, double[] expTimePoints) {	
		double error = 0.0;
		for (int i = 0; i < expData.length; i++) {
			for (int j = 0; j < expTimePoints.length; j++) {
				double pointError = Math.abs(expData[i][j] - solution[i][j])/(expTimePoints[j]+timeOffset);
				error += pointError;
			}
		}
		return error;
	}
	
}
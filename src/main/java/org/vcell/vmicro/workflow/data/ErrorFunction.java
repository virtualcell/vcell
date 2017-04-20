package org.vcell.vmicro.workflow.data;

public interface ErrorFunction {
	double getFittingErrorMetric(double[][] solution, double[][] measurementErrors, double[][] expData, double[] timePoints);
}
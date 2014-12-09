package org.vcell.vmicro.workflow.data;

import cbit.vcell.opt.Parameter;

public class FRAPOptData_Data {
	public int numEstimatedParams;
	public double leastError;
	public boolean bApplyMeasurementError;
	public double[][] dimensionReducedRefData;
	public double[] refDataTimePoints;
	public Parameter fixedParam;
	public double[][] measurementErrors;

	public FRAPOptData_Data(int numEstimatedParams, double leastError,
			boolean bApplyMeasurementError, double[][] dimensionReducedRefData,
			double[] refDataTimePoints, Parameter fixedParam,
			double[][] measurementErrors) {
		this.numEstimatedParams = numEstimatedParams;
		this.leastError = leastError;
		this.bApplyMeasurementError = bApplyMeasurementError;
		this.dimensionReducedRefData = dimensionReducedRefData;
		this.refDataTimePoints = refDataTimePoints;
		this.fixedParam = fixedParam;
		this.measurementErrors = measurementErrors;
	}
}
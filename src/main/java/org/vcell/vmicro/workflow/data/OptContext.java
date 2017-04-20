package org.vcell.vmicro.workflow.data;

import java.util.ArrayList;

import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.opt.Parameter;


public class OptContext {
	private OptModel optModel = null;
	private ErrorFunction errorFunction = null;
	private double[][] expData = null;
	private double[][] measurementErrors = null;
	private double[] expTimePoints = null;

	public OptContext(OptModel optModel, double[][] expData, double[] expTimePoints, double[][] measurementErrors, ErrorFunction errorFunction) {
		this.optModel = optModel;
		this.errorFunction = errorFunction;
		this.expData = expData;
		this.expTimePoints = expTimePoints;
		this.measurementErrors = measurementErrors;
	}
	
	public void setFixedParameter(Parameter fixedParameter, double fixedParameterValue){
		optModel.setFixedParameter(fixedParameter, fixedParameterValue);
	}
	
	public void clearFixedParameter(){
		optModel.clearFixedParameter();
	}
	
	public Parameter[] getParameters(){
		return optModel.getParameters();
	}

	public double computeError(double[] parameters) {

		double[][] solution = optModel.getSolution(parameters, expTimePoints);
		
		double fittingError = errorFunction.getFittingErrorMetric(solution, measurementErrors, expData, expTimePoints);
		
		double error = fittingError + optModel.getPenalty(parameters);
//StringBuffer buffer = new StringBuffer("parameters (");
//for (double p : parameters){
//	buffer.append(p+",");
//}
//buffer.append(") = "+error);
//System.err.println(buffer.toString());
		return error;
	}
	
	public double[] getExperimentalTimePoints(){
		return this.expTimePoints;
	}

	public String getModelName() {
		return optModel.getName();
	}

	public RowColumnResultSet computeSolution(double[] parameterValues, NormalizedSampleFunction[] rois, double[] times) {
		double[][] simData = optModel.getSolution(parameterValues, times);
		ArrayList<String> colNames = new ArrayList<String>();
		colNames.add("t");
		for (NormalizedSampleFunction roi : rois){
			colNames.add(roi.getName());
		}
		RowColumnResultSet results = new RowColumnResultSet(colNames.toArray(new String[0]));
		for (int i=0;i<times.length;i++){
			double[] row = new double[rois.length+1];
			row[0] = times[i];
			for (int j=0;j<rois.length;j++){
				row[j+1] = simData[j][i];
			}
			results.addRow(row);
		}
		return results;
	}

	public RowColumnResultSet getExperimentalData(NormalizedSampleFunction[] rois) {
		ArrayList<String> colNames = new ArrayList<String>();
		colNames.add("t");
		for (NormalizedSampleFunction roi : rois){
			colNames.add(roi.getName());
		}
		RowColumnResultSet results = new RowColumnResultSet(colNames.toArray(new String[0]));
		for (int i=0;i<expTimePoints.length;i++){
			double[] row = new double[rois.length+1];
			row[0] = expTimePoints[i];
			for (int j=0;j<rois.length;j++){
				row[j+1] = expData[j][i];
			}
			results.addRow(row);
		}
		return results;
	}
	
}

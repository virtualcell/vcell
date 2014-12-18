package org.vcell.vmicro.workflow.data;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.opt.Parameter;


public class OptContext {
	private OptModel optModel = null;
	private double[][] expData = null;
	private double[][] measurementErrors = null;
	private double[] expTimePoints = null;

	public OptContext(OptModel optModel, double[][] expData, double[] expTimePoints, double[][] measurementErrors) {
		this.optModel = optModel;
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
		
		error = error + optModel.getPenalty(parameters);
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

	public RowColumnResultSet computeSolution(double[] parameterValues, ROI[] rois, double[] times) {
		double[][] simData = optModel.getSolution(parameterValues, times);
		ArrayList<String> colNames = new ArrayList<String>();
		colNames.add("t");
		for (ROI roi : rois){
			colNames.add(roi.getROIName());
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

	public RowColumnResultSet getExperimentalData(ROI[] rois) {
		ArrayList<String> colNames = new ArrayList<String>();
		colNames.add("t");
		for (ROI roi : rois){
			colNames.add(roi.getROIName());
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

package org.vcell.vmicro.workflow.data;

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

	public String getModelName() {
		return optModel.getName();
	}

}

package org.vcell.vmicro.workflow.data;

import cbit.vcell.opt.Parameter;

public abstract class OptModel {
	private Parameter fixedParameter = null;
	private Double fixedParameterValue = null; 
	private final Parameter[] parameters;
	private final String name;
	
	public OptModel(String name, Parameter[] parameters){
		this.name = name;
		this.parameters = parameters;
	}
	
	public final String getName(){
		return this.name;
	}
	
	public final Parameter[] getParameters() {
		return parameters;
	}

	public final void setFixedParameter(Parameter fixedParameter, double fixedParameterValue){
		this.fixedParameter = fixedParameter;
		this.fixedParameterValue = fixedParameterValue;
	}
	
	public final void clearFixedParameter(){
		this.fixedParameter = null;
		this.fixedParameterValue = null;
	}
	
	public final double getFixedParameterValue(){
		return this.fixedParameterValue;
	}
	
	public final boolean isFixedParameter(String paramName){
		return (fixedParameter!=null && fixedParameter.getName().equals(paramName));
	}
	
	public final boolean hasFixedParameter(){
		return fixedParameter!=null;
	}
	
	public final double[][] getSolution(double[] newParams, double[] solutionTimePoints){
		if (newParams.length == parameters.length-1){
			double[] paramValues = new double[parameters.length];
			int dataCount = 0;
			for (int i=0;i<parameters.length;i++){
				if (isFixedParameter(parameters[i].getName())){
					paramValues[i] = getFixedParameterValue();
				}else{
					paramValues[i] = newParams[dataCount++];
				}
			}
			return getSolution0(paramValues,solutionTimePoints);
		}else{
			return getSolution0(newParams,solutionTimePoints);
		}
	}
	
	protected abstract double[][] getSolution0(double[] newParams, double[] solutionTimePoints);
	
	public abstract double getPenalty(double[] parameters2);

}

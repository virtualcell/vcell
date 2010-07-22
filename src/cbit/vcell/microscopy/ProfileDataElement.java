package cbit.vcell.microscopy;

import cbit.vcell.opt.Parameter;

public class ProfileDataElement 
{
	private String paramName = null;
	private double paramVal = 0;
	private double likelihood = 0;
	private Parameter[] bestParams = null;
	
	public ProfileDataElement(double arg_paramValue, double arg_likelihood, Parameter[] arg_bestParams)
	{
		this(null, arg_paramValue, arg_likelihood, arg_bestParams );
	}
	
	public ProfileDataElement(String arg_paramName, double arg_paramValue, double arg_likelihood, Parameter[] arg_bestParams)
	{
		paramName = arg_paramName;
		paramVal = arg_paramValue;
		likelihood = arg_likelihood;
		bestParams = arg_bestParams;
	}
	
	public String getParamName() {
		return paramName;
	}
	
	public double getParameterValue()
	{
		return paramVal;
	}
	public double getLikelihood()
	{
		return likelihood;
	}
	public Parameter[] getBestParameters()
	{
		return bestParams;
	}
}



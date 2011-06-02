package cbit.vcell.microscopy;

import cbit.vcell.opt.Parameter;

public class AnalysisParameters 
{
	private Parameter[] parameters = null;
	
	public Parameter[] getParameters() {
		return parameters;
	}

	public void setParameters(Parameter[] parameters) {
		this.parameters = parameters;
	}

	public AnalysisParameters(Parameter[] arg_parameters)
	{
		parameters = arg_parameters;
	}

	/*public boolean compareEqual(Matchable obj)
	{
		//should make Parameter implements matchable
//		Compare.isEqualOrNull(getParameters(), anaParameter.getParameters());
		return false;
	}*/

}

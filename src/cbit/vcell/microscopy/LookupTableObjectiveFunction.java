package cbit.vcell.microscopy;

import cbit.function.DefaultScalarFunction;

public class LookupTableObjectiveFunction extends DefaultScalarFunction {

	private FRAPOptData refData = null;
	public LookupTableObjectiveFunction(FRAPOptData argRefData)
	{
		super();
		refData = argRefData;
	}
	@Override
	public double f(double[] x) {
		// 
		double diffRate = x[0];
		try{
		    double error = refData.computeError(diffRate);
		    return error;
		}catch(Exception e)
		{
			e.printStackTrace(System.out);
			return FRAPOptimization.bigValue;
		}
		// TODO Auto-generated method stub
	}

	@Override
	public int getNumArgs() {
		// TODO Auto-generated method stub
		return 1;
	}
}


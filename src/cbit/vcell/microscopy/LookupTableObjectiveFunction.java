package cbit.vcell.microscopy;

import cbit.function.DefaultScalarFunction;

public class LookupTableObjectiveFunction extends DefaultScalarFunction {

	private FRAPOptData optData = null;
	public LookupTableObjectiveFunction(FRAPOptData argOptData)
	{
		super();
		optData = argOptData;
	}
	@Override
	public double f(double[] x) {
		// 
		try{
		    double error = optData.computeError(x);
		    return error;
		}catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage(),e);
		}
		// TODO Auto-generated method stub
	}

	@Override
	public int getNumArgs() {
		// TODO Auto-generated method stub
		return 1;
	}
}


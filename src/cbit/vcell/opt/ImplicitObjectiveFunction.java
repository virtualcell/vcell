package cbit.vcell.opt;

import java.util.Vector;

import org.vcell.util.Issue;

import cbit.function.DefaultScalarFunction;

public class ImplicitObjectiveFunction extends ObjectiveFunction
{
	DefaultScalarFunction scalarFunc = null;
	
	public ImplicitObjectiveFunction(DefaultScalarFunction argScalarFunc)
	{
		super();
		scalarFunc = argScalarFunc;
	}
	@Override
	public void gatherIssues(Vector<Issue> issueList) {
	}

	@Override
	public String getVCML() {
		throw new RuntimeException("No XML string generated from implicit objective function.");
	}

	public DefaultScalarFunction getObjectiveFunction()
	{
		return scalarFunc;
	}
}

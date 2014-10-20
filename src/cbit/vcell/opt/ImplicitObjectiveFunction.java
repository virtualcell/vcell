/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.opt;

import java.util.List;

import org.vcell.util.Issue;
import org.vcell.util.IssueContext;

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
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
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

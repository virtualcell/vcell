/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization;

import cbit.vcell.parser.Expression;

public class ExplicitObjectiveFunction implements ObjectiveFunction {
	private Expression expression = null;
	
	public ExplicitObjectiveFunction(Expression arg_expression){
		expression = arg_expression;
	}

	public Expression getExpression() {
		return expression;
	}
}

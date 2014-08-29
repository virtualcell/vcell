/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;

import java.io.Serializable;

import cbit.vcell.solver.AnnotatedFunction;

public class OutputContext implements Serializable {
private AnnotatedFunction[] outputFunctions = null;

public OutputContext(AnnotatedFunction[] outputFunctions) {
	this.outputFunctions = outputFunctions;
}

public AnnotatedFunction[] getOutputFunctions() {
	return outputFunctions;
}

public AnnotatedFunction getOutputFunction(String functionName) {
	AnnotatedFunction function = null;
	if (outputFunctions != null) {
		for (int i = 0; i < outputFunctions.length; i++) {
			if (outputFunctions[i].getName().equals(functionName)) {
				function = outputFunctions[i];
			}
		}
	}
	return function;
}

}

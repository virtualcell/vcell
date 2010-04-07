package cbit.vcell.client.data;

import cbit.vcell.math.AnnotatedFunction;

public class OutputContext {
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

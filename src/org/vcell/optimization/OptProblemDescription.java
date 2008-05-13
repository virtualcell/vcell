package org.vcell.optimization;

public class OptProblemDescription {
	
	private ObjectiveFunction objectiveFunction = null;
	private ParameterDescription parameterDescription = null;
	private ConstraintDescription constraintDescription = null;
	
	public OptProblemDescription(ObjectiveFunction arg_objectiveFunction,
			ParameterDescription arg_parameterDescription,
			ConstraintDescription arg_constraintDescription) {
		this.objectiveFunction = arg_objectiveFunction;
		this.parameterDescription = arg_parameterDescription;
		this.constraintDescription = arg_constraintDescription;
	}

	public ObjectiveFunction getObjectiveFunction() {
		return objectiveFunction;
	}

	public ParameterDescription getParameterDescription() {
		return parameterDescription;
	}

	public ConstraintDescription getConstraintDescription() {
		return constraintDescription;
	}
}

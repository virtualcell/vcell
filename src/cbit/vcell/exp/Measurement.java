package cbit.vcell.exp;
import cbit.vcell.constraints.AbstractConstraint;
/**
 * Insert the type's description here.
 * Creation date: (1/6/2005 1:19:35 PM)
 * @author: Jim Schaff
 */
public abstract class Measurement {
	private Experiment.ExperimentParameter measuredQuantity = null;
	private String description = null;
	private AbstractConstraint[] preconditions = null;
	private Experiment.ExperimentParameter[] independentVariables = null;
	
/**
 * Measurement constructor comment.
 */
protected Measurement(Experiment.ExperimentParameter argMeasuredQuantity, String argDescription, cbit.vcell.constraints.AbstractConstraint[] argPreconditions, Experiment.ExperimentParameter[] argIndependentVars) {
	super();
	this.measuredQuantity = argMeasuredQuantity;
	this.description = argDescription;
	this.preconditions = argPreconditions;
	this.independentVariables = argIndependentVars;
}
}
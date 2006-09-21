package cbit.vcell.exp;
/**
 * Insert the type's description here.
 * Creation date: (1/6/2005 1:43:30 PM)
 * @author: Jim Schaff
 */
public class FieldMeasurement extends Measurement {
/**
 * TimeSeriesMeasurement constructor comment.
 * @param argMeasuredQuantity cbit.vcell.exp.Experiment.ExperimentParameter
 * @param argDescription java.lang.String
 * @param argPreconditions cbit.vcell.constraints.AbstractConstraint[]
 * @param argIndependentVars cbit.vcell.exp.Experiment.ExperimentParameter[]
 */
protected FieldMeasurement(cbit.vcell.exp.Experiment.ExperimentParameter argMeasuredQuantity, String argDescription, cbit.vcell.constraints.AbstractConstraint[] argPreconditions, cbit.vcell.exp.Experiment.ExperimentParameter[] argIndependentVars) {
	super(argMeasuredQuantity, argDescription, argPreconditions, argIndependentVars);
}
}
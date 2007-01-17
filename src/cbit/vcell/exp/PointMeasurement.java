package cbit.vcell.exp;
/**
 * Insert the type's description here.
 * Creation date: (1/6/2005 1:41:30 PM)
 * @author: Jim Schaff
 */
public class PointMeasurement extends Measurement {
/**
 * PointMeasurement constructor comment.
 * @param argMeasuredQuantity cbit.vcell.exp.Experiment.ExperimentParameter
 * @param argDescription java.lang.String
 * @param argPreconditions cbit.vcell.constraints.AbstractConstraint[]
 * @param argIndependentVars cbit.vcell.exp.Experiment.ExperimentParameter[]
 */
protected PointMeasurement(cbit.vcell.exp.Experiment.ExperimentParameter argMeasuredQuantity, String argDescription, cbit.vcell.constraints.AbstractConstraint[] argPreconditions) {
	super(argMeasuredQuantity, argDescription, argPreconditions, null);
}
}
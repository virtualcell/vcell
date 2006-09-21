package cbit.vcell.solver.ode;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.math.*;
import cbit.vcell.parser.*;
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:59:44 PM)
 * @author: John Wagner
 */
public class ODEStateVariable extends StateVariable {
	VolVariable variable = null;
//	OdeEquation ode = null;
	Expression optimizedRateExp = null;
	Expression initialExp = null;
//	double data[] = null;
/**
 * TimeSeriesData constructor comment.
 */
public ODEStateVariable(OdeEquation ode, cbit.vcell.simulation.Simulation simulation) throws ExpressionException, MathException {
	super(ode.getVariable());
	this.variable = (VolVariable)ode.getVariable();
	ode.bind(simulation);
	optimizedRateExp = ode.getFlattenedRateExpression(simulation);
	initialExp = ode.getInitialExpression();
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param values double[]
 */
public double evaluateIC(double values[]) throws ExpressionException {
	return initialExp.evaluateVector(values);
}
/**
 * This method was created in VisualAge.
 * @return double
 * @param values double[]
 */
public double evaluateRate(double values[]) throws ExpressionException {
	return optimizedRateExp.evaluateVector(values);
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.Variable
 */
public Expression getInitialRateExpression() throws ExpressionException {
	return initialExp;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.Variable
 */
public Expression getRateExpression() throws ExpressionException {
	return optimizedRateExp;
}
}

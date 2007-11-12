package cbit.vcell.solver.ode;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.parser.*;
import java.util.*;
import java.io.*;
import cbit.vcell.math.*;
import cbit.vcell.solver.*;
/**
 * Insert the type's description here.
 * Creation date: (3/8/00 10:29:24 PM)
 * @author: John Wagner
 */
public class CVodeFileWriter extends OdeFileWriter {

/**
 * OdeFileCoder constructor comment.
 */
public CVodeFileWriter(Simulation simulation) {
	super(simulation);
}


/**
 * Insert the method's description here.
 * Creation date: (3/8/00 10:31:52 PM)
 */
protected void writeEquations(java.io.PrintWriter pw) throws MathException, ExpressionException {
	VariableSymbolTable varsSymbolTable = createSymbolTable();
		
	for (int i = 0; i < getStateVariableCount(); i++) {
		StateVariable stateVar = (StateVariable)getStateVariable(i);
		Expression rateExpr = new Expression(0.0);
		if (stateVar instanceof ODEStateVariable) {
			rateExpr = ((ODEStateVariable)stateVar).getRateExpression();
		} else if (stateVar instanceof SensStateVariable) {
			rateExpr = ((SensStateVariable)stateVar).getRateExpression();
		}
		Expression initExpr = new Expression(0.0);
		if (stateVar instanceof ODEStateVariable) {
			initExpr = ((ODEStateVariable)stateVar).getInitialRateExpression();
			initExpr.bindExpression(getSimulation());
			initExpr = getSimulation().substituteFunctions(initExpr);
		} else if (stateVar instanceof SensStateVariable) {
			initExpr = ((SensStateVariable)stateVar).getInitialRateExpression();
		}		
		
		rateExpr.bindExpression(varsSymbolTable);
		rateExpr = MathUtilities.substituteFunctions(rateExpr, varsSymbolTable);
		pw.println("ODE "+stateVar.getVariable().getName()+" INIT "+ initExpr.flatten().infix() + ";\n\t RATE " + rateExpr.flatten().infix() + ";");
	}
}
}
package cbit.vcell.solver.ode;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.parser.*;
import java.util.*;
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
	
	HashMap<Discontinuity, String> discontinuityNameMap = new HashMap<Discontinuity, String>();	
	
	StringBuffer sb = new StringBuffer();
	sb.append("NUM_EQUATIONS " + getStateVariableCount() + "\n");
	for (int i = 0; i < getStateVariableCount(); i++) {
		StateVariable stateVar = (StateVariable)getStateVariable(i);
		Expression rateExpr = new Expression(0.0);
		if (stateVar instanceof ODEStateVariable) {
			rateExpr = ((ODEStateVariable)stateVar).getRateExpression();
		} else if (stateVar instanceof SensStateVariable) {
			rateExpr = ((SensStateVariable)stateVar).getRateExpression();
		}
		Expression initExpr = null;
		if (stateVar instanceof ODEStateVariable) {
			initExpr = ((ODEStateVariable)stateVar).getInitialRateExpression();
			initExpr.bindExpression(getSimulation());
			initExpr = getSimulation().substituteFunctions(initExpr);
		} else if (stateVar instanceof SensStateVariable) {
			initExpr = ((SensStateVariable)stateVar).getInitialRateExpression();
		}
		
		initExpr.substituteInPlace(new Expression("t"), new Expression(0.0));
		
		rateExpr.bindExpression(varsSymbolTable);
		rateExpr = MathUtilities.substituteFunctions(rateExpr, varsSymbolTable).flatten();
		Expression newRateExpr = new Expression(rateExpr);		
		Vector<Discontinuity> v = rateExpr.getDiscontinuities();		
		for (Discontinuity od : v) {
			String dname = discontinuityNameMap.get(od);
			if (dname == null) {
				dname = "D_B" + discontinuityNameMap.size();
				discontinuityNameMap.put(od, dname);				
			}
			newRateExpr.substituteInPlace(od.getDiscontinuityExp(), new Expression(dname));
		}

		sb.append("ODE "+stateVar.getVariable().getName()+" INIT "+ initExpr.flatten().infix() + ";\n\t RATE " + newRateExpr.flatten().infix() + ";\n");
	}
	
	if (discontinuityNameMap.size() > 0) {
		pw.println("DISCONTINUITIES " + discontinuityNameMap.size());
		for (Discontinuity od : discontinuityNameMap.keySet()) {
			pw.println(discontinuityNameMap.get(od) + " " + od.getDiscontinuityExp().flatten().infix() + "; " + od.getRootFindingExp().flatten().infix() + ";");
		}
	}
	pw.print(sb);
}
}
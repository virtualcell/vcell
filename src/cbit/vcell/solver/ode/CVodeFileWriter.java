package cbit.vcell.solver.ode;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

import cbit.vcell.math.MathException;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.parser.Discontinuity;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.SimulationJob;
/**
 * Insert the type's description here.
 * Creation date: (3/8/00 10:29:24 PM)
 * @author: John Wagner
 */
public class CVodeFileWriter extends OdeFileWriter {

public CVodeFileWriter(PrintWriter pw, SimulationJob simJob) {
	super(pw, simJob, false);
}
	
public CVodeFileWriter(PrintWriter pw, SimulationJob simJob, boolean bUseMessaging) {
	super(pw, simJob, bUseMessaging);
}

/**
 * Insert the method's description here.
 * Creation date: (3/8/00 10:31:52 PM)
 */
protected String writeEquations(HashMap<Discontinuity, String> discontinuityNameMap) throws MathException, ExpressionException {	
	
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < getStateVariableCount(); i++) {
		StateVariable stateVar = getStateVariable(i);
		Expression rateExpr = new Expression(stateVar.getRateExpression());
		Expression initExpr = new Expression(stateVar.getInitialRateExpression());
		
		initExpr = MathUtilities.substituteFunctions(initExpr, varsSymbolTable).flatten();
		initExpr.substituteInPlace(new Expression("t"), new Expression(0.0));
		rateExpr = MathUtilities.substituteFunctions(rateExpr, varsSymbolTable).flatten();
		
		Vector<Discontinuity> v = rateExpr.getDiscontinuities();		
		for (Discontinuity od : v) {
			od = getSubsitutedAndFlattened(od,varsSymbolTable);
			String dname = discontinuityNameMap.get(od);
			if (dname == null) {
				dname = ROOT_VARIABLE_PREFIX + discontinuityNameMap.size();
				discontinuityNameMap.put(od, dname);				
			}
			rateExpr.substituteInPlace(od.getDiscontinuityExp(), new Expression("(" + dname + "==1)"));
		}

		sb.append("ODE "+stateVar.getVariable().getName()+" INIT "+ initExpr.flatten().infix() + ";\n\t RATE " + rateExpr.flatten().infix() + ";\n");
	}

	return sb.toString();
}


@Override
String getSolverName() {
	return "CVODE";
}
}
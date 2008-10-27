package cbit.vcell.solver.ode;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.parser.*;

import java.io.PrintWriter;
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
public CVodeFileWriter(PrintWriter pw, Simulation simulation) {
	this(pw, simulation, 0, false);
}

public CVodeFileWriter(PrintWriter pw, Simulation simulation, int ji, boolean bUseMessaging) {
	super(pw, simulation, ji, bUseMessaging);
}


/**
 * Insert the method's description here.
 * Creation date: (3/8/00 10:31:52 PM)
 */
protected void writeEquations() throws MathException, ExpressionException {		
	VariableSymbolTable varsSymbolTable = createSymbolTable();
	
	HashMap<Discontinuity, String> discontinuityNameMap = new HashMap<Discontinuity, String>();	
	
	StringBuffer sb = new StringBuffer();
	sb.append("NUM_EQUATIONS " + getStateVariableCount() + "\n");
	for (int i = 0; i < getStateVariableCount(); i++) {
		StateVariable stateVar = (StateVariable)getStateVariable(i);
		Expression rateExpr = null;
		if (stateVar instanceof ODEStateVariable) {
			rateExpr = new Expression(((ODEStateVariable)stateVar).getRateExpression());
		} else if (stateVar instanceof SensStateVariable) {
			rateExpr = new Expression(((SensStateVariable)stateVar).getRateExpression());
		}
		Expression initExpr = null;
		if (stateVar instanceof ODEStateVariable) {
			initExpr = new Expression(((ODEStateVariable)stateVar).getInitialRateExpression());
		} else if (stateVar instanceof SensStateVariable) {
			initExpr = new Expression(((SensStateVariable)stateVar).getInitialRateExpression());
		}
		
		initExpr.substituteInPlace(new Expression("t"), new Expression(0.0));
		initExpr = MathUtilities.substituteFunctions(initExpr, varsSymbolTable).flatten();
		rateExpr = MathUtilities.substituteFunctions(rateExpr, varsSymbolTable).flatten();
		
		Vector<Discontinuity> v = rateExpr.getDiscontinuities();		
		for (Discontinuity od : v) {
			od.subsituteAndFlatten(varsSymbolTable);
			String dname = discontinuityNameMap.get(od);
			if (dname == null) {
				dname = ROOT_VARIABLE_PREFIX + discontinuityNameMap.size();
				discontinuityNameMap.put(od, dname);				
			}
			rateExpr.substituteInPlace(od.getDiscontinuityExp(), new Expression(dname));
		}

		sb.append("ODE "+stateVar.getVariable().getName()+" INIT "+ initExpr.flatten().infix() + ";\n\t RATE " + rateExpr.flatten().infix() + ";\n");
	}
	
	if (discontinuityNameMap.size() > 0) {
		printWriter.println("DISCONTINUITIES " + discontinuityNameMap.size());
		for (Discontinuity od : discontinuityNameMap.keySet()) {
			printWriter.println(discontinuityNameMap.get(od) + " " + od.getDiscontinuityExp().flatten().infix() + "; " + od.getRootFindingExp().flatten().infix() + ";");
		}
	}
	printWriter.print(sb);
}


@Override
String getSolverName() {
	return "CVODE";
}
}
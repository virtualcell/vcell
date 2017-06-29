/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

import cbit.vcell.math.MathException;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.Discontinuity;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
/**
 * Insert the type's description here.
 * Creation date: (3/8/00 10:29:24 PM)
 * @author: John Wagner
 */
public class CVodeFileWriter extends OdeFileWriter {

public CVodeFileWriter(PrintWriter pw, SimulationTask simTask) {
	super(pw, simTask, false);
}
	
public CVodeFileWriter(PrintWriter pw, SimulationTask simTask, boolean bUseMessaging) {
	super(pw, simTask, bUseMessaging);
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
		String[] symbols0 = initExpr.getSymbols();
		if(symbols0 != null){
			for(String symbol:symbols0){
				SymbolTableEntry ste = initExpr.getSymbolBinding(symbol);
				if( !ste.equals(ReservedVariable.X) && !ste.equals(ReservedVariable.Y) && !ste.equals(ReservedVariable.Z)){
					throw new MathException("Variables are not allowed in initial condition.\nInitial condition of variable:" + stateVar.getVariable().getName() + " has variable(" +symbol+ ") in expression."); 
				}
			}
		}
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

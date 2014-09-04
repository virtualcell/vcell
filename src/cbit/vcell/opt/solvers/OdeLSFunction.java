/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.opt.solvers;
import java.io.PrintWriter;

import org.vcell.optimization.OptSolverCallbacks;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.User;
import org.vcell.util.document.VersionFlag;

import cbit.function.DefaultScalarFunction;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.opt.OdeObjectiveFunction;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.ode.IDAFileWriter;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.solvers.NativeIDASolver;
/**
 * Insert the type's description here.
 * Creation date: (9/5/2005 1:32:55 PM)
 * @author: Jim Schaff
 */
public class OdeLSFunction extends DefaultScalarFunction {
	private OdeObjectiveFunction odeObjectiveFunction = null;
	private String unscaledParameterNames[] = null;
	private double parameterScalings[] = null;
	private ODESolverResultSet odeSolverResultSet = null;
	private OptSolverCallbacks optSolverCallbacks = null;

/**
 * OdeLSFunction constructor comment.
 */
public OdeLSFunction(OdeObjectiveFunction argOdeObjectiveFunction, String[] argUnscaledParameterNames, double[] argParameterScalings, OptSolverCallbacks argOptSolverCallbacks) {
	super();
	this.odeObjectiveFunction = argOdeObjectiveFunction;

	if (argUnscaledParameterNames == null || argParameterScalings == null) {
		throw new RuntimeException("Parameters cannot be null");
	}
	this.unscaledParameterNames = argUnscaledParameterNames;
	this.parameterScalings = argParameterScalings;
	this.optSolverCallbacks = argOptSolverCallbacks;
}


/**
 * f method comment.
 */
private double calculateWeightedError(double[] x) {
	//
	// Clear previous results
	//
	odeSolverResultSet = null;
	String idaInputString = null;
	
	try {
		long t1 = System.currentTimeMillis();
		if (optSolverCallbacks.getStopRequested()){
			throw new RuntimeException("user aborted");
		}
		//
		// Create new simulation and apply current parameter values in x[] as math overrides
		//
		SimulationVersion simVersion = new SimulationVersion(
			new KeyValue("12345"), "name", new User("user",new KeyValue("123")),
			new GroupAccessNone(), null, // versionBranchPointRef
			new java.math.BigDecimal(1.0), // branchID
			new java.util.Date(), VersionFlag.Archived, "",  null);
		Simulation simulation = new Simulation(simVersion,odeObjectiveFunction.getMathDescription());
		SimulationSymbolTable simSymbolTable = new SimulationSymbolTable(simulation, 0);
		
		MathOverrides mathOverrides = simulation.getMathOverrides();
		for (int i = 0; i < unscaledParameterNames.length; i++){
			double unscaledParameterValue = x[i] * parameterScalings[i];
			mathOverrides.putConstant(new Constant(unscaledParameterNames[i],new Expression(unscaledParameterValue)));
			System.out.print(unscaledParameterNames[i]+"="+x[i]+" ");
		}
		System.out.println();
		ReferenceData refData = odeObjectiveFunction.getReferenceData();
		double refDataEndTime = refData.getDataByColumn(0)[refData.getNumDataRows()-1];
		simulation.getSolverTaskDescription().setTimeBounds(new TimeBounds(0.0, refDataEndTime));
		simulation.getSolverTaskDescription().setSolverDescription(SolverDescription.IDA);

		java.io.StringWriter stringWriter = new java.io.StringWriter();
		IDAFileWriter idaFileWriter = new IDAFileWriter(new PrintWriter(stringWriter,true), new SimulationTask(new SimulationJob(simulation, 0, null),0));
		idaFileWriter.write(unscaledParameterNames);
		stringWriter.close();
		StringBuffer buffer = stringWriter.getBuffer();
		idaInputString = buffer.toString();
		
			
		long t2 = System.currentTimeMillis();

		final NativeIDASolver nativeIDASolver = new NativeIDASolver();
		optSolverCallbacks.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
			public void propertyChange(java.beans.PropertyChangeEvent evt){
				if (((OptSolverCallbacks)evt.getSource()).getStopRequested()){
					nativeIDASolver.setStopRequested(true);
				}
			}
		});
		nativeIDASolver.setStopRequested(optSolverCallbacks.getStopRequested());
		RowColumnResultSet rcResultSet = nativeIDASolver.solve(idaInputString, x);

		long t3 = System.currentTimeMillis();

		//
		// copy into OdeSolverResultSet
		//
		ODESolverResultSet tempOdeSolverResultSet = new ODESolverResultSet();
		for (int i = 0; i < rcResultSet.getDataColumnCount(); i++){
			tempOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(rcResultSet.getColumnDescriptions(i).getName()));
		}
		for (int i = 0; i < rcResultSet.getRowCount(); i++){
			tempOdeSolverResultSet.addRow(rcResultSet.getRow(i));
		}
		//
		// add appropriate Function columns to result set
		//
		Function functions[] = simSymbolTable.getFunctions();
		for (int i = 0; i < functions.length; i++){
			if (SimulationSymbolTable.isFunctionSaved(functions[i])){
				Expression exp1 = new Expression(functions[i].getExpression());
				try {
					exp1 = simSymbolTable.substituteFunctions(exp1);
				} catch (MathException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
				} catch (ExpressionException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
				}
				
				try {
					for (int j = 0; j < unscaledParameterNames.length; j ++) {
						exp1.substituteInPlace(new cbit.vcell.parser.Expression(unscaledParameterNames[j]), new cbit.vcell.parser.Expression(x[j]));
					}
					
					FunctionColumnDescription cd = new FunctionColumnDescription(exp1.flatten(),functions[i].getName(), null, functions[i].getName(), false);
					tempOdeSolverResultSet.addFunctionColumn(cd);
				}catch (cbit.vcell.parser.ExpressionException e){
					e.printStackTrace(System.out);
				}
			}
		}

		odeSolverResultSet = tempOdeSolverResultSet;
		
		long t4 = System.currentTimeMillis();
		System.out.println("init="+(t2-t1)+"ms, solve="+(t3-t2)+"ms, post-process="+(t4-t3)+"ms");
		return MathTestingUtilities.calcWeightedSquaredError(odeSolverResultSet,refData);
	} catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	} catch (SolverException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	} catch (ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	} catch (Exception e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * f method comment.
 */
public double f(double[] x) {
	return calculateWeightedError(x);
}


/**
 * Insert the method's description here.
 * Creation date: (9/5/2005 1:32:55 PM)
 * @return int
 */
public int getNumArgs() {
	return unscaledParameterNames.length;
}


/**
 * Insert the method's description here.
 * Creation date: (9/6/2005 9:18:19 AM)
 * @return cbit.vcell.solver.ode.ODESolverResultSet
 */
public ODESolverResultSet getOdeSolverResultSet() {
	return odeSolverResultSet;
}
}
